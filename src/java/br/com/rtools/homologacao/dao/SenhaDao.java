package br.com.rtools.homologacao.dao;

import br.com.rtools.homologacao.Senha;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SenhaDao extends DB {

    public List sequence(Integer filial_id, Integer limit) {
        try {
            String queryString;
            queryString = " SELECT S.* "
                    + "       FROM hom_senha AS S                   \n"
                    + "      WHERE id_filial = " + filial_id + "    \n"
                    + "        AND nr_mesa > 0                      \n"
                    + "        AND ds_hora_chamada IS NOT NULL      \n"
                    + "        AND dt_data = current_date           \n"
                    + "   ORDER BY dt_nova_chamada IS NOT NULL DESC,\n"
                    + "            nr_ordem DESC,                   \n"
                    + "            nr_senha DESC                    \n"
                    + "      LIMIT " + limit + "                    ";
            Query query = getEntityManager().createNativeQuery(queryString, Senha.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                queryString = " SELECT S.* "
                        + "       FROM hom_senha AS S                   \n"
                        + "      WHERE id_filial = " + filial_id + "    \n"
                        + "        AND nr_mesa > 0                      \n"
                        + "        AND ds_hora_chamada IS NOT NULL      \n"
                        + "        AND dt_data = current_date           \n"
                        + "        AND dt_nova_chamada IS NOT NULL      ";
                query = getEntityManager().createNativeQuery(queryString);
                List listUpdate = query.getResultList();
                if (!listUpdate.isEmpty()) {
                    try {
                        getEntityManager().getTransaction().begin();
                        Query queryB = getEntityManager().createNativeQuery("UPDATE hom_senha SET dt_nova_chamada = null WHERE dt_nova_chamada IS NOT NULL");
                        queryB.executeUpdate();
                        getEntityManager().getTransaction().commit();
                    } catch (Exception e) {
                        getEntityManager().getTransaction().rollback();
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();

        }
    }

    public List findRequest(Integer filial_id) {
        List list;
        try {
            String queryString;
            queryString = " SELECT S.* "
                    + "       FROM hom_senha AS S                   \n"
                    + "      WHERE id_filial = " + filial_id + "    \n"
                    + "        AND dt_verificada IS NOT NULL        \n"
                    + "   ORDER BY nr_ordem DESC                    \n";
            Query query = getEntityManager().createNativeQuery(queryString, Senha.class);
            list = query.getResultList();
            if (!list.isEmpty()) {
                try {
                    getEntityManager().getTransaction().begin();
                    Query queryB = getEntityManager().createNativeQuery("UPDATE hom_senha SET dt_verificada = null WHERE dt_verificada IS NOT NULL");
                    queryB.executeUpdate();
                    getEntityManager().getTransaction().commit();
                    return list;
                } catch (Exception e) {
                    getEntityManager().getTransaction().rollback();
                    return null;
                }
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

}
