package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioMovimentosNaoGeradosDao extends DB {

    private Integer idRelatorio;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioMovimentosNaoGeradosDao() {
        this.idRelatorio = null;
        this.relatorioOrdem = null;
    }

    public RelatorioMovimentosNaoGeradosDao(Integer idRelatorio, RelatorioOrdem relatorioOrdem) {
        this.idRelatorio = idRelatorio;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String referencia) {
        // CHAMADOS 1490
        try {
            String queryString = " -- RelatorioMovimentosNaoGeradosDao->find() \n\n"
                    + "      SELECT R.id      AS responsavel_id,                \n"
                    + "             R.ds_nome AS responsavel_nome,              \n"
                    + "             P.id      AS titular_id,                    \n"
                    + "             P.ds_nome AS titular_nome,                  \n"
                    + "             S.matricula,                                \n"
                    + "             S.categoria                                 \n"
                    + "        FROM fin_movimento   AS M                                \n"
                    + "  INNER JOIN pes_pessoa      AS P ON P.id = M.id_titular         \n"
                    + "  INNER JOIN pes_pessoa      AS R ON R.id = M.id_pessoa          \n"
                    + "   LEFT JOIN soc_socios_vw   AS S ON S.codsocio = M.id_titular   \n"
                    + "   LEFT JOIN (                                                   \n"
                    + "      SELECT id_titular                                  \n"
                    + "        FROM fin_movimento AS M                          \n"
                    + "       WHERE is_ativo = true                             \n"
                    + "         AND M.id_servicos IN (SELECT id_servico FROM fin_servico_pessoa GROUP BY id_servico)                \n"
                    + "         AND extract(MONTH FROM M.dt_vencimento) = extract(MONTH FROM CAST('01/" + referencia + "' AS date)) \n"
                    + "         AND extract(YEAR  FROM M.dt_vencimento) = extract(YEAR  FROM CAST('01/" + referencia + "' AS date)) \n"
                    + "    GROUP BY id_titular                                                                                      \n"
                    + "            ) AS X ON X.id_titular = p.id                                                                    \n"
                    + "       WHERE x.id_titular IS NULL                                                                            \n"
                    + "         AND is_ativo = true                                                                                 \n"
                    + "         AND M.id_servicos IN(SELECT id_servico FROM fin_servico_pessoa GROUP BY id_servico)                 \n"
                    + "         AND extract(MONTH FROM M.dt_vencimento) = extract(month FROM date_trunc('MONTH', CAST('01/" + referencia + "' AS date)) - INTERVAL '1 month' ) \n"
                    + "         AND extract(YEAR  FROM M.dt_vencimento) = extract(YEAR  FROM date_trunc('MONTH', CAST('01/" + referencia + "' AS date)) - INTERVAL '1 month' ) \n"
                    + "    GROUP BY R.id,        \n"
                    + "             R.ds_nome,   \n"
                    + "             P.id,        \n"
                    + "             P.ds_nome,   \n"
                    + "             S.matricula, \n"
                    + "             S.categoria  \n"
                    + "    ORDER BY 2,1          \n";

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

}
