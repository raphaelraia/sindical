/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.financeiro.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class GuiasDao extends DB {

    public List findByUsuarioSuggestions(Integer limit, Integer diff_days, Integer usuario_id) {
        try {

            String queryString = ""
                    + "     SELECT M.id_servicos,                               \n"
                    + "            S.ds_descricao,                              \n"
                    + "            CG.id,                                       \n"
                    + "            CG.ds_descricao,                             \n"
                    + "            CSG.id,                                      \n"
                    + "            CSG.ds_descricao,                            \n"
                    + "            P.id,                                        \n"
                    + "            P.ds_nome,                                   \n"
                    + "            MAX(L.dt_emissao)                            \n"
                    + "       FROM fin_guia G                                   \n"
                    + " INNER JOIN fin_lote L ON L.id = G.id_lote               \n"
                    + " INNER JOIN fin_movimento M ON M.id_lote = G.id_lote     \n"
                    + " INNER JOIN fin_servicos S ON S.id = M.id_servicos       \n"
                    + " INNER JOIN soc_convenio_sub_grupo CSG ON CSG.id = G.id_convenio_sub_grupo      \n"
                    + " INNER JOIN soc_convenio_grupo CG ON CG.id = CSG.id_grupo_convenio              \n"
                    + " INNER JOIN pes_pessoa P ON P.id = G.id_convenio                                \n"
                    + "      WHERE L.dt_emissao BETWEEN (current_date - " + diff_days + ") AND current_date    \n"
                    + "        AND L.id_usuario IS NOT NULL                     \n"
                    + "        AND L.id_usuario = " + usuario_id
                    + "        AND S.ds_situacao = 'A'                          \n"
                    + "   GROUP BY M.id_servicos,                               \n"
                    + "            S.ds_descricao,                              \n"
                    + "            CG.id,                                       \n"
                    + "            CG.ds_descricao,                             \n"
                    + "            CSG.id,                                      \n"
                    + "            CSG.ds_descricao,                            \n"
                    + "            P.id,                                        \n"
                    + "            P.ds_nome                                    \n"
                    + "     HAVING COUNT(*) > 2                                 \n"
                    + "   ORDER BY MAX(L.dt_emissao) DESC                       \n"
                    + "      LIMIT " + limit;
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
