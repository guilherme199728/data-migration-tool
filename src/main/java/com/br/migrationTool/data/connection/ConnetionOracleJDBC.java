package com.br.migrationTool.data.connection;

import com.br.migrationTool.dto.ConectionDataBaseDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnetionOracleJDBC {
    public static Connection getConnectionProd() {
        // TODO: trocar por properties
        ConectionDataBaseDto conectionDataBaseDto = ConectionDataBaseDto.builder()
                .database("ORCLCDB")
                .host("localhost")
                .user("PROD")
                .password("PROD")
                .port("1521")
                .build();

        return getConnection(conectionDataBaseDto);
    }

    public static Connection getConnectionHomolog() {
        // TODO: trocar por properties
        ConectionDataBaseDto conectionDataBaseDto = ConectionDataBaseDto.builder()
            .database("ORCLCDB")
            .host("localhost")
            .user("HOMOLOG")
            .password("HOMOLOG")
            .port("1521")
            .build();

        return getConnection(conectionDataBaseDto);
    }

    private static Connection getConnection(ConectionDataBaseDto conectionDataBaseDto) {
        Connection conn = null;

        try {

            String connectionString = "jdbc:oracle:thin:@"
                    + conectionDataBaseDto.getHost() +
                    ":" + conectionDataBaseDto.getPort() +
                    ":" + conectionDataBaseDto.getDatabase();
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(
                connectionString,
                conectionDataBaseDto.getUser(),
                conectionDataBaseDto.getPassword()
            );

            return conn;

        } catch (ClassNotFoundException e) {
            System.out.println("Classe Driver JDBC não foi localizado, erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro de conexão com o Banco de dados, erro: " + e.getMessage());
        }

        return conn;
    }
}
