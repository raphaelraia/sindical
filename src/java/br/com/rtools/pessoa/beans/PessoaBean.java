package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.Empregados;
import br.com.rtools.arrecadacao.beans.RaisBean;
import br.com.rtools.arrecadacao.beans.WebREPISBean;
import br.com.rtools.arrecadacao.dao.EmpregadosDao;
import br.com.rtools.associativo.DeclaracaoPeriodo;
import br.com.rtools.associativo.ExameMedico;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.Suspencao;
import br.com.rtools.associativo.beans.CupomMovimentoBean;
import br.com.rtools.associativo.beans.FrequenciaCatracaBean;
import br.com.rtools.associativo.beans.SorteioMovimentoBean;
import br.com.rtools.associativo.dao.CampeonatoDao;
import br.com.rtools.associativo.dao.DeclaracaoPessoaDao;
import br.com.rtools.associativo.dao.ExameMedicoDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.SuspencaoDao;
import br.com.rtools.cobranca.beans.TmktHistoricoBean;
import br.com.rtools.digitalizacao.beans.DigitalizacaoBean;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.SelectItemSort;
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
public class PessoaBean implements Serializable {

    private Pessoa pessoa;
    private Fisica fisica;
    private Juridica juridica;
    private String descPesquisa;
    private String porPesquisa;
    private String comoPesquisa;
    private String masc;
    private String maxl;
    private List<Pessoa> listaPessoa;
    private List<SelectItem> listSelectDetalhes;
    private String selectDetalhes;
    private String tipoPessoa;
    private List<Socios> listMatriculasInativas;
    private List<Agendamento> listHomologacao;
    private List<Suspencao> listSuspensao;
    private List<DeclaracaoPessoaObject> listDeclaracaoPessoa;
    private String tipoDeclaracaoPessoa;
    private String ano;
    private String referencia;
    private Integer referenciaId;
    private List<SelectItem> listAnos;
    private List<SelectItem> listReferencia;
    private String situacaoFuncionario;
    private List<PessoaEmpresa> listFuncionarios;
    private List<Empregados> listEmpregados;
    private List<SelectItem> listAnoDeclaracaoAnualDebitos;
    private String anoDeclaracaoAnualDebitos;
    private List<ExameMedico> listExameMedico;
    private List<CampeonatoObject> listCampeonato;
    private Boolean campeonatoAtivo;

    @PostConstruct
    public void init() {
        campeonatoAtivo = true;
        pessoa = new Pessoa();
        fisica = new Fisica();
        juridica = new Juridica();
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        masc = "";
        maxl = "";
        ano = null;
        referencia = null;
        listaPessoa = new ArrayList();
        listSelectDetalhes = new ArrayList();
        listMatriculasInativas = new ArrayList();
        listHomologacao = new ArrayList();
        listAnos = null;
        listReferencia = null;
        listDeclaracaoPessoa = new ArrayList();
        tipoDeclaracaoPessoa = "empresa_conveniada";
        selectDetalhes = "";
        situacaoFuncionario = "ativos";
        listFuncionarios = new ArrayList();
        listCampeonato = new ArrayList();
        listExameMedico = new ArrayList();
        listEmpregados = new ArrayList();
        listAnoDeclaracaoAnualDebitos = new ArrayList();

        if (GenericaSessao.exists("tipoPessoa")) {
            tipoPessoa = GenericaSessao.getString("tipoPessoa", true);
            if (tipoPessoa.equals("pessoaFisica")) {
                // JURÍDICA BEAN PODE ESTA NA SESSÃO, O LINK VEIO DA FÍSICA
                GenericaSessao.remove("juridicaBean");
                fisica = ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getFisica();
                pessoa = fisica.getPessoa();
            }
            if (tipoPessoa.equals("pessoaJuridica")) {
                // FÍSICA BEAN PODE ESTA NA SESSÃO, O LINK VEIO DA JURÍDICA
                GenericaSessao.remove("fisicaBean");
                juridica = ((JuridicaBean) GenericaSessao.getObject("juridicaBean")).getJuridica();
                pessoa = juridica.getPessoa();
            }
            loadListSelectDetalhes();
            load();
        }
    }

    @PreDestroy
    public void destroy() {
        // GenericaSessao.remove("poessoaPesquisa");
        GenericaSessao.remove("pessoaBean");
//        GenericaSessao.remove("sorteioMovimentoBean");
//        GenericaSessao.remove("cupomMovimentoBean");
//        GenericaSessao.remove("tmktHistoricoBean");
//        GenericaSessao.remove("digitalizacaoBean");
//        GenericaSessao.remove("frequenciaCatracaBean");
//        GenericaSessao.remove("spcBean");
//        GenericaSessao.remove("webREPISBean");
    }

    public void load() {
        switch (selectDetalhes) {
            case "exame_medico":
                if (listExameMedico.isEmpty()) {
                    listExameMedico = new ExameMedicoDao().findByPessoa(pessoa.getId());
                }
                break;
            case "sorteios":
                GenericaSessao.remove("sorteioMovimentoBean");
                SorteioMovimentoBean sorteioMovimentoBean = new SorteioMovimentoBean();
                sorteioMovimentoBean.loadListSorteioMovimento(pessoa.getId());
                GenericaSessao.put("sorteioMovimentoBean", sorteioMovimentoBean);
                break;
            case "cupons":
                GenericaSessao.remove("cupomMovimentoBean");
                CupomMovimentoBean cupomMovimentoBean = new CupomMovimentoBean();
                cupomMovimentoBean.loadListCupomMovimento(pessoa.getId());
                GenericaSessao.put("cupomMovimentoBean", cupomMovimentoBean);
                break;
            case "telemarketing":
                GenericaSessao.remove("tmktHistoricoBean");
                TmktHistoricoBean tmktHistoricoBean = new TmktHistoricoBean();
                tmktHistoricoBean.loadListTmktHistorico(pessoa.getId());
                GenericaSessao.put("tmktHistoricoBean", tmktHistoricoBean);
                break;
            case "documentos":
                GenericaSessao.remove("digitalizacaoBean");
                DigitalizacaoBean digitalizacaoBean = new DigitalizacaoBean();
                digitalizacaoBean.loadListDocumentos(pessoa.getId());
                GenericaSessao.put("digitalizacaoBean", digitalizacaoBean);
                break;
            case "frequencia_catraca":
                GenericaSessao.remove("frequenciaCatracaBean");
                FrequenciaCatracaBean frequenciaCatracaBean = new FrequenciaCatracaBean();
                frequenciaCatracaBean.loadListByPessoa(pessoa.getId());
                GenericaSessao.put("frequenciaCatracaBean", frequenciaCatracaBean);
                break;
            case "spc":
                GenericaSessao.remove("spcBean");
                SpcBean spcBean = new SpcBean();
                spcBean.setListaSPC(new ArrayList());
                spcBean.loadListByPessoa(pessoa.getId());
                GenericaSessao.put("spcBean", spcBean);
                break;
            case "repis":
                GenericaSessao.remove("webREPISBean");
                WebREPISBean webREPISBean = new WebREPISBean();
                webREPISBean.loadListRepisMovimentoPessoa(pessoa.getId());
                GenericaSessao.put("webREPISBean", webREPISBean);
                break;
            case "rais":
                GenericaSessao.remove("raisBean");
                RaisBean raisBean = new RaisBean();
                if (tipoPessoa.equals("pessoaJuridica")) {
                    raisBean.loadListRaisEmpresa(pessoa.getId());
                }
                if (tipoPessoa.equals("pessoaFisica")) {
                    if (!pessoa.getDocumento().isEmpty()) {
                        raisBean.loadListRaisPessoa(pessoa.getDocumento());
                    }
                }
                GenericaSessao.put("raisBean", raisBean);
                break;
            case "matriculas":
                listMatriculasInativas = new ArrayList();
                listMatriculasInativas = new SociosDao().pesquisaHistoricoDeInativacao(pessoa.getId());
                break;
            case "homologacao_funcionario":
                listHomologacao = new ArrayList();
                listHomologacao = new HomologacaoDao().pesquisaPorFuncionario(pessoa.getId());
                break;
            case "homologacao_empresa":
                listHomologacao = new ArrayList();
                listHomologacao = new HomologacaoDao().pesquisaPorEmpresa(pessoa.getId());
                break;
            case "suspencao":
                listSuspensao = new ArrayList();
                listSuspensao = new SuspencaoDao().pesquisaSuspensao((Integer) pessoa.getId());
                break;
            case "declaracao":
                descPesquisa = "";
                loadListAnoDeclaracaoPessoa();
                loadListPeriodoDeclaracaoPessoa();
                loadListDeclaracaoPessoa();
                break;
            case "funcionarios":
                loadListFuncionarios();
                break;
            case "declaracao_anual_debitos":
                listAnoDeclaracaoAnualDebitos = new ArrayList();
                anoDeclaracaoAnualDebitos = "";
                List<String> list = new PessoaDao().listAnoDeclaracaoAnualDebitos(pessoa.getId());
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        anoDeclaracaoAnualDebitos = list.get(i);
                    }
                    listAnoDeclaracaoAnualDebitos.add(new SelectItem(list.get(i), list.get(i), list.get(i)));
                }
                break;
            case "campeonato":
                campeonatoAtivo = true;
                loadListCampeonato();
                break;
        }
    }

    public void loadListCampeonato() {
        listCampeonato = new ArrayList();
        List listResult = new CampeonatoDao().findByPessoaDetalhes(pessoa.getId(), campeonatoAtivo);

        for (int i = 0; i < listResult.size(); i++) {
            List o = (List) listResult.get(i);
            listCampeonato.add(
                    new CampeonatoObject(
                            o.get(0),
                            o.get(1),
                            o.get(2),
                            o.get(3),
                            o.get(4),
                            o.get(5),
                            o.get(6),
                            o.get(7),
                            o.get(8),
                            o.get(9)
                    )
            );
        }
    }

    public void loadListDeclaracaoPessoa() {
        listDeclaracaoPessoa = new ArrayList();
        DeclaracaoPessoaDao dao = new DeclaracaoPessoaDao();
        List<List> listDeclaracao;
        if (tipoPessoa.equals("pessoaJuridica")) {
            // LISTA DECLARAÇÃO JURIDICA (CONVENIADA) ou (EMPRESA DA PESSOA)
            listDeclaracao = dao.find(tipoDeclaracaoPessoa, ano, referenciaId, pessoa.getId(), descPesquisa);
        } else {
            // LISTA DECLARAÇÃO FÍSICA (BENEFICIÁRIO)
            listDeclaracao = dao.find("pessoa_fisica", ano, referenciaId, pessoa.getId(), descPesquisa);
        }
        for (int i = 0; i < listDeclaracao.size(); i++) {
            List o = (List) listDeclaracao.get(i);
            listDeclaracaoPessoa.add(
                    new DeclaracaoPessoaObject(
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
                            o.get(10)
                    )
            );
        }
    }

    public void loadListAnoDeclaracaoPessoa() {
        ano = "";
        listAnos = new ArrayList();
        List list = new DeclaracaoPessoaDao().anos();
        for (int i = 0; i < list.size(); i++) {
            String a = ((List) list.get(i)).get(0).toString();
            if (i == 0) {
                ano = a;
            }
            listAnos.add(new SelectItem(a, a));
        }
    }

    public void listenerDeclaracao(String tcase) {
        switch (tcase) {
            case "tipo_declaracao_pessoa":
                descPesquisa = "";
                loadListAnoDeclaracaoPessoa();
                loadListPeriodoDeclaracaoPessoa();
                loadListDeclaracaoPessoa();
                break;
            case "ano":
                descPesquisa = "";
                loadListPeriodoDeclaracaoPessoa();
                loadListDeclaracaoPessoa();
                break;
            case "periodo":
                descPesquisa = "";
                loadListDeclaracaoPessoa();
                break;
            case "pessoa":
                loadListDeclaracaoPessoa();
                break;
            default:
                break;
        }

    }

    public void loadListPeriodoDeclaracaoPessoa() {
        referenciaId = null;
        listReferencia = new ArrayList();
        List<DeclaracaoPeriodo> list = new DeclaracaoPessoaDao().periodo(ano);
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                listReferencia.add(new SelectItem(null, "Todos períodos"));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                referenciaId = list.get(i).getId();
            }
            listReferencia.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListaPessoa() {
        listaPessoa.clear();
        PessoaDao pesquisa = new PessoaDao();
        if (!descPesquisa.isEmpty()) {
            listaPessoa = pesquisa.pesquisarPessoa(descPesquisa, porPesquisa, comoPesquisa);
        }
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getMasc() {
        return masc;
    }

    public void setMasc(String masc) {
        this.masc = masc;
    }

    public String salvar() {
        Dao di = new Dao();
        if (pessoa.getId() == -1) {
            di.openTransaction();
            if (di.save(pessoa)) {
                di.commit();
            } else {
                di.rollback();
            }
        } else {
            di.openTransaction();
            if (di.update(pessoa)) {
                di.commit();
            } else {
                di.rollback();
            }
        }
        return null;
    }

    public String novo() {
        setPessoa(pessoa = new Pessoa());
        return "pessoa";
    }

    public String excluir() {
        Dao di = new Dao();
        if (pessoa.getId() != -1) {
            di.openTransaction();
            if (di.delete(pessoa)) {
                di.commit();
            } else {
                di.rollback();
            }
        }
        setPessoa(pessoa = new Pessoa());
        return "pesquisaPessoa";
    }

    public void CarregarPessoa() {
    }

    public void refreshForm() {
    }

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
        loadListaPessoa();
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
        loadListaPessoa();
    }

    public String pesquisarPessoa() {
        GenericaSessao.put("urlRetorno", "agenda");
        return "pesquisaPessoa";
    }

    public synchronized String editar(Pessoa p) {
        pessoa = p;
        GenericaSessao.put("pessoaPesquisa", pessoa);
        GenericaSessao.put("linkClicado", true);
        pessoa = new Pessoa();
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        if (GenericaSessao.exists("urlRetorno")) {
            return GenericaSessao.getString("urlRetorno");
        } else {
            return null;
        }
    }

    public String getColocarMascara() {
        if (porPesquisa.equals("telefone1")) {
            masc = "telefone";
        } else {
            masc = "";
        }
        return masc;
    }

    public String getColocarMaxlenght() {
        if (porPesquisa.equals("telefone1")) {
            maxl = "14";
        } else {
            maxl = "50";
        }
        return maxl;
    }

    public List<Pessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<Pessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public String getMascaraPesquisa() {
        return Mask.getMascaraPesquisa(porPesquisa, true);
    }

    public void limparMascara() {
        descPesquisa = "";
    }

    public List<SelectItem> getListSelectDetalhes() {
        return listSelectDetalhes;
    }

    public void setListSelectDetalhes(List<SelectItem> listSelectDetalhes) {
        this.listSelectDetalhes = listSelectDetalhes;
    }

    public void loadListSelectDetalhes() {
        ControleAcessoBean cab = new ControleAcessoBean();
        if (tipoPessoa.isEmpty()) {
            return;
        }
        // PESSOA FÍSICA
        if (tipoPessoa.equals("pessoaFisica")) {
            listSelectDetalhes.add(new SelectItem("cupons", "Cupons", "CONSULTA CUPONS (PESSOA FÍSICA)", cab.verificarPermissao("consulta_cupons", 4)));
            listSelectDetalhes.add(new SelectItem("frequencia_catraca", "Frequência Catraca", "CONSULTA FREQUÊNCIA CATRACA", cab.verificarPermissao("consulta_frequencia_catraca", 4)));
            listSelectDetalhes.add(new SelectItem("historico_fisica", "Histórico", "CONSULTA HISTÓRICO (PESSOA FÍSICA)", cab.verificarPermissao("consulta_historico_fisica", 4)));
            listSelectDetalhes.add(new SelectItem("homologacao_funcionario", "Homologação", "CONSULTA HOMOLOGAÇÃO", cab.verificarPermissao("homologacao_funcionario", 4)));
            listSelectDetalhes.add(new SelectItem("matriculas", "Matrículas", "CONSULTA MATRÍCULAS", cab.verificarPermissao("consulta_matriculas", 4)));
            listSelectDetalhes.add(new SelectItem("movimentos_fisica", "Movimentos", "CONSULTA MOVIMENTOS (PESSOA FÍSICA)", cab.verificarPermissao("consulta_movimentos_fisica", 4)));
            listSelectDetalhes.add(new SelectItem("oposicoes", "Oposições", "CONSULTA OPOSIÇÃO (PESSOA FÍSICA)", cab.verificarPermissao("consulta_oposicao_pessoa_fisica", 4)));
            listSelectDetalhes.add(new SelectItem("sorteios", "Sorteios", "CONSULTA SORTEIOS (PESSOA FÍSICA)", cab.verificarPermissao("consulta_sorteios", 4)));
            listSelectDetalhes.add(new SelectItem("suspencao", "Suspenção", "SUSPENÇÃO", cab.verificarPermissao("consulta_suspencao", 4)));
            listSelectDetalhes.add(new SelectItem("declaracao_anual_debitos", "Dec. Anual de Débitos ", "DECLARAÇÃO ANUAL DE DÉBICOS", cab.verificarPermissao("declaracao_anual_debitos", 4)));
            listSelectDetalhes.add(new SelectItem("exame_medico", "Exame Médico", "CONSULTA EXAME MÉDICO (PESSOA FÍSICA)", cab.verificarPermissao("consulta_exame_medico", 4)));
            listSelectDetalhes.add(new SelectItem("campeonato", "Campeonato", "CONSULTA CAMPEONATO", cab.verificarPermissao("campeonato", 4)));
        }
        // PESSOA JURÍDICA
        if (tipoPessoa.equals("pessoaJuridica")) {
            listSelectDetalhes.add(new SelectItem("repis", "Repis", "CONSULTA REPIS (PESSOA JURÍDICA)", cab.verificarPermissao("consulta_repis", 4)));
            listSelectDetalhes.add(new SelectItem("homologacao_empresa", "Homologação", "CONSULTA HOMOLOGAÇÃO", cab.verificarPermissao("homologacao_empresa", 4)));
            listSelectDetalhes.add(new SelectItem("funcionarios", "Funcionários", "FUNCIONÁRIOS", cab.verificarPermissao("consulta_funcionarios", 4)));
        }
        // PESSOA FÍSICA E JURÍDICA
        if (tipoPessoa.equals("pessoaFisica") || tipoPessoa.equals("pessoaJuridica")) {
            listSelectDetalhes.add(new SelectItem("telemarketing", "Telemarketing", "CONSULTA ATENDIMENTOS TELEMARKETING (PESSOA FÍSICA E JURÍDICA)", cab.verificarPermissao("consulta_telemarketing", 4)));
            listSelectDetalhes.add(new SelectItem("documentos", "Documentos", "CONSULTA DOCUMENTOS (PESSOA FÍSICA E JURÍDICA)", cab.verificarPermissao("consulta_documentos", 4)));
            listSelectDetalhes.add(new SelectItem("spc", "SPC", "CONSULTA PESSOA SPC", cab.verificarPermissao("consulta_pessoa_spc", 4)));
            listSelectDetalhes.add(new SelectItem("rais", "RAIS", "CONSULTA RAIS", cab.verificarPermissao("consulta_rais", 4)));
            listSelectDetalhes.add(new SelectItem("declaracao", "Declaração", "DECLARAÇÃO", cab.verificarPermissao("consulta_declaracao", 4)));
        }
        if (!listSelectDetalhes.isEmpty()) {
            SelectItemSort.sort(listSelectDetalhes);
            int y = 0;
            selectDetalhes = "";
            for (int i = 0; i < listSelectDetalhes.size(); i++) {
                if (!listSelectDetalhes.get(i).isDisabled()) {
                    if (y == 0) {
                        selectDetalhes = "" + listSelectDetalhes.get(i).getValue();
                        break;
                    }
                }

            }
        }
    }

    public void loadListFuncionarios() {
        listFuncionarios = new ArrayList();
        PessoaEmpresaDao pessoaEmpresaDao = new PessoaEmpresaDao();
        switch (situacaoFuncionario) {
            case "todos":
                listFuncionarios = pessoaEmpresaDao.findAllByPessoa(pessoa.getId(), null);
                break;
            case "ativos":
                listFuncionarios = pessoaEmpresaDao.findAllByPessoa(pessoa.getId(), false);
                break;
            default:
                listFuncionarios = pessoaEmpresaDao.findAllByPessoa(pessoa.getId(), true);
                break;
        }
        // MOSTRA A QTDE QUE É REGISTRADA/SOLICITADA DE EMPREGADOS NO ACESSO WEB A CADA PERÍODO DEFINIDO PELO CLIENTE OU MENSAL
        listEmpregados = new ArrayList();
        listEmpregados = new EmpregadosDao().findByJuridica(pessoa.getJuridica().getId(), false);
    }

    public String getSelectDetalhes() {
        return selectDetalhes;
    }

    public void setSelectDetalhes(String selectDetalhes) {
        this.selectDetalhes = selectDetalhes;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public List<Socios> getListMatriculasInativas() {
        return listMatriculasInativas;
    }

    public void setListMatriculasInativas(List<Socios> listMatriculasInativas) {
        this.listMatriculasInativas = listMatriculasInativas;
    }

    public List<Agendamento> getListHomologacao() {
        return listHomologacao;
    }

    public void setListHomologacao(List<Agendamento> listHomologacao) {
        this.listHomologacao = listHomologacao;
    }

    public List<Suspencao> getListSuspensao() {
        return listSuspensao;
    }

    public void setListSuspensao(List<Suspencao> listSuspensao) {
        this.listSuspensao = listSuspensao;
    }

    public List<DeclaracaoPessoaObject> getListDeclaracaoPessoa() {
        return listDeclaracaoPessoa;
    }

    public void setListDeclaracaoPessoa(List<DeclaracaoPessoaObject> listDeclaracaoPessoa) {
        this.listDeclaracaoPessoa = listDeclaracaoPessoa;
    }

    public String getTipoDeclaracaoPessoa() {
        return tipoDeclaracaoPessoa;
    }

    public void setTipoDeclaracaoPessoa(String tipoDeclaracaoPessoa) {
        this.tipoDeclaracaoPessoa = tipoDeclaracaoPessoa;
    }

    public String getSituacaoFuncionario() {
        return situacaoFuncionario;
    }

    public void setSituacaoFuncionario(String situacaoFuncionario) {
        this.situacaoFuncionario = situacaoFuncionario;
    }

    public List<PessoaEmpresa> getListFuncionarios() {
        return listFuncionarios;
    }

    public void setListFuncionarios(List<PessoaEmpresa> listFuncionarios) {
        this.listFuncionarios = listFuncionarios;
    }

    public List<SelectItem> getListAnoDeclaracaoAnualDebitos() {
        return listAnoDeclaracaoAnualDebitos;
    }

    public void setListAnoDeclaracaoAnualDebitos(List<SelectItem> listAnoDeclaracaoAnualDebitos) {
        this.listAnoDeclaracaoAnualDebitos = listAnoDeclaracaoAnualDebitos;
    }

    public String getAnoDeclaracaoAnualDebitos() {
        return anoDeclaracaoAnualDebitos;
    }

    public void setAnoDeclaracaoAnualDebitos(String anoDeclaracaoAnualDebitos) {
        this.anoDeclaracaoAnualDebitos = anoDeclaracaoAnualDebitos;
    }

    public void printDeclaracaoAnualDebitos() {
        if (anoDeclaracaoAnualDebitos == null || anoDeclaracaoAnualDebitos.isEmpty()) {
            return;
        }
        if (new PessoaDao().existDeclaracaoAnualDebitos(anoDeclaracaoAnualDebitos, pessoa.getId())) {
            GenericaMensagem.warn("MENSAGEM", "CONSTAM DÉBITOS PARA ESTE ANO!");
            return;
        }
        Map map = new HashMap();
        map.put("titulo", "DECLARAÇÃO ANUAL DE SITUAÇÃO DE DÉBITOS");
        map.put("pessoa_nome", pessoa.getNome());
        map.put("pessoa_documento", pessoa.getDocumento());
        map.put("ano", anoDeclaracaoAnualDebitos);
        Jasper.FILIAL = (Filial) new Dao().find(new Filial(), 1);
        Jasper.IS_HEADER = true;
        Jasper.printReports("/Relatorios/DECLARACAO_ANUAL_DEBITOS.jasper", "Declaração anual de débitos", new ArrayList(), map);
        Jasper.IS_HEADER = false;
        Jasper.FILIAL = null;
    }

    public List<ExameMedico> getListExameMedico() {
        return listExameMedico;
    }

    public void setListExameMedico(List<ExameMedico> listExameMedico) {
        this.listExameMedico = listExameMedico;
    }

    public List<Empregados> getListEmpregados() {
        return listEmpregados;
    }

    public void setListEmpregados(List<Empregados> listEmpregados) {
        this.listEmpregados = listEmpregados;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public List<SelectItem> getListAnos() {
        return listAnos;
    }

    public void setListAnos(List<SelectItem> listAnos) {
        this.listAnos = listAnos;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Integer referenciaId) {
        this.referenciaId = referenciaId;
    }

    public List<SelectItem> getListReferencia() {
        return listReferencia;
    }

    public void setListReferencia(List<SelectItem> listReferencia) {
        this.listReferencia = listReferencia;
    }

    public List<CampeonatoObject> getListCampeonato() {
        return listCampeonato;
    }

    public void setListCampeonato(List<CampeonatoObject> listCampeonato) {
        this.listCampeonato = listCampeonato;
    }

    public Boolean getCampeonatoAtivo() {
        return campeonatoAtivo;
    }

    public void setCampeonatoAtivo(Boolean campeonatoAtivo) {
        this.campeonatoAtivo = campeonatoAtivo;
    }

    public class DeclaracaoPessoaObject {

//        Cadastro de Pessoa Juridica (Emitidas Para os Funcionários Desta Empresa) ----> Trocar nome da coluna Titular para Funcionário
        private Object funcionario;
//        Somente pessoa física e (Emitidas para esta Empresa)
        private Object titular;
//        Beneficiário
        private Object beneficiario;
        private Object categoria;
        private Object matricula;
        private Object tipo;
        private Object periodo;
        private Object cnpj;
        private Object convenio;
        private Object id_declaracao;
        private Object emissao;

        public DeclaracaoPessoaObject() {
            this.funcionario = null;
            this.titular = null;
            this.beneficiario = null;
            this.categoria = null;
            this.matricula = null;
            this.tipo = null;
            this.periodo = null;
            this.cnpj = null;
            this.convenio = null;
            this.id_declaracao = null;
            this.emissao = null;
        }

        public DeclaracaoPessoaObject(Object funcionario, Object titular, Object beneficiario, Object categoria, Object matricula, Object tipo, Object periodo, Object cnpj, Object convenio, Object id_declaracao, Object emissao) {
            this.funcionario = funcionario;
            this.titular = titular;
            this.beneficiario = beneficiario;
            this.categoria = categoria;
            this.matricula = matricula;
            this.tipo = tipo;
            this.periodo = periodo;
            this.cnpj = cnpj;
            this.convenio = convenio;
            this.id_declaracao = id_declaracao;
            this.emissao = emissao;
        }

        public Object getFuncionario() {
            return funcionario;
        }

        public void setFuncionario(Object funcionario) {
            this.funcionario = funcionario;
        }

        public Object getTitular() {
            return titular;
        }

        public void setTitular(Object titular) {
            this.titular = titular;
        }

        public Object getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Object beneficiario) {
            this.beneficiario = beneficiario;
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

        public Object getTipo() {
            return tipo;
        }

        public void setTipo(Object tipo) {
            this.tipo = tipo;
        }

        public Object getPeriodo() {
            return periodo;
        }

        public void setPeriodo(Object periodo) {
            this.periodo = periodo;
        }

        public Object getCnpj() {
            return cnpj;
        }

        public void setCnpj(Object cnpj) {
            this.cnpj = cnpj;
        }

        public Object getConvenio() {
            return convenio;
        }

        public void setConvenio(Object convenio) {
            this.convenio = convenio;
        }

        public Object getId_declaracao() {
            return id_declaracao;
        }

        public void setId_declaracao(Object id_declaracao) {
            this.id_declaracao = id_declaracao;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

    }

    public class CampeonatoObject {

        private Object modalidade;
        private Object campeonato;
        private Object equipe;
        private Object inicio;
        private Object fim;
        private Object responsavel;
        private Object valor;
        private Object dependente;
        private Object parentesco;
        private Object valor_dependente;

        public CampeonatoObject(Object modalidade, Object campeonato, Object equipe, Object inicio, Object fim, Object responsavel, Object valor, Object dependente, Object parentesco, Object valor_dependente) {
            this.modalidade = modalidade;
            this.campeonato = campeonato;
            this.equipe = equipe;
            this.inicio = inicio;
            this.fim = fim;
            this.responsavel = responsavel;
            this.valor = valor;
            this.dependente = dependente;
            this.parentesco = parentesco;
            this.valor_dependente = valor_dependente;
        }

        public Object getModalidade() {
            return modalidade;
        }

        public void setModalidade(Object modalidade) {
            this.modalidade = modalidade;
        }

        public Object getCampeonato() {
            return campeonato;
        }

        public void setCampeonato(Object campeonato) {
            this.campeonato = campeonato;
        }

        public Object getEquipe() {
            return equipe;
        }

        public void setEquipe(Object equipe) {
            this.equipe = equipe;
        }

        public Object getInicio() {
            return inicio;
        }

        public void setInicio(Object inicio) {
            this.inicio = inicio;
        }

        public Object getFim() {
            return fim;
        }

        public void setFim(Object fim) {
            this.fim = fim;
        }

        public Object getResponsavel() {
            return responsavel;
        }

        public void setResponsavel(Object responsavel) {
            this.responsavel = responsavel;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getDependente() {
            return dependente;
        }

        public void setDependente(Object dependente) {
            this.dependente = dependente;
        }

        public Object getParentesco() {
            return parentesco;
        }

        public void setParentesco(Object parentesco) {
            this.parentesco = parentesco;
        }

        public Object getValor_dependente() {
            return valor_dependente;
        }

        public void setValor_dependente(Object valor_dependente) {
            this.valor_dependente = valor_dependente;
        }

    }

}
