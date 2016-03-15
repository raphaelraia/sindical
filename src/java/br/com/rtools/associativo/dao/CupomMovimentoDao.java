package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CupomMovimento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CupomMovimentoDao extends DB {

    public List findByCupom(Integer cupom_id) {
        String queryString = "       "
                + "     SELECT CM.*                                             \n"
                + "       FROM eve_cupom_movimento AS CM                        \n"
                + " INNER JOIN pes_pessoa AS P ON P.id = CM.id_pessoa           \n"
                + "      WHERE CM.id_cupom = " + cupom_id + "                   \n"
                + "   ORDER BY SM.dt_emissao DESC,                              \n"
                + "            P.ds_nome ASC                                    \n";

        try {
            Query query = getEntityManager().createNativeQuery(queryString, CupomMovimento.class);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CM FROM CupomMovimento AS CM WHERE CM.pessoa.id = :pessoa_id ORDER BY CM.dtEmissao DESC ");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
