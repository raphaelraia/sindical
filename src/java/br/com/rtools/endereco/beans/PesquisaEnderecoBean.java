package br.com.rtools.endereco.beans;

import br.com.rtools.endereco.*;
import br.com.rtools.endereco.dao.EnderecoDao;
import br.com.rtools.utilitarios.CEPService;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PesquisaEnderecoBean implements Serializable {

    private String cep;
    private String tcase;
    private List<Endereco> listaEndereco;

    @PostConstruct
    public void init() {
        listaEndereco = new ArrayList();
        cep = "";
        tcase = "";
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("pesquisaEnderecoBean");
    }

    public void find() {
        listaEndereco.clear();
        if (cep != null && !cep.isEmpty()) {
            if (listaEndereco.isEmpty()) {
                EnderecoDao db = new EnderecoDao();
                listaEndereco = db.pesquisaEnderecoCep(cep);
                if (listaEndereco.isEmpty()) {
                    CEPService cEPService = new CEPService();
                    cEPService.setCep(cep);
                    cEPService.procurar();
                    listaEndereco = db.pesquisaEnderecoCep(cep);
                }
            }
        }
    }

    public void put(String t) {
        tcase = t;
    }

    public String put(Endereco e) {
        switch (tcase) {
            case "pessoaFisica":
                GenericaSessao.put("enderecoPesquisa", e);
                PF.closeDialog("dlg_pesquisa_endereco");
                PF.update("form_pessoa_fisica");
                break;
            case "pessoaJuridica":
                GenericaSessao.put("enderecoPesquisa", e);
                PF.closeDialog("dlg_pesquisa_endereco");
                PF.update("formPessoaJuridica");
                break;
        }
        return tcase;
    }

    public List<Endereco> getListaEndereco() {
        return listaEndereco;
    }

    public void setListaEndereco(List<Endereco> listaEndereco) {
        this.listaEndereco = listaEndereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTcase() {
        return tcase;
    }

    public void setTcase(String tcase) {
        this.tcase = tcase;
    }
}
