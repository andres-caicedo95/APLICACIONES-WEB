package com.mycompany.project2.servlet;

import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.UsuarioFacadeLocal;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/userImage")
public class UserImageServlet extends HttpServlet {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuario requerido");
            return;
        }

        try {
            Integer idUsuario = Integer.valueOf(idParam);
            Usuario usuario = usuarioFacade.find(idUsuario);

            if (usuario == null || usuario.getImagenUsuario() == null) {
                // Servir imagen por defecto
                response.sendRedirect(request.getContextPath() + "/resources/images/default-user.png");
                return;
            }

            byte[] imagen = usuario.getImagenUsuario();
            response.setContentType("image/jpeg"); // o "image/png", pero asumimos JPEG
            response.setContentLength(imagen.length);
            response.getOutputStream().write(imagen);
            response.getOutputStream().flush();

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al cargar imagen");
        }
    }
}
