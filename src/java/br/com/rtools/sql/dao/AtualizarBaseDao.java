package br.com.rtools.sql.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sql.AtualizarBase;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AtualizarBaseDao extends DB {

    public List<AtualizarBase> find() {
        try {
            Query query = getEntityManager().createQuery("SELECT AB FROM AtualizarBase AS AB ORDER BY AB.dtCadastro DESC");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

}
