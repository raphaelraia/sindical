package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ReuniaoPresenca;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ReuniaoPresencaDao extends DB {

    public List<ReuniaoPresenca> findByReuniao(Integer reuniao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT RP FROM ReuniaoPresenca AS RP WHERE RP.reuniao.id = :reuniao_id ORDER BY RP.pessoa.nome ASC");
            query.setParameter("reuniao_id", reuniao_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
