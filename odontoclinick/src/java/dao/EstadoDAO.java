package dao;
import modelo.Estado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

public class EstadoDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los estados
    public List<Estado> listarTodos() {
        List<Estado> estados = new ArrayList<>();
        String sql = "SELECT * FROM estado";
        
        try {
            conn = ds.getConnection();
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

    // Buscar un estado por ID
    public Estado buscar(int id) {
        Estado estado = null;
        String sql = "SELECT * FROM estado WHERE id_estado = ?";
        
        try {
            conn = ds.getConnection();
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

    // Guardar un nuevo estado
    public void guardar(Estado estado) {
        String sql = "INSERT INTO estado(nombre_estado) VALUES (?)";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getNombre_estado());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar estado: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar un estado existente
    public void actualizar(Estado estado) {
        String sql = "UPDATE estado SET nombre_estado = ? WHERE id_estado = ?";
        
        try {
            conn = ds.getConnection();
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

    // Eliminar un estado por ID
    public void eliminar(int id) {
        String sql = "DELETE FROM estado WHERE id_estado = ?";
        
        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar estado: " + e.getMessage());
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
