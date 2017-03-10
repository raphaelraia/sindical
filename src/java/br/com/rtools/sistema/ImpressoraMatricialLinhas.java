package br.com.rtools.sistema;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "sis_impressora_matricial_linhas")
public class ImpressoraMatricialLinhas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_impressao", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private ImpressoraMatricial impressao;
    @Column(name = "nr_tamanho_fonte")
    private Integer tamanhoFonte;
    @Column(name = "ds_linha")
    private String linha;

    public ImpressoraMatricialLinhas() {
        this.id = null;
        this.impressao = null;
        this.tamanhoFonte = null;
        this.linha = "";
    }

    public ImpressoraMatricialLinhas(Integer id, ImpressoraMatricial impressao, Integer tamanhoFonte, String linha) {
        this.id = id;
        this.impressao = impressao;
        this.tamanhoFonte = tamanhoFonte;
        this.linha = linha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ImpressoraMatricial getImpressao() {
        return impressao;
    }

    public void setImpressao(ImpressoraMatricial impressao) {
        this.impressao = impressao;
    }

    public Integer getTamanhoFonte() {
        return tamanhoFonte;
    }

    public void setTamanhoFonte(Integer tamanhoFonte) {
        this.tamanhoFonte = tamanhoFonte;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

}
