/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.DeclaracaoTipo;
import br.com.rtools.associativo.dao.DeclaracaoPessoaDao;
import br.com.rtools.associativo.dao.DeclaracaoTipoDao;
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
public class DeclaracaoPessoaBean implements Serializable {

    private Integer indexDeclaracaoTipo = 0;
    private List<SelectItem> listaDeclaracaoTipo = new ArrayList();
    private Integer indexConvenio = 0;
    private List<SelectItem> listaConvenio = new ArrayList();

    public DeclaracaoPessoaBean() {
        loadListaDeclaracaoTipo();
        loadListaConvenio();
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

    public final void loadListaConvenio() {
        listaConvenio.clear();
        List<Object> result = new DeclaracaoPessoaDao().listaConvenio(Integer.valueOf(listaDeclaracaoTipo.get(indexDeclaracaoTipo).getDescription()));

        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);

            listaConvenio.add(
                    new SelectItem(
                            i,
                            linha.get(1).toString() + "; " + linha.get(2).toString() + "; " + linha.get(3).toString(),
                            "" + (Integer) linha.get(0)
                    )
            );
        }
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

    public Integer getIndexConvenio() {
        return indexConvenio;
    }

    public void setIndexConvenio(Integer indexConvenio) {
        this.indexConvenio = indexConvenio;
    }

    public List<SelectItem> getListaConvenio() {
        return listaConvenio;
    }

    public void setListaConvenio(List<SelectItem> listaConvenio) {
        this.listaConvenio = listaConvenio;
    }
}
