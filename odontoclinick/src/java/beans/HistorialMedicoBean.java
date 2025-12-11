package beans;

import dao.HistorialMedicoDAO;
import dao.PacienteDAO;
import dao.CitaDAO;
import dao.MedicoDAO;
import modelo.HistorialMedico;
import modelo.Paciente; // Asegurado
import modelo.Cita;     // Asegurado
import modelo.Medico;   // Asegurado
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class HistorialMedicoBean implements Serializable {

    // DAOs
    private final HistorialMedicoDAO historialDAO = new HistorialMedicoDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final CitaDAO citaDAO = new CitaDAO();

    // Listas para la Vista
    private List<HistorialMedico> listaHistoriales;
    private List<Paciente> listaPacientes;
    private List<Medico> listaMedicos;
    private List<Cita> listaCitas; // Citas que generaron el historial

    // Variables para Formulario (store/update)
    private HistorialMedico historialActual;

    @PostConstruct
    public void init() {
        historialActual = new HistorialMedico();
        historialActual.setFecha(new Date()); // Fecha por defecto: hoy
        
        // Cargar listas para selects y tabla
        listaHistoriales = historialDAO.listarHistoriales();
        
        // NOTA: Asegúrate de que PacienteDAO y MedicoDAO tengan el método listarTodos()
        listaPacientes = pacienteDAO.listarTodos();
        listaMedicos = medicoDAO.listarTodos();
        listaCitas = citaDAO.listar(); 
    }

    // --- Métodos de CRUD ---

    public void guardar() {
        try {
            historialActual.setFecha_creacion(new Date()); // Esta línea es la que fallaba si faltaban imports arriba.

            // Si es nuevo historial
            if (historialActual.getId_historial() == 0) {
                historialDAO.guardar(historialActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Historial registrado."));
            } 
            // Si es edición
            else {
                historialDAO.actualizar(historialActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Historial actualizado."));
            }

            // Limpiar y recargar
            resetFormulario();
            listaHistoriales = historialDAO.listarHistoriales();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el historial: " + e.getMessage()));
        }
    }
    
    public void prepararEdicion(HistorialMedico h) {
        this.historialActual = h;
    }
    
    public void eliminar(int idHistorial) {
        try {
            historialDAO.eliminar(idHistorial);
            listaHistoriales = historialDAO.listarHistoriales(); // Recargar lista
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Historial médico eliminado."));
        } catch (Exception e) {
             FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar: " + e.getMessage()));
        }
    }

    public void resetFormulario() {
        historialActual = new HistorialMedico();
        historialActual.setFecha(new Date());
    }

    // --- Getters y Setters ---

    public List<HistorialMedico> getListaHistoriales() { return listaHistoriales; }
    public List<Paciente> getListaPacientes() { return listaPacientes; }
    public List<Medico> getListaMedicos() { return listaMedicos; }
    public List<Cita> getListaCitas() { return listaCitas; }
    public HistorialMedico getHistorialActual() { return historialActual; }
    public void setHistorialActual(HistorialMedico historialActual) { this.historialActual = historialActual; }
}