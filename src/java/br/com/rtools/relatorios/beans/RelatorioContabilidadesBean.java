package br.com.rtools.relatorios.beans;

import br.com.rtools.endereco.Cidade;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioContabilidadesDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioContabilidadesBean implements Serializable {

    private List<Filters> listFilters;
    /**
     * 0 - Cidades; 1 - Tipo Relatórios; 2 - Tipo Endereço; 3 - Quantidade
     * Inicio 4 - Quantidade Fim;
     */
    private Integer idRelatorio;
    private Integer idRelatorioOrdem;
    private Integer idTipoEndereco;
    private Integer nrQtdeInicio;
    private Integer nrQtdeFim;

    private List<SelectItem> listRelatorios;
    private List<SelectItem> listTipoEndereco;
    private List<SelectItem> listQuantidadeInicio;
    private List<SelectItem> listQuantidadeFim;
    private List<SelectItem> listRelatorioOrdem;
    private Integer quantidadeEmpresas;
    private String radioEmpresas;
    private String radioCidades;
    private String radioOrdem;
    private boolean ocultaEmpresas;
    private boolean ocultaCidades;
    private Relatorios relatorios;
    private String radioEmail;
    private Map<String, Integer> listCidade;
    private List selectedCidade;

    @PostConstruct
    public void init() {
        nrQtdeInicio = 0;
        nrQtdeFim = 0;
        idRelatorio = null;
        idRelatorioOrdem = null;
        idTipoEndereco = null;
        listRelatorios = new ArrayList<>();
        listTipoEndereco = new ArrayList<>();
        listQuantidadeInicio = new ArrayList<>();
        listQuantidadeFim = new ArrayList<>();
        listRelatorioOrdem = new ArrayList<>();
        quantidadeEmpresas = 0;
        radioEmpresas = "todas";
        radioCidades = "todas";
        radioOrdem = "razao";
        ocultaEmpresas = false;
        ocultaCidades = false;
        relatorios = null;
        radioEmail = "";
        Jasper jasper = new Jasper();
        jasper.init();
        loadFilters();
        loadRelatorio();
        loadRelatorioOrdem();
        loadListTipoEndereco();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioContabilidadesBean");
        GenericaSessao.remove("jasperBean");
    }

    public void print() {
        String cidades = "";
        int inicio = 0;
        int fim = 0;

        RelatorioDao db = new RelatorioDao();
        RelatorioContabilidadesDao dbConta = new RelatorioContabilidadesDao();
        PessoaEnderecoDao dbPesEnd = new PessoaEnderecoDao();
        Cidade cidade;
        Dao dao = new Dao();
        Relatorios r = db.pesquisaRelatorios(idRelatorio);
        if (!listRelatorioOrdem.isEmpty()) {
            relatorios.setQryOrdem(((RelatorioOrdem) dao.find(new RelatorioOrdem(), idRelatorioOrdem)).getQuery());
        }
        TipoEndereco tipoEndereco = (TipoEndereco) dao.find(new TipoEndereco(), idTipoEndereco);

        // CONTABILIDADES DO RELATORIO -----------------------------------------------------------
        if (listFilters.get(0).getActive()) {
            if (radioEmpresas.equals("comEmpresas")) {
                inicio = nrQtdeInicio;
                fim = nrQtdeFim;
                if (inicio > fim) {
                    inicio = fim;
                }
            }
        }

        // CIDADE DO RELATORIO -----------------------------------------------------------
        if (listFilters.get(1).getActive()) {
            switch (radioCidades) {
                case "especificas":
                    cidades = inIdCidade();
                    break;
                case "local":
                    cidade = dbPesEnd.pesquisaEndPorPessoaTipo(1, 2).getEndereco().getCidade();
                    cidades = Integer.toString(cidade.getId());
                    break;
                case "outras":
                    cidade = dbPesEnd.pesquisaEndPorPessoaTipo(1, 2).getEndereco().getCidade();
                    cidades = Integer.toString(cidade.getId());
                    break;
                default:
                    break;
            }
        }
        String emails = "";
        if (listFilters.get(2).getActive()) {
            emails = radioEmail;
        }
        List list = dbConta.listaRelatorioContabilidades(radioEmpresas, inicio, fim, radioCidades, cidades, radioOrdem, tipoEndereco.getId(), emails);
        if (list.isEmpty()) {
            GenericaMensagem.info("Sistema", "Não existem registros para o relatório selecionado");
            return;
        }
        try {
            Collection listaEscritorios = new ArrayList<>();
            try {
                String dados[] = new String[8];
                for (int i = 0; i < list.size(); i++) {
                    int quantidade = 0;
                    Juridica juridica = (Juridica) dao.find(new Juridica(), Integer.parseInt(((List) list.get(i)).get(0).toString()));
                    PessoaEndereco pessoaEndereco = (PessoaEndereco) dao.find(new PessoaEndereco(), Integer.parseInt(((List) list.get(i)).get(1).toString()));
                    quantidade = Integer.parseInt(((List) list.get(i)).get(2).toString());
                    try {
                        dados[0] = pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[1] = pessoaEndereco.getEndereco().getLogradouro().getDescricao();
                        dados[2] = pessoaEndereco.getNumero();
                        dados[3] = pessoaEndereco.getComplemento();
                        dados[4] = pessoaEndereco.getEndereco().getBairro().getDescricao();
                        dados[5] = pessoaEndereco.getEndereco().getCep();
                        dados[6] = pessoaEndereco.getEndereco().getCidade().getCidade();
                        dados[7] = pessoaEndereco.getEndereco().getCidade().getUf();
                    } catch (Exception e) {
                        dados[0] = "";
                        dados[1] = "";
                        dados[2] = "";
                        dados[3] = "";
                        dados[4] = "";
                        dados[5] = "";
                        dados[6] = "";
                        dados[7] = "";
                    }
                    listaEscritorios.add(new ParametroEscritorios(
                            juridica.getId(), // ESCRITÓRIO - ID
                            juridica.getPessoa().getNome(), // ESCRITÓRIO - NOME
                            dados[0], // ESCRITÓRIO - DESCRICAO ENDERECO
                            dados[1], // ESCRITÓRIO - LOGRADOURO
                            dados[2], // ESCRITÓRIO - NUMERO
                            dados[3], // ESCRITÓRIO - COMPLEMENTO
                            dados[4], // ESCRITÓRIO - BAIRRO
                            dados[5], // ESCRITÓRIO - CEP
                            dados[6], // ESCRITÓRIO - CIDADE
                            dados[7], // ESCRITÓRIO - UF
                            juridica.getPessoa().getTelefone1(),
                            juridica.getPessoa().getEmail1(),
                            quantidade));
                }
                Jasper.TYPE = "paisagem";
                if (r.getExcel()) {
                    Jasper.EXCEL_FIELDS = r.getCamposExcel();
                } else {
                    Jasper.EXCEL_FIELDS = "";
                }
                Jasper.printReports(relatorios.getJasper(), "escritorios", listaEscritorios);
            } catch (Exception erro) {
                GenericaMensagem.info("Sistema", "O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
                System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
            }
        } catch (Exception erro) {
            GenericaMensagem.info("Sistema", "O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("quantidade_empresas", "Quantidade de Empresas", false, false));
        listFilters.add(new Filters("cidade", "Cidade", false, false));
        listFilters.add(new Filters("email", "Email", false, false));
    }

    // LOAD
    public void loadRelatorio() {
        listRelatorios = new ArrayList();
        if (listRelatorios.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
            }
            if (!list.isEmpty()) {
                idRelatorio = list.get(0).getId();
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    idRelatorio = list.get(i).getId();
                }
                listRelatorios.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
            loadRelatorioOrdem();
        }
    }

    public void loadRelatorioOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadListTipoEndereco() {
        listTipoEndereco = new ArrayList();
        List<TipoEndereco> list = (List<TipoEndereco>) new Dao().find("TipoEndereco", new int[]{2, 3, 4, 5});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idTipoEndereco = list.get(i).getId();
            }
            listTipoEndereco.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListCidade() {
        listCidade = new LinkedHashMap<>();
        selectedCidade = new ArrayList<>();
        if (radioCidades.equals("especificas")) {
            List<Cidade> list = (List<Cidade>) new RelatorioDao().pesquisaCidadesRelatorio();
            for (int i = 0; i < list.size(); i++) {
                listCidade.put(list.get(i).getCidade(), list.get(i).getId());
            }
        }
    }

    public void loadListQuantidadeInicio() {
        loadQuantidadeEmpresas();
        listQuantidadeInicio = new ArrayList();
        if (listQuantidadeInicio.isEmpty()) {
            for (int i = 0; i < quantidadeEmpresas; i++) {
                boolean itemSelecionado = false;
                if (i == 0) {
                    itemSelecionado = true;
                }
                listQuantidadeInicio.add(new SelectItem(i + 1, Integer.toString(i + 1), Integer.toString(i + 1), false, false, itemSelecionado));
            }
        }
    }

    public void loadListQuantidadeFim() {
        loadQuantidadeEmpresas();
        listQuantidadeFim = new ArrayList();
        if (listQuantidadeFim.isEmpty()) {
            for (int i = 0; i < quantidadeEmpresas; i++) {
                boolean itemSelecionado = false;
                if (i + 1 == quantidadeEmpresas) {
                    nrQtdeFim = i;
                }
                if (i + 1 >= nrQtdeInicio) {
                    listQuantidadeFim.add(new SelectItem(i + 1, Integer.toString(i + 1), Integer.toString(i + 1), false, false, itemSelecionado));
                }
            }
        }
    }

    public void loadQuantidadeEmpresas() {
        if (quantidadeEmpresas <= 0) {
            RelatorioContabilidadesDao db = new RelatorioContabilidadesDao();
            quantidadeEmpresas = db.quantidadeEmpresas();
        }
    }

    /**
     * 0 - Cidades; 1 - Tipo Relatórios; 2 - Tipo Endereço; 3 - Quantidade
     * Inicio; 4 - Quantidade Fim; 5 -Relatório Ordem
     *
     * @param tcase
     * @return
     */
//    public Integer[] getIndex() {
//        return index;
//    }
//
//    public void setIndex(Integer[] index) {
//        if (index[4] < index[3]) {
//            index[3] = index[4];
//        }
//        this.index = index;
//    }
    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                break;
            case 2:
                loadListCidade();
                break;
            case 3:
                loadListQuantidadeFim();
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "quantidade_empresas":
                radioEmpresas = "todas";
                nrQtdeInicio = 0;
                nrQtdeFim = 0;
                listQuantidadeInicio = new ArrayList();
                listQuantidadeFim = new ArrayList();
                if (filter.getActive()) {
                    loadListQuantidadeInicio();
                    loadListQuantidadeFim();
                }
                break;
            case "cidade":
                listCidade = new LinkedHashMap<>();
                selectedCidade = new ArrayList<>();
                if (filter.getActive()) {
                    loadListCidade();
                }
                break;
            case "email":
                radioEmail = "";
                break;
        }
    }

    // TRATAMENTO
    public String inIdCidade() {
        String ids = null;
        if (selectedCidade != null) {
            for (int i = 0; i < selectedCidade.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedCidade.get(i);
                } else {
                    ids += "," + selectedCidade.get(i);
                }
            }
        }
        return ids;
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public Relatorios getRelatorios() {
        try {
            if (relatorios != null && !Objects.equals(relatorios.getId(), idRelatorio)) {
                Jasper.EXPORT_TO = false;
            }
            relatorios = (Relatorios) new Dao().find(new Relatorios(), idRelatorio);
        } catch (Exception e) {
            relatorios = new Relatorios();
            Jasper.EXPORT_TO = false;
        }
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
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

    public Integer getIdTipoEndereco() {
        return idTipoEndereco;
    }

    public void setIdTipoEndereco(Integer idTipoEndereco) {
        this.idTipoEndereco = idTipoEndereco;
    }

    public Integer getNrQtdeInicio() {
        return nrQtdeInicio;
    }

    public void setNrQtdeInicio(Integer nrQtdeInicio) {
        this.nrQtdeInicio = nrQtdeInicio;
    }

    public Integer getNrQtdeFim() {
        return nrQtdeFim;
    }

    public void setNrQtdeFim(Integer nrQtdeFim) {
        this.nrQtdeFim = nrQtdeFim;
    }

    public List<SelectItem> getListRelatorios() {
        return listRelatorios;
    }

    public void setListRelatorios(List<SelectItem> listRelatorios) {
        this.listRelatorios = listRelatorios;
    }

    public List<SelectItem> getListTipoEndereco() {
        return listTipoEndereco;
    }

    public void setListTipoEndereco(List<SelectItem> listTipoEndereco) {
        this.listTipoEndereco = listTipoEndereco;
    }

    public List<SelectItem> getListQuantidadeInicio() {
        return listQuantidadeInicio;
    }

    public void setListQuantidadeInicio(List<SelectItem> listQuantidadeInicio) {
        this.listQuantidadeInicio = listQuantidadeInicio;
    }

    public List<SelectItem> getListQuantidadeFim() {
        return listQuantidadeFim;
    }

    public void setListQuantidadeFim(List<SelectItem> listQuantidadeFim) {
        this.listQuantidadeFim = listQuantidadeFim;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public Integer getQuantidadeEmpresas() {
        return quantidadeEmpresas;
    }

    public void setQuantidadeEmpresas(Integer quantidadeEmpresas) {
        this.quantidadeEmpresas = quantidadeEmpresas;
    }

    public String getRadioEmpresas() {
        return radioEmpresas;
    }

    public void setRadioEmpresas(String radioEmpresas) {
        this.radioEmpresas = radioEmpresas;
    }

    public String getRadioCidades() {
        return radioCidades;
    }

    public void setRadioCidades(String radioCidades) {
        this.radioCidades = radioCidades;
    }

    public String getRadioOrdem() {
        return radioOrdem;
    }

    public void setRadioOrdem(String radioOrdem) {
        this.radioOrdem = radioOrdem;
    }

    public boolean isOcultaEmpresas() {
        ocultaEmpresas = radioEmpresas.equals("comEmpresas");
        return ocultaEmpresas;
    }

    public void setOcultaEmpresas(boolean ocultaEmpresas) {
        this.ocultaEmpresas = ocultaEmpresas;
    }

    public boolean isOcultaCidades() {
        ocultaCidades = radioCidades.equals("especificas");
        return ocultaCidades;
    }

    public void setOcultaCidades(boolean ocultaCidades) {
        this.ocultaCidades = ocultaCidades;
    }

    public String getRadioEmail() {
        return radioEmail;
    }

    public void setRadioEmail(String radioEmail) {
        this.radioEmail = radioEmail;
    }

    public Map<String, Integer> getListCidade() {
        return listCidade;
    }

    public void setListCidade(Map<String, Integer> listCidade) {
        this.listCidade = listCidade;
    }

    public List getSelectedCidade() {
        return selectedCidade;
    }

    public void setSelectedCidade(List selectedCidade) {
        this.selectedCidade = selectedCidade;
    }

    public class ParametroEscritorios {

        private Integer escritorio_id;
        private String escritorio_nome;
        private String escritorio_endereco;
        private String escritorio_logradouro;
        private String escritorio_numero;
        private String escritorio_complemento;
        private String escritorio_bairro;
        private String escritorio_cep;
        private String escritorio_cidade;
        private String escritorio_uf;
        private String escritorio_telefone;
        private String escritorio_email;
        private Integer escritorio_quantidade_empresas;

        public ParametroEscritorios(Integer escritorio_id, String escritorio_nome, String escritorio_endereco, String escritorio_logradouro, String escritorio_numero, String escritorio_complemento, String escritorio_bairro, String escritorio_cep, String escritorio_cidade, String escritorio_uf, String escritorio_telefone, String escritorio_email, Integer escritorio_quantidade_empresas) {
            this.escritorio_id = escritorio_id;
            this.escritorio_nome = escritorio_nome;
            this.escritorio_endereco = escritorio_endereco;
            this.escritorio_logradouro = escritorio_logradouro;
            this.escritorio_numero = escritorio_numero;
            this.escritorio_complemento = escritorio_complemento;
            this.escritorio_bairro = escritorio_bairro;
            this.escritorio_cep = escritorio_cep;
            this.escritorio_cidade = escritorio_cidade;
            this.escritorio_uf = escritorio_uf;
            this.escritorio_telefone = escritorio_telefone;
            this.escritorio_email = escritorio_email;
            this.escritorio_quantidade_empresas = escritorio_quantidade_empresas;
        }

        public Integer getEscritorio_id() {
            return escritorio_id;
        }

        public void setEscritorio_id(Integer escritorio_id) {
            this.escritorio_id = escritorio_id;
        }

        public String getEscritorio_nome() {
            return escritorio_nome;
        }

        public void setEscritorio_nome(String escritorio_nome) {
            this.escritorio_nome = escritorio_nome;
        }

        public String getEscritorio_endereco() {
            return escritorio_endereco;
        }

        public void setEscritorio_endereco(String escritorio_endereco) {
            this.escritorio_endereco = escritorio_endereco;
        }

        public String getEscritorio_logradouro() {
            return escritorio_logradouro;
        }

        public void setEscritorio_logradouro(String escritorio_logradouro) {
            this.escritorio_logradouro = escritorio_logradouro;
        }

        public String getEscritorio_numero() {
            return escritorio_numero;
        }

        public void setEscritorio_numero(String escritorio_numero) {
            this.escritorio_numero = escritorio_numero;
        }

        public String getEscritorio_complemento() {
            return escritorio_complemento;
        }

        public void setEscritorio_complemento(String escritorio_complemento) {
            this.escritorio_complemento = escritorio_complemento;
        }

        public String getEscritorio_bairro() {
            return escritorio_bairro;
        }

        public void setEscritorio_bairro(String escritorio_bairro) {
            this.escritorio_bairro = escritorio_bairro;
        }

        public String getEscritorio_cep() {
            return escritorio_cep;
        }

        public void setEscritorio_cep(String escritorio_cep) {
            this.escritorio_cep = escritorio_cep;
        }

        public String getEscritorio_cidade() {
            return escritorio_cidade;
        }

        public void setEscritorio_cidade(String escritorio_cidade) {
            this.escritorio_cidade = escritorio_cidade;
        }

        public String getEscritorio_uf() {
            return escritorio_uf;
        }

        public void setEscritorio_uf(String escritorio_uf) {
            this.escritorio_uf = escritorio_uf;
        }

        public String getEscritorio_telefone() {
            return escritorio_telefone;
        }

        public void setEscritorio_telefone(String escritorio_telefone) {
            this.escritorio_telefone = escritorio_telefone;
        }

        public String getEscritorio_email() {
            return escritorio_email;
        }

        public void setEscritorio_email(String escritorio_email) {
            this.escritorio_email = escritorio_email;
        }

        public Integer getEscritorio_quantidade_empresas() {
            return escritorio_quantidade_empresas;
        }

        public void setEscritorio_quantidade_empresas(Integer escritorio_quantidade_empresas) {
            this.escritorio_quantidade_empresas = escritorio_quantidade_empresas;
        }

    }
}
