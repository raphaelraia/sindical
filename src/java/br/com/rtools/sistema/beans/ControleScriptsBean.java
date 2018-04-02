package br.com.rtools.sistema.beans;

import br.com.rtools.sistema.ControleScripts;
import br.com.rtools.sistema.dao.ControleScriptsDao;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class ControleScriptsBean implements Serializable {

    private List<ControleScripts> listControleScripts;
    private String dataInicial;
    private String dataFinal;

    public ControleScriptsBean() {
        listControleScripts = new ArrayList();
        dataInicial = DataHoje.data();
        dataFinal = "";
        loadListControleScripts();
    }

    public final void loadListControleScripts() {
        listControleScripts = new ArrayList();
        listControleScripts = new ControleScriptsDao().find(dataInicial, dataFinal);
    }

    public List<ControleScripts> getListControleScripts() {
        return listControleScripts;
    }

    public void setListControleScripts(List<ControleScripts> listControleScripts) {
        this.listControleScripts = listControleScripts;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

}
