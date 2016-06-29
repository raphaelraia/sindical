package br.com.rtools.arrecadacao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "arr_certificado_arquivos",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"id_convencao_periodo", "id_pessoa", "ds_arquivo"})
        }
)
public class CertificadoArquivos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_convencao_periodo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private ConvencaoPeriodo convencaoPeriodo;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_upload")
    private Date dtUpload;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_download")
    private Date dtDownload;
    @Column(name = "ds_path", length = 500)
    private String path;
    @Column(name = "ds_arquivo", length = 500)
    private String arquivo;

    public CertificadoArquivos() {
        this.id = null;
        this.convencaoPeriodo = null;
        this.pessoa = null;
        this.dtUpload = null;
        this.dtDownload = null;
        this.path = "";
        this.arquivo = "";
    }

    public CertificadoArquivos(Integer id, ConvencaoPeriodo convencaoPeriodo, Pessoa pessoa, Date dtUpload, Date dtDownload, String path, String arquivo) {
        this.id = id;
        this.convencaoPeriodo = convencaoPeriodo;
        this.pessoa = pessoa;
        this.dtUpload = dtUpload;
        this.dtDownload = dtDownload;
        this.path = path;
        this.arquivo = arquivo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ConvencaoPeriodo getConvencaoPeriodo() {
        return convencaoPeriodo;
    }

    public void setConvencaoPeriodo(ConvencaoPeriodo convencaoPeriodo) {
        this.convencaoPeriodo = convencaoPeriodo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtUpload() {
        return dtUpload;
    }

    public void setDtUpload(Date dtUpload) {
        this.dtUpload = dtUpload;
    }

    public String getUpload() {
        return DataHoje.converteData(dtUpload);
    }

    public void setUpload(String upload) {
        this.dtUpload = DataHoje.converte(upload);
    }

    public Date getDtDownload() {
        return dtDownload;
    }

    public void setDtDownload(Date dtDownload) {
        this.dtDownload = dtDownload;
    }

    public String getDownload() {
        return DataHoje.converteData(dtDownload);
    }

    public void setDownload(String download) {
        this.dtDownload = DataHoje.converte(download);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getExtension() {
        if (this.id != null) {
            try {
                return arquivo.split("\\.")[1];
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public String getFileName() {
        if (this.id != null) {
            try {
                return id + "." + getExtension();
            } catch (Exception e) {
                return id.toString();
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "CertificadoArquivos{" + "id=" + id + ", convencaoPeriodo=" + convencaoPeriodo + ", pessoa=" + pessoa + ", dtUpload=" + dtUpload + ", dtDownload=" + dtDownload + ", path=" + path + ", arquivo=" + arquivo + '}';
    }

}
