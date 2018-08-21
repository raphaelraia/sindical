package br.com.rtools.financeiro.beans;

import br.com.rtools.academia.beans.MatriculaAcademiaBean;
import br.com.rtools.arrecadacao.beans.BaixaBoletoBean;
import br.com.rtools.associativo.beans.EmissaoGuiasBean;
import br.com.rtools.associativo.beans.MovimentosReceberSocialBean;
import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.Banco;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.Cartao;
import br.com.rtools.financeiro.CartaoPag;
import br.com.rtools.financeiro.CartaoRec;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.ContaTipoPagamento;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoRecibo;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.financeiro.dao.ContaRecebimentoDao;
import br.com.rtools.financeiro.dao.ContaRotinaDao;
import br.com.rtools.financeiro.dao.FechamentoDiarioDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.LancamentoFinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.financeiro.lista.ListValoresBaixaGeral;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.movimento.ImprimirRecibo;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Modulo;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.StatusRetornoMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@SessionScoped
public class BaixaGeralBean implements Serializable {

    private String quitacao = DataHoje.data();
    private String vencimento = DataHoje.data();
    private String valor = "0,00";
    private String numero = "";
    private String numeroChequePag = "";
    private String total = "0,00";
    private List<ListValoresBaixaGeral> listaValores = new ArrayList();
    private List<Movimento> listaMovimentos = new ArrayList();
    private List<SelectItem> listaCartao = new ArrayList();
    private List<SelectItem> listaBanco = new ArrayList();
    private List<SelectItem> listaBancoSaida = new ArrayList();
    private List<SelectItem> listaConta = new ArrayList();
    private List<SelectItem> listaTipoPagamento = new ArrayList();

    private Integer idConta = 0;
    private int idTipoPagamento = 0;
    private int idCartao = 0;
    private int idBanco = 0;
    private int idBancoSaida = 0;
    private Rotina rotina = null;
    private Modulo modulo = new Modulo();
    private boolean desHabilitaConta = false;
    private boolean desHabilitaQuitacao = false;
    private boolean desHabilitaNumero = false;
    private boolean desHabilitadoVencimento = false;
    private boolean retorna = false;
    private String mensagem = "";
    private Plano5 plano5 = new Plano5();
    private int index = 0;
    private String tipo = "";
    private String banco = "";
    private String taxa = "0";
    private String es = "E";
    private ChequeRec chequeRec = new ChequeRec();
    private boolean visibleModal = false;
    private final ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();

    private String valorTroco = "";
    private String valorEditavel = "";
    private TipoPagamento tipoPagamento;
    private Caixa caixa = new Caixa();
    private String obs;

    private Integer indexListaTodosBancos = 0;
    private List<SelectItem> listaTodosBancos = new ArrayList();

    private Date dataConciliacao = null;
    private ControleAcessoBean cab = new ControleAcessoBean();
    private String dataEmissaoRecibo;

    private Boolean visibleImprimirRecibo = false;
    private Date dataOcorrencia = null;

    private Date dataCreditoCartao = null;

    private String valorAcrescimo = "";
    private boolean confirmaAcrescimo = false;

    @PostConstruct
    public void init() {
        cfb.init();
        getListaMovimentos();
        getTipo();
        getPlano5();
        tipoPagamento = (TipoPagamento) new Dao().find(new TipoPagamento(), Integer.valueOf(getListaTipoPagamento().get(idTipoPagamento).getDescription()));
        caixa = new Caixa();
        obs = "";

        retornaCaixa();
        loadListaTodosBancos();

        cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");
        dataEmissaoRecibo = "";
    }

    public void confirmarAcrescimo(){
        confirmaAcrescimo = true;
    }
    
    public void atualizaCartao() {
        Cartao cart = (Cartao) new Dao().find(new Cartao(), Integer.valueOf(listaCartao.get(idCartao).getDescription()));

        dataCreditoCartao = DataHoje.converte(new DataHoje().incrementarDias(cart.getDias(), quitacao));
    }

    public void atualizaDataOcorrencia() {
        dataOcorrencia = DataHoje.dataHoje();
    }

    public void validacao() {
        String data_fechamento_diario = DataHoje.converteData(new FechamentoDiarioDao().ultimaDataContaSaldo());
        String data_hoje = DataHoje.data();

        if (DataHoje.menorData(data_hoje, data_fechamento_diario) || data_hoje.equals(data_fechamento_diario)) {
            mensagem = "FECHAMENTO DIÁRIO EFETUADO, MOVIMENTO NÃO PODE SER BAIXADO!";
            visibleModal = true;
            retorna = true;
            visibleImprimirRecibo = false;
        }
    }

    public final void loadListaTodosBancos() {
        listaTodosBancos.clear();

        List<Banco> result = new FinanceiroDao().listaDeBancos();
        listaTodosBancos.add(new SelectItem(0, "-- SELECIONE UM BANCO --", "0"));
        for (int i = 0; i < result.size(); i++) {
            Banco b = result.get(i);
            listaTodosBancos.add(
                    new SelectItem(
                            i + 1,
                            b.getNumero() + " - " + b.getBanco(),
                            "" + b.getId()
                    )
            );
        }
    }

    public void verificaValorDigitado() {
        boolean dinheiro = false;
        boolean cheque = false;
        for (int i = 0; i < listaValores.size(); i++) {
            if (listaValores.get(i).getTipoPagamento().getId() == 3) {
                dinheiro = true;
            }

            if (listaValores.get(i).getTipoPagamento().getId() == 4 || listaValores.get(i).getTipoPagamento().getId() == 5) {
                cheque = true;
            }
        }

        if (dinheiro) {
            for (int i = 0; i < listaValores.size(); i++) {
                if (listaValores.get(i).getTipoPagamento().getId() == 3) {
                    double valorx = Moeda.converteUS$(listaValores.get(i).getValor());
                    double valordigitado = Moeda.converteUS$(listaValores.get(i).getValorDigitado());
                    if (valorx < valordigitado) {
                        double valor_troco = Moeda.subtracao(valordigitado, valorx);
                        valorTroco = Moeda.converteR$Double(valor_troco);
                    }
                }
            }
        } else {
            valorTroco = "";
        }

        if (cheque) {
            for (int i = 0; i < listaValores.size(); i++) {
                if (listaValores.get(i).getTipoPagamento().getId() == 4 || listaValores.get(i).getTipoPagamento().getId() == 5) {
                    double valorx = Moeda.converteUS$(listaValores.get(i).getValor());
                    double valordigitado = Moeda.converteUS$(listaValores.get(i).getValorDigitado());
                    if (valorx < valordigitado) {
                        double valor_acrescimo = Moeda.subtracao(valordigitado, valorx);
                        valorAcrescimo = Moeda.converteR$Double(valor_acrescimo);
                    }
                }
            }
        } else {
            valorAcrescimo = "";
        }
    }

    public void atualizaTipo() {
        tipoPagamento = (TipoPagamento) new Dao().find(new TipoPagamento(), Integer.valueOf(getListaTipoPagamento().get(idTipoPagamento).getDescription()));
        if (tipoPagamento.getId() == 6 || tipoPagamento.getId() == 7) {
            listaCartao.clear();
            idCartao = 0;
        }
    }

    public void alteraNumeroChequeConta() {
        LancamentoFinanceiroDao db = new LancamentoFinanceiroDao();
        Plano5Dao dbx = new Plano5Dao();
        if (listaBancoSaida.size() == 1 && listaBancoSaida.get(0).getDescription().isEmpty()) {
            GenericaMensagem.error("Erro", "Nenhum Banco para Saida Encontrado");
            return;
        }

        Plano5 pl = dbx.pesquisaPlano5IDContaBanco(Integer.valueOf(listaBancoSaida.get(idBancoSaida).getDescription()));

        ChequePag ch = db.pesquisaChequeConta(numeroChequePag, pl.getId());
        ContaBanco cb = (ContaBanco) new Dao().find(new ContaBanco(), Integer.valueOf(listaBancoSaida.get(idBancoSaida).getDescription()));
        cb = (ContaBanco) new Dao().rebind(cb);
        if (ch != null) {
            GenericaMensagem.warn("Erro", "O cheque " + numeroChequePag + " já existe");
            numeroChequePag = String.valueOf(cb.getUCheque() + 1);
        }

        if (Integer.valueOf(numeroChequePag) == cb.getUCheque()) {
            numeroChequePag = String.valueOf(cb.getUCheque() + 1);
            return;
        }

        if (Integer.valueOf(numeroChequePag) == cb.getUCheque() + 1) {
            return;
        }

        if ((Integer.valueOf(numeroChequePag) + 1) > (cb.getUCheque() + 1)) {
            GenericaMensagem.warn("Erro", "Número " + (Integer.valueOf(numeroChequePag)) + " maior que permitido!");
            numeroChequePag = String.valueOf(cb.getUCheque() + 1);
        }
    }

    public String retorno() {
        if (retorna) {
            GenericaSessao.put("baixa_geral_sucesso", true);
            String url = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
            GenericaSessao.put("linkClicado", true);

            GenericaSessao.remove("tipo_recibo_imprimir");

            switch (url) {
                case "baixaBoleto":
                    ((BaixaBoletoBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("baixaBoletoBean")).loadListaBoleto();
                    return "baixaBoleto";

                case "movimentosReceberSocial":
                    ((MovimentosReceberSocialBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("movimentosReceberSocialBean")).getListaMovimento().clear();
                    return "movimentosReceberSocial";

                case "lancamentoFinanceiro":
                    return "lancamentoFinanceiro";

                case "emissaoGuias":
                    return "emissaoGuias";

                case "matriculaAcademia":
                    return "matriculaAcademia";

                case "menuPrincipal":
                    return "menuPrincipal";

                case "acessoNegado":
                    return "menuPrincipal";

                case "geracaoDebitosCartao":
                    GenericaSessao.put("lista_movimentos_baixados", listaMovimentos);
                    return "geracaoDebitosCartao";

                case "matriculaEscola":
                    return "matriculaEscola";

                case "vendasBaile":
                    return "vendasBaile";

                case "conviteMovimento":
                    return "conviteMovimento";

                case "devolucaoFilme":
                    return "devolucaoFilme";

                case "contasAPagar":
                    return "contasAPagar";

                default:
                    return "menuPrincipal";
            }
        } else {
            return null;
        }
    }

    private double somaValoresGrid() {
        double soma = 0;
        for (ListValoresBaixaGeral listaValore : listaValores) {
            soma = Moeda.soma(soma, Moeda.converteStringToDouble(listaValore.getValor()));
        }
        return soma;
    }

    public void inserir() {
        if (Moeda.converteUS$(valor) < 0) {
            GenericaMensagem.error("Atenção", "Valor negativo não é permitido!");
            return;
        }

        double valorGrid = 0;
        int tipo_dinheiro = 0;
        int tipo_cheque = 0;

        for (ListValoresBaixaGeral listaValore : listaValores) {
            valorGrid = Moeda.soma(valorGrid, Moeda.converteStringToDouble(listaValore.getValor()));

            switch (listaValore.getTipoPagamento().getId()) {
                case 3: // DINHEIRO
                    tipo_dinheiro++;
                    break;
                case 4: // CHEQUE
                case 5: // CHEQUE-PRÉ
                    tipo_cheque++;
                    break;
                case 6: // CARTÃO CRÉDITO
                case 7: // CARTÃO DÉBITO
                    break;
                case 2: // BOLETO
                case 8: // DEPÓSITO BANCÁRIO
                case 9: // DOC / TED
                case 10: // TRANS. BANCÁRIA
                case 13: // DÉBITO AUTOMATICO
                case 15: // INTERNET BANK
                    break;
                default: // OUTROS
                    break;
            }

        }

        if (tipo_dinheiro >= 1 && tipoPagamento.getId() == 3) {
            GenericaMensagem.error("Atenção", "Dinheiro já adicionado!");
            return;
        }

        if (tipoPagamento.getId() == 4 || tipoPagamento.getId() == 5) {
            if (tipo_dinheiro >= 1) {
                GenericaMensagem.error("Atenção", "Cheque deve ser adicionado PRIMEIRO!");
                return;
            }
        }

        valorEditavel = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(total), valorGrid));

        double valorDigitado;
        if (Moeda.converteUS$(valor) > Moeda.converteUS$(valorEditavel)) {
            valorDigitado = Moeda.converteUS$(valor);
            valor = valorEditavel;
        } else {
            valorDigitado = Moeda.converteUS$(valor);
        }

        // CHEQUE
        switch (tipoPagamento.getId()) {
            case 4:
            case 5:
                if (valorDigitado > Moeda.converteUS$(valor)){
                    Double calc = valorDigitado - Moeda.converteUS$(valor);
                    if (calc > 1){
                        GenericaMensagem.warn("Atenção", "Valor máximo para o acréscimo é de 1,00!");
                        return;
                    }
                }
                
                if (!getEs().isEmpty() && getEs().equals("S")) {
                    if (numeroChequePag.isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Digite um número para o Cheque!");
                        return;
                    }

                    Plano5Dao db = new Plano5Dao();
                    if (listaBancoSaida.size() == 1 && listaBancoSaida.get(0).getDescription().isEmpty()) {
                        GenericaMensagem.error("Erro", "Nenhum Banco saida Encontrado!");
                        return;
                    }

                    Plano5 pl = db.pesquisaPlano5IDContaBanco(Integer.valueOf(listaBancoSaida.get(idBancoSaida).getDescription()));

                    for (int i = 0; i < listaValores.size(); i++) {
                        if (listaValores.get(i).getChequePag() != null) {
                            if (listaValores.get(i).getChequePag().getPlano5().getId() == pl.getId()) {
                                GenericaMensagem.error("Erro", "Esta CONTA já foi adicionada!");
                                return;
                            }
                        }
                        listaBancoSaida.get(i).setValue(i);
                    }

                    ChequePag ch_p = new ChequePag();
                    ch_p.setCheque(numeroChequePag);
                    ch_p.setPlano5(pl);
                    ch_p.setDtVencimentoString(vencimento);

                    listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numeroChequePag, tipoPagamento, ch_p, null, pl, null, null, null, Moeda.converteR$Double(valorDigitado), (FStatus) (new Dao()).find(new FStatus(), 8), null, null, DataHoje.dataHoje(), null));
                } else {
                    if (numero.isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Digite um número para o Cheque!");
                        return;
                    }

                    if (Integer.valueOf(listaTodosBancos.get(indexListaTodosBancos).getDescription()).equals(0) || chequeRec.getAgencia().isEmpty() || chequeRec.getConta().isEmpty()) {
                        GenericaMensagem.warn("Atenção", "Agência, Conta e Banco não podem estar vazios!");
                        return;
                    }

                    ChequeRec ch = new ChequeRec();
                    ch.setAgencia(chequeRec.getAgencia());
                    ch.setBanco((Banco) new Dao().find(new Banco(), Integer.valueOf(listaTodosBancos.get(indexListaTodosBancos).getDescription())));
                    ch.setCheque(numero);
                    ch.setConta(chequeRec.getConta());
                    ch.setEmissao(quitacao);

                    ch.setVencimento(vencimento);
                    if (plano5.getId() == -1) {
                        GenericaMensagem.error("Erro", "Nenhum Plano5 Encontrado!");
                        return;
                    }
                    listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, ch, plano5, null, null, null, Moeda.converteR$Double(valorDigitado), (FStatus) (new Dao()).find(new FStatus(), 7), null, null, DataHoje.dataHoje(), null));
                }
                numero = "";
                numeroChequePag = "";
                chequeRec = new ChequeRec();
                break;
            case 6:
            case 7:
                if (!listaValores.isEmpty() && Moeda.converteUS$(total) == valorGrid) {
                    GenericaMensagem.error("Atenção", "Os valores já conferem!");
                    return;
                }

                // CARTAO
                if (listaCartao.isEmpty()) {
                    GenericaMensagem.error("SISTEMA", "NENHUM CARTÃO CADASTRADO!");
                    return;
                }
                Cartao cart = (Cartao) new Dao().find(new Cartao(), Integer.valueOf(listaCartao.get(idCartao).getDescription()));
                if (!getEs().isEmpty() && getEs().equals("S")) {
                    CartaoPag cartao_pag = null;
                    listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, null, cart, cartao_pag, null, Moeda.converteR$Double(valorDigitado), null, null, null, DataHoje.dataHoje(), dataCreditoCartao));
                } else {
                    CartaoRec cartao_rec = new CartaoRec(-1, null, cart);
                    listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, null, cart, null, cartao_rec, Moeda.converteR$Double(valorDigitado), (FStatus) (new Dao()).find(new FStatus(), 8), null, null, DataHoje.dataHoje(), dataCreditoCartao));
                }
                if (!listaCartao.get(0).getDescription().isEmpty()) {
                }

                dataCreditoCartao = null;
                break;
            case 2:
            case 8:
            case 9:
            case 10:
            case 13:
            case 15:
                if (!listaValores.isEmpty() && Moeda.converteUS$(total) == valorGrid) {
                    GenericaMensagem.error("Atenção", "Os valores já conferem!");
                    return;
                }

                Plano5Dao db = new Plano5Dao();
                if (listaBanco.size() == 1 && listaBanco.get(0).getDescription().isEmpty()) {
                    GenericaMensagem.error("Erro", "Nenhum Banco Encontrado!");
                    return;
                }

                for (ListValoresBaixaGeral listaValore : listaValores) {
                    // NÃO PODE ADIOCIONAR ESSES TIPOS JUNTO COM BOL/DEP BAN/DOC etc DEVIDO A DATA DE OCORRÊNCIA PODER SER DIFERENTE
                    if (listaValore.getTipoPagamento().getId() == 3 || listaValore.getTipoPagamento().getId() == 4 || listaValore.getTipoPagamento().getId() == 5 || listaValore.getTipoPagamento().getId() == 6 || listaValore.getTipoPagamento().getId() == 7) {
                        GenericaMensagem.error("Ação não permitida", "Não é possível adicionar Contate o Administrador!");
                        return;
                    }
                }

                // PLANO DEFAULT
                Plano5 pl = db.pesquisaPlano5IDContaBanco(Integer.valueOf(listaBanco.get(idBanco).getDescription()));
                Plano5 pl_conciliacao = null;
                Date dt_conciliacao = null;
                Date dt_ocorrencia = null;

                // QUANDO RECEBIMENTO TIPO
                // 8;"Depósito Bancário" 9;"DOC / TED" 10;"Trans. Bancária"
                if (!getEs().isEmpty() && getEs().equals("E")) {
                    if (tipoPagamento.getId() == 2 || tipoPagamento.getId() == 8 || tipoPagamento.getId() == 9 || tipoPagamento.getId() == 10 || tipoPagamento.getId() == 13) {
                        if (dataOcorrencia == null) {
                            GenericaMensagem.error("Atenção", "Digite uma data de ocorrência!");
                            return;
                        }

                        if (DataHoje.maiorData(dataOcorrencia, DataHoje.dataHoje())) {
                            GenericaMensagem.error("Atenção", "Data de Ocorrência não pode ser maior que hoje!");
                            dataOcorrencia = null;
                            return;
                        }

                        // ALTERAÇÃO DE PLANO PARA CONCILIAÇÃO
                        pl = (Plano5) new Dao().find(new Plano5(), 1);
                        pl_conciliacao = db.pesquisaPlano5IDContaBanco(Integer.valueOf(listaBanco.get(idBanco).getDescription()));
                        dt_conciliacao = dataConciliacao;
                        dt_ocorrencia = dataOcorrencia;
                    }
                }

                listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, pl, null, null, null, Moeda.converteR$Double(valorDigitado), null, pl_conciliacao, dt_conciliacao, dt_ocorrencia, null));
                numero = "";
                dataConciliacao = null;
                dataOcorrencia = null;
                break;
            default:
                if (!listaValores.isEmpty() && Moeda.converteUS$(total) == valorGrid) {
                    GenericaMensagem.error("Atenção", "Os valores já conferem!");
                    return;
                }

                if (plano5.getId() == -1) {
                    GenericaMensagem.error("Erro", "Nenhum Plano5 Encontrado!");
                    return;
                }

                for (ListValoresBaixaGeral listaValore : listaValores) {
                    // NÃO PODE ADIOCIONAR ESSES TIPOS JUNTO COM BOL/DEP BAN/DOC etc DEVIDO A DATA DE OCORRÊNCIA PODER SER DIFERENTE
                    if (listaValore.getTipoPagamento().getId() == 2 || listaValore.getTipoPagamento().getId() == 8 || listaValore.getTipoPagamento().getId() == 9 || listaValore.getTipoPagamento().getId() == 10 || listaValore.getTipoPagamento().getId() == 13 || listaValore.getTipoPagamento().getId() == 13) {
                        GenericaMensagem.error("Ação não permitida", "Não é possível adicionar Contate o Administrador!");
                        return;
                    }
                }
                listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, plano5, null, null, null, Moeda.converteR$Double(valorDigitado), null, null, null, DataHoje.dataHoje(), null));
                break;
        }

        desHabilitaConta = true;
        desHabilitaQuitacao = true;

        verificaValorDigitado();
        valor = Moeda.converteR$Double(Moeda.subtracao(Moeda.converteUS$(total), somaValoresGrid()));
    }

    public String remover(int index) {
        listaValores.remove(index);
        double soma = somaValoresGrid();
        double valorF = Moeda.converteStringToDouble(valor);
        double totalF = Moeda.converteStringToDouble(total);
        if ((Moeda.soma(soma, valorF) < totalF) || (soma == 0)) {
            valorF = Moeda.subtracao(totalF, soma);
        } else {
            valorF = 0;
        }
        setValor(Moeda.converteR$Double(valorF));

        verificaValorDigitado();
        return null;
    }

    public List<SelectItem> getListaConta() {
        if (listaConta.isEmpty()) {
            ContaRotinaDao db = new ContaRotinaDao();
            List select;
            if (!verificaBaixaBoleto()) {
                select = db.pesquisaContasPorRotina(1);
            } else {
                select = db.pesquisaContasPorRotina();
            }

            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaConta.add(new SelectItem(
                            i,
                            (String) ((Plano5) select.get(i)).getConta(),
                            Integer.toString(((Plano5) select.get(i)).getId())));
                }
            } else {
                listaConta.add(new SelectItem(0, "Nenhuma Conta Encontrada", ""));
            }
        }
        return listaConta;
    }

    public List<SelectItem> getListaTipoPagamento() {
        if (listaTipoPagamento.isEmpty()) {
            Dao dao = new Dao();
            ContaRecebimentoDao dao_cr = new ContaRecebimentoDao();

            List<TipoPagamento> select;
            if (!verificaBaixaBoleto()) {
                if (Moeda.converteUS$(total) != 0) {
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        select = dao.find("TipoPagamento", new int[]{3, 4, 5, 8, 9, 10, 13, 15});
                        idTipoPagamento = 0;
                    } else if (tipo.equals("caixa")) {
                        select = dao_cr.listaTipoPagamentoBaixa("2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13");
                        idTipoPagamento = 1;
                    } else {
                        select = dao_cr.listaTipoPagamentoBaixa("2, 8, 9, 10, 11, 13");
                        idTipoPagamento = 0;
                    }
                } else {
                    select = dao_cr.listaTipoPagamentoBaixa("3");
                    idTipoPagamento = 0;
                }
            } else {
                select = dao_cr.listaTipoPagamentoBaixa("2");
                idTipoPagamento = 0;
            }

            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaTipoPagamento.add(new SelectItem(
                            i,
                            select.get(i).getDescricao(),
                            Integer.toString(select.get(i).getId())
                    ));
                }
            } else {
                listaTipoPagamento.add(new SelectItem(0, "Nenhum tipo de pagamento Encontrado", ""));
            }
        }
        return listaTipoPagamento;
    }

    private Rotina getRotina() {
        if (rotina == null) {
            HttpServletRequest paginaRequerida = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String urlDestino = paginaRequerida.getRequestURI();
            rotina = new RotinaDao().pesquisaRotinaPermissao(urlDestino);
        }
        return rotina;
    }

    public String baixar() {
        GenericaSessao.remove("baixa_geral_sucesso");
        if (listaValores.isEmpty()) {
            return mensagem = "Lista esta vazia!";
        }

        MacFilial macFilial = (MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial");
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario");
        Filial filial = macFilial.getFilial();

        String m = retornaCaixa();
        if (!m.isEmpty()) {
            return m;
        }

        if (Moeda.converteUS$(valor) > 0) {
            return mensagem = "Complete as parcelas para que o Valor seja zerado!";
        } else if (Moeda.converteUS$(valor) < 0) {
            return mensagem = "Erro com o campo valor!";
        }

        MovimentoDao db = new MovimentoDao();
        if (!verificaBaixaBoleto()) {
            if (getListaConta().size() == 1 && getListaConta().get(0).getDescription().isEmpty()) {
                return mensagem = "Lista de Planos Vazia, verificar Conta Rotina!";
            }
        } else {
            Boleto bol = db.pesquisaBoletos(listaMovimentos.get(0).getNrCtrBoleto());
            if (bol == null) {
                return mensagem = "Não existe conta banco para baixar este boleto!";
            }
        }

        if (DataHoje.converte(quitacao) == null) {
            quitacao = DataHoje.data();
        }

        List<FormaPagamento> lfp = new ArrayList();

        for (int i = 0; i < listaValores.size(); i++) {
            double valor_baixa = Moeda.converteUS$(String.valueOf(listaValores.get(i).getValor()));

            ContaTipoPagamento ctp = new ContaRecebimentoDao().pesquisaContaTipoPagamento(listaValores.get(i).getTipoPagamento().getId());

            // CHEQUE
            switch (listaValores.get(i).getTipoPagamento().getId()) {
                case 1:
                    // NOTA FISCAL
                    lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, null, 0, listaValores.get(i).getStatus(), 0, null, null, listaValores.get(i).getNumero(), false));
                    break;
                case 2:
                    // BOLETO
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        // VALOR DE ACORDO COM O CHAMADO #2352

                        ContaCobranca cc = new ContaCobrancaDao().pesquisaContaCobrancaMovimento(listaMovimentos.get(0).getId());
                        // antes
                        //Boleto bol = db.pesquisaBoletos(listaMovimentos.get(0).getNrCtrBoleto());
                        if (cc == null) {
                            return mensagem = "Erro ao Pesquisar Conta Cobrança!";
                        }

                        Double valor_liquido = Moeda.multiplicar(valor_baixa, cc.getRepasse() / 100);

                        Date data_credito = DataHoje.converte(new DataHoje().incrementarDias(1, DataHoje.converteData(listaValores.get(i).getDataOcorrencia())));

                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, ctp.getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_liquido, data_credito, 0, listaValores.get(i).getStatus(), 0, listaValores.get(i).getConciliacaoPlano5(), null, listaValores.get(i).getNumero(), false));
                    }
                    break;
                case 3:
                    // DINHEIRO
                    lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    break;
                case 4:

                    if (!valorAcrescimo.isEmpty() && !confirmaAcrescimo){
                        return mensagem = "É NECESSÁRIO CONFIRMAR ESSE ACRÉSCIMO!";
                    }
                    
                    // CHEQUE
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, listaValores.get(i).getChequePag(), 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, listaValores.get(i).getChequeRec(), null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, null, 0, listaValores.get(i).getStatus(), 0, null, null, listaValores.get(i).getNumero(), false));
                    }
                    break;
                case 5:
                    // CHEQUE-PRÉ
                    if (!valorAcrescimo.isEmpty() && !confirmaAcrescimo){
                        return mensagem = "É NECESSÁRIO CONFIRMAR ESSE ACRÉSCIMO!";
                    }
                    
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, listaValores.get(i).getChequePag(), 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, listaValores.get(i).getChequeRec(), null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, null, 0, listaValores.get(i).getStatus(), 0, null, null, listaValores.get(i).getNumero(), false));
                    }
                    break;
                case 6:
                case 7:
                    // CARTAO - CRÉDITO / DÉBITO
                    Cartao cartao = listaValores.get(i).getCartao();
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, cartao.getPlano5Baixa(), listaValores.get(i).getCartaoPag(), null, listaValores.get(i).getTipoPagamento(), 0, listaValores.get(i).getDataCreditoCartao(), Moeda.divisao(Moeda.multiplicar(valor_baixa, cartao.getTaxa()), 100), listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, cartao.getPlano5Baixa(), null, listaValores.get(i).getCartaoRec(), listaValores.get(i).getTipoPagamento(), 0, listaValores.get(i).getDataCreditoCartao(), Moeda.divisao(Moeda.multiplicar(valor_baixa, cartao.getTaxa()), 100), listaValores.get(i).getStatus(), 0, cartao.getPlano5(), null, "", false));
                    }
                    break;
                case 8:
                    // DEPÓSITO BANCARIO
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, ctp.getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, DataHoje.dataHoje(), 0, listaValores.get(i).getStatus(), 0, listaValores.get(i).getConciliacaoPlano5(), null, "", false));
                    }
                    break;
                case 9:
                    // DOC / TED
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, ctp.getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, DataHoje.dataHoje(), 0, listaValores.get(i).getStatus(), 0, listaValores.get(i).getConciliacaoPlano5(), null, listaValores.get(i).getNumero(), false));
                    }
                    break;
                case 10:
                    // TRANS. BANCÁRIA
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, ctp.getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, DataHoje.dataHoje(), 0, listaValores.get(i).getStatus(), 0, listaValores.get(i).getConciliacaoPlano5(), null, listaValores.get(i).getNumero(), false));
                    }
                    break;
                case 13:
                    // DÉBITO AUTOMÁTICO
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, ctp.getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, null, 0, listaValores.get(i).getStatus(), 0, listaValores.get(i).getConciliacaoPlano5(), null, listaValores.get(i).getNumero(), false));
                    }
                    break;
                default:
                    // OUTROS
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0, listaValores.get(i).getStatus(), 0, null, null, "", false));
                    } else {
                        lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, ctp.getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), valor_baixa, null, 0, listaValores.get(i).getStatus(), 0, listaValores.get(i).getPlano5(), null, listaValores.get(i).getNumero(), false));
                    }
                    break;
            }
        }

        for (int i = 0; i < listaMovimentos.size(); i++) {
            listaMovimentos.get(i).setTaxa(Moeda.converteUS$(taxa));
        }

        double vl = (!valorTroco.isEmpty()) ? Moeda.converteUS$(valorTroco) : 0;
        double vl_acres = (!valorAcrescimo.isEmpty()) ? Moeda.converteUS$(valorAcrescimo) : 0;

        StatusRetornoMensagem sr = GerarMovimento.baixarMovimentoManual(listaMovimentos, usuario, lfp, Moeda.converteStringToDouble(total), quitacao, caixa, vl, listaValores.get(0).getDataOcorrencia(), vl_acres);

        if (!sr.getStatus()) {
            mensagem = "Erro ao baixar! " + sr.getMensagem();
            return null;
        } else {
            Dao dao = new Dao();
            Boolean erro_baixa = false;
            for (int i = 0; i < listaMovimentos.size(); i++) {
                if (listaMovimentos.get(i).getId() == -1) {
                    mensagem = "Erro ao baixar (MOVIMENTO)!";
                    erro_baixa = true;
                    break;
                }
                if (listaMovimentos.get(i).getBaixa() == null || listaMovimentos.get(i).getBaixa().getId() == -1) {
                    mensagem = "Erro ao baixar (BAIXA)!";
                    erro_baixa = true;
                    break;
                } else {
                    Baixa b = (Baixa) dao.find(new Baixa(), listaMovimentos.get(i).getBaixa().getId());
                    if (b == null) {
                        erro_baixa = true;
                        mensagem = "Erro ao baixar (BAIXA IS NULL)!";
                        break;

                    }
                }
            }

            if (erro_baixa) {
                new NovoLog().save("");
                for (int i = 0; i < listaMovimentos.size(); i++) {
                    if (listaMovimentos.get(i).getBaixa() != null || listaMovimentos.get(i).getBaixa().getId() == -1) {
                        listaMovimentos.get(i).setBaixa(null);
                    }
                }
                return mensagem;
            }

            listaValores.clear();
            total = "0.0";
            String url = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
            GenericaSessao.put("linkClicado", true);
            switch (url) {
                case "baixaBoleto":
                    ((BaixaBoletoBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("baixaBoletoBean")).loadListaBoleto();
                    break;

                case "movimentosReceberSocial":
                    ((MovimentosReceberSocialBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("movimentosReceberSocialBean")).getListaMovimento().clear();
                    break;

                case "emissaoGuias":
                case "menuPrincipal":
                    break;

                case "lancamentoFinanceiro":
                    ((LancamentoFinanceiroBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("lancamentoFinanceiroBean")).getListaParcela().clear();
                    ((LancamentoFinanceiroBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("lancamentoFinanceiroBean")).getListaParcelaSelecionada().clear();
                    break;

                case "matriculaAcademia":
                    ((MatriculaAcademiaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("matriculaAcademiaBean")).getListaMovimentos().clear();
                    ((MatriculaAcademiaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("matriculaAcademiaBean")).setDesabilitaCamposMovimento(true);
                    ((MatriculaAcademiaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("matriculaAcademiaBean")).setDesabilitaDiaVencimento(true);
                    break;

                case "contasAPagar":
                    ((ContasAPagarBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("contasAPagarBean")).loadListaContas();
                    break;

                default:
                    break;
            }

            ((EmissaoGuiasBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emissaoGuiasBean")).atualizarHistorico();

            retorna = true;
            mensagem = "Baixa realizada com sucesso!";
            GenericaSessao.put("baixa_sucesso", true);
            visibleModal = true;
            visibleImprimirRecibo = true;
        }
        return null;
    }

    public String retornaCaixa() {
        MacFilial macFilial = (MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial");
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario");

        Filial filial;

        try {
            filial = macFilial.getFilial();
        } catch (Exception e) {
            return mensagem = "Não é foi possível encontrar a filial no sistema!";
        }

        if (!macFilial.getCaixaOperador()) {
            if (tipo.equals("caixa")) {
                if (macFilial.getCaixa() == null) {
                    caixa = new Caixa();
                    return mensagem = "Não é possivel salvar baixa sem um caixa definido para esta estação!";
                }

                caixa = macFilial.getCaixa();
            }
        } else {
            FinanceiroDao dao = new FinanceiroDao();
            caixa = dao.pesquisaCaixaUsuario(usuario.getId(), filial.getId());

            if (tipo.equals("caixa")) {
                if (caixa == null) {
                    caixa = new Caixa();
                    return mensagem = "Não é possivel salvar baixa sem um caixa/operador definido!";
                }
            }
        }

        return "";
    }

    public void imprimirRecibo() {
        if (!listaMovimentos.isEmpty()) {
            Dao dao = new Dao();
            for (int i = 0; i < listaMovimentos.size(); i++) {
                try {
                    Movimento m = (Movimento) dao.rebind(dao.find(listaMovimentos.get(i)));
                    if (m == null) {
                        mensagem = "Não é possivel salvar baixa sem um caixa/operador definido!";
                        return;
                    }
                    if (m.getBaixa() == null) {
                        mensagem = "MOVIMENTO NÃO BAIXADO!";
                        if (listaMovimentos.get(i).getBaixa() != null) {
                            mensagem = "MOVIMENTO NÃO BAIXADO! INFORMAR CÓDIGO DA BAIXA AO ADMINISTRADOR! CÓDIGO: " + listaMovimentos.get(i).getBaixa().getId();
                            // SE O ERRO DE PERSISTIR LIBERAR O BLOCO ABAIXO
                            /*
                            dao.openTransaction();
                            List<FormaPagamento> listFP = new FormaPagamentoDao().findByBaixa(listaMovimentos.get(i).getBaixa().getId());
                            for (int z = 0; z < listFP.size(); z++) {
                                if (!dao.delete(listFP.get(z))) {
                                    dao.rollback();
                                    return;
                                }
                            }
                            if (!dao.delete(listaMovimentos.get(i).getBaixa())) {
                                dao.rollback();
                                return;
                            }
                             */
                        }
                        return;
                    }
                } catch (Exception e) {
                    mensagem = e.getMessage();
                    return;
                }
            }

            Map map = new HashMap();
            if (!dataEmissaoRecibo.isEmpty()) {
                map.put("data_emissao", dataEmissaoRecibo);
            }
            if (!getObs().isEmpty()) {
                map.put("obs", obs);
            } else {
                map.put("obs", "");
            }

            ImprimirRecibo ir = new ImprimirRecibo();

            Boolean stat;

            if (!GenericaSessao.exists("tipo_recibo_imprimir")) {
                stat = ir.gerar_recibo(listaMovimentos.get(0).getId(), map);
            } else if (((TipoRecibo) GenericaSessao.getObject("tipo_recibo_imprimir")).getId() == 1) {
                stat = ir.gerar_recibo(listaMovimentos.get(0).getId(), map);
            } else {
                stat = ir.gerar_recibo_generico(listaMovimentos, null);
            }

            if (stat) {
                ir.imprimir();
            }

        }
    }

    public String getQuitacao() {
        return quitacao;
    }

    public void setQuitacao(String quitacao) {
        this.dataEmissaoRecibo = quitacao;
        this.quitacao = quitacao;
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.converteR$(valor);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public Integer getIdConta() {
        return idConta;
    }

    public void setIdConta(Integer idConta) {
        this.idConta = idConta;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public int getIdTipoPagamento() {
        return idTipoPagamento;
    }

    public void setIdTipoPagamento(int idTipoPagamento) {
        this.idTipoPagamento = idTipoPagamento;
    }

    private void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Modulo getModulo() {
        if (modulo.getId() == -1) {
            modulo = (Modulo) new Dao().find(new Modulo(), 3);
        }
        return modulo;
    }

    private void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public boolean isDesHabilitaConta() {
        if ((!listaValores.isEmpty()) || (verificaBaixaBoleto())) {
            desHabilitaConta = true;
        } else {
            desHabilitaConta = false;
        }
        return desHabilitaConta;
    }

    public boolean verificaBaixaBoleto() {
        String urlRetorno = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
        return urlRetorno.equals("baixaBoleto") && tipo.equals("banco");
    }

    public void setDesHabilitaConta(boolean desHabilitaConta) {
        this.desHabilitaConta = desHabilitaConta;
    }

    public boolean isDesHabilitaQuitacao() {
        if (tipo.equals("banco")) {
            desHabilitaQuitacao = false;
        } else // TRUE = não tem permissão
        if (cab.verificaPermissao("alterar_data_quitacao_caixa", 3)) {
            desHabilitaQuitacao = true;
        } else {
            desHabilitaQuitacao = false;
        }
        return desHabilitaQuitacao;
    }

    public void setDesHabilitaQuitacao(boolean desHabilitaQuitacao) {
        this.desHabilitaQuitacao = desHabilitaQuitacao;
    }

    public boolean isDesHabilitaNumero() {
        if (tipoPagamento.getId() == 3 || tipoPagamento.getId() == 6 || tipoPagamento.getId() == 7 || (!getEs().isEmpty() && getEs().equals("S"))) {
            desHabilitaNumero = true;
            numero = "";
        } else {
            desHabilitaNumero = false;
        }
        return desHabilitaNumero;
    }

    public void setDesHabilitaNumero(boolean desHabilitaNumero) {
        this.desHabilitaNumero = desHabilitaNumero;
    }

    public boolean isDesHabilitadoVencimento() {
        if (tipoPagamento.getId() == 5) {
            desHabilitadoVencimento = false;
        } else {
            vencimento = quitacao;
            desHabilitadoVencimento = true;
        }
        return desHabilitadoVencimento;
    }

    public void setDesHabilitadoVencimento(boolean desHabilitadoVencimento) {
        this.desHabilitadoVencimento = desHabilitadoVencimento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Plano5 getPlano5() {
        if (plano5.getId() == -1 && !listaMovimentos.isEmpty()) {
            if (verificaBaixaBoleto()) {
                plano5 = listaMovimentos.get(0).getPlano5();
            } else if (tipo.equals("caixa")) {
                plano5 = (Plano5) new Dao().find(new Plano5(), Integer.parseInt(((SelectItem) getListaConta().get(getIdConta())).getDescription()));
            } else {
                MovimentoDao db = new MovimentoDao();
                Boleto bol = db.pesquisaBoletos(listaMovimentos.get(0).getNrCtrBoleto());
                if (bol == null) {
                    return plano5;
                }

                plano5 = new Plano5Dao().pesquisaPlano5IDContaBanco(bol.getContaCobranca().getContaBanco().getId());
            }
        }
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public List<Movimento> getListaMovimentos() {
        if (listaMovimentos.isEmpty()) {
            if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("listaMovimento") != null) {
                listaMovimentos = (List) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("listaMovimento");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("listaMovimento");
                double valorTotal = 0;

                if (total.equals("0,00")) {
                    for (int i = 0; i < listaMovimentos.size(); i++) {
                        valorTotal = Moeda.soma(valorTotal, listaMovimentos.get(i).getValorBaixa());
                    }
                    total = Moeda.converteR$Double(valorTotal);
                    valor = total;
                }
            }
        }
        return listaMovimentos;
    }

    public void setListaMovimentos(List<Movimento> listaMovimentos) {
        this.listaMovimentos = listaMovimentos;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTipo() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("caixa_banco") != null) {
            tipo = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("caixa_banco");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("caixa_banco");
        }
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     *
     * @param tipo (Tipos: caixa)
     */
    public static void listenerTipoCaixaSession(String tipo) {
        if (tipo != null && !tipo.isEmpty()) {
            GenericaSessao.put("caixa_banco", tipo);
        }
    }

    public String getBanco() {
        if (banco.isEmpty()) {
            MovimentoDao db = new MovimentoDao();
            ImprimirBoleto imp = new ImprimirBoleto();
            Boleto bol = db.pesquisaBoletos(listaMovimentos.get(0).getNrCtrBoleto());

            if (bol == null) {
                listaMovimentos = imp.atualizaContaCobrancaMovimento(listaMovimentos);
            }

            bol = db.pesquisaBoletos(listaMovimentos.get(0).getNrCtrBoleto());
            if (bol != null) {
                banco = bol.getContaCobranca().getContaBanco().getConta() + " / " + bol.getContaCobranca().getContaBanco().getBanco().getBanco();
                if (!getListaBanco().isEmpty()) {
                    for (int i = 0; i < listaBanco.size(); i++) {
                        if (bol.getContaCobranca().getContaBanco().getId() == Integer.valueOf(listaBanco.get(i).getDescription())) {
                            idBanco = i;
                        }
                    }
                }
            } else {
                banco = "BANCO";
            }
        }
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public ChequeRec getChequeRec() {
        return chequeRec;
    }

    public void setChequeRec(ChequeRec chequeRec) {
        this.chequeRec = chequeRec;
    }

    public String getTaxa() {
        return Moeda.converteR$(taxa);
    }

    public void setTaxa(String taxa) {
        this.taxa = Moeda.substituiVirgula(taxa);
    }

    public boolean isRetorna() {
        return retorna;
    }

    public void setRetorna(boolean retorna) {
        this.retorna = retorna;
    }

    public void alteraVencimento() {
        int quitacaoInteiro = DataHoje.converteDataParaRefInteger(quitacao);
        int vencimentoInteiro = DataHoje.converteDataParaRefInteger(vencimento);
        if (quitacaoInteiro != vencimentoInteiro) {
            vencimento = quitacao;
        }
    }

    public List<SelectItem> getListaCartao() {
        if (listaCartao.isEmpty()) {
            List<Cartao> result = new Dao().list(new Cartao());
            int conta = 0;
            if (!result.isEmpty()) {
                for (Cartao result1 : result) {
                    String tipox = result1.getDebitoCredito().equals("D") ? "Débito" : "Crédito";
                    if (tipoPagamento.getId() == 6 && result1.getDebitoCredito().equals("C")) {
                        listaCartao.add(new SelectItem(conta, result1.getDescricao() + " - " + tipox, Integer.toString(result1.getId())));
                        conta++;
                    } else if (tipoPagamento.getId() == 7 && result1.getDebitoCredito().equals("D")) {
                        listaCartao.add(new SelectItem(conta, result1.getDescricao() + " - " + tipox, Integer.toString(result1.getId())));
                        conta++;
                    }
                }
            } else {
                listaCartao.add(new SelectItem(0, "Nenhum Cartão Encontrado", ""));
            }
        }
        return listaCartao;
    }

    public void setListaCartao(List<SelectItem> listaCartao) {
        this.listaCartao = listaCartao;
    }

    public int getIdCartao() {
        return idCartao;
    }

    public void setIdCartao(int idCartao) {
        this.idCartao = idCartao;
    }

    public List<SelectItem> getListaBanco() {
        if (listaBanco.isEmpty()) {
            List<ContaBanco> result = new ArrayList();
            if (verificaBaixaBoleto()) {
                MovimentoDao db = new MovimentoDao();
                Boleto bol = db.pesquisaBoletos(listaMovimentos.get(0).getNrCtrBoleto());
                result.add(bol.getContaCobranca().getContaBanco());
            } else {
                result = new Dao().list(new ContaBanco(), true);
            }

            if (!result.isEmpty()) {
                for (int i = 0; i < result.size(); i++) {
                    listaBanco.add(new SelectItem(i, result.get(i).getAgencia() + " " + result.get(i).getConta() + " - " + result.get(i).getBanco().getBanco(), Integer.toString(result.get(i).getId())));
                }
            } else {
                listaBanco.add(new SelectItem(0, "Nenhum Banco Encontrado", ""));
            }
        }
        return listaBanco;
    }

    public void setListaBanco(List<SelectItem> listaBanco) {
        this.listaBanco = listaBanco;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getEs() {
        if (GenericaSessao.exists("esMovimento")) {
            es = String.valueOf(GenericaSessao.getString("esMovimento", true));
        }
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getNumeroChequePag() {
        return numeroChequePag;
    }

    public void setNumeroChequePag(String numeroChequePag) {
        this.numeroChequePag = numeroChequePag;
    }

    public List<SelectItem> getListaBancoSaida() {
        if (listaBancoSaida.isEmpty()) {
            List<ContaBanco> result = new Dao().list(new ContaBanco(), true);

            if (!result.isEmpty()) {
                for (int i = 0; i < result.size(); i++) {
                    listaBancoSaida.add(new SelectItem(i, result.get(i).getAgencia() + " " + result.get(i).getConta() + " - " + result.get(i).getBanco().getBanco(), Integer.toString(result.get(i).getId())));
                }
            } else {
                listaBancoSaida.add(new SelectItem(0, "Nenhum Banco Encontrado", ""));
            }
        }

        if (!getEs().isEmpty() && getEs().equals("S")) {
            if (listaBancoSaida.get(idBancoSaida).getDescription().isEmpty()) {
                return listaBancoSaida;
            }

            ContaBanco cb = (ContaBanco) new Dao().find(new ContaBanco(), Integer.valueOf(listaBancoSaida.get(idBancoSaida).getDescription()));
            cb = (ContaBanco) new Dao().rebind(cb);
            numeroChequePag = String.valueOf(cb.getUCheque() + 1);
        }
        return listaBancoSaida;
    }

    public void setListaBancoSaida(List<SelectItem> listaBancoSaida) {
        this.listaBancoSaida = listaBancoSaida;
    }

    public int getIdBancoSaida() {
        return idBancoSaida;
    }

    public void setIdBancoSaida(int idBancoSaida) {
        this.idBancoSaida = idBancoSaida;
    }

    public void setListaConta(List<SelectItem> listaConta) {
        this.listaConta = listaConta;
    }

    public void setListaTipoPagamento(List<SelectItem> listaTipoPagamento) {
        this.listaTipoPagamento = listaTipoPagamento;
    }

    public boolean isVisibleModal() {
        return visibleModal;
    }

    public void setVisibleModal(boolean visibleModal) {
        this.visibleModal = visibleModal;
    }

    public String getValorTroco() {
        return valorTroco;
    }

    public void setValorTroco(String valorTroco) {
        this.valorTroco = valorTroco;
    }

    public List<ListValoresBaixaGeral> getListaValores() {
        return listaValores;
    }

    public void setListaValores(List<ListValoresBaixaGeral> listaValores) {
        this.listaValores = listaValores;
    }

    public String getValorEditavel() {
        return valorEditavel;
    }

    public void setValorEditavel(String valorEditavel) {
        this.valorEditavel = valorEditavel;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public static String submitStatic(List<Movimento> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        GenericaSessao.put("listaMovimento", list);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
    }

    public String submit(List<Movimento> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        GenericaSessao.put("listaMovimento", list);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
    }

    public static String submitStatic(List<Movimento> list, String tipo) {
        listenerTipoCaixaSession(tipo);
        if (list == null || list.isEmpty()) {
            return null;
        }
        GenericaSessao.put("listaMovimento", list);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
    }

    public String submit(List<Movimento> list, String tipo) {
        listenerTipoCaixaSession(tipo);
        if (list == null || list.isEmpty()) {
            return null;
        }
        GenericaSessao.put("listaMovimento", list);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public String getObs() {
        if (GenericaSessao.exists("mensagem_recibo")) {
            obs = GenericaSessao.getString("mensagem_recibo", true);
        }
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public List<SelectItem> getListaTodosBancos() {
        return listaTodosBancos;
    }

    public void setListaTodosBancos(List<SelectItem> listaTodosBancos) {
        this.listaTodosBancos = listaTodosBancos;
    }

    public Integer getIndexListaTodosBancos() {
        return indexListaTodosBancos;
    }

    public void setIndexListaTodosBancos(Integer indexListaTodosBancos) {
        this.indexListaTodosBancos = indexListaTodosBancos;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public String getDataConciliacaoString() {
        return DataHoje.converteData(dataConciliacao);
    }

    public void setDataConciliacaoString(String dataConciliacaoString) {
        this.dataConciliacao = DataHoje.converte(dataConciliacaoString);
    }

    public String getDataEmissaoRecibo() {
        return dataEmissaoRecibo;
    }

    public void setDataEmissaoRecibo(String dataEmissaoRecibo) {
        this.dataEmissaoRecibo = dataEmissaoRecibo;
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

    public String recibo2Via(Integer movimento_id) {
        return recibo2Via((Movimento) new Dao().find(new Movimento(), movimento_id));
    }

    public String recibo2Via(Movimento mov) {
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

    public Boolean getVisibleImprimirRecibo() {
        return visibleImprimirRecibo;
    }

    public void setVisibleImprimirRecibo(Boolean visibleImprimirRecibo) {
        this.visibleImprimirRecibo = visibleImprimirRecibo;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getDataOcorrenciaString() {
        return DataHoje.converteData(dataOcorrencia);
    }

    public void setDataOcorrenciaString(String dataOcorrenciaString) {
        this.dataOcorrencia = DataHoje.converte(dataOcorrenciaString);
    }

    public Date getDataCreditoCartao() {
        return dataCreditoCartao;
    }

    public void setDataCreditoCartao(Date dataCreditoCartao) {
        this.dataCreditoCartao = dataCreditoCartao;
    }

    public String getDataCreditoCartaoString() {
        return DataHoje.converteData(dataCreditoCartao);
    }

    public void setDataCreditoCartaoString(String dataCreditoCartaoString) {
        this.dataCreditoCartao = DataHoje.converte(dataCreditoCartaoString);
    }

    public String getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(String valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public boolean isConfirmaAcrescimo() {
        return confirmaAcrescimo;
    }

    public void setConfirmaAcrescimo(boolean confirmaAcrescimo) {
        this.confirmaAcrescimo = confirmaAcrescimo;
    }
}
