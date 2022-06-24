package org.seed.mybatis.maven.adapters.oracle;


import org.seed.mybatis.maven.GeneratorConfig;
import org.seed.mybatis.maven.dbparser.SQLService;
import org.seed.mybatis.maven.dbparser.TableSelector;

public class OracleService implements SQLService {

	@Override
	public TableSelector getTableSelector(GeneratorConfig generatorConfig) {
		return new OracleTableSelector(new OracleColumnSelector(generatorConfig), generatorConfig);
	}

}
