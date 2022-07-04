package org.seed.mybatis.springboot.scatter;

public class VoidShardingStrategy implements ShardingStrategy {

    private Object basis;

    @Override
    public String getDataSourceId() {
        return null;
    }

    @Override
    public String getTablePrefix() {
        return null;
    }

    @Override
    public String getTableSuffix() {
        return null;
    }

    @Override
    public void setBasis(Object basis) {
        this.basis = basis;
    }
}
