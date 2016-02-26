package br.com.rtools.associativo.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SorteioCategoriaDao extends DB {

    public List findBySorteio(Integer sorteio_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SC FROM SorteioCategoria AS SC WHERE SC.sorteio.id = :sorteio_id ORDER BY SC.categoria.categoria ASC ");
            query.setParameter("sorteio_id", sorteio_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List exist(Integer sorteio_id, Integer categoria_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SC FROM SorteioCategoria AS SC WHERE SC.sorteio.id = :sorteio_id AND SC.categoria.id = :categoria_id ORDER BY SC.categoria.categoria ASC ");
            query.setParameter("sorteio_id", sorteio_id);
            query.setParameter("categoria_id", categoria_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
