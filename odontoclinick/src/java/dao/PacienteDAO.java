package dao;

import modelo.Paciente;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
public class PacienteDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre, u.apellidos FROM paciente p " +
                     "INNER JOIN usuario u ON p.id_usuario = u.id_usuario " +
                     "WHERE u.id_estado = 1";

        try {
            conn = Conexion.conectar(); 
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId_paciente(rs.getInt("id_paciente"));
                p.setId_usuario(rs.getInt("id_usuario"));
                p.setDireccion(rs.getString("direccion"));
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                p.setUsuario(u);

                pacientes.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pacientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }

        return pacientes;
    }

    public Paciente buscar(int id) {
        Paciente p = null;
        String sql = "SELECT * FROM paciente WHERE id_paciente = ?";

        try {
            conn = Conexion.conectar(); 
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                p = mapResultSetToPaciente(rs);
                p.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar paciente: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return p;
    }
    
    private Paciente mapResultSetToPaciente(ResultSet rs) throws SQLException {
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
        return p;
    }

    public Paciente buscarPorIdUsuario(int idUsuario) {
        Paciente p = null;
        String sql = "SELECT * FROM paciente WHERE id_usuario = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                p = mapResultSetToPaciente(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar paciente por usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return p;
    }

    public List<Paciente> listarConFiltro(String textoBusqueda) {
        List<Paciente> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, u.nombre, u.apellidos, u.correo, u.telefono as tel_personal, u.nombre_usuario " +
            "FROM paciente p " +
            "JOIN usuario u ON p.id_usuario = u.id_usuario " +
            "WHERE u.id_estado = 1 "
        );

        if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
            sql.append("AND (u.nombre LIKE ? OR u.apellidos LIKE ? OR u.telefono LIKE ?) ");
        }

        try {
            conn = Conexion.conectar(); 
            ps = conn.prepareStatement(sql.toString());
            
            if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
                String filtro = "%" + textoBusqueda + "%";
                ps.setString(1, filtro);
                ps.setString(2, filtro);
                ps.setString(3, filtro);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Paciente p = mapResultSetToPaciente(rs);
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("tel_personal")); 
                u.setNombre_usuario(rs.getString("nombre_usuario"));
                p.setUsuario(u); 
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error listar pacientes filtro: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    public boolean registrarPacienteTransaccion(modelo.Usuario u, Paciente p) throws SQLException {
        boolean exito = false;
        Connection connTransaccion = null;

        try {
            connTransaccion = Conexion.conectar();
            connTransaccion.setAutoCommit(false); 
            
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 4, 1, NOW())";
            PreparedStatement psUser = connTransaccion.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            ResultSet rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) {
                idGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("Error generando ID usuario");
            }
            rsKeys.close();
            psUser.close();

            String sqlPac = "INSERT INTO paciente (id_usuario, direccion, eps, rh, alergias, enfermedades_preexistentes, contacto_emergencia_nombre, contacto_emergencia_telefono, fecha_nacimiento, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
            PreparedStatement psPac = connTransaccion.prepareStatement(sqlPac);
            psPac.setInt(1, idGenerado);
            psPac.setString(2, p.getDireccion());
            psPac.setString(3, p.getEps());
            psPac.setString(4, p.getRh());
            psPac.setString(5, p.getAlergias());
            psPac.setString(6, p.getEnfermedades_preexistentes());
            psPac.setString(7, p.getContacto_emergencia_nombre());
            psPac.setString(8, p.getContacto_emergencia_telefono());
            
            if (p.getFecha_nacimiento() != null) {
                psPac.setDate(9, new Date(p.getFecha_nacimiento().getTime()));
            } else {
                psPac.setNull(9, java.sql.Types.DATE);
            }
            
            psPac.executeUpdate();
            psPac.close();

            connTransaccion.commit(); 
            exito = true;

        } catch (SQLException e) {
            if (connTransaccion != null) connTransaccion.rollback();
            throw e; 
        } finally {
            if (connTransaccion != null) {
                try { connTransaccion.setAutoCommit(true); } catch(SQLException ex){}
                try { connTransaccion.close(); } catch(SQLException ex){}
            }
        }
        return exito;
    }

    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}