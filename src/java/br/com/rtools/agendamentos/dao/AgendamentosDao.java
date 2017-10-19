package br.com.rtools.agendamentos.dao;

import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class AgendamentosDao extends DB {

    public List findSchedules() {
        try {
            Query query = getEntityManager().createNativeQuery("");
            return query.getResultList();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

}
