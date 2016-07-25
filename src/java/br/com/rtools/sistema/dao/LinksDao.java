package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.Links;
import java.util.List;
import javax.persistence.Query;

public class LinksDao extends DB {

    public Links pesquisaNomeArquivo(String arquivo) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select l "
                    + "  from Links l "
                    + " where l.nomeArquivo = '" + arquivo + "'");
            return (Links) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findAllByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT L FROM Links AS L WHERE L.pessoa.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
