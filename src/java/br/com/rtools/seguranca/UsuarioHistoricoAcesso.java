package br.com.rtools.seguranca;

import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "seg_usuario_historico_acesso")
public class UsuarioHistoricoAcesso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_mac_filial", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private MacFilial macFilial;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Usuario usuario;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_login")
    private Date dtLogin;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_logout")
    private Date dtLogout;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_expired")
    private Date dtExpired;
    @Column(name = "ds_ip", length = 15)
    private String ip;
    @Column(name = "ds_dispositivo", length = 255)
    private String dispositivo;
    @Column(name = "nr_session_id", length = 150)
    private String sessionId;

    public UsuarioHistoricoAcesso() {
        this.id = null;
        this.macFilial = null;
        this.usuario = null;
        this.dtLogin = new Date();
        this.dtLogout = null;
        this.dtExpired = null;
        this.ip = "";
        this.dispositivo = "";
        this.sessionId = null;
    }

    public UsuarioHistoricoAcesso(Integer id, MacFilial macFilial, Usuario usuario, Date dtLogin, Date dtLogout, Date dtExpired, String ip, String dispositivo, String sessionId) {
        this.id = id;
        this.macFilial = macFilial;
        this.usuario = usuario;
        this.dtLogin = dtLogin;
        this.dtLogout = dtLogout;
        this.dtExpired = dtExpired;
        this.ip = ip;
        this.dispositivo = dispositivo;
        this.sessionId = sessionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MacFilial getMacFilial() {
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDtLogin() {
        return dtLogin;
    }

    public void setDtLogin(Date dtLogin) {
        this.dtLogin = dtLogin;
    }

    public String getLogin() {
        return DataHoje.converteData(dtLogin);
    }

    public void setLogin(String login) {
        this.dtLogin = DataHoje.converte(login);
    }

    public String getLoginHora() {
        return DataHoje.converteHora(dtLogin);
    }

    public Date getDtLogout() {
        return dtLogout;
    }

    public void setDtLogout(Date dtLogout) {
        this.dtLogout = dtLogout;
    }

    public String getLogout() {
        return DataHoje.converteData(dtLogout);
    }

    public void setLogout(String logout) {
        this.dtLogin = DataHoje.converte(logout);
    }

    public String getLogoutHora() {
        return DataHoje.converteHora(dtLogout);
    }

    public Date getDtExpired() {
        return dtExpired;
    }

    public void setDtExpired(Date dtExpired) {
        this.dtExpired = dtExpired;
    }

    public String getExpired() {
        return DataHoje.converteData(dtExpired);
    }

    public void setExpired(String expired) {
        this.dtExpired = DataHoje.converte(expired);
    }

    public String getExpiredHora() {
        return DataHoje.converteHora(dtExpired);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
