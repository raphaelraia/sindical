package br.com.rtools.associativo.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class PoltronasDao extends DB {

    public List<Integer> listaPoltronasUsadas(int idEvento) {
        List<Integer> list = new ArrayList();
        try {
            Query query = getEntityManager().createQuery("SELECT CR.poltrona FROM CaravanaReservas CR WHERE CR.venda.evento.id = :evento_id AND CR.dtCancelamento IS NULL ORDER BY CR.poltrona");
            query.setParameter("evento_id", idEvento);
            list = query.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }
}
