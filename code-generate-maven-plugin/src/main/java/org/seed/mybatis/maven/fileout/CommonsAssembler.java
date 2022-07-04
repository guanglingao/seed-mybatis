package org.seed.mybatis.maven.fileout;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.seed.mybatis.maven.CodeGenerateContext;
import org.seed.mybatis.maven.util.VelocityParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用文件生成
 *
 * @author guanglin.gao
 * @date 2022-05-31
 */
public class CommonsAssembler {


    /**
     * Service 中通用模板
     */
    private static Map<String, String> commonFileMap = new HashMap<>();

    /**
     * API 中通用模板
     */
    private static Map<String, String> commonApiFileMap = new HashMap<>();

    /**
     * 配置 通用模板
     */
    private static Map<String, String> commonConfigFileMap = new HashMap<>();

    /**
     * api 配置模板
     */
    private static Map<String, String> commonApiConfigFileMap = new HashMap<>();

    static {
        // 工具类
        commonFileMap.put("util/CodeAuth.vm", "util");
        commonFileMap.put("util/HttpClient.vm", "util");
        commonFileMap.put("util/IDWorker.vm", "util");
        commonFileMap.put("util/MD5.vm", "util");
        commonFileMap.put("util/RandomStr.vm", "util");
        commonFileMap.put("util/ScaleConvertor.vm", "util");
        commonFileMap.put("util/SpringTool.vm", "util");

        // 应用运行配置
        commonFileMap.put("configuration/CurrentUser.vm", "configuration");
        commonFileMap.put("configuration/CurrentUserAttachService.vm", "configuration");
        commonFileMap.put("configuration/CurrentUserMethodArgumentResolver.vm", "configuration");
        commonFileMap.put("configuration/DataSourceConfiguration.vm", "configuration");
        commonFileMap.put("configuration/EnableOpenFeign.vm", "configuration");
        commonFileMap.put("configuration/GlobalInterceptor.vm", "configuration");
        commonFileMap.put("configuration/OkhttpConfiguration.vm", "configuration");
        commonFileMap.put("configuration/RequestParamHandler.vm", "configuration");
        commonFileMap.put("configuration/SecurityFilter.vm", "configuration");
        commonFileMap.put("configuration/TransactionAdvisor.vm", "configuration");
        commonFileMap.put("configuration/WebConfiguration.vm", "configuration");

        // 程序运行通用配置
        commonFileMap.put("universal/ErrorPageRouter.vm", "universal");
        commonFileMap.put("universal/ExceptionHandlerAdvisor.vm", "universal");
        commonFileMap.put("universal/LocalCache.vm", "universal");
        commonFileMap.put("universal/ThreadContextHolder.vm", "universal");
        commonFileMap.put("Application.vm", "");

        // 填充api通用文件
        commonApiFileMap.put("", "dto");

        // 生成pom文件和配置文件
        commonConfigFileMap.put("application.yml", "src/main/resources");
        commonConfigFileMap.put("DemoComplexSqlMapper.xml.bak", "src/main/resources/mapper");
        commonConfigFileMap.put("pom.xml", "");
        commonConfigFileMap.put("smart-doc.json", "src/main/resources");

        // -- api
        commonApiConfigFileMap.put("api/pom.xml", "");

    }


    public List<CodeFile> generateCodeFile(CommonContext context) throws IOException {

        List<CodeFile> rst = new ArrayList<>();
        String jarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String targetFolderPath = context.getAbsoluteRoot() + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + context.getPackageName().replace(".", File.separator) + File.separator;
        String baseJarPath = "jar:file:" + jarPath + "!/templates/commons/";
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("author", context.getAuthor());
        velocityContext.put("dateNow", LocalDate.now().toString());
        velocityContext.put("serviceName", context.getServiceName());
        velocityContext.put("context", context);
        // 生成ServiceApp文件
        if (CodeGenerateContext.isApiMode()) {
            // 设置基础路径
            targetFolderPath = context.getAbsoluteRoot() + File.separator + "service" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + context.getPackageName().replace(".", File.separator) + File.separator;
        }
        fillCodeFiles(commonFileMap, rst, baseJarPath, targetFolderPath, velocityContext);
        targetFolderPath = context.getAbsoluteRoot() + File.separator + "api" + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + context.getPackageName().replace(".", File.separator) + File.separator;
        if (CodeGenerateContext.isApiMode()) {
            // 生成 api 文件
            fillCodeFiles(commonApiFileMap, rst, baseJarPath, targetFolderPath, velocityContext);
        }

        // 生成配置文件和pom文件
        targetFolderPath = context.getAbsoluteRoot() + File.separator + "service" + File.separator;
        fillCodeFiles(commonConfigFileMap, rst, baseJarPath, targetFolderPath, velocityContext);

        targetFolderPath = context.getAbsoluteRoot() + File.separator + "api" + File.separator;
        if (CodeGenerateContext.isApiMode()) {
            // 生成 api 配置文件
            fillCodeFiles(commonApiConfigFileMap, rst, baseJarPath, targetFolderPath, velocityContext);
        }
        return rst;
    }

    private void fillCodeFiles(final Map<String, String> fileMap, final List<CodeFile> codeFiles, String baseJarPath, String targetFolderPath, VelocityContext velocityContext) throws IOException {
        for (Map.Entry<String, String> entry : fileMap.entrySet()) {
            CodeFile codeFile = new CodeFile();
            String filePath = entry.getKey();
            if (filePath.trim().equals("")) {
                continue;
            }
            String fileName = null;
            if (filePath.endsWith(".vm")) {
                fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".")) + ".java";
            } else {
                fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            }
            URL url = new URL(baseJarPath + filePath);
            String template = IOUtils.toString(url.openStream(), "utf-8");
            codeFile.setFileName(fileName);
            codeFile.setFolder(targetFolderPath + entry.getValue() + File.separator);

            String content = null;
            if (!filePath.endsWith(".vm")) {
                if (CodeGenerateContext.isApiMode()) {
                    // 在pom中加入api依赖，因为api中定义了dto
                    if (filePath.endsWith("pom.xml") && !filePath.contains("api/")) {
                        String dependent = "\n" +
                                "        <dependency>\n" +
                                "            <groupId>${context.packageName}</groupId>\n" +
                                "            <artifactId>${serviceName}-api</artifactId>\n" +
                                "            <version>1.0.0-SNAPSHOT</version>\n" +
                                "        </dependency>";
                        int i = template.indexOf("<dependencies>");
                        template = template.substring(0, i + "<dependencies>".length()) + dependent + template.substring(i + "<dependencies>".length());
                    }
                }

                CommonContext context = (CommonContext) velocityContext.get("context");
                content = StringUtils.replace(template, "${context.packageName}", context.getPackageName(), -1);
                content = StringUtils.replace(content, "${serviceName}", context.getServiceName(), -1);
                content = StringUtils.replace(content, "${dateNow}", LocalDate.now().toString(), -1);
                content = StringUtils.replace(content, "${author}", context.getAuthor(), -1);

            } else {
                content = VelocityParser.output(velocityContext, template);
            }
            codeFile.setContent(content);
            codeFiles.add(codeFile);
        }
    }

}
