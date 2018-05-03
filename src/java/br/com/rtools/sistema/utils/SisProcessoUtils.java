package br.com.rtools.sistema.utils;

import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.RunningProcess;
import br.com.rtools.utilitarios.Sessions;
import java.util.Date;

public class SisProcessoUtils {

    private SisProcesso sisProcesso;
    private Date aborted;
    private Relatorios relatorio;
    private Boolean status;

    public SisProcessoUtils() {
        Sessions.remove("session_process_uid");
        Rotina r = new Rotina().get();
        if (r != null) {
            Sessions.remove("session_process_xuid" + r.getId());
        }
        RunningProcess.stop();
        status = null;
        relatorio = null;
        aborted = null;
        this.sisProcesso = new SisProcesso();
    }

    public void statusOpen() {
        this.status = true;
        RunningProcess.run();
    }

    public void statusClose() {
        this.status = null;
        RunningProcess.stop();
    }

    public void start() {
        start(null);
    }

    public void start(Boolean status) {
        if (status != null && this.status == null) {
            this.status = status;
        }
        if (this.status != null) {
            RunningProcess.run();
        }
        if (sisProcesso.getId() == null) {
            Usuario u = Usuario.getUsuario();
            if (u != null) {
                Rotina r = new Rotina().get();
                if (r != null) {
                    sisProcesso.setTempo(System.currentTimeMillis());
                    sisProcesso.setUsuario(u);
                    sisProcesso.setRotina(r);
                    sisProcesso.setRelatorio(relatorio);
                    sisProcesso.setData(new Date());
                    new Dao().save(sisProcesso, true);
                    Sessions.put("session_process_uid", sisProcesso.getId());
                    Sessions.put("session_process_xuid" + r.getId(), sisProcesso.getId());
                }
            }
        } else {
            sisProcesso.setTempo(System.currentTimeMillis());
        }
    }

    public void startQuery() {
        startQuery(false);
    }

    public void startQuery(Boolean status) {
        if (sisProcesso.getId() == null) {
            if (status != null && this.status == null) {
                this.status = status;
            }
            if (this.status != null) {
                RunningProcess.run();
            }
            Usuario u = Usuario.getUsuario();
            if (u != null) {
                Rotina r = new Rotina().get();
                if (r != null) {
                    sisProcesso.setTempoQuery(System.currentTimeMillis());
                    sisProcesso.setUsuario(u);
                    sisProcesso.setRotina(r);
                    sisProcesso.setData(new Date());
                    sisProcesso.setWeb(false);
                    sisProcesso.setFinalizado(new Date());
                    sisProcesso.setRelatorio(relatorio);
                    new Dao().save(sisProcesso, true);
                    Sessions.put("session_process_uid", sisProcesso.getId());
                    Sessions.put("session_process_xuid" + r.getId(), sisProcesso.getId());
                }
            }
        } else {
            sisProcesso.setTempoQuery(System.currentTimeMillis());
        }
    }

    public void finishQuery() {
        if (sisProcesso.getId() != null) {
            Dao dao = new Dao();
            sisProcesso = (SisProcesso) dao.rebind(dao.find(sisProcesso));
            if(sisProcesso.getAbortado() == null) {
                sisProcesso.setTempoQuery(System.currentTimeMillis() - sisProcesso.getTempoQuery());
                sisProcesso.setFinalizado(new Date());
                dao.update(sisProcesso, true);                
            }
        }
        Sessions.remove("session_process_uid");
    }

    public void finish() {
        if (sisProcesso.getId() != null) {
            Dao dao = new Dao();
            sisProcesso = (SisProcesso) dao.rebind(dao.find(sisProcesso));
            if (this.status != null && this.status) {
                RunningProcess.stop();
            }
            sisProcesso.setTempo(System.currentTimeMillis() - sisProcesso.getTempo());
            if(sisProcesso.getAbortado() == null) {
                sisProcesso.setAbortado(aborted);
                sisProcesso.setFinalizado(new Date());                
                dao.update(sisProcesso, true);
            }
        }
        Sessions.remove("session_process_uid");
    }

    public void cancel() {
        aborted = new Date();
    }

    public SisProcesso getSisProcesso() {
        return sisProcesso;
    }

    public void setSisProcesso(SisProcesso sisProcesso) {
        this.sisProcesso = sisProcesso;
    }

    public Date getAborted() {
        return aborted;
    }

    public void setAborted(Date aborted) {
        this.aborted = aborted;
    }

    public Relatorios getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorios relatorio) {
        this.relatorio = relatorio;
    }

    public Integer pid() {
        if (sisProcesso != null && sisProcesso.getId() != null) {
            return sisProcesso.getId();
        }
        return null;
    }

}
