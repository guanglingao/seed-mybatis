package org.seed.mybatis.core.query;

/**
 * 排序支持
 */
public interface Sortable {
    /**
     * 是否具备排序,是返回true
     *
     * @return true，具备排序
     */
    boolean getSortable();

    /**
     * 返回排序信息
     *
     * @return 返回排序信息, 如:id ASC,name ASC,date desc. 没有排序则返回""
     */
    String getOrder();
}
