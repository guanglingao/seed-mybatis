package org.seed.mybatis.springboot.scatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String dataSourceKey = RoutingDataSourceContext.getDataSourceKey();
        log.debug("Using Data Source Key: {}", dataSourceKey);
        return dataSourceKey;
    }

}
