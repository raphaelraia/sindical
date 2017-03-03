package br.com.rtools.relatorios.beans;

import br.com.rtools.locadoraFilme.Genero;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioLocadoraDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioLocadoraBean implements Serializable {

    private Integer idRelatorioOrdem;
    private Integer idRelatorio;
    private List<SelectItem> listRelatorio;
    private List<SelectItem> listRelatorioOrdem;
    private List<Filters> filters;
    private Map<String, Integer> listGenero;
    private List selectedGenero;
    private Map<String, Integer> listFilial;
    private List selectedFilial;
    private String mesAnoLancamento;
    private String selectedOrder;

    public RelatorioLocadoraBean() {
        selectedOrder = "";
        idRelatorio = null;
        idRelatorioOrdem = null;
        listRelatorio = new ArrayList();
        listRelatorioOrdem = new ArrayList();
        loadFilters();
        loadRelatorios();
        loadRelatoriosOrdem();
    }

    public void clear() {
        GenericaSessao.put("relatorioLocadoraBean", new RelatorioLocadoraBean());
    }

    public void listener(String tcase) {
        if (tcase.equals("relatorios")) {
            loadRelatoriosOrdem();
        }
    }

    public void limparFiltro() {
        GenericaSessao.put("relatorioSociosBean", new RelatorioSociosBean());
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("filial", "Filial", false));
        filters.add(new Filters("genero", "Gênero", false));
        // filters.add(new Filters("lancamento", "Lançamento", false));
        filters.add(new Filters("mes_ano_lancamento", "Mês/Ano - Lançamento", false));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters f = new Filters();
        f.setKey(filter);
        f.setActive(false);
        for (Filters f2 : filters) {
            if (f2.getKey().equals(filter)) {
                f2.setActive(false);
            }
        }
        load(f);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {

            case "mes_ano_lancamento":
                mesAnoLancamento = "";
                break;
            case "genero":
                listGenero = new LinkedHashMap<>();
                selectedGenero = new ArrayList();
                if (filter.getActive()) {
                    loadGenero();
                }
                break;
            case "filial":
                listFilial = new LinkedHashMap<>();
                selectedFilial = new ArrayList();
                if (filter.getActive()) {
                    loadFilial();
                }
                break;
        }
    }

    public String print() {
        Relatorios relatorios = new RelatorioDao().pesquisaRelatorios(idRelatorio);
        List<ObjectLocadora> listObjectLocadora = new ArrayList<>();
        if (mesAnoLancamento != null && !mesAnoLancamento.isEmpty()) {
            mesAnoLancamento = mesAnoLancamento.substring(0, 2) + "/" + mesAnoLancamento.substring(2, 6);
        }
        List list = new RelatorioLocadoraDao().find(relatorios.getNome(), inIdFilial(), inIdGenero(), mesAnoLancamento, getShow("lancamento"));
        if (list.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Nenhum registro encontrado!");
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            listObjectLocadora.add(
                    new ObjectLocadora(
                            o.get(0).toString().toUpperCase(),
                            o.get(1),
                            o.get(2),
                            o.get(3),
                            o.get(4),
                            o.get(5),
                            o.get(6).toString().toUpperCase()
                    )
            );
        }
        Jasper.TYPE = "default";
        Jasper.TITLE = relatorios.getNome();
        Map map = new HashMap();
        map.put("groups", selectedOrder);
        Jasper.printReports(relatorios.getJasper(), relatorios.getNome(), (Collection) listObjectLocadora, map);
        return null;
    }

    public Boolean getShow(String filtro) {
        try {
            for (Filters f : filters) {
                if (f.getKey().equals(filtro)) {
                    if (f.getActive()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void loadRelatorios() {
        listRelatorio = new ArrayList();
        RelatorioDao db = new RelatorioDao();
        List<Relatorios> list = db.pesquisaTipoRelatorio(new Rotina().get().getId());
        Integer default_id = 0;
        for (int i = 0; i < list.size(); i++) {
            Boolean disabled = false;
            if (i == 0) {
                idRelatorio = list.get(i).getId();
                default_id = list.get(i).getId();
            }
            if (list.get(i).getPrincipal()) {
                default_id = list.get(i).getId();
                idRelatorio = list.get(i).getId();
            }
            listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome(), "", disabled));
        }
    }

    public void loadRelatoriosOrdem() {
        listRelatorioOrdem = new ArrayList();
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

    public void loadGenero() {
        listGenero = new LinkedHashMap<>();
        selectedGenero = new ArrayList();
        List<Genero> list = new Dao().list(new Genero(), true);
        for (int i = 0; i < list.size(); i++) {
            listGenero.put(list.get(i).getDescricao(), list.get(i).getId());
        }
    }

    public void loadFilial() {
        listFilial = new LinkedHashMap<>();
        selectedFilial = new ArrayList();
        List<Filial> list = new FilialDao().findByTabela("loc_titulo_filial");
        for (int i = 0; i < list.size(); i++) {
            listFilial.put(list.get(i).getFilial().getPessoa().getNome(), list.get(i).getId());
        }
    }

    public String inIdFilial() {
        String ids = null;
        if (selectedFilial != null) {
            ids = "";
            for (int i = 0; i < selectedFilial.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedFilial.get(i).toString();
                } else {
                    ids += "," + selectedFilial.get(i).toString();
                }
            }
        }
        return ids;
    }

    public String inIdGenero() {
        String ids = null;
        if (selectedGenero != null) {
            ids = "";
            for (int i = 0; i < selectedGenero.size(); i++) {
                if (i == 0) {
                    ids = "" + selectedGenero.get(i).toString();
                } else {
                    ids += "," + selectedGenero.get(i).toString();
                }
            }
        }
        return ids;
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

    public List<Filters> getFilters() {
        return filters;
    }

    public void setFilters(List<Filters> filters) {
        this.filters = filters;
    }

    public Map<String, Integer> getListGenero() {
        return listGenero;
    }

    public void setListGenero(Map<String, Integer> listGenero) {
        this.listGenero = listGenero;
    }

    public List getSelectedGenero() {
        return selectedGenero;
    }

    public void setSelectedGenero(List selectedGenero) {
        this.selectedGenero = selectedGenero;
    }

    public Map<String, Integer> getListFilial() {
        return listFilial;
    }

    public void setListFilial(Map<String, Integer> listFilial) {
        this.listFilial = listFilial;
    }

    public List getSelectedFilial() {
        return selectedFilial;
    }

    public void setSelectedFilial(List selectedFilial) {
        this.selectedFilial = selectedFilial;
    }

    public String getMesAnoLancamento() {
        return mesAnoLancamento;
    }

    public void setMesAnoLancamento(String mesAnoLancamento) {
        this.mesAnoLancamento = mesAnoLancamento;
    }

    public String getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(String selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public class ObjectLocadora {

        private Object genero;
        private Object titulo;
        private Object qtde;
        private Object cadastro;
        private Object lancamento;
        private Object codigo_barras;
        private Object filial;

        public ObjectLocadora() {
            this.genero = null;
            this.titulo = null;
            this.qtde = null;
            this.cadastro = null;
            this.lancamento = null;
            this.codigo_barras = null;
            this.filial = null;
        }

        public ObjectLocadora(Object genero, Object titulo, Object qtde, Object cadastro, Object lancamento, Object codigo_barras, Object filial) {
            this.genero = genero;
            this.titulo = titulo;
            this.qtde = qtde;
            this.cadastro = cadastro;
            this.lancamento = lancamento;
            this.codigo_barras = codigo_barras;
            this.filial = filial;
        }

        public Object getGenero() {
            return genero;
        }

        public void setGenero(Object genero) {
            this.genero = genero;
        }

        public Object getTitulo() {
            return titulo;
        }

        public void setTitulo(Object titulo) {
            this.titulo = titulo;
        }

        public Object getQtde() {
            return qtde;
        }

        public void setQtde(Object qtde) {
            this.qtde = qtde;
        }

        public Object getCadastro() {
            return cadastro;
        }

        public void setCadastro(Object cadastro) {
            this.cadastro = cadastro;
        }

        public Object getLancamento() {
            return lancamento;
        }

        public void setLancamento(Object lancamento) {
            this.lancamento = lancamento;
        }

        public Object getCodigo_barras() {
            return codigo_barras;
        }

        public void setCodigo_barras(Object codigo_barras) {
            this.codigo_barras = codigo_barras;
        }

        public Object getFilial() {
            return filial;
        }

        public void setFilial(Object filial) {
            this.filial = filial;
        }

    }

}
