package org.seed.mybatis.maven.fileout;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.seed.mybatis.maven.dbparser.JavaColumnDefinition;
import org.seed.mybatis.maven.dbparser.TableDefinition;
import org.seed.mybatis.maven.util.ClassField;
import org.seed.mybatis.maven.util.IDWorker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * SQL上下文,这里可以取到表,字段信息<br>
 * 最终会把SQL上下文信息放到velocity中
 */
public class SqlContext extends BaseContext{

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LocalDateTime localDateTime = LocalDateTime.now();

    /**
     * 表结构定义
     */
    private final TableDefinition tableDefinition;
    private final JavaColumnDefinition javaPkColumn;


    /**
     * 删除的前缀
     */
    private List<String> delPrefixes;

    /**
     * 待删除的后缀
     */
    private List<String> delSuffixes;

    /**
     * 数据库名
     */
    private String dbName;



    public SqlContext(TableDefinition tableDefinition) {
        this.tableDefinition = tableDefinition;
        // 默认为全字母小写的类名
        super.setPackageName(getJavaBeanName().toLowerCase());
        this.javaPkColumn = (JavaColumnDefinition) this.tableDefinition.getPkColumn();
    }

    public String getDatetime() {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }

    public String getDate() {
        return localDateTime.format(DATE_FORMATTER);
    }

    public String getTime() {
        return localDateTime.format(TIME_FORMATTER);
    }

    public int getRandomInt() {
        return RandomUtils.nextInt(1,Integer.MAX_VALUE);
    }

    public long getRandomLong() {
        return RandomUtils.nextLong();
    }

    public boolean getRandomBoolean() {
        return RandomUtils.nextBoolean();
    }

    public String getUuid() {
        return UUID.randomUUID().toString();
    }

    public long getNextId() {
        return IDWorker.getInstance().nextId();
    }

    /**
     * 返回Java类名
     *
     * @return
     */
    public String getJavaBeanName() {
        return getClassName();
    }

    /**
     * 返回类名
     * @return
     */
    public String getClassName() {
        String tableName = getJavaBeanNameLF();
        return ClassField.upperFirstLetter(tableName);
    }

    /**
     * 返回Java类名且首字母小写
     *
     * @return
     */
    public String getJavaBeanNameLF() {
        String tableName = tableDefinition.getTableName();
        if(CollectionUtils.isNotEmpty(delPrefixes)){
            for(String prefix : delPrefixes){
                if(tableName.startsWith(prefix)){
                    tableName = StringUtils.removeStart(tableName,prefix);
                }else {
                    tableName = RegExUtils.removeFirst(tableName, prefix);
                }
            }
        }

        if(CollectionUtils.isNotEmpty(delSuffixes)){
            for(String suffix : delSuffixes){
                if(tableName.endsWith(suffix)){
                    tableName = StringUtils.removeEnd(tableName,suffix);
                }else {
                    tableName = RegExUtils.removePattern(tableName, suffix);
                }
            }
        }

        tableName = ClassField.underlineFilter(tableName);
        tableName = ClassField.dotFilter(tableName);
        return ClassField.lowerFirstLetter(tableName);
    }

    public String getPkName() {
        if (javaPkColumn != null) {
            return javaPkColumn.getColumnName();
        }
        return "";
    }

    public String getJavaPkName() {
        if (javaPkColumn != null) {
            return javaPkColumn.getJavaFieldName();
        }
        return "";
    }

    public String getJavaPkType() {
        if (javaPkColumn != null) {
            return javaPkColumn.getJavaType();
        }
        return "";
    }

    public String getMybatisPkType() {
        if (javaPkColumn != null) {
            return javaPkColumn.getMybatisJdbcType();
        }
        return "";
    }

    public TableDefinition getTableDefinition() {
        return tableDefinition;
    }





    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }


    public List<String> getDelPrefixes() {
        return delPrefixes;
    }

    public void setDelPrefixes(List<String> delPrefixes) {
        this.delPrefixes = delPrefixes;
    }

    public List<String> getDelSuffixes() {
        return delSuffixes;
    }

    public void setDelSuffixes(List<String> delSuffixes) {
        this.delSuffixes = delSuffixes;
    }


}

