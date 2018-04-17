package br.com.rtools.webservice;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class WSMonitor implements Serializable {

    public void response() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            JSONObject obj = new JSONObject();

            JSONArray list = new JSONArray();
//            if (bp != null) {
//                obj.put("est", "");
//                externalContext.getResponseOutputWriter().write(obj.toString());
//                facesContext.responseComplete();
//                return;
//            }

            externalContext.getResponseOutputWriter().write(new JSONObject("{\"identificador\":null}").toString());
            facesContext.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(WSMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
