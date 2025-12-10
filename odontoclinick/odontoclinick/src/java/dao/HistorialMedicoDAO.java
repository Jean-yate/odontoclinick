package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.HistorialMedico;

/*
imports innecesarios, imports de relacionalidad
import modelo.Paciente;
import modelo.Cita;
import modelo.Medico;
*/

public class HistorialMedicoDAO {
    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final CitaDAO citaDAO = new CitaDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    public List<HistorialMedico> listarHistoriales() {
        List<HistorialMedico> lstHistoriales = new ArrayList<>();
        try {
            String sql = "SELECT * FROM historial_medico";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                HistorialMedico h = new HistorialMedico();
                h.setId_historial(rs.getInt("id_historial"));
                h.setId_paciente(rs.getInt("id_paciente"));
                h.setId_cita(rs.getInt("id_cita"));
                h.setId_doctor(rs.getInt("id_doctor"));
                h.setDiagnostico(rs.getString("diagnostico"));
                h.setTratamiento_realizado(rs.getString("tratamiento_realizado"));
                h.setObservaciones(rs.getString("observaciones"));
                h.setFecha(rs.getDate("fecha"));
                h.setFecha_creacion(rs.getDate("fecha_creacion"));

                h.setPaciente(pacienteDAO.buscar(rs.getInt("id_paciente")));
                h.setCita(citaDAO.buscar(rs.getInt("id_cita")));
                h.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));

                lstHistoriales.add(h);
            }
        } catch (SQLException e) {
            System.out.println("Error listarHistoriales: " + e.getMessage());
        }
        return lstHistoriales;
    }

    public void guardar(HistorialMedico h) {
        try {
            String sql = "INSERT INTO historial_medico (id_paciente, id_cita, id_doctor, diagnostico, tratamiento_realizado, observaciones, fecha, fecha_creacion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getId_paciente());
            ps.setInt(2, h.getId_cita());
            ps.setInt(3, h.getId_doctor());
            ps.setString(4, h.getDiagnostico());
            ps.setString(5, h.getTratamiento_realizado());
            ps.setString(6, h.getObservaciones());
            ps.setDate(7, new java.sql.Date(h.getFecha().getTime()));
            ps.setDate(8, new java.sql.Date(h.getFecha_creacion().getTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar historial: " + e.getMessage());
        }
    }

    public HistorialMedico buscar(int id) {
        HistorialMedico h = null;
        try {
            String sql = "SELECT * FROM historial_medico WHERE id_historial = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                h = new HistorialMedico();
                h.setId_historial(rs.getInt("id_historial"));
                h.setId_paciente(rs.getInt("id_paciente"));
                h.setId_cita(rs.getInt("id_cita"));
                h.setId_doctor(rs.getInt("id_doctor"));
                h.setDiagnostico(rs.getString("diagnostico"));
                h.setTratamiento_realizado(rs.getString("tratamiento_realizado"));
                h.setObservaciones(rs.getString("observaciones"));
                h.setFecha(rs.getDate("fecha"));
                h.setFecha_creacion(rs.getDate("fecha_creacion"));

                h.setPaciente(pacienteDAO.buscar(rs.getInt("id_paciente")));
                h.setCita(citaDAO.buscar(rs.getInt("id_cita")));
                h.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));
            }
        } catch (SQLException e) {
            System.out.println("Error buscar historial: " + e.getMessage());
        }
        return h;
    }

    public void actualizar(HistorialMedico h) {
        try {
            String sql = "UPDATE historial_medico SET id_paciente = ?, id_cita = ?, id_doctor = ?, diagnostico = ?, "
                    + "tratamiento_realizado = ?, observaciones = ?, fecha = ?, fecha_creacion = ? WHERE id_historial = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getId_paciente());
            ps.setInt(2, h.getId_cita());
            ps.setInt(3, h.getId_doctor());
            ps.setString(4, h.getDiagnostico());
            ps.setString(5, h.getTratamiento_realizado());
            ps.setString(6, h.getObservaciones());
            ps.setDate(7, new java.sql.Date(h.getFecha().getTime()));
            ps.setDate(8, new java.sql.Date(h.getFecha_creacion().getTime()));
            ps.setInt(9, h.getId_historial());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar historial: " + e.getMessage());
        }
    }

    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM historial_medico WHERE id_historial = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar historial: " + e.getMessage());
        }
    }
}
