package br.com.rtools.relatorios.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioCobrancaDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioCobrancaDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioCobrancaDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(String inGrupoFinanceiro, String inSubGrupoFinanceiro, String inServicos, String tipoSocio, String tipoPessoa, String tipoMesesDebito, String monthS, String monthF) {
        // CHAMADOS 1192
        try {
            String queryString = "";
            queryString += " -- RelatorioCobrancaDao->find()                \n"
                    + "      SELECT P.codigo      AS pessoa_id,               \n" // 0
                    + "             P.nome        AS pessoa_nome,             \n" // 1
                    + "             P.cpf         AS pessoa_documento,        \n" // 2
                    + "             P.cidade      AS pessoa_cidade,           \n" // 3
                    + "             P.uf          AS pessoa_uf,               \n" // 4
                    + "             P.telefone    AS pessoa_telefone,         \n" // 5
                    + "             P.telefone2   AS pessoa_telefone2,        \n" // 6
                    + "             P.telefone3   AS pessoa_telefone3,        \n" // 7
                    + "             sum(valor)    AS valor,                   \n" // 8
                    + "             count(*)      AS meses,                   \n" // 9
                    + "             P.logradouro  AS pessoa_logradouro,       \n" // 10
                    + "             P.endereco  AS pessoa_descricao_endereco, \n" // 11
                    + "             P.bairro      AS pessoa_bairro,           \n" // 12
                    + "             P.numero      AS pessoa_numero,           \n" // 13
                    + "             P.complemento AS pessoa_complemento,      \n" // 14
                    + "             P.cep         AS pessoa_cep               \n" // 15
                    + "        FROM (                                       \n"
                    + "               SELECT id_pessoa,                     \n"
                    + "                      extract(MONTH FROM dt_vencimento) AS mes,      \n"
                    + "                      extract(YEAR  FROM dt_vencimento) AS ano,      \n"
                    + "                      SUM(nr_valor) AS valor                         \n"
                    + "                 FROM fin_movimento AS M                             \n"
                    + "           INNER JOIN fin_servicos AS SE ON SE.id = M.id_servicos    \n"
                    + "            LEFT JOIN fin_subgrupo AS SB ON SB.id = SE.id_subgrupo   \n"
                    + "                WHERE M.is_ativo = true                              \n"
                    + "                  AND M.id_baixa IS NULL                             \n"
                    + "                  AND M.dt_vencimento < current_date                 \n"
                    + "                  AND M.id_servicos NOT IN (                         \n"
                    + "                      SELECT id_servicos                             \n"
                    + "                        FROM fin_servico_rotina                      \n"
                    + "                       WHERE id_rotina = 4 )                         \n"
                    + ""
                    + "";
            // GRUPO FINANÇEIRO
            if (inGrupoFinanceiro != null && !inGrupoFinanceiro.isEmpty() && inSubGrupoFinanceiro == null && inSubGrupoFinanceiro.isEmpty()) {
                queryString += " AND SB.id_grupo IN (" + inGrupoFinanceiro + ") \n";
            }
            // SUBGRUPO FINANÇEIRO
            if (inGrupoFinanceiro != null && !inGrupoFinanceiro.isEmpty() && !inSubGrupoFinanceiro.isEmpty()) {
                queryString += " AND SB.id IN (" + inSubGrupoFinanceiro + ") \n";
            }
            // SERVIÇOS
            if (inServicos != null && !inServicos.isEmpty()) {
                queryString += " AND SE.id IN (" + inServicos + ") \n";
            }
            queryString += " GROUP BY M.id_pessoa,                              \n"
                    + "            extract(MONTH FROM M.dt_vencimento),         \n"
                    + "            extract(YEAR FROM M.dt_vencimento)           \n"
                    + ") AS X                                                   \n"
                    + " INNER JOIN pes_pessoa_vw AS P ON P.codigo = X.id_pessoa \n";
            List listWhere = new ArrayList<>();
            // SÓCIOS / NÃO SÓCIOS / TODOS
            if (tipoSocio.equals("socios")) {
                listWhere.add("X.id_pessoa IN (SELECT titular FROM soc_socios_vw) ");
            } else if (tipoSocio.equals("nao_socios")) {
                listWhere.add("X.id_pessoa NOT IN (SELECT titular FROM soc_socios_vw) ");
            }
            // FÍSICA / JURÍDICA / TODAS
            if (tipoPessoa.equals("fisica")) {
                listWhere.add("X.id_pessoa IN (SELECT id_pessoa FROM pes_fisica)");
            } else if (tipoPessoa.equals("juridica")) {
                listWhere.add("X.id_pessoa IN (SELECT id_pessoa FROM pes_juridica)");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            queryString += ""
                    + " GROUP BY P.codigo,      \n"
                    + "          P.nome,        \n"
                    + "          P.cpf,         \n"
                    + "          P.cidade,      \n"
                    + "          P.uf,          \n"
                    + "          P.telefone,    \n"
                    + "          P.telefone2,   \n"
                    + "          P.telefone3,   \n"
                    + "          P.logradouro,  \n"
                    + "          P.endereco,    \n"
                    + "          P.bairro,      \n"
                    + "          P.numero,      \n"
                    + "          P.complemento, \n"
                    + "          P.cep          \n";
            switch (tipoMesesDebito) {
                case "apartir":
                    queryString += " HAVING COUNT(*) >= " + monthS;
                    break;
                case "ate":
                    queryString += " HAVING COUNT(*) <= " + monthS;
                    break;
                case "faixa":
                    queryString += " HAVING COUNT(*) >= " + monthS + " AND COUNT(*) <= " + monthF + " ";
                    break;
                default:
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
