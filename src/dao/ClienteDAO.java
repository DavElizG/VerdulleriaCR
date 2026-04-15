package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

/*
 * Clase que se encarga de todas las operaciones de la tabla Clientes.
 * DAO significa "Data Access Object" — es un patrón que separa la lógica
 * de negocio del acceso a datos. Cada tabla tiene su propio DAO.
 */
public class ClienteDAO {

    /*
     * Inserta un cliente nuevo en la base de datos.
     * Usamos PreparedStatement en vez de concatenar el SQL directamente
     * porque así evitamos inyección SQL (un tipo de ataque muy común).
     * Retorna true si se insertó al menos una fila, false si algo falló.
     */
    public boolean insertar(Cliente c) {
        // Los signos ? son marcadores de posición que llenamos luego con setString
        String sql = "INSERT INTO Clientes (cedula, nombre, telefono, direccion, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Asignamos cada ? con el dato correspondiente del objeto Cliente
            ps.setString(1, c.getCedula());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.setString(5, c.getEmail());

            // executeUpdate retorna cuántas filas se afectaron; si es > 0 fue exitoso
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Actualiza los datos de un cliente que ya existe.
     * La cédula no se puede cambiar porque es la llave primaria (PRIMARY KEY),
     * por eso va en el WHERE y no en el SET.
     */
    public boolean actualizar(Cliente c) {
        String sql = "UPDATE Clientes SET nombre = ?, telefono = ?, direccion = ?, email = ? WHERE cedula = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getCedula()); // este es el filtro del WHERE
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Elimina un cliente buscándolo por su cédula.
     * Solo recibimos la cédula como String, no necesitamos el objeto completo.
     */
    public boolean eliminar(String cedula) {
        String sql = "DELETE FROM Clientes WHERE cedula = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * Trae todos los clientes y los mete en una lista.
     * El ResultSet es como un cursor que va fila por fila.
     * Por cada fila creamos un objeto Cliente y lo agregamos a la lista.
     */
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM Clientes";

        // try-with-resources cierra automáticamente la conexión al terminar
        try (Connection con = Conexion.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // rs.getString("columna") lee el valor de esa columna en la fila actual
                Cliente c = new Cliente(
                    rs.getString("cedula"),
                    rs.getString("nombre"),
                    rs.getString("telefono"),
                    rs.getString("direccion"),
                    rs.getString("email")
                );
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}