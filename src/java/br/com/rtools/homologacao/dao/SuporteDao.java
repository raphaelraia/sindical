/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.homologacao.dao;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.homologacao.Prazo;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class SuporteDao extends DB {
    
    public List<Convencao> listaConvencao(){
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT c.* FROM arr_convencao c ORDER BY c.ds_descricao", Convencao.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        
        return new ArrayList();
    }
    
    public List<GrupoCidade> listaGrupoCidade(){
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT gc.* FROM arr_grupo_cidade gc ORDER BY gc.ds_descricao", GrupoCidade.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        
        return new ArrayList();
    }
    
    public List<Prazo> listaPrazo(){
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT p.* FROM hom_prazo p", Prazo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        
        return new ArrayList();
    }
    
}
