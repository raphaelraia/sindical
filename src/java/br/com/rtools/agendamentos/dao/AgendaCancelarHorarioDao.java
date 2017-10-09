package br.com.rtools.agendamentos.dao;

import br.com.rtools.agendamentos.AgendaCancelarHorario;
import br.com.rtools.homologacao.CancelarHorario;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TemporalType;

public class AgendaCancelarHorarioDao extends DB {

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Integer subgrupo_convenio_id, Integer convenio_id) {
        try {
            Query qry = getEntityManager().createQuery("SELECT ACH FROM AgendaCancelarHorario AS ACH WHERE ACH.horario.filial.id = :filial_id AND ACH.horario.subGrupoConvenio.id = :subgrupo_convenio_id AND ACH.horario.convenio.id = :convenio_id ORDER BY ACH.dtData DESC, ACH.horario.hora ASC");
            qry.setParameter("subgrupo_convenio_id", subgrupo_convenio_id);
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public AgendaCancelarHorario findBy(Date data, Integer horario_id, Integer filial_id, Integer subgrupo_convenio_id, Integer convenio_id) {
        return findBy(data, horario_id, filial_id, null, subgrupo_convenio_id, convenio_id);
    }

    public AgendaCancelarHorario findBy(Date data, Integer horario_id, Integer filial_id, Integer semana_id, Integer subgrupo_convenio_id, Integer convenio_id) {
        try {
            Query query;
            if (semana_id != null) {
                if (data == null) {
                    query = getEntityManager().createQuery("SELECT ACH FROM AgendaCancelarHorario AS ACH WHERE ACH.horario.id = :horario_id AND ACH.horario.filial.id = :filial_id AND ACH.horario.semana.id = :semana_id AND ACH.horario.subGrupoConvenio.id = :subgrupo_convenio_id AND ACH.horario.convenio.id = :convenio_id");
                } else {
                    query = getEntityManager().createQuery("SELECT ACH FROM AgendaCancelarHorario AS ACH WHERE ACH.horario.id = :horario_id AND ACH.horario.filial.id = :filial_id AND ACH.horario.semana.id = :semana_id AND ACH.horario.subGrupoConvenio.id = :subgrupo_convenio_id AND ACH.horario.convenio.id = :convenio_id AND ACH.dtData = :data");
                    query.setParameter("data", data);
                }
                query.setParameter("semana_id", semana_id);
                query.setMaxResults(1);
            } else {
                if (data == null) {
                    query = getEntityManager().createQuery("SELECT ACH FROM AgendaCancelarHorario AS ACH WHERE ACH.horario.id = :horario_id AND ACH.horario.filial.id = :filial_id AND ACH.horario.subGrupoConvenio.id = :subgrupo_convenio_id AND ACH.horario.convenio.id = :convenio_id");
                } else {
                    query = getEntityManager().createQuery("SELECT ACH FROM AgendaCancelarHorario AS ACH WHERE ACH.horario.id = :horario_id AND ACH.horario.filial.id = :filial_id AND ACH.horario.subGrupoConvenio.id = :subgrupo_convenio_id AND ACH.horario.convenio.id = :convenio_id AND ACH.dtData = :data");
                    query.setParameter("data", data);
                }
            }
            query.setParameter("subgrupo_convenio_id", subgrupo_convenio_id);
            query.setParameter("convenio_id", convenio_id);
            query.setParameter("horario_id", horario_id);
            query.setParameter("filial_id", filial_id);
            return (AgendaCancelarHorario) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final) {
        return findAll(filial_id, data_inicial, data_final, null, null, null, null);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final, String horario) {
        return findAll(filial_id, data_inicial, data_final, null, horario, null, null);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final, String horario, Integer subgrupo_convenio_id) {
        return findAll(filial_id, data_inicial, data_final, null, horario, subgrupo_convenio_id, null);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final, String horario, Integer subgrupo_convenio_id, Integer convenio_id) {
        return findAll(filial_id, data_inicial, data_final, null, horario, subgrupo_convenio_id, convenio_id);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final, Integer semana_id) {
        return findAll(filial_id, data_inicial, data_final, semana_id, null);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final, Integer semana_id, Integer subgrupo_convenio_id) {
        return findAll(filial_id, data_inicial, data_final, semana_id, null, subgrupo_convenio_id, null);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Integer subgrupo_convenio_id, Integer convenio_id) {
        return findAll(filial_id, data_inicial, null, null, null, subgrupo_convenio_id, convenio_id);
    }
    
    public List<AgendaCancelarHorario> findAll2(Integer filial_id, Date data_inicial, Date data_final, Integer subgrupo_convenio_id, Integer convenio_id) {
        return findAll(filial_id, data_inicial, data_final, null, null, subgrupo_convenio_id, convenio_id);
    }

    public List<AgendaCancelarHorario> findAll(Integer filial_id, Date data_inicial, Date data_final, Integer semana_id, String horario, Integer subgrupo_convenio_id, Integer convenio_id) {

        List listWhere = new ArrayList();
        try {
            String queryString
                    = "     SELECT ACH.*                                        \n"
                    + "       FROM ag_cancelar_horario ACH                      \n"
                    + " INNER JOIN ag_horarios AS H ON H.id = ACH.id_horarios    \n";
            if (data_final != null) {
                int di = DataHoje.converteDataParaInteger(DataHoje.converteData(data_inicial));
                int df = DataHoje.converteDataParaInteger(DataHoje.converteData(data_final));
                if (df < di) {
                    listWhere.add("ACH.dt_data = date('" + data_inicial + "')");
                } else {
                    listWhere.add("ACH.dt_data BETWEEN date('" + data_inicial + "') AND ('" + data_final + "')");
                }
            } else {
                listWhere.add("ACH.dt_data = date('" + data_inicial + "')");
            }

            if (filial_id != null) {
                listWhere.add("H.id_filial = " + filial_id);
            }
            if (semana_id != null) {
                listWhere.add("H.id_semana = " + semana_id);
            }
            if (horario != null) {
                listWhere.add("H.ds_hora = '" + horario + "'");
            }
            if (subgrupo_convenio_id != null) {
                listWhere.add("H.id_convenio_sub_grupo = " + subgrupo_convenio_id);
            }
            if (convenio_id != null) {
                listWhere.add("H.id_convenio = " + convenio_id);
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += " ORDER BY ACH.dt_data DESC, H.ds_hora ASC ";
            Query query = getEntityManager().createNativeQuery(queryString, AgendaCancelarHorario.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
