package br.com.rtools.associativo.db;

import br.com.rtools.associativo.CVenda;
import br.com.rtools.associativo.Reservas;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class VendasCaravanaDBToplink extends DB implements VendasCaravanaDB {

    @Override
    public CVenda pesquisaCodigo(int id) {
        CVenda result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("CVenda.pesquisaID");
            qry.setParameter("pid", id);
            result = (CVenda) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    @Override
    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select c from CVenda c");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public int qntReservas(int idEvento, int idGrupoEvento) {
        int qnt = -1;
        try {
            Query qry = getEntityManager().createQuery("select count(res) "
                    + "  from Reservas res"
                    + " where res.cVenda.aEvento.id =" + idEvento
                    + "   and res.cVenda.aEvento.descricaoEvento.grupoEvento.id = " + idGrupoEvento);
            qnt = Integer.parseInt(String.valueOf((Long) qry.getSingleResult()));
            return qnt;
        } catch (EJBQLException e) {
            e.getMessage();
            return qnt;
        }
    }

    @Override
    public List<Integer> listaPoltronasUsadas(int idEvento) {
        List<Integer> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select res.poltrona "
                    + "  from Reservas res"
                    + " where res.cVenda.aEvento.id =" + idEvento
                    + " order by res.poltrona");
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    @Override
    public List<Reservas> listaReservasVenda(int idVenda) {
        List<Reservas> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select res"
                    + "  from Reservas res"
                    + " where res.cVenda.id = " + idVenda);
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    public List<Reservas> listaReservasVendaPessoa(int idVenda, int idPessoa) {
        List<Reservas> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select res"
                    + "  from Reservas res"
                    + " where res.cVenda.id = " + idVenda
                    + "   and res.pessoa.id = " + idPessoa);
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    @Override
    public List<Movimento> listaMovCaravana(int idResponsavel, int idEvt) {
        List<Movimento> list = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select mov"
                                                    + "  from Movimento mov"
                                                    + " where mov.pessoa.id = " + idResponsavel
                                                    + "   and mov.lote.evt.id = " + idEvt
                                                    + " order by mov.dtVencimento");
            list = qry.getResultList();
            return list;
        } catch (EJBQLException e) {
            e.getMessage();
            return list;
        }
    }

    @Override
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
    
    @Override
    public List<Object> listaTipoAgrupado(Integer id_venda) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT es.ds_descricao descricao, \n" +
                    "       count(r.id_pessoa) quantidade_pessoas,\n" +
                    "       0 valor \n" +
                    "  FROM car_reservas r\n" +
                    " INNER JOIN eve_evento_servico es ON es.id = r.id_evento_servico\n" +
                    " WHERE r.id_cvenda = "+id_venda+"\n" +
                    " GROUP BY es.ds_descricao\n" +
                    " ORDER BY es.ds_descricao"
            );
            return  qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }
}
