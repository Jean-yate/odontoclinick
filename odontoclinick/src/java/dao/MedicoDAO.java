package dao;

import modelo.Medico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import modelo.Usuario;
/*
imports innecesarios, imports de relacionalidad
import dao.UsuarioDAO;
import modelo.Usuario;
import modelo.Especialidad;
*/

public class MedicoDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    // Listar todos los médicos
    public List<Medico> listarTodos() {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT * FROM medico";

        try {
            conn = ds.getConnection();
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

                // Relaciones
                m.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
                m.setEspecialidad(especialidadDAO.buscar(rs.getInt("id_especialidad")));

                medicos.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar médicos: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return medicos;
    }
    // Buscar médico por ID
    // Guardar médico
    public void guardar(Medico m) {
        String sql = "INSERT INTO medico(id_usuario, id_especialidad, anos_experiencia, licencia_medica, fecha_ingreso) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try {
            conn = ds.getConnection();
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

    // Actualizar médico
    public void actualizar(Medico m) {
        String sql = "UPDATE medico SET id_usuario = ?, id_especialidad = ?, anos_experiencia = ?, "
                   + "licencia_medica = ?, fecha_ingreso = ? WHERE id_doctor = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getId_usuario());
            ps.setInt(2, m.getId_especialidad());
            ps.setInt(3, m.getAnos_experiencia());
            ps.setString(4, m.getLicencia_medica());
            ps.setDate(5, new Date(m.getFecha_ingreso().getTime()));
            ps.setInt(6, m.getId_doctor());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar médico: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar médico
    public void eliminar(int id) {
        String sql = "DELETE FROM medico WHERE id_doctor = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar médico: " + e.getMessage());
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

    Medico buscar(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    // AGREGAR EN: dao/MedicoDAO.java

public Medico buscarPorIdUsuario(int idUsuario) {
    Medico m = null;
    // Corrección: Usamos 'medicos' (plural) según tu SQL
    String sql = "SELECT * FROM medico WHERE id_usuario = ?";

    try {
        conn = ds.getConnection();
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
            
            // Cargar relaciones
            m.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
            m.setEspecialidad(especialidadDAO.buscar(rs.getInt("id_especialidad")));
        }
    } catch (SQLException e) {
        System.out.println("Error al buscar médico por usuario: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return m;
}

    public boolean registrarMedicoTransaccion(Usuario u, Medico m) throws SQLException {
        boolean exito = false;
        Connection connTx = null;
        PreparedStatement psUser = null;
        PreparedStatement psMed = null;
        ResultSet rsKeys = null;

        try {
            // 1. Usamos tu conexión (Asegúrate que dao.Conexion.conectar() funcione o usa ds.getConnection())
            connTx = dao.Conexion.conectar(); 
            // O si usas DataSource: connTx = ds.getConnection();
            
            connTx.setAutoCommit(false);

            // ---------------------------------------------------------
            // PASO A: Insertar el Usuario (Rol 2 = Doctor)
            // ---------------------------------------------------------
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 2, 1, NOW())";
            
            psUser = connTx.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            // Obtener el ID que la base de datos le asignó al nuevo usuario
            rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) {
                idGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo generar el ID del usuario médico.");
            }

            // ---------------------------------------------------------
            // PASO B: Insertar el Médico usando el ID del paso A
            // ---------------------------------------------------------
            String sqlMed = "INSERT INTO medico (id_usuario, id_especialidad, licencia_medica, anos_experiencia, fecha_ingreso) VALUES (?, ?, ?, ?, ?)";
            
            psMed = connTx.prepareStatement(sqlMed);
            psMed.setInt(1, idGenerado);
            psMed.setInt(2, m.getId_especialidad());
            psMed.setString(3, m.getLicencia_medica());
            psMed.setInt(4, m.getAnos_experiencia());
            
            // Manejo seguro de la fecha de ingreso
            if (m.getFecha_ingreso() != null) {
                psMed.setDate(5, new java.sql.Date(m.getFecha_ingreso().getTime()));
            } else {
                psMed.setDate(5, new java.sql.Date(System.currentTimeMillis())); // Fecha hoy
            }
            
            psMed.executeUpdate();

            // ---------------------------------------------------------
            // PASO C: Confirmar todo
            // ---------------------------------------------------------
            connTx.commit(); 
            exito = true;

        } catch (SQLException e) {
            // Si algo falla, deshacemos TODO (ni usuario ni médico se guardan)
            if (connTx != null) {
                try { connTx.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace(); // Imprime el error para que sepas qué pasó
            throw e; // Avisamos al Bean que falló
        } finally {
            // Cerrar recursos manuales
            try { if (rsKeys != null) rsKeys.close(); } catch (SQLException e) {}
            try { if (psUser != null) psUser.close(); } catch (SQLException e) {}
            try { if (psMed != null) psMed.close(); } catch (SQLException e) {}
            try { 
                if (connTx != null) {
                    connTx.setAutoCommit(true);
                    connTx.close(); 
                }
            } catch (SQLException e) {}
        }
        return exito;
    }

}

