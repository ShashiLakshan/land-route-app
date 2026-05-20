package com.lra.landroute.util;

import java.util.Locale;
import java.util.Objects;

public class StringUtil {

    public static String normalize(String value) {
        return Objects.requireNonNullElse(value, "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}
