package br.com.rtools.pessoa.dao;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.pessoa.Cnae;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class EnviarArquivosDao extends DB {

    public Juridica pesquisaCodigo(int id) {
        Juridica result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Juridica.pesquisaID");
            qry.setParameter("pid", id);
            result = (Juridica) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaContabilidades() {
        String textQuery = "";
        try {
            textQuery = "     SELECT jc.id,                                           "
                    + "            p.ds_nome as nome,                               "
                    + "            p.ds_telefone1 as telefone,                      "
                    + "            count(*) qtde,                                   "
                    + "            p.ds_email1 as email                             "
                    + "       FROM arr_contribuintes_vw as c                        "
                    + " INNER JOIN pes_juridica as jc on jc.id = c.id_contabilidade "
                    + " INNER JOIN pes_pessoa as p on p.id = jc.id_pessoa           "
                    + "      WHERE c.dt_inativacao is null                          "
                    + "        AND length(rtrim(p.ds_email1)) > 0                   "
                    + "   GROUP BY jc.id,                                           "
                    + "            p.ds_nome,                                       "
                    + "            p.ds_telefone1,                                  "
                    + "            ds_email1                                        "
                    + "   ORDER BY p.ds_nome                                        ";

            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();

        } catch (EJBQLException e) {
            return new ArrayList();
        }
    }

    public List pesquisaContabilidades(String inConvencao, String inGrupoCidade) {
        String textQuery = "";
        try {
            textQuery = "     SELECT jc.id,                                           "
                    + "            p.ds_nome as nome,                               "
                    + "            p.ds_telefone1 as telefone,                      "
                    + "            count(*) qtde,                                   "
                    + "            p.ds_email1 as email                             "
                    + "       FROM arr_contribuintes_vw as c                        "
                    + " INNER JOIN pes_juridica as jc on jc.id = c.id_contabilidade "
                    + " INNER JOIN pes_pessoa as p on p.id = jc.id_pessoa           ";

            if (!inConvencao.isEmpty()) {
                textQuery += " AND c.id_convencao IN (" + inConvencao + ") ";
            }

            if (!inGrupoCidade.isEmpty()) {
                textQuery += " AND c.id_grupo_cidade IN (" + inGrupoCidade + ") ";
            }

            textQuery += ""
                    + "      WHERE c.dt_inativacao is null                          "
                    + "        AND length(rtrim(p.ds_email1)) > 0                   "
                    + "   GROUP BY jc.id,                                           "
                    + "            p.ds_nome,                                       "
                    + "            p.ds_telefone1,                                  "
                    + "            ds_email1                                        "
                    + "   ORDER BY p.ds_nome                                        ";

            Query qry = getEntityManager().createNativeQuery(textQuery);
            return qry.getResultList();

        } catch (EJBQLException e) {
            return new ArrayList();
        }
    }

    public List pesquisaContribuintes(String in_convencao, String in_grupo_cidade, String in_cnae, boolean empresasDebito, String ids_servicos, String data_vencimento, String tipoContabilidade, Integer qtdeIni, Integer qtdeFim) {
        List listWhere = new ArrayList();
        String queryString = "";
        String subQueryString = "";
//        String caso = "";
//        String inStringCnae = "";
//        String textQuery = "";
//        String textQuery1;
        try {
            String inner_join = "";

            queryString = " SELECT DISTINCT(c.id_juridica),                     \n"
                    + "            p.ds_nome        AS nome,                    \n"
                    + "            p.ds_telefone1   AS telefone,                \n"
                    + "            p.ds_email1      AS email                    \n"
                    + "       FROM arr_contribuintes_vw AS C                    \n"
                    + " INNER JOIN pes_pessoa   AS P  ON P.id  = C.id_pessoa    \n"
                    + " INNER JOIN pes_juridica AS J  ON J.id  = C.id_juridica  \n"
                    + " INNER JOIN pes_cnae     AS CN ON CN.id = J.id_cnae      \n";

            if (empresasDebito) {
                if (!ids_servicos.isEmpty()) {
                    queryString += " INNER JOIN fin_movimento M ON M.id_pessoa = P.id AND M.dt_vencimento < '" + data_vencimento + "' AND M.id_servicos IN (" + ids_servicos + ") AND M.is_ativo = true \n";
                }
            }
            listWhere.add("C.dt_inativacao IS NULL");
            listWhere.add("length(rtrim(P.ds_email1)) > 0");
            if (!in_convencao.isEmpty() && in_grupo_cidade.isEmpty() && in_cnae.isEmpty()) {
                listWhere.add("C.id_convencao IN (" + in_convencao + ") ");
            } else if (!in_convencao.isEmpty() && !in_grupo_cidade.isEmpty() && in_cnae.isEmpty()) {
                listWhere.add("C.id_grupo_cidade IN (" + in_grupo_cidade + ") ");
            } else if (!in_convencao.isEmpty() && !in_grupo_cidade.isEmpty() && !in_cnae.isEmpty()) {
                listWhere.add("J.id_cnae IN (" + in_cnae + ") ");
            }
            if (tipoContabilidade.equals("sem")) {
                listWhere.add("(J.id_contabilidade IS NULL OR J.id_contabilidade = 0)");
            } else if (tipoContabilidade.equals("com")) {
                listWhere.add("J.id_contabilidade > 0");
            } else if (tipoContabilidade.equals("faixa") && qtdeIni > 0 || tipoContabilidade.equals("apartir")) {
                listWhere.add("J.id_contabilidade > 0");
                inner_join = "";
                subQueryString += " ("
                        + "     SELECT count(DISTINCT(C2.id_juridica))                  \n"
                        + "       FROM arr_contribuintes_vw AS C2                       \n"
                        + " INNER JOIN pes_pessoa   AS P2  ON P2.id  = C2.id_pessoa     \n"
                        + " INNER JOIN pes_juridica AS J2  ON J2.id  = C2.id_juridica   \n"
                        + " INNER JOIN pes_cnae     AS CN2 ON CN2.id = J2.id_cnae       \n";
                if (empresasDebito) {
                    if (!ids_servicos.isEmpty()) {
                        subQueryString += " INNER JOIN fin_movimento M2 ON M2.id_pessoa = P2.id AND M2.dt_vencimento < '" + data_vencimento + "' AND M2.id_servicos IN (" + ids_servicos + ") AND M2.is_ativo = true \n";

                    }
                }
                subQueryString += " WHERE c2.dt_inativacao IS NULL \n";
                subQueryString += " AND length(rtrim(p2.ds_email1)) > 0 \n";
                if (!in_convencao.isEmpty() && in_grupo_cidade.isEmpty() && in_cnae.isEmpty()) {
                    subQueryString += " AND C2.id_convencao IN (" + in_convencao + ") \n";
                } else if (!in_convencao.isEmpty() && !in_grupo_cidade.isEmpty() && in_cnae.isEmpty()) {
                    subQueryString += " AND C2.id_grupo_cidade IN(" + in_grupo_cidade + ") \n";
                } else if (!in_convencao.isEmpty() && !in_grupo_cidade.isEmpty() && !in_cnae.isEmpty()) {
                    subQueryString += " AND J2.id_cnae IN(" + in_cnae + ") \n";
                }
                if (qtdeFim < qtdeIni) {
                    qtdeFim = 0;
                }
                if (tipoContabilidade.equals("faixa")) {
                    if ((qtdeIni > 0 && qtdeFim <= 0) || (Objects.equals(qtdeIni, qtdeFim))) {
                        subQueryString += " AND C2.id_contabilidade = C.id_contabilidade) = " + qtdeIni + " \n";
                    } else if (qtdeIni > 0 && qtdeFim > 0) {
                        subQueryString += " AND C2.id_contabilidade = C.id_contabilidade) BETWEEN " + qtdeIni + " AND " + qtdeFim + " \n";
                    } else {
                        subQueryString += " AND C2.id_contabilidade = C.id_contabilidade) = " + qtdeIni + " \n";
                    }
                } else {
                    if ((qtdeIni > 0 && qtdeFim <= 0) || (Objects.equals(qtdeIni, qtdeFim))) {
                        subQueryString += " AND C2.id_contabilidade = c.id_contabilidade) >= " + qtdeIni + " \n";
                    }
                }
            }
            if (!subQueryString.isEmpty()) {
                listWhere.add(subQueryString);
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + "\n";
                }
            }

            queryString += " ORDER BY P.ds_nome ";
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Convencao> listaConvencao() {
        return listaConvencao(false);
    }

    public List<Convencao> listaConvencao(boolean isContabilidade) {
        String textQuery;
        try {

            textQuery = "   SELECT c.id_convencao               "
                    + "     FROM arr_contribuintes_vw AS c      ";

            if (isContabilidade) {
                textQuery += " WHERE id_contabilidade IS NOT NULL ";
            }

            textQuery += ""
                    + " GROUP BY c.id_convencao                 "
                    + " ORDER BY c.id_convencao                 ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                if (!list.isEmpty()) {
                    String idConvencao = "";
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            idConvencao = ((List) list.get(i)).get(0).toString();
                        } else {
                            idConvencao += ", " + ((List) list.get(i)).get(0).toString();
                        }
                    }
                    Query qryConvencoes = getEntityManager().createQuery(" SELECT con FROM Convencao AS con WHERE con.id IN (" + idConvencao + ")");
                    List list1 = qryConvencoes.getResultList();
                    if (!list1.isEmpty()) {
                        return list1;
                    }
                }
                return list;
            }
        } catch (EJBQLException e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<GrupoCidade> listaGrupoCidadePorConvencao(String listaConvencao) {
        String textQuery;
        String filtroPorConvencao = "";
        if (!listaConvencao.isEmpty()) {
            filtroPorConvencao = " WHERE c.id_convencao in (" + listaConvencao + ") ";
        }
        try {

            textQuery = "   SELECT c.id_grupo_cidade          "
                    + "     FROM arr_contribuintes_vw AS c  "
                    + filtroPorConvencao
                    + " GROUP BY c.id_grupo_cidade          "
                    + " ORDER BY c.id_grupo_cidade          ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                String idGrupoCidade = "";
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idGrupoCidade = ((List) list.get(i)).get(0).toString();
                    } else {
                        idGrupoCidade += ", " + ((List) list.get(i)).get(0).toString();
                    }
                }
                Query qryGrupoCidade = getEntityManager().createQuery(" SELECT gc FROM GrupoCidade AS gc WHERE gc.id IN(" + idGrupoCidade + ")");
                List list1 = qryGrupoCidade.getResultList();
                if (!list1.isEmpty()) {
                    return list1;
                }
            }
        } catch (EJBQLException e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Cnae> listaCnaePorConvencao(String listaConvencao) {
        String textQuery = "";
        String filtroPorConvencao = "";
        if (!listaConvencao.isEmpty()) {

            filtroPorConvencao = " WHERE c.id_convencao in (" + listaConvencao + ")   ";
        }
        try {
            textQuery = "     SELECT cn.id                                      "
                    + "       FROM arr_contribuintes_vw AS c                  "
                    + " INNER JOIN pes_juridica AS j on j.id = c.id_juridica  "
                    + " INNER JOIN pes_cnae AS cn on cn.id = j.id_cnae        "
                    + filtroPorConvencao
                    + "   GROUP BY cn.id                                      "
                    + "   ORDER BY cn.id                                      ";
            Query qry = getEntityManager().createNativeQuery(textQuery);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                String idCnaeConvencao = "";
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idCnaeConvencao = ((List) list.get(i)).get(0).toString();
                    } else {
                        idCnaeConvencao += ", " + ((List) list.get(i)).get(0).toString();
                    }
                }
                Query qryGrupoCidade = getEntityManager().createQuery(" SELECT C FROM Cnae AS C WHERE C.id IN(" + idCnaeConvencao + ") ");
                List list1 = qryGrupoCidade.getResultList();
                if (!list1.isEmpty()) {
                    return list1;
                }
            }
        } catch (EJBQLException e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Servicos> listaServicosAteVencimento() {
        String text = "SELECT se.* \n "
                + "  FROM fin_servicos se \n "
                + " INNER JOIN arr_mensagem_convencao m ON m.id_servicos = se.id \n "
                + " GROUP BY se.id, se.ds_descricao \n "
                + " ORDER BY se.ds_descricao ";
        try {
            Query qry = getEntityManager().createNativeQuery(text, Servicos.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
