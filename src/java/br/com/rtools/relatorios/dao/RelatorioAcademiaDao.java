package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioAcademiaDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    /**
     * 0 - NOME 1 - IDADE 2 - NASCIMENTO 3 - SEXO 4 - CIDADE 5 - RESPONSÁVEL 6 -
     * SERVIÇO 7 - PERÍODO - 8 EMISSÃO
     *
     * @param emissaoInicial
     * @param emissaoFinal
     * @param idResponsavel
     * @param idAluno
     * @param inModalidade
     * @param inIdPeriodos
     * @param inSexo
     * @param periodo
     * @param matricula_situacao
     * @param order
     * @param in_grupo_categoria
     * @param in_categoria
     * @param nao_socio
     * @param convenio_empresa
     * @param idade
     * @param desconto
     * @param desconto_final
     * @param tipoCarencia
     * @param situacaoFinanceira
     * @param vencimentoInicial
     * @param vencimentoFinal
     * @param quitacaoInicial
     * @param carenciaDias
     * @param quitacaoFinal
     * @param situacao
     * @param tipoValor
     * @param valorInicial
     * @param valorFinal
     * @return
     */
    public List find(String emissaoInicial, String emissaoFinal, Integer idResponsavel, Integer idAluno, String inModalidade, String inIdPeriodos, String inSexo, String periodo, String matricula_situacao, Integer[] idade, String in_grupo_categoria, String in_categoria, Boolean nao_socio, Boolean convenio_empresa, Float desconto, Float desconto_final, String tipoCarencia, Integer carenciaDias, String situacao, String situacaoFinanceira, String vencimentoInicial, String vencimentoFinal, String quitacaoInicial, String quitacaoFinal, String order, String tipoValor, String valorInicial, String valorFinal) {
        List listWhere = new ArrayList();
        String queryString = "";
        if (relatorios.getId().equals(31)) {
            queryString = " -- RelatorioAcademiaDao->find(Cadastral)                                \n"
                    + "     SELECT PA.nome AS aluno_nome,                                           \n" // 0 - NOME
                    + "            func_idade(PA.dt_nascimento, current_date)  AS idade,            \n" // 1 - IDADE
                    + "            PA.dt_nascimento                            AS nascimento,       \n" // 2 - NASCIMENTO
                    + "            PA.sexo,                                                         \n" // 3 - SEXO
                    + "            PA.cidade,                                                       \n" // 4 - CIDADE
                    + "            PR.ds_nome                                  AS responsavel_nome, \n" // 5 - RESPONSÁVEL
                    + "            S.ds_descricao                              AS servico,          \n" // 6 - SERVIÇO
                    + "            P.ds_descricao                              AS periodo,          \n" // 7 - PERÍODO
                    + "            SP.dt_emissao                               AS emissao,          \n" // 8 - EMISSÃO
                    + "            A.dt_inativo                                AS inativacao,       \n" // 9 - INATIVAÇÃO
                    + "            CASE WHEN SP.nr_desconto = 0                                     \n"
                    + "                 THEN round(                                                 \n"
                    + "                     CAST(                                                   \n"
                    + "                         (                                                   \n"
                    + "                             func_valor_servico(                             \n"
                    + "                                 SP.id_pessoa,                               \n"
                    + "                                 SP.id_servico,                              \n"
                    + "                                 current_date,                               \n"
                    + "                                 0,                                          \n"
                    + "                                 SOC.id_categoria)                           \n"
                    + "                         )                                                   \n"
                    + "                     AS numeric), 2                                          \n"
                    + "            ) ELSE round(                                                    \n"
                    + "                     CAST(                                                   \n"
                    + "                             func_valor_servico_cheio(                       \n"
                    + "                                 SP.id_pessoa,                               \n"
                    + "                                 SP.id_servico, current_date) * ( 1 - (SP.nr_desconto/100) ) \n"
                    + "                     AS numeric), 2) "
                    + "            END AS valor                                                      \n"
                    + "       FROM matr_academia AS A                                                \n"
                    + " INNER JOIN fin_servico_pessoa   AS SP  ON SP.id         = A.id_servico_pessoa\n"
                    + " INNER JOIN pes_fisica_vw        AS PA  ON PA.codigo     = SP.id_pessoa       \n"
                    + " INNER JOIN pes_pessoa           AS PR  ON PR.id         = SP.id_cobranca     \n"
                    + "  LEFT JOIN soc_socios_vw        AS SOC ON SOC.codsocio  = SP.id_pessoa       \n"
                    + " INNER JOIN aca_servico_valor    AS ASV ON ASV.id        = A.id_servico_valor \n"
                    + " INNER JOIN fin_servicos         AS S   ON S.id          = ASV.id_servico     \n"
                    + " INNER JOIN sis_periodo          AS P   ON P.id          = ASV.id_periodo     \n"
                    + "  LEFT JOIN                                                                   \n"
                    + "  (SELECT SP.id_pessoa, id_servico                                            \n"
                    + "      FROM fin_servico_pessoa    AS SP                                        \n"
                    + " INNER JOIN matr_academia        AS M    ON M.id_servico_pessoa = SP.id       \n"
                    + "     WHERE SP.is_ativo = true                                                 \n"
                    + " ) AS MA ON MA.id_pessoa = SP.id_pessoa AND MA.id_servico = SP.id_servico     \n";
            if (convenio_empresa != null && convenio_empresa) {
                queryString += " INNER JOIN fin_desconto_servico_empresa AS FDSE ON FDSE.id_juridica = PA.id_juridica AND FDSE.id_servico = SP.id_servico ";
                listWhere.add("SP.id_pessoa NOT IN (SELECT SOCVW.codsocio FROM soc_socios_vw AS SOCVW GROUP BY SOCVW.codsocio )");
            }
        } else if (relatorios.getId().equals(32)) {
            queryString = " -- RelatorioAcademiaDao->find(Financeiro)                               \n"
                    + "     SELECT PA.nome AS anulo_nome,                                           \n" // 0 - NOME
                    + "            func_idade(PA.dt_nascimento, current_date)  AS idade,            \n" // 1 - IDADE
                    + "            PA.dt_nascimento                            AS nascimento,       \n" // 2 - NASCIMENTO
                    + "            PA.sexo,                                                         \n" // 3 - SEXO
                    + "            PA.cidade,                                                       \n" // 4 - CIDADE
                    + "            PR.ds_nome                                  AS responsavel_nome, \n" // 5 - RESPONSÁVEL
                    + "            S.ds_descricao                              AS servico,          \n" // 6 - SERVIÇO
                    + "            P.ds_descricao                              AS periodo,          \n" // 7 - PERÍODO
                    + "            SP.dt_emissao                               AS emissao,          \n" // 8 - EMISSÃO
                    + "            A.dt_inativo                                AS inativacao,       \n" // 9 - INATIVAÇÃO
                    + "            MV.dt_vencimento                            AS data_vencimento,  \n" // 10 - DATA VENCIMENTO
                    + "            B.dt_baixa                                  AS data_baixa,       \n" // 11 - DATA BAIXA
                    + "            MV.nr_valor                                 AS valor,            \n" // 12 - VALOR
                    + "            MV.nr_valor_baixa                           AS valor_baixa,      \n" // 13 - VALOR BAIXA
                    + "            SP.nr_desconto                              AS desconto          \n" // 14 - DESCONTO
                    + "       FROM fin_movimento AS MV                                              \n"
                    + "  LEFT JOIN fin_baixa            AS B   ON B.id          = MV.id_baixa       \n"
                    + " INNER JOIN fin_servico_pessoa   AS SP  ON SP.id_pessoa  = MV.id_pessoa          \n"
                    + "                                           AND SP.id_servico = MV.id_servicos    \n"
                    + "                                           AND SP.is_ativo = true                \n"
                    + "                                           AND MV.is_ativo                       \n"
                    + " INNER JOIN pes_fisica_vw        AS PA  ON PA.codigo     = SP.id_pessoa          \n"
                    + " INNER JOIN pes_pessoa           AS PR  ON PR.id         = SP.id_cobranca        \n"
                    + "  LEFT JOIN soc_socios_vw        AS SOC ON SOC.codsocio  = SP.id_pessoa          \n"
                    + " INNER JOIN matr_academia        AS A   ON A.id_servico_pessoa = SP.id           \n"
                    + " INNER JOIN aca_servico_valor    AS ASV ON ASV.id        = A.id_servico_valor    \n"
                    + " INNER JOIN fin_servicos         AS S   ON S.id          = ASV.id_servico        \n"
                    + " INNER JOIN sis_periodo          AS P   ON P.id          = ASV.id_periodo        \n"
                    + "  LEFT JOIN                                                                      \n"
                    + "  (SELECT SP.id_pessoa, id_servico                                               \n"
                    + "      FROM fin_servico_pessoa    AS SP                                           \n"
                    + " INNER JOIN matr_academia        AS M    ON M.id_servico_pessoa = SP.id          \n"
                    + "     WHERE SP.is_ativo = true                                                    \n"
                    + " ) AS MA ON MA.id_pessoa = SP.id_pessoa AND MA.id_servico = SP.id_servico        \n";
            if (convenio_empresa != null && convenio_empresa) {
                queryString += " INNER JOIN fin_desconto_servico_empresa AS FDSE ON FDSE.id_juridica = PA.id_juridica AND FDSE.id_servico = SP.id_servico ";
                listWhere.add("SP.id_pessoa NOT IN (SELECT SOCVW.codsocio FROM soc_socios_vw AS SOCVW GROUP BY SOCVW.codsocio )");
            }
        }
        String emissaoInativacaoString = "";
        if (periodo != null) {
            switch (periodo) {
                case "emissao":
                    emissaoInativacaoString = " SP.dt_emissao ";
                    break;
                case "inativacao":
                    emissaoInativacaoString = " A.dt_inativo ";
                    break;
            }
            if (!emissaoInicial.isEmpty() && !emissaoFinal.isEmpty()) {
                listWhere.add(emissaoInativacaoString + "BETWEEN '" + emissaoInicial + "' AND '" + emissaoFinal + "'");
            } else if (!emissaoFinal.isEmpty()) {
                listWhere.add(emissaoInativacaoString + " = '" + emissaoInicial + "'");
            } else if (!emissaoFinal.isEmpty()) {
                listWhere.add(emissaoInativacaoString + " = '" + emissaoFinal + "'");
            } else if (emissaoInicial.isEmpty() || emissaoFinal.isEmpty()) {
                if (matricula_situacao != null && matricula_situacao.equals("ativos")) {
                    listWhere.add(emissaoInativacaoString + " IS NOT NULL ");
                }
            }
        }

        if (!valorInicial.isEmpty() || !valorFinal.isEmpty()) {
            String subquery = "";
            subquery += " ( ";
            valorInicial = Float.toString(Moeda.converteUS$(valorInicial));
            valorFinal = Float.toString(Moeda.converteUS$(valorFinal));
            switch (tipoValor) {
                case "igual":
                    if (!valorInicial.isEmpty()) {
                        subquery += " "
                                + "SP.nr_desconto = 0 AND round( CAST ( ( func_valor_servico ( SP.id_pessoa, SP.id_servico, current_date, 0, SOC.id_categoria) ) AS numeric), 2) \n"
                                + " = " + valorInicial + "\n"
                                + " OR SP.nr_desconto > 0 AND round ( CAST( func_valor_servico_cheio( SP.id_pessoa, SP.id_servico, current_date) * (1 - (SP.nr_desconto / 100)) AS numeric), 2) \n"
                                + " = " + valorInicial + " \n";
                    }
                    break;
                case "apartir":
                    if (!valorInicial.isEmpty()) {
                        subquery += " "
                                + "SP.nr_desconto = 0 AND round( CAST ( ( func_valor_servico ( SP.id_pessoa, SP.id_servico, current_date, 0, SOC.id_categoria) ) AS numeric), 2) \n"
                                + " >= " + valorInicial + "\n"
                                + " OR SP.nr_desconto > 0 AND round ( CAST( func_valor_servico_cheio( SP.id_pessoa, SP.id_servico, current_date) * (1 - (SP.nr_desconto / 100)) AS numeric), 2) \n"
                                + " >= " + valorInicial + " \n";
                    }
                    break;
                case "ate":
                    if (!valorInicial.isEmpty()) {
                        subquery += " "
                                + "SP.nr_desconto = 0 AND round( CAST ( ( func_valor_servico ( SP.id_pessoa, SP.id_servico, current_date, 0, SOC.id_categoria) ) AS numeric), 2) \n"
                                + " <= " + valorInicial + " \n"
                                + " OR SP.nr_desconto > 0 AND round ( CAST( func_valor_servico_cheio( SP.id_pessoa, SP.id_servico, current_date) * (1 - (SP.nr_desconto / 100)) AS numeric), 2) \n"
                                + " <= " + valorInicial + " \n";
                    }
                    break;
                case "faixa":
                    if (!valorInicial.isEmpty() && !valorFinal.isEmpty()) {
                        subquery += " "
                                + "SP.nr_desconto = 0 AND round( CAST ( ( func_valor_servico ( SP.id_pessoa, SP.id_servico, current_date, 0, SOC.id_categoria) ) AS numeric), 2) \n"
                                + " BETWEEN " + valorInicial + " AND " + valorFinal + "\n"
                                + " OR SP.nr_desconto > 0 AND round ( CAST( func_valor_servico_cheio( SP.id_pessoa, SP.id_servico, current_date) * (1 - (SP.nr_desconto / 100)) AS numeric), 2) \n"
                                + " BETWEEN " + valorInicial + " AND " + valorFinal + "\n";
                    }
                    break;
                default:
                    break;
            }

            subquery += " ) ";
            listWhere.add(subquery);

        }

        if (matricula_situacao != null) {
            switch (matricula_situacao) {
                case "ativos":
                    listWhere.add(" SP.is_ativo = true ");
                    break;
                case "inativos":
                    listWhere.add(" MA.id_pessoa IS NULL ");
                    listWhere.add(" SP.id IN                                                        \n"
                            + "    (                                                                \n"
                            + "       SELECT max(sp.id)         AS id_servico_pessoa                \n"
                            + "         FROM fin_servico_pessoa AS SP                               \n"
                            + "   INNER JOIN matr_academia      AS M ON M.id_servico_pessoa = SP.id \n"
                            + "   INNER JOIN                                                        \n"
                            + "       (                                                             \n"
                            + "             SELECT max(sp.id) AS id_servico_pessoa,                 \n"
                            + "                    SP.id_pessoa,                                          \n"
                            + "                    max(m.dt_inativo)  AS dt_inativo                       \n"
                            + "               FROM fin_servico_pessoa AS SP                               \n"
                            + "         INNER JOIN matr_academia      AS M ON M.id_servico_pessoa = SP.id \n"
                            + "              WHERE SP.is_ativo = false                                    \n"
                            + "           GROUP BY SP.id_pessoa                                           \n"
                            + "       ) AS xmi ON sp.id_pessoa = xmi.id_pessoa                            \n"
                            + "         AND xmi.id_servico_pessoa = sp.id                                 \n"
                            + "     GROUP BY SP.id                                                        \n"
                            + "    ) ");
                    break;
            }
        }

        if (idade[0] != 0 || idade[1] != 0) {
            if (idade[0].equals(idade[1])) {
                listWhere.add(" func_idade(PA.dt_nascimento, current_date) = " + idade[0]);
            } else if (idade[0] >= 0 && idade[1] == 0) {
                listWhere.add(" func_idade(PA.dt_nascimento, current_date) >= " + idade[0]);
            } else {
                listWhere.add(" func_idade(PA.dt_nascimento, current_date) BETWEEN " + idade[0] + " AND " + idade[1]);
            }
        }
        if (idResponsavel != null) {
            listWhere.add("SP.id_cobranca = " + idResponsavel);
        }
        if (idAluno != null) {
            listWhere.add("SP.id_pessoa = " + idAluno);
        }
        if (inModalidade != null) {
            listWhere.add("SP.id_servico IN(" + inModalidade + ")");
        }
        if (inIdPeriodos != null) {
            listWhere.add("ASV.id_periodo IN(" + inIdPeriodos + ")");
        }
        if (inSexo != null && !inSexo.isEmpty()) {
            listWhere.add("PA.sexo LIKE '" + inSexo + "'");
        }
        if (nao_socio != null && nao_socio) {
            listWhere.add("SP.id_pessoa NOT IN (SELECT SOCVW.codsocio FROM soc_socios_vw AS SOCVW GROUP BY SOCVW.codsocio)");
        } else if ((in_grupo_categoria != null && !in_grupo_categoria.isEmpty()) || (in_categoria != null && !in_categoria.isEmpty())) {
            if (in_categoria != null && !in_categoria.isEmpty()) {
                listWhere.add("SOC.id_categoria IN (" + in_categoria + ")");
            } else if (in_grupo_categoria != null && !in_grupo_categoria.isEmpty()) {
                listWhere.add("SOC.id_grupo_categoria IN (" + in_grupo_categoria + ")");
            }
        }

        if (carenciaDias != null && carenciaDias >= 0) {
            Boolean s = false;
            if (situacao != null) {
                s = !situacao.equals("adimplente");
            }
            switch (tipoCarencia) {
                case "todos":
                    listWhere.add(" func_inadimplente(SP.id_pessoa, " + carenciaDias + ") = " + s);
                    break;
                case "eleicao":
                    listWhere.add(" func_inadimplente_eleicao(SP.id_pessoa, " + carenciaDias + ") = " + s);
                    break;
                case "clube":
                    listWhere.add(" func_inadimplente_clube(SP.id_pessoa, " + carenciaDias + ") = " + s);
                    break;
            }
        }

        if (situacaoFinanceira != null) {
            switch (situacaoFinanceira) {
                case "nao_quitados":
                    listWhere.add(" MV.id_baixa IS NULL ");
                    break;
                case "atrasados":
                    listWhere.add(" MV.dt_vencimento IS NULL AND MV.dt_vencimento < current_date  ");
                    break;
                case "ativo":
                    listWhere.add(" SP.is_ativo = true ");
                    break;
            }
        }

        if (!vencimentoInicial.isEmpty() || !vencimentoFinal.isEmpty()) {
            if (!vencimentoInicial.isEmpty() && !vencimentoFinal.isEmpty()) {
                listWhere.add(" MV.dt_vencimento  BETWEEN '" + vencimentoInicial + "' AND '" + vencimentoFinal + "'");
            } else if (!vencimentoInicial.isEmpty() && vencimentoFinal.isEmpty()) {
                listWhere.add(" MV.dt_vencimento  = '" + vencimentoInicial + "'");
            }
        }

        if (!quitacaoInicial.isEmpty() || !quitacaoFinal.isEmpty()) {
            if (!quitacaoInicial.isEmpty() && !quitacaoFinal.isEmpty()) {
                listWhere.add(" B.dt_baixa BETWEEN '" + quitacaoInicial + "' AND '" + quitacaoFinal + "'");
            } else if (!quitacaoInicial.isEmpty() && quitacaoFinal.isEmpty()) {
                listWhere.add(" B.dt_baixa = '" + quitacaoInicial + "'");
            }
        }

        if (desconto != null && desconto_final != null) {
            listWhere.add("SP.nr_desconto BETWEEN " + desconto + " AND " + desconto_final);
        } else if (desconto != null && desconto_final == null) {
            listWhere.add("SP.nr_desconto >= " + desconto);
        } else if (desconto == null && desconto_final != null) {
            listWhere.add("SP.nr_desconto <= " + desconto_final);
        }

        if (!listWhere.isEmpty()) {
            queryString += " WHERE ";
            for (int i = 0; i < listWhere.size(); i++) {
                if (i > 0) {
                    queryString += " AND ";
                }
                queryString += listWhere.get(i).toString() + " \n";

            }
        }
        if (relatorios != null && order.isEmpty()) {
            if (!relatorios.getQryOrdem().isEmpty()) {
                queryString += " ORDER BY " + relatorios.getQryOrdem();
            }
        } else if (!order.isEmpty()) {
            queryString += " ORDER BY " + order;
        }

        try {
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }
}
