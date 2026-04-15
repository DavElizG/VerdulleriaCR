package modelo;

/*
 * Representa una línea dentro de una factura.
 * Cada vez que se agrega un producto a la factura, se crea un DetalleFactura.
 * Por ejemplo: "2.5 kg de tomate a ¢500 = ¢1250"
 */
public class DetalleFactura {
    private Producto producto;
    private double cantidad;  // kilos o unidades vendidas
    private double subtotal;  // precio * cantidad, se calcula automáticamente

    /*
     * Al crear el detalle calculamos el subtotal de una vez.
     * Así no tenemos que calcularlo después cada vez que lo necesitemos.
     */
    public DetalleFactura(Producto producto, double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad;
    }

    public Producto getProducto() { return producto; }
    public double getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
}