package beans;

import dao.EspecialidadDAO;
import dao.RolDAO;
import dao.UsuarioDAO;
import modelo.Especialidad;
import modelo.Medico;
import modelo.Paciente;
import modelo.Rol;
import modelo.Secretaria;
import modelo.Usuario;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class RegistroBean implements Serializable {

    // DAOs
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    // Objetos para capturar datos
    private Usuario nuevoUsuario;
    private Paciente nuevoPaciente;
    private Medico nuevoMedico;
    private Secretaria nuevaSecretaria;

    // Variables auxiliares
    private String confirmacionPassword;
    private List<Rol> listaRoles;
    private List<Especialidad> listaEspecialidades;
    
    // Control de vista
    private int idRolSeleccionado;

    @PostConstruct
    public void init() {
        nuevoUsuario = new Usuario();
        nuevoPaciente = new Paciente();
        nuevoMedico = new Medico();
        nuevaSecretaria = new Secretaria();
        
        // Cargar listas para <f:selectItems>
        listaRoles = rolDAO.listarTodos(); // Asegúrate de tener este método en RolDAO
        listaEspecialidades = especialidadDAO.listar(); // Asegúrate de tener este método en EspecialidadDAO
    }

    // --- LÓGICA DE REGISTRO (Reemplaza AuthController@register) ---
    public String registrar() {
        try {
            // 1. Validaciones básicas
            if (!nuevoUsuario.getContrasena().equals(confirmacionPassword)) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden."));
                return null;
            }

            if (usuarioDAO.existeUsuario(nuevoUsuario.getNombre_usuario())) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre de usuario ya está en uso."));
                return null;
            }

            // 2. Registro según Rol (Switch similar a Laravel) [cite: 311-316]
            switch (idRolSeleccionado) {
                case 2: // Médico
                    // Validar campos de médico si es necesario
                    usuarioDAO.registrarMedicoTransaccion(nuevoUsuario, nuevoMedico);
                    break;
                case 3: // Secretaria
                    usuarioDAO.registrarSecretariaTransaccion(nuevoUsuario, nuevaSecretaria);
                    break;
                case 4: // Paciente
                    usuarioDAO.registrarPacienteTransaccion(nuevoUsuario, nuevoPaciente);
                    break;
                default:
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Seleccione un rol válido."));
                    return null;
            }

            // 3. Éxito y Redirección
            // Opcional: Auto-login aquí llamando a SesionBean, como hace Laravel Auth::login($user)
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado correctamente. Inicie sesión."));
            
            return "/auth/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error crítico", "No se pudo registrar: " + e.getMessage()));
            return null;
        }
    }

    // --- Getters y Setters ---

    public Usuario getNuevoUsuario() { return nuevoUsuario; }
    public void setNuevoUsuario(Usuario nuevoUsuario) { this.nuevoUsuario = nuevoUsuario; }

    public Paciente getNuevoPaciente() { return nuevoPaciente; }
    public void setNuevoPaciente(Paciente nuevoPaciente) { this.nuevoPaciente = nuevoPaciente; }

    public Medico getNuevoMedico() { return nuevoMedico; }
    public void setNuevoMedico(Medico nuevoMedico) { this.nuevoMedico = nuevoMedico; }

    public Secretaria getNuevaSecretaria() { return nuevaSecretaria; }
    public void setNuevaSecretaria(Secretaria nuevaSecretaria) { this.nuevaSecretaria = nuevaSecretaria; }

    public String getConfirmacionPassword() { return confirmacionPassword; }
    public void setConfirmacionPassword(String confirmacionPassword) { this.confirmacionPassword = confirmacionPassword; }

    public List<Rol> getListaRoles() { return listaRoles; }
    
    public List<Especialidad> getListaEspecialidades() { return listaEspecialidades; }

    public int getIdRolSeleccionado() { return idRolSeleccionado; }
    public void setIdRolSeleccionado(int idRolSeleccionado) { this.idRolSeleccionado = idRolSeleccionado; }
}