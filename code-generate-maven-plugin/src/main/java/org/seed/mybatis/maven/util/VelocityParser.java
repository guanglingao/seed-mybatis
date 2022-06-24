package org.seed.mybatis.maven.util;

import org.apache.velocity.VelocityContext;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 根据velocity模板输出完整内容
 */
public class VelocityParser {


    static {
        org.apache.velocity.app.Velocity.init();
    }

    public static String output(VelocityContext context, String template) {
        StringReader reader = new StringReader(template);
        StringWriter writer = new StringWriter();
        // 不用vm文件
        org.apache.velocity.app.Velocity.evaluate(context, writer, "seed", reader);

        try {
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}

