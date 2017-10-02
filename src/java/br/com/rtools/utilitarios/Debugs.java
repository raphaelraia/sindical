package br.com.rtools.utilitarios;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class Debugs {

    private String sessionDebugs = "";

    public static void put(String sessionName, String value) {
        GenericaSessao.remove("sessionDebugs");
        if (GenericaSessao.exists(sessionName)) {
            if (GenericaSessao.getBoolean(sessionName)) {
                GenericaSessao.put("sessionDebugs", value);
                try {
                    PF.update("header:form_debug_query");
                } catch (Exception e) {

                }
            }
        }
    }

    public Boolean getExists(String sessionName) {
        if (GenericaSessao.exists(sessionName)) {
            if (GenericaSessao.getBoolean(sessionName)) {
                if (GenericaSessao.exists("sessionDebugs")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String show(String sessionName) {
        sessionDebugs = "";
        if (GenericaSessao.exists(sessionName)) {
            if (GenericaSessao.getBoolean(sessionName)) {
                sessionDebugs = GenericaSessao.getString("sessionDebugs", true);
            }
        }
        return sessionDebugs;
    }

    public void remove() {
        GenericaSessao.remove("sessionDebugs");
    }

    public String getSessionDebugs() {
        return sessionDebugs;
    }

    public void setSessionDebugs(String sessionDebugs) {
        // this.sessionDebugs = sessionDebugs; 
    }

}
