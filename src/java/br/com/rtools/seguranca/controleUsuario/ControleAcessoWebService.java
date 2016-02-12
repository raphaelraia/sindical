package br.com.rtools.seguranca.controleUsuario;

import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.WebService;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.seguranca.dao.WebServiceDao;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.webservice.classes.WSHeaders;
import br.com.rtools.webservice.classes.WSStatus;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

public class ControleAcessoWebService {

    private String client;
    private String user;
    private String password;
    private String app;
    private String key;
    private String method;
    private String action;
    private String mac;
    private Boolean session;

    public ControleAcessoWebService() {
        this.session = false;
        this.client = GenericaRequisicao.getParametro("client");
        this.user = GenericaRequisicao.getParametro("user");
        this.password = GenericaRequisicao.getParametro("password");
        this.app = GenericaRequisicao.getParametro("app");
        this.key = GenericaRequisicao.getParametro("key");
        this.method = GenericaRequisicao.getParametro("method");
        this.action = GenericaRequisicao.getParametro("action");
        this.mac = GenericaRequisicao.getParametro("mac");
        try {
            this.session = Boolean.parseBoolean(GenericaRequisicao.getParametro("session"));
        } catch (Exception e) {
        }
        if (client != null && !client.isEmpty()) {
            GenericaSessao.put("sessaoCliente", client);
        }
    }

    /**
     * Para encontrar o nome do dispositivo é necessário verificar este conforme
     * estação (PC, tablet, smartphone...), não é o nome de usuário, esse deverá
     * esta de acordo com o MAC registrado.
     *
     * @return
     */
    public Boolean permiteWebService() {
        return permiteWebService(this.mac, false) != null;
    }

    /**
     * Para encontrar o nome do dispositivo é necessário verificar este conforme
     * estação (PC, tablet, smartphone...), não é o nome de usuário, esse deverá
     * esta de acordo com o MAC registrado.
     *
     * @param mac
     * @return
     */
    public Boolean permiteWebService(String mac) {
        return permiteWebService(mac, false) != null;
    }

    public WSStatus permiteWebService(String mac, Boolean message) {
        WSHeaders wSHeaders = new WSHeaders();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        WSStatus sStatus = new WSStatus();
        try {
            sStatus.setCodigo(1);
            String hostName = wSHeaders.getDeviceName();
            FacesContext req = FacesContext.getCurrentInstance();
            MacFilial macFilial = new MacFilialDao().pesquisaMac(mac);
            if (macFilial == null) {
                sStatus.setDescricao("Mac não existe!");
                return sStatus;
            }
            if (!hostName.equals(macFilial.getNomeDispositivo())) {
                sStatus.setDescricao("Computador não registrado! Solicite a liberação ao administrador do sistema.");
                return sStatus;
            }
            WebService webService = new WebServiceDao().find(user, password);
            if (webService != null) {
                if (!webService.getAtivo()) {
                    sStatus.setDescricao("Usuário inátivo!");
                }
            } else {
                sStatus.setDescricao("Usuário/senha inválidos!");
            }
            sStatus.setCodigo(0);
            sStatus.setDescricao("Cliente válido");
        } catch (Exception e) {
            sStatus.setCodigo(1);
            sStatus.setDescricao(e.getMessage());
        }
        return sStatus;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Boolean getSession() {
        return session;
    }

    public void setSession(Boolean session) {
        this.session = session;
    }

    public static Boolean session() {
        String client = GenericaRequisicao.getParametro("client");
        String mac = GenericaRequisicao.getParametro("mac");
        return session(client, mac);
    }

    public static Boolean session(String client, String mac) {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            String hostName = ia.getHostName();
            Diretorio.criar("cookie", true);
            String path_cookie = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/global/cookie");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            String json = "{'expires':'" + cal.getTime() + "', 'client':'" + client + "', 'host_name':'" + hostName + "'}";
            File file = new File(path_cookie + "/" + mac + ".json");
            JSONObject cookie;
            Boolean create = false;
            Date expires;
            if (file.exists()) {
                cookie = new JSONObject(FileUtils.readFileToString(file));
                expires = (Date) cookie.get("expires");
                client = cookie.getString("client");
                if (expires.before(new Date())) {
                    create = true;
                    file.delete();
                }
                if (!client.equals(client)) {
                    create = true;
                    file.delete();
                }
            } else {
                create = true;
            }
            if (create) {
                try {
                    try (PrintWriter pw = new PrintWriter(new FileOutputStream(path_cookie + "/" + mac + ".json", false), false)) {
                        pw.print(json);
                        pw.write(13);
                        pw.write(10);
                    }
                } catch (Exception e) {

                }
            }
        } catch (NullPointerException | JSONException | IOException e) {
            return false;
        }
        return true;
    }

}
