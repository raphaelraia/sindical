package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.dao.FindDao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisAutorizacoesDao extends DB {

    public List<SisAutorizacoes> findAll() {
        return findAll("aberto", "", "", "");
    }

    public Boolean exists(SisAutorizacoes sa) {
        try {
            String queryString = ""
                    + "     SELECT SA.*                                         \n"
                    + "       FROM sis_autorizacoes AS SA                       \n"
                    + "      WHERE SA.id_operador = " + sa.getOperador().getId() + " \n"
                    + "        AND SA.ds_tabela = '" + sa.getTabela() + "'      \n"
                    + "        AND SA.ds_coluna = '" + sa.getColuna() + "'      \n"
                    + "        AND SA.ds_dados_originais = '" + sa.getDadosOriginais() + "' \n"
                    + "        AND SA.ds_dados_alterados = '" + sa.getDadosAlterados() + "' \n"
                    + "        AND SA.id_pessoa = " + sa.getPessoa().getId() + "\n"
                    + "        AND SA.is_autorizado = false                     \n"
                    + "        AND SA.dt_autorizacao IS NULL                    \n";
            Query query = getEntityManager().createNativeQuery(queryString);
            return !query.getResultList().isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    public List<SisAutorizacoes> findAll(String status, String filter, String value, String value2) {
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

}
