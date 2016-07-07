package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.principal.DB;
import javax.persistence.Query;

public class BoletoDao extends DB {

    public Boleto findByNrCtrBoleto(String nrCtrBoleto) {
        try {
            Query query = getEntityManager().createQuery("SELECT B FROM Boleto AS B WHERE B.nrCtrBoleto = :nrCtrBoleto");
            query.setParameter("nrCtrBoleto", nrCtrBoleto);
            return (Boleto) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Boleto findByMovimento(Movimento movimento) {
        try {
            Query query = getEntityManager().createQuery("SELECT B FROM Boleto AS B WHERE B.nrCtrBoleto = :nrCtrBoleto");
            query.setParameter("nrCtrBoleto", movimento.getNrCtrBoleto());
            return (Boleto) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
