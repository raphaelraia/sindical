package br.com.rtools.agenda;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.BaseEntity;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "age_secretaria")
@NamedQueries({
    @NamedQuery(name = "Secretaria.findAll", query = "SELECT S FROM Secretaria S ORDER BY S.secretaria.pessoa.nome ASC ")
})
public class Secretaria implements BaseEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_secretaria", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario secretaria;

    public Secretaria() {
        this.id = null;
        this.usuario = null;
        this.secretaria = null;
    }

    public Secretaria(Integer id, Usuario usuario, Usuario secretaria) {
        this.id = id;
        this.usuario = usuario;
        this.secretaria = secretaria;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario agenda) {
        this.usuario = agenda;
    }

    public Usuario getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Usuario secretaria) {
        this.secretaria = secretaria;
    }

}
