package beans;

import dao.HorarioDAO;
import dao.MedicoDAO;
import modelo.Horario;
import modelo.Medico;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class HorarioBean implements Serializable {

    // --- INYECCIÓN DE DEPENDENCIA (SESIÓN) ---
    // Necesario para saber qué médico está logueado y filtrar sus horarios
    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    // --- DAOs ---
    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    // --- VARIABLES DE VISTA ---
    private List<Horario> listaHorarios;
    private Horario horarioActual;

    // Variables auxiliares para el componente de hora (p:calendar trabaja con java.util.Date)
    private Date horaInicioUtil;
    private Date horaFinUtil;

    // --- INICIALIZACIÓN ---
    @PostConstruct
    public void init() {
        cargarHorarios();
        prepararNuevo(); // Inicializa el objeto para evitar errores de nulos al cargar
    }

    /**
     * Carga la tabla de horarios.
     * Si es MÉDICO: Solo carga sus propios horarios.
     * Si es ADMIN: Carga todos.
     */
    public void cargarHorarios() {
        if (sesionBean.esMedico()) {
            // Buscamos el objeto Medico usando el ID del Usuario logueado
            Medico m = medicoDAO.buscarPorIdUsuario(sesionBean.getUsuarioLogueado().getId_usuario());
            if (m != null) {
                listaHorarios = horarioDAO.listarPorDoctor(m.getId_doctor());
            } else {
                listaHorarios = new ArrayList<>();
            }
        } else {
            // Caso Admin u otros roles
            listaHorarios = horarioDAO.listar();
        }
    }

    /**
     * Este método limpia el formulario para crear un registro nuevo.
     * Reemplaza al antiguo 'resetFormulario'.
     */
    public void prepararNuevo() {
        this.horarioActual = new Horario();
        this.horarioActual.setActivo(true);            // Por defecto activo
        this.horarioActual.setDuracion_cita_minutos(30); // Valor sugerido
        
        // Limpiamos las variables temporales de fecha
        this.horaInicioUtil = null;
        this.horaFinUtil = null;
    }

    /**
     * Carga los datos de un horario existente para editarlo.
     * Convierte la hora SQL a Date para que el calendario la entienda.
     */
    public void prepararEdicion(Horario h) {
        this.horarioActual = h;
        // Conversión necesaria: java.sql.Time -> java.util.Date
        if (h.getHora_inicio() != null) {
            this.horaInicioUtil = new Date(h.getHora_inicio().getTime());
        }
        if (h.getHora_fin() != null) {
            this.horaFinUtil = new Date(h.getHora_fin().getTime());
        }
    }

    public void guardar() {
        try {
            // Validar que se hayan ingresado las horas
            if (horaInicioUtil == null || horaFinUtil == null) {
                mensajeError("Error", "Debe seleccionar hora de inicio y fin.");
                return;
            }

            // Conversión inversa: java.util.Date -> java.sql.Time
            horarioActual.setHora_inicio(new Time(horaInicioUtil.getTime()));
            horarioActual.setHora_fin(new Time(horaFinUtil.getTime()));

            // Si el usuario es médico, asignamos automáticamente su ID
            if (sesionBean.esMedico()) {
                Medico m = medicoDAO.buscarPorIdUsuario(sesionBean.getUsuarioLogueado().getId_usuario());
                if (m != null) {
                    horarioActual.setId_doctor(m.getId_doctor());
                }
            }

            // Decidir si es registro Nuevo (ID 0) o Edición
            if (horarioActual.getId_horario() == 0) {
                horarioDAO.guardar(horarioActual);
                mensaje("Éxito", "Horario agregado correctamente.");
            } else {
                horarioDAO.actualizar(horarioActual);
                mensaje("Éxito", "Horario actualizado.");
            }

            // Actualizar la tabla y limpiar el formulario
            cargarHorarios();
            prepararNuevo();

        } catch (Exception e) {
            e.printStackTrace();
            mensajeError("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    public void eliminar(int idHorario) {
        try {
            horarioDAO.eliminar(idHorario);
            cargarHorarios();
            mensaje("Info", "Horario eliminado.");
        } catch (Exception e) {
            mensajeError("Error", "No se pudo eliminar.");
        }
    }

    // --- MENSAJES DE ALERTA ---
    private void mensaje(String titulo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, texto));
    }

    private void mensajeError(String titulo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, titulo, texto));
    }

    // --- GETTERS Y SETTERS ---

    // Importante: Setter para la inyección de dependencia
    public void setSesionBean(SesionBean sesionBean) {
        this.sesionBean = sesionBean;
    }

    public List<Horario> getListaHorarios() {
        return listaHorarios;
    }

    public Horario getHorarioActual() {
        return horarioActual;
    }

    public void setHorarioActual(Horario horarioActual) {
        this.horarioActual = horarioActual;
    }

    public Date getHoraInicioUtil() {
        return horaInicioUtil;
    }

    public void setHoraInicioUtil(Date horaInicioUtil) {
        this.horaInicioUtil = horaInicioUtil;
    }

    public Date getHoraFinUtil() {
        return horaFinUtil;
    }

    public void setHoraFinUtil(Date horaFinUtil) {
        this.horaFinUtil = horaFinUtil;
    }
}