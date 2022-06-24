package org.seed.mybatis.core.ext;


import org.apache.ibatis.io.Resources;
import org.seed.mybatis.core.SeedMybatisConstants;
import org.seed.mybatis.core.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class MyBatisResource {

    private String content;

    private String filename;

    private String filepath;

    private boolean merged;

    private boolean exist = true;

    public static MyBatisResource build(String content, Class<?> daoClass) {
        MyBatisResource myBatisResource = new MyBatisResource();
        myBatisResource.setContent(content);
        myBatisResource.setFilename(daoClass.getSimpleName() + SeedMybatisConstants.XML_SUFFIX);
        return myBatisResource;
    }

    /**
     * 构建MyBatisResource
     *
     * @param classpath classpath，aa/bb/Mapper.xml
     * @return 返回MyBatisResource对象
     * @throws IOException
     */
    public static MyBatisResource buildFromClasspath(String classpath) {
        String filename = getFilename(classpath);
        MyBatisResource myBatisResource = new MyBatisResource();
        myBatisResource.setFilename(filename);
        myBatisResource.setFilepath(classpath);
        try {
            if (classpath.startsWith("/")) {
                classpath = classpath.substring(1);
            }
            InputStream inputStream = Resources.getResourceAsStream(classpath);
            String content = IOUtil.toString(inputStream, StandardCharsets.UTF_8);
            myBatisResource.setContent(content);
        } catch (IOException e) {
            myBatisResource.exist = false;
        }
        return myBatisResource;
    }

    public static MyBatisResource buildFromFile(String filename, String content) {
        MyBatisResource myBatisResource = new MyBatisResource();
        myBatisResource.setFilename(filename);
        myBatisResource.setContent(content);
        return myBatisResource;
    }

    public String getContent() {
        return content;
    }

    private static String getFilename(String resource) {
        int start = resource.lastIndexOf('/') + 1;
        return resource.substring(start);
    }

    public InputStream getInputStream() {
        return IOUtil.toInputStream(content, StandardCharsets.UTF_8);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public boolean exists() {
        return exist;
    }

    public boolean isMerged() {
        return merged;
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    @Override
    public String toString() {
        return filepath == null ? filename : filepath;
    }
}
