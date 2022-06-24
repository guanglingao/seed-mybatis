package org.seed.mybatis.core;

import org.dom4j.Attribute;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;

/**
 *
 */
public interface SeedMybatisConstants {

    String ENCODE = "UTF-8";

    String EMPTY = "";
    String XML_SUFFIX = ".xml";
    String NODE_MAPPER = "mapper";
    String MAPPER_START = "<mapper>";
    String MAPPER_END = "</mapper>";
    String MAPPER_EMPTY = "<mapper/>";
    String ATTR_NAMESPACE = "namespace";
    String SAX_READER_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";


    String EXT_MAPPER_PLACEHOLDER = "<!--_ext_mapper_-->";
    String TEMPLATE_SUFFIX = ".vm";
    String DEFAULT_CLASS_PATH = "/seed-mybatis/tpl/";

    Attribute NAMESPACE = new DOMAttribute(new QName(ATTR_NAMESPACE));

}
