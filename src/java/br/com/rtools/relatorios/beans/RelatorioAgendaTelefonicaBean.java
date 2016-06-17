package br.com.rtools.relatorios.beans;

import br.com.rtools.agenda.Agenda;
import br.com.rtools.agenda.GrupoAgenda;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.relatorios.dao.RelatorioAgendaTelefonicaDao;
import br.com.rtools.sistema.SisProcesso;
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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioAgendaTelefonicaBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listUf;
    private String uf;

    private Map<String, Integer> listGrupo;
    private List selectedGrupo;

    private Map<String, Integer> listCidade;
    private List selectedCidade;

    @PostConstruct
    public void init() {
        new Jasper().init();
        listFilters = new ArrayList();
        loadFilters();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioAgendaTelefonicaBean");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        List<Etiquetas> c = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioAgendaTelefonicaDao ratd = new RelatorioAgendaTelefonicaDao();
        List<Agenda> list = ratd.find(inIdGrupo(), inIdCidade());
        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            Endereco endereco = list.get(i).getEndereco();
            String nome = "";
            String obs = "";
            if (list.get(i).getPessoa() != null) {
                nome = list.get(i).getPessoa().getNome();
                obs = list.get(i).getNome();
            } else {
                nome = list.get(i).getNome();
            }
            c.add(new Etiquetas(nome, endereco.getLogradouro().getDescricao(), endereco.getDescricaoEndereco().getDescricao(), list.get(i).getNumero(), endereco.getBairro().getDescricao(), endereco.getCidade().getCidade(), endereco.getCidade().getUf(), endereco.getCep(), list.get(i).getComplemento(), obs));
        }
        if (c.isEmpty()) {
            GenericaMensagem.info("Sistema", "Nenhum registro encontrado!");
            return;
        }

        Jasper.printReports(
                "/Relatorios/ETIQUETAS.jasper",
                "etiquetas",
                (Collection) c
        );
        sisProcesso.finish();
    }

    // LOAD
    public void load() {

    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("grupo", "Grupo", false));
        listFilters.add(new Filters("cidade", "Cidade", false));
        load(listFilters.get(1));

    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadListCidade();
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    // LOAD
    public void load(Filters filter) {
        switch (filter.getKey()) {
            case "grupo":
                if (!filter.getActive()) {
                    listGrupo = new LinkedHashMap<>();
                    selectedGrupo = new ArrayList<>();
                } else {
                    loadListGrupo();
                }
                break;
            case "cidade":
                if (!filter.getActive()) {
                    listUf = new ArrayList();
                    uf = "SP";
                    listCidade = new LinkedHashMap<>();
                    selectedCidade = new ArrayList<>();
                } else {
                    loadListUf();
                    loadListCidade();
                }
                break;
        }
    }

    public void loadListGrupo() {
        listGrupo = new LinkedHashMap<>();
        selectedGrupo = new ArrayList<>();
        List<GrupoAgenda> list = new RelatorioAgendaTelefonicaDao().findGrupos();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listGrupo.put(list.get(i).getDescricao(), list.get(i).getId());
            }
        }
    }

    public void loadListUf() {
        listUf = new ArrayList();
        uf = "SP";
        List list = new RelatorioAgendaTelefonicaDao().findAllUf();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                listUf.add(new SelectItem(o.get(0).toString(), o.get(0).toString()));
            }
        }
    }

    public void loadListCidade() {
        listCidade = new LinkedHashMap<>();
        selectedGrupo = new ArrayList<>();
        List<Cidade> list = new RelatorioAgendaTelefonicaDao().findAllCidades(uf);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                listCidade.put(list.get(i).getCidade(), list.get(i).getId());
            }
        }
    }

    // TRATAMENTO
    public String inIdGrupo() {
        String ids = null;
        if (selectedGrupo != null) {
            for (int i = 0; i < selectedGrupo.size(); i++) {
                if (ids == null) {
                    ids = "" + selectedGrupo.get(i);
                } else {
                    ids += "," + selectedGrupo.get(i);
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

    // GETTERS AND SETTERS
    /**
     * 0 GRUPO ; 1 CIDADE
     *
     * @return
     */
    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<SelectItem> getListUf() {
        return listUf;
    }

    public void setListUf(List<SelectItem> listUf) {
        this.listUf = listUf;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Map<String, Integer> getListGrupo() {
        return listGrupo;
    }

    public void setListGrupo(Map<String, Integer> listGrupo) {
        this.listGrupo = listGrupo;
    }

    public List getSelectedGrupo() {
        return selectedGrupo;
    }

    public void setSelectedGrupo(List selectedGrupo) {
        this.selectedGrupo = selectedGrupo;
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

}
