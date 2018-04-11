package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.BackupPostgres;
import java.util.List;
import javax.persistence.Query;

public class BackupPostgresDao extends DB {

    public BackupPostgres exist() {
        try {
            Query query = getEntityManager().createQuery("SELECT BP FROM BackupPostgres AS BP WHERE BP.dtEnviado IS NULL ORDER BY BP.id ASC");
            query.setMaxResults(1);
            return (BackupPostgres) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public BackupPostgres exist(Integer configuracao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT BP FROM BackupPostgres AS BP WHERE BP.dtEnviado IS NULL AND BP.configuracao.id = :configuracao_id ORDER BY BP.id ASC");
            query.setParameter("configuracao_id", configuracao_id);
            query.setMaxResults(1);
            return (BackupPostgres) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<BackupPostgres> findByAllClient(Integer configuracao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT BP FROM BackupPostgres AS BP WHERE BP.configuracao.id = :configuracao_id ORDER BY BP.id ASC");
            query.setParameter("configuracao_id", configuracao_id);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
