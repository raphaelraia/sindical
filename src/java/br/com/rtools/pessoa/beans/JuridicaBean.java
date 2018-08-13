package br.com.rtools.pessoa.beans;

import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.pessoa.dao.CnaeDao;
import br.com.rtools.arrecadacao.dao.ContribuintesInativosDao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.pessoa.dao.EnvioEmailsDao;
import br.com.rtools.pessoa.dao.TipoEnderecoDao;
import br.com.rtools.pessoa.dao.TipoDocumentoDao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.arrecadacao.*;
import br.com.rtools.arrecadacao.beans.OposicaoBean;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.lista.ListaSociosEmpresa;
import br.com.rtools.cobranca.TmktHistorico;
import br.com.rtools.cobranca.dao.TmktHistoricoDao;
import br.com.rtools.digitalizacao.Documento;
import br.com.rtools.digitalizacao.dao.DigitalizacaoDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.MalaDiretaDao;
import br.com.rtools.pessoa.dao.PessoaComplementoDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Evento;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.WebService;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoCnpj;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.sistema.SisAutorizacoesTipo;
import br.com.rtools.sistema.dao.SisAutorizacoesDao;
import br.com.rtools.utilitarios.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class JuridicaBean implements Serializable {

    private Integer idStatusCobranca;
    private List<SelectItem> listStatusCobranca;
    private Juridica juridica = new Juridica();
    private PessoaEndereco pessoaEndereco = new PessoaEndereco();
    private Filial filial = new Filial();
    private Filial filialSwap = new Filial();
    private ContribuintesInativos contribuintesInativos = new ContribuintesInativos();
    private Endereco endereco = new Endereco();
    private GrupoCidade gruCids = new GrupoCidade();
    private Convencao convencao = new Convencao();
    private CnaeConvencao cnaeConvencao = new CnaeConvencao();
    private EnvioEmails envioEmails = new EnvioEmails();
    private int indicaTab = 0;
    private String enderecoCompleto;
    private String enderecoDeletado = null;
    private String descPesquisa = "";
    private String porPesquisa = "nome";
    private String comoPesquisa = "I";
    private String descPesquisaCnae = "";
    private String porPesquisaCnae = "cnae";
    private String comoPesquisaCnae = "";
    private String filialMatriz = "m";
    private String msgConfirma;
    private String msgDocumento = "";
    private String maskCnae = "cnae";
    private String mask;
    private String log;
    private String desc;
    private String cid;
    private String uf;
    private String strGrupoCidade = "";
    private String strCnaeConvencao = "";
    private String cnaeContribuinte = " sem cnae! ";
    private String enderecoCobranca = "";
    private String strSimpleEndereco = "";
    private boolean renNovoEndereco = false;
    private boolean renEndereco = false;
    private String renChkEndereco = "false";
    private String colorContri = "red";
    private String numDocumento = "";
    private String strArquivo = "";
    private String nomeContabilidade = "";
    private String nomePesquisaContabilidade = "";
    private int idTipoDocumento = 1;
    private int idPorte = 0;
    private int idMotivoInativacao = 0;
    private int idIndex = -1;
    private int idIndexCnae = -1;
    private int idIndexEndereco = -1;
    private int idIndexInativacao = -1;
    private int idIndexContabilidade = -1;
    private int idIndexPertencente = -1;
    private boolean marcar;
    private boolean alterarEnd = false;
    private boolean endComercial = false;
    private boolean habServContabil = true;
    private boolean carregaEnvios = false;
    private boolean renderAtivoInativo = false;
    private List<Oposicao> listaOposicao = new ArrayList();
    private List<ListaSociosEmpresa> listSocios = new ArrayList<>();
    private boolean somenteAtivas = false;
    private boolean somenteContabilidades = false;
    private List listaEnd = new ArrayList();
    private List listEn = new ArrayList();
    private List<Cnae> listaCnae = new ArrayList();
    private List<Juridica> listaJuridica = new ArrayList();
    private List<Juridica> listaContabilidade = new ArrayList();
    private List<DataObject> listaEmpresasPertencentes = new ArrayList();
    private final List<SelectItem> listaTipoDocumento = new ArrayList();
    private List<SelectItem> listaPorte = new ArrayList();
    private List<ContribuintesInativos> listaContribuintesInativos = new ArrayList();
    private final List<SelectItem> listaMotivoInativacao = new ArrayList();
    private String atualiza = "";
    private String tipoFiltro = "todas";
    private JuridicaReceita juridicaReceita = new JuridicaReceita();
    // [0] Contribuintes
    // [1] Contabilidades
    // [2] Todas
    private Boolean[] disabled;
    private ConfiguracaoCnpj configuracaoCnpj;
    private ConfiguracaoArrecadacao configuracaoArrecadacao;

    // MALA DIRETA
    private Boolean habilitaMalaDireta = false;
    private String idMalaDiretaGrupo = null;
    private List<MalaDireta> listMalaDireta = new ArrayList();
    private List<SelectItem> listMalaDiretaGrupo = new ArrayList();

    // TAB DOCUMENTOS
    private List<Documento> listaDocumentos = new ArrayList();
    private List<LinhaArquivo> listaArquivos = new ArrayList();
    private Integer offset = 0;
    private Integer count = 0;
    private Integer limit = 500;

    private Date dtRecadastro = DataHoje.dataHoje();
    private PessoaComplemento pessoaComplemento = new PessoaComplemento();

    private PesquisaCNPJ pesquisaCNPJ = new PesquisaCNPJ();
    private List<SelectItem> listaCnaeReceita = new ArrayList();
    private Integer idCnaeReceita = 1;
    private SisAutorizacoes sisAutorizacoes;
    private String alterType;
    private List<SisAutorizacoes> listSisAutorizacoes;
    private Fisica fisicaMei;
    private Boolean newMei;
    private Pessoa responsavel;
    private Integer diaVencimento;
    private List<SelectItem> listDataVencimento;
    private Registro registro;

    @PostConstruct
    public void init() {
        configuracaoArrecadacao = ConfiguracaoArrecadacao.get();
        idStatusCobranca = null;
        listStatusCobranca = new ArrayList();
        listDataVencimento = new ArrayList();
        responsavel = new Pessoa();
        disabled = new Boolean[3];
        disabled[0] = false;
        disabled[1] = false;
        disabled[2] = false;
        newMei = false;
        configuracaoCnpj = (ConfiguracaoCnpj) new Dao().find(new ConfiguracaoCnpj(), 1);
        sisAutorizacoes = new SisAutorizacoes();
        listSisAutorizacoes = new ArrayList();
        alterType = "";
        fisicaMei = new Fisica();
        if (GenericaSessao.exists("newMei", true)) {
            fisicaMei = (Fisica) new Dao().find((Fisica) GenericaSessao.getObject("newFisicaMei", true));
            if (fisicaMei != null) {
                newMei = true;
                juridica.getPessoa().setNome(fisicaMei.getPessoa().getNome() + " " + fisicaMei.getPessoa().getDocumento());
                idPorte = 3;
                juridica.getPessoa().setTelefone1(fisicaMei.getPessoa().getTelefone1());
                juridica.getPessoa().setTelefone2(fisicaMei.getPessoa().getTelefone2());
                juridica.getPessoa().setTelefone3(fisicaMei.getPessoa().getTelefone3());
                juridica.getPessoa().setTelefone4(fisicaMei.getPessoa().getTelefone4());
                juridica.getPessoa().setEmail1(fisicaMei.getPessoa().getEmail1());
                juridica.getPessoa().setEmail2(fisicaMei.getPessoa().getEmail2());
                juridica.getPessoa().setEmail3(fisicaMei.getPessoa().getEmail3());
                juridica.getPessoa().setSite(fisicaMei.getPessoa().getSite());
                juridica.getPessoa().setSite(fisicaMei.getPessoa().getSite());

                if (fisicaMei.getPessoa().getPessoaEndereco() != null) {
                    abreEndereco();
                    GenericaSessao.put("enderecoPesquisa", fisicaMei.getPessoa().getPessoaEndereco().getEndereco());
                    pessoaEndereco = fisicaMei.getPessoa().getPessoaEndereco();
                }
            }
        }
        registro = registro.get();
        diaVencimento = registro.getFinDiaVencimentoCobranca();
        loadListDataVencimento();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("juridicaBean");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("todosecontribuintes");
        GenericaSessao.remove("contribuintes");
        GenericaSessao.remove("escritorios");
        GenericaSessao.remove("pessoaBean");
        GenericaSessao.remove("pessoaComplementoBean");
    }

    public void limparRecadastro() {
        juridica.getPessoa().setDtRecadastro(juridica.getPessoa().getDtCriacao());
    }

    public void loadDataRecadastro() {
        dtRecadastro = DataHoje.dataHoje();
    }

    public String hojeRecadastro() {
        juridica.getPessoa().setDtRecadastro(DataHoje.dataHoje());
        return null;
    }

    public void updateDataRecadastro() {
        juridica.getPessoa().setDtRecadastro(dtRecadastro);
        if (juridica.getId() != -1) {
            Juridica j = (Juridica) new Dao().find(new Juridica(), juridica.getId());
            String antes = " De: ID - " + juridica.getId()
                    + " - Nome: " + j.getPessoa().getNome()
                    + " - Documento: " + j.getPessoa().getDocumento()
                    + " - Recadastro : " + juridica.getPessoa().getRecadastroString();
            PessoaDao pessoaDao = new PessoaDao();

            Date date = juridica.getPessoa().getDtAtualizacao();
            juridica.getPessoa().setDtAtualizacao(new Date());
            new Dao().rebind(juridica);
            if (pessoaDao.updateRecadastro(juridica.getPessoa())) {
                NovoLog novoLog = new NovoLog();
                novoLog.setTabela("pes_juridica");
                novoLog.setCodigo(juridica.getId());
                novoLog.update(antes,
                        " Recadastro > Nome: " + juridica.getPessoa().getNome()
                        + " - Documento: " + juridica.getPessoa().getDocumento()
                        + " - Recadastro : " + juridica.getPessoa().getRecadastroString());
                if (pessoaDao.updateAtualizacao(juridica.getPessoa())) {
                    new Dao().rebind(juridica.getPessoa());
                    GenericaMensagem.info("Sucesso", "Registro atualizado!");
                    return;
                }
                GenericaMensagem.info("Sucesso", "Registro atualizado!");
                return;
            }
            juridica.getPessoa().setDtAtualizacao(date);
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void loadListaDocumentos() {
        listaDocumentos.clear();

        DigitalizacaoDao dao = new DigitalizacaoDao();

        if (juridica.getId() != -1) {
            listaDocumentos = dao.listaDocumento(juridica.getPessoa().getId());
        }
    }

    public void verDocumentos(Documento linha) {
        listaArquivos.clear();

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String path = servletContext.getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/documentos/" + linha.getPessoa().getId() + "/" + linha.getId() + "/";
        File file = new File(path);

        File lista_datas[] = file.listFiles();

        if (lista_datas != null) {
            for (File lista_data : lista_datas) {
                String ext = FilenameUtils.getExtension(lista_data.getPath()).toUpperCase();
                String mimeType = servletContext.getMimeType(lista_data.getPath());
                listaArquivos.add(new LinhaArquivo("fileExtension" + ext + ".png", lista_data.getName(), mimeType, linha));
            }
        }
    }

    public void alterarCnaeSecundario() {
        retornaCnaeReceita((Cnae) new Dao().find(new Cnae(), Integer.valueOf(listaCnaeReceita.get(idCnaeReceita).getDescription())));
    }

    public void acaoPesquisarCNPJ() {
        WebService webService = new WebService();
        try {
            pesquisaCNPJ.setCaptcha("");
            HashMap hash;
            if (true) {
                hash = pesquisaCNPJ.pesquisar();
            } else {
                hash = pesquisaCNPJ.pesquisar();
            }
            if ((Boolean) hash.get("status")) {
                PF.openDialog("dlg_confirma_pesquisa_cpnj");
                PF.update(":formPessoaJuridica:dlg_confirma_pesquisa_cpnj");
            } else {
                GenericaMensagem.error("Erro", hash.get("mensagem").toString());
            }
        } catch (Exception e) {
            GenericaMensagem.error("Erro", "Não foi possível Pesquisar CNPJ, contate o administrador!");
        }
    }

    public void pesquisaCnpjPadrao() {
        this.pesquisaCnpjXML(true);
    }

    public void pesquisaCnpjXML() {
        if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 3) {
            this.pesquisaCnpjXML(false);
        } else {
            this.pesquisaCnpjXML(true);
        }
    }

    public void pesquisaCnpjXML(Boolean confirmar) {
        if (configuracaoCnpj == null || configuracaoCnpj.getLocal()) {
            if (juridica.getId() != -1) {
                return;
            }

            if (juridica.getPessoa().getDocumento().isEmpty()) {
                GenericaMensagem.warn("Atenção", "Documento Inválido!");
                return;
            }

            String documento = AnaliseString.extrairNumeros(juridica.getPessoa().getDocumento());

            if (!validaTipoDocumento(2, documento)) {
                msgDocumento = "Documento inválido!";
                GenericaMensagem.warn("Atenção", "Documento Inválido!");
                PF.update("formPessoaJuridica");
                return;
            }

            JuridicaDao dbj = new JuridicaDao();
            List listDocumento = dbj.pesquisaJuridicaPorDoc(juridica.getPessoa().getDocumento());
            for (int i = 0; i < listDocumento.size(); i++) {
                if (!listDocumento.isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Empresa já esta cadastrada no Sistema!");
                    PF.update("formPessoaJuridica");
                    return;
                }
            }

            PessoaDao db = new PessoaDao();

            juridicaReceita = db.pesquisaJuridicaReceita(documento);
            if (juridicaReceita.getPessoa() != null && juridicaReceita.getPessoa().getId() != -1) {
                GenericaMensagem.warn("Atenção", "Pessoa já cadastrada no Sistema!");
                PF.update("formPessoaJuridica");
                return;
            }

            pesquisaCNPJ.setCnpj(documento);

            if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 3 && !confirmar && juridicaReceita.getId() == -1) {
                acaoPesquisarCNPJ();
                return;
            }

            Dao dao = new Dao();
            JuridicaReceitaJSON.JuridicaReceitaObject jro;
            if (juridicaReceita.getId() == -1) {
                // tipo = "wooki" = pago / "gratis" = gratis / "padrao" = desenvolvido pelo sistema
                // tipo = "wooki" e "gratis" (pesquisaCNPJ) = NULL, "padrao" passar o PesquisaCNPJ com o retorno do método pesquisar = true
                if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 3 && confirmar) {
                    jro = new JuridicaReceitaJSON(pesquisaCNPJ).pesquisar();
                    // ERRO AO PROCESSAR CAPTCHA
                    if (jro.getStatus() == -2) {
                        GenericaMensagem.warn("Atenção", "Erro ao Pesquisar, contate o administrador: " + jro.getMsg());
                        return;
                    }

                    if (jro.getStatus() == -3) {
                        GenericaMensagem.warn("Atenção", jro.getMsg());
                        return;
                    }
                } else if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 1) {
                    jro = new JuridicaReceitaJSON(documento, "wooki").pesquisar();
                } else if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 2) {
                    jro = new JuridicaReceitaJSON(documento, "gratis").pesquisar();
                } else if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 4) {
                    jro = new JuridicaReceitaJSON(pesquisaCNPJ, "rtools").pesquisar();
                } else if (configuracaoCnpj.getTipoPesquisaCnpj().getId() == 5) {
                    jro = new JuridicaReceitaJSON(documento, "hubdodesenvolvedor").pesquisar();
                } else {
                    jro = new JuridicaReceitaJSON(documento, "sindical").pesquisar();
                }

                // NULL É PORQUE DEU ERRO DESCONHECIDO
                if (jro == null) {
                    GenericaMensagem.warn("Atenção", "Erro ao Pesquisar, contate o administrador");
                    return;
                }

                // SE NÃO ENCONTRAR NA WOOKI
                if (jro.getStatus() == -1) {
                    // desabilitado por demostrar falhas
                    GenericaMensagem.warn("Atenção", "Erro ao Pesquisar, contate o administrador: " + jro.getMsg());
                    return;
                }

                if (jro.getStatus() == 0) {
                    juridicaReceita.setNome(jro.getNome_empresarial());
                    juridicaReceita.setFantasia(jro.getTitulo_estabelecimento());
                    juridicaReceita.setDocumento(documento);
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
                        return;
                    }

                    dao.commit();
                }
            } else {
                // recarrego o jro porque no Object JuridicaReceita não contem os campos email1, email2, email3, telefone1, telefone2, telefone3, listaCnae e Endereco 
                jro = new JuridicaReceitaJSON(juridicaReceita).load();

            }

            juridica.getPessoa().setNome(juridicaReceita.getNome().toUpperCase());
            juridica.setFantasia(juridicaReceita.getFantasia().toUpperCase());
            juridica.setDtAbertura(juridicaReceita.getDtAbertura());
            if (juridicaReceita.getDtSituacaoCadastral() != null) {
                juridica.getPessoa().setDtAtualizacao(juridicaReceita.getDtSituacaoCadastral());
            }
            if (juridicaReceita.getStatus().toUpperCase().equals("BAIXADA")) {
                juridica.setDtFechamento(juridicaReceita.getDtSituacaoCadastral());
            }

            if (jro == null) {
                return;
            }

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
                return;
            }

            if (jro.getLista_cnae_secundario().isEmpty()) {
                retornaCnaeReceita(jro.getLista_cnae().get(0));
            } else {
                retornaCnaeListReceita(jro.getLista_cnae().get(0), jro.getLista_cnae_secundario());
            }

            endereco = jro.getEndereco();

            if (endereco != null) {
                for (PessoaEndereco pe : jro.getPessoaEndereco()) {
                    pe.setPessoa(juridica.getPessoa());
                    listaEnd.add(pe);
                }
                pessoaEndereco = new PessoaEndereco();
            } else {
                String msg = "Endereço não encontrado no Sistema - CEP: " + juridicaReceita.getCep() + " DESC: " + juridicaReceita.getDescricaoEndereco() + " BAIRRO: " + juridicaReceita.getBairro();
                GenericaMensagem.warn("Atenção", msg);
            }
        }
    }

    public void accordion(TabChangeEvent event) {
        indicaTab = ((TabView) event.getComponent()).getActiveIndex();

        if (indicaTab == 6) {
            loadListaDocumentos();
        }
    }

    public void pesquisaDocumento() {
        JuridicaDao db = new JuridicaDao();
        if (!juridica.getPessoa().getDocumento().isEmpty()) {
            List<Juridica> lista = db.pesquisaJuridicaPorDoc(juridica.getPessoa().getDocumento());
            if (!lista.isEmpty()) {
                GenericaMensagem.warn("Erro", "Esse documento já existe para: " + lista.get(0).getPessoa().getNome());
            }
        }
    }

    public String getInadimplente() {
        if (juridica.getId() != -1) {
            JuridicaDao db = new JuridicaDao();
            int[] in = db.listaInadimplencia(juridica.getPessoa().getId());

            if (in[0] > 0 && in[1] > 0) {
                return "Esta empresa está inadimplente em " + in[0] + " mes(es) e com " + in[1] + " movimento(s) em atraso.";
            }
        }
        return "";
    }

    public String getContribuinte() {
        JuridicaDao db = new JuridicaDao();
        if (juridica.getId() != -1) {
            List listax = db.listaJuridicaContribuinte(juridica.getId());

            for (int i = 0; i < listax.size(); i++) {
                if (((List) listax.get(0)).get(11) != null) {
                    // CONTRIBUINTE INATIVO
                    //cnaeContribuinte = " cnae contribuinte porém empresa inativa!";
                    cnaeContribuinte = " ";
                    colorContri = "red";
                    renderAtivoInativo = false;
                    return "CONTRIBUINTE INATIVO";
                } else {
                    //cnaeContribuinte = "cnae contribuinte!";
                    cnaeContribuinte = " ";
                    colorContri = "blue";
                    renderAtivoInativo = true;
                    return "CONTRIBUINTE ATIVO";
                }
            }
        }
        if (juridica.getCnae() != null && juridica.getCnae().getId() != -1) {
            CnaeConvencaoDao dbCnaeCon = new CnaeConvencaoDao();
            if (dbCnaeCon.pesquisaCnaeComConvencao(juridica.getCnae().getId()) != null) {
                //cnaeContribuinte = " cnae contribuinte!";
                cnaeContribuinte = " ";
                colorContri = "blue";
            } else {
                cnaeContribuinte = " este cnae não está na convenção! ";
                colorContri = "red";
            }
        }
        renderAtivoInativo = false;
        return "NÃO CONTRIBUINTE";
    }

    public void inativarContribuintes() {
        JuridicaDao db = new JuridicaDao();

        Dao dao = new Dao();

        NovoLog logs = new NovoLog();

        logs.setCodigo(juridica.getId());
        logs.setTabela("arr_contribuintes_inativos");

        if (!listaMotivoInativacao.isEmpty()) {
            contribuintesInativos.setJuridica(juridica);
            contribuintesInativos.setDtAtivacao(null);
            contribuintesInativos.setMotivoInativacao(db.pesquisaCodigoMotivoInativacao(Integer.parseInt(((SelectItem) listaMotivoInativacao.get(idMotivoInativacao)).getDescription())));

            dao.openTransaction();

            if (!dao.save(contribuintesInativos)) {
                dao.rollback();
                GenericaMensagem.error("Erro", "Não foi possível salvar Inativação!");
                return;
            }

            PessoaEmpresaDao dbp = new PessoaEmpresaDao();
            List<PessoaEmpresa> result = dbp.listaPessoaEmpresaPorJuridica(juridica.getId());
            String ids_pessoa_empresa = "";
            if (!result.isEmpty()) {
                for (PessoaEmpresa pe : result) {
                    pe.setPrincipal(false);
                    pe.setDemissao(DataHoje.data());

                    if (!dao.update(pe)) {
                        dao.rollback();
                        GenericaMensagem.error("Erro", "Não foi possível demissionar sócios!");
                        return;
                    }

                    if (ids_pessoa_empresa.isEmpty()) {
                        ids_pessoa_empresa = "" + pe.getId();
                    } else {
                        ids_pessoa_empresa += ", " + pe.getId();
                    }
                }
            }

            dao.commit();

            logs.update("",
                    "** Inativação de Empresas**\n"
                    + " ID: " + juridica.getId() + "\n"
                    + " NOME: " + juridica.getPessoa().getNome() + "\n"
                    + " MOTIVO: " + contribuintesInativos.getMotivoInativacao().getDescricao() + "\n"
                    + " SOCILITANTE: " + contribuintesInativos.getSolicitante() + "\n"
                    + " OBS: " + contribuintesInativos.getObservacao() + "\n"
                    + " ATIVAÇÃO: " + contribuintesInativos.getAtivacao() + "\n"
                    + " INATIVAÇÃO: " + contribuintesInativos.getInativacao() + "\n"
                    + " PESSOA EMPRESA ID: {" + ids_pessoa_empresa + "}"
            );

            GenericaMensagem.info("Sucesso", "Contribuinte Inativado!");
            contribuintesInativos = new ContribuintesInativos();
            listaContribuintesInativos.clear();
            getListaContribuintesInativos();
            getContribuinte();
        } else {
            GenericaMensagem.error("Erro", "Não existe Motivo de Inativação!");
        }
    }

    public List<PessoaEmpresa> getListaPessoaEmpresa() {
        List<PessoaEmpresa> result = new ArrayList();
        if (juridica.getId() != -1) {
            PessoaEmpresaDao dbp = new PessoaEmpresaDao();
            result = dbp.listaPessoaEmpresaPorJuridica(juridica.getId());
        }
        return result;
    }

    public String getEnderecoCobranca() {
        PessoaEndereco ende = null;
        String strCompl;
        if (!listaEnd.isEmpty()) {
            ende = (PessoaEndereco) listaEnd.get(0);
        }

        if (ende != null) {
            if (ende.getComplemento() == null || ende.getComplemento().isEmpty()) {
                strCompl = " ";
            } else {
                strCompl = " ( " + ende.getComplemento() + " ) ";
            }
            enderecoCobranca = ende.getEndereco().getLogradouro().getDescricao() + " "
                    + ende.getEndereco().getDescricaoEndereco().getDescricao() + ", " + ende.getNumero() + " " + ende.getEndereco().getBairro().getDescricao() + ","
                    + strCompl + ende.getEndereco().getCidade().getCidade() + " - " + ende.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(ende.getEndereco().getCep());
        } else if (alterarEnd) {
            getListaEnderecos();
        } else {
            enderecoCobranca = "NENHUM";
        }
        return enderecoCobranca;
    }

    public List<ContribuintesInativos> getListaContribuintesInativos() {
        if (listaContribuintesInativos.isEmpty()) {
            ContribuintesInativosDao db = new ContribuintesInativosDao();
            listaContribuintesInativos = db.listaContribuintesInativos(juridica.getId());
            if (listaContribuintesInativos == null) {
                listaContribuintesInativos = new ArrayList();
            }
        }
        return listaContribuintesInativos;
    }

    public String btnExcluirMotivoInativacao(ContribuintesInativos linha) {
        contribuintesInativos = linha;//(ContribuintesInativos) listaContribuintesInativos.get(idIndexInativacao);
        Dao dao = new Dao();
        dao.openTransaction();
        if (!dao.delete((ContribuintesInativos) dao.find(new ContribuintesInativos(), contribuintesInativos.getId()))) {
            GenericaMensagem.error("Erro", "Não foi possível excluir Motivo de Inativação, tente novamente!");
            dao.rollback();
            return null;
        } else {
            dao.commit();
        }
        listaContribuintesInativos.clear();
        getListaContribuintesInativos();
        msgConfirma = "Motivo excluído com sucesso!";
        contribuintesInativos = new ContribuintesInativos();
        GenericaMensagem.info("Sucesso", "Motivo Excluído!");
        if (listaContribuintesInativos.isEmpty()) {
        }
        getContribuinte();
        return null;
    }

    public String reativarContribuintes() {
        retornarCnaeConvencao();
        if (cnaeConvencao == null || cnaeConvencao.getCnae().getId() == -1) {
            GenericaMensagem.warn("Erro", "Cnae atual não pertence a Categoria!");
            return null;
        }

        ContribuintesInativosDao db = new ContribuintesInativosDao();
        ContribuintesInativos cont = db.pesquisaContribuintesInativos(juridica.getId());

        NovoLog logs = new NovoLog();

        logs.setCodigo(juridica.getId());
        logs.setTabela("arr_contribuintes_inativos");

        if (cont.getId() != -1) {
            Dao dao = new Dao();
            contribuintesInativos = (ContribuintesInativos) dao.find(new ContribuintesInativos(), cont.getId());
            contribuintesInativos.setAtivacao(DataHoje.data());
            dao.openTransaction();
            if (!dao.update(contribuintesInativos)) {
                GenericaMensagem.warn("Erro", "Erro ao reativar Empresa!");
                dao.rollback();
                return null;
            } else {
                GenericaMensagem.info("Sucesso", "Contribuinte Reativado!");
                dao.commit();
            }

            logs.update("",
                    "** Reativação de Empresas**\n"
                    + " ID: " + juridica.getId() + "\n"
                    + " NOME: " + juridica.getPessoa().getNome() + "\n"
                    + " MOTIVO: " + contribuintesInativos.getMotivoInativacao().getDescricao() + "\n"
                    + " SOCILITANTE: " + contribuintesInativos.getSolicitante() + "\n"
                    + " OBS: " + contribuintesInativos.getObservacao() + "\n"
                    + " ATIVAÇÃO: " + contribuintesInativos.getAtivacao() + "\n"
                    + " INATIVAÇÃO: " + contribuintesInativos.getInativacao()
            );

            contribuintesInativos = new ContribuintesInativos();
            listaContribuintesInativos.clear();
            getListaContribuintesInativos();
            getContribuinte();
        }
        //GenericaMensagem.warn("Erro", "Salve o cadastro para efetuar a ativação");
        return null;
    }

    public void setEnderecoCobranca(String enderecoCobranca) {
        this.enderecoCobranca = enderecoCobranca;
    }

    public String getDtAtivacao() {
        String dt = "";
        return dt;
    }

    public String getDtAtivacaoInativo() {
        String dt = "";
        if (contribuintesInativos != null) {
            if (contribuintesInativos.getId() != -1) {
                dt = contribuintesInativos.getAtivacao();
            }
        } else {
            dt = "";
        }
        return dt;
    }

    public String salvar() {
        Dao dao = new Dao();
        JuridicaDao db = new JuridicaDao();
        Pessoa pessoa = getJuridica().getPessoa();
        List listDocumento;
        if (listaEnd.isEmpty() || pessoa.getId() == -1) {
            adicionarEnderecos();
        }

        juridica.setPorte((Porte) dao.find(new Porte(), Integer.parseInt(getListaPorte().get(idPorte).getDescription())));

        if (!ValidaDocumentos.isEmailValido(juridica.getPessoa().getEmail1())) {
            GenericaMensagem.error("Erro", "Email 1 inválido!");
            return null;
        }
        if (!ValidaDocumentos.isEmailValido(juridica.getPessoa().getEmail2())) {
            GenericaMensagem.error("Erro", "Email 2 inválido!");
            return null;
        }
        if (!ValidaDocumentos.isEmailValido(juridica.getPessoa().getEmail3())) {
            GenericaMensagem.error("Erro", "Email 3 inválido!");
            return null;
        }

        juridica.getPessoa().setNome(juridica.getPessoa().getNome().trim());
        pessoaComplemento.setNrDiaVencimento(diaVencimento);
        if (responsavel != null && responsavel.getId() != -1) {
            pessoaComplemento.setResponsavel(responsavel);
        }

//
//        if (juridica.getId() == -1) {
//            loadListStatusCobranca();
//        }
//        if (idStatusCobranca == null) {
//            if (configuracaoArrecadacao.getCobrancaEmail()) {
////                for (int i = 0; i < listStatusCobranca.size(); i++) {
////                    if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 3 && !listStatusCobranca.get(i).isDisabled()) {
////                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 3));
////                    }
////                    if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 4 && !listStatusCobranca.get(i).isDisabled()) {
////                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 4));
////                        break;
////                    }
////                }
//            } else {
//                for (int i = 0; i < listStatusCobranca.size(); i++) {
//                    if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 1 && !listStatusCobranca.get(i).isDisabled()) {
//                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 1));
//                    }
//                    if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 2 && !listStatusCobranca.get(i).isDisabled()) {
//                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 2));
//                        break;
//                    }
//                }
//            }
//        } else {
//            pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), idStatusCobranca));
//        }

        loadListStatusCobranca();

        dao.openTransaction();
        if (juridica.getId() == -1) {
//
//            if (configuracaoArrecadacao.getCobrancaEmail()) {
//                for (int i = 0; i < listStatusCobranca.size(); i++) {
//                    if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 2 && !listStatusCobranca.get(i).isDisabled()) {
//                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 2));
//                    }
//                }
//            } else {
//                for (int i = 0; i < listStatusCobranca.size(); i++) {
//                    if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 1 && !listStatusCobranca.get(i).isDisabled()) {
//                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 1));
//                    }
//                }
//            }

            juridica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), Integer.parseInt(((SelectItem) getListaTipoDocumento().get(idTipoDocumento)).getDescription())));
            if (juridica.getPessoa().getNome().isEmpty()) {
                GenericaMensagem.error("Erro", "O campo nome não pode ser nulo!");
                dao.rollback();
                return null;
            }

            if (Integer.parseInt(((SelectItem) getListaTipoDocumento().get(idTipoDocumento)).getDescription()) == 4) {
                pessoa.setDocumento("0");
            } else {
                listDocumento = db.pesquisaJuridicaPorDoc(juridica.getPessoa().getDocumento());
                for (int i = 0; i < listDocumento.size(); i++) {
                    if (!listDocumento.isEmpty()) {
                        GenericaMensagem.error("Erro", "Empresa já existente no Sistema!");
                        dao.rollback();
                        return null;
                    }
                }
            }

            if (!validaTipoDocumento(Integer.parseInt(getListaTipoDocumento().get(idTipoDocumento).getDescription()), juridica.getPessoa().getDocumento())) {
                GenericaMensagem.error("Erro", "Documento Invalido!");
                dao.rollback();
                return null;
            }

            if (listaEnd.isEmpty()) {
                GenericaMensagem.error("Erro", "Cadastro não pode ser salvo sem Endereço!");
                dao.rollback();
                return null;
            }

            if (juridica.getPessoa().getId() == -1) {
                if (!dao.save(pessoa)) {
                    GenericaMensagem.error("Erro", "Erro ao Salvar Dados!");
                    dao.rollback();
                    return null;
                }

                pessoaComplemento.setPessoa(pessoa);

                if (!dao.save(pessoaComplemento)) {
                    GenericaMensagem.warn("Validação", "Ao salvar pessoa complemento!");
                    dao.rollback();
                    return null;
                }

                juridica.setPessoa(pessoa);
                if (juridica.getCnae().getId() == -1) {
                    juridica.setCnae(null);
                }

                if (juridicaReceita.getId() != -1) {
                    juridicaReceita.setPessoa(pessoa);
                    if (!dao.update(juridicaReceita)) {
                        GenericaMensagem.error("Erro", "Erro ao Salvar Dados!");
                        dao.rollback();
                        return null;
                    }
                }

                if (!gerarLoginSenhaPessoa(juridica.getPessoa(), dao)) {
                    GenericaMensagem.error("Erro", "Erro ao Salvar Dados!");
                    dao.rollback();
                    return null;
                }
                if (dao.save(juridica)) {

                    if (juridicaReceita != null && juridicaReceita.getId() != -1) {
                        if (juridicaReceita.getStatus().toUpperCase().equals("BAIXADA")) {
                            ContribuintesInativos ci = new ContribuintesInativos();
                            ci.setJuridica(juridica);
                            ci.setDtInativacao(juridicaReceita.getDtSituacaoCadastral());
                            ci.setMotivoInativacao((MotivoInativacao) dao.find(new MotivoInativacao(), 4));
                            ci.setSolicitante("SISTEMA");
                            ci.setObservacao("INATIVAÇÃO AUTOMÁTICA PELO SISTEMA, CONFORME CADASTRO INATIVO NA RECEITA!!!");
                            if (!dao.save(ci)) {
                                GenericaMensagem.error("Erro", "Erro ao salvar contribuinte inátivo!");
                                dao.rollback();
                                return null;
                            }
                            contribuintesInativos = ci;
                        }
                    }

                    if (newMei) {
                        if (fisicaMei != null && fisicaMei.getId() != -1) {
                            PessoaEmpresa pe = new PessoaEmpresaDao().findSocioProprietario(fisicaMei.getId());
                            if (pe == null) {
                                pe = new PessoaEmpresa();
                                pe.setSocio(true);
                                pe.setJuridica(juridica);
                                pe.setFisica(fisicaMei);
                                pe.setFuncao((Profissao) dao.find(new Profissao(), 0));
                                pe.setSetor("");
                                if (juridica.getDtAbertura() == null) {
                                    pe.setDtAdmissao(new Date());
                                } else {
                                    pe.setDtAdmissao(juridica.getDtAbertura());
                                }
                                pe.setAvisoTrabalhado(false);
                                pe.setPrincipal(false);

                                if (!dao.save(pe)) {
                                    GenericaMensagem.error("Erro", "Erro ao salvar pessoa empresa!");
                                    dao.rollback();
                                    return null;
                                }
                            }
                        }
                    }

                    if (newMei) {
                        GenericaMensagem.info("Sucesso", "Novo cadastro de Micro Empreendedor Individual criado com sucesso! Empreendedor: " + fisicaMei.getPessoa().getNome());
                    } else {
                        GenericaMensagem.info("Sucesso", "Cadastro salvo!");
                    }

                    dao.commit();

                    NovoLog novoLog = new NovoLog();
                    novoLog.setTabela("pes_juridica");
                    novoLog.setCodigo(juridica.getId());
                    novoLog.save("ID: " + juridica.getId() + " - Pessoa: (" + juridica.getPessoa().getId() + ") " + juridica.getPessoa().getNome() + " - Abertura" + juridica.getAbertura() + " - Fechamento" + juridica.getAbertura() + " - I.E.: " + juridica.getInscricaoEstadual() + " - Insc. Mun.: " + juridica.getInscricaoMunicipal() + " - Responsável: " + juridica.getResponsavel());
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("juridicaPesquisa", juridica);
                } else {
                    GenericaMensagem.error("Erro", "Erro ao Salvar Dados!");
                    dao.rollback();
                    return null;
                }
            }
        } else {

            Juridica jur = (Juridica) dao.find(juridica);
//
//            if (!jur.getPessoa().getEmail1().equals(juridica.getPessoa().getEmail1())) {
//                if (!jur.getPessoa().getEmail1().isEmpty() && juridica.getPessoa().getEmail1().isEmpty()) {
//                    pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 1));
//                    idStatusCobranca = 1;
//                } else if (jur.getPessoa().getEmail1().isEmpty() && !juridica.getPessoa().getEmail1().isEmpty()) {
//                    idStatusCobranca = 1;
//                    if (configuracaoArrecadacao.getCobrancaEmail()) {
//                        idStatusCobranca = 2;
//                        pessoaComplemento.setStatusCobranca((StatusCobranca) dao.find(new StatusCobranca(), 2));
//                    }
//                }
//            }

            juridica.getPessoa().setDtAtualizacao(new Date());
            //juridica.getPessoa().setDtAtualizacao(null);
            if (juridica.getPessoa().getNome().isEmpty()) {
                GenericaMensagem.error("Erro", "O campo nome não pode ser nulo!");
                dao.rollback();
                return null;
            }
            juridica.getPessoa().setNome(jur.getPessoa().getNome());

            if (Integer.parseInt(((SelectItem) getListaTipoDocumento().get(idTipoDocumento)).getDescription()) == 4) {
                juridica.getPessoa().setDocumento("0");
            } else {
                juridica.getPessoa().setTipoDocumento(jur.getPessoa().getTipoDocumento());
                juridica.getPessoa().setDocumento(jur.getPessoa().getDocumento());
                listDocumento = db.pesquisaJuridicaPorDoc(juridica.getPessoa().getDocumento());
                for (int i = 0; i < listDocumento.size(); i++) {
                    if (!listDocumento.isEmpty() && ((Juridica) listDocumento.get(i)).getId() != juridica.getId()) {
                        GenericaMensagem.error("Erro", "Empresa já existente no Sistema!");
                        dao.rollback();
                        return null;
                    }
                }
                //juridica.getPessoa().setTipoDocumento((TipoDocumento) dao.find(new TipoDocumento(), Integer.parseInt(((SelectItem) getListaTipoDocumento().get(idTipoDocumento)).getDescription())));
                for (int i = 0; i < listaTipoDocumento.size(); i++) {
                    if (juridica.getPessoa().getTipoDocumento().getId() == Integer.parseInt(listaTipoDocumento.get(i).getDescription())) {
                        idTipoDocumento = i;
                        break;
                    }
                }
            }
            if (!validaTipoDocumento(Integer.parseInt(getListaTipoDocumento().get(idTipoDocumento).getDescription()), juridica.getPessoa().getDocumento())) {
                GenericaMensagem.error("Erro", "Documento Invalido!");
                dao.rollback();
                return null;
            }
            adicionarEnderecos();
            if ((juridica.getPessoa().getLogin()) == null && (juridica.getPessoa().getSenha()) == null) {
                if (!gerarLoginSenhaPessoa(juridica.getPessoa(), dao)) {
                    GenericaMensagem.error("Erro", "Erro ao atualizar Cadastro!");
                    dao.rollback();
                    return null;
                }
            }

            String beforeUpdate = "ID: " + jur.getId() + " - Pessoa: (" + jur.getPessoa().getId() + ") " + jur.getPessoa().getNome() + " - Abertura: " + jur.getAbertura() + " - Fechamento: " + jur.getAbertura() + " - I.E.: " + jur.getInscricaoEstadual() + " - Insc. Mun.: " + jur.getInscricaoMunicipal() + " - Responsável: " + jur.getResponsavel();

            if (!dao.update(juridica.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Cadastro!");
                dao.rollback();
                return null;
            }
            if (pessoaComplemento.getId() == -1) {
                pessoaComplemento.setPessoa(juridica.getPessoa());
                if (!dao.save(pessoaComplemento)) {
                    GenericaMensagem.warn("Validação", "Ao salvar pessoa complemento!");
                    dao.rollback();
                    return null;
                }
            } else {
                if (!dao.update(pessoaComplemento)) {
                    GenericaMensagem.warn("Validação", "Ao atualziar pessoa complemento!");
                    dao.rollback();
                    return null;
                }
            }

            if (dao.update(juridica)) {

                GenericaMensagem.info("Sucesso", "Cadastro atualizado com Sucesso!");
                dao.commit();

                // ATUALIZA CONTABILIDADE SE FOR ELA MESMA A PESSOA JURIDICA ---
                if ((juridica.getContabilidade() != null && juridica.getContabilidade().getId() != -1) && (juridica.getContabilidade().getId() == juridica.getId())) {
                    nomeContabilidade = juridica.getPessoa().getNome();
                    //juridica = (Juridica) new Dao().find(new Juridica(), juridica.getId());
                    //juridica.setContabilidade(juridica);
                    //dbSalvar.alterarObjeto(juridica.getContabilidade());
                }

                NovoLog novoLog = new NovoLog();
                novoLog.setTabela("pes_juridica");
                novoLog.setCodigo(juridica.getId());
                novoLog.update(beforeUpdate, "ID: " + juridica.getId() + " - Pessoa: (" + juridica.getPessoa().getId() + ") " + juridica.getPessoa().getNome() + " - Abertura: " + juridica.getAbertura() + " - Fechamento: " + juridica.getAbertura() + " - I.E.: " + juridica.getInscricaoEstadual() + " - Insc. Mun.: " + juridica.getInscricaoMunicipal() + " - Responsável: " + juridica.getResponsavel());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("juridicaPesquisa", juridica);
            } else {
                dao.rollback();
                GenericaMensagem.error("Erro", "Erro ao atualizar Cadastro!");
            }
        }
        getContribuinte();
        salvarEndereco();

//        loadListStatusCobranca();
        return null;
    }

    public String novo() {
//        juridica = new Juridica();
//        contabilidade = new Juridica();
//        pessoaEndereco = new PessoaEndereco();
//        convencao = new Convencao();
//        marcar = false;
//        alterarEnd = false;
//        renChkEndereco = "false";
//        cnaeContribuinte = " sem cnae! ";
//        colorContri = "red";
//        renEndereco = "false";
//        renNovoEndereco = "false";
//        msgDocumento = "";
//        listaEnd = new ArrayList();
//        idTipoDocumento = 1;
//        idPorte = 0;
//        listaContribuintesInativos.clear();
//        setEnderecoCompleto("");
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("pessoaComplementoBean");
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoPesquisa");
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoNum");
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoComp");
//        listaEmpresasPertencentes.clear();
        GenericaSessao.remove("juridicaBean");
        return "pessoaJuridica";
    }

    public void novoGenerico() {
        juridica = new Juridica();
        nomeContabilidade = "";
        pessoaEndereco = new PessoaEndereco();
        convencao = new Convencao();
        marcar = false;
        alterarEnd = false;
        renChkEndereco = "false";
        cnaeContribuinte = " sem cnae! ";
        colorContri = "red";
        renEndereco = false;
        renNovoEndereco = false;
        msgDocumento = "";
        listaEnd = new ArrayList();
        idTipoDocumento = 1;
        idPorte = 0;
        listaContribuintesInativos.clear();
        pessoaComplemento = new PessoaComplemento();
        setEnderecoCompleto("");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoPesquisa");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoNum");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoComp");
    }

    public String excluir() {
        Dao dao = new Dao();
        PessoaEnderecoDao dbPE = new PessoaEnderecoDao();
        if (juridica.getId() == -1) {
            GenericaMensagem.error("Erro", "Pesquise uma empresa para ser excluída!");
            return null;
        }
        List<PessoaEndereco> listaEndereco = dbPE.pesquisaEndPorPessoa(juridica.getPessoa().getId());
        PessoaComplemento pc = new PessoaComplementoDao().findByPessoa(juridica.getPessoa().getId());

        dao.openTransaction();
        if (!listaEndereco.isEmpty()) {
            PessoaEndereco pe;
            for (int i = 0; i < listaEndereco.size(); i++) {
                pe = (PessoaEndereco) dao.find(new PessoaEndereco(), listaEndereco.get(i).getId());
                if (!dao.delete(pe)) {
                    GenericaMensagem.error("Erro", "Erro ao excluir uma Pessoa Endereço!");
                    dao.rollback();
                    return null;
                }
            }
        }

        if (pc != null) {
            if (!dao.delete(pc)) {
                GenericaMensagem.error("Erro", "Erro ao excluir uma Pessoa Complemento!");
                dao.rollback();
                return null;
            }
        }

        ContribuintesInativosDao dbCI = new ContribuintesInativosDao();
        List<ContribuintesInativos> listaCI = dbCI.listaContribuintesInativos(juridica.getId());

        if (!listaCI.isEmpty()) {
            ContribuintesInativos ci;
            for (int i = 0; i < listaCI.size(); i++) {
                ci = (ContribuintesInativos) dao.find(new ContribuintesInativos(), listaCI.get(i).getId());
                if (!dao.delete(ci)) {
                    GenericaMensagem.error("Erro", "Erro ao excluir Contribuintes Inativos!");
                    dao.rollback();
                    return null;
                }
            }
        }

        // ------------------------------------------------------------------------------------------------------
        if (!dao.delete((Juridica) dao.find(new Juridica(), juridica.getId()))) {
            GenericaMensagem.error("Erro", "Erro ao excluir Jurídica!");
            dao.rollback();
            return null;
        }

        //  EXCLUIR OS EMAILS ENVIADOS PESSOA --------------------------------------------------------------
        EnvioEmailsDao db = new EnvioEmailsDao();
        List<EnvioEmails> listE = db.pesquisaTodosPorPessoa(juridica.getPessoa().getId());

        for (int i = 0; i < listE.size(); i++) {
            if (!dao.delete((EnvioEmails) dao.find(new EnvioEmails(), listE.get(i).getId()))) {
                GenericaMensagem.error("Erro", "Erro ao excluir Emails enviados!");
                dao.rollback();
                return null;
            }
        }

        PessoaDao dbp = new PessoaDao();
        String documento = AnaliseString.extrairNumeros(juridica.getPessoa().getDocumento());
        JuridicaReceita jr = dbp.pesquisaJuridicaReceita(documento);

        if (jr.getId() != -1) {
            if (!dao.delete(dao.find(new JuridicaReceita(), jr.getId()))) {
                GenericaMensagem.error("Erro", "Erro ao excluir Pesquisa da Receita!");
                dao.rollback();
                return null;
            }
        }
        // -------------------------------------------------------------------------------------------------

        if (!dao.delete((Pessoa) dao.find(new Pessoa(), juridica.getPessoa().getId()))) {
            GenericaMensagem.error("Erro", "Erro ao excluir Pessoa!");
            dao.rollback();
            return null;
        }
        GenericaMensagem.info("Sucesso", "Cadastro excluido com sucesso!");
        NovoLog novoLog = new NovoLog();
        novoLog.delete("ID: " + juridica.getId() + " - Pessoa: (" + juridica.getPessoa().getId() + ") " + juridica.getPessoa().getNome() + " - Abertura" + juridica.getAbertura() + " - Fechamento" + juridica.getAbertura() + " - I.E.: " + juridica.getInscricaoEstadual() + " - Insc. Mun.: " + juridica.getInscricaoMunicipal() + " - Responsável: " + juridica.getResponsavel());
        dao.commit();
        novoGenerico();
        return null;
    }

    public String editar(Juridica j, Boolean completo) {
        Dao dao = new Dao();
        // listRepisMovimento.clear();
        String url = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        Boolean clear = false;
        if (!completo) {
            if (url != null && !url.isEmpty() && !url.equals("pessoaJuridica")) {
                switch (url) {
                    case "agendamento":
                        break;
                    default:
                        clear = true;
                }
                if (clear) {
                    GenericaSessao.remove("juridicaBean");
                }
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("juridicaPesquisa", j);
                return url;
            }
        }
        juridica = (Juridica) dao.rebind(j);
        juridica.setPessoa((Pessoa) dao.rebind(j.getPessoa()));
        listSocios.clear();
        listaContribuintesInativos.clear();
        listaEmpresasPertencentes = new ArrayList();
        contribuintesInativos = new ContribuintesInativos();

        if (juridica.getContabilidade() != null) {
            nomeContabilidade = juridica.getContabilidade().getPessoa().getNome();
        } else {
            nomeContabilidade = "";
        }

        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        if (!getListaTipoDocumento().isEmpty()) {
            for (int o = 0; o < listaTipoDocumento.size(); o++) {
                if (Integer.parseInt(listaTipoDocumento.get(o).getDescription()) == juridica.getPessoa().getTipoDocumento().getId()) {
                    idTipoDocumento = o;
                }
            }
        }

        if (!getListaPorte().isEmpty()) {
            for (int o = 0; o < listaPorte.size(); o++) {
                if (Integer.parseInt(listaPorte.get(o).getDescription()) == juridica.getPorte().getId()) {
                    idPorte = o;
                }
            }
        }

        getContribuinte();
        if (juridica.getContabilidade() == null) {
            renChkEndereco = "false";
        } else {
            renChkEndereco = "true";
        }
        renNovoEndereco = false;
        renEndereco = false;
        alterarEnd = true;
        listaEnd = new ArrayList();
        enderecoCobranca = "NENHUM";
        getListaEnderecos();
        loadListDataVencimento();
        pessoaComplemento = new PessoaComplemento();
        pessoaComplemento = juridica.getPessoa().getPessoaComplemento();
        if (pessoaComplemento.getId() == -1) {
            pessoaComplemento = (PessoaComplemento) dao.rebind(new PessoaComplementoDao().findByPessoa(juridica.getPessoa().getId()));
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
            }
        }

        responsavel = pessoaComplemento.getResponsavel();
        if (responsavel == null) {
            responsavel = new Pessoa();
        }

        if (pessoaComplemento.getStatusCobranca() != null) {
            idStatusCobranca = pessoaComplemento.getStatusCobranca().getId();

        }
        diaVencimento = pessoaComplemento.getNrDiaVencimento();

        loadListStatusCobranca();
        
        existeOposicaoEmpresa();
        loadListSocios();
        loadMalaDireta();
        if (indicaTab == 6) {
            loadListaDocumentos();
        }
        return "pessoaJuridica";

    }

    public String editar(Juridica j) {
        return editar(j, false);
    }

    public String editarEmpresaPertencente(DataObject linha) {
        juridica = (Juridica) linha.getArgumento0();// (Juridica) listaEmpresasPertencentes.get(idIndexPertencente).getArgumento0();
        if (juridica.getContabilidade() != null) {
            nomeContabilidade = juridica.getContabilidade().getPessoa().getNome();
        }
        listaEmpresasPertencentes = new ArrayList();

        //contabilidade = juridica.getContabilidade();
        String url = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        indicaTab = 0;
        if (!getListaPorte().isEmpty()) {
            for (int o = 0; o < listaPorte.size(); o++) {
                if (Integer.parseInt(listaPorte.get(o).getDescription()) == juridica.getPorte().getId()) {
                    idPorte = o;
                }
            }
        }

        existeOposicaoEmpresa();
        loadListSocios();
        loadMalaDireta();

        if (url != null) {
            if (!getListaTipoDocumento().isEmpty()) {
                for (int o = 0; o < listaTipoDocumento.size(); o++) {
                    if (Integer.parseInt(listaTipoDocumento.get(o).getDescription()) == juridica.getPessoa().getTipoDocumento().getId()) {
                        setIdTipoDocumento(o);
                    }
                }
            }
            if (juridica.getContabilidade() == null) {
                renChkEndereco = "false";
            } else {
                renChkEndereco = "true";
            }
            renNovoEndereco = false;
            renEndereco = false;
            alterarEnd = true;
            listaEnd = new ArrayList();
            enderecoCobranca = "NENHUM";
            listaContribuintesInativos.clear();
            getListaEnderecos();

            return "pessoaJuridica";
        }
        return "pessoaJuridica";
    }

    public String editarEmpresaContabilidade() {
        listaEmpresasPertencentes = new ArrayList();
        Dao dao = new Dao();
        juridica = (Juridica) dao.find(new Juridica(), juridica.getContabilidade().getId());
        if (juridica.getContabilidade() != null) {
            nomeContabilidade = juridica.getContabilidade().getPessoa().getNome();
        }
        String url = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
//        loadListStatusCobranca(true);
        pessoaComplemento = new PessoaComplemento();
        pessoaComplemento = juridica.getPessoa().getPessoaComplemento();
        if (pessoaComplemento.getId() == -1) {
            pessoaComplemento = (PessoaComplemento) dao.rebind(new PessoaComplementoDao().findByPessoa(juridica.getPessoa().getId()));
            if (pessoaComplemento == null) {
                pessoaComplemento = new PessoaComplemento();
            }
        }

        responsavel = pessoaComplemento.getResponsavel();
        if (responsavel == null) {
            responsavel = new Pessoa();
        }

        if (pessoaComplemento.getStatusCobranca() != null) {
            idStatusCobranca = pessoaComplemento.getStatusCobranca().getId();
        }
        diaVencimento = pessoaComplemento.getNrDiaVencimento();

        existeOposicaoEmpresa();
        loadListSocios();
        loadMalaDireta();

        if (url != null) {
            if (!getListaTipoDocumento().isEmpty()) {
                for (int o = 0; o < listaTipoDocumento.size(); o++) {
                    if (Integer.parseInt(listaTipoDocumento.get(o).getDescription()) == juridica.getPessoa().getTipoDocumento().getId()) {
                        setIdTipoDocumento(o);
                    }
                }
            }
            if (juridica.getContabilidade() == null) {
                renChkEndereco = "false";
            } else {
                renChkEndereco = "true";
            }

            renNovoEndereco = false;
            renEndereco = false;
            alterarEnd = true;
            listaEnd = new ArrayList();
            enderecoCobranca = "NENHUM";
            listaContribuintesInativos.clear();
            getListaEnderecos();

            return "pessoaJuridica";
        }
        return "pessoaJuridica";
    }

    public String editarContabilidade(Juridica j) {
        Juridica contabilidade = j; //(Juridica) listaContabilidade.get(idIndexContabilidade);
        juridica.setContabilidade(contabilidade);
        juridica.setEmailEscritorio(true);
        juridica.setCobrancaEscritorio(true);
        renChkEndereco = "true";
        nomeContabilidade = contabilidade.getPessoa().getNome();
        listaEmpresasPertencentes = new ArrayList();
        //chkEndContabilidade = true; // ROGÉRINHO PEDIU PRA VOLTAR TRUE NA DATA -- 30/07/2013 -- POR CAUSA DO CARLOS DE LIMEIRA
        return "pessoaJuridica";
    }

//   public List getListaJuridica(){
//       List result = null;
//       JuridicaDB db = new JuridicaDao();
//       result = db.pesquisaTodos();
//       return result;
//   }
    public String adicionarEnderecos() {
        // List tiposE = new ArrayList();
        TipoEnderecoDao db_tipoEndereco = new TipoEnderecoDao();
        PessoaEnderecoDao db_pesEnd = new PessoaEnderecoDao();
        endereco = new Endereco();
        String num;
        String comp;
        int i = 0;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("enderecoNum", pessoaEndereco.getNumero());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("enderecoComp", pessoaEndereco.getComplemento());
        List tiposE = db_tipoEndereco.listaTipoEnderecoParaJuridica();
        endereco = (Endereco) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoPesquisa");
        if (endereco != null) {
            if (!alterarEnd || listaEnd.isEmpty()) {
                num = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoNum");
                comp = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoComp");
                while (i < tiposE.size()) {
                    pessoaEndereco.setEndereco(endereco);
                    pessoaEndereco.setTipoEndereco((TipoEndereco) tiposE.get(i));
                    pessoaEndereco.setPessoa(juridica.getPessoa());
                    pessoaEndereco.setNumero(num);
                    pessoaEndereco.setComplemento(comp);
                    listaEnd.add(pessoaEndereco);
                    i++;
                    pessoaEndereco = new PessoaEndereco();
                }
            } else {
                num = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoNum");
                comp = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoComp");
                if (!listaEnd.isEmpty() && (pessoaEndereco.getTipoEndereco().getId() == 2)) {

                    if (pessoaEndereco.getId() != -1) {
                        // PessoaEndereco pessoaEndeAnt = new PessoaEndereco();
                        PessoaEndereco pessoaEndeAnt = db_pesEnd.pesquisaEndPorPessoaTipo(pessoaEndereco.getPessoa().getId(), 2);
                        ((PessoaEndereco) listaEnd.get(0)).setTipoEndereco((TipoEndereco) tiposE.get(0));
                        ((PessoaEndereco) listaEnd.get(0)).setEndereco(endereco);
                        ((PessoaEndereco) listaEnd.get(0)).setComplemento(pessoaEndereco.getComplemento());
                        ((PessoaEndereco) listaEnd.get(0)).setNumero(pessoaEndereco.getNumero());
                        for (int u = 1; u < listaEnd.size(); u++) {
                            if (comparaEndereco(pessoaEndeAnt, (PessoaEndereco) listaEnd.get(u))) {
                                ((PessoaEndereco) listaEnd.get(u)).setTipoEndereco((TipoEndereco) tiposE.get(u));
                                ((PessoaEndereco) listaEnd.get(u)).setEndereco(endereco);
                                ((PessoaEndereco) listaEnd.get(u)).setComplemento(pessoaEndereco.getComplemento());
                                ((PessoaEndereco) listaEnd.get(u)).setNumero(pessoaEndereco.getNumero());
                            }
                        }
                        endComercial = true;
                    } else {
                        listaEnd = new ArrayList();
                        for (int u = 0; u < tiposE.size(); u++) {
                            pessoaEndereco.setEndereco(endereco);
                            pessoaEndereco.setTipoEndereco((TipoEndereco) tiposE.get(u));
                            pessoaEndereco.setPessoa(juridica.getPessoa());
                            pessoaEndereco.setNumero(num);
                            pessoaEndereco.setComplemento(comp);
                            listaEnd.add(pessoaEndereco);
                            pessoaEndereco = new PessoaEndereco();
                        }
                    }
                } else {
                    pessoaEndereco.setEndereco(endereco);
                    pessoaEndereco.setPessoa(juridica.getPessoa());
                    pessoaEndereco.setNumero(num);
                    pessoaEndereco.setComplemento(comp);

                    ((PessoaEndereco) listaEnd.get(idIndexEndereco)).setEndereco(endereco);
                    ((PessoaEndereco) listaEnd.get(idIndexEndereco)).setComplemento(pessoaEndereco.getComplemento());
                    ((PessoaEndereco) listaEnd.get(idIndexEndereco)).setNumero(pessoaEndereco.getNumero());
                }
                alterarEnd = false;
            }
            renEndereco = true;
            renNovoEndereco = false;
        }
        setEnderecoCompleto("");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoPesquisa");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoNum");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("enderecoComp");
        return "pessoaJuridica";
    }

    public boolean comparaEndereco(PessoaEndereco pessoaEnde1, PessoaEndereco pessoaEnde2) {
        boolean compara;
        if (pessoaEnde1 != null && pessoaEnde2 != null) {
            if (pessoaEnde1.getComplemento() == null || pessoaEnde2.getComplemento() == null) {
                pessoaEnde1.setComplemento("");
                pessoaEnde2.setComplemento("");
            }
            if ((pessoaEnde1.getEndereco().getId() == pessoaEnde2.getEndereco().getId()
                    && pessoaEnde1.getNumero().equals(pessoaEnde2.getNumero())
                    && pessoaEnde1.getComplemento().equals(pessoaEnde2.getComplemento()))) {
                compara = true;
            } else {
                compara = false;
            }
        } else {
            compara = false;
        }
        return compara;
    }

    public List<PessoaEndereco> getListaEnderecos() {
        // PessoaEndereco pesEn = new PessoaEndereco();
        String strCompl;
        if (!getPesquisaEndPorPessoa().isEmpty() && alterarEnd && listaEnd.isEmpty()) {
            listaEnd = getPesquisaEndPorPessoa();
            if (listaEnd.size() < 4) {
                PessoaEndereco pe = (PessoaEndereco) listaEnd.get(0);
                PessoaEndereco pe2 = new PessoaEndereco();
                Dao dao = new Dao();
                List<TipoEndereco> list = dao.find("TipoEndereco", new int[]{2, 3, 4, 5});
                List<TipoEndereco> listB = list;
                for (int i = 0; i < listaEnd.size(); i++) {
                    for (int y = 0; y < listB.size(); y++) {
                        if (((PessoaEndereco) listaEnd.get(i)).getTipoEndereco().getId() == listB.get(y).getId()) {
                            try {
                                list.remove(listB.get(y));
                            } catch (Exception e) {
                                // e.getMessage();
                            }
                        }
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    pe2 = new PessoaEndereco();
                    pe2.setComplemento(pe.getComplemento());
                    pe2.setEndereco(pe.getEndereco());
                    pe2.setNumero(pe.getNumero());
                    pe2.setPessoa(pe.getPessoa());
                    pe2.setTipoEndereco(list.get(i));
                    dao.save(pe2, true);
                }
                listaEnd.clear();
                listaEnd = getPesquisaEndPorPessoa();
            }
            PessoaEndereco pesEn = (PessoaEndereco) (listaEnd.get(1));
            if (pesEn.getComplemento() == null || pesEn.getComplemento().isEmpty()) {
                strCompl = " ";
            } else {
                strCompl = " ( " + pesEn.getComplemento() + " ) ";
            }
            enderecoCobranca = pesEn.getEndereco().getLogradouro().getDescricao() + " "
                    + pesEn.getEndereco().getDescricaoEndereco().getDescricao() + ", " + pesEn.getNumero() + " " + pesEn.getEndereco().getBairro().getDescricao() + ","
                    + strCompl + pesEn.getEndereco().getCidade().getCidade() + " - " + pesEn.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(pesEn.getEndereco().getCep());
        }
        return listaEnd;
    }

    public void abreEndereco() {
        listaEnd = getListaEnderecos();
        if (listaEnd.isEmpty()) {
            renEndereco = false;
            renNovoEndereco = true;
            pessoaEndereco = new PessoaEndereco();
            listaEnd = new ArrayList();
        } else {
            renEndereco = true;
            renNovoEndereco = false;
        }
    }

    public void salvarEndereco() {
        //VERIFICAR ENDERECO CONTABILIDADE
        verificarEndContabilidade();
        if (juridica.getId() != -1) {
            Dao dao = new Dao();
            if (getPesquisaEndPorPessoa().isEmpty()) {
                for (int i = 0; i < listaEnd.size(); i++) {
                    pessoaEndereco = (PessoaEndereco) listaEnd.get(i);
                    dao.openTransaction();
                    if (dao.save(pessoaEndereco)) {
                        dao.commit();
                    } else {
                        dao.rollback();
                        msgConfirma = "Erro ao Salvar Endereço!";
                    }
                    pessoaEndereco = new PessoaEndereco();
                }
            } else if (endComercial) {
                atualizarEndJuridicaComContabil();
                for (int o = 0; o < listaEnd.size(); o++) {
                    dao.openTransaction();
                    if (dao.update((PessoaEndereco) listaEnd.get(o))) {
                        dao.commit();
                    } else {
                        dao.rollback();
                    }
                }
                endComercial = false;
            } else {
                if (pessoaEndereco.getTipoEndereco().getId() == 3) {
                    atualizarEndJuridicaComContabil();
                }
                for (int o = 0; o < listaEnd.size(); o++) {
                    dao.openTransaction();
                    if (dao.update((PessoaEndereco) listaEnd.get(o))) {
                        dao.commit();
                    } else {
                        dao.rollback();
                    }
                }
            }
            pessoaEndereco = new PessoaEndereco();
        }
    }

    public void verificarEndContabilidade() {
        PessoaEnderecoDao db = new PessoaEnderecoDao();
        if (juridica.getId() != -1) {
            if (juridica.isCobrancaEscritorio() && (juridica.getContabilidade() != null && juridica.getContabilidade().getId() != -1)) {
                if (juridica.getId() != juridica.getContabilidade().getId()) {
                    PessoaEndereco pesEndCon = db.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3);
                    if ((!listaEnd.isEmpty()) && pesEndCon != null) {
                        pessoaEndereco = (PessoaEndereco) listaEnd.get(1);
                        pessoaEndereco.setComplemento(pesEndCon.getComplemento());
                        pessoaEndereco.setNumero(pesEndCon.getNumero());
                        endereco = pesEndCon.getEndereco();
                        pessoaEndereco.setEndereco(endereco);
                        listaEnd.set(1, pessoaEndereco);
                    }
                }
            } else if (juridica != null) {
                if (juridica.getContabilidade() != null) {
                    if (comparaEndereco((PessoaEndereco) listaEnd.get(1), db.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3))) {
                        PessoaEndereco pesEndCon = db.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 2);
                        if ((!listaEnd.isEmpty()) && pesEndCon != null && !endComercial) {
                            pessoaEndereco = (PessoaEndereco) listaEnd.get(1);
                            pessoaEndereco.setComplemento(pesEndCon.getComplemento());
                            pessoaEndereco.setNumero(pesEndCon.getNumero());
                            endereco = pesEndCon.getEndereco();
                            pessoaEndereco.setEndereco(endereco);
                            listaEnd.set(1, pessoaEndereco);
                        }
                    }
                }
                //juridica.setCobrancaEscritorio(false);
            }
        }
    }

    public void pesquisaContabilidadeI() {
        if (!nomePesquisaContabilidade.isEmpty()) {
            JuridicaDao db = new JuridicaDao();
            db.setContabilidade(true);
            listaContabilidade = db.pesquisaPessoa(nomePesquisaContabilidade, "nome", "I");
        } else {
            listaContabilidade = new ArrayList();
        }
    }

    public void pesquisaContabilidadeP() {
        if (!nomePesquisaContabilidade.isEmpty()) {
            JuridicaDao db = new JuridicaDao();
            db.setContabilidade(true);
            listaContabilidade = db.pesquisaPessoa(nomePesquisaContabilidade, "nome", "P");
        } else {
            listaContabilidade = new ArrayList();
        }
    }

    public String atualizarEndJuridicaComContabil() {
        if (juridica.getId() != -1) {
            JuridicaDao db = new JuridicaDao();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            Dao dao = new Dao();
            List listaPesEndEmpPertencente = db.pesquisaPesEndEmpresaComContabil(juridica.getId());
            PessoaEndereco endeEmp2 = pesEndDB.pesquisaEndPorPessoaTipo(juridica.getPessoa().getId(), 2);
            if (!listaPesEndEmpPertencente.isEmpty()) {
                pessoaEndereco = (PessoaEndereco) listaEnd.get(1);
                for (int i = 0; i < listaPesEndEmpPertencente.size(); i++) {
                    if (comparaEndereco(endeEmp2, (PessoaEndereco) listaPesEndEmpPertencente.get(i))) {
                        PessoaEndereco endeEmp = (PessoaEndereco) listaPesEndEmpPertencente.get(i);
                        endeEmp.setComplemento(pessoaEndereco.getComplemento());
                        endeEmp.setNumero(pessoaEndereco.getNumero());
                        endeEmp.setEndereco(pessoaEndereco.getEndereco());
                        dao.openTransaction();
                        if (dao.update(endeEmp)) {
                            dao.commit();
                            endeEmp = new PessoaEndereco();
                        } else {
                            dao.rollback();
                        }
                    }
                }
            }
        }
        return null;
    }

    public String RetornarObjetoDaGrid(PessoaEndereco linha, int index) {
        pessoaEndereco = linha;//(PessoaEndereco) listaEnd.get(idIndexEndereco);
        idIndexEndereco = index;
        PessoaEnderecoDao db = new PessoaEnderecoDao();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("enderecoPesquisa", pessoaEndereco.getEndereco());
        log = pessoaEndereco.getEndereco().getLogradouro().getDescricao();
        desc = pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao();
        cid = pessoaEndereco.getEndereco().getCidade().getCidade();
        uf = pessoaEndereco.getEndereco().getCidade().getUf();
        setEnderecoCompleto(log + " " + desc + ", " + cid + " - " + uf);
        renEndereco = false;
        renNovoEndereco = true;
        alterarEnd = true;
        return "pessoaJuridica";
    }

    public String CarregarEndereco() {
        int idEndereco = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paramEndereco"));
        pessoaEndereco.setEndereco((Endereco) new Dao().find(new Endereco(), idEndereco));
        setEnderecoCompleto((pessoaEndereco.getEndereco().getLogradouro().getDescricao()) + " " + pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao());
        return "pessoaJuridica";
    }

    public List<String> BuscaTipoEndereco(Object event) {
        //List<String> result = new Vector<String>();
        String txtDigitado = event.toString().toLowerCase().toUpperCase();
        TipoEnderecoDao db = new TipoEnderecoDao();
        List<String> result = db.pesquisaTipoEnderecoParaJuridica('%' + txtDigitado + '%');
        return (result);
    }

    public List<String> BuscaTipoDocumento(Object event) {
        //List<String> result = new Vector<String>();
        String txtDigitado = event.toString().toLowerCase().toUpperCase();
        TipoDocumentoDao db = new TipoDocumentoDao();
        List<String> result = db.pesquisaTipoDocumento('%' + txtDigitado + '%');
        return (result);
    }

    public List getPesquisaEndPorPessoa() {
        PessoaEnderecoDao db = new PessoaEnderecoDao();
        List result = db.pesquisaEndPorPessoa(juridica.getPessoa().getId());
        return result;
    }

    public String voltarEndereco() {
        indicaTab = 0;
        return "pessoaJuridica";
    }

    public boolean getHabilitar() {
        if (juridica.getPessoa().getId() == -1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getHabilitarFilial() {
        if ((juridica.getPessoa().getId() != -1) && (filialMatriz.equals("m"))) {
            Dao dao = new Dao();
            if (filial.getId() == -1) {
                filial.setFilial(juridica);
                filial.setMatriz(juridica);
                dao.openTransaction();
                if (dao.save(filial)) {
                    dao.commit();
                } else {
                    dao.rollback();
                }
            } else {
                dao.openTransaction();
                if (dao.update(filial)) {
                    dao.commit();
                } else {
                    dao.rollback();
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public String excluirPessoaEndereco() {
        if (pessoaEndereco.getId() != -1) {
            if (new Dao().delete(pessoaEndereco, true)) {
                pessoaEndereco = new PessoaEndereco();
            }
        }
        setEnderecoCompleto("");
        return "pessoaJuridica";
    }

    public void refreshForm() {
    }

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
        listaJuridica.clear();
        loadList();
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
        listaJuridica.clear();
        loadList();
    }

    public void acaoPesquisaCnaeInicial() {
        comoPesquisaCnae = "I";
        descPesquisaCnae = descPesquisaCnae.replace("-", "").replace("/", "").replace(".", "");
        listaCnae.clear();
    }

    public void acaoPesquisaCnaeParcial() {
        comoPesquisaCnae = "P";
        descPesquisaCnae = descPesquisaCnae.replace("-", "").replace("/", "").replace(".", "");
        listaCnae.clear();
    }

    public List<Cnae> getListaCnae() {
        if (listaCnae.isEmpty()) {
            CnaeDao db = new CnaeDao();
            listaCnae = db.pesquisaCnae(descPesquisaCnae, porPesquisaCnae, comoPesquisaCnae);
        }
        return listaCnae;
    }

    public void setListaCnae(List<Cnae> listaCnae) {
        this.listaCnae = listaCnae;
    }

    public void retornaCnaeReceita(Cnae cn) {
        juridica.setCnae(cn);
        CnaeConvencaoDao dbCnaeCon = new CnaeConvencaoDao();
        if (dbCnaeCon.pesquisaCnaeComConvencao(juridica.getCnae().getId()) != null) {
            cnaeContribuinte = " cnae contribuinte!";
            colorContri = "blue";
        } else {
            cnaeContribuinte = " este cnae não está na convenção!";
            colorContri = "red";
        }
    }

    public void retornaCnaeListReceita(Cnae cn, List<Cnae> list_cn) {
        idCnaeReceita = 1;
        listaCnaeReceita.clear();

        listaCnaeReceita.add(new SelectItem(0, cn.getNumero() + " " + cn.getCnae() + " ( Principal na Receita )", "" + cn.getId()));
        for (int i = 0; i < list_cn.size(); i++) {
            listaCnaeReceita.add(new SelectItem(i + 1, list_cn.get(i).getNumero() + " " + list_cn.get(i).getCnae(), "" + list_cn.get(i).getId()));
        }

        retornaCnaeReceita(cn);
    }

    public String retornaCnae(Cnae cn) {
        //Cnae tcnae = null;
        //Cnae tcnae = (Cnae) listaCnae.get(idIndexCnae);
        Cnae tcnae = cn;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cnaePesquisado", tcnae);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        descPesquisaCnae = "";
        if (((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno")).equals("pessoaJuridica")) {
            juridica.setCnae(tcnae);
            CnaeConvencaoDao dbCnaeCon = new CnaeConvencaoDao();
            if (dbCnaeCon.pesquisaCnaeComConvencao(juridica.getCnae().getId()) != null) {
                cnaeContribuinte = " cnae contribuinte!";
                colorContri = "blue";
            } else {
                cnaeContribuinte = " este cnae não está na convenção!";
                colorContri = "red";
            }
            return "pessoaJuridica";
        } else {
            return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno");
        }
    }

    public String JuridicaFilialGrid() {
        filial = new Filial();
        int i = filial.getFilial().getId();
        filial.setMatriz(juridica);
        if (new Dao().save(filial)) {

        } else {

        }
        //setIndicaTab("filial");
        return "pessoaJuridica";
    }

    public List getPesquisaJuridicaFilial() {
        //List result = null;
        FilialDao db = new FilialDao();
        List result = db.pesquisaJuridicaFilial(juridica.getId());
        return result;
    }

    public void excluirFilial() {

        FilialDao db = new FilialDao();
        filial = db.pesquisaFilialPertencente(juridica.getId(), filial.getFilial().getId());

        if (new Dao().delete(filial)) {

        } else {

        }
        filial = new Filial();
    }

    public List getPesquisaFilial() {
        //List result = null;
        FilialDao db = new FilialDao();
        List result = db.pesquisaFilial(descPesquisa, porPesquisa, comoPesquisa, juridica.getId());
        return result;
    }

    public boolean getHabilitarComboBoxFilial() {
        if (juridica.getPessoa().getId() == -1) {
            return false;
        } else {
            return true;
        }
    }

    public String getColocarMascara() {
        return Mask.getMascara(getListaTipoDocumento().get(idTipoDocumento).getLabel());
    }

    public String getColocarMascara2() {
        if (alterType == null || alterType.equals("nome")) {
            return "";
        } else {
            return Mask.getMascara(getListaTipoDocumento().get(idTipoDocumento).getLabel());
        }
    }

    public List<SelectItem> getListaTipoDocumento() {
        if (listaTipoDocumento.isEmpty()) {
            List<TipoDocumento> list = (List<TipoDocumento>) new Dao().list(new TipoDocumento());
            for (int i = 0; i < list.size(); i++) {
                listaTipoDocumento.add(new SelectItem(i, (String) (list.get(i)).getDescricao(), Integer.toString((list.get(i)).getId())));
            }
        }
        return listaTipoDocumento;
    }

    public String getRetornarEnderecoAmbos() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoPesquisa") != null) {
            log = ((Endereco) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoPesquisa")).getLogradouro().getDescricao();
            desc = ((Endereco) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoPesquisa")).getDescricaoEndereco().getDescricao();
            cid = ((Endereco) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoPesquisa")).getCidade().getCidade();
            uf = ((Endereco) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("enderecoPesquisa")).getCidade().getUf();
            setEnderecoCompleto(log + " " + desc + ", " + cid + " - " + uf);
        }
        return enderecoCompleto;
    }

    public CnaeConvencao retornarCnaeConvencao() {
        CnaeConvencaoDao dbCnae = new CnaeConvencaoDao();
        if (juridica.getCnae() != null && juridica.getCnae().getId() != -1) {
            cnaeConvencao = dbCnae.pesquisaCnaeComConvencao(juridica.getCnae().getId());
        } else {
            cnaeConvencao = new CnaeConvencao();
        }
        return cnaeConvencao;
    }

    public void btnExcluirContabilidadePertencente() {
//        if (juridica.getId() != -1) {
//            //chkEndContabilidade = false;
//            juridica.setContabilidade(null);
//            juridica.setEmailEscritorio(false);
//            juridica.setCobrancaEscritorio(false);
//            
//            //salvarEndereco();
//        } else {
//            juridica.setContabilidade(null);
//            juridica.setEmailEscritorio(false);
//            juridica.setCobrancaEscritorio(false);
//            listaEnd.set(1, listaEnd.get(0));
//        }

        juridica.setContabilidade(null);
        juridica.setEmailEscritorio(false);
        juridica.setCobrancaEscritorio(false);

        if (!listaEnd.isEmpty()) {
            PessoaEndereco pe = (PessoaEndereco) listaEnd.get(1);
            pe.setComplemento(((PessoaEndereco) listaEnd.get(0)).getComplemento());
            pe.setEndereco(((PessoaEndereco) listaEnd.get(0)).getEndereco());
            pe.setNumero(((PessoaEndereco) listaEnd.get(0)).getNumero());
            listaEnd.set(1, pe);
        }
    }

    public List<SelectItem> getListaMotivoInativacao() {
        if (listaMotivoInativacao.isEmpty()) {
            List<MotivoInativacao> list = (List<MotivoInativacao>) new Dao().list(new MotivoInativacao());
            for (int i = 0; i < list.size(); i++) {
                listaMotivoInativacao.add(new SelectItem(i,
                        list.get(i).getDescricao(),
                        Integer.toString(list.get(i).getId())));
            }
        }
        return listaMotivoInativacao;
    }

    public String pesquisarPessoaJuridicaGeracaoCadastrar() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("urlRetorno", "processamentoIndividual");
        return "pessoaJuridica";
    }

    public boolean validaTipoDocumento(int idDoc, String docS) {
        // 1 cpf, 2 cnpj, 3 cei, 4 nenhum
        //String documento = "";
        String documento = docS.replace(".", "").replace("/", "").replace("-", "");

        boolean ye = false;
        if (idDoc == 1) {
            ye = ValidaDocumentos.isValidoCPF(documento);
        }
        if (idDoc == 2) {
            ye = ValidaDocumentos.isValidoCNPJ(documento);
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

    public String linkDaReceita() {
        if (juridica != null) {
            int i = 0;
            String documento = "";
            String docLaco = juridica.getPessoa().getDocumento();
            if (validaTipoDocumento(2, docLaco)) {
                while (i < docLaco.length()) {
                    String as = docLaco.substring(i, i + 1);
                    if (!as.equals(".") && !as.equals("-") && !as.equals("/")) {
                        documento = documento + as;
                    }
                    i++;
                }
                Clipboard copia = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(documento);
                copia.setContents(selection, null);
            } else {
                Clipboard copia = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection("Ação Inválida!");
                copia.setContents(selection, null);
            }
        }
        return null;
    }

    public List<DataObject> getListaEmpresasPertencentes() {
        if (juridica.getId() != -1) {
            if (listaEmpresasPertencentes.isEmpty()) {
                JuridicaDao db = new JuridicaDao();
                PessoaEnderecoDao dbPe = new PessoaEnderecoDao();
                PessoaEndereco pe;
                List listaX = db.listaContabilidadePertencente(juridica.getId());
                for (int i = 0; i < listaX.size(); i++) {
                    pe = dbPe.pesquisaEndPorPessoaTipo(((Juridica) (listaX.get(i))).getPessoa().getId(), 2);
                    listaEmpresasPertencentes.add(new DataObject((Juridica) (listaX.get(i)), pe));
                }
            }
        }
        return listaEmpresasPertencentes;
    }

    public void enviarEmail() {
        if (juridica.getId() != -1) {
            Dao dao = new Dao();
            Mail mail = new Mail();
            mail.setFiles(new ArrayList());
            mail.setEmail(
                    new Email(
                            -1,
                            DataHoje.dataHoje(),
                            DataHoje.livre(new Date(), "HH:mm"),
                            (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                            (Rotina) dao.find(new Rotina(), 82),
                            null,
                            "Envio de Login e Senha",
                            "<h5><b>Login: </b> " + juridica.getPessoa().getLogin() + "</h5><br /> <h5><b>Senha: </b> " + juridica.getPessoa().getSenha() + "</h5>",
                            false,
                            false
                    )
            );
            List<Pessoa> pessoas = new ArrayList();
            pessoas.add(juridica.getPessoa());

            List<EmailPessoa> emailPessoas = new ArrayList();
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
                msgConfirma = retorno[1];
                GenericaMensagem.error("Erro", msgConfirma);
            } else {
                msgConfirma = retorno[0];
                GenericaMensagem.info("Sucesso", msgConfirma);
            }

        } else {
            GenericaMensagem.warn("Erro", "Pesquisar uma Empresa para envio!");
        }
    }

    public String enviarEmailParaTodos() {
        JuridicaDao juridicaDB = new JuridicaDao();
        List<Juridica> juridicas = juridicaDB.pesquisaJuridicaComEmail();
        Registro reg = Registro.get();
        msgConfirma = EnviarEmail.EnviarEmailAutomatico(reg, juridicas);
        return null;
    }

    public boolean isRenEnviarEmail() {
        if (juridica.getId() == 1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean gerarLoginSenhaPessoa(Pessoa pessoa, Dao dao) {
        try {
            String login = "", senha = "", nome = "";
            senha = senha + DataHoje.hora().replace(":", "");
            senha = Integer.toString(Integer.parseInt(senha) + Integer.parseInt(senha + "43"));
            senha = senha.substring(senha.length() - 6, senha.length());
            nome = AnaliseString.removerAcentos(pessoa.getNome().replace(" ", "X").toUpperCase());
            nome = nome.replace("-", "Y");
            nome = nome.replace(".", "W");
            nome = nome.replace("/", "Z");
            nome = nome.replace("A", "Q");
            nome = nome.replace("E", "R");
            nome = nome.replace("I", "H");
            nome = nome.replace("O", "P");
            nome = nome.replace("U", "M");
            nome = ("JHSRGDQ" + nome) + pessoa.getId();
            login = nome.substring(nome.length() - 6, nome.length());
            pessoa.setLogin(login);
            pessoa.setSenha(senha);
            return dao.update(pessoa);
        } catch (Exception e) {
            return false;
        }

    }

    public void atualizaEnvioEmails() {
        if (juridica.getId() != -1) {
            envioEmails = new EnvioEmails();
            envioEmails.setEmail(juridica.getPessoa().getEmail1());
            envioEmails.setHistorico("Envio de Login e senha para Contribuinte.");
            envioEmails.setOperacao("LOGIN");
            envioEmails.setPessoa(juridica.getPessoa());
            envioEmails.setDtEnvio(DataHoje.dataHoje());
            carregaEnvios = true;
            listEn = new ArrayList();
        }
    }

    public String bloqueioContribuicao() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pessoaPesquisa", juridica.getPessoa());
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).bloqueioServicos();
    }

    public String extratoTela() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pessoaPesquisa", juridica.getPessoa());
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).extratoTela();
    }

    public String extratoTelaListaContabil(Juridica j) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pessoaPesquisa", j.getPessoa());
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).extratoTela();
    }

    public void updateCobrancaEscritorio(Juridica j) {
        new Dao().update(j, true);
        if (j.isCobrancaEscritorio()) {
            GenericaMensagem.info("Sucesso", "A COBRANÇA SERÁ REALIZADA NESTE ESCRITÓRIO");
        } else {
            GenericaMensagem.info("Sucesso", "A COBRANÇA SERÁ FEITA DIRETA NA EMPRESA");
        }
        listaEmpresasPertencentes = new ArrayList();
        getListaEmpresasPertencentes();

    }

    public String retornaDaInativacao() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        return "pessoaJuridica";
    }

    public void setFilialSwap(Filial filialSwap) {
        this.filialSwap = filialSwap;
    }

    public Filial getFilialSwap() {
        return filialSwap;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilialMatriz(String filialMatriz) {
        this.filialMatriz = filialMatriz;
    }

    public String getFilialMatriz() {
        return filialMatriz;
    }

    public void setComoPesquisaCnae(String comoPesquisaCnae) {
        this.comoPesquisaCnae = comoPesquisaCnae;
    }

    public String getComoPesquisaCnae() {
        return comoPesquisaCnae;
    }

    public void setPorPesquisaCnae(String porPesquisaCnae) {
        this.porPesquisaCnae = porPesquisaCnae;
    }

    public String getPorPesquisaCnae() {
        return porPesquisaCnae;
    }

    public void setDescPesquisaCnae(String descPesquisaCnae) {
        this.descPesquisaCnae = descPesquisaCnae;
    }

    public String getDescPesquisaCnae() {
        return descPesquisaCnae;
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

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setEnderecoDeletado(String enderecoDeletado) {
        this.enderecoDeletado = enderecoDeletado;
    }

    public String getEnderecoDeletado() {
        return enderecoDeletado;
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

    public int getIndicaTab() {
        return indicaTab;
    }

    public void setIndicaTab(int indicaTab) {
        this.indicaTab = indicaTab;
    }

    public Juridica getJuridica() {
        if (juridica.getFantasia().isEmpty() || juridica.getFantasia() == null) {
            juridica.setFantasia(juridica.getPessoa().getNome());
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public String getStrGrupoCidade() {
        ConvencaoCidadeDao dbCon = new ConvencaoCidadeDao();
        if (convencao.getId() != -1 && !listaEnd.isEmpty()) {
            gruCids = dbCon.pesquisaGrupoCidadeJuridica(convencao.getId(), ((PessoaEndereco) listaEnd.get(3)).getEndereco().getCidade().getId());
            if (gruCids != null) {
                strGrupoCidade = gruCids.getDescricao();
            } else {
                strGrupoCidade = "";
                gruCids = new GrupoCidade();
            }
        } else {
            strGrupoCidade = "";
            gruCids = new GrupoCidade();
        }
        return strGrupoCidade;
    }

    public void setStrGrupoCidade(String strGrupoCidade) {
        this.strGrupoCidade = strGrupoCidade;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public boolean getMarcar() {
        return marcar;
    }

    public void setMarcar(boolean marcar) {
        this.marcar = marcar;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public int getIdMotivoInativacao() {
        return idMotivoInativacao;
    }

    public void setIdMotivoInativacao(int idMotivoInativacao) {
        this.idMotivoInativacao = idMotivoInativacao;
    }

    public Convencao getConvencao() {
        return convencao;
    }

    public void setConvencao(Convencao convencao) {
        this.convencao = convencao;
    }

    public CnaeConvencao getCnaeConvencao() {
        return cnaeConvencao;
    }

    public void setCnaeConvencao(CnaeConvencao cnaeConvencao) {
        this.cnaeConvencao = cnaeConvencao;
    }

    public String getCnaeContribuinte() {
        return cnaeContribuinte;
    }

    public void setCnaeContribuinte(String cnaeContribuinte) {
        this.cnaeContribuinte = cnaeContribuinte;
    }

    public String abrirPDFConvencao() {
//        ImprimirBoleto imp = new ImprimirBoleto();
//        ConvencaoCidade conv = new ConvencaoCidade();
        ConvencaoCidadeDao db = new ConvencaoCidadeDao();
        ConvencaoCidade conv = db.pesquisarConvencao(convencao.getId(), gruCids.getId());
        try {
            if (conv != null) {
                if (conv.getCaminho() != null || !conv.getCaminho().isEmpty()) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/convencao/" + conv.getCaminho() + ".pdf");
                    File fl = new File(caminho);
                    if (fl.exists()) {

                        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                        response.sendRedirect("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/convencao/" + conv.getCaminho() + ".pdf");
                        //imp.visualizar(fl);
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public String getStrCnaeConvencao() {
        //CnaeConvencao cc = new CnaeConvencao();
        CnaeConvencao cc = retornarCnaeConvencao();
        if (cc != null) {
            strCnaeConvencao = cc.getConvencao().getDescricao();
            convencao = cc.getConvencao();
        } else {
            strCnaeConvencao = "";
            convencao = new Convencao();
        }
        return strCnaeConvencao;
    }

    public void setStrCnaeConvencao(String strCnaeConvencao) {
        this.strCnaeConvencao = strCnaeConvencao;
    }

    public ContribuintesInativos getContribuintesInativos() {
        return contribuintesInativos;
    }

    public void setContribuintesInativos(ContribuintesInativos contribuintesInativos) {
        this.contribuintesInativos = contribuintesInativos;
    }

    public String getStrSimpleEndereco() {
        if (juridica.getId() == -1) {
            strSimpleEndereco = "Adicionar Endereço";
        } else {
            strSimpleEndereco = "Mais Endereço";
        }
        return strSimpleEndereco;
    }

    public void setStrSimpleEndereco(String strSimpleEndereco) {
        this.strSimpleEndereco = strSimpleEndereco;
    }

    public boolean getRenNovoEndereco() {
        return renNovoEndereco;
    }

    public void setRenNovoEndereco(boolean renNovoEndereco) {
        this.renNovoEndereco = renNovoEndereco;
    }

    public boolean getRenEndereco() {
        return renEndereco;
    }

    public void setRenEndereco(boolean renEndereco) {
        this.renEndereco = renEndereco;
    }

//    public boolean getChkEndContabilidade() {
//        return chkEndContabilidade;
//    }
//
//    public void setChkEndContabilidade(boolean chkEndContabilidade) {
//        this.chkEndContabilidade = chkEndContabilidade;
//    }
//
//    public String getRenChkEndereco() {
//        if (renChkEndereco.equals("false")) {
//            chkEndContabilidade = false;
//        }
//        return renChkEndereco;
//    }
    public void setRenChkEndereco(String renChkEndereco) {
        this.renChkEndereco = renChkEndereco;
    }

    public String getColorContri() {
        return colorContri;
    }

    public void setColorContri(String colorContri) {
        this.colorContri = colorContri;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public void setHabServContabil(boolean habServContabil) {
        this.habServContabil = habServContabil;
    }

    public void setListaTipoDocumento(List<SelectItem> listaTipoDocumento) {
        this.setListaTipoDocumento(listaTipoDocumento);
    }

    public EnvioEmails getEnvioEmails() {
        return envioEmails;
    }

    public void setEnvioEmails(EnvioEmails envioEmails) {
        this.envioEmails = envioEmails;
    }

    public String getAtualiza() {
        if (atualiza.isEmpty()) {
            atualiza = "Agora ";
        } else {
            atualiza += "Funcionou!!";
        }
        return atualiza;
    }

    public void setAtualiza(String atualiza) {
        this.atualiza = atualiza;
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
        listaJuridica.clear();
        offset = result;
        loadList(offset);
    }

    public void loadList() {
        limit = 500;
        offset = 0;
        JuridicaDao db = new JuridicaDao();
        boolean somenteContabilidadesx = false;
        boolean somenteContribuintesAtivos = false;
        switch (tipoFiltro) {
            case "escritorios":
                somenteContabilidadesx = true;
                break;
            case "contribuintes_ativos":
                somenteContribuintesAtivos = true;
                break;
        }
        List list = db.pesquisaPessoa(descPesquisa.trim(), porPesquisa, comoPesquisa, somenteContabilidadesx, somenteContribuintesAtivos, null, null);
        if (!list.isEmpty()) {
            try {
                count = Integer.parseInt(((List) list.get(0)).get(0).toString());
            } catch (Exception e) {
                count = 0;
            }
        }
        loadList(0);
    }

    public void loadList(Integer new_offset) {
        JuridicaDao db = new JuridicaDao();
        boolean somenteContabilidadesx = false;
        boolean somenteContribuintesAtivos = false;
        switch (tipoFiltro) {
            case "escritorios":
                somenteContabilidadesx = true;
                break;
            case "contribuintes_ativos":
                somenteContribuintesAtivos = true;
                break;
        }
        listaJuridica = db.pesquisaPessoa(descPesquisa.trim(), porPesquisa, comoPesquisa, somenteContabilidadesx, somenteContribuintesAtivos, limit, new_offset);
    }

    public List<Juridica> getListaJuridica() {
        return listaJuridica;
    }

    public String status(Juridica j) {
        String status;
        JuridicaDao db = new JuridicaDao();
        List listax = db.listaJuridicaContribuinte(j.getId());
        if (listax.isEmpty()) {
            status = "NÃO CONTRIBUINTE";
        } else if (((List) listax.get(0)).get(11) != null) {
            status = "CONTRIBUINTE INATIVO";
        } else {
            status = "ATIVO";
        }
        return status;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public int getIdIndexCnae() {
        return idIndexCnae;
    }

    public void setIdIndexCnae(int idIndexCnae) {
        this.idIndexCnae = idIndexCnae;
    }

    public int getIdIndexEndereco() {
        return idIndexEndereco;
    }

    public void setIdIndexEndereco(int idIndexEndereco) {
        this.idIndexEndereco = idIndexEndereco;
    }

    public int getIdIndexInativacao() {
        return idIndexInativacao;
    }

    public void setIdIndexInativacao(int idIndexInativacao) {
        this.idIndexInativacao = idIndexInativacao;
    }

    public boolean isRenderAtivoInativo() {
        return renderAtivoInativo;
    }

    public void setRenderAtivoInativo(boolean renderAtivoInativo) {
        this.renderAtivoInativo = renderAtivoInativo;
    }

//    public Juridica getContabilidade() {
//        return contabilidade;
//    }
//
//    public void setContabilidade(Juridica contabilidade) {
//        this.contabilidade = contabilidade;
//    }
    public int getIdIndexContabilidade() {
        return idIndexContabilidade;
    }

    public void setIdIndexContabilidade(int idIndexContabilidade) {
        this.idIndexContabilidade = idIndexContabilidade;
    }

    public List<Juridica> getListaContabilidade() {
        return listaContabilidade;
    }

    public void setListaContabilidade(List<Juridica> listaContabilidade) {
        this.listaContabilidade = listaContabilidade;
    }

    public int getIdIndexPertencente() {
        return idIndexPertencente;
    }

    public void setIdIndexPertencente(int idIndexPertencente) {
        this.idIndexPertencente = idIndexPertencente;
    }

    public String getStrArquivo() {
        //ConvencaoCidade conv = new ConvencaoCidade();
        ConvencaoCidadeDao db = new ConvencaoCidadeDao();
        ConvencaoCidade conv = db.pesquisarConvencao(convencao.getId(), gruCids.getId());
        if (!strGrupoCidade.isEmpty()) {
            if (conv != null) {
                FacesContext context = FacesContext.getCurrentInstance();
                String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/convencao/" + conv.getCaminho() + ".pdf");
                File fl = new File(caminho);
                if (fl.exists()) {
                    strArquivo = "true";
                } else {
                    strArquivo = "false";
                }
            } else {
                strArquivo = "false";
            }
        } else {
            strArquivo = "false";
        }
        return strArquivo;
    }

    public void setStrArquivo(String strArquivo) {
        this.strArquivo = strArquivo;
    }

    public String getMsgDocumento() {
        return msgDocumento;
    }

    public void setMsgDocumento(String msgDocumento) {
        this.msgDocumento = msgDocumento;
    }

    public String getMaskCnae() {
        if (porPesquisaCnae.equals("cnae")) {
            maskCnae = "cnae";
        } else {
            maskCnae = "";
        }
        return maskCnae;
    }

    public void setMaskCnae(String maskCnae) {
        this.maskCnae = maskCnae;
    }

    public int getIdPorte() {
        return idPorte;
    }

    public void setIdPorte(int idPorte) {
        this.idPorte = idPorte;
    }

    public List<SelectItem> getListaPorte() {
        if (listaPorte.isEmpty()) {
            List<Porte> select = new Dao().list(new Porte());
            for (int i = 0; i < select.size(); i++) {
                listaPorte.add(new SelectItem(i,
                        select.get(i).getDescricao(),
                        Integer.toString(select.get(i).getId())));
            }
        }
        return listaPorte;
    }

    public void setListaPorte(List<SelectItem> listaPorte) {
        this.listaPorte = listaPorte;
    }

    public String limparCampoPesquisa() {
        setDescPesquisa("");
        return null;
    }

    public void limparCnae() {
        //if (juridica.getId() != -1) {
        juridica.setCnae(null);
        //}
    }

    public String getMascaraPesquisaJuridica() {
        return Mask.getMascaraPesquisa(porPesquisa, true);
    }

    public void existeOposicaoEmpresa() {
        if (juridica.getId() != -1) {
            OposicaoDao odbt = new OposicaoDao();
            listaOposicao = odbt.listaOposicaoEmpresaID(juridica.getId());
        } else {
            listaOposicao = new ArrayList();
        }
    }

    public void listenerPessoaJuridia() {
        GenericaSessao.remove("oposicaoPesquisa");
        OposicaoBean oposicaoBean = new OposicaoBean();
        oposicaoBean.setPorPesquisa("cnpj");
        oposicaoBean.setComoPesquisa("Inicial");
        oposicaoBean.setDescricaoPesquisa(juridica.getPessoa().getDocumento());
        oposicaoBean.setListaOposicaos(new ArrayList());
        oposicaoBean.getListaOposicaos();
        GenericaSessao.put("oposicaoBean", oposicaoBean);
    }

    public String getNomeContabilidade() {
        if (juridica.getContabilidade() == null) {
            nomeContabilidade = "";
        }
        return nomeContabilidade;
    }

    public void setNomeContabilidade(String nomeContabilidade) {
        this.nomeContabilidade = nomeContabilidade;
    }

    public String getNomePesquisaContabilidade() {
        return nomePesquisaContabilidade;
    }

    public void setNomePesquisaContabilidade(String nomePesquisaContabilidade) {
        this.nomePesquisaContabilidade = nomePesquisaContabilidade;
    }

    public List<Oposicao> getListaOposicao() {
        return listaOposicao;
    }

    public void setListaOposicao(List<Oposicao> listaOposicao) {
        this.listaOposicao = listaOposicao;
    }

    public boolean isSomenteAtivas() {
        return somenteAtivas;
    }

    public void setSomenteAtivas(boolean somenteAtivas) {
        this.somenteAtivas = somenteAtivas;
    }

    public boolean isSomenteContabilidades() {
        return somenteContabilidades;
    }

    public void setSomenteContabilidades(boolean somenteContabilidades) {
        this.somenteContabilidades = somenteContabilidades;
    }

    public void pesquisaTodosEAtivos() {
        GenericaSessao.remove("juridicaBean");
        JuridicaBean juridicaBean = new JuridicaBean();
        juridicaBean.setSomenteAtivas(true);
        juridicaBean.getDisabled();
        Boolean[] bs = new Boolean[3];
        bs[0] = false;
        bs[1] = true;
        bs[2] = false;
        juridicaBean.setDisabled(bs);
        juridicaBean.setTipoFiltro("contribuintes_ativos");
        juridicaBean.loadList();
        GenericaSessao.put("juridicaBean", juridicaBean);
        GenericaSessao.put("tipoPesquisaPessoaJuridica", "todosecontribuintes");
    }

    public void pesquisaSomenteAtivos() {
        GenericaSessao.remove("juridicaBean");
        JuridicaBean juridicaBean = new JuridicaBean();
        juridicaBean.setSomenteAtivas(true);
        Boolean[] bs = new Boolean[3];
        bs[0] = false;
        bs[1] = true;
        bs[2] = true;
        juridicaBean.setDisabled(bs);
        juridicaBean.setTipoFiltro("contribuintes_ativos");
        juridicaBean.loadList();
        GenericaSessao.put("juridicaBean", juridicaBean);
        GenericaSessao.put("tipoPesquisaPessoaJuridica", "contribuintes");
    }

    public void pesquisaSomenteContabilidades() {
        GenericaSessao.remove("juridicaBean");
        JuridicaBean juridicaBean = new JuridicaBean();
        juridicaBean.setSomenteAtivas(true);
        Boolean[] bs = new Boolean[3];
        bs[0] = true;
        bs[1] = false;
        bs[2] = true;
        juridicaBean.setDisabled(bs);
        juridicaBean.setTipoFiltro("escritorios");
        juridicaBean.loadList();
        GenericaSessao.put("juridicaBean", juridicaBean);
        GenericaSessao.put("tipoPesquisaPessoaJuridica", "escritorios");
    }

    public String getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(String tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public Boolean[] getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean[] disabled) {
        this.disabled = disabled;
    }

    public List<ListaSociosEmpresa> getListSocios() {
        return listSocios;
    }

    public void setListSocios(List<ListaSociosEmpresa> listSocios) {
        this.listSocios = listSocios;
    }

    public void loadListSocios() {
        listSocios = new ArrayList<>();
        SociosDao sociosDao = new SociosDao();
        List list = sociosDao.pesquisaSocioPorEmpresa(juridica.getPessoa().getId());
        for (int i = 0; i < list.size(); i++) {
            Integer matricula = Integer.parseInt(AnaliseString.converteNullString(((List) list.get(i)).get(1)));
            Date filiacao = null;
            if (!((List) list.get(i)).get(3).toString().isEmpty()) {
                filiacao = (Date) ((List) list.get(i)).get(3);
            }
            Date admissao = null;
            if (!AnaliseString.converteNullString(((List) list.get(i)).get(4)).isEmpty()) {
                admissao = (Date) ((List) list.get(i)).get(4);
            }
            Boolean desconto_folha = false;
            if (!AnaliseString.converteNullString(((List) list.get(i)).get(5)).isEmpty()) {
                desconto_folha = (Boolean) ((List) list.get(i)).get(5);
            }
            listSocios.add(
                    new ListaSociosEmpresa(
                            AnaliseString.converteNullString(((List) list.get(i)).get(0)),
                            matricula,
                            AnaliseString.converteNullString(((List) list.get(i)).get(2)),
                            filiacao,
                            admissao,
                            desconto_folha
                    ));
        }
    }

    public ConfiguracaoCnpj getConfiguracaoCnpj() {
        return configuracaoCnpj;
    }

    public void setConfiguracaoCnpj(ConfiguracaoCnpj configuracaoCnpj) {
        this.configuracaoCnpj = configuracaoCnpj;
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
        if (juridica.getId() != -1) {
            Dao dao = new Dao();
            MalaDireta md = new MalaDiretaDao().findByPessoa(juridica.getPessoa().getId());
            if (idMalaDiretaGrupo != null) {
                if (habilitaMalaDireta) {
                    MalaDiretaGrupo mdg = (MalaDiretaGrupo) dao.find(new MalaDiretaGrupo(), Integer.parseInt(idMalaDiretaGrupo));
                    if (md == null) {
                        md = new MalaDireta();
                        md.setMalaDiretaGrupo(mdg);
                        md.setPessoa(juridica.getPessoa());
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
        MalaDireta md = new MalaDiretaDao().findByPessoa(juridica.getPessoa().getId());
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

    public List<Documento> getListaDocumentos() {
        return listaDocumentos;
    }

    public void setListaDocumentos(List<Documento> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    public List<LinhaArquivo> getListaArquivos() {
        return listaArquivos;
    }

    public void setListaArquivos(List<LinhaArquivo> listaArquivos) {
        this.listaArquivos = listaArquivos;
    }

    public List<TmktHistorico> getListTelemarketing() {
        if (juridica.getPessoa().getId() != -1) {
            return new TmktHistoricoDao().findByPessoa(juridica.getPessoa().getId());
        }
        return new ArrayList();
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

    public Date getDtRecadastro() {
        return dtRecadastro;
    }

    public void setDtRecadastro(Date dtRecadastro) {
        this.dtRecadastro = dtRecadastro;
    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

    public PesquisaCNPJ getPesquisaCNPJ() {
        return pesquisaCNPJ;
    }

    public void setPesquisaCNPJ(PesquisaCNPJ pesquisaCNPJ) {
        this.pesquisaCNPJ = pesquisaCNPJ;
    }

    public List<SelectItem> getListaCnaeReceita() {
        return listaCnaeReceita;
    }

    public void setListaCnaeReceita(List<SelectItem> listaCnaeReceita) {
        this.listaCnaeReceita = listaCnaeReceita;
    }

    public Integer getIdCnaeReceita() {
        return idCnaeReceita;
    }

    public void setIdCnaeReceita(Integer idCnaeReceita) {
        this.idCnaeReceita = idCnaeReceita;
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
    }

    public void sendRequest() {
        SisAutorizacoesDao sad = new SisAutorizacoesDao();
        List<SisAutorizacoes> listAutorizacoesSecundarias = new ArrayList();
        Dao dao = new Dao();
        Juridica j = (Juridica) dao.find(juridica);
        Boolean verificarPermissao = new ControleAcessoBean().verificarPermissao("autorizar", 3);
        sisAutorizacoes.setRotina(new Rotina().get());
        sisAutorizacoes.setOperador(Usuario.getUsuario());
        sisAutorizacoes.setPessoa(juridica.getPessoa());
        if (sisAutorizacoes.getMotivoSolicitacao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar o motivo da solicitação!");
            return;
        }
        if (sisAutorizacoes.getDadosAlterados().isEmpty()) {
            GenericaMensagem.warn("Validação", "O campo de alteração não pode ser vazio!");
            return;
        }
        if (sisAutorizacoes.getDadosAlterados().length() < 5) {
            GenericaMensagem.warn("Validação", "Informar um motivo válido!");
            return;
        }
        if (alterType.equals("cnpj")) {
            sisAutorizacoes.setSisAutorizacoesTipo((SisAutorizacoesTipo) dao.find(new SisAutorizacoesTipo(), 1));
            if (!new JuridicaDao().pesquisaJuridicaPorDoc(sisAutorizacoes.getDadosAlterados()).isEmpty()) {
                GenericaMensagem.warn("Validação", "Empresa já existente no Sistema!");
                return;
            }
            if (!validaTipoDocumento(Integer.parseInt(getListaTipoDocumento().get(idTipoDocumento).getDescription()), sisAutorizacoes.getDadosAlterados())) {
                GenericaMensagem.warn("Erro", "Documento Invalido!");
                return;
            }
            sisAutorizacoes.setTabela("pes_pessoa");
            sisAutorizacoes.setColuna("ds_documento");
            sisAutorizacoes.setCodigo(juridica.getPessoa().getId());
            sisAutorizacoes.setDadosOriginais(juridica.getPessoa().getDocumento());
            sisAutorizacoes.setMotivoSolicitacao("Alteração do documento: " + sisAutorizacoes.getMotivoSolicitacao());
            sisAutorizacoes.setEvento((Evento) dao.find(new Evento(), 3));
            SisAutorizacoes s2 = new SisAutorizacoes();
            s2.setOperador(sisAutorizacoes.getOperador());
            s2.setDtSolicitacao(sisAutorizacoes.getDtSolicitacao());
            s2.setHoraSolicitacao(sisAutorizacoes.getHoraSolicitacao());
            s2.setRotina(sisAutorizacoes.getRotina());
            s2.setPessoa(sisAutorizacoes.getPessoa());
            s2.setTabela(sisAutorizacoes.getTabela());
            s2.setOperador(sisAutorizacoes.getOperador());
            s2.setMotivoSolicitacao(sisAutorizacoes.getMotivoSolicitacao());
            s2.setEvento(sisAutorizacoes.getEvento());
            s2.setTabela("pes_pessoa");
            s2.setColuna("id_tipo_documento");
            s2.setCodigo(sisAutorizacoes.getCodigo());
            s2.setDadosAlterados(((SelectItem) getListaTipoDocumento().get(idTipoDocumento)).getDescription());
            s2.setSisAutorizacoesTipo(sisAutorizacoes.getSisAutorizacoesTipo());
            listAutorizacoesSecundarias.add(s2);
            if (verificarPermissao) {
                for (int o = 0; o < listaTipoDocumento.size(); o++) {
                    if (Integer.parseInt(listaTipoDocumento.get(o).getDescription()) == j.getPessoa().getTipoDocumento().getId()) {
                        idTipoDocumento = o;
                        break;
                    }
                }
            }
        } else if (alterType.equals("nome")) {
            sisAutorizacoes.setSisAutorizacoesTipo((SisAutorizacoesTipo) dao.find(new SisAutorizacoesTipo(), 1));
            if (juridica.getPessoa().getNome().equals(sisAutorizacoes.getDadosAlterados().toUpperCase())) {
                GenericaMensagem.warn("Validação", "PARA ALTERAR DEVE SE UTILIZAR OUTRO NOME!");
                return;
            }
            sisAutorizacoes.setTabela("pes_pessoa");
            sisAutorizacoes.setColuna("ds_nome");
            sisAutorizacoes.setCodigo(juridica.getPessoa().getId());
            sisAutorizacoes.setDadosOriginais(juridica.getPessoa().getNome());
            sisAutorizacoes.setDadosAlterados(sisAutorizacoes.getDadosAlterados().toUpperCase());
            sisAutorizacoes.setMotivoSolicitacao("Alteração do nome: " + sisAutorizacoes.getMotivoSolicitacao());
            sisAutorizacoes.setEvento((Evento) dao.find(new Evento(), 3));
        }
        if (sad.exists(sisAutorizacoes)) {
            GenericaMensagem.warn("Validação", "SOLICITAÇÃO JÁ REALIZADA EM ANDAMENTO!");
            return;
        }
        dao.openTransaction();
        Boolean isGestor = false;
        if (!verificarPermissao) {
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
            for (int i = 0; i < listAutorizacoesSecundarias.size(); i++) {
                listAutorizacoesSecundarias.get(i).setGestor(sisAutorizacoes.getGestor());
                listAutorizacoesSecundarias.get(i).setDtAutorizacao(sisAutorizacoes.getDtAutorizacao());
                listAutorizacoesSecundarias.get(i).setHoraAutorizacao(sisAutorizacoes.getHoraAutorizacao());
                listAutorizacoesSecundarias.get(i).setAutorizado(sisAutorizacoes.getAutorizado());
            }
        }
        if (!dao.save(sisAutorizacoes)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao enviar a solicitação!");
            return;
        }
        for (int i = 0; i < listAutorizacoesSecundarias.size(); i++) {
            listAutorizacoesSecundarias.get(i).setAutorizacoes(sisAutorizacoes);
            if (!dao.save(listAutorizacoesSecundarias.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Ao enviar a solicitação secundária!");
                return;
            }
        }
        dao.commit();
        sisAutorizacoes = new SisAutorizacoes();
        if (isGestor) {
            String antes = " ID: " + j.getId()
                    + " - Nome: " + j.getPessoa().getNome()
                    + " - Documento: " + j.getPessoa().getDocumento();
            juridica.getPessoa().setDtAtualizacao(new Date());
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("pes_pessoa");
            novoLog.setCodigo(juridica.getPessoa().getId());
            novoLog.update(antes, " ID: " + juridica.getId()
                    + " - Nome: " + juridica.getPessoa().getNome()
                    + " - Documento: " + juridica.getPessoa().getDocumento());
            juridica = (Juridica) new Dao().rebind(juridica);
            juridica.setPessoa((Pessoa) dao.rebind(juridica.getPessoa()));
            PF.update("formPessoaJuridica");
            GenericaMensagem.info("Sucesso", "DADOS ALTERADOS COM SUCESSO");
        } else {
            String ws = "client:" + ControleUsuarioBean.getCliente().toLowerCase() + ",sisautorizacoes";
            WSSocket.send(ws, "1");
            GenericaMensagem.info("Sucesso", "SOLICITAÇÃO ENVIADA");
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
        listSisAutorizacoes = new SisAutorizacoesDao().findByPessoa(juridica.getPessoa().getId());
    }

    public String getMascaraAlteracao() {
        if (alterType != null && !alterType.isEmpty()) {
            return Mask.getMascaraPesquisa(alterType, true);
        }
        return "";
    }

    public Fisica getFisicaMei() {
        return fisicaMei;
    }

    public void setFisicaMei(Fisica fisicaMei) {
        this.fisicaMei = fisicaMei;
    }

    public Boolean getNewMei() {
        return newMei;
    }

    public void setNewMei(Boolean newMei) {
        this.newMei = newMei;
    }

    public Pessoa getResponsavel() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            responsavel = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");
            GenericaSessao.remove("pessoaPesquisa");
        }
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public void loadListDataVencimento() {
        listDataVencimento = new ArrayList();
        for (int i = 1; i <= 31; i++) {
            if (diaVencimento == null || diaVencimento == 0) {
                diaVencimento = i;
            }
            listDataVencimento.add(new SelectItem(Integer.toString(i)));
        }
    }

    public List<SelectItem> getListDataVencimento() {
        return listDataVencimento;
    }

    public void setListDataVencimento(List<SelectItem> listDataVencimento) {
        this.listDataVencimento = listDataVencimento;
    }

    public Integer getIdStatusCobranca() {
        return idStatusCobranca;
    }

    public void setIdStatusCobranca(Integer idStatusCobranca) {
        this.idStatusCobranca = idStatusCobranca;
    }

    public List<SelectItem> getListStatusCobranca() {
        return listStatusCobranca;
    }

    public void setListStatusCobranca(List<SelectItem> listStatusCobranca) {
        this.listStatusCobranca = listStatusCobranca;
    }

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public void loadListStatusCobranca() {
//        loadListStatusCobranca(false);

        listStatusCobranca.clear();

        List<StatusCobranca> result = new Dao().find("StatusCobranca", new int[]{1, 2});

        for (int i = 0; i < result.size(); i++) {
            listStatusCobranca.add(new SelectItem(result.get(i).getId(), result.get(i).getDescricao(), result.get(i).getDescricao(), false));
        }

        if (configuracaoArrecadacao.getCobrancaEmail()) {
            if (!juridica.getPessoa().getEmail1().isEmpty()) {
                idStatusCobranca = 2;
                pessoaComplemento.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), 2));
            } else {
                idStatusCobranca = 1;
                pessoaComplemento.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), 1));
            }
        } else {
            idStatusCobranca = 1;
            pessoaComplemento.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), 1));
        }

    }

    public void loadListStatusCobranca(Boolean isListEscritorio) {
//        listStatusCobranca = new ArrayList();
//        List<StatusCobranca> list = new Dao().find("StatusCobranca", new int[]{1, 2});
//        if (isListEscritorio == null || isListEscritorio == false) {
//            if (juridica.getId() != -1) {
//                if (juridica.getCnae() != null && juridica.getCnae().getId() != -1) {
//                    if (juridica.getCnae().getId() == 1) {
//                        isListEscritorio = true;
//                    }
//                }
//            }
//        }
//        for (int i = 0; i < list.size(); i++) {
//            if (null != list.get(i).getId()) {
//                switch (list.get(i).getId()) {
//                    // 1 BOLETO
//                    case 1:
//                        if (isListEscritorio) {
//                            listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
//                        } else {
//                            if (juridica.getPessoa().getPessoaEndereco() != null) {
//                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
//                            } else {
//                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "Empresa sem endereço!", true));
//                            }
//                        }
//                        break;
//                    // Email
////                    case 2:
////                        if (isListEscritorio) {
////                            if (juridica.getPessoa().getPessoaEndereco() != null) {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
////                            } else {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "Escritório sem endereço", true));
////                            }
////                        } else {
////                            if (juridica.getContabilidade() != null && juridica.getContabilidade().getPessoa().getPessoaEndereco() != null) {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
////                            } else {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "Escritório sem endereço", true));
////                            }
////                        }
////                        break;
//                    // ANTIGO - 3 SEM ESCRITÓRIO, empresa COM email 
//                    // 2 EMAIL
//                    case 2:
//                        if (isListEscritorio) {
//                            listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
//                        } else {
//                            if (!juridica.getPessoa().getEmail1().isEmpty()) {
//                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
//                            } else {
//                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "Empresa sem e-mail", true));
//                            }
//                        }
//                        break;
//                    // 4 Quando vincular Escritório que tenha email
////                    case 4:
////                        if (isListEscritorio) {
////                            if (!juridica.getPessoa().getEmail1().isEmpty()) {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
////                            } else {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "Escritório sem emailmail", true));
////                            }
////                        } else {
////                            if (juridica.getContabilidade() != null && !juridica.getContabilidade().getPessoa().getEmail1().isEmpty()) {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), ""));
////                            } else {
////                                listStatusCobranca.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "Escritório sem e-mail", true));
////                            }
////                        }
////                        break;
//                    default:
//                        break;
//                }
//            }
//        }
    }

    public List loadListStatusCobranca(Integer tcase) {
//        listStatusCobranca = new ArrayList();
//        Dao dao = new Dao();
//
//        PessoaComplemento pc;
//        if (pessoaComplemento.getId() == -1) {
//            pc = pessoaComplemento;
//        } else {
//            pc = (PessoaComplemento) new Dao().find(pessoaComplemento);
//        }
//        Pessoa p = pc.getPessoa();
//        Pessoa escritorio = null;
//        if (pc.getPessoa().getJuridica().getContabilidade() != null) {
//            escritorio = (Pessoa) dao.find(new Pessoa(), pc.getPessoa().getJuridica().getContabilidade().getPessoa().getId());
//        }
//        Juridica empresa = (Juridica) dao.find(new Juridica(), pc.getPessoa().getJuridica().getId());
//        Boolean isListEscritorio = false;
//        if (empresa.getCnae() != null && empresa.getCnae().getId() != -1) {
//            if (empresa.getCnae().getId() == 1) {
//                isListEscritorio = true;
//            }
//        }
//        Boolean disabled = true;
//        StatusCobranca sc = new StatusCobranca();
//        PessoaEndereco pe = p.getPessoaEndereco();
//        if (tcase == null) {
//            // 1 SEM ESCRITÓRIO, empresa SEM email 
//            sc = (StatusCobranca) new Dao().find(new StatusCobranca(), 1);
//            if (escritorio == null && p.getEmail1().isEmpty()) {
//                if (p.getPessoaEndereco() != null) {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Empresa não possui endereço cadastrado para envio de cobrança!!!", false));
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", true));
//                }
//            } else {
//                if (p.getPessoaEndereco() != null) {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", false));
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Empresa sem endereço!!!", true));
//                }
//            }
//            // 2 Quando vincular Escritório que NÃO tenha email 
//            sc = (StatusCobranca) new Dao().find(new StatusCobranca(), 2);
//            if (escritorio != null && escritorio.getEmail1().isEmpty()) {
//                if (escritorio.getPessoaEndereco() == null) {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Escritório vínculado não possui endereço!!!", true));
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", false));
//                }
//            } else {
//                if (escritorio != null && escritorio.getPessoaEndereco() == null) {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Escritório vínculado não possui endereço!!!", true));
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", false));
//                }
//            }
//            // 3 SEM ESCRITÓRIO, empresa COM email 
//            sc = (StatusCobranca) new Dao().find(new StatusCobranca(), 3);
//            if (escritorio == null && !p.getEmail1().isEmpty()) {
//                if (ValidaDocumentos.isEmailValido(p.getEmail1())) {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", false));
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Email da empresa inválido!", true));
//                }
//            } else {
//                listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Empresa está sem email!", true));
//            }
//            // 4 Quando vincular Escritório que tenha email
//            sc = (StatusCobranca) new Dao().find(new StatusCobranca(), 4);
//            if (isListEscritorio) {
//                if (!p.getEmail1().isEmpty()) {
//                    if (ValidaDocumentos.isEmailValido(p.getEmail1())) {
//                        listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", false));
//                    } else {
//                        listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Email do escritório inválido!", true));
//                    }
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Escritório sem email!", true));
//                }
//            } else {
//                if (escritorio != null && !escritorio.getEmail1().isEmpty()) {
//                    if (ValidaDocumentos.isEmailValido(escritorio.getEmail1())) {
//                        listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "", false));
//                    } else {
//                        listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Email do escritório inválido!", true));
//                    }
//                } else {
//                    listStatusCobranca.add(new SelectItem(sc.getId(), sc.getDescricao(), "Escritório sem email!", true));
//                }
//            }
//
//            if (pc.getStatusCobranca() == null) {
//                if (configuracaoArrecadacao.getCobrancaEmail()) {
//                    for (int i = 0; i < listStatusCobranca.size(); i++) {
//                        if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 3 && !listStatusCobranca.get(i).isDisabled()) {
//                            idStatusCobranca = 3;
//                        }
//                        if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 4 && !listStatusCobranca.get(i).isDisabled()) {
//                            idStatusCobranca = 4;
//                            break;
//                        }
//                    }
//                } else {
//                    for (int i = 0; i < listStatusCobranca.size(); i++) {
//                        if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 1 && !listStatusCobranca.get(i).isDisabled()) {
//                            idStatusCobranca = 1;
//                        }
//                        if (Integer.parseInt(listStatusCobranca.get(i).getValue().toString()) == 2 && !listStatusCobranca.get(i).isDisabled()) {
//                            idStatusCobranca = 2;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
        return listStatusCobranca;
    }

    public ConfiguracaoArrecadacao getConfiguracaoArrecadacao() {
        return configuracaoArrecadacao;
    }

    public void setConfiguracaoArrecadacao(ConfiguracaoArrecadacao configuracaoArrecadacao) {
        this.configuracaoArrecadacao = configuracaoArrecadacao;
    }

    public JuridicaReceita getJuridicaReceita() {
        return juridicaReceita;
    }

    public void setJuridicaReceita(JuridicaReceita juridicaReceita) {
        this.juridicaReceita = juridicaReceita;
    }

    public void refreshSisAutorizacao(Boolean b) {
        try {
            if (b) {
                Juridica j = (Juridica) new Dao().rebind(juridica);
                juridica.getPessoa().setNome(j.getPessoa().getNome());
                juridica.getPessoa().setDocumento(j.getPessoa().getDocumento());
                juridica.getPessoa().setTipoDocumento(j.getPessoa().getTipoDocumento());
                for (int i = 0; i < listaTipoDocumento.size(); i++) {
                    if (j.getPessoa().getTipoDocumento().getId() == Integer.parseInt(listaTipoDocumento.get(i).getDescription())) {
                        idTipoDocumento = i;
                        break;
                    }
                }
                Messages.info("Sucesso", "Correção cadastral autorizada");
            } else {
                Messages.warn("Recusada", "Correção cadastral recusada! Consulte detalhes em sua requisição.");
            }
            PF.update("formPessoaJuridica");
        } catch (Exception e) {

        }

    }
}
