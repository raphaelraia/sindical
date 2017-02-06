package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Historico;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class HistoricoDao extends DB {

    public Historico findByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT H FROM Historico AS H WHERE H.movimento.id = :movimento_id ORDER BY H.id DESC");
            query.setParameter("movimento_id", movimento_id);
            return (Historico) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Historico> findAllByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT H FROM Historico AS H WHERE H.movimento.id = :movimento_id ORDER BY H.id DESC");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
