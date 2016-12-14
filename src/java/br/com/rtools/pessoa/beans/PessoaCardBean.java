package br.com.rtools.pessoa.beans;

import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.PermissaoUsuario;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.dao.PermissaoUsuarioDao;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.beans.EmailBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@ViewScoped
public class PessoaCardBean implements Serializable {

    private String cliente = "";
    private Fisica fisica = new Fisica();
    private Juridica juridica = new Juridica();
    private Pessoa pessoa = new Pessoa();
    private PessoaEndereco pessoaEndereco = new PessoaEndereco();
    private PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
    private String[] imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
    private Usuario usuario;
    private List<PermissaoUsuario> listPermissaoUsuario = new ArrayList();

    public void cardPessoa(Integer pessoa_id) {
        close();
        Dao dao = new Dao();
        pessoa = (Pessoa) dao.find(new Pessoa(), pessoa_id);
    }

    public void cardUsuario(Integer pessoa_id) {
        close();
        FisicaDao fisicaDB = new FisicaDao();
        fisica = (Fisica) fisicaDB.pesquisaFisicaPorPessoa(pessoa_id);
        if (fisica == null) {
            JuridicaDao juridicaDB = new JuridicaDao();
            juridica = (Juridica) juridicaDB.pesquisaJuridicaPorPessoa(pessoa_id);
            pessoa = juridica.getPessoa();
        } else {
            pessoa = fisica.getPessoa();
        }
        usuario = new UsuarioDao().pesquisaUsuarioPorPessoa(pessoa.getId());
        if (usuario != null) {
            listPermissaoUsuario = new ArrayList();
            listPermissaoUsuario = new PermissaoUsuarioDao().pesquisaListaPermissaoPorUsuario(usuario.getId());
        }
    }

    public void cardFisica(Integer pessoa_id) {
        close();
        FisicaDao fisicaDB = new FisicaDao();
        fisica = (Fisica) fisicaDB.pesquisaFisicaPorPessoa(pessoa_id);
        pessoa = fisica.getPessoa();
    }

    public void cardJuridica(Integer pessoa_id) {
        close();
        JuridicaDao juridicaDB = new JuridicaDao();
        juridica = (Juridica) juridicaDB.pesquisaJuridicaPorPessoa(pessoa_id);
        if (juridica == null) {
            juridica = (Juridica) new Dao().find(new Juridica(), pessoa_id);
        }
    }

    public void cardByIdJuridica(Integer juridica_id) {
        close();
        juridica = (Juridica) new Dao().find(new Juridica(), juridica_id);
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public String getImagemJuridica(String caminho) {
        String caminhoTemp = "/Cliente/" + getCliente() + "/" + caminho;
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/" + caminho);
        for (String imagensTipo1 : imagensTipo) {
            File file = new File(arquivo + "/" + juridica.getPessoa().getId() + "." + imagensTipo1);
            if (file.exists()) {
                return caminhoTemp + "/" + juridica.getPessoa().getId() + "." + imagensTipo1;
            }
        }
        return "";
    }

    public String getImagemJuridica(String caminho, int idPessoa) {
        String caminhoTemp = "/Cliente/" + getCliente() + "/" + caminho;
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/" + caminho);
        for (String imagensTipo1 : imagensTipo) {
            File file = new File(arquivo + "/" + idPessoa + "." + imagensTipo1);
            if (file.exists()) {
                return caminhoTemp + "/" + idPessoa + "." + imagensTipo1;
            }
        }
        return "";
    }

    public String getImagemFisica(String caminho) {
        String caminhoTemp = "/Cliente/" + getCliente() + "/" + caminho;
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/" + caminho);
        if (fisica != null && fisica.getPessoa().getId() != -1) {
            for (String imagensTipo1 : imagensTipo) {
                File file = new File(arquivo + "/" + fisica.getPessoa().getId() + "." + imagensTipo1);
                if (file.exists()) {
                    return caminhoTemp + "/" + fisica.getPessoa().getId() + "." + imagensTipo1;
                }
            }
        }
        return "";
    }

    public String getImagemFisica(String caminho, int idPessoa) {
        String caminhoTemp = "/Cliente/" + getCliente() + "/" + caminho;
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/" + caminho);
        for (String imagensTipo1 : imagensTipo) {
            File file = new File(arquivo + "/" + idPessoa + "." + imagensTipo1);
            if (file.exists()) {
                return caminhoTemp + "/" + idPessoa + "." + imagensTipo1;
            }
        }
        return "";
    }

    public String getImagemPessoa(String caminho) {
        String caminhoTemp = "/Cliente/" + getCliente() + "/" + caminho;
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/" + caminho);
        if (pessoa != null && pessoa.getId() != -1) {
            for (String imagensTipo1 : imagensTipo) {
                File file = new File(arquivo + "/" + pessoa.getId() + "." + imagensTipo1);
                if (file.exists()) {
                    return caminhoTemp + "/" + pessoa.getId() + "." + imagensTipo1;
                }
            }
        }
        return "";
    }

    public String getImagemPessoa(String caminho, int idPessoa) {
        String caminhoTemp = "/Cliente/" + getCliente() + "/" + caminho;
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/" + caminho);
        for (String imagensTipo1 : imagensTipo) {
            File file = new File(arquivo + "/" + idPessoa + "." + imagensTipo1);
            if (file.exists()) {
                return caminhoTemp + "/" + idPessoa + "." + imagensTipo1;
            }
        }
        return "";
    }

    public String[] getImagensTipo() {
        return imagensTipo;
    }

    public void setImagensTipo(String[] imagensTipo) {
        this.imagensTipo = imagensTipo;
    }

    public String getCliente() {
        if (GenericaSessao.exists("sessaoCliente")) {
            cliente = GenericaSessao.getString("sessaoCliente");
        }
        return cliente;
    }

    public void close() {
        juridica = new Juridica();
        fisica = new Fisica();
        pessoa = new Pessoa();
        pessoaEndereco = new PessoaEndereco();
        pessoaEmpresa = new PessoaEmpresa();
        usuario = null;
        listPermissaoUsuario = new ArrayList();
    }

    public PessoaEndereco getPessoaEndereco() {
        if (pessoaEndereco.getId() == -1) {
            PessoaEnderecoDao db = new PessoaEnderecoDao();
            if (fisica != null && fisica.getId() != -1) {
                pessoaEndereco = (PessoaEndereco) db.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 4);
            } else if (juridica != null && juridica.getId() != -1) {
                pessoaEndereco = (PessoaEndereco) db.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 4);
            } else if (pessoa != null && pessoa.getId() != -1) {
                pessoaEndereco = (PessoaEndereco) db.pesquisaEndPorPessoaTipo(pessoa.getId(), 4);
            }
            if (pessoaEndereco == null) {
                pessoaEndereco = new PessoaEndereco();
            }
        }
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        if (pessoaEmpresa.getId() == -1) {
            PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
            if (fisica != null && fisica.getId() != -1) {
                pessoaEmpresa = (PessoaEmpresa) pessoaEmpresaDB.pesquisaPessoaEmpresaPorFisica(fisica.getId());
            } else if (pessoa != null && pessoa.getId() != -1) {
                pessoaEmpresa = (PessoaEmpresa) pessoaEmpresaDB.pesquisaPessoaEmpresaPorPessoa(pessoa.getId());
            }
        }
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public String enviaEmail(int idPessoa) throws IOException {
        String urlDestino = ((HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest())).getRequestURI();
        ChamadaPaginaBean chamadaPaginaBean = new ChamadaPaginaBean();
        urlDestino = chamadaPaginaBean.converteURL(urlDestino);
        return enviaEmail(idPessoa, urlDestino);
    }

    public String enviaEmail(int idPessoa, String urlRetorno) throws IOException {
        Dao dao = new Dao();
        Pessoa p = (Pessoa) dao.find(new Pessoa(), idPessoa);
        if (p == null || p.getEmail1().isEmpty()) {
            return null;
        }
        EmailBean emailBean = new EmailBean();
        emailBean.destroy();
        emailBean.init();
        emailBean.newMessage();
        emailBean.getEmail().setAssunto("Contato");
        emailBean.getEmail().setRotina((Rotina) dao.find(new Rotina(), 112));
        emailBean.getEmail().setMensagem("Prezada Sr(a) " + p.getNome());
        emailBean.setEmailPessoa(new EmailPessoa(-1, emailBean.getEmail(), p, p.getEmail1(), "", "", null, DataHoje.livre(new Date(), "H:m")));
        emailBean.addEmail();
        emailBean.setUrlRetorno(urlRetorno);
        GenericaSessao.put("emailBean", emailBean);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisa("email");
    }

    public String getStatusJuridica() {
        return status(juridica.getPessoa());
    }

    public String getStatusFisica() {
        return status(fisica.getPessoa());
    }

    public String getStatusPessoa() {
        return status(pessoa);
    }

    public String getContribuinteStatus() {
        return situacao(pessoa);
    }

    public String getStatusJuridicaPorPessoaEmpresa() {
        return status(pessoaEmpresa.getJuridica().getPessoa());
    }

    public String getStatusFisicaPorPessoaEmpresa() {
        return status(pessoaEmpresa.getFisica().getPessoa());
    }

    public String status(Pessoa p) {
        MovimentoDao movimentoDB = new MovimentoDao();
        if (p.getId() != -1) {
            if (movimentoDB.existeDebitoPessoa(pessoa, DataHoje.dataHoje())) {
                return "EM DÉBITO";
            } else {
                return "REGULAR";
            }
        }
        return "";
    }

    public String situacao(Pessoa p) {
        try {
            JuridicaDao juridicaDB = new JuridicaDao();
            if (juridicaDB.empresaInativa(p.getId())) {
                return "CONTRIBUINTE INATIVO";
            } else {
                return "CONTRIBUINTE";
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return "ERRO!";
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<PermissaoUsuario> getListPermissaoUsuario() {
        return listPermissaoUsuario;
    }

    public void setListPermissaoUsuario(List<PermissaoUsuario> listPermissaoUsuario) {
        this.listPermissaoUsuario = listPermissaoUsuario;
    }

}
