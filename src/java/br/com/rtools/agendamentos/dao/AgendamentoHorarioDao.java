package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.AgendamentoHorario;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AgendamentoHorarioDao extends DB {

    public List<AgendamentoHorario> findBy(Integer agendamento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT AH FROM AgendamentoHorario AH WHERE AH.agendamento.id = :agendamento_id ORDER BY AH.agendaHorarios.hora ASC");
            query.setParameter("agendamento_id", agendamento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public String firstTime(Integer agendamento_id) {
//        try {
//            Query query = getEntityManager().createQuery("SELECT AH.agendaHorarios.hora FROM AgendamentoHorario AH WHERE AH.agendamento.id = :agendamento_id ORDER BY AH.agendaHorarios.hora ASC");
//            query.setParameter("agendamento_id", agendamento_id);
//            query.setMaxResults(1);
//            return query.getResultList();
//        } catch (Exception e) {
//            return new ArrayList();
//        }
        return "";
    }

}
