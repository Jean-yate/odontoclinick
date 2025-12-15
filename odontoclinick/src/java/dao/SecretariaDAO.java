package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Secretaria;

public class SecretariaDAO {
    private Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); 
    
    public List<Secretaria> listar() {
    List<Secretaria> lista = new ArrayList<>();
    String sql = "SELECT s.id_secretaria, s.turno, s.fecha_ingreso, " +
                 "u.id_usuario, u.nombre, u.apellidos, u.correo, u.id_estado, " +
                 "u.telefono, u.nombre_usuario " + 
                 "FROM secretaria s " +
                 "INNER JOIN usuario u ON s.id_usuario = u.id_usuario";

    try {
        conn = Conexion.conectar();
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            Secretaria sec = new Secretaria();
            sec.setId_secretaria(rs.getInt("id_secretaria"));
            sec.setTurno(rs.getString("turno"));
            sec.setFecha_ingreso(rs.getDate("fecha_ingreso"));
            sec.setId_usuario(rs.getInt("id_usuario"));

            modelo.Usuario u = new modelo.Usuario();
            u.setId_usuario(rs.getInt("id_usuario"));
            u.setNombre(rs.getString("nombre"));
            u.setApellidos(rs.getString("apellidos"));
            u.setCorreo(rs.getString("correo"));
            u.setId_estado(rs.getInt("id_estado"));
            u.setTelefono(rs.getString("telefono"));
            u.setNombre_usuario(rs.getString("nombre_usuario"));

            sec.setUsuario(u);
            lista.add(sec);
        }
    } catch (SQLException e) {
        System.out.println("Error listar: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return lista;
}

    public boolean cambiarEstado(int idUsuario, int nuevoEstado) {
    String sql = "UPDATE usuario SET id_estado = ? WHERE id_usuario = ?";
    
    try {
        conn = Conexion.conectar(); 
        ps = conn.prepareStatement(sql);
        
        ps.setInt(1, nuevoEstado);
        ps.setInt(2, idUsuario);
        
        return ps.executeUpdate() > 0;
        
    } catch (SQLException e) {
        System.out.println("Error SQL cambiar estado: " + e.getMessage());
        return false;
    } finally {
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}

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

            connTransaccion = dao.Conexion.conectar();
            connTransaccion.setAutoCommit(false);

            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 3, 1, NOW())";
            
            psUser = connTransaccion.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono()); 
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) {
                idGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo generar el ID del usuario.");
            }

            String sqlSec = "INSERT INTO secretaria (id_usuario, turno, fecha_ingreso) VALUES (?, ?, ?)";
            psSec = connTransaccion.prepareStatement(sqlSec);
            psSec.setInt(1, idGenerado);
            psSec.setString(2, s.getTurno());

            if (s.getFecha_ingreso() != null) {
                psSec.setDate(3, new java.sql.Date(s.getFecha_ingreso().getTime()));
            } else {
                psSec.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            }

            psSec.executeUpdate();

            connTransaccion.commit();
            exito = true;

        } catch (SQLException e) {
            System.out.println("Error en transacción secretaria: " + e.getMessage());
            if (connTransaccion != null) {
                try { connTransaccion.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e; 
        } finally {
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
