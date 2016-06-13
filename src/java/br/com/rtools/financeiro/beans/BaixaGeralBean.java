package br.com.rtools.financeiro.beans;

import br.com.rtools.academia.beans.MatriculaAcademiaBean;
import br.com.rtools.arrecadacao.beans.BaixaBoletoBean;
import br.com.rtools.associativo.beans.EmissaoGuiasBean;
import br.com.rtools.associativo.beans.MovimentosReceberSocialBean;
import br.com.rtools.associativo.beans.VendaBaileBean;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.Cartao;
import br.com.rtools.financeiro.CartaoPag;
import br.com.rtools.financeiro.CartaoRec;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.dao.ContaRotinaDao;
import br.com.rtools.financeiro.db.FinanceiroDB;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.financeiro.dao.LancamentoFinanceiroDao;
import br.com.rtools.financeiro.db.MovimentoDB;
import br.com.rtools.financeiro.db.MovimentoDBToplink;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.financeiro.lista.ListValoresBaixaGeral;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.movimento.ImprimirRecibo;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Modulo;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
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
    private String es = "";
    private ChequeRec chequeRec = new ChequeRec();
    private boolean visibleModal = false;
    private final ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();

    private String valorTroco = "";
    private String valorEditavel = "";
    private TipoPagamento tipoPagamento;
    private Caixa caixa = new Caixa();
    private String obs;

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
    }

    public void verificaValorDigitado() {
        float valor_troco = 0;
        boolean dinheiro = false;
        for (int i = 0; i < listaValores.size(); i++) {
            if (listaValores.get(i).getTipoPagamento().getId() == 3) {
                dinheiro = true;
            }
        }

        if (dinheiro) {
            for (int i = 0; i < listaValores.size(); i++) {
                if (listaValores.get(i).getTipoPagamento().getId() == 3) {
                    float valorx = Moeda.converteUS$(listaValores.get(i).getValor());
                    float valordigitado = Moeda.converteUS$(listaValores.get(i).getValorDigitado());
                    if (valorx < valordigitado) {
                        valor_troco = Moeda.subtracaoValores(valordigitado, valorx);
                        valorTroco = Moeda.converteR$Float(valor_troco);
                    }
                }
            }
        } else {
            valorTroco = "";
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
            if (url.equals("baixaBoleto")) {
                ((BaixaBoletoBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("baixaBoletoBean")).loadListaBoleto();
                return "baixaBoleto";
            } else if (url.equals("movimentosReceberSocial")) {
                ((MovimentosReceberSocialBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("movimentosReceberSocialBean")).getListaMovimento().clear();
                return "movimentosReceberSocial";
            } else if (url.equals("lancamentoFinanceiro")) {
                return "lancamentoFinanceiro";
            } else if (url.equals("emissaoGuias")) {
                return "emissaoGuias";
            } else if (url.equals("matriculaAcademia")) {
                return "matriculaAcademia";
            } else if (url.equals("menuPrincipal")) {
                return "menuPrincipal";
            } else if (url.equals("acessoNegado")) {
                return "menuPrincipal";
            } else if (url.equals("geracaoDebitosCartao")) {
                GenericaSessao.put("lista_movimentos_baixados", listaMovimentos);
                return "geracaoDebitosCartao";
            } else if (url.equals("matriculaEscola")) {
                return "matriculaEscola";
            } else if (url.equals("vendasBaile")) {
                ((VendaBaileBean) GenericaSessao.getObject("vendaBaileBean")).novo();
                return "vendasBaile";
            } else if (url.equals("conviteMovimento")) {
                return "conviteMovimento";
            } else if (url.equals("devolucaoFilme")) {
                return "devolucaoFilme";
            } else {
                return "menuPrincipal";
            }
        } else {
            return null;
        }
    }

    private float somaValoresGrid() {
        float soma = 0;
        for (ListValoresBaixaGeral listaValore : listaValores) {
            soma = Moeda.somaValores(soma,
                    Float.parseFloat(Moeda.substituiVirgula(String.valueOf(listaValore.getValor())))
            );
        }
        return soma;
    }

    public void inserir() {
        if (Moeda.converteUS$(valor) < 0) {
            GenericaMensagem.error("Atenção", "Valor negativo não é permitido!");
            return;
        }

        float valorGrid = 0;
        int tipo_dinheiro = 0;
        for (ListValoresBaixaGeral listaValore : listaValores) {
            valorGrid = Moeda.somaValores(valorGrid,
                    Float.parseFloat(Moeda.substituiVirgula(String.valueOf(listaValore.getValor())))
            );

            if (listaValore.getTipoPagamento().getId() == 3) {
                tipo_dinheiro++;
            }
        }

        if (tipo_dinheiro >= 1 && tipoPagamento.getId() == 3) {
            GenericaMensagem.error("Atenção", "Dinheiro já adicionado!");
            return;
        }

        if (!listaValores.isEmpty() && Moeda.converteUS$(total) == valorGrid) {
            GenericaMensagem.error("Atenção", "Os valores já conferem!");
            return;
        }

        float valorDigitado = 0;
        valorEditavel = Moeda.converteR$Float(Moeda.subtracaoValores(Moeda.converteUS$(total), valorGrid));

        if (Moeda.converteUS$(valor) > Moeda.converteUS$(valorEditavel)) {
            valorDigitado = Moeda.converteUS$(valor);
            valor = valorEditavel;
        } else {
            valorDigitado = Moeda.converteUS$(valor);
        }

        // CHEQUE
        if (tipoPagamento.getId() == 4 || tipoPagamento.getId() == 5) {
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
                ch_p.setVencimento(vencimento);

//                if (tipo.equals("caixa")) {
//                    ch_p.setStatus((FStatus) (new Dao()).find(new FStatus(), 7));
//                } else {
                ch_p.setStatus((FStatus) (new Dao()).find(new FStatus(), 8));
//                }

                listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numeroChequePag, tipoPagamento, ch_p, null, pl, null, null, null, Moeda.converteR$Float(valorDigitado)));
            } else {
                if (numero.isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Digite um número para o Cheque!");
                    return;
                }

                if (chequeRec.getAgencia().isEmpty() || chequeRec.getBanco().isEmpty() || chequeRec.getConta().isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Agência, Conta e Banco não podem estar vazios!");
                    return;
                }

                ChequeRec ch = new ChequeRec();
                ch.setAgencia(chequeRec.getAgencia());
                ch.setBanco(chequeRec.getBanco());
                ch.setCheque(numero);
                ch.setConta(chequeRec.getConta());
                ch.setEmissao(quitacao);

//                if (tipo.equals("caixa")) {
                ch.setStatus((FStatus) (new Dao()).find(new FStatus(), 7));
//                } else {
//                    ch.setStatus((FStatus) (new Dao()).find(new FStatus(), 8));
//                }

                ch.setVencimento(vencimento);
                if (plano5.getId() == -1) {
                    GenericaMensagem.error("Erro", "Nenhum Plano5 Encontrado!");
                    return;
                }
                listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, ch, plano5, null, null, null, Moeda.converteR$Float(valorDigitado)));
            }

            numero = "";
            numeroChequePag = "";
            chequeRec = new ChequeRec();
        } else if (tipoPagamento.getId() == 6 || tipoPagamento.getId() == 7) {
            // CARTAO
            if (listaCartao.size() == 1 && !listaCartao.get(0).getDescription().isEmpty()) {
                Cartao cart = (Cartao) new Dao().find(new Cartao(), Integer.valueOf(listaCartao.get(idCartao).getDescription()));
                if (!getEs().isEmpty() && getEs().equals("S")) {
                    CartaoPag cartao_pag = null;
                    listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, null, cart, cartao_pag, null, Moeda.converteR$Float(valorDigitado)));
                } else {
                    CartaoRec cartao_rec = new CartaoRec(-1, (FStatus) (new Dao()).find(new FStatus(), 8), null);
                    listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, null, cart, null, cartao_rec, Moeda.converteR$Float(valorDigitado)));
                }
            }
        } else if (tipoPagamento.getId() == 2 || tipoPagamento.getId() == 8 || tipoPagamento.getId() == 9 || tipoPagamento.getId() == 10 || tipoPagamento.getId() == 13) {
            Plano5Dao db = new Plano5Dao();
            if (listaBanco.size() == 1 && listaBanco.get(0).getDescription().isEmpty()) {
                GenericaMensagem.error("Erro", "Nenhum Banco Encontrado!");
                return;
            }
            Plano5 pl = db.pesquisaPlano5IDContaBanco(Integer.valueOf(listaBanco.get(idBanco).getDescription()));
            listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, pl, null, null, null, Moeda.converteR$Float(valorDigitado)));

            numero = "";
        } else {
            if (plano5.getId() == -1) {
                GenericaMensagem.error("Erro", "Nenhum Plano5 Encontrado!");
                return;
            }
            listaValores.add(new ListValoresBaixaGeral(vencimento, valor, numero, tipoPagamento, null, null, plano5, null, null, null, Moeda.converteR$Float(valorDigitado)));
        }
        desHabilitaConta = true;
        desHabilitaQuitacao = true;

        verificaValorDigitado();
        valor = Moeda.converteR$Float(Moeda.subtracaoValores(Moeda.converteUS$(total), somaValoresGrid()));
    }

    public String remover(int index) {
        listaValores.remove(index);
        float soma = somaValoresGrid();
        float valorF = Float.parseFloat(Moeda.substituiVirgula(valor));
        float totalF = Float.parseFloat(Moeda.substituiVirgula(total));
        if ((Moeda.somaValores(soma, valorF) < totalF) || (soma == 0)) {
            valorF = Moeda.subtracaoValores(totalF, soma);
        } else {
            valorF = 0;
        }
        setValor(Moeda.converteR$Float(valorF));

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
            List<TipoPagamento> select = new ArrayList();
            if (!verificaBaixaBoleto()) {
                if (Moeda.converteUS$(total) != 0) {
                    if (!getEs().isEmpty() && getEs().equals("S")) {
                        select = dao.find("TipoPagamento", new int[]{3, 4, 5, 8, 9, 10});
                        idTipoPagamento = 0;
                    } else if (tipo.equals("caixa")) {
                        select = dao.find("TipoPagamento", new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13});
                        idTipoPagamento = 1;
                    } else {
                        select = dao.find("TipoPagamento", new int[]{2, 8, 9, 10, 11, 13});
                        idTipoPagamento = 0;
                    }
                } else {
                    select = dao.find("TipoPagamento", new int[]{3});
                    idTipoPagamento = 0;
                }
            } else {
                select = dao.find("TipoPagamento", new int[]{2});
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

        if (!verificaBaixaBoleto()) {
            if (getListaConta().size() == 1 && getListaConta().get(0).getDescription().isEmpty()) {
                return mensagem = "Lista de Planos Vazia, verificar Conta Rotina!";
            }
        } else {
            MovimentoDB db = new MovimentoDBToplink();
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
            float valor_baixa = Moeda.converteUS$(String.valueOf(listaValores.get(i).getValor()));
            // CHEQUE
            if (listaValores.get(i).getTipoPagamento().getId() == 4 || listaValores.get(i).getTipoPagamento().getId() == 5) {
                if (!getEs().isEmpty() && getEs().equals("S")) {
                    lfp.add(new FormaPagamento(-1, null, null, listaValores.get(i).getChequePag(), 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0));
                } else {
                    lfp.add(new FormaPagamento(-1, null, listaValores.get(i).getChequeRec(), null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0));
                }
            } else if (listaValores.get(i).getTipoPagamento().getId() == 6 || listaValores.get(i).getTipoPagamento().getId() == 7) {
                // CARTAO    
                Cartao cartao = listaValores.get(i).getCartao();
                DataHoje dh = new DataHoje();
                
                if (!getEs().isEmpty() && getEs().equals("S")) {
                    lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, cartao.getPlano5(), listaValores.get(i).getCartaoPag(), null, listaValores.get(i).getTipoPagamento(), 0, dh.converte(dh.incrementarDias(cartao.getDias(), quitacao)), Moeda.divisaoValores(Moeda.multiplicarValores(valor_baixa, cartao.getTaxa()), 100)));
                } else {
                    lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, cartao.getPlano5(), null, listaValores.get(i).getCartaoRec(), listaValores.get(i).getTipoPagamento(), 0, dh.converte(dh.incrementarDias(cartao.getDias(), quitacao)), Moeda.divisaoValores(Moeda.multiplicarValores(valor_baixa, cartao.getTaxa()), 100)));
                }
            } else if (listaValores.get(i).getTipoPagamento().getId() == 8 || listaValores.get(i).getTipoPagamento().getId() == 9 || listaValores.get(i).getTipoPagamento().getId() == 10) {
                // DOC BANCARIO    
                lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, DataHoje.dataHoje(), 0));
            } else {
                // DINHEIRO E OUTROS
                lfp.add(new FormaPagamento(-1, null, null, null, 0, valor_baixa, filial, listaValores.get(i).getPlano5(), null, null, listaValores.get(i).getTipoPagamento(), 0, null, 0));
            }
        }

        for (int i = 0; i < listaMovimentos.size(); i++) {
            listaMovimentos.get(i).setTaxa(Moeda.converteUS$(taxa));
        }

        float vl = (!valorTroco.isEmpty()) ? Moeda.converteUS$(valorTroco) : 0;

        if (!GerarMovimento.baixarMovimentoManual(listaMovimentos, usuario, lfp, Moeda.substituiVirgulaFloat(total), quitacao, caixa, vl)) {
            mensagem = "Erro ao atualizar boleto!";
            return null;
        } else {
            listaValores.clear();
            total = "0.0";
            String url = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
            GenericaSessao.put("linkClicado", true);
            if (url.equals("baixaBoleto")) {
                ((BaixaBoletoBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("baixaBoletoBean")).loadListaBoleto();
            } else if (url.equals("movimentosReceberSocial")) {
                ((MovimentosReceberSocialBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("movimentosReceberSocialBean")).getListaMovimento().clear();
            } else if (url.equals("emissaoGuias") || url.equals("menuPrincipal")) {

            } else if (url.equals("lancamentoFinanceiro")) {
                ((LancamentoFinanceiroBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("lancamentoFinanceiroBean")).getListaParcela().clear();
                ((LancamentoFinanceiroBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("lancamentoFinanceiroBean")).getListaParcelaSelecionada().clear();
            } else if (url.equals("matriculaAcademia")) {
                ((MatriculaAcademiaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("matriculaAcademiaBean")).getListaMovimentos().clear();
                ((MatriculaAcademiaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("matriculaAcademiaBean")).setDesabilitaCamposMovimento(true);
                ((MatriculaAcademiaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("matriculaAcademiaBean")).setDesabilitaDiaVencimento(true);
            }

            ((EmissaoGuiasBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emissaoGuiasBean")).atualizarHistorico();

            retorna = true;
            mensagem = "Baixa realizada com sucesso!";
            GenericaSessao.put("baixa_sucesso", true);
            visibleModal = true;
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

        if (!macFilial.isCaixaOperador()) {
            if (tipo.equals("caixa")) {
                if (macFilial.getCaixa() == null) {
                    caixa = new Caixa();
                    return mensagem = "Não é possivel salvar baixa sem um caixa definido para esta estação!";
                }

                caixa = macFilial.getCaixa();
            }
        } else {
            FinanceiroDB db = new FinanceiroDBToplink();
            caixa = db.pesquisaCaixaUsuario(usuario.getId(), filial.getId());

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
            Map map = new HashMap();
            if (!getObs().isEmpty()) {
                map.put("obs", obs);
            } else {
                map.put("obs", "");
            }
            new ImprimirRecibo().recibo(listaMovimentos.get(0).getId(), map);
        }
    }

    public String getQuitacao() {
        return quitacao;
    }

    public void setQuitacao(String quitacao) {
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
        } else {
            desHabilitaQuitacao = true;
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
                MovimentoDB db = new MovimentoDBToplink();
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
                float valorTotal = 0;

                if (total.equals("0,00")) {
                    for (int i = 0; i < listaMovimentos.size(); i++) {
                        valorTotal = Moeda.somaValores(valorTotal, listaMovimentos.get(i).getValorBaixa());
                    }
                    total = Moeda.converteR$Float(valorTotal);
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
            MovimentoDB db = new MovimentoDBToplink();
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
                MovimentoDB db = new MovimentoDBToplink();
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
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("esMovimento") != null) {
            es = String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("esMovimento"));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("esMovimento");
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
            ContaBanco cb = (ContaBanco) new Dao().find(new ContaBanco(), Integer.valueOf(listaBancoSaida.get(idBancoSaida).getDescription()));

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
}