package com.br.migrationTool.utils;

import java.util.List;

public class StringUtils {
    public static String arrangeStringSeparatedByComma(List<String> listItems) {
        return String.join(", ", listItems);
    }
}
