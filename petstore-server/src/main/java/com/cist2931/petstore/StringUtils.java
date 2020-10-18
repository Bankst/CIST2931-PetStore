package com.cist2931.petstore;

public final class StringUtils {
    public static boolean hasValue(String string) {
        return string != null && !string.isEmpty();
    }

    public static boolean hasValue(String... strings) {
        boolean has = true;
        for (String string : strings) {
            if (!(string != null && !string.isEmpty())) {
                has = false;
                break;
            }
        }

        return has;
    }
}
