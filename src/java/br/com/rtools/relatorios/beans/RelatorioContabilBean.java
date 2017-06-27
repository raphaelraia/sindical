/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioContabilDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class RelatorioContabilBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private String dataInicial;
    private String dataFinal;

    public RelatorioContabilBean() {
        new Jasper().init();

        loadRelatorio();
        loadFilters();
    }

    public void print() {
        if (look()) {
            GenericaMensagem.warn("Validação", "Selecione um filtro!");
            return;
        }

        if (dataInicial.isEmpty() || dataFinal.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar datas!");
            return;
        }

        Relatorios rel = new RelatorioDao().pesquisaRelatorios(idRelatorio);

        List<Object> result = new RelatorioContabilDao().pesquisar(rel, dataInicial, dataFinal);

        if (result.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }

        List<ObjectListaRelatorio> lista = new ArrayList();
        
        for (Object obj : result) {
            List linha = (List) obj;
            lista.add(new ObjectListaRelatorio((Date) linha.get(0), linha.get(1).toString(), linha.get(2).toString(), linha.get(3).toString(), linha.get(4).toString(), (Double) linha.get(5), (Double) linha.get(6)));
        }
        
        
        String detalheRelatorio = "";
        List<String> listDetalhePesquisa = new ArrayList();
        
        listDetalhePesquisa.add("Período: " + dataInicial + " até " + dataFinal);
        if (listDetalhePesquisa.isEmpty()) {
            detalheRelatorio += "Pesquisar todos registros!";
        } else {
            detalheRelatorio += "";
            for (int i = 0; i < listDetalhePesquisa.size(); i++) {
                if (i == 0) {
                    detalheRelatorio += "" + listDetalhePesquisa.get(i);
                } else {
                    detalheRelatorio += "; " + listDetalhePesquisa.get(i);
                }
            }
        }
        
        Jasper.EXPORT_TO = true;
        Jasper.TYPE = "contabil";
        Map map = new HashMap();
        map.put("detalhes_relatorio", detalheRelatorio);
        
        Jasper.printReports(rel.getJasper(), rel.getNome(), (Collection) lista, map);
    }

    public final void loadRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
            }

            if (!list.isEmpty()) {
                idRelatorio = list.get(0).getId();
            }

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    idRelatorio = list.get(i).getId();
                }
                listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public final void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("data", "Data", true, true));
        load(listFilters.get(0));
    }

    public boolean look() {
        for (int i = 0; i < listFilters.size(); i++) {
            if (listFilters.get(i).getActive()) {
                return false;
            }
        }
        return true;
    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadFilters();
                break;
            case 2:
                //loadListDataInicial();
                //loadListDataFinal(false);
                break;
            case 3:
                //loadListDataFinal();
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "data":
                if (filter.getActive()) {
                } else {
                    dataInicial = "";
                    dataFinal = "";
                }
                break;
        }
    }

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
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

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public class ObjectListaRelatorio {

        private Date baixa;
        private String conta;
        private String conta_contabil;
        private String partida;
        private String historico;
        private Double debito;
        private Double credito;

        public ObjectListaRelatorio(Date baixa, String conta, String conta_contabil, String partida, String historico, Double debito, Double credito) {
            this.baixa = baixa;
            this.conta = conta;
            this.conta_contabil = conta_contabil;
            this.partida = partida;
            this.historico = historico;
            this.debito = debito;
            this.credito = credito;
        }

        public Date getBaixa() {
            return baixa;
        }

        public void setBaixa(Date baixa) {
            this.baixa = baixa;
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

        public String getConta_contabil() {
            return conta_contabil;
        }

        public void setConta_contabil(String conta_contabil) {
            this.conta_contabil = conta_contabil;
        }

        public String getPartida() {
            return partida;
        }

        public void setPartida(String partida) {
            this.partida = partida;
        }

        public String getHistorico() {
            return historico;
        }

        public void setHistorico(String historico) {
            this.historico = historico;
        }

        public Double getDebito() {
            return debito;
        }

        public void setDebito(Double debito) {
            this.debito = debito;
        }

        public Double getCredito() {
            return credito;
        }

        public void setCredito(Double credito) {
            this.credito = credito;
        }

    }

}
