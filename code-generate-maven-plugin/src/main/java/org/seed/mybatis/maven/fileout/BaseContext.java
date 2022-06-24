package org.seed.mybatis.maven.fileout;

public abstract class BaseContext {

    /**
     * 作者名
     */
    private String author;


    /**
     * 包名
     */
    private String packageName;

    /**
     * 根路径
     */
    private String absoluteRoot;

    /**
     * 服务名
     */
    private String serviceName;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAbsoluteRoot() {
        return absoluteRoot;
    }

    public void setAbsoluteRoot(String absoluteRoot) {
        this.absoluteRoot = absoluteRoot;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
