/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.DeclaracaoGrupo;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class DeclaracaoGrupoDao extends DB {

    public List<SubGrupoConvenio> listaSubGrupoConvenio() {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT sg.* FROM soc_convenio_sub_grupo sg ORDER BY sg.ds_descricao", SubGrupoConvenio.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Boolean existeDeclaracaoGrupo(Integer id_tipo, Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT dg.* \n"
                    + "  FROM soc_declaracao_grupo dg \n"
                    + " WHERE dg.id_declaracao_tipo = " + id_tipo + "\n"
                    + "   AND dg.id_subgrupo = " + id_grupo, DeclaracaoGrupo.class
            );
            return ((DeclaracaoGrupo) qry.getSingleResult()) != null;
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

    public List<DeclaracaoGrupo> listaDeclaracaoGrupo() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT dg.* \n"
                    + "  FROM soc_declaracao_grupo dg \n"
                    + " INNER JOIN soc_declaracao_tipo dt ON dt.id = dg.id_declaracao_tipo \n"
                    + " INNER JOIN soc_convenio_sub_grupo sg ON sg.id = dg.id_subgrupo \n"
                    + " ORDER BY dt.ds_descricao, sg.ds_descricao", DeclaracaoGrupo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
