package br.com.rtools.associativo.dao;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class ExtratoTelaSocialDao extends DB {

    public List<Vector> listaMovimentosSocial(
            String tipo, String ordenacao, String faixa_data, String tipo_data, String data_inicial, String data_final, String referencia_inicial, String referencia_final, String boleto_inicial, String boleto_final, String tipo_pessoa, Integer pessoa_id, Integer id_servico, Integer id_tipo_servico, Integer id_status_retorno, String id_boleto_adicionado_remessa, Integer id_conta_cobranca, Integer filial_id
    ) {
        List listWhere = new ArrayList();
        String queryString
                = "      -- ExtratoTelaSocialDao->listaMovimentosSocial(" + Usuario.getUsuario().getPessoa().getNome() + ") \n\n"
                + "      SELECT M.id,                                           \n " // 00
                + "             PR.ds_documento,                                \n " // 01
                + "             PR.ds_nome,                                     \n " // 02
                + "             PT.ds_nome,                                     \n " // 03
                + "             PB.ds_nome,                                     \n " // 04
                + "             BO.ds_boleto AS boleto,                         \n " // 05
                + "             S.ds_descricao,                                 \n " // 06
                + "             TS.ds_descricao,                                \n " // 07
                + "             M.ds_referencia,                                \n " // 08
                + "             M.dt_vencimento,                                \n " // 09
                + "             M.nr_valor,                                     \n " // 10
                + "             B.dt_baixa,                                     \n " // 11
                + "             M.nr_valor_baixa,                               \n " // 12
                + "             M.nr_taxa,                                      \n " // 13
                + "             M.id_baixa,                                     \n " // 14
                + "             BO.id,                                          \n " // 15
                + "             PT.ds_documento,                                \n " // 16
                + "             PB.ds_documento,                                \n " // 17
                + "             BO.dt_vencimento,                               \n " // 18
                + "             BO.dt_vencimento_original,                      \n " // 19
                + "             B.dt_importacao,                                \n " // 20
                + "             B.dt_ocorrencia,                                \n " // 21
                + "             R.ds_rotina                                     \n " // 22
                + "        FROM fin_movimento M \n "
                + "  INNER JOIN fin_lote         L  ON L.id  = M.id_lote                            \n "
                + "  INNER JOIN pes_pessoa       PR ON PR.id = M.id_pessoa -- RESPONSAVEL           \n "
                + "  INNER JOIN pes_pessoa       PT ON PT.id = M.id_titular -- TITULAR              \n "
                + "  INNER JOIN pes_pessoa       PB ON PB.id = M.id_beneficiario -- BENEFICIARIO    \n "
                + "  INNER JOIN fin_servicos     S  ON S.id  = M.id_servicos                        \n "
                + "  INNER JOIN fin_tipo_servico TS ON TS.id = M.id_tipo_servico                    \n "
                + "  INNER JOIN seg_rotina       R  ON R.id  = L.id_rotina                          \n "
                + "   LEFT JOIN fin_baixa        B  ON B.id  = M.id_baixa                           \n "
                + "   LEFT JOIN fin_boleto       BO ON BO.nr_ctr_boleto = M.nr_ctr_boleto           \n ";
        // PEGAR MOVIMENTOS QUE FORAM EXCLUIDOS ----------------------------------
        //                + "   LEFT JOIN fin_movimento_inativo mi ON m.id = mi.id_movimento \n "
        //                + "   LEFT JOIN seg_usuario u ON u.id = mi.id_usuario \n "
        //                + "   LEFT JOIN pes_pessoa pu ON pu.id = u.id_pessoa \n "
        // PEGAR MOVIMENTOS QUE FORAM EXCLUIDOS ----------------------------------
        // + "  WHERE M.is_ativo = true AND s.id NOT IN (SELECT sr.id_servicos FROM fin_servico_rotina sr WHERE sr.id_rotina = 4) \n ";
        listWhere.add("M.id_servicos NOT IN (SELECT SR.id_servicos FROM fin_servico_rotina SR WHERE SR.id_rotina = 4) ");
        listWhere.add("M.is_ativo = true");

        switch (tipo) {
            case "todos":
                break;

            case "recebidas":
                listWhere.add("M.id_baixa IS NOT NULL");
                break;

            case "nao_recebidas":
                listWhere.add("M.id_baixa IS NULL");
                break;

            case "atrasadas":
                listWhere.add("M.id_baixa IS NULL AND m.dt_vencimento < CURRENT_DATE");
                break;
        }

        if (pessoa_id != null && pessoa_id != -1) {
            switch (tipo_pessoa) {
                case "nenhum":
                    //and += " AND M.id_pessoa = "+pessoa_id;
                    break;
                case "responsavel":
                    listWhere.add("M.id_pessoa = " + pessoa_id);
                    break;
                case "titular":
                    listWhere.add("M.id_titular = " + pessoa_id);
                    break;
                case "beneficiario":
                    listWhere.add("M.id_beneficiario = " + pessoa_id);
                    break;
            }
        }
        switch (faixa_data) {
            case "ocorrencia":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_ocorrencia = '" + data_inicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_ocorrencia >= '" + data_inicial + "'");
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_ocorrencia <= '" + data_inicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            listWhere.add("B.dt_ocorrencia >= '" + data_inicial + "' AND B.dt_ocorrencia <= '" + data_final + "'");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "baixa":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_baixa = '" + data_inicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_baixa >= '" + data_inicial + "'");
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_baixa <= '" + data_inicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            listWhere.add("B.dt_baixa >= '" + data_inicial + "' AND B.dt_baixa <= '" + data_final + "'");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "importacao":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_importacao = '" + data_inicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_importacao >= '" + data_inicial + "'");
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_importacao <= '" + data_inicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            listWhere.add("B.dt_importacao >= '" + data_inicial + "' AND B.dt_importacao <= '" + data_final + "'");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "vencimento":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("M.dt_vencimento = '" + data_inicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("M.dt_vencimento >= '" + data_inicial + "'");
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("M.dt_vencimento <= '" + data_inicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            listWhere.add("M.dt_vencimento >= '" + data_inicial + "' AND M.dt_vencimento <= '" + data_final + "'");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "lancamento":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("L.dt_lancamento = '" + data_inicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("L.dt_lancamento >= '" + data_inicial + "'");
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("L.dt_lancamento <= '" + data_inicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            listWhere.add("L.dt_lancamento >= '" + data_inicial + "' AND L.dt_lancamento <= '" + data_final + "'");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "registro":
                switch (tipo_data) {
                    case "igual":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_cobranca_registrada = '" + data_inicial + "'");
                        }
                        break;
                    case "apartir":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_cobranca_registrada >= '" + data_inicial + "'");
                        }
                        break;
                    case "ate":
                        if (!data_inicial.isEmpty()) {
                            listWhere.add("B.dt_cobranca_registrada <= '" + data_inicial + "'");
                        }
                        break;
                    case "faixa":
                        if (!data_inicial.isEmpty() && !data_final.isEmpty()) {
                            listWhere.add("B.dt_cobranca_registrada >= '" + data_inicial + "' AND B.dt_cobranca_registrada <= '" + data_final + "'");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "referencia":

                switch (tipo_data) {
                    case "igual":
                        if (!referencia_inicial.isEmpty()) {
                            String ini = referencia_inicial.substring(3, 7) + referencia_inicial.substring(0, 2);
                            listWhere.add("substring(M.ds_referencia, 4, 8)|| substring(M.ds_referencia, 0, 3) = '" + ini + "'");
                        }
                        break;
                    case "apartir":
                        if (!referencia_inicial.isEmpty()) {
                            String ini = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
                            listWhere.add("substring(M.ds_referencia, 4, 8)|| substring(M.ds_referencia, 0, 3) >= '" + ini + "'");
                        }
                        break;
                    case "ate":
                        if (!referencia_inicial.isEmpty()) {
                            String ini = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
                            listWhere.add("substring(M.ds_referencia, 4, 8)|| substring(M.ds_referencia, 0, 3) <= '" + ini + "'");
                        }
                        break;
                    case "faixa":
                        if (!referencia_inicial.isEmpty() && !referencia_final.isEmpty()) {
                            String ini = referencia_inicial.substring(3, 7) + referencia_inicial.substring(0, 2);
                            String fin = referencia_final.substring(3, 7) + referencia_final.substring(0, 2);
                            listWhere.add("substring(M.ds_referencia, 4, 8)|| substring(M.ds_referencia, 0, 3) >= '" + ini + "' AND substring(M.ds_referencia, 4, 8)|| substring(M.ds_referencia, 0, 3) <= '" + fin + "' \n ");
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
//        switch (tipoDataPesquisa) {
//            case "recebimento":
//                if (!dataInicial.isEmpty() && dataFinal.isEmpty()) {
//                    and += " and b.dt_baixa >= '" + dataInicial + "'";
//                } else if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
//                    and += " and b.dt_baixa >= '" + dataInicial + "' and b.dt_baixa <= '" + dataFinal + "'";
//                } else if (dataInicial.isEmpty() && !dataFinal.isEmpty()) {
//                    and += "' and b.dt_baixa <= '" + dataFinal + "'";
//                }
//                break;
//            case "importacao":
//                if (!dataInicial.isEmpty() && dataFinal.isEmpty()) {
//                    and += " and b.dt_importacao >= '" + dataInicial + "'";
//                } else if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
//                    and += " and b.dt_importacao >= '" + dataInicial + "' and b.dt_importacao <= '" + dataFinal + "'";
//                } else if (dataInicial.isEmpty() && !dataFinal.isEmpty()) {
//                    and += "' and b.dt_importacao <= '" + dataFinal + "'";
//                }
//                break;
//            case "vencimento":
//                if (!dataInicial.isEmpty() && dataFinal.isEmpty()) {
//                    and += " and m.dt_vencimento >= '" + dataInicial + "'";
//                } else if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
//                    and += " and m.dt_vencimento >= '" + dataInicial + "' and m.dt_vencimento <= '" + dataFinal + "'";
//                } else if (dataInicial.isEmpty() && !dataFinal.isEmpty()) {
//                    and += "' and m.dt_vencimento <= '" + dataFinal + "'";
//                }
//                break;
//            case "referencia":
//                if (!dataRefInicial.isEmpty() && dataRefFinal.isEmpty()) {
//                    String ini = dataRefInicial.substring(3, 7) + dataRefInicial.substring(0, 2);
//                    and += " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "'";
//                } else if (!dataRefInicial.isEmpty() && !dataRefFinal.isEmpty()) {
//                    String ini = dataRefInicial.substring(3, 7) + dataRefInicial.substring(0, 2);
//                    String fin = dataRefFinal.substring(3, 7) + dataRefFinal.substring(0, 2);
//                    and += " and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) >= '" + ini + "' and substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "'";
//                } else if (dataRefInicial.isEmpty() && !dataRefFinal.isEmpty()) {
//                    String fin = dataRefFinal.substring(3, 7) + dataRefFinal.substring(0, 2);
//                    and += "' substring(m.ds_referencia, 4, 8)|| substring(m.ds_referencia, 0, 3) <= '" + fin + "'";
//                }
//                break;
//        }

        if (!boleto_inicial.isEmpty() || !boleto_final.isEmpty()) {
            if (boleto_inicial.equals("0")) {
                boleto_inicial = "";
            }
            if (boleto_final.equals("0")) {
                boleto_final = "";
            }
            if (!boleto_inicial.isEmpty() && boleto_final.isEmpty()) {
                listWhere.add("BO.ds_boleto >= '" + boleto_inicial + "' ");
            } else if (!boleto_inicial.isEmpty() && !boleto_final.isEmpty()) {
                listWhere.add("BO.ds_boleto >= '" + boleto_inicial + "'");
                listWhere.add("BO.ds_boleto <= '" + boleto_final + "' ");
            } else if (boleto_inicial.isEmpty() && !boleto_final.isEmpty()) {
                listWhere.add("BO.ds_boleto <= '" + boleto_final + "'");
            }
        }

        if (filial_id != null) {
            listWhere.add("L.id_filial = " + filial_id);
        }

        if (id_servico != 0) {
            listWhere.add("M.id_servicos = " + id_servico);
        }

        if (id_tipo_servico != 0) {
            listWhere.add("M.id_tipo_servico = " + id_tipo_servico);
        }

        switch (id_status_retorno) {
            case -1:
                // TODOS BOLETOS
                break;
            case -2:
                // NÃO REGISTRADOS
                listWhere.add("(BO.id_status_retorno IS NULL OR BO.id_status_retorno = 4) AND BO.dt_vencimento > CURRENT_DATE");
                break;
            default:
                // STATUS
                listWhere.add("BO.id_status_retorno = " + id_status_retorno);
                break;
        }

        if (!id_boleto_adicionado_remessa.isEmpty()) {
            listWhere.add("BO.id NOT IN (" + id_boleto_adicionado_remessa + ")");
        }

        if (id_conta_cobranca != -1) {
            listWhere.add("BO.id_conta_cobranca = " + id_conta_cobranca);
        }
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        String orderBy = "";
        switch (ordenacao) {
            case "referencia":
                orderBy += " ORDER BY substring(M.ds_referencia, 4, 8)|| substring(M.ds_referencia, 0, 3) DESC \n";
                break;

            case "vencimento":
                orderBy += " ORDER BY M.dt_vencimento DESC \n";
                break;

            case "baixa":
                orderBy += " ORDER BY B.dt_baixa DESC \n";
                break;

            case "ocorrencia":
                orderBy += " ORDER BY B.dt_ocorrencia DESC \n";
                break;

            case "importacao":
                orderBy += " ORDER BY B.dt_importacao DESC \n";
                break;

            case "boleto":
                orderBy += " ORDER BY BO.ds_boleto DESC \n";
                break;
        }

        queryString += orderBy + " LIMIT 15000 ";
        Debugs.put("habilitaDebugQuery", queryString);
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Servicos> listaServicosAssociativo() {
        try {
            Query qry = getEntityManager().createQuery(
                    "SELECT s FROM Servicos s where s.id NOT IN (SELECT sr.servicos.id FROM ServicoRotina sr WHERE sr.rotina.id = 4) ORDER BY s.descricao"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
