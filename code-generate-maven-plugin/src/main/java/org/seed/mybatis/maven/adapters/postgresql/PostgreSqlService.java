package org.seed.mybatis.maven.adapters.postgresql;


import org.seed.mybatis.maven.GeneratorConfig;
import org.seed.mybatis.maven.dbparser.SQLService;
import org.seed.mybatis.maven.dbparser.TableSelector;

public class PostgreSqlService implements SQLService {
    @Override
    public TableSelector getTableSelector(GeneratorConfig generatorConfig) {
        return new PostgreSqlTableSelector(new PostgreSqlColumnSelector(generatorConfig), generatorConfig);
    }

}
