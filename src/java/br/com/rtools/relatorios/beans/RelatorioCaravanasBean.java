package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Caravana;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioCaravanasDao;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Reports;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioCaravanasBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;

    private List<SelectItem> listCaravanas;
    private Integer idCaravana;

    @PostConstruct
    public void init() {
        listFilters = new ArrayList();

        listRelatorio = new ArrayList<>();
        listCaravanas = new ArrayList<>();
        idRelatorio = null;
        idCaravana = null;

        loadFilters();
        loadListCaravanas();
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioCaravanasBean");
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
        String detalheRelatorio = "";
        List<ObjectJasper> cs = new ArrayList<>();
        List<Etiquetas> e = new ArrayList<>();
        sisProcesso.startQuery();
        RelatorioCaravanasDao rcd = new RelatorioCaravanasDao();
        if (!listRelatorioOrdem.isEmpty()) {
            if (idRelatorioOrdem != null) {
                rcd.setRelatorioOrdem((RelatorioOrdem) new Dao().find(new RelatorioOrdem(), idRelatorioOrdem));
            }
        }
        rcd.setRelatorios(r);
        List list = rcd.find(idCaravana + "");
        sisProcesso.finishQuery();
        ObjectJasper oj = new ObjectJasper();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (null != r.getId()) {
                oj = new ObjectJasper(
                        o.get(0),
                        o.get(1),
                        o.get(2),
                        o.get(3),
                        o.get(4),
                        o.get(5),
                        o.get(6),
                        o.get(7),
                        o.get(8),
                        o.get(9),
                        o.get(10),
                        o.get(11),
                        o.get(12),
                        o.get(13)
                );
            }
            cs.add(oj);
        }
        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }
        Reports reports = new Reports();
        reports.setTITLE(r.getNome() + " " + cs.get(0).getDescricao_caravana() + " - " + DataHoje.converteData((Date) cs.get(0).getData()));
        reports.print(r.getJasper(), r.getNome(), (Collection) cs);
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

    public void loadListCaravanas() {
        listCaravanas = new ArrayList();
        List<Caravana> list = new RelatorioCaravanasDao().findAll();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCaravana = list.get(i).getId();
            }
            listCaravanas.add(new SelectItem(list.get(i).getId(), list.get(i).getEvento().getDescricaoEvento().getDescricao() + " " + list.get(i).getTituloComplemento() + " (" + list.get(i).getDataEmbarqueIda() + " - " + list.get(i).getDataEmbarqueRetorno() + ")"));
        }
    }

    public void load() {
        // loadListaFiltro();
        loadRelatorioOrdem();
    }

    public void loadFilters() {
        listFilters = new ArrayList<>();
        listFilters.add(new Filters("caravanas", "Caravanas", true, true));
        // listFilters.add(new Filters("datas", "Datas", false, false));
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
            case "caravanas":
                if (filter.getActive()) {
                    loadListCaravanas();
                } else {
                    listCaravanas = new ArrayList<>();
                    idCaravana = null;
                }
                break;
        }
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters filters = new Filters();
        filters.setKey(filter);
        filters.setActive(false);
        for (Filters f : listFilters) {
            if (f.getKey().equals(filter)) {
                f.setActive(false);
            }
        }
        load(filters);
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

    public Boolean getShow(String filtro) {
        if (listFilters.stream().filter((filters) -> (filters.getKey().equals(filtro))).anyMatch((filters) -> (filters.getActive()))) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getListCaravanas() {
        return listCaravanas;
    }

    public void setListCaravanas(List<SelectItem> listCaravanas) {
        this.listCaravanas = listCaravanas;
    }

    public Integer getIdCaravana() {
        return idCaravana;
    }

    public void setIdCaravana(Integer idCaravana) {
        this.idCaravana = idCaravana;
    }

    public class ObjectJasper {

        private Object descricao_caravana;
        private Object data;
        private Object emissao;
        private Object operador;
        private Object id_responsavel;
        private Object responsavel;
        private Object id_beneficiario;
        private Object beneficiario;
        private Object pagamento;
        private Object vencimento;
        private Object valor;
        private Object valor_baixa;
        private Object caixa;
        private Object observacao;

        public ObjectJasper() {
            this.descricao_caravana = null;
            this.data = null;
            this.emissao = null;
            this.operador = null;
            this.id_responsavel = null;
            this.responsavel = null;
            this.id_beneficiario = null;
            this.beneficiario = null;
            this.pagamento = null;
            this.vencimento = null;
            this.valor = null;
            this.valor_baixa = null;
            this.caixa = null;
            this.observacao = null;
        }

        public ObjectJasper(Object descricao_caravana, Object data, Object emissao, Object operador, Object id_responsavel, Object responsavel, Object id_beneficiario, Object beneficiario, Object pagamento, Object vencimento, Object valor, Object valor_baixa, Object caixa, Object observacao) {
            this.descricao_caravana = descricao_caravana;
            this.data = data;
            this.emissao = emissao;
            this.operador = operador;
            this.id_responsavel = id_responsavel;
            this.responsavel = responsavel;
            this.id_beneficiario = id_beneficiario;
            this.beneficiario = beneficiario;
            this.pagamento = pagamento;
            this.vencimento = vencimento;
            this.valor = valor;
            this.valor_baixa = valor_baixa;
            this.caixa = caixa;
            this.observacao = observacao;
        }

        public Object getDescricao_caravana() {
            return descricao_caravana;
        }

        public void setDescricao_caravana(Object descricao_caravana) {
            this.descricao_caravana = descricao_caravana;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public Object getEmissao() {
            return emissao;
        }

        public void setEmissao(Object emissao) {
            this.emissao = emissao;
        }

        public Object getOperador() {
            return operador;
        }

        public void setOperador(Object operador) {
            this.operador = operador;
        }

        public Object getId_responsavel() {
            return id_responsavel;
        }

        public void setId_responsavel(Object id_responsavel) {
            this.id_responsavel = id_responsavel;
        }

        public Object getResponsavel() {
            return responsavel;
        }

        public void setResponsavel(Object responsavel) {
            this.responsavel = responsavel;
        }

        public Object getId_beneficiario() {
            return id_beneficiario;
        }

        public void setId_beneficiario(Object id_beneficiario) {
            this.id_beneficiario = id_beneficiario;
        }

        public Object getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Object beneficiario) {
            this.beneficiario = beneficiario;
        }

        public Object getPagamento() {
            return pagamento;
        }

        public void setPagamento(Object pagamento) {
            this.pagamento = pagamento;
        }

        public Object getVencimento() {
            return vencimento;
        }

        public void setVencimento(Object vencimento) {
            this.vencimento = vencimento;
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

        public Object getCaixa() {
            return caixa;
        }

        public void setCaixa(Object caixa) {
            this.caixa = caixa;
        }

        public Object getObservacao() {
            return observacao;
        }

        public void setObservacao(Object observacao) {
            this.observacao = observacao;
        }

    }
}
