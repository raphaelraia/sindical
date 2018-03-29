package br.com.rtools.sistema;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "sis_controle_scripts")
public class ControleScripts implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "dt_data", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtData;
    @JoinColumn(name = "id_tipo_controle_scripts", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TipoControleScripts controleScripts;
    @Column(name = "ds_servidor", length = 150)
    private String servidor;
    @Column(name = "ds_descricao", length = 150)
    private String descricao;
    @Column(name = "ds_mac", length = 20)
    private String mac;
    @Column(name = "nr_tamanho")
    private Integer tamanho;
    @Column(name = "is_erro")
    private Boolean erro;

    public ControleScripts() {
        this.id = null;
        this.dtData = new Date();
        this.controleScripts = null;
        this.servidor = "";
        this.descricao = "";
        this.mac = "";
        this.tamanho = 0;
        this.erro = false;
    }

    public ControleScripts(Integer id, Date dtData, TipoControleScripts controleScripts, String servidor, String descricao, String mac, Integer tamanho, Boolean erro) {
        this.id = id;
        this.dtData = dtData;
        this.controleScripts = controleScripts;
        this.descricao = descricao;
        this.servidor = servidor;
        this.mac = mac;
        this.tamanho = tamanho;
        this.erro = erro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public TipoControleScripts getControleScripts() {
        return controleScripts;
    }

    public void setControleScripts(TipoControleScripts controleScripts) {
        this.controleScripts = controleScripts;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public Boolean getErro() {
        return erro;
    }

    public void setErro(Boolean erro) {
        this.erro = erro;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

}
