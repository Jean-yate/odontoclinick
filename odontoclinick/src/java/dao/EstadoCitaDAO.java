package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.EstadoCita;

public class EstadoCitaDAO {
    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los estados de cita
    public List<EstadoCita> listar() {
        List<EstadoCita> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM estado_cita";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                EstadoCita estado = new EstadoCita();
                estado.setId_estado_cita(rs.getInt("id_estado_cita"));
                estado.setNombre_estado(rs.getString("nombre_estado"));
                estado.setColor(rs.getString("color"));
                lista.add(estado);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar Estados de Cita: " + e.getMessage());
        }
        return lista;
    }

    // Buscar por ID
    public EstadoCita buscar(int id) {
        EstadoCita estado = null;
        try {
            String sql = "SELECT * FROM estado_cita WHERE id_estado_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                estado = new EstadoCita();
                estado.setId_estado_cita(rs.getInt("id_estado_cita"));
                estado.setNombre_estado(rs.getString("nombre_estado"));
                estado.setColor(rs.getString("color"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar Estado de Cita: " + e.getMessage());
        }
        return estado;
    }

    // Guardar nuevo estado
    public void guardar(EstadoCita estado) {
        try {
            String sql = "INSERT INTO estado_cita (nombre_estado, color) VALUES (?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getNombre_estado());
            ps.setString(2, estado.getColor());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar Estado de Cita: " + e.getMessage());
        }
    }

    // Actualizar estado
    public void actualizar(EstadoCita estado) {
        try {
            String sql = "UPDATE estado_cita SET nombre_estado = ?, color = ? WHERE id_estado_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getNombre_estado());
            ps.setString(2, estado.getColor());
            ps.setInt(3, estado.getId_estado_cita());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar Estado de Cita: " + e.getMessage());
        }
    }

    // Eliminar estado
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM estado_cita WHERE id_estado_cita = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar Estado de Cita: " + e.getMessage());
        }
    }
}
