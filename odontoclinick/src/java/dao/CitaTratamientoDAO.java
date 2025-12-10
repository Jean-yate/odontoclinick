package dao;

import modelo.CitaTratamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/*
imports innecesarios, imports de relacionalidad
import modelo.Cita;
import modelo.Tratamiento;
*/

public class CitaTratamientoDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private final CitaDAO citaDAO = new CitaDAO();
    private final TratamientoDAO tratamientoDAO = new TratamientoDAO();

    public List<CitaTratamiento> listarTodos() {
        List<CitaTratamiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM cita_tratamiento";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                CitaTratamiento ct = new CitaTratamiento();
                ct.setId_cita_tratamiento(rs.getInt("id_cita_tratamiento"));
                ct.setId_cita(rs.getInt("id_cita"));
                ct.setId_tratamiento(rs.getInt("id_tratamiento"));
                ct.setObservaciones(rs.getString("observaciones"));
                ct.setCosto_aplicado(rs.getFloat("costo_aplicado"));
                ct.setCompletado(rs.getBoolean("completado"));

                ct.setCita(citaDAO.buscar(ct.getId_cita()));
                ct.setTratamiento(tratamientoDAO.buscar(ct.getId_tratamiento()));

                lista.add(ct);
            }

        } catch (SQLException e) {
            System.out.println("Error listar CitaTratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return lista;
    }

    public CitaTratamiento buscar(int id) {
        CitaTratamiento ct = null;
        String sql = "SELECT * FROM cita_tratamiento WHERE id_cita_tratamiento = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                ct = new CitaTratamiento();
                ct.setId_cita_tratamiento(rs.getInt("id_cita_tratamiento"));
                ct.setId_cita(rs.getInt("id_cita"));
                ct.setId_tratamiento(rs.getInt("id_tratamiento"));
                ct.setObservaciones(rs.getString("observaciones"));
                ct.setCosto_aplicado(rs.getFloat("costo_aplicado"));
                ct.setCompletado(rs.getBoolean("completado"));

                ct.setCita(citaDAO.buscar(ct.getId_cita()));
                ct.setTratamiento(tratamientoDAO.buscar(ct.getId_tratamiento()));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar CitaTratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return ct;
    }

    public void guardar(CitaTratamiento ct) {
        String sql = "INSERT INTO cita_tratamiento(id_cita, id_tratamiento, observaciones, costo_aplicado, completado) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ct.getId_cita());
            ps.setInt(2, ct.getId_tratamiento());
            ps.setString(3, ct.getObservaciones());
            ps.setFloat(4, ct.getCosto_aplicado());
            ps.setBoolean(5, ct.isCompletado());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar CitaTratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void actualizar(CitaTratamiento ct) {
        String sql = "UPDATE cita_tratamiento SET id_cita = ?, id_tratamiento = ?, observaciones = ?, costo_aplicado = ?, completado = ? "
                   + "WHERE id_cita_tratamiento = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ct.getId_cita());
            ps.setInt(2, ct.getId_tratamiento());
            ps.setString(3, ct.getObservaciones());
            ps.setFloat(4, ct.getCosto_aplicado());
            ps.setBoolean(5, ct.isCompletado());
            ps.setInt(6, ct.getId_cita_tratamiento());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar CitaTratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM cita_tratamiento WHERE id_cita_tratamiento = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar CitaTratamiento: " + e.getMessage());
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
