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
}
