/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.escola.beans;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.RelatorioParametros;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioFinanceiroEscolaDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class RelatorioFinanceiroEscolaBean implements Serializable {

    private Integer idRelatorio = 0;
    private List<SelectItem> listaRelatorio = new ArrayList();

    private Integer idRelatorioOrdem = 0;
    private List<SelectItem> listaRelatorioOrdem = new ArrayList();

    private String tipoPessoa = "";
    private Pessoa pessoa = new Pessoa();
    /**
     * Lista de Filtros (indices)
     * <p>
     * <br />0 CONTA CONTÁBIL
     * <br />1 GRUPO
     */
    private List<Filtros> listaFiltros = new ArrayList();
    private List<ListaServicos> listaServicos = new ArrayList();

    private String dataVencimento = "";
    private String dataVencimentoFinal = "";
    private String dataQuitacao = "";
    private String dataQuitacaoFinal = "";
    
    private boolean chkExcel = false;
    private boolean chkTodosServicos = false;

    public RelatorioFinanceiroEscolaBean() {
        loadListaRelatorio();
        loadListaFiltro();
    }

    
    public void marcarTodosServicos() {
        for (ListaServicos listaServico : listaServicos) {
            listaServico.setSelecionado(chkTodosServicos);
        }
    }
    
    public void imprimir() {
        Relatorios relatorios = (Relatorios) new Dao().find(new Relatorios(), Integer.parseInt(listaRelatorio.get(idRelatorio).getDescription()));

        String tipo_pessoa = null, id_servicos = "";
        List<String> ldescricao = new ArrayList();
        Integer id_pessoa = null;

        // TIPO PESSOA
        if (listaFiltros.get(0).ativo && pessoa.getId() != -1) {
            ldescricao.add("Tipo Pessoa: " + (tipoPessoa.equals("aluno") ? "ALUNO: " : "RESPONSÁVEL: ") + pessoa.getNome());
            tipo_pessoa = tipoPessoa;
            id_pessoa = pessoa.getId();
            
        }
        
        // DATAS
        String dtVencimento = "", dtQuitacao = "";
        String dtVencimentoFinal = "", dtQuitacaoFinal = "";
        if (listaFiltros.get(1).ativo) {
            if (!dataVencimento.isEmpty() && !dataVencimentoFinal.isEmpty()) {
                ldescricao.add("Data de Vencimento de: " + dataVencimento + " à " + dataVencimentoFinal);
                dtVencimento = dataVencimento;
                dtVencimentoFinal = dataVencimentoFinal;
            } else if (!dataVencimento.isEmpty() && dataVencimentoFinal.isEmpty()) {
                ldescricao.add("Data de Vencimento: " + dataVencimento);
                dtVencimento = dataVencimento;
            } else if (dataVencimento.isEmpty() && !dataVencimentoFinal.isEmpty()) {
                ldescricao.add("Data de Vencimento até: " + dataVencimentoFinal);
                dtVencimentoFinal = dataVencimentoFinal;
            }

            if (!dataQuitacao.isEmpty() && !dataQuitacaoFinal.isEmpty()) {
                ldescricao.add("Data de Quitação de: " + dataQuitacao + " à " + dataQuitacaoFinal);
                dtQuitacao = dataQuitacao;
                dtQuitacaoFinal = dataQuitacaoFinal;
            } else if (!dataQuitacao.isEmpty() && dataQuitacaoFinal.isEmpty()) {
                ldescricao.add("Data de Quitação: " + dataQuitacao);
                dtQuitacao = dataQuitacao;
            } else if (dataQuitacao.isEmpty() && !dataQuitacaoFinal.isEmpty()) {
                ldescricao.add("Data de Quitação até: " + dataQuitacaoFinal);
                dtQuitacaoFinal = dataQuitacaoFinal;
            }
        }
        
        // SERVIÇOS
        if (listaFiltros.get(2).ativo) {
            String descricao_curso = "";
            for (ListaServicos ls : listaServicos) {
                if (ls.selecionado) {
                    if (id_servicos.isEmpty()) {
                        descricao_curso = ls.getServico().getDescricao().toUpperCase();
                        id_servicos = "" + ls.getServico().getId();
                    } else {
                        descricao_curso += ", " + ls.getServico().getDescricao().toUpperCase();
                        id_servicos += ", " + ls.getServico().getId();
                    }
                }
            }
            
            if (!id_servicos.isEmpty()){
                ldescricao.add("Cursos: " + descricao_curso);
            }
        }

        String ordem = "";
        if (!listaRelatorioOrdem.isEmpty()) {
            ordem = ((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), Integer.valueOf(listaRelatorioOrdem.get(idRelatorioOrdem).getDescription()))).getQuery();
        }

        String descricaoData = "";
        for (String linha : ldescricao) {
            if (descricaoData.isEmpty()) {
                descricaoData = linha;
            } else {
                descricaoData += ", " + linha;
            }
        }
        Map params = new HashMap();
        params.put("descricao_pesquisa", descricaoData);

        List<Object> result = new RelatorioFinanceiroEscolaDao().listaRelatorioFinanceiro(id_servicos, dtVencimento, dtVencimentoFinal, dtQuitacao, dtQuitacaoFinal, tipo_pessoa, id_pessoa, ordem, relatorios);

        if (result.isEmpty()) {
            GenericaMensagem.error("Atenção", "Nenhum resultado encontrado para a pesquisa!");
            return;
        }

        List<RelatorioParametros> listaRL = new RelatorioDao().listaRelatorioParametro(relatorios.getId());

        List<HashMap> list_hash = new ArrayList();

        String[] param_query = new String[listaRL.size()];
        for (int i = 0; i < listaRL.size(); i++) {
            param_query[i] = listaRL.get(i).getApelido();
        }

        for (Object linha : result) {
            List list = (List) linha;
            HashMap<String, Object> map = new LinkedHashMap();
            for (int i = 0; i < param_query.length; i++) {
                map.put(param_query[i], list.get(i));
            }

            list_hash.add(map);
        }

        Jasper.EXPORT_TO = chkExcel;
        Jasper.IS_HEADER_PARAMS = true;

        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), list_hash, params);
    }

    public void acao(Filtros linha) {
        linha.setAtivo(!linha.ativo);

        switch (linha.chave) {
            case "tipoPessoa":
                pessoa = new Pessoa();
                break;
            case "datas":
                break;
            case "servicos":
                loadListaServicos();
                break;
        }
    }

    public final void loadListaServicos() {
        listaServicos.clear();

        List<Servicos> result = new RelatorioFinanceiroEscolaDao().listaServicos();
        for (Servicos result1 : result) {
            listaServicos.add(new ListaServicos(false, result1));
        }
    }
    
    public final void loadListaFiltro() {
        listaFiltros.clear();

        listaFiltros.add(new Filtros("tipoPessoa", "Tipo de Pessoa", false));
        listaFiltros.add(new Filtros("datas", "Datas", false));
        listaFiltros.add(new Filtros("servicos", "Cursos", false));
    }

    public final void loadListaRelatorioOrdem() {
        listaRelatorioOrdem.clear();

        RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
        List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(Integer.parseInt(listaRelatorio.get(idRelatorio).getDescription()));

        for (int i = 0; i < list.size(); i++) {
            listaRelatorioOrdem.add(
                    new SelectItem(
                            i,
                            list.get(i).getNome(),
                            "" + list.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaRelatorio() {
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(377);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRelatorio = i;
            }
            if (list.get(i).getPrincipal()) {
                idRelatorio = i;
            }
            listaRelatorio.add(
                    new SelectItem(
                            i,
                            list.get(i).getNome(),
                            Integer.toString(list.get(i).getId())
                    )
            );
        }

        loadListaRelatorioOrdem();
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public List<SelectItem> getListaRelatorio() {
        return listaRelatorio;
    }

    public void setListaRelatorio(List<SelectItem> listaRelatorio) {
        this.listaRelatorio = listaRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public List<SelectItem> getListaRelatorioOrdem() {
        return listaRelatorioOrdem;
    }

    public void setListaRelatorioOrdem(List<SelectItem> listaRelatorioOrdem) {
        this.listaRelatorioOrdem = listaRelatorioOrdem;
    }

    public List<Filtros> getListaFiltros() {
        return listaFiltros;
    }

    public void setListaFiltros(List<Filtros> listaFiltros) {
        this.listaFiltros = listaFiltros;
    }

    public boolean isChkExcel() {
        return chkExcel;
    }

    public void setChkExcel(boolean chkExcel) {
        this.chkExcel = chkExcel;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(String dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public String getDataQuitacao() {
        return dataQuitacao;
    }

    public void setDataQuitacao(String dataQuitacao) {
        this.dataQuitacao = dataQuitacao;
    }

    public String getDataQuitacaoFinal() {
        return dataQuitacaoFinal;
    }

    public void setDataQuitacaoFinal(String dataQuitacaoFinal) {
        this.dataQuitacaoFinal = dataQuitacaoFinal;
    }

    public List<ListaServicos> getListaServicos() {
        return listaServicos;
    }

    public void setListaServicos(List<ListaServicos> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public boolean isChkTodosServicos() {
        return chkTodosServicos;
    }

    public void setChkTodosServicos(boolean chkTodosServicos) {
        this.chkTodosServicos = chkTodosServicos;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("fisicaPesquisa")){
            pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public class Filtros {

        private String chave = "";
        private String opcao = "";
        private boolean ativo = false;

        public Filtros(String chave, String opcao, boolean ativo) {
            this.chave = chave;
            this.opcao = opcao;
            this.ativo = ativo;
        }

        public String getChave() {
            return chave;
        }

        public void setChave(String chave) {
            this.chave = chave;
        }

        public String getOpcao() {
            return opcao;
        }

        public void setOpcao(String opcao) {
            this.opcao = opcao;
        }

        public boolean isAtivo() {
            return ativo;
        }

        public void setAtivo(boolean ativo) {
            this.ativo = ativo;
        }
    }
    
    public class ListaServicos {
        private Boolean selecionado;
        private Servicos servico;

        public ListaServicos(Boolean selecionado, Servicos servico) {
            this.selecionado = selecionado;
            this.servico = servico;
        }

        public Boolean getSelecionado() {
            return selecionado;
        }

        public void setSelecionado(Boolean selecionado) {
            this.selecionado = selecionado;
        }

        public Servicos getServico() {
            return servico;
        }

        public void setServico(Servicos servico) {
            this.servico = servico;
        }
        
    }
}
