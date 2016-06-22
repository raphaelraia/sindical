package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.GrupoFinanceiro;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.SubGrupoFinanceiro;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCobrancaDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GenericaString;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class RelatorioCobrancaBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private Map<String, Integer> listServicos;
    private List selectedServicos;

    private Map<String, Integer> listTipoServico;
    private List selectedTipoServico;

    private Map<String, Integer> listGrupoFinanceiro;
    private List selectedGrupoFinanceiro;

    private Map<String, Integer> listSubGrupoFinanceiro;
    private List selectedSubGrupoFinanceiro;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private String tipoSocio;
    private String tipoPessoa;
    private String tipoMesesDebito;
    private String tipoMesesDebitoData;
    private String monthS;
    private String monthF;
    private String dtS;
    private String dtF;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listServicos = null;
        selectedServicos = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        tipoSocio = "socios";
        tipoPessoa = "todas";
        tipoMesesDebito = "todos";
        tipoMesesDebitoData = "todos";

        monthS = "0";
        monthF = "0";

        dtS = "";
        dtF = "";

        listServicos = new LinkedHashMap<>();
        selectedServicos = new ArrayList();

        listTipoServico = new LinkedHashMap<>();
        selectedTipoServico = new ArrayList();

        listGrupoFinanceiro = new LinkedHashMap<>();
        selectedGrupoFinanceiro = new ArrayList();

        listSubGrupoFinanceiro = new LinkedHashMap<>();
        selectedSubGrupoFinanceiro = new ArrayList();

        loadFilters();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCobrancaBean");
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
        String order = "";
        Integer titular_id = null;
        Map map = new HashMap();
        FacesContext faces = FacesContext.getCurrentInstance();
        Pessoa p = (Pessoa) new Dao().find(new Pessoa(), 1);
        map.put("sindicato_nome", p.getNome());
        map.put("sindicato_documento", p.getDocumento());
        map.put("sindicato_site", p.getSite());
        map.put("sindicato_logradouro", p.getPessoaEndereco().getEndereco().getLogradouro().getDescricao());
        map.put("sindicato_endereco", p.getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao());
        map.put("sindicato_numero", p.getPessoaEndereco().getNumero());
        map.put("sindicato_complemento", p.getPessoaEndereco().getComplemento());
        map.put("sindicato_bairro", p.getPessoaEndereco().getEndereco().getBairro().getDescricao());
        map.put("sindicato_cidade", p.getPessoaEndereco().getEndereco().getCidade().getCidade());
        map.put("sindicato_uf", p.getPessoaEndereco().getEndereco().getCidade().getUf());
        map.put("sindicato_cep", p.getPessoaEndereco().getEndereco().getCep());
        map.put("sindicato_telefone", p.getTelefone1());
        map.put("sindicato_logo", ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
        map.put("sindicato_email", p.getEmail1());
        String detalheRelatorio = "";
        List<Cobranca> cs = new ArrayList<>();
        List<Etiquetas> e = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioCobrancaDao rcd = new RelatorioCobrancaDao();
        rcd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));
        rcd.setRelatorios(r);
        List list = rcd.find(inIdGrupoFinanceiro(), inIdSubGrupoFinanceiro(), inIdServicos(), inIdTipoServico(), tipoSocio, tipoPessoa, tipoMesesDebito, monthS, monthF, tipoMesesDebitoData, dtS, dtF);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (tags) {
                try {
                    e.add(
                            new Etiquetas(
                                    GenericaString.converterNullToString(o.get(1)), // Nome
                                    GenericaString.converterNullToString(o.get(10)), // Logradouro
                                    GenericaString.converterNullToString(o.get(11)), // Endereço
                                    GenericaString.converterNullToString(o.get(13)), // Número
                                    GenericaString.converterNullToString(o.get(12)), // Bairro
                                    GenericaString.converterNullToString(o.get(4)), // Cidade
                                    GenericaString.converterNullToString(o.get(5)), // UF
                                    GenericaString.converterNullToString(o.get(15)), // Cep
                                    GenericaString.converterNullToString(o.get(14)) // Complemento
                            )
                    );
                } catch (Exception ex) {
                }
            } else {
                cs.add(
                        new Cobranca(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9), o.get(10), o.get(11), o.get(12), o.get(13), o.get(14), o.get(15), o.get(16))
                );
            }
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        if (tags) {
            Jasper.EXPORT_TO = true;
            Jasper.EXPORT_TYPE = "pdf";
            Jasper.printReports("/Relatorios/ETIQUETAS.jasper", "etiquetas", (Collection) e);
        } else {
            Jasper.EXPORT_TO = true;
            Jasper.TITLE = r.getNome();
            Jasper.TYPE = "default";
            Jasper.printReports(r.getJasper(), r.getNome(), (Collection) cs, map);
        }
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
        listFilters.add(new Filters("grupo_financeiro", "Grupo Financeiro", false, false));
        listFilters.add(new Filters("subgrupo_financeiro", "Subgrupo Financeiro", false, true));
        listFilters.add(new Filters("servicos", "Serviços", false, false));
        listFilters.add(new Filters("socios", "Sócios", false, false));
        listFilters.add(new Filters("tipo_pessoa", "Tipo de pessoa", false, false));
        listFilters.add(new Filters("meses_debito", "Meses em Débito", false, false));
        listFilters.add(new Filters("meses_debito_data", "Meses em Débito por Data", false, false));

    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadSubGrupoFinanceiro();
                loadServicos();
                break;
            case 2:
                loadServicos();
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "grupo_financeiro":
                if (filter.getActive()) {
                    loadGrupoFinanceiro();
                    loadSubGrupoFinanceiro();
                    loadServicos();
                } else {
                    listGrupoFinanceiro = new LinkedHashMap<>();
                    listSubGrupoFinanceiro = new LinkedHashMap<>();
                    selectedSubGrupoFinanceiro = new ArrayList<>();
                    selectedGrupoFinanceiro = new ArrayList<>();
                    loadSubGrupoFinanceiro();
                    loadServicos();
                }
                break;
            case "subgrupo_financeiro":
                break;
            case "servicos":
                if (filter.getActive()) {
                    loadServicos();
                    loadTipoServico();
                } else {
                    listServicos = new LinkedHashMap<>();
                    selectedServicos = new ArrayList<>();
                    listTipoServico = new LinkedHashMap<>();
                    selectedTipoServico = new ArrayList<>();
                }
                break;
            case "tipo_servico":
                break;
            case "socios":
                if (filter.getActive()) {

                } else {
                    tipoSocio = "socios";
                }
                break;
            case "tipo_pessoa":
                if (filter.getActive()) {

                } else {
                    tipoPessoa = "todas";
                }
                break;
            case "meses_debito":
                if (filter.getActive()) {

                } else {
                    tipoMesesDebito = "todos";
                    monthS = "0";
                    monthF = "0";
                }
                break;
            case "meses_debito_data":
                if (filter.getActive()) {

                } else {
                    tipoMesesDebitoData = "todos";
                    dtS = "";
                    dtF = "";
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
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
     * de pessoa; 5 meses débito; 6 Tipo de Serviço;
     *
     * @return
     */
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

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public void loadGrupoFinanceiro() {
        listGrupoFinanceiro = new LinkedHashMap<>();
        selectedGrupoFinanceiro = new ArrayList<>();
        listSubGrupoFinanceiro = new HashMap<>();
        selectedSubGrupoFinanceiro = new ArrayList();
        listServicos = new HashMap<>();
        selectedServicos = new ArrayList<>();
        List<GrupoFinanceiro> list = new Dao().list(new GrupoFinanceiro(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listGrupoFinanceiro.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadSubGrupoFinanceiro() {
        listSubGrupoFinanceiro = new LinkedHashMap<>();
        selectedSubGrupoFinanceiro = new ArrayList();
        loadServicos();
        if (inIdGrupoFinanceiro() != null && !inIdGrupoFinanceiro().isEmpty()) {
            listSubGrupoFinanceiro = new HashMap<>();
            FinanceiroDao fd = new FinanceiroDao();
            List<SubGrupoFinanceiro> list = fd.listaSubGrupo(inIdGrupoFinanceiro());
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    listSubGrupoFinanceiro.put(list.get(i).getDescricao(), list.get(i).getId());
                }
            }
        }
    }

    public void loadServicos() {
        listServicos = new LinkedHashMap<>();
        selectedServicos = new ArrayList<>();
        List<Servicos> list;
        ServicosDao servicosDao = new ServicosDao();
        if (!selectedSubGrupoFinanceiro.isEmpty()) {
            servicosDao.setSituacao("A");
            list = new ServicosDao().findBySubGrupoFinanceiro(inIdSubGrupoFinanceiro());
        } else if (!selectedGrupoFinanceiro.isEmpty()) {
            servicosDao.setSituacao("A");
            list = new ServicosDao().findByGrupoFinanceiro(inIdGrupoFinanceiro());
        } else {
            list = new Dao().list(new Servicos(), true);
        }
        for (int i = 0; i < list.size(); i++) {
            listServicos.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadTipoServico() {
        listTipoServico = new LinkedHashMap<>();
        selectedTipoServico = new ArrayList<>();
        List<TipoServico> list = new Dao().list(new TipoServico(), true);
        for (int i = 0; i < list.size(); i++) {
            listTipoServico.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    // TRATAMENTO
    public String inIdSubGrupoFinanceiro() {
        String ids = null;
        if (selectedSubGrupoFinanceiro != null) {
            for (int i = 0; i < selectedSubGrupoFinanceiro.size(); i++) {
                if (selectedSubGrupoFinanceiro.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedSubGrupoFinanceiro.get(i);
                    } else {
                        ids += "," + selectedSubGrupoFinanceiro.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdGrupoFinanceiro() {
        String ids = null;
        if (selectedGrupoFinanceiro != null) {
            for (int i = 0; i < selectedGrupoFinanceiro.size(); i++) {
                if (selectedGrupoFinanceiro.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedGrupoFinanceiro.get(i);
                    } else {
                        ids += "," + selectedGrupoFinanceiro.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdServicos() {
        String ids = null;
        if (selectedServicos != null) {
            for (int i = 0; i < selectedServicos.size(); i++) {
                if (selectedServicos.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedServicos.get(i);
                    } else {
                        ids += "," + selectedServicos.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdTipoServico() {
        String ids = null;
        if (selectedTipoServico != null) {
            for (int i = 0; i < selectedTipoServico.size(); i++) {
                if (selectedTipoServico.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedTipoServico.get(i);
                    } else {
                        ids += "," + selectedTipoServico.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String getTipoSocio() {
        return tipoSocio;
    }

    public void setTipoSocio(String tipoSocio) {
        this.tipoSocio = tipoSocio;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getTipoMesesDebito() {
        return tipoMesesDebito;
    }

    public void setTipoMesesDebito(String tipoMesesDebito) {
        this.tipoMesesDebito = tipoMesesDebito;
    }

    public String getTipoMesesDebitoData() {
        return tipoMesesDebitoData;
    }

    public void setTipoMesesDebitoData(String tipoMesesDebitoData) {
        this.tipoMesesDebitoData = tipoMesesDebitoData;
    }

    public Map<String, Integer> getListGrupoFinanceiro() {
        return listGrupoFinanceiro;
    }

    public void setListGrupoFinanceiro(Map<String, Integer> listGrupoFinanceiro) {
        this.listGrupoFinanceiro = listGrupoFinanceiro;
    }

    public List getSelectedGrupoFinanceiro() {
        return selectedGrupoFinanceiro;
    }

    public void setSelectedGrupoFinanceiro(List selectedGrupoFinanceiro) {
        this.selectedGrupoFinanceiro = selectedGrupoFinanceiro;
    }

    public Map<String, Integer> getListSubGrupoFinanceiro() {
        return listSubGrupoFinanceiro;
    }

    public void setListSubGrupoFinanceiro(Map<String, Integer> listSubGrupoFinanceiro) {
        this.listSubGrupoFinanceiro = listSubGrupoFinanceiro;
    }

    public List getSelectedSubGrupoFinanceiro() {
        return selectedSubGrupoFinanceiro;
    }

    public void setSelectedSubGrupoFinanceiro(List selectedSubGrupoFinanceiro) {
        this.selectedSubGrupoFinanceiro = selectedSubGrupoFinanceiro;
    }

    public String getMonthS() {
        return monthS;
    }

    public void setMonthS(String monthS) {
        this.monthS = monthS;
    }

    public String getMonthF() {
        return monthF;
    }

    public void setMonthF(String monthF) {
        this.monthF = monthF;
    }

    public String getDtS() {
        return dtS;
    }

    public void setDtS(String dtS) {
        this.dtS = dtS;
    }

    public String getDtF() {
        return dtF;
    }

    public void setDtF(String dtF) {
        this.dtF = dtF;
    }

    public Map<String, Integer> getListTipoServico() {
        return listTipoServico;
    }

    public void setListTipoServico(Map<String, Integer> listTipoServico) {
        this.listTipoServico = listTipoServico;
    }

    public List getSelectedTipoServico() {
        return selectedTipoServico;
    }

    public void setSelectedTipoServico(List selectedTipoServico) {
        this.selectedTipoServico = selectedTipoServico;
    }

    public class Cobranca {

        private Object pessoa_id;
        private Object pessoa_nome;
        private Object pessoa_documento;
        private Object pessoa_cidade;
        private Object pessoa_uf;
        private Object pessoa_telefone;
        private Object pessoa_telefone2;
        private Object pessoa_telefone3;
        private Object valor;
        private Object meses;
        private Object pessoa_logradouro;
        private Object pessoa_descricao_endereco;
        private Object pessoa_bairro;
        private Object pessoa_numero;
        private Object pessoa_complemento;
        private Object pessoa_cep;
        private Object categoria;

        /**
         * *
         *
         * @param pessoa_id
         * @param pessoa_nome
         * @param pessoa_documento
         * @param pessoa_cidade
         * @param pessoa_uf
         * @param pessoa_telefone
         * @param pessoa_telefone2
         * @param pessoa_telefone3
         * @param valor
         * @param meses
         * @param pessoa_logradouro
         * @param pessoa_descricao_endereco
         * @param pessoa_bairro
         * @param pessoa_numero
         * @param pessoa_complemento
         * @param pessoa_cep
         * @param categoria
         */
        public Cobranca(Object pessoa_id, Object pessoa_nome, Object pessoa_documento, Object pessoa_cidade, Object pessoa_uf, Object pessoa_telefone, Object pessoa_telefone2, Object pessoa_telefone3, Object valor, Object meses, Object pessoa_logradouro, Object pessoa_descricao_endereco, Object pessoa_bairro, Object pessoa_numero, Object pessoa_complemento, Object pessoa_cep, Object categoria) {
            this.pessoa_id = pessoa_id;
            this.pessoa_nome = pessoa_nome;
            this.pessoa_documento = pessoa_documento;
            this.pessoa_cidade = pessoa_cidade;
            this.pessoa_uf = pessoa_uf;
            this.pessoa_telefone = pessoa_telefone;
            this.pessoa_telefone2 = pessoa_telefone2;
            this.pessoa_telefone3 = pessoa_telefone3;
            this.valor = valor;
            this.meses = meses;
            this.pessoa_logradouro = pessoa_logradouro;
            this.pessoa_descricao_endereco = pessoa_descricao_endereco;
            this.pessoa_bairro = pessoa_bairro;
            this.pessoa_numero = pessoa_numero;
            this.pessoa_complemento = pessoa_complemento;
            this.pessoa_cep = pessoa_cep;
            this.categoria = categoria;
        }

        public Object getPessoa_id() {
            return pessoa_id;
        }

        public void setPessoa_id(Object pessoa_id) {
            this.pessoa_id = pessoa_id;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getPessoa_documento() {
            return pessoa_documento;
        }

        public void setPessoa_documento(Object pessoa_documento) {
            this.pessoa_documento = pessoa_documento;
        }

        public Object getPessoa_cidade() {
            return pessoa_cidade;
        }

        public void setPessoa_cidade(Object pessoa_cidade) {
            this.pessoa_cidade = pessoa_cidade;
        }

        public Object getPessoa_uf() {
            return pessoa_uf;
        }

        public void setPessoa_uf(Object pessoa_uf) {
            this.pessoa_uf = pessoa_uf;
        }

        public Object getPessoa_telefone() {
            return pessoa_telefone;
        }

        public void setPessoa_telefone(Object pessoa_telefone) {
            this.pessoa_telefone = pessoa_telefone;
        }

        public Object getPessoa_telefone2() {
            return pessoa_telefone2;
        }

        public void setPessoa_telefone2(Object pessoa_telefone2) {
            this.pessoa_telefone2 = pessoa_telefone2;
        }

        public Object getPessoa_telefone3() {
            return pessoa_telefone3;
        }

        public void setPessoa_telefone3(Object pessoa_telefone3) {
            this.pessoa_telefone3 = pessoa_telefone3;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getMeses() {
            return meses;
        }

        public void setMeses(Object meses) {
            this.meses = meses;
        }

        public Object getPessoa_logradouro() {
            return pessoa_logradouro;
        }

        public void setPessoa_logradouro(Object pessoa_logradouro) {
            this.pessoa_logradouro = pessoa_logradouro;
        }

        public Object getPessoa_descricao_endereco() {
            return pessoa_descricao_endereco;
        }

        public void setPessoa_descricao_endereco(Object pessoa_descricao_endereco) {
            this.pessoa_descricao_endereco = pessoa_descricao_endereco;
        }

        public Object getPessoa_bairro() {
            return pessoa_bairro;
        }

        public void setPessoa_bairro(Object pessoa_bairro) {
            this.pessoa_bairro = pessoa_bairro;
        }

        public Object getPessoa_numero() {
            return pessoa_numero;
        }

        public void setPessoa_numero(Object pessoa_numero) {
            this.pessoa_numero = pessoa_numero;
        }

        public Object getPessoa_complemento() {
            return pessoa_complemento;
        }

        public void setPessoa_complemento(Object pessoa_complemento) {
            this.pessoa_complemento = pessoa_complemento;
        }

        public Object getPessoa_cep() {
            return pessoa_cep;
        }

        public void setPessoa_cep(Object pessoa_cep) {
            this.pessoa_cep = pessoa_cep;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

    }
}
