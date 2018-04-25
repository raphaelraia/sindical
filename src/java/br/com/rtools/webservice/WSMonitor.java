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
public class WSMonitor implements Serializable {

    public void response() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String action = GenericaRequisicao.getParametro("action");
            JSONObject obj = new JSONObject();
            obj.put("status", "offline");
            if (action == null || action.isEmpty()) {

            } else if (action.equals("status_server")) {
                obj.put("status", "online");
            } else if (action.equals("status_database")) {
                obj.put("status", "online");
            }

            JSONArray list = new JSONArray();
//            if (bp != null) {
//                obj.put("est", "");
//                externalContext.getResponseOutputWriter().write(obj.toString());
//                facesContext.responseComplete();
//                return;
//            }

            externalContext.getResponseOutputWriter().write(obj.toString());
            facesContext.responseComplete();
        } catch (IOException ex) {
            Logger.getLogger(WSMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String status(String database) {
//        if (database.isEmpty()) {
//            return "specify client";
//        }
//        List<PgStatActivityBean.PgStatActivity> list = new ArrayList();
//        String nomeCliente = null;
//        DBExternal dbe = new DBExternal();
//        dbe.configure(database);
//        dbe.setApplicationName("monitor status " + database);
//        Connection conn = null;
//        ResultSet rs = null;
//        PreparedStatement ps = null;
//        try {
//            conn = dbe.getConnection();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        String queryString = "    "
//                + "SELECT PSA.datid,            \n" // 0
//                + "       PSA.datname,          \n" // 1
//                + "       PSA.pid,              \n" // 2
//                + "       PSA.usesysid,         \n" // 3
//                + "       PSA.usename,          \n" // 4
//                + "       PSA.application_name, \n" // 5
//                + "       PSA.client_addr,      \n" // 6
//                + "       PSA.client_hostname,  \n" // 7
//                + "       PSA.client_port,      \n" // 8
//                + "       PSA.backend_start,    \n" // 9
//                + "       PSA.xact_start,       \n" // 10
//                + "       PSA.query_start,      \n" // 11
//                + "       PSA.state_change,     \n" // 12
//                + "       PSA.waiting,          \n" // 13
//                + "       PSA.state,            \n" // 14
//                + "       PSA.query             \n" // 15
//                + "  FROM pg_stat_activity PSA  \n"
//                + " WHERE pid <> 0              ";
//
//        String datname = null;
//        if (Sessions.exists("sessaoCliente")) {
//            datname = Sessions.getString("sessaoCliente");
//            if (datname.equals("ComercioRP")) {
//                datname = "Sindical";
//            }
//            if (listDataBase.isEmpty()) {
//                if (!Sessions.getString("sessaoCliente").equals("Rtools")) {
//                    queryString += " AND PSA.datname = '" + datname + "'";
//                }
//            } else {
//                if (dataBase != null && !dataBase.isEmpty()) {
//                    queryString += " AND PSA.datname = '" + dataBase + "'";
//                }
//            }
//            if (state != null && !state.isEmpty()) {
//                queryString += " AND PSA.state = '" + state + "'";
//            }
//        }
//        queryString += "   ORDER BY PSA.datname ASC, PSA.application_name \n";
//
//        try {
//            ps = conn.prepareStatement(queryString);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//
//                list.add(new PgStatActivityBean.PgStatActivity(
//                        rs.getObject("datid"),
//                        rs.getObject("datname"),
//                        rs.getObject("pid"),
//                        rs.getObject("usesysid"),
//                        rs.getObject("usename"),
//                        rs.getObject("application_name"),
//                        rs.getObject("client_addr"),
//                        rs.getObject("client_hostname"),
//                        rs.getObject("client_port"),
//                        rs.getObject("backend_start"),
//                        rs.getObject("xact_start"),
//                        rs.getObject("query_start"),
//                        rs.getObject("state_change"),
//                        rs.getObject("waiting"),
//                        rs.getObject("state"),
//                        rs.getObject("query")
//                ));
//            }
//        } catch (SQLException e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            return list;
//        } finally {
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException e) {
//                    /* ignored */
//                }
//            }
//            if (ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) {
//                    /* ignored */
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    /* ignored */
//                }
//            }
//        }
        return "";
    }

}
