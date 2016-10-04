package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.relatorios.dao.RelatorioResumoImpressaoBoletosDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioResumoImpressaoBoletosBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List<SelectItem> listDataImpressao;
    private String dataImpressao;

    @PostConstruct
    public void init() {
        new Jasper().init();
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        loadRelatorio();
        loadFilters();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioResumoImpressaoBoletosBean");
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
        if (look()) {
            GenericaMensagem.warn("Validação", "Selecione um filtro!");
            return;
        }
        if (dataImpressao == null || dataImpressao.isEmpty() || listDataImpressao.isEmpty()) {
            dataImpressao = "21/09/2016";
            GenericaMensagem.warn("Validação", "Informar data de impressão!");
            // return;
        }
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();

        if (r == null) {
            return;
        }

        List listDetalhePesquisa = new ArrayList();
        List<ObjectJasper> oj = new ArrayList();
        sisProcesso.startQuery();
        List list = new RelatorioResumoImpressaoBoletosDao().find(dataImpressao);

        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1)));
        }

        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }

        String detalheRelatorio = "";
        listDetalhePesquisa.add("Impressão: " + dataImpressao);
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

        Jasper.EXPORT_TO = true;
        Jasper.TITLE = r.getNome().toUpperCase();
        Jasper.TYPE = "default";
        Map map = new HashMap();
        map.put("detalhes_relatorio", detalheRelatorio);
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) oj, map);
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

    public void load() {
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("data_impressao", "Data", true, true));
        load(listFilters.get(0));

    }

    public boolean look() {
        for (int i = 0; i < listFilters.size(); i++) {
            if (listFilters.get(i).getActive()) {
                return false;
            }
        }
        return true;
    }

    // LISTENER
    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                loadFilters();
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
            case "data_impressao":
                dataImpressao = "";
                loadListDataImpressao();
                break;
        }
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
     * 0 CAIXA / BANCO; 1 Data
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
            r = new RelatorioDao().pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public void loadListDataImpressao() {
        listDataImpressao = new ArrayList();
        dataImpressao = "";
        List list = new RelatorioResumoImpressaoBoletosDao().findDates();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                String d = DataHoje.converteData((Date) o.get(0));
                if (i == 0) {
                    dataImpressao = d;
                }
                listDataImpressao.add(new SelectItem(d, d));
            }
        }
    }

    public List<SelectItem> getListDataImpressao() {
        return listDataImpressao;
    }

    public void setListDataImpressao(List<SelectItem> listDataImpressao) {
        this.listDataImpressao = listDataImpressao;
    }

    public String getDataImpressao() {
        return dataImpressao;
    }

    public void setDataImpressao(String dataImpressao) {
        this.dataImpressao = dataImpressao;
    }

    public class ObjectJasper {

        private Object categoria;
        private Object total;

        public ObjectJasper() {
            this.categoria = null;
            this.total = null;
        }

        public ObjectJasper(Object categoria, Object total) {
            this.categoria = categoria;
            this.total = total;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

        public Object getTotal() {
            return total;
        }

        public void setTotal(Object total) {
            this.total = total;
        }

    }
}
