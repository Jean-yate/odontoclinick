package beans;

import dao.*;
import modelo.*;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class DashboardBean implements Serializable {

    // Inyectamos el bean de sesión para obtener el usuario logueado
    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    // Instancias de DAOs
    private CitaDAO citaDAO = new CitaDAO();
    private MedicoDAO medicoDAO = new MedicoDAO();
    private PacienteDAO pacienteDAO = new PacienteDAO();
    private SecretariaDAO secretariaDAO = new SecretariaDAO();
    private PagoDAO pagoDAO = new PagoDAO();
    // private HistorialMedicoDAO historialDAO = new HistorialMedicoDAO(); // Descomentar si usas historiales

    // Listas para las vistas
    private List<Cita> citasProximas;
    private List<Cita> citasPasadas;
    
    // Listas Exclusivas de Admin
    private List<Medico> listaMedicosAdmin;
    private List<Secretaria> listaSecretariasAdmin;
    private List<Pago> listaPagosAdmin;

    // Listas Exclusivas de Médico
    // private List<HistorialMedico> listaHistoriales; 

    // Perfiles cargados
    private Medico medicoActual;
    private Paciente pacienteActual;

    @PostConstruct
    public void init() {
        Usuario usuario = sesionBean.getUsuarioLogueado();
        
        // Inicializar listas para evitar NullPointerException en la vista
        citasProximas = new ArrayList<>();
        citasPasadas = new ArrayList<>();

        if (usuario != null) {
            int rol = usuario.getId_rol();

            switch (rol) {
                case 1: // ADMINISTRADOR [cite: 538]
                    cargarAdmin();
                    break;
                case 2: // MÉDICO 
                    cargarMedico(usuario.getId_usuario());
                    break;
                case 3: // SECRETARIA [cite: 549]
                    cargarSecretaria();
                    break;
                case 4: // PACIENTE 
                    cargarPaciente(usuario.getId_usuario());
                    break;
            }
        }
    }

    // --- Lógica ADMIN ---
    private void cargarAdmin() {
        // Carga listas completas para gestión
        listaMedicosAdmin = medicoDAO.listarTodos();
        listaSecretariasAdmin = secretariaDAO.listar();
        listaPagosAdmin = pagoDAO.listar();
    }

    // --- Lógica MÉDICO ---
    private void cargarMedico(int idUsuario) {
        // Busca el perfil médico usando el método nuevo en MedicoDAO
        medicoActual = medicoDAO.buscarPorIdUsuario(idUsuario);

        if (medicoActual != null) {
            // Carga citas filtradas por el ID del doctor
            citasProximas = citaDAO.listarPorFiltrosDashboard(medicoActual.getId_doctor(), null, true);
            citasPasadas = citaDAO.listarPorFiltrosDashboard(medicoActual.getId_doctor(), null, false);
            
            // Aquí cargarías historiales si fuera necesario:
            // listaHistoriales = historialDAO.listarPorDoctor(medicoActual.getId_doctor());
        }
    }

    // --- Lógica SECRETARIA ---
    private void cargarSecretaria() {
        // Ve TODAS las citas del sistema
        citasProximas = citaDAO.listarPorFiltrosDashboard(null, null, true);
        citasPasadas = citaDAO.listarPorFiltrosDashboard(null, null, false);
    }

    // --- Lógica PACIENTE ---
    private void cargarPaciente(int idUsuario) {
        // Busca el perfil paciente usando el método nuevo en PacienteDAO
        pacienteActual = pacienteDAO.buscarPorIdUsuario(idUsuario);

        if (pacienteActual != null) {
            // Carga citas filtradas por el ID del paciente
            citasProximas = citaDAO.listarPorFiltrosDashboard(null, pacienteActual.getId_paciente(), true);
            citasPasadas = citaDAO.listarPorFiltrosDashboard(null, pacienteActual.getId_paciente(), false);
        }
    }

    // --- Setters para inyección ---
    public void setSesionBean(SesionBean sesionBean) {
        this.sesionBean = sesionBean;
    }

    // --- Getters para la vista .xhtml ---
    public List<Cita> getCitasProximas() { return citasProximas; }
    public List<Cita> getCitasPasadas() { return citasPasadas; }
    
    public List<Medico> getListaMedicosAdmin() { return listaMedicosAdmin; }
    public List<Secretaria> getListaSecretariasAdmin() { return listaSecretariasAdmin; }
    public List<Pago> getListaPagosAdmin() { return listaPagosAdmin; }

    public Medico getMedicoActual() { return medicoActual; }
    public Paciente getPacienteActual() { return pacienteActual; }
}