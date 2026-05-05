package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "SessionFilter", urlPatterns = {"/panel/*", "/admin/*", "/auth/giris.xhtml"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String girisURI = request.getContextPath() + "/auth/giris.xhtml";
        String girisSonrasiURI = request.getContextPath() + "/panel/profil.xhtml";

        boolean oturumAcik = request.getSession().getAttribute("user") != null;
        boolean girisSayfasi = request.getRequestURI().equals(girisURI);

        if (oturumAcik || girisSayfasi) {
            if (girisSayfasi && oturumAcik) {
                response.sendRedirect(girisSonrasiURI);
            } else {
                chain.doFilter(request, response);
            }
        } else {
            if (isAJAXRequest(request)) {
                response.setContentType("text/xml");
                response.setCharacterEncoding("UTF-8");
                response.getWriter()
                        .write("<?xml version='1.0' encoding='UTF-8'?>"
                                + "<partial-response><redirect url='" + girisURI + "'/></partial-response>");
            } else {
                response.sendRedirect(girisURI);
            }
        }
    }

    private boolean isAJAXRequest(HttpServletRequest request) {
        String facesRequest = request.getHeader("Faces-Request");
        return "partial/ajax".equals(facesRequest);
    }
}
