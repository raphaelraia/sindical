package br.com.rtools.associativo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "soc_modelo_carteirinha")
@NamedQuery(name = "ModeloCarteirinha.findAll", query = "SELECT MC FROM ModeloCarteirinha AS MC ORDER BY MC.descricao ASC")
public class ModeloCarteirinha implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 150)
    private String descricao;
    @Column(name = "ds_jasper", length = 150)
    private String jasper;
    @Column(name = "ds_foto", length = 255)
    private String foto;

    public ModeloCarteirinha() {
        this.id = -1;
        this.descricao = "";
        this.jasper = "";
        this.foto = null;
    }

    public ModeloCarteirinha(Integer id, String descricao, String jasper, String foto) {
        this.id = id;
        this.descricao = descricao;
        this.jasper = jasper;
        this.foto = foto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getJasper() {
        return jasper;
    }

    public void setJasper(String jasper) {
        this.jasper = jasper;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
