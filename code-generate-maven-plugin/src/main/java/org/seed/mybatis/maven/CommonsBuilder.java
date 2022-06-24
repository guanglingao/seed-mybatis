package org.seed.mybatis.maven;


import org.seed.mybatis.maven.fileout.CodeFile;
import org.seed.mybatis.maven.fileout.CommonContext;
import org.seed.mybatis.maven.fileout.CommonsAssembler;
import org.seed.mybatis.maven.util.FileOutput;
import java.io.IOException;
import java.util.List;

/**
 * 通用代码生成
 */
public class CommonsBuilder {



    public static List<CodeFile> buildCommonFiles(String tomlConfigPath) throws IOException {

        // 1.获取作者、父级包等配置
        GeneratorConfig config = new GeneratorConfig(tomlConfigPath);
        String author = config.getAuthor();
        String basePackage = config.getBasePackage();
        String rootPath = config.getRootFolder();
        String serviceName = config.getServiceName();
        // 2.加载通用文件模板，生成对应文件
        CommonContext context = new CommonContext();
        context.setAuthor(author);
        context.setAbsoluteRoot(rootPath);
        context.setPackageName(basePackage);
        context.setServiceName(serviceName);
        List<CodeFile> rst = new CommonsAssembler().generateCodeFile(context);
        FileOutput.create(rst);
        return rst;
    }


}
