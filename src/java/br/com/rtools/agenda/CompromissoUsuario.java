package br.com.rtools.agenda;

import br.com.rtools.seguranca.Usuario;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "age_compromisso_usuario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_compromisso", "id_usuario"})
)
public class CompromissoUsuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_compromisso", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Compromisso compromisso;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne
    private Usuario usuario;

    public CompromissoUsuario() {
        this.id = null;
        this.compromisso = null;
        this.usuario = null;
    }

    public CompromissoUsuario(Integer id, Compromisso compromisso, Usuario usuario) {
        this.id = id;
        this.compromisso = compromisso;
        this.usuario = usuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Compromisso getCompromisso() {
        return compromisso;
    }

    public void setCompromisso(Compromisso compromisso) {
        this.compromisso = compromisso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
