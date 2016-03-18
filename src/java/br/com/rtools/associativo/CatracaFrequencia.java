package br.com.rtools.associativo;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "soc_catraca_frequencia")
public class CatracaFrequencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id")
    @ManyToOne
    private Departamento departamento;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id")
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_acesso", nullable = false)
    private Date dtAcesso;
    @Column(name = "ds_hora_acesso")
    private String horaAcesso;
    @Column(name = "ds_es")
    private String es;
    @JoinColumn(name = "id_sis_pessoa", referencedColumnName = "id")
    @ManyToOne
    private SisPessoa sisPessoa;

    public CatracaFrequencia() {
        this.id = -1;
        this.departamento = new Departamento();
        this.pessoa = new Pessoa();
        this.dtAcesso = DataHoje.dataHoje();
        this.horaAcesso = DataHoje.horaMinuto();
        this.es = "E";
        this.sisPessoa = new SisPessoa();
    }

    public CatracaFrequencia(Integer id, Departamento departamento, Pessoa pessoa, Date dtAcesso, String horaAcesso, String es, SisPessoa sisPessoa) {
        this.id = id;
        this.departamento = departamento;
        this.pessoa = pessoa;
        this.dtAcesso = dtAcesso;
        this.horaAcesso = horaAcesso;
        this.es = es;
        this.sisPessoa = sisPessoa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDtAcesso() {
        return dtAcesso;
    }

    public void setDtAcesso(Date dtAcesso) {
        this.dtAcesso = dtAcesso;
    }

    public String getAcesso() {
        return DataHoje.converteData(dtAcesso);
    }

    public void setAcesso(String acesso) {
        this.dtAcesso = DataHoje.converte(acesso);
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public SisPessoa getSisPessoa() {
        return sisPessoa;
    }

    public void setSisPessoa(SisPessoa sisPessoa) {
        this.sisPessoa = sisPessoa;
    }

    public String getHoraAcesso() {
        return horaAcesso;
    }

    public void setHoraAcesso(String horaAcesso) {
        this.horaAcesso = horaAcesso;
    }

}
