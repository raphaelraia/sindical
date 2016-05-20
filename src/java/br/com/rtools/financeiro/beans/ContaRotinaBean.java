package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.ContaRotina;
import br.com.rtools.financeiro.Plano4;
import br.com.rtools.financeiro.dao.Plano4Dao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ContaRotinaBean implements Serializable {
    private Integer idRotina = 0;
    private List<SelectItem> listaRotina = new ArrayList();
    private Integer idPlano4 = 0;
    private List<SelectItem> listaPlano4 = new ArrayList();
    private List<ContaRotina> listaContaRotina = new ArrayList();
    

    @PostConstruct
    public void init(){
        
    }
    
    @PreDestroy
    public void destroy(){
        
    }
    
    public void adicionar(){
        ContaRotina cr = new ContaRotina(
                -1, 
                (Rotina) new Dao().find(new Rotina(), Integer.valueOf(listaRotina.get(idRotina).getDescription())), 
                (Plano4) new Dao().find(new Plano4(), Integer.valueOf(listaPlano4.get(idPlano4).getDescription())), 
                null, 
                1
        );
        
        Dao dao = new Dao();
        
        dao.openTransaction();
        
        if (!dao.save(cr)){
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível salvar Conta Rotina!");
            return;
        }
        
        dao.commit();
        
        listaContaRotina.clear();
        listaPlano4.clear();
        GenericaMensagem.info("Sucesso", "Conta Rotina Adicionada!");
    }
    
    public void remover(ContaRotina linha){
        Dao dao = new Dao();
        
        dao.openTransaction();
        
        if (!dao.delete(dao.find(linha))){
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível excluir Conta Rotina!");
            return;
        }
        
        dao.commit();
        
        listaContaRotina.clear();
        listaPlano4.clear();
        GenericaMensagem.info("Sucesso", "Conta Rotina Removida!");
    }
    
    public Integer getIdRotina() {
        return idRotina;
    }

    public void setIdRotina(Integer idRotina) {
        this.idRotina = idRotina;
    }

    public List<SelectItem> getListaRotina() {
        if (listaRotina.isEmpty()){
            List<Rotina> result = new ArrayList();
            
            result.add((Rotina) new Dao().find(new Rotina(), 1));
            result.add((Rotina) new Dao().find(new Rotina(), 2));
            
            for (int i = 0; i < result.size(); i++){
                listaRotina.add(
                        new SelectItem(
                                i, 
                                result.get(i).getRotina(), 
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }
        return listaRotina;
    }

    public void setListaRotina(List<SelectItem> listaRotina) {
        this.listaRotina = listaRotina;
    }

    public Integer getIdPlano4() {
        return idPlano4;
    }

    public void setIdPlano4(Integer idPlano4) {
        this.idPlano4 = idPlano4;
    }

    public List<SelectItem> getListaPlano4() {
        if (listaPlano4.isEmpty()){
            Plano4Dao db = new Plano4Dao();
            
            List<Plano4> result = db.listaPlano4ContaRotina();
            
            for (int i = 0; i < result.size(); i++){
                listaPlano4.add(
                        new SelectItem(
                                i, 
                                result.get(i).getClassificador()+" - "+result.get(i).getConta(), 
                                Integer.toString(result.get(i).getId())
                        )
                );
            }
        }
        return listaPlano4;
    }

    public void setListaPlano4(List<SelectItem> listaPlano4) {
        this.listaPlano4 = listaPlano4;
    }

    public List<ContaRotina> getListaContaRotina() {
        if (listaContaRotina.isEmpty()){
            //listaContaRotina = new Dao().liveList("SELECT cr FROM ContaRotina cr");
            listaContaRotina = new Dao().list(new ContaRotina());
        }
        return listaContaRotina;
    }

    public void setListaContaRotina(List<ContaRotina> listaContaRotina) {
        this.listaContaRotina = listaContaRotina;
    }
}
