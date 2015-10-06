package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCobrancaExternaDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioCobrancaExternaBean implements Serializable {

    private List<Filters> listFilters;

    private Map<String, Integer> listServicos;
    private List selectedServicos;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private Map<String, Integer> listTipoCobranca;
    private List selectedTipoCobranca;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listServicos = null;
        selectedServicos = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        listTipoCobranca = null;
        selectedTipoCobranca = new ArrayList<>();

        listRelatorioOrdem = new ArrayList<>();
        idRelatorioOrdem = null;

        loadListaFiltro();
        loadRelatorio();
        loadRelatorioOrdem();
        loadTipoCobranca();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCobrancaExternaBean");
    }

    public void print() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        if (r == null) {
            return;
        }
        String order = "";
        String detalheRelatorio = "";
        List<RelatorioCobrancaExterna> rces = new ArrayList<>();
        List list = new RelatorioCobrancaExternaDao().find(r.getId(), inIdTipoCobranca());
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            rces.add(
                    new RelatorioCobrancaExterna(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6))
            );
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.TITLE = r.getNome();
        Jasper.TYPE = "default";
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) rces);
    }

    // LOAD
    public void loadRelatorio() {
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    idRelatorio = list.get(i).getId();
                }
                listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
            loadRelatorioOrdem();
        }
    }

    public void loadRelatorioOrdem() {
        if (idRelatorio != null) {
            listRelatorioOrdem.clear();
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadTipoCobranca() {
        listTipoCobranca = null;
        selectedTipoCobranca = new ArrayList();
        listTipoCobranca = new HashMap<>();
        List<FTipoDocumento> list = new Dao().find("FTipoDocumento", new int[]{13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23});
        listTipoCobranca.put("Selecionar", null);
        if (list != null) {
            for (FTipoDocumento list1 : list) {
                listTipoCobranca.put(list1.getDescricao(), list1.getId());
            }
        }
    }

    public void loadListaFiltro() {
        listFilters.clear();
        listFilters.add(new Filters("tipo_cobranca", "Tipo de Cobrança", false));

    }

    // LISTENERS
    public void clear(Filters filter) {
        filter.setActive(!filter.getActive());
        switch (filter.getKey()) {
            case "tipo_cobranca":
                loadTipoCobranca();
                break;
        }
    }

    public void close(Filters filter) {
        if (!filter.getActive()) {
            switch (filter.getKey()) {
                case "tipo_cobranca":
                    loadTipoCobranca();
                    break;
            }
        }
    }

    // TRATAMENTO
    public String inIdTipoCobranca() {
        String ids = null;
        if (selectedTipoCobranca != null) {
            for (int i = 0; i < selectedTipoCobranca.size(); i++) {
                if (selectedTipoCobranca.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedTipoCobranca.get(i);
                    } else {
                        ids += "," + selectedTipoCobranca.get(i);
                    }
                }
            }
        }
        return ids;
    }

    // GETTERS AND SETTERS
    public List<SelectItem> getListRelatorios() {
        return listRelatorio;
    }

    public void setListRelatorios(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public Map<String, Integer> getListServicos() {
        return listServicos;
    }

    public void setListServicos(Map<String, Integer> listServicos) {
        this.listServicos = listServicos;
    }

    public List getSelectedServicos() {
        return selectedServicos;
    }

    public void setSelectedServicos(List selectedServicos) {
        this.selectedServicos = selectedServicos;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Map<String, Integer> getListTipoCobranca() {
        return listTipoCobranca;
    }

    public void setListTipoCobranca(Map<String, Integer> listTipoCobranca) {
        this.listTipoCobranca = listTipoCobranca;
    }

    public List getSelectedTipoCobranca() {
        return selectedTipoCobranca;
    }

    public void setSelectedTipoCobranca(List selectedTipoCobranca) {
        this.selectedTipoCobranca = selectedTipoCobranca;
    }

    public class RelatorioCobrancaExterna {

        private Object tipo_cobranca_descricao;
        private Object socio_codigo;
        private Object socio_nome;
        private Object mes;
        private Object ano;
        private Object valor;
        private Object endereco_cobranca;

        public RelatorioCobrancaExterna() {
            this.tipo_cobranca_descricao = null;
            this.socio_codigo = null;
            this.socio_nome = null;
            this.mes = null;
            this.ano = null;
            this.valor = null;
            this.endereco_cobranca = null;
        }

        /**
         *
         * @param tipo_cobranca_descricao
         * @param socio_codigo
         * @param socio_nome
         * @param mes
         * @param ano
         * @param valor
         * @param endereco_cobranca
         */
        public RelatorioCobrancaExterna(Object tipo_cobranca_descricao, Object socio_codigo, Object socio_nome, Object mes, Object ano, Object valor, Object endereco_cobranca) {
            this.tipo_cobranca_descricao = tipo_cobranca_descricao;
            this.socio_codigo = socio_codigo;
            this.socio_nome = socio_nome;
            this.mes = mes;
            this.ano = ano;
            this.valor = valor;
            this.endereco_cobranca = endereco_cobranca;
        }

        public Object getTipo_cobranca_descricao() {
            return tipo_cobranca_descricao;
        }

        public void setTipo_cobranca_descricao(Object tipo_cobranca_descricao) {
            this.tipo_cobranca_descricao = tipo_cobranca_descricao;
        }

        public Object getSocio_codigo() {
            return socio_codigo;
        }

        public void setSocio_codigo(Object socio_codigo) {
            this.socio_codigo = socio_codigo;
        }

        public Object getSocio_nome() {
            return socio_nome;
        }

        public void setSocio_nome(Object socio_nome) {
            this.socio_nome = socio_nome;
        }

        public Object getMes() {
            return mes;
        }

        public void setMes(Object mes) {
            this.mes = mes;
        }

        public Object getAno() {
            return ano;
        }

        public void setAno(Object ano) {
            this.ano = ano;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getEndereco_cobranca() {
            return endereco_cobranca;
        }

        public void setEndereco_cobranca(Object endereco_cobranca) {
            this.endereco_cobranca = endereco_cobranca;
        }

    }
}
