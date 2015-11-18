package br.com.rtools.digitalizacao.dao;

import br.com.rtools.digitalizacao.GrupoDigitalizacao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class GrupoDigitalizacaoDao extends DB {

    public List<GrupoDigitalizacao> listaGrupo(){
        Query qry = getEntityManager().createQuery("SELECT g FROM GrupoDigitalizacao g ORDER BY g.descricao");
        
        try{
            return qry.getResultList();
        }catch(Exception e){
            e.getMessage();
        }
        return new ArrayList();
    }
}
