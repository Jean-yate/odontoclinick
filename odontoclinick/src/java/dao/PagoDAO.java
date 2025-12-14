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

    private Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los pagos
    public List<Pago> listar() {
        List<Pago> lista = new ArrayList<>();
        
        // EL CAMBIO CLAVE: Usamos JOIN para conectar las tablas y llegar al nombre
        String sql = "SELECT p.*, " +
                     "u.nombre, u.apellidos " + 
                     "FROM pago p " +
                     "INNER JOIN cita c ON p.id_cita = c.id_cita " +
                     "INNER JOIN paciente pac ON c.id_paciente = pac.id_paciente " +
                     "INNER JOIN usuario u ON pac.id_usuario = u.id_usuario " +
                     "ORDER BY p.fecha_pago DESC";

        try {
            // Aseguramos la conexión
            if (conn == null || conn.isClosed()) {
                conn = Conexion.conectar();
            }
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Pago pago = new Pago();
                
                // 1. Datos del Pago (lo que ya tenías)
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdCita(rs.getInt("id_cita"));
                pago.setIdMetodoPago(rs.getInt("id_metodo_pago"));
                pago.setFechaPago(rs.getTimestamp("fecha_pago"));
                pago.setMonto(rs.getBigDecimal("monto"));
                pago.setReferencia(rs.getString("referencia"));
                pago.setNotas(rs.getString("notas"));

                // 2. CONSTRUCCIÓN DEL OBJETO ANIDADO (La magia para el reporte)
                
                // A. Usuario (Nombre y Apellido)
                modelo.Usuario u = new modelo.Usuario();
                u.setNombre(rs.getString("nombre"));
                u.setApellidos(rs.getString("apellidos"));

                // B. Paciente (con el Usuario dentro)
                modelo.Paciente pac = new modelo.Paciente();
                pac.setUsuario(u);

                // C. Cita (con el Paciente dentro)
                modelo.Cita c = new modelo.Cita();
                c.setId_cita(rs.getInt("id_cita"));
                c.setPaciente(pac);

                // D. Guardamos la Cita completa dentro del Pago
                pago.setCita(c);
                
                lista.add(pago);
            }

        } catch (SQLException e) {
            System.out.println("Error listar pagos completo: " + e.getMessage());
            e.printStackTrace();
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
