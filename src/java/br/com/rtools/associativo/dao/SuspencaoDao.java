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

    public Suspencao existsSisPessoa(String documento) {
        try {
            String queryString = ""
                    + "    SELECT SU.*                                          \n"
                    + "      FROM pes_pessoa    AS P                            \n"
                    + "INNER JOIN pes_fisica    AS F  ON F.id_pessoa = P.id     \n"
                    + "INNER JOIN sis_pessoa    AS S  ON S.ds_documento IS NOT NULL AND TRIM(S.ds_documento) <> '' AND S.ds_documento = P.ds_documento \n"
                    + "INNER JOIN soc_suspencao AS SU ON SU.id_pessoa = P.id    \n"
                    + "     WHERE current_date BETWEEN SU.dt_inicial AND SU.dt_final    \n"
                    + "       AND S.ds_documento = '" + documento + "'";
            Query query = getEntityManager().createNativeQuery(queryString, Suspencao.class);
            return (Suspencao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Suspencao existsSisPessoa(String nome, String nascimento) {
        try {
            String queryString = ""
                    + "    SELECT SU.*                                          \n"
                    + "      FROM pes_pessoa    AS P                            \n"
                    + "INNER JOIN pes_fisica    AS F  ON F.id_pessoa = P.id     \n"
                    + "INNER JOIN sis_pessoa    AS S  ON S.ds_nome = P.ds_nome AND S.dt_nascimento = F.dt_nascimento AND S.dt_nascimento IS NOT NULL AND F.dt_nascimento IS NOT NULL \n"
                    + "INNER JOIN soc_suspencao AS SU ON SU.id_pessoa = P.id    \n"
                    + "     WHERE current_date BETWEEN SU.dt_inicial AND SU.dt_final \n"
                    + "       AND func_translate(UPPER(S.ds_nome)) LIKE func_translate(UPPER('" + nome + "'))"
                    + "       AND S.dt_nascimento = '" + nascimento + "'";
            Query query = getEntityManager().createNativeQuery(queryString, Suspencao.class);
            return (Suspencao) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
