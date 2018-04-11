package br.com.rtools.webservice;

import br.com.rtools.sistema.BackupPostgres;
import br.com.rtools.sistema.dao.BackupPostgresDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

@ManagedBean
@RequestScoped
@ViewScoped
public class WSBackupPostgres implements Serializable {

    public WSBackupPostgres() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        GenericaSessao.put("sessaoCliente", "Rtools");
    }

    public void exists() {
        try {
            try {
                Integer backup_postgres_id = Integer.parseInt(GenericaRequisicao.getParametro("backup_postgres_id"));
                if (backup_postgres_id != null && backup_postgres_id != 0) {
                    Dao dao = new Dao();
                    BackupPostgres bp = (BackupPostgres) dao.find(new BackupPostgres(), backup_postgres_id);
                    if (bp != null) {
                        bp = (BackupPostgres) dao.rebind(bp);
                        bp.setDtProcessado(new Date());
                        dao.update(bp, true);
                        return;
                    }
                }
            } catch (Exception e) {

            }

            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

            try {
                BackupPostgres bp = new BackupPostgresDao().exist();
                if (bp != null) {
                    bp = (BackupPostgres) new Dao().rebind(bp);
                    bp.setDtEnviado(new Date());
                    new Dao().update(bp, true);
                }
                JSONObject obj = new JSONObject();

                JSONArray list = new JSONArray();
                if (bp != null) {
                    obj.put("server", bp.getConfiguracao().getDatabaseServerAlias());
                    obj.put("database", bp.getConfiguracao().getIdentifica());
                    obj.put("backup_postgres_id", bp.getId());
                    externalContext.getResponseOutputWriter().write(obj.toString());
                    facesContext.responseComplete();
                    return;
                }

                externalContext.getResponseOutputWriter().write(new JSONObject("{\"identificador\":null}").toString());
                facesContext.responseComplete();
            } catch (NumberFormatException | IOException e) {
                e.getMessage();
            }
        } catch (Exception e) {

        }
    }

}
