package beans;

import dao.UsuarioDAO;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import modelo.Usuario;

@ManagedBean(name = "sesionBean")
@SessionScoped
public class SesionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private UsuarioDAO usuarioDAO;

    // Campos del formulario
    private String usuario;
    private String contrasena;

    // Usuario autenticado
    private Usuario usuarioLogueado;

    @PostConstruct
    public void init() {
        usuario = "";
        contrasena = "";
        usuarioLogueado = null;
        
        // CORRECCIÓN 1: Inicializar el DAO aquí. Si falla la BD, fallará en el PostConstruct, 
        // pero aseguramos que el objeto no es nulo dentro del método iniciarSesion().
        try {
             usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
             System.err.println("ERROR FATAL AL INICIALIZAR USUARIODAO: " + e.getMessage());
             // Puedes dejar usuarioDAO como null, pero el iniciarSesion debe manejarlo.
        }
    }

    // LOGIN
    public String iniciarSesion() {
        try {
            if (usuarioDAO == null) {
                // Si la inicialización del DAO falló en @PostConstruct (Problema de BD/Configuración)
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                        "Error de conexión", "El sistema no pudo iniciar el acceso a la base de datos."));
                return null;
            }
            
            Usuario u = usuarioDAO.login(usuario, contrasena);

            if (u != null) {
                usuarioLogueado = u;
                return "/dashboard/index.xhtml?faces-redirect=true";
            }

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Credenciales incorrectas o usuario inactivo."));
            return null;

        } catch (Exception e) {
            // Este catch capturaría un error SQL si el login falla a nivel de base de datos
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Error", "Error de conexión en el login."));
            
            e.printStackTrace(); // Imprime el stack trace para depuración
            
            return null;
        }
    }

    // LOGOUT
    public String cerrarSesion() {
        FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .invalidateSession();
        return "/auth/login.xhtml?faces-redirect=true";
    }

    // PROTECCIÓN DE VISTAS
    public void verificarSesion() throws IOException {
        if (usuarioLogueado == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().redirect(
                context.getExternalContext().getRequestContextPath()
                + "/auth/login.xhtml");
        }
    }

    // ROLES
    public boolean isLogueado() {
        return usuarioLogueado != null;
    }

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

    // GETTERS & SETTERS
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Usuario getUsuarioLogueado() { return usuarioLogueado; }
    public void setUsuarioLogueado(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
}