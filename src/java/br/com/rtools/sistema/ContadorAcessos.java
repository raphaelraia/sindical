package br.com.rtools.sistema;

import br.com.rtools.seguranca.Modulo;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import javax.persistence.*;

@Entity
@Table(name = "sis_contador_acessos")
@NamedQuery(name = "ContadorAcessos.pesquisaID", query = "select ca from ContadorAcessos ca where ca.id = :pid")
public class ContadorAcessos implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @JoinColumn(name = "id_modulo", referencedColumnName = "id")
    @ManyToOne
    private Modulo modulo;
    @Column(name = "nr_acesso")
    private Integer acessos;

    public ContadorAcessos() {
        this.id = null;
        this.usuario = new Usuario();
        this.rotina = new Rotina();
        this.modulo = null;
        this.acessos = 0;
    }

    public ContadorAcessos(Integer id, Usuario usuario, Rotina rotina, Modulo modulo, Integer acessos) {
        this.id = id;
        this.usuario = usuario;
        this.rotina = rotina;
        this.modulo = modulo;
        this.acessos = acessos;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getAcessos() {
        return acessos;
    }

    public void setAcessos(Integer acessos) {
        this.acessos = acessos;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }
}
