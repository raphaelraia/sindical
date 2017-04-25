package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FechamentoCaixa;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.TransferenciaCaixa;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.impressao.beans.ImprimirFechamentoCaixa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class CaixaFechadoBean implements Serializable {

    private List<DataObject> listaFechamento = new ArrayList();
    private List<SelectItem> listaCaixa = new ArrayList();
    private int idCaixa = 0;
    private FechamentoCaixa fechamentoCaixa = new FechamentoCaixa();
    private String valorTransferencia = "0,00";

    private final ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();

    @PostConstruct
    public void init() {
        cfb.init();
    }

    @PreDestroy
    public void destroy() {

    }

    public void imprimir(DataObject linha) {
        ImprimirFechamentoCaixa ifc = new ImprimirFechamentoCaixa();

        // id_fechamento, id_caixa
        ifc.imprimir((Integer) ((Vector) linha.getArgumento0()).get(1), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));
    }

    public void reabrir() {
        FinanceiroDao db = new FinanceiroDao();
        Dao dao = new Dao();

        List<Baixa> lista_baixa = db.listaBaixa(fechamentoCaixa.getId());

        dao.openTransaction();
        for (int i = 0; i < lista_baixa.size(); i++) {
            Baixa ba = ((Baixa) dao.find(new Baixa(), lista_baixa.get(i).getId()));

            ba.setFechamentoCaixa(null);
            if (!dao.update(ba)) {
                GenericaMensagem.warn("Erro", "Não foi possivel alterar a Baixa!");
                dao.rollback();
                return;
            }
        }

        List<TransferenciaCaixa> lista_transferencia = db.listaTransferencia(fechamentoCaixa.getId());
        for (int i = 0; i < lista_transferencia.size(); i++) {
            TransferenciaCaixa tc = ((TransferenciaCaixa) dao.find(new TransferenciaCaixa(), lista_transferencia.get(i).getId()));

            if (tc.getFechamentoEntrada() != null && tc.getFechamentoEntrada().getId() == fechamentoCaixa.getId()) {
                tc.setFechamentoEntrada(null);
            } else if (tc.getFechamentoSaida() != null && tc.getFechamentoSaida().getId() == fechamentoCaixa.getId()) {
                tc.setFechamentoSaida(null);
            }

            if (!dao.update(tc)) {
                GenericaMensagem.warn("Erro", "Não foi possivel alterar a Transferência Caixa!");
                dao.rollback();
                return;
            }
        }

        if (!dao.delete(dao.find(new FechamentoCaixa(), fechamentoCaixa.getId()))) {
            GenericaMensagem.warn("Erro", "Não foi possivel excluir a Fechamento Caixa!");
            dao.rollback();
            return;
        }

        GenericaMensagem.info("Sucesso", "Reabertura de Caixa concluído!");
        dao.commit();
        listaFechamento.clear();
        fechamentoCaixa = new FechamentoCaixa();
    }

    public void reabrir(DataObject dob) {
        fechamentoCaixa = (FechamentoCaixa) (new Dao()).find(new FechamentoCaixa(), (Integer) ((Vector) dob.getArgumento0()).get(1));
    }

    public void transferir() {
        //Caixa caixa = (Caixa)(new ().pesquisaCodigo(Integer.valueOf(listaCaixa.get(idCaixa).getDescription()) ,"Caixa"));

        transferirCaixaGenerico(fechamentoCaixa.getId(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()), valorTransferencia);

        fechamentoCaixa = new FechamentoCaixa();
        listaFechamento.clear();
    }

    public void transferir(DataObject dob) {
        fechamentoCaixa = (FechamentoCaixa) new Dao().find(new FechamentoCaixa(), (Integer) ((Vector) dob.getArgumento0()).get(1));

        Caixa caixa = (Caixa) new Dao().find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));
        valorTransferencia = somaValorTransferencia(fechamentoCaixa, caixa);
    }

    public void transferirCaixaGenerico(Integer id_fechamento, Integer id_caixa, String valort) {
        FinanceiroDao db = new FinanceiroDao();

        Caixa caixa = (Caixa) new Dao().find(new Caixa(), id_caixa);
        FechamentoCaixa fc = (FechamentoCaixa) new Dao().find(new FechamentoCaixa(), id_fechamento);

        List<TransferenciaCaixa> lista_tc = db.listaTransferenciaDinheiro(fc.getId(), caixa.getId());
        List<FormaPagamento> lista_fp_entrada = db.listaTransferenciaFormaPagamento(fc.getId(), caixa.getId(), "E");
        List<FormaPagamento> lista_fp_saida = db.listaTransferenciaFormaPagamento(fc.getId(), caixa.getId(), "S");

        //float dinheiro_transferencia = 0, outros = 0;
        //float dinheiro_pagamento = 0, outros_pagamento = 0;
//        
//        float transferencia_entrada = 0, transferencia_saida = 0, dinheiro_baixa = 0, cheque = 0, cheque_pre = 0, cartao_cre = 0, cartao_deb = 0, saldo_atual = 0;
//        float deposito_bancario = 0, doc_bancario = 0, transferencia_bancaria = 0, ticket = 0, debito = 0, boleto = 0;
        float valor_caixa = 0, outros = 0;
        float valor_saida = 0;

//        for (int i = 0; i < lista_tc.size(); i++){
//            dinheiro_transferencia = Moeda.somaValores(dinheiro_transferencia, lista_tc.get(i).getValor());
//        }
        // TOTAL DINHEIRO
//        for (int i = 0; i < lista_fp_entrada.size(); i++){
//            if (lista_fp_entrada.get(i).getTipoPagamento().getId() == 3){
//                dinheiro_baixa = Moeda.somaValores(dinheiro_baixa, lista_fp_entrada.get(i).getValor());
//            }
//        }
        // TOTAL SAIDA
        for (int i = 0; i < lista_fp_saida.size(); i++) {
            // EM 24/04/2017 ROGÉRIO, MOVIMENTO DE SAIDA SOMAVA TODOS OS TIPOS DE PAGAMENTO
            // PARA ABATER DAS ENTRADAS CUJO SALDO TRANSFERIDO PARA O CAIXA CENTRAL
            // MODIFICADO NESTA DATA DE FORMA QUE SOMASSE SOMENTE OS PAGAMENTOS EM DINHEIRO PARA ABATER DOS RECEBIMENTOS EM DINHEIRO
            // POIS PAGAMENTOS EM CHEQUE, TRANSAÇÕES BANCÁRIAS ETC ... NÃO PODEM SER ABATIDO DOS RECEBIMENTOS EM DINHEIRO
            // valor_saida = Moeda.somaValores(valor_saida, lista_fp_saida.get(i).getValor());
            
            if (lista_fp_saida.get(i).getTipoPagamento().getId() == 3) {
                valor_saida = Moeda.somaValores(valor_saida, lista_fp_saida.get(i).getValor());
            }
//            if (lista_fp_saida.get(i).getTipoPagamento().getId() == 3){
//                dinheiro_pagamento = Moeda.somaValores(dinheiro_pagamento, lista_fp_saida.get(i).getValor());
//            }else{
//                outros_pagamento = Moeda.somaValores(outros_pagamento, lista_fp_saida.get(i).getValor());
//            }
        }

        // VALOR DO CAIXA
        for (int i = 0; i < lista_fp_entrada.size(); i++) {
            if (lista_fp_entrada.get(i).getPlano5() != null && lista_fp_entrada.get(i).getPlano5().getId() == 1) {
                valor_caixa = Moeda.somaValores(valor_caixa, lista_fp_entrada.get(i).getValor());
            } else {
                outros = Moeda.somaValores(outros, lista_fp_entrada.get(i).getValor());
            }
        }
        List<Vector> lista = db.pesquisaSaldoAtualRelatorio(caixa.getId(), fc.getId());
        float valor_saldo_atual = 0;
        if (!lista.isEmpty()) {
            valor_saldo_atual = Moeda.converteUS$(Moeda.converteR$(lista.get(0).get(1).toString()));
        }
        valor_caixa = Moeda.somaValores(valor_caixa, valor_saldo_atual);
        valor_caixa = Moeda.subtracaoValores(valor_caixa, valor_saida);

        // ROGÉRIO QUER QUE TRANSFERE ZERO CASO O VALOR SEJA NEGATIVO
        if (valor_caixa < 0) {
            valor_caixa = 0;
        }

        //float total_dinheiro = Moeda.somaValores(Moeda.somaValores(dinheiro_transferencia, dinheiro_baixa), valor_saldo_atual);
        //float soma = Moeda.somaValores(total_dinheiro, outros);
//        float soma_pagamento = Moeda.somaValores(dinheiro_pagamento, outros_pagamento);
//        float valor_minimo = Moeda.subtracaoValores(outros, soma_pagamento);
        if (valor_caixa != Moeda.converteUS$(valort)) {
            if (Moeda.converteUS$(valort) > valor_caixa) {
                GenericaMensagem.warn("Erro", "Valor da Transferência deve ser no MÁXIMO R$ " + Moeda.converteR$Float(valor_caixa));
                return;
            }

            if (Moeda.converteUS$(valort) < outros) {
                GenericaMensagem.warn("Erro", "Valor da Transferência deve ser no MÍNIMO R$ " + Moeda.converteR$Float(outros));
                return;
            }

            float saldo_atual = 0;
            if (Moeda.converteUS$(valort) >= outros) {
                saldo_atual = Moeda.subtracaoValores(valor_caixa, Moeda.converteUS$(valort));
            }
            fc.setSaldoAtual(saldo_atual);
        }

//        if (Moeda.converteUS$(valort) != soma){
//            if (Moeda.converteUS$(valort) > fc.getValorFechamento()){
//                GenericaMensagem.warn("Erro", "Valor da Transferência deve ser no MÁXIMO R$ " + Moeda.converteR$Float(fc.getValorFechamento()));
//                return;
//            }
//
//            if (Moeda.converteUS$(valort) < outros){
//                GenericaMensagem.warn("Erro", "Valor da Transferência deve ser no MÍNIMO R$ " + Moeda.converteR$Float(outros));
//                return;
//            }else if (Moeda.converteUS$(valort) >= outros){
//                saldo_atual = Moeda.subtracaoValores(fc.getValorFechamento(),Moeda.converteUS$(valort));
//            }
//            fc.setSaldoAtual(saldo_atual);
//        
//        }
        Dao dao = new Dao();
        dao.openTransaction();

        if (!dao.update(fc)) {
            GenericaMensagem.warn("Erro", "Não foi possivel alterar Fechamento Caixa!");
            //sv.desfazerTransacao();
            dao.rollback();
            return;
        }

        // AQUI pesquisaCaixaUm COLOCAR id_filial
        TransferenciaCaixa tc = new TransferenciaCaixa(
                -1,
                caixa,
                Moeda.converteUS$(valort),
                //Moeda.subtracaoValores(total_transferencia, caixa.getFundoFixo()),
                new FinanceiroDao().pesquisaCaixaUm(),
                //DataHoje.dataHoje(),
                fc.getDtData(),
                (FStatus) new Dao().find(new FStatus(), 12),
                null,
                fc,
                (Usuario) GenericaSessao.getObject("sessaoUsuario")
        );

        if (!dao.save(tc)) {
            GenericaMensagem.warn("Erro", "Não foi possivel salvar esta Transferência, verifique se existe CAIXA 01 cadastrado!");
            //sv.desfazerTransacao();
            dao.rollback();
            return;
        }

        //sv.comitarTransacao();
        dao.commit();
        GenericaMensagem.info("Sucesso", "Transferência entre Caixas concluído!");
    }

    public String somaValorTransferencia(FechamentoCaixa fc, Caixa c) {
        FinanceiroDao db = new FinanceiroDao();
        List<FormaPagamento> lista_fp_entrada = db.listaTransferenciaFormaPagamento(fc.getId(), c.getId(), "E");
        List<FormaPagamento> lista_fp_saida = db.listaTransferenciaFormaPagamento(fc.getId(), c.getId(), "S");

        List<TransferenciaCaixa> lista_tc = db.listaTransferenciaDinheiro(fc.getId(), c.getId());

        float dinheiro_transferencia = 0;
        float total_transferencia = 0;
        float total_saida = 0;

        for (int i = 0; i < lista_tc.size(); i++) {
            dinheiro_transferencia = Moeda.somaValores(dinheiro_transferencia, lista_tc.get(i).getValor());
        }

        for (int i = 0; i < lista_fp_entrada.size(); i++) {
            if (lista_fp_entrada.get(i).getTipoPagamento().getId() == 3
                    || lista_fp_entrada.get(i).getTipoPagamento().getId() == 4
                    || lista_fp_entrada.get(i).getTipoPagamento().getId() == 5
                    || lista_fp_entrada.get(i).getTipoPagamento().getId() == 11) {
                total_transferencia = Moeda.somaValores(total_transferencia, lista_fp_entrada.get(i).getValor());
            }
        }

        for (int i = 0; i < lista_fp_saida.size(); i++) {
            total_saida = Moeda.somaValores(total_saida, lista_fp_saida.get(i).getValor());
        }

        total_transferencia = Moeda.somaValores(total_transferencia, dinheiro_transferencia);

        List<Vector> lista = db.pesquisaSaldoAtualRelatorio(c.getId(), fc.getId());
        float valor_saldo_atual;

        total_transferencia = Moeda.subtracaoValores(total_transferencia, total_saida);

        if (!lista.isEmpty()) {
            valor_saldo_atual = Moeda.converteUS$(Moeda.converteR$(lista.get(0).get(1).toString()));
            total_transferencia = Moeda.somaValores(valor_saldo_atual, total_transferencia);
        }

        if (total_transferencia <= c.getFundoFixo()) {
            total_transferencia = 0;
        } else {
            total_transferencia = Moeda.subtracaoValores(total_transferencia, c.getFundoFixo());
        }

        return Moeda.converteR$Float(total_transferencia);
    }

    public List<DataObject> getListaFechamento() {
        if (listaFechamento.isEmpty()) {
            FinanceiroDao db = new FinanceiroDao();
            Caixa caixa = (Caixa) (new Dao().find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription())));

            List<Vector> lista = db.listaFechamentoCaixaTransferencia(caixa.getId());
            for (int i = 0; i < lista.size(); i++) {
                String valor_d = "0,00";
                int status = 0;
                float vl_fechado = Moeda.converteUS$(lista.get(i).get(2).toString()),
                        vl_informado = Moeda.converteUS$(lista.get(i).get(3).toString());

                if (vl_fechado > vl_informado) {
                    // EM FALTA
                    status = 1;
                    valor_d = Moeda.converteR$Float(Moeda.subtracaoValores(vl_fechado, vl_informado));
                } else if (vl_fechado < vl_informado) {
                    // EM SOBRA
                    valor_d = Moeda.converteR$Float(Moeda.subtracaoValores(vl_informado, vl_fechado));
                    status = 2;
                }

                listaFechamento.add(new DataObject(lista.get(i),
                        DataHoje.converteData((Date) lista.get(i).get(4)), // DATA
                        lista.get(i).get(5).toString(), // HORA
                        Moeda.converteR$(lista.get(i).get(2).toString()), // VALOR FECHAMENTO
                        Moeda.converteR$(lista.get(i).get(3).toString()), // VALOR INFORMADO
                        status, // STATUS
                        valor_d,
                        null,
                        null,
                        null
                ));
            }
        }
        return listaFechamento;
    }

    public void setListaFechamento(List<DataObject> listaFechamento) {
        this.listaFechamento = listaFechamento;
    }

    public List<SelectItem> getListaCaixa() {
        if (listaCaixa.isEmpty()) {
            List<Caixa> list = new FinanceiroDao().listaCaixa();
            for (int i = 0; i < list.size(); i++) {
                listaCaixa.add(
                        new SelectItem(
                                i,
                                list.get(i).getCaixa() + " - " + list.get(i).getDescricao(),
                                Integer.toString(list.get(i).getId())
                        )
                );
            }
        }
        return listaCaixa;
    }

    public void setListaCaixa(List<SelectItem> listaCaixa) {
        this.listaCaixa = listaCaixa;
    }

    public int getIdCaixa() {
        return idCaixa;
    }

    public void setIdCaixa(int idCaixa) {
        this.idCaixa = idCaixa;
    }

    public FechamentoCaixa getFechamentoCaixa() {
        return fechamentoCaixa;
    }

    public void setFechamentoCaixa(FechamentoCaixa fechamentoCaixa) {
        this.fechamentoCaixa = fechamentoCaixa;
    }

    public String getValorTransferencia() {
        return Moeda.converteR$(valorTransferencia);
    }

    public void setValorTransferencia(String valorTransferencia) {
        this.valorTransferencia = Moeda.substituiVirgula(valorTransferencia);
    }

}
