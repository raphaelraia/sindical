package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisNotificacao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisNotificacaoClienteDao extends DB {

    public List findBy(Integer sis_notificacao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SNC FROM SisNotificacaoCliente SNC WHERE SNC.sisNotificacao.id = :sis_notificacao_id ORDER BY SNC.configuracao.identifica ASC ");
            query.setParameter("sis_notificacao_id", sis_notificacao_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<SisNotificacao> findAll() {
        try {
            String queryString = ""
                    + "     SELECT N.*                                                                      \n"
                    + "       FROM sis_notificacao AS N                                                     \n"
                    + " INNER JOIN sis_notificacao_categoria AS NCAT ON NCAT.id = N.id_notificacao_categoria\n"
                    + "      WHERE N.is_ativo = true                                                        \n"
                    + "        AND current_timestamp >= N.dt_inicial                                        \n"
                    + "        AND current_timestamp <= N.dt_final  ";

            Query query = getEntityManager().createNativeQuery(queryString, SisNotificacao.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
