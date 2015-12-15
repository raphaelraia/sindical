package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.locadoraFilme.LocadoraStatus;
import br.com.rtools.locadoraFilme.LocadoraTaxa;
import br.com.rtools.locadoraFilme.dao.LocadoraStatusDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class LocadoraStatusBean implements Serializable {

    private LocadoraStatus locadoraStatus;
    private Integer idFilial;
    private List<SelectItem> listFilial;
    private Integer idSemana;
    private List<SelectItem> listSemana;
    private Integer idLocadoraTaxa;
    private List<SelectItem> listLocadoraTaxa;
    private List<LocadoraStatus> listLocadoraStatus;

    @PostConstruct
    public void init() {
        locadoraStatus = new LocadoraStatus();
        idFilial = null;
        listFilial = new ArrayList<>();
        idSemana = null;
        listSemana = new ArrayList<>();
        idLocadoraTaxa = null;
        listLocadoraTaxa = new ArrayList<>();
        listLocadoraStatus = new ArrayList<>();
        loadFilial();
        loadLocadoraTaxa();
        loadSemana();
        loadLocadoraStatus();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("locadoraStatusBean");
    }

    public void loadFilial() {
        List<Filial> list = new Dao().list(new Filial(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            listFilial.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public void loadLocadoraTaxa() {
        List<LocadoraTaxa> list = new Dao().list(new LocadoraTaxa(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idLocadoraTaxa = list.get(i).getId();
            }
            listLocadoraTaxa.add(new SelectItem(list.get(i).getId(), list.get(i).getServicoDiaria().getDescricao() + " (" + list.get(i).getServicoDiaria().getDescricao() + ")"));
        }
    }

    public void loadSemana() {
        List<Semana> list = new Dao().list(new Semana());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSemana = list.get(i).getCurrentDay();
            }
            listSemana.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadLocadoraStatus() {
        listLocadoraStatus.clear();
        listLocadoraStatus.addAll(new LocadoraStatusDao().findAllByFilialData(idFilial));
        listLocadoraStatus.addAll(new LocadoraStatusDao().findAllByFilialSemana(idFilial));
    }

    public void save() {
        Dao dao = new Dao();
        if (idFilial == null) {
            GenericaMensagem.warn("Validação", "Informar filial!");
            return;
        }
        if (listLocadoraTaxa.isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar taxas!");
            return;
        }
        locadoraStatus.setFilial((Filial) dao.find(new Filial(), idFilial));
        if (locadoraStatus.getData() != null) {
            locadoraStatus.setSemana(null);
        } else {
            locadoraStatus.setSemana((Semana) dao.find(new Semana(), idSemana));
        }
        locadoraStatus.setTaxa((LocadoraTaxa) dao.find(new LocadoraTaxa(), idLocadoraTaxa));
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("loc_status");
        novoLog.setCodigo(locadoraStatus.getId());
        String semena_id = "Nenhum";
        if (locadoraStatus.getTaxa() != null) {
            semena_id = "" + locadoraStatus.getTaxa().getId();
        }
        if (locadoraStatus.getId() == null) {
            if (dao.save(locadoraStatus, true)) {
                novoLog.save(
                        "ID" + locadoraStatus.getId()
                        + " - Filial: (" + locadoraStatus.getFilial().getId() + ") "
                        + semena_id
                        + " - Taxa: (" + locadoraStatus.getTaxa().getId() + ") "
                        + " - Data: " + locadoraStatus.getDataString()
                );
                locadoraStatus = new LocadoraStatus();
                loadLocadoraStatus();
                GenericaMensagem.info("Sucesso", "Registro inserido");
            } else {
                GenericaMensagem.warn("Validação", "Registro já existe!");
            }
        } else {
            if (dao.update(locadoraStatus, true)) {
                LocadoraStatus ls = (LocadoraStatus) dao.find(locadoraStatus);
                String semena_id_before = "Nenhum";
                if (ls.getTaxa() != null) {
                    semena_id_before = "" + ls.getTaxa().getId();
                }
                String beforeUpdate
                        = "ID" + ls.getId()
                        + " - Filial: (" + ls.getFilial().getId() + ") "
                        + semena_id_before
                        + " - Taxa: (" + ls.getTaxa().getId() + ") "
                        + " - Data: " + ls.getDataString();
                novoLog.update(beforeUpdate,
                        "ID" + locadoraStatus.getId()
                        + " - Filial: (" + locadoraStatus.getFilial().getId() + ") "
                        + semena_id
                        + " - Taxa: (" + locadoraStatus.getTaxa().getId() + ") "
                        + " - Data: " + locadoraStatus.getDataString()
                );
                locadoraStatus = new LocadoraStatus();
                loadLocadoraStatus();
                GenericaMensagem.info("Sucesso", "Registro atualizado");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
    }

    public void edit(LocadoraStatus ls) {
        idFilial = ls.getFilial().getId();
        if (ls.getSemana() != null) {
            idSemana = ls.getSemana().getId();
        }
        idLocadoraTaxa = ls.getTaxa().getId();
        locadoraStatus = ls;
    }

    public void delete(LocadoraStatus ls) {
        if (new Dao().delete(ls, true)) {
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("loc_status");
            novoLog.setCodigo(ls.getId());
            String semena_id = "Nenhum";
            if (ls.getTaxa() != null) {
                semena_id = "" + ls.getTaxa().getId();
            }
            novoLog.delete(
                    "ID" + ls.getId()
                    + " - Filial: (" + ls.getFilial().getId() + ") "
                    + semena_id
                    + " - Taxa: (" + ls.getTaxa().getId() + ") "
                    + " - Data: " + ls.getDataString()
            );
            locadoraStatus = new LocadoraStatus();
            loadLocadoraStatus();
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void listener(Integer tcase) {
        if (tcase == 1) {
            loadFilial();
            loadLocadoraTaxa();
        }
    }

    public LocadoraStatus getLocadoraStatus() {
        return locadoraStatus;
    }

    public void setLocadoraStatus(LocadoraStatus locadoraStatus) {
        this.locadoraStatus = locadoraStatus;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public List<SelectItem> getListFilial() {
        return listFilial;
    }

    public void setListFilial(List<SelectItem> listFilial) {
        this.listFilial = listFilial;
    }

    public Integer getIdSemana() {
        return idSemana;
    }

    public void setIdSemana(Integer idSemana) {
        this.idSemana = idSemana;
    }

    public List<SelectItem> getListSemana() {
        return listSemana;
    }

    public void setListSemana(List<SelectItem> listSemana) {
        this.listSemana = listSemana;
    }

    public Integer getIdLocadoraTaxa() {
        return idLocadoraTaxa;
    }

    public void setIdLocadoraTaxa(Integer idLocadoraTaxa) {
        this.idLocadoraTaxa = idLocadoraTaxa;
    }

    public List<SelectItem> getListLocadoraTaxa() {
        return listLocadoraTaxa;
    }

    public void setListLocadoraTaxa(List<SelectItem> listLocadoraTaxa) {
        this.listLocadoraTaxa = listLocadoraTaxa;
    }

    public List<LocadoraStatus> getListLocadoraStatus() {
        return listLocadoraStatus;
    }

    public void setListLocadoraStatus(List<LocadoraStatus> listLocadoraStatus) {
        this.listLocadoraStatus = listLocadoraStatus;
    }

}
