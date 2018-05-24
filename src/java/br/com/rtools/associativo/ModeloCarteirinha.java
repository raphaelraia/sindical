package br.com.rtools.associativo;

import br.com.rtools.associativo.dao.ModeloCarteirinhaCategoriaDao;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

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
    @Column(name = "is_foto", nullable = false, columnDefinition = "boolean default true")
    private Boolean fotoCartao;
    @Column(name = "is_dependente_lista", nullable = false, columnDefinition = "boolean default false")
    private Boolean dependenteLista;

    public ModeloCarteirinha() {
        this.id = -1;
        this.descricao = "";
        this.jasper = "";
        this.foto = null;
        this.fotoCartao = true;
        this.dependenteLista = false;
    }

    public ModeloCarteirinha(Integer id, String descricao, String jasper, String foto, Boolean fotoCartao, Boolean dependenteLista) {
        this.id = id;
        this.descricao = descricao;
        this.jasper = jasper;
        this.foto = foto;
        this.fotoCartao = fotoCartao;
        this.dependenteLista = dependenteLista;
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

    public Boolean getFotoCartao() {
        return fotoCartao;
    }

    public void setFotoCartao(Boolean fotoCartao) {
        this.fotoCartao = fotoCartao;
    }

    /**
     * Caso for gerar uma listagem para o dependente neste modelo de
     * carteirinha, lista ou sublista usada no mesmo cartão ou no cartão com
     * jasper que contenha verso
     *
     * @return
     */
    public Boolean getDependenteLista() {
        return dependenteLista;
    }

    public void setDependenteLista(Boolean dependenteLista) {
        this.dependenteLista = dependenteLista;
    }

}
