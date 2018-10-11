package com.wanfang.datacleaning.util;

import org.slf4j.Logger;

/**
 *    
 *  @Description 日志工具类
 *  @Author   luqs   
 *  @Date 2018/4/2 18:50 
 *  @Version  V1.0   
 */
public class LoggerUtils {

    private LoggerUtils() {
    }

    /**
     * 添加TRACE级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，若文本中含拼接内容，请调用appendTraceLog(Logger logger, String logText, Object... objects)
     */
    public static void appendTraceLog(Logger logger, String logText) {
        if (logger.isTraceEnabled()) {
            logger.trace(logText);
        }
    }

    /**
     * 添加TRACE级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，采用占位符“{}”形式，如：appendTraceLog(logger,"添加{}级别{}","TRACE","日志"）
     * @param objects
     */
    public static void appendTraceLog(Logger logger, String logText, Object... objects) {
        if (logger.isTraceEnabled()) {
            logger.trace(logText, objects);
        }
    }

    /**
     * 添加DEBUG级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，若文本中含拼接内容，请调用appendDebugLog(Logger logger, String logText, Object... objects)
     */
    public static void appendDebugLog(Logger logger, String logText) {
        if (logger.isDebugEnabled()) {
            logger.debug(logText);
        }
    }

    /**
     * 添加DEBUG级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，采用占位符“{}”形式，如：appendDebugLog(logger,"添加{}级别{}","DEBUG","日志")
     * @param objects
     */
    public static void appendDebugLog(Logger logger, String logText, Object... objects) {
        if (logger.isDebugEnabled()) {
            logger.debug(logText, objects);
        }
    }

    /**
     * 添加INFO级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，若文本中含拼接内容，请调用appendInfoLog(Logger logger, String logText, Object... objects)
     */
    public static void appendInfoLog(Logger logger, String logText) {
        if (logger.isInfoEnabled()) {
            logger.info(logText);
        }
    }

    /**
     * 添加INFO级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，采用占位符“{}”形式，如：appendInfoLog(logger,"添加{}级别{}","INFO","日志")
     * @param objects
     */
    public static void appendInfoLog(Logger logger, String logText, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(logText, objects);
        }
    }

    /**
     * 添加WARN级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，若文本中含拼接内容，请调用appendWarnLog(Logger logger, String logText, Object... objects)
     */
    public static void appendWarnLog(Logger logger, String logText) {
        if (logger.isWarnEnabled()) {
            logger.warn(logText);
        }
    }

    /**
     * 添加WARN级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，采用占位符“{}”形式，如：appendWarnLog(logger,"添加{}级别{}","WARN","日志")
     * @param objects
     */
    public static void appendWarnLog(Logger logger, String logText, Object... objects) {
        if (logger.isWarnEnabled()) {
            logger.warn(logText, objects);
        }
    }

    /**
     * 添加ERROR级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，若文本中含拼接内容，请调用appendErrorLog(Logger logger, String logText, Object... objects)
     */
    public static void appendErrorLog(Logger logger, String logText) {
        if (logger.isErrorEnabled()) {
            logger.error(logText);
        }
    }

    /**
     * 添加ERROR级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，采用占位符“{}”形式，如：appendErrorLog(logger,"添加{}级别{}","ERROR","日志")
     * @param objects
     */
    public static void appendErrorLog(Logger logger, String logText, Object... objects) {
        if (logger.isErrorEnabled()) {
            logger.error(logText, objects);
        }
    }

    /**
     * 添加ERROR级别日志
     *
     * @param logger  logger
     * @param logText 日志文本，采用占位符“{}”形式，如：appendErrorLog(logger,"添加{}级别{}","ERROR","日志")
     * @param e
     */
    public static void appendErrorLog(Logger logger, String logText, Throwable e) {
        if (logger.isErrorEnabled()) {
            logger.error(logText, e);
        }
    }
}
