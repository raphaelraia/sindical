package br.com.rtools.sistema;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.MacFilial;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_dispositivo")
public class Dispositivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Filial filial;
    @JoinColumn(name = "id_mac_filial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private MacFilial macFilial;
    @Column(name = "ds_mac", columnDefinition = "character varying default ''")
    private String mac;
    @Column(name = "ds_nome")
    private String nome;
    @JoinColumn(name = "id_tipo_dispositivo", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private TipoDispositivo tipoDispositivo;
    @Column(name = "dt_data", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Column(name = "dt_conectado")
    @Temporal(TemporalType.TIMESTAMP)
    private Date conectado;
    @Column(name = "is_ativo", nullable = false, columnDefinition = "boolean default true")
    private Boolean ativo;
    @Column(name = "ds_socket_host")
    private String socketHost;
    @Column(name = "ds_socket_port")
    private Integer socketPort;
    @Column(name = "ds_mensagem_alerta")
    private String mensagemAlerta;
//    @Column(name = "ds_token")
//    private String token;

    public Dispositivo() {
        this.id = null;
        this.filial = null;
        this.macFilial = null;
        this.mac = "";
        this.nome = "";
        this.tipoDispositivo = null;
        this.data = new Date();
        this.conectado = null;
        this.ativo = false;
        this.socketHost = null;
        this.socketPort = null;
        this.mensagemAlerta = "";
    }

    public Dispositivo(Integer id, Filial filial, MacFilial macFilial, String mac, String nome, TipoDispositivo tipoDispositivo, Date data, Date conectado, Boolean ativo, String socketHost, Integer socketPort, String mensagemAlerta) {
        this.id = id;
        this.filial = filial;
        this.macFilial = macFilial;
        this.mac = mac;
        this.nome = nome;
        this.tipoDispositivo = tipoDispositivo;
        this.data = data;
        this.conectado = conectado;
        this.ativo = ativo;
        this.socketHost = socketHost;
        this.socketPort = socketPort;
        this.mensagemAlerta = mensagemAlerta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public MacFilial getMacFilial() {
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoDispositivo getTipoDispositivo() {
        return tipoDispositivo;
    }

    public void setTipoDispositivo(TipoDispositivo tipoDispositivo) {
        this.tipoDispositivo = tipoDispositivo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getConectado() {
        return conectado;
    }

    public void setConectado(Date conectado) {
        this.conectado = conectado;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getSocketHost() {
        return socketHost;
    }

    public void setSocketHost(String socketHost) {
        this.socketHost = socketHost;
    }

    public Integer getSocketPort() {
        return socketPort;
    }

    public void setSocketPort(Integer socketPort) {
        this.socketPort = socketPort;
    }

    public String getMensagemAlerta() {
        return mensagemAlerta;
    }

    public void setMensagemAlerta(String mensagemAlerta) {
        this.mensagemAlerta = mensagemAlerta;
    }

}
