package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Cupom;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.dao.CupomDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.dao.CidadeDao;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCupomDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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

@ManagedBean
@SessionScoped
public class RelatorioCupomBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private Map<String, Integer> listOperador;
    private List selectedOperador;

    private Map<String, Integer> listParentesco;
    private List selectedParentesco;

    private Map<String, Integer> listCidade;
    private List selectedCidade;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List<SelectItem> listCupom;
    private Integer idCupom;

    private String sexo;

    private String dataEmissaoInicial;
    private String dataEmissaoFinal;

    private String tipoDataEmissao;

    private String idadeInicial;
    private String idadeFinal;

    @PostConstruct
    public void init() {
        Jasper.load();
        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        idRelatorio = null;

        dataEmissaoInicial = "";
        dataEmissaoFinal = "";

        idadeInicial = "";
        idadeFinal = "";

        tipoDataEmissao = "";

        sexo = "";

        loadFilters();
        loadRelatorio();
        loadRelatorioOrdem();
        loadListCupom();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCobrancaBean");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();
        if (r == null) {
            return;
        }
        String order = "";
        Integer titular_id = null;
        Map map = new HashMap();
        FacesContext faces = FacesContext.getCurrentInstance();
        List<ObjectJasper> cs = new ArrayList<>();
        List<Etiquetas> e = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioCupomDao rcd = new RelatorioCupomDao();
        if (!listRelatorioOrdem.isEmpty()) {
            if (idRelatorioOrdem != null) {
                rcd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));
            }
        }
        rcd.setRelatorios(r);
        List list = rcd.find(idCupom, tipoDataEmissao, dataEmissaoInicial, dataEmissaoFinal, idadeInicial, idadeFinal, sexo, inIdOperador(), inIdParentesco(), inIdCidade());
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            cs.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8)));
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Jasper.EXPORT_TO = true;
        Jasper.TYPE = "default";
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) cs, map);
        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
    }

    // LOAD
    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
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
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadListCupom() {
        listCupom = new ArrayList();
        List<Cupom> list = new CupomDao().findAll();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCupom = list.get(i).getId();
            }
            listCupom.add(new SelectItem(list.get(i).getId(), "Data: " + list.get(i).getData() + " - " + list.get(i).getDescricao()));
        }
    }

    public void load() {
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("data_emissao", "Data Emissão", false, false));
        listFilters.add(new Filters("sexo", "Sexo", false, false));
        listFilters.add(new Filters("parentesco", "Parentesco", false, false));
        listFilters.add(new Filters("idade", "Idade", false, false));
        listFilters.add(new Filters("operador", "Operador", false, false));
        listFilters.add(new Filters("cidade_socio", "Cidade da Empresa do Sócio", false, false));
    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                break;
            case 2:
                break;
        }
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "data_emissao":
                dataEmissaoInicial = "";
                dataEmissaoFinal = "";
                break;
            case "sexo":
                sexo = "";
                break;
            case "parentesco":
                if (filter.getActive()) {
                    loadListParentesco();
                } else {
                    listParentesco = new LinkedHashMap<>();
                    selectedParentesco = new ArrayList<>();
                }
                break;
            case "idade":
                idadeInicial = "";
                idadeFinal = "";
                break;
            case "operador":
                if (filter.getActive()) {
                    loadListOperador();
                } else {
                    listOperador = new LinkedHashMap<>();
                    selectedOperador = new ArrayList<>();
                }
                break;
            case "cidade_socio":
                if (filter.getActive()) {
                    loadListCidade();
                } else {
                    listCidade = new LinkedHashMap<>();
                    selectedCidade = new ArrayList<>();
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
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

    public void loadListParentesco() {
        listParentesco = new LinkedHashMap<>();
        selectedParentesco = new ArrayList<>();
        List<Parentesco> list = new Dao().list(new Parentesco(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listParentesco.put(list.get(i).getParentesco(), list.get(i).getId());
            }
        }
    }

    public void loadListOperador() {
        listOperador = new LinkedHashMap<>();
        selectedOperador = new ArrayList<>();
        List<Usuario> list = new Dao().list(new Usuario(), true);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listOperador.put(list.get(i).getPessoa().getNome(), list.get(i).getId());
            }
        }
    }

    public void loadListCidade() {
        listCidade = new LinkedHashMap<>();
        selectedCidade = new ArrayList<>();
        CidadeDao cidadeDao = new CidadeDao();
        List<Cidade> list = cidadeDao.pesquisaCidadeObj(Registro.get().getFilial().getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf());
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listCidade.put(list.get(i).getCidade(), list.get(i).getId());
            }
        }
    }

    // TRATAMENTO
    public String inIdParentesco() {
        String ids = null;
        if (selectedParentesco != null) {
            for (int i = 0; i < selectedParentesco.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedParentesco.get(i);
                } else {
                    ids += "," + selectedParentesco.get(i);
                }
            }
        }
        return ids;
    }

    public String inIdOperador() {
        String ids = null;
        if (selectedOperador != null) {
            for (int i = 0; i < selectedOperador.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedOperador.get(i);
                } else {
                    ids += "," + selectedOperador.get(i);
                }
            }
        }
        return ids;
    }

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

    public Map<String, Integer> getListOperador() {
        return listOperador;
    }

    public void setListOperador(Map<String, Integer> listOperador) {
        this.listOperador = listOperador;
    }

    public List getSelectedOperador() {
        return selectedOperador;
    }

    public void setSelectedOperador(List selectedOperador) {
        this.selectedOperador = selectedOperador;
    }

    public List<SelectItem> getListCupom() {
        return listCupom;
    }

    public void setListCupom(List<SelectItem> listCupom) {
        this.listCupom = listCupom;
    }

    public Integer getIdCupom() {
        return idCupom;
    }

    public void setIdCupom(Integer idCupom) {
        this.idCupom = idCupom;
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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
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

    public String getDataEmissaoInicial() {
        return dataEmissaoInicial;
    }

    public void setDataEmissaoInicial(String dataEmissaoInicial) {
        this.dataEmissaoInicial = dataEmissaoInicial;
    }

    public String getDataEmissaoFinal() {
        return dataEmissaoFinal;
    }

    public void setDataEmissaoFinal(String dataEmissaoFinal) {
        this.dataEmissaoFinal = dataEmissaoFinal;
    }

    public String getTipoDataEmissao() {
        return tipoDataEmissao;
    }

    public void setTipoDataEmissao(String tipoDataEmissao) {
        this.tipoDataEmissao = tipoDataEmissao;
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

    public class ObjectJasper {

        private Object cupom_descricao;
        private Object data_evento;
        private Object cupom_emissao;
        private Object pessoa_nome;
        private Object pessoa_sexo;
        private Object pessoa_idade;
        private Object parentesco;
        private Object usuario_login;
        private Object operador_nome;

        public ObjectJasper(Object cupom_descricao, Object data_evento, Object cupom_emissao, Object pessoa_nome, Object pessoa_sexo, Object pessoa_idade, Object parentesco, Object usuario_login, Object operador_nome) {
            this.cupom_descricao = cupom_descricao;
            this.data_evento = data_evento;
            this.cupom_emissao = cupom_emissao;
            this.pessoa_nome = pessoa_nome;
            this.pessoa_sexo = pessoa_sexo;
            this.pessoa_idade = pessoa_idade;
            this.parentesco = parentesco;
            this.usuario_login = usuario_login;
            this.operador_nome = operador_nome;
        }

        public Object getCupom_descricao() {
            return cupom_descricao;
        }

        public void setCupom_descricao(Object cupom_descricao) {
            this.cupom_descricao = cupom_descricao;
        }

        public Object getData_evento() {
            return data_evento;
        }

        public void setData_evento(Object data_evento) {
            this.data_evento = data_evento;
        }

        public Object getCupom_emissao() {
            return cupom_emissao;
        }

        public void setCupom_emissao(Object cupom_emissao) {
            this.cupom_emissao = cupom_emissao;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getPessoa_sexo() {
            return pessoa_sexo;
        }

        public void setPessoa_sexo(Object pessoa_sexo) {
            this.pessoa_sexo = pessoa_sexo;
        }

        public Object getPessoa_idade() {
            return pessoa_idade;
        }

        public void setPessoa_idade(Object pessoa_idade) {
            this.pessoa_idade = pessoa_idade;
        }

        public Object getParentesco() {
            return parentesco;
        }

        public void setParentesco(Object parentesco) {
            this.parentesco = parentesco;
        }

        public Object getUsuario_login() {
            return usuario_login;
        }

        public void setUsuario_login(Object usuario_login) {
            this.usuario_login = usuario_login;
        }

        public Object getOperador_nome() {
            return operador_nome;
        }

        public void setOperador_nome(Object operador_nome) {
            this.operador_nome = operador_nome;
        }

    }
}
