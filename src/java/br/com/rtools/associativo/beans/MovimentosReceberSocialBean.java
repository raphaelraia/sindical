package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.utils.PlanilhasSocialUtils;
import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Guia;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.TransferenciaCaixa;
import br.com.rtools.financeiro.beans.ConfiguracaoFinanceiroBean;
import br.com.rtools.financeiro.beans.PlanilhaDebitoBean;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoPessoaDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.movimento.ImprimirRecibo;
import br.com.rtools.movimento.TrataVencimento;
import br.com.rtools.movimento.TrataVencimentoRetorno;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.beans.FisicaBean;
import br.com.rtools.pessoa.beans.JuridicaBean;
import br.com.rtools.pessoa.dao.PessoaComplementoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AutenticaUsuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class MovimentosReceberSocialBean implements Serializable {

    private String porPesquisa = "abertos";
    private List<DataObject> listaMovimento = new ArrayList();
    private String titular = "";
    private String beneficiario = "";
    private String data = "";
    private String boleto = "";
    private String diasAtraso = "";
    private String multa = "", juros = "", correcao = "";
    private String caixa = "";
    private String documento = "";
    private String referencia = "";
    private String tipo = "";
    private String id_baixa = "";
    private String msgConfirma = "";
    private String desconto = "0,00";
    private boolean chkSeleciona = false;
    private boolean addMais = false;
    private Pessoa pessoa = new Pessoa();
    private List<Pessoa> listaPessoa = new ArrayList();

    private String matricula = "";
    private String categoria = "";
    private String grupo = "";
    private String status = "";

    private String descPesquisaBoleto = "";
    private List<SelectItem> listaContas = new ArrayList();
    private int indexConta = 0;
    private final ConfiguracaoSocialBean csb = new ConfiguracaoSocialBean();

    private boolean pessoaJuridicaNaLista = false;
    private final ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();
    private String motivoInativacao = "";
    private String motivoReativacao = "";

    private ControleAcessoBean cab = new ControleAcessoBean();
    private String referenciaPesquisa = "";

    private Socios socios;
    private DataObject linhaSelecionada = new DataObject();
    private String novoDesconto = "0,00";

    private boolean booAcrescimo = true;

    private List<ClassMovimentoAnexo> listaMovimentosAnexo = new ArrayList();
    private List<ClassMovimentoAnexo> listaMovimentosAnexoSelecionados = new ArrayList();

    private String vencimentoNovoBoleto = "";

    private Movimento movimentoRemover = null;

    private DataObject objectVencimento = new DataObject(new Boleto(), "");
    private DataObject objectMensagem = new DataObject(new Boleto(), "");
    private boolean chkBoletosAtrasados = false;

    private String criterioReferencia = "";
    private String criterioLoteBaixa = "";

    private String motivoEstorno = "";

    private List<LinhaBoletosAnexo> listaBoletosAnexo = new ArrayList();
    private List<LinhaBoletosAnexo> listaBoletosAnexoSelecionado = new ArrayList();
    private List<LinhaMovimentoDoBoleto> listaMovimentoDoBoleto = new ArrayList();

    private Boolean visibleAnexar = false;
    private LinhaBoletosAnexo linhaBoletosAnexo = null;
    private String limitePesquisa = "todos";

    private String dataEmissaoRecibo = "";

    private SelecionaBoleto selecionaBoleto = new SelecionaBoleto();

    @PostConstruct
    public void init() {
        csb.init();
        cfb.init();

        cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");

        socios = new Socios();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("usuarioAutenticado");
    }

    public Boolean movimentosBaixado(Boleto b) {
        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        List<Movimento> l_movimento = db.listaMovimentosPorNrCtrBoleto(b.getNrCtrBoleto());
        for (Movimento m : l_movimento) {
            if (m.getBaixa() != null) {
                return true;
            }
        }
        return false;
    }

    public void loadListaMovimentoDoBoleto(LinhaBoletosAnexo lma) {
        if (lma != null) {
            linhaBoletosAnexo = lma;
        }

        listaMovimentoDoBoleto.clear();

        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        List<Movimento> l_movimento = db.listaMovimentosPorNrCtrBoleto(linhaBoletosAnexo.getBoleto().getNrCtrBoleto());

        for (Movimento m : l_movimento) {
            listaMovimentoDoBoleto.add(
                    new LinhaMovimentoDoBoleto(
                            false,
                            m
                    )
            );
        }
    }

    public void clickCriteriosDeBusca() {

    }

    public void limparCriteriosDeBusca() {
        criterioReferencia = "";
        criterioLoteBaixa = "";

        listaMovimento.clear();
    }

    public TransferenciaCaixa transferenciaCaixa(int id_fechamento_caixa_saida) {
        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        List<TransferenciaCaixa> l = db.transferenciaCaixa(id_fechamento_caixa_saida);
        return (l.isEmpty()) ? new TransferenciaCaixa() : l.get(0);
    }

    public void alterarVencimento() {
        String vencimentox = objectVencimento.getArgumento1().toString();

        if (DataHoje.menorData(vencimentox, DataHoje.data())) {
            GenericaMensagem.warn("Atençao", "Data de vencimento nao pode ser MENOR que data atual!");
            return;
        }

        Boleto boletox = (Boleto) objectVencimento.getArgumento0();
        Dao dao = new Dao();

        dao.openTransaction();

        if (boletox.getStatusRetorno() != null && boletox.getStatusRetorno().getId() == 2) {
            boletox.setDtCobrancaRegistrada(DataHoje.dataHoje());
            boletox.setDtStatusRetorno(DataHoje.dataHoje());
            boletox.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 6));
        }

        boletox.setVencimento(vencimentox);
        boletox.setDtProcessamento(DataHoje.dataHoje());

        if (!dao.update(boletox)) {
            GenericaMensagem.error("Error", "Nao foi possivel alterar vencimento do Boleto! Tente Novamente.");
            return;
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Vencimento Alterado para " + vencimentox);

        objectVencimento = new DataObject(new Boleto(), "");
        loadBoletosAbertos();
    }

    public void selecionaVencimentoBoleto(Integer id_boleto) {
        Boleto boletox = (Boleto) new Dao().find(new Boleto(), id_boleto);

        if (boletox != null) {
            // BOLETO COM VENCIMENTO ANTERIOR , NOVO VENCIMENTO
            objectVencimento = new DataObject(boletox, "");
        }
    }

    public void alterarMensagem() {
        String mensagem = objectMensagem.getArgumento1().toString();
        Boleto boletox = (Boleto) objectMensagem.getArgumento0();
        Dao dao = new Dao();

        dao.openTransaction();

        boletox.setMensagem(mensagem.toUpperCase());

        if (!dao.update(boletox)) {
            GenericaMensagem.error("Error", "Nao foi possivel alterar mensagem do Boleto! Tente Novamente.");
            return;
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Mensagem Alterada para " + mensagem);

        objectMensagem = new DataObject(new Boleto(), "");
        loadBoletosAbertos();
    }

    public void selecionaMensagemBoleto(Integer id_boleto) {
        Boleto boletox = (Boleto) new Dao().find(new Boleto(), id_boleto);

        if (boletox != null) {
            // BOLETO COM MENSAGEM ANTERIOR , NOVA MENSAGEM
            objectMensagem = new DataObject(boletox, "");
        }
    }

    public void removerMovimento() {
        Integer quantidadeSelecionada = 0;

        for (LinhaMovimentoDoBoleto lmb : listaMovimentoDoBoleto) {
            if (lmb.getSelecionado()) {
                quantidadeSelecionada++;
            }
        }

        Dao dao = new Dao();

        dao.openTransaction();
        if (movimentoRemover == null) {

            Boolean selecionado = false;
            for (LinhaMovimentoDoBoleto lmb : listaMovimentoDoBoleto) {
                if (lmb.getSelecionado()) {

                    Boleto b = lmb.getMovimento().getBoleto();

                    if (b.getStatusRetorno() != null && (b.getStatusRetorno().getId() == 2 || b.getStatusRetorno().getId() == 4 || b.getStatusRetorno().getId() == 5)) {
                        if (quantidadeSelecionada != listaMovimentoDoBoleto.size()) {
                            GenericaMensagem.warn("Atenção", b.getStatusRetorno().getDescricao() + " apenas poderá ser removido se TODOS movimentos forem selecionados!");
                            dao.rollback();
                            return;
                        }
                    }

                    selecionado = true;
                    lmb.getMovimento().setNrCtrBoleto("");
                    lmb.getMovimento().setDocumento("");

                    if (!dao.update(lmb.getMovimento())) {
                        GenericaMensagem.error("Erro", "Não foi possível atualizar Movimento, tente novamente!");
                        dao.rollback();
                        return;
                    }
                }
            }

            if (!selecionado) {
                GenericaMensagem.warn("Atenção", "Nenhum movimento foi selecionado!");
                dao.rollback();
                return;
            }
        } else {

            Boleto b = movimentoRemover.getBoleto();

            //  erro aqui testar
            if (b.getStatusRetorno() != null && (b.getStatusRetorno().getId() == 2 || b.getStatusRetorno().getId() == 4 || b.getStatusRetorno().getId() == 5)) {
                if (listaMovimentoDoBoleto.size() != 1) {
                    GenericaMensagem.warn("Atenção", b.getStatusRetorno().getDescricao() + " apenas poderá ser removido se TODOS movimentos forem selecionados!");
                    movimentoRemover = null;
                    dao.rollback();
                    return;
                }
            }

            movimentoRemover.setNrCtrBoleto("");
            movimentoRemover.setDocumento("");

            if (!dao.update(movimentoRemover)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Movimento, tente novamente!");
                movimentoRemover = null;
                dao.rollback();
                return;
            }
        }
        dao.commit();

        loadBoletosAbertos();
        loadMovimentosAnexo();
        loadListaMovimentoDoBoleto(null);
        movimentoRemover = null;

    }

    public void loadBoletosAbertos() {
        listaBoletosAnexo.clear();
        listaBoletosAnexoSelecionado.clear();

        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();

        List<Vector> result = db.listaBoletosAbertosAgrupado(pessoa.getId(), chkBoletosAtrasados);

        if (!result.isEmpty()) {

            Dao dao = new Dao();

            dao.openTransaction();
            for (List linha : result) {

                Boleto bo = (Boleto) new Dao().find(new Boleto(), linha.get(0));

                TrataVencimentoRetorno tvr = TrataVencimento.boletoExiste(bo);

                if (tvr.getVencido()) {
                    bo.setDtProcessamento(DataHoje.dataHoje());
                }

                bo.setVencimento(tvr.getVencimentoBoletoString());
                bo.setValor(tvr.getValor_calculado());

                if (!dao.update(bo)) {
                    dao.rollback();
                    return;
                }

                listaBoletosAnexo.add(
                        // [0] - b.id, [1] - b.nr_ctr_boleto, [2] - b.ds_boleto, [3] - sum(m.nr_valor), [4] - b.dt_vencimento, [5] - b.dt_vencimento_original, [6] - b.ds_mensagem
                        new LinhaBoletosAnexo(
                                linha,
                                bo,
                                movimentosBaixado(bo)
                        )
                );

            }

            dao.commit();
        }
    }

    public void loadMovimentosAnexo() {
        listaMovimentosAnexo.clear();
        listaMovimentosAnexoSelecionados.clear();

        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        // PESQUISA RESPONSAVEL DA PESSOA
        FunctionsDao dbfunc = new FunctionsDao();
        Pessoa t = dbfunc.titularDaPessoa(pessoa.getId());

        List<Object> result = db.listaMovimentosAbertosAnexarAgrupado(pessoa.getId(), t.getId());

        Dao dao = new Dao();
        for (Object ob : result) {
            List linha = (List) ob;

            Movimento m = (Movimento) dao.find(new Movimento(), (Integer) linha.get(0));

            Double juros = (Double) linha.get(1);
            Double multa = (Double) linha.get(2);
            Double correcao = (Double) linha.get(3);

            listaMovimentosAnexo.add(
                    new ClassMovimentoAnexo(
                            m,
                            juros, // JUROS
                            multa, // MULTA
                            correcao, // CORRECAO
                            Moeda.soma(Moeda.soma(juros, Moeda.soma(multa, correcao)), m.getValor()) // VALOR CALCULADO
                    )
            );

        }
    }

    public void clickRemoverMovimentos(Movimento movimento) {
        if (movimento != null) {
            movimentoRemover = (Movimento) new Dao().rebind(movimento);
        } else {
            for (LinhaMovimentoDoBoleto lmb : listaMovimentoDoBoleto) {
                if (lmb.getSelecionado()) {
                    lmb.setMovimento((Movimento) new Dao().rebind(lmb.getMovimento()));
                }
            }
        }
    }

    public void clickAnexarMovimentos() {
        chkBoletosAtrasados = false;

        loadBoletosAbertos();
        loadMovimentosAnexo();

        if (!listaMovimentosAnexo.isEmpty()) {
            vencimentoNovoBoleto = listaMovimentosAnexo.get(0).getMovimento().getVencimento();

            if (DataHoje.menorData(vencimentoNovoBoleto, DataHoje.data())) {
                vencimentoNovoBoleto = DataHoje.data();
            }
        } else {
            vencimentoNovoBoleto = DataHoje.data();
        }

        visibleAnexar = true;
    }

    public void anexarMovimentos() {
        if (listaBoletosAnexoSelecionado.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Selecione um Boleto para Anexar Movimentos");
            return;
        }

        if (listaBoletosAnexoSelecionado.size() > 1) {
            GenericaMensagem.warn("Atenção", "Apenas 1 Boleto pode ser selecionado para Anexar!");
            return;
        }

        if (listaMovimentosAnexoSelecionados.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Selecione ao menos 1 Movimento para Anexar!");
            return;
        }

        Dao dao = new Dao();

        listaBoletosAnexoSelecionado.get(0).setBoleto((Boleto) dao.find(new Boleto(), listaBoletosAnexoSelecionado.get(0).getBoleto().getId()));

        Boleto b = listaBoletosAnexoSelecionado.get(0).getBoleto();

        if (b.getStatusRetorno() != null && (b.getStatusRetorno().getId() == 2 || b.getStatusRetorno().getId() == 4 || b.getStatusRetorno().getId() == 5)) {
            GenericaMensagem.warn("Atenção", b.getStatusRetorno().getDescricao() + " não pode ser anexado!");
            return;

        }

        dao.openTransaction();
        for (ClassMovimentoAnexo cmov : listaMovimentosAnexoSelecionados) {
            cmov.getMovimento().setNrCtrBoleto(b.getNrCtrBoleto());
            cmov.getMovimento().setDocumento(b.getBoletoComposto());

            if (!dao.update(cmov.getMovimento())) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Movimento, tente novamente!");
                return;
            }
        }
        dao.commit();

        GenericaMensagem.info("Sucesso", "Movimentos Anexados ao Boleto " + listaBoletosAnexoSelecionado.get(0).getBoleto().getBoletoComposto());

        Object ob = dao.liveSingle("select func_boleto_vencimento_original()", true);

        if (ob == null || (!(Boolean) ((Vector) ob).get(0))) {
            GenericaMensagem.error("Erro", "Não foi possível atualizar vencimento original!");
        }

        loadBoletosAbertos();
        loadMovimentosAnexo();
    }

    public void criarBoletos() {
        if (listaMovimentosAnexoSelecionados.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Selecione ao menos 1 Movimento para Criar um novo Boleto!");
            return;
        }

        if (vencimentoNovoBoleto.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite um VENCIMENTO para este novo Boleto!");
            return;
        }

        if (DataHoje.menorData(vencimentoNovoBoleto, DataHoje.data())) {
            GenericaMensagem.warn("Atenção", "VENCIMENTO não pode ser menor que Data de Hoje!");
            return;
        }

        FunctionsDao f = new FunctionsDao();

        List<Movimento> lm = new ArrayList();

        for (ClassMovimentoAnexo cma : listaMovimentosAnexoSelecionados) {
            lm.add(cma.getMovimento());
        }

        if (f.gerarBoletoSocial(lm, vencimentoNovoBoleto)) {
            GenericaMensagem.info("Sucesso", "Boleto Criado para o vencimento " + vencimentoNovoBoleto);
            loadBoletosAbertos();
            loadMovimentosAnexo();
        } else {
            GenericaMensagem.error("Erro", "Não foi possível gerar Boleto!");
        }

    }

    public void clickSelecionaBoleto(Integer id_boleto) {

        if (id_boleto == null) {
            id_boleto = linhaBoletosAnexo.getBoleto().getId();
        }

        selecionaBoleto = new SelecionaBoleto(id_boleto, false);
    }

    public void atualizarTela() {
        loadBoletosAbertos();
        loadMovimentosAnexo();
    }

    public void imprimirBoletos() {
        imprimirBoletos(false);
    }

    public void imprimirBoletos(Boolean download) {
        Boleto boletox = (Boleto) new Dao().find(new Boleto(), selecionaBoleto.getIdBoleto());

        if (boletox == null) {
            return;
        }

        ImprimirBoleto ib = new ImprimirBoleto();
        ib.imprimirBoletoSocial(boletox, "soc_boletos_geral_vw", false);

        if (download) {
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
            ib.setPathPasta(pathPasta);
            ib.baixarArquivo();
        } else {
            ib.visualizar(null);
        }
    }

    public void cliqueCalculoAcrescimo(DataObject linha) {
        if (linha != null) {
            linhaSelecionada = linha;
            if (cab.verificaPermissao("calcularJurosSocial", 3)) {
                GenericaSessao.put("AutenticaUsuario", new AutenticaUsuario("calcularJurosSocial", 3, "formMovimentosReceber", "movimentosReceberSocialBean", "calculoAcrescimo"));
                return;
            }
            calculoAcrescimo();
        } else {
            if (cab.verificaPermissao("calcularJurosSocial", 3)) {
                GenericaSessao.put("AutenticaUsuario", new AutenticaUsuario("calcularJurosSocial", 3, "formMovimentosReceber", "movimentosReceberSocialBean", "calculoTodosAcrescimo"));
                return;
            }
            calculoTodosAcrescimo();
        }

        PF.update("formMovimentosReceber");
    }

    public void calculoTodosAcrescimo() {
        booAcrescimo = !(booAcrescimo);
        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        for (DataObject linha : listaMovimento) {
            double[] valor = db.pesquisaValorAcrescimo(((Movimento) linha.getArgumento1()).getId());
            if (!booAcrescimo) {
                linha.setArgumento29(false);
                linha.setArgumento9(Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(linha.getArgumento9().toString()), valor[0])));
            } else {
                linha.setArgumento29(true);
                linha.setArgumento9(Moeda.converteR$Double(valor[1]));
            }
        }
        calculoDesconto();
    }

    public void calculoAcrescimo() {

        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        double[] valor = db.pesquisaValorAcrescimo(((Movimento) linhaSelecionada.getArgumento1()).getId());
        if ((Boolean) linhaSelecionada.getArgumento29()) {
            linhaSelecionada.setArgumento29(false);
            linhaSelecionada.setArgumento9(Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(linhaSelecionada.getArgumento9().toString()), valor[0])));
        } else {
            linhaSelecionada.setArgumento29(true);
            linhaSelecionada.setArgumento9(Moeda.converteR$Double(valor[1]));
        }

        calculoDesconto();
    }

    public String cadastroPessoa(DataObject linha, Pessoa pessoax) {
        if (pessoax == null) {
            Movimento mov = (Movimento) linha.getArgumento1();
            pessoax = mov.getBeneficiario();
        }

        FisicaDao dbf = new FisicaDao();
        Fisica f = dbf.pesquisaFisicaPorPessoa(pessoax.getId());

        if (f != null) {
            String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pessoaFisica();

            FisicaBean fb = new FisicaBean();
            fb.editarFisica(f, true);
            GenericaSessao.put("fisicaBean", fb);
            return retorno;
        }

        JuridicaDao dbj = new JuridicaDao();
        Juridica j = dbj.pesquisaJuridicaPorPessoa(pessoax.getId());

        if (j != null) {
            String retorno = (new ChamadaPaginaBean()).pessoaJuridica();
            JuridicaBean jb = new JuridicaBean();
            jb.editar(j, true);
            GenericaSessao.put("juridicaBean", jb);
            return retorno;
        }
        return null;
    }

    public void autorizarDesconto() {
        GenericaSessao.put("AutenticaUsuario", new AutenticaUsuario("dlg_desconto", "autorizaDescontos", 3));
    }

    public void adicionarDesconto() {
        desconto = novoDesconto;
        PF.closeDialog("dlg_autentica_usuario");
        PF.closeDialog("dlg_desconto");
        calculoDesconto();
        novoDesconto = "0,00";
        PF.update("formMovimentosReceber");
    }

    public void permissaoEcalculoDesconto() {
        // if TRUE não tem permissão
        if (cab.verificarPermissao("descontoTotalMensalidades", 1) || cab.verificarPermissao("descontoTotalMensalidades", 3)) {
            if (Moeda.converteUS$(desconto) > 5) {
                GenericaMensagem.warn("Atenção", "Usuário sem permissão para desconto maior que R$ 5,00");
                desconto = "0,00";
            }
        }

        calculoDesconto();
    }

    public void inativarMovimentos() {
        if (motivoInativacao.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite um motivo para exclusão!");
            return;
        } else if (motivoInativacao.length() < 6) {
            GenericaMensagem.warn("Atenção", "Motivo de exclusão inválido!");
            return;
        }

        List<Movimento> listam = new ArrayList();

        if (baixado()) {
            GenericaMensagem.warn("Atenção", "Boletos BAIXADOS não podem ser excluídos!");
            return;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser excluídos!");
            return;
        }

        if (acordados()) {
            GenericaMensagem.warn("Atenção", "Boletos do tipo ACORDO não podem ser excluídos, veja opções de acordo!");
            return;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        for (DataObject dh : listaMovimento) {
            if ((Boolean) dh.getArgumento0()) {
                int id_movimento = ((Movimento) dh.getArgumento1()).getId();
                Movimento mov = (Movimento) new Dao().find(new Movimento(), id_movimento);
                listam.add(mov);
                novoLog.setCodigo(mov.getId());
                novoLog.setTabela("fin_movimento");
                novoLog.update("INATIVAR",
                        " Movimento - ID: " + mov.getId()
                        + " - Ref.: " + mov.getReferencia()
                        + " - Vencimento: " + mov.getVencimento()
                        + " - Valor: " + mov.getValor()
                        + " - Responsável: (" + mov.getPessoa().getId() + ") " + mov.getPessoa().getNome()
                );
            }
        }

        if (listam.isEmpty()) {
            novoLog.cancelList();
            GenericaMensagem.warn("Atenção", "Nenhum boletos foi selecionado!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        if (!GerarMovimento.inativarArrayMovimento(listam, motivoInativacao, dao).isEmpty()) {
            novoLog.cancelList();
            GenericaMensagem.error("Atenção", "Ocorreu um erro em uma das exclusões, verifique o log!");
            dao.rollback();
            return;
        } else {
            GenericaMensagem.info("Sucesso", "Boletos foram excluídos!");
        }
        novoLog.saveList();
        listaMovimento.clear();
        dao.commit();
    }

    public void reativarMovimentos() {
        if (motivoReativacao.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite um motivo para reativação!");
            return;
        } else if (motivoReativacao.length() < 6) {
            GenericaMensagem.warn("Atenção", "Motivo de válido para reativação! Com mais de 6 caracteres.");
            return;
        }

        List<Movimento> listam = new ArrayList();

        if (baixado()) {
            GenericaMensagem.warn("Atenção", "Boletos BAIXADOS não podem ser reativados!");
            return;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser reativados!");
            return;
        }

        if (acordados()) {
            GenericaMensagem.warn("Atenção", "Boletos do tipo ACORDO não podem ser reativados!");
            return;
        }

        for (DataObject dh : listaMovimento) {
            if ((Boolean) dh.getArgumento0()) {
                listam.add((Movimento) new Dao().find(new Movimento(), ((Movimento) dh.getArgumento1()).getId()));
            }
        }

        if (listam.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum boletos foi selecionado!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        if (!GerarMovimento.reativarArrayMovimento(listam, motivoReativacao, dao).isEmpty()) {
            GenericaMensagem.error("Atenção", "Ocorreu um erro em uma dos movimentos a serem reativados, verifique o log!");
            dao.rollback();
            return;
        } else {
            GenericaMensagem.info("Sucesso", "Boletos foram reativados!");
        }

        listaMovimento.clear();
        dao.commit();
        motivoReativacao = "";
    }

    public void excluirAcordo() {
        int qnt = 0;
        Movimento mov = null;

        for (int i = 0; i < listaMovimento.size(); i++) {
            if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                qnt++;
                mov = (Movimento) listaMovimento.get(i).getArgumento1();
            }
        }

        if (qnt == 0) {
            GenericaMensagem.warn("Atenção", "Nenhum Acordo selecionado!");
            return;
        }

        if (qnt > 1) {
            GenericaMensagem.warn("Atenção", "Mais de um Acordo foi selecionado!");
            return;
        }

        String resposta = GerarMovimento.excluirUmAcordoSocial(mov);

        if (resposta.isEmpty()) {
            GenericaMensagem.info("Sucesso", "Acordo Excluído!");
            listaMovimento.clear();

            PF.update("formMovimentosReceber");
            PF.closeDialog("dlg_excluir_acordo");
            return;
        }

        GenericaMensagem.error("Atenção", resposta);
    }

    public String caixaOuBanco() {
        ControleAcessoBean cabx = new ControleAcessoBean();

        if (!cabx.getBotaoBaixaBanco()) {
            PF.openDialog("dlg_caixa_banco");
            return null;
        }

        return telaBaixa("caixa");
    }

    public void pessoaJuridicaNaListaxx() {
        JuridicaDao db = new JuridicaDao();
        for (Pessoa p : listaPessoa) {
            Juridica j = db.pesquisaJuridicaPorPessoa(p.getId());

            if (j != null) {
                pessoaJuridicaNaLista = true;
                return;
            }
        }
        pessoaJuridicaNaLista = false;
    }

    public Guia pesquisaGuia(int id_lote) {
        MovimentoDao db = new MovimentoDao();
        Guia gu = db.pesquisaGuias(id_lote);
        return gu;
    }

    public List<SelectItem> getListaContas() {
        if (listaContas.isEmpty()) {
            ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
            List<ContaCobranca> result = servDB.listaContaCobrancaAtivoAssociativo();
            if (result.isEmpty()) {
                listaContas.add(new SelectItem(0, "Nenhuma Conta Encontrada", "0"));
                return listaContas;
            }
            int contador = 0;
            for (int i = 0; i < result.size(); i++) {
                // LAYOUT 2 = SINDICAL
                if (result.get(i).getLayout().getId() != 2) {
                    listaContas.add(
                            new SelectItem(
                                    contador,
                                    result.get(i).getApelido() + " - " + result.get(i).getCodCedente(), // CODCEDENTE NO CASO DE OUTRAS
                                    Integer.toString(result.get(i).getId())
                            )
                    );
                    contador++;
                }
            }
        }
        return listaContas;
    }

    public void pesquisaBoleto() {
        if (descPesquisaBoleto.isEmpty() || descPesquisaBoleto.equals("0")) {
            if (pessoa.getId() != -1) {
                porPesquisa = "todos";
                listaMovimento.clear();
                getListaMovimento();
                socios = new Socios();
            }
            return;
        }

        try {
            MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
            ContaCobranca contaCobranca = (ContaCobranca) new Dao().find(new ContaCobranca(), Integer.parseInt(((SelectItem) listaContas.get(indexConta)).getDescription()));
            Pessoa p;
            if (((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId() == 1) {
                p = db.pesquisaPessoaPorBoleto(descPesquisaBoleto, contaCobranca.getId(), true);
            } else {
                p = db.pesquisaPessoaPorBoleto(descPesquisaBoleto, contaCobranca.getId());
            }
            listaPessoa.clear();
            pessoa = new Pessoa();
            socios = new Socios();

            if (p != null) {
                pessoa = p;
                listaPessoa.add(p);
                pessoaJuridicaNaListaxx();
            }
            porPesquisa = "todos";
            listaMovimento.clear();
            getListaMovimento();
        } catch (Exception e) {
            descPesquisaBoleto = "";
            GenericaMensagem.fatal("Atenção", "Digite um número de Boleto válido!");
        }
    }

    public void salvarRecibo(byte[] arquivo, Baixa baixa) {
        if (baixa.getCaixa() == null) {
            return;
        }

        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/" + "Arquivos/recibo/" + baixa.getCaixa().getCaixa() + "/" + DataHoje.converteData(baixa.getDtBaixa()).replace("/", "-"));
        Diretorio.criar("Arquivos/recibo/" + baixa.getCaixa().getCaixa() + "/" + DataHoje.converteData(baixa.getDtBaixa()).replace("/", "-"));

        String path_arquivo = caminho + "/" + String.valueOf(baixa.getUsuario().getId()) + "_" + String.valueOf(baixa.getId()) + ".pdf";
        File file_arquivo = new File(path_arquivo);

        if (file_arquivo.exists()) {
            path_arquivo = caminho + "/" + String.valueOf(baixa.getUsuario().getId()) + "_" + String.valueOf(baixa.getId()) + "_(2).pdf";
        }

        try {
            File fl = new File(path_arquivo);
            try (FileOutputStream out = new FileOutputStream(fl)) {
                out.write(arquivo);
                out.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public String targetImprimeRecibo(Integer movimento_id) {
        if (validaImprimeRecibo((Movimento) new Dao().find(new Movimento(), movimento_id))) {
            return "_blank";
        }
        return "";
    }

    public String targetImprimeRecibo(Movimento mov) {
        if (validaImprimeRecibo(mov)) {
            return "_blank";
        }
        return "";
    }

    public Boolean validaImprimeRecibo(Movimento mov) {
        if (Usuario.getUsuario().getId() != 1) {
            if (mov.getBaixa() != null && !mov.getBaixa().getImportacao().isEmpty()) {
                GenericaMensagem.fatal("ATENÇÃO", "RECIBO COM DATA DE IMPORTAÇÃO NÃO PODE SER REIMPRESSO!");
                return false;
            }
            if (mov.getBaixa().getUsuario().getId() != Usuario.getUsuario().getId() && cab.verificaPermissao("reimpressao_recibo_outro_operador", 4)) {
                GenericaMensagem.fatal("ATENÇÃO", "USUÁRIO SEM PERMISSÃO PARA REIMPRIMIR ESTE RECIBO! (BAIXADO POR: " + mov.getBaixa().getUsuario().getPessoa().getNome() + ")");
                return false;
            }
        }
        return true;
    }

    public String recibo(Movimento mov) {
        ImprimirRecibo ir = new ImprimirRecibo();
        if (validaImprimeRecibo(mov)) {
            Map map = new HashMap();
            map.put("2_via", true);

            if (!dataEmissaoRecibo.isEmpty()) {
                map.put("data_emissao", dataEmissaoRecibo);
            }

            if (ir.gerar_recibo(mov.getId(), map)) {
                ir.imprimir();
            }

        }
        return null;
    }

    public void encaminhamento(Integer id_lote) {
        ImprimirRecibo ir = new ImprimirRecibo();

        if (ir.gerar_encaminhamento(id_lote)) {

            ir.imprimir();

        }

    }

    public String removerPesquisa() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaPesquisa");
        pessoa = new Pessoa();
        return "movimentosReceberSocial";
    }

    public String removerPessoaLista(int index) {
        listaPessoa.remove(index);
        listaMovimento.clear();
        return "movimentosReceberSocial";
    }

    public boolean baixado() {
        for (int i = 0; i < listaMovimento.size(); i++) {
            if (((Boolean) listaMovimento.get(i).getArgumento0()) && listaMovimento.get(i).getArgumento26() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean fechadosCaixa() {
        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        for (int i = 0; i < listaMovimento.size(); i++) {
            if (((Boolean) listaMovimento.get(i).getArgumento0())
                    && (((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa() != null) && (((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa().getFechamentoCaixa() != null)) {
                return true;
            }
        }
        return false;
    }

    public boolean semValor() {
        for (int i = 0; i < listaMovimento.size(); i++) {
            if ((Boolean) listaMovimento.get(i).getArgumento0() && ((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean acordados() {
        for (int i = 0; i < listaMovimento.size(); i++) {
            if ((Boolean) listaMovimento.get(i).getArgumento0() && String.valueOf(listaMovimento.get(i).getArgumento3()).equals("Acordo")) {
                return true;
            }
        }
        return false;
    }

    public String refazerMovimentos() {
        if (listaMovimento.isEmpty()) {
            msgConfirma = "Não existem Movimentos para serem refeitos!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        MovimentoDao db = new MovimentoDao();
        int qnt = 0;

        List<Movimento> lm = new ArrayList();

        for (DataObject listaMovimento1 : listaMovimento) {
            if ((Boolean) listaMovimento1.getArgumento0()) {
                qnt++;
                lm.add((Movimento) listaMovimento1.getArgumento1());
            }
        }

        if (qnt == 0) {
            msgConfirma = "Nenhum Movimentos selecionado!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (baixado()) {
            msgConfirma = "Existem Movimentos pagos, não podem ser refeitos!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        ServicoPessoaDao spd = new ServicoPessoaDao();
        for (Movimento m : lm) {
            ServicoPessoa sp = spd.pesquisaServicoPessoa(m.getBeneficiario().getId(), m.getServicos().getId(), true);

            if (sp == null) {
                msgConfirma = "O SERVIÇO " + m.getServicos().getDescricao() + " para a PESSOA " + m.getBeneficiario().getNome() + " não pode ser refeito!";
                GenericaMensagem.warn("Atenção", msgConfirma);
                return null;
            }
            novoLog.setCodigo(m.getId());
            novoLog.setTabela("fin_movimento");
            novoLog.update("REFAZER (FUNÇÃO GERAR MOVIMENTOS)",
                    " Movimento - ID: " + m.getId()
                    + " - Ref.: " + m.getReferencia()
                    + " - Vencimento: " + m.getVencimento()
                    + " - Valor: " + m.getValor()
                    + " - Responsável: (" + m.getPessoa().getId() + ") " + m.getPessoa().getNome()
            );

        }

        if (!GerarMovimento.refazerMovimentos(lm)) {
            msgConfirma = "Não foi possível refazer movimentos";
            GenericaMensagem.error("Erro", msgConfirma);
            return null;
        }
        novoLog.saveList();

        msgConfirma = "Boletos atualizados!";
        GenericaMensagem.info("Sucesso", msgConfirma);

        listaMovimento.clear();
        return null;
    }

    public String estornarBaixa() {
        GenericaSessao.remove("estorno_movimento_sucesso");
        if (listaMovimento.isEmpty()) {
            msgConfirma = "Não existem boletos para serem estornados!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }
        int qnt = 0;
        Movimento mov = null;

        for (int i = 0; i < listaMovimento.size(); i++) {
            if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                qnt++;
                mov = (Movimento) listaMovimento.get(i).getArgumento1();
            }
        }

        if (qnt == 0) {
            msgConfirma = "Nenhum Movimento selecionado!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (qnt > 1) {
            msgConfirma = "Mais de um movimento foi selecionado!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (!baixado()) {
            msgConfirma = "Existem boletos que não foram pagos para estornar!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (fechadosCaixa()) {
            GenericaMensagem.warn("Atenção", "Boletos COM CAIXA FECHADO não podem ser estornados!");
            return null;
        }

        if (motivoEstorno.isEmpty() || motivoEstorno.length() <= 5) {
            GenericaMensagem.error("Atenção", "Motivo de Estorno INVÁLIDO!");
            return null;
        }

        Usuario user = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        if (mov != null && mov.getBaixa().getUsuario().getId() != user.getId()) {
            if (cab.getBotaoEstornarMensalidadesOutrosUsuarios()) {
                GenericaMensagem.error("Atenção", "Você não tem permissão para estornar esse movimento!");
                return null;
            }
        }

        if (!mov.isAtivo()) {
            msgConfirma = "Boleto ID: " + mov.getId() + " esta inativo, não é possivel concluir estorno!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (mov.getLote().getRotina() != null && mov.getLote().getRotina().getId() == 132) {
            mov.setAtivo(false);
        }

        Integer id_baixa_estornada = mov.getBaixa().getId();

        StatusRetornoMensagem sr = GerarMovimento.estornarMovimento(mov, motivoEstorno);

        if (!sr.getStatus()) {
            msgConfirma = sr.getMensagem();
            GenericaMensagem.warn("Erro", msgConfirma);
        } else {
            msgConfirma = sr.getMensagem();

            NovoLog novoLog = new NovoLog();
            novoLog.setCodigo(mov.getId());
            novoLog.setTabela("fin_movimento");
            novoLog.update("",
                    " Movimento - ID: " + mov.getId()
                    + " - Ref.: " + mov.getReferencia()
                    + " - Vencimento: " + mov.getVencimento()
                    + " - Valor: " + mov.getValor()
                    + " - Responsável: (" + mov.getPessoa().getId() + ") " + mov.getPessoa().getNome()
                    + " - Motivo: " + motivoEstorno
                    + " - Número da Baixa: " + id_baixa_estornada
            );
            GenericaMensagem.info("Sucesso", msgConfirma);
            GenericaSessao.put("baixa_sucesso", true);
        }
        listaMovimento.clear();
        chkSeleciona = true;
        motivoEstorno = "";
        return null;
    }

    public String telaBaixa(String caixa_banco) {
        List lista = new ArrayList();
        MacFilial macFilial = (MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial");

        if (macFilial == null) {
            msgConfirma = "Não existe filial na sessão!";
            GenericaMensagem.warn("Erro", msgConfirma);
            PF.closeDialog("dlg_caixa_banco");
            PF.update("formMovimentosReceber");
            return null;
        }

        if (!macFilial.getCaixaOperador()) {
            if (macFilial.getCaixa() == null) {
                msgConfirma = "Configurar Caixa nesta estação de trabalho!";
                GenericaMensagem.warn("Erro", msgConfirma);
                PF.closeDialog("dlg_caixa_banco");
                PF.update("formMovimentosReceber");
                return null;
            }
        } else {
            FinanceiroDao dao = new FinanceiroDao();
            Caixa caixax = dao.pesquisaCaixaUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId(), macFilial.getFilial().getId());

            if (caixax == null) {
                msgConfirma = "Configurar Caixa para este Operador!";
                GenericaMensagem.warn("Erro", msgConfirma);
                PF.closeDialog("dlg_caixa_banco");
                PF.update("formMovimentosReceber");
                return null;
            }
        }

        if (baixado()) {
            msgConfirma = "Existem boletos baixados na lista!";
            GenericaMensagem.warn("Erro", msgConfirma);
            PF.closeDialog("dlg_caixa_banco");
            PF.update("formMovimentosReceber");
            return null;
        }

        // ROGÉRIO PEDIU PARA BAIXAR BOLETOS COM VALOR ZERADO -- chamado 540
//        if (semValor()) {
//            msgConfirma = "Boletos sem valor não podem ser Baixados!";
//            GenericaMensagem.warn("Erro", msgConfirma);
//            PF.closeDialog("dlg_caixa_banco");
//            PF.update("formMovimentosReceber");            
//            return null;
//        }
        Boolean error = false;
        if (!listaMovimento.isEmpty()) {
            for (int i = 0; i < listaMovimento.size(); i++) {
                Movimento movimento = new Movimento();
                if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                    movimento = (Movimento) listaMovimento.get(i).getArgumento1();
                    if (((PessoaComplemento) listaMovimento.get(i).getArgumento31()).getBloqueiaObsAviso()) {
                        GenericaMensagem.error("Mensagem", "Beneficiário (BLOQUEADO): " + ((PessoaComplemento) listaMovimento.get(i).getArgumento31()).getPessoa().getNome() + " - " + ((PessoaComplemento) listaMovimento.get(i).getArgumento31()).getObsAviso());
                        error = true;
                    }
                    if (((PessoaComplemento) listaMovimento.get(i).getArgumento32()).getBloqueiaObsAviso()) {
                        GenericaMensagem.error("Mensagem", "Titular (BLOQUEADO): " + ((PessoaComplemento) listaMovimento.get(i).getArgumento32()).getPessoa().getNome() + " - " + ((PessoaComplemento) listaMovimento.get(i).getArgumento32()).getObsAviso());
                        error = true;
                    }
                    if (!error) {
                        movimento.setMulta(Moeda.converteUS$(listaMovimento.get(i).getArgumento19().toString()));
                        movimento.setJuros(Moeda.converteUS$(listaMovimento.get(i).getArgumento20().toString()));
                        movimento.setCorrecao(Moeda.converteUS$(listaMovimento.get(i).getArgumento21().toString()));
                        movimento.setDesconto(Moeda.converteUS$(listaMovimento.get(i).getArgumento8().toString()));
                        movimento.setValor(Moeda.converteUS$(listaMovimento.get(i).getArgumento6().toString()));
                        movimento.setValorBaixa(Moeda.converteUS$(listaMovimento.get(i).getArgumento9().toString()));
                        lista.add(movimento);
                    }
                }
            }
            if (error) {
                PF.closeDialog("dlg_caixa_banco");
                PF.update("formMovimentosReceber");
                return null;
            }
            if (!lista.isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lista);

                GenericaSessao.put("caixa_banco", caixa_banco);
                GenericaSessao.put("tipo_recibo_imprimir", new Dao().find(new TipoRecibo(), 1));
                return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).baixaGeral();
            } else {
                msgConfirma = "Nenhum boleto foi selecionado";
                GenericaMensagem.warn("Atenção", msgConfirma);
                PF.closeDialog("dlg_caixa_banco");
                PF.update("formMovimentosReceber");
            }
        } else {
            msgConfirma = "Lista vazia!";
            GenericaMensagem.warn("Atenção", msgConfirma);
            PF.closeDialog("dlg_caixa_banco");
            PF.update("formMovimentosReceber");
        }
        return null;
    }

    public String telaMovimento(Movimento mov) {
        List lista = new ArrayList();
        lista.add(mov);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lista);
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).alterarMovimento();
    }

    public String telaAcordo() {
        List lista = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        Movimento movimento = new Movimento();
        if (baixado()) {
            msgConfirma = "Existem boletos baixados na lista!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (semValor()) {
            msgConfirma = "Boletos sem valor não podem ser Acordados!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        if (acordados()) {
            msgConfirma = "Boletos do tipo Acordo não podem ser Reacordados!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }
        if (!listaMovimento.isEmpty()) {
            for (int i = 0; i < listaMovimento.size(); i++) {
                if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                    movimento = (Movimento) listaMovimento.get(i).getArgumento1();

                    movimento.setMulta(Moeda.converteUS$(listaMovimento.get(i).getArgumento19().toString()));
                    movimento.setJuros(Moeda.converteUS$(listaMovimento.get(i).getArgumento20().toString()));
                    movimento.setCorrecao(Moeda.converteUS$(listaMovimento.get(i).getArgumento21().toString()));

                    movimento.setDesconto(Moeda.converteUS$(listaMovimento.get(i).getArgumento8().toString()));

                    movimento.setValor(Moeda.converteUS$(listaMovimento.get(i).getArgumento6().toString()));

                    movimento.setValorBaixa(Moeda.converteUS$(listaMovimento.get(i).getArgumento9().toString()));
                    lista.add(movimento);
                }
            }
            if (!lista.isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listaMovimento", lista);
                return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).acordoSocial();
            } else {
                msgConfirma = "Nenhum boleto foi selecionado";
                GenericaMensagem.warn("Erro", msgConfirma);
            }
        } else {
            msgConfirma = "Lista vazia!";
            GenericaMensagem.warn("Erro", msgConfirma);
        }
        return null;
    }

    public String print() {
        List list = new ArrayList();
        MovimentoDao db = new MovimentoDao();
        Movimento movimento = new Movimento();
        if (!listaMovimento.isEmpty()) {
            for (int i = 0; i < listaMovimento.size(); i++) {
                if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                    movimento = (Movimento) listaMovimento.get(i).getArgumento1();

                    movimento.setMulta(Moeda.converteUS$(listaMovimento.get(i).getArgumento19().toString()));
                    movimento.setJuros(Moeda.converteUS$(listaMovimento.get(i).getArgumento20().toString()));
                    movimento.setCorrecao(Moeda.converteUS$(listaMovimento.get(i).getArgumento21().toString()));

                    movimento.setDesconto(Moeda.converteUS$(listaMovimento.get(i).getArgumento8().toString()));

                    movimento.setValor(Moeda.converteUS$(listaMovimento.get(i).getArgumento6().toString()));

                    movimento.setValorBaixa(Moeda.converteUS$(listaMovimento.get(i).getArgumento9().toString()));
                    list.add(movimento);
                }
            }
            if (!list.isEmpty()) {
                new PlanilhasSocialUtils().print(list);
            } else {
                msgConfirma = "Nenhum boleto foi selecionado";
                GenericaMensagem.warn("Erro", msgConfirma);
            }
        } else {
            msgConfirma = "Lista vazia!";
            GenericaMensagem.warn("Erro", msgConfirma);
        }
        return null;
    }

    public void calculoDesconto() {
        double descPorcento = 0;
        double desc = 0;
        double calc = Moeda.substituiVirgulaDouble(getValorPraDesconto()); // VALOR PARA DESCONTO TEM QUE SER A SOMA DE TODOS OS VALORES CHECADOS (MENOS) IF SEM ACRESCIMO
        double calculo_total_aberto = 0;

        if (Moeda.converteUS$(desconto) > calc) {
            desconto = String.valueOf(calc);
        }

        descPorcento = Moeda.multiplicar(Moeda.divisao(Moeda.converteUS$(desconto), calc), 100);
        List<DataObject> linha = new ArrayList();

        for (int i = 0; i < listaMovimento.size(); i++) {
            MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
            double[] valorx = db.pesquisaValorAcrescimo(((Movimento) listaMovimento.get(i).getArgumento1()).getId());

            if ((Boolean) listaMovimento.get(i).getArgumento0() && ((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa() == null) {
                double calculo = 0;
                if ((Boolean) listaMovimento.get(i).getArgumento29()) {
                    double valox = valorx[1];
                    desc = Moeda.divisao(Moeda.multiplicar(valox, descPorcento), 100);
                    listaMovimento.get(i).setArgumento8(Moeda.converteR$(String.valueOf(desc)));
                    calculo = Moeda.converteDoubleR$Double(Moeda.subtracao(valox, desc));
                    listaMovimento.get(i).setArgumento9(Moeda.converteR$(String.valueOf(calculo)));

                    linha.add(listaMovimento.get(i));
                } else {
                    double valox = Moeda.subtracao(valorx[1], valorx[0]);
                    desc = Moeda.divisao(Moeda.multiplicar(valox, descPorcento), 100);
                    calculo = Moeda.converteDoubleR$Double(Moeda.subtracao(valox, desc));
                    listaMovimento.get(i).setArgumento9(Moeda.converteR$(String.valueOf(calculo)));
                }
                calculo_total_aberto = Moeda.soma(calculo_total_aberto, calculo);
            } else {
                // AQUI ESTA APAGANDO O DESCONTO QUE VEM DO BANCO
                // 21/12/2017 (NÃO APAGA MAIS O DESCONTO) REABILITADO O DESCONTO, SEM SABER O MOTIVO PORQUE ESTAVA APAGANDO EM CASO DE QUITAÇÃO (ROGÉRIO)
                // listaMovimento.get(i).setArgumento8("0,00");
                if ((Boolean) listaMovimento.get(i).getArgumento29()) {
                    listaMovimento.get(i).setArgumento9(Moeda.converteR$Double(valorx[1]));
                } else {
                    listaMovimento.get(i).setArgumento9(Moeda.converteR$Double(Moeda.subtracao(valorx[1], valorx[0])));
                }
            }
        }

        // CORRIGE OS VALORES QUE NÃO CORRESPONDE OS CENTAVOS APÓS DESCONTO
        // ex. VALOR 27,00 DESCONTO 20,00 VALOR CALCULADO 6,99
        // ADICIONA 0,01 CENTAVO NO ULTIMO MOVIMENTO SELECIONADO
        double calcx = Moeda.subtracao(calc, Moeda.converteUS$(desconto));
        if (calcx != Moeda.converteDoubleR$Double(calculo_total_aberto)) {
            if (calculo_total_aberto > calcx) {
                int quantidade = Integer.valueOf(Moeda.limparVirgula(Moeda.converteR$Double(Moeda.subtracao(calculo_total_aberto, calcx))));
                for (int i = 0; i < quantidade; i++) {
                    // SOMA O DESCONTO
                    double vld = Moeda.converteUS$(linha.get(i).getArgumento8().toString());
                    vld = Moeda.soma(vld, Double.parseDouble("0.01"));
                    linha.get(i).setArgumento8(Moeda.converteR$Double(vld));

                    // SUBTRAI DO VALOR CALCULADO
                    double vlc = Moeda.converteUS$(linha.get(i).getArgumento9().toString());
                    vlc = Moeda.subtracao(vlc, Double.parseDouble("0.01"));
                    linha.get(i).setArgumento9(Moeda.converteR$Double(vlc));
                }
            } else {
                int quantidade = Integer.valueOf(Moeda.limparVirgula(Moeda.converteR$Double(Moeda.subtracao(calcx, calculo_total_aberto))));
                for (int i = 0; i < quantidade; i++) {
                    // SUBTRAI DO DESCONTO
                    double vld = Moeda.converteUS$(linha.get(i).getArgumento8().toString());
                    vld = Moeda.subtracao(vld, Double.parseDouble("0.01"));
                    linha.get(i).setArgumento8(Moeda.converteR$Double(vld));

                    // SOMA O VALOR CALCULADO
                    double vlc = Moeda.converteUS$(linha.get(i).getArgumento9().toString());
                    vlc = Moeda.soma(vlc, Double.parseDouble("0.01"));
                    linha.get(i).setArgumento9(Moeda.converteR$Double(vlc));
                }
            }
        }
        // ------------------------------------------------------------------
    }

    public void atualizarStatus() {
        listaMovimento.clear();

        if (porPesquisa.equals("quitados") || porPesquisa.equals("todos")) {
            limitePesquisa = "50";
        } else if (porPesquisa.equals("abertos")) {
            limitePesquisa = "todos";
        }
    }

    public void listenerPesquisa() {
        listaMovimento.clear();
    }

    public String converteData(Date data) {
        return DataHoje.converteData(data);
    }

    public String converteValor(String valor) {
        return Moeda.converteR$(valor);
    }

    public String getTotal() {
        try {
            if (!listaMovimento.isEmpty()) {
                double soma = 0;
                for (int i = 0; i < listaMovimento.size(); i++) {
                    if ((Boolean) listaMovimento.get(i).getArgumento0() && ((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa() == null) {
                        soma = Moeda.soma(soma, Moeda.converteUS$(listaMovimento.get(i).getArgumento6().toString()));
                    }
                }

                return Moeda.converteR$Double(soma);
            } else {
                return "0,00";
            }

        } catch (Exception e) {
            return "0,00";
        }
    }

    public String getAcrescimo() {
        if (!listaMovimento.isEmpty()) {
            double soma = 0;
            for (int i = 0; i < listaMovimento.size(); i++) {
                if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                    soma = Moeda.soma(soma, Moeda.converteUS$(listaMovimento.get(i).getArgumento7().toString()));
                }
            }

            return Moeda.converteR$Double(soma);
        } else {
            return "0,00";
        }
    }

    public String getValorPraDesconto() {
        if (!listaMovimento.isEmpty()) {
            double soma = 0;
            for (int i = 0; i < listaMovimento.size(); i++) {
                if ((Boolean) listaMovimento.get(i).getArgumento0() && ((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa() == null) {
                    MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
                    double[] valorx = db.pesquisaValorAcrescimo(((Movimento) listaMovimento.get(i).getArgumento1()).getId());

                    if ((Boolean) listaMovimento.get(i).getArgumento29()) {
                        soma = Moeda.soma(soma, valorx[1]);
                    } else {
                        soma = Moeda.soma(soma, Moeda.subtracao(valorx[1], valorx[0]));
                    }
                }
            }
            return Moeda.converteR$Double(soma);
        } else {
            return "0,00";
        }
    }

    public String getTotalCalculado() {
        if (!listaMovimento.isEmpty()) {
            double soma = 0;
            for (int i = 0; i < listaMovimento.size(); i++) {
                if ((Boolean) listaMovimento.get(i).getArgumento0() && ((Movimento) listaMovimento.get(i).getArgumento1()).getBaixa() == null) {
                    soma = Moeda.soma(soma, Moeda.converteUS$(listaMovimento.get(i).getArgumento9().toString()));
                }
            }
            return Moeda.converteR$Double(soma);
        } else {
            return "0,00";
        }
    }

    public void complementoPessoa(DataObject linha) {
        // COMENTARIO PARA ORDEM QUE VEM DA QUERY
        //titular = (String) linha.getArgumento15(); // 13 - TITULAR
        tipo = (String) linha.getArgumento3(); // 1 - TIPO SERVIÇO
        referencia = (String) linha.getArgumento4(); // 2 - REFERENCIA
        id_baixa = (linha.getArgumento26() == null) ? "" : linha.getArgumento26().toString(); // 23 - ID_BAIXA

        beneficiario = (String) linha.getArgumento14(); // 12 - BENEFICIARIO
        data = linha.getArgumento16().toString(); // 16 - CRIACAO
        boleto = (String) linha.getArgumento17(); // 17 - BOLETO
        diasAtraso = linha.getArgumento18().toString(); // 18 - DIAS EM ATRASO
        multa = "R$ " + Moeda.converteR$(linha.getArgumento19().toString()); // 19 - MULTA
        juros = "R$ " + Moeda.converteR$(linha.getArgumento20().toString()); // 20 - JUROS
        correcao = "R$ " + Moeda.converteR$(linha.getArgumento21().toString()); // 21 - CORRECAO
        caixa = (linha.getArgumento22() == null) ? "Nenhum" : linha.getArgumento22().toString(); // 22 - CAIXA 
        documento = (linha.getArgumento23() == null) ? "Sem Documento" : linha.getArgumento23().toString(); // 24 - DOCUMENTO

        int id_lote = Integer.valueOf(linha.getArgumento27().toString());

        MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
        List<Vector> lista = db.dadosSocio(id_lote);

        if (!lista.isEmpty()) {
            titular = lista.get(0).get(0).toString(); // TITULAR
            matricula = lista.get(0).get(1).toString(); // MATRICULA
            categoria = lista.get(0).get(2).toString(); // CATEGORIA
            grupo = lista.get(0).get(3).toString(); // GRUPO
            status = lista.get(0).get(4).toString(); // CASE
        } else {
            titular = "";
            matricula = "";
            categoria = "";
            grupo = "";
            status = "";
        }
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public void marcarTodos() {
        for (DataObject listaMovimento1 : listaMovimento) {
            listaMovimento1.setArgumento0(chkSeleciona);
        }

        calculoDesconto();
    }

    public List<DataObject> getListaMovimento() {
        if (listaMovimento.isEmpty() && !listaPessoa.isEmpty()) {
            MovimentosReceberSocialDao db = new MovimentosReceberSocialDao();
            String id_pessoa = "", id_responsavel = "";

            FisicaDao dbf = new FisicaDao();
            JuridicaDao dbj = new JuridicaDao();
            FunctionsDao dbfunc = new FunctionsDao();
            PessoaComplementoDao pcd = new PessoaComplementoDao();
            List<Pessoa> listaPessoaQry = new ArrayList();
            for (Pessoa pe : listaPessoa) {
                // PESSOA FISICA -----
                Fisica fi = dbf.pesquisaFisicaPorPessoa(pe.getId());
                if (fi != null) {
                    // PESQUISA RESPONSAVEL DA PESSOA
                    Pessoa t = dbfunc.titularDaPessoa(pe.getId());
                    if (t != null) {
                        listaPessoaQry.add(t);
                    }
                    continue;
                }

                // PESSOA JURIDICA ---
                Juridica ju = dbj.pesquisaJuridicaPorPessoa(pe.getId());
                if (ju != null) {
                    listaPessoaQry.add(ju.getPessoa());
                }
            }

            for (int i = 0; i < listaPessoa.size(); i++) {
                if (id_pessoa.length() > 0 && i != listaPessoa.size()) {
                    id_pessoa = id_pessoa + ",";
                }
                id_pessoa = id_pessoa + String.valueOf(listaPessoa.get(i).getId());
            }

            for (int i = 0; i < listaPessoaQry.size(); i++) {
                if (id_responsavel.length() > 0 && i != listaPessoaQry.size()) {
                    id_responsavel = id_responsavel + ",";
                }
                id_responsavel = id_responsavel + String.valueOf(listaPessoaQry.get(i).getId());
            }

            List<Vector> lista;

            if (dbf.pesquisaFisicaPorPessoa(pessoa.getId()) != null) {
                lista = db.pesquisaListaMovimentos(id_pessoa, id_responsavel, porPesquisa, criterioReferencia, "fisica", criterioLoteBaixa, limitePesquisa);
            } else {
                lista = db.pesquisaListaMovimentos(id_pessoa, id_responsavel, porPesquisa, criterioReferencia, "juridica", criterioLoteBaixa, limitePesquisa);
            }

            boolean chk, disabled;
            String dataBaixa;

            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).get(8) != null) {
                    dataBaixa = DataHoje.converteData((Date) lista.get(i).get(8));
                } else {
                    dataBaixa = "";
                }

                // DATA DE HOJE MENOR OU IGUAL A DATA DE VENCIMENTO
                if ((DataHoje.converteDataParaInteger(DataHoje.converteData((Date) lista.get(i).get(3)))
                        <= DataHoje.converteDataParaInteger(DataHoje.data())
                        || DataHoje.converteDataParaReferencia(DataHoje.converteData((Date) lista.get(i).get(3))).equals(DataHoje.converteDataParaReferencia(DataHoje.data())))
                        && dataBaixa.isEmpty()) {
                    chk = true;
                } else {
                    chk = false;
                }

                // DATA DE HOJE MENOR QUE DATA DE VENCIMENTO
                if (DataHoje.converteDataParaInteger(DataHoje.converteData((Date) lista.get(i).get(3)))
                        < DataHoje.converteDataParaInteger(DataHoje.data())
                        && dataBaixa.isEmpty()) {

                    if (csb.getConfiguracaoSocial() == null) {
                        csb.init();
                    }
                    disabled = csb.getConfiguracaoSocial().getRecebeAtrasado();

                } else {
                    disabled = false;
                }
                PessoaComplemento pcb = pcd.findByPessoa((Integer) lista.get(i).get(26));
                if (pcb == null) {
                    pcb = new PessoaComplemento();
                }
                PessoaComplemento pcr = pcd.findByPessoa((Integer) lista.get(i).get(13));
                if (pcr == null) {
                    pcr = new PessoaComplemento();
                }
                String importacao = null;
                try {
                    importacao = ((Date) lista.get(i).get(27)).toString();
                } catch (Exception e) {
                }
                listaMovimento.add(new DataObject(
                        chk, // ARG 0
                        (Movimento) new Dao().find(new Movimento(), lista.get(i).get(14)), // ARG 1 Movimento
                        lista.get(i).get(0), // ARG 2 SERVICO
                        lista.get(i).get(1), // ARG 3 TIPO_SERVICO
                        lista.get(i).get(2), // ARG 4 REFERENCIA
                        DataHoje.converteData((Date) lista.get(i).get(3)), // ARG 5 VENCIMENTO
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(4))), // ARG 6 VALOR
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(5))), // ARG 7 ACRESCIMO
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(6))), // ARG 8 DESCONTO
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(7))), // ARG 9 VALOR CALCULADO
                        dataBaixa, // ARG 10 DATA BAIXA
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(9))), // ARG 11 VALOR_BAIXA
                        lista.get(i).get(10), // ARG 12 ES
                        lista.get(i).get(11), // ARG 13 RESPONSAVEL -> NOME
                        lista.get(i).get(12), // ARG 14 BENEFICIARIO -> NOME
                        lista.get(i).get(13), // ARG 15 TITULAR -> ID
                        DataHoje.converteData((Date) lista.get(i).get(16)), // ARG 16 CRIACAO
                        lista.get(i).get(17), // ARG 17 BOLETO
                        lista.get(i).get(18), // ARG 18 DIAS DE ATRASO
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(19))), // ARG 29 MULTA
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(20))), // ARG 20 JUROS
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(21))), // ARG 21 CORRECAO
                        getConverteNullString(lista.get(i).get(22)), // ARG 22 CAIXA 
                        lista.get(i).get(24), // ARG 23 DOCUMENTO
                        Moeda.converteR$(getConverteNullString(lista.get(i).get(7))), // ARG 24 VALOR CALCULADO ORIGINAL
                        disabled,
                        lista.get(i).get(23), // ARG 26 ID_BAIXA
                        lista.get(i).get(15), // ARG 27 ID_LOTE
                        (!descPesquisaBoleto.isEmpty() && descPesquisaBoleto.equals(lista.get(i).get(17))) ? "tblListaBoleto" : "", // BOLETO PESQUISADO -- ARG 28
                        true, // ARG 29 JUROS
                        lista.get(i).get(25), // ARG 30 NOME TITULAR
                        pcb, // ARG 31 PESSOA COMPLEMENTO BENEFICIÁRIO
                        pcr, // ARG 32 PESSOA COMPLEMENTO TITULAR
                        DataHoje.converteData(DataHoje.converteDateSqlToDate(importacao)), // ARG 33 DATA IMPORTAÇÃO
                        null
                )
                );
            }

            calculoDesconto();
        }
        return listaMovimento;
    }

    public void setListaMovimento(List<DataObject> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getBoleto() {
        return boleto;
    }

    public void setBoleto(String boleto) {
        this.boleto = boleto;
    }

    public String getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(String diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public String getMulta() {
        return multa;
    }

    public void setMulta(String multa) {
        this.multa = multa;
    }

    public String getJuros() {
        return juros;
    }

    public void setJuros(String juros) {
        this.juros = juros;
    }

    public String getCorrecao() {
        return correcao;
    }

    public void setCorrecao(String correcao) {
        this.correcao = correcao;
    }

    public String getCaixa() {
        return caixa;
    }

    public void setCaixa(String caixa) {
        this.caixa = caixa;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getDesconto() {
        if (desconto.isEmpty()) {
            desconto = "0,00";
        }
        return Moeda.converteR$(desconto);
    }

    public void setDesconto(String desconto) {
        if (desconto.isEmpty()) {
            desconto = "0,00";
        }
        this.desconto = Moeda.converteR$(desconto);
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public boolean isChkSeleciona() {
        return chkSeleciona;
    }

    public void setChkSeleciona(boolean chkSeleciona) {
        this.chkSeleciona = chkSeleciona;
    }

    public void adicionarPesquisa() {
        addMais = true;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            if (!addMais) {
                pessoa = new Pessoa();
                pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");

                SociosDao dbs = new SociosDao();
                socios = dbs.pesquisaSocioPorPessoaAtivo(pessoa.getId());

                listaPessoa.clear();

                listaPessoa.add(pessoa);
                listaMovimento.clear();
            } else {
                listaPessoa.add((Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("pessoaPesquisa"));
                listaMovimento.clear();
                addMais = false;
            }
            calculoDesconto();
            pessoaJuridicaNaListaxx();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaPesquisa");
            booAcrescimo = true;
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<Pessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public String getConverteNullString(Object object) {
        if (object == null) {
            return "";
        } else {
            return String.valueOf(object);
        }
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId_baixa() {
        return id_baixa;
    }

    public void setId_baixa(String id_baixa) {
        this.id_baixa = id_baixa;
    }

    public String getDescPesquisaBoleto() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            descPesquisaBoleto = "";
        }
        return descPesquisaBoleto;
    }

    public void setDescPesquisaBoleto(String descPesquisaBoleto) {
        this.descPesquisaBoleto = descPesquisaBoleto;
    }

    public void setListaContas(List<SelectItem> listaContas) {
        this.listaContas = listaContas;
    }

    public int getIndexConta() {
        return indexConta;
    }

    public void setIndexConta(int indexConta) {
        this.indexConta = indexConta;
    }

    public boolean isPessoaJuridicaNaLista() {
        return pessoaJuridicaNaLista;
    }

    public void setPessoaJuridicaNaLista(boolean pessoaJuridicaNaLista) {
        this.pessoaJuridicaNaLista = pessoaJuridicaNaLista;
    }

    public String getMotivoInativacao() {
        return motivoInativacao;
    }

    public void setMotivoInativacao(String motivoInativacao) {
        this.motivoInativacao = motivoInativacao;
    }

    public String getReferenciaPesquisa() {
        return referenciaPesquisa;
    }

    public void setReferenciaPesquisa(String referenciaPesquisa) {
        this.referenciaPesquisa = referenciaPesquisa;
    }

    public ControleAcessoBean getCab() {
        return cab;
    }

    public void setCab(ControleAcessoBean cab) {
        this.cab = cab;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public DataObject getLinhaSelecionada() {
        return linhaSelecionada;
    }

    public void setLinhaSelecionada(DataObject linhaSelecionada) {
        this.linhaSelecionada = linhaSelecionada;
    }

    public String getNovoDesconto() {
        if (novoDesconto.isEmpty()) {
            novoDesconto = "0,00";
        }
        return Moeda.converteR$(novoDesconto);
    }

    public void setNovoDesconto(String novoDesconto) {
        if (novoDesconto.isEmpty()) {
            novoDesconto = "0,00";
        }
        this.novoDesconto = Moeda.converteR$(novoDesconto);
    }

    public boolean isBooAcrescimo() {
        return booAcrescimo;
    }

    public void setBooAcrescimo(boolean booAcrescimo) {
        this.booAcrescimo = booAcrescimo;
    }

    public String put(Pessoa p) {
        String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).movimentosReceberSocial();
        GenericaSessao.put("movimentosReceberSocialBean", new MovimentosReceberSocialBean());
        GenericaSessao.put("pessoaPesquisa", p);
        return retorno;
    }

    public List<ClassMovimentoAnexo> getListaMovimentosAnexo() {
        return listaMovimentosAnexo;
    }

    public void setListaMovimentosAnexo(List<ClassMovimentoAnexo> listaMovimentosAnexo) {
        this.listaMovimentosAnexo = listaMovimentosAnexo;
    }

    public List<ClassMovimentoAnexo> getListaMovimentosAnexoSelecionados() {
        return listaMovimentosAnexoSelecionados;
    }

    public void setListaMovimentosAnexoSelecionados(List<ClassMovimentoAnexo> listaMovimentosAnexoSelecionados) {
        this.listaMovimentosAnexoSelecionados = listaMovimentosAnexoSelecionados;
    }

    public String getVencimentoNovoBoleto() {
        return vencimentoNovoBoleto;
    }

    public void setVencimentoNovoBoleto(String vencimentoNovoBoleto) {
        this.vencimentoNovoBoleto = vencimentoNovoBoleto;
    }

    public Movimento getMovimentoRemover() {
        return movimentoRemover;
    }

    public void setMovimentoRemover(Movimento movimentoRemover) {
        this.movimentoRemover = movimentoRemover;
    }

    public DataObject getObjectVencimento() {
        return objectVencimento;
    }

    public void setObjectVencimento(DataObject objectVencimento) {
        this.objectVencimento = objectVencimento;
    }

    public boolean isChkBoletosAtrasados() {
        return chkBoletosAtrasados;
    }

    public void setChkBoletosAtrasados(boolean chkBoletosAtrasados) {
        this.chkBoletosAtrasados = chkBoletosAtrasados;
    }

    public String getCriterioReferencia() {
        return criterioReferencia;
    }

    public void setCriterioReferencia(String criterioReferencia) {
        this.criterioReferencia = criterioReferencia;
    }

    public String getCriterioLoteBaixa() {
        return criterioLoteBaixa;
    }

    public void setCriterioLoteBaixa(String criterioLoteBaixa) {
        this.criterioLoteBaixa = criterioLoteBaixa;
    }

    public DataObject getObjectMensagem() {
        return objectMensagem;
    }

    public void setObjectMensagem(DataObject objectMensagem) {
        this.objectMensagem = objectMensagem;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public List<LinhaBoletosAnexo> getListaBoletosAnexo() {
        return listaBoletosAnexo;
    }

    public void setListaBoletosAnexo(List<LinhaBoletosAnexo> listaBoletosAnexo) {
        this.listaBoletosAnexo = listaBoletosAnexo;
    }

    public List<LinhaBoletosAnexo> getListaBoletosAnexoSelecionado() {
        return listaBoletosAnexoSelecionado;
    }

    public void setListaBoletosAnexoSelecionado(List<LinhaBoletosAnexo> listaBoletosAnexoSelecionado) {
        this.listaBoletosAnexoSelecionado = listaBoletosAnexoSelecionado;
    }

    public List<LinhaMovimentoDoBoleto> getListaMovimentoDoBoleto() {
        return listaMovimentoDoBoleto;
    }

    public void setListaMovimentoDoBoleto(List<LinhaMovimentoDoBoleto> listaMovimentoDoBoleto) {
        this.listaMovimentoDoBoleto = listaMovimentoDoBoleto;
    }

    public Boolean getVisibleAnexar() {
        return visibleAnexar;
    }

    public void setVisibleAnexar(Boolean visibleAnexar) {
        this.visibleAnexar = visibleAnexar;
    }

    public LinhaBoletosAnexo getLinhaBoletosAnexo() {
        return linhaBoletosAnexo;
    }

    public void setLinhaBoletosAnexo(LinhaBoletosAnexo linhaBoletosAnexo) {
        this.linhaBoletosAnexo = linhaBoletosAnexo;
    }

    public String getLimitePesquisa() {
        return limitePesquisa;
    }

    public void setLimitePesquisa(String limitePesquisa) {
        this.limitePesquisa = limitePesquisa;
    }

    public String getMotivoReativacao() {
        return motivoReativacao;
    }

    public void setMotivoReativacao(String motivoReativacao) {
        this.motivoReativacao = motivoReativacao;
    }

    public String getDataEmissaoRecibo() {
        return dataEmissaoRecibo;
    }

    public void setDataEmissaoRecibo(String dataEmissaoRecibo) {
        this.dataEmissaoRecibo = dataEmissaoRecibo;
    }

    public Boolean renderedEncaminhamento(Integer id_lote) {
        MovimentoDao db = new MovimentoDao();
        Guia gu = db.pesquisaGuias(id_lote);

        if (gu.getId() != -1 && gu.getSubGrupoConvenio() != null) {
            return gu.getSubGrupoConvenio().getEncaminhamento();
        }
        return false;
    }

    public String imprimirPlanilha() {
        PlanilhaDebitoBean.printNoNStatic(new ArrayList());
        return null;
    }

    public class LinhaBoletosAnexo {

        private List listaQuery;
        private Boleto boleto;
        private Boolean movimentoBaixado;

        public LinhaBoletosAnexo(List listaQuery, Boleto boleto, Boolean movimentoBaixado) {
            this.listaQuery = listaQuery;
            this.boleto = boleto;
            this.movimentoBaixado = movimentoBaixado;
        }

        public Boleto getBoleto() {
            return boleto;
        }

        public void setBoleto(Boleto boleto) {
            this.boleto = boleto;
        }

        public List getListaQuery() {
            return listaQuery;
        }

        public void setListaQuery(List listaQuery) {
            this.listaQuery = listaQuery;
        }

        public Boolean getMovimentoBaixado() {
            return movimentoBaixado;
        }

        public void setMovimentoBaixado(Boolean movimentoBaixado) {
            this.movimentoBaixado = movimentoBaixado;
        }
    }

    public class LinhaMovimentoDoBoleto {

        private Boolean selecionado;
        private Movimento movimento;

        public LinhaMovimentoDoBoleto(Boolean selecionado, Movimento movimento) {
            this.selecionado = selecionado;
            this.movimento = movimento;
        }

        public Boolean getSelecionado() {
            return selecionado;
        }

        public void setSelecionado(Boolean selecionado) {
            this.selecionado = selecionado;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

    }

    public class SelecionaBoleto {

        private Integer idBoleto;
        private Boolean download;

        public SelecionaBoleto() {
            this.idBoleto = -1;
            this.download = false;
        }

        public SelecionaBoleto(Integer idBoleto, Boolean download) {
            this.idBoleto = idBoleto;
            this.download = download;
        }

        public Integer getIdBoleto() {
            return idBoleto;
        }

        public void setIdBoleto(Integer idBoleto) {
            this.idBoleto = idBoleto;
        }

        public Boolean getDownload() {
            return download;
        }

        public void setDownload(Boolean download) {
            this.download = download;
        }

    }

    public SelecionaBoleto getSelecionaBoleto() {
        return selecionaBoleto;
    }

    public void setSelecionaBoleto(SelecionaBoleto selecionaBoleto) {
        this.selecionaBoleto = selecionaBoleto;
    }

    public class ClassMovimentoAnexo {

        private Movimento movimento;
        private Double juros;
        private Double multa;
        private Double correcao;
        private Double valorCalculado;

        public ClassMovimentoAnexo(Movimento movimento, Double juros, Double multa, Double correcao, Double valorCalculado) {
            this.movimento = movimento;
            this.juros = juros;
            this.multa = multa;
            this.correcao = correcao;
            this.valorCalculado = valorCalculado;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Double getJuros() {
            return juros;
        }

        public void setJuros(Double juros) {
            this.juros = juros;
        }

        public Double getMulta() {
            return multa;
        }

        public void setMulta(Double multa) {
            this.multa = multa;
        }

        public Double getCorrecao() {
            return correcao;
        }

        public void setCorrecao(Double correcao) {
            this.correcao = correcao;
        }

        public Double getValorCalculado() {
            return valorCalculado;
        }

        public void setValorCalculado(Double valorCalculado) {
            this.valorCalculado = valorCalculado;
        }

    }

    public Boolean getSelected() {
        for (int i = 0; i < listaMovimento.size(); i++) {
            if ((Boolean) listaMovimento.get(i).getArgumento0()) {
                return true;
            }
        }
        return false;
    }
}
