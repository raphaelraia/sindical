package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.CartaoPag;
import br.com.rtools.financeiro.CartaoRec;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.ContaOperacao;
import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.HistoricoBancario;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.ContaOperacaoDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoBancarioDao;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class MovimentoBancarioBean implements Serializable {

    private int idConta = 0;
    private int idServicos = 0;
    private final List<SelectItem> listaConta = new ArrayList();
    private List<SelectItem> listaServicos = new ArrayList();
    private String valor = "";
    private String tipo = "saida";
    private List<ObjectMovimentoBancario> listaMovimento = new ArrayList();
    private Date dataEmissao = DataHoje.dataHoje();

    private int idContaOperacao = 0;
    private List<SelectItem> listaContaOperacao = new ArrayList();

    private FormaPagamento formaPagamentoEditar = new FormaPagamento();
    private Lote loteEditar = new Lote();
    private Movimento movimentoEditar = new Movimento();

    private ObjectMovimentoBancario statusEditar;
    private List<SelectItem> listaStatus = new ArrayList();
    private Integer indexStatus = 0;

    private ContaSaldo contaSaldo = new ContaSaldo();
    private Double saldoFinal = new Double(0);
    private Double saldoEntradaBloqueado = new Double(0);
    private Double saldoSaidaBloqueado = new Double(0);
    private Double saldoDisponivel = new Double(0);
    private String historico = "";

    private List<SelectItem> listaHistoricoBancario = new ArrayList();
    private Integer indexHistoricoBancario = 0;

    private HistoricoBancario historicoBancario = new HistoricoBancario();

    // FILTROS
    private String esFiltro = "todos";
    private String tipoPagamentoFiltro = "todos";
    private String statusFiltro = "todos";
    // ---

    public MovimentoBancarioBean() {
        loadListaConta();
        loadListaContaOperacao();
        loadListaStatus();
        loadListaMovimento();
    }
    
    public final void novaClass(){

        idServicos = 0;
        listaServicos = new ArrayList();
        valor = "";
        tipo = "saida";
        listaMovimento = new ArrayList();
        dataEmissao = DataHoje.dataHoje();

        idContaOperacao = 0;
        listaContaOperacao = new ArrayList();

        formaPagamentoEditar = new FormaPagamento();
        loteEditar = new Lote();
        movimentoEditar = new Movimento();

        //ObjectMovimentoBancario statusEditar;
        listaStatus = new ArrayList();
        indexStatus = 0;

        contaSaldo = new ContaSaldo();
        saldoFinal = new Double(0);
        saldoEntradaBloqueado = new Double(0);
        saldoSaidaBloqueado = new Double(0);
        saldoDisponivel = new Double(0);
        historico = "";

        listaHistoricoBancario = new ArrayList();
        indexHistoricoBancario = 0;

        historicoBancario = new HistoricoBancario();

        // FILTROS
        esFiltro = "todos";
        tipoPagamentoFiltro = "todos";
        statusFiltro = "todos";
        // ---
        
        loadListaContaOperacao();
        loadListaStatus();
        loadListaMovimento();
    }

    public void editarHistoricoBancario() {
        historicoBancario = (HistoricoBancario) new Dao().find(new HistoricoBancario(), Integer.valueOf(listaHistoricoBancario.get(indexHistoricoBancario).getDescription()));
    }

    public void novoHistoricoBancario() {
        Plano5 pl_hist = historicoBancario.getPlano5();
        Rotina rot = historicoBancario.getRotina();

        historicoBancario = new HistoricoBancario();

        historicoBancario.setPlano5(pl_hist);
        historicoBancario.setRotina(rot);
    }

    public void salvarHistoricoBancario() {
        Dao dao = new Dao();

        dao.openTransaction();
        if (historicoBancario.getId() == -1) {
            ContaOperacao co = (ContaOperacao) new Dao().find(new ContaOperacao(), Integer.valueOf(listaContaOperacao.get(idContaOperacao).getDescription()));

            historicoBancario.setPlano5(co.getPlano5());
            historicoBancario.setRotina((Rotina) dao.find(new Rotina(), 225));

            if (!dao.save(historicoBancario)) {
                GenericaMensagem.error("Atenção", "Erro ao atualizar histórico");
                dao.rollback();
                PF.update("formMovimentoBancario:panel_historico_bancario");
                return;
            }
            GenericaMensagem.info("Sucesso", "Histórico Salvo!");
        } else {
            if (!dao.update(historicoBancario)) {
                GenericaMensagem.error("Atenção", "Erro ao atualizar histórico");
                dao.rollback();
                PF.update("formMovimentoBancario:panel_historico_bancario");
                return;
            }
            GenericaMensagem.info("Sucesso", "Histórico Atualizado!");
        }

        dao.commit();

        loadListaHistoricoBancario();
    }

    public void excluirHistoricoBancario() {
        Dao dao = new Dao();

        dao.openTransaction();
        if (historicoBancario.getId() != -1) {
            if (!dao.delete(historicoBancario)) {
                GenericaMensagem.error("Atenção", "Erro ao excluir histórico");
                dao.rollback();
                PF.update("formMovimentoBancario:panel_historico_bancario");
                return;
            }
            GenericaMensagem.info("Sucesso", "Histórico Excluído!");
            dao.commit();
        }

        loadListaHistoricoBancario();
    }

    public final void loadListaHistoricoBancario() {
        listaHistoricoBancario.clear();
        indexHistoricoBancario = 0;

        ContaOperacao co = (ContaOperacao) new Dao().find(new ContaOperacao(), Integer.valueOf(listaContaOperacao.get(idContaOperacao).getDescription()));
        List<HistoricoBancario> result = new MovimentoBancarioDao().listaHistoricoBancario(co.getPlano5().getId(), 225);

        for (int i = 0; i < result.size(); i++) {
            listaHistoricoBancario.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }

        loadHistoricoDefault();
    }

    public final void loadHistoricoDefault() {
        if (movimentoEditar.getId() == -1) {
            if (!listaHistoricoBancario.isEmpty()) {
                historico = ((HistoricoBancario) new Dao().find(new HistoricoBancario(), Integer.valueOf(listaHistoricoBancario.get(indexHistoricoBancario).getDescription()))).getHistorico();
            } else {
                historico = "";
            }
        }
//        if (loteEditar.getId() == -1) {
//            if (tipo.equals("saida")) {
//                if (historico.isEmpty() || historico.equals("Taxa Bancária")) {
//                    historico = "Taxa Bancária";
//                }
//            } else {
//                if (historico.isEmpty() || historico.equals("Taxa Bancária")) {
//                    historico = "";
//                }
//            }
//        }
    }

    public void limparFiltro() {
        esFiltro = "todos";
        tipoPagamentoFiltro = "todos";
        statusFiltro = "todos";

        loadListaMovimento();
    }

    public Boolean temFiltro() {
        return !esFiltro.equals("todos") || !tipoPagamentoFiltro.equals("todos") || !statusFiltro.equals("todos");
    }

    public final void loadListaMovimento() {
        listaMovimento.clear();
        contaSaldo = new ContaSaldo();
        saldoFinal = new Double(0);
        saldoEntradaBloqueado = new Double(0);
        saldoSaidaBloqueado = new Double(0);
        saldoDisponivel = new Double(0);

        MovimentoBancarioDao mdao = new MovimentoBancarioDao();
        Dao dao = new Dao();

        if (listaConta.isEmpty()) {
            GenericaMensagem.fatal("Erro", "Nenhuma Conta Cadastrada!");
            return;
        }

        Plano5 plano = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
        if (plano == null) {
            GenericaMensagem.fatal("Erro", "Plano de Contas não encontrado!");
            return;
        }

        List<Object> result = mdao.listaMovimentoBancario(plano.getId(), esFiltro, tipoPagamentoFiltro, statusFiltro);

        Boolean comeca_conta_saldo = true;
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                List result_object = (List) (Object) result.get(i);

                FormaPagamento fp = (FormaPagamento) dao.find(new FormaPagamento(), (Integer) result_object.get(0));
                Baixa b = (Baixa) dao.find(new Baixa(), (Integer) result_object.get(1));
                Movimento m = (Movimento) dao.find(new Movimento(), (Integer) result_object.get(4));

                ChequeRec cheque_rec = null;
                if (result_object.get(7) != null) {
                    cheque_rec = (ChequeRec) dao.find(new ChequeRec(), (Integer) result_object.get(7));
                }

                ChequePag cheque_pag = null;
                if (result_object.get(8) != null) {
                    cheque_pag = (ChequePag) dao.find(new ChequePag(), (Integer) result_object.get(8));
                }

                CartaoRec cartao_rec = null;
                if (result_object.get(10) != null) {
                    cartao_rec = (CartaoRec) dao.find(new CartaoRec(), (Integer) result_object.get(10));
                }

                CartaoPag cartao_pag = null;
                if (result_object.get(11) != null) {
                    cartao_pag = (CartaoPag) dao.find(new CartaoPag(), (Integer) result_object.get(11));
                }

                List<ObjectDetalheMovimentoBancario> list_detalhe = new ArrayList();
                List<Object> result_detalhe = mdao.listaDetalheMovimentoBancario((Integer) result_object.get(1));
                for (Object linha : result_detalhe) {
                    List list = (List) linha;
                    list_detalhe.add(new ObjectDetalheMovimentoBancario((String) list.get(0), Moeda.converteUS$(Moeda.converteR$Double((Double) list.get(1)))));
                }

                Double valor_saldo_anterior, valor_saldo;
                if (comeca_conta_saldo && !temFiltro()) {
                    DataHoje dh = new DataHoje();

                    // contaSaldo = mdao.pesquisaContaSaldoData(dh.decrementarDias(1, b.getBaixa()), plano.getId());
                    contaSaldo = mdao.pesquisaContaSaldoData(b.getBaixa(), plano.getId());
                    valor_saldo_anterior = contaSaldo.getSaldo();
                    valor_saldo = fp.getValor();
                } else {
                    if (listaMovimento.isEmpty()) {
                        valor_saldo_anterior = new Double(0);
                    } else {
                        valor_saldo_anterior = listaMovimento.get(i - 1).getSaldo();
                    }
                    valor_saldo = fp.getValor();
                }

                listaMovimento.add(
                        new ObjectMovimentoBancario(
                                fp, // ID FORMA PAGAMENTO
                                b, // ID BAIXA
                                (String) result_object.get(2), // DOCUMENTO
                                (String) result_object.get(3), // HISTORICO 
                                m, // ID BAIXA
                                (m.getEs().equals("E") ? Moeda.soma(valor_saldo_anterior, valor_saldo) : Moeda.subtracao(valor_saldo_anterior, valor_saldo)), // SALDO
                                (TipoPagamento) dao.find(new TipoPagamento(), (Integer) result_object.get(6)), // ID TIPO PAGAMENTO
                                cheque_rec,
                                cheque_pag,
                                list_detalhe,
                                cartao_rec,
                                cartao_pag
                        )
                );

                comeca_conta_saldo = false;

                calculaValoresDoTipo(cheque_rec, cheque_pag, cartao_rec, cartao_pag, m, fp);
            }
        } else {
            contaSaldo = mdao.pesquisaContaSaldoData(null, plano.getId());
        }

        if (!listaMovimento.isEmpty()) {
            saldoFinal = listaMovimento.get(listaMovimento.size() - 1).getSaldo();

            //saldoDisponivel = Moeda.somaValores(Moeda.subtracaoValores(saldoFinal, saldoEntradaBloqueado), saldoSaidaBloqueado);
            // NÃO SUBTRAI A SAIDA BLOQUEADA PARA NÃO CONTAR COM O DISPONIVEL COM SALDO COMPROMETIDO
            // MESMO SABENDO QUE NO BANCO ESSA SAIDA BLOQUEADA ESTARÁ DISPONÍVEL
            // JÁ RECEITA BLOQUEADA, LITERALMENTE NÃO ESTARÁ DISPONÍVEL
            saldoDisponivel = Moeda.subtracao(saldoFinal, saldoEntradaBloqueado);
        } else {
            saldoFinal = contaSaldo.getSaldo();
            saldoDisponivel = contaSaldo.getSaldo();
        }
    }

    public void calculaValoresDoTipo(ChequeRec cheque_rec, ChequePag cheque_pag, CartaoRec cartao_rec, CartaoPag cartao_pag, Movimento m, FormaPagamento fp) {
        if (cheque_rec != null || cheque_pag != null || cartao_rec != null || cartao_pag != null) {
            if (cheque_rec != null) {
                if (fp.getStatus().getId() == 8 && m.getEs().equals("E")) {
                    saldoEntradaBloqueado = Moeda.soma(saldoEntradaBloqueado, fp.getValor());
                } else {
                    saldoDisponivel = Moeda.soma(saldoDisponivel, fp.getValor());
                }
                if (fp.getStatus().getId() == 8 && m.getEs().equals("S")) {
                    saldoSaidaBloqueado = Moeda.soma(saldoSaidaBloqueado, fp.getValor());
                } else {
                    saldoDisponivel = Moeda.soma(saldoDisponivel, fp.getValor());
                }
            }

            if (cheque_pag != null) {
                if (fp.getStatus().getId() == 8 && m.getEs().equals("E")) {
                    saldoSaidaBloqueado = Moeda.soma(saldoEntradaBloqueado, fp.getValor());
                }
                if (fp.getStatus().getId() == 8 && m.getEs().equals("S")) {
                    saldoSaidaBloqueado = Moeda.soma(saldoSaidaBloqueado, fp.getValor());
                }
            }

            if (cartao_rec != null) {
                if (fp.getStatus().getId() == 8 && m.getEs().equals("E")) {
                    saldoEntradaBloqueado = Moeda.soma(saldoEntradaBloqueado, fp.getValor());
                } else {
                    saldoDisponivel = Moeda.soma(saldoDisponivel, fp.getValor());
                }
                if (fp.getStatus().getId() == 8 && m.getEs().equals("S")) {
                    saldoSaidaBloqueado = Moeda.soma(saldoSaidaBloqueado, fp.getValor());
                } else {
                    saldoDisponivel = Moeda.soma(saldoDisponivel, fp.getValor());
                }
            }

            // CARTÃO PAGAMENTO NÃO TEM STATUS POR ENQUANTO
//            if (cartao_pag != null) {
//                if (cartao_pag.getStatus().getId() == 8 && m.getEs().equals("E")) {
//                    saldoSaidaBloqueado = Moeda.somaValores(saldoEntradaBloqueado, fp.getValor());
//                }
//                if (cartao_pag.getStatus().getId() == 8 && m.getEs().equals("S")) {
//                    saldoSaidaBloqueado = Moeda.somaValores(saldoSaidaBloqueado, fp.getValor());
//                }
//            }
        }
    }

    public final void loadListaConta() {
        listaConta.clear();

        Plano5Dao db = new Plano5Dao();

        List<Plano5> listPlano5 = db.pesquisaCaixaBanco();
        for (int i = 0; i < listPlano5.size(); i++) {
            listaConta.add(
                    new SelectItem(
                            i,
                            //listPlano5.get(i).getContaBanco().getBanco().getBanco() + " - " + listPlano5.get(i).getContaBanco().getAgencia() + " - " + listPlano5.get(i).getContaBanco().getConta(),
                            listPlano5.get(i).getConta(),
                            Integer.toString((listPlano5.get(i).getId()))
                    )
            );
        }
    }

    public final void loadListaContaOperacao() {
        listaContaOperacao.clear();
        
        List<ContaOperacao> result = new ContaOperacaoDao().findByOperacao(tipo.equals("entrada") ? 7 : 8);
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaContaOperacao.add(
                        new SelectItem(
                                i,
                                result.get(i).getPlano5().getPlano4().getConta() + " - " + result.get(i).getPlano5().getConta(),
                                "" + result.get(i).getId()
                        )
                );
            }
        }

        loadListaHistoricoBancario();
    }

    public final void loadListaStatus() {
        listaStatus.clear();
        
        List<FStatus> result = new FinanceiroDao().listaFStatusIn("8, 9, 10, 11");
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaStatus.add(
                        new SelectItem(
                                i,
                                result.get(i).getDescricao(),
                                "" + result.get(i).getId()
                        )
                );
            }
        }
    }

    public void salvar() {
        if (Moeda.converteUS$(valor) <= 0) {
            GenericaMensagem.warn("Atenção", "Valor não pode ser Zero!");
            return;
        }

        Dao dao = new Dao();
        if (formaPagamentoEditar.getId() == -1) {
            Lote lote;
            Movimento movimento;
            Baixa baixa;
            FormaPagamento forma_pagamento;

            ContaOperacao co = (ContaOperacao) dao.find(new ContaOperacao(), Integer.valueOf(listaContaOperacao.get(idContaOperacao).getDescription()));
            Plano5 plano5_lote = co.getPlano5();

            Plano5 plano5_forma_pagamento = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));

            Date ultima_data_conta_saldo = new MovimentoBancarioDao().ultimaDataContaSaldo();
            if (ultima_data_conta_saldo != null) {
                if (DataHoje.menorData(dataEmissao, ultima_data_conta_saldo) || dataEmissao.equals(ultima_data_conta_saldo)) {
                    GenericaMensagem.warn("Atenção", "Data de emissão não pode ser menor ou igual a " + DataHoje.converteData(ultima_data_conta_saldo));
                    return;
                }

                if (DataHoje.maiorData(dataEmissao, DataHoje.dataHoje())) {
                    GenericaMensagem.warn("Atenção", "Data de emissão não pode ser maior que a data de hoje!");
                    return;
                }
            }

            baixa = novaBaixa(dataEmissao);

            if (tipo.equals("saida")) {
                lote = novoLote(dao, "P", plano5_lote, Moeda.converteUS$(valor), (FStatus) dao.find(new FStatus(), 1), historico);
                movimento = novoMovimento(dao, lote, baixa, "S");
                forma_pagamento = novaFormaPagamento(dao, baixa, Moeda.converteUS$(valor), plano5_forma_pagamento, (TipoPagamento) dao.find(new TipoPagamento(), 14));
            } else {
                lote = novoLote(dao, "R", plano5_lote, Moeda.converteUS$(valor), (FStatus) dao.find(new FStatus(), 1), historico);
                movimento = novoMovimento(dao, lote, baixa, "E");
                forma_pagamento = novaFormaPagamento(dao, baixa, Moeda.converteUS$(valor), plano5_forma_pagamento, (TipoPagamento) dao.find(new TipoPagamento(), 14));
            }

            dao.openTransaction();
            if (!dao.save(baixa)) {
                GenericaMensagem.warn("Erro", "Erro ao salvar Baixa");
                dao.rollback();
                return;
            }

            if (!dao.save(lote)) {
                GenericaMensagem.warn("Erro", "Erro ao salvar Lote");
                dao.rollback();
                return;
            }

            if (!dao.save(movimento)) {
                GenericaMensagem.warn("Erro", "Erro ao salvar Movimento");
                dao.rollback();
                return;
            }

            if (!dao.save(forma_pagamento)) {
                GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento");
                dao.rollback();
                return;
            }

            dao.commit();

            loadListaMovimento();
            GenericaMensagem.info("Sucesso", "Movimento salvo com Sucesso!");
            novo();
        } else {
            if (formaPagamentoEditar.getChequeRec() != null || formaPagamentoEditar.getChequePag() != null || formaPagamentoEditar.getCartaoRec() != null || formaPagamentoEditar.getCartaoPag() != null) {
                GenericaMensagem.error("Atenção", "Essa Operação não pode ser ATUALIZADA!");
                novo();
                return;
            }

            formaPagamentoEditar.setValorString(valor);
            movimentoEditar.setValorString(valor);
            movimentoEditar.setValorBaixaString(valor);
            loteEditar.setValorString(valor);
            loteEditar.setHistoricoContabil(historico);

            dao.openTransaction();

            if (!dao.update(formaPagamentoEditar)) {
                GenericaMensagem.warn("Erro", "Erro ao Atualizar Forma de Pagamento!");
                dao.rollback();
                return;
            }

            if (!dao.update(movimentoEditar)) {
                GenericaMensagem.warn("Erro", "Erro ao Atualizar Movimento!");
                dao.rollback();
                return;
            }

            if (!dao.update(loteEditar)) {
                GenericaMensagem.warn("Erro", "Erro ao Atualizar Lote!");
                dao.rollback();
                return;
            }

            dao.commit();

            loadListaMovimento();

            GenericaMensagem.info("Sucesso", "Registro Atualizado!");
            novo();
        }
    }

    public void editar(ObjectMovimentoBancario omb) {
        formaPagamentoEditar = omb.getFormaPagamento();
        movimentoEditar = omb.getMovimento();
        loteEditar = omb.getMovimento().getLote();
        historico = loteEditar.getHistoricoContabil();

        for (int i = 0; i < listaConta.size(); i++) {
            if (Integer.valueOf(listaConta.get(i).getDescription()) == omb.getFormaPagamento().getPlano5().getId()) {
                idConta = i;
            }
        }

        valor = omb.getFormaPagamento().getValorString();

        tipo = omb.getMovimento().getEs().equals("S") ? "saida" : "entrada";

        dataEmissao = omb.getBaixa().getDtBaixa();

        loadListaContaOperacao();

        for (int i = 0; i < listaContaOperacao.size(); i++) {
            // VER COMO RETORNAR O PLANO 5 DA CONTA OPERAÇÃO
            //if (Integer.valueOf(listaContaOperacao.get(i).getDescription()) ==  // ?? ){
            //    idContaOperacao = i;
            //}
        }
    }

    public void excluir() {
        if (formaPagamentoEditar.getBaixa().getFechamentoCaixa() != null) {
            GenericaMensagem.error("Atenção", "Movimentos com Caixa Fechado não podem ser excluídos!");
            return;
        }

        if (formaPagamentoEditar.getChequeRec() != null || formaPagamentoEditar.getChequePag() != null || formaPagamentoEditar.getCartaoRec() != null || formaPagamentoEditar.getCartaoPag() != null) {
            GenericaMensagem.error("Atenção", "Essa Operação não pode ser EXCLUÍDA!");
            novo();
            return;
        }

        movimentoEditar.setAtivo(false);

        Dao dao = new Dao();
        dao.openTransaction();

        if (!dao.update(movimentoEditar)) {
            GenericaMensagem.warn("Erro", "Erro ao Excluir Movimento!");
            dao.rollback();
            return;
        }

        dao.commit();

        NovoLog log = new NovoLog();

        log.delete(
                "FORMA DE PAGAMENTO ID: " + formaPagamentoEditar.getId() + " \n"
                + "LOTE ID: " + movimentoEditar.getLote().getId() + " \n"
                + "MOVIMENTO ID: " + movimentoEditar.getId() + " \n"
                + "MOVIMENTO STATUS: " + (movimentoEditar.isAtivo() ? "ATIVADO" : "INATIVADO") + " \n"
        );

        GenericaMensagem.info("Sucesso", "Movimento Excluído!");
        novo();
    }

    public void novo() {
        //Integer id = idConta;
        
        //GenericaSessao.put("movimentoBancarioBean", new MovimentoBancarioBean());
        
        //((MovimentoBancarioBean) GenericaSessao.getObject("movimentoBancarioBean")).setIdConta(id);
        
        novaClass();
    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, double valor, FStatus fstatus, String historicox) {
        return new Lote(
                -1,
                (Rotina) dao.find(new Rotina(), 225), // ROTINA
                pag_rec, // PAG REC
                DataHoje.data(), // LANCAMENTO
                (Pessoa) dao.find(new Pessoa(), 0), // PESSOA
                plano, // PLANO 5
                false,// VENCER CONTABIL
                "", // DOCUMENTO
                valor, // VALOR
                (Filial) dao.find(new Filial(), 1), // FILIAL
                null, // DEPARTAMENTO
                null, // EVT
                historicox, // HISTORICO
                (FTipoDocumento) dao.find(new FTipoDocumento(), 4), // 4 - CHEQUE / 5 - CHEQUE PRE
                (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1), // 1 - A VISTA / 2 - PRAZO
                fstatus, // 1 - EFETIVO // 8 - DEPOSITADO // 14 - NÃO CONTABILIZAR
                null, // PESSOA SEM CADASTRO
                false, // DESCONTO FOLHA
                0, // DESCONTO
                null,
                null,
                null,
                false,
                historicox,
                null,
                ""
        );
    }

    public Movimento novoMovimento(Dao dao, Lote lote, Baixa baixa, String e_s) {
        return new Movimento(
                -1,
                lote,
                lote.getPlano5(), // PLANO 5
                lote.getPessoa(), // PESSOA
                (Servicos) dao.find(new Servicos(), 50), // SERVICO
                baixa, // BAIXA
                (TipoServico) dao.find(new TipoServico(), 1), // TIPO SERVICO
                null, // ACORDO
                lote.getValor(), // VALOR
                "", // REFERENCIA
                DataHoje.data(), // VENCIMENTO
                1, // QND
                true, // ATIVO
                e_s, // E_S
                false, // OBRIGACAO 
                null, // TITULAR
                null, // BENEFICIARIO
                "", // DOCUMENTO
                "", // NR_CTR_BOLETO
                DataHoje.data(), // VENCTO ORIGINAL
                0, // DESCONTO ATE VENCIMENTO
                0, // CORRECAO
                0, // JUROS
                0, // MULTA
                0, // DESCONTO
                0, // TAXA
                lote.getValor(), // VALOR BAIXA
                lote.getFtipoDocumento(), // 4 - CHEQUE / 5 - CHEQUE PRE
                0, // REPASSE AUTOMATICO
                null // MATRICULA SOCIO
        );
    }

    // NORMAL
    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, TipoPagamento tipoPagamento) {
        return this.novaFormaPagamento(dao, baixa, valor, plano, null, null, null, null, tipoPagamento, null, 0);
    }

    // CHEQUE PAGAMENTO
    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, ChequePag chequePag, TipoPagamento tipoPagamento, FStatus fstatus, Integer devolucao) {
        return this.novaFormaPagamento(dao, baixa, valor, plano, chequePag, null, null, null, tipoPagamento, fstatus, devolucao);
    }

    // CHEQUE RECEBIMENTO
    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, ChequeRec chequeRec, TipoPagamento tipoPagamento, FStatus fstatus, Integer devolucao) {
        return this.novaFormaPagamento(dao, baixa, valor, plano, null, chequeRec, null, null, tipoPagamento, fstatus, devolucao);
    }

    // CARTÃO PAGAMENTO
    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, CartaoPag cartaoPag, TipoPagamento tipoPagamento, FStatus fstatus, Integer devolucao) {
        return this.novaFormaPagamento(dao, baixa, valor, plano, null, null, cartaoPag, null, tipoPagamento, fstatus, devolucao);
    }

    // CARTÃO RECEBIMENTO
    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, CartaoRec cartaoRec, TipoPagamento tipoPagamento, FStatus fstatus, Integer devolucao) {
        return this.novaFormaPagamento(dao, baixa, valor, plano, null, null, null, cartaoRec, tipoPagamento, fstatus, devolucao);
    }

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, ChequePag chequePag, ChequeRec chequeRec, CartaoPag cartaoPag, CartaoRec cartaoRec, TipoPagamento tipoPagamento, FStatus fstatus, Integer devolucao) {
        FormaPagamento fp = new FormaPagamento(
                -1,
                baixa,
                null,
                null,
                100,
                valor,
                (Filial) dao.find(new Filial(), 1),
                plano,
                null,
                null,
                tipoPagamento,
                0,
                DataHoje.dataHoje(),
                0,
                fstatus,
                devolucao,
                null,
                null,
                null
        );

        if (chequePag != null) {
            fp.setChequePag(chequePag);
        }

        if (chequeRec != null) {
            fp.setChequeRec(chequeRec);
        }

        if (cartaoPag != null) {
            fp.setCartaoPag(cartaoPag);
        }

        if (cartaoRec != null) {
            fp.setCartaoRec(cartaoRec);
        }

        return fp;
    }

    public Baixa novaBaixa(Date dataEmissao) {
        return new Baixa(
                -1,
                (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"),
                dataEmissao,
                null,
                0,
                "",
                null,
                null,
                null,
                0,
                0
        );
    }

    public HashMap statusCheque(ObjectMovimentoBancario omb) {
        // id 8 = DEPOSITADO
        HashMap hash = new LinkedHashMap();
        if (omb != null) {
            if (omb.getChequePag() != null) {
                hash.put("status", omb.getFormaPagamento().getStatus().getDescricao());

                if (omb.getFormaPagamento().getStatus().getId() == 10 || omb.getFormaPagamento().getStatus().getId() == 11) {
                    hash.put("cor", "color: red;");
                } else {
                    hash.put("cor", "color: black;");
                }
                return hash;
            }

            if (omb.getChequeRec() != null) {
                hash.put("status", omb.getFormaPagamento().getStatus().getDescricao());

                if (omb.getFormaPagamento().getStatus().getId() == 10 || omb.getFormaPagamento().getStatus().getId() == 11) {
                    hash.put("cor", "color: red;");
                } else {
                    hash.put("cor", "color: black;");
                }
                return hash;
            }

            if (omb.getCartaoRec() != null) {
                hash.put("status", omb.getFormaPagamento().getStatus().getDescricao());

                if (omb.getFormaPagamento().getStatus().getId() == 10 || omb.getFormaPagamento().getStatus().getId() == 11) {
                    hash.put("cor", "color: red;");
                } else {
                    hash.put("cor", "color: black;");
                }
                return hash;
            }

            // CARTÃO PAGAMENTO POR ENQUANDO NÃO TEM STATUS
            if (omb.getCartaoPag() != null) {
//                hash.put("status", omb.getCartaoPag().getStatus().getDescricao());
//
//                if (omb.getCartaoPag().getStatus().getId() == 10 || omb.getCartaoPag().getStatus().getId() == 11) {
//                    hash.put("cor", "color: red;");
//                } else {
//                    hash.put("cor", "color: black;");
//                }
//                return hash;
            }
        }

        hash.put("status", "");
        hash.put("cor", "");
        return hash;
    }

    public Boolean mostraStatus(ObjectMovimentoBancario omb) {
        if (omb != null) {
            if (omb.getChequePag() != null) {
                // SE FOR ENTRADA E O STATUS FOR SUSTADO OU DEVOLVIDO, ENTÃO MOSTRAR STATUS APENAS DO S (SAÍDA)
                return !(omb.getMovimento().getEs().equals("E") && (omb.getFormaPagamento().getStatus().getId() == 10 || omb.getFormaPagamento().getStatus().getId() == 11));
            }

            if (omb.getChequeRec() != null) {
                // SE FOR ENTRADA E O STATUS FOR SUSTADO OU DEVOLVIDO, ENTÃO MOSTRAR STATUS APENAS DO S (SAÍDA)
                return !(omb.getMovimento().getEs().equals("E") && (omb.getFormaPagamento().getStatus().getId() == 10 || omb.getFormaPagamento().getStatus().getId() == 11));
            }

            if (omb.getCartaoRec() != null) {
                // SE FOR ENTRADA E O STATUS FOR SUSTADO OU DEVOLVIDO, ENTÃO MOSTRAR STATUS APENAS DO S (SAÍDA)
                return !(omb.getMovimento().getEs().equals("E") && (omb.getFormaPagamento().getStatus().getId() == 10 || omb.getFormaPagamento().getStatus().getId() == 11));
            }

            if (omb.getCartaoPag() != null) {
                // SE FOR ENTRADA E O STATUS FOR SUSTADO OU DEVOLVIDO, ENTÃO MOSTRAR STATUS APENAS DO S (SAÍDA)
                // POR ENQUANTO CARTÃO PAG NÃO TEM STATUS
                //return !(omb.getMovimento().getEs().equals("E") && (omb.getCartaoPag().getStatus().getId() == 10 || omb.getCartaoPag().getStatus().getId() == 11));
                return false;
            }
        }

        return false;
    }

    public Boolean desabilitaStatus(ObjectMovimentoBancario linha) {
        if (linha.getChequePag() != null) {
            if (linha.getFormaPagamento().getStatus().getId() == 8) {
                return false;
            }
        }

        if (linha.getChequeRec() != null) {
            if (linha.getFormaPagamento().getStatus().getId() == 8) {
                return false;
            }
        }

        if (linha.getCartaoRec() != null) {
            if (linha.getFormaPagamento().getStatus().getId() == 8) {
                return false;
            }
        }

        // POR ENQUANTO CARTÃO PAGAMENTO NÃO TEM STATUS
        if (linha.getCartaoPag() != null) {
//            if (linha.getCartaoPag().getStatus().getId() == 8) {
//                return false;
//            }
        }
        return true;
    }

    public void selecionaStatus(ObjectMovimentoBancario linha) {
        statusEditar = linha;

        if (statusEditar.getChequePag() != null) {
            for (int i = 0; i < listaStatus.size(); i++) {
                if (statusEditar.getFormaPagamento().getStatus().getId() == Integer.valueOf(listaStatus.get(i).getDescription())) {
                    indexStatus = i;
                }
            }
        }

        if (statusEditar.getChequeRec() != null) {
            for (int i = 0; i < listaStatus.size(); i++) {
                if (statusEditar.getFormaPagamento().getStatus().getId() == Integer.valueOf(listaStatus.get(i).getDescription())) {
                    indexStatus = i;
                }
            }
        }

        if (statusEditar.getCartaoRec() != null) {
            for (int i = 0; i < listaStatus.size(); i++) {
                if (statusEditar.getFormaPagamento().getStatus().getId() == Integer.valueOf(listaStatus.get(i).getDescription())) {
                    indexStatus = i;
                }
            }
        }

        if (statusEditar.getCartaoPag() != null) {
//            for (int i = 0; i < listaStatus.size(); i++) {
//                if (statusEditar.getCartaoPag().getStatus().getId() == Integer.valueOf(listaStatus.get(i).getDescription())) {
//                    indexStatus = i;
//                }
//            }
        }
    }

    public void atualizarStatus() {
        if (statusEditar.getChequePag() == null
                && statusEditar.getChequeRec() == null
                && statusEditar.getCartaoPag() == null
                && statusEditar.getCartaoRec() == null) {
            GenericaMensagem.warn("Atenção", "Não existe nada para ser atualizado!");
            return;
        }

        if (!atualizarStatus(statusEditar)) {
            GenericaMensagem.error("Erro", "Não foi possível atualizar Cartão!");
        }

        statusEditar = null;
    }

    public Boolean atualizarStatus(ObjectMovimentoBancario linha) {
        Dao dao = new Dao();

        Integer id_status = Integer.valueOf(listaStatus.get(indexStatus).getDescription());
        FStatus f_status = (FStatus) dao.find(new FStatus(), id_status);

        // LIQUIDADO
        if (f_status.getId() == 9) {
            dao.openTransaction();
            // SETA STATUS LIQUIDADO NA FORMA DE PAGAMENTO
            linha.getFormaPagamento().setStatus(f_status);

            if (!dao.update(linha.getFormaPagamento())) {
                dao.rollback();
                return false;
            }

            dao.commit();

            loadListaMovimento();
            GenericaMensagem.info("Sucesso", "Status LIQUIDADO Atualizado!");
            return true;
        }

        // DEVOLVIDO
        if (f_status.getId() == 10) {
            dao.openTransaction();
            if (linha.getChequeRec() != null) {
                // DEVOLVE CHEQUE RECEBIDO
                if (devolveChequeRec(dao, f_status, linha)) {
                    dao.commit();
                    loadListaMovimento();
                    GenericaMensagem.info("Sucesso", "Status DEVOLVIDO Atualizado!");
                    return true;
                }
            }

            if (linha.getChequePag() != null) {
                // DEVOLVE CHEQUE PAGAMENTO
                if (devolveChequePag(dao, f_status, linha)) {
                    dao.commit();
                    loadListaMovimento();
                    GenericaMensagem.info("Sucesso", "Status DEVOLVIDO Atualizado!");
                    return true;
                }
            }

            if (linha.getCartaoRec() != null) {
                // DEVOLVE CARTAO RECEBIDO
                //devolveCartaoRec(dao, linha);
                if (1 == 1) {
                    GenericaMensagem.error("Erro", "Status não definido!");
                    dao.rollback();
                    return false;
                }
                dao.commit();

                loadListaMovimento();
                GenericaMensagem.info("Sucesso", "Status DEVOLVIDO Atualizado!");
                return true;
            }

            if (linha.getCartaoPag() != null) {
                // DEVOLVE CARTAO PAGAMENTO
                //devolveCartaoPag(dao, linha);
                if (1 == 1) {
                    GenericaMensagem.error("Erro", "Status não definido!");
                    dao.rollback();
                    return false;
                }
                dao.commit();

                loadListaMovimento();
                GenericaMensagem.info("Sucesso", "Status DEVOLVIDO Atualizado!");
                return true;
            }
        }
        // -----------------------------------------------------------------
        // -----------------------------------------------------------------
        return false;
    }

    public Boolean devolveChequeRec(Dao dao, FStatus f_status_devolvido, ObjectMovimentoBancario linha) {
        Plano5 plano5_forma_pagamento_saida = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
        Plano5 plano5_forma_pagamento_entrada = (Plano5) dao.find(new Plano5(), 1);
        String historicox = "Devolvido da conta ( " + plano5_forma_pagamento_saida.getConta() + " ) para a conta " + plano5_forma_pagamento_entrada.getConta();

        // SAIDA -----------------------------------------------------------
        // -----------------------------------------------------------------
        Baixa baixa_saida = criarLoteMovimentoSaida(dao, linha.getFormaPagamento().getValor(), historicox);

        if (baixa_saida == null) {
            return false;
        }

        // DEVOLVEU A PRIMEIRA E SEGUNDA VEZ
        // LIQUIDADO
        linha.getFormaPagamento().setStatus((FStatus) dao.find(new FStatus(), 9));
        if (!dao.update(linha.getFormaPagamento())) {
            return false;
        }

        Integer nr_devolucao = linha.getFormaPagamento().getDevolucao();
        // CRIA SAIDA
        FormaPagamento fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), f_status_devolvido, nr_devolucao + 1);

//        switch (linha.getFormaPagamento().getDevolucao()) {
//            case 0:
//                // DEVOLVEU A PRIMEIRA VEZ
//                // LIQUIDADO
//                linha.getFormaPagamento().setStatus((FStatus) dao.find(new FStatus(), 9));
//                if (!dao.update(linha.getFormaPagamento())) {
//                    return false;
//                }
//
//                // CRIA SAIDA
//                fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), f_status_devolvido, linha.getFormaPagamento().getDevolucao() + 1);
//                break;
//            case 1:
//                // DEVOLVEU PELA SEGUNDA VEZ
//                linha.getFormaPagamento().setStatus((FStatus) dao.find(new FStatus(), 9));
//                
//                if (!dao.update(linha.getFormaPagamento())) {
//                    return false;
//                }
//                
//                // CRIA SAIDA
//                fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), f_status_devolvido, linha.getFormaPagamento().getDevolucao() + 1);
//                break;
//        }
//        if (linha.getChequePag() != null) {
//            fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getChequePag(), (TipoPagamento) dao.find(new TipoPagamento(), 4), f_status);
//        } else if (linha.getChequeRec() != null) {
//            fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), f_status);
//        } else if (linha.getCartaoPag() != null) {
//            fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getCartaoPag(), linha.getTipoPagamento(), f_status);
//        } else if (linha.getCartaoRec() != null) {
//            fp = novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, linha.getCartaoRec(), linha.getTipoPagamento(), f_status);
//        }
        if (!dao.save(fp)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
            return false;
        }
        // -----------------------------------------------------------------
        // -----------------------------------------------------------------

        // ENTRADA ---------------------------------------------------------
        // -----------------------------------------------------------------
        Baixa baixa_entrada = criarLoteMovimentoEntrada(dao, linha.getFormaPagamento().getValor(), historicox);

        FormaPagamento fp2 = null;
        switch (nr_devolucao) {
            case 0:
                // DEVOLVEU A PRIMEIRA VEZ
                // A DEPOSITAR
                // CRIA ENTRADA
                fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), (FStatus) dao.find(new FStatus(), 7), nr_devolucao + 1);
                break;
            case 1:
                // DEVOLVEU PELA SEGUNDA VEZ
                fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), f_status_devolvido, nr_devolucao + 1);
                break;
        }
//        if (linha.getChequePag() != null) {
//            fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getChequePag(), (TipoPagamento) dao.find(new TipoPagamento(), 4), fstatus2);
//        } else if (linha.getChequeRec() != null) {
//            fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getChequeRec(), (TipoPagamento) dao.find(new TipoPagamento(), 4), fstatus2);
//        } else if (linha.getCartaoPag() != null) {
//            fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getCartaoPag(), linha.getTipoPagamento(), fstatus2);
//        } else if (linha.getCartaoRec() != null) {
//            fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getCartaoRec(), linha.getTipoPagamento(), fstatus2);
//        }

        if (!dao.save(fp2)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
            return false;
        }
        return true;
    }

    public Boolean devolveChequePag(Dao dao, FStatus f_status_devolvido, ObjectMovimentoBancario linha) {
        // SAIDA -----------------------------------------------------------
        // -----------------------------------------------------------------
        switch (linha.getFormaPagamento().getDevolucao()) {
            case 0:
                // DEVOLVEU A PRIMEIRA VEZ
                linha.getFormaPagamento().setDevolucao(linha.getFormaPagamento().getDevolucao() + 1);

                if (!dao.update(linha.getFormaPagamento())) {
                    return false;
                }
                break;
            case 1:
                // DEVOLVEU PELA SEGUNDA VEZ
                linha.getFormaPagamento().setStatus(f_status_devolvido);
                linha.getFormaPagamento().setDevolucao(linha.getFormaPagamento().getDevolucao() + 1);

                if (!dao.update(linha.getFormaPagamento())) {
                    return false;
                }
                break;
        }
//        if (linha.getCartaoPag() != null) {
//            fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getCartaoPag(), linha.getTipoPagamento(), fstatus2);
//        } else if (linha.getCartaoRec() != null) {
//            fp2 = novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, linha.getCartaoRec(), linha.getTipoPagamento(), fstatus2);
//        }
        return true;
    }

    public Baixa criarLoteMovimentoSaida(Dao dao, Double valor, String historicox) {
        Baixa baixa_saida = novaBaixa(DataHoje.dataHoje());
        if (!dao.save(baixa_saida)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Saida!");
            return null;
        }

        Plano5 plano5_lote_saida = (Plano5) dao.find(new Plano5(), 1);

        Lote lote_saida = novoLote(dao, "P", plano5_lote_saida, valor, (FStatus) dao.find(new FStatus(), 14), historicox);
        if (!dao.save(lote_saida)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
            return null;
        }

        Movimento movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");
        if (!dao.save(movimento_saida)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Saida!");
            return null;
        }

        return baixa_saida;
    }

    public Baixa criarLoteMovimentoEntrada(Dao dao, Double valor, String historicox) {
        Baixa baixa_entrada = novaBaixa(DataHoje.dataHoje());
        if (!dao.save(baixa_entrada)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Entrada!");
            return null;
        }

        Plano5 plano5_lote_entrada = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));

        Lote lote_entrada = novoLote(dao, "R", plano5_lote_entrada, valor, (FStatus) dao.find(new FStatus(), 1), historicox);
        if (!dao.save(lote_entrada)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
            return null;
        }

        Movimento movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");
        if (!dao.save(movimento_entrada)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Entrada!");
            return null;
        }

        return baixa_entrada;
    }

    public List<SelectItem> getListaConta() {
        return listaConta;
    }

    public List<SelectItem> getListaServicos() {
        if (listaServicos.isEmpty()) {
            ServicosDao db = new ServicosDao();
            List<Servicos> select = db.pesquisaTodos(225);
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaServicos.add(
                            new SelectItem(
                                    i,
                                    select.get(i).getDescricao(),
                                    Integer.toString(select.get(i).getId())
                            )
                    );
                }
            }
        }
        return listaServicos;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public int getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(int idServicos) {
        this.idServicos = idServicos;
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<ObjectMovimentoBancario> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimento(List<ObjectMovimentoBancario> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public int getIdContaOperacao() {
        return idContaOperacao;
    }

    public void setIdContaOperacao(int idContaOperacao) {
        this.idContaOperacao = idContaOperacao;
    }

    public List<SelectItem> getListaContaOperacao() {
        return listaContaOperacao;
    }

    public void setListaContaOperacao(List<SelectItem> listaContaOperacao) {
        this.listaContaOperacao = listaContaOperacao;
    }

    public FormaPagamento getFormaPagamentoEditar() {
        return formaPagamentoEditar;
    }

    public void setFormaPagamentoEditar(FormaPagamento formaPagamentoEditar) {
        this.formaPagamentoEditar = formaPagamentoEditar;
    }

    public Lote getLoteEditar() {
        return loteEditar;
    }

    public void setLoteEditar(Lote loteEditar) {
        this.loteEditar = loteEditar;
    }

    public Movimento getMovimentoEditar() {
        return movimentoEditar;
    }

    public void setMovimentoEditar(Movimento movimentoEditar) {
        this.movimentoEditar = movimentoEditar;
    }

    public List<SelectItem> getListaStatus() {
        return listaStatus;
    }

    public Integer getIndexStatus() {
        return indexStatus;
    }

    public void setIndexStatus(Integer indexStatus) {
        this.indexStatus = indexStatus;
    }

    public ContaSaldo getContaSaldo() {
        return contaSaldo;
    }

    public void setContaSaldo(ContaSaldo contaSaldo) {
        this.contaSaldo = contaSaldo;
    }

    public Double getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public String getSaldoFinalString() {
        return Moeda.converteR$Double(saldoFinal);
    }

    public void setSaldoFinalString(String saldoFinalString) {
        this.saldoFinal = Moeda.converteUS$(saldoFinalString);
    }

    public Double getSaldoEntradaBloqueado() {
        return saldoEntradaBloqueado;
    }

    public void setSaldoEntradaBloqueado(Double saldoEntradaBloqueado) {
        this.saldoEntradaBloqueado = saldoEntradaBloqueado;
    }

    public String getSaldoEntradaBloqueadoString() {
        return Moeda.converteR$Double(saldoEntradaBloqueado);
    }

    public void setSaldoEntradaBloqueadoString(String saldoEntradaBloqueadoString) {
        this.saldoEntradaBloqueado = Moeda.converteUS$(saldoEntradaBloqueadoString);
    }

    public Double getSaldoSaidaBloqueado() {
        return saldoSaidaBloqueado;
    }

    public void setSaldoSaidaBloqueado(Double saldoSaidaBloqueado) {
        this.saldoSaidaBloqueado = saldoSaidaBloqueado;
    }

    public String getSaldoSaidaBloqueadoString() {
        return Moeda.converteR$Double(saldoSaidaBloqueado);
    }

    public void setSaldoSaidaBloqueadoString(String saldoSaidaBloqueadoString) {
        this.saldoSaidaBloqueado = Moeda.converteUS$(saldoSaidaBloqueadoString);
    }

    public Double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(Double saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public String getSaldoDisponivelString() {
        return Moeda.converteR$Double(saldoDisponivel);
    }

    public void setSaldoDisponivelString(String saldoDisponivelString) {
        this.saldoDisponivel = Moeda.converteUS$(saldoDisponivelString);
    }

    public String getEsFiltro() {
        return esFiltro;
    }

    public void setEsFiltro(String esFiltro) {
        this.esFiltro = esFiltro;
    }

    public String getTipoPagamentoFiltro() {
        return tipoPagamentoFiltro;
    }

    public void setTipoPagamentoFiltro(String tipoPagamentoFiltro) {
        this.tipoPagamentoFiltro = tipoPagamentoFiltro;
    }

    public String getStatusFiltro() {
        return statusFiltro;
    }

    public void setStatusFiltro(String statusFiltro) {
        this.statusFiltro = statusFiltro;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataEmissaoString() {
        return DataHoje.converteData(dataEmissao);
    }

    public void setDataEmissaoString(String dataEmissaoString) {
        this.dataEmissao = DataHoje.converte(dataEmissaoString);
    }

    public List<SelectItem> getListaHistoricoBancario() {
        return listaHistoricoBancario;
    }

    public Integer getIndexHistoricoBancario() {
        return indexHistoricoBancario;
    }

    public void setIndexHistoricoBancario(Integer indexHistoricoBancario) {
        this.indexHistoricoBancario = indexHistoricoBancario;
    }

    public HistoricoBancario getHistoricoBancario() {
        return historicoBancario;
    }

    public void setHistoricoBancario(HistoricoBancario historicoBancario) {
        this.historicoBancario = historicoBancario;
    }

    public class ObjectMovimentoBancario {

        private FormaPagamento formaPagamento;
        private Baixa baixa;
        private String documento;
        private String historico;
        private Movimento movimento;
        private Double saldo;
        private TipoPagamento tipoPagamento;
        private ChequeRec chequeRec;
        private ChequePag chequePag;
        private List<ObjectDetalheMovimentoBancario> listDetalheMovimento;
        private CartaoRec cartaoRec;
        private CartaoPag cartaoPag;

        public ObjectMovimentoBancario(FormaPagamento formaPagamento, Baixa baixa, String documento, String historico, Movimento movimento, Double saldo, TipoPagamento tipoPagamento, ChequeRec chequeRec, ChequePag chequePag, List<ObjectDetalheMovimentoBancario> listDetalheMovimento, CartaoRec cartaoRec, CartaoPag cartaoPag) {
            this.formaPagamento = formaPagamento;
            this.baixa = baixa;
            this.documento = documento;
            this.historico = historico;
            this.movimento = movimento;
            this.saldo = saldo;
            this.tipoPagamento = tipoPagamento;
            this.chequeRec = chequeRec;
            this.chequePag = chequePag;
            this.listDetalheMovimento = listDetalheMovimento;
            this.cartaoRec = cartaoRec;
            this.cartaoPag = cartaoPag;
        }

        public FormaPagamento getFormaPagamento() {
            return formaPagamento;
        }

        public void setFormaPagamento(FormaPagamento formaPagamento) {
            this.formaPagamento = formaPagamento;
        }

        public Baixa getBaixa() {
            return baixa;
        }

        public void setBaixa(Baixa baixa) {
            this.baixa = baixa;
        }

        public String getDocumento() {
            return documento;
        }

        public void setDocumento(String documento) {
            this.documento = documento;
        }

        public String getHistorico() {
            return historico;
        }

        public void setHistorico(String historico) {
            this.historico = historico;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Double getSaldo() {
            return saldo;
        }

        public void setSaldo(Double saldo) {
            this.saldo = saldo;
        }

        public String getSaldoString() {
            return Moeda.converteR$Double(saldo);
        }

        public void setSaldoString(String saldoString) {
            this.saldo = Moeda.converteUS$(saldoString);
        }

        public TipoPagamento getTipoPagamento() {
            return tipoPagamento;
        }

        public void setTipoPagamento(TipoPagamento tipoPagamento) {
            this.tipoPagamento = tipoPagamento;
        }

        public ChequeRec getChequeRec() {
            return chequeRec;
        }

        public void setChequeRec(ChequeRec chequeRec) {
            this.chequeRec = chequeRec;
        }

        public ChequePag getChequePag() {
            return chequePag;
        }

        public void setChequePag(ChequePag chequePag) {
            this.chequePag = chequePag;
        }

        public List<ObjectDetalheMovimentoBancario> getListDetalheMovimento() {
            return listDetalheMovimento;
        }

        public void setListDetalheMovimento(List<ObjectDetalheMovimentoBancario> listDetalheMovimento) {
            this.listDetalheMovimento = listDetalheMovimento;
        }

        public CartaoRec getCartaoRec() {
            return cartaoRec;
        }

        public void setCartaoRec(CartaoRec cartaoRec) {
            this.cartaoRec = cartaoRec;
        }

        public CartaoPag getCartaoPag() {
            return cartaoPag;
        }

        public void setCartaoPag(CartaoPag cartaoPag) {
            this.cartaoPag = cartaoPag;
        }
    }

    public class ObjectDetalheMovimentoBancario {

        private String conta;
        private Double valor;

        public ObjectDetalheMovimentoBancario(String conta, Double valor) {
            this.conta = conta;
            this.valor = valor;
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public String getValorString() {
            return Moeda.converteR$Double(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteUS$(valorString);
        }

    }
}
