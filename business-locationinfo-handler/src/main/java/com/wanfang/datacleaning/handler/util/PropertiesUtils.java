package com.wanfang.datacleaning.handler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 *    
 *  @Description
 *  @Author   luqs   
 *  @Date 2018/8/15 15:11 
 *  @Version  V1.0   
 */
public class PropertiesUtils {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    private static Properties prop = new Properties();
    private static final String DEFAULT_PROPERTIES_FILE_PATH = "routineConfig.properties";

    static {
        try {
            prop.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE_PATH));
        } catch (Exception e) {
            logger.error("加载【{}】出现异常：", DEFAULT_PROPERTIES_FILE_PATH, e);
        }
    }

    private PropertiesUtils() {
    }

    /**
     * 获取默认properties文件中指定key的值
     *
     * @param key properties文件中的key
     * @return String 若key不存在，则返回null
     */
    public static String getValue(String key) {
        String propertyValue = null;

        try {
            propertyValue = getValue(prop, DEFAULT_PROPERTIES_FILE_PATH, key);
        } catch (IOException ioe) {
            logger.error("加载【{}】出现异常：", DEFAULT_PROPERTIES_FILE_PATH, ioe);
        }

        return propertyValue;
    }

    /**
     * 获取默认properties文件中指定key的值
     *
     * @param key properties文件中的key
     * @return String 若key不存在，则返回defaultValue
     */
    public static String getValue(String key, String defaultValue) {
        String propertyValue = defaultValue;

        try {
            propertyValue = getValue(prop, DEFAULT_PROPERTIES_FILE_PATH, key);

            if (propertyValue == null || "".equals(propertyValue)) {
                propertyValue = defaultValue;
            }
        } catch (IOException ioe) {
            logger.error("加载【{}】出现异常：", DEFAULT_PROPERTIES_FILE_PATH, ioe);
        }

        return propertyValue;
    }

    /**
     * 获取指定properties文件中指定key的值
     *
     * @param resourceFilePath classpath下的文件路径
     * @param key              properties文件中的key
     * @return String 若指定properties文件或指定key不存在，则返回null
     */
    public static String getValueBySpecifiedFile(String resourceFilePath, String key) {
        String propertyValue = null;

        try {
            propertyValue = getValueBySpecFile(resourceFilePath, key);
        } catch (IOException ioe) {
            logger.error("文件：【{}】，key：【{}】，getValueBySpecifiedFile()出现异常：", key, resourceFilePath, ioe);
        }

        return propertyValue;
    }

    /**
     * 获取指定properties文件中指定key的值
     *
     * @param resourceFilePath classpath下的文件路径
     * @param key              properties文件中的key
     * @return String 若指定properties文件或指定key不存在，则返回defaultValue
     */
    public static String getValueBySpecifiedFile(String resourceFilePath, String key, String defaultValue) {
        String propertyValue = null;

        try {
            propertyValue = getValueBySpecFile(resourceFilePath, key);
        } catch (IOException ioe) {
            logger.error("文件：【{}】，key：【{}】，getValueBySpecifiedFile()出现异常：", key, resourceFilePath, ioe);
        }

        if (propertyValue == null || "".equals(propertyValue)) {
            propertyValue = defaultValue;
        }

        return propertyValue;
    }

    /**
     * 获取指定properties文件中指定key的值
     *
     * @param prop
     * @param resourceFilePath classpath下的文件路径
     * @param key              properties文件中的key
     * @return String
     * @throws IOException
     */
    private static String getValue(Properties prop, String resourceFilePath, String key) throws IOException {
        if (prop == null || prop.isEmpty()) {
            prop = new Properties();
            prop.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(resourceFilePath));
        }

        return prop.getProperty(key);
    }

    /**
     * 获取指定properties文件中指定key的值
     *
     * @param resourceFilePath classpath下的文件路径
     * @param key              properties文件中的key
     * @return String
     * @throws IOException
     */
    private static String getValueBySpecFile(String resourceFilePath, String key) throws IOException {
        Properties prop = new Properties();

        prop.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(resourceFilePath));

        return prop.getProperty(key);
    }
}
