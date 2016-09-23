package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CaravanaVenda;
import br.com.rtools.associativo.CaravanaReservas;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class VendasCaravanaDao extends DB {

    public List<Movimento> listaMovCaravanaBaixado(int idLoteBaixa) {
        List<Movimento> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select mov"
                    + "  from Movimento mov"
                    + " where mov.loteBaixa.id = " + idLoteBaixa);
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    public List<Object> listaTipoAgrupado(Integer id_venda) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT es.ds_descricao descricao, \n"
                    + "       count(r.id_pessoa) quantidade_pessoas,\n"
                    + "       0 valor \n"
                    + "  FROM car_reservas r\n"
                    + " INNER JOIN eve_evento_servico es ON es.id = r.id_evento_servico\n"
                    + " WHERE r.id_caravana_venda = " + id_venda + " AND r.dt_cancelamento IS NULL \n"
                    + " GROUP BY es.ds_descricao\n"
                    + " ORDER BY es.ds_descricao"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

}
