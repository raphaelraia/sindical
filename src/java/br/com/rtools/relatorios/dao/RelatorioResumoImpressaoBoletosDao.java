package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioResumoImpressaoBoletosDao extends DB {

    private Integer idRelatorio;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioResumoImpressaoBoletosDao() {
        this.idRelatorio = null;
        this.relatorioOrdem = null;
    }

    public RelatorioResumoImpressaoBoletosDao(Integer idRelatorio, RelatorioOrdem relatorioOrdem) {
        this.idRelatorio = idRelatorio;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String data_impressao) {
        // CHAMADOS 1490
        try {
            String queryString = " -- RelatorioResumoImpressaoBoletosDao->find()                       \n\n"
                    + "(                                                                                \n"
                    + "     SELECT categoria,                                                           \n"
                    + "            COUNT(*) total                                                       \n"
                    + "       FROM (                                                                    \n"
                    + "                 SELECT id_titular,                                              \n"
                    + "              CASE WHEN categoria IS NULL THEN 'NÃO SÓCIO' ELSE categoria END    \n"
                    + "                   FROM fin_impressao AS I                                       \n"
                    + "             INNER JOIN fin_movimento AS M on M.id = I.id_movimento              \n"
                    + "             INNER JOIN pes_fisica AS F on F.id_pessoa = M.id_pessoa             \n"
                    + "              LEFT JOIN soc_socios_vw AS s on S.codsocio = M.id_titular          \n"
                    + "                  WHERE I.dt_impressao = '" + data_impressao + "'                \n"
                    + "               GROUP BY id_titular,                                              \n"
                    + "                        categoria                                                \n"
                    + "            ) AS II                                                              \n"
                    + "   GROUP BY categoria                                                            \n"
                    + ")                                                                                \n"
                    + "                                                                                 \n"
                    + "UNION                                                                            \n"
                    + "                                                                                 \n"
                    + "(                                                                                \n"
                    + "     SELECT 'TOTAL DE EXTRATOS',                                                 \n"
                    + "            count(*) total                                                       \n"
                    + "       FROM  (                                                                   \n"
                    + "                 SELECT id_titular,                                              \n"
                    + "              CASE WHEN categoria IS NULL THEN 'NÃO SÓCIO' ELSE categoria END    \n"
                    + "                   FROM fin_impressao AS I                                       \n"
                    + "             INNER JOIN fin_movimento AS M ON M.id = I.id_movimento              \n"
                    + "             INNER JOIN pes_fisica AS F ON F.id_pessoa = M.id_pessoa             \n"
                    + "              LEFT JOIN soc_socios_vw AS S ON S.codsocio = M.id_titular          \n"
                    + "                  WHERE I.dt_impressao = '" + data_impressao + "'                \n"
                    + "                GROUP BY id_titular,                                             \n"
                    + "                         categoria                                               \n"
                    + "            ) AS II                                                              \n"
                    + ")                                                                                \n"
                    + "                                                                                 \n"
                    + "UNION                                                                            \n"
                    + "                                                                                 \n"
                    + "(                                                                                \n"
                    + "     SELECT 'TOTAL DE INADIMPLENTES',                                            \n"
                    + "            COUNT(*)                                                             \n"
                    + "       FROM sis_carta_impressao                                                  \n"
                    + "      WHERE dt_impressao = '" + data_impressao + "'                              \n"
                    + ")                                                                                \n"
                    + "                                                                                 \n"
                    + "   ORDER BY 1                                                                    \n";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

    public List findDates() {
        try {
            String queryString = " -- RelatorioResumoImpressaoBoletosDao()->findDates() \n\n"
                    + "         SELECT dt_processamento,                        \n"
                    + "                count(*) AS qtde                         \n"
                    + "           FROM                                          \n"
                    + "          (                                              \n"
                    + "                 SELECT dt_processamento,                \n"
                    + "                        nr_ctr_boleto                    \n"
                    + "                   FROM fin_impressao AS FI              \n"
                    + "             INNER JOIN fin_movimento    AS M ON M.id = FI.id_movimento                                          \n"
                    + "             INNER JOIN soc_lote_boleto  AS B ON RIGHT('0000000'||text(B.id), 5)||'01' = right(nr_ctr_boleto,7)  \n"
                    + "                  WHERE M.is_ativo = true                \n"
                    + "                    AND M.id_servicos NOT IN (           \n"
                    + "                         SELECT id_servicos              \n"
                    + "                           FROM fin_servico_rotina       \n"
                    + "                          WHERE id_rotina = 4            \n"
                    + "                  )                                      \n"
                    + "                  AND dt_processamento > '01/09/2016'    \n"
                    + "               GROUP BY dt_processamento,                \n"
                    + "                     nr_ctr_boleto                       \n"
                    + "          ) AS I                                         \n"
                    + "       GROUP BY dt_processamento                         \n"
                    + "         HAVING count(*) > 100                           \n"
                    + "       ORDER BY dt_processamento DESC                    ";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

}
