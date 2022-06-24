package org.seed.mybatis.maven.adapters.oracle;



import org.seed.mybatis.maven.dbparser.ColumnSelector;
import org.seed.mybatis.maven.GeneratorConfig;
import org.seed.mybatis.maven.dbparser.TableDefinition;
import org.seed.mybatis.maven.dbparser.TableSelector;
import org.seed.mybatis.maven.util.ClassField;

import java.util.Map;


/**
 * 查询oracle数据库表
 */
public class OracleTableSelector extends TableSelector {

	public OracleTableSelector(ColumnSelector columnSelector,
							   GeneratorConfig dataBaseConfig) {
		super(columnSelector, dataBaseConfig);
	}

	/**
	 * SELECT a.TABLE_NAME,b.COMMENTS
	 * FROM ALL_TABLES a,USER_TAB_COMMENTS b
	 * WHERE a.TABLE_NAME=b.TABLE_NAME
	 * AND a.OWNER='SYSTEM'
	 * @param generatorConfig generatorConfig
	 * @return
	 */
	@Override
	protected String getShowTablesSQL(GeneratorConfig generatorConfig) {
		StringBuilder sb = new StringBuilder("");
		sb.append(" SELECT a.TABLE_NAME as NAME,b.COMMENTS" +
				"  FROM ALL_TABLES a,USER_TAB_COMMENTS b" +
				"  WHERE a.TABLE_NAME=b.TABLE_NAME");
		sb.append(" AND 1=1 ");
		if(this.getSchTableNames() != null && this.getSchTableNames().size() > 0) {
			StringBuilder tables = new StringBuilder();
			for (String table : this.getSchTableNames()) {
				tables.append(",'").append(table).append("'");
			}
			sb.append(" AND a.TABLE_NAME IN (" + tables.substring(1) + ")");
		}
		return sb.toString();
	}

	@Override
	protected TableDefinition buildTableDefinition(Map<String, Object> tableMap) {
		TableDefinition tableDefinition = new TableDefinition();
		tableDefinition.setTableName(ClassField.convertString(tableMap.get("NAME")));
		tableDefinition.setComment(ClassField.convertString(tableMap.get("COMMENTS")));
		return tableDefinition;
	}

}
