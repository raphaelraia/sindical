/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.homologacao.beans;

import br.com.rtools.atendimento.AteOperacao;
import br.com.rtools.homologacao.OperacaoDepartamento;
import br.com.rtools.homologacao.dao.OperacaoDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Departamento;
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
public class OperacaoBean implements Serializable {

    private AteOperacao operacao = new AteOperacao();
    private List<SelectItem> listaOperacao = new ArrayList();
    private Integer indexOperacao = 0;
    private List<SelectItem> listaFilial = new ArrayList();
    private Integer indexFilial = 0;
    private List<SelectItem> listaDepartamento = new ArrayList();
    private Integer indexDepartamento = 0;
    private List<OperacaoDepartamento> listaOperacaoDepartamento = new ArrayList();

    public OperacaoBean() {
        loadListaOperacao();
        loadListaFilial();
        loadListaDepartamento();
        loadListaOperacaoDepartamento();
    }

    public void salvar() {
        Dao dao = new Dao();

        dao.openTransaction();
        if (operacao.getId() == -1) {
            if (!dao.save(operacao)) {
                GenericaMensagem.error("ATENÇÃO", "Erro ao salvar Operação!");
                return;
            }

            GenericaMensagem.info("SUCESSO", "Operação Salva!");
        } else {
            if (!dao.update(operacao)) {
                GenericaMensagem.error("ATENÇÃO", "Erro ao atualizar Operação!");
                return;
            }

            GenericaMensagem.info("SUCESSO", "Operação Atualizada!");
        }

        dao.commit();
        loadListaOperacao();
        loadListaOperacaoDepartamento();
    }

    public void adicionar() {
        Dao dao = new Dao();

        
        OperacaoDepartamento od = new OperacaoDepartamento();
        od.setOperacao(operacao);
        od.setFilial((Filial) dao.find(new Filial(), Integer.valueOf(listaFilial.get(indexFilial).getDescription())));
        od.setDepartamento((Departamento) dao.find(new Departamento(), Integer.valueOf(listaDepartamento.get(indexDepartamento).getDescription())));
        
        if (!new OperacaoDao().listaExisteOperacaoDepartamento(od.getOperacao().getId(), od.getFilial().getId(), od.getDepartamento().getId()).isEmpty()){
            GenericaMensagem.warn("ATENÇÃO", "Operação Departamento já existe!");
            return;
        }
                
        dao.openTransaction();

        if (!dao.save(od)) {
            GenericaMensagem.error("ATENÇÃO", "Erro ao salvar Operação Departamento!");
            return;
        }

        dao.commit();
        GenericaMensagem.info("SUCESSO", "Operação Departamento salva!");
        
        loadListaOperacaoDepartamento();
    }

    public void remover(OperacaoDepartamento od) {
        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(od)) {
            GenericaMensagem.error("ATENÇÃO", "Erro ao excluir Operação Departamento!");
            return;
        }

        dao.commit();
        GenericaMensagem.info("SUCESSO", "Operação Departamento excluída!");

        loadListaOperacaoDepartamento();
    }

    public final void loadListaOperacao() {
        listaOperacao.clear();
        indexOperacao = 0;
        
        List<AteOperacao> result = new OperacaoDao().listaOperacao();

        for (int i = 0; i < result.size(); i++) {
            listaOperacao.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadListaFilial() {
        listaFilial.clear();
        indexFilial = 0;
        
        List<Filial> result = new Dao().list(new Filial());

        for (int i = 0; i < result.size(); i++) {
            listaFilial.add(
                    new SelectItem(
                            i,
                            result.get(i).getFilial().getPessoa().getNome(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadListaDepartamento() {
        listaDepartamento.clear();
        indexDepartamento = 0;
        
        List<Departamento> result = new Dao().list(new Departamento());

        for (int i = 0; i < result.size(); i++) {
            listaDepartamento.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadListaOperacaoDepartamento() {
        operacao = (AteOperacao) new Dao().find(new AteOperacao(), Integer.valueOf(listaOperacao.get(indexOperacao).getDescription()));

        listaOperacaoDepartamento.clear();
        
        listaOperacaoDepartamento = new OperacaoDao().listaOperacaoDepartamento(operacao.getId());
    }

    public AteOperacao getOperacao() {
        return operacao;
    }

    public void setOperacao(AteOperacao operacao) {
        this.operacao = operacao;
    }

    public List<SelectItem> getListaOperacao() {
        return listaOperacao;
    }

    public void setListaOperacao(List<SelectItem> listaOperacao) {
        this.listaOperacao = listaOperacao;
    }

    public Integer getIndexOperacao() {
        return indexOperacao;
    }

    public void setIndexOperacao(Integer indexOperacao) {
        this.indexOperacao = indexOperacao;
    }

    public List<SelectItem> getListaFilial() {
        return listaFilial;
    }

    public void setListaFilial(List<SelectItem> listaFilial) {
        this.listaFilial = listaFilial;
    }

    public Integer getIndexFilial() {
        return indexFilial;
    }

    public void setIndexFilial(Integer indexFilial) {
        this.indexFilial = indexFilial;
    }

    public List<SelectItem> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<SelectItem> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public Integer getIndexDepartamento() {
        return indexDepartamento;
    }

    public void setIndexDepartamento(Integer indexDepartamento) {
        this.indexDepartamento = indexDepartamento;
    }

    public List<OperacaoDepartamento> getListaOperacaoDepartamento() {
        return listaOperacaoDepartamento;
    }

    public void setListaOperacaoDepartamento(List<OperacaoDepartamento> listaOperacaoDepartamento) {
        this.listaOperacaoDepartamento = listaOperacaoDepartamento;
    }
}
