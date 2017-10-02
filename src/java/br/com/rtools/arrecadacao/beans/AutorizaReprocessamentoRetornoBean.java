/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.dao.RetornoDao;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Retorno;
import br.com.rtools.financeiro.RetornoReprocessa;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
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
public class AutorizaReprocessamentoRetornoBean implements Serializable {

    private List<SelectItem> listaContaCobranca = new ArrayList();
    private Integer indexListaContaCobranca = 0;

    private List<Retorno> listaRetorno = new ArrayList();
    private List<Retorno> listaRetornoSelecionado = new ArrayList();

    private List<RetornoReprocessa> listaRetornoReprocessa = new ArrayList();

    public AutorizaReprocessamentoRetornoBean() {

        loadListaContaCobranca();
        loadListaRetornoReprocessa();

    }

    public final void loadListaContaCobranca() {
        listaContaCobranca.clear();
        indexListaContaCobranca = 0;

        ServicoContaCobrancaDao servDB = new ServicoContaCobrancaDao();
        List<ContaCobranca> result = servDB.listaContaCobrancaAtivoArrecadacao();

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getLayout().getId() == 2) {
                listaContaCobranca.add(
                        new SelectItem(
                                i,
                                result.get(i).getApelido() + " - " + result.get(i).getSicasSindical() + " - " + result.get(i).getContaBanco().getBanco().getBanco(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            } else {
                listaContaCobranca.add(
                        new SelectItem(
                                i,
                                result.get(i).getApelido() + " - " + result.get(i).getCodCedente() + " - " + result.get(i).getContaBanco().getBanco().getBanco(),
                                Integer.toString(result.get(i).getId())
                        )
                );
            }

        }

        loadListaRetorno();
    }

    public final void loadListaRetorno() {
        listaRetorno.clear();
        listaRetornoSelecionado.clear();

        listaRetorno = new RetornoDao().listaRetorno(Integer.valueOf(listaContaCobranca.get(indexListaContaCobranca).getDescription()));

        loadListaRetornoReprocessa();
    }

    public void autorizar() {

        if (!listaRetornoSelecionado.isEmpty()) {

            Dao dao = new Dao();

            dao.openTransaction();

            for (Retorno r : listaRetornoSelecionado) {
                
                if (!new RetornoDao().listaRetornoReprocessa(r.getContaCobranca().getId(), r.getSequencial()).isEmpty()) {
                    GenericaMensagem.error("ATENÇÃO", "AUTORIZAÇÃO PARA " + r.getArquivo() + " - " + r.getSequencial() + " JÁ EXISTE");
                    dao.rollback();
                    return;
                }

                RetornoReprocessa rr = new RetornoReprocessa(
                        -1,
                        (ContaCobranca) dao.find(new ContaCobranca(), Integer.valueOf(listaContaCobranca.get(indexListaContaCobranca).getDescription())),
                        r.getSequencial()
                );

                if (!dao.save(rr)) {
                    dao.rollback();
                    GenericaMensagem.error("ATENÇÃO", "ERRO AO SALVAR AUTORIZAÇÃO");
                    return;
                }

            }

            dao.commit();

            GenericaMensagem.info("SUCESSO", "AUTORIZAÇÃO SALVA");
            listaRetornoSelecionado.clear();

            loadListaRetornoReprocessa();

            return;
        }

        GenericaMensagem.warn("ATENÇÃO", "NENHUM RETORNO SELECIONADO");

    }

    public void excluir(RetornoReprocessa rr) {
        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(rr)) {
            dao.rollback();
            GenericaMensagem.warn("ATENÇÃO", "ERRO AO EXCLUIR AUTORIZAÇÃO");
            return;
        }

        dao.commit();
        GenericaMensagem.info("SUCESSO", "AUTORIZAÇÃO EXCLUÍDA");
        listaRetornoSelecionado.clear();

        loadListaRetornoReprocessa();
    }

    public final void loadListaRetornoReprocessa() {
        listaRetornoReprocessa.clear();

        listaRetornoReprocessa = new RetornoDao().listaRetornoReprocessa(Integer.valueOf(listaContaCobranca.get(indexListaContaCobranca).getDescription()));
    }

    public List<SelectItem> getListaContaCobranca() {
        return listaContaCobranca;
    }

    public void setListaContaCobranca(List<SelectItem> listaContaCobranca) {
        this.listaContaCobranca = listaContaCobranca;
    }

    public Integer getIndexListaContaCobranca() {
        return indexListaContaCobranca;
    }

    public void setIndexListaContaCobranca(Integer indexListaContaCobranca) {
        this.indexListaContaCobranca = indexListaContaCobranca;
    }

    public List<Retorno> getListaRetorno() {
        return listaRetorno;
    }

    public void setListaRetorno(List<Retorno> listaRetorno) {
        this.listaRetorno = listaRetorno;
    }

    public List<Retorno> getListaRetornoSelecionado() {
        return listaRetornoSelecionado;
    }

    public void setListaRetornoSelecionado(List<Retorno> listaRetornoSelecionado) {
        this.listaRetornoSelecionado = listaRetornoSelecionado;
    }

    public List<RetornoReprocessa> getListaRetornoReprocessa() {
        return listaRetornoReprocessa;
    }

    public void setListaRetornoReprocessa(List<RetornoReprocessa> listaRetornoReprocessa) {
        this.listaRetornoReprocessa = listaRetornoReprocessa;
    }

}
