package br.com.rtools.webservice;

import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.dao.EmailPessoaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@RequestScoped
@ViewScoped
public class WSEmailConfirmaRecebimento implements Serializable {

    public String getTitle() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("text/html");
            externalContext.setResponseCharacterEncoding("UTF-8");
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String cliente = GenericaRequisicao.getParametro("cliente");
            GenericaSessao.put("sessaoCliente", cliente);
            String token = GenericaRequisicao.getParametro("token");
            String confirm = confirm(cliente, token);
            externalContext.getResponseOutputWriter().write(confirm);
            facesContext.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(WSMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public void response() {
        getTitle();
    }

    protected String confirm(String client, String token) {
        if (client == null || client.isEmpty()) {
            return "Empty client";
        }
        if (token == null || token.isEmpty()) {
            return "Empty token";
        }
        EmailPessoaDao epd = new EmailPessoaDao();
        EmailPessoa ep = epd.findByUUID(token);
        if (ep != null) {
            Dao dao = new Dao();
            ep.setRecebimento(new Date());
            dao.update(ep, true);
            return "Obrigado pela confirmação";
        }
        return "Já confirmado";
    }

}
