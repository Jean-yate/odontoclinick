package beans;

import dao.HistorialMedicoDAO;
import dao.PacienteDAO;
import dao.CitaDAO;
import dao.MedicoDAO;
import modelo.HistorialMedico;
import modelo.Paciente;
import modelo.Cita;
import modelo.Medico;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat; 
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors; 

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
    private List<Cita> listaCitas;

    // Variables para Formulario
    private HistorialMedico historialActual;
    private int idCitaSeleccionada;  // Captura el ID de la URL
    private Cita citaAsociada;       // Datos de la cita al atender
    private String fechaActual;      // Fecha bonita para la vista
    
    // Filtros para el Buscador (index.xhtml)
    private String filtroPaciente;
    private String filtroDiagnostico;
    private Date fechaInicio;
    private Date fechaFin;
    // =========================================================================

    @PostConstruct
    public void init() {
        historialActual = new HistorialMedico();
        historialActual.setFecha(new Date()); 
        
        // Cargar listas iniciales
        listaHistoriales = historialDAO.listarHistoriales();
        listaPacientes = pacienteDAO.listarTodos();
        listaMedicos = medicoDAO.listarTodos();
        listaCitas = citaDAO.listarTodos(); 
        
        //Inicializar fecha 
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        fechaActual = sdf.format(new Date());
    }

    // --- MÉTODOS ---

    /**
     * NUEVO: Este método se llama automáticamente al entrar a 'gestion.xhtml'
     * Sirve para precargar los datos del paciente si venimos de una Cita.
     */
    public void cargarDatosCita() {
        if (idCitaSeleccionada > 0) {
            try {
                this.citaAsociada = citaDAO.buscar(idCitaSeleccionada);
                if (this.citaAsociada != null) {
                    // Preparamos el historial con datos heredados
                    this.historialActual = new HistorialMedico();
                    this.historialActual.setId_cita(this.citaAsociada.getId_cita());
                    this.historialActual.setId_paciente(this.citaAsociada.getId_paciente());
                    this.historialActual.setId_doctor(this.citaAsociada.getId_doctor());
                    this.historialActual.setFecha(new Date());
                    
                    // Vinculamos objetos para que se vean los nombres en el formulario
                    this.historialActual.setPaciente(this.citaAsociada.getPaciente());
                    this.historialActual.setMedico(this.citaAsociada.getMedico());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * NUEVO: Lógica para filtrar la tabla en 'index.xhtml' sin ir a la BD cada vez
     */
    public void buscarConFiltros() {
        List<HistorialMedico> todos = historialDAO.listarHistoriales();
        
        this.listaHistoriales = todos.stream()
            .filter(h -> {
                // Filtro Texto (Paciente o Diagnostico)
                boolean matchPac = true;
                boolean matchDiag = true;
                
                if (filtroPaciente != null && !filtroPaciente.isEmpty()) {
                    String nombre = h.getPaciente().getUsuario().getNombre() + " " + h.getPaciente().getUsuario().getApellidos();
                    matchPac = nombre.toLowerCase().contains(filtroPaciente.toLowerCase());
                }
                if (filtroDiagnostico != null && !filtroDiagnostico.isEmpty()) {
                    String texto = (h.getDiagnostico() + " " + h.getTratamiento_realizado()).toLowerCase();
                    matchDiag = texto.contains(filtroDiagnostico.toLowerCase());
                }
                return matchPac && matchDiag;
            })
            .filter(h -> {
                // Filtro Fechas
                if (fechaInicio != null && h.getFecha().before(fechaInicio)) return false;
                if (fechaFin != null && h.getFecha().after(fechaFin)) return false;
                return true;
            })
            .collect(Collectors.toList());
    }

    public String guardar() {
        try {
            // Asegurar fechas
            if(historialActual.getFecha() == null) historialActual.setFecha(new Date());
            historialActual.setFecha_creacion(new Date());

            if (historialActual.getId_historial() == 0) {
                // CREAR
                historialDAO.guardar(historialActual);
                
                // NUEVO: Si viene de una cita, actualizamos la cita a "Completada" (Estado 4)
                if (citaAsociada != null) {
                    citaAsociada.setId_estado_cita(4); 
                    citaDAO.actualizar(citaAsociada);
                }
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Atención registrada correctamente."));
            } else {
                // ACTUALIZAR
                historialDAO.actualizar(historialActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Historial actualizado."));
            }

            resetFormulario();
            // Recargar lista completa
            listaHistoriales = historialDAO.listarHistoriales(); 
            
            // Redirigir al dashboard si venimos de atender una cita
            if(citaAsociada != null) {
                return "/dashboard/index?faces-redirect=true";
            }
            return null; // Quedarse en la misma pagina si es edicion normal

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }
    
    public void prepararEdicion(HistorialMedico h) {
        this.historialActual = h;
    }
    
    public void eliminar(int idHistorial) {
        try {
            historialDAO.eliminar(idHistorial);
            listaHistoriales = historialDAO.listarHistoriales(); 
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Registro eliminado."));
        } catch (Exception e) {
             FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        }
    }

    public void resetFormulario() {
        historialActual = new HistorialMedico();
        historialActual.setFecha(new Date());
        citaAsociada = null; // Limpiar cita asociada
    }

    // --- GETTERS Y SETTERS ---

    public List<HistorialMedico> getListaHistoriales() { return listaHistoriales; }
    public List<Paciente> getListaPacientes() { return listaPacientes; }
    public List<Medico> getListaMedicos() { return listaMedicos; }
    public List<Cita> getListaCitas() { return listaCitas; }
    
    public HistorialMedico getHistorialActual() { return historialActual; }
    public void setHistorialActual(HistorialMedico historialActual) { this.historialActual = historialActual; }

    public int getIdCitaSeleccionada() { return idCitaSeleccionada; }
    public void setIdCitaSeleccionada(int idCitaSeleccionada) { this.idCitaSeleccionada = idCitaSeleccionada; }

    public Cita getCitaAsociada() { return citaAsociada; }
    public void setCitaAsociada(Cita citaAsociada) { this.citaAsociada = citaAsociada; }

    public String getFechaActual() { return fechaActual; }
    
    public String getFiltroPaciente() { return filtroPaciente; }
    public void setFiltroPaciente(String filtroPaciente) { this.filtroPaciente = filtroPaciente; }

    public String getFiltroDiagnostico() { return filtroDiagnostico; }
    public void setFiltroDiagnostico(String filtroDiagnostico) { this.filtroDiagnostico = filtroDiagnostico; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }
}