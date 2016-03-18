package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Cupom;
import br.com.rtools.associativo.Sorteio;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CupomDao extends DB {

    public List findByHistorico(Boolean history) {
        return findByHistorico(history, Boolean.TRUE);
    }

    public List findByHistorico(Boolean history, Boolean actives) {
        String queryString;
        try {
            if (history) {
                queryString = ""
                        + "SELECT C.*                                           \n"
                        + "  FROM eve_cupom AS C                                \n"
                        + " WHERE C.dt_inicio < CURRENT_DATE                    \n";
            } else {
                queryString = ""
                        + "SELECT C.*                                           \n"
                        + "  FROM eve_cupom AS C                                \n"
                        + " WHERE C.dt_data >= CURRENT_DATE                     \n";
            }
//            if (actives) {
//                queryString += " AND C.is_ativo = true \n";
//            }
            Query query = getEntityManager().createNativeQuery(queryString, Cupom.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Sorteio find(String descricao, String data) {
        try {
            String queryString = ""
                    + "SELECT C.*                                               \n"
                    + "  FROM eve_cupom AS C                                    \n"
                    + " WHERE C.dt_data = '" + data + "'                        \n"
                    + "   AND func_translate(UPPER(C.ds_descricao)) LIKE func_translate(UPPER('" + descricao + "')) \n"
                    + " LIMIT 1";
            Query query = getEntityManager().createNativeQuery(queryString, Cupom.class);
            return (Sorteio) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
