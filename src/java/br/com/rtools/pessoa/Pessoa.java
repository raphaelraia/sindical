package br.com.rtools.pessoa;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import javax.servlet.ServletContext;
import org.primefaces.event.SelectEvent;

@Entity
@Table(name = "pes_pessoa")
@NamedQuery(name = "Pessoa.pesquisaID", query = "select pes from Pessoa pes where pes.id=:pid")
public class Pessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_nome", length = 150, nullable = false)
    private String nome;
    @JoinColumn(name = "id_tipo_documento", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private TipoDocumento tipoDocumento;
    @Column(name = "ds_obs", length = 1000, nullable = true)
    private String obs;
    @Column(name = "ds_site", length = 50, nullable = true)
    private String site;
    @Column(name = "ds_telefone1", length = 20, nullable = true)
    private String telefone1;
    @Column(name = "ds_telefone2", length = 20)
    private String telefone2;
    @Column(name = "ds_telefone3", length = 20)
    private String telefone3;
    @Column(name = "ds_telefone4", length = 20)
    private String telefone4;
    @Column(name = "ds_email1", length = 50, nullable = true)
    private String email1;
    @Column(name = "ds_email2", length = 50)
    private String email2;
    @Column(name = "ds_email3", length = 50)
    private String email3;
    @Column(name = "ds_documento", length = 30, nullable = false)
    private String documento;
    @Column(name = "ds_login", length = 50, nullable = true)
    private String login;
    @Column(name = "ds_senha", length = 50, nullable = true)
    private String senha;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_criacao")
    private Date dtCriacao;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_atualizacao")
    private Date dtAtualizacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_recadastro")
    private Date dtRecadastro;

    @Transient
    private Boolean isTitular;

    @Transient
    private Boolean oposicao;

    public Pessoa() {
        this.id = -1;
        this.nome = "";
        this.tipoDocumento = new TipoDocumento();
        this.obs = "";
        this.site = "";
        setCriacao(DataHoje.data());
        this.telefone1 = "";
        this.telefone2 = "";
        this.telefone3 = "";
        this.telefone4 = "";
        this.email1 = "";
        this.email2 = "";
        this.email3 = "";
        this.documento = "";
        this.login = "";
        this.senha = "";
        this.dtAtualizacao = null;
        this.dtRecadastro = DataHoje.dataHoje();
        this.oposicao = null;
    }

    public Pessoa(int id, String nome, TipoDocumento tipoDocumento, String obs, String site, String criacao,
            String telefone1, String telefone2, String telefone3, String telefone4, String email1, String email2, String email3, String documento, String login, String senha, Date dtRecadastro) {
        this.id = id;
        this.nome = nome;
        this.tipoDocumento = tipoDocumento;
        this.obs = obs;
        this.site = site;
        setCriacao(criacao);
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.telefone3 = telefone3;
        this.telefone4 = telefone4;
        this.email1 = email1;
        this.email2 = email2;
        this.email3 = email3;
        this.documento = documento;
        this.login = login;
        this.senha = senha;
        this.dtAtualizacao = null;
        this.dtRecadastro = dtRecadastro;
        this.oposicao = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome != null && !nome.isEmpty()) {
            nome = nome.toUpperCase();
            nome = nome.replaceAll("\\s+", " ");
            nome = nome.trim();
        }
        this.nome = nome;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTelefone1() {
        return telefone1;
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getTelefone3() {
        return telefone3;
    }

    public void setTelefone3(String telefone3) {
        this.telefone3 = telefone3;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getEmail3() {
        return email3;
    }

    public void setEmail3(String email3) {
        this.email3 = email3;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getDocumentoSomentoNumeros() {
        if (!documento.isEmpty()) {
            return AnaliseString.onlyNumbers(documento);
        }
        return "";
    }

    public Date getDtCriacao() {
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public String getCriacao() {
        return DataHoje.converteData(dtCriacao);
    }

    public void setCriacao(String criacao) {
        this.dtCriacao = DataHoje.converte(criacao);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Date getDtAtualizacao() {
        return dtAtualizacao;
    }

    public void setDtAtualizacao(Date dtAtualizacao) {
        this.dtAtualizacao = dtAtualizacao;
    }

    public String getAtualizacao() {
        if (dtAtualizacao != null) {
            return DataHoje.livre(dtAtualizacao, "dd/MM/yyyy") + " às " + DataHoje.livre(dtAtualizacao, "HH:mm") + " hr(s)";
        }
        return null;
    }

    public void setAtualizacao(String dtAtualizacao) {
        this.dtAtualizacao = DataHoje.converte(dtAtualizacao);
    }

    public void selecionaDataCriacao(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dtCriacao = DataHoje.converte(format.format(event.getObject()));
    }

    public PessoaEndereco getPessoaEndereco() {
        int idTipoEndereco = 1;
        if (tipoDocumento.getId() == 2) {
            idTipoEndereco = 2;
        }
        PessoaEndereco pessoaEndereco = new PessoaEnderecoDao().pesquisaEndPorPessoaTipo(this.id, idTipoEndereco);
        if (pessoaEndereco == null) {
            pessoaEndereco = new PessoaEnderecoDao().pesquisaEndPorPessoaTipo(this.id, 2);
        }
        return pessoaEndereco;
    }

    public PessoaComplemento getPessoaComplemento() {
        PessoaComplemento pessoaComplemento = new PessoaComplemento();
        pessoaComplemento.setPessoa(null);
        if (this.id != -1) {
            PessoaDao pessoaDB = new PessoaDao();
            pessoaComplemento = pessoaDB.pesquisaPessoaComplementoPorPessoa(this.id);
        }
        return pessoaComplemento;
    }

    public Juridica getJuridica() {
        Juridica juridica = new Juridica();
        juridica.setPessoa(null);
        if (this.id != -1) {
            JuridicaDao juridicaDB = new JuridicaDao();
            juridica = juridicaDB.pesquisaJuridicaPorPessoa(this.id);
            if (juridica.getId() != -1) {
                juridica = (Juridica) new Dao().rebind(juridica);
                juridica.setPessoa(null);
            }

        }
        return juridica;
    }

    public Fisica getFisica() {
        Fisica fisica = new Fisica();
        fisica.setPessoa(null);
        if (this.id != -1) {
            FisicaDao fisicaDB = new FisicaDao();
            fisica = fisicaDB.pesquisaFisicaPorPessoa(this.id);
            if (fisica.getId() != -1) {
                fisica = (Fisica) new Dao().rebind(fisica);
            }
            fisica.setPessoa(null);
        }
        return fisica;
    }

    public Socios getSocios() {
        Socios socios = new Socios();
        if (this.id != -1) {
            SociosDao sociosDB = new SociosDao();
            socios = sociosDB.pesquisaSocioPorPessoaAtivo(this.id);
            socios.getServicoPessoa().setPessoa(null);
            if (socios.getMatriculaSocios().getTitular().getId() == this.id) {
                isTitular = true;
            } else {
                isTitular = false;
            }
        }
        return socios;
    }

    public Integer getDiaVencimentoOriginal() {
        if (this.id != -1) {
            PessoaDao db = new PessoaDao();
            PessoaComplemento pc = db.pesquisaPessoaComplementoPorPessoa(this.id);
            if (pc.getId() == -1) {
                Registro registro = (Registro) new Dao().find(new Registro(), 1);
                return registro.getFinDiaVencimentoCobranca();
            } else {
                return pc.getNrDiaVencimento();
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Pessoa other = (Pessoa) obj;
        return true;
    }

    @Override
    public String toString() {
        return "Pessoa{" + "id=" + id + ", nome=" + nome + ", tipoDocumento=" + tipoDocumento + ", obs=" + obs + ", site=" + site + ", telefone1=" + telefone1 + ", telefone2=" + telefone2 + ", telefone3=" + telefone3 + ", email1=" + email1 + ", email2=" + email2 + ", email3=" + email3 + ", documento=" + documento + ", login=" + login + ", senha=" + senha + ", dtCriacao=" + dtCriacao + ", dtAtualizacao=" + dtAtualizacao + ", isTitular=" + isTitular + '}';
    }

    public String getFotoResource() {
        if (this.id != -1) {
            FisicaDao fisicaDB = new FisicaDao();
            Fisica fisica = fisicaDB.pesquisaFisicaPorPessoa(this.id);
            String foto = "";
            if (fisica != null) {
                foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + this.id + "/" + fisica.getFoto() + ".png";
                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);

                if (f.exists()) {
                    return foto;
                }

                foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + this.id + "/" + fisica.getFoto() + ".jpg";
                f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);

                if (f.exists()) {
                    return foto;
                }

                if (fisica.getSexo().equals("F")) {
                    foto = "images/user_female.png";
                } else {
                    foto = "images/user_male.png";
                }
            } else {
                JuridicaDao juridicaDB = new JuridicaDao();
                Juridica juridica = juridicaDB.pesquisaJuridicaPorPessoa(this.id);

                if (juridica != null) {
                    foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + this.id + "/" + juridica.getFoto() + ".png";
                    File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);

                    if (f.exists()) {
                        return foto;
                    }

                    foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + this.id + "/" + juridica.getFoto() + ".jpg";
                    f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);

                    if (f.exists()) {
                        return foto;
                    }
                }

                foto = "images/user_male.png";
            }
            return foto;
        }
        return "images/user_male.png";
    }

    public String getFoto() {
        return getFotoResource();
    }

    /**
     * Retorna true se for titular ( getIsTitular() )
     *
     * @return
     */
    public Boolean getIsTitular() {
        return isTitular;
    }

    public void setIsTitular(Boolean isTitular) {
        this.isTitular = isTitular;
    }

    public String getTelefone4() {
        return telefone4;
    }

    public void setTelefone4(String telefone4) {
        this.telefone4 = telefone4;
    }

    public Date getDtRecadastro() {
        return dtRecadastro;
    }

    public void setDtRecadastro(Date dtRecadastro) {
        this.dtRecadastro = dtRecadastro;
    }

    public String getRecadastroString() {
        return DataHoje.converteData(dtRecadastro);
    }

    public void setRecadastroString(String recadastroString) {
        this.dtRecadastro = DataHoje.converte(recadastroString);
    }

    public Boolean getExistOposicao() {
        if (this.id != -1) {
            if (!this.documento.isEmpty()) {
                if (oposicao == null) {
                    oposicao = new OposicaoDao().existPessoaDocumentoPeriodo(documento, ConfiguracaoArrecadacao.get().getIgnoraPeriodoConvencaoOposicao());
                }
            } else {
                if (oposicao == null) {
                    oposicao = false;
                }
            }
        }
        return oposicao;
    }

}
