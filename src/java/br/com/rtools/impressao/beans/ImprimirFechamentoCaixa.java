package br.com.rtools.impressao.beans;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.ChequeRec;
import br.com.rtools.financeiro.FechamentoCaixa;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.TransferenciaCaixa;
import br.com.rtools.financeiro.beans.ConfiguracaoFinanceiroBean;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.impressao.ParametroEstornoCaixa;
import br.com.rtools.impressao.ParametroFechamentoCaixa;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import net.sf.jasperreports.engine.JasperReport;

public class ImprimirFechamentoCaixa {

    private final ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();

    @PostConstruct
    public void init() {
        cfb.init();
    }

    public void imprimirApenasEstorno(Integer id_caixa, String data) {
        FinanceiroDao db = new FinanceiroDao();

        Caixa caixa = (Caixa) (new Dao().find(new Caixa(), id_caixa));
        List<Object> result_list_estorno = db.listaEstornoFechamentoCaixa(caixa.getId(), data);

        JasperReport j_estorno_caixa = Jasper.load("ESTORNO_CAIXA.jasper");

        Jasper jasper = new Jasper();
        jasper.start();

        if (!result_list_estorno.isEmpty()) {
            List<ParametroEstornoCaixa> li = new ArrayList();

            for (Object ob : result_list_estorno) {
                List linha = (List) ob;
                li.add(
                        new ParametroEstornoCaixa(
                                linha.get(0),
                                linha.get(1),
                                linha.get(2),
                                linha.get(3),
                                linha.get(4),
                                linha.get(5),
                                linha.get(6),
                                linha.get(7)
                        )
                );
            }

            jasper.add(j_estorno_caixa, li);
        }
        jasper.finish("Estornos do Caixa");
    }

    public void imprimir(Integer id_fechamento, Integer id_caixa) {
        cfb.init();
        Caixa caixa = (Caixa) (new Dao().find(new Caixa(), id_caixa));
        FinanceiroDao db = new FinanceiroDao();
        FechamentoCaixa fc = (FechamentoCaixa) (new Dao().find(new FechamentoCaixa(), id_fechamento));

        List<Object> result_list_estorno = db.listaEstornoFechamentoCaixa(fc.getId());

        List<FormaPagamento> lista_fp_entrada = db.listaTransferenciaFormaPagamento(fc.getId(), caixa.getId(), "E");
        List<FormaPagamento> lista_fp_saida = db.listaTransferenciaFormaPagamento(fc.getId(), caixa.getId(), "S");
        double transferencia_entrada = 0, transferencia_saida = 0, transferencia_saida_central = 0, dinheiro_baixa = 0, cheque = 0, cheque_pre = 0, cartao_cre = 0, cartao_deb = 0, saldo_atual = 0;
        double deposito_bancario = 0, doc_bancario = 0, transferencia_bancaria = 0, ticket = 0, debito = 0, boleto = 0;
        double dinheiro_pagamento = 0, outros_pagamento = 0;
        Collection lista = new ArrayList();
        List<DataObject> lista_cheque = new ArrayList();

        List<TransferenciaCaixa> lEntrada = db.listaTransferenciaDinheiroEntrada(fc.getId(), caixa.getId());

        List<TransferenciaCaixa> lSaida = db.listaTransferenciaDinheiroSaidaEntreCaixas(fc.getId(), caixa.getId());

        List<TransferenciaCaixa> lSaida_central = db.listaTransferenciaDinheiroSaidaCaixaCentral(fc.getId(), caixa.getId());
        for (int i = 0; i < lEntrada.size(); i++) {
            transferencia_entrada = Moeda.soma(transferencia_entrada, lEntrada.get(i).getValor());
        }

        for (int i = 0; i < lSaida.size(); i++) {
            transferencia_saida = Moeda.soma(transferencia_saida, lSaida.get(i).getValor());
        }

        for (int i = 0; i < lSaida_central.size(); i++) {
            transferencia_saida_central = Moeda.soma(transferencia_saida_central, lSaida_central.get(i).getValor());
        }

        for (int i = 0; i < lista_fp_entrada.size(); i++) {
            switch (lista_fp_entrada.get(i).getTipoPagamento().getId()) {
                case 2:
                    boleto = Moeda.soma(boleto, lista_fp_entrada.get(i).getValor());
                    break;
                case 3:
                    dinheiro_baixa = Moeda.soma(dinheiro_baixa, lista_fp_entrada.get(i).getValor());
                    break;
                case 4:
                    cheque = Moeda.soma(cheque, lista_fp_entrada.get(i).getValor());
                    lista_cheque.add(new DataObject(lista_fp_entrada.get(i).getChequeRec(), Moeda.converteR$Double(lista_fp_entrada.get(i).getValor())));
                    break;
                case 5:
                    cheque_pre = Moeda.soma(cheque_pre, lista_fp_entrada.get(i).getValor());
                    lista_cheque.add(new DataObject(lista_fp_entrada.get(i).getChequeRec(), Moeda.converteR$Double(lista_fp_entrada.get(i).getValor())));
                    break;
                case 6:
                    cartao_cre = Moeda.soma(cartao_cre, lista_fp_entrada.get(i).getValor());
                    break;
                case 7:
                    cartao_deb = Moeda.soma(cartao_deb, lista_fp_entrada.get(i).getValor());
                    break;
                case 8:
                    deposito_bancario = Moeda.soma(deposito_bancario, lista_fp_entrada.get(i).getValor());
                    break;
                case 9:
                    doc_bancario = Moeda.soma(doc_bancario, lista_fp_entrada.get(i).getValor());
                    break;
                case 10:
                    transferencia_bancaria = Moeda.soma(transferencia_bancaria, lista_fp_entrada.get(i).getValor());
                    break;
                case 11:
                    ticket = Moeda.soma(ticket, lista_fp_entrada.get(i).getValor());
                    break;
                case 13:
                    debito = Moeda.soma(debito, lista_fp_entrada.get(i).getValor());
                    break;
            }
        }

        for (int i = 0; i < lista_fp_saida.size(); i++) {
//            dinheiro_pagamento = Moeda.somaValores(dinheiro_pagamento, lista_fp_saida.get(i).getValor());
            switch (lista_fp_saida.get(i).getTipoPagamento().getId()) {
                case 3:
                    dinheiro_pagamento = Moeda.soma(dinheiro_pagamento, lista_fp_saida.get(i).getValor());
                    break;
                default:
                    outros_pagamento = Moeda.soma(outros_pagamento, lista_fp_saida.get(i).getValor());
                    break;
            }
        }

        String status = "VALOR BATIDO";
        double soma = 0;
        if (fc.getValorFechamento() > fc.getValorInformado()) {
            soma = Moeda.subtracao(fc.getValorFechamento(), fc.getValorInformado());
            status = "EM FALTA R$ " + Moeda.converteR$Double(soma);
        } else if (fc.getValorFechamento() < fc.getValorInformado()) {
            soma = Moeda.subtracao(fc.getValorInformado(), fc.getValorFechamento());
            status = "EM SOBRA R$ " + Moeda.converteR$Double(soma);
        }

        List<Vector> lista_s = db.pesquisaSaldoAtualRelatorio(caixa.getId(), fc.getId());
        if (!lista_s.isEmpty()) {
            saldo_atual = Moeda.converteUS$(Moeda.converteR$(lista_s.get(0).get(1).toString()));
        }

        List<Vector> lista_u = db.pesquisaUsuarioFechamento(fc.getId());
        String usuarios = "";
        if (!lista_u.isEmpty()) {
            for (int i = 0; i < lista_u.size(); i++) {
                if (usuarios.length() > 0 && i != lista_u.size()) {
                    usuarios += " / ";
                }
                usuarios += lista_u.get(i).get(0).toString();
            }
        }

        double total_dinheiro = dinheiro_baixa;
        double valor_caixa = Moeda.subtracao(Moeda.soma(Moeda.soma(Moeda.soma(Moeda.soma(dinheiro_baixa, cheque), cheque_pre), ticket), saldo_atual), dinheiro_pagamento);

        JasperReport j_fechamento_caixa = Jasper.load("FECHAMENTO_CAIXA.jasper");
        JasperReport j_estorno_caixa = Jasper.load("ESTORNO_CAIXA.jasper");

        Jasper jasper = new Jasper();
        jasper.start();

        if (!lista_cheque.isEmpty()) {
            for (int i = 0; i < lista_cheque.size(); i++) {
                ChequeRec cr = (ChequeRec) lista_cheque.get(i).getArgumento0();
                lista.add(new ParametroFechamentoCaixa(
                        fc.getData() + " - " + fc.getHora(),
                        caixa.getFilial().getFilial().getPessoa().getNome(),
                        caixa.getCaixa() == 0 ? "" : Integer.toString(caixa.getCaixa()),
                        usuarios,
                        Moeda.converteR$Double(fc.getValorFechamento()), // valor_fechamento
                        Moeda.converteR$Double(fc.getValorInformado()),
                        Moeda.converteR$Double(saldo_atual),
                        Moeda.converteR$Double(total_dinheiro),
                        Moeda.converteR$Double(cheque),
                        Moeda.converteR$Double(cheque_pre),
                        Moeda.converteR$Double(cartao_cre),
                        Moeda.converteR$Double(cartao_deb),
                        Moeda.converteR$Double(transferencia_entrada),
                        Moeda.converteR$Double(transferencia_saida),
                        Moeda.converteR$Double(dinheiro_pagamento),
                        Moeda.converteR$Double(outros_pagamento),
                        status,
                        cr.getAgencia() + " - " + cr.getConta() + " " + cr.getBanco().getNumero(),
                        cr.getCheque() + " - " + cr.getVencimento() + " | R$ " + lista_cheque.get(i).getArgumento1(),
                        caixa.getDescricao(),
                        cfb.getConfiguracaoFinanceiro().isAlterarValorFechamento(),
                        Moeda.converteR$Double(valor_caixa), // valor_caixa
                        Moeda.converteR$Double(transferencia_saida_central), // valor_transferido
                        Moeda.converteR$Double(caixa.getFundoFixo()), // saldo_atual
                        Moeda.converteR$Double(deposito_bancario),
                        Moeda.converteR$Double(doc_bancario),
                        Moeda.converteR$Double(transferencia_bancaria),
                        Moeda.converteR$Double(ticket),
                        Moeda.converteR$Double(debito),
                        Moeda.converteR$Double(boleto),
                        (!result_list_estorno.isEmpty()) ? "* Existe(m) estorno(s) para este caixa *" : ""
                ));
            }
        } else {
            lista.add(new ParametroFechamentoCaixa(
                    fc.getData() + " - " + fc.getHora(),
                    caixa.getFilial().getFilial().getPessoa().getNome(),
                    caixa.getCaixa() == 0 ? "" : Integer.toString(caixa.getCaixa()),
                    usuarios,
                    Moeda.converteR$Double(fc.getValorFechamento()),
                    Moeda.converteR$Double(fc.getValorInformado()),
                    Moeda.converteR$Double(saldo_atual),
                    Moeda.converteR$Double(total_dinheiro),
                    Moeda.converteR$Double(cheque),
                    Moeda.converteR$Double(cheque_pre),
                    Moeda.converteR$Double(cartao_cre),
                    Moeda.converteR$Double(cartao_deb),
                    Moeda.converteR$Double(transferencia_entrada),
                    Moeda.converteR$Double(transferencia_saida),
                    Moeda.converteR$Double(dinheiro_pagamento),
                    Moeda.converteR$Double(outros_pagamento),
                    status,
                    null,
                    null,
                    caixa.getDescricao(),
                    cfb.getConfiguracaoFinanceiro().isAlterarValorFechamento(),
                    Moeda.converteR$Double(valor_caixa), // valor_caixa
                    Moeda.converteR$Double(transferencia_saida_central), // valor_transferido
                    Moeda.converteR$Double(caixa.getFundoFixo()), // saldo_atual
                    Moeda.converteR$Double(deposito_bancario),
                    Moeda.converteR$Double(doc_bancario),
                    Moeda.converteR$Double(transferencia_bancaria),
                    Moeda.converteR$Double(ticket),
                    Moeda.converteR$Double(debito),
                    Moeda.converteR$Double(boleto),
                    (!result_list_estorno.isEmpty()) ? "* Existe(m) estorno(s) para este caixa *" : ""
            ));
        }

        jasper.add(j_fechamento_caixa, lista);

        if (!result_list_estorno.isEmpty()) {
            List<ParametroEstornoCaixa> li = new ArrayList();

            for (Object ob : result_list_estorno) {
                List linha = (List) ob;
                li.add(
                        new ParametroEstornoCaixa(
                                linha.get(0),
                                linha.get(1),
                                linha.get(2),
                                linha.get(3),
                                linha.get(4),
                                linha.get(5),
                                linha.get(6),
                                linha.get(7)
                        )
                );
            }

            jasper.add(j_estorno_caixa, li);
        }

        if (!lista_fp_saida.isEmpty()) {
            JasperReport j_fechamento_caixa_s = Jasper.load("FECHAMENTO_CAIXA_SAIDA.jasper");
            List<ParamFechamentoCaixaSaida> li = new ArrayList();

            for (FormaPagamento fps : lista_fp_saida) {
                Pessoa p = fps.getResponsavel();
                li.add(
                        new ParamFechamentoCaixaSaida(p.getDocumento(), p.getNome(), fps.getDocumento(), fps.getValor())
                );

            }

            jasper.add(j_fechamento_caixa_s, li);
        }

        jasper.finish("Fechamento Caixa");
    }

    public class ParamFechamentoCaixaSaida {

        private Object documento;
        private Object nome;
        private Object numero;
        private Object valor;

        public ParamFechamentoCaixaSaida(Object documento, Object nome, Object numero, Object valor) {
            this.documento = documento;
            this.nome = nome;
            this.numero = numero;
            this.valor = valor;
        }

        public Object getDocumento() {
            return documento;
        }

        public void setDocumento(Object documento) {
            this.documento = documento;
        }

        public Object getNome() {
            return nome;
        }

        public void setNome(Object nome) {
            this.nome = nome;
        }

        public Object getNumero() {
            return numero;
        }

        public void setNumero(Object numero) {
            this.numero = numero;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

    }
}
