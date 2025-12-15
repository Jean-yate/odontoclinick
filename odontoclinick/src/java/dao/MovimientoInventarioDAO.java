package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.MovimientoInventario;

public class MovimientoInventarioDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    public List<MovimientoInventario> listar() {
        List<MovimientoInventario> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM movimiento_inventario";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                MovimientoInventario mov = new MovimientoInventario();
                mov.setIdMovimiento(rs.getInt("id_movimiento"));
                mov.setIdProducto(rs.getInt("id_producto"));
                mov.setCantidad(rs.getInt("cantidad"));
                mov.setStockAnterior(rs.getInt("stock_anterior"));
                mov.setStockNuevo(rs.getInt("stock_nuevo"));
                mov.setIdUsuario(rs.getInt("id_usuario"));
                mov.setTipoMovimiento(rs.getString("tipo_movimiento"));
                mov.setMotivo(rs.getString("motivo"));
                mov.setFechaMovimiento(rs.getTimestamp("fecha_movimiento"));
                lista.add(mov);
            }
        } catch (SQLException e) {
            System.out.println("Error listar movimientos: " + e.getMessage());
        }
        return lista;
    }

    public void actualizar(MovimientoInventario mov) {
        try {
            String sql = "UPDATE movimiento_inventario SET id_producto = ?, cantidad = ?, stock_anterior = ?, stock_nuevo = ?, id_usuario = ?, tipo_movimiento = ?, motivo = ?, fecha_movimiento = ? WHERE id_movimiento = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, mov.getIdProducto());
            ps.setInt(2, mov.getCantidad());
            ps.setInt(3, mov.getStockAnterior());
            ps.setInt(4, mov.getStockNuevo());
            ps.setInt(5, mov.getIdUsuario());
            ps.setString(6, mov.getTipoMovimiento());
            ps.setString(7, mov.getMotivo());
            ps.setTimestamp(8, mov.getFechaMovimiento());
            ps.setInt(9, mov.getIdMovimiento());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar movimiento: " + e.getMessage());
        }
    }

    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM movimiento_inventario WHERE id_movimiento = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar movimiento: " + e.getMessage());
        }
    }

    public void guardar(MovimientoInventario mov) {
    try {
        conn.setAutoCommit(false); 
        String sqlMov = "INSERT INTO movimiento_inventario(...) VALUES(...)";
        ps = conn.prepareStatement(sqlMov);
        ps.executeUpdate();

        String sqlUpdate = "";
        if (mov.getTipoMovimiento().equals("ENTRADA")) {
            sqlUpdate = "UPDATE producto SET stock_actual = stock_actual + ? WHERE id_producto = ?";
        } else { // SALIDA
            sqlUpdate = "UPDATE producto SET stock_actual = stock_actual - ? WHERE id_producto = ?";
        }
        
        ps = conn.prepareStatement(sqlUpdate);
        ps.setInt(1, mov.getCantidad());
        ps.setInt(2, mov.getIdProducto());
        ps.executeUpdate();

        conn.commit(); 

    } catch (SQLException e) {
        try {
            conn.rollback(); 
        } catch (SQLException ex) { ex.printStackTrace(); }
        System.out.println("Error transacción inventario: " + e.getMessage());
        } finally {
        try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
}
