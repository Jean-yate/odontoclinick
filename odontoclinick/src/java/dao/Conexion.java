package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/odontoclinic"
            + "?useSSL=false"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=America/Bogota";

    private static final String USER = "jean";
    private static final String PASS = "12345";

    private static Connection conn;

    public static Connection conectar() {

        try {
            if (conn == null || conn.isClosed()) {

                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                conn = DriverManager.getConnection(URL, USER, PASS);
            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error en la conexión: " + e.getMessage());
        }

        return conn;
    }
}
