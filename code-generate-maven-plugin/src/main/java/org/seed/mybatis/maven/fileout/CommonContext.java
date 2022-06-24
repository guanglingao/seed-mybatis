package org.seed.mybatis.maven.fileout;


/**
 * 通用文件生成
 */
public class CommonContext extends BaseContext{

    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
