package br.com.rtools.agenda.beans;

import br.com.rtools.agenda.Compromisso;
import br.com.rtools.agenda.CompromissoUsuario;
import br.com.rtools.agenda.dao.CompromissoUsuarioDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@ManagedBean
@SessionScoped
public class CompromissoUsuarioBean implements Serializable {

    private Compromisso compromisso;
    private CompromissoUsuario compromissoUsuario;
    private List<CompromissoUsuario> listCompromissoUsuario;
    private ScheduleModel eventModel;

    private String tipoHistorico;
    private String tipoData;
    private String dataInicial;
    private String dataFinal;
    private String cancelados;
    private List<SelectItem> listUsuario;
    private Integer idUsuarioFiltro;

    @PostConstruct
    public void init() {
        compromissoUsuario = new CompromissoUsuario();
        compromisso = new Compromisso();
        eventModel = new DefaultScheduleModel();
        loadListCompromissoUsuario();
        tipoHistorico = "hoje_amanha";
        tipoData = "";
        dataInicial = "";
        dataFinal = "";
        cancelados = "ativos";
        idUsuarioFiltro = Usuario.getUsuario().getId();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("compromissoUsuarioBean");
    }

    public void clear() {
        GenericaSessao.remove("compromissoUsuarioBean");
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "pesquisar":
                listCompromissoUsuario = new ArrayList();
                listCompromissoUsuario = new CompromissoUsuarioDao().findCompromissos(null, tipoHistorico, tipoData, dataInicial, dataFinal, cancelados, Usuario.getUsuario().getId());
                break;
            default:
                break;
        }

    }

    public void loadListCompromissoUsuario() {
        eventModel = new DefaultScheduleModel();
        listCompromissoUsuario = new ArrayList();
        listCompromissoUsuario = new CompromissoUsuarioDao().findCompromissos(null, "hoje_amanha", tipoData, dataInicial, dataFinal, "ativos", Usuario.getUsuario().getId());
//        for (int i = 0; i < listCompromissoUsuario.size(); i++) {
//            Date dataInicial = DataHoje.converteDataHora(listCompromissoUsuario.get(i).getCompromisso().getData(), listCompromissoUsuario.get(i).getCompromisso().getHoraInicial());
//            Date dataFinal = dataInicial;
//            if (!listCompromissoUsuario.get(i).getCompromisso().getHoraFinal().isEmpty()) {
//                dataFinal = DataHoje.converteDataHora(listCompromissoUsuario.get(i).getCompromisso().getData(), listCompromissoUsuario.get(i).getCompromisso().getHoraFinal());
//            }
//            eventModel.addEvent(new DefaultScheduleEvent(listCompromissoUsuario.get(i).getCompromisso().getDescricao(), dataInicial, dataFinal, listCompromissoUsuario.get(i)));
//        }
    }

    public List<CompromissoUsuario> getListCompromissoUsuario() {
        return listCompromissoUsuario;
    }

    public void setListCompromissoUsuario(List<CompromissoUsuario> listCompromissoUsuario) {
        this.listCompromissoUsuario = listCompromissoUsuario;
    }

    public void edit(CompromissoUsuario cu) {
        compromissoUsuario = cu;
    }

    public void edit(SelectEvent selectEvent) {
        DefaultScheduleEvent event = (DefaultScheduleEvent) selectEvent.getObject();
        compromissoUsuario = (CompromissoUsuario) event.getData();
    }

    public void selectedData(SelectEvent selectEvent) {
        Date d = (Date) selectEvent.getObject();
        DataHoje dh = new DataHoje();
        d = DataHoje.converte(dh.incrementarDias(1, DataHoje.converteData(d)));
        ScheduleEvent event = new DefaultScheduleEvent("", d, d);
        compromisso = new Compromisso();
        compromisso.setDtData(event.getStartDate());
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public CompromissoUsuario getCompromissoUsuario() {
        return compromissoUsuario;
    }

    public void setCompromissoUsuario(CompromissoUsuario compromissoUsuario) {
        this.compromissoUsuario = compromissoUsuario;
    }

    public Compromisso getCompromisso() {
        return compromisso;
    }

    public void setCompromisso(Compromisso compromisso) {
        this.compromisso = compromisso;
    }

    public void cancel() {
        compromissoUsuario.getCompromisso().setCancelamento(DataHoje.data());
        compromissoUsuario.getCompromisso().setUsuarioCancelador(Usuario.getUsuario());
        if (new Dao().update(compromissoUsuario.getCompromisso(), true)) {
            GenericaMensagem.info("SUCESSO", "REGISTRO ATUALIZADO");
        } else {
            GenericaMensagem.warn("ERRO", "AO ATUALIZADO REGISTRO!");
        }
    }

    public String getTipoHistorico() {
        return tipoHistorico;
    }

    public void setTipoHistorico(String tipoHistorico) {
        this.tipoHistorico = tipoHistorico;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getCancelados() {
        return cancelados;
    }

    public void setCancelados(String cancelados) {
        this.cancelados = cancelados;
    }

    public List<SelectItem> getListUsuario() {
        return listUsuario;
    }

    public void setListUsuario(List<SelectItem> listUsuario) {
        this.listUsuario = listUsuario;
    }

    public Integer getIdUsuarioFiltro() {
        return idUsuarioFiltro;
    }

    public void setIdUsuarioFiltro(Integer idUsuarioFiltro) {
        this.idUsuarioFiltro = idUsuarioFiltro;
    }

}
