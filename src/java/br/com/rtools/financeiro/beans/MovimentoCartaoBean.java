package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.Cartao;
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
import br.com.rtools.financeiro.dao.MovimentoCartaoDao;
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
import java.util.Date;
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
public class MovimentoCartaoBean implements Serializable {

    private Integer indexCartaoCombo = 0;
    private List<SelectItem> listaCartaoCombo = new ArrayList();
    private List<ObjectListaCartoes> listaCartoes = new ArrayList();
    private List<ObjectListaCartoes> listaCartoesSelecionado = new ArrayList();

    private Double valorTotal = new Double(0);
    private Double valorTotalSelecionado = new Double(0);

    private Double valorTotalLiquido = new Double(0);
    private Double valorTotalLiquidoSelecionado = new Double(0);

    private Cartao cartaoSelecionado = new Cartao();
    private Date dataTransferencia = DataHoje.dataHoje();

    public MovimentoCartaoBean() {
        loadListaCartaoCombo();
        loadListaCartoes();
    }

    public String calculoSomaValores(ObjectListaCartoes linha_oc) {
        Double somaValores = new Double(0);
        for (ObjectListaCartoes oc : listaCartoes) {
            if (oc.getFormaPagamento().getBaixa().getBaixa().equals(linha_oc.getFormaPagamento().getBaixa().getBaixa())) {
                somaValores = Moeda.soma(somaValores, oc.getValor());
            }
        }
        return Moeda.converteR$Double(somaValores);
    }

    public String calculoSomaLiquidos(ObjectListaCartoes linha_oc) {
        Double somaLiquidos = new Double(0);
        for (ObjectListaCartoes oc : listaCartoes) {
            if (oc.getFormaPagamento().getBaixa().getBaixa().equals(linha_oc.getFormaPagamento().getBaixa().getBaixa())) {
                somaLiquidos = Moeda.soma(somaLiquidos, oc.getLiquido());
            }
        }
        return Moeda.converteR$Double(somaLiquidos);
    }

    // METODO ANTIGO ROGÉRIO CHAMADO #2310
//    public void transferirCartao() {
//        if (listaCartoesSelecionado.isEmpty()) {
//            GenericaMensagem.fatal("Atenção", "Selecione pelo menos um Cartão para transferir!");
//            return;
//        }
//
//        Dao dao = new Dao();
//        dao.openTransaction();
//
//        for (ObjectListaCartoes oc : listaCartoesSelecionado) {
//            oc.getFormaPagamento().setStatus((FStatus) dao.find(new FStatus(), 9));
//
//            if (!dao.update(oc.getFormaPagamento())) {
//                GenericaMensagem.error("Atenção", "Não foi possível atualizar Status!");
//                dao.rollback();
//                return;
//            }
//        }
//
//        Cartao cart = (Cartao) dao.find(new Cartao(), Integer.valueOf(listaCartaoCombo.get(indexCartaoCombo).getDescription()));
//
//        Plano5 plano_saida = cart.getPlano5();
//        //Plano5 plano_entrada = (Plano5) dao.find(new Plano5(), 1);
//        Plano5 plano_entrada = cart.getPlano5Baixa();
//        Plano5 plano_saida_despesa = cart.getPlano5Despesa();
//
//        String historico_saida = "Referente ao repasse liquido (sem a taxa financeira) dos recebimentos de cartões para a conta (" + plano_saida.getConta() + ")";
//        Lote lote_saida = novoLote(dao, "P", plano_saida, valorTotalLiquidoSelecionado, (FStatus) dao.find(new FStatus(), 1), historico_saida);
//
//        String historico_entrada = "Referente ao repasse liquido (sem a taxa financeira) dos recebimentos de cartões para a conta (" + plano_saida.getConta() + ")";
//        Lote lote_entrada = novoLote(dao, "R", plano_entrada, valorTotalLiquidoSelecionado, (FStatus) dao.find(new FStatus(), 14), historico_entrada);
//
//        String historico_saida_despesa = "Referente ao pagamento de despesa financeira do repasse de recebimento de cartões";
//        Lote lote_saida_despesa = novoLote(dao, "P", plano_saida_despesa, Moeda.subtracao(valorTotalSelecionado, valorTotalLiquidoSelecionado), (FStatus) dao.find(new FStatus(), 1), historico_saida_despesa);
//
//        if (!dao.save(lote_saida) || !dao.save(lote_entrada) || !dao.save(lote_saida_despesa)) {
//            GenericaMensagem.warn("Erro", "Erro ao salvar Lote");
//            dao.rollback();
//            return;
//        }
//
//        Baixa baixa_saida = novaBaixa();
//        Baixa baixa_entrada = novaBaixa();
//        Baixa baixa_saida_despesa = novaBaixa();
//
//        if (!dao.save(baixa_saida) || !dao.save(baixa_entrada) || !dao.save(baixa_saida_despesa)) {
//            GenericaMensagem.warn("Erro", "Erro ao salvar Baixa");
//            dao.rollback();
//            return;
//        }
//
//        Movimento movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");
//        Movimento movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");
//        Movimento movimento_saida_despesa = novoMovimento(dao, lote_saida_despesa, baixa_saida_despesa, "S");
//
//        if (!dao.save(movimento_saida) || !dao.save(movimento_entrada) || !dao.save(movimento_saida_despesa)) {
//            GenericaMensagem.warn("Erro", "Erro ao salvar Movimento");
//            dao.rollback();
//            return;
//        }
//
//        FormaPagamento forma_saida = novaFormaPagamento(dao, baixa_saida, lote_saida.getValor(), plano_entrada);
//        FormaPagamento forma_entrada = novaFormaPagamento(dao, baixa_entrada, lote_entrada.getValor(), plano_saida);
//        FormaPagamento forma_saida_despesa = novaFormaPagamento(dao, baixa_saida_despesa, lote_saida_despesa.getValor(), plano_entrada);
//
//        if (!dao.save(forma_saida) || !dao.save(forma_entrada) || !dao.save(forma_saida_despesa)) {
//            GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento");
//            dao.rollback();
//            return;
//        }
//
//        dao.commit();
//
//        GenericaMensagem.info("Sucesso", "Cartões transferidos!");
//        loadListaCartoes();
//    }
    public void transferirCartao() {
        if (listaCartoesSelecionado.isEmpty()) {
            GenericaMensagem.fatal("Atenção", "Selecione pelo menos um Cartão para transferir!");
            return;
        }

        if (dataTransferencia == null) {
            GenericaMensagem.fatal("Atenção", "Digite uma data para o crédito!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        for (ObjectListaCartoes oc : listaCartoesSelecionado) {
            oc.getFormaPagamento().setStatus((FStatus) dao.find(new FStatus(), 9));

            if (!dao.update(oc.getFormaPagamento())) {
                GenericaMensagem.error("Atenção", "Não foi possível atualizar Status!");
                dao.rollback();
                return;
            }
        }

        cartaoSelecionado = (Cartao) dao.find(new Cartao(), Integer.valueOf(listaCartaoCombo.get(indexCartaoCombo).getDescription()));

        Plano5 plano_entrada = cartaoSelecionado.getPlano5Baixa();

        String historico_entrada = "Referente ao repasse de recebimento(s) (" + plano_entrada.getConta() + ") para a conta " + cartaoSelecionado.getPlano5().getConta() + ".";
        Lote lote_entrada = novoLote(dao, "R", plano_entrada, valorTotalLiquidoSelecionado, (FStatus) dao.find(new FStatus(), 9), historico_entrada);

        if (!dao.save(lote_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Lote");
            dao.rollback();
            return;
        }

        Baixa baixa_entrada = novaBaixa(dataTransferencia);

        if (!dao.save(baixa_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Baixa");
            dao.rollback();
            return;
        }

        Movimento movimento_entrada = novoMovimento(dao, lote_entrada, baixa_entrada, "E");

        if (!dao.save(movimento_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Movimento");
            dao.rollback();
            return;
        }

        FormaPagamento forma_entrada = novaFormaPagamento(dao, baixa_entrada, lote_entrada.getValor(), cartaoSelecionado.getPlano5(), dataTransferencia);

        if (!dao.save(forma_entrada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento");
            dao.rollback();
            return;
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Cartões transferidos!");
        loadListaCartoes();
    }

    public void calculoValores() {
        valorTotalSelecionado = new Double(0);
        valorTotalLiquidoSelecionado = new Double(0);

        for (ObjectListaCartoes oc : listaCartoesSelecionado) {
            valorTotalSelecionado = Moeda.soma(valorTotalSelecionado, oc.getValor());
            valorTotalLiquidoSelecionado = Moeda.soma(valorTotalLiquidoSelecionado, oc.getLiquido());
        }
    }

    public final void loadListaCartaoCombo() {
        listaCartaoCombo.clear();

        MovimentoCartaoDao mdao = new MovimentoCartaoDao();

        List<Object> result = mdao.listaCartoesCombo();
        Dao dao = new Dao();
        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);
            Cartao cart = (Cartao) dao.find(new Cartao(), (Integer) linha.get(0));

            listaCartaoCombo.add(
                    new SelectItem(
                            i, cart.getDescricao() + " - " + cart.getDebitoCredito(), "" + cart.getId()
                    )
            );
        }
    }

    public final void loadListaCartoes() {
        listaCartoes.clear();
        listaCartoesSelecionado.clear();

        valorTotal = new Double(0);
        valorTotalSelecionado = new Double(0);

        valorTotalLiquido = new Double(0);
        valorTotalLiquidoSelecionado = new Double(0);

        MovimentoCartaoDao mdao = new MovimentoCartaoDao();

        if (listaCartaoCombo.isEmpty()) {
            GenericaMensagem.error("ATENÇÃO", "Nenhum cartão cadastrado!");
            return;
        }

        Dao dao = new Dao();
        Cartao cart = (Cartao) dao.find(new Cartao(), Integer.valueOf(listaCartaoCombo.get(indexCartaoCombo).getDescription()));

        List<Object> result = mdao.listaCartoes(cart.getId(), cart.getPlano5().getId());
        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);

            List<Object> result_detalhe = mdao.listaCartoesDetalhe((Integer) linha.get(0));

            List<ObjectCartaoDetalhe> l_detalhe = new ArrayList();

            for (int w = 0; w < result_detalhe.size(); w++) {
                List linha_detalhe = (List) result_detalhe.get(w);

                l_detalhe.add(
                        new ObjectCartaoDetalhe(
                                (Pessoa) dao.find(new Pessoa(), (Integer) linha_detalhe.get(0)),
                                (Pessoa) dao.find(new Pessoa(), (Integer) linha_detalhe.get(1)),
                                (Pessoa) dao.find(new Pessoa(), (Integer) linha_detalhe.get(2)),
                                (Date) linha_detalhe.get(3),
                                linha_detalhe.get(4).toString(),
                                (Double) linha_detalhe.get(5)
                        )
                );

            }

            listaCartoes.add(
                    new ObjectListaCartoes(
                            (FormaPagamento) dao.find(new FormaPagamento(), (Integer) linha.get(0)),
                            (Baixa) dao.find(new Baixa(), (Integer) linha.get(1)),
                            (Date) linha.get(2),
                            (Double) linha.get(3),
                            (Double) linha.get(4),
                            (Double) linha.get(5),
                            linha.get(6).toString(),
                            (Date) linha.get(7),
                            l_detalhe
                    )
            );

            valorTotal = Moeda.soma(valorTotal, (Double) linha.get(3));
            valorTotalLiquido = Moeda.soma(valorTotalLiquido, (Double) linha.get(5));
        }

        cartaoSelecionado = (Cartao) dao.find(new Cartao(), Integer.valueOf(listaCartaoCombo.get(indexCartaoCombo).getDescription()));

    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, double valor, FStatus fstatus, String historico_contabil) {
        return new Lote(
                -1,
                (Rotina) dao.find(new Rotina(), 415), // ROTINA
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
                (FTipoDocumento) dao.find(new FTipoDocumento(), 10), // 4 - CHEQUE / 5 - CHEQUE PRE / 10 - TRANSFERENCIA BANCÁRIA
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
                (Pessoa) dao.find(new Pessoa(), 0), // TITULAR
                (Pessoa) dao.find(new Pessoa(), 0), // BENEFICIARIO
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

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano, Date data_transferencia) {
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
                data_transferencia,
                0,
                null,
                0,
                null,
                null,
                ""
        );
    }

    public Baixa novaBaixa(Date data_transferencia) {
        return new Baixa(
                -1,
                Usuario.getUsuario(),
                data_transferencia,
                null,
                0,
                "",
                null,
                null,
                null,
                0,
                0,
                data_transferencia,
                null,
                null
        );
    }

    public Integer getIndexCartaoCombo() {
        return indexCartaoCombo;
    }

    public void setIndexCartaoCombo(Integer indexCartaoCombo) {
        this.indexCartaoCombo = indexCartaoCombo;
    }

    public List<SelectItem> getListaCartaoCombo() {
        return listaCartaoCombo;
    }

    public void setListaCartaoCombo(List<SelectItem> listaCartaoCombo) {
        this.listaCartaoCombo = listaCartaoCombo;
    }

    public List<ObjectListaCartoes> getListaCartoes() {
        return listaCartoes;
    }

    public void setListaCartoes(List<ObjectListaCartoes> listaCartoes) {
        this.listaCartoes = listaCartoes;
    }

    public List<ObjectListaCartoes> getListaCartoesSelecionado() {
        return listaCartoesSelecionado;
    }

    public void setListaCartoesSelecionado(List<ObjectListaCartoes> listaCartoesSelecionado) {
        this.listaCartoesSelecionado = listaCartoesSelecionado;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorTotalString() {
        return Moeda.converteR$Double(valorTotal);
    }

    public void setValorTotalString(String valorTotalString) {
        this.valorTotal = Moeda.converteUS$(valorTotalString);
    }

    public Double getValorTotalSelecionado() {
        return valorTotalSelecionado;
    }

    public void setValorTotalSelecionado(Double valorTotalSelecionado) {
        this.valorTotalSelecionado = valorTotalSelecionado;
    }

    public String getValorTotalSelecionadoString() {
        return Moeda.converteR$Double(valorTotalSelecionado);
    }

    public void setValorTotalSelecionadoString(String valorTotalSelecionadoString) {
        this.valorTotalSelecionado = Moeda.converteUS$(valorTotalSelecionadoString);
    }

    public Double getValorTotalLiquido() {
        return valorTotalLiquido;
    }

    public void setValorTotalLiquido(Double valorTotalLiquido) {
        this.valorTotalLiquido = valorTotalLiquido;
    }

    public String getValorTotalLiquidoString() {
        return Moeda.converteR$Double(valorTotalLiquido);
    }

    public void setValorTotalLiquidoString(String valorTotalLiquidoString) {
        this.valorTotalLiquido = Moeda.converteUS$(valorTotalLiquidoString);
    }

    public Double getValorTotalLiquidoSelecionado() {
        return valorTotalLiquidoSelecionado;
    }

    public void setValorTotalLiquidoSelecionado(Double valorTotalLiquidoSelecionado) {
        this.valorTotalLiquidoSelecionado = valorTotalLiquidoSelecionado;
    }

    public String getValorTotalLiquidoSelecionadoString() {
        return Moeda.converteR$Double(valorTotalLiquidoSelecionado);
    }

    public void setValorTotalLiquidoSelecionadoString(String valorTotalLiquidoSelecionadoString) {
        this.valorTotalLiquidoSelecionado = Moeda.converteUS$(valorTotalLiquidoSelecionadoString);
    }

    public class ObjectListaCartoes {

        private FormaPagamento formaPagamento;
        private Baixa baixa;
        private Date data;
        private Double valor;
        private Double taxa;
        private Double liquido;
        private String baixaOrdem;
        private Date credito;
        private List<ObjectCartaoDetalhe> listaObjectCartaoDetalhe;

        public ObjectListaCartoes(FormaPagamento formaPagamento, Baixa baixa, Date data, Double valor, Double taxa, Double liquido, String baixaOrdem, Date credito, List<ObjectCartaoDetalhe> listaObjectCartaoDetalhe) {
            this.formaPagamento = formaPagamento;
            this.baixa = baixa;
            this.data = data;
            this.valor = valor;
            this.taxa = taxa;
            this.liquido = liquido;
            this.baixaOrdem = baixaOrdem;
            this.credito = credito;
            this.listaObjectCartaoDetalhe = listaObjectCartaoDetalhe;
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

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public String getDataString() {
            return DataHoje.converteData(data);
        }

        public void setDataString(String dataString) {
            this.data = DataHoje.converte(dataString);
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

        public Double getTaxa() {
            return taxa;
        }

        public void setTaxa(Double taxa) {
            this.taxa = taxa;
        }

        public String getTaxaString() {
            return Moeda.converteR$Double(taxa);
        }

        public void setTaxaString(String taxaString) {
            this.taxa = Moeda.converteUS$(taxaString);
        }

        public Double getLiquido() {
            return liquido;
        }

        public void setLiquido(Double liquido) {
            this.liquido = liquido;
        }

        public String getLiquidoString() {
            return Moeda.converteR$Double(liquido);
        }

        public void setLiquidoString(String liquidoString) {
            this.liquido = Moeda.converteUS$(liquidoString);
        }

        public String getBaixaOrdem() {
            return baixaOrdem;
        }

        public void setBaixaOrdem(String baixaOrdem) {
            this.baixaOrdem = baixaOrdem;
        }

        public Date getCredito() {
            return credito;
        }

        public void setCredito(Date credito) {
            this.credito = credito;
        }

        public String getCreditoString() {
            return DataHoje.converteData(credito);
        }

        public void setCreditoString(String creditoString) {
            this.credito = DataHoje.converte(creditoString);
        }

        public List<ObjectCartaoDetalhe> getListaObjectCartaoDetalhe() {
            return listaObjectCartaoDetalhe;
        }

        public void setListaObjectCartaoDetalhe(List<ObjectCartaoDetalhe> listaObjectCartaoDetalhe) {
            this.listaObjectCartaoDetalhe = listaObjectCartaoDetalhe;
        }

    }

    public class ObjectCartaoDetalhe {

        private Pessoa responsavel;
        private Pessoa titular;
        private Pessoa beneficiario;
        private Date vencimento;
        private String operacao;
        private Double valor;

        public ObjectCartaoDetalhe(Pessoa responsavel, Pessoa titular, Pessoa beneficiario, Date vencimento, String operacao, Double valor) {
            this.responsavel = responsavel;
            this.titular = titular;
            this.beneficiario = beneficiario;
            this.vencimento = vencimento;
            this.operacao = operacao;
            this.valor = valor;
        }

        public Pessoa getResponsavel() {
            return responsavel;
        }

        public void setResponsavel(Pessoa responsavel) {
            this.responsavel = responsavel;
        }

        public Pessoa getTitular() {
            return titular;
        }

        public void setTitular(Pessoa titular) {
            this.titular = titular;
        }

        public Pessoa getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Pessoa beneficiario) {
            this.beneficiario = beneficiario;
        }

        public Date getVencimento() {
            return vencimento;
        }

        public void setVencimento(Date vencimento) {
            this.vencimento = vencimento;
        }

        public String getVencimentoString() {
            return DataHoje.converteData(vencimento);
        }

        public void setVencimentoString(String vencimentoString) {
            this.vencimento = DataHoje.converte(vencimentoString);
        }

        public String getOperacao() {
            return operacao;
        }

        public void setOperacao(String operacao) {
            this.operacao = operacao;
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

    public Cartao getCartaoSelecionado() {
        return cartaoSelecionado;
    }

    public void setCartaoSelecionado(Cartao cartaoSelecionado) {
        this.cartaoSelecionado = cartaoSelecionado;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public String getDataTransferenciaString() {
        return DataHoje.converteData(dataTransferencia);
    }

    public void setDataTransferenciaString(String dataTransferenciaString) {
        this.dataTransferencia = DataHoje.converte(dataTransferenciaString);
    }

}
