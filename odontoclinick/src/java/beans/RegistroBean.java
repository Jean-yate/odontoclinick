package beans;

import dao.*;
import modelo.*;

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

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RolDAO rolDAO = new RolDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    private Usuario nuevoUsuario;
    private Paciente nuevoPaciente;
    private Medico nuevoMedico;
    private Secretaria nuevaSecretaria;

    private String confirmacionPassword;
    private Integer idRolSeleccionado;

    private List<Rol> listaRoles;
    private List<Especialidad> listaEspecialidades;

    @PostConstruct
    public void init() {
        nuevoUsuario = new Usuario();
        nuevoPaciente = new Paciente();
        nuevoMedico = new Medico();
        nuevaSecretaria = new Secretaria();

        listaRoles = rolDAO.listarTodos();
        listaEspecialidades = especialidadDAO.listar();
    }

    public String registrar() {
        try {
            if (!nuevoUsuario.getContrasena().equals(confirmacionPassword)) {
                error("Las contraseñas no coinciden");
                return null;
            }

            if (usuarioDAO.existeUsuario(nuevoUsuario.getNombre_usuario())) {
                error("El nombre de usuario ya existe");
                return null;
            }

            if (idRolSeleccionado == null) {
                error("Debe seleccionar un rol");
                return null;
            }

            nuevoUsuario.setId_rol(idRolSeleccionado);
            nuevoUsuario.setId_estado(1);

            switch (idRolSeleccionado) {
                case 1:
                    usuarioDAO.registrarUsuarioTransaccionSimple(nuevoUsuario);
                    break;
                case 2:
                    usuarioDAO.registrarMedicoTransaccion(nuevoUsuario, nuevoMedico);
                    break;
                case 3:
                    usuarioDAO.registrarSecretariaTransaccion(nuevoUsuario, nuevaSecretaria);
                    break;
                case 4:
                    usuarioDAO.registrarPacienteTransaccion(nuevoUsuario, nuevoPaciente);
                    break;
                default:
                    throw new IllegalArgumentException("Rol inválido");
            }

            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            info("Usuario registrado correctamente");

            return "/auth/login.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            error("Error al registrar: " + e.getMessage());
            return null;
        }
    }

    private void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }

    private void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", msg));
    }

    // GETTERS Y SETTERS OMITIDOS POR BREVEDAD (los mismos que ya tienes)
    
    public Usuario getNuevoUsuario() {
    return nuevoUsuario;
}

public void setNuevoUsuario(Usuario nuevoUsuario) {
    this.nuevoUsuario = nuevoUsuario;
}

public Paciente getNuevoPaciente() {
    return nuevoPaciente;
}

public void setNuevoPaciente(Paciente nuevoPaciente) {
    this.nuevoPaciente = nuevoPaciente;
}

public Medico getNuevoMedico() {
    return nuevoMedico;
}

public void setNuevoMedico(Medico nuevoMedico) {
    this.nuevoMedico = nuevoMedico;
}

public Secretaria getNuevaSecretaria() {
    return nuevaSecretaria;
}

public void setNuevaSecretaria(Secretaria nuevaSecretaria) {
    this.nuevaSecretaria = nuevaSecretaria;
}

public String getConfirmacionPassword() {
    return confirmacionPassword;
}

public void setConfirmacionPassword(String confirmacionPassword) {
    this.confirmacionPassword = confirmacionPassword;
}

public Integer getIdRolSeleccionado() {
    return idRolSeleccionado;
}

public void setIdRolSeleccionado(Integer idRolSeleccionado) {
    this.idRolSeleccionado = idRolSeleccionado;
}

public List<Rol> getListaRoles() {
    return listaRoles;
}

public List<Especialidad> getListaEspecialidades() {
    return listaEspecialidades;
}

}
