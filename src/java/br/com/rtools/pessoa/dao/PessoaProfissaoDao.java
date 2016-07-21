package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.PessoaProfissao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PessoaProfissaoDao extends DB {

    public PessoaProfissao pesquisaProfPorFisica(int id) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select pf"
                    + "  from PessoaProfissao pf"
                    + " where pf.fisica.id = :pid");
            qry.setParameter("pid", id);
            return (PessoaProfissao) qry.getSingleResult();
        } catch (Exception e) {
            return new PessoaProfissao();
        }
    }

    public List<PessoaProfissao> findByFisica(Integer fisica_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT PP FROM PessoaProfissao AS PP WHERE PP.fisica.id = :fisica_id");
            query.setParameter("fisica_id", fisica_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
