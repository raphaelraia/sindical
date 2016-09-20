package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.EventoServicoValor;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EventoServicoValorDao extends DB {

    public EventoServicoValor pesquisaEventoServicoValor(Integer evento_servico_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT EV FROM EventoServicoValor EV WHERE EV.eventoServico.id = :evento_servico_id");
            query.setParameter("evento_servico_id", evento_servico_id);
            return (EventoServicoValor) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return new EventoServicoValor();
        }
    }

    public List<EventoServicoValor> pesquisaServicoValorPorEvento(Integer evento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM EventoServicoValor R WHERE R.eventoServico.evento.id = :evento_id ORDER BY R.eventoServico.servicos.descricao, R.eventoServico.categoria.categoria");
            query.setParameter("evento_id", evento_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }
}
