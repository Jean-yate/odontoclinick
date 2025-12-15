package dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class Conexion {

    // Nombre JNDI exacto que configuraste en GlassFish
    private static final String JNDI_NAME = "jdbc/odontoclinic";

    public static Connection conectar() {
        Connection conn = null;
        try {
            // 1. Obtener el contexto del servidor
            Context ctx = new InitialContext();
            
            // 2. Buscar el Pool de conexiones (DataSource)
            // A veces GlassFish requiere el prefijo java:comp/env/ si se configuró resource-ref
            // Pero intentaremos primero con el nombre directo global.
            DataSource ds = (DataSource) ctx.lookup(JNDI_NAME);
            
            // 3. Obtener una conexión disponible del pool
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