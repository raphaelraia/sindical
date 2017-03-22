package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.Periodo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PeriodoDao extends DB {

    public List<Periodo> findByPeriodoMensalidade() {
        try {
            String queryString = ""
                    + "SELECT P.* \n"
                    + "FROM sis_periodo AS P \n"
                    + "WHERE P.id IN (\n"
                    + "SELECT id_periodo FROM soc_periodo_mensalidade GROUP BY id_periodo\n"
                    + ") \n"
                    + "ORDER BY P.nr_dias;";
            Query query = getEntityManager().createNativeQuery(queryString, Periodo.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }

    }
}
