package br.com.rtools.pessoa.utilitarios;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@ManagedBean(name = "pessoaUtilitariosBean")
@ViewScoped
public class PessoaUtilitarios implements Serializable {

    private Pessoa pessoa;
    private MacFilial macFilial = (MacFilial) GenericaSessao.getObject("acessoFilial");
    private Usuario usuarioSessao = (Usuario) GenericaSessao.getObject("sessaoUsuario");

    public PessoaUtilitarios(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public PessoaUtilitarios() {
        this.pessoa = new Pessoa();
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    // Fácil
    /**
     * Retorna lista de endereços da pessoa
     *
     * @return
     */
    public List<PessoaEndereco> listaPessoaEndereco() {
        List<PessoaEndereco> pessoaEnderecos = new ArrayList();
        if (this.pessoa.getId() != -1) {
            PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
            pessoaEnderecos = (List<PessoaEndereco>) pessoaEnderecoDB.pesquisaEndPorPessoa(this.pessoa.getId());
        }
        return pessoaEnderecos;
    }

    /**
     * Retorna lista de endereços da pessoa
     *
     * @param tipoEndereco
     * @return
     */
    public PessoaEndereco pessoaEndereco(TipoEndereco tipoEndereco) {
        PessoaEndereco pessoaEnderecos = new PessoaEndereco();
        if (this.pessoa.getId() != -1) {
            PessoaEnderecoDao pessoaEnderecoDB = new PessoaEnderecoDao();
            if (tipoEndereco.getId() != -1) {
                pessoaEnderecos = (PessoaEndereco) pessoaEnderecoDB.pesquisaEndPorPessoaTipo(this.pessoa.getId(), tipoEndereco.getId());
            }
        }
        return pessoaEnderecos;
    }

    /**
     * Retorna lista de empresas da pessoa
     *
     * @return
     */
    public List<PessoaEmpresa> listaPessoaEmpresa() {
        List<PessoaEmpresa> pessoaEmpresa = new ArrayList<>();
        if (this.pessoa.getId() != -1) {
            PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
            Fisica fisica = fisica();
            pessoaEmpresa = (List<PessoaEmpresa>) pessoaEmpresaDB.listaPessoaEmpresaPorFisica(fisica.getId());
        }
        return pessoaEmpresa;
    }

    /**
     * Retorna lista de empresas da pessoa
     *
     * @return
     */
    public PessoaEmpresa pessoaEmpresa() {
        PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
        if (this.pessoa.getId() != -1) {
            PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
            pessoaEmpresa = (PessoaEmpresa) pessoaEmpresaDB.pesquisaPessoaEmpresaPorPessoa(this.pessoa.getId());
        }
        return pessoaEmpresa;
    }

    /**
     * Retorna pessoa jurídica da pessoa
     *
     * @return
     */
    public Juridica juridica() {
        Juridica juridica = new Juridica();
        if (this.pessoa.getId() != -1) {
            JuridicaDao juridicaDB = new JuridicaDao();
            juridicaDB.pesquisaJuridicaPorPessoa(this.pessoa.getId());
        }
        return juridica;
    }

    /**
     * Retorna pessoa física da pessoa
     *
     * @return
     */
    public Fisica fisica() {
        Fisica fisica = new Fisica();
        if (this.pessoa.getId() != -1) {
            FisicaDao fisicaDB = new FisicaDao();
            fisicaDB.pesquisaFisicaPorPessoa(this.pessoa.getId());
        }
        return fisica;
    }

    /**
     * Retorna usuário da pessoa
     *
     * @return
     */
    public Usuario usuario() {
        Usuario usuario1 = new Usuario();
        if (this.pessoa.getId() != -1) {
            UsuarioDao usuarioDB = new UsuarioDao();
            usuarioDB.pesquisaUsuarioPorPessoa(this.pessoa.getId());
        }
        return usuario1;
    }

    /**
     * Retorna usuário da pessoa
     *
     * @return
     */
    public Registro registro() {
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setId(1);
        return registro(pessoa1);
    }

    /**
     * Retorna usuário da pessoa
     *
     * @param pessoa
     * @return
     */
    public Registro registro(Pessoa pessoa) {
        Registro registro = new Registro();
        if (pessoa.getId() != -1) {
            registro = Registro.get();
        }
        return registro;
    }

    /**
     * Retorna macfilial da sessão
     *
     * @return
     */
    public MacFilial getMacFilial() {
        return macFilial;
    }

    /**
     * Retorna usuário da sessão
     *
     * @return
     */
    public Usuario getUsuarioSessao() {
        return usuarioSessao;
    }

    /**
     * Retorna foto da pessoa
     *
     * @param pessoa
     * @return
     */
    public String getFotoPessoaFisica(Pessoa pessoa) {
        return getFotoPessoaFisica(pessoa, 0);
    }

    /**
     * Retorna foto da pessoa
     *
     * @param pessoa
     * @param waiting
     * @return
     */
    public String getFotoPessoaFisica(Pessoa pessoa, Integer waiting) {
        if (waiting > 0) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException ex) {
            }
        }
        String foto = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + pessoa.getId() + ".png";
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(foto));
        if (!f.exists()) {
            foto = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + pessoa.getId() + ".jpg";
            f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(foto));
            if (!f.exists()) {
                if (pessoa.getFisica().getSexo().equals("F")) {
                    foto = "/Imagens/user_female.png";
                } else {
                    foto = "/Imagens/user_male.png";
                }
            }
        }
        return foto;
    }

    /**
     * Retorna foto da pessoa
     *
     * @param fisica
     * @return
     */
    public String getFotoPessoaFisica(Fisica fisica) {
        return getFotoPessoaFisica(fisica, 0);
    }

    /**
     * Retorna foto da pessoa
     *
     * @param fisica
     * @param waiting
     * @return
     */
    public String getFotoPessoaFisica(Fisica fisica, Integer waiting) {
        if (waiting > 0) {
            try {
                Thread.sleep(waiting);
            } catch (InterruptedException ex) {
            }
        }
        String foto = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + fisica.getPessoa().getId() + ".png";
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(foto));
        if (!f.exists()) {
            foto = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + fisica.getPessoa().getId() + ".jpg";
            f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(foto));
            if (!f.exists()) {
                if (fisica.getSexo().equals("F")) {
                    foto = "/Imagens/user_female.png";
                } else {
                    foto = "/Imagens/user_male.png";
                }
            }
        }
        return foto;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public void setUsuarioSessao(Usuario usuarioSessao) {
        this.usuarioSessao = usuarioSessao;
    }
}
