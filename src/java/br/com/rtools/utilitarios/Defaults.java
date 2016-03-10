package br.com.rtools.utilitarios;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

@ManagedBean
@ViewScoped
public class Defaults {

    private String host;
    private Integer port;
    private String url_sistem_master;
    private String email_suport;

    public Defaults() {
        this.host = "";
        this.port = 0;
        this.url_sistem_master = "";
        this.email_suport = "";
    }

    public Defaults(String host, Integer port, String url_sistem_master, String email_suport) {
        this.host = host;
        this.port = port;
        this.url_sistem_master = url_sistem_master;
        this.email_suport = email_suport;
    }

    public void loadJson() {
        FacesContext faces = FacesContext.getCurrentInstance();
        try {
            File file = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/resources/conf/defaults.json"));
            if (!file.exists()) {
                return;
            }
            String json = null;
            try {
                json = FileUtils.readFileToString(file);
            } catch (IOException ex) {
                Logger.getLogger(Defaults.class.getName()).log(Level.SEVERE, null, ex);
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
                url_sistem_master = jSONObject.getString("url_sistem_master");
            } catch (Exception e) {

            }
            try {
                email_suport = jSONObject.getString("email_suport");
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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getURL() {
        loadJson();
        String url = "";
        if (!host.isEmpty()) {
            url += host;
        }
        if (port > 0 && port != 80) {
            url += ":" + port;
        }
        return url;
    }

    public String getUrl_sistem_master() {
        return url_sistem_master;
    }

    public void setUrl_sistem_master(String url_sistem_master) {
        this.url_sistem_master = url_sistem_master;
    }

    public String getEmail_suport() {
        return email_suport;
    }

    public void setEmail_suport(String email_suport) {
        this.email_suport = email_suport;
    }
}
