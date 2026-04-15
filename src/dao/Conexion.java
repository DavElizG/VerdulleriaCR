// Este paquete agrupa todo lo que tiene que ver con la base de datos
package dao;

// Jackcess es la librería que nos permite crear el archivo .accdb desde Java
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import java.io.File;
import java.io.IOException;
// Estas son las clases de Java para trabajar con bases de datos (JDBC)
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Esta clase maneja la conexión con la base de datos Access.
 * Todos los métodos son estáticos porque solo necesitamos una sola
 * conexión para toda la aplicación, no tiene sentido crear objetos de esta clase.
 */
public class Conexion {

    // Ruta donde se guarda el archivo de Access. user.dir es la carpeta raíz del proyecto
    private static final String DB_PATH = System.getProperty("user.dir") + "/db/VerduleriaCR.accdb";

    // URL de conexión que usa UCanAccess para conectarse al archivo .accdb
    private static final String URL = "jdbc:ucanaccess://" + DB_PATH;

    /*
     * Este bloque "static" se ejecuta automáticamente cuando la clase se carga
     * por primera vez. Así nos aseguramos de que la base de datos esté lista
     * antes de que alguien intente usarla.
     */
    static {
        crearArchivoSiNoExiste();
        inicializarDB();
    }

    /*
     * Retorna una conexión abierta a la base de datos.
     * Cada DAO llama este método para obtener su conexión.
     * Si algo falla, retorna null y muestra el error.
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error de conexión a Access: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Revisa si el archivo .accdb ya existe. Si no existe, lo crea usando Jackcess.
     * Usamos el formato V2010 porque es el más compatible con Access moderno.
     * Jackcess es necesario aquí porque UCanAccess solo abre archivos existentes,
     * no los crea desde cero.
     */
    private static void crearArchivoSiNoExiste() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            try {
                // Crea la carpeta "db" si tampoco existe
                dbFile.getParentFile().mkdirs();
                Database db = new DatabaseBuilder(dbFile)
                        .setFileFormat(Database.FileFormat.V2010)
                        .create();
                db.close();
                System.out.println("Archivo VerduleriaCR.accdb creado.");
            } catch (IOException e) {
                System.out.println("Error al crear el archivo .accdb: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /*
     * Crea las tablas si todavía no existen en la base de datos.
     * Esto sirve para que la primera vez que se corra el programa,
     * la base de datos quede lista sin tener que hacer nada manual.
     */
    private static void inicializarDB() {
        try (Connection con = DriverManager.getConnection(URL);
             Statement st = con.createStatement()) {

            // DatabaseMetaData nos da información sobre las tablas que ya existen
            DatabaseMetaData meta = con.getMetaData();

            // Solo crea la tabla si no existe — así no da error si ya fue creada antes
            if (!tablaExiste(meta, "Clientes")) {
                st.execute("CREATE TABLE Clientes ("
                        + "cedula VARCHAR(20) NOT NULL PRIMARY KEY, "
                        + "nombre VARCHAR(100), "
                        + "telefono VARCHAR(20), "
                        + "direccion VARCHAR(200), "
                        + "email VARCHAR(100))");
            }

            if (!tablaExiste(meta, "Productos")) {
                st.execute("CREATE TABLE Productos ("
                        + "id AUTOINCREMENT PRIMARY KEY, " // AUTOINCREMENT es el equivalente a AUTO_INCREMENT en Access
                        + "nombre VARCHAR(100), "
                        + "precio DOUBLE, "
                        + "stock DOUBLE)");
            }

            if (!tablaExiste(meta, "Facturas")) {
                st.execute("CREATE TABLE Facturas ("
                        + "id AUTOINCREMENT PRIMARY KEY, "
                        + "cliente_cedula VARCHAR(20), "
                        + "fecha DATETIME, "
                        + "total DOUBLE)");
            }

            // Esta tabla guarda cada línea de producto dentro de una factura
            if (!tablaExiste(meta, "DetalleFacturas")) {
                st.execute("CREATE TABLE DetalleFacturas ("
                        + "id AUTOINCREMENT PRIMARY KEY, "
                        + "factura_id INTEGER, "   // referencia a qué factura pertenece
                        + "producto_id INTEGER, "  // referencia al producto vendido
                        + "cantidad DOUBLE, "
                        + "subtotal DOUBLE)");
            }

        } catch (SQLException e) {
            System.out.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Método auxiliar que consulta los metadatos de la base de datos
     * para saber si una tabla con ese nombre ya existe.
     * Usamos toUpperCase() porque Access guarda los nombres en mayúsculas.
     */
    private static boolean tablaExiste(DatabaseMetaData meta, String tabla) throws SQLException {
        try (ResultSet rs = meta.getTables(null, null, tabla.toUpperCase(), null)) {
            return rs.next(); // si hay al menos un resultado, la tabla existe
        }
    }
}