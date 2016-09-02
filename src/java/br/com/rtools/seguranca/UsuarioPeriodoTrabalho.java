package br.com.rtools.seguranca;

import br.com.rtools.financeiro.PeriodoTrabalho;
import br.com.rtools.sistema.Semana;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "seg_usuario_periodo_trabalho",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_usuario", "id_semana"})
)
public class UsuarioPeriodoTrabalho implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @JoinColumn(name = "id_semana", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Semana semana;
    @JoinColumn(name = "id_periodo_trabalho", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private PeriodoTrabalho periodoTrabalho;
    @Column(name = "ds_hora_inicial_1")
    private String horaInicial1;
    @Column(name = "ds_hora_final_1")
    private String horaFinal1;
    @Column(name = "ds_hora_inicial_2")
    private String horaInicial2;
    @Column(name = "ds_hora_final_2")
    private String horaFinal2;
    @Column(name = "nr_tolerancia", columnDefinition = "integer default 0", nullable = false)
    private Integer nrTolerancia;
    @Column(name = "is_ativo", columnDefinition = "boolean default false", nullable = false)
    private Boolean ativo;

    public UsuarioPeriodoTrabalho() {
        this.id = null;
        this.usuario = null;
        this.semana = null;
        this.periodoTrabalho = null;
        this.horaInicial1 = "";
        this.horaFinal1 = "";
        this.horaInicial2 = "";
        this.horaFinal2 = "";
        this.nrTolerancia = 0;
        this.ativo = false;
    }

    public UsuarioPeriodoTrabalho(Integer id, Usuario usuario, Semana semana, PeriodoTrabalho periodoTrabalho, String horaInicial1, String horaFinal1, String horaInicial2, String horaFinal2, Integer nrTolerancia, Boolean ativo) {
        this.id = id;
        this.usuario = usuario;
        this.semana = semana;
        this.periodoTrabalho = periodoTrabalho;
        this.horaInicial1 = horaInicial1;
        this.horaFinal1 = horaFinal1;
        this.horaInicial2 = horaInicial2;
        this.horaFinal2 = horaFinal2;
        this.nrTolerancia = nrTolerancia;
        this.ativo = ativo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Semana getSemana() {
        return semana;
    }

    public void setSemana(Semana semana) {
        this.semana = semana;
    }

    public PeriodoTrabalho getPeriodoTrabalho() {
        return periodoTrabalho;
    }

    public void setPeriodoTrabalho(PeriodoTrabalho periodoTrabalho) {
        this.periodoTrabalho = periodoTrabalho;
    }

    public String getHoraInicial1() {
        return horaInicial1;
    }

    public void setHoraInicial1(String horaInicial1) {
        this.horaInicial1 = horaInicial1;
    }

    public String getHoraFinal1() {
        return horaFinal1;
    }

    public void setHoraFinal1(String horaFinal1) {
        this.horaFinal1 = horaFinal1;
    }

    public String getHoraInicial2() {
        return horaInicial2;
    }

    public void setHoraInicial2(String horaInicial2) {
        this.horaInicial2 = horaInicial2;
    }

    public String getHoraFinal2() {
        return horaFinal2;
    }

    public void setHoraFinal2(String horaFinal2) {
        this.horaFinal2 = horaFinal2;
    }

    public Integer getNrTolerancia() {
        return nrTolerancia;
    }

    public void setNrTolerancia(Integer nrTolerancia) {
        this.nrTolerancia = nrTolerancia;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}
