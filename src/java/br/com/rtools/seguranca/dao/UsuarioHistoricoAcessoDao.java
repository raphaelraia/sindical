package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.UsuarioHistoricoAcesso;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class UsuarioHistoricoAcessoDao extends DB {

    public UsuarioHistoricoAcesso find(Integer usuario_id) {
        try {
            String queryString = " "
                    + "     SELECT UHA.*                                        \n"
                    + "       FROM seg_usuario_historico_acesso AS UHA          \n"
                    + "      WHERE id IN (                                      \n"
                    + "            SELECT (max(id))                             \n"
                    + "              FROM seg_usuario_historico_acesso          \n"
                    + "             WHERE id_usuario = " + usuario_id + "       \n"
                    + "               AND ds_es = 'E'                           \n"
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
                    + "        AND ds_es = 'E'                                  \n"
                    + "     ORDER BY dt_data DESC, ds_es DESC                   \n";
            Query query = getEntityManager().createNativeQuery(queryString, UsuarioHistoricoAcesso.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
