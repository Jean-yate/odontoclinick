package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.MovimientoInventario;

/*
imports innecesarios, imports de tipo de dato sql
import java.sql.Timestamp;
*/

public class MovimientoInventarioDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los movimientos
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

    // Guardar movimiento
    public void guardar(MovimientoInventario mov) {
        try {
            String sql = "INSERT INTO movimiento_inventario(id_producto, cantidad, stock_anterior, stock_nuevo, id_usuario, tipo_movimiento, motivo, fecha_movimiento) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, mov.getIdProducto());
            ps.setInt(2, mov.getCantidad());
            ps.setInt(3, mov.getStockAnterior());
            ps.setInt(4, mov.getStockNuevo());
            ps.setInt(5, mov.getIdUsuario());
            ps.setString(6, mov.getTipoMovimiento());
            ps.setString(7, mov.getMotivo());
            ps.setTimestamp(8, mov.getFechaMovimiento());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar movimiento: " + e.getMessage());
        }
    }

    // Buscar movimiento por ID
    public MovimientoInventario buscar(int id) {
        MovimientoInventario mov = null;
        try {
            String sql = "SELECT * FROM movimiento_inventario WHERE id_movimiento = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                mov = new MovimientoInventario();
                mov.setIdMovimiento(rs.getInt("id_movimiento"));
                mov.setIdProducto(rs.getInt("id_producto"));
                mov.setCantidad(rs.getInt("cantidad"));
                mov.setStockAnterior(rs.getInt("stock_anterior"));
                mov.setStockNuevo(rs.getInt("stock_nuevo"));
                mov.setIdUsuario(rs.getInt("id_usuario"));
                mov.setTipoMovimiento(rs.getString("tipo_movimiento"));
                mov.setMotivo(rs.getString("motivo"));
                mov.setFechaMovimiento(rs.getTimestamp("fecha_movimiento"));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar movimiento: " + e.getMessage());
        }
        return mov;
    }

    // Actualizar movimiento
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

    // Eliminar movimiento
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
}
