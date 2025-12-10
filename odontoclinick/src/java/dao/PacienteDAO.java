package dao;

import modelo.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/*
imports innecesarios, imports de modelos
import modelo.Usuario;
*/

public class PacienteDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Listar todos los pacientes
    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM paciente";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId_paciente(rs.getInt("id_paciente"));
                p.setId_usuario(rs.getInt("id_usuario"));
                p.setDireccion(rs.getString("direccion"));
                p.setEps(rs.getString("eps"));
                p.setRh(rs.getString("rh"));
                p.setAlergias(rs.getString("alergias"));
                p.setEnfermedades_preexistentes(rs.getString("enfermedades_preexistentes"));
                p.setContacto_emergencia_nombre(rs.getString("contacto_emergencia_nombre"));
                p.setContacto_emergencia_telefono(rs.getString("contacto_emergencia_telefono"));
                p.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                p.setFecha_registro(rs.getDate("fecha_registro"));

                // Relación con usuario
                p.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));

                pacientes.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pacientes: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return pacientes;
    }

    // Buscar paciente por ID
    public Paciente buscar(int id) {
        Paciente p = null;
        String sql = "SELECT * FROM paciente WHERE id_paciente = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                p = new Paciente();
                p.setId_paciente(rs.getInt("id_paciente"));
                p.setId_usuario(rs.getInt("id_usuario"));
                p.setDireccion(rs.getString("direccion"));
                p.setEps(rs.getString("eps"));
                p.setRh(rs.getString("rh"));
                p.setAlergias(rs.getString("alergias"));
                p.setEnfermedades_preexistentes(rs.getString("enfermedades_preexistentes"));
                p.setContacto_emergencia_nombre(rs.getString("contacto_emergencia_nombre"));
                p.setContacto_emergencia_telefono(rs.getString("contacto_emergencia_telefono"));
                p.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                p.setFecha_registro(rs.getDate("fecha_registro"));

                p.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar paciente: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return p;
    }

    // Guardar paciente
    public void guardar(Paciente p) {
        String sql = "INSERT INTO paciente(id_usuario, direccion, eps, rh, alergias, enfermedades_preexistentes, "
                   + "contacto_emergencia_nombre, contacto_emergencia_telefono, fecha_nacimiento, fecha_registro) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getId_usuario());
            ps.setString(2, p.getDireccion());
            ps.setString(3, p.getEps());
            ps.setString(4, p.getRh());
            ps.setString(5, p.getAlergias());
            ps.setString(6, p.getEnfermedades_preexistentes());
            ps.setString(7, p.getContacto_emergencia_nombre());
            ps.setString(8, p.getContacto_emergencia_telefono());
            ps.setDate(9, new Date(p.getFecha_nacimiento().getTime()));
            ps.setDate(10, new Date(p.getFecha_registro().getTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar paciente: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar paciente
    public void actualizar(Paciente p) {
        String sql = "UPDATE paciente SET direccion = ?, eps = ?, rh = ?, alergias = ?, enfermedades_preexistentes = ?, "
                   + "contacto_emergencia_nombre = ?, contacto_emergencia_telefono = ?, fecha_nacimiento = ?, fecha_registro = ? "
                   + "WHERE id_paciente = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, p.getDireccion());
            ps.setString(2, p.getEps());
            ps.setString(3, p.getRh());
            ps.setString(4, p.getAlergias());
            ps.setString(5, p.getEnfermedades_preexistentes());
            ps.setString(6, p.getContacto_emergencia_nombre());
            ps.setString(7, p.getContacto_emergencia_telefono());
            ps.setDate(8, new Date(p.getFecha_nacimiento().getTime()));
            ps.setDate(9, new Date(p.getFecha_registro().getTime()));
            ps.setInt(10, p.getId_paciente());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar paciente: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar paciente
    public void eliminar(int id) {
        String sql = "DELETE FROM paciente WHERE id_paciente = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar paciente: " + e.getMessage());
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
    // AGREGAR EN: dao/PacienteDAO.java

public Paciente buscarPorIdUsuario(int idUsuario) {
    Paciente p = null;
    // Corrección: Usamos 'pacientes' (plural) según tu SQL
    String sql = "SELECT * FROM pacientes WHERE id_usuario = ?";
    
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        conn = ds.getConnection(); // Usando tu DataSource configurado
        ps = conn.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        rs = ps.executeQuery();

        if (rs.next()) {
            p = new Paciente();
            p.setId_paciente(rs.getInt("id_paciente"));
            p.setId_usuario(rs.getInt("id_usuario"));
            p.setDireccion(rs.getString("direccion"));
            p.setEps(rs.getString("eps"));
            p.setRh(rs.getString("rh"));
            p.setAlergias(rs.getString("alergias"));
            p.setEnfermedades_preexistentes(rs.getString("enfermedades_preexistentes"));
            p.setContacto_emergencia_nombre(rs.getString("contacto_emergencia_nombre"));
            p.setContacto_emergencia_telefono(rs.getString("contacto_emergencia_telefono"));
            p.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
            p.setFecha_registro(rs.getDate("fecha_registro"));

            // Cargar el objeto Usuario completo para mostrar nombres
            p.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
        }
    } catch (SQLException e) {
        System.out.println("Error al buscar paciente por usuario: " + e.getMessage());
    } finally {
        // Asegúrate de tener un método cerrarRecursos(conn, ps, rs) o cerrarlos manualmente
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return p;
}
}
