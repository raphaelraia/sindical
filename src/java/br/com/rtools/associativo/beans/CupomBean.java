package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.Cupom;
import br.com.rtools.associativo.CupomCategoria;
import br.com.rtools.associativo.dao.CupomCategoriaDao;
import br.com.rtools.associativo.dao.CupomDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.SelectItemSort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class CupomBean {

    private Cupom cupom;
    private List<Cupom> listCupom;
    private List<CupomCategoria> listCupomCategoria;
    private Boolean historico;
    private Integer idCategoria;
    private List<SelectItem> listCategoria;

    @PostConstruct
    public void init() {
        cupom = new Cupom();
        listCupom = new ArrayList();
        listCategoria = new ArrayList();
        listCupomCategoria = new ArrayList();
        idCategoria = null;
        historico = false;
        loadListCupom();
        loadListCupomCategoria();
        loadListCategoria();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("cupomBean");
    }

    public void loadListCupom() {
        listCupom = new ArrayList<>();
        CupomDao cupomDao = new CupomDao();
        listCupom = cupomDao.findByHistorico(this.historico);
    }

    public void loadListCategoria() {
        listCategoria = new ArrayList<>();
        List<Categoria> list = new Dao().list(new Categoria(), true);
        int b = 0;
        if (!listCupomCategoria.isEmpty()) {
            for (int x = 0; x < listCupomCategoria.size(); x++) {
                for (int i = 0; i < list.size(); i++) {
                    if (listCupomCategoria.get(x).getCategoria().getId().equals(list.get(i).getId())) {
                        list.remove(i);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (b == 0) {
                idCategoria = list.get(i).getId();
                b++;
            }
            listCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getCategoria()));
        }
        SelectItemSort.sort(listCategoria);
    }

    public void loadListCupomCategoria() {
        listCupomCategoria = new ArrayList<>();
        CupomCategoriaDao cupomCategoriaDao = new CupomCategoriaDao();
        listCupomCategoria = cupomCategoriaDao.find(cupom.getId());
    }

    public void add() {
        Dao dao = new Dao();
        CupomCategoria cupomCategoria = new CupomCategoria();
        cupomCategoria.setCategoria((Categoria) dao.find(new Categoria(), idCategoria));
        if (validaCupomCategoria(cupomCategoria)) {
            GenericaMensagem.warn("Validação", "Categoria já existe!");
            return;
        }
        listCupomCategoria.add(cupomCategoria);
        if (cupom.getId() != null) {
            cupomCategoria.setCupom(cupom);
            saveCupomCategoria();
            loadListCupomCategoria();
        }
        GenericaMensagem.info("Sucesso", "Categoria adicionada!");
        loadListCategoria();
    }

    public Boolean validaCupomCategoria(CupomCategoria cc) {
        for (int i = 0; i < listCupomCategoria.size(); i++) {
            if (Objects.equals(listCupomCategoria.get(i).getCategoria().getId(), cc.getCategoria().getId())) {
                listCupomCategoria.remove(cc);
                return true;
            }
        }
        return false;
    }

    public void saveCupomCategoria() {
        for (int i = 0; i < listCupomCategoria.size(); i++) {
            if (listCupomCategoria.get(i).getId() == null) {
                if (listCupomCategoria.get(i).getCupom().getId() == null) {
                    listCupomCategoria.get(i).setCupom(cupom);
                }
                new Dao().save(listCupomCategoria.get(i), true);
            }
        }
    }

    public void remove(CupomCategoria cc) {
        if (cc.getId() == null) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            listCupomCategoria.remove(cc);
            loadListCategoria();
            return;
        }
        if (new Dao().delete(cc, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            loadListCupomCategoria();
            loadListCategoria();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void edit(Cupom c) {
        cupom = (Cupom) new Dao().rebind(c);
        loadListCupomCategoria();
        loadListCategoria();
        listCupom.remove(c);
    }

    public void save() {
        if (cupom.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar o nome do cupom!");
            return;
        }
        if (cupom.getData().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar a data do cupom!");
            return;
        }
        String beforeUpdate = "";
        if (cupom.getId() != null) {
            Cupom c = (Cupom) new Dao().find(cupom);
            beforeUpdate = "Cupom: (" + c.getId() + ") " + c.getDescricao() + " - Data: " + c.getData() + " - Carência Inadimplencia Dias: " + c.getCarenciaInadimplenciaDias();
        }
        if (cupom.getId() == null) {
            if (new Dao().save(cupom, true)) {
                saveCupomCategoria();
                GenericaMensagem.info("Sucesso", "Registro inserido");
                loadListCupom();
                NovoLog novoLog = new NovoLog();
                novoLog.save("Cupom: (" + cupom.getId() + ") " + cupom.getDescricao() + " - Data: " + cupom.getData() + " - Carência Inadimplencia Dias: " + cupom.getCarenciaInadimplenciaDias());
            } else {
                GenericaMensagem.warn("Erro", "Registro já existe!");
            }
        } else if (new Dao().update(cupom, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
            loadListCupom();
            NovoLog novoLog = new NovoLog();
            novoLog.update(beforeUpdate, "Cupom: (" + cupom.getId() + ") " + cupom.getDescricao() + " - Data: " + cupom.getData() + " - Carência Inadimplencia Dias: " + cupom.getCarenciaInadimplenciaDias());
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void delete() {
        if (new Dao().delete(cupom, true)) {
            GenericaMensagem.warn("Sucesso", "Registro removido!");
            NovoLog novoLog = new NovoLog();
            novoLog.delete("Cupom: (" + cupom.getId() + ") " + cupom.getDescricao() + " - Data: " + cupom.getData() + " - Carência Inadimplencia Dias: " + cupom.getCarenciaInadimplenciaDias());
            loadListCupom();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public List<Cupom> getListCupom() {
        return listCupom;
    }

    public void setListCupom(List<Cupom> listCupom) {
        this.listCupom = listCupom;
    }

    public List<CupomCategoria> getListCupomCategoria() {
        return listCupomCategoria;
    }

    public void setListCupomCategoria(List<CupomCategoria> listCupomCategoria) {
        this.listCupomCategoria = listCupomCategoria;
    }

    public Boolean getHistorico() {
        return historico;
    }

    public void setHistorico(Boolean historico) {
        this.historico = historico;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public List<SelectItem> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<SelectItem> listCategoria) {
        this.listCategoria = listCategoria;
    }

}
