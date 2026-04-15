package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;
import modelo.DetalleFactura;
import modelo.Factura;
import modelo.Producto;

/*
 * DAO que maneja la creación de facturas.
 * También incluye métodos de apoyo para buscar clientes y productos
 * que se necesitan al momento de armar una factura nueva.
 */
public class FacturaDAO {

    /*
     * Guarda una factura completa: primero inserta la cabecera (Facturas)
     * y luego inserta cada línea de detalle (DetalleFacturas).
     * 
     * Se usa una transacción para que si algo falla a mitad del proceso,
     * se deshaga todo y no queden datos a medias en la base de datos.
     * 
     * Retorna el id generado para la factura, o -1 si ocurrió un error.
     */
    public int guardarFactura(Factura factura) {
        String sqlFactura = "INSERT INTO Facturas (cliente_cedula, fecha, total) VALUES (?, ?, ?)";
        String sqlDetalle = "INSERT INTO DetalleFacturas (factura_id, producto_id, cantidad, subtotal) VALUES (?, ?, ?, ?)";

        Connection con = null;
        try {
            con = Conexion.getConnection();
            // Desactivamos el autocommit para manejar la transacción manualmente
            con.setAutoCommit(false);

            // --- Paso 1: insertar la factura principal ---
            // RETURN_GENERATED_KEYS le dice a JDBC que queremos recuperar el id generado
            PreparedStatement psFactura = con.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS);
            psFactura.setString(1, factura.getCliente().getCedula());
            // Convertimos Date a Timestamp porque JDBC lo requiere para campos DATETIME
            psFactura.setTimestamp(2, java.sql.Timestamp.valueOf(
                    factura.getFecha().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()));
            psFactura.setDouble(3, factura.getTotal());
            psFactura.executeUpdate();

            // Leemos el id que generó la base de datos para esta factura
            ResultSet rs = psFactura.getGeneratedKeys();
            int facturaId = 0;
            if (rs.next()) {
                facturaId = rs.getInt(1);
                factura.setId(facturaId); // también lo guardamos en el objeto
            }

            // --- Paso 2: insertar cada línea de detalle ---
            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
            for (DetalleFactura det : factura.getDetalles()) {
                psDetalle.setInt(1, facturaId);                    // a qué factura pertenece
                psDetalle.setInt(2, det.getProducto().getId());    // qué producto se vendió
                psDetalle.setDouble(3, det.getCantidad());
                psDetalle.setDouble(4, det.getSubtotal());
                psDetalle.executeUpdate();
            }

            // Si todo salió bien confirmamos los cambios
            con.commit();
            return facturaId;

        } catch (SQLException e) {
            // Si algo falló, deshacemos todo lo que se hizo en esta transacción
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;
        } finally {
            // El finally siempre se ejecuta — devolvemos el autocommit a su estado normal
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Trae la lista de productos disponibles.
     * Se usa en el formulario de nueva factura para que el usuario
     * pueda elegir qué productos agregar.
     */
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Productos";

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
     * Busca un cliente por cédula para cargarlo en la factura.
     * Si no existe ese número de cédula, retorna null.
     */
    public Cliente buscarCliente(String cedula) {
        String sql = "SELECT * FROM Clientes WHERE cedula = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Cliente(
                    rs.getString("cedula"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}