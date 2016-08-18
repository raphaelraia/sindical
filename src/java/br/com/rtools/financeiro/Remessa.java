package br.com.rtools.financeiro;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_remessa")
public class Remessa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_nome_arquivo")
    private String nomeArquivo;
    @Column(name = "dt_emissao")
    @Temporal(TemporalType.DATE)
    private Date dtEmissao;
    @Column(name = "ds_hora_emissao")
    private String horaEmissao;

    public Remessa() {
        this.id = -1;
        this.nomeArquivo = "";
        this.dtEmissao = null;
        this.horaEmissao = "";
    }

    public Remessa(int id, String nomeArquivo, Date dtEmissao, String horaEmissao) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.dtEmissao = dtEmissao;
        this.horaEmissao = horaEmissao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getHoraEmissao() {
        return horaEmissao;
    }

    public void setHoraEmissao(String horaEmissao) {
        this.horaEmissao = horaEmissao;
    }

}
