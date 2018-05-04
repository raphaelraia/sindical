package br.com.rtools.utilitarios;

import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.sql.beans.PgStatActivityBean;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class RunningProcess implements Serializable {

    private final Rotina rotina;
    private Date start;

    public RunningProcess() {
        rotina = new Rotina().get();
        Sessions.remove("start_running_process");
        if (rotina != null) {
            Sessions.remove("session_process_uid" + rotina.getId());
        }
        start = new Date();
    }

    public static void init() {
        Sessions.put("start_running_process", new Date());
        Sessions.remove("runningProcess");
    }

    public static void run() {
        Sessions.put("start_running_process", new Date());
        Sessions.put("runningProcess", true);
    }

    public static void stop() {
        Sessions.remove("runningProcess");
        Sessions.remove("start_running_process");
    }

    public Boolean getRunningProcess() {
        try {
            if (Sessions.exists("runningProcess")) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean isRenderedKill() {
        Date d = (Date) Sessions.getDate("start_running_process");
        int diff = DataHoje.diffHour(DataHoje.livre(d, "HH:mm:ss"), DataHoje.livre(new Date(), "HH:mm:ss"));
        if (diff > 0) {
            return rotina != null && Sessions.exists("session_process_xuid" + rotina.getId());
        }
        return false;
    }

    public void kill() {
        Sessions.remove("start_running_process");
        Sessions.remove("runningProcess");
        PF.closeDialog("running_process");
        if (rotina != null && Sessions.exists("session_process_xuid" + rotina.getId())) {
            Messages.warn("Sistema", "Processo abortado pelo usuário nº" + Sessions.getInteger("session_process_xuid" + rotina.getId()));
            PgStatActivityBean pgStatActivityBean = new PgStatActivityBean();
            pgStatActivityBean.kill_session_process_uid("" + Sessions.getInteger("session_process_xuid" + rotina.getId()));
            Sessions.remove("session_process_xuid" + rotina.getId());
        }
    }

}
