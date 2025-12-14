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

    // Inicialización controlada por JSF
    @PostConstruct
    public void init() {
        usuario = "";
        contrasena = "";
        usuarioLogueado = null;
    }

    // LOGIN
    public String iniciarSesion() {
        try {
            if (usuarioDAO == null) {
                usuarioDAO = new UsuarioDAO();
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
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_FATAL,
                    "Error", "Error de conexión."));
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
