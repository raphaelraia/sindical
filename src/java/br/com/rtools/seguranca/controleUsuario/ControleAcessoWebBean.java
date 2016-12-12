package br.com.rtools.seguranca.controleUsuario;

import br.com.rtools.arrecadacao.Empregados;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.endereco.Bairro;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.DescricaoEndereco;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.Logradouro;
import br.com.rtools.endereco.dao.EnderecoDao;
import br.com.rtools.pessoa.Cnae;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.JuridicaReceita;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.Porte;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.CnaeDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.TipoEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.sistema.ConfiguracaoCnpj;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.JuridicaReceitaJSON;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.PesquisaCNPJ;
import br.com.rtools.utilitarios.SelectTranslate;
import br.com.rtools.utilitarios.ValidaDocumentos;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

@ManagedBean
@SessionScoped
public class ControleAcessoWebBean implements Serializable {

    private Registro registro = new Registro();
    private Pessoa pessoa = new Pessoa();
    private Pessoa pessoaContribuinte = null;
    private Pessoa pessoaContabilidade = null;
    private Pessoa pessoaPatronal = null;
    private Juridica empresa = new Juridica();
    private String login = "";
    private String status = "";
    private String strTipoPesquisa = "cnpj";
    private String descPesquisa = "";
    private String msgEmail = "";
    private String msgNovaSenha = "";
    private String verificaSenha = "";
    private String comoPesquisa = "";
    private String novaSenha = "";
    private String confirmaSenha = "";
    private String email = "";
    private String msgLoginInvalido = "";
    private boolean renEsqueci = false;
    private List listaEmpresas = new ArrayList();
    private int idJuridica = 0;
    private String link = "";
    private boolean tipoLink = false;

    private String documento = "";
    private Empregados empregados = new Empregados();
    private String alteraLogin = "";
    private String alteraSenha = "";
    private String alteraSenha2 = "";

    private PesquisaCNPJ pesquisaCNPJ = new PesquisaCNPJ();

    private List<SelectItem> listaDocumentoAcesso = new ArrayList();
    private Integer indexDocumentoAcesso = 0;

    public ControleAcessoWebBean() {
        loadListaDocumentoAcesso();
    }

    public final void loadListaDocumentoAcesso() {
        listaDocumentoAcesso.clear();
        indexDocumentoAcesso = 0;

        Registro r = getRegistro();

        if (r.isAcessoWebDocumento()) {
            Dao dao = new Dao();
            List<TipoDocumento> l = new ArrayList();//new Dao().list(new TipoDocumento());
            l.add((TipoDocumento) dao.find(new TipoDocumento(), 2));
            l.add((TipoDocumento) dao.find(new TipoDocumento(), 1));
            l.add((TipoDocumento) dao.find(new TipoDocumento(), 3));

            for (TipoDocumento tp : l) {
                if (r.isAcessoWebDocumentoCPF() && tp.getId() == 1) {
                    listaDocumentoAcesso.add(new SelectItem(listaDocumentoAcesso.size(), tp.getDescricao(), Integer.toString(tp.getId())));
                }
                if (r.isAcessoWebDocumentoCNPJ() && tp.getId() == 2) {
                    listaDocumentoAcesso.add(new SelectItem(listaDocumentoAcesso.size(), tp.getDescricao(), Integer.toString(tp.getId())));
                }
                if (r.isAcessoWebDocumentoCEI() && tp.getId() == 3) {
                    listaDocumentoAcesso.add(new SelectItem(listaDocumentoAcesso.size(), tp.getDescricao(), Integer.toString(tp.getId())));
                }
            }
            
            if(listaDocumentoAcesso.isEmpty()){
                listaDocumentoAcesso.add(new SelectItem(listaDocumentoAcesso.size(), "INVÁLIDO", "99"));
            }
        }
    }

    public String maskDocumento() {
        switch (Integer.valueOf(listaDocumentoAcesso.get(indexDocumentoAcesso).getDescription())) {
            case 1:
                return "999.999.999-99";
            case 2:
                return "99.999.999/9999-99";
            case 3:
                return "99.999.99999/99";
        }
        return "";
    }

    public void acaoPesquisarCNPJ() {
        try {
            pesquisaCNPJ.setCaptcha("");
            HashMap hash = pesquisaCNPJ.pesquisar();
            if ((Boolean) hash.get("status")) {
                PF.openDialog("dlg_confirma_pesquisa_cpnj");
                PF.update("formLogin:dlg_confirma_pesquisa_cpnj");
            } else {
                GenericaMensagem.error("Erro", hash.get("mensagem").toString());
            }
        } catch (Exception e) {
            GenericaMensagem.error("Erro", "Não foi possível Pesquisar CNPJ, contate o administrador!");
        }
    }

    public void validaEmpregados() {
        if (pessoaContribuinte.getEmail1().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Informe o EMAIL da Empresa para entrar no Sistema!");
            return;
        }

        if (pessoaContribuinte.getTelefone1().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Informe o TELEFONE da Empresa para entrar no Sistema!");
            return;
        }

        PF.openDialog("dlg_empregados_confirma");
        PF.update("i_panel_quantidade");
    }

    public Pessoa getPessoaContribuinte() {
        return pessoaContribuinte;
    }

    public void setPessoaContribuinte(Pessoa pessoaContribuinte) {
        this.pessoaContribuinte = pessoaContribuinte;
    }

    public Pessoa getPessoaContabilidade() {
        return pessoaContabilidade;
    }

    public void setPessoaContabilidade(Pessoa pessoaContabilidade) {
        this.pessoaContabilidade = pessoaContabilidade;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public JuridicaReceita pesquisaNaReceitaWeb(Boolean confirmar, String documento_pesquisa) {
        Dao dao = new Dao();
        ConfiguracaoCnpj conf_cnpj = (ConfiguracaoCnpj) dao.find(new ConfiguracaoCnpj(), 1);

        if (conf_cnpj == null) {
            GenericaMensagem.error("Atenção", "Configuração não encontrada, contate o administrador!");
            return null;
        }

        if (!conf_cnpj.getWeb()) {
            GenericaMensagem.error("Atenção", "Empresa não encontrada, e sem acesso a Pesquisar na Receita!");
            return null;
        }

        JuridicaDao dbj = new JuridicaDao();
        List listDocumento = dbj.pesquisaJuridicaPorDoc(documento_pesquisa);

        for (int i = 0; i < listDocumento.size(); i++) {
            if (!listDocumento.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Empresa já esta cadastrada no Sistema!");
                return null;
            }
        }

        PessoaDao db = new PessoaDao();

        String documento_extrair = AnaliseString.extrairNumeros(documento_pesquisa);

        JuridicaReceita juridicaReceita = db.pesquisaJuridicaReceita(documento_extrair);
        if (juridicaReceita.getPessoa() != null && juridicaReceita.getPessoa().getId() != -1) {
            GenericaMensagem.warn("Atenção", "Pessoa já cadastrada no Sistema!");
            return null;
        }

        pesquisaCNPJ.setCnpj(documento_extrair);

        if (conf_cnpj.getTipoPesquisaCnpj().getId() == 3 && !confirmar && juridicaReceita.getId() == -1) {
            acaoPesquisarCNPJ();
            return null;
        }

        JuridicaReceitaJSON.JuridicaReceitaObject jro;
        if (juridicaReceita.getId() == -1) {
            // tipo = "wooki" = pago / "gratis" = gratis / "padrao" = desenvolvido pelo sistema
            // tipo = "wooki" e "gratis" (pesquisaCNPJ) = NULL, "padrao" passar o PesquisaCNPJ com o retorno do método pesquisar = true
            if (conf_cnpj.getTipoPesquisaCnpj().getId() == 3 && confirmar) {
                jro = new JuridicaReceitaJSON(pesquisaCNPJ).pesquisar();
                // ERRO AO PROCESSAR CAPTCHA
                if (jro.getStatus() == -2) {
                    GenericaMensagem.warn("Atenção", "Erro ao Pesquisar, contate o administrador: " + jro.getMsg());
                    return null;
                }

                if (jro.getStatus() == -3) {
                    GenericaMensagem.warn("Atenção", jro.getMsg());
                    return null;
                }
            } else if (conf_cnpj.getTipoPesquisaCnpj().getId() == 1) {
                jro = new JuridicaReceitaJSON(documento_extrair, "wooki").pesquisar();
            } else {
                jro = new JuridicaReceitaJSON(documento_extrair, "gratis").pesquisar();
            }

            // NULL É PORQUE DEU ERRO DESCONHECIDO
            if (jro == null) {
                GenericaMensagem.warn("Atenção", "Erro ao Pesquisar, contate o administrador");
                return null;
            }

            // SE NÃO ENCONTRAR NA WOOKI
            if (jro.getStatus() == -1) {
                // desabilitado por demostrar falhas
                GenericaMensagem.warn("Atenção", "Erro ao Pesquisar, contate o administrador: " + jro.getMsg());
                return null;
            }

            if (jro.getStatus() == 0) {
                juridicaReceita.setNome(jro.getNome_empresarial());
                juridicaReceita.setFantasia(jro.getTitulo_estabelecimento());
                juridicaReceita.setDocumento(documento_extrair);
                juridicaReceita.setCep(jro.getCep());
                juridicaReceita.setDescricaoEndereco(jro.getLogradouro());
                juridicaReceita.setBairro(jro.getBairro());
                juridicaReceita.setComplemento(jro.getComplemento());
                juridicaReceita.setNumero(jro.getNumero());
                juridicaReceita.setCnae(jro.getAtividade_principal());
                juridicaReceita.setPessoa(null);
                juridicaReceita.setStatus(jro.getSituacao_cadastral());
                juridicaReceita.setDtAbertura(DataHoje.converte(jro.getData_abertura()));
                juridicaReceita.setCnaeSegundario(jro.getAtividades_secundarias());
                juridicaReceita.setCidade(jro.getMunicipio());
                juridicaReceita.setUf(jro.getUf());
                juridicaReceita.setEmail(jro.getEmail_rf());
                juridicaReceita.setTelefone(jro.getTelefone_rf());

                dao.openTransaction();

                if (!dao.save(juridicaReceita)) {
                    GenericaMensagem.warn("Erro", "Erro ao Salvar pesquisa!");
                    dao.rollback();
                    return null;
                }

                dao.commit();
            }
        } else {
            // recarrego o jro porque no Object JuridicaReceita não contem os campos email1, email2, email3, telefone1, telefone2, telefone3, listaCnae e Endereco 
            jro = new JuridicaReceitaJSON(juridicaReceita).load();
        }

        if (jro == null) {
            return null;
        }

        dao.openTransaction();
        if (juridicaReceita.getPessoa() == null) {
            Pessoa pessoax = new Pessoa(
                    -1, juridicaReceita.getNome(), (TipoDocumento) dao.find(new TipoDocumento(), 2), "", "", DataHoje.data(), "", "", "", "", "", "", "", AnaliseString.mascaraCnpj(documento_extrair), "", "", DataHoje.dataHoje()
            );

            if (!dao.save(pessoax)) {
                GenericaMensagem.warn("Erro", "Erro ao Salvar pesquisa!");
                dao.rollback();
                return null;
            }
            juridicaReceita.setPessoa(pessoax);
            if (!dao.update(juridicaReceita)) {
                GenericaMensagem.warn("Erro", "Erro ao Salvar pesquisa!");
                dao.rollback();
                return null;
            }
        }

        Juridica juridica = new Juridica();

        juridica.setPessoa(juridicaReceita.getPessoa());
        juridica.setFantasia(juridicaReceita.getFantasia().toUpperCase());
        juridica.setDtAbertura(juridicaReceita.getDtAbertura());

        String nomeContabilidade = "";
        if (!jro.getEmail1().isEmpty()) {
            juridica.setContabilidade(dbj.pesquisaContabilidadePorEmail(jro.getEmail1()));
            if (juridica.getContabilidade() != null) {
                nomeContabilidade = juridica.getContabilidade().getPessoa().getNome();
            }
        }

        juridica.getPessoa().setEmail1(jro.getEmail1());
        juridica.getPessoa().setEmail2(jro.getEmail2());
        juridica.getPessoa().setEmail3(jro.getEmail3());

        juridica.getPessoa().setTelefone1(jro.getTelefone1());
        juridica.getPessoa().setTelefone2(jro.getTelefone2());
        juridica.getPessoa().setTelefone3(jro.getTelefone3());

        if (jro.getLista_cnae().isEmpty()) {
            GenericaMensagem.warn("Erro", "Erro ao pesquisar CNAE");
            dao.rollback();
            return null;
        }

        // SE CNAE SECUNDÁRIO FOR VAZIO
        //if (jro.getLista_cnae_secundario().isEmpty()) {
        if (1 == 1) {
            juridica.setCnae(jro.getLista_cnae().get(0));

            CnaeConvencaoDao dbCnaeCon = new CnaeConvencaoDao();
            if (dbCnaeCon.pesquisaCnaeComConvencao(juridica.getCnae().getId()) != null) {
                // CNAE CONTRIBUINTE
            } else {
                // CNAE NÃO ESTA NA CONVENÇÃO
                if (juridica.getCnae().getId() == 1) {
                    GenericaMensagem.warn("Atenção", "CONTABILIDADE, entre com o CNPJ da Empresa!");
                } else {
                    GenericaMensagem.warn("Atenção", "Empresa não pertence a esta entidade!");
                }
                dao.rollback();
                return null;
            }
        } else {
            // TRAZ LISTA DE CNAES PARA SELECIONAR, IGUAL AO PESSOA JURÍDICA
            // retornaCnaeListReceita(jro.getLista_cnae().get(0), jro.getLista_cnae_secundario());
        }

        Endereco endereco = jro.getEndereco();
        List<PessoaEndereco> lista_pessoa_endereco = new ArrayList();
        if (endereco != null) {
            for (PessoaEndereco pe : jro.getPessoaEndereco()) {
                pe.setPessoa(juridica.getPessoa());
                lista_pessoa_endereco.add(pe);
            }
        } else {
            String msg = "Endereço não encontrado no Sistema - CEP: " + juridicaReceita.getCep() + " DESC: " + juridicaReceita.getDescricaoEndereco() + " BAIRRO: " + juridicaReceita.getBairro();
            GenericaMensagem.warn("Atenção", msg);
        }

        juridica.setPorte((Porte) dao.find(new Porte(), 1));
        if (!dao.save(juridica)) {
            GenericaMensagem.warn("Erro", "Não foi possível salvar EMPRESA, tente novamente!");
            dao.rollback();
            return null;
        }

        for (PessoaEndereco listapex : lista_pessoa_endereco) {
            if (!dao.save(listapex)) {
                GenericaMensagem.warn("Erro", "Não foi possível salvar ENDEREÇO, tente novamente!");
                dao.rollback();
                return null;
            }
        }

        dao.commit();

        return juridicaReceita;
    }

    public JuridicaReceita pesquisaNaReceitaWeb(String documentox) {
        PessoaDao db = new PessoaDao();
        JuridicaReceita jr = db.pesquisaJuridicaReceita(documentox);
        Dao dao = new Dao();
        if (jr.getId() == -1) {
            try {
                ConfiguracaoCnpj configuracaoCnpj = (ConfiguracaoCnpj) dao.find(new ConfiguracaoCnpj(), 1);
                URL url = null;
                if (configuracaoCnpj == null) {
                    url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documentox + "&dias=" + configuracaoCnpj.getDias() + "&usuario=rogerio@rtools.com.br&senha=989899");
                } else if (configuracaoCnpj.getEmail().isEmpty() || configuracaoCnpj.getSenha().isEmpty()) {
                    url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documentox + "&dias=" + configuracaoCnpj.getDias() + "&usuario=rogerio@rtools.com.br&senha=989899");
                } else {
                    url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documentox + "&dias=" + configuracaoCnpj.getDias() + "&usuario=" + configuracaoCnpj.getEmail() + "&senha=" + configuracaoCnpj.getSenha());
                }

                //URL url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero="+documentox+"&usuario=rogerio@rtools.com.br&senha=989899");
                //URL url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=00000000000191&usuario=teste@wooki.com.br&senha=teste");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String str = in.readLine();
                    JSONObject obj = new JSONObject(str);
                    int status = obj.getInt("status");
                    String error = obj.getString("msg");

                    if (status == 6) {
                        GenericaMensagem.warn("Atenção", "Limite de acessos excedido!");
                        return null;
                    }

                    if (status == 1) {
                        GenericaMensagem.info("Atenção", "Atualizando esse CNPJ na receita, pesquise novamente em 30 segundos!");
                        return null;
                    }

                    if (status != 0) {
                        GenericaMensagem.error("Erro", error);
                        return null;
                    }

                    jr.setNome(obj.getString("nome_empresarial"));
                    jr.setFantasia(obj.getString("titulo_estabelecimento"));
                    jr.setDocumento(documentox);
                    jr.setCep(AnaliseString.mascaraCep(obj.getString("cep")));
                    jr.setDescricaoEndereco(obj.getString("logradouro"));
                    jr.setBairro(obj.getString("bairro"));
                    jr.setComplemento(obj.getString("complemento"));
                    jr.setNumero(obj.getString("numero"));
                    jr.setCnae(obj.getString("atividade_principal"));
                    jr.setPessoa(null);
                    jr.setStatus(obj.getString("situacao_cadastral"));
                    jr.setDtAbertura(DataHoje.converte(obj.getString("data_abertura")));
                    jr.setCnaeSegundario(obj.getString("atividades_secundarias"));
                    jr.setCidade(obj.getString("municipio"));
                    jr.setUf(obj.getString("uf"));
                    jr.setEmail(obj.getString("email_rf"));
                    jr.setTelefone(obj.getString("telefone_rf"));

                    Dao di = new Dao();
                    di.openTransaction();
                    if (!di.save(jr)) {
                        GenericaMensagem.warn("Erro", "Erro ao Salvar pesquisa!");
                        di.rollback();
                        return null;
                    }
                    di.commit();
                }
            } catch (IOException | JSONException e) {
                GenericaMensagem.warn("Erro", e.getMessage());
                return null;
            }
        }

        Dao di = new Dao();
        di.openTransaction();
        if (jr.getPessoa() == null) {
            Pessoa pessoax = new Pessoa(
                    -1, jr.getNome(), (TipoDocumento) di.find(new TipoDocumento(), 2), "", "", DataHoje.data(), "", "", "", "", "", "", "", AnaliseString.mascaraCnpj(documentox), "", "", DataHoje.dataHoje()
            );

            if (!di.save(pessoax)) {
                GenericaMensagem.warn("Erro", "Erro ao Salvar pesquisa!");
                di.rollback();
                return null;
            }
            jr.setPessoa(pessoax);
            di.update(jr);
        }

        Juridica juridica = new Juridica();
        jr.getPessoa().setNome(jr.getPessoa().getNome().toUpperCase());
        juridica.setPessoa(jr.getPessoa());
        juridica.setFantasia(jr.getNome().toUpperCase());

        String emails[] = (jr.getEmail() == null) ? "".split("") : jr.getEmail().toLowerCase().split(" ");
        String telefones[] = (jr.getTelefone() == null) ? "".split("") : jr.getTelefone().split(" / ");

        JuridicaDao dbj = new JuridicaDao();
        if (!emails[0].isEmpty()) {
            juridica.setContabilidade(dbj.pesquisaContabilidadePorEmail(emails[0]));
        }

        switch (emails.length) {
            case 1:
                juridica.getPessoa().setEmail1(emails[0]);
                break;
            case 2:
                juridica.getPessoa().setEmail1(emails[0]);
                juridica.getPessoa().setEmail2(emails[1]);
                break;
            case 3:
                juridica.getPessoa().setEmail1(emails[0]);
                juridica.getPessoa().setEmail2(emails[1]);
                juridica.getPessoa().setEmail3(emails[2]);
                break;
        }

        switch (telefones.length) {
            case 1:
                juridica.getPessoa().setTelefone1(telefones[0]);
                break;
            case 2:
                juridica.getPessoa().setTelefone1(telefones[0]);
                juridica.getPessoa().setTelefone2(telefones[1]);
                break;
            case 3:
                juridica.getPessoa().setTelefone1(telefones[0]);
                juridica.getPessoa().setTelefone2(telefones[1]);
                juridica.getPessoa().setTelefone3(telefones[2]);
                break;
        }

        String result[] = jr.getCnae().split(" ");
        CnaeDao dbc = new CnaeDao();
        String cnaex = result[result.length - 1].replace("(", "").replace(")", "");
        //List<Cnae> listac = dbc.pesquisaCnae(result[0], "cnae", "I");
        List<Cnae> listac = dbc.pesquisaCnae(cnaex, "cnae", "I");

        if (listac.isEmpty()) {
            listac = dbc.pesquisaCnae(result[0], "cnae", "I");
            if (listac.isEmpty()) {
                GenericaMensagem.warn("Erro", "Erro ao pesquisar CNAE");
                di.rollback();
                return null;
            }
        }

        CnaeConvencaoDao dbCnaeCon = new CnaeConvencaoDao();
        if (dbCnaeCon.pesquisaCnaeComConvencao(((Cnae) listac.get(0)).getId()) != null) {
            juridica.setCnae((Cnae) listac.get(0));
            // CNAE CONTRIBUINTE
        } else {
            // CNAE NÃO ESTA NA CONVENÇÃO
            if (((Cnae) listac.get(0)).getId() == 1) {
                GenericaMensagem.warn("Atenção", "CONTABILIDADE, entre com o CNPJ da Empresa!");
            } else {
                GenericaMensagem.warn("Atenção", "Empresa não pertence a esta entidade!");
            }
            di.rollback();
            return null;
        }

        PessoaEnderecoDao dbe = new PessoaEnderecoDao();

        String cep = jr.getCep();
        cep = cep.replace(".", "").replace("-", "");

        String descricao[] = AnaliseString.removerAcentos(jr.getDescricaoEndereco()).split(" ");
        String bairros[] = AnaliseString.removerAcentos(jr.getBairro()).split(" ");

        Endereco endereco = dbe.enderecoReceita(cep, descricao, bairros);
        List<PessoaEndereco> listape = new ArrayList();

        if (endereco == null) {
            SelectTranslate st = new SelectTranslate();

            List<Bairro> lbairro = st.select(new Bairro()).where("ds_descricao", jr.getBairro()).find();
            Bairro bx;

            if (lbairro.isEmpty()) {
                bx = new Bairro(-1, jr.getBairro(), false);

                if (!di.save(bx)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível salvar o Bairro, tente novamente!");
                    return null;
                }
            } else {
                bx = lbairro.get(0);
            }

            List<DescricaoEndereco> ldescricao = st.select(new DescricaoEndereco()).where("ds_descricao", jr.getDescricaoEndereco()).find();
            DescricaoEndereco dex;

            if (ldescricao.isEmpty()) {
                dex = new DescricaoEndereco(-1, jr.getDescricaoEndereco(), false);

                if (!di.save(dex)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível salvar o Descrição, tente novamente!");
                    return null;
                }
            } else {
                dex = ldescricao.get(0);
            }

            List<Cidade> lcidade = st.select(new Cidade()).where("ds_cidade", jr.getCidade()).find();
            Cidade cx;

            if (lcidade.isEmpty()) {
                cx = new Cidade(-1, jr.getCidade(), jr.getUf());

                if (!di.save(cx)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível salvar a Cidade, tente novamente!");
                    return null;
                }
            } else {
                cx = lcidade.get(0);
            }

            EnderecoDao dbx = new EnderecoDao();
            List<Endereco> le = dbx.pesquisaEnderecoCep(cep);

            if (le.isEmpty()) {
                endereco = new Endereco(-1, cx, bx, (Logradouro) di.find(new Logradouro(), 0), dex, cep, "", false);
//                di.rollback();
//                GenericaMensagem.error("Erro", "CEP não encontrado no sistema, contate seu Sindicato!");
//                return null;
            } else {
                endereco = new Endereco(-1, le.get(0).getCidade(), bx, le.get(0).getLogradouro(), dex, cep, "", false);
            }

            if (!di.save(endereco)) {
                di.rollback();
                GenericaMensagem.error("Erro", "Não foi possível salvar o Endereço, tente novamente!");
                return null;
            }
        }

        if (endereco != null) {
            TipoEnderecoDao dbt = new TipoEnderecoDao();
            List tiposE = dbt.listaTipoEnderecoParaJuridica();
            for (int i = 0; i < tiposE.size(); i++) {
                PessoaEndereco pessoaEndereco = new PessoaEndereco();
                pessoaEndereco.setEndereco(endereco);
                pessoaEndereco.setTipoEndereco((TipoEndereco) tiposE.get(i));
                pessoaEndereco.setPessoa(juridica.getPessoa());
                pessoaEndereco.setNumero(jr.getNumero());
                pessoaEndereco.setComplemento(jr.getComplemento());
                listape.add(pessoaEndereco);

            }
        } else {
            String msg = "Endereço não encontrado no Sistema - CEP: " + jr.getCep() + " DESC: " + jr.getDescricaoEndereco() + " BAIRRO: " + jr.getBairro();
            GenericaMensagem.warn("Erro", msg);

        }

        juridica.setPorte((Porte) di.find(new Porte(), 1));
        if (!di.save(juridica)) {
            GenericaMensagem.warn("Erro", "Não foi possível salvar EMPRESA, tente novamente!");
            di.rollback();
            return null;
        }

        for (PessoaEndereco listapex : listape) {
            if (!di.save(listapex)) {
                GenericaMensagem.warn("Erro", "Não foi possível salvar ENDEREÇO, tente novamente!");
                di.rollback();
                return null;
            }
        }

        di.commit();
        return jr;
    }

    public String salvarEmpregados() {
        Dao di = new Dao();

        di.openTransaction();
        JuridicaDao db = new JuridicaDao();

        empregados.setJuridica(db.pesquisaJuridicaPorPessoa(pessoaContribuinte.getId()));
        empregados.setReferencia(DataHoje.data().substring(3));

        if (!di.save(empregados)) {
            GenericaMensagem.error("Erro", "Não foi possível salvar quantidade, tente novamente!");
            di.rollback();
            return null;
        }

        if (!di.update(pessoaContribuinte)) {
            GenericaMensagem.error("Erro", "Não foi possível alterar cadastro, tente novamente!");
            di.rollback();
            return null;
        }

        di.commit();

        status = "Contribuinte";
        login = pessoaContribuinte.getNome() + " - " + pessoaContribuinte.getTipoDocumento().getDescricao() + " : " + pessoaContribuinte.getDocumento() + " ( " + status + " )";

        GenericaSessao.put("sessaoUsuarioAcessoWeb", pessoaContribuinte);
        GenericaSessao.put("linkClicado", true);
        GenericaSessao.put("userName", pessoaContribuinte.getDocumento());
        GenericaSessao.put("indicaAcesso", "web");
        return "menuPrincipalAcessoWeb";
    }

    public String validacaoDocumento(Boolean confirma_pesquisa_receita) throws IOException {
        if (documento.isEmpty()) {
            GenericaMensagem.error("Login Inválido", "Digite um Documento válido!");
            PF.update("formLogin");
            return null;
        }

        String documentox = AnaliseString.extrairNumeros(documento);

        if (Integer.valueOf(listaDocumentoAcesso.get(indexDocumentoAcesso).getDescription()) == 2) {
            if (!ValidaDocumentos.isValidoCNPJ(documentox)) {
                GenericaMensagem.warn("Erro", "CNPJ Inválido!");
                PF.update("formLogin");
                return null;
            }
        } else if (Integer.valueOf(listaDocumentoAcesso.get(indexDocumentoAcesso).getDescription()) == 1) {
            if (!ValidaDocumentos.isValidoCPF(documentox)) {
                GenericaMensagem.warn("Erro", "CPF Inválido!");
                PF.update("formLogin");
                return null;
            }
        } else if (Integer.valueOf(listaDocumentoAcesso.get(indexDocumentoAcesso).getDescription()) == 99) {
                GenericaMensagem.warn("Erro", "Lista Inválida!");
                PF.update("formLogin");
                return null;
        } else {
            // CEI
        }

        JuridicaDao db = new JuridicaDao();
        List<Juridica> listDocumento = db.pesquisaJuridicaPorDoc(documento);

        if (!listDocumento.isEmpty() && listDocumento.size() > 1) {
            GenericaMensagem.warn("Atenção", "Documento Inválido, contate seu Sindicato!");
            PF.update("formLogin");
            return null;
        }

        Juridica juridica = null;
        UsuarioDao dbu = new UsuarioDao();

        // SE TER CADASTRO NO SISTEMA
        if (!listDocumento.isEmpty()) {
            juridica = listDocumento.get(0);
        } else if (Integer.valueOf(listaDocumentoAcesso.get(indexDocumentoAcesso).getDescription()) == 2) {
            // SE NÃO TER CADASTRO NO SISTEMA
            JuridicaReceita jr = pesquisaNaReceitaWeb(confirma_pesquisa_receita, documento);
            if (jr == null) {
                return null;
            }

            juridica = db.pesquisaJuridicaPorPessoa(jr.getPessoa().getId());
        } else {
            GenericaMensagem.warn("Atenção", "Documento Inválido, contate seu Sindicato!");
            PF.update("formLogin");
            return null;
        }

        pessoaPatronal = dbu.ValidaUsuarioPatronalWeb(juridica.getPessoa().getId());

        if (pessoaPatronal != null) {
            GenericaMensagem.info("Confirmação de Acesso PATRONAL", "Confirme seu Login e Senha!");
            PF.openDialog("dlg_patronal");
            return null;
        }

        pessoaContribuinte = dbu.ValidaUsuarioContribuinteWeb(juridica.getPessoa().getId());
        pessoaContabilidade = dbu.ValidaUsuarioContabilidadeWeb(juridica.getPessoa().getId());

        if (pessoaContribuinte == null && pessoaContabilidade != null) {
            GenericaMensagem.warn("Atenção", "CONTABILIDADE, entre com o CNPJ da Empresa!");
            return null;
        }

        List<Vector> listax = db.listaJuridicaContribuinte(juridica.getId());

        if (!listax.isEmpty()) {
            // 11 - DATA DE INATIVACAO
            if (listax.get(0).get(11) != null) {
                GenericaMensagem.warn("Atenção", "Empresa Inativa, contate seu Sindicato!");
                return null;
            }
        } else {
            GenericaMensagem.warn("Atenção", "Empresa não contribuinte, contate seu Sindicato!");
            return null;
        }

        empregados = db.pesquisaEmpregados(juridica.getId());

        if (empregados == null) {
            empregados = new Empregados();
            PF.update(":i_form_empregado");
            PF.openDialog("dlg_empregados");
            return null;
        }

        status = "Contribuinte";
        login = pessoaContribuinte.getNome() + " - " + pessoaContribuinte.getTipoDocumento().getDescricao() + " : " + pessoaContribuinte.getDocumento() + " ( " + status + " )";

        GenericaSessao.put("sessaoUsuarioAcessoWeb", pessoaContribuinte);
        GenericaSessao.put("linkClicado", true);
        GenericaSessao.put("userName", pessoaContribuinte.getDocumento());
        GenericaSessao.put("indicaAcesso", "web");
        return "menuPrincipalAcessoWeb";
    }

    public String validacao() throws IOException {
        if (pessoa.getLogin().isEmpty()) {
            GenericaMensagem.error("Login Inválido", "Digite um LOGIN válido!");
            return null;
        }

        if (pessoa.getSenha().isEmpty()) {
            GenericaMensagem.error("Login Inválido", "Digite uma SENHA válida!");
            return null;
        }

        if (pessoa.getLogin().equals("contribuinte") && pessoa.getSenha().equals("sindical")) {
            pessoa = new PessoaDao().contribuinteRandon();
        }

        if (pessoa.getLogin().equals("contabilidade") && pessoa.getSenha().equals("sindical")) {
            pessoa = new PessoaDao().contabilidadeRandon();
        }

        String pagina = null;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("indicaAcesso", "web");
        UsuarioDao db = new UsuarioDao();
        pessoa = db.ValidaUsuarioWeb(pessoa.getLogin(), pessoa.getSenha());
        if (pessoa != null) {
            pessoaContribuinte = db.ValidaUsuarioContribuinteWeb(pessoa.getId());
            pessoaContabilidade = db.ValidaUsuarioContabilidadeWeb(pessoa.getId());
            if (pessoaContribuinte != null && pessoaContabilidade == null) {
                JuridicaDao dbj = new JuridicaDao();
                List listax = dbj.listaJuridicaContribuinte(dbj.pesquisaJuridicaPorPessoa(pessoaContribuinte.getId()).getId());
                if (listax.isEmpty()) {
                    //msgLoginInvalido = "Usuário não contribuinte!";
                    GenericaMensagem.error("Login Inválido", "Usuário não Contribuinte");
                    return null;
                } else if (((List) listax.get(0)).get(11) != null) {
                    //msgLoginInvalido = "Contribuinte inativo, contate seu sindicato!";
                    GenericaMensagem.error("Login Inválido", "Contribuinte inativo, contate seu Sindicato!");
                    return null;
                } else {
                }
            }
            pessoaPatronal = db.ValidaUsuarioPatronalWeb(pessoa.getId());
        } else {
            pessoaContribuinte = null;
            pessoaContabilidade = null;
            pessoaPatronal = null;
        }

        if ((pessoaContribuinte != null) && (pessoaContabilidade != null) && pessoaPatronal == null) {
            status = "Contribuinte - Contabilidade";
        } else if (pessoaContribuinte != null && pessoaPatronal == null) {
            status = "Contribuinte";
        } else if (pessoaContabilidade != null && pessoaPatronal == null) {
            status = "Contabilidade";
        } else if (pessoaPatronal != null) {
            status = "Patronal";
        }

        if ((pessoaContribuinte != null) || (pessoaContabilidade != null) || (pessoaPatronal != null)) {
            pagina = "menuPrincipalAcessoWeb";
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sessaoUsuarioAcessoWeb", pessoa);
            if (pessoa.getTipoDocumento().getId() == 4) {
                login = pessoa.getNome() + " ( " + status + " )";
            } else {
                login = pessoa.getNome() + " - "
                        + pessoa.getTipoDocumento().getDescricao() + ": "
                        + pessoa.getDocumento() + " ( "
                        + status + " )";
            }
            if (pessoa != null) {
                GenericaSessao.put("userName", "WEB - " + pessoa.getLogin() + " (" + GenericaSessao.getString("sessaoCliente") + ")");
            }
            pessoa = new Pessoa();
//           pessoaContribuinte = new Pessoa();
//           pessoaContabilidade = new Pessoa();
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        } else {
            if (pessoa == null) {
                //msgLoginInvalido = "Usuário ou/e senha inválidos!";
                GenericaMensagem.error("Login Inválido", "Usuário ou/e senha Inválidos!");
            } else {
                //msgLoginInvalido = "Usuário não Contribuinte, ou Contabilidade sem Empresa!";
                GenericaMensagem.error("Login Inválido", "Usuário não Contribuinte, ou Contabilidade sem Empresa!");
            }
            if (pessoa != null) {
                GenericaSessao.put("userName", "WEB - " + pessoa.getLogin() + " (" + GenericaSessao.getString("sessaoCliente") + ")");
            }
            pessoa = new Pessoa();
//           pessoaContribuinte = new Pessoa();
//           pessoaContabilidade = new Pessoa();
        }
        return pagina;
    }

    public void enviarEmail() {
        if (email.isEmpty()) {
            GenericaMensagem.warn("Erro", "Digite um Email para verificação!");
            return;
        }
        Juridica empresax = new Juridica();

        if (strTipoPesquisa.equals("nome")) {
            if (descPesquisa.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Digite o nome da Empresa para Pesquisar!");
                return;
            }

            if (getListaEmpresa().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Pesquise sua empresa para concluir a Solicitação!");
                return;
            }

            empresax = (Juridica) new Dao().find(new Juridica(), Integer.parseInt(getListaEmpresa().get(idJuridica).getDescription()));
            if (empresax == null) {
                GenericaMensagem.warn("Não foi possível completar seu pedido", "Empresa não encontrada no Sistema, Contate o seu Sindicato.");
                return;
            }
        } else {
            if (descPesquisa.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Digite o documento da Empresa!");
                return;
            }

            JuridicaDao db = new JuridicaDao();
            List<Juridica> lista = db.pesquisaJuridicaPorDoc(descPesquisa);

            if (lista.isEmpty()) {
                GenericaMensagem.warn("Não foi possível completar seu pedido", "Empresa não encontrada no Sistema, Contate o seu Sindicato.");
                return;
            }
            empresax = (Juridica) lista.get(0);
        }

        if (!validaEmail(empresax)) {
            GenericaMensagem.warn("Não foi possível completar seu pedido", "E-mail digitado é Inválido!");
            return;
        }

        Dao di = new Dao();
        Mail mail = new Mail();
        mail.setFiles(new ArrayList());
        mail.setEmail(
                new Email(
                        -1,
                        DataHoje.dataHoje(),
                        DataHoje.livre(new Date(), "HH:mm"),
                        (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                        (Rotina) di.find(new Rotina(), 261),
                        null,
                        "Envio de Login e Senha",
                        "<h5><b>Login: </b> " + empresax.getPessoa().getLogin() + "</h5><br /> <h5><b>Senha: </b> " + empresax.getPessoa().getSenha() + "</h5>",
                        false,
                        false
                )
        );
        List<Pessoa> pessoas = new ArrayList();
        pessoas.add(empresax.getPessoa());

        List<EmailPessoa> emailPessoas = new ArrayList<EmailPessoa>();
        EmailPessoa emailPessoa = new EmailPessoa();
        for (Pessoa pe : pessoas) {
            emailPessoa.setDestinatario(pe.getEmail1());
            emailPessoa.setPessoa(pe);
            emailPessoa.setRecebimento(null);
            emailPessoas.add(emailPessoa);
            mail.setEmailPessoas(emailPessoas);
            emailPessoa = new EmailPessoa();
        }

        String[] retorno = mail.send("personalizado");

        if (!retorno[1].isEmpty()) {
            GenericaMensagem.error("Erro", retorno[1]);
        } else {
            GenericaMensagem.info("Confirmação", "Seu LOGIN e SENHA foram enviados para o email cadastrado!");
        }
    }

    public boolean validaEmail(Juridica emp) {
        if (emp.getPessoa().getEmail1() != null) {
            if (emp.getPessoa().getEmail1().equals(email)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public List<SelectItem> getListaEmpresa() {
        if (listaEmpresas.isEmpty()) {
            int i = 0;
            JuridicaDao db = new JuridicaDao();
            List select = db.pesquisaPessoa(descPesquisa, "nome", comoPesquisa);
            while (i < select.size()) {
                listaEmpresas.add(new SelectItem(new Integer(i),
                        (String) ((Juridica) select.get(i)).getPessoa().getNome() + " - "
                        + (String) ((Juridica) select.get(i)).getPessoa().getTipoDocumento().getDescricao() + ": "
                        + (String) ((Juridica) select.get(i)).getPessoa().getDocumento(),
                        Integer.toString(((Juridica) select.get(i)).getId())));
                i++;
            }
        }
        return listaEmpresas;
    }

    public String salvarConf() {
        if (!novaSenha.equals("")) {
            pessoa = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb");
            if (pessoa.getId() != -1) {
                if (verificaSenha.equals(pessoa.getSenha()) && confirmaSenha.equals(novaSenha)) {
                    pessoa.setSenha(novaSenha);
                    if (new Dao().update(pessoa, true)) {
                        msgNovaSenha = "Atualizado com sucesso!";
                    } else {
                        msgNovaSenha = "Erro ao Atualizar Senha!";
                    }
                } else {
                    msgNovaSenha = "Senha de verificação inválida!";
                }
            }
        } else {
            msgNovaSenha = "Digite uma Senha para ser atualizado!";
        }
        pessoa = new Pessoa();
        return null;
    }

    public void inicial() {
        comoPesquisa = "I";
        listaEmpresas = new ArrayList();
    }

    public void parcial() {
        comoPesquisa = "P";
        listaEmpresas = new ArrayList();
    }

    public String esqueciASenha() {
        if (renEsqueci) {
            pessoa = new Pessoa();
            pessoaContribuinte = null;
            pessoaContabilidade = null;
            empresa = new Juridica();
            login = "";
            status = "";
            strTipoPesquisa = "cnpj";
            descPesquisa = "";
            msgEmail = "";
            msgNovaSenha = "";
            verificaSenha = "";
            comoPesquisa = "";
            msgNovaSenha = "";
            email = "";
            listaEmpresas = new ArrayList();
            idJuridica = 0;
            renEsqueci = false;
        } else {
            renEsqueci = true;
        }
        return "indexAcessoWeb";
    }

    public String getLinkSite() {
        Pessoa p = (Pessoa) new Dao().find(new Pessoa(), 1);
        if (p != null) {
            if (p.getSite() != null) {
                return p.getSite();
            }
        }
        return "";
    }

    public void sairSistemaWeb() throws IOException {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb") != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("sessaoUsuarioAcessoWeb");
        }
        String retorno = "";
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente") != null) {
            //retorno = "indexAcessoWeb.jsf?cliente=" + (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente");
            retorno = "web/" + (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente");
        }
        limparSessaoAcessoWeb();
        FacesContext.getCurrentInstance().getExternalContext().redirect(retorno);
    }

    public void limparSessaoAcessoWeb() {
        FacesContext conext = FacesContext.getCurrentInstance();
        //Verifica a sessao e a grava na variavel
        HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
        //Fecha/Destroi sessao
        session.invalidate();
    }

    public String getStrTipoPesquisa() {
        return strTipoPesquisa;
    }

    public void setStrTipoPesquisa(String strTipoPesquisa) {
        this.strTipoPesquisa = strTipoPesquisa;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public String getMsgEmail() {
        return msgEmail;
    }

    public void setMsgEmail(String msgEmail) {
        this.msgEmail = msgEmail;
    }

    public boolean isRenEsqueci() {
        return renEsqueci;
    }

    public void setRenEsqueci(boolean renEsqueci) {
        this.renEsqueci = renEsqueci;
    }

    public int getIdJuridica() {
        return idJuridica;
    }

    public void setIdJuridica(int idJuridica) {
        this.idJuridica = idJuridica;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public String getMsgNovaSenha() {
        return msgNovaSenha;
    }

    public void setMsgNovaSenha(String msgNovaSenha) {
        this.msgNovaSenha = msgNovaSenha;
    }

    public String getVerificaSenha() {
        return verificaSenha;
    }

    public void setVerificaSenha(String verificaSenha) {
        this.verificaSenha = verificaSenha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmaSenha() {
        return confirmaSenha;
    }

    public void setConfirmaSenha(String confirmaSenha) {
        this.confirmaSenha = confirmaSenha;
    }

    public String getMsgLoginInvalido() {
        return msgLoginInvalido;
    }

    public void setMsgLoginInvalido(String msgLoginInvalido) {
        this.msgLoginInvalido = msgLoginInvalido;
    }

    public Pessoa getPessoaPatronal() {
        return pessoaPatronal;
    }

    public void setPessoaPatronal(Pessoa pessoaPatronal) {
        this.pessoaPatronal = pessoaPatronal;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean getTipoLink() {
        Pessoa p = (Pessoa) new Dao().find(new Pessoa(), 1);
        if (p != null) {
            tipoLink = !p.getSite().equals("");
            link = p.getSite();
        }
        return tipoLink;
    }

    public void setTipoLink(boolean tipoLink) {
        this.tipoLink = tipoLink;
    }

    public Registro getRegistro() {
        //if (registro.getId() == -1) {
        registro = (Registro) new Dao().liveSingle("SELECT r FROM Registro r WHERE r.id = 1");
        //}
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Empregados getEmpregados() {
        return empregados;
    }

    public void setEmpregados(Empregados empregados) {
        this.empregados = empregados;
    }

    // ALTERAR LOGIN
    public String getLoginAtual() {
        if (pessoaContribuinte != null) {
            return pessoaContribuinte.getLogin();
        }
        if (pessoaContabilidade != null) {
            return pessoaContabilidade.getLogin();
        }
        return "";
    }

    public String getAlteraLogin() {
        return alteraLogin;
    }

    public void setAlteraLogin(String alteraLogin) {
        this.alteraLogin = alteraLogin;
    }

    public String getAlteraSenha() {
        return alteraSenha;
    }

    public void setAlteraSenha(String alteraSenha) {
        this.alteraSenha = alteraSenha;
    }

    public String getAlteraSenha2() {
        return alteraSenha2;
    }

    public void setAlteraSenha2(String alteraSenha2) {
        this.alteraSenha2 = alteraSenha2;
    }

    public void updatePessoaWeb(String tcase) {
        Pessoa p = new Pessoa();
        switch (tcase) {
            case "login":
                if (alteraLogin.isEmpty()) {
                    GenericaMensagem.warn("Validação", "Informar login!");
                    return;
                }
                if (pessoaContribuinte != null) {
                    if (new PessoaDao().existLogin(alteraLogin)) {
                        GenericaMensagem.warn("Validação", "Login já existe!");
                        return;
                    }
                }
                if (pessoaContabilidade != null) {
                    if (new PessoaDao().existLogin(alteraLogin)) {
                        GenericaMensagem.warn("Validação", "Login já existe!");
                        return;
                    }
                }
                if (pessoaContribuinte != null) {
                    pessoaContribuinte.setLogin(alteraLogin);
                    p = pessoaContribuinte;
                }
                if (pessoaContabilidade != null) {
                    pessoaContabilidade.setLogin(alteraLogin);
                    p = pessoaContabilidade;
                }
                break;
            case "senha":
                if (alteraSenha.isEmpty()) {
                    GenericaMensagem.warn("Validação", "Informar senha!");
                    return;
                }
                if (alteraSenha.length() < 6 || alteraSenha.length() > 50) {
                    GenericaMensagem.warn("Validação", "A senha deve conter no mínimo 6 caracteres e no máximo 50 !");
                    return;
                }
                if (!alteraSenha.equals(alteraSenha2)) {
                    GenericaMensagem.warn("Validação", "A senha de confirmação esta diferente da senha!");
                    return;
                }
                if (pessoaContribuinte != null) {
                    pessoaContribuinte.setSenha(alteraSenha);
                    p = pessoaContribuinte;
                }
                if (pessoaContabilidade != null) {
                    pessoaContabilidade.setSenha(alteraSenha);
                    p = pessoaContabilidade;
                }
                break;
            default:
                return;
        }
        if (new Dao().update(p, true)) {
            if (pessoaContribuinte != null) {
                pessoaContribuinte = p;
            }
            if (pessoaContabilidade != null) {
                pessoaContabilidade = p;
            }
            switch (tcase) {
                case "login":
                    GenericaMensagem.info("Sucesso", "Login atualizado com sucesso! " + getLoginAtual());
                    break;
                case "senha":
                    GenericaMensagem.info("Sucesso", "Senha atualizada com sucesso");
                    break;
            }
        }
    }

    public boolean validaTipoDocumento(int idDoc, String docS) {
        // 1 cpf, 2 cnpj, 3 cei, 4 nenhum
        String documentox = docS.replace(".", "").replace("/", "").replace("-", "");

        boolean ye = false;
        if (idDoc == 1) {
            ye = ValidaDocumentos.isValidoCPF(documentox);
        }
        if (idDoc == 2) {
            ye = ValidaDocumentos.isValidoCNPJ(documentox);
        }
        if (idDoc == 3) {
            //ye = ValidaDocumentos.isValidoCEI(documento);
            ye = true;
        }
        if (idDoc == 4) {
            ye = true;
        }

        return ye;
    }

    public PesquisaCNPJ getPesquisaCNPJ() {
        return pesquisaCNPJ;
    }

    public void setPesquisaCNPJ(PesquisaCNPJ pesquisaCNPJ) {
        this.pesquisaCNPJ = pesquisaCNPJ;
    }

    public List<SelectItem> getListaDocumentoAcesso() {
        return listaDocumentoAcesso;
    }

    public void setListaDocumentoAcesso(List<SelectItem> listaDocumentoAcesso) {
        this.listaDocumentoAcesso = listaDocumentoAcesso;
    }

    public Integer getIndexDocumentoAcesso() {
        return indexDocumentoAcesso;
    }

    public void setIndexDocumentoAcesso(Integer indexDocumentoAcesso) {
        this.indexDocumentoAcesso = indexDocumentoAcesso;
    }

}
