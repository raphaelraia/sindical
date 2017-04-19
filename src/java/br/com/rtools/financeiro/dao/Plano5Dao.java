package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.Plano4;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class Plano5Dao extends DB {

    public List<String> pesquisaPlano5(int id) {
        List<String> result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p"
                    + "  from Plano5 p"
                    + " where p.plano4.id = :pid");
            qry.setParameter("pid", id);
            result = (List<String>) qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 pesquisaPlano5IDContaBanco(int id) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery("select pl5 from Plano5 pl5 where pl5.contaBanco.id = :pid");
            qry.setParameter("pid", id);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaCaixaBanco() {
        List result = null;
        try {
            Query qry = getEntityManager().createQuery("SELECT P5 FROM Plano5 P5 WHERE P5.plano4.id IN (SELECT CR.plano4.id FROM ContaRotina CR WHERE CR.rotina.id = 2 ) ORDER BY P5.plano4.id, P5.conta");
            result = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List<String> pesquisaPlano5(String des_plano4, String des_plano5) {
        List<String> result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p.conta"
                    + "  from Plano5 p"
                    + " where p.conta like :conta5"
                    + "   and p.plano4.conta like :conta4");
            qry.setParameter("conta4", des_plano4);
            qry.setParameter("conta5", des_plano5);
            result = (List<String>) qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 idPlano5(Plano5 des_plano5) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery("select p from Plano5 p where p.ds_numero = :d_plano5");
            qry.setParameter("d_plano5", des_plano5.getNumero());
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 pesquisaPlano5PorDesc(String desc, String desc4) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pl5"
                    + "  from Plano5 pl5"
                    + " where pl5.conta = :desc"
                    + "   and pl5.plano4.conta = :desc4");
            qry.setParameter("desc", desc);
            qry.setParameter("desc4", desc4);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 pesquisaPlano5PorDesc(String desc) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pl5"
                    + "  from Plano5 pl5"
                    + " where pl5.conta like :desc");
            qry.setParameter("desc", desc);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano4 pesquisaPl4PorString(String desc, String p4) {
        Plano4 result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pl5.plano4"
                    + "  from Plano5 pl5"
                    + " where pl5.conta = :desc"
                    + "   and pl5.plano4.conta = :p4");
            qry.setParameter("desc", desc);
            qry.setParameter("p4", p4);
            result = (Plano4) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public ContaBanco pesquisaUltimoCheque(int pid) {
        ContaBanco result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p.contaBanco"
                    + "  from Plano5 p"
                    + " where p.id = :pid");
            qry.setParameter("pid", pid);
            result = (ContaBanco) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List listPlano4AgrupadoPlanoVw() {
        String queryString = " "
                + "     SELECT id_p4,                                           "
                + "            CONCAT(conta1 ||' - '|| conta3 ||' - '|| conta4) "
                + "       FROM plano_vw GROUP BY                                "
                + "            conta1,                                          "
                + "            conta3,                                          "
                + "            conta4,                                          "
                + "            classificador,                                   "
                + "            id_p4                                            "
                + "   ORDER BY classificador ";
        try {
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List findPlano5ByPlano4(int idPlano4) {
        try {
            Query query = getEntityManager().createQuery("SELECT P5 FROM Plano5 AS P5 WHERE P5.plano4.id = :p1 ORDER BY P5.classificador");
            query.setParameter("p1", idPlano4);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    public List<Plano5> find(Integer plano5_id, Integer tipo_id) {
        try {
            //String queryString = "SELECT P5 FROM Plano5 P5 WHERE P5.contaTipo.id = :tipo_id ";
            String queryString
                    = "SELECT p5.* \n "
                    + "  FROM fin_plano5 p5 \n "
                    + " WHERE p5.id IN (SELECT id_plano5 FROM fin_conta_tipo_plano5 WHERE id_conta_tipo = " + tipo_id + ")";
            
            if (plano5_id != -1) {
                queryString += " AND p5.id = " + plano5_id;
            }
            
            Query query = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findAllGroupByChequePag() {
        String queryString = ""
                + "    "
                + "     SELECT P5.*                                             \n"
                + "       FROM fin_plano5 P5                                    \n"
                + "      WHERE P5.id IN (                                       \n"
                + "                                                             \n"
                + "    SELECT P5.id                                             \n"
                + "      FROM fin_cheque_pag      AS ch                         \n"
                + "INNER JOIN fin_plano5          AS ct ON ct.id = ch.id_plano5 \n"
                + "INNER JOIN fin_conta_banco     AS cb ON cb.id = ct.id_conta_banco \n"
                + "INNER JOIN fin_banco           AS bc ON bc.id = cb.id_banco  \n"
                + "INNER JOIN fin_forma_pagamento AS f  ON f.id_cheque_pag=ch.id\n"
                + "INNER JOIN fin_baixa           AS b  ON b.id  = f.id_baixa   \n"
                + "INNER JOIN fin_movimento       AS m  ON m.id_baixa=b.id      \n"
                + "                                    AND m.is_ativo=true      \n"
                + "INNER JOIN fin_lote            AS l  ON l.id  = m.id_lote    \n"
                + "INNER JOIN pes_pessoa          AS p  ON p.id  = m.id_pessoa  \n"
                + "INNER JOIN fin_plano5          AS p5 ON p5.id = m.id_plano5  \n"
                + "  GROUP BY P5.id                                             \n"
                + "  ORDER BY P5.id                                             \n"
                + "\n"
                + "\n"
                + ")";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Plano5.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
