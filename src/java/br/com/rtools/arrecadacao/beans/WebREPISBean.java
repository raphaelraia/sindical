package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.CertidaoDisponivel;
import br.com.rtools.arrecadacao.CertidaoMensagem;
import br.com.rtools.arrecadacao.CertidaoTipo;
import br.com.rtools.arrecadacao.CertificadoArquivos;
import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.arrecadacao.Patronal;
import br.com.rtools.arrecadacao.PisoSalarial;
import br.com.rtools.arrecadacao.PisoSalarialLote;
import br.com.rtools.arrecadacao.RepisMovimento;
import br.com.rtools.arrecadacao.RepisStatus;
import br.com.rtools.arrecadacao.dao.CertificadoArquivosDao;
import br.com.rtools.arrecadacao.dao.ConvencaoPeriodoDao;
import br.com.rtools.arrecadacao.dao.EmpregadosDao;
import br.com.rtools.arrecadacao.dao.WebREPISDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.dao.CidadeDao;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.impressao.ParametroCertificado;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class WebREPISBean implements Serializable {

    private Pessoa pessoa = new Pessoa();
    private Endereco endereco = new Endereco();
    private PessoaEndereco pessoaEndereco = new PessoaEndereco();
    private Pessoa pessoaContribuinte = new Pessoa();
    private Pessoa pessoaContabilidade = new Pessoa();
    private Pessoa pessoaSolicitante = new Pessoa();
    private Pessoa escritorio = new Pessoa();
    private List<SelectItem> listComboPessoa = new ArrayList();
    private List<SelectItem> listComboRepisStatus = new ArrayList();
    private List<SelectItem> listComboCertidaoDisponivel = new ArrayList();
    private List<SelectItem> listConvencaoPeriodo = new ArrayList();
    private List<RepisMovimento> listRepisMovimento = new ArrayList();
    private List<RepisMovimento> listRepisMovimentoPatronal = new ArrayList();
    private List<RepisMovimento> listRepisMovimentoPatronalSelecionado = new ArrayList();
    private int idPessoa = 0;
    private int idRepisStatus = 0;
    private int indexCertidaoDisponivel = 0;
    private Integer idConvencaoPeriodo = null;
    private boolean renderContabil = false;
    private boolean renderEmpresa = false;
    private boolean showProtocolo = false;
    private RepisMovimento repisMovimento = new RepisMovimento();
    private String descPesquisa = "";
    private String porPesquisa = "nome";
    private String comoPesquisa = "";
    private String tipoPesquisa = "nome";
    private String descricao = "";
    private List listArquivosEnviados = new ArrayList();
    private String valueLenght = "15";
    private String contato = "";
    private ConfiguracaoArrecadacao configuracaoArrecadacao;
    private List<RepisMovimento> listRepisMovimentoPessoa;
    private Boolean uploadCertificado;
    private List<CertificadoArquivos> listCertificadoArquivos;

    private List<CertidaoDisponivel> listCertidaoDisponivelSolicitar = new ArrayList();
    private boolean chkTodasCertidoes = false;
    private ObjectFiltro objectFiltro = new ObjectFiltro();

    public WebREPISBean() {
        pessoa = new Pessoa();
        listRepisMovimentoPessoa = new ArrayList();
        UsuarioDao db = new UsuarioDao();
        getPessoa();
        pessoaContribuinte = db.ValidaUsuarioContribuinteWeb(pessoa.getId());
        pessoaContabilidade = db.ValidaUsuarioContabilidadeWeb(pessoa.getId());
        if (pessoaContribuinte != null && pessoaContabilidade != null) {
            renderEmpresa = false;
            renderContabil = true;
        } else if (pessoaContribuinte != null) {
            renderEmpresa = true;
            renderContabil = false;
        } else if (pessoaContabilidade != null) {
            renderEmpresa = false;
            renderContabil = true;
        } else {
            renderEmpresa = false;
            renderContabil = false;
        }
        configuracaoArrecadacao = ConfiguracaoArrecadacao.get();
        uploadCertificado = false;
        if (configuracaoArrecadacao.getUploadCertificado()) {
            if (pessoaContribuinte != null) {
                ConvencaoPeriodo cp = getConvencaoPeriodoEmpresa();
                loadListConvencaoPeriodo(cp);
                if (new EmpregadosDao().pesquisaQuantidadeEmpregados(cp.getId(), pessoa.getId()) > 0) {
                    List<CertificadoArquivos> list = new CertificadoArquivosDao().findBy(cp.getId(), pessoa.getId());
                    if (list.isEmpty()) {
                        uploadCertificado = true;
                    }
                }
                loadCertificadoArquivos();
            }
        }

        chkTodasCertidoes = false;
        if(renderContabil) {
             getListComboPessoa();            
        }
        loadListaCertidaoDisponivel();
        //marcarTodasCertidoes();
    }

    public final void marcarTodasCertidoes() {
        for (CertidaoDisponivel cd : listCertidaoDisponivelSolicitar) {
            cd.setSelected(chkTodasCertidoes);
        }
    }

    public final void loadListaCertidaoDisponivel() {
        listComboCertidaoDisponivel.clear();
        listCertidaoDisponivelSolicitar.clear();

        WebREPISDao db = new WebREPISDao();
        JuridicaDao dbj = new JuridicaDao();

        Juridica juridica = null;

        if (pessoaContribuinte == null && listComboPessoa.isEmpty()) {
            return;
        }

        if (pessoaContribuinte != null) {
            juridica = dbj.pesquisaJuridicaPorPessoa(pessoaContribuinte.getId());
        } else {
            juridica = dbj.pesquisaJuridicaPorPessoa(Integer.valueOf(listComboPessoa.get(idPessoa).getDescription()));
        }

        List<List> listax = dbj.listaJuridicaContribuinte(juridica.getId());

        if (listax.isEmpty()) {
            listComboCertidaoDisponivel.add(new SelectItem(0, "Nenhuma Certidão Disponível", "0"));
            return;
        }

        int id_convencao = (Integer) listax.get(0).get(5), id_grupo = (Integer) listax.get(0).get(6);
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        PessoaEndereco pend = dao.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 5);

        if (pend == null) {
            listComboCertidaoDisponivel.add(new SelectItem(0, "Nenhuma Certidão Disponível", "0"));
            return;
        }

        List<CertidaoDisponivel> result = db.listaCertidaoDisponivel(pend.getEndereco().getCidade().getId(), id_convencao);

        if (result.isEmpty()) {
            listComboCertidaoDisponivel.add(new SelectItem(0, "Nenhuma Certidão Disponível", "0"));
            return;
        }

        listCertidaoDisponivelSolicitar = result;

        for (int i = 0; i < result.size(); i++) {
            listComboCertidaoDisponivel.add(
                    new SelectItem(
                            i, result.get(i).getCertidaoTipo().getDescricao(), String.valueOf(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadCertificadoArquivos() {
        listCertificadoArquivos = new ArrayList();
        listCertificadoArquivos = new CertificadoArquivosDao().findByPessoa(pessoa.getId());
    }

    public final void loadListConvencaoPeriodo(ConvencaoPeriodo cp) {
        List<ConvencaoPeriodo> list = new ConvencaoPeriodoDao().listaConvencaoPeriodo(cp.getConvencao().getId(), cp.getGrupoCidade().getId());
        listConvencaoPeriodo = new ArrayList();
        listConvencaoPeriodo.add(
                new SelectItem(
                        null,
                        "-- ATUAL -- "
                )
        );
        for (int i = 0; i < list.size(); i++) {
            listConvencaoPeriodo.add(
                    new SelectItem(
                            list.get(i).getId(),
                            list.get(i).getReferenciaInicial() + " - " + list.get(i).getReferenciaFinal()
                    )
            );
        }
    }

    public PessoaEndereco enderecoPessoa(int id_pessoa) {
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        PessoaEndereco endereco_pessoa = dao.pesquisaEndPorPessoaTipo(id_pessoa, 5);
        return endereco_pessoa;
    }

    public String retornaCor(RepisMovimento rm) {
        if (DataHoje.igualdadeData(rm.getPessoa().getCriacao(), rm.getDataEmissaoString()) && rm.getRepisStatus().getId() == 1) {
            return "tblColorx";
        }
        return "";
    }

    public void alterValueLenght(String value) {
        listRepisMovimentoPatronal.clear();
        listRepisMovimentoPatronalSelecionado.clear();

        valueLenght = value;

        pesquisar();
    }

    public String refresh() {
        return "webLiberacaoREPIS";
    }

    public void liberarListaSolicitacao() {
        Dao di = new Dao();
        di.openTransaction();

        for (RepisMovimento listRepis : listRepisMovimentoPatronalSelecionado) {
            RepisMovimento rm = (RepisMovimento) di.find(new RepisMovimento(), listRepis.getId());

            RepisStatus rs = (RepisStatus) di.find(new RepisStatus(), Integer.parseInt(listComboRepisStatus.get(idRepisStatus).getDescription()));
            rm.setRepisStatus(rs);
            if (rs.getId() == 2 || rs.getId() == 3 || rs.getId() == 4) {
                rm.setDataResposta(DataHoje.dataHoje());
            } else {
                rm.setDataResposta(null);
            }

            if (!di.update(rm)) {
                di.rollback();
                GenericaMensagem.error("Erro", "Não foi possível atualizar STATUS, tente novamente!");
                return;
            }
        }
        GenericaMensagem.info("Sucesso", "Registros Atualizados");
        di.commit();

        listRepisMovimentoPatronal.clear();
        listRepisMovimentoPatronalSelecionado.clear();
    }

    public void pesquisar() {
        WebREPISDao db = new WebREPISDao();
        listRepisMovimentoPatronal.clear();
        listRepisMovimentoPatronalSelecionado.clear();
        Patronal patro = db.pesquisaPatronalPorPessoa(pessoa.getId());

        switch (tipoPesquisa) {
            default:
                listRepisMovimentoPatronal = db.pesquisarListaLiberacao(tipoPesquisa, descricao, patro.getId(), valueLenght, objectFiltro);
                break;
        }
    }

    public String pesquisarPorSolicitante() {
        WebREPISDao db = new WebREPISDao();
        listRepisMovimento.clear();
        if (renderEmpresa) {
            listRepisMovimento = db.pesquisarListaSolicitacao(tipoPesquisa, descricao, pessoa.getId(), -1);
        } else {
            listRepisMovimento = db.pesquisarListaSolicitacao(tipoPesquisa, descricao, -1, pessoa.getId());
        }

        return null;
    }

    public void limpar() {
        repisMovimento = new RepisMovimento();
        showProtocolo = false;
        pessoaSolicitante = new Pessoa();
        //idPessoa = 0;
        listRepisMovimento.clear();
        loadListaCertidaoDisponivel();
    }

    public String limparRepisLiberacao() {
        repisMovimento = new RepisMovimento();
        listRepisMovimentoPatronal.clear();
        return "webLiberacaoREPIS";
    }

    public RepisMovimento getRepisMovimento() {
        return repisMovimento;
    }

    public void setRepisMovimento(RepisMovimento repisMovimento) {
        this.repisMovimento = repisMovimento;
    }

    public List listPessoaRepisAno() {
        WebREPISDao wsrepisdb = new WebREPISDao();
        List<RepisMovimento> result = new ArrayList();
        if (renderEmpresa) {
            result = wsrepisdb.pesquisarListaSolicitacao("", "", pessoa.getId(), -1);
        } else if (renderContabil) {
            //Pessoa pes = (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(listComboPessoa.get(idPessoa).getDescription()));
            result = wsrepisdb.pesquisarListaSolicitacao("", "", -1, pessoa.getId());
        }
        return result;
    }

    public boolean showAndamentoProtocolo(int idPessoa, int idPatronal, CertidaoDisponivel cd) {
        WebREPISDao wsrepisdb = new WebREPISDao();
        //CertidaoDisponivel cd = (CertidaoDisponivel) new Dao().find(new CertidaoDisponivel(), Integer.valueOf(listComboCertidaoDisponivel.get(indexCertidaoDisponivel).getDescription()));
        if (wsrepisdb.validaPessoaRepisAnoTipoPatronal(idPessoa, getAnoConvencao(), cd.getCertidaoTipo().getId(), idPatronal).size() > 0) {
            return true;
        }
        return false;
    }

    public void solicitarREPIS() {
        Dao di = new Dao();
        // ConfiguracaoArrecadacao cf = ConfiguracaoArrecadacao.get();
        String detalhes = configuracaoArrecadacao.getFilial().getFilial().getPessoa().getNome() + " - Telefone: " + configuracaoArrecadacao.getFilial().getFilial().getPessoa().getTelefone1();
        if (!listComboPessoa.isEmpty()) {
            if (Integer.parseInt(listComboPessoa.get(idPessoa).getDescription()) > 0) {
                setPessoaSolicitante((Pessoa) di.find(new Pessoa(), Integer.parseInt(listComboPessoa.get(idPessoa).getDescription())));
            }
        } else {
            setPessoaSolicitante(getPessoa());
        }
        WebREPISDao dbr = new WebREPISDao();
        if (!dbr.listaAcordoAberto(pessoaSolicitante.getId()).isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não foi possível concluir sua solicitação. Consulte o sindicato!" + detalhes);
            return;
        }

        HomologacaoDao db = new HomologacaoDao();
        setShowProtocolo(false);

        if (!db.pesquisaPessoaDebito(pessoaSolicitante.getId(), DataHoje.data()).isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não foi possível concluir sua solicitação. Consulte o sindicato!" + detalhes);
        } else {
            if (contato.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informe o nome do solicitante!");
                return;
            }

            Patronal patronal = dbr.pesquisaPatronalPorSolicitante(getPessoaSolicitante().getId());
            if (patronal == null) {
                GenericaMensagem.warn("Atenção", "Nenhuma patronal encontrada!");
                return;
            }
            JuridicaDao dbj = new JuridicaDao();
            Juridica juridicax = dbj.pesquisaJuridicaPorPessoa(pessoaSolicitante.getId());
            PisoSalarialLote lote = dbr.pesquisaPisoSalarial(getAnoConvencao(), patronal.getId(), juridicax.getPorte().getId());

            if (lote.getId() == -1) {
                GenericaMensagem.warn("Atenção", "Patronal sem Lote, contate seu Sindicato!" + detalhes);
                return;
            }

            if (DataHoje.menorData(lote.getValidade(), DataHoje.data())) {
                GenericaMensagem.warn("Atenção", "Solicitação para esta patronal vencida!");
                return;
            }

//            if (listComboCertidaoDisponivel.size() == 1 && listComboCertidaoDisponivel.get(indexCertidaoDisponivel).getDescription().equals("0")) {
//                GenericaMensagem.warn("Atenção", "Nenhuma Certidão disponível!");
//                return;
//            }
            if (listCertidaoDisponivelSolicitar.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Nenhuma Certidão disponível!");
                return;
            }

            Boolean selected = false;
            for (CertidaoDisponivel cd : listCertidaoDisponivelSolicitar) {
                if (cd.getSelected()) {
                    selected = true;
                }
            }

            if (!selected) {
                GenericaMensagem.warn("Atenção", "Nenhuma Certidão Selecionada!");
                return;
            }

            //CertidaoDisponivel cd = (CertidaoDisponivel) di.find(new CertidaoDisponivel(), Integer.valueOf(listComboCertidaoDisponivel.get(indexCertidaoDisponivel).getDescription()));
            if (configuracaoArrecadacao != null) {
                if (configuracaoArrecadacao.getCertificadoFaturementoBrutoAnual()) {
                    if (repisMovimento.getFaturamentoBrutoAnual() <= 0) {
                        GenericaMensagem.warn("Validação", "Informar o valor do "
                                + "faturamento bruto anual!");
                        return;
                    }
                }
            }

            di.openTransaction();

            Boolean commit = false;
            for (CertidaoDisponivel cd : listCertidaoDisponivelSolicitar) {
                if (cd.getSelected()) {
                    List<ConvencaoPeriodo> result = dbr.listaConvencaoPeriodo(cd.getCidade().getId(), cd.getConvencao().getId());

                    Integer anoConvencao = null;

                    if (cd.isPeriodoConvencao()) {
                        if (result.isEmpty()) {
                            GenericaMensagem.warn("Atenção", "Contribuinte fora do Período de Convenção!");
                            return;
                        }
                        anoConvencao = Integer.valueOf(result.get(0).getReferenciaFinal().substring(3));
                    } else {
                        anoConvencao = getAnoAtual();
                    }

//                    repisMovimento.setAno(anoConvencao);
//                    repisMovimento.setContato(contato);
//                    repisMovimento.setRepisStatus((RepisStatus) di.find(new RepisStatus(), 1));
//                    repisMovimento.setPessoa(getPessoaSolicitante());
//                    repisMovimento.setDataResposta(null);
//                    repisMovimento.setDataEmissao(DataHoje.dataHoje());
//                    repisMovimento.setPatronal(patronal);
//                    repisMovimento.setCertidaoTipo(cd.getCertidaoTipo());
//                    
                    RepisMovimento repis_save = new RepisMovimento(
                            -1,
                            DataHoje.dataHoje(),
                            contato,
                            pessoaSolicitante,
                            null,
                            anoConvencao,
                            (RepisStatus) di.find(new RepisStatus(), 1),
                            patronal,
                            cd.getCertidaoTipo(),
                            repisMovimento.getDataImpressao(),
                            repisMovimento.getFaturamentoBrutoAnual()
                    );

                    if (!showAndamentoProtocolo(pessoaSolicitante.getId(), repis_save.getPatronal().getId(), cd)) {
                        if (!di.save(repis_save)) {
                            di.rollback();
                            GenericaMensagem.error("Erro", "Não foi possível concluir sua solicitação. Consulte o sindicato!" + detalhes);
                            return;
                        }

                        commit = true;
                    } else {
                        GenericaMensagem.warn("Atenção", "Certidão " + cd.getCertidaoTipo().getDescricao() + " já solicitada!");
//                        di.rollback();
//                        limpar();
                    }
                }
            }

            if (commit) {
                di.commit();
                GenericaMensagem.info("Sucesso", "Solicitação encaminhada com sucesso!");
            } else {
                di.rollback();
            }
            limpar();
        }
    }

    public void updateStatus() {
        Dao di = new Dao();
        if (repisMovimento.getId() != -1) {
            RepisStatus rs = (RepisStatus) di.find(new RepisStatus(), Integer.parseInt(listComboRepisStatus.get(idRepisStatus).getDescription()));
            repisMovimento.setRepisStatus(rs);

            if (rs.getId() == 2 || rs.getId() == 3 || rs.getId() == 4) {
                repisMovimento.setDataResposta(DataHoje.dataHoje());
            } else {
                repisMovimento.setDataResposta(null);
            }

            di.openTransaction();
            if (di.update(repisMovimento)) {
                di.commit();
                GenericaMensagem.info("Sucesso", "Status atualizado com sucesso!");

                listRepisMovimento.clear();
                repisMovimento = new RepisMovimento();
            } else {
                di.rollback();
                GenericaMensagem.error("Erro", "Falha na atualização do Status!");
            }
        }
    }

    public void edit(RepisMovimento rm) {
        repisMovimento = rm;
        for (int i = 0; i < getListComboRepisStatus().size(); i++) {
            if (Integer.parseInt(listComboRepisStatus.get(i).getDescription()) == repisMovimento.getRepisStatus().getId()) {
                setIdRepisStatus(i);
            }
        }
        WebREPISDao dbw = new WebREPISDao();
        Juridica jur = dbw.pesquisaEscritorioDaEmpresa(repisMovimento.getPessoa().getId());
        if (jur != null) {
            escritorio = jur.getPessoa();
        }
    }

    public String imprimirCertificado(RepisMovimento rm) {
        List<RepisMovimento> listam = new ArrayList();
        listam.add(rm);
        imprimirCertificado(listam);
        return null;
    }

    public String imprimirCertificado(List<RepisMovimento> listam) {
        JuridicaDao dbj = new JuridicaDao();
        WebREPISDao dbw = new WebREPISDao();
        List<JasperPrint> lista_jasper = new ArrayList();

        if (listam.isEmpty()) {
            return null;
        }
        Dao di = new Dao();
        try {
            Juridica sindicato = dbj.pesquisaJuridicaPorPessoa(1);

            PessoaEnderecoDao dao = new PessoaEnderecoDao();
            PessoaEndereco sindicato_endereco = dao.pesquisaEndPorPessoaTipo(1, 5);

            di.openTransaction();
            for (RepisMovimento repis : listam) {
                if (repis.getRepisStatus().getId() != 1) {
                    Juridica juridica = dbj.pesquisaJuridicaPorPessoa(repis.getPessoa().getId());
                    PisoSalarialLote lote = dbw.pesquisaPisoSalarial(repis.getAno(), repis.getPatronal().getId(), juridica.getPorte().getId());
                    PessoaEndereco ee = dao.pesquisaEndPorPessoaTipo(repis.getPessoa().getId(), 5);
                    List<PisoSalarial> listapiso = dbw.listaPisoSalarialLote(lote.getId());

                    List<List> listax = dbj.listaJuridicaContribuinte(juridica.getId());
                    if (listax.isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Empresa não é Contribuinte!");
                        di.rollback();
                        return null;
                    }
                    int id_convencao = (Integer) listax.get(0).get(5), id_grupo = (Integer) listax.get(0).get(6);

                    String referencia = DataHoje.DataToArray(repis.getDataEmissao())[2] + DataHoje.DataToArray(repis.getDataEmissao())[1];

                    List<ConvencaoPeriodo> result = dbw.listaConvencaoPeriodoData(ee.getEndereco().getCidade().getId(), id_convencao, referencia);

                    if (result.isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Contribuinte fora do período de Convenção!");
                        di.rollback();
                        return null;
                    }

                    String ref = result.get(0).getReferenciaInicial().substring(3) + "/" + result.get(0).getReferenciaFinal().substring(3);
                    Date data_validade = DataHoje.converte(DataHoje.qtdeDiasDoMes(Integer.valueOf(result.get(0).getReferenciaFinal().substring(0, 2)), Integer.valueOf(result.get(0).getReferenciaFinal().substring(3))) + "/" + result.get(0).getReferenciaFinal());

                    Collection<ParametroCertificado> vetor = new ArrayList();
                    String logoPatronal = "",
                            imagemFundo = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/certificado_domingo_fundo.png"),
                            logoCaminho = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPatronal/" + repis.getPatronal().getId());
                    if (new File(logoCaminho + ".jpg").exists()) {
                        logoCaminho = logoCaminho + ".jpg";
                    } else if (new File(logoCaminho + ".JPG").exists()) {
                        logoCaminho = logoCaminho + ".JPG";
                    } else if (new File(logoCaminho + ".png").exists()) {
                        logoCaminho = logoCaminho + ".png";
                    } else if (new File(logoCaminho + ".PNG").exists()) {
                        logoCaminho = logoPatronal + repis.getPatronal().getId() + ".PNG";
                    } else if (new File(logoCaminho + ".gif").exists()) {
                        logoCaminho = logoCaminho + ".gif";
                    } else {
                        logoCaminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png");
                    }

                    String cep = AnaliseString.mascaraCep(ee.getEndereco().getCep());
                    String ende = (ee.getComplemento().isEmpty())
                            ? ee.getEndereco().getLogradouro().getDescricao() + " " + ee.getEndereco().getDescricaoEndereco().getDescricao() + ", " + ee.getNumero() + " - " + ee.getEndereco().getBairro().getDescricao() + " - CEP: " + cep + " - " + ee.getEndereco().getCidade().getCidadeToString()
                            : ee.getEndereco().getLogradouro().getDescricao() + " " + ee.getEndereco().getDescricaoEndereco().getDescricao() + ", " + ee.getNumero() + " ( " + ee.getComplemento() + " ) " + ee.getEndereco().getBairro().getDescricao() + " - CEP: " + cep + " - " + ee.getEndereco().getCidade().getCidadeToString();

                    CertidaoMensagem certidaoMensagem = null;
                    File file = null;

                    switch (repis.getCertidaoTipo().getId()) {
                        case 1:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/REPIS.jasper"));
                            break;
                        case 2:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CERTIDAO_DOMINGOS.jasper"));
                            break;
                        case 3:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CERTIDAO_FERIADOS.jasper"));
                            certidaoMensagem = dbw.pesquisaCertidaoMensagem(ee.getEndereco().getCidade().getId(), 3);
                            break;
                        case 4:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CERTIFICADO_DOMINGOS.jasper"));
                            break;
                        case 5:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/REPIS_AUXILIAR.jasper"));
                            break;
                        case 6:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CERTIDAO_DOMINGOS_AUXILIAR.jasper"));
                            break;
                        case 7:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CERTIDAO_FERIADOS_AUXILIAR.jasper"));
                            certidaoMensagem = dbw.pesquisaCertidaoMensagem(ee.getEndereco().getCidade().getId(), 3);
                            break;
                        case 8:
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CERTIDAO_PARCELAMENTO_REAJUSTE_SALARIAL.jasper"));
                            break;
                    }

                    JasperReport jasper = (JasperReport) JRLoader.loadObject(file);

                    for (PisoSalarial piso : listapiso) {
                        BigDecimal valor = new BigDecimal(piso.getValor());
                        if (valor.toString().equals("0")) {
                            valor = null;
                        }

                        vetor.add(
                                new ParametroCertificado(
                                        repis.getPatronal().getPessoa().getNome(),
                                        logoCaminho,
                                        repis.getPatronal().getBaseTerritorial(),
                                        sindicato.getPessoa().getNome(),
                                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                                        repis.getPessoa().getNome(),
                                        repis.getPessoa().getDocumento(),
                                        juridica.getPorte().getDescricao(),
                                        piso.getDescricao(),
                                        valor,
                                        (certidaoMensagem != null) ? certidaoMensagem.getMensagem() : piso.getPisoSalarialLote().getMensagem(),
                                        DataHoje.dataExtenso(DataHoje.converteData(data_validade), 3),//piso.getPisoSalarialLote().getDtValidade(),
                                        sindicato_endereco.getEndereco().getCidade().getCidade() + " - " + sindicato_endereco.getEndereco().getCidade().getUf(),
                                        piso.getPisoSalarialLote().getAno(),
                                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/LogoSelo.png"),
                                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoFundo.png"),
                                        String.valueOf(repis.getId()),
                                        "0000000000".substring(0, 10 - String.valueOf(repis.getId()).length()) + String.valueOf(repis.getId()),
                                        DataHoje.dataExtenso(repis.getDataEmissaoString(), 3),
                                        ende,
                                        ref,
                                        imagemFundo
                                )
                        );
                    }

                    JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
                    lista_jasper.add(JasperFillManager.fillReport(jasper, null, dtSource));

                    if (repis.getDataImpressao() == null) {
                        RepisMovimento repisx = (RepisMovimento) di.find(new RepisMovimento(), repis.getId());
                        repisx.setDataImpressao(DataHoje.dataHoje());

                        di.save(repisx);
                    }
                }
            }

            if (!lista_jasper.isEmpty()) {
                di.commit();
                JRPdfExporter exporter = new JRPdfExporter();
                String nomeDownload = "certificado_" + DataHoje.livre(DataHoje.dataHoje(), "yyyyMMdd-HHmmss") + ".pdf";
                String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/repis");
                if (!new File(pathPasta).exists()) {
                    new File(pathPasta).mkdirs();
                }
                exporter.setExporterInput(SimpleExporterInput.getInstance(lista_jasper));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pathPasta + "/" + nomeDownload));

                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

                configuration.setCreatingBatchModeBookmarks(true);

                exporter.setConfiguration(configuration);

                exporter.exportReport();

                File fl = new File(pathPasta);

                if (fl.exists()) {
                    Download download = new Download(
                            nomeDownload,
                            pathPasta,
                            "application/pdf",
                            FacesContext.getCurrentInstance()
                    );
                    download.baixar();
                    download.remover();
                }
                listRepisMovimentoPatronal.clear();
            } else {
                di.rollback();
                GenericaMensagem.warn("Atenção", "O Status da Certidão não pode impresso!");
            }
        } catch (NumberFormatException | JRException e) {
            di.rollback();
            e.getMessage();
            GenericaMensagem.error("Erro", "Arquivo de Certidão não encontrado! " + e.getMessage());
        }
        return null;
    }

    public String getEnderecoString() {
        PessoaEnderecoDao dao = new PessoaEnderecoDao();
        PessoaEndereco ende = null;
        List listaEnd = dao.pesquisaEndPorPessoa(repisMovimento.getPessoa().getId());
        String strCompl;
        String enderecoString;
        if (!listaEnd.isEmpty()) {
            ende = (PessoaEndereco) listaEnd.get(0);
        }

        if (ende != null) {
            if (ende.getComplemento() == null || ende.getComplemento().isEmpty()) {
                strCompl = " ";
            } else {
                strCompl = " ( " + ende.getComplemento() + " ) ";
            }
            enderecoString = ende.getEndereco().getLogradouro().getDescricao() + " "
                    + ende.getEndereco().getDescricaoEndereco().getDescricao() + ", " + ende.getNumero() + " " + ende.getEndereco().getBairro().getDescricao() + ","
                    + strCompl + ende.getEndereco().getCidade().getCidade() + " - " + ende.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(ende.getEndereco().getCep());
        } else {
            enderecoString = "NENHUM";
        }
        return enderecoString;
    }

    public void clear() {
        setShowProtocolo(true);
        getListRepisMovimento();
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("sessaoUsuarioAcessoWeb")) {
            pessoa = (Pessoa) GenericaSessao.getObject("sessaoUsuarioAcessoWeb");
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getPessoaContribuinte() {
        return pessoaContribuinte;
    }

    public void setPessoaContribuinte(Pessoa pessoaContribuinte) {
        this.pessoaContribuinte = pessoaContribuinte;
    }

    public Pessoa getPessoaContabilidade() {
        return pessoaContabilidade;
    }

    public void setPessoaContabilidade(Pessoa pessoaContabilidade) {
        this.pessoaContabilidade = pessoaContabilidade;
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    public int getAnoAtual() {
        return Integer.parseInt(DataHoje.livre(DataHoje.dataHoje(), "yyyy"));
    }

    public int getAnoConvencao() {
        WebREPISDao wsrepisdb = new WebREPISDao();
        CertidaoDisponivel cd = (CertidaoDisponivel) new Dao().find(new CertidaoDisponivel(), Integer.valueOf(listComboCertidaoDisponivel.get(indexCertidaoDisponivel).getDescription()));
        List<ConvencaoPeriodo> result = wsrepisdb.listaConvencaoPeriodo(cd.getCidade().getId(), cd.getConvencao().getId());
        if (result.isEmpty()) {
            return getAnoAtual();
        } else {
            return Integer.parseInt(result.get(0).getReferenciaFinal().substring(3));
        }
    }

    public ConvencaoPeriodo getConvencaoPeriodoEmpresa() {
        Pessoa p = new Pessoa();
        if (!listComboPessoa.isEmpty()) {
            if (Integer.parseInt(listComboPessoa.get(idPessoa).getDescription()) > 0) {
                p = (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(listComboPessoa.get(idPessoa).getDescription()));
            }
        } else {
            p = getPessoa();
        }
        return new ConvencaoPeriodoDao().findByPessoa(p.getId());
    }

    public Integer getAnoConvencaoEmpresa() {
        ConvencaoPeriodo convencaoPeriodo = getConvencaoPeriodoEmpresa();
        if (convencaoPeriodo == null) {
            return getAnoAtual();
        } else {
            return Integer.parseInt(convencaoPeriodo.getReferenciaFinal().substring(3));
        }
    }

    public boolean isShowProtocolo() {
        return showProtocolo;
    }

    public void setShowProtocolo(boolean showProtocolo) {
        this.showProtocolo = showProtocolo;
    }

    public List<RepisMovimento> getListRepisMovimento() {
        if (listRepisMovimento.isEmpty()) {
            List list_result = listPessoaRepisAno();
            if (!list_result.isEmpty()) {
                listRepisMovimento = list_result;
            }
        }
        return listRepisMovimento;
    }

    public void setListRepisMovimento(List<RepisMovimento> listRepisMovimento) {
        this.listRepisMovimento = listRepisMovimento;
    }

    public List<SelectItem> getListComboPessoa() {
        if (listComboPessoa.isEmpty()) {
            JuridicaDao dbJur = new JuridicaDao();
            getPessoa();
            List<Juridica> select = null;
            select = dbJur.listaContabilidadePertencente(dbJur.pesquisaJuridicaPorPessoa(pessoa.getId()).getId());
            if (select != null) {
                int i = 0;
                while (i < select.size()) {
                    listComboPessoa.add(new SelectItem(i,
                            (String) (select.get(i)).getPessoa().getNome(),
                            Integer.toString((select.get(i)).getPessoa().getId())));
                    i++;
                }
            }
        }
        return listComboPessoa;
    }

    public List<SelectItem> getListComboRepisStatus() {
        if (listComboRepisStatus.isEmpty()) {
            Dao di = new Dao();
            List<RepisStatus> list = di.list(new RepisStatus());
            for (int i = 0; i < list.size(); i++) {
                listComboRepisStatus.add(new SelectItem(i, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
            }
        }
        return listComboRepisStatus;
    }

    public void setListComboPessoa(List<SelectItem> listComboPessoa) {
        this.listComboPessoa = listComboPessoa;
    }

    public Pessoa getPessoaSolicitante() {
        return pessoaSolicitante;
    }

    public void setPessoaSolicitante(Pessoa pessoaSolicitante) {
        this.pessoaSolicitante = pessoaSolicitante;
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

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
        listRepisMovimento.clear();
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
        listRepisMovimento.clear();
    }

    public List<RepisMovimento> getListRepisMovimentoPatronal() {
        if (listRepisMovimentoPatronal.isEmpty()) {
            WebREPISDao wsrepisdb = new WebREPISDao();
            Patronal patro = wsrepisdb.pesquisaPatronalPorPessoa(pessoa.getId());
            if (tipoPesquisa.equals("status")) {
                listRepisMovimentoPatronal = wsrepisdb.pesquisarListaLiberacao("status", "", patro.getId(), valueLenght, objectFiltro);
            } else {
                listRepisMovimentoPatronal = wsrepisdb.pesquisarListaLiberacao("", "", patro.getId(), valueLenght, objectFiltro);
            }
        }
        return listRepisMovimentoPatronal;
    }

    public void setListRepisMovimentoPatronal(List<RepisMovimento> listRepisMovimentoPatronal) {
        this.listRepisMovimentoPatronal = listRepisMovimentoPatronal;
    }

    public void setListComboRepisStatus(List<SelectItem> listComboRepisStatus) {
        this.listComboRepisStatus = listComboRepisStatus;
    }

    public int getIdRepisStatus() {
        return idRepisStatus;
    }

    public void setIdRepisStatus(int idRepisStatus) {
        this.idRepisStatus = idRepisStatus;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public PessoaEndereco getPessoaEndereco() {
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public boolean isRenderContabil() {
        return renderContabil;
    }

    public void setRenderContabil(boolean renderContabil) {
        this.renderContabil = renderContabil;
    }

    public boolean isRenderEmpresa() {
        return renderEmpresa;
    }

    public void setRenderEmpresa(boolean renderEmpresa) {
        this.renderEmpresa = renderEmpresa;
    }

    public List getListArquivosEnviados() {
        try {
            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/repis/" + pessoa.getId() + "/");
            File file = new File(caminho);
            file.mkdir();

            File file2 = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/repis/"));
            File listFile[] = file2.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isFile()) {
                    listFile[i].renameTo(new File(file.getPath() + "/" + listFile[i].getName()));
                    listArquivosEnviados.clear();
                }
            }

            File list[] = file.listFiles();
            if (listArquivosEnviados.size() != list.length) {
                for (int i = 0; i < list.length; i++) {
                    listArquivosEnviados.add(list[i].getName());
                }
            }
        } catch (Exception e) {

        }

        return listArquivosEnviados;
    }

    public void setListArquivosEnviados(List listArquivosEnviados) {
        this.listArquivosEnviados = listArquivosEnviados;
    }

    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Pessoa getEscritorio() {
        return escritorio;
    }

    public void setEscritorio(Pessoa escritorio) {
        this.escritorio = escritorio;
    }

    public List<SelectItem> getListComboCertidaoDisponivel() {
        return listComboCertidaoDisponivel;
    }

    public void setListComboCertidaoDisponivel(List<SelectItem> listComboCertidaoDisponivel) {
        this.listComboCertidaoDisponivel = listComboCertidaoDisponivel;
    }

    public int getIndexCertidaoDisponivel() {
        return indexCertidaoDisponivel;
    }

    public void setIndexCertidaoDisponivel(int indexCertidaoDisponivel) {
        this.indexCertidaoDisponivel = indexCertidaoDisponivel;
    }

    public List<RepisMovimento> getListRepisMovimentoPatronalSelecionado() {
        return listRepisMovimentoPatronalSelecionado;
    }

    public void setListRepisMovimentoPatronalSelecionado(List<RepisMovimento> listRepisMovimentoPatronalSelecionado) {
        this.listRepisMovimentoPatronalSelecionado = listRepisMovimentoPatronalSelecionado;
    }

    public String getValueLenght() {
        return valueLenght;
    }

    public void setValueLenght(String valueLenght) {
        this.valueLenght = valueLenght;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public void loadListRepisMovimentoPessoa(Integer pessoa_id) {
        listRepisMovimentoPessoa = new WebREPISDao().listRepisPorPessoa(pessoa_id);
    }

    public List<RepisMovimento> getListRepisMovimentoPessoa() {
        return listRepisMovimentoPessoa;
    }

    public void setListRepisMovimentoPessoa(List<RepisMovimento> listRepisMovimentoPessoa) {
        this.listRepisMovimentoPessoa = listRepisMovimentoPessoa;
    }

    public Boolean getUploadCertificado() {
        return uploadCertificado;
    }

    public void setUploadCertificado(Boolean uploadCertificado) {
        this.uploadCertificado = uploadCertificado;
    }

    public void upload(FileUploadEvent event) {
        Pessoa p = null;
        Dao dao = new Dao();
        if (!listComboPessoa.isEmpty()) {
            if (Integer.parseInt(listComboPessoa.get(idPessoa).getDescription()) > 0) {
                p = (Pessoa) dao.find(new Pessoa(), Integer.parseInt(listComboPessoa.get(idPessoa).getDescription()));
            }
        } else {
            p = getPessoa();
        }
        if (p == null) {
            GenericaMensagem.warn("Erro", "NENHUMA PESSOA ENCONTRADA!");
            return;
        }
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setResourceFolder(true);
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        configuracaoUpload.setEvent(event);
        ConvencaoPeriodo cp;
        if (idConvencaoPeriodo == null) {
            cp = getConvencaoPeriodoEmpresa();
        } else {
            cp = (ConvencaoPeriodo) new Dao().find(new ConvencaoPeriodo(), idConvencaoPeriodo);
        }
        configuracaoUpload.setDiretorio("web/arquivos/certificado/anexos/" + cp.getId() + "/" + p.getId() + "/");
        CertificadoArquivos certificadoArquivos = new CertificadoArquivos();
        certificadoArquivos.setArquivo(event.getFile().getFileName());
        certificadoArquivos.setDtUpload(new Date());
        certificadoArquivos.setConvencaoPeriodo(cp);
        certificadoArquivos.setPessoa(p);
        certificadoArquivos.setPath(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + GenericaSessao.getString("sessaoCliente") + "/" + "web/arquivos/certificado/anexos/" + cp.getId() + "/" + p.getId() + "/");
        if (dao.save(certificadoArquivos, true)) {
            configuracaoUpload.setRenomear(certificadoArquivos.getId().toString() + "." + Upload.extractExtension(event.getFile().getFileName()));
            if (Upload.enviar(configuracaoUpload, true)) {
                if (new Dao().update(certificadoArquivos, true)) {
                    loadCertificadoArquivos();
                    if (new EmpregadosDao().pesquisaQuantidadeEmpregados(cp.getId(), p.getId()) > 0) {
                        List<CertificadoArquivos> list = new CertificadoArquivosDao().findBy(cp.getId(), p.getId());
                        if (list.isEmpty()) {
                            uploadCertificado = true;
                            PF.update("form_upload");
                        } else {
                            uploadCertificado = false;
                        }
                    }
                    GenericaMensagem.info("Sucesso", "ARQUIVO ENVIADO!");
                } else {
                    new Dao().delete(certificadoArquivos, true);
                    GenericaMensagem.warn("Erro", "AO ENVIAR ARQUIVO!");
                }
            }
        } else {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ENVIAR ARQUIVO!");
        }
    }

    public List<CertificadoArquivos> getListCertificadoArquivos() {
        return listCertificadoArquivos;
    }

    public void setListCertificadoArquivos(List<CertificadoArquivos> listCertificadoArquivos) {
        this.listCertificadoArquivos = listCertificadoArquivos;
    }

    public ConfiguracaoArrecadacao getConfiguracaoArrecadacao() {
        return configuracaoArrecadacao;
    }

    public void setConfiguracaoArrecadacao(ConfiguracaoArrecadacao configuracaoArrecadacao) {
        this.configuracaoArrecadacao = configuracaoArrecadacao;
    }

    public void view(CertificadoArquivos ca) throws IOException {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + String.valueOf(request.getServerPort()) + "/";
        // url += "Sindical/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + la.getDocFile().getPessoa().getId() + "/" + la.getDocFile().getId() + "/" + URLEncoder.encode(la.getNameFile(), "UTF-8").replace("+", "%20");
        response.sendRedirect(url);
    }

    public void download(CertificadoArquivos ca) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String mimeType = servletContext.getMimeType(ca.getPath() + "/" + ca.getFileName());
        Download d = new Download(ca.getFileName(), ca.getPath(), mimeType, FacesContext.getCurrentInstance());
        d.baixar();
    }

    public void deleteFile(CertificadoArquivos ca) {
        if (ca.getDtDownload() != null) {
            GenericaMensagem.warn("Validação", "NÃO É POSSÍVEL REMOVER ARQUIVOS JÁ VERIFICADOS!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        if (dao.delete(ca)) {
            String path = ca.getPath() + "/" + ca.getFileName();
            File file = new File(path);
            if (file.exists()) {
                if (file.delete()) {
                    dao.commit();
                    loadCertificadoArquivos();
                    GenericaMensagem.info("Sucesso", "ARQUIVO REMOVIDO!");
                    ConvencaoPeriodo cf = getConvencaoPeriodoEmpresa();
                    if (new EmpregadosDao().pesquisaQuantidadeEmpregados(cf.getId(), pessoa.getId()) > 0) {
                        List<CertificadoArquivos> list = new CertificadoArquivosDao().findBy(cf.getId(), pessoa.getId());
                        if (list.isEmpty()) {
                            uploadCertificado = true;
                            PF.update("form_upload");
                        }
                    }
                    return;
                }
            } else {
                dao.commit();
                loadCertificadoArquivos();
                ConvencaoPeriodo cf = getConvencaoPeriodoEmpresa();
                if (new EmpregadosDao().pesquisaQuantidadeEmpregados(cf.getId(), pessoa.getId()) > 0) {
                    List<CertificadoArquivos> list = new CertificadoArquivosDao().findBy(cf.getId(), pessoa.getId());
                    if (list.isEmpty()) {
                        uploadCertificado = true;
                        PF.update("form_upload");
                    }
                }
                return;
            }
        }
        GenericaMensagem.warn("Erro", "AO REMOVER ARQUIVO!");
        dao.rollback();
    }

    public List<SelectItem> getListConvencaoPeriodo() {
        return listConvencaoPeriodo;
    }

    public void setListConvencaoPeriodo(List<SelectItem> listConvencaoPeriodo) {
        this.listConvencaoPeriodo = listConvencaoPeriodo;
    }

    public Integer getIdConvencaoPeriodo() {
        return idConvencaoPeriodo;
    }

    public void setIdConvencaoPeriodo(Integer idConvencaoPeriodo) {
        this.idConvencaoPeriodo = idConvencaoPeriodo;
    }

    public List<CertidaoDisponivel> getListCertidaoDisponivelSolicitar() {
        return listCertidaoDisponivelSolicitar;
    }

    public void setListCertidaoDisponivelSolicitar(List<CertidaoDisponivel> listCertidaoDisponivelSolicitar) {
        this.listCertidaoDisponivelSolicitar = listCertidaoDisponivelSolicitar;
    }

    public boolean isChkTodasCertidoes() {
        return chkTodasCertidoes;
    }

    public void setChkTodasCertidoes(boolean chkTodasCertidoes) {
        this.chkTodasCertidoes = chkTodasCertidoes;
    }

    public ObjectFiltro getObjectFiltro() {
        return objectFiltro;
    }

    public void setObjectFiltro(ObjectFiltro objectFiltro) {
        this.objectFiltro = objectFiltro;
    }

    public class ObjectFiltro {

        private Boolean booleanAno;
        private Integer ano;

        private Boolean booleanEmissao;
        private String emissaoInicial;
        private String emissaoFinal;

        private Boolean booleanResposta;
        private String respostaInicial;
        private String respostaFinal;

        private Boolean booleanStatus;
        private Integer indexStatus;
        private List<SelectItem> listaStatus;

        private Boolean booleanTipoCertidao;
        private Integer indexTipoCertidao;
        private List<SelectItem> listaTipoCertidao;

        private Boolean booleanCidade;
        private Integer indexCidade;
        private List<SelectItem> listaCidade;

        public ObjectFiltro() {
            // ANO
            this.booleanAno = false;
            this.ano = null;
            // DATA EMISSÃO
            this.booleanEmissao = false;
            this.emissaoInicial = "";
            this.emissaoFinal = "";
            // DATA RESPOSTA
            this.booleanResposta = false;
            this.respostaInicial = "";
            this.respostaFinal = "";
            // STATUS
            this.booleanStatus = true;
            this.indexStatus = 1;
            this.listaStatus = loadListaStatus();
            // TIPO
            this.booleanTipoCertidao = false;
            this.indexTipoCertidao = 0;
            this.listaTipoCertidao = loadListaTipoCertidao();
            // CIDADE
            this.booleanCidade = false;
            this.indexCidade = 0;
            this.listaCidade = loadListaCidade();
        }

        public ObjectFiltro(Boolean booleanAno, Integer ano, Boolean booleanEmissao, String emissaoInicial, String emissaoFinal, Boolean booleanResposta, String respostaInicial, String respostaFinal, Boolean booleanStatus, Integer indexStatus, List<SelectItem> listaStatus, Integer indexTipoCertidao, List<SelectItem> listaTipoCertidao) {
            this.booleanAno = booleanAno;
            this.ano = ano;
            this.booleanEmissao = booleanEmissao;
            this.emissaoInicial = emissaoInicial;
            this.emissaoFinal = emissaoFinal;
            this.booleanResposta = booleanResposta;
            this.respostaInicial = respostaInicial;
            this.respostaFinal = respostaFinal;
            this.booleanStatus = booleanStatus;
            this.indexStatus = indexStatus;
            this.listaStatus = listaStatus;
            this.indexTipoCertidao = indexTipoCertidao;
            this.listaTipoCertidao = listaTipoCertidao;
        }

        public final List<SelectItem> loadListaStatus() {
            List<SelectItem> list = new ArrayList();
            Dao di = new Dao();

            List<RepisStatus> result = di.list(new RepisStatus());
            list.add(new SelectItem(0, "Todos", "0"));

            for (int i = 0; i < result.size(); i++) {
                list.add(
                        new SelectItem(
                                i + 1,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
            return list;
        }

        public final List<SelectItem> loadListaTipoCertidao() {
            List<SelectItem> list = new ArrayList();
            Dao di = new Dao();
            List<CertidaoTipo> result = di.list("CertidaoTipo");

            for (int i = 0; i < result.size(); i++) {
                list.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
            return list;
        }

        public final List<SelectItem> loadListaCidade() {
            List<SelectItem> list = new ArrayList();
            CidadeDao db = new CidadeDao();

            List<Cidade> result = db.listaCidadeParaREPIS();

            if (result.isEmpty()) {
                list.add(new SelectItem(0, "Nenhuma Cidade encontrada", "0"));
                return list;
            }

            for (int i = 0; i < result.size(); i++) {
                list.add(
                        new SelectItem(i, result.get(i).getCidadeToString(), Integer.toString(result.get(i).getId()))
                );
            }
            return list;
        }

        public Boolean getBooleanAno() {
            return booleanAno;
        }

        public void setBooleanAno(Boolean booleanAno) {
            this.booleanAno = booleanAno;
        }

        public Integer getAno() {
            return ano;
        }

        public void setAno(Integer ano) {
            this.ano = ano;
        }

        public Boolean getBooleanEmissao() {
            return booleanEmissao;
        }

        public void setBooleanEmissao(Boolean booleanEmissao) {
            this.booleanEmissao = booleanEmissao;
        }

        public String getEmissaoInicial() {
            return emissaoInicial;
        }

        public void setEmissaoInicial(String emissaoInicial) {
            this.emissaoInicial = emissaoInicial;
        }

        public String getEmissaoFinal() {
            return emissaoFinal;
        }

        public void setEmissaoFinal(String emissaoFinal) {
            this.emissaoFinal = emissaoFinal;
        }

        public Boolean getBooleanResposta() {
            return booleanResposta;
        }

        public void setBooleanResposta(Boolean booleanResposta) {
            this.booleanResposta = booleanResposta;
        }

        public String getRespostaInicial() {
            return respostaInicial;
        }

        public void setRespostaInicial(String respostaInicial) {
            this.respostaInicial = respostaInicial;
        }

        public String getRespostaFinal() {
            return respostaFinal;
        }

        public void setRespostaFinal(String respostaFinal) {
            this.respostaFinal = respostaFinal;
        }

        public Boolean getBooleanStatus() {
            return booleanStatus;
        }

        public void setBooleanStatus(Boolean booleanStatus) {
            this.booleanStatus = booleanStatus;
        }

        public Integer getIndexStatus() {
            return indexStatus;
        }

        public void setIndexStatus(Integer indexStatus) {
            this.indexStatus = indexStatus;
        }

        public List<SelectItem> getListaStatus() {
            return listaStatus;
        }

        public void setListaStatus(List<SelectItem> listaStatus) {
            this.listaStatus = listaStatus;
        }

        public Boolean getBooleanTipoCertidao() {
            return booleanTipoCertidao;
        }

        public void setBooleanTipoCertidao(Boolean booleanTipoCertidao) {
            this.booleanTipoCertidao = booleanTipoCertidao;
        }

        public Integer getIndexTipoCertidao() {
            return indexTipoCertidao;
        }

        public void setIndexTipoCertidao(Integer indexTipoCertidao) {
            this.indexTipoCertidao = indexTipoCertidao;
        }

        public List<SelectItem> getListaTipoCertidao() {
            return listaTipoCertidao;
        }

        public void setListaTipoCertidao(List<SelectItem> listaTipoCertidao) {
            this.listaTipoCertidao = listaTipoCertidao;
        }

        public Boolean getBooleanCidade() {
            return booleanCidade;
        }

        public void setBooleanCidade(Boolean booleanCidade) {
            this.booleanCidade = booleanCidade;
        }

        public Integer getIndexCidade() {
            return indexCidade;
        }

        public void setIndexCidade(Integer indexCidade) {
            this.indexCidade = indexCidade;
        }

        public List<SelectItem> getListaCidade() {
            return listaCidade;
        }

        public void setListaCidade(List<SelectItem> listaCidade) {
            this.listaCidade = listaCidade;
        }

    }

}
