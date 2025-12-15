package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import modelo.Cita;
import modelo.EstadoCita;
import modelo.Medico;
import modelo.Paciente;
import modelo.Usuario;

public class CitaDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public List<Cita> listarConFiltros(String paciente, String medico, Integer estado) {
        List<Cita> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.*, ");
        sql.append("  ec.nombre_estado, ec.color, ");
        sql.append("  u_pac.nombre as nom_pac, u_pac.apellidos as ape_pac, ");
        sql.append("  u_med.nombre as nom_med, u_med.apellidos as ape_med ");
        sql.append("FROM cita c ");
        sql.append("INNER JOIN estado_cita ec ON c.id_estado_cita = ec.id_estado_cita ");
        sql.append("INNER JOIN paciente p ON c.id_paciente = p.id_paciente ");
        sql.append("INNER JOIN usuario u_pac ON p.id_usuario = u_pac.id_usuario ");
        sql.append("INNER JOIN medico m ON c.id_doctor = m.id_doctor ");
        sql.append("INNER JOIN usuario u_med ON m.id_usuario = u_med.id_usuario ");
        sql.append("WHERE 1=1 ");

        if (paciente != null && !paciente.isEmpty()) {
            sql.append("AND (u_pac.nombre LIKE ? OR u_pac.apellidos LIKE ?) ");
        }
        if (medico != null && !medico.isEmpty()) {
            sql.append("AND (u_med.nombre LIKE ? OR u_med.apellidos LIKE ?) ");
        }
        if (estado != null && estado != 0) {
            sql.append("AND c.id_estado_cita = ? ");
        }
        
        sql.append("ORDER BY c.fecha_hora ASC");

        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql.toString());

            int index = 1;
            if (paciente != null && !paciente.isEmpty()) {
                ps.setString(index++, "%" + paciente + "%");
                ps.setString(index++, "%" + paciente + "%");
            }
            if (medico != null && !medico.isEmpty()) {
                ps.setString(index++, "%" + medico + "%");
                ps.setString(index++, "%" + medico + "%");
            }
            if (estado != null && estado != 0) {
                ps.setInt(index++, estado);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listarConFiltros: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    public List<Cita> listarTodos() {
        return listarConFiltros(null, null, 0);
    }

    public void eliminar(int idCita) {
        String sql = "DELETE FROM cita WHERE id_cita = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCita);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public List<String> obtenerHorasOcupadas(int idMedico, Date fecha) {
        List<String> horas = new ArrayList<>();
        String sql = "SELECT fecha_hora FROM cita WHERE id_doctor = ? " +
                     "AND DATE(fecha_hora) = DATE(?) AND id_estado_cita != 5";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMedico);
            ps.setDate(2, new java.sql.Date(fecha.getTime()));
            rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_hora");
                if (ts != null) horas.add(sdf.format(ts));
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerHorasOcupadas: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return horas;
    }

    public boolean existeCitaEnHorario(int idMedico, Date fechaHora) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM cita WHERE id_doctor = ? AND fecha_hora = ? AND id_estado_cita != 5";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMedico);
            ps.setTimestamp(2, new Timestamp(fechaHora.getTime()));
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) existe = true;
        } catch (SQLException e) {
            System.out.println("Error existeCitaEnHorario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return existe;
    }

    public void guardar(Cita c) {
        String sql = "INSERT INTO cita (id_paciente, id_doctor, fecha_hora, notas_paciente, id_estado_cita, fecha_creacion) VALUES (?, ?, ?, ?, ?, NOW())";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId_paciente());
            ps.setInt(2, c.getId_doctor());
            if (c.getFecha_hora() != null) ps.setTimestamp(3, new java.sql.Timestamp(c.getFecha_hora().getTime()));
            else ps.setNull(3, java.sql.Types.TIMESTAMP);
            ps.setString(4, c.getNotas_paciente());
            ps.setInt(5, c.getId_estado_cita());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void actualizar(Cita c) {
        String sql = "UPDATE cita SET id_paciente=?, id_doctor=?, fecha_hora=?, notas_paciente=?, id_estado_cita=?, fecha_actualizacion=NOW() WHERE id_cita=?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId_paciente());
            ps.setInt(2, c.getId_doctor());
            if (c.getFecha_hora() != null) ps.setTimestamp(3, new java.sql.Timestamp(c.getFecha_hora().getTime()));
            else ps.setNull(3, java.sql.Types.TIMESTAMP);
            ps.setString(4, c.getNotas_paciente());
            ps.setInt(5, c.getId_estado_cita());
            ps.setInt(6, c.getId_cita());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar Cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public List<Cita> listarPorFiltrosDashboard(Integer idDoctor, Integer idPaciente, boolean futuras) {
        List<Cita> lista = new ArrayList<>();
        String operador = futuras ? ">=" : "<";
        String orden = futuras ? "ASC" : "DESC";
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT c.*, ec.nombre_estado, ec.color, ");
        sql.append(" u_pac.nombre as nom_pac, u_pac.apellidos as ape_pac, ");
        sql.append(" u_med.nombre as nom_med, u_med.apellidos as ape_med ");
        sql.append("FROM cita c ");
        sql.append("INNER JOIN estado_cita ec ON c.id_estado_cita = ec.id_estado_cita ");
        sql.append("INNER JOIN paciente p ON c.id_paciente = p.id_paciente ");
        sql.append("INNER JOIN usuario u_pac ON p.id_usuario = u_pac.id_usuario ");
        sql.append("INNER JOIN medico m ON c.id_doctor = m.id_doctor ");
        sql.append("INNER JOIN usuario u_med ON m.id_usuario = u_med.id_usuario ");
        sql.append("WHERE c.fecha_hora ").append(operador).append(" NOW() ");

        if (idDoctor != null) sql.append(" AND c.id_doctor = ? ");
        if (idPaciente != null) sql.append(" AND c.id_paciente = ? ");
        
        sql.append(" AND c.id_estado_cita != 5 ");
        sql.append(" ORDER BY c.fecha_hora ").append(orden).append(" LIMIT 10");

        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql.toString());
            int index = 1;
            if (idDoctor != null) ps.setInt(index++, idDoctor);
            if (idPaciente != null) ps.setInt(index++, idPaciente);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCita(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error Dashboard: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    public Cita buscar(int id) {
        Cita c = null;
        String sql = "SELECT * FROM cita WHERE id_cita = ?"; 
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                c = new Cita();
                c.setId_cita(rs.getInt("id_cita"));
                c.setId_paciente(rs.getInt("id_paciente"));
                c.setId_doctor(rs.getInt("id_doctor"));
                c.setFecha_hora(rs.getTimestamp("fecha_hora"));
                c.setNotas_paciente(rs.getString("notas_paciente"));
                c.setId_estado_cita(rs.getInt("id_estado_cita"));
                Paciente p = new Paciente(); p.setId_paciente(c.getId_paciente()); c.setPaciente(p);
                Medico m = new Medico(); m.setId_doctor(c.getId_doctor()); c.setMedico(m);
                EstadoCita ec = new EstadoCita(); ec.setId_estado_cita(c.getId_estado_cita()); c.setEstadoCita(ec);
            }
        } catch (SQLException e) {
            System.out.println("Error buscar Cita: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return c;
    }

    private Cita mapearCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setId_cita(rs.getInt("id_cita"));
        c.setFecha_hora(rs.getTimestamp("fecha_hora"));
        c.setNotas_paciente(rs.getString("notas_paciente"));
        c.setId_paciente(rs.getInt("id_paciente"));
        c.setId_doctor(rs.getInt("id_doctor"));
        c.setId_estado_cita(rs.getInt("id_estado_cita"));

        EstadoCita ec = new EstadoCita();
        ec.setId_estado_cita(rs.getInt("id_estado_cita"));
        ec.setNombre_estado(rs.getString("nombre_estado"));
        ec.setColor(rs.getString("color"));
        c.setEstadoCita(ec);

        Paciente p = new Paciente();
        p.setId_paciente(rs.getInt("id_paciente"));
        Usuario uPac = new Usuario();
        uPac.setNombre(rs.getString("nom_pac"));
        uPac.setApellidos(rs.getString("ape_pac"));
        p.setUsuario(uPac);
        c.setPaciente(p);

        Medico m = new Medico();
        m.setId_doctor(rs.getInt("id_doctor"));
        Usuario uMed = new Usuario();
        uMed.setNombre(rs.getString("nom_med"));
        uMed.setApellidos(rs.getString("ape_med"));
        m.setUsuario(uMed);
        c.setMedico(m);

        return c;
    }

    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}