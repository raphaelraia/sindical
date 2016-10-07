package br.com.rtools.pessoa;

import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Mask;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "pes_fisica_importacao")
public class FisicaImportacao implements Serializable {

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
    @Column(name = "ds_rg", length = 20)
    private String rg;
    @Column(name = "ds_carteira", length = 100)
    private String carteira;
    @Column(name = "ds_serie", length = 15)
    private String serie;
    @Column(name = "ds_sexo", length = 1)
    private String sexo;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_nascimento", length = 10)
    private Date dtNascimento;
    @Column(name = "ds_nacionalidade", length = 50)
    private String nacionalidade;
    @Column(name = "ds_naturalidade", length = 50)
    private String naturalidade;
    @Column(name = "ds_orgao_emissao_rg", length = 30)
    private String orgao_emissao_rg;
    @Column(name = "ds_uf_emissao_rg", length = 2)
    private String uf_emissao_rg;
    @Column(name = "ds_estado_civil", length = 30)
    private String estado_civil;
    @Column(name = "ds_pai", length = 100)
    private String pai;
    @Column(name = "ds_mae", length = 100)
    private String mae;
    @Column(name = "ds_nit", length = 30)
    private String nit;
    @Column(name = "ds_pis", length = 30)
    private String pis;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_aposentadoria", length = 10)
    private Date dtAposentadoria;
    @Column(name = "ds_titulo_eleitor", length = 20)
    private String titulo_eleitor;
    @Column(name = "ds_titulo_secao", length = 20)
    private String titulo_secao;
    @Column(name = "ds_titulo_zona", length = 20)
    private String titulo_zona;
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
    @Column(name = "nr_matricula", length = 30)
    private String matricula;
    @Column(name = "ds_categoria", length = 100)
    private String categoria;
    @Column(name = "ds_profissao", length = 100)
    private String profissao;
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
    @Column(name = "ds_cep", length = 25)
    private String cep;
    @Column(name = "ds_endereco_original", length = 500)
    private String endereco_original;
    @Column(name = "ds_empresa_documento", length = 25)
    private String empresa_documento;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_filiacao", length = 10)
    private Date dtFiliacao;
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Endereco endereco;
    @JoinColumn(name = "id_fisica", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Fisica fisica;
    @JoinColumn(name = "id_profissao", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Profissao profissaoObjeto;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "dt_homologacao", length = 10)
    private Date dtHomologacao;
    @JoinColumn(name = "id_naturalidade", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Cidade naturalidadeObjeto;
    @Column(name = "nr_codigo", length = 25)
    private String codigo;
    @Transient
    private String nascimento;
    @Transient
    private String filiacao;
    @Transient
    private String inativacao;
    @Transient
    private String aposentadoria;
    @Transient
    private String criacao;

    public FisicaImportacao() {
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
        this.rg = "";
        this.carteira = "";
        this.serie = "";
        this.sexo = "";
        this.dtNascimento = null;
        this.nacionalidade = "";
        this.naturalidade = "";
        this.orgao_emissao_rg = "";
        this.uf_emissao_rg = "";
        this.estado_civil = "";
        this.pai = "";
        this.mae = "";
        this.nit = "";
        this.pis = "";
        this.dtAposentadoria = null;
        this.titulo_eleitor = "";
        this.titulo_secao = "";
        this.titulo_zona = "";
        this.foto = "";
        this.dtImportacao = new Date();
        this.dtInativacao = null;
        this.dtCriacao = null;
        this.matricula = "";
        this.categoria = "";
        this.profissao = "";
        this.logradouro = "";
        this.descricao_endereco = "";
        this.numero = "";
        this.complemento = "";
        this.bairro = "";
        this.cidade = "";
        this.uf = "";
        this.cep = "";
        this.endereco_original = "";
        this.empresa_documento = "";
        this.dtFiliacao = null;
        this.endereco = null;
        this.fisica = null;
        this.nascimento = "";
        this.filiacao = "";
        this.inativacao = "";
        this.aposentadoria = "";
        this.aposentadoria = "";
        this.criacao = "";
        this.profissaoObjeto = null;
        this.dtHomologacao = null;
        this.naturalidadeObjeto = null;
        this.codigo = null;
    }

    public FisicaImportacao(Integer id, String nome, String documento, String telefone1, String telefone2, String telefone3, String telefone4, String email1, String email2, String email3, String site, String observacao, String rg, String carteira, String serie, String sexo, Date dtNascimento, String nacionalidade, String naturalidade, String orgao_emissao_rg, String uf_emissao_rg, String estado_civil, String pai, String mae, String nit, String pis, Date dtAposentadoria, String titulo_eleitor, String titulo_secao, String titulo_zona, String foto, Date dtImportacao, Date dtInativacao, Date dtCriacao, String matricula, String categoria, String profissao, String logradouro, String descricao_endereco, String numero, String complemento, String bairro, String cidade, String uf, String cep, String endereco_original, String empresa_documento, Date dtFiliacao, Endereco endereco, Fisica fisica, Profissao profissaoObjeto, Date dtHomologacao, Cidade naturalidadeObjeto, String codigo) {
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
        this.rg = rg;
        this.carteira = carteira;
        this.serie = serie;
        this.sexo = sexo;
        this.dtNascimento = dtNascimento;
        this.nacionalidade = nacionalidade;
        this.naturalidade = naturalidade;
        this.orgao_emissao_rg = orgao_emissao_rg;
        this.uf_emissao_rg = uf_emissao_rg;
        this.estado_civil = estado_civil;
        this.pai = pai;
        this.mae = mae;
        this.nit = nit;
        this.pis = pis;
        this.dtAposentadoria = dtAposentadoria;
        this.titulo_eleitor = titulo_eleitor;
        this.titulo_secao = titulo_secao;
        this.titulo_zona = titulo_zona;
        this.foto = foto;
        this.dtImportacao = dtImportacao;
        this.dtInativacao = dtInativacao;
        this.dtCriacao = dtCriacao;
        this.matricula = matricula;
        this.categoria = categoria;
        this.profissao = profissao;
        this.logradouro = logradouro;
        this.descricao_endereco = descricao_endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.cep = cep;
        this.endereco_original = endereco_original;
        this.empresa_documento = empresa_documento;
        this.dtFiliacao = dtFiliacao;
        this.endereco = endereco;
        this.fisica = fisica;
        this.profissaoObjeto = profissaoObjeto;
        this.dtHomologacao = dtHomologacao;
        this.cidade = cidade;
        this.codigo = codigo;
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

    public String getDocumento() {
        documento = documento.replace("-", "");
        documento = documento.replace(".", "");
        documento = documento.replace("_", "");
        return documento.trim();
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

    public String getRg() {
        return rg.trim();
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCarteira() {
        return carteira;
    }

    public void setCarteira(String carteira) {
        this.carteira = carteira;
    }

    public String getSerie() {
        return serie.trim();
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getSexo() {
        return sexo.trim();
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNascimento() {
        return DataHoje.converteData(dtNascimento);
    }

    public void setNascimento(String nascimento) {
        this.dtNascimento = DataHoje.converte(nascimento);
    }

    public String getNacionalidade() {
        return nacionalidade.trim();
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getNaturalidade() {
        return naturalidade.trim();
    }

    public void setNaturalidade(String naturalidade) {
        this.naturalidade = naturalidade;
    }

    public String getOrgao_emissao_rg() {
        return orgao_emissao_rg.trim();
    }

    public void setOrgao_emissao_rg(String orgao_emissao_rg) {
        this.orgao_emissao_rg = orgao_emissao_rg;
    }

    public String getUf_emissao_rg() {
        return uf_emissao_rg.trim();
    }

    public void setUf_emissao_rg(String uf_emissao_rg) {
        this.uf_emissao_rg = uf_emissao_rg;
    }

    public String getEstado_civil() {
        return estado_civil.trim();
    }

    public void setEstado_civil(String estado_civil) {
        this.estado_civil = estado_civil;
    }

    public String getPai() {
        return pai.trim();
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public String getMae() {
        return mae.trim();
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getNit() {
        return nit.trim();
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getPis() {
        return pis.trim();
    }

    public void setPis(String pis) {
        this.pis = pis;
    }

    public String getAposentadoria() {
        return DataHoje.converteData(dtAposentadoria);
    }

    public void setAposentadoria(String aposentadoria) {
        this.dtAposentadoria = DataHoje.converte(aposentadoria);
    }

    public String getTitulo_eleitor() {
        return titulo_eleitor.trim();
    }

    public void setTitulo_eleitor(String titulo_eleitor) {
        this.titulo_eleitor = titulo_eleitor;
    }

    public String getTitulo_secao() {
        return titulo_secao.trim();
    }

    public void setTitulo_secao(String titulo_secao) {
        this.titulo_secao = titulo_secao;
    }

    public String getTitulo_zona() {
        return titulo_zona.trim();
    }

    public void setTitulo_zona(String titulo_zona) {
        this.titulo_zona = titulo_zona;
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

    public String getMatricula() {
        return matricula.trim();
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCategoria() {
        return categoria.trim();
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getProfissao() {
        return profissao.trim();
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
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

    public String getEmpresa_documento() {
        return empresa_documento.trim();
    }

    public void setEmpresa_documento(String empresa_documento) {
        this.empresa_documento = empresa_documento;
    }

    public String getFiliacao() {
        return DataHoje.converteData(dtFiliacao);
    }

    public void setFiliacao(String filiacao) {
        this.dtFiliacao = DataHoje.converte(filiacao);
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
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

    public Date getDtNascimento() {
        if (nascimento != null && !nascimento.isEmpty()) {
            dtNascimento = DataHoje.converte(nascimento);
        }
        return dtNascimento;
    }

    public void setDtNascimento(Date dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public Date getDtAposentadoria() {
        if (aposentadoria != null && !aposentadoria.isEmpty()) {
            dtAposentadoria = DataHoje.converte(aposentadoria);
        }
        return dtAposentadoria;
    }

    public void setDtAposentadoria(Date dtAposentadoria) {
        this.dtAposentadoria = dtAposentadoria;
    }

    public Date getDtImportacao() {
        return dtImportacao;
    }

    public void setDtImportacao(Date dtImportacao) {
        this.dtImportacao = dtImportacao;
    }

    public Date getDtInativacao() {
        if (inativacao != null && !inativacao.isEmpty()) {
            dtInativacao = DataHoje.converte(inativacao);
        }
        return dtInativacao;
    }

    public void setDtInativacao(Date dtInativacao) {
        this.dtInativacao = dtInativacao;
    }

    public Date getDtCriacao() {
        if (filiacao != null && !criacao.isEmpty()) {
            dtCriacao = DataHoje.converte(criacao);
        }
        return dtCriacao;
    }

    public void setDtCriacao(Date dtCriacao) {
        this.dtCriacao = dtCriacao;
    }

    public Date getDtFiliacao() {
        if (filiacao != null && !filiacao.isEmpty()) {
            dtFiliacao = DataHoje.converte(filiacao);
        }
        return dtFiliacao;
    }

    public void setDtFiliacao(Date dtFiliacao) {
        this.dtFiliacao = dtFiliacao;
    }

    public Profissao getProfissaoObjeto() {
        return profissaoObjeto;
    }

    public void setProfissaoObjeto(Profissao profissaoObjeto) {
        this.profissaoObjeto = profissaoObjeto;
    }

    public Date getDtHomologacao() {
        return dtHomologacao;
    }

    public void setDtHomologacao(Date dtHomologacao) {
        this.dtHomologacao = dtHomologacao;
    }

    public void reviseDocumento() {
        if (!documento.isEmpty() && !documento.equals("0")) {
            documento = replace(documento);
            if (documento.length() < 11) {
                while (documento.length() < 11) {
                    documento = "0" + documento;
                }
            }
        } else {
            documento = "0";
        }
    }

    public void reviseCNPJ() {
        if (!empresa_documento.isEmpty() && !empresa_documento.equals("0")) {
            empresa_documento = replace(empresa_documento);
            while (empresa_documento.length() < 14) {
                empresa_documento = "0" + empresa_documento;
            }
        } else {
            empresa_documento = "0";
        }
    }

    public void revisePIS() {
        if (!pis.isEmpty()) {
            pis = replace(pis);
            if (pis.length() < 11) {
                while (pis.length() < 11) {
                    pis = "0" + pis;
                }
            }
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

    public void reviseSexo() {
        if (!sexo.isEmpty()) {
            sexo = (AnaliseString.removerAcentos(sexo)).toUpperCase();
            if (sexo.contains("MASCULINO") || sexo.contains("HOMEM")) {
                sexo = "M";
            } else if (sexo.contains("FEMININO") || sexo.contains("MULHER")) {
                sexo = "F";
            } else {
                sexo = "F";
            }
        }
    }

    public void reviseEstadoCivil() {
        if (estado_civil.isEmpty()) {
            estado_civil = "Indefinido(a)";
        } else {
            estado_civil = (AnaliseString.removerAcentos(estado_civil)).toUpperCase();
            if (estado_civil.contains("AMASIADO") || estado_civil.contains("AMASIADO")) {
                estado_civil = "Amasiado(a)";
            } else if (estado_civil.contains("CASADO") || estado_civil.contains("CASADA")) {
                estado_civil = "Casado(a)";
            } else if (estado_civil.contains("DESQUITADO") || estado_civil.contains("DESQUITADO")) {
                estado_civil = "Desquitado(a)";
            } else if (estado_civil.contains("DIVORCIADO") || estado_civil.contains("DIVORCIADA")) {
                estado_civil = "Divorciado(a)";
            } else if (estado_civil.contains("INDEFINIDO") || estado_civil.contains("INDEFINIDA")) {
                estado_civil = "Indefinido(a)";
            } else if (estado_civil.contains("SEPARADO") || estado_civil.contains("SEPARADA")) {
                estado_civil = "Separado(a)";
            } else if (estado_civil.contains("SOLTEIRO") || estado_civil.contains("SOLTEIRA")) {
                estado_civil = "Solteiro(a)";
            } else if (estado_civil.contains("VIUVO") || estado_civil.contains("VIUVA")) {
                estado_civil = "Viuvo(a)";
            } else {
                estado_civil = "Indefinido(a)";
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

    public void reviseCTPS() {
        String[] s;
        if (serie == null || serie.isEmpty()) {
            if (carteira != null && !carteira.isEmpty()) {
                try {
                    s = carteira.split(".");
                    if (s.length > 1) {
                        carteira = s[0];
                        serie = s[1];
                        return;
                    }
                    s = carteira.split("-");
                    if (s.length > 1) {
                        carteira = s[0];
                        serie = s[1];
                        return;
                    }
                    s = carteira.split("\\\\");
                    if (s.length > 1) {
                        carteira = s[0];
                        serie = s[1];
                        return;
                    }
                    s = carteira.split("/");
                    if (s.length > 1) {
                        carteira = s[0];
                        serie = s[1];
                        return;
                    }
                } catch (Exception e) {

                }
            }
        }

    }

    public Cidade getNaturalidadeObjeto() {
        return naturalidadeObjeto;
    }

    public void setNaturalidadeObjeto(Cidade naturalidadeObjeto) {
        this.naturalidadeObjeto = naturalidadeObjeto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
