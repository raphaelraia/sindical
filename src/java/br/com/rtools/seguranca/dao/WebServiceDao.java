package br.com.rtools.seguranca.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.WebService;
import javax.persistence.Query;

public class WebServiceDao extends DB {

    public WebService find(String login, String senha) {
        try {
            Query query = getEntityManager().createQuery("SELECT WS FROM WebService AS WS WHERE WS.login = :login AND WS.senha = :senha");
            query.setParameter("login", login);
            query.setParameter("senha", senha);
            return (WebService) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
