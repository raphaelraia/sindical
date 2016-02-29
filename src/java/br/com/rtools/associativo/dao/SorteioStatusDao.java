package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.SorteioStatus;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class SorteioStatusDao extends DB {

    public SorteioStatus findBySorteio(Integer sorteio_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SS FROM SorteioStatus AS SS WHERE SS.sorteio.id = :sorteio_id ");
            query.setParameter("sorteio_id", sorteio_id);
            return (SorteioStatus) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }
}
