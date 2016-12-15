package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Impressao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ImpressaoDao extends DB {

    public List<Impressao> findByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT I FROM Impressao AS I WHERE I.movimento.id = :movimento_id ORDER BY I.dtImpressao DESC");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<Impressao> findByUsuario(Integer usuario_id, Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT I FROM Impressao AS I WHERE I.movimento.id = :movimento_id ORDER BY I.dtImpressao DESC");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
