/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.DeclaracaoPeriodo;
import br.com.rtools.associativo.DeclaracaoTipo;
import br.com.rtools.associativo.dao.DeclaracaoTipoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
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
public class DeclaracaoTipoBean implements Serializable {

    private DeclaracaoTipo declaracaoTipo = new DeclaracaoTipo();
    private List<DeclaracaoTipo> listaDeclaracaoTipo = new ArrayList();

    private List<DeclaracaoPeriodo> listaDeclaracaoPeriodo = new ArrayList();
    private DeclaracaoPeriodo declaracaoPeriodo = new DeclaracaoPeriodo();
    
    private DeclaracaoTipo declaracaoTipoEditar = new DeclaracaoTipo();

    public DeclaracaoTipoBean() {
        loadDeclaracaoTipo();
    }

    public void openDeclaracaoPeriodo(DeclaracaoTipo dt) {
        declaracaoTipoEditar = dt;

        declaracaoPeriodo = new DeclaracaoPeriodo();

        loadListaDeclaracaoPeriodo();
    }

    public final void loadListaDeclaracaoPeriodo() {
        listaDeclaracaoPeriodo.clear();

        listaDeclaracaoPeriodo = new DeclaracaoTipoDao().listaDeclaracaoPeriodo(declaracaoTipoEditar.getId());
    }

    public void salvarDeclaracaoPeriodo() {
        Dao dao = new Dao();

        
        dao.openTransaction();
        
        if (declaracaoPeriodo.getId() == -1) {
            
            declaracaoPeriodo.setDeclaracaoTipo(declaracaoTipoEditar);
            
            if (!new DeclaracaoTipoDao().listaDeclaracaoPeriodoExiste(declaracaoTipoEditar.getId(), declaracaoPeriodo.getDescricao(), declaracaoPeriodo.getAno()).isEmpty()){
                dao.rollback();
                GenericaMensagem.warn("Atenção", "Essa Declaração já existe!");
                return;
            }
            
            if (!dao.save(declaracaoPeriodo)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Não foi possível salvar Declaração Período!");
                return;
            }
        } else if (!dao.update(declaracaoPeriodo)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Não foi possível salvar Declaração Período!");
            return;
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Declaração Salva!");

        declaracaoPeriodo = new DeclaracaoPeriodo();

        loadListaDeclaracaoPeriodo();
    }

    public void excluirDeclaracaoPeriodo() {
        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(declaracaoPeriodo)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Não foi possível Excluir Declaração Período!");
            return;
        }

        dao.commit();
        
        GenericaMensagem.info("Sucesso", "Declaração Período excluída!");

        declaracaoPeriodo = new DeclaracaoPeriodo();

        loadListaDeclaracaoPeriodo();
    }

    public void editarDeclaracaoPeriodo(DeclaracaoPeriodo dp) {
        declaracaoPeriodo = dp;
    }

    public void novoDP() {
        declaracaoPeriodo = new DeclaracaoPeriodo();
    }

    public final void loadDeclaracaoTipo() {
        listaDeclaracaoTipo.clear();

        listaDeclaracaoTipo = new DeclaracaoTipoDao().listaDeclaracaoTipo();
    }

    public Boolean validaSalvar() {
        if (declaracaoTipo.getDescricao().isEmpty() || declaracaoTipo.getDescricao().length() < 4) {
            GenericaMensagem.warn("Atenção", "Digite uma Descrição para a Declaração!");
            return false;
        }

        if (declaracaoTipo.getJasper().isEmpty() || declaracaoTipo.getJasper().length() < 4) {
            GenericaMensagem.warn("Atenção", "Digite o nome do Jasper para a Declaração!");
            return false;
        }

        if (declaracaoTipo.getIdadeInicio() == null || declaracaoTipo.getIdadeInicio() < 0) {
            GenericaMensagem.warn("Atenção", "Digite uma idade inicial válida!");
            return false;
        }

        if (declaracaoTipo.getIdadeFinal() == null || declaracaoTipo.getIdadeFinal() < 0 || declaracaoTipo.getIdadeInicio() > declaracaoTipo.getIdadeFinal()) {
            GenericaMensagem.warn("Atenção", "Digite uma idade final válida!");
            return false;
        }

        return true;
    }

    public void salvar() {
        if (!validaSalvar()) {
            return;
        }

        Dao dao = new Dao();
        NovoLog logs = new NovoLog();
        logs.setTabela("soc_declaracao_tipo");

        dao.openTransaction();
        if (declaracaoTipo.getId() == -1) {
            if (!dao.save(declaracaoTipo)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Não foi possível salvar Declaração Tipo!");
                return;
            }
            logs.save(
                    "ID: " + declaracaoTipo.getId() + "\n"
                    + "Descrição: " + declaracaoTipo.getDescricao() + "\n"
                    + "Jasper: " + declaracaoTipo.getJasper() + "\n"
                    + "Idade Inicial: " + declaracaoTipo.getIdadeInicio() + "\n"
                    + "Idade Final: " + declaracaoTipo.getIdadeFinal() + "\n"
                    + "Dias Atraso: " + declaracaoTipo.getDiasCarencia() + "\n"
            );
            GenericaMensagem.info("Sucesso", "Declaração Tipo Salva!");
        } else {
            DeclaracaoTipo dt_antes = (DeclaracaoTipo) dao.find(new DeclaracaoTipo(), declaracaoTipo.getId());
            logs.update(
                    "ID: " + dt_antes.getId() + "\n"
                    + "Descrição: " + dt_antes.getDescricao() + "\n"
                    + "Jasper: " + dt_antes.getJasper() + "\n"
                    + "Idade Inicial: " + dt_antes.getIdadeInicio() + "\n"
                    + "Idade Final: " + dt_antes.getIdadeFinal() + "\n"
                    + "Dias Atraso: " + dt_antes.getDiasCarencia() + "\n",
                    "ID: " + declaracaoTipo.getId() + "\n"
                    + "Descrição: " + declaracaoTipo.getDescricao() + "\n"
                    + "Jasper: " + declaracaoTipo.getJasper() + "\n"
                    + "Idade Inicial: " + declaracaoTipo.getIdadeInicio() + "\n"
                    + "Idade Final: " + declaracaoTipo.getIdadeFinal() + "\n"
                    + "Dias Atraso: " + declaracaoTipo.getDiasCarencia() + "\n"
            );

            if (!dao.update(declaracaoTipo)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Não foi possível salvar Declaração Tipo!");
                return;
            }

            GenericaMensagem.info("Sucesso", "Declaração Tipo Atualizada!");
        }

        dao.commit();
        declaracaoTipo = new DeclaracaoTipo();
        loadDeclaracaoTipo();
    }

    public void editar(DeclaracaoTipo dt) {
        declaracaoTipo = dt;
    }

    public void novo() {
        GenericaSessao.put("declaracaoTipoBean", new DeclaracaoTipoBean());
    }

    public void excluir() {

        Dao dao = new Dao();
        NovoLog logs = new NovoLog();

        dao.openTransaction();
        if (declaracaoTipo.getId() != -1) {
            if (!dao.delete(declaracaoTipo)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível excluir Declaração Tipo!");
                return;
            }
            dao.commit();

            logs.delete(
                    "ID: " + declaracaoTipo.getId() + "\n"
                    + "Descrição: " + declaracaoTipo.getDescricao() + "\n"
                    + "Jasper: " + declaracaoTipo.getJasper() + "\n"
                    + "Idade Inicial: " + declaracaoTipo.getIdadeInicio() + "\n"
                    + "Idade Final: " + declaracaoTipo.getIdadeFinal() + "\n"
                    + "Dias Atraso: " + declaracaoTipo.getDiasCarencia() + "\n"
            );

            declaracaoTipo = new DeclaracaoTipo();
            loadDeclaracaoTipo();

            GenericaMensagem.info("Sucesso", "Declaração Tipo Excluída!");
        }
    }

    public DeclaracaoTipo getDeclaracaoTipo() {
        return declaracaoTipo;
    }

    public void setDeclaracaoTipo(DeclaracaoTipo declaracaoTipo) {
        this.declaracaoTipo = declaracaoTipo;
    }

    public List<DeclaracaoTipo> getListaDeclaracaoTipo() {
        return listaDeclaracaoTipo;
    }

    public void setListaDeclaracaoTipo(List<DeclaracaoTipo> listaDeclaracaoTipo) {
        this.listaDeclaracaoTipo = listaDeclaracaoTipo;
    }

    public List<DeclaracaoPeriodo> getListaDeclaracaoPeriodo() {
        return listaDeclaracaoPeriodo;
    }

    public void setListaDeclaracaoPeriodo(List<DeclaracaoPeriodo> listaDeclaracaoPeriodo) {
        this.listaDeclaracaoPeriodo = listaDeclaracaoPeriodo;
    }

    public DeclaracaoPeriodo getDeclaracaoPeriodo() {
        return declaracaoPeriodo;
    }

    public void setDeclaracaoPeriodo(DeclaracaoPeriodo declaracaoPeriodo) {
        this.declaracaoPeriodo = declaracaoPeriodo;
    }

}
