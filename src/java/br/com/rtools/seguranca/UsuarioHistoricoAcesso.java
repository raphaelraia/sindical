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
    @Column(name = "dt_data")
    private Date dtData;
    @Column(name = "ds_ip", length = 15)
    private String ip;
    @Column(name = "ds_es", length = 1, nullable = false)
    private String es;
    @Column(name = "ds_dispositivo", length = 255)
    private String dispositivo;

    public UsuarioHistoricoAcesso() {
        this.id = null;
        this.macFilial = null;
        this.usuario = null;
        this.dtData = new Date();
        this.ip = "";
        this.es = "";
        this.dispositivo = "";
    }

    public UsuarioHistoricoAcesso(Integer id, MacFilial macFilial, Usuario usuario, Date dtData, String ip, String es, String dispositivo) {
        this.id = id;
        this.macFilial = macFilial;
        this.usuario = usuario;
        this.dtData = dtData;
        this.ip = ip;
        this.es = es;
        this.dispositivo = dispositivo;
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

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public String getData() {
        return DataHoje.converteData(dtData);
    }

    public void setData(String data) {
        this.dtData = DataHoje.converte(data);
    }

    public String getHora() {
        return DataHoje.converteHora(dtData);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

}
