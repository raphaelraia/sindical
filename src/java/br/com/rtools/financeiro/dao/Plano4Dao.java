package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;

public class Plano4Dao extends DB {

    public List listaPlano4ContaRotina() {
        try {
            Query qry = getEntityManager().createQuery("SELECT p FROM Plano4 p WHERE p.id NOT IN (SELECT cr.plano4.id FROM ContaRotina cr) ORDER BY p.classificador");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }
}
