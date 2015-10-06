package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.db.CategoriaDB;
import br.com.rtools.associativo.db.CategoriaDBToplink;
import br.com.rtools.associativo.db.ParentescoDB;
import br.com.rtools.associativo.db.ParentescoDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.GrupoFinanceiro;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.SubGrupoFinanceiro;
import br.com.rtools.financeiro.db.FinanceiroDB;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.RelatorioParametros;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioFinanceiroDao;
import br.com.rtools.relatorios.dao.RelatorioFinanceiroSocialDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
public class RelatorioFinanceiroSocialBean implements Serializable {

    private Integer idRelatorio = 0;
    private List<SelectItem> listaRelatorio = new ArrayList();

    private Integer idRelatorioOrdem = 0;
    private List<SelectItem> listaRelatorioOrdem = new ArrayList();

    private Boolean chkExcel = false;

    private Integer idGrupoCategoria = 0;
    private List<SelectItem> listaGrupoCategoria = new ArrayList();
    private Integer idCategoria = 0;
    private List<SelectItem> listaCategoria = new ArrayList();
    private Integer idParentesco = 0;
    private List<SelectItem> listaParentesco = new ArrayList();

    private Boolean chkCidadeSocio = true;
    private Cidade cidadeSocio = new Cidade();
    private Cidade cidadeEmpresa = new Cidade();
    private Boolean votante = true;

    private String dataCadastro = "";
    private String dataCadastroFinal = "";
    private String dataRecadastro = "";
    private String dataRecadastroFinal = "";
    private String dataAdmissao = "";
    private String dataAdmissaoFinal = "";
    private String dataDemissao = "";
    private String dataDemissaoFinal = "";
    private String dataFiliacao = "";
    private String dataFiliacaoFinal = "";
    private String dataAposentadoria = "";
    private String dataAposentadoriaFinal = "";
    private String dataAtualizacao = "";
    private String dataAtualizacaoFinal = "";

    private String tipoSituacao = "ativo";

    private Integer idGrupo = 0;
    private List<SelectItem> listaGrupo = new ArrayList();

    private Integer idSubGrupo = 0;
    private List<SelectItem> listaSubGrupo = new ArrayList();

    private Integer idServicos = 0;
    private List<SelectItem> listaServicos = new ArrayList();

    private Integer idTipoCobranca = 0;
    private List<SelectItem> listaTipoCobranca = new ArrayList();

    private String dataEmissao = "";
    private String dataEmissaoFinal = "";
    private String dataVencimento = "";
    private String dataVencimentoFinal = "";
    private String dataQuitacao = "";
    private String dataQuitacaoFinal = "";
    private String tipoES = "E";
    private Pessoa pessoa = new Pessoa();
    private String tipoPessoa = "responsavel";
    private String tipoSituacaoFinanceiro = "atrasado";
    private String tipoDepartamento = "outros";
    private String tipoPessoaFinanceiro = "pessoa";

    private String descontoFolhaSocio = "SIM";
    private String descontoFolhaFinanceiro = "SIM";
    /**
     * Lista de Filtros (indices)
     * <p>
     * 0 GRUPO / CATEGORIA
     * <br />1 GRAU
     * <br />2 CIDADE DO SÓCIO
     * <br />3 CIDADE DA EMPRESA
     * <br />4 VOTANTE
     * <br />5 DATAS
     * <br />6 SITUAÇÃO
     * <br />7 PESSOA
     * <br />8 DESCONTO FOLHA SOCIAL
     */
    private List<Filtros> listaFiltros = new ArrayList();

    /**
     * Lista de Filtros (indices)
     * <p>
     * 0 GRUPO 1 SUB GRUPO
     * <br />2 SERVIÇOS
     * <br />3 DATAS
     * <br />4 E / S
     * <br />5 SITUAÇÃO FINANCEIRO
     * <br />6 DEPARTAMENTO
     * <br />7 TIPO PESSOA
     * <br />8 TIPO COBRANÇA
     * <br />9 DESCONTO FOLHA FINANCEIRO
     */
    private List<Filtros> listaFiltrosFinanceiro = new ArrayList();

    @PostConstruct
    public void init() {
        loadListaRelatorio();
        loadListaFiltro();
        loadListaFiltroFinanceiro();
        loadListaTipoCobranca();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioFinanceiroBean");
    }

    public void loadListaRelatorioOrdem() {
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

    public void loadListaRelatorio() {
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(327);
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

    public void loadListaFiltro() {
        listaFiltros.clear();

        listaFiltros.add(new Filtros("grupoCategoria", "Grupo / Categoria", false));
        listaFiltros.add(new Filtros("grau", "Grau", false));
        listaFiltros.add(new Filtros("cidadeSocio", "Cidade do Sócio", false));
        listaFiltros.add(new Filtros("cidadeEmpresa", "Cidade da Empresa", false));
        listaFiltros.add(new Filtros("votante", "Votante", false));
        listaFiltros.add(new Filtros("datas", "Datas", false));
        listaFiltros.add(new Filtros("situacao", "Situação", false));
        listaFiltros.add(new Filtros("pessoa", "Pessoa", false));
        listaFiltros.add(new Filtros("desconto_folha_social", "Desconto em Folha Social", false));

    }

    public void loadListaFiltroFinanceiro() {
        listaFiltrosFinanceiro.clear();

        listaFiltrosFinanceiro.add(new Filtros("grupo", "Grupo Financeiro", false));
        listaFiltrosFinanceiro.add(new Filtros("subGrupo", "Sub Grupo Financeiro", false));
        listaFiltrosFinanceiro.add(new Filtros("servico", "Serviços", false));
        listaFiltrosFinanceiro.add(new Filtros("datas", "Datas", false));
        listaFiltrosFinanceiro.add(new Filtros("es", "E / S", false));
        listaFiltrosFinanceiro.add(new Filtros("situacaoFinanceiro", "Situação Financeira", false));
        listaFiltrosFinanceiro.add(new Filtros("departamento", "Departamento", true));
        listaFiltrosFinanceiro.add(new Filtros("tipoPessoa", "Tipo de Pessoa", false));
        listaFiltrosFinanceiro.add(new Filtros("tipo_cobranca", "Tipo de Cobrança", false));
        listaFiltrosFinanceiro.add(new Filtros("desconto_folha_financeiro", "Desconto em Folha Financeiro", false));
    }

    public void acao(Filtros linha) {
        linha.setAtivo(!linha.ativo);
        // NÃO É NECESSÁRIO ESTAR TODOS OS TIPOS DA LISTA NESSE SWITCH 
        switch (linha.chave) {
            case "grupoCategoria":
                loadListaGrupoCategoria();
                break;
            case "grau":
                loadListaParentesco();
                break;
            case "cidadeSocio":
                cidadeSocio = new Cidade();
                break;
            case "cidadeEmpresa":
                cidadeEmpresa = new Cidade();
                break;
            case "pessoa":
                pessoa = new Pessoa();
                break;
            case "desconto_folha_socio":
                descontoFolhaSocio = "SIM";
                break;
        }
    }

    public void acaoFinanceiro(Filtros linha) {
        linha.setAtivo(!linha.ativo);
        // NÃO É NECESSÁRIO ESTAR TODOS OS TIPOS DA LISTA NESSE SWITCH 
        switch (linha.chave) {
            case "grupo":
                loadListaGrupo();
                break;
            case "subGrupo":
                loadListaSubGrupo();
                break;
            case "servicos":
                loadListaServicos();
                break;
            case "departamento":
                break;
            case "tipoPessoa":
                break;
            case "tipo_cobranca":
                loadListaTipoCobranca();
                break;
            case "desconto_folha_financeiro":
                descontoFolhaFinanceiro = "SIM";
                    break;
        }
    }

    public final void loadListaGrupoCategoria() {
        listaGrupoCategoria.clear();
        idGrupoCategoria = 0;

        CategoriaDB db = new CategoriaDBToplink();
        List<GrupoCategoria> grupoCategorias = db.pesquisaGrupoCategoriaOrdenada();

        if (!grupoCategorias.isEmpty()) {
            for (int i = 0; i < grupoCategorias.size(); i++) {
                listaGrupoCategoria.add(new SelectItem(i, grupoCategorias.get(i).getGrupoCategoria(), "" + grupoCategorias.get(i).getId()));
            }
        } else {
            listaGrupoCategoria.add(new SelectItem(0, "Nenhum Grupo Categoria Encontrado", "0"));
        }

        loadListaCategoria();
    }

    public final void loadListaCategoria() {
        listaCategoria.clear();
        idCategoria = 0;
        if (!listaGrupoCategoria.isEmpty()) {
            CategoriaDB db = new CategoriaDBToplink();
            List<Categoria> select = db.pesquisaCategoriaPorGrupo(Integer.parseInt(listaGrupoCategoria.get(idGrupoCategoria).getDescription()));
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaCategoria.add(new SelectItem(i, select.get(i).getCategoria(), Integer.toString(select.get(i).getId())));
                }
            } else {
                listaCategoria.add(new SelectItem(0, "Nenhuma Categoria Encontrada", "0"));
            }
        } else {
            listaCategoria.add(new SelectItem(0, "Nenhuma Categoria Encontrada", "0"));
        }

        if (listaFiltros.get(0).ativo && listaFiltros.get(1).ativo) {
            loadListaParentesco();
        }
    }

    public void loadListaParentesco() {
        idParentesco = 0;
        listaParentesco.clear();

        ParentescoDB db = new ParentescoDao();

        List<Parentesco> select;

        if (listaFiltros.get(0).ativo) {
            select = db.pesquisaTodosSemTitularCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
        } else {
            select = db.pesquisaTodosSemTitular();
        }

        for (int i = 0; i < select.size(); i++) {
            listaParentesco.add(new SelectItem(i, select.get(i).getParentesco(), Integer.toString(select.get(i).getId())));
        }

        //List<Parentesco> select = db.pesquisaTodosSemTitularCategoriaSexo(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()), sexo);
        //List<Parentesco> select = db.pesquisaTodosSemTitular();
    }

    public void loadListaGrupo() {
        listaGrupo.clear();

        Dao di = new Dao();
        List<GrupoFinanceiro> result = di.list(new GrupoFinanceiro());

        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaGrupo.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }

        loadListaSubGrupo();
    }

    public void loadListaSubGrupo() {
        listaSubGrupo.clear();
        idSubGrupo = 0;
        idServicos = 0;
        FinanceiroDB db = new FinanceiroDBToplink();

        // (listaFiltrosFinanceiro.get(0).ativo) GRUPO
        List<SubGrupoFinanceiro> result = db.listaSubGrupo((listaFiltrosFinanceiro.get(0).ativo) ? Integer.valueOf(listaGrupo.get(idGrupo).getDescription()) : null);
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaSubGrupo.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }

        loadListaServicos();
    }

    public void loadListaServicos() {
        listaServicos.clear();
        idServicos = 0;

        // (listaFiltrosFinanceiro.get(1).ativo) SUB GRUPO
        List<Servicos> result = new RelatorioFinanceiroDao().listaServicosSubGrupo((listaFiltrosFinanceiro.get(1).ativo) ? Integer.valueOf(listaSubGrupo.get(idSubGrupo).getDescription()) : null);
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaServicos.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }
    }

    public void loadListaTipoCobranca() {
        listaTipoCobranca.clear();
        idTipoCobranca = 0;

        // (listaFiltrosFinanceiro.get(1).ativo) SUB GRUPO
        List<FTipoDocumento> result = new Dao().find("FTipoDocumento", new int[]{13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23});
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaTipoCobranca.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }
    }

    public String pesquisaPessoa() {
        GenericaSessao.put("linkClicado", true);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisaPessoa();
    }

    public String pesquisaCidadeSocio() {
        chkCidadeSocio = true;
        GenericaSessao.put("linkClicado", true);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).cidade();
    }

    public String pesquisaCidadeEmpresa() {
        chkCidadeSocio = false;
        GenericaSessao.put("linkClicado", true);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).cidade();
    }

    public void imprimir() {
        Relatorios relatorios = (Relatorios) new Dao().find(new Relatorios(), Integer.parseInt(listaRelatorio.get(idRelatorio).getDescription()));
        Integer id_grupo_categoria = null, id_categoria = null, id_parentesco = null, id_cidade_socio = null, id_cidade_empresa = null, id_tipo_cobranca = null;
        Boolean is_votante = null;
        String is_desc_folha_soc = null;
        String is_desc_folha_fin = null;
        String dtCadastro = "", dtRecadastro = "", dtAdmissao = "", dtDemissao = "", dtFiliacao = "", dtAposentadoria = "", dtAtualizacao = "";
        String dtCadastroFinal = "", dtRecadastroFinal = "", dtAdmissaoFinal = "", dtDemissaoFinal = "", dtFiliacaoFinal = "", dtAposentadoriaFinal = "", dtAtualizacaoFinal = "";
        String tipo_situacao = "";
        List<String> ldescricao = new ArrayList();

        // GRUPO / CATEGORIA
        if (listaFiltros.get(0).ativo) {
            id_grupo_categoria = Integer.valueOf(listaGrupoCategoria.get(idGrupoCategoria).getDescription());
            id_categoria = Integer.valueOf(listaCategoria.get(idCategoria).getDescription());
        }

        // PARENTESCO
        if (listaFiltros.get(1).ativo) {
            id_parentesco = Integer.valueOf(listaParentesco.get(idParentesco).getDescription());
        }

        // CIDADE DO SÓCIO
        if (listaFiltros.get(2).ativo) {
            if (cidadeSocio.getId() != -1) {
                id_cidade_socio = cidadeSocio.getId();
            }
        }

        // CIDADE DA EMPRESA
        if (listaFiltros.get(3).ativo) {
            if (cidadeEmpresa.getId() != -1) {
                id_cidade_empresa = cidadeEmpresa.getId();
            }
        }

        // VOTANTE
        if (listaFiltros.get(4).ativo) {
            is_votante = votante;
        }

        // DESCONTO FOLHA SOCIAL
        if (listaFiltros.get(8).ativo) {
            is_desc_folha_soc = descontoFolhaFinanceiro;
        }

        // DATAS
        if (listaFiltros.get(5).ativo) {
            // CADASTRO --------------
            if (!dataCadastro.isEmpty() && !dataCadastroFinal.isEmpty()) {
                ldescricao.add("Data de Cadastro de: " + dataCadastro + " à " + dataCadastroFinal);
                dtCadastro = dataCadastro;
                dtCadastroFinal = dataCadastroFinal;
            } else if (!dataCadastro.isEmpty() && dataCadastroFinal.isEmpty()) {
                ldescricao.add("Data de Cadastro: " + dataCadastro);
                dtCadastro = dataCadastro;
            } else if (dataCadastro.isEmpty() && !dataCadastroFinal.isEmpty()) {
                ldescricao.add("Data de Cadastro até: " + dataCadastroFinal);
                dtCadastroFinal = dataCadastroFinal;
            }

            // RECADASTRO --------------
            if (!dataRecadastro.isEmpty() && !dataRecadastroFinal.isEmpty()) {
                ldescricao.add("Data de Recadastro de: " + dataRecadastro + " à " + dataRecadastroFinal);
                dtRecadastro = dataRecadastro;
                dtRecadastroFinal = dataRecadastroFinal;
            } else if (!dataRecadastro.isEmpty() && dataRecadastroFinal.isEmpty()) {
                ldescricao.add("Data de Recadastro: " + dataRecadastro);
                dtRecadastro = dataRecadastro;
            } else if (dataRecadastro.isEmpty() && !dataRecadastroFinal.isEmpty()) {
                ldescricao.add("Data de Recadastro até: " + dataRecadastroFinal);
                dtRecadastroFinal = dataRecadastroFinal;
            }

            // ADMISSÃO --------------
            if (!dataAdmissao.isEmpty() && !dataAdmissaoFinal.isEmpty()) {
                ldescricao.add("Data de Admissão de: " + dataAdmissao + " à " + dataAdmissaoFinal);
                dtAdmissao = dataAdmissao;
                dtAdmissaoFinal = dataAdmissaoFinal;
            } else if (!dataAdmissao.isEmpty() && dataAdmissaoFinal.isEmpty()) {
                ldescricao.add("Data de Admissão: " + dataAdmissao);
                dtAdmissao = dataAdmissao;
            } else if (dataAdmissao.isEmpty() && !dataAdmissaoFinal.isEmpty()) {
                ldescricao.add("Data de Admissão até: " + dataAdmissaoFinal);
                dtAdmissaoFinal = dataAdmissaoFinal;
            }

            // DEMISSÃO --------------
            if (!dataDemissao.isEmpty() && !dataDemissaoFinal.isEmpty()) {
                ldescricao.add("Data de Demissão de: " + dataDemissao + " à " + dataDemissaoFinal);
                dtDemissao = dataDemissao;
                dtDemissaoFinal = dataDemissaoFinal;
            } else if (!dataDemissao.isEmpty() && dataDemissaoFinal.isEmpty()) {
                ldescricao.add("Data de Demissão: " + dataDemissao);
                dtDemissao = dataDemissao;
            } else if (dataDemissao.isEmpty() && !dataDemissaoFinal.isEmpty()) {
                ldescricao.add("Data de Demissão até: " + dataDemissaoFinal);
                dtDemissaoFinal = dataDemissaoFinal;
            }

            // FILIAÇÃO --------------
            if (!dataFiliacao.isEmpty() && !dataFiliacaoFinal.isEmpty()) {
                ldescricao.add("Data de Filiação de: " + dataFiliacao + " à " + dataFiliacaoFinal);
                dtFiliacao = dataFiliacao;
                dtFiliacaoFinal = dataFiliacaoFinal;
            } else if (!dataFiliacao.isEmpty() && dataFiliacaoFinal.isEmpty()) {
                ldescricao.add("Data de Filiação: " + dataFiliacao);
                dtFiliacao = dataFiliacao;
            } else if (dataFiliacao.isEmpty() && !dataFiliacaoFinal.isEmpty()) {
                ldescricao.add("Data de Filiação até: " + dataFiliacaoFinal);
                dtFiliacaoFinal = dataFiliacaoFinal;
            }

            // APOSENTADORIA --------------
            if (!dataAposentadoria.isEmpty() && !dataAposentadoriaFinal.isEmpty()) {
                ldescricao.add("Data de Aposentadoria de: " + dataAposentadoria + " à " + dataAposentadoriaFinal);
                dtAposentadoria = dataAposentadoria;
                dtAposentadoriaFinal = dataAposentadoriaFinal;
            } else if (!dataAposentadoria.isEmpty() && dataAposentadoriaFinal.isEmpty()) {
                ldescricao.add("Data de Aposentadoria: " + dataAposentadoria);
                dtFiliacao = dataAposentadoria;
            } else if (dataAposentadoria.isEmpty() && !dataAposentadoriaFinal.isEmpty()) {
                ldescricao.add("Data de Aposentadoria até: " + dataAposentadoriaFinal);
                dtAposentadoriaFinal = dataAposentadoriaFinal;
            }

            // ATUALIZAÇÃO --------------
            if (!dataAtualizacao.isEmpty() && !dataAtualizacaoFinal.isEmpty()) {
                ldescricao.add("Data de Atualização de: " + dataAtualizacao + " à " + dataAtualizacaoFinal);
                dtAtualizacao = dataAtualizacao;
                dtAtualizacaoFinal = dataAtualizacaoFinal;
            } else if (!dataAtualizacao.isEmpty() && dataAtualizacaoFinal.isEmpty()) {
                ldescricao.add("Data de Atualização: " + dataAtualizacao);
                dtAtualizacao = dataAtualizacao;
            } else if (dataAtualizacao.isEmpty() && !dataAtualizacaoFinal.isEmpty()) {
                ldescricao.add("Data de Atualização até: " + dataAtualizacaoFinal);
                dtAtualizacaoFinal = dataAtualizacaoFinal;
            }
        }

        // SITUAÇÃO
        if (listaFiltros.get(6).ativo) {
            tipo_situacao = tipoSituacao;
        }

        // PESSOA SÓCIO
        Integer id_pessoa = null;
        if (listaFiltros.get(7).ativo) {
            id_pessoa = pessoa.getId();
        }

        Integer id_grupo_financeiro = null, id_sub_grupo = null, id_servicos = null;
        // FILTROS FINANCEIRO ---------------------
        // GRUPO
        if (listaFiltrosFinanceiro.get(0).ativo) {
            id_grupo_financeiro = Integer.valueOf(listaGrupo.get(idGrupo).getDescription());
        }

        // SUB GRUPO
        if (listaFiltrosFinanceiro.get(1).ativo) {
            id_sub_grupo = Integer.valueOf(listaSubGrupo.get(idSubGrupo).getDescription());
        }

        // SERVIÇOS 
        if (listaFiltrosFinanceiro.get(2).ativo) {
            id_servicos = Integer.valueOf(listaServicos.get(idServicos).getDescription());
        }

        // TIPO DE COBRANÇA
        if (listaFiltrosFinanceiro.get(8).ativo) {
            id_tipo_cobranca = Integer.valueOf(listaTipoCobranca.get(idTipoCobranca).getDescription());
        }

        // DATAS
        String dtEmissao = "", dtVencimento = "", dtQuitacao = "";
        String dtEmissaoFinal = "", dtVencimentoFinal = "", dtQuitacaoFinal = "";
        if (listaFiltrosFinanceiro.get(3).ativo) {
            // EMISSÃO --------------
            if (!dataEmissao.isEmpty() && !dataEmissaoFinal.isEmpty()) {
                ldescricao.add("Data de Emissão de: " + dataEmissao + " à " + dataEmissaoFinal);
                dtEmissao = dataEmissao;
                dtEmissaoFinal = dataEmissaoFinal;
            } else if (!dataEmissao.isEmpty() && dataEmissaoFinal.isEmpty()) {
                ldescricao.add("Data de Emissão: " + dataEmissao);
                dtEmissao = dataEmissao;
            } else if (dataEmissao.isEmpty() && !dataEmissaoFinal.isEmpty()) {
                ldescricao.add("Data de Emissão até: " + dataEmissaoFinal);
                dtEmissaoFinal = dataEmissaoFinal;
            }

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

        // ENTRADA / SAIDA 
        String tipo_es = "";
        if (listaFiltrosFinanceiro.get(4).ativo) {
            tipo_es = tipoES;
        }

        // SITUAÇÃO FINANCEIRO
        String tipo_situacao_financeiro = "";
        if (listaFiltrosFinanceiro.get(5).ativo) {
            tipo_situacao_financeiro = tipoSituacaoFinanceiro;
        }

        // DEPARTAMENTO
        String tipo_departamento = "";
        if (listaFiltrosFinanceiro.get(6).ativo) {
            tipo_departamento = tipoDepartamento;
        }

        // TIPO PESSOA
        String tipo_pessoa = "";
        if (listaFiltrosFinanceiro.get(7).ativo) {
            tipo_pessoa = tipoPessoaFinanceiro;
        }

        if (listaFiltrosFinanceiro.get(9).ativo) {
            is_desc_folha_fin = descontoFolhaFinanceiro;
        }

        Map params = new HashMap();
        // MOEDA PARA BRASIL VALORES IREPORT PTBR CONVERTE VALOR JASPER PTBR MOEDA
        params.put("REPORT_LOCALE", new Locale("pt", "BR"));

        String descricaoData = "";
        for (String linha : ldescricao) {
            if (descricaoData.isEmpty()) {
                descricaoData = linha;
            } else {
                descricaoData += ", " + linha;
            }
        }
        params.put("descricao_data", descricaoData);
        params.put("logo_sindicato", ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));

        String ordem = "";
        if (!listaRelatorioOrdem.isEmpty()) {
            ordem = ((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), Integer.valueOf(listaRelatorioOrdem.get(idRelatorioOrdem).getDescription()))).getQuery();
        }

        List<Object> result = new RelatorioFinanceiroSocialDao().listaRelatorioFinanceiroSocial(id_grupo_categoria, id_categoria, id_parentesco, id_cidade_socio, id_cidade_empresa, is_votante, dtCadastro, dtCadastroFinal, dtRecadastro, dtRecadastroFinal, dtAdmissao, dtAdmissaoFinal, dtDemissao, dtDemissaoFinal, dtFiliacao, dtFiliacaoFinal, dtAposentadoria, dtAposentadoriaFinal, dtAtualizacao, dtAtualizacaoFinal, tipo_situacao, tipoPessoa, id_pessoa, id_grupo_financeiro, id_sub_grupo, id_servicos, id_tipo_cobranca, dtEmissao, dtEmissaoFinal, dtVencimento, dtVencimentoFinal, dtQuitacao, dtQuitacaoFinal, tipo_es, tipo_situacao_financeiro, tipo_departamento, tipo_pessoa, is_desc_folha_soc, is_desc_folha_fin, ordem, relatorios);

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
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), list_hash, params);

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

    /**
     * Lista de Filtros (indices)
     * <p>
     * 0 GRUPO / CATEGORIA
     * <br />1 GRAU
     * <br />2 CIDADE DO SÓCIO
     * <br />3 CIDADE DA EMPRESA
     * <br />4 VOTANTE
     * <br />5 DATAS
     * <br />6 SITUAÇÃO
     * <br />7 PESSOA
     * <br />8 TIPO COBRANÇA
     * <br />9 DESCONTO EM FOLHA FINANCEIRO
     *
     * @return Lista de Filtros
     */
    public List<Filtros> getListaFiltros() {
        return listaFiltros;
    }

    public void setListaFiltros(List<Filtros> listaFiltros) {
        this.listaFiltros = listaFiltros;
    }

    public Boolean getChkExcel() {
        return chkExcel;
    }

    public void setChkExcel(Boolean chkExcel) {
        this.chkExcel = chkExcel;
    }

    public Integer getIdGrupoCategoria() {
        return idGrupoCategoria;
    }

    public void setIdGrupoCategoria(Integer idGrupoCategoria) {
        this.idGrupoCategoria = idGrupoCategoria;
    }

    public List<SelectItem> getListaGrupoCategoria() {
        return listaGrupoCategoria;
    }

    public void setListaGrupoCategoria(List<SelectItem> listaGrupoCategoria) {
        this.listaGrupoCategoria = listaGrupoCategoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public List<SelectItem> getListaCategoria() {
        return listaCategoria;
    }

    public void setListaCategoria(List<SelectItem> listaCategoria) {
        this.listaCategoria = listaCategoria;
    }

    public Integer getIdParentesco() {
        return idParentesco;
    }

    public void setIdParentesco(Integer idParentesco) {
        this.idParentesco = idParentesco;
    }

    public List<SelectItem> getListaParentesco() {
        return listaParentesco;
    }

    public void setListaParentesco(List<SelectItem> listaParentesco) {
        this.listaParentesco = listaParentesco;
    }

    public Boolean getChkCidadeSocio() {
        return chkCidadeSocio;
    }

    public void setChkCidadeSocio(Boolean chkCidadeSocio) {
        this.chkCidadeSocio = chkCidadeSocio;
    }

    public Cidade getCidadeSocio() {
        if (GenericaSessao.exists("cidadePesquisa") && chkCidadeSocio) {
            cidadeSocio = (Cidade) GenericaSessao.getObject("cidadePesquisa", true);
            chkCidadeSocio = true;
        }
        return cidadeSocio;
    }

    public void setCidadeSocio(Cidade cidadeSocio) {
        this.cidadeSocio = cidadeSocio;
    }

    public Cidade getCidadeEmpresa() {
        if (GenericaSessao.exists("cidadePesquisa") && !chkCidadeSocio) {
            cidadeEmpresa = (Cidade) GenericaSessao.getObject("cidadePesquisa", true);
            chkCidadeSocio = true;
        }
        return cidadeEmpresa;
    }

    public void setCidadeEmpresa(Cidade cidadeEmpresa) {
        this.cidadeEmpresa = cidadeEmpresa;
    }

    public Boolean getVotante() {
        return votante;
    }

    public void setVotante(Boolean votante) {
        this.votante = votante;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDataCadastroFinal() {
        return dataCadastroFinal;
    }

    public void setDataCadastroFinal(String dataCadastroFinal) {
        this.dataCadastroFinal = dataCadastroFinal;
    }

    public String getDataRecadastro() {
        return dataRecadastro;
    }

    public void setDataRecadastro(String dataRecadastro) {
        this.dataRecadastro = dataRecadastro;
    }

    public String getDataRecadastroFinal() {
        return dataRecadastroFinal;
    }

    public void setDataRecadastroFinal(String dataRecadastroFinal) {
        this.dataRecadastroFinal = dataRecadastroFinal;
    }

    public String getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(String dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getDataAdmissaoFinal() {
        return dataAdmissaoFinal;
    }

    public void setDataAdmissaoFinal(String dataAdmissaoFinal) {
        this.dataAdmissaoFinal = dataAdmissaoFinal;
    }

    public String getDataDemissao() {
        return dataDemissao;
    }

    public void setDataDemissao(String dataDemissao) {
        this.dataDemissao = dataDemissao;
    }

    public String getDataDemissaoFinal() {
        return dataDemissaoFinal;
    }

    public void setDataDemissaoFinal(String dataDemissaoFinal) {
        this.dataDemissaoFinal = dataDemissaoFinal;
    }

    public String getDataFiliacao() {
        return dataFiliacao;
    }

    public void setDataFiliacao(String dataFiliacao) {
        this.dataFiliacao = dataFiliacao;
    }

    public String getDataFiliacaoFinal() {
        return dataFiliacaoFinal;
    }

    public void setDataFiliacaoFinal(String dataFiliacaoFinal) {
        this.dataFiliacaoFinal = dataFiliacaoFinal;
    }

    public String getDataAposentadoria() {
        return dataAposentadoria;
    }

    public void setDataAposentadoria(String dataAposentadoria) {
        this.dataAposentadoria = dataAposentadoria;
    }

    public String getDataAposentadoriaFinal() {
        return dataAposentadoriaFinal;
    }

    public void setDataAposentadoriaFinal(String dataAposentadoriaFinal) {
        this.dataAposentadoriaFinal = dataAposentadoriaFinal;
    }

    public String getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(String dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getDataAtualizacaoFinal() {
        return dataAtualizacaoFinal;
    }

    public void setDataAtualizacaoFinal(String dataAtualizacaoFinal) {
        this.dataAtualizacaoFinal = dataAtualizacaoFinal;
    }

    public String getTipoSituacao() {
        return tipoSituacao;
    }

    public void setTipoSituacao(String tipoSituacao) {
        this.tipoSituacao = tipoSituacao;
    }

    /**
     * Lista de Filtros (indices)
     * <p>
     * 0 GRUPO 1 SUB GRUPO
     * <br />2 SERVIÇOS
     * <br />3 DATAS
     * <br />4 E / S
     * <br />5 SITUAÇÃO FINANCEIRO
     * <br />6 DEPARTAMENTO
     * <br />7 TIPO PESSOA
     * <br />8 TIPO COBRANÇA
     *
     * @return Lista de Filtros
     */
    public List<Filtros> getListaFiltrosFinanceiro() {
        return listaFiltrosFinanceiro;
    }

    public void setListaFiltrosFinanceiro(List<Filtros> listaFiltrosFinanceiro) {
        this.listaFiltrosFinanceiro = listaFiltrosFinanceiro;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public List<SelectItem> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<SelectItem> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

    public Integer getIdSubGrupo() {
        return idSubGrupo;
    }

    public void setIdSubGrupo(Integer idSubGrupo) {
        this.idSubGrupo = idSubGrupo;
    }

    public List<SelectItem> getListaSubGrupo() {
        return listaSubGrupo;
    }

    public void setListaSubGrupo(List<SelectItem> listaSubGrupo) {
        this.listaSubGrupo = listaSubGrupo;
    }

    public Integer getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(Integer idServicos) {
        this.idServicos = idServicos;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public void setListaServicos(List<SelectItem> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataEmissaoFinal() {
        return dataEmissaoFinal;
    }

    public void setDataEmissaoFinal(String dataEmissaoFinal) {
        this.dataEmissaoFinal = dataEmissaoFinal;
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

    public String getTipoES() {
        return tipoES;
    }

    public void setTipoES(String tipoES) {
        this.tipoES = tipoES;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getTipoSituacaoFinanceiro() {
        return tipoSituacaoFinanceiro;
    }

    public void setTipoSituacaoFinanceiro(String tipoSituacaoFinanceiro) {
        this.tipoSituacaoFinanceiro = tipoSituacaoFinanceiro;
    }

    public String getTipoDepartamento() {
        return tipoDepartamento;
    }

    public void setTipoDepartamento(String tipoDepartamento) {
        this.tipoDepartamento = tipoDepartamento;
    }

    public String getTipoPessoaFinanceiro() {
        return tipoPessoaFinanceiro;
    }

    public void setTipoPessoaFinanceiro(String tipoPessoaFinanceiro) {
        this.tipoPessoaFinanceiro = tipoPessoaFinanceiro;
    }

    public List<SelectItem> getListaTipoCobranca() {
        return listaTipoCobranca;
    }

    public void setListaTipoCobranca(List<SelectItem> listaTipoCobranca) {
        this.listaTipoCobranca = listaTipoCobranca;
    }

    public Integer getIdTipoCobranca() {
        return idTipoCobranca;
    }

    public void setIdTipoCobranca(Integer idTipoCobranca) {
        this.idTipoCobranca = idTipoCobranca;
    }

    public String getDescontoFolhaSocio() {
        return descontoFolhaSocio;
    }

    public void setDescontoFolhaSocio(String descontoFolhaSocio) {
        this.descontoFolhaSocio = descontoFolhaSocio;
    }

    public String getDescontoFolhaFinanceiro() {
        return descontoFolhaFinanceiro;
    }

    public void setDescontoFolhaFinanceiro(String descontoFolhaFinanceiro) {
        this.descontoFolhaFinanceiro = descontoFolhaFinanceiro;
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
}
