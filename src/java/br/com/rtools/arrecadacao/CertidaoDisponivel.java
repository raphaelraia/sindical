package br.com.rtools.arrecadacao;

import br.com.rtools.endereco.Cidade;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "arr_certidao_disponivel")
@NamedQuery(name = "CertidaoDisponivel.pesquisaID", query = "select cd from CertidaoDisponivel cd where cd.id = :pid")
public class CertidaoDisponivel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_cidade", referencedColumnName = "id")
    @ManyToOne
    private Cidade cidade;    
    @JoinColumn(name = "id_convencao", referencedColumnName = "id")
    @ManyToOne
    private Convencao convencao;    
    @JoinColumn(name = "id_certidao_tipo", referencedColumnName = "id")
    @ManyToOne
    private CertidaoTipo certidaoTipo;    
    @Column(name = "is_periodo_convencao", columnDefinition = "boolean default false")
    private boolean periodoConvencao;    

    @Transient
    private Boolean selected;
    
    public CertidaoDisponivel() {
        this.id = -1;
        this.cidade = new Cidade();
        this.convencao = new Convencao();
        this.certidaoTipo = new CertidaoTipo();
        this.periodoConvencao = true;
        this.selected = false;
    }
    
    public CertidaoDisponivel(int id, Cidade cidade, Convencao convencao, CertidaoTipo certidaoTipo, boolean periodoConvencao) {
        this.id = id;
        this.cidade = cidade;
        this.convencao = convencao;
        this.certidaoTipo = certidaoTipo;
        this.periodoConvencao = periodoConvencao;
        this.selected = false;
    }
    
    public CertidaoDisponivel(int id, Cidade cidade, Convencao convencao, CertidaoTipo certidaoTipo, boolean periodoConvencao, Boolean selected) {
        this.id = id;
        this.cidade = cidade;
        this.convencao = convencao;
        this.certidaoTipo = certidaoTipo;
        this.periodoConvencao = periodoConvencao;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Convencao getConvencao() {
        return convencao;
    }

    public void setConvencao(Convencao convencao) {
        this.convencao = convencao;
    }

    public CertidaoTipo getCertidaoTipo() {
        return certidaoTipo;
    }

    public void setCertidaoTipo(CertidaoTipo certidaoTipo) {
        this.certidaoTipo = certidaoTipo;
    }

    public boolean isPeriodoConvencao() {
        return periodoConvencao;
    }

    public void setPeriodoConvencao(boolean periodoConvencao) {
        this.periodoConvencao = periodoConvencao;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
    
}
