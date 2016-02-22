package br.com.rtools.homologacao.dao;

import br.com.rtools.homologacao.Senha;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SenhaDao extends DB {

    public List ultimasQuatro(Integer filial_id) {
        try {
            String queryString;
            queryString = " SELECT S.* "
                    + "       FROM hom_senha AS S                   \n"
                    + "      WHERE id_filial = " + filial_id + "    \n"
                    + "        AND nr_mesa > 0                      \n"
                    + "        AND ds_hora_chamada IS NOT NULL      \n"
                    + "        AND dt_data = current_date           \n"
                    + "   ORDER BY nr_ordem DESC, nr_senha DESC     \n"
                    + "      LIMIT 4                                ";
            Query query = getEntityManager().createNativeQuery(queryString, Senha.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();

        }
    }

    public Boolean novaChamada(Integer filial_id) {
        try {
            String queryString;
            queryString = " SELECT S.* "
                    + "       FROM hom_senha AS S                   \n"
                    + "      WHERE id_filial = " + filial_id + "    \n"
                    + "        AND nr_mesa > 0                      \n"
                    + "        AND ds_hora_chamada IS NOT NULL      \n"
                    + "        AND ds_hora_chamada IS NOT NULL      \n"
                    + "        AND dt_verificada IS NOT NULL        \n"
                    + "   ORDER BY nr_ordem DESC                    ";
            Query query = getEntityManager().createNativeQuery(queryString, Senha.class);
            Boolean retorno = !query.getResultList().isEmpty();
            if(retorno) {
                try {
                    getEntityManager().getTransaction().begin();
                    Query queryB = getEntityManager().createNativeQuery("UPDATE hom_senha SET dt_verificada = null WHERE dt_verificada IS NOT NULL");
                    queryB.executeUpdate();
                    getEntityManager().getTransaction().commit();
                    return true;
                } catch (Exception e) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }                
            }
            return retorno;
        } catch (Exception e) {
            return false;

        }
    }

}
