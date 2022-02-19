package com.br.migrationTool.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TableReferencesDao {

    public static List<String> getParentTables(String dataBaseA, String dataBaseB, String tableName, Connection connection) throws SQLException {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT C_PK.TABLE_NAME, B.COLUMN_NAME, A.COLUMN_NAME")
        .append("FROM ALL_CONS_COLUMNS A")
        .append("JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER")
        .append("AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME")
        .append("JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER")
        .append("AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME")
        .append("JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME")
        .append("AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME")
        .append("WHERE C.CONSTRAINT_TYPE = 'R'")
        .append("AND A.OWNER = '").append(dataBaseA).append("'")
        .append("AND B.OWNER = '").append(dataBaseB).append("'")
        .append("AND A.TABLE_NAME = '").append(tableName).append("'");

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()) {

        }

        return null;
    }
}
