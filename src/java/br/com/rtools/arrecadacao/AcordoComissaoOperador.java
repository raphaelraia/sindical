package br.com.rtools.arrecadacao;

import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arr_acordo_comissao_operador",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_rotina"})
)
public class AcordoComissaoOperador implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @Column(name = "nr_comissao", nullable = false)
    private Double nrComissao;
    @Column(name = "is_comissao ", nullable = false, columnDefinition = "boolean default false")
    private Boolean comissao;

    public AcordoComissaoOperador() {
        this.id = null;
        this.usuario = null;
        this.rotina = null;
        this.nrComissao = new Double(0);
        this.comissao = false;
    }

    public AcordoComissaoOperador(Integer id, Usuario usuario, Rotina rotina, Double nrComissao, Boolean comissao) {
        this.id = id;
        this.usuario = usuario;
        this.rotina = rotina;
        this.nrComissao = nrComissao;
        this.comissao = comissao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Double getNrComissao() {
        return nrComissao;
    }

    public void setNrComissao(Double nrComissao) {
        this.nrComissao = nrComissao;
    }

    public Boolean getComissao() {
        return comissao;
    }

    public void setComissao(Boolean comissao) {
        this.comissao = comissao;
    }

}
