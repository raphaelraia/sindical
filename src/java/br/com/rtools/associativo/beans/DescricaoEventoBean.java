package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.AEvento;
import br.com.rtools.associativo.DescricaoEvento;
import br.com.rtools.associativo.GrupoEvento;
import br.com.rtools.associativo.dao.AEventoDao;
import br.com.rtools.associativo.dao.DescricaoEventoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class DescricaoEventoBean implements Serializable {

    private DescricaoEvento descricaoEvento;
    private Integer idGrupoEvento;
    private List<DescricaoEvento> listDescricaoEvento;
    private List<SelectItem> listGrupoEvento;

    @PostConstruct
    public void init() {
        descricaoEvento = new DescricaoEvento();
        idGrupoEvento = 0;
        listDescricaoEvento = new ArrayList();
        listGrupoEvento = new ArrayList();
        loadListGrupoEvento();
        loadListDescricaoEvento();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("descricaoEventoBean");
    }

    public void save() {
        if (descricaoEvento.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE O NOME DO EVENTO!");
            return;
        }
        if (listGrupoEvento.isEmpty()) {
            GenericaMensagem.warn("Validaçao", "CADASTRAR GRUPO EVENTO!");
            return;
        }
        Dao dao = new Dao();
        GrupoEvento grupoEvento = (GrupoEvento) dao.find(new GrupoEvento(), idGrupoEvento);
        descricaoEvento.setGrupoEvento(grupoEvento);
        dao.openTransaction();
        if (new DescricaoEventoDao().existeDescricaoEvento(descricaoEvento)) {
            dao.rollback();
            GenericaMensagem.warn("Validaçao", "DESCRIÇÃO JÁ EXISTE PARA ESTE GRUPO!");
            return;
        }
        if (descricaoEvento.getId() == null) {
            if (!dao.save(descricaoEvento)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ADICIONAR DESCRIÇÃO EVENTO!");
                return;
            }
//            AEvento aEvento = new AEvento();
//            if (!dao.save(aEvento)) {
//                dao.rollback();
//                GenericaMensagem.warn("Erro", "AO ADICIONAR EVENTO!");
//                return;
//            }
            dao.commit();
            new NovoLog().save("GRUPO EVENTO: " + descricaoEvento.getGrupoEvento().getDescricao() + " - DESCRIÇÃO: " + descricaoEvento.getDescricao());
            loadListDescricaoEvento();
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        } else if (!dao.update(descricaoEvento)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ADICIONAR REGISTRO!");
        } else {
            dao.commit();
            new NovoLog().update("", "GRUPO EVENTO: " + descricaoEvento.getGrupoEvento().getDescricao() + " - DESCRIÇÃO: " + descricaoEvento.getDescricao());
            loadListDescricaoEvento();
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
        }
    }

    public void delete(DescricaoEvento de) {
        Dao dao = new Dao();
        dao.openTransaction();
        List<AEvento> list = new AEventoDao().pesquisaPorDescricaoEvento(descricaoEvento.getId());
        for (int i = 0; i < list.size(); i++) {
            if (!dao.delete(list.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER EVENTO, EVENTO JÁ EM USO!");
                return;
            }
        }
        if (!dao.delete(de)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER DESCRIÇÃO EVENTO");
            return;
        }
        dao.commit();
        new NovoLog().delete("GRUPO EVENTO: " + de.getGrupoEvento().getDescricao() + " - DESCRIÇÃO: " + de.getDescricao());
        GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        loadListDescricaoEvento();
        descricaoEvento = new DescricaoEvento();
    }

    public void edit(DescricaoEvento de) {
        descricaoEvento = (DescricaoEvento) new Dao().rebind(de);
        idGrupoEvento = descricaoEvento.getGrupoEvento().getId();
    }

    public void loadListDescricaoEvento() {
        listDescricaoEvento = new ArrayList();
        listDescricaoEvento = (List<DescricaoEvento>) new Dao().list(new DescricaoEvento(), true);
    }

    public void loadListGrupoEvento() {
        listGrupoEvento = new ArrayList();
        List<GrupoEvento> list = (List<GrupoEvento>) new Dao().list(new GrupoEvento(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupoEvento = list.get(i).getId();
            }
            listGrupoEvento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public DescricaoEvento getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(DescricaoEvento descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }

    public Integer getIdGrupoEvento() {
        return idGrupoEvento;
    }

    public void setIdGrupoEvento(Integer idGrupoEvento) {
        this.idGrupoEvento = idGrupoEvento;
    }

    public List<DescricaoEvento> getListDescricaoEvento() {
        return listDescricaoEvento;
    }

    public void setListDescricaoEvento(List<DescricaoEvento> listDescricaoEvento) {
        this.listDescricaoEvento = listDescricaoEvento;
    }

    public List<SelectItem> getListGrupoEvento() {
        return listGrupoEvento;
    }

    public void setListGrupoEvento(List<SelectItem> listGrupoEvento) {
        this.listGrupoEvento = listGrupoEvento;
    }

}
