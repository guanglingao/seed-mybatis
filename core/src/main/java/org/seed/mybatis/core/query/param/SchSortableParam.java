package org.seed.mybatis.core.query.param;


import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.query.Query;
import org.seed.mybatis.core.query.TenantQuery;

/**
 *
 */
public interface SchSortableParam extends IParam {
	/**返回排序字段
	 * @return 返回排序字段
	 */
	String fetchSortName();
	/**返回排序字段
	 * @return 返回排序字段
	 */
	String fetchSortOrder();
	/**
	 * 数据库排序字段
	 * @return 返回数据库排序字段
	 */
	default String fetchDBSortname() {
		return FieldUtil.camelToUnderline(fetchSortName());
	}


	@Override
	default Query toQuery() {
		return IParam.super.toQuery().orderByAsc(fetchDBSortname(), fetchSortOrder());
	}

	@Override
	default TenantQuery toTenantQuery() {
		TenantQuery tenantQuery = IParam.super.toTenantQuery();
		tenantQuery.orderByAsc(fetchDBSortname(), fetchSortOrder());
		return tenantQuery;
	}

}
