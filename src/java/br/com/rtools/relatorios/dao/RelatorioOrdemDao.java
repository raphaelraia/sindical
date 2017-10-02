package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioOrdemDao extends DB {

    public List findAllByRelatorio(Integer relatorio) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT RO.* FROM sis_relatorio_ordem AS RO WHERE RO.id_relatorio = " + relatorio + " ORDER BY RO.ds_descricao ASC", RelatorioOrdem.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public Boolean defineDefault(RelatorioOrdem ro) {
        try {
            getEntityManager().getTransaction().begin();
            Query query = getEntityManager().createNativeQuery("UPDATE sis_relatorio_ordem SET is_default = false WHERE id_relatorio = " + ro.getRelatorios().getId());
            if (query.executeUpdate() == 0) {
                getEntityManager().getTransaction().rollback();
                return false;
            }
            query = getEntityManager().createNativeQuery("UPDATE sis_relatorio_ordem SET is_default = true WHERE id = " + ro.getId());
            if (query.executeUpdate() == 0) {
                getEntityManager().getTransaction().rollback();
                return false;
            }
            getEntityManager().getTransaction().commit();
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
        return true;
    }

    public RelatorioOrdem findDefaultByRelatorio(Integer relatorio_id, Boolean principal) {
        try {
            Query query = getEntityManager().createQuery("SELECT RO FROM RelatorioOrdem AS RO WHERE RO.relatorios.id = :relatorio_id AND RO.principal = true");
            query.setParameter("relatorio_id", relatorio_id);
            query.setParameter("principal", principal);
            query.setMaxResults(1);
            return (RelatorioOrdem) query.getSingleResult();
        } catch (Exception e) {
        }
        return null;
    }

}
