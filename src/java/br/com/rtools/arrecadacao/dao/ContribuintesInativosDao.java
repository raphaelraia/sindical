package br.com.rtools.arrecadacao.dao;

import br.com.rtools.arrecadacao.ContribuintesInativos;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class ContribuintesInativosDao extends DB {

    public ContribuintesInativos pesquisaContribuintesInativos(int id) {
        ContribuintesInativos result = new ContribuintesInativos();
        try {
//            Query qry = getEntityManager().createQuery(
//                    " select ci " +
//                    "  from ContribuintesInativos ci " +
//                    " where ci.juridica.id = :pid " +
//                    "  and   ci.dtAtivacao is null");
//            qry.setParameter("pid", id);
//            //result = (ContribuintesInativos) qry.getSingleResult();
//            result = (ContribuintesInativos) qry.getSingleResult();
            Query qry = getEntityManager().createNativeQuery("select id from arr_contribuintes_inativos where id_juridica = " + id + " and dt_ativacao is null");
            List vetor = qry.getResultList();
            if (!vetor.isEmpty()) {
                result = (ContribuintesInativos) new Dao().find(new ContribuintesInativos(), (Integer) ((Vector) vetor.get(0)).get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ContribuintesInativos();
        }
        return result;
    }

    public List listaContribuintesInativos(int id) {
        List result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select ci"
                    + "  from ContribuintesInativos ci "
                    + " where ci.juridica.id = :pid ");// +
            //" and   ci.dtAtivacao is null");
            qry.setParameter("pid", id);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }
}
