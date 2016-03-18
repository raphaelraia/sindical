package br.com.rtools.associativo.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CupomCategoriaDao extends DB {

    public List find(Integer cupom_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CC FROM CupomCategoria AS CC WHERE CC.cupom.id = :cupom_id ORDER BY CC.categoria.categoria ASC");
            query.setParameter("cupom_id", cupom_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
