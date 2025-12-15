package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Pago;

public class PagoDAO {

    private Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    public List<Pago> listar() {
        List<Pago> lista = new ArrayList<>();

        String sql = "SELECT p.*, " +
                     "u.nombre, u.apellidos " + 
                     "FROM pago p " +
                     "INNER JOIN cita c ON p.id_cita = c.id_cita " +
                     "INNER JOIN paciente pac ON c.id_paciente = pac.id_paciente " +
                     "INNER JOIN usuario u ON pac.id_usuario = u.id_usuario " +
                     "ORDER BY p.fecha_pago DESC";

        try {
            if (conn == null || conn.isClosed()) {
                conn = Conexion.conectar();
            }
            
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

                modelo.Usuario u = new modelo.Usuario();
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));

                modelo.Paciente pac = new modelo.Paciente();
                pac.setUsuario(u);

                modelo.Cita c = new modelo.Cita();
                c.setId_cita(rs.getInt("id_cita"));
                c.setPaciente(pac);

                pago.setCita(c);
                
                lista.add(pago);
            }

        } catch (SQLException e) {
            System.out.println("Error listar pagos completo: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

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
