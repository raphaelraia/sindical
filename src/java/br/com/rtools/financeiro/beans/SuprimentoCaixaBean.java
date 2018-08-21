/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoPagamento;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.SuprimentoCaixaDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author claudemir
 */
@ManagedBean
@SessionScoped
public class SuprimentoCaixaBean {

    private Integer indexContaEntrada = 0;
    private final List<SelectItem> listaContaEntrada = new ArrayList();
    private Integer indexContaSaida = 0;
    private final List<SelectItem> listaContaSaida = new ArrayList();
    private String numero = "";
    private Double valor = new Double(0);

    public SuprimentoCaixaBean() {
        loadListaContaSaida();
    }

    public void novo() {
        if (GenericaSessao.exists("suprimentoCaixaBean")) {
            GenericaSessao.put("suprimentoCaixaBean", new SuprimentoCaixaBean());
        }

        PF.update("header:form_suprimento_caixa:panel_suprimento_caixa");
        PF.openDialog("dlg_suprimento_caixa");
    }

    public void novaTela() {
        if (GenericaSessao.exists("suprimentoCaixaBean")) {
            GenericaSessao.put("suprimentoCaixaBean", new SuprimentoCaixaBean());
        }
    }

    public void liquidar() {
        Dao dao = new Dao();

        if (!listaContaSaida.isEmpty() && Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()) == 0) {
            GenericaMensagem.error("Atenção", "Selecione uma Conta de Saída!");
            return;
        }

        if (listaContaEntrada.isEmpty()) {
            GenericaMensagem.error("Atenção", "Lista de Conta de Entrada vazia!");
            return;
        }

        if (Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()).equals(Integer.valueOf(listaContaEntrada.get(indexContaEntrada).getDescription()))) {
            GenericaMensagem.error("Atenção", "Contas não poder ser iguais!");
            return;
        }

        if (valor <= 0) {
            GenericaMensagem.error("Atenção", "Valor não pode ser Zerado (0)!");
            return;
        }

        if (numero == null || numero.isEmpty()) {
            GenericaMensagem.error("Atenção", "Número do cheque não pode ser vazio!");
            return;
        }

        Plano5 plano_saida = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()));
        Plano5 plano_entrada = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaContaEntrada.get(indexContaEntrada).getDescription()));

        String historico_contabil = "Suprimento de CAIXA pela conta bancária " + listaContaSaida.get(indexContaSaida).getLabel() + ", cheque número " + numero;

        Lote lote_saida = novoLote(dao, "P", plano_entrada, valor, (FStatus) dao.find(new FStatus(), 14), historico_contabil);
        Lote lote_entrada = novoLote(dao, "R", plano_saida, valor, (FStatus) dao.find(new FStatus(), 1), historico_contabil);

        dao.openTransaction();

        if (!dao.save(lote_saida) || !dao.save(lote_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Lote");
            dao.rollback();
            return;
        }

        Baixa baixa_saida = novaBaixa();
        Baixa baixa_entrada = novaBaixa();

        if (!dao.save(baixa_saida) || !dao.save(baixa_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Baixa");
            dao.rollback();
            return;
        }

        Movimento movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");
        Movimento movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");

        if (!dao.save(movimento_saida) || !dao.save(movimento_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Movimento");
            dao.rollback();
            return;
        }

        ChequePag chequePag = new ChequePag(-1, numero, DataHoje.dataHoje(), DataHoje.dataHoje(), plano_saida, null, null, null, null, null);
        ChequeRec chequeRec = new ChequeRec(-1, "", plano_saida.getContaBanco().getBanco(), plano_saida.getContaBanco().getAgencia(), plano_saida.getContaBanco().getConta(), numero, DataHoje.dataHoje(), DataHoje.dataHoje(), DataHoje.dataHoje());

        if (!dao.save(chequePag) || !dao.save(chequeRec)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Cheques");
            dao.rollback();
            return;
        }

        FormaPagamento forma_saida = novaFormaPagamento(dao, baixa_saida, lote_saida.getValor(), plano_saida, null, chequePag);
        FormaPagamento forma_entrada = novaFormaPagamento(dao, baixa_entrada, lote_entrada.getValor(), plano_entrada, chequeRec, null);

        if (!dao.save(forma_saida) || !dao.save(forma_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento");
            dao.rollback();
            return;
        }

        ContaBanco cb = plano_saida.getContaBanco();

        cb.setUChequeString(numero);

        if (!dao.update(cb)) {
            GenericaMensagem.warn("Erro", "Erro ao atualizar número cheque em Conta Banco!");
            dao.rollback();
            return;
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Liquidação Concluída!");

        novaTela();
    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, double valor, FStatus fstatus, String historico_contabil) {
        return new Lote(
                -1,
                (Rotina) dao.find(new Rotina(), 225), // ROTINA
                pag_rec, // PAG REC
                DataHoje.data(), // LANCAMENTO
                (Pessoa) dao.find(new Pessoa(), 1), // PESSOA
                plano, // PLANO 5
                false,// VENCER CONTABIL
                "", // DOCUMENTO
                valor, // VALOR
                (Filial) dao.find(new Filial(), 1), // FILIAL
                null, // DEPARTAMENTO
                null, // EVT
                historico_contabil, // HISTORICO
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
                historico_contabil,
                null,
                ""
        );
    }

    public Baixa novaBaixa() {
        return new Baixa(
                -1,
                Usuario.getUsuario(),
                DataHoje.dataHoje(),
                null,
                0,
                "",
                null,
                null,
                null,
                0,
                0,
                DataHoje.dataHoje(),
                null,
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
                lote.getPessoa(), // TITULAR
                lote.getPessoa(), // BENEFICIARIO
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

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, ChequeRec cheque_rec, ChequePag cheque_pag) {
        return new FormaPagamento(
                -1,
                baixa,
                cheque_rec,
                cheque_pag,
                100,
                valor,
                (Filial) dao.find(new Filial(), 1),
                plano,
                null,
                null,
                (TipoPagamento) dao.find(new TipoPagamento(), 10),
                0,
                DataHoje.dataHoje(),
                0,
                (FStatus) dao.find(new FStatus(), 9),
                0,
                null,
                null,
                "",
                false
        );
    }

    public final void loadListaContaSaida() {
        listaContaSaida.clear();

        SuprimentoCaixaDao dao = new SuprimentoCaixaDao();

        List<Object> result = dao.listaContasSaida();
        listaContaSaida.add(
                new SelectItem(0, "Selecione uma Conta de Saída", "0")
        );

        for (int i = 0; i < result.size(); i++) {
            List linha = (List) (Object) result.get(i);

            listaContaSaida.add(
                    new SelectItem(
                            i + 1,
                            (String) linha.get(1),
                            Integer.toString((Integer) linha.get(0)))
            );
        }

        loadListaContaEntrada();
    }

    public final void loadListaContaEntrada() {
        listaContaEntrada.clear();

        SuprimentoCaixaDao dao = new SuprimentoCaixaDao();

        List<Object> result = dao.listaContasEntrada();
        
        for (int i = 0; i < result.size(); i++) {
            List linha = (List) (Object) result.get(i);
            listaContaEntrada.add(
                    new SelectItem(
                            i,
                            (String) linha.get(1),
                            Integer.toString((Integer) linha.get(0)))
            );
        }

        numero = "";
        
        if (Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()) != 0) {
            ChequePag cp = new ChequePag();
            cp.setPlano5((Plano5) new Dao().find(new Plano5(), Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription())));

            if (cp.getNrProximoCheque() != null) {
                numero = Integer.toString(cp.getNrProximoCheque());
            } 
        }
    }

    public Integer getIndexContaEntrada() {
        return indexContaEntrada;
    }

    public void setIndexContaEntrada(Integer indexContaEntrada) {
        this.indexContaEntrada = indexContaEntrada;
    }

    public final List<SelectItem> getListaContaEntrada() {
        return listaContaEntrada;
    }

    public Integer getIndexContaSaida() {
        return indexContaSaida;
    }

    public void setIndexContaSaida(Integer indexContaSaida) {
        this.indexContaSaida = indexContaSaida;
    }

    public final List<SelectItem> getListaContaSaida() {
        return listaContaSaida;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
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
        this.valor = Moeda.converteStringToDouble(valorString);
    }
}
