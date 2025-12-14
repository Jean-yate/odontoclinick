package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;
// Importamos los modelos necesarios para las transacciones
import modelo.Medico;
import modelo.Paciente;
import modelo.Secretaria;

public class UsuarioDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    // Instancias para cargar relaciones (Roles y Estados)
    private final RolDAO rolDAO = new RolDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();

    // ==========================================
    // SECCIÓN 1: MÉTODOS DE LECTURA (Login, Listar, Buscar)
    // ==========================================

    public Usuario login(String user, String pass) {
        Usuario u = null;
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND id_estado = 1"; 
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("contrasena").equals(pass)) {
                    u = mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error login: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return u;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listar usuarios: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return usuarios;
    }

    public Usuario buscar(int id) {
        Usuario u = null;
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                u = mapResultSetToUsuario(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error buscar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return u;
    }

    // Usado por AdminBean
    public List<Usuario> listarPorRol(int idRol) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE id_rol = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idRol);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error listarPorRol: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    // Usado por RegistroBean (Línea 66)
    public boolean existeUsuario(String username) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } catch (SQLException e) {
            System.out.println("Error existeUsuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return existe;
    }

    // ==========================================
    // SECCIÓN 2: MÉTODOS CRUD SIMPLES (AdminBean)
    // ==========================================

    public void guardar(Usuario u) {
        String sql = "INSERT INTO usuario(nombre_usuario, nombre, apellidos, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion, fecha_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNombre_usuario());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getContrasena());
            ps.setInt(7, (u.getRol() != null) ? u.getRol().getId_rol() : u.getId_rol());
            ps.setInt(8, (u.getEstado() != null) ? u.getEstado().getId_estado() : u.getId_estado());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre_usuario=?, nombre=?, apellidos=?, correo=?, telefono=?, contrasena=?, id_rol=?, id_estado=?, fecha_actualizacion=NOW() WHERE id_usuario=?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNombre_usuario());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getContrasena());
            ps.setInt(7, (u.getRol() != null) ? u.getRol().getId_rol() : u.getId_rol());
            ps.setInt(8, (u.getEstado() != null) ? u.getEstado().getId_estado() : u.getId_estado());
            ps.setInt(9, u.getId_usuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try {
            conn = Conexion.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // ==========================================
    // SECCIÓN 3: TRANSACCIONES (RegistroBean)
    // ==========================================
    
    // MÉTODO NUEVO: Para el Administrador (Rol 1) o cualquier rol sin tabla secundaria
    /**
     * Registra solo en la tabla 'usuario' (para roles sin tablas secundarias, ej. Admin).
     */
    public void registrarUsuarioTransaccionSimple(Usuario u) throws SQLException {
        Connection connTx = null;
        PreparedStatement psUser = null;
        ResultSet rsKeys = null;
        
        // La consulta usa el ID_ROL que viene en el objeto 'u' (será 1 para Admin)
        String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?, 1, NOW())";
        
        try {
            connTx = Conexion.conectar();
            connTx.setAutoCommit(false); // Inicio Transacción

            psUser = connTx.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.setInt(7, u.getId_rol()); // Usamos el ID de rol que viene del Bean (ej. 1)
            psUser.executeUpdate();

            rsKeys = psUser.getGeneratedKeys();
            if (!rsKeys.next()) {
                 throw new SQLException("Fallo al obtener ID del usuario (Admin/Simple)");
            }
            
            connTx.commit(); // Confirmar
        } catch (SQLException e) {
            if (connTx != null) connTx.rollback();
            throw e;
        } finally {
            try { if (rsKeys != null) rsKeys.close(); } catch (SQLException e) {}
            try { if (psUser != null) psUser.close(); } catch (SQLException e) {}
            if (connTx != null) { connTx.setAutoCommit(true); connTx.close(); }
        }
    }


    // Usado por RegistroBean (Médico - Rol 2)
    public void registrarMedicoTransaccion(Usuario u, Medico m) throws SQLException {
        Connection connTx = null;
        try {
            connTx = Conexion.conectar();
            connTx.setAutoCommit(false); // Inicio Transacción

            // A. Insertar Usuario
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 2, 1, NOW())";
            PreparedStatement psUser = connTx.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            ResultSet rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) idGenerado = rsKeys.getInt(1);
            else throw new SQLException("Fallo al obtener ID del usuario médico");
            
            psUser.close();
            rsKeys.close();

            // B. Insertar Médico
            String sqlMed = "INSERT INTO medico (id_usuario, id_especialidad, licencia_medica, anos_experiencia, fecha_ingreso) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement psMed = connTx.prepareStatement(sqlMed);
            psMed.setInt(1, idGenerado);
            psMed.setInt(2, m.getId_especialidad());
            psMed.setString(3, m.getLicencia_medica());
            psMed.setInt(4, m.getAnos_experiencia());
            psMed.executeUpdate();
            psMed.close();

            connTx.commit(); // Confirmar
        } catch (SQLException e) {
            if (connTx != null) connTx.rollback();
            throw e;
        } finally {
            if (connTx != null) { connTx.setAutoCommit(true); connTx.close(); }
        }
    }

    // Usado por RegistroBean (Secretaria - Rol 3)
    public void registrarSecretariaTransaccion(Usuario u, Secretaria s) throws SQLException {
        Connection connTx = null;
        try {
            connTx = Conexion.conectar();
            connTx.setAutoCommit(false);

            // A. Insertar Usuario
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 3, 1, NOW())";
            PreparedStatement psUser = connTx.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            ResultSet rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) idGenerado = rsKeys.getInt(1);
            else throw new SQLException("Fallo al obtener ID del usuario secretaria");

            psUser.close();
            rsKeys.close();

            // B. Insertar Secretaria
            String sqlSec = "INSERT INTO secretaria (id_usuario, turno, fecha_ingreso) VALUES (?, ?, NOW())";
            PreparedStatement psSec = connTx.prepareStatement(sqlSec);
            psSec.setInt(1, idGenerado);
            psSec.setString(2, s.getTurno());
            psSec.executeUpdate();
            psSec.close();

            connTx.commit();
        } catch (SQLException e) {
            if (connTx != null) connTx.rollback();
            throw e;
        } finally {
            if (connTx != null) { connTx.setAutoCommit(true); connTx.close(); }
        }
    }

    // Usado por RegistroBean (Paciente - Rol 4)
    public void registrarPacienteTransaccion(Usuario u, Paciente p) throws SQLException {
        Connection connTx = null;
        try {
            connTx = Conexion.conectar();
            connTx.setAutoCommit(false);

            // A. Insertar Usuario
            String sqlUser = "INSERT INTO usuario (nombre, apellidos, nombre_usuario, correo, telefono, contrasena, id_rol, id_estado, fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, 4, 1, NOW())";
            PreparedStatement psUser = connTx.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, u.getNombre());
            psUser.setString(2, u.getApellidos());
            psUser.setString(3, u.getNombre_usuario());
            psUser.setString(4, u.getCorreo());
            psUser.setString(5, u.getTelefono());
            psUser.setString(6, u.getContrasena());
            psUser.executeUpdate();

            ResultSet rsKeys = psUser.getGeneratedKeys();
            int idGenerado = -1;
            if (rsKeys.next()) idGenerado = rsKeys.getInt(1);
            else throw new SQLException("Fallo al obtener ID del usuario paciente");

            psUser.close();
            rsKeys.close();

            // B. Insertar Paciente (CORRECCIÓN: Se agrega fecha_nacimiento)
            String sqlPac = "INSERT INTO paciente (id_usuario, direccion, eps, rh, fecha_nacimiento, fecha_registro) VALUES (?, ?, ?, ?, ?, NOW())";
            PreparedStatement psPac = connTx.prepareStatement(sqlPac);
            psPac.setInt(1, idGenerado);
            psPac.setString(2, p.getDireccion());
            psPac.setString(3, p.getEps());
            psPac.setString(4, p.getRh());
            
            // CUIDADO: Convertir java.util.Date a java.sql.Date y manejar nulls
            if (p.getFecha_nacimiento() != null) {
                psPac.setDate(5, new java.sql.Date(p.getFecha_nacimiento().getTime()));
            } else {
                psPac.setNull(5, java.sql.Types.DATE);
            }
            
            psPac.executeUpdate();
            psPac.close();

            connTx.commit();
        } catch (SQLException e) {
            if (connTx != null) connTx.rollback();
            throw e;
        } finally {
            if (connTx != null) { connTx.setAutoCommit(true); connTx.close(); }
        }
    }

    // ==========================================
    // HELPER (Mapeo de ResultSet a Objeto)
    // ==========================================
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId_usuario(rs.getInt("id_usuario"));
        u.setNombre_usuario(rs.getString("nombre_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setCorreo(rs.getString("correo"));
        u.setTelefono(rs.getString("telefono"));
        u.setContrasena(rs.getString("contrasena"));
        u.setFecha_creacion(rs.getDate("fecha_creacion"));
        u.setId_rol(rs.getInt("id_rol"));
        u.setId_estado(rs.getInt("id_estado"));
        try {
            if (rolDAO != null) u.setRol(rolDAO.buscar(rs.getInt("id_rol")));
            if (estadoDAO != null) u.setEstado(estadoDAO.buscar(rs.getInt("id_estado")));
        } catch (Exception ex) {}
        return u;
    }

    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}