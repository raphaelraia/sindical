package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.EventoBanda;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EventoBandaDao extends DB {

    public List<EventoBanda> pesquisaBandasDoEvento(Integer idEvento) {
        List<EventoBanda> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "select ev "
                    + "  from EventoBanda ev"
                    + " where ev.evento.id = " + idEvento);
            lista = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            lista = new ArrayList<>();
        }
        return lista;
    }
}
