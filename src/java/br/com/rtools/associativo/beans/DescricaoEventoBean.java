package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.AEvento;
import br.com.rtools.associativo.DescricaoEvento;
import br.com.rtools.associativo.GrupoEvento;
import br.com.rtools.associativo.dao.AEventoDao;
import br.com.rtools.associativo.dao.DescricaoEventoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class DescricaoEventoBean implements Serializable {

    private DescricaoEvento descricaoEvento = new DescricaoEvento();
    private int idGrupoEvento = 0;
    private List<DescricaoEvento> listaDescricaoEvento = new ArrayList();
    private List<SelectItem> listaGrupoEvento = new ArrayList();

    public void salvar() {
        if (descricaoEvento.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite um nome para o Evento!");
            PF.update("formDescricaoEvento:i_msg");
            return;
        }

        if (getListaGrupoEvento().isEmpty()) {
            GenericaMensagem.warn("Validaçao", "Lista de Grupo Evento vazia!");
            PF.update("formDescricaoEvento:i_msg");
            return;
        }

        DescricaoEventoDao dedb = new DescricaoEventoDao();
        Dao dao = new Dao();
        GrupoEvento grupoEvento = (GrupoEvento) dao.find(new GrupoEvento(), Integer.parseInt(getListaGrupoEvento().get(idGrupoEvento).getDescription()));
        descricaoEvento.setGrupoEvento(grupoEvento);

        if (dedb.existeDescricaoEvento(descricaoEvento)) {
            GenericaMensagem.warn("Validaçao", "Descrição já cadastrada para o grupo selecionado!");
            PF.update("formDescricaoEvento:i_msg");
            return;
        }

        dao.openTransaction();

        if (descricaoEvento.getId() == -1) {
            if (!dao.save(descricaoEvento)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao adicionar registro!");
                PF.update("formDescricaoEvento:i_msg");
                GenericaMensagem.info("Sucesso", "Registro adicionado");
            }

            NovoLog novoLog = new NovoLog();
            novoLog.save(descricaoEvento.toString());
        } else if (!dao.update(descricaoEvento)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao adicionar registro!");
            PF.update("formDescricaoEvento:i_msg");
            return;
        }

        dao.commit();
        descricaoEvento = new DescricaoEvento();
        listaDescricaoEvento.clear();
        PF.update("formDescricaoEvento");
    }

    public void excluir(DescricaoEvento de) {
        Dao dao = new Dao();
        List<AEvento> ae = new AEventoDao().pesquisaPorDescricaoEvento(de.getId());
        dao.openTransaction();
        for (AEvento ae1 : ae) {
            if (!dao.delete((AEvento) dao.find(ae1))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao excluir evento!");
                PF.update("formDescricaoEvento:i_msg");
                return;
            }
        }
        if (!dao.delete((DescricaoEvento) dao.find(de))) {
            dao.commit();
            GenericaMensagem.warn("Erro", "Ao excluir registro");
            PF.update("formDescricaoEvento:i_msg");
        } else {
            NovoLog novoLog = new NovoLog();
            novoLog.delete(de.toString());
            dao.commit();
            GenericaMensagem.info("Sucesso", "Registro excluído!");
            listaDescricaoEvento.clear();
            PF.update("formDescricaoEvento");
        }
    }

    public void editar(DescricaoEvento de) {
        descricaoEvento = de;

        for (int i = 0; i < listaGrupoEvento.size(); i++) {
            if (descricaoEvento.getGrupoEvento().getId() == Integer.valueOf(listaGrupoEvento.get(i).getDescription())) {
                idGrupoEvento = i;
            }
        }
    }

    public List<DescricaoEvento> getListaDescricaoEvento() {
        if (listaDescricaoEvento.isEmpty()) {
            listaDescricaoEvento = (List<DescricaoEvento>) new Dao().list(new DescricaoEvento(), true);
        }
        return listaDescricaoEvento;
    }

    public void setListaDescricaoEvento(List<DescricaoEvento> listaDescricaoEvento) {
        this.listaDescricaoEvento = listaDescricaoEvento;
    }

    public List<SelectItem> getListaGrupoEvento() {
        if (listaGrupoEvento.isEmpty()) {
            List<GrupoEvento> list = (List<GrupoEvento>) new Dao().list(new GrupoEvento(), true);
            for (int i = 0; i < list.size(); i++) {
                listaGrupoEvento.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
            }
        }
        return listaGrupoEvento;
    }

    public DescricaoEvento getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(DescricaoEvento descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }

    public int getIdGrupoEvento() {
        return idGrupoEvento;
    }

    public void setIdGrupoEvento(int idGrupoEvento) {
        this.idGrupoEvento = idGrupoEvento;
    }

    public void setListaGrupoEvento(List<SelectItem> listaGrupoEvento) {
        this.listaGrupoEvento = listaGrupoEvento;
    }
}
