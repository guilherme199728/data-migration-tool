package com.br.migrationTool.datas.daos;

import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.ChildrenTableDto;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.ParentTableDto;
import com.br.migrationTool.dtos.migration.TableStructureDto;
import com.br.migrationTool.utils.OwnerUtils;
import com.br.migrationTool.utils.StringUtils;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class TableReferencesDao {

    @Autowired
    ConnectionOracleJDBC connectionOracleJDBC;

    @Autowired
    OwnerUtils ownerUtils;

    public List<ParentTableDto> getParentTablesFromConstraint(
            String tableName, boolean isProd
    ) throws SQLException {

        String sql = "SELECT C_PK.TABLE_NAME AS TABLENAME, B.COLUMN_NAME AS PRIMARYKEYNAME, A.COLUMN_NAME AS FOREINGKEYNAME " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER " +
        "AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME " +
        "AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "WHERE C.CONSTRAINT_TYPE = 'R' " +
        "AND A.OWNER = ? " +
        "AND B.OWNER = ? " +
        "AND A.TABLE_NAME = ? " +
        "AND B.TABLE_NAME <> ? ";

        String owner = ownerUtils.getOwner(isProd);

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<ParentTableDto>> rsh = new BeanListHandler<>(ParentTableDto.class);
        Object [] params = new Object[]{owner, owner, tableName, tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public TableStructureDto getTableDtoFromConstraint(
            String tableName, boolean isProd
    ) throws SQLException {

        String sql =
            "SELECT a.column_name AS PRIMARYKEYNAME, a.table_name AS TABLENAME " +
            "FROM all_cons_columns a " +
            "JOIN all_constraints c ON a.owner = c.owner " +
            "AND a.constraint_name = c.constraint_name " +
            "WHERE c.owner = ? " +
            "AND c.constraint_type = 'P' " +
            "AND LOWER(c.table_name) = LOWER(?)";

        String owner = ownerUtils.getOwner(isProd);

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<TableStructureDto> rsh = new BeanHandler<>(TableStructureDto.class);
        Object [] params = new Object[]{owner, tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public List<ChildrenTableDto> getChildrenTablesFromConstraint(
            String tableName, boolean isProd
    ) throws SQLException {

        String sql = "SELECT C.TABLE_NAME AS TABLENAME, B.COLUMN_NAME AS PRIMARYKEYNAME, B2.COLUMN_NAME AS FOREINGKEYNAME " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.R_CONSTRAINT_NAME " +
        "JOIN ALL_CONSTRAINTS C_PK ON C.R_OWNER = C_PK.OWNER " +
        "AND C.R_CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "JOIN ALL_CONS_COLUMNS B ON B.TABLE_NAME = C_PK.TABLE_NAME " +
        "AND B.CONSTRAINT_NAME = C_PK.CONSTRAINT_NAME " +
        "JOIN ALL_CONS_COLUMNS B2 ON B2.TABLE_NAME = C.TABLE_NAME " +
        "AND B2.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "WHERE C.CONSTRAINT_TYPE = 'R' " +
        "AND A.OWNER = ? " +
        "AND B.OWNER = ? " +
        "AND A.TABLE_NAME = ? ";

        String owner = ownerUtils.getOwner(isProd);

        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<ChildrenTableDto>> rsh = new BeanListHandler<>(ChildrenTableDto.class);
        Object [] params = new Object[]{owner, owner, tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public String getPrimaryKeyNameFromConstraint(
            String tableName, boolean isProd
    ) throws SQLException {

        String sql = "SELECT A.COLUMN_NAME " +
        "FROM ALL_CONS_COLUMNS A " +
        "JOIN ALL_CONSTRAINTS C ON A.OWNER = C.OWNER " +
        "AND A.CONSTRAINT_NAME = C.CONSTRAINT_NAME " +
        "WHERE C.OWNER = ? " +
        "AND C.CONSTRAINT_TYPE = 'P' " +
        "AND C.TABLE_NAME = ? ";

        String owner = ownerUtils.getOwner(isProd);

        PreparedStatement ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sql);
        ps.setString(1, owner);
        ps.setString(2, tableName);
        ResultSet rs = ps.executeQuery();

        String primaryKeyNames = null;
        while (rs.next()) {
            primaryKeyNames = rs.getString("COLUMN_NAME");
        }

        return primaryKeyNames;
    }

    public List<String> getAllNamesColumnsTableFromTableName(
            String tableName, boolean isProd
    ) throws SQLException {

        String sql = "SELECT COLUMN_NAME " +
        "FROM USER_TAB_COLUMNS WHERE " +
        "TABLE_NAME = ? ";

        PreparedStatement ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sql);
        ps.setString(1, tableName);
        ResultSet rs = ps.executeQuery();

        List<String> allNamesColunsTable = new ArrayList<>();
        while (rs.next()) {
            allNamesColunsTable.add(rs.getString("COLUMN_NAME"));
        }

        return allNamesColunsTable;
    }

    public HashMap<String, String> getAllNamesAndTypeColumnsTableFromTableName(
            String tableName, boolean isProd
    ) throws SQLException {

        String sql = "SELECT COLUMN_NAME, DATA_TYPE " +
                "FROM USER_TAB_COLUMNS WHERE " +
                "TABLE_NAME = ? ";

        PreparedStatement ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sql);
        ps.setString(1, tableName);
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData md = rs.getMetaData();

        HashMap<String, String> allNamesColumnsTable = new HashMap<>();

        while (rs.next()) {
            allNamesColumnsTable.put(rs.getString(md.getColumnName(1)), rs.getString(md.getColumnName(2)));
        }

        return allNamesColumnsTable;
    }

    public List<String> getPrimaryKeysByParentTable(
            MigrationDto migrationDto, ParentTableDto parentTableDto, boolean isProd
    ) throws SQLException {

        int offSet = 950;

        String sql = "SELECT A.%s FROM %s.%s A " +
                "JOIN %s.%s B on " +
                "A.%s = B.%s WHERE A.%s IN " +
                getPrimaryKeysConcatenatedByOffSet(
                        parentTableDto.getForeingKeyName(),
                        migrationDto.getPrimaryKeys(),
                        offSet
                );

        String sqlBuilt = String.format(
                sql,
                parentTableDto.getForeingKeyName(),
                ownerUtils.getOwner(isProd),
                migrationDto.getTableName(),
                ownerUtils.getOwner(isProd),
                parentTableDto.getTableName(),
                parentTableDto.getForeingKeyName(),
                parentTableDto.getPrimaryKeyName(),
                migrationDto.getTableStructureDto().getPrimaryKeyName()

        );

        PreparedStatement ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sqlBuilt);
        ResultSet rs = ps.executeQuery();

        List<String> allPrimaryKeys = new ArrayList<>();
        while (rs.next()) {
            allPrimaryKeys.add(rs.getString(parentTableDto.getForeingKeyName()));
        }

        return allPrimaryKeys;
    }

    public List<String> getPrimaryKeysByRange(
            String tableName, String primaryKeyName, String whereColum, List<String> primaryKeys, boolean isProd
    ) throws SQLException {

        String primaryKeyString = StringUtils.arrangeStringSeparatedByComma(primaryKeys);
        String sql = String.format(
            "SELECT %s FROM %s.%s WHERE %s IN (%s)", primaryKeyName, ownerUtils.getOwner(isProd), tableName, whereColum, primaryKeyString
        );

        PreparedStatement ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<String> allNamesColumnsTable = new ArrayList<>();

        while (rs.next()) {
            allNamesColumnsTable.add(rs.getString(primaryKeyName));
        }

        return allNamesColumnsTable;
    }

    private String getPrimaryKeysConcatenatedByOffSet(String primaryKeyName, List<String> primaryKeys, int offSet) {

        List<String> listPrimaryKeysByOffSet = getListPrimaryKeysSeparatedByBarByOffset(offSet, primaryKeys);

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

    private static List<String> getListPrimaryKeysSeparatedByBarByOffset(int offSet, List<String> primaryKeys) {
        StringBuilder primaryKeysConcat = new StringBuilder();
        int nextOffSet = offSet;
        int index = 1;

        for (String primaryKey : primaryKeys) {
            primaryKeysConcat.append("'").append(primaryKey).append("'");
            if(nextOffSet == index){
                primaryKeysConcat.append("/");
                nextOffSet = nextOffSet + offSet;
            } else if (index != primaryKeys.size()){
                primaryKeysConcat.append(",");
            }

            index++;
        }

        return List.of(primaryKeysConcat.toString().split("/"));
    }
}
