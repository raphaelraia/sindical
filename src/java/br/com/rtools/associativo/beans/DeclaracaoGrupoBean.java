/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.DeclaracaoGrupo;
import br.com.rtools.associativo.DeclaracaoTipo;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.DeclaracaoGrupoDao;
import br.com.rtools.associativo.dao.DeclaracaoTipoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class DeclaracaoGrupoBean implements Serializable {

    private Integer indexDeclaracaoTipo = 0;
    private List<SelectItem> listaDeclaracaoTipo = new ArrayList();
    private Integer indexSubGrupo = 0;
    private List<SelectItem> listaSubGrupo = new ArrayList();

    private List<DeclaracaoGrupo> listaDeclaracaoGrupo = new ArrayList();
    private DeclaracaoGrupo declaracaoGrupo = new DeclaracaoGrupo();

    public DeclaracaoGrupoBean() {
        loadListaDeclaracaoTipo();
        loadListaSubGrupo();
        loadListaDeclaracaoGrupo();
    }

    public final void loadListaDeclaracaoTipo() {
        listaDeclaracaoTipo.clear();
        List<DeclaracaoTipo> result = new DeclaracaoTipoDao().listaDeclaracaoTipo();

        for (int i = 0; i < result.size(); i++) {
            listaDeclaracaoTipo.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaSubGrupo() {
        listaSubGrupo.clear();
        List<SubGrupoConvenio> result = new DeclaracaoGrupoDao().listaSubGrupoConvenio();

        for (int i = 0; i < result.size(); i++) {
            listaSubGrupo.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao() + " ( " + result.get(i).getGrupoConvenio().getDescricao() + " )",
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaDeclaracaoGrupo() {
        listaDeclaracaoGrupo.clear();
        
        listaDeclaracaoGrupo = new DeclaracaoGrupoDao().listaDeclaracaoGrupo();
    }

    public Boolean validaAdicionar() {
        DeclaracaoGrupoDao dao = new DeclaracaoGrupoDao();
        if (dao.existeDeclaracaoGrupo(Integer.valueOf(listaDeclaracaoTipo.get(indexDeclaracaoTipo).getDescription()), Integer.valueOf(listaSubGrupo.get(indexSubGrupo).getDescription()))){
            GenericaMensagem.warn("Atenção", "Tipo e Sub Grupo já adicionados!");
            return false;
        }
        return true;
    }

    public void adicionar() {
        if (!validaAdicionar()){
            return;
        }
        
        Dao dao = new Dao();
        
        dao.openTransaction();
        
        declaracaoGrupo = new DeclaracaoGrupo();
        declaracaoGrupo.setDeclaracaoTipo((DeclaracaoTipo) dao.find(new DeclaracaoTipo(), Integer.valueOf(listaDeclaracaoTipo.get(indexDeclaracaoTipo).getDescription())));
        declaracaoGrupo.setSubGrupoConvenio((SubGrupoConvenio) dao.find(new SubGrupoConvenio(), Integer.valueOf(listaSubGrupo.get(indexSubGrupo).getDescription())));
        
        if (!dao.save(declaracaoGrupo)){
            dao.rollback();
            GenericaMensagem.error("Atenção", "Erro ao Adicionar Grupo Declaração!");
            return;
        }
        
        dao.commit();
        
        declaracaoGrupo = new DeclaracaoGrupo();
        loadListaDeclaracaoGrupo();
        
        GenericaMensagem.info("Sucesso", "Grupo Declaração adicionada!");
    }

    public void selecionarDeclaracaoGrupo(DeclaracaoGrupo dg) {
        declaracaoGrupo = dg;
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(declaracaoGrupo)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Não foi possível excluir Declaração Grupo!");
            return;
        }

        dao.commit();
        loadListaDeclaracaoGrupo();
        GenericaMensagem.info("Sucesso", "Declaração Grupo excluída!");
    }

    public Integer getIndexDeclaracaoTipo() {
        return indexDeclaracaoTipo;
    }

    public void setIndexDeclaracaoTipo(Integer indexDeclaracaoTipo) {
        this.indexDeclaracaoTipo = indexDeclaracaoTipo;
    }

    public List<SelectItem> getListaDeclaracaoTipo() {
        return listaDeclaracaoTipo;
    }

    public void setListaDeclaracaoTipo(List<SelectItem> listaDeclaracaoTipo) {
        this.listaDeclaracaoTipo = listaDeclaracaoTipo;
    }

    public Integer getIndexSubGrupo() {
        return indexSubGrupo;
    }

    public void setIndexSubGrupo(Integer indexSubGrupo) {
        this.indexSubGrupo = indexSubGrupo;
    }

    public List<SelectItem> getListaSubGrupo() {
        return listaSubGrupo;
    }

    public void setListaSubGrupo(List<SelectItem> listaSubGrupo) {
        this.listaSubGrupo = listaSubGrupo;
    }

    public List<DeclaracaoGrupo> getListaDeclaracaoGrupo() {
        return listaDeclaracaoGrupo;
    }

    public void setListaDeclaracaoGrupo(List<DeclaracaoGrupo> listaDeclaracaoGrupo) {
        this.listaDeclaracaoGrupo = listaDeclaracaoGrupo;
    }

    public DeclaracaoGrupo getDeclaracaoGrupo() {
        return declaracaoGrupo;
    }

    public void setDeclaracaoGrupo(DeclaracaoGrupo declaracaoGrupo) {
        this.declaracaoGrupo = declaracaoGrupo;
    }
}
