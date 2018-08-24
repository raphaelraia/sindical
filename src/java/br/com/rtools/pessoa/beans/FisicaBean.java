package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.pessoa.dao.PessoaProfissaoDao;
import br.com.rtools.pessoa.dao.TipoEnderecoDao;
import br.com.rtools.pessoa.dao.TipoDocumentoDao;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.beans.MovimentosReceberSocialBean;
import br.com.rtools.associativo.beans.SociosBean;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.beans.PesquisaEnderecoBean;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ServicoPessoaDao;
import br.com.rtools.financeiro.dao.ServicoRotinaDao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.Recepcao;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.dao.CancelamentoDao;
import br.com.rtools.homologacao.dao.HomologacaoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.MalaDiretaDao;
import br.com.rtools.pessoa.dao.PessoaComplementoDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.utilitarios.PessoaUtilitarios;
import br.com.rtools.seguranca.Evento;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.sistema.SisAutorizacoesTipo;
import br.com.rtools.sistema.dao.SisAutorizacoesDao;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.File;
import java.io.IOException;
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
    private PessoaEmpresa pessoaEmpresaEdit = new PessoaEmpresa();
    private PessoaEmpresa pessoaEmpresaNova = null;
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
    private String pais = "Brasileira(o)";
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
    private Integer offset = 0;
    private Integer count = 0;
    private Integer limit = 500;

    private String inCategoriaSocio = null;
    private List<Fisica> listFisicaSugestao = new ArrayList();
    private Boolean cadastrar = false;
    private Boolean editar = false;
    private SisAutorizacoes sisAutorizacoes;
    private String alterType;
    private List<SisAutorizacoes> listSisAutorizacoes;
    private ConfiguracaoSocial configuracaoSocial;
    private ConfiguracaoArrecadacao configuracaoArrecadacao;
    private List<String> listSugestion;
    private String selectedSugestion;
    private String solicitarAutorizacao;
    private List<SelectItem> listServicosAutorizados;
    private Integer idServicosAutorizados;
    private List<SelectItem> listNrRegistro;
    private Integer nrRegistro = 1;
    
    public FisicaBean() {
        GenericaSessao.remove("pessoaComplementoBean");
        GenericaSessao.remove("sessaoSisAutorizacao");
        solicitarAutorizacao = "";
        sisAutorizacoes = new SisAutorizacoes();
        configuracaoSocial = ConfiguracaoSocial.get();
        configuracaoArrecadacao = ConfiguracaoArrecadacao.get();
        listSisAutorizacoes = new ArrayList();
        alterType = "";
        listSugestion = new ArrayList();
        selectedSugestion = new String();
        listServicosAutorizados = new ArrayList();
        loadListNrRegistro();
    }
    
    public void loadListServicosAutorizados() {
        listServicosAutorizados = new ArrayList();
        ServicoRotinaDao srd = new ServicoRotinaDao();
        // LIBERA EMISSÃO DE SERVIÇO COM DÉBITO (CADASTRO COM DÉBITO)
        List<Servicos> list = srd.pesquisaTodosServicosComRotinas(451);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServicosAutorizados = list.get(i).getId();
            }
            listServicosAutorizados.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "LIBERA EMISSÃO DE SERVIÇO COM DÉBITO (CADASTRO COM DÉBITO)"));
        }
    }

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

        MovimentoDao db = new MovimentoDao();
        int qnt = 0;

        List<Movimento> lm = new ArrayList();

        for (Vector listaMovimento1 : listaMovimento) {
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
            listaMovimento = new FisicaDao().listaMovimentoFisica(fisica.getPessoa().getId(), tipoStatusMovimento, tipoPesquisaMovimento);
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
        mensagem = "";
        NovoLog logs = new NovoLog();
        FisicaDao db = new FisicaDao();
        Pessoa pessoa = fisica.getPessoa();
        List listDocumento;
        if ((listaPessoaEndereco.isEmpty() || pessoa.getId() == -1) && enderecox.getId() != -1) {
            adicionarEnderecos();
        }

        boolean sucesso = false;
        Dao dao = new Dao();
        dao.openTransaction();
        pessoaUpper();

        if (fisica.getEstadoCivil().isEmpty() || fisica.getEstadoCivil().contains("Indefinido")) {
            mensagem = "Selecionar estado civil diferente de Indefinido!";
            return;
        }

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
                fisica.setNacionalidade(pais);
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
            Fisica f = (Fisica) dao.find(new Fisica(), fisica.getId());
            f = (Fisica) new Dao().rebind(f);
            /* 
            if(!f.getPessoa().getNome().equals(fisica.getPessoa().getNome())) {
                fisica.getPessoa().setNome(f.getPessoa().getNome());
            }
            if(!f.getPessoa().getDocumento().isEmpty() && !f.getPessoa().getDocumento().equals("0")) {
                fisica.getPessoa().setDocumento(f.getPessoa().getDocumento());
            }
             */
            fisica.getPessoa().setNome(f.getPessoa().getNome());
            if (fisica.getPessoa().getEmail1().isEmpty()) {
                if (configuracaoSocial.getObrigatorioEmail()) {
                    if (fisica.getPessoa().getSocios().getId() != -1) {
                        if (fisica.getPessoa().getIsTitular()) {
                            GenericaMensagem.warn("Validação", "INFORMAR E-MAIL DO TITULAR!");
                            return;
                        }
                    }
                }
            }
            fisica.getPessoa().setDtAtualizacao(new Date());
            fisica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), 1));
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
                        mensagem = "Esta pessoa já esta cadastrada! " + fisi1.getPessoa().getNome();
                        return;
                    }
                }
            }

            fisica.setNacionalidade(pais);
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
        salvarPessoaComplemento();
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
        mensagem = "";
        if (socios.getId() != -1) {
            mensagem = "Esse cadastro esta associado, desvincule para excluir!";
            return;
        }
        //PessoaDB dbPessoa = new PessoaDao();
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
            PessoaProfissaoDao dbPP = new PessoaProfissaoDao();
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
            PessoaEmpresaDao dbEM = new PessoaEmpresaDao();
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
        FisicaDao dao = new FisicaDao();
        Fisica f = dao.pesquisaFisicaPorPessoa(p.getId());
        f = (Fisica) new Dao().rebind(f);
        return editarFisica(f);
    }

    public String editarFisica(Fisica f) {
        return editarFisica(f, false);
    }

    public String editarFisica(Fisica f, Boolean completo) {
        GenericaSessao.remove("pessoaComplementoBean");
        if (f.getId() == -1) {
            return null;
        }
        Dao dao = new Dao();
        selectedFisica = new ArrayList<>();
        multiple = false;
        pessoaComplemento = new PessoaComplemento();
        String url = (String) GenericaSessao.getString("urlRetorno");
        fisica = (Fisica) new Dao().rebind(f);
        dao.refresh(f.getPessoa());
        solicitarAutorizacao = "";
        if (!listernerValidacao(f, url)) {
            if (!url.equals("pessoaFisica")) {
                PF.update("form_pesquisa_pessoa");
                return null;
            }
        }
        GenericaSessao.put("fisicaPesquisa", f);
        if (!url.equals("pessoaFisica") && !url.equals("menuPrincipal") && !completo) {
            GenericaSessao.put("linkClicado", true);
            return url;
        }
        PessoaEmpresaDao db = new PessoaEmpresaDao();
        PessoaProfissaoDao dbp = new PessoaProfissaoDao();
        GenericaSessao.remove("pessoaComplementoBean");
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        alterarEnd = true;
        listaPessoa = new ArrayList();
        msgSocio = "";

        loadPessoaEmpresa();

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
        for (int i = 0; i < getListaPaises().size(); i++) {
            if ((listaPaises.get(i).getLabel().toUpperCase()).contains(fisica.getNacionalidade().toUpperCase())) {
                pais = listaPaises.get(i).getLabel();
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
        loadListPessoaEmpresa();
        if (pessoaComplemento.getId() == -1) {
            pessoaComplemento = (PessoaComplemento) dao.rebind(new PessoaComplementoDao().findByPessoa(fisica.getPessoa().getId()));
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
            }
        }

        // LISTA DE OPOSIÇÕES --
        filtroOposicao = "ativas";
        loadListaOposicao();
        loadListaInativacao();
        // --

        // loadListaDocumentos();
        if (!getCadastrar() && !getEditar()) {
            return url;
        } else {
            return null;
        }
    }

    public void showImagemFisica() {
    }

    public void existePessoaDocumento() {
        if (fisica.getPessoa().getDocumento().equals("___.___.___-__")) {
            fisica.getPessoa().setDocumento("");
            PF.update("form_pessoa_fisica:i_tabview_fisica:i_p_cpf");
            PF.update("form_pessoa_fisica:i_tabview_fisica:idPessoaComplemento");
            return;
        }
        if (!fisica.getPessoa().getDocumento().isEmpty() && !fisica.getPessoa().getDocumento().equals("___.___.___-__")) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                mensagem = "Documento (CPF) inválido! " + fisica.getPessoa().getDocumento();
                GenericaMensagem.warn("Validação", "Documento (CPF) inválido! " + fisica.getPessoa().getDocumento());
                PF.update("form_pessoa_fisica:i_tabview_fisica:id_valida_documento");
                PF.update("form_pessoa_fisica:i_tabview_fisica:i_p_cpf");
                PF.update("form_pessoa_fisica:i_tabview_fisica:idPessoaComplemento");
                // fisica.getPessoa().setDocumento("");
                return;
            }
        }
        if (fisica.getId() == -1) {
            FisicaDao db = new FisicaDao();
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
            PessoaProfissaoDao dbp = new PessoaProfissaoDao();
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
            loadListaInativacao();
            loadListPessoaEmpresa();
            if (success) {
                RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
                RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_end_rendered");
            }
        }
    }

    public void existePessoaNomeNascimento() {
        if (fisica.getId() == -1) {
            Fisica f = null;
            if (!fisica.getNascimento().isEmpty() && !fisica.getPessoa().getNome().isEmpty()) {
                FisicaDao db = new FisicaDao();
                f = db.pesquisaFisicaPorNomeNascimento(fisica.getPessoa().getNome(), fisica.getDtNascimento());
                if (f != null) {
                    editarFisicaParametro(f);
                    pessoaUpper();
                    loadMalaDireta();
                    loadListaInativacao();
                    loadListPessoaEmpresa();
                    RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
                    showImagemFisica();
                }
            }
            if (f == null || f.getId() == -1) {
                if (fisica.getId() == -1) {
                    if (!fisica.getPessoa().getNome().isEmpty()) {
                        listFisicaSugestao = new ArrayList();
                        listFisicaSugestao = new FisicaDao().findByNome(fisica.getPessoa().getNome());
                        if (!listFisicaSugestao.isEmpty()) {
                            PF.openDialog("dlg_sugestoes");
                            PF.update("form_pessoa_fisica:i_sugestoes");
                        }
                    }
                }
            }
        }
    }

    public void useFisicaSugestao(Fisica f) {
        editarFisicaParametro(f);
        pessoaUpper();
        loadMalaDireta();
        loadListaInativacao();
        pessoaComplemento = fisica.getPessoa().getPessoaComplemento();
        PessoaProfissaoDao dbp = new PessoaProfissaoDao();
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
        loadListaInativacao();
        loadListPessoaEmpresa();
    }

    public String editarFisicaParametro(Fisica f) {
        Dao dao = new Dao();
        PessoaEmpresaDao db = new PessoaEmpresaDao();
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
        for (int i = 0; i < getListaPaises().size(); i++) {
            if ((listaPaises.get(i).getLabel().toUpperCase()).contains(f.getNacionalidade().toUpperCase())) {
                pais = listaPaises.get(i).getLabel();
                break;
            }
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
        loadListaInativacao();
        loadMalaDireta();
        return url;
    }

    public void loadListaInativacao() {
        listaSocioInativo = new ArrayList();
        if (fisica.getId() != -1 && socios != null && socios.getId() != -1 && socios.getParentesco().getId() == 1 && !socios.getServicoPessoa().isAtivo()) {
            listaSocioInativo = new SociosDao().pesquisaSocioPorPessoaInativo(fisica.getPessoa().getId(), true);
            for (int i = 0; i < listaSocioInativo.size(); i++) {
                if (fisica.getPessoa().getId() != listaSocioInativo.get(i).getMatriculaSocios().getTitular().getId()) {
                    listaSocioInativo.clear();
                    break;
                }
            }
        }
    }

    public void editarFisicaSocio(Fisica fis) {
        Dao dao = new Dao();
        SociosDao db = new SociosDao();
        socios = db.pesquisaSocioPorPessoaAtivo(fisica.getPessoa().getId());
        if (socios.getId() == -1) {
            List<Socios> ls = new SociosDao().pesquisaSocioPorPessoaInativo(fisica.getPessoa().getId());
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
        List result = new Dao().list(new Fisica());
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
    }

    public void editarPessoaEndereco(PessoaEndereco pessoaEnderecox, int index) {
        pessoaEndereco = pessoaEnderecox;
        visibleEditarEndereco = true;
        indexEndereco = index;
    }

    public String CarregarEndereco() {
        int idEndereco = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paramEndereco"));
        pessoaEndereco.setEndereco((Endereco) new Dao().find(new Endereco(), idEndereco));
        setEnderecoCompleto((pessoaEndereco.getEndereco().getLogradouro().getDescricao()) + " " + pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao());
        return "pessoaFisica";
    }

    public List<String> BuscaTipoEndereco(Object object) {
        String txtDigitado = object.toString().toLowerCase().toUpperCase();
        TipoEnderecoDao db = new TipoEnderecoDao();
        List<String> list = db.pesquisaTipoEnderecoParaFisica('%' + txtDigitado + '%');
        return list;
    }

    public List<String> BuscaTipoDocumento(Object object) {
        String txtDigitado = object.toString().toLowerCase().toUpperCase();
        TipoDocumentoDao db = new TipoDocumentoDao();
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
        PessoaEmpresaDao db = new PessoaEmpresaDao();
        Dao dao = new Dao();
        pessoaEmpresa.setNrRegistro(nrRegistro);
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
                List listax = new JuridicaDao().listaJuridicaContribuinte(pessoaEmpresa.getJuridica().getId());
                for (int i = 0; i < listax.size(); i++) {
                    if (((List) listax.get(0)).get(11) != null) {
                        if (pessoaEmpresa.getDtDemissao() == null) {
                            // CONTRIBUINTE INATIVO
                            mensagemAviso = "Empresa Inativa não pode ser vinculada! Somente com data de demissão";
                            visibleMsgAviso = true;
                            renderJuridicaPesquisa = true;
                            pessoaEmpresa = new PessoaEmpresa();
                            return;
                        }
                    }
                }
                dao.save(pessoaEmpresa, true);
            } else {
                dao.update(pessoaEmpresa, true);
            }

            if (pessoaEmpresa.getDemissao() != null && !pessoaEmpresa.getDemissao().isEmpty()) {
                pessoaEmpresa = new PessoaEmpresa();
                profissao = new Profissao();
                GenericaSessao.remove("juridicaPesquisa");
                renderJuridicaPesquisa = false;

                List<PessoaEmpresa> lpe = db.listaPessoaEmpresaPorFisicaDemissao(fisica.getId());

                if (!lpe.isEmpty()) {
                    lpe.get(0).setPrincipal(true);

                    dao.update(lpe.get(0), true);

                    pessoaEmpresa = lpe.get(0);
                    renderJuridicaPesquisa = true;
                }
            }
        }
    }

    public void novaEmpresa() {
        PessoaEmpresa pe = (PessoaEmpresa) new Dao().find(pessoaEmpresa);
        pessoaEmpresaNova = pe;
        listaPessoaEmpresa.add(pe);
        pessoaEmpresa = new PessoaEmpresa();
    }

    public void adicionarEmpresa() {
        if (fisica.getId() != -1 && pessoaEmpresa.getJuridica().getId() != -1) {
            if (pessoaEmpresa.getAdmissao().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Data de Admissão não pode estar vazia!");
                return;
            }

            Boolean principal = false;
            Boolean clear = true;
            if (pessoaEmpresa.getId() != -1 && pessoaEmpresa.isPrincipal() && pessoaEmpresa.getDtDemissao() == null) {
                principal = true;
                clear = false;
            }

            pessoaEmpresa.setFisica(fisica);
            pessoaEmpresa.setAvisoTrabalhado(false);
            pessoaEmpresa.setPrincipal(principal);
            pessoaEmpresa.setNrRegistro(nrRegistro);

            if (profissao.getProfissao() == null || profissao.getProfissao().isEmpty()) {
                pessoaEmpresa.setFuncao(null);
            } else {
                pessoaEmpresa.setFuncao(profissao);
            }

            Dao di = new Dao();

            di.openTransaction();

            if (pessoaEmpresa.getId() == -1) {
                List listax = new JuridicaDao().listaJuridicaContribuinte(pessoaEmpresa.getJuridica().getId());
                for (int i = 0; i < listax.size(); i++) {
                    if (((List) listax.get(0)).get(11) != null) {
                        if (pessoaEmpresa.getDtDemissao() == null) {
                            // CONTRIBUINTE INATIVO
                            mensagemAviso = "Empresa Inativa não pode ser vinculada! Somente com data de demissão";
                            GenericaMensagem.error("Validação", "Empresa Inativa não pode ser vinculada! Somente com data de demissão.");
                            visibleMsgAviso = true;
                            renderJuridicaPesquisa = true;
                            return;
                        }
                    }
                }
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
            if (clear) {
                GenericaMensagem.info("Sucesso", "Empresa Adicionada!");
                pessoaEmpresa = new PessoaEmpresa();
            } else {
                GenericaMensagem.info("Sucesso", "Registro atualizado!");
            }
            if (pessoaEmpresaNova != null && pessoaEmpresaNova.getId() != -1) {
                pessoaEmpresa = (PessoaEmpresa) di.find(pessoaEmpresaNova);
                pessoaEmpresaNova = null;
            }
            loadListPessoaEmpresa();

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
            FisicaDao db = new FisicaDao();
            PessoaEmpresaDao dbEmp = new PessoaEmpresaDao();
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
            List listNacionalidade = PessoaUtilitarios.loadListPaises();
            for (int i = 0; i < listNacionalidade.size(); i++) {
                listaPaises.add(new SelectItem(listNacionalidade.get(i).toString(), listNacionalidade.get(i).toString()));
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
            List<Profissao> lista = (List<Profissao>) new Dao().list(new Profissao(), true);
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == 0) {
                    idProfissao = i;
                }
                listaProfissoes.add(new SelectItem(i, lista.get(i).getProfissao(), "" + lista.get(i).getId()));
            }
        }
        return listaProfissoes;
    }

    public String getCidadeNaturalidade() {
        String nat;
        if (!pais.equals("Brasileira(o)")) {
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
                    if (!pais.equals("Brasileira(o)")) {
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

    public String excluirEmpresaAnterior(PessoaEmpresa pe) {
        HomologacaoDao dbAge = new HomologacaoDao();
        List<Agendamento> agendas = dbAge.pesquisaAgendamentoPorPessoaEmpresa(pe.getId());
        // CHAMADO 1262
        if (!agendas.isEmpty()) {
            GenericaMensagem.error("ATENÇÃO", "Empresa com data de demissão não pode ser removida!");
            return null;
        }

        Dao dao = new Dao();
        for (Agendamento agenda : agendas) {
            if (!dao.delete(agenda, true)) {
                GenericaMensagem.warn("Erro", "Não foi possível remover este agendamento!");
                return null;
            }
        }
        if (dao.delete(pe, true)) {
            GenericaMensagem.info("Sucesso", "Empresa removida com sucesso");
        } else {
            GenericaMensagem.warn("Erro", "Não foi possível remover esta empresa!");
        }
        loadListPessoaEmpresa();
        return null;
    }

    public void removerJuridicaPesquisada() {
        if (pessoaEmpresa.getId() != -1) {
            Dao dao = new Dao();
            if (!dao.delete(pessoaEmpresa, true)) {
                mensagem = "Empresa com Agendamento não pode ser excluída!";
                GenericaMensagem.warn("Erro", "Empresa com Agendamento não pode ser excluída!");
                PF.openDialog("dlg_painel_mensagem");
                return;
            }
        }
        GenericaSessao.remove("juridicaPesquisa");
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        renderJuridicaPesquisa = false;

        PessoaEmpresaDao db = new PessoaEmpresaDao();
        Dao dao = new Dao();
        List<PessoaEmpresa> lpe = db.listaPessoaEmpresaPorFisicaDemissao(fisica.getId());

        if (!lpe.isEmpty()) {
            lpe.get(0).setPrincipal(true);

            dao.update(lpe.get(0), true);

            pessoaEmpresa = lpe.get(0);
            renderJuridicaPesquisa = true;
        }
        RequestContext.getCurrentInstance().update("form_pessoa_fisica:i_panel_pessoa_fisica");
    }

    public void alterarEmpresaAtual(PessoaEmpresa pe) {
        Dao di = new Dao();

        if (!pe.getDemissao().isEmpty()) {
            GenericaMensagem.error("Atenção", "Pessoa demissionada não pode ser Reativa!");
            return;
        }

        nrRegistro = pessoaEmpresa.getNrRegistro();

        if (pessoaEmpresa.getId() == -1) {
            pe.setDtAlternaPrincipal(new Date());
            if (!di.update(pe, true)) {
                return;
            }
        } else {
            pe.setDtAlternaPrincipal(new Date());
            if (!di.update(pe, true)) {
                return;
            }
            pessoaEmpresa.setPrincipal(false);
            if (!di.update(pessoaEmpresa, true)) {
                return;
            }
            pe.setPrincipal(true);
            if (!di.update(pe, true)) {
                return;
            }
        }
        pessoaEmpresa = new PessoaEmpresa();
        loadPessoaEmpresa();
        loadListPessoaEmpresa();
    }

    public String associarFisica() {
        if (new SociosDao().existPessoasMesmaMatricula()) {
            GenericaMensagem.warn("Sistema", "Constam a mesma pessoa mais de uma vez na mesma matrícula!");
            return null;
        }
        if (new SociosDao().existMatriculaAtivaAtivacaoDesordenada()) {
            GenericaMensagem.warn("Sistema", "Matrícula ativa com id_servico_pessoa menor que último, favor entrar em contato com nosso suporte técnico.");
            List<Pessoa> list = new SociosDao().listMatriculaAtivaAtivacaoDesordenada(true, null);
            for (int i = 0; i < list.size(); i++) {
                GenericaMensagem.warn("" + (i + 1), "ID: " + list.get(i).getId() + " - Nome: " + list.get(i).getNome());
            }
            return null;
        }
        boolean reativar = false;
        Pessoa p = (Pessoa) new Dao().find(fisica.getPessoa());
        if (tipoCadastro == -1) {
            GenericaMensagem.warn("Validação", "Cadastre uma pessoa fisica para associar!");
            return "pessoaFisica";
        } else if (tipoCadastro == 1) {
            if (socios.getId() == -1) {
                if (p.getDocumento().isEmpty() || p.getDocumento().equals("0")) {
                    if (configuracaoSocial.getBloqueiaCpf()) {
                        GenericaMensagem.warn("Erro", "Para se associar é necessário ter número de documento (CPF) no cadastro!");
                        return null;
                    }
                }
                if (p.getEmail1().isEmpty()) {
                    if (configuracaoSocial.getObrigatorioEmail()) {
                        GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO, PARA ASSOCIAR!");
                        return null;
                    }
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
            if (p.getEmail1().isEmpty()) {
                if (configuracaoSocial.getObrigatorioEmail()) {
                    GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO, PARA ASSOCIAR!");
                    return null;
                }
            }
        } else if (tipoCadastro == 5) {
            reativar = socios.getServicoPessoa().isAtivo();
            if (p.getEmail1().isEmpty()) {
                if (configuracaoSocial.getObrigatorioEmail()) {
                    GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO, PARA ASSOCIAR!");
                    return null;
                }
            }
        }

        if (socios.getId() == -1 || (socios.getId() != -1 && (socios.getMatriculaSocios().getDtInativo() != null || !socios.getServicoPessoa().isAtivo()))) {
            if (DataHoje.menorData(p.getRecadastroString(), new DataHoje().decrementarDias(30, DataHoje.data()))) {
                GenericaMensagem.error("Atenção", "Conferir os dados para fins de RECADASTRAMENTO!");
                return null;
            }

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
        return associarFisica(_pessoa, null);
    }

    public String associarFisica(Pessoa _pessoa, Socios s) {
        // REATIVAR SÓCIO
        if (s != null) {
            if (Usuario.getUsuario().getId() != 1) {
                if (s.getMatriculaSocios().getDtInativo() != null) {
                    int diff = DataHoje.diffDays(s.getMatriculaSocios().getInativo(), DataHoje.data());
                    if (diff > s.getMatriculaSocios().getCategoria().getNrDiasReativacao()) {
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "Não é possível reativar sócio com mais de " + s.getMatriculaSocios().getCategoria().getNrDiasReativacao() + " dias de inativação!");
                        return null;
                    }
                }
            }
        }
        if (!listernerValidacao(fisica, "associarFisica")) {
            return null;
        }
        if (fisica.getPessoa().getEmail1().isEmpty()) {
            if (configuracaoSocial.getObrigatorioEmail()) {
                GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO, PARA ASSOCIAR!");
                return null;
            }
        }
        clear(0);
        String retorno = ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).socios();
        GenericaSessao.put("pessoaEmpresaPesquisa", (new PessoaEmpresaDao()).pesquisaPessoaEmpresaPorPessoa(_pessoa.getId()));
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

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
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
        if (GenericaSessao.exists("fisicaPesquisaEditar")) {
            editarFisica((Fisica) GenericaSessao.getObject("fisicaPesquisaEditar", true), true);
        }
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public int getIdProfissao() {
        return idProfissao;
    }

    public void setIdProfissao(int idProfissao) {
        this.idProfissao = idProfissao;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa") && !GenericaSessao.exists("tipoPessoaJuridica")) {
            JuridicaDao db = new JuridicaDao();
            Juridica j = (Juridica) GenericaSessao.getObject("juridicaPesquisa");
            List listax = db.listaJuridicaContribuinte(j.getId());

            if (!listax.isEmpty()) {
                for (int i = 0; i < listax.size(); i++) {
                    if (((List) listax.get(0)).get(11) != null) {
                        // CONTRIBUINTE INATIVO
                        mensagemAviso = "Empresa Inativa não pode ser vinculada! Somente com data de demissão";
                        visibleMsgAviso = true;
                        pessoaEmpresa.setJuridica(j);
                        renderJuridicaPesquisa = true;
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

    public void loadListPessoaEmpresa() {
        listaPessoaEmpresa = new ArrayList();
        PessoaEmpresaDao db = new PessoaEmpresaDao();
        if (fisica.getId() != -1) {
            listaPessoaEmpresa = db.listaPessoaEmpresaPorFisica(fisica.getId());
        }
    }

    public List<PessoaEmpresa> getListaPessoaEmpresa() {
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

    public void listener(String tcase) {
        if (tcase.equals("tipoPesquisa")) {
            if (porPesquisa.equals("nome") && !descPesquisa.isEmpty() && (descPesquisa.length() == 11 || descPesquisa.length() == 14)) {
                try {
                    if (ValidaDocumentos.isValidoCPF(descPesquisa)) {
                        String cpf = descPesquisa;
                        porPesquisa = "cpf";
                        mascaraPesquisaFisica();
                        descPesquisa = Mask.cpf(cpf);
                    }
                } catch (Exception e) {

                }
            }
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
        cadastrar = false;
        editar = false;
        limit = 500;
        offset = 0;
        List list = new ArrayList<>();
        if (!(descPesquisa.trim()).isEmpty()) {
            FisicaDao fisicaDao = new FisicaDao();
            switch (pesquisaPor) {
                case "socioativo":
                    list = fisicaDao.pesquisaPessoaSocio(descPesquisa.trim(), porPesquisa, comoPesquisa, null, null);
                    break;
                case "socio_titular_ativo":
                    list = fisicaDao.pesquisaPessoaSocio(descPesquisa.trim(), porPesquisa, comoPesquisa, true, null, null);
                    break;
                case "pessoa":
                    list = fisicaDao.pesquisaPessoa(descPesquisa.trim(), porPesquisa, comoPesquisa, null, null);
                    break;
                case "socioinativo":
                    list = fisicaDao.pesquisaPessoaSocioInativo(descPesquisa.trim(), porPesquisa, comoPesquisa, null, null);
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
            FisicaDao db = new FisicaDao();
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
        PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
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

    }

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
            if (listaPessoaEndereco.size() == 1) {

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
            FisicaDao db = new FisicaDao();
            //listaServicoPessoa = db.listaServicoPessoa(fisica.getPessoa().getId(), chkDependente);
            Integer id_categoria = (getSocios() != null && socios.getId() != -1) ? socios.getMatriculaSocios().getCategoria().getId() : null;

            List<Vector> result = db.listaHistoricoServicoPessoa(fisica.getPessoa().getId(), id_categoria, chkSomenteDestaPessoa);
            somaValoresHistorico = "0,00";
            for (Vector linha : result) {
                try {
                    listaServicoPessoa.add(new DataObject(linha, null));
                    double f = 0;
                    try {
                        f = ((Double) linha.get(8)).doubleValue();
                    } catch (Exception e) {

                    }
                    somaValoresHistorico = Moeda.converteR$Double(Moeda.soma(Moeda.converteUS$(somaValoresHistorico), f));
                } catch (Exception e) {
                    listaServicoPessoa = new ArrayList();
                    break;
                }
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
        if (!listaPessoaEndereco.isEmpty()) {
            if (listaPessoaEndereco.size() == 1) {
                strEndereco = "CADASTRAR TODOS OS ENDEREÇOS!";
            } else {
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
            pessoaOposicao = odbt.existPessoaDocumentoPeriodo(fisica.getPessoa().getDocumento(), configuracaoArrecadacao.getIgnoraPeriodoConvencaoOposicao());
        }
    }

    public boolean existePessoaOposicaoPorDocumento(String documento) {
        if (!documento.isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            return odbt.existPessoaDocumentoPeriodo(documento, configuracaoArrecadacao.getIgnoraPeriodoConvencaoOposicao());
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
        SociosDao sociosDao = new SociosDao();
        Socios s = sociosDao.pesquisaSocioPorPessoaAtivo(idPessoa);
        if (s != null && s.getId() != -1) {
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
        solicitarAutorizacao = "";
        String pesquisaFisicaTipo = GenericaSessao.getString("pesquisaFisicaTipo");
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
            SociosDao sociosDB = new SociosDao();
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
        // MAIOR DE IDADE
        switch (validacao) {
            case "vendasCaravana":
                if (pesquisaFisicaTipo.equals("responsavel")) {
                    if (f.getDtNascimento() == null) {
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "INFORMAR DATA DE NASCIMENTO!");
                        permite = false;
                    } else if (f.getIdade() < 18) {
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "RESPONSÁVEL DEVE SER MAIOR DE IDADE!");
                        permite = false;
                    }
                }
                break;
        }
        // OPOSIÇÃO
        switch (validacao) {
            case "convenioMedico":
            case "matriculaEscola":
            case "matriculaAcademia":
            case "locacaoFilme":
            case "agendamentos":
            case "cupomMovimento":
                // case "associarFisica":
                if (!p.getDocumento().isEmpty()) {
                    OposicaoDao odbt = new OposicaoDao();
                    if (odbt.existPessoaDocumentoPeriodo(p.getDocumento(), configuracaoArrecadacao.getIgnoraPeriodoConvencaoOposicao())) {
                        count++;
                        pessoaOposicao = true;
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "Contém carta(s) de oposição!");
                        permite = false;
                    }
                }
                break;
        }

        // DÉBITOS -- ORIGINAL
        // DÉBITOS
        /*
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
                    GenericaMensagem.warn("Mensagem: (" + count + ")", "EXISTE(m) DÉBITO(s)!");
                    permite = false;
                }
                break;
        }
         */
        // DÉBITOS -- LIBERAR SE FOR UTILIZAR AUTORIZAÇÃO
        switch (validacao) {
            case "convenioMedico":
            case "matriculaAcademia":
            case "emissaoGuias":
            case "geracaoDebitosCartao":
            case "locacaoFilme":
            case "associarFisica":
            case "conviteMovimento":
            case "agendamentos":
                GenericaSessao.remove("sessaoSisAutorizacao");
                Boolean ignoreCase = false;
                if (validacao.equals("emissaoGuias")) {
                    SisAutorizacoes sa = new SisAutorizacoesDao().findAutorizado(3, Usuario.getUsuario().getId(), fisica.getPessoa().getId(), new ChamadaPaginaBean().getRotinaRetorno().getId());
                    if (sa != null) {
                        if (sa.getDtAutorizacao() == null) {
                            count++;
                            GenericaMensagem.warn("Mensagem: (" + count + ")", "SOLICITAÇÃO AGUARDANDO AUTORIZAÇÃO!");
                            permite = false;
                        } else {
                            if (sa.getAutorizado()) {
                                if (sa.getDtConcluido() == null) {
                                    GenericaSessao.put("sessaoSisAutorizacao", sa);
                                    ignoreCase = true;
                                } else {
                                    count++;
                                    GenericaMensagem.warn("Mensagem: (" + count + ")", "ESTÁ SOLICITAÇÃO JÁ FOI CONCLUÍDA E PROCESSADA PELO USUÁRIO NO DIA " + sa.getConcluidoString() + "!");
                                    permite = false;
                                }
                            } else {
                                count++;
                                GenericaMensagem.warn("Mensagem: (" + count + ")", " O GESTOR " + sa.getGestor().getPessoa().getNome() + " RECUSOU SEU PEDIDO. MOTIVO: " + sa.getMotivoRecusa());
                                permite = false;

                            }

                        }
                    }
                }
                if (!ignoreCase) {
                    FunctionsDao functionsDao = new FunctionsDao();
                    if (functionsDao.inadimplente(p.getId())) {
                        count++;
                        permite = false;
                        GenericaMensagem.warn("Mensagem: (" + count + ")", "EXISTE(m) DÉBITO(s)!");
                        if (validacao.equals("emissaoGuias")) {
                            solicitarAutorizacao = "debitos";
                            loadListServicosAutorizados();
                        }
                    }
                }
                break;
        }
        // EMAIL OBRIGATÓRIO
        switch (validacao) {
            case "matriculaAcademia":
            case "matriculaEscola":
                String tipoFisica = "";
                if (GenericaSessao.exists("pesquisaFisicaTipo")) {
                    tipoFisica = GenericaSessao.getString("pesquisaFisicaTipo");
                }
                if (tipoFisica.equals("aluno")) {
                    if (fisica.getPessoa().getEmail1().isEmpty()) {
                        if (configuracaoSocial.getObrigatorioEmail()) {
                            count++;
                            GenericaMensagem.warn("Validação", "E-MAIL OBRIGATÓRIO, PARA CADASTRAR ALUNO!");
                            permite = false;
                        }
                    }
                }
                break;
        }

        // BLOQUEIO
        switch (validacao) {
            case "vendasCaravana":
            case "matriculaAcademia":
            case "matriculaEscola":
            case "emissaoGuias":
            case "lancamentoIndividual":
            case "geracaoDebitosCartao":
            case "locacaoFilme":
            case "associarFisica":
            case "agendamentos":
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
                        GenericaMensagem.fatal("Mensagem: (" + count + ")", "NECESSÁRIO SER SÓCIO!");
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
        Double d = new Double(0);
        if (valor != null) {
            try {
                d = new Double(valor.toString());
            } catch (Exception e) {

            }
        }
        return Moeda.converteR$Double(d);
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
            if (inativoDesde.isEmpty()) {
                inativoDesde = socios.getServicoPessoa().getInativacao();
            }
            if (!inativoDesde.isEmpty() && !socios.getServicoPessoa().getMotivoInativacao().isEmpty()) {
                inativoDesde += " - MOTIVO: " + socios.getServicoPessoa().getMotivoInativacao().toUpperCase();
            }

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
            PessoaDao pessoaDao = new PessoaDao();
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

    public void editPessoaEmpresa(PessoaEmpresa pe) {
        pessoaEmpresaEdit = pe;
    }

    public void updatePessoaEmpresa() {
        if (new Dao().update(pessoaEmpresaEdit, true)) {
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
            pessoaEmpresaEdit = new PessoaEmpresa();
        } else {
            GenericaMensagem.warn("Erro", "AO ATUALIZAR DADOS DA EMPRESA");
        }
    }

    public void closePessoaEmpresa() {
        pessoaEmpresaEdit = new PessoaEmpresa();
    }

    public void deletePessoaEmpresa() {
        List<Agendamento> listAgendamento = new HomologacaoDao().pesquisaPorPessoaEmpresa(pessoaEmpresaEdit.getId());
        Dao dao = new Dao();
        dao.openTransaction();
        String agendamento_id = "";
        for (int i = 0; i < listAgendamento.size(); i++) {
            Senha senha = listAgendamento.get(i).getSenha();
            if (senha != null && senha.getId() != -1) {
                if (!dao.delete(listAgendamento.get(i).getSenha())) {
                    GenericaMensagem.warn("Erro", "AO EXCLUIR SENHA DE HOMOLOGAÇÃO!");
                    dao.rollback();
                    return;
                }
            }
            Cancelamento cancelamento = new CancelamentoDao().findByAgendamento(listAgendamento.get(i).getId());
            if (cancelamento != null && cancelamento.getId() != -1) {
                if (!dao.delete(cancelamento)) {
                    GenericaMensagem.warn("Erro", "AO AGENDAMENTO CANCELADO!");
                    dao.rollback();
                    return;
                }
            }
            if (listAgendamento.get(i).getRecepcao() != null) {
                Recepcao recepcao = listAgendamento.get(i).getRecepcao();
                listAgendamento.get(i).setRecepcao(null);
                if (!dao.update(listAgendamento.get(i))) {
                    GenericaMensagem.warn("Erro", "AO EXCLUIR AGENDAMENTO - UPDATE RECEPÇÃO!");
                    dao.rollback();
                    return;
                }
                if (!dao.delete(recepcao)) {
                    GenericaMensagem.warn("Erro", "AO EXCLUIR RECEPÇÃO!");
                    dao.rollback();
                    return;
                }
            }
            if (!dao.delete(listAgendamento.get(i))) {
                GenericaMensagem.warn("Erro", "AO EXCLUIR AGENDAMENTO!");
                dao.rollback();
                return;
            }
            if (i == 0) {
                agendamento_id = "" + listAgendamento.get(i).getId();
            } else {
                agendamento_id = "," + listAgendamento.get(i).getId();
            }
        }
        if (!dao.delete(pessoaEmpresaEdit)) {
            GenericaMensagem.warn("Erro", "AO EXCLUIR PESSOA EMPRESA!");
            dao.rollback();
            return;
        }
        dao.commit();
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("pes_pessoa_empresa");
        novoLog.setCodigo(pessoaEmpresaEdit.getId());
        novoLog.delete(
                " ID: " + pessoaEmpresaEdit.getId()
                + " - Agendamento (IDS: " + agendamento_id + ") "
                + " - Funcionário (" + pessoaEmpresaEdit.getFisica().getPessoa().getId() + ") - Nome: " + pessoaEmpresaEdit.getFisica().getPessoa().getNome()
                + " - Empresa: (" + pessoaEmpresaEdit.getJuridica().getPessoa().getId() + " ) Nome: " + pessoaEmpresaEdit.getJuridica().getPessoa().getNome()
                + " - Admissão:  " + pessoaEmpresaEdit.getAdmissao()
                + " - Demissão:  " + pessoaEmpresaEdit.getDemissao());
        GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        loadListPessoaEmpresa();
        pessoaEmpresaEdit = new PessoaEmpresa();
    }

    public PessoaEmpresa getPessoaEmpresaEdit() {
        if (GenericaSessao.exists("juridicaPesquisa") && GenericaSessao.exists("tipoPessoaJuridica")) {
            JuridicaDao db = new JuridicaDao();
            Juridica j = (Juridica) GenericaSessao.getObject("juridicaPesquisa");
            List listax = db.listaJuridicaContribuinte(j.getId());
            if (!listax.isEmpty()) {
                for (int i = 0; i < listax.size(); i++) {
                    if (((List) listax.get(0)).get(11) != null) {
                        // CONTRIBUINTE INATIVO
                        mensagemAviso = "Empresa Inativa não pode ser vinculada!";
                        visibleMsgAviso = true;
                    } else {
                        pessoaEmpresaEdit.setJuridica(j);
                    }
                }
            } else {
                pessoaEmpresaEdit.setJuridica(j);
            }
            GenericaSessao.remove("juridicaPesquisa");
            GenericaSessao.remove("tipoPessoaJuridica");
        }
        return pessoaEmpresaEdit;
    }

    public void editPessoaEmpresaEdit(PessoaEmpresa pessoaEmpresaEdit) {
        Agendamento a = pessoaEmpresaEdit.getAgendamento();
        if (a != null) {
//            if (a.getStatus().getId() == 2 || a.getStatus().getId() == 4 || a.getStatus().getId() == 5 || a.getStatus().getId() == 6) {
//                GenericaMensagem.warn("Validação", "NÃO É POSSÍVEL ALTERAR EMPRESA AGENDADA, EM ATENDIMENTO OU HOMOLOGADA! ");
//                return;
//            }
        }
        this.pessoaEmpresaEdit = pessoaEmpresaEdit;
        // this.nrRegistro = this.pessoaEmpresaEdit.getNrRegistro();
        PF.openDialog("dlg_pessoa_empresa");
        PF.update("form_pessoa_fisica:i_painel_pe_edit");
    }

    public void setPessoaEmpresaEdit(PessoaEmpresa pessoaEmpresaEdit) {
        this.pessoaEmpresaEdit = pessoaEmpresaEdit;
    }

    public List<Fisica> getListFisicaSugestao() {
        return listFisicaSugestao;
    }

    public void setListFisicaSugestao(List<Fisica> listFisicaSugestao) {
        this.listFisicaSugestao = listFisicaSugestao;
    }

    public Boolean getCadastrar() {
        if (GenericaSessao.exists("cadastrar")) {
            cadastrar = true;
        }
        return cadastrar;
    }

    public void setCadastrar(Boolean cadastrar) {
        this.cadastrar = cadastrar;
    }

    public Boolean getEditar() {
        if (GenericaSessao.exists("editar")) {
            editar = true;
        }
        return editar;
    }

    public void setEditar(Boolean editar) {
        this.editar = editar;
    }

    // public void
    public SisAutorizacoes getSisAutorizacoes() {
        return sisAutorizacoes;
    }

    public void setSisAutorizacoes(SisAutorizacoes sisAutorizacoes) {
        this.sisAutorizacoes = sisAutorizacoes;
    }

    public void openRequest(String alterType) {
        this.alterType = alterType;
        sisAutorizacoes = new SisAutorizacoes();
        if (solicitarAutorizacao.equals("debitos")) {
            sisAutorizacoes.setMotivoSolicitacao("Liberar cadastro com débitos. Motivo: ");
        }
    }

    public void sendRequest() {
        SisAutorizacoesDao sad = new SisAutorizacoesDao();
        //List<SisAutorizacoes> listAutorizacoesSecundarias = new ArrayList();
        Dao dao = new Dao();
        Fisica f = (Fisica) dao.find(fisica);

        sisAutorizacoes.setRotina(new Rotina().get());
        sisAutorizacoes.setOperador(Usuario.getUsuario());
        sisAutorizacoes.setPessoa(fisica.getPessoa());

        if (sisAutorizacoes.getMotivoSolicitacao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar o motivo da solicitação!");
            return;
        }
        if (!alterType.equals("debitos")) {
            if (sisAutorizacoes.getDadosAlterados().isEmpty()) {
                GenericaMensagem.warn("Validação", "O campo de alteração não pode ser vazio!");
                return;
            }
            if (sisAutorizacoes.getDadosAlterados().length() < 5) {
                GenericaMensagem.warn("Validação", "Informar um motivo válido!");
                return;
            }
        }
        Integer tcase = 1;
        switch (alterType) {
            case "cpf":
                sisAutorizacoes.setSisAutorizacoesTipo((SisAutorizacoesTipo) dao.find(new SisAutorizacoesTipo(), 1));
                if (sisAutorizacoes.getDadosAlterados().equals("___.___.___-__")) {
                    GenericaMensagem.warn("Validação", "O campo de alteração não pode ser vazio!");
                    sisAutorizacoes.setDadosAlterados("");
                    return;
                }
                sisAutorizacoes.setEvento((Evento) dao.find(new Evento(), 3));
                //SisAutorizacoes s2 = new SisAutorizacoes();
                if (!new FisicaDao().pesquisaFisicaPorDoc(sisAutorizacoes.getDadosAlterados()).isEmpty()) {
                    GenericaMensagem.warn("Validação", "Pessoa já existente no Sistema!");
                    return;
                }
                if (!sisAutorizacoes.getDadosAlterados().isEmpty() || sisAutorizacoes.getDadosAlterados().equals("0")) {
                    if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(sisAutorizacoes.getDadosAlterados()))) {
                        GenericaMensagem.warn("Validação", "Documento (CPF) inválido! " + fisica.getPessoa().getDocumento());
                        mensagem = "Documento (CPF) inválido! " + fisica.getPessoa().getDocumento();
                        return;
                    }
                }
                sisAutorizacoes.setTabela("pes_pessoa");
                sisAutorizacoes.setColuna("ds_documento");
                sisAutorizacoes.setCodigo(fisica.getPessoa().getId());
                sisAutorizacoes.setDadosOriginais(fisica.getPessoa().getDocumento());
                sisAutorizacoes.setMotivoSolicitacao("Alteração do documento: " + sisAutorizacoes.getMotivoSolicitacao());
                break;
            case "nome":
                sisAutorizacoes.setSisAutorizacoesTipo((SisAutorizacoesTipo) dao.find(new SisAutorizacoesTipo(), 1));
                if (fisica.getPessoa().getNome().equals(sisAutorizacoes.getDadosAlterados().toUpperCase())) {
                    GenericaMensagem.warn("Validação", "PARA ALTERAR DEVE SE UTILIZAR OUTRO NOME!");
                    return;
                }
                sisAutorizacoes.setTabela("pes_pessoa");
                sisAutorizacoes.setColuna("ds_nome");
                sisAutorizacoes.setCodigo(fisica.getPessoa().getId());
                sisAutorizacoes.setDadosOriginais(fisica.getPessoa().getNome());
                sisAutorizacoes.setDadosAlterados(sisAutorizacoes.getDadosAlterados().toUpperCase());
                sisAutorizacoes.setMotivoSolicitacao("Alteração do nome: " + sisAutorizacoes.getMotivoSolicitacao());
                break;
            // COMENTAR SE NÃO FOR USAR NA EMISSÃO DE GUIAS
            case "debitos":
                tcase = 2;
                sisAutorizacoes.setSisAutorizacoesTipo((SisAutorizacoesTipo) dao.find(new SisAutorizacoesTipo(), 2));
                sisAutorizacoes.setRotinaDestino(new ChamadaPaginaBean().getRotinaRetorno());
                if (listServicosAutorizados.isEmpty()) {
                    sisAutorizacoes.setMotivoSolicitacao("CADASTRAR SERVIÇO ROTINA: : " + sisAutorizacoes.getMotivoSolicitacao());
                    GenericaMensagem.warn("SISTEMA", "CADASTRAR SERVIÇO ROTINA: LIBERA EMISSÃO DE SERVIÇO COM DÉBITO (CADASTRO COM DÉBITO)");
                    return;
                }
                sisAutorizacoes.setServicos((Servicos) dao.find(new Servicos(), idServicosAutorizados));
                break;
            default:
                break;
        }
        if (sad.exists(tcase, sisAutorizacoes)) {
            GenericaMensagem.warn("Validação", "SOLICITAÇÃO JÁ REALIZADA EM ANDAMENTO!");
            return;
        }
        dao.openTransaction();
        Boolean isGestor = false;
        if (!new ControleAcessoBean().verificarPermissao("autorizar", 3)) {
            if (!sad.execute(dao, sisAutorizacoes)) {
                dao.rollback();
                Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
                return;
            }
            isGestor = true;
            sisAutorizacoes.setGestor(Usuario.getUsuario());
            sisAutorizacoes.setDtAutorizacao(DataHoje.dataHoje());
            sisAutorizacoes.setHoraAutorizacao(DataHoje.horaMinuto());
            sisAutorizacoes.setAutorizado(true);
//            for (int i = 0; i < listAutorizacoesSecundarias.size(); i++) {
//                listAutorizacoesSecundarias.get(i).setGestor(sisAutorizacoes.getGestor());
//                listAutorizacoesSecundarias.get(i).setDtAutorizacao(sisAutorizacoes.getDtAutorizacao());
//                listAutorizacoesSecundarias.get(i).setHoraAutorizacao(sisAutorizacoes.getHoraAutorizacao());
//                listAutorizacoesSecundarias.get(i).setAutorizado(sisAutorizacoes.getAutorizado());
//            }
        }
        if (!dao.save(sisAutorizacoes)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao enviar a solicitação!");
            return;
        }
//        for (int i = 0; i < listAutorizacoesSecundarias.size(); i++) {
//            listAutorizacoesSecundarias.get(i).setAutorizacoes(sisAutorizacoes);
//            if (!dao.save(listAutorizacoesSecundarias.get(i))) {
//                dao.rollback();
//                GenericaMensagem.warn("Erro", "Ao enviar a solicitação secundária!");
//                return;
//            }
//        }
        dao.commit();
        sisAutorizacoes = new SisAutorizacoes();
        if (isGestor) {
            String antes = " ID: " + f.getId()
                    + " - Nome: " + f.getPessoa().getNome()
                    + " - Documento: " + f.getPessoa().getDocumento();
            fisica.getPessoa().setDtAtualizacao(new Date());
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("pes_pessoa");
            novoLog.setCodigo(fisica.getPessoa().getId());
            novoLog.update(antes, " ID: " + fisica.getId()
                    + " - Nome: " + fisica.getPessoa().getNome()
                    + " - Documento: " + fisica.getPessoa().getDocumento());
            fisica = (Fisica) new Dao().rebind(fisica);
            fisica.setPessoa((Pessoa) dao.rebind(fisica.getPessoa()));
            try {
                PF.update("form_pessoa_fisica");
            } catch (Exception e) {

            }
            solicitarAutorizacao = "";
            if (alterType.equals("debitos")) {
                GenericaMensagem.info("Sucesso", "SOLICITAÇÃO AUTORIZADA");
            } else {
                GenericaMensagem.info("Sucesso", "DADOS ALTERADOS COM SUCESSO");
            }
        } else {
            GenericaMensagem.info("Sucesso", "SOLICITAÇÃO ENVIADA");
            String ws = "client:" + ControleUsuarioBean.getCliente().toLowerCase() + ",sisautorizacoes";
            WSSocket.send(ws, "1");
        }
    }

    public void removeRequest(SisAutorizacoes sa) {
        if (!new Dao().delete(sa, true)) {
            GenericaMensagem.warn("Erro", "Ao remover a solicitação!");
            return;
        }
        GenericaMensagem.info("Sucesso", "SOLICITAÇÃO REMOVIDA");
        loadListSisAutorizacoes();
    }

    public String getAlterType() {
        return alterType;
    }

    public void setAlterType(String alterType) {
        this.alterType = alterType;
    }

    public List<SisAutorizacoes> getListSisAutorizacoes() {
        return listSisAutorizacoes;
    }

    public void setListSisAutorizacoes(List<SisAutorizacoes> listSisAutorizacoes) {
        this.listSisAutorizacoes = listSisAutorizacoes;
    }

    public void loadListSisAutorizacoes() {
        listSisAutorizacoes = new ArrayList();
        listSisAutorizacoes = new SisAutorizacoesDao().findByPessoa(fisica.getPessoa().getId());
    }

    public String getMascaraAlteracao() {
        if (alterType != null && !alterType.isEmpty()) {
            return Mask.getMascaraPesquisa(alterType, true);
        }
        return "";
    }

    public Boolean getAlteraCpf() {
        if (fisica.getId() == -1) {
            return true;
        } else {
            Fisica f = (Fisica) new Dao().find(new Fisica(), fisica.getId());
            if (f.getPessoa().getDocumento().isEmpty() || f.getPessoa().getDocumento().equals("0")) {
                return true;
            }
            return false;
        }
    }

    public ConfiguracaoSocial getConfiguracaoSocial() {
        return configuracaoSocial;
    }

    public void setConfiguracaoSocial(ConfiguracaoSocial configuracaoSocial) {
        this.configuracaoSocial = configuracaoSocial;
    }

    public List<String> findSuggestions(String name) {
        if (name.isEmpty()) {
            listSugestion = new ArrayList();
            return new ArrayList();
        }
        List<Fisica> list = new ArrayList();
        name = name.trim();
        FisicaDao db = new FisicaDao();
        String como = "P";
        if (name.length() <= 3) {
            como = "I";
        }
        String in = fisica.getPessoa().getId() + "";
        db.setNot_in(in);
        db.setLimit(100);
        db.setIgnore(true);
        list = db.pesquisaPessoa(name, "nome", como);
        if (list.isEmpty()) {
            if (ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(name))) {
                db.setLimit(1);
                db.setIgnore(true);
                list = db.pesquisaPessoa(como, "cpf", "");
            } else if (DataHoje.isDataValida(como)) {
                db.setIgnore(true);
                db.setLimit(100);
                list = db.pesquisaPessoa(como, "nascimento", "");
            }
            if (list.isEmpty()) {
                db.setIgnore(true);
                db.setLimit(2);
                list = db.pesquisaPessoa(como, "rg", "");
            }
        }
        listSugestion = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            listSugestion.add(list.get(i).getPessoa().getNome());
        }
        return listSugestion;
    }

    public void selectSuggestions(String tcase) {
        if (tcase.equals("pai")) {
            fisica.setPai(selectedSugestion);
        } else if (tcase.equals("mae")) {
            fisica.setMae(selectedSugestion);
        }
    }

    public List<String> getListSugestion() {
        return listSugestion;
    }

    public void setListSugestion(List<String> listSugestion) {
        this.listSugestion = listSugestion;
    }

    public String getSelectedSugestion() {
        return selectedSugestion;
    }

    public void setSelectedSugestion(String selectedSugestion) {
        this.selectedSugestion = selectedSugestion;
    }

    public Boolean getLiberaRotinaRetorno() {
        if (GenericaSessao.exists("urlRetorno")) {
            if (GenericaSessao.getString("urlRetorno").equals("emissaoGuias")) {
                return true;
            }
        }
        return false;
    }

    public String getSolicitarAutorizacao() {
        return solicitarAutorizacao;
    }

    public void setSolicitarAutorizacao(String solicitarAutorizacao) {
        this.solicitarAutorizacao = solicitarAutorizacao;
    }

    public List<SelectItem> getListServicosAutorizados() {
        return listServicosAutorizados;
    }

    public void setListServicosAutorizados(List<SelectItem> listServicosAutorizados) {
        this.listServicosAutorizados = listServicosAutorizados;
    }

    public Integer getIdServicosAutorizados() {
        return idServicosAutorizados;
    }

    public void setIdServicosAutorizados(Integer idServicosAutorizados) {
        this.idServicosAutorizados = idServicosAutorizados;
    }

    public String pessoaJuridicaMei() throws IOException {
        if (fisica.getId() != -1) {
            if (fisica.getPessoa().getDocumento().isEmpty() || fisica.getPessoa().getDocumento().equals("0")) {
                GenericaMensagem.warn("Validação", "Informar CPF!");
                return null;
            }
            if (listaPessoaEndereco.isEmpty()) {
                GenericaMensagem.warn("Validação", "Cadastrar endereço!");
                return null;
            }
        }
        List<PessoaEmpresa> listPessoaEmpresa = new PessoaEmpresaDao().findAllByFisica(fisica.getId());
        Juridica j = new Juridica();
        JuridicaBean juridicaBean = new JuridicaBean();
        for (int i = 0; i < listPessoaEmpresa.size(); i++) {
            if (listPessoaEmpresa.get(i).isSocio()) {
                GenericaSessao.remove("juridicaBean");
                JuridicaBean jb = new JuridicaBean();
                jb.init();
                GenericaSessao.put("juridicaBean", jb);
                return ((JuridicaBean) GenericaSessao.getObject("juridicaBean")).editar(listPessoaEmpresa.get(i).getJuridica(), true);
            }
        }
        GenericaSessao.remove("juridicaBean");
        GenericaSessao.put("newMei", true);
        GenericaSessao.put("newFisicaMei", fisica);
        ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).setDisabled(true);
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pagina("pessoaJuridica");
    }

    public boolean isFisicaMei() {
        if (fisica.getId() != -1) {
            return new PessoaEmpresaDao().findSocioProprietario(fisica.getId()) != null;
        }
        return false;
    }

    public void refreshSisAutorizacao(Boolean b) {
        try {
            if (b) {
                Fisica f = (Fisica) new Dao().rebind(fisica);
                fisica.getPessoa().setNome(f.getPessoa().getNome());
                fisica.getPessoa().setDocumento(f.getPessoa().getDocumento());
                fisica.getPessoa().setTipoDocumento(f.getPessoa().getTipoDocumento());
                Messages.info("Sucesso", "Correção cadastral autorizada");
            } else {
                Messages.warn("Recusada", "Correção cadastral recusada! Consulte detalhes em sua requisição.");
            }
            PF.update("form_pessoa_fisica");
        } catch (Exception e) {

        }

    }

    public ConfiguracaoArrecadacao getConfiguracaoArrecadacao() {
        return configuracaoArrecadacao;
    }

    public void setConfiguracaoArrecadacao(ConfiguracaoArrecadacao configuracaoArrecadacao) {
        this.configuracaoArrecadacao = configuracaoArrecadacao;
    }

    public List<SelectItem> getListNrRegistro() {
        return listNrRegistro;
    }

    public void setListNrRegistro(List<SelectItem> listNrRegistro) {
        this.listNrRegistro = listNrRegistro;
    }

    public Integer getNrRegistro() {
        return nrRegistro;
    }

    public void setNrRegistro(Integer nrRegistro) {
        this.nrRegistro = nrRegistro;
    }

    public void loadListNrRegistro() {
        listNrRegistro = new ArrayList();
        for (int i = 1; i < 6; i++) {
            listNrRegistro.add(new SelectItem(i, (i + "")));
        }
    }

    private void loadPessoaEmpresa() {
        PessoaEmpresaDao db = new PessoaEmpresaDao();
        PessoaProfissaoDao dbp = new PessoaProfissaoDao();
        pessoaEmpresa = (PessoaEmpresa) db.pesquisaPessoaEmpresaPorFisica(fisica.getId());
        if (pessoaEmpresa.getId() != -1) {
            pessoaEmpresa = (PessoaEmpresa) new Dao().rebind(pessoaEmpresa);
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
    }

}
