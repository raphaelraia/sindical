package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.ServicoCategoria;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class ServicoCategoriaDao extends DB {

    public List pesquisaServCatPorId(Integer categoria_id) {
        try {
            Query qry = getEntityManager().createQuery("select sc "
                    + "  from ServicoCategoria sc"
                    + " where sc.categoria.id = " + categoria_id
                    + " order by sc.parentesco.id");
            return (qry.getResultList());
        } catch (EJBQLException e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public ServicoCategoria pesquisaPorParECat(Integer parentesco_id, Integer categoria_id) {
        ServicoCategoria result = null;
        try {
            Query qry = getEntityManager().createQuery("select sc from ServicoCategoria sc "
                    + " where sc.categoria.id = " + categoria_id + " "
                    + "   and sc.parentesco.id = " + parentesco_id);
            result = (ServicoCategoria) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public ServicoCategoria find(Integer parentesco_id, Integer categoria_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SC FROM ServicoCategoria SC WHERE SC.categoria.id = :categoria_id AND SC.parentesco.id = :parentesco_id");
            query.setParameter("parentesco_id", parentesco_id);
            query.setParameter("categoria_id", categoria_id);
            return (ServicoCategoria) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
