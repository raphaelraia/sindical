package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.dao.ContaBancoDao;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioChequesDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioChequesBean implements Serializable {

    private Integer idRelatorioOrdem;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private List<Filters> filters;
    private Map<String, Integer> listContaBanco;
    private List selectedContaBanco;
    private Map<String, Integer> listPlano5;
    private List selectedPlano5;
    // private String selectedStatus;

    // DATAS
    // private List<DateFilters> listDateFilters;
    private List<SelectItem> listDates;
    private String selectedDate;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private Pessoa pessoa;
    private List<Pessoa> listPessoa;

    public RelatorioChequesBean() {
        // selectedStatus = "";
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        loadFilters();
        loadRelatorios();
        loadRelatoriosOrdem();
    }

    public void clear() {
        GenericaSessao.put("relatorioLocadoraBean", new RelatorioLocadoraBean());
    }

    public void listener(String tcase) {
        if (tcase.equals("relatorios")) {
            loadRelatoriosOrdem();
        }
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("conta_banco", "Conta Banco", false));
        filters.add(new Filters("plano5", "Histórico", false));
        filters.add(new Filters("status", "Status", false));
        filters.add(new Filters("pessoa", "Pessoa", false));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters f = new Filters();
        f.setKey(filter);
        f.setActive(false);
        for (Filters f2 : filters) {
            if (f2.getKey().equals(filter)) {
                f2.setActive(false);
            }
        }
        load(f);
    }

    public void loadStatus() {
        typeDate = "todos";
        startDate = "";
        finishDate = "";
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {

            case "status":
                selectedDate = "";
                loadStatus();
                if (filter.getActive()) {
                    selectedDate = "nao_impressos";
                    loadDates();
                }

                break;
            case "conta_banco":
                listContaBanco = new LinkedHashMap<>();
                selectedContaBanco = new ArrayList();
                if (filter.getActive()) {
                    loadContaBanco();
                }
                break;
            case "plano5":
                listPlano5 = new LinkedHashMap<>();
                selectedPlano5 = new ArrayList();
                if (filter.getActive()) {
                    loadPlano5();
                }
                break;
            case "pessoa":
                listPessoa = new ArrayList();
                pessoa = new Pessoa();
                break;
        }
    }

    public void addPessoa() {
        for (int i = 0; i < listPessoa.size(); i++) {
            if (listPessoa.get(i).getId() == pessoa.getId()) {
                GenericaMensagem.warn("Validação", "PESSOA JÁ SELECIONADA!");
                return;
            }
        }
        listPessoa.add(pessoa);
        pessoa = new Pessoa();
    }

    public void removePessoa() {
        pessoa = new Pessoa();
    }

    public void removePessoa(Pessoa p) {
        listPessoa.remove(pessoa);
    }

    public String print() {
        Relatorios relatorios = new RelatorioDao().pesquisaRelatorios(idRelatorio);
        List<ObjectCheques> listObjectCheques = new ArrayList<>();
        DateFilters dateFilters = new DateFilters();
        dateFilters.setTitle(selectedDate);
        dateFilters.setType(typeDate);
        dateFilters.setStart(startDate);
        dateFilters.setFinish(finishDate);
        List list = new RelatorioChequesDao().find(inIdPlano5(), inIdContaBanco(), inIdPessoas(), dateFilters);
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            listObjectCheques.add(
                    new ObjectCheques(
                            o.get(0),
                            o.get(1),
                            o.get(2),
                            o.get(3),
                            o.get(4),
                            o.get(5),
                            o.get(6),
                            o.get(7),
                            o.get(8),
                            o.get(9),
                            o.get(10),
                            o.get(11)
                    )
            );
        }
        Jasper.TYPE = "default";
        Jasper.TITLE = relatorios.getNome();
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) listObjectCheques);
        return null;
    }

    public Boolean getShow(String filtro) {
        try {
            for (Filters f : filters) {
                if (f.getKey().equals(filtro)) {
                    if (f.getActive()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void loadRelatorios() {
        listRelatorio = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        Integer default_id = 0;
        for (int i = 0; i < list.size(); i++) {
            Boolean disabled = false;
            if (i == 0) {
                idRelatorio = list.get(i).getId();
                default_id = list.get(i).getId();
            }
            if (list.get(i).getPrincipal()) {
                default_id = list.get(i).getId();
                idRelatorio = list.get(i).getId();
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome(), "", disabled));
        }
    }

    public void loadRelatoriosOrdem() {
        listRelatorioOrdem = new ArrayList();
        idRelatorioOrdem = 0;
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
                if (list.get(i).getPrincipal()) {
                    idRelatorioOrdem = list.get(i).getId();
                }
            }
        }
    }

    public void loadContaBanco() {
        listContaBanco = new LinkedHashMap<>();
        selectedContaBanco = new ArrayList();
        List<ContaBanco> list = new ContaBancoDao().findAllGroupByChequePag();
        for (int i = 0; i < list.size(); i++) {
            listContaBanco.put("BANCO: " + list.get(i).getBanco().getNumero() + " - AGÊNCIA: " + list.get(i).getAgencia() + " CONTA: " + list.get(i).getConta(), list.get(i).getId());
        }
    }

    public void loadPlano5() {
        listPlano5 = new LinkedHashMap<>();
        selectedPlano5 = new ArrayList();
        List<Plano5> list = new Plano5Dao().findAllGroupByChequePag();
        for (int i = 0; i < list.size(); i++) {
            listPlano5.put(list.get(i).getConta(), list.get(i).getId());
        }
    }

    // TRATAMENTO
    public String inIdPessoas() {
        String ids = null;
        if (listPessoa != null) {
            if (pessoa != null && pessoa.getId() != -1) {
                ids = "";
                ids = "" + pessoa.getId();
            }
            for (int i = 0; i < listPessoa.size(); i++) {
                if (listPessoa.get(i) != null) {
                    if (ids == null) {
                        ids = "";
                        ids = "" + listPessoa.get(i).getId();
                    } else {
                        ids += "," + listPessoa.get(i).getId();
                    }
                }
            }
        }
        return ids;
    }

    public String inIdPlano5() {
        String ids = null;
        if (selectedPlano5 != null) {
            ids = "";
            for (int i = 0; i < selectedPlano5.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedPlano5.get(i).toString();
                } else {
                    ids += "," + selectedPlano5.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdContaBanco() {
        String ids = null;
        if (selectedContaBanco != null) {
            ids = "";
            for (int i = 0; i < selectedContaBanco.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedContaBanco.get(i).toString();
                } else {
                    ids += "," + selectedContaBanco.get(i).toString();
                }
            }
        }
        return ids;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public List<Filters> getFilters() {
        return filters;
    }

    public void setFilters(List<Filters> filters) {
        this.filters = filters;
    }

    public Map<String, Integer> getListContaBanco() {
        return listContaBanco;
    }

    public void setListContaBanco(Map<String, Integer> listContaBanco) {
        this.listContaBanco = listContaBanco;
    }

    public List getSelectedContaBanco() {
        return selectedContaBanco;
    }

    public void setSelectedContaBanco(List selectedContaBanco) {
        this.selectedContaBanco = selectedContaBanco;
    }

    public Map<String, Integer> getListPlano5() {
        return listPlano5;
    }

    public void setListPlano5(Map<String, Integer> listPlano5) {
        this.listPlano5 = listPlano5;
    }

    public List getSelectedPlano5() {
        return selectedPlano5;
    }

    public void setSelectedPlano5(List selectedPlano5) {
        this.selectedPlano5 = selectedPlano5;
    }
//
//    public String getSelectedStatus() {
//        return selectedStatus;
//    }
//
//    public void setSelectedStatus(String selectedStatus) {
//        this.selectedStatus = selectedStatus;
//    }

    public void loadDates() {
        listDates = new ArrayList();
        listDates.add(new SelectItem("nao_impressos", "NÃO IMPRESSOS"));
        listDates.add(new SelectItem("impressos", "IMPRESSOS"));
        listDates.add(new SelectItem("cancelados", "CANCELADOS"));
        listDates.add(new SelectItem("emissao", "EMISSÃO"));
    }

    public List<SelectItem> getListDates() {
        return listDates;
    }

    public void setListDates(List<SelectItem> listDates) {
        this.listDates = listDates;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = ((Pessoa) GenericaSessao.getObject("pessoaPesquisa", true));
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getListPessoa() {
        return listPessoa;
    }

    public void setListPessoa(List<Pessoa> listPessoa) {
        this.listPessoa = listPessoa;
    }

    public class ObjectCheques {

        private Object emissao;
        private Object banco_numero;
        private Object banco;
        private Object conta;
        private Object agencia;
        private Object cheque;
        private Object cpf_cnpj;
        private Object nome;
        private Object impressao;
        private Object cancelamento;
        private Object historico;
        private Object valor;

        public ObjectCheques() {
            this.emissao = null;
            this.banco_numero = null;
            this.banco = null;
            this.conta = null;
            this.agencia = null;
            this.cheque = null;
            this.cpf_cnpj = null;
            this.nome = null;
            this.impressao = null;
            this.cancelamento = null;
            this.historico = null;
            this.valor = null;
        }

        public ObjectCheques(Object emissao, Object banco_numero, Object banco, Object conta, Object agencia, Object cheque, Object cpf_cnpj, Object nome, Object impressao, Object cancelamento, Object historico, Object valor) {
            this.emissao = emissao;
            this.banco_numero = banco_numero;
            this.banco = banco;
            this.conta = conta;
            this.agencia = agencia;
            this.cheque = cheque;
            this.cpf_cnpj = cpf_cnpj;
            this.nome = nome;
            this.impressao = impressao;
            this.cancelamento = cancelamento;
            this.historico = historico;
            this.valor = valor;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

        public Object getBanco_numero() {
            return banco_numero;
        }

        public void setBanco_numero(Object banco_numero) {
            this.banco_numero = banco_numero;
        }

        public Object getBanco() {
            return banco;
        }

        public void setBanco(Object banco) {
            this.banco = banco;
        }

        public Object getConta() {
            return conta;
        }

        public void setConta(Object conta) {
            this.conta = conta;
        }

        public Object getAgencia() {
            return agencia;
        }

        public void setAgencia(Object agencia) {
            this.agencia = agencia;
        }

        public Object getCheque() {
            return cheque;
        }

        public void setCheque(Object cheque) {
            this.cheque = cheque;
        }

        public Object getCpf_cnpj() {
            return cpf_cnpj;
        }

        public void setCpf_cnpj(Object cpf_cnpj) {
            this.cpf_cnpj = cpf_cnpj;
        }

        public Object getNome() {
            return nome;
        }

        public void setNome(Object nome) {
            this.nome = nome;
        }

        public Object getImpressao() {
            return impressao;
        }

        public void setImpressao(Object impressao) {
            this.impressao = impressao;
        }

        public Object getCancelamento() {
            return cancelamento;
        }

        public void setCancelamento(Object cancelamento) {
            this.cancelamento = cancelamento;
        }

        public Object getHistorico() {
            return historico;
        }

        public void setHistorico(Object historico) {
            this.historico = historico;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

    }

}
