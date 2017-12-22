package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.Credenciadores;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CredenciadoresDao extends DB {

    public List<Credenciadores> listaCredenciadores() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT c.* \n "
                    + " FROM soc_credenciadores c \n "
                    + "INNER JOIN pes_pessoa p ON p.id = c.id_pessoa \n "
                    + "WHERE c.dt_inativacao IS NULL \n "
                    + "ORDER BY p.ds_nome ", Credenciadores.class
            );

            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }
    
    public List<Credenciadores> listaTodosCredenciadores() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT c.* \n "
                    + " FROM soc_credenciadores c \n "
                    + "INNER JOIN pes_pessoa p ON p.id = c.id_pessoa \n "
                    + "ORDER BY p.ds_nome ", Credenciadores.class
            );

            return qry.getResultList();
        } catch (Exception e) {

        }
        return new ArrayList();
    }

}
