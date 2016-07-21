package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.EmailPessoa;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class EmailPessoaDao extends DB {

    public List<EmailPessoa> findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT EP FROM EmailPessoa AS EP WHERE EP.pessoa.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
