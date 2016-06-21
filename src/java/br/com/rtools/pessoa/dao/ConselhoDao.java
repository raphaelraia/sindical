package br.com.rtools.pessoa.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.pessoa.Conselho;
import java.util.List;
import javax.persistence.Query;

public class ConselhoDao extends DB  {


    public Conselho idConselho(Conselho des_conselho) {
        Conselho result = null;
        String descricao = des_conselho.getConselho().toLowerCase().toUpperCase();
        try {
            Query qry = getEntityManager().createQuery("select con from Conselho con where UPPER(con.conselho) = :d_conselho");
            qry.setParameter("d_conselho", descricao);
            result = (Conselho) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }
}
