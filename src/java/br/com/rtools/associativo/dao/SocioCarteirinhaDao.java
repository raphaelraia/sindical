package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.AutorizaImpressaoCartao;
import br.com.rtools.associativo.HistoricoCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Debugs;
import br.com.rtools.utilitarios.Messages;
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
        if ((filter.equals("nome_titular") || filter.equals("cpf_titular") || filter.equals("nascimento") || filter.equals("nome") || filter.equals("codigo") || filter.equals("cpf") || filter.equals("empresa") || filter.equals("cnpj") || filter.equals("matricula")) && query.isEmpty()) {
            return new ArrayList();
        }

        Boolean ignoreData = false;
        if ((filter.equals("nome_titular") || filter.equals("cpf_titular") || filter.equals("nascimento") || filter.equals("nome") || filter.equals("codigo") || filter.equals("cpf") || filter.equals("empresa") || filter.equals("cnpj") || filter.equals("matricula") && !query.isEmpty())) {
            ignoreData = true;
        }

        String queryString
                = "SELECT P.id                                                                          \n "
                + "  FROM pes_pessoa AS P                                                               \n "
                + " INNER JOIN pes_fisica AS F ON F.id_pessoa = P.id                                    \n "
                + " INNER JOIN soc_carteirinha AS SC ON SC.id_pessoa = P.id                             \n "
                + " INNER JOIN soc_modelo_carteirinha AS MC ON MC.id = SC.id_modelo_carteirinha         \n "
                + " INNER JOIN conf_social AS CS ON CS.id = 1                                           \n "
                + "  LEFT JOIN fin_servico_pessoa AS SP on SP.id_pessoa = P.id                          \n "
                + "  LEFT JOIN soc_socios AS SO on SO.id_servico_pessoa = SP.id                         \n "
                + "  LEFT JOIN matr_socios AS MT ON MT.id = SO.id_matricula_socios                      \n "
                + "  LEFT JOIN pes_pessoa AS TI ON TI.id = MT.id_titular                                \n ";

        queryString
                += "  LEFT JOIN soc_categoria AS C ON C.id = mt.id_categoria AND mt.id_categoria > 0    \n"
                + "  LEFT JOIN fin_movimento AS M ON M.id_beneficiario = SC.id_pessoa AND M.id_servicos IN (SELECT id_servico_cartao FROM seg_registro) AND m.dt_vencimento >='06/04/2015' \n"
                + "  LEFT JOIN soc_historico_carteirinha SH ON SH.id_carteirinha = SC.id AND SH.id_movimento = M.id \n";

        Registro registro = (Registro) new Dao().find(new Registro(), 1);

        try {
            // NÃO IMPRESSOS
            List listWhere = new ArrayList();

            if (status.equals("pendentes")) {

                String subquery = ""
                        + " (                                                                                              \n "
                        + "     (MT.id IS NULL AND CS.is_cobranca_carteirinha_nao_socio = false AND SC.dt_emissao IS NULL) \n "
                        + "     OR (C.is_cobranca_carteirinha = false AND SC.dt_emissao IS NULL)                           \n "
                        + "     OR (C.is_cobranca_carteirinha = true AND M.id_servicos IS NOT NULL AND SH.id_movimento IS NULL AND M.is_ativo = true) \n "
                        + "     OR (C.is_cobranca_carteirinha = true AND P.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE id_historico_carteirinha IS NULL)) \n "
                        + "     OR (MT.id is null AND CS.is_cobranca_carteirinha_nao_socio = true AND M.id_servicos IS NOT NULL AND SH.id_movimento IS NULL AND M.is_ativo = true) \n "
                        + "     OR (MT.id is null AND CS.is_cobranca_carteirinha_nao_socio = true AND P.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE id_historico_carteirinha IS NULL)) \n "
                        + ") \n ";

                listWhere.add(subquery);

                if (!ignoreData) {
                    if ((start_date != null && !start_date.isEmpty()) || type_date.equals("hoje")) {
                        switch (type_date) {
                            case "hoje":
                                listWhere.add("SC.dt_emissao = CURRENT_DATE");
                                break;
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
                                    listWhere.add("SC.dt_emissao BETWEEN  '" + start_date + "' AND '" + finish_date + "'");
                                }
                                break;
                        }
                    }
                } else {
                    listWhere.add("SC.dt_emissao IS NULL");
                }
            } else {

                if (!ignoreData) {
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
                            if ((start_date != null && !start_date.isEmpty()) || type_date.equals("hoje")) {
                                switch (type_date) {
                                    case "igual":
                                        listWhere.add("SC.dt_emissao = '" + start_date + "'");
                                        break;
                                    case "hoje":
                                        listWhere.add("SC.dt_emissao = current_date");
                                        break;
                                    case "apartir":
                                        listWhere.add("SC.dt_emissao >= '" + start_date + "'");
                                        break;
                                    case "ate":
                                        listWhere.add("SC.dt_emissao <= '" + start_date + "'");
                                        break;
                                    case "faixa":
                                        if (start_date != null && !start_date.isEmpty()) {
                                            listWhere.add("SC.dt_emissao BETWEEN  '" + start_date + "' AND '" + finish_date + "'");
                                        }
                                        break;
                                }
                            } else {
                                if (!type_date.equals("nenhum")) {
                                    Messages.warn("Sistema", "INFORMAR UMA DATA VÁLIDA");
                                    return new ArrayList();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    listWhere.add("SC.dt_emissao IS NOT NULL");
                }
                if (operador_id != null) {
                    listWhere.add("SH.id_usuario = " + operador_id);
                }
            }

            // PESSOA / NOME
            if (!inPessoasImprimir.isEmpty()) {
                filter = "in_pessoas";
            }
            // (filtro = 'NENHUM' )
            if (filter.isEmpty() && type_date.isEmpty() && status.equals("pendentes")) {
                listWhere.add(
                        " ( \n "
                        + " (C.is_cobranca_carteirinha = false AND SC.dt_criacao >= current_date - 30) \n "
                        + " OR (C.is_cobranca_carteirinha = true AND M.id_servicos IS NOT NULL AND SH.id_movimento IS NULL AND M.is_ativo = true AND M.dt_vencimento >= current_date-30) \n "
                        + " OR (C.is_cobranca_carteirinha = true AND P.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE id_historico_carteirinha IS NULL AND dt_emissao >= current_date-30)) \n "
                        + " OR (MT.id IS NULL AND CS.is_cobranca_carteirinha_nao_socio = true AND M.id_servicos IS NOT NULL AND SH.id_movimento IS NULL AND M.is_ativo = true AND M.dt_vencimento >= current_date-30) \n "
                        + " OR (MT.id IS NULL AND P.ds_nome IS NOT NULL AND P.ds_nome <> '' AND CS.is_cobranca_carteirinha_nao_socio = false) \n"
                        + ") \n "
                );
            }

            switch (filter) {
                case "nome":
                    listWhere.add("TRIM(UPPER(func_translate(P.ds_nome))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))");
                    break;
                case "nome_titular":
                    listWhere.add("TRIM(UPPER(func_translate(TI.ds_nome))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))");
                    break;
                case "matricula":
                    listWhere.add("MT.nr_matricula = " + query + "");
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
                    listWhere.add("TI.ds_documento LIKE '%" + query + "%'");
                    break;
                case "empresa":
                    // listWhere.add("TRIM(UPPER(func_translate(P.empresa))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))"); *
                    break;
                case "cnpj":
                    //listWhere.add("P.cnpj LIKE '%" + query + "%'");
                    break;
                case "nascimento":
                    listWhere.add("P.dt_nascimento = '" + query + "'");
                    break;
                default:
                    break;
            }

            // ROGÉRIO PODIU PRA COMENTAR O CAMPO ABAIXO - 07/10/2015
            // SE NÃO FOR SÓCIO (ACADEMIA)
            listWhere.add("SC.is_ativo = true");
            // QUE POSSUEM FOTOS
            listWhere.add(
                    " ( F.ds_foto <> '' OR ( P.id IN (SELECT id_pessoa FROM soc_autoriza_impressao_cartao WHERE is_foto = TRUE AND id_historico_carteirinha IS NULL) OR MC.is_foto = false ) ) \n"
            );

            if (filial_id != null) {
                listWhere.add("MS.id_filial = " + filial_id + "");
            }
            // QUE POSSUEM FOTOS
            if (!registro.isCarteirinhaDependente()) {
                listWhere.add("SO.id_parentesco = 1");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + "\n";
                }
            }
            // GROUP DA QUERY
            queryString += " GROUP BY P.id ";

            Query qry = getEntityManager().createNativeQuery(queryString);
            List<Object> result = qry.getResultList();
            String in = "";
            if (!result.isEmpty()) {
                for (Object line : result) {
                    if (in.isEmpty()) {
                        in = ((Integer) ((List) line).get(0)).toString();
                    } else {
                        in += ", " + ((Integer) ((List) line).get(0)).toString();
                    }
                }
            } else {
                return new ArrayList();
            }

            String queryString2
                    = "SELECT " + getCartaoAlias() + " FROM pessoa_vw P \n ";

            queryString2
                    += " INNER JOIN soc_carteirinha         AS SC ON SC.id_pessoa = P.codigo                       \n"
                    + " INNER JOIN soc_modelo_carteirinha  AS MC ON MC.id      = SC.id_modelo_carteirinha         \n"
                    + " INNER JOIN conf_social             AS CS ON CS.id = 1                                     \n"
                    + " LEFT JOIN soc_categoria            AS C ON C.id = P.id_categoria AND P.id_categoria > 0   \n"
                    + " LEFT JOIN fin_movimento            AS M ON M.id_beneficiario = SC.id_pessoa AND M.id_servicos IN (SELECT id_servico_cartao FROM seg_registro) AND m.dt_vencimento >='06/04/2015' \n"
                    + " LEFT JOIN soc_historico_carteirinha SH ON SH.id_carteirinha = SC.id AND SH.id_movimento = M.id ";

            queryString2
                    += " WHERE p.codigo > 0 and p.codigo in \n "
                    + "( " 
                    + in 
                    + " ) \n";
            
            queryString2
                    += "GROUP BY " + getCartaoGroup();
            
            // ORDEM DA QUERY
            switch (indexOrdem) {
                case "0":
                    queryString2 += " ORDER BY P.nome ";
                    break;
                case "1":
                    queryString2 += " ORDER BY P.empresa, P.cnpj, P.nome ";
                    break;
                case "2":
                    queryString2 += " ORDER BY P.cnpj, P.nome ";
                    break;
                case "3":
                    queryString2 += " ORDER BY SC.dt_emissao DESC, P.nome ";
                    break;
                case "4":
                    queryString2 += " ORDER BY SC.dt_emissao DESC, P.empresa, P.cnpj, P.nome ";
                    break;
                default:
                    break;
            }
            
            Debugs.put("habilitaDebugQuery", queryString2);
            qry = getEntityManager().createNativeQuery(queryString2);

            return qry.getResultList();
        } catch (NumberFormatException e) {
            e.getMessage();
        }

        return new ArrayList();
    }

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
        String text_qry = "SELECT ai FROM AutorizaImpressaoCartao ai WHERE ai.pessoa.id = " + id_pessoa + " AND ai.modeloCarteirinha.id = " + id_modelo + " AND ai.historicoCarteirinha IS NULL";
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

    public String getCartaoAlias() {
        return "               P.codigo,                                                \n" // 0 CÓDIGO
                + "            P.nome,                                                  \n" // 1 NOME
                + "            P.cnpj,                                                  \n" // 2 CNPJ
                + "            P.empresa,                                               \n" // 3 RAZÃO SOCIAL
                + "            to_char(sc.dt_emissao, 'DD/MM/YYYY') AS impresso,        \n" // 4 EMISSÃO
                + "            P.cidade,                                                \n" // 5 EMPRESA CIDADE
                + "            to_char(sc.dt_validade_carteirinha, 'DD/MM/YYYY') AS validade_carteirinha, \n" // 6 VALIDADE
                + "            P.uf,                                                    \n" // 7 EMPRESA UF
                + "            to_char(p.admissao, 'DD/MM/YYYY') AS admissao,           \n" // 8 ADMISSÃO
                + "            P.fantasia,                                              \n" // 9 FANTASIA
                + "            P.matricula,                                             \n" // 10 MATRÍCULA
                + "            SC.nr_via,                                               \n" // 11 VIA
                + "            P.codigo,                                                \n" // 12 CÓDIGO
                + "            to_char(P.filiacao, 'DD/MM/YYYY') AS filiacao,           \n" // 13 FILIAÇÃO
                + "            P.profissao,                                             \n" // 14 PROFISSÃO / CARGO
                + "            P.cpf,                                                   \n" // 15 CPF
                + "            P.ds_rg,                                                 \n" // 16 RG
                + "            max(m.id),                                               \n" // 17 MOVIMENTO MAX(ID)
                + "            SC.nr_cartao,                                            \n" // 18 CARTÃO NÚMERO
                + "            SC.id,                                                   \n" // 19 CARTEIRINHA ID
                + "            MC.ds_descricao,                                         \n" // 20 CATEGORIA
                + "            P.logradouro,                                            \n" // 21 LOGRADOURO
                + "            P.endereco,                                              \n" // 22 ENDEREÇO
                + "            P.numero,                                                \n" // 23 NÚMERO 
                + "            P.complemento,                                           \n" // 24 COMPLEMENTO
                + "            P.bairro,                                                \n" // 25 BAIRRO
                + "            P.cidade,                                                \n" // 26 CIDADE
                + "            P.uf,                                                    \n" // 27 UF
                + "            P.cep,                                                   \n" // 28 CEP
                + "            P.nacionalidade,                                         \n" // 29 NASCIONALIDADE
                + "            to_char(P.dt_nascimento, 'DD/MM/YYYY') AS nascimento,    \n" // 30 NASCIMENTO
                + "            P.estado_civil,                                          \n" // 31 ESTADO CÍVIL
                + "            P.ctps,                                                  \n" // 32 CARTEIRA (CTPS)
                + "            P.ds_serie as serie,                                     \n" // 33 SÉRIE
                + "            P.ds_orgao_emissao_rg AS orgao_expeditor,                \n" // 34 ORGÃO EMISSÃO RG
                + "            P.codigo_funcional,                                      \n" // 35 CÓDIGO FUNCIONAL
                + "            P.parentesco,                                            \n" // 36 PARENTESCO
                + "            P.categoria,                                             \n" // 37 CATEGORIA
                + "            P.fantasia AS fantasia_titular,                          \n" // 38 FANTASIA EMPRESA - TITULAR
                + "            P.codigo_funcional AS codigo_funcional_titular,          \n" // 39 CÓDIGO FUNCIONAL - TITULAR
                + "            P.id_titular,                                            \n" // 40 TITULAR ID
                + "            P.grupo_categoria,                                       \n" // 41 GRUPO CATEGORIA
                + "            P.dt_aposentadoria,                                      \n" // 42 APOSENTADORIA
                + "            P.pai,                                                   \n" // 43 PAI
                + "            P.mae                                                    \n" // 44 MÃE
                ;
    }

    public String getCartaoGroup() {
        // GROUP DA QUERY
        return "             P.codigo,                                      \n"
                + "          P.nome,                                        \n"
                + "          P.cnpj,                                        \n"
                + "          P.empresa,                                     \n"
                + "          to_char(SC.dt_emissao, 'DD/MM/YYYY'),          \n"
                + "          P.cidade,                                      \n"
                + "          to_char(SC.dt_validade_carteirinha, 'DD/MM/YYYY'), \n"
                + "          P.uf,                                          \n"
                + "          to_char(P.admissao, 'DD/MM/YYYY'),             \n"
                + "          P.fantasia,                                    \n"
                + "          P.matricula,                                   \n"
                + "          SC.nr_via,                                     \n"
                + "          to_char(P.filiacao, 'DD/MM/YYYY'),             \n"
                + "          P.profissao,                                   \n"
                + "          P.cpf,                                         \n"
                + "          P.ds_rg,                                       \n"
                + "          SC.nr_cartao,                                  \n"
                + "          SC.id,                                         \n"
                + "          MC.ds_descricao,                               \n"
                + "          P.logradouro,                                  \n"
                + "          P.endereco,                                    \n"
                + "          P.numero,                                      \n"
                + "          P.complemento,                                 \n"
                + "          P.bairro,                                      \n"
                + "          P.cidade,                                      \n"
                + "          P.uf,                                          \n"
                + "          P.cep,                                         \n"
                + "          P.nacionalidade,                               \n"
                + "          to_char(P.dt_nascimento, 'DD/MM/YYYY'),        \n"
                + "          P.estado_civil,                                \n"
                + "          P.ctps,                                        \n"
                + "          P.ds_serie,                                    \n"
                + "          P.ds_orgao_emissao_rg,                         \n"
                + "          P.codigo_funcional,                            \n"
                + "          P.parentesco,                                  \n"
                + "          P.categoria,                                   \n"
                + "          P.fantasia,                                    \n"
                + "          P.codigo_funcional,                            \n"
                + "          P.id_titular,                                 \n"
                + "          P.grupo_categoria,                             \n"
                + "          P.dt_aposentadoria,                            \n"
                + "          P.pai,                                         \n"
                + "          P.mae                                          \n";
    }

}
