package dao;

import modelo.Tratamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;

/*
imports innecesarios, imports de relacionalidad
import modelo.CitaTratamiento;
*/

public class TratamientoDAO {

    @Resource(lookup = "jdbc/odontoclinic")
    private DataSource ds;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    
    // Listar todos los tratamientos
    public List<Tratamiento> listarTodos() {
        List<Tratamiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM tratamiento";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Tratamiento t = new Tratamiento();
                t.setId_tratamiento(rs.getInt("id_tratamiento"));
                t.setCodigo(rs.getString("codigo"));
                t.setNombre_tratamiento(rs.getString("nombre_tratamiento"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setCosto_base(rs.getFloat("costo_base"));
                t.setDuracion_estimada_minutos(rs.getInt("duracion_estimada_minutos"));
                t.setActivo(rs.getBoolean("activo"));

                // Si quieres, también puedes traer los CitaTratamientos asociados
                // t.setCitaTratamientos(citaTratDAO.listarPorTratamiento(t.getId_tratamiento()));

                lista.add(t);
            }

        } catch (SQLException e) {
            System.out.println("Error listar tratamientos: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return lista;
    }

    // Buscar tratamiento por ID
    public Tratamiento buscar(int id) {
        Tratamiento t = null;
        String sql = "SELECT * FROM tratamiento WHERE id_tratamiento = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                t = new Tratamiento();
                t.setId_tratamiento(rs.getInt("id_tratamiento"));
                t.setCodigo(rs.getString("codigo"));
                t.setNombre_tratamiento(rs.getString("nombre_tratamiento"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setCosto_base(rs.getFloat("costo_base"));
                t.setDuracion_estimada_minutos(rs.getInt("duracion_estimada_minutos"));
                t.setActivo(rs.getBoolean("activo"));
            }

        } catch (SQLException e) {
            System.out.println("Error buscar tratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }

        return t;
    }

    // Guardar tratamiento
    public void guardar(Tratamiento t) {
        String sql = "INSERT INTO tratamiento(codigo, nombre_tratamiento, descripcion, costo_base, duracion_estimada_minutos, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getCodigo());
            ps.setString(2, t.getNombre_tratamiento());
            ps.setString(3, t.getDescripcion());
            ps.setFloat(4, t.getCosto_base());
            ps.setInt(5, t.getDuracion_estimada_minutos());
            ps.setBoolean(6, t.isActivo());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error guardar tratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Actualizar tratamiento
    public void actualizar(Tratamiento t) {
        String sql = "UPDATE tratamiento SET codigo = ?, nombre_tratamiento = ?, descripcion = ?, "
                   + "costo_base = ?, duracion_estimada_minutos = ?, activo = ? WHERE id_tratamiento = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getCodigo());
            ps.setString(2, t.getNombre_tratamiento());
            ps.setString(3, t.getDescripcion());
            ps.setFloat(4, t.getCosto_base());
            ps.setInt(5, t.getDuracion_estimada_minutos());
            ps.setBoolean(6, t.isActivo());
            ps.setInt(7, t.getId_tratamiento());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar tratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Eliminar tratamiento
    public void eliminar(int id) {
        String sql = "DELETE FROM tratamiento WHERE id_tratamiento = ?";

        try {
            conn = ds.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar tratamiento: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Cerrar recursos
    private void cerrarRecursos() {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}
