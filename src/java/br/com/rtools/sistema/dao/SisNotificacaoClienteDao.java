package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
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
}
