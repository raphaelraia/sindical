package br.com.rtools.webservice;

import br.com.rtools.principal.DBExternal;
import br.com.rtools.sql.beans.PgStatActivityBean;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Sessions;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
public class WSRotina implements Serializable {

    public void response() {
        JSONObject obj = new JSONObject();
        obj.put("funcionamento", "");
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String rotina_id = GenericaRequisicao.getParametro("rotina_id");
            String action = GenericaRequisicao.getParametro("action");
            if (action.equals("funcionamento")) {
                obj.put("funcionamento", funcionamento(Integer.parseInt(rotina_id)));
            }
            externalContext.getResponseOutputWriter().write(obj.toString());
            facesContext.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(WSMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected String funcionamento(Integer rotina_id) {
        DBExternal dbe = new DBExternal();
        dbe.setDatabase("Rtools");
        dbe.setApplicationName(" rotinas funcionamento: " + rotina_id);
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = dbe.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String queryString = "   "
                + " -- auth client \n\n         "
                + " SELECT *                    \n"
                + "   FROM seg_rotina           \n"
                + "  WHERE id = " + rotina_id;

        try {
            ps = conn.prepareStatement(queryString);
            rs = ps.executeQuery();
            if (!rs.next()) {
                return "";
            }

            return rs.getString("ds_funcionamento");
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    /* ignored */
                }
            }
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
        return "";
    }

}
