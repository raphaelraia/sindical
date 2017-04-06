package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.BaixaLog;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class BaixaLogDao extends DB {

    public List<BaixaLog> listByBaixaMovimento(Integer baixa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT BL FROM BaixaLog BL WHERE BL.baixa.id = :baixa_id");
            query.setParameter("baixa_id", baixa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
