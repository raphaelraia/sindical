package br.com.rtools.homologacao;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "hom_demissao")
@NamedQueries({
    @NamedQuery(name = "Demissao.pesquisaID", query = "SELECT D FROM Demissao AS D WHERE D.id = :pid"),
    @NamedQuery(name = "Demissao.findAll", query = "SELECT D FROM Demissao AS D ORDER BY D.descricao ASC "),
    @NamedQuery(name = "Demissao.findName", query = "SELECT D FROM Demissao AS D WHERE UPPER(D.descricao) LIKE :pdescricao ORDER BY D.descricao ASC ")
})
public class Demissao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", length = 50, nullable = false)
    private String descricao;
    @Column(name = "is_motivo_web", columnDefinition = "boolean default false")
    private Boolean motivoWeb;
    @Column(name = "ds_mensagem_motivo_web", length = 1000, nullable = false)
    private String mensagemMotivoWeb;

    public Demissao() {
        this.id = null;
        this.descricao = "";
        this.motivoWeb = false;
        this.mensagemMotivoWeb = "";
    }

    public Demissao(Integer id, String descricao, Boolean motivoWeb, String mensagemMotivoWeb) {
        this.id = id;
        this.descricao = descricao;
        this.motivoWeb = motivoWeb;
        this.mensagemMotivoWeb = mensagemMotivoWeb;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getMotivoWeb() {
        return motivoWeb;
    }

    public void setMotivoWeb(Boolean motivoWeb) {
        this.motivoWeb = motivoWeb;
    }

    public String getMensagemMotivoWeb() {
        return mensagemMotivoWeb;
    }

    public void setMensagemMotivoWeb(String mensagemMotivoWeb) {
        this.mensagemMotivoWeb = mensagemMotivoWeb;
    }
}
