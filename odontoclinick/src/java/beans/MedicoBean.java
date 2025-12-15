package beans;

import dao.MedicoDAO;
import dao.EspecialidadDAO;
import dao.UsuarioDAO;
import modelo.Medico;
import modelo.Usuario;
import modelo.Especialidad;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "medicoBean")
@ViewScoped
public class MedicoBean implements Serializable {

    // --- 1. INYECCIÓN DE SESIÓN (Para saber quién está logueado en Perfil) ---
    @ManagedProperty("#{sesionBean}")
    private SesionBean sesionBean;

    // --- DAOs ---
    private final MedicoDAO medicoDAO = new MedicoDAO();
    private final EspecialidadDAO especialidadDAO = new EspecialidadDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // --- VARIABLES ---
    private List<Medico> listaMedicos;
    private List<Especialidad> listaEspecialidades;

    private Medico medicoActual;
    private Usuario usuarioActual;

    @PostConstruct
    public void init() {
        prepararNuevo();
        cargarListas();
        
        // Si el usuario logueado es Médico, cargamos sus datos por defecto para el Perfil
        if (sesionBean != null && sesionBean.esMedico()) {
            cargarDatosPerfil();
        }
    }

    // =========================================================================
    // LÓGICA PARA PERFIL DEL MÉDICO (Lo que te faltaba)
    // =========================================================================

    public void cargarDatosPerfil() {
        try {
            // Obtenemos el usuario de la sesión
            Usuario uLogueado = sesionBean.getUsuarioLogueado();
            
            // Buscamos el objeto Médico asociado a ese usuario
            this.medicoActual = medicoDAO.buscarPorIdUsuario(uLogueado.getId_usuario());
            
            // Referenciamos el usuario para poder editar correo/telefono
            this.usuarioActual = this.medicoActual.getUsuario();
            
            // Limpiamos la contraseña en memoria para que el campo salga vacío (seguridad)
            // Si el usuario quiere cambiarla, escribirá una nueva.
            this.usuarioActual.setContrasena(""); 
        } catch (Exception e) {
            System.out.println("Error cargando perfil: " + e.getMessage());
        }
    }

    public void actualizarPerfil() {
        try {
            // 1. Lógica de Contraseña
            if (usuarioActual.getContrasena() == null || usuarioActual.getContrasena().trim().isEmpty()) {
                // Si dejó el campo vacío, NO actualizamos la contraseña (buscamos la vieja)
                Usuario uOriginal = new dao.UsuarioDAO().buscar(usuarioActual.getId_usuario());
                usuarioActual.setContrasena(uOriginal.getContrasena());
            } 
            // Si escribió algo, se guardará esa nueva contraseña.

            // 2. Actualizar Usuario (Teléfono, Correo, Clave)
            usuarioDAO.actualizar(usuarioActual);
            
            // 3. Feedback
            mensaje("Éxito", "Tus datos han sido actualizados.");
            
            // Limpiamos clave de nuevo visualmente
            usuarioActual.setContrasena(""); 
            
        } catch (Exception e) {
            mensajeError("Error", "No se pudo actualizar el perfil.");
        }
    }

    // =========================================================================
    // LÓGICA PARA ADMINISTRADOR (CRUD Medicos)
    // =========================================================================

    public void prepararNuevo() {
        medicoActual = new Medico();
        usuarioActual = new Usuario();
        usuarioActual.setId_rol(2); // Rol Doctor
        medicoActual.setUsuario(usuarioActual);
    }

    public void prepararEdicion(Medico m) {
        this.medicoActual = m;
        this.usuarioActual = m.getUsuario();
        // Mantenemos la contraseña oculta/vacía al abrir edición
        this.usuarioActual.setContrasena(""); 
    }

    public void cargarListas() {
        listaMedicos = medicoDAO.listarTodos();
        listaEspecialidades = especialidadDAO.listar(); 
    }

    public void guardarMedico() {
        try {
            if(medicoActual.getId_doctor() == 0) {
                 // Registrar
                 boolean exito = medicoDAO.registrarMedicoTransaccion(usuarioActual, medicoActual);
                 if (exito) {
                    mensaje("Éxito", "Médico registrado correctamente.");
                    cargarListas();
                    prepararNuevo();
                 } else {
                    mensajeError("Error", "No se pudo registrar.");
                 }
            } else {
                // Actualizar (Admin)
                // Si la contraseña está vacía, recuperamos la anterior
                if (usuarioActual.getContrasena().isEmpty()) {
                    Usuario uOld = usuarioDAO.buscar(usuarioActual.getId_usuario());
                    usuarioActual.setContrasena(uOld.getContrasena());
                }
                
                usuarioDAO.actualizar(usuarioActual);
                medicoDAO.actualizar(medicoActual);
                mensaje("Éxito", "Médico actualizado correctamente.");
                cargarListas();
                prepararNuevo();
            }
        } catch (Exception e) {
            mensajeError("Error", e.getMessage());
        }
    }

    public void eliminar(Medico m) {
        try {
            Usuario u = m.getUsuario();
            u.setId_estado(2); // Inactivo
            usuarioDAO.actualizar(u);
            cargarListas();
            mensaje("Info", "Médico desactivado.");
        } catch (Exception e) {
            mensajeError("Error", "No se pudo desactivar.");
        }
    }

    public void reactivar(Medico m) {
        try {
            Usuario u = m.getUsuario();
            u.setId_estado(1); // Activo
            usuarioDAO.actualizar(u);
            cargarListas();
            mensaje("Éxito", "Médico reactivado.");
        } catch (Exception e) {
            mensajeError("Error", "No se pudo reactivar.");
        }
    }
    
    public void cambiarEstado(Medico med) {
    int estadoActual = med.getUsuario().getId_estado();
    int nuevoEstado = (estadoActual == 1) ? 2 : 1;

    boolean exito = medicoDAO.cambiarEstado(med.getUsuario().getId_usuario(), nuevoEstado);

        if (exito) {
            med.getUsuario().setId_estado(nuevoEstado);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Estado actualizado", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al actualizar", null));
        }
    }

    // --- UTILIDADES ---
    private void mensaje(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, t, m)); }
    private void mensajeError(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, t, m)); }

    // --- GETTERS Y SETTERS ---
    public void setSesionBean(SesionBean sesionBean) { this.sesionBean = sesionBean; } // Setter Importante

    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario usuarioActual) { this.usuarioActual = usuarioActual; }
    
    public Medico getMedicoActual() { return medicoActual; }
    public void setMedicoActual(Medico medicoActual) { this.medicoActual = medicoActual; }

    public List<Medico> getListaMedicos() { return listaMedicos; }
    public List<Especialidad> getListaEspecialidades() { return listaEspecialidades; }
}