package br.com.rtools.financeiro.dao;

import br.com.rtools.financeiro.*;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class PlanoDao extends DB {
      
   
   
    public List pesquisaPlano(String desc, String por, String como, String plano, int id) {
        List result = null;

        String textQuery = null;
        if (como.equals("T")) {
            if (plano.equals("Plano")) {
                textQuery = "select p from Plano p " + " order by p.numero";
            } else if (plano.equals("Plano2")) {
                textQuery = "select p2 from Plano2 p2 where p2.plano.id = " + id + " order by p2.numero";
            } else if (plano.equals("Plano3")) {
                textQuery = "select p3 from Plano3 p3 where p3.plano2.id = " + id + " order by p3.numero";
            } else if (plano.equals("Plano4")) {
                textQuery = "select p4 from Plano4 p4 where p4.plano3.id = " + id + " order by p3.numero";
            } else if (plano.equals("Plano5")) {
                if (id != -1) {
                    textQuery = "select p5 from Plano5 p5 where p5.plano4.id = " + id + " order by p5.numero";
                } else {
                    textQuery = "select p5 from Plano5 p5" + " order by p5.numero";
                }
            }
            try {
                Query qry = getEntityManager().createQuery(textQuery);
                result = qry.getResultList();
            } catch (Exception e) {
                result = null;
            }
        } else {
            if (como.equals("I")) {
                desc = desc.toLowerCase().toUpperCase() + "%";
            } else if (como.equals("P")) {
                desc = "%" + desc.toLowerCase().toUpperCase() + "%";
            }

            if (plano.equals("Plano")) {
                textQuery = "select p from Plano p where p." + por + " like :desc" + " order by p.numero";
            } else if (plano.equals("Plano2")) {
                textQuery = "select p2 from Plano2 p2 where p2." + por + " like :desc and p2.plano.id = " + id + " order by p2.numero";
            } else if (plano.equals("Plano3")) {
                textQuery = "select p3 from Plano3 p3 where p3." + por + " like :desc and p3.plano2.id = " + id + " order by p3.numero";
            } else if (plano.equals("Plano4")) {
                textQuery = "select p4 from Plano4 p4 where p4." + por + " like :desc and p4.plano3.id = " + id + " order by p4.numero";
            } else if (plano.equals("Plano5")) {
                if (id != -1) {
                    textQuery = "select p5 from Plano5 p5 where p5." + por + " like :desc and p5.plano5.id = " + id + " order by p5.numero";
                } else {
                    textQuery = "select p5 from Plano5 p5 where p5." + por + " like :desc" + " order by p5.numero";
                }
            }

            if (desc != null) {
                try {
                    Query qry = getEntityManager().createQuery(textQuery);
                    qry.setParameter("desc", desc);
                    result = qry.getResultList();
                } catch (Exception e) {
                    result = null;
                }
            }

        }

        return result;
    }

   
    public List pesquisaPorPlano(String desc, String por, String como, String plano) {
        desc = AnaliseString.removerAcentos(desc);
        desc = desc.toUpperCase();
        String textQuery = null;
        if (como.equals("T")) {
            textQuery = "";
        } else if (como.equals("P")) {
            desc = "%" + desc.toLowerCase().toUpperCase() + "%";
            textQuery = "SELECT O.* FROM " + plano + " O WHERE UPPER(TRANSLATE(O." + por + ")) LIKE '" + desc + "' ORDER BY O." + por;
        } else if (como.equals("I")) {
            desc = desc.toLowerCase().toUpperCase() + "%";
            textQuery = "SELECT O.* FROM " + plano + " O WHERE UPPER(TRANSLATE(O." + por + ")) LIKE '" + desc + "' ORDER BY O." + por;
        }
        try {
            Query query = getEntityManager().createNativeQuery(textQuery, Plano5.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

   
    public List pesquisaPlanos(String plano) {

        List result = null;

        String textQuery = null;
        textQuery = "select p from " + plano + " p " + " order by p.classificador, p.numero";
        try {
            Query qry = getEntityManager().createQuery(textQuery);
            result = qry.getResultList();
        } catch (Exception e) {
            result = new ArrayList();
        }

        return result;
    }
}
