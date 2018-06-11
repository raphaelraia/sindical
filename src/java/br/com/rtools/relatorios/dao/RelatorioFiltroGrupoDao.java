package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioFiltroGrupoDao extends DB {

    public List findByRotina(Integer rotina_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT RFD FROM RelatorioFiltroGrupo AS RFD WHERE RFD.rotina.id = :rotina_id ORDER BY RFD.nrOrdem, RFD.descricao");
            query.setParameter("rotina_id", rotina_id);
            return query.getResultList();
        } catch (Exception e) {
        }
        return new ArrayList();
    }

}
