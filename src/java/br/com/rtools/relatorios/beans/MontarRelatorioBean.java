package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.RelatorioFiltro;
import br.com.rtools.relatorios.RelatorioFiltroGrupo;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioFiltroDao;
import br.com.rtools.relatorios.dao.RelatorioFiltroGrupoDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.RotinaGrupo;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.seguranca.dao.RotinaGrupoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Sessions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.ReorderEvent;

@ManagedBean
@SessionScoped
public class MontarRelatorioBean implements Serializable {

    private List<SelectItem> listRotinas;
    private Integer idRotina;
    private List<SelectItem> listRelatorios;
    private Integer idRelatorio;
    private List<SelectItem> listGrupos;
    private Integer idGrupo;
    private Boolean showFilters;
    private RelatorioFiltroGrupo relatorioFiltroGrupo;
    private RelatorioFiltro relatorioFiltro;
    private List<RelatorioFiltroGrupo> listRelatorioFiltroGrupo;
    private List<RelatorioFiltro> listRelatorioFiltro;
    private Rotina rotina;

    public MontarRelatorioBean() {
        this.listRotinas = new ArrayList();
        this.idRotina = null;
        this.listRelatorios = new ArrayList();
        this.listGrupos = new ArrayList();
        this.idRelatorio = null;
        this.idGrupo = null;
        this.relatorioFiltroGrupo = new RelatorioFiltroGrupo();
        this.relatorioFiltro = new RelatorioFiltro();
        this.listRelatorioFiltroGrupo = new ArrayList();
        this.rotina = new Rotina();
        loadListRotinas();
    }

    public void save() {
        Dao dao = new Dao();
        if (idRotina == null) {
            Messages.warn("Validação", "Informar rotina!");
            return;
        }
        relatorioFiltroGrupo.setRotina((Rotina) dao.find(new Rotina(), idRotina));
        if (relatorioFiltroGrupo.getId() == null) {
            if (!dao.save(relatorioFiltroGrupo, true)) {
                Messages.warn("Erro", "Ao inserir registro!");
                return;
            }
            Messages.info("Sucesso", "Registro inserido");
        } else {
            if (!dao.update(relatorioFiltroGrupo, true)) {
                Messages.warn("Erro", "Ao atualizar registro!");
                return;
            }
            Messages.info("Sucesso", "Registro atualizado");
        }
        relatorioFiltroGrupo = new RelatorioFiltroGrupo();
        loadListRelatorioFiltroGrupo();
    }

    public void addFiltro() {
        Dao dao = new Dao();
        if (rotina.getId() == -1) {
            Messages.warn("Validação", "Informar rotina!");
            return;
        }
        if (idGrupo == null) {
            Messages.warn("Validação", "Informar grupo!");
            return;
        }
        relatorioFiltro.setRotina((Rotina) dao.find(new Rotina(), idRotina));
        relatorioFiltro.setRelatorioFiltroGrupo((RelatorioFiltroGrupo) dao.find(new RelatorioFiltroGrupo(), idGrupo));
        if (relatorioFiltro.getId() == null) {
            if (!dao.save(relatorioFiltro, true)) {
                Messages.warn("Erro", "Ao inserir registro!");
                return;
            }
            Messages.info("Sucesso", "Registro inserido");
        } else {
            if (!dao.update(relatorioFiltro, true)) {
                Messages.warn("Erro", "Ao atualizar registro!");
                return;
            }
            Messages.info("Sucesso", "Registro atualizado");
        }
        relatorioFiltro = new RelatorioFiltro();
        loadListRelatorioFiltro();
    }

    public void delete(RelatorioFiltroGrupo rfg) {
        if (!new Dao().delete(rfg, true)) {
            Messages.warn("Erro", "Ao remover registro!");
            return;
        }
        relatorioFiltroGrupo = new RelatorioFiltroGrupo();
        Messages.info("Sucesso", "Registro removido");
        loadListRelatorioFiltroGrupo();
    }

    public void deleteFiltro(RelatorioFiltro rf) {
        if (!new Dao().delete(rf, true)) {
            Messages.warn("Erro", "Ao remover registro!");
            return;
        }
        relatorioFiltro = new RelatorioFiltro();
        Messages.info("Sucesso", "Registro removido");
        loadListRelatorioFiltro();
    }

    public void closeRelatorioFiltroGrupo() {
        showFilters = false;
        relatorioFiltroGrupo = new RelatorioFiltroGrupo();
        rotina = new Rotina();
    }

    public void editRelatorioFiltroGrupo() {
        if (idRotina == null) {
            return;
        }
        loadListGrupos();
        rotina = (Rotina) new Dao().find(new Rotina(), idRotina);
        loadListRelatorioFiltro();
        showFilters = true;
        relatorioFiltroGrupo = new RelatorioFiltroGrupo();
        relatorioFiltro = new RelatorioFiltro();
    }

    public void editFiltro(RelatorioFiltro rf) {
        relatorioFiltro = rf;
        idGrupo = rf.getRelatorioFiltroGrupo().getId();
    }

    public void editRelatorioFiltroGrupo(RelatorioFiltroGrupo rfg) {
        if (idRotina == null) {
            return;
        }
        loadListGrupos();
        rotina = (Rotina) new Dao().find(new Rotina(), idRotina);
        loadListRelatorioFiltro();
        showFilters = true;
        idGrupo = rfg.getId();
        relatorioFiltroGrupo = rfg;
        relatorioFiltro = new RelatorioFiltro();
    }

    public void updateOrder() {

    }

    public final void loadListRotinas() {
        listRotinas = new ArrayList();
        List<RotinaGrupo> list = new RotinaGrupoDao().findByGrupo(new Rotina().get().getId());
        listRotinas.add(new SelectItem(null, "SELECIONAR"));
        idRotina = null;
        for (int i = 0; i < list.size(); i++) {
            listRotinas.add(new SelectItem(list.get(i).getRotina().getId(), list.get(i).getRotina().getRotina()));
        }
    }

    public final void loadListGrupos() {
        listGrupos = new ArrayList();
        idGrupo = null;
        listGrupos.add(new SelectItem(null, "SELECIONAR"));
        if (idRotina != null) {
            List<RelatorioFiltroGrupo> list = new RelatorioFiltroGrupoDao().findByRotina(idRotina);
            for (int i = 0; i < list.size(); i++) {
                listGrupos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }

        }
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "reload":
                relatorioFiltroGrupo = new RelatorioFiltroGrupo();
                loadListRelatorioFiltroGrupo();
                break;
            case "new":
                Sessions.remove("montarRelatorioBean");
                break;
            case "new_filtro":
                relatorioFiltro = new RelatorioFiltro();
                break;
            case "reload_grupo":
                loadListRelatorioFiltro();
                break;
            default:
                break;
        }
    }

    public void loadListRelatorios() {
        listRelatorios = new ArrayList();
        listRelatorios.add(new SelectItem(null, "SELECIONAR"));
        idRelatorio = null;
        if (idRotina != null) {
            List<Relatorios> list = new RelatorioDao().findByRotina(idRotina);
            for (int i = 0; i < list.size(); i++) {
                listRelatorios.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void loadListRelatorioFiltroGrupo() {
        listRelatorioFiltroGrupo = new ArrayList();
        if (idRotina != null) {
            listRelatorioFiltroGrupo = new RelatorioFiltroGrupoDao().findByRotina(idRotina);
        }
    }

    public void loadListRelatorioFiltro() {
        listRelatorioFiltro = new ArrayList();
        if (idGrupo == null) {
            listRelatorioFiltro = new RelatorioFiltroDao().findByRotina(idRotina);
        } else {
            listRelatorioFiltro = new RelatorioFiltroDao().findBy(idRotina, idGrupo);
        }
    }

    public List<SelectItem> getListRotinas() {
        return listRotinas;
    }

    public void setListRotinas(List<SelectItem> listRotinas) {
        this.listRotinas = listRotinas;
    }

    public Integer getIdRotina() {
        return idRotina;
    }

    public void setIdRotina(Integer idRotina) {
        this.idRotina = idRotina;
    }

    public RelatorioFiltroGrupo getRelatorioFiltroGrupo() {
        return relatorioFiltroGrupo;
    }

    public void setRelatorioFiltroGrupo(RelatorioFiltroGrupo relatorioFiltroGrupo) {
        this.relatorioFiltroGrupo = relatorioFiltroGrupo;
    }

    public List<RelatorioFiltroGrupo> getListRelatorioFiltroGrupo() {
        return listRelatorioFiltroGrupo;
    }

    public void setListRelatorioFiltroGrupo(List<RelatorioFiltroGrupo> listRelatorioFiltroGrupo) {
        this.listRelatorioFiltroGrupo = listRelatorioFiltroGrupo;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Boolean getShowFilters() {
        return showFilters;
    }

    public void setShowFilters(Boolean showFilters) {
        this.showFilters = showFilters;
    }

    public void onRowReorder(ReorderEvent event) {
        Dao dao = new Dao();
        for (int i = 0; i < listRelatorioFiltroGrupo.size(); i++) {
            listRelatorioFiltroGrupo.get(i).setNrOrdem(i);
            dao.update(listRelatorioFiltroGrupo.get(i), true);
        }
        Messages.info("Sucesso", "Registro reordenado");
        // FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowReorderFiltro(ReorderEvent event) {
        if(idGrupo == null) {
            return;
        }
        Dao dao = new Dao();
        for (int i = 0; i < listRelatorioFiltro.size(); i++) {
            listRelatorioFiltro.get(i).setOrdem(i);
            dao.update(listRelatorioFiltro.get(i), true);
        }
        Messages.info("Sucesso", "Registro reordenado");
        // FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<SelectItem> getListRelatorios() {
        return listRelatorios;
    }

    public void setListRelatorios(List<SelectItem> listRelatorios) {
        this.listRelatorios = listRelatorios;
    }

    public RelatorioFiltro getRelatorioFiltro() {
        return relatorioFiltro;
    }

    public void setRelatorioFiltro(RelatorioFiltro relatorioFiltro) {
        this.relatorioFiltro = relatorioFiltro;
    }

    public List<RelatorioFiltro> getListRelatorioFiltro() {
        return listRelatorioFiltro;
    }

    public void setListRelatorioFiltro(List<RelatorioFiltro> listRelatorioFiltro) {
        this.listRelatorioFiltro = listRelatorioFiltro;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public List<SelectItem> getListGrupos() {
        return listGrupos;
    }

    public void setListGrupos(List<SelectItem> listGrupos) {
        this.listGrupos = listGrupos;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

}
