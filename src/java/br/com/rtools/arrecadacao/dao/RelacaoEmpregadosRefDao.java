package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.RelacaoEmpregadosRef;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelacaoEmpregadosRefDao extends DB {

    public List<RelacaoEmpregadosRef> list() {
        try {
            Query query = getEntityManager().createQuery("SELECT REF FROM RelacaoEmpregadosRef REF ORDER BY REF.referencia DESC");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
