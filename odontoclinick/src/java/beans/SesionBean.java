package beans;

import dao.UsuarioDAO;
import java.io.IOException;
import modelo.Usuario;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "sesionBean")
@SessionScoped
public class SesionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // IMPORTANTE: No hacemos "new UsuarioDAO()" aquí para evitar pantalla blanca.
    private UsuarioDAO usuarioDAO;

    // Variables del formulario
    private String usuario = "";
    private String contrasena = "";

    private Usuario usuarioLogueado;

    // --- LÓGICA DE LOGIN ---
    public String iniciarSesion() {
        try {
            // Inicializamos el DAO aquí (Lazy Loading)
            if (usuarioDAO == null) {
                usuarioDAO = new UsuarioDAO();
            }

            Usuario u = usuarioDAO.login(usuario, contrasena);

            if (u != null) {
                this.usuarioLogueado = u;
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", u.getNombre()));

                // Redirección al Dashboard
                return "/dashboard/index.xhtml?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas o usuario inactivo."));
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Error de conexión: " + e.getMessage()));
            return null;
        }
    }

    // --- LÓGICA DE LOGOUT ---
    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        // CORRECCIÓN: Ruta con 'auth' en minúscula
        return "/auth/login.xhtml?faces-redirect=true";
    }

    // --- SEGURIDAD EN VISTAS ---
    public void verificarSesion() {
        try {
            if (usuarioLogueado == null) {
                FacesContext context = FacesContext.getCurrentInstance();
                String path = context.getExternalContext().getRequestContextPath();
                // CORRECCIÓN: Ruta con 'auth' en minúscula
                context.getExternalContext().redirect(path + "/auth/login.xhtml");
            }
        } catch (IOException e) {
            System.out.println("Error en redirección: " + e.getMessage());
        }
    }

    // --- HELPERS PARA VISTAS (Roles) ---
    
    public boolean isLogueado() { return usuarioLogueado != null; }

    public boolean esAdmin() {
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 1;
    }

    public boolean esMedico() {
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 2;
    }

    public boolean esSecretaria() {
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 3;
    }

    public boolean esPaciente() {
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 4;
    }

    // --- Getters y Setters ---

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public void setUsuarioLogueado(Usuario usuarioLogueado) { this.usuarioLogueado = usuarioLogueado; }
}