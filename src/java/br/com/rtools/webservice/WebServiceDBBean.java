package br.com.rtools.webservice;

import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@RequestScoped
public class WebServiceDBBean implements Serializable {

    public void loadSessaoClinte() {
        String client = GenericaRequisicao.getParametro("client");
        try {

        } catch (Exception e) {

        }
        GenericaSessao.put("sessaoCliente", client);
    }

}
