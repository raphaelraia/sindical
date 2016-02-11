package br.com.rtools.arrecadacao;

import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;

@Entity
@Table(name = "arr_oposicao_pessoa")
@NamedQuery(name = "OposicaoPessoa.pesquisaID", query = "select op from OposicaoPessoa op where op.id=:pid")
public class OposicaoPessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_cadastro")
    private Date dataCadastro;
    @Column(name = "ds_nome", length = 200, nullable = false)
    private String nome;
    @Column(name = "ds_sexo", length = 1)
    private String sexo;
    @Column(name = "ds_cpf", length = 15)
    private String cpf;
    @Column(name = "ds_rg", length = 12)
    private String rg;
    @Column(name = "ds_email1", length = 100)
    private String email1;
    @Column(name = "ds_telefone1", length = 20)
    private String telefone1;
    @Column(name = "ds_telefone2", length = 20)
    private String telefone2;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_nascimento")
    private Date dataNascimento;    

    public OposicaoPessoa() {
        this.id = -1;
        setDataCadastroString(DataHoje.data());
        this.nome = "";
        this.cpf = "";
        this.rg = "";
        this.email1 = "";
        this.telefone1 = "";
        this.telefone2 = "";
        this.dataNascimento = null;
    }

    public OposicaoPessoa(Integer id, String dataCadastro, String nome, String cpf, String rg, String observacao, String email1, String telefone1, String telefone2, Date dataNascimento) {
        this.id = id;
        setDataCadastroString(dataCadastro);
        this.nome = nome;
        this.cpf = cpf;
        this.rg = rg;
        this.email1 = email1;
        this.telefone1 = telefone1;
        this.telefone2 = telefone2;
        this.dataNascimento = dataNascimento;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getDataCadastroString() {
        return DataHoje.converteData(this.dataCadastro);
    }

    public void setDataCadastroString(String dataCadastro) {
        this.dataCadastro = DataHoje.converte(dataCadastro);
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
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

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public String getDataNascimentoString() {
        return DataHoje.converteData(this.dataNascimento);
    }

    public void setDataNascimentoString(String dataNascimentoString) {
        this.dataNascimento = DataHoje.converte(dataNascimentoString);
    }    
}
