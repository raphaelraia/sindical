package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Convenio;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class ConvenioDao extends DB {

    public List listaTodos(boolean orderPessoa, boolean orderGrupoConvenio, boolean orderSubGrupoConvenio) {
        return listaTodosPorPessoa(orderPessoa, orderGrupoConvenio, orderSubGrupoConvenio, null);
    }

    public List listaTodosPorPessoa(boolean orderPessoa, boolean orderGrupoConvenio, boolean orderSubGrupoConvenio, Convenio convenio) {
        String where = "";
        String order_by = "";

        if (orderPessoa) {
            order_by = " ORDER BY c.juridica.pessoa.nome ";
        }

        if (orderGrupoConvenio) {
            if (order_by.isEmpty()) {
                order_by = " ORDER BY c.subGrupoConvenio.grupoConvenio.descricao ";
            } else {
                order_by += ", c.subGrupoConvenio.grupoConvenio.descricao ";
            }
        }

        if (orderSubGrupoConvenio) {
            if (order_by.isEmpty()) {
                order_by = " ORDER BY c.subGrupoConvenio.descricao ";
            } else {
                order_by += ", c.subGrupoConvenio.descricao ";
            }
        }

        if (convenio != null) {
            where = " WHERE C.juridica.id = " + convenio.getJuridica().getId();
        }
        try {
            Query query = getEntityManager().createQuery(
                    " SELECT c "
                    + "   FROM Convenio c "
                    + where
                    + order_by);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public boolean existeSubGrupoEmpresa(Convenio convenio) {
        try {
            Query query = getEntityManager().createQuery(" SELECT C FROM Convenio AS C WHERE C.juridica.id = :idJuridica AND C.subGrupoConvenio.id = :idSubGrupoConvenio");
            query.setParameter("idJuridica", convenio.getJuridica().getId());
            query.setParameter("idSubGrupoConvenio", convenio.getSubGrupoConvenio().getId());
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public List pesquisaConvenioPorGrupoPessoa(int idPessoaJuridica, int idGrupoConvenio) {
        List lista = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select c"
                    + "  from Convenio c"
                    + " where c.juridica.pessoa.id = :idPessoa"
                    + "   and c.grupoConvenio.id = :idGrupo");
            qry.setParameter("idPessoa", idPessoaJuridica);
            qry.setParameter("idGrupo", idGrupoConvenio);
            lista = qry.getResultList();
            if (lista == null) {
                lista = new ArrayList();
            }
            return lista;
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List pesquisaJuridicaPorGrupoESubGrupo(int idSubGrupoConvenio, int idGrupo) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select c.juridica"
                    + "  from Convenio c"
                    + " where c.subGrupoConvenio.id = " + idSubGrupoConvenio
                    + "   and c.subGrupoConvenio.grupoConvenio.id = " + idGrupo);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List<Convenio> findByJuridica(Integer juridica_id) {
        try {
            Query query = getEntityManager().createQuery(" SELECT C FROM Convenio AS C WHERE C.juridica.id = :juridica_id ORDER BY C.subGrupoConvenio.grupoConvenio.descricao, C.subGrupoConvenio.descricao");
            query.setParameter("juridica_id", juridica_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    // PARA AGENDA DE HORÁRIOS
    public List<Pessoa> findAllBySubGrupoConvenio(Integer subgrupo_convenio_id) {
        String queryString = "   "
                + "     SELECT P.*                                              \n"
                + "       FROM pes_pessoa   AS P                                 \n"
                + " INNER JOIN pes_juridica AS J ON J.id_pessoa = P.id           \n"
                + "      WHERE J.id IN (                                         \n"
                + "                     SELECT C.id_juridica FROM soc_convenio AS C \n"
                + "                 INNER JOIN soc_convenio_servico AS CS ON CS.id_convenio_sub_grupo = C.id_convenio_sub_grupo \n"
                + "                      WHERE CS.id_convenio_sub_grupo = " + subgrupo_convenio_id + " \n"
                + "                        AND CS.is_agendamento = true          \n"
                + "     )                                                       \n"
                + "   ORDER BY P.ds_nome ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    // PARA AGENDA DE HORÁRIOS
    public List<Pessoa> findAllBySubGrupoConvenio(Integer subgrupo_convenio_id, Integer servico_id, Date date, Boolean web, Boolean socio, String hora) {
        String queryString = ""
                + "     SELECT P.*                                              \n"
                + "       FROM pes_pessoa   AS P                                 \n"
                + " INNER JOIN pes_juridica AS J ON J.id_pessoa = P.id           \n"
                + "      WHERE J.id IN (                                         \n"
                + "                     SELECT C.id_juridica FROM soc_convenio AS C \n"
                + "                 INNER JOIN soc_convenio_servico AS CS ON CS.id_convenio_sub_grupo = C.id_convenio_sub_grupo \n"
                + "                      WHERE CS.id_convenio_sub_grupo = " + subgrupo_convenio_id + " \n";
        if (servico_id != null) {
            queryString += " CS.id_servico = " + servico_id;
        }
        queryString += " AND CS.is_agendamento = true          \n"
                + "     )                                                       \n";
        queryString += " AND P.id IN (   \n"
                + "         SELECT AH.id_convenio\n"
                + "           FROM ag_horarios AH \n"
                + "          WHERE AH.ativo = true\n"
                + "            AND AH.id_convenio_sub_grupo = " + subgrupo_convenio_id + " \n";
        if (!socio) {
            queryString += " AND AH.is_socio = false   \n";
        }
        if (web) {
            queryString += " AND AH.is_web = false \n";
        }
        if (date != null) {
            queryString += " AND AH.id_semana = " + DataHoje.diaDaSemana(date) + "\n";
            queryString += " AND (func_horarios_disponiveis_agendamento(AH.id, date('" + DataHoje.converteData(date) + "'::date)) > 0 OR AH.is_encaixe = true) \n";
        }
        if (!hora.isEmpty()) {
            queryString += " AND AH.ds_hora = '" + hora + "' \n";
        }
        queryString += " GROUP BY AH.id_convenio \n"
                + "	)";
        queryString += "   ORDER BY P.ds_nome ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    // PARA AGENDA DE HORÁRIOS COM HORÁRIOS
    public List<Pessoa> findAllBySubGrupoConvenioAvailable(Integer subgrupo_convenio_id) {
        String queryString = "   "
                + "     SELECT P.*                                              \n"
                + "       FROM pes_pessoa   AS P                                 \n"
                + " INNER JOIN pes_juridica AS J ON J.id_pessoa = P.id           \n"
                + "      WHERE J.id IN (                                         \n"
                + "                     SELECT C.id_juridica FROM soc_convenio AS C \n"
                + "                 INNER JOIN soc_convenio_servico AS CS ON CS.id_convenio_sub_grupo = C.id_convenio_sub_grupo \n"
                + "                      WHERE CS.id_convenio_sub_grupo = " + subgrupo_convenio_id + " \n"
                + "                        AND CS.is_agendamento = true          \n"
                + "     )                                                       \n"
                + "   ORDER BY P.ds_nome ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();

        }
    }

    public Convenio find(Integer convenio_subgrupo_id, Integer juridica_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Convenio C WHERE C.juridica.id = :juridica_id AND C.subGrupoConvenio.id = :convenio_subgrupo_id");
            query.setParameter("juridica_id", juridica_id);
            query.setParameter("convenio_subgrupo_id", convenio_subgrupo_id);
            return (Convenio) (query.getSingleResult());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
