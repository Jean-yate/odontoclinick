package beans;

import dao.PagoDAO;
import dao.CitaDAO; 
import modelo.Pago;
import modelo.Cita; 
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class PagoBean implements Serializable {

    // CORRECCIÓN 1: Quitamos 'final' y el 'new'. Solo declaramos las variables.
    private PagoDAO pagoDAO;
    private CitaDAO citaDAO; 

    // Listas para la Vista
    private List<Pago> listaPagos;
    private List<Cita> listaCitasPendientes; 

    // Variables para Formulario
    private Pago pagoActual;

    @PostConstruct
    public void init() {
        // CORRECCIÓN 2: Inicializamos los DAOs aquí dentro (Zona Segura)
        try {
            pagoDAO = new PagoDAO();
            citaDAO = new CitaDAO(); 
            
            pagoActual = new Pago();
            pagoActual.setMonto(BigDecimal.ZERO);
            
            // Ahora sí podemos llamar a la base de datos
            listaPagos = pagoDAO.listar(); 
            
            // Asegúrate de que CitaDAO tenga el método listarTodos()
            if(citaDAO != null) {
                listaCitasPendientes = citaDAO.listarConFiltros(null, null, null);
            }
            
        } catch (Exception e) {
            System.out.println("Error al iniciar PagoBean: " + e.getMessage());
        }
    }
    
    // ... (Métodos de guardar, eliminar, etc.) ...

    public void guardar() {
        try {
            pagoActual.setFechaPago(new Timestamp(new Date().getTime()));

            if (pagoActual.getIdPago() == 0) {
                pagoDAO.guardar(pagoActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago registrado correctamente."));
            } else {
                pagoDAO.actualizar(pagoActual);
                FacesContext.getCurrentInstance().addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Pago actualizado correctamente."));
            }

            resetFormulario();
            listaPagos = pagoDAO.listar(); 

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el pago: " + e.getMessage()));
        }
    }

    public void resetFormulario() {
        pagoActual = new Pago();
        pagoActual.setMonto(BigDecimal.ZERO);
    }

    // --- Getters y Setters ---

    public List<Pago> getListaPagos() { return listaPagos; }
    public List<Cita> getListaCitasPendientes() { return listaCitasPendientes; }
    public Pago getPagoActual() { return pagoActual; }
    public void setPagoActual(Pago pagoActual) { this.pagoActual = pagoActual; }
}