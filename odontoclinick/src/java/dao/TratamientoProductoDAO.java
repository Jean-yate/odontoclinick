package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.TratamientoProducto;

public class TratamientoProductoDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todos los registros
    public List<TratamientoProducto> listar() {
        List<TratamientoProducto> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tratamiento_producto";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TratamientoProducto tp = new TratamientoProducto();
                tp.setIdTratamientoProducto(rs.getInt("id_tratamiento_producto"));
                tp.setIdTratamiento(rs.getInt("id_tratamiento"));
                tp.setIdProducto(rs.getInt("id_producto"));
                tp.setCantidadRequerida(rs.getBigDecimal("cantidad_requerida"));
                lista.add(tp);
            }
        } catch (SQLException e) {
            System.out.println("Error listar TratamientoProducto: " + e.getMessage());
        }
        return lista;
    }

    // Buscar por ID
    public TratamientoProducto buscar(int id) {
        TratamientoProducto tp = null;
        try {
            String sql = "SELECT * FROM tratamiento_producto WHERE id_tratamiento_producto = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                tp = new TratamientoProducto();
                tp.setIdTratamientoProducto(rs.getInt("id_tratamiento_producto"));
                tp.setIdTratamiento(rs.getInt("id_tratamiento"));
                tp.setIdProducto(rs.getInt("id_producto"));
                tp.setCantidadRequerida(rs.getBigDecimal("cantidad_requerida"));
            }
        } catch (SQLException e) {
            System.out.println("Error buscar TratamientoProducto: " + e.getMessage());
        }
        return tp;
    }

    // Guardar nuevo registro
    public void guardar(TratamientoProducto tp) {
        try {
            String sql = "INSERT INTO tratamiento_producto VALUES(null, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tp.getIdTratamiento());
            ps.setInt(2, tp.getIdProducto());
            ps.setBigDecimal(3, tp.getCantidadRequerida());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar TratamientoProducto: " + e.getMessage());
        }
    }

    // Actualizar registro
    public void actualizar(TratamientoProducto tp) {
        try {
            String sql = "UPDATE tratamiento_producto SET id_tratamiento = ?, id_producto = ?, cantidad_requerida = ? WHERE id_tratamiento_producto = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tp.getIdTratamiento());
            ps.setInt(2, tp.getIdProducto());
            ps.setBigDecimal(3, tp.getCantidadRequerida());
            ps.setInt(4, tp.getIdTratamientoProducto());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar TratamientoProducto: " + e.getMessage());
        }
    }

    // Eliminar registro
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM tratamiento_producto WHERE id_tratamiento_producto = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar TratamientoProducto: " + e.getMessage());
        }
    }
}
