package br.com.rtools.financeiro;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_servico_valor_historico")
public class ServicoValorHistorico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Servicos servico;
    @JoinColumn(name = "id_servico_valor", referencedColumnName = "id")
    @ManyToOne
    private ServicoValor servicoValor;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Usuario usuario;
    @Column(name = "nr_idade_ini", length = 18, nullable = false, columnDefinition = "integer default 0")
    private Integer idadeIni;
    @Column(name = "nr_idade_fim", length = 18, nullable = false, columnDefinition = "integer default 500")
    private Integer idadeFim;
    @Column(name = "nr_valor", length = 18, nullable = false, columnDefinition = "double precision default 0")
    private Float valor;
    @Column(name = "nr_desconto_ate_vencimento", length = 18, nullable = false, columnDefinition = "double precision default 0")
    private Float descontoAteVenc;
    @Column(name = "nr_taxa", length = 18, nullable = true, columnDefinition = "double precision default 0")
    private Float taxa;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_data")
    private Date dtData;

    public ServicoValorHistorico() {
        this.id = null;
        this.servico = null;
        this.servicoValor = null;
        this.usuario = null;
        this.idadeIni = null;
        this.idadeFim = null;
        this.valor = new Float(0);
        this.descontoAteVenc = new Float(0);
        this.taxa = new Float(0);
        this.dtData = null;
    }

    public ServicoValorHistorico(Integer id, Servicos servico, ServicoValor servicoValor, Usuario usuario, Integer idadeIni, Integer idadeFim, Float valor, Float descontoAteVenc, Float taxa, Date dtData) {
        this.id = id;
        this.servico = servico;
        this.servicoValor = servicoValor;
        this.usuario = usuario;
        this.idadeIni = idadeIni;
        this.idadeFim = idadeFim;
        this.valor = valor;
        this.descontoAteVenc = descontoAteVenc;
        this.taxa = taxa;
        this.dtData = dtData;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServico() {
        return servico;
    }

    public void setServico(Servicos servico) {
        this.servico = servico;
    }

    public ServicoValor getServicoValor() {
        return servicoValor;
    }

    public void setServicoValor(ServicoValor servicoValor) {
        this.servicoValor = servicoValor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getIdadeIni() {
        return idadeIni;
    }

    public void setIdadeIni(Integer idadeIni) {
        this.idadeIni = idadeIni;
    }

    public Integer getIdadeFim() {
        return idadeFim;
    }

    public void setIdadeFim(Integer idadeFim) {
        this.idadeFim = idadeFim;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public Float getDescontoAteVenc() {
        return descontoAteVenc;
    }

    public void setDescontoAteVenc(Float descontoAteVenc) {
        this.descontoAteVenc = descontoAteVenc;
    }

    public Float getTaxa() {
        return taxa;
    }

    public void setTaxa(Float taxa) {
        this.taxa = taxa;
    }

    public Date getDtData() {
        return dtData;
    }

    public void setDtData(Date dtData) {
        this.dtData = dtData;
    }

    public String getData() {
        return DataHoje.converteData(dtData);
    }

    public void setData(String data) {
        this.dtData = DataHoje.converte(data);
    }

    public String getHora() {
        return DataHoje.converteHora(dtData);
    }

}
