package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Horario;

/*
imports innecesarios, imports de relacionalidad  o sql
import modelo.Medico;
import java.sql.Time;
*/

public class HorarioDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    private final MedicoDAO medicoDAO = new MedicoDAO();

    // Listar todos los horarios
    public List<Horario> listar() {
        List<Horario> horarios = new ArrayList<>();
        try {
            String sql = "SELECT * FROM horario";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Horario h = new Horario();
                h.setId_horario(rs.getInt("id_horario"));
                h.setId_doctor(rs.getInt("id_doctor"));
                h.setDia_semana(rs.getString("dia_semana"));
                h.setDuracion_cita_minutos(rs.getInt("duracion_cita_minutos"));
                h.setHora_inicio(rs.getTime("hora_inicio"));
                h.setHora_fin(rs.getTime("hora_fin"));
                h.setActivo(rs.getBoolean("activo"));
                h.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));

                horarios.add(h);
            }

        } catch (SQLException e) {
            System.out.println("Error listar Horarios: " + e.getMessage());
        }

        return horarios;
    }

    // Buscar horario por ID
    public Horario buscar(int id) {
        Horario h = null;
        try {
            String sql = "SELECT * FROM horario WHERE id_horario = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                h = new Horario();
                h.setId_horario(rs.getInt("id_horario"));
                h.setId_doctor(rs.getInt("id_doctor"));
                h.setDia_semana(rs.getString("dia_semana"));
                h.setDuracion_cita_minutos(rs.getInt("duracion_cita_minutos"));
                h.setHora_inicio(rs.getTime("hora_inicio"));
                h.setHora_fin(rs.getTime("hora_fin"));
                h.setActivo(rs.getBoolean("activo"));
                h.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar Horario: " + e.getMessage());
        }

        return h;
    }

    // Guardar nuevo horario
    public void guardar(Horario h) {
        try {
            String sql = "INSERT INTO horario (id_doctor, dia_semana, duracion_cita_minutos, hora_inicio, hora_fin, activo) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getId_doctor());
            ps.setString(2, h.getDia_semana());
            ps.setInt(3, h.getDuracion_cita_minutos());
            ps.setTime(4, h.getHora_inicio());
            ps.setTime(5, h.getHora_fin());
            ps.setBoolean(6, h.isActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Horario: " + e.getMessage());
        }
    }

    // Actualizar horario existente
    public void actualizar(Horario h) {
        try {
            String sql = "UPDATE horario SET id_doctor = ?, dia_semana = ?, duracion_cita_minutos = ?, hora_inicio = ?, hora_fin = ?, activo = ? WHERE id_horario = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getId_doctor());
            ps.setString(2, h.getDia_semana());
            ps.setInt(3, h.getDuracion_cita_minutos());
            ps.setTime(4, h.getHora_inicio());
            ps.setTime(5, h.getHora_fin());
            ps.setBoolean(6, h.isActivo());
            ps.setInt(7, h.getId_horario());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar Horario: " + e.getMessage());
        }
    }

    // Eliminar horario
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM horario WHERE id_horario = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Horario: " + e.getMessage());
        }
    }
    // AGREGAR O REEMPLAZAR EN: dao/HorarioDAO.java

public modelo.Horario buscarPorMedicoYDia(int idDoctor, String diaSemana) {
    modelo.Horario h = null;
    // Compara ignorando mayúsculas/minúsculas y verifica que esté activo
    String sql = "SELECT * FROM horario WHERE id_doctor = ? AND LOWER(dia_semana) = LOWER(?) AND activo = 1";
    
    try {
        // CORRECCIÓN: Usamos tu variable 'conn' existente o llamamos a tu clase Conexion
        if (conn == null || conn.isClosed()) {
             // Si la conexión se cerró, la reabrimos usando tu clase estática
             // (Asumiendo que 'conn' no sea final, si es final, usa una variable local)
             // Para seguridad, usaremos una variable local aquí:
             java.sql.Connection conexionLocal = dao.Conexion.conectar();
             ps = conexionLocal.prepareStatement(sql);
        } else {
             ps = conn.prepareStatement(sql);
        }

        ps.setInt(1, idDoctor);
        ps.setString(2, diaSemana);
        rs = ps.executeQuery();

        if (rs.next()) {
            h = new modelo.Horario();
            h.setId_horario(rs.getInt("id_horario"));
            h.setId_doctor(rs.getInt("id_doctor"));
            h.setDia_semana(rs.getString("dia_semana"));
            h.setDuracion_cita_minutos(rs.getInt("duracion_cita_minutos"));
            h.setHora_inicio(rs.getTime("hora_inicio"));
            h.setHora_fin(rs.getTime("hora_fin"));
            h.setActivo(rs.getBoolean("activo"));
        }
    } catch (SQLException e) {
        System.out.println("Error buscarPorMedicoYDia: " + e.getMessage());
    } 
    // No cerramos recursos aquí si tu 'conn' es compartida y final en el DAO,
    // pero si usas el patrón de abrir/cerrar por método, descomenta el finally.
    /*
    finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
    }
    */
    return h;
}
    }
    

