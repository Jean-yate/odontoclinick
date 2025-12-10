package beans;

import dao.SecretariaDAO;
import modelo.Secretaria;
import modelo.Usuario;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@ManagedBean(name = "secretariaBean")
@ViewScoped
public class SecretariaBean implements Serializable {

    private final SecretariaDAO secretariaDAO = new SecretariaDAO();

    private List<Secretaria> listaSecretarias;
    
    // Necesitamos dos objetos para el formulario: Usuario (datos login) y Secretaria (datos turno)
    private Secretaria secretariaActual;
    private Usuario usuarioActual;

    @PostConstruct
    public void init() {
        prepararNuevo();
        cargarLista();
    }

    public void prepararNuevo() {
        secretariaActual = new Secretaria();
        usuarioActual = new Usuario();
        usuarioActual.setId_rol(3); // Rol Secretaria
        secretariaActual.setUsuario(usuarioActual); // Vincularlos por si acaso
    }

    public void cargarLista() {
        // Este método en DAO debe hacer JOIN usuario y secretaria
        listaSecretarias = secretariaDAO.listar(); 
    }

    public void guardarSecretaria() {
        try {
            boolean exito = false;

            // --- CASO 1: CREAR NUEVA (ID es 0) ---
            if (secretariaActual.getId_secretaria() == 0) {
                // Validación: Contraseña obligatoria al crear
                if (usuarioActual.getContrasena() == null || usuarioActual.getContrasena().isEmpty()) {
                    mensajeError("Error", "La contraseña es obligatoria para nuevos usuarios.");
                    return;
                }
                
                // Usamos la transacción que crea Usuario + Secretaria de una vez
                exito = secretariaDAO.registrarTransaccion(usuarioActual, secretariaActual);
            } 
            
            // --- CASO 2: EDITAR EXISTENTE (ID mayor a 0) ---
            else {
                // 1. Manejo de la contraseña en Edición:
                // Si el campo contraseña está vacío, NO la tocamos (mantenemos la vieja de la BD).
                // Si escribieron algo, se actualizará.
                if (usuarioActual.getContrasena() == null || usuarioActual.getContrasena().isEmpty()) {
                    // Buscamos el usuario original para recuperar su contraseña vieja y no perderla
                    // (O simplemente no la incluimos en el UPDATE del DAO si tenemos un método específico)
                    // Para simplificar, asumimos que tu DAO actualiza todo.
                    Usuario original = new dao.UsuarioDAO().buscar(usuarioActual.getId_usuario());
                    usuarioActual.setContrasena(original.getContrasena());
                }

                // 2. Actualizamos la tabla USUARIO (Nombre, Apellido, Correo, Teléfono)
                // Necesitamos instanciar UsuarioDAO aquí o usar una variable global inyectada
                dao.UsuarioDAO uDao = new dao.UsuarioDAO();
                uDao.actualizar(usuarioActual); // Este método ya lo tienes en UsuarioDAO

                // 3. Actualizamos la tabla SECRETARIA (Turno, Fecha Ingreso)
                secretariaDAO.actualizar(secretariaActual); // Este método ya lo tienes en SecretariaDAO
                
                exito = true; // Asumimos éxito si no hubo error SQL
            }

            // --- RESULTADO FINAL ---
            if (exito) {
                mensaje("Éxito", "Datos guardados correctamente.");
                cargarLista();   // Recargar la tabla para ver cambios
                prepararNuevo(); // Limpiar el formulario
                // Opcional: Cerrar el modal aquí si usas PrimeFaces
                // PrimeFaces.current().executeScript("PF('wdialogo').hide();");
            } else {
                mensajeError("Error", "No se pudieron guardar los cambios.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mensajeError("Error Grave", e.getMessage());
        }
    }
    // Mensajes helpers... (igual que AdminBean)
    private void mensaje(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, t, m)); }
    private void mensajeError(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, t, m)); }
    
    // Getters y Setters
    public List<Secretaria> getListaSecretarias() { return listaSecretarias; }
    public void setListaSecretarias(List<Secretaria> l) { this.listaSecretarias = l; }
    public Secretaria getSecretariaActual() { return secretariaActual; }
    public void setSecretariaActual(Secretaria s) { this.secretariaActual = s; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario u) { this.usuarioActual = u; }
    
}