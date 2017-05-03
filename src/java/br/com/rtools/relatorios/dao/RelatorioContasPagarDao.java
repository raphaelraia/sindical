package br.com.rtools.relatorios.dao;

import br.com.rtools.financeiro.ContaOperacao;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioContasPagarDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioContasPagarDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioContasPagarDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String in_filiais, String in_contas, String in_credores, String tipoDataPagamento, String dtPI, String dtPF, String tipoDataVencimento, String dtVI, String dtVF, String tipoDataEmissao, String dtEI, String dtEF) {
        if (relatorios == null) {
            return new ArrayList();
        }
        // CHAMADOS 1400
        try {
            String queryString = "";
            queryString += " -- RelatorioContasPagarDao->find()     \n"
                    + "     SELECT FI.ds_nome       AS filial,      \n"
                    + "            M.emissao        AS emissao,     \n"
                    + "            M.lote_documento AS documento,   \n"
                    + "            M.lote_valor*-1  AS total,       \n"
                    + "            FO.ds_nome       AS credor,      \n"
                    + "            M.conta          AS conta,       \n"
                    + "            M.valor*-1       AS valor,       \n"
                    + "            M.vencimento     AS vencimento,  \n"
                    + "            M.valor_baixa*-1 AS valor_pago,  \n"
                    + "            M.baixa          AS baixa        \n"
                    + "       FROM movimentos_vw    AS M            \n"
                    + " INNER JOIN pes_pessoa       AS FO ON FO.id = M.id_pessoa        \n"
                    + " INNER JOIN pes_filial       AS F  ON F.id  = M.filial_criacao   \n"
                    + " INNER JOIN pes_juridica     AS J  ON J.id  = F.id_filial        \n"
                    + " INNER JOIN pes_pessoa       AS FI ON FI.id = J.id_pessoa        \n";
            // DATA BAIXA
            List listWhere = new ArrayList<>();

            listWhere.add(" M.es = 'S'");
            listWhere.add(" M.id_plano5 NOT IN (SELECT id_plano5 FROM caixa_banco_vw)");
            
            switch (relatorios.getId()) {
                // Contas a Pagar (filtro fixo) 
                case 94:
                    listWhere.add("M.id_baixa IS NULL");
                    break;
                // Contas Pagas  (filtro fixo) 
                case 95:
                    listWhere.add("M.id_baixa IS NOT NULL");
                    break;
                case 3:
                    break;
                default:
                    break;
            }
            // FILIAIS
            if (in_filiais != null) {
                listWhere.add("F.id IN (" + in_filiais + ")");
            }
            // CONTAS
            if (in_contas != null) {
                listWhere.add("M.id_conta IN (" + in_contas + ")");
            }
            // CREDORES
            if (in_credores != null) {
                listWhere.add("FO.id IN (" + in_credores + ")");
            }
            // DATA BAIXA
            if (!dtPI.isEmpty() || !dtPF.isEmpty()) {
                switch (tipoDataPagamento) {
                    case "igual":
                        listWhere.add("M.baixa = '" + dtPI + "'");
                        break;
                    case "apartir":
                        listWhere.add("M.baixa >= '" + dtPI + "'");
                        break;
                    case "ate":
                        listWhere.add("M.baixa <= '" + dtPI + "'");
                        break;
                    case "faixa":
                        listWhere.add("M.baixa BETWEEN '" + dtPI + "' AND '" + dtPF + "'");
                        break;
                    default:
                        break;
                }
            }
            // DATA VENCIMENTO
            if (!dtVI.isEmpty() || !dtVF.isEmpty()) {
                switch (tipoDataVencimento) {
                    case "igual":
                        listWhere.add("M.vencimento = '" + dtVI + "'");
                        break;
                    case "apartir":
                        listWhere.add("M.vencimento >= '" + dtVI + "'");
                        break;
                    case "ate":
                        listWhere.add("M.vencimento <= '" + dtVI + "'");
                        break;
                    case "faixa":
                        listWhere.add("M.vencimento BETWEEN '" + dtVI + "' AND '" + dtVF + "'");
                        break;
                    default:
                        break;
                }
            }
            // DATA EMISSÃO
            if (!dtEI.isEmpty() || !dtEF.isEmpty()) {
                switch (tipoDataEmissao) {
                    case "igual":
                        listWhere.add("M.emissao = '" + dtEI + "'");
                        break;
                    case "apartir":
                        listWhere.add("M.emissao >= '" + dtEI + "'");
                        break;
                    case "ate":
                        listWhere.add("M.emissao <= '" + dtEI + "'");
                        break;
                    case "faixa":
                        listWhere.add("M.emissao BETWEEN '" + dtEI + "' AND '" + dtEF + "'");
                        break;
                    default:
                        break;
                }
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            if (relatorioOrdem != null) {
                queryString += " ORDER BY  " + relatorioOrdem.getQuery() + " \n";
            }

            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
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

    public List findAllContaOperacaoGroup() {
        try {
            String queryString = ""
                    + "     SELECT O.id,                                        \n"
                    + "            O.ds_descricao as operacao                   \n"
                    + "       FROM fin_conta_operacao AS CO                     \n"
                    + " INNER JOIN fin_operacao AS O ON O.id = CO.id_operacao   \n"
                    + " INNER JOIN plano_vw     AS P ON P.id_p5 = CO.id_plano5  \n"
                    + "      WHERE CO.ds_es =   'S'                             \n"
                    + "   GROUP BY O.id, O.ds_descricao                         \n"
                    + "   ORDER BY O.ds_descricao ";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public List findByContaOperacao(String in_conta_operacao) {
        try {
            String queryString = ""
                    + "     SELECT co.id_plano5,                                \n"
                    + "            conta5,                                      \n"
                    + "            O.ds_descricao as operacao                   \n"
                    + "       FROM fin_conta_operacao AS CO                     \n"
                    + " INNER JOIN fin_operacao AS O ON O.id = CO.id_operacao   \n"
                    + " INNER JOIN plano_vw     AS P ON P.id_p5 = CO.id_plano5  \n"
                    + "      WHERE CO.ds_es =   'S'                             \n"
                    + "        AND O.id IN (" + in_conta_operacao + ")          \n"
                    + "   ORDER BY O.ds_descricao, conta5 ";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
