package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.ControleScripts;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class ControleScriptsDao extends DB {

    public List<ControleScripts> find(String dataInicial, String dataFinal) {
        try {
            String queryString = ""
                    + "SELECT CS.* \n"
                    + "FROM sis_controle_scripts CS ";
            if (!dataInicial.isEmpty() && dataFinal.isEmpty()) {
                queryString += " WHERE CS.dt_data::date = '" + dataInicial + "'";
            } else if (!dataInicial.isEmpty() && !dataFinal.isEmpty()) {
                queryString += " WHERE CS.dt_data::date BETWEEN '" + dataInicial + "' AND '" + dataFinal + "'";
            }
            queryString += " ORDER BY dt_data::date DESC, CS.ds_servidor ASC, CS.ds_descricao ASC LIMIT 1000";
            Query query = getEntityManager().createNativeQuery(queryString, ControleScripts.class);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
