package br.com.rtools.pessoa;

import br.com.rtools.endereco.Endereco;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Mask;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "pes_juridica_importacao")
public class JuridicaImportacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_nome", length = 150)
    private String nome;
    @Column(name = "ds_documento", length = 30)
    private String documento;
    @Column(name = "ds_telefone1", length = 20)
    private String telefone1;
    @Column(name = "ds_telefone2", length = 20)
    private String telefone2;
    @Column(name = "ds_telefone3", length = 20)
    private String telefone3;
    @Column(name = "ds_telefone4", length = 20)
    private String telefone4;
    @Column(name = "ds_email1", length = 50)
    private String email1;
    @Column(name = "ds_email2", length = 50)
    private String email2;
    @Column(name = "ds_email3", length = 50)
    private String email3;
    @Column(name = "ds_site", length = 50)
    private String site;
    @Column(name = "ds_obs", length = 1000)
    private String observacao;
    @Column(name = "ds_foto", length = 1000)
    private String foto;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_importacao")
    private Date dtImportacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inativacao", length = 10)
    private Date dtInativacao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_criacao", length = 10)
    private Date dtCriacao;
    @Column(name = "ds_logradouro", length = 100)
    private String logradouro;
    @Column(name = "ds_descricao_endereco", length = 100)
    private String descricao_endereco;
    @Column(name = "ds_numero", length = 30)
    private String numero;
    @Column(name = "ds_complemento", length = 100)
    private String complemento;
    @Column(name = "ds_bairro", length = 100)
    private String bairro;
    @Column(name = "ds_cidade", length = 100)
    private String cidade;
    @Column(name = "ds_uf", length = 2)
    private String uf;
    @Column(name = "ds_logradouro_extraido", length = 100)
    private String logradouro_extraido;
    @Column(name = "ds_descricao_endereco_extraida", length = 100)
    private String descricao_endereco_extraida;
    @Column(name = "ds_numero_extraido", length = 30)
    private String numero_extraido;
    @Column(name = "ds_complemento_extraido", length = 100)
    private String complemento_extraido;
    @Column(name = "ds_bairro_extraido", length = 100)
    private String bairro_extraido;
    @Column(name = "ds_cidade_extraida", length = 100)
    private String cidade_extraida;
    @Column(name = "ds_uf_extraido", length = 2)
    private String uf_extraido;
    @Column(name = "ds_cep", length = 25)
    private String cep;
    @Column(name = "ds_endereco_original", length = 500)
    private String endereco_original;
    @Column(name = "ds_fantasia", length = 200)
    private String fantasia;
    @JoinColumn(name = "id_cnae", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Cnae cnae;
    @Column(name = "ds_cnae_descricao", length = 500)
    private String cnae_descricao;
    @JoinColumn(name = "id_contabilidade", referencedColumnName = "id")
    @OneToOne
    private Juridica contabilidade;
    @Column(name = "ds_contabilidade_nome", length = 150)
    private String contabilidade_nome;
    @Column(name = "ds_inscricao_estadual")
    private String inscricao_estadual;
    @Column(name = "ds_inscricao_municipal")
    private String inscricao_municipal;
    @Column(name = "ds_contato", length = 50)
    private String contato;
    @Column(name = "ds_responsavel", length = 50)
    private String responsavel;
    @JoinColumn(name = "id_porte", referencedColumnName = "id")
    @ManyToOne
    private Porte porte;
    @Column(name = "ds_porte_descricao", length = 500)
    private String porte_descricao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_abertura")
    private Date dtAbertura;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_fechamento")
    private Date dtFechamento;
    @JoinColumn(name = "id_juridica", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Juridica juridica;
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Endereco endereco;
    @Column(name = "nr_codigo", length = 25)
    private String codigo;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_homologacao")
    private Date dtHomologacao;
    @Transient
    private String criacao;
    @Transient
    private String abertura;
    @Transient
    private String fechamento;

    public JuridicaImportacao() {
        this.id = null;
        this.nome = "";
        this.documento = "";
        this.telefone1 = "";
        this.telefone2 = "";
        this.telefone3 = "";
        this.telefone4 = "";
        this.email1 = "";
        this.email2 = "";
        this.email3 = "";
        this.site = "";
        this.observacao = "";
        this.foto = "";
        this.dtImportacao = new Date();
        this.dtInativacao = null;
        this.dtCriacao = null;
        this.logradouro = "";
        this.descricao_endereco = "";
        this.numero = "";
        this.complemento = "";
        this.bairro = "";
        this.cidade = "";
        this.uf = "";
        this.logradouro_extraido = "";
        this.descricao_endereco_extraida = "";
        this.numero_extraido = "";
        this.complemento_extraido = "";
        this.bairro_extraido = "";
        this.cidade_extraida = "";
        this.uf_extraido = "";
        this.cep = "";
        this.endereco_original = "";
        this.fantasia = "";
        this.cnae = null;
        this.cnae_descricao = "";
        this.contabilidade = null;
        this.contabilidade_nome = "";
        this.inscricao_estadual = "";
        this.inscricao_municipal = "";
        this.contato = "";
        this.responsavel = "";
        this.porte = null;
        this.porte_descricao = "";
        this.dtAbertura = null;
        this.dtFechamento = null;
        this.juridica = null;
        this.endereco = null;
        this.codigo = "";
        this.dtHomologacao = null;
        this.criacao = null;
        this.abertura = null;
        this.fechamento = null;
    }

    public JuridicaImportacao(Integer id, String nome, String documento, String telefone1, String telefone2, String telefone3, String telefone4, String email1, String email2, String email3, String site, String observacao, String foto, Date dtImportacao, Date dtInativacao, Date dtCriacao, String logradouro, String descricao_endereco, String numero, String complemento, String bairro, String cidade, String uf, String logradouro_extraido, String descricao_endereco_extraida, String numero_extraido, String complemento_extraido, String bairro_extraido, String cidade_extraida, String uf_extraido, String cep, String endereco_original, String fantasia, Cnae cnae, String cnae_descricao, Juridica contabilidade, String contabilidade_nome, String inscricao_estadual, String inscricao_municipal, String contato, String responsavel, Porte porte, String porte_descricao, Date dtAbertura, Date dtFechamento, Juridica juridica, Endereco endereco, String codigo, Date dtHomologacao, String criacao, String abertura, String fechamento) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.telefone3 = telefone3;
        this.telefone4 = telefone4;
        this.email1 = email1;
        this.email2 = email2;
        this.email3 = email3;
        this.site = site;
        this.observacao = observacao;
        this.foto = foto;
        this.dtImportacao = dtImportacao;
        this.dtInativacao = dtInativacao;
        this.dtCriacao = dtCriacao;
        this.logradouro = logradouro;
        this.descricao_endereco = descricao_endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.logradouro_extraido = logradouro_extraido;
        this.descricao_endereco_extraida = descricao_endereco_extraida;
        this.numero_extraido = numero_extraido;
        this.complemento_extraido = complemento_extraido;
        this.bairro_extraido = bairro_extraido;
        this.cidade_extraida = cidade_extraida;
        this.uf_extraido = uf_extraido;
        this.cep = cep;
        this.endereco_original = endereco_original;
        this.fantasia = fantasia;
        this.cnae = cnae;
        this.cnae_descricao = cnae_descricao;
        this.contabilidade = contabilidade;
        this.contabilidade_nome = contabilidade_nome;
        this.inscricao_estadual = inscricao_estadual;
        this.inscricao_municipal = inscricao_municipal;
        this.contato = contato;
        this.responsavel = responsavel;
        this.porte = porte;
        this.porte_descricao = porte_descricao;
        this.dtAbertura = dtAbertura;
        this.dtFechamento = dtFechamento;
        this.juridica = juridica;
        this.endereco = endereco;
        this.codigo = codigo;
        this.criacao = criacao;
        this.abertura = abertura;
        this.fechamento = fechamento;
        this.dtHomologacao = dtHomologacao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome.trim();
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefone1() {
        return telefone1.trim();
    }

    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2.trim();
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getTelefone3() {
        return telefone3.trim();
    }

    public void setTelefone3(String telefone3) {
        this.telefone3 = telefone3;
    }

    public String getTelefone4() {
        return telefone4.trim();
    }

    public void setTelefone4(String telefone4) {
        this.telefone4 = telefone4;
    }

    public String getEmail1() {
        return email1.trim();
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2.trim();
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getEmail3() {
        return email3.trim();
    }

    public void setEmail3(String email3) {
        this.email3 = email3;
    }

    public String getSite() {
        return site.trim();
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getObservacao() {
        return observacao.trim();
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getFoto() {
        return foto.trim();
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getImportacao() {
        return DataHoje.converteData(dtImportacao);
    }

    public void setImportacao(String importacao) {
        this.dtImportacao = DataHoje.converte(importacao);
    }

    public String getLogradouro() {
        return logradouro.trim();
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getDescricao_endereco() {
        return descricao_endereco.trim();
    }

    public void setDescricao_endereco(String descricao_endereco) {
        this.descricao_endereco = descricao_endereco;
    }

    public String getNumero() {
        return numero.trim();
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento.trim();
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro.trim();
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade.trim();
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf.trim();
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep.trim();
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco_original() {
        return endereco_original.trim();
    }

    public void setEndereco_original(String endereco_original) {
        this.endereco_original = endereco_original;
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public String getInativacao() {
        return DataHoje.converteData(dtInativacao);
    }

    public void setInativacao(String inativacao) {
        this.dtInativacao = DataHoje.converte(inativacao);
    }

    public String getCriacao() {
        return DataHoje.converteData(dtCriacao);
    }

    public void setCriacao(String criacao) {
        this.dtCriacao = DataHoje.converte(criacao);
    }

    public Date getDtImportacao() {
        return dtImportacao;
    }

    public void setDtImportacao(Date dtImportacao) {
        this.dtImportacao = dtImportacao;
    }

    public Date getDtAbertura() {
        if (abertura != null && !abertura.isEmpty()) {
            dtAbertura = DataHoje.converte(abertura);
        }
        return dtAbertura;
    }

    public void setDtAbertura(Date dtAbertura) {
        this.dtAbertura = dtAbertura;
    }

    public Date getDtFechamento() {
        if (fechamento != null && !fechamento.isEmpty()) {
            dtFechamento = DataHoje.converte(fechamento);
        }
        return dtFechamento;
    }

    public void setDtFechamento(Date dtFechamento) {
        this.dtFechamento = dtFechamento;
    }

    public Date getDtCriacao() {
        if (criacao != null && !criacao.isEmpty()) {
            dtCriacao = DataHoje.converte(criacao);
        }
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getLogradouro_extraido() {
        return logradouro_extraido;
    }

    public void setLogradouro_extraido(String logradouro_extraido) {
        this.logradouro_extraido = logradouro_extraido;
    }

    public String getDescricao_endereco_extraida() {
        return descricao_endereco_extraida;
    }

    public void setDescricao_endereco_extraida(String descricao_endereco_extraida) {
        this.descricao_endereco_extraida = descricao_endereco_extraida;
    }

    public String getNumero_extraido() {
        return numero_extraido;
    }

    public void setNumero_extraido(String numero_extraido) {
        this.numero_extraido = numero_extraido;
    }

    public String getComplemento_extraido() {
        return complemento_extraido;
    }

    public void setComplemento_extraido(String complemento_extraido) {
        this.complemento_extraido = complemento_extraido;
    }

    public String getBairro_extraido() {
        return bairro_extraido;
    }

    public void setBairro_extraido(String bairro_extraido) {
        this.bairro_extraido = bairro_extraido;
    }

    public String getCidade_extraida() {
        return cidade_extraida;
    }

    public void setCidade_extraida(String cidade_extraida) {
        this.cidade_extraida = cidade_extraida;
    }

    public String getUf_extraido() {
        return uf_extraido;
    }

    public void setUf_extraido(String uf_extraido) {
        this.uf_extraido = uf_extraido;
    }

    public Date getDtInativacao() {
        return dtInativacao;
    }

    public void setDtInativacao(Date dtInativacao) {
        this.dtInativacao = dtInativacao;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public Cnae getCnae() {
        return cnae;
    }

    public void setCnae(Cnae cnae) {
        this.cnae = cnae;
    }

    public String getCnae_descricao() {
        return cnae_descricao;
    }

    public void setCnae_descricao(String cnae_descricao) {
        this.cnae_descricao = cnae_descricao;
    }

    public Juridica getContabilidade() {
        return contabilidade;
    }

    public void setContabilidade(Juridica contabilidade) {
        this.contabilidade = contabilidade;
    }

    public String getContabilidade_nome() {
        return contabilidade_nome;
    }

    public void setContabilidade_nome(String contabilidade_nome) {
        this.contabilidade_nome = contabilidade_nome;
    }

    public String getInscricao_estadual() {
        return inscricao_estadual;
    }

    public void setInscricao_estadual(String inscricao_estadual) {
        this.inscricao_estadual = inscricao_estadual;
    }

    public String getInscricao_municipal() {
        return inscricao_municipal;
    }

    public void setInscricao_municipal(String inscricao_municipal) {
        this.inscricao_municipal = inscricao_municipal;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Porte getPorte() {
        return porte;
    }

    public void setPorte(Porte porte) {
        this.porte = porte;
    }

    public String getPorte_descricao() {
        return porte_descricao;
    }

    public void setPorte_descricao(String porte_descricao) {
        this.porte_descricao = porte_descricao;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getAbertura() {
        return abertura;
    }

    public void setAbertura(String abertura) {
        this.abertura = abertura;
    }

    public String getFechamento() {
        return fechamento;
    }

    public void setFechamento(String fechamento) {
        this.fechamento = fechamento;
    }

    public String getDocumento() {
        documento = documento.replace("-", "");
        documento = documento.replace(".", "");
        documento = documento.replace("_", "");
        return documento.trim();
    }

    public void reviseDocumento() {
        if (!documento.isEmpty() && !documento.equals("0")) {
            documento = replace(documento);
            if (documento.length() < 14) {
                while (documento.length() < 14) {
                    documento = "0" + documento;
                }
            }
        } else {
            documento = "0";
        }
    }

    public void reviseTelefone() {
        telefone1 = replaceTelefone(telefone1);
        telefone2 = replaceTelefone(telefone2);
        telefone3 = replaceTelefone(telefone3);
        telefone4 = replaceTelefone(telefone4);
    }

    public void reviseCEP() {
        if (!cep.isEmpty()) {
            cep = replaceCEP(cep);
            if (cep.length() < 8) {
                while (cep.length() < 8) {
                    cep = "0" + cep;
                }
            }
        }
    }

    public void reviseDatas() {
        if (dtCriacao == null) {
            if (dtAbertura != null) {
                dtCriacao = dtAbertura;
            } else {
                dtCriacao = DataHoje.converte("01/01/1900");
            }
        }
    }

    private String replaceTelefone(String phone) {
        if (!phone.isEmpty()) {
            phone = replace(phone);
            phone = AnaliseString.onlyNumbers(phone);
            if (phone.length() >= 8) {
                if (phone.length() == 8) {
                    Registro r = Registro.get();
                    if (r != null && r.getFilial() != null && r.getFilial().getId() != -1) {
                        if (!r.getFilial().getPessoa().getTelefone1().isEmpty()) {
                            String p = AnaliseString.onlyNumbers(replace(r.getFilial().getPessoa().getTelefone1()));
                            if (p.length() == 10 || p.length() == 11) {
                                phone = p.substring(0, 2) + phone;
                            }
                        }
                    }
                } else {
                    while (phone.length() < 10) {
                        phone = "0" + phone;
                    }
                }
                phone = Mask.applyPhoneMask(phone);
            } else {
                phone = "";
            }
        }
        return phone;
    }

    private String replaceCEP(String phone) {
        return replace(phone);
    }

    public String replace(String v) {
        try {
            if (v != null && !v.isEmpty()) {
                v = v.replace("-", "");
                v = v.replace("(", "");
                v = v.replace(")", "");
                v = v.replace("_", "");
                v = v.replace(".", "");
                v = v.replace(" ", "");
                v = v.replace("[", "");
                v = v.replace("]", "");
                v = v.replace("|", "");
                v = v.replace("*", "");
            }
        } catch (Exception e) {

        }
        return v;
    }

    public void reviseEndereco() {

    }

    public Date getDtHomologacao() {
        return dtHomologacao;
    }

    public void setDtHomologacao(Date dtHomologacao) {
        this.dtHomologacao = dtHomologacao;
    }

}
