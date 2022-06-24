package org.seed.mybatis.maven.adapters.mysql;


import org.seed.mybatis.maven.GeneratorConfig;
import org.seed.mybatis.maven.dbparser.SQLService;
import org.seed.mybatis.maven.dbparser.TableSelector;

public class MySqlService implements SQLService {

	@Override
	public TableSelector getTableSelector(GeneratorConfig generatorConfig) {
		return new MySqlTableSelector(new MySqlColumnSelector(generatorConfig), generatorConfig);
	}

}
