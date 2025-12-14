package dao;

import modelo.Medico;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    // 1. ELIMINAMOS @Resource private DataSource ds;
    
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    // Listar todos los médicos
    public List<Medico> listarTodos() {
        List<Medico> medicos = new ArrayList<>();
        // Hacemos JOIN para traer datos del usuario y la especialidad de una vez
        // Esto es mucho más rápido y evita errores de conexión anidados
        String sql = "SELECT m.*, u.nombre, u.apellidos, e.nombre_especialidad " +
                     "FROM medico m " +
                     "JOIN usuario u ON m.id_usuario = u.id_usuario " +
                     "JOIN especialidad e ON m.id_especialidad = e.id_especialidad";

        try {
            conn = Conexion.conectar(); // USAMOS LA CLASE CONEXION
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Medico m = new Medico();
                m.setId_doctor(rs.getInt("id_doctor"));
                m.setId_usuario(rs.getInt("id_usuario"));
                m.setId_especialidad(rs.getInt("id_especialidad"));
                m.setAnos_experiencia(rs.getInt("anos_experiencia"));
                m.setLicencia_medica(rs.getString("licencia_medica"));
                m.setFecha_ingreso(rs.getDate("fecha_ingreso"));

                // Llenamos datos básicos del usuario para el Combo Box (SelectOneMenu)
                modelo.Usuario u = new modelo.Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                m.setUsuario(u);

                // Llenamos datos de especialidad
                modelo.Especialidad e = new modelo.Especialidad();
                e.setId_especialidad(rs.getInt("id_especialidad"));
                e.setNombre_especialidad(rs.getString("nombre_especialidad"));
                m.setEspecialidad(e);

                medicos.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar médicos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }

        return medicos;
    }

    // Buscar médico por ID (Implementación corregida)
    public Medico buscar(int id) {
        Medico m = null;
        String sql = "SELECT * FROM medico WHERE id_doctor = ?";

        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                m = new Medico();
                m.setId_doctor(rs.getInt("id_doctor"));
                m.setId_usuario(rs.getInt("id_usuario"));
                m.setId_especialidad(rs.getInt("id_especialidad"));
                m.setAnos_experiencia(rs.getInt("anos_experiencia"));
                m.setLicencia_medica(rs.getString("licencia_medica"));
                m.setFecha_ingreso(rs.getDate("fecha_ingreso"));

                // Aquí sí llamamos a los DAOs auxiliares porque es solo 1 registro
                m.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
                m.setEspecialidad(especialidadDAO.buscar(rs.getInt("id_especialidad")));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar médico: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return m;
    }

    // Buscar por Usuario ID
    public Medico buscarPorIdUsuario(int idUsuario) {
        Medico m = null;
        String sql = "SELECT * FROM medico WHERE id_usuario = ?";

        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                m = new Medico();
                m.setId_doctor(rs.getInt("id_doctor"));
                m.setId_usuario(rs.getInt("id_usuario"));
                m.setId_especialidad(rs.getInt("id_especialidad"));
                m.setLicencia_medica(rs.getString("licencia_medica"));
                m.setAnos_experiencia(rs.getInt("anos_experiencia"));
                m.setFecha_ingreso(rs.getDate("fecha_ingreso"));
                
                m.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
                m.setEspecialidad(especialidadDAO.buscar(rs.getInt("id_especialidad")));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar médico por usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return m;
    }

    // Guardar (Simple)
    public void guardar(Medico m) {
        String sql = "INSERT INTO medico(id_usuario, id_especialidad, anos_experiencia, licencia_medica, fecha_ingreso) VALUES (?, ?, ?, ?, ?)";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getId_usuario());
            ps.setInt(2, m.getId_especialidad());
            ps.setInt(3, m.getAnos_experiencia());
            ps.setString(4, m.getLicencia_medica());
            ps.setDate(5, new Date(m.getFecha_ingreso().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar médico: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }
    
    // Actualizar
    public void actualizar(Medico m) {
        String sql = "UPDATE medico SET id_especialidad = ?, anos_experiencia = ?, licencia_medica = ?, fecha_ingreso = ? WHERE id_doctor = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getId_especialidad());
            ps.setInt(2, m.getAnos_experiencia());
            ps.setString(3, m.getLicencia_medica());
            ps.setDate(4, new Date(m.getFecha_ingreso().getTime()));
            ps.setInt(5, m.getId_doctor());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar médico: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }
    
    // Transacción Compleja (Registrar Usuario + Médico)
    public boolean registrarMedicoTransaccion(Usuario u, Medico m) throws SQLException {
        boolean exito = false;
        Connection connTx = null;
        PreparedStatement psUser = null;
        PreparedStatement psMed = null;
        ResultSet rsKeys = null;

        try {
            connTx = Conexion.conectar(); // CONEXION DEL POOL
            connTx.setAutoCommit(false);

            // 1. Usuario
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 2, 1, NOW())";
            psUser = connTx.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) {
                idGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo generar el ID del usuario médico.");
            }

            // 2. Médico
            String sqlMed = "INSERT INTO medico (id_usuario, id_especialidad, licencia_medica, anos_experiencia, fecha_ingreso) VALUES (?, ?, ?, ?, ?)";
            psMed = connTx.prepareStatement(sqlMed);
            psMed.setInt(1, idGenerado);
            psMed.setInt(2, m.getId_especialidad());
            psMed.setString(3, m.getLicencia_medica());
            psMed.setInt(4, m.getAnos_experiencia());
            
            if (m.getFecha_ingreso() != null) {
                psMed.setDate(5, new java.sql.Date(m.getFecha_ingreso().getTime()));
            } else {
                psMed.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            }
            psMed.executeUpdate();

            connTx.commit();
            exito = true;

        } catch (SQLException e) {
            if (connTx != null) connTx.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            try { if (rsKeys != null) rsKeys.close(); } catch (Exception e) {}
            try { if (psUser != null) psUser.close(); } catch (Exception e) {}
            try { if (psMed != null) psMed.close(); } catch (Exception e) {}
            try { 
                if (connTx != null) {
                    connTx.setAutoCommit(true); // Restaurar estado
                    connTx.close(); 
                }
            } catch (Exception e) {}
        }
        return exito;
    }

    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}