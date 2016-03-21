package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RotinaGrupoDao extends DB {

    public List find(Integer rotina_id) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT RG FROM RotinaGrupo AS RG WHERE RG.rotina.id = :rotina_id ORDER BY RG.grupo.rotina ASC ");
            qry.setParameter("rotina_id", rotina_id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findAll() {
        try {
            Query qry = getEntityManager().createQuery(" SELECT RG.grupo FROM RotinaGrupo AS RG GROUP BY RG.grupo ORDER BY RG.grupo.rotina ASC ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
    
    public List findByGrupo(Integer rotina_grupo_id) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT RG FROM RotinaGrupo AS RG WHERE RG.grupo.id = :rotina_grupo_id ORDER BY RG.rotina.rotina ASC ");
            qry.setParameter("rotina_grupo_id", rotina_grupo_id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
