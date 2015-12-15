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

    public List pesquisaHistoricoPorPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT LM FROM LocadoraMovimento LM WHERE LM.dtDevolucao IS NOT NULL AND LM.locadoraLote.pessoa.id = :pessoa_id ORDER BY LM.dtDevolucao DESC");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findAllByPessoa(String data_locacao, Integer pessoa_id) {
        try {
            String queryString = "SELECT LM FROM LocadoraMovimento AS LM WHERE LM.locadoraLote.pessoa.id = " + pessoa_id + " AND LM.locadoraLote.dtLocacao = '" + data_locacao + "' ORDER BY LM.locadoraLote.dtLocacao ASC";
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
