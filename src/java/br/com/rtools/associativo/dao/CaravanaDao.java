package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Caravana;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
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

    public Caravana findByCaravana(Integer caravana_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Caravana AS C WHERE C.id = :caravana_id");
            query.setParameter("caravana_id", caravana_id);
            return (Caravana) query.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public List<Caravana> findAll(String tcase) {
        try {
            Query query = null;
            if (null != tcase) {
                switch (tcase) {
                    case "all":
                        query = getEntityManager().createNativeQuery("SELECT C.* FROM car_caravana AS C ORDER BY C.dt_embarque_ida ASC", Caravana.class);
                        break;
                    case "all_desc":
                        query = getEntityManager().createNativeQuery("SELECT C.* FROM car_caravana AS C ORDER BY C.dt_embarque_ida DESC", Caravana.class);
                        break;
                    case "current":
                        query = getEntityManager().createNativeQuery("SELECT C.* FROM car_caravana AS C WHERE C.dt_inativacao IS NULL AND C.dt_embarque_ida >= CURRENT_DATE ORDER BY C.dt_embarque_ida ASC", Caravana.class);
                        break;
                    case "old":
                        query = getEntityManager().createNativeQuery("SELECT C.* FROM car_caravana AS C WHERE (C.dt_embarque_ida < CURRENT_DATE OR C.dt_inativacao IS NOT NULL) ORDER BY C.dt_embarque_ida DESC", Caravana.class);
                        break;
                    default:
                        break;
                }
            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
