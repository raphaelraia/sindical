package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.dao.CategoriaDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.GrupoFinanceiro;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.SubGrupoFinanceiro;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.dao.FTipoDocumentoDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.impressao.ParametroSocios;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioSociosDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Mes;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Zip;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class RelatorioSociosBean implements Serializable {

    private Integer idRelatorioOrdem;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private List<SelectItem> listGroups;

    private List<DateFilters> listDateFilters;
    private List<SelectItem> listDates;
    private String selectedDate;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private String selectedGroups;

    private Map<String, Integer> listServicos;
    private List selectedServicos;

    private Map<String, Integer> listGrupoFinanceiro;
    private List selectedGrupoFinanceiro;

    private Map<String, Integer> listSubGrupoFinanceiro;
    private List selectedSubGrupoFinanceiro;

    private Map<String, Integer> listDescontoSocial;
    private List selectedDescontoSocial;

    private List<Filters> filtersSocio;
    private List<Filters> filtersEmpresa;
    private List<Filters> filtersFinanceiro;

    private Map<String, Integer> listTipoCobranca;
    private List selectedTipoCobranca;

    private Map<String, Integer> listCidadesSocio;
    private List selectedCidadesSocio;

    private Map<String, Integer> listCidadesEmpresa;
    private List selectedCidadesEmpresa;

    private Map<String, Integer> listGrupoCategoria;
    private List selectedGrupoCategoria;

    private Map<String, Integer> listCategoria;
    private List selectedCategoria;

    private Map<String, Integer> listParentesco;
    private List selectedParentesco;

    private Map<String, Integer> listMeses;
    private List selectedMeses;

    private Map<String, String> listEstadoCivil;
    private List selectedEstadoCivil;

    private Map<String, String> listAlfabeto;
    private List selectedAlfabeto;

    private String situacaoString;
    private Boolean compactar;
    private Integer carenciaDias;
    private String tipoCarencia;
    private Boolean enableFolha;
    private Boolean porFolha;
    private Juridica empresa;
    private List<Juridica> listEmpresas;
    private Pessoa socio;
    private List<Pessoa> listSocios;
    private String minQtdeFuncionario;
    private String maxQtdeFuncionario;
    private boolean ordemAniversario;
    private Boolean contemServicos;
    private String tipoEleicao;
    private String tipoSexo;
    private String tipoCarteirinha;
    private String tipoFotos;
    private String tipoDescontoGeracao;
    private String tipoEmpresas;
    private String tipoOrdem;
    private String tipoEmail;
    private String tipoTelefone;
    private String tipoEstadoCivil;
    private String tipoBiometria;
    private String tipoDescontoFolha;
    private String tipoSuspencao;
    private String tipoOposicao;
    private String statusSocio;
    private Integer idEmpresas;
    private Integer idDias;
    private String matriculaInicial;
    private String matriculaFinal;
    private String idadeInicial;
    private String idadeFinal;
    private String diaInicial;
    private String diaFinal;

    public RelatorioSociosBean() {
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        situacaoString = null;
        compactar = false;
        carenciaDias = null;
        tipoCarencia = "";
        enableFolha = false;
        porFolha = false;
        empresa = new Juridica();
        minQtdeFuncionario = null;
        maxQtdeFuncionario = null;
        ordemAniversario = false;
        contemServicos = null;
        tipoEleicao = "";
        tipoSexo = "";
        tipoCarteirinha = "";
        tipoFotos = "";
        tipoDescontoGeracao = "";
        tipoEmpresas = "";
        tipoOrdem = "";
        tipoEmail = "";
        tipoTelefone = "";
        tipoEstadoCivil = "";
        tipoBiometria = "";
        tipoDescontoFolha = "";
        tipoSuspencao = "";
        tipoOposicao = "";
        statusSocio = "";
        idEmpresas = null;
        idDias = 0;
        selectedGroups = "categoria";
        matriculaInicial = "0";
        matriculaFinal = "";
        idadeInicial = "0";
        idadeFinal = "500";
        diaInicial = "1";
        diaFinal = "31";
        loadRelatorios();
        loadRelatoriosOrdem();
        loadFilters();
        loadGroups();

    }

    public void clear() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public void listener(String tcase) {
        if (tcase.equals("reload")) {
            loadRelatoriosOrdem();
            selectedGroups = "categoria";
            if (idRelatorio == 12 || idRelatorio == 16 || idRelatorio == 40) {
                for (int i = 0; i < filtersSocio.size(); i++) {
                    if (filtersSocio.get(i).getKey().equals("status")) {
                        statusSocio = "";
                        filtersSocio.get(i).setActive(true);
                        filtersSocio.get(i).setDisabled(true);
                        break;
                    }
                }
            } else {
                for (int i = 0; i < filtersSocio.size(); i++) {
                    if (filtersSocio.get(i).getKey().equals("status")) {
                        statusSocio = "socio";
                        filtersSocio.get(i).setActive(false);
                        filtersSocio.get(i).setDisabled(true);
                        break;
                    }
                }
            }
        } else if (tcase.equals("empresas")) {
            empresa = new Juridica();
            listEmpresas = new ArrayList();
            idEmpresas = null;
            minQtdeFuncionario = "0";
            maxQtdeFuncionario = "0";
        } else if (tcase.equals("reload_relatorios")) {
            loadRelatorios(false);
            loadRelatoriosOrdem();
        }
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public final void loadFilters() {

        // SÓCIO
        filtersSocio = new ArrayList<>();
        filtersSocio.add(new Filters("socios", "Sócios", false));
        filtersSocio.add(new Filters("aniversario", "Aniversário", false));
        filtersSocio.add(new Filters("biometria", "Biometria", false));
        filtersSocio.add(new Filters("carteirinha", "Carteirinha", false));
        filtersSocio.add(new Filters("cidade_socio", "Cidade do Sócio", false));
        filtersSocio.add(new Filters("datas", "Datas", false));
        filtersSocio.add(new Filters("email", "Email", false));
        filtersSocio.add(new Filters("estado_civil", "Estado Civil", false));
        filtersSocio.add(new Filters("fotos", "Fotos", false));
        filtersSocio.add(new Filters("grau", "Grau", true));
        filtersSocio.add(new Filters("grupo_categoria", "Grupo / Categoria", false));
        filtersSocio.add(new Filters("idade", "Idade", false));
        filtersSocio.add(new Filters("numero_matricula", "Número da Matrícula", false));
        filtersSocio.add(new Filters("oposicao", "Oposição", false));
        loadParentesco();
        filtersSocio.add(new Filters("status", "Status", true));
        listener("reload");
        filtersSocio.add(new Filters("sexo", "Sexo", false));
        filtersSocio.add(new Filters("suspencao", "Suspenção", false));
        filtersSocio.add(new Filters("telefone", "Telefone", false));
        filtersSocio.add(new Filters("votante", "Votante", false));

        // EMPRESA
        filtersEmpresa = new ArrayList<>();
        /* 01 */ filtersEmpresa.add(new Filters("cidade_empresa", "Cidade da Empresa", false));
        /* 00 */ filtersEmpresa.add(new Filters("empresas", "Empresas", false));

        // FINANÇEIRO
        filtersFinanceiro = new ArrayList<>();
        /* 02 */ filtersFinanceiro.add(new Filters("desconto_folha", "Desconto em Folha", false));
        /* 03 */ filtersFinanceiro.add(new Filters("desconto_social", "Desconto Social", false));
        /* 00 */ filtersFinanceiro.add(new Filters("servicos", "Serviços", false));
        /* 14 */ filtersFinanceiro.add(new Filters("situacao", "Situação", false));
        /* 01 */ filtersFinanceiro.add(new Filters("tipo_cobranca", "Tipo de Cobrança", false));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters filters = new Filters();
        filters.setKey(filter);
        filters.setActive(false);
        for (Filters f : filtersSocio) {
            if (f.getKey().equals(filter)) {
                f.setActive(false);
            }
        }
        for (Filters f : filtersEmpresa) {
            if (f.getKey().equals(filter)) {
                f.setActive(false);
            }
        }
        for (Filters f : filtersFinanceiro) {
            if (f.getKey().equals(filter)) {
                f.setActive(false);
            }
        }
        load(filters);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {
            /**
             * SÓCIOS
             */
            case "socios":
                socio = new Pessoa();
                listSocios = new ArrayList();
                break;
            case "datas":
                listDateFilters = new ArrayList();
                listDateFilters = new ArrayList();
                listDates = new ArrayList();
                selectedDate = "";
                typeDate = "faixa";
                startDate = "";
                finishDate = "";
                if (filter.getActive()) {
                    loadDates();
                }
                break;
            case "numero_matricula":
                matriculaInicial = "";
                matriculaFinal = "";
                if (filter.getActive()) {
                    matriculaInicial = "0";
                    matriculaFinal = "9999999";
                }
                break;
            case "suspencao":
                tipoSuspencao = "";
                if (filter.getActive()) {
                    tipoSuspencao = "com";
                    loadRelatorios(false);
                } else {
                    loadRelatorios(true);
                }
                loadRelatoriosOrdem();
                break;
            case "oposicao":
                tipoOposicao = "";
                if (filter.getActive()) {
                    tipoOposicao = "com";
                }
                break;
            case "idade":
                idadeInicial = "";
                idadeFinal = "";
                if (filter.getActive()) {
                    idadeInicial = "0";
                    idadeFinal = "500";
                }
                break;
            case "sexo":
                tipoSexo = "";
                if (filter.getActive()) {
                    tipoSexo = "M";
                }
                break;
            case "email":
                tipoEmail = "";
                if (filter.getActive()) {
                    tipoEmail = "com";
                }
                break;
            case "telefone":
                tipoTelefone = "";
                if (filter.getActive()) {
                    tipoTelefone = "com";
                }
                break;
            case "biometria":
                tipoBiometria = "";
                if (filter.getActive()) {
                    tipoBiometria = "com";
                }
                break;
            case "fotos":
                tipoFotos = "";
                if (filter.getActive()) {
                    tipoFotos = "com";
                    loadAlfabeto();
                }
                break;
            case "status":
                statusSocio = "";
                if (filter.getActive()) {
                    statusSocio = "socio";
                }
                break;
            case "carteirinha":
                tipoCarteirinha = "";
                if (filter.getActive()) {
                    tipoCarteirinha = "com";
                    loadDates();
                }
                break;
            case "aniversario":
                listMeses = new LinkedHashMap<>();
                selectedMeses = new ArrayList<>();
                ordemAniversario = false;
                if (filter.getActive()) {
                    ordemAniversario = true;
                    loadMeses();
                }
                break;
            case "grupo_categoria":
                listGrupoCategoria = new LinkedHashMap<>();
                selectedGrupoCategoria = new ArrayList();
                listCategoria = new LinkedHashMap<>();
                selectedCategoria = new ArrayList();
                if (filter.getActive()) {
                    loadGrupoCategoria();
                    loadCategoria();
                }
                break;
            case "grau":
                listParentesco = new LinkedHashMap<>();
                selectedParentesco = new ArrayList();
                if (filter.getActive()) {
                    loadParentesco();
                }
                break;
            case "cidade_socio":
                listCidadesSocio = new LinkedHashMap<>();
                selectedCidadesSocio = new ArrayList();
                if (filter.getActive()) {
                    loadCidadesSocio();
                }
                break;
            case "estado_civil":
                listEstadoCivil = new LinkedHashMap<>();
                selectedEstadoCivil = new ArrayList<>();
                if (filter.getActive()) {
                    loadEstadoCivil();
                }
                break;
            /**
             * EMPRESAS
             */
            case "empresas":
                tipoEmpresas = "com";
                empresa = new Juridica();
                listEmpresas = new ArrayList();
                idEmpresas = null;
                minQtdeFuncionario = "0";
                maxQtdeFuncionario = "0";
                break;
            case "cidade_empresa":
                listCidadesEmpresa = new LinkedHashMap<>();
                selectedCidadesEmpresa = new ArrayList();
                if (filter.getActive()) {
                    loadCidadesEmpresa();
                }
                break;
            /**
             * FINANCEIRO
             */
            case "servicos":
                listServicos = new LinkedHashMap<>();
                selectedServicos = new ArrayList();
                contemServicos = null;
                if (filter.getActive()) {
                    contemServicos = true;
                    loadGrupoFinanceiro();
                    loadSubGrupoFinanceiro();
                    loadServicos();
                }
                break;
            case "tipo_cobranca":
                listTipoCobranca = new LinkedHashMap<>();
                selectedTipoCobranca = new ArrayList();
                if (filter.getActive()) {
                    loadTipoCobranca();
                }
                break;
            case "desconto_folha":
                tipoDescontoFolha = "";
                if (filter.getActive()) {
                    tipoDescontoFolha = "com";

                }
                break;
            case "desconto_social":
                listDescontoSocial = new LinkedHashMap<>();
                selectedDescontoSocial = new ArrayList();
                if (filter.getActive()) {
                    loadDescontoSocial();
                }
                break;
            case "situacao":
                tipoCarencia = "";
                situacaoString = "";
                carenciaDias = 0;
                if (filter.getActive()) {
                    tipoCarencia = "todos";
                    situacaoString = "adimplente";
                }
                break;

        }
    }

    public void addFilterDate() {
        if (selectedDate == null || selectedDate.isEmpty()) {
            return;
        }
        if (typeDate.equals("igual") || typeDate.equals("apartir") || typeDate.equals("ate")) {
            if (startDate.isEmpty()) {
                GenericaMensagem.warn("Validação", "INFORMAR DATA INICIAL!");
                return;
            }
        } else if (typeDate.equals("faixa")) {
            if (startDate.isEmpty()) {
                GenericaMensagem.warn("Validação", "INFORMAR DATA INICIAL!");
                return;
            }
            if (finishDate.isEmpty()) {
                GenericaMensagem.warn("Validação", "INFORMAR DATA FINAL!");
                return;
            }
        }
        listDateFilters.add(new DateFilters(true, selectedDate, typeDate, startDate, finishDate));
        loadDates();
        selectedDate = "";
        typeDate = "faixa";
        startDate = "";
        finishDate = "";
    }

    public void removeFilterDate(DateFilters df) {
        listDateFilters.remove(df);
        loadDates();
    }

    public void addSocio() {
        for (int i = 0; i < listSocios.size(); i++) {
            if (listSocios.get(i).getId() == socio.getId()) {
                GenericaMensagem.warn("Validação", "SÓCIO JÁ SELECIONADO!");
                return;
            }
        }
        listSocios.add(socio);
        socio = new Pessoa();
    }

    public void removeSocio() {
        socio = new Pessoa();
    }

    public void removeSocio(Pessoa p) {
        listSocios.remove(socio);
    }

    public void print() {
        List<ParametroSocios> list = loadListParametroSocios();
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return;
        }
        Collection collection = new ArrayList();
        collection.addAll(list);
        RelatorioDao db = new RelatorioDao();
        Relatorios relatorios = db.pesquisaRelatorios(idRelatorio);
        if (relatorios.getPorFolha()) {
            Jasper.GROUP_NAME = relatorios.getNomeGrupo();
            if (porFolha) {
                // Jasper.setIS_BY_LEAF((Boolean) true);
            } else {
                // Jasper.setIS_BY_LEAF((Boolean) false);
            }
        }
        Jasper.TYPE = "default";
        Jasper.TITLE = relatorios.getNome();
        Map map = new HashMap();
        map.put("groups", selectedGroups);
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) collection, map);
    }

    public void download() {
        try {
            File folder = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/temp/pessoa/");
            if (folder.isDirectory()) {
                File[] sun = folder.listFiles();
                for (File toDelete : sun) {
                    toDelete.delete();
                }
            }
            folder.delete();
        } catch (Exception e) {

        }

        List<ParametroSocios> list = loadListParametroSocios();
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return;
        }
        if (getShow("fotos")) {
            if (selectedAlfabeto == null || selectedAlfabeto.isEmpty()) {
                GenericaMensagem.warn("Validação", "SELECIONAR UMA LETRA!");
                return;
            }
            if (selectedAlfabeto.size() > 3) {
                GenericaMensagem.warn("Validação", "SELECIONAR MÁXIMO 3 LETRAS!");
                return;
            }
        }
        List<File> listFiles = new ArrayList();
        String foto = "";
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDs_foto() != null && !list.get(i).getDs_foto().isEmpty()) {
                Integer codigo = 0;
                try {
                    codigo = Integer.parseInt(list.get(i).getDs_foto());
                } catch (Exception e) {

                }
                if (codigo != list.get(i).getCodigo()) {
                    File fileTemp = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/temp/pessoa/");
                    if (!fileTemp.exists()) {
                        fileTemp.mkdirs();
                    }
                    foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + list.get(i).getCodigo() + "/" + list.get(i).getDs_foto() + ".png";
                    String new_foto = "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/temp/pessoa/" + list.get(i).getCodigo() + ".png";
                    File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);
                    File new_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + new_foto);
                    if (f.exists()) {
                        try {
                            Files.copy(f, new_f);
                            new_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + new_foto);
                            if (new_f.exists()) {
                                listFiles.add(new_f);
                            }
                            // new File(new_f.getPath().replace(".png", "")).delete();
                        } catch (Exception e) {
                            e.getMessage();

                        }
                        continue;
                    }
                    foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + list.get(i).getCodigo() + "/" + list.get(i).getDs_foto() + ".jpg";
                    new_foto = "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/temp/pessoa/" + list.get(i).getCodigo() + ".jpg";
                    f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);
                    new_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + new_foto);
                    if (f.exists()) {
                        try {
                            Files.copy(f, new_f);
                            new_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + new_foto);
                            if (new_f.exists()) {
                                listFiles.add(new_f);
                            }
                            // new File(new_f.getPath().replace(".jpg", "")).delete();
                        } catch (Exception e) {
                            e.getMessage();

                        }
                        continue;
                    }
                } else {
                    foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + list.get(i).getCodigo() + "/" + list.get(i).getDs_foto() + ".png";
                    File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);

                    if (f.exists()) {
                        listFiles.add(f);
                        continue;
                    }

                    foto = "cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + list.get(i).getCodigo() + "/" + list.get(i).getDs_foto() + ".jpg";
                    f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/" + foto);

                    if (f.exists()) {
                        listFiles.add(f);
                    }
                }
            }
        }
        Zip zip = new Zip();
        File path = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/fotos/"));
        if (!path.exists()) {
            path.mkdirs();
        }
        File outputFile = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/fotos/fotos_" + inIdAlfabeto().replace(",", "") + "_" + UUID.randomUUID().toString().replace("-", "_") + ".zip"));
        try {
            zip.zip(listFiles, outputFile);
            Download download = new Download(
                    outputFile.getName(),
                    outputFile.getParent(),
                    "zip",
                    FacesContext.getCurrentInstance());
            download.baixar();
            download.remover();
            File folder = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/temp/pessoa/");
            if (folder.isDirectory()) {
                File[] sun = folder.listFiles();
                for (File toDelete : sun) {
                    toDelete.delete();
                }
            }
            folder.delete();
        } catch (IOException ex) {
            Logger.getLogger(RelatorioSociosBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ParametroSocios> loadListParametroSocios() {
        if (getShow("situacao")) {
            if (carenciaDias < 0) {
                GenericaMensagem.warn("Validação", "INFORMAR CARÊNCIA DE DIAS EM DÉBITO!");
                return new ArrayList();
            }
        }
        if (!isFiltroSelecionado()) {
            GenericaMensagem.warn("Validação", "SELECIONAR UM FILTRO PARA REALIZAR A PESQUISA!");
            return new ArrayList();
        }

        RelatorioSociosDao relatorioSociosDao = new RelatorioSociosDao();

        if (!listRelatorioOrdem.isEmpty()) {
            Dao dao = new Dao();
            relatorioSociosDao.setRelatorioOrdem((RelatorioOrdem) dao.find(new RelatorioOrdem(), idRelatorioOrdem));
        }
        String meses = "";
        String di = "";
        String df = "";
        if (inIdMeses() != null) {
            di = String.valueOf(diaInicial);
            df = String.valueOf(diaFinal);
            meses = inIdMeses();
            if (!inIdMeses().isEmpty()) {
                if (di.length() == 1) {
                    di = "0" + di;
                }
                if (df.length() == 1) {
                    df = "0" + df;
                }
            }

        }
        idEmpresas = null;
        if (empresa != null && empresa.getId() != -1) {
            idEmpresas = empresa.getId();

        }
        RelatorioDao db = new RelatorioDao();
        Relatorios relatorios = db.pesquisaRelatorios(idRelatorio);
        relatorioSociosDao.setRelatorios(relatorios);
        List<List> list = relatorioSociosDao.find(
                /**
                 * IN
                 */
                inIdTipoCobranca(),
                inIdGrupoCategoria(),
                inIdCategoria(),
                inIdParentesco(),
                inIdCidadesSocio(),
                inIdCidadesEmpresa(),
                inIdGrupoFinanceiro(),
                inIdSubGrupoFinanceiro(),
                inIdServicos(),
                inIdDescontoSocial(),
                inIdSocios(),
                inIdAlfabeto(),
                /**
                 * EMPRESAS
                 */
                "" + idEmpresas,
                minQtdeFuncionario,
                maxQtdeFuncionario,
                /**
                 * TIPOS
                 */
                tipoFotos,
                tipoSexo,
                tipoCarteirinha,
                "",
                tipoEmail,
                tipoTelefone,
                inIdEstadoCivil(),
                tipoEmpresas,
                tipoCarencia,
                situacaoString,
                tipoDescontoFolha,
                tipoBiometria,
                tipoSuspencao,
                tipoOposicao,
                /**
                 * OUTROS
                 */
                matriculaInicial,
                matriculaFinal,
                idadeInicial,
                idadeFinal,
                carenciaDias,
                contemServicos,
                statusSocio,
                /**
                 * ANIVERSÁRIO
                 */
                meses,
                di,
                df,
                ordemAniversario,
                /**
                 * DATAS
                 */
                listDateFilters
        );
        List<ParametroSocios> pses = new ArrayList();
        Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
        String s_site = sindicato.getPessoa().getSite(), // SITE
                s_nome = sindicato.getPessoa().getNome(), // SIN NOME
                s_endereco = sindicato.getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao(), // SIN ENDERECO
                s_logradouro = sindicato.getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao(), // SIN LOGRADOURO
                s_numero = sindicato.getPessoa().getPessoaEndereco().getNumero(), // SIN NUMERO
                s_complemento = sindicato.getPessoa().getPessoaEndereco().getComplemento(), // SIN COMPLEMENTO
                s_bairro = sindicato.getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao(), // SIN BAIRRO
                s_cep = sindicato.getPessoa().getPessoaEndereco().getEndereco().getCep(), // SIN CEP
                s_cidade = sindicato.getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade(), // SIN CIDADE
                s_uf = sindicato.getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf(),// SIN UF 
                s_documento = sindicato.getPessoa().getDocumento(); // SIN DOCUMENTO 

        for (int i = 0; i < list.size(); i++) {
            Boolean votante = false;
            if (list.get(i).get(67) != null) {
                votante = (Boolean) list.get(i).get(67);
            }
            BigDecimal nrDesconto = new BigDecimal(0);
            if (list.get(i).get(58) != null) {
                try {
                    nrDesconto = new BigDecimal(Float.parseFloat(getConverteNullString(list.get(i).get(58))));
                } catch (Exception e) {

                }
            }
            Boolean descontoFolha = false;
            if (list.get(i).get(70) != null) {
                descontoFolha = (Boolean) list.get(i).get(70);
            }
            ParametroSocios parametroSocios = new ParametroSocios(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                    //                    getConverteNullString(result.get(i).get(1)), // SITE
                    //                    getConverteNullString(result.get(i).get(2)), // SIN NOME
                    //                    getConverteNullString(result.get(i).get(3)), // SIN ENDERECO
                    //                    getConverteNullString(result.get(i).get(4)), // SIN LOGRADOURO
                    //                    getConverteNullString(result.get(i).get(5)), // SIN NUMERO
                    //                    getConverteNullString(result.get(i).get(6)), // SIN COMPLEMENTO
                    //                    getConverteNullString(result.get(i).get(7)), // SIN BAIRRO
                    //                    getConverteNullString(result.get(i).get(8)), // SIN CEP
                    //                    getConverteNullString(result.get(i).get(9)), // SIN CIDADE
                    //                    getConverteNullString(result.get(i).get(10)),// SIN UF 
                    //                    getConverteNullString(result.get(i).get(11)),// SIN DOCUMENTO 
                    s_site, // SITE
                    s_nome, // SIN NOME
                    s_endereco, // SIN ENDERECO
                    s_logradouro, // SIN LOGRADOURO
                    s_numero, // SIN NUMERO
                    s_complemento, // SIN COMPLEMENTO
                    s_bairro, // SIN BAIRRO
                    s_cep, // SIN CEP
                    s_cidade, // SIN CIDADE
                    s_uf,// SIN UF 
                    sindicato.getPessoa().getDocumento(),// SIN DOCUMENTO 
                    getConverteNullInt(list.get(i).get(12)),// CODIGO 
                    (Date) list.get(i).get(13),// CADASTRO
                    getConverteNullString(list.get(i).get(14)),// NOME
                    getConverteNullString(list.get(i).get(15)),// CPF
                    getConverteNullString(list.get(i).get(16)),// TELEFONE
                    getConverteNullString(list.get(i).get(17)),// UF EMISSAO RG
                    getConverteNullString(list.get(i).get(18)),// ESTADO CIVIL
                    getConverteNullString(list.get(i).get(19)),// CTPS
                    getConverteNullString(list.get(i).get(20)),// PAI
                    getConverteNullString(list.get(i).get(21)),// SEXO
                    getConverteNullString(list.get(i).get(22)),// MAE
                    getConverteNullString(list.get(i).get(23)),// NACIONALIDADE
                    getConverteNullString(list.get(i).get(24)),// NIT
                    getConverteNullString(list.get(i).get(25)),// ORGAO EMISSAO RG
                    getConverteNullString(list.get(i).get(26)),// PIS
                    getConverteNullString(list.get(i).get(27)),// SERIE
                    (Date) list.get(i).get(28),// APOSENTADORIA ------------
                    getConverteNullString(list.get(i).get(29)),// NATURALIDADE
                    (Date) list.get(i).get(30),// RECADASTRO
                    (Date) list.get(i).get(31),// DT NASCIMENTO -------------
                    getConverteNullString(list.get(i).get(32)),// DT FOTO -------------------
                    getConverteNullString(list.get(i).get(33)),// RG
                    "",// CAMINHO DA FOTO SOCIO
                    getConverteNullString(list.get(i).get(35)),// LOGRADOURO
                    getConverteNullString(list.get(i).get(36)),// ENDERECO
                    getConverteNullString(list.get(i).get(37)),// NUMERO
                    getConverteNullString(list.get(i).get(38)),// COMPLEMENTO
                    getConverteNullString(list.get(i).get(39)),// BAIRRO
                    getConverteNullString(list.get(i).get(40)),// CIDADE
                    getConverteNullString(list.get(i).get(41)),// UF
                    getConverteNullString(getConverteNullString(list.get(i).get(42))),// CEP
                    getConverteNullString(list.get(i).get(43)),// SETOR
                    (Date) list.get(i).get(44),// DT ADMISSAO ---------------
                    getConverteNullString(list.get(i).get(45)),// PROFISSAO
                    getConverteNullString(list.get(i).get(46)),// EMPRESA FANTASIA
                    getConverteNullString(list.get(i).get(47)),// NOME EMPRESA
                    getConverteNullString(list.get(i).get(48)),// EMPRESA CNPJ
                    getConverteNullString(list.get(i).get(49)),// EMPRESA TELEFONE
                    getConverteNullString(list.get(i).get(50)),// EMPRESA LOGRADOURO
                    getConverteNullString(list.get(i).get(51)),// EMPRESA ENDERECO
                    getConverteNullString(list.get(i).get(52)),// EMPRESA NUMERO
                    getConverteNullString(list.get(i).get(53)),// "       COMPLEMENTO 
                    getConverteNullString(list.get(i).get(54)),// "       BAIRRO
                    getConverteNullString(list.get(i).get(55)),// "       CIDADE
                    getConverteNullString(list.get(i).get(56)),// "       UF
                    getConverteNullString(list.get(i).get(57)),// "       CEP
                    getConverteNullString(list.get(i).get(58)),// TITULAR
                    getConverteNullString(list.get(i).get(59)),// COD SOCIO
                    getConverteNullString(list.get(i).get(60)),// NOME SOCIO
                    getConverteNullString(list.get(i).get(61)),// PARENTESCO 
                    getConverteNullInt(list.get(i).get(62)),// MATRICULA
                    getConverteNullString(list.get(i).get(63)),// CATEGORIA
                    getConverteNullString(list.get(i).get(64)),// GRUPO CATEGORIA
                    (Date) list.get(i).get(65),// DT FILIACAO --------------
                    (Date) list.get(i).get(66),// INATIVACAO ---------------
                    votante,// VOTANTE
                    getConverteNullString(list.get(i).get(68)),// GRAU
                    nrDesconto,// NR DESCONTO
                    descontoFolha,
                    getConverteNullString(list.get(i).get(71)),// TIPO COBRANCA
                    getConverteNullInt(list.get(i).get(72)),// COD TIPO COBRANCA
                    getConverteNullString(list.get(i).get(73)),// TELEFONE2
                    getConverteNullString(list.get(i).get(74)), // TELEFONE3                                          
                    getConverteNullString(list.get(i).get(75)), // EMAIL 1
                    getConverteNullString(list.get(i).get(76)), // CONTABILIDADE - NOME
                    getConverteNullString(list.get(i).get(77)), // CONTABILIDADE - CONTATO
                    getConverteNullString(list.get(i).get(78)), // CONTABILIDADE - EMAIL
                    ((getConverteNullString(list.get(i).get(79)) != null) ? DataHoje.converteData((Date) list.get(i).get(79)) : ""), // ADMISSAO EMPRESA DEMISSIONADA
                    ((getConverteNullString(list.get(i).get(80)) != null) ? DataHoje.converteData((Date) list.get(i).get(80)) : ""), // DEMISSAO EMPRESA DEMISSIONADA
                    getConverteNullString(list.get(i).get(81)), // CNPJ EMPRESA DEMISSIONADA
                    getConverteNullString(list.get(i).get(82)), // EMPRESA DEMISSIONADA
                    getConverteNullString(list.get(i).get(83)), // IDADE
                    list.get(i).get(84), // SUSPENÇÃO MOTIVO
                    list.get(i).get(85), // SUSPENÇÃO DATA INICIAL
                    list.get(i).get(86) // SUSPENÇÃO DATA FINAL
            );
            pses.add(parametroSocios);
        }
        return pses;

    }

    public List<SelectItem> getListaEmpresas() {
        List<SelectItem> empresas = new ArrayList<SelectItem>();
        if (tipoEmpresas.equals("especificas")) {
            int i = 0;
            RelatorioSociosDao db = new RelatorioSociosDao();
            List<Juridica> select = db.listaEmpresaDoSocio();
            if (!select.isEmpty()) {
                while (i < select.size()) {
                    empresas.add(new SelectItem(new Integer(i),
                            (String) ((Juridica) select.get(i)).getPessoa().getNome(),
                            Integer.toString(((Juridica) select.get(i)).getId())));
                    i++;
                }
            }
        }
        return empresas;
    }

    public String getConverteNullString(Object object) {
        if (object == null) {
            return "";
        } else {
            return String.valueOf(object);
        }
    }

    public Integer getConverteNullInt(Object object) {
        if (object == null) {
            return 0;
        } else {
            return (Integer) object;
        }
    }
//
//    public String getDataCadastro() {
//        return dataCadastro;
//    }
//
//    public void setDataCadastro(String dataCadastro) {
//        this.dataCadastro = dataCadastro;
//    }
//
//    public String getDataRecadastro() {
//        return dataRecadastro;
//    }
//
//    public void setDataRecadastro(String dataRecadastro) {
//        this.dataRecadastro = dataRecadastro;
//    }

    public String getMatriculaInicial() {
        return matriculaInicial;
    }

    public void setMatriculaInicial(String matriculaInicial) {
        this.matriculaInicial = matriculaInicial;
    }

    public String getMatriculaFinal() {
        return matriculaFinal;
    }

    public void setMatriculaFinal(String matriculaFinal) {
        this.matriculaFinal = matriculaFinal;
    }

    public String getTipoEleicao() {
        return tipoEleicao;
    }

    public void setTipoEleicao(String tipoEleicao) {
        this.tipoEleicao = tipoEleicao;
    }

    public String getIdadeInicial() {
        return idadeInicial;
    }

    public void setIdadeInicial(String idadeInicial) {
        this.idadeInicial = idadeInicial;
    }

    public String getIdadeFinal() {
        return idadeFinal;
    }

    public void setIdadeFinal(String idadeFinal) {
        this.idadeFinal = idadeFinal;
    }

    public String getTipoSexo() {
        return tipoSexo;
    }

    public void setTipoSexo(String tipoSexo) {
        this.tipoSexo = tipoSexo;
    }

    public String getTipoCarteirinha() {
        return tipoCarteirinha;
    }

    public void setTipoCarteirinha(String tipoCarteirinha) {
        this.tipoCarteirinha = tipoCarteirinha;
    }

    public String getTipoDescontoFolha() {
        return tipoDescontoFolha;
    }

    public void setTipoDescontoFolha(String tipoDescontoFolha) {
        this.tipoDescontoFolha = tipoDescontoFolha;
    }

    public String getTipoFotos() {
        return tipoFotos;
    }

    public void setTipoFotos(String tipoFotos) {
        this.tipoFotos = tipoFotos;
    }

    public String getTipoDescontoGeracao() {
        return tipoDescontoGeracao;
    }

    public void setTipoDescontoGeracao(String tipoDescontoGeracao) {
        this.tipoDescontoGeracao = tipoDescontoGeracao;
    }

    public String getTipoEmpresas() {
        return tipoEmpresas;
    }

    public void setTipoEmpresas(String tipoEmpresas) {
        this.tipoEmpresas = tipoEmpresas;
    }

    public Integer getIdEmpresas() {
        return idEmpresas;
    }

    public void setIdEmpresas(Integer idEmpresas) {
        this.idEmpresas = idEmpresas;
    }

    public Integer getIdDias() {
        return idDias;
    }

    public void setIdDias(Integer idDias) {
        this.idDias = idDias;
    }

    public String getDiaInicial() {
        return diaInicial;
    }

    public void setDiaInicial(String diaInicial) {
        this.diaInicial = diaInicial;
    }

    public String getDiaFinal() {
        return diaFinal;
    }

    public void setDiaFinal(String diaFinal) {
        this.diaFinal = diaFinal;
    }

    public String getTipoOrdem() {
        return tipoOrdem;
    }

    public void setTipoOrdem(String tipoOrdem) {
        this.tipoOrdem = tipoOrdem;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public String getTipoEmail() {
        return tipoEmail;
    }

    public void setTipoEmail(String tipoEmail) {
        this.tipoEmail = tipoEmail;
    }

    public String getTipoTelefone() {
        return tipoTelefone;
    }

    public void setTipoTelefone(String tipoTelefone) {
        this.tipoTelefone = tipoTelefone;
    }

    public String getTipoEstadoCivil() {
        return tipoEstadoCivil;
    }

    public void setTipoEstadoCivil(String tipoEstadoCivil) {
        this.tipoEstadoCivil = tipoEstadoCivil;
    }

//
//    public String getDataAdmissaoSocio() {
//        return dataAdmissaoSocio;
//    }
//
//    public void setDataAdmissaoSocio(String dataAdmissaoSocio) {
//        this.dataAdmissaoSocio = dataAdmissaoSocio;
//    }
//
//    public String getDataAdmissaoEmpresa() {
//        return dataAdmissaoEmpresa;
//    }
//
//    public void setDataAdmissaoEmpresa(String dataAdmissaoEmpresa) {
//        this.dataAdmissaoEmpresa = dataAdmissaoEmpresa;
//    }
//
//    public String getDataCadastroFim() {
//        return dataCadastroFim;
//    }
//
//    public void setDataCadastroFim(String dataCadastroFim) {
//        this.dataCadastroFim = dataCadastroFim;
//    }
//
//    public String getDataRecadastroFim() {
//        return dataRecadastroFim;
//    }
//
//    public void setDataRecadastroFim(String dataRecadastroFim) {
//        this.dataRecadastroFim = dataRecadastroFim;
//    }
//
//    public String getDataDemissaoFim() {
//        return dataDemissaoFim;
//    }
//
//    public void setDataDemissaoFim(String dataDemissaoFim) {
//        this.dataDemissaoFim = dataDemissaoFim;
//    }
//
//    public String getDataAdmissaoSocioFim() {
//        return dataAdmissaoSocioFim;
//    }
//
//    public void setDataAdmissaoSocioFim(String dataAdmissaoSocioFim) {
//        this.dataAdmissaoSocioFim = dataAdmissaoSocioFim;
//    }
//
//    public String getDataAdmissaoEmpresaFim() {
//        return dataAdmissaoEmpresaFim;
//    }
//
//    public void setDataAdmissaoEmpresaFim(String dataAdmissaoEmpresaFim) {
//        this.dataAdmissaoEmpresaFim = dataAdmissaoEmpresaFim;
//    }
//
//    public String getDataAposetandoria() {
//        return dataAposetandoria;
//    }
//
//    public void setDataAposetandoria(String dataAposetandoria) {
//        this.dataAposetandoria = dataAposetandoria;
//    }
//
//    public String getDataAposetandoriaFim() {
//        return dataAposetandoriaFim;
//    }
//
//    public void setDataAposetandoriaFim(String dataAposetandoriaFim) {
//        this.dataAposetandoriaFim = dataAposetandoriaFim;
//    }
    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Integer getCarenciaDias() {
        return carenciaDias;
    }

    public void setCarenciaDias(Integer carenciaDias) {
        try {
            this.carenciaDias = carenciaDias;
        } catch (Exception e) {
            this.carenciaDias = 0;
        }
    }

    public String getCarenciaDiasString() {
        try {
            return Integer.toString(carenciaDias);
        } catch (Exception e) {
            return "0";
        }
    }

    public void setCarenciaDiasString(String carenciaDiasString) {
        try {
            this.carenciaDias = Integer.parseInt(carenciaDiasString);
        } catch (Exception e) {
            this.carenciaDias = 0;
        }
    }

    public String getTipoCarencia() {
        return tipoCarencia;
    }

    public void setTipoCarencia(String tipoCarencia) {
        this.tipoCarencia = tipoCarencia;
    }

    public Boolean getEnableFolha() {
        if (idRelatorio != null) {
            Relatorios r = (Relatorios) new Dao().find(new Relatorios(), idRelatorio);
            if (r != null) {
                enableFolha = r.getPorFolha();
            }
        }
        return enableFolha;
    }

    public void setEnableFolha(Boolean enableFolha) {
        this.enableFolha = enableFolha;
    }

    public Boolean getPorFolha() {
        return porFolha;
    }

    public void setPorFolha(Boolean porFolha) {
        this.porFolha = porFolha;
    }

    public Juridica getEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            empresa = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        if (empresa == null) {
            empresa = new Juridica();
        }
        this.empresa = empresa;
    }

    public Boolean getCompactar() {
        return compactar;
    }

    public void setCompactar(Boolean compactar) {
        this.compactar = compactar;
    }

    public String getTipoBiometria() {
        return tipoBiometria;
    }

    public void setTipoBiometria(String tipoBiometria) {
        this.tipoBiometria = tipoBiometria;
    }

    public String getSituacaoString() {
        return situacaoString;
    }

    public void setSituacaoString(String situacaoString) {
        this.situacaoString = situacaoString;
    }

    public String getMinQtdeFuncionario() {
        return minQtdeFuncionario;
    }

    public void setMinQtdeFuncionario(String minQtdeFuncionario) {
        this.minQtdeFuncionario = minQtdeFuncionario;
    }

    public String getMaxQtdeFuncionario() {
        return maxQtdeFuncionario;
    }

    public void setMaxQtdeFuncionario(String maxQtdeFuncionario) {
        this.maxQtdeFuncionario = maxQtdeFuncionario;
    }

    public String getMinQtdeFuncionarioString() {
        try {
            return minQtdeFuncionario;
        } catch (Exception e) {
            return "0";
        }
    }

    public void setMinQtdeFuncionarioString(String minQtdeFuncionarioString) {
        try {
            Integer min = Integer.parseInt(minQtdeFuncionarioString);
            Integer max = Integer.parseInt(maxQtdeFuncionario);
            if (max != null && min != null && min > max) {
                maxQtdeFuncionario = "" + min;
            }
            this.minQtdeFuncionario = minQtdeFuncionarioString;
        } catch (Exception e) {
            this.minQtdeFuncionario = "0";

        }
    }

    public String getMaxQtdeFuncionarioString() {
        try {
            return maxQtdeFuncionario;
        } catch (Exception e) {
            return "0";
        }
    }

    public void setMaxQtdeFuncionarioString(String maxQtdeFuncionarioString) {
        try {
            Integer min = Integer.parseInt(minQtdeFuncionario);
            Integer max = Integer.parseInt(maxQtdeFuncionarioString);
            if (max != null && min != null && min > max) {
                maxQtdeFuncionario = "" + min;
            }
            this.maxQtdeFuncionario = maxQtdeFuncionarioString;
        } catch (Exception e) {
            this.maxQtdeFuncionario = "0";
        }
    }
//
//    public String getDataAtualicacao() {
//        return dataAtualicacao;
//    }
//
//    public void setDataAtualicacao(String dataAtualicacao) {
//        this.dataAtualicacao = dataAtualicacao;
//    }
//
//    public String getDataAtualicacaoFim() {
//        return dataAtualicacaoFim;
//    }
//
//    public void setDataAtualicacaoFim(String dataAtualicacaoFim) {
//        this.dataAtualicacaoFim = dataAtualicacaoFim;
//    }

    public boolean isOrdemAniversario() {
        return ordemAniversario;
    }

    public void setOrdemAniversario(boolean ordemAniversario) {
        this.ordemAniversario = ordemAniversario;
    }

    public void loadGrupoFinanceiro() {
        listGrupoFinanceiro = new LinkedHashMap<>();
        selectedGrupoFinanceiro = new ArrayList<>();
        listSubGrupoFinanceiro = new LinkedHashMap<>();
        selectedSubGrupoFinanceiro = new ArrayList();
        listServicos = new LinkedHashMap<>();
        selectedServicos = new ArrayList<>();
        List<GrupoFinanceiro> list = new Dao().list(new GrupoFinanceiro(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listGrupoFinanceiro.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadSubGrupoFinanceiro() {
        listSubGrupoFinanceiro = new LinkedHashMap<>();
        selectedSubGrupoFinanceiro = new ArrayList();
        loadServicos();
        if (inIdGrupoFinanceiro() != null && !inIdGrupoFinanceiro().isEmpty()) {
            listSubGrupoFinanceiro = new LinkedHashMap<>();
            FinanceiroDao fd = new FinanceiroDao();
            List<SubGrupoFinanceiro> list = fd.listaSubGrupo(inIdGrupoFinanceiro());
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    listSubGrupoFinanceiro.put(list.get(i).getDescricao(), list.get(i).getId());
                }
            }
        }
    }

    public void loadServicos() {
        listServicos = new LinkedHashMap<>();
        selectedServicos = new ArrayList<>();
        List<Servicos> list;
        ServicosDao servicosDao = new ServicosDao();
        if (selectedSubGrupoFinanceiro != null && !selectedSubGrupoFinanceiro.isEmpty()) {
            servicosDao.setSituacao("A");
            list = new ServicosDao().findBySubGrupoFinanceiro(inIdSubGrupoFinanceiro());
        } else if (selectedGrupoFinanceiro != null && !selectedGrupoFinanceiro.isEmpty()) {
            servicosDao.setSituacao("A");
            list = new ServicosDao().findByGrupoFinanceiro(inIdGrupoFinanceiro());
        } else {
            list = new Dao().list(new Servicos(), true);
        }
        for (int i = 0; i < list.size(); i++) {
            listServicos.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadGroups() {
        listGroups = new ArrayList();
        listGroups.add(new SelectItem("categoria", "Categoria (Default)"));
        listGroups.add(new SelectItem("empresa", "Empresa"));
        listGroups.add(new SelectItem("titular", "Titular"));
        listGroups.add(new SelectItem("parentesco", "Parentesco", "", true));
    }

    public void loadDescontoSocial() {
        listDescontoSocial = new LinkedHashMap<>();
        selectedDescontoSocial = new ArrayList<>();
        List<DescontoSocial> list = new Dao().list(new DescontoSocial(), true);
        for (int i = 0; i < list.size(); i++) {
            listDescontoSocial.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadTipoCobranca() {
        listTipoCobranca = new LinkedHashMap<>();
        selectedTipoCobranca = new ArrayList<>();
        FTipoDocumentoDao db = new FTipoDocumentoDao();
        List<FTipoDocumento> list = new ArrayList();
        list.add((FTipoDocumento) new Dao().find(new FTipoDocumento(), 2));
        list.addAll(db.pesquisaListaTipoExtrato());
        for (int i = 0; i < list.size(); i++) {
            listTipoCobranca.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadDates() {
        listDates = new ArrayList();
        listDates.add(new SelectItem("admissao", "Admissão"));
        listDates.add(new SelectItem("aposentadoria", "Aposentadoria"));
        listDates.add(new SelectItem("atualizacao", "Atualização"));
        listDates.add(new SelectItem("cadastro", "Cadastro"));
        listDates.add(new SelectItem("demissao", "Demissão"));
        listDates.add(new SelectItem("filiacao", "Filiação"));
        listDates.add(new SelectItem("recadastro", "Recadastro"));
        if (getShow("carteirinha")) {
            listDates.add(new SelectItem("validade_carteirinha", "Validade Carteirinha"));
        }
        // RELOAD DATA
        if (listDateFilters != null) {
            for (int i = 0; i < listDateFilters.size(); i++) {
                for (int x = 0; x < listDates.size(); x++) {
                    if (listDateFilters.get(i).getTitle().equals(listDates.get(x).getValue().toString())) {
                        listDates.get(x).setDisabled(true);
                        break;
                    }
                }
            }
        }
    }

    public void loadMeses() {
        listMeses = new LinkedHashMap<>();
        selectedMeses = new ArrayList<>();
        List<Mes> list = new Dao().list(new Mes());
        String month = DataHoje.livre(new Date(), "MM");
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(month) == list.get(i).getId()) {
                selectedMeses.add(list.get(i).getId());
            }
            listMeses.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    // TRATAMENTO
    public String inIdSocios() {
        String ids = null;
        if (listSocios != null) {
            if (socio != null && socio.getId() != -1) {
                ids = "";
                ids = "" + socio.getId();
            }
            for (int i = 0; i < listSocios.size(); i++) {
                if (listSocios.get(i) != null) {
                    if (ids == null) {
                        ids = "";
                        ids = "" + listSocios.get(i).getId();
                    } else {
                        ids += "," + listSocios.get(i).getId();
                    }
                }
            }
        }
        return ids;
    }

    public String inIdSubGrupoFinanceiro() {
        String ids = null;
        if (selectedSubGrupoFinanceiro != null) {
            for (int i = 0; i < selectedSubGrupoFinanceiro.size(); i++) {
                if (selectedSubGrupoFinanceiro.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedSubGrupoFinanceiro.get(i);
                    } else {
                        ids += "," + selectedSubGrupoFinanceiro.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdGrupoFinanceiro() {
        String ids = null;
        if (selectedGrupoFinanceiro != null) {
            for (int i = 0; i < selectedGrupoFinanceiro.size(); i++) {
                if (selectedGrupoFinanceiro.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedGrupoFinanceiro.get(i);
                    } else {
                        ids += "," + selectedGrupoFinanceiro.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdMeses() {
        String ids = null;
        if (selectedMeses != null) {
            for (int i = 0; i < selectedMeses.size(); i++) {
                if (selectedMeses.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedMeses.get(i);
                    } else {
                        ids += "," + selectedMeses.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdServicos() {
        String ids = null;
        if (selectedServicos != null) {
            for (int i = 0; i < selectedServicos.size(); i++) {
                if (selectedServicos.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedServicos.get(i);
                    } else {
                        ids += "," + selectedServicos.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdDescontoSocial() {
        String ids = null;
        if (selectedDescontoSocial != null) {
            for (int i = 0; i < selectedDescontoSocial.size(); i++) {
                if (selectedDescontoSocial.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedDescontoSocial.get(i);
                    } else {
                        ids += "," + selectedDescontoSocial.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public Map<String, Integer> getListGrupoFinanceiro() {
        return listGrupoFinanceiro;
    }

    public void setListGrupoFinanceiro(Map<String, Integer> listGrupoFinanceiro) {
        this.listGrupoFinanceiro = listGrupoFinanceiro;
    }

    public List getSelectedGrupoFinanceiro() {
        return selectedGrupoFinanceiro;
    }

    public void setSelectedGrupoFinanceiro(List selectedGrupoFinanceiro) {
        this.selectedGrupoFinanceiro = selectedGrupoFinanceiro;
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

    public Map<String, Integer> getListSubGrupoFinanceiro() {
        return listSubGrupoFinanceiro;
    }

    public void setListSubGrupoFinanceiro(Map<String, Integer> listSubGrupoFinanceiro) {
        this.listSubGrupoFinanceiro = listSubGrupoFinanceiro;
    }

    public List getSelectedSubGrupoFinanceiro() {
        return selectedSubGrupoFinanceiro;
    }

    public void setSelectedSubGrupoFinanceiro(List selectedSubGrupoFinanceiro) {
        this.selectedSubGrupoFinanceiro = selectedSubGrupoFinanceiro;
        this.selectedSubGrupoFinanceiro = selectedSubGrupoFinanceiro;
    }

    public String getContemServicos() {
        try {
            return "" + contemServicos;
        } catch (Exception e) {
            return null;
        }
    }

    public void setContemServicos(String contemServicos) {
        if (contemServicos.equals("null")) {
            this.contemServicos = null;
        } else {
            this.contemServicos = Boolean.parseBoolean(contemServicos);
        }
    }

    public Boolean getShow(String filtro) {
        try {
            for (Filters filters : filtersSocio) {
                if (filters.getKey().equals(filtro)) {
                    if (filters.getActive()) {
                        return true;
                    }
                }
            }
            for (Filters filters : filtersEmpresa) {
                if (filters.getKey().equals(filtro)) {
                    if (filters.getActive()) {
                        return true;
                    }
                }
            }
            for (Filters filters : filtersFinanceiro) {
                if (filters.getKey().equals(filtro)) {
                    if (filters.getActive()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public List<Filters> getFiltersSocio() {
        return filtersSocio;
    }

    public void setFiltersSocio(List<Filters> filtersSocio) {
        this.filtersSocio = filtersSocio;
    }

    public List<Filters> getFiltersEmpresa() {
        return filtersEmpresa;
    }

    public void setFiltersEmpresa(List<Filters> filtersEmpresa) {
        this.filtersEmpresa = filtersEmpresa;
    }

    public List<Filters> getFiltersFinanceiro() {
        return filtersFinanceiro;
    }

    public void setFiltersFinanceiro(List<Filters> filtersFinanceiro) {
        this.filtersFinanceiro = filtersFinanceiro;
    }

    public Map<String, Integer> getListDescontoSocial() {
        return listDescontoSocial;
    }

    public void setListDescontoSocial(Map<String, Integer> listDescontoSocial) {
        this.listDescontoSocial = listDescontoSocial;
    }

    public List getSelectedDescontoSocial() {
        return selectedDescontoSocial;
    }

    public void setSelectedDescontoSocial(List selectedDescontoSocial) {
        this.selectedDescontoSocial = selectedDescontoSocial;
    }

    public String getStatusSocio() {
        return statusSocio;
    }

    public void setStatusSocio(String statusSocio) {
        this.statusSocio = statusSocio;
    }

    // LOAD
    public void loadRelatorios() {
        loadRelatorios(true);
    }

    public void loadRelatorios(Boolean reload_id) {
        listRelatorio = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        Integer default_id = 0;
        for (int i = 0; i < list.size(); i++) {
            Boolean disabled = false;
            if (reload_id) {
                if (i == 0) {
                    idRelatorio = list.get(i).getId();
                    default_id = list.get(i).getId();
                }
                if (list.get(i).getPrincipal()) {
                    default_id = list.get(i).getId();
                    idRelatorio = list.get(i).getId();
                }
            }
            disabled = false;
            if (list.get(i).getId() == 106) {
                disabled = true;
                if (!reload_id && getShow("suspencao") && tipoSuspencao != null && !tipoSuspencao.isEmpty()) {
                    if (tipoSuspencao.equals("com")) {
                        idRelatorio = list.get(i).getId();
                        disabled = false;
                    } else if (tipoSuspencao.equals("sem")) {
                        idRelatorio = default_id;
                    }
                }
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome(), "", disabled));
        }
    }

    public void loadRelatoriosOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadGrupoCategoria() {
        listGrupoCategoria = new LinkedHashMap<>();
        selectedGrupoCategoria = new ArrayList();
        List<GrupoCategoria> list = (List<GrupoCategoria>) new Dao().list(new GrupoCategoria(), true);
        for (GrupoCategoria gc : list) {
            selectedGrupoCategoria.add(gc.getId());
            listGrupoCategoria.put(gc.getGrupoCategoria(), gc.getId());
        }
    }

    public void loadCategoria() {
        listCategoria = new LinkedHashMap<>();
        selectedCategoria = new ArrayList();
        CategoriaDao db = new CategoriaDao();
        List<Categoria> list = new ArrayList();
        if (!listGrupoCategoria.isEmpty()) {
            String ids = inIdGrupoCategoria();
            if (ids != null) {
                list = db.pesquisaCategoriaPorGrupoIds(ids);
            }
        }
        for (Categoria c : list) {
            c.setSelected(true);
            listCategoria.put(c.getCategoria(), c.getId());
        }
    }

    public void loadParentesco() {
        listParentesco = new LinkedHashMap<>();
        selectedParentesco = new ArrayList();
        List<Parentesco> list = new Dao().list(new Parentesco(), true);
        for (Parentesco p : list) {
            if (p.getParentesco().equals("TITULAR")) {
                selectedParentesco.add(p.getId());
            }
            listParentesco.put(p.getParentesco(), p.getId());
        }
    }

    public void loadCidadesSocio() {
        Registro r = Registro.get();
        listCidadesSocio = new LinkedHashMap<>();
        selectedCidadesSocio = new ArrayList();
        List<Cidade> list = new RelatorioSociosDao().listaCidadeDoSocio();
        for (int i = 0; i < list.size(); i++) {
            if (r.getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade().getId() == list.get(i).getId()) {
                selectedCidadesSocio.add(list.get(i).getId());
            }
            listCidadesSocio.put(list.get(i).getCidade(), list.get(i).getId());
        }
    }

    public void loadCidadesEmpresa() {
        Registro r = Registro.get();
        listCidadesEmpresa = new LinkedHashMap<>();
        selectedCidadesEmpresa = new ArrayList();
        List<Cidade> list = new RelatorioSociosDao().listaCidadeDaEmpresa();
        for (int i = 0; i < list.size(); i++) {
            if (r.getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade().getId() == list.get(i).getId()) {
                selectedCidadesEmpresa.add(list.get(i).getId());
            }
            listCidadesEmpresa.put(list.get(i).getCidade(), list.get(i).getId());
        }
    }

    public void loadEstadoCivil() {
        listEstadoCivil = new LinkedHashMap<>();
        selectedEstadoCivil = new ArrayList<>();
        listEstadoCivil.put("Amasiado(a)", "Amasiado(a)");
        listEstadoCivil.put("Casado(a)", "Casado(a)");
        listEstadoCivil.put("Desquitado(a)", "Desquitado(a)");
        listEstadoCivil.put("Divorciado(a)", "Divorciado(a)");
        listEstadoCivil.put("Indefinido", "Indefinido");
        listEstadoCivil.put("Separado(a)", "Separado(a)");
        listEstadoCivil.put("Solteiro(a)", "Solteiro(a)");
        listEstadoCivil.put("Viuvo(a)", "Viuvo(a)");
    }

    public void loadAlfabeto() {
        listAlfabeto = new LinkedHashMap<>();
        selectedAlfabeto = new ArrayList<>();
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            String ch2 = "" + Character.valueOf(ch);
            listAlfabeto.put(ch2, ch2);
        }
    }

    // SELECTEDS
    public String inIdGrupoCategoria() {
        String ids = null;
        if (selectedGrupoCategoria != null) {
            ids = "";
            for (int i = 0; i < selectedGrupoCategoria.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedGrupoCategoria.get(i).toString();
                } else {
                    ids += "," + selectedGrupoCategoria.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdCategoria() {
        String ids = null;
        if (selectedCategoria != null) {
            ids = "";
            for (int i = 0; i < selectedCategoria.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedCategoria.get(i).toString();
                } else {
                    ids += "," + selectedCategoria.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdParentesco() {
        String ids = null;
        if (selectedParentesco != null) {
            ids = "";
            for (int i = 0; i < selectedParentesco.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedParentesco.get(i).toString();
                } else {
                    ids += "," + selectedParentesco.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdCidadesSocio() {
        String ids = null;
        if (selectedCidadesSocio != null) {
            ids = "";
            for (int i = 0; i < selectedCidadesSocio.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedCidadesSocio.get(i).toString();
                } else {
                    ids += "," + selectedCidadesSocio.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdTipoCobranca() {
        String ids = null;
        if (selectedTipoCobranca != null) {
            ids = "";
            for (int i = 0; i < selectedTipoCobranca.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedTipoCobranca.get(i).toString();
                } else {
                    ids += "," + selectedTipoCobranca.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdCidadesEmpresa() {
        String ids = null;
        if (selectedCidadesEmpresa != null) {
            ids = "";
            for (int i = 0; i < selectedCidadesEmpresa.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedCidadesEmpresa.get(i).toString();
                } else {
                    ids += "," + selectedCidadesEmpresa.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdEstadoCivil() {
        String ids = null;
        if (selectedEstadoCivil != null) {
            ids = "";
            for (int i = 0; i < selectedEstadoCivil.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedEstadoCivil.get(i).toString();
                } else {
                    ids += "," + selectedEstadoCivil.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdAlfabeto() {
        String ids = null;
        if (selectedAlfabeto != null) {
            ids = "";
            for (int i = 0; i < selectedAlfabeto.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedAlfabeto.get(i).toString();
                } else {
                    ids += "," + selectedAlfabeto.get(i).toString();
                }
            }
        }
        return ids;
    }

    public Map<String, String> getListEstadoCivil() {
        return listEstadoCivil;
    }

    public void setListEstadoCivil(Map<String, String> listEstadoCivil) {
        this.listEstadoCivil = listEstadoCivil;
    }

    public List getSelectedEstadoCivil() {
        return selectedEstadoCivil;
    }

    public void setSelectedEstadoCivil(List selectedEstadoCivil) {
        this.selectedEstadoCivil = selectedEstadoCivil;
    }

    public Map<String, Integer> getListCidadesSocio() {
        return listCidadesSocio;
    }

    public void setListCidadesSocio(Map<String, Integer> listCidadesSocio) {
        this.listCidadesSocio = listCidadesSocio;
    }

    public List getSelectedCidadesSocio() {
        return selectedCidadesSocio;
    }

    public void setSelectedCidadesSocio(List selectedCidadesSocio) {
        this.selectedCidadesSocio = selectedCidadesSocio;
    }

    public Map<String, Integer> getListTipoCobranca() {
        return listTipoCobranca;
    }

    public void setListTipoCobranca(Map<String, Integer> listTipoCobranca) {
        this.listTipoCobranca = listTipoCobranca;
    }

    public List getSelectedTipoCobranca() {
        return selectedTipoCobranca;
    }

    public void setSelectedTipoCobranca(List selectedTipoCobranca) {
        this.selectedTipoCobranca = selectedTipoCobranca;
    }

    public Map<String, Integer> getListCidadesEmpresa() {
        return listCidadesEmpresa;
    }

    public void setListCidadesEmpresa(Map<String, Integer> listCidadesEmpresa) {
        this.listCidadesEmpresa = listCidadesEmpresa;
    }

    public List getSelectedCidadesEmpresa() {
        return selectedCidadesEmpresa;
    }

    public void setSelectedCidadesEmpresa(List selectedCidadesEmpresa) {
        this.selectedCidadesEmpresa = selectedCidadesEmpresa;
    }

    public Map<String, Integer> getListMeses() {
        return listMeses;
    }

    public void setListMeses(Map<String, Integer> listMeses) {
        this.listMeses = listMeses;
    }

    public List getSelectedMeses() {
        return selectedMeses;
    }

    public void setSelectedMeses(List selectedMeses) {
        this.selectedMeses = selectedMeses;
    }

    public Map<String, Integer> getListParentesco() {
        return listParentesco;
    }

    public void setListParentesco(Map<String, Integer> listParentesco) {
        this.listParentesco = listParentesco;
    }

    public List getSelectedParentesco() {
        return selectedParentesco;
    }

    public void setSelectedParentesco(List selectedParentesco) {
        this.selectedParentesco = selectedParentesco;
    }

    public Map<String, Integer> getListGrupoCategoria() {
        return listGrupoCategoria;
    }

    public void setListGrupoCategoria(Map<String, Integer> listGrupoCategoria) {
        this.listGrupoCategoria = listGrupoCategoria;
    }

    public List getSelectedGrupoCategoria() {
        return selectedGrupoCategoria;
    }

    public void setSelectedGrupoCategoria(List selectedGrupoCategoria) {
        this.selectedGrupoCategoria = selectedGrupoCategoria;
    }

    public Map<String, Integer> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(Map<String, Integer> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public List getSelectedCategoria() {
        return selectedCategoria;
    }

    public void setSelectedCategoria(List selectedCategoria) {
        this.selectedCategoria = selectedCategoria;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public List<DateFilters> getListDateFilters() {
        return listDateFilters;
    }

    public void setListDateFilters(List<DateFilters> listDateFilters) {
        this.listDateFilters = listDateFilters;
    }

    public List<SelectItem> getListDates() {
        return listDates;
    }

    public void setListDates(List<SelectItem> listDates) {
        this.listDates = listDates;
    }

    public String getDateItemDescription(String title) {
        for (int x = 0; x < listDates.size(); x++) {
            if (title.equals(listDates.get(x).getValue().toString())) {
                return listDates.get(x).getLabel();
            }
        }
        return "";
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Boolean isFiltroSelecionado() {
        List<Filters> list = new ArrayList();
        list.addAll(filtersSocio);
        list.addAll(filtersEmpresa);
        list.addAll(filtersFinanceiro);
        for (Filters list1 : list) {
            if (list1.getActive()) {
                return true;
            }
        }
        return false;
    }

    // REMOVIDOS
//    private List<DataObject> listaMenuRSocial = new ArrayList();
//    private boolean booMatricula = false;
//    private boolean booIdade = false;
//    private boolean booGrupoCategoria = false;
//    private boolean booSexo = false;
//    private boolean booGrau = true;
//    private boolean booFotos = false;
//    private boolean booCarteirinha = false;
//    private boolean booTipoCobranca = false;
//    private boolean booCidadeSocio = false;
//    private boolean booCidadeEmpresa = false;
//    private boolean booAniversario = false;
//    private boolean booData = false;
//    private boolean booVotante = false;
//    private boolean booEmail = false;
//    private boolean booTelefone = false;
//    private boolean booEstadoCivil = false;
//    private boolean booEmpresa = false;
//    private boolean booServicos = false;
//    private Boolean situacao = false;
//    private boolean booBiometria = false;
//    private boolean booDescontoFolha = false;
//    private boolean booStatusSocio = false;
    // private List<GrupoCategoria> listGrupoCategoria = new ArrayList();
    // private List<Categoria> listCategoria = new ArrayList();
    // private List<Parentesco> listParentesco = new ArrayList();
    //    private boolean chkGrupo = true;
//    private boolean chkCategoria = true;
//    private boolean chkGrau = false;
//    private boolean chkTipoCobranca = false;
//    private boolean chkCidadesSocio = false;
//    private boolean chkCidadesEmpresa = false;
//    private boolean chkMeses = false;
//    private boolean chkTodos = false;
//    private boolean chkSocios = false;
//    private boolean chkEscola = false;
//    private boolean chkAcademia = false;
//    private boolean chkConvênioMedico = false;
//    private boolean chkServicos = false;
//    private boolean chkEmpresa = false;
    // private List<DataObject> listaCidadesSocio = new ArrayList();
    // private List<DataObject> listaCidadesEmpresa = new ArrayList();
    // private List<DataObject> listaMeses = new ArrayList();
    // private List listaServicos = new ArrayList();
    // private String selectAccordion = "simples";        
//    public List<DataObject> getListaMenuRSocial() {
//        if (listaMenuRSocial.isEmpty()) {
//            /* 00 */ listaMenuRSocial.add(new DataObject("Número da Matricula ", "Editar", true, null, null, null));
//            /* 01 */ listaMenuRSocial.add(new DataObject("Idade ", "Editar", true, null, null, null));
//            /* 02 */ listaMenuRSocial.add(new DataObject("Grupo / Categoria ", "Editar", true, null, null, null));
//            /* 03 */ listaMenuRSocial.add(new DataObject("Sexo ", "Editar", true, null, null, null));
//            /* 04 */ listaMenuRSocial.add(new DataObject("Grau ", "Remover", true, null, null, null));
//            /* 05 */ listaMenuRSocial.add(new DataObject("Fotos ", "Editar", true, null, null, null));
//            /* 06 */ listaMenuRSocial.add(new DataObject("Carteirinha ", "Editar", true, null, null, null));
//            /* 07 */ listaMenuRSocial.add(new DataObject("Tipo de Cobrança ", "Editar", true, null, null, null));
//            /* 08 */ listaMenuRSocial.add(new DataObject("Cidade do Sócio ", "Editar", true, null, null, null));
//            /* 09 */ listaMenuRSocial.add(new DataObject("Cidade do Empresa ", "Editar", true, null, null, null));
//            /* 10 */ listaMenuRSocial.add(new DataObject("Aniversário ", "Editar", true, null, null, null));
//            /* 11 */ listaMenuRSocial.add(new DataObject("Datas ", "Editar", true, null, null, null));
//            /* 12 */ listaMenuRSocial.add(new DataObject("Votante ", "Editar", true, null, null, null));
//            /* 13 */ listaMenuRSocial.add(new DataObject("Email ", "Editar", true, null, null, null));
//            /* 14 */ listaMenuRSocial.add(new DataObject("Telefone ", "Editar", true, null, null, null));
//            /* 15 */ listaMenuRSocial.add(new DataObject("Estado Civil ", "Editar", true, null, null, null));
//            /* 16 */ listaMenuRSocial.add(new DataObject("Empresas ", "Editar", true, null, null, null));
//            /* 17 */ listaMenuRSocial.add(new DataObject("Situação ", "Editar", true, null, null, null));
//            /* 18 */ listaMenuRSocial.add(new DataObject("Biometria ", "Editar", true, null, null, null));
//            /* 19 */ listaMenuRSocial.add(new DataObject("Desconto Folha ", "Editar", true, null, null, null));
//            /* 20 */ listaMenuRSocial.add(new DataObject("Serviços ", "Editar", true, null, null, null));
//            /* 21 */ listaMenuRSocial.add(new DataObject("Desconto Social ", "Editar", true, null, null, null));
//            /* 22 */ listaMenuRSocial.add(new DataObject("Status", "Editar", false, null, null, null));
//        }
//        return listaMenuRSocial;
//    }
//
//    public void setListaMenuRSocial(List<DataObject> listaMenuRSocial) {
//        this.listaMenuRSocial = listaMenuRSocial;
//    }
//
//    public void listener(String tcase) {
//        switch (tcase) {
//            case "empresas":
//                for (int i = 0; i < listaMenuRSocial.size(); i++) {
//                    if (i == 22) {
//                        if (idRelatorio == 12 || idRelatorio == 16 || idRelatorio == 40) {
//                            statusSocio = "";
//                            // booStatusSocio = false;
//                            listaMenuRSocial.get(i).setArgumento2(true);
//                        } else {
//                            listaMenuRSocial.get(i).setArgumento2(false);
//                        }
//                    }
//                }
//                break;
//        }
//    }
//    public String inIdGrupoCategoria() {
//        String ids = "";
//        for (int i = 0; i < listGrupoCategoria.size(); i++) {
//            if (listGrupoCategoria.get(i).getSelected()) {
//                if (ids.isEmpty()) {
//                    ids = "" + listGrupoCategoria.get(i).getId();
//                } else {
//                    ids += ", " + listGrupoCategoria.get(i).getId();
//                }
//            }
//        }
//        return ids;
//    }
//
//    public String inIdCategoria() {
//        String ids = "";
//        for (Categoria listCategoria1 : listCategoria) {
//            if (listCategoria1.getSelected()) {
//                if (ids.isEmpty()) {
//                    ids = "" + listCategoria1.getId();
//                } else {
//                    ids += ", " + listCategoria1.getId();
//                }
//            }
//        }
//        return ids;
//    }
//
//    public String inIdParentesco() {
//        String ids = null;
//        for (Parentesco listParentesco1 : listParentesco) {
//            if (listParentesco1.getSelected()) {
//                ids = "" + listParentesco1.getId();
//            }
//        }
//        return ids;
//    }
//    public boolean validaFiltro() {
//        return !(!booMatricula
//                && !booIdade
//                && !booGrupoCategoria
//                && !booSexo
//                && !booGrau
//                && !booFotos
//                && !booCarteirinha
//                && !booTipoCobranca
//                && !booCidadeSocio
//                && !booCidadeEmpresa
//                && !booAniversario
//                && !booData
//                && !booVotante
//                && !booEmail
//                && !booTelefone
//                && !booEstadoCivil
//                && !booEmpresa
//                && !situacao
//                && !booBiometria
//                && !booDescontoFolha
//                && !booServicos
//                && !booDescontoSocial
//                && !booStatusSocio);
//    }
//    public String getSelectAccordion() {
//        return selectAccordion;
//    }
//
//    public void setSelectAccordion(String selectAccordion) {
//        this.selectAccordion = selectAccordion;
//    }
//    public boolean isBooMatricula() {
//        return booMatricula;
//    }
//
//    public void setBooMatricula(boolean booMatricula) {
//        this.booMatricula = booMatricula;
//    }
//
//    public boolean isBooIdade() {
//        return booIdade;
//    }
//
//    public void setBooIdade(boolean booIdade) {
//        this.booIdade = booIdade;
//    }
//
//    public boolean isBooGrupoCategoria() {
//        return booGrupoCategoria;
//    }
//
//    public void setBooGrupoCategoria(boolean booGrupoCategoria) {
//        this.booGrupoCategoria = booGrupoCategoria;
//    }
//    public List<GrupoCategoria> getListGrupoCategoria() {
//        if (listGrupoCategoria.isEmpty()) {
//            List<GrupoCategoria> list = (List<GrupoCategoria>) new Dao().list(new GrupoCategoria(), true);
//            for (GrupoCategoria gc : list) {
//                gc.setSelected(true);
//                listGrupoCategoria.add(gc);
//            }
//        }
//        return listGrupoCategoria;
//    }
//
//    public void setListGrupoCategoria(List<GrupoCategoria> listGrupoCategoria) {
//        this.listGrupoCategoria = listGrupoCategoria;
//    }
//    public void marcarGrupoCatregoria() {
//        for (int i = 0; i < listGrupoCategoria.size(); i++) {
//            listGrupoCategoria.get(i).setSelected(chkGrupo);
//        }
//        listCategoria.clear();
//    }
//
//    public void marcarUmGrupoCategoria() {
//        listCategoria.clear();
//    }
//
//    public void marcarCategorias() {
//        for (int i = 0; i < listCategoria.size(); i++) {
//            listCategoria.get(i).setSelected(chkCategoria);
//        }
//    }
//
//    public boolean isBooSexo() {
//        return booSexo;
//    }
//
//    public void setBooSexo(boolean booSexo) {
//        this.booSexo = booSexo;
//    }
//
//    public boolean isBooGrau() {
//        return booGrau;
//    }
//
//    public void setBooGrau(boolean booGrau) {
//        this.booGrau = booGrau;
//    }
//
//    public boolean isBooFotos() {
//        return booFotos;
//    }
//
//    public void setBooFotos(boolean booFotos) {
//        this.booFotos = booFotos;
//    }
//
//    public boolean isBooCarteirinha() {
//        return booCarteirinha;
//    }
//
//    public void setBooCarteirinha(boolean booCarteirinha) {
//        this.booCarteirinha = booCarteirinha;
//    }
//
//    public boolean isBooTipoCobranca() {
//        return booTipoCobranca;
//    }
//
//    public void setBooTipoCobranca(boolean booTipoCobranca) {
//        this.booTipoCobranca = booTipoCobranca;
//    }
//
//    public boolean isBooCidadeSocio() {
//        return booCidadeSocio;
//    }
//
//    public void setBooCidadeSocio(boolean booCidadeSocio) {
//        this.booCidadeSocio = booCidadeSocio;
//    }
//
//    public boolean isBooCidadeEmpresa() {
//        return booCidadeEmpresa;
//    }
//
//    public void setBooCidadeEmpresa(boolean booCidadeEmpresa) {
//        this.booCidadeEmpresa = booCidadeEmpresa;
//    }
//
//    public boolean isBooAniversario() {
//        return booAniversario;
//    }
//
//    public void setBooAniversario(boolean booAniversario) {
//        this.booAniversario = booAniversario;
//    }
//
//    public boolean isBooData() {
//        return booData;
//    }
//
//    public void setBooData(boolean booData) {
//        this.booData = booData;
//    }
//    public boolean isBooVotante() {
//        return booVotante;
//    }
//
//    public void setBooVotante(boolean booVotante) {
//        this.booVotante = booVotante;
//    }
//
//    public boolean isBooEmail() {
//        return booEmail;
//    }
//
//    public void setBooEmail(boolean booEmail) {
//        this.booEmail = booEmail;
//    }
//
//    public boolean isBooTelefone() {
//        return booTelefone;
//    }
//
//    public void setBooTelefone(boolean booTelefone) {
//        this.booTelefone = booTelefone;
//    }
    //
//    public boolean isChkTodos() {
//        return chkTodos;
//    }
//
//    public void setChkTodos(boolean chkTodos) {
//        this.chkTodos = chkTodos;
//    }
//
//    public boolean isChkSocios() {
//        return chkSocios;
//    }
//
//    public void setChkSocios(boolean chkSocios) {
//        this.chkSocios = chkSocios;
//    }
//
//    public boolean isChkEscola() {
//        return chkEscola;
//    }
//
//    public void setChkEscola(boolean chkEscola) {
//        this.chkEscola = chkEscola;
//    }
//
//    public boolean isChkAcademia() {
//        return chkAcademia;
//    }
//
//    public void setChkAcademia(boolean chkAcademia) {
//        this.chkAcademia = chkAcademia;
//    }
//
//    public boolean isChkConvênioMedico() {
//        return chkConvênioMedico;
//    }
//
//    public void setChkConvênioMedico(boolean chkConvênioMedico) {
//        this.chkConvênioMedico = chkConvênioMedico;
//    }
//
//    public boolean isChkServicos() {
//        return chkServicos;
//    }
//
//    public void setChkServicos(boolean chkServicos) {
//        this.chkServicos = chkServicos;
//    }
    //
//    public boolean isChkTipoCobranca() {
//        return chkTipoCobranca;
//    }
//
//    public void setChkTipoCobranca(boolean chkTipoCobranca) {
//        this.chkTipoCobranca = chkTipoCobranca;
//    }
//
//    public boolean isChkCidadesSocio() {
//        return chkCidadesSocio;
//    }
//
//    public void setChkCidadesSocio(boolean chkCidadesSocio) {
//        this.chkCidadesSocio = chkCidadesSocio;
//    }
//
//    public boolean isChkCidadesEmpresa() {
//        return chkCidadesEmpresa;
//    }
//
//    public void setChkCidadesEmpresa(boolean chkCidadesEmpresa) {
//        this.chkCidadesEmpresa = chkCidadesEmpresa;
//    }
//
//    public boolean isChkMeses() {
//        return chkMeses;
//    }
//
//    public void setChkMeses(boolean chkMeses) {
//        this.chkMeses = chkMeses;
//    }
//    public boolean isChkGrau() {
//        return chkGrau;
//    }
//
//    public void setChkGrau(boolean chkGrau) {
//        this.chkGrau = chkGrau;
//    }
    //
//    public boolean isChkGrupo() {
//        return chkGrupo;
//    }
//
//    public void setChkGrupo(boolean chkGrupo) {
//        this.chkGrupo = chkGrupo;
//    }
//
//    public boolean isChkCategoria() {
//        return chkCategoria;
//    }
//
//    public void setChkCategoria(boolean chkCategoria) {
//        this.chkCategoria = chkCategoria;
//    }
//
//    public String getMatriculaInicial() {
//        return matriculaInicial;
//    }
//    public List<DataObject> getListaTipoCobranca() {
//        if (listaTipoCobranca.isEmpty()) {
//            FTipoDocumentoDao db = new FTipoDocumentoDao();
//            List select = new ArrayList();
//            select.add(new Dao().find(new FTipoDocumento(), 2));
//            select.addAll(db.pesquisaListaTipoExtrato());
//            for (int i = 0; i < select.size(); i++) {
//                listaTipoCobranca.add(new DataObject(false, (FTipoDocumento) select.get(i)));
//            }
//        }
//        return listaTipoCobranca;
//    }
//
//    public void setListaTipoCobranca(List<DataObject> listaTipoCobranca) {
//        this.listaTipoCobranca = listaTipoCobranca;
//    }
//
//    public void marcarTipos() {
//        for (int i = 0; i < listaTipoCobranca.size(); i++) {
//            listaTipoCobranca.get(i).setArgumento0(chkTipoCobranca);
//        }
//    }
//    public void marcarGrau() {
//        for (int i = 0; i < listParentesco.size(); i++) {
//            listParentesco.get(i).setSelected(chkGrau);
//        }
//    }
//    public List getListaCidadesSocio() {
//        if (listaCidadesSocio.isEmpty()) {
//            RelatorioSociosDao db = new RelatorioSociosDao();
//            List select = new ArrayList();
//            select.addAll(db.listaCidadeDoSocio());
//            for (int i = 0; i < select.size(); i++) {
//                listaCidadesSocio.add(new DataObject(false, ((Cidade) select.get(i))));
//            }
//        }
//        return listaCidadesSocio;
//    }
//
//    public void setListaCidadesSocio(List listaCidadesSocio) {
//        this.listaCidadesSocio = listaCidadesSocio;
//    }
//
//    public void marcarCidadesSocio() {
//        for (int i = 0; i < listaCidadesSocio.size(); i++) {
//            ((DataObject) listaCidadesSocio.get(i)).setArgumento0(chkCidadesSocio);
//        }
//    }
//    public List getListaCidadesEmpresa() {
//        if (listaCidadesEmpresa.isEmpty()) {
//            RelatorioSociosDao db = new RelatorioSociosDao();
//            List select = new ArrayList();
//            select.addAll(db.listaCidadeDaEmpresa());
//            for (int i = 0; i < select.size(); i++) {
//                listaCidadesEmpresa.add(new DataObject(false, ((Cidade) select.get(i))));
//            }
//        }
//        return listaCidadesEmpresa;
//    }
//
//    public void setListaCidadesEmpresa(List listaCidadesEmpresa) {
//        this.listaCidadesEmpresa = listaCidadesEmpresa;
//    }
//
//    public void marcarCidadesEmpresa() {
//        for (int i = 0; i < listaCidadesEmpresa.size(); i++) {
//            listaCidadesEmpresa.get(i).setArgumento0(chkCidadesEmpresa);
//        }
//    }
//    public List<DataObject> getListaMeses() {
//        if (listaMeses.isEmpty()) {
//            listaMeses.add(new DataObject(false, "Janeiro", "01", null, null, null));
//            listaMeses.add(new DataObject(false, "Fevereiro", "02", null, null, null));
//            listaMeses.add(new DataObject(false, "Março", "03", null, null, null));
//            listaMeses.add(new DataObject(false, "Abril", "04", null, null, null));
//            listaMeses.add(new DataObject(false, "Maio", "05", null, null, null));
//            listaMeses.add(new DataObject(false, "Junho", "06", null, null, null));
//            listaMeses.add(new DataObject(false, "Julho", "07", null, null, null));
//            listaMeses.add(new DataObject(false, "Agosto", "08", null, null, null));
//            listaMeses.add(new DataObject(false, "Setembro", "09", null, null, null));
//            listaMeses.add(new DataObject(false, "Outubro", "10", null, null, null));
//            listaMeses.add(new DataObject(false, "Novembro", "11", null, null, null));
//            listaMeses.add(new DataObject(false, "Dezembro", "12", null, null, null));
//        }
//        return listaMeses;
//    }
//
//    public void setListaMeses(List listaMeses) {
//        this.listaMeses = listaMeses;
//    }
//    public void marcarMeses() {
//        for (int i = 0; i < listaMeses.size(); i++) {
//            ((DataObject) listaMeses.get(i)).setArgumento0(chkMeses);
//        }
//    }
//
//    public List getListaServicos() {
//        if (listaServicos.isEmpty()) {
//            RelatorioSociosDao db = new RelatorioSociosDao();
//            List select = new ArrayList();
//            if (chkSocios) {
//                select.addAll(db.listaSPSocios());
//            }
//            if (chkConvênioMedico) {
//                select.addAll(db.listaSPConvenioMedico());
//            }
//            if (chkAcademia) {
//                select.addAll(db.listaSPAcademia());
//            }
//            if (chkEscola) {
//                select.addAll(db.listaSPEscola());
//            }
//            for (int i = 0; i < select.size(); i++) {
//                listaServicos.add(new DataObject(false, (Servicos) select.get(i)));
//            }
//        }
//        return listaServicos;
//    }
//
//    public void setListaServicos(List listaServicos) {
//        this.listaServicos = listaServicos;
//    }
//
//    public void marcarServicos() {
//        for (int i = 0; i < listaServicos.size(); i++) {
//            ((DataObject) listaServicos.get(i)).setArgumento0(chkServicos);
//        }
//    }
//
//    public void marcarInscritos() {
//        chkSocios = chkTodos;
//        chkAcademia = chkTodos;
//        chkConvênioMedico = chkTodos;
//        chkEscola = chkTodos;
//        refreshFormServicos();
//    }
//
//    public void refreshForm() {
//    }
//    public void refreshFormServicos() {
//        listaServicos.clear();
//    }
//
//    public boolean isBooEstadoCivil() {
//        return booEstadoCivil;
//    }
//
//    public void setBooEstadoCivil(boolean booEstadoCivil) {
//        this.booEstadoCivil = booEstadoCivil;
//    }
//
//    public List<Parentesco> getListParentesco() {
//        if (listParentesco.isEmpty()) {
//            List<Parentesco> list = new Dao().list(new Parentesco(), true);
//            for (Parentesco p : list) {
//                if (p.getParentesco().equals("TITULAR")) {
//                    p.setSelected(true);
//                } else {
//                    p.setSelected(false);
//                }
//                listParentesco.add(p);
//            }
//        }
//        return listParentesco;
//    }
//
//    public void setListParentesco(List<Parentesco> listParentesco) {
//        this.listParentesco = listParentesco;
//    }
//    public boolean isChkEmpresa() {
//        return chkEmpresa;
//    }
//
//    public void setChkEmpresa(boolean chkEmpresa) {
//        this.chkEmpresa = chkEmpresa;
//    }
//
//    public boolean isBooEmpresa() {
//        return booEmpresa;
//    }
//
//    public void setBooEmpresa(boolean booEmpresa) {
//        this.booEmpresa = booEmpresa;
//    }
//    public Boolean getSituacao() {
//        return situacao;
//    }
//
//    public void setSituacao(Boolean situacao) {
//        this.situacao = situacao;
//    }
    //
//    public boolean isBooBiometria() {
//        return booBiometria;
//    }
//
//    public void setBooBiometria(boolean booBiometria) {
//        this.booBiometria = booBiometria;
//    }
    //
//    public boolean isBooDescontoFolha() {
//        return booDescontoFolha;
//    }
//
//    public void setBooDescontoFolha(boolean booDescontoFolha) {
//        this.booDescontoFolha = booDescontoFolha;
//    }
    //
//    public boolean isBooServicos() {
//        return booServicos;
//    }
//
//    public void setBooServicos(boolean booServicos) {
//        this.booServicos = booServicos;
//    }    
    //    public boolean isBooStatusSocio() {
//        return booStatusSocio;
//    }
//
//    public void setBooStatusSocio(boolean booStatusSocio) {
//        this.booStatusSocio = booStatusSocio;
//    }
    //    private String dataCadastro = "";
//    private String dataCadastroFim = "";
//    private String dataRecadastro = "";
//    private String dataRecadastroFim = "";
//    private String dataDemissao = "";
//    private String dataDemissaoFim = "";
//    private String dataAdmissaoSocio = "";
//    private String dataAdmissaoSocioFim = "";
//    private String dataAdmissaoEmpresa = "";
//    private String dataAdmissaoEmpresaFim = "";
//    private String dataAposetandoria = "";
//    private String dataAposetandoriaFim = "";
//    private String dataAtualicacao = "";
//    private String dataAtualicacaoFim = "";    
    // private List<DataObject> listaTipoCobranca = new ArrayList();
//    public void editarOpcao(int index) {
//        if (listaMenuRSocial.get(index).getArgumento1().equals("Remover")) {
//            listaMenuRSocial.get(index).setArgumento1("Editar");
//        } else {
//            listaMenuRSocial.get(index).setArgumento1("Remover");
//        }
//
//        if (index == 0) {
//            booMatricula = !booMatricula;
//            if (booMatricula) {
//                matriculaInicial = 0;
//                matriculaFinal = 0;
//            } else {
//                matriculaInicial = null;
//                matriculaFinal = null;
//            }
//        } else if (index == 1) {
//            booIdade = !booIdade;
//            if (booIdade) {
//                idadeInicial = 0;
//                idadeFinal = 0;
//            } else {
//                idadeInicial = null;
//                idadeFinal = null;
//            }
//        } else if (index == 2) {
//            booGrupoCategoria = !booGrupoCategoria;
//            if (booGrupoCategoria) {
//                listGrupoCategoria.clear();
//                listCategoria.clear();
//                getListGrupoCategoria();
//                getListCategoria();
//            } else {
//                listGrupoCategoria.clear();
//                listCategoria.clear();
//            }
//        } else if (index == 3) {
//            booSexo = !booSexo;
//            if (booSexo) {
//                tipoSexo = "M";
//            } else {
//                tipoSexo = "";
//            }
//        } else if (index == 4) {
//            booGrau = !booGrau;
//            if (booGrau) {
//                listParentesco.clear();
//                getListParentesco();
//            } else {
//                listParentesco.clear();
//            }
//        } else if (index == 5) {
//            booFotos = !booFotos;
//            if (booFotos) {
//                tipoFotos = "com";
//            } else {
//                tipoFotos = "";
//            }
//        } else if (index == 6) {
//            booCarteirinha = !booCarteirinha;
//            if (booCarteirinha) {
//                tipoCarteirinha = "com";
//            } else {
//                tipoCarteirinha = "";
//            }
//        } else if (index == 7) {
//            booTipoCobranca = !booTipoCobranca;
//        } else if (index == 8) {
//            booCidadeSocio = !booCidadeSocio;
//        } else if (index == 9) {
//            booCidadeEmpresa = !booCidadeEmpresa;
//        } else if (index == 10) {
//            booAniversario = !booAniversario;
//            if (booAniversario) {
//                listaMeses.clear();
//                getListaMeses();
//                diaInicial = 1;
//                diaFinal = 31;
//                ordemAniversario = false;
//            } else {
//                listaMeses.clear();
//                diaInicial = 0;
//                diaFinal = 0;
//                ordemAniversario = false;
//            }
//        } else if (index == 11) {
//            booData = !booData;
//        } else if (index == 12) {
//            booVotante = !booVotante;
//        } else if (index == 13) {
//            booEmail = !booEmail;
//        } else if (index == 14) {
//            booTelefone = !booTelefone;
//        } else if (index == 15) {
//            booEstadoCivil = !booEstadoCivil;
//            if (booEstadoCivil) {
//                tipoEstadoCivil = "Solteiro(a)";
//            } else {
//                tipoEstadoCivil = "";
//            }
//        } else if (index == 16) {
//            booEmpresa = !booEmpresa;
//            if (!booEmpresa) {
//                minQtdeFuncionario = null;
//                maxQtdeFuncionario = null;
//                empresa = new Juridica();
//            }
//        } else if (index == 17) {
//            situacao = !situacao;
//            if (situacao) {
//                tipoCarencia = "todos";
//                situacaoString = "adimplente";
//                carenciaDias = 0;
//            } else {
//                situacaoString = null;
//                tipoCarencia = "todos";
//                carenciaDias = null;
//            }
//        } else if (index == 18) {
//            booBiometria = !booBiometria;
//        } else if (index == 19) {
//            booDescontoFolha = !booDescontoFolha;
//        } else if (index == 20) {
//            booServicos = !booServicos;
//            if (booServicos) {
//                loadGrupoFinanceiro();
//                loadSubGrupoFinanceiro();
//                contemServicos = false;
//            } else {
//                listSubGrupoFinanceiro = null;
//                selectedSubGrupoFinanceiro = null;
//                listSubGrupoFinanceiro = null;
//                selectedSubGrupoFinanceiro = new ArrayList();
//                listServicos = new LinkedHashMap<>();
//                selectedServicos = new ArrayList<>();
//                contemServicos = null;
//            }
//        } else if (index == 21) {
//            booDescontoSocial = !booDescontoSocial;
//            if (booDescontoSocial) {
//                loadDescontoSocial();
//            } else {
//                listDescontoSocial = null;
//                selectedDescontoSocial = null;
//            }
//        } else if (index == 22) {
//            booStatusSocio = !booStatusSocio;
//            if (booDescontoSocial) {
//                statusSocio = "socio";
//            }
//        }
//    public String getDataDemissao() {
//        return dataDemissao;
//    }
//
//    public void setDataDemissao(String dataDemissao) {
//        this.dataDemissao = dataDemissao;
//    }
//    }
    public List<Juridica> getListEmpresas() {
        return listEmpresas;
    }

    public void setListEmpresas(List<Juridica> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public Pessoa getSocio() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            socio = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return socio;
    }

    public void setSocio(Pessoa socio) {
        this.socio = socio;
    }

    public List<Pessoa> getListSocios() {
        return listSocios;
    }

    public void setListSocios(List<Pessoa> listSocios) {
        this.listSocios = listSocios;
    }

    public Map<String, String> getListAlfabeto() {
        return listAlfabeto;
    }

    public void setListAlfabeto(Map<String, String> listAlfabeto) {
        this.listAlfabeto = listAlfabeto;
    }

    public List getSelectedAlfabeto() {
        return selectedAlfabeto;
    }

    public void setSelectedAlfabeto(List selectedAlfabeto) {
        this.selectedAlfabeto = selectedAlfabeto;
    }

    public String getTipoSuspencao() {
        return tipoSuspencao;
    }

    public void setTipoSuspencao(String tipoSuspencao) {
        this.tipoSuspencao = tipoSuspencao;
    }

    public String getTipoOposicao() {
        return tipoOposicao;
    }

    public void setTipoOposicao(String tipoOposicao) {
        this.tipoOposicao = tipoOposicao;
    }

    public List<SelectItem> getListGroups() {
        return listGroups;
    }

    public void setListGroups(List<SelectItem> listGroups) {
        this.listGroups = listGroups;
    }

    public String getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(String selectedGroups) {
        this.selectedGroups = selectedGroups;
    }
}
