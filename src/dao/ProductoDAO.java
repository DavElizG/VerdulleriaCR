package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

/*
 * DAO de la tabla Productos.
 * Maneja todo lo que tenga que ver con agregar, modificar,
 * eliminar y consultar productos de la verdulería.
 */
public class ProductoDAO {

    /*
     * Inserta un producto nuevo. El id no lo ponemos nosotros,
     * lo genera automáticamente la base de datos (AUTOINCREMENT).
     */
    public boolean insertar(Producto p) {
        String sql = "INSERT INTO Productos (nombre, precio, stock) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setDouble(3, p.getStock());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Actualiza el nombre, precio y stock de un producto existente.
     * Lo identificamos por el id, que es único para cada producto.
     */
    public boolean actualizar(Producto p) {
        String sql = "UPDATE Productos SET nombre = ?, precio = ?, stock = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setDouble(3, p.getStock());
            ps.setInt(4, p.getId()); // el id va al final porque es el del WHERE

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Elimina el producto con ese id de la base de datos
    public boolean eliminar(int id) {
        String sql = "DELETE FROM Productos WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Devuelve todos los productos ordenados por nombre alfabéticamente.
     * ORDER BY nombre hace que vengan ya ordenados desde la base de datos.
     */
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos ORDER BY nombre";

        try (Connection con = Conexion.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getDouble("stock")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /*
     * Busca un producto específico por su id.
     * Retorna null si no lo encuentra, por eso hay que verificar antes de usar el resultado.
     */
    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM Productos WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            // if en vez de while porque esperamos solo un resultado (id es único)
            if (rs.next()) {
                return new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getDouble("stock")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Resta la cantidad vendida del stock del producto.
     * La condición "stock >= cantidadVendida" en el WHERE evita que el stock
     * quede en negativo — si no hay suficiente, sencillamente no ejecuta el UPDATE.
     */
    public boolean actualizarStock(int productoId, double cantidadVendida) {
        String sql = "UPDATE Productos SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, cantidadVendida);
            ps.setInt(2, productoId);
            ps.setDouble(3, cantidadVendida); // validamos que haya suficiente stock
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Busca productos cuyo nombre contenga el texto ingresado.
     * Los % en el LIKE son comodines: %papa% encuentra "papa", "papas", "camote papa", etc.
     */
    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos WHERE nombre LIKE ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getDouble("stock")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}