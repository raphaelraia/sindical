package br.com.rtools.homologacao;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "hom_acrescentar_horario")
public class AcrescentarHorario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_horarios", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Horarios horarios;
    @JoinColumn(name = "id_filial", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Filial filial;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_data")
    private Date dtData;
    @Column(name = "nr_quantidade")
    private int quantidade;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario usuario;

    public AcrescentarHorario() {
        this.id = null;
        this.horarios = new Horarios();
        this.filial = new Filial();
        this.setData("");
        this.quantidade = 0;
        this.usuario = new Usuario();
    }

    public AcrescentarHorario(Integer idI, Horarios horarios1, Filial filial1, String dataString, Integer quantidadeI, Usuario usuario1) {
        this.id = idI;
        this.horarios = horarios1;
        this.filial = filial1;
        this.setData(dataString);
        this.quantidade = quantidadeI;
        this.usuario = usuario1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Horarios getHorarios() {
        return horarios;
    }

    public void setHorarios(Horarios horarios1) {
        this.horarios = horarios1;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getData() {
        if (dtData != null) {
            return DataHoje.converteData(dtData);
        } else {
            return "";
        }
    }

    public void setData(String data) {
        if (!(data.isEmpty())) {
            this.dtData = DataHoje.converte(data);
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
