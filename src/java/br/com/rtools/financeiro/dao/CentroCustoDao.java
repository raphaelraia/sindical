package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CentroCustoDao extends DB {

    public List findByFilial(Integer filial_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT CC FROM CentroCusto AS CC WHERE CC.filial.id = :filial_id ORDER BY CC.descricao ");
            query.setParameter("filial_id", filial_id);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }
}
