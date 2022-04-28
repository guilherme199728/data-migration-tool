package com.br.migrationTool.datas.daos;

import com.br.migrationTool.constraints.querys.MigrationQueryConstraint;
import com.br.migrationTool.constraints.querys.TableReferenceQueryConstraint;
import com.br.migrationTool.datas.connections.ConnectionOracleJDBC;
import com.br.migrationTool.dtos.migration.MigrationDto;
import com.br.migrationTool.dtos.migration.NamesTypesFieldsTableDto;
import com.br.migrationTool.dtos.migration.ParentTableDto;
import com.br.migrationTool.dtos.migration.TableDataDto;
import com.br.migrationTool.utils.OwnerUtils;
import com.br.migrationTool.utils.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataTableDao {

    @Autowired
    ConnectionOracleJDBC connectionOracleJDBC;
    @Autowired
    OwnerUtils ownerUtils;
    @Autowired
    TableReferencesDao tableReferencesDao;

    public List<TableDataDto> getDataTableByPrimaryKey(
        String tableName, String primaryKeyName, String primaryKey
    ) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableDataDto> allTableDataDto = new ArrayList<>();

        try {
            String sql = String.format(
                MigrationQueryConstraint.GET_DATA_TABLE_BY_PRIMARY_KEY,
                ownerUtils.getOwner(true),
                tableName,
                primaryKeyName
            );

            ps = connectionOracleJDBC.getConnection(true).prepareStatement(sql);
            ps.setString(1, primaryKey);
            rs = ps.executeQuery();

            List<NamesTypesFieldsTableDto> namesTypesFieldsTableDtos =
                tableReferencesDao.getAllNamesAndTypeColumnsTableFromTableName(
                    tableName, false
                );

            while (rs.next()) {
                for (NamesTypesFieldsTableDto namesTypesFieldsTableDto : namesTypesFieldsTableDtos) {
                    TableDataDto tableDataDto = TableDataDto.builder()
                        .fieldName(namesTypesFieldsTableDto.getFieldName())
                        .filedData(correctFieldData(namesTypesFieldsTableDto, rs))
                        .filedType(namesTypesFieldsTableDto.getFieldType())
                        .build();

                    allTableDataDto.add(tableDataDto);
                }
            }
        } finally {
            connectionOracleJDBC.close(ps, rs);
        }

        return allTableDataDto;
    }

    private String correctFieldData(
        NamesTypesFieldsTableDto namesTypesFieldsTableDto, ResultSet rs
    ) throws SQLException {

        return namesTypesFieldsTableDto.getFieldType().equals("BLOB") ?
            null :
            rs.getString(namesTypesFieldsTableDto.getFieldName());
    }

    public List<String> getPrimaryKeysByParentTable(
        MigrationDto migrationDto, ParentTableDto parentTableDto, boolean isProd
    ) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> allPrimaryKeys = new ArrayList<>();

        try {
            String sql = TableReferenceQueryConstraint.GET_PRIMARY_KEYS_BY_PARENT_TABLE +
                SqlUtils.getPrimaryKeysConcatenatedByOffSet(
                    parentTableDto.getForeingKeyName(),
                    migrationDto.getPrimaryKeys()
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
                migrationDto.getBasicTableStructureDto().getPrimaryKeyName()
            );

            ps = connectionOracleJDBC.getConnection(isProd).prepareStatement(sqlBuilt);
            rs = ps.executeQuery();

            while (rs.next()) {
                allPrimaryKeys.add(rs.getString(parentTableDto.getForeingKeyName()));
            }
        } finally {
            connectionOracleJDBC.close(ps, rs);
        }

        return allPrimaryKeys;
    }
}
