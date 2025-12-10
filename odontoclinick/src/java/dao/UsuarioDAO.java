package dao;

import modelo.Usuario;
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
imports innecesarios, imports de relacionalidad
import modelo.Rol;
import modelo.Estado;
*/

public class UsuarioDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    private final RolDAO rolDAO = new RolDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre_usuario(rs.getString("nombre_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setContrasena(rs.getString("contrasena"));
                u.setFecha_creacion(rs.getDate("fecha_creacion"));
                u.setFecha_actualizacion(rs.getDate("fecha_actualizacion"));
                
                int idRol = rs.getInt("id_rol");
                int idEstado = rs.getInt("id_estado");
                u.setRol(rolDAO.buscar(idRol));
                u.setEstado(estadoDAO.buscar(idEstado));

                usuarios.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return usuarios;
    }

    // Buscar un usuario por ID
    public Usuario buscar(int id) {
        Usuario u = null;
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre_usuario(rs.getString("nombre_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setContrasena(rs.getString("contrasena"));
                u.setFecha_creacion(rs.getDate("fecha_creacion"));
                u.setFecha_actualizacion(rs.getDate("fecha_actualizacion"));

                int idRol = rs.getInt("id_rol");
                int idEstado = rs.getInt("id_estado");
                u.setRol(rolDAO.buscar(idRol));
                u.setEstado(estadoDAO.buscar(idEstado));
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return u;
    }

    // Guardar un nuevo usuario
    public void guardar(Usuario u) {
        String sql = "INSERT INTO usuario(nombre_usuario, nombre, apellidos, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion, fecha_actualizacion) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNombre_usuario());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getContrasena());
            ps.setInt(7, u.getRol().getId_rol());
            ps.setInt(8, u.getEstado().getId_estado());
            ps.setDate(9, new Date(u.getFecha_creacion().getTime()));
            ps.setDate(10, new Date(u.getFecha_actualizacion().getTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar usuario
    public void actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre_usuario = ?, nombre = ?, apellidos = ?, correo = ?, telefono = ?, contrasena = ?, id_rol = ?, id_estado = ?, fecha_actualizacion = ? "
                   + "WHERE id_usuario = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNombre_usuario());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getContrasena());
            ps.setInt(7, u.getRol().getId_rol());
            ps.setInt(8, u.getEstado().getId_estado());
            ps.setDate(9, new Date(u.getFecha_actualizacion().getTime()));
            ps.setInt(10, u.getId_usuario());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar usuario
    public void eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
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
    // Agrega esto dentro de la clase UsuarioDAO
public Usuario login(String user, String pass) {
    Usuario u = null;
    // Buscamos usuario por nombre_usuario y que esté ACTIVO (id_estado = 1)
    String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND id_estado = 1";

    try {
        // Asegúrate de usar tu variable de conexión 'conn' o 'ds'
        conn = dao.Conexion.conectar(); 
        ps = conn.prepareStatement(sql);
        ps.setString(1, user);
        rs = ps.executeQuery();

        if (rs.next()) {
            // Nota: En un entorno real, aquí usarías BCrypt.checkpw(pass, rs.getString("contrasena"))
            // Para este ejemplo asumimos comparación directa como base.
            if (rs.getString("contrasena").equals(pass)) {
                u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre_usuario(rs.getString("nombre_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));
                u.setId_rol(rs.getInt("id_rol"));
                u.setId_estado(rs.getInt("id_estado"));
                // Puedes cargar el Rol completo si lo necesitas
            }
        }
    } catch (SQLException e) {
        System.out.println("Error en login DAO: " + e.getMessage());
    } finally {
        cerrarRecursos(); // Tu método privado existente
    }
    return u;
}

// AGREGAR EN dao/UsuarioDAO.java

// Método para verificar si el usuario ya existe (Validación unique:usuarios)
public boolean existeUsuario(String nombreUsuario) {
    boolean existe = false;
    String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ?";
    try {
        conn = dao.Conexion.conectar();
        ps = conn.prepareStatement(sql);
        ps.setString(1, nombreUsuario);
        rs = ps.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            existe = true;
        }
    } catch (SQLException e) {
    } finally {
        cerrarRecursos();
    }
    return existe;
}

// Transacción para registrar Médico (Usuario + Médico)
public void registrarMedicoTransaccion(Usuario u, modelo.Medico m) throws SQLException {
    Connection connTransaccion = null;
    try {
        connTransaccion = dao.Conexion.conectar();
        connTransaccion.setAutoCommit(false); // Iniciar transacción

        // 1. Insertar Usuario y obtener ID generado
        String sqlUser = "INSERT INTO usuario (nombre_usuario, nombre, apellidos, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion, fecha_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement psUser = connTransaccion.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, u.getNombre_usuario());
        psUser.setString(2, u.getNombre());
        psUser.setString(3, u.getApellidos());
        psUser.setString(4, u.getCorreo());
        psUser.setString(5, u.getTelefono());
        psUser.setString(6, u.getContrasena());
        psUser.setInt(7, 2); // Rol Médico
        psUser.setInt(8, 1); // Activo
        psUser.executeUpdate();

        ResultSet rsKeys = psUser.getGeneratedKeys();
        int idGenerado = 0;
        if (rsKeys.next()) idGenerado = rsKeys.getInt(1);

        // 2. Insertar Médico usando el ID del usuario
        String sqlMed = "INSERT INTO medico (id_usuario, id_especialidad, licencia_medica, anos_experiencia, fecha_ingreso) VALUES (?, ?, ?, ?, NOW())";
        PreparedStatement psMed = connTransaccion.prepareStatement(sqlMed);
        psMed.setInt(1, idGenerado);
        psMed.setInt(2, m.getId_especialidad());
        psMed.setString(3, m.getLicencia_medica()); // Asumiendo que se pide en registro o se deja null
        psMed.setInt(4, 0); // Experiencia inicial 0 o pedir en form
        psMed.executeUpdate();

        connTransaccion.commit(); // Confirmar
    } catch (SQLException e) {
        if (connTransaccion != null) connTransaccion.rollback();
        throw e;
    } finally {
        if (connTransaccion != null) connTransaccion.setAutoCommit(true);
    }
}

// Transacción para registrar Paciente (Usuario + Paciente)
public void registrarPacienteTransaccion(Usuario u, modelo.Paciente p) throws SQLException {
    Connection connTransaccion = null;
    try {
        connTransaccion = dao.Conexion.conectar();
        connTransaccion.setAutoCommit(false); 

        // 1. Insertar Usuario
        String sqlUser = "INSERT INTO usuario (nombre_usuario, nombre, apellidos, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion, fecha_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement psUser = connTransaccion.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, u.getNombre_usuario());
        psUser.setString(2, u.getNombre());
        psUser.setString(3, u.getApellidos());
        psUser.setString(4, u.getCorreo());
        psUser.setString(5, u.getTelefono());
        psUser.setString(6, u.getContrasena());
        psUser.setInt(7, 4); // Rol Paciente
        psUser.setInt(8, 1); // Activo
        psUser.executeUpdate();

        ResultSet rsKeys = psUser.getGeneratedKeys();
        int idGenerado = 0;
        if (rsKeys.next()) idGenerado = rsKeys.getInt(1);

        // 2. Insertar Paciente
        String sqlPac = "INSERT INTO paciente (id_usuario, direccion, eps, rh, fecha_registro) VALUES (?, ?, ?, ?, NOW())";
        PreparedStatement psPac = connTransaccion.prepareStatement(sqlPac);
        psPac.setInt(1, idGenerado);
        psPac.setString(2, p.getDireccion());
        psPac.setString(3, p.getEps());
        psPac.setString(4, p.getRh());
        psPac.executeUpdate();

        connTransaccion.commit();
    } catch (SQLException e) {
        if (connTransaccion != null) connTransaccion.rollback();
        throw e;
    } finally {
        if (connTransaccion != null) connTransaccion.setAutoCommit(true);
    }
}

// Transacción para registrar Secretaria (Usuario + Secretaria)
public void registrarSecretariaTransaccion(Usuario u, modelo.Secretaria s) throws SQLException {
    Connection connTransaccion = null;
    try {
        connTransaccion = dao.Conexion.conectar();
        connTransaccion.setAutoCommit(false); 

        // 1. Insertar Usuario
        String sqlUser = "INSERT INTO usuario (nombre_usuario, nombre, apellidos, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion, fecha_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        PreparedStatement psUser = connTransaccion.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
        psUser.setString(1, u.getNombre_usuario());
        psUser.setString(2, u.getNombre());
        psUser.setString(3, u.getApellidos());
        psUser.setString(4, u.getCorreo());
        psUser.setString(5, u.getTelefono());
        psUser.setString(6, u.getContrasena());
        psUser.setInt(7, 3); // Rol Secretaria
        psUser.setInt(8, 1); // Activo
        psUser.executeUpdate();

        ResultSet rsKeys = psUser.getGeneratedKeys();
        int idGenerado = 0;
        if (rsKeys.next()) idGenerado = rsKeys.getInt(1);

        // 2. Insertar Secretaria
        String sqlSec = "INSERT INTO secretaria (id_usuario, turno, fecha_ingreso) VALUES (?, ?, NOW())";
        PreparedStatement psSec = connTransaccion.prepareStatement(sqlSec);
        psSec.setInt(1, idGenerado);
        psSec.setString(2, s.getTurno());
        psSec.executeUpdate();

        connTransaccion.commit();
    } catch (SQLException e) {
        if (connTransaccion != null) connTransaccion.rollback();
        throw e;
    } finally {
        if (connTransaccion != null) connTransaccion.setAutoCommit(true);
    }
    
}
}
