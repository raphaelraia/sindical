package br.com.rtools.escola.dao;

import br.com.rtools.escola.AgrupaTurma;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AgrupaTurmaDao extends DB {

    public List findIntegral() {
        try {
            String queryString = ""
                    + "     SELECT AT.*                             \n"
                    + "       FROM esc_agrupa_turma AS AT           \n"
                    + "      WHERE AT.id_turma_integral IS NOT NULL \n"
                    + "        AND AT.id_turma_integral = AT.id_turma  ";
            Query query = getEntityManager().createNativeQuery(queryString, AgrupaTurma.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List pesquisaPorTurmaIntegral(int idTurma) {
        try {
            Query query = getEntityManager().createQuery(" SELECT AT FROM AgrupaTurma AS AT WHERE AT.turmaIntegral.id = :idTurma ");
            query.setParameter("idTurma", idTurma);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

}
