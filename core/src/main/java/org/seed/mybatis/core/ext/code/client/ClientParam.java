package org.seed.mybatis.core.ext.code.client;


import org.seed.mybatis.core.SeedMybatisConfig;
import org.seed.mybatis.core.util.ClassUtil;

/**
 * 客户端参数
 */
public class ClientParam {

	private Class<?> mapperClass;
	/** 模板资源 */
	private String templateContent;
	private String globalVmLocation;
	private SeedMybatisConfig config;

	public Class<?> getEntityClass() {
		if (mapperClass.isInterface()) {
			return ClassUtil.getSuperInterfaceGenericType(mapperClass, 0);
		} else {
			return ClassUtil.getSuperClassGenricType(mapperClass, 0);
		}
	}
	
	public String getGlobalVmPlaceholder() {
		return this.config.getGlobalVmPlaceholder();
	}

	public String getGlobalVmLocation() {
		return globalVmLocation;
	}

	public void setGlobalVmLocation(String globalVmLocation) {
		this.globalVmLocation = globalVmLocation;
	}

	public Class<?> getMapperClass() {
		return mapperClass;
	}

	public void setMapperClass(Class<?> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public SeedMybatisConfig getConfig() {
		return config;
	}

	public void setConfig(SeedMybatisConfig config) {
		this.config = config;
	}

	public String getCountExpression() {
		return this.config.getCountExpression();
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}


}
