package org.seed.mybatis.maven.adapters.mysql;



import org.apache.commons.lang3.StringUtils;
import org.seed.mybatis.maven.dbparser.ColumnDefinition;
import org.seed.mybatis.maven.dbparser.ColumnSelector;
import org.seed.mybatis.maven.GeneratorConfig;
import org.seed.mybatis.maven.dbparser.TypeFormatter;
import org.seed.mybatis.maven.util.ClassField;

import java.util.Map;
import java.util.Set;

/**
 * mysql表信息查询
 */
public class MySqlColumnSelector extends ColumnSelector {

	private static final TypeFormatter TYPE_FORMATTER = new MySqlTypeFormatter();

	private static final String SHOW_SQL = " SELECT " +
			" COLUMN_NAME AS 'Field', " +
			" COLUMN_DEFAULT AS 'Default', " +
			" IS_NULLABLE AS 'Null', " +
			" DATA_TYPE AS 'DataType', " +
			" CASE DATA_TYPE " +
			"     WHEN 'int' THEN NUMERIC_PRECISION " +
			"     WHEN 'varchar' THEN CHARACTER_MAXIMUM_LENGTH " +
			" END AS 'MaxLength', " +
			" IFNULL(NUMERIC_SCALE,0) AS 'Scale', " +
			" COLUMN_TYPE AS 'Type', " +
			" COLUMN_KEY 'KEY', " +
			" EXTRA AS 'Extra', " +
			" COLUMN_COMMENT AS 'Comment' " +
			" FROM information_schema.`COLUMNS` " +
			" WHERE 1=1 AND TABLE_SCHEMA = '%s' AND TABLE_NAME = '%s' ";

	public MySqlColumnSelector(GeneratorConfig generatorConfig) {
		super(generatorConfig);
	}

	/**
	 * SHOW FULL COLUMNS FROM 表名
	 */
	@Override
	protected String getColumnInfoSQL(String tableName) {
		return String.format(SHOW_SQL, getGeneratorConfig().getDbName(), tableName);
	}

	/*
	 * {FIELD=username, EXTRA=, COMMENT=用户名, COLLATION=utf8_general_ci, PRIVILEGES=select,insert,update,references, KEY=PRI, NULL=NO, DEFAULT=null, TYPE=varchar(20)}
	 */
	@Override
	protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap){
		Set<String> columnSet = rowMap.keySet();

		for (String columnInfo : columnSet) {
			rowMap.put(columnInfo.toUpperCase(), rowMap.get(columnInfo));
		}

		ColumnDefinition columnDefinition = new ColumnDefinition();

		columnDefinition.setColumnName(ClassField.convertString(rowMap.get("FIELD")));

		boolean isIdentity = "auto_increment".equalsIgnoreCase(ClassField.convertString(rowMap.get("EXTRA")));
		columnDefinition.setIsIdentity(isIdentity);

		boolean isPk = "PRI".equalsIgnoreCase(ClassField.convertString(rowMap.get("KEY")));
		columnDefinition.setIsPk(isPk);

		String type = ClassField.convertString(rowMap.get("TYPE"));
		columnDefinition.setType(TYPE_FORMATTER.format(type));

		columnDefinition.setComment(ClassField.convertString(rowMap.get("COMMENT")));

		String maxLength = ClassField.convertString(rowMap.get("MAXLENGTH"));
		columnDefinition.setMaxLength(new Integer(StringUtils.isEmpty(maxLength) ? "0" : maxLength));

		String scale = ClassField.convertString(rowMap.get("SCALE"));
		columnDefinition.setScale(new Integer(StringUtils.isEmpty(scale) ? "0" : scale));

		String isNullable = ClassField.convertString(rowMap.get("NULL"));
		columnDefinition.setIsNullable("YES".equalsIgnoreCase(isNullable));

		return columnDefinition;
	}

}