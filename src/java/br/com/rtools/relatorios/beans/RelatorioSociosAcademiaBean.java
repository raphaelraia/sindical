package br.com.rtools.relatorios.beans;

import br.com.rtools.academia.AcademiaServicoValor;
import br.com.rtools.academia.dao.AcademiaDao;
import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.db.CategoriaDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioSociosAcademiaDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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

@ManagedBean
@SessionScoped
public class RelatorioSociosAcademiaBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List selectedModalidades;
    private List selectedPeriodos;
    private Map<String, Integer> listModalidades;
    private Map<String, Integer> listPeriodos;
    private Relatorios relatorios;
    private List selectedGrupoCategoria;
    private List selectedCategoria;
    private Map<String, Integer> listCategoria;
    private Map<String, Integer> listGrupoCategoria;

    @PostConstruct
    public void init() {
        loadListaFiltro();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioSociosAcademiaBean");
    }

    // LISTENERS
    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "modalidade":
                loadModalidade();
                break;
            case "grupo_categoria":
                loadGrupoCategoria();
                loadCategoria();
                break;
        }
    }

    // LOAD
    public void loadListaFiltro() {
        listFilters = new ArrayList();
        listFilters.add(new Filters("modalidade", "Modalidade", false));
        listFilters.add(new Filters("grupo_categoria", "Grupo Categoria", false));

    }

    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        try {
            if (listRelatorio.isEmpty()) {
                List<Relatorios> list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(new Rotina().get().getId());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPrincipal()) {
                        idRelatorio = list.get(i).getId();
                    }
                    listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
                }
                if(idRelatorio == null) {
                    if(!listRelatorio.isEmpty()) {
                        idRelatorio = list.get(0).getId();
                    }
                }
                loadRelatorioOrdem();
            }
        } catch (Exception e) {

        }
    }

    public void loadRelatorioOrdem() {
        if (idRelatorio != null) {
            listRelatorioOrdem = new ArrayList();
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

    public void loadModalidade() {
        listModalidades = new HashMap<>();
        selectedModalidades = new ArrayList();
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

    public void loadCategoria() {
        selectedCategoria = new ArrayList();
        listCategoria = new LinkedHashMap<>();
        List<Categoria> list = (List<Categoria>) new CategoriaDao().findCategoriaByGrupoCategoria(inIdGrupoCategoria());
        for (int i = 0; i < list.size(); i++) {
            listCategoria.put(list.get(i).getCategoria(), list.get(i).getId());
        }
    }

    public void loadGrupoCategoria() {
        selectedGrupoCategoria = new ArrayList();
        listGrupoCategoria = new LinkedHashMap<>();
        List<GrupoCategoria> list = new Dao().list(new GrupoCategoria(), true);
        for (int i = 0; i < list.size(); i++) {
            listGrupoCategoria.put(list.get(i).getGrupoCategoria(), list.get(i).getId());
        }
    }

    public void print() throws FileNotFoundException {
        print(0);
    }

    public void print(int tcase) throws FileNotFoundException {
        Relatorios relatorios = null;
        if (!getListRelatorio().isEmpty()) {
            RelatorioDao rgdb = new RelatorioDao();
            relatorios = rgdb.pesquisaRelatorios(idRelatorio);
        } else {
            GenericaMensagem.info("Sistema", "Nenhum relatório encontrado!");
            return;
        }
        if (relatorios == null) {
            return;
        }
        String detalheRelatorio = "";
        String inIdModalidades = inIdModalidades();
        String inIdPeriodos = inIdPeriodos();
        List listDetalhePesquisa = new ArrayList();
        String in_grupo_categoria = inIdGrupoCategoria();
        String in_categoria = inIdCategoria();
        RelatorioSociosAcademiaDao rsad = new RelatorioSociosAcademiaDao();
        rsad.setRelatorios(getRelatorios());
        RelatorioOrdem ro = null;
        if(idRelatorioOrdem != null) {
            ro = (RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem);
        }
        rsad.setRelatorioOrdem(ro);
        List list = rsad.find(inIdModalidades, in_grupo_categoria, in_categoria);
        if (list.isEmpty()) {
            GenericaMensagem.info("Sistema", "Não existem registros para o relatório selecionado!");
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
        List<ParametroRelatorioSociosAcademia> prsas = new ArrayList<>();
        ParametroRelatorioSociosAcademia prsa;
        for (Object list1 : list) {
            prsa = new ParametroRelatorioSociosAcademia(
                    ((List) list1).get(0),
                    ((List) list1).get(1),
                    ((List) list1).get(2),
                    ((List) list1).get(3),
                    ((List) list1).get(4),
                    ((List) list1).get(5),
                    ((List) list1).get(6),
                    ((List) list1).get(7),
                    ((List) list1).get(8)
            );
            prsas.add(prsa);
        }
        Jasper.TITLE = getRelatorios().getNome();
        if (!prsas.isEmpty()) {
            if (getRelatorios().getExcel()) {
                Jasper.EXCEL_FIELDS = relatorios.getCamposExcel();
            } else {
                Jasper.EXCEL_FIELDS = "";
            }
            Jasper.TYPE = "default";
            Jasper.printReports(getRelatorios().getJasper(), getRelatorios().getNome(), (Collection) prsas);

        }
    }

    public Map<String, Integer> getListModalidades() {
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
            if (!Objects.equals(relatorios.getId(), idRelatorio)) {
                Jasper.EXPORT_TO = false;
            }
            relatorios = (Relatorios) new Dao().find(new Relatorios(), idRelatorio);
        } catch (Exception e) {
            relatorios = new Relatorios();
            Jasper.EXPORT_TO = false;
        }
        return relatorios;
    }

    public Map<String, Integer> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(Map<String, Integer> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public Map<String, Integer> getListGrupoCategoria() {
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

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
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

    public class ParametroRelatorioSociosAcademia {

        private Object titular_nome;
        private Object aluno_nome;
        private Object parentesco_descricao;
        private Object qtde_dependentes;
        private Object valor;
        private Object desconto;
        private Object valor_cheio;
        private Object categoria_descricao;
        private Object modalidade;

        public ParametroRelatorioSociosAcademia(Object titular_nome, Object aluno_nome, Object parentesco_descricao, Object qtde_dependentes, Object valor, Object desconto, Object valor_cheio, Object categoria_descricao, Object modalidade) {
            this.titular_nome = titular_nome;
            this.aluno_nome = aluno_nome;
            this.parentesco_descricao = parentesco_descricao;
            this.qtde_dependentes = qtde_dependentes;
            this.valor = valor;
            this.desconto = desconto;
            this.valor_cheio = valor_cheio;
            this.categoria_descricao = categoria_descricao;
            this.modalidade = modalidade;
        }

        public Object getTitular_nome() {
            return titular_nome;
        }

        public void setTitular_nome(Object titular_nome) {
            this.titular_nome = titular_nome;
        }

        public Object getAluno_nome() {
            return aluno_nome;
        }

        public void setAluno_nome(Object aluno_nome) {
            this.aluno_nome = aluno_nome;
        }

        public Object getParentesco_descricao() {
            return parentesco_descricao;
        }

        public void setParentesco_descricao(Object parentesco_descricao) {
            this.parentesco_descricao = parentesco_descricao;
        }

        public Object getQtde_dependentes() {
            return qtde_dependentes;
        }

        public void setQtde_dependentes(Object qtde_dependentes) {
            this.qtde_dependentes = qtde_dependentes;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getDesconto() {
            return desconto;
        }

        public void setDesconto(Object desconto) {
            this.desconto = desconto;
        }

        public Object getValor_cheio() {
            return valor_cheio;
        }

        public void setValor_cheio(Object valor_cheio) {
            this.valor_cheio = valor_cheio;
        }

        public Object getCategoria_descricao() {
            return categoria_descricao;
        }

        public void setCategoria_descricao(Object categoria_descricao) {
            this.categoria_descricao = categoria_descricao;
        }

        public Object getModalidade() {
            return modalidade;
        }

        public void setModalidade(Object modalidade) {
            this.modalidade = modalidade;
        }
    }
}
