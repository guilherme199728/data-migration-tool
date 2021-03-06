package com.br.migrationTool.utils;

import com.br.migrationTool.constraints.FieldTypesConstraint;
import com.br.migrationTool.dtos.migration.TableDataDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlUtils {

    public static String transformDataToSqlField(TableDataDto tableDataDto) {

        switch (tableDataDto.getFiledType()) {
            case FieldTypesConstraint.VARCHAR_2, FieldTypesConstraint.CHAR -> {
                if (tableDataDto.getFiledData() == null) {
                    return "''";
                }
                return "'" + tableDataDto.getFiledData() + "'";
            }
            case FieldTypesConstraint.NUMBER -> {
                if (tableDataDto.getFiledData() == null) {
                    return null;
                }
                return tableDataDto.getFiledData();
            }
            case FieldTypesConstraint.DATE -> {
                if (tableDataDto.getFiledData() == null) {
                    return null;
                }
                return "TIMESTAMP '" + tableDataDto.getFiledData() + "'";
            }
            case FieldTypesConstraint.FLOAT -> {
                if (tableDataDto.getFiledData() != null) {
                    return null;
                }
                return Float.valueOf(tableDataDto.getFiledData()).toString();
            }
            case FieldTypesConstraint.BLOB -> {
                if (tableDataDto.getFiledData() == null) {
                    return null;
                }
                return "'" + tableDataDto.getFiledData() + "'";
            }
        }

        return null;
    }

    public static String arrangeStringInsertSqlSeparatedByCommaAndInsideParenthesesByListString(List<String> listString) {
        return "(" + StringUtils.arrangeStringSeparatedByComma(listString) + ")";
    }

    public static String arrangeStringUpdateSqlSeparatedByCommaByListTableDataDto(List<TableDataDto> tableDataDtos) {

        List<String> TablesData = new ArrayList<>();

        for (TableDataDto tableDataDto : tableDataDtos) {
            TablesData.add(tableDataDto.getFieldName() + " = " + SqlUtils.transformDataToSqlField(tableDataDto));
        }

        return StringUtils.arrangeStringSeparatedByComma(TablesData);
    }

    public static String getStringSqlInsertData(String tableName, List<TableDataDto> tableDataDtos) {

        StringBuilder sqlInsert = new StringBuilder();

        String tableFields = SqlUtils.arrangeStringInsertSqlSeparatedByCommaAndInsideParenthesesByListString(
            tableDataDtos
                .stream()
                .map(TableDataDto::getFieldName)
                .collect(Collectors.toList())
        );

        String tableValues = SqlUtils.arrangeStringInsertSqlSeparatedByCommaAndInsideParenthesesByListString(
            tableDataDtos
                .stream()
                .map(SqlUtils::transformDataToSqlField)
                .collect(Collectors.toList())
        );

        return sqlInsert
            .append("INSERT INTO ")
            .append(tableName)
            .append(" ")
            .append(tableFields)
            .append(" VALUES ")
            .append(tableValues)
            .toString();
    }

    public static String getStringSqlUpdateData(String tableName, String primaryKeyName, List<TableDataDto> tableDataDtos) {

        StringBuilder sqlUpdate = new StringBuilder();
        String tableFields = SqlUtils.arrangeStringUpdateSqlSeparatedByCommaByListTableDataDto(tableDataDtos);

        return sqlUpdate
            .append("UPDATE ")
            .append(tableName)
            .append(" SET ")
            .append(tableFields)
            .append(" WHERE ")
            .append(primaryKeyName)
            .append(" = ")
            .append(getPkValueByPkName(primaryKeyName, tableDataDtos))
            .toString();
    }

    public static String getPkValueByPkName(String primaryKeyName, List<TableDataDto> allTableDataDto) {

        for (TableDataDto tableDataDto : allTableDataDto) {

            if (tableDataDto.getFieldName().equals(primaryKeyName)) {
                return transformDataToSqlField(tableDataDto);
            }
        }

        return null;
    }

    public static String getPrimaryKeysConcatenatedByOffSet(String primaryKeyName, List<String> primaryKeys) {

        List<String> listPrimaryKeysByOffSet = getListPrimaryKeysSeparatedByBarByOffset(primaryKeys);

        int index = 1;
        StringBuilder offSetQueryConcat = new StringBuilder();
        for (String primaryKeyByOffSet : listPrimaryKeysByOffSet) {
            if (index == 1) {
                offSetQueryConcat.append("(").append(primaryKeyByOffSet).append(")");
            } else {
                offSetQueryConcat.append(" OR ").append(primaryKeyName).append(" IN (").append(primaryKeyByOffSet).append(")");
            }

            index++;
        }

        return offSetQueryConcat.toString();
    }

    private static List<String> getListPrimaryKeysSeparatedByBarByOffset(List<String> primaryKeys) {

        int offSet = 950;
        StringBuilder primaryKeysConcat = new StringBuilder();
        int nextOffSet = offSet;
        int index = 1;

        for (String primaryKey : primaryKeys) {
            primaryKeysConcat.append("'").append(primaryKey).append("'");
            if (nextOffSet == index) {
                primaryKeysConcat.append("/");
                nextOffSet = nextOffSet + offSet;
            } else if (index != primaryKeys.size()) {
                primaryKeysConcat.append(",");
            }

            index++;
        }

        return List.of(primaryKeysConcat.toString().split("/"));
    }

}
