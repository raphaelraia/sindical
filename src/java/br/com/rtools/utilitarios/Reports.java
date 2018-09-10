package br.com.rtools.utilitarios;

import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DBExternal;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

@ManagedBean(name = "reportsBean")
@ViewScoped
public class Reports implements Serializable {

    /**
     * Diretório do arquivo
     */
    public String PATH;
    /**
     * Nome extra do arquivo
     */
    public String PART_NAME;
    /**
     * Baixar arquivo (Default true);
     */
    public Boolean DOWNLOAD;
    /**
     * Remover arquivo após gerar (Default true);
     */
    public Boolean REMOVE_FILE;
    /**
     * Uso interno
     */
    public byte[] BYTES;
    /**
     * Se o arquivo vai ter configuração com cabeçalho (SUBREPORT)
     */
    public Boolean HEADER;
    /**
     * Passar apenas os parametros do cabeçalho
     */
    public Boolean HEADER_PARAMS;
    /**
     * Impressão por folha (configurar grupo)
     */
    public Boolean BY_LEAF;
    /**
     * Nome do grupo
     */
    public String GROUP_NAME;
    /**
     * Se o arquivo é comprimido
     */
    public Boolean COMPRESS_FILE;
    /**
     * Limite da compressão do arquivo
     */
    public Integer COMPRESS_LIMIT;
    /**
     * Define a extensão do arquivo compactado
     */
    public String COMPRESS_EXTENSION;
    /**
     * Uso interno (2GB)
     */
    public int MEGABYTE;
    /**
     * Retorna o nome do arquivo gerado
     */
    public String FILE_NAME_GENERATED;
    public String CONTEXT_FILE;
    /**
     * Retorna o nome do arquivo gerado
     */
    public List LIST_FILE_GENERATED;
    /**
     * set: retrato, paisagem, recibo_sem_logo
     */
    public String TYPE;
    /**
     * Nome do arquivo subreport
     */
    public String SUBREPORT_NAME;
    /**
     * Impressão por folha (configurar grupo)
     */
    public Boolean REPORT_CONNECTION;
    /**
     * Exportar
     */
    public Boolean EXPORT_TO;
    /**
     * Exportar para tipo , default: IF (EXPORT_TO == false) default = pdf ELSE
     * IF (EXPORT_TO == true) EXPORT_TYPE = tipo definido
     */
    public String EXPORT_TYPE;
    /**
     * Campos Excel
     */
    public String EXCEL_FIELDS;
    /**
     * Não permite finalizar a compressão, para se obter a lista de arquivos
     * gerados
     */
    public Boolean NO_COMPACT;
    /**
     * Ignora uso de código único na String do nome do relaório
     */
    public Boolean IGNORE_UUID;
    /**
     * Database
     */
    public DBExternal dbe;
    /**
     * Query
     */
    public String QUERY_STRING;
    /**
     * Query Srint
     */
    public Boolean IS_QUERY_STRING;
    /**
     * Relatório Título
     */
    public String TITLE;
    /**
     * Filial Header Relatório
     */
    public Filial FILIAL;
    /**
     * Filial Header Relatório
     */
    public Boolean USER_PATH;
    /**
     * Nome do relatório
     */
    public String JASPER_NAME;
    /**
     * Local / Nome do Arquivo
     */
    public String JASPER_FILE;
    /**
     * Local / Nome do Arquivo
     */
    public Collection COLLECTION;

    public List jasperPrintList = new ArrayList();

    public Reports() {
        this.PATH = "downloads" + File.separator + "relatorios";
        this.PART_NAME = "relatorio";
        this.DOWNLOAD = true;
        this.REMOVE_FILE = true;
        this.BYTES = null;
        this.HEADER = false;
        this.HEADER_PARAMS = false;
        this.BY_LEAF = false;
        this.GROUP_NAME = "";
        this.COMPRESS_FILE = false;
        this.COMPRESS_LIMIT = 0;
        this.COMPRESS_EXTENSION = "zip";
        this.MEGABYTE = (1024 * 20560);
        this.FILE_NAME_GENERATED = "";
        this.CONTEXT_FILE = "";
        this.LIST_FILE_GENERATED = new ArrayList();
        this.TYPE = "";
        this.SUBREPORT_NAME = "";
        this.REPORT_CONNECTION = false;
        this.EXPORT_TO = false;
        this.EXPORT_TYPE = "";
        this.EXCEL_FIELDS = "";
        this.NO_COMPACT = false;
        this.USER_PATH = false;
        this.IGNORE_UUID = false;
        this.dbe = null;
        this.QUERY_STRING = "";
        this.IS_QUERY_STRING = false;
        this.TITLE = "";
        this.FILIAL = null;
        JASPER_FILE = "";
        JASPER_NAME = "";
        COLLECTION = null;
    }

    public void load() {
        JASPER_FILE = "";
        JASPER_NAME = "";
        COLLECTION = null;
        PATH = "downloads" + File.separator + "relatorios";
        PART_NAME = "relatorio";
        DOWNLOAD = true;
        REMOVE_FILE = true;
        BYTES = null;
        HEADER = false;
        HEADER_PARAMS = false;
        BY_LEAF = false;
        USER_PATH = false;
        GROUP_NAME = "";
        COMPRESS_FILE = false;
        COMPRESS_LIMIT = 0;
        COMPRESS_EXTENSION = "zip";
        MEGABYTE = (1024 * 20560);
        FILE_NAME_GENERATED = "";
        CONTEXT_FILE = "";
        LIST_FILE_GENERATED = new ArrayList();
        TYPE = "";
        SUBREPORT_NAME = "";
        REPORT_CONNECTION = false;
        EXPORT_TO = false;
        EXPORT_TYPE = "";
        EXCEL_FIELDS = "";
        NO_COMPACT = false;
        IGNORE_UUID = false;
        dbe = null;
        IS_QUERY_STRING = false;
        QUERY_STRING = "";
    }

    /**
     * Inicia uma lista de Jaspers
     */
    public void start() {
        jasperPrintList = new ArrayList();
    }

    /**
     * Adiciona um jasper a Lista
     *
     * @param fileName
     * @param c
     */
    public void add(String fileName, Collection c) {
        add(load(fileName), null, c);
    }

    /**
     * Adiciona um jasper a Lista
     *
     * @param jasperReport
     * @param c
     */
    public void add(JasperReport jasperReport, Collection c) {
        add(jasperReport, null, c);
    }

    /**
     * Adiciona um jasper a Lista
     *
     * @param fileName
     * @param map
     * @param c
     */
    public void add(String fileName, Map map, Collection c) {
        add(load(fileName), map, c);
    }

    /**
     * Adiciona um jasper a Lista
     *
     * @param jasperReport
     * @param map
     * @param c
     */
    public void add(JasperReport jasperReport, Map map, Collection c) {
        JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource((Collection) c);
        jasperPrintList.add(fillObject(jasperReport, map, dtSource));
    }

    /**
     * Imprime a lista gerada
     */
    public void finish() {
        print("download", jasperPrintList);
    }

    /**
     * *
     *
     * @param jasperName Arquivo.jasper
     * @param fileName Nome que será impresso
     * @param c Collection
     * @param media (pdf, xml, txt, html
     */
    public void printMedia(String jasperName, String fileName, Collection c, String media) {
        print(jasperName, fileName, c, null, new ArrayList(), null, media);
    }

    /**
     * Imprime a lista gerada com nome do arquivo
     *
     * @param filename
     */
    public void finish(String filename) {
        print(filename, jasperPrintList);
        jasperPrintList = new ArrayList();
    }

    public void print() {
        print(JASPER_FILE, JASPER_NAME, COLLECTION);
    }

    public void print(String jasperName, String fileName, Collection c) {
        print(jasperName, fileName, c, null);
    }

    public void print(String jasperName, String fileName, JRDataSource dataSource) {
        print(jasperName, fileName, new ArrayList(), null, new ArrayList(), dataSource, null);
    }

    public void print(String jasperName, String fileName, List list, Map parameters) {
        print(jasperName, fileName, (Collection) list, parameters);
    }

    public void print(String jasperName, String fileName, Collection c, Map parameters) {
        print(jasperName, fileName, c, parameters, new ArrayList());
    }

    /**
     * Envia uma lista com varios arquivos JASPER
     *
     * @param fileName
     * @param jasperListExport
     */
    public void print(String fileName, List<FillObject> jasperListExport) {
        print("", fileName, new ArrayList(), null, jasperListExport);
    }

    public void print(String jasperName, String fileName, Collection listCollections, Map parameters, List<FillObject> jasperListExport) throws SecurityException, IllegalArgumentException {
        print(jasperName, fileName, listCollections, parameters, jasperListExport, null, null);
    }

    public void print(String jasperName, String fileName, Collection listCollections, Map parameters, List<FillObject> jasperListExport, JRDataSource jRDataSource, String media) throws SecurityException, IllegalArgumentException {
        LIST_FILE_GENERATED = new ArrayList();
        Dao dao = new Dao();
        // Integer idUsuario = ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId();
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        //Juridica juridica = (Juridica) dao.find(new Juridica(), 1);
        byte[] bytesComparer = null;
        byte[] b = null;
        if (jasperListExport.isEmpty()) {
            if ((fileName.isEmpty() || jasperName.isEmpty() || listCollections.isEmpty()) && !IS_QUERY_STRING) {
                if (listCollections.isEmpty() && jRDataSource == null) {
                    if (parameters != null || parameters.size() > 0) {
                    } else {
                        GenericaMensagem.info("Sistema", "Erro ao criar relatório!");
                    }
                }
            }
            jasperName = jasperName.trim();
        } else if (fileName.isEmpty() && !IS_QUERY_STRING) {
            GenericaMensagem.info("Sistema", "Erro ao criar relatório!");
            return;
        }
        fileName = fileName.trim();
        fileName = fileName.replace(" ", "_");
        fileName = fileName.replace("/", "");
        fileName = fileName.replace("(", "_");
        fileName = fileName.replace(")", "_");
        fileName = fileName.replace("-", "_");
        fileName = fileName.toLowerCase();
        fileName = AnaliseString.removerAcentos(fileName);
        if (USER_PATH) {
            if (!Diretorio.criar("Arquivos" + File.separator + PATH + File.separator + "tmp" + File.separator + Usuario.getUsuario().getId() + File.separator + fileName)) {
                GenericaMensagem.info("Sistema", "Erro ao criar diretório!");
                return;
            }
        } else {
            if (!Diretorio.criar("Arquivos" + File.separator + PATH + File.separator + fileName)) {
                GenericaMensagem.info("Sistema", "Erro ao criar diretório!");
                return;
            }
        }
        // DEFINE O CABEÇALHO
        FacesContext faces = FacesContext.getCurrentInstance();
        String subreport = "";
        if (parameters == null) {
            parameters = new HashMap();
        }
        // MOEDA PARA BRASIL VALORES IREPORT PT-BR CONVERTE VALOR JASPER VALOR IREPORT VALOR
        parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));
        if (TITLE != null && !TITLE.isEmpty()) {
            parameters.put("relatorio_titulo", TITLE);
        }

        subreport = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/CABECALHO_PAISAGEM.jasper");
        if (HEADER || HEADER_PARAMS) {
            ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
            cab.init();

            Juridica juridica;
            String documentox;
            if (FILIAL != null) {
                juridica = FILIAL.getFilial();
                documentox = juridica.getPessoa().getDocumento();// ? sindicato.getPessoa().getDocumento() : ;

            } else {
                juridica = cab.getConfiguracaoArrecadacao().getFilial().getFilial();
                documentox = juridica.getPessoa().getDocumento();// ? sindicato.getPessoa().getDocumento() : ;
            }

            if (juridica.getPessoa().getDocumento().isEmpty() || juridica.getPessoa().getDocumento().equals("0")) {
                Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
                documentox = sindicato.getPessoa().getDocumento();
            }

            FILIAL = null;

            if (HEADER) {
                if (TYPE.isEmpty()) {
                    TYPE = "default";
                }
            }

            switch (TYPE) {
                case "retrato":
                    subreport = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/CABECALHO_RETRATO.jasper");
                    break;
                case "default":
                case "paisagem":
                    subreport = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/CABECALHO_PAISAGEM.jasper");
                    break;
                case "recibo_sem_logo":
                    subreport = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/CABECALHO_RECIBO_SEM_LOGO.jasper");
                    break;
                case "recibo_com_logo":
                    subreport = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/CABECALHO_RECIBO_COM_LOGO.jasper");
                    break;
                case "contabil":
                    subreport = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/CABECALHO_CONTABIL.jasper");
                    break;
                default:
                    HEADER = false;
                    break;
            }
            parameters.put("is_header", HEADER);
            if (parameters.get("sindicato_nome") == null || parameters.get("companhia_nome") == null) {
                parameters.put("sindicato_nome", juridica.getPessoa().getNome());
                parameters.put("companhia_nome", juridica.getPessoa().getNome());
            }
            if (parameters.get("sindicato_documento") == null || parameters.get("companhia_documento") == null) {
                parameters.put("sindicato_documento", documentox);
                parameters.put("companhia_documento", documentox);
            }
            if (parameters.get("sindicato_site") == null || parameters.get("companhia_site") == null) {
                parameters.put("sindicato_site", juridica.getPessoa().getSite());
                parameters.put("companhia_site", juridica.getPessoa().getSite());
            }
            if (parameters.get("sindicato_logradouro") == null || parameters.get("companhia_logradouro") == null) {
                parameters.put("sindicato_logradouro", juridica.getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao());
                parameters.put("companhia_logradouro", juridica.getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao());
            }
            if (parameters.get("sindicato_endereco") == null || parameters.get("companhia_endereco") == null) {
                parameters.put("sindicato_endereco", juridica.getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao());
                parameters.put("companhia_endereco", juridica.getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao());
            }
            if (parameters.get("sindicato_numero") == null || parameters.get("companhia_numero") == null) {
                parameters.put("sindicato_numero", juridica.getPessoa().getPessoaEndereco().getNumero());
                parameters.put("companhia_numero", juridica.getPessoa().getPessoaEndereco().getNumero());
            }
            if (parameters.get("sindicato_complemento") == null || parameters.get("companhia_complemento") == null) {
                parameters.put("sindicato_complemento", juridica.getPessoa().getPessoaEndereco().getComplemento());
                parameters.put("companhia_complemento", juridica.getPessoa().getPessoaEndereco().getComplemento());
            }
            if (parameters.get("sindicato_bairro") == null || parameters.get("companhia_bairro") == null) {
                parameters.put("sindicato_bairro", juridica.getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao());
                parameters.put("companhia_bairro", juridica.getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao());
            }
            if (parameters.get("sindicato_cidade") == null || parameters.get("companhia_cidade") == null) {
                parameters.put("sindicato_cidade", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade());
                parameters.put("companhia_cidade", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade());
            }
            if (parameters.get("sindicato_uf") == null || parameters.get("companhia_uf") == null) {
                parameters.put("sindicato_uf", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf());
                parameters.put("companhia_uf", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf());
            }
            if (parameters.get("sindicato_cep") == null || parameters.get("companhia_cep") == null) {
                parameters.put("sindicato_cep", juridica.getPessoa().getPessoaEndereco().getEndereco().getCep());
                parameters.put("companhia_cep", juridica.getPessoa().getPessoaEndereco().getEndereco().getCep());
            }
            if (parameters.get("sindicato_telefone") == null || parameters.get("companhia_telefone") == null) {
                parameters.put("sindicato_telefone", juridica.getPessoa().getTelefone1());
                parameters.put("companhia_telefone", juridica.getPessoa().getTelefone1());
            }
            if (parameters.get("sindicato_logo") == null || parameters.get("companhia_logo") == null) {
                parameters.put("sindicato_logo", ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
                parameters.put("companhia_logo", ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
            }
            if (parameters.get("sindicato_email") == null || parameters.get("companhia_email") == null) {
                parameters.put("sindicato_email", juridica.getPessoa().getEmail1());
                parameters.put("companhia_email", juridica.getPessoa().getEmail1());
            }
            if (parameters.get("operador") == null) {
                if (u != null) {
                    parameters.put("operador", u.getPessoa().getNome());
                }
            }
            // CORREÇÃO
            // parameters.put("companhia_nome", juridica.getPessoa().getNome());
            // parameters.put("companhia_documento", juridica.getPessoa().getDocumento());
            // parameters.put("companhia_site", juridica.getPessoa().getSite());
            // parameters.put("companhia_logradouro", juridica.getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao());
            // parameters.put("companhia_endereco", juridica.getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao());
            // parameters.put("companhia_numero", juridica.getPessoa().getPessoaEndereco().getNumero());
            // parameters.put("companhia_complemento", juridica.getPessoa().getPessoaEndereco().getComplemento());
            // parameters.put("companhia_bairro", juridica.getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao());
            // parameters.put("companhia_cidade", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade());
            // parameters.put("companhia_uf", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf());
            // parameters.put("companhia_cep", juridica.getPessoa().getPessoaEndereco().getEndereco().getCep());
            // parameters.put("companhia_telefone", juridica.getPessoa().getTelefone1());
            // parameters.put("companhia_logo", ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
            //
            parameters.put("template_dir", subreport);
        } else {
            parameters.put("is_header", HEADER);
        }

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        String downloadName = "";
        String mimeType = "";
        List listFilesZip = new ArrayList();
        List listTemp = new ArrayList();
        String realPath = "";
        JasperPrint print = null;
        JRBeanCollectionDataSource dtSource;

        // DEU ERRO NO MOMENTO EM QUE FOI IMPRIMIR UM RELATÓRIO PELA WEB, ONDE NÃO SE TEM Usuario
        // RETORNO NULL
        // DATA DE ALTERAÇÃO 07/05/2015
        // CHAMADO 736 - Priscila
        Pessoa p = (u != null) ? u.getPessoa() : (Pessoa) GenericaSessao.getObject("sessaoUsuarioAcessoWeb");
        Integer idPessoa = p.getId();

        // -----------------------------------------------------------------------------------------------------
        // -----------------------------------------------------------------------------------------------------
        if (PATH.isEmpty()) {
            realPath = "Cliente" + File.separator + ControleUsuarioBean.getCliente() + File.separator + "Arquivos" + File.separator;
        } else {
            realPath = "Cliente" + File.separator + ControleUsuarioBean.getCliente() + File.separator + "Arquivos" + File.separator + PATH + File.separator;
        }

        if (USER_PATH) {
            realPath += "tmp" + File.separator + Usuario.getUsuario().getId() + "" + File.separator;
        }

        realPath += fileName + File.separator;

        String dirPath = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + realPath;
        File f = new File(dirPath);
        if (!f.exists()) {
            if (PATH.isEmpty()) {
                realPath = File.separator + "Cliente" + File.separator + ControleUsuarioBean.getCliente() + File.separator + "Arquivos" + File.separator + fileName + File.separator;
            } else {
                realPath = File.separator + "Cliente" + File.separator + ControleUsuarioBean.getCliente() + File.separator + "Arquivos" + File.separator + PATH + File.separator + fileName + File.separator;
            }
            dirPath = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + realPath;
            f = new File(dirPath);
            if (!f.exists()) {

            }
        }
        if (!PART_NAME.isEmpty()) {
            PART_NAME = PART_NAME.trim();
            PART_NAME = PART_NAME.toLowerCase();
            PART_NAME = PART_NAME.replace("-", "_");
            PART_NAME = PART_NAME.replace(" ", "_");
            PART_NAME = PART_NAME.replace("/", "");
            PART_NAME = AnaliseString.removerAcentos(PART_NAME);
            PART_NAME = "_" + PART_NAME;
        }
        UUID uuidX = UUID.randomUUID();
        String uuid = "_" + uuidX.toString().replace("-", "_");
        if (IGNORE_UUID) {
            uuid = "";
        }
        DBExternal con = new DBExternal();

        try {
            try {
                byte[] bytes;
                JasperReport subJasper;
                if (!SUBREPORT_NAME.isEmpty()) {
                    if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/ " + SUBREPORT_NAME)).exists()) {
                        subJasper = (JasperReport) JRLoader.loadObject(new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/ " + SUBREPORT_NAME)));
                    } else {
                        subJasper = (JasperReport) JRLoader.loadObject(new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/" + SUBREPORT_NAME)));
                    }
                }
                if (REPORT_CONNECTION) {
                    if (dbe != null) {
                        con = dbe;
                    } else {
                        con.setDatabase(GenericaSessao.getString("sessaoCliente"));
                    }
                    if (new File(subreport).exists()) {
                        parameters.put("REPORT_CONNECTION", con.getConnection());
                    }
                }
                JasperReport jasper = null;
                String jasper_path = "";
                if (!jasperName.contains(".jasper") && !jasperName.contains(".jrxml")) {
                    jasperName += ".jasper";
                }
                if (jasperListExport.isEmpty()) {
                    try {
                        if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + jasperName)).exists()) {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + jasperName);
                            jasper = (JasperReport) JRLoader.loadObject(new File(jasper_path));
                        } else if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "" + jasperName)).exists()) {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "" + jasperName);
                            jasper = (JasperReport) JRLoader.loadObject(new File(jasper_path));
                        } else if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/" + jasperName)).exists()) {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/" + jasperName);
                            jasper = (JasperReport) JRLoader.loadObject(new File(jasper_path));
                        } else {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath(jasperName);
                            jasper = (JasperReport) JRLoader.loadObject(new File(jasper_path));
                        }
                        if (!new File(jasper_path).exists()) {
                            GenericaMensagem.warn("Sistema", "Arquivo não encontrado:" + jasperName);
                            return;
                        }
                    } catch (Exception e) {
                        String jasperNameJrxml = jasperName.replace(".jasper", ".jrxml");
                        if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + jasperNameJrxml)).exists()) {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + jasperNameJrxml);
                        } else if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "" + jasperNameJrxml)).exists()) {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "" + jasperNameJrxml);
                        } else if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/" + jasperNameJrxml)).exists()) {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/" + jasperNameJrxml);
                        } else {
                            jasper_path = ((ServletContext) faces.getExternalContext().getContext()).getRealPath(jasperNameJrxml);
                        }
                        if (!new File(jasper_path).exists()) {
                            GenericaMensagem.warn("Sistema", "Arquivo não encontrado:" + jasperName);
                            return;
                        }
                    }
                    if (!GROUP_NAME.isEmpty()) {
                        JRGroup[] jRGroups = jasper.getGroups();
                        for (int i = 0; i < jasper.getGroups().length; i++) {
                            if (jRGroups[i].getName().equals(GROUP_NAME)) {
                                if (BY_LEAF) {
                                    ((JRGroup) jasper.getGroups()[i]).setStartNewPage(true);
                                } else {
                                    ((JRGroup) jasper.getGroups()[i]).setStartNewPage(false);
                                }
                                break;
                            }
                        }
                    }
                }
                Integer size = 0;
                //Collection[] collections = new Collection[size];
                if (COMPRESS_FILE && COMPRESS_LIMIT > 0) {
                    if (listCollections.size() > COMPRESS_LIMIT) {
                        size = listCollections.size() / COMPRESS_LIMIT;
                        size = (int) Math.ceil((double) size);
                        size += 1;
                    } else {
                        COMPRESS_FILE = false;
                    }
                }

                try {
                    List<JasperPrint> listJasper = new ArrayList();
                    if (jasperListExport.isEmpty()) {
                        jasper.setProperty(fileName, PATH);
                        if (IS_QUERY_STRING) {
                            if (!QUERY_STRING.isEmpty()) {
                                String jasper_jrxml = jasper_path.replace(".jasper", ".jrxml");
                                //JRDesignQuery query = new JRDesignQuery();
                                JasperDesign jasperDesign = JRXmlLoader.load(jasper_jrxml);
                                // update the data query
                                JRDesignQuery jRDesignQuery = new JRDesignQuery();
                                jRDesignQuery.setText(QUERY_STRING);
                                jasperDesign.setQuery(jRDesignQuery);
                                jasper = JasperCompileManager.compileReport(jasperDesign);
                                if (con != null) {
                                    parameters.put("REPORT_CONNECTION", con.getConnection());
                                    print = JasperFillManager.fillReport(jasper, parameters);
                                    listJasper.add(print);
                                }
                            }
                        } else {
                            if (jRDataSource != null) {
                                print = JasperFillManager.fillReport(jasper, parameters, jRDataSource);
                                listJasper.add(print);
                            } else {
                                dtSource = new JRBeanCollectionDataSource(listCollections);
                                print = JasperFillManager.fillReport(jasper, parameters, dtSource);
                            }
                            listJasper.add(print);
                        }
                    } else {
                        for (FillObject fo : jasperListExport) {
                            if (fo.getParameters() == null) {
                                fo.setParameters(parameters);
                            } else {
                                fo.getParameters().putAll(parameters);
                            }
                            if (fo.getJasperReport() == null) {
                                System.out.println("Erro > Jasper não encontrado (null)");
                                GenericaMensagem.info("Erro", "Jasper não encontrado (null)");
                            }
                            print = JasperFillManager.fillReport(fo.getJasperReport(), fo.getParameters(), fo.getDataSource());
                            listJasper.add(print);
                        }
                    }

                    Boolean export_to = false;
                    String export_type = "";
                    if (media != null && !media.isEmpty()) {
                        export_to = true;
                        export_type = media;
                    } else {
                        export_to = EXPORT_TO;
                        export_type = EXPORT_TYPE;
                    }

                    if (export_to && !export_type.equals("pdf") && !export_type.isEmpty()) {
                        downloadName = fileName + PART_NAME + uuid + "." + export_type;
                        String fileString = dirPath + File.separator + downloadName;
                        CONTEXT_FILE = realPath + downloadName;
                        File file = new File(fileString);
                        switch (export_type) {
                            case "xls": {
                                mimeType = "application/xls";
                                JRXlsExporter exporter = new JRXlsExporter();
                                exporter.setExporterInput(SimpleExporterInput.getInstance(listJasper));
                                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
                                SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                                configuration.setIgnoreGraphics(true);
                                exporter.setConfiguration(configuration);
                                exporter.exportReport();
                                break;
                            }
                            case "docx": {
                                mimeType = "application/xls";
                                JRDocxExporter exporter = new JRDocxExporter();
                                exporter.setExporterInput(SimpleExporterInput.getInstance(listJasper));
                                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
                                SimpleDocxReportConfiguration configuration = new SimpleDocxReportConfiguration();
                                exporter.setConfiguration(configuration);
                                exporter.exportReport();
                                break;
                            }
                            case "html":
                                JasperExportManager.exportReportToHtmlFile(print, fileString);
                                break;
                            case "text": {
                                mimeType = "text/plain";
                                JRCsvDataSource dataSource = new JRCsvDataSource(JRLoader.getLocationInputStream(downloadName));
                                dataSource.setRecordDelimiter("\r\n");
                                dataSource.setUseFirstRowAsHeader(true);
                                JRTextExporter exporter = new JRTextExporter();
                                exporter.setParameter(JRTextExporterParameter.PAGE_WIDTH, 80);
                                exporter.setParameter(JRTextExporterParameter.PAGE_HEIGHT, 40);
                                exporter.setParameter(JRExporterParameter.JASPER_PRINT, listJasper);
                                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, file.getPath());
                                exporter.exportReport();
                                break;
                            }
                            case "json":
                                break;
                            case "odt":
                                break;
                            case "rtf":
                                break;
                            case "pptx":
                                break;
                            case "xml":
                                JasperExportManager.exportReportToXmlFile(print, fileString, false);
                                break;
                            default:
                                break;
                        }
                    } else {
                        downloadName = fileName + PART_NAME + uuid + ".pdf";
                        CONTEXT_FILE = realPath + downloadName;
                        File file = new File(dirPath + File.separator + downloadName);
                        mimeType = "application/pdf";

                        JRPdfExporter exporter = new JRPdfExporter();
                        exporter.setExporterInput(SimpleExporterInput.getInstance(listJasper));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
                        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                        configuration.setCreatingBatchModeBookmarks(true);
                        exporter.setConfiguration(configuration);
                        exporter.exportReport();
                    }
                } catch (JRException e) {
                    System.out.println(e);
                    DOWNLOAD = false;
                    COMPRESS_FILE = false;
                    GenericaMensagem.warn("Erro de sistema", e.getMessage());
                    return;
                } catch (OutOfMemoryError e) {
                    DOWNLOAD = false;
                    COMPRESS_FILE = false;
                    MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
                    long maxMemory = heapUsage.getMax() / MEGABYTE;
                    long usedMemory = heapUsage.getUsed() / MEGABYTE;
                    System.out.println("Memória > Tamanho do arquivo não suporta o formato PDF, tente novamente baixando o mesmo compactado. Memória usada: " + usedMemory + "M/" + maxMemory + "M");
                    GenericaMensagem.info("Servidor > Memória", "Tamanho do arquivo não suporta o formato PDF, tente novamente baixando o mesmo compactado. Memória usada: " + usedMemory + "M/" + maxMemory + "M");
                }

                if (COMPRESS_FILE) {
                    if (COMPRESS_EXTENSION.equals("zip")) {
                        mimeType = "application/zip, application/octet-stream";
                    } else {
                        mimeType = "application/x-rar-compressed, application/octet-stream";
                    }
                    Compact.OUT_FILE = fileName + PART_NAME + uuid + "." + COMPRESS_EXTENSION;
                    Compact.PATH_OUT_FILE = realPath;
                    try {
                        listFilesZip.add(dirPath + "/" + downloadName);
                        Compact.toZip(fileName + PART_NAME + uuid + "." + COMPRESS_EXTENSION, dirPath + "/" + downloadName);
                        downloadName = fileName + PART_NAME + uuid + "." + COMPRESS_EXTENSION;
                    } catch (IOException e) {
                        GenericaMensagem.warn("Erro de sistema", e.getMessage());
                    }
                }

            } catch (JRException erro) {
                GenericaMensagem.info("Sistema", "O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
                System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
                DOWNLOAD = false;
            }
        } catch (OutOfMemoryError e) {
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            long maxMemory = heapUsage.getMax() / MEGABYTE;
            long usedMemory = heapUsage.getUsed() / MEGABYTE;
            System.out.println("Memória > Tamanho do arquivo não suporta o formato PDF, tente novamente baixando o mesmo compactado. Memória usada: " + usedMemory + "M/" + maxMemory + "M");
            GenericaMensagem.info("Servidor > Memória", "Tamanho do arquivo não suporta o formato PDF, tente novamente baixando o mesmo compactado. Memória usada: " + usedMemory + "M/" + maxMemory + "M");
            DOWNLOAD = false;
        }
        //}
        if (DOWNLOAD) {
            Download download = new Download(downloadName, dirPath, mimeType, FacesContext.getCurrentInstance());
            download.baixar();
            FILE_NAME_GENERATED = dirPath + downloadName;
            if (REMOVE_FILE) {
                download.remover();
            }
            if (!listFilesZip.isEmpty()) {
                for (int i = 0; i < listFilesZip.size(); i++) {
                    f = new File(listFilesZip.get(i).toString());
                    f.delete();
                }
            }
        } else {
            FILE_NAME_GENERATED = dirPath + downloadName;
        }
        dbe = null;

        clear();
    }

    public void clear() {
        PATH = "downloads/relatorios";
        PART_NAME = "relatorio";
        DOWNLOAD = true;
        REMOVE_FILE = true;
        BYTES = null;
        HEADER = false;
        // IMPRIME POR FOLHA
        BY_LEAF = false;
        GROUP_NAME = "";
        COMPRESS_FILE = false;
        COMPRESS_LIMIT = 0;
        COMPRESS_EXTENSION = "zip";
        SUBREPORT_NAME = "";
        REPORT_CONNECTION = false;
        NO_COMPACT = false;
        EXCEL_FIELDS = "";
        IGNORE_UUID = false;
        QUERY_STRING = "";
        IS_QUERY_STRING = false;
    }

    public void deleteFile() {
        try {
            File f = new File(FILE_NAME_GENERATED);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {

        }
        REMOVE_FILE = true;
        DOWNLOAD = true;
        FILE_NAME_GENERATED = "";
    }

    public String classAnnotationValue(Class classType, Class annotationType, String attributeName) {
        String value = null;

        Annotation annotation = classType.getAnnotation(annotationType);
        if (annotation != null) {
            try {
                value = (String) annotation.annotationType().getMethod(attributeName).invoke(annotation);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            }
        }

        return value;
    }

    public String getFILE_NAME_GENERATED() {
        return FILE_NAME_GENERATED;
    }

    public void setFILE_NAME_GENERATED(String aFILE_NAME_GENERATED) {
        FILE_NAME_GENERATED = aFILE_NAME_GENERATED;
    }

    public Boolean getEXPORT_TO() {
        return EXPORT_TO;
    }

    public void setEXPORT_TO(Boolean aEXPORT_TO_EXCEL) {
        EXPORT_TO = aEXPORT_TO_EXCEL;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String aTYPE) {
        TYPE = aTYPE;
    }

    /**
     * Retorna todos os tipos
     *
     * @return
     */
    public List<SelectItem> getListDefaultTypes() {
        return listTypes("");
    }

    /**
     * Definir tipo da combo, separados por vírgula. Ex. pdf,excel,txt,xml (sem
     * espaços)
     *
     * @param types (Extensões)
     * @return
     */
    public List<SelectItem> listTypes(String types) {
        List list = new ArrayList();
        if (types == null || types.isEmpty()) {
            list.add(new SelectItem("pdf", "PDF", "pdf", false));
            list.add(new SelectItem("xls", "Excel", "xls", false));
            list.add(new SelectItem("docx", "Word", "docx", false));
            list.add(new SelectItem("txt", "Texto", "txt", true));
            list.add(new SelectItem("xml", "XML", "xml", false));
            list.add(new SelectItem("ppt", "Power Point", "ppt", true));
            list.add(new SelectItem("json", "JSON", "json", true));
            return list;
        }
        String[] t = types.split(",");
        try {
            for (int i = 0; i < t.length; i++) {
                switch (t[i]) {
                    case "pdf":
                        list.add(new SelectItem("pdf", "PDF", "pdf", false));
                        break;
                    case "xls":
                        list.add(new SelectItem("xls", "Excel", "xls", false));
                        break;
                    case "docx":
                        list.add(new SelectItem("docx", "Word", "docx", false));
                        break;
                    case "txt":
                        list.add(new SelectItem("txt", "Texto", "txt", true));
                        break;
                    case "xml":
                        list.add(new SelectItem("xml", "XML", "xml", false));
                        break;
                    case "ppt":
                        list.add(new SelectItem("ppt", "Power Point", "ppt", true));
                        break;
                    case "json":
                        list.add(new SelectItem("json", "JSON", "json", true));
                        break;
                }
            }
        } catch (Exception e) {
            list.add(new SelectItem("pdf", "PDF", "pdf", false));
            list.add(new SelectItem("xls", "Excel", "xls", false));
            list.add(new SelectItem("docx", "Word", "docx", false));
            list.add(new SelectItem("txt", "Texto", "txt", true));
            list.add(new SelectItem("xml", "XML", "xml", false));
            list.add(new SelectItem("ppt", "Power Point", "ppt", true));
            list.add(new SelectItem("json", "JSON", "json", true));
        }
        return list;
    }

    /**
     * *
     * Carrega um objeto JasperReport nos caminhos já predefinidos no sistema,
     * cliente ou personalizado
     *
     * @param filename
     * @return
     */
    public JasperReport load(String filename) {
        try {
            return (JasperReport) JRLoader.loadObject(new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/" + filename)));
        } catch (Exception e1) {
            try {
                return (JasperReport) JRLoader.loadObject(new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + filename)));
            } catch (Exception e2) {

            }
        }
        return null;
    }

    /**
     * Cria uma lista tipo fill, para adicionar varios Jaspers diferentes a uma
     * lista genérica
     *
     * @param jasperReport
     * @param parameters
     * @param dataSource
     * @return
     */
    public FillObject fillObject(JasperReport jasperReport, Map<String, Object> parameters, JRDataSource dataSource) {
        return new FillObject(jasperReport, parameters, dataSource);
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }

    public String getPART_NAME() {
        return PART_NAME;
    }

    public void setPART_NAME(String PART_NAME) {
        this.PART_NAME = PART_NAME;
    }

    public String getGROUP_NAME() {
        return GROUP_NAME;
    }

    public void setGROUP_NAME(String GROUP_NAME) {
        this.GROUP_NAME = GROUP_NAME;
    }

    public Integer getCOMPRESS_LIMIT() {
        return COMPRESS_LIMIT;
    }

    public void setCOMPRESS_LIMIT(Integer COMPRESS_LIMIT) {
        this.COMPRESS_LIMIT = COMPRESS_LIMIT;
    }

    public int getMEGABYTE() {
        return MEGABYTE;
    }

    public void setMEGABYTE(int MEGABYTE) {
        this.MEGABYTE = MEGABYTE;
    }

    public String getCONTEXT_FILE() {
        return CONTEXT_FILE;
    }

    public void setCONTEXT_FILE(String CONTEXT_FILE) {
        this.CONTEXT_FILE = CONTEXT_FILE;
    }

    public List getLIST_FILE_GENERATED() {
        return LIST_FILE_GENERATED;
    }

    public void setLIST_FILE_GENERATED(List LIST_FILE_GENERATED) {
        this.LIST_FILE_GENERATED = LIST_FILE_GENERATED;
    }

    public String getSUBREPORT_NAME() {
        return SUBREPORT_NAME;
    }

    public void setSUBREPORT_NAME(String SUBREPORT_NAME) {
        this.SUBREPORT_NAME = SUBREPORT_NAME;
    }

    public Boolean getNO_COMPACT() {
        return NO_COMPACT;
    }

    public void setNO_COMPACT(Boolean NO_COMPACT) {
        this.NO_COMPACT = NO_COMPACT;
    }

    public Boolean getIGNORE_UUID() {
        return IGNORE_UUID;
    }

    public void setIGNORE_UUID(Boolean IGNORE_UUID) {
        this.IGNORE_UUID = IGNORE_UUID;
    }

    public String getQUERY_STRING() {
        return QUERY_STRING;
    }

    public void setQUERY_STRING(String QUERY_STRING) {
        this.QUERY_STRING = QUERY_STRING;
    }

    public Boolean getIS_QUERY_STRING() {
        return IS_QUERY_STRING;
    }

    public void setIS_QUERY_STRING(Boolean IS_QUERY_STRING) {
        this.IS_QUERY_STRING = IS_QUERY_STRING;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public Filial getFILIAL() {
        return FILIAL;
    }

    public void setFILIAL(Filial FILIAL) {
        this.FILIAL = FILIAL;
    }

    public List getJasperPrintList() {
        return jasperPrintList;
    }

    public void setJasperPrintList(List jasperPrintList) {
        this.jasperPrintList = jasperPrintList;
    }

    public Boolean getDOWNLOAD() {
        return DOWNLOAD;
    }

    public void setDOWNLOAD(Boolean DOWNLOAD) {
        this.DOWNLOAD = DOWNLOAD;
    }

    public Boolean getREMOVE_FILE() {
        return REMOVE_FILE;
    }

    public void setREMOVE_FILE(Boolean REMOVE_FILE) {
        this.REMOVE_FILE = REMOVE_FILE;
    }

    public byte[] getBYTES() {
        return BYTES;
    }

    public void setBYTES(byte[] BYTES) {
        this.BYTES = BYTES;
    }

    public Boolean getHEADER() {
        return HEADER;
    }

    public void setHEADER(Boolean HEADER) {
        this.HEADER = HEADER;
    }

    public Boolean getHEADER_PARAMS() {
        return HEADER_PARAMS;
    }

    public void setHEADER_PARAMS(Boolean HEADER_PARAMS) {
        this.HEADER_PARAMS = HEADER_PARAMS;
    }

    public Boolean getBY_LEAF() {
        return BY_LEAF;
    }

    public void setBY_LEAF(Boolean BY_LEAF) {
        this.BY_LEAF = BY_LEAF;
    }

    public Boolean getCOMPRESS_FILE() {
        return COMPRESS_FILE;
    }

    public void setCOMPRESS_FILE(Boolean COMPRESS_FILE) {
        this.COMPRESS_FILE = COMPRESS_FILE;
    }

    public String getCOMPRESS_EXTENSION() {
        return COMPRESS_EXTENSION;
    }

    public void setCOMPRESS_EXTENSION(String COMPRESS_EXTENSION) {
        this.COMPRESS_EXTENSION = COMPRESS_EXTENSION;
    }

    public Boolean getREPORT_CONNECTION() {
        return REPORT_CONNECTION;
    }

    public void setREPORT_CONNECTION(Boolean REPORT_CONNECTION) {
        this.REPORT_CONNECTION = REPORT_CONNECTION;
    }

    public String getEXCEL_FIELDS() {
        return EXCEL_FIELDS;
    }

    public void setEXCEL_FIELDS(String EXCEL_FIELDS) {
        this.EXCEL_FIELDS = EXCEL_FIELDS;
    }

    public DBExternal getDbe() {
        return dbe;
    }

    public void setDbe(DBExternal dbe) {
        this.dbe = dbe;
    }

    public Boolean getUSER_PATH() {
        return USER_PATH;
    }

    public void setUSER_PATH(Boolean USER_PATH) {
        this.USER_PATH = USER_PATH;
    }

    public String getJASPER_NAME() {
        return JASPER_NAME;
    }

    public void setJASPER_NAME(String JASPER_NAME) {
        this.JASPER_NAME = JASPER_NAME;
    }

    public String getJASPER_FILE() {
        return JASPER_FILE;
    }

    public void setJASPER_FILE(String JASPER_FILE) {
        this.JASPER_FILE = JASPER_FILE;
    }

    public Collection getCOLLECTION() {
        return COLLECTION;
    }

    public void setCOLLECTION(Collection COLLECTION) {
        this.COLLECTION = COLLECTION;
    }

    public static class FillObject {

        private JasperReport jasperReport;
        private Map<String, Object> parameters;
        private JRDataSource dataSource;

        public FillObject(JasperReport jasperReport, Map<String, Object> parameters, JRDataSource dataSource) {
            this.jasperReport = jasperReport;
            this.parameters = parameters;
            this.dataSource = dataSource;
        }

        public JasperReport getJasperReport() {
            return jasperReport;
        }

        public void setJasperReport(JasperReport jasperReport) {
            this.jasperReport = jasperReport;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }

        public JRDataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(JRDataSource dataSource) {
            this.dataSource = dataSource;
        }

    }

    public String getEXPORT_TYPE() {
        return EXPORT_TYPE;
    }

    public void setEXPORT_TYPE(String aEXPORT_TYPE) {
        EXPORT_TYPE = aEXPORT_TYPE;
    }

    // USAR - ADICIONAR AO JASPER NO XML
    /**
     *
     * <parameter name="sindicato_nome" class="java.lang.String"/>
     * <parameter name="sindicato_documento" class="java.lang.String"/>
     * <parameter name="sindicato_site" class="java.lang.String"/>
     * <parameter name="sindicato_logradouro" class="java.lang.String"/>
     * <parameter name="sindicato_endereco" class="java.lang.String"/>
     * <parameter name="sindicato_numero" class="java.lang.String"/>
     * <parameter name="sindicato_complemento" class="java.lang.String"/>
     * <parameter name="sindicato_bairro" class="java.lang.String"/>
     * <parameter name="sindicato_cidade" class="java.lang.String"/>
     * <parameter name="sindicato_uf" class="java.lang.String"/>
     * <parameter name="sindicato_cep" class="java.lang.String"/>
     * <parameter name="sindicato_logo" class="java.lang.String"/>
     * <parameter name="template_dir" class="java.lang.String"/>
     */
    /**
     * <pageHeader>
     * <band height="66">
     * <subreport>
     * <reportElement stretchType="RelativeToBandHeight" x="0" y="0" width="200" height="66" isRemoveLineWhenBlank="true" uuid="f01fa284-50bd-4581-997b-00977d4362c4"/>
     * <subreportParameter name="sindicato_nome">
     * <subreportParameterExpression><![CDATA[$P{sindicato_nome}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_documento">
     * <subreportParameterExpression><![CDATA[$P{sindicato_documento}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_site">
     * <subreportParameterExpression><![CDATA[$P{sindicato_site}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_logradouro">
     * <subreportParameterExpression><![CDATA[$P{sindicato_logradouro}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_endereco">
     * <subreportParameterExpression><![CDATA[$P{sindicato_endereco}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_numero">
     * <subreportParameterExpression><![CDATA[$P{sindicato_numero}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_complemento">
     * <subreportParameterExpression><![CDATA[$P{sindicato_complemento}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_bairro">
     * <subreportParameterExpression><![CDATA[$P{sindicato_bairro}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_cidade">
     * <subreportParameterExpression><![CDATA[$P{sindicato_cidade}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_uf">
     * <subreportParameterExpression><![CDATA[$P{sindicato_uf}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_cep">
     * <subreportParameterExpression><![CDATA[$P{sindicato_cep}]]></subreportParameterExpression>
     * </subreportParameter>
     * <subreportParameter name="sindicato_logo">
     * <subreportParameterExpression><![CDATA[$P{sindicato_logo}]]></subreportParameterExpression>
     * </subreportParameter>
     * <connectionExpression><![CDATA[$P{REPORT_CONNECTION}]]></connectionExpression>
     * <subreportExpression><![CDATA[$P{template_dir}]]></subreportExpression>
     * </subreport>
     * </band>
     * </pageHeader>
     */
}
