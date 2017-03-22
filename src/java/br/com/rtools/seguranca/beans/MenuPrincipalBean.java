package br.com.rtools.seguranca.beans;

import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.utilitarios.DataObject;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MenuPrincipalBean {

    public List<DataObject> listInconsistenciaBaixa = null;

    public List<DataObject> getListInconsistenciaBaixa() {
        if (listInconsistenciaBaixa == null) {
            listInconsistenciaBaixa = DataObject.converte(new MovimentoDao().existsInconsistenciaBaixa());
        }
        return listInconsistenciaBaixa;
    }

}
