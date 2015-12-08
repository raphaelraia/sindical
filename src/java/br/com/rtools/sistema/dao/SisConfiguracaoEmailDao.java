package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class SisConfiguracaoEmailDao extends DB {

    public List findByConfiguracao(Integer configuracao_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SCE FROM SisConfiguracaoEmail AS SCE WHERE SCE.configuracao.id = :configuracao_id");
            query.setParameter("configuracao_id", configuracao_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
