package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.RepisMovimento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RepisMovimentoDao extends DB {

    public List<RepisMovimento> findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT RM FROM RepisMovimento AS RM WHERE RM.pessoa.id = :pessoa_id ORDER BY RM.dataEmissao ");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

}
