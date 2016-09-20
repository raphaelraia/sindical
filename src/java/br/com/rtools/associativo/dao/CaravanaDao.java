package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Caravana;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class CaravanaDao extends DB {

    public Caravana pesquisaCaravanaPorEvento(Integer evento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Caravana AS C WHERE C.evento.id = :evento_id");
            query.setParameter("evento_id", evento_id);
            return (Caravana) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
