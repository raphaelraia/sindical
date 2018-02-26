package br.com.rtools.escola.beans;

import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Midia;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.LancamentoIndividualDao;
import br.com.rtools.escola.*;
import br.com.rtools.escola.dao.MatriculaContratoDao;
import br.com.rtools.escola.dao.MatriculaEscolaDao;
import br.com.rtools.escola.dao.TurmaDao;
import br.com.rtools.escola.lista.ListaMatriculaEscola;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.DescontoServicoEmpresa;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.ServicoValor;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.DescontoServicoEmpresaDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.pessoa.dao.SpcDao;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.utilitarios.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class MatriculaEscolaBean implements Serializable {

    private Filial filial;
    private Fisica aluno;
    private Juridica juridica;
    private Juridica empresa;
    private Lote lote;
    private MatriculaEscola matriculaEscola;
    private Turma turma;
    private MatriculaContrato matriculaContrato;
    private MatriculaIndividual matriculaIndividual;
    private MatriculaTurma matriculaTurma;
    private EscolaAutorizados escolaAutorizados;
    private EscolaAutorizados escolaAutorizadosDetalhes;
    private MacFilial macFilial;
    private Movimento movimento;
    private Pessoa responsavel;
    private PessoaComplemento pessoaComplemento;
    private Registro registro;
    private Socios socios;
    private List listaGridMEscola;
    private List<SelectItem> listaStatus;
    private List<FTipoDocumento> listaFTipoDocumento;
    private List<SelectItem> listaCursosDisponiveis;
    private List<SelectItem> listaVendedor;
    private List<SelectItem> listaProfessor;
    private List<SelectItem> listaMidia;
    private List<SelectItem> listaNumeros;
    private List<SelectItem> listaDataVencimento;
    private List<SelectItem> listaIndividual;
    private List<SelectItem> listaMesVencimento;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listParceiro;
    private List<Turma> listaTurma;
    private List<EscolaAutorizados> listaEscolaAutorizadas;
    private List<ListaMatriculaEscola> listaMatriculaEscolas;
    private int idDiaVencimento;
    private int idDataTaxa;
    private int idMesVencimento;
    private int idFTipoDocumento;
    private int idIndividual;
    private int idMidia;
    private int idTurma;
    private int idStatus;
    private int idStatusFiltro;
    private int idVendedor;
    private int idProfessor;
    private int idServico;
    private Integer idCursosDisponiveis;
    private int idadeAluno;
    private int vagasDisponiveis;
    private Integer filial_id;
    private Integer filial_id_2;
    private Integer idParceiro;
    private double vTaxa;
    private boolean alterarPessoaComplemento;
    private boolean desabilitaTurma;
    private boolean desabilitaIndividual;
    private boolean desabilitaCamposMovimento;
    private boolean desabilitaGeracaoContrato;
    private boolean desabilitaDescontoFolha;
    private boolean desabilitaDiaVencimento;
    private boolean desabilitaCampo;
    private boolean habilitaGerarParcelas;
    private boolean limpar;
    private boolean ocultaBotaoSalvar;
    private boolean ocultaDescontoFolha;
    private boolean visibility;
    private Boolean liberaAcessaFilial;
    private String comoPesquisa;
    private String descricao;
    private String descricaoCurso;
    private String msgStatusFilial;
    private String msgStatusDebito;
    private String msgStatusEmpresa;
    private String openModal;
    private String porPesquisa;
    private String target;
    private String tipoMatricula;
    private String valor;
    private String valorParcela;
    private String valorParcelaVencimento;
    private String valorLiquido;
    private String valorTaxa;

    private ServicoPessoa servicoPessoa;
    private int numeroParcelas;
    private String valorTotal;

    private Date dataEntrada;

    private Double descontoServicoEmpresa;
    private String descontoServicoEmpresaString;

    @PostConstruct
    public void init() {
        filial = new Filial();
        aluno = new Fisica();
        juridica = new Juridica();
        empresa = new Juridica();
        lote = new Lote();
        matriculaEscola = new MatriculaEscola();
        turma = new Turma();
        matriculaContrato = new MatriculaContrato();
        matriculaIndividual = new MatriculaIndividual();
        matriculaTurma = new MatriculaTurma();
        escolaAutorizados = new EscolaAutorizados();
        escolaAutorizadosDetalhes = new EscolaAutorizados();
        macFilial = new MacFilial();
        movimento = new Movimento();
        responsavel = new Pessoa();
        pessoaComplemento = new PessoaComplemento();
        registro = new Registro();
        socios = new Socios();
        listaGridMEscola = new ArrayList();
        listaStatus = new ArrayList();
        listaFTipoDocumento = new ArrayList();
        listaCursosDisponiveis = new ArrayList();
        listaVendedor = new ArrayList();
        listaProfessor = new ArrayList();
        listaMidia = new ArrayList();
        listaNumeros = new ArrayList();
        listaDataVencimento = new ArrayList();
        listaIndividual = new ArrayList();
        listaDataVencimento = new ArrayList();
        listaMesVencimento = new ArrayList();
        listaTurma = new ArrayList();
        listaEscolaAutorizadas = new ArrayList();
        listaMatriculaEscolas = new ArrayList();
        listFiliais = new ArrayList();
        listParceiro = new ArrayList();
        idDiaVencimento = 0;
        idParceiro = -1;
        idDataTaxa = 0;
        idMesVencimento = 0;
        idFTipoDocumento = 0;
        idIndividual = 0;
        idMidia = 0;
        idTurma = 0;
        idStatus = 0;
        idStatusFiltro = 0;
        idVendedor = 0;
        idProfessor = 0;
        idServico = 0;
        idCursosDisponiveis = 0;
        idadeAluno = 0;
        vagasDisponiveis = 0;
        vTaxa = 0;
        alterarPessoaComplemento = false;
        desabilitaTurma = false;
        desabilitaIndividual = false;
        desabilitaCamposMovimento = false;
        desabilitaGeracaoContrato = false;
        desabilitaDescontoFolha = true;
        desabilitaDiaVencimento = false;
        desabilitaCampo = false;
        habilitaGerarParcelas = false;
        limpar = false;
        ocultaBotaoSalvar = false;
        ocultaDescontoFolha = true;
        visibility = false;
        comoPesquisa = "";
        descricao = "";
        descricaoCurso = "";
        msgStatusFilial = "";
        msgStatusDebito = "";
        msgStatusEmpresa = "";
        openModal = "";
        porPesquisa = "aluno";
        target = "#";
        tipoMatricula = "Turma";
        valor = "";
        valorParcela = "";
        valorParcelaVencimento = "";
        valorLiquido = "";
        valorTaxa = "0,00";
        servicoPessoa = new ServicoPessoa();
        numeroParcelas = 0;
        valorTotal = "0,00";
        liberaAcessaFilial = false;
        filial_id = 0;
        filial_id_2 = 0;
        loadDiaVencimento();
        loadLiberaAcessaFilial();
        dataEntrada = DataHoje.dataHoje();
        loadListParceiro();
    }

    /* chamado quando outra view for chamada através do UIViewRoot.setViewId(String viewId) */
    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("matriculaEscolaBean");
        GenericaSessao.remove("matriculaEscolaPesquisa");
        GenericaSessao.remove("pesquisaFisicaTipo");
        GenericaSessao.remove("pessoaPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("fisicaPesquisa");
    }
//
//    public void onDateSelect(SelectEvent event) {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//        //matriculaEscola.setDataMatricula(DataHoje.converte(format.format(event.getObject())));
//        servicoPessoa.setDtEmissao(DataHoje.converte(format.format(event.getObject())));
//        listaMesVencimento.clear();
//        listaDiaParcela.clear();
//    }

    public void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public void loadDiaVencimento() {
        getListaDataVencimento();
        getRegistro();
        Integer dia;
        if (pessoaComplemento.getId() == -1) {
            dia = registro.getFinDiaVencimentoCobranca();
        } else {
            dia = pessoaComplemento.getNrDiaVencimento();
        }

        for (int i = 1; i < listaDataVencimento.size(); i++) {
            if (Objects.equals(Integer.valueOf(listaDataVencimento.get(i).getValue().toString()), dia)) {
                idDiaVencimento = dia;
            }
        }
    }

    public String novo() {
        GenericaSessao.remove("matriculaEscolaBean");
        return null;
    }

    public boolean validaDataVigoracaoValidadeBoolean() {
        if (!getServicoIndividual().isCursoRenovacao()) {
            if (!DataHoje.validaReferencias(servicoPessoa.getReferenciaVigoracao(), servicoPessoa.getReferenciaValidade())) {
                GenericaMensagem.warn("Atenção", "Data de INICIAL não pode ser menor que FINAL para as Parcelas!");
                return false;
            }
        }
        return true;
    }

    public void validaDataVigoracaoValidade() {
        validaDataVigoracaoValidadeBoolean();
    }

    public String telaBaixa(String caixa_banco) {
        if (macFilial == null) {
            GenericaMensagem.warn("Erro", "Não existe filial na sessão!");
            return null;
        }

        if (!macFilial.getCaixaOperador()) {
            if (macFilial.getCaixa() == null) {
                GenericaMensagem.warn("Erro", "Configurar Caixa nesta estação de trabalho!");
                return null;
            }
        } else {
            FinanceiroDao dao = new FinanceiroDao();
            Caixa caixax = dao.pesquisaCaixaUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId(), macFilial.getFilial().getId());

            if (caixax == null) {
                GenericaMensagem.warn("Erro", "Configurar Caixa para este Operador!");
                return null;
            }
        }

        List<Movimento> lm = new ArrayList();
        movimento.setValorBaixa(movimento.getValor());
        lm.add(movimento);

        if (!lm.isEmpty()) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lm);
            GenericaSessao.put("caixa_banco", caixa_banco);
            GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
            return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
        } else {
            GenericaMensagem.warn("Atenção", "Lista de movimento vazia, tente novamente!");
            //PF.update("formMovimentosReceber");
        }
        return null;
    }

    public String gerarEntrada() {
        Plano5 plano5;
        Servicos servicos;

        DataHoje dh = new DataHoje();
        if (DataHoje.maiorData(DataHoje.converteData(dataEntrada), dh.incrementarAnos(1, DataHoje.data()))) {
            GenericaMensagem.warn("Atenção", "Data de entrada não pode ser maior que 1 Ano!");
            PF.openDialog("dlg_entrada");
            return null;
        }

        if (Moeda.converteUS$(valorTaxa) <= 0) {
            GenericaMensagem.warn("Atenção", "Digite um valor para a entrada!");
            return null;
        }

        if (tipoMatricula.equals("Turma")) {
            plano5 = matriculaTurma.getTurma().getCursos().getPlano5();
            servicos = matriculaTurma.getTurma().getCursos();
        } else {
            plano5 = matriculaIndividual.getCurso().getPlano5();
            servicos = matriculaIndividual.getCurso();
        }

        FTipoDocumento fTipoDocumento = (FTipoDocumento) new Dao().find(new FTipoDocumento(), servicoPessoa.getTipoDocumento().getId());
        setLote(
                new Lote(
                        -1,
                        (Rotina) new Dao().find(new Rotina(), 151),
                        "R",
                        DataHoje.data(),
                        servicoPessoa.getCobranca(),
                        plano5,
                        false,
                        "",
                        0, // matriculaEscola.getValorTotal(),
                        matriculaEscola.getFilial(),
                        null,
                        servicoPessoa.getEvt(),
                        "",
                        fTipoDocumento,
                        (CondicaoPagamento) new Dao().find(new CondicaoPagamento(), 1),
                        (FStatus) new Dao().find(new FStatus(), 1),
                        null,
                        servicoPessoa.isDescontoFolha(),
                        0,
                        null,
                        null,
                        null,
                        false,
                        "",
                        null,
                        ""
                )
        );

        String referencia = DataHoje.converteData(dataEntrada);
        referencia = referencia.substring(3, 10);

        setMovimento(
                new Movimento(
                        -1,
                        lote,
                        plano5,
                        servicoPessoa.getCobranca(),
                        servicos,
                        null,
                        (TipoServico) new Dao().find(new TipoServico(), 1),
                        null,
                        Moeda.converteUS$(valorTaxa),
                        referencia,
                        DataHoje.converteData(dataEntrada),
                        1,
                        true,
                        "E",
                        false,
                        servicoPessoa.getCobranca(), // TITULAR / RESPONSÁVEL
                        servicoPessoa.getPessoa(), // BENEFICIÁRIO
                        "",
                        "",
                        DataHoje.converteData(dataEntrada),
                        0, //valorDescontoAteVencimento,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        fTipoDocumento,
                        0,
                        new MatriculaSocios()
                )
        );

        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.save(lote)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Lote!");
            dao.rollback();
            return null;
        }

        // ATUALIZAR VARIÁVEL MATRICULA SÓCIO ( SENÃO TENTA GRAVAR NO BANCO -1 CANSANDO ERRO )
        movimento.getMatriculaSocios();
        if (!dao.save(movimento)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar Movimento!");
            dao.rollback();
            return null;
        }

        dao.commit();

        if (DataHoje.converteData(dataEntrada).equals(DataHoje.data())) {
            return telaBaixa("caixa");
        } else {
            GenericaMensagem.info("Sucesso", "Matrícula Concluída!");
            PF.closeDialog("dlg_entrada");
            return null;
        }
    }

    public void calculoValor() {
        String valorx;
        Servicos se = (Servicos) new Dao().find(new Servicos(), idServico);
        if (se != null) {
            if (servicoPessoa.getNrDesconto() == 0) {
                if (aluno.getPessoa().getSocios().getId() != -1) {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, aluno.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
                } else {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, null));
                }
            } else {
                double valorx_c = new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), se.getId(), DataHoje.dataHoje());

                double calculo = Moeda.divisao(Moeda.multiplicar(servicoPessoa.getNrDesconto(), valorx_c), 100);
                valorx = Moeda.converteR$Double(Moeda.subtracao(valorx_c, Moeda.converteDoubleR$Double(calculo)));
            }

            valor = Moeda.converteR$(valorx);
            vTaxa = new FunctionsDao().valorServico(aluno.getPessoa().getId(), idServico, DataHoje.dataHoje(), 2, null);
        }
    }

    public void calculoDesconto() {
        String valorx;
        Servicos se = (Servicos) new Dao().find(new Servicos(), idServico);
        if (se != null) {
            if (aluno.getPessoa().getSocios().getId() != -1) {
                valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, aluno.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
            } else {
                valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(servicoPessoa.getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, null));
            }

            String valorx_cheio = Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(servicoPessoa.getPessoa().getId(), se.getId(), DataHoje.dataHoje()));

            if (Moeda.converteUS$(valor) == Moeda.converteUS$(valorx)) {
                servicoPessoa.setNrDescontoString("0.0");
            } else {
                double valorx_c = Moeda.subtracao(Moeda.converteUS$(valorx_cheio), Moeda.converteUS$(valor));
                valorx_c = Moeda.multiplicar(Moeda.divisao(valorx_c, Moeda.converteUS$(valorx_cheio)), 100);
                servicoPessoa.setNrDescontoString(Double.toString(valorx_c));
            }

            if (aluno.getPessoa().getId() != -1) {
                Integer idade = new DataHoje().calcularIdade(aluno.getDtNascimento());
                List<ServicoValor> lsv = new MatriculaEscolaDao().listServicoValorPorServicoIdade(se.getId(), idade);
                valorParcelaVencimento = (lsv.isEmpty()) ? valor : Moeda.converteR$Double(Moeda.converteUS$(Moeda.valorDoPercentual(valor, Moeda.converteR$Double(lsv.get(0).getDescontoAteVenc()))));
            } else {
                valorParcelaVencimento = valor;
            }
        }
    }

    public void gerarContrato() {
        if (matriculaEscola.getId() != -1) {
            Dao dao = new Dao();
            String contratoCurso;
            String contratoDiaSemana = "";
            MatriculaContratoDao mcd = new MatriculaContratoDao();
            if (tipoMatricula.equals("Individual")) {
                matriculaContrato = mcd.pesquisaCodigoServico(matriculaIndividual.getCurso().getId());
            } else {
                matriculaContrato = mcd.pesquisaCodigoServico(matriculaTurma.getTurma().getCursos().getId());
            }
            if (matriculaContrato == null) {
                GenericaMensagem.error("Sistema", "Não é possível gerar um contrato para este serviço. Para gerar um contrato acesse: Menu Escola > Suporte > Modelo Contrato.");
                return;
            }
            String dataInicial;
            String dataFinal;
            String horaInicial;
            String horaFinal;
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$aluno", servicoPessoa.getPessoa().getNome()));
            FisicaDao fisicaDB = new FisicaDao();
            Fisica contratoFisica = fisicaDB.pesquisaFisicaPorPessoa(servicoPessoa.getCobranca().getId());
            if (contratoFisica == null) {
                contratoFisica = new Fisica();
            }
            Fisica rgAluno = fisicaDB.pesquisaFisicaPorPessoa(servicoPessoa.getPessoa().getId());
            if (rgAluno == null) {
                rgAluno = new Fisica();
            }
            List listaDiaSemana = new ArrayList();
            int periodoMeses = 0;
            String periodoMesesExtenso;
            Object periodoMesesObject;
            if (tipoMatricula.equals("Individual")) {
                contratoCurso = matriculaIndividual.getCurso().getDescricao();
                if (matriculaIndividual.getSegunda()) {
                    listaDiaSemana.add("Seg");
                }
                if (matriculaIndividual.getTerca()) {
                    listaDiaSemana.add("Ter");
                }
                if (matriculaIndividual.getQuarta()) {
                    listaDiaSemana.add("Qua");
                }
                if (matriculaIndividual.getQuinta()) {
                    listaDiaSemana.add("Qui");
                }
                if (matriculaIndividual.getSexta()) {
                    listaDiaSemana.add("Sex");
                }
                if (matriculaIndividual.getSabado()) {
                    listaDiaSemana.add("Sab");
                }
                if (matriculaIndividual.getDomingo()) {
                    listaDiaSemana.add("Dom");
                }
                horaInicial = matriculaIndividual.getInicio();
                horaFinal = matriculaIndividual.getTermino();
                dataInicial = matriculaIndividual.getDataInicioString();
                dataFinal = matriculaIndividual.getDataTerminoString();
                if (dataFinal.isEmpty()) {
                    dataFinal = "* (data Indeterminada)";
                    periodoMesesObject = "Indeterminado";
                } else {
                    periodoMeses = DataHoje.quantidadeMeses(matriculaIndividual.getDataInicio(), matriculaIndividual.getDataTermino());
                    periodoMesesObject = periodoMeses;
                }
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$mesAnoInicialExtenso", DataHoje.dataExtenso(matriculaIndividual.getDataInicioString(), 1)));
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$mesAnoFinalExtenso", DataHoje.dataExtenso(matriculaIndividual.getDataTerminoString(), 1)));
            } else {
                Turma turmax = ((MatriculaTurma) dao.find(matriculaTurma)).getTurma();
                contratoCurso = matriculaTurma.getTurma().getCursos().getDescricao();
                periodoMesesObject = DataHoje.quantidadeMeses(turma.getDtInicio(), turma.getDtTermino());
                if (turmax.getSegunda()) {
                    listaDiaSemana.add("Seg");
                }
                if (turmax.getTerca()) {
                    listaDiaSemana.add("Ter");
                }
                if (turmax.getQuarta()) {
                    listaDiaSemana.add("Qua");
                }
                if (turmax.getQuinta()) {
                    listaDiaSemana.add("Qui");
                }
                if (turmax.getSexta()) {
                    listaDiaSemana.add("Sex");
                }
                if (turmax.getSabado()) {
                    listaDiaSemana.add("Sab");
                }
                if (turmax.getDomingo()) {
                    listaDiaSemana.add("Dom");
                }
                dataInicial = matriculaTurma.getTurma().getDataInicio();
                dataFinal = matriculaTurma.getTurma().getDataTermino();
                if (dataFinal.isEmpty()) {
                    dataFinal = "Indeterminada";
                }
                horaInicial = matriculaTurma.getTurma().getHoraInicio();
                horaFinal = matriculaTurma.getTurma().getHoraTermino();
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$descricao", turmax.getDescricao()));
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$mesAnoInicialExtenso", DataHoje.dataExtenso(matriculaTurma.getTurma().getDataInicio(), 1)));
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$mesAnoFinalExtenso", DataHoje.dataExtenso(matriculaTurma.getTurma().getDataTermino(), 1)));
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$sala", matriculaTurma.getTurma().getSala()));
            }
            if (periodoMeses == 0) {
                periodoMesesExtenso = "mês atual";
            } else {
                ValorExtenso valorExtenso = new ValorExtenso();
                valorExtenso.setNumber((double) periodoMeses);
                periodoMesesExtenso = (valorExtenso.toString()).replace("reais", "");
            }
            for (int i = 0; i < listaDiaSemana.size(); i++) {
                if (i == 0) {
                    contratoDiaSemana = listaDiaSemana.get(i).toString();
                } else {
                    contratoDiaSemana += " , " + listaDiaSemana.get(i).toString();
                }
            }
            String enderecoAlunoString = "";
            String bairroAlunoString = "";
            String cidadeAlunoString = "";
            String estadoAlunoString = "";
            String cepAlunoString = "";
            String enderecoResponsavelString = "";
            String bairroResponsavelString = "";
            String cidadeResponsavelString = "";
            String estadoResponsavelString = "";
            String cepResponsavelString = "";
            PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
            PessoaEndereco pessoaEnderecoAluno = (PessoaEndereco) pessoaEnderecoDB.pesquisaEndPorPessoaTipo(servicoPessoa.getPessoa().getId(), 1);

            int idTipoEndereco = -1;
            if (pessoaEnderecoAluno != null) {
                enderecoAlunoString = pessoaEnderecoAluno.getEndereco().getEnderecoSimplesToString() + ", " + pessoaEnderecoAluno.getNumero();
                bairroAlunoString = pessoaEnderecoAluno.getEndereco().getBairro().getDescricao();
                cidadeAlunoString = pessoaEnderecoAluno.getEndereco().getCidade().getCidade();
                estadoAlunoString = pessoaEnderecoAluno.getEndereco().getCidade().getUf();
                cepAlunoString = pessoaEnderecoAluno.getEndereco().getCep();
            }
            if (servicoPessoa.getCobranca().getId() != servicoPessoa.getPessoa().getId()) {
                // Tipo Documento - CPF
                if (servicoPessoa.getCobranca().getTipoDocumento().getId() == 1) {
                    idTipoEndereco = 1;
                    // Tipo Documento - CNPJ
                } else if (servicoPessoa.getCobranca().getTipoDocumento().getId() == 2) {
                    idTipoEndereco = 3;
                }
            } else {
                enderecoResponsavelString = enderecoAlunoString;
                bairroResponsavelString = bairroAlunoString;
                cidadeResponsavelString = cidadeAlunoString;
                estadoResponsavelString = estadoAlunoString;
                cepResponsavelString = cepAlunoString;
            }
            PessoaEndereco pessoaEnderecoResponsavel = (PessoaEndereco) pessoaEnderecoDB.pesquisaEndPorPessoaTipo(servicoPessoa.getCobranca().getId(), idTipoEndereco);
            if (pessoaEnderecoResponsavel != null) {
                enderecoResponsavelString = pessoaEnderecoResponsavel.getEndereco().getEnderecoSimplesToString() + ", " + pessoaEnderecoResponsavel.getNumero();
                bairroResponsavelString = pessoaEnderecoResponsavel.getEndereco().getBairro().getDescricao();
                cidadeResponsavelString = pessoaEnderecoResponsavel.getEndereco().getCidade().getCidade();
                estadoResponsavelString = pessoaEnderecoResponsavel.getEndereco().getCidade().getUf();
                cepResponsavelString = pessoaEnderecoResponsavel.getEndereco().getCep();
            }
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$cpfAluno", (servicoPessoa.getPessoa().getDocumento())));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$rgAluno", rgAluno.getRg()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$telefonesAluno", servicoPessoa.getPessoa().getTelefone1() + " . " + servicoPessoa.getPessoa().getTelefone2() + " . " + servicoPessoa.getPessoa().getTelefone3()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$estadoCivilAluno", rgAluno.getEstadoCivil()));
            if (getResponsavel().getId() != -1) {

            }
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$responsavel", (servicoPessoa.getCobranca().getNome())));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$cpfResponsavel", servicoPessoa.getCobranca().getDocumento()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$rgResponsavel", (contratoFisica.getRg())));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$telefonesResponsavel", servicoPessoa.getCobranca().getTelefone1() + " . " + servicoPessoa.getCobranca().getTelefone2() + " . " + servicoPessoa.getCobranca().getTelefone3()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$estadoCivilResponsavel", contratoFisica.getEstadoCivil()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$curso", (contratoCurso)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$diaSemana", (contratoDiaSemana)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$dataInicialExtenso", (DataHoje.dataExtenso(dataInicial))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$dataFinalExtenso", (DataHoje.dataExtenso(dataFinal))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$dataExtenso", (DataHoje.dataExtenso(DataHoje.data()))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$dataInicial", (dataInicial)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$dataFinal", (dataFinal)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$descontoExtenso", Moeda.converteR$Double(servicoPessoa.getNrDesconto())));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$desconto", Moeda.converteR$Double(servicoPessoa.getNrDesconto())));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$parcelas", (Integer.toString(numeroParcelas))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$diaVencimento", (Integer.toString(servicoPessoa.getNrDiaVencimento()))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorAteVencimento", getValorString()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$horaInicial", (horaInicial)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$horaFinal", (horaFinal)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$dataMatricula", DataHoje.dataExtenso(servicoPessoa.getEmissao(), 1)));
            double valorTotalComDesconto = 0;

//            if (false == true) {
//                String valorTotalx = new FunctionsDao().scriptSimples(" SUM(nr_valor) FROM fin_movimento WHERE id_tipo_servico = 1 AND id_lote = " + listaMovimentos.get(0).getLote().getId());
//                if (!valorTotalx.isEmpty()) {
//                    valorTotalComDesconto = Moeda.subtracaoValores(Moeda.converteUS$(valorTotalx), servicoPessoa.getNrDesconto());
//                }
//                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorTotalComDesconto", Moeda.converteR$Double(valorTotalComDesconto)));
//                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorTotal", Moeda.converteR$Double(Double.parseDouble(valorTotal))));
//                if (valorTotalComDesconto > 0) {
//                    // AQUI matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorParcelaComDesconto", (Moeda.converteR$Double(valorTotalComDesconto / matriculaEscola.getNumeroParcelas()))));
//                }
//            } else {
//                //valorTotalComDesconto = matriculaEscola.getValorTotal() - matriculaEscola.getDesconto();
//                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorTotalComDesconto", (Moeda.converteR$Double((valorTotalComDesconto)))));
//                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorTotal", getValorString()));
//                if (valorTotalComDesconto > 0) {
//                    // AQUI matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorParcelaComDesconto", (Moeda.converteR$Double(valorTotalComDesconto / matriculaEscola.getNumeroParcelas()))));
//                }
//            }
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorTotal", valorTotal));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorTotalComDesconto", valorTotal));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorParcelaComDesconto", getValorString()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$valorParcela", getValorString()));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$matricula", (Integer.toString(matriculaEscola.getId()))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$ano", (DataHoje.livre(DataHoje.dataHoje(), "yyyy"))));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$enderecoAluno", (enderecoAlunoString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$bairroAluno", (bairroAlunoString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$cidadeAluno", (cidadeAlunoString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$estadoAluno", (estadoAlunoString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$cepAluno", (cepAlunoString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$mesesExtenso", (periodoMesesExtenso)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$meses", periodoMesesObject.toString()));
            String alunoNascimento = "";
            if (contratoFisica.getId() != -1) {
                alunoNascimento = (contratoFisica.getNascimento());
            }
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$nascimentoAluno", (alunoNascimento)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$emailAluno", (servicoPessoa.getPessoa().getEmail1())));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$enderecoResponsavel", (enderecoResponsavelString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$bairroResponsavel", (bairroResponsavelString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$cidadeResponsavel", (cidadeResponsavelString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$estadoResponsavel", (estadoResponsavelString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$cepResponsavel", (cepResponsavelString)));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$emailResponsavel", (servicoPessoa.getCobranca().getEmail1())));
            String valorTaxaString = "";
            String listaValores = "";
            String listaValoresComData = "";
            int z = 1;
            int quantidadeDivisao = 0;
//            if (!listaMovimentos.isEmpty()) {
//                if (listaMovimentos.get(0).getTipoServico().getId() == 5) {
//                    if (listaMovimentos.size() > 1) {
//                        quantidadeDivisao = listaMovimentos.size() - 1;
//                    } else {
//                        quantidadeDivisao = listaMovimentos.size();
//                    }
//                } else {
//                    if (listaMovimentos.size() > 1) {
//                        quantidadeDivisao = listaMovimentos.size();
//                    }
//                }
//            }
            double valorDesc = servicoPessoa.getNrDesconto() / quantidadeDivisao;
//            for (Movimento listaMovimento : listaMovimentos) {
//                if (listaMovimento.getTipoServico().getId() == 5) {
//                    valorTaxaString = Moeda.converteR$Double(listaMovimento.getValor());
//                } else {
//                    if (z == 1) {
//                        if (valorDesc > 0) {
//                            listaValores = "Parcela nº" + z + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor() - valorDesc);
//                            listaValoresComData = z + "º - " + listaMovimento.getVencimento() + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor() - valorDesc);
//                        } else {
//                            listaValores = "Parcela nº" + z + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor());
//                            listaValoresComData = z + "º - " + listaMovimento.getVencimento() + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor());
//                        }
//                    } else {
//                        if (listaMovimento.getDesconto() > 0) {
//                            listaValores += "; " + "Parcela nº" + z + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor() - valorDesc);
//                            listaValoresComData += "; " + z + "º - " + listaMovimento.getVencimento() + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor() - valorDesc);
//                        } else {
//                            listaValores += "; " + "Parcela nº" + z + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor());
//                            listaValoresComData += "; " + z + "º - " + listaMovimento.getVencimento() + " - Valor: R$ " + Moeda.converteR$Double(listaMovimento.getValor());
//                        }
//                    }
//                    matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$vencimentoParcela" + z, listaMovimento.getVencimento()));
//                    z++;
//                }
//            }
//            
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$vencimentoParcela", ""));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$taxa", valorTaxaString));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$listaValoresComData", listaValoresComData));
            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$listaValores", listaValores));

            PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
            PessoaEmpresa pessoaEmpresaAluno = pessoaEmpresaDB.pesquisaPessoaEmpresaPorPessoa(servicoPessoa.getPessoa().getId());
            PessoaEmpresa pessoaEmpresaResponsavel = pessoaEmpresaDB.pesquisaPessoaEmpresaPorPessoa(servicoPessoa.getCobranca().getId());
            if (pessoaEmpresaAluno.getId() != -1) {
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$localTrabalhoAluno", (pessoaEmpresaAluno.getJuridica().getPessoa().getNome() + " - CNPJ: " + pessoaEmpresaAluno.getJuridica().getPessoa().getDocumento())));
            } else {
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$localTrabalhoAluno", ""));
            }
            if (pessoaEmpresaResponsavel.getId() != -1) {
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$localTrabalhoResponsavel", (pessoaEmpresaResponsavel.getJuridica().getPessoa().getNome() + " - CNPJ: " + pessoaEmpresaResponsavel.getJuridica().getPessoa().getDocumento())));
            } else {
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("$localTrabalhoResponsavel", ""));
            }

            matriculaContrato.setDescricao(matriculaContrato.getDescricao().replace("<br>", "<br />"));
            try {
                matriculaContrato.setDescricao(matriculaContrato.getDescricao().replaceAll("(<img[^>]*[^/]>)(?!\\s*</img>)", "$1</img>"));
            } catch (Exception e) {

            }
            try {
                File dirFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/contrato/"));
                if (!dirFile.exists()) {
                    if (!Diretorio.criar("Arquivos/contrato")) {
                        return;
                    }
                }
                String fileName = "contrato" + DataHoje.hora().hashCode() + ".pdf";
                String filePDF = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/contrato/" + fileName);
                File file = new File(filePDF);
                boolean success = file.createNewFile();
                if (success) {
                    // LOG CONTRATO
                    NovoLog novoLog = new NovoLog();
                    novoLog.setCodigo(matriculaEscola.getId());
                    novoLog.setTabela("matr_escola");
                    novoLog.print("Matricula Escola "
                            + tipoMatricula + ": ID " + matriculaEscola.getId()
                            + " - Aluno: " + servicoPessoa.getPessoa().getId() + " - " + servicoPessoa.getPessoa().getNome()
                            + " - Responsável: " + servicoPessoa.getCobranca().getId() + " - " + servicoPessoa.getCobranca().getNome()
                            + " - Mac Filial da impressão: " + MacFilial.getAcessoFilial().getDescricao() + " - MAC: " + MacFilial.getAcessoFilial().getMac()
                    );

                    try (OutputStream os = new FileOutputStream(filePDF)) {
                        HtmlToPDF.convert(matriculaContrato.getDescricao(), os);
                    }
                    String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/contrato");
                    Download download = new Download(fileName, pathPasta, "application/pdf", FacesContext.getCurrentInstance());
                    download.baixar();
                    download.remover();
                }
            } catch (IOException e) {
                e.getMessage();
            } catch (com.itextpdf.text.DocumentException ex) {
                Logger.getLogger(MatriculaEscolaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void gerarCarne() throws Exception {
        if (matriculaEscola.getServicoPessoa().getEvt() != null) {
//            if (listaMovimentos.size() > 0) {
//                PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
//                PessoaEndereco pessoaEndereco = ((List<PessoaEndereco>) pessoaEnderecoDB.pesquisaEndPorPessoa(matriculaEscola.getFilial().getFilial().getPessoa().getId())).get(0);
//                List<CarneEscola> list = new ArrayList<>();
//                int j = 1;
//                for (Movimento listaMovimento : listaMovimentos) {
//                    if (listaMovimento.getTipoServico().getId() != 5) {
//                        list.add(new CarneEscola(
//                                matriculaEscola.getId(),
//                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
//                                GenericaString.converterNullToString(matriculaEscola.getFilial().getFilial().getPessoa().getNome()),
//                                GenericaString.converterNullToString(pessoaEndereco.getEndereco().getLogradouro().getDescricao()),
//                                GenericaString.converterNullToString(pessoaEndereco.getNumero()),
//                                GenericaString.converterNullToString(pessoaEndereco.getComplemento()),
//                                GenericaString.converterNullToString(pessoaEndereco.getEndereco().getBairro().getDescricao()),
//                                GenericaString.converterNullToString(pessoaEndereco.getEndereco().getCep()),
//                                GenericaString.converterNullToString(pessoaEndereco.getEndereco().getCidade().getCidade()),
//                                GenericaString.converterNullToString(pessoaEndereco.getEndereco().getCidade().getUf()),
//                                GenericaString.converterNullToString(matriculaEscola.getFilial().getFilial().getPessoa().getTelefone1()),
//                                GenericaString.converterNullToString(matriculaEscola.getFilial().getFilial().getPessoa().getEmail1()),
//                                GenericaString.converterNullToString(matriculaEscola.getFilial().getFilial().getPessoa().getSite()),
//                                GenericaString.converterNullToString(matriculaEscola.getFilial().getFilial().getPessoa().getDocumento()),
//                                GenericaString.converterNullToString(servicoPessoa.getCobranca().getNome()),
//                                GenericaString.converterNullToString(Integer.toString(servicoPessoa.getCobranca().getId())),
//                                GenericaString.converterNullToString(servicoPessoa.getPessoa().getNome()),
//                                GenericaString.converterNullToString(""),
//                                GenericaString.converterNullToString(listaMovimento.getServicos().getDescricao()),
//                                GenericaString.converterNullToString(DataHoje.converteData(listaMovimento.getDtVencimento())),
//                                getValorString(), // GenericaString.converterNullToString(Double.toString(matriculaEscola.getValorTotal())),  VALOR TOTAL
//                                GenericaString.converterNullToString(Double.toString(listaMovimento.getValor())),
//                                ((Usuario) (GenericaSessao.getObject("sessaoUsuario"))).getPessoa().getNome(),
//                                j++,
//                                "")
//                        );
//                    }
//                }
//                if (!list.isEmpty()) {
//                    Jasper.PATH = "downloads";
//                    Jasper.PART_NAME = "escola";
//                    Jasper.printReports("/Relatorios/CARNE.jasper", "carne", list);
//                }
//            }
        }

    }

    public void limpar() {
        if (limpar == true) {
            novo();
        }
    }

    public String converteData(Date data) {
        return DataHoje.converteData(data);
    }

    public void salvar() {
        Filial fil = getMacFilial().getFilial();
        if (fil.getId() == -1) {
            GenericaMensagem.warn("Atenção", "Para salvar este cadastro é necessário realizar acesso com MAC Filial!");
            return;
        }

        if (!validaDataVigoracaoValidadeBoolean()) {
            return;
        }

        int idPessoa = servicoPessoa.getCobranca().getId();
        if (servicoPessoa.getPessoa().getId() == -1) {
            GenericaMensagem.warn("Atenção", "Informar nome do aluno!");
            return;
        }
        ConfiguracaoSocial cs = ConfiguracaoSocial.get();
        if (cs.getObrigatorioEmail()) {
            if (servicoPessoa.getPessoa().getEmail1().isEmpty()) {
                GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO PARA O ALUNO!");
                GenericaMensagem.warn("Sistema", "CORRIJA ESTE CADASTRO PARA REALIZAR ALTERAÇÕES!");
                return;
            }
        }
        if (servicoPessoa.getCobranca().getId() == -1) {
            GenericaMensagem.warn("Atenção", "Informar nome do responsável!");
            return;
        }
        if (verificaSeContribuinteInativo(servicoPessoa.getCobranca())) {
            GenericaMensagem.warn("Atenção", "Empresa inativa!");
            return;
        }
        SpcDao spcDB = new SpcDao();
        if (spcDB.existeRegistroPessoaSPC(servicoPessoa.getCobranca())) {
            GenericaMensagem.warn("Atenção", "Responsável possui cadastro SERASA/SPC!");
            return;
        }
        getFilial();
        Dao dao = new Dao();
        matriculaEscola.setFilial((Filial) dao.find(new Filial(), Integer.parseInt(getListFiliais().get(filial_id).getDescription())));
        if (matriculaEscola.getFilial().getId() == -1) {
            GenericaMensagem.warn("Atenção", "Informar a filial! Obs: Necessário acessar o sistema usando autênticação.");
            return;
        }
        if (listaStatus.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Informar a situação/status do aluno!");
            return;
        }
        if (listaVendedor.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Informar o nome do vendedor!");
            return;
        }
        if (listaMidia.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Informar o tipo de mídia!");
            return;
        }
        if (listaMidia.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Informar o tipo de mídia!");
            return;
        }

        if (tipoMatricula.equals("Individual")) {
            if (matriculaEscola.getId() == -1) {
                if (matriculaIndividual.getDataInicio() == null) {
                    GenericaMensagem.warn("Atenção", "Informar a data inicial!");
                    return;
                }
                int dataInicioInteger = DataHoje.converteDataParaInteger(matriculaIndividual.getDataInicioString());
                int dataFinalInteger = DataHoje.converteDataParaInteger(matriculaIndividual.getDataTerminoString());
                int dataHojeInteger = DataHoje.converteDataParaInteger(servicoPessoa.getEmissao());
                if (dataInicioInteger < dataHojeInteger) {
                    GenericaMensagem.warn("Atenção", "A data inicial do curso deve ser maior ou igual a data de emissão!");
                    return;
                }
                if (getServicoIndividual().isCursoRenovacao()) {
                    servicoPessoa.setReferenciaValidade("");
                    matriculaIndividual.setDataTermino(null);
                } else {
                    if (matriculaIndividual.getDataTermino() == null) {
                        GenericaMensagem.warn("Atenção", "Informar a data de término!");
                        return;
                    }
                    if (dataFinalInteger < dataHojeInteger) {
                        GenericaMensagem.warn("Atenção", "A data final do curso deve ser maior ou igual a data de emissão!");
                        return;
                    }
                    if (dataFinalInteger < dataInicioInteger) {
                        GenericaMensagem.warn("Atenção", "A data final deve ser maior ou igual a data inicial!");
                        return;
                    }
                }
                if (DataHoje.validaHora(matriculaIndividual.getInicio()).isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Hora inicial invalida!");
                    return;
                }
                if (DataHoje.validaHora(matriculaIndividual.getTermino()).isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Hora final invalida!");
                    return;
                }
            }
            if (listaIndividual.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar curso!");
                return;
            }
            if (listaProfessor.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar o nome do professor! Caso não exista cadastre um professor.");
                return;
            }

            if (!matriculaIndividual.getDomingo() && !matriculaIndividual.getSegunda() && !matriculaIndividual.getTerca() && !matriculaIndividual.getQuarta() && !matriculaIndividual.getQuinta() && !matriculaIndividual.getSexta() && !matriculaIndividual.getSabado()) {
                GenericaMensagem.warn("Atenção", "Selecione ao menos um Dia da Semana!");
                return;
            }
        } else {
            if (listaTurma.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Informar a turma!");
                return;
            }
            if (turma.getId() == -1) {
                GenericaMensagem.warn("Atenção", "Informar a turma!");
                return;
            }
        }

        NovoLog novoLog = new NovoLog();
        matriculaEscola.setVendedor((Vendedor) dao.find(new Vendedor(), Integer.parseInt(listaVendedor.get(idVendedor).getDescription())));
        matriculaEscola.setMidia((Midia) dao.find(new Midia(), Integer.parseInt(listaMidia.get(idMidia).getDescription())));
        servicoPessoa.setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 2));

        if (tipoMatricula.equals("Individual")) {
            if (Integer.parseInt(listaIndividual.get(idIndividual).getDescription()) != 0) {
                matriculaIndividual.setCurso((Servicos) dao.find(new Servicos(), Integer.parseInt(listaIndividual.get(idIndividual).getDescription())));
                servicoPessoa.setServicos(matriculaIndividual.getCurso());
            } else {
                matriculaIndividual.setCurso(null);
                servicoPessoa.setServicos(null);
            }
            if (Integer.parseInt(listaProfessor.get(idProfessor).getDescription()) != 0) {
                matriculaIndividual.setProfessor((Professor) dao.find(new Professor(), Integer.parseInt(listaProfessor.get(idProfessor).getDescription())));
            } else {
                matriculaIndividual.setProfessor(null);
            }

            //servicoPessoa.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(matriculaIndividual.getDataInicioString()));
            //servicoPessoa.setReferenciaValidade(DataHoje.converteDataParaReferencia(matriculaIndividual.getDataTerminoString()));
        } else if (tipoMatricula.equals("Turma")) {
            matriculaTurma.setTurma(turma);
            servicoPessoa.setServicos(turma.getCursos());
            //servicoPessoa.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(turma.getDataInicio()));
            //servicoPessoa.setReferenciaValidade(DataHoje.converteDataParaReferencia(turma.getDataTermino()));
        }

        //servicoPessoa.setNrDiaVencimento(idDiaParcela);
        servicoPessoa.setNrDiaVencimento(idDiaVencimento);
        dao.openTransaction();
        if (matriculaEscola.getId() == -1) {
            servicoPessoa.setDescontoSocial((DescontoSocial) new Dao().find(new DescontoSocial(), 1));

            Evt evt = new Evt();
            if (!dao.save(evt)) {
                GenericaMensagem.error("Atenção", "Erro ao Salvar EVT!");
                dao.rollback();
                return;
            }

            servicoPessoa.setEvt(evt);
            if (!dao.save(servicoPessoa)) {
                GenericaMensagem.error("Atenção", "Erro ao Salvar Serviço Pessoa!");
                dao.rollback();
                return;
            }
            if (idParceiro != null && idParceiro != -1) {
                servicoPessoa.setParceiro((Pessoa) dao.find(new Pessoa(), idParceiro));
            }
            matriculaEscola.setServicoPessoa(servicoPessoa);
            matriculaEscola.setEscStatus((EscStatus) dao.find(new EscStatus(), 1));
            MatriculaEscolaDao med = new MatriculaEscolaDao();
            FunctionsDao functionsDB = new FunctionsDao();
            int idNumeroVagas = functionsDB.vagasEscolaTurma(matriculaTurma.getTurma().getId());
            if (idNumeroVagas == 0 && !tipoMatricula.equals("Individual")) {
                matriculaEscola.setId(-1);
                matriculaTurma.setId(-1);
                matriculaIndividual.setId(-1);
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Não existem mais vagas disponíveis para esta turma!");
                return;
            }
            pessoaComplemento = med.pesquisaDataRefPessoaComplemto(idPessoa);
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
                pessoaComplemento.setNrDiaVencimento(idDiaVencimento);
                if (idFTipoDocumento == 2) {
                    pessoaComplemento.setCobrancaBancaria(true);
                } else {
                    pessoaComplemento.setCobrancaBancaria(false);
                }
                pessoaComplemento.setPessoa((Pessoa) dao.find(new Pessoa(), idPessoa));
                if (!dao.save(pessoaComplemento)) {
                    dao.rollback();
                    GenericaMensagem.error("Atenção", "Falha ao inserir pessoa complemento!");
                    return;
                }
            }
            //matriculaEscola.setHabilitado(true);
            getFilial();
            // matriculaEscola.setFilial((Filial) dao.find(new Filial(), Integer.parseInt(getListFiliais().get(filial_id).getDescription())));

            getMacFilial();
            String tipoMatriculaLog;

            if (dao.save(matriculaEscola)) {
                if (tipoMatricula.equals("Turma")) {
                    if (med.existeVagasDisponivel(matriculaTurma)) {
                        dao.rollback();
                        GenericaMensagem.warn("Atenção", "Não existem mais vagas disponíveis para essa turma!");
                        return;
                    }
                    matriculaTurma.setMatriculaEscola(matriculaEscola);
                    if (med.existeMatriculaTurma(matriculaTurma)) {
                        dao.rollback();
                        matriculaEscola.setId(-1);
                        matriculaTurma.setId(-1);
                        matriculaIndividual.setId(-1);
                        GenericaMensagem.warn("Atenção", "Aluno já cadastrado para essa turma!");
                        return;
                    }
                    setDesabilitaIndividual(true);
                    if (!dao.save(matriculaTurma)) {
                        matriculaEscola.setId(-1);
                        matriculaTurma.setId(-1);
                        matriculaIndividual.setId(-1);
                        dao.rollback();
                        GenericaMensagem.error("Atenção", "Falha ao adicionar a matricula turma!");
                        return;
                    }
                    tipoMatriculaLog = ""
                            + " - Turma: " + matriculaTurma.getTurma().getId() + " - " + matriculaTurma.getTurma().getCursos().getDescricao();
                } else {
                    setDesabilitaTurma(true);
                    matriculaIndividual.setMatriculaEscola(matriculaEscola);
                    if (med.existeMatriculaIndividual(matriculaIndividual)) {
                        dao.rollback();
                        matriculaEscola.setId(-1);
                        matriculaTurma.setId(-1);
                        matriculaIndividual.setId(-1);
                        GenericaMensagem.warn("Atenção", "Aluno já cadastrado para essa matricula!");
                        return;
                    }
                    if (!dao.save(matriculaIndividual)) {
                        matriculaEscola.setId(-1);
                        matriculaTurma.setId(-1);
                        matriculaIndividual.setId(-1);
                        dao.rollback();
                        GenericaMensagem.error("Atenção", "Falha ao adicionar esta matricula individual!");
                        return;
                    }
                    tipoMatriculaLog = " - Curso: " + matriculaIndividual.getCurso().getId() + " - " + matriculaIndividual.getCurso().getDescricao()
                            //+ " - Professor: " + matriculaIndividual.getProfessor().getId() + " - " + matriculaIndividual.getProfessor().getProfessor()
                            + " - Período: " + matriculaIndividual.getDataInicioString() + " até " + matriculaIndividual.getDataTerminoString();
                }

                //pessoaResponsavelMemoria = servicoPessoa.getCobranca();
                //pessoaAlunoMemoria = servicoPessoa.getPessoa();
                dao.commit();
                desabilitaCampo = true;

                novoLog.save("Matricula Escola "
                        + tipoMatricula + ": ID " + matriculaEscola.getId()
                        + " - Aluno: " + servicoPessoa.getPessoa().getId() + " - " + servicoPessoa.getPessoa().getNome()
                        + " - Responsável: " + servicoPessoa.getCobranca().getId() + " - " + servicoPessoa.getCobranca().getNome()
                        //+ " - Desconto: " + matriculaEscola.getDescontoAteVencimentoString()
                        + " - Dia do vencimento: " + servicoPessoa.getNrDiaVencimento()//matriculaEscola.getDiaVencimento()
                        //+ " - Valor: " + matriculaEscola.getValorTotalString()
                        + " - Número de Parcelas: " //+ matriculaEscola.getEscStatus().getDescricao()
                        + " - Filial: " + matriculaEscola.getFilial().getFilial().getPessoa().getId()
                        + " - Midia: " + matriculaEscola.getMidia().getDescricao()
                        + tipoMatriculaLog
                );

                target = "_blank";
                dao.close();

                GenericaMensagem.info("Sucesso", "Matrícula efetuada com sucesso!");
                carregaEntrada();
                PF.update("form_matricula_escola:panel_entrada");
                PF.openDialog("dlg_entrada");
            } else {
                dao.rollback();
                matriculaEscola.setId(-1);
                matriculaTurma.setId(-1);
                matriculaIndividual.setId(-1);
                GenericaMensagem.error("Erro", "Não foi possível salvar Matrícula, tente novamente!");
            }
        } else {
            if (!Objects.equals(fil.getId(), matriculaEscola.getFilial().getId())) {
                GenericaMensagem.warn("Atençao", "Registro não pode ser atualizado por esta filial!");
                return;
            }
            if (matriculaEscola.getEscStatus().getId() == 3) {
                GenericaMensagem.warn("Atençao", "Não é possível atualizar/salvar quando o status esta como desistente!");
                return;
            }

            if (!dao.update(servicoPessoa)) {
                GenericaMensagem.error("Atençao", "Erro ao Alterar Serviço Pessoa!");
                dao.rollback();
                return;
            }

            if (dao.update(matriculaEscola)) {
                if (tipoMatricula.equals("Turma")) {
                    setDesabilitaIndividual(true);
                    matriculaTurma.setMatriculaEscola(matriculaEscola);
                    if (!dao.update(matriculaTurma)) {
                        dao.rollback();
                        GenericaMensagem.error("Atençao", "Falha ao atualizar a matricula turma!");
                        return;
                    }
                } else {
                    setDesabilitaTurma(true);
                    matriculaIndividual.setMatriculaEscola(matriculaEscola);
                    if (!dao.update(matriculaIndividual)) {
                        dao.rollback();
                        GenericaMensagem.error("Atençao", "Falha ao atualizar a matricula individual!");
                        return;
                    }
                }
                dao.commit();
                //pessoaResponsavelMemoria = servicoPessoa.getCobranca();
                //pessoaAlunoMemoria = servicoPessoa.getPessoa();
                GenericaMensagem.info("Sucesso", "Matrícula atualizada com sucesso!");

                target = "_blank";
                dao.close();
            } else {
                dao.rollback();
            }
        }
    }

    public void carregaEntrada() {
        listaMesVencimento.clear();
        dataEntrada = DataHoje.dataHoje();
        getListaMesVencimento();
        valorTaxa = valorParcelaVencimento;
//        if (aluno.getPessoa().getSocios().getId() != -1) {
//            valorTaxa = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), idServico, DataHoje.dataHoje(), 0, aluno.getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
//        } else {
//            valorTaxa = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), idServico, DataHoje.dataHoje(), 0, null));
//        }
    }

    public String editar(int id) {
        int i = id;
        return null;
    }

    public String editar(MatriculaEscola me) {
        idStatusFiltro = 5;
        escolaAutorizadosDetalhes = new EscolaAutorizados();
        getListaGridMEscola().clear();
        matriculaEscola = me;
        servicoPessoa = matriculaEscola.getServicoPessoa();
        MatriculaEscolaDao med = new MatriculaEscolaDao();
        desabilitaCampo = true;
//        idDiaVencimentoPessoa = 0;

        if (matriculaEscola.getServicoPessoa().getEvt() != null) {
            desabilitaCamposMovimento = true;
            desabilitaDiaVencimento = true;
        }
        for (int i = 0; i < listaVendedor.size(); i++) {
            if (matriculaEscola.getVendedor().getId() == Integer.parseInt(listaVendedor.get(i).getDescription())) {
                idVendedor = i;
                break;
            }
        }
        for (int i = 0; i < listaMidia.size(); i++) {
            if (matriculaEscola.getMidia().getId() == Integer.parseInt(listaMidia.get(i).getDescription())) {
                idMidia = i;
                break;
            }
        }
        for (int i = 0; i < listaStatus.size(); i++) {
            if (matriculaEscola.getEscStatus().getId() == Integer.parseInt(listaStatus.get(i).getDescription())) {
                idStatus = i;
                break;
            }
        }
        for (int i = 0; i < listFiliais.size(); i++) {
            if (matriculaEscola.getFilial().getId() == Integer.parseInt(listFiliais.get(i).getDescription())) {
                filial_id = i;
                break;
            }
        }
//        setValorString(matriculaEscola.getValorTotalString());
        matriculaIndividual = med.pesquisaCodigoMIndividual(matriculaEscola.getId());
        matriculaTurma = med.pesquisaCodigoMTurma(matriculaEscola.getId());

        if (matriculaIndividual.getId() != -1) {
            //tipoMatricula = "Individual";
            desabilitaTurma = true;
            desabilitaIndividual = false;
            if (matriculaIndividual.getProfessor() != null) {
                for (int i = 0; i < listaProfessor.size(); i++) {
                    if (matriculaIndividual.getProfessor().getId() == Integer.parseInt(listaProfessor.get(i).getDescription())) {
                        idProfessor = i;
                        break;
                    }
                }
            }
            getListaIndividual();
            for (int i = 0; i < listaIndividual.size(); i++) {
                if (matriculaIndividual.getCurso().getId() == Integer.parseInt(listaIndividual.get(i).getDescription())) {
                    idIndividual = i;
                    break;
                }
            }
        } else if (matriculaTurma.getId() != -1) {
            //tipoMatricula = "Turma";
            turma = matriculaTurma.getTurma();
            for (int i = 0; i < listaTurma.size(); i++) {
                if (Objects.equals(matriculaTurma.getTurma().getId(), turma.getId())) {
                    idTurma = i;
                    break;
                }
            }
            desabilitaTurma = false;
            desabilitaIndividual = true;
        } else {
            return null;
        }
        idFTipoDocumento = servicoPessoa.getTipoDocumento().getId();
        FisicaDao fisicaDB = new FisicaDao();
        aluno = fisicaDB.pesquisaFisicaPorPessoa(servicoPessoa.getPessoa().getId());
        if (aluno.getId() != -1) {
            getResponsavel();
            verificaSocio();
        }

        pegarIdServico();
        //atualizaValor();
        calculaValorLiquido();

        loadListParceiro();

        idParceiro = matriculaEscola.getServicoPessoa().getParceiro() != null ? matriculaEscola.getServicoPessoa().getParceiro().getId() : -1;

        listaMesVencimento.clear();

        //pessoaResponsavelMemoria = servicoPessoa.getCobranca();
        //pessoaAlunoMemoria = servicoPessoa.getPessoa();
//        analisaResponsavel();
        String urlRetorno = "matriculaEscola";
        GenericaSessao.put("linkClicado", true);
        if (GenericaSessao.exists("urlRetorno")) {
            if (!GenericaSessao.getString("urlRetorno").equals("matriculaEscola")) {
                urlRetorno = GenericaSessao.getString("urlRetorno");
                GenericaSessao.put("matriculaEscolaPesquisa", matriculaEscola);
                if (tipoMatricula.equals("Individual")) {
                    GenericaSessao.put("matriculaIndividualPesquisa", matriculaIndividual);
                } else {
                    GenericaSessao.put("matriculaTurmaPesquisa", matriculaTurma);
                }
            }
        }
        return urlRetorno;
    }

    public void excluir() {
        if (matriculaEscola.getId() != -1) {
            if (matriculaEscola.getEscStatus().getId() == 2 || matriculaEscola.getEscStatus().getId() == 3 || matriculaEscola.getEscStatus().getId() == 4) {
                GenericaMensagem.warn("Atençao", "Não é possível excluir a matrícula quando o status esta como desistente!");
                return;
            }

            MatriculaEscolaDao med = new MatriculaEscolaDao();
            String resultado = med.desfazerMovimento(matriculaEscola);

            if (resultado != null) {
                GenericaMensagem.warn("Atenção", resultado);
                return;
            }

            Dao dao = new Dao();
            dao.openTransaction();

            if (tipoMatricula.equals("Individual")) {
                if (!dao.delete((MatriculaIndividual) dao.find(matriculaIndividual))) {
                    dao.rollback();
                    GenericaMensagem.warn("Atenção", "Falha ao excluir essa matrícula!");
                    return;
                }
            } else if (!dao.delete((MatriculaTurma) dao.find(matriculaTurma))) {
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Falha ao excluir essa matrícula!");
                return;
            }

            if (!getListaEscolaAutorizadas().isEmpty()) {
                for (EscolaAutorizados ea : listaEscolaAutorizadas) {
                    if (!dao.delete((EscolaAutorizados) dao.find(ea))) {
                        GenericaMensagem.warn("Atençao", "Falha ao as pessoas autorizadas!");
                        return;
                    }
                }
            }

            matriculaEscola = (MatriculaEscola) dao.find(matriculaEscola);
            servicoPessoa = (ServicoPessoa) dao.find(servicoPessoa);
            if (dao.delete(matriculaEscola) && dao.delete(servicoPessoa)) {
                dao.commit();
                novo();
                GenericaMensagem.info("Sucesso", "Matricula excluída com sucesso!");
            } else {
                dao.rollback();
                GenericaMensagem.warn("Atençao", "Falha ao excluir essa matrícula!");
            }

        }
    }

    public void calculaValorLiquido() {
        calculoValor();
        calculoDesconto();
    }

    public void atualizaValor() {
    }

    public String valorServico(MatriculaEscola me) {

        String valorx;
        if (me.getServicoPessoa().getServicos() != null) {
            if (me.getServicoPessoa().getNrDesconto() == 0) {
                if (me.getServicoPessoa().getPessoa().getSocios().getId() != -1) {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(me.getServicoPessoa().getPessoa().getId(), me.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje(), 0, me.getServicoPessoa().getPessoa().getSocios().getMatriculaSocios().getCategoria().getId()));
                } else {
                    valorx = Moeda.converteR$Double(new FunctionsDao().valorServico(me.getServicoPessoa().getPessoa().getId(), me.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje(), 0, null));
                }
            } else {
                double valorx_c = new FunctionsDao().valorServicoCheio(me.getServicoPessoa().getPessoa().getId(), me.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje());

                double calculo = Moeda.divisao(Moeda.multiplicar(me.getServicoPessoa().getNrDesconto(), valorx_c), 100);
                valorx = Moeda.converteR$Double(Moeda.subtracao(valorx_c, calculo));
            }

            return Moeda.converteR$(valorx);
        }

        return "0,00";
//        FunctionsDB functionsDB = new FunctionsDao();
//        if (me.getServicoP essoa().getPessoa().getSocios().getId() != -1) {
//            return Moeda.converteR$(Double.toString(functionsDB.valorServico(me.getServicoPessoa().getPessoa().getId(), me.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje(), 0, me.getServicoPessoa().getPessoa().getSocios().getMatriculaSocios().getCategoria().getId())));
//        } else {
//            return Moeda.converteR$(Double.toString(functionsDB.valorServico(me.getServicoPessoa().getPessoa().getId(), me.getServicoPessoa().getServicos().getId(), DataHoje.dataHoje(), 0, null)));
//        }
    }

    public void updateGridParceiro() {
        pegarIdServico();
        dse();
        calculoValor();
        calculoDesconto();
        updateData();
    }

    public void updateGrid() {
        pegarIdServico();
        loadListParceiro();
        dse();
        calculoValor();
        calculoDesconto();
        updateData();
    }

    public void updateData() {
        if (matriculaEscola.getId() == -1) {
            switch (tipoMatricula) {
                case "Individual":
                    if (DataHoje.menorData(servicoPessoa.getEmissao(), matriculaIndividual.getDataInicioString())) {
                        servicoPessoa.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(matriculaIndividual.getDataInicioString()));
                    } else {
                        servicoPessoa.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(servicoPessoa.getEmissao()));
                    }
                    servicoPessoa.setReferenciaValidade(DataHoje.converteDataParaReferencia(matriculaIndividual.getDataTerminoString()));
                    break;
                case "Turma":
                    if (DataHoje.menorData(servicoPessoa.getEmissao(), turma.getDataInicio())) {
                        servicoPessoa.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(turma.getDataInicio()));
                    } else {
                        servicoPessoa.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(servicoPessoa.getEmissao()));
                    }
                    servicoPessoa.setReferenciaValidade(DataHoje.converteDataParaReferencia(turma.getDataTermino()));
                    break;
            }
        }
    }

    public MatriculaIndividual getMatriculaIndividual() {
        return matriculaIndividual;
    }

    public void setMatriculaIndividual(MatriculaIndividual matriculaIndividual) {
        this.matriculaIndividual = matriculaIndividual;
    }

    public MatriculaTurma getMatriculaTurma() {
        return matriculaTurma;
    }

    public void setMatriculaTurma(MatriculaTurma matriculaTurma) {
        this.matriculaTurma = matriculaTurma;
    }

    public Fisica getAluno() {
        if (!GenericaSessao.exists("fisicaPesquisa") && !GenericaSessao.exists("pessoaPesquisa")) {
            return aluno;
        }

        if (!GenericaSessao.exists("pesquisaFisicaTipo")) {
            return aluno;
        }

        String tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo", false);
        if (tipoFisica.equals("aluno")) {
            aluno = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            verificaSocio();
            LancamentoIndividualDao dbl = new LancamentoIndividualDao();
            if (!dbl.listaSerasa(aluno.getPessoa().getId()).isEmpty()) {
                GenericaMensagem.warn("PESSOA", aluno.getPessoa().getNome() + " contém o nome no Serasa!");
            }
            if (socios != null && socios.getId() != -1) {
                // PESSOA ASSOCIADA
                servicoPessoa.setCobranca(retornaResponsavel(aluno.getPessoa().getId(), true));
            } else {
                // PESSOA NÁO ASSOCIADA
                servicoPessoa.setCobranca(retornaResponsavel(aluno.getPessoa().getId(), false));
            }

            servicoPessoa.setPessoa(aluno.getPessoa());
            GenericaSessao.remove("pesquisaFisicaTipo");
            loadListParceiro();
        } else if (tipoFisica.equals("responsavel")) {
            verificaSocio();
            if (socios != null && socios.getId() != -1) {
                // PESSOA ASSOCIADA
                servicoPessoa.setCobranca(retornaResponsavel(aluno.getPessoa().getId(), true));
            } else {
                // PESSOA NÁO ASSOCIADA
                servicoPessoa.setCobranca(retornaResponsavel(aluno.getPessoa().getId(), false));
            }
            GenericaSessao.remove("pessoaPesquisa");
            GenericaSessao.remove("pesquisaFisicaTipo");
        }
        pegarIdServico();
        calculaValorLiquido();
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

//    public Fisica getAlunoxx() {
//        if (GenericaSessao.exists("fisicaPesquisa")) {
//            MatriculaEscolaDao med = new MatriculaEscolaDao();
//            if (GenericaSessao.exists("pesquisaFisicaTipo")) {
//                String tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo");
//                if (tipoFisica.equals("aluno")) {
//                    GenericaSessao.remove("pesquisaFisicaTipo");
//                    valorTaxa = "";
//                    //taxa = false;
//                    aluno = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
////                    if (servicoPessoa.getPessoa().getId() == -1) {
////                        pessoaAlunoMemoria = aluno.getPessoa();
////                    } else {
////                        if (aluno.getPessoa().getId() != servicoPessoa.getPessoa().getId()) {
////                            pessoaAlunoMemoria = aluno.getPessoa();
////                        }
////                    }
//                    if (aluno.getId() != -1) {
//                        getResponsavel();
//                        verificaSocio();
//                    }
//                    if (responsavel.getId() != -1) {
//                        pessoaComplemento = new PessoaComplemento();
//                        pessoaComplemento = med.pesquisaDataRefPessoaComplemto(responsavel.getId());
////                        if (pessoaComplemento != null) {
////                            this.idDiaVencimentoPessoa = pessoaComplemento.getNrDiaVencimento();
////                        }
//                        //matriculaEscola.setResponsavel(responsavel);
//                        servicoPessoa.setCobranca(responsavel);
//                    } else {
//                        FunctionsDB functionsDB = new FunctionsDao();
//                        int idade = functionsDB.idade("dt_nascimento", "current_date", aluno.getPessoa().getId());
//                        if (idade < 18) {
//                            GenericaMensagem.warn("Validação", "Responsável deve ser MAIOR DE IDADE!");
//                        }
//                        if (!med.verificaPessoaEnderecoDocumento("fisica", aluno.getPessoa().getId())) {
//                            GenericaMensagem.warn("Validação", "Responsável deve conter um ENDEREÇO!");
//                        }
//                    }
//                    //matriculaEscola.setAluno(aluno.getPessoa());
//                    servicoPessoa.setPessoa(aluno.getPessoa());
//
//                    //matriculaEscola.setResponsavel(responsavel);
//                    servicoPessoa.setCobranca(responsavel);
//                    atualizaPessoaComplemento(0);
//                    pegarIdServico();
//                    //atualizaValor();
//                    calculaValorLiquido();
//                } else if (tipoFisica.equals("responsavel")) {
//                    GenericaSessao.remove("pesquisaFisicaTipo");
//                    Pessoa resp = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
//                    FunctionsDB functionsDB = new FunctionsDao();
//                    int idade = functionsDB.idade("dt_nascimento", "current_date", resp.getId());
//                    if (idade >= 18) {
//                        if (med.verificaPessoaEnderecoDocumento("fisica", resp.getId())) {
//                            //matriculaEscola.setResponsavel(resp);
//                            servicoPessoa.setCobranca(resp);
//                            atualizaPessoaComplemento(0);
//                        }
//                    } else {
//                        GenericaMensagem.warn("Validação", "Responsável deve ser maior de idade!");
//                    }
//                    GenericaSessao.remove("juridicaPesquisa");
//                }
//            }
////            analisaResponsavel();
//        }
//        return aluno;
//    }
    public void setAluno(Fisica aluno) {
        this.aluno = aluno;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public boolean verificaSeContribuinteInativo(Pessoa responsavelPessoa) {
        JuridicaDao juridicaDB = new JuridicaDao();
        if (juridicaDB.empresaInativa(responsavelPessoa, "FECHOU")) {
            msgStatusEmpresa = "Empresa inátiva!";
            return true;
        }
        return false;
    }

    public void verificaDebitosResponsavel(Pessoa responsavelPessoa) {
        msgStatusDebito = "";
        setOcultaBotaoSalvar(false);
        if (responsavelPessoa.getId() != -1) {
            MovimentoDao movimentoDB = new MovimentoDao();
            if (movimentoDB.existeDebitoPessoa(responsavelPessoa, null)) {
                msgStatusDebito = "Responsável possui débitos!";
                setOcultaBotaoSalvar(true);
            }
        }
    }

    public void descontoFolhaResponsavel() {
        getResponsavel();
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            List<EscStatus> list = (List<EscStatus>) new Dao().list(new EscStatus());
            for (int i = 0; i < list.size(); i++) {
                listaStatus.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
            }
        }
        return listaStatus;
    }

    public void setListaStatus(List<SelectItem> listaStatus) {
        this.listaStatus = listaStatus;
    }

    public List<SelectItem> getListaVendedor() {
        if (listaVendedor.isEmpty()) {
            List<Vendedor> list = (List<Vendedor>) new Dao().list(new Vendedor(), true);
            for (int i = 0; i < list.size(); i++) {
                listaVendedor.add(new SelectItem(i, list.get(i).getPessoa().getNome(), "" + list.get(i).getId()));
            }
        }
        return listaVendedor;
    }

    public void setListaVendedor(List<SelectItem> listaVendedor) {
        this.listaVendedor = listaVendedor;
    }

    public List<SelectItem> getListaProfessor() {
        if (listaProfessor.isEmpty()) {
            List<Professor> list = (List<Professor>) new Dao().list(new Professor(), true);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    listaProfessor.add(new SelectItem(i, list.get(i).getProfessor().getNome(), "" + list.get(i).getId()));
                }
            } else {
                listaProfessor.add(new SelectItem(0, "Nenhum Professor Cadastrado", "0"));
            }
        }
        return listaProfessor;
    }

    public void setListaProfessor(List<SelectItem> listaProfessor) {
        this.listaProfessor = listaProfessor;
    }

    public List<SelectItem> getListaMidia() {
        if (listaMidia.isEmpty()) {
            Dao dao = new Dao();
            List<Midia> list = (List<Midia>) new Dao().list(new Midia(), true);
            for (int i = 0; i < list.size(); i++) {
                listaMidia.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
            }
        }
        return listaMidia;
    }

    public void setListaMidia(List<SelectItem> listaMidia) {
        this.listaMidia = listaMidia;
    }

    public List<SelectItem> getListaNumeros() {
        if (listaNumeros.isEmpty()) {
            for (int i = 1; i <= 24; i++) {
                listaNumeros.add(new SelectItem(Integer.toString(i)));
            }
        }
        return listaNumeros;
    }

    public void setListaNumeros(List<SelectItem> listaNumeros) {
        this.listaNumeros = listaNumeros;
    }

    public List<SelectItem> getListaDataVencimento() {
        if (listaDataVencimento.isEmpty()) {
            if (servicoPessoa.getPessoa().getId() == -1) {
                for (int i = 1; i <= 31; i++) {
                    listaDataVencimento.add(new SelectItem(Integer.toString(i)));
                }
            }
        }

        return listaDataVencimento;
    }

    public void setListaDataVencimento(List<SelectItem> listaDataVencimento) {
        this.listaDataVencimento = listaDataVencimento;
    }

    public int getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(int idTurma) {
        this.idTurma = idTurma;
    }

    public int getIdIndividual() {
        return idIndividual;
    }

    public void setIdIndividual(int idIndividual) {
        this.idIndividual = idIndividual;
    }

    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public int getIdMidia() {
        return idMidia;
    }

    public void setIdMidia(int idMidia) {
        this.idMidia = idMidia;
    }

    public int getIdDiaVencimento() {
        return idDiaVencimento;
    }

    public void setIdDiaVencimento(int idDiaVencimento) {
        this.idDiaVencimento = idDiaVencimento;
    }
//
//    public int getDiaVencimento() {
//        return this.diaVencimento;
//    }
//
//    public void setDiaVencimento(int diaVencimento) {
//        this.diaVencimento = diaVencimento;
//    }

    public List<SelectItem> getListaIndividual() {
        if (listaIndividual.isEmpty()) {
            ServicosDao db = new ServicosDao();
            List list = db.pesquisaTodos(151);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    listaIndividual.add(new SelectItem((int) i,
                            (String) ((Servicos) list.get(i)).getDescricao(),
                            Integer.toString(((Servicos) list.get(i)).getId())));
                }
                if (matriculaEscola.getId() == -1) {
                    // TRAZ O SERVIÇO MAIS USADO
                    Integer mainId = new MatriculaEscolaDao().findMatriculaIndivualMainId();
                    if (mainId != null) {
                        if (!listaIndividual.isEmpty()) {
                            for (int i = 0; i < listaIndividual.size(); i++) {
                                if (Integer.parseInt(listaIndividual.get(i).getDescription()) == mainId) {
                                    idIndividual = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                listaIndividual.add(new SelectItem(0,
                        "Sem Serviço para esta rotina",
                        "0")
                );
            }
        }
        return listaIndividual;
    }

    public void setListaIndividual(List<SelectItem> listaIndividual) {
        this.listaIndividual = listaIndividual;
    }

    public String getValorParcelaVencimento() {
        return Moeda.converteR$(valorParcelaVencimento);
    }

    public void setValorParcelaVencimento(String valorParcelaVencimento) {
        this.valorParcelaVencimento = Moeda.substituiVirgula(valorParcelaVencimento);
    }

    public String getValorParcelaVencimentoString() {
        return Moeda.converteR$(valorParcelaVencimento);
    }

    public void setValorParcelaVencimentoString(String valorParcelaVencimento) {
        this.valorParcelaVencimento = Moeda.substituiVirgula(valorParcelaVencimento);
    }

    public String getValorParcela() {
        return Moeda.substituiVirgula(valorParcela);
    }

    public void setValorParcela(String valorParcela) {
        this.valorParcela = Moeda.substituiVirgula(valorParcela);
    }

    public String getValorLiquido() {
        return Moeda.substituiVirgula(valorLiquido);
    }

    public void setValorLiquido(String valorLiquido) {
        this.valorLiquido = Moeda.substituiVirgula(valorLiquido);
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$(valor);
    }

    public void setValorString(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public void validaHoraInicio() {
        matriculaIndividual.setInicio(DataHoje.validaHora(matriculaIndividual.getInicio()));
    }

    public void validaHoraFinal() {
        matriculaIndividual.setTermino(DataHoje.validaHora(matriculaIndividual.getTermino()));
    }

    public boolean isDesabilitaTurma() {
        return desabilitaTurma;
    }

    public void setDesabilitaTurma(boolean desabilitaTurma) {
        this.desabilitaTurma = desabilitaTurma;
    }

    public boolean isDesabilitaIndividual() {
        return desabilitaIndividual;
    }

    public void setDesabilitaIndividual(boolean desabilitaIndividual) {
        this.desabilitaIndividual = desabilitaIndividual;
    }

    public String getValorParcelaString() {
        return Moeda.converteR$(valorParcela);
    }

    public void setValorParcelaString(String valorParcela) {
        this.valorParcela = Moeda.substituiVirgula(valorParcela);
    }

    public void acaoPesquisaInicial() {
        setComoPesquisa("Inicial");
        listaMatriculaEscolas.clear();
        loadList();
    }

    public void acaoPesquisaParcial() {
        setComoPesquisa("Parcial");
        listaMatriculaEscolas.clear();
        loadList();
    }

    public void loadList() {
        if (!descricaoCurso.isEmpty() || !descricao.isEmpty()) {
            if (listaMatriculaEscolas.isEmpty()) {
                MatriculaEscolaDao dB = new MatriculaEscolaDao();
                int idStatusI = idStatusFiltro;
                if (idStatusI != 5) {
                    idStatusI = Integer.parseInt(listaStatus.get(idStatusFiltro).getDescription());
                }
                List<MatriculaEscola> list = dB.pesquisaMatriculaEscola(tipoMatricula, descricaoCurso, descricao, comoPesquisa, porPesquisa, idStatusI, (Filial) new Dao().find(new Filial(), Integer.parseInt(getListFiliais().get(filial_id).getDescription())));
                MatriculaIndividual mIndividual;
                MatriculaTurma mTurma;
                for (MatriculaEscola listaMatriculaEscola : list) {
                    if (tipoMatricula.equals("Individual")) {
                        mIndividual = (MatriculaIndividual) dB.pesquisaCodigoMIndividual(listaMatriculaEscola.getId());
                        listaMatriculaEscolas.add(new ListaMatriculaEscola(listaMatriculaEscola, mIndividual, null, mIndividual.getCurso().getDescricao(), mIndividual.getDataInicioString() + " - " + mIndividual.getDataTerminoString()));
                    } else {
                        mTurma = dB.pesquisaCodigoMTurma(listaMatriculaEscola.getId());
                        listaMatriculaEscolas.add(new ListaMatriculaEscola(listaMatriculaEscola, null, mTurma, mTurma.getTurma().getCursos().getDescricao(), mTurma.getTurma().getDataInicio() + " - " + mTurma.getTurma().getDataTermino()));
                    }
                }
            }
        }
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoCurso() {
        return descricaoCurso;
    }

    public void setDescricaoCurso(String descricaoCurso) {
        this.descricaoCurso = descricaoCurso;
    }

    public List<ListaMatriculaEscola> getListaMatriculaEscolas() {
        return listaMatriculaEscolas;
    }

    public void setListaMatriculaEscolas(List<ListaMatriculaEscola> listaMatriculaEscolas) {
        this.listaMatriculaEscolas = listaMatriculaEscolas;
    }

    public List getListaGridMEscola() {
        return listaGridMEscola;
    }

    public void setListaGridMEscola(List listaGridMEscola) {
        this.listaGridMEscola = listaGridMEscola;
    }

    public MatriculaEscola getMatriculaEscola() {
        getAluno();
        if (socios.getId() == -1) {
            getJuridica();
        }
        if (matriculaEscola.getFilial().getId() == -1) {
            getMacFilial();
            // matriculaEscola.setFilial(macFilial.getFilial());
        }
        if (target.equals("#")) {
            if (matriculaEscola.getServicoPessoa().getEvt() != null) {
                desabilitaGeracaoContrato = true;
                target = "_blank";
            } else {
                desabilitaGeracaoContrato = false;
            }
        }
        return matriculaEscola;
    }

    public void setMatriculaEscola(MatriculaEscola matriculaEscola) {
        this.matriculaEscola = matriculaEscola;
    }

    public MatriculaContrato getContratoEscola() {
        return matriculaContrato;
    }

    public void setContratoEscola(MatriculaContrato matriculaContrato) {
        this.matriculaContrato = matriculaContrato;
    }

    public String getOpenModal() {
        return openModal;
    }

    public void setOpenModal(String openModal) {
        this.openModal = openModal;
    }
//
//    public boolean getTaxa() {
//        return taxa;
//    }
//
//    public void setTaxa(boolean taxa) {
//        this.taxa = taxa;
//    }

    public void pegarIdServico() {
        if (tipoMatricula.equals("Individual")) {
            if (listaIndividual.isEmpty()) {
                getListaIndividual();
            }
            //if (turma.getId() != -1) {
            //idServico = turma.getCursos().getId();
            idServico = Integer.valueOf(listaIndividual.get(idIndividual).getDescription());
            //}
        } else {
            idServico = 0;
            if (turma.getId() != -1) {
                idServico = turma.getCursos().getId();
            }
        }
    }

    public MacFilial getMacFilial() {
        macFilial = MacFilial.getAcessoFilial();
        msgStatusFilial = "";
        if (macFilial.getId() == -1) {
            msgStatusFilial = "Informar filial";
        }
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public String getMsgStatusFilial() {
        if (msgStatusFilial.equals("")) {
            getFilial();
        }
        return msgStatusFilial;
    }

    public void setMsgStatusFilial(String msgStatusFilial) {
        this.msgStatusFilial = msgStatusFilial;
    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

//    public int getIdDiaVencimentoPessoa() {
//        return idDiaVencimentoPessoa;
//    }
//
//    public void setIdDiaVencimentoPessoa(int idDiaVencimentoPessoa) {
//        this.idDiaVencimentoPessoa = idDiaVencimentoPessoa;
//    }
    public boolean isDesabilitaCampo() {
        return desabilitaCampo;
    }

    public void setDesabilitaCampo(boolean desabilitaCampo) {
        this.desabilitaCampo = desabilitaCampo;
    }

    public void verificaSocio() {
        socios = aluno.getPessoa().getSocios(); // dB.pesquisaSocioPorPessoa(aluno.getPessoa().getId());
    }

    public void pesquisaFisica(String tipo) {
        if (GenericaSessao.exists("pesquisaFisicaTipo")) {
            GenericaSessao.remove("pesquisaFisicaTipo");
        }
        GenericaSessao.put("pesquisaFisicaTipo", tipo);
    }

//    public void cobrarTaxa() {
//        if (taxa == true) {
//            this.taxa = true;
//            this.valorTaxa = Moeda.converteR$Double(vTaxa);
//            //this.valorTaxa = Moeda.converteR$Double(new FunctionsDao().valorServico(aluno.getPessoa().getId(), idServico, DataHoje.dataHoje(), 2, null));
//        } else {
//            this.taxa = false;
//            this.valorTaxa = "";
//        }
//        idDataTaxa = 0;
//        idMesVencimento = 0;
//        listaMesVencimento.clear();
//        getListaMesVencimento();
//    }
    public String getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(String valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public String getValorTaxaString() {
        return Moeda.converteR$(valorTaxa);
    }

    public void setValorTaxaString(String valorTaxa) {
        this.valorTaxa = Moeda.converteR$(valorTaxa);
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public void setHabilitaGerarParcelas(boolean habilitaGerarParcelas) {
        this.habilitaGerarParcelas = habilitaGerarParcelas;
    }

    public boolean isDesabilitaCamposMovimento() {
        return desabilitaCamposMovimento;
    }

    public void setDesabilitaCamposMovimento(boolean desabilitaCamposMovimento) {
        this.desabilitaCamposMovimento = desabilitaCamposMovimento;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isDesabilitaGeracaoContrato() {
        return desabilitaGeracaoContrato;
    }

    public void setDesabilitaGeracaoContrato(boolean desabilitaGeracaoContrato) {
        this.desabilitaGeracaoContrato = desabilitaGeracaoContrato;
    }

    public String getTipoMatricula() {
        return tipoMatricula;
    }

    public void setTipoMatricula(String tipoMatricula) {
        this.tipoMatricula = tipoMatricula;
    }

    public void tipoMatriculaPor(TabChangeEvent event) {
        tipoMatricula = event.getTab().getTitle();
        updateGrid();
    }

    public boolean isDesabilitaDescontoFolha() {
        return desabilitaDescontoFolha;
    }

    public void setDesabilitaDescontoFolha(boolean desabilitaDescontoFolha) {
        this.desabilitaDescontoFolha = desabilitaDescontoFolha;
    }

    public List<FTipoDocumento> getListaFTipoDocumento() {
        if (listaFTipoDocumento.isEmpty()) {
            Dao dao = new Dao();
            listaFTipoDocumento = (List<FTipoDocumento>) dao.find("FTipoDocumento", new int[]{13, 2});
        }
        return listaFTipoDocumento;
    }

    public void setListaFTipoDocumento(List<FTipoDocumento> listaFTipoDocumento) {
        this.listaFTipoDocumento = listaFTipoDocumento;
    }

    public int getIdFTipoDocumento() {
        return idFTipoDocumento;
    }

    public void setIdFTipoDocumento(int idFTipoDocumento) {
        this.idFTipoDocumento = idFTipoDocumento;
    }

    public boolean isDesabilitaDiaVencimento() {
        return desabilitaDiaVencimento;
    }

    public void setDesabilitaDiaVencimento(boolean desabilitaDiaVencimento) {
        this.desabilitaDiaVencimento = desabilitaDiaVencimento;
    }

    public void bloqueiaComboDiaVencimento() {
        if (idFTipoDocumento == 2) {
            desabilitaDiaVencimento = true;
        } else if (idFTipoDocumento == 13) {
            desabilitaDiaVencimento = false;
        }
    }

    public boolean isOcultaDescontoFolha() {
        return ocultaDescontoFolha;
    }

    public void setOcultaDescontoFolha(boolean ocultaDescontoFolha) {
        this.ocultaDescontoFolha = ocultaDescontoFolha;
    }

    public Juridica getJuridica() {
//        if (GenericaSessao.exists("juridicaPesquisa")) {
//            juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
//            MatriculaEscolaDao med = new MatriculaEscolaDao();
//            if (med.verificaPessoaEnderecoDocumento("juridica", juridica.getPessoa().getId())) {
//                responsavel = juridica.getPessoa();
//                if (responsavel.getId() != -1) {
//                    atualizaPessoaComplemento(1);
//                    pessoaComplemento = new PessoaComplemento();
//                    pessoaComplemento = med.pesquisaDataRefPessoaComplemto(responsavel.getId());
////                    if (pessoaComplemento != null) {
////                        this.idDiaVencimentoPessoa = pessoaComplemento.getNrDiaVencimento();
////                    }
//                    //matriculaEscola.setResponsavel(juridica.getPessoa());
//                    servicoPessoa.setCobranca(juridica.getPessoa());
//                }
//                setOcultaDescontoFolha(false);
//                desabilitaDescontoFolha = false;
//                pegarIdServico();
//                //atualizaValor();
//                calculaValorLiquido();
//            }
//            juridica = new Juridica();
////            analisaResponsavel();
//        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    /**
     * Tipo : 0 => Aluno / 1 => Responsável
     *
     * @param tipo
     */
    public void atualizaPessoaComplemento(int tipo) {
        PessoaDao pdb = new PessoaDao();
        PessoaComplemento pc;
        Pessoa p;
        if (tipo == 0) {
            p = servicoPessoa.getCobranca();
        } else {
            p = responsavel;
        }
        pc = pdb.pesquisaPessoaComplementoPorPessoa(p.getId());
        pessoaComplemento = pc;
        if (pc.getId() == -1) {
            pc.setNrDiaVencimento(getRegistro().getFinDiaVencimentoCobranca());
            pc.setCobrancaBancaria(true);
            pc.setPessoa(p);
            new Dao().save(pc, true);
        }
        loadDiaVencimento();
    }

    public boolean isOcultaBotaoSalvar() {
        return ocultaBotaoSalvar;
    }

    public void setOcultaBotaoSalvar(boolean ocultaBotaoSalvar) {
        this.ocultaBotaoSalvar = ocultaBotaoSalvar;
    }

    public String getMsgStatusDebito() {
        return msgStatusDebito;
    }

    public void setMsgStatusDebito(String msgStatusDebito) {
        this.msgStatusDebito = msgStatusDebito;
    }

    public Juridica getEmpresa() {
        FunctionsDao functionsDB = new FunctionsDao();
        int titular = functionsDB.responsavel(servicoPessoa.getCobranca().getId(), true);
        if (titular > 0) {
            JuridicaDao juridicaDB = new JuridicaDao();
            Juridica juridicaB = juridicaDB.pesquisaJuridicaPorPessoa(titular);
            if (juridicaB != null) {
                empresa = juridicaB;
            }

        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public String getMsgStatusEmpresa() {
        return msgStatusEmpresa;
    }

    public void setMsgStatusEmpresa(String msgStatusEmpresa) {
        this.msgStatusEmpresa = msgStatusEmpresa;
    }

    public int getVagasDisponiveis() {
        if (turma.getId() != -1) {
            FunctionsDao functionsDB = new FunctionsDao();
            setVagasDisponiveis(functionsDB.vagasEscolaTurma(turma.getId()));
        }
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(int vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

//    public void analisaResponsavel() {
//        if (servicoPessoa.getCobranca().getId() != -1) {
//            if (responsavel.getId() != servicoPessoa.getCobranca().getId()) {
//                pessoaResponsavelMemoria = responsavel;
//            }
//            if (servicoPessoa.getCobranca().getTipoDocumento().getId() == 2) {
//                desabilitaDescontoFolha = false;
//                ocultaDescontoFolha = false;
//                verificaSeContribuinteInativo(servicoPessoa.getCobranca());
//            } else {
//                desabilitaDescontoFolha = true;
//                ocultaDescontoFolha = true;
//            }
//            //verificaDebitosResponsavel(servicoPessoa.getCobranca());
//        } else {
//            pessoaResponsavelMemoria = responsavel;
//        }
//    }
    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public int getIdadeAluno() {
        if (servicoPessoa.getPessoa().getId() != -1) {
            FunctionsDao fdb = new FunctionsDao();
            idadeAluno = fdb.idade("dt_nascimento", "current_date", servicoPessoa.getPessoa().getId());
        }
        return idadeAluno;
    }

    public void setIdadeAluno(int idadeAluno) {
        this.idadeAluno = idadeAluno;
    }

    public List<SelectItem> getListaCursosDisponiveis() {
        if (!getListFiliais().isEmpty()) {
            if (listaCursosDisponiveis.isEmpty()) {
                TurmaDao td = new TurmaDao();
                List<Turma> list = (List<Turma>) td.listaTurmaAtivaPorFilial(Integer.parseInt(getListFiliais().get(filial_id).getDescription()));
                int idCurso = 0;
                int j = 0;
                for (Turma list1 : list) {
                    if (idCurso != list1.getCursos().getId()) {
                        idCurso = list1.getCursos().getId();
                        listaCursosDisponiveis.add(new SelectItem((int) j, (String) list1.getCursos().getDescricao(), Integer.toString(list1.getCursos().getId())));
                        j++;
                    }
                }
                if (matriculaEscola.getId() == -1) {
                    // TRAZ O SERVIÇO MAIS USADO
                    Integer mainId = new MatriculaEscolaDao().findMatriculaTurmaMainId();
                    if (mainId != null) {
                        if (!listaCursosDisponiveis.isEmpty()) {
                            for (int i = 0; i < listaCursosDisponiveis.size(); i++) {
                                if (Integer.parseInt(listaCursosDisponiveis.get(i).getDescription()) == mainId) {
                                    idCursosDisponiveis = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return listaCursosDisponiveis;
    }

    public void setListaCursosDisponiveis(List<SelectItem> listaCursosDisponiveis) {
        this.listaCursosDisponiveis = listaCursosDisponiveis;
    }

    public Integer getIdCursosDisponiveis() {
        return idCursosDisponiveis;
    }

    public void setIdCursosDisponiveis(Integer idCursosDisponiveis) {
        this.idCursosDisponiveis = idCursosDisponiveis;
    }

    public List<Turma> getListaTurma() {
        if (listaTurma.isEmpty()) {
            if (!listaCursosDisponiveis.isEmpty()) {
                TurmaDao td = new TurmaDao();
                if (!listaCursosDisponiveis.isEmpty() && idCursosDisponiveis != null) {
                    listaTurma = td.listaTurmaAtivaPorFilialServico(Integer.parseInt(getListFiliais().get(filial_id).getDescription()), Integer.parseInt(listaCursosDisponiveis.get(idCursosDisponiveis).getDescription()));
                }
            }
        }
        return listaTurma;
    }

    public void setListaTurma(List<Turma> listaTurma) {
        this.listaTurma = listaTurma;
    }

    public void selecionaTurma(Turma t) {
        turma = t;
        updateGrid();
    }

//    public static String UTF8toISO(String str) {
//        Charset utf8charset = Charset.forName("UTF-8");
//        Charset iso88591charset = Charset.forName("ISO-8859-1");
//        ByteBuffer inputBuffer = ByteBuffer.wrap(str.getBytes());
//        // decode UTF-8
//        CharBuffer data = utf8charset.decode(inputBuffer);
//        // encode ISO-8559-1
//        ByteBuffer outputBuffer = iso88591charset.encode(data);
//        byte[] outputData = outputBuffer.array();
//        return new String(outputData);
//    }
    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void openDialog() {
        visibility = true;
    }

    public void close() {
        visibility = false;
        PF.closeDialog("dgl_adicionar");
    }

    // PESSOAS AUTORIZADAS
    public void novaPessoaAutorizada() {
        escolaAutorizados = new EscolaAutorizados();
        listaEscolaAutorizadas.clear();
    }

    public void salvarPessoaAutorizada() {
        if (matriculaEscola.getId() != -1) {
            if (escolaAutorizados.getPessoa().getId() == -1) {
                GenericaMensagem.warn("Validação", "Pesquisar uma pessoa!");
                return;
            }
            for (EscolaAutorizados listaEscolaAutorizada : listaEscolaAutorizadas) {
                if (listaEscolaAutorizada.getPessoa().getId() == escolaAutorizados.getPessoa().getId()) {
                    GenericaMensagem.warn("Validação", "Pessoa já cadastrada!");
                    return;
                }
            }
            escolaAutorizados.setMatriculaEscola(matriculaEscola);
            if (escolaAutorizados.getId() == -1) {
                if (new Dao().save(escolaAutorizados, true)) {
                    escolaAutorizados = new EscolaAutorizados();
                    listaEscolaAutorizadas.clear();
                    GenericaMensagem.info("Sucesso", "Pessoa adicionada com sucesso");
                } else {
                    GenericaMensagem.warn("Erro", "Erro ao adicionar pessoa!");
                }
            }
        }
    }

    public void detalhesAutorizados(EscolaAutorizados ea) {
        escolaAutorizadosDetalhes = ea;
    }

    public void excluirPessoaAutorizada(EscolaAutorizados ea) {
        if (new Dao().delete(ea, true)) {
            escolaAutorizados = new EscolaAutorizados();
            listaEscolaAutorizadas.clear();
            GenericaMensagem.info("Sucesso", "Pessoa excluída com sucesso");
        } else {
            GenericaMensagem.warn("Erro", "Erro ao excluir pessoa!");
        }
        PF.update("i_msg_autorizados");
    }

    public EscolaAutorizados getEscolaAutorizados() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            if (GenericaSessao.exists("pesquisaFisicaTipo")) {
                String tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo");
                if (tipoFisica.equals("autorizada")) {
                    GenericaSessao.remove("pesquisaFisicaTipo");
                    escolaAutorizados.setPessoa(((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa());
                }
            }
        }
        return escolaAutorizados;
    }

    public void setEscolaAutorizados(EscolaAutorizados escolaAutorizados) {
        this.escolaAutorizados = escolaAutorizados;
    }

    public List<EscolaAutorizados> getListaEscolaAutorizadas() {
        if (matriculaEscola.getId() != -1) {
            if (listaEscolaAutorizadas.isEmpty()) {
                MatriculaEscolaDao medb = new MatriculaEscolaDao();
                listaEscolaAutorizadas = medb.listaPessoasAutorizas(matriculaEscola.getId());
            }
        }
        return listaEscolaAutorizadas;
    }

    public void setListaEscolaAutorizadas(List<EscolaAutorizados> listaEscolaAutorizadas) {
        this.listaEscolaAutorizadas = listaEscolaAutorizadas;
    }

    public EscolaAutorizados getEscolaAutorizadosDetalhes() {
        return escolaAutorizadosDetalhes;
    }

    public void setEscolaAutorizadosDetalhes(EscolaAutorizados escolaAutorizadosDetalhes) {
        this.escolaAutorizadosDetalhes = escolaAutorizadosDetalhes;
    }

    public boolean isAlterarPessoaComplemento() {
        if (matriculaEscola.getId() != -1) {
            if (servicoPessoa.getCobranca().getId() != -1) {
                PessoaDao pessoaDB = new PessoaDao();
                PessoaComplemento pc = pessoaDB.pesquisaPessoaComplementoPorPessoa(servicoPessoa.getCobranca().getId());
                alterarPessoaComplemento = pc.getId() != -1;
            }
        }
        return alterarPessoaComplemento;
    }

    public void setAlterarPessoaComplemento(boolean alterarPessoaComplemento) {
        this.alterarPessoaComplemento = alterarPessoaComplemento;
    }

    public void updatePessoaComplemento() {
        if (servicoPessoa.getCobranca().getId() != -1) {
            PessoaDao pessoaDB = new PessoaDao();
            PessoaComplemento pc = pessoaDB.pesquisaPessoaComplementoPorPessoa(servicoPessoa.getCobranca().getId());
            Dao dao = new Dao();
            pc.setNrDiaVencimento(idDiaVencimento);
            dao.openTransaction();
            if (dao.update(pc)) {
                //matriculaEscola.setDiaVencimento(pc.getNrDiaVencimento());
                servicoPessoa.setNrDiaVencimento(pc.getNrDiaVencimento());
                if (dao.update(matriculaEscola)) {
                    dao.commit();
                    listaMatriculaEscolas.clear();
                } else {
                    dao.rollback();
                }
            } else {
                dao.rollback();
            }
        }
    }

    public Registro getRegistro() {
        if (registro.getId() == -1) {
            registro = (Registro) new Dao().find(new Registro(), 1);
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public int getIdDataTaxa() {
        return idDataTaxa;
    }

    public void setIdDataTaxa(int idDataTaxa) {
        this.idDataTaxa = idDataTaxa;
    }

    public int getIdMesVencimento() {
        return idMesVencimento;
    }

    public void setIdMesVencimento(int idMesVencimento) {
        this.idMesVencimento = idMesVencimento;
    }

    public List<SelectItem> getListaMesVencimento() {
        if (listaMesVencimento.isEmpty()) {
            boolean isTaxa = false;
            DataHoje dh = new DataHoje();
            //String data = matriculaEscola.getDataMatriculaString();
            String data = servicoPessoa.getEmissao();
            String mesAno;
            int iDtMr;
            int iDtVct;
            for (int i = 0; i < getListaNumeros().size(); i++) {
                if (i > 0) {
                    data = dh.incrementarMeses(1, data);
                }
                if (!isTaxa) {
                    //iDtMr = DataHoje.converteDataParaInteger(matriculaEscola.getDataMatriculaString());
                    iDtMr = DataHoje.converteDataParaInteger(servicoPessoa.getEmissao());
                    iDtVct = DataHoje.converteDataParaInteger(data);
//                    if (taxa) {
                    if (iDtVct > iDtMr) {
                        idMesVencimento = i;
                        isTaxa = true;
                    } else {
                        idMesVencimento = 0;
                    }
//                    } else {
//                        isTaxa = true;
//                        idMesVencimento = 0;
//                    }
                }
                mesAno = data.substring(3, 5) + "/" + data.substring(6, 10);
                listaMesVencimento.add(new SelectItem(i, mesAno, mesAno));
            }
        }
        return listaMesVencimento;
    }

    public void setListaMesVencimento(List<SelectItem> listaMesVencimento) {
        this.listaMesVencimento = listaMesVencimento;
    }

//    public List<Movimento> getListaOutrosMovimentos() {
//        return listaOutrosMovimentos;
//    }
//
//    public void setListaOutrosMovimentos(List<Movimento> listaOutrosMovimentos) {
//        this.listaOutrosMovimentos = listaOutrosMovimentos;
//    }
    public int getIdStatusFiltro() {
        return idStatusFiltro;
    }

    public void setIdStatusFiltro(int idStatusFiltro) {
        this.idStatusFiltro = idStatusFiltro;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

//    public int getIdDiaParcela() {
//        return idDiaParcela;
//    }
//
//    public void setIdDiaParcela(int idDiaParcela) {
//        this.idDiaParcela = idDiaParcela;
//    }
//
//    public List<SelectItem> getListaDiaParcela() {
//        if (listaDiaParcela.isEmpty()) {
//            //int dia = DataHoje.DataToArrayInt(matriculaEscola.getDataMatricula())[0];
//            int dia = DataHoje.DataToArrayInt(servicoPessoa.getEmissao())[0];
//            for (int i = 1; i <= 31; i++) {
//                listaDiaParcela.add(new SelectItem(Integer.toString(i)));
//                if (dia == i) {
//                    idDiaParcela = i;
//                }
//            }
//
//        }
//        return listaDiaParcela;
//    }
//
//    public void setListaDiaParcela(List<SelectItem> listaDiaParcela) {
//        this.listaDiaParcela = listaDiaParcela;
//    }
    public ServicoPessoa getServicoPessoa() {
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

    public int getNumeroParcelas() {
        if ((servicoPessoa.getReferenciaVigoracao() != null && servicoPessoa.getReferenciaValidade() != null)
                && (!servicoPessoa.getReferenciaVigoracao().isEmpty() && !servicoPessoa.getReferenciaValidade().isEmpty())
                && DataHoje.menorData("01/" + servicoPessoa.getReferenciaVigoracao(), "01/" + servicoPessoa.getReferenciaValidade()) || DataHoje.igualdadeData("01/" + servicoPessoa.getReferenciaVigoracao(), "01/" + servicoPessoa.getReferenciaValidade())) {
            String dt = "01/" + servicoPessoa.getReferenciaVigoracao();
            // numeroParcelas = DataHoje.quantidadeMeses(DataHoje.converte(dt), DataHoje.converte("01/" + servicoPessoa.getReferenciaValidade())) + 1;
            numeroParcelas = DataHoje.diffMonths(dt, "01/" + servicoPessoa.getReferenciaValidade()) + 1;
            valorTotal = Moeda.converteR$Double(Moeda.multiplicar(Moeda.converteUS$(valor), numeroParcelas));
        } else {
            numeroParcelas = 0;
            valorTotal = getValorString();
        }
        return numeroParcelas;
    }

    public int getQtdeMeses() {
        if ((servicoPessoa.getReferenciaVigoracao() != null && servicoPessoa.getReferenciaValidade() != null)
                && (!servicoPessoa.getReferenciaVigoracao().isEmpty() && !servicoPessoa.getReferenciaValidade().isEmpty())
                && DataHoje.menorData("01/" + servicoPessoa.getReferenciaVigoracao(), "01/" + servicoPessoa.getReferenciaValidade()) || DataHoje.igualdadeData("01/" + servicoPessoa.getReferenciaVigoracao(), "01/" + servicoPessoa.getReferenciaValidade())) {
            String dt = "01/" + servicoPessoa.getReferenciaVigoracao();
            return DataHoje.diffMonths(dt, "01/" + servicoPessoa.getReferenciaValidade());
        } else {
            return 0;
        }
    }

    public void setNumeroParcelas(int numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
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
                            if (Objects.equals(f.getId(), list.get(i).getFilial().getId())) {
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

    /**
     * 1 - Limpar filial_id_2 set 0;
     *
     * @param tcase
     */
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                filial_id_2 = 0;
                break;
        }

    }

    public Servicos getServicoIndividual() {
        try {
            return (Servicos) new Dao().find(new Servicos(), Integer.parseInt(listaIndividual.get(idIndividual).getDescription()));
        } catch (Exception e) {
            return new Servicos();
        }
    }

    public Integer getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(Integer idParceiro) {
        this.idParceiro = idParceiro;
    }

    public List<SelectItem> getListParceiro() {
        return listParceiro;
    }

    public void setListParceiro(List<SelectItem> listParceiro) {
        this.listParceiro = listParceiro;
    }

    public Double getDescontoServicoEmpresa() {
        return descontoServicoEmpresa;
    }

    public void setDescontoServicoEmpresa(Double descontoServicoEmpresa) {
        this.descontoServicoEmpresa = descontoServicoEmpresa;
    }

    private void loadListParceiro() {
        if (socios.getId() == -1 && aluno.getId() != -1) {
            listParceiro = new ArrayList();
            DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
            Servicos s = new Servicos();
            if (tipoMatricula.equals("Individual")) {
            } else {
                s = turma.getCursos();
            }
            List<DescontoServicoEmpresa> list = dsed.findByGrupo(2, idServico);
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

    /**
     * Desconto Serviço Empresa
     */
    public void dse() {
        if (idParceiro != null && idParceiro != -1) {
            Servicos s;
            s = (Servicos) new Dao().find(new Servicos(), idServico);
            DescontoServicoEmpresaDao dsed = new DescontoServicoEmpresaDao();
            DescontoServicoEmpresa dse = dsed.findByGrupo(2, s.getId(), idParceiro);
            if (dse != null) {
                descontoServicoEmpresa = dse.getDesconto();
            }
        } else {
            descontoServicoEmpresa = new Double(0);
        }
    }
}
