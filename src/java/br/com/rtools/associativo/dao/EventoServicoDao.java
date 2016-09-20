package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.EventoServico;
import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class EventoServicoDao extends DB {

    public List listaEventoServico(int idAEvento) {
        try {
            Query qry = getEntityManager().createQuery("select es "
                    + "  from EventoServico es"
                    + " where es.evento.id = " + idAEvento);
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public EventoServico pesquisaPorEventoEServico(int idAEvento, int idServico) {
        try {
            Query qry = getEntityManager().createQuery("select es "
                    + "  from EventoServico es"
                    + " where es.evento.id = " + idAEvento
                    + "   and es.servicos.id = " + idServico);
            return (EventoServico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
