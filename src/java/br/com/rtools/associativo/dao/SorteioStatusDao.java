package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.SorteioStatus;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SorteioStatusDao extends DB {

    public SorteioStatus findBySorteio(Integer sorteio_id, Integer grupo_cidade_id) {
        try {
            Query query;
            if (grupo_cidade_id == null) {
                query = getEntityManager().createQuery("SELECT SS FROM SorteioStatus AS SS WHERE SS.sorteio.id = :sorteio_id AND SS.grupoCidade IS NULL");
            } else {
                query = getEntityManager().createQuery("SELECT SS FROM SorteioStatus AS SS WHERE SS.sorteio.id = :sorteio_id AND SS.grupoCidade.id = :grupo_cidade_id");
                query.setParameter("grupo_cidade_id", grupo_cidade_id);
            }
            query.setParameter("sorteio_id", sorteio_id);
            return (SorteioStatus) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

    public List<SorteioStatus> findBySorteio(Integer sorteio_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SS FROM SorteioStatus AS SS WHERE SS.sorteio.id = :sorteio_id ");
            query.setParameter("sorteio_id", sorteio_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
