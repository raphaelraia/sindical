package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import br.com.rtools.associativo.dao.ModeloCarteirinhaCategoriaDao;
import br.com.rtools.associativo.db.SocioCarteirinhaDBToplink;
import br.com.rtools.associativo.lista.ListModeloCarterinhaCategoria;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ModeloCarteirinhaBean {

    private ModeloCarteirinha modeloCarteirinha;
    private Map<Integer, Integer> categoria_id;
    private Map<Integer, Integer> rotina_id;
    private List<ModeloCarteirinha> listModeloCarteirinha;
    private List<ModeloCarteirinhaCategoria> listModeloCarteirinhaCategoria;
    private List<ListModeloCarterinhaCategoria> listGeneric;

    @PostConstruct
    public void init() {
        modeloCarteirinha = new ModeloCarteirinha();
        categoria_id = new HashMap<>();
        rotina_id = new HashMap<>();
        listModeloCarteirinha = new ArrayList();
        listModeloCarteirinhaCategoria = new ArrayList();
        listGeneric = new ArrayList();
        loadListModeloCarteirinha();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("modeloCarteirinhaBean");
    }

    public void save() {
        if (modeloCarteirinha.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Erro", "Digite um nome para o Modelo!");
            return;
        }
        if (modeloCarteirinha.getJasper().isEmpty()) {
            GenericaMensagem.warn("Erro", "Digite o caminho do Jasper!");
            return;
        }
        if (modeloCarteirinha.getId() == -1) {
            if (new Dao().save(modeloCarteirinha, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                loadListModeloCarteirinha();
                loadListModeloCarteirinhaCategoria();
            } else {
                GenericaMensagem.warn("Erro", "Ao adicionar registro!");
            }
        } else {
            if (new Dao().update(modeloCarteirinha, true)) {
                GenericaMensagem.info("Sucesso", "Registro atualizado");
                loadListModeloCarteirinha();
                loadListModeloCarteirinhaCategoria();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
    }

    public void saveModeloCarteirinhaCategoria() {
        saveModeloCarteirinhaCategoria(modeloCarteirinha);
    }

    public void saveModeloCarteirinhaCategoria(ModeloCarteirinha mc) {
        Dao dao = new Dao();
        ModeloCarteirinhaCategoria mcc = new ModeloCarteirinhaCategoria();
        mcc.setModeloCarteirinha(mc);
        Integer mc_id = null;
        Integer pos = null;
        for (int i = 0; i < listGeneric.size(); i++) {
            if (listGeneric.get(i).getModelo_categoria_id() == mc.getId()) {
                if (listGeneric.get(i).getRotina_id() != null) {
                    mcc.setRotina((Rotina) dao.find(new Rotina(), listGeneric.get(i).getRotina_id()));
                }
                if (listGeneric.get(i).getCategoria_id() != null) {
                    mcc.setCategoria((Categoria) dao.find(new Categoria(), listGeneric.get(i).getCategoria_id()));
                }
                mc_id = listGeneric.get(i).getModelo_categoria_id();
                pos = i;
                break;
            }
        }
        ModeloCarteirinhaCategoria exists = new SocioCarteirinhaDBToplink().pesquisaModeloCarteirinhaCategoria(mc.getId(), (mcc.getCategoria() == null) ? -1 : mcc.getCategoria().getId(), mcc.getRotina().getId());
        if (exists != null) {
            GenericaMensagem.warn("Validação", "Modelo já existe!");
            return;
        }
        if (new Dao().save(mcc, true)) {
            GenericaMensagem.info("Sucesso", "Registro adicionado");
            List<ModeloCarteirinhaCategoria> list = loadListModeloCarteirinhaCategoria(mc_id);
            if (!list.isEmpty()) {
                listGeneric.get(pos).setListMCC(new ArrayList());
                listGeneric.get(pos).setListMCC(list);
                listGeneric.get(pos).setListCategoria(new ArrayList());
                listGeneric.get(pos).setListCategoria(listCategoria(mc_id, mcc.getRotina().getId()));
            }
        } else {
            GenericaMensagem.warn("Erro", "Ao adicionar registro!");
        }
    }

    public void clear() {
        GenericaSessao.remove("modeloCarteirinhaBean");
    }

    public void deleteModeloCarteirinha(ModeloCarteirinha mc) {
        Dao dao = new Dao();
        for (int i = 0; i < listModeloCarteirinhaCategoria.size(); i++) {
            new Dao().delete(listModeloCarteirinhaCategoria.get(i), true);
        }
        if (!new Dao().delete(mc, true)) {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
            dao.rollback();
            return;
        }
        loadListModeloCarteirinha();
        Integer mc_id = null;
        Integer pos = null;
        for (int i = 0; i < listGeneric.size(); i++) {
            if (listGeneric.get(i).getModelo_categoria_id() == mc.getId()) {
                mc_id = listGeneric.get(i).getModelo_categoria_id();
                pos = i;
                break;
            }
        }
        List<ModeloCarteirinhaCategoria> list = loadListModeloCarteirinhaCategoria(mc_id);
        if (!list.isEmpty()) {
            listGeneric.get(pos).setListMCC(new ArrayList());
            listGeneric.get(pos).setListMCC(list);
        }
        GenericaMensagem.info("Sucesso", "Registro excluído!");
    }

    public void deleteModeloCarteirinhaCategoria(ModeloCarteirinhaCategoria mcc) {
        if (!new Dao().delete(mcc, true)) {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
            return;
        }
        Integer mc_id = null;
        Integer pos = null;
        for (int i = 0; i < listGeneric.size(); i++) {
            if (listGeneric.get(i).getModelo_categoria_id() == mcc.getModeloCarteirinha().getId()) {
                mc_id = listGeneric.get(i).getModelo_categoria_id();
                pos = i;
                break;
            }
        }
        List<ModeloCarteirinhaCategoria> list = loadListModeloCarteirinhaCategoria(mc_id);
        if (!list.isEmpty()) {
            listGeneric.get(pos).setListMCC(new ArrayList());
            listGeneric.get(pos).setListMCC(list);
        } else {
            listGeneric.get(pos).setListMCC(new ArrayList());
        }
        listGeneric.get(pos).setListCategoria(new ArrayList());
        listGeneric.get(pos).setListCategoria(listCategoria(mc_id, mcc.getRotina().getId()));
        GenericaMensagem.info("Sucesso", "Registro excluído!");
    }

    public void edit(ModeloCarteirinha mc) {
        modeloCarteirinha = (ModeloCarteirinha) new Dao().rebind(mc);
        loadListModeloCarteirinhaCategoria();
    }

    public ModeloCarteirinha getModeloCarteirinha() {
        return modeloCarteirinha;
    }

    public void setModeloCarteirinha(ModeloCarteirinha modeloCarteirinha) {
        this.modeloCarteirinha = modeloCarteirinha;
    }

    public List<SelectItem> listCategoria(Integer modelo_carteirinha_id, Integer rotina_id) {
        List<SelectItem> listCategoria = new ArrayList();
        List<Categoria> list = new ModeloCarteirinhaCategoriaDao().findNotInCategoriaByMCC(modelo_carteirinha_id, rotina_id);
        if (!list.isEmpty()) {
            listCategoria.add(new SelectItem(null, "Sem Categoria"));
            for (int i = 1; i < list.size(); i++) {
                listCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getCategoria()));
            }
        } else {
            listCategoria.add(new SelectItem(0, "Nenhuma Categoria encontrada", "0"));
        }
        return listCategoria;
    }

    public List<SelectItem> listRotina(Integer modelo_carteirinha_id) {
        List<SelectItem> listRotina = new ArrayList();
        List<Rotina> list = new ArrayList<>();
        list.add((Rotina) new Dao().find(new Rotina(), 170));
        list.add((Rotina) new Dao().find(new Rotina(), 122));
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                listRotina.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
            }
        } else {
            listRotina.add(new SelectItem(0, "Nenhuma Rotina encontrada", "0"));
        }
        return listRotina;
    }

    public void loadListModeloCarteirinha() {
        listModeloCarteirinha = new ArrayList();
        listModeloCarteirinha = new Dao().list(new ModeloCarteirinha());
        for (int i = 0; i < listModeloCarteirinha.size(); i++) {
            ListModeloCarterinhaCategoria generic = new ListModeloCarterinhaCategoria();
            generic.setListRotina(listRotina(listModeloCarteirinha.get(i).getId()));
            for (int x = 0; x < generic.getListRotina().size(); i++) {
                generic.setRotina_id((Integer) generic.getListRotina().get(x).getValue());
                break;
            }
            generic.setListCategoria(listRotina(listModeloCarteirinha.get(i).getId()));
            for (int x = 0; x < generic.getListCategoria().size(); i++) {
                generic.setCategoria_id((Integer) generic.getListCategoria().get(x).getValue());
                break;
            }
            generic.setModelo_categoria_id(listModeloCarteirinha.get(i).getId());
            generic.setListCategoria(listCategoria(listModeloCarteirinha.get(i).getId(), generic.getRotina_id()));
            generic.setListMCC(loadListModeloCarteirinhaCategoria(listModeloCarteirinha.get(i).getId()));
            listGeneric.add(generic);
        }
    }

    public void loadListModeloCarteirinhaCategoria() {
        listModeloCarteirinhaCategoria = loadListModeloCarteirinhaCategoria(modeloCarteirinha.getId());
    }

    public List loadListModeloCarteirinhaCategoria(Integer modelo_carteirinha_id) {
        return new ModeloCarteirinhaCategoriaDao().findByModeloCarteirinha(modelo_carteirinha_id);

    }

    public Integer categoria_id(Integer modelo_carteirinha_id) {
        try {
            Integer c_id = null;
            for (Map.Entry<Integer, Integer> entry : categoria_id.entrySet()) {
                if (Objects.equals(modelo_carteirinha_id, entry.getKey())) {
                    c_id = entry.getValue();
                }
            }
            return c_id;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer rotina_id(Integer modelo_carteirinha_id) {
        try {
            Integer c_id = null;
            for (Map.Entry<Integer, Integer> entry : rotina_id.entrySet()) {
                if (Objects.equals(modelo_carteirinha_id, entry.getKey())) {
                    c_id = entry.getValue();
                }
            }
            return c_id;
        } catch (Exception e) {
            return null;
        }
    }

    public List<ModeloCarteirinha> getListModeloCarteirinha() {
        return listModeloCarteirinha;
    }

    public void setListModeloCarteirinha(List<ModeloCarteirinha> listModeloCarteirinha) {
        this.listModeloCarteirinha = listModeloCarteirinha;
    }

    public List<ModeloCarteirinhaCategoria> getListModeloCarteirinhaCategoria() {
        return listModeloCarteirinhaCategoria;
    }

    public void setListModeloCarteirinhaCategoria(List<ModeloCarteirinhaCategoria> listModeloCarteirinhaCategoria) {
        this.listModeloCarteirinhaCategoria = listModeloCarteirinhaCategoria;
    }

    /**
     * Lista ModeloCarteirinhaCategoria
     *
     * @param modelo_carteirinha_id
     * @return
     */
    public List<ModeloCarteirinhaCategoria> listMCC(Integer modelo_carteirinha_id) {
        return loadListModeloCarteirinhaCategoria(modelo_carteirinha_id);
    }

    public List<ListModeloCarterinhaCategoria> getListGeneric() {
        return listGeneric;
    }

    public void setListGeneric(List<ListModeloCarterinhaCategoria> listGeneric) {
        this.listGeneric = listGeneric;
    }
}
