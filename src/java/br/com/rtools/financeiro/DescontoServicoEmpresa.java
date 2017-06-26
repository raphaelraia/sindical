package br.com.rtools.financeiro;

import br.com.rtools.pessoa.Juridica;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "fin_desconto_servico_empresa")
@NamedQuery(name = "DescontoServicoEmpresa.pesquisaID", query = "SELECT DSEM FROM DescontoServicoEmpresa AS DSEM WHERE DSEM.id=:pid")
public class DescontoServicoEmpresa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id")
    @ManyToOne
    private Juridica juridica;
    @JoinColumn(name = "id_servico", referencedColumnName = "id")
    @ManyToOne
    private Servicos servicos;
    @Column(name = "nr_desconto")
    private double desconto;
    @JoinColumn(name = "id_grupo ", referencedColumnName = "id")
    @ManyToOne
    private DescontoServicoEmpresaGrupo grupo;

    public DescontoServicoEmpresa() {
        this.id = -1;
        this.juridica = new Juridica();
        this.servicos = new Servicos();
        this.desconto = 0;
        this.grupo = null;
    }

    public DescontoServicoEmpresa(int id, Juridica juridica, Servicos servicos, double desconto, DescontoServicoEmpresaGrupo grupo) {
        this.id = id;
        this.juridica = juridica;
        this.servicos = servicos;
        this.desconto = desconto;
        this.grupo = grupo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public String getDescontoString() {
        return Moeda.converteR$Double(desconto);
    }

    public void setDescontoString(String desconto) {
        this.desconto = Double.parseDouble(Moeda.substituiVirgula(desconto));
    }

    public DescontoServicoEmpresaGrupo getGrupo() {
        return grupo;
    }

    public void setGrupo(DescontoServicoEmpresaGrupo grupo) {
        this.grupo = grupo;
    }
}
