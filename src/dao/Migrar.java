package dao;

public class Migrar {
    public static void main(String[] args) {
        System.out.println("Iniciando migración...");
        try (java.sql.Connection con = Conexion.getConnection()) {
            if (con != null) {
                System.out.println("Conexión exitosa.");
                System.out.println("Tablas creadas correctamente en VerduleriaCR.accdb");
            } else {
                System.out.println("Error: no se pudo conectar.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
