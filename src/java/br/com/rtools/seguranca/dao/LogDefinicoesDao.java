package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class LogDefinicoesDao extends DB {

    public List list() {
        try {
            Query query = getEntityManager().createQuery("SELECT LDA FROM LogDefinicoes AS LDA ORDER BY LDA.id");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
