package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.dao.SisProcessoDao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class SisProcessoBean implements Serializable {

    private List<SelectItem>[] listSelectItem;
    private List<Processos> listProcessos;
    private Boolean[] filtro;
    private Date[] data;
    private String[] hora;
    private Integer[] index;
    private String tipo;
    private String indexAccordion;
    private String porPesquisa;
    private String descPesquisa;

    @PostConstruct
    public void init() {
        filtro = new Boolean[5];
        filtro[0] = false;
        filtro[1] = false;
        filtro[2] = false;
        filtro[3] = false;
        filtro[4] = false;
        filtro[0] = false;
        filtro[1] = false;
        filtro[2] = false;
        filtro[3] = false;
        listProcessos = new ArrayList<>();
        listSelectItem = new ArrayList[1];
        listSelectItem[0] = new ArrayList<>();
        data = new Date[2];
        data[0] = DataHoje.dataHoje();
        data[1] = DataHoje.dataHoje();
        hora = new String[2];
        hora[0] = "";
        hora[1] = "";
        index = new Integer[1];
        index[0] = 0;
        tipo = "Avançado";
        indexAccordion = "Avançado";
        porPesquisa = "";
        descPesquisa = "";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("sisProcessoBean");
    }

    public List<SelectItem> getListRotinas() {
        if (listSelectItem[0].isEmpty()) {
            List<Rotina> list = (List<Rotina>) new SisProcessoDao().findRotinasGroup();
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[0].add(new SelectItem(i, list.get(i).getRotina(), "" + list.get(i).getId()));
            }
            if (listSelectItem[0].isEmpty()) {
                listSelectItem[0] = new ArrayList<>();
            }
        }
        return listSelectItem[0];
    }

    public void typeChange(TabChangeEvent event) {
        tipo = event.getTab().getTitle();
        indexAccordion = ((AccordionPanel) event.getComponent()).getActiveIndex();
    }

    public void selectedDataInicial(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.data[0] = DataHoje.converte(format.format(event.getObject()));
    }

    public void selectedDataFinal(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.data[1] = DataHoje.converte(format.format(event.getObject()));
    }

    public void clear() {
        if (!filtro[0]) {
            listSelectItem = new ArrayList[1];
            listSelectItem[0] = new ArrayList<>();
        }
        if (!filtro[1]) {
            data[0] = DataHoje.dataHoje();
            data[1] = DataHoje.dataHoje();
            hora[0] = "";
            hora[1] = "";
        }
        if (!filtro[4]) {
            porPesquisa = "";
            descPesquisa = "";
        }
    }

    public void close(String close) {
        if (close.equals("periodo")) {
            filtro[0] = false;
            data[0] = DataHoje.dataHoje();
            data[1] = DataHoje.dataHoje();
            hora[0] = "";
            hora[1] = "";
        } else if (close.equals("rotina")) {
            filtro[2] = false;
            listSelectItem = new ArrayList[1];
            listSelectItem[0] = new ArrayList<>();
        } else if (close.equals("descricao")) {
            filtro[4] = false;
            descPesquisa = "";
            porPesquisa = "";
        }
        PF.update("form_logs:id_panel");
    }

    public String getIndexAccordion() {
        return indexAccordion;
    }

    public void setIndexAccordion(String indexAccordion) {
        this.indexAccordion = indexAccordion;
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
     * <li>[0] List[SelectItem] Evento</li>
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

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public void removeFiltro() {
        GenericaSessao.put("removeFiltro", true);
    }

    /**
     * <strong>Filtros</strong>
     * <ul>
     * <li>[0] Periodo</li>
     * <li>[1] Periodo Convenção</li>
     * <li>[2] Periodo Pesquisas Oposição</li>
     * <li>[3] Cnae</li>
     * </ul>
     *
     * @return boolean
     */
    public Boolean[] getFiltro() {
        return filtro;
    }

    public void setFiltro(Boolean[] filtro) {
        this.filtro = filtro;
    }

    public void loadProcessos() {
        listProcessos.clear();
        String dtInicial = null;
        String dtFinal = null;
        String hrInicial = null;
        String hrFinal = null;
        int idR = 0;
        int idU = 0;
        String idInEventos = null;
        if (filtro[0]) {
            dtInicial = DataHoje.converteData(data[0]);
            dtFinal = DataHoje.converteData(data[1]);
            hrInicial = hora[0];
            hrFinal = hora[1];
        }
        if (filtro[2]) {
            if (!getListRotinas().isEmpty()) {
                idR = Integer.parseInt(getListRotinas().get(index[0]).getDescription());
            }
        }
        if (filtro[4]) {

        }
        List list = new SisProcessoDao().find(dtInicial, dtFinal, hrInicial, hrFinal, idR, descPesquisa);
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            listProcessos.add(new Processos(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), o.get(8)));
        }
    }

    public List<Processos> getListProcessos() {
        return listProcessos;
    }

    public void setListProcessos(List<Processos> listProcessos) {
        this.listProcessos = listProcessos;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    /**
     * <strong>Data</strong>
     * <ul>
     * <li>[0]Inicial</li>
     * <li>[0]Final</li>
     * </ul>
     *
     * @return Date
     */
    public Date[] getData() {
        return data;
    }

    public void setData(Date[] data) {
        this.data = data;
    }

    /**
     * <strong>Hora</strong>
     * <ul>
     * <li>[0]Inicial</li>
     * <li>[0]Final</li>
     * </ul>
     *
     * @return Date
     */
    public String[] getHora() {
        return hora;
    }

    public void setHora(String[] hora) {
        this.hora = hora;
    }

    public class Processos {

        private Object rotina_id;
        private Object rotina_descricao;
        private Object detalhes;
        private Object tempo_min;
        private Object tempo_med;
        private Object tempo_max;
        private Object tempo_query_min;
        private Object tempo_query_med;
        private Object tempo_query_max;

        public Processos(Object rotina_id, Object rotina_descricao, Object detalhes, Object tempo_min, Object tempo_med, Object tempo_max, Object tempo_query_min, Object tempo_query_med, Object tempo_query_max) {
            this.rotina_id = rotina_id;
            this.rotina_descricao = rotina_descricao;
            this.detalhes = detalhes;
            this.tempo_min = tempo_min;
            this.tempo_med = tempo_med;
            this.tempo_max = tempo_max;
            this.tempo_query_min = tempo_query_min;
            this.tempo_query_med = tempo_query_med;
            this.tempo_query_max = tempo_query_max;
        }

        public Object getRotina_id() {
            return rotina_id;
        }

        public void setRotina_id(Object rotina_id) {
            this.rotina_id = rotina_id;
        }

        public Object getRotina_descricao() {
            return rotina_descricao;
        }

        public void setRotina_descricao(Object rotina_descricao) {
            this.rotina_descricao = rotina_descricao;
        }

        public Object getDetalhes() {
            return detalhes;
        }

        public void setDetalhes(Object detalhes) {
            this.detalhes = detalhes;
        }

        public Object getTempo_min() {
            return tempo_min;
        }

        public void setTempo_min(Object tempo_min) {
            this.tempo_min = tempo_min;
        }

        public Object getTempo_med() {
            return tempo_med;
        }

        public void setTempo_med(Object tempo_med) {
            this.tempo_med = tempo_med;
        }

        public Object getTempo_max() {
            return tempo_max;
        }

        public void setTempo_max(Object tempo_max) {
            this.tempo_max = tempo_max;
        }

        public Object getTempo_query_min() {
            return tempo_query_min;
        }

        public void setTempo_query_min(Object tempo_query_min) {
            this.tempo_query_min = tempo_query_min;
        }

        public Object getTempo_query_med() {
            return tempo_query_med;
        }

        public void setTempo_query_med(Object tempo_query_med) {
            this.tempo_query_med = tempo_query_med;
        }

        public Object getTempo_query_max() {
            return tempo_query_max;
        }

        public void setTempo_query_max(Object tempo_query_max) {
            this.tempo_query_max = tempo_query_max;
        }

    }

    public Object tempoMedio() {
        try {
            return new SisProcessoDao().avgByRotina(new Rotina().get().getId());
        } catch (Exception e) {
            return 0;
        }
    }
}
