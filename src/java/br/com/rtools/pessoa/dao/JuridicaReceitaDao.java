package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.JuridicaReceita;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class JuridicaReceitaDao extends DB {

    public List<JuridicaReceita> findByPessoa(Integer pessoa_id) {
        try {
            getEntityManager().clear();
            Query query = getEntityManager().createQuery("SELECT JR FROM JuridicaReceita AS JR WHERE JR.pessoa.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
