package br.com.rtools.pessoa.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.pessoa.EnvioEmails;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EnvioEmailsDao extends DB {

    public List<EnvioEmails> pesquisaTodosPorPessoa(int idPessoa) {
        List<EnvioEmails> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select e from EnvioEmails e where e.pessoa.id = " + idPessoa);
            result = (qry.getResultList());
            return result;
        } catch (Exception e) {
            return result;
        }
    }
}
