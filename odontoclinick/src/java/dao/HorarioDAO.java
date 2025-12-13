package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Horario;

public class HorarioDAO {


    // 1. ELIMINAMOS la conexión global
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // MedicoDAO ya lo corregimos antes, así que es seguro instanciarlo aquí
    private final MedicoDAO medicoDAO = new MedicoDAO();

    // Listar todos los horarios
    public List<Horario> listar() {
        List<Horario> horarios = new ArrayList<>();
        String sql = "SELECT * FROM horario";
        
        try {
            conn = Conexion.conectar(); // 2. Conexión nueva
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Horario h = mapResultSetToHorario(rs);
                horarios.add(h);
            }
        } catch (SQLException e) {
            System.out.println("Error listar Horarios: " + e.getMessage());
        } finally {
            cerrarRecursos(); // 3. Cerrar
        }

        return horarios;
    }

    // Buscar horario por ID
    public Horario buscar(int id) {
        Horario h = null;
        String sql = "SELECT * FROM horario WHERE id_horario = ?";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                h = mapResultSetToHorario(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error buscar Horario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return h;
    }
    
    // Método helper para mapear (ahorra código repetido)
    private Horario mapResultSetToHorario(ResultSet rs) throws SQLException {
        Horario h = new Horario();
        h.setId_horario(rs.getInt("id_horario"));
        h.setId_doctor(rs.getInt("id_doctor"));
        h.setDia_semana(rs.getString("dia_semana"));
        h.setDuracion_cita_minutos(rs.getInt("duracion_cita_minutos"));
        h.setHora_inicio(rs.getTime("hora_inicio"));
        h.setHora_fin(rs.getTime("hora_fin"));
        h.setActivo(rs.getBoolean("activo"));
        // Cargar médico si es necesario
        try {
             h.setMedico(medicoDAO.buscar(rs.getInt("id_doctor")));
        } catch(Exception e) {}
        return h;
    }

    // Guardar nuevo horario
    public void guardar(Horario h) {
        String sql = "INSERT INTO horario (id_doctor, dia_semana, duracion_cita_minutos, hora_inicio, hora_fin, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            conn = Conexion.conectar();
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
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar horario existente
    public void actualizar(Horario h) {
        String sql = "UPDATE horario SET id_doctor = ?, dia_semana = ?, duracion_cita_minutos = ?, hora_inicio = ?, hora_fin = ?, activo = ? WHERE id_horario = ?";
        try {
            conn = Conexion.conectar();
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
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar horario
    public void eliminar(int id) {
        String sql = "DELETE FROM horario WHERE id_horario = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Horario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }
    
    // MÉTODO CORREGIDO Y LIMPIO
    public modelo.Horario buscarPorMedicoYDia(int idDoctor, String diaSemana) {
        modelo.Horario h = null;
        String sql = "SELECT * FROM horario WHERE id_doctor = ? AND LOWER(dia_semana) = LOWER(?) AND activo = 1";
        
        try {
            conn = Conexion.conectar(); // Usamos la conexión estándar del pool
            ps = conn.prepareStatement(sql);
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
        } finally {
            cerrarRecursos(); // Importante cerrar aquí también
        }
        return h;
    }
    
    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return h;
}
    public List<modelo.Horario> listarPorDoctor(int idDoctor) {
        List<modelo.Horario> lista = new ArrayList<>();
        String sql = "SELECT * FROM horario WHERE id_doctor = ?";
        try {
            // Usamos tu conexión estática o la variable conn si está abierta
            if (conn == null || conn.isClosed()) conn = Conexion.conectar();
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idDoctor);
            rs = ps.executeQuery();

            while (rs.next()) {
                modelo.Horario h = new modelo.Horario();
                h.setId_horario(rs.getInt("id_horario"));
                h.setId_doctor(rs.getInt("id_doctor"));
                h.setDia_semana(rs.getString("dia_semana"));
                h.setDuracion_cita_minutos(rs.getInt("duracion_cita_minutos"));
                h.setHora_inicio(rs.getTime("hora_inicio"));
                h.setHora_fin(rs.getTime("hora_fin"));
                h.setActivo(rs.getBoolean("activo"));
                // No cargamos el objeto Médico completo para evitar ciclos, ya sabemos quién es
                lista.add(h);
            }
        } catch (SQLException e) {
            System.out.println("Error listarPorDoctor: " + e.getMessage());
        }
        return lista;
    }
