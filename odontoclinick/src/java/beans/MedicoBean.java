package beans;

import dao.MedicoDAO;
import dao.EspecialidadDAO; // Necesitas crear este DAO simple
import modelo.Medico;
import modelo.Usuario;
import modelo.Especialidad;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "medicoBean")
@ViewScoped
public class MedicoBean implements Serializable {

    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();

    private List<Medico> listaMedicos;
    private List<Especialidad> listaEspecialidades; // Para el Combo Box

    private Medico medicoActual;
    private Usuario usuarioActual;

    @PostConstruct
    public void init() {
        prepararNuevo();
        cargarListas();
    }

    public void prepararNuevo() {
        medicoActual = new Medico();
        usuarioActual = new Usuario();
        usuarioActual.setId_rol(2); // 2 = Doctor
    }

    public void cargarListas() {
        listaMedicos = medicoDAO.listarTodos();
        listaEspecialidades = especialidadDAO.listar(); 
    }

    public void guardarMedico() {
        try {
            boolean exito = medicoDAO.registrarMedicoTransaccion(usuarioActual, medicoActual);
            
            if (exito) {
                mensaje("Éxito", "Médico registrado correctamente.");
                cargarListas();
                prepararNuevo();
            } else {
                mensajeError("Error", "No se pudo registrar.");
            }
        } catch (Exception e) {
            mensajeError("Error", e.getMessage());
        }
    }

    // Helpers y Getters/Setters...
    private void mensaje(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, t, m)); }
    private void mensajeError(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, t, m)); }
    
    // Getters vitales para la vista
    public List<Medico> getListaMedicos() { return listaMedicos; }
    public List<Especialidad> getListaEspecialidades() { return listaEspecialidades; }
    public Medico getMedicoActual() { return medicoActual; }
    public void setMedicoActual(Medico m) { this.medicoActual = m; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario u) { this.usuarioActual = u; }
}
