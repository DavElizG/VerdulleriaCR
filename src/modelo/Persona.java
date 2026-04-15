package modelo;

/*
 * Clase abstracta que representa a cualquier persona del sistema.
 * Es abstracta porque nunca vamos a crear una Persona sola —
 * siempre va a ser un Cliente o algún otro tipo específico.
 * 
 * Las clases que hereden de esta reciben automáticamente cedula, nombre y teléfono.
 */
public abstract class Persona {
    // protected para que las subclases puedan acceder directamente si hace falta
    protected String cedula;
    protected String nombre;
    protected String telefono;

    // Constructor que obliga a dar los tres datos básicos al crear una persona
    public Persona(String cedula, String nombre, String telefono) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    // Getters para leer los datos desde afuera de la clase
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }

    // Setters para modificar los datos
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
