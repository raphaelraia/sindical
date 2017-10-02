package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioSociosAcademiaDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;
    private String order;

    /**
     * <ul>
     * <li>RESULTADOS</li>
     * <li>[0] - titular_nome;</li>
     * <li>[1] - aluno_nome;</li>
     * <li>[2] - parentesco_descricao;</li>
     * <li>[3] - qtde_dependentes;</li>
     * <li>[4] - valor;</li>
     * <li>[5] - desconto;</li>
     * <li>[6] - valor_cheio;</li>
     * <li>[7] - categoria_descricao;</li>
     * <li>[8] - modalidade;</li>
     * </ul>
     *
     * @param inModalidade
     * @param in_grupo_categoria
     * @param in_categoria
     * @return
     */
    public List find(String inModalidade, String in_grupo_categoria, String in_categoria) {
        List listWhere = new ArrayList();
        String queryString = " -- RelatorioSociosAcademiaDao->find() \n\n   "
                + "    SELECT T.ds_nome AS titular_nome,                        \n" /* 0 - Titular -> Nome */
                + "           P.ds_nome AS aluno_nome,                          \n" /* 1 - Aluno -> Nome */
                + "           SW.parentesco AS parentesco_descricao,            \n" /* 2 - Parentesco -> Descrição */
                + "	      SO.qt as qtde_dependentes,                        \n" /* 3 - Quantidade de Dependentes  */
                + "           CASE WHEN SP.nr_desconto = 0                      \n"
                + "             THEN                                            \n"
                + "                 round(CAST((func_valor_servico(SP.id_pessoa, SP.id_servico, current_date, 0, SW.id_categoria)) AS numeric), 2)                   \n"
                + "             ELSE                                                                                                                                 \n"
                + "                 round(CAST (func_valor_servico_cheio(SP.id_pessoa,SP.id_servico, current_date) * (1 - ( SP.nr_desconto / 100 ) ) AS numeric), 2) \n"
                + "             END AS valor,                                   \n" /* 4 - Movimento -> Valor */
                + "           SP.nr_desconto AS desconto,                       \n" /* 5 - Serviço Pessoa - Desconto */
                + "           func_valor_servico_cheio(SP.id_pessoa, SP.id_servico, current_date) AS valor_cheio, \n" /* 6 - Valor Cheio */
                + "           categoria AS categoria_descricao,                             \n" /* 7 - Categoria - Descrição */
                + "           -- Modalidade                                                 \n"
                + "           SE.ds_descricao AS modalidade                                 \n" /* 8 - Serviço (Modalidade) - Descrição */
                + "      FROM matr_academia AS M                                            \n"
                + "INNER JOIN fin_servico_pessoa    AS SP   ON SP.id = M.id_servico_pessoa  \n"
                + "INNER JOIN fin_servicos          AS se   ON SE.id = SP.id_servico        \n"
                + "INNER JOIN pes_pessoa            AS p    ON P.id  = SP.id_pessoa         \n"
                + "INNER JOIN soc_socios_vw         AS SW   ON SW.codsocio = P.id           \n"
                + "INNER JOIN pes_pessoa            AS T    ON T.id  = SW.titular           \n"
                + " LEFT JOIN (                                                             \n"
                + "		SELECT titular AS id_titular,                               \n"
                + "                    count(*) AS qt                                       \n"
                + "               FROM soc_socios_vw                                        \n"
                + "		 WHERE titular <> codsocio                                  \n"
                + "           GROUP BY titular                                              \n"
                + ") AS SO ON SO.id_titular = T.id  \n";
        listWhere.add("M.dt_inativo IS NULL");
        if (inModalidade != null) {
            listWhere.add("SP.id_servico IN(" + inModalidade + ")");
        }
        if ((in_grupo_categoria != null && !in_grupo_categoria.isEmpty()) || (in_categoria != null && !in_categoria.isEmpty())) {
            if (in_categoria != null && !in_categoria.isEmpty()) {
                listWhere.add("SW.id_categoria IN (" + in_categoria + ")");
            } else if (in_grupo_categoria != null && !in_grupo_categoria.isEmpty()) {
                listWhere.add("SW.id_grupo_categoria IN (" + in_grupo_categoria + ")");
            }
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
        if (relatorioOrdem != null) {
            if (!relatorioOrdem.getQuery().isEmpty()) {
                queryString += " ORDER BY " + relatorioOrdem.getQuery();
            }
        }

        try {
            Debugs.put("habilitaDebugQuery", queryString);
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }
}
