package com.br.migrationTool.builders;

import java.util.ArrayList;
import java.util.List;

public class PrimaryKeysBuilder {
    private static List<String> primaryKeys;

    public static PrimaryKeysBuilder onePrimaryKeysBuilder() {
        PrimaryKeysBuilder builder = new PrimaryKeysBuilder();
        primaryKeys = new ArrayList<>();
        primaryKeys.add("1");
        primaryKeys.add("5");
        primaryKeys.add("3");
        primaryKeys.add("2");
        primaryKeys.add("5");
        primaryKeys.add("5");
        primaryKeys.add("22");
        primaryKeys.add("10");
        primaryKeys.add("4");
        primaryKeys.add("1");

        return builder;
    }

    public List<String> builder() {
        return primaryKeys;
    }
}
