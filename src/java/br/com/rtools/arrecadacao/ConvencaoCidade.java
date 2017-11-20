package br.com.rtools.arrecadacao;

import br.com.rtools.pessoa.Pessoa;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "arr_convencao_cidade")
@NamedQueries({
    @NamedQuery(name = "ConvencaoCidade.findAll", query = "SELECT C FROM ConvencaoCidade AS C ORDER BY C.id"),
    @NamedQuery(name = "ConvencaoCidade.pesquisaID", query = "SELECT C FROM ConvencaoCidade AS C WHERE C.id=:pid")
})
public class ConvencaoCidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_grupo_cidade", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private GrupoCidade grupoCidade;
    @JoinColumn(name = "id_convencao", referencedColumnName = "id", nullable = false)
    @OneToOne
    private Convencao convencao;
    @Column(name = "ds_caminho", length = 100)
    private String caminho;
    @JoinColumn(name = "id_sindicato", referencedColumnName = "id")
    @OneToOne
    private Pessoa sindicato;
    
    public ConvencaoCidade() {
        this.id = -1;
        this.grupoCidade = new GrupoCidade();
        this.convencao = new Convencao();
        this.caminho = "";
        this.sindicato = null;
    }

    public ConvencaoCidade(int id, GrupoCidade grupoCidade, Convencao convencao, String caminho, Pessoa sindicato) {
        this.id = id;
        this.grupoCidade = grupoCidade;
        this.convencao = convencao;
        this.caminho = caminho;
        this.sindicato = sindicato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GrupoCidade getGrupoCidade() {
        return grupoCidade;
    }

    public void setGrupoCidade(GrupoCidade grupoCidade) {
        this.grupoCidade = grupoCidade;
    }

    public Convencao getConvencao() {
        return convencao;
    }

    public void setConvencao(Convencao convencao) {
        this.convencao = convencao;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public Pessoa getSindicato() {
        return sindicato;
    }

    public void setSindicato(Pessoa sindicato) {
        this.sindicato = sindicato;
    }
}
