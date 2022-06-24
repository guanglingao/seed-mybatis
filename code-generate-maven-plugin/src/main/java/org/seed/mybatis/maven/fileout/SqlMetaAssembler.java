package org.seed.mybatis.maven.fileout;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.seed.mybatis.maven.CodeGenerateContext;
import org.seed.mybatis.maven.dbparser.JavaColumnDefinition;
import org.seed.mybatis.maven.util.VelocityParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqlMetaAssembler {


    /**
     * columns
     **/
    private final static String COLUMNS_TEMPLATE = "/templates/column.vm";
    /**
     * controller
     **/
    private final static String CONTROLLER_TEMPLATE = "/templates/controller.vm";

    /**
     * entity
     **/
    private final static String ENTITY_TEMPLATE = "/templates/entity.vm";
    /**
     * mapper
     **/
    private final static String MAPPER_TEMPLATE = "/templates/mapper.vm";
    /**
     * service
     **/
    private final static String SERVICE_TEMPLATE = "/templates/service.vm";
    // ------ api
    /**
     * export
     **/
    private final static String EXPORT_TEMPLATE = "/templates/api/export.vm";

    /**
     * dto
     */
    private final static String DTO_TEMPLATE = "/templates/api/dto.vm";

    /**
     * api
     */
    private final static String API_TEMPLATE = "/templates/api/api.vm";


    public List<CodeFile> generateCodeFile(SqlContext sqlContext) throws IOException {

        // 组装velocity模板，并生成文件

        VelocityContext context = new VelocityContext();
        JavaColumnDefinition pkColumn = (JavaColumnDefinition) sqlContext.getTableDefinition().getPkColumn();
        if (pkColumn == null) {
            pkColumn = new JavaColumnDefinition();
        }
        context.put("author", sqlContext.getAuthor());
        context.put("serviceName", sqlContext.getServiceName());
        context.put("dateNow", LocalDate.now().toString());
        context.put("context", sqlContext);
        context.put("table", sqlContext.getTableDefinition());
        context.put("pk", pkColumn);
        context.put("columns", sqlContext.getTableDefinition().getColumnDefinitions());
        context.put("csharpColumns", sqlContext.getTableDefinition().getCsharpColumnDefinitions());
        // 加载当前jar包中的模板文件
        String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String targetFolderPath = sqlContext.getAbsoluteRoot() + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + sqlContext.getPackageName().replace(".", File.separator) + File.separator;
        if (CodeGenerateContext.isApiMode()) {
            // 设置基础路径
            targetFolderPath = sqlContext.getAbsoluteRoot() + File.separator + "service" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + sqlContext.getPackageName().replace(".", File.separator) + File.separator;
        }
        String baseJarPath = "jar:file:" + jarPath + "!";

        List<CodeFile> rst = new ArrayList<>();

        // 生成columns文件
        URL url = new URL(baseJarPath + COLUMNS_TEMPLATE);
        String template = IOUtils.toString(url.openStream(), "utf-8");
        String content = VelocityParser.output(context, template);
        String fileName = sqlContext.getJavaBeanName() + "Column.java";
        CodeFile codeFile = new CodeFile(targetFolderPath + "dao" + File.separator + "entity" + File.separator, fileName, content);
        rst.add(codeFile);
        // 生成controller
        url = new URL(baseJarPath + CONTROLLER_TEMPLATE);
        template = IOUtils.toString(url.openStream(), "utf-8");
        content = VelocityParser.output(context, template);
        fileName = sqlContext.getJavaBeanName() + "Controller.java";
        codeFile = new CodeFile(targetFolderPath + "controller" + File.separator, fileName, content);
        rst.add(codeFile);
        // 生成entity
        url = new URL(baseJarPath + ENTITY_TEMPLATE);
        template = IOUtils.toString(url.openStream(), "utf-8");
        content = VelocityParser.output(context, template);
        fileName = sqlContext.getJavaBeanName() + ".java";
        codeFile = new CodeFile(targetFolderPath + "dao" + File.separator + "entity" + File.separator, fileName, content);
        rst.add(codeFile);
        // 生成mapper
        url = new URL(baseJarPath + MAPPER_TEMPLATE);
        template = IOUtils.toString(url.openStream(), "utf-8");
        content = VelocityParser.output(context, template);
        fileName = sqlContext.getJavaBeanName() + "Mapper.java";
        codeFile = new CodeFile(targetFolderPath + "dao" + File.separator + "mapper" + File.separator, fileName, content);
        rst.add(codeFile);
        // 生成service
        url = new URL(baseJarPath + SERVICE_TEMPLATE);
        template = IOUtils.toString(url.openStream(), "utf-8");
        content = VelocityParser.output(context, template);
        fileName = sqlContext.getJavaBeanName() + "Service.java";
        codeFile = new CodeFile(targetFolderPath + "service" + File.separator, fileName, content);
        rst.add(codeFile);
        if (CodeGenerateContext.isApiMode()) {
            // 生成export
            url = new URL(baseJarPath + EXPORT_TEMPLATE);
            template = IOUtils.toString(url.openStream(), "utf-8");
            content = VelocityParser.output(context, template);
            fileName = sqlContext.getJavaBeanName() + "Export.java";
            codeFile = new CodeFile(targetFolderPath + "export" + File.separator, fileName, content);
            rst.add(codeFile);

            targetFolderPath = sqlContext.getAbsoluteRoot() + File.separator + "api" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + sqlContext.getPackageName().replace(".", File.separator) + File.separator;
            // 生成dto
            url = new URL(baseJarPath + DTO_TEMPLATE);
            template = IOUtils.toString(url.openStream(), "utf-8");
            content = VelocityParser.output(context, template);
            fileName = sqlContext.getJavaBeanName() + "Dto.java";
            codeFile = new CodeFile(targetFolderPath + "dto" + File.separator, fileName, content);
            rst.add(codeFile);
            // 生成api
            url = new URL(baseJarPath + API_TEMPLATE);
            template = IOUtils.toString(url.openStream(), "utf-8");
            content = VelocityParser.output(context, template);
            fileName = sqlContext.getJavaBeanName() + "Api.java";
            codeFile = new CodeFile(targetFolderPath + "api" + File.separator, fileName, content);
            rst.add(codeFile);
        }

        return rst;

    }


}
