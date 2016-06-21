package br.com.rtools.pessoa.dao;

import br.com.rtools.pessoa.PessoaProfissao;
import br.com.rtools.principal.DB;
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
}
