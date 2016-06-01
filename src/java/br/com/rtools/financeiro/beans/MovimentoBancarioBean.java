package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.ContaOperacao;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.ContaOperacaoDao;
import br.com.rtools.financeiro.db.FinanceiroDB;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.financeiro.db.ServicosDB;
import br.com.rtools.financeiro.db.ServicosDBToplink;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
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
    private final List<SelectItem> listaServicos = new ArrayList();
    private String valor = "";
    private String tipo = "saida";
    private List<ObjectMovimentoBancario> listaMovimento = new ArrayList();

    private int idContaOperacao = 0;
    private List<SelectItem> listaContaOperacao = new ArrayList();

    private FormaPagamento formaPagamentoEditar = new FormaPagamento();
    private Lote loteEditar = new Lote();
    private Movimento movimentoEditar = new Movimento();

    private ObjectMovimentoBancario statusChequeEditar;
    private final List<SelectItem> listaStatus = new ArrayList();
    private Integer indexStatus = 0;

    public MovimentoBancarioBean() {
        loadListaContaOperacao();
        loadListaStatus();
    }

    public final void loadListaContaOperacao() {
        listaContaOperacao.clear();

        List<ContaOperacao> result = new ContaOperacaoDao().findByOperacao(tipo.equals("entrada") ? 7 : 8);
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                listaContaOperacao.add(
                        new SelectItem(
                                i,
                                result.get(i).getPlano5().getConta(),
                                "" + result.get(i).getId()
                        )
                );
            }
        }
    }

    public final void loadListaStatus() {
        listaStatus.clear();

        List<FStatus> result = new FinanceiroDBToplink().listaFStatusIn("8, 9, 10, 11");
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
        if ( Moeda.converteUS$(valor) <= 0){
            GenericaMensagem.warn("Atenção", "Valor não pode ser Zero!");
            return;
        }
        
        Dao dao = new Dao();
        if (formaPagamentoEditar.getId() == -1) {
            Lote lote;
            Movimento movimento;
            Baixa baixa;
            FormaPagamento forma_pagamento;
            //Plano5 plano = (Plano5) sv.pesquisaCodigo(Integer.valueOf(listaConta.get(idConta).getDescription()), "Plano5");
            ContaOperacao co = (ContaOperacao) dao.find(new ContaOperacao(), Integer.valueOf(listaContaOperacao.get(idContaOperacao).getDescription()));
            Plano5 plano5_lote = co.getPlano5();

            Plano5 plano5_forma_pagamento = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));

            if (tipo.equals("saida")) {
                baixa = novaBaixa();
                lote = novoLote(dao, "P", plano5_lote, Moeda.converteUS$(valor), (FStatus) dao.find(new FStatus(), 1));
                movimento = novoMovimento(dao, lote, baixa, "S");
                forma_pagamento = novaFormaPagamento(dao, baixa, Moeda.converteUS$(valor), plano5_forma_pagamento, null, null, (TipoPagamento) dao.find(new TipoPagamento(), 3));
            } else {
                baixa = novaBaixa();
                lote = novoLote(dao, "R", plano5_lote, Moeda.converteUS$(valor), (FStatus) dao.find(new FStatus(), 1));
                movimento = novoMovimento(dao, lote, baixa, "E");
                forma_pagamento = novaFormaPagamento(dao, baixa, Moeda.converteUS$(valor), plano5_forma_pagamento, null, null, (TipoPagamento) dao.find(new TipoPagamento(), 3));
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

            listaMovimento.clear();
            dao.commit();

            GenericaMensagem.info("Sucesso", "Movimento salvo com Sucesso!");
        } else {
            formaPagamentoEditar.setValorString(valor);
            movimentoEditar.setValorString(valor);
            movimentoEditar.setValorBaixaString(valor);
            loteEditar.setValorString(valor);

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

            GenericaMensagem.info("Sucesso", "Registro Atualizado!");
            novo();
        }
    }

    public void editar(ObjectMovimentoBancario omb) {
        formaPagamentoEditar = omb.getFormaPagamento();
        movimentoEditar = omb.getMovimento();
        loteEditar = omb.getMovimento().getLote();

        for (int i = 0; i < listaConta.size(); i++) {
            if (Integer.valueOf(listaConta.get(i).getDescription()) == omb.getFormaPagamento().getPlano5().getId()) {
                idConta = i;
            }
        }

        valor = omb.getFormaPagamento().getValorString();

        tipo = omb.getMovimento().getEs().equals("S") ? "saida" : "entrada";

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
        GenericaSessao.put("movimentoBancarioBean", new MovimentoBancarioBean());
    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, float valor, FStatus fstatus) {
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
                "Deposito bancário para a conta ??", // HISTORICO
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

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, float valor, Plano5 plano, ChequePag chequePag, ChequeRec chequeRec, TipoPagamento tipoPagamento) {
        if (chequePag != null) {
            return new FormaPagamento(
                    -1,
                    baixa,
                    null,
                    chequePag,
                    100,
                    valor,
                    (Filial) dao.find(new Filial(), 1),
                    plano,
                    null,
                    null,
                    tipoPagamento,
                    0,
                    DataHoje.dataHoje(),
                    0
            );
        }

        if (chequeRec != null) {
            return new FormaPagamento(
                    -1,
                    baixa,
                    chequeRec,
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
                    0
            );
        }

        return new FormaPagamento(
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
                0
        );
    }

    public Baixa novaBaixa() {
        return new Baixa(
                -1,
                (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"),
                DataHoje.data(),
                "",
                0,
                "",
                null,
                null,
                null,
                0
        );
    }

    public HashMap statusCheque(ObjectMovimentoBancario omb) {
        // id 8 = DEPOSITADO
        HashMap hash = new LinkedHashMap();
        if (omb != null) {
            if (omb.getChequePag() != null) {
                hash.put("status", omb.getChequePag().getStatus().getDescricao());
                
                if (omb.getChequePag().getStatus().getId() == 10 || omb.getChequePag().getStatus().getId() == 11) {
                    hash.put("cor", "color: red;");
                } else{
                    hash.put("cor", "color: black;");
                }
                return hash;
            }

            if (omb.getChequeRec() != null) {
                hash.put("status", omb.getChequeRec().getStatus().getDescricao());
                
                if (omb.getChequeRec().getStatus().getId() == 10 || omb.getChequeRec().getStatus().getId() == 11) {
                    hash.put("cor", "color: red;");
                } else{
                    hash.put("cor", "color: black;");
                }
                return hash;
            }
        }

        hash.put("status", "");
        hash.put("cor", "");
        return hash;
    }

    public Boolean mostraStatus(ObjectMovimentoBancario omb) {
        return omb != null && (omb.getChequePag() != null || omb.getChequeRec() != null);
    }

    public Boolean desabilitaStatus(ObjectMovimentoBancario linha) {
        if (linha.getChequePag() != null) {
            if (linha.getChequePag().getStatus().getId() == 8) {
                return false;
            }
        }

        if (linha.getChequeRec() != null) {
            if (linha.getChequeRec().getStatus().getId() == 8) {
                return false;
            }
        }
        
        return true;
    }

    public void selecionaStatus(ObjectMovimentoBancario linha) {
        statusChequeEditar = linha;

        if (statusChequeEditar.getChequePag() != null) {
            for (int i = 0; i < listaStatus.size(); i++) {
                if (statusChequeEditar.getChequePag().getStatus().getId() == Integer.valueOf(listaStatus.get(i).getDescription())) {
                    indexStatus = i;
                }
            }
        }

        if (statusChequeEditar.getChequeRec() != null) {
            for (int i = 0; i < listaStatus.size(); i++) {
                if (statusChequeEditar.getChequeRec().getStatus().getId() == Integer.valueOf(listaStatus.get(i).getDescription())) {
                    indexStatus = i;
                }
            }
        }
    }

    public void atualizarStatus() {
        if (statusChequeEditar.getChequePag() == null && statusChequeEditar.getChequeRec() == null) {
            GenericaMensagem.warn("Atenção", "Não existe Cheque para ser atualizado!");
            return;
        }

        if (statusChequeEditar.getChequePag() != null) {
            if (!atualizarChequePag(statusChequeEditar.getChequePag(), statusChequeEditar)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Cheque!");
                return;
            }
        } else if (statusChequeEditar.getChequeRec() != null) {
            if (!atualizarChequeRec(statusChequeEditar.getChequeRec(), statusChequeEditar)) {
                GenericaMensagem.error("Erro", "Não foi possível atualizar Cheque!");
                return;
            }
        }

        statusChequeEditar = null;
    }

    public Boolean atualizarChequePag(ChequePag chequePag, ObjectMovimentoBancario linha) {
        Integer id_status = Integer.valueOf(listaStatus.get(indexStatus).getDescription());

        Dao dao = new Dao();
        dao.openTransaction();
        //9-"LIQUIDADO"
        //10-"DEVOLVIDO"
        //11-"SUSTADO"

        if (id_status == 9) {
            chequePag.setStatus((FStatus) dao.find(new FStatus(), 9));

            if (!dao.update(chequePag)) {
                dao.rollback();
                return false;
            }

            listaMovimento.clear();
            GenericaMensagem.info("Sucesso", "Cheque LIQUIDADO Atualizado!");
            dao.commit();
        } else if (id_status == 10 || id_status == 11) {
            chequePag.setStatus((FStatus) dao.find(new FStatus(), id_status));

            if (!dao.update(chequePag)) {
                dao.rollback();
                return false;
            }

            // SAIDA -----------------------------------------------------------
            // -----------------------------------------------------------------
            Baixa baixa_saida = novaBaixa();
            if (!dao.save(baixa_saida)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Saida!");
                dao.rollback();
                return false;
            }

            Plano5 plano5_lote_saida = (Plano5) dao.find(new Plano5(), 1);

            Lote lote_saida = novoLote(dao, "P", plano5_lote_saida, linha.getFormaPagamento().getValor(), (FStatus) dao.find(new FStatus(), 14));
            if (!dao.save(lote_saida)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
                dao.rollback();
                return false;
            }

            Movimento movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");
            if (!dao.save(movimento_saida)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Saida!");
                dao.rollback();
                return false;
            }

            Plano5 plano5_forma_pagamento_saida = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
            
            if (!dao.save(novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, chequePag, null, (TipoPagamento) dao.find(new TipoPagamento(), 4)))) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
                dao.rollback();
                return false;
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------
            
            // ENTRADA ---------------------------------------------------------
            // -----------------------------------------------------------------
            Baixa baixa_entrada = novaBaixa();
            if (!dao.save(baixa_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Entrada!");
                dao.rollback();
                return false;
            }

            //Plano5 plano5_lote = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
            Plano5 plano5_lote_entrada = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));

            Lote lote_entrada = novoLote(dao, "R", plano5_lote_entrada, linha.getFormaPagamento().getValor(), (FStatus) dao.find(new FStatus(), 1));
            if (!dao.save(lote_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
                dao.rollback();
                return false;
            }

            Movimento movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");
            if (!dao.save(movimento_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Entrada!");
                dao.rollback();
                return false;
            }

            Plano5 plano5_forma_pagamento_entrada = (Plano5) dao.find(new Plano5(), 1);
            
            if (!dao.save(novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, chequePag, null, (TipoPagamento) dao.find(new TipoPagamento(), 4)))) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
                dao.rollback();
                return false;
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------
            
            listaMovimento.clear();
            dao.commit();

            if (id_status == 10) {
                GenericaMensagem.info("Sucesso", "Cheque DEVOLVIDO concluído!");
            } else {
                GenericaMensagem.info("Sucesso", "Cheque SUSTADO concluído!");
            }
        }
        return true;
    }

    public Boolean atualizarChequeRec(ChequeRec chequeRec, ObjectMovimentoBancario linha) {
        Integer id_status = Integer.valueOf(listaStatus.get(indexStatus).getDescription());

        Dao dao = new Dao();
        dao.openTransaction();

        //9-"LIQUIDADO"
        //10-"DEVOLVIDO"
        //11-"SUSTADO"
        if (id_status == 9) {
            chequeRec.setStatus((FStatus) dao.find(new FStatus(), 9));

            if (!dao.update(chequeRec)) {
                dao.rollback();
                return false;
            }

            listaMovimento.clear();
            GenericaMensagem.info("Sucesso", "Cheque LIQUIDADO Atualizado!");
            dao.commit();
        } else if (id_status == 10 || id_status == 11) {
            chequeRec.setStatus((FStatus) dao.find(new FStatus(), id_status));

            if (!dao.update(chequeRec)) {
                dao.rollback();
                return false;
            }

            // SAIDA -----------------------------------------------------------
            // -----------------------------------------------------------------
            Baixa baixa_saida = novaBaixa();
            if (!dao.save(baixa_saida)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Saida!");
                dao.rollback();
                return false;
            }

            Plano5 plano5_lote_saida = (Plano5) dao.find(new Plano5(), 1);

            Lote lote_saida = novoLote(dao, "P", plano5_lote_saida, linha.getFormaPagamento().getValor(), (FStatus) dao.find(new FStatus(), 14));
            if (!dao.save(lote_saida)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
                dao.rollback();
                return false;
            }

            Movimento movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");
            if (!dao.save(movimento_saida)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Saida!");
                dao.rollback();
                return false;
            }

            Plano5 plano5_forma_pagamento_saida = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
            
            if (!dao.save(novaFormaPagamento(dao, baixa_saida, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_saida, null, chequeRec, (TipoPagamento) dao.find(new TipoPagamento(), 4)))) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
                dao.rollback();
                return false;
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------
            
            // ENTRADA ---------------------------------------------------------
            // -----------------------------------------------------------------
            Baixa baixa_entrada = novaBaixa();
            if (!dao.save(baixa_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Entrada!");
                dao.rollback();
                return false;
            }

            //Plano5 plano5_lote = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
            Plano5 plano5_lote_entrada = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));

            Lote lote_entrada = novoLote(dao, "R", plano5_lote_entrada, linha.getFormaPagamento().getValor(), (FStatus) dao.find(new FStatus(), 1));
            if (!dao.save(lote_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
                dao.rollback();
                return false;
            }

            Movimento movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");
            if (!dao.save(movimento_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Entrada!");
                dao.rollback();
                return false;
            }

            Plano5 plano5_forma_pagamento_entrada = (Plano5) dao.find(new Plano5(), 1);
            
            if (!dao.save(novaFormaPagamento(dao, baixa_entrada, linha.getFormaPagamento().getValor(), plano5_forma_pagamento_entrada, null, chequeRec, (TipoPagamento) dao.find(new TipoPagamento(), 4)))) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
                dao.rollback();
                return false;
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            listaMovimento.clear();
            dao.commit();

            if (id_status == 10) {
                GenericaMensagem.info("Sucesso", "Cheque DEVOLVIDO concluído!");
            } else {
                GenericaMensagem.info("Sucesso", "Cheque SUSTADO concluído!");
            }
        }
        return true;
    }

    public List<SelectItem> getListaConta() {
        if (listaConta.isEmpty()) {
            Plano5Dao db = new Plano5Dao();

            List<Plano5> result = db.pesquisaCaixaBanco();
            for (int i = 0; i < result.size(); i++) {
                listaConta.add(
                        new SelectItem(
                                i,
                                result.get(i).getContaBanco().getBanco().getBanco() + " - " + result.get(i).getContaBanco().getAgencia() + " - " + result.get(i).getContaBanco().getConta(),
                                Integer.toString((result.get(i).getId()))
                        )
                );
            }
        }
        return listaConta;
    }

    public List<SelectItem> getListaServicos() {
        if (listaServicos.isEmpty()) {
            ServicosDB db = new ServicosDBToplink();
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
        if (listaMovimento.isEmpty()) {
            FinanceiroDB db = new FinanceiroDBToplink();
            Dao dao = new Dao();

            if (listaConta.isEmpty()) {
                GenericaMensagem.fatal("Erro", "Nenhuma Conta Cadastrada!");
                return new ArrayList();
            }

            Plano5 plano = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
            if (plano == null) {
                GenericaMensagem.fatal("Erro", "Plano de Contas não encontrado!");
                return new ArrayList();
            }

            List<Object> result = db.listaMovimentoBancario(plano.getId());

            for (Object lista : result) {
                List result_object = (List) lista;
                ChequeRec cheque_rec = null;
                if (result_object.get(7) != null) {
                    cheque_rec = (ChequeRec) dao.find(new ChequeRec(), (Integer) result_object.get(7));
                }

                ChequePag cheque_pag = null;
                if (result_object.get(8) != null) {
                    cheque_pag = (ChequePag) dao.find(new ChequePag(), (Integer) result_object.get(8));
                }

                List<ObjectDetalheMovimentoBancario> list_detalhe = new ArrayList();
                List<Object> result_detalhe = db.listaDetalheMovimentoBancario((Integer) result_object.get(1));
                for (Object linha : result_detalhe) {
                    List list = (List) linha;
                    list_detalhe.add(new ObjectDetalheMovimentoBancario((String) list.get(0), Moeda.converteUS$(Moeda.converteR$Double((Double) list.get(1)))));
                }

                listaMovimento.add(
                        new ObjectMovimentoBancario(
                                (FormaPagamento) dao.find(new FormaPagamento(), (Integer) result_object.get(0)), // ID FORMA PAGAMENTO
                                (Baixa) dao.find(new Baixa(), (Integer) result_object.get(1)), // ID BAIXA
                                (String) result_object.get(2), // DOCUMENTO
                                (String) result_object.get(3), // HISTORICO 
                                (Movimento) dao.find(new Movimento(), (Integer) result_object.get(4)), // ID BAIXA
                                Moeda.converteUS$(Moeda.converteR$Double((Double) result_object.get(5))), // SALDO
                                (TipoPagamento) dao.find(new TipoPagamento(), (Integer) result_object.get(6)), // ID TIPO PAGAMENTO
                                cheque_rec,
                                cheque_pag,
                                list_detalhe
                        )
                );
            }
        }
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

    public class ObjectMovimentoBancario {

        private FormaPagamento formaPagamento;
        private Baixa baixa;
        private String documento;
        private String historico;
        private Movimento movimento;
        private Float saldo;
        private TipoPagamento tipoPagamento;
        private ChequeRec chequeRec;
        private ChequePag chequePag;
        private List<ObjectDetalheMovimentoBancario> listDetalheMovimento;

        public ObjectMovimentoBancario(FormaPagamento formaPagamento, Baixa baixa, String documento, String historico, Movimento movimento, Float saldo, TipoPagamento tipoPagamento, ChequeRec chequeRec, ChequePag chequePag, List<ObjectDetalheMovimentoBancario> listDetalheMovimento) {
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

        public Float getSaldo() {
            return saldo;
        }

        public void setSaldo(Float saldo) {
            this.saldo = saldo;
        }

        public String getSaldoString() {
            return Moeda.converteR$Float(saldo);
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
    }

    public class ObjectDetalheMovimentoBancario {

        private String conta;
        private Float valor;

        public ObjectDetalheMovimentoBancario(String conta, Float valor) {
            this.conta = conta;
            this.valor = valor;
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

        public Float getValor() {
            return valor;
        }

        public void setValor(Float valor) {
            this.valor = valor;
        }

        public String getValorString() {
            return Moeda.converteR$Float(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteUS$(valorString);
        }

    }
}
