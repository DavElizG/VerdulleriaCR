package modelo;

/*
 * Representa un producto que se vende en la verdulería.
 * El stock puede ser en kilos (ej: tomates) o unidades (ej: pinas),
 * por eso usamos double en vez de int.
 */
public class Producto {
    private int id;       // id lo asigna Access automáticamente
    private String nombre;
    private double precio;
    private double stock; // en kilos o unidades según el producto

    public Producto(int id, String nombre, double precio, double stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public double getStock() { return stock; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setStock(double stock) { this.stock = stock; }
}