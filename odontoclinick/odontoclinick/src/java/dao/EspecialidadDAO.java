package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Especialidad;

public class EspecialidadDAO {
    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todas las especialidades
    public List<Especialidad> listar() {
        List<Especialidad> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM especialidad";
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
        }
        return lista;
    }

    // Buscar especialidad por ID
    public Especialidad buscar(int id) {
        Especialidad esp = null;
        try {
            String sql = "SELECT * FROM especialidad WHERE id_especialidad = ?";
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
        }
        return esp;
    }

    // Guardar nueva especialidad
    public void guardar(Especialidad esp) {
        try {
            String sql = "INSERT INTO especialidad(nombre_especialidad, descripcion) VALUES(?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, esp.getNombre_especialidad());
            ps.setString(2, esp.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Especialidad: " + e.getMessage());
        }
    }

    // Actualizar especialidad existente
    public void actualizar(Especialidad esp) {
        try {
            String sql = "UPDATE especialidad SET nombre_especialidad = ?, descripcion = ? WHERE id_especialidad = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, esp.getNombre_especialidad());
            ps.setString(2, esp.getDescripcion());
            ps.setInt(3, esp.getId_especialidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar Especialidad: " + e.getMessage());
        }
    }

    // Eliminar especialidad por ID
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM especialidad WHERE id_especialidad = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Especialidad: " + e.getMessage());
        }
    }
}
