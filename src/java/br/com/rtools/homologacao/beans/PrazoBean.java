/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.homologacao.beans;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.homologacao.Prazo;
import br.com.rtools.homologacao.dao.PrazoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
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
public class PrazoBean implements Serializable {

    private List<SelectItem> listaConvencao = new ArrayList();
    private List<SelectItem> listaGrupoCidade = new ArrayList();
    private Integer indexConvencao = 0;
    private Integer indexGrupoCidade = 0;
    private Prazo prazo = new Prazo();
    private List<Prazo> listaPrazo = new ArrayList();

    public PrazoBean() {
        loadListaConvencao();
        loadListaGrupoCidade();
        loadListaPrazo();
    }

    public void adicionar() {
        if (prazo.getCidade().getId() == -1) {
            GenericaMensagem.warn("ATENÇÃO", "Pesquise uma Cidade");
            return;
        }
        Dao dao = new Dao();

        prazo.setConvencao((Convencao) dao.find(new Convencao(), Integer.valueOf(listaConvencao.get(indexConvencao).getDescription())));
        prazo.setGrupoCidade((GrupoCidade) dao.find(new GrupoCidade(), Integer.valueOf(listaGrupoCidade.get(indexGrupoCidade).getDescription())));

        dao.openTransaction();

        if (prazo.getId() == -1) {
            if (!dao.save(prazo)) {
                GenericaMensagem.error("ATENÇÃO", "Erro ao Adicionar Prazo!");
                dao.rollback();
                return;
            }
        } else {
            if (!dao.update(prazo)) {
                GenericaMensagem.error("ATENÇÃO", "Erro ao Alterar Prazo!");
                dao.rollback();
                return;
            }
        }

        GenericaMensagem.info("SUCESSO", "Registro Salvo!");
        
        dao.commit();
        prazo = new Prazo();
        indexConvencao = 0;
        indexGrupoCidade = 0;
        loadListaPrazo();
    }

    public void editar(Prazo p) {
        prazo = p;
        for (int i = 0; i < listaConvencao.size(); i++){
            if (prazo.getConvencao().getId() == Integer.valueOf(listaConvencao.get(i).getDescription()) ){
                indexConvencao = i;
            }
        }
        
        for (int i = 0; i < listaGrupoCidade.size(); i++){
            if (prazo.getGrupoCidade().getId() == Integer.valueOf(listaGrupoCidade.get(i).getDescription()) ){
                indexGrupoCidade = i;
            }
        }
    }

    public void excluir(){
        Dao dao = new Dao();
        
        dao.openTransaction();
        
        if (!dao.delete(dao.find(prazo))) {
            GenericaMensagem.error("ATENÇÃO", "Não foi possível excluir Registro!");
            dao.rollback();
            return;
        } 
        GenericaMensagem.info("SUCESSO", "Registro Excluído!");
        
        dao.commit();
        prazo = new Prazo();
        loadListaPrazo();
        
    }
    
    public final void loadListaConvencao() {
        listaConvencao.clear();
        indexConvencao = 0;

        List<Convencao> result = new PrazoDao().listaConvencao();

        for (int i = 0; i < result.size(); i++) {
            listaConvencao.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaGrupoCidade() {
        listaGrupoCidade.clear();
        indexGrupoCidade = 0;

        List<GrupoCidade> result = new PrazoDao().listaGrupoCidade();

        for (int i = 0; i < result.size(); i++) {
            listaGrupoCidade.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaPrazo() {
        listaPrazo.clear();

        listaPrazo = new PrazoDao().listaPrazo();
    }

    public List<SelectItem> getListaConvencao() {
        return listaConvencao;
    }

    public void setListaConvencao(List<SelectItem> listaConvencao) {
        this.listaConvencao = listaConvencao;
    }

    public List<SelectItem> getListaGrupoCidade() {
        return listaGrupoCidade;
    }

    public void setListaGrupoCidade(List<SelectItem> listaGrupoCidade) {
        this.listaGrupoCidade = listaGrupoCidade;
    }

    public Integer getIndexConvencao() {
        return indexConvencao;
    }

    public void setIndexConvencao(Integer indexConvencao) {
        this.indexConvencao = indexConvencao;
    }

    public Integer getIndexGrupoCidade() {
        return indexGrupoCidade;
    }

    public void setIndexGrupoCidade(Integer indexGrupoCidade) {
        this.indexGrupoCidade = indexGrupoCidade;
    }

    public Prazo getPrazo() {
        if (GenericaSessao.exists("cidadePesquisa")) {
            prazo.setCidade((Cidade) GenericaSessao.getObject("cidadePesquisa", true));
        }
        return prazo;
    }

    public void setPrazo(Prazo prazo) {
        this.prazo = prazo;
    }

    public List<Prazo> getListaPrazo() {
        return listaPrazo;
    }

    public void setListaPrazo(List<Prazo> listaPrazo) {
        this.listaPrazo = listaPrazo;
    }

}
