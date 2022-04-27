package com.br.migrationTool.utils;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

public class StringUtils {
    public static String arrangeStringSeparatedByComma(List<String> listItems) {
        return String.join(", ", listItems);
    }

    public static String blobToString(Blob blob) throws SQLException {

        if (blob == null) {
            return null;
        }

        byte[] blobBytes = blob.getBytes(1, (int)blob.length());

        StringBuilder hex = new StringBuilder();
        for (byte i : blobBytes) {
            hex.append(String.format("%02X", i));
        }
        return hex.toString();
    }
}
