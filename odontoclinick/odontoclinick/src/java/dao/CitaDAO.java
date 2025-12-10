package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

import modelo.Cita;

/* 
imports innecesarios, imports de relacionalidad
import dao.EstadoCitaDAO;
import modelo.Paciente;
import modelo.Medico;
import modelo.EstadoCita;
*/

public class CitaDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EstadoCitaDAO estadoCitaDAO = new EstadoCitaDAO();

    // Listar todas las citas
    public List<Cita> listarTodos() {
        List<Cita> citas = new ArrayList<>();
        String sql = "SELECT * FROM cita";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cita c = new Cita();
                c.setId_cita(rs.getInt("id_cita"));
                c.setId_paciente(rs.getInt("id_paciente"));
                c.setId_doctor(rs.getInt("id_doctor"));
                c.setId_estado_cita(rs.getInt("id_estado_cita"));
                c.setNotas_paciente(rs.getString("notas_paciente"));
                c.setNotas_doctor(rs.getString("notas_doctor"));
                c.setFecha_hora(rs.getTimestamp("fecha_hora"));
                c.setFecha_creacion(rs.getTimestamp("fecha_creacion"));
                c.setFecha_actualizacion(rs.getTimestamp("fecha_actualizacion"));

                // Relaciones
                c.setPaciente(pacienteDAO.buscar(rs.getInt("id_paciente")));
                c.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));
                c.setEstadoCita(estadoCitaDAO.buscar(rs.getInt("id_estado_cita")));

                citas.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar citas: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return citas;
    }

    // Buscar cita por ID
    public Cita buscar(int id) {
        Cita c = null;
        String sql = "SELECT * FROM cita WHERE id_cita = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = new Cita();
                c.setId_cita(rs.getInt("id_cita"));
                c.setId_paciente(rs.getInt("id_paciente"));
                c.setId_doctor(rs.getInt("id_doctor"));
                c.setId_estado_cita(rs.getInt("id_estado_cita"));
                c.setNotas_paciente(rs.getString("notas_paciente"));
                c.setNotas_doctor(rs.getString("notas_doctor"));
                c.setFecha_hora(rs.getTimestamp("fecha_hora"));
                c.setFecha_creacion(rs.getTimestamp("fecha_creacion"));
                c.setFecha_actualizacion(rs.getTimestamp("fecha_actualizacion"));

                // Relaciones
                c.setPaciente(pacienteDAO.buscar(rs.getInt("id_paciente")));
                c.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));
                c.setEstadoCita(estadoCitaDAO.buscar(rs.getInt("id_estado_cita")));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return c;
    }

    // Guardar cita
    public void guardar(Cita c) {
        String sql = "INSERT INTO cita(id_paciente, id_doctor, id_estado_cita, notas_paciente, notas_doctor, fecha_hora, fecha_creacion, fecha_actualizacion) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId_paciente());
            ps.setInt(2, c.getId_doctor());
            ps.setInt(3, c.getId_estado_cita());
            ps.setString(4, c.getNotas_paciente());
            ps.setString(5, c.getNotas_doctor());
            ps.setTimestamp(6, (Timestamp) c.getFecha_hora());
            ps.setTimestamp(7, (Timestamp) c.getFecha_creacion());
            ps.setTimestamp(8, (Timestamp) c.getFecha_actualizacion());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar cita
    public void actualizar(Cita c) {
        String sql = "UPDATE cita SET id_paciente = ?, id_doctor = ?, id_estado_cita = ?, "
                   + "notas_paciente = ?, notas_doctor = ?, fecha_hora = ?, fecha_creacion = ?, fecha_actualizacion = ? "
                   + "WHERE id_cita = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId_paciente());
            ps.setInt(2, c.getId_doctor());
            ps.setInt(3, c.getId_estado_cita());
            ps.setString(4, c.getNotas_paciente());
            ps.setString(5, c.getNotas_doctor());
            ps.setTimestamp(6, (Timestamp) c.getFecha_hora());
            ps.setTimestamp(7, (Timestamp) c.getFecha_creacion());
            ps.setTimestamp(8, (Timestamp) c.getFecha_actualizacion());
            ps.setInt(9, c.getId_cita());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar cita
    public void eliminar(int id) {
        String sql = "DELETE FROM cita WHERE id_cita = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Cerrar recursos
    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}
