/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.escola.beans;

import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
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
public class RelatorioFinanceiroEscolaBean implements Serializable {
    private Integer idRelatorio = 0;
    private List<SelectItem> listaRelatorio = new ArrayList();

    private Integer idRelatorioOrdem = 0;
    private List<SelectItem> listaRelatorioOrdem = new ArrayList();

    public RelatorioFinanceiroEscolaBean() {
        loadListaRelatorio();
    }


    public final void loadListaRelatorioOrdem() {
        listaRelatorioOrdem.clear();

        RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
        List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(Integer.parseInt(listaRelatorio.get(idRelatorio).getDescription()));

        for (int i = 0; i < list.size(); i++) {
            listaRelatorioOrdem.add(
                    new SelectItem(
                            i,
                            list.get(i).getNome(),
                            "" + list.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaRelatorio() {
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(377);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRelatorio = i;
            }
            if (list.get(i).getPrincipal()) {
                idRelatorio = i;
            }
            listaRelatorio.add(
                    new SelectItem(
                            i,
                            list.get(i).getNome(),
                            Integer.toString(list.get(i).getId())
                    )
            );
        }

        loadListaRelatorioOrdem();
    }
    
    
    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public List<SelectItem> getListaRelatorio() {
        return listaRelatorio;
    }

    public void setListaRelatorio(List<SelectItem> listaRelatorio) {
        this.listaRelatorio = listaRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public List<SelectItem> getListaRelatorioOrdem() {
        return listaRelatorioOrdem;
    }

    public void setListaRelatorioOrdem(List<SelectItem> listaRelatorioOrdem) {
        this.listaRelatorioOrdem = listaRelatorioOrdem;
    }

}
