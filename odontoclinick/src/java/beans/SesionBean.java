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

    // DAO
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Variables del formulario de Login
    private String usuario;
    private String contrasena;

    // Objeto que almacena los datos del usuario logueado (Equivalente a Auth::user())
    private Usuario usuarioLogueado;

    // --- LÓGICA DE LOGIN (Reemplaza AuthController@login) ---
    public String iniciarSesion() {
        try {
            Usuario u = usuarioDAO.login(usuario, contrasena);

            if (u != null) {
                // Login exitoso
                this.usuarioLogueado = u;
                
                // Mensaje flash (opcional)
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", u.getNombre()));

                // Redirección al Dashboard según Rol (Similar al switch de Laravel)
                // Usamos 'faces-redirect=true' para cambiar la URL del navegador
                return "/dashboard/index.xhtml?faces-redirect=true";
            } else {
                // Login fallido
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Credenciales incorrectas o usuario inactivo."));
                return null; // Se queda en la misma página
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Error de conexión"));
            return null;
        }
    }

    // --- LÓGICA DE LOGOUT (Reemplaza AuthController@logout) ---
    public String cerrarSesion() {
        // Invalida la sesión HTTP completa
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/auth/login.xhtml?faces-redirect=true";
    }

    // --- SEGURIDAD EN VISTAS (Middleware) ---
    // Coloca esto en tus vistas protegidas: <f:event type="preRenderView" listener="#{sesionBean.verificarSesion}" />
    public void verificarSesion() {
        try {
            if (usuarioLogueado == null) {
                FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/auth/login.xhtml");
            }
        } catch (IOException e) {
            // Manejo de error en redirección
        }
    }

    // --- HELPERS PARA VISTAS (Para usar en 'rendered' de JSF) ---
    
    public boolean isLogueado() {
        return usuarioLogueado != null;
    }

    public boolean esAdmin() {
        // id_rol 1 = Administrador (según tu SQL)
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 1;
    }

    public boolean esMedico() {
        // id_rol 2 = Doctor
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 2;
    }

    public boolean esSecretaria() {
        // id_rol 3 = Secretaria
        return usuarioLogueado != null && usuarioLogueado.getId_rol() == 3;
    }

    public boolean esPaciente() {
        // id_rol 4 = Paciente
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