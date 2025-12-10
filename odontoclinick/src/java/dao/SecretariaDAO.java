package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Secretaria;

/*
imports innecesarios, imports de relacionalidad y tipo de datos
import java.util.Date;
import modelo.Usuario;
*/

public class SecretariaDAO {
    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // Para traer el objeto Usuario completo

    // Listar todas las secretarias
    public List<Secretaria> listar() {
        List<Secretaria> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM secretaria";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Secretaria sec = new Secretaria();
                sec.setId_secretaria(rs.getInt("id_secretaria"));
                sec.setId_usuario(rs.getInt("id_usuario"));
                sec.setTurno(rs.getString("turno"));
                sec.setFecha_ingreso(rs.getDate("fecha_ingreso"));
                sec.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
                lista.add(sec);
            }
        } catch (SQLException e) {
            System.out.println("Error listar Secretaria: " + e.getMessage());
        }
        return lista;
    }

    // Buscar secretaria por ID
    public Secretaria buscar(int id) {
        Secretaria sec = null;
        try {
            String sql = "SELECT * FROM secretaria WHERE id_secretaria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                sec = new Secretaria();
                sec.setId_secretaria(rs.getInt("id_secretaria"));
                sec.setId_usuario(rs.getInt("id_usuario"));
                sec.setTurno(rs.getString("turno"));
                sec.setFecha_ingreso(rs.getDate("fecha_ingreso"));
                sec.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
            }
        } catch (SQLException e) {
            System.out.println("Error buscar Secretaria: " + e.getMessage());
        }
        return sec;
    }

    // Guardar nueva secretaria
    public void guardar(Secretaria sec) {
        try {
            String sql = "INSERT INTO secretaria(id_usuario, turno, fecha_ingreso) VALUES(?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sec.getId_usuario());
            ps.setString(2, sec.getTurno());
            ps.setDate(3, new java.sql.Date(sec.getFecha_ingreso().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Secretaria: " + e.getMessage());
        }
    }

    // Actualizar secretaria existente
    public void actualizar(Secretaria sec) {
        try {
            String sql = "UPDATE secretaria SET id_usuario = ?, turno = ?, fecha_ingreso = ? WHERE id_secretaria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sec.getId_usuario());
            ps.setString(2, sec.getTurno());
            ps.setDate(3, new java.sql.Date(sec.getFecha_ingreso().getTime()));
            ps.setInt(4, sec.getId_secretaria());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar Secretaria: " + e.getMessage());
        }
    }

    // Eliminar secretaria por ID
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM secretaria WHERE id_secretaria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Secretaria: " + e.getMessage());
        }
    }
    
    public boolean registrarTransaccion(modelo.Usuario u, modelo.Secretaria s) throws SQLException {
        boolean exito = false;
        Connection connTransaccion = null;
        PreparedStatement psUser = null;
        PreparedStatement psSec = null;
        ResultSet rsKeys = null;

        try {
            // Usamos tu clase de Conexión
            connTransaccion = dao.Conexion.conectar();
            connTransaccion.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Insertar Usuario (Rol 3 = Secretaria, Estado 1 = Activo)
            // Nota: El teléfono personal va en la tabla usuario
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 3, 1, NOW())";
            
            psUser = connTransaccion.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono()); 
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            // 2. Obtener el ID generado automáticamente
            rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) {
                idGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo generar el ID del usuario.");
            }

            // 3. Insertar Secretaria
            // Nota: fecha_ingreso se inserta o se usa la actual
            String sqlSec = "INSERT INTO secretaria (id_usuario, turno, fecha_ingreso) VALUES (?, ?, ?)";
            psSec = connTransaccion.prepareStatement(sqlSec);
            psSec.setInt(1, idGenerado);
            psSec.setString(2, s.getTurno());
            
            // Manejo seguro de la fecha
            if (s.getFecha_ingreso() != null) {
                psSec.setDate(3, new java.sql.Date(s.getFecha_ingreso().getTime()));
            } else {
                psSec.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            }

            psSec.executeUpdate();

            connTransaccion.commit(); // CONFIRMAR CAMBIOS
            exito = true;

        } catch (SQLException e) {
            System.out.println("Error en transacción secretaria: " + e.getMessage());
            if (connTransaccion != null) {
                try { connTransaccion.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e; // Relanzar para que el Bean sepa que falló
        } finally {
            // Cerrar recursos manuales de este método
            try { if (rsKeys != null) rsKeys.close(); } catch (SQLException e) {}
            try { if (psUser != null) psUser.close(); } catch (SQLException e) {}
            try { if (psSec != null) psSec.close(); } catch (SQLException e) {}
            try { 
                if (connTransaccion != null) {
                    connTransaccion.setAutoCommit(true);
                    connTransaccion.close(); 
                }
            } catch (SQLException e) {}
        }
        return exito;
    }
    
}
