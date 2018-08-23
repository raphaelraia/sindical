package br.com.rtools.estoque.beans;

import br.com.rtools.estoque.ProdutoGrupo;
import br.com.rtools.estoque.ProdutoSubGrupo;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ProdutoSubGrupoBean {

    private List<SelectItem> listProdutoGrupos;
    private Integer idProdutoGrupo;
    private List<ProdutoSubGrupo> listProdutoSubGrupo;
    private ProdutoSubGrupo produtoSubGrupo;

    @PostConstruct
    public void init() {
        idProdutoGrupo = null;
        produtoSubGrupo = new ProdutoSubGrupo();
        listProdutoGrupos = new ArrayList<>();
        listProdutoSubGrupo = new ArrayList<>();
        loadListProdutoGrupos();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("produtoSubGrupoBean");
    }

    public void save() {
        for (ProdutoSubGrupo psg : listProdutoSubGrupo) {
            if (psg.getDescricao().equals(produtoSubGrupo.getDescricao())) {
                GenericaMensagem.warn("Validação", "Registro já existe!");
                return;
            }
        }
        if (produtoSubGrupo.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar descrição!");
            return;
        }
        if (idProdutoGrupo == null) {
            GenericaMensagem.warn("Validação", "Selecionar um grupo!");
            return;
        }
        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        produtoSubGrupo.setProdutoGrupo((ProdutoGrupo) di.find(new ProdutoGrupo(), idProdutoGrupo));
        if (produtoSubGrupo.getId() == null) {
            if (di.save(produtoSubGrupo, true)) {
                novoLog.save(produtoSubGrupo, true);
                GenericaMensagem.info("Sucesso", "Registro adicionado com sucesso");
            } else {
                GenericaMensagem.warn("Erro", "Ao adicionar registro!");
            }
        } else {
            ProdutoSubGrupo psgBefore = (ProdutoSubGrupo) di.find(produtoSubGrupo);
            if (di.update(produtoSubGrupo, true)) {
                novoLog.update(psgBefore, produtoSubGrupo, true);
                GenericaMensagem.info("Sucesso", "Registro atualizado com sucesso");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
        produtoSubGrupo = new ProdutoSubGrupo();
        listProdutoSubGrupo.clear();
    }

    public void edit(ProdutoSubGrupo psg) {
        Dao di = new Dao();
        produtoSubGrupo = (ProdutoSubGrupo) di.rebind(psg);
    }

    public void delete(ProdutoSubGrupo psg) {
        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        if (di.delete(psg, true)) {
            novoLog.delete(psg, true);
            GenericaMensagem.info("Sucesso", "Registro removido com sucesso");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
        produtoSubGrupo = new ProdutoSubGrupo();
        listProdutoSubGrupo.clear();
    }

    public List<ProdutoSubGrupo> getListProdutoSubGrupo() {
        if (listProdutoSubGrupo.isEmpty()) {
            Dao di = new Dao();
            if (idProdutoGrupo != null) {
                listProdutoSubGrupo = (List<ProdutoSubGrupo>) di.listQuery(new ProdutoSubGrupo(), "findGrupo", new Object[]{idProdutoGrupo});
            }
        }
        return listProdutoSubGrupo;
    }

    public void setListProdutoSubGrupo(List<ProdutoSubGrupo> listProdutoSubGrupo) {
        this.listProdutoSubGrupo = listProdutoSubGrupo;
    }

    public List<SelectItem> getListProdutoGrupos() {
        return listProdutoGrupos;
    }

    public void setListProdutoGrupos(List<SelectItem> listProdutoGrupos) {
        this.listProdutoGrupos = listProdutoGrupos;
    }

    public void loadListProdutoGrupos() {
        Dao di = new Dao();
        List<ProdutoGrupo> list = (List<ProdutoGrupo>) di.list(new ProdutoGrupo(), true);
        listProdutoGrupos = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idProdutoGrupo = list.get(i).getId();
            }
            listProdutoGrupos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public ProdutoSubGrupo getProdutoSubGrupo() {
        return produtoSubGrupo;
    }

    public void setProdutoSubGrupo(ProdutoSubGrupo produtoSubGrupo) {
        this.produtoSubGrupo = produtoSubGrupo;
    }

    public Integer getIdProdutoGrupo() {
        return idProdutoGrupo;
    }

    public void setIdProdutoGrupo(Integer idProdutoGrupo) {
        this.idProdutoGrupo = idProdutoGrupo;
    }

}
