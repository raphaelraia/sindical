package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LocadoraMovimentoDao extends DB {

    public List pesquisaAtrasadosPorPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT LM FROM LocadoraMovimento LM WHERE LM.locadoraLote.pessoa.id = :pessoa_id AND LM.dtDevolucao > LM.dtDevolucaoPrevisao ORDER BY LM.dtDevolucaoPrevisao DESC");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List pesquisaPendentesPorPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT LM FROM LocadoraMovimento LM WHERE LM.locadoraLote.pessoa.id = :pessoa_id AND LM.dtDevolucao IS NULL ORDER BY LM.dtDevolucaoPrevisao DESC");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List pesquisaHistoricoPorPessoa(Integer pessoa_id, Integer filial_id) {
        return pesquisaHistoricoPorPessoa("todos", pessoa_id, filial_id);
    }

    public List pesquisaHistoricoPorPessoa(String tcase, Integer pessoa_id, Integer filial_id) {
        try {
            Query query = null;
            switch (tcase) {
                case "todos":
                    query = getEntityManager().createQuery("SELECT LM FROM LocadoraMovimento LM WHERE LM.dtDevolucao IS NULL AND LM.locadoraLote.pessoa.id = :pessoa_id AND LM.locadoraLote.filial.id = :filial_id ORDER BY LM.locadoraLote.dtLocacao DESC");
                    break;
                case "devolvidos":
                    query = getEntityManager().createQuery("SELECT LM FROM LocadoraMovimento LM WHERE LM.dtDevolucao IS NOT NULL AND LM.locadoraLote.pessoa.id = :pessoa_id AND LM.locadoraLote.filial.id = :filial_id ORDER BY LM.dtDevolucao DESC");
                    break;
                case "nao_devolvidos":
                    query = getEntityManager().createQuery("SELECT LM FROM LocadoraMovimento LM WHERE LM.dtDevolucao IS NULL AND LM.dtDevolucaoPrevisao IS NOT NULL AND CURRENT_TIMESTAMP > LM.dtDevolucaoPrevisao AND LM.locadoraLote.pessoa.id = :pessoa_id AND LM.locadoraLote.filial.id = :filial_id ORDER BY LM.dtDevolucaoPrevisao DESC");
                    break;
                default:
                    break;
            }
            query.setParameter("pessoa_id", pessoa_id);
            query.setParameter("filial_id", filial_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List findAllByPessoa(String data_locacao, Integer pessoa_id) {
        return findAllByPessoa(data_locacao, pessoa_id, null);
    }

    public List findAllByPessoa(String data_locacao, Integer pessoa_id, Integer filial_id) {
        try {
            String queryString = ""
                    + "     SELECT LM                                           \n"
                    + "       FROM LocadoraMovimento AS LM                      \n"
                    + "      WHERE LM.locadoraLote.pessoa.id = " + pessoa_id + "\n"
                    + "        AND LM.locadoraLote.dtLocacao = '" + data_locacao + "' \n";
            if (filial_id != null) {
                queryString += "LM.locadoraLote.filial.id = " + filial_id + "   \n";
            }
            queryString += " ORDER BY LM.locadoraLote.dtLocacao ASC";
            Query query = getEntityManager().createQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }
}
