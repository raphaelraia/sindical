package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Debugs;
import br.com.rtools.utilitarios.Queries;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import oracle.toplink.essentials.config.TopLinkQueryHints;
import oracle.toplink.essentials.exceptions.EJBQLException;
import oracle.toplink.essentials.internal.weaving.TopLinkWeaver;
import oracle.toplink.essentials.sessions.Session;

public class RelatorioSociosDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    /**
     *
     *
     * @param in_tipo_cobranca
     * @param in_grupo_categoria
     * @param in_categoria
     * @param in_parentesco
     * @param in_cidade_socio
     * @param in_cidade_empresa
     * @param in_grupo_financeiro
     * @param in_subgrupo_financeiro
     * @param in_servicos
     * @param in_desconto_social
     * @param in_socios
     * @param in_alfabeto
     * @param in_cnaes
     * @param in_empresas
     * @param minQtdeFuncionario
     * @param maxQtdeFuncionario
     * @param foto
     * @param sexo
     * @param carteirinha
     * @param votante
     * @param email
     * @param telefone
     * @param estado_civil
     * @param empresa
     * @param carencia
     * @param situacao
     * @param desconto_folha
     * @param biometria
     * @param matricula_inicial
     * @param matricula_final
     * @param beneficio
     * @param idade_inicial
     * @param descontoSocialNenhum
     * @param descontoSocialPadrao
     * @param frequencia
     * @param idade_final
     * @param carencia_dias
     * @param contem_servicos
     * @param status
     * @param suspencao
     * @param oposicao
     * @param in_aniversario
     * @param dia_aniversario_inicial
     * @param dia_aniversario_final
     * @param ordemAniversario
     * @param listDateFilters
     * @param chk_validade_dependente
     * @param ref_validade_dependente_inicial
     * @param ref_validade_dependente_final
     * @return
     */
    public List find(
            /**
             * IN
             */
            String in_tipo_cobranca,
            String in_grupo_categoria,
            String in_categoria,
            String in_parentesco,
            String in_cidade_socio,
            String in_cidade_empresa,
            String in_grupo_financeiro,
            String in_subgrupo_financeiro,
            String in_servicos,
            String in_desconto_social,
            String in_socios,
            String in_alfabeto,
            String in_cnaes,
            /**
             * EMPRESAS
             */
            String in_empresas,
            String minQtdeFuncionario,
            String maxQtdeFuncionario,
            /**
             * TIPOS
             */
            String foto,
            String sexo,
            String carteirinha,
            String votante,
            String email,
            String telefone,
            String estado_civil,
            String empresa,
            String carencia,
            String situacao,
            String desconto_folha,
            String biometria,
            String suspencao,
            String oposicao,
            String beneficio,
            String frequencia,
            Boolean descontoSocialNenhum,
            Boolean descontoSocialPadrao,
            /**
             * OUTROS
             */
            String matricula_inicial,
            String matricula_final,
            String idade_inicial,
            String idade_final,
            Integer carencia_dias,
            Boolean contem_servicos,
            String status,
            /**
             * ANIVERSÁRIO
             */
            String in_aniversario,
            String dia_aniversario_inicial,
            String dia_aniversario_final,
            Boolean ordemAniversario,
            /**
             * DATAS
             */
            List<DateFilters> listDateFilters,
            /**
             * VALIDADE DEPENDENTE
             */
            Boolean chk_validade_dependente,
            String ref_validade_dependente_inicial,
            String ref_validade_dependente_final) {
        if (listDateFilters == null) {
            listDateFilters = new ArrayList();
        }
        String p_demissao;
        DateFilters demissao = DateFilters.getDateFilters(listDateFilters, "demissao");
        if (demissao != null && demissao.getDtStart() != null && demissao.getDtFinish() != null) {
            p_demissao = " , "
                    + " pempresa.admissao_empresa_demissionada,                 \n" // 79
                    + " pempresa.demissao_empresa_demissionada,                 \n" // 80
                    + " pempresa.cnpj_empresa_demissionada,                     \n" // 81
                    + " pempresa.empresa_demissionada                           \n "; // 82
        } else {
            p_demissao = " , "
                    + " dm.admissao  AS admissao_empresa_demissionada,          \n" // 79
                    + " dm.demissao  AS demissao_empresa_demissionada,          \n" // 80
                    + " dm.documento AS cnpj_empresa_demissionada,              \n" // 81
                    + " dm.empresa   AS empresa_demissionada                    \n "; // 82            
        }

        String queryString = " -- RelatorioSociosDao->find()                    \n"
                + "SELECT                                                       \n"
                + "           '' AS sindLogo,                                   \n" // 0
                + "           '' AS sindSite,                                   \n" // 1
                + "           '' AS sinnome,                                    \n" // 2
                + "           '' AS sinendereco,                                \n" // 3
                + "           '' AS sinlogradouro,                              \n" // 4
                + "           '' AS sinnumero,                                  \n" // 5
                + "           '' AS sincomplemento,                             \n" // 6
                + "           '' AS sinbairro,                                  \n" // 7
                + "           '' AS sincep,                                     \n" // 8
                + "           '' AS sincidade,                                  \n" // 9
                + "           '' AS sinuF,                                      \n" // 10
                + "           '' AS sindocumento,                               \n" // 11
                + "           P.codigo,                                         \n" // 12
                + "           P.cadastro,                                       \n" // 13
                + "           P.nome,                                           \n" // 14
                + "           P.cpf,                                            \n" // 15
                + "           P.telefone,                                       \n" // 16
                + "           P.ds_uf_emissao_rg,                               \n" // 17
                + "           P.estado_civil,                                   \n" // 18
                + "           P.ctps,                                           \n" // 19
                + "           P.pai,                                            \n" // 20
                + "           P.sexo,                                           \n" // 21
                + "           P.mae,                                            \n" // 22
                + "           P.nacionalidade,                                  \n" // 23
                + "           P.nit,                                            \n" // 24
                + "           P.ds_orgao_emissao_rg,                            \n" // 25
                + "           P.ds_pis,                                         \n" // 26
                + "           P.ds_serie,                                       \n" // 27
                + "           P.dt_aposentadoria,                               \n" // 28
                + "           P.ds_naturalidade,                                \n" // 29
                + "           P.recadastro,                                     \n" // 30
                + "           P.dt_nascimento,                                  \n" // 31
                + "           P.ds_foto,                                        \n" // 32
                + "           P.ds_rg,                                          \n" // 33
                + "           P.foto,                                           \n" // 34
                + "           P.logradouro,                                     \n" // 35
                + "           P.endereco,                                       \n" // 36
                + "           P.numero,                                         \n" // 37
                + "           P.complemento,                                    \n" // 38
                + "           P.bairro,                                         \n" // 39
                + "           P.cidade,                                         \n" // 40
                + "           P.uf,                                             \n" // 41
                + "           P.cep,                                            \n" // 42
                + "           P.setor,                                          \n" // 43
                + "           P.admissao,                                       \n" // 44
                + "           P.profissao,                                      \n" // 45
                + "           P.fantasia,                                       \n" // 46
                + "           P.empresa,                                        \n" // 47
                + "           P.cnpj,                                           \n" // 48
                + "           P.e_telefone,                                     \n" // 49
                + "           P.e_logradouro,                                   \n" // 50
                + "           P.e_endereco,                                     \n" // 51
                + "           P.e_numero,                                       \n" // 52
                + "           P.e_complemento,                                  \n" // 53
                + "           P.e_bairro,                                       \n" // 54
                + "           P.e_cidade,                                       \n" // 55
                + "           P.e_uf,                                           \n" // 56
                + "           P.e_cep,                                          \n" // 57
                + "           P.id_titular,                                     \n" // 58
                + "           P.codigo AS codsocio,                             \n" // 59
                + "           P.titular,                                        \n" // 60
                + "           P.parentesco,                                     \n" // 61
                + "           P.matricula,                                      \n" // 62
                + "           P.categoria,                                      \n" // 63
                + "           P.grupo_categoria,                                \n" // 64
                + "           P.filiacao,                                       \n" // 65
                + "           P.inativacao,                                     \n" // 66
                + "           P.votante,                                        \n" // 67
                + "           P.grau,                                           \n" // 68
                + "           P.nr_desconto,                                    \n" // 69
                + "           P.desconto_folha,                                 \n" // 70
                + "           P.tipo_cobranca,                                  \n" // 71
                + "           P.cod_tipo_cobranca,                              \n" // 72
                + "           P.telefone2,                                      \n" // 73
                + "           P.telefone3,                                      \n" // 74           
                + "           P.email,                                          \n" // 75
                + "           P.contabil_nome,                                  \n" // 76
                + "           P.contabil_contato,                               \n" // 77
                + "           P.contabil_telefone                               \n" // 78
                + "           " + p_demissao + ",                               \n "
                + "           func_idade(P.dt_nascimento,CURRENT_DATE) AS idade,\n" // 83
                + "           P.suspencao_motivo,                               \n" // 84
                + "           P.suspencao_inicial,                              \n" // 85
                + "           P.suspencao_final                                 \n" // 86
                + "      FROM pessoa_vw          AS p                           \n "
                // + " LEFT JOIN soc_socios_vw      AS so   ON so.codsocio     = p.codigo              \n "
                // + " LEFT JOIN pes_pessoa_vw      AS pt   ON pt.codigo       = so.titular            \n "
                + " LEFT JOIN demitidos_vw       AS dm   ON dm.id_pessoa = p.codigo                 \n ";
        // + " LEFT JOIN soc_suspencao      AS SS   ON SS.id_pessoa = p.codigo \n              \n ";
        if (status.equals("nao_socio")) {
            // queryString += " LEFT JOIN pes_juridica AS J ON J.id = P.e_id AND J.id IN(SELECT id_juridica FROM arr_contribuintes_vw WHERE dt_inativacao IS NULL ) \n ";
        } else {
            // queryString += " LEFT JOIN pes_juridica AS J ON J.id = P.e_id \n ";
        }
//        queryString += " LEFT JOIN pes_juridica       AS PJC  ON PJC.id          = J.id_contabilidade    \n "
//                + " LEFT JOIN pes_pessoa         AS PC   ON PC.id           = PJC.id_pessoa         \n ";

        if (demissao != null) {
            queryString += " INNER JOIN (                                           \n"
                    + "     SELECT id_fisica,                                       \n"
                    + "            PE.dt_admissao AS admissao_empresa_demissionada, \n"
                    + "            dt_demissao    AS demissao_empresa_demissionada, \n"
                    + "            P.ds_documento AS cnpj_empresa_demissionada,     \n"
                    + "            P.ds_nome      AS empresa_demissionada           \n"
                    + "       FROM pes_pessoa_empresa pe                            \n"
                    + " INNER JOIN pes_juridica J ON J.id = PE.id_juridica          \n"
                    + " INNER JOIN pes_pessoa   P ON P.id = J.id_pessoa             \n";
            queryString += "";
            if ((demissao.getDtStart() != null && !demissao.getStart().isEmpty()) || demissao.getType().equals("com") || demissao.getType().equals("sem")) {
                switch (demissao.getType()) {
                    case "igual":
                        queryString += " WHERE pe.dt_demissao = '" + demissao.getStart() + "' \n";
                        break;
                    case "apartir":
                        queryString += " WHERE pe.dt_demissao >= '" + demissao.getStart() + "' \n";
                        break;
                    case "ate":
                        queryString += " WHERE pe.dt_demissao <= '" + demissao.getStart() + "' \n";
                        break;
                    case "faixa":
                        if (!demissao.getStart().isEmpty()) {
                            queryString += " WHERE pe.dt_demissao BETWEEN  '" + demissao.getStart() + "' AND '" + demissao.getFinish() + "'\n";
                        }
                        break;
                    case "com":
                        queryString += " WHERE pe.dt_demissao IS NOT NULL \n";
                        break;
                    case "sem":
                        queryString += " WHERE pe.dt_demissao IS NULL \n";
                        break;
                }
                queryString += " ) AS pempresa ON pempresa.id_fisica = p.id_fisica \n ";
            }
        }

        List listWhere = new ArrayList();

        if (relatorios.getQry() != null && !relatorios.getQry().isEmpty()) {
            listWhere.add(relatorios.getQry());
        }

        if (relatorios.getId() != 13) {
            if (status.equals("nao_socio")) {
                listWhere.add("((p.inativacao IS NOT NULL AND p.matricula IS NULL) OR (p.matricula IS NULL))");
            } else if (status.equals("socio")) {
                // listWhere.add("p.inativacao IS NULL AND p.matricula IS NOT NULL");
            }
        }
        // MATRICULA -----------------

        if (status.equals("socio")) {
            if ((matricula_inicial != null && !matricula_inicial.isEmpty() && Integer.parseInt(matricula_inicial) > 0) && (matricula_final != null && !matricula_final.isEmpty() && Integer.parseInt(matricula_final) > 0)) {
                listWhere.add("p.matricula BETWEEN " + matricula_inicial + " AND " + matricula_final);
            } else {
                listWhere.add("p.matricula BETWEEN 0 AND 9999999");
            }
        } else if (status.equals("nao_socio")) {
            listWhere.add("P.matricula IS NULL");
        }

        if (suspencao != null && !suspencao.isEmpty()) {
            switch (suspencao) {
                case "com":
                    listWhere.add("CURRENT_DATE BETWEEN P.suspencao_inicial AND P.suspencao_final");
                    break;
                case "foram_suspensos":
                    listWhere.add("NOT CURRENT_DATE BETWEEN P.suspencao_inicial AND P.suspencao_final");
                    break;
                case "sem":
                    listWhere.add("P.codigo NOT IN (SELECT id_pessoa FROM soc_suspencao WHERE CURRENT_DATE BETWEEN dt_inicial AND dt_final )");
                    break;
                default:
                    break;
            }
        }
        if (oposicao != null && !oposicao.isEmpty()) {
            String referencia = DataHoje.DataToArray(DataHoje.dataHoje())[2] + DataHoje.DataToArray(DataHoje.dataHoje())[1];
            String subquery = "";
            if (oposicao.equals("com")) {
                subquery += " P.cpf <> '' AND exists \n";
            } else if (oposicao.equals("sem")) {
                subquery += " NOT exists \n";
            }
            subquery += "(  SELECT O.*                                                                                                                \n"
                    + "       FROM arr_oposicao AS O                                                                                                            \n"
                    + " INNER JOIN arr_convencao_periodo AS CP ON CP.id = O.id_convencao_periodo                                                                \n"
                    + " INNER JOIN arr_oposicao_pessoa AS OP ON OP.id = O.id_oposicao_pessoa                                                                    \n"
                    + "      WHERE ('" + referencia + "' >= CAST(SUBSTRING(CP.ds_referencia_inicial,4,4) || SUBSTRING(CP.ds_referencia_inicial,1,2)  AS int)    \n"
                    + "        AND '" + referencia + "' <= CAST(SUBSTRING(CP.ds_referencia_final,4,4) || SUBSTRING(CP.ds_referencia_final  ,1,2) AS int))       \n"
                    + "        AND OP.ds_cpf = P.cpf                                                                                                            \n"
                    + "        AND O.dt_inativacao IS NULL                                                                                                      \n"
                    + "      LIMIT 1                                                                                                                            \n"
                    + " ) ";
            listWhere.add(subquery);
        }

        //filtro += relatorio.getQry(); 
        // IDADE ---------------------
        if ((idade_inicial != null && !idade_inicial.isEmpty() && Integer.parseInt(idade_inicial) > 0) && (idade_final != null && !idade_final.isEmpty() && Integer.parseInt(idade_final) > 0)) {
            listWhere.add("extract(year FROM age(P.dt_nascimento)) >= " + idade_inicial + " AND extract(year FROM age(P.dt_nascimento)) <= " + idade_final + "");
        }

        // GRUPO CATEGORIA -----------
        if (in_grupo_categoria != null && !in_grupo_categoria.isEmpty()) {
            listWhere.add("P.id_grupo_categoria IN(" + in_grupo_categoria + ")");
        }

        if (in_categoria != null && !in_categoria.isEmpty()) {
            listWhere.add("P.id_categoria IN(" + in_categoria + ")");
        }
        if (in_socios != null && !in_socios.isEmpty()) {
            listWhere.add("P.codigo IN(" + in_socios + ")");
        }

        // SEXO ----------------------
        if (!sexo.isEmpty()) {
            listWhere.add("P.sexo = '" + sexo + "'");
        }

        // PARENTESCO ----------------
        if (in_parentesco != null && !in_parentesco.isEmpty()) {
            if (relatorios.getId() != 13) {
                if (status.equals("socio")) {
                    listWhere.add("P.id_parentesco IN(" + in_parentesco + ")");
                } else if (status.equals("nao_socio")) {
                    listWhere.add("P.id_parentesco IS NULL");
                }
            }
        }
        // TIPO DE COBRANÇA  ---------
        if (in_tipo_cobranca != null && !in_tipo_cobranca.isEmpty()) {
            listWhere.add("P.cod_tipo_cobranca IN(" + in_tipo_cobranca + ")");
        }
        // CIDADES SÓCIO  ------------------
        if (in_cidade_socio != null && !in_cidade_socio.isEmpty()) {
            listWhere.add("P.id_cidade IN(" + in_cidade_socio + ")");
        }
        // CIDADES EMPRESA -----------
        if (in_cidade_empresa != null && !in_cidade_empresa.isEmpty()) {
            listWhere.add("P.e_id_cidade IN(" + in_cidade_empresa + ")");
        }
        // DESCONTO SOCIAL  ----------

        if (in_desconto_social != null && !in_desconto_social.isEmpty()) {
            listWhere.add("P.id_desconto IN ( " + in_desconto_social + " ) ");
        }

        // DESCONTO NENHUM
        if (descontoSocialNenhum && descontoSocialPadrao) {
            listWhere.add("((P.nr_desconto = 0) OR (P.nr_desconto > 0 AND P.id_desconto = 1))");
        } else {
            if (descontoSocialNenhum) {
                listWhere.add("P.nr_desconto = 0");
            }

            // DESCONTO PADRÃO
            if (descontoSocialPadrao) {
                listWhere.add("P.nr_desconto > 0 AND P.id_desconto = 1");
            }
        }

        /**
         * TIPOS
         */
        if (foto.equals("com")) {
            listWhere.add(" P.ds_foto <> ''");

            String subquery = "";
            if (in_alfabeto != null && !in_alfabeto.isEmpty()) {
                String alfa[] = in_alfabeto.split(",");
                String alfaString = "";
                for (int i = 0; i < alfa.length; i++) {
                    if (i == 0) {
                        alfaString += "'" + alfa[i] + "'";
                    } else {
                        alfaString += ",'" + alfa[i] + "'";
                    }
                }
                subquery += " SUBSTR(UPPER(TRIM(P.nome)), 1, 1) IN (" + alfaString + ") ";
                listWhere.add(subquery);
            }

        } else if (foto.equals("sem")) {
            listWhere.add(" P.ds_foto = ''");
        }

        if (carteirinha.equals("com")) {
            String subquery = " "
                    + "P.codsocio IN (                              \n"
                    + "                 SELECT sc.id_pessoa          \n"
                    + "                   FROM soc_carteirinha AS SC \n";
            DateFilters validade_carteirinha = DateFilters.getDateFilters(listDateFilters, "validade_carteirinha");
            if (validade_carteirinha != null) {
                if (validade_carteirinha.getDtStart() != null && !validade_carteirinha.getStart().isEmpty()) {
                    switch (validade_carteirinha.getType()) {
                        case "igual":
                            subquery += " WHERE SC.dt_validade_carteirinha = '" + validade_carteirinha.getStart() + "' AND is_ativo = true \n";
                            break;
                        case "apartir":
                            subquery += " WHERE SC.dt_validade_carteirinha >= '" + validade_carteirinha.getStart() + "' AND is_ativo = true \n";
                            break;
                        case "ate":
                            subquery += " WHERE SC.dt_validade_carteirinha <= '" + validade_carteirinha.getStart() + "' AND is_ativo = true \n";
                            break;
                        case "faixa":
                            if (!validade_carteirinha.getStart().isEmpty()) {
                                subquery += " WHERE SC.dt_validade_carteirinha BETWEEN '" + validade_carteirinha.getStart() + "' AND '" + validade_carteirinha.getFinish() + "' AND is_ativo = true \n";
                            }
                            break;
                        default:
                            break;
                    }
                }
                subquery += " GROUP BY sc.id_pessoa ) \n";
                listWhere.add(subquery);

            }
        } else if (carteirinha.equals("sem")) {
            listWhere.add("P.codsocio NOT IN(SELECT sc.id_pessoa FROM soc_carteirinha AS sc GROUP BY sc.id_pessoa)");
        }

        if (email.equals("com")) {
            listWhere.add("P.email <> ''");
        } else if (email.equals("sem")) {
            listWhere.add("P.email = ''");
        }

        if (telefone.equals("com")) {
            listWhere.add("P.telefone <> ''");
        } else if (telefone.equals("sem")) {
            listWhere.add("P.telefone = ''");
        }

        if (!votante.isEmpty()) {
            switch (votante) {
                case "votante":
                    listWhere.add("P.votante = true");
                    break;
                default:
                    listWhere.add("P.votante = false");
                    break;
            }
        }

        if (!beneficio.isEmpty()) {
            String subQuery = "";
            switch (beneficio) {
                case "academia":
                case "clube":
                case "escola":
                case "hidroginastica":
                case "danca_salao":
                    Integer departamento_id = 0;
                    switch (beneficio) {
                        case "academia":
                            departamento_id = 11;
                            break;
                        case "clube":
                            departamento_id = 12;
                            break;
                        case "escola":
                            departamento_id = 13;
                            break;
                        case "hidroginastica":
                            departamento_id = 16;
                            break;
                        case "danca_salao":
                            departamento_id = 17;
                            break;
                        default:
                            break;
                    }
                    subQuery = " P.codsocio " + (frequencia.equals("nunca_utilizou") ? " NOT IN " : " IN ") + " (SELECT id_pessoa FROM soc_catraca_frequencia WHERE id_departamento = " + departamento_id + " ";
                    switch (frequencia) {
                        case "ontem":
                            subQuery += " AND dt_acesso = (current_date - 1) ";
                            break;
                        case "hoje":
                            subQuery += " AND dt_acesso = current_date ";
                            break;
                        case "mes_atual":
                            subQuery += " AND extract(month FROM dt_acesso) = extract(month FROM current_date) ";
                            break;
                        case "mes_passado":
                            subQuery += " AND extract(month FROM dt_acesso) = extract(month FROM (current_date - INTERVAL '1 month')) ";
                            break;
                        case "ano_atual":
                            subQuery += " AND extract(year FROM dt_acesso) = extract(year FROM current_date) ";
                            break;
                        case "ano_passado":
                            subQuery += " AND extract(year FROM dt_acesso) = extract(year FROM (current_date - INTERVAL '1 year')) ";
                            break;
                        default:
                            break;
                    }
                    subQuery += ")";
                    listWhere.add(subQuery);
                    break;
                case "caravana":
                    subQuery = " P.codsocio " + (frequencia.equals("nunca_utilizou") ? " NOT IN " : " IN ") + " ("
                            + " SELECT CR.id_pessoa FROM car_reservas CR"
                            + " INNER JOIN car_venda CV ON CV.id = CR.id_caravana_venda"
                            + " INNER JOIN car_caravana CAR ON CAR.id = CV.id_caravana ";
                    switch (frequencia) {
                        case "ontem":
                            subQuery += " AND CAR.dt_saida = (current_date - 1) ";
                            break;
                        case "hoje":
                            subQuery += " AND CAR.dt_saida = current_date ";
                            break;
                        case "mes_atual":
                            subQuery += " AND extract(month FROM CAR.dt_saida) = extract(month FROM current_date) ";
                            break;
                        case "mes_passado":
                            subQuery += " AND extract(month FROM CAR.dt_saida) = extract(month FROM (current_date - INTERVAL '1 month')) ";
                            break;
                        case "ano_atual":
                            subQuery += " AND extract(year FROM CAR.dt_saida) = extract(year FROM current_date) ";
                            break;
                        case "ano_passado":
                            subQuery += " AND extract(year FROM CAR.dt_saida) = extract(year FROM (current_date - INTERVAL '1 year')) ";
                            break;
                        default:
                            break;
                    }
                    subQuery += "AND CV.dt_cancelamento IS NULL ";
                    subQuery += ")";
                    listWhere.add(subQuery);
                    break;
                default:
                    break;
            }
        }

        if (estado_civil != null && !estado_civil.isEmpty()) {
            String ec[] = estado_civil.split(",");
            String ecString = "";
            for (int i = 0; i < ec.length; i++) {
                if (i == 0) {
                    ecString += "'" + ec[i] + "'";
                } else {
                    ecString += ",'" + ec[i] + "'";
                }
            }
            listWhere.add("P.estado_civil IN (" + ecString + ")");
        }

        if (biometria.equals("com")) {
            listWhere.add(" P.codigo IN (SELECT id_pessoa FROM pes_biometria WHERE is_ativo = TRUE) ");
        } else if (biometria.equals("sem")) {
            listWhere.add(" P.codigo NOT IN (SELECT id_pessoa FROM pes_biometria WHERE is_ativo = TRUE) ");
        }

        if (desconto_folha.equals("com")) {
            listWhere.add(" P.desconto_folha = true ");
        } else if (desconto_folha.equals("sem")) {
            listWhere.add(" P.desconto_folha = false ");
        }

        if (!in_aniversario.isEmpty()) {
            String subfiltro = " P.codigo IN (SELECT id_pessoa FROM pes_fisica WHERE ";
            subfiltro += "     extract(month from dt_nascimento) IN (" + in_aniversario + ")" + " \n ";

            subfiltro += " AND extract(day from dt_nascimento) >= " + Integer.valueOf(dia_aniversario_inicial) + " \n "
                    + " AND extract(day from dt_nascimento) <= " + Integer.valueOf(dia_aniversario_final) + " \n ";

            subfiltro += " ) ";
            listWhere.add(subfiltro);
        }

        if (!listDateFilters.isEmpty()) {
            DateFilters cadastro = DateFilters.getDateFilters(listDateFilters, "cadastro");
            if (cadastro != null) {
                if ((cadastro.getDtStart() != null && !cadastro.getStart().isEmpty()) || cadastro.getType().equals("com") || cadastro.getType().equals("sem")) {
                    switch (cadastro.getType()) {
                        case "igual":
                            listWhere.add(" P.cadastro = '" + cadastro.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" P.cadastro >= '" + cadastro.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" P.cadastro <= '" + cadastro.getStart() + "'");
                            break;
                        case "faixa":
                            if (!cadastro.getStart().isEmpty()) {
                                listWhere.add(" P.cadastro BETWEEN '" + cadastro.getStart() + "' AND '" + cadastro.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" P.cadastro IS NOT NULL ");
                            break;
                        case "null":
                            listWhere.add(" P.cadastro IS NULL ");
                            break;
                        default:
                            break;
                    }
                }
            }
            DateFilters recadastro = DateFilters.getDateFilters(listDateFilters, "recadastro");
            if (recadastro != null) {
                if ((recadastro.getDtStart() != null && !recadastro.getStart().isEmpty()) || recadastro.getType().equals("com") || recadastro.getType().equals("sem")) {
                    switch (recadastro.getType()) {
                        case "igual":
                            listWhere.add(" P.recadastro = '" + recadastro.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" P.recadastro >= '" + recadastro.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" P.recadastro <= '" + recadastro.getStart() + "'");
                            break;
                        case "faixa":
                            if (!recadastro.getFinish().isEmpty()) {
                                listWhere.add(" P.recadastro BETWEEN '" + recadastro.getStart() + "' AND '" + recadastro.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" P.recadastro IS NOT NULL");
                            break;
                        case "sem":
                            listWhere.add(" P.recadastro IS NULL");
                            break;
                        default:
                            break;
                    }
                }
            }
            DateFilters atualizacao = DateFilters.getDateFilters(listDateFilters, "atualizacao");
            if (atualizacao != null) {
                if ((atualizacao.getDtStart() != null && !atualizacao.getStart().isEmpty()) || atualizacao.getType().equals("com") || atualizacao.getType().equals("sem")) {
                    switch (atualizacao.getType()) {
                        case "igual":
                            listWhere.add(" P.dt_atualizacao = '" + atualizacao.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" P.dt_atualizacao >= '" + atualizacao.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" P.dt_atualizacao <= '" + atualizacao.getStart() + "'");
                            break;
                        case "faixa":
                            if (!atualizacao.getFinish().isEmpty()) {
                                listWhere.add(" P.dt_atualizacao BETWEEN '" + atualizacao.getStart() + "' AND '" + atualizacao.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" P.dt_atualizacao IS NOT NULL");
                            break;
                        case "sem":
                            listWhere.add(" P.dt_atualizacao IS NULL");
                            break;
                        default:
                            break;
                    }
                }
            }
            DateFilters filiacao = DateFilters.getDateFilters(listDateFilters, "filiacao");
            if (filiacao != null) {
                if ((filiacao.getDtStart() != null && !filiacao.getStart().isEmpty()) || filiacao.getType().equals("com") || filiacao.getType().equals("sem")) {
                    switch (filiacao.getType()) {
                        case "igual":
                            listWhere.add(" P.filiacao = '" + filiacao.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" P.filiacao >= '" + filiacao.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" P.filiacao <= '" + filiacao.getStart() + "'");
                            break;
                        case "faixa":
                            if (!filiacao.getFinish().isEmpty()) {
                                listWhere.add(" P.filiacao BETWEEN '" + filiacao.getStart() + "' AND '" + filiacao.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" P.filiacao IS NOT NULL");
                            break;
                        case "sem":
                            listWhere.add(" P.filiacao IS NULL");
                            break;
                        default:
                            break;
                    }
                }
            }
            DateFilters aposentadoria = DateFilters.getDateFilters(listDateFilters, "aposentadoria");
            if (aposentadoria != null) {
                if ((aposentadoria.getDtStart() != null && !aposentadoria.getStart().isEmpty()) || aposentadoria.getType().equals("com") || aposentadoria.getType().equals("sem")) {
                    switch (aposentadoria.getType()) {
                        case "igual":
                            listWhere.add(" P.dt_aposentadoria = '" + aposentadoria.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" P.dt_aposentadoria >= '" + aposentadoria.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" P.dt_aposentadoria <= '" + aposentadoria.getStart() + "'");
                            break;
                        case "faixa":
                            if (!aposentadoria.getFinish().isEmpty()) {
                                listWhere.add(" P.dt_aposentadoria BETWEEN '" + aposentadoria.getStart() + "' AND '" + aposentadoria.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" P.dt_aposentadoria IS NOT NULL");
                            break;
                        case "sem":
                            listWhere.add(" P.dt_aposentadoria IS NULL");
                            break;
                        default:
                            break;
                    }
                }
            }
            DateFilters admissao = DateFilters.getDateFilters(listDateFilters, "admissao");
            if (admissao != null) {
                if ((admissao.getDtStart() != null && !admissao.getStart().isEmpty() || admissao.getType().equals("com") || admissao.getType().equals("sem"))) {
                    switch (admissao.getType()) {
                        case "igual":
                            listWhere.add(" P.admissao = '" + admissao.getStart() + "'");
                            break;
                        case "apartir":
                            listWhere.add(" P.admissao >= '" + admissao.getStart() + "'");
                            break;
                        case "ate":
                            listWhere.add(" P.admissao <= '" + admissao.getStart() + "'");
                            break;
                        case "faixa":
                            if (!admissao.getFinish().isEmpty()) {
                                listWhere.add(" P.admissao BETWEEN '" + admissao.getStart() + "' AND '" + admissao.getFinish() + "'");
                            }
                            break;
                        case "com":
                            listWhere.add(" P.admissao IS NOT NULL ");
                            break;
                        case "sem":
                            listWhere.add(" P.admissao IS NULL ");
                            break;
                        default:
                            break;
                    }
                }
            }

        } else {
            listWhere.add("(p.principal = true OR p.principal IS NULL)");
        }

        if (empresa != null && !empresa.isEmpty()) {
            if (!in_empresas.equals("-1") && empresa.equals("especificas")) {
                if (relatorios.getId() == 46) {
                    listWhere.add("dm.id_juridica IN (" + in_empresas + ")");
                } else {
                    listWhere.add("P.e_id IN (" + in_empresas + ")");
                }
            } else if (empresa.equals("com")) {
                if (relatorios.getId() == 46) {
                    listWhere.add("dm.empresa <> '' ");
                } else {
                    listWhere.add("P.empresa <> '' ");
                }
                // CNAES
                if (!in_cnaes.isEmpty()) {
                    listWhere.add("(J.id_cnae IS NOT NULL AND J.id_cnae IN(" + in_cnaes + "))");
                }
            } else if (empresa.equals("sem")) {
                listWhere.add("J.id IS NULL ");
            }
            if (empresa.equals("com")) {
                if (minQtdeFuncionario != null && maxQtdeFuncionario != null && (Integer.parseInt(minQtdeFuncionario) > 0 || Integer.parseInt(maxQtdeFuncionario) > 0)) {
                    String subfiltro = " P.e_id IN (SELECT pempre.e_id "
                            + "         FROM soc_socios_vw socp  "
                            + " INNER JOIN pes_pessoa_vw pempre ON pempre.codigo = socp.codsocio    "
                            + " WHERE pempre.e_id > 0  "
                            + " GROUP BY pempre.e_id  ";
                    if (minQtdeFuncionario.equals(maxQtdeFuncionario)) {
                        subfiltro += " HAVING COUNT(*) = " + minQtdeFuncionario + " ";
                    } else if (Integer.parseInt(minQtdeFuncionario) > 0 && Integer.parseInt(maxQtdeFuncionario) == 0) {
                        subfiltro += " HAVING COUNT(*) <= " + minQtdeFuncionario + " ";
                    } else if (Integer.parseInt(minQtdeFuncionario) >= 0 && Integer.parseInt(maxQtdeFuncionario) > 0) {
                        subfiltro += " HAVING COUNT(*) >= " + minQtdeFuncionario + " AND COUNT(*) <= " + maxQtdeFuncionario + " ";
                    }
                    subfiltro += " )";
                    listWhere.add(subfiltro);
                }
            }
        }

        if (carencia_dias != null && carencia_dias >= 0) {
            Boolean s = false;
            if (situacao != null) {
                s = !situacao.equals("adimplente");
            }
            switch (carencia) {
                case "eleicao":
                    listWhere.add("func_inadimplente_eleicao(P.codsocio, " + carencia_dias + ") = " + s);
                    break;
                case "clube":
                    listWhere.add("func_inadimplente_clube(P.codsocio, " + carencia_dias + ") = " + s);
                    break;
                default:
                    listWhere.add("func_inadimplente(P.codsocio, " + carencia_dias + ") = " + s + "");
                    break;
            }
        }

        if ((in_grupo_financeiro != null && !in_grupo_financeiro.isEmpty()) || (in_subgrupo_financeiro != null && !in_subgrupo_financeiro.isEmpty()) || (in_servicos != null && !in_servicos.isEmpty()) && contem_servicos != null) {
            String subquery = "";
            if (contem_servicos != null) {
                if (contem_servicos) {
                    subquery += " p.codigo IN ";
                } else {
                    subquery += " p.codigo NOT IN ";
                }
            }
            subquery
                    += " (                                                              \n"
                    + "     SELECT id_pessoa                                            \n"
                    + "       FROM fin_servico_pessoa   AS SP                           \n"
                    + " INNER JOIN fin_servicos         AS SE ON SE.id = SP.id_servico  \n"
                    + "  LEFT JOIN fin_subgrupo         AS SB ON SB.id = SE.id_subgrupo \n"
                    + "  LEFT JOIN fin_grupo            AS G  ON G.id  = SB.id_grupo    \n"
                    + "      WHERE SE.ds_situacao = 'A'                                 \n";

            if (in_servicos != null && !in_servicos.isEmpty()) {
                subquery += " AND SE.id IN (" + in_servicos + ")                        \n";
            } else if (in_subgrupo_financeiro != null && !in_subgrupo_financeiro.isEmpty()) {
                subquery += " AND SB.id IN (" + in_subgrupo_financeiro + ")             \n";
            } else if (in_grupo_financeiro != null && !in_grupo_financeiro.isEmpty()) {
                subquery += " AND G.id IN (" + in_grupo_financeiro + ")                 \n";
            }
            subquery += " ) \n";
            listWhere.add(subquery);
        }

        if (chk_validade_dependente != null) {
            if (chk_validade_dependente) {
                listWhere.add(" (P.validade IS NULL OR P.validade = '')");
            } else if (ref_validade_dependente_inicial != null && ref_validade_dependente_final == null) {
                String r_ini = ref_validade_dependente_inicial.substring(3, 7) + ref_validade_dependente_inicial.substring(0, 2);
                listWhere.add(" SUBSTRING(P.validade, 4, 8) || SUBSTRING(P.validade, 0, 3) >= '" + r_ini + "'");
            } else if (ref_validade_dependente_inicial == null && ref_validade_dependente_final != null) {
                String r_fim = ref_validade_dependente_final.substring(3, 7) + ref_validade_dependente_final.substring(0, 2);
                listWhere.add(" SUBSTRING(P.validade, 4, 8) || SUBSTRING(P.validade, 0, 3) <= '" + r_fim + "'");
            } else {
                String r_ini = ref_validade_dependente_inicial.substring(3, 7) + ref_validade_dependente_inicial.substring(0, 2);
                String r_fim = ref_validade_dependente_final.substring(3, 7) + ref_validade_dependente_final.substring(0, 2);
                listWhere.add(" SUBSTRING(P.validade, 4, 8) || SUBSTRING(P.validade, 0, 3) >= '" + r_ini + "'");
                listWhere.add(" SUBSTRING(P.validade, 4, 8) || SUBSTRING(P.validade, 0, 3) <= '" + r_fim + "'");
            }
        }

        String ordem = "";

        if (ordemAniversario) {
            ordem += " extract(day from p.dt_nascimento), extract(month from p.dt_nascimento), extract(year from p.dt_nascimento) ";
        }
        // listWhere.add(" P.codigo <> 2158 ");
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
//
        if (relatorioOrdem != null && ordem.isEmpty()) {
            ordem = " ORDER BY " + relatorioOrdem.getQuery();
//            tordem += tordem.isEmpty() ? " p.nome " : ", p.nome ";
//            
//            }
        } else {
            ordem = " ORDER BY " + ordem;
        }
        try {
//            Queries queries = new Queries();
//            queries.select("P.id");
//            queries.selectGroup("P.id", "codigo");
//            queries.from("P.id");
//            queries.join("P.id");
//            String q = queries.createQuery();

            queryString = Queries.get(queryString);
            Debugs.put("habilitaDebugQuery", queryString);
            Query query = getEntityManager().createNativeQuery(queryString);
            // query.setMaxResults(206);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List listaSociosInativos(Boolean comDependentes, String dt_inativacao_i, String dt_inativacao_f, String dt_filiacao_i, String dt_filiacao_f, Integer categoria_id, Integer grupo_categoria_id, String ordernarPor, String status, String in_motivo_inativacao) {

        String select, innerjoin = "", textQry, and = "", orderby, ordem = "";

        if (comDependentes != null && comDependentes) {
            select = "  SELECT P.ds_nome AS titular,    \n" // 0
                    + "        S.titular AS codtitular, \n" // 1
                    + "        S.codsocio,              \n" // 2
                    + "        S.nome,                  \n" // 3
                    + "        S.parentesco,            \n" // 4
                    + "        S.matricula,             \n" // 5
                    + "        S.categoria,             \n" // 6
                    + "        S.filiacao,              \n" // 7
                    + "        S.inativacao,            \n" // 8
                    + "        S.motivo_inativacao,     \n" // 9
                    + "        S.id_categoria,          \n" // 10
                    + "        S.id_grupo_categoria,    \n" // 11
                    + "        DE.documento,            \n" // 12
                    + "        DE.empresa               \n"; // 13
            innerjoin = " INNER JOIN pes_pessoa AS P ON P.id = S.titular \n";
            innerjoin += " LEFT JOIN demitidos_vw AS DE ON DE.id_pessoa = S.codsocio \n";
            orderby = " P.ds_nome,      \n"
                    + " S.titular,      \n"
                    + " S.categoria,    \n"
                    + " S.matricula,    \n"
                    + " S.id_parentesco,\n"
                    + " S.nome,         \n"
                    + " S.parentesco    \n";

            switch (ordernarPor) {
                case "matricula":
                    ordem = " S.matricula, \n";
                    break;
                case "categoria":
                    ordem = " S.categoria, \n";
                    break;
                case "inativacao":
                    ordem = " S.inativacao DESC, \n";
                    break;
                case "filiacao":
                    ordem = " S.filiacao DESC, \n";
                    break;
                case "empresa":
                    ordem = " DE.empresa ASC, \n";
                    break;
                default:
                    break;
            }

        } else {
            select = "SELECT S.nome AS titular,         \n" // 0
                    + "      S.codsocio AS codtitular,  \n" // 1
                    + "      S.codsocio,                \n" // 2
                    + "      S.nome,                    \n" // 3
                    + "      S.parentesco,              \n" // 4
                    + "      S.matricula,               \n" // 5
                    + "      S.categoria,               \n" // 6
                    + "      S.filiacao,                \n" // 7
                    + "      S.inativacao,              \n" // 8
                    + "      S.motivo_inativacao,       \n" // 9
                    + "      S.id_categoria,            \n" // 10
                    + "      S.id_grupo_categoria,      \n" // 11
                    + "      DE.documento,              \n" // 12
                    + "      DE.empresa                 \n"; // 13;
            and = "    WHERE S.parentesco = 'TITULAR'   \n";
            innerjoin += " LEFT JOIN demitidos_vw AS DE ON DE.id_pessoa = S.codsocio \n";
            orderby = " S.inativacao DESC ";

            switch (ordernarPor) {
                case "filiacao":
                    ordem = " S.filiacao DESC, \n";
                    break;
                case "nome":
                    ordem = " S.nome, \n";
                    break;
                case "matricula":
                    ordem = " S.matricula, \n";
                    break;
                case "categoria":
                    ordem = " S.categoria, \n";
                    break;
                case "empresa":
                    ordem = " DE.empresa, \n";
                    break;
                default:
                    break;
            }
        }

        orderby = " ORDER BY " + ordem + orderby;

        if (dt_inativacao_i != null && dt_inativacao_f != null) {
            if (!dt_inativacao_i.isEmpty() && dt_inativacao_f.isEmpty()) {
                and += " AND S.inativacao >= '" + dt_inativacao_i + "'";
            } else if (dt_inativacao_i.isEmpty() && !dt_inativacao_f.isEmpty()) {
                and += " AND S.inativacao <= '" + dt_inativacao_f + "'";
            } else if (!dt_inativacao_i.isEmpty() && !dt_inativacao_f.isEmpty()) {
                and += " AND S.inativacao >= '" + dt_inativacao_i + "' AND S.inativacao <= '" + dt_inativacao_f + "'";
            }
        }

        if (dt_filiacao_i != null && dt_filiacao_f != null) {
            if (!dt_filiacao_i.isEmpty() && dt_filiacao_f.isEmpty()) {
                and += " AND S.filiacao >= '" + dt_filiacao_i + "'";
            } else if (dt_filiacao_i.isEmpty() && !dt_filiacao_f.isEmpty()) {
                and += " AND S.filiacao <= '" + dt_filiacao_f + "'";
            } else if (!dt_filiacao_i.isEmpty() && !dt_filiacao_f.isEmpty()) {
                and += " AND S.filiacao >= '" + dt_filiacao_i + "' AND S.filiacao <= '" + dt_filiacao_f + "'";
            }
        }

        if (categoria_id != null) {
            and += " AND S.id_categoria = " + categoria_id;
        }

        if (grupo_categoria_id != null) {
            and += " AND S.id_grupo_categoria = " + grupo_categoria_id;
        }

        if (status != null) {
            if (status.equals("inativos_hoje_ativos")) {
                and += " AND S.codsocio IN (SELECT codsocio FROM SOC_SOCIOS_VW ) \n";
            } else {
                and += " AND S.codsocio NOT IN (SELECT codsocio FROM SOC_SOCIOS_VW ) \n";
            }
        }

        if (in_motivo_inativacao != null) {
            and += " AND S.id_motivo IN ( " + in_motivo_inativacao + ") \n";
        }

        textQry = select
                + "  FROM soc_socios_inativos_vw S"
                + innerjoin
                + and
                + orderby;

        try {
            Debugs.put("habilitaDebugQuery", textQry);
            Query qry = getEntityManager().createNativeQuery(textQry);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List listaEmpresaDoSocio() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select pe.juridica "
                    + "  from PessoaEmpresa pe"
                    + " where pe.fisica.id in "
                    + "(select f.id "
                    + "   from Fisica f, Socios s"
                    + "  where s.servicoPessoa.pessoa.id = f.pessoa.id "
                    + "  group by f.id) "
                    + " group by pe.juridica, pe.juridica.pessoa.nome order by pe.juridica.pessoa.nome");
            result = qry.getResultList();
        } catch (EJBQLException e) {
        }
        return result;
    }

    public List listaCidadeDoSocio() {
        List result = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select c from Cidade c"
                    + " where c.id in (select e.cidade.id"
                    + "                  from Endereco e"
                    + "                 where e.id in (select pe.endereco.id"
                    + "                                  from PessoaEndereco pe"
                    + "                                 where pe.pessoa.id in (select s.servicoPessoa.pessoa.id"
                    + "                                                          from Socios s"
                    + "                                                         group by s.servicoPessoa.pessoa.id"
                    + "                                                       )"
                    + "                                   and pe.tipoEndereco.id = 1"
                    + "                                 group by pe.endereco.id"
                    + "                                )"
                    + "                 group by e.cidade.id"
                    + "                ) order by c.cidade ASC ";
            Query qry = getEntityManager().createQuery(textQuery);
            result = qry.getResultList();
        } catch (EJBQLException e) {
        }
        return result;
    }

    public List listaCidadeDaEmpresa() {
        List result = new ArrayList();
        String textQuery = "";
        try {
            textQuery = "select c from Cidade c"
                    + " where c.id in (select e.cidade.id"
                    + "                  from Endereco e"
                    + "                 where e.id in (select pe.endereco.id"
                    + "                                  from PessoaEndereco pe"
                    + "                                 where pe.pessoa.id in (select pem.juridica.pessoa.id "
                    + "  from PessoaEmpresa pem"
                    + " where pem.fisica.id in "
                    + "(select f.id "
                    + "   from Fisica f, Socios s"
                    + "  where s.servicoPessoa.pessoa.id = f.pessoa.id "
                    + "  group by f.id) "
                    + " group by pem.juridica.pessoa.id "
                    + ")"
                    + "                                   and pe.tipoEndereco.id = 5"
                    + "                                 group by pe.endereco.id"
                    + "                                )"
                    + "                 group by e.cidade.id"
                    + "                ) ORDER BY C.cidade ASC ";
            Query qry = getEntityManager().createQuery(textQuery);
            result = qry.getResultList();
        } catch (EJBQLException e) {
        }
        return result;
    }

    public List listaSPSocios() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select sp.servicos "
                    + "  from ServicoPessoa sp"
                    + " where sp.id in (select s.servicoPessoa.id "
                    + "                   from Socios s)"
                    + " group by sp.servicos");
            result = qry.getResultList();
        } catch (EJBQLException e) {
        }
        return result;
    }

    public List listaSPAcademia() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select sp.servicos "
                    + "  from ServicoPessoa sp"
                    + " where sp.id in (select m.servicoPessoa.id "
                    + "                   from MatriculaAcademia m)"
                    + " group by sp.servicos");
            result = qry.getResultList();
        } catch (EJBQLException e) {
        }
        return result;
    }

    public List listaSPEscola() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select s "
                    + "  from Servicos s"
                    + " where s.id in (select mi.curso.id from MatriculaIndividual mi group by mi.curso.id)"
                    + "    or s.id in (select t.cursos.id from Turma t group by t.cursos.id)"
                    + " group by s");
            result = qry.getResultList();
        } catch (EJBQLException e) {
        }
        return result;
    }

    public List listaSPConvenioMedico() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select sp.servicos "
                    + "  from ServicoPessoa sp"
                    + " where sp.id in (select m.servicoPessoa.id "
                    + "                   from MatriculaConvenioMedico m)"
                    + " group by sp.servicos");
            result = qry.getResultList();
        } catch (EJBQLException e) {
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
