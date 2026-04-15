package modelo;

/*
 * Interfaz que deben implementar las clases que tienen precio con impuesto.
 * En Costa Rica el IVA es del 13%.
 * Al usar una interfaz nos aseguramos de que cualquier clase "vendible"
 * siempre tenga este método disponible.
 */
public interface Vendible {
    double calcularPrecioConImpuesto(); // 13% IVA
}