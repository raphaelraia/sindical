package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.PeriodoMensalidade;
import br.com.rtools.sistema.Mes;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.dao.FindDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class PeriodoMensalidadeBean implements Serializable {

    private PeriodoMensalidade periodoMensalidade;
    private List<SelectItem> listPeriodo;
    private List<SelectItem> listMes;
    private Integer idPeriodo;
    private Integer idMes;
    private List<PeriodoMensalidade> listPeriodoMensalidade;

    public PeriodoMensalidadeBean() {
        this.periodoMensalidade = new PeriodoMensalidade();
        this.listPeriodo = new ArrayList();
        this.listMes = new ArrayList();
        this.idPeriodo = null;
        this.idMes = null;
        this.listPeriodoMensalidade = new ArrayList();
        loadListPeriodo();
        loadListMes();
    }

    public void save() {
        if (listMes.isEmpty()) {
            GenericaMensagem.warn("VALIDAÇÃO", "CADASTRAR MESES!");
            return;
        }
        Dao dao = new Dao();
        periodoMensalidade.setPeriodo((Periodo) dao.find(new Periodo(), idPeriodo));
        periodoMensalidade.setMes((Mes) dao.find(new Mes(), idMes));
        if (periodoMensalidade.getId() == null) {
            for (int i = 0; i < listPeriodoMensalidade.size(); i++) {
                if (Objects.equals(listPeriodoMensalidade.get(i).getPeriodo().getId(), idPeriodo)) {
                    GenericaMensagem.warn("VALIDAÇÃO", "REGISTRO JÁ EXISTE!");
                    return;
                }
            }
            for (int i = 0; i < listPeriodoMensalidade.size(); i++) {
                if (Objects.equals(listPeriodoMensalidade.get(i).getPeriodo().getId(), idPeriodo) && listPeriodoMensalidade.get(i).getMes() == null) {
                    GenericaMensagem.warn("VALIDAÇÃO", "REGISTRO JÁ EXISTE!");
                    return;
                }
            }
            new Dao().save(periodoMensalidade, true);
            GenericaMensagem.info("Sucesso", "Registro inserido");
        } else {
            new Dao().update(periodoMensalidade, true);
            GenericaMensagem.info("Sucesso", "Registro atualizado");
        }
        loadListMes();
        listPeriodoMensalidade = new ArrayList();
        periodoMensalidade = new PeriodoMensalidade();
    }

    public void edit(PeriodoMensalidade ps) {
        periodoMensalidade = (PeriodoMensalidade) new Dao().find(ps);
        idPeriodo = periodoMensalidade.getPeriodo().getId();
        idMes = periodoMensalidade.getMes().getId();
        if (idMes != null) {
            loadListMes(true);
        }
    }

    public void delete(PeriodoMensalidade ps) {
        new Dao().delete(ps, true);
        periodoMensalidade = new PeriodoMensalidade();
        GenericaMensagem.info("Sucesso", "Registro removido");
        loadListMes();
        listPeriodoMensalidade = new ArrayList();
    }

    public PeriodoMensalidade getPeriodoMensalidade() {
        return periodoMensalidade;
    }

    public void setPeriodoMensalidade(PeriodoMensalidade periodoMensalidade) {
        this.periodoMensalidade = periodoMensalidade;
    }

    public List<SelectItem> getListPeriodo() {
        return listPeriodo;
    }

    public void setListPeriodo(List<SelectItem> listPeriodo) {
        this.listPeriodo = listPeriodo;
    }

    public List<SelectItem> getListMes() {
        return listMes;
    }

    public void setListMes(List<SelectItem> listMes) {
        this.listMes = listMes;
    }

    public Integer getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(Integer idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Integer getIdMes() {
        return idMes;
    }

    public void setIdMes(Integer idMes) {
        this.idMes = idMes;
    }

    public List<PeriodoMensalidade> getListPeriodoMensalidade() {
        if (listPeriodoMensalidade.isEmpty()) {
            listPeriodoMensalidade = new Dao().list(new PeriodoMensalidade(), true);
        }
        return listPeriodoMensalidade;
    }

    public void setListPeriodoMensalidade(List<PeriodoMensalidade> listPeriodoMensalidade) {
        this.listPeriodoMensalidade = listPeriodoMensalidade;
    }

    public void loadListPeriodo() {
        listPeriodo = new ArrayList();
        List<Periodo> list = new Dao().list(new Periodo(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idPeriodo = list.get(i).getId();
            }
            listPeriodo.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListMes() {
        loadListMes(false);
    }

    public void loadListMes(Boolean all) {
        listMes = new ArrayList();
        List<Mes> list;
        if (all != null && all) {
            list = new Dao().list(new Mes(), true);
        } else {
            list = new FindDao().findNotInByTabela(Mes.class, "sis_mes", new String[]{"id"}, "soc_periodo_mensalidade", "id_mes", "id_periodo", "" + idPeriodo, "");
        }
        listMes.add(new SelectItem(null, "-- FILIAÇÃO --"));
        if (all == null && !all) {
            idMes = null;
        }
        for (int i = 0; i < list.size(); i++) {
            listMes.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao().toUpperCase()));
        }
    }

}
