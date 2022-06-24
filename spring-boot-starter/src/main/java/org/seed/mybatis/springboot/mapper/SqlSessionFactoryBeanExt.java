package org.seed.mybatis.springboot.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.SeedMybatisContext;
import org.seed.mybatis.core.ext.MapperLocationsBuilder;
import org.seed.mybatis.core.ext.MyBatisResource;
import org.seed.mybatis.core.util.DbUtil;
import org.seed.mybatis.core.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SqlSessionFactoryBeanExt extends SqlSessionFactoryBean {

    private final MapperLocationsBuilder mapperLocationsBuilder = new MapperLocationsBuilder();

    private Resource[] mapperLocations;

    private Resource[] finalMapperLocations;

    private volatile SqlSessionFactory sqlSessionFactory;

    private String basePackage;

    private String dialect;


    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        SeedMybatisContext.setApplicationContext(applicationContext);
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.dialect = DbUtil.getDialect(dataSource);
    }

    @Override
    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    @Override
    protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
        Assert.notNull(this.basePackage, "属性 'basePackage' 必填");
        List<MyBatisResource> myBatisResources;
        if (this.mapperLocations != null) {
            myBatisResources = new ArrayList<>(this.mapperLocations.length);
            for (Resource mapperLocation : this.mapperLocations) {
                MyBatisResource myBatisResource = new MyBatisResource();
                myBatisResource.setFilename(mapperLocation.getFilename());
                myBatisResource.setContent(IOUtil.toString(mapperLocation.getInputStream(), StandardCharsets.UTF_8));
                myBatisResources.add(myBatisResource);
            }
        } else {
            myBatisResources = new ArrayList<>();
        }
        MyBatisResource[] allMyBatisResource = mapperLocationsBuilder.build(this.basePackage, myBatisResources, this.dialect);

        this.finalMapperLocations = convertResources(allMyBatisResource);
        // 重新设置mapperLocation属性
        super.setMapperLocations(finalMapperLocations);
        this.sqlSessionFactory = super.buildSqlSessionFactory();
        log.debug("SeedMyBatis SqlSessionFactory 构建完成");
        return this.sqlSessionFactory;
    }

    private Resource[] convertResources(MyBatisResource[] allMyBatisResource) {
        Resource[] resources = new Resource[allMyBatisResource.length];
        for (int i = 0; i < allMyBatisResource.length; i++) {
            MyBatisResource myBatisResource = allMyBatisResource[i];
            InputStreamResource inputStreamResource = new InputStreamResource(myBatisResource.getInputStream(), myBatisResource.toString());
            resources[i] = inputStreamResource;
        }
        return resources;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        new HotDeploy(this).start();
    }

    /**
     * @param basePackage 指定哪些包需要被扫描,支持多个包"package.a,package.b"并对每个包都会递归搜索
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setConfig(SeedMybatisConfig config) {
        mapperLocationsBuilder.setConfig(config);
    }

    public SeedMybatisConfig getConfig() {
        return mapperLocationsBuilder.getConfig();
    }

    public Resource[] getMapperLocations() {
        return mapperLocations;
    }

    public Resource[] getFinalMapperLocations() {
        return finalMapperLocations;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * 不能用InputStreamResource，因为InputStreamResource只能read一次
     */
    private static class MapperResource extends ByteArrayResource {
        private final String filename;

        public MapperResource(String xml, String filename) {
            /*
             * 必须指明InputStreamResource的description 主要解决多个dao报Invalid bound
             * statement (not found)BUG。
             * 经排查是由XMLMapperBuilder.parse()中，configuration.isResourceLoaded(
             * resource)返回true导致。 因为InputStreamResource.toString()始终返回同一个值，
             * 需要指定description保证InputStreamResource.toString()唯一。
             */
            super(xml.getBytes(StandardCharsets.UTF_8), filename);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            MapperResource that = (MapperResource) o;
            return Objects.equals(filename, that.filename);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), filename);
        }
    }
}

