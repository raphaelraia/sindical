package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.beans.MovimentosReceberSocialBean;
import br.com.rtools.associativo.beans.SociosBean;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.db.SociosDB;
import br.com.rtools.associativo.db.SociosDBToplink;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.beans.PesquisaEnderecoBean;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.dao.ServicoPessoaDao;
import br.com.rtools.financeiro.db.MovimentoDB;
import br.com.rtools.financeiro.db.MovimentoDBToplink;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.db.HomologacaoDB;
import br.com.rtools.homologacao.db.HomologacaoDBToplink;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.MalaDiretaDao;
import br.com.rtools.pessoa.dao.PessoaComplementoDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.db.*;
import br.com.rtools.pessoa.utilitarios.PessoaUtilitarios;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.db.FunctionsDao;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class FisicaBean extends PesquisarProfissaoBean implements Serializable {

    private Fisica fisica = new Fisica();

    private PessoaProfissao pessoaProfissao = new PessoaProfissao();
    private PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();

    private Usuario usuario = new Usuario();
    private PessoaComplemento pessoaComplemento = new PessoaComplemento();
    private Socios socios = new Socios();
    //private String renEndereco = "false";
    private String indicaTab = "pessoal";
    private String enderecoCompleto = "";
    private String descPesquisa = "";
    private String porPesquisa = "nome";
    private String comoPesquisa = "I";
    private String mensagem = "";
    private String masc = "";
    private String maxl = "";
    private String enderecoCobranca = "";
    private String renAbreEnd = "true";
    private String msgSocio = "";
    private String lblSocio = "";
    private String pesquisaPor = "pessoa";
    private String tipo = "";
    private String tipoSocio = "";
    private int indexPessoaFisica = 0;
    private String indexNovoEndereco = "";
    private boolean alterarEnd = false;
    private boolean endResidencial = false;
    private boolean fotoTemp = false;
    private boolean renderJuridicaPesquisa = false;
    public List itens = new ArrayList();
    private List<DataObject> listaPessoa = new ArrayList();
    private List<Fisica> listaPessoaFisica = new ArrayList();
    private List<PessoaEmpresa> listaPessoaEmpresa = new ArrayList();
    private final List<SelectItem> listaProfissoes = new ArrayList();
    private List<SelectItem> listaPaises = new ArrayList();
    private int idPais = 11;
    private int idProfissao = 0;
    private int idIndexFisica = 0;
    private int idIndexPessoaEmp = 0;
    private Part file;
    private String fileContent = "";
    private String cliente = "";
    private boolean readyOnlineNaturalidade = true;
    private boolean disabledNaturalidade = false;
    private String[] imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
    private List<Socios> listaSocioInativo = new ArrayList();
    private String mask = "";

    private Endereco enderecox = new Endereco();
    private List<PessoaEndereco> listaPessoaEndereco = new ArrayList();
    private String numero = "";
    private String complemento = "";
    private PessoaEndereco pessoaEndereco = new PessoaEndereco();
    private boolean visibleEditarEndereco = false;
    private List<DataObject> listaServicoPessoa = new ArrayList();
    private List<Socios> listaSocios = new ArrayList();
    private boolean chkSomenteDestaPessoa = false;
    private boolean pessoaOposicao = false;
    private String validacao = "";

    private int indexEndereco = 0;
    private String strEndereco = "";
    private Integer tipoCadastro = -1;

    private List<Fisica> selectedFisica = new ArrayList();

    private Boolean multiple = false;

    private String somaValoresHistorico = "0,00";

    private List<Vector> listaMovimento = new ArrayList();
    private String tipoStatusMovimento = "abertos";
    private String tipoPesquisaMovimento = "beneficiario";

    private String inativoDesde = "";
    private boolean visibleMsgAviso = false;
    private String mensagemAviso = "";
    private Date dtRecadastro = DataHoje.dataHoje();

    // MALA DIRETA
    private Boolean habilitaMalaDireta = false;
    private String idMalaDiretaGrupo = null;
    private List<MalaDireta> listMalaDireta = new ArrayList();
    private List<SelectItem> listMalaDiretaGrupo = new ArrayList();

    private List<Oposicao> listaOposicao = new ArrayList();
    private String filtroOposicao = "ativas";

//    // TAB DOCUMENTOS
//    private List<Documento> listaDocumentos = new ArrayList();
//    private List<LinhaArquivo> listaArquivos = new ArrayList();
    private Integer offset = 0;
    private Integer count = 0;
    private Integer limit = 500;

    private String inCategoriaSocio = null;

    public FisicaBean() {

    }

//    public void loadListaDocumentos() {
//        listaDocumentos.clear();
//
//        DigitalizacaoDao dao = new DigitalizacaoDao();
//
//        if (fisica.getId() != -1) {
//            listaDocumentos = dao.listaDocumento(fisica.getPessoa().getId());
//        }
//    }
//    public void verDocumentos(Documento linha) {
//        listaArquivos.clear();
//
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + linha.getPessoa().getId() + "/" + linha.getId() + "/";
//        File file = new File(path);
//
//        File lista_datas[] = file.listFiles();
//
//        if (lista_datas != null) {
//            for (File lista_data : lista_datas) {
//                String ext = FilenameUtils.getExtension(lista_data.getPath()).toUpperCase();
//                String mimeType = servletContext.getMimeType(lista_data.getPath());
//                listaArquivos.add(new LinhaArquivo("fileExtension" + ext + ".png", lista_data.getName(), mimeType, linha));
//            }
//        }
//    }
    public void loadListaOposicao() {
        if (fisica.getId() != -1) {
            listaOposicao.clear();

            OposicaoDao dao = new OposicaoDao();

            listaOposicao = dao.listaOposicaoDocumento(fisica.getPessoa().getDocumento(), filtroOposicao);
        }
    }

    public void closeMensagemAviso() {
        visibleMsgAviso = false;
    }

    public String refazerMovimentos() {
        if (listaMovimento.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Não existem Movimentos para serem refeitos!");
            return null;
        }

        MovimentoDB db = new MovimentoDBToplink();
        int qnt = 0;

        List<Movimento> lm = new ArrayList();

        for (Vector listaMovimento1 : listaMovimento) {
            //if ((Boolean) listaMovimento1.getArgumento0()) {
            qnt++;
            lm.add((Movimento) new Dao().find(new Movimento(), listaMovimento1.get(0)));
            //}
        }

        if (qnt == 0) {
            GenericaMensagem.warn("Atenção", "Nenhum Movimentos selecionado!");
            return null;
        }

        for (Movimento m : lm) {
            if (m.getBaixa() != null) {
                GenericaMensagem.warn("Atenção", "Boletos pagos não podem ser refeitos!");
                return null;
            }
        }

        ServicoPessoaDao spd = new ServicoPessoaDao();
        for (Movimento m : lm) {
            ServicoPessoa sp = spd.pesquisaServicoPessoa(m.getBeneficiario().getId(), m.getServicos().getId(), true);

            if (sp == null) {
                GenericaMensagem.warn("Atenção", "O SERVIÇO " + m.getServicos().getDescricao() + " para a PESSOA " + m.getBeneficiario().getNome() + " não pode ser refeito!");
                return null;
            }
        }

//      PERMISSÃO DE ACESSO
//        ControleAcessoBean cab = new ControleAcessoBean();
//        Usuario user = (Usuario) GenericaSessao.getObject("sessaoUsuario");
//        if (mov.getBaixa().getUsuario().getId() != user.getId()) {
//            if (cab.getBotaoEstornarMensalidadesOutrosUsuarios()) {
//                GenericaMensagem.error("Atenção", "Você não tem permissão para estornar esse movimento!");
//                return null;
//            }
//        }
        if (!GerarMovimento.refazerMovimentos(lm)) {
            GenericaMensagem.error("Erro", "Não foi possível refazer movimentos");
            return null;
        }

        GenericaMensagem.info("Sucesso", "Boletos atualizados!");
        loadListaMovimento();
        return null;
    }

    public String telaMovimentosReceberSocial() {
        String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).movimentosReceberSocial();
        GenericaSessao.put("movimentosReceberSocialBean", new MovimentosReceberSocialBean());
        GenericaSessao.put("pessoaPesquisa", fisica.getPessoa());
        return retorno;
    }

    public void loadListaMovimento() {
        if (fisica.getId() != -1) {
            listaMovimento.clear();

            FisicaDB db = new FisicaDBToplink();

            listaMovimento = db.listaMovimentoFisica(fisica.getPessoa().getId(), tipoStatusMovimento, tipoPesquisaMovimento);
        }
    }

    public String idade() {
        if (!fisica.getNascimento().isEmpty()) {
            return new DataHoje().calcularIdade(fisica.getNascimento()) + " anos";
        } else {
            return "0 anos";
        }
    }

    public String novo() {
        String urlTemp = "/Cliente/" + getCliente() + "/temp/" + "foto/" + ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId() + "/perfil.png";
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(urlTemp);
        while (new File(arquivo).exists()) {
            boolean d = new File(arquivo).delete();
            if (d) {
                break;
            }
        }
        GenericaSessao.put("photoCamBean", new PhotoCam());
        GenericaSessao.put("fisicaBean", new FisicaBean());
        GenericaSessao.put("pessoaComplementoBean", new PessoaComplementoBean());
        GenericaSessao.put("pesquisaEnderecoBean", new PesquisaEnderecoBean());
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("fisicaPesquisaList");
        GenericaSessao.remove("enderecoPesquisa");
        GenericaSessao.remove("enderecoNum");
        GenericaSessao.remove("enderecoComp");
        GenericaSessao.remove("pessoaComplementoBean");
        GenericaSessao.remove("pesquisaEnderecoBean");
        clear(0);
        return "pessoaFisica";
    }

    public String getEnderecoCobranca() {
        for (PessoaEndereco pe : listaPessoaEndereco) {
            String strCompl = "";
            if (pe.getTipoEndereco().getId() == 3) {
                if (pe.getComplemento().isEmpty()) {
                    strCompl = " ";
                } else {
                    strCompl = " ( " + pe.getComplemento() + " ) ";
                }

                return enderecoCobranca = pe.getEndereco().getLogradouro().getDescricao() + " "
                        + pe.getEndereco().getDescricaoEndereco().getDescricao() + ", " + pe.getNumero() + " " + pe.getEndereco().getBairro().getDescricao() + ","
                        + strCompl + pe.getEndereco().getCidade().getCidade() + " - " + pe.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(pe.getEndereco().getCep());
            }
        }
        return enderecoCobranca;
    }

    public void salvar() {
        NovoLog logs = new NovoLog();
        FisicaDB db = new FisicaDBToplink();
        Pessoa pessoa = fisica.getPessoa();
        List listDocumento;
        if ((listaPessoaEndereco.isEmpty() || pessoa.getId() == -1) && enderecox.getId() != -1) {
            adicionarEnderecos();
        }

        boolean sucesso = false;
        Dao dao = new Dao();
        dao.openTransaction();
        pessoaUpper();

        if (fisica.getPessoa().getTelefone3().isEmpty() && !fisica.getPessoa().getTelefone4().isEmpty()) {
            fisica.getPessoa().setTelefone3(fisica.getPessoa().getTelefone4());
            fisica.getPessoa().setTelefone4("");
        }

        if ((fisica.getPessoa().getId() == -1) && (fisica.getId() == -1)) {
            fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            if (!db.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome(),
                    fisica.getDtNascimento(),
                    fisica.getRg()).isEmpty()) {
                mensagem = "Esta pessoa já esta cadastrada!";
                return;
            }
            if (fisica.getNascimento().isEmpty() || fisica.getNascimento().length() < 10) {
                mensagem = "Data de nascimento esta inválida!";
                return;
            }
            if (!fisica.getNascimento().isEmpty()) {
                if (!DataHoje.isDataValida(fisica.getNascimento())) {
                    mensagem = "Data de nascimento esta inválida!";
                    return;
                }
            }
            if (pessoa.getDocumento().equals("") || pessoa.getDocumento().equals("0")) {
                pessoa.setDocumento("0");
            } else {
                if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                    mensagem = "Documento Invalido!";
                    return;
                }
                listDocumento = db.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    mensagem = "Documento já existente!";
                    return;
                }
            }
            if (fisica.getPessoa().getNome().equals("")) {
                mensagem = "O campo nome não pode ser nulo! ";
                return;
            }
            if (dao.save(pessoa)) {
                fisica.setNacionalidade(getListaPaises().get(idPais).getLabel());
                fisica.setPessoa(pessoa);
                if (dao.save(fisica)) {

                    GenericaSessao.put("fisicaPesquisa", fisica);
                    mensagem = "Cadastro salvo com Sucesso!";
                    logs.setTabela("pes_fisica");
                    logs.setCodigo(fisica.getId());
                    logs.save("ID " + fisica.getId()
                            + " - Pessoa: " + fisica.getPessoa().getId()
                            + " - Nome: " + fisica.getPessoa().getNome()
                            + " - Nascimento: " + fisica.getNascimento()
                            + " - CPF: " + fisica.getPessoa().getDocumento()
                            + " - RG: " + fisica.getRg()
                            + " - Recadastro : " + fisica.getPessoa().getRecadastroString());
                    dao.commit();
                    sucesso = true;
                } else {
                    mensagem = "Erro ao Salvar Pessoa Fisica!";
                    dao.rollback();
                }
            } else {
                mensagem = "Erro ao Salvar Pessoa!";
                dao.rollback();
            }
        } else {
            fisica.getPessoa().setDtAtualizacao(new Date());
            fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
            Fisica f = (Fisica) dao.find(new Fisica(), fisica.getId());
            String antes = " ID - " + f.getId()
                    + " - Nome: " + f.getPessoa().getNome()
                    + " - Nascimento: " + f.getNascimento()
                    + " - CPF: " + f.getPessoa().getDocumento()
                    + " - RG: " + f.getRg()
                    + " - Recadastro : " + f.getPessoa().getRecadastroString();
            if (fisica.getPessoa().getDocumento().equals("") || fisica.getPessoa().getDocumento().equals("0")) {
                fisica.getPessoa().setDocumento("0");
            } else {
                if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                    mensagem = "Documento Inválido!";
                    return;
                }
                listDocumento = db.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
                for (Object listDocumento1 : listDocumento) {
                    if (!listDocumento.isEmpty() && ((Fisica) listDocumento1).getId() != fisica.getId()) {
                        mensagem = "Documento já existente!";
                        return;
                    }
                }
            }
            if (fisica.getNascimento().isEmpty() || fisica.getNascimento().length() < 10) {
                mensagem = "Data de Nascimento esta inválida!";
                return;
            }
            List<Fisica> fisi = db.pesquisaFisicaPorNomeNascRG(fisica.getPessoa().getNome(),
                    fisica.getDtNascimento(),
                    fisica.getRg());
            if (!fisi.isEmpty()) {
                for (Fisica fisi1 : fisi) {
                    if (fisi1.getId() != fisica.getId()) {
                        mensagem = "Esta pessoa já esta cadastrada!";
                        return;
                    }
                }
            }

            fisica.setNacionalidade(getListaPaises().get(idPais).getLabel());
            if (dao.update(fisica.getPessoa())) {
                logs.setTabela("pes_fisica");
                logs.setCodigo(fisica.getId());
                logs.update(antes,
                        " Nome: " + fisica.getPessoa().getNome()
                        + " - Nascimento: " + f.getNascimento()
                        + " - CPF: " + fisica.getPessoa().getDocumento()
                        + " - RG: " + fisica.getRg()
                        + " - Recadastro : " + fisica.getPessoa().getRecadastroString());
            } else {
                dao.rollback();
                return;
            }

            if (dao.update(fisica)) {
                GenericaSessao.put("fisicaPesquisa", fisica);
                mensagem = "Cadastro atualizado com Sucesso!";
                sucesso = true;
                dao.commit();
            } else {
                mensagem = "Erro ao Atualizar!";
                dao.rollback();
            }
        }
        salvarEndereco();
        salvarPessoaEmpresa();
        salvarPessoaProfissao();
        //limparCamposData();
//        if (sucesso) {
//            salvarImagem();
//            // new Dao().update(fisica, true);
//        }
    }

    public void pessoaUpper() {
        fisica.getPessoa().setNome(fisica.getPessoa().getNome().toUpperCase().trim());
        fisica.setRg(fisica.getRg().toUpperCase().trim());
        fisica.setPai(fisica.getPai().toUpperCase().trim());
        fisica.setMae(fisica.getMae().toUpperCase().trim());
    }

    public void salvarPessoaComplemento() {
        if (fisica.getPessoa().getId() != -1) {
            ((PessoaComplementoBean) GenericaSessao.getObject("pessoaComplementoBean")).update(fisica.getPessoa().getId());
            pessoaComplemento = ((PessoaComplementoBean) GenericaSessao.getObject("pessoaComplementoBean")).getPessoaComplemento();
        }
    }

    public void salvarPessoaProfissao() {
        if (!listaProfissoes.isEmpty()) {
            Dao dao = new Dao();
            dao.openTransaction();
            pessoaProfissao.setProfissao((Profissao) dao.find(new Profissao(), Integer.parseInt(listaProfissoes.get(idProfissao).getDescription())));
            if (fisica.getId() == -1) {
                pessoaProfissao = new PessoaProfissao();
                pessoaProfissao.setFisica(fisica);
                if (!dao.save(pessoaProfissao)) {
                    dao.rollback();
                } else {
                    dao.commit();
                }
            } else {
                if (pessoaProfissao.getId() == -1) {
                    pessoaProfissao.setFisica(fisica);
                    if (!dao.save(pessoaProfissao)) {
                        dao.rollback();
                        return;
                    }
                } else if (!dao.update(pessoaProfissao)) {
                    dao.rollback();
                    return;
                }
                dao.commit();
            }
        }
    }

    public String novoOK() {
        if (fisica.getId() == -1) {
            GenericaSessao.put("fisicaBean", new FisicaBean());
        }
        return "pessoaFisica";
    }

    public void salvarEndereco() {
        //List endPorPessoa = getPesquisaEndPorPessoa();
        if (fisica.getId() != -1) {
            Dao dao = new Dao();
            if (!listaPessoaEndereco.isEmpty()) {
                dao.openTransaction();
                for (PessoaEndereco pe : listaPessoaEndereco) {
                    if (pe.getId() == -1) {
                        if (!dao.save(pe)) {
                            GenericaMensagem.warn("Erro", "Não foi possivel SALVAR endereço!");
                            dao.rollback();
                            return;
                        }
                    } else if (!dao.update(pe)) {
                        GenericaMensagem.warn("Erro", "Não foi possivel ALTERAR endereço!");
                        dao.rollback();
                        return;
                    }
                }

                dao.commit();
            }

        }
    }

    public void excluir() {
        if (socios.getId() != -1) {
            mensagem = "Esse cadastro esta associado, desvincule para excluir!";
            return;
        }
        //PessoaDB dbPessoa = new PessoaDBToplink();
        if (fisica.getId() != -1) {
            //fisica.setPessoa(dbPessoa.pesquisaCodigo(fisica.getPessoa().getId()));
            PessoaEnderecoDao dbPE = new PessoaEnderecoDao();
            List<PessoaEndereco> listaEndereco = dbPE.pesquisaEndPorPessoa(fisica.getPessoa().getId());
            Dao dao = new Dao();
            dao.openTransaction();
            // EXCLUI ENDEREÇO -----------------
            if (!listaEndereco.isEmpty()) {
                for (PessoaEndereco listaEndereco1 : listaEndereco) {
                    if (!dao.delete(listaEndereco1)) {
                        dao.rollback();
                        mensagem = "Erro ao excluir endereços!";
                        return;
                    }
                }
            }
            PessoaProfissaoDB dbPP = new PessoaProfissaoDBToplink();
            PessoaProfissao pp = dbPP.pesquisaProfPorFisica(fisica.getId());
            // EXCLUI PROFISSÃO -----------------
            if (pp.getId() != -1) {
                if (!dao.delete(pp)) {
                    dao.rollback();
                    mensagem = "Erro ao excluir profissão!";
                    return;
                }
            }
            // EXCLUI PESSOA EMPRESA ------------
            PessoaEmpresaDB dbEM = new PessoaEmpresaDBToplink();
            List<PessoaEmpresa> listaPessoaEmp = dbEM.listaPessoaEmpresaTodos(fisica.getId());
            if (!listaPessoaEmp.isEmpty()) {
                for (PessoaEmpresa listaPessoaEmp1 : listaPessoaEmp) {
                    if (!dao.delete(listaPessoaEmp1)) {
                        dao.rollback();
                        mensagem = "Erro ao excluir pessoas empresa!";
                        return;
                    }
                }
            }
            if (!dao.delete(fisica)) {
                dao.rollback();
                mensagem = "Física não pode ser excluída!";
                return;
            }
            if (!dao.delete(fisica.getPessoa())) {
                dao.rollback();
                mensagem = "Cadastro Pessoa não pode ser excluída!";
                return;
            }
            dao.commit();
            apagarImagem();
            NovoLog logs = new NovoLog();
            logs.delete("ID: " + fisica.getId() + " - Pessoa: " + fisica.getPessoa().getId() + " - Nascimento: " + fisica.getNascimento() + " - Nome: " + fisica.getPessoa().getNome() + " - CPF: " + fisica.getPessoa().getDocumento() + " - RG: " + fisica.getRg());
            //GenericaSessao.put("fisicaBean", new FisicaBean());
            //((FisicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fisicaBean")).setMensagem("Cadastro Excluido com sucesso!");            
            novo();
            mensagem = "Cadastro Excluido com sucesso!";
        } else {
            mensagem = "Pesquise uma pessoa física para ser excluída!";
        }
    }

    public String complete() {
        String url = (String) GenericaSessao.getString("urlRetorno");
        GenericaSessao.put("fisicaPesquisaList", selectedFisica);
        GenericaSessao.put("linkClicado", true);
        multiple = false;
        return url;
    }

    public String editarFisica(Pessoa p) {
        FisicaDB fisicaDB = new FisicaDBToplink();
        Fisica f = fisicaDB.pesquisaFisicaPorPessoa(p.getId());
        f = (Fisica) new Dao().rebind(f);
        return editarFisica(f);
    }

    public String editarFisica(Fisica f) {
        return editarFisica(f, false);
    }

    public String editarFisica(Fisica f, Boolean completo) {
        Dao dao = new Dao();
        selectedFisica = new ArrayList<>();
        multiple = false;
        pessoaComplemento = new PessoaComplemento();
        String url = (String) GenericaSessao.getString("urlRetorno");
        fisica = (Fisica) new Dao().rebind(f);
        dao.refresh(f.getPessoa());

        if (!listernerValidacao(f, url)) {
            return null;
        }

        GenericaSessao.put("fisicaPesquisa", f);
        if (!url.equals("pessoaFisica") && !completo) {
            GenericaSessao.put("linkClicado", true);
            return url;
        }
        PessoaEmpresaDB db = new PessoaEmpresaDBToplink();
        PessoaProfissaoDB dbp = new PessoaProfissaoDBToplink();
        GenericaSessao.remove("pessoaComplementoBean");
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        alterarEnd = true;
        listaPessoa = new ArrayList();
        msgSocio = "";
        pessoaEmpresa = (PessoaEmpresa) db.pesquisaPessoaEmpresaPorFisica(fisica.getId());
        if (pessoaEmpresa.getId() != -1) {
            pessoaEmpresa = (PessoaEmpresa) dao.rebind(pessoaEmpresa);
            if (pessoaEmpresa.getFuncao() != null) {
                profissao = pessoaEmpresa.getFuncao();
            } else {
                profissao = new Profissao();
            }
            //GenericaSessao.put("juridicaPesquisa", pessoaEmpresa.getJuridica());
            renderJuridicaPesquisa = true;
        } else {
            GenericaSessao.remove("juridicaPesquisa");
            profissao = new Profissao();
            renderJuridicaPesquisa = false;
        }

        pessoaProfissao = dbp.pesquisaProfPorFisica(fisica.getId());
        if (pessoaProfissao.getId() != -1) {
            pessoaProfissao = (PessoaProfissao) dao.rebind(pessoaProfissao);
            for (int i = 0; i < listaProfissoes.size(); i++) {
                if (Objects.equals(Integer.valueOf(listaProfissoes.get(i).getDescription()), pessoaProfissao.getProfissao().getId())) {
                    idProfissao = i;
                    break;
                }
            }
        }
        for (int i = 0; i < listaPaises.size(); i++) {
            if ((listaPaises.get(i).getLabel().toUpperCase()).equals(fisica.getNacionalidade().toUpperCase())) {
                idPais = i;
                break;
            }
        }
        loadMalaDireta();
        indexNovoEndereco = "";
        strEndereco = "";
        listaPessoaEndereco.clear();
        getListaPessoaEndereco();
        listaServicoPessoa.clear();
        editarFisicaSocio(fisica);
        showImagemFisica();
        GenericaSessao.put("linkClicado", true);
        existePessoaOposicaoPorPessoa();
        //fotoTempPerfil = "";
        clear(0);
        loadListaMovimento();
        if (pessoaComplemento.getId() == -1) {
            pessoaComplemento = (PessoaComplemento) dao.rebind(new PessoaComplementoDao().findByPessoa(fisica.getPessoa().getId()));
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
            }
        }

        // LISTA DE OPOSIÇÕES --
        filtroOposicao = "ativas";
        loadListaOposicao();
        // --

        // loadListaDocumentos();
        return url;
    }

    public void showImagemFisica() {
//        for (String imagensTipo1 : imagensTipo) {
//            String path = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/Fisica/" + fisica.getFoto() + "." + imagensTipo1);
//            File fpath = new File(path);
//            if (fpath.exists()) {
//                fotoPerfilStreamed = ImageConverter.getImageStreamed(fpath, "image/png");
//            }
//        }
    }

    public void existePessoaDocumento() {
        if (!fisica.getPessoa().getDocumento().isEmpty() && !fisica.getPessoa().getDocumento().equals("___.___.___-__") && fisica.getId() == -1) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                mensagem = "Documento Invalido!";
                GenericaMensagem.warn("Validação", "Documento (CPF) inválido! " + fisica.getPessoa().getDocumento());
                PF.update("form_pessoa_fisica:i_tabview_fisica:id_valida_documento:" + fisica.getPessoa().getDocumento());
                fisica.getPessoa().setDocumento("");
                return;
            }
            FisicaDB db = new FisicaDBToplink();
            List lista = db.pesquisaFisicaPorDoc(fisica.getPessoa().getDocumento());
            Boolean success = false;
            if (!lista.isEmpty()) {
                success = true;
                String x = editarFisicaParametro((Fisica) lista.get(0));
                pessoaUpper();
                pessoaComplemento = fisica.getPessoa().getPessoaComplemento();
                getListaPessoaEndereco();
                showImagemFisica();
            }
            existePessoaOposicaoPorPessoa();
            PessoaProfissaoDB dbp = new PessoaProfissaoDBToplink();
            pessoaProfissao = dbp.pesquisaProfPorFisica(fisica.getId());
            if (pessoaProfissao.getId() != -1) {
                for (int i = 0; i < listaProfissoes.size(); i++) {
                    if (Objects.equals(Integer.valueOf(listaProfissoes.get(i).getDescription()), pessoaProfissao.getProfissao().getId())) {
                        idProfissao = i;
                        break;
                    }
                }
            }
            loadMalaDireta();
            if (success) {
                RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
                RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_end_rendered");
                RequestContext.getCurrentInstance().update("form_pessoa_fisica:id_msg_aviso_block");
            }
        }
    }

    public void existePessoaNomeNascimento() {
        if (fisica.getId() == -1) {
            if (!fisica.getNascimento().isEmpty() && !fisica.getPessoa().getNome().isEmpty()) {
                FisicaDB db = new FisicaDBToplink();
                Fisica f = db.pesquisaFisicaPorNomeNascimento(fisica.getPessoa().getNome(), fisica.getDtNascimento());
                if (f != null) {
                    String x = editarFisicaParametro(f);
                    pessoaUpper();
                    loadMalaDireta();
                    RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
                    showImagemFisica();
                }
            }
        }
    }

    public String editarFisicaParametro(Fisica f) {
        Dao dao = new Dao();
        PessoaEmpresaDB db = new PessoaEmpresaDBToplink();
        fisica = (Fisica) dao.rebind(f);
        GenericaSessao.put("fisicaPesquisa", fisica);
        String url = (String) GenericaSessao.getString("urlRetorno");
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        alterarEnd = true;
        pessoaEmpresa = db.pesquisaPessoaEmpresaPorFisica(fisica.getId());
        if (pessoaEmpresa.getId() != -1) {
            pessoaEmpresa = (PessoaEmpresa) dao.rebind(pessoaEmpresa);
            if (pessoaEmpresa.getFuncao() != null) {
                profissao = pessoaEmpresa.getFuncao();
            } else {
                profissao = new Profissao();
            }
            //GenericaSessao.put("juridicaPesquisa", pessoaEmpresa.getJuridica());
            renderJuridicaPesquisa = true;
        } else {
            GenericaSessao.remove("juridicaPesquisa");
            profissao = new Profissao();
            renderJuridicaPesquisa = false;
        }

        listaServicoPessoa.clear();
        editarFisicaSocio(fisica);
        GenericaSessao.put("linkClicado", true);

        showImagemFisica();
        getListaSocioInativo().clear();
        getListaSocioInativo();
        getListaPessoaEndereco().clear();
        getListaPessoaEndereco();
        getStrEndereco();
        if (pessoaComplemento.getId() == -1) {
            pessoaComplemento = (PessoaComplemento) dao.rebind(new PessoaComplementoDao().findByPessoa(fisica.getPessoa().getId()));
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
            }
        }
        return url;
    }

    public void editarFisicaSocio(Fisica fis) {
        Dao dao = new Dao();
        SociosDB db = new SociosDBToplink();
        socios = db.pesquisaSocioPorPessoaAtivo(fisica.getPessoa().getId());
        if (socios.getId() == -1) {
            //socios = new SociosDBToplink().pesquisaSocioTitularInativoPorPessoa(fisica.getPessoa().getId());
            List<Socios> ls = new SociosDBToplink().pesquisaSocioPorPessoaInativo(fisica.getPessoa().getId());
            if (!ls.isEmpty()) {
                socios = (Socios) dao.rebind(ls.get(0));
            } else {
                socios = new Socios();
            }
        } else {
            socios = (Socios) dao.rebind(socios);
        }
        listaSocioInativo.clear();
    }

    public List getListaFisica() {
        FisicaDB db = new FisicaDBToplink();
        List result = db.pesquisaTodos();
        return result;
    }

    /**
     * TIPO ENDERECO PESSOA FÍSICA {1,3,4}
     */
    public void alterarEndereco() {
        visibleEditarEndereco = false;
        enderecox = new Endereco();
        for (PessoaEndereco pe : listaPessoaEndereco) {

        }
    }

    public void alterarTodosEndereco() {
        visibleEditarEndereco = false;

        for (int i = 0; i < listaPessoaEndereco.size(); i++) {
            listaPessoaEndereco.get(i).setEndereco(pessoaEndereco.getEndereco());
            listaPessoaEndereco.get(i).setNumero(pessoaEndereco.getNumero());
            listaPessoaEndereco.get(i).setComplemento(pessoaEndereco.getComplemento());
        }

        enderecox = new Endereco();
    }

    public void adicionarEnderecos() {
        Dao dao = new Dao();
        List<TipoEndereco> tipoEnderecos = (List<TipoEndereco>) dao.find("TipoEndereco", new int[]{1, 3, 4});
        if (enderecox.getId() != -1) {

            listaPessoaEndereco.clear();
            for (TipoEndereco tipoEndereco : tipoEnderecos) {
                listaPessoaEndereco.add(new PessoaEndereco(
                        -1,
                        enderecox,
                        tipoEndereco,
                        fisica.getPessoa(),
                        numero,
                        complemento
                ));
            }
        }

        enderecox = new Endereco();
        if (1 == 1) {
            return;
        }
        // COMPARA ENDERECOS
        //comparaEndereco(pessoaEndeAnt, (PessoaEndereco) listaEnd.get(u))
        Endereco endereco = new Endereco();

        //GenericaSessao.put("enderecoNum", pessoaEndereco.getNumero());
        //GenericaSessao.put("enderecoComp", pessoaEndereco.getComplemento());
    }

    public void editarPessoaEndereco(PessoaEndereco pessoaEnderecox, int index) {
        pessoaEndereco = pessoaEnderecox;
        visibleEditarEndereco = true;
        indexEndereco = index;
        //GenericaSessao.put("enderecoPesquisa", pessoaEndereco.getEndereco());
        //log = pessoaEndereco.getEndereco().getLogradouro().getDescricao();
        //desc = pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao();
        //cid = pessoaEndereco.getEndereco().getCidade().getCidade();
        //uf = pessoaEndereco.getEndereco().getCidade().getUf();
//        setEnderecoCompleto(log + " " + desc + ", " + cid + " - " + uf);
//        renEndereco = "false";
//        setRenNovoEndereco("true");
//        alterarEnd = true;
    }
//    public void editarPessoaEndereco(int index) {
//        pessoaEndereco = (PessoaEndereco) listaEnd.get(index);
//        GenericaSessao.put("enderecoPesquisa", pessoaEndereco.getEndereco());
//        log = pessoaEndereco.getEndereco().getLogradouro().getDescricao();
//        desc = pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao();
//        cid = pessoaEndereco.getEndereco().getCidade().getCidade();
//        uf = pessoaEndereco.getEndereco().getCidade().getUf();
//        setEnderecoCompleto(log + " " + desc + ", " + cid + " - " + uf);
//        renEndereco = "false";
//        setRenNovoEndereco("true");
//        alterarEnd = true;
//    }
//
//    public List getListaPessoaEndereco() {
//        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
//        List list = salvarAcumuladoDB.listaObjeto("PessoaEndereco");
//        return list;
//    }

    public String CarregarEndereco() {
        int idEndereco = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paramEndereco"));
        pessoaEndereco.setEndereco((Endereco) new Dao().find(new Endereco(), idEndereco));
        setEnderecoCompleto((pessoaEndereco.getEndereco().getLogradouro().getDescricao()) + " " + pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao());
        return "pessoaFisica";
    }

    public List<String> BuscaTipoEndereco(Object object) {
        String txtDigitado = object.toString().toLowerCase().toUpperCase();
        TipoEnderecoDB db = new TipoEnderecoDBToplink();
        List<String> list = db.pesquisaTipoEnderecoParaFisica('%' + txtDigitado + '%');
        return list;
    }

    public List<String> BuscaTipoDocumento(Object object) {
        String txtDigitado = object.toString().toLowerCase().toUpperCase();
        TipoDocumentoDB db = new TipoDocumentoDBToplink();
        List<String> list = db.pesquisaTipoDocumento('%' + txtDigitado + '%');
        return list;
    }

    public String getRetornaEndereco() {
        return "pessoaFisica";
    }

    public List getPesquisaEndPorPessoa() {
        PessoaEnderecoDao db = new PessoaEnderecoDao();
        List list = db.pesquisaEndPorPessoa(fisica.getPessoa().getId());
        return list;
    }

//    public String voltarEndereco() {
//        return "pessoaFisica";
//    }
    public boolean getHabilitar() {
        return fisica.getPessoa().getId() == -1;
    }

    public String excluirPessoaEndereco() {
        if (pessoaEndereco.getId() != -1) {
            Dao dao = new Dao();
            dao.delete(pessoaEndereco, true);
        }
        pessoaEndereco = new PessoaEndereco();
        setEnderecoCompleto("");
        return "pessoaFisica";
    }

    public void salvarPessoaEmpresa() {
        PessoaEmpresaDB db = new PessoaEmpresaDBToplink();
        if (fisica.getId() != -1 && pessoaEmpresa.getJuridica().getId() != -1) {
            pessoaEmpresa.setFisica(fisica);
            pessoaEmpresa.setAvisoTrabalhado(false);

            if (pessoaEmpresa.getDtAdmissao() == null) {
                mensagem = "Informar data de admissão!";
                return;
            }

            if (!pessoaEmpresa.getDemissao().isEmpty() && pessoaEmpresa.getDemissao() != null) {
                if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                        > DataHoje.converteDataParaInteger(DataHoje.data())) {
                    mensagem = "Data de Demissão maior que atual!";
                    return;
                }
            }

            if (pessoaEmpresa.getDtAdmissao() != null && pessoaEmpresa.getDtDemissao() != null) {
                int dataAdmissao = DataHoje.converteDataParaInteger(pessoaEmpresa.getAdmissao());
                int dataDemissao = DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao());
                if (dataDemissao <= dataAdmissao) {
                    mensagem = "Data de demissão deve ser maior que data de admissão!";
                    pessoaEmpresa.setDemissao(null);
                    return;
                }
                pessoaEmpresa.setPrincipal(false);
            }

            if (profissao.getProfissao() == null || profissao.getProfissao().isEmpty()) {
                pessoaEmpresa.setFuncao(null);
            } else {
                pessoaEmpresa.setFuncao(profissao);
            }

            if (pessoaEmpresa.getId() == -1) {
                db.insert(pessoaEmpresa);
            } else {
                db.update(pessoaEmpresa);
            }

            if (pessoaEmpresa.getDemissao() != null && !pessoaEmpresa.getDemissao().isEmpty()) {
                pessoaEmpresa = new PessoaEmpresa();
                profissao = new Profissao();
                GenericaSessao.remove("juridicaPesquisa");
                renderJuridicaPesquisa = false;

                List<PessoaEmpresa> lpe = db.listaPessoaEmpresaPorFisicaDemissao(fisica.getId());

                if (!lpe.isEmpty()) {
                    lpe.get(0).setPrincipal(true);

                    db.update(lpe.get(0));

                    pessoaEmpresa = lpe.get(0);
                    renderJuridicaPesquisa = true;
                }
            }
        }
    }

    public void adicionarEmpresa() {
        if (fisica.getId() != -1 && pessoaEmpresa.getJuridica().getId() != -1) {
            if (pessoaEmpresa.getAdmissao().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Data de Admissão não pode estar vazia!");
                return;
            }

            pessoaEmpresa.setFisica(fisica);
            pessoaEmpresa.setAvisoTrabalhado(false);
            pessoaEmpresa.setPrincipal(false);

            if (profissao.getProfissao() == null || profissao.getProfissao().isEmpty()) {
                pessoaEmpresa.setFuncao(null);
            } else {
                pessoaEmpresa.setFuncao(profissao);
            }

            Dao di = new Dao();

            di.openTransaction();

            if (pessoaEmpresa.getId() == -1) {
                if (!di.save(pessoaEmpresa)) {
                    di.rollback();
                    GenericaMensagem.error("ERRO", "Não foi possível adicionar Empresa!");
                    return;
                }
            } else if (!di.update(pessoaEmpresa)) {
                di.rollback();
                GenericaMensagem.error("ERRO", "Não foi possível adicionar Empresa!");
                return;
            }
            di.commit();

            //RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
            GenericaMensagem.info("Sucesso", "Empresa Adicionada!");
            pessoaEmpresa = new PessoaEmpresa();
            profissao = new Profissao();
            renderJuridicaPesquisa = false;
        }
    }

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
        listaPessoa.clear();
        listaPessoaFisica.clear();
        loadList();
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
        listaPessoa.clear();
        listaPessoaFisica.clear();
        loadList();
    }

    public int getRetornaIdPessoaList() {
        //fisica = (Fisica) getHtmlTable().getRowData();
        return fisica.getPessoa().getId();
    }

    public List<DataObject> getListaPessoa() {
        if (listaPessoa.isEmpty()) {
            List<Fisica> result2 = new ArrayList();
            FisicaDB db = new FisicaDBToplink();
            PessoaEmpresaDB dbEmp = new PessoaEmpresaDBToplink();
            if (pesquisaPor.equals("socioativo")) {
                result2 = db.pesquisaPessoaSocio(descPesquisa, porPesquisa, comoPesquisa, limit, offset);
            } else if (pesquisaPor.equals("pessoa")) {
                result2 = db.pesquisaPessoa(descPesquisa, porPesquisa, comoPesquisa, limit, offset);
            } else if (pesquisaPor.equals("socioinativo")) {
                result2 = db.pesquisaPessoaSocioInativo(descPesquisa, porPesquisa, comoPesquisa, limit, offset);
            }
            for (Fisica result21 : result2) {
                listaPessoa.add(new DataObject(result21, (PessoaEmpresa) dbEmp.pesquisaPessoaEmpresaPorFisica(result21.getId())));
            }
        }
        return listaPessoa;
    }

    public void setListaPessoa(List<DataObject> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public String getColocarMascaraPesquisa() {
        masc = "";
        if (porPesquisa.equals("cpf")) {
            masc = "cpf";
        }
        return masc;
    }

    public String getColocarMaxlenghtPesquisa() {
        maxl = "50";
        if (porPesquisa.equals("cpf")) {
            maxl = "14";
        }
        return maxl;
    }

    public boolean comparaEndereco(PessoaEndereco pessoaEnde1, PessoaEndereco pessoaEnde2) {
        boolean compara;
        if (pessoaEnde1 != null && pessoaEnde2 != null) {
            compara = (pessoaEnde1.getEndereco().getId() == pessoaEnde2.getEndereco().getId()
                    && pessoaEnde1.getNumero().equals(pessoaEnde2.getNumero())
                    && pessoaEnde1.getComplemento().equals(pessoaEnde2.getComplemento()));
        } else {
            compara = false;
        }
        return compara;
    }

    public List<SelectItem> getListaPaises() {
        if (listaPaises.isEmpty()) {
            // String[] lista = new String[]{};
            String[] lista = new String[]{
                "Africana(o)",
                "Afegã(o)",
                "Alemã(o)",
                "Americana(o)",
                "Angolana(o)",
                "Argelina(o)",
                "Argentina(o)",
                "Asiática(o)",
                "Australiana(o)",
                "Belga(o)",
                "Boliviana(o)",
                "Brasileira(o)",
                "Canadense(o)",
                "Canadiana(o)",
                "Chilena(o)",
                "Chinesa(o)",
                "Colombiana(o)",
                "Cubana(o)",
                "Da Nova Zelândia(o)",
                "Dinamarquesa(o)",
                "Egípcia(o)",
                "Equatoriana(o)",
                "Espanha(o)",
                "Espanhola(o)",
                "Europeu(o)",
                "Finlandesa(o)",
                "Francesa(o)",
                "Grega(o)",
                "Haitiana(o)",
                "Holandesa(o)",
                "Hondurenha(o)",
                "Hungara(o)",
                "Indiana(o)",
                "Inglesa(o)",
                "Iraneana(o)",
                "Iraquiana(o)",
                "Italiana(o)",
                "Jamaicana(o)",
                "Japonesa(o)",
                "Marroquina(o)",
                "Mexicana(o)",
                "Norte Americana(o)",
                "Norueguesa(o)",
                "Paquistanesa(o)",
                "Paraguaia(o)",
                "Peruana(o)",
                "Polaca(o)",
                "Portuguesa(o)",
                "Queniana(o)",
                "Russa(o)",
                "Sueca(o)",
                "Suiça(o)",
                "Sul-Africana(o)",
                "Sul-Coreana(o)",
                "Turca(o)",
                "Uraguaia(o)",
                "Venezuelana(o)"};
            for (int i = 0; i < lista.length; i++) {
                listaPaises.add(new SelectItem(i, lista[i], String.valueOf(i)));
            }
//            if (fisica.getId() != -1) {
//                for (int i = 0; i < listaPaises.size(); i++) {
//                    if ((listaPaises.get(i).getLabel().toUpperCase()).equals(fisica.getNacionalidade().toUpperCase())) {
//                        idPais = i;
//                    }
//                }
//            }
        }
        return listaPaises;
    }

    public void setListaPaises(List<SelectItem> listaPaises) {
        this.listaPaises = listaPaises;
    }

    public List<SelectItem> getListaProfissoes() {
        if (listaProfissoes.isEmpty()) {
            ProfissaoDB db = new ProfissaoDBToplink();
            List<Profissao> lista = (List<Profissao>) db.pesquisaTodos();
            for (int i = 0; i < lista.size(); i++) {
                listaProfissoes.add(new SelectItem(i, lista.get(i).getProfissao(), "" + lista.get(i).getId()));
            }
        }
        return listaProfissoes;
    }

    public String getCidadeNaturalidade() {
        String nat;
        if (idPais != 11) {
            readyOnlineNaturalidade = false;
            disabledNaturalidade = true;
            nat = "";
            fisica.setNaturalidade(nat);
            return nat;
        } else {
            readyOnlineNaturalidade = true;
            disabledNaturalidade = false;
        }
        Cidade cidade;
        if (GenericaSessao.exists("cidadePesquisa")) {
            cidade = (Cidade) GenericaSessao.getObject("cidadePesquisa", true);
            nat = cidade.getCidade();
            nat = nat + " - " + cidade.getUf();
            fisica.setNaturalidade(nat);
        }

        if (!fisica.getNaturalidade().isEmpty()) {
            nat = fisica.getNaturalidade();
            return nat;
        }

        if (fisica.getId() == -1 || fisica.getNaturalidade().isEmpty()) {
            PessoaEnderecoDao dbPes = new PessoaEnderecoDao();
            Dao dao = new Dao();
            Filial fili = (Filial) dao.find(new Filial(), 1);
            if (fili != null) {
                Pessoa pes = fili.getMatriz().getPessoa();
                if (pes.getId() != -1) {
                    cidade = ((PessoaEndereco) dbPes.pesquisaEndPorPessoa(pes.getId()).get(0)).getEndereco().getCidade();
                    nat = cidade.getCidade();
                    nat = nat + " - " + cidade.getUf();
                    if (idPais != 11) {
                        fisica.setNaturalidade("");
                    } else {
                        fisica.setNaturalidade(nat);
                    }
                    return nat;
                }
            }
        }
        return null;
    }

//    public void salvarImagem() {
//        if (fisica.getId() != -1) {
//            ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//
//            // nomeFoto NÃO ESTA SENDO SETADO POIS A FOTO DA PESSOA ESTA SENDO PEGADA DIRETAMENTE DA CLASSE pessoa.getFotoResource();
//            // PARA FUNCIONAR TEM QUE CRIAR UM getFoto DENTRO DA PRÓPRIA CLASSE FisicaBean ASSIM COMO ESTA NO BEAN ConviteMovimentoBean
//            if (nomeFoto.isEmpty()) {
//                // CASO QUEIRA REMOVER A FOTO ANTERIOR
//                File fotoAntiga = new File(servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente() + "/imagens/pessoa/" + fisica.getPessoa().getId() + "/" + fisica.getFoto() + ".png");
//                if (fotoAntiga.exists()) {
//                    FileUtils.deleteQuietly(fotoAntiga);
//                }
//
//                Dao dao = new Dao();
//                dao.openTransaction();
//                fisica.setFoto(nomeFoto);
//                dao.update(fisica);
//                dao.commit();
//            }
//        }
//
//    }
    public String excluirEmpresaAnterior(PessoaEmpresa pe) {
        HomologacaoDB dbAge = new HomologacaoDBToplink();
        List<Agendamento> agendas = dbAge.pesquisaAgendamentoPorPessoaEmpresa(pe.getId());
        // CHAMADO 1262
        if (!agendas.isEmpty()) {
            GenericaMensagem.error("ATENÇÃO", "Empresa com data de demissão não pode ser removida!");
            return null;
        }

        Dao dao = new Dao();
        for (Agendamento agenda : agendas) {
            if (!dao.delete(agenda, true)) {
                ErrorCodeDao errorCodeDao = dao.deleteErrorCode(agenda);
                GenericaMensagem.warn("Erro", "Não foi possível remover este agendamento!");
                //GenericaMensagem.error("Sistema", errorCodeDao.getSimpleMessage());
                return null;
            }
        }
        if (dao.delete(pe, true)) {
            GenericaMensagem.info("Sucesso", "Empresa removida com sucesso");
        } else {
            ErrorCodeDao errorCodeDao = dao.deleteErrorCode(pe);
            GenericaMensagem.warn("Erro", "Não foi possível remover esta empresa!");
            //GenericaMensagem.error("Sistema", errorCodeDao.getSimpleMessage());
        }
        listaPessoaEmpresa.clear();
        return null;
    }

    public void removerJuridicaPesquisada() {
        if (pessoaEmpresa.getId() != -1) {
            Dao dao = new Dao();
            if (!dao.delete(pessoaEmpresa, true)) {
                mensagem = "Empresa com Agendamento não pode ser excluída!";
                PF.update("form_pessoa_fisica:i_panel_mensagem");
                PF.openDialog("dlg_painel_mensagem");
                return;
            }
        }
        GenericaSessao.remove("juridicaPesquisa");
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        renderJuridicaPesquisa = false;

        PessoaEmpresaDB db = new PessoaEmpresaDBToplink();
        List<PessoaEmpresa> lpe = db.listaPessoaEmpresaPorFisicaDemissao(fisica.getId());

        if (!lpe.isEmpty()) {
            lpe.get(0).setPrincipal(true);

            db.update(lpe.get(0));

            pessoaEmpresa = lpe.get(0);
            renderJuridicaPesquisa = true;
        }
        RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
    }

    public void alterarEmpresaAtual(PessoaEmpresa pe) {
        Dao di = new Dao();
        di.openTransaction();

        if (!pe.getDemissao().isEmpty()) {
            GenericaMensagem.error("Atenção", "Pessoa demissionada não pode ser Reativa!");
            return;
        }

        if (pessoaEmpresa.getId() == -1) {
            pe.setPrincipal(true);
            if (!di.update(pe)) {
                di.rollback();
                return;
            }
            pessoaEmpresa = pe;
        } else {
            pessoaEmpresa.setPrincipal(false);
            pe.setPrincipal(true);

            if (!di.update(pessoaEmpresa) || !di.update(pe)) {
                di.rollback();
                return;
            }
            pessoaEmpresa = pe;
        }

        di.commit();
    }

    public String associarFisica() {
        if (new SociosDao().existPessoasMesmaMatricula()) {
            GenericaMensagem.warn("Sistema", "Constam a mesma pessoa mais de uma vez na mesma matrícula!");
            return null;
        }
        if (new SociosDao().existMatriculaAtivaAtivacaoDesordenada()) {
            GenericaMensagem.warn("Sistema", "Matrícula ativa com id_servico_pessoa menor que último, favor entrar em contato com nosso suporte técnico.");
            return null;
        }
        boolean reativar = false;
        Pessoa p = fisica.getPessoa();
        if (tipoCadastro == -1) {
            GenericaMensagem.warn("Validação", "Cadastre uma pessoa fisica para associar!");
            return "pessoaFisica";
        } else if (tipoCadastro == 1) {
            if (socios.getId() == -1) {
                if (fisica.getPessoa().getDocumento().isEmpty() || fisica.getPessoa().getDocumento().equals("0")) {
                    GenericaMensagem.warn("Erro", "Para se associar é necessário ter número de documento (CPF) no cadastro!");
                    return null;
                }
            }
            reativar = socios.getServicoPessoa().isAtivo();
        } else if (tipoCadastro == 2) {
            reativar = socios.getServicoPessoa().isAtivo();
        } else if (tipoCadastro == 3) {
            p = socios.getMatriculaSocios().getTitular();
            reativar = socios.getServicoPessoa().isAtivo();
        } else if (tipoCadastro == 4) {
            reativar = socios.getServicoPessoa().isAtivo();
        } else if (tipoCadastro == 5) {
            reativar = socios.getServicoPessoa().isAtivo();
        }

        if (socios.getId() == -1 || (socios.getId() != -1 && (socios.getMatriculaSocios().getDtInativo() != null || !socios.getServicoPessoa().isAtivo()))) {
            if (listaPessoaEndereco.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Cadastrar um Endereço!");
                return null;
            }
        }

        if (pessoaEmpresa.getId() != -1) {
            GenericaSessao.put("pessoaEmpresaPesquisa", pessoaEmpresa);
        }

        String retorno = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).socios();
        if (socios.getId() == -1) {
            reativar = false;
        }

        if (socios.getId() == -1) {
            if (!listernerValidacao(fisica, "associarFisica")) {
                return null;
            }
        } else if (socios.getId() != -1 && (socios.getMatriculaSocios().getDtInativo() != null || !socios.getServicoPessoa().isAtivo())) {
            if (!listernerValidacao(fisica, "associarFisica")) {
                return null;
            }
        }

        GenericaSessao.put("sociosBean", new SociosBean());
        SociosBean sb = (SociosBean) GenericaSessao.getObject("sociosBean");
        clear(0);
        sb.loadSocio(p, reativar);
        return retorno;
    }

    public String associarFisica(Pessoa _pessoa) {
        if (!listernerValidacao(fisica, "associarFisica")) {
            return null;
        }
        clear(0);
        String retorno = ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).socios();
        GenericaSessao.put("pessoaEmpresaPesquisa", (new PessoaEmpresaDBToplink()).pesquisaPessoaEmpresaPorPessoa(_pessoa.getId()));
        GenericaSessao.put("sociosBean", new SociosBean());
        SociosBean sb = (SociosBean) GenericaSessao.getObject("sociosBean");
        sb.loadSocio(fisica.getPessoa(), true);
        return retorno;
    }

    public String hojeRecadastro() {
        fisica.getPessoa().setDtRecadastro(DataHoje.dataHoje());
        return null;
    }

    public List getItens() {
        return itens;
    }

    public void setItens(List itens) {
        this.itens = itens;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public String getDescPesquisa() {
        if (porPesquisa.equals("matricula") || porPesquisa.equals("codigo_pessoa")) {
            try {
                Integer.parseInt(descPesquisa);
            } catch (Exception e) {
                this.descPesquisa = "";
            }
        }
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public PessoaProfissao getPessoaProfissao() {
        return pessoaProfissao;
    }

    public void setPessoaProfissao(PessoaProfissao pessoaProfissao) {
        this.pessoaProfissao = pessoaProfissao;
    }

    public PessoaEndereco getPessoaEndereco() {
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public String getIndicaTab() {
        return indicaTab;
    }

    public void setIndicaTab(String indicaTab) {
        this.indicaTab = indicaTab;
    }

    public Fisica getFisica() {
        if (fisica.getId() == -1) {
            tipo = "novo";
        } else {
            tipo = "naonovo";
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getRenAbreEnd() {
        return renAbreEnd;
    }

    public void setRenAbreEnd(String renAbreEnd) {
        this.renAbreEnd = renAbreEnd;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public int getIdProfissao() {
        return idProfissao;
    }

    public void setIdProfissao(int idProfissao) {
        this.idProfissao = idProfissao;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            JuridicaDB db = new JuridicaDBToplink();
            Juridica j = (Juridica) GenericaSessao.getObject("juridicaPesquisa");
            List listax = db.listaJuridicaContribuinte(j.getId());

            if (!listax.isEmpty()) {
                for (int i = 0; i < listax.size(); i++) {
                    if (((List) listax.get(0)).get(11) != null) {
                        // CONTRIBUINTE INATIVO
                        mensagemAviso = "Empresa Inativa não pode ser vinculada!";
                        visibleMsgAviso = true;
                    } else {
                        pessoaEmpresa.setJuridica(j);
                        renderJuridicaPesquisa = true;
                    }
                }
            } else {
                pessoaEmpresa.setJuridica(j);
                renderJuridicaPesquisa = true;
            }
            GenericaSessao.remove("juridicaPesquisa");
        }
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public boolean isRenderJuridicaPesquisa() {
        return renderJuridicaPesquisa;
    }

    public void setRenderJuridicaPesquisa(boolean renderJuridicaPesquisa) {
        this.renderJuridicaPesquisa = renderJuridicaPesquisa;
    }

    public int getIdIndexFisica() {
        return idIndexFisica;
    }

    public void setIdIndexFisica(int idIndexFisica) {
        this.idIndexFisica = idIndexFisica;
    }

    public List<PessoaEmpresa> getListaPessoaEmpresa() {
        PessoaEmpresaDB db = new PessoaEmpresaDBToplink();
        if (fisica.getId() != -1) {
            listaPessoaEmpresa = db.listaPessoaEmpresaPorFisica(fisica.getId());
        }
        return listaPessoaEmpresa;
    }

    public void setListaPessoaEmpresa(List<PessoaEmpresa> listaPessoaEmpresa) {
        this.listaPessoaEmpresa = listaPessoaEmpresa;
    }

    public int getIdIndexPessoaEmp() {
        return idIndexPessoaEmp;
    }

    public void setIdIndexPessoaEmp(int idIndexPessoaEmp) {
        this.idIndexPessoaEmp = idIndexPessoaEmp;
    }

    public Socios getSocios() {
        if (GenericaSessao.exists("socioPesquisa")) {
            socios = (Socios) GenericaSessao.getObject("socioPesquisa", true);
        }
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public String getMsgSocio() {
        return msgSocio;
    }

    public void setMsgSocio(String msgSocio) {
        this.msgSocio = msgSocio;
    }

    public String getLblSocio() {
        if (socios.getId() == -1) {
            lblSocio = "ASSOCIAR";
        } else if (socios.getId() != -1 && (socios.getMatriculaSocios().getDtInativo() != null || !socios.getServicoPessoa().isAtivo())) {
            lblSocio = "ASSOCIAR";
        } else {
            lblSocio = "VER CADASTRO";
        }
        return lblSocio;
    }

    public void setLblSocio(String lblSocio) {
        this.lblSocio = lblSocio;
    }

    public String getPesquisaPor() {
        return pesquisaPor;
    }

    public void setPesquisaPor(String pesquisaPor) {
        this.pesquisaPor = pesquisaPor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoSocio() {
        if (socios.getId() != -1) {
            if (socios.getMatriculaSocios().getTitular().getId() == fisica.getPessoa().getId()) {
                tipoSocio = "Titular";
            } else {
                tipoSocio = "Dependente";
            }
        } else {
            tipoSocio = "";
        }
        return tipoSocio;
    }

    public void setTipoSocio(String tipoSocio) {
        this.tipoSocio = tipoSocio;
    }

    public void limparCamposData() {
        if (pessoaEmpresa.getId() != -1) {
            if (pessoaEmpresa.getJuridica().getId() != -1) {
                if (!pessoaEmpresa.getDemissao().isEmpty()) {
                    pessoaEmpresa.setAdmissao("");
                    pessoaEmpresa.setDemissao("");
                    pessoaEmpresa = new PessoaEmpresa();
                }
            }
        }
    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
        List<FacesMessage> msgs = new ArrayList<>();
        Part files = (Part) value;
        if (files.getSize() > 1024) {
            msgs.add(new FacesMessage("file too big"));
        }
        if (!"text/plain".equals(files.getContentType())) {
            msgs.add(new FacesMessage("not a text file"));
        }
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

    public void mascaraPesquisaFisica() {
        descPesquisa = "";
        mask = Mask.getMascaraPesquisa(porPesquisa, true);
    }

    public void reloadList(Boolean addOrRemove) {
        Integer result = 0;
        if (addOrRemove) {
            result = offset + 500;
            if (result > count) {
                limit = count - offset;
                result = offset;
            } else {
                limit = 500;
            }
        } else {
            result = offset - 500;
            if (result < 0) {
                offset = 0;
            } else {
                if (count < result) {
                    limit = count - offset;
                    result = offset;
                } else {
                    limit = 500;
                }
                if (offset < 500) {
                    result = 500;
                }
            }
        }
        listaPessoaFisica.clear();
        offset = result;
        loadList(offset);
    }

    public void loadList() {
        limit = 500;
        offset = 0;
        List list = new ArrayList<>();
        if (!(descPesquisa.trim()).isEmpty()) {
            FisicaDBToplink fisicaDBToplink = new FisicaDBToplink();
            switch (pesquisaPor) {
                case "socioativo":
                    list = fisicaDBToplink.pesquisaPessoaSocio(descPesquisa.trim(), porPesquisa, comoPesquisa, null, null);
                    break;
                case "socio_titular_ativo":
                    list = fisicaDBToplink.pesquisaPessoaSocio(descPesquisa.trim(), porPesquisa, comoPesquisa, true, null, null);
                    break;
                case "pessoa":
                    list = fisicaDBToplink.pesquisaPessoa(descPesquisa.trim(), porPesquisa, comoPesquisa, null, null);
                    break;
                case "socioinativo":
                    list = fisicaDBToplink.pesquisaPessoaSocioInativo(descPesquisa.trim(), porPesquisa, comoPesquisa, null, null);
                    break;
            }
        }
        if (!list.isEmpty()) {
            try {
                count = Integer.parseInt(((List) list.get(0)).get(0).toString());
            } catch (Exception e) {
                count = 0;
            }
        }
        loadList(0);
    }

    public void loadList(Integer offset) {
        if (!(descPesquisa.trim()).isEmpty()) {
            FisicaDBToplink db = new FisicaDBToplink();
            switch (pesquisaPor) {
                case "socioativo":
                    listaPessoaFisica = db.pesquisaPessoaSocio(descPesquisa.trim(), porPesquisa, comoPesquisa, limit, offset);
                    break;
                case "socio_titular_ativo":
                    listaPessoaFisica = db.pesquisaPessoaSocio(descPesquisa.trim(), porPesquisa, comoPesquisa, true, limit, offset);
                    break;
                case "pessoa":
                    listaPessoaFisica = db.pesquisaPessoa(descPesquisa.trim(), porPesquisa, comoPesquisa, limit, offset);
                    break;
                case "socioinativo":
                    listaPessoaFisica = db.pesquisaPessoaSocioInativo(descPesquisa.trim(), porPesquisa, comoPesquisa, limit, offset);
                    break;
            }
        }
    }

    public List<Fisica> getListaPessoaFisica() {
        return listaPessoaFisica;
    }

    public void setListaPessoaFisica(List<Fisica> listaPessoaFisica) {
        this.listaPessoaFisica = listaPessoaFisica;
    }

    public String pessoaEmpresaString(Fisica f) {
        String pessoaEmpresaString = "";
        PessoaEmpresaDB pessoaEmpresaDB = new PessoaEmpresaDBToplink();
        PessoaEmpresa pe = (PessoaEmpresa) pessoaEmpresaDB.pesquisaPessoaEmpresaPorFisica(f.getId());
        if (pe != null) {
            if (pe.getId() != -1) {
                pessoaEmpresaString = pe.getJuridica().getPessoa().getNome();
            }
        }
        return (pessoaEmpresaString.isEmpty()) ? "SEM EMPRESA" : pessoaEmpresaString;
    }

    public void novoEndereco(TabChangeEvent event) {
        indexNovoEndereco = ((AccordionPanel) event.getComponent()).getActiveIndex();
    }

    public void accordion(TabChangeEvent event) {
        indexPessoaFisica = ((TabView) event.getComponent()).getActiveIndex();

        if (indexPessoaFisica == 3) {
            listaServicoPessoa.clear();
        }

        // TAB MOVIMENTOS
        if (indexPessoaFisica == 5) {
            loadListaMovimento();
        }

        // TAB OPOSIÇÕES
        if (indexPessoaFisica == 6) {
            filtroOposicao = "ativas";
            loadListaOposicao();
        }

        // TAB DIGITALIZAÇÃO DE DOCUMENTOS
        if (indexPessoaFisica == 7) {
            // loadListaDocumentos();
        }
    }

    public String getIndexNovoEndereco() {
        return indexNovoEndereco;
    }

    public void setIndexNovoEndereco(String indexNovoEndereco) {
        this.indexNovoEndereco = indexNovoEndereco;
    }

    public int getIndexPessoaFisica() {
        return indexPessoaFisica;
    }

    public void setIndexPessoaFisica(int indexPessoaFisica) {
        this.indexPessoaFisica = indexPessoaFisica;
    }

    public String getCliente() {
        if (cliente.equals("")) {
            if (GenericaSessao.exists("sessaoCliente")) {
                return GenericaSessao.getString("sessaoCliente");
            }
        }
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void loadingImage() throws InterruptedException {
//        Thread.sleep(5000);
//        if (fisica.getId() != -1) {
//            salvarImagem();
//        }
//        PF.closeDialog("dlg_loading_image");
    }
//
//    public void capturar(CaptureEvent captureEvent) throws FileNotFoundException {
//        UUID uuidX = UUID.randomUUID();
//        String nameTemp = uuidX.toString().replace("-", "_");
//
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        byte[] data = captureEvent.getData();
//        String path;
//
//        Diretorio.criar("temp/foto/" + getUsuario().getId());
//
//        path = servletContext.getRealPath("") + "Cliente" + File.separator + getCliente() + File.separator + "temp" + File.separator + "foto" + File.separator + getUsuario().getId() + File.separator + nameTemp + ".png";
//
//        try {
//            FileUtils.writeByteArrayToFile(new File(path), data);
//        } catch (IOException e) {
//            throw new FacesException("Error in writing captured image.", e);
//        }
//
//        fotoPerfilStreamed = ImageConverter.getImageStreamed(new File(path), "image/png");
//        nomeFoto = nameTemp;
//    }

//    public void upload(FileUploadEvent event) {
//        UUID uuidX = UUID.randomUUID();
//        String nameTemp = uuidX.toString().replace("-", "_");
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        Diretorio.criar("temp/foto/" + getUsuario().getId());
//        String path = servletContext.getRealPath("") + "Cliente" + File.separator + getCliente() + File.separator + "temp" + File.separator + "foto" + File.separator + getUsuario().getId() + File.separator + nameTemp + ".png";
//        try {
//            FileUtils.writeByteArrayToFile(new File(path), event.getFile().getContents());
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        nomeFoto = nameTemp;
//    }
    public void apagarImagem() {
        boolean sucesso = false;
        try {
            String path = ("/resources/cliente/" + ControleUsuarioBean.getCliente() + "/imagens/pessoa/" + fisica.getPessoa().getId() + "/").toLowerCase();
            String fcaminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(path);
            if (new File((fcaminho + "/" + fisica.getFoto() + ".png")).exists() && FileUtils.deleteQuietly(new File(fcaminho + "/" + fisica.getFoto() + ".png"))) {
                sucesso = true;
            } else if (new File((fcaminho + "/" + fisica.getFoto() + ".jpg")).exists() && FileUtils.deleteQuietly(new File(fcaminho + "/" + fisica.getFoto() + ".jpg"))) {
                sucesso = true;
            } else if (new File((fcaminho + "/" + fisica.getFoto() + ".jpeg")).exists() && FileUtils.deleteQuietly(new File(fcaminho + "/" + fisica.getFoto() + ".jpeg"))) {
                sucesso = true;
            } else if (new File((fcaminho + "/" + fisica.getFoto() + ".gif")).exists() && FileUtils.deleteQuietly(new File(fcaminho + "/" + fisica.getFoto() + ".gif"))) {
                sucesso = true;
            }

            if (!sucesso) {
                GenericaMensagem.error("Atenção", "Imagem não encontrada no Servidor!");
                return;
            }

            fisica.setDtFoto(null);
            fisica.setFoto("");
            new Dao().update(fisica, true);

        } catch (Exception e) {
            GenericaMensagem.error("Sistema", e.getMessage());
        }
    }

    public void validaPIS() {
        GenericaMensagem.warn("Validação", "Número do PIS inválido!");
        ValidaDocumentos.isValidoPIS(fisica.getPis());
    }

    public boolean isReadyOnlineNaturalidade() {
        return readyOnlineNaturalidade;
    }

    public void setReadyOnlineNaturalidade(boolean readyOnlineNaturalidade) {
        this.readyOnlineNaturalidade = readyOnlineNaturalidade;
    }

    public boolean isDisabledNaturalidade() {
        return disabledNaturalidade;
    }

    public void setDisabledNaturalidade(boolean disabledNaturalidad) {
        this.disabledNaturalidade = disabledNaturalidad;
    }

    public String[] getImagensTipo() {
        return imagensTipo;
    }

    public void setImagensTipo(String[] imagensTipo) {
        this.imagensTipo = imagensTipo;
    }

    public List<Socios> getListaSocioInativo() {
        if (listaSocioInativo.isEmpty() && fisica.getId() != -1) {
            //listaSocioInativo = new SociosDBToplink().listaSocioTitularInativoPorPessoa(fisica.getPessoa().getId()); 
            listaSocioInativo = new SociosDBToplink().pesquisaSocioPorPessoaInativo(fisica.getPessoa().getId());
            for (int i = 0; i < listaSocioInativo.size(); i++) {
                if (fisica.getPessoa().getId() != listaSocioInativo.get(i).getMatriculaSocios().getTitular().getId()) {
                    listaSocioInativo.clear();
                    break;
                }
            }
        }
        return listaSocioInativo;
    }

    public void setListaSocioInativo(List<Socios> listaSocioInativo) {
        this.listaSocioInativo = listaSocioInativo;
    }

    public Endereco getEnderecox() {
        if (GenericaSessao.getObject("enderecoPesquisa") != null) {
            enderecox = (Endereco) GenericaSessao.getObject("enderecoPesquisa", true);

            enderecoCompleto = enderecox.getLogradouro().getDescricao() + " "
                    + enderecox.getDescricaoEndereco().getDescricao() + ", "
                    + enderecox.getCidade().getCidade() + " - "
                    + enderecox.getCidade().getUf();

            if (visibleEditarEndereco) {
                pessoaEndereco.setEndereco(enderecox);
            }
        }
        return enderecox;
    }

    public void setEnderecox(Endereco enderecox) {
        this.enderecox = enderecox;
    }

    public List<PessoaEndereco> getListaPessoaEndereco() {
        if (fisica.getId() != -1 && listaPessoaEndereco.isEmpty()) {
            PessoaEnderecoDao db = new PessoaEnderecoDao();
            listaPessoaEndereco = db.pesquisaEndPorPessoa(fisica.getPessoa().getId());
            if(listaPessoaEndereco.size() == 1) {
                
            }
        }
        return listaPessoaEndereco;
    }

    public void setListaPessoaEndereco(List<PessoaEndereco> listaPessoaEndereco) {
        this.listaPessoaEndereco = listaPessoaEndereco;
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

    public boolean isVisibleEditarEndereco() {
        return visibleEditarEndereco;
    }

    public void setVisibleEditarEndereco(boolean visibleEditarEndereco) {
        this.visibleEditarEndereco = visibleEditarEndereco;
    }

    public List<DataObject> getListaServicoPessoa() {
        if (fisica.getId() != -1 && listaServicoPessoa.isEmpty()) {
            FisicaDB db = new FisicaDBToplink();
            //listaServicoPessoa = db.listaServicoPessoa(fisica.getPessoa().getId(), chkDependente);
            Integer id_categoria = (getSocios() != null && socios.getId() != -1) ? socios.getMatriculaSocios().getCategoria().getId() : null;

            List<Vector> result = db.listaHistoricoServicoPessoa(fisica.getPessoa().getId(), id_categoria, chkSomenteDestaPessoa);
            somaValoresHistorico = "0,00";
            for (Vector linha : result) {
                listaServicoPessoa.add(new DataObject(linha, null));
                somaValoresHistorico = Moeda.converteR$Float(Moeda.somaValores(Moeda.converteUS$(somaValoresHistorico), ((Double) linha.get(8)).floatValue()));
            }
        }
        return listaServicoPessoa;
    }

    public void setListaServicoPessoa(List<DataObject> listaServicoPessoa) {
        this.listaServicoPessoa = listaServicoPessoa;
    }

    public boolean isChkSomenteDestaPessoa() {
        return chkSomenteDestaPessoa;
    }

    public void setChkSomenteDestaPessoa(boolean chkSomenteDestaPessoa) {
        this.chkSomenteDestaPessoa = chkSomenteDestaPessoa;
    }

    public String getStrEndereco() {
        if(fisica.getId() == -1) {
            if (!listaPessoaEndereco.isEmpty()) {
                strEndereco
                        = listaPessoaEndereco.get(1).getEndereco().getLogradouro().getDescricao() + " "
                        + listaPessoaEndereco.get(1).getEndereco().getDescricaoEndereco().getDescricao() + " " + listaPessoaEndereco.get(1).getNumero() + ", "
                        + listaPessoaEndereco.get(1).getEndereco().getBairro().getDescricao() + ", " + listaPessoaEndereco.get(1).getComplemento() + " "
                        + listaPessoaEndereco.get(1).getEndereco().getCidade().getCidade() + "  -  "
                        + listaPessoaEndereco.get(1).getEndereco().getCidade().getUf() + " / CEP: " + AnaliseString.mascaraCep(listaPessoaEndereco.get(1).getEndereco().getCep());
            }            
        }
        return strEndereco;
    }

    public void setStrEndereco(String strEndereco) {
        this.strEndereco = strEndereco;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public void existePessoaOposicaoPorPessoa() {
        if (!fisica.getPessoa().getDocumento().isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            pessoaOposicao = odbt.existPessoaDocumentoPeriodo(fisica.getPessoa().getDocumento());
        }
    }

    public boolean existePessoaOposicaoPorDocumento(String documento) {
        if (!documento.isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            return odbt.existPessoaDocumentoPeriodo(documento);
        }
        return false;
    }

    public boolean isPessoaOposicao() {
        return pessoaOposicao;
    }

    public void setPessoaOposicao(boolean pessoaOposicao) {
        this.pessoaOposicao = pessoaOposicao;
    }

    public Integer getTipoCadastro() {
        if (fisica.getId() == -1) {
            // CADASTRO NOVO
            tipoCadastro = -1;
            return tipoCadastro;
        } else if (socios.getId() == -1) {
            // CADASTRO PARA ALTERAR
            tipoCadastro = 1;
            return tipoCadastro;
        }

        if (fisica.getPessoa().getId() == socios.getMatriculaSocios().getTitular().getId()) {
            if (socios.getServicoPessoa().isAtivo()) {
                // SÓCIO TITULAR
                tipoCadastro = 2;
            } else if (listaSocioInativo.isEmpty()) {
                // SÓCIO INATIVO
                tipoCadastro = 4;
            } else if (!listaSocioInativo.isEmpty()) {
                // SÓCIO INATIVO
                tipoCadastro = 5;
            }
        } else if (socios.getServicoPessoa().isAtivo() && ((socios.getServicoPessoa().getReferenciaValidade() != null && socios.getServicoPessoa().getReferenciaValidade().isEmpty()) || DataHoje.maiorData("01/" + socios.getServicoPessoa().getReferenciaValidade(), DataHoje.data()))) {
            // SÓCIO DEPENDENTE
            tipoCadastro = 3;
        } else if (listaSocioInativo.isEmpty()) {
            // SÓCIO INATIVO
            tipoCadastro = 4;
        } else if (!listaSocioInativo.isEmpty()) {
            // SÓCIO INATIVO
            tipoCadastro = 5;
        }
        return tipoCadastro;
    }

    public void setTipoCadastro(Integer tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<Socios> getListaSocios() {
        return listaSocios;
    }

    public void setListaSocios(List<Socios> listaSocios) {
        this.listaSocios = listaSocios;
    }

    public void listenerSocios(Integer idPessoa) {
        listaSocios.clear();
        SociosDB sociosDB = new SociosDBToplink();
        Socios s = sociosDB.pesquisaSocioPorPessoaAtivo(idPessoa);
        if (s != null && s.getId() != -1) {
            SociosDao sociosDao = new SociosDao();
            listaSocios = sociosDao.pesquisaDependentePorMatricula(s.getMatriculaSocios().getId(), false);
        }
    }

    public void listernerTipoPesquisa(String tipoValidacao) {
        String descricao = "";
        String como = "";
        String por = "";
        FisicaBean fisicaBean = new FisicaBean();
        if (GenericaSessao.exists("fisicaBean")) {
            descricao = ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getDescPesquisa();
            List list = ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaPessoaFisica();
            como = ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getComoPesquisa();
            por = ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getPorPesquisa();
            GenericaSessao.remove("fisicaBean");
            GenericaSessao.remove("fisicaPesquisa");
            fisicaBean.setDescPesquisa(descricao);
            fisicaBean.setListaPessoaFisica(list);
            fisicaBean.setComoPesquisa(como);
            fisicaBean.setPorPesquisa(por);
        }
        fisicaBean.setValidacao(tipoValidacao);
        if (tipoValidacao.equals("socio_titular_ativo")) {
            fisicaBean.setPesquisaPor("socio_titular_ativo");
        }
        if (tipoValidacao.equals("multiple")) {
            fisicaBean.setMultiple(true);
        }
        fisicaBean.getListaPessoaFisica();
        GenericaSessao.put("fisicaBean", fisicaBean);
    }

    public String getValidacao() {
        return validacao;
    }

    public void setValidacao(String validacao) {
        this.validacao = validacao;
    }

    public Boolean listernerValidacao(Fisica f, String tipoValidacao) {
        pessoaOposicao = false;
        Boolean permite = true;
        Pessoa p = f.getPessoa();
        Socios s;
        Integer count = 0;
        if (validacao.isEmpty() && tipoValidacao.isEmpty()) {
            return permite;
        } else if (!tipoValidacao.isEmpty()) {
            validacao = tipoValidacao;
        }
        // SÓCIO
        if (validacao.equals("socioativo") || validacao.equals("socio_titular_ativo")) {
            SociosDB sociosDB = new SociosDBToplink();
            s = sociosDB.pesquisaSocioPorPessoa(p.getId());
            if (s.getId() == -1) {
                count++;
                GenericaMensagem.warn("Mensagem: (" + count + ")", "Pessoa não é sócia!");
                permite = false;
            }
            if (!s.getServicoPessoa().isAtivo()) {
                count++;
                GenericaMensagem.warn("Mensagem: (" + count + ")", "Sócio está inátivo!");
                permite = false;
            }
            if (validacao.equals("socio_titular_ativo")) {
                if (s.getMatriculaSocios().getTitular().getId() != p.getId()) {
                    count++;
                    GenericaMensagem.warn("Mensagem: (" + count + ")", "Sócio não é titular!");
                    permite = false;
                }
            }
        }
        // OPOSIÇÃO
        switch (validacao) {
            case "matriculaEscola":
            case "matriculaAcademia":
            case "convenioMedico":
            case "locacaoFilme":
            case "associarFisica":
                if (!p.getDocumento().isEmpty()) {
                    OposicaoDao odbt = new OposicaoDao();
                    if (odbt.existPessoaDocumentoPeriodo(p.getDocumento())) {
                        count++;
                        pessoaOposicao = true;
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "Contém carta(s) de oposição!");
                        permite = false;
                    }
                }
                break;
        }

        // DÉBITOS
        switch (validacao) {
            case "convenioMedico":
            case "matriculaAcademia":
            case "emissaoGuias":
            case "geracaoDebitosCartao":
            case "locacaoFilme":
            case "associarFisica":
                FunctionsDao functionsDao = new FunctionsDao();
                if (functionsDao.inadimplente(p.getId())) {
                    count++;
                    GenericaMensagem.warn("Mensagem: (" + count + ")", "Existe(m) débito(s)!");
                    permite = false;
                }
                break;
        }

        // BLOQUEIO
        switch (validacao) {
            case "matriculaAcademia":
            case "matriculaEscola":
            case "emissaoGuias":
            case "lancamentoIndividual":
            case "geracaoDebitosCartao":
            case "locacaoFilme":
            case "associarFisica":
                PessoaComplemento pc = f.getPessoa().getPessoaComplemento();
                if (pc.getBloqueiaObsAviso()) {
                    count++;
                    GenericaMensagem.fatal("Mensagem: (" + count + ")", "Cadastro bloqueado!");
                    permite = false;
                }
                if (pc.getObsAviso() != null && !pc.getObsAviso().isEmpty()) {
                    count++;
                    GenericaMensagem.warn("Mensagem de bloqueio: (" + count + ")", pc.getObsAviso());
                }
                break;
        }

        // CUPOM
        switch (validacao) {
            case "cupomMovimento":
                getInCategoriaSocio();
                if (inCategoriaSocio != null && !inCategoriaSocio.isEmpty()) {
                    Socios soc = fisica.getPessoa().getSocios();
                    if (soc == null) {
                        count++;
                        GenericaMensagem.fatal("Mensagem: (" + count + ")", "Necessário ser sócio!");
                        permite = false;
                    } else if (soc != null && soc.getId() != -1) {
                        String[] in = inCategoriaSocio.split(",");
                        Boolean t = false;
                        for (int i = 0; i < in.length; i++) {
                            if (in[i].equals("" + soc.getMatriculaSocios().getCategoria().getId())) {
                                t = true;
                                break;
                            }
                        }
                        if (!t) {
                            count++;
                            GenericaMensagem.fatal("Mensagem: (" + count + ")", "Sócio não pertence a nenhuma categoria específicada no cupom!");
                            permite = false;
                        }
                    }
                }
                break;
        }
        return permite;
    }

    public String getPath() {
        if (fisica.getId() == -1) {
            return "temp/foto/" + new PessoaUtilitarios().getUsuarioSessao().getId();
        } else {
            return "Imagens/Fotos";
        }
    }

    public void clear(Integer tCase) {
        if (tCase == 0) {
            try {
//                GenericaSessao.remove("cropperBean");
//                GenericaSessao.remove("uploadBean");
//                GenericaSessao.remove("photoCamBean");
//                FileUtils.deleteDirectory(new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "/Cliente/" + getCliente() + "/temp/" + "foto/" + getUsuario().getId()));
//                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + -1 + ".png"));
//                if (f.exists()) {
//                    f.delete();
//                }
            } catch (Exception ex) {
                ex.getMessage();
            }
        } else if (tCase == 1) {
            fisica.getPessoa().setDtRecadastro(fisica.getPessoa().getDtCriacao());
        } else if (tCase == 2) {
            dtRecadastro = DataHoje.dataHoje();
        }
    }

    public Boolean getMultiple() {
        return multiple;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public List<Fisica> getSelectedFisica() {
        return selectedFisica;
    }

    public void setSelectedFisica(List<Fisica> selectedFisica) {
        this.selectedFisica = selectedFisica;
    }

    public void removeSelected(Fisica f) {
        selectedFisica.remove(f);
    }

    public String converteData(Date data) {
        return DataHoje.converteData(data);
    }

    public String converteMoeda(String valor) {
        return Moeda.converteR$(valor);
    }

    public String getSomaValoresHistorico() {
        return somaValoresHistorico;
    }

    public void setSomaValoresHistorico(String somaValoresHistorico) {
        this.somaValoresHistorico = somaValoresHistorico;
    }

    public List<Vector> getListaMovimento() {
        return listaMovimento;
    }

    public void setListaMovimento(List<Vector> listaMovimento) {
        this.listaMovimento = listaMovimento;
    }

    public String getTipoStatusMovimento() {
        return tipoStatusMovimento;
    }

    public void setTipoStatusMovimento(String tipoStatusMovimento) {
        this.tipoStatusMovimento = tipoStatusMovimento;
    }

    public String getTipoPesquisaMovimento() {
        return tipoPesquisaMovimento;
    }

    public void setTipoPesquisaMovimento(String tipoPesquisaMovimento) {
        this.tipoPesquisaMovimento = tipoPesquisaMovimento;
    }

    public String getInativoDesde() {
        if (socios.getId() == -1) {
            //lblSocio = "ASSOCIAR";
            inativoDesde = "";
        } else if (socios.getId() != -1 && (socios.getMatriculaSocios().getDtInativo() != null || !socios.getServicoPessoa().isAtivo())) {
            // lblSocio = "ASSOCIAR";
            inativoDesde = (!socios.getMatriculaSocios().getInativo().isEmpty()) ? socios.getMatriculaSocios().getInativo() : socios.getServicoPessoa().getReferenciaValidade();
        } else {
            //   lblSocio = "VER CADASTRO";
            inativoDesde = "";
        }
        return inativoDesde;
    }

    public void setInativoDesde(String inativoDesde) {
        this.inativoDesde = inativoDesde;
    }

    public boolean isVisibleMsgAviso() {
        return visibleMsgAviso;
    }

    public void setVisibleMsgAviso(boolean visibleMsgAviso) {
        this.visibleMsgAviso = visibleMsgAviso;
    }

    public String getMensagemAviso() {
        return mensagemAviso;
    }

    public void setMensagemAviso(String mensagemAviso) {
        this.mensagemAviso = mensagemAviso;
    }

    public String getRecadastro() {
        return DataHoje.converteData(dtRecadastro);
    }

    public void setRecadastro(String recadastro) {
        this.dtRecadastro = DataHoje.converte(recadastro);
    }

    public Date getDtRecadastro() {
        return dtRecadastro;
    }

    public void setDtRecadastro(Date dtRecadastro) {
        this.dtRecadastro = dtRecadastro;
    }

    public void loadDataRecadastro() {
        dtRecadastro = DataHoje.dataHoje();
    }

    public void updateDataRecadastro() {
        fisica.getPessoa().setDtRecadastro(dtRecadastro);
        if (fisica.getId() != -1) {
            Fisica f = (Fisica) new Dao().find(new Fisica(), fisica.getId());
            String antes = " De: ID - " + fisica.getId()
                    + " - Nome: " + f.getPessoa().getNome()
                    + " - Nascimento: " + f.getNascimento()
                    + " - CPF: " + f.getPessoa().getDocumento()
                    + " - RG: " + f.getRg()
                    + " - Recadastro : " + fisica.getPessoa().getRecadastroString();
            PessoaDBToplink pessoaDao = new PessoaDBToplink();
            Date date = fisica.getPessoa().getDtAtualizacao();
            fisica.getPessoa().setDtAtualizacao(new Date());
            new Dao().rebind(fisica);
            if (pessoaDao.updateRecadastro(fisica.getPessoa())) {
                NovoLog novoLog = new NovoLog();
                novoLog.setTabela("pes_fisica");
                novoLog.setCodigo(fisica.getId());
                novoLog.update(antes,
                        " Recadastro > Nome: " + fisica.getPessoa().getNome()
                        + " - Nascimento: " + f.getNascimento()
                        + " - CPF: " + fisica.getPessoa().getDocumento()
                        + " - RG: " + fisica.getRg()
                        + " - Recadastro : " + fisica.getPessoa().getRecadastroString());
                if (pessoaDao.updateAtualizacao(fisica.getPessoa())) {
                    new Dao().rebind(fisica.getPessoa());
                    GenericaMensagem.info("Sucesso", "Registro atualizado!");
                    return;
                }
                GenericaMensagem.info("Sucesso", "Registro atualizado!");
                return;
            }
            fisica.getPessoa().setDtAtualizacao(date);
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void setListMalaDireta(List<MalaDireta> listMalaDireta) {
        this.listMalaDireta = listMalaDireta;
    }

    public List<SelectItem> getListMalaDiretaGrupo() {
        if (habilitaMalaDireta) {
            if (listMalaDiretaGrupo.isEmpty()) {
                idMalaDiretaGrupo = null;
                List<MalaDiretaGrupo> listMDG = new Dao().list(new MalaDiretaGrupo(), true);
                int x = 0;
                for (int i = 0; i < listMDG.size(); i++) {
                    if (listMDG.get(i).getAtivo()) {
                        if (x == 0) {
                            idMalaDiretaGrupo = "" + listMDG.get(i).getId();
                        }
                        listMalaDiretaGrupo.add(new SelectItem(listMDG.get(i).getId(), listMDG.get(i).getDescricao()));
                        x++;
                    }
                }
            }
        }
        return listMalaDiretaGrupo;
    }

    public void setListMalaDiretaGrupo(List<SelectItem> listMalaDiretaGrupo) {
        this.listMalaDiretaGrupo = listMalaDiretaGrupo;
    }

    public Boolean getHabilitaMalaDireta() {
        if (this.habilitaMalaDireta) {
            getListMalaDiretaGrupo();
        } else {
            listMalaDiretaGrupo.clear();
            idMalaDiretaGrupo = null;
        }
        return habilitaMalaDireta;
    }

    public void setHabilitaMalaDireta(Boolean habilitaMalaDireta) {
        this.habilitaMalaDireta = habilitaMalaDireta;
        if (this.habilitaMalaDireta) {
            getListMalaDiretaGrupo();
        } else {
            listMalaDiretaGrupo.clear();
            idMalaDiretaGrupo = null;
        }
    }

    public String getIdMalaDiretaGrupo() {
        return idMalaDiretaGrupo;
    }

    public void setIdMalaDiretaGrupo(String idMalaDiretaGrupo) {
        this.idMalaDiretaGrupo = idMalaDiretaGrupo;
    }

    public void saveMalaDireta() {
        if (fisica.getId() != -1) {
            Dao dao = new Dao();
            MalaDireta md = new MalaDiretaDao().findByPessoa(fisica.getPessoa().getId());
            if (idMalaDiretaGrupo != null) {
                if (habilitaMalaDireta) {
                    MalaDiretaGrupo mdg = (MalaDiretaGrupo) dao.find(new MalaDiretaGrupo(), Integer.parseInt(idMalaDiretaGrupo));
                    if (md == null) {
                        md = new MalaDireta();
                        md.setMalaDiretaGrupo(mdg);
                        md.setPessoa(fisica.getPessoa());
                        if (dao.save(md, true)) {
                            GenericaMensagem.info("Sucesso", "Registro atualizado!");
                            return;
                        }
                    } else {
                        md.setMalaDiretaGrupo(mdg);
                        if (dao.update(md, true)) {
                            GenericaMensagem.info("Sucesso", "Registro atualizado!");
                            return;
                        }
                    }
                } else if (md != null) {
                    if (dao.delete(md, true)) {
                        idMalaDiretaGrupo = null;
                        listMalaDiretaGrupo.clear();
                        GenericaMensagem.info("Sucesso", "Registro atualizado!");
                        return;
                    }
                }
            } else if (md != null) {
                if (dao.delete(md, true)) {
                    idMalaDiretaGrupo = null;
                    listMalaDiretaGrupo.clear();
                    GenericaMensagem.info("Sucesso", "Registro atualizado!");
                    return;
                }
            }
        }
        GenericaMensagem.warn("Erro", "Ao atualizar registro!");
    }

    public void loadMalaDireta() {
        habilitaMalaDireta = false;
        MalaDireta md = new MalaDiretaDao().findByPessoa(fisica.getPessoa().getId());
        if (md != null) {
            habilitaMalaDireta = true;
            for (int i = 0; i < getListMalaDiretaGrupo().size(); i++) {
                MalaDiretaGrupo mdg = (MalaDiretaGrupo) new Dao().find(new MalaDiretaGrupo(), getListMalaDiretaGrupo().get(i).getValue());
                if (mdg.getAtivo()) {
                    if (md.getMalaDiretaGrupo().getId().equals(mdg.getId())) {
                        idMalaDiretaGrupo = "" + mdg.getId();
                        break;
                    }
                }
            }
        }
        if (idMalaDiretaGrupo == null) {
            habilitaMalaDireta = false;
        }
    }

    public List<Oposicao> getListaOposicao() {
        return listaOposicao;
    }

    public void setListaOposicao(List<Oposicao> listaOposicao) {
        this.listaOposicao = listaOposicao;
    }

//    public List<Documento> getListaDocumentos() {
//        return listaDocumentos;
//    }
//
//    public void setListaDocumentos(List<Documento> listaDocumentos) {
//        this.listaDocumentos = listaDocumentos;
//    }
//
//    public List<LinhaArquivo> getListaArquivos() {
//        return listaArquivos;
//    }
//
//    public void setListaArquivos(List<LinhaArquivo> listaArquivos) {
//        this.listaArquivos = listaArquivos;
//    }
//    public List<TmktHistorico> getListTelemarketing() {
//        if (fisica.getPessoa().getId() != -1) {
//            return new TmktHistoricoDao().findByPessoa(fisica.getPessoa().getId());
//        }
//        return new ArrayList();
//    }
    public String getFiltroOposicao() {
        return filtroOposicao;
    }

    public void setFiltroOposicao(String filtroOposicao) {
        this.filtroOposicao = filtroOposicao;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getDe() {
        int result = 0;
        if (offset <= 0) {
            offset = 0;
            return 0;
        } else {
            return offset;
        }
    }

    public Integer getAte() {
        int result = offset + 500;
        if (result > count) {
            result = count;
        }
        return result;
    }

    public String getInCategoriaSocio() {
        if (GenericaSessao.exists("inCategoriaSocio")) {
            inCategoriaSocio = GenericaSessao.getString("inCategoriaSocio", true);
        }
        return inCategoriaSocio;
    }

    public void setInCategoriaSocio(String inCategoriaSocio) {
        this.inCategoriaSocio = inCategoriaSocio;
    }
}
