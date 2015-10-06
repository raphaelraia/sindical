package br.com.rtools.escola.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class CartaoEscolaDao extends DB {

    /**
     *
     * @param aluno_id
     * @param cursos_id
     * @param period
     * @return (0 - MATRÍCULA ESCOLA; 1 - MATRÍCULA ESCOLA - DATA CARTÃO; 2 -
     * ALUNO - FOTO; 3 - PESSOA - NOME; 4 - SERVIÇOS - DESCRIÇÃO (CURSO); 5 -
     * ALUNO - NASCIMENTO; 6 - ALUNO - RG; 7 - VÁLIDADE )
     */
    public List find(Integer aluno_id, Integer cursos_id, String period) {
        try {
            String queryString = ""
                    + "    SELECT M.id               AS matricula_escola_id,                \n" // 0 - MATRÍCULA ESCOLA - ID
                    + "           M.dt_cartao        AS data_impressao,                     \n" // 1 - MATRÍCULA ESCOLA - DATA CARTÃO
                    + "           P.id               AS pessoa_id,                          \n" // 2 - ALUNO - FOTO
                    + "           P.ds_nome          AS pessoa_nome,                        \n" // 3 - PESSOA - NOME
                    + "           SE.ds_descricao    AS servico_descricao,                  \n" // 4 - SERVIÇOS - DESCRIÇÃO (CURSO)
                    + "           F.dt_nascimento    AS pessoa_nascimento,                  \n" // 5 - ALUNO - NASCIMENTO
                    + "           F.ds_rg            AS pessoa_rg,                          \n" // 6 - ALUNO - RG
                    + "           current_date       AS validade,                           \n" // 7 - VÁLIDADE
                    + "           SE.id              AS servico_id                          \n" // 8 - SERVIÇOS - ID
                    + "      FROM matr_escola        AS M                                   \n"
                    + "INNER JOIN fin_servico_pessoa AS SP ON SP.id = M.id_servico_pessoa   \n"
                    + "INNER JOIN pes_pessoa         AS P  ON P.id = SP.id_pessoa           \n"
                    + "INNER JOIN pes_fisica         AS F  ON F.id_pessoa = P.id            \n"
                    + "INNER JOIN fin_servicos       AS SE ON SE.id = SP.id_servico         \n"
                    + "     WHERE SE.id_modelo_cartao IS NOT NULL                           \n";

            List listQuery = new ArrayList();
            listQuery.add(" ((cast('01/'||ds_ref_validade AS date) > current_date and ds_ref_validade<>'') or ds_ref_validade='') ");
            listQuery.add(" SP.is_ativo = true ");
            switch (period) {
                case "nao_impressos":
                    listQuery.add(" M.dt_cartao IS NULL ");
                    break;
                case "hoje":
                    listQuery.add(" M.dt_cartao = current_date ");
                    break;
                case "ultimos_30_dias":
                    listQuery.add(" M.dt_cartao > (current_date - 30)");
                    break;
                case "todos":
                    listQuery.add(" M.dt_cartao IS NOT NULL ");
                    break;
            }
            if (aluno_id != null) {
                listQuery.add(" P.id = " + aluno_id);
            }
            if (cursos_id != null) {
                listQuery.add(" SE.id = " + cursos_id);
            }
            for (int i = 0; i < listQuery.size(); i++) {
                queryString += " AND " + listQuery.get(i).toString() + "\n";
            }
            queryString += " ORDER BY P.ds_nome ";
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
