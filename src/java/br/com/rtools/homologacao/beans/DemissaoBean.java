package br.com.rtools.homologacao.beans;

import br.com.rtools.homologacao.Demissao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class DemissaoBean implements Serializable {

    private Demissao demissao;
    private List<Demissao> listDemissao;

    @PostConstruct
    public void init() {
        demissao = new Demissao();
        loadListDemissao();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("demissaoBean");
    }

    public void save() {
        if (demissao.getMensagemMotivoWeb().isEmpty() && !demissao.getMotivoWeb()) {
            GenericaMensagem.warn("Validação", "INFORMAR MOTIVO");
            return;
        }
        if (demissao.getId() == null) {
            if (new Dao().save(demissao, true)) {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                loadListDemissao();
            } else {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
            }
        } else if (new Dao().update(demissao, true)) {
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
            loadListDemissao();
        } else {
            GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO!");
        }
    }

    public void edit(Demissao d) {
        demissao = (Demissao) new Dao().rebind(d);
    }

    public void delete(Demissao d) {
        if (Objects.equals(d.getId(), demissao.getId())) {
            demissao = (Demissao) new Dao().rebind(d);
            demissao = new Demissao();
        }
        if (new Dao().delete(d, true)) {
            GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
            demissao = new Demissao();
            loadListDemissao();
        } else {
            GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO!");
        }
    }

    public Demissao getDemissao() {
        return demissao;
    }

    public void setDemissao(Demissao demissao) {
        this.demissao = demissao;
    }

    public void loadListDemissao() {
        listDemissao = new ArrayList();
        listDemissao = new Dao().list(new Demissao(), true);
    }

    public List<Demissao> getListDemissao() {
        return listDemissao;
    }

    public void setListDemissao(List<Demissao> listDemissao) {
        this.listDemissao = listDemissao;
    }
}
