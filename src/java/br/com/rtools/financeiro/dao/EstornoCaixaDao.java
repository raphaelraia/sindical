package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EstornoCaixaDao extends DB {

    public List findAllByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT EC FROM EstornoCaixa AS EC WHERE EC.movimento.id = :movimento_id");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
