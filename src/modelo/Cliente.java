package modelo;

/*
 * Cliente hereda de Persona, lo que significa que ya tiene cedula,
 * nombre y teléfono. Acá solo agregamos lo que es exclusivo del cliente:
 * la dirección y el email.
 * 
 * Herencia nos ahorra repetir código que Persona ya tiene.
 */
public class Cliente extends Persona {
    private String direccion;
    private String email;

    // El constructor llama a super() para inicializar los campos de Persona
    public Cliente(String cedula, String nombre, String telefono, String direccion, String email) {
        super(cedula, nombre, telefono);
        this.direccion = direccion;
        this.email = email;
    }

    public String getDireccion() { return direccion; }
    public String getEmail() { return email; }

    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setEmail(String email) { this.email = email; }
}