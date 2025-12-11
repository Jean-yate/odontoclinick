package beans;

import dao.PagoDAO;
import dao.CitaDAO; // ¡Asegurado!
import modelo.Pago;
import modelo.Cita; // ¡Asegurado!
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

    // DAOs
    private final PagoDAO pagoDAO = new PagoDAO();
    private final CitaDAO citaDAO = new CitaDAO(); 

    // Listas para la Vista
    private List<Pago> listaPagos;
    private List<Cita> listaCitasPendientes; // <-- Aquí necesitas la lista Cita

    // Variables para Formulario (store/update)
    private Pago pagoActual;

    @PostConstruct
    public void init() {
        pagoActual = new Pago();
        listaPagos = pagoDAO.listar(); 
        // LÍNEA CRÍTICA: Debes asegurarte que CitaDAO.java tiene un método listar()
        listaCitasPendientes = citaDAO.listar(); 
        pagoActual.setMonto(BigDecimal.ZERO);
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