package com.cist2931.petstore.logging;

public enum LogLevel {
    OFF(0, Logger.ANSI_BLACK),
    ERROR(1, Logger.ANSI_RED),
    WARN(2, Logger.ANSI_YELLOW),
    INFO(3, Logger.ANSI_GREEN),
    DEBUG(4, Logger.ANSI_WHITE),
    TRACE(5, Logger.ANSI_CYAN);

    public final String colorCode;
    public final int code;

    LogLevel(int code, String colorCode) {
        this.code = code;
        this.colorCode = colorCode;
    }
}
