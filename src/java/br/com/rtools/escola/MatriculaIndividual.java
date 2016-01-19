package br.com.rtools.escola;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.DataHoje;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.*;
import org.primefaces.event.SelectEvent;

@Entity
@Table(name = "esc_matr_individual")
@NamedQuery(name = "MatriculaIndividual.pesquisaID", query = "select m from MatriculaIndividual m where m.id=:pid")
public class MatriculaIndividual implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_matr_escola", referencedColumnName = "id")
    @ManyToOne
    private MatriculaEscola matriculaEscola;
    @JoinColumn(name = "id_curso", referencedColumnName = "id")
    @ManyToOne
    private Servicos curso;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inicio")
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_termino")
    private Date dataTermino;
    @Column(name = "tm_inicio", length = 5)
    private String inicio;
    @Column(name = "tm_termino", length = 5)
    private String termino;
    @JoinColumn(name = "id_professor", referencedColumnName = "id")
    @ManyToOne
    private Professor professor;
    @Column(name = "is_seg", columnDefinition = "boolean default false")
    private Boolean segunda;
    @Column(name = "is_ter", columnDefinition = "boolean default false")
    private Boolean terca;
    @Column(name = "is_qua", columnDefinition = "boolean default false")
    private Boolean quarta;
    @Column(name = "is_qui", columnDefinition = "boolean default false")
    private Boolean quinta;
    @Column(name = "is_sex", columnDefinition = "boolean default false")
    private Boolean sexta;
    @Column(name = "is_sab", columnDefinition = "boolean default false")
    private Boolean sabado;
    @Column(name = "is_dom", columnDefinition = "boolean default false")
    private Boolean domingo;

    public MatriculaIndividual() {
        id = -1;
        matriculaEscola = new MatriculaEscola();
        curso = new Servicos();
        dataInicio = DataHoje.dataHoje();
        dataTermino = null;
        inicio = "";
        termino = "";
        professor = new Professor();
        segunda = false;
        terca = false;
        quarta = false;
        quinta = false;
        sexta = false;
        sabado = false;
        domingo = false;
    }

    public MatriculaIndividual(Integer id, MatriculaEscola matriculaEscola, Servicos curso, Date dataInicio, Date dataTermino, String inicio, String termino, Professor professor, Boolean segunda, Boolean terca, Boolean quarta, Boolean quinta, Boolean sexta, Boolean sabado, Boolean domingo) {
        this.id = id;
        this.matriculaEscola = matriculaEscola;
        this.curso = curso;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.inicio = inicio;
        this.termino = termino;
        this.professor = professor;
        this.segunda = segunda;
        this.terca = terca;
        this.quarta = quarta;
        this.quinta = quinta;
        this.sexta = sexta;
        this.sabado = sabado;
        this.domingo = domingo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MatriculaEscola getMatriculaEscola() {
        return matriculaEscola;
    }

    public void setMatriculaEscola(MatriculaEscola matriculaEscola) {
        this.matriculaEscola = matriculaEscola;
    }

    public Servicos getCurso() {
        return curso;
    }

    public void setCurso(Servicos curso) {
        this.curso = curso;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(Date dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public void setDataInicioString(String dataInicio) {
        this.dataInicio = DataHoje.converte(dataInicio);
    }

    public String getDataInicioString() {
        return DataHoje.converteData(dataInicio);
    }

    public void setDataTerminoString(String dataTermino) {
        this.dataTermino = DataHoje.converte(dataTermino);
    }

    public String getDataTerminoString() {
        return DataHoje.converteData(dataTermino);
    }

    public void listenerInicio(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataInicio = DataHoje.converte(format.format(event.getObject()));
    }

    public void listenerTermino(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataTermino = DataHoje.converte(format.format(event.getObject()));
    }

    public Boolean getSegunda() {
        return segunda;
    }

    public void setSegunda(Boolean segunda) {
        this.segunda = segunda;
    }

    public Boolean getTerca() {
        return terca;
    }

    public void setTerca(Boolean terca) {
        this.terca = terca;
    }

    public Boolean getQuarta() {
        return quarta;
    }

    public void setQuarta(Boolean quarta) {
        this.quarta = quarta;
    }

    public Boolean getQuinta() {
        return quinta;
    }

    public void setQuinta(Boolean quinta) {
        this.quinta = quinta;
    }

    public Boolean getSexta() {
        return sexta;
    }

    public void setSexta(Boolean sexta) {
        this.sexta = sexta;
    }

    public Boolean getSabado() {
        return sabado;
    }

    public void setSabado(Boolean sabado) {
        this.sabado = sabado;
    }

    public Boolean getDomingo() {
        return domingo;
    }

    public void setDomingo(Boolean domingo) {
        this.domingo = domingo;
    }
}
