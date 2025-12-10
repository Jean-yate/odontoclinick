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
    // AGREGAR EN: dao/CitaDAO.java

public List<Cita> listarPorFiltrosDashboard(Integer idDoctor, Integer idPaciente, boolean futuras) {
    List<Cita> lista = new ArrayList<>();
    
    // Si futuras = true, buscamos fechas mayores o iguales a hoy. Si no, menores.
    String operador = futuras ? ">=" : "<";
    String orden = futuras ? "ASC" : "DESC";

    // Corrección: Usamos 'citas' (plural) según tu SQL
    StringBuilder sql = new StringBuilder("SELECT * FROM cita WHERE fecha_hora " + operador + " NOW() ");

    if (idDoctor != null) {
        sql.append(" AND id_doctor = ? ");
    }
    if (idPaciente != null) {
        sql.append(" AND id_paciente = ? ");
    }
    // Ocultar canceladas (opcional, ajusta según tu lógica)
    sql.append(" AND id_estado_cita NOT IN (5, 6) "); 
    
    sql.append(" ORDER BY fecha_hora ").append(orden);


    try {
        conn = ds.getConnection();
        ps = conn.prepareStatement(sql.toString());

        int index = 1;
        if (idDoctor != null) ps.setInt(index++, idDoctor);
        if (idPaciente != null) ps.setInt(index++, idPaciente);

        rs = ps.executeQuery();

        while (rs.next()) {
            Cita c = new Cita();
            c.setId_cita(rs.getInt("id_cita"));
            c.setId_paciente(rs.getInt("id_paciente"));
            c.setId_doctor(rs.getInt("id_doctor"));
            c.setFecha_hora(rs.getTimestamp("fecha_hora"));
            c.setId_estado_cita(rs.getInt("id_estado_cita"));
            c.setNotas_doctor(rs.getString("notas_doctor"));
            c.setNotas_paciente(rs.getString("notas_paciente"));

            // Cargar relaciones para la vista
            c.setPaciente(pacienteDAO.buscar(c.getId_paciente()));
            c.setMedico(medicoDAO.buscar(c.getId_doctor()));
            c.setEstadoCita(estadoCitaDAO.buscar(c.getId_estado_cita()));

            lista.add(c);
        }
    } catch (SQLException e) {
        System.out.println("Error listar dashboard: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return lista;
}
    // AGREGAR EN: dao/CitaDAO.java

// 1. Listar con filtros (Reemplaza CitaController@index)
public List<Cita> listarConFiltros(String textoPaciente, String textoMedico, Integer idEstado) {
    List<Cita> lista = new ArrayList<>();
    
    // JOINs necesarios para buscar por nombre (que está en la tabla usuarios)
    StringBuilder sql = new StringBuilder(
        "SELECT c.* FROM cita c " +
        "JOIN paciente p ON c.id_paciente = p.id_paciente " +
        "JOIN usuario up ON p.id_usuario = up.id_usuario " + // Usuario Paciente
        "JOIN medico m ON c.id_doctor = m.id_doctor " +
        "JOIN usuario um ON m.id_usuario = um.id_usuario " + // Usuario Medico
        "WHERE 1=1 "
    );

    if (textoPaciente != null && !textoPaciente.isEmpty()) {
        sql.append(" AND (up.nombre LIKE ? OR up.apellidos LIKE ?) ");
    }
    if (textoMedico != null && !textoMedico.isEmpty()) {
        sql.append(" AND (um.nombre LIKE ? OR um.apellidos LIKE ?) ");
    }
    if (idEstado != null && idEstado > 0) {
        sql.append(" AND c.id_estado_cita = ? ");
    }
    
    sql.append(" ORDER BY c.fecha_hora ASC");


    try {
        conn = ds.getConnection();
        ps = conn.prepareStatement(sql.toString());
        
        int index = 1;
        if (textoPaciente != null && !textoPaciente.isEmpty()) {
            ps.setString(index++, "%" + textoPaciente + "%");
            ps.setString(index++, "%" + textoPaciente + "%");
        }
        if (textoMedico != null && !textoMedico.isEmpty()) {
            ps.setString(index++, "%" + textoMedico + "%");
            ps.setString(index++, "%" + textoMedico + "%");
        }
        if (idEstado != null && idEstado > 0) {
            ps.setInt(index++, idEstado);
        }

        rs = ps.executeQuery();
        while (rs.next()) {
            Cita c = new Cita();
            c.setId_cita(rs.getInt("id_cita"));
            c.setId_paciente(rs.getInt("id_paciente"));
            c.setId_doctor(rs.getInt("id_doctor"));
            c.setFecha_hora(rs.getTimestamp("fecha_hora"));
            c.setId_estado_cita(rs.getInt("id_estado_cita"));
            c.setNotas_doctor(rs.getString("notas_doctor"));
            c.setNotas_paciente(rs.getString("notas_paciente"));
            
            // Cargar objetos completos para mostrar en la tabla
            c.setPaciente(pacienteDAO.buscar(c.getId_paciente()));
            c.setMedico(medicoDAO.buscar(c.getId_doctor()));
            c.setEstadoCita(estadoCitaDAO.buscar(c.getId_estado_cita()));
            
            lista.add(c);
        }
    } catch (SQLException e) {
        System.out.println("Error listarConFiltros: " + e.getMessage());
    } finally {
        // Cerrar recursos manualmente o usar tu método cerrarRecursos()
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return lista;
}

// 2. Validar duplicados (Reemplaza la lógica $existeCita en store)
public boolean existeCitaEnHorario(int idDoctor, java.util.Date fechaHora) {
    boolean existe = false;
    // Estado 5=Cancelada, 6=No asistió (ajustar IDs según tu tabla estados_cita)
    String sql = "SELECT COUNT(*) FROM cita WHERE id_doctor = ? AND fecha_hora = ? AND id_estado_cita NOT IN (5, 6)";
    
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        conn = ds.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, idDoctor);
        // Convertir java.util.Date a Timestamp para comparar fecha y hora exacta
        ps.setTimestamp(2, new java.sql.Timestamp(fechaHora.getTime()));
        
        rs = ps.executeQuery();
        if (rs.next()) {
            existe = rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.out.println("Error existeCitaEnHorario: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return existe;
}

// 3. Obtener citas ocupadas de un día (Para calcular huecos disponibles)
public List<String> obtenerHorasOcupadas(int idDoctor, java.util.Date fecha) {
    List<String> horas = new ArrayList<>();
    String sql = "SELECT DATE_FORMAT(fecha_hora, '%H:%i') as hora FROM cita WHERE id_doctor = ? AND DATE(fecha_hora) = ? AND id_estado_cita NOT IN (5, 6)";
    

    try {
        conn = ds.getConnection();
        ps = conn.prepareStatement(sql);
        ps.setInt(1, idDoctor);
        ps.setDate(2, new java.sql.Date(fecha.getTime())); // Solo la parte de fecha
        
        rs = ps.executeQuery();
        while (rs.next()) {
            horas.add(rs.getString("hora"));
        }
    } catch (SQLException e) {
        System.out.println("Error obtenerHorasOcupadas: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return horas;
}
}
