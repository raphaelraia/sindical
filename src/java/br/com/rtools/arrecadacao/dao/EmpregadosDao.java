package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.Empregados;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EmpregadosDao extends DB {

    /**
     *
     * @param convencao_periodo_id
     * @param pessoa_id
     * @return
     */
    public Integer pesquisaQuantidadeEmpregados(Integer convencao_periodo_id, Integer pessoa_id) {
        String queryString = ""
                + "      SELECT cast(sum(E.nr_quantidade) AS int)                                                                       \n"
                + "        FROM arr_empregados          AS E                                                                            \n"
                + "  INNER JOIN arr_convencao_periodo   AS CP ON CP.id = " + convencao_periodo_id + "                                   \n"
                + "  INNER JOIN pes_juridica            AS J  ON J.id = E.id_juridica                                                   \n"
                + "       WHERE cast('01/'||E.ds_referencia AS date)                                                                    \n"
                + "                 BETWEEN cast('01/'|| CP.ds_referencia_inicial AS date)                                              \n"
                + "                     AND date_trunc('month', cast('01/' || CP.ds_referencia_final AS date)) + INTERVAL '1 month' - INTERVAL '1 day' \n"
                + "         AND J.id_pessoa = " + pessoa_id;
        try {
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return Integer.parseInt(((List) list.get(0)).get(0).toString());
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public List<Empregados> findByJuridica(Integer juridica_id) {
        return findByJuridica(juridica_id, true);
    }

    public List<Empregados> findByJuridica(Integer juridica_id, Boolean asc) {
        try {
            Query query;
            if (asc) {
                query = getEntityManager().createQuery(" SELECT E FROM Empregados AS E WHERE E.juridica.id = :juridica_id ORDER BY E.dtLancamento ASC");
            } else {
                query = getEntityManager().createQuery(" SELECT E FROM Empregados AS E WHERE E.juridica.id = :juridica_id ORDER BY E.dtLancamento DESC");
            }
            query.setParameter("juridica_id", juridica_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

}
