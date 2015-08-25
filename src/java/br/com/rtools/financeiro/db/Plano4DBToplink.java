package br.com.rtools.financeiro.db;

import br.com.rtools.principal.DB;
import br.com.rtools.financeiro.Plano4;
import java.util.List;
import javax.persistence.Query;

public class Plano4DBToplink extends DB implements Plano4DB {

    @Override
    public boolean insert(Plano4 plano4) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(plano4);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    @Override
    public boolean update(Plano4 plano4) {
        try {
            getEntityManager().merge(plano4);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean delete(Plano4 plano4) {
        try {
            getEntityManager().remove(plano4);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Plano4 pesquisaCodigo(int id) {
        Plano4 result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Plano4.pesquisaID");
            qry.setParameter("pid", id);
            result = (Plano4) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select p from Plano4 p ");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List pesquisaTodasStrings() {
        try {
            Query qry = getEntityManager().createQuery("select p.conta from Plano4 p ");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public List listaPlano4ContaRotina() {
        try {
            Query qry = getEntityManager().createQuery("SELECT p FROM Plano4 p WHERE p.id NOT IN (SELECT cr.plano4.id FROM ContaRotina cr) ORDER BY p.classificador");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }
}
