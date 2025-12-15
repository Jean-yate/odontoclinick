package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

public class ProductoDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;
    private final CategoriaProductoDAO catDAO = new CategoriaProductoDAO();

    // =============================
    // LISTAR PRODUCTOS
    // =============================
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM producto";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Producto prod = new Producto();
                prod.setId_producto(rs.getInt("id_producto"));
                prod.setCodigo(rs.getString("codigo_producto"));
                prod.setNombre_producto(rs.getString("nombre_producto"));
                prod.setDescripcion(rs.getString("descripcion"));
                prod.setPrecio_compra(rs.getFloat("precio_compra"));
                prod.setPrecio_venta(rs.getFloat("precio_venta"));
                prod.setStock_actual(rs.getInt("stock_actual"));
                prod.setStock_minimo(rs.getInt("stock_minimo"));
                prod.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
                prod.setFecha_creacion(rs.getDate("fecha_creacion"));
                prod.setActivo(rs.getBoolean("activo"));

                int idCat = rs.getInt("id_categoria");
                prod.setCategoria(catDAO.buscar(idCat));

                lista.add(prod);
            }

        } catch (SQLException e) {
            System.out.println("Error listar productos: " + e.getMessage());
        }
        return lista;
    }

    // =============================
    // GUARDAR PRODUCTO
    // =============================
    public void guardar(Producto prod) {
        try {
            String sql = "INSERT INTO producto (codigo_producto, nombre_producto, descripcion, precio_compra, precio_venta, stock_actual, stock_minimo, fecha_vencimiento, fecha_creacion, activo, id_categoria) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);

            ps.setString(1, prod.getCodigo());
            ps.setString(2, prod.getNombre_producto());
            ps.setString(3, prod.getDescripcion());
            ps.setFloat(4, prod.getPrecio_compra());
            ps.setFloat(5, prod.getPrecio_venta());
            ps.setInt(6, prod.getStock_actual());
            ps.setInt(7, prod.getStock_minimo());

            // 🔹 FECHA VENCIMIENTO NO OBLIGATORIA (CORRECCIÓN)
            if (prod.getFecha_vencimiento() != null) {
                ps.setDate(8, new Date(prod.getFecha_vencimiento().getTime()));
            } else {
                ps.setDate(8, null);
            }

            // Fecha creación (DEBE tener valor)
            ps.setDate(9, new Date(prod.getFecha_creacion().getTime()));

            ps.setBoolean(10, prod.isActivo());
            ps.setInt(11, prod.getCategoria().getId_categoria());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error guardar producto: " + e.getMessage());
        }
    }

    // =============================
    // BUSCAR POR ID
    // =============================
    public Producto buscar(int id) {
        Producto prod = null;
        try {
            String sql = "SELECT * FROM producto WHERE id_producto = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                prod = new Producto();
                prod.setId_producto(rs.getInt("id_producto"));
                prod.setCodigo(rs.getString("codigo_producto"));
                prod.setNombre_producto(rs.getString("nombre_producto"));
                prod.setDescripcion(rs.getString("descripcion"));
                prod.setPrecio_compra(rs.getFloat("precio_compra"));
                prod.setPrecio_venta(rs.getFloat("precio_venta"));
                prod.setStock_actual(rs.getInt("stock_actual"));
                prod.setStock_minimo(rs.getInt("stock_minimo"));
                prod.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
                prod.setFecha_creacion(rs.getDate("fecha_creacion"));
                prod.setActivo(rs.getBoolean("activo"));

                int idCat = rs.getInt("id_categoria");
                prod.setCategoria(catDAO.buscar(idCat));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar producto: " + e.getMessage());
        }
        return prod;
    }

    // =============================
    // ACTUALIZAR PRODUCTO
    // =============================
    public void actualizar(Producto prod) {
        try {
            String sql = "UPDATE producto SET codigo_producto=?, nombre_producto=?, descripcion=?, precio_compra=?, precio_venta=?, stock_actual=?, stock_minimo=?, fecha_vencimiento=?, fecha_creacion=?, activo=?, id_categoria=? "
                       + "WHERE id_producto=?";

            ps = conn.prepareStatement(sql);

            ps.setString(1, prod.getCodigo());
            ps.setString(2, prod.getNombre_producto());
            ps.setString(3, prod.getDescripcion());
            ps.setFloat(4, prod.getPrecio_compra());
            ps.setFloat(5, prod.getPrecio_venta());
            ps.setInt(6, prod.getStock_actual());
            ps.setInt(7, prod.getStock_minimo());

            if (prod.getFecha_vencimiento() != null) {
                ps.setDate(8, new Date(prod.getFecha_vencimiento().getTime()));
            } else {
                ps.setDate(8, null);
            }

            ps.setDate(9, new Date(prod.getFecha_creacion().getTime()));
            ps.setBoolean(10, prod.isActivo());
            ps.setInt(11, prod.getCategoria().getId_categoria());
            ps.setInt(12, prod.getId_producto());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error actualizar producto: " + e.getMessage());
        }
    }

    // =============================
    // ELIMINAR PRODUCTO
    // =============================
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM producto WHERE id_producto = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar producto: " + e.getMessage());
        }
    }
}
