package org.seed.mybatis.core.ext.code.generator;


/**
 * MyBatis Mapper 文件Association
 */
public class AssociationDefinition {

	private String property;
	private String column;
	private String select;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

}
