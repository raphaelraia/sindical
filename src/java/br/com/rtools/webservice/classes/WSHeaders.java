package br.com.rtools.webservice.classes;

import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class WSHeaders {

    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

    private String client;
    private String user;
    private String password;
    private String app;
    private String key;
    private String mac;
    private String deviceName;
    private Boolean ssl;

    public WSHeaders() {
//        String client = GenericaRequisicao.getParametro("client");
//        String user = GenericaRequisicao.getParametro("user");
//        String password = GenericaRequisicao.getParametro("password");
//        String app = GenericaRequisicao.getParametro("app");
//        String key = GenericaRequisicao.getParametro("key");
//        String mac = GenericaRequisicao.getParametro("mac");
        this.client = request.getHeader("Device-Client");
        this.user = request.getHeader("Device-User");
        this.password = request.getHeader("Device-Password");
        this.app = request.getHeader("Device-App");
        this.key = request.getHeader("Device-Key");
        this.mac = request.getHeader("Device-Mac");
        this.deviceName = request.getHeader("Device-Name");
        try {
            this.ssl = Boolean.parseBoolean(request.getHeader("Device-Ssl"));
        } catch (Exception e) {

        }
        if (client != null && !client.isEmpty()) {
            GenericaSessao.put("sessaoCliente", client);
        }
    }

    public WSHeaders(String client, String user, String password, String app, String key, String mac, String deviceName, Boolean ssl) {
        this.client = client;
        this.user = user;
        this.password = password;
        this.app = app;
        this.key = key;
        this.mac = mac;
        this.deviceName = deviceName;
        this.ssl = ssl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

}
