package org.seed.mybatis.core.util;

import org.apache.ibatis.io.Resources;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 */
public class IOUtil {

    private static final int EOF = -1;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static final String UTF_8 = "UTF-8";

    /**
     * 字符串转成流
     *
     * @param input    内容
     * @param encoding 编码
     * @return 返回inputStream流
     */
    public static InputStream toInputStream(String input, Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(encoding));
    }

    /**
     * 复制流，将input复制到output
     *
     * @param input  输入
     * @param output 输出
     * @return
     * @throws IOException
     */
    public static int copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * 将流转成字符串
     *
     * @param input    流
     * @param encoding 编码
     * @return 返回字符串
     * @throws IOException
     */
    public static String toString(InputStream input, Charset encoding) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static void copy(InputStream input, Writer output, Charset encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }

    /**
     * 逐行读取流，返回每行数据
     *
     * @param input    流
     * @param encoding 编码
     * @return 返回每行数据
     * @throws IOException
     */
    public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, encoding);
        return readLines(reader);
    }

    /**
     * 逐行读取流，返回每行数据
     *
     * @param input 流
     * @return 返回每行数据
     * @throws IOException
     */
    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable == null) {
                continue;
            }
            try {
                closeable.close();
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    public static List<ResourceFile> listFiles(File file, String extension) throws IOException {
        List<ResourceFile> files = new ArrayList<>();
        String filename = file.getName();
        if (file.isFile() && filename.toLowerCase().endsWith(extension)) {
            String content = toString(file, StandardCharsets.UTF_8);
            files.add(new ResourceFile(filename, content));
            return files;
        } else if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File subFile : subFiles) {
                    List<ResourceFile> fileList = listFiles(subFile, extension);
                    files.addAll(fileList);
                }
            }
        }
        return files;
    }

    public static String toString(final File file, final Charset encoding) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return IOUtil.toString(in, encoding);
        }
    }

    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    /**
     * 获取jar中的文件
     *
     * @param classpath path路径
     * @param extension 后缀
     * @return 返回文件内容
     * @throws IOException
     */
    public static List<ResourceFile> listJarFiles(String classpath, String extension) throws IOException {
        URL resourceURL = null;
        try {
            resourceURL = Resources.getResourceURL(classpath);
        } catch (IOException e) {
            return Collections.emptyList();
        }
        URLConnection urlConnection = resourceURL.openConnection();
        List<ResourceFile> list = new ArrayList<>();
        if (urlConnection instanceof JarURLConnection) {
            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (!(!jarEntry.isDirectory() && name.startsWith(classpath) && name.endsWith(extension))) {
                    continue;
                }
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                String content = IOUtil.toString(inputStream, StandardCharsets.UTF_8);
                list.add(new ResourceFile(name, content));
            }
        } else if (urlConnection instanceof FileURLConnection) {
            String filePath = URLDecoder.decode(resourceURL.getPath(), UTF_8);
            File folder = new File(filePath);
            List<ResourceFile> resourceFiles = listFiles(folder, extension);
            list.addAll(resourceFiles);
        }
        return list;
    }

    public static class ResourceFile {
        private String filename;

        private String content;

        public ResourceFile(String filename, String content) {
            this.filename = filename;
            this.content = content;
        }

        public InputStream getInputStream() {
            return toInputStream(content, StandardCharsets.UTF_8);
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "ResourceFile{" +
                    "filename='" + filename + '\'' +
                    '}';
        }
    }

    public static class StringBuilderWriter extends Writer implements Serializable {

        private static final long serialVersionUID = -146927496096066153L;

        private final StringBuilder builder;

        /**
         * Constructs a new {@link StringBuilder} instance with default capacity.
         */
        public StringBuilderWriter() {
            this.builder = new StringBuilder();
        }

        /**
         * Constructs a new {@link StringBuilder} instance with the specified capacity.
         *
         * @param capacity The initial capacity of the underlying {@link StringBuilder}
         */
        public StringBuilderWriter(final int capacity) {
            this.builder = new StringBuilder(capacity);
        }

        /**
         * Constructs a new instance with the specified {@link StringBuilder}.
         *
         * <p>If {@code builder} is null a new instance with default capacity will be created.</p>
         *
         * @param builder The String builder. May be null.
         */
        public StringBuilderWriter(final StringBuilder builder) {
            this.builder = builder != null ? builder : new StringBuilder();
        }

        /**
         * Appends a single character to this Writer.
         *
         * @param value The character to append
         * @return This writer instance
         */
        @Override
        public Writer append(final char value) {
            builder.append(value);
            return this;
        }

        /**
         * Appends a character sequence to this Writer.
         *
         * @param value The character to append
         * @return This writer instance
         */
        @Override
        public Writer append(final CharSequence value) {
            builder.append(value);
            return this;
        }

        /**
         * Appends a portion of a character sequence to the {@link StringBuilder}.
         *
         * @param value The character to append
         * @param start The index of the first character
         * @param end   The index of the last character + 1
         * @return This writer instance
         */
        @Override
        public Writer append(final CharSequence value, final int start, final int end) {
            builder.append(value, start, end);
            return this;
        }

        /**
         * Closing this writer has no effect.
         */
        @Override
        public void close() {
            // no-op
        }

        /**
         * Flushing this writer has no effect.
         */
        @Override
        public void flush() {
            // no-op
        }


        /**
         * Writes a String to the {@link StringBuilder}.
         *
         * @param value The value to write
         */
        @Override
        public void write(final String value) {
            if (value != null) {
                builder.append(value);
            }
        }

        /**
         * Writes a portion of a character array to the {@link StringBuilder}.
         *
         * @param value  The value to write
         * @param offset The index of the first character
         * @param length The number of characters to write
         */
        @Override
        public void write(final char[] value, final int offset, final int length) {
            if (value != null) {
                builder.append(value, offset, length);
            }
        }

        /**
         * Returns the underlying builder.
         *
         * @return The underlying builder
         */
        public StringBuilder getBuilder() {
            return builder;
        }

        /**
         * Returns {@link StringBuilder#toString()}.
         *
         * @return The contents of the String builder.
         */
        @Override
        public String toString() {
            return builder.toString();
        }
    }


}
