package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Suspencao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SuspencaoDao extends DB {

    public boolean existeSuspensaoSocio(Pessoa pessoa) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT s.* \n"
                    + "  FROM soc_suspencao s\n"
                    + " WHERE s.id_pessoa = " + pessoa.getId() + "\n"
                    + "   AND s.dt_final < CURRENT_DATE", Suspencao.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public Suspencao pesquisaSuspensao(Pessoa pessoa) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT s.* \n"
                    + "  FROM soc_suspencao s\n"
                    + " WHERE s.id_pessoa = " + pessoa.getId() + "\n"
                    + "   AND s.dt_final < CURRENT_DATE", Suspencao.class);
            return (Suspencao) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Suspencao> pesquisaSuspensao(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "     SELECT S.*               \n"
                    + "     FROM soc_suspencao S   \n"
                    + "    WHERE S.id_pessoa = " + pessoa_id + "\n"
                    + " ORDER BY S.dt_final DESC, S.dt_inicial ASC ", Suspencao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Suspencao exists(Integer pessoa_id) {
        try {
            String queryString = ""
                    + "     SELECT S.*                                          \n"
                    + "       FROM soc_suspencao S                              \n"
                    + "      WHERE s.id_pessoa = " + pessoa_id + "              \n"
                    + "        AND current_date BETWEEN S.dt_inicial AND S.dt_final ";
            Query query = getEntityManager().createNativeQuery(queryString, Suspencao.class);
            return (Suspencao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
