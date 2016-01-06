package br.com.rtools.webservice;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
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
public class WebServiceBean implements Serializable {

    private Pessoa pessoa;

    public WebServiceBean() {
        this.pessoa = new Pessoa();
    }

    public WebServiceBean(Pessoa pessoa) {
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

    public void renderJson() {
        try {
            String client = GenericaRequisicao.getParametro("client");
            String user = GenericaRequisicao.getParametro("user");
            String password = GenericaRequisicao.getParametro("password");
            String app = GenericaRequisicao.getParametro("app");
            String key = GenericaRequisicao.getParametro("key");
            String method = GenericaRequisicao.getParametro("method");
            String action = GenericaRequisicao.getParametro("action");
            GenericaSessao.remove("conexao");
            GenericaSessao.put("sessaoCliente", client);
            JSONObject jSONObject = new JSONObject();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            String jsonResponse = "";
            if (!key.equals("123456")) {
                jSONObject.put("error_code", "0");
                jSONObject.put("error_details", "invalid key");
                jsonResponse = jSONObject.toString();
            } else {
                List<TipoDocumento> listBiometria = new Dao().list(new TipoDocumento());
                jsonResponse = listBiometria.toString();
            }
            externalContext.getResponseOutputWriter().write(jsonResponse);
            facesContext.responseComplete();
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

}
