/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class AtualizaSocioDemissionadoDao extends DB {

    public List<Object> listaSocioDemissionado(String tipo, String aposentadoria) {
        try {
            String text = "SELECT pe.id AS pessoa_empresa_id,\n"
                    + "       p.id AS pessoa_id, \n"
                    + "       p.ds_nome AS pessoa_nome, \n"
                    + "       e.ds_documento AS empresa_documento, \n"
                    + "       e.ds_nome AS empresa_nome, \n"
                    + "       pe.dt_admissao AS data_admissao, \n"
                    + "       pe.dt_demissao AS data_demissao, \n"
                    + "       tl.lancamento AS contactado \n"
                    + "  FROM pes_pessoa_empresa AS pe \n"
                    + " INNER JOIN pes_fisica AS f ON f.id = pe.id_fisica \n"
                    + " INNER JOIN pes_pessoa AS p ON p.id = f.id_pessoa \n"
                    + " INNER JOIN soc_socios_vw AS so ON so.codsocio = p.id \n"
                    + " INNER JOIN conf_social AS cf ON cf.id = 1 \n"
                    + " INNER JOIN pes_juridica AS j ON j.id = pe.id_juridica \n"
                    + " INNER JOIN pes_pessoa AS e ON e.id = j.id_pessoa \n"
                    + " INNER JOIN (SELECT id_fisica, max(dt_demissao) AS demissao FROM pes_pessoa_empresa WHERE dt_demissao IS NOT NULL GROUP BY id_fisica) AS x ON x.id_fisica = pe.id_fisica \n"
                    + "  LEFT JOIN (SELECT id_pessoa_empresa, max(dt_lancamento) AS lancamento FROM tlm_historico WHERE id_pessoa_empresa > 0 GROUP BY id_pessoa_empresa) AS tl ON tl.id_pessoa_empresa = pe.id \n"
                    + " WHERE f.id NOT IN (SELECT id_fisica FROM pes_pessoa_empresa WHERE dt_demissao IS NULL) \n"
                    + "   AND pe.dt_demissao <= CURRENT_DATE - 90 \n"
                    + "   AND so.id_grupo_categoria = cf.id_grupo_categoria_inativa_demissionado \n";
            if (aposentadoria.equals("aposentado")) {
                text += "AND F.dt_aposentadoria IS NOT NULL ";
            } else if (aposentadoria.equals("nao_aposentado")) {
                text += "AND F.dt_aposentadoria IS NULL ";
            }

            String filtro = "";

            switch (tipo) {
                case "contactar":
                    filtro = "   AND tl.id_pessoa_empresa IS NULL \n";
                    break;
                case "contactados":
                    filtro = "   AND tl.id_pessoa_empresa IS NOT NULL \n";
                    break;
            }

            text += filtro;
            text += " ORDER BY pe.dt_demissao ";

            Query qry = getEntityManager().createNativeQuery(text);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
