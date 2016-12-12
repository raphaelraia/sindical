package br.com.rtools.financeiro;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
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
    @Column(name = "dt_envio_banco")
    @Temporal(TemporalType.DATE)
    private Date dtEnvioBanco;
    @JoinColumn(name = "id_usuario_emissao", referencedColumnName = "id")
    @ManyToOne
    private Usuario usuarioEmissao;
    @JoinColumn(name = "id_usuario_envio_banco", referencedColumnName = "id")
    @ManyToOne
    private Usuario usuarioEnvioBanco;

    public Remessa() {
        this.id = -1;
        this.nomeArquivo = "";
        this.dtEmissao = null;
        this.horaEmissao = "";
        this.dtEnvioBanco = null;
        this.usuarioEmissao = null;
        this.usuarioEnvioBanco = null;
    }

    public Remessa(int id, String nomeArquivo, Date dtEmissao, String horaEmissao, Date dtEnvioBanco, Usuario usuarioEmissao, Usuario usuarioEnvioBanco) {
        this.id = id;
        this.nomeArquivo = nomeArquivo;
        this.dtEmissao = dtEmissao;
        this.horaEmissao = horaEmissao;
        this.dtEnvioBanco = dtEnvioBanco;
        this.usuarioEmissao = usuarioEmissao;
        this.usuarioEnvioBanco = usuarioEnvioBanco;
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
    
    public String getDtEmissaoString() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setDtEmissaoString(String dtEmissaoString) {
        this.dtEmissao = DataHoje.converte(dtEmissaoString);
    }

    public String getHoraEmissao() {
        return horaEmissao;
    }

    public void setHoraEmissao(String horaEmissao) {
        this.horaEmissao = horaEmissao;
    }

    public Date getDtEnvioBanco() {
        return dtEnvioBanco;
    }

    public void setDtEnvioBanco(Date dtEnvioBanco) {
        this.dtEnvioBanco = dtEnvioBanco;
    }
    
    public String getDtEnvioBancoString() {
        return DataHoje.converteData(dtEnvioBanco);
    }

    public void setDtEnvioBancoString(String dtEnvioBancoString) {
        this.dtEnvioBanco = DataHoje.converte(dtEnvioBancoString);
    }

    public Usuario getUsuarioEmissao() {
        return usuarioEmissao;
    }

    public void setUsuarioEmissao(Usuario usuarioEmissao) {
        this.usuarioEmissao = usuarioEmissao;
    }

    public Usuario getUsuarioEnvioBanco() {
        return usuarioEnvioBanco;
    }

    public void setUsuarioEnvioBanco(Usuario usuarioEnvioBanco) {
        this.usuarioEnvioBanco = usuarioEnvioBanco;
    }

}
