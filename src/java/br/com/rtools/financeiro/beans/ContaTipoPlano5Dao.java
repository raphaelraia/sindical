package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.ContaTipoPlano5;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ContaTipoPlano5Dao extends DB {

    public List<ContaTipoPlano5> find(Integer plano5_id, Integer tipo_id) {
        try {
            String queryString = "SELECT CT FROM ContaTipoPlano5 CT WHERE CT.contaTipo.id = :tipo_id ";
            if (plano5_id != -1) {
                queryString += " AND CT.plano5.id = :plano5_id ";
            }
            Query query = getEntityManager().createQuery(queryString);
            query.setParameter("tipo_id", tipo_id);
            if (plano5_id != -1) {
                query.setParameter("plano5_id", plano5_id);

            }
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
