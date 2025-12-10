package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Secretaria;

/*
imports innecesarios, imports de relacionalidad y tipo de datos
import java.util.Date;
import modelo.Usuario;
*/

public class SecretariaDAO {
    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // Para traer el objeto Usuario completo

    // Listar todas las secretarias
    public List<Secretaria> listar() {
        List<Secretaria> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM secretaria";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Secretaria sec = new Secretaria();
                sec.setId_secretaria(rs.getInt("id_secretaria"));
                sec.setId_usuario(rs.getInt("id_usuario"));
                sec.setTurno(rs.getString("turno"));
                sec.setFecha_ingreso(rs.getDate("fecha_ingreso"));
                sec.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
                lista.add(sec);
            }
        } catch (SQLException e) {
            System.out.println("Error listar Secretaria: " + e.getMessage());
        }
        return lista;
    }

    // Buscar secretaria por ID
    public Secretaria buscar(int id) {
        Secretaria sec = null;
        try {
            String sql = "SELECT * FROM secretaria WHERE id_secretaria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                sec = new Secretaria();
                sec.setId_secretaria(rs.getInt("id_secretaria"));
                sec.setId_usuario(rs.getInt("id_usuario"));
                sec.setTurno(rs.getString("turno"));
                sec.setFecha_ingreso(rs.getDate("fecha_ingreso"));
                sec.setUsuario(usuarioDAO.buscar(rs.getInt("id_usuario")));
            }
        } catch (SQLException e) {
            System.out.println("Error buscar Secretaria: " + e.getMessage());
        }
        return sec;
    }

    // Guardar nueva secretaria
    public void guardar(Secretaria sec) {
        try {
            String sql = "INSERT INTO secretaria(id_usuario, turno, fecha_ingreso) VALUES(?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sec.getId_usuario());
            ps.setString(2, sec.getTurno());
            ps.setDate(3, new java.sql.Date(sec.getFecha_ingreso().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar Secretaria: " + e.getMessage());
        }
    }

    // Actualizar secretaria existente
    public void actualizar(Secretaria sec) {
        try {
            String sql = "UPDATE secretaria SET id_usuario = ?, turno = ?, fecha_ingreso = ? WHERE id_secretaria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, sec.getId_usuario());
            ps.setString(2, sec.getTurno());
            ps.setDate(3, new java.sql.Date(sec.getFecha_ingreso().getTime()));
            ps.setInt(4, sec.getId_secretaria());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar Secretaria: " + e.getMessage());
        }
    }

    // Eliminar secretaria por ID
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM secretaria WHERE id_secretaria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar Secretaria: " + e.getMessage());
        }
    }
}
