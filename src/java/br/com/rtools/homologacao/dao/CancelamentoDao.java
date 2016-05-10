package br.com.rtools.homologacao.dao;

import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class CancelamentoDao extends DB {

    public Cancelamento findByAgendamento(Integer agendamento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Cancelamento AS C WHERE C.agendamento.id = :agendamento_id ORDER BY C.agendamento.id DESC");
            query.setParameter("agendamento_id", agendamento_id);
            query.setMaxResults(1);
            return (Cancelamento) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
