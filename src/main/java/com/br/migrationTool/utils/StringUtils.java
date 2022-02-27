package com.br.migrationTool.utils;

import com.br.migrationTool.FieldTypesConstraint;
import com.br.migrationTool.dto.TableDataDto;

import java.util.List;

public class StringUtils {
    public static String arrangeStringSeparatedByCommaAndInsideParenthesesByListString(List<String> listString) {

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

    public static String arrangeStringSeparatedByCommaAndInsideParenthesesByListTableDataDto(List<TableDataDto> allTableDataDto) {

        StringBuilder allString = new StringBuilder();
        int index = 1;

        for (TableDataDto tableDataDto : allTableDataDto) {
            if(allTableDataDto.size() == index) {
                allString.append(transformDataToSqlField(tableDataDto));
            } else {
                allString.append(transformDataToSqlField(tableDataDto)).append(", ");
            }

            index++;
        }

        return "(" + allString + ")";
    }

    public static String transformDataToSqlField(TableDataDto tableDataDto) {

        switch (tableDataDto.getFiledType()){
            case FieldTypesConstraint.VARCHAR_2:
                return "'" + tableDataDto.getFiledData() + "'";
            case FieldTypesConstraint.NUMBER:
                return tableDataDto.getFiledData();
            case FieldTypesConstraint.DATE:
                if (tableDataDto.getFiledData() != null) {
                    return "TIMESTAMP " + "'" + tableDataDto.getFiledData() + "'";
                }
            case FieldTypesConstraint.FLOAT:
                if (tableDataDto.getFiledData() != null) {
                    return Float.valueOf(tableDataDto.getFiledData()).toString();
                }
        }

        return null;
    }
}
