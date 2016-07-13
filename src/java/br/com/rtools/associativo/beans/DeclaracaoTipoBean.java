/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

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

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class DeclaracaoTipoBean implements Serializable {
    private DeclaracaoTipo declaracaoTipo = new DeclaracaoTipo();
    private List<DeclaracaoTipo> listaDeclaracaoTipo = new ArrayList();
    
    
    public DeclaracaoTipoBean(){
        loadDeclaracaoTipo();
    }
    
    public final void loadDeclaracaoTipo(){
        listaDeclaracaoTipo.clear();
        
        listaDeclaracaoTipo = new DeclaracaoTipoDao().listaDeclaracaoTipo();
    }
    
    public Boolean validaSalvar(){
        if (declaracaoTipo.getDescricao().isEmpty() || declaracaoTipo.getDescricao().length() < 4){
            GenericaMensagem.warn("Atenção", "Digite uma Descrição para a Declaração!");
            return false;
        }
        
        if (declaracaoTipo.getJasper().isEmpty() || declaracaoTipo.getJasper().length() < 4){
            GenericaMensagem.warn("Atenção", "Digite o nome do Jasper para a Declaração!");
            return false;
        }
        
        if (declaracaoTipo.getIdadeInicio() == null || declaracaoTipo.getIdadeInicio() < 0){
            GenericaMensagem.warn("Atenção", "Digite uma idade inicial válida!");
            return false;
        }
        
        if (declaracaoTipo.getIdadeFinal() == null || declaracaoTipo.getIdadeFinal() < 0 || declaracaoTipo.getIdadeInicio() > declaracaoTipo.getIdadeFinal()){
            GenericaMensagem.warn("Atenção", "Digite uma idade final válida!");
            return false;
        }
        
        if (declaracaoTipo.getValidade() == null || declaracaoTipo.getValidade() < 0){
            GenericaMensagem.warn("Atenção", "Digite uma validade!");
            return false;
        }
        
        return true;
    }
    
    public void salvar(){
        if (!validaSalvar()){
            return;
        }
        
        Dao dao = new Dao();
        NovoLog logs = new NovoLog();
        logs.setTabela("soc_declaracao_tipo");
        
        dao.openTransaction();
        if (declaracaoTipo.getId() == -1){
            if (!dao.save(declaracaoTipo)){
                dao.rollback();
                GenericaMensagem.error("Atenção", "Não foi possível salvar Declaração Tipo!");
                return;
            }
            logs.save(
                    "ID: " + declaracaoTipo.getId() + "\n" +
                    "Descrição: " + declaracaoTipo.getDescricao() + "\n" +
                    "Jasper: " + declaracaoTipo.getJasper() + "\n" +
                    "Idade Inicial: " + declaracaoTipo.getIdadeInicio() + "\n" +
                    "Idade Final: " + declaracaoTipo.getIdadeFinal()+ "\n" +
                    "Validade: " + declaracaoTipo.getValidade()+ "\n" +
                    "Tipo Validade: " + (declaracaoTipo.getValidadeTipo() == 0 ? "Dia" : declaracaoTipo.getValidadeTipo() == 1 ? "Mês" : "Ano") + "\n" +
                    "Dias Atraso: " + declaracaoTipo.getDiasAtraso() + "\n"
            );
            GenericaMensagem.info("Sucesso", "Declaração Tipo Salva!");
        }else{
            DeclaracaoTipo dt_antes = (DeclaracaoTipo) dao.find(new DeclaracaoTipo(), declaracaoTipo.getId());
            logs.update(
                    "ID: " + dt_antes.getId() + "\n" +
                    "Descrição: " + dt_antes.getDescricao() + "\n" +
                    "Jasper: " + dt_antes.getJasper() + "\n" +
                    "Idade Inicial: " + dt_antes.getIdadeInicio() + "\n" +
                    "Idade Final: " + dt_antes.getIdadeFinal()+ "\n" +
                    "Validade: " + dt_antes.getValidade()+ "\n" +
                    "Tipo Validade: " + (dt_antes.getValidadeTipo() == 0 ? "Dia" : dt_antes.getValidadeTipo() == 1 ? "Mês" : "Ano") + "\n" +
                    "Dias Atraso: " + dt_antes.getDiasAtraso() + "\n",
                    
                    "ID: " + declaracaoTipo.getId() + "\n" +
                    "Descrição: " + declaracaoTipo.getDescricao() + "\n" +
                    "Jasper: " + declaracaoTipo.getJasper() + "\n" +
                    "Idade Inicial: " + declaracaoTipo.getIdadeInicio() + "\n" +
                    "Idade Final: " + declaracaoTipo.getIdadeFinal()+ "\n" +
                    "Validade: " + declaracaoTipo.getValidade()+ "\n" +
                    "Tipo Validade: " + (declaracaoTipo.getValidadeTipo() == 0 ? "Dia" : declaracaoTipo.getValidadeTipo() == 1 ? "Mês" : "Ano") + "\n" +
                    "Dias Atraso: " + declaracaoTipo.getDiasAtraso() + "\n"
            );

            if (!dao.update(declaracaoTipo)){
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
    
    public void editar(DeclaracaoTipo dt){
        declaracaoTipo = dt;
    }
    
    public void novo(){
        GenericaSessao.put("declaracaoTipoBean", new DeclaracaoTipoBean());
    }
    
    public void excluir(){
        
        Dao dao = new Dao();
        NovoLog logs = new NovoLog();
        
        dao.openTransaction();
        if (declaracaoTipo.getId() != -1){
            if (!dao.delete(declaracaoTipo)){
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível excluir Declaração Tipo!");
                return;
            }
            dao.commit();
            
            logs.delete(
                    "ID: " + declaracaoTipo.getId() + "\n" +
                    "Descrição: " + declaracaoTipo.getDescricao() + "\n" +
                    "Jasper: " + declaracaoTipo.getJasper() + "\n" +
                    "Idade Inicial: " + declaracaoTipo.getIdadeInicio() + "\n" +
                    "Idade Final: " + declaracaoTipo.getIdadeFinal()+ "\n" +
                    "Validade: " + declaracaoTipo.getValidade()+ "\n" +
                    "Tipo Validade: " + (declaracaoTipo.getValidadeTipo() == 0 ? "Dia" : declaracaoTipo.getValidadeTipo() == 1 ? "Mês" : "Ano") + "\n" +
                    "Dias Atraso: " + declaracaoTipo.getDiasAtraso() + "\n"
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
    
}
