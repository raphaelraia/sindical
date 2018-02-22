package br.com.rtools.academia.beans;

import br.com.rtools.academia.AcademiaSemana;
import br.com.rtools.academia.AcademiaServicoValor;
import br.com.rtools.academia.dao.AcademiaDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.HistoricoEmissaoGuias;
import br.com.rtools.associativo.MatriculaAcademia;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.LancamentoIndividualDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.escola.dao.MatriculaEscolaDao;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.DescontoPromocional;
import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.MovimentoInativo;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.ServicoValor;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.DescontoPromocionalDao;
import br.com.rtools.financeiro.dao.DescontoServicoEmpresaDao;
import br.com.rtools.financeiro.dao.LoteDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoValorDao;
import br.com.rtools.impressao.CarneEscola;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.ImageConverter;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.Sessions;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class MatriculaAcademiaBean implements Serializable {

    private MatriculaAcademia matriculaAcademia;
    private Fisica aluno;
    private Registro registro;
    private Pessoa responsavel;
    private Pessoa cobranca;
    private Juridica juridica;
    //private Pessoa pessoaAlunoMemoria;
    //private Pessoa pessoaResponsavelMemoria;
    private PessoaComplemento pessoaComplemento;
    private Movimento movimento;
    private Socios socios;
    private Socios sociosCobranca;
    private Lote lote;
    private String descricaoPesquisa;
    private String porPesquisa;
    private String comoPesquisa;
    private String message;
    private String messageStatusDebito;
    private String messageStatusEmpresa;
    private String valor;
    private String valorParcela;
    private String valorParcelaVencimento;
    private String valorLiquido;
    private String valorTaxa;
    private String target;
    private List<MatriculaAcademia> listaAcademia;
    private List<Movimento> listaMovimentos;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listaDiaVencimento;
    private List<SelectItem> listaModalidades;
    private List<SelectItem> listaPeriodosGrade;
    private List<SelectItem> listaDiaParcela;
    private List<SelectItem> listParceiro;
    private boolean taxa;
    private boolean ocultaBotaoSalvar;
    private boolean socio;
    private boolean desabilitaCamposMovimento;
    private boolean desabilitaGeracaoContrato;
    private boolean desabilitaDiaVencimento;
    private boolean ocultaParcelas;
    private boolean ocultaBotaoTarifaCartao;
    private boolean taxaCartao;
    private boolean matriculaAtiva;
    private Boolean disabled;
    private Boolean liberaAcessaFilial;
    private int idDiaVencimento;
    private int idModalidade;
    private Object idModalidadePesquisa;
    private int idPeriodoGrade;
    private int idServico;
    private int idDiaVencimentoPessoa;
    private int idFTipoDocumento;
    private int idDiaParcela;
    private Integer idParceiro;
    private Integer filial_id;
    private Integer filial_id_2;
    private double vTaxa;
    private double desconto;
    private double valorCartao;
    private String dataValidade;
    private String mensagemInadinplente;
    // FOTO
    private StreamedContent fotoStreamed = null;
    private String nomeFoto = "";

    private MatriculaAcademia matriculaAcademiaAntiga;
    private String valorLiquidoAntigo;
    private Double descontoServicoEmpresa;
    private String descontoServicoEmpresaString;
    private Boolean trocarMatriculaAcademia;
    private String periodoString;
    private String periodoEmissaoString;

    @PostConstruct
    public void init() {
        Sessions.remove("photoCapture");
        Sessions.remove("photoCamBean");
        matriculaAcademia = new MatriculaAcademia();
        matriculaAcademiaAntiga = new MatriculaAcademia();
        aluno = new Fisica();
        registro = new Registro();
        responsavel = new Pessoa();
        cobranca = null;
        juridica = new Juridica();
        pessoaComplemento = new PessoaComplemento();
        movimento = new Movimento();
        socios = new Socios();
        lote = new Lote();
        descricaoPesquisa = "";
        porPesquisa = "";
        comoPesquisa = "";
        message = "";
        messageStatusDebito = "";
        periodoString = "";
        periodoEmissaoString = "";
        messageStatusEmpresa = "";
        valor = "";
        valorParcela = "";
        valorParcelaVencimento = "";
        valorLiquido = "";
        valorLiquidoAntigo = "";
        valorTaxa = "";
        target = "#";
        listaAcademia = new ArrayList();
        listaMovimentos = new ArrayList();
        listaDiaVencimento = new ArrayList();
        listaModalidades = new ArrayList();
        listaPeriodosGrade = new ArrayList();
        listaDiaParcela = new ArrayList();
        listFiliais = new ArrayList();
        taxa = false;
        ocultaBotaoSalvar = false;
        socio = false;
        desabilitaCamposMovimento = false;
        desabilitaGeracaoContrato = false;
        desabilitaDiaVencimento = false;
        ocultaParcelas = true;
        ocultaBotaoTarifaCartao = true;
        matriculaAtiva = true;
        taxaCartao = false;
        loadListaDiaVencimento();
        idModalidade = 0;
        idModalidadePesquisa = null;
        idPeriodoGrade = 0;
        idServico = 0;
        idDiaVencimentoPessoa = 0;
        idFTipoDocumento = 0;
        vTaxa = 0;
        desconto = 0;
        valorCartao = 0;
        dataValidade = "";
        descontoServicoEmpresa = new Double(0);
        matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.livre(matriculaAcademia.getServicoPessoa().getDtEmissao(), "MM/yyyy"));
        getRegistro();
        disabled = false;

        getListaModalidades();
        pegarIdServico();
        calculoValor();
        calculoDesconto();
        filial_id = 0;
        filial_id_2 = 0;
        liberaAcessaFilial = false;
        loadLiberaAcessaFilial();
        trocarMatriculaAcademia = false;
    }

    public void loadListaDiaParcela() {
        listaDiaParcela.clear();
        idDiaParcela = 0;
        for (int i = 1; i <= 31; i++) {
            listaDiaParcela.add(new SelectItem(Integer.toString(i)));
        }

        idDiaParcela = idDiaVencimento;
    }

    public void loadListaDiaVencimento() {
        listaDiaVencimento.clear();
        idDiaVencimento = 0;

        if (listaDiaVencimento.isEmpty() || matriculaAcademia.getServicoPessoa().getPessoa().getId() != -1) {
            if (matriculaAcademia.getServicoPessoa().getId() == -1) {
                listaDiaVencimento.clear();
                int dia;
                if (matriculaAcademia.getServicoPessoa().getPessoa().getId() != -1) {
                    PessoaDao dbp = new PessoaDao();
                    pessoaComplemento = dbp.pesquisaPessoaComplementoPorPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId());

                    if (pessoaComplemento.getId() == -1) {
                        dia = getRegistro().getFinDiaVencimentoCobranca();
                    } else {
                        dia = pessoaComplemento.getNrDiaVencimento();
                    }

                } else {
                    dia = DataHoje.DataToArrayInt(matriculaAcademia.getServicoPessoa().getEmissao())[0];
                }

                for (int i = 1; i <= 31; i++) {
                    listaDiaVencimento.add(new SelectItem(Integer.toString(i)));
                    if (dia == i) {
                        idDiaVencimento = i;
                    }
                }
            } else {
                int dia = matriculaAcademia.getServicoPessoa().getNrDiaVencimento();
                for (int i = 1; i <= 31; i++) {
                    listaDiaVencimento.add(new SelectItem(Integer.toString(i)));
                    if (dia == i) {
                        idDiaVencimento = i;
                    }
                }
            }
        }

        loadListaDiaParcela();
//        listaDiaVencimento.clear();
//        idDiaVencimento = 0;
//        if (listaDiaVencimento.isEmpty()) {
//            for (int i = 1; i <= 31; i++) {
//                listaDiaVencimento.add(new SelectItem(Integer.toString(i)));
//            }
//        }
//
//        idDiaVencimento = Integer.parseInt(DataHoje.data().substring(0, 2));
    }

    public void atualizarDiaVencimento() {
        idDiaParcela = idDiaVencimento;
    }

    public void cancelarTrocaMatricula() {
        editar(matriculaAcademiaAntiga);
        matriculaAcademiaAntiga = new MatriculaAcademia();
        trocarMatriculaAcademia = false;
    }

    public void novoParaTrocaMatricula() {
        Pessoa s_pessoa = matriculaAcademia.getServicoPessoa().getPessoa();
        Pessoa s_cobranca = matriculaAcademia.getServicoPessoa().getCobranca();
        matriculaAcademiaAntiga = matriculaAcademia;
        valorLiquidoAntigo = valorLiquido;

        ServicoPessoa sp = matriculaAcademia.getServicoPessoa();
        matriculaAcademia = new MatriculaAcademia();

        matriculaAcademia.getServicoPessoa().setPessoa(s_pessoa);
        matriculaAcademia.getServicoPessoa().setCobranca(s_cobranca);

        registro = new Registro();
        cobranca = null;
        descricaoPesquisa = "";
        porPesquisa = "";
        comoPesquisa = "";
        message = "";
        messageStatusDebito = "";
        messageStatusEmpresa = "";
        valor = "";
        valorParcela = "";
        valorParcelaVencimento = "";
        valorLiquido = "";
        valorTaxa = "";
        target = "#";
        listaAcademia = new ArrayList();
        listaMovimentos = new ArrayList();
        listaDiaVencimento = new ArrayList();
        listaModalidades = new ArrayList();
        listaPeriodosGrade = new ArrayList();
        listaDiaParcela = new ArrayList();
        listFiliais = new ArrayList();
        taxa = false;
        ocultaBotaoSalvar = false;
        desabilitaCamposMovimento = false;
        desabilitaGeracaoContrato = false;
        desabilitaDiaVencimento = false;
        ocultaParcelas = true;
        ocultaBotaoTarifaCartao = true;
        matriculaAtiva = true;
        taxaCartao = false;
        idDiaVencimento = Integer.parseInt(DataHoje.data().substring(0, 2));
        idModalidade = 0;
        idModalidadePesquisa = null;
        idPeriodoGrade = 0;
        idServico = 0;
        idDiaVencimentoPessoa = 0;
        idFTipoDocumento = 0;
        vTaxa = 0;
        desconto = 0;
        valorCartao = 0;
        idDiaParcela = 0;
        dataValidade = "";
        matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.livre(matriculaAcademia.getServicoPessoa().getDtEmissao(), "MM/yyyy"));
        getRegistro();
        disabled = false;
        AcademiaDao academiaDao = new AcademiaDao();
        List<AcademiaServicoValor> list = academiaDao.listaServicoValorPorRotina();
        int idServicoMemoria = 0;
        int b = 0;
        getListaModalidades();
        for (int i = 0; i < list.size(); i++) {
            if (idServicoMemoria != list.get(i).getServicos().getId()) {
                idServicoMemoria = list.get(i).getServicos().getId();
                if (sp.getServicos().getId() == list.get(i).getServicos().getId()) {
                    idModalidade = b;
                    break;
                }
                b++;
            }
        }
        listaPeriodosGrade = new ArrayList();
        getListaPeriodosGrade();
        for (int i = 0; i < listaPeriodosGrade.size(); i++) {
            if (Integer.parseInt(listaPeriodosGrade.get(i).getDescription()) == matriculaAcademiaAntiga.getAcademiaServicoValor().getId()) {
                idPeriodoGrade = i;
                break;
            }
        }
        pegarIdServico();
        calculoValor();
        calculoDesconto();
        filial_id = 0;
        filial_id_2 = 0;
        liberaAcessaFilial = false;
        loadLiberaAcessaFilial();
        trocarMatriculaAcademia = true;

        List<Movimento> listMovimentoInativar = new MovimentoDao().findBy("pessoa", matriculaAcademiaAntiga.getServicoPessoa().getCobranca().getId(), matriculaAcademiaAntiga.getServicoPessoa().getServicos().getId(), false, true, false);
        if (!listMovimentoInativar.isEmpty()) {
            GenericaMensagem.warn("SISTEMA", "Matrícula com movimentos em aberto!");
        }

    }

    public void loadLiberaAcessaFilial() {
        if (new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    @PreDestroy
    public void destroy() {
        clear();
        clear(2);
    }

    public void calculoValor() {
        String valorx;
        if (aluno.getId() != -1) {
            Servicos se = (Servicos) new Dao().find(new Servicos(), idServico);
            if (se != null) {
                if (aluno.getPessoa().getSocios().getId() != -1) {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, aluno.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
                } else {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, null));
                }

                valor = Moeda.converteR$(valorx);

                if (matriculaAcademia.getId() == -1 && desconto == 0) {
                    Dao di = new Dao();
                    AcademiaServicoValor asv = (AcademiaServicoValor) di.find(new AcademiaServicoValor(), Integer.parseInt(getListaPeriodosGrade().get(idPeriodoGrade).getDescription()));
                    if (!asv.getFormula().isEmpty()) {
                        String calculoFormula = asv.getFormula().replace("valor", Moeda.substituiVirgula(valor));
                        if (!(new FunctionsDao().scriptSimples(calculoFormula)).isEmpty()) {
                            String valord = Moeda.converteR$(new FunctionsDao().scriptSimples(calculoFormula));
                            desconto = Moeda.subtracao(Moeda.converteUS$(valorx), Moeda.converteUS$(valord));
                        }
                    }
                }
            }
        }
    }

    public void calculoDesconto() {
        String valorx;
        if (aluno.getId() != -1) {

            Servicos se = (Servicos) new Dao().find(new Servicos(), idServico);
            if (se != null) {
                if (aluno.getPessoa().getSocios().getId() != -1) {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, aluno.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
                } else {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, null));
                }

                String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(aluno.getPessoa().getId(), se.getId(), DataHoje.dataHoje()));
                double calculo = Moeda.converteUS$(valorx_cheio) - (Moeda.converteUS$(valorx) - desconto);
                double valor_do_percentual = Moeda.converteUS$(Moeda.percentualDoValor(valorx_cheio, Moeda.converteR$Double(calculo)));

                if (desconto == 0) {
                    matriculaAcademia.getServicoPessoa().setNrDescontoString("0.0");
                } else {
                    matriculaAcademia.getServicoPessoa().setNrDesconto(valor_do_percentual);
                }

                if (aluno.getPessoa().getId() != -1) {
                    Integer idade = new DataHoje().calcularIdade(aluno.getDtNascimento());
                    List<ServicoValor> lsv = new MatriculaEscolaDao().listServicoValorPorServicoIdade(se.getId(), idade);
                    valorLiquido = (lsv.isEmpty()) ? valor : Moeda.converteR$Double(Moeda.converteUS$(Moeda.valorDoPercentual(valor, Moeda.converteR$Double(lsv.get(0).getDescontoAteVenc()))));
                    valorLiquido = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(valorLiquido), desconto));
                } else {
                    valorLiquido = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(valor), desconto));
                }
                if (descontoServicoEmpresa != null && !descontoServicoEmpresa.equals(0)) {
                    valorLiquido = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(valorLiquido), Moeda.converteUS$(getDescontoServicoEmpresaString())));
                }
            }
        }
    }

    public void load() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            getAluno();
        }
        if (GenericaSessao.exists("juridicaPesquisa")) {
            getJuridica();
        }
    }

    public void clear() {
        clear(0);
        clear(1);
    }

    public void clear(Integer tCase) {
        if (tCase == 0) {
            GenericaSessao.remove("matriculaAcademiaBean");
            GenericaSessao.remove("fisicaPesquisa");
            GenericaSessao.remove("juridicaPesquisa");
//            GenericaSessao.remove("uplophotoadBean");
//            GenericaSessao.remove("photoCamBean");
        }
        if (tCase == 1) {
//            try {
//                FileUtils.deleteDirectory(new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "/Cliente/" + ControleUsuarioBean.getCliente() + "/temp/" + "foto/" + new SegurancaUtilitariosBean().getSessaoUsuario().getId()));
//                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
//                if (f.exists()) {
//                    f.delete();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(FisicaBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        if (tCase == 2) {
//            GenericaSessao.remove("cropperBean");
//            GenericaSessao.remove("uploadBean");
//            GenericaSessao.remove("photoCamBean");
        }
    }

    public void salvarData() {
        if (matriculaAcademia.getServicoPessoa().getCobranca().getId() != -1) {
            Dao dao = new Dao();
            Pessoa pResponsavel = matriculaAcademia.getServicoPessoa().getCobranca();
            MatriculaEscolaDao med = new MatriculaEscolaDao();
            PessoaComplemento pc = med.pesquisaDataRefPessoaComplemto(pResponsavel.getId());
            if (pc == null || pc.getId() == -1) {
                pc = new PessoaComplemento();
                pc.setPessoa(pResponsavel);
                pc.setNrDiaVencimento(idDiaVencimento);
                dao.save(pc, true);
            } else {
                pc.setNrDiaVencimento(idDiaVencimento);
                dao.update(pc, true);
            }

            idDiaParcela = idDiaVencimento;
        }
    }

    public String save() {
        message = "";
        if (MacFilial.getAcessoFilial().getId() == -1) {
            message = "Para salvar convites não cortesia configurar Filial em sua estação trabalho!";
            return null;
        }
        if (matriculaAcademia.getServicoPessoa().getPessoa().getId() == -1) {
            message = "Pesquisar uma pessoa!";
            return null;
        }

        ConfiguracaoSocial cs = ConfiguracaoSocial.get();
        if (cs.getObrigatorioEmail()) {
            if (matriculaAcademia.getServicoPessoa().getPessoa().getEmail1().isEmpty()) {
                GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO PARA O ALUNO!");
                GenericaMensagem.warn("Sistema", "CORRIJA ESTE CADASTRO PARA REALIZAR ALTERAÇÕES!");
                message = "E-MAIL OBRIGATÓRIO PARA O ALUNO!";
                return null;
            }
        }
        if (matriculaAcademia.getServicoPessoa().getCobranca().getId() == -1) {
            message = "Pesquisar um responsável!";
            return null;
        }
        if (listaModalidades.isEmpty()) {
            message = "Cadastrar modalidades!";
            return null;
        }
        if (listaPeriodosGrade.isEmpty()) {
            message = "Cadastrar período grade!";
            return null;
        }
        if (valor.isEmpty()) {
            valor = "0";
        }
        matriculaAcademia.getServicoPessoa().setNrDiaVencimento(idDiaParcela);
        Dao dao = new Dao();
        matriculaAcademia.getServicoPessoa().setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 1));
        matriculaAcademia.setAcademiaServicoValor((AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaPeriodosGrade.get(idPeriodoGrade).getDescription())));

        List<ServicoValor> lsv = new MatriculaEscolaDao().listServicoValorPorServicoIdade(matriculaAcademia.getAcademiaServicoValor().getServicos().getId(), aluno.getIdade());

        if (lsv.isEmpty()) {
            message = "Idade do aluno não pertence à esta modalidade!";
            return null;
        }

        if (cobranca != null) {
            matriculaAcademia.getServicoPessoa().setCobranca(cobranca);
        } else {
            matriculaAcademia.getServicoPessoa().setCobranca(matriculaAcademia.getServicoPessoa().getCobranca());
        }
        if (matriculaAcademia.getServicoPessoa().isDescontoFolha()) {
            if (sociosCobranca.getId() != -1) {
                //matriculaAcademia.getServicoPessoa().setCobranca(matriculaAcademia.getServicoPessoa().getPessoa());
            }
        }
        if (responsavel != null && responsavel.getId() != -1) {
            matriculaAcademia.getServicoPessoa().setCobranca(responsavel);
        }
        NovoLog novoLog = new NovoLog();
        if (idParceiro != null && idParceiro != -1) {
            matriculaAcademia.getServicoPessoa().setParceiro((Pessoa) dao.find(new Pessoa(), idParceiro));
        } else {
            matriculaAcademia.getServicoPessoa().setParceiro(null);
        }
        if (matriculaAcademia.getId() == -1) {
            AcademiaDao academiaDao = new AcademiaDao();
            if (academiaDao.existeAlunoModalidadePeriodo(matriculaAcademia.getServicoPessoa().getPessoa().getId(), matriculaAcademia.getAcademiaServicoValor().getServicos().getId(), matriculaAcademia.getAcademiaServicoValor().getId(), matriculaAcademia.getServicoPessoa().getDtEmissao())) {
                message = "Aluno já cadastrado para esta modalidade!";
                return null;
            }
//            Integer quantidadeAlunosAcademia = 0;
//            if (matriculaAcademia.getAcademiaServicoValor().getServicos().getNrVagas() > 0) {
//                quantidadeAlunosAcademia = matriculaAcademia.getAcademiaServicoValor().getServicos().getNrVagas() - new FunctionsDao().quantidadeAlunosAcademia(matriculaAcademia.getAcademiaServicoValor().getServicos().getId());
//                if (quantidadeAlunosAcademia <= 0) {
//                    message = "Vagas esgotadas para essa modalidade! Limite de vagas: " + matriculaAcademia.getAcademiaServicoValor().getServicos().getNrVagas();
//                    return null;
//                }
//            }
            SocioCarteirinha socioCarteirinha = new SocioCarteirinha();
            SocioCarteirinhaDao scdb = new SocioCarteirinhaDao();
            // PESQUISA CARTEIRINHA SEM MODELO
            String validadeCarteirinha = "";
            DataHoje dh = new DataHoje();
            if (socios.getId() != -1) {
                // validadeCarteirinha = dh.incrementarMeses(socios.getMatriculaSocios().getCategoria().getGrupoCategoria().getNrValidadeMesCartao(), DataHoje.data());
                // modeloCarteirinha = scdb.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 122);
            } else {
                validadeCarteirinha = dh.incrementarMeses(cs.getValidadeMesesCartaoAcademia(), DataHoje.data());
            }
            ModeloCarteirinha modeloCarteirinha = scdb.pesquisaModeloCarteirinha(-1, 122);
            if (modeloCarteirinha == null) {
                message = "Informar modelo da carteirinha!";
                return null;
            }
            // CRIA CARTEIRINHA CASO NÃO EXISTA
            SocioCarteirinha scx = scdb.pesquisaCarteirinhaPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId(), modeloCarteirinha.getId());
            Boolean insert = false;
            if (scx == null || scx.getId() == -1) {
                socioCarteirinha.setEmissao("");
                socioCarteirinha.setCartao(0);
                socioCarteirinha.setVia(1);
                socioCarteirinha.setValidadeCarteirinha(validadeCarteirinha);
                socioCarteirinha.setPessoa(matriculaAcademia.getServicoPessoa().getPessoa());
                socioCarteirinha.setModeloCarteirinha(modeloCarteirinha);
                insert = true;
            } else {
                socioCarteirinha = null;
            }
            Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
            matriculaAcademia.setUsuario(usuario);
            matriculaAcademia.getServicoPessoa().setServicos(matriculaAcademia.getAcademiaServicoValor().getServicos());
            dao.openTransaction();
            if (socioCarteirinha != null) {
                if (!dao.save(socioCarteirinha)) {
                    dao.rollback();
                    message = "Erro ao adicionar sócio carteirinha!";
                    return null;
                }
                if (insert) {
                    socioCarteirinha.setCartao(socioCarteirinha.getId());
                    if (!dao.update(socioCarteirinha)) {
                        dao.rollback();
                        message = "Erro ao atualizar sócio carteirinha!";
                        return null;
                    }
                }
            }
            // DESCONTO SOCIAL DEFAULT
            matriculaAcademia.getServicoPessoa().setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
            if (!dao.save(matriculaAcademia.getServicoPessoa())) {
                dao.rollback();
                message = "Erro ao adicionar serviço pessoa!";
                return null;
            }
            matriculaAcademia.setEvt(null);
            matriculaAcademia.setValidade(dataValidade);
            if (!dao.save(matriculaAcademia)) {
                dao.rollback();
                message = "Erro ao adicionar registro!";
                return null;
            }

            MatriculaEscolaDao med = new MatriculaEscolaDao();
            pessoaComplemento = med.pesquisaDataRefPessoaComplemto(matriculaAcademia.getServicoPessoa().getCobranca().getId());
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
                pessoaComplemento.setNrDiaVencimento(idDiaVencimento);
                pessoaComplemento.setPessoa((Pessoa) dao.find(new Pessoa(), matriculaAcademia.getServicoPessoa().getCobranca().getId()));
                if (!dao.save(pessoaComplemento)) {
                    dao.rollback();
                    message = "Falha ao inserir pessoa complemento!";
                    return null;
                }
            }
            //pessoaAlunoMemoria = matriculaAcademia.getServicoPessoa().getPessoa();
            //pessoaResponsavelMemoria = matriculaAcademia.getServicoPessoa().getCobranca();
            if (trocarMatriculaAcademia) {
                List<Movimento> listMovimentoInativar = new MovimentoDao().findBy("pessoa", matriculaAcademiaAntiga.getServicoPessoa().getCobranca().getId(), matriculaAcademiaAntiga.getServicoPessoa().getServicos().getId(), false, true, false);
                for (int i = 0; i < listMovimentoInativar.size(); i++) {
                    listMovimentoInativar.get(i).setAtivo(false);
                    if (!dao.update(listMovimentoInativar.get(i))) {
                        message = "Falha ao inátivar movimentos!";
                        return null;
                    }
                    MovimentoInativo movimentoInativo = new MovimentoInativo();
                    movimentoInativo.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));
                    movimentoInativo.setDtData(new Date());
                    movimentoInativo.setMovimento(listMovimentoInativar.get(i));
                    movimentoInativo.setHistorico("TROCA DE MODALIDADE NA ACADEMIA!");
                    if (!dao.save(movimentoInativo)) {
                        message = "Falha ao inátivar movimentos!";
                        return null;
                    }
                }
            }
            trocarMatriculaAcademia = false;
            message = "Registro inserido com sucesso";
            novoLog.save(""
                    + "ID: " + matriculaAcademia.getId()
                    + " - Pessoa: (" + matriculaAcademia.getServicoPessoa().getPessoa().getId() + ") " + matriculaAcademia.getServicoPessoa().getPessoa().getNome()
                    + " - Cobrança: (" + matriculaAcademia.getServicoPessoa().getCobranca().getId() + ") " + matriculaAcademia.getServicoPessoa().getCobranca().getNome()
                    + " - Serviço: (" + matriculaAcademia.getAcademiaServicoValor().getServicos().getId() + ") " + matriculaAcademia.getAcademiaServicoValor().getServicos().getDescricao()
                    + " - Academia Servico Valor: (" + matriculaAcademia.getAcademiaServicoValor().getId() + ")"
                    + " - Parcelas: " + matriculaAcademia.getNumeroParcelas() + " "
            );

            dao.commit();
            return gerarMovimento();
        } else {
            MatriculaAcademia ma = (MatriculaAcademia) dao.find(matriculaAcademia);
            if (ma == null) {
                message = "Registro não existe no sistema, clique em novo e faça novamente a operaçao!";
                return null;
            }
            SocioCarteirinha socioCarteirinha = new SocioCarteirinha();
            SocioCarteirinhaDao scdb = new SocioCarteirinhaDao();
            // PESQUISA CARTEIRINHA SEM MODELO
            String validadeCarteirinha = "";
            DataHoje dh = new DataHoje();
            if (socios.getId() != -1) {
                // validadeCarteirinha = dh.incrementarMeses(socios.getMatriculaSocios().getCategoria().getGrupoCategoria().getNrValidadeMesCartao(), DataHoje.data());
                // modeloCarteirinha = scdb.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 122);
            } else {
                validadeCarteirinha = dh.incrementarAnos(5, DataHoje.data());
            }
            ModeloCarteirinha modeloCarteirinha = scdb.pesquisaModeloCarteirinha(-1, 122);
            if (modeloCarteirinha == null) {
                message = "Informar modelo da carteirinha!";
                return null;
            }
            // CRIA CARTEIRINHA CASO NÃO EXISTA
            SocioCarteirinha scx = scdb.pesquisaCarteirinhaPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId(), modeloCarteirinha.getId());
            Boolean insert = false;
            if (scx == null || scx.getId() == -1) {
                socioCarteirinha.setEmissao("");
                socioCarteirinha.setCartao(0);
                socioCarteirinha.setValidadeCarteirinha(validadeCarteirinha);
                socioCarteirinha.setVia(1);
                socioCarteirinha.setPessoa(matriculaAcademia.getServicoPessoa().getPessoa());
                socioCarteirinha.setModeloCarteirinha(modeloCarteirinha);
                insert = true;
            } else {
                socioCarteirinha = null;
            }
            dao.openTransaction();
            if (socioCarteirinha != null) {
                if (!dao.save(socioCarteirinha)) {
                    dao.rollback();
                    message = "Erro ao adicionar sócio carteirinha!";
                    return null;
                }
                if (insert) {
                    socioCarteirinha.setCartao(socioCarteirinha.getId());
                    if (!dao.update(socioCarteirinha)) {
                        dao.rollback();
                        message = "Erro ao atualizar sócio carteirinha!";
                        return null;
                    }
                }
            }
            if (!dao.update(matriculaAcademia.getServicoPessoa())) {
                dao.rollback();
                message = "Erro ao atualizar serviço pessoa!";
                return null;
            }
            String beforeUpdate = ""
                    + "ID: " + ma.getId()
                    + " - Pessoa: (" + ma.getServicoPessoa().getPessoa().getId() + ") " + ma.getServicoPessoa().getPessoa().getNome()
                    + " - Cobrança: (" + ma.getServicoPessoa().getCobranca().getId() + ") " + ma.getServicoPessoa().getCobranca().getNome()
                    + " - Serviço: (" + ma.getAcademiaServicoValor().getServicos().getId() + ") " + ma.getAcademiaServicoValor().getServicos().getDescricao()
                    + " - Academia Servico Valor: (" + ma.getAcademiaServicoValor().getId() + ")"
                    + " - Parcelas: " + ma.getNumeroParcelas() + " ";
            if (!dao.update(matriculaAcademia)) {
                dao.rollback();
                message = "Erro ao atualizar registro!";
                return null;
            }
//            pessoaAlunoMemoria = matriculaAcademia.getServicoPessoa().getPessoa();
//            pessoaResponsavelMemoria = matriculaAcademia.getServicoPessoa().getCobranca();
            message = "Registro atualizado com sucesso";
            novoLog.update(beforeUpdate,
                    "ID: " + matriculaAcademia.getId()
                    + " - Pessoa: (" + matriculaAcademia.getServicoPessoa().getPessoa().getId() + ") " + matriculaAcademia.getServicoPessoa().getPessoa().getNome()
                    + " - Cobrança: (" + matriculaAcademia.getServicoPessoa().getCobranca().getId() + ") " + matriculaAcademia.getServicoPessoa().getCobranca().getNome()
                    + " - Serviço: (" + matriculaAcademia.getAcademiaServicoValor().getServicos().getId() + ") " + matriculaAcademia.getAcademiaServicoValor().getServicos().getDescricao()
                    + " - Academia Servico Valor: (" + matriculaAcademia.getAcademiaServicoValor().getId() + ")"
                    + " - Parcelas: " + matriculaAcademia.getNumeroParcelas() + " "
            );
            dao.commit();
            if (matriculaAcademia.getAcademiaServicoValor().getPeriodo().getId() == 3) {
                new FunctionsDao().gerarMensalidades(matriculaAcademia.getServicoPessoa().getPessoa().getId(), matriculaAcademia.getServicoPessoa().getReferenciaVigoracao());
            }
        }

        salvarImagem();
        trocarMatriculaAcademia = false;
        return null;
    }

    public void salvarImagem() {
        if (!Diretorio.criar("Imagens/Fotos/Fisica")) {
            return;
        }

        File fotoTempx = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/temp/foto/" + Usuario.getUsuario().getId() + "/" + nomeFoto + ".png"));
        if (fotoTempx.exists()) {
            String path = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + nomeFoto + ".png");
            try {
                FileUtils.moveFile(fotoTempx, new File(path));
                fotoStreamed = ImageConverter.getImageStreamed(new File(path), "image/png");
            } catch (IOException ex) {
                ex.getMessage();
                GenericaMensagem.error("Atenção", "Erro ao salvar Foto, verifique as permissões de acesso!");
                return;
            }
//            FisicaDB db = new FisicaDao();
//            Fisica f = db.pesquisaFisicaPorPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId());

            File fotoAntiga = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + aluno.getFoto() + ".png"));
            if (fotoAntiga.exists()) {
                FileUtils.deleteQuietly(fotoAntiga);
            }

            Dao dao = new Dao();
            dao.openTransaction();
            aluno.setFoto(nomeFoto);
            dao.update(aluno);
            dao.commit();
        }
    }

    public void showImagemFisica() {
        String[] imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
        for (String imagensTipo1 : imagensTipo) {
            String path = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + aluno.getFoto() + "." + imagensTipo1);
            File fpath = new File(path);
            if (fpath.exists()) {
                fotoStreamed = ImageConverter.getImageStreamed(fpath, "image/png");
            }
        }
    }

    public void delete() {
        Dao di = new Dao();
        if (matriculaAcademia.getId() != -1) {
            di.openTransaction();
            listaMovimentos.clear();
            for (int i = 0; i < getListaMovimentos().size(); i++) {
                if (listaMovimentos.get(i).getBaixa() != null) {
                    message = "Não é possível excluir um registro com movimentos já baixados!";
                    di.rollback();
                    return;
                }
                if (!di.delete(listaMovimentos.get(i))) {
                    message = "Erro ao excluir movimento!";
                    di.rollback();
                    return;
                }
            }
            if (!listaMovimentos.isEmpty()) {
                if (!di.delete(listaMovimentos.get(0).getLote())) {
                    message = "Erro ao excluir lote!";
                    di.rollback();
                    return;
                }
            }
            if (!di.delete(matriculaAcademia)) {
                di.rollback();
                message = "Erro ao excluir registro!";
                return;
            }
            if (!di.delete(matriculaAcademia.getServicoPessoa())) {
                di.rollback();
                message = "Erro ao excluir serviço pessoa!";
                return;
            }
            message = "Registro excluído com sucesso";
            NovoLog novoLog = new NovoLog();
            novoLog.delete(""
                    + "ID: " + matriculaAcademia.getId()
                    + " - Pessoa: (" + matriculaAcademia.getServicoPessoa().getPessoa().getId() + ") " + matriculaAcademia.getServicoPessoa().getPessoa().getNome()
                    + " - Cobrança: (" + matriculaAcademia.getServicoPessoa().getCobranca().getId() + ") " + matriculaAcademia.getServicoPessoa().getCobranca().getNome()
                    + " - Serviço: (" + matriculaAcademia.getAcademiaServicoValor().getServicos().getId() + ") " + matriculaAcademia.getAcademiaServicoValor().getServicos().getDescricao()
                    + " - Academia Servico Valor: (" + matriculaAcademia.getAcademiaServicoValor().getId() + ")"
                    + " - Parcelas: " + matriculaAcademia.getNumeroParcelas() + " "
            );
            di.commit();
            clear();
            clear(2);
        }

    }

    public String editar(MatriculaAcademia ma) {
        this.trocarMatriculaAcademia = false;
        matriculaAcademiaAntiga = new MatriculaAcademia();
        disabled = false;
        socios = new Socios();
        mensagemInadinplente = "";
        matriculaAcademia = (MatriculaAcademia) new Dao().find(new MatriculaAcademia(), ma.getId());
        idDiaVencimentoPessoa = 0;
        if (matriculaAcademia.getEvt() != null || matriculaAcademia.getAcademiaServicoValor().getPeriodo().getId() == 3) {
            desabilitaCamposMovimento = true;
            desabilitaDiaVencimento = true;
        }

        Dao dao = new Dao();
        for (int i = 0; i < listaModalidades.size(); i++) {
            AcademiaServicoValor asv = (AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(i).getDescription()));
            if (asv.getServicos().getId() == ma.getServicoPessoa().getServicos().getId()) {
                idModalidade = i;
                break;
            }
        }
        listaPeriodosGrade.clear();
        getListaPeriodosGrade();
        for (int i = 0; i < listaPeriodosGrade.size(); i++) {
            if (Integer.parseInt(listaPeriodosGrade.get(i).getDescription()) == ma.getAcademiaServicoValor().getId()) {
                idPeriodoGrade = i;
                break;
            }
        }
        taxa = matriculaAcademia.isTaxa();
        taxaCartao = matriculaAcademia.isTaxaCartao();
        idDiaVencimento = ma.getServicoPessoa().getNrDiaVencimento();
        idFTipoDocumento = matriculaAcademia.getServicoPessoa().getTipoDocumento().getId();
        FisicaDao fisicaDB = new FisicaDao();
        aluno = fisicaDB.pesquisaFisicaPorPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId());
        if (aluno.getId() != -1) {
            getResponsavel();
            if (responsavel.getId() != -1) {
                MatriculaEscolaDao med = new MatriculaEscolaDao();
                PessoaComplemento pc = med.pesquisaDataRefPessoaComplemto(responsavel.getId());
                if (pc != null && pc.getId() != -1) {
                    this.idDiaVencimento = pc.getNrDiaVencimento();
                } else {
                    this.idDiaVencimento = Integer.parseInt(DataHoje.data().substring(0, 2));
                }
            }
            verificaSocio();
        }

        String valorx;
        if (aluno.getPessoa().getSocios().getId() != -1) {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), matriculaAcademia.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje(), 0, aluno.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
        } else {
            valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), matriculaAcademia.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje(), 0, null));
            loadListParceiro();
            idParceiro = matriculaAcademia.getServicoPessoa().getParceiro() != null ? matriculaAcademia.getServicoPessoa().getParceiro().getId() : -1;
            if (idParceiro != -1) {
                dse();
            }
        }
        String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(aluno.getPessoa().getId(), matriculaAcademia.getAcademiaServicoValor().getServicos().getId(), DataHoje.dataHoje()));

        if (matriculaAcademia.getServicoPessoa().getNrDesconto() != 0) {
            desconto = Moeda.subtracao(Moeda.converteUS$(valorx), Moeda.converteUS$(Moeda.valorDoPercentual(valorx_cheio, Double.toString(matriculaAcademia.getServicoPessoa().getNrDesconto()))));
        } else {
            desconto = 0;
        }

        pegarIdServico();
        atualizaValor();

        GenericaSessao.put("linkClicado", true);
        loadListaDiaParcela();
        clear(1);
        clear(2);
        showImagemFisica();
        return "matriculaAcademia";
    }

    public void inative() {
        if (matriculaAcademia.getServicoPessoa().isAtivo()) {
            if (matriculaAcademia.getMotivoInativacao().isEmpty() || matriculaAcademia.getMotivoInativacao().length() < 5) {
                GenericaMensagem.error("Atenção", "Digite um motivo de inativação válido!");
                return;
            }

            matriculaAcademia.getServicoPessoa().setAtivo(false);
            matriculaAcademia.setDtInativo(new Date());
            Dao dao = new Dao();

            dao.openTransaction();

            if (!dao.update(matriculaAcademia)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao atualizar matrícula!");
                return;
            }

            if (!dao.update(matriculaAcademia.getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao atualizar serviço pessoa!");
                return;
            }

            desabilitaCamposMovimento = true;
            desabilitaDiaVencimento = true;
            listaAcademia.clear();
            try {
                NovoLog novoLog = new NovoLog();
                novoLog.setTabela("matr_academia");
                novoLog.setCodigo(matriculaAcademia.getId());
                novoLog.update("", ""
                        + "ID: " + matriculaAcademia.getId()
                        + " - Pessoa: (" + matriculaAcademia.getServicoPessoa().getPessoa().getId() + ") " + matriculaAcademia.getServicoPessoa().getPessoa().getNome()
                        + " - Cobrança: (" + matriculaAcademia.getServicoPessoa().getCobranca().getId() + ") " + matriculaAcademia.getServicoPessoa().getCobranca().getNome()
                        + " - Serviço: (" + matriculaAcademia.getAcademiaServicoValor().getServicos().getId() + ") " + matriculaAcademia.getAcademiaServicoValor().getServicos().getDescricao()
                        + " - Motivo: " + matriculaAcademia.getMotivoInativacao()
                );
            } catch (Exception e) {
                GenericaMensagem.error("Atenção", e.getMessage());
                dao.rollback();
                return;
            }

            dao.commit();
            GenericaMensagem.info("Sucesso", "Matrícula Inativada");
        }
    }

    public void inativaMatriculaTrocada(Dao dao) {
        if (matriculaAcademiaAntiga.getServicoPessoa().isAtivo()) {
            matriculaAcademiaAntiga.getServicoPessoa().setAtivo(false);
            matriculaAcademiaAntiga.setMotivoInativacao("TROCA DE MATRÍCULA PARA [" + matriculaAcademia.getId() + "]");
            matriculaAcademiaAntiga.setDtInativo(new Date());

            dao.update(matriculaAcademiaAntiga);
            dao.update(matriculaAcademiaAntiga.getServicoPessoa());

            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("matr_academia");
            novoLog.setCodigo(matriculaAcademia.getId());
            novoLog.update("** TROCA DE MATRÍCULA ** \n"
                    + "ID: " + matriculaAcademiaAntiga.getId() + " \n"
                    + " - Pessoa: (" + matriculaAcademiaAntiga.getServicoPessoa().getPessoa().getId() + ") " + matriculaAcademiaAntiga.getServicoPessoa().getPessoa().getNome() + " \n"
                    + " - Cobrança: (" + matriculaAcademiaAntiga.getServicoPessoa().getCobranca().getId() + ") " + matriculaAcademiaAntiga.getServicoPessoa().getCobranca().getNome() + " \n"
                    + " - Serviço: (" + matriculaAcademiaAntiga.getAcademiaServicoValor().getServicos().getId() + ") " + matriculaAcademiaAntiga.getAcademiaServicoValor().getServicos().getDescricao() + " \n"
                    + " - Motivo: " + matriculaAcademiaAntiga.getMotivoInativacao() + " \n"
                    + " - Valor: " + valorLiquidoAntigo,
                    "ID: " + matriculaAcademia.getId() + " \n"
                    + " - Pessoa: (" + matriculaAcademia.getServicoPessoa().getPessoa().getId() + ") " + matriculaAcademia.getServicoPessoa().getPessoa().getNome() + " \n"
                    + " - Cobrança: (" + matriculaAcademia.getServicoPessoa().getCobranca().getId() + ") " + matriculaAcademia.getServicoPessoa().getCobranca().getNome() + " \n"
                    + " - Serviço: (" + matriculaAcademia.getAcademiaServicoValor().getServicos().getId() + ") " + matriculaAcademia.getAcademiaServicoValor().getServicos().getDescricao() + " \n"
                    + " - Valor: " + valorLiquido
            );
        }
    }

    public MatriculaAcademia getMatriculaAcademia() {
        getListaModalidades();
        getListaPeriodosGrade();
        if (socio == false) {
            getJuridica();
        }
        if (cobranca == null) {
            Dao di = new Dao();
            FunctionsDao functionsDB = new FunctionsDao();
            if (matriculaAcademia.getServicoPessoa().isDescontoFolha()) {
                int idResponsavel = functionsDB.responsavel(matriculaAcademia.getServicoPessoa().getPessoa().getId(), matriculaAcademia.getServicoPessoa().isDescontoFolha());
                if (idResponsavel != -1) {
                    cobranca = (Pessoa) di.find(new Pessoa(), idResponsavel);
                } else {
                    cobranca = matriculaAcademia.getServicoPessoa().getCobranca();
                }
            } else {
                int idResponsavelEmpresa = functionsDB.responsavel(aluno.getPessoa().getId(), true);
                if (idResponsavelEmpresa != -1) {
                    JuridicaDao juridicaDB = new JuridicaDao();
                    Juridica juridicaB = juridicaDB.pesquisaJuridicaPorPessoa(idResponsavelEmpresa);
                    if (juridicaB != null) {
                        if (juridicaB.getId() != -1) {
                            cobranca = (Pessoa) di.find(new Pessoa(), idResponsavelEmpresa);
                        } else {
                            cobranca = matriculaAcademia.getServicoPessoa().getCobranca();
                        }
                    } else {
                        cobranca = matriculaAcademia.getServicoPessoa().getCobranca();
                    }
                } else {
                    cobranca = matriculaAcademia.getServicoPessoa().getCobranca();
                }
            }
            if (cobranca.getId() == -1) {
                cobranca = null;
            }
        }
        if (matriculaAcademia.getServicoPessoa().getCobranca().getId() != -1) {
            if (matriculaAcademia.getId() == -1) {
                JuridicaDao juridicaDB = new JuridicaDao();
                Juridica j = juridicaDB.pesquisaJuridicaPorPessoa(matriculaAcademia.getServicoPessoa().getCobranca().getId());
                if (j != null) {
                    verificaSeContribuinteInativo();
                }
            }
        }
        return matriculaAcademia;
    }

    public void setMatriculaAcademia(MatriculaAcademia matriculaAcademia) {
        this.matriculaAcademia = matriculaAcademia;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MatriculaAcademia> getListaAcademia() {
        return listaAcademia;
    }

    public void setListaAcademia(List<MatriculaAcademia> listaAcademia) {
        this.listaAcademia = listaAcademia;
    }

    public List<SelectItem> getListaDiaVencimento() {
        return listaDiaVencimento;
    }

    public void setListaDiaVencimento(List<SelectItem> listaDiaVencimento) {
        this.listaDiaVencimento = listaDiaVencimento;
    }

    public List<SelectItem> getListaModalidades() {
        if (listaModalidades.isEmpty()) {
            AcademiaDao academiaDao = new AcademiaDao();
            List<AcademiaServicoValor> list = academiaDao.listaServicoValorPorRotina();
            int idServicoMemoria = 0;
            int b = 0;
            for (int i = 0; i < list.size(); i++) {
                if (idServicoMemoria != list.get(i).getServicos().getId()) {
                    listaModalidades.add(new SelectItem(b, list.get(i).getServicos().getDescricao(), Integer.toString(list.get(i).getId())));
                    idServicoMemoria = list.get(i).getServicos().getId();
                    b++;
                }
            }
            if (matriculaAcademia.getId() == -1) {
                // TRAZ O SERVIÇO MAIS USADO
                Integer mainId = academiaDao.findMainId();
                if (mainId != null) {
                    if (!listaModalidades.isEmpty()) {
                        for (int i = 0; i < listaModalidades.size(); i++) {
                            if (Integer.parseInt(listaModalidades.get(i).getDescription()) == mainId) {
                                idModalidade = i;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return listaModalidades;
    }

    public void setListaModalidades(List<SelectItem> listaModalidades) {
        this.listaModalidades = listaModalidades;
    }

    public List<SelectItem> getListaPeriodosGrade() {

        if (listaPeriodosGrade.isEmpty()) {
            if (!listaModalidades.isEmpty()) {
                // idPeriodoGrade = 0;
                // AcademiaDao db = new AcademiaDao();

                Dao di = new Dao();
                AcademiaDao academiaDao = new AcademiaDao();
                // List<AcademiaServicoValor> listaAcademiaServicoValor = di.list(new AcademiaServicoValor(), true);
                AcademiaServicoValor asv = (AcademiaServicoValor) di.find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription()));
                List<AcademiaServicoValor> listaAcademiaServicoValor = academiaDao.listaAcademiaServicoValorPorServico(asv.getServicos().getId());

                for (int w = 0; w < listaAcademiaServicoValor.size(); w++) {
                    // ALTERADO PELO ROGÉRIO CHAMADO #2276
//                    String text = "";
//                    List<AcademiaSemana> listaAcademiaSemana = academiaDao.listaAcademiaSemana(listaAcademiaServicoValor.get(w).getId());
//                    for (int i = 0; i < listaAcademiaSemana.size(); i++) {
//                        text += listaAcademiaSemana.get(i).getSemana().getDescricao().substring(0, 3) + ": " + listaAcademiaSemana.get(i).getAcademiaGrade().getHoraInicio() + " às " + listaAcademiaSemana.get(i).getAcademiaGrade().getHoraFim() + " ";
//                        //listaPeriodosGrade.add(new SelectItem(i, text, Integer.toString(listaAcademiaSemana.get(i).getId())));
//                    }

//                    String text = listaAcademiaServicoValor.get(w).getPeriodo().getDescricao() + " - " + text;
//                    listaPeriodosGrade.add(new SelectItem(w, text, Integer.toString(listaAcademiaServicoValor.get(w).getId())));

                    String text = "Período - " + listaAcademiaServicoValor.get(w).getDescricao();
                    
                    listaPeriodosGrade.add(new SelectItem(w, text, Integer.toString(listaAcademiaServicoValor.get(w).getId())));
                }

                Collections.sort(listaPeriodosGrade, new Comparator<SelectItem>() {
                    @Override
                    public int compare(SelectItem sItem1, SelectItem sItem2) {
                        String sItem1Label = sItem1.getLabel();
                        String sItem2Label = sItem2.getLabel();

                        return (sItem1Label.compareToIgnoreCase(sItem2Label));
                    }
                });

                List<SelectItem> list = new ArrayList<>();
                for (int i = 0; i < listaPeriodosGrade.size(); i++) {
                    list.add(new SelectItem(i, listaPeriodosGrade.get(i).getLabel(), listaPeriodosGrade.get(i).getDescription()));
                }

                listaPeriodosGrade = list;

//                AcademiaDao academiaDao = new AcademiaDao();
//                Dao di = new Dao();
//                List<AcademiaServicoValor> list = academiaDao.listaAcademiaServicoValorPorServico(((AcademiaServicoValor) di.find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription()))).getServicos().getId());
//                List<AcademiaSemana> listSemana = new ArrayList<AcademiaSemana>();
//                for (int i = 0; i < list.size(); i++) {
//                    listSemana.clear();
//                    //listSemana = academiaDao.listaAcademiaSemana(list.get(i).getAcademiaGrade().getId());
//                    String periodoSemana = "";
//                    for (int j = 0; j < listSemana.size(); j++) {
//                        if (j == 0) {
//                            periodoSemana += semanaResumo(listSemana.get(j).getSemana().getDescricao());
//                        } else {
//                            periodoSemana += " - " + semanaResumo(listSemana.get(j).getSemana().getDescricao());
//                        }
//                    }
//                    //listaPeriodosGrade.add(new SelectItem(i, list.get(i).getPeriodo().getDescricao() + " - " + list.get(i).getAcademiaGrade().getHoraInicio() + "-" + list.get(i).getAcademiaGrade().getHoraFim() + " - " + periodoSemana, Integer.toString(list.get(i).getId())));
//                }
            }
            if (matriculaAcademia.getId() == -1) {
                Dao dao = new Dao();
                AcademiaServicoValor asv = (AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaPeriodosGrade.get(idPeriodoGrade).getDescription()));
                if (asv.getPeriodo().getId() == 3) {
                    // ROGÉRIO PEDIU PRA COMENTAR 15/02/2016 PORQUE ESTAMOS TRATANDO A VIGORAÇÃO DE ACORDO COM O CÁLCULO DE TAXA PROPORCIONAL REFERÊNCIA #1226
                    //DataHoje dh = new DataHoje();
                    //String novaData = dh.incrementarMeses(1, DataHoje.data());
                    //matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.converteDataParaReferencia(novaData));
                }
            }
        }
        return listaPeriodosGrade;
    }

    public void setListaPeriodosGrade(List<SelectItem> listaPeriodosGrade) {
        this.listaPeriodosGrade = listaPeriodosGrade;
    }

    public void carregaParcelas() {
        Dao di = new Dao();
        AcademiaServicoValor asv = (AcademiaServicoValor) di.find(new AcademiaServicoValor(), Integer.parseInt(getListaPeriodosGrade().get(idPeriodoGrade).getDescription()));
        int id = asv.getPeriodo().getId();
        switch (id) {
            case 5:
                ocultaParcelas = false;
                break;
            case 6:
                ocultaParcelas = false;
                break;
            case 7:
                ocultaParcelas = false;
                break;
            default:
                ocultaParcelas = true;
        }
        if (matriculaAcademia.getNumeroParcelas() == 0 || matriculaAcademia.getNumeroParcelas() == asv.getNumeroParcelas()) {
            matriculaAcademia.setNumeroParcelas(asv.getNumeroParcelas());
        }
    }

    public boolean isTaxa() {
        matriculaAcademia.setTaxa(taxa);
        return taxa;
    }

    public void setTaxa(boolean taxa) {
        this.taxa = taxa;
    }

    public Fisica getAluno() {
        if (!GenericaSessao.exists("fisicaPesquisa") && !GenericaSessao.exists("pessoaPesquisa")) {
            return aluno;
        }

        if (!GenericaSessao.exists("pesquisaFisicaTipo")) {
            return aluno;
        }

        String tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo", true);

        clear(1);
        disabled = false;
        mensagemInadinplente = "";
        socios = new Socios();
        if (tipoFisica.equals("aluno")) {
            aluno = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            verificaSocio();
            LancamentoIndividualDao dbl = new LancamentoIndividualDao();
            if (!dbl.listaSerasa(aluno.getPessoa().getId()).isEmpty()) {
                GenericaMensagem.warn("PESSOA", aluno.getPessoa().getNome() + " contém o nome no Serasa!");
            }
            if (socios != null && socios.getId() != -1) {
                // PESSOA ASSOCIADA
                matriculaAcademia.getServicoPessoa().setCobranca(retornaResponsavel(aluno.getPessoa().getId(), true));
            } else {
                // PESSOA NÁO ASSOCIADA
                matriculaAcademia.getServicoPessoa().setCobranca(retornaResponsavel(aluno.getPessoa().getId(), false));
                loadListParceiro();
            }

            matriculaAcademia.getServicoPessoa().setPessoa(aluno.getPessoa());
        } else {
            verificaSocio();
            if (socios != null && socios.getId() != -1) {
                // PESSOA ASSOCIADA
                matriculaAcademia.getServicoPessoa().setCobranca(retornaResponsavel(aluno.getPessoa().getId(), true));
            } else {
                // PESSOA NÁO ASSOCIADA
                matriculaAcademia.getServicoPessoa().setCobranca(retornaResponsavel(aluno.getPessoa().getId(), false));
            }
            GenericaSessao.remove("pessoaPesquisa");
        }
        pegarIdServico();
        //calculaValorLiquido();        
        atualizaValor();
        loadListaDiaVencimento();

        return aluno;
    }

    public Pessoa retornaResponsavel(Integer id_pessoa, boolean associada) {
        if (associada) {
            responsavel = new FunctionsDao().titularDaPessoa(id_pessoa);
        } else {
            if (GenericaSessao.exists("pessoaPesquisa")) {
                responsavel = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");
            } else {
                responsavel = (Pessoa) new Dao().find(new Pessoa(), id_pessoa);
            }

            // RESPONSAVEL FISICA
            FisicaDao dbf = new FisicaDao();
            Fisica fi = dbf.pesquisaFisicaPorPessoa(responsavel.getId());
            if (fi != null) {
                DataHoje dh = new DataHoje();
                int idade = dh.calcularIdade(fi.getNascimento());
                if (idade < 18) {
                    GenericaMensagem.warn("RESPONSÁVEL", responsavel.getNome() + " não é maior de idade!");
                    return responsavel = new Pessoa();
                }
            } else {
                // RESPONSAVEL JURIDICA
                // POR ENQUANTO NÃO FAZ NADA
                GenericaMensagem.warn("RESPONSÁVEL", "Pessoa Juridica não disponível no momento!");
                return responsavel = new Pessoa();
            }
        }

        Socios s = responsavel.getSocios();
        if (s != null && s.getId() != -1) {
            if (responsavel.getId() != s.getMatriculaSocios().getTitular().getId()) {
                GenericaMensagem.warn("RESPONSÁVEL", responsavel.getNome() + " é um sócio dependente!");
                return responsavel = new Pessoa();
            }
        }

        // MENSAGEM SE POSSUI DÉBITOS
        if (new FunctionsDao().inadimplente(responsavel.getId())) {
            GenericaMensagem.warn("RESPONSÁVEL", responsavel.getNome() + " possui débitos com o Sindicato!");
        }

        // ENDEREÇO OBRIGATÓRIO
        JuridicaDao dbj = new JuridicaDao();
        List lista_pe = dbj.pesquisarPessoaEnderecoJuridica(responsavel.getId());
        if (lista_pe.isEmpty()) {
            GenericaMensagem.warn("RESPONSÁVEL", responsavel.getNome() + " não possui endereço cadastrado!");
            return responsavel = new Pessoa();
        }

        // CADASTRO NO SERASA
        LancamentoIndividualDao dbl = new LancamentoIndividualDao();
        if (!dbl.listaSerasa(responsavel.getId()).isEmpty()) {
            GenericaMensagem.warn("PESSOA", responsavel.getNome() + " contém o nome no Serasa!");
        }

        PessoaDao pdb = new PessoaDao();
        pessoaComplemento = new PessoaComplemento();
        pessoaComplemento = pdb.pesquisaPessoaComplementoPorPessoa(responsavel.getId());

        return responsavel;
    }

//    public Fisica getAlunoxxx() {
//        if (GenericaSessao.exists("fisicaPesquisa")) {
//            clear(1);
//            disabled = false;
//            MatriculaEscolaDao med = new MatriculaEscolaDao();
//            if (GenericaSessao.exists("pesquisaFisicaTipo")) {
//                socios = new Socios();
//                mensagemInadinplente = "";
//                String tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo", true);
//                switch (tipoFisica) {
//                    case "aluno":
//                        valorTaxa = "";
//                        taxa = false;
//                        aluno = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
//                        if (matriculaAcademia.getServicoPessoa().getPessoa().getId() == -1) {
//                            pessoaAlunoMemoria = aluno.getPessoa();
//                        } else {
//                            if (aluno.getPessoa().getId() != matriculaAcademia.getServicoPessoa().getPessoa().getId()) {
//                                pessoaAlunoMemoria = aluno.getPessoa();
//                            }
//                        }
//                        if (aluno.getId() != -1) {
//                            getResponsavel();
//                            verificaSocio();
//                        }
//                        if (responsavel.getId() != -1) {
//                            pessoaComplemento = new PessoaComplemento();
//                            pessoaComplemento = med.pesquisaDataRefPessoaComplemto(responsavel.getId());
//                            if (pessoaComplemento != null && pessoaComplemento.getId() != -1) {
//                                this.idDiaVencimentoPessoa = pessoaComplemento.getNrDiaVencimento();
//                                this.idDiaVencimento = pessoaComplemento.getNrDiaVencimento();
//                            }
//                            matriculaAcademia.getServicoPessoa().setCobranca(responsavel);
//                        }
//                        matriculaAcademia.getServicoPessoa().setPessoa(aluno.getPessoa());
//                        matriculaAcademia.getServicoPessoa().setCobranca(responsavel);
//                        pegarIdServico();
//                        atualizaValor();
//                        //calculaValorLiquido();
//                        GenericaSessao.remove("juridicaPesquisa");
//                        if (matriculaAcademia.getServicoPessoa().getPessoa().getId() != -1 || matriculaAcademia.getServicoPessoa().getCobranca().getId() != -1) {
//                            if (new FunctionsDao().inadimplente(matriculaAcademia.getServicoPessoa().getCobranca().getId()) || new FunctionsDao().inadimplente(matriculaAcademia.getServicoPessoa().getPessoa().getId())) {
//                                responsavel = new Pessoa();
//                                matriculaAcademia.getServicoPessoa().setCobranca(responsavel);
//                                mensagemInadinplente = "Aluno em Débito!";
//                                GenericaMensagem.fatal("Atenção", "Aluno em Débito!");
//                                disabled = true;
//                                return null;
//                            }
//                        }
//                        // verificaDebitosResponsavel(matriculaAcademia.getServicoPessoa().getCobranca());
//                        break;
//                    case "responsavel":
//                        socios = new Socios();
//                        Pessoa resp = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
//                        FunctionsDB functionsDB = new FunctionsDao();
//                        int idade = functionsDB.idade("dt_nascimento", "current_date", resp.getId());
//                        if (idade >= 18) {
//                            if (matriculaAcademia.getServicoPessoa().getPessoa().getId() != -1 || matriculaAcademia.getServicoPessoa().getCobranca().getId() != -1) {
//                                if (new FunctionsDao().inadimplente(matriculaAcademia.getServicoPessoa().getCobranca().getId()) || new FunctionsDao().inadimplente(matriculaAcademia.getServicoPessoa().getPessoa().getId())) {
//                                    mensagemInadinplente = "Aluno em Débito!";
//                                    GenericaMensagem.fatal("Atenção", "Aluno em Débito!");
//                                    disabled = true;
//                                    return null;
//                                }
//                            }
//                            if (med.verificaPessoaEnderecoDocumento("fisica", resp.getId())) {
//                                matriculaAcademia.getServicoPessoa().setCobranca(resp);
//                            }
//                        } else {
//                            GenericaMensagem.warn("Validação", "Responsável deve ser maior de idade!");
//                        }
//                        GenericaSessao.remove("juridicaPesquisa");
//                        //                    verificaDebitosResponsavel(matriculaAcademia.getServicoPessoa().getCobranca());
//                        pessoaComplemento = new PessoaComplemento();
//                        pessoaComplemento = med.pesquisaDataRefPessoaComplemto(matriculaAcademia.getServicoPessoa().getCobranca().getId());
//                        if (pessoaComplemento != null) {
//                            this.idDiaVencimentoPessoa = pessoaComplemento.getNrDiaVencimento();
//                            this.idDiaVencimento = pessoaComplemento.getNrDiaVencimento();
//                        }
//                        break;
//                }
//            }
//            if (matriculaAcademia.getServicoPessoa().getCobranca().getId() == -1) {
//                pessoaResponsavelMemoria = responsavel;
//            } else {
//                if (responsavel.getId() != matriculaAcademia.getServicoPessoa().getCobranca().getId()) {
//                    pessoaResponsavelMemoria = responsavel;
//                }
//            }
//        }
//        getSocios();
//        return aluno;
//    }
    public void setAluno(Fisica aluno) {
        this.aluno = aluno;
    }

    public Pessoa getResponsavel() {
        if (aluno.getId() != -1) {
            FunctionsDao functionsDB = new FunctionsDao();
            int titularResponsavel = functionsDB.responsavel(aluno.getPessoa().getId(), matriculaAcademia.getServicoPessoa().isDescontoFolha());
            if (titularResponsavel > -1 && titularResponsavel > 0) {
                Dao di = new Dao();
                responsavel = (Pessoa) di.find(new Pessoa(), titularResponsavel);
            }
        } else {
            responsavel = new Pessoa();
        }
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public int getIdDiaVencimento() {
        return idDiaVencimento;
    }

    public void setIdDiaVencimento(int idDiaVencimento) {
        this.idDiaVencimento = idDiaVencimento;
    }

    public int getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(int idModalidade) {
        this.idModalidade = idModalidade;
    }

    public int getIdPeriodoGrade() {
        return idPeriodoGrade;
    }

    public void setIdPeriodoGrade(int idPeriodoGrade) {
        this.idPeriodoGrade = idPeriodoGrade;
    }

    public void verificaDebitosResponsavel(Pessoa responsavelPessoa) {
        messageStatusDebito = "";
        setOcultaBotaoSalvar(false);
        if (responsavelPessoa.getId() != -1) {
            MovimentoDao dao = new MovimentoDao();
            if (dao.existeDebitoPessoa(responsavelPessoa, null)) {
                messageStatusDebito = "Responsável possui débitos!";
                setOcultaBotaoSalvar(true);
            }
        }
    }

    public String getMensagemStatusDebito() {
        return messageStatusDebito;
    }

    public void setMensagemStatusDebito(String messageStatusDebito) {
        this.messageStatusDebito = messageStatusDebito;
    }

    public boolean isOcultaBotaoSalvar() {
        return ocultaBotaoSalvar;
    }

    public void setOcultaBotaoSalvar(boolean ocultaBotaoSalvar) {
        this.ocultaBotaoSalvar = ocultaBotaoSalvar;
    }

    public void pegarIdServico() {
        if (!listaModalidades.isEmpty()) {
            Dao di = new Dao();
            idServico = ((AcademiaServicoValor) (di.find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription())))).getServicos().getId();
        }
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public void verificaSocio() {
        //SociosDB dB = new SociosDao();
//        Socios sociosx = dB.pesquisaSocioPorPessoa(aluno.getPessoa().getId());
//        if (sociosx != null) {
//            socio = sociosx.getId() != -1;
//        }
        socios = aluno.getPessoa().getSocios();
        socio = socios != null && socios.getId() != -1;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(String valorParcela) {
        this.valorParcela = valorParcela;
    }

    public String getValorParcelaVencimento() {
        return valorParcelaVencimento;
    }

    public void setValorParcelaVencimento(String valorParcelaVencimento) {
        this.valorParcelaVencimento = valorParcelaVencimento;
    }

    public String getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(String valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getValorTaxa() {
        if (vTaxa > 0) {
            valorTaxa = "" + vTaxa;
        } else {
            valorTaxa = "";
        }
        return valorTaxa;
    }

    public void setValorTaxa(String valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public Double getDescontoServicoEmpresa() {
        return descontoServicoEmpresa;
    }

    public void setDescontoServicoEmpresa(Double descontoServicoEmpresa) {
        this.descontoServicoEmpresa = descontoServicoEmpresa;
    }

    public String getDescontoServicoEmpresaString() {
        try {
            return Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(getValor()), Moeda.converteUS$(Moeda.valorDoPercentual(getValor(), Moeda.converteR$Double(descontoServicoEmpresa)))));
        } catch (Exception e) {
            return "0";
        }
    }

    public String getValorTaxaString() {
        return Moeda.substituiVirgula(valorTaxa);
    }

    public void setValorTaxaString(String valorTaxa) {
        this.valorTaxa = Moeda.substituiVirgula(valorTaxa);
    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

    public int getIdDiaVencimentoPessoa() {
        return idDiaVencimentoPessoa;
    }

    public void setIdDiaVencimentoPessoa(int idDiaVencimentoPessoa) {
        this.idDiaVencimentoPessoa = idDiaVencimentoPessoa;
    }

    public void listener(String tcase) {
        if (tcase.equals("recalcular1")) {
            recalcular();
            loadListParceiro();
        }
        if (tcase.equals("descontoServicoEmpresa")) {
            dse();
            recalcular1();
        }
        if (tcase.equals("recalcular_troca")) {
            desconto = 0;
            // pegarIdServico();
            // atualizaValor();
            //calculaValorLiquido();
            listaPeriodosGrade.clear();
            getListaPeriodosGrade();
            if (matriculaAcademia.getId() == -1) {
                Dao dao = new Dao();
                AcademiaServicoValor asv = (AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaPeriodosGrade.get(idPeriodoGrade).getDescription()));
                if (asv.getPeriodo().getId() == 3) {
                    // ROGÉRIO PEDIU PRA COMENTAR 15/02/2016 PORQUE ESTAMOS TRATANDO A VIGORAÇÃO DE ACORDO COM O CÁLCULO DE TAXA PROPORCIONAL REFERÊNCIA #1226
                    //DataHoje dh = new DataHoje();
                    //String novaData = dh.incrementarMeses(1, matriculaAcademia.getServicoPessoa().getEmissao());
                    //matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.converteDataParaReferencia(novaData));
                }
            }

        }
    }

    /**
     * Desconto Serviço Empresa
     */
    public void dse() {
        if (idParceiro != null && idParceiro != -1) {
            AcademiaServicoValor asv = (AcademiaServicoValor) new Dao().find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription()));
            DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
            DescontoServicoEmpresa dse = dsed.findByGrupo(2, asv.getServicos().getId(), idParceiro);
            if (dse != null) {
                descontoServicoEmpresa = dse.getDesconto();
            }
        } else {
            descontoServicoEmpresa = new Double(0);
        }
    }

    public void recalcular() {
        desconto = 0;
        pegarIdServico();
        idPeriodoGrade = 0;
        listaPeriodosGrade = new ArrayList();
        getListaPeriodosGrade();
        atualizaValor();
        //calculaValorLiquido();
//        if (matriculaAcademia.getId() == -1) {
//            Dao dao = new Dao();
//            AcademiaServicoValor asv = (AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaPeriodosGrade.get(idPeriodoGrade).getDescription()));
//            if (asv.getPeriodo().getId() == 3) {
//                // ROGÉRIO PEDIU PRA COMENTAR 15/02/2016 PORQUE ESTAMOS TRATANDO A VIGORAÇÃO DE ACORDO COM O CÁLCULO DE TAXA PROPORCIONAL REFERÊNCIA #1226
//                //DataHoje dh = new DataHoje();
//                //String novaData = dh.incrementarMeses(1, matriculaAcademia.getServicoPessoa().getEmissao());
//                //matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.converteDataParaReferencia(novaData));
//            }
//        }
    }

    public void recalcular1() {
        desconto = 0;
        pegarIdServico();
        atualizaValor();
        //calculaValorLiquido();
        idPeriodoGrade = 0;
        listaPeriodosGrade.clear();
        getListaPeriodosGrade();
//        if (matriculaAcademia.getId() == -1) {
//            Dao dao = new Dao();
//            AcademiaServicoValor asv = (AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaPeriodosGrade.get(idPeriodoGrade).getDescription()));
//            if (asv.getPeriodo().getId() == 3) {
//                // ROGÉRIO PEDIU PRA COMENTAR 15/02/2016 PORQUE ESTAMOS TRATANDO A VIGORAÇÃO DE ACORDO COM O CÁLCULO DE TAXA PROPORCIONAL REFERÊNCIA #1226
//                //DataHoje dh = new DataHoje();
//                //String novaData = dh.incrementarMeses(1, matriculaAcademia.getServicoPessoa().getEmissao());
//                //matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.converteDataParaReferencia(novaData));
//            }
//        }
    }

    public void recalcular2() {
        desconto = 0;
        pegarIdServico();
        atualizaValor();
    }

    public void calculaValorLiquido() {
        listaPeriodosGrade.clear();
        valor = Moeda.substituiVirgula(valor);
        valorLiquido = "0";
        valorParcela = "0";
        valorParcelaVencimento = "0";
        if (!valor.isEmpty()) {
            if (desconto > Double.parseDouble(valor)) {
                desconto = 0;
            }
            if (Double.parseDouble(valor) - desconto >= 0) {
                double valor_cheio = new FunctionsDao().valorServicoCheio(aluno.getPessoa().getId(), idServico, DataHoje.dataHoje());
                double valor_desconto = Moeda.subtracao(100, Moeda.converteUS$(Moeda.percentualDoValor(Moeda.converteR$Double(valor_cheio), Moeda.converteR$Double(desconto))));
                valorLiquido = valor;
                valorLiquido = Moeda.converteR$Double(Double.parseDouble(Moeda.substituiVirgula(valorLiquido)) - desconto);

                if (aluno.getPessoa().getId() != -1) {
                    Integer idade = new DataHoje().calcularIdade(aluno.getDtNascimento());
                    List<ServicoValor> lsv = new MatriculaEscolaDao().listServicoValorPorServicoIdade(idServico, idade);
                    valorLiquido = (lsv.isEmpty()) ? valor : Moeda.converteR$Double(Moeda.converteUS$(Moeda.valorDoPercentual(valor, Moeda.converteR$Double(lsv.get(0).getDescontoAteVenc()))));
                }

                double valorDesconto = 0;//Moeda.subtracao(valor_cheio, Moeda.converteUS$(valorLiquido));
                valorDesconto = Moeda.multiplicar(Moeda.divisao(valor_desconto, valor_cheio), 100);
                matriculaAcademia.getServicoPessoa().setNrDesconto(valorDesconto);
            }
        }
        valor = Moeda.converteR$(valor);
        carregaParcelas();
    }

    public void atualizaValor() {
        calculoValor();
        calculoDesconto();
    }

    public void pesquisaFisica(String tipoPesquisa) {
        GenericaSessao.put("pesquisaFisicaTipo", tipoPesquisa);
    }

    public boolean isSocio() {
        return socio;
    }

    public void setSocio(boolean socio) {
        this.socio = socio;
    }

    public boolean isDesabilitaCamposMovimento() {
        return desabilitaCamposMovimento;
    }

    public void setDesabilitaCamposMovimento(boolean desabilitaCamposMovimento) {
        this.desabilitaCamposMovimento = desabilitaCamposMovimento;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public String getDescontoString() {
        return Moeda.converteR$Double(desconto);
    }

    public void setDescontoString(String descontoString) {
        this.desconto = Moeda.converteUS$(descontoString);
    }

    public void cobrarTaxa() {
        if (taxa == true) {
            this.valorTaxa = Moeda.converteR$Double(vTaxa);
        } else {
            this.valorTaxa = "";
        }
    }

    public Juridica getJuridica() {
//        if (GenericaSessao.exists("juridicaPesquisa")) {
//            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
//            MatriculaEscolaDao med = new MatriculaEscolaDao();
//            if (med.verificaPessoaEnderecoDocumento("juridica", juridica.getPessoa().getId())) {
//                responsavel = juridica.getPessoa();
//                if (responsavel.getId() != -1) {
//                    pessoaComplemento = new PessoaComplemento();
//                    pessoaComplemento = med.pesquisaDataRefPessoaComplemto(responsavel.getId());
//                    if (pessoaComplemento != null) {
//                        this.idDiaVencimentoPessoa = pessoaComplemento.getNrDiaVencimento();
//                    }
//                    matriculaAcademia.getServicoPessoa().setCobranca(juridica.getPessoa());
//                }
//                pegarIdServico();
//                atualizaValor();
//                //calculaValorLiquido();
//            }
//            if (matriculaAcademia.getServicoPessoa().getCobranca().getId() == -1) {
//                pessoaResponsavelMemoria = responsavel;
//            } else {
//                if (responsavel.getId() != matriculaAcademia.getServicoPessoa().getCobranca().getId()) {
//                    pessoaResponsavelMemoria = responsavel;
//                }
//            }
//            juridica = new Juridica();
//            // verificaDebitosResponsavel(matriculaAcademia.getServicoPessoa().getCobranca());
//            if (matriculaAcademia.getServicoPessoa().getPessoa().getId() != -1 || matriculaAcademia.getServicoPessoa().getCobranca().getId() != -1) {
//                if (new FunctionsDao().inadimplente(matriculaAcademia.getServicoPessoa().getCobranca().getId()) || new FunctionsDao().inadimplente(matriculaAcademia.getServicoPessoa().getPessoa().getId())) {
//                    GenericaMensagem.fatal("Atenção", "Aluno em Débito!");
//                    mensagemInadinplente = "Aluno em Débito!";
//                    responsavel = new Pessoa();
//                    matriculaAcademia.getServicoPessoa().setCobranca(responsavel);
//                    disabled = true;
//                    return null;
//                }
//            }
//        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public boolean verificaSeContribuinteInativo() {
        JuridicaDao juridicaDB = new JuridicaDao();
        if (juridicaDB.empresaInativa(matriculaAcademia.getServicoPessoa().getCobranca(), "FECHOU")) {
            messageStatusEmpresa = "Empresa inátiva!";
            return true;
        }
        return false;
    }

    public String getMensagemStatusEmpresa() {
        return messageStatusEmpresa;
    }

    public void setMensagemStatusEmpresa(String messageStatusEmpresa) {
        this.messageStatusEmpresa = messageStatusEmpresa;
    }

    public String gerarMovimento() {
        if (matriculaAcademia.getId() != -1) {
            if (matriculaAcademia.getEvt() == null) {
                int periodo = matriculaAcademia.getAcademiaServicoValor().getPeriodo().getId();
                int numeroParcelas = matriculaAcademia.getNumeroParcelas();
                if (numeroParcelas == 0) {
                    numeroParcelas = 1;
                }

                if (periodo == 3) {
                    if (matriculaAcademiaAntiga.getId() != -1) {
                        trocarMatricula();
                        if (Moeda.converteUS$(valorLiquido) > Moeda.converteUS$(valorLiquidoAntigo)) {
                            Double valor_taxa = Moeda.subtracao(Moeda.converteUS$(valorLiquido), Moeda.converteUS$(valorLiquidoAntigo));

                            // PARA GERAR PROPORCIONAL
                            if (valor_taxa > 0) {
                                if (matriculaAcademia.getServicoPessoa().getServicos().isCobrarProporcionalidadeAcademia()) {
                                    if (!gerarTaxaMovimento(valor_taxa, true, false)) {
                                        GenericaMensagem.warn("ATENÇÃO", "Movimento não foi gerado, Tente novamente!");
                                        return null;
                                    }
                                }
                            }
                        }
                    } else if (Moeda.converteUS$(valorLiquido) > 0) {
                        // TAXA PROPORCIONAL ATÉ O VENCIMENTO
                        // METODO NOVO PARA O CHAMADO 1226

                        // PARA GERAR PROPORCIONAL
                        if (matriculaAcademia.getServicoPessoa().getServicos().isCobrarProporcionalidadeAcademia()) {
                            if (!gerarTaxaMovimento(Moeda.converteUS$(valorLiquido), true, false)) {
                                GenericaMensagem.warn("ATENÇÃO", "Movimento não foi gerado, Tente novamente!");
                                return null;
                            }
                        }
                    } // new FunctionsDao().gerarMensalidades(matriculaAcademia.getServicoPessoa().getPessoa().getId(), retornaReferenciaGeracao());

                    if (Moeda.converteUS$(valorLiquido) > 0) {
                        // PARA GERAR DO MÊS
                        if (!gerarTaxaMovimento(Moeda.converteUS$(valorLiquido), false, false)) {
                            GenericaMensagem.warn("ATENÇÃO", "Movimento não foi gerado, Tente novamente!");
                            return null;
                        }
                    } // --------------

                    if (Moeda.converteUS$(valorLiquido) > 0) {
                        // PARA GERAR DO PRÓXIMO MÊS
                        if (!gerarTaxaMovimento(Moeda.converteUS$(valorLiquido), false, true)) {
                            GenericaMensagem.warn("ATENÇÃO", "Movimento não foi gerado, Tente novamente!");
                            return null;
                        }
                    } // --------------

                    if (!matriculaAcademia.isTaxa()) {
                        desabilitaCamposMovimento = true;
                        desabilitaDiaVencimento = true;
                        GenericaMensagem.warn("Validação", "Movimento gerado com sucesso!");
                        return null;
                    }
                }
//                if (matriculaAcademia.getServicoPessoa().getPessoa().getId() != pessoaAlunoMemoria.getId()) {
//                    GenericaMensagem.warn("Validação", "Salvar o novo aluno / responsável para gerar movimentos!");
//                    return null;
//                }
//                if (matriculaAcademia.getServicoPessoa().getCobranca().getId() != pessoaResponsavelMemoria.getId()) {
//                    GenericaMensagem.warn("Validação", "Salvar o novo aluno / responsável para gerar movimentos!");
//                    return null;
//                }
                String vencimento;
                String referencia;
                Dao di = new Dao();
                Plano5 plano5;
                // 1 | DIÁRIO       | 1
                // 2 | SEMANAL      | 7
                // 3 | MENSAL       | 30
                // 4 | BIMESTRAL    | 60
                // 5 | TRIMESTRAL   | 90
                // 6 | SEMESTRAL    | 180
                // 7 | ANUAL        | 365
                Servicos servicos;
                int idCondicaoPagto;
                if (numeroParcelas == 1) {
                    idCondicaoPagto = 1;
                } else {
                    idCondicaoPagto = 2;
                }

                plano5 = matriculaAcademia.getServicoPessoa().getServicos().getPlano5();
                servicos = matriculaAcademia.getServicoPessoa().getServicos();
                FTipoDocumento fTipoDocumento = (FTipoDocumento) di.find(new FTipoDocumento(), matriculaAcademia.getServicoPessoa().getTipoDocumento().getId());
                setLote(
                        new Lote(
                                -1,
                                (Rotina) di.find(new Rotina(), 122),
                                "R",
                                DataHoje.data(),
                                matriculaAcademia.getServicoPessoa().getCobranca(),
                                matriculaAcademia.getServicoPessoa().getServicos().getPlano5(),
                                false,
                                "",
                                0,
                                null,
                                null,
                                null,
                                "",
                                fTipoDocumento,
                                (CondicaoPagamento) di.find(new CondicaoPagamento(), idCondicaoPagto),
                                (FStatus) di.find(new FStatus(), 1),
                                null,
                                matriculaAcademia.getServicoPessoa().isDescontoFolha(), 0,
                                null,
                                null,
                                null,
                                false,
                                "",
                                null,
                                ""
                        )
                );
                di.openTransaction();
                try {

                    String nrCtrBoletoResp = "";

                    for (int x = 0; x < (Integer.toString(matriculaAcademia.getServicoPessoa().getCobranca().getId())).length(); x++) {
                        nrCtrBoletoResp += 0;
                    }

                    nrCtrBoletoResp += matriculaAcademia.getServicoPessoa().getCobranca().getId();

                    String mes = matriculaAcademia.getServicoPessoa().getEmissao().substring(3, 5);
                    String ano = matriculaAcademia.getServicoPessoa().getEmissao().substring(6, 10);
                    referencia = mes + "/" + ano;

                    //if (DataHoje.qtdeDiasDoMes(Integer.parseInt(mes), Integer.parseInt(ano)) >= matriculaAcademia.getServicoPessoa().getNrDiaVencimento()) {
                    if (DataHoje.qtdeDiasDoMes(Integer.parseInt(mes), Integer.parseInt(ano)) >= idDiaParcela) {
                        if (idDiaParcela < 10) {
                            vencimento = "0" + idDiaParcela + "/" + mes + "/" + ano;
                        } else {
                            vencimento = idDiaParcela + "/" + mes + "/" + ano;
                        }
                    } else {
                        String diaSwap = Integer.toString(DataHoje.qtdeDiasDoMes(Integer.parseInt(mes), Integer.parseInt(ano)));
                        if (diaSwap.length() < 2) {
                            diaSwap = "0" + diaSwap;
                        }
                        vencimento = diaSwap + "/" + mes + "/" + ano;
                    }

                    boolean insereTaxa = false;
                    if (isTaxa()) {
                        insereTaxa = true;
                    }
                    boolean cobrarTaxaCartao = false;
                    if (taxaCartao) {
                        cobrarTaxaCartao = true;
                    }
                    Evt evt = new Evt();
                    if (!di.save(evt)) {
                        di.rollback();
                        GenericaMensagem.warn("Sistema", "Não foi possível gerar esse movimento!");
                        return null;
                    }
                    lote.setFilial((Filial) di.find(new Filial(), 1));
                    lote.setEvt(evt);
                    matriculaAcademia.setEvt(evt);
                    if (di.save(lote)) {
                        int loop;
                        if (insereTaxa) {
                            loop = numeroParcelas + 1;
                        } else {
                            loop = numeroParcelas;
                        }
                        if (cobrarTaxaCartao) {
                            loop = loop + 1;
                        }
                        String vecimentoString = "";
                        Pessoa pessoaAluno = matriculaAcademia.getServicoPessoa().getPessoa();
                        Pessoa pessoaResponsavelTitular = matriculaAcademia.getServicoPessoa().getCobranca();
                        Pessoa pessoaResponsavel = matriculaAcademia.getServicoPessoa().getCobranca();
                        if (pessoaResponsavel.getId() == -1) {
                            di.rollback();
                            return null;
                        }
                        int b = 0;
                        for (int i = 0; i < loop; i++) {
                            double valorParcelaF;
                            double valorDescontoAteVencimento;
                            TipoServico tipoServico;
                            if (insereTaxa) {
                                tipoServico = (TipoServico) di.find(new TipoServico(), 5);
                                valorParcelaF = vTaxa;
                                valorDescontoAteVencimento = 0;
                                vecimentoString = vencimento;
                                vencimento = DataHoje.data();
                                insereTaxa = false;
                            } else if (cobrarTaxaCartao) {
                                tipoServico = (TipoServico) di.find(new TipoServico(), 5);
                                valorParcelaF = valorCartao;
                                valorDescontoAteVencimento = 0;
                                vecimentoString = vencimento;
                                vencimento = DataHoje.data();
                                cobrarTaxaCartao = false;
                            } else {
                                tipoServico = (TipoServico) di.find(new TipoServico(), 1);
                                valorDescontoAteVencimento = 0;
                                valorParcelaF = Moeda.substituiVirgulaDouble(valorLiquido);
                                if (!vecimentoString.equals("")) {
                                    vencimento = vecimentoString;
                                    vecimentoString = "";
                                }
                                mes = vencimento.substring(3, 5);
                                ano = vencimento.substring(6, 10);
                                referencia = mes + "/" + ano;
                                switch (periodo) {
                                    case 1:
                                        vencimento = DataHoje.data();
                                        break;
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7:
                                        valorParcelaF = valorParcelaF / matriculaAcademia.getNumeroParcelas();
                                        if (b > 0) {
                                            vencimento = (new DataHoje()).incrementarMeses(1, vencimento);
                                        }
                                        break;
                                }
                                b++;
                            }
                            String nrCtrBoleto = nrCtrBoletoResp + Long.toString(DataHoje.calculoDosDias(DataHoje.converte("07/10/1997"), DataHoje.converte(vencimento)));
                            setMovimento(new Movimento(
                                    -1,
                                    lote,
                                    plano5,
                                    pessoaResponsavel, // EMPRESA DO RESPONSÁVEL (SE DESCONTO FOLHA) OU RESPONSÁVEL (SE NÃO FOR DESCONTO FOLHA)
                                    matriculaAcademia.getServicoPessoa().getServicos(),
                                    null,
                                    tipoServico,
                                    null,
                                    valorParcelaF,
                                    referencia,
                                    vencimento,
                                    1,
                                    true,
                                    "E",
                                    false,
                                    pessoaResponsavelTitular, // TITULAR / RESPONSÁVEL
                                    pessoaAluno, // BENEFICIÁRIO
                                    "",
                                    nrCtrBoleto,
                                    vencimento,
                                    valorDescontoAteVencimento,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    fTipoDocumento,
                                    0,
                                    new MatriculaSocios()));
                            // ATUALIZAR VARIÁVEL MATRICULA SÓCIO ( SENÃO TENTA GRAVAR NO BANCO -1 CANSANDO ERRO )
                            movimento.getMatriculaSocios();
                            if (!di.save(movimento)) {
                                di.rollback();
                                GenericaMensagem.warn("Sistema", "Não foi possível gerar esse movimento!");
                                return null;
                            }
                            if (matriculaAcademia.getAcademiaServicoValor().getPeriodo().getId() == 3) {
                                break;
                            }
                        }
                        if (!di.update(matriculaAcademia)) {
                            di.rollback();
                            GenericaMensagem.warn("Sistema", "Não foi possível gerar esse movimento!");
                            return null;
                        }
                        if (!di.update(matriculaAcademia.getServicoPessoa())) {
                            di.rollback();
                            GenericaMensagem.warn("Sistema", "Não foi possível gerar esse movimento!");
                            return null;
                        }
                        di.commit();
                        GenericaMensagem.info("Sucesso", "Movimentos gerados com sucesso");
                        desabilitaCamposMovimento = true;
                        desabilitaDiaVencimento = true;
                        return baixaGeral(false);
                    } else {
                        di.rollback();
                        GenericaMensagem.warn("Sistema", "Não foi possível gerar esse movimento!");
                    }
                } catch (NumberFormatException e) {
                    di.rollback();
                }
            } else {
                GenericaMensagem.warn("Sistema", "Esse movimento já foi gerado!");
            }
        } else {
            GenericaMensagem.warn("Sistema", "Pesquisar aluno!");
        }
        return null;
    }

    public Boolean gerarTaxaMovimento(Double valor_calculo, Boolean proporcional, Boolean geraProximoMes) {

        DescontoPromocional descontoPromocional = new DescontoPromocionalDao().findByServicoMatricula(matriculaAcademia.getServicoPessoa().getServicos().getId());
        if (descontoPromocional != null && descontoPromocional.getDesconto() > 0) {
            valor_calculo = Moeda.converteUS$(Moeda.valorDoPercentual(Moeda.converteR$Double(valor_calculo), Moeda.converteR$Double(descontoPromocional.getDesconto())));

            GenericaMensagem.error("IMPORTANTE", "Desconto Promocional Na Matrícula ao gerar o(s) movimento(s)");

        }

        String mes = DataHoje.data().substring(3, 5),
                ano = DataHoje.data().substring(6, 10),
                referencia = mes + "/" + ano;

        String vencimento;

        String proximo_vencimento = (idDiaParcela < 10) ? "0" + idDiaParcela + "/" + mes + "/" + ano : idDiaParcela + "/" + mes + "/" + ano;
        Double valor_x;
        DataHoje dh = new DataHoje();
        String data_hoje = DataHoje.data();
        Integer dia_hoje = Integer.valueOf(data_hoje.substring(0, 2));

        if (geraProximoMes) {
            if (dia_hoje != idDiaParcela) {
                return true;
            }
        }

        Dao dao = new Dao();
        TipoServico tipoServico;

        if (proporcional) {
            vencimento = DataHoje.data();
            // ADICIONADO PARA NÃO GERAR UMA TAXA CASO O DATA DE VENCIMENTO FOR A MESMA QUE DATA ATUAL (HOJE)
            if (data_hoje.equals(proximo_vencimento)) {
                return true;
            }

            if (dia_hoje < idDiaParcela) {
                Integer qnt_dias = Integer.valueOf(Long.toString(DataHoje.calculoDosDias(DataHoje.converte(data_hoje), DataHoje.converte(proximo_vencimento))));
                valor_x = Moeda.multiplicar(Moeda.divisao(valor_calculo, 30), qnt_dias);
            } else if (dia_hoje == idDiaParcela) {
                valor_x = valor_calculo;
            } else {
                proximo_vencimento = dh.incrementarMeses(1, proximo_vencimento);
                Integer qnt_dias = Integer.valueOf(Long.toString(DataHoje.calculoDosDias(DataHoje.converte(data_hoje), DataHoje.converte(proximo_vencimento))));

                valor_x = Moeda.multiplicar(Moeda.divisao(valor_calculo, 30), qnt_dias);
            }
            tipoServico = (TipoServico) dao.find(new TipoServico(), 5);
        } else {
            if (dia_hoje < idDiaParcela) {
                vencimento = proximo_vencimento;
            } else if (dia_hoje == idDiaParcela) {
                // CHAMADO #2226 LINHA COMENTADA PARA NOVA
                if (!geraProximoMes) {
                    vencimento = DataHoje.data();
                } else {
                    proximo_vencimento = dh.incrementarMeses(1, proximo_vencimento);
                    vencimento = proximo_vencimento;
                }
            } else {
                proximo_vencimento = dh.incrementarMeses(1, proximo_vencimento);
                vencimento = proximo_vencimento;
            }

            valor_x = valor_calculo;
            tipoServico = (TipoServico) dao.find(new TipoServico(), 1);
        }

        dao.openTransaction();
        FTipoDocumento fTipoDocumento = (FTipoDocumento) dao.find(new FTipoDocumento(), matriculaAcademia.getServicoPessoa().getTipoDocumento().getId());
        Lote lote_taxa
                = new Lote(
                        -1,
                        (Rotina) dao.find(new Rotina(), 122),
                        "R",
                        DataHoje.data(),
                        matriculaAcademia.getServicoPessoa().getCobranca(),
                        matriculaAcademia.getServicoPessoa().getServicos().getPlano5(),
                        false,
                        "",
                        0,
                        (Filial) dao.find(new Filial(), 1),
                        null,
                        null,
                        "",
                        fTipoDocumento,
                        (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1),
                        (FStatus) dao.find(new FStatus(), 1),
                        null,
                        matriculaAcademia.getServicoPessoa().isDescontoFolha(),
                        0,
                        null,
                        null,
                        null,
                        false,
                        "",
                        null,
                        ""
                );

        if (!dao.save(lote_taxa)) {
            dao.rollback();
            GenericaMensagem.warn("Sistema", "Não foi possível salvar Lote de Taxa!");
            return false;
        }

        Movimento m
                = new Movimento(
                        -1,
                        lote_taxa,
                        matriculaAcademia.getServicoPessoa().getServicos().getPlano5(),
                        matriculaAcademia.getServicoPessoa().getCobranca(),
                        matriculaAcademia.getServicoPessoa().getServicos(),
                        null,
                        tipoServico,
                        null,
                        valor_x,
                        referencia,
                        vencimento,
                        1,
                        true,
                        "E",
                        false,
                        matriculaAcademia.getServicoPessoa().getCobranca(), // TITULAR / RESPONSÁVEL
                        matriculaAcademia.getServicoPessoa().getPessoa(), // BENEFICIÁRIO
                        "",
                        "",
                        vencimento,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        fTipoDocumento,
                        0,
                        new MatriculaSocios()
                );

        // ATUALIZAR VARIÁVEL MATRICULA SÓCIO ( SENÃO TENTA GRAVAR NO BANCO -1 CANSANDO ERRO )
        m.getMatriculaSocios();
        if (!dao.save(m)) {
            dao.rollback();
            GenericaMensagem.warn("Sistema", "Não foi possível salvar Movimento de Taxa gerar esse movimento!");
            return false;
        }

        dao.commit();
        NovoLog logs = new NovoLog();
        logs.setTabela("matr_academia");
        logs.setCodigo(matriculaAcademia.getId());
        logs.save(
                "** GERAÇÃO DE TAXA MATRÍCULA **\n "
                + "Matrícula ID: " + matriculaAcademia.getId() + " \n "
                + "Movimento ID: " + m.getId() + " \n "
                + " - Valor: " + m.getValorString() + " \n "
                + " - Vencimento: " + m.getVencimento()
        );
        return true;
    }

    public String retornaReferenciaGeracao() {
        String referencia_geracao = matriculaAcademia.getServicoPessoa().getReferenciaVigoracao();

        DataHoje dh = new DataHoje();
        String data_hoje = DataHoje.data();
        Integer dia_hoje = Integer.valueOf(data_hoje.substring(0, 2));

        if (dia_hoje >= idDiaParcela) {
            referencia_geracao = DataHoje.converteDataParaReferencia(dh.incrementarMeses(1, "01/" + referencia_geracao));
        }
        return referencia_geracao;
    }

    public String baixaGeral(boolean mensal) {
        Dao dao = new Dao();
        List<Movimento> listaMovimentoAuxiliar = new ArrayList();
        if (mensal) {

        } else {
            if (matriculaAcademia.isTaxa()) {
                for (int i = 0; i < getListaMovimentos().size(); i++) {
                    HistoricoEmissaoGuias heg = new HistoricoEmissaoGuias();
                    Movimento m = (Movimento) dao.find(new Movimento(), listaMovimentos.get(i).getId());
                    double descontox = listaMovimentos.get(i).getDesconto();
                    double valorx = Moeda.converteUS$(listaMovimentos.get(i).getValorString());
                    m.setMulta(listaMovimentos.get(i).getMulta());
                    m.setJuros(listaMovimentos.get(i).getJuros());
                    m.setDesconto(descontox);
                    m.setValor(listaMovimentos.get(i).getValor());
                    m.setValorBaixa(valorx);
                    listaMovimentoAuxiliar.add(m);
                    heg.setMovimento(m);
                    heg.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));
                    if (i == 1) {
                        break;
                    }
                }
            } else {
                for (int i = 0; i < getListaMovimentos().size(); i++) {
                    HistoricoEmissaoGuias heg = new HistoricoEmissaoGuias();
                    Movimento m = (Movimento) dao.find(new Movimento(), listaMovimentos.get(i).getId());
                    double descontox = listaMovimentos.get(i).getDesconto();
                    double valorx = Moeda.converteUS$(listaMovimentos.get(i).getValorString());
                    m.setMulta(listaMovimentos.get(i).getMulta());
                    m.setJuros(listaMovimentos.get(i).getJuros());
                    m.setDesconto(descontox);
                    m.setValor(listaMovimentos.get(i).getValor());
                    m.setValorBaixa(valorx);
                    listaMovimentoAuxiliar.add(m);
                    heg.setMovimento(m);
                    heg.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));
                    break;
                }
            }
            if (!listaMovimentoAuxiliar.isEmpty()) {
                GenericaSessao.put("listaMovimento", listaMovimentoAuxiliar);
                GenericaSessao.put("tipo_recibo_imprimir", dao.find(new TipoRecibo(), 1));
                return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
            }
        }
        return null;
    }

    public void desfazerMovimento() {
        if (matriculaAcademia.getId() != -1) {
            if (matriculaAcademia.getEvt() != null) {
                if (existeMovimento()) {
                    GenericaMensagem.warn("Validação", "Movimento já possui baixa, não pode ser cancelado!");
                    return;
                }
                AcademiaDao academiaDao = new AcademiaDao();
                if (academiaDao.desfazerMovimento(matriculaAcademia)) {
                    listaMovimentos.clear();
                    desabilitaCamposMovimento = false;
                    bloqueiaComboDiaVencimento();
                    GenericaMensagem.info("Sucesso", "Transação desfeita com sucesso");
                } else {
                    GenericaMensagem.warn("Falha", "ao desfazer essa transação!");
                }
                Dao di = new Dao();
                matriculaAcademia = (MatriculaAcademia) di.find(matriculaAcademia);
            }
        }
    }

    public void renovarMatricula() {
        if (matriculaAcademia.getId() != -1) {
            //if (matriculaAcademia.getEvt() != null) {
            AcademiaDao academiaDao = new AcademiaDao();
            List<Movimento> list_movimento = academiaDao.listaRefazerMovimento(matriculaAcademia);

            if (!list_movimento.isEmpty()) {
                Dao dao = new Dao();

                dao.openTransaction();

                if (!GerarMovimento.inativarArrayMovimento(list_movimento, "RENOVAÇÃO CADASTRAL DE MATRÍCULA ACADEMIA", dao).isEmpty()) {
                    GenericaMensagem.warn("ATENÇÃO", "Não foi possível renovar cadastro, tente novamente!");
                    return;
                }

                //int idEvt = matriculaAcademia.getEvt().getId();
                matriculaAcademia.getServicoPessoa().setReferenciaVigoracao(DataHoje.converteDataParaReferencia(DataHoje.data()));
                if (!dao.update(matriculaAcademia.getServicoPessoa())) {
                    dao.rollback();
                    return;
                }

                if (!dao.update(matriculaAcademia)) {
                    dao.rollback();
                    return;
                }

                dao.commit();

                matriculaAcademia = (MatriculaAcademia) dao.find(matriculaAcademia);

                gerarMovimento();
            } else {
                GenericaMensagem.warn("ATENÇÃO", "Não tem movimentos para serem renovados!");
            }
            //}
        }
    }

    public void trocarMatricula() {

        AcademiaDao academiaDao = new AcademiaDao();
        List<Movimento> list_movimento = academiaDao.listaRefazerMovimento(matriculaAcademiaAntiga);

        Dao dao = new Dao();
        dao.openTransaction();

        if (!list_movimento.isEmpty()) {
            if (!GerarMovimento.inativarArrayMovimento(list_movimento, "TROCA DE MATRÍCULA ACADEMIA", dao).isEmpty()) {
                GenericaMensagem.warn("ATENÇÃO", "Não foi possível trocar cadastro, tente novamente!");
                return;
            }

        }

        inativaMatriculaTrocada(dao);
        dao.commit();
        matriculaAcademiaAntiga = new MatriculaAcademia();

    }

    public void gerarCarne() throws Exception {
        if (matriculaAcademia.getEvt() != null) {
            if (listaMovimentos.size() > 0) {
                List<CarneEscola> list = new ArrayList<>();
                if (!list.isEmpty()) {
                    Jasper.PATH = "downloads";
                    Jasper.PART_NAME = "academia";
                    Jasper.printReports("/Relatorios/CARNE.jasper", "carne", list);
                }
            }
        }
    }

    public boolean isDesabilitaGeracaoContrato() {
        return desabilitaGeracaoContrato;
    }

    public void setDesabilitaGeracaoContrato(boolean desabilitaGeracaoContrato) {
        this.desabilitaGeracaoContrato = desabilitaGeracaoContrato;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public List<Movimento> getListaMovimentos() {
        if (listaMovimentos.isEmpty()) {
            if (matriculaAcademia.getId() != -1) {
                int count = 0;
                if (matriculaAcademia.getEvt() != null) {
                    MovimentoDao dao = new MovimentoDao();
                    LoteDao loteDB = new LoteDao();
                    lote = (Lote) loteDB.pesquisaLotePorEvt(matriculaAcademia.getEvt());
                    listaMovimentos = dao.listaMovimentosDoLote(lote.getId());
                    for (int i = 0; i < listaMovimentos.size(); i++) {
                        if (listaMovimentos.get(i).getTipoServico().getId() == 5) {
                            setTaxa(true);
                            valorTaxa = Moeda.converteR$Double(listaMovimentos.get(i).getValor());
                            listaMovimentos.get(i).setQuantidade(0);
                        } else {
                            count++;
                            listaMovimentos.get(i).setQuantidade(count);
                        }
                    }
                    lote = new Lote();
                }
            }
        }
        return listaMovimentos;
    }

    public void setListaMovimentos(List<Movimento> listaMovimentos) {
        this.listaMovimentos = listaMovimentos;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public boolean existeMovimento() {
        if (matriculaAcademia.getEvt() != null) {
            MovimentoDao dao = new MovimentoDao();
            if (!((List) dao.movimentosBaixadosPorEvt(matriculaAcademia.getEvt().getId())).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void bloqueiaComboDiaVencimento() {
        if (idFTipoDocumento == 2) {
            desabilitaDiaVencimento = true;
        } else if (idFTipoDocumento == 13) {
            desabilitaDiaVencimento = false;
        }
    }

    public boolean isDesabilitaDiaVencimento() {
        return desabilitaDiaVencimento;
    }

    public void setDesabilitaDiaVencimento(boolean desabilitaDiaVencimento) {
        this.desabilitaDiaVencimento = desabilitaDiaVencimento;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public String periodoSemanaString(MatriculaAcademia academia) {
        String periodoSemana = "";
        AcademiaDao academiaDao = new AcademiaDao();
        List<AcademiaServicoValor> list = academiaDao.listaAcademiaServicoValorPorServico(academia.getServicoPessoa().getServicos().getId());
        List<AcademiaSemana> listSemana = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            listSemana.clear();
//            listSemana = academiaDao.listaAcademiaSemana(list.get(i).getAcademiaGrade().getId());
            for (int j = 0; j < listSemana.size(); j++) {
                if (j == 0) {
                    periodoSemana += listSemana.get(j).getSemana().getDescricao();
                } else {
                    periodoSemana += " - " + listSemana.get(j).getSemana().getDescricao();
                }
            }
        }
        return periodoSemana;
    }

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
        listaAcademia.clear();
        loadList();
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
        listaAcademia.clear();
        loadList();
    }

    public void loadList() {
        int id = 0;
        if (idModalidadePesquisa != null) {
            try {
                //id = Integer.parseInt(idModalidadePesquisa.toString());
                Dao di = new Dao();
                id = ((AcademiaServicoValor) (di.find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(Integer.parseInt(idModalidadePesquisa.toString())).getDescription())))).getServicos().getId();
            } catch (NumberFormatException e) {
                id = 0;
            }
        }
        if (idModalidadePesquisa != null || !descricaoPesquisa.isEmpty() || !periodoEmissaoString.isEmpty()) {
            AcademiaDao academiaDao = new AcademiaDao();
            listaAcademia = academiaDao.pesquisaMatriculaAcademia("", porPesquisa, comoPesquisa, descricaoPesquisa, matriculaAtiva, id, periodoEmissaoString, "", "");
        }
    }

    public String getMascaraPesquisa() {
        return Mask.getMascaraPesquisa(porPesquisa, true);
    }

    public boolean isOcultaParcelas() {
        return ocultaParcelas;
    }

    public void setOcultaParcelas(boolean ocultaParcelas) {
        this.ocultaParcelas = ocultaParcelas;
    }

    public Pessoa getCobranca() {
        return cobranca;
    }

    public void setCobranca(Pessoa cobranca) {
        this.cobranca = cobranca;
    }

    public String semanaResumo(String descricao) {
        descricao = descricao.substring(0, 3);
        return descricao;
    }

    public Registro getRegistro() {
        if (registro != null) {
            Dao di = new Dao();
            registro = (Registro) di.find(new Registro(), 1);
            if (registro.getServicos() != null) {
                ServicoValorDao servicoValorDB = new ServicoValorDao();
                List<ServicoValor> list = (List<ServicoValor>) servicoValorDB.pesquisaServicoValor(registro.getServicos().getId());
                if (!list.isEmpty()) {
                    valorCartao = list.get(0).getValor();
                }
                ocultaBotaoTarifaCartao = false;
            } else {
                ocultaBotaoTarifaCartao = true;
            }
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public boolean isOcultaBotaoTarifaCartao() {
        return ocultaBotaoTarifaCartao;
    }

    public void setOcultaBotaoTarifaCartao(boolean ocultaBotaoTarifaCartao) {
        this.ocultaBotaoTarifaCartao = ocultaBotaoTarifaCartao;
    }

    public double getValorCartao() {
        return valorCartao;
    }

    public void setValorCartao(double valorCartao) {
        this.valorCartao = valorCartao;
    }

    public boolean isTaxaCartao() {
        return taxaCartao;
    }

    public void setTaxaCartao(boolean taxaCartao) {
        this.taxaCartao = taxaCartao;
    }

//    public boolean isAlunoFoto() {
//        if (matriculaAcademia.getServicoPessoa().getPessoa().getId() != -1) {
//            File file = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + matriculaAcademia.getServicoPessoa().getPessoa().getId() + ".png"));
//            alunoFoto = file.exists();
//        }
//        return alunoFoto;
//    }
//
//    public void setAlunoFoto(boolean alunoFoto) {
//        this.alunoFoto = alunoFoto;
//    }
    public int getIdDiaParcela() {
        return idDiaParcela;
    }

    public void setIdDiaParcela(int idDiaParcela) {
        this.idDiaParcela = idDiaParcela;
    }

    public List<SelectItem> getListaDiaParcela() {
        return listaDiaParcela;
    }

    public String getLoad() {
        return "";
    }

    public boolean isMatriculaAtiva() {
        return matriculaAtiva;
    }

    public void setMatriculaAtiva(boolean matriculaAtiva) {
        this.matriculaAtiva = matriculaAtiva;
    }

    public Object getIdModalidadePesquisa() {
        return idModalidadePesquisa;
    }

    public void setIdModalidadePesquisa(Object idModalidadePesquisa) {
        this.idModalidadePesquisa = idModalidadePesquisa;
    }

    public String getDataValidade() {
        if (matriculaAcademia.getServicoPessoa().getDtEmissao() != null) {
            if (listaPeriodosGrade.isEmpty()) {
                return "";
            }
            Dao dao = new Dao();
            Periodo periodo = ((AcademiaServicoValor) dao.find(new AcademiaServicoValor(), Integer.parseInt(listaPeriodosGrade.get(idPeriodoGrade).getDescription()))).getPeriodo();
            DataHoje dh = new DataHoje();
            switch (periodo.getId()) {
                case 1:
                    dataValidade = matriculaAcademia.getServicoPessoa().getEmissao();
                    break;
                case 3:
                    dataValidade = "";
                    break;
                default:
                    dataValidade = dh.incrementarDias(periodo.getDias(), matriculaAcademia.getServicoPessoa().getEmissao());
                    break;
            }
        }
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Socios getSocios() {
//        if (aluno.getId() != -1) {
//            SociosDB sociosDB = new SociosDao();
//            if (socios.getId() == -1) {
//                socios = sociosDB.pesquisaSocioPorPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId());
//            }
//        }
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public boolean isOcultaMensal() {
        if (matriculaAcademia.getAcademiaServicoValor().getPeriodo().getId() == 2) {

        }
        return true;
    }

    public Socios getSociosCobranca() {
        if (cobranca != null) {
            SociosDao sociosDB = new SociosDao();
            sociosCobranca = sociosDB.pesquisaSocioPorPessoa(matriculaAcademia.getServicoPessoa().getCobranca().getId());
        }
        return sociosCobranca;
    }

    public void setSociosCobranca(Socios sociosCobranca) {
        this.sociosCobranca = sociosCobranca;
    }

    public String getMensagemInadinplente() {
        return mensagemInadinplente;
    }

    public void setMensagemInadinplente(String mensagemInadinplente) {
        this.mensagemInadinplente = mensagemInadinplente;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public List<SelectItem> getListFiliais() {
        if (listFiliais.isEmpty()) {
            Filial f = MacFilial.getAcessoFilial().getFilial();
            if (f.getId() != -1) {
                if (liberaAcessaFilial || Usuario.getUsuario().getId() == 1) {
                    liberaAcessaFilial = true;
                    // ROTINA MATRÍCULA ESCOLA
                    List<FilialRotina> list = new FilialRotinaDao().findByRotina(new Rotina().get().getId());
                    // ID DA FILIAL
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (i == 0) {
                                filial_id = i;
                            }
                            if (Objects.equals(f.getId(), list.get(i).getId())) {
                                filial_id = i;
                            }
                            listFiliais.add(new SelectItem(i, list.get(i).getFilial().getFilial().getPessoa().getNome(), "" + list.get(i).getFilial().getId()));
                        }
                    } else {
                        filial_id = 0;
                        listFiliais.add(new SelectItem(0, f.getFilial().getPessoa().getNome(), "" + f.getId()));
                    }
                } else {
                    filial_id = 0;
                    listFiliais.add(new SelectItem(0, f.getFilial().getPessoa().getNome(), "" + f.getId()));
                }
            }
        }
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public Integer getFilial_id() {
        return filial_id;
    }

    public void setFilial_id(Integer filial_id) {
        this.filial_id = filial_id;
    }

    public Integer getFilial_id_2() {
        return filial_id_2;
    }

    public void setFilial_id_2(Integer filial_id_2) {
        this.filial_id_2 = filial_id_2;
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public StreamedContent getFotoStreamed() {
//        try {
//            if (PhotoCapture.getFile() != null) {
//                nomeFoto = PhotoCapture.getImageName();
//                PhotoCapture.unload();
//            }
//            
//            if (PhotoUpload.getFile() != null) {
//                nomeFoto = PhotoUpload.getImageName();
//                PhotoUpload.unload();
//            }
//
//            fotoStreamed = null;
//            File fotoTempx = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/temp/foto/" + Usuario.getUsuario().getId() + "/" + nomeFoto + ".png"));
//            if (fotoTempx.exists()) {
//                fotoStreamed = ImageConverter.getImageStreamed(fotoTempx, "image/png");
//            } else {
////                FisicaDB db = new FisicaDao();
////                Fisica f = db.pesquisaFisicaPorPessoa(matriculaAcademia.getServicoPessoa().getPessoa().getId());
//                
//                File fotoSave = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/Fisica/" + aluno.getFoto() + ".png"));
//                if (fotoSave.exists()) {
//                    fotoStreamed = ImageConverter.getImageStreamed(fotoSave, "image/png");
//                }
//                
//            }
//        } catch (Exception e) {
//            e.getMessage();
//        }
        return fotoStreamed;
    }

    public void setFotoStreamed(StreamedContent fotoStreamed) {
        this.fotoStreamed = fotoStreamed;
    }

    public String getNomeFoto() {
        return nomeFoto;
    }

    public void setNomeFoto(String nomeFoto) {
        this.nomeFoto = nomeFoto;
    }

    public MatriculaAcademia getMatriculaAcademiaAntiga() {
        return matriculaAcademiaAntiga;
    }

    public void setMatriculaAcademiaAntiga(MatriculaAcademia matriculaAcademiaAntiga) {
        this.matriculaAcademiaAntiga = matriculaAcademiaAntiga;
    }

    public String getValorLiquidoAntigo() {
        return valorLiquidoAntigo;
    }

    public void setValorLiquidoAntigo(String valorLiquidoAntigo) {
        this.valorLiquidoAntigo = valorLiquidoAntigo;
    }

    public List<SelectItem> getListParceiro() {
        return listParceiro;
    }

    public void setListParceiro(List<SelectItem> listParceiro) {
        this.listParceiro = listParceiro;
    }

    public Integer getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(Integer idParceiro) {
        this.idParceiro = idParceiro;
    }

    private void loadListParceiro() {
        if (socios.getId() == -1 && aluno.getId() != -1) {
            listParceiro = new ArrayList();
            DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
            // List<DescontoServicoEmpresa> list = dsed.findByGrupo(2);
            AcademiaServicoValor asv = (AcademiaServicoValor) new Dao().find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription()));
            List<DescontoServicoEmpresa> list = dsed.findByGrupo(asv.getServicos().getId());
            listParceiro.add(new SelectItem(-1, "NENHUM"));
            idParceiro = -1;
            Integer pessoa_id = null;
            descontoServicoEmpresa = new Double(0);
            descontoServicoEmpresaString = "0";
            for (int i = 0; i < list.size(); i++) {
                if (pessoa_id == null || pessoa_id != list.get(i).getJuridica().getPessoa().getId()) {
                    listParceiro.add(new SelectItem(list.get(i).getJuridica().getPessoa().getId(), list.get(i).getJuridica().getPessoa().getNome()));
                    pessoa_id = list.get(i).getJuridica().getPessoa().getId();
                }
            }
        }
    }

    public Boolean getTrocarMatriculaAcademia() {
        return trocarMatriculaAcademia;
    }

    public void setTrocarMatriculaAcademia(Boolean trocarMatriculaAcademia) {
        this.trocarMatriculaAcademia = trocarMatriculaAcademia;
    }

    public String getPeriodoString() {
        return periodoString;
    }

    public void setPeriodoString(String periodoString) {
        this.periodoString = periodoString;
    }

    public String getPeriodoEmissaoString() {
        return periodoEmissaoString;
    }

    public void setPeriodoEmissaoString(String periodoEmissaoString) {
        this.periodoEmissaoString = periodoEmissaoString;
    }

//    public Integer getQuantidadeVagas() {
//        try {
//            AcademiaServicoValor asv = (AcademiaServicoValor) new Dao().find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription()));
//            if (asv != null) {
//                if (asv.getServicos().getNrVagas() > 0) {
//                    return asv.getServicos().getNrVagas();
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        return null;
//    }
//
//    public Integer getQuantidadeDisponiveis() {
//        try {
//            AcademiaServicoValor asv = (AcademiaServicoValor) new Dao().find(new AcademiaServicoValor(), Integer.parseInt(listaModalidades.get(idModalidade).getDescription()));
//            if (asv != null) {
//                if (asv.getServicos().getNrVagas() > 0) {
//                    return asv.getServicos().getNrVagas() - new FunctionsDao().quantidadeAlunosAcademia(asv.getServicos().getId());
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        return null;
//    }
}
