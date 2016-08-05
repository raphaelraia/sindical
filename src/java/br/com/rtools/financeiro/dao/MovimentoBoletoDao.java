package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.MovimentoBoleto;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class MovimentoBoletoDao extends DB {

    public MovimentoBoleto findByBoleto(Integer boleto_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MB FROM MovimentoBoleto AS MB WHERE MB.boleto.id = :boleto_id");
            query.setParameter("boleto_id", boleto_id);
            return (MovimentoBoleto) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public MovimentoBoleto findByMovimento(Integer movimento_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT MB FROM MovimentoBoleto AS MB WHERE MB.movimento.id = :movimento_id");
            query.setParameter("movimento_id", movimento_id);
            return (MovimentoBoleto) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
