package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.impressao.ParametroChequesRecebidos;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.relatorios.dao.RelatorioFinanceiroDao;
import br.com.rtools.relatorios.dao.RelatorioChequesRecebidosDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioChequesRecebidosBean implements Serializable {

    // FILIAL
    private boolean chkFilial = false;
    private List<Filial> listaFilial = new ArrayList<>();
    private List<Filial> listaFilialSelecionada = new ArrayList<>();

    // CAIXA
    private boolean chkCaixa = false;
    private List<Caixa> listaCaixa = new ArrayList<>();
    private List<Caixa> listaCaixaSelecionado = new ArrayList<>();

    // TIPO DATA
    private boolean chkTipoData = false;
    private String tipoData = "emissao";
    private String dataInicial = DataHoje.data();
    private String dataFinal = DataHoje.data();

    // STATUS
    private boolean chkStatus = false;
    private int idStatus = 0;
    private List<SelectItem> listaStatus = new ArrayList<>();

    private boolean chkConta = false;

    private Map<String, Integer> listContas;
    private List selectedContas;

    public void porFilial() {
        chkFilial = chkFilial == true ? false : true;
    }

    public void porCaixa() {
        chkCaixa = chkCaixa == true ? false : true;
    }

    public void porTipoData() {
        chkTipoData = chkTipoData == true ? false : true;
    }

    public void porStatus() {
        chkStatus = chkStatus == true ? false : true;
    }

    public void porConta() {
        chkConta = chkConta != true;
        if (chkConta) {
            loadContas();
        } else {
            selectedContas = new ArrayList();
            listContas = new HashMap<>();
        }
    }

    public void loadContas() {
        selectedContas = new ArrayList();
        listContas = new HashMap<>();
        List<Plano5> list = new RelatorioChequesRecebidosDao().findAllPlanos5CaixaBanco();
        for (int i = 0; i < list.size(); i++) {
            listContas.put(list.get(i).getConta(), list.get(i).getId());
        }
    }

    public void visualizar() {
        String ids_filial = "";
        if (chkFilial && !listaFilialSelecionada.isEmpty()) {
            for (int i = 0; i < listaFilialSelecionada.size(); i++) {
                if (ids_filial.length() > 0 && i != listaFilialSelecionada.size()) {
                    ids_filial += ",";
                }
                ids_filial += listaFilialSelecionada.get(i).getId();
            }
        }

        String ids_caixa = "";
        if (chkCaixa && !listaCaixaSelecionado.isEmpty()) {
            for (int i = 0; i < listaCaixaSelecionado.size(); i++) {
                if (ids_caixa.length() > 0 && i != listaCaixaSelecionado.size()) {
                    ids_caixa += ",";
                }
                ids_caixa += listaCaixaSelecionado.get(i).getId();
            }
        }

        String tipo = "", d_i = "", d_f = "";
        if (chkTipoData && (!dataInicial.isEmpty() || !dataFinal.isEmpty())) {
            tipo = tipoData;
            d_i = dataInicial;
            d_f = dataFinal;
        }

        int id_status = 0;
        if (chkStatus) {
            id_status = Integer.valueOf(listaStatus.get(idStatus).getDescription());
        }

        Collection lista = new ArrayList();

        RelatorioFinanceiroDao db = new RelatorioFinanceiroDao();

        List<Vector> result = db.listaChequesRecebidos(ids_filial, ids_caixa, tipo, d_i, d_f, id_status, inIdContas());

        for (Vector linha : result) {
            lista.add(new ParametroChequesRecebidos(
                    linha.get(0).toString(), // FILIAL
                    linha.get(1).toString(), // EMISSAO
                    linha.get(2).toString(), // VENCIMENTO
                    linha.get(3).toString(), // BANCO
                    linha.get(4).toString(), // AGENCIA
                    linha.get(5).toString(), // CONTA
                    linha.get(6).toString(), // CHEQUE
                    new BigDecimal(linha.get(7).toString()), // VALOR
                    linha.get(8).toString(), // ID_BAIXA
                    linha.get(9).toString() // CAIXA
            ));
        }
        Jasper.printReports("/Relatorios/CHEQUES_RECEBIDOS.jasper", "Relatório Cheques Recebidos", lista);
    }

    public boolean isChkFilial() {
        return chkFilial;
    }

    public void setChkFilial(boolean chkFilial) {
        this.chkFilial = chkFilial;
    }

    public List<Filial> getListaFilial() {
        if (listaFilial.isEmpty()) {
            listaFilial = (List<Filial>) (new Dao()).list(new Filial(), true);
        }
        return listaFilial;
    }

    public void setListaFilial(List<Filial> listaFilial) {
        this.listaFilial = listaFilial;
    }

    public List<Filial> getListaFilialSelecionada() {
        return listaFilialSelecionada;
    }

    public void setListaFilialSelecionada(List<Filial> listaFilialSelecionada) {
        this.listaFilialSelecionada = listaFilialSelecionada;
    }

    public boolean isChkCaixa() {
        return chkCaixa;
    }

    public void setChkCaixa(boolean chkCaixa) {
        this.chkCaixa = chkCaixa;
    }

    public List<Caixa> getListaCaixa() {
        if (listaCaixa.isEmpty()) {
            listaCaixa = (new FinanceiroDBToplink()).listaCaixa();
        }
        return listaCaixa;
    }

    public void setListaCaixa(List<Caixa> listaCaixa) {
        this.listaCaixa = listaCaixa;
    }

    public List<Caixa> getListaCaixaSelecionado() {
        return listaCaixaSelecionado;
    }

    public void setListaCaixaSelecionado(List<Caixa> listaCaixaSelecionado) {
        this.listaCaixaSelecionado = listaCaixaSelecionado;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
    }

    public boolean isChkTipoData() {
        return chkTipoData;
    }

    public void setChkTipoData(boolean chkTipoData) {
        this.chkTipoData = chkTipoData;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public boolean isChkStatus() {
        return chkStatus;
    }

    public void setChkStatus(boolean chkStatus) {
        this.chkStatus = chkStatus;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            RelatorioFinanceiroDao db = new RelatorioFinanceiroDao();

            List<FStatus> select = db.listaStatusCheque("7,8,9,10,11");

            for (int i = 0; i < select.size(); i++) {
                listaStatus.add(new SelectItem(
                        i,
                        select.get(i).getDescricao(),
                        Integer.toString(select.get(i).getId()))
                );
            }

        }
        return listaStatus;
    }

    public void setListaStatus(List<SelectItem> listaStatus) {
        this.listaStatus = listaStatus;
    }

    public Map<String, Integer> getListContas() {
        return listContas;
    }

    public void setListContas(Map<String, Integer> listContas) {
        this.listContas = listContas;
    }

    public List getSelectedContas() {
        return selectedContas;
    }

    public void setSelectedContas(List selectedContas) {
        this.selectedContas = selectedContas;
    }

    public boolean isChkConta() {
        return chkConta;
    }

    public void setChkConta(boolean chkConta) {
        this.chkConta = chkConta;
    }

    public String inIdContas() {
        String ids = null;
        if (selectedContas != null) {
            ids = "";
            for (int i = 0; i < selectedContas.size(); i++) {
                if (selectedContas.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedContas.get(i);
                    } else {
                        ids += "," + selectedContas.get(i);
                    }
                }
            }
        }
        return ids;
    }

}
