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
import br.com.rtools.financeiro.dao.ConciliacaoDao;
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
public class ConciliacaoBean implements Serializable {

    private List<SelectItem> listaConta = new ArrayList();
    private Integer indexListaConta = 0;

    private String filtro = "conciliar";

    private List<ObjectConciliacao> listaConciliacao = new ArrayList();

    private ObjectConciliacao objectConciliacaoSelecionado = new ObjectConciliacao();

    private List<SelectItem> listaObjectParaConciliar = new ArrayList();
    private Integer indexListaObjectParaConciliar = 0;

    public ConciliacaoBean() {
        loadListaConta();
        loadListaMovimento();
    }

    public void conciliar() {
        ConciliacaoDao c_dao = new ConciliacaoDao();
        Dao dao = new Dao();

        dao.openTransaction();

        Plano5 pl5_conciliacao = c_dao.pesquisaPlano5Conciliacao();
        if (pl5_conciliacao == null) {
            GenericaMensagem.warn("Erro", "Plano 5 Conciliação não encontrado para conta tipo = 4");
            dao.rollback();
            return;
        }

        String historico_contabil
                = "Referente a conciliação do recebimento em Depósito Bancário (" + objectConciliacaoSelecionado.getTipoPagamento().getDescricao() + ") "
                + "na conta (" + objectConciliacaoSelecionado.getFormaPagamento().getPlano5().getConta() + ")";
        Lote lote_saida = novoLote(dao, "P", pl5_conciliacao, objectConciliacaoSelecionado.getValor(), (FStatus) dao.find(new FStatus(), 1), historico_contabil);

        if (!dao.save(lote_saida)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Lote");
            dao.rollback();
            return;
        }

        Baixa baixa_saida = novaBaixa();

        if (!dao.save(baixa_saida)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Baixa");
            dao.rollback();
            return;
        }

        Movimento movimento_saida = novoMovimento(dao, lote_saida, baixa_saida, "S");

        if (!dao.save(movimento_saida)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Movimento");
            dao.rollback();
            return;
        }

        Plano5 plano_saida = (Plano5) dao.find(new Plano5(), 1);
        FormaPagamento forma_saida = novaFormaPagamento(dao, baixa_saida, lote_saida.getValor(), plano_saida);

        if (!dao.save(forma_saida)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento");
            dao.rollback();
            return;
        }

        FormaPagamento forma_conciliada = (FormaPagamento) dao.find(new FormaPagamento(), Integer.valueOf(listaObjectParaConciliar.get(indexListaObjectParaConciliar).getDescription()));
        FormaPagamento forma_conciliada_selecionada = objectConciliacaoSelecionado.getFormaPagamento();

        forma_conciliada.setConciliado(forma_conciliada_selecionada);
        forma_conciliada_selecionada.setConciliado(forma_conciliada);

        if (!dao.update(forma_conciliada) || !dao.update(forma_conciliada_selecionada)) {
            GenericaMensagem.warn("Erro", "Erro ao salvar Forma de Pagamento Conciliada!");
            dao.rollback();
            return;
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Movimentos Conciliados!");

        loadListaMovimento();
    }

    public void filtrar() {
        loadListaMovimento();
    }

    public final void loadListaMovimento() {
        listaConciliacao.clear();

        ConciliacaoDao c_dao = new ConciliacaoDao();

        List<Object> result = c_dao.listaConciliacao(Integer.valueOf(listaConta.get(indexListaConta).getDescription()), "04/07/2016", filtro);

        Dao dao = new Dao();

        for (Object lista : result) {
            List linha = (List) lista;

            listaConciliacao.add(
                    new ObjectConciliacao(
                            (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(0)),
                            (TipoPagamento) dao.find(new TipoPagamento(), (Integer) linha.get(1)),
                            ((Double) linha.get(2)),
                            (Date) linha.get(3),
                            (FormaPagamento) dao.find(new FormaPagamento(), (Integer) linha.get(4)),
                            (FormaPagamento) dao.find(new FormaPagamento(), (Integer) linha.get(5))
                    )
            );
        }
    }

    public void selecionarParaConciliacao(ObjectConciliacao oc) {
        listaObjectParaConciliar.clear();
        indexListaObjectParaConciliar = 0;

        objectConciliacaoSelecionado = oc;

        ConciliacaoDao c_dao = new ConciliacaoDao();
        //Dao dao = new Dao();

        List<Object> result = c_dao.listaParaConciliar(Integer.valueOf(listaConta.get(indexListaConta).getDescription()), oc.getDataConciliacaoString(), oc.getValor());
        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);

            listaObjectParaConciliar.add(
                    new SelectItem(
                            i,
                            DataHoje.converteData((Date) linha.get(0)) + " - " + Moeda.converteR$Double((Double) linha.get(1)),
                            "" + linha.get(2)
                    )
            );
        }

        if (listaObjectParaConciliar.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhum movimento encontrado para conciliar!");
            PF.update("formConciliacao");
            return;
        }

        PF.update("formConciliacao:panel_conciliar");
        PF.openDialog("dlg_conciliar");
    }

    public final void loadListaConta() {
        listaConta.clear();

        ConciliacaoDao db = new ConciliacaoDao();

        List<Plano5> result = db.listaCaixaBancoVW();

        for (int i = 0; i < result.size(); i++) {
            listaConta.add(
                    new SelectItem(
                            i,
                            result.get(i).getConta(),
                            Integer.toString((result.get(i).getId()))
                    )
            );
        }
    }

    public Lote novoLote(Dao dao, String pag_rec, Plano5 plano, double valor, FStatus fstatus, String historico_contabil) {
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

    public FormaPagamento novaFormaPagamento(Dao dao, Baixa baixa, double valor, Plano5 plano) {
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
                null
        );
    }

    public List<SelectItem> getListaConta() {
        return listaConta;
    }

    public void setListaConta(List<SelectItem> listaConta) {
        this.listaConta = listaConta;
    }

    public Integer getIndexListaConta() {
        return indexListaConta;
    }

    public void setIndexListaConta(Integer indexListaConta) {
        this.indexListaConta = indexListaConta;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<ObjectConciliacao> getListaConciliacao() {
        return listaConciliacao;
    }

    public void setListaConciliacao(List<ObjectConciliacao> listaConciliacao) {
        this.listaConciliacao = listaConciliacao;
    }

    public ObjectConciliacao getObjectConciliacaoSelecionado() {
        return objectConciliacaoSelecionado;
    }

    public void setObjectConciliacaoSelecionado(ObjectConciliacao objectConciliacaoSelecionado) {
        this.objectConciliacaoSelecionado = objectConciliacaoSelecionado;
    }

    public List<SelectItem> getListaObjectParaConciliar() {
        return listaObjectParaConciliar;
    }

    public void setListaObjectParaConciliar(List<SelectItem> listaObjectParaConciliar) {
        this.listaObjectParaConciliar = listaObjectParaConciliar;
    }

    public Integer getIndexListaObjectParaConciliar() {
        return indexListaObjectParaConciliar;
    }

    public void setIndexListaObjectParaConciliar(Integer indexListaObjectParaConciliar) {
        this.indexListaObjectParaConciliar = indexListaObjectParaConciliar;
    }

    public class ObjectConciliacao {

        private Pessoa pessoa;
        private TipoPagamento tipoPagamento;
        private Double valor;
        private Date dataConciliacao;
        private FormaPagamento formaPagamento;
        private FormaPagamento conciliado;

        public ObjectConciliacao() {
            this.pessoa = new Pessoa();
            this.tipoPagamento = new TipoPagamento();
            this.valor = new Double(0);
            this.dataConciliacao = DataHoje.dataHoje();
            this.formaPagamento = new FormaPagamento();
            this.conciliado = new FormaPagamento();
        }

        public ObjectConciliacao(Pessoa pessoa, TipoPagamento tipoPagamento, Double valor, Date dataConciliacao, FormaPagamento formaPagamento, FormaPagamento conciliado) {
            this.pessoa = pessoa;
            this.tipoPagamento = tipoPagamento;
            this.valor = valor;
            this.dataConciliacao = dataConciliacao;
            this.formaPagamento = formaPagamento;
            this.conciliado = conciliado;
        }

        public Pessoa getPessoa() {
            return pessoa;
        }

        public void setPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        public TipoPagamento getTipoPagamento() {
            return tipoPagamento;
        }

        public void setTipoPagamento(TipoPagamento tipoPagamento) {
            this.tipoPagamento = tipoPagamento;
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

        public FormaPagamento getFormaPagamento() {
            return formaPagamento;
        }

        public void setFormaPagamento(FormaPagamento formaPagamento) {
            this.formaPagamento = formaPagamento;
        }

        public FormaPagamento getConciliado() {
            return conciliado;
        }

        public void setConciliado(FormaPagamento conciliado) {
            this.conciliado = conciliado;
        }
    }

    public class ObjectParaConciliar {

        private Date baixa;
        private Double valorBaixa;

        public ObjectParaConciliar() {
            this.baixa = null;
            this.valorBaixa = null;
        }

        public ObjectParaConciliar(Date baixa, Double valorBaixa) {
            this.baixa = baixa;
            this.valorBaixa = valorBaixa;
        }

        public Date getBaixa() {
            return baixa;
        }

        public void setBaixa(Date baixa) {
            this.baixa = baixa;
        }

        public String getBaixaString() {
            return DataHoje.converteData(baixa);
        }

        public void setBaixaString(String baixaString) {
            this.baixa = DataHoje.converte(baixaString);
        }

        public Double getValorBaixa() {
            return valorBaixa;
        }

        public void setValorBaixa(Double valorBaixa) {
            this.valorBaixa = valorBaixa;
        }

        public String getValorBaixaString() {
            return Moeda.converteR$Double(valorBaixa);
        }

        public void setValorBaixaString(String valorBaixaString) {
            this.valorBaixa = Moeda.converteUS$(valorBaixaString);
        }
    }
}
