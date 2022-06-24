package org.seed.mybatis.maven;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.seed.mybatis.maven.dbparser.SQLService;
import org.seed.mybatis.maven.dbparser.SQLServiceFactory;
import org.seed.mybatis.maven.dbparser.TableDefinition;
import org.seed.mybatis.maven.dbparser.TableSelector;
import org.seed.mybatis.maven.fileout.CodeFile;
import org.seed.mybatis.maven.fileout.SqlMetaAssembler;
import org.seed.mybatis.maven.fileout.SqlContext;
import org.seed.mybatis.maven.util.FileOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
public class CodesBuilder {


    public static List<CodeFile> buildCodeFiles(String tomlConfigPath) throws IOException {
        // 1. 加载全部配置
        GeneratorConfig config = new GeneratorConfig(tomlConfigPath);
        SQLService service = SQLServiceFactory.build(config);
        List<TableDefinition> tableDefinitionAll = service.getTableSelector(config).getSimpleTableDefinitions();
        // 2. 过滤掉不需要生成的表
        List<TableDefinition> tableDefinitions = filterTablesByConfig(tableDefinitionAll, config);
        if (tableDefinitions.isEmpty()) {
            log.error("待生成的数据表，完成过滤后为空，生成流程已经中止！");
            return null;
        }

        // 3. 对每一张表，生成文件对象
        List<SqlContext> sqlContextList = buildSQLContextList(tableDefinitions, config);

        List<CodeFile> rst = new ArrayList<>();
        for (SqlContext sqlContext : sqlContextList) {
            rst.addAll(new SqlMetaAssembler().generateCodeFile(sqlContext));
        }

        // 4.  输出
        FileOutput.create(rst);
        return rst;
    }


    private static List<TableDefinition> filterTablesByConfig(List<TableDefinition> all, GeneratorConfig config) {
        List<TableDefinition> include = null;
        if (StringUtils.isNotBlank(config.getTablesInclude())) {
            if (config.getTablesInclude().equals("*") || config.getTablesInclude().equals("all")) {
                // 全部数据表
                include = all;
            } else {
                // 只是include中的表
                String includeStr = config.getTablesInclude();
                include = new ArrayList<>();
                String[] includeArr = includeStr.split(",");
                for (TableDefinition tableDefinition : all) {
                    for (String includeName : includeArr) {
                        if (StringUtils.isBlank(includeName)) {
                            continue;
                        }
                        if (tableDefinition.getTableName().equals(includeName)) {
                            include.add(tableDefinition);
                        } else {
                            if (Pattern.matches(includeName, tableDefinition.getTableName())) {
                                include.add(tableDefinition);
                            }
                        }
                    }
                }
            }
        } else {
            include = all;
        }

        // 排除例外
        String excludeStr = config.getTablesExclude();
        if (StringUtils.isBlank(excludeStr)) {
            return include;
        }
        String[] excludeArr = excludeStr.split(",");
        Iterator<TableDefinition> iterator = include.iterator();
        while (iterator.hasNext()) {
            TableDefinition tableDefinition = iterator.next();
            for (String excludeName : excludeArr) {
                if (StringUtils.isBlank(excludeName)) {
                    continue;
                }
                if (excludeName.equals(tableDefinition.getTableName())) {
                    iterator.remove();
                } else {
                    if (Pattern.matches(excludeName, tableDefinition.getTableName())) {
                        iterator.remove();
                    }
                }
            }
        }

        return include;
    }


    private static List<SqlContext> buildSQLContextList(List<TableDefinition> tableDefinitions, GeneratorConfig generatorConfig) {

        List<String> tableNames = tableDefinitions.stream().map(o -> o.getTableName()).collect(Collectors.toList());
        List<SqlContext> contextList = new ArrayList<>();
        SQLService service = SQLServiceFactory.build(generatorConfig);

        TableSelector tableSelector = service.getTableSelector(generatorConfig);
        tableSelector.setSchTableNames(tableNames);

        tableDefinitions = tableSelector.getTableDefinitions();

        for (TableDefinition tableDefinition : tableDefinitions) {
            SqlContext sqlContext = new SqlContext(tableDefinition);
            sqlContext.setDbName(generatorConfig.getDbName());
            if (StringUtils.isNotBlank(generatorConfig.getTableNamePrefix())) {
                sqlContext.setDelPrefixes(Arrays.asList(generatorConfig.getTableNamePrefix().split(",")));
            }
            if (StringUtils.isNotBlank(generatorConfig.getTableNameSuffix())) {
                sqlContext.setDelSuffixes(Arrays.asList(generatorConfig.getTableNameSuffix().split(",")));
            }
            sqlContext.setPackageName(generatorConfig.getBasePackage());
            sqlContext.setAuthor(generatorConfig.getAuthor());
            sqlContext.setAbsoluteRoot(generatorConfig.getRootFolder());
            sqlContext.setServiceName(generatorConfig.getServiceName());
            contextList.add(sqlContext);
        }
        return contextList;
    }




}
