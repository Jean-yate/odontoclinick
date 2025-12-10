package beans;

import dao.*;
import modelo.*;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@ViewScoped
public class CitaBean implements Serializable {

    // Inyección de Sesión (para saber quién registra la cita)
    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    // DAOs
    private final CitaDAO citaDAO = new CitaDAO();
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EstadoCitaDAO estadoDAO = new EstadoCitaDAO();
    private final HorarioDAO horarioDAO = new HorarioDAO();

    // Listas para la Vista
    private List<Cita> listaCitas;
    private List<Paciente> listaPacientes;
    private List<Medico> listaMedicos;
    private List<EstadoCita> listaEstados;
    private List<String> horasDisponibles;

    // Filtros de Búsqueda (index)
    private String filtroPaciente;
    private String filtroMedico;
    private Integer filtroEstado;

    // Variables para Formulario (store/update)
    private Cita citaActual;
    private Date fechaSeleccionada;
    private String horaSeleccionada;

    @PostConstruct
    public void init() {
        citaActual = new Cita();
        horasDisponibles = new ArrayList<>();
        
        // Cargar listas para selects
        listaPacientes = pacienteDAO.listarTodos();
        listaMedicos = medicoDAO.listarTodos();
        listaEstados = estadoDAO.listar();
        
        // Cargar tabla inicial
        buscar();
    }

    // --- 1. REPLICA DE MÉTODO index() CON FILTROS ---
    public void buscar() {
        listaCitas = citaDAO.listarConFiltros(filtroPaciente, filtroMedico, filtroEstado);
    }

    // --- 2. REPLICA DE MÉTODO getAvailableHours() (Lógica AJAX) ---
    public void cargarHorasDisponibles() {
        horasDisponibles.clear();
        
        // Validaciones previas
        if (citaActual.getId_doctor() == 0 || fechaSeleccionada == null) {
            return;
        }

        // Obtener día de la semana en español (Lunes, Martes...)
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaSeleccionada);
        String[] dias = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        String diaSemana = dias[cal.get(Calendar.DAY_OF_WEEK) - 1];

        // 1. Buscar horario del médico para ese día
        Horario horario = horarioDAO.buscarPorMedicoYDia(citaActual.getId_doctor(), diaSemana);
        
        if (horario == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "El médico no atiende los " + diaSemana));
            return;
        }

        // 2. Obtener citas ya ocupadas ese día
        List<String> horasOcupadas = citaDAO.obtenerHorasOcupadas(citaActual.getId_doctor(), fechaSeleccionada);

        // 3. Generar slots de tiempo (While loop de Laravel convertido a Java)
        long inicio = horario.getHora_inicio().getTime();
        long fin = horario.getHora_fin().getTime();
        int duracionMinutos = horario.getDuracion_cita_minutos();

        // Usamos Calendar para iterar sumando minutos
        Calendar iterador = Calendar.getInstance();
        iterador.setTimeInMillis(inicio);
        // Ajustamos la fecha base del iterador para que no afecte comparaciones, solo nos importa la hora
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        while (iterador.getTimeInMillis() < fin) {
            String horaStr = sdf.format(iterador.getTime());
            
            // Si la hora NO está ocupada, la agregamos
            if (!horasOcupadas.contains(horaStr)) {
                // Validación extra: Si es HOY, no mostrar horas pasadas
                if (esHoy(fechaSeleccionada)) {
                    if (iterador.getTimeInMillis() > obtenerHoraActualEnMillis()) {
                         horasDisponibles.add(horaStr);
                    }
                } else {
                    horasDisponibles.add(horaStr);
                }
            }
            // Sumar la duración de la cita
            iterador.add(Calendar.MINUTE, duracionMinutos);
        }
    }

    // --- 3. REPLICA DE MÉTODO store() ---
    public String guardar() {
        try {
            // Combinar fecha del Calendar + hora del Select
            Date fechaHoraFinal = combinarFechaHora(fechaSeleccionada, horaSeleccionada);
            
            // 1. Validar fecha pasada
            if (fechaHoraFinal.before(new Date())) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No puede agendar en el pasado"));
                return null;
            }

            // 2. Validar duplicados (por seguridad backend)
            if (citaDAO.existeCitaEnHorario(citaActual.getId_doctor(), fechaHoraFinal)) {
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Horario ya ocupado"));
                return null;
            }

            // 3. Asignar datos faltantes
            citaActual.setFecha_hora(new Timestamp(fechaHoraFinal.getTime()));
            citaActual.setFecha_actualizacion(new Timestamp(new Date().getTime()));

            // Si es nueva cita
            if (citaActual.getId_cita() == 0) {
                citaActual.setFecha_creacion(new Timestamp(new Date().getTime()));
                
                // Si el usuario es PACIENTE, estado inicial = Pendiente (ID 1 o el que definas)
                if (sesionBean.esPaciente()) {
                    citaActual.setId_paciente(sesionBean.getUsuarioLogueado().getId_usuario()); // OJO: necesitas buscar ID paciente real
                    // Busca el ID paciente real usando el DAO que hicimos antes
                    Paciente p = pacienteDAO.buscarPorIdUsuario(sesionBean.getUsuarioLogueado().getId_usuario());
                    citaActual.setId_paciente(p.getId_paciente());
                    citaActual.setId_estado_cita(1); // 1 = Programada/Pendiente
                } else {
                    // Si es Secretaria/Admin, usa el estado seleccionado o por defecto
                    if (citaActual.getId_estado_cita() == 0) citaActual.setId_estado_cita(1);
                }
                
                citaDAO.guardar(citaActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cita agendada correctamente"));
            } 
            // Si es edición
            else {
                citaDAO.actualizar(citaActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cita actualizada"));
            }

            // Limpiar y recargar
            citaActual = new Cita();
            fechaSeleccionada = null;
            horaSeleccionada = null;
            buscar();
            
            return "index?faces-redirect=true"; // Redirigir al listado

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
    }
    
    // --- LÓGICA DE CANCELAR (destroy) ---
    public void cancelar(Cita cita) {
        // En lugar de borrar físico, cambiamos estado a 'Cancelada' (ID 5 según tus inserts)
        cita.setId_estado_cita(5);
        citaDAO.actualizar(cita);
        buscar(); // Recargar tabla
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_WARN, "Cancelada", "La cita ha sido cancelada."));
    }

    // --- UTILIDADES ---
    
    private Date combinarFechaHora(Date fecha, String hora) {
        if (fecha == null || hora == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        String[] parts = hora.split(":");
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    
    private boolean esHoy(Date fecha) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fecha);
        cal2.setTime(new Date());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    private long obtenerHoraActualEnMillis() {
        Calendar now = Calendar.getInstance();
        // Creamos un calendar base solo con la hora actual
        Calendar base = Calendar.getInstance();
        base.set(1970, 0, 1, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        return base.getTimeInMillis(); 
        // Nota: Esta lógica de comparación de horas es simplificada. 
        // Idealmente compararías Timestamp completo.
    }

    // --- Getters y Setters ---

    public void setSesionBean(SesionBean sesionBean) { this.sesionBean = sesionBean; }
    
    public List<Cita> getListaCitas() { return listaCitas; }
    public List<Paciente> getListaPacientes() { return listaPacientes; }
    public List<Medico> getListaMedicos() { return listaMedicos; }
    public List<EstadoCita> getListaEstados() { return listaEstados; }
    public List<String> getHorasDisponibles() { return horasDisponibles; }

    public String getFiltroPaciente() { return filtroPaciente; }
    public void setFiltroPaciente(String filtroPaciente) { this.filtroPaciente = filtroPaciente; }
    public String getFiltroMedico() { return filtroMedico; }
    public void setFiltroMedico(String filtroMedico) { this.filtroMedico = filtroMedico; }
    public Integer getFiltroEstado() { return filtroEstado; }
    public void setFiltroEstado(Integer filtroEstado) { this.filtroEstado = filtroEstado; }

    public Cita getCitaActual() { return citaActual; }
    public void setCitaActual(Cita citaActual) { this.citaActual = citaActual; }
    
    public Date getFechaSeleccionada() { return fechaSeleccionada; }
    public void setFechaSeleccionada(Date fechaSeleccionada) { this.fechaSeleccionada = fechaSeleccionada; }
    
    public String getHoraSeleccionada() { return horaSeleccionada; }
    public void setHoraSeleccionada(String horaSeleccionada) { this.horaSeleccionada = horaSeleccionada; }
}