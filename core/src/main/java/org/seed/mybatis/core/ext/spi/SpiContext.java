package org.seed.mybatis.core.ext.spi;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.seed.mybatis.core.ext.code.util.FieldUtil;
import org.seed.mybatis.core.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI容器<br>
 * <pre>
 * 默认加载<code>META-INF/seedmybatis.factories.default</code>
 * 用户可以自定义<code>META-INF/seedmybatis.factories</code>用来覆盖<code>META-INF/seedmybatis.factories.default</code>中的配置
 *
 * <code>seedmybatis.factories.default</code>文件内容格式：{@literal <key>=<value>}形式。
 * key：固定不变，接口名字且首字母小写
 * value：实现类的全限定名
 * </pre>
 */
@Slf4j
public class SpiContext {


    private static final String DEFAULT_FILE_NAME = "META-INF/seedmybatis.factories.default";
    private static final String EXT_FILE_NAME = "META-INF/seedmybatis.factories";

    private final static Map<String, Object> instanceMap = new ConcurrentHashMap<>(8);

    private static volatile boolean init = false;

    private static final Object LOCK = new Object();

    public static ClassSearch getClassSearch() {
        return getService(SpiKey.CLASS_SEARCH);
    }

    public static BeanExecutor getBeanExecutor() {
        return getService(SpiKey.BEAN_EXECUTOR);
    }

    public static MapperBuilder getMapperBuilder() {
        return getService(SpiKey.MAPPER_BUILDER);
    }

    private static <T> T getService(SpiKey spiKey) {
        return getService(spiKey.getKey());
    }

    public static <T> T getService(String beanName) {
        doInitialize();
        return (T) instanceMap.get(beanName);
    }

    private static void doInitialize() {
        if (!init) {
            synchronized (LOCK) {
                if (!init) {
                    // 初始化默认spi
                    initialize(DEFAULT_FILE_NAME);
                    // 初始化扩展spi
                    initialize(EXT_FILE_NAME);
                    init = true;
                }
            }
        }
    }

    private static void initialize(String filename) {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(filename);
        } catch (IOException e) {
            // no file
            return;
        }
        if (inputStream == null) {
            return;
        }
        try {
            List<String> lines = IOUtil.readLines(inputStream, StandardCharsets.UTF_8);
            for (String line : lines) {
                // 注释
                if (line.startsWith("#")) {
                    continue;
                }
                String[] split = line.split("=");
                if (split.length != 2) {
                    throw new RuntimeException("'seedmybatis.factories' config error, must be key=value pattern");
                }
                String key = split[0];
                String className = split[1];
                Class<?> aClass = Class.forName(className);
                Object instance = aClass.newInstance();
                instanceMap.put(key, instance);
            }
        } catch (Exception e) {
            log.error("read lines error", e);
            throw new RuntimeException("init spi error", e);
        }

    }

    public enum SpiKey {
        CLASS_SEARCH(ClassSearch.class),
        MAPPER_BUILDER(MapperBuilder.class),
        BEAN_EXECUTOR(BeanExecutor.class),
        ;

        private final String key;

        SpiKey(Class<?> clazz) {
            if (!clazz.isInterface()) {
                throw new IllegalArgumentException("clazz must an interface");
            }
            this.key = FieldUtil.lowerFirstLetter(clazz.getSimpleName());
        }

        public String getKey() {
            return key;
        }
    }

}
