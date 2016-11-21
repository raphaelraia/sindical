package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.AutorizaImpressaoCartao;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class AutorizaImpressaoCartaoDao extends DB {

    public List find(String status, String filter, String query, Integer operador_id, String type_date, String start_date, String finish_date) {
        if ((filter.equals("nascimento") || filter.equals("nome") || filter.equals("codigo") || filter.equals("cpf")) && query.isEmpty()) {
            return new ArrayList();
        }
        try {
            String queryString
                    = " -- AutorizaImpressaoCartaoDao->find() \n\n "
                    + "     SELECT AIC.*                                                        \n"
                    + "       FROM soc_autoriza_impressao_cartao AS AIC                         \n"
                    + " INNER JOIN pes_pessoa                    AS P   ON P.id = AIC.id_pessoa \n"
                    + " INNER JOIN pes_fisica                    AS F   ON P.id = F.id_pessoa   \n";
            if (status.equals("impressos")) {
                queryString += " INNER JOIN soc_historico_carteirinha     AS HC  ON HC.id = AIC.id_historico_carteirinha  \n";
            }

            // NÃO IMPRESSOS
            List listWhere = new ArrayList();
            switch (status) {
                case "hoje":
                    listWhere.add("AIC.dt_emissao = current_date");
                    break;
                case "ontem":
                    listWhere.add("AIC.dt_emissao = current_date-1");
                    break;
                case "ultimos_30_dias":
                    listWhere.add("AIC.dt_emissao BETWEEN current_date-30 AND current_date");
                    break;
                case "todos":
                    break;
                case "emissao":
                    if (start_date != null && !start_date.isEmpty()) {
                        switch (type_date) {
                            case "igual":
                                listWhere.add("AIC.dt_emissao = '" + start_date + "'");
                                break;
                            case "apartir":
                                listWhere.add("AIC.dt_emissao >= '" + start_date + "'");
                                break;
                            case "ate":
                                listWhere.add("AIC.dt_emissao <= '" + start_date + "'");
                                break;
                            case "faixa":
                                if (!start_date.isEmpty()) {
                                    listWhere.add("AIC.dt_emissao BETWEEN  '" + start_date + "' AND '" + finish_date + "'");
                                }
                                break;
                        }
                    }
                    break;
                case "impressos":
                    if (start_date != null && !start_date.isEmpty()) {
                        switch (type_date) {
                            case "igual":
                                listWhere.add("HC.dt_emissao = '" + start_date + "'");
                                break;
                            case "apartir":
                                listWhere.add("HC.dt_emissao >= '" + start_date + "'");
                                break;
                            case "ate":
                                listWhere.add("HC.dt_emissao <= '" + start_date + "'");
                                break;
                            case "faixa":
                                if (!start_date.isEmpty()) {
                                    listWhere.add("HC.dt_emissao BETWEEN  '" + start_date + "' AND '" + finish_date + "'");
                                }
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
            if (operador_id != null) {
                listWhere.add("AIC.id_usuario = " + operador_id);
            }

            // PESSOA / NOME
            switch (filter) {
                case "nome":
                    listWhere.add("TRIM(UPPER(func_translate(P.ds_nome))) LIKE TRIM(UPPER(func_translate('%" + query + "%')))");
                    break;
                case "codigo":
                    listWhere.add("P.id = " + query + "");
                    break;
                case "cpf":
                    listWhere.add("P.ds_documento LIKE '%" + query + "%'");
                    break;
                case "nascimento":
                    listWhere.add("F.dt_nascimento = '" + query + "'");
                    break;
                default:
                    break;
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + "\n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + "\n";
                }
            }
            // GROUP DA QUERY            
            queryString += " ORDER BY AIC.dt_emissao DESC, P.ds_nome ";
            Query qry = getEntityManager().createNativeQuery(queryString, AutorizaImpressaoCartao.class);

            return qry.getResultList();
        } catch (NumberFormatException e) {
            e.getMessage();
        }

        return new ArrayList();
    }

}
