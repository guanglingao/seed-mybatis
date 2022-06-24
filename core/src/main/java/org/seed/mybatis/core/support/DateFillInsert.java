package org.seed.mybatis.core.support;


import org.seed.mybatis.core.handler.BaseFill;
import org.seed.mybatis.core.handler.FillType;

import java.util.Date;

/**
 * insert时的字段填充<br>
 * 在做insert操作时,如果表里面有gmt_create字段,则自动填充时间
 *
 *
 */
public class DateFillInsert extends BaseFill<Date> {

	private String columnName = "gmt_create";

	public DateFillInsert() {
		super();
	}

	public DateFillInsert(String columnName) {
		super();
		this.columnName = columnName;
	}

	@Override
	public FillType getFillType() {
		return FillType.INSERT;
	}

	@Override
	public Date getFillValue(Date defaultValue) {
		if(defaultValue == null) {
			defaultValue = new Date();
		}
		return defaultValue;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

}
