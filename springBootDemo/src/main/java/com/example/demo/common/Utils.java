package com.example.demo.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * 打印当前线程堆栈信息
     *
     * @param prefix
     */
    public static void printTrack(String prefix) {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();

        if (null == st) {
            logger.info("invalid stack");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (StackTraceElement e : st) {
            if (sb.length() > 0) {
                sb.append(" <- ");
                sb.append(System.getProperty("line.separator"));
            }

            sb.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));
        }

        logger.info(prefix
                + "\n************************************************************\n"
                + sb.toString()
                + "\n************************************************************");
    }
}