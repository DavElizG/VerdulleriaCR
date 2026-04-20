# VerduleriaCR - Sistema de Facturación

**Curso:** BIS34 Programación 1  
**Profesora:** Kimberly Villegas Cubillo  
**Fecha de Entrega:** 21-04-2026  
**Valor:** 20%  
**Integrantes:** 

---

## Descripción del Proyecto

Sistema de facturación para una verdulería desarrollado en Java con interfaz gráfica (JFrame), base de datos Microsoft Access y aplicación de los conceptos de Programación Orientada a Objetos vistos en clase.

---

## Cumplimiento de Requisitos

### ✅ 1. Formularios / JFrames — (50 pts)

| Formulario | Archivo | Descripción |
|---|---|---|
| Menú Principal | `src/vista/MainFrame.java` | Ventana principal con navegación al resto del sistema |
| Gestión de Clientes | `src/vista/GestionClientesForm.java` | Registro, edición y eliminación de clientes |
| Gestión de Productos | `src/vista/GestionProductosForm.java` | Registro, edición y eliminación de productos |
| Nueva Factura | `src/vista/NuevaFacturaForm.java` | Generación de facturas con detalle de productos |

---

### ✅ 2. Clases — (25 pts)

| Clase | Archivo | Descripción |
|---|---|---|
| `Persona` | `src/modelo/Persona.java` | Clase abstracta base con nombre, apellidos y cédula |
| `Cliente` | `src/modelo/Cliente.java` | Hereda de `Persona` — herencia simple |
| `Producto` | `src/modelo/Producto.java` | Representa un producto con precio y stock |
| `Factura` | `src/modelo/Factura.java` | Implementa `Vendible` e `Imprimible` — herencia múltiple mediante interfaces |
| `DetalleFactura` | `src/modelo/DetalleFactura.java` | Línea de detalle de una factura (producto + cantidad + subtotal) |
| `Vendible` | `src/modelo/Vendible.java` | Interface que define el comportamiento de venta |
| `Imprimible` | `src/modelo/Imprimible.java` | Interface que define el comportamiento de impresión |

**Herencia aplicada:**
- **Simple:** `Cliente` extiende `Persona`
- **Múltiple (via interfaces):** `Factura` implementa `Vendible` e `Imprimible`

Cada clase define sus propios atributos y métodos según el contexto del sistema.

---

### ✅ 3. Conexión a Base de Datos ACCESS — (25 pts)

| Componente | Archivo | Descripción |
|---|---|---|
| Conexión | `src/dao/Conexion.java` | Maneja la conexión JDBC a Microsoft Access usando UCanAccess. Crea el archivo `.accdb` automáticamente si no existe |
| DAO Clientes | `src/dao/ClienteDAO.java` | Operaciones CRUD sobre la tabla `Clientes` |
| DAO Productos | `src/dao/ProductoDAO.java` | Operaciones CRUD sobre la tabla `Productos` |
| DAO Facturas | `src/dao/FacturaDAO.java` | Guarda facturas completas con transacciones (commit/rollback) |
| Base de datos | `db/VerduleriaCR.accdb` | Archivo Access con las tablas: `Clientes`, `Productos`, `Facturas`, `DetalleFacturas` |

**Driver utilizado:** UCanAccess 5.0.1 (librerías incluidas en `lib/`)

---

### ✅ 4. Reporte / Factura — (Puntos extra: 2%)

Al guardar una factura desde `src/vista/NuevaFacturaForm.java`, se abre automáticamente una ventana de reporte (`src/vista/VistaFacturaForm.java`) que:
- Muestra el recibo completo con formato de tiquete: cliente, productos, cantidad, precio unitario, subtotales
- Calcula IVA 13% y total con impuesto usando el método `calcularPrecioConImpuesto()` de la interfaz `Vendible`
- Incluye botón **Imprimir** que abre el diálogo de impresión del sistema operativo
- El método `generarReporte()` de la interfaz `Imprimible` registra un resumen de la factura en el logger de Java

#### ¿Cómo se investigó e implementó la impresión?

Java Swing incluye en la clase `JTextComponent` (padre de `JTextArea`) un método llamado `.print()` que se conecta directamente con la API de impresión del sistema operativo (`java.awt.print`). Esto significa que **no se necesita ninguna librería externa** como JasperReports o iText.

**Flujo completo del reporte:**

```
1. Usuario llena la factura y presiona "Guardar"
        ↓
2. FacturaDAO guarda la factura y sus detalles en Access (con transacción commit/rollback)
        ↓
3. NuevaFacturaForm crea: new VistaFacturaForm(null, facturaActual).setVisible(true)
        ↓
4. VistaFacturaForm.generarTextoFactura() construye el recibo como texto formateado
        ↓
5. El texto se muestra en un JTextArea (solo lectura, fuente Monospaced para alinear columnas)
        ↓
6. Si el usuario presiona "Imprimir" → areaTexto.print() abre el diálogo de impresoras del SO
```

**Decisiones de diseño:**
- Se usó `JDialog` en lugar de `JFrame` para la ventana del reporte porque `JDialog` puede ser **modal** (bloquea la ventana anterior mientras está abierta).
- El IVA se calcula con `factura.calcularPrecioConImpuesto()`, método de la interfaz `Vendible` — esto demuestra el uso práctico de interfaces y herencia múltiple en Java.
- El formato del recibo usa `String.format` con especificadores de ancho (`%-18s`, `%10.2f`) para que las columnas queden alineadas en la fuente monoespaciada.

**Código clave en `VistaFacturaForm.java`:**
```java
// Abre el diálogo de impresión del sistema operativo directamente
btnImprimir.addActionListener(e -> {
    try {
        areaTexto.print(null, new MessageFormat("Página {0}"));
    } catch (PrinterException ex) {
        JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage());
    }
});
```

---

## Estructura del Proyecto

```
VerduleriaCR/
├── src/
│   ├── dao/           → Acceso a datos (Conexion, ClienteDAO, ProductoDAO, FacturaDAO)
│   ├── modelo/        → Clases del dominio (Persona, Cliente, Producto, Factura, etc.)
│   └── vista/         → Formularios JFrame (MainFrame, Clientes, Productos, Factura)
├── lib/               → Librerías UCanAccess (incluidas, no requiere instalación)
├── db/                → Base de datos VerduleriaCR.accdb
└── nbproject/         → Configuración del proyecto NetBeans
```

---

## Instrucciones para Ejecutar

1. Clonar o descomprimir el proyecto
2. Abrir en **NetBeans**
3. Las librerías ya están incluidas en `lib/` — no se necesita agregar nada
4. Ejecutar con **Shift + F6** o el botón Run
5. La base de datos se crea automáticamente en `db/` al primer inicio

> **Nota:** No abrir el archivo `VerduleriaCR.accdb` en Microsoft Access mientras la aplicación está corriendo.
