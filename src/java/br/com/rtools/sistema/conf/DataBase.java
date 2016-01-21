package br.com.rtools.sistema.conf;

import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class DataBase {

    private String host;
    private String database;
    private String password;

    public DataBase() {
        this.host = "";
        this.database = "";
        this.password = "";
    }

    public DataBase(String host, String database, String password) {
        this.host = host;
        this.database = database;
        this.password = password;
    }

    public void loadJson() {
        FacesContext faces = FacesContext.getCurrentInstance();
        try {
            File file = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/conf/database.json"));
            if (!file.exists()) {
                return;
            }
            String json = null;
            try {
                json = FileUtils.readFileToString(file);
            } catch (IOException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }
            JSONObject jSONObject = new JSONObject(json);
            try {
                host = jSONObject.getString("host");
            } catch (Exception e) {

            }
            try {
                database = jSONObject.getString("database");
            } catch (Exception e) {

            }
            try {
                password = jSONObject.getString("password");
            } catch (Exception e) {

            }
        } catch (JSONException ex) {

        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
