package dao;

import modelo.Estado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstadoDAO {
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public List<Estado> listarTodos() {
        List<Estado> estados = new ArrayList<>();
        String sql = "SELECT * FROM estado";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Estado estado = new Estado();
                estado.setId_estado(rs.getInt("id_estado"));
                estado.setNombre_estado(rs.getString("nombre_estado"));
                estados.add(estado);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar estados: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        
        return estados;
    }

    public Estado buscar(int id) {
        Estado estado = null;
        String sql = "SELECT * FROM estado WHERE id_estado = ?";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                estado = new Estado();
                estado.setId_estado(rs.getInt("id_estado"));
                estado.setNombre_estado(rs.getString("nombre_estado"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estado: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return estado;
    }

    public void guardar(Estado estado) {
        String sql = "INSERT INTO estado(nombre_estado) VALUES (?)";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getNombre_estado());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar estado: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void actualizar(Estado estado) {
        String sql = "UPDATE estado SET nombre_estado = ? WHERE id_estado = ?";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getNombre_estado());
            ps.setInt(2, estado.getId_estado());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM estado WHERE id_estado = ?";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar estado: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}