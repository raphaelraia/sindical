package br.com.rtools.financeiro;

import br.com.rtools.seguranca.Departamento;
import javax.persistence.*;

@Entity
@Table(name = "fin_servico_aviso",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"id_servico", "id_departamento"})
        }
)
public class ServicoAviso implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Servicos servicos;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Departamento departamento;

    public ServicoAviso() {
        this.id = -1;
        this.servicos = new Servicos();
        this.departamento = new Departamento();
    }

    public ServicoAviso(int id, Servicos servicos, Departamento departamento) {
        this.id = id;
        this.servicos = servicos;
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

}
