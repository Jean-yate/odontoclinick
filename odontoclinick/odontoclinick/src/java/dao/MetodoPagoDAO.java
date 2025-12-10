package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.MetodoPago;

public class MetodoPagoDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los métodos de pago
    public List<MetodoPago> listar() {
        List<MetodoPago> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM metodo_pago";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                MetodoPago metodo = new MetodoPago();
                metodo.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                metodo.setNombre_metodo(rs.getString("nombre_metodo"));
                metodo.setActivo(rs.getBoolean("activo"));
                lista.add(metodo);
            }

        } catch (SQLException e) {
            System.out.println("Error listar métodos de pago: " + e.getMessage());
        }
        return lista;
    }

    // Guardar método de pago
    public void guardar(MetodoPago metodo) {
        try {
            String sql = "INSERT INTO metodo_pago(nombre_metodo, activo) VALUES(?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, metodo.getNombre_metodo());
            ps.setBoolean(2, metodo.isActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar método de pago: " + e.getMessage());
        }
    }

    // Buscar método de pago por ID
    public MetodoPago buscar(int id) {
        MetodoPago metodo = null;
        try {
            String sql = "SELECT * FROM metodo_pago WHERE id_metodo_pago = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                metodo = new MetodoPago();
                metodo.setId_metodo_pago(rs.getInt("id_metodo_pago"));
                metodo.setNombre_metodo(rs.getString("nombre_metodo"));
                metodo.setActivo(rs.getBoolean("activo"));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar método de pago: " + e.getMessage());
        }
        return metodo;
    }

    // Actualizar método de pago
    public void actualizar(MetodoPago metodo) {
        try {
            String sql = "UPDATE metodo_pago SET nombre_metodo = ?, activo = ? WHERE id_metodo_pago = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, metodo.getNombre_metodo());
            ps.setBoolean(2, metodo.isActivo());
            ps.setInt(3, metodo.getId_metodo_pago());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar método de pago: " + e.getMessage());
        }
    }

    // Eliminar método de pago
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM metodo_pago WHERE id_metodo_pago = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar método de pago: " + e.getMessage());
        }
    }
}
