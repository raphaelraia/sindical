package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.EventoServico;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
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

    public EventoServico pesquisaEventoServico(Integer id_servicos, Integer id_categoria, Integer id_evento) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT es.* \n "
                    + "  FROM eve_evento_servico es \n "
                    + " WHERE es.id_servicos = " + id_servicos + " \n "
                    + "   AND es.id_evento = " + id_evento + " \n "
                    + "   AND es.id_categoria " + (id_categoria == null ? " IS NULL" : " = " + id_categoria + " \n "),
                    EventoServico.class
            );
            return (EventoServico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public EventoServico pesquisaEventoServico(Integer id_servicos, Integer id_categoria, Integer id_evento, Boolean responsavel) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT es.* \n "
                    + "  FROM eve_evento_servico es \n "
                    + " WHERE es.id_servicos = " + id_servicos + " \n "
                    + "   AND es.id_evento = " + id_evento + " \n "
                    + "   AND es.id_categoria " + (id_categoria == null ? " IS NULL" : " = " + id_categoria + " \n ")
                    + "   AND es.is_responsavel = " + responsavel,
                    EventoServico.class
            );
            return (EventoServico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public EventoServico pesquisaEventoServico(Integer id_servicos, Integer id_categoria, Integer id_evento, Integer faixaInicial, Integer faixaFinal, String sexo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT es.* \n "
                    + "  FROM eve_evento_servico es \n "
                    + " WHERE es.id_servicos = " + id_servicos + " \n "
                    + "   AND es.id_evento = " + id_evento + " \n "
                    + "   AND es.id_categoria " + (id_categoria == null ? " IS NULL" : " = " + id_categoria + " \n "
                            + "   AND esv.nr_idade_inicial = " + faixaInicial + " \n "
                            + "   AND esv.nr_idade_final = " + faixaFinal + " \n "
                            + "   AND esv.ds_sexo = '" + sexo + "' \n "), EventoServico.class
            );
            return (EventoServico) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List listaCategoriaPorEventoServico(Integer id_servicos, Integer id_evento) {
        try {
            String textqry
                    = " SELECT c.* \n"
                    + "   FROM soc_categoria c \n "
                    + "  WHERE c.id NOT IN ("
                    + "     SELECT es.id_categoria \n "
                    + "       FROM eve_evento_servico es \n "
                    + "      INNER JOIN eve_evento_servico_valor esv ON esv.id_evento_servico = es.id "
                    + "      WHERE es.id_servicos = " + id_servicos
                    + "        AND es.id_categoria IS NOT NULL "
                    + "        AND es.id_evento = " + id_evento
                    + " ) "
                    + " ORDER BY c.ds_categoria";

            Query qry = getEntityManager().createNativeQuery(textqry, Categoria.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List listaCategoriaPorEventoServico(Integer id_servicos, String sexo, Integer faixaInicial, Integer faixaFinal, Integer id_evento) {
        try {
            String textqry
                    = " SELECT c.* \n"
                    + "   FROM soc_categoria c \n "
                    + "  WHERE c.id NOT IN ("
                    + "     SELECT es.id_categoria \n "
                    + "       FROM eve_evento_servico es \n "
                    + "      INNER JOIN eve_evento_servico_valor esv ON esv.id_evento_servico = es.id "
                    + "      WHERE es.id_servicos = " + id_servicos
                    + "        AND es.id_categoria IS NOT NULL "
                    + "        AND es.id_evento = " + id_evento
                    + "        AND esv.nr_idade_inicial = " + faixaInicial
                    + "        AND esv.nr_idade_final = " + faixaFinal
                    + "        AND esv.ds_sexo = '" + sexo + "'"
                    + " ) "
                    + " ORDER BY c.ds_categoria";

            Query qry = getEntityManager().createNativeQuery(textqry, Categoria.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public EventoServico findByEvento(Integer evento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT ES FROM EventoServico ES WHERE ES.evento.id = :evento_id");
            query.setParameter("evento_id", evento_id);
            return (EventoServico) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public EventoServico findByEvento(Integer evento_id, Integer categoria_id) {
        return findByEvento(evento_id, categoria_id, null);
    }

    public EventoServico findByEvento(Integer evento_id, Integer categoria_id, Boolean responsavel) {
        try {
            Query query;
            if (responsavel == null) {
                if (categoria_id == null) {
                    query = getEntityManager().createQuery("SELECT ES FROM EventoServico ES WHERE ES.evento.id = :evento_id AND ES.categoria IS NULL");
                } else {
                    query = getEntityManager().createQuery("SELECT ES FROM EventoServico ES WHERE ES.evento.id = :evento_id AND ES.categoria.id = :categoria_id");
                    query.setParameter("categoria_id", categoria_id);
                }
            } else {
                if (categoria_id == null) {
                    query = getEntityManager().createQuery("SELECT ES FROM EventoServico ES WHERE ES.evento.id = :evento_id AND ES.categoria IS NULL AND ES.responsavel = :responsavel");
                } else {
                    query = getEntityManager().createQuery("SELECT ES FROM EventoServico ES WHERE ES.evento.id = :evento_id AND ES.categoria.id = :categoria_id AND ES.responsavel = :responsavel");
                    query.setParameter("categoria_id", categoria_id);
                }
                query.setParameter("responsavel", responsavel);
            }
            query.setParameter("evento_id", evento_id);
            return (EventoServico) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
