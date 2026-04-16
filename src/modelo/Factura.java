package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Representa una factura de venta.
 * Implementa dos interfaces: Vendible (para calcular el precio con IVA)
 * e Imprimible (para generar el reporte en papel).
 * Usar interfaces nos obliga a implementar esos métodos y hace el código más ordenado.
 */
public class Factura implements Vendible, Imprimible {
    private static final Logger logger = Logger.getLogger(Factura.class.getName());
    private int id;          // lo asigna la base de datos
    private final Date fecha;      // se asigna automáticamente cuando se crea la factura
    private Cliente cliente;
    private final List<DetalleFactura> detalles = new ArrayList<>();
    private double total;    // se va acumulando con cada producto que se agrega

    // Al crear la factura, guardamos la fecha y hora actuales
    public Factura() {
        this.fecha = new Date();
    }

    /*
     * Agrega un producto a la factura y suma su subtotal al total general.
     * Cada vez que llamamos este método la factura se va "armando".
     */
    public void agregarDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        total += detalle.getSubtotal(); // acumulamos el total
    }

    public List<DetalleFactura> getDetalles() { return detalles; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public double getTotal() { return total; }
    public Date getFecha() { return fecha; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Método de la interfaz Vendible: aplica el 13% de IVA al total
    @Override
    public double calcularPrecioConImpuesto() {
        return total * 1.13;
    }

    // Método de la interfaz Imprimible: imprime un resumen de la factura en el logger
    @Override
    public void generarReporte() {
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "=== Factura N\u00b0 {0} ===", id);
            logger.log(Level.INFO, "Cliente: {0} | C\u00e9dula: {1}",
                new Object[]{cliente.getNombre(), cliente.getCedula()});
            for (DetalleFactura d : detalles) {
                logger.log(Level.INFO, "  {0} x{1} = {2}",
                    new Object[]{d.getProducto().getNombre(), d.getCantidad(), d.getSubtotal()});
            }
            logger.log(Level.INFO, "  TOTAL con IVA: {0}", calcularPrecioConImpuesto());
        }
    }
}