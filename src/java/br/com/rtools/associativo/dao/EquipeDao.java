package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Equipe;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EquipeDao extends DB {

    public Equipe exists(Integer modalidade_id, String descricao) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT E.* FROM eve_equipe E WHERE E.id_modalidade = " + modalidade_id + " AND func_translate(upper(trim(E.ds_descricao))) = func_translate(upper(trim('" + descricao + "')))", Equipe.class);
            return (Equipe) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findByModalidade(Integer modalidade_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT E FROM Equipe E WHERE E.modalidade.id = :modalidade_id ORDER BY E.descricao ASC");
            query.setParameter("modalidade_id", modalidade_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
