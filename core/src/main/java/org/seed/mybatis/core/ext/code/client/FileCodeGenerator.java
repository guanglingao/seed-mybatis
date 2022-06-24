package org.seed.mybatis.core.ext.code.client;


import org.apache.ibatis.io.Resources;
import org.apache.velocity.VelocityContext;
import org.seed.mybatis.core.ext.code.NotEntityException;
import org.seed.mybatis.core.ext.code.generator.MapperContext;
import org.seed.mybatis.core.ext.code.generator.TableDefinition;
import org.seed.mybatis.core.ext.code.generator.TableSelector;
import org.seed.mybatis.core.ext.code.util.VelocityUtil;
import org.seed.mybatis.core.util.IOUtil;
import org.seed.mybatis.core.util.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 代码生成器，根据定义好的velocity模板生成代码
 */
public class FileCodeGenerator {

    public String generateCode(ClientParam clientParam) throws NotEntityException, IOException {
        String finalContent = this.buildTemplateInputStream(clientParam);
        MapperContext sqlContext = this.buildClientSQLContextList(clientParam);
        VelocityContext context = new VelocityContext();

        TableDefinition tableDefinition = sqlContext.getTableDefinition();

        // vm模板中的变量
        context.put("context", sqlContext);
        context.put("table", tableDefinition);
        context.put("key", tableDefinition.getKeyColumn());
        context.put("columns", tableDefinition.getTableColumns());
        // include transient column
        context.put("allColumns", tableDefinition.getAllColumns());
        context.put("countExpression", clientParam.getCountExpression());
        context.put("associations", tableDefinition.getAssociationDefinitions());

        return VelocityUtil.generate(context, finalContent);
    }

    /**
     * 返回模板文件内容
     *
     * @throws IOException
     */
    private String buildTemplateInputStream(ClientParam clientParam) throws IOException {
        // 模板文件
        String templateContent = clientParam.getTemplateContent();
        try {
            if (StringUtil.hasText(clientParam.getGlobalVmLocation())) {
                // 全局模板文件
                InputStream inputStream = Resources.getResourceAsStream(clientParam.getGlobalVmLocation());
                return this.mergeGlobalVm(templateContent, IOUtil.toString(inputStream, StandardCharsets.UTF_8), clientParam.getGlobalVmPlaceholder());
            } else {
                return templateContent;
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 合并全局模板,globalVmResource的内容合并到vmResource中
     */
    private String mergeGlobalVm(String templateContent, String globalVmContent, String placeholder) throws IOException {
        return templateContent.replace(placeholder, globalVmContent);
    }

    /**
     * 返回SQL上下文列表
     *
     * @param clientParam 参数
     * @return 返回SQL上下文
     * @throws NotEntityException
     */
    private MapperContext buildClientSQLContextList(ClientParam clientParam) throws NotEntityException {
        Class<?> entityClass = clientParam.getEntityClass();
        if (entityClass == Object.class || entityClass == Void.class) {
            throw new NotEntityException();
        }
        TableSelector tableSelector = new TableSelector(entityClass, clientParam.getConfig());

        TableDefinition tableDefinition = tableSelector.getTableDefinition();

        MapperContext context = new MapperContext(tableDefinition);

        String namespace = this.buildNamespace(clientParam.getMapperClass());
        context.setClassName(entityClass.getName());
        context.setClassSimpleName(entityClass.getSimpleName());
        context.setPackageName(entityClass.getPackage().getName());
        context.setNamespace(namespace);

        return context;
    }

    private String buildNamespace(Class<?> mapperClass) {
        return mapperClass.getName();
    }

}
