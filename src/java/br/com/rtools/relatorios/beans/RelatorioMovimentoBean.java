package br.com.rtools.relatorios.beans;

import br.com.rtools.arrecadacao.CnaeConvencao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.ConvencaoCidade;
import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.db.ServicoRotinaDBToplink;
import br.com.rtools.impressao.ParametroMovimentos;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioContribuintesDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioMovimentosDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioProdutosDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import static br.com.rtools.utilitarios.ImpressaoParaSocios.getConverteNullString;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.SalvaArquivos;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@ManagedBean
@SessionScoped
public class RelatorioMovimentoBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List<SelectItem> listConvencao;
    private Integer idConvencao;

    private String porData;
    private String tipoData;
    private String dataInicial;
    private String dataFinal;

    private Map<String, Integer> listServicos;
    private List selectedServicos;

    private Map<String, Integer> listTipoServico;
    private List selectedTipoServico;

    private Map<String, Integer> listGrupoCidade;
    private List selectedGrupoCidade;

    private String condicao;
    private String geradosCaixa;
    private String situacao;
    private String radioContabilidade;

    private Juridica empresa;

    private List<Juridica> listContabilidade;
    private List<Juridica> selectedContabilidade;
    private List<Juridica> listContabilidadePesquisa;
    private String descricaoPesquisaContabilidade;

    private CnaeConvencao[] selectedCnaeConvencao;
    private List<CnaeConvencao> listCnaeConvencao;

    private List<Cidade> listCidadesBase;
    private List<Cidade> selectedCidadesBase;

    private Boolean cnae;
    private Boolean totaliza;
    private String valorBaixaInicial;
    private String valorBaixaFinal;

    @PostConstruct
    public void init() {
        new Jasper().init();
        situacao = "A";
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        porData = "";
        tipoData = "";
        descricaoPesquisaContabilidade = "";
        dataInicial = "";
        dataFinal = "";
        empresa = null;
        valorBaixaInicial = "";
        valorBaixaFinal = "";
        cnae = false;
        totaliza = false;
        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioMovimentoBean");
        GenericaSessao.remove("pessoaPesquisa");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        if (!lock()) {
            GenericaMensagem.warn("Mensagem", "Selecione pelo menos um filtro!");
            return;
        }
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        List listDetalhePesquisa = new ArrayList();
        sisProcesso.startQuery();
        RelatorioProdutosDao rpd = new RelatorioProdutosDao();
        Collection list = listaPesquisa();
        sisProcesso.finishQuery();
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        String detalheRelatorio = "";
        if (listDetalhePesquisa.isEmpty()) {
            detalheRelatorio += "Pesquisar todos registros!";
        } else {
            detalheRelatorio += "";
            for (int i = 0; i < listDetalhePesquisa.size(); i++) {
                if (i == 0) {
                    detalheRelatorio += "Detalhes: " + listDetalhePesquisa.get(i).toString();
                } else {
                    detalheRelatorio += "; " + listDetalhePesquisa.get(i).toString();
                }
            }
        }
        Jasper.EXPORT_TO = true;
        Jasper.TITLE = "RELATÓRIO " + r.getNome().toUpperCase();
        Jasper.TYPE = "default";
        Map map = new HashMap();
        map.put("detalhes_relatorio", detalheRelatorio);
        Jasper.printReports(r.getJasper(), r.getNome(), list, map);
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
    }

    public Collection listaPesquisa() {

        RelatorioMovimentosDao rpd = new RelatorioMovimentosDao();
        ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
        cab.init();
        Juridica sindicato = cab.getConfiguracaoArrecadacao().getFilial().getFilial();
        //Juridica sindicato = (Juridica) (new Dao()).find(new Juridica(), 1);
        PessoaEndereco endSindicato = (new PessoaEnderecoDao()).pesquisaEndPorPessoaTipo(sindicato.getPessoa().getId(), 3);

        String contabilidades = "";
        if (radioContabilidade != null) {
            if (radioContabilidade.equals("selecionado")) {
                contabilidades = inIdContabilidade();
            } else {
                contabilidades = radioContabilidade;
            }
        }
        rpd.setRelatorios(getRelatorios());
        if (idRelatorioOrdem != null) {
            RelatorioOrdem ro = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem);
            if (ro != null) {
                rpd.setRelatorioOrdem(ro);
            } else {
                ro = new RelatorioOrdem();
                ro.setQuery("" + idRelatorioOrdem);
                rpd.setRelatorioOrdem(ro);
            }
        }
        rpd.setRelatorios(getRelatorios());
        List list = rpd.find(
                condicao,
                situacao,
                inIdServicos(),
                inIdTipoServico(),
                (empresa != null ? Integer.toString(empresa.getId()) : null),
                contabilidades,
                porData,
                tipoData,
                dataInicial,
                dataFinal,
                (idConvencao != null ? Integer.toString(idConvencao) : null),
                inIdGrupoCidade(),
                inIdCidadesBase(),
                inIdCnaeConvencao(),
                valorBaixaInicial, // V.I.
                valorBaixaFinal // V.F.
        );

        Collection listaParametro = new ArrayList<>();

        if (null != idRelatorio) {
            switch (idRelatorio) {
                case 66:
                case 68:
                    // RESUMO CONTRIBUICOES - RESUMO CONTRIBUICOES ANALITICO
                    for (Object l : list) {
                        List o = (List) l;
                        listaParametro.add(
                                new ObjectMovimentosResumo(
                                        o.get(0),
                                        o.get(1),
                                        o.get(2),
                                        o.get(3),
                                        o.get(4),
                                        o.get(5)
                                )
                        );
                    }
                    break;
                case 67:
                    // RESUMO CONTRIBUICOES POR EMPRESA
                    for (Object l : list) {
                        List o = (List) l;
                        listaParametro.add(
                                new ObjectMovimentosResumo(
                                        o.get(0),
                                        o.get(1),
                                        o.get(2),
                                        o.get(3),
                                        o.get(4),
                                        o.get(5),
                                        o.get(6),
                                        o.get(7)
                                )
                        );
                    }
                    break;
                case 78:
                    // RESUMO CONTRIBUICOES POR CIDADE
                    for (Object l : list) {
                        List o = (List) l;
                        listaParametro.add(
                                new ObjectMovimentosResumo(
                                        o.get(0),
                                        o.get(1),
                                        o.get(2),
                                        o.get(3),
                                        o.get(4),
                                        o.get(5),
                                        o.get(6)
                                )
                        );
                    }
                    break;
                default:
                    for (Object l : list) {
                        List o = (List) l;
                        float valor = Float.parseFloat(getConverteNullString(o.get(6))); // VALOR ORIGINAL

                        String quitacao = "", importacao = "", usuario = "";

                        if (o.get(38) != null) {
                            quitacao = DataHoje.converteData((Date) o.get(39));
                            importacao = DataHoje.converteData((Date) o.get(40));
                            usuario = getConverteNullString(o.get(42));
                        }

                        String srepasse = getConverteNullString(o.get(48));
                        float repasse = 0;
                        if (srepasse.isEmpty()) {
                            repasse = Moeda.multiplicarValores(Float.parseFloat(getConverteNullString(o.get(47))), Moeda.divisaoValores(0, 100));
                        } else {
                            repasse = Moeda.multiplicarValores(Float.parseFloat(getConverteNullString(o.get(47))), Moeda.divisaoValores(Float.parseFloat(srepasse), 100));
                        }

                        float valorLiquido = Moeda.subtracaoValores(Moeda.subtracaoValores(Float.parseFloat(getConverteNullString(o.get(47))), Float.valueOf(Float.parseFloat(getConverteNullString(o.get(43))))), repasse);

                        listaParametro.add(
                                new ParametroMovimentos(
                                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                                        sindicato.getPessoa().getNome(),
                                        endSindicato.getEndereco().getDescricaoEndereco().getDescricao(),
                                        endSindicato.getEndereco().getLogradouro().getDescricao(),
                                        endSindicato.getNumero(),
                                        endSindicato.getComplemento(),
                                        endSindicato.getEndereco().getBairro().getDescricao(),
                                        endSindicato.getEndereco().getCep(),
                                        endSindicato.getEndereco().getCidade().getCidade(),
                                        endSindicato.getEndereco().getCidade().getUf(),
                                        sindicato.getPessoa().getTelefone1(),
                                        sindicato.getPessoa().getEmail1(),
                                        sindicato.getPessoa().getSite(),
                                        sindicato.getPessoa().getTipoDocumento().getDescricao(),
                                        sindicato.getPessoa().getDocumento(),
                                        getConverteNullInt(o.get(41)), // ID JURIDICA
                                        getConverteNullString(o.get(10)), // NOME PESSOA
                                        getConverteNullString(o.get(11)), // ENDERECO PESSOA
                                        getConverteNullString(o.get(12)), // LOGRADOURO PESSOA
                                        getConverteNullString(o.get(13)), // NUMERO ENDERECO PESSOA
                                        getConverteNullString(o.get(14)), // COMPLEMENTO PESSOA
                                        getConverteNullString(o.get(15)), // BAIRRO PESSOA
                                        getConverteNullString(o.get(16)), // CEP PESSOA
                                        getConverteNullString(o.get(17)), // CIDADE PESSOA
                                        getConverteNullString(o.get(18)), // UF PESSOA
                                        getConverteNullString(o.get(19)), // TELEFONE PESSOA
                                        getConverteNullString(o.get(20)), // EMAIL PESSOA
                                        getConverteNullString(o.get(21)), // TIPO DOC PESSOA
                                        getConverteNullString(o.get(22)), // DOCUMENTO PESSOA
                                        getConverteNullInt(o.get(23)), // ID CNAE
                                        getConverteNullString(o.get(24)), // NUMERO CNAE
                                        getConverteNullString(o.get(25)), // NOME CNAE
                                        getConverteNullInt(o.get(26)), // ID CONTABILIDADE
                                        getConverteNullString(o.get(27)), // NOME CONTABILIDADE
                                        getConverteNullString(o.get(28)), // ENDERECO CONTABIL
                                        getConverteNullString(o.get(29)), // LOGRADOURO CONTABIL
                                        getConverteNullString(o.get(30)), // NUMERO CONTABIL
                                        getConverteNullString(o.get(31)), // COMPLEMENTO CONTABIL
                                        getConverteNullString(o.get(32)), // BAIRRO CONTABIL
                                        getConverteNullString(o.get(33)), // CEP CONTABIL
                                        getConverteNullString(o.get(34)), // CIDADE CONTABIL
                                        getConverteNullString(o.get(35)), // UF CONTABIL
                                        getConverteNullString(o.get(36)), // TELEFONE CONTABIL
                                        getConverteNullString(o.get(37)), // EMAIL CONTABIL
                                        getConverteNullString(o.get(1)), // NUMERO BOLETO
                                        getConverteNullString(o.get(2)), // SERVICO
                                        getConverteNullString(o.get(3)), // TIPO SERVICO
                                        getConverteNullString(o.get(4)), // REFERENCIA
                                        DataHoje.converteData((Date) o.get(5)), // VENCIMENTO
                                        quitacao, //result.get(i).getLoteBaixa().getQuitacao(),
                                        new BigDecimal(Float.parseFloat(getConverteNullString(o.get(47)))), // VALOR BAIXA
                                        new BigDecimal(Float.parseFloat(getConverteNullString(o.get(43)))),// TAXA
                                        importacao, //result.get(i).getLoteBaixa().getImportacao(),
                                        usuario,// result.get(i).getLoteBaixa().getUsuario().getPessoa().getNome()
                                        new BigDecimal(Float.parseFloat(getConverteNullString(o.get(44)))),// MULTA,
                                        new BigDecimal(Float.parseFloat(getConverteNullString(o.get(45)))),// JUROS,
                                        new BigDecimal(Float.parseFloat(getConverteNullString(o.get(46)))),// CORRECAO,
                                        new BigDecimal(valor), // VALOR TOTAL
                                        new BigDecimal(repasse), // REPASSE
                                        new BigDecimal(valorLiquido), // VALOR LIQUIDO
                                        totaliza
                                )
                        );
                    }
                    break;
            }
        }
        return listaParametro;
    }

    public void enviarEmail() {
        if (!lock()) {
            return;
        }

        Collection collection = listaPesquisa();

        if (collection.isEmpty()) {
            GenericaMensagem.warn("Erro", "Nenhum movimento foi encontrado nesses valores");
            return;
        }

        JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(collection);
        Relatorios relatorio = getRelatorios();

        String nomeDownload = "", pathPasta = "";
        try {
            JasperPrint print = JasperFillManager.fillReport(
                    (JasperReport) JRLoader.loadObject(new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(relatorio.getJasper()))),
                    null,
                    dtSource
            );

            byte[] arquivo = JasperExportManager.exportReportToPdf(print);

            nomeDownload = "relatorio_movimentos_" + DataHoje.horaMinuto().replace(":", "") + ".pdf";
            SalvaArquivos sa = new SalvaArquivos(arquivo, nomeDownload, false);

            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/relatorios");
            sa.salvaNaPasta(pathPasta);

        } catch (Exception e) {

        }

        Registro registro = Registro.get();
        // ENVIO DE EMAIL PARA EMPRESA SELECIONADA
        if (empresa != null) {
            try {
                List<Pessoa> pessoas = new ArrayList();
                pessoas.add(empresa.getPessoa());

                String mensagem = "";
                List<File> fls = new ArrayList<>();
                if (!registro.isEnviarEmailAnexo()) {
                    mensagem = " <h5>Visualize seu relatório clicando no link abaixo</5><br /><br />"
                            + " <a href='" + registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/relatorios/" + nomeDownload + "' target='_blank'>Clique aqui para abrir relatório</a><br />";
                } else {

                    fls.add(new File(pathPasta + "/" + nomeDownload));
                    mensagem = "<h5>Baixe seu relatório Anexado neste email</5><br /><br />";
                }
                Mail mail = new Mail();
                mail.setFiles(fls);
                mail.setEmail(
                        new Email(
                                -1,
                                DataHoje.dataHoje(),
                                DataHoje.livre(new Date(), "HH:mm"),
                                (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                                new Rotina().get(),
                                null,
                                "Envio de Relatório",
                                mensagem,
                                false,
                                false
                        )
                );

                List<EmailPessoa> emailPessoas = new ArrayList<>();
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

                    GenericaMensagem.warn("Envio para EMPRESA", retorno[1]);
                } else {
                    GenericaMensagem.info("Envio para EMPRESA", retorno[0]);
                }
            } catch (Exception e) {
            }
        }
        // ENVIO DE EMAIL PARA CONTABILIDADE SELECIONADA

        if (!selectedContabilidade.isEmpty()) {
            for (int i = 0; i < selectedContabilidade.size(); i++) {
                try {
                    List<Pessoa> pessoas = new ArrayList();
                    pessoas.add(selectedContabilidade.get(i).getPessoa());

                    String mensagem = "";
                    List<File> fls = new ArrayList<>();
                    if (!registro.isEnviarEmailAnexo()) {
                        mensagem = " <h5>Visualize seu relatório clicando no link abaixo</5><br /><br />"
                                + " <a href='" + registro.getUrlPath() + "/Sindical/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/relatorios/" + nomeDownload + "' target='_blank'>Clique aqui para abrir relatório</a><br />";
                    } else {
                        fls.add(new File(pathPasta + "/" + nomeDownload));
                        mensagem = "<h5>Baixe seu relatório Anexado neste email</5><br /><br />";
                    }
                    Mail mail = new Mail();
                    mail.setFiles(fls);
                    mail.setEmail(
                            new Email(
                                    -1,
                                    DataHoje.dataHoje(),
                                    DataHoje.livre(new Date(), "HH:mm"),
                                    (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                                    new Rotina().get(),
                                    null,
                                    "Envio de Relatório",
                                    mensagem,
                                    false,
                                    false
                            )
                    );

                    List<EmailPessoa> emailPessoas = new ArrayList<>();
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
                        GenericaMensagem.warn("Envio para CONTABILIDADE", retorno[1]);
                    } else {
                        GenericaMensagem.info("Envio para CONTABILIDADE", retorno[0]);
                    }

                } catch (Exception e) {
                }
            }
        }

    }

    // LOAD
    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(110);
            }
            if (!list.isEmpty()) {
                idRelatorio = list.get(0).getId();
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    idRelatorio = list.get(i).getId();
                }
                listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
            loadRelatorioOrdem();
        }
    }

    public void loadRelatorioOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            listRelatorioOrdem.add(new SelectItem(-1, "Vencimento"));
            listRelatorioOrdem.add(new SelectItem(-2, "Quitação"));
            listRelatorioOrdem.add(new SelectItem(-3, "Importação"));
            listRelatorioOrdem.add(new SelectItem(-4, "Referência"));
            idRelatorioOrdem = -1;
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void load() {
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("empresa", "Empresa", false, false));
        listFilters.add(new Filters("contabilidade", "Contabilidade", false, false));
        listFilters.add(new Filters("convencao", "Convenção", false, false));
        listFilters.add(new Filters("servicos", "Serviços", false, false));
        listFilters.add(new Filters("tipo_servico", "Tipo de Serviço", false, false));
        listFilters.add(new Filters("cidades_base", "Cidades da Base", false, false));
        listFilters.add(new Filters("data", "Data", false, false));
        listFilters.add(new Filters("valor_baixa", "Valor Baixa", false, false));

    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadFilters();
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "empresa":
                if (!filter.getActive()) {
                    empresa = null;
                }
                break;
            case "contabilidade":
                radioContabilidade = "selecionado";
                if (!filter.getActive()) {
                    listContabilidade = new ArrayList();
                    selectedContabilidade = new ArrayList();
                    listContabilidadePesquisa = new ArrayList();
                    descricaoPesquisaContabilidade = "";
                } else {
                    listContabilidadePesquisa = new ArrayList();
                    descricaoPesquisaContabilidade = "";
                    loadListContabilidade();
                }
                break;
            case "convencao":
                if (!filter.getActive()) {
                    listConvencao = new ArrayList();
                    idConvencao = null;
                    listGrupoCidade = new LinkedHashMap<>();
                    selectedGrupoCidade = new ArrayList();
                    selectedCnaeConvencao = null;
                    listCnaeConvencao = new ArrayList();
                    cnae = false;
                } else {
                    loadListConvencao();
                    loadListGrupoCidade();
                }
                break;
            case "cidades_base":
                if (!filter.getActive()) {
                    listCidadesBase = new ArrayList();
                    selectedCidadesBase = new ArrayList();
                } else {
                    loadListCidadesBase();
                }
                break;
            case "servicos":
                if (!filter.getActive()) {
                    listServicos = new LinkedHashMap<>();
                    selectedServicos = new ArrayList<>();
                } else {
                    loadListServicos();
                }
                break;
            case "tipo_servico":
                if (!filter.getActive()) {
                    listTipoServico = new LinkedHashMap<>();
                    selectedTipoServico = new ArrayList<>();
                } else {
                    loadListTipoServico();
                }
                break;
            case "data":
                if (!filter.getActive()) {
                    porData = "";
                    tipoData = "";
                    dataInicial = "";
                    dataFinal = "";
                } else {
                    porData = "vencimento";
                    tipoData = "faixa";
                    dataInicial = "";
                    dataFinal = "";
                }
                break;
            case "valor_baixa":
                if (!filter.getActive()) {
                    valorBaixaInicial = "";
                    valorBaixaFinal = "";
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void loadListServicos() {
        listServicos = new LinkedHashMap<>();
        selectedServicos = new ArrayList<>();

        ServicoRotinaDBToplink srdao = new ServicoRotinaDBToplink();
        List<Servicos> list = srdao.pesquisaTodosServicosComRotinas(4);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listServicos.put(list.get(i).getDescricao().toUpperCase(), list.get(i).getId());

            }
        }
    }

    public void loadListTipoServico() {
        listTipoServico = new LinkedHashMap<>();
        selectedTipoServico = new ArrayList<>();
        List<TipoServico> list = new Dao().list(new TipoServico(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listTipoServico.put(list.get(i).getDescricao().toUpperCase(), list.get(i).getId());
            }
        }
    }

    public void loadListConvencao() {
        listConvencao = new ArrayList();
        List<Convencao> list = new Dao().list(new Convencao(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idConvencao = list.get(i).getId();
                }
                listConvencao.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao().toUpperCase()));
            }
        }
    }

    public void loadListGrupoCidade() {
        listGrupoCidade = new LinkedHashMap<>();
        selectedGrupoCidade = new ArrayList<>();
        ConvencaoCidadeDao convencaoCidadeDao = new ConvencaoCidadeDao();
        List<ConvencaoCidade> list = convencaoCidadeDao.pesquisaGrupoPorConvencao(idConvencao);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listGrupoCidade.put(list.get(i).getGrupoCidade().getDescricao().toUpperCase(), list.get(i).getId());
            }
        }
        loadListCnaeConvencao();
    }

    public void loadListCnaeConvencao() {
        listCnaeConvencao = new ArrayList();
        selectedCnaeConvencao = null;
        if (cnae) {
            RelatorioContribuintesDao relatorioContribuintesDao = new RelatorioContribuintesDao();
            listCnaeConvencao = relatorioContribuintesDao.pesquisarCnaeConvencaoPorConvencao("" + idConvencao);
        }
    }

    public void loadListContabilidade() {
        listContabilidade = new ArrayList<>();
        selectedContabilidade = new ArrayList<>();
        JuridicaDao juridicaDao = new JuridicaDao();
        listContabilidade = juridicaDao.pesquisaContabilidade();
    }

    public void findContabilidade() {
        if (listContabilidadePesquisa == null) {
            listContabilidadePesquisa = new ArrayList();
        }
        if (listContabilidadePesquisa.isEmpty()) {
            listContabilidadePesquisa.addAll(listContabilidade);
        }

        listContabilidade.clear();

        if (descricaoPesquisaContabilidade.length() == 0) {
            listContabilidade.addAll(listContabilidadePesquisa);
            return;
        }

        if (!listContabilidadePesquisa.isEmpty()) {
            List aux = new ArrayList();
            for (int i = 0; i < listContabilidadePesquisa.size(); i++) {
                if (listContabilidadePesquisa.get(i).getPessoa().getNome().toUpperCase().contains(descricaoPesquisaContabilidade.toUpperCase())) {
                    aux.add(listContabilidadePesquisa.get(i));
                } else if (listContabilidadePesquisa.get(i).getPessoa().getDocumento().contains(descricaoPesquisaContabilidade)) {
                    aux.add(listContabilidadePesquisa.get(i));
                }
            }

            if (!aux.isEmpty()) {
                listContabilidade.addAll(aux);
            } else {
                listContabilidade.addAll(listContabilidadePesquisa);
            }
        }

    }

    public void loadListCidadesBase() {
        listCidadesBase = new ArrayList();
        selectedCidadesBase = new ArrayList();
        GrupoCidadesDao grupoCidadesDao = new GrupoCidadesDao();
        listCidadesBase = grupoCidadesDao.pesquisaCidadesBase();
    }

    // TRATAMENTO
    public String inIdServicos() {
        String ids = null;
        if (selectedServicos != null) {
            ids = "";
            for (int i = 0; i < selectedServicos.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedServicos.get(i);
                } else {
                    ids += "," + selectedServicos.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdTipoServico() {
        String ids = null;
        if (selectedTipoServico != null) {
            ids = "";
            for (int i = 0; i < selectedTipoServico.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedTipoServico.get(i);
                } else {
                    ids += "," + selectedTipoServico.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdContabilidade() {
        String ids = null;
        if (selectedContabilidade != null) {
            ids = "";
            for (int i = 0; i < selectedContabilidade.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedContabilidade.get(i).getId();
                } else {
                    ids += "," + selectedContabilidade.get(i).getId();
                }
            }
        }
        return ids;
    }

    public String inIdCnaeConvencao() {
        String ids = null;
        if (selectedCnaeConvencao != null) {
            ids = "";
            for (int i = 0; i < selectedCnaeConvencao.length; i++) {
                if (i == 0) {
                    ids += "" + selectedCnaeConvencao[i].getCnae().getId();
                } else {
                    ids += ", " + selectedCnaeConvencao[i].getCnae().getId();
                }
            }
        }
        return ids;
    }

    public String inIdGrupoCidade() {
        String ids = null;
        if (selectedGrupoCidade != null) {
            ids = "";
            for (int i = 0; i < selectedGrupoCidade.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedGrupoCidade.get(i);
                } else {
                    ids += "," + selectedGrupoCidade.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdCidadesBase() {
        String ids = null;
        if (selectedCidadesBase != null) {
            ids = "";
            for (int i = 0; i < selectedCidadesBase.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedCidadesBase.get(i).getId();
                } else {
                    ids += "," + selectedCidadesBase.get(i).getId();
                }
            }
        }
        return ids;
    }

    // GETTERS AND SETTERS
    public List<SelectItem> getListRelatorios() {
        return listRelatorio;
    }

    public void setListRelatorios(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    /**
     * 0 grupo finançeiro; 1 subgrupo finançeiro; 2 serviços; 3 sócios; 4 tipo
     * de pessoa; 5 meses débito
     *
     * @return
     */
    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            r = rgdb.pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public void clear(String tCase) {
        switch (tCase) {
            case "empresa":
                empresa = null;
                break;
        }
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public String getGeradosCaixa() {
        return geradosCaixa;
    }

    public void setGeradosCaixa(String geradosCaixa) {
        this.geradosCaixa = geradosCaixa;
    }

    public Juridica getEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            empresa = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public List<Juridica> getListContabilidade() {
        return listContabilidade;
    }

    public void setListContabilidade(List<Juridica> listContabilidade) {
        this.listContabilidade = listContabilidade;
    }

    public Map<String, Integer> getListServicos() {
        return listServicos;
    }

    public void setListServicos(Map<String, Integer> listServicos) {
        this.listServicos = listServicos;
    }

    public List getSelectedServicos() {
        return selectedServicos;
    }

    public void setSelectedServicos(List selectedServicos) {
        this.selectedServicos = selectedServicos;
    }

    public Map<String, Integer> getListTipoServico() {
        return listTipoServico;
    }

    public void setListTipoServico(Map<String, Integer> listTipoServico) {
        this.listTipoServico = listTipoServico;
    }

    public List getSelectedTipoServico() {
        return selectedTipoServico;
    }

    public void setSelectedTipoServico(List selectedTipoServico) {
        this.selectedTipoServico = selectedTipoServico;
    }

    public String getPorData() {
        return porData;
    }

    public void setPorData(String porData) {
        this.porData = porData;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void setSelectedContabilidade(List<Juridica> selectedContabilidade) {
        this.selectedContabilidade = selectedContabilidade;
    }

    public String getRadioContabilidade() {
        return radioContabilidade;
    }

    public void setRadioContabilidade(String radioContabilidade) {
        this.radioContabilidade = radioContabilidade;
    }

    public List<Juridica> getSelectedContabilidade() {
        return selectedContabilidade;
    }

    public List<Juridica> getListContabilidadePesquisa() {
        return listContabilidadePesquisa;
    }

    public void setListContabilidadePesquisa(List<Juridica> listContabilidadePesquisa) {
        this.listContabilidadePesquisa = listContabilidadePesquisa;
    }

    public String getDescricaoPesquisaContabilidade() {
        return descricaoPesquisaContabilidade;
    }

    public void setDescricaoPesquisaContabilidade(String descricaoPesquisaContabilidade) {
        this.descricaoPesquisaContabilidade = descricaoPesquisaContabilidade;
    }

    public Map<String, Integer> getListGrupoCidade() {
        return listGrupoCidade;
    }

    public void setListGrupoCidade(Map<String, Integer> listGrupoCidade) {
        this.listGrupoCidade = listGrupoCidade;
    }

    public List getSelectedGrupoCidade() {
        return selectedGrupoCidade;
    }

    public void setSelectedGrupoCidade(List selectedGrupoCidade) {
        this.selectedGrupoCidade = selectedGrupoCidade;
    }

    public List<SelectItem> getListConvencao() {
        return listConvencao;
    }

    public void setListConvencao(List<SelectItem> listConvencao) {
        this.listConvencao = listConvencao;
    }

    public Integer getIdConvencao() {
        return idConvencao;
    }

    public void setIdConvencao(Integer idConvencao) {
        this.idConvencao = idConvencao;
    }

    public Boolean getCnae() {
        return cnae;
    }

    public void setCnae(Boolean cnae) {
        this.cnae = cnae;
    }

    public CnaeConvencao[] getSelectedCnaeConvencao() {
        return selectedCnaeConvencao;
    }

    public void setSelectedCnaeConvencao(CnaeConvencao[] selectedCnaeConvencao) {
        this.selectedCnaeConvencao = selectedCnaeConvencao;
    }

    public List<CnaeConvencao> getListCnaeConvencao() {
        return listCnaeConvencao;
    }

    public void setListCnaeConvencao(List<CnaeConvencao> listCnaeConvencao) {
        this.listCnaeConvencao = listCnaeConvencao;
    }

    public List<Cidade> getListCidadesBase() {
        return listCidadesBase;
    }

    public void setListCidadesBase(List<Cidade> listCidadesBase) {
        this.listCidadesBase = listCidadesBase;
    }

    public List<Cidade> getSelectedCidadesBase() {
        return selectedCidadesBase;
    }

    public void setSelectedCidadesBase(List<Cidade> selectedCidadesBase) {
        this.selectedCidadesBase = selectedCidadesBase;
    }

    public String getValorBaixaInicial() {
        return valorBaixaInicial;
    }

    public void setValorBaixaInicial(String valorBaixaInicial) {
        this.valorBaixaInicial = valorBaixaInicial;
    }

    public String getValorBaixaFinal() {
        return valorBaixaFinal;
    }

    public void setValorBaixaFinal(String valorBaixaFinal) {
        this.valorBaixaFinal = valorBaixaFinal;
    }

    public Boolean getTotaliza() {
        return totaliza;
    }

    public void setTotaliza(Boolean totaliza) {
        this.totaliza = totaliza;
    }

    public int getConverteNullInt(Object object) {
        if (object == null) {
            return 0;
        } else {
            return (Integer) object;
        }
    }

    public Boolean lock() {
        for (int i = 0; i < listFilters.size(); i++) {
            if (listFilters.get(i).getActive()) {
                return true;
            }
        }
        return false;
    }

    public class ObjectMovimentosResumo {

        private Object mes;
        private Object ano;
        private Object empresa_cnpj;
        private Object empresa_nome;
        private Object contribuicao;
        private Object valor;
        private Object taxa;
        private Object liquido;
        private Object cidade;

        public ObjectMovimentosResumo(Object mes, Object ano, Object contribuicao, Object valor, Object taxa, Object liquido) {
            this.mes = mes;
            this.ano = ano;
            this.contribuicao = contribuicao;
            this.valor = valor;
            this.taxa = taxa;
            this.liquido = liquido;
        }

        public ObjectMovimentosResumo(Object mes, Object ano, Object empresa_cnpj, Object empresa_nome, Object contribuicao, Object valor, Object taxa, Object liquido) {
            this.mes = mes;
            this.ano = ano;
            this.empresa_cnpj = empresa_cnpj;
            this.empresa_nome = empresa_nome;
            this.contribuicao = contribuicao;
            this.valor = valor;
            this.taxa = taxa;
            this.liquido = liquido;
        }

        public ObjectMovimentosResumo(Object cidade, Object mes, Object ano, Object contribuicao, Object valor, Object taxa, Object liquido) {
            this.cidade = cidade;
            this.mes = mes;
            this.ano = ano;
            this.contribuicao = contribuicao;
            this.valor = valor;
            this.taxa = taxa;
            this.liquido = liquido;
        }

        public Object getMes() {
            return mes;
        }

        public void setMes(Object mes) {
            this.mes = mes;
        }

        public Object getAno() {
            return ano;
        }

        public void setAno(Object ano) {
            this.ano = ano;
        }

        public Object getEmpresa_cnpj() {
            return empresa_cnpj;
        }

        public void setEmpresa_cnpj(Object empresa_cnpj) {
            this.empresa_cnpj = empresa_cnpj;
        }

        public Object getEmpresa_nome() {
            return empresa_nome;
        }

        public void setEmpresa_nome(Object empresa_nome) {
            this.empresa_nome = empresa_nome;
        }

        public Object getContribuicao() {
            return contribuicao;
        }

        public void setContribuicao(Object contribuicao) {
            this.contribuicao = contribuicao;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getTaxa() {
            return taxa;
        }

        public void setTaxa(Object taxa) {
            this.taxa = taxa;
        }

        public Object getLiquido() {
            return liquido;
        }

        public void setLiquido(Object liquido) {
            this.liquido = liquido;
        }

        public Object getCidade() {
            return cidade;
        }

        public void setCidade(Object cidade) {
            this.cidade = cidade;
        }
    }

}
