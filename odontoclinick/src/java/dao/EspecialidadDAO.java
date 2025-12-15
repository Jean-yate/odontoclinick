package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Especialidad;

public class EspecialidadDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public List<Especialidad> listar() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM especialidad";
        
        try {
            conn = Conexion.conectar(); 
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Especialidad esp = new Especialidad();
                esp.setId_especialidad(rs.getInt("id_especialidad"));
                esp.setNombre_especialidad(rs.getString("nombre_especialidad"));
                esp.setDescripcion(rs.getString("descripcion"));
                lista.add(esp);
            }
        } catch (SQLException e) {
            System.out.println("Error listar Especialidad: " + e.getMessage());
        } finally {
            cerrarRecursos(); 
        }
        return lista;
    }

    public Especialidad buscar(int id) {
        Especialidad esp = null;
        String sql = "SELECT * FROM especialidad WHERE id_especialidad = ?";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                esp = new Especialidad();
                esp.setId_especialidad(rs.getInt("id_especialidad"));
                esp.setNombre_especialidad(rs.getString("nombre_especialidad"));
                esp.setDescripcion(rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            System.out.println("Error buscar Especialidad: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return esp;
    }

    public void guardar(Especialidad esp) {
        String sql = "INSERT INTO especialidad(nombre_especialidad, descripcion) VALUES(?, ?)";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, esp.getNombre_especialidad());
            ps.setString(2, esp.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Especialidad: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void actualizar(Especialidad esp) {
        String sql = "UPDATE especialidad SET nombre_especialidad = ?, descripcion = ? WHERE id_especialidad = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, esp.getNombre_especialidad());
            ps.setString(2, esp.getDescripcion());
            ps.setInt(3, esp.getId_especialidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar Especialidad: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM especialidad WHERE id_especialidad = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Especialidad: " + e.getMessage());
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