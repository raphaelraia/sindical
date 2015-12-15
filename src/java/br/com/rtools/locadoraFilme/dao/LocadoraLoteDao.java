package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.locadoraFilme.LocadoraLote;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class LocadoraLoteDao extends DB {

    /**
     * Verifica se a pessoa já fez alguma locação.
     *
     * @param pessoa_id
     * @return
     */
    public Boolean exists(Integer pessoa_id) {
        try {
            String queryString = "SELECT * FROM loc_lote AS LT WHERE LT.id_pessoa = " + pessoa_id + " LIMIT 1";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            return list.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List findAllByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT LT FROM LocadoraLote AS LT WHERE LT.pessoa.id = :pessoa_id ORDER BY LT.dtLocacao ASC");
            query.setParameter("pessoa_id", pessoa_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public LocadoraLote findByPessoa(Integer pessoa_id, Boolean current_date) {
        try {
            String queryString = "SELECT LT FROM LocadoraLote AS LT WHERE LT.pessoa.id = :pessoa_id AND LT.dtLocacao = '" + DataHoje.data() + "' ORDER BY LT.dtLocacao ASC";
            Query query = getEntityManager().createQuery(queryString);
            query.setParameter("pessoa_id", pessoa_id);
            return (LocadoraLote) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findAllByPessoa(String data_locacao, Integer pessoa_id) {
        try {
            String queryString = "SELECT LT FROM LocadoraLote AS LT WHERE LT.pessoa.id = " + pessoa_id + " AND LT.dtLocacao = '" + data_locacao + "' BY LT.dtLocacao ASC";
            Query query = getEntityManager().createQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
