package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.BloqueioRotina;
import java.util.List;
import javax.persistence.Query;

public class BloqueioRotinaDao extends DB {

    public BloqueioRotina existUsuarioRotinaPessoa(int idRotina, int idPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT BR FROM BloqueioRotina AS BR WHERE BR.rotina.id = :idRotina AND BR.pessoa.id = :idPessoa");
            query.setParameter("idRotina", idRotina);
            query.setParameter("idPessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (BloqueioRotina) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public BloqueioRotina existRotinaCodigo(Integer rotina_id, Integer codigo) {
        try {
            Query query = getEntityManager().createQuery("SELECT BR FROM BloqueioRotina AS BR WHERE BR.rotina.id = :rotina_id AND BR.codigo = :codigo");
            query.setParameter("rotina_id", rotina_id);
            query.setParameter("codigo", codigo);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (BloqueioRotina) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean liberaRotinasBloqueadas() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT *                                          "
                    + "   FROM sis_bloqueio_rotina                      "
                    + "  WHERE dt_bloqueio < CURRENT_DATE LIMIT 1       ");
            if (!qry.getResultList().isEmpty()) {
                getEntityManager().getTransaction().begin();
                Query qryUpdateAgendamento = getEntityManager().createNativeQuery(
                        "  DELETE FROM sis_bloqueio_rotina                      "
                        + " WHERE dt_bloqueio < CURRENT_DATE                    ");
                if (qryUpdateAgendamento.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                getEntityManager().getTransaction().commit();
            }
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
        return true;
    }

    public boolean liberaRotinaBloqueada(Integer rotina_id) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT *                                          "
                    + "   FROM sis_bloqueio_rotina                      "
                    + "  WHERE dt_bloqueio < CURRENT_DATE               "
                    + "    AND id_rotina = " + rotina_id+ "  LIMIT 1    "
            );
            if (!qry.getResultList().isEmpty()) {
                getEntityManager().getTransaction().begin();
                Query qryUpdateAgendamento = getEntityManager().createNativeQuery(
                        "  DELETE FROM sis_bloqueio_rotina                      "
                        + " WHERE dt_bloqueio < CURRENT_DATE                    "
                        + "   AND id_rotina = " + rotina_id);
                if (qryUpdateAgendamento.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                getEntityManager().getTransaction().commit();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
