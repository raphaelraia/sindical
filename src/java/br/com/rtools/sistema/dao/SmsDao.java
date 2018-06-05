package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import javax.persistence.Query;
import br.com.rtools.sistema.Sms;

public class SmsDao extends DB {

    public Sms findBy(String tabela, String chave, Integer codigo) {
        try {
            String queryString = "SELECT T.* FROM sis_sms AS T WHERE T.ds_tabela = '" + tabela + "' AND T.ds_chave = '" + chave + "' AND T.nr_codigo = " + codigo + " ORDER BY T.nr_codigo DESC";
            Query query = getEntityManager().createNativeQuery(queryString, Sms.class);
            query.setMaxResults(1);
            return (Sms) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Object findBy(String tabela, String chave, Integer codigo, Class type) {
        try {
            String queryString = "SELECT T.* FROM " + tabela + " AS T WHERE T." + chave + " = " + codigo + " ORDER BY T." + chave + " DESC";
            Query query = getEntityManager().createNativeQuery(queryString, type);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Object findBy(String tabela, String chave, String valor, Class type) {
        try {
            String queryString = "SELECT T.* FROM " + tabela + " AS T WHERE T." + chave + " = '" + valor + "' ORDER BY T." + chave + " DESC";
            Query query = getEntityManager().createNativeQuery(queryString, type);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Object findAll(String tabela, String chave, Integer codigo, Class type) {
        try {
            String queryString = "SELECT T.* FROM " + tabela + " AS T WHERE T." + chave + " = " + codigo + " ORDER BY T." + chave + " DESC";
            Query query = getEntityManager().createNativeQuery(queryString, type);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Object findAll(String tabela, String chave, String valor, Class type) {
        try {
            String queryString = "SELECT T.* FROM " + tabela + " AS T WHERE T." + chave + " = '" + valor + "' ORDER BY T." + chave + " DESC";
            Query query = getEntityManager().createNativeQuery(queryString, type);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
