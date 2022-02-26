package com.br.migrationTool.utils;

import java.util.List;

public class StringUtils {
    public static String arrangeStringSeparatedByCommaAndInsideParentheses(List<String> listString) {

        StringBuilder allString = new StringBuilder();
        int index = 1;

        for (String string : listString) {
            if(listString.size() == index) {
                allString.append(string);
            } else {
                allString.append(string).append(", ");
            }

            index++;
        }

        return "(" + allString + ")";
    }
}
