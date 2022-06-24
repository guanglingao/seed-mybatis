package org.seed.mybatis.core.util;


import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.seed.mybatis.core.SeedMybatisConstants;
import org.seed.mybatis.core.ext.exception.MapperFileBuildException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class MybatisFileUtil {

    private static final Log LOG = LogFactory.getLog(MybatisFileUtil.class);

    /**
     * 获取扩展文件内容
     *
     * @param in 扩展文件
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static String getExtFileContent(InputStream in) throws DocumentException {
        Document document = buildSAXReader().read(in);
        Element mapperNode = document.getRootElement();
        return trimMapperNode(mapperNode);
    }

    /**
     * 去除{@literal <mapper></mapper>}只保留中间内容部分
     *
     * @param mapperNode
     * @return
     */
    public static String trimMapperNode(Element mapperNode) {
        String rootNodeName = mapperNode.getName();

        if (!SeedMybatisConstants.NODE_MAPPER.equals(rootNodeName)) {
            throw new MapperFileBuildException("mapper文件必须含有<mapper>节点,是否缺少<mapper></mapper>?");
        }
        // 去除namespace属性
        mapperNode.remove(SeedMybatisConstants.NAMESPACE);
        String xml = mapperNode.asXML();
        xml = xml
                // 去除<mapper>
                .replace(SeedMybatisConstants.MAPPER_START, SeedMybatisConstants.EMPTY)
                // 去除</mapper>
                .replace(SeedMybatisConstants.MAPPER_END, SeedMybatisConstants.EMPTY)
                // 去除<mapper/>
                .replace(SeedMybatisConstants.MAPPER_EMPTY, SeedMybatisConstants.EMPTY);
        return xml;
    }

    private static SAXReader buildSAXReader() {
        SAXReader reader = new SAXReader();
        reader.setEncoding(SeedMybatisConstants.ENCODE);
        try {
            reader.setFeature(SeedMybatisConstants.SAX_READER_FEATURE, false);
        } catch (SAXException e) {
            LOG.error("reader.setFeature fail by ", e);
        }
        return reader;
    }
}
