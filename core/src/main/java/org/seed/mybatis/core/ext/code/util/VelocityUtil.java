package org.seed.mybatis.core.ext.code.util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.seed.mybatis.core.util.IOUtil;

import java.io.*;

/**
 * Velocity工具类,根据模板内容生成文件
 */
public class VelocityUtil {

	private VelocityUtil() {
		super();
	}

	static {
		Velocity.init();
	}

	private static String TAG = "seed-mybatis";
	private static String UTF8 = "UTF-8";

	public static String generate(VelocityContext context, InputStream inputStream) {
		try {
			return generate(context, inputStream, UTF8);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String generate(VelocityContext context, InputStream inputStream, String charset) throws UnsupportedEncodingException {
		return generate(context, new InputStreamReader(inputStream, charset));
	}

	public static String generate(VelocityContext context, Reader reader) {
		StringWriter writer = new StringWriter();
		// 不用vm文件
		Velocity.evaluate(context, writer, TAG, reader);

		IOUtil.closeQuietly(writer, reader);

		return writer.toString();

	}

	public static String generate(VelocityContext context, String template) {
		return generate(context, new StringReader(template));
	}
}
