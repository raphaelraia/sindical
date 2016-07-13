/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.DeclaracaoTipo;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class DeclaracaoTipoDao extends DB {

    public List<DeclaracaoTipo> listaDeclaracaoTipo() {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT dt.* FROM soc_declaracao_tipo dt ORDER BY dt.ds_descricao", DeclaracaoTipo.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
