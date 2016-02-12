package br.com.rtools.webservice;

import br.com.rtools.webservice.classes.WSStatus;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.webservice.classes.WSHeaders;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceContext;

@ManagedBean
@RequestScoped
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

    @Resource
    WebServiceContext wsContext;

    public void response() {
        WSHeaders wSHeaders = new WSHeaders();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        WSStatus wSStatus = new WSStatus();
        try {
            GenericaSessao.remove("conexao");
            ControleAcessoWebService caws = new ControleAcessoWebService();
            wSStatus = caws.permiteWebService(wSHeaders.getMac(), true);
            if (wSStatus.getCodigo() == 0) {
                GenericaSessao.put("sessaoWebService", wSHeaders.getClient());
                if (caws.getSession() != null && caws.getSession()) {
                    if (!ControleAcessoWebService.session(wSHeaders.getClient(), wSHeaders.getMac())) {
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
