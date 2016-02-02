package br.com.rtools.seguranca.controleUsuario;

import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.WebService;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.seguranca.dao.WebServiceDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.net.InetAddress;

public class ControleAcessoWebService {

    private String client;
    private String user;
    private String password;
    private String app;
    private String key;
    private String method;
    private String action;
    private String mac;

    public ControleAcessoWebService() {
        this.client = GenericaRequisicao.getParametro("client");
        this.user = GenericaRequisicao.getParametro("user");
        this.password = GenericaRequisicao.getParametro("password");
        this.app = GenericaRequisicao.getParametro("app");
        this.key = GenericaRequisicao.getParametro("key");
        this.method = GenericaRequisicao.getParametro("method");
        this.action = GenericaRequisicao.getParametro("action");
        this.mac = GenericaRequisicao.getParametro("mac");
        GenericaSessao.put("sessaoCliente", client);
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

    public String permiteWebService(String mac, Boolean message) {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            String hostName = ia.getHostName();
            MacFilial macFilial = new MacFilialDao().pesquisaMac(mac);
            if(macFilial == null) {
                return "Mac não existe!";                
            }
            if (!hostName.equals(macFilial.getNomeDispositivo())) {
                return "Computador não registrado! Solicite a liberação ao administrador do sistema.";
            }
            WebService webService = new WebServiceDao().find(user, password);
            if (webService != null) {
                if (!webService.getAtivo()) {
                    return "Usuário inátivo!";
                }
            } else {
                return "Usuário/senha inválidos!";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
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

}
