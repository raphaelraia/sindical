package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.HistoricoEmissaoGuias;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class HistoricoEmissaoGuiasDao extends DB {

    public List<HistoricoEmissaoGuias> findByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT HEG FROM HistoricoEmissaoGuias HEG WHERE HEG.movimento.id = :movimento_id");
            query.setParameter("movimento_id", movimento_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
