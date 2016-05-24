package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ServicoPessoaBloqueio;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ServicoPessoaBloqueioDao extends DB {

    public ServicoPessoaBloqueio find(Integer servico_pessoa_id, Integer mes_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SPB FROM ServicoPessoaBloqueio SPB WHERE SPB.servicoPessoa.id = :servico_pessoa_id AND SPB.mes.id = :mes_id");
            query.setParameter("servico_pessoa_id", servico_pessoa_id);
            query.setParameter("mes_id", mes_id);
            return (ServicoPessoaBloqueio) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findByServicoPessoa(Integer servico_pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SPB FROM ServicoPessoaBloqueio SPB WHERE SPB.servicoPessoa.id = :servico_pessoa_id ORDER BY SPB.mes.id ");
            query.setParameter("servico_pessoa_id", servico_pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
