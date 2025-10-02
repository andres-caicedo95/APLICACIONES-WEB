package com.mycompany.project2.controller;

import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import javax.ejb.EJB;
import java.util.logging.Logger;
import java.util.logging.Level;

@Named
@SessionScoped
public class PerfilController implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PerfilController.class.getName());

    @Inject
    private login login;

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    private Part uploadedFile; // Para recibir el archivo desde el formulario

    // Getter y setter para el archivo subido
    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    // Getter del usuario logueado
    public Usuario getUsuario() {
        return login.getUsuarioLogueado();
    }

    // Método para guardar cambios en el perfil (datos personales)
    public String guardarPerfil() {
        try {
            usuarioFacade.edit(login.getUsuarioLogueado());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Perfil actualizado correctamente", null));
            return null; // permanece en la página
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar perfil", e);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar el perfil", null));
            return null;
        }
    }

    // Método para subir la foto de perfil (compatible con Java 8)
    public void subirFoto() {
        if (uploadedFile == null || uploadedFile.getSize() == 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione una imagen", null));
            return;
        }

        try {
            // Leer el contenido del archivo como byte[] (compatible con Java 8)
            InputStream inputStream = uploadedFile.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            byte[] imageData = buffer.toByteArray();
            inputStream.close();

            // Asignar la imagen al usuario
            login.getUsuarioLogueado().setImagenUsuario(imageData);

            // Guardar en la base de datos
            usuarioFacade.edit(login.getUsuarioLogueado());

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Foto de perfil actualizada", null));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al leer la imagen", e);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al subir la imagen", null));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la imagen en la BD", e);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al guardar la imagen", null));
        }
    }
}
