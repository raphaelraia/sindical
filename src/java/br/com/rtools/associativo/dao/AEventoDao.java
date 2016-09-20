package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.AEvento;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AEventoDao extends DB {

    public List<AEvento> pesquisaPorDescricaoEvento(Integer descricao_evento_id) {
        List<AEvento> list;
        try {
            Query qry = getEntityManager().createQuery("SELECT AE FROM AEvento AS AE WHERE AE.descricaoEvento.id = :descricao_evento_id ORDER BY AE.descricaoEvento.grupoEvento.descricao ASC, AE.descricaoEvento.descricao ASC");
            qry.setParameter("descricao_evento_id", descricao_evento_id);
            list = qry.getResultList();
        } catch (Exception e) {
            list = new ArrayList();
        }
        return list;
    }

    public AEvento findByDescricaoEvento(Integer descricao_evento_id) {
        try {
            Query qry = getEntityManager().createQuery("SELECT AE FROM AEvento AS AE WHERE AE.descricaoEvento.id = :descricao_evento_id ORDER BY AE.descricaoEvento.grupoEvento.descricao ASC, AE.descricaoEvento.descricao ASC");
            qry.setParameter("descricao_evento_id", descricao_evento_id);
            return (AEvento) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
