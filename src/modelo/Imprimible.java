package modelo;

/*
 * Interfaz para las clases que pueden generar un reporte imprimible.
 * Por ahora solo Factura la implementa, pero si en el futuro
 * quisiéramos imprimir órdenes o recibos, también implementarían esta interfaz.
 */
public interface Imprimible {
    void generarReporte();
}