package com.br.migrationTool.datas.daos;

import com.br.migrationTool.constraints.querys.TableReferenceQueryConstraint;
import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.BasicTableStructureDto;
import com.br.migrationTool.dtos.migration.ChildrenTableDto;
import com.br.migrationTool.dtos.migration.NamesTypesFieldsTableDto;
import com.br.migrationTool.dtos.migration.ParentTableDto;
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
import java.sql.SQLException;
import java.util.ArrayList;
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

        String sql = TableReferenceQueryConstraint.GET_PARENT_TABLE;
        String owner = ownerUtils.getOwner(isProd);
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<ParentTableDto>> rsh = new BeanListHandler<>(ParentTableDto.class);
        Object[] params = new Object[]{owner, owner, tableName, tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public BasicTableStructureDto getBasicTableStructureFromConstraint(
        String tableName, boolean isProd
    ) throws SQLException {

        String sql = TableReferenceQueryConstraint.GET_BASIC_TABLE_STRUCTURE;
        String owner = ownerUtils.getOwner(isProd);
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<BasicTableStructureDto> rsh = new BeanHandler<>(BasicTableStructureDto.class);
        Object[] params = new Object[]{owner, tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public List<NamesTypesFieldsTableDto> getAllNamesAndTypeColumnsTableFromTableName(
        String tableName, boolean isProd
    ) throws SQLException {

        String sql = TableReferenceQueryConstraint.GET_ALL_NAMES_AND_TYPE_COLUMNS_TABLE;
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<NamesTypesFieldsTableDto>> rsh = new BeanListHandler<>(NamesTypesFieldsTableDto.class);
        Object[] params = new Object[]{tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public List<ChildrenTableDto> getChildrenTablesFromConstraint(
        String tableName, boolean isProd
    ) throws SQLException {

        String sql = TableReferenceQueryConstraint.GET_CHILDREN_TABLES;
        String owner = ownerUtils.getOwner(isProd);
        QueryRunner runner = new QueryRunner();
        ResultSetHandler<List<ChildrenTableDto>> rsh = new BeanListHandler<>(ChildrenTableDto.class);
        Object[] params = new Object[]{owner, owner, tableName};

        return runner.query(connectionOracleJDBC.getConnection(isProd), sql, rsh, params);
    }

    public String getPrimaryKeyNameFromConstraint(
        String tableName, boolean isProd
    ) throws SQLException {

        ResultSet rs = null;
        PreparedStatement ps = null;
        String primaryKeyNames = null;

        try {
            String sql = TableReferenceQueryConstraint.GET_PRIMARY_KEY_NAME;

            ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sql);
            ps.setString(1, ownerUtils.getOwner(isProd));
            ps.setString(2, tableName);
            rs = ps.executeQuery();

            while (rs.next()) {
                primaryKeyNames = rs.getString("COLUMN_NAME");
            }
        } finally {
            connectionOracleJDBC.close(ps, rs);
        }

        return primaryKeyNames;
    }

    public List<String> getPrimaryKeys(
        String tableName, String primaryKeyName, String whereColum, List<String> primaryKeys, boolean isProd
    ) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> allNamesColumnsTable = new ArrayList<>();

        try {
            String primaryKeyString = StringUtils.arrangeStringSeparatedByComma(primaryKeys);
            String sql = String.format(
                TableReferenceQueryConstraint.GET_PRIMARY_KEYS,
                primaryKeyName,
                ownerUtils.getOwner(isProd),
                tableName,
                whereColum,
                primaryKeyString
            );

            ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                allNamesColumnsTable.add(rs.getString(primaryKeyName));
            }
        } finally {
            connectionOracleJDBC.close(ps, rs);
        }

        return allNamesColumnsTable;
    }
}
