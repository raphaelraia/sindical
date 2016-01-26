package br.com.rtools.relatorios.beans;

import br.com.rtools.cobranca.TmktContato;
import br.com.rtools.cobranca.TmktNatureza;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioTelemarketingDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class RelatorioTelemarketingBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private Map<String, Integer> listOperador;
    private List selectedOperador;

    private Map<String, Integer> listTmktContato;
    private List selectedTmktContato;

    private Map<String, Integer> listTmktNatureza;
    private List selectedTmktNatureza;

    private Map<String, Integer> listDepartamento;
    private List selectedDepartamento;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private Pessoa pessoa;

    private String tipoDataLancamento;
    private String dataLancamentoInicial;
    private String dataLancamentoFinal;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        tipoDataLancamento = "todos";
        dataLancamentoInicial = "";
        dataLancamentoFinal = "";

        pessoa = new Pessoa();

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
        List<ObjectJasper> cs = new ArrayList<>();
        List<Etiquetas> e = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioTelemarketingDao rcd = new RelatorioTelemarketingDao();
        if(idRelatorioOrdem != null) {
            rcd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));            
        }
        rcd.setRelatorios(r);
        List list = rcd.find(tipoDataLancamento, dataLancamentoInicial, dataLancamentoFinal, pessoa, inIdOperador(), inIdTmktNatureza(), inIdTmktContato(), inIdDepartamento());
        sisProcesso.finishQuery();
        ObjectJasper oj = new ObjectJasper();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (null != r.getId()) {
                switch (r.getId()) {
                    case 71:
                        oj = new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7));
                        break;
                    case 72:
                        oj = new ObjectJasper(o.get(0), o.get(1));
                        break;
                    case 73:
                        oj = new ObjectJasper(o.get(0), o.get(1), o.get(2));
                        break;
                    case 74:
                        oj = new ObjectJasper(o.get(0), o.get(1), true);
                        break;
                    default:
                        break;
                }
            }
            cs.add(oj);
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.EXPORT_TO = true;
        Jasper.TITLE = r.getNome();
        Jasper.TYPE = "default";
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) cs, map);
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
        listFilters.add(new Filters("data_lancamento", "Data Lançamento", false, false));
        listFilters.add(new Filters("pessoa", "Pessoa", false, true));
        listFilters.add(new Filters("operador", "Operador", false, false));
        listFilters.add(new Filters("natureza", "Natureza", false, false));
        listFilters.add(new Filters("tipo_contato", "Tipo de contato", false, false));
        listFilters.add(new Filters("departamento", "Departamento", false, false));
    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "data_lancamento":
                tipoDataLancamento = "";
                dataLancamentoInicial = "";
                dataLancamentoFinal = "";
                break;
            case "pessoa":
                pessoa = new Pessoa();
                break;
            case "operador":
                if (filter.getActive()) {
                    loadListOperador();
                } else {
                    listOperador = new LinkedHashMap<>();
                    selectedOperador = new ArrayList<>();
                }
                break;
            case "natureza":
                if (filter.getActive()) {
                    loadListTmktNatureza();
                } else {
                    listTmktNatureza = new LinkedHashMap<>();
                    selectedTmktNatureza = new ArrayList<>();
                }
                break;
            case "tipo_contato":
                if (filter.getActive()) {
                    loadListTmktContato();
                } else {
                    listTmktContato = new LinkedHashMap<>();
                    selectedTmktContato = new ArrayList<>();
                }
                break;
            case "departamento":
                if (filter.getActive()) {
                    loadListDepartamento();
                } else {
                    listDepartamento = new LinkedHashMap<>();
                    selectedDepartamento = new ArrayList<>();
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

    public void loadListTmktContato() {
        listTmktContato = new LinkedHashMap<>();
        selectedTmktContato = new ArrayList<>();
        List<TmktContato> list = new Dao().list(new TmktContato(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listTmktContato.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListTmktNatureza() {
        listTmktNatureza = new LinkedHashMap<>();
        selectedTmktNatureza = new ArrayList<>();
        List<TmktNatureza> list = new Dao().list(new TmktNatureza(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listTmktNatureza.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListOperador() {
        listOperador = new LinkedHashMap<>();
        selectedOperador = new ArrayList<>();
        List<Usuario> list = new Dao().list(new Usuario(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listOperador.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
            }
        }
    }

    public void loadListDepartamento() {
        listDepartamento = new LinkedHashMap<>();
        selectedDepartamento = new ArrayList<>();
        List<Departamento> list = new Dao().list(new Departamento(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listDepartamento.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    // TRATAMENTO
    public String inIdTmktContato() {
        String ids = null;
        if (selectedTmktContato != null) {
            for (int i = 0; i < selectedTmktContato.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedTmktContato.get(i);
                } else {
                    ids += "," + selectedTmktContato.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdTmktNatureza() {
        String ids = null;
        if (selectedTmktNatureza != null) {
            for (int i = 0; i < selectedTmktNatureza.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedTmktNatureza.get(i);
                } else {
                    ids += "," + selectedTmktNatureza.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdOperador() {
        String ids = null;
        if (selectedOperador != null) {
            for (int i = 0; i < selectedOperador.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedOperador.get(i);
                } else {
                    ids += "," + selectedOperador.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdDepartamento() {
        String ids = null;
        if (selectedDepartamento != null) {
            for (int i = 0; i < selectedDepartamento.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedDepartamento.get(i);
                } else {
                    ids += "," + selectedDepartamento.get(i);
                }
            }
        }
        return ids;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Map<String, Integer> getListDepartamento() {
        return listDepartamento;
    }

    public void setListDepartamento(Map<String, Integer> listDepartamento) {
        this.listDepartamento = listDepartamento;
    }

    public List getSelectedDepartamento() {
        return selectedDepartamento;
    }

    public void setSelectedDepartamento(List selectedDepartamento) {
        this.selectedDepartamento = selectedDepartamento;
    }

    public String getTipoDataLancamento() {
        return tipoDataLancamento;
    }

    public void setTipoDataLancamento(String tipoDataLancamento) {
        this.tipoDataLancamento = tipoDataLancamento;
    }

    public String getDataLancamentoInicial() {
        return dataLancamentoInicial;
    }

    public void setDataLancamentoInicial(String dataLancamentoInicial) {
        this.dataLancamentoInicial = dataLancamentoInicial;
    }

    public String getDataLancamentoFinal() {
        return dataLancamentoFinal;
    }

    public void setDataLancamentoFinal(String dataLancamentoFinal) {
        this.dataLancamentoFinal = dataLancamentoFinal;
    }

    public Map<String, Integer> getListOperador() {
        return listOperador;
    }

    public void setListOperador(Map<String, Integer> listOperador) {
        this.listOperador = listOperador;
    }

    public List getSelectedOperador() {
        return selectedOperador;
    }

    public void setSelectedOperador(List selectedOperador) {
        this.selectedOperador = selectedOperador;
    }

    public Map<String, Integer> getListTmktContato() {
        return listTmktContato;
    }

    public void setListTmktContato(Map<String, Integer> listTmktContato) {
        this.listTmktContato = listTmktContato;
    }

    public List getSelectedTmktContato() {
        return selectedTmktContato;
    }

    public void setSelectedTmktContato(List selectedTmktContato) {
        this.selectedTmktContato = selectedTmktContato;
    }

    public Map<String, Integer> getListTmktNatureza() {
        return listTmktNatureza;
    }

    public void setListTmktNatureza(Map<String, Integer> listTmktNatureza) {
        this.listTmktNatureza = listTmktNatureza;
    }

    public List getSelectedTmktNatureza() {
        return selectedTmktNatureza;
    }

    public void setSelectedTmktNatureza(List selectedTmktNatureza) {
        this.selectedTmktNatureza = selectedTmktNatureza;
    }

    public class ObjectJasper {

        private Object data_lancamento;
        private Object natureza;
        private Object tipo_contato;
        private Object contato;
        private Object operador_nome;
        private Object departamento;
        private Object pessoa_nome;
        private Object historico;
        private Object ano;
        private Object mes;
        private Object quantidade;

        public ObjectJasper() {
            this.data_lancamento = null;
            this.natureza = null;
            this.tipo_contato = null;
            this.contato = null;
            this.operador_nome = null;
            this.departamento = null;
            this.pessoa_nome = null;
            this.historico = null;
            this.ano = null;
            this.mes = null;
            this.quantidade = null;
        }

        /**
         * Analítico
         *
         * @param data_lancamento
         * @param natureza
         * @param tipo_contato
         * @param contato
         * @param operador_nome
         * @param departamento
         * @param pessoa_nome
         * @param historico
         */
        public ObjectJasper(Object data_lancamento, Object natureza, Object tipo_contato, Object contato, Object operador_nome, Object departamento, Object pessoa_nome, Object historico) {
            this.data_lancamento = data_lancamento;
            this.natureza = natureza;
            this.tipo_contato = tipo_contato;
            this.contato = contato;
            this.operador_nome = operador_nome;
            this.departamento = departamento;
            this.pessoa_nome = pessoa_nome;
            this.historico = historico;
        }

        /**
         * Resumo Data
         *
         * @param data_lancamento
         * @param quantidade
         */
        public ObjectJasper(Object data_lancamento, Object quantidade) {
            this.data_lancamento = data_lancamento;
            this.quantidade = quantidade;
        }

        /**
         * Resumo Mês a Mês
         *
         * @param ano
         * @param mes
         * @param quantidade
         */
        public ObjectJasper(Object ano, Object mes, Object quantidade) {
            this.ano = ano;
            this.mes = mes;
            this.quantidade = quantidade;
        }

        /**
         * Resumo Operador
         *
         * @param operador_nome
         * @param quantidade
         * @param operador
         */
        public ObjectJasper(Object operador_nome, Object quantidade, Boolean operador) {
            this.operador_nome = operador_nome;
            this.quantidade = quantidade;
        }

        public Object getData_lancamento() {
            return data_lancamento;
        }

        public void setData_lancamento(Object data_lancamento) {
            this.data_lancamento = data_lancamento;
        }

        public Object getNatureza() {
            return natureza;
        }

        public void setNatureza(Object natureza) {
            this.natureza = natureza;
        }

        public Object getTipo_contato() {
            return tipo_contato;
        }

        public void setTipo_contato(Object tipo_contato) {
            this.tipo_contato = tipo_contato;
        }

        public Object getContato() {
            return contato;
        }

        public void setContato(Object contato) {
            this.contato = contato;
        }

        public Object getOperador_nome() {
            return operador_nome;
        }

        public void setOperador_nome(Object operador_nome) {
            this.operador_nome = operador_nome;
        }

        public Object getDepartamento() {
            return departamento;
        }

        public void setDepartamento(Object departamento) {
            this.departamento = departamento;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getHistorico() {
            return historico;
        }

        public void setHistorico(Object historico) {
            this.historico = historico;
        }

        public Object getAno() {
            return ano;
        }

        public void setAno(Object ano) {
            this.ano = ano;
        }

        public Object getMes() {
            return mes;
        }

        public void setMes(Object mes) {
            this.mes = mes;
        }

        public Object getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Object quantidade) {
            this.quantidade = quantidade;
        }

    }
}
