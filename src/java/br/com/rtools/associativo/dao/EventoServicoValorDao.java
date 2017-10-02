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
            Query query = getEntityManager().createQuery("SELECT EVS FROM EventoServicoValor EVS WHERE EVS.eventoServico.evento.id = :evento_id ORDER BY EVS.eventoServico.servicos.descricao, EVS.eventoServico.categoria.categoria");
            query.setParameter("evento_id", evento_id);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List<EventoServicoValor> listaServicoValorPorEvento(Integer idEvento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT esv.* \n "
                    + "  FROM eve_evento_servico_valor esv \n "
                    + " INNER JOIN eve_evento_servico es ON es.id = esv.id_evento_servico \n "
                    + " INNER JOIN eve_evento e ON e.id = es.id_evento \n "
                    + "  LEFT JOIN soc_categoria c ON c.id = es.id_categoria \n "
                    + " INNER JOIN fin_servicos s ON s.id = es.id_servicos \n "
                    + " WHERE e.id = " + idEvento + " \n "
                    + " ORDER BY s.ds_descricao, c.ds_categoria", EventoServicoValor.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public EventoServicoValor findByEventoCategoria(Integer evento_servico_id, Integer categoria_id) {
        return findByEventoCategoria(evento_servico_id, categoria_id, null);
    }

    public EventoServicoValor findByEventoCategoria(Integer evento_servico_id, Integer categoria_id, Boolean responsavel) {
        try {
            Query query;
            if (responsavel == null) {
                if (categoria_id == null) {
                    query = getEntityManager().createQuery("SELECT ESV FROM EventoServicoValor ESV WHERE ESV.eventoServico.id = :evento_servico_id");
                } else {
                    query = getEntityManager().createQuery("SELECT ESV FROM EventoServicoValor ESV WHERE ESV.eventoServico.id = :evento_servico_id AND ESV.eventoServico.categoria.id = :categoria_id");
                    query.setParameter("categoria_id", categoria_id);
                }
            } else {
                if (categoria_id == null) {
                    query = getEntityManager().createQuery("SELECT ESV FROM EventoServicoValor ESV WHERE ESV.eventoServico.id = :evento_servico_id AND ESV.eventoServico.responsavel = :responsavel");
                } else {
                    query = getEntityManager().createQuery("SELECT ESV FROM EventoServicoValor ESV WHERE ESV.eventoServico.id = :evento_servico_id AND ESV.eventoServico.categoria.id = :categoria_id AND ESV.eventoServico.responsavel = :responsavel");
                }
                query.setParameter("responsavel", responsavel);
            }
            query.setParameter("evento_servico_id", evento_servico_id);
            return (EventoServicoValor) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
