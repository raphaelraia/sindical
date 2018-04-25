package br.com.rtools.sistema.dao;

import br.com.rtools.principal.DB;
import br.com.rtools.principal.DBExternal;
import br.com.rtools.sistema.ControleScripts;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public void store(String mac, Integer tamanho, Boolean erro, String database, String server, Integer tipo_controle_scripts_id, Integer backup_postgres_id, Integer configuracao_id) {
        String queryString = ""
                + "INSERT INTO sis_controle_scripts (                       \n"
                + "         ds_mac,                                         \n"
                + "         nr_tamanho,                                     \n"
                + "         dt_data,                                        \n"
                + "         is_erro,                                        \n"
                + "         ds_descricao,                                   \n"
                + "         ds_servidor,                                    \n"
                + "         id_tipo_controle_scripts,                       \n"
                + "         id_backup_postgres,                             \n"
                + "         id_configuracao)                                \n"
                + "  VALUES ('" + mac + "',                                 \n"
                + "         " + tamanho + ",                                \n"
                + "         current_date,                                   \n"
                + "         " + erro + ",                                   \n"
                + "         '" + database + "',                             \n"
                + "         '" + server + "',                               \n"
                + "         " + tipo_controle_scripts_id + ",               \n"
                + "         " + backup_postgres_id + ",                     \n"
                + "         " + configuracao_id + ")";
        DBExternal dbe = new DBExternal();
        dbe.configure("Rtools");
        dbe.setApplicationName("controle scripts - backups");
        Connection conn = null;
        PreparedStatement ps = null;
        String history = "";
        try {
            conn = dbe.getConnection(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(queryString);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    /* ignored */
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */
                }
            }
        }
    }

}
