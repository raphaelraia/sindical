package br.com.rtools.associativo;

import br.com.rtools.endereco.Endereco;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "car_caravana")
@NamedQueries({
    @NamedQuery(name = "Caravana.findAll", query = "SELECT C FROM Caravana AS C ORDER BY C.dtEmbarqueIda DESC, C.horaEmbarqueIda ASC")
})
public class Caravana implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_evento", referencedColumnName = "id")
    @ManyToOne
    private AEvento evento;
    @Column(name = "ds_titulo_complemento", length = 300)
    private String tituloComplemento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_embarque_ida", nullable = false)
    private Date dtEmbarqueIda;
    @Column(name = "tm_embarque_ida", nullable = false, length = 5)
    private String horaEmbarqueIda;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_embarque_retorno", nullable = false)
    private Date dtEmbarqueRetorno;
    @Column(name = "tm_embarque_retorno", nullable = false, length = 5)
    private String horaEmbarqueRetorno;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_estadia_inicio", nullable = false)
    private Date dtEstadiaInicio;
    @Column(name = "tm_estadia_inicio", nullable = false, length = 5)
    private String horaEstadiaInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_estadia_fim", nullable = false)
    private Date dtEstadiaFim;
    @Column(name = "tm_estadia_fim", nullable = false, length = 5)
    private String horaEstadiaFim;
    @Column(name = "tm_duracao_viagem", nullable = false, length = 5)
    private String duracaoViagem;
    @Column(name = "is_cafe", nullable = true)
    private Boolean cafe;
    @Column(name = "is_almoco", nullable = true)
    private Boolean almoco;
    @Column(name = "is_jantar", nullable = true)
    private Boolean jantar;
    @Column(name = "nr_poltronas", nullable = true)
    private Integer quantidadePoltronas;
    @Column(name = "nr_guia_recolhimento", nullable = true)
    private Integer guiaRecolhimento;
    @Column(name = "ds_observacao", length = 255)
    private String observacao;
    @JoinColumn(name = "id_evt", referencedColumnName = "id")
    @ManyToOne
    private Evt evt;
    @Column(name = "ds_relatorio", length = 5000)
    private String relatorio;
    @Column(name = "ds_local_embarque_ida", length = 300)
    private String localEmbarqueIda;
    @JoinColumn(name = "id_endereco_embarque_ida", referencedColumnName = "id")
    @ManyToOne
    private Endereco enderecoEmbarqueIda;
    @Column(name = "ds_numero")
    private String numero;
    @Column(name = "ds_complemento_embarque_ida", length = 300)
    private String complementoEmbarqueIda;

    public Caravana() {
        this.id = null;
        this.evento = null;
        this.tituloComplemento = "";
        this.dtEmbarqueIda = null;
        this.horaEmbarqueIda = "";
        this.dtEmbarqueRetorno = null;
        this.horaEmbarqueRetorno = "";
        this.dtEstadiaInicio = null;
        this.horaEstadiaInicio = "";
        this.dtEstadiaFim = null;
        this.horaEstadiaFim = "";
        this.duracaoViagem = "";
        this.cafe = false;
        this.almoco = false;
        this.jantar = false;
        this.quantidadePoltronas = 0;
        this.guiaRecolhimento = 0;
        this.observacao = "";
        this.evt = null;
        this.relatorio = "";
        this.localEmbarqueIda = "";
        this.enderecoEmbarqueIda = null;
        this.numero = "";
        this.complementoEmbarqueIda = "";
    }

    public Caravana(Integer id, AEvento evento, String tituloComplemento, Date dtEmbarqueIda, String horaEmbarqueIda, Date dtEmbarqueRetorno, String horaEmbarqueRetorno, Date dtEstadiaInicio, String horaEstadiaInicio, Date dtEstadiaFim, String horaEstadiaFim, String duracaoViagem, Boolean cafe, Boolean almoco, Boolean jantar, Integer quantidadePoltronas, Integer guiaRecolhimento, String observacao, Evt evt, String relatorio, String localEmbarqueIda, Endereco enderecoEmbarqueIda, String numero, String complementoEmbarqueIda) {
        this.id = id;
        this.evento = evento;
        this.tituloComplemento = tituloComplemento;
        this.dtEmbarqueIda = dtEmbarqueIda;
        this.horaEmbarqueIda = horaEmbarqueIda;
        this.dtEmbarqueRetorno = dtEmbarqueRetorno;
        this.horaEmbarqueRetorno = horaEmbarqueRetorno;
        this.dtEstadiaInicio = dtEstadiaInicio;
        this.horaEstadiaInicio = horaEstadiaInicio;
        this.dtEstadiaFim = dtEstadiaFim;
        this.horaEstadiaFim = horaEstadiaFim;
        this.duracaoViagem = duracaoViagem;
        this.cafe = cafe;
        this.almoco = almoco;
        this.jantar = jantar;
        this.quantidadePoltronas = quantidadePoltronas;
        this.guiaRecolhimento = guiaRecolhimento;
        this.observacao = observacao;
        this.evt = evt;
        this.relatorio = relatorio;
        this.localEmbarqueIda = localEmbarqueIda;
        this.enderecoEmbarqueIda = enderecoEmbarqueIda;
        this.numero = numero;
        this.complementoEmbarqueIda = complementoEmbarqueIda;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AEvento getEvento() {
        return evento;
    }

    public void setEvento(AEvento evento) {
        this.evento = evento;
    }

    public String getTituloComplemento() {
        return tituloComplemento;
    }

    public void setTituloComplemento(String tituloComplemento) {
        this.tituloComplemento = tituloComplemento;
    }

    public Date getDtEmbarqueIda() {
        return dtEmbarqueIda;
    }

    public void setDtEmbarqueIda(Date dtEmbarqueIda) {
        this.dtEmbarqueIda = dtEmbarqueIda;
    }

    public String getDataEmbarqueIda() {
        return DataHoje.converteData(dtEmbarqueIda);
    }

    public void setDataEmbarqueIda(String embarqueIda) {
        this.dtEmbarqueIda = DataHoje.converte(embarqueIda);
    }

    public String getHoraEmbarqueIda() {
        return horaEmbarqueIda;
    }

    public void setHoraEmbarqueIda(String horaEmbarqueIda) {
        this.horaEmbarqueIda = horaEmbarqueIda;
    }

    public Date getDtEmbarqueRetorno() {
        return dtEmbarqueRetorno;
    }

    public void setDtEmbarqueRetorno(Date dtEmbarqueRetorno) {
        this.dtEmbarqueRetorno = dtEmbarqueRetorno;
    }

    public String getDataEmbarqueRetorno() {
        return DataHoje.converteData(dtEmbarqueRetorno);
    }

    public void setDataEmbarqueRetorno(String embarqueRetorno) {
        this.dtEmbarqueRetorno = DataHoje.converte(embarqueRetorno);
    }

    public String getHoraEmbarqueRetorno() {
        return horaEmbarqueRetorno;
    }

    public void setHoraEmbarqueRetorno(String horaEmbarqueRetorno) {
        this.horaEmbarqueRetorno = horaEmbarqueRetorno;
    }

    public Date getDtEstadiaInicio() {
        return dtEstadiaInicio;
    }

    public void setDtEstadiaInicio(Date dtEstadiaInicio) {
        this.dtEstadiaInicio = dtEstadiaInicio;
    }

    public String getDataEstadiaInicio() {
        return DataHoje.converteData(dtEstadiaInicio);
    }

    public void setDataEstadiaInicio(String estadiaInicio) {
        this.dtEstadiaInicio = DataHoje.converte(estadiaInicio);
    }

    public String getHoraEstadiaInicio() {
        return horaEstadiaInicio;
    }

    public void setHoraEstadiaInicio(String horaEstadiaInicio) {
        this.horaEstadiaInicio = horaEstadiaInicio;
    }

    public Date getDtEstadiaFim() {
        return dtEstadiaFim;
    }

    public void setDtEstadiaFim(Date dtEstadiaFim) {
        this.dtEstadiaFim = dtEstadiaFim;
    }

    public String getDataEstadiaFim() {
        return DataHoje.converteData(dtEstadiaFim);
    }

    public void setDataEstadiaFim(String estadiaFim) {
        this.dtEstadiaFim = DataHoje.converte(estadiaFim);
    }

    public String getHoraEstadiaFim() {
        return horaEstadiaFim;
    }

    public void setHoraEstadiaFim(String horaEstadiaFim) {
        this.horaEstadiaFim = horaEstadiaFim;
    }

    public String getDuracaoViagem() {
        return duracaoViagem;
    }

    public void setDuracaoViagem(String duracaoViagem) {
        this.duracaoViagem = duracaoViagem;
    }

    public Boolean getCafe() {
        return cafe;
    }

    public void setCafe(Boolean cafe) {
        this.cafe = cafe;
    }

    public Boolean getAlmoco() {
        return almoco;
    }

    public void setAlmoco(Boolean almoco) {
        this.almoco = almoco;
    }

    public Boolean getJantar() {
        return jantar;
    }

    public void setJantar(Boolean jantar) {
        this.jantar = jantar;
    }

    public Integer getQuantidadePoltronas() {
        return quantidadePoltronas;
    }

    public void setQuantidadePoltronas(Integer quantidadePoltronas) {
        this.quantidadePoltronas = quantidadePoltronas;
    }

    public String getQuantidadePoltronasString() {
        try {
            return Integer.toString(quantidadePoltronas);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    public void setQuantidadePoltronasString(String quantidadePoltronasString) {
        try {
            this.quantidadePoltronas = Integer.parseInt(quantidadePoltronasString);
        } catch (NumberFormatException e) {

        }
    }

    public Integer getGuiaRecolhimento() {
        return guiaRecolhimento;
    }

    public void setGuiaRecolhimento(Integer guiaRecolhimento) {
        this.guiaRecolhimento = guiaRecolhimento;
    }

    public String getGuiaRecolhimentoString() {
        try {
            return Integer.toString(guiaRecolhimento);
        } catch (NumberFormatException e) {
            return "0";
        }
    }

    public void setGuiaRecolhimentoString(String guiaRecolhimentoString) {
        try {
            this.guiaRecolhimento = Integer.parseInt(guiaRecolhimentoString);
        } catch (NumberFormatException e) {

        }
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Evt getEvt() {
        return evt;
    }

    public void setEvt(Evt evt) {
        this.evt = evt;
    }

    public String getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    public String getLocalEmbarqueIda() {
        return localEmbarqueIda;
    }

    public void setLocalEmbarqueIda(String localEmbarqueIda) {
        this.localEmbarqueIda = localEmbarqueIda;
    }

    public Endereco getEnderecoEmbarqueIda() {
        return enderecoEmbarqueIda;
    }

    public void setEnderecoEmbarqueIda(Endereco enderecoEmbarqueIda) {
        numero = "";
        complementoEmbarqueIda = "";
        this.enderecoEmbarqueIda = enderecoEmbarqueIda;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplementoEmbarqueIda() {
        return complementoEmbarqueIda;
    }

    public void setComplementoEmbarqueIda(String complementoEmbarqueIda) {
        this.complementoEmbarqueIda = complementoEmbarqueIda;
    }

}
