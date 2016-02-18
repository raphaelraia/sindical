package br.com.rtools.webservice;

import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.webservice.classes.WSExecutarSistema;
import br.com.rtools.webservice.classes.WSHeaders;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.json.JSONException;

@ManagedBean
@RequestScoped
@ViewScoped
public class ExecutarSistemaBean implements Serializable {

    public void response() {
        WSHeaders wSHeaders = new WSHeaders();
        try {
            ControleAcessoWebService caws = new ControleAcessoWebService();
            Registro registro = (Registro) new Dao().find(new Registro(), 1);
            WSExecutarSistema wes = new WSExecutarSistema();
            if(GenericaSessao.exists("sessaoWebService")) {
                int X = 0;
            }
            wes.setUrl(registro.getUrlPath() + "/Sindical/" + wSHeaders.getClient() + "/");
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            Gson gson = new Gson();
            externalContext.getResponseOutputWriter().write(gson.toJson(wes));
            facesContext.responseComplete();
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

}
