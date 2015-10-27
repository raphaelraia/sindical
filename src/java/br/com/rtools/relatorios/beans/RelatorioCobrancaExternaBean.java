package br.com.rtools.relatorios.beans;

import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.impressao.RelatorioCobrancaExterna;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCobrancaExternaDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
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

    private Fisica fisica;

    @PostConstruct
    public void init() {
        fisica = new Fisica();
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
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        String order = "";
        Integer titular_id = null;
        Map map = new HashMap();
        if (listFilters.get(1).getActive()) {
            if (fisica.getId() != -1) {
                titular_id = fisica.getPessoa().getId();
            } else {
                GenericaMensagem.warn("Validação", "Pesquise uma pessoa!");
                return;
            }
            Pessoa p = new Registro().getRegistroEmpresarial().getFilial().getPessoa();
            map.put("entidade_nome", p.getNome());
            map.put("entidade_documento", p.getDocumento());
            map.put("entidade_endereco", p.getPessoaEndereco().getEnderecoCompletoString());
        }
        String detalheRelatorio = "";
        List<RelatorioCobrancaExterna> rces = new ArrayList<>();
        List<CobrancaExternaRecibo> cers = new ArrayList<>();
        sisProcesso.startQuery();
        List list = new RelatorioCobrancaExternaDao().find(r.getId(), inIdTipoCobranca(), titular_id);
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (r.getId() == 58) {
                cers.add(
                        new CobrancaExternaRecibo(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8), o.get(9), o.get(10), o.get(11), o.get(12))
                );
            } else {
                rces.add(
                        new RelatorioCobrancaExterna(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6))
                );
            }
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.TITLE = r.getNome();
        if (r.getId() == 58) {
            Jasper.IS_HEADER = true;
            Jasper.TYPE = "recibo_sem_logo";
            Jasper.printReports(r.getJasper(), r.getNome(), (Collection) cers, map);
        } else {
            Jasper.TYPE = "default";
            Jasper.printReports(r.getJasper(), r.getNome(), (Collection) rces, map);
        }
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
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

    public void load() {
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadTipoCobranca() {
        listTipoCobranca = null;
        selectedTipoCobranca = new ArrayList();
        listTipoCobranca = new LinkedHashMap<>();
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
        listFilters.add(new Filters("titular", "Titular", false));

    }

    // LISTENERS
    public void clear(Filters filter) {
        filter.setActive(!filter.getActive());
        switch (filter.getKey()) {
            case "tipo_cobranca":
                loadTipoCobranca();
                break;
            case "titular":
                fisica = new Fisica();
                break;
        }
    }

    public void close(Filters filter) {
        if (!filter.getActive()) {
            switch (filter.getKey()) {
                case "tipo_cobranca":
                    loadTipoCobranca();
                    break;
                case "titular":
                    fisica = new Fisica();
                    break;
            }
        }
    }

    // TRATAMENTO
    public String inIdTipoCobranca() {
        String ids = null;
        if (selectedTipoCobranca != null) {
            ids = "";
            int b = 0;
            for (int i = 0; i < selectedTipoCobranca.size(); i++) {
                if (selectedTipoCobranca.get(i) != null) {
                    if(b > 0) {
                        ids += ",";
                    }
                    if(selectedTipoCobranca.get(i) != null) {
                        ids += selectedTipoCobranca.get(i);
                        b++;
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

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

//    public class RelatorioCobrancaExterna {
//
//        private Object tipo_cobranca_descricao;
//        private Object socio_codigo;
//        private Object socio_nome;
//        private Object mes;
//        private Object ano;
//        private Object valor;
//        private Object endereco_cobranca;
//
//        /**
//         *
//         * @param tipo_cobranca_descricao
//         * @param socio_codigo
//         * @param socio_nome
//         * @param mes
//         * @param ano
//         * @param valor
//         * @param endereco_cobranca
//         */
//        public RelatorioCobrancaExterna(Object tipo_cobranca_descricao, Object socio_codigo, Object socio_nome, Object mes, Object ano, Object valor, Object endereco_cobranca) {
//            this.tipo_cobranca_descricao = tipo_cobranca_descricao;
//            this.socio_codigo = socio_codigo;
//            this.socio_nome = socio_nome;
//            this.mes = mes;
//            this.ano = ano;
//            this.valor = valor;
//            this.endereco_cobranca = endereco_cobranca;
//        }
//
//        public Object getTipo_cobranca_descricao() {
//            return tipo_cobranca_descricao;
//        }
//
//        public void setTipo_cobranca_descricao(Object tipo_cobranca_descricao) {
//            this.tipo_cobranca_descricao = tipo_cobranca_descricao;
//        }
//
//        public Object getSocio_codigo() {
//            return socio_codigo;
//        }
//
//        public void setSocio_codigo(Object socio_codigo) {
//            this.socio_codigo = socio_codigo;
//        }
//
//        public Object getSocio_nome() {
//            return socio_nome;
//        }
//
//        public void setSocio_nome(Object socio_nome) {
//            this.socio_nome = socio_nome;
//        }
//
//        public Object getMes() {
//            return mes;
//        }
//
//        public void setMes(Object mes) {
//            this.mes = mes;
//        }
//
//        public Object getAno() {
//            return ano;
//        }
//
//        public void setAno(Object ano) {
//            this.ano = ano;
//        }
//
//        public Object getValor() {
//            return valor;
//        }
//
//        public void setValor(Object valor) {
//            this.valor = valor;
//        }
//
//        public Object getEndereco_cobranca() {
//            return endereco_cobranca;
//        }
//
//        public void setEndereco_cobranca(Object endereco_cobranca) {
//            this.endereco_cobranca = endereco_cobranca;
//        }
//
//    }
    public class CobrancaExternaRecibo {

        private Object empresa_nome;
        private Object empresa_cnpj;
        private Object categoria;
        private Object matricula;
        private Object tipo_cobranca_descricao;
        private Object socio_codigo;
        private Object socio_nome;
        private Object mes;
        private Object ano;
        private Object servico_descricao;
        private Object valor;
        private Object beneficiario;
        private Object movimento_vencimento;

        /**
         *
         * @param empresa_nome
         * @param empresa_cnpj
         * @param categoria
         * @param matricula
         * @param tipo_cobranca_descricao
         * @param socio_codigo
         * @param socio_nome
         * @param mes
         * @param ano
         * @param servico_descricao
         * @param valor
         * @param beneficiario
         * @param movimento_vencimento
         */
        public CobrancaExternaRecibo(Object empresa_nome, Object empresa_cnpj, Object categoria, Object matricula, Object tipo_cobranca_descricao, Object socio_codigo, Object socio_nome, Object mes, Object ano, Object servico_descricao, Object valor, Object beneficiario, Object movimento_vencimento) {
            this.empresa_nome = empresa_nome;
            this.empresa_cnpj = empresa_cnpj;
            this.categoria = categoria;
            this.matricula = matricula;
            this.tipo_cobranca_descricao = tipo_cobranca_descricao;
            this.socio_codigo = socio_codigo;
            this.socio_nome = socio_nome;
            this.mes = mes;
            this.ano = ano;
            this.servico_descricao = servico_descricao;
            this.valor = valor;
            this.beneficiario = beneficiario;
            this.movimento_vencimento = movimento_vencimento;
        }

        public Object getEmpresa_nome() {
            return empresa_nome;
        }

        public void setEmpresa_nome(Object empresa_nome) {
            this.empresa_nome = empresa_nome;
        }

        public Object getEmpresa_cnpj() {
            return empresa_cnpj;
        }

        public void setEmpresa_cnpj(Object empresa_cnpj) {
            this.empresa_cnpj = empresa_cnpj;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

        public Object getMatricula() {
            return matricula;
        }

        public void setMatricula(Object matricula) {
            this.matricula = matricula;
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

        public Object getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Object beneficiario) {
            this.beneficiario = beneficiario;
        }

        public Object getServico_descricao() {
            return servico_descricao;
        }

        public void setServico_descricao(Object servico_descricao) {
            this.servico_descricao = servico_descricao;
        }

        public Object getMovimento_vencimento() {
            return movimento_vencimento;
        }

        public void setMovimento_vencimento(Object movimento_vencimento) {
            this.movimento_vencimento = movimento_vencimento;
        }

    }
}
