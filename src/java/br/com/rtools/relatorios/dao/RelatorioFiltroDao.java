package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioFiltroDao extends DB {

    public List findByRotina(Integer rotina_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT RF FROM RelatorioFiltro AS RF WHERE RF.rotina.id = :rotina_id ORDER BY RF.relatorioFiltroGrupo.id, RF.ordem");
            query.setParameter("rotina_id", rotina_id);
            return query.getResultList();
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findBy(Integer rotina_id, Integer grupo_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT RF FROM RelatorioFiltro AS RF WHERE RF.rotina.id = :rotina_id AND RF.relatorioFiltroGrupo.id = :grupo_id ORDER BY RF.relatorioFiltroGrupo.id, RF.ordem");
            if (grupo_id == null) {
                query = getEntityManager().createQuery("SELECT RF FROM RelatorioFiltro AS RF WHERE RF.rotina.id = :rotina_id AND RF.relatorioFiltroGrupo IS NULL ORDER BY RF.relatorioFiltroGrupo.id, RF.ordem");
            } else {
                query.setParameter("grupo_id", grupo_id);                
            }
            query.setParameter("rotina_id", rotina_id);
            return query.getResultList();
        } catch (Exception e) {
        }
        return new ArrayList();
    }

}
