package beans;

import dao.HorarioDAO;
import dao.MedicoDAO;
import modelo.Horario;
import modelo.Medico;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class HorarioBean implements Serializable {

    // DAOs
    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final MedicoDAO medicoDAO = new MedicoDAO();

    // Listas para la Vista
    private List<Horario> listaHorarios;
    private List<Medico> listaMedicos;
    private List<String> diasSemana;

    // Variables para Formulario (store/update)
    private Horario horarioActual;
    private java.util.Date horaInicioUtil; // Para manejo con p:calendar/h:input
    private java.util.Date horaFinUtil;

    @PostConstruct
    public void init() {
        horarioActual = new Horario();
        listaHorarios = horarioDAO.listar();
        listaMedicos = medicoDAO.listarTodos();
        
        // Días de la semana para el select
        diasSemana = new ArrayList<>();
        diasSemana.add("Lunes");
        diasSemana.add("Martes");
        diasSemana.add("Miércoles");
        diasSemana.add("Jueves");
        diasSemana.add("Viernes");
        diasSemana.add("Sábado");
        diasSemana.add("Domingo");
    }

    // --- Métodos de CRUD ---

    public void guardar() {
        try {
            // Convertir java.util.Date a java.sql.Time
            horarioActual.setHora_inicio(new Time(horaInicioUtil.getTime()));
            horarioActual.setHora_fin(new Time(horaFinUtil.getTime()));

            // Si es nuevo horario
            if (horarioActual.getId_horario() == 0) {
                horarioDAO.guardar(horarioActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Horario guardado correctamente."));
            } 
            // Si es edición
            else {
                horarioDAO.actualizar(horarioActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Horario actualizado correctamente."));
            }

            // Limpiar y recargar
            resetFormulario();
            listaHorarios = horarioDAO.listar();

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar: " + e.getMessage()));
        }
    }
    
    public void prepararEdicion(Horario h) {
        this.horarioActual = h;
        // Convertir java.sql.Time a java.util.Date para el formulario
        this.horaInicioUtil = h.getHora_inicio();
        this.horaFinUtil = h.getHora_fin();
    }
    
    public void eliminar(int idHorario) {
        try {
            horarioDAO.eliminar(idHorario);
            listaHorarios = horarioDAO.listar(); // Recargar lista
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Eliminado", "Horario eliminado correctamente."));
        } catch (Exception e) {
             FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al eliminar: " + e.getMessage()));
        }
    }

    public void resetFormulario() {
        horarioActual = new Horario();
        horaInicioUtil = null;
        horaFinUtil = null;
    }

    // --- Getters y Setters ---

    public List<Horario> getListaHorarios() { return listaHorarios; }
    public List<Medico> getListaMedicos() { return listaMedicos; }
    public List<String> getDiasSemana() { return diasSemana; }
    public Horario getHorarioActual() { return horarioActual; }
    public void setHorarioActual(Horario horarioActual) { this.horarioActual = horarioActual; }
    public java.util.Date getHoraInicioUtil() { return horaInicioUtil; }
    public void setHoraInicioUtil(java.util.Date horaInicioUtil) { this.horaInicioUtil = horaInicioUtil; }
    public java.util.Date getHoraFinUtil() { return horaFinUtil; }
    public void setHoraFinUtil(java.util.Date horaFinUtil) { this.horaFinUtil = horaFinUtil; }
}
