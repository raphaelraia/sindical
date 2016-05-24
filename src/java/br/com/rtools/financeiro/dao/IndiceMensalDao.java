package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.Indice;
import br.com.rtools.financeiro.IndiceMensal;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class IndiceMensalDao extends DB {

    public List pesquisaIndMensalExistente(int idIndice, int ano, int mes) {
        List result;
        Query qry = getEntityManager().createQuery("select im from IndiceMensal im "
                + " where im.indice.id = " + idIndice
                + "   and im.ano = " + ano
                + "   and im.mes = " + mes);
        result = qry.getResultList();
        if (result.isEmpty()) {
            return new ArrayList();
        } else {
            return result;
        }
    }

    public List pesquisaTodosIndices() {
        try {
            Query qry = getEntityManager().createQuery("select i from Indice i order by i.descricao");
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaIndiceMensalPorIDIndice(int idIndice) {
        try {
            Query qry = getEntityManager().createQuery("select im from IndiceMensal im where im.indice.id = " + idIndice
                    + " order by im.ano desc, im.mes asc");
            return (qry.getResultList());
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public IndiceMensal pesquisaCodigo(int id) {
        IndiceMensal result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("IndiceMensal.pesquisaID");
            qry.setParameter("pid", id);
            result = (IndiceMensal) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Indice pesquisaCodigoIndice(int id) {
        Indice result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Indice.pesquisaID");
            qry.setParameter("pid", id);
            result = (Indice) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }
}
