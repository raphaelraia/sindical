package br.com.rtools.sistema;

import br.com.rtools.seguranca.Rotina;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "sis_critica")
public class Critica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Rotina rotina;
    @Column(name = "ds_chave", length = 150)
    private String chave;
    @Column(name = "ds_chave2", length = 30)
    private String chave2;
    @Column(name = "ds_critica", length = 5000)
    private String critica;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_data")
    private Date dtData;

    public Critica() {
        this.id = null;
        this.rotina = null;
        this.chave = "";
        this.chave2 = "";
        this.critica = "";
        this.dtData = new Date();
    }

    public Critica(Rotina rotina, String chave, String critica) {
        this.id = null;
        this.rotina = rotina;
        this.chave = chave;
        this.chave2 = "";
        this.critica = critica;
        this.dtData = new Date();
    }

    public Critica(Rotina rotina, String chave, String chave2, String critica) {
        this.id = null;
        this.rotina = rotina;
        this.chave = chave;
        this.chave2 = chave2;
        this.critica = critica;
        this.dtData = new Date();
    }

    public Critica(Integer id, Rotina rotina, String chave, String critica) {
        this.id = id;
        this.rotina = rotina;
        this.chave = chave;
        this.chave2 = "";
        this.critica = critica;
        this.dtData = new Date();
    }

    public Critica(Integer id, Rotina rotina, String chave, String chave2, String critica) {
        this.id = id;
        this.rotina = rotina;
        this.chave = chave;
        this.chave2 = chave2;
        this.critica = critica;
        this.dtData = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getChave2() {
        return chave2;
    }

    public void setChave2(String chave2) {
        this.chave2 = chave2;
    }

    public String getCritica() {
        return critica;
    }

    public void setCritica(String critica) {
        this.critica = critica;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

}
