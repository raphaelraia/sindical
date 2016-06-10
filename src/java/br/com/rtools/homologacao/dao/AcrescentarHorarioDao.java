package br.com.rtools.homologacao.dao;

import br.com.rtools.homologacao.AcrescentarHorario;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class AcrescentarHorarioDao extends DB {

    public List pesquisaTodos(Integer idFilial) {
        try {
            Query qry = getEntityManager().createQuery("SELECT AH FROM AcrescentarHorario AS AH WHERE AH.filial.id = :filial ORDER BY AH.dtData DESC, AH.horarios.hora ASC");
            qry.setParameter("filial", idFilial);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public AcrescentarHorario pesquisaAcrescimoHorario(Date data, Integer idHorario, Integer idFilial) {
        return pesquisaAcrescimoHorarioSemana(data, idHorario, idFilial, null);
    }

    public AcrescentarHorario pesquisaAcrescimoHorarioSemana(Date data, Integer idHorario, Integer idFilial, Integer idSemana) {
        AcrescentarHorario acrescentarHorario = new AcrescentarHorario();
        try {
            Query query;
            if (idSemana != null) {
                if (data == null) {
                    query = getEntityManager().createQuery("SELECT AH FROM AcrescentarHorario AS AH WHERE AH.horarios.id = :horario AND AH.filial.id = :filial AND AH.horarios.semana.id = :semana");
                } else {
                    query = getEntityManager().createQuery("SELECT AH FROM AcrescentarHorario AS AH WHERE  AH.dtData = :data AND AH.horarios.id = :horario AND AH.filial.id = :filial AND AH.horarios.semana.id = :semana");
                    query.setParameter("data", data);
                }
                query.setParameter("semana", idSemana);
                query.setMaxResults(1);
            } else {
                query = getEntityManager().createQuery("SELECT AH FROM AcrescentarHorario AS AH WHERE AH.dtData = :data AND AH.horarios.id = :horario AND AH.filial.id = :filial");
                query.setParameter("data", data);
            }
            query.setParameter("horario", idHorario);
            query.setParameter("filial", idFilial);
            if (!query.getResultList().isEmpty()) {
                acrescentarHorario = (AcrescentarHorario) query.getSingleResult();
            }
        } catch (Exception e) {
            return acrescentarHorario;
        }
        return acrescentarHorario;
    }

    public List<AcrescentarHorario> listaTodosHorariosAcrescentados(Integer idFilial, Date dataInicial, Date dataFinal) {
        return listaTodosHorariosAcrescentados(idFilial, dataInicial, dataFinal, null, null);
    }

    public List<AcrescentarHorario> listaTodosHorariosAcrescentados(Integer idFilial, Date dataInicial, Date dataFinal, String horario) {
        return listaTodosHorariosAcrescentados(idFilial, dataInicial, dataFinal, null, horario);

    }

    public List<AcrescentarHorario> listaTodosHorariosAcrescentados(Integer idFilial, Date dataInicial, Date dataFinal, Integer idSemana) {
        return listaTodosHorariosAcrescentados(idFilial, dataInicial, dataFinal, idSemana, null);
    }

    public List<AcrescentarHorario> listaTodosHorariosAcrescentados(Integer idFilial, Date dataInicial, Date dataFinal, Integer idSemana, String horario) {
        List list = new ArrayList();
        if (dataFinal != null) {
            String queryPeriodo;
            int intDataInicial = DataHoje.converteDataParaInteger(DataHoje.converteData(dataInicial));
            int intDataFinal = DataHoje.converteDataParaInteger(DataHoje.converteData(dataFinal));
            if (intDataFinal < intDataInicial) {
                queryPeriodo = " '" + dataInicial + "' AND '" + dataInicial + "'  ";
            } else {
                queryPeriodo = " '" + dataInicial + "' AND '" + dataFinal + "'  ";
            }
            try {
                String queryString
                        = "     SELECT AH.id                                        "
                        + "       FROM hom_acrescentar_horario AH                      "
                        + " INNER JOIN hom_horarios AS H ON H.id = AH.id_horarios   "
                        + "      WHERE AH.dt_data BETWEEN " + queryPeriodo
                        + "        AND AH.id_filial = " + idFilial;
                if (idSemana != null) {
                    queryString += " AND H.id_semana = " + idSemana;
                }
                if (horario != null) {
                    queryString += " AND H.ds_hora = '" + horario + "'";
                }
                Query qry = getEntityManager().createNativeQuery(queryString);
                if (!qry.getResultList().isEmpty()) {
                    list = qry.getResultList();
                    String queryListaIdPeriodo = "";
                    for (int i = 0; i < list.size(); i++) {
                        String id = ((Integer) ((List) (list).get(i)).get(0)).toString();
                        if (i == 0) {
                            queryListaIdPeriodo = id;
                        } else {
                            queryListaIdPeriodo += ", " + id;
                        }
                    }
                    list.clear();
                    queryString = "SELECT AH FROM AcrescentarHorario AH WHERE AH.id IN (" + queryListaIdPeriodo + ") ORDER BY AH.dtData DESC, AH.horarios.hora ASC";
                    Query qryResultadoPeriodo = getEntityManager().createQuery(queryString);
                    if (!qryResultadoPeriodo.getResultList().isEmpty()) {
                        list = qryResultadoPeriodo.getResultList();
                    }
                }
            } catch (Exception e) {
            }

        } else {
            try {
                Query qry = getEntityManager().createQuery("SELECT AH FROM AcrescentarHorario AH WHERE AH.dtData = :dtData AND AH.filial.id = :idFilial ORDER BY AH.dtData DESC, AH.horarios.hora ASC ");
                qry.setParameter("idFilial", idFilial);
                qry.setParameter("dtData", dataInicial);
                if (!qry.getResultList().isEmpty()) {
                    list = qry.getResultList();
                }
            } catch (Exception e) {
            }
        }
        return list;
    }
}
