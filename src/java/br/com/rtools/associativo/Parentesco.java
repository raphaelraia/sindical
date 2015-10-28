package br.com.rtools.associativo;

import javax.persistence.*;

@Entity
@Table(name = "soc_parentesco")
@NamedQueries({
    @NamedQuery(name = "Parentesco.pesquisaID", query = "SELECT P FROM Parentesco AS P WHERE P.id = :pid"),
    @NamedQuery(name = "Parentesco.findAll", query = "SELECT P FROM Parentesco AS P ORDER BY P.parentesco ASC")
})
public class Parentesco implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_parentesco", length = 30, nullable = true)
    private String parentesco;
    @Column(name = "ds_sexo", length = 1, nullable = true)
    private String sexo;
    @Column(name = "nr_validade", length = 10, nullable = true)
    private Integer nrValidade;
    @Column(name = "validade", nullable = true)
    private boolean validade;
    @Column(name = "ativo", nullable = true)
    private boolean ativo;

    @Transient
    private Boolean selected;

    public Parentesco() {
        this.id = -1;
        this.parentesco = "";
        this.sexo = "";
        this.nrValidade = 0;
        this.validade = false;
        this.ativo = true;
        this.selected = false;
    }

    public Parentesco(Integer id, String parentesco, String sexo, Integer nrValidade, boolean validade, boolean ativo) {
        this.id = id;
        this.parentesco = parentesco;
        this.sexo = sexo;
        this.nrValidade = nrValidade;
        this.validade = validade;
        this.ativo = ativo;
        this.selected = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public boolean isValidade() {
        return validade;
    }

    public void setValidade(boolean validade) {
        this.validade = validade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getNrValidade() {
        return nrValidade;
    }

    public void setNrValidade(Integer nrValidade) {
        this.nrValidade = nrValidade;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
