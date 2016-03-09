package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.db.CategoriaDB;
import br.com.rtools.associativo.db.CategoriaDBToplink;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.GrupoFinanceiro;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.SubGrupoFinanceiro;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.financeiro.db.FTipoDocumentoDB;
import br.com.rtools.financeiro.db.FTipoDocumentoDBToplink;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.impressao.ParametroSocios;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioSociosDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

@ManagedBean
@SessionScoped
public class RelatorioSociosBean implements Serializable {

    private String dataCadastro = "";
    private String dataCadastroFim = "";
    private String dataRecadastro = "";
    private String dataRecadastroFim = "";
    private String dataDemissao = "";
    private String dataDemissaoFim = "";
    private String dataAdmissaoSocio = "";
    private String dataAdmissaoSocioFim = "";
    private String dataAdmissaoEmpresa = "";
    private String dataAdmissaoEmpresaFim = "";
    private String dataAposetandoria = "";
    private String dataAposetandoriaFim = "";
    private String dataAtualicacao = "";
    private String dataAtualicacaoFim = "";
    private String tipoEleicao = "todos";
    private String tipoSexo = "";
    private String tipoCarteirinha = "";
    private String tipoSuspensos = "todos";
    private String tipoFotos = "";
    private String tipoDescontoGeracao = "todos";
    private String tipoEmpresas = "todas";
    private String tipoOrdem = "nome";
    private String tipoEmail = "todos";
    private String tipoTelefone = "todos";
    private String tipoEstadoCivil = "Solteiro(a)";
    private String tipoBiometria = "com";
    private String tipoDescontoFolha = "com";
    private boolean chkGrupo = true;
    private boolean chkCategoria = true;
    private boolean chkGrau = false;
    private boolean chkTipoCobranca = false;
    private boolean chkCidadesSocio = false;
    private boolean chkCidadesEmpresa = false;
    private boolean chkMeses = false;
    private boolean chkTodos = false;
    private boolean chkSocios = false;
    private boolean chkEscola = false;
    private boolean chkAcademia = false;
    private boolean chkConvênioMedico = false;
    private boolean chkServicos = false;
    private boolean chkEmpresa = false;
    private Integer idEmpresas = null;
    private Integer idDias = 0;
    private Integer matriculaInicial = 0;
    private Integer matriculaFinal = 9999999;
    private Integer idadeInicial = 0;
    private Integer idadeFinal = 500;
    private Integer diaInicial = 1;
    private Integer diaFinal = 31;
    private Integer idRelatorioOrdem = null;
    private Integer idRelatorio = null;
    private List<DataObject> listaTipoCobranca = new ArrayList();
    private List<DataObject> listaCidadesSocio = new ArrayList();
    private List<DataObject> listaCidadesEmpresa = new ArrayList();
    private List<DataObject> listaMeses = new ArrayList();
    private List listaServicos = new ArrayList();
    private String selectAccordion = "simples";
    private List<DataObject> listaMenuRSocial = new ArrayList();
    private List<GrupoCategoria> listGrupoCategoria = new ArrayList();
    private List<Categoria> listCategoria = new ArrayList();
    private List<Parentesco> listParentesco = new ArrayList();
    private List<SelectItem> listRelatorio = new ArrayList();
    private List<SelectItem> listRelatorioOrdem = new ArrayList();
    private boolean booMatricula = false;
    private boolean booIdade = false;
    private boolean booGrupoCategoria = false;
    private boolean booSexo = false;
    private boolean booGrau = true;
    private boolean booFotos = false;
    private boolean booCarteirinha = false;
    private boolean booTipoCobranca = false;
    private boolean booCidadeSocio = false;
    private boolean booCidadeEmpresa = false;
    private boolean booAniversario = false;
    private boolean booData = false;
    private boolean booVotante = false;
    private boolean booEmail = false;
    private boolean booTelefone = false;
    private boolean booEstadoCivil = false;
    private boolean booEmpresa = false;
    private boolean booServicos = false;
    private Boolean situacao = false;
    private boolean booBiometria = false;
    private boolean booDescontoFolha = false;
    private String situacaoString = null;
    private Boolean compactar = false;
    private Integer carenciaDias = null;
    private String tipoCarencia = "todos";
    private Boolean enableFolha = false;
    private Boolean porFolha = false;
    private Juridica empresa = new Juridica();
    private Integer minQtdeFuncionario = null;
    private Integer maxQtdeFuncionario = null;
    private boolean ordemAniversario = false;
    private Boolean contemServicos = null;
    private Boolean booDescontoSocial = false;

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

    public RelatorioSociosBean() {
        loadFilters();
    }

    public void clear() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public boolean validaFiltro() {
        return !(!booMatricula
                && !booIdade
                && !booGrupoCategoria
                && !booSexo
                && !booGrau
                && !booFotos
                && !booCarteirinha
                && !booTipoCobranca
                && !booCidadeSocio
                && !booCidadeEmpresa
                && !booAniversario
                && !booData
                && !booVotante
                && !booEmail
                && !booTelefone
                && !booEstadoCivil
                && !booEmpresa
                && !situacao
                && !booBiometria
                && !booDescontoFolha
                && !booServicos
                && !booDescontoSocial);
    }

    public void editarOpcao(int index) {
        if (listaMenuRSocial.get(index).getArgumento1().equals("Remover")) {
            listaMenuRSocial.get(index).setArgumento1("Editar");
        } else {
            listaMenuRSocial.get(index).setArgumento1("Remover");
        }

        if (index == 0) {
            booMatricula = !booMatricula;
            if (booMatricula) {
                matriculaInicial = 0;
                matriculaFinal = 0;
            } else {
                matriculaInicial = null;
                matriculaFinal = null;
            }
        } else if (index == 1) {
            booIdade = !booIdade;
            if (booIdade) {
                idadeInicial = 0;
                idadeFinal = 0;
            } else {
                idadeInicial = null;
                idadeFinal = null;
            }
        } else if (index == 2) {
            booGrupoCategoria = !booGrupoCategoria;
            if (booGrupoCategoria) {
                listGrupoCategoria.clear();
                listCategoria.clear();
                getListGrupoCategoria();
                getListCategoria();
            } else {
                listGrupoCategoria.clear();
                listCategoria.clear();
            }
        } else if (index == 3) {
            booSexo = !booSexo;
            if (booSexo) {
                tipoSexo = "M";
            } else {
                tipoSexo = "";
            }
        } else if (index == 4) {
            booGrau = !booGrau;
            if (booGrau) {
                listParentesco.clear();
                getListParentesco();
            } else {
                listParentesco.clear();
            }
        } else if (index == 5) {
            booFotos = !booFotos;
            if (booFotos) {
                tipoFotos = "com";
            } else {
                tipoFotos = "";
            }
        } else if (index == 6) {
            booCarteirinha = !booCarteirinha;
            if (booCarteirinha) {
                tipoCarteirinha = "com";
            } else {
                tipoCarteirinha = "";
            }
        } else if (index == 7) {
            booTipoCobranca = !booTipoCobranca;
        } else if (index == 8) {
            booCidadeSocio = !booCidadeSocio;
        } else if (index == 9) {
            booCidadeEmpresa = !booCidadeEmpresa;
        } else if (index == 10) {
            booAniversario = !booAniversario;
            if (booAniversario) {
                listaMeses.clear();
                getListaMeses();
                diaInicial = 1;
                diaFinal = 31;
                ordemAniversario = false;
            } else {
                listaMeses.clear();
                diaInicial = 0;
                diaFinal = 0;
                ordemAniversario = false;
            }
        } else if (index == 11) {
            booData = !booData;
        } else if (index == 12) {
            booVotante = !booVotante;
        } else if (index == 13) {
            booEmail = !booEmail;
        } else if (index == 14) {
            booTelefone = !booTelefone;
        } else if (index == 15) {
            booEstadoCivil = !booEstadoCivil;
            if (booEstadoCivil) {
                tipoEstadoCivil = "Solteiro(a)";
            } else {
                tipoEstadoCivil = "";
            }
        } else if (index == 16) {
            booEmpresa = !booEmpresa;
            if (!booEmpresa) {
                minQtdeFuncionario = null;
                maxQtdeFuncionario = null;
                empresa = new Juridica();
            }
        } else if (index == 17) {
            situacao = !situacao;
            if (situacao) {
                tipoCarencia = "todos";
                situacaoString = "adimplente";
                carenciaDias = 0;
            } else {
                situacaoString = null;
                tipoCarencia = "todos";
                carenciaDias = null;
            }
        } else if (index == 18) {
            booBiometria = !booBiometria;
        } else if (index == 19) {
            booDescontoFolha = !booDescontoFolha;
        } else if (index == 20) {
            booServicos = !booServicos;
            if (booServicos) {
                loadGrupoFinanceiro();
                loadSubGrupoFinanceiro();
                contemServicos = false;
            } else {
                listSubGrupoFinanceiro = null;
                selectedSubGrupoFinanceiro = null;
                listSubGrupoFinanceiro = null;
                selectedSubGrupoFinanceiro = new ArrayList();
                listServicos = new HashMap<>();
                selectedServicos = new ArrayList<>();
                contemServicos = null;
            }
        } else if (index == 21) {
            booDescontoSocial = !booDescontoSocial;
            if (booDescontoSocial) {
                loadDescontoSocial();
            } else {
                listDescontoSocial = null;
                selectedDescontoSocial = null;
            }
        }

    }

    public final void loadFilters() {

        // SÓCIO
        filtersSocio = new ArrayList<>();
        /* 00 */ filtersSocio.add(new Filters("numero_matricula", "Número da Matrícula", false));
        /* 01 */ filtersSocio.add(new Filters("idade", "Idade", false));
        /* 02 */ filtersSocio.add(new Filters("grupo_categoria", "Grupo / Categoria", false));
        /* 03 */ filtersSocio.add(new Filters("sexo", "Sexo", false));
        /* 04 */ filtersSocio.add(new Filters("grau", "Grau", false));
        /* 05 */ filtersSocio.add(new Filters("fotos", "Fotos", false));
        /* 06 */ filtersSocio.add(new Filters("cidade_socio", "Cidade do Sócio", false));
        /* 07 */ filtersSocio.add(new Filters("carteirinha", "Carteirinha", false));
        /* 08 */ filtersSocio.add(new Filters("aniversario", "Aniversário", false));
        /* 09 */ filtersSocio.add(new Filters("estado_civil", "Estado Civil", false));
        /* 10 */ filtersSocio.add(new Filters("votante", "Votante", false));
        /* 11 */ filtersSocio.add(new Filters("email", "Email", false));
        /* 12 */ filtersSocio.add(new Filters("telefone", "Telefone", false));
        /* 13 */ filtersSocio.add(new Filters("biometria", "Estado Civil", false));
        /* 14 */ filtersSocio.add(new Filters("situacao", "Situação", false));

        // EMPRESA
        filtersEmpresa = new ArrayList<>();
        /* 01 */ filtersEmpresa.add(new Filters("empresas", "Empresas", false));
        /* 02 */ filtersEmpresa.add(new Filters("cidade_empresa", "Cidade da Empresa", false));

        // FINANÇEIRO
        filtersFinanceiro = new ArrayList<>();
        /* 01 */ filtersFinanceiro.add(new Filters("servicos", "Serviços", false));
        /* 02 */ filtersFinanceiro.add(new Filters("tipo_cobranca", "Tipo de Cobrança", false));
        /* 03 */ filtersFinanceiro.add(new Filters("datas", "Datas", false));
        /* 04 */ filtersFinanceiro.add(new Filters("desconto_folha", "Desconto em Folha", false));
        /* 05 */ filtersFinanceiro.add(new Filters("desconto_social", "Desconto Social", false));
    }

    public void close(Filters filter) {
//        if (!filter.getActive()) {
//            switch (filter.getGroup()) {
//                case "Sócio":
//                    switch (filter.getKey()) {
//                        case "tipo_cobranca":
//                            loadTipoCobranca();
//                            break;
//                        case "titular":
//                            fisica = new Fisica();
//                            break;
//                    }   break;
//                case "Empresa":
//                    break;
//                case "Financeiro":
//                    break;
//            }
//        }
    }

    public List<DataObject> getListaMenuRSocial() {
        if (listaMenuRSocial.isEmpty()) {
            /* 00 */ listaMenuRSocial.add(new DataObject("Número da Matricula ", "Editar", null, null, null, null));
            /* 01 */ listaMenuRSocial.add(new DataObject("Idade ", "Editar", null, null, null, null));
            /* 02 */ listaMenuRSocial.add(new DataObject("Grupo / Categoria ", "Editar", null, null, null, null));
            /* 03 */ listaMenuRSocial.add(new DataObject("Sexo ", "Editar", null, null, null, null));
            /* 04 */ listaMenuRSocial.add(new DataObject("Grau ", "Remover", null, null, null, null));
            /* 05 */ listaMenuRSocial.add(new DataObject("Fotos ", "Editar", null, null, null, null));
            /* 06 */ listaMenuRSocial.add(new DataObject("Carteirinha ", "Editar", null, null, null, null));
            /* 07 */ listaMenuRSocial.add(new DataObject("Tipo de Cobrança ", "Editar", null, null, null, null));
            /* 08 */ listaMenuRSocial.add(new DataObject("Cidade do Sócio ", "Editar", null, null, null, null));
            /* 09 */ listaMenuRSocial.add(new DataObject("Cidade do Empresa ", "Editar", null, null, null, null));
            /* 10 */ listaMenuRSocial.add(new DataObject("Aniversário ", "Editar", null, null, null, null));
            /* 11 */ listaMenuRSocial.add(new DataObject("Datas ", "Editar", null, null, null, null));
            /* 12 */ listaMenuRSocial.add(new DataObject("Votante ", "Editar", null, null, null, null));
            /* 13 */ listaMenuRSocial.add(new DataObject("Email ", "Editar", null, null, null, null));
            /* 14 */ listaMenuRSocial.add(new DataObject("Telefone ", "Editar", null, null, null, null));
            /* 15 */ listaMenuRSocial.add(new DataObject("Estado Civil ", "Editar", null, null, null, null));
            /* 16 */ listaMenuRSocial.add(new DataObject("Empresas ", "Editar", null, null, null, null));
            /* 17 */ listaMenuRSocial.add(new DataObject("Situação ", "Editar", null, null, null, null));
            /* 18 */ listaMenuRSocial.add(new DataObject("Biometria ", "Editar", null, null, null, null));
            /* 19 */ listaMenuRSocial.add(new DataObject("Desconto Folha ", "Editar", null, null, null, null));
            /* 20 */ listaMenuRSocial.add(new DataObject("Serviços ", "Editar", null, null, null, null));
            /* 21 */ listaMenuRSocial.add(new DataObject("Desconto Social ", "Editar", null, null, null, null));
        }
        return listaMenuRSocial;
    }

    public void setListaMenuRSocial(List<DataObject> listaMenuRSocial) {
        this.listaMenuRSocial = listaMenuRSocial;
    }

    public List<SelectItem> getListRelatorios() {
        if (listRelatorio.isEmpty()) {
            RelatorioDao db = new RelatorioDao();
            List<Relatorios> list = db.pesquisaTipoRelatorio(171);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorio = i;
                }
                if (list.get(i).getPrincipal()) {
                    idRelatorio = i;
                }
                listRelatorio.add(new SelectItem(i,
                        list.get(i).getNome(),
                        Integer.toString(list.get(i).getId())));
            }
        }
        return listRelatorio;
    }

    public String visualizarRelatorio() {
        if (situacao) {
            if (carenciaDias < 0) {
                GenericaMensagem.warn("Sistema", "Informar carência de débito em dias:!");
                return null;
            }
        }
        // ESTA TRAZENDO TODOS REGISTRO DO BANCO -- rogério pediu
//        if (!validaFiltro()){
//            GenericaMensagem.warn("Atenção", "Selecione algum filtro para esta pesquisa!");
//            return null;
//        }

        RelatorioDao db = new RelatorioDao();
        RelatorioSociosDao dbS = new RelatorioSociosDao();
        Relatorios relatorios = db.pesquisaRelatorios(Integer.parseInt(getListRelatorios().get(idRelatorio).getDescription()));
        if (!listRelatorioOrdem.isEmpty()) {
            Dao dao = new Dao();
            relatorios.setQryOrdem(((RelatorioOrdem) dao.find(new RelatorioOrdem(), Integer.parseInt(getListRelatorioOrdem().get(idRelatorioOrdem).getDescription()))).getQuery());
        }

        String ids_gc = "", ids_c = "", id_ds = "";
        if (booGrupoCategoria) {
            ids_gc = inIdGrupoCategoria();
            ids_c = inIdCategoria();
        }

        if (booDescontoSocial) {
            id_ds = inIdDescontoSocial();
        }

        String meses = "";
        String di = String.valueOf(diaInicial), df = String.valueOf(diaFinal);
        boolean ordema = false;
        if (booAniversario) {
            for (int i = 0; i < listaMeses.size(); i++) {
                if ((Boolean) listaMeses.get(i).getArgumento0()) {
                    if (meses.length() > 0 && i != listaMeses.size()) {
                        meses += ",";
                    }
                    meses += Integer.valueOf(listaMeses.get(i).getArgumento2().toString());
                }
            }

            if (di.length() == 1) {
                di = "0" + di;
            }
            if (df.length() == 1) {
                df = "0" + df;
            }

            ordema = ordemAniversario;
        }

        String ids_pagamento = "";
        for (int i = 0; i < listaTipoCobranca.size(); i++) {
            if ((Boolean) listaTipoCobranca.get(i).getArgumento0()) {
                if (ids_pagamento.length() > 0 && i != listaTipoCobranca.size()) {
                    ids_pagamento += ",";
                }
                ids_pagamento += ((FTipoDocumento) listaTipoCobranca.get(i).getArgumento1()).getId();
            }
        }

        String ids_cidade_socio = "";
        for (int i = 0; i < listaCidadesSocio.size(); i++) {
            if ((Boolean) listaCidadesSocio.get(i).getArgumento0()) {
                if (ids_cidade_socio.length() > 0 && i != listaCidadesSocio.size()) {
                    ids_cidade_socio += ",";
                }
                ids_cidade_socio += ((Cidade) listaCidadesSocio.get(i).getArgumento1()).getId();
            }
        }

        String ids_cidade_empresa = "";
        for (int i = 0; i < listaCidadesEmpresa.size(); i++) {
            if ((Boolean) listaCidadesEmpresa.get(i).getArgumento0()) {
                if (ids_cidade_empresa.length() > 0 && i != listaCidadesEmpresa.size()) {
                    ids_cidade_empresa += ",";
                }
                ids_cidade_empresa += ((Cidade) listaCidadesEmpresa.get(i).getArgumento1()).getId();
            }
        }

        String ids_parentesco = inIdParentesco();
        List<List> result = dbS.pesquisaSocios(
                relatorios, matriculaInicial, matriculaFinal, idadeInicial, idadeFinal, ids_gc, ids_c, tipoSexo, ids_parentesco, tipoFotos, tipoCarteirinha,
                booTipoCobranca, ids_pagamento, booCidadeSocio, ids_cidade_socio, booCidadeEmpresa, ids_cidade_empresa,
                booAniversario, meses, di, df, ordema, booData, dataCadastro, dataCadastroFim, dataRecadastro, dataRecadastroFim, dataDemissao, dataDemissaoFim, dataAdmissaoSocio,
                dataAdmissaoSocioFim, dataAdmissaoEmpresa, dataAdmissaoEmpresaFim, booVotante, tipoEleicao,
                booEmail, tipoEmail, booTelefone, tipoTelefone, booEstadoCivil, tipoEstadoCivil, booEmpresa, tipoEmpresas, empresa.getId(), minQtdeFuncionario, maxQtdeFuncionario, dataAposetandoria, dataAposetandoriaFim, tipoOrdem, tipoCarencia, carenciaDias, situacaoString,
                booBiometria, tipoBiometria, booDescontoFolha, tipoDescontoFolha, dataAtualicacao, dataAtualicacaoFim, contemServicos, inIdGrupoFinanceiro(), inIdSubGrupoFinanceiro(), inIdServicos(), inIdDescontoSocial()
        );
        
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
                    
        Collection lista = new ArrayList();
        for (int i = 0; i < result.size(); i++) {
            lista.add(new ParametroSocios(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
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
                    getConverteNullInt(result.get(i).get(12)),// CODIGO 
                    (Date) result.get(i).get(13),// CADASTRO
                    getConverteNullString(result.get(i).get(14)),// NOME
                    getConverteNullString(result.get(i).get(15)),// CPF
                    getConverteNullString(result.get(i).get(16)),// TELEFONE
                    getConverteNullString(result.get(i).get(17)),// UF EMISSAO RG
                    getConverteNullString(result.get(i).get(18)),// ESTADO CIVIL
                    getConverteNullString(result.get(i).get(19)),// CTPS
                    getConverteNullString(result.get(i).get(20)),// PAI
                    getConverteNullString(result.get(i).get(21)),// SEXO
                    getConverteNullString(result.get(i).get(22)),// MAE
                    getConverteNullString(result.get(i).get(23)),// NACIONALIDADE
                    getConverteNullString(result.get(i).get(24)),// NIT
                    getConverteNullString(result.get(i).get(25)),// ORGAO EMISSAO RG
                    getConverteNullString(result.get(i).get(26)),// PIS
                    getConverteNullString(result.get(i).get(27)),// SERIE
                    (Date) result.get(i).get(28),// APOSENTADORIA ------------
                    getConverteNullString(result.get(i).get(29)),// NATURALIDADE
                    (Date) result.get(i).get(30),// RECADASTRO
                    (Date) result.get(i).get(31),// DT NASCIMENTO -------------
                    getConverteNullString(result.get(i).get(32)),// DT FOTO -------------------
                    getConverteNullString(result.get(i).get(33)),// RG
                    "",// CAMINHO DA FOTO SOCIO
                    getConverteNullString(result.get(i).get(35)),// LOGRADOURO
                    getConverteNullString(result.get(i).get(36)),// ENDERECO
                    getConverteNullString(result.get(i).get(37)),// NUMERO
                    getConverteNullString(result.get(i).get(38)),// COMPLEMENTO
                    getConverteNullString(result.get(i).get(39)),// BAIRRO
                    getConverteNullString(result.get(i).get(40)),// CIDADE
                    getConverteNullString(result.get(i).get(41)),// UF
                    getConverteNullString(getConverteNullString(result.get(i).get(42))),// CEP
                    getConverteNullString(result.get(i).get(43)),// SETOR
                    (Date) result.get(i).get(44),// DT ADMISSAO ---------------
                    getConverteNullString(result.get(i).get(45)),// PROFISSAO
                    getConverteNullString(result.get(i).get(46)),// EMPRESA FANTASIA
                    getConverteNullString(result.get(i).get(47)),// NOME EMPRESA
                    getConverteNullString(result.get(i).get(48)),// EMPRESA CNPJ
                    getConverteNullString(result.get(i).get(49)),// EMPRESA TELEFONE
                    getConverteNullString(result.get(i).get(50)),// EMPRESA LOGRADOURO
                    getConverteNullString(result.get(i).get(51)),// EMPRESA ENDERECO
                    getConverteNullString(result.get(i).get(52)),// EMPRESA NUMERO
                    getConverteNullString(result.get(i).get(53)),// "       COMPLEMENTO 
                    getConverteNullString(result.get(i).get(54)),// "       BAIRRO
                    getConverteNullString(result.get(i).get(55)),// "       CIDADE
                    getConverteNullString(result.get(i).get(56)),// "       UF
                    getConverteNullString(result.get(i).get(57)),// "       CEP
                    getConverteNullString(result.get(i).get(58)),// TITULAR
                    getConverteNullString(result.get(i).get(59)),// COD SOCIO
                    getConverteNullString(result.get(i).get(60)),// NOME SOCIO
                    getConverteNullString(result.get(i).get(61)),// PARENTESCO 
                    getConverteNullInt(result.get(i).get(62)),// MATRICULA
                    getConverteNullString(result.get(i).get(63)),// CATEGORIA
                    getConverteNullString(result.get(i).get(64)),// GRUPO CATEGORIA
                    (Date) result.get(i).get(65),// DT FILIACAO --------------
                    (Date) result.get(i).get(66),// INATIVACAO ---------------
                    (Boolean) result.get(i).get(67),// VOTANTE
                    getConverteNullString(result.get(i).get(68)),// GRAU
                    new BigDecimal(Float.parseFloat(getConverteNullString(result.get(i).get(58)))),// NR DESCONTO
                    (Boolean) result.get(i).get(70),
                    getConverteNullString(result.get(i).get(71)),// TIPO COBRANCA
                    getConverteNullInt(result.get(i).get(72)),// COD TIPO COBRANCA
                    getConverteNullString(result.get(i).get(73)),// TELEFONE2
                    getConverteNullString(result.get(i).get(74)), // TELEFONE3                                          
                    getConverteNullString(result.get(i).get(75)), // EMAIL 1
                    getConverteNullString(result.get(i).get(76)), // CONTABILIDADE - NOME
                    getConverteNullString(result.get(i).get(77)), // CONTABILIDADE - CONTATO
                    getConverteNullString(result.get(i).get(78)), // CONTABILIDADE - EMAIL
                    ((getConverteNullString(result.get(i).get(79)) != null) ? DataHoje.converteData((Date) result.get(i).get(79)) : ""), // ADMISSAO EMPRESA DEMISSIONADA
                    ((getConverteNullString(result.get(i).get(80)) != null) ? DataHoje.converteData((Date) result.get(i).get(80)) : ""), // DEMISSAO EMPRESA DEMISSIONADA
                    getConverteNullString(result.get(i).get(81)), // CNPJ EMPRESA DEMISSIONADA
                    getConverteNullString(result.get(i).get(82)), // EMPRESA DEMISSIONADA
                    getConverteNullString(result.get(i).get(83)) // IDADE
            ));
//            if (i == 2392){
//                break;
//            }
        }

        if (lista.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return null;
        }

        Jasper.PART_NAME = AnaliseString.removerAcentos(relatorios.getNome().toLowerCase());
        Jasper.PART_NAME = Jasper.PART_NAME.replace("/", "");
        Jasper.PATH = "downloads";
        if (relatorios.getPorFolha()) {
            Jasper.GROUP_NAME = relatorios.getNomeGrupo();
            if (porFolha) {
                // Jasper.setIS_BY_LEAF((Boolean) true);
            } else {
                // Jasper.setIS_BY_LEAF((Boolean) false);
            }
        }
        // Jasper.COMPRESS_FILE = false;
        // Jasper.COMPRESS_LIMIT = 1000;
        Jasper.printReports(relatorios.getJasper(), "relatorios", (Collection) lista);
        return null;
    }

    public List<Categoria> getListCategoria() {
        if (listCategoria.isEmpty()) {
            CategoriaDB db = new CategoriaDBToplink();
            List<Categoria> list = new ArrayList();
            if (!listGrupoCategoria.isEmpty()) {
                String ids = inIdGrupoCategoria();
                if (ids != null) {
                    list = db.pesquisaCategoriaPorGrupoIds(ids);
                }
            }
            for (Categoria c : list) {
                c.setSelected(true);
                listCategoria.add(c);
            }
        }
        return listCategoria;
    }

    public void setListCategoria(List<Categoria> listCategoria) {
        this.listCategoria = listCategoria;
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

    public List<DataObject> getListaTipoCobranca() {
        if (listaTipoCobranca.isEmpty()) {
            FTipoDocumentoDB db = new FTipoDocumentoDBToplink();
            List select = new ArrayList();
            select.add(db.pesquisaCodigo(2));
            select.addAll(db.pesquisaListaTipoExtrato());
            for (int i = 0; i < select.size(); i++) {
                listaTipoCobranca.add(new DataObject(false, (FTipoDocumento) select.get(i)));
            }
        }
        return listaTipoCobranca;
    }

    public void setListaTipoCobranca(List<DataObject> listaTipoCobranca) {
        this.listaTipoCobranca = listaTipoCobranca;
    }

    public void marcarTipos() {
        for (int i = 0; i < listaTipoCobranca.size(); i++) {
            listaTipoCobranca.get(i).setArgumento0(chkTipoCobranca);
        }
    }

    public void marcarGrau() {
        for (int i = 0; i < listParentesco.size(); i++) {
            listParentesco.get(i).setSelected(chkGrau);
        }
    }

    public List getListaCidadesSocio() {
        if (listaCidadesSocio.isEmpty()) {
            RelatorioSociosDao db = new RelatorioSociosDao();
            List select = new ArrayList();
            select.addAll(db.listaCidadeDoSocio());
            for (int i = 0; i < select.size(); i++) {
                listaCidadesSocio.add(new DataObject(false, ((Cidade) select.get(i))));
            }
        }
        return listaCidadesSocio;
    }

    public void setListaCidadesSocio(List listaCidadesSocio) {
        this.listaCidadesSocio = listaCidadesSocio;
    }

    public void marcarCidadesSocio() {
        for (int i = 0; i < listaCidadesSocio.size(); i++) {
            ((DataObject) listaCidadesSocio.get(i)).setArgumento0(chkCidadesSocio);
        }
    }

    public List getListaCidadesEmpresa() {
        if (listaCidadesEmpresa.isEmpty()) {
            RelatorioSociosDao db = new RelatorioSociosDao();
            List select = new ArrayList();
            select.addAll(db.listaCidadeDaEmpresa());
            for (int i = 0; i < select.size(); i++) {
                listaCidadesEmpresa.add(new DataObject(false, ((Cidade) select.get(i))));
            }
        }
        return listaCidadesEmpresa;
    }

    public void setListaCidadesEmpresa(List listaCidadesEmpresa) {
        this.listaCidadesEmpresa = listaCidadesEmpresa;
    }

    public void marcarCidadesEmpresa() {
        for (int i = 0; i < listaCidadesEmpresa.size(); i++) {
            listaCidadesEmpresa.get(i).setArgumento0(chkCidadesEmpresa);
        }
    }

    public List<DataObject> getListaMeses() {
        if (listaMeses.isEmpty()) {
            listaMeses.add(new DataObject(false, "Janeiro", "01", null, null, null));
            listaMeses.add(new DataObject(false, "Fevereiro", "02", null, null, null));
            listaMeses.add(new DataObject(false, "Março", "03", null, null, null));
            listaMeses.add(new DataObject(false, "Abril", "04", null, null, null));
            listaMeses.add(new DataObject(false, "Maio", "05", null, null, null));
            listaMeses.add(new DataObject(false, "Junho", "06", null, null, null));
            listaMeses.add(new DataObject(false, "Julho", "07", null, null, null));
            listaMeses.add(new DataObject(false, "Agosto", "08", null, null, null));
            listaMeses.add(new DataObject(false, "Setembro", "09", null, null, null));
            listaMeses.add(new DataObject(false, "Outubro", "10", null, null, null));
            listaMeses.add(new DataObject(false, "Novembro", "11", null, null, null));
            listaMeses.add(new DataObject(false, "Dezembro", "12", null, null, null));
        }
        return listaMeses;
    }

    public void setListaMeses(List listaMeses) {
        this.listaMeses = listaMeses;
    }

    public void marcarMeses() {
        for (int i = 0; i < listaMeses.size(); i++) {
            ((DataObject) listaMeses.get(i)).setArgumento0(chkMeses);
        }
    }

    public List getListaServicos() {
        if (listaServicos.isEmpty()) {
            RelatorioSociosDao db = new RelatorioSociosDao();
            List select = new ArrayList();
            if (chkSocios) {
                select.addAll(db.listaSPSocios());
            }
            if (chkConvênioMedico) {
                select.addAll(db.listaSPConvenioMedico());
            }
            if (chkAcademia) {
                select.addAll(db.listaSPAcademia());
            }
            if (chkEscola) {
                select.addAll(db.listaSPEscola());
            }
            for (int i = 0; i < select.size(); i++) {
                listaServicos.add(new DataObject(false, (Servicos) select.get(i)));
            }
        }
        return listaServicos;
    }

    public void setListaServicos(List listaServicos) {
        this.listaServicos = listaServicos;
    }

    public void marcarServicos() {
        for (int i = 0; i < listaServicos.size(); i++) {
            ((DataObject) listaServicos.get(i)).setArgumento0(chkServicos);
        }
    }

    public void marcarInscritos() {
        chkSocios = chkTodos;
        chkAcademia = chkTodos;
        chkConvênioMedico = chkTodos;
        chkEscola = chkTodos;
        refreshFormServicos();
    }

    public void refreshForm() {
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

    public void refreshFormServicos() {
        listaServicos.clear();
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDataRecadastro() {
        return dataRecadastro;
    }

    public void setDataRecadastro(String dataRecadastro) {
        this.dataRecadastro = dataRecadastro;
    }

    public boolean isChkGrupo() {
        return chkGrupo;
    }

    public void setChkGrupo(boolean chkGrupo) {
        this.chkGrupo = chkGrupo;
    }

    public boolean isChkCategoria() {
        return chkCategoria;
    }

    public void setChkCategoria(boolean chkCategoria) {
        this.chkCategoria = chkCategoria;
    }

    public Integer getMatriculaInicial() {
        return matriculaInicial;
    }

    public void setMatriculaInicial(Integer matriculaInicial) {
        this.matriculaInicial = matriculaInicial;
    }

    public Integer getMatriculaFinal() {
        return matriculaFinal;
    }

    public void setMatriculaFinal(Integer matriculaFinal) {
        this.matriculaFinal = matriculaFinal;
    }

    public String getTipoEleicao() {
        return tipoEleicao;
    }

    public void setTipoEleicao(String tipoEleicao) {
        this.tipoEleicao = tipoEleicao;
    }

    public Integer getIdadeInicial() {
        return idadeInicial;
    }

    public void setIdadeInicial(Integer idadeInicial) {
        this.idadeInicial = idadeInicial;
    }

    public Integer getIdadeFinal() {
        return idadeFinal;
    }

    public void setIdadeFinal(Integer idadeFinal) {
        this.idadeFinal = idadeFinal;
    }

    public String getTipoSexo() {
        return tipoSexo;
    }

    public void setTipoSexo(String tipoSexo) {
        this.tipoSexo = tipoSexo;
    }

    public boolean isChkGrau() {
        return chkGrau;
    }

    public void setChkGrau(boolean chkGrau) {
        this.chkGrau = chkGrau;
    }

    public String getTipoCarteirinha() {
        return tipoCarteirinha;
    }

    public void setTipoCarteirinha(String tipoCarteirinha) {
        this.tipoCarteirinha = tipoCarteirinha;
    }

    public String getTipoSuspensos() {
        return tipoSuspensos;
    }

    public void setTipoSuspensos(String tipoSuspensos) {
        this.tipoSuspensos = tipoSuspensos;
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

    public boolean isChkTipoCobranca() {
        return chkTipoCobranca;
    }

    public void setChkTipoCobranca(boolean chkTipoCobranca) {
        this.chkTipoCobranca = chkTipoCobranca;
    }

    public boolean isChkCidadesSocio() {
        return chkCidadesSocio;
    }

    public void setChkCidadesSocio(boolean chkCidadesSocio) {
        this.chkCidadesSocio = chkCidadesSocio;
    }

    public boolean isChkCidadesEmpresa() {
        return chkCidadesEmpresa;
    }

    public void setChkCidadesEmpresa(boolean chkCidadesEmpresa) {
        this.chkCidadesEmpresa = chkCidadesEmpresa;
    }

    public boolean isChkMeses() {
        return chkMeses;
    }

    public void setChkMeses(boolean chkMeses) {
        this.chkMeses = chkMeses;
    }

    public Integer getIdDias() {
        return idDias;
    }

    public void setIdDias(Integer idDias) {
        this.idDias = idDias;
    }

    public Integer getDiaInicial() {
        return diaInicial;
    }

    public void setDiaInicial(Integer diaInicial) {
        this.diaInicial = diaInicial;
    }

    public Integer getDiaFinal() {
        return diaFinal;
    }

    public void setDiaFinal(Integer diaFinal) {
        this.diaFinal = diaFinal;
    }

    public String getTipoOrdem() {
        return tipoOrdem;
    }

    public void setTipoOrdem(String tipoOrdem) {
        this.tipoOrdem = tipoOrdem;
    }

    public boolean isChkTodos() {
        return chkTodos;
    }

    public void setChkTodos(boolean chkTodos) {
        this.chkTodos = chkTodos;
    }

    public boolean isChkSocios() {
        return chkSocios;
    }

    public void setChkSocios(boolean chkSocios) {
        this.chkSocios = chkSocios;
    }

    public boolean isChkEscola() {
        return chkEscola;
    }

    public void setChkEscola(boolean chkEscola) {
        this.chkEscola = chkEscola;
    }

    public boolean isChkAcademia() {
        return chkAcademia;
    }

    public void setChkAcademia(boolean chkAcademia) {
        this.chkAcademia = chkAcademia;
    }

    public boolean isChkConvênioMedico() {
        return chkConvênioMedico;
    }

    public void setChkConvênioMedico(boolean chkConvênioMedico) {
        this.chkConvênioMedico = chkConvênioMedico;
    }

    public boolean isChkServicos() {
        return chkServicos;
    }

    public void setChkServicos(boolean chkServicos) {
        this.chkServicos = chkServicos;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public String getSelectAccordion() {
        return selectAccordion;
    }

    public void setSelectAccordion(String selectAccordion) {
        this.selectAccordion = selectAccordion;
    }

    public boolean isBooMatricula() {
        return booMatricula;
    }

    public void setBooMatricula(boolean booMatricula) {
        this.booMatricula = booMatricula;
    }

    public boolean isBooIdade() {
        return booIdade;
    }

    public void setBooIdade(boolean booIdade) {
        this.booIdade = booIdade;
    }

    public boolean isBooGrupoCategoria() {
        return booGrupoCategoria;
    }

    public void setBooGrupoCategoria(boolean booGrupoCategoria) {
        this.booGrupoCategoria = booGrupoCategoria;
    }

    public List<GrupoCategoria> getListGrupoCategoria() {
        if (listGrupoCategoria.isEmpty()) {
            List<GrupoCategoria> list = (List<GrupoCategoria>) new Dao().list(new GrupoCategoria(), true);
            for (GrupoCategoria gc : list) {
                gc.setSelected(true);
                listGrupoCategoria.add(gc);
            }
        }
        return listGrupoCategoria;
    }

    public void setListGrupoCategoria(List<GrupoCategoria> listGrupoCategoria) {
        this.listGrupoCategoria = listGrupoCategoria;
    }

    public void marcarGrupoCatregoria() {
        for (int i = 0; i < listGrupoCategoria.size(); i++) {
            listGrupoCategoria.get(i).setSelected(chkGrupo);
        }
        listCategoria.clear();
    }

    public void marcarUmGrupoCategoria() {
        listCategoria.clear();
    }

    public void marcarCategorias() {
        for (int i = 0; i < listCategoria.size(); i++) {
            listCategoria.get(i).setSelected(chkCategoria);
        }
    }

    public boolean isBooSexo() {
        return booSexo;
    }

    public void setBooSexo(boolean booSexo) {
        this.booSexo = booSexo;
    }

    public boolean isBooGrau() {
        return booGrau;
    }

    public void setBooGrau(boolean booGrau) {
        this.booGrau = booGrau;
    }

    public boolean isBooFotos() {
        return booFotos;
    }

    public void setBooFotos(boolean booFotos) {
        this.booFotos = booFotos;
    }

    public boolean isBooCarteirinha() {
        return booCarteirinha;
    }

    public void setBooCarteirinha(boolean booCarteirinha) {
        this.booCarteirinha = booCarteirinha;
    }

    public boolean isBooTipoCobranca() {
        return booTipoCobranca;
    }

    public void setBooTipoCobranca(boolean booTipoCobranca) {
        this.booTipoCobranca = booTipoCobranca;
    }

    public boolean isBooCidadeSocio() {
        return booCidadeSocio;
    }

    public void setBooCidadeSocio(boolean booCidadeSocio) {
        this.booCidadeSocio = booCidadeSocio;
    }

    public boolean isBooCidadeEmpresa() {
        return booCidadeEmpresa;
    }

    public void setBooCidadeEmpresa(boolean booCidadeEmpresa) {
        this.booCidadeEmpresa = booCidadeEmpresa;
    }

    public boolean isBooAniversario() {
        return booAniversario;
    }

    public void setBooAniversario(boolean booAniversario) {
        this.booAniversario = booAniversario;
    }

    public boolean isBooData() {
        return booData;
    }

    public void setBooData(boolean booData) {
        this.booData = booData;
    }

    public String getDataDemissao() {
        return dataDemissao;
    }

    public void setDataDemissao(String dataDemissao) {
        this.dataDemissao = dataDemissao;
    }

    public boolean isBooVotante() {
        return booVotante;
    }

    public void setBooVotante(boolean booVotante) {
        this.booVotante = booVotante;
    }

    public boolean isBooEmail() {
        return booEmail;
    }

    public void setBooEmail(boolean booEmail) {
        this.booEmail = booEmail;
    }

    public boolean isBooTelefone() {
        return booTelefone;
    }

    public void setBooTelefone(boolean booTelefone) {
        this.booTelefone = booTelefone;
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

    public boolean isBooEstadoCivil() {
        return booEstadoCivil;
    }

    public void setBooEstadoCivil(boolean booEstadoCivil) {
        this.booEstadoCivil = booEstadoCivil;
    }

    public String getTipoEstadoCivil() {
        return tipoEstadoCivil;
    }

    public void setTipoEstadoCivil(String tipoEstadoCivil) {
        this.tipoEstadoCivil = tipoEstadoCivil;
    }

    public List<Parentesco> getListParentesco() {
        if (listParentesco.isEmpty()) {
            List<Parentesco> list = new Dao().list(new Parentesco(), true);
            for (Parentesco p : list) {
                if (p.getParentesco().equals("TITULAR")) {
                    p.setSelected(true);
                } else {
                    p.setSelected(false);
                }
                listParentesco.add(p);
            }
        }
        return listParentesco;
    }

    public void setListParentesco(List<Parentesco> listParentesco) {
        this.listParentesco = listParentesco;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        listRelatorioOrdem.clear();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(new Rotina().get().getId());
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(i, list.get(i).getNome(), "" + list.get(i).getId()));
            }
        }
        return listRelatorioOrdem;
    }

    public boolean isChkEmpresa() {
        return chkEmpresa;
    }

    public void setChkEmpresa(boolean chkEmpresa) {
        this.chkEmpresa = chkEmpresa;
    }

    public boolean isBooEmpresa() {
        return booEmpresa;
    }

    public void setBooEmpresa(boolean booEmpresa) {
        this.booEmpresa = booEmpresa;
    }

    public String getDataAdmissaoSocio() {
        return dataAdmissaoSocio;
    }

    public void setDataAdmissaoSocio(String dataAdmissaoSocio) {
        this.dataAdmissaoSocio = dataAdmissaoSocio;
    }

    public String getDataAdmissaoEmpresa() {
        return dataAdmissaoEmpresa;
    }

    public void setDataAdmissaoEmpresa(String dataAdmissaoEmpresa) {
        this.dataAdmissaoEmpresa = dataAdmissaoEmpresa;
    }

    public String getDataCadastroFim() {
        return dataCadastroFim;
    }

    public void setDataCadastroFim(String dataCadastroFim) {
        this.dataCadastroFim = dataCadastroFim;
    }

    public String getDataRecadastroFim() {
        return dataRecadastroFim;
    }

    public void setDataRecadastroFim(String dataRecadastroFim) {
        this.dataRecadastroFim = dataRecadastroFim;
    }

    public String getDataDemissaoFim() {
        return dataDemissaoFim;
    }

    public void setDataDemissaoFim(String dataDemissaoFim) {
        this.dataDemissaoFim = dataDemissaoFim;
    }

    public String getDataAdmissaoSocioFim() {
        return dataAdmissaoSocioFim;
    }

    public void setDataAdmissaoSocioFim(String dataAdmissaoSocioFim) {
        this.dataAdmissaoSocioFim = dataAdmissaoSocioFim;
    }

    public String getDataAdmissaoEmpresaFim() {
        return dataAdmissaoEmpresaFim;
    }

    public void setDataAdmissaoEmpresaFim(String dataAdmissaoEmpresaFim) {
        this.dataAdmissaoEmpresaFim = dataAdmissaoEmpresaFim;
    }

    public String getDataAposetandoria() {
        return dataAposetandoria;
    }

    public void setDataAposetandoria(String dataAposetandoria) {
        this.dataAposetandoria = dataAposetandoria;
    }

    public String getDataAposetandoriaFim() {
        return dataAposetandoriaFim;
    }

    public void setDataAposetandoriaFim(String dataAposetandoriaFim) {
        this.dataAposetandoriaFim = dataAposetandoriaFim;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Boolean getSituacao() {
        return situacao;
    }

    public void setSituacao(Boolean situacao) {
        this.situacao = situacao;
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
            Relatorios r = (Relatorios) new Dao().find(new Relatorios(), Integer.parseInt(getListRelatorios().get(idRelatorio).getDescription()));
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

    public boolean isBooBiometria() {
        return booBiometria;
    }

    public void setBooBiometria(boolean booBiometria) {
        this.booBiometria = booBiometria;
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

    public Integer getMinQtdeFuncionario() {
        return minQtdeFuncionario;
    }

    public void setMinQtdeFuncionario(Integer minQtdeFuncionario) {
        this.minQtdeFuncionario = minQtdeFuncionario;
    }

    public Integer getMaxQtdeFuncionario() {
        return maxQtdeFuncionario;
    }

    public void setMaxQtdeFuncionario(Integer maxQtdeFuncionario) {
        this.maxQtdeFuncionario = maxQtdeFuncionario;
    }

    public String getMinQtdeFuncionarioString() {
        try {
            return Integer.toString(minQtdeFuncionario);
        } catch (Exception e) {
            return "0";
        }
    }

    public void setMinQtdeFuncionarioString(String minQtdeFuncionarioString) {
        try {
            Integer min = Integer.parseInt(minQtdeFuncionarioString);
            Integer max = maxQtdeFuncionario;
            if (max != null && min != null && min > max) {
                maxQtdeFuncionario = min;
            }
            this.minQtdeFuncionario = Integer.parseInt(minQtdeFuncionarioString);
        } catch (Exception e) {
            this.minQtdeFuncionario = 0;

        }
    }

    public String getMaxQtdeFuncionarioString() {
        try {
            return Integer.toString(maxQtdeFuncionario);
        } catch (Exception e) {
            return "0";
        }
    }

    public void setMaxQtdeFuncionarioString(String maxQtdeFuncionarioString) {
        try {
            Integer min = minQtdeFuncionario;
            Integer max = Integer.parseInt(maxQtdeFuncionarioString);
            if (max != null && min != null && min > max) {
                maxQtdeFuncionario = min;
            }
            this.maxQtdeFuncionario = Integer.parseInt(maxQtdeFuncionarioString);
        } catch (Exception e) {
            this.maxQtdeFuncionario = 0;
        }
    }

    public boolean isBooDescontoFolha() {
        return booDescontoFolha;
    }

    public void setBooDescontoFolha(boolean booDescontoFolha) {
        this.booDescontoFolha = booDescontoFolha;
    }

    public String getDataAtualicacao() {
        return dataAtualicacao;
    }

    public void setDataAtualicacao(String dataAtualicacao) {
        this.dataAtualicacao = dataAtualicacao;
    }

    public String getDataAtualicacaoFim() {
        return dataAtualicacaoFim;
    }

    public void setDataAtualicacaoFim(String dataAtualicacaoFim) {
        this.dataAtualicacaoFim = dataAtualicacaoFim;
    }

    public boolean isOrdemAniversario() {
        return ordemAniversario;
    }

    public void setOrdemAniversario(boolean ordemAniversario) {
        this.ordemAniversario = ordemAniversario;
    }

    public boolean isBooServicos() {
        return booServicos;
    }

    public void setBooServicos(boolean booServicos) {
        this.booServicos = booServicos;
    }

    public void loadGrupoFinanceiro() {
        listGrupoFinanceiro = new LinkedHashMap<>();
        selectedGrupoFinanceiro = new ArrayList<>();
        listSubGrupoFinanceiro = new HashMap<>();
        selectedSubGrupoFinanceiro = new ArrayList();
        listServicos = new HashMap<>();
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
            listSubGrupoFinanceiro = new HashMap<>();
            FinanceiroDBToplink fd = new FinanceiroDBToplink();
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
        if (!selectedSubGrupoFinanceiro.isEmpty()) {
            servicosDao.setSituacao("A");
            list = new ServicosDao().findBySubGrupoFinanceiro(inIdSubGrupoFinanceiro());
        } else if (!selectedGrupoFinanceiro.isEmpty()) {
            servicosDao.setSituacao("A");
            list = new ServicosDao().findByGrupoFinanceiro(inIdGrupoFinanceiro());
        } else {
            list = new Dao().list(new Servicos(), true);
        }
        for (int i = 0; i < list.size(); i++) {
            listServicos.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadDescontoSocial() {
        listDescontoSocial = new LinkedHashMap<>();
        selectedDescontoSocial = new ArrayList<>();
        List<DescontoSocial> list = new Dao().list(new DescontoSocial());
        for (int i = 0; i < list.size(); i++) {
            listDescontoSocial.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    // TRATAMENTO
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

    public String inIdGrupoCategoria() {
        String ids = "";
        for (int i = 0; i < listGrupoCategoria.size(); i++) {
            if (listGrupoCategoria.get(i).getSelected()) {
                if (ids.isEmpty()) {
                    ids = "" + listGrupoCategoria.get(i).getId();
                } else {
                    ids += ", " + listGrupoCategoria.get(i).getId();
                }
            }
        }
        return ids;
    }

    public String inIdCategoria() {
        String ids = "";
        for (Categoria listCategoria1 : listCategoria) {
            if (listCategoria1.getSelected()) {
                if (ids.isEmpty())
                    ids = "" + listCategoria1.getId();
                else
                    ids += ", " + listCategoria1.getId();
            }
        }
        return ids;
    }

    public String inIdParentesco() {
        String ids = null;
        for (Parentesco listParentesco1 : listParentesco) {
            if (listParentesco1.getSelected()) {
                ids = "" + listParentesco1.getId();
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

    public Boolean getBooDescontoSocial() {
        return booDescontoSocial;
    }

    public void setBooDescontoSocial(Boolean booDescontoSocial) {
        this.booDescontoSocial = booDescontoSocial;
    }

}
