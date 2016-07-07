/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
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
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class TransferenciaEntreContasBean implements Serializable {

    private Float valor = (float) 0;
    private Integer indexContaEntrada = 0;
    private List<SelectItem> listaContaEntrada = new ArrayList();
    private Integer indexContaSaida = 0;
    private List<SelectItem> listaContaSaida = new ArrayList();

    public TransferenciaEntreContasBean() {
        loadListaContaSaida();
    }

    public void novo() {
        indexContaSaida = 0;
        indexContaEntrada = 0;
        valor = (float) 0;
    }

    public final void loadListaContaSaida() {
        listaContaSaida.clear();

        FinanceiroDao db = new FinanceiroDao();
        List<Object> result = db.listaContasParaTransferencia(null);
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

        FinanceiroDao db = new FinanceiroDao();
        List<Object> result;
        if (Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()) == 0) {
            result = db.listaContasParaTransferencia(null);
        } else {
            result = db.listaContasParaTransferencia(Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()));
        }

        listaContaEntrada.add(
                new SelectItem(0, "Selecione uma Conta de Entrada", "0")
        );

        for (int i = 0; i < result.size(); i++) {
            List linha = (List) (Object) result.get(i);
            listaContaEntrada.add(
                    new SelectItem(
                            i + 1,
                            (String) linha.get(1),
                            Integer.toString((Integer) linha.get(0)))
            );
        }
    }

    public void transferir() {
        Dao dao = new Dao();

        if (!listaContaSaida.isEmpty() && Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()) == 0) {
            GenericaMensagem.error("Atenção", "Selecione uma Conta de Saída!");
            return;
        }

        if (!listaContaEntrada.isEmpty() && Integer.valueOf(listaContaEntrada.get(indexContaEntrada).getDescription()) == 0) {
            GenericaMensagem.error("Atenção", "Selecione uma Conta de Entrada!");
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

        Plano5 plano_saida = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaContaSaida.get(indexContaSaida).getDescription()));
        Plano5 plano_entrada = (Plano5) dao.find(new Plano5(), Integer.valueOf(listaContaEntrada.get(indexContaEntrada).getDescription()));

        String historico_contabil = "Transferência bancária da conta " + listaContaSaida.get(indexContaSaida).getLabel() + " para a conta " + listaContaEntrada.get(indexContaEntrada).getLabel();
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

        FormaPagamento forma_saida = novaFormaPagamento(dao, baixa_saida, lote_saida.getValor(), plano_saida);
        FormaPagamento forma_entrada = novaFormaPagamento(dao, baixa_entrada, lote_entrada.getValor(), plano_entrada);

        if (!dao.save(forma_saida) || !dao.save(forma_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento");
            dao.rollback();
            return;
        }

        dao.commit();

        novo();

        GenericaMensagem.info("Sucesso", "Transferência entre Contas Concluída!");
    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, float valor, FStatus fstatus, String historico_contabil) {
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
                historico_contabil
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

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, float valor, Plano5 plano) {
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
                (TipoPagamento) dao.find(new TipoPagamento(), 10),
                0,
                DataHoje.dataHoje(),
                0,
                null,
                0, 
                null,
                null, 
                null
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
                0
        );
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

    public Integer getIndexContaEntrada() {
        return indexContaEntrada;
    }

    public void setIndexContaEntrada(Integer indexContaEntrada) {
        this.indexContaEntrada = indexContaEntrada;
    }

    public List<SelectItem> getListaContaEntrada() {
        return listaContaEntrada;
    }

    public void setListaContaEntrada(List<SelectItem> listaContaEntrada) {
        this.listaContaEntrada = listaContaEntrada;
    }

    public Integer getIndexContaSaida() {
        return indexContaSaida;
    }

    public void setIndexContaSaida(Integer indexContaSaida) {
        this.indexContaSaida = indexContaSaida;
    }

    public List<SelectItem> getListaContaSaida() {
        return listaContaSaida;
    }

    public void setListaContaSaida(List<SelectItem> listaContaSaida) {
        this.listaContaSaida = listaContaSaida;
    }

}
