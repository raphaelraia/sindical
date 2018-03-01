package br.com.rtools.sql.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sql.AtualizarBaseCliente;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AtualizarBaseClienteDao extends DB {

    public List<AtualizarBaseCliente> find(Integer atualizar_base_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ABC FROM AtualizarBaseCliente AS ABC WHERE ABC.atualizarBase.id = :atualizar_base_id ORDER BY ABC.cliente.identifica ASC");
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
