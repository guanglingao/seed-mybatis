package org.seed.mybatis.springboot.scatter;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 分库、分表；重定向表名
 */
@Component
@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class RedirectToTableInterceptor implements Interceptor {


    private static boolean enableTableSharding = false;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (!enableTableSharding) {
            // 未开启分表，直接执行
            return invocation.proceed();
        }

        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        Statement statement = CCJSqlParserUtil.parse(sql);
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(statement);
        String newSql = null;
        for (String tableName : tableList) {
            if (tableName.startsWith("`") && tableName.endsWith("`")) {
                tableName = tableName.substring(1, tableName.length() - 1);
            }
            if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
                tableName = tableName.substring(1, tableName.length() - 1);
            }
            String newTableName = RoutingDataSourceContext.getTablePrefix() + tableName + RoutingDataSourceContext.getTableSuffix();
            newSql = sql.replace(tableName, newTableName);
            log.debug("Replaced Table Name [{}] with [{}]", tableName, newTableName);
            log.debug("Original-SQL: {}", sql);
            log.debug("New-SQL: {}", newSql);
        }
        BoundSql bs = new BoundSql(ms.getConfiguration(), newSql, boundSql.getParameterMappings(), parameterObject);
        MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(bs));
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                bs.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        args[0] = newMs;
        return invocation.proceed();
    }


    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }


    public static void setEnableTableSharding(boolean enableTableSharding) {
        RedirectToTableInterceptor.enableTableSharding = enableTableSharding;
    }


    private static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }


}
