package org.seed.mybatis.core.ext.code.client;



import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.ext.code.NotEntityException;
import org.seed.mybatis.core.ext.exception.MapperFileBuildException;
import java.io.IOException;


public class ClassClient {


    private static String EMPTY_XML = "" +
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
            "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">" +
            "<mapper namespace=\"%s\"> " +
            " <!--_ext_mapper_--> " +
            " <!--_global_vm_--> " +
            "</mapper>";

    private FileCodeGenerator generator;

    private SeedMybatisConfig config;

    public ClassClient(SeedMybatisConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config不能为null");
        }
        this.config = config;
        this.generator = new FileCodeGenerator();
    }

    /**
     * 生成mybatis文件
     *
     * @param mapperClass      Mapper的class对象
     * @param templateContent  模板资源
     * @param globalVmLocation 全局模板路径
     * @return 返回xml内容
     */
    public String generateMybatisXml(Class<?> mapperClass, String templateContent, String globalVmLocation) {
        ClientParam param = new ClientParam();
        param.setTemplateContent(templateContent);
        param.setGlobalVmLocation(globalVmLocation);
        param.setMapperClass(mapperClass);
        param.setConfig(config);

        try {
            return generator.generateCode(param);
        } catch (NotEntityException e) {
            return String.format(EMPTY_XML, mapperClass.getName());
        } catch (IOException e) {
            throw new MapperFileBuildException(e);
        }
    }

}
