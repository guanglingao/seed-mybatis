package org.seed.mybatis.maven.fileout;

import java.io.Serializable;


/**
 * 带输出的文件对象数据
 */
public class CodeFile implements Serializable {

    private String folder;
    private String fileName;
    private String content;


    public CodeFile(){}

    public CodeFile(String folder,String fileName,String content){
        this.folder = folder;
        this.fileName = fileName;
        this.content = content;
    }


    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
