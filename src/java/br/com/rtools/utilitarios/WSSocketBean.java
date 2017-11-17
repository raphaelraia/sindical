package br.com.rtools.utilitarios;

import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import java.io.File;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@ViewScoped
public class WSSocketBean {

    public String createLink(String[] id) {
        return createLink(null, id);
    }

    public String createLink(String path, String[] id) {
        return createLink("", "", id);
    }

    public String createLink(String client, String path, String[] id) {
        String url = "";
        try {
            HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String scheme = "ws";
            if (hsr.getScheme().equals("http")) {
                scheme = "ws";
            } else {
                scheme = "wss";
            }
            if (path == null || path.isEmpty()) {
                path = "ws_socket";
            }
            if (client != null && !client.isEmpty()) {
                client = new ControleUsuarioBean().getClienteLowerCaseString() + File.separator;
            }
            url = scheme + "://" + hsr.getHeader("host") + hsr.getContextPath() + File.separator + path + File.separator + client + id;
        } catch (Exception e) {
            return "";
        }
        return url;
    }

}
