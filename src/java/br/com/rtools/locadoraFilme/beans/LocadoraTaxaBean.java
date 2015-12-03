package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.locadoraFilme.LocadoraTaxa;
import br.com.rtools.locadoraFilme.dao.LocadoraTaxaDao;
import br.com.rtools.logSistema.NovoLog;
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
public class LocadoraTaxaBean implements Serializable {

    private LocadoraTaxa locadoraTaxa;

    private Integer idServicoDiaria;
    private List<SelectItem> listServicoDiaria;

    private Integer idServicoMultaDiaria;
    private List<SelectItem> listServicoMultaDiaria;

    private List<LocadoraTaxa> listLocadoraTaxa;

    @PostConstruct
    public void init() {
        locadoraTaxa = new LocadoraTaxa();
        idServicoDiaria = null;
        idServicoMultaDiaria = null;
        listServicoDiaria = new ArrayList<>();
        listServicoMultaDiaria = new ArrayList<>();
        loadServicoDiaria();
        loadServicoMultaDiaria();
        listLocadoraTaxa = new ArrayList<>();
        loadServicoMultaDiaria();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("locadoraTaxaBean");
    }

    public void loadServicoDiaria() {
        ServicosDao servicosDao = new ServicosDao();
        servicosDao.setSituacao("A");
        List<Servicos> list = servicosDao.findAll();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServicoDiaria = list.get(i).getId();
            }
            listServicoDiaria.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadServicoMultaDiaria() {
        listServicoMultaDiaria.clear();
        List<Servicos> list = new ServicosDao().findNotInByTabela("loc_taxa", "id_servico_multa_diaria", "id_servico_diaria", Integer.toString(idServicoDiaria), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServicoMultaDiaria = list.get(i).getId();
            }
            listServicoMultaDiaria.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadLocadoraTaxa() {
        listLocadoraTaxa.clear();
        listLocadoraTaxa = new LocadoraTaxaDao().findAllByServicoDiaria(idServicoDiaria);
    }

    public void save() {
        Dao dao = new Dao();
        if (idServicoDiaria == null) {
            GenericaMensagem.warn("Erro", "Informar serviço diária!");
            return;
        }
        if (idServicoMultaDiaria == null) {
            GenericaMensagem.warn("Erro", "Informar serviço multa!");
            return;
        }
        locadoraTaxa.setServicoDiaria((Servicos) dao.find(new Servicos(), idServicoDiaria));
        locadoraTaxa.setServicoMultaDiaria((Servicos) dao.find(new Servicos(), idServicoMultaDiaria));
        if (new Dao().save(locadoraTaxa, true)) {
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("loc_taxa");
            novoLog.setCodigo(locadoraTaxa.getId());
            novoLog.save(
                    "ID" + locadoraTaxa.getId()
                    + " - Serviço diária: (" + locadoraTaxa.getServicoDiaria().getId() + ") " + locadoraTaxa.getServicoDiaria().getDescricao()
                    + " - Serviço multa diária: (" + locadoraTaxa.getServicoMultaDiaria().getId() + ") " + locadoraTaxa.getServicoMultaDiaria().getDescricao()
            );
            locadoraTaxa = new LocadoraTaxa();
            listener(1);
            GenericaMensagem.info("Sucesso", "Registro inserido");
        } else {
            GenericaMensagem.warn("Erro", "Ao adicionar registro!");
        }
    }

    public void delete(LocadoraTaxa lt) {
        if (new Dao().delete(lt, true)) {
            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("loc_taxa");
            novoLog.setCodigo(lt.getId());
            novoLog.delete(
                    "ID" + lt.getId()
                    + " - Serviço diária: (" + lt.getServicoDiaria().getId() + ") " + lt.getServicoDiaria().getDescricao()
                    + " - Serviço multa diária: (" + lt.getServicoMultaDiaria().getId() + ") " + lt.getServicoMultaDiaria().getDescricao()
            );
            locadoraTaxa = new LocadoraTaxa();
            listener(1);
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void listener(Integer tcase) {
        if (tcase == 1) {
            loadServicoMultaDiaria();
            loadLocadoraTaxa();
        }
    }

    public LocadoraTaxa getLocadoraTaxa() {
        return locadoraTaxa;
    }

    public void setLocadoraTaxa(LocadoraTaxa locadoraTaxa) {
        this.locadoraTaxa = locadoraTaxa;
    }

    public Integer getIdServicoDiaria() {
        return idServicoDiaria;
    }

    public void setIdServicoDiaria(Integer idServicoDiaria) {
        this.idServicoDiaria = idServicoDiaria;
    }

    public List<SelectItem> getListServicoDiaria() {
        return listServicoDiaria;
    }

    public void setListServicoDiaria(List<SelectItem> listServicoDiaria) {
        this.listServicoDiaria = listServicoDiaria;
    }

    public Integer getIdServicoMultaDiaria() {
        return idServicoMultaDiaria;
    }

    public void setIdServicoMultaDiaria(Integer idServicoMultaDiaria) {
        this.idServicoMultaDiaria = idServicoMultaDiaria;
    }

    public List<SelectItem> getListServicoMultaDiaria() {
        return listServicoMultaDiaria;
    }

    public void setListServicoMultaDiaria(List<SelectItem> listServicoMultaDiaria) {
        this.listServicoMultaDiaria = listServicoMultaDiaria;
    }

    public List<LocadoraTaxa> getListLocadoraTaxa() {
        return listLocadoraTaxa;
    }

    public void setListLocadoraTaxa(List<LocadoraTaxa> listLocadoraTaxa) {
        this.listLocadoraTaxa = listLocadoraTaxa;
    }

}
