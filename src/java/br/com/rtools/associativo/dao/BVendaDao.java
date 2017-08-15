package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.BVenda;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class BVendaDao extends DB {

    public List<BVenda> findBy(Integer pessoa_id, Integer evento_id) {
        try {

            Query query = getEntityManager().createQuery("SELECT V FROM BVenda AS V WHERE V.responsavel.id = :pessoa_id AND V.evento.id = :evento_id");
            query.setParameter("pessoa_id", pessoa_id);
            query.setParameter("evento_id", evento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
