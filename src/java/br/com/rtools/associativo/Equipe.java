package br.com.rtools.associativo;

import br.com.rtools.pessoa.Juridica;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "eve_equipe",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "id_modalidade",
                    "ds_descricao"
                }
        )
)
@NamedQueries({
    @NamedQuery(name = "Equipe.findAll", query = "SELECT E FROM Equipe AS E ORDER BY E.descricao ASC")
})
public class Equipe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_modalidade", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private CampeonatoModalidade modalidade;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id")
    @ManyToOne
    private Juridica patrocinador;
    @Column(name = "ds_descricao", length = 300)
    private String descricao;

    public Equipe() {
        this.id = null;
        this.modalidade = null;
        this.patrocinador = null;
        this.descricao = "";
    }

    public Equipe(Integer id, CampeonatoModalidade modalidade, Juridica patrocinador, String descricao) {
        this.id = id;
        this.modalidade = modalidade;
        this.patrocinador = patrocinador;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CampeonatoModalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(CampeonatoModalidade modalidade) {
        this.modalidade = modalidade;
    }

    public Juridica getPatrocinador() {
        return patrocinador;
    }

    public void setPatrocinador(Juridica patrocinador) {
        this.patrocinador = patrocinador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
