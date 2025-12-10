package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Pago;

/*
imports innecesarios, imports de tipos de datos sql
import java.sql.Timestamp;
import java.math.BigDecimal;
*/

public class PagoDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los pagos
    public List<Pago> listar() {
        List<Pago> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM pago";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Pago pago = new Pago();
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdCita(rs.getInt("id_cita"));
                pago.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                pago.setFechaPago(rs.getTimestamp("fecha_pago"));
                pago.setMonto(rs.getBigDecimal("monto"));
                pago.setReferencia(rs.getString("referencia"));
                pago.setNotas(rs.getString("notas"));
                lista.add(pago);
            }

        } catch (SQLException e) {
            System.out.println("Error listar pagos: " + e.getMessage());
        }
        return lista;
    }

    // Guardar pago
    public void guardar(Pago pago) {
        try {
            String sql = "INSERT INTO pago(id_cita, id_metodo_pago, fecha_pago, monto, referencia, notas) VALUES(?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, pago.getIdCita());
            ps.setInt(2, pago.getIdMetodoPago());
            ps.setTimestamp(3, pago.getFechaPago());
            ps.setBigDecimal(4, pago.getMonto());
            ps.setString(5, pago.getReferencia());
            ps.setString(6, pago.getNotas());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar pago: " + e.getMessage());
        }
    }

    // Buscar pago por ID
    public Pago buscar(int id) {
        Pago pago = null;
        try {
            String sql = "SELECT * FROM pago WHERE id_pago = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                pago = new Pago();
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdCita(rs.getInt("id_cita"));
                pago.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                pago.setFechaPago(rs.getTimestamp("fecha_pago"));
                pago.setMonto(rs.getBigDecimal("monto"));
                pago.setReferencia(rs.getString("referencia"));
                pago.setNotas(rs.getString("notas"));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar pago: " + e.getMessage());
        }
        return pago;
    }

    // Actualizar pago
    public void actualizar(Pago pago) {
        try {
            String sql = "UPDATE pago SET id_cita = ?, id_metodo_pago = ?, fecha_pago = ?, monto = ?, referencia = ?, notas = ? WHERE id_pago = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, pago.getIdCita());
            ps.setInt(2, pago.getIdMetodoPago());
            ps.setTimestamp(3, pago.getFechaPago());
            ps.setBigDecimal(4, pago.getMonto());
            ps.setString(5, pago.getReferencia());
            ps.setString(6, pago.getNotas());
            ps.setInt(7, pago.getIdPago());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar pago: " + e.getMessage());
        }
    }

    // Eliminar pago
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM pago WHERE id_pago = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar pago: " + e.getMessage());
        }
    }
}
