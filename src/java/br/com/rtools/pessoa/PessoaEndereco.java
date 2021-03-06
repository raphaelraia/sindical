package br.com.rtools.pessoa;

import br.com.rtools.endereco.Endereco;
import br.com.rtools.utilitarios.Mask;
import javax.persistence.*;

@Entity
@Table(name = "pes_pessoa_endereco")
@NamedQuery(name = "PessoaEndereco.pesquisaID", query = "select pese from PessoaEndereco pese where pese.id=:pid")
public class PessoaEndereco implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_endereco", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Endereco endereco;
    @JoinColumn(name = "id_tipo_endereco", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private TipoEndereco tipoEndereco;
    @JoinColumn(name = "id_pessoa", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @Column(name = "ds_numero", length = 30, nullable = false)
    private String numero;
    @Column(name = "ds_complemento", length = 50, nullable = true)
    private String complemento;

    public PessoaEndereco() {
        this.id = -1;
        this.endereco = new Endereco();
        this.tipoEndereco = new TipoEndereco();
        this.pessoa = new Pessoa();
        this.numero = "";
        this.complemento = "";
    }

    public PessoaEndereco(int id, Endereco endereco, TipoEndereco tipoEndereco, Pessoa pessoa, String numero, String complemento) {
        this.id = id;
        this.endereco = endereco;
        this.tipoEndereco = tipoEndereco;
        this.pessoa = pessoa;
        this.numero = numero;
        this.complemento = complemento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getEnderecoCompletoString() {
        String enderecoString = "";
        try {
            if (!this.endereco.getLogradouro().getDescricao().equals("")) {
                enderecoString += this.endereco.getLogradouro().getDescricao();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getDescricaoEndereco().getDescricao().equals("")) {
                enderecoString += " " + this.endereco.getDescricaoEndereco().getDescricao();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getBairro().getDescricao().equals("")) {
                enderecoString += ", " + this.endereco.getBairro().getDescricao();
            }
        } catch (Exception e) {

        }
        try {
            if (!numero.equals("")) {
                enderecoString += ", nº " + numero + " ";
            }
        } catch (Exception e) {

        }
        try {
            if (!complemento.equals("")) {
                enderecoString += " - " + this.complemento;
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getCidade().getCidade().equals("")) {
                enderecoString += " - " + this.endereco.getCidade().getCidade();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getCidade().getUf().equals("")) {
                enderecoString += " - " + this.endereco.getCidade().getUf();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getCep().equals("")) {
                enderecoString += " - CEP: " + Mask.cep(this.endereco.getCep());
            }
        } catch (Exception e) {

        }
        return enderecoString;
    }

    public String getEnderecoCompletoSemComplementoString() {
        String enderecoString = "";
        try {
            if (!this.endereco.getLogradouro().getDescricao().isEmpty()) {
                enderecoString += this.endereco.getLogradouro().getDescricao();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getDescricaoEndereco().getDescricao().isEmpty()) {
                enderecoString += " " + this.endereco.getDescricaoEndereco().getDescricao();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getBairro().getDescricao().isEmpty()) {
                enderecoString += ", " + this.endereco.getBairro().getDescricao();
            }
        } catch (Exception e) {

        }
        try {
            if (!numero.equals("")) {
                enderecoString += ", nº " + numero + " ";
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getCidade().getCidade().equals("")) {
                enderecoString += " - " + this.endereco.getCidade().getCidade();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getCidade().getUf().equals("")) {
                enderecoString += " - " + this.endereco.getCidade().getUf();
            }
        } catch (Exception e) {

        }
        try {
            if (!this.endereco.getCep().equals("")) {
                enderecoString += " - CEP: " + Mask.cep(this.endereco.getCep());
            }
        } catch (Exception e) {

        }
        return enderecoString;
    }

    /* public String getEnderecoToString(){
     String result = this.getEndereco().getLogradouro().getLogradouro() + " " +
     this.getEndereco().getDescricaoEndereco().getDescricaoEndereco() + ", " +
     this.getEndereco().getBairro().getBairro() + ", " +
     this.getEndereco().getCidade().getCidade() + "  -  "  +
     this.getEndereco().getCidade().getUf() + "  ";
     if (!(this.complemento.equals(""))){

     }
     return result;
     }*/
}
