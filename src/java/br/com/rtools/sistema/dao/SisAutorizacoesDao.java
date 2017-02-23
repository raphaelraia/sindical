package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisAutorizacoesDao extends DB {

    public List<SisAutorizacoes> findAll() {
        return findAll(-1, "aberto", "", "", "");
    }

    public Boolean exists(SisAutorizacoes sa) {
        return !find(1, null, null, null, sa).isEmpty();
    }

    public Boolean exists(Integer tcase, SisAutorizacoes sa) {
        return !find(tcase, null, null, null, sa).isEmpty();
    }

    public SisAutorizacoes findAutorizado(Integer tcase, Integer operador_id, Integer pessoa_id, Integer rotina_destino_id) {
        List<SisAutorizacoes> list = find(tcase, operador_id, pessoa_id, rotina_destino_id, null);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public List find(Integer tcase, Integer operador_id, Integer pessoa_id, Integer rotina_destino_id, SisAutorizacoes sa) {
        try {
            String queryString = "";
            if (null != tcase) {
                switch (tcase) {
                    case 1:
                        queryString = ""
                                + "     SELECT SA.*                                         \n"
                                + "       FROM sis_autorizacoes AS SA                       \n"
                                + "      WHERE SA.id_operador = " + sa.getOperador().getId() + " \n"
                                + "        AND SA.ds_tabela = '" + sa.getTabela() + "'      \n"
                                + "        AND SA.id_tipo_autorizacao = 1                   \n"
                                + "        AND SA.ds_coluna = '" + sa.getColuna() + "'      \n"
                                + "        AND SA.ds_dados_originais = '" + sa.getDadosOriginais() + "' \n"
                                + "        AND SA.ds_dados_alterados = '" + sa.getDadosAlterados() + "' \n"
                                + "        AND SA.id_pessoa = " + sa.getPessoa().getId() + "\n"
                                + "        AND SA.is_autorizado = false                     \n"
                                + "        AND SA.dt_autorizacao IS NULL                    \n";
                        break;
                    case 2:
                        queryString = ""
                                + "     SELECT SA.*                                         \n"
                                + "       FROM sis_autorizacoes AS SA                       \n"
                                + "      WHERE SA.id_operador = " + sa.getOperador().getId() + " \n"
                                + "        AND SA.id_tipo_autorizacao = 2                   \n"
                                + "        AND SA.ds_motivo_solicitacao  = '" + sa.getMotivoSolicitacao() + "' \n"
                                + "        AND SA.id_rotina_destino  = " + sa.getRotinaDestino().getId() + " \n"
                                + "        AND SA.id_pessoa = " + sa.getPessoa().getId() + "\n"
                                + "        AND SA.is_autorizado = false                     \n"
                                + "        AND SA.dt_autorizacao IS NULL                    \n"
                                + "        AND SA.dt_concluido IS NULL                      \n";
                        break;
                    case 3:
                        queryString = ""
                                + "     SELECT SA.* "
                                + "       FROM sis_autorizacoes SA"
                                + "      WHERE SA.id IN (\n"
                                + "     SELECT MAX(SA.id)                                       \n"
                                + "       FROM sis_autorizacoes AS SA                           \n"
                                + "      WHERE SA.id_operador = " + operador_id + "             \n"
                                + "        AND SA.id_tipo_autorizacao = 2                       \n"
                                + "        AND SA.id_rotina_destino  = " + rotina_destino_id + "\n"
                                + "        AND SA.id_pessoa = " + pessoa_id + "                 \n"
                                + "        AND SA.dt_autorizacao = CURRENT_DATE                 \n"
                                + "   )"
                                + "    ORDER BY SA.id DESC                                       \n";
                        break;
                    default:
                        break;
                }
            }
            Query query = getEntityManager().createNativeQuery(queryString, SisAutorizacoes.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<SisAutorizacoes> findAll(Integer tipo_autorizacao_id, String status, String filter, String value, String value2) {
        try {
            String queryString = "SELECT SA.*                                   \n"
                    + "             FROM sis_autorizacoes SA                    \n"
                    + "       INNER JOIN pes_pessoa P ON P.id = SA.id_pessoa    \n"
                    + "            WHERE                                        \n";
            Query query = null;
            switch (status) {
                case "autorizado":
                    queryString += "SA.dt_autorizacao IS NOT NULL               \n"
                            + "     AND SA.is_autorizado = true                 \n";
                    break;
                case "recusado":
                    queryString += " SA.dt_autorizacao IS NOT NULL              \n"
                            + "      AND SA.is_autorizado = false               \n";
                    break;
                case "aberto":
                    queryString += " SA.dt_autorizacao IS NULL                  \n"
                            + "      AND SA.is_autorizado = false               \n";
                    break;
                default:
                    break;
            }
            if (value != null) {
                switch (filter) {
                    case "rotina":
                        queryString += " AND SA.id_rotina = " + value + "           \n";
                        break;
                    case "pessoa":
                        queryString += " AND TRIM(UPPER(func_translate(P.ds_nome))) LIKE TRIM(UPPER(func_translate('%" + value + "%')))       \n";
                        break;
                    case "operador":
                        queryString += " AND SA.id_operador = " + value + "         \n";
                        break;
                    case "gestor":
                        queryString += " AND SA.id_gestor = " + value + "           \n";
                        break;
                    case "data":
                        queryString += " AND SA.dt_solicitacao = '" + value + "'    \n";
                        break;
                }
            }
            if (tipo_autorizacao_id != -1) {
                queryString += " AND SA.id_tipo_autorizacao = " + tipo_autorizacao_id;
            }
            queryString += " AND SA.id_autorizacao IS NULL                          \n";
            queryString += " ORDER BY SA.dt_solicitacao DESC ";
            query = getEntityManager().createNativeQuery(queryString, SisAutorizacoes.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<SisAutorizacoes> findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SA FROM SisAutorizacoes SA WHERE SA.pessoa.id = :pessoa_id AND ((SA.dtAutorizacao IS NULL AND SA.autorizado = false) OR (SA.dtAutorizacao IS NOT NULL AND SA.autorizado = false)) ORDER BY SA.dtSolicitacao DESC");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List<SisAutorizacoes> findByPessoa(Integer pessoa_id, Boolean all) {
        try {
            Query query = getEntityManager().createQuery("SELECT SA FROM SisAutorizacoes SA WHERE SA.pessoa.id = :pessoa_id ORDER BY SA.dtSolicitacao DESC");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Boolean execute(Dao dao, SisAutorizacoes sa) {
        if (sa.getColuna() == null && sa.getCodigo() == null && sa.getTabela() == null) {
            return true;
        }
        try {
            String asColumn = "";
            try {
                Integer.parseInt(sa.getDadosAlterados());
                asColumn = "" + sa.getDadosAlterados() + "";
            } catch (Exception e) {
                asColumn = "'" + sa.getDadosAlterados() + "'";
            }
            String queryString = "UPDATE " + sa.getTabela() + " SET " + sa.getColuna() + " = " + asColumn + " WHERE id = " + sa.getCodigo() + "";
            Query query = dao.getEntityManager().createNativeQuery(queryString);
            query.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<SisAutorizacoes> findByAutorizacao(Integer autorizacao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SA FROM SisAutorizacoes AS SA WHERE SA.autorizacoes.id = :autorizacao_id");
            query.setParameter("autorizacao_id", autorizacao_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
