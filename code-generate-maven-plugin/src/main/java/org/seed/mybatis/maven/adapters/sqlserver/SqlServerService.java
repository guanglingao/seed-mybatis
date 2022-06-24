package org.seed.mybatis.maven.adapters.sqlserver;


import org.seed.mybatis.maven.GeneratorConfig;
import org.seed.mybatis.maven.dbparser.SQLService;
import org.seed.mybatis.maven.dbparser.TableSelector;

public class SqlServerService implements SQLService {

	@Override
	public TableSelector getTableSelector(GeneratorConfig generatorConfig) {
		return new SqlServerTableSelector(new SqlServerColumnSelector(generatorConfig), generatorConfig);
	}

}
