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
import java.util.Collections;

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
    private int idCitaSeleccionada;  
    private Cita citaAsociada;        
    private String fechaActual;      
    
    // Filtros para el Buscador (index.xhtml)
    private String filtroPaciente;
    private String filtroDiagnostico;
    private Date fechaInicio;
    private Date fechaFin;
    
    // =========================================================================
    // PROPIEDADES NUEVAS AÑADIDAS
    // =========================================================================
    
    // 1. Captura el ID del historial al ir a editar
    private Integer idHistorialAEditar; 
    
    // 2. Lista de Historiales agrupados por paciente (para el Acordeón/DataView)
    private List<List<HistorialMedico>> listaHistorialesAgrupados; 
    
    // 3. Captura el ID del paciente seleccionado para la creación manual
    private Integer idPacienteSeleccionado; 

    @PostConstruct
    public void init() {
        historialActual = new HistorialMedico();
        historialActual.setFecha(new Date()); 
        
        // Cargar listas iniciales
        listaHistoriales = historialDAO.listarHistoriales();
        listaPacientes = pacienteDAO.listarTodos();
        listaMedicos = medicoDAO.listarTodos();
        listaCitas = citaDAO.listarConFiltros(null,null,null); 
        
        // Inicializar fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        fechaActual = sdf.format(new Date());
        
        // Inicializar la agrupación
        if (listaHistoriales != null) {
            getListaHistorialesAgrupados();
        } else {
            listaHistorialesAgrupados = Collections.emptyList();
        }
    }

    // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

    public void cargarDatosCita() {
        if (idCitaSeleccionada > 0) {
            try {
                this.citaAsociada = citaDAO.buscar(idCitaSeleccionada);
                if (this.citaAsociada != null) {
                    this.historialActual = new HistorialMedico();
                    this.historialActual.setId_cita(this.citaAsociada.getId_cita());
                    this.historialActual.setId_paciente(this.citaAsociada.getId_paciente());
                    this.historialActual.setId_doctor(this.citaAsociada.getId_doctor());
                    this.historialActual.setFecha(new Date());
                    
                    this.historialActual.setPaciente(this.citaAsociada.getPaciente());
                    this.historialActual.setMedico(this.citaAsociada.getMedico());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * NUEVO: Vincula el objeto Paciente seleccionado en el selectOneMenu al historialActual.
     */
    public void vincularPacienteSeleccionado() {
        historialActual = new HistorialMedico();
        historialActual.setFecha(new Date());

        if (idPacienteSeleccionado != null && idPacienteSeleccionado > 0) {
            try {
                Paciente paciente = pacienteDAO.buscar(idPacienteSeleccionado);
                if (paciente != null) {
                    historialActual.setId_paciente(idPacienteSeleccionado);
                    historialActual.setPaciente(paciente);
                }
            } catch (Exception e) {
                 FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fallo al cargar datos del paciente."));
            }
        }
    }

    public void buscarConFiltros() {
        List<HistorialMedico> todos = historialDAO.listarHistoriales();
        
        this.listaHistoriales = todos.stream()
            .filter(h -> {
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
                if (fechaInicio != null && h.getFecha().before(fechaInicio)) return false;
                if (fechaFin != null && h.getFecha().after(fechaFin)) return false;
                return true;
            })
            .collect(Collectors.toList());
            
        getListaHistorialesAgrupados(); 
        
        if (listaHistorialesAgrupados.isEmpty() && (filtroPaciente != null || filtroDiagnostico != null || fechaInicio != null || fechaFin != null)) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", "No se encontraron historiales con los filtros aplicados."));
        }
    }
    
    /**
     * NUEVO: Carga el historial por ID al navegar a la página de edición.
     */
    public void cargarHistorialParaEdicion() {
        if (idHistorialAEditar != null && idHistorialAEditar > 0) {
            try {
                this.historialActual = historialDAO.buscar(idHistorialAEditar);
                
                if (this.historialActual == null) {
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Historial no encontrado."));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String guardar() {
        try {
            if (historialActual.getFecha() == null) historialActual.setFecha(new Date());
            historialActual.setFecha_creacion(new Date());

            if (historialActual.getId_historial() == 0) {
                // CREAR (Si viene de Cita)
                historialDAO.guardar(historialActual);
                
                if (citaAsociada != null) {
                    citaAsociada.setId_estado_cita(4); 
                    citaDAO.actualizar(citaAsociada);
                    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                    FacesContext.getCurrentInstance().addMessage(null, 
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Atención registrada correctamente."));
                    return "/dashboard/index?faces-redirect=true";
                }
                
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Atención registrada correctamente."));

            } else {
                // ACTUALIZAR (Desde el formulario de edición)
                historialDAO.actualizar(historialActual);
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Historial actualizado."));
                    
                // Redirigir al listado principal (index.xhtml)
                return "/medico/historiales/index.xhtml?faces-redirect=true";
            }

            resetFormulario();
            listaHistoriales = historialDAO.listarHistoriales(); 
            getListaHistorialesAgrupados();
            return null;
            
        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }
    
    /**
     * NUEVO: Guarda un historial creado manualmente (sin cita asociada).
     */
    public String guardarNuevoHistorial() {
        // Validación básica
        if (historialActual.getPaciente() == null) {
             FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Debe seleccionar un paciente."));
            return null;
        }
        
        // Asignación de datos (Asumimos ID_DOCTOR 1 o lo obtenemos de una sesión)
        // historialActual.setId_doctor(ID_DOCTOR_LOGUEADO);
        historialActual.setId_doctor(1); // <<--- Ajustar si tienes lógica de sesión
        historialActual.setId_cita(0); 

        try {
            historialActual.setFecha(new Date());
            historialActual.setFecha_creacion(new Date());
            
            historialDAO.guardar(historialActual);
            
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Historial creado manualmente."));
                
            resetFormulario();
            
            // Redirigir al listado
            return "/medico/historiales/index.xhtml?faces-redirect=true"; 

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar: " + e.getMessage()));
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
            getListaHistorialesAgrupados(); 
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
        citaAsociada = null;
        idPacienteSeleccionado = null; // Limpiar selección manual
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
    
    // Getters y Setters de las nuevas propiedades
    public List<List<HistorialMedico>> getListaHistorialesAgrupados() {
        if (listaHistoriales == null) {
             buscarConFiltros(); 
        }

        listaHistorialesAgrupados = listaHistoriales.stream()
            .collect(Collectors.groupingBy(HistorialMedico::getId_paciente)) 
            .values() 
            .stream()
            .collect(Collectors.toList());

        return listaHistorialesAgrupados;
    }

    public Integer getIdHistorialAEditar() { return idHistorialAEditar; }
    public void setIdHistorialAEditar(Integer idHistorialAEditar) { this.idHistorialAEditar = idHistorialAEditar; }
    
    public Integer getIdPacienteSeleccionado() { return idPacienteSeleccionado; }
    public void setIdPacienteSeleccionado(Integer idPacienteSeleccionado) { this.idPacienteSeleccionado = idPacienteSeleccionado; }
}