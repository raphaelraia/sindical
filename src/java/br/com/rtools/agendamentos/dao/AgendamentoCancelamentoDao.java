package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.AgendamentoCancelamento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AgendamentoCancelamentoDao extends DB {

    public List<AgendamentoCancelamento> findByAgendamento(Integer agendamento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT AC FROM AgendamentoCancelamento AC WHERE AC.agendamentoHorario.agendamento.id = :agendamento_id ORDER BY AC.agendamentoHorario.agendaHorarios.hora ASC");
            query.setParameter("agendamento_id", agendamento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
