package br.com.rtools.relatorios.beans;

import br.com.rtools.academia.AcademiaServicoValor;
import br.com.rtools.academia.dao.AcademiaDao;
import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.db.CategoriaDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioAcademiaDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.PF;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class RelatorioAcademiaBean implements Serializable {

    private Pessoa aluno;
    private Pessoa responsavel;
    private List selectedModalidades;
    private List selectedPeriodos;
    private List<SelectItem>[] listSelectItem;
    private Map<String, Integer> listModalidades;
    private Map<String, Integer> listPeriodos;
    private Boolean[] filtro;
    private Date dataInicial;
    private Date dataFinal;
    private Integer[] index;
    private Integer[] idade;
    private String tipoRelatorio;
    private String tipo;
    private String indexAccordion;
    private String order;
    private String sexo;
    private String periodo;
    private Relatorios relatorios;
    private List selectedGrupoCategoria;
    private List selectedCategoria;
    private List selectedConvenioEmpresa;
    private Map<String, Integer> listCategoria;
    private Map<String, Integer> listGrupoCategoria;
    private Float desconto;
    private Float descontoFinal;
    private Boolean situacao;
    private String situacaoString;
    private String situacaoFinanceira;
    private Integer carenciaDias;
    private String tipoCarencia;
    private String matricula_situacao;
    private Boolean mostrarDataInativacao;
    private Rotina rotina;
    private String vencimentoInicial;
    private String vencimentoFinal;
    private String quitacaoInicial;
    private String quitacaoFinal;

    @PostConstruct
    public void init() {
        filtro = new Boolean[17];
        filtro[0] = false; // MODALIDADE
        filtro[1] = false; // PERÍODO EMISSÃO / INATIVAÇÃO
        filtro[2] = false; // RESPONSÁVEL
        filtro[3] = false; // ALUNO
        filtro[4] = false; // SEXO
        filtro[5] = false; // ORDER
        filtro[6] = false; // PERIODO
        filtro[7] = true; // ATIVO
        filtro[8] = false; // IDADE
        filtro[9] = false; // GRUPO CATEGORIA
        filtro[10] = null; // NÃO SÓCIO
        filtro[11] = false; // CONVÊNIO EMPRESA
        filtro[12] = false; // FAIXA DESCONTO
        filtro[13] = false; // SITUAÇÃO
        filtro[14] = false; // SITUAÇÃO FINANCEIRA
        filtro[15] = false; // FAIXA VENCIMENTO
        filtro[16] = false; // FAIXA QUITAÇÃO
        listSelectItem = new ArrayList[2];
        listSelectItem[0] = new ArrayList<>();
        listSelectItem[1] = new ArrayList<>();
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        index = new Integer[2];
        index[0] = 0;
        index[1] = 0;
        idade = new Integer[2];
        idade[0] = 0;
        idade[1] = 0;
        tipoRelatorio = "Simples";
        indexAccordion = "Simples";
        order = "PA.nome, SP.dt_emissao";
        aluno = new Pessoa();
        responsavel = new Pessoa();
        sexo = "";
        periodo = null;
        tipo = "todos";
        Jasper.EXPORT_TO = false;
        selectedGrupoCategoria = null;
        selectedCategoria = null;
        selectedConvenioEmpresa = null;
        listCategoria = null;
        listGrupoCategoria = null;
        desconto = new Float(0);
        descontoFinal = new Float(0);
        situacaoString = null;
        situacaoFinanceira = null;
        situacao = false;
        carenciaDias = null;
        tipoCarencia = "todos";
        mostrarDataInativacao = false;
        rotina = new Rotina().get();
        vencimentoInicial = "";
        vencimentoFinal = "";
        quitacaoInicial = "";
        quitacaoFinal = "";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioAcademiaBean");
        GenericaSessao.remove("pessoaPesquisa");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("juridicaPesquisa");
        GenericaSessao.remove("tipoPesquisaPessoaJuridica");
    }

    public void print() throws FileNotFoundException {
        print(0);
    }

    public void print(int tcase) throws FileNotFoundException {
        if (!getListTipoRelatorios().isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            relatorios = rgdb.pesquisaRelatorios(index[0]);
        } else {
            GenericaMensagem.info("Sistema", "Nenhum relatório encontrado!");
            return;
        }
        if (relatorios == null) {
            return;
        }
        String detalheRelatorio = "";
        Integer idResponsavel = null;
        Integer idAluno = null;
        String pIStringI = "";
        String pFStringI = "";
        String vIStringI = "";
        String vFStringI = "";
        String qIStringI = "";
        String qFStringI = "";
        String sexoString = "";
        String dReferencia = "";
        String inIdModalidades = inIdModalidades();
        String inIdPeriodos = inIdPeriodos();
        List listDetalhePesquisa = new ArrayList();
        if (filtro[1]) {
            pIStringI = DataHoje.converteData(dataInicial);
            pFStringI = DataHoje.converteData(dataFinal);
            if (periodo != null) {
                if (periodo.equals("inativacao")) {
                    listDetalhePesquisa.add(" Período de Inativação entre " + pIStringI + " e " + pFStringI);
                } else if (periodo.equals("emissao")) {
                    listDetalhePesquisa.add(" Período de Emissão entre " + pIStringI + " e " + pFStringI);
                }
            }
        }
        if (!filtro[8]) {
            idade[0] = 0;
            idade[0] = 0;
        }
        if (filtro[4]) {
            switch (sexo) {
                case "M":
                    sexoString = "Masculino";
                    break;
                case "F":
                    sexoString = "Feminino";
                    break;
                default:
                    sexoString = "Todos";
                    break;
            }
            listDetalhePesquisa.add(" Sexo: " + sexoString + "");
        }
        if (filtro[13]) {
            listDetalhePesquisa.add(" Tipo de Carência: " + tipoCarencia.toUpperCase() + "");
            listDetalhePesquisa.add(" Situação: " + situacaoString.toUpperCase() + "");
            if (carenciaDias > 0) {
                listDetalhePesquisa.add(" Carência Dias: " + carenciaDias + "");
            }
        }
        if (!dReferencia.isEmpty()) {
            listDetalhePesquisa.add(" Período convenção: " + dReferencia + "");
        }
        if (aluno.getId() != -1) {
            idAluno = aluno.getId();
            listDetalhePesquisa.add(" Aluno CPF: " + aluno.getDocumento() + " - " + aluno.getNome());
        }
        if (responsavel.getId() != -1) {
            idResponsavel = responsavel.getId();
            listDetalhePesquisa.add(" Responsável: " + responsavel.getDocumento() + " - " + responsavel.getNome());
        }
        if (order == null) {
            order = "";
        }
        String in_grupo_categoria = inIdGrupoCategoria();
        String in_categoria = inIdCategoria();
        Boolean nao_socio = null;
        Boolean convenio_empresa = null;
        if (filtro[9]) {
            if (filtro[10] != null && filtro[10]) {
                nao_socio = true;
            }
            if (filtro[11] != null && filtro[11]) {
                convenio_empresa = true;
            }
            if (in_categoria.isEmpty()) {
                in_categoria = null;
            }

        }

        Float desconto_inicial = null;
        Float desconto_final = null;
        if (filtro[12]) {
            desconto_inicial = desconto;
            desconto_final = descontoFinal;
        }

        RelatorioAcademiaDao rad = new RelatorioAcademiaDao();
        rad.setRelatorios(relatorios);
        if(relatorios.getId() == 32) {
            relatorios.setQueryString("PA.nome, MV.dt_vencimento");
            order = "PA.nome, MV.dt_vencimento";
        }
        List list = rad.find(
                pIStringI,
                pFStringI,
                idResponsavel,
                idAluno,
                inIdModalidades,
                inIdPeriodos,
                sexo,
                periodo,
                matricula_situacao,
                idade,
                in_grupo_categoria,
                in_categoria,
                nao_socio,
                convenio_empresa,
                desconto_inicial,
                desconto_final,
                tipoCarencia,
                carenciaDias,
                situacaoString,
                situacaoFinanceira,
                vencimentoInicial,
                vencimentoFinal,
                quitacaoInicial,
                quitacaoFinal,
                order
        );
        if (list.isEmpty()) {
            GenericaMensagem.info("Sistema", "Não existem registros para o relatório selecionado");
            return;
        }

        if (listDetalhePesquisa.isEmpty()) {
            detalheRelatorio += "Pesquisar todos registros!";
        } else {
            detalheRelatorio += "";
            for (int i = 0; i < listDetalhePesquisa.size(); i++) {
                if (i == 0) {
                    detalheRelatorio += "" + listDetalhePesquisa.get(i).toString();
                } else {
                    detalheRelatorio += "; " + listDetalhePesquisa.get(i).toString();
                }
            }
        }
        String nascimento = "";
        String emissao = "";
        String inativacao = "";
        List<ParametroAcademiaCadastral> pacs = new ArrayList<>();
        ParametroAcademiaCadastral pac = new ParametroAcademiaCadastral();
        for (Object list1 : list) {
            List o = (List) list1;
            if (relatorios.getId() == 31) {
                pac = new ParametroAcademiaCadastral(
                        o.get(0),
                        o.get(1),
                        o.get(2),
                        o.get(3),
                        o.get(4),
                        o.get(6),
                        o.get(7),
                        o.get(5),
                        o.get(8),
                        o.get(9)
                );
            } else if (relatorios.getId() == 32) {
                pac = new ParametroAcademiaCadastral(
                        o.get(0),
                        o.get(1),
                        o.get(2),
                        o.get(3),
                        o.get(4),
                        o.get(6),
                        o.get(7),
                        o.get(5),
                        o.get(8),
                        o.get(9),
                        o.get(10),
                        o.get(11),
                        o.get(12),
                        o.get(13)
                );
            }
            pacs.add(pac);
        }
        if (!pacs.isEmpty()) {
            Jasper.TITLE = relatorios.getNome();
            if (relatorios.getExcel()) {
                Jasper.EXCEL_FIELDS = relatorios.getCamposExcel();
            } else {
                Jasper.EXCEL_FIELDS = "";
            }
            Map map = new HashMap();
            map.put("detalhes_relatorio", detalheRelatorio);
            if (mostrarDataInativacao && matricula_situacao.equals("inativos")) {
                map.put("matricula_situacao", matricula_situacao);
            } else {
                map.put("matricula_situacao", "");
            }
            Jasper.printReports(relatorios.getJasper(), "academia", (Collection) pacs, map);

        }

    }

    public List<SelectItem> getListTipoRelatorios() {
        if (listSelectItem[0].isEmpty()) {
            RelatorioDao db = new RelatorioDao();
            List<Relatorios> list = (List<Relatorios>) db.pesquisaTipoRelatorio(rotina.getId());
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    index[0] = list.get(i).getId();
                }
                listSelectItem[0].add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
            if (listSelectItem[0].isEmpty()) {
                listSelectItem[0] = new ArrayList<>();
            }
        }
        return listSelectItem[0];
    }

    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public void tipoRelatorioChange(TabChangeEvent event) {
        tipoRelatorio = event.getTab().getTitle();
        indexAccordion = ((AccordionPanel) event.getComponent()).getActiveIndex();
        if (tipoRelatorio.equals("Simples")) {
            clear();
            filtro[2] = false;
            filtro[0] = false;
        }
    }

    public void selecionaDataInicial(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataInicial = DataHoje.converte(format.format(event.getObject()));
    }

    public void selecionaDataFinal(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataFinal = DataHoje.converte(format.format(event.getObject()));
    }

    public void clear() {
        if (!filtro[0]) {
            selectedModalidades = null;
        }
        if (!filtro[1]) {
            dataInicial = DataHoje.dataHoje();
            dataFinal = DataHoje.dataHoje();
            periodo = null;
        }
        if (!filtro[2]) {
            responsavel = new Pessoa();
        }
        if (!filtro[3]) {
            aluno = new Pessoa();
        }
        if (!filtro[4]) {
            sexo = "";
        }
        if (!filtro[5]) {
            order = "PA.nome, SP.dt_emissao";
        }
        if (!filtro[6]) {
            selectedPeriodos = null;
        }
        if (!filtro[8]) {
            idade[0] = 0;
            idade[1] = 0;
        }
        if (!filtro[9]) {
            selectedGrupoCategoria = null;
            selectedCategoria = null;
            selectedConvenioEmpresa = null;
            filtro[10] = null;
            filtro[11] = false;
            listGrupoCategoria = null;
            listCategoria = null;
        } else {
            filtro[10] = false;
        }
        if (!filtro[13]) {
            situacaoString = null;
            situacao = situacao;
            carenciaDias = null;
            tipoCarencia = "todos";
        }
        if (!filtro[14]) {
            situacaoFinanceira = null;
        }
        if (!filtro[15]) {
            vencimentoInicial = "";
            vencimentoFinal = "";
        }
        if (!filtro[16]) {
            quitacaoInicial = "";
            quitacaoFinal = "";
        }
    }

    public void clear(Integer tcase) {
        switch (tcase) {
            case 0:
                listCategoria = null;
                break;
            case 1:
                clear();
                break;
        }
    }

    public void close(String close) {
        switch (close) {
            case "modalidade":
                selectedModalidades = null;
                listModalidades = null;
                filtro[0] = false;
                break;
            case "emissao_inativacao":
                filtro[1] = false;
                dataInicial = DataHoje.dataHoje();
                dataFinal = DataHoje.dataHoje();
                periodo = null;
                break;
            case "responsavel":
                responsavel = new Pessoa();
                filtro[2] = false;
                break;
            case "aluno":
                filtro[3] = false;
                aluno = new Pessoa();
                break;
            case "sexo":
                sexo = "";
                filtro[4] = false;
                break;
            case "order":
                order = "PA.nome, SP.dt_emissao";
                filtro[5] = false;
                break;
            case "periodo":
                filtro[6] = false;
                selectedPeriodos = null;
                listPeriodos = null;
                break;
            case "idade":
                filtro[8] = false;
                idade[0] = 0;
                idade[1] = 0;
                break;
            case "grupo_categoria":
                filtro[9] = false;
                filtro[10] = null;
                filtro[11] = false;
                listGrupoCategoria = null;
                listCategoria = null;
                selectedGrupoCategoria = null;
                selectedCategoria = null;
                selectedConvenioEmpresa = null;
                break;
            case "faixaDesconto":
                filtro[12] = false;
                break;
            case "situacao":
                situacaoString = null;
                situacao = false;
                carenciaDias = null;
                tipoCarencia = "todos";
                filtro[13] = false;
                break;
            case "situacao_financeira":
                situacaoFinanceira = null;
                filtro[14] = false;
                break;
            case "vencimento":
                vencimentoInicial = "";
                vencimentoFinal = "";
                filtro[15] = false;
                break;
            case "quitacao":
                quitacaoInicial = "";
                quitacaoFinal = "";
                filtro[16] = false;
                break;
        }
        PF.update("form_relatorio:id_panel");
    }

    public String getIndexAccordion() {
        return indexAccordion;
    }

    public void setIndexAccordion(String indexAccordion) {
        this.indexAccordion = indexAccordion;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<SelectItem>[] getListSelectItem() {
        return listSelectItem;
    }

    public void setListSelectItem(List<SelectItem>[] listSelectItem) {
        this.listSelectItem = listSelectItem;
    }

    /**
     * <strong>Index</strong>
     * <ul>
     * <li>[0] Tipos de Relatórios</li>
     * <li>[1] List[SelectItem] Convenção Período</li>
     * </ul>
     *
     * @return Integer
     */
    public Integer[] getIndex() {
        return index;
    }

    public void setIndex(Integer[] index) {
        this.index = index;
    }

    /**
     * <strong>Filtros</strong>
     * <ul>
     * <li>[0] MODALIDADE</li>
     * <li>[1] PERÍODO EMISSÃO / INATIVAÇÃO</li>
     * <li>[2] RESPONSÁVEL</li>
     * <li>[3] ALUNO</li>
     * <li>[4] SEXO</li>
     * <li>[5] ORDENAÇÃO </li>
     * <li>[6] PERIODOS </li>
     * <li>[7] PERIODO INATIVAÇÃO </li>
     * <li>[8] IDADE </li>
     * <li>[9] GRUPO / CATEGORIA </li>
     * <li>[10] NÃO SÓCIO </li>
     * <li>[11] CONVÊNIO EMPRESA </li>
     * <li>[12] FAIXA DESCONTO </li>
     * <li>[13] SITUAÇÃO </li>
     * <li>[14] SITUAÇÃO FINANCEIRA</li>
     * <li>[15] FAIXA VENCIMENTO</li>
     * <li>[16] FAIXA QUITAÇÃO</li>
     * </ul>
     *
     * @return boolean
     */
    public Boolean[] getFiltro() {
        if ((filtro[10] != null && filtro[10]) || filtro[11]) {
            listGrupoCategoria = null;
            listCategoria = null;
            selectedGrupoCategoria = null;
            selectedCategoria = null;
            selectedConvenioEmpresa = null;
        }
        return filtro;
    }

    public void setFiltro(Boolean[] filtro) {
        this.filtro = filtro;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Pessoa getAluno() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            aluno = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return aluno;
    }

    public void setAluno(Pessoa aluno) {
        this.aluno = aluno;
    }

    public Pessoa getResponsavel() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            responsavel = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
        }
        return responsavel;
    }

    public void setResponsavel(Pessoa aluno) {
        this.aluno = aluno;
    }

    public Map<String, Integer> getListModalidades() {
        if (listModalidades == null) {
            listModalidades = new HashMap<>();
            AcademiaDao academiaDao = new AcademiaDao();
            List<AcademiaServicoValor> list = academiaDao.listaServicoValorPorRotina();
            int idServicoMemoria = 0;
            int b = 0;
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (idServicoMemoria != list.get(i).getServicos().getId()) {
                        listModalidades.put(list.get(i).getServicos().getDescricao(), list.get(i).getServicos().getId());
                        idServicoMemoria = list.get(i).getServicos().getId();
                        b++;
                    }
                }
            }
        }
        return listModalidades;
    }

    public void setListModalidades(Map<String, Integer> listModalidades) {
        this.listModalidades = listModalidades;
    }

    public Map<String, Integer> getListPeriodos() {
        if (listPeriodos == null) {
            listPeriodos = new HashMap<>();
            Dao dao = new Dao();
            List<Periodo> list = dao.list(new Periodo());
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    listPeriodos.put(list.get(i).getDescricao(), list.get(i).getId());
                }
            }
        }
        return listPeriodos;
    }

    public void setListPeriodos(Map<String, Integer> listPeriodos) {
        this.listPeriodos = listPeriodos;
    }

    public String inIdModalidades() {
        String ids = null;
        if (selectedModalidades != null) {
            for (int i = 0; i < selectedModalidades.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedModalidades.get(i);
                } else {
                    ids += "," + selectedModalidades.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdPeriodos() {
        String ids = null;
        if (selectedPeriodos != null) {
            for (int i = 0; i < selectedPeriodos.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedPeriodos.get(i);
                } else {
                    ids += "," + selectedPeriodos.get(i);
                }
            }
        }
        return ids;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List getSelectedModalidades() {
        return selectedModalidades;
    }

    public void setSelectedModalidades(List selectedModalidades) {
        this.selectedModalidades = selectedModalidades;
    }

    public List getSelectedPeriodos() {
        return selectedPeriodos;
    }

    public void setSelectedPeriodos(List selectedPeriodos) {
        this.selectedPeriodos = selectedPeriodos;
    }

    public Relatorios getRelatorios() {
        try {
            if (!Objects.equals(relatorios.getId(), index[0])) {
                Jasper.EXPORT_TO = false;
            }
            relatorios = (Relatorios) new Dao().find(new Relatorios(), index[0]);
        } catch (Exception e) {
            relatorios = new Relatorios();
            Jasper.EXPORT_TO = false;
        }
        return relatorios;
    }

    public Integer[] getIdade() {
        return idade;
    }

    public void setIdade(Integer[] idade) {
        this.idade = idade;
    }

    public String getIdadeInicial() {
        return Integer.toString(idade[0]);
    }

    public void setIdadeInicial(String idadeInicial) {
        Integer idadeInteger = 0;
        try {
            if (Integer.parseInt(idadeInicial) < 0) {
                idadeInteger = 0;
            }
            if (idade[1] < 0 && idade[1] <= Integer.parseInt(idadeInicial)) {
                idadeInteger = Integer.parseInt(idadeInicial) + 1;
            } else {
                idadeInteger = Integer.parseInt(idadeInicial);
            }
        } catch (NumberFormatException e) {
            idadeInteger = 0;
        }
        idade[0] = idadeInteger;
        if (idade[0] > 0) {
            if (idade[1] == 0) {
            } else if (idade[1] < idade[0]) {
                idade[1] = idade[0];
            }

        }
    }

    public String getIdadeFinal() {
        return Integer.toString(idade[1]);
    }

    public void setIdadeFinal(String idadeFinal) {
        if (idadeFinal.isEmpty()) {
            idade[1] = 0;
            return;
        }
        Integer idadeInteger = 0;
        try {
            if (idade[0] < 0) {
                idade[0] = 0;
            }
            if (Integer.parseInt(idadeFinal) < 0 && Integer.parseInt(idadeFinal) < idade[0]) {
                idadeInteger = idade[0] + 1;
            } else {
                idadeInteger = Integer.parseInt(idadeFinal);
            }
        } catch (NumberFormatException e) {
            idadeInteger = idade[0];
        }
        idade[1] = idadeInteger;
        if (idade[1] > 150) {
            idadeInteger = 150;
        }
        if (idade[1] < idade[0] && idade[1] != 0) {
            idade[1] = idade[0];
        }
    }

    public List getSelectedGrupoCategoria() {
        return selectedGrupoCategoria;
    }

    public void setSelectedGrupoCategoria(List selectedGrupoCategoria) {
        this.selectedGrupoCategoria = selectedGrupoCategoria;
    }

    public List getSelectedCategoria() {
        return selectedCategoria;
    }

    public void setSelectedCategoria(List selectedCategoria) {
        this.selectedCategoria = selectedCategoria;
    }

    public List getSelectedConvenioEmpresa() {
        return selectedConvenioEmpresa;
    }

    public void setSelectedConvenioEmpresa(List selectedConvenioEmpresa) {
        this.selectedConvenioEmpresa = selectedConvenioEmpresa;
    }

    public Map<String, Integer> getListCategoria() {
        if (listCategoria == null) {
            selectedCategoria = null;
            listCategoria = new LinkedHashMap<>();
            String ids = inIdGrupoCategoria();
            List<Categoria> list = (List<Categoria>) new CategoriaDao().findCategoriaByGrupoCategoria(ids);
            for (int i = 0; i < list.size(); i++) {
                listCategoria.put(list.get(i).getCategoria(), list.get(i).getId());
            }
        }
        return listCategoria;
    }

    public void setListCategoria(Map<String, Integer> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public Map<String, Integer> getListGrupoCategoria() {
        if (listGrupoCategoria == null) {
            listGrupoCategoria = new LinkedHashMap<>();
            List<GrupoCategoria> list = new Dao().list(new GrupoCategoria(), true);
            for (int i = 0; i < list.size(); i++) {
                listGrupoCategoria.put(list.get(i).getGrupoCategoria(), list.get(i).getId());
            }
        }
        return listGrupoCategoria;
    }

    public void setListGrupoCategoria(Map<String, Integer> listGrupoCategoria) {
        this.listGrupoCategoria = listGrupoCategoria;
    }

    public String inIdCategoria() {
        String ids = "";
        if (selectedCategoria != null) {
            for (int i = 0; i < selectedCategoria.size(); i++) {
                if (ids.isEmpty()) {
                    ids = "" + selectedCategoria.get(i);
                } else {
                    ids += "," + selectedCategoria.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdGrupoCategoria() {
        String ids = "";
        if (selectedGrupoCategoria != null) {
            for (int i = 0; i < selectedGrupoCategoria.size(); i++) {
                if (ids.isEmpty()) {
                    ids = "" + selectedGrupoCategoria.get(i);
                } else {
                    ids += "," + selectedGrupoCategoria.get(i);
                }
            }
        }
        return ids;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }

    public Float getDescontoFinal() {
        return descontoFinal;
    }

    public void setDescontoFinal(Float descontoFinal) {
        this.descontoFinal = descontoFinal;
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

    public String getSituacaoString() {
        return situacaoString;
    }

    public void setSituacaoString(String situacaoString) {
        this.situacaoString = situacaoString;
    }

    public String getMatricula_situacao() {
        return matricula_situacao;
    }

    public void setMatricula_situacao(String matricula_situacao) {
        this.matricula_situacao = matricula_situacao;
    }

    public Boolean getMostrarDataInativacao() {
        return mostrarDataInativacao;
    }

    public void setMostrarDataInativacao(Boolean mostrarDataInativacao) {
        this.mostrarDataInativacao = mostrarDataInativacao;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public String getSituacaoFinanceira() {
        return situacaoFinanceira;
    }

    public void setSituacaoFinanceira(String situacaoFinanceira) {
        this.situacaoFinanceira = situacaoFinanceira;
    }

    public String getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(String vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public String getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(String vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public String getQuitacaoInicial() {
        return quitacaoInicial;
    }

    public void setQuitacaoInicial(String quitacaoInicial) {
        this.quitacaoInicial = quitacaoInicial;
    }

    public String getQuitacaoFinal() {
        return quitacaoFinal;
    }

    public void setQuitacaoFinal(String quitacaoFinal) {
        this.quitacaoFinal = quitacaoFinal;
    }

    public class ParametroAcademiaCadastral {

        private Object aluno_nome;
        private Object aluno_idade;
        private Object aluno_nascimento;
        private Object aluno_sexo;
        private Object aluno_cidade;
        private Object servico;
        private Object periodo;
        private Object responsavel_nome;
        private Object emissao;
        private Object inativacao;
        private Object data_vencimento;
        private Object data_baixa;
        private Object valor;
        private Object valor_baixa;

        public ParametroAcademiaCadastral() {
            this.aluno_nome = null;
            this.aluno_idade = null;
            this.aluno_nascimento = null;
            this.aluno_sexo = null;
            this.aluno_cidade = null;
            this.servico = null;
            this.periodo = null;
            this.responsavel_nome = null;
            this.emissao = null;
            this.inativacao = null;
            this.data_vencimento = null;
            this.data_baixa = null;
            this.valor = null;
            this.valor_baixa = null;
        }

        /**
         *
         * @param aluno_nome
         * @param aluno_idade
         * @param aluno_nascimento
         * @param aluno_sexo
         * @param aluno_cidade
         * @param servico
         * @param periodo
         * @param responsavel_nome
         * @param emissao
         * @param inativacao
         */
        public ParametroAcademiaCadastral(Object aluno_nome, Object aluno_idade, Object aluno_nascimento, Object aluno_sexo, Object aluno_cidade, Object servico, Object periodo, Object responsavel_nome, Object emissao, Object inativacao) {
            this.aluno_nome = aluno_nome;
            this.aluno_idade = aluno_idade;
            this.aluno_nascimento = aluno_nascimento;
            this.aluno_sexo = aluno_sexo;
            this.aluno_cidade = aluno_cidade;
            this.servico = servico;
            this.periodo = periodo;
            this.responsavel_nome = responsavel_nome;
            this.emissao = emissao;
            this.inativacao = inativacao;
        }

        /**
         *
         * @param aluno_nome
         * @param aluno_idade
         * @param aluno_nascimento
         * @param aluno_sexo
         * @param aluno_cidade
         * @param servico
         * @param periodo
         * @param responsavel_nome
         * @param emissao
         * @param inativacao
         * @param data_vencimento
         * @param data_baixa
         * @param valor
         * @param valor_baixa
         */
        public ParametroAcademiaCadastral(Object aluno_nome, Object aluno_idade, Object aluno_nascimento, Object aluno_sexo, Object aluno_cidade, Object servico, Object periodo, Object responsavel_nome, Object emissao, Object inativacao, Object data_vencimento, Object data_baixa, Object valor, Object valor_baixa) {
            this.aluno_nome = aluno_nome;
            this.aluno_idade = aluno_idade;
            this.aluno_nascimento = aluno_nascimento;
            this.aluno_sexo = aluno_sexo;
            this.aluno_cidade = aluno_cidade;
            this.servico = servico;
            this.periodo = periodo;
            this.responsavel_nome = responsavel_nome;
            this.emissao = emissao;
            this.inativacao = inativacao;
            this.data_vencimento = data_vencimento;
            this.data_baixa = data_baixa;
            this.valor = valor;
            this.valor_baixa = valor_baixa;
        }

        /**
         * Nome
         *
         * @see aaaa
         * @return
         */
        // ALUNO
        public Object getAluno_nome() {
            return aluno_nome;
        }

        // ALUNO
        public void setAluno_nome(Object aluno_nome) {
            this.aluno_nome = aluno_nome;
        }

        public Object getAluno_idade() {
            return aluno_idade;
        }

        public void setAluno_idade(Object aluno_idade) {
            this.aluno_idade = aluno_idade;
        }

        public Object getAluno_nascimento() {
            return aluno_nascimento;
        }

        public void setAluno_nascimento(Object aluno_nascimento) {
            this.aluno_nascimento = aluno_nascimento;
        }

        public Object getAluno_sexo() {
            return aluno_sexo;
        }

        public void setAluno_sexo(Object aluno_sexo) {
            this.aluno_sexo = aluno_sexo;
        }

        public Object getAluno_cidade() {
            return aluno_cidade;
        }

        public void setAluno_cidade(Object aluno_cidade) {
            this.aluno_cidade = aluno_cidade;
        }

        public Object getServico() {
            return servico;
        }

        public void setServico(Object servico) {
            this.servico = servico;
        }

        public Object getPeriodo() {
            return periodo;
        }

        public void Object(Object periodo) {
            this.periodo = periodo;
        }

        public Object getResponsavel_nome() {
            return responsavel_nome;
        }

        public void setResponsavel_nome(Object responsavel_nome) {
            this.responsavel_nome = responsavel_nome;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

        public Object getInativacao() {
            return inativacao;
        }

        public void setInativacao(Object inativacao) {
            this.inativacao = inativacao;
        }

        public Object getData_vencimento() {
            return data_vencimento;
        }

        public void setData_vencimento(Object data_vencimento) {
            this.data_vencimento = data_vencimento;
        }

        public Object getData_baixa() {
            return data_baixa;
        }

        public void setData_baixa(Object data_baixa) {
            this.data_baixa = data_baixa;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getValor_baixa() {
            return valor_baixa;
        }

        public void setValor_baixa(Object valor_baixa) {
            this.valor_baixa = valor_baixa;
        }
    }
}
