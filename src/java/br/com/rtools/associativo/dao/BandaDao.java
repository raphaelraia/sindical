package br.com.rtools.associativo.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.associativo.Banda;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class BandaDao extends DB {

    public List<Banda> findAllNotInEvento(Integer evento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT B FROM Banda B WHERE B.id NOT IN (SELECT EB.banda.id FROM EventoBanda EB WHERE EB.evento.id = :evento_id) ORDER BY B.descricao ASC");
            query.setParameter("evento_id", evento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
