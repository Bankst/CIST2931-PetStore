package com.cist2931.petstore.logging;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused")
public class Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String className;

    public Logger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }

    public Logger(Class<?> clazz, String suffix) {
        this.className = clazz.getSimpleName() + " - " + suffix;
    }

    public static String getDate() {
        return simpleDateFormat.format(new Date());
    }

    public static String format(
            String logMessage, LogLevel level, String clazz, boolean color) {
        String date = getDate();
        StringBuilder builder = new StringBuilder();
        if (color) builder.append(level.colorCode);
        builder
                .append("[")
                .append(date)
                .append("] [")
                .append(clazz)
                .append("] [")
                .append(level.name())
                .append("] ")
                .append(logMessage);
        if (color) builder.append(ANSI_RESET);
        return builder.toString();
    }

    private static LogLevel logLevel = LogLevel.DEBUG;
    private static final List<LogAppender> logAppenders = new ArrayList<>();

    public static void setLogLevel(LogLevel logLevel) {
        Logger.logLevel = logLevel;
    }

    static {
        logAppenders.add(new ConsoleLogAppender());
        // TODO: get working directory
        // addFileAppender();
    }

    public static void addFileAppender(Path logFilePath) {
        File file = logFilePath.toFile();
        if (!file.exists()) {
            try {
                if (!file.getParentFile().mkdirs()) { throw new IOException("Failed to create directories"); }
                if (!file.createNewFile()) { throw new IOException("Failed to create log file"); }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logAppenders.add(new FileLogAppender(logFilePath));
    }

    private static void log(String message, LogLevel level, String clazz) {
        for (LogAppender a : logAppenders) {
            boolean shouldColor = a instanceof ConsoleLogAppender;
            String formattedMessage = format(message, level, clazz, shouldColor);
            a.log(formattedMessage, level);
        }
    }

    public boolean shouldLog(LogLevel logLevel) {
        return logLevel.code <= Logger.logLevel.code;
    }

    private void log(String message, LogLevel level) {
        if (shouldLog(level)) {
            log(message, level, className);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void log(String message, LogLevel messageLevel, LogLevel conditionalLevel) {
        if (shouldLog(conditionalLevel)) {
            log(message, messageLevel, className);
        }
    }

    /**
     * Logs an error message
     * @param message Error message to print
     */
    public void error(String message) {
        log(message, LogLevel.ERROR);
    }

    /**
     * Logs an error message with the stack trace of a Throwable. The stacktrace will only be printed
     * if the current LogLevel is DEBUG
     *
     * @param message Message about error to print
     * @param throwable Thrown {@link Throwable} or {@link Exception} to print stack trace of
     */
    public void error(String message, Throwable throwable) {
        log(message, LogLevel.ERROR);
        log(convertStackTraceToString(throwable), LogLevel.ERROR, LogLevel.DEBUG);
    }

    /**
     * Logs a warning message
     * @param message Warning message to print
     */
    public void warn(String message) {
        log(message, LogLevel.WARN);
    }

    /**
     * Logs an info message
     * @param message Info message to print
     */
    public void info(String message) {
        log(message, LogLevel.INFO);
    }

    /**
     * Logs a debug message
     * @param message Debug message to print
     */
    public void debug(String message) {
        log(message, LogLevel.DEBUG);
    }

    /**
     * Logs a trace message
     * @param message Trace message to print
     */
    public void trace(String message) {
        log(message, LogLevel.TRACE);
    }

    /**
     * Converts a {@link Throwable} or {@link Exception}'s StackTrace to a string
     * @param throwable Thrown {@link Throwable} or {@link Exception} to convert to string
     * @return StackTrace as a string
     */
    private static String convertStackTraceToString(Throwable throwable) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    private interface LogAppender {
        void log(String message, LogLevel level);
    }

    private static class ConsoleLogAppender implements LogAppender {
        @Override
        public void log(String message, LogLevel level) {
            if (level == LogLevel.ERROR) {
                System.err.println(message);
            } else {
                System.out.println(message);
            }
        }
    }

    private static class FileLogAppender implements LogAppender {
        private OutputStream out;

        public FileLogAppender(Path logFilePath) {
            try {
                out = new FileOutputStream(logFilePath.toFile());
                out.flush();
            } catch (FileNotFoundException e) {
                out = null;
                System.err.println("Unable to log to file " + logFilePath.toString());
            } catch (IOException e) {
                System.err.println("Unable to log to file " + logFilePath.toString());
            }
        }

        @Override
        public void log(String message, LogLevel level) {
            message += "\n";
            try {
                out.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
