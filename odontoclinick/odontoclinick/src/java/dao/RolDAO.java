package dao;

import modelo.Rol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class RolDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los roles
    public List<Rol> listarTodos() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT * FROM rol";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId_rol(rs.getInt("id_rol"));
                rol.setNombre_rol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                roles.add(rol);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar roles: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        
        return roles;
    }

    // Buscar un rol por ID
    public Rol buscar(int id) {
        Rol rol = null;
        String sql = "SELECT * FROM rol WHERE id_rol = ?";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                rol = new Rol();
                rol.setId_rol(rs.getInt("id_rol"));
                rol.setNombre_rol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar rol: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        
        return rol;
    }

    // Guardar un nuevo rol
    public void guardar(Rol rol) {
        String sql = "INSERT INTO rol(nombre_rol, descripcion) VALUES (?, ?)";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rol.getNombre_rol());
            ps.setString(2, rol.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar rol: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar un rol existente
    public void actualizar(Rol rol) {
        String sql = "UPDATE rol SET nombre_rol = ?, descripcion = ? WHERE id_rol = ?";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, rol.getNombre_rol());
            ps.setString(2, rol.getDescripcion());
            ps.setInt(3, rol.getId_rol());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar rol: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar un rol por ID
    public void eliminar(int id) {
        String sql = "DELETE FROM rol WHERE id_rol = ?";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar rol: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Método privado para cerrar recursos
    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}
