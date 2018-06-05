 
package br.com.rtools.configuracao.dao;

import br.com.rtools.configuracao.ConfiguracaoSms;
import br.com.rtools.principal.DB;
import javax.persistence.Query;
 
public class ConfiguracaoSmsDao extends DB {
    
    public ConfiguracaoSms findPrincipal() {
        try {
            Query query = getEntityManager().createQuery("SELECT CS FROM ConfiguracaoSms CS ");
            return (ConfiguracaoSms) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
