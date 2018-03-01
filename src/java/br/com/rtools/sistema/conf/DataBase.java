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
    private Integer port;
    private String user;

    public DataBase() {
        this.host = "";
        this.port = 5432;
        this.database = "";
        this.password = "";
        this.user = "postgres";
    }

    public DataBase(String host, Integer port, String database, String password, String user) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.password = password;
        this.user = user;
    }

    public void loadJson() {
        loadJson(null);
    }

    public void loadJson(String cliente) {
        FacesContext faces = FacesContext.getCurrentInstance();
        try {
            File file;
            if (cliente == null) {
                file = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/conf/database.json"));
            } else {
                file = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/resources/cliente/" + cliente.toLowerCase() + "/conf/database.json"));
            }
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
                port = jSONObject.getInt("port");
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
            try {
                user = jSONObject.getString("user");
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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
