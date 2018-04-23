package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.UsuarioHistoricoAcesso;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class UsuarioHistoricoAcessoDao extends DB {

    public UsuarioHistoricoAcesso lastLogin(Integer usuario_id) {
        try {
            String queryString = " "
                    + "     SELECT UHA.*                                        \n"
                    + "       FROM seg_usuario_historico_acesso AS UHA          \n"
                    + "      WHERE id IN (                                      \n"
                    + "            SELECT (max(id))                             \n"
                    + "              FROM seg_usuario_historico_acesso          \n"
                    + "             WHERE id_usuario = " + usuario_id + "       \n"
                    + "      ) ";
            Query query = getEntityManager().createNativeQuery(queryString, UsuarioHistoricoAcesso.class);
            return (UsuarioHistoricoAcesso) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<UsuarioHistoricoAcesso> list(Integer usuario_id) {
        try {
            String queryString = " "
                    + "     SELECT UHA.*                                        \n"
                    + "       FROM seg_usuario_historico_acesso AS UHA          \n"
                    + "      WHERE id_usuario = " + usuario_id + "              \n"
                    + "     ORDER BY dt_login DESC                              \n";
            Query query = getEntityManager().createNativeQuery(queryString, UsuarioHistoricoAcesso.class);
            query.setMaxResults(100);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<UsuarioHistoricoAcesso> listOpenedSession(Integer usuario_id) {
        try {
            String queryString = " "
                    + "     SELECT UHA.*                                        \n"
                    + "       FROM seg_usuario_historico_acesso AS UHA          \n"
                    + "      WHERE id_usuario = " + usuario_id + "              \n"
                    + "        AND dt_logout IS NULL                            \n"
                    + "        AND dt_expired IS NULL                           \n"
                    + "     ORDER BY dt_login DESC                              \n";
            Query query = getEntityManager().createNativeQuery(queryString, UsuarioHistoricoAcesso.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<UsuarioHistoricoAcesso> findResume() {
        try {
            String queryString = " "
                    + "     SELECT UHA.*                                        \n"
                    + "       FROM seg_usuario_historico_acesso AS UHA          \n"
                    + "     ORDER BY dt_login DESC                              \n";
            Query query = getEntityManager().createNativeQuery(queryString, UsuarioHistoricoAcesso.class);
            query.setMaxResults(500);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public void clear() {
        try {
            Query qry = getEntityManager().createNativeQuery("DELETE FROM seg_usuario_historico_acesso WHERE dt_login < current_date - 180");
            getEntityManager().getTransaction().begin();
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
        }
    }

}
