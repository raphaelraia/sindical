package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisAutorizacoesDao extends DB {

    public List<SisAutorizacoes> findAll() {
        return findAll("aberto");
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

    public List<SisAutorizacoes> findAll(String tcase) {
        try {
            Query query = null;
            switch (tcase) {
                case "autorizado":
                    query = getEntityManager().createQuery("SELECT SA FROM SisAutorizacoes SA WHERE SA.dtAutorizacao IS NOT NULL AND SA.autorizado = true ORDER BY SA.dtSolicitacao DESC");
                    break;
                case "recusado":
                    query = getEntityManager().createQuery("SELECT SA FROM SisAutorizacoes SA WHERE SA.dtAutorizacao IS NOT NULL AND SA.autorizado = false ORDER BY SA.dtSolicitacao DESC");
                    break;
                case "aberto":
                    query = getEntityManager().createQuery("SELECT SA FROM SisAutorizacoes SA WHERE SA.dtAutorizacao IS NULL AND SA.autorizado = false ORDER BY SA.dtSolicitacao DESC");
                    break;
                default:
                    break;
            }
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
