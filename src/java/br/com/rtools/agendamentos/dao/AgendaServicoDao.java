package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.AgendaServico;
import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class AgendaServicoDao extends DB {

    public Boolean existByAgendaServico(Integer servico_id, Boolean web) {
        try {
            Query query;
            if (web == null) {
                query = getEntityManager().createQuery("SELECT ASE FROM AgendaServico ASE WHERE ASE.servico.id = :servico_id");
            } else {
                query = getEntityManager().createQuery("SELECT ASE FROM AgendaServico ASE WHERE ASE.servico.id = :servico_id AND ASE.web = :web");
                query.setParameter("web", web);
            }
            query.setParameter("servico_id", servico_id);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public AgendaServico findByAgendaServico(Integer servico_id, Boolean web) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AgendaServico ASE WHERE ASE.servico.id = :servico_id AND ASE.web = :web");
            query.setParameter("servico_id", servico_id);
            query.setParameter("web", web);
            return (AgendaServico) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<AgendaServico> findByServico(Integer servico_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ASE FROM AgendaServico ASE WHERE ASE.servico.id = :servico_id ORDER BY ASE.web ASC");
            query.setParameter("servico_id", servico_id);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
