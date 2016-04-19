package br.com.rtools.relatorios.dao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioTelemarketingDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioTelemarketingDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioTelemarketingDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String tipoDataLancamento, String dataLancamentoInicial, String dataLancamentoFinal, Pessoa pessoa, String inOperador, String inNatureza, String inTipoContato, String inDepartamento) {
        // CHAMADOS 1192
        if (relatorios == null || relatorios.getId() == null) {
            return new ArrayList();
        }
        try {
            String queryString = "";
            queryString += " -- RelatorioTelemarketingDao->find()                \n";
            switch (relatorios.getId()) {
                case 71:
                    /**
                     * Analítico
                     */
                    queryString += ""
                            + "     SELECT H.dt_lancamento AS data_lancamento,  \n" // 0
                            + "            N.ds_descricao  AS natureza,         \n" // 1
                            + "            C.ds_descricao  AS tipo_contato,     \n" // 2
                            + "            H.ds_contato    AS contato,          \n" // 3
                            + "            O.ds_nome       AS operador_nome,    \n" // 4
                            + "            D.ds_descricao  AS departamento,     \n" // 5
                            + "            P.ds_nome       AS pessoa_nome,      \n" // 6
                            + "            H.ds_historico  AS historico         \n" // 7
                            + "       FROM tlm_historico   AS H                 \n" // 8
                            + " INNER JOIN pes_pessoa       AS P ON P.id=H.id_pessoa        \n"
                            + " INNER JOIN seg_usuario      AS U ON U.id=H.id_operador      \n"
                            + " INNER JOIN seg_departamento AS D ON D.id=H.id_departamento  \n"
                            + " INNER JOIN tlm_contato      AS C ON C.id=H.id_contato       \n"
                            + " INNER JOIN tlm_natureza     AS N ON N.id=H.id_natureza      \n"
                            + " INNER JOIN pes_pessoa       AS O ON O.id=U.id_pessoa  ";
                    break;
                case 72:
                    /**
                     * Resumo
                     */
                    queryString += ""
                            + "     SELECT to_date(to_char(H.dt_lancamento, 'YYYY/MM/DD'), 'YYYY/MM/DD') AS data_lancamento, \n" // 0
                            + "            count(*)         AS qtde                         \n" // 1
                            + "       FROM tlm_historico    AS H                            \n"
                            + " INNER JOIN pes_pessoa       AS P ON P.id = H.id_pessoa      \n"
                            + " INNER JOIN seg_usuario      AS U ON U.id = H.id_operador    \n"
                            + " INNER JOIN seg_departamento AS D ON D.id = H.id_departamento\n"
                            + " INNER JOIN tlm_contato      AS C ON C.id = H.id_contato     \n"
                            + " INNER JOIN tlm_natureza     AS N on N.id = H.id_natureza    \n"
                            + " INNER JOIN pes_pessoa       AS O on O.id = u.id_pessoa      \n";
                    break;
                case 73:
                    /**
                     * Mês a Mês
                     */
                    queryString += ""
                            + "    SELECT CAST(extract(YEAR FROM H.dt_lancamento) AS BIGINT)  AS ano,   \n" //0
                            + "           CAST(extract(MONTH FROM H.dt_lancamento) AS BIGINT) AS mes,   \n" //1
                            + "           count(*)          AS qtde                         \n" //2
                            + "       FROM tlm_historico    AS H                            \n"
                            + " INNER JOIN pes_pessoa       AS P ON P.id=H.id_pessoa        \n"
                            + " INNER JOIN seg_usuario      AS U ON U.id=H.id_operador      \n"
                            + " INNER JOIN seg_departamento AS D ON D.id=H.id_departamento  \n"
                            + " INNER JOIN tlm_contato      AS C ON C.id=H.id_contato       \n"
                            + " INNER JOIN tlm_natureza     AS N ON N.id=H.id_natureza      \n"
                            + " INNER JOIN pes_pessoa       AS O ON O.id=U.id_pessoa        \n";
                    break;
                case 74:
                    /**
                     * Operador
                     */
                    queryString += ""
                            + "     SELECT O.ds_nome        AS operador,                    \n" // 0
                            + "            count(*)         AS qtde                         \n" // 1
                            + "       FROM tlm_historico    AS H                            \n"
                            + " INNER JOIN pes_pessoa       AS P ON P.id = H.id_pessoa      \n"
                            + " INNER JOIN seg_usuario      AS U ON U.id = H.id_operador    \n"
                            + " INNER JOIN seg_departamento AS D ON D.id = H.id_departamento\n"
                            + " INNER JOIN tlm_contato      AS C ON C.id = H.id_contato     \n"
                            + " INNER JOIN tlm_natureza     AS N ON N.id = H.id_natureza    \n"
                            + " INNER JOIN pes_pessoa       AS O ON P.id = U.id_pessoa      \n";
                    break;
                default:
                    break;
            }

            List listWhere = new ArrayList();
            if (!tipoDataLancamento.equals("todos")) {
                if (dataLancamentoInicial != null && !dataLancamentoInicial.isEmpty()) {
                    switch (tipoDataLancamento) {
                        case "igual":
                            listWhere.add(" H.dt_lancamento = '" + dataLancamentoInicial + "'");
                            break;
                        case "apartir":
                            listWhere.add(" H.dt_lancamento >= '" + dataLancamentoInicial + "'");
                            break;
                        case "ate":
                            listWhere.add(" H.dt_lancamento <= '" + dataLancamentoInicial + "'");
                            break;
                        case "faixa":
                            if (dataLancamentoFinal != null && !dataLancamentoFinal.isEmpty()) {
                                listWhere.add(" H.dt_lancamento BETWEEN '" + dataLancamentoInicial + "' AND '" + dataLancamentoFinal + "'");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            if (pessoa != null && pessoa.getId() != -1) {
                listWhere.add(" P.id = " + pessoa.getId());
            }
            // OPERADOR
            if (inOperador != null && !inOperador.isEmpty()) {
                listWhere.add(" H.id_operador IN (" + inOperador + ")");
            }
            // NATUREZA
            if (inNatureza != null && !inNatureza.isEmpty()) {
                listWhere.add(" H.id_natureza IN (" + inNatureza + ")");
            }
            // TIPO CONTATO
            if (inTipoContato != null && !inTipoContato.isEmpty()) {
                listWhere.add(" H.id_contato IN (" + inTipoContato + ")");
            }
            // DEPARTAMENTO
            if (inDepartamento != null && !inDepartamento.isEmpty()) {
                listWhere.add(" H.id_departamento IN (" + inDepartamento + ")");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            switch (relatorios.getId()) {
                case 72:
                    queryString
                            += "  GROUP BY to_date(to_char(H.dt_lancamento, 'YYYY/MM/DD'), 'YYYY/MM/DD')  \n"
                            + "   ORDER BY to_date(to_char(H.dt_lancamento, 'YYYY/MM/DD'), 'YYYY/MM/DD')  ";
                    break;
                case 73:
                    queryString
                            += "  GROUP BY extract(MONTH FROM H.dt_lancamento),             \n"
                            + "            extract(YEAR FROM H.dt_lancamento)               \n"
                            + "   ORDER BY extract(YEAR FROM H.dt_lancamento),              \n"
                            + "            extract(MONTH FROM H.dt_lancamento)              \n";
                    break;
                case 74:
                    queryString += "   GROUP BY O.ds_nome  ";
                    break;
            }
            if (relatorioOrdem != null) {
                queryString += " ORDER BY " + relatorioOrdem.getQuery();

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

}
