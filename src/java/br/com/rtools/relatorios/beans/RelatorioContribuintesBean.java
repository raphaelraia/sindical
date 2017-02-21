package br.com.rtools.relatorios.beans;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.dao.ConvencaoDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadeDao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.endereco.Bairro;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.impressao.ParametroContribuintes;
import br.com.rtools.pessoa.CentroComercial;
import br.com.rtools.pessoa.Cnae;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioContribuintesDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DateFilters;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
public class RelatorioContribuintesBean implements Serializable {

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;
    private List<SelectItem> listContabilidades;
    private Integer idContabilidade;
    private String tipoEscritorio;
    private String tipoEmail;
    private String envioLogin;
    private String tipoCentroComercial;
    private String tipoCidade;
    private Map<String, Integer> listCidades;
    private List selectedCidade;
    private List<SelectItem> listTipoServico;
    private Integer idTipoServico;
    private Map<String, Integer> listServicos;
    private List selectedServicos;
    private Map<String, Integer> listCentrosComerciais;
    private List selectedCentroComercial;
    private List selectedConvencao;
    private List selectedGrupoCidades;
    private List selectedCnae;
    private Map<String, Integer> listConvencoes;
    private Map<String, Integer> listGrupoCidades;
    private Map<String, Integer> listCnaes;
    private String ordem;
    private String comboCondicao;
    private List resultConvencao;
    private List resultCnaeConvencao;
    private Bairro bairro;
    private List<SelectItem> listTipoEndereco;
    private Integer idTipoEndereco;
    private String dataValorInicial;
    private String dataValorFinal;
    private String valorInicial;
    private String valorFinal;
    private String tipoDataValor;
    private List<Filters> filters;
    // DATAS
    private List<DateFilters> listDateFilters;
    private List<SelectItem> listDates;
    private String selectedDate;
    private String typeDate;
    private String startDate;
    private String finishDate;
    private Juridica empresa;
    private List<Juridica> listEmpresa;

    public RelatorioContribuintesBean() {
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("simplesPesquisa");
        empresa = null;
        listEmpresa = new ArrayList();
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        tipoEscritorio = "todos";
        tipoCentroComercial = "nenhum";
        ordem = "razao";
        tipoCidade = "todas";
        comboCondicao = "ativos";
        idContabilidade = 0;
        idTipoEndereco = 0;
        resultConvencao = new ArrayList();
        resultCnaeConvencao = new ArrayList();
        bairro = null;
        listTipoEndereco = new ArrayList<>();
        selectedConvencao = new ArrayList<>();
        selectedGrupoCidades = new ArrayList<>();
        selectedCnae = new ArrayList<>();
        listCnaes = null;
        listConvencoes = null;
        listGrupoCidades = null;
        loadListServicos();
        Jasper jasper = new Jasper();
        jasper.init();
        loadListTipoEndereco();
        loadFilters();
        loadRelatorios();
        loadRelatoriosOrdem();
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("escritorio", "Escritório", false));
        filters.add(new Filters("empresa", "Empresas", false));
        filters.add(new Filters("envio_login", "Envio de Login", false));
        filters.add(new Filters("cidade", "Cidade", false));
        filters.add(new Filters("centro_comercial", "Centro Comercial", false));
        filters.add(new Filters("datas", "Datas", false));
        filters.add(new Filters("email", "Email", false));
        filters.add(new Filters("cnae_convencao", "Cnae/Convenção", false));
        filters.add(new Filters("valor_pagto", "Valor de Pagto", false));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters filters = new Filters();
        filters.setKey(filter);
        filters.setActive(false);
        for (Filters f : this.filters) {
            if (f.getKey().equals(filter)) {
                f.setActive(false);
            }
        }
        load(filters);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "escritorio":
                idContabilidade = null;
                listContabilidades = new ArrayList<>();
                if (filter.getActive()) {
                    loadListContabilidades();
                }
                break;
            case "envio_login":
                envioLogin = "todos";
                break;
            case "cidade":
                tipoCidade = "";
                listCidades = new LinkedHashMap<>();
                selectedCidade = new ArrayList();
                if (filter.getActive()) {
                    loadListCidades();
                }
                break;
            case "centro_comercial":
                listCentrosComerciais = new LinkedHashMap<>();
                selectedCentroComercial = null;
                if (filter.getActive()) {
                    loadListCentroComercial();
                }
                break;
            case "datas":
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
            case "email":
                tipoEmail = "email";
                break;
            case "cnae_convencao":
                if (filter.getActive()) {
                    loadListConvencoes();
                    loadListCnaes();
                    loadListGrupoCidades();
                }
                break;
            case "bairro":
                bairro = null;
                break;
            case "valor_pagto":
                dataValorInicial = "";
                dataValorFinal = "";
                valorInicial = "0";
                valorFinal = "0";
                tipoDataValor = "vencimento";
                listServicos = new LinkedHashMap<>();
                selectedServicos = new ArrayList();
                listTipoServico = new ArrayList();
                idTipoServico = null;
                if (filter.getActive()) {
                    loadListServicos();
                    loadListTipoServico();
                }
                break;
            case "empresa":
                empresa = null;
                listEmpresa = new ArrayList();
                if (filter.getActive()) {
                }
                break;

        }
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "tipo_escritorio":
                listContabilidades = new ArrayList<>();
                if (tipoEscritorio.equals("especifico")) {
                    loadListContabilidades();
                }
                break;
            case "centro_comercial":
                switch (tipoCentroComercial) {
                    case "com":
                        loadListCentroComercial();
                        idTipoEndereco = 3;
                        break;
                    case "nenhum":
                        listCentrosComerciais = new LinkedHashMap<>();
                        selectedCentroComercial = new ArrayList();
                        break;
                    case "sem":
                        selectedCentroComercial = new ArrayList();
                        loadListCentroComercial();
                        for (Map.Entry<String, Integer> lcc : listCentrosComerciais.entrySet()) {
                            selectedCentroComercial.add(lcc.getValue());
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "valor_pagto_1":
                dataValorInicial = "";
                dataValorFinal = "";
                break;
            case "valor_pagto_2":
                valorInicial = "0";
                valorFinal = "0";
                break;
            case "convencao":
                loadListCnaes();
                loadListGrupoCidades();
                break;
            case "relatorios":
                if (listRelatorio.get(idRelatorio).getLabel().toUpperCase().contains("ESCRITÓRIO")) {
                    for (int i = 0; i < filters.size(); i++) {
                        if (filters.get(i).getKey().equals("escritorio")) {
                            filters.get(i).setActive(true);
                            load(filters.get(i));
                            tipoEscritorio = "comEscritorio";
                        }
                    }
                }
                loadRelatoriosOrdem();
                break;

        }
    }

    public Boolean isFiltroSelecionado() {
        List<Filters> list = new ArrayList();
        list.addAll(filters);
        for (Filters list1 : list) {
            if (list1.getActive()) {
                return true;
            }
        }
        return false;
    }

    public void loadDates() {
        listDates = new ArrayList();
        listDates.add(new SelectItem("atualizacao", "Atualização"));
        listDates.add(new SelectItem("cadastro", "Cadastro"));
        listDates.add(new SelectItem("recadastro", "Recadastro"));
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

    public final void loadRelatorios() {
        listRelatorio = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        for (int i = 0; i < list.size(); i++) {
            Boolean disabled = false;
            if (i == 0) {
                idRelatorio = list.get(i).getId();
            }
            if (list.get(i).getPrincipal()) {
                idRelatorio = list.get(i).getId();
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome(), "", disabled));
        }
    }

    public final void loadRelatoriosOrdem() {
        listRelatorioOrdem = (List<SelectItem>) new ArrayList();
        idRelatorioOrdem = 0;
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
                if (list.get(i).getPrincipal()) {
                    idRelatorioOrdem = list.get(i).getId();
                }
            }
        }
    }

    public final void loadListCentroComercial() {
        listCentrosComerciais = new LinkedHashMap<>();
        selectedCentroComercial = new ArrayList();
        List<CentroComercial> list = (List<CentroComercial>) new Dao().list(new CentroComercial(), true);
        for (int i = 0; i < list.size(); i++) {
            listCentrosComerciais.put(list.get(i).getJuridica().getPessoa().getNome(), list.get(i).getJuridica().getPessoa().getId());
        }
    }

    public final void loadListServicos() {
        listServicos = new LinkedHashMap<>();
        selectedServicos = new ArrayList();
        List<Servicos> list = new RelatorioContribuintesDao().listaServicos();
        for (int i = 0; i < list.size(); i++) {
            listServicos.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public final void loadListTipoServico() {
        listTipoServico = new ArrayList();
        idTipoEndereco = 0;
        List<TipoServico> list = new RelatorioContribuintesDao().listaTipoServico();
        listTipoServico.add(new SelectItem(0, "Selecionar Tipo Serviço"));
        for (int i = 0; i < list.size(); i++) {
            listTipoServico.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }

    }

    public final void loadListTipoEndereco() {
        listTipoEndereco = new ArrayList();
        List<TipoEndereco> list = (List<TipoEndereco>) new Dao().find("TipoEndereco", new int[]{2, 3, 4, 5});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idTipoEndereco = list.get(i).getId();
            }
            listTipoEndereco.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public final void loadListContabilidades() {
        listContabilidades = new ArrayList<>();
        List<Juridica> list = new RelatorioContribuintesDao().pesquisaContabilidades();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idContabilidade = list.get(i).getId();
            }
            listContabilidades.add(new SelectItem(list.get(i).getId(), list.get(i).getPessoa().getNome() + " - " + list.get(i).getPessoa().getDocumento()));
        }
    }

    public final void loadListCidades() {
        listCidades = new LinkedHashMap<>();
        selectedCidade = new ArrayList();
        List<Cidade> list = new RelatorioDao().pesquisaCidadesRelatorio();
        for (int i = 0; i < list.size(); i++) {
            listCidades.put(list.get(i).getCidade(), list.get(i).getId());
        }
    }

    public final void loadListConvencoes() {
        listConvencoes = new LinkedHashMap<>();
        selectedConvencao = new ArrayList();
        List<Convencao> list = new ConvencaoDao().listaConvencao();
        for (int i = 0; i < list.size(); i++) {
            listConvencoes.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public final void loadListCnaes() {
        listCnaes = new LinkedHashMap<>();
        selectedCnae = new ArrayList();
        String ids = inIdConvencao();
        if (!ids.isEmpty()) {
            List<Cnae> list = (List<Cnae>) new OposicaoDao().listaCnaesPorOposicaoJuridica(ids);
            for (int i = 0; i < list.size(); i++) {
                listCnaes.put(list.get(i).getCnae() + " - " + list.get(i).getNumero(), list.get(i).getId());
            }
        }
    }

    public final void loadListGrupoCidades() {
        listGrupoCidades = new LinkedHashMap<>();
        selectedGrupoCidades = new ArrayList();
        String ids = inIdConvencao();
        if (!ids.isEmpty()) {
            List<GrupoCidade> list = new GrupoCidadeDao().listaGrupoCidadePorConvencao(ids);
            for (int i = 0; i < list.size(); i++) {
                listGrupoCidades.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public String inIdCentroComercial() {
        String ids = null;
        if (selectedCentroComercial != null) {
            for (int i = 0; i < selectedCentroComercial.size(); i++) {
                if (selectedCentroComercial.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedCentroComercial.get(i);
                    } else {
                        ids += "," + selectedCentroComercial.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdCidades() {
        String ids = null;
        if (selectedCidade != null) {
            for (int i = 0; i < selectedCidade.size(); i++) {
                if (selectedCidade.get(i) != null) {
                    if (ids == null) {
                        ids = "" + selectedCidade.get(i);
                    } else {
                        ids += "," + selectedCidade.get(i);
                    }
                }
            }
        }
        return ids;
    }

    public String inIdEmpresas() {
        String ids = null;
        if (listEmpresa != null && !listEmpresa.isEmpty()) {
            for (int i = 0; i < listEmpresa.size(); i++) {
                if (listEmpresa.get(i) != null) {
                    if (ids == null) {
                        ids = "" + listEmpresa.get(i).getPessoa().getId();
                    } else {
                        ids += "," + listEmpresa.get(i).getPessoa().getId();
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

    public String inIdConvencao() {
        String ids = "";
        if (selectedConvencao != null) {
            for (int i = 0; i < selectedConvencao.size(); i++) {
                if (ids.isEmpty()) {
                    ids = "" + selectedConvencao.get(i);
                } else {
                    ids += "," + selectedConvencao.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdGruposCidade() {
        String ids = "";
        if (selectedGrupoCidades != null) {
            for (int i = 0; i < selectedGrupoCidades.size(); i++) {
                if (ids.isEmpty()) {
                    ids = "" + selectedGrupoCidades.get(i);
                } else {
                    ids += "," + selectedGrupoCidades.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdCnaes() {
        String ids = "";
        if (selectedCnae != null) {
            for (int i = 0; i < selectedCnae.size(); i++) {
                if (ids.isEmpty()) {
                    ids = "" + selectedCnae.get(i);
                } else {
                    ids += "," + selectedCnae.get(i);
                }
            }

        }
        return ids;
    }

    public void addEmpresa() {
        if (empresa != null) {
            listEmpresa.add(empresa);
        }
        empresa = null;
    }

    public void removeEmpresa(Juridica empresa) {
        listEmpresa.remove(empresa);
    }

    public void print() {
        if (!isFiltroSelecionado()) {
            GenericaMensagem.warn("Validação", "SELECIONAR UM FILTRO PARA REALIZAR A PESQUISA!");
            return;
        }

        String escritorio = "";
        String centros = "",
                in_enderecos = "",
                numeros = "";
        String in_bairros = "";

        RelatorioContribuintesDao rcd = new RelatorioContribuintesDao();
        rcd.setRelatorios((Relatorios) new Dao().find(new Relatorios(), idRelatorio));
        rcd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));

        PessoaEnderecoDao dbPesEnd = new PessoaEnderecoDao();
        Cidade cidade;
        // CONDICAO DO RELATORIO -----------------------------------------------------------
        String condicao = comboCondicao;

        // ESCRITORIO DO RELATORIO -----------------------------------------------------------
        if (getShow("escritorio")) {
            switch (tipoEscritorio) {
                case "todos":
                    escritorio = "todos";
                    break;
                case "semEscritorio":
                    escritorio = "semEscritorio";
                    break;
                case "comEscritorio":
                    escritorio = "comEscritorio";
                    break;
                case "especifico":
                    Juridica contabilidade = (Juridica) new Dao().find(new Juridica(), idContabilidade);
                    escritorio = Integer.toString(contabilidade.getId());
                    break;
            }
        }
        String in_cidades = "";
        switch (tipoCidade) {
            case "especificas":
                in_cidades = inIdCidades();
                break;
            case "local":
                cidade = dbPesEnd.pesquisaEndPorPessoaTipo(1, 2).getEndereco().getCidade();
                in_cidades = Integer.toString(cidade.getId());
                break;
            case "outras":
                cidade = dbPesEnd.pesquisaEndPorPessoaTipo(1, 2).getEndereco().getCidade();
                in_cidades = Integer.toString(cidade.getId());
                break;
        }
        if (bairro != null) {
            List<Bairro> listaBairro = new ArrayList();
            listaBairro.add((Bairro) new Dao().find(bairro));
            if (!listaBairro.isEmpty()) {
                for (int i = 0; i < listaBairro.size(); i++) {
                    if (in_bairros.length() > 0 && i != listaBairro.size()) {
                        in_bairros += ",";
                    }
                    in_bairros += Integer.toString(listaBairro.get(i).getId());
                }
            }
        }

        // CENTRO COMERCIAL -----------------------------------------------------------
        List idsEnderecos;
        if (getShow("centro_comercial")) {
            if (selectedCentroComercial == null || selectedCentroComercial.isEmpty()) {
                GenericaMensagem.warn("Sistema", "Selecione pelo menos um centro comercial.");
                return;
            }
            String in_centro_comercial = inIdCentroComercial();
            if (in_centro_comercial != null && !in_centro_comercial.isEmpty()) {
                idsEnderecos = rcd.listaCentros(in_centro_comercial);
                for (int i = 0; i < idsEnderecos.size(); i++) {
                    if (in_enderecos.length() > 0 && i != idsEnderecos.size()) {
                        in_enderecos += ",";
                        numeros += ",";
                    }
                    //if (radioCentroComercial.equals("com")){
                    in_enderecos += ((List) idsEnderecos.get(i)).get(0);
                    numeros += "'" + ((List) idsEnderecos.get(i)).get(1) + "'";
                    //} else
                    //    enderecos += idsEnderecos.get(i).get(2);
                }
            }
        }

        // VALORES DO RELATÓRIO
        String valor_inicial = null, valor_final = null, data_valor_inicial = null, data_valor_final = null;
        if (Moeda.converteUS$(valorInicial) > 0 || Moeda.converteUS$(valorFinal) > 0) {
            if (Moeda.converteUS$(valorInicial) > Moeda.converteUS$(valorFinal)) {
                GenericaMensagem.warn("Sistema", "Valor Inicial não pode ser maior que Final!");
                return;
            }

            valor_inicial = valorInicial;
            valor_final = valorFinal;

            if (dataValorInicial.isEmpty() || dataValorFinal.isEmpty()) {
                GenericaMensagem.warn("Sistema", "Preencha todas as Datas!");
                return;
            }

            if (DataHoje.maiorData(dataValorInicial, dataValorFinal)) {
                GenericaMensagem.warn("Sistema", "Data Inicial não pode ser maior que Data Final!");
                return;
            }

            data_valor_inicial = dataValorInicial;
            data_valor_final = dataValorFinal;
        }

        String inCentroComercial = "";
        Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
        PessoaEndereco endSindicato = dbPesEnd.pesquisaEndPorPessoaTipo(1, 3);

        List<List> list = rcd.find(
                condicao,
                escritorio,
                tipoCidade,
                in_cidades,
                idTipoEndereco,
                in_enderecos,
                tipoCentroComercial,
                inIdCentroComercial(),
                numeros,
                in_bairros,
                inIdConvencao(),
                inIdCnaes(),
                inIdGruposCidade(),
                tipoEmail,
                envioLogin,
                valor_inicial,
                valor_final,
                data_valor_inicial,
                data_valor_final,
                tipoDataValor,
                inIdServicos(),
                idTipoServico,
                inIdEmpresas(),
                /**
                 * DATAS
                 */
                listDateFilters
        );
        if (list.isEmpty()) {
            GenericaMensagem.info("Sistema", "Não existem registros para o relatório selecionado");
            return;
        }
        FacesContext faces = FacesContext.getCurrentInstance();
        List<ParametroContribuintes> c = new ArrayList<>();
        try {
            for (int i = 0; i < list.size(); i++) {
                c.add(new ParametroContribuintes(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
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
                        getConverteNullInt((list.get(i)).get(0)), // ID
                        getConverteNullString((list.get(i)).get(1)), // NOME PESSOA
                        getConverteNullString((list.get(i)).get(4)), // DESCRICAO ENDERECO
                        getConverteNullString((list.get(i)).get(3)), // LOGRADOURO
                        getConverteNullString((list.get(i)).get(7)), // NUMERO
                        getConverteNullString((list.get(i)).get(8)), // COMPLEMENTO
                        getConverteNullString((list.get(i)).get(11)), // BAIRRO
                        getConverteNullString((list.get(i)).get(9)), // CEP
                        getConverteNullString((list.get(i)).get(5)), // CIDADE
                        getConverteNullString((list.get(i)).get(6)), // UF
                        getConverteNullString((list.get(i)).get(12)), // TELEFONE
                        getConverteNullString((list.get(i)).get(13)), // EMAIL
                        getConverteNullString((list.get(i)).get(14)), // TIPO DOCUMENTO
                        getConverteNullString((list.get(i)).get(2)), // DOCUMENTO
                        getConverteNullInt((list.get(i)).get(15)), //ID CNAE
                        getConverteNullString((list.get(i)).get(16)), // NUMERO CNAE
                        getConverteNullString((list.get(i)).get(17)), // DESCRICAO CNAE
                        getConverteNullInt((list.get(i)).get(18)), // ID CONTABILIDADE
                        getConverteNullString((list.get(i)).get(10)), // NOME CONTABILIDADE
                        getConverteNullString((list.get(i)).get(20)), // DESCRICAO ENDERECO CONTABILIDADE
                        getConverteNullString((list.get(i)).get(19)), // LOGRADOURO CONTABILIDADE
                        getConverteNullString((list.get(i)).get(24)), // NUMERO CONTABILIDADE
                        getConverteNullString((list.get(i)).get(25)), // COMPLEMENTO CONTABILIDADE
                        getConverteNullString((list.get(i)).get(21)), // BAIRRO CONTABILIDADE
                        getConverteNullString((list.get(i)).get(26)), // CEP CONTABILIDADE
                        getConverteNullString((list.get(i)).get(22)), // CIDADE CONTABILIDADE
                        getConverteNullString((list.get(i)).get(23)), // UF CONTABILIDADE
                        getConverteNullString((list.get(i)).get(27)), // TELEFONE CONTABILIDADE
                        getConverteNullString((list.get(i)).get(28)) // EMAIL CONTABILIDADE
                ));
            }
            Jasper.printReports(rcd.getRelatorios().getJasper(), rcd.getRelatorios().getNome(), (Collection) c);
        } catch (Exception erro) {
            GenericaMensagem.info("Sistema", "O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public Boolean getShow(String filtro) {
        try {
            for (Filters f2 : filters) {
                if (f2.getKey().equals(filtro)) {
                    if (f2.getActive()) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public String getDateItemDescription(String title) {
        for (int x = 0; x < listDates.size(); x++) {
            if (title.equals(listDates.get(x).getValue().toString())) {
                return listDates.get(x).getLabel();
            }
        }
        return "";
    }

    public Object getConverterNull(Object object) {
        return object;
    }

    public String getConverteNullString(Object object) {
        if (object == null) {
            return "";
        } else {
            return String.valueOf(object);
        }
    }

    public int getConverteNullInt(Object object) {
        if (object == null) {
            return 0;
        } else {
            return (Integer) object;
        }
    }

    public Map<String, Integer> getListCnaes() {
        return listCnaes;
    }

    public void setListCnaes(Map<String, Integer> listCnaes) {
        this.listCnaes = listCnaes;
    }

    public Map<String, Integer> getListConvencaos() {
        return listConvencoes;
    }

    public void setListConvencaos(Map<String, Integer> listConvencoes) {
        this.listConvencoes = listConvencoes;
    }

    public Map<String, Integer> getListGrupoCidades() {
        return listGrupoCidades;
    }

    public void setListGrupoCidades(HashMap<String, Integer> listGrupoCidades) {
        this.listGrupoCidades = listGrupoCidades;
    }

    public List getSelectedConvencao() {
        return selectedConvencao;
    }

    public void setSelectedConvencao(List selectedConvencao) {
        this.selectedConvencao = selectedConvencao;
    }

    public List getSelectedGrupoCidades() {
        return selectedGrupoCidades;
    }

    public void setSelectedGrupoCidades(List selectedGrupoCidades) {
        this.selectedGrupoCidades = selectedGrupoCidades;
    }

    public List getSelectedCnae() {
        return selectedCnae;
    }

    public void setSelectedCnae(List selectedCnae) {
        this.selectedCnae = selectedCnae;
    }

    public String getTipoCidade() {
        return tipoCidade;
    }

    public void setTipoCidade(String tipoCidade) {
        this.tipoCidade = tipoCidade;
    }

    public Integer getIdContabilidade() {
        return idContabilidade;
    }

    public void setIdContabilidade(Integer idContabilidade) {
        this.idContabilidade = idContabilidade;
    }

    public String getComboCondicao() {
        return comboCondicao;
    }

    public void setComboCondicao(String comboCondicao) {
        this.comboCondicao = comboCondicao;
    }

    public Integer getIdTipoEndereco() {
        return idTipoEndereco;
    }

    public void setIdTipoEndereco(Integer idTipoEndereco) {
        this.idTipoEndereco = idTipoEndereco;
    }

    public String getEnvioLogin() {
        return envioLogin;
    }

    public void setEnvioLogin(String envioLogin) {
        this.envioLogin = envioLogin;
    }

    public String getTipoCentroComercial() {
        return tipoCentroComercial;
    }

    public void setTipoCentroComercial(String tipoCentroComercial) {
        this.tipoCentroComercial = tipoCentroComercial;
    }

    public Bairro getBairro() {
        if (GenericaSessao.exists("simplesPesquisa")) {
            bairro = (Bairro) GenericaSessao.getObject("simplesPesquisa", true);
        }
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getTipoEmail() {
        return tipoEmail;
    }

    public void setTipoEmail(String tipoEmail) {
        this.tipoEmail = tipoEmail;
    }

    public String getDataValorInicial() {
        return dataValorInicial;
    }

    public void setDataValorInicial(String dataValorInicial) {
        this.dataValorInicial = dataValorInicial;
    }

    public String getDataValorFinal() {
        return dataValorFinal;
    }

    public void setDataValorFinal(String dataValorFinal) {
        this.dataValorFinal = dataValorFinal;
    }

    public String getValorInicial() {
        return Moeda.converteR$(valorInicial);
    }

    public void setValorInicial(String valorInicial) {
        this.valorInicial = Moeda.converteR$(valorInicial);
    }

    public String getValorFinal() {
        return Moeda.converteR$(valorFinal);
    }

    public void setValorFinal(String valorFinal) {
        this.valorFinal = Moeda.converteR$(valorFinal);
    }

    public String getTipoDataValor() {
        return tipoDataValor;
    }

    public void setTipoDataValor(String tipoDataValor) {
        this.tipoDataValor = tipoDataValor;
    }

    public List<SelectItem> getListTipoServico() {
        return listTipoServico;
    }

    public void setListTipoServico(List<SelectItem> listTipoServico) {
        this.listTipoServico = listTipoServico;
    }

    public Integer getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(Integer idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public List<Filters> getFilters() {
        return filters;
    }

    public void setFilters(List<Filters> filters) {
        this.filters = filters;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List getResultConvencao() {
        return resultConvencao;
    }

    public void setResultConvencao(List resultConvencao) {
        this.resultConvencao = resultConvencao;
    }

    public List getResultCnaeConvencao() {
        return resultCnaeConvencao;
    }

    public void setResultCnaeConvencao(List resultCnaeConvencao) {
        this.resultCnaeConvencao = resultCnaeConvencao;
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

    public String getTypeDate() {
        return typeDate;
    }

    public void setTypeDate(String typeDate) {
        this.typeDate = typeDate;
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

    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
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

    public List<SelectItem> getListContabilidades() {
        return listContabilidades;
    }

    public void setListContabilidades(List<SelectItem> listContabilidades) {
        this.listContabilidades = listContabilidades;
    }

    public String getTipoEscritorio() {
        return tipoEscritorio;
    }

    public void setTipoEscritorio(String tipoEscritorio) {
        this.tipoEscritorio = tipoEscritorio;
    }

    public Map<String, Integer> getListCidades() {
        return listCidades;
    }

    public void setListCidades(Map<String, Integer> listCidades) {
        this.listCidades = listCidades;
    }

    public List getSelectedCidade() {
        return selectedCidade;
    }

    public void setSelectedCidade(List selectedCidade) {
        this.selectedCidade = selectedCidade;
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

    public Map<String, Integer> getListCentrosComerciais() {
        return listCentrosComerciais;
    }

    public void setListCentrosComerciais(Map<String, Integer> listCentrosComerciais) {
        this.listCentrosComerciais = listCentrosComerciais;
    }

    public List getSelectedCentroComercial() {
        return selectedCentroComercial;
    }

    public void setSelectedCentroComercial(List selectedCentroComercial) {
        this.selectedCentroComercial = selectedCentroComercial;
    }

    public List<SelectItem> getListTipoEndereco() {
        return listTipoEndereco;
    }

    public void setListTipoEndereco(List<SelectItem> listTipoEndereco) {
        this.listTipoEndereco = listTipoEndereco;
    }

    public Map<String, Integer> getListConvencoes() {
        return listConvencoes;
    }

    public void setListConvencoes(Map<String, Integer> listConvencoes) {
        this.listConvencoes = listConvencoes;
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

    public List<Juridica> getListEmpresa() {
        return listEmpresa;
    }

    public void setListEmpresa(List<Juridica> listEmpresa) {
        this.listEmpresa = listEmpresa;
    }
}
