package br.com.rtools.webservice;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

@ManagedBean
@RequestScoped
@ViewScoped
public class AutenticarDispositivoBean implements Serializable {

    private Pessoa pessoa;

    public AutenticarDispositivoBean() {
        this.pessoa = new Pessoa();
    }

    public AutenticarDispositivoBean(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getToString() {
        String pessoaString = new Pessoa().toString();
        return pessoaString;
    }

    public void response() {
        try {
            GenericaSessao.remove("conexao");
            ControleAcessoWebService caws = new ControleAcessoWebService();
            JSONObject jSONObject = new JSONObject();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            String jsonResponse = "";
            Boolean error = false;
            String result = caws.permiteWebService(caws.getMac(), true);
            if (result != null) {
                error = true;
                jSONObject.put("status_code", 1);
                jSONObject.put("status_details", result);
            } else {
                error = false;
                jSONObject.put("status_code", 0);
                jSONObject.put("status_details", "Dispositivo OK");
            }
            jsonResponse = jSONObject.toString();
            externalContext.getResponseOutputWriter().write(jsonResponse);
            facesContext.responseComplete();
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

}
