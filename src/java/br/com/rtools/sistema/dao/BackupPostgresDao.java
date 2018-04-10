package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.BackupPostgres;
import javax.persistence.Query;

public class BackupPostgresDao extends DB {

    public BackupPostgres exist() {
        try {
            Query query = getEntityManager().createQuery("SELECT BP FROM BackupPostgres AS BP WHERE BP.dtProcessado IS NULL ORDER BY BP.id ASC");
            query.setMaxResults(1);
            return (BackupPostgres) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
