package br.com.rtools.pessoa.dao;

import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;
import oracle.toplink.essentials.internal.ejb.cmp3.EJBQueryImpl;
import oracle.toplink.essentials.queryframework.DatabaseQuery;

public class FisicaDao extends DB {

    private Integer limit = 0;
    private String not_in = "";
    private String inCategoriaSocio = "";
    private Boolean ignore = false;

    public List<Fisica> pesquisaPessoa(String desc, String por, String como) {
        return pesquisaPessoa(desc, por, como, null, null);
    }

    public List<Fisica> pesquisaPessoa(String desc, String por, String como, Integer limit, Integer offset) {
        if (desc.isEmpty()) {
            return new ArrayList();
        }
        try {
            String textQuery = "";
            String field = "";

            if (por.equals("codigo_pessoa")) {
                field = "p.id";
            } else {
                desc = AnaliseString.normalizeLower(desc);
                switch (como) {
                    case "I":
                        desc = desc + "%";
                        break;
                    case "P":
                        desc = "%" + desc + "%";
                        break;
                }
            }

            int maxResults = 1000;
            if (ignore) {
                switch (desc.length()) {
                    case 1:
                        this.limit = 50;
                        break;
                    case 2:
                        this.limit = 150;
                        break;
                    case 3:
                        this.limit = 500;
                        break;
                    default:
                        break;
                }
            }

            if (por.equals("nome")) {
                field = "p.ds_nome";
            }
            if (por.equals("email1")) {
                field = "p.ds_email1";
            }
            if (por.equals("email2")) {
                field = "p.ds_email2";
            }
            if (por.equals("rg")) {
                field = "f.ds_rg";
            }
            if (por.equals("cpf")) {
                field = "p.ds_documento";
            }
            if (por.equals("nascimento")) {
                field = "f.dt_nascimento";
            }

            String textSelect = " SELECT F.* ";
            if (offset == null && !ignore) {
                textSelect = " SELECT COUNT(*) ";

            }

            switch (por) {
                case "endereco":
                    textQuery
                            = textSelect
                            + "        FROM pes_pessoa_endereco pesend                                                                                                                               "
                            + "  INNER JOIN pes_pessoa pes ON (pes.id = pesend.id_pessoa)                                                                                                            "
                            + "  INNER JOIN end_endereco ende ON (ende.id = pesend.id_endereco)                                                                                                      "
                            + "  INNER JOIN end_cidade cid ON (cid.id = ende.id_cidade)                                                                                                              "
                            + "  INNER JOIN end_descricao_endereco enddes ON (enddes.id = ende.id_descricao_endereco)                                                                                "
                            + "  INNER JOIN end_bairro bai ON (bai.id = ende.id_bairro)                                                                                                              "
                            + "  INNER JOIN end_logradouro logr ON (logr.id = ende.id_logradouro)                                                                                                    "
                            + "  INNER JOIN pes_fisica F ON (F.id_pessoa = pes.id)                                                                                                               "
                            + "  WHERE (LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || bai.ds_descricao || ', ' || cid.ds_cidade || ', ' || cid.ds_uf)) LIKE '%" + desc + "%' "
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  || ', ' || cid.ds_uf)) LIKE '%" + desc + "%'                     "
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  )) LIKE '%" + desc + "%'                                         "
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                    "
                            + "     OR LOWER(FUNC_TRANSLATE(enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                                                "
                            + "     OR LOWER(FUNC_TRANSLATE(cid.ds_cidade)) LIKE '%" + desc + "%'                                                                                                      "
                            + "     OR LOWER(FUNC_TRANSLATE(ende.ds_cep)) = '" + desc + "')"
                            + "    AND pesend.id_tipo_endereco = 1 "
                            + "  ";
                    if (offset != null) {
                        textQuery += " ORDER BY pes.ds_nome ";
                    }
                    break;
                case "codigo":
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica AS F                                 "
                            + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa           "
                            + "  INNER JOIN pes_pessoa_empresa PE ON F.id = PE.id_fisica    "
                            + "       WHERE PE.ds_codigo LIKE '" + desc + "'                ";
                    if (offset != null || ignore) {
                        textQuery += "       ORDER BY P.ds_nome ";
                    }
                    break;
                case "matricula":
                    desc = desc.replace("%", "");
                    try {
                        Integer.parseInt(desc);
                    } catch (Exception e) {
                        return new ArrayList();
                    }
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica AS F                                                         "
                            + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa                                   "
                            + "       WHERE P.id IN (                                                               "
                            + "              SELECT P2.id                                                           "
                            + "                FROM fin_servico_pessoa  AS SP                                       "
                            + "          INNER JOIN soc_socios          AS S    ON S.id_servico_pessoa  = SP.id     "
                            + "          INNER JOIN matr_socios         AS MS   ON MS.id = S.id_matricula_socios    "
                            + "          INNER JOIN pes_pessoa          AS P2   ON P2.id = SP.id_pessoa             "
                            + "               WHERE SP.is_ativo = TRUE  "
                            + "                 AND ms.nr_matricula = " + desc.replace("%", "");
                    textQuery += " AND SP.id_pessoa = MS.id_titular ";
                    textQuery += " ) ";
                    if (offset != null || ignore) {
                        textQuery += "       ORDER BY P.ds_nome ";
                    }
                    break;
                case "codigo_pessoa":
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica AS F                                 "
                            + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa           "
                            + "       WHERE " + field + " = " + Integer.valueOf(desc);
                    if (!not_in.isEmpty()) {
                        textQuery += " AND P.id NOT IN (" + not_in + ")";
                    }
                    if (offset != null || ignore) {
                        textQuery += "       ORDER BY P.ds_nome ";
                    }
                    break;
                case "nascimento":
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica AS F                                 "
                            + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa           "
                            + "       WHERE " + field + " = '" + desc + "'";
                    if (!not_in.isEmpty()) {
                        textQuery += " AND P.id NOT IN (" + not_in + ")";
                    }
                    if (offset != null || ignore) {
                        textQuery += "       ORDER BY P.ds_nome ";
                    }
                    break;
                default:
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica AS F                                 "
                            + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa           "
                            + "       WHERE LOWER(FUNC_TRANSLATE(" + field + ")) LIKE '" + desc + "'";
                    if (!not_in.isEmpty()) {
                        textQuery += " AND P.id NOT IN (" + not_in + ")";
                    }
                    if (offset != null || ignore) {
                        textQuery += "       ORDER BY P.ds_nome ";
                    }
                    break;
            }
            if (offset != null && limit != null) {
                textQuery += " LIMIT " + limit + " OFFSET " + offset;
            } else if (this.limit > 0) {
                textQuery += " LIMIT " + this.limit;
            }
            Query query;
            if (offset == null) {
                if (this.limit > 0) {
                    query = getEntityManager().createNativeQuery(textQuery, Fisica.class);
                } else {
                    query = getEntityManager().createNativeQuery(textQuery);
                }
            } else {
                query = getEntityManager().createNativeQuery(textQuery, Fisica.class);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Fisica> recentes() {
        try {
            String queryString = ""
                    + "     SELECT F.*                                          \n"
                    + "       FROM pes_fisica F                                 \n"
                    + " INNER JOIN pes_pessoa P ON P.id = F.id_pessoa           \n"
                    + "      WHERE (P.dt_recadastro = CURRENT_DATE OR P.dt_atualizacao::date = CURRENT_DATE OR P.dt_criacao = CURRENT_DATE) \n"
                    + "   ORDER BY P.dt_atualizacao DESC NULLS LAST, P.ds_nome ASC ";
            Query query = getEntityManager().createNativeQuery(queryString, Fisica.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPessoaSocio(String desc, String por, String como, Integer limit, Integer offset) {
        return pesquisaPessoaSocio(desc, por, como, false, limit, offset);
    }

    public List pesquisaPessoaSocio(String desc, String por, String como, Boolean titular, Integer limit, Integer offset) {
        if (desc.isEmpty()) {
            return new ArrayList();
        }
        try {
            String textQuery = "";
            String field = "";

            if (por.equals("codigo_pessoa")) {
                field = "p.id";
            } else {
                desc = AnaliseString.normalizeLower(desc);
                desc = (como.equals("I") ? desc + "%" : "%" + desc + "%");
            }

            if (por.equals("nome")) {
                field = "p.ds_nome";
            }
            if (por.equals("email1")) {
                field = "p.ds_email1";
            }
            if (por.equals("email2")) {
                field = "p.ds_email2";
            }
            if (por.equals("rg")) {
                field = "f.ds_rg";
            }
            if (por.equals("cpf")) {
                field = "p.ds_documento";
            }
            String textSelect = " SELECT F.* ";
            if (offset == null) {
                textSelect = " SELECT COUNT(*) ";

            }
            switch (por) {
                case "endereco":
                    textQuery
                            = textSelect
                            + "        FROM pes_pessoa_endereco pesend                                                                                                                               "
                            + "  INNER JOIN pes_pessoa pes ON (pes.id = pesend.id_pessoa)                                                                                                            "
                            + "  INNER JOIN end_endereco ende ON (ende.id = pesend.id_endereco)                                                                                                      "
                            + "  INNER JOIN end_cidade cid ON (cid.id = ende.id_cidade)                                                                                                              "
                            + "  INNER JOIN end_descricao_endereco enddes ON (enddes.id = ende.id_descricao_endereco)                                                                                "
                            + "  INNER JOIN end_bairro bai ON (bai.id = ende.id_bairro)                                                                                                              "
                            + "  INNER JOIN end_logradouro logr ON (logr.id = ende.id_logradouro)                                                                                                    "
                            + "  INNER JOIN pes_fisica fis ON (F.id_pessoa = pes.id)                                                                                                               "
                            + "  WHERE (LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || bai.ds_descricao || ', ' || cid.ds_cidade || ', ' || cid.ds_uf)) LIKE '%" + desc + "%' "
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  || ', ' || cid.ds_uf)) LIKE '%" + desc + "%'                     "
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  )) LIKE '%" + desc + "%'                                         "
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                    "
                            + "     OR LOWER(FUNC_TRANSLATE(enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                                                "
                            + "     OR LOWER(FUNC_TRANSLATE(cid.ds_cidade)) LIKE '%" + desc + "%'                                                                                                      "
                            + "     OR LOWER(FUNC_TRANSLATE(ende.ds_cep)) = '" + desc + "'"
                            + "  ) "
                            + "  AND pesend.id_tipo_endereco = 1 "
                            + "  AND pes.id IN ( "
                            + "              SELECT P2.id                                                           "
                            + "                FROM fin_servico_pessoa  AS SP                                       "
                            + "          INNER JOIN soc_socios          AS S    ON S.id_servico_pessoa  = SP.id     "
                            + "          INNER JOIN matr_socios         AS MS   ON MS.id = S.id_matricula_socios    "
                            + "          INNER JOIN pes_pessoa          AS P2   ON P2.id = SP.id_pessoa             "
                            + "               WHERE SP.is_ativo = TRUE                                              ";
                    if (titular) {
                        textQuery += " AND SP.id_pessoa = MS.id_titular ";
                    }
                    if (!inCategoriaSocio.isEmpty()) {
                        textQuery += " AND C.id_categoria IN (" + inCategoriaSocio + ")";
                    }
                    textQuery += " ) ";
                    if (offset != null) {
                        textQuery += "  ORDER BY P.ds_nome ";
                    }
                    break;
                case "matricula":
                    desc = desc.replace("%", "");
                    try {
                        Integer.parseInt(desc);
                    } catch (Exception e) {
                        return new ArrayList();
                    }
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica AS F                                                         "
                            + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa                                   "
                            + "       WHERE P.id IN (                                                               "
                            + "              SELECT P2.id                                                           "
                            + "                FROM fin_servico_pessoa  AS SP                                       "
                            + "          INNER JOIN soc_socios          AS S    ON S.id_servico_pessoa  = SP.id     "
                            + "          INNER JOIN matr_socios         AS MS   ON MS.id = S.id_matricula_socios    "
                            + "          INNER JOIN pes_pessoa          AS P2   ON P2.id = SP.id_pessoa             "
                            + "               WHERE SP.is_ativo = TRUE  "
                            + "                 AND ms.nr_matricula = " + desc.replace("%", "");
                    if (titular) {
                        textQuery += " AND SP.id_pessoa = MS.id_titular ";
                    }
                    if (!inCategoriaSocio.isEmpty()) {
                        textQuery += " AND C.id_categoria IN (" + inCategoriaSocio + ")";
                    }
                    textQuery += " ) ";
                    if (offset != null) {
                        textQuery += "  ORDER BY P.ds_nome ";
                    }
                    break;
                case "codigo":
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica          AS F                                                "
                            + "  INNER JOIN pes_pessoa          AS P    ON P.id = F.id_pessoa                       "
                            + "  INNER JOIN pes_pessoa_empresa  AS PE   ON F.id = PE.id_fisica                      "
                            + "       WHERE PE.ds_codigo LIKE '" + desc + "'                                        "
                            + "         AND p.id IN (                                                               "
                            + "              SELECT P2.id                                                           "
                            + "                FROM fin_servico_pessoa  AS SP                                       "
                            + "          INNER JOIN soc_socios          AS S    ON S.id_servico_pessoa  = SP.id     "
                            + "          INNER JOIN matr_socios         AS MS   ON MS.id = S.id_matricula_socios    "
                            + "          INNER JOIN pes_pessoa          AS P2   ON P2.id = SP.id_pessoa             "
                            + "               WHERE SP.is_ativo = TRUE                                              ";
                    if (titular) {
                        textQuery += " AND SP.id_pessoa = MS.id_titular ";
                    }
                    if (!inCategoriaSocio.isEmpty()) {
                        textQuery += " AND C.id_categoria IN (" + inCategoriaSocio + ")";
                    }
                    textQuery += " ) ";
                    if (offset != null) {
                        textQuery += "  ORDER BY P.ds_nome ";
                    }
                    break;
                default:
                    if (por.equals("codigo_pessoa")) {
                        textQuery
                                = textSelect
                                + "        FROM pes_fisica AS F                                                         "
                                + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa                                   "
                                + "       WHERE " + field + " = " + Integer.valueOf(desc)
                                + "         AND P.id IN (                                                               "
                                + "              SELECT P2.id                                                           "
                                + "                FROM fin_servico_pessoa  AS SP                                       "
                                + "          INNER JOIN soc_socios          AS S    ON S.id_servico_pessoa  = SP.id     "
                                + "          INNER JOIN matr_socios         AS MS   ON MS.id = S.id_matricula_socios    "
                                + "          INNER JOIN pes_pessoa          AS P2   ON P2.id = SP.id_pessoa             "
                                + "               WHERE SP.is_ativo = TRUE                                              ";
                    } else {
                        textQuery
                                = textSelect
                                + "        FROM pes_fisica AS F                                                         "
                                + "  INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa                                   "
                                + "       WHERE LOWER(FUNC_TRANSLATE(" + field + ")) LIKE '" + desc + "'                "
                                + "         AND P.id IN (                                                               "
                                + "              SELECT P2.id                                                           "
                                + "                FROM fin_servico_pessoa  AS SP                                       "
                                + "          INNER JOIN soc_socios          AS S    ON S.id_servico_pessoa  = SP.id     "
                                + "          INNER JOIN matr_socios         AS MS   ON MS.id = S.id_matricula_socios    "
                                + "          INNER JOIN pes_pessoa          AS P2   ON P2.id = SP.id_pessoa             "
                                + "               WHERE SP.is_ativo = TRUE                                              ";
                    }

                    if (titular) {
                        textQuery += " AND SP.id_pessoa = MS.id_titular ";
                    }
                    if (!inCategoriaSocio.isEmpty()) {
                        textQuery += " AND C.id_categoria IN (" + inCategoriaSocio + ")";
                    }
                    textQuery += " ) ";
                    if (offset != null) {
                        textQuery += "  ORDER BY P.ds_nome ";
                    }
                    break;
            }
            if (offset != null && limit != null) {
                textQuery += " LIMIT " + limit + " OFFSET " + offset;
            }
            Query query;
            if (offset == null) {
                query = getEntityManager().createNativeQuery(textQuery);
            } else {
                query = getEntityManager().createNativeQuery(textQuery, Fisica.class);
            }

            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List pesquisaPessoaSocioInativo(String desc, String por, String como, Integer limit, Integer offset) {
        if (desc.isEmpty()) {
            return new ArrayList();
        }
        try {
            String textQuery = "";
            String field = "";

            if (por.equals("codigo_pessoa")) {
                field = "p.id";
            } else {
                desc = AnaliseString.normalizeLower(desc);
                desc = (como.equals("I") ? desc + "%" : "%" + desc + "%");
            }

            if (por.equals("nome")) {
                field = "p.ds_nome";
            }
            if (por.equals("email1")) {
                field = "p.ds_email1";
            }
            if (por.equals("email2")) {
                field = "p.ds_email2";
            }
            if (por.equals("rg")) {
                field = "f.ds_rg";
            }
            if (por.equals("cpf")) {
                field = "p.ds_documento";
            }
            String textSelect = " SELECT F.* ";
            if (offset == null) {
                textSelect = " SELECT COUNT(*) ";

            }

            if (por.equals("endereco")) {
                textQuery
                        = textSelect
                        + "        FROM pes_pessoa_endereco pesend                                                                                                                               "
                        + "  INNER JOIN pes_pessoa pes ON (pes.id = pesend.id_pessoa)                                                                                                            "
                        + "  INNER JOIN end_endereco ende ON (ende.id = pesend.id_endereco)                                                                                                      "
                        + "  INNER JOIN end_cidade cid ON (cid.id = ende.id_cidade)                                                                                                              "
                        + "  INNER JOIN end_descricao_endereco enddes ON (enddes.id = ende.id_descricao_endereco)                                                                                "
                        + "  INNER JOIN end_bairro bai ON (bai.id = ende.id_bairro)                                                                                                              "
                        + "  INNER JOIN end_logradouro logr ON (logr.id = ende.id_logradouro)                                                                                                    "
                        + "  INNER JOIN pes_fisica F ON (F.id_pessoa = pes.id)                                                                                                               "
                        + "  WHERE (LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || bai.ds_descricao || ', ' || cid.ds_cidade || ', ' || cid.ds_uf)) LIKE '%" + desc + "%' "
                        + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  || ', ' || cid.ds_uf)) LIKE '%" + desc + "%'                     "
                        + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao || ', ' || cid.ds_cidade  )) LIKE '%" + desc + "%'                                         "
                        + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricao || ' ' || enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                    "
                        + "     OR LOWER(FUNC_TRANSLATE(enddes.ds_descricao)) LIKE '%" + desc + "%'                                                                                                "
                        + "     OR LOWER(FUNC_TRANSLATE(cid.ds_cidade)) LIKE '%" + desc + "%'                                                                                                      "
                        + "     OR LOWER(FUNC_TRANSLATE(ende.ds_cep)) = '" + desc + "'"
                        + "  ) "
                        + "  AND pesend.id_tipo_endereco = 1 "
                        + "  AND pes.id IN ( "
                        + "         SELECT p2.id FROM fin_servico_pessoa sp "
                        + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                        + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                        + "  ) "
                        + "  AND pes.id NOT IN ( "
                        + "         SELECT p2.id FROM fin_servico_pessoa sp "
                        + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                        + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                        + "          WHERE sp.is_ativo = TRUE ) ";
                if (offset != null) {
                    textQuery += "  ORDER BY P.ds_nome ";
                }

            } else if (por.equals("matricula")) {
                textQuery
                        = textSelect
                        + "        FROM pes_fisica f "
                        + "  INNER JOIN pes_pessoa p ON p.id = f.id_pessoa "
                        + "  WHERE p.id IN ( "
                        + "         SELECT p2.id FROM fin_servico_pessoa sp "
                        + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                        + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                        + "          INNER JOIN matr_socios ms ON  ms.id = s.id_matricula_socios "
                        + "            AND ms.nr_matricula = " + desc.replace("%", "")
                        + "  ) "
                        + "  AND p.id NOT IN ( "
                        + "         SELECT p2.id FROM fin_servico_pessoa sp "
                        + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                        + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                        + "          WHERE sp.is_ativo = TRUE ) ";
                if (offset != null) {
                    textQuery += "  ORDER BY P.ds_nome ";
                }
            } else if (por.equals("codigo")) {
                textQuery
                        = textSelect
                        + "        FROM pes_fisica f "
                        + "  INNER JOIN pes_pessoa p ON p.id = f.id_pessoa "
                        + "  INNER JOIN pes_pessoa_empresa pe ON f.id = pe.id_fisica "
                        + "  WHERE pe.ds_codigo LIKE '" + desc + "'"
                        + "    AND p.id IN ( "
                        + "         SELECT p2.id FROM fin_servico_pessoa sp "
                        + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                        + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                        + "    ) "
                        + "    AND p.id NOT IN ( "
                        + "         SELECT p2.id FROM fin_servico_pessoa sp "
                        + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                        + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                        + "          WHERE sp.is_ativo = TRUE ) ";
                if (offset != null) {
                    textQuery += "  ORDER BY P.ds_nome ";
                }
            } else if (!field.isEmpty()) {
                if (por.equals("codigo_pessoa")) {
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica f "
                            + "  INNER JOIN pes_pessoa p ON p.id = f.id_pessoa "
                            + "  WHERE " + field + " = " + Integer.valueOf(desc)
                            + "    AND p.id IN ( "
                            + "         SELECT p2.id FROM fin_servico_pessoa sp "
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                            + "    ) "
                            + "    AND p.id NOT IN ( "
                            + "         SELECT p2.id FROM fin_servico_pessoa sp "
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                            + "          WHERE sp.is_ativo = TRUE ) ";
                    if (offset != null) {
                        textQuery += "  ORDER BY P.ds_nome ";
                    }
                } else {
                    textQuery
                            = textSelect
                            + "        FROM pes_fisica f "
                            + "  INNER JOIN pes_pessoa p ON p.id = f.id_pessoa "
                            + "  WHERE LOWER(FUNC_TRANSLATE(" + field + ")) LIKE LOWER(FUNC_TRANSLATE('" + desc + "'))"
                            + "    AND p.id IN ( "
                            + "         SELECT p2.id FROM fin_servico_pessoa sp "
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                            + "    ) "
                            + "    AND p.id NOT IN ( "
                            + "         SELECT p2.id FROM fin_servico_pessoa sp "
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa "
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa "
                            + "          WHERE sp.is_ativo = TRUE "
                            + "    ) ";
                    if (offset != null) {
                        textQuery += "  ORDER BY P.ds_nome ";
                    }
                }
            } else {
                textQuery = "";
            }
            if (!textQuery.isEmpty()) {
                if (offset != null && limit != null) {
                    textQuery += " LIMIT " + limit + " OFFSET " + offset;
                }
                Query query;
                if (offset == null) {
                    query = getEntityManager().createNativeQuery(textQuery);
                } else {
                    query = getEntityManager().createNativeQuery(textQuery, Fisica.class);
                }
                List list = query.getResultList();
                if (!list.isEmpty()) {
                    return list;
                }
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public Fisica idFisica(Fisica des_fisica) {
        Fisica result = null;
        String descricao = des_fisica.getPessoa().getNome().toLowerCase().toUpperCase();
        try {
            Query qry = getEntityManager().createQuery("select nom from Fisica nom where UPPER(nom.fisica) = :d_fisica");
            qry.setParameter("d_fisica", descricao);
            result = (Fisica) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List<Fisica> pesquisaFisicaPorDoc(String doc) {
        return pesquisaFisicaPorDoc(doc, true);
    }

    public List<Fisica> pesquisaFisicaPorDoc(String doc, boolean like) {
        if (like) {
            doc = "%" + doc + "%";
        }
        try {
//            Query qry = getEntityManager().createQuery("SELECT FIS FROM Fisica AS FIS WHERE FIS.pessoa.documento LIKE :documento");
//            qry.setParameter("documento", documento);
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT f.* \n "
                    + " FROM pes_fisica AS f \n"
                    + "INNER JOIN pes_pessoa p ON p.id = f.id_pessoa \n"
                    + "WHERE p.ds_documento LIKE '" + doc + "'", Fisica.class
            );
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Fisica> pesquisaFisicaPorDocRG(String doc) {
        doc = doc.replace("-", "").replace(".", "");
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT f.* FROM pes_fisica f WHERE replace(replace(f.ds_rg, '-', ''), '.', '') LIKE '" + doc + "'", Fisica.class
            );

            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List<Fisica> pesquisaFisicaPorDocSemLike(String doc) {
        try {
            Query qry = getEntityManager().createQuery(
                    "   SELECT FIS                           "
                    + "   FROM Fisica AS FIS                 "
                    + "  WHERE FIS.pessoa.documento LIKE :documento ");
            qry.setParameter("documento", doc);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Fisica pesquisaFisicaPorPessoa(int idPessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select f"
                    + "  from Fisica f "
                    + " where f.pessoa.id = :pid");
            qry.setParameter("pid", idPessoa);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return (Fisica) qry.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<Fisica> pesquisaFisicaPorNomeNascRG(String nome, Date nascimento, String RG) {
        String textQuery = "";
        try {
            if (RG.isEmpty() && nascimento != null) {
                textQuery = "select f"
                        + "  from Fisica f "
                        + " where UPPER(f.pessoa.nome) like '" + nome.toLowerCase().toUpperCase() + "'"
                        + "   and f.dtNascimento = :nasc";
            } else if (!RG.isEmpty()) {
                textQuery = "select f"
                        + "  from Fisica f "
                        + " where f.rg = '" + RG + "'";
            } else {
                return new ArrayList();
            }
            Query qry = getEntityManager().createQuery(textQuery);
            if (RG.isEmpty()) {
                qry.setParameter("nasc", nascimento);
            }
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaFisicaPorNome(String nome) {
        try {
            String textQuery = "select f "
                    + "  from Fisica f "
                    + " where UPPER(f.pessoa.nome) like '%" + nome + "%'";
            Query qry = getEntityManager().createQuery(textQuery);

            qry.setMaxResults(200);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByNome(String nome) {
        try {
            String queryString = "SELECT F.* FROM pes_fisica AS F \n"
                    + "INNER JOIN pes_pessoa AS P ON P.id = F.id_pessoa\n"
                    + "WHERE TRIM(UPPER(func_translate(ds_nome))) LIKE TRIM(UPPER(('" + nome + "')))";
            Query query = getEntityManager().createNativeQuery(queryString, Fisica.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List pesquisaPessoaSocioID(int id_pessoa) {
        List lista = new Vector<Object>();
        String textQuery = null;

        try {
            textQuery = "select fis from Fisica fis, "
                    + //"                 Pessoa pes     " +
                    "  where fis.pessoa.id = " + id_pessoa
                    + "   and pes.id in ( select soc.servicoPessoa.pessoa.id from Socios soc "
                    + " where soc.servicoPessoa.ativo = true )";
            Query qry = getEntityManager().createQuery(textQuery);
            lista = qry.getResultList();
        } catch (Exception e) {
            return lista;
        }
        return lista;
    }

    public List<ServicoPessoa> listaServicoPessoa(int id_pessoa, boolean dependente) {
        List lista = new Vector();
        String textQuery = "SELECT sp FROM ServicoPessoa sp WHERE sp.ativo = TRUE";

        if (dependente) {
            textQuery += " AND sp.pessoa.id = " + id_pessoa;
        } else {
            //textQuery += " AND sp.cobranca.id = "+id_pessoa+" OR sp.pessoa.id = "+id_pessoa;
            textQuery += " AND sp.cobranca.id = " + id_pessoa + " OR (sp.pessoa.id = " + id_pessoa + " AND sp.ativo = TRUE)";
        }

        try {
            Query qry = getEntityManager().createQuery(textQuery);
            lista = qry.getResultList();
        } catch (Exception e) {
            return lista;
        }
        return lista;
    }

    public List<Vector> listaHistoricoServicoPessoa(Integer id_pessoa, Integer id_categoria, Boolean somenteDestaPessoa) {
        String textQuery
                = "  SELECT sp.dt_emissao AS emissao, \n"
                + "       p.ds_nome AS nome, \n"
                + "       sp.desconto_folha AS desconto_folha, \n"
                + "       sp.nr_desconto AS desconto, \n"
                + "       sp.ds_ref_vigoracao AS referencia_vigoracao, \n"
                + "       sp.ds_ref_validade AS referencia_validade, \n"
                + "       s.ds_descricao AS descricao, \n"
                + "       CASE \n"
                + "          WHEN sp.nr_valor_fixo = 0 then func_valor_servico(sp.id_pessoa, sp.id_servico, CURRENT_DATE, 0, " + id_categoria + ")\n"
                + "          WHEN sp.nr_valor_fixo > 0 then sp.nr_valor_fixo\n"
                + "       END as valor,\n"
                + "       CASE \n"
                + "          WHEN sp.nr_desconto=0 and sp.nr_valor_fixo=0 then func_valor_servico(sp.id_pessoa, sp.id_servico, CURRENT_DATE, 0, " + id_categoria + ")\n"
                + "          WHEN sp.nr_desconto>0 and sp.nr_valor_fixo=0 then func_valor_servico_cheio(sp.id_pessoa, sp.id_servico, CURRENT_DATE) - ( sp.nr_desconto * func_valor_servico_cheio(sp.id_pessoa, sp.id_servico, CURRENT_DATE) / 100) \n"
                + "          WHEN sp.nr_desconto=0 and sp.nr_valor_fixo>0 then sp.nr_valor_fixo\n"
                + "          WHEN sp.nr_desconto>0 and sp.nr_valor_fixo>0 then sp.nr_valor_fixo - ( sp.nr_desconto * sp.nr_valor_fixo / 100) \n"
                + "       END as valor_com_desconto \n "
                + "  FROM fin_servico_pessoa sp \n "
                + " INNER JOIN pes_pessoa p ON p.id = sp.id_pessoa \n"
                + " INNER JOIN fin_servicos s ON s.id = sp.id_servico \n"
                + // socios com vigoração ativa ** ?
                //" INNER JOIN soc_socios soc ON soc.id_servico_pessoa = sp.id \n" +
                //" INNER JOIN matr_socios ma ON ma.id = soc.id_matricula_socios \n" +
                " WHERE sp.is_ativo = TRUE \n";

        if (somenteDestaPessoa) {
            textQuery += " AND sp.id_pessoa = " + id_pessoa + " \n";
        } else {
            textQuery += " AND sp.id_cobranca = ( select func_titular_da_pessoa(" + id_pessoa + ") ) \n";
        }

        Query qry = getEntityManager().createNativeQuery(textQuery);
        DatabaseQuery databaseQuery = ((EJBQueryImpl) qry).getDatabaseQuery();
        String sqlString = databaseQuery.getSQLString();

        try {
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Fisica pesquisaFisicaPorNomeNascimento(String nome, Date nascimento) {
        if (nome.isEmpty() && nascimento != null) {
            return null;
        }
        try {
//            Query query = getEntityManager().createQuery("SELECT F FROM Fisica AS F WHERE UPPER(F.pessoa.nome) LIKE :nome AND F.dtNascimento = :nascimento");
//            query.setParameter("nascimento", nascimento);
//            query.setParameter("nome", nome.trim());

            nome = AnaliseString.normalizeLower(nome);

            Query query = getEntityManager().createNativeQuery(
                    "SELECT f.* \n "
                    + "  FROM pes_fisica f \n "
                    + " INNER JOIN pes_pessoa p ON p.id = f.id_pessoa \n "
                    + " WHERE LOWER(FUNC_TRANSLATE(p.ds_nome)) LIKE '" + nome + "' \n "
                    + "   AND f.dt_nascimento = '" + DataHoje.converteData(nascimento) + "'", Fisica.class
            );
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (Fisica) query.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Vector> listaMovimentoFisica(Integer id_pessoa, String status, String tipo_pesquisa) {
        String text
                = " SELECT m.id, \n "
                + "        pr.ds_nome, \n "
                + "        pt.ds_nome,  \n "
                + "        pb.ds_nome,  \n "
                + "        m.ds_referencia,  \n "
                + "        m.dt_vencimento,  \n "
                + "        s.ds_descricao,  \n "
                + "        ts.ds_descricao,  \n "
                + "        func_valor(m.id) as valor,  \n "
                + "        b.dt_baixa,  \n "
                + "        m.ds_documento,  \n "
                + "        pu.ds_nome as nome_usuario,  \n "
                + "        mi.ds_historico,  \n "
                + "        mi.dt_data \n "
                + "   FROM fin_movimento m  \n "
                + "  INNER JOIN pes_pessoa pr ON pr.id = m.id_pessoa \n "
                + "  INNER JOIN pes_pessoa pt ON pt.id = m.id_titular \n "
                + "  INNER JOIN pes_pessoa pb ON pb.id = m.id_beneficiario \n "
                + "  INNER JOIN fin_servicos s ON s.id = m.id_servicos \n "
                + "  INNER JOIN fin_tipo_servico ts ON ts.id = m.id_tipo_servico \n "
                + "   LEFT JOIN fin_baixa b ON b.id = m.id_baixa \n "
                + "   LEFT JOIN fin_movimento_inativo mi ON mi.id_movimento = m.id \n "
                + "   LEFT JOIN seg_usuario u ON u.id = mi.id_usuario \n "
                + "   LEFT JOIN pes_pessoa pu ON pu.id = u.id_pessoa \n "
                + ((!status.equals("excluidos")) ? "  WHERE m.is_ativo = true " : " WHERE m.is_ativo = false ");
        String and = "";

        switch (tipo_pesquisa) {
            case "responsavel":
                and += " AND m.id_pessoa = " + id_pessoa + " \n ";
                break;
            case "titular":
                and += " AND m.id_titular = " + id_pessoa + " \n ";
                break;
            case "beneficiario":
                and += " AND m.id_beneficiario = " + id_pessoa + " \n ";
                break;
            case "beneficiario_titular_atual":
                and += " AND m.id_beneficiario = " + id_pessoa + " AND m.id_titular = func_titular_da_pessoa(" + id_pessoa + ") \n ";
                break;
        }

        switch (status) {
            case "todos":
                break;
            case "abertos":
                and += " AND m.id_baixa IS NULL \n ";
                break;
            case "quitados":
                and += " AND m.id_baixa IS NOT NULL \n ";
                break;
        }

        try {
            Query qry = getEntityManager().createNativeQuery(text + and + " ORDER BY m.dt_vencimento DESC, pb.ds_nome");
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getNot_in() {
        return not_in;
    }

    public void setNot_in(String not_in) {
        this.not_in = not_in;
    }

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public String getInCategoriaSocio() {
        return inCategoriaSocio;
    }

    public void setInCategoriaSocio(String inCategoriaSocio) {
        this.inCategoriaSocio = inCategoriaSocio;
    }

    public Fisica findByDocumento(String documento) {
        try {
            Query query = getEntityManager().createQuery("SELECT F FROM Fisica AS F WHERE F.pessoa.documento LIKE :documento");
            query.setParameter("documento", documento);
            return (Fisica) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

}
