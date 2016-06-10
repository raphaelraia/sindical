package br.com.rtools.seguranca;

import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "seg_usuario_acesso",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_permissao"})
)
public class UsuarioAcesso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_permissao", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Permissao permissao;
    @Column(name = "is_permite", columnDefinition = "boolean default false", nullable = false)
    private Boolean permite;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_expira")
    private Date dtExpira;

    public UsuarioAcesso() {
        this.id = null;
        this.usuario = new Usuario();
        this.permissao = new Permissao();
        this.permite = false;
        this.dtExpira = null;
    }

    public UsuarioAcesso(Integer id, Usuario usuario, Permissao permissao, Boolean permite, Date dtExpira) {
        this.id = id;
        this.usuario = usuario;
        this.permissao = permissao;
        this.permite = permite;
        this.dtExpira = dtExpira;
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

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    public Boolean getPermite() {
        return permite;
    }

    public void setPermite(Boolean permite) {
        this.permite = permite;
    }

    public Date getDtExpira() {
        return dtExpira;
    }

    public void setDtExpira(Date dtExpira) {
        this.dtExpira = dtExpira;
    }

    public String getExpira() {
        return DataHoje.converteData(dtExpira);
    }

    public void setExpira(String expira) {
        this.dtExpira = DataHoje.converte(expira);
    }
}
