package br.com.rtools.financeiro;

import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.pessoa.Administradora;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "fin_servicos")
@NamedQueries({
    @NamedQuery(name = "Servicos.pesquisaID", query = "SELECT S FROM Servicos AS S WHERE S.id = :pid")
    ,
    @NamedQuery(name = "Servicos.findAll", query = "SELECT S FROM Servicos AS S ORDER BY S.descricao ASC ")
})
public class Servicos implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 100, nullable = true)
    private String descricao;
    @JoinColumn(name = "id_filial", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Filial filial;
    @JoinColumn(name = "id_plano5", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "id_departamento", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Departamento departamento;
    @Column(name = "nr_validade_guia_dias", nullable = true)
    private int validade;
    @Column(name = "ds_codigo", length = 10, nullable = true)
    private String codigo;
    @Column(name = "ds_situacao", length = 1, nullable = true)
    private String situacao;
    @Column(name = "is_debito_clube", nullable = true, columnDefinition = "boolean default false")
    private boolean debitoClube;
    @Column(name = "is_altera_valor", nullable = true, columnDefinition = "boolean default false")
    private boolean alterarValor;
    @Column(name = "is_adm", nullable = true, columnDefinition = "boolean default false")
    private boolean adm;
    @Column(name = "is_tabela", nullable = true, columnDefinition = "boolean default false")
    private boolean tabela;
    @Column(name = "is_eleicao", nullable = true, columnDefinition = "boolean default false")
    private boolean eleicao;
    @Column(name = "is_agrupa_boleto", nullable = true, columnDefinition = "boolean default false")
    private boolean agrupaBoleto;
    @Column(name = "is_produto", nullable = true, columnDefinition = "boolean default false")
    private boolean produto;
    @JoinColumn(name = "id_subgrupo", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private SubGrupoFinanceiro subGrupoFinanceiro;
    @Column(name = "is_valor_fixo", nullable = true, columnDefinition = "boolean default false")
    private boolean valorFixo;
    @JoinColumn(name = "id_periodo", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Periodo periodo;
    @Column(name = "nr_qtde_periodo")
    private int quantidadePeriodo;
    @Column(name = "is_familiar_periodo", nullable = false, columnDefinition = "boolean default false")
    private boolean familiarPeriodo;
    @Column(name = "is_valor_zerado", nullable = false, columnDefinition = "boolean default false")
    private boolean valorZerado;
    @Column(name = "is_validade_guias_vigente", nullable = false, columnDefinition = "boolean default false")
    private boolean validadeGuiasVigente;
    @JoinColumn(name = "id_administradora", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Administradora administradora;
    @Column(name = "is_validade_guias", nullable = false, columnDefinition = "boolean default false")
    private boolean validadeGuias;
    @Column(name = "is_curso_renovacao", nullable = false, columnDefinition = "boolean default false")
    private boolean cursoRenovacao;
    @Column(name = "is_boleto", nullable = true, columnDefinition = "boolean default false")
    private boolean boleto;
    @JoinColumn(name = "id_modelo_cartao", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private ModeloCarteirinha modeloCarteirinha;
    @Column(name = "nr_meses_debito_exclusao")
    private Integer mesesDebitoExclusao;
    @Column(name = "is_meses_debito_exclusao_todos", nullable = true, columnDefinition = "boolean default false")
    private boolean mesesDebitoExclusaoTodos;
    @Column(name = "is_guia_somente_socio", nullable = true, columnDefinition = "boolean default false")
    private Boolean guiaSomenteSocio;
    @Column(name = "nr_vagas", nullable = false, columnDefinition = "integer default 0")
    private Integer nrVagas;
    @Column(name = "is_debito_homologacao", nullable = false, columnDefinition = "boolean default true")
    private boolean debitoHomologacao;

    @Transient
    private Boolean selected;

    @Transient
    private Double valorCheio;

    public Servicos() {
        this.id = -1;
        this.descricao = "";
        this.filial = new Filial();
        this.plano5 = new Plano5();
        this.departamento = new Departamento();
        this.validade = 0;
        this.codigo = "";
        this.situacao = "A";
        this.debitoClube = false;
        this.alterarValor = false;
        this.adm = false;
        this.tabela = false;
        this.eleicao = false;
        this.agrupaBoleto = false;
        this.subGrupoFinanceiro = new SubGrupoFinanceiro();
        this.produto = false;
        this.valorFixo = false;
        this.periodo = new Periodo();
        this.quantidadePeriodo = 0;
        this.familiarPeriodo = false;
        this.valorZerado = false;
        this.validadeGuiasVigente = false;
        this.administradora = null;
        this.validadeGuias = false;
        this.cursoRenovacao = false;
        this.boleto = false;
        this.modeloCarteirinha = null;
        this.mesesDebitoExclusao = 60;
        this.selected = false;
        this.mesesDebitoExclusaoTodos = false;
        this.guiaSomenteSocio = false;
        this.nrVagas = 0;
        // TRANSIENT
        this.valorCheio = null;
        this.debitoHomologacao = true;
    }

    public Servicos(int id,
            String descricao,
            Filial filial,
            Plano5 plano5,
            Departamento departamento,
            int validade,
            String codigo,
            String situacao,
            boolean debito,
            boolean alterarValor,
            boolean adm,
            boolean tabela,
            boolean eleicao,
            boolean agrupaBoleto,
            SubGrupoFinanceiro subGrupoFinanceiro,
            boolean produto,
            boolean valorFixo,
            Periodo periodo,
            int quantidadePeriodo,
            boolean familiarPeriodo,
            boolean valorZerado,
            boolean validadeGuiasVigente,
            Administradora administradora,
            boolean validadeGuias,
            boolean cursoRenovacao,
            boolean boleto,
            ModeloCarteirinha modeloCarteirinha,
            Integer mesesDebitoExclusao,
            boolean mesesDebitoExclusaoTodos,
            boolean guiaSomenteSocio,
            Integer nrVagas,
            boolean debitoHomologacao) {
        this.id = id;
        this.descricao = descricao;
        this.filial = filial;
        this.plano5 = plano5;
        this.departamento = departamento;
        this.validade = validade;
        this.codigo = codigo;
        this.situacao = situacao;
        this.debitoClube = debitoClube;
        this.alterarValor = alterarValor;
        this.adm = adm;
        this.tabela = tabela;
        this.eleicao = eleicao;
        this.agrupaBoleto = agrupaBoleto;
        this.subGrupoFinanceiro = subGrupoFinanceiro;
        this.produto = produto;
        this.valorFixo = valorFixo;
        this.periodo = periodo;
        this.quantidadePeriodo = quantidadePeriodo;
        this.familiarPeriodo = familiarPeriodo;
        this.valorZerado = valorZerado;
        this.validadeGuiasVigente = validadeGuiasVigente;
        this.administradora = administradora;
        this.validadeGuias = validadeGuias;
        this.cursoRenovacao = cursoRenovacao;
        this.boleto = boleto;
        this.modeloCarteirinha = modeloCarteirinha;
        this.mesesDebitoExclusao = mesesDebitoExclusao;
        this.mesesDebitoExclusaoTodos = mesesDebitoExclusaoTodos;
        this.guiaSomenteSocio = guiaSomenteSocio;
        this.nrVagas = nrVagas;
        this.debitoHomologacao = debitoHomologacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public int getValidade() {
        return validade;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public boolean isDebitoClube() {
        return debitoClube;
    }

    public void setDebitoClube(boolean debitoClube) {
        this.debitoClube = debitoClube;
    }

    public boolean isAlterarValor() {
        return alterarValor;
    }

    public void setAlterarValor(boolean alterarValor) {
        this.alterarValor = alterarValor;
    }

    public boolean isAdm() {
        return adm;
    }

    public void setAdm(boolean adm) {
        this.adm = adm;
    }

    public boolean isTabela() {
        return tabela;
    }

    public void setTabela(boolean tabela) {
        this.tabela = tabela;
    }

    public boolean isEleicao() {
        return eleicao;
    }

    public void setEleicao(boolean eleicao) {
        this.eleicao = eleicao;
    }

    public boolean isAgrupaBoleto() {
        return agrupaBoleto;
    }

    public void setAgrupaBoleto(boolean agrupaBoleto) {
        this.agrupaBoleto = agrupaBoleto;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public SubGrupoFinanceiro getSubGrupoFinanceiro() {
        return subGrupoFinanceiro;
    }

    public void setSubGrupoFinanceiro(SubGrupoFinanceiro subGrupoFinanceiro) {
        this.subGrupoFinanceiro = subGrupoFinanceiro;
    }

    public boolean isProduto() {
        return produto;
    }

    public void setProduto(boolean produto) {
        this.produto = produto;
    }

    public boolean isValorFixo() {
        return valorFixo;
    }

    public void setValorFixo(boolean valorFixo) {
        this.valorFixo = valorFixo;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public int getQuantidadePeriodo() {
        return quantidadePeriodo;
    }

    public void setQuantidadePeriodo(int quantidadePeriodo) {
        this.quantidadePeriodo = quantidadePeriodo;
    }

    public boolean isFamiliarPeriodo() {
        return familiarPeriodo;
    }

    public void setFamiliarPeriodo(boolean familiarPeriodo) {
        this.familiarPeriodo = familiarPeriodo;
    }

    public boolean isValorZerado() {
        return valorZerado;
    }

    public void setValorZerado(boolean valorZerado) {
        this.valorZerado = valorZerado;
    }

    public boolean isValidadeGuiasVigente() {
        return validadeGuiasVigente;
    }

    public void setValidadeGuiasVigente(boolean validadeGuiasVigente) {
        this.validadeGuiasVigente = validadeGuiasVigente;
    }

    public Administradora getAdministradora() {
        return administradora;
    }

    public void setAdministradora(Administradora administradora) {
        this.administradora = administradora;
    }

    public boolean isValidadeGuias() {
        return validadeGuias;
    }

    public void setValidadeGuias(boolean validadeGuias) {
        this.validadeGuias = validadeGuias;
    }

    public boolean isCursoRenovacao() {
        return cursoRenovacao;
    }

    public void setCursoRenovacao(boolean cursoRenovacao) {
        this.cursoRenovacao = cursoRenovacao;
    }

    public boolean isBoleto() {
        return boleto;
    }

    public void setBoleto(boolean boleto) {
        this.boleto = boleto;
    }

    public ModeloCarteirinha getModeloCarteirinha() {
        return modeloCarteirinha;
    }

    public void setModeloCarteirinha(ModeloCarteirinha modeloCarteirinha) {
        this.modeloCarteirinha = modeloCarteirinha;
    }

    public Integer getMesesDebitoExclusao() {
        return mesesDebitoExclusao;
    }

    public void setMesesDebitoExclusao(Integer mesesDebitoExclusao) {
        this.mesesDebitoExclusao = mesesDebitoExclusao;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Servicos other = (Servicos) obj;
        return true;
    }

    @Override
    public String toString() {
        return "Servicos{" + "id=" + id + ", descricao=" + descricao + ", filial=" + filial + ", plano5=" + plano5 + ", departamento=" + departamento + ", validade=" + validade + ", codigo=" + codigo + ", situacao=" + situacao + ", debitoClube=" + debitoClube + ", alterarValor=" + alterarValor + ", adm=" + adm + ", tabela=" + tabela + ", eleicao=" + eleicao + ", agrupaBoleto=" + agrupaBoleto + ", produto=" + produto + ", subGrupoFinanceiro=" + subGrupoFinanceiro + ", valorFixo=" + valorFixo + ", periodo=" + periodo + ", quantidadePeriodo=" + quantidadePeriodo + ", familiarPeriodo=" + familiarPeriodo + ", valorZerado=" + valorZerado + ", validadeGuiasVigente=" + validadeGuiasVigente + ", administradora=" + administradora + ", validadeGuias=" + validadeGuias + ", cursoRenovacao=" + cursoRenovacao + ", boleto=" + boleto + ", modeloCarteirinha=" + modeloCarteirinha + '}';
    }

    public boolean isMesesDebitoExclusaoTodos() {
        return mesesDebitoExclusaoTodos;
    }

    public void setMesesDebitoExclusaoTodos(boolean mesesDebitoExclusaoTodos) {
        this.mesesDebitoExclusaoTodos = mesesDebitoExclusaoTodos;
    }

    /**
     * Por pessoa
     *
     * @param pessoa_id
     * @return
     */
    public Double getValorCheio(Integer pessoa_id) {
        try {
            return new FunctionsDao().valorServicoCheio(pessoa_id, this.id, new Date());
        } catch (Exception e) {
            return new Double(0);
        }
    }

    /**
     * Por pessoa
     *
     * @param pessoa_id
     * @return
     */
    public String getValorCheioString(Integer pessoa_id) {
        try {
            return Moeda.converteR$Double(new FunctionsDao().valorServicoCheio(pessoa_id, this.id, new Date()));
        } catch (Exception e) {
            return "0,00";
        }
    }

    /**
     * Valor do serviço com ID 1
     *
     * @return
     */
    public Double getValorCheio() {
        if (valorCheio == null) {
            try {
                valorCheio = new FunctionsDao().valorServicoCheio(1, this.id, new Date());
                return valorCheio;
            } catch (Exception e) {
                return new Double(0);
            }
        }
        return new Double(0);
    }

    public String getValorCheioString() {
        return Moeda.converteR$Double(getValorCheio());
    }

    public void setValorCheioString(String valorCheioString) {
        this.valorCheio = Moeda.converteUS$(valorCheioString);
    }

    public void setValorCheio(Double valorCheio) {
        this.valorCheio = valorCheio;
    }

    public Boolean getGuiaSomenteSocio() {
        return guiaSomenteSocio;
    }

    public void setGuiaSomenteSocio(Boolean guiaSomenteSocio) {
        this.guiaSomenteSocio = guiaSomenteSocio;
    }

    public Integer getNrVagas() {
        return nrVagas;
    }

    public void setNrVagas(Integer nrVagas) {
        this.nrVagas = nrVagas;
    }

    public boolean isDebitoHomologacao() {
        return debitoHomologacao;
    }

    public void setDebitoHomologacao(boolean debitoHomologacao) {
        this.debitoHomologacao = debitoHomologacao;
    }

}
