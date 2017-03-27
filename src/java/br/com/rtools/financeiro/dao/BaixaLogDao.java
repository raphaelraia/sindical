package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.BaixaLog;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class BaixaLogDao extends DB {

    public BaixaLog findByBaixaMovimento(Integer baixa_id, Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT BL FROM BaixaLog BL WHERE BL.baixa.id = :baixa_id AND BL.movimento.id = :movimento_id");
            query.setParameter("baixa_id", baixa_id);
            query.setParameter("movimento_id", movimento_id);
            return (BaixaLog) query.getSingleResult();
        } catch (Exception e) {
            return null;

        }
    }
}
