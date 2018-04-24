package br.com.rtools.sql.beans;

import br.com.rtools.principal.DB;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.dao.ConfiguracaoDao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Sessions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.persistence.Query;

@ManagedBean
@SessionScoped
public class PgStatActivityBean implements Serializable {

    private List<PgStatActivity> listPgStatActivity;
    private List<SelectItem> listDataBase;
    private String dataBase;
    private String state;
    private Integer interval;

    public PgStatActivityBean() {
        interval = 120;
        dataBase = "";
        state = "";
        loadListDataBase();
        loadListPgStatActivity();
    }

    public final void loadListPgStatActivity() {
        this.listPgStatActivity = new ArrayList();
        List list = pgStatActivity();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            int pos = 0;
            listPgStatActivity.add(
                    new PgStatActivity(
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++),
                            o.get(pos++)
                    ));

            System.err.println(pos);
        }
        System.err.println(listPgStatActivity);
    }

    public List<PgStatActivity> getListPgStatActivity() {
        return listPgStatActivity;
    }

    public void setListPgStatActivity(List<PgStatActivity> listPgStatActivity) {
        this.listPgStatActivity = listPgStatActivity;
    }

    public List<SelectItem> getListDataBase() {
        return listDataBase;
    }

    public void setListDataBase(List<SelectItem> listDataBase) {
        this.listDataBase = listDataBase;
    }

    private void loadListDataBase() {
        listDataBase = new ArrayList();
        if (Sessions.getString("sessaoCliente").equals("Rtools")) {
            List<Configuracao> list = new ConfiguracaoDao().listAllActives();
            Configuracao configuracao = new ConfiguracaoDao().find("Rtools");
            for (int i = 0; i < list.size(); i++) {
                if (configuracao.getDatabaseServerAlias().equals(list.get(i).getDatabaseServerAlias())) {
                    listDataBase.add(new SelectItem(list.get(i).getIdentifica(), list.get(i).getIdentifica()));
                }
            }
        }
    }

    public String getDataBase() {
        return dataBase;
    }

    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public class PgStatActivity {

        private Object datid;
        private Object datname;
        private Object pid;
        private Object usesysid;
        private Object usename;
        private Object application_name;
        private Object client_addr;
        private Object client_hostname;
        private Object client_port;
        private Object backend_start;
        private Object xact_start;
        private Object query_start;
        private Object state_change;
        private Object waiting;
        private Object state;
        private Object query;

        public PgStatActivity(Object datid, Object datname, Object pid, Object usesysid, Object usename, Object application_name, Object client_addr, Object client_hostname, Object client_port, Object backend_start, Object xact_start, Object query_start, Object state_change, Object waiting, Object state, Object query) {
            this.datid = datid;
            this.datname = datname;
            this.pid = pid;
            this.usesysid = usesysid;
            this.usename = usename;
            this.application_name = application_name;
            this.client_addr = client_addr;
            this.client_hostname = client_hostname;
            this.client_port = client_port;
            this.backend_start = backend_start;
            this.xact_start = xact_start;
            this.query_start = query_start;
            this.state_change = state_change;
            this.waiting = waiting;
            this.state = state;
            this.query = query;
        }

        public Object getDatid() {
            return datid;
        }

        public void setDatid(Object datid) {
            this.datid = datid;
        }

        public Object getDatname() {
            return datname;
        }

        public void setDatname(Object datname) {
            this.datname = datname;
        }

        public Object getPid() {
            return pid;
        }

        public void setPid(Object pid) {
            this.pid = pid;
        }

        public Object getUsesysid() {
            return usesysid;
        }

        public void setUsesysid(Object usesysid) {
            this.usesysid = usesysid;
        }

        public Object getUsename() {
            return usename;
        }

        public void setUsename(Object usename) {
            this.usename = usename;
        }

        public Object getApplication_name() {
            return application_name;
        }

        public void setApplication_name(Object application_name) {
            this.application_name = application_name;
        }

        public Object getClient_addr() {
            return client_addr;
        }

        public void setClient_addr(Object client_addr) {
            this.client_addr = client_addr;
        }

        public Object getClient_hostname() {
            return client_hostname;
        }

        public void setClient_hostname(Object client_hostname) {
            this.client_hostname = client_hostname;
        }

        public Object getClient_port() {
            return client_port;
        }

        public void setClient_port(Object client_port) {
            this.client_port = client_port;
        }

        public Object getBackend_start() {
            return backend_start;
        }

        public void setBackend_start(Object backend_start) {
            this.backend_start = backend_start;
        }

        public Object getXact_start() {
            return xact_start;
        }

        public void setXact_start(Object xact_start) {
            this.xact_start = xact_start;
        }

        public Object getQuery_start() {
            return query_start;
        }

        public void setQuery_start(Object query_start) {
            this.query_start = query_start;
        }

        public Object getState_change() {
            return state_change;
        }

        public void setState_change(Object state_change) {
            this.state_change = state_change;
        }

        public Object getWaiting() {
            return waiting;
        }

        public void setWaiting(Object waiting) {
            this.waiting = waiting;
        }

        public Object getState() {
            return state;
        }

        public void setState(Object state) {
            this.state = state;
        }

        public Object getQuery() {
            return query;
        }

        public void setQuery(Object query) {
            this.query = query;
        }

        public String getQueryResume() {
            String resume = "";
            try {
                resume = query.toString();
                if (resume.length() > 50) {
                    resume = resume.substring(0, 50) + "...";
                }
            } catch (Exception e) {

            }
            return resume;
        }

    }

    public List pgStatActivity() {

        DB db = new DB();
        try {
            String queryString = "    "
                    + "SELECT PSA.datid,            \n" // 0
                    + "       PSA.datname,          \n" // 1
                    + "       PSA.pid,              \n" // 2
                    + "       PSA.usesysid,         \n" // 3
                    + "       PSA.usename,          \n" // 4
                    + "       PSA.application_name, \n" // 5
                    + "       PSA.client_addr,      \n" // 6
                    + "       PSA.client_hostname,  \n" // 7
                    + "       PSA.client_port,      \n" // 8
                    + "       PSA.backend_start,    \n" // 9
                    + "       PSA.xact_start,       \n" // 10
                    + "       PSA.query_start,      \n" // 11
                    + "       PSA.state_change,     \n" // 12
                    + "       PSA.waiting,          \n" // 13
                    + "       PSA.state,            \n" // 14
                    + "       PSA.query             \n" // 15
                    + "  FROM pg_stat_activity PSA  \n"
                    + " WHERE pid <> 0              ";
            String datname = null;
            if (Sessions.exists("sessaoCliente")) {
                datname = Sessions.getString("sessaoCliente");
                if (datname.equals("ComercioRP")) {
                    datname = "Sindical";
                }
                if (listDataBase.isEmpty()) {
                    if (!Sessions.getString("sessaoCliente").equals("Rtools")) {
                        queryString += " AND PSA.datname = '" + datname + "'";
                    }
                } else {
                    if (dataBase != null && !dataBase.isEmpty()) {
                        queryString += " AND PSA.datname = '" + dataBase + "'";
                    }
                }
                if (state != null && !state.isEmpty()) {
                    queryString += " AND PSA.state = '" + state + "'";
                }
            }
            queryString += "   ORDER BY PSA.datname ASC, PSA.application_name \n";
            Query qry = db.getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public void pgTerminateBackend(Object pid) {
        DB db = new DB();
        try {
            Integer id = Integer.parseInt(pid.toString());
            String queryString = "SELECT pg_terminate_backend(" + id + ")";
            Query query = db.getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (true) {
                loadListPgStatActivity();
                Messages.info("Sucesso", "Processo finalizado!!!");
            }
        } catch (Exception e) {

        }
    }

    public Integer count(String tcase) {
        Integer count = 0;
        String aux = "";
        for (int i = 0; i < listPgStatActivity.size(); i++) {
            if(tcase.equals("clients")) {
                if (listPgStatActivity.get(i).getDatname() != null && !listPgStatActivity.get(i).getDatname().equals(aux)) {
                    aux = listPgStatActivity.get(i).getDatname().toString();
                    count++;
                }
            } else {
                if (listPgStatActivity.get(i).getState() != null && listPgStatActivity.get(i).getState().equals(tcase)) {
                    count++;
                }
            }
        }
        return count;
    }
    
    public Boolean getAlert(String tcase) {
        Integer count = 0;
        for (int i = 0; i < listPgStatActivity.size(); i++) {
            if (listPgStatActivity.get(i).getState().equals(tcase)) {
                count++;
            }
        }
        return false;
    }
    
    

}
