package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.DescricaoEvento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class DescricaoEventoDao extends DB {

    public List<DescricaoEvento> pesquisaDescricaoPorGrupo(Integer grupo_evento_id) {
        List<DescricaoEvento> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("SELECT DE from DescricaoEvento DE WHERE DE.grupoEvento.id = :grupo_evento_id");
            qry.setParameter("grupo_evento_id", grupo_evento_id);
            result = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public boolean existeDescricaoEvento(DescricaoEvento de) {
        try {
            String queryString = "SELECT DE.* FROM eve_desc_evento AS DE WHERE DE.id_grupo_evento = " + de.getGrupoEvento().getId() + " AND func_translate(UPPER(TRIM(DE.ds_descricao))) LIKE func_translate(UPPER(TRIM('" + de.getDescricao() + "')))";
            Query query = getEntityManager().createNativeQuery(queryString, DescricaoEvento.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
