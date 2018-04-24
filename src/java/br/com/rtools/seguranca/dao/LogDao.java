package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.LogDefinicoes;
import javax.persistence.Query;

public class LogDao extends DB {

    public void clearLogs(Integer rotina_id, Integer nrDias, Boolean ignoreRotina) {
        if (nrDias == null) {
            nrDias = LogDefinicoes.KEEP_DAYS_LOGS;
        }
        if (nrDias < 180) {
            nrDias = 180;
        }
        String queryString = "DELETE FROM seg_log WHERE dt_data < current_date - " + nrDias;
        if (!ignoreRotina) {
            if (rotina_id == null) {
                queryString += " AND id_rotina IS NULL ";
            } else {
                queryString += " AND id_rotina = " + rotina_id;
            }
        }
        if (rotina_id != null) {
            if (rotina_id == -1) {
                queryString += " AND id_rotina IS NOT NULL AND id_rotina NOT IN (SELECT id_rotina FROM seg_log_definicoes WHERE id_rotina IS NOT NULL )";
            }
        }
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            getEntityManager().getTransaction().begin();
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
    }
}
