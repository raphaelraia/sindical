package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.QueryString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioProdutosDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioProdutosDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioProdutosDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String startFinish, String type, String description, String situacao_estoque, String in_filiais, String in_tipos, String in_grupos, String in_subgrupos, String in_cores, String in_unidades, String tipoDataCadastro, String dtCI, String dtCF, String estoqueSituacao) {
        if (relatorios == null) {
            return new ArrayList();
        }
        // CHAMADOS 1490
        try {
            String queryString = "";
            queryString += " -- RelatorioProdutosDao->find()                    \n"
                    + "      SELECT G.ds_descricao  AS grupo,                   \n"
                    + "             SB.ds_descricao AS subgrupo,                \n"
                    + "             rtrim(                                      \n"
                    + "                 ltrim(                                  \n"
                    + "                     ltrim(rtrim(P.ds_descricao))||' '||U.ds_descricao||' '||ltrim(rtrim(P.ds_marca))||ltrim(rtrim(P.ds_sabor))||' '||ltrim(rtrim(P.ds_medida))||' '||ltrim(rtrim(P.ds_fabricante))||' '||ltrim(rtrim(P.ds_modelo)) \n"
                    + "                 )                                       \n"
                    + "             ) AS produto,                               \n"
                    + "             P.nr_valor AS valor,                        \n"
                    + "             PF.ds_nome AS filial_nome,                  \n"
                    + "             E.nr_estoque AS estoque,                    \n"
                    + "             E.nr_estoque_minimo AS estoque_minimo,      \n"
                    + "             E.nr_estoque_maximo AS estoque_maximo,      \n"
                    + "             E.nr_custo_medio AS custo_medio             \n"
                    + "        FROM est_produto AS P                            \n"
                    + "  INNER JOIN est_grupo    AS G  ON G.id  = P.id_grupo    \n"
                    + "  INNER JOIN est_subgrupo AS SB ON SB.id = P.id_subgrupo \n"
                    + "  INNER JOIN est_unidade  AS U  ON U.id  = P.id_unidade  \n"
                    + "   LEFT JOIN est_estoque  AS E  ON E.id_produto = P.id   \n"
                    + "   LEFT JOIN pes_filial   AS F  ON F.id = E.id_filial    \n"
                    + "   LEFT JOIN pes_juridica AS J  ON J.id = F.id_filial    \n"
                    + "   LEFT JOIN pes_pessoa   AS PF ON PF.id = J.id_pessoa   \n";
            List listWhere = new ArrayList<>();
            // SITUAÇÃO
            switch (situacao_estoque) {
                // Contas a Pagar (filtro fixo) 
                case "A":
                    listWhere.add("E.ativo = true");
                    break;
                // Contas Pagas  (filtro fixo) 
                case "I":
                    listWhere.add("E.ativo = false");
                    break;
                default:
                    break;
            }
            // PESQUISA
            if (description != null && !description.isEmpty()) {
                switch (type) {
                    case "descricao":
                        listWhere.add("TRIM(UPPER(FUNC_TRANSLATE(P.ds_descricao))) " + QueryString.typeSearch(description, Integer.parseInt(startFinish)));
                        break;
                    case "modelo":
                        listWhere.add("TRIM(UPPER(FUNC_TRANSLATE(P.ds_modelo))) " + QueryString.typeSearch(description, Integer.parseInt(startFinish)));
                        break;
                    case "marca":
                        listWhere.add("TRIM(UPPER(FUNC_TRANSLATE(P.ds_marca))) " + QueryString.typeSearch(description, Integer.parseInt(startFinish)));
                        break;
                    case "fabricante":
                        listWhere.add("TRIM(UPPER(FUNC_TRANSLATE(P.ds_fabricante))) " + QueryString.typeSearch(description, Integer.parseInt(startFinish)));
                        break;

                }
            }
            // ESTOQUE
            if (estoqueSituacao != null && !estoqueSituacao.isEmpty()) {
                switch (estoqueSituacao) {
                    case "normal":
                        listWhere.add("E.nr_estoque BETWEEN E.nr_estoque_minimo AND E.nr_estoque_maximo");
                        break;
                    case "alto":
                        listWhere.add("E.nr_estoque > E.nr_estoque_maximo");
                        break;
                    case "baixo":
                        listWhere.add("E.nr_estoque < E.nr_estoque_minimo");
                        break;
                    case "falta":
                        listWhere.add("E.nr_estoque < 0");
                        break;
                }
            }
            // FILIAIS
            if (in_filiais != null) {
                listWhere.add("E.id_filial IN (" + in_filiais + ")");
            }
            // TIPO
            if (in_tipos != null) {
                listWhere.add("E.id_tipo IN (" + in_tipos + ")");
            }
            // GRUPO
            if (in_grupos != null) {
                listWhere.add("P.id_grupo IN (" + in_grupos + ")");
            }
            // SUBGRUPO
            if (in_subgrupos != null) {
                listWhere.add("P.id_subgrupo IN (" + in_subgrupos + ")");
            }
            // COR
            if (in_cores != null) {
                listWhere.add("P.id_cor IN (" + in_cores + ")");
            }
            // UNIDADE
            if (in_unidades != null) {
                listWhere.add("P.id_unidade IN (" + in_unidades + ")");
            }
            // DATA CADASTRO
            if (!dtCI.isEmpty() || !dtCF.isEmpty()) {
                switch (tipoDataCadastro) {
                    case "igual":
                        listWhere.add("P.dt_cadastro = '" + dtCI + "'");
                        break;
                    case "apartir":
                        listWhere.add("P.dt_cadastro >= '" + dtCI + "'");
                        break;
                    case "ate":
                        listWhere.add("P.dt_cadastro <= '" + dtCI + "'");
                        break;
                    case "faixa":
                        listWhere.add("P.dt_cadastro BETWEEN '" + dtCI + "' AND '" + dtCF + "'");
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
