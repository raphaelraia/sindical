package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioMovimentosDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioMovimentosDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioMovimentosDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(
            String condicao,
            String situacao,
            String in_servicos,
            String in_tipo_servico,
            String in_empresas,
            String in_contabilidades,
            String por_data,
            String tipo_data,
            String data_inicial,
            String data_final,
            String in_convencao,
            String in_grupo_cidades,
            String in_cidades_base,
            String in_cnaes,
            String valor_baixa_inicial,
            String valor_baixa_final,
            List<DateFilters> listDateFilters) {

        if (relatorios == null) {
            return new ArrayList();
        }
        if (listDateFilters == null) {
            listDateFilters = new ArrayList();
        }
        List listWhere = new ArrayList<>();
        String queryString
                = "    FROM fin_movimento                AS mov                   \n "
                + " INNER JOIN fin_servicos            AS se               ON se.id                = mov.id_servicos                            \n "
                + " INNER JOIN fin_servico_rotina      AS ser              ON ser.id_servicos      = se.id AND ser.id_rotina = 4                \n "
                + "  LEFT JOIN fin_baixa               AS lot              ON lot.id               = mov.id_baixa                               \n "
                + " INNER JOIN pes_pessoa              AS pes              ON pes.id               = mov.id_pessoa                              \n "
                + "  LEFT JOIN pes_juridica            AS jur              ON jur.id_pessoa        = pes.id                                     \n "
                + "  LEFT JOIN pes_juridica            AS esc              ON esc.id               = jur.id_contabilidade                       \n "
                + "  LEFT JOIN fin_boleto              AS bol              ON bol.nr_ctr_boleto    = mov.nr_ctr_boleto                          \n "
                + "  LEFT JOIN fin_conta_cobranca      AS cc               ON cc.id                = bol.id_conta_cobranca                      \n "
                + " INNER JOIN pes_tipo_documento      AS pdoc             ON pdoc.id              = pes.id_tipo_documento                      \n "
                + " INNER JOIN fin_tipo_servico        AS ts               ON ts.id                = mov.id_tipo_servico                        \n "
                + "  LEFT JOIN pes_pessoa              AS pesc             ON pesc.id              = esc.id_pessoa                              \n "
                + "  LEFT JOIN pes_tipo_documento      AS escdoc           ON escdoc.id            = pes.id_tipo_documento                                                              \n "
                + "  LEFT JOIN pes_cnae                AS cnae             ON cnae.id              = jur.id_cnae                                                                        \n "
                + "  LEFT JOIN pes_pessoa_endereco     AS pes_pend         ON pes_pend.id_pessoa   = pes.id AND (pes_pend.id_tipo_endereco = 2 OR pes_pend.id_tipo_endereco IS NULL)    \n "
                + "  LEFT JOIN end_endereco            AS pes_end          ON pes_end.id           = pes_pend.id_endereco                                                               \n "
                + "  LEFT JOIN end_logradouro          AS pes_logradouro   ON pes_logradouro.id    = pes_end.id_logradouro                                                              \n "
                + "  LEFT JOIN end_descricao_endereco  AS pes_endereco     ON pes_endereco.id      = pes_end.id_descricao_endereco                                                      \n "
                + "  LEFT JOIN end_bairro              AS pes_bairro       ON pes_bairro.id        = pes_end.id_bairro                                                                  \n "
                + "  LEFT JOIN end_cidade              AS pes_cidade       ON pes_cidade.id        = pes_end.id_cidade                                                                  \n "
                + "  LEFT JOIN pes_pessoa_endereco     AS esc_pend         ON esc_pend.id_pessoa   = pesc.id AND (esc_pend.id_tipo_endereco = 2 OR esc_pend.id_tipo_endereco IS NULL)   \n "
                + "  LEFT JOIN end_endereco            AS esc_end          ON esc_end.id           = esc_pend.id_endereco                                                               \n "
                + "  LEFT JOIN end_logradouro          AS esc_logradouro   ON esc_logradouro.id    = esc_end.id_logradouro                                                              \n "
                + "  LEFT JOIN end_descricao_endereco  AS esc_endereco     ON esc_endereco.id      = esc_end.id_descricao_endereco                                                      \n "
                + "  LEFT JOIN end_bairro              AS esc_bairro       ON esc_bairro.id        = esc_end.id_bairro                                                                  \n "
                + "  LEFT JOIN end_cidade              AS esc_cidade       ON esc_cidade.id        = esc_end.id_cidade                                                                  \n "
                + "  LEFT JOIN seg_usuario             AS us               ON us.id                = lot.id_usuario                                                                     \n "
                + "  LEFT JOIN pes_pessoa              AS upes             ON upes.id              = us.id_pessoa ";
        // CONDICAO -----------------------------------------------------
        listWhere.add("mov.is_ativo = true");
        switch (condicao) {
            case "todos":
                break;
            case "ativos":
                listWhere.add("mov.id_pessoa IN ( SELECT id_pessoa FROM arr_contribuintes_vw WHERE dt_inativacao IS NULL )");
                break;
            case "inativos":
                listWhere.add("mov.id_pessoa NOT IN ( SELECT id_pessoa FROM arr_contribuintes_vw WHERE dt_inativacao IS NULL )");
                listWhere.add("mov.id_pessoa IN ( SELECT id_pessoa FROM arr_contribuintes_vw WHERE dt_inativacao IS NOT NULL GROUP BY id_pessoa )");
                break;
            case "naoContribuintes":
                listWhere.add("mov.id_pessoa NOT IN ( SELECT id_pessoa FROM arr_contribuintes_vw WHERE dt_inativacao IS NULL )");
                listWhere.add("mov.id_pessoa NOT IN ( SELECT id_pessoa FROM arr_contribuintes_vw WHERE dt_inativacao IS NOT NULL GROUP BY id_pessoa )");
                break;
        }

        // CONTRIBUICAO DE RELATORIO---------------------------------------------
        if (in_servicos != null && !in_servicos.isEmpty()) {
            listWhere.add("mov.id_servicos IN ( " + in_servicos + ")");
        }

        // TIPO SERVICO DO RELATORIO-----------------------------------------------
        if (in_tipo_servico != null && !in_tipo_servico.isEmpty()) {
            listWhere.add("mov.id_tipo_servico IN ( " + in_tipo_servico + " )");
        }

        // PESSOA DO RELATORIO-----------------------------------------------------
        if (in_empresas != null && !in_empresas.isEmpty()) {
            listWhere.add("jur.id IN ( " + in_empresas + " )");
        }

        // FILTRAR POR ESCRITÓRIOS ------------------------------------------------        
        if (in_contabilidades != null && !in_contabilidades.isEmpty()) {
            switch (in_contabilidades) {
                case "sem":
                    listWhere.add("jur.id_contabilidade IS NULL ");
                    break;
                case "com":
                    listWhere.add("jur.id_contabilidade IS NOT NULL");
                    break;
                default:
                    listWhere.add("esc.id IN ( " + in_contabilidades + " )");
                    break;
            }
        }

        // FILTRO MOVIMENTO ---------------------------------------------------------
        switch (situacao) {
            case "todas":
                break;
            case "recebidas":
                listWhere.add("mov.id_baixa IS NOT NULL");
                break;
            case "naorecebidas":
                listWhere.add("mov.id_baixa IS NULL");
                break;
            case "atrasadas":
                listWhere.add("mov.id_baixa IS NULL AND mov.dt_vencimento < '" + DataHoje.data() + "'");
                break;
            case "atrasadas_quitadas":
                listWhere.add("mov.id_baixa > 0 AND lot.dt_baixa > mov.dt_vencimento");
                break;
        }

        String data_mes = "extract(month from lot.dt_baixa)", data_ano = "extract(year from lot.dt_baixa)";
        // DATA DO RELATORIO ---------------------------------------------------------
        if (!listDateFilters.isEmpty()) {
            DateFilters importacao = DateFilters.getDateFilters(listDateFilters, "importacao");
            if (importacao != null) {
                listWhere.add("mov.id_baixa = lot.id ");
                if ((importacao.getDtStart() != null && !importacao.getStart().isEmpty()) || importacao.getType().equals("com") || importacao.getType().equals("sem")) {
                    switch (importacao.getType()) {
                        case "igual":
                            listWhere.add(" lot.dt_importacao = '" + importacao.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" lot.dt_importacao >= '" + importacao.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" lot.dt_importacao <= '" + importacao.getStart() + "'");
                            break;
                        case "faixa":
                            if (!importacao.getStart().isEmpty()) {
                                listWhere.add(" lot.dt_importacao BETWEEN '" + importacao.getStart() + "' AND '" + importacao.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" lot.dt_importacao IS NOT NULL ");
                            break;
                        case "null":
                            listWhere.add(" lot.dt_importacao IS NULL ");
                            break;
                        default:
                            break;
                    }
                }
                data_mes = "extract(month from lot.dt_importacao)";
                data_ano = "extract(year from lot.dt_importacao)";
            }
            DateFilters recebimento = DateFilters.getDateFilters(listDateFilters, "recebimento");
            if (recebimento != null) {
                if ((recebimento.getDtStart() != null && !recebimento.getStart().isEmpty()) || recebimento.getType().equals("com") || recebimento.getType().equals("sem")) {
                    switch (recebimento.getType()) {
                        case "igual":
                            listWhere.add(" lot.dt_baixa = '" + recebimento.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" lot.dt_baixa >= '" + recebimento.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" lot.dt_baixa <= '" + recebimento.getStart() + "'");
                            break;
                        case "faixa":
                            if (!recebimento.getFinish().isEmpty()) {
                                listWhere.add(" lot.dt_baixa BETWEEN '" + recebimento.getStart() + "' AND '" + recebimento.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" lot.dt_baixa IS NOT NULL");
                            break;
                        case "sem":
                            listWhere.add(" lot.dt_baixa IS NULL");
                            break;
                        default:
                            break;
                    }
                    data_mes = "extract(month from lot.dt_baixa)";
                    data_ano = "extract(year from lot.dt_baixa)";
                }
            }
            DateFilters vencimento = DateFilters.getDateFilters(listDateFilters, "vencimento");
            if (vencimento != null) {
                if ((vencimento.getDtStart() != null && !vencimento.getStart().isEmpty()) || vencimento.getType().equals("com") || vencimento.getType().equals("sem")) {
                    switch (vencimento.getType()) {
                        case "igual":
                            listWhere.add(" mov.dt_vencimento = '" + vencimento.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" mov.dt_vencimento >= '" + vencimento.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" mov.dt_vencimento <= '" + vencimento.getStart() + "'");
                            break;
                        case "faixa":
                            if (!vencimento.getFinish().isEmpty()) {
                                listWhere.add(" mov.dt_vencimento BETWEEN '" + vencimento.getStart() + "' AND '" + vencimento.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" mov.dt_vencimento IS NOT NULL");
                            break;
                        case "sem":
                            listWhere.add(" mov.dt_vencimento IS NULL");
                            break;
                        default:
                            break;
                    }
                    data_mes = "extract(month from mov.dt_vencimento)";
                    data_ano = "extract(year from mov.dt_vencimento)";
                }
            }
            DateFilters referencia = DateFilters.getDateFilters(listDateFilters, "referencia");
            if (referencia != null) {
                if ((referencia.getStart() != null && !referencia.getStart().isEmpty()) || referencia.getType().equals("com") || referencia.getType().equals("sem")) {

                    String ini = "";
                    try {
                        ini = referencia.getStart();
                    } catch (Exception e) {

                    }
                    String fin = "";
                    try {
                        fin = referencia.getFinish();
                    } catch (Exception e) {

                    }

                    switch (referencia.getType()) {
                        case "igual":
                            listWhere.add("mov.ds_referencia = '" + ini + "'");
                            break;
                        case "apartir":
                            listWhere.add("mov.ds_referencia >= '" + ini + "'");
                            break;
                        case "ate":
                            listWhere.add("mov.ds_referencia <= '" + ini + "'");
                            break;
                        case "faixa":
                            listWhere.add("mov.ds_referencia BETWEEN '" + ini + "' AND '" + fin + "'");
                            break;
                        default:
                            break;
                    }
                }
                data_mes = "cast(substring(mov.ds_referencia, 0, 3) as double precision)";
                data_ano = "cast(substring(mov.ds_referencia, 4, 8) as double precision)";
            }
        }

        if (!por_data.isEmpty()) {
            switch (por_data) {
//                case "importacao":
//                    listWhere.add("mov.id_baixa = lot.id ");
//                    if (!data_inicial.isEmpty() || !data_final.isEmpty()) {
//                        switch (tipo_data) {
//                            case "igual":
//                                listWhere.add("lot.dt_importacao = '" + data_inicial + "'");
//                                break;
//                            case "apartir":
//                                listWhere.add("lot.dt_importacao >= '" + data_inicial + "'");
//                                break;
//                            case "ate":
//                                listWhere.add("lot.dt_importacao <= '" + data_inicial + "'");
//                                break;
//                            case "faixa":
//                                listWhere.add("lot.dt_importacao BETWEEN '" + data_inicial + "' AND '" + data_final + "'");
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                    data_mes = "extract(month from lot.dt_importacao)";
//                    data_ano = "extract(year from lot.dt_importacao)";
//                    break;
//                    case "recebimento":
//                        listWhere.add("mov.id_baixa = lot.id ");
//                        if (!data_inicial.isEmpty() || !data_final.isEmpty()) {
//                            switch (tipo_data) {
//                                case "igual":
//                                    listWhere.add("lot.dt_baixa = '" + data_inicial + "'");
//                                    break;
//                                case "apartir":
//                                    listWhere.add("lot.dt_baixa >= '" + data_inicial + "'");
//                                    break;
//                                case "ate":
//                                    listWhere.add("lot.dt_baixa <= '" + data_inicial + "'");
//                                    break;
//                                case "faixa":
//                                    listWhere.add("lot.dt_baixa BETWEEN '" + data_inicial + "' AND '" + data_final + "'");
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                        data_mes = "extract(month from lot.dt_baixa)";
//                        data_ano = "extract(year from lot.dt_baixa)";
//                        break;
//                    case "vencimento":
//                        if (!data_inicial.isEmpty() || !data_final.isEmpty()) {
//                            switch (tipo_data) {
//                                case "igual":
//                                    listWhere.add("mov.dt_vencimento = '" + data_inicial + "'");
//                                    break;
//                                case "apartir":
//                                    listWhere.add("mov.dt_vencimento >= '" + data_inicial + "'");
//                                    break;
//                                case "ate":
//                                    listWhere.add("mov.dt_vencimento <= '" + data_inicial + "'");
//                                    break;
//                                case "faixa":
//                                    listWhere.add("mov.dt_vencimento BETWEEN '" + data_inicial + "' AND '" + data_final + "'");
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                        data_mes = "extract(month from mov.dt_vencimento)";
//                        data_ano = "extract(year from mov.dt_vencimento)";
//                        break;
//                case "referencia":
//                    String ini = "";
//                    try {
//                        ini = data_inicial.substring(3, 7) + data_inicial.substring(0, 2);
//                    } catch (Exception e) {
//
//                    }
//                    String fin = "";
//                    try {
//                        fin = data_final.substring(3, 7) + data_final.substring(0, 2);
//                    } catch (Exception e) {
//
//                    }
//
//                    if (!data_inicial.isEmpty() || !data_final.isEmpty()) {
//                        switch (tipo_data) {
//                            case "igual":
//                                listWhere.add("concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) = '" + ini + "'");
//                                break;
//                            case "apartir":
//                                listWhere.add("concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) >= '" + ini + "'");
//                                break;
//                            case "ate":
//                                listWhere.add("concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) <= '" + ini + "'");
//                                break;
//                            case "faixa":
//                                listWhere.add("concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) BETWEEN '" + ini + "' AND '" + fin + "'");
//                                break;
//                            default:
//                                break;
//                        }
//                    }
////                    listWhere.add("concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) >=  \'" + ini + "\'  "
////                            + " AND concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) <=  \'" + fin + "\'");
//                    data_mes = "cast(substring(mov.ds_referencia, 0, 3) as double precision)";
//                    data_ano = "cast(substring(mov.ds_referencia, 4, 8) as double precision)";
//                    break;
            }
        }

        if (!valor_baixa_inicial.isEmpty() && !valor_baixa_inicial.isEmpty() && Moeda.converteUS$(valor_baixa_inicial) >= 0 && Moeda.converteUS$(valor_baixa_final) > 0) {
            listWhere.add("mov.nr_valor_baixa BETWEEN " + Moeda.converteUS$(valor_baixa_inicial) + " AND " + Moeda.converteUS$(valor_baixa_final));
        } else if (!valor_baixa_inicial.isEmpty() && Moeda.converteUS$(valor_baixa_inicial) > 0) {
            listWhere.add("mov.nr_valor_baixa >= " + Moeda.converteUS$(valor_baixa_inicial));
        }

        // CONVENCAO DO RELATORIO ------------------------------------------------------------------------------------
        if (in_cnaes == null || in_cnaes.isEmpty()) {
            if (in_convencao != null && !in_convencao.isEmpty()) {
                listWhere.add("jur.id_cnae in (SELECT id_cnae from arr_cnae_convencao WHERE id_convencao IN (" + in_convencao + "))");
            }
        } else if (!in_cnaes.isEmpty()) {
            listWhere.add("jur.id_cnae IN (" + in_cnaes + ")");
        }

        // GRUPO CIDADE DO RELATORIO -----------------------------------------------------------------------------------
        if (in_grupo_cidades != null && !in_grupo_cidades.isEmpty()) {
            listWhere.add("pes_cidade.id in (SELECT id_cidade from arr_grupo_cidades WHERE id_grupo_cidade IN (" + in_grupo_cidades + "))");
        }

        // IDS CIDADES DA BASE -----------------------------------------------------------------------------------
        if (in_cidades_base != null && !in_cidades_base.isEmpty()) {
            listWhere.add("pes_cidade.id IN (" + in_cidades_base + ")");
        }

        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        String[] montaqry = objetoRelatorio(relatorios, data_mes, data_ano);
        queryString = montaqry[0] + queryString;

        // SE NÃO TER GRUPO
        if (montaqry[1].isEmpty()) {

        } else {
            queryString += "GROUP BY " + montaqry[1];
        }

        if (relatorioOrdem != null && relatorioOrdem.getId() != null) {
            queryString += " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
        } else if (montaqry[2].isEmpty()) {
            String ordem2;
            if (relatorios.getQryOrdem() == null || relatorios.getQryOrdem().isEmpty()) {
                ordem2 = " ORDER BY ";
            } else {
                ordem2 = " ORDER BY " + relatorios.getQryOrdem() + ", ";
            }

            // ORDEM DO RELATORIO --------------------------------------------------------
            if (in_empresas != null && !in_empresas.isEmpty()) {
                queryString += ordem2 + " pes.ds_nome, ";
                switch (relatorioOrdem.getQuery()) {
                    case "-1":
                        queryString += " mov.dt_vencimento \n ";
                        break;
                    case "-2":
                        queryString += " lot.dt_baixa \n ";
                        break;
                    case "-3":
                        queryString += " lot.dt_importacao \n ";
                        break;
                    case "-4":
                        queryString += " concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) \n ";
                        break;
                }
            } else {
                queryString += ordem2 + " pes.ds_nome, ";
                switch (relatorioOrdem.getQuery()) {
                    case "-1":
                        queryString += " mov.dt_vencimento \n ";
                        break;
                    case "-2":
                        queryString += " lot.dt_baixa \n ";
                        break;
                    case "-3":
                        queryString += " lot.dt_importacao \n ";
                        break;
                    case "-4":
                        queryString += " concatenar(substring(mov.ds_referencia, 4, 8), substring(mov.ds_referencia, 0, 3)) \n ";
                        break;
                }
            }

        } else {
            queryString += "ORDER BY " + montaqry[2];
        }

        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public String[] objetoRelatorio(Relatorios relatorio, String mes, String ano) {
        String select, group, order;

        if (relatorio.getId() == 66) {
            // RESUMO CONTRIBUICOES
            select = "-- RelatorioMovimentosDao - listaMovimentos;           \n\n"
                    + "SELECT " + mes + " as mes, \n "
                    + "      " + ano + " as ano, \n "
                    + "      se.ds_descricao as contribuicao, \n "
                    + "      sum(mov.nr_valor_baixa) as valor_recebido, \n "
                    + "      sum(mov.nr_taxa) as taxa, \n "
                    + "      sum(mov.nr_valor_baixa - (mov.nr_valor_baixa*(cc.nr_repasse/100)))-sum(mov.nr_taxa) as valor_liquido \n ";

            group = mes + ", " + ano + ", se.ds_descricao \n ";

            order = mes + ", " + ano + ",se.ds_descricao  \n ";
        } else if (relatorio.getId() == 67) {
            // RESUMO CONTRIBUICOES POR EMPRESA
            select = "-- RelatorioMovimentosDao - listaMovimentos;           \n\n"
                    + "SELECT " + mes + " as mes, \n "
                    + "      " + ano + " as ano, \n "
                    + "      pes.ds_documento as cnpj, \n "
                    + "      pes.ds_nome as empresa, \n "
                    + "      se.ds_descricao as contribuicao, \n "
                    + "      sum(mov.nr_valor_baixa) as valor_recebido, \n "
                    + "      sum(mov.nr_taxa) as taxa, \n "
                    + "      sum(mov.nr_valor_baixa - (mov.nr_valor_baixa*(cc.nr_repasse/100)))-sum(mov.nr_taxa) as valor_liquido \n ";

            group = "pes.ds_nome, \n "
                    + "pes.ds_documento, \n "
                    + mes + ", " + ano + ", se.ds_descricao \n ";

            order = "pes.ds_nome, \n "
                    + "pes.ds_documento, \n "
                    + mes + ", " + ano + ", se.ds_descricao \n ";
        } else if (relatorio.getId() == 68) {
            // RESUMO CONTRIBUICOES ANALITICO
            select = "-- RelatorioMovimentosDao - listaMovimentos;           \n\n"
                    + "SELECT " + mes + " as mes, \n "
                    + "      " + ano + " as ano, \n "
                    + "      se.ds_descricao as contribuicao, \n "
                    + "      sum(mov.nr_valor_baixa) as valor_recebido, \n "
                    + "      sum(mov.nr_taxa) as taxa, \n "
                    + "      sum(mov.nr_valor_baixa - (mov.nr_valor_baixa*(cc.nr_repasse/100)))-sum(mov.nr_taxa) as valor_liquido \n ";

            group = mes + ", " + ano + ", se.ds_descricao \n ";

            order = "se.ds_descricao, " + mes + ", " + ano + " \n ";
        } else if (relatorio.getId() == 78) {
            // RESUMO CONTRIBUICOES CIDADE
            select = "-- RelatorioMovimentosDao - listaMovimentos;           \n\n"
                    + "SELECT pes_cidade.ds_cidade as cidade, \n"
                    + mes + " as mes, \n "
                    + ano + " as ano, \n "
                    + "      se.ds_descricao as contribuicao, \n "
                    + "      sum(mov.nr_valor_baixa) as valor_recebido, \n "
                    + "      sum(mov.nr_taxa) as taxa, \n "
                    + "      sum(mov.nr_valor_baixa - (mov.nr_valor_baixa*(cc.nr_repasse/100)))-sum(mov.nr_taxa) as valor_liquido \n ";

            group = " pes_cidade.ds_cidade, " + mes + ", " + ano + ", se.ds_descricao ";

            order = " se.ds_descricao, pes_cidade.ds_cidade, " + mes + ", " + ano;
        } else {
            // OUTROS RELATORIOS
            select = ""
                    + "-- RelatorioMovimentosDao - listaMovimentos;           \n\n"
                    + "SELECT mov.id                       AS idMov,                \n "
                    + "       mov.ds_documento             AS numeroDocumento,      \n "
                    + "       se.ds_descricao              AS servico,              \n "
                    + "       ts.ds_descricao              AS tipoServico,          \n "
                    + "       mov.ds_referencia            AS referencia,           \n "
                    + "       mov.dt_vencimento            AS vencimento,           \n "
                    + "       mov.nr_valor                 AS valor,                \n "
                    + "       se.id                        AS idServico,            \n "
                    + "       ts.id                        AS idTipoServico,        \n "
                    + "       pes.id                       AS idPessoa,             \n "
                    + "       pes.ds_nome                  AS nomePessoa,           \n "
                    + "       pes_endereco.ds_descricao    AS enderecoPessoa,       \n "
                    + "       pes_logradouro.ds_descricao  AS logradouroPessoa,     \n "
                    + "       pes_pend.ds_numero           AS numeroPessoa,         \n "
                    + "       pes_pend.ds_complemento      AS complementoPessoa,    \n "
                    + "       pes_bairro.ds_descricao      AS bairroPessoa,         \n "
                    + "       pes_end.ds_cep               AS cepPessoa,            \n "
                    + "       pes_cidade.ds_cidade         AS cidadePessoa,         \n "
                    + "       pes_cidade.ds_uf             AS ufCidade,             \n "
                    + "       pes.ds_telefone1             AS telefonePessoa,       \n "
                    + "       pes.ds_email1                AS emailPessoa,          \n "
                    + "       pdoc.ds_descricao            AS tipoDocPessoa,        \n "
                    + "       pes.ds_documento             AS documentoPessoa,      \n "
                    + "       cnae.id                      AS idCnae,               \n "
                    + "       cnae.ds_numero               AS numeroCnae,           \n "
                    + "       cnae.ds_cnae                 AS nomeCnae,             \n "
                    + "       jur.id_contabilidade         AS idContabil,           \n "
                    + "       pesc.ds_nome                 AS nomeContabil,         \n "
                    + "       esc_endereco.ds_descricao    AS enderecoContabil,     \n "
                    + "       esc_logradouro.ds_descricao  AS logradouroContabil,   \n "
                    + "       esc_pend.ds_numero           AS numeroContabil,       \n "
                    + "       esc_pend.ds_complemento      AS complementoContabil,  \n "
                    + "       esc_bairro.ds_descricao      AS bairroContabil,       \n "
                    + "       esc_end.ds_cep               AS cepContabil,          \n "
                    + "       esc_cidade.ds_cidade         AS cidadeContabil,       \n "
                    + "       esc_cidade.ds_uf             AS ufCidade,             \n "
                    + "       pesc.ds_telefone1            AS telefoneContabil,     \n "
                    + "       pesc.ds_email1               AS emailContabil,        \n "
                    + "       lot.id                       AS idLote,               \n "
                    + "       lot.dt_baixa                 AS quitacaoLote,         \n "
                    + "       lot.dt_importacao            AS importacaoLote,       \n "
                    + "       jur.id                       AS idJuridica,           \n "
                    + "       upes.ds_nome                 AS usuario,              \n "
                    + "       mov.nr_taxa                  AS taxa,                 \n "
                    + "       mov.nr_multa                 AS multa,                \n "
                    + "       mov.nr_juros                 AS juros,                \n "
                    + "       mov.nr_correcao              AS correcao,             \n "
                    + "       mov.nr_valor_baixa           AS valor_baixa,          \n "
                    + "       cc.nr_repasse                AS vl_repasse            \n ";
            group = "";
            order = "";
        }

        String[] qry = new String[3];

        qry[0] = select;
        qry[1] = group;
        qry[2] = order;

        return qry;
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
