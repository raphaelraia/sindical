package br.com.rtools.retornos;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SicoobDao extends DB {

    public List<Object> xsicoob(String boleto) {
        Query qry = getEntityManager().createNativeQuery(
                "select * from xsicoob_tmk where " +
                "substring('000000000000000'||'"+boleto+"', length('000000000000000'||'"+boleto+"') - 16, length('000000000000000'||'"+boleto+"')) " +
                " = " +
                "substring('000000000000000'||boleto, length('000000000000000'||boleto) - 16, length('000000000000000'||boleto))"
        );
        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public void xupdate(String data_pagamento, String boleto) {

        getEntityManager().getTransaction().begin();
        Query qry = getEntityManager().createNativeQuery(
                "update xsicoob_tmk set valor_errado_baixa = '" + data_pagamento + "' where "+
                "substring('000000000000000'||'"+boleto+"', length('000000000000000'||'"+boleto+"') - 16, length('000000000000000'||'"+boleto+"')) " +
                " = " +
                "substring('000000000000000'||boleto, length('000000000000000'||boleto) - 16, length('000000000000000'||boleto))"
        );

        try {
            qry.executeUpdate();
            getEntityManager().getTransaction().commit();

        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            e.getMessage();
        }

    }
}
