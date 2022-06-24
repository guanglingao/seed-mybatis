package org.seed.mybatis.core.util;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.seed.mybatis.core.ext.exception.DatabaseConnectException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 *
 */
public class DbUtil {

    private static final Log LOG = LogFactory.getLog(DbUtil.class);

    /**
     * 获取数据库类型
     */
    public static String getDialect(DataSource dataSource) {
        if (dataSource == null) {
            throw new NullPointerException("dataSource 不能为null");
        }
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String productName = metaData.getDatabaseProductName();
            LOG.debug("数据库名称：" + productName);
            return productName;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new DatabaseConnectException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }
}
