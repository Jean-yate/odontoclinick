package dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Conexion {

    private static final String JNDI_NAME = "jdbc/odontoclinic";

    public static Connection conectar() {
        Connection conn = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(JNDI_NAME);
            conn = ds.getConnection();
            
        } catch (NamingException e) {
            System.out.println("ERROR JNDI: No se encontró el recurso " + JNDI_NAME);
            System.out.println("Verifica que el nombre en GlassFish coincida.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("ERROR SQL: No se pudo obtener conexión del Pool.");
            e.printStackTrace();
        }
        return conn;
    }
}