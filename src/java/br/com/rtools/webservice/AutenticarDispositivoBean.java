package br.com.rtools.webservice;

import br.com.rtools.webservice.classes.WSStatus;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaSessao;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

@ManagedBean
@ApplicationScoped
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
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        WSStatus wSStatus = new WSStatus();
        try {
            GenericaSessao.remove("conexao");
            ControleAcessoWebService caws = new ControleAcessoWebService();
            wSStatus = caws.permiteWebService(caws.getMac(), true);
            if (wSStatus.getCodigo() == 0) {
                GenericaSessao.put("sessaoWebService", caws.getClient());
                if (caws.getSession() != null && caws.getSession()) {
                    if (!ControleAcessoWebService.session(caws.getClient(), caws.getMac())) {
                        wSStatus.setCodigo(1);
                        wSStatus.setDescricao("Erro ao gerar sessão!");
                    }
                }
            }
            externalContext.getResponseOutputWriter().write(gson.toJson(wSStatus));
            facesContext.responseComplete();

        } catch (Exception e) {
            wSStatus.setCodigo(1);
            wSStatus.setDescricao(e.getMessage());
            try {
                externalContext.getResponseOutputWriter().write(gson.toJson(wSStatus));
            } catch (IOException ex) {
                Logger.getLogger(AutenticarDispositivoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
