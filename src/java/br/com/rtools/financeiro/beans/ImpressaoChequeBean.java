/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.ChequePag;
import br.com.rtools.financeiro.ImpressoraCheque;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.dao.ImpressaoChequeDao;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.MacFilial;
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
public class ImpressaoChequeBean implements Serializable {

    private List<SelectItem> listaConta = new ArrayList();
    private Integer indexConta = 0;

    private List<SelectItem> listaImpressora = new ArrayList();
    private Integer indexImpressora = 0;

    private String statusCheque = "emitir";

    private List<ObjectListaCheque> listaCheques = new ArrayList();

    private ObjectImprimirCheque imprimirCheque = new ObjectImprimirCheque();

    public ImpressaoChequeBean() {
        loadListaConta();
        loadListaCheques();
        loadListaImpressora();
    }

    public void selecionarLinha(ObjectListaCheque linha_cheque) {
        imprimirCheque = new ObjectImprimirCheque(linha_cheque, null);
    }

    public void validaImpressao(ObjectListaCheque linha_cheque) {
        imprimirCheque = new ObjectImprimirCheque();

        ImpressaoChequeDao idao = new ImpressaoChequeDao();

        if (listaImpressora.get(0).getDescription() == null) {
            GenericaMensagem.error("Atenção", "Nenhuma Impressora Cadastrada!");
            return;
        }

        Dao dao = new Dao();

        ImpressoraCheque ic = (ImpressoraCheque) dao.find(new ImpressoraCheque(), Integer.valueOf(listaImpressora.get(indexImpressora).getDescription()));
        ic.setAtivo(true);
        // SETA FALSO PARA TESTAR SE O PROJETO DESKTOP DA IMPRESSORA ESTA ATUALIZANDO PARA TRUE
        // SENDO ASSIM ESTA ATIVA
        if (!dao.update(ic, true)) {
            GenericaMensagem.error("Atenção", "Erro ao atualizar status da impressora!");
            return;
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.getMessage();
        }

        ic = (ImpressoraCheque) dao.rebind(ic);
        if (!ic.getAtivo()) {
            GenericaMensagem.error("Atenção", "Impressora inativa, verifique!");
            return;
        }

        imprimirCheque = new ObjectImprimirCheque(linha_cheque, ic);
    }

    public void imprimir() {
        ImpressoraCheque ic = imprimirCheque.getImpressoraCheque();

        ic.setBanco(imprimirCheque.getLinhaCheque().getCodigo_banco());
        ic.setCidade(imprimirCheque.getLinhaCheque().getCidade());
        ic.setData(imprimirCheque.getLinhaCheque().getData());
        ic.setFavorecido(imprimirCheque.getLinhaCheque().getFavorecido());
        ic.setMensagem("");
        ic.setValor(imprimirCheque.getLinhaCheque().getValorString());
        ic.setMensagemErro("imprimindo");

        Dao dao = new Dao();

        dao.openTransaction();
        if (!dao.update(ic)) {
            GenericaMensagem.error("Atenção", "Erro ao atualizar dados para Impressora!");
            dao.rollback();
            return;
        }
        dao.commit();

        while (ic.getMensagemErro().equals("imprimindo")) {
            ic = (ImpressoraCheque) dao.rebind(ic);
        }

        if (ic.getMensagemErro().isEmpty()) {
            ChequePag cp = (ChequePag) dao.find(new ChequePag(), imprimirCheque.getLinhaCheque().getCheque_id());
            cp = (ChequePag) dao.rebind(cp);

            cp.setDtImpressao(DataHoje.dataHoje());
            cp.setOperadorImpressao(Usuario.getUsuario());

            dao.openTransaction();
            if (!dao.update(cp)) {
                GenericaMensagem.error("Atenção", "Erro ao atualizar Cheque!");
                dao.rollback();
                return;
            }
            dao.commit();

            loadListaCheques();

            GenericaMensagem.info("Sucesso", "Dados enviados para impressora!");
        } else {
            GenericaMensagem.error("Erro ao imprimir cheque", ic.getMensagemErro());
        }

    }

    public void cancelar() {
        Dao dao = new Dao();

        dao.openTransaction();

        ChequePag cp = (ChequePag) dao.find(new ChequePag(), imprimirCheque.getLinhaCheque().getCheque_id());

        cp.setDtCancelamento(DataHoje.dataHoje());
        cp.setOperadorCancelamento(Usuario.getUsuario());

        if (!dao.update(cp)) {
            GenericaMensagem.error("Atenção", "Erro ao atualizar Cheque!");
            dao.rollback();
            return;
        }

        dao.commit();

        loadListaCheques();

        GenericaMensagem.info("Sucesso", "Cheque Cancelado!");
    }

    public void restaurar() {
        Dao dao = new Dao();

        dao.openTransaction();

        ChequePag cp = (ChequePag) dao.find(new ChequePag(), imprimirCheque.getLinhaCheque().getCheque_id());

        cp.setDtCancelamento(null);
        cp.setOperadorCancelamento(null);

        if (!dao.update(cp)) {
            GenericaMensagem.error("Atenção", "Erro ao atualizar Cheque!");
            dao.rollback();
            return;
        }

        dao.commit();

        loadListaCheques();

        GenericaMensagem.info("Sucesso", "Cheque Restaurado!");
    }

    public final void loadListaCheques() {
        listaCheques.clear();

        PessoaEnderecoDao pedao = new PessoaEnderecoDao();
        PessoaEndereco sindicato_endereco = pedao.pesquisaEndPorPessoaTipo(1, 5);

        if (!listaConta.isEmpty()) {
            List<Object> result = new ImpressaoChequeDao().listaCheques(Integer.valueOf(listaConta.get(indexConta).getDescription()), statusCheque);
            Dao dao = new Dao();
            for (Object lista : result) {
                List linha = (List) lista;

                listaCheques.add(
                        new ObjectListaCheque(
                                "000".substring(0, 3 - linha.get(0).toString().length()) + linha.get(0).toString(),
                                linha.get(1).toString(),
                                Moeda.converteUS$(Moeda.converteR$Double((Double) linha.get(2))),
                                linha.get(3).toString(),
                                DataHoje.converteData((Date) linha.get(4)),
                                (Integer) linha.get(5),
                                DataHoje.converteData((Date) linha.get(6)),
                                DataHoje.converteData((Date) linha.get(7)),
                                DataHoje.converteData((Date) linha.get(8)),
                                linha.get(9).toString(),
                                sindicato_endereco.getEndereco().getCidade().getCidadeToString(),
                                DataHoje.converteData((Date) linha.get(10)),
                                (ChequePag) dao.find(new ChequePag(), (Integer) linha.get(5))
                        )
                );
            }
        }
    }

    public final void loadListaConta() {
        listaConta.clear();
        indexConta = 0;

        List<Plano5> result = new ImpressaoChequeDao().listaConta();

        for (int i = 0; i < result.size(); i++) {
            listaConta.add(
                    new SelectItem(
                            i,
                            result.get(i).getConta(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadListaImpressora() {
        listaImpressora.clear();
        indexImpressora = 0;

        List<ImpressoraCheque> result = new ImpressaoChequeDao().listaImpressora();

        if (!result.isEmpty()) {
            for (int i = 0; i < result.size(); i++) {
                String dispostivo = "";
                if(result.get(i).getMacFilial() != null) {
                    dispostivo = " - " + result.get(i).getMacFilial().getDescricao();                    
                }
                listaImpressora.add(
                        new SelectItem(
                                i,
                                result.get(i).getImpressora() + " - " + result.get(i).getApelido() + dispostivo,
                                Integer.toString(result.get(i).getId())
                        )
                );
                if(MacFilial.getAcessoFilial().getMac().equals(result.get(i).getMac())) {
                    indexImpressora = i;
                }
            }
        } else {
            listaImpressora.add(new SelectItem(0, "NENHUMA IMPRESSORA ENCONTRADA", null));
        }
    }

    public List<SelectItem> getListaConta() {
        return listaConta;
    }

    public void setListaConta(List<SelectItem> listaConta) {
        this.listaConta = listaConta;
    }

    public Integer getIndexConta() {
        return indexConta;
    }

    public void setIndexConta(Integer indexConta) {
        this.indexConta = indexConta;
    }

    public String getStatusCheque() {
        return statusCheque;
    }

    public void setStatusCheque(String statusCheque) {
        this.statusCheque = statusCheque;
    }

    public List<ObjectListaCheque> getListaCheques() {
        return listaCheques;
    }

    public void setListaCheques(List<ObjectListaCheque> listaCheques) {
        this.listaCheques = listaCheques;
    }

    public List<SelectItem> getListaImpressora() {
        return listaImpressora;
    }

    public void setListaImpressora(List<SelectItem> listaImpressora) {
        this.listaImpressora = listaImpressora;
    }

    public Integer getIndexImpressora() {
        return indexImpressora;
    }

    public void setIndexImpressora(Integer indexImpressora) {
        this.indexImpressora = indexImpressora;
    }

    public ObjectImprimirCheque getImprimirCheque() {
        return imprimirCheque;
    }

    public void setImprimirCheque(ObjectImprimirCheque imprimirCheque) {
        this.imprimirCheque = imprimirCheque;
    }

    public class ObjectImprimirCheque {

        private ObjectListaCheque linhaCheque;
        private ImpressoraCheque impressoraCheque;

        public ObjectImprimirCheque() {
            this.linhaCheque = null;
            this.impressoraCheque = new ImpressoraCheque();
        }

        public ObjectImprimirCheque(ObjectListaCheque linhaCheque, ImpressoraCheque impressoraCheque) {
            this.linhaCheque = linhaCheque;
            this.impressoraCheque = impressoraCheque;
        }

        public ObjectListaCheque getLinhaCheque() {
            return linhaCheque;
        }

        public void setLinhaCheque(ObjectListaCheque linhaCheque) {
            this.linhaCheque = linhaCheque;
        }

        public ImpressoraCheque getImpressoraCheque() {
            return impressoraCheque;
        }

        public void setImpressoraCheque(ImpressoraCheque impressoraCheque) {
            this.impressoraCheque = impressoraCheque;
        }

    }

    public class ObjectListaCheque {

        private String codigo_banco;
        private String banco;
        private Float valor;
        private String favorecido;
        private String data;
        private Integer cheque_id;
        private String emissao;
        private String vencimento;
        private String cancelamento;
        private String cheque_numero;
        private String cidade;
        private String impressao;
        private ChequePag chequePag;

        public ObjectListaCheque(String codigo_banco, String banco, Float valor, String favorecido, String data, Integer cheque_id, String emissao, String vencimento, String cancelamento, String cheque_numero, String cidade, String impressao, ChequePag chequePag) {
            this.codigo_banco = codigo_banco;
            this.banco = banco;
            this.valor = valor;
            this.favorecido = favorecido;
            this.data = data;
            this.cheque_id = cheque_id;
            this.emissao = emissao;
            this.vencimento = vencimento;
            this.cancelamento = cancelamento;
            this.cheque_numero = cheque_numero;
            this.cidade = cidade;
            this.impressao = impressao;
            this.chequePag = chequePag;
        }

        public String getCodigo_banco() {
            return codigo_banco;
        }

        public void setCodigo_banco(String codigo_banco) {
            this.codigo_banco = codigo_banco;
        }

        public String getBanco() {
            return banco;
        }

        public void setBanco(String banco) {
            this.banco = banco;
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

        public String getFavorecido() {
            return favorecido;
        }

        public void setFavorecido(String favorecido) {
            this.favorecido = favorecido;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Integer getCheque_id() {
            return cheque_id;
        }

        public void setCheque_id(Integer cheque_id) {
            this.cheque_id = cheque_id;
        }

        public String getEmissao() {
            return emissao;
        }

        public void setEmissao(String emissao) {
            this.emissao = emissao;
        }

        public String getVencimento() {
            return vencimento;
        }

        public void setVencimento(String vencimento) {
            this.vencimento = vencimento;
        }

        public String getCancelamento() {
            return cancelamento;
        }

        public void setCancelamento(String cancelamento) {
            this.cancelamento = cancelamento;
        }

        public String getCheque_numero() {
            return cheque_numero;
        }

        public void setCheque_numero(String cheque_numero) {
            this.cheque_numero = cheque_numero;
        }

        public String getCidade() {
            return cidade;
        }

        public void setCidade(String cidade) {
            this.cidade = cidade;
        }

        public String getImpressao() {
            return impressao;
        }

        public void setImpressao(String impressao) {
            this.impressao = impressao;
        }

        public ChequePag getChequePag() {
            return chequePag;
        }

        public void setChequePag(ChequePag chequePag) {
            this.chequePag = chequePag;
        }
    }
}
