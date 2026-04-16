package vista;

import java.awt.*;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import javax.swing.*;
import modelo.DetalleFactura;
import modelo.Factura;

/*
 * Esta clase muestra el reporte de la factura después de guardarla.
 * La investigamos porque el enunciado pedía generar una factura como punto extra.
 *
 * Lo que descubrimos es que Java Swing ya tiene un método .print() incluido en JTextArea
 * que abre directamente el diálogo de impresoras del sistema, sin necesidad de instalar
 * ninguna librería adicional como JasperReports.
 *
 * Usamos JDialog en vez de JFrame porque JDialog puede ser "modal",
 * eso significa que bloquea la ventana de atrás mientras esté abierto,
 * así el usuario no puede seguir usando el sistema hasta cerrar el reporte.
 *
 * El IVA lo calculamos con el método calcularPrecioConImpuesto() que viene de la
 * interfaz Vendible que implementa la clase Factura. Esto lo pusimos así para
 * demostrar que las interfaces sirven para algo concreto en el proyecto.
 */
public class VistaFacturaForm extends JDialog {

    // Constantes de formato para evitar repetir las mismas cadenas
    private static final String LINEA_DOBLE = "================================================\n";
    private static final String LINEA_SIMPLE = "------------------------------------------------\n";
    private static final String FMT_TOTALES  = "%-32s %10.2f%n";

    /*
     * Constructor: recibe el frame padre y la factura que se acaba de guardar.
     * El true al final hace que el diálogo sea modal (bloquea la ventana de atrás).
     */
    public VistaFacturaForm(Frame parent, Factura factura) {
        super(parent, "Factura N° " + factura.getId(), true);
        initComponents(factura);
    }

    // Construye los componentes de la ventana manualmente (sin diseñador)
    private void initComponents(Factura factura) {
        setSize(460, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Variable local final para que el lambda de impresión pueda acceder a ella
        final JTextArea areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaTexto.setEditable(false);
        areaTexto.setMargin(new Insets(10, 10, 10, 10));
        areaTexto.setText(generarTextoFactura(factura));

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        // Panel de botones abajo
        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // El método .print() de JTextArea abre directamente el diálogo de impresión del sistema
        btnImprimir.addActionListener(e -> {
            try {
                areaTexto.print(
                    null,                                    // encabezado
                    new MessageFormat("Página {0}")         // pie de página
                );
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage());
            }
        });

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    /*
     * Genera el texto de la factura con formato de recibo.
     * Usamos StringBuilder para ir armando línea por línea.
     * String.format con %-Xs alinea el texto a la izquierda en X caracteres.
     */
    private String generarTextoFactura(Factura factura) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();

        sb.append(LINEA_DOBLE);
        sb.append("             VERDULERÍA CR                      \n");
        sb.append("       Sistema de Facturación                   \n");
        sb.append(LINEA_DOBLE);
        sb.append(String.format("Factura N°: %d%n", factura.getId()));
        sb.append(String.format("Fecha:      %s%n", sdf.format(factura.getFecha())));
        sb.append(LINEA_SIMPLE);
        sb.append(String.format("Cliente:    %s%n", factura.getCliente().getNombre()));
        sb.append(String.format("Cédula:     %s%n", factura.getCliente().getCedula()));
        sb.append(String.format("Teléfono:   %s%n", factura.getCliente().getTelefono()));
        sb.append(LINEA_SIMPLE);
        sb.append(String.format("%-18s %5s %8s %10s%n", "Producto", "Cant.", "Precio", "Subtotal"));
        sb.append(LINEA_SIMPLE);

        // Recorremos cada línea de la factura
        for (DetalleFactura d : factura.getDetalles()) {
            sb.append(String.format("%-18s %5.2f %8.2f %10.2f%n",
                d.getProducto().getNombre(),
                d.getCantidad(),
                d.getProducto().getPrecio(),
                d.getSubtotal()));
        }

        // Cálculo del resumen de totales
        double subtotal = factura.getTotal();
        double iva      = subtotal * 0.13;
        double total    = factura.calcularPrecioConImpuesto(); // usa el método de la interfaz Vendible

        sb.append(LINEA_SIMPLE);
        sb.append(String.format(FMT_TOTALES, "Subtotal (₡):", subtotal));
        sb.append(String.format(FMT_TOTALES, "IVA 13% (₡):", iva));
        sb.append(String.format(FMT_TOTALES, "TOTAL (₡):", total));
        sb.append(LINEA_DOBLE);
        sb.append("          ¡Gracias por su compra!               \n");
        sb.append(LINEA_DOBLE);

        return sb.toString();
    }
}
