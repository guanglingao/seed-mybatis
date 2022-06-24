package org.seed.mybatis.maven;


import io.ous.jtoml.JToml;
import io.ous.jtoml.Toml;
import io.ous.jtoml.TomlTable;
import lombok.extern.slf4j.Slf4j;
import org.seed.mybatis.maven.dbparser.DbType;
import java.io.File;
import java.io.IOException;

@Slf4j
public class GeneratorConfig {

    private Integer dbType;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * schema(PGSQL专用)
     */
    private String schemaName;
    /**
     * 数据库host
     */
    private String host;
    /**
     * 数据库端口
     */
    private Integer port;
    /**
     * 数据库用户名
     */
    private String username;
    /**
     * 数据库密码
     */
    private String password;
    /**
     * 全局代码基本包
     **/
    private String basePackage;
    /**
     * 包含哪些表
     **/
    private String tablesInclude;
    /**
     * 排除哪些表
     **/
    private String tablesExclude;
    /**
     * 去除表名前缀
     **/
    private String tableNamePrefix;
    /**
     * 去除表名后缀
     **/
    private String tableNameSuffix;

    /**
     * 作者
     */
    private String author;

    /**
     * 从maven的basedir中读取根目录
     */
    private String rootFolder;

    /**
     * 服务名
     */
    private String serviceName;

    public GeneratorConfig(String tomlPath) throws IOException {
        String generateConfigFile = tomlPath +File.separator+"code-generate-meta.toml";
        this.setRootFolder(tomlPath);
        // 加载本地的toml配置文件
        // 设置当前项目的路径 rootFolder
        Toml result = JToml.parse(new File(generateConfigFile));

        TomlTable appTable = result.getTomlTable("application");
        // 全局属性
        this.setBasePackage(appTable.getString("basePackage"));
        this.setAuthor(appTable.getString("author"));
        this.setServiceName(appTable.getString("serviceName"));
        TomlTable dbTable = result.getTomlTable("database");
        // 数据库类型
        String driverClass = dbTable.getString("driverClass");
        String jdbcUrl = dbTable.getString("jdbcUrl");
        if (driverClass.contains("mysql")) {
            this.setDbType(DbType.of("mysql").getType());
            String hostPortDb = jdbcUrl.replace("jdbc:mysql://", "");
            String host = hostPortDb.substring(0, hostPortDb.indexOf(":"));
            String port = hostPortDb.substring(hostPortDb.indexOf(":") + 1, hostPortDb.indexOf("/"));
            String db = hostPortDb.substring(hostPortDb.indexOf("/") + 1, hostPortDb.indexOf("?"));
            this.setHost(host);
            this.setPort(Integer.valueOf(port));
            this.setDbName(db);
            this.setSchemaName(null);
        } else if (driverClass.contains("oracle")) {
            this.setDbType(DbType.of("oracle").getType());
            String hostPortDb = jdbcUrl.replace("jdbc:oracle:thin:@", "");
            String host = hostPortDb.substring(0, hostPortDb.indexOf(":"));
            String port = hostPortDb.substring(hostPortDb.indexOf(":") + 1, hostPortDb.indexOf("/"));
            String db = hostPortDb.substring(hostPortDb.indexOf("/") + 1);
            this.setHost(host);
            this.setPort(Integer.valueOf(port));
            this.setDbName(db);
            this.setSchemaName(null);
        } else if (driverClass.contains("sqlserver")) {
            this.setDbType(DbType.of("sqlserver").getType());
            String hostPortDb = jdbcUrl.replace("jdbc:sqlserver://", "");
            String host = hostPortDb.substring(0, hostPortDb.indexOf(":"));
            String port = hostPortDb.substring(hostPortDb.indexOf(":") + 1, hostPortDb.indexOf(";"));
            String db = hostPortDb.substring(hostPortDb.indexOf("DatabaseName=") + 1);
            this.setHost(host);
            this.setPort(Integer.valueOf(port));
            this.setDbName(db);
            this.setSchemaName(null);

        } else if (driverClass.contains("postgresql")) {
            this.setDbType(DbType.of("postgresql").getType());
            String hostPortDb = jdbcUrl.replace("jdbc:postgresql://", "");
            String host = hostPortDb.substring(0, hostPortDb.indexOf(":"));
            String port = hostPortDb.substring(hostPortDb.indexOf(":") + 1, hostPortDb.indexOf("/"));

            String db = null;
            String schemaName = "public";
            if (hostPortDb.indexOf("?") > 0) {
                db = hostPortDb.substring(hostPortDb.indexOf("/") + 1, hostPortDb.indexOf("?"));
                if (hostPortDb.indexOf("searchpath") > 0) {
                    schemaName = hostPortDb.substring(hostPortDb.indexOf("searchpath=") + 1);
                }
                if (hostPortDb.indexOf("currentSchema") > 0) {
                    schemaName = hostPortDb.substring(hostPortDb.indexOf("currentSchema=") + 1);
                }
            } else {
                db = hostPortDb.substring(hostPortDb.indexOf("/") + 1);
            }
            this.setHost(host);
            this.setPort(Integer.valueOf(port));
            this.setDbName(db);
            this.setSchemaName(schemaName);
        }
        // 用户名密码
        String userName = dbTable.getString("userName");
        this.setUsername(userName);
        String password = dbTable.getString("password");
        this.setPassword(password);

        // 表名识别配置
        TomlTable tablesTable = result.getTomlTable("tables");
        this.setTablesInclude(tablesTable.getString("include"));
        this.setTablesExclude(tablesTable.getString("exclude"));
        this.setTableNamePrefix(tablesTable.getString("prefix"));
        this.setTableNameSuffix(tablesTable.getString("suffix"));

    }

    public String getDriverClass() {
        DbType dbType = DbType.of(this.dbType);
        if (dbType == null) {
            throw new RuntimeException("不支持数据库类型" + this.dbType + "，请在DbType.java中配置");
        }
        return dbType.getDriverClass();
    }

    public String getJdbcUrl() {
        DbType dbType = DbType.of(this.dbType);
        if (dbType == null) {
            throw new RuntimeException("不支持数据库类型" + this.dbType + "，请在DbType.java中配置");
        }
        String jdbcUrl = dbType.getJdbcUrl();
        return String.format(jdbcUrl, host, port, dbName);
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Integer getDbType() {
        return dbType;
    }

    public void setDbType(Integer dbType) {
        this.dbType = dbType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getTablesInclude() {
        return tablesInclude;
    }

    public void setTablesInclude(String tablesInclude) {
        this.tablesInclude = tablesInclude;
    }

    public String getTablesExclude() {
        return tablesExclude;
    }

    public void setTablesExclude(String tablesExclude) {
        this.tablesExclude = tablesExclude;
    }

    public String getTableNamePrefix() {
        return tableNamePrefix;
    }

    public void setTableNamePrefix(String tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix;
    }

    public String getTableNameSuffix() {
        return tableNameSuffix;
    }

    public void setTableNameSuffix(String tableNameSuffix) {
        this.tableNameSuffix = tableNameSuffix;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
