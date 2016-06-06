package br.com.rtools.relatorios.beans;

import br.com.rtools.estoque.EstoqueTipo;
import br.com.rtools.estoque.ProdutoGrupo;
import br.com.rtools.estoque.ProdutoSubGrupo;
import br.com.rtools.estoque.ProdutoUnidade;
import br.com.rtools.estoque.dao.ProdutoSubGrupoDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioProdutosDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.Cor;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioProdutosBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private String tipoDataCadastro;
    private String dtCI;
    private String dtCF;

    private String startFinish;
    private String type;
    private String description;

    private String situacao;

    private Map<String, Integer> listFiliais;
    private List selectedFiliais;

    private Map<String, Integer> listTipo;
    private List selectedTipo;

    private Map<String, Integer> listGrupo;
    private List selectedGrupo;

    private Map<String, Integer> listSubGrupo;
    private List selectedSubGrupo;

    private Map<String, Integer> listUnidade;
    private List selectedUnidade;

    private Map<String, Integer> listCor;
    private List selectedCor;

    private String estoqueSituacao;

    @PostConstruct
    public void init() {
        new Jasper().init();
        situacao = "A";
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        tipoDataCadastro = "todos";
        dtCI = "";
        dtCF = "";
        startFinish = "I";
        type = "";
        description = "";
        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
        estoqueSituacao = "";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioProdutosBean");
        GenericaSessao.remove("pessoaPesquisa");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        List listDetalhePesquisa = new ArrayList();
        List<ObjectJasper> oj = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioProdutosDao rpd = new RelatorioProdutosDao();
        if (idRelatorioOrdem != null) {
            RelatorioOrdem ro = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem);
            rpd.setRelatorioOrdem(ro);
        }
        if (!dtCI.isEmpty()) {
            listDetalhePesquisa.add("Período de vencimento " + dtCI + (!dtCF.isEmpty() ? " até ".concat(dtCF) : ""));
        }
        rpd.setRelatorios(r);
        String in_filiais = inIdFiliais();
        String in_tipos = inIdTipos();
        String in_grupos = inIdGrupos();
        String in_subgrupos = inIdSubGrupos();
        String in_cores = null;
        String in_unidades = inIdUnidades();
        if (in_filiais != null) {
            if (selectedFiliais != null) {
                String fs = "";
                for (int i = 0; i < selectedFiliais.size(); i++) {
                    Filial f = (Filial) new Dao().find(new Filial(), new Integer(selectedFiliais.get(i).toString()));
                    fs += f.getFilial().getPessoa().getNome() + "; ";
                }
                listDetalhePesquisa.add("Filial(s): " + fs);
            }
        }
        List list = rpd.find(startFinish, type, description, situacao, in_filiais, in_tipos, in_grupos, in_subgrupos, in_cores, in_unidades, tipoDataCadastro, dtCI, dtCF, estoqueSituacao);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8)));
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        String detalheRelatorio = "";
        if (listDetalhePesquisa.isEmpty()) {
            detalheRelatorio += "Pesquisar todos registros!";
        } else {
            detalheRelatorio += "";
            for (int i = 0; i < listDetalhePesquisa.size(); i++) {
                if (i == 0) {
                    detalheRelatorio += "Detalhes: " + listDetalhePesquisa.get(i).toString();
                } else {
                    detalheRelatorio += "; " + listDetalhePesquisa.get(i).toString();
                }
            }
        }
        Jasper.EXPORT_TO = true;
        Jasper.TITLE = "RELATÓRIO " + r.getNome().toUpperCase();
        Jasper.TYPE = "default";
        Map map = new HashMap();
        map.put("detalhes_relatorio", detalheRelatorio);
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) oj, map);
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
    }

    // LOAD
    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
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
            loadRelatorioOrdem();
        }
    }

    public void loadRelatorioOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
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

    public void load() {
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("cadastro", "Cadastro", false, false));
        listFilters.add(new Filters("situacao", "Situação", false, false));
        listFilters.add(new Filters("filial", "Filiais", false, false));
        listFilters.add(new Filters("tipo", "Tipo", false, false));
        listFilters.add(new Filters("grupo", "Grupo", false, false));
        listFilters.add(new Filters("unidade", "Unidade", false, false));
        listFilters.add(new Filters("cor", "Cores", false, false));
        listFilters.add(new Filters("pesquisa", "Pesquisa", false, false));
        listFilters.add(new Filters("estoque", "Estoque", false, false));

    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadFilters();
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "cadastro":
                if (!filter.getActive()) {
                    tipoDataCadastro = "todos";
                    dtCI = "";
                    dtCF = "";
                }
                break;
            case "situacao":
                if (!filter.getActive()) {
                    situacao = "";
                } else {
                    situacao = "A";
                }
                break;
            case "filial":
                if (!filter.getActive()) {
                    listFiliais = new LinkedHashMap<>();
                    selectedFiliais = new ArrayList<>();
                } else {
                    loadListFiliais();
                }
                break;
            case "tipo":
                if (!filter.getActive()) {
                    listTipo = new LinkedHashMap<>();
                    selectedTipo = new ArrayList<>();
                } else {
                    loadListTipos();
                }
                break;
            case "grupo":
                if (!filter.getActive()) {
                    listGrupo = new LinkedHashMap<>();
                    selectedGrupo = new ArrayList<>();
                    listSubGrupo = new LinkedHashMap<>();
                    selectedSubGrupo = new ArrayList<>();
                } else {
                    loadListGrupos();
                    loadListSubGrupos();
                }
                break;
            case "cor":
                if (!filter.getActive()) {
                    listCor = new LinkedHashMap<>();
                    selectedCor = new ArrayList<>();
                } else {
                    loadListCores();
                }
                break;
            case "unidade":
                if (!filter.getActive()) {
                    listUnidade = new LinkedHashMap<>();
                    selectedUnidade = new ArrayList<>();
                } else {
                    loadListUnidades();
                }
                break;
            case "pesquisa":
                if (!filter.getActive()) {
                    startFinish = "";
                    type = "descricao";
                    description = "";
                }
                break;
            case "estoque":
                if (!filter.getActive()) {
                    estoqueSituacao = "";
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void loadListFiliais() {
        listFiliais = new LinkedHashMap<>();
        selectedFiliais = new ArrayList<>();
        List<Filial> list = new Dao().list(new Filial(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listFiliais.put(list.get(i).getFilial().getPessoa().getNome(), list.get(i).getId());
            }
        }
    }

    public void loadListTipos() {
        listTipo = new LinkedHashMap<>();
        selectedTipo = new ArrayList<>();
        List<EstoqueTipo> list = new Dao().list(new EstoqueTipo(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listTipo.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListGrupos() {
        listGrupo = new LinkedHashMap<>();
        selectedGrupo = new ArrayList<>();
        List<ProdutoGrupo> list = new Dao().list(new ProdutoGrupo(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listGrupo.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListSubGrupos() {
        listSubGrupo = new LinkedHashMap<>();
        selectedSubGrupo = new ArrayList<>();
        String in_grupos = inIdGrupos();
        if (in_grupos != null) {
            List<ProdutoSubGrupo> list = new ProdutoSubGrupoDao().findByGrupo(in_grupos);
            for (int i = 0; i < list.size(); i++) {
                listSubGrupo.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListUnidades() {
        listUnidade = new LinkedHashMap<>();
        selectedUnidade = new ArrayList<>();
        List<ProdutoUnidade> list = new Dao().list(new ProdutoUnidade(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listUnidade.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListCores() {
        listCor = new LinkedHashMap<>();
        selectedCor = new ArrayList<>();
        List<Cor> list = new Dao().list(new Cor(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listCor.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    // TRATAMENTO
    public String inIdFiliais() {
        String ids = null;
        if (selectedFiliais != null) {
            for (int i = 0; i < selectedFiliais.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedFiliais.get(i);
                } else {
                    ids += "," + selectedFiliais.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdCores() {
        String ids = null;
        if (selectedCor != null) {
            for (int i = 0; i < selectedCor.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedCor.get(i);
                } else {
                    ids += "," + selectedCor.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdTipos() {
        String ids = null;
        if (selectedTipo != null) {
            for (int i = 0; i < selectedTipo.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedTipo.get(i);
                } else {
                    ids += "," + selectedTipo.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdGrupos() {
        String ids = null;
        if (selectedGrupo != null) {
            for (int i = 0; i < selectedGrupo.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedGrupo.get(i);
                } else {
                    ids += "," + selectedGrupo.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdSubGrupos() {
        String ids = null;
        if (selectedSubGrupo != null) {
            for (int i = 0; i < selectedSubGrupo.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedSubGrupo.get(i);
                } else {
                    ids += "," + selectedSubGrupo.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdUnidades() {
        String ids = null;
        if (selectedUnidade != null) {
            for (int i = 0; i < selectedUnidade.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedUnidade.get(i);
                } else {
                    ids += "," + selectedUnidade.get(i);
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

    /**
     * 0 grupo finançeiro; 1 subgrupo finançeiro; 2 serviços; 3 sócios; 4 tipo
     * de pessoa; 5 meses débito
     *
     * @return
     */
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

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public Map<String, Integer> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(Map<String, Integer> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public List getSelectedFiliais() {
        return selectedFiliais;
    }

    public void setSelectedFiliais(List selectedFiliais) {
        this.selectedFiliais = selectedFiliais;
    }

    public String getStartFinish() {
        return startFinish;
    }

    public void setStartFinish(String startFinish) {
        this.startFinish = startFinish;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getTipoDataCadastro() {
        return tipoDataCadastro;
    }

    public void setTipoDataCadastro(String tipoDataCadastro) {
        this.tipoDataCadastro = tipoDataCadastro;
    }

    public String getDtCI() {
        return dtCI;
    }

    public void setDtCI(String dtCI) {
        this.dtCI = dtCI;
    }

    public String getDtCF() {
        return dtCF;
    }

    public void setDtCF(String dtCF) {
        this.dtCF = dtCF;
    }

    public Map<String, Integer> getListTipo() {
        return listTipo;
    }

    public void setListTipo(Map<String, Integer> listTipo) {
        this.listTipo = listTipo;
    }

    public List getSelectedTipo() {
        return selectedTipo;
    }

    public void setSelectedTipo(List selectedTipo) {
        this.selectedTipo = selectedTipo;
    }

    public Map<String, Integer> getListGrupo() {
        return listGrupo;
    }

    public void setListGrupo(Map<String, Integer> listGrupo) {
        this.listGrupo = listGrupo;
    }

    public List getSelectedGrupo() {
        return selectedGrupo;
    }

    public void setSelectedGrupo(List selectedGrupo) {
        this.selectedGrupo = selectedGrupo;
    }

    public Map<String, Integer> getListSubGrupo() {
        return listSubGrupo;
    }

    public void setListSubGrupo(Map<String, Integer> listSubGrupo) {
        this.listSubGrupo = listSubGrupo;
    }

    public List getSelectedSubGrupo() {
        return selectedSubGrupo;
    }

    public void setSelectedSubGrupo(List selectedSubGrupo) {
        this.selectedSubGrupo = selectedSubGrupo;
    }

    public Map<String, Integer> getListUnidade() {
        return listUnidade;
    }

    public void setListUnidade(Map<String, Integer> listUnidade) {
        this.listUnidade = listUnidade;
    }

    public List getSelectedUnidade() {
        return selectedUnidade;
    }

    public void setSelectedUnidade(List selectedUnidade) {
        this.selectedUnidade = selectedUnidade;
    }

    public Map<String, Integer> getListCor() {
        return listCor;
    }

    public void setListCor(Map<String, Integer> listCor) {
        this.listCor = listCor;
    }

    public List getSelectedCor() {
        return selectedCor;
    }

    public void setSelectedCor(List selectedCor) {
        this.selectedCor = selectedCor;
    }

    public String getEstoqueSituacao() {
        return estoqueSituacao;
    }

    public void setEstoqueSituacao(String estoqueSituacao) {
        this.estoqueSituacao = estoqueSituacao;
    }

    public class ObjectJasper {

        private Object grupo;
        private Object subgrupo;
        private Object produto;
        private Object filial;
        private Object valor;
        private Object estoque;
        private Object estoque_minimo;
        private Object estoque_maximo;
        private Object custo_medio;

        public ObjectJasper() {
            this.grupo = null;
            this.subgrupo = null;
            this.produto = null;
            this.filial = null;
            this.valor = null;
            this.estoque = null;
            this.estoque_minimo = null;
            this.estoque_maximo = null;
            this.custo_medio = null;
        }

        public ObjectJasper(Object grupo, Object subgrupo, Object produto, Object valor, Object filial, Object estoque, Object estoque_minimo, Object estoque_maximo, Object custo_medio) {
            this.grupo = grupo;
            this.subgrupo = subgrupo;
            this.produto = produto;
            this.filial = filial;
            this.valor = valor;
            this.estoque = estoque;
            this.estoque_minimo = estoque_minimo;
            this.estoque_maximo = estoque_maximo;
            this.custo_medio = custo_medio;
        }

        public Object getGrupo() {
            return grupo;
        }

        public void setGrupo(Object grupo) {
            this.grupo = grupo;
        }

        public Object getSubgrupo() {
            return subgrupo;
        }

        public void setSubgrupo(Object subgrupo) {
            this.subgrupo = subgrupo;
        }

        public Object getProduto() {
            return produto;
        }

        public void setProduto(Object produto) {
            this.produto = produto;
        }

        public Object getFilial() {
            return filial;
        }

        public void setFilial(Object filial) {
            this.filial = filial;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getEstoque() {
            return estoque;
        }

        public void setEstoque(Object estoque) {
            this.estoque = estoque;
        }

        public Object getEstoque_minimo() {
            return estoque_minimo;
        }

        public void setEstoque_minimo(Object estoque_minimo) {
            this.estoque_minimo = estoque_minimo;
        }

        public Object getEstoque_maximo() {
            return estoque_maximo;
        }

        public void setEstoque_maximo(Object estoque_maximo) {
            this.estoque_maximo = estoque_maximo;
        }

        public Object getCusto_medio() {
            return custo_medio;
        }

        public void setCusto_medio(Object custo_medio) {
            this.custo_medio = custo_medio;
        }

    }

}
