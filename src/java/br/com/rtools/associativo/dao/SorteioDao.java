package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Sorteio;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SorteioDao extends DB {

    public List findByHistorico(Boolean history) {
        String queryString;
        try {
            if (history) {
                queryString = ""
                        + "SELECT S.*                                               \n"
                        + "  FROM sort_sorteio AS S                                 \n"
                        + " WHERE S.dt_inicio < CURRENT_DATE                        \n";
            } else {
                queryString = ""
                        + "SELECT S.*                                               \n"
                        + "  FROM sort_sorteio AS S                                 \n"
                        + " WHERE S.dt_inicio >= CURRENT_DATE                       \n"
                        + "    OR ( S.dt_fim IS NULL OR S.dt_fim <= CURRENT_DATE )  \n";
            }
            Query query = getEntityManager().createNativeQuery(queryString, Sorteio.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Sorteio find(String descricao, String dataInicio, String dataFim) {
        try {
            String queryString = ""
                    + " SELECT S.*                                  \n"
                    + "  FROM sort_sorteio AS S                     \n"
                    + " WHERE S.dt_inicio = '" + dataInicio + "'    \n"
                    + "   AND S.dt_fim = '" + dataFim + "'          \n"
                    + "   AND func_translate(UPPER(S.ds_descricao)) LIKE func_translate(UPPER('" + descricao + "')) \n"
                    + " LIMIT 1";
            Query query = getEntityManager().createNativeQuery(queryString, Sorteio.class);
            return (Sorteio) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
