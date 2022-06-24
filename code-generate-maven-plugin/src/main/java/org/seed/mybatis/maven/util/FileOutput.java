package org.seed.mybatis.maven.util;

import org.seed.mybatis.maven.fileout.CodeFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * 文件输出
 */
public class FileOutput {


    /**
     * 创建文件
     *
     * @param codeFiles 文件属性和内容
     * @throws IOException
     */
    public static void create(List<CodeFile> codeFiles) throws IOException {
        for (CodeFile codeFile : codeFiles) {
            File folder = new File(codeFile.getFolder());
            if (!folder.exists()) {
                try {
                    folder.mkdirs();
                } catch (Exception e) {
                }
            }
            File targetFile = new File(codeFile.getFolder() + codeFile.getFileName());
            targetFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(targetFile);
            fos.write(codeFile.getContent().getBytes());
            fos.close();
        }
    }


}
