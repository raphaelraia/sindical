package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.EventoServicoValor;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EventoServicoValorDao extends DB {

    public EventoServicoValor pesquisaEventoServicoValor(int idEventoServico) {
        try {
            Query qry = getEntityManager().createQuery("select ev "
                    + "  from EventoServicoValor ev"
                    + " where ev.eventoServico.id = " + idEventoServico);
            return (EventoServicoValor) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return new EventoServicoValor();
        }
    }

    public List<EventoServicoValor> pesquisaServicoValorPorEvento(int idEvento) {
        List<EventoServicoValor> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select r from EventoServicoValor r where r.eventoServico.aEvento.id = " + idEvento + " order by r.eventoServico.servicos.descricao, r.eventoServico.categoria.categoria");
            lista = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            lista = new ArrayList();
        }
        return lista;
    }
}
