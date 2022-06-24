package org.seed.mybatis.maven.dbparser;


import org.seed.mybatis.maven.GeneratorConfig;

public interface SQLService {

	TableSelector getTableSelector(GeneratorConfig generatorConfig);

}
