package br.com.rtools.sql.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sql.AtualizarBaseScript;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AtualizarBaseScriptDao extends DB {

    public List<AtualizarBaseScript> find(Integer atualizar_base_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT AB FROM AtualizarBaseScript AS AB WHERE AB.atualizarBase.id = :atualizar_base_id ORDER BY AB.id ASC");
            query.setParameter("atualizar_base_id", atualizar_base_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

}
