package br.com.rtools.locadoraFilme.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LocadoraAutorizadosDao extends DB {

    public List findAllByTitular(Integer titular_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT LA FROM LocadoraAutorizados AS LA WHERE LA.titular.id = :titular_id ORDER BY LA.titular.nome ASC, LA.nome ASC ");
            query.setParameter("titular_id", titular_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }
}
