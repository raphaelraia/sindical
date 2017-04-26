package br.com.rtools.seguranca.beans;

import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.sistema.Erros;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Jasper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MenuPrincipalBean {

    public List<DataObject> listInconsistenciaBaixa = null;
    private List<Erros> listErros;

    public List<DataObject> getListInconsistenciaBaixa() {
        if (listInconsistenciaBaixa == null) {
            listInconsistenciaBaixa = DataObject.converte(new MovimentoDao().existsInconsistenciaBaixa());
        }
        return listInconsistenciaBaixa;
    }

    public List<Erros> getListErros() {
        if (listErros == null) {
            listErros = new ArrayList();
            listErros = new Dao().list(new Erros(), true);
        }
        return listErros;
    }

    public void setListErros(List<Erros> listErros) {
        this.listErros = listErros;
    }

    public void printErros() {
        Jasper.printReports("/Relatorios/ERROS.jasper", "Códigos de Erro", (Collection) listErros);
    }

}
