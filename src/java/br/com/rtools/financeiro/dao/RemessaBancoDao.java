package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class RemessaBancoDao extends DB {

    public Object pesquisaRemessaBancoCobranca(int id_cobranca) {
        Object result = new ArrayList();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " select max(rb.nr_lote) + 1 from fin_remessa_banco rb "
                    + " inner join fin_boleto b on (b.id = rb.id_boleto) "
                    + " inner join fin_conta_cobranca c on (b.id_conta_cobranca = c.id) "
                    + " and c.id = " + id_cobranca
            );
            result = (Object) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return ((Vector) result).get(0);
    }

    public List<RemessaBanco> listaBoletoComRemessaBanco(String ids, Integer id_status_remessa) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT rb.* \n "
                    + "  FROM fin_remessa_banco rb \n "
                    + " WHERE rb.id_boleto IN (" + ids + ") \n "
                    + "   AND rb.id_status_remessa = " + id_status_remessa, RemessaBanco.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
