package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.AutorizaImpressaoCartao;
import br.com.rtools.associativo.HistoricoCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class SocioCarteirinhaDao extends DB {

    /**
     *
     * @param pessoa (ID)
     * @param modelo (ID)
     * @return
     */
    public SocioCarteirinha pesquisaPorPessoaModelo(Integer pessoa, Integer modelo) {
        try {
            Query query = getEntityManager().createQuery("SELECT SC FROM SocioCarteirinha AS SC WHERE SC.pessoa.id = :pessoa AND SC.modeloCarteirinha.id = :modelo");
            query.setParameter("pessoa", pessoa);
            query.setParameter("modelo", modelo);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (SocioCarteirinha) list.get(0);
            }
        } catch (Exception e) {

        }
        return null;
    }

    public SocioCarteirinha pesquisaCodigo(int id) {
        SocioCarteirinha result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("SocioCarteirinha.pesquisaID");
            qry.setParameter("pid", id);
            if (!qry.getResultList().isEmpty()) {
                result = (SocioCarteirinha) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("SELECT sc FROM SocioCarteirinha sc");
            if (!qry.getResultList().isEmpty()) {
                return (qry.getResultList());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List pesquisaSocioSemCarteirinha() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("   SELECT s FROM Socios s"
                    + "    WHERE s.id NOT IN ( SELECT sc.socios.id FROM SocioCarteirinha sc)"
                    + " ORDER BY s.matriculaSocios.id");
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaSocioSemCarteirinhaDependente() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("   SELECT s                                                             "
                    + "     FROM Socios s                                                      "
                    + "    WHERE s.id NOT IN ( SELECT sc.socios.id FROM SocioCarteirinha sc )  "
                    + "      AND s.parentesco.id = 1                                           "
                    + " ORDER BY s.matriculaSocios.id                                          ");
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaSocioCarteirinhaSocio(int idSocio) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("SELECT sc "
                    + "  FROM SocioCarteirinha sc"
                    + " WHERE sc.socios.id = :idSocio");
            qry.setParameter("idSocio", idSocio);
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
        }
        return result;
    }

    public List find(String status, String filter, String query, String indexOrdem, Integer filial_id, Integer operador_id, String type_date, String start_date, String finish_date, String inPessoasImprimir) {
        if ((filter.equals("nascimento") || filter.equals("nome") || filter.equals("codigo") || filter.equals("cpf") || filter.equals("empresa") || filter.equals("cnpj")) && query.isEmpty()) {
            return new ArrayList();
        }

        Registro registro = (Registro) new Dao().find(new Registro(), 1);
        try {
            String queryString
                    = " -- SocioCarteirinhaDao->pesquisaCarteirinha() \n\n "
                    + "     SELECT p.id,                                                    \n" // 0 
                    + "            p.ds_nome,                                               \n" // 1 NOME
                    + "            pj.ds_documento,                                         \n" // 2 CNPJ
                    + "            pj.ds_nome,                                              \n" // 3 RAZÃO SOCIAL
                    + "            to_char(sc.dt_emissao, 'DD/MM/YYYY') as impresso,        \n" // 4 EMISSÃO
                    + "            c.ds_cidade,                                             \n" // 5 EMPRESA CIDADE
                    + "            to_char(sc.dt_validade_carteirinha, 'DD/MM/YYYY') as validade_carteirinha, \n" // 6 VALIDADE
                    + "            c.ds_uf,                                                 \n" // 7 EMPRESA UF
                    + "            to_char(pe.dt_admissao, 'DD/MM/YYYY') as admissao,       \n" // 8 ADMISSÃO
                    + "            j.ds_fantasia,                                           \n" // 9 FANTASIA
                    + "            s.matricula,                                             \n" // 10 MATRÍCULA
                    + "            s.nr_via,                                                \n" // 11 VIA
                    + "            s.id_socio,                                              \n" // 12 
                    + "            to_char(s.filiacao, 'DD/MM/YYYY') as filiacao,           \n" // 13 FILIAÇÃO
                    + "            pr.ds_profissao as cargo,                                \n" // 14 PROFISSÃO
                    + "            p.ds_documento,                                          \n" // 15 CPF
                    + "            f.ds_rg,                                                 \n" // 16 RG
                    + "            max(m.id),                                               \n" // 17
                    + "            sc.nr_cartao,                                            \n" // 18
                    + "            sc.id,                                                   \n" // 19
                    + "            mc.ds_descricao,                                         \n" // 20 CATEGORIA
                    + "            px.logradouro,                                           \n" // 21 LOGRADOURO
                    + "            px.endereco,                                             \n" // 22 ENDEREÇO
                    + "            px.numero,                                               \n" // 23 NÚMERO 
                    + "            px.complemento,                                          \n" // 24 COMPLEMENTO
                    + "            px.bairro,                                               \n" // 25 BAIRRO
                    + "            px.cidade,                                               \n" // 26 CIDADE
                    + "            px.uf,                                                   \n" // 27 UF
                    + "            px.cep,                                                  \n" // 28 CEP
                    + "            px.nacionalidade,                                        \n" // 29 NASCIONALIDADE
                    + "            to_char(px.dt_nascimento, 'DD/MM/YYYY') as nascimento,   \n" // 30 NASCIMENTO
                    + "            px.estado_civil as estadocivil,                          \n" // 31 ESTADO CÍVIL
                    + "            px.ctps as carteira,                                     \n" // 32 CARTEIRA (CTPS)
                    + "            px.ds_serie as serie,                                    \n" // 33 SÉRIE
                    + "            px.ds_orgao_emissao_rg AS orgao_expeditor,               \n" // 34 ORGÃO EMISSÃO RG
                    + "            pe.ds_codigo as codigo_funcional,                        \n" // 35 CÓDIGO FUNCIONAL
                    + "            s.parentesco,                                            \n" // 36 PARENTESCO
                    + "            s.categoria,                                             \n" // 37 CATEGORIA
                    + "            pt.fantasia AS fantasia_titular,                         \n" // 38 FANTASIA EMPRESA - TITULAR
                    + "            pt.codigo_funcional AS codigo_funcional_titular,         \n" // 39 CÓDIGO FUNCIONAL - TITULAR
                    + "            s.titular AS titular_id,                                 \n" // 40 TITULAR ID
                    + "            s.grupo_categoria,                                       \n" // 41 GRUPO CATEGORIA
                    + "            f.dt_aposentadoria,                                      \n" // 42 APOSENTADORIA
                    + "            f.ds_pai,                                                \n" // 43 PAI
                    + "            f.ds_mae                                                 \n" // 44 MÃE
                    + "       FROM pes_fisica           AS f                                                                \n"
                    + " INNER JOIN pes_pessoa           AS p    ON p.id         = f.id_pessoa                               \n"
                    + " INNER JOIN pes_pessoa_vw        AS px   ON p.id         = px.codigo                                 \n"
                    + " INNER JOIN soc_carteirinha      AS sc   ON sc.id_pessoa = p.id                                      \n"
                    + " INNER JOIN soc_modelo_carteirinha AS mc ON mc.id        = sc.id_modelo_carteirinha                  \n"
                    + "  LEFT JOIN soc_socios_vw        AS s    ON s.codsocio   = f.id_pessoa AND s.inativacao IS NULL      \n"
                    + "  LEFT JOIN pes_pessoa_vw        AS pt   ON pt.codigo    = s.titular                                 \n"
                    + "  LEFT JOIN pes_pessoa_empresa   AS pe   ON f.id         = pe.id_fisica AND pe.dt_demissao IS NULL   \n"
                    + "  LEFT JOIN pes_profissao        AS pr   ON pr.id        = pe.id_funcao                              \n"
                    + "  LEFT JOIN pes_juridica         AS j    ON j.id         = pe.id_juridica                            \n"
                    + "  LEFT JOIN pes_pessoa           AS pj   ON pj.id        = j.id_pessoa                               \n"
                    + "  LEFT JOIN pes_pessoa_endereco  AS pend ON pend.id_pessoa = pj.id AND pend.id_tipo_endereco = 2     \n" // NO COMERCIO O ENDEREÇO É DA EMPRESA -- EM ITAPETININGA NÃO SEI 24/07/2014 -- MODIFICAÇÃO PEDIDA PELA PRISCILA
                    //"  LEFT JOIN pes_pessoa_endereco pend on pend.id_pessoa = p.id AND pend.id_tipo_endereco = 1" + // ENDEREÇO DO SÓCIO
                    + "  LEFT JOIN end_endereco         AS ende ON ende.id      = pend.id_endereco                          \n"
                    + "  LEFT JOIN end_cidade           AS c    ON c.id         = ende.id_cidade                            \n"
                    // USADA ATÉ 06/04/2015 - CHAMADO 665
                    // + "  LEFT JOIN fin_movimento        AS m    ON m.id_pessoa  = sc.id_pessoa AND m.id_servicos in (SELECT id_servicos FROM fin_servico_rotina where id_rotina = 170) "
                    + "  LEFT JOIN fin_movimento        AS m    ON m.id_beneficiario = sc.id_pessoa AND m.id_servicos IN (SELECT id_servico_cartao FROM seg_registro) AND m.dt_vencimento >='06/04/2015' \n"
                    + "  LEFT JOIN soc_historico_carteirinha sh ON sh.id_movimento = m.id                                   \n";

            // NÃO IMPRESSOS
            List listWhere = new ArrayList();
            if (status.equals("nao_impressos")) {
                if (registro.isCobrancaCarteirinha()) {
                    String subquery = ""
                            + " (                                                                                                       \n"
                            + "	(m.id_servicos IS NOT NULL AND sh.id_movimento IS NULL AND m.is_ativo = true) OR                        \n"
                            + "	(p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE id_historico_carteirinha IS NULL))  \n"
                            + " )";
                    listWhere.add(subquery);
                    listWhere.add("SC.dt_emissao IS NULL");
                } else {
                    // listWhere.add("SC.id NOT IN (SELECT id_carteirinha FROM soc_historico_carteirinha)");
                    listWhere.add("SC.dt_emissao IS NULL");
                }
                if (start_date != null && !start_date.isEmpty()) {
                    switch (type_date) {
                        case "igual":
                            listWhere.add("SC.dt_emissao = '" + start_date + "'");
                            break;
                        case "apartir":
                            listWhere.add("SC.dt_emissao >= '" + start_date + "'");
                            break;
                        case "ate":
                            listWhere.add("SC.dt_emissao <= '" + start_date + "'");
                            break;
                        case "faixa":
                            if (!start_date.isEmpty()) {
                                listWhere.add("SC.dt_demissao BETWEEN  '" + start_date + "' AND '" + finish_date + "'");
                            }
                            break;
                    }
                }
            } else {
                if (registro.isCobrancaCarteirinha()) {
                    listWhere.add("SC.id IN (SELECT id_carteirinha FROM soc_historico_carteirinha) AND SH.id_movimento > 0");
                } else {
                    listWhere.add("SC.id IN (SELECT id_carteirinha FROM soc_historico_carteirinha)");
                }
                switch (status) {
                    case "hoje":
                        listWhere.add("SC.dt_emissao IS NOT NULL AND SC.dt_emissao = current_date");
                        break;
                    case "ontem":
                        listWhere.add("SC.dt_emissao IS NOT NULL AND SC.dt_emissao = current_date-1");
                        break;
                    case "ultimos_30_dias":
                        listWhere.add("SC.dt_emissao IS NOT NULL AND SC.dt_emissao BETWEEN current_date-30 AND current_date");
                        break;
                    case "impressos":
                        if (start_date != null && !start_date.isEmpty()) {
                            switch (type_date) {
                                case "igual":
                                    listWhere.add("SH.dt_emissao = '" + start_date + "'");
                                    break;
                                case "apartir":
                                    listWhere.add("SH.dt_emissao >= '" + start_date + "'");
                                    break;
                                case "ate":
                                    listWhere.add("SH.dt_emissao <= '" + start_date + "'");
                                    break;
                                case "faixa":
                                    if (!start_date.isEmpty()) {
                                        listWhere.add("SH.dt_demissao BETWEEN  '" + start_date + "' AND '" + finish_date + "'");
                                    }
                                    break;
                            }
                        }
                        break;
                    default:
                        break;
                }
                if (operador_id != null) {
                    listWhere.add("SH.id_usuario = " + operador_id);
                }
            }

            // PESSOA / NOME
            if (!inPessoasImprimir.isEmpty()) {
                filter = "in_pessoas";
            }
            switch (filter) {
                case "nome":
                    listWhere.add("TRIM(UPPER(func_translate(P.ds_nome))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))");
                    break;
                case "nome_titular":
                    listWhere.add("TRIM(UPPER(func_translate(PT.nome))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))");
                    break;
                case "matricula":
                    listWhere.add("S.matricula = " + query + "");
                    break;
                case "codigo":
                    listWhere.add("P.id = " + query + "");
                    break;
                case "in_pessoas":
                    listWhere.add("P.id IN ( " + inPessoasImprimir + " )");
                    break;
                case "cpf":
                    listWhere.add("P.ds_documento LIKE '%" + query + "%'");
                    break;
                case "cpf_titular":
                    listWhere.add("PT.cpf LIKE '%" + query + "%'");
                    break;
                case "empresa":
                    listWhere.add("TRIM(UPPER(func_translate(PJ.ds_nome))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))");
                    break;
                case "cnpj":
                    listWhere.add("PJ.ds_documento LIKE '%" + query + "%'");
                    break;
                case "nascimento":
                    listWhere.add("F.dt_nascimento = '" + query + "'");
                    break;
                default:
                    break;
            }

            // ROGÉRIO PODIU PRA COMENTAR O CAMPO ABAIXO - 07/10/2015
            // SE NÃO FOR SÓCIO (ACADEMIA)
            listWhere.add("sc.is_ativo = true");
            // QUE POSSUEM FOTOS
            if (registro.isFotoCartao()) {
                listWhere.add("(F.ds_foto <> '' OR P.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL))");
            }

            if (filial_id != null) {
                listWhere.add("s.id_filial = " + filial_id + "");
            }

            // QUE POSSUEM FOTOS
            if (!registro.isCarteirinhaDependente()) {
                listWhere.add("S.id_parentesco = 1");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + "\n";
                }
            }
            // GROUP DA QUERY
            queryString += " "
                    + " GROUP BY p.id,                                       \n"
                    + "          p.ds_nome,                                  \n"
                    + "          pj.ds_documento,                            \n"
                    + "          pj.ds_nome,                                 \n"
                    + "          to_char(sc.dt_emissao, 'DD/MM/YYYY'),       \n"
                    + "          c.ds_cidade,                                \n"
                    + "          to_char(sc.dt_validade_carteirinha, 'DD/MM/YYYY'), \n"
                    + "          c.ds_uf,                                    \n"
                    + "          to_char(pe.dt_admissao, 'DD/MM/YYYY'),      \n"
                    + "          j.ds_fantasia,                              \n"
                    + "          s.matricula,                                \n"
                    + "          s.nr_via,                                   \n"
                    + "          s.id_socio,                                 \n"
                    + "          to_char(s.filiacao, 'DD/MM/YYYY'),          \n"
                    + "          pr.ds_profissao,                            \n"
                    + "          p.ds_documento,                             \n"
                    + "          f.ds_rg,                                    \n"
                    + "          sc.nr_cartao,                               \n"
                    + "          sc.id,                                      \n"
                    + "          mc.ds_descricao,                            \n"
                    + "          px.logradouro,                              \n"
                    + "          px.endereco,                                \n"
                    + "          px.numero,                                  \n"
                    + "          px.complemento,                             \n"
                    + "          px.bairro,                                  \n"
                    + "          px.cidade,                                  \n"
                    + "          px.uf,                                      \n"
                    + "          px.cep,                                     \n"
                    + "          px.nacionalidade,                           \n"
                    + "          to_char(px.dt_nascimento, 'DD/MM/YYYY'),    \n"
                    + "          px.estado_civil,                            \n"
                    + "          px.ctps,                                    \n"
                    + "          px.ds_serie,                                \n"
                    + "          px.ds_orgao_emissao_rg,                     \n"
                    + "          pe.ds_codigo,                               \n"
                    + "          s.parentesco,                               \n"
                    + "          s.categoria,                                \n"
                    + "          pt.fantasia,                                \n"
                    + "          pt.codigo_funcional,                        \n"
                    + "          s.titular,                                  \n"
                    + "          s.grupo_categoria,                          \n"
                    + "          f.dt_aposentadoria,                         \n"
                    + "          f.ds_pai,                                   \n"
                    + "          f.ds_mae                                    \n";

            // ORDEM DA QUERY
            switch (indexOrdem) {
                case "0":
                    queryString += " ORDER BY p.ds_nome ";
                    break;
                case "1":
                    queryString += " ORDER BY pj.ds_nome, pj.ds_documento, p.ds_nome ";
                    break;
                case "2":
                    queryString += " ORDER BY pj.ds_documento, p.ds_nome ";
                    break;
                case "3":
                    queryString += " ORDER BY sc.dt_emissao desc, p.ds_nome ";
                    break;
                case "4":
                    queryString += " ORDER BY sc.dt_emissao desc, pj.ds_nome, pj.ds_documento, p.ds_nome";
                    break;
                default:
                    break;
            }
            Query qry = getEntityManager().createNativeQuery(queryString);

            return qry.getResultList();
        } catch (NumberFormatException e) {
            e.getMessage();
        }

        return new ArrayList();
    }

    // IGNORAR
    public List<Vector> pesquisaCarteirinha(String tipo, String descricao, String indexOrdem, Integer id_filial) {
        Registro registro = (Registro) new Dao().find(new Registro(), 1);
        try {
            String textqry
                    = " -- SocioCarteirinhaDao->pesquisaCarteirinha() \n\n "
                    + "     SELECT p.id,                                                    \n" // 0 
                    + "            p.ds_nome,                                               \n" // 1 NOME
                    + "            pj.ds_documento,                                         \n" // 2 CNPJ
                    + "            pj.ds_nome,                                              \n" // 3 RAZÃO SOCIAL
                    + "            to_char(sc.dt_emissao, 'DD/MM/YYYY') as impresso,        \n" // 4 EMISSÃO
                    + "            c.ds_cidade,                                             \n" // 5 EMPRESA CIDADE
                    + "            to_char(sc.dt_validade_carteirinha, 'DD/MM/YYYY') as validade_carteirinha, \n" // 6 VALIDADE
                    + "            c.ds_uf,                                                 \n" // 7 EMPRESA UF
                    + "            to_char(pe.dt_admissao, 'DD/MM/YYYY') as admissao,       \n" // 8 ADMISSÃO
                    + "            j.ds_fantasia,                                           \n" // 9 FANTASIA
                    + "            s.matricula,                                             \n" // 10 MATRÍCULA
                    + "            s.nr_via,                                                \n" // 11 VIA
                    + "            s.id_socio,                                              \n" // 12 
                    + "            to_char(s.filiacao, 'DD/MM/YYYY') as filiacao,           \n" // 13 FILIAÇÃO
                    + "            pr.ds_profissao as cargo,                                \n" // 14 PROFISSÃO
                    + "            p.ds_documento,                                          \n" // 15 CPF
                    + "            f.ds_rg,                                                 \n" // 16 RG
                    + "            max(m.id),                                               \n" // 17
                    + "            sc.nr_cartao,                                            \n" // 18
                    + "            sc.id,                                                   \n" // 19
                    + "            mc.ds_descricao,                                         \n" // 20 CATEGORIA
                    + "            px.logradouro,                                           \n" // 21 LOGRADOURO
                    + "            px.endereco,                                             \n" // 22 ENDEREÇO
                    + "            px.numero,                                               \n" // 23 NÚMERO 
                    + "            px.complemento,                                          \n" // 24 COMPLEMENTO
                    + "            px.bairro,                                               \n" // 25 BAIRRO
                    + "            px.cidade,                                               \n" // 26 CIDADE
                    + "            px.uf,                                                   \n" // 27 UF
                    + "            px.cep,                                                  \n" // 28 CEP
                    + "            px.nacionalidade,                                        \n" // 29 NASCIONALIDADE
                    + "            to_char(px.dt_nascimento, 'DD/MM/YYYY') as nascimento,   \n" // 30 NASCIMENTO
                    + "            px.estado_civil as estadocivil,                          \n" // 31 ESTADO CÍVIL
                    + "            px.ctps as carteira,                                     \n" // 32 CARTEIRA (CTPS)
                    + "            px.ds_serie as serie,                                    \n" // 33 SÉRIE
                    + "            px.ds_orgao_emissao_rg AS orgao_expeditor,               \n" // 34 ORGÃO EMISSÃO RG
                    + "            pe.ds_codigo as codigo_funcional,                        \n" // 35 CÓDIGO FUNCIONAL
                    + "            s.parentesco,                                            \n" // 36 PARENTESCO
                    + "            s.categoria,                                             \n" // 37 CATEGORIA
                    + "            pt.fantasia AS fantasia_titular,                         \n" // 38 FANTASIA EMPRESA - TITULAR
                    + "            pt.codigo_funcional AS codigo_funcional_titular,         \n" // 39 CÓDIGO FUNCIONAL - TITULAR
                    + "            s.titular AS titular_id,                                 \n" // 40 TITULAR ID
                    + "            s.grupo_categoria,                                       \n" // 41 GRUPO CATEGORIA
                    + "            f.dt_aposentadoria                                       \n" // 42 APOSENTADORIA
                    + "       FROM pes_fisica           AS f                                                                \n"
                    + " INNER JOIN pes_pessoa           AS p    ON p.id         = f.id_pessoa                               \n"
                    + " INNER JOIN pes_pessoa_vw        AS px   ON p.id         = px.codigo                                 \n"
                    + "  LEFT JOIN soc_socios_vw        AS s    ON s.codsocio   = f.id_pessoa AND s.inativacao IS NULL      \n"
                    + "  LEFT JOIN pes_pessoa_vw        AS pt   ON pt.codigo    = s.titular                                 \n"
                    + "  LEFT JOIN pes_pessoa_empresa   AS pe   ON f.id         = pe.id_fisica AND pe.dt_demissao IS NULL   \n"
                    + "  LEFT JOIN pes_profissao        AS pr   ON pr.id        = pe.id_funcao                              \n"
                    + "  LEFT JOIN pes_juridica         AS j    ON j.id         = pe.id_juridica                            \n"
                    + "  LEFT JOIN pes_pessoa           AS pj   ON pj.id        = j.id_pessoa                               \n"
                    + "  LEFT JOIN pes_pessoa_endereco  AS pend ON pend.id_pessoa = pj.id AND pend.id_tipo_endereco = 2     \n" // NO COMERCIO O ENDEREÇO É DA EMPRESA -- EM ITAPETININGA NÃO SEI 24/07/2014 -- MODIFICAÇÃO PEDIDA PELA PRISCILA
                    //"  LEFT JOIN pes_pessoa_endereco pend on pend.id_pessoa = p.id AND pend.id_tipo_endereco = 1" + // ENDEREÇO DO SÓCIO
                    + "  LEFT JOIN end_endereco         AS ende ON ende.id      = pend.id_endereco                          \n"
                    + "  LEFT JOIN end_cidade           AS c    ON c.id         = ende.id_cidade                            \n"
                    + " INNER JOIN soc_carteirinha      AS sc   ON sc.id_pessoa = p.id                                      \n"
                    // USADA ATÉ 06/04/2015 - CHAMADO 665
                    // + "  LEFT JOIN fin_movimento        AS m    ON m.id_pessoa  = sc.id_pessoa AND m.id_servicos in (SELECT id_servicos FROM fin_servico_rotina where id_rotina = 170) "
                    + "  LEFT JOIN fin_movimento        AS m    ON m.id_beneficiario = sc.id_pessoa AND m.id_servicos IN (SELECT id_servico_cartao FROM seg_registro) AND m.dt_vencimento >='06/04/2015' \n"
                    + "  LEFT JOIN soc_historico_carteirinha sh ON sh.id_movimento = m.id                                   \n"
                    + " INNER JOIN soc_modelo_carteirinha AS mc ON mc.id        = sc.id_modelo_carteirinha                  \n";

            // NÃO IMPRESSOS / EMPRESAS
            if (tipo.equals("niEmpresa")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE ( "
                            + "	(m.id_servicos IS NOT NULL AND sh.id_movimento IS NULL AND m.is_ativo = true) OR "
                            + "	(p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE id_historico_carteirinha IS NULL)) "
                            + "       ) \n";
                } else {
                    textqry += " WHERE sc.id NOT IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "    AND TRIM(UPPER(func_translate(pj.ds_nome)))  LIKE TRIM(UPPER(func_translate('%" + descricao + "%'))) \n";
                }

                if (registro.isFotoCartao()) {
                    //textqry += "    AND (f.dt_foto IS NOT NULL OR p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL)) \n";
                    textqry += "    AND (f.ds_foto <> '' OR p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL)) \n";
                }
            }

            // NÃO IMPRESSOS / CNPJ
            if (tipo.equals("niCNPJ")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE ( "
                            + "	(m.id_servicos IS NOT NULL AND sh.id_movimento IS NULL AND m.is_ativo = true) OR "
                            + "	(p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE id_historico_carteirinha IS NULL)) "
                            + "       ) \n";
                } else {
                    textqry += " WHERE sc.id NOT IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "   AND lower(pj.ds_documento)  LIKE '%" + descricao.toLowerCase() + "%' \n";
                }

                if (registro.isFotoCartao()) {
                    //textqry += "    AND (f.dt_foto IS NOT NULL OR p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL)) \n";
                    textqry += "    AND (f.ds_foto <> '' OR p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL)) \n";
                }
            }

            // IMPRESSOS / EMPRESAS
            if (tipo.equals("iEmpresa")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) AND sh.id_movimento > 0 \n";
                } else {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "    AND TRIM(UPPER(func_translate(pj.ds_nome)))  LIKE TRIM(UPPER(func_translate('%" + descricao + "%'))) \n";
                }
            }

            // IMPRESSOS / CNPJ
            if (tipo.equals("iCNPJ")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) AND sh.id_movimento > 0 \n";
                } else {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "    AND lower(pj.ds_documento)  LIKE '%" + descricao.toLowerCase() + "%' \n";
                }
            }

            // IMPRESSOS / ULTIMOS 30 DIAS
            if (tipo.equals("iDias")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) AND sh.id_movimento > 0 \n";
                } else {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "    AND TRIM(UPPER(func_translate(pj.ds_nome)))  LIKE TRIM(UPPER(func_translate('%" + descricao + "%'))) "
                            + "    AND sc.dt_emissao is not null AND sc.dt_emissao BETWEEN current_date-30 AND current_date \n";
                } else {
                    textqry += "    AND sc.dt_emissao is not null AND sc.dt_emissao BETWEEN current_date-30 AND current_date \n";
                }
            }

            // HOJE
            if (tipo.equals("iOntem")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) AND sh.id_movimento IS NOT NULL \n";
                } else {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "    AND TRIM(UPPER(func_translate(pj.ds_nome)))  LIKE TRIM(UPPER(func_translate('%" + descricao + "%'))) \n"
                            + "    AND sc.dt_emissao IS NOT NULL AND sc.dt_emissao = current_date-1 \n";
                } else {
                    textqry += "    AND sc.dt_emissao IS NOT NULL AND sc.dt_emissao = current_date-1 \n";
                }
            }

            // HOJE
            if (tipo.equals("iHoje")) {
                if (registro.isCobrancaCarteirinha()) {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) AND sh.id_movimento IS NOT NULL \n";
                } else {
                    textqry += " WHERE sc.id IN (select id_carteirinha FROM soc_historico_carteirinha) \n";
                }

                if (!descricao.isEmpty()) {
                    textqry += "    AND TRIM(UPPER(func_translate(pj.ds_nome)))  LIKE TRIM(UPPER(func_translate('%" + descricao + "%'))) \n"
                            + "    AND sc.dt_emissao IS NOT NULL AND sc.dt_emissao = current_date \n";
                } else {
                    textqry += "    AND sc.dt_emissao IS NOT NULL AND sc.dt_emissao = current_date \n";
                }
            }

            if ((tipo.equals("iNome") || tipo.equals("iCodigo") || tipo.equals("iCPF")) && descricao.isEmpty()) {
                return new ArrayList();
            }

            // PESSOA / NOME
            if (tipo.equals("iNome")) {
                if (!descricao.isEmpty()) {
                    textqry += "    WHERE TRIM(UPPER(func_translate(p.ds_nome)))  LIKE TRIM(UPPER(func_translate('%" + descricao + "%'))) \n";
                } else {
                    textqry += " ";
                }
            }

            // SOCIO / MATRICULA
            if (tipo.equals("iMatricula")) {
                if (!descricao.isEmpty()) {
                    textqry += "    WHERE s.matricula = " + Integer.parseInt(descricao) + " \n";
                } else {
                    textqry += "  ";
                }
            }

            // PESSOA / CODIGO
            if (tipo.equals("iID")) {
                if (!descricao.isEmpty()) {
                    textqry += "    WHERE p.id = " + Integer.parseInt(descricao + " \n");
                } else {
                    textqry += "  ";
                }
            }

            // PESSOA / CPF
            if (tipo.equals("iCPF")) {
                if (!descricao.isEmpty()) {
                    textqry += "    WHERE lower(p.ds_documento) LIKE '%" + descricao.toLowerCase() + "%' \n";
                } else {
                    textqry += " ";
                }
            }

            // ROGÉRIO PODIU PRA COMENTAR O CAMPO ABAIXO - 07/10/2015
            textqry += " AND sc.is_ativo = true \n"; // SE NÃO FOR SÓCIO (ACADEMIA)
            // QUE POSSUEM FOTOS
            if (registro.isFotoCartao()) {
                textqry += "    AND (f.ds_foto <> '' OR p.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL)) \n";
            }

            if (id_filial != null) {
                textqry += " AND s.id_filial = " + id_filial + " \n";
            }

            // QUE POSSUEM FOTOS
            if (!registro.isCarteirinhaDependente()) {
                textqry += " AND s.id_parentesco = 1 \n";
            }

            if (id_filial != null) {
                textqry += " AND s.id_filial = " + id_filial + " \n";
            }

            // GROUP DA QUERY
            textqry += " "
                    + " GROUP BY p.id,                                       \n"
                    + "          p.ds_nome,                                  \n"
                    + "          pj.ds_documento,                            \n"
                    + "          pj.ds_nome,                                 \n"
                    + "          to_char(sc.dt_emissao, 'DD/MM/YYYY'),       \n"
                    + "          c.ds_cidade,                                \n"
                    + "          to_char(sc.dt_validade_carteirinha, 'DD/MM/YYYY'), \n"
                    + "          c.ds_uf,                                    \n"
                    + "          to_char(pe.dt_admissao, 'DD/MM/YYYY'),      \n"
                    + "          j.ds_fantasia,                              \n"
                    + "          s.matricula,                                \n"
                    + "          s.nr_via,                                   \n"
                    + "          s.id_socio,                                 \n"
                    + "          to_char(s.filiacao, 'DD/MM/YYYY'),          \n"
                    + "          pr.ds_profissao,                            \n"
                    + "          p.ds_documento,                             \n"
                    + "          f.ds_rg,                                    \n"
                    + "          sc.nr_cartao,                               \n"
                    + "          sc.id,                                      \n"
                    + "          mc.ds_descricao,                            \n"
                    + "          px.logradouro,                              \n"
                    + "          px.endereco,                                \n"
                    + "          px.numero,                                  \n"
                    + "          px.complemento,                             \n"
                    + "          px.bairro,                                  \n"
                    + "          px.cidade,                                  \n"
                    + "          px.uf,                                      \n"
                    + "          px.cep,                                     \n"
                    + "          px.nacionalidade,                           \n"
                    + "          to_char(px.dt_nascimento, 'DD/MM/YYYY'),    \n"
                    + "          px.estado_civil,                            \n"
                    + "          px.ctps,                                    \n"
                    + "          px.ds_serie,                                \n"
                    + "          px.ds_orgao_emissao_rg,                     \n"
                    + "          pe.ds_codigo,                               \n"
                    + "          s.parentesco,                               \n"
                    + "          s.categoria,                                \n"
                    + "          pt.fantasia,                                \n"
                    + "          pt.codigo_funcional,                        \n"
                    + "          s.titular,                                  \n"
                    + "          s.grupo_categoria,                          \n"
                    + "          f.dt_aposentadoria                          \n";

            // ORDEM DA QUERY
            if (indexOrdem.equals("0")) {
                textqry += " ORDER BY p.ds_nome ";
            } else if (indexOrdem.equals("1")) {
                textqry += " ORDER BY pj.ds_nome, pj.ds_documento, p.ds_nome ";
            } else if (indexOrdem.equals("2")) {
                textqry += " ORDER BY pj.ds_documento, p.ds_nome ";
            } else if (indexOrdem.equals("3")) {
                textqry += " ORDER BY sc.dt_emissao desc, p.ds_nome ";
            } else if (indexOrdem.equals("4")) {
                textqry += " ORDER BY sc.dt_emissao desc, pj.ds_nome, pj.ds_documento, p.ds_nome";
            }
            Query qry = getEntityManager().createNativeQuery(textqry);

            return qry.getResultList();
        } catch (NumberFormatException e) {
            e.getMessage();
        }

        return new ArrayList();
    }

//   
//    public List listaFiltro(String indexFiltro, String descEmpresa, String indexOrdem, boolean fantasia) {
//        List lista = new ArrayList();
//        String textqry = 
//                "     SELECT p.codigo AS codigo,                                        " + // 0 CÓDIGO
//                "            p.nome AS nome,                                            " + // 1 NOME
//                "            p.cnpj AS cnpj,                                            " + // 2 CNPJ
//                "            p.empresa as empresa,                                      " + // 3 EMPRESA
//                "            to_char(c.dt_emissao, 'DD/MM/YYYY') AS data,               " + // 4 DATA EMISSÃO
//                "            p.e_cidade AS cidade,                                      " + // 5 CIDADE
//                "            to_char(s.validade_carteirinha, 'DD/MM/YYYY') AS validade, " + // 6 VALIDADE
//                "            p.e_uf AS uf,                                              " + // 7 ESTADO
//                "            to_char(p.admissao, 'DD/MM/YYYY') AS admissao,             " + // 8 ADMISSÃO
//                "            p.fantasia AS fantasia,                                    " + // 9 FANTASIA
//                "            s.matricula AS matricula,                                  " + // 10 MATRICULA
//                "            s.nr_via AS via,                                           " + // 11 VIA
//                "            s.id_socio AS codsocio,                                    " + // 12 CÓDIGO SÓCIO
//                "            to_char(s.filiacao, 'DD/MM/YYYY') as filiacao,             " + // 13 FILIAÇÃO
//                "            p.profissao AS cargo,                                      " + // 14 PROFISSÃO
//                "            p.cpf,                                                     " + // 15 CPF
//                "            p.ds_rg AS rg                                              " + // 16 RG                
//                "       FROM pes_pessoa_vw AS p                                         "
//                + " INNER JOIN soc_socios_vw AS s on s.codsocio = p.codigo and s.parentesco = 'TITULAR' "
//                + "  LEFT JOIN soc_carteirinha AS c on c.id_socio = s.id_socio";
//        String por = "empresa";
//        if (fantasia) {
//            por = "fantasia";
//        }
//
//        descEmpresa = descEmpresa.toUpperCase();
//        if (indexFiltro.equals("0")) {
////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is null";
//            textqry += " where s.id_socio not in (select id_socio from soc_carteirinha) and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("1")) {
////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is null and upper(p."+por+") like ('%"+descEmpresa+"%')";
//            textqry += " where ( s.id_socio not in (select id_socio from soc_carteirinha) ) and upper(p." + por + ") like ('%" + descEmpresa + "%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("2")) {
//            textqry += " where ( s.id_socio not in (select id_socio from soc_carteirinha) ) and p.cnpj like ('%" + descEmpresa + "%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("3")) {
//            textqry += " where upper(p.nome) like ('%" + descEmpresa + "%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("4")) {
//            int cod;
//            try {
//                cod = Integer.valueOf(descEmpresa);
//            } catch (NumberFormatException e) {
//                cod = 0;
//            }
//            textqry += " where p.codigo = " + cod + " and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("5")) {
//            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and upper(p." + por + ") like ('%" + descEmpresa + "%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("6")) {
//            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and p.cnpj like ('" + descEmpresa + "%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        } else if (indexFiltro.equals("7")) {
//            //textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and c.dt_emissao>=(current_date-30) and to_char(p.demissao, 'DD/MM/YYYY') is null";
//            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and to_char(dt_emissao,'yyyymmyy')>=to_char(current_date-30,'yyyymmyy') and to_char(p.demissao, 'DD/MM/YYYY') is null ";
//        } else if (indexFiltro.equals("8")) {
//            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and to_char(p.demissao, 'DD/MM/YYYY') is null";
//        }
////        if (indexFiltro.equals("0")){
//////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is null";
////            textqry += " where s.id_socio not in (select id_socio from soc_carteirinha) and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("1")){
//////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is null and upper(p."+por+") like ('%"+descEmpresa+"%')";
////            textqry += " where ( s.id_socio not in (select id_socio from soc_carteirinha) ) and upper(p."+por+") like ('%"+descEmpresa+"%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("2")){
////            textqry += " where ( s.id_socio not in (select id_socio from soc_carteirinha) ) and p.cnpj like ('%"+descEmpresa+"%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("3")){
////            textqry += " where upper(p.nome) like ('%"+descEmpresa+"%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("4")){
////            int cod = 0;
////            try{ cod = Integer.valueOf(descEmpresa); }catch(Exception e){cod = 0;}
////            textqry += " where p.codigo = "+cod+" and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("5")){
////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and upper(p."+por+") like ('%"+descEmpresa+"%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("6")){
////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and p.cnpj like ('"+descEmpresa+"%') and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("7")){
////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and c.dt_emissao>=(current_date-30) and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }else if (indexFiltro.equals("8")){
////            textqry += " where to_char(c.dt_emissao, 'DD/MM/YYYY') is not null and to_char(p.demissao, 'DD/MM/YYYY') is null";
////        }
//
//        textqry += " group by p.codigo, "
//                + " p.nome, "
//                + " p.cnpj, "
//                + " p.empresa, "
//                + " to_char(c.dt_emissao, 'DD/MM/YYYY'), "
//                + " p.e_cidade, "
//                + " to_char(s.validade_carteirinha, 'DD/MM/YYYY'), "
//                + " p.e_uf, to_char(p.admissao, 'DD/MM/YYYY'), "
//                + " p.fantasia, "
//                + " s.matricula, "
//                + " s.nr_via, "
//                + " s.id_socio, "
//                + " s.filiacao, "
//                + " p.profissao, "
//                + " p.cpf, "
//                + " p.ds_rg ";
//
//        if (indexOrdem.equals("0")) {
//            textqry += " order by p.nome ";
//        } else if (indexOrdem.equals("1")) {
//            textqry += " order by p.empresa, p.cnpj, p.nome ";
//        } else if (indexOrdem.equals("2")) {
//            textqry += " order by p.cnpj, p.nome ";
//        } else if (indexOrdem.equals("3")) {
//            textqry += " order by data desc, p.nome ";
//        } else if (indexOrdem.equals("4")) {
//            textqry += " order by data desc, p.empresa, p.cnpj, p.nome";
//        }
//
//        try {
//            Query qry = getEntityManager().createNativeQuery(textqry);
//            if (!qry.getResultList().isEmpty()) {
//                lista = qry.getResultList();
//            }
//        } catch (Exception e) {
//        }
//        return lista;
//    }
//   
//    public List listaFiltroCartao(int id_socio) {
//        List lista = new ArrayList();
//        String textqry = 
//                "     SELECT p.codigo as codigo,                                        " + // 0 CÓDIGO
//                "            p.nome as nome,                                            " + // 1 NOME
//                "            p.cnpj as cnpj,                                            " + // 2 CNPJ
//                "            p.empresa as empresa,                                      " + // 3 EMPRESA
//                "            to_char(c.dt_emissao, 'DD/MM/YYYY') as data,               " + // 4 DATA EMISSÃO
//                "            p.e_cidade as cidade,                                      " + // 5 CIDADE
//                "            to_char(s.validade_carteirinha, 'DD/MM/YYYY') as validade, " + // 6 VALIDADE
//                "            p.e_uf as uf,                                              " + // 7 ESTADO
//                "            to_char(p.admissao, 'DD/MM/YYYY') as admissao,             " + // 8 ADMISSÃO
//                "            p.fantasia as fantasia,                                    " + // 9 FANTASIA
//                "            s.matricula as matricula,                                  " + // 10 MATRICULA
//                "            s.nr_via as via,                                           " + // 11 VIA
//                "            s.id_socio as codsocio,                                    " + // 12 CÓDIGO SÓCIO
//                "            to_char(s.filiacao, 'DD/MM/YYYY') as filiacao,             " + // 13 FILIAÇÃO
//                "            p.profissao AS cargo,                                      " + // 14 PROFISSÃO
//                "            p.cpf,                                                     " + // 15 CPF
//                "            p.ds_rg AS rg                                              " + // 16 RG                   
//                "       FROM pes_pessoa_vw AS p                                         "
//                + " INNER JOIN soc_socios_vw AS s on s.codsocio = p.codigo                "
//                + "  LEFT JOIN soc_carteirinha AS c on c.id_socio = s.id_socio            "
//                + "      WHERE s.id_socio = " + id_socio + " ";
//        try {
//            Query qry = getEntityManager().createNativeQuery(textqry);
//            if (!qry.getResultList().isEmpty()) {
//                lista = qry.getResultList();
//            }
//        } catch (Exception e) {
//        }
//
//        return lista;
//    }
    public List filtroCartao(int id_pessoa) {
        List lista = new ArrayList();
        String textqry
                = "     SELECT p.codigo AS codigo,                                        \n" // 0 CÓDIGO
                + "            p.nome AS nome,                                            \n" // 1 NOME
                + "            p.cnpj AS cnpj,                                            \n" // 2 CNPJ
                + "            p.empresa AS empresa,                                      \n" // 3 EMPRESA
                + "            to_char(c.dt_emissao, 'DD/MM/YYYY') AS emissao,            \n" // 4 DATA EMISSÃO
                + "            p.e_cidade AS cidade,                                      \n" // 5 CIDADE
                + "            to_char(s.validade_carteirinha, 'DD/MM/YYYY') AS validade, \n" // 6 VALIDADE
                + "            p.e_uf AS uf,                                              \n" // 7 ESTADO (UF)
                + "            to_char(p.admissao, 'DD/MM/YYYY') as admissao,             \n" // 8 ADMISSÃO
                + "            p.fantasia AS fantasia,                                    \n" // 9 FANTASIA
                + "            s.matricula AS matricula,                                  \n" // 10 MATRICULA
                + "            s.nr_via AS via,                                           \n" // 11 VIA
                + "            s.id_socio AS codigo_socio,                                \n" // 12 CÓDIGO SÓCIO
                + "            to_char(s.filiacao, 'DD/MM/YYYY') as filiacao,             \n" // 13 FILIAÇÃO
                + "            p.profissao AS profissao,                                  \n" // 14 PROFISSÃO
                + "            p.cpf,                                                     \n" // 15 CPF
                + "            p.ds_rg AS rg,                                             \n" // 16 RG 
                + "            max(m.id),                                                 \n" // 17 ID MOVIMENTO 
                + "            c.nr_cartao,                                               \n" // 18 NÚMERO CARTÃO
                + "            c.id,                                                      \n" // 19 
                + "            mc.ds_descricao,                                           \n" // 20 
                + "            p.logradouro,                                              \n" // 21 LOGRADOURO
                + "            p.endereco,                                                \n" // 22 ENDEREÇO
                + "            p.numero,                                                  \n" // 23 NÚMERO
                + "            p.complemento,                                             \n" // 24 COMPLEMENTO
                + "            p.bairro,                                                  \n" // 25 BAIRRO
                + "            p.cidade,                                                  \n" // 26 CIDADE
                + "            p.uf,                                                      \n" // 27 UF
                + "            p.cep,                                                     \n" // 28 CEP
                + "            p.nacionalidade,                                           \n" // 29 NASCIONALIDADE
                + "            to_char(p.dt_nascimento, 'DD/MM/YYYY') as nascimento,      \n" // 30 NASCIMENTO
                + "            p.estado_civil as estado_civil,                            \n" // 31 ESTADO CÍVIL
                + "            p.ctps as carteira,                                        \n" // 32 CARTEIRA (CTPS)
                + "            p.ds_serie as serie,                                       \n" // 33 SÉRIE
                + "            p.ds_orgao_emissao_rg AS orgao_expeditor,                  \n" // 34 ÓRGÃO EXPEDITOR
                + "            p.codigo_funcional,                                        \n" // 35 CÓDIGO FUNCIONAL
                + "            s.parentesco,                                              \n" // 36 PARENTESCO
                + "            s.categoria,                                               \n" // 37 CATEGORIA
                + "            pt.fantasia AS fantasia_titular,                           \n" // 38 FANTASIA EMPRESA - TITULAR
                + "            pt.codigo_funcional AS codigo_funcional_titular,           \n" // 39 CÓDIGO FUNCIONAL - TITULAR
                + "            s.titular AS titular_id,                                   \n" // 40 TITULAR ID
                + "            s.grupo_categoria,                                         \n" // 41 GRUPO CATEGORIA
                + "            f.dt_aposentadoria,                                        \n" // 42 APOSENTADORIA
                + "            f.ds_pai,                                                  \n" // 43 PAI
                + "            f.ds_mae                                                   \n" // 44 MÃE                
                + "       FROM pes_pessoa_vw                    AS p                                                \n"
                + " INNER JOIN soc_socios_vw                    AS s  ON s.codsocio     = p.codigo                  \n"
                + " INNER JOIN pes_fisica                       AS F  ON F.id_pessoa    = p.codigo                  \n"
                + " INNER JOIN soc_carteirinha                  AS c  ON c.id_pessoa    = s.codsocio                \n"
                + " INNER JOIN soc_modelo_carteirinha_categoria AS cc ON s.id_categoria = cc.id_categoria           \n"
                + " INNER JOIN soc_modelo_carteirinha           AS mc ON mc.id          = cc.id_modelo_carteirinha  \n"
                + "  LEFT JOIN pes_pessoa_vw                    AS pt ON pt.codigo      = s.titular                 \n"
                + "  LEFT JOIN fin_movimento                    AS m  ON m.id_pessoa    = c.id_pessoa AND m.id_servicos IN(SELECT id_servicos FROM fin_servico_rotina WHERE id_rotina = 120) \n"
                + "      WHERE s.codsocio = " + id_pessoa;

        textqry += "   GROUP BY p.codigo,                                       \n" // 0 CÓDIGO
                + "             p.nome,                                         \n" // 1 NOME
                + "             p.cnpj,                                         \n" // 2 CNPJ
                + "             p.empresa,                                      \n" // 3 EMPRESA
                + "             to_char(c.dt_emissao, 'DD/MM/YYYY'),            \n" // 4 DATA EMISSÃO
                + "             p.e_cidade,                                     \n" // 5 CIDADE
                + "             to_char(s.validade_carteirinha, 'DD/MM/YYYY'),  \n" // 6 VALIDADE
                + "             p.e_uf,                                         \n" // 7 ESTADO
                + "             to_char(p.admissao, 'DD/MM/YYYY'),              \n" // 8 ADMISSÃO
                + "             p.fantasia,                                     \n" // 9 FANTASIA
                + "             s.matricula,                                    \n" // 10 MATRICULA
                + "             s.nr_via,                                       \n" // 11 VIA
                + "             s.id_socio,                                     \n" // 12 CÓDIGO SÓCIO
                + "             to_char(s.filiacao, 'DD/MM/YYYY'),              \n" // 13 FILIAÇÃO
                + "             p.profissao,                                    \n" // 14 PROFISSÃO
                + "             p.cpf,                                          \n" // 15 CPF
                + "             p.ds_rg,                                        \n" // 16 RG 
                + "             c.nr_cartao,                                    \n" // 17 NÚMERO CARTÃO
                + "             c.id,                                           \n" // 18
                + "             mc.ds_descricao,                                \n" // 19 MODELO CARTEIRINHA
                + "             p.logradouro,                                   \n" // 20
                + "             p.endereco,                                     \n" // 21
                + "             p.numero,                                       \n" // 22
                + "             p.complemento,                                  \n" // 23
                + "             p.bairro,                                       \n" // 24
                + "             p.cidade,                                       \n" // 25
                + "             p.uf,                                           \n" // 26
                + "             p.cep,                                          \n" // 27
                + "             p.nacionalidade,                                \n" // 28
                + "             to_char(p.dt_nascimento, 'DD/MM/YYYY'),         \n" // 29
                + "             p.estado_civil,                                 \n" // 30
                + "             p.ctps,                                         \n" // 31
                + "             p.ds_serie,                                     \n" // 32
                + "             p.ds_orgao_emissao_rg,                          \n" // 34
                + "             p.codigo_funcional,                             \n" // 35
                + "             s.parentesco,                                   \n" // 36
                + "             s.categoria,                                    \n" // 37
                + "             pt.fantasia,                                    \n" // 38
                + "             pt.codigo_funcional,                            \n" // 39
                + "             s.titular,                                      \n" // 40
                + "             s.grupo_categoria,                              \n" // 41
                + "             f.dt_aposentadoria,                             \n" // 42
                + "             f.ds_pai,                                       \n" // 43
                + "             f.ds_mae                                        \n"; // 44
        try {
            Query qry = getEntityManager().createNativeQuery(textqry);
            if (!qry.getResultList().isEmpty()) {
                lista = qry.getResultList();
            }
        } catch (Exception e) {
        }

        return lista;
    }

    public List listaPesquisaEtiqueta(int id_pessoa) {
        List lista = new ArrayList();
        String textqry = "SELECT nome, logradouro, endereco, numero, bairro, cidade, uf, cep, complemento FROM pes_pessoa_vw WHERE codigo = " + id_pessoa;
        try {
            Query qry = getEntityManager().createNativeQuery(textqry);
            if (!qry.getResultList().isEmpty()) {
                lista = qry.getResultList();
            }
        } catch (Exception e) {
        }
        return lista;
    }

    public boolean verificaSocioCarteirinhaExiste(int id_pessoa) {
        try {
            Query qry = getEntityManager().createNativeQuery(" SELECT * FROM soc_carteirinha WHERE id_pessoa = " + id_pessoa + " AND dt_emissao = current_date ");
            if (!qry.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public List<HistoricoCarteirinha> listaHistoricoCarteirinha(int id_pessoa) {
        String text_qry = "SELECT hc FROM HistoricoCarteirinha hc WHERE hc.carteirinha.pessoa.id = " + id_pessoa;
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return qry.getResultList();

        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List<AutorizaImpressaoCartao> listaAutoriza(int id_pessoa, int id_modelo) {
        String text_qry = "SELECT ai FROM AutorizaImpressaoCartao ai WHERE ai.pessoa.id = " + id_pessoa + " AND ai.modeloCarteirinha.id = " + id_modelo;
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList<AutorizaImpressaoCartao>();
        }
    }

    public AutorizaImpressaoCartao pesquisaAutorizaSemHistorico(int id_pessoa, int id_modelo) {
        String text_qry = "SELECT ai FROM AutorizaImpressaoCartao ai WHERE ai.pessoa.id = " + id_pessoa + " AND ai.modeloCarteirinha.id = " + id_modelo + " AND ai.historicoCarteirinha IS NULL";
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return (AutorizaImpressaoCartao) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public AutorizaImpressaoCartao pesquisaAutorizaPorHistorico(int id_historico) {
        String text_qry = "SELECT ai FROM AutorizaImpressaoCartao ai WHERE ai.historicoCarteirinha.id = " + id_historico;
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return (AutorizaImpressaoCartao) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<SocioCarteirinha> listaSocioCarteirinhaAutoriza(int id_pessoa, int id_modelo) {
        String text_qry = "SELECT sc FROM SocioCarteirinha sc WHERE sc.pessoa.id = " + id_pessoa + " AND sc.modeloCarteirinha.id = " + id_modelo;
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList<SocioCarteirinha>();
        }
    }

    public ModeloCarteirinha pesquisaModeloCarteirinha(int id_categoria, int id_rotina) {
        String text_qry = "SELECT mcc.modeloCarteirinha FROM ModeloCarteirinhaCategoria mcc";

        if (id_rotina == -1 && id_categoria == -1) {

        } else if (id_categoria != -1 && id_rotina == -1) {
            text_qry += " WHERE mcc.categoria IS NOT NULL AND mcc.categoria.id = " + id_categoria;
        } else if (id_categoria != -1 && id_rotina != -1) {
            text_qry += " WHERE mcc.categoria IS NOT NULL AND mcc.categoria.id = " + id_categoria + " AND mcc.rotina.id = " + id_rotina;
        } else if (id_categoria == -1 && id_rotina != -1) {
            text_qry += " WHERE mcc.rotina.id = " + id_rotina + " AND mcc.categoria IS NULL";
        }

        try {
            Query query = getEntityManager().createQuery(text_qry);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (ModeloCarteirinha) query.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public ModeloCarteirinhaCategoria pesquisaModeloCarteirinhaCategoria(int id_modelo, int id_categoria, int id_rotina) {
        String text_qry = "SELECT mcc FROM ModeloCarteirinhaCategoria mcc WHERE mcc.modeloCarteirinha.id = " + id_modelo + " ";

        if (id_rotina == -1 && id_categoria == -1) {

        } else if (id_categoria != -1 && id_rotina == -1) {
            text_qry += " AND mcc.categoria IS NOT NULL AND mc.ccategoria.id = " + id_categoria;
        } else if (id_categoria != -1 && id_rotina != -1) {
            text_qry += " AND mcc.categoria IS NOT NULL AND mcc.categoria.id = " + id_categoria + " AND mcc.rotina.id = " + id_rotina;
        } else if (id_categoria == -1 && id_rotina != -1) {
            text_qry += " AND mcc.categoria IS NULL AND mcc.rotina.id = " + id_rotina;
        }

        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return (ModeloCarteirinhaCategoria) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public SocioCarteirinha pesquisaCarteirinhaPessoa(int id_pessoa, int id_modelo) {
        String text_qry = "SELECT sc FROM SocioCarteirinha sc WHERE sc.pessoa.id = " + id_pessoa + " AND sc.modeloCarteirinha.id = " + id_modelo;
        try {
            Query qry = getEntityManager().createQuery(text_qry);
            return (SocioCarteirinha) qry.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List findByPessoa(Integer pessoa_id) {
        try {
            Query query = getEntityManager().createQuery("SELECT SC FROM SocioCarteirinha SC WHERE SC.pessoa.id = :pessoa_id");
            query.setParameter("pessoa_id", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
