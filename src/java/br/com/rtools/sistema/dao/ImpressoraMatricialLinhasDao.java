package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ImpressoraMatricialLinhas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ImpressoraMatricialLinhasDao extends DB {

    public List<ImpressoraMatricialLinhas> findByImpressoraMatricial(Integer impressora_matricial_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT L FROM ImpressoraMatricialLinhas L WHERE L.impressao.id = :impressora_matricial_id ORDER BY L.id ASC");
            query.setParameter("impressora_matricial_id", impressora_matricial_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
