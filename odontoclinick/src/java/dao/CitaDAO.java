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

    // --- 1. LISTAR CON FILTROS (Para la Tabla Principal del Bean) ---
    public List<Cita> listarConFiltros(String paciente, String medico, Integer estado) {
        List<Cita> lista = new ArrayList<>();
        
        // Hacemos JOINs para traer los nombres del Paciente, del Médico y el Estado
        // Esto permite mostrar #{cita.paciente.usuario.nombre} en la tabla sin hacer 1000 consultas
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

        // Lógica dinámica de filtros
        if (paciente != null && !paciente.isEmpty()) {
            sql.append("AND (u_pac.nombre LIKE ? OR u_pac.apellidos LIKE ?) ");
        }
        if (medico != null && !medico.isEmpty()) {
            sql.append("AND (u_med.nombre LIKE ? OR u_med.apellidos LIKE ?) ");
        }
        if (estado != null && estado != 0) { // Asumiendo que 0 es "todos"
            sql.append("AND c.id_estado_cita = ? ");
        }
        
        sql.append("ORDER BY c.fecha_hora ASC");

        try {
            conn = Conexion.conectar(); // IMPORTANTE: Usamos la conexión correcta
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
                Cita c = new Cita();
                c.setId_cita(rs.getInt("id_cita"));
                c.setFecha_hora(rs.getTimestamp("fecha_hora"));
                c.setNotas_paciente(rs.getString("notas_paciente"));
                c.setId_paciente(rs.getInt("id_paciente"));
                c.setId_doctor(rs.getInt("id_doctor"));
                c.setId_estado_cita(rs.getInt("id_estado_cita"));

                // Mapear Estado
                EstadoCita ec = new EstadoCita();
                ec.setId_estado_cita(rs.getInt("id_estado_cita"));
                ec.setNombre_estado(rs.getString("nombre_estado"));
                ec.setColor(rs.getString("color"));
                c.setEstadoCita(ec);

                // Mapear Paciente (Solo nombres para visualización rápida)
                Paciente p = new Paciente();
                p.setId_paciente(rs.getInt("id_paciente"));
                Usuario uPac = new Usuario();
                uPac.setNombre(rs.getString("nom_pac"));
                uPac.setApellidos(rs.getString("ape_pac"));
                p.setUsuario(uPac);
                c.setPaciente(p);

                // Mapear Médico
                Medico m = new Medico();
                m.setId_doctor(rs.getInt("id_doctor"));
                Usuario uMed = new Usuario();
                uMed.setNombre(rs.getString("nom_med"));
                uMed.setApellidos(rs.getString("ape_med"));
                m.setUsuario(uMed);
                c.setMedico(m);

                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error listarConFiltros Citas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    // --- 2. OBTENER HORAS OCUPADAS (Para calcular disponibilidad) ---
    public List<String> obtenerHorasOcupadas(int idMedico, Date fecha) {
        List<String> horas = new ArrayList<>();
        // Buscamos citas activas (no canceladas) para ese médico en ese día
        // Asumo que ID 5 es "Cancelada". Ajusta si es diferente.
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
                if (ts != null) {
                    horas.add(sdf.format(ts));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerHorasOcupadas: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return horas;
    }

    // --- 3. VERIFICAR DUPLICADOS ---
    public boolean existeCitaEnHorario(int idMedico, Date fechaHora) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM cita WHERE id_doctor = ? AND fecha_hora = ? AND id_estado_cita != 5";
        
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMedico);
            ps.setTimestamp(2, new Timestamp(fechaHora.getTime()));
            rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            System.out.println("Error existeCitaEnHorario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return existe;
    }

    // --- 4. GUARDAR CITA (Corregido) ---
    public void guardar(Cita c) {
        String sql = "INSERT INTO cita (id_paciente, id_doctor, fecha_hora, notas_paciente, id_estado_cita, fecha_creacion) " +
                     "VALUES (?, ?, ?, ?, ?, NOW())";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId_paciente());
            ps.setInt(2, c.getId_doctor());
            
            // --- AQUÍ ESTABA EL ERROR ---
            // Convertimos java.util.Date a java.sql.Timestamp manualmente
            if (c.getFecha_hora() != null) {
                ps.setTimestamp(3, new java.sql.Timestamp(c.getFecha_hora().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }
            // ----------------------------
            
            ps.setString(4, c.getNotas_paciente());
            ps.setInt(5, c.getId_estado_cita());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Cita: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }
    }

    // --- 5. ACTUALIZAR CITA (Corregido) ---
    public void actualizar(Cita c) {
        String sql = "UPDATE cita SET id_paciente=?, id_doctor=?, fecha_hora=?, notas_paciente=?, id_estado_cita=?, fecha_actualizacion=NOW() " +
                     "WHERE id_cita=?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, c.getId_paciente());
            ps.setInt(2, c.getId_doctor());

            // --- MISMA CORRECCIÓN AQUÍ ---
            if (c.getFecha_hora() != null) {
                ps.setTimestamp(3, new java.sql.Timestamp(c.getFecha_hora().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }
            // -----------------------------

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
// --- AGREGAR ESTO EN CitaDAO.java (AL FINAL) ---

    // Método para el Dashboard (filtra por doctor, paciente y si es futura o pasada)
    public List<Cita> listarPorFiltrosDashboard(Integer idDoctor, Integer idPaciente, boolean futuras) {
        List<Cita> lista = new ArrayList<>();
        
        // Si es 'futuras' buscamos fecha >= HOY, si no, fecha < HOY
        String operador = futuras ? ">=" : "<";
        // Las futuras se ordenan de la más cercana a la lejana (ASC)
        // Las pasadas se ordenan de la más reciente a la más vieja (DESC)
        String orden = futuras ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM cita WHERE fecha_hora ").append(operador).append(" NOW() ");

        // Filtros dinámicos
        if (idDoctor != null) {
            sql.append(" AND id_doctor = ? ");
        }
        if (idPaciente != null) {
            sql.append(" AND id_paciente = ? ");
        }
        
        // Opcional: No mostrar las citas canceladas (id 5) en el dashboard
        sql.append(" AND id_estado_cita != 5 ");
        
        sql.append(" ORDER BY fecha_hora ").append(orden);
        // Limitar a 10 para no saturar el dashboard
        sql.append(" LIMIT 10");

        Connection localConn = null;
        PreparedStatement localPs = null;
        ResultSet localRs = null;

        try {
            localConn = Conexion.conectar();
            localPs = localConn.prepareStatement(sql.toString());

            int index = 1;
            if (idDoctor != null) {
                localPs.setInt(index++, idDoctor);
            }
            if (idPaciente != null) {
                localPs.setInt(index++, idPaciente);
            }

            localRs = localPs.executeQuery();

            // Instanciamos DAOs auxiliares AQUÍ para cargar los nombres
            // (Es ineficiente instanciarlos por cada fila, pero seguro para evitar loops)
            PacienteDAO pDao = new PacienteDAO();
            MedicoDAO mDao = new MedicoDAO();
            EstadoCitaDAO eDao = new EstadoCitaDAO();

            while (localRs.next()) {
                Cita c = new Cita();
                c.setId_cita(localRs.getInt("id_cita"));
                c.setId_paciente(localRs.getInt("id_paciente"));
                c.setId_doctor(localRs.getInt("id_doctor"));
                c.setFecha_hora(localRs.getTimestamp("fecha_hora"));
                c.setNotas_paciente(localRs.getString("notas_paciente"));
                c.setId_estado_cita(localRs.getInt("id_estado_cita"));

                // Cargar datos relacionales para mostrar nombres en el Dashboard
                try {
                    c.setPaciente(pDao.buscar(c.getId_paciente()));
                    c.setMedico(mDao.buscar(c.getId_doctor()));
                    c.setEstadoCita(eDao.buscar(c.getId_estado_cita()));
                } catch (Exception ex) {
                    // Ignorar error de carga secundaria
                }

                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error listarPorFiltrosDashboard: " + e.getMessage());
        } finally {
            try { if (localRs != null) localRs.close(); } catch (Exception e) {}
            try { if (localPs != null) localPs.close(); } catch (Exception e) {}
            try { if (localConn != null) localConn.close(); } catch (Exception e) {}
        }
        return lista;
    }
    
    // Buscar Cita por ID (Necesario para CitaTratamientoDAO)
    public Cita buscar(int id) {
        Cita c = null;
        // Hacemos JOIN básico para que no de NullPointer si accedes al paciente
        String sql = "SELECT c.*, " +
                     " p.id_usuario as id_usu_pac, m.id_usuario as id_usu_med " +
                     " FROM cita c " +
                     " LEFT JOIN paciente p ON c.id_paciente = p.id_paciente " +
                     " LEFT JOIN medico m ON c.id_doctor = m.id_doctor " +
                     " WHERE c.id_cita = ?";

        Connection localConn = null;
        PreparedStatement localPs = null;
        ResultSet localRs = null;

        try {
            localConn = Conexion.conectar();
            localPs = localConn.prepareStatement(sql);
            localPs.setInt(1, id);
            localRs = localPs.executeQuery();

            if (localRs.next()) {
                c = new Cita();
                c.setId_cita(localRs.getInt("id_cita"));
                c.setId_paciente(localRs.getInt("id_paciente"));
                c.setId_doctor(localRs.getInt("id_doctor"));
                c.setFecha_hora(localRs.getTimestamp("fecha_hora"));
                c.setNotas_paciente(localRs.getString("notas_paciente"));
                c.setId_estado_cita(localRs.getInt("id_estado_cita"));
                
                // NOTA: No cargamos todos los objetos anidados pesados aquí para evitar recursividad infinita
                // Si necesitas el nombre del paciente, tendrías que llamar a pacienteDAO.buscar()
                // pero CUIDADO con los loops (Cita -> Paciente -> Cita...)
            }
        } catch (SQLException e) {
            System.out.println("Error buscando Cita por ID: " + e.getMessage());
        } finally {
            try { if (localRs != null) localRs.close(); } catch (Exception e) {}
            try { if (localPs != null) localPs.close(); } catch (Exception e) {}
            try { if (localConn != null) localConn.close(); } catch (Exception e) {}
        }
        return c;
    }

    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}