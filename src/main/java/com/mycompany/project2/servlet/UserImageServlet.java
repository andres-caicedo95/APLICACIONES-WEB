package com.mycompany.project2.servlet;

import com.mycompany.project2.entities.Usuario;
import com.mycompany.project2.services.UsuarioFacadeLocal;
//import com.mycompany.project2.sessions.UsuarioFacadeLocal;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/userImage")
public class UserImageServlet extends HttpServlet {

    @EJB
    private UsuarioFacadeLocal usuarioFacade;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String id = req.getParameter("id");
            if (id == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            Usuario u = usuarioFacade.find(Integer.valueOf(id));
            byte[] img = u != null ? u.getImagenUsuario() : null;
            if (img != null && img.length > 0) {
                resp.setContentType("image/png");
                resp.setContentLength(img.length);
                resp.getOutputStream().write(img);
            } else {
                resp.sendRedirect(req.getContextPath() + "/resources/images/default-user.png");
            }
        } catch (Exception ex) {
            resp.setStatus(500);
        }
    }
}
