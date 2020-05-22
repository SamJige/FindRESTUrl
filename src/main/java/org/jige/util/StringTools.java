package org.jige.util;

import org.apache.commons.lang3.StringUtils;

public class StringTools {
    public static void log(Object... strs) {
        System.out.println(StringUtils.join(strs, ""));
    }
}
