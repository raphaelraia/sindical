package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Debugs;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class RelatorioContribuintesDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioContribuintesDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioContribuintesDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List pesquisaContabilidades() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select distinct jur.contabilidade "
                    + "  from Juridica jur"
                    + " where jur.contabilidade is not null"
                    + " order by jur.contabilidade.pessoa.nome");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisarCnaeConvencaoPorConvencao(String ids) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select distinct cc from CnaeConvencao cc "
                    + " where cc.convencao.id in (" + ids + ") "
                    + " order by cc.cnae.cnae");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisarGrupoPorConvencao(String ids) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select gc from GrupoCidade gc "
                    + " where gc.id in (select c.grupoCidade.id from ConvencaoCidade c where c.convencao.id in  (" + ids + "))"
                    + " order by gc.descricao");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    /**
     *
     * @param condicao
     * @param escritorio
     * @param tipoCidade
     * @param tipo_endereco_id
     * @param in_cidades
     * @param endereco_id
     * @param cTipo
     * @param in_centro_comercial
     * @param numero
     * @param in_bairros
     * @param convencoes
     * @param cnaes
     * @param in_grupo_cidade
     * @param email
     * @param envio_login
     * @param valor_inicial
     * @param valor_final
     * @param data_valor_inicial
     * @param data_valor_final
     * @param tipo_data_valor
     * @param in_servicos
     * @param tipo_servico_id
     * @param in_empresas
     * @param listDateFilters
     * @return
     */
    public List find(
            String condicao,
            String escritorio,
            String tipoCidade,
            String in_cidades,
            Integer tipo_endereco_id,
            String endereco_id,
            String cTipo,
            String in_centro_comercial,
            String numero,
            String in_bairros,
            String convencoes,
            String cnaes,
            String in_grupo_cidade,
            String email,
            String envio_login,
            String valor_inicial,
            String valor_final,
            String data_valor_inicial,
            String data_valor_final,
            String tipo_data_valor,
            String in_servicos,
            Integer tipo_servico_id,
            String in_empresas,
            /**
             * DATAS
             */
            List<DateFilters> listDateFilters
    ) {
        List listWhere = new ArrayList();
        List result = new ArrayList();
        String queryString = "";
        try {
            queryString = " -- RelatorioContribuintesDao->listaRelatorioContribuintes() \n"
                    + "SELECT j.id AS idJuridica,                               " // 0
                    + "       p.ds_nome                AS nome,                 \n" // 01
                    + "       p.ds_documento           AS documento,            \n" // 02
                    + "       l.ds_descricao           AS logradouro,           \n" // 03
                    + "       de.ds_descricao          AS descricaoEndereco,    \n" // 04
                    + "       c.ds_cidade              AS cidade,               \n" // 05
                    + "       c.ds_uf                  AS uf,                   \n" // 06
                    + "       pe.ds_numero             AS numero,               \n" // 07
                    + "       pe.ds_complemento        AS complemento,          \n" // 08
                    + "       e.ds_cep                 AS cep,                  \n" // 09
                    + "       conpes.ds_nome           AS nomeContabilidade,    \n" // 10
                    + "       b.ds_descricao           AS bairro,               \n" // 11
                    + "       p.ds_telefone1           AS telefone,             \n" // 12
                    + "       p.ds_email1              AS email,                \n" // 13
                    + "       t.ds_descricao           AS tdocumento,           \n" // 14
                    + "       cn.id                    AS idCnae,               \n" // 15
                    + "       cn.ds_numero             AS numero,               \n" // 16
                    + "       cn.ds_cnae               AS descricao,            \n" // 17
                    + "       con.id                   AS idContabilidade,      \n" // 18
                    + "       lcon.ds_descricao        AS conLogradouro,        \n" // 19
                    + "       decon.ds_descricao       AS conDescricaoEndereco, \n"// 20
                    + "       bcon.ds_descricao        AS conBairro,            \n" // 21
                    + "       ccon.ds_cidade           AS conCidade,            \n" // 22
                    + "       ccon.ds_uf               AS conUf,                \n" //23
                    + "       pecon.ds_numero          AS conNumero,            \n" // 24
                    + "       pecon.ds_complemento     AS conComplemento,       \n" // 25
                    + "       econ.ds_cep              AS conCep,               \n" // 26
                    + "       conpes.ds_telefone1      AS contelefone1,         \n" // 27
                    + "       conpes.ds_email1         AS conemal1,             \n" // 28
                    + "       p.id                     AS idPessoa              \n" // 29
                    + "        FROM pes_juridica j"
                    + "  INNER JOIN pes_pessoa             AS p on p.id = j.id_pessoa           \n"
                    + "  INNER JOIN arr_contribuintes_vw   AS CO on CO.id_juridica = j.id       \n"
                    + "  INNER JOIN pes_pessoa_endereco    AS pe on pe.id_pessoa = j.id_pessoa  \n"
                    + "  INNER JOIN end_endereco           AS e on e.id = pe.id_endereco        \n"
                    + "  INNER JOIN end_cidade             AS c on e.id_cidade = c.id           \n"
                    + "  INNER JOIN end_logradouro         AS l on l.id = e.id_logradouro       \n"
                    + "  INNER JOIN end_descricao_endereco AS de on de.id = e.id_descricao_endereco \n"
                    + "  INNER JOIN end_bairro             AS b on b.id = e.id_bairro               \n"
                    + "   LEFT JOIN pes_juridica           AS con on con.id = j.id_contabilidade    \n"
                    + "   LEFT JOIN pes_pessoa             AS conpes on conpes.id = con.id_pessoa   \n"
                    + "   LEFT JOIN pes_tipo_documento     AS t on t.id = p.id_tipo_documento       \n"
                    + "   LEFT JOIN pes_cnae               AS cn on cn.id = j.id_cnae               \n"
                    + "   LEFT JOIN pes_pessoa_endereco    AS pecon on pecon.id_pessoa = con.id_pessoa \n"
                    + "   LEFT JOIN end_endereco           AS econ  on econ.id = pecon.id_endereco  \n"
                    + "   LEFT JOIN end_cidade             AS ccon  on econ.id_cidade = ccon.id     \n"
                    + "   LEFT JOIN end_logradouro         AS lcon  on lcon.id = econ.id_logradouro \n"
                    + "   LEFT JOIN end_descricao_endereco AS decon on decon.id = econ.id_descricao_endereco \n"
                    + "   LEFT JOIN end_bairro             AS bcon  on bcon.id = econ.id_bairro     \n"
                    + "   LEFT JOIN pes_centro_comercial   AS CECOM ON CECOM.id_juridica = j.id     \n"
                    + "   LEFT JOIN arr_contribuintes_inativos  AS ACC ON ACC.id_juridica = j.id     \n";

            // CONVENCAO GRUPO --------------------------------------------
            String cg_where = "", cg_and = "";
            if (in_grupo_cidade.length() != 0) {
                cg_where += " WHERE c.id_grupo_cidade in (" + in_grupo_cidade + ")";
                cg_and += " AND c.id_grupo_cidade in (" + in_grupo_cidade + ")";
            }

            // CONDICAO -----------------------------------------------------
            switch (condicao) {
                case "contribuintes":
                    // listWhere.add("J.id IN (SELECT C.id_juridica FROM arr_contribuintes_vw C " + cg_where + ") ");
                    break;
                case "ativos":
                    // listWhere.add("J.id IN (SELECT C.id_juridica FROM arr_contribuintes_vw c WHERE C.dt_inativacao IS NULL " + cg_and + ") ");
                    listWhere.add("CO.dt_inativacao IS NULL");
                    break;
                case "inativos":
                    // listWhere.add("J.id IN (SELECT C.id_juridica FROM arr_contribuintes_vw C WHERE C.dt_inativacao IS NOT NULL " + cg_and + ")");
                    listWhere.add("CO.dt_inativacao IS NOT NULL");
                    break;
                case "naoContribuinte":
                    // listWhere.add("(J.id NOT IN (SELECT C.id_juridica FROM arr_contribuintes_vw C " + cg_where + ")) ");
                    break;
            }

            // ESCRITORIO ---------------------------------------------------
            if (!escritorio.isEmpty()) {
                switch (escritorio) {
                    case "todos":
                        break;
                    case "semEscritorio":
                        listWhere.add("J.id_contabilidade IS NULL");
                        break;
                    case "comEscritorio":
                        listWhere.add("J.id_contabilidade IS NOT NULL");
                        break;
                    default:
                        listWhere.add("J.id_contabilidade = " + escritorio);
                        break;
                }
            }

            // ENVIO DE LOGIN SENHA
            if (envio_login != null && !envio_login.isEmpty()) {
                switch (envio_login) {
                    case "sim":
                        listWhere.add("J.id_pessoa IN(SELECT EE.id_pessoa FROM pes_envio_emails EE WHERE EE.ds_operacao = 'Login e Senha') ");
                        break;
                    case "nao":
                        listWhere.add("J.id_pessoa NOT IN(SELECT EE.id_pessoa FROM pes_envio_emails EE WHERE EE.ds_operacao = 'Login e Senha') ");
                        break;
                }
            }

            // CIDADE -------------------------------------------------------
            if (tipoCidade != null && !tipoCidade.isEmpty()) {

                switch (tipoCidade) {
                    case "todas":
                        listWhere.add("(PE.id_tipo_endereco = " + tipo_endereco_id + " OR PE.id_tipo_endereco IS NULL)");
                        listWhere.add("(PECON.id_tipo_endereco = 5 OR PECON.id_tipo_endereco IS NULL)");
                        break;
                    case "especificas":
                    case "local":
                        listWhere.add("(PE.id_tipo_endereco = " + tipo_endereco_id + " OR PE.id_tipo_endereco IS NULL)");
                        listWhere.add("(PECON.id_tipo_endereco = 5 OR PECON.id_tipo_endereco IS NULL)");
                        listWhere.add("E.id_cidade IN (" + in_cidades + ")");
                        break;
                    case "outras":
                        listWhere.add("(PE.id_tipo_endereco = " + tipo_endereco_id + " OR PE.id_tipo_endereco IS NULL)");
                        listWhere.add("(PECON.id_tipo_endereco = 5 OR PECON.id_tipo_endereco IS NULL)");
                        listWhere.add("E.id_cidade <> IN (" + in_cidades + ")");
                        break;
                }
            }

            // CENTRO COMERCIAL --------------------------------------------
            if (cTipo.equals("com")) {
                String subqueryString = ""
                        + "     PE.id_endereco IS NOT NULL                      \n"
                        + "      AND (PE.id_endereco, PE.ds_numero) IN (        \n"
                        + "    SELECT PCCPE.id_endereco, PCCPE.ds_numero        \n"
                        + "      FROM pes_centro_comercial PCC                  \n"
                        + "INNER JOIN pes_juridica PCCJ ON PCCJ.id = PCC.id_juridica                \n"
                        + "INNER JOIN pes_pessoa_endereco PCCPE ON PCCPE.id_pessoa = PCCJ.id_pessoa \n"
                        + "     WHERE PCCPE.id_tipo_endereco = 5                                    \n";
                if (in_centro_comercial != null && !in_centro_comercial.isEmpty()) {
                    subqueryString += " AND PCCJ.id_pessoa IN (" + in_centro_comercial + ") \n";
                }
                subqueryString += "  "
                        + " GROUP BY PCCPE.id_endereco, PCCPE.ds_numero         \n"
                        + " )                                                   \n"
                        + " ";

                listWhere.add(subqueryString);
                listWhere.add("PE.id_tipo_endereco = " + tipo_endereco_id + " ");
//                if (in_centro_comercial != null && !in_centro_comercial.isEmpty()) {
//                    listWhere.add("PCECOM.id IN (" + in_centro_comercial + ") ");
//                    if (endereco_id != null && !endereco_id.isEmpty()) {
//                        listWhere.add("PE.id_tipo_endereco = " + tipo_endereco_id + " AND PE.id_endereco IN(" + endereco_id + ") AND PE.ds_numero IN(" + numero + ")");
//                    }
//                }
            } else if (cTipo.equals("sem")) {
                String subqueryString = ""
                        + "     PE.id_endereco IS NOT NULL                      \n"
                        + "      AND (PE.id_endereco, PE.ds_numero) NOT IN (    \n"
                        + "    SELECT PCCPE.id_endereco, PCCPE.ds_numero        \n"
                        + "      FROM pes_centro_comercial PCC                  \n"
                        + "INNER JOIN pes_juridica PCCJ ON PCCJ.id = PCC.id_juridica                \n"
                        + "INNER JOIN pes_pessoa_endereco PCCPE ON PCCPE.id_pessoa = PCCJ.id_pessoa \n"
                        + "     WHERE PCCPE.id_tipo_endereco = 5                                    \n";
                if (in_centro_comercial != null && !in_centro_comercial.isEmpty()) {
                    // subqueryString += " AND PCCJ.id_pessoa IN (" + in_centro_comercial + ") \n";
                }
                subqueryString += "  "
                        + " GROUP BY PCCPE.id_endereco, PCCPE.ds_numero         \n"
                        + " )                                                   \n"
                        + " ";
                listWhere.add(subqueryString);
                listWhere.add("PE.id_tipo_endereco = " + tipo_endereco_id + " ");
            }
            if (in_bairros != null && !in_bairros.isEmpty()) {
                listWhere.add("b.id in (" + in_bairros + ")");
            }
            if (in_empresas != null && !in_empresas.isEmpty()) {
                listWhere.add("P.id in (" + in_empresas + ")");
            }

            // CONVENÇÃO
            if (convencoes != null && !convencoes.isEmpty()) {
                // listWhere.add("j.id IN (select c.id_juridica from arr_contribuintes_vw c where id_convencao in (" + convencoes + ")) ");
                listWhere.add("CO.id_convencao IN (" + convencoes + ") ");
            }
            // CNAES
            if (cnaes != null && !cnaes.isEmpty()) {
                listWhere.add("j.id_cnae in ( " + cnaes + " ) ");
            }
            // EMAIL -------------------------------------------------------
            if (email != null && !email.isEmpty()) {
                if (email.equals("email_sem")) {
                    listWhere.add("P.ds_email1 = ''");
                } else if (email.equals("email_com")) {
                    listWhere.add("P.ds_email1 <> ''");
                }
            }

            // CADASTRO
            if (listDateFilters != null && !listDateFilters.isEmpty()) {
                DateFilters dateFilters = DateFilters.getDateFilters(listDateFilters, "cadastro");
                if (dateFilters != null) {
                    if ((dateFilters.getDtStart() != null && !dateFilters.getStart().isEmpty()) || dateFilters.getType().equals("com") || dateFilters.getType().equals("sem")) {
                        switch (dateFilters.getType()) {
                            case "igual":
                                listWhere.add(" p.dt_criacao = '" + dateFilters.getStart() + "'");
                                break;
                            case "apartir":
                                listWhere.add(" p.dt_criacao >= '" + dateFilters.getStart() + "'");
                                break;
                            case "ate":
                                listWhere.add(" p.dt_criacao <= '" + dateFilters.getStart() + "'");
                                break;
                            case "faixa":
                                if (!dateFilters.getStart().isEmpty()) {
                                    listWhere.add(" p.dt_criacao BETWEEN '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                                }
                                break;
                            case "com":
                                listWhere.add(" p.dt_criacao IS NOT NULL ");
                                break;
                            case "null":
                                listWhere.add(" p.dt_criacao IS NULL ");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            // ATUALIZAÇÃO
            if (listDateFilters != null && !listDateFilters.isEmpty()) {
                DateFilters dateFilters = DateFilters.getDateFilters(listDateFilters, "atualizacao");
                if (dateFilters != null) {
                    if ((dateFilters.getDtStart() != null && !dateFilters.getStart().isEmpty()) || dateFilters.getType().equals("com") || dateFilters.getType().equals("sem")) {
                        switch (dateFilters.getType()) {
                            case "igual":
                                listWhere.add(" p.dt_atualizacao = '" + dateFilters.getStart() + "'");
                                break;
                            case "apartir":
                                listWhere.add(" p.dt_atualizacao >= '" + dateFilters.getStart() + "'");
                                break;
                            case "ate":
                                listWhere.add(" p.dt_atualizacao <= '" + dateFilters.getStart() + "'");
                                break;
                            case "faixa":
                                if (!dateFilters.getStart().isEmpty()) {
                                    listWhere.add(" p.dt_atualizacao BETWEEN '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                                }
                                break;
                            case "com":
                                listWhere.add(" p.dt_atualizacao IS NOT NULL ");
                                break;
                            case "null":
                                listWhere.add(" p.dt_atualizacao IS NULL ");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            // RECADASTRO
            if (listDateFilters != null && !listDateFilters.isEmpty()) {
                DateFilters dateFilters = DateFilters.getDateFilters(listDateFilters, "recadastro");
                if (dateFilters != null) {
                    if ((dateFilters.getDtStart() != null && !dateFilters.getStart().isEmpty()) || dateFilters.getType().equals("com") || dateFilters.getType().equals("sem")) {
                        switch (dateFilters.getType()) {
                            case "igual":
                                listWhere.add(" p.dt_recadastro = '" + dateFilters.getStart() + "'");
                                break;
                            case "apartir":
                                listWhere.add(" p.dt_recadastro >= '" + dateFilters.getStart() + "'");
                                break;
                            case "ate":
                                listWhere.add(" p.dt_recadastro <= '" + dateFilters.getStart() + "'");
                                break;
                            case "faixa":
                                if (!dateFilters.getStart().isEmpty()) {
                                    listWhere.add(" p.dt_recadastro BETWEEN '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                                }
                                break;
                            case "com":
                                listWhere.add(" p.dt_recadastro IS NOT NULL ");
                                break;
                            case "null":
                                listWhere.add(" p.dt_recadastro IS NULL ");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            // RECADASTRO
            if (listDateFilters != null && !listDateFilters.isEmpty()) {
                DateFilters dateFilters = DateFilters.getDateFilters(listDateFilters, "inativacao");
                if (dateFilters != null) {
                    if ((dateFilters.getDtStart() != null && !dateFilters.getStart().isEmpty()) || dateFilters.getType().equals("com") || dateFilters.getType().equals("sem")) {
                        switch (dateFilters.getType()) {
                            case "igual":
                                listWhere.add(" ACC.dt_inativacao = '" + dateFilters.getStart() + "'");
                                break;
                            case "apartir":
                                listWhere.add(" ACC.dt_inativacao >= '" + dateFilters.getStart() + "'");
                                break;
                            case "ate":
                                listWhere.add(" ACC.dt_inativacao <= '" + dateFilters.getStart() + "'");
                                break;
                            case "faixa":
                                if (!dateFilters.getStart().isEmpty()) {
                                    listWhere.add(" ACC.dt_inativacao BETWEEN '" + dateFilters.getStart() + "' AND '" + dateFilters.getFinish() + "'");
                                }
                                break;
                            case "com":
                                listWhere.add(" ACC.dt_inativacao IS NOT NULL ");
                                break;
                            case "null":
                                listWhere.add(" ACC.dt_inativacao IS NULL ");
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            // VALOR
            if (valor_inicial != null && valor_final != null && data_valor_inicial != null && data_valor_final != null) {
                String subquery = "  p.id IN ( \n "
                        + "SELECT m.id_pessoa \n"
                        + "  FROM fin_movimento AS m \n"
                        + " INNER JOIN fin_baixa AS b ON b.id = m.id_baixa \n"
                        + " INNER JOIN fin_servico_rotina AS sr ON sr.id_rotina = 4 AND sr.id_servicos = m.id_servicos \n"
                        + " WHERE m.is_ativo = TRUE \n"
                        + (tipo_data_valor.equals("vencimento") ? " AND m.dt_vencimento >= '" + data_valor_inicial + "' AND m.dt_vencimento <= '" + data_valor_final + "' \n" : " AND b.dt_baixa >= '" + data_valor_inicial + "' AND b.dt_baixa <= '" + data_valor_final + "' \n")
                        + "   AND m.nr_valor_baixa > (" + Moeda.converteUS$(valor_inicial) + " - 1) AND m.nr_valor_baixa < (" + Moeda.converteUS$(valor_final) + " + 1) \n"
                        + (in_servicos != null ? " AND m.id_servicos IN (" + in_servicos + ") \n " : "")
                        + (tipo_servico_id != null ? " AND m.id_tipo_servico = " + tipo_servico_id + " \n " : "")
                        + " GROUP BY id_pessoa )";
                listWhere.add(subquery);
            }

            // ORDEM ------------------------------------------------------------------------
//            if (getRelatorios().getQryOrdem() == null || getRelatorios().getQryOrdem().isEmpty()) {
//                switch ("") {
//                    case "razao":
//                        textQuery += " order by p.ds_nome ";
//                        break;
//                    case "documento":
//                        textQuery += " order by p.ds_documento ";
//                        break;
//                    case "endereco":
//                        textQuery += " order by c.ds_uf, c.ds_cidade, l.ds_descricao, de.ds_descricao, pe.ds_numero";
//                        break;
//                    case "cep":
//                        textQuery += " order by e.ds_cep, c.ds_uf, c.ds_cidade, l.ds_descricao, de.ds_descricao, pe.ds_numero";
//                        break;
//                    case "escritorio":
//                        textQuery += " order by conpes.ds_nome,p.ds_nome ";
//                        break;
//                }
            // } else {
            // textQuery += " ORDER BY " + getRelatorios().getQryOrdem();
            //}
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            if (relatorioOrdem != null) {
                queryString += " ORDER BY " + relatorioOrdem.getQuery();
                //            tordem += tordem.isEmpty() ? " p.nome " : ", p.nome ";
                //            
                //            }
            } else {
                // queryString += " ORDER BY " + "";
            }
            Debugs.put("habilitaDebugQuery", queryString);
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public List listaCentros(String ids) {
        List result = new ArrayList();
        String textQuery = ""
                + "     SELECT pe2.id_endereco,                                 \n"
                + "            pe2.ds_numero                                    \n"
                + "       FROM pes_pessoa_endereco pe2                          \n"
                + " INNER JOIN pes_pessoa p2   ON p2.id = pe2.id_pessoa         \n"
                + " INNER JOIN pes_juridica j2 ON j2.id_pessoa = p2.id          \n"
                + " INNER JOIN pes_centro_comercial cc2 ON cc2.id_juridica = j2.id \n"
                + "      WHERE pe2.id_tipo_endereco = 5                         \n"
                + "        AND j2.id IN (" + ids + ")                           \n";
        try {
            Query qry = getEntityManager().createNativeQuery(textQuery);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List listaRelatorioContribuintesPorJuridica(String condicao, String escritorio, String tipoPCidade, String cidade, String ordem, String cnaes, int idTipoEndereco, String idsJuridica) {
        List result = new ArrayList();
        String textQuery = "";
        try {
            Query qry = getEntityManager().createNativeQuery("select id_juridica from arr_contribuintes_vw");

            // VIEWER CONTRIBUINTES ---------------------------------------------------------------------
            Vector listCont = (Vector) qry.getResultList();
            String idsListCont = "";
            for (int i = 0; i < listCont.size(); i++) {
                if (idsListCont.length() > 0 && i != listCont.size()) {
                    idsListCont = idsListCont + ",";
                }
                idsListCont = idsListCont + (Integer) ((Vector) listCont.get(i)).get(0);
            }

            textQuery = "select j "
                    + "   from Juridica j,"
                    + "        PessoaEndereco pe ";

            // CONDICAO -----------------------------------------------------
            if (condicao.equals("contribuintes")) {
                textQuery = textQuery + "  where (j.id in (" + idsListCont + ") "
                        + "     or j.id in (select ci.juridica.id "
                        + "                   from ContribuintesInativos ci "
                        + "                  group by ci.juridica.id)) "
                        + "    and j.id in (" + idsJuridica + ")";
            } else if (condicao.equals("ativos")) {
                textQuery = textQuery + "  where j.id in (" + idsListCont + ") "
                        + "    and j.id in (" + idsJuridica + ")";
            } else if (condicao.equals("inativos")) {
                textQuery = textQuery + "  where j.id not in (" + idsListCont + ") "
                        + "    and j.id in (select ci.juridica.id from ContribuintesInativos ci group by ci.juridica.id) "
                        + "    and j.id in (" + idsJuridica + ")";
            } else if (condicao.equals("naoContribuinte")) {
                textQuery += "  where j.id not in (" + idsListCont + ") "
                        + "     and j.id in (" + idsJuridica + ")";
            }
            // ESCRITORIO ---------------------------------------------------
            if (escritorio.equals("todos")) {
            } else if (escritorio.equals("semEscritorio")) {
                textQuery = textQuery + " and j.contabilidade is null ";
            } else if (escritorio.equals("comEscritorio")) {
                textQuery = textQuery + " and j.contabilidade is not null ";
            } else {
                textQuery = textQuery + " and j.contabilidade.id = " + Integer.parseInt(escritorio);
            }
            // CIDADE -------------------------------------------------------
            if (tipoPCidade.equals("todas")) {
                textQuery = textQuery + " and pe.pessoa.id = j.pessoa.id "
                        + " and pe.tipoEndereco.id = " + idTipoEndereco;
            } else if (tipoPCidade.equals("especificas")) {
                textQuery = textQuery + " and pe.pessoa.id = j.pessoa.id "
                        + " and pe.tipoEndereco.id = " + idTipoEndereco
                        + " and pe.endereco.cidade.id = " + Integer.parseInt(cidade);
            } else if (tipoPCidade.equals("local")) {
                textQuery = textQuery + " and pe.pessoa.id = j.pessoa.id "
                        + " and pe.tipoEndereco.id = " + idTipoEndereco
                        + " and pe.endereco.cidade.id = " + Integer.parseInt(cidade);
            } else if (tipoPCidade.equals("outras")) {
                textQuery = textQuery + " and pe.pessoa.id = j.pessoa.id "
                        + " and pe.tipoEndereco.id = " + idTipoEndereco
                        + " and pe.endereco.cidade.id <> " + Integer.parseInt(cidade);
            }
            // CNAES

            if (cnaes.length() != 0) {
                textQuery = textQuery + " and j.cnae.id in ( " + cnaes + " ) ";
            }

            // ORDEM ------------------------------------------------------------------------
            if (ordem.equals("razao")) {
                textQuery = textQuery + " order by j.pessoa.nome";
            } else if (ordem.equals("documento")) {
                textQuery = textQuery + " order by j.pessoa.documento";
            } else if (ordem.equals("endereco")) {
                textQuery = textQuery + " order by pe.endereco.cidade.uf,"
                        + " pe.endereco.cidade.cidade, pe.endereco.logradouro.descricao,"
                        + " pe.endereco.descricaoEndereco.descricao,"
                        + " pe.numero";
            } else if (ordem.equals("cep")) {
                textQuery = textQuery + " order by pe.endereco.cep,"
                        + " pe.endereco.cidade.uf,"
                        + " pe.endereco.cidade.cidade, pe.endereco.logradouro.descricao,"
                        + " pe.endereco.descricaoEndereco.descricao,"
                        + " pe.numero";
            } else if (ordem.equals("escritorio")) {
                textQuery = textQuery + " order by j.contabilidade.pessoa.nome,"
                        + "          j.pessoa.nome";
            }

            qry = getEntityManager().createQuery(textQuery);
            result = qry.getResultList();
        } catch (EJBQLException e) {
            e.getMessage();
        }
        return result;
    }

    public List<Servicos> listaServicos() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT s.* FROM fin_servicos s WHERE s.id IN (SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 4)", Servicos.class);
            result = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List<TipoServico> listaTipoServico() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT ts.* FROM fin_tipo_servico ts ORDER BY ts.ds_descricao", TipoServico.class);
            result = qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

}
