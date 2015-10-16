package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioDescontoFolhaDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
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
public class RelatorioDescontoFolhaBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private Pessoa socio;

    private Juridica empresa;

    private String referenciaInicial;
    private String referenciaFinal;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        empresa = new Juridica();
        socio = new Pessoa();
        listRelatorioOrdem = new ArrayList<>();
        idRelatorioOrdem = null;
        referenciaInicial = null;
        referenciaFinal = null;
        loadListaFiltro();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCaixaBean");
    }

    public void print() {
        if (referenciaInicial.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar referência!");
            return;
        }
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        List relatorioDetalhes = new ArrayList<>();
        String rf_i = null;
        String rf_f = null;
        Integer empresa_id = null;
        Integer titular_id = null;
        if (listFilters.get(0).getActive()) {
            if (empresa.getId() != -1) {
                empresa_id = empresa.getId();
                relatorioDetalhes.add("Empresa (" + empresa_id + ") " + empresa.getPessoa().getNome());
            }
        }
        if (listFilters.get(1).getActive()) {
            if (socio.getId() != -1) {
                titular_id = socio.getId();
                relatorioDetalhes.add("Empresa (" + titular_id + ") " + socio.getNome());
            }
        }
        if (listFilters.get(2).getActive()) {
            rf_i = getReferenciaInicial();
            rf_f = getReferenciaFinal();
        }
        List<RelatorioDescontoFolha> rdfs = new ArrayList<>();
        sisProcesso.startQuery();
        List list = new RelatorioDescontoFolhaDao().find(empresa_id, titular_id, rf_i, rf_f);
        sisProcesso.finishQuery();
        ConfiguracaoSocial configuracaoSocial = (ConfiguracaoSocial) new Dao().find(new ConfiguracaoSocial(), 1);
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            rdfs.add(
                    new RelatorioDescontoFolha(
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
                            o.get(11),
                            o.get(12),
                            o.get(13),
                            o.get(14),
                            o.get(15),
                            o.get(16)
                    )
            );
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.TITLE = r.getNome();
        Jasper.TYPE = "default";
        Map map = new HashMap();
        map.put("obs_desconto_folha", configuracaoSocial.getObsDescontoFolha());
        Jasper.printReports(r.getJasper(), r.getNome(), rdfs, map);
        sisProcesso.setProcesso("Relatório " + r.getNome());
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

    public void loadListaFiltro() {
        listFilters.clear();
        listFilters.add(new Filters("empresa", "Empresa", false));
        listFilters.add(new Filters("titular", "Titular", false));
        listFilters.add(new Filters("referencia", "Referência", true, true));
    }

    // LISTENERS
    public void clear(Filters filter) {
        filter.setActive(!filter.getActive());
        switch (filter.getKey()) {
            case "empresa":
                empresa = new Juridica();
                break;
            case "titular":
                socio = new Pessoa();
                break;
            case "referencia":
                referenciaInicial = null;
                referenciaFinal = null;
                break;
        }
    }

    public void close(Filters filter) {
        if (!filter.getActive()) {
            switch (filter.getKey()) {
                case "empresa":
                    empresa = new Juridica();
                    break;
                case "titular":
                    socio = new Pessoa();
                    break;
                case "referencia":
                    referenciaInicial = null;
                    referenciaFinal = null;
                    break;
            }
        }
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

    public Pessoa getSocio() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            socio = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return socio;
    }

    public void setSocio(Pessoa socio) {
        this.socio = socio;
    }

    public Juridica getEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            empresa = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    public class RelatorioDescontoFolha {

        private Object socio_codigo;
        private Object empresa_nome;
        private Object titular_nome;
        private Object movimento_referencia;
        private Object movimento_vencimento;
        private Object movimento_valor;
        private Object empresa_documento;
        private Object empresa_telefone1;
        private Object e_endereco_logradouro;
        private Object e_endereco_descricao;
        private Object e_endereco_numero;
        private Object e_endereco_complemento;
        private Object e_endereco_bairro;
        private Object e_endereco_cidade;
        private Object e_endereco_uf;
        private Object e_endereco_cep;
        private Object empresa_contato;

        public RelatorioDescontoFolha(Object socio_codigo, Object empresa_nome, Object titular_nome, Object movimento_referencia, Object movimento_vencimento, Object movimento_valor, Object empresa_documento, Object empresa_telefone1, Object e_endereco_logradouro, Object e_endereco_descricao, Object e_endereco_numero, Object e_endereco_complemento, Object e_endereco_bairro, Object e_endereco_cidade, Object e_endereco_uf, Object e_endereco_cep, Object empresa_contato) {
            this.socio_codigo = socio_codigo;
            this.empresa_nome = empresa_nome;
            this.titular_nome = titular_nome;
            this.movimento_referencia = movimento_referencia;
            this.movimento_vencimento = movimento_vencimento;
            this.movimento_valor = movimento_valor;
            this.empresa_documento = empresa_documento;
            this.empresa_telefone1 = empresa_telefone1;
            this.e_endereco_logradouro = e_endereco_logradouro;
            this.e_endereco_descricao = e_endereco_descricao;
            this.e_endereco_numero = e_endereco_numero;
            this.e_endereco_complemento = e_endereco_complemento;
            this.e_endereco_bairro = e_endereco_bairro;
            this.e_endereco_cidade = e_endereco_cidade;
            this.e_endereco_uf = e_endereco_uf;
            this.e_endereco_cep = e_endereco_cep;
            this.empresa_contato = empresa_contato;
        }

        public Object getSocio_codigo() {
            return socio_codigo;
        }

        public void setSocio_codigo(Object socio_codigo) {
            this.socio_codigo = socio_codigo;
        }

        public Object getEmpresa_nome() {
            return empresa_nome;
        }

        public void setEmpresa_nome(Object empresa_nome) {
            this.empresa_nome = empresa_nome;
        }

        public Object getTitular_nome() {
            return titular_nome;
        }

        public void setTitular_nome(Object titular_nome) {
            this.titular_nome = titular_nome;
        }

        public Object getMovimento_referencia() {
            return movimento_referencia;
        }

        public void setMovimento_referencia(Object movimento_referencia) {
            this.movimento_referencia = movimento_referencia;
        }

        public Object getMovimento_vencimento() {
            return movimento_vencimento;
        }

        public void setMovimento_vencimento(Object movimento_vencimento) {
            this.movimento_vencimento = movimento_vencimento;
        }

        public Object getMovimento_valor() {
            return movimento_valor;
        }

        public void setMovimento_valor(Object movimento_valor) {
            this.movimento_valor = movimento_valor;
        }

        public Object getEmpresa_documento() {
            return empresa_documento;
        }

        public void setEmpresa_documento(Object empresa_documento) {
            this.empresa_documento = empresa_documento;
        }

        public Object getEmpresa_telefone1() {
            return empresa_telefone1;
        }

        public void setEmpresa_telefone1(Object empresa_telefone1) {
            this.empresa_telefone1 = empresa_telefone1;
        }

        public Object getE_endereco_logradouro() {
            return e_endereco_logradouro;
        }

        public void setE_endereco_logradouro(Object e_endereco_logradouro) {
            this.e_endereco_logradouro = e_endereco_logradouro;
        }

        public Object getE_endereco_descricao() {
            return e_endereco_descricao;
        }

        public void setE_endereco_descricao(Object e_endereco_descricao) {
            this.e_endereco_descricao = e_endereco_descricao;
        }

        public Object getE_endereco_numero() {
            return e_endereco_numero;
        }

        public void setE_endereco_numero(Object e_endereco_numero) {
            this.e_endereco_numero = e_endereco_numero;
        }

        public Object getE_endereco_complemento() {
            return e_endereco_complemento;
        }

        public void setE_endereco_complemento(Object e_endereco_complemento) {
            this.e_endereco_complemento = e_endereco_complemento;
        }

        public Object getE_endereco_bairro() {
            return e_endereco_bairro;
        }

        public void setE_endereco_bairro(Object e_endereco_bairro) {
            this.e_endereco_bairro = e_endereco_bairro;
        }

        public Object getE_endereco_cidade() {
            return e_endereco_cidade;
        }

        public void setE_endereco_cidade(Object e_endereco_cidade) {
            this.e_endereco_cidade = e_endereco_cidade;
        }

        public Object getE_endereco_uf() {
            return e_endereco_uf;
        }

        public void setE_endereco_uf(Object e_endereco_uf) {
            this.e_endereco_uf = e_endereco_uf;
        }

        public Object getE_endereco_cep() {
            return e_endereco_cep;
        }

        public void setE_endereco_cep(Object e_endereco_cep) {
            this.e_endereco_cep = e_endereco_cep;
        }

        public Object getEmpresa_contato() {
            return empresa_contato;
        }

        public void setEmpresa_contato(Object empresa_contato) {
            this.empresa_contato = empresa_contato;
        }
    }
}
