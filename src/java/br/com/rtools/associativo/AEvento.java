package br.com.rtools.associativo;

import javax.persistence.*;

@Entity
@Table(name = "eve_evento")
public class AEvento implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_descricao_evento", referencedColumnName = "id", nullable = true)
    @OneToOne
    private DescricaoEvento descricaoEvento;

    public AEvento() {
        this.id = -1;
        this.descricaoEvento = new DescricaoEvento();
    }

    public AEvento(Integer id, DescricaoEvento descricaoEvento) {
        this.id = id;
        this.descricaoEvento = descricaoEvento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DescricaoEvento getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(DescricaoEvento descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }
}
