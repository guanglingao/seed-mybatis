package org.seed.mybatis.core.ext;


import lombok.extern.slf4j.Slf4j;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.SeedMybatisConstants;
import org.seed.mybatis.core.ext.code.client.ClassClient;
import org.seed.mybatis.core.ext.exception.GenerateCodeException;
import org.seed.mybatis.core.ext.exception.MapperFileBuildException;
import org.seed.mybatis.core.ext.spi.ClassSearch;
import org.seed.mybatis.core.ext.spi.SpiContext;
import org.seed.mybatis.core.util.IOUtil;
import org.seed.mybatis.core.util.MybatisFileUtil;
import org.seed.mybatis.core.util.StringUtil;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * mapper构建
 */
@Slf4j
public class MapperLocationsBuilder {


    private final Map<String, MyBatisResource> mybatisMapperStore = new HashMap<>();

    private SeedMybatisConfig config = new SeedMybatisConfig();

    private List<String> mapperNames = Collections.emptyList();

    private Set<Class<?>> mapperClasses = new HashSet<>(64);

    private String dialect;

    public MyBatisResource[] build(String basePackage, List<MyBatisResource> myBatisResources, String dialect) {
        for (MyBatisResource myBatisResource : myBatisResources) {
            // XxDao.xml
            String filename = myBatisResource.getFilename();
            mybatisMapperStore.put(filename, myBatisResource);
        }
        this.dialect = dialect;
        try {
            String[] basePackages = StringUtil.tokenizeToStringArray(basePackage,
                    StringUtil.CONFIG_LOCATION_DELIMITERS);
            ClassSearch classSearch = SpiContext.getClassSearch();
            Set<Class<?>> clazzSet = classSearch.search(Object.class, basePackages);
            return this.buildMapperLocations(clazzSet);
        } catch (Exception e) {
            log.error("构建mapper失败", e);
            throw new MapperFileBuildException(e);
        } finally {
            destroy();
        }
    }

    private void destroy() {
        mybatisMapperStore.clear();
    }

    private MyBatisResource getMapperFile(String mapperFileName) {
        return mybatisMapperStore.get(mapperFileName);
    }

    private MyBatisResource[] buildMapperLocations(Set<Class<?>> clazzSet) {
        this.initContext(clazzSet);

        List<MyBatisResource> mapperLocations = this.buildMapperResource(clazzSet);

        this.addUnmergedResource(mapperLocations);

        this.addCommonSqlClasspathMapper(mapperLocations);

        return mapperLocations.toArray(new MyBatisResource[mapperLocations.size()]);
    }

    private List<MyBatisResource> buildMapperResource(Set<Class<?>> clazzSet) {
        int classCount = clazzSet.size();
        if (classCount == 0) {
            return new ArrayList<>();
        }
        final MyBatisResource templateResource = this.buildTemplateResource(this.getDbName());
        log.debug("使用模板:" + templateResource);
        final String globalVmLocation = this.config.getGlobalVmLocation();
        final ClassClient codeClient = new ClassClient(config);
        final List<MyBatisResource> mapperLocations = new ArrayList<>(classCount);

        long startTime = System.currentTimeMillis();
        try {
            String templateContent = templateResource.getContent();
            for (Class<?> daoClass : clazzSet) {
                String xml = codeClient.generateMybatisXml(daoClass, templateContent, globalVmLocation);
                xml = mergeExtMapperFile(daoClass, xml);
                saveMapper(daoClass.getSimpleName() + SeedMybatisConstants.XML_SUFFIX, xml);
                mapperLocations.add(MyBatisResource.build(xml, daoClass));
            }

            long endTime = System.currentTimeMillis();
            log.debug("生成Mapper内容总耗时：" + (endTime - startTime) / 1000.0 + "秒");
            return mapperLocations;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GenerateCodeException(e);
        }

    }

    private List<String> buildMapperNames(Set<Class<?>> clazzSet) {
        List<String> list = new ArrayList<>(clazzSet.size());
        for (Class<?> mapperClass : clazzSet) {
            list.add(mapperClass.getSimpleName());
        }
        return list;
    }

    private void initContext(Set<Class<?>> clazzSet) {
        mapperClasses.addAll(clazzSet);
        mapperNames = this.buildMapperNames(clazzSet);
        for (Class<?> mapperClass : clazzSet) {
            ExtContext.addMapperClass(mapperClass);
        }
    }

    public Set<Class<?>> getMapperClasses() {
        return mapperClasses;
    }

    /**
     * 保存mapper到本地文件夹
     *
     * @throws IOException
     */
    private void saveMapper(String filename, final String content) throws IOException {
        String saveDir = config.getMapperSaveDir();
        if (StringUtil.hasText(saveDir)) {
            String path = saveDir + "/" + filename;
            log.debug("保存mapper文件到" + path);
            try (OutputStream out = new FileOutputStream(path)) {
                IOUtil.copy(IOUtil.toInputStream(content, StandardCharsets.UTF_8), out);
            } catch (IOException e) {
                throw e;
            }
        }
    }

    private MyBatisResource buildTemplateResource(String dialect) {
        // mysql.vm
        String templateFileName = this.buildTemplateFileName(dialect);
        // 优先使用classpath根目录下的vm模板
        MyBatisResource myBatisResource = MyBatisResource.buildFromClasspath(templateFileName);
        if (myBatisResource.exists()) {
            return myBatisResource;
        }
        String templateClasspath = config.getTemplateClasspath();
        if (StringUtil.isEmpty(templateClasspath)) {
            templateClasspath = SeedMybatisConstants.DEFAULT_CLASS_PATH;
        }
        // 返回格式：classpath路径 + 数据库名称 + 文件后缀
        // 如：/seed-mybatis/tpl/mysql.vm
        String location = templateClasspath + templateFileName;
        return MyBatisResource.buildFromClasspath(location);
    }

    /**
     * 构建文件名
     */
    private String buildTemplateFileName(String dialect) {
        dialect = dialect.replaceAll("\\s", "").toLowerCase();
        return dialect + SeedMybatisConstants.TEMPLATE_SUFFIX;
    }

    private void addCommonSqlClasspathMapper(List<MyBatisResource> mapperLocations) {
        String commonSqlClasspath = config.getCommonSqlClasspath();
        MyBatisResource myBatisResource = MyBatisResource.buildFromClasspath(commonSqlClasspath);
        mapperLocations.add(myBatisResource);
    }

    /**
     * 合并其它mapper
     */
    private void addUnmergedResource(List<MyBatisResource> mapperLocations) {
        Collection<MyBatisResource> mapperResourceDefinitions = this.mybatisMapperStore.values();
        for (MyBatisResource mapperResourceDefinition : mapperResourceDefinitions) {
            if (mapperResourceDefinition.isMerged()) {
                continue;
            }
            log.debug("加载未合并Mapper：" + mapperResourceDefinition.getFilename());
            mapperLocations.add(mapperResourceDefinition);
        }
    }

    /**
     * 合并扩展mapper文件内容
     *
     * @throws DocumentException
     * @throws IOException
     */
    private String mergeExtMapperFile(Class<?> mapperClass, String xml) throws IOException, DocumentException {
        // 自定义文件
        String mapperFileName = mapperClass.getSimpleName() + SeedMybatisConstants.XML_SUFFIX;
        // 先找跟自己同名的xml，如:UserMapper.java -> UserMapper.xml
        MyBatisResource myBatisResource = this.getMapperFile(mapperFileName);
        StringBuilder extXml = new StringBuilder();

        if (myBatisResource != null) {
            // 追加内容
            String extFileContent = MybatisFileUtil.getExtFileContent(myBatisResource.getInputStream());
            extXml.append(extFileContent);

            myBatisResource.setMerged(true);
        }
        // 再找namespace一样的xml
        String otherMapperXml = this.buildOtherMapperContent(mapperClass, this.mybatisMapperStore.values());
        extXml.append(otherMapperXml);

        xml = xml.replace(SeedMybatisConstants.EXT_MAPPER_PLACEHOLDER, extXml.toString());

        return xml;
    }

    /**
     * 一个Mapper.java可以对应多个Mapper.xml。只要namespace相同，就会把它们的内容合并，最终形成一个完整的MapperResource<br>
     * 这样做的好处是每人维护一个文件相互不干扰，至少在提交代码是不会冲突，同时也遵循了开闭原则。
     *
     * @throws IOException
     * @throws DocumentException
     */
    private String buildOtherMapperContent(Class<?> mapperClass, Collection<MyBatisResource> mapperResourceDefinitions) throws IOException, DocumentException {
        StringBuilder xml = new StringBuilder();
        String trueNamespace = mapperClass.getName();
        for (MyBatisResource mapperResourceDefinition : mapperResourceDefinitions) {
            String filename = mapperResourceDefinition.getFilename();
            filename = filename.substring(0, filename.length() - 4);
            if (mapperResourceDefinition.isMerged() || mapperNames.contains(filename)) {
                continue;
            }
            InputStream in = mapperResourceDefinition.getInputStream();
            Document document = this.buildSAXReader().read(in);
            Element mapperNode = document.getRootElement();

            Attribute attrNamespace = mapperNode.attribute(SeedMybatisConstants.ATTR_NAMESPACE);
            String namespaceValue = attrNamespace == null ? null : attrNamespace.getValue();

            if (StringUtil.isEmpty(namespaceValue)) {
                throw new MapperFileBuildException("Mapper文件[" + mapperResourceDefinition.getFilename() + "]的namespace不能为空。");
            }

            if (trueNamespace.equals(namespaceValue)) {
                String contentXml = MybatisFileUtil.trimMapperNode(mapperNode);
                xml.append(contentXml);
                mapperResourceDefinition.setMerged(true);
            }
        }
        return xml.toString();

    }

    private SAXReader buildSAXReader() {
        SAXReader reader = new SAXReader();
        reader.setEncoding(SeedMybatisConstants.ENCODE);
        try {
            reader.setFeature(SeedMybatisConstants.SAX_READER_FEATURE, false);
        } catch (SAXException e) {
            log.error("reader.setFeature fail by ", e);
        }
        return reader;
    }

    public void setConfig(SeedMybatisConfig config) {
        this.config = config;
    }

    public String getDbName() {
        return dialect;
    }

    public void setDbName(String dialect) {
        this.dialect = dialect;
    }

    public void setMapperExecutorPoolSize(int poolSize) {
        config.setMapperExecutorPoolSize(poolSize);
    }

    public SeedMybatisConfig getConfig() {
        return config;
    }


}
