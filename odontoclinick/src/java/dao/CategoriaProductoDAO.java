package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.CategoriaProducto;

public class CategoriaProductoDAO {

    private final Connection conn = Conexion.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Listar todas las categorías
    public List<CategoriaProducto> listar() {
        List<CategoriaProducto> lista = new ArrayList<>();
        try {
            String sql = "SELECT * FROM categoria_producto";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                CategoriaProducto cat = new CategoriaProducto();
                cat.setId_categoria(rs.getInt("id_categoria"));
                cat.setNombre_categoria(rs.getString("nombre_categoria"));
                cat.setDescripcion(rs.getString("descripcion"));
                lista.add(cat);
            }
        } catch (SQLException e) {
            System.out.println("Error listar categorías: " + e.getMessage());
        }
        return lista;
    }

    // Guardar nueva categoría
    public void guardar(CategoriaProducto cat) {
        try {
            String sql = "INSERT INTO categoria_producto(nombre_categoria, descripcion) VALUES(?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cat.getNombre_categoria());
            ps.setString(2, cat.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar categoría: " + e.getMessage());
        }
    }

    // Buscar categoría por ID
    public CategoriaProducto buscar(int id) {
        CategoriaProducto cat = null;
        try {
            String sql = "SELECT * FROM categoria_producto WHERE id_categoria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                cat = new CategoriaProducto();
                cat.setId_categoria(rs.getInt("id_categoria"));
                cat.setNombre_categoria(rs.getString("nombre_categoria"));
                cat.setDescripcion(rs.getString("descripcion"));
            }
        } catch (SQLException e) {
            System.out.println("Error buscar categoría: " + e.getMessage());
        }
        return cat;
    }

    // Actualizar categoría
    public void actualizar(CategoriaProducto cat) {
        try {
            String sql = "UPDATE categoria_producto SET nombre_categoria = ?, descripcion = ? WHERE id_categoria = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cat.getNombre_categoria());
            ps.setString(2, cat.getDescripcion());
            ps.setInt(3, cat.getId_categoria());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar categoría: " + e.getMessage());
        }
    }

    // Eliminar categoría
    public void eliminar(int id) {
        try {
            String sql = "DELETE FROM categoria_producto WHERE id_categoria = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar categoría: " + e.getMessage());
        }
    }
}
