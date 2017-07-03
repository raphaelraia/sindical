package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.EventoBaile;
import br.com.rtools.associativo.dao.VendaBaileDao;
import br.com.rtools.impressao.ParametroFechamentoBaile;
import br.com.rtools.relatorios.dao.RelatorioFechamentoBaileDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioFechamentoBaileBean implements Serializable {

    private final List<SelectItem> listaEventoBaile = new ArrayList();
    private Integer idEventoBaile = 0;
    private Boolean EXPORT_TO = false;
    private Boolean mostrar_todos = false;

    @PostConstruct
    public void init() {
        loadListaEventoBaile();
    }

    @PreDestroy
    public void destroy() {

    }

    public void clear(Integer tcase) {
        switch (tcase) {
            case 1:
                loadListaEventoBaile();
                break;
        }
    }

    public void imprimir() {
        RelatorioFechamentoBaileDao dao = new RelatorioFechamentoBaileDao();

        List list = dao.listaEventoBaile(Integer.valueOf(listaEventoBaile.get(idEventoBaile).getDescription()));
        if (list.isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum registro encontrado!");
            return;
        }
        List<ParametroFechamentoBaile> lista = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            List object = (List) list.get(i);
            String status = "";
            if (object.get(0) == null) {
                GenericaMensagem.warn("Erro", "Esta baile esta com registro vazios/nulos!");
                return;
            }
            try {
                if (object.get(4) != null) {
                    status = object.get(4).toString();
                    if (((Integer) object.get(12) == 13 || (Integer) object.get(12) == 15)) {
                        status = "Cortesia";
                    }

                }
            } catch (Exception e) {
            }
            Double valor = (object.get(9) != null ? (Double) object.get(9) : 0);
            lista.add(
                    new ParametroFechamentoBaile(
                            object.get(0), // EMISSAO
                            object.get(1), // OPERADOR
                            object.get(2), // CODIGO
                            object.get(3), // CONVIDADO
                            status, // STATUS
                            object.get(5), // MESA
                            object.get(6), // CONVITE
                            object.get(7), // VENCIMENTO
                            object.get(8), // PAGAMENTO
                            valor, // VALOR
                            object.get(10), // CAIXA
                            object.get(11), // OBS
                            null
                    )
            );
        }
        Jasper.TITLE = "FECHAMENTO BAILE \n " + listaEventoBaile.get(idEventoBaile).getLabel();
        Jasper.printReports("/Relatorios/FECHAMENTO_BAILE.jasper", "fechamento_baile", lista);
    }

    public void loadListaEventoBaile() {
        listaEventoBaile.clear();
        VendaBaileDao dao = new VendaBaileDao();
        List<EventoBaile> result = dao.listaBaile(mostrar_todos);
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaEventoBaile.add(new SelectItem(
                        i,
                        result.get(i).getEvento().getDescricaoEvento().getDescricao() + " -  "
                        + result.get(i).getDataString() + " - ("
                        + result.get(i).getHoraInicio() + " às  "
                        + result.get(i).getHoraFim() + ")   "
                        + result.get(i).getQuantidadeMesas() + " mesas  / " + result.get(i).getQuantidadeConvites() + " convites",
                        Integer.toString((result.get(i)).getId())
                )
                );
            }
        }
    }

    public List<SelectItem> getListaEventoBaile() {
        return listaEventoBaile;
    }

    public Integer getIdEventoBaile() {
        return idEventoBaile;
    }

    public void setIdEventoBaile(Integer idEventoBaile) {
        this.idEventoBaile = idEventoBaile;
    }

    public Boolean getMostrar_todos() {
        return mostrar_todos;
    }

    public void setMostrar_todos(Boolean mostrar_todos) {
        this.mostrar_todos = mostrar_todos;
    }
}
