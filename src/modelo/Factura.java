package modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Representa una factura de venta.
 * Implementa dos interfaces: Vendible (para calcular el precio con IVA)
 * e Imprimible (para generar el reporte en papel).
 * Usar interfaces nos obliga a implementar esos métodos y hace el código más ordenado.
 */
public class Factura implements Vendible, Imprimible {
    private int id;          // lo asigna la base de datos
    private Date fecha;      // se asigna automáticamente cuando se crea la factura
    private Cliente cliente;
    private List<DetalleFactura> detalles = new ArrayList<>();
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

    // Método de la interfaz Imprimible: aquí iría la lógica para imprimir
    @Override
    public void generarReporte() {
        // Se implementará más adelante con Jasper
        System.out.println("Factura generada para cliente: " + cliente.getNombre());
    }
}