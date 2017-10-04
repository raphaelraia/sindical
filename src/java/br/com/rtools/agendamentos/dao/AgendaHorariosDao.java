package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
// https://bitbucket.org/brunovschettini/atentools

public class AgendaHorariosDao extends DB {

    public List<AgendaHorarios> findBy(Integer filial_id, Integer semana_id, Integer subgrupo_convenio_id, Integer convenio_id) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT AH FROM AgendaHorarios AH WHERE AH.filial.id = :filial_id AND AH.semana.id = :semana_id AND AH.subGrupoConvenio.id = :subgrupo_convenio_id AND AH.convenio.id = :convenio_id ORDER BY AH.hora");
            qry.setParameter("filial_id", filial_id);
            qry.setParameter("semana_id", semana_id);
            qry.setParameter("subgrupo_convenio_id", subgrupo_convenio_id);
            qry.setParameter("convenio_id", convenio_id);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByFilial(Integer filial_id, String horario, Integer semana_id) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT ah FROM AgendaHorarios ah "
                    + "  WHERE ah.hora = '" + horario + "'"
                    + "    AND ah.semana.id = '" + semana_id + "'"
                    + "    AND ah.filial.id = " + filial_id);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByFilial(Integer filial_id, String horario, Integer semana_id, Integer subgrupo_convenio_id, Integer convenio_id) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT AH FROM AgendaHorarios AH "
                    + "  WHERE AH.hora = '" + horario + "'"
                    + "    AND AH.semana.id = '" + semana_id + "'"
                    + "    AND AH.filial.id = " + filial_id
                    + "    AND AH.subGrupoConvenio.id = " + subgrupo_convenio_id
                    + "    AND AH.convenio.id = " + convenio_id
            );
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaHorariosAgrupadosPorFilialSemana(Integer filial_id, Integer semana_id) {
        try {
            Query query;
            if (semana_id == null) {
                query = getEntityManager().createQuery(" SELECT AH.hora FROM AgendaHorarios AS AH WHERE AH.filial.id = :filial_id GROUP BY AH.hora ORDER BY AH.hora ");
                query.setParameter("filial_id", filial_id);
            } else {
                query = getEntityManager().createQuery(" SELECT AH.hora FROM AgendaHorarios AS AH WHERE AH.filial.id = :filial_id AND AH.semana.id = :semana_id GROUP BY AH.hora ORDER BY AH.hora ");
                query.setParameter("filial_id", filial_id);
                query.setParameter("semana_id", semana_id);
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
    
    public List listaHorariosAgrupadosPorFilialSemana(Integer filial_id, Integer semana_id, Integer subgrupo_convenio_id, Integer convenio_id) {
        try {
            Query query;
            if (semana_id == null) {
                query = getEntityManager().createQuery("SELECT AH.hora FROM AgendaHorarios AS AH WHERE AH.filial.id = :filial_id AND AH.subGrupoConvenio.id = :subgrupo_convenio_id AND AH.convenio.id = :convenio_id GROUP BY AH.hora ORDER BY AH.hora ");
                query.setParameter("filial_id", filial_id);
            } else {
                query = getEntityManager().createQuery("SELECT AH.hora FROM AgendaHorarios AS AH WHERE AH.filial.id = :filial_id AND AH.semana.id = :semana_id AND AH.subGrupoConvenio.id = :subgrupo_convenio_id AND AH.convenio.id = :convenio_id GROUP BY AH.hora ORDER BY AH.hora ");
                query.setParameter("filial_id", filial_id);
            }
            query.setParameter("semana_id", semana_id);
            query.setParameter("subgrupo_convenio_id", subgrupo_convenio_id);
            query.setParameter("convenio_id", convenio_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPorHorarioFilial(Integer filial_id, String horario) {
        try {
            Query qry = getEntityManager().createQuery(
                    "   SELECT H FROM Horarios h "
                    + "  WHERE H.hora = '" + horario + "'"
                    + "    AND H.filial.id = " + filial_id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List<Horarios> listaTodosHorariosDisponiveisPorFilial(int idFilial, Date date, boolean isCancelados) {
        List result = new ArrayList();
        int diaDaSemana;
        String diaSemanaWhere = "";
        String dataWhere = "";
        if (isCancelados == false) {
            diaDaSemana = DataHoje.diaDaSemana(date);
            diaSemanaWhere = " AND h.semana.id = " + diaDaSemana;
        }
        try {
            Query qry = getEntityManager().createQuery("SELECT h FROM Horarios h WHERE " + dataWhere + " h.filial.id = :pfilial" + diaSemanaWhere + " ORDER BY H.hora ASC");
            qry.setParameter("pfilial", idFilial);
            if (!qry.getResultList().isEmpty()) {
                result = (qry.getResultList());
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }
}
