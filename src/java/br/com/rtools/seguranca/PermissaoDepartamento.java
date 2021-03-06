package br.com.rtools.seguranca;

import br.com.rtools.seguranca.dao.PermissaoDao;
import br.com.rtools.seguranca.dao.PermissaoDepartamentoDao;
import javax.persistence.*;

@Entity
@Table(name = "seg_permissao_departamento")
@NamedQuery(name = "PermissaoDepartamento.pesquisaID", query = "select pd from PermissaoDepartamento pd where pd.id=:pid")
public class PermissaoDepartamento implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_permissao", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Permissao permissao;
    @JoinColumn(name = "id_nivel", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Nivel nivel;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Departamento departamento;

    public PermissaoDepartamento() {
        this.id = -1;
        this.permissao = new Permissao();
        this.nivel = new Nivel();
        this.departamento = new Departamento();
    }

    public PermissaoDepartamento(int id, Permissao permissao, Nivel nivel, Departamento departamento) {
        this.id = id;
        this.permissao = permissao;
        this.nivel = nivel;
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    // OPERAÇÕES
    public Boolean getInclusao() {
        if (this.id != -1) {
            return new PermissaoDepartamentoDao().findBy(this.departamento.getId(), this.nivel.getId(), this.permissao.getModulo().getId(), this.permissao.getRotina().getId(), 1) != null;
        }
        return false;
    }

    public Boolean getExclusao() {
        if (this.id != -1) {
            return new PermissaoDepartamentoDao().findBy(this.departamento.getId(), this.nivel.getId(), this.permissao.getModulo().getId(), this.permissao.getRotina().getId(), 2) != null;
        }
        return false;
    }

    public Boolean getAlteracao() {
        if (this.id != -1) {
            return new PermissaoDepartamentoDao().findBy(this.departamento.getId(), this.nivel.getId(), this.permissao.getModulo().getId(), this.permissao.getRotina().getId(), 3) != null;
        }
        return false;
    }

    public Boolean getConsulta() {
        if (this.id != -1) {
            return new PermissaoDepartamentoDao().findBy(this.departamento.getId(), this.nivel.getId(), this.permissao.getModulo().getId(), this.permissao.getRotina().getId(), 4) != null;
        }
        return false;
    }

}
