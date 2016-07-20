package br.com.rtools.webservice.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.webservice.WSCaptcha;
import javax.persistence.Query;

public class WSCaptchaDao extends DB {

    public WSCaptcha find(String session_id, String document) {
        try {
            // Query query = getEntityManager().createQuery("SELECT WS FROM WSCaptcha AS WS WHERE WS.sessionId LIKE :session_id AND WS.document = :document");
            Query query = getEntityManager().createQuery("SELECT WS FROM WSCaptcha AS WS WHERE WS.document LIKE :document");
            // query.setParameter("session_id", session_id);
            query.setParameter("document", document);
            return (WSCaptcha) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
