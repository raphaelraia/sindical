package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.Plano5Dao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class DepositoBancarioBean implements Serializable {

    private int idConta = 0;
    private List<SelectItem> listaConta = new ArrayList();
    private final List<ObjectCheque> listaCheques = new ArrayList();
    private List<ObjectCheque> listaSelecionado = new ArrayList();
    private Float valorTotal = (float) 0;
    private Float valorTotalSelecionado = (float) 0;

    public DepositoBancarioBean() {
        loadListaCheques();
    }

    public final void loadListaCheques() {
        listaCheques.clear();
        listaSelecionado.clear();
        FinanceiroDao db = new FinanceiroDao();
        Dao dao = new Dao();

        List<Object> result = db.listaDeCheques(7);

        for (Object lista : result) {
            List linha = (List) lista;
            ChequeRec cheque = (ChequeRec) dao.find(new ChequeRec(), (Integer) linha.get(0));
            Baixa baixa = (Baixa) dao.find(new Baixa(), (Integer) linha.get(1));
            FormaPagamento forma_pagamento = (FormaPagamento) dao.find(new FormaPagamento(), (Integer) linha.get(9));

            listaCheques.add(
                    new ObjectCheque(cheque, forma_pagamento, baixa)
            );

            valorTotal = Moeda.somaValores(valorTotal, forma_pagamento.getValor());
        }

    }

    public void calculoValores() {
        valorTotalSelecionado = (float) 0;

        for (ObjectCheque oc : listaSelecionado) {
            valorTotalSelecionado = Moeda.somaValores(valorTotalSelecionado, oc.getFormaPagamento().getValor());
        }
    }

    public void depositar() {
        if (listaSelecionado.isEmpty()) {
            GenericaMensagem.warn("Erro", "Nenhum cheque foi selecionado!");
            return;
        }

        Dao dao = new Dao();
        // VERIFICA STATUS DO CHEQUE
        for (int i = 0; i < listaSelecionado.size(); i++) {
            if (!mensagemStatus(listaSelecionado.get(i).getFormaPagamento().getStatus().getId())) {
                return;
            }
        }

        dao.openTransaction();
        Plano5 plano_combo = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaConta.get(idConta).getDescription()));
        Plano5 plano_caixa = (Plano5) dao.find(new Plano5(), 1);

        String historico_contabil = "Deposito bancário para a conta " + plano_combo.getConta();

        FStatus fstatus_liquidado = (FStatus) dao.find(new FStatus(), 9);
        // UPDATE NOS CHEQUES
        for (int i = 0; i < listaSelecionado.size(); i++) {
            listaSelecionado.get(i).getFormaPagamento().setStatus(fstatus_liquidado);

            if (!dao.update(listaSelecionado.get(i).getFormaPagamento())) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Saida!");
                dao.rollback();
                return;
            }
        }

        // MOVIMENTO SAIDA -----------------------------------------------------
        Baixa baixa_saida = null;
        Lote lote_saida = null;
        Movimento movimento_saida = null;
        for (int i = 0; i < listaSelecionado.size(); i++) {
            if (baixa_saida == null) {
                baixa_saida = novaBaixa();
                if (!dao.save(baixa_saida)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Saida!");
                    dao.rollback();
                    return;
                }
            }

            float valor = listaSelecionado.get(i).getFormaPagamento().getValor();

            if (lote_saida == null) {
                lote_saida = novoLote(dao, "P", plano_combo, listaSelecionado.get(i).getChequeRec(), valor, (FStatus) dao.find(new FStatus(), 1), historico_contabil);
                if (!dao.save(lote_saida)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Saida!");
                    dao.rollback();
                    return;
                }
            }

            if (movimento_saida == null) {
                movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");
                if (!dao.save(movimento_saida)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Saida!");
                    dao.rollback();
                    return;
                }
            }

            Plano5 plano_forma = plano_caixa;
            if (!dao.save(novaFormaPagamento(dao, baixa_saida, valor, plano_forma, listaSelecionado.get(i).getChequeRec(), (FStatus) dao.find(new FStatus(), 15), listaSelecionado.get(i).getFormaPagamento().getDevolucao()))) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
                dao.rollback();
                return;
            }
        }

        // MOVIMENTO ENTRADA ---------------------------------------------------
        Baixa baixa_entrada = null;
        Lote lote_entrada = null;
        Movimento movimento_entrada = null;
        for (int i = 0; i < listaSelecionado.size(); i++) {
            if (baixa_entrada == null) {
                baixa_entrada = novaBaixa();
                if (!dao.save(baixa_entrada)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel salvar Baixa Entrada!");
                    dao.rollback();
                    return;
                }
            }

            float valor = listaSelecionado.get(i).getFormaPagamento().getValor();

            Plano5 plano = plano_caixa;

            if (lote_entrada == null) {
                lote_entrada = novoLote(dao, "R", plano, listaSelecionado.get(i).getChequeRec(), valor, (FStatus) dao.find(new FStatus(), 14), historico_contabil);
                if (!dao.save(lote_entrada)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel salvar Lote Entrada!");
                    dao.rollback();
                    return;
                }
            }

            movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");
            if (!dao.save(movimento_entrada)) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Movimento Entrada!");
                dao.rollback();
                return;
            }

//            listaSelecionado.get(i).getFormaPagamento().setStatus((FStatus) dao.find(new FStatus(), 8));
//            if (!dao.update(listaSelecionado.get(i).getFormaPagamento())) {
//                GenericaMensagem.warn("Erro", "Não foi possivel atualizar cheque!");
//                dao.rollback();
//                return;
//            }
            Plano5 plano_forma = plano_combo;
            if (!dao.save(novaFormaPagamento(dao, baixa_entrada, valor, plano_forma, listaSelecionado.get(i).getChequeRec(), (FStatus) dao.find(new FStatus(), 8), listaSelecionado.get(i).getFormaPagamento().getDevolucao()))) {
                GenericaMensagem.warn("Erro", "Não foi possivel salvar Forma de Pagamento Saida!");
                dao.rollback();
                return;
            }
        }

        dao.commit();

        loadListaCheques();
        GenericaMensagem.info("Sucesso", "Cheques depositados com Sucesso!");
    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, ChequeRec cheque, float valor, FStatus fstatus, String historico_contabil) {
        return new Lote(
                -1,
                (Rotina) dao.find(new Rotina(), 224), // ROTINA
                pag_rec, // PAG REC
                DataHoje.data(), // LANCAMENTO
                (Pessoa) dao.find(new Pessoa(), 0), // PESSOA
                plano, // PLANO 5
                false,// VENCER CONTABIL
                cheque.getCheque(), // DOCUMENTO
                valor, // VALOR
                (Filial) dao.find(new Filial(), 1), // FILIAL
                null, // DEPARTAMENTO
                null, // EVT
                historico_contabil, // HISTÓRICO
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
                historico_contabil, // HISTÓRICO CONTÁBIL
                null
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
                null // MATRICULA SÓCIO
        );
    }

    public Baixa novaBaixa() {
        return new Baixa(
                -1,
                (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario"),
                DataHoje.dataHoje(),
                null,
                0,
                "",
                null,
                null,
                null,
                0
        );
    }

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, float valor, Plano5 plano, ChequeRec cheque, FStatus fstatus, Integer devolucao) {
        return new FormaPagamento(
                -1,
                baixa,
                cheque,
                null,
                100,
                valor,
                (Filial) dao.find(new Filial(), 1),
                plano,
                null,
                null,
                (TipoPagamento) dao.find(new TipoPagamento(), 8), // 4 - CHEQUE / 5 - CHEQUE PRE / 8 - Depósito Bancário
                0,
                DataHoje.dataHoje(),
                0,
                fstatus,
                devolucao, 
                null, 
                null, 
                null
        );
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

    public boolean mensagemStatus(int id_status) {
        if (id_status == 8) {
            GenericaMensagem.warn("Erro", "Cheque DEPOSITADO não pode ser selecionado!");
            return false;
        } else if (id_status == 9) {
            GenericaMensagem.warn("Erro", "Cheque LIQUIDADO não pode ser selecionado!");
            return false;
        } else if (id_status == 10) {
            GenericaMensagem.warn("Erro", "Cheque DEVOLVIDO não pode ser selecionado!");
            return false;
        } else if (id_status == 11) {
            GenericaMensagem.warn("Erro", "Cheque SUSTADO não pode ser selecionado!");
            return false;
        }
        return true;
    }

    public void setListaConta(List<SelectItem> listaConta) {
        this.listaConta = listaConta;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public List<ObjectCheque> getListaCheques() {
        return listaCheques;
    }

    public List<ObjectCheque> getListaSelecionado() {
        return listaSelecionado;
    }

    public void setListaSelecionado(List<ObjectCheque> listaSelecionado) {
        this.listaSelecionado = listaSelecionado;
    }

    public Float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorTotalString() {
        return Moeda.converteR$Float(valorTotal);
    }

    public void setValorTotalString(String valorTotalString) {
        this.valorTotal = Moeda.converteUS$(valorTotalString);
    }

    public Float getValorTotalSelecionado() {
        return valorTotalSelecionado;
    }

    public void setValorTotalSelecionado(Float valorTotalSelecionado) {
        this.valorTotalSelecionado = valorTotalSelecionado;
    }

    public String getValorTotalSelecionadoString() {
        return Moeda.converteR$Float(valorTotalSelecionado);
    }

    public void setValorTotalSelecionadoString(String valorTotalSelecionadoString) {
        this.valorTotalSelecionado = Moeda.converteUS$(valorTotalSelecionadoString);
    }

    public class ObjectCheque {

        private ChequeRec chequeRec = new ChequeRec();
        private FormaPagamento formaPagamento = new FormaPagamento();
        private Baixa baixa = new Baixa();

        public ObjectCheque(ChequeRec chequeRec, FormaPagamento formaPagamento, Baixa baixa) {
            this.chequeRec = chequeRec;
            this.formaPagamento = formaPagamento;
            this.baixa = baixa;
        }

        public ChequeRec getChequeRec() {
            return chequeRec;
        }

        public void setChequeRec(ChequeRec chequeRec) {
            this.chequeRec = chequeRec;
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
    }

}
