package br.com.rtools.estoque.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ProdutoSubGrupoDao extends DB {

    public List findByGrupo(Integer grupo_id) {
        return findByGrupo("" + grupo_id);
    }

    public List findByGrupo(String in_grupos) {
        if (in_grupos.isEmpty()) {
            return new ArrayList();
        }
        try {
            String queryString = "SELECT PSG FROM ProdutoSubGrupo AS PSG WHERE PSG.produtoGrupo.id IN( " + in_grupos + " ) ORDER BY PSG.descricao ASC ";
            Query query = getEntityManager().createQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

}
