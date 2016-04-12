package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.CentroCustoContabil;
import br.com.rtools.financeiro.CentroCustoContabilSub;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.AnaliseString;
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
public class CentroCustoSubcontabilBean {

    private CentroCustoContabilSub centroCustoContabilSub;
    private List<CentroCustoContabilSub> listCentroCustoContabilSub;
    private List<SelectItem> listCentroCustoContabil;
    private Integer idCentroCustoContabil;

    @PostConstruct
    public void init() {
        centroCustoContabilSub = new CentroCustoContabilSub();
        loadListCentroCustoContabil();
        loadListCentroCustoContabilSub();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("centroCustoSubcontabilBean");
    }

    public void loadListCentroCustoContabil() {
        listCentroCustoContabil = new ArrayList();
        List<CentroCustoContabil> list = new Dao().list(new CentroCustoContabil(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCentroCustoContabil = list.get(i).getId();
            }
            listCentroCustoContabil.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao() + " - Código: " + list.get(i).getCodigo()));
        }
    }

    public void loadListCentroCustoContabilSub() {
        if(!listCentroCustoContabil.isEmpty() && idCentroCustoContabil != null) {
            listCentroCustoContabilSub = new ArrayList();
            listCentroCustoContabilSub = new Dao().listQuery(new CentroCustoContabilSub(), "findByCCC", new Object[]{idCentroCustoContabil});            
        }
    }
    
    public void listener() {
        loadListCentroCustoContabil();
        loadListCentroCustoContabilSub() ;
    }

    public void save() {
        NovoLog novoLog = new NovoLog();
        Dao dao = new Dao();
        if (listCentroCustoContabil.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR CENTRO DE CUSTO CONTÁBIL!");
            return;
        }
        centroCustoContabilSub.setCentroCustoContabil((CentroCustoContabil) dao.find(new CentroCustoContabil(), idCentroCustoContabil));
        if (centroCustoContabilSub.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR DESCRIÇÃO!");
            return;
        }
        if (centroCustoContabilSub.getCodigo() < 0) {
            GenericaMensagem.warn("Validação", "INFORMAR CÓDIGO!");
            return;
        }
        if (centroCustoContabilSub.getId() == -1) {
            for (int i = 0; i < listCentroCustoContabilSub.size(); i++) {
                if (AnaliseString.removerAcentos(centroCustoContabilSub.getDescricao().toUpperCase().trim()).equals(AnaliseString.removerAcentos(listCentroCustoContabilSub.get(i).getDescricao().toUpperCase().trim()))) {
                    GenericaMensagem.warn("Validação", "DESCRIÇÃO JÁ EXISTE!");
                    return;
                }
                if (centroCustoContabilSub.getCodigo().equals(listCentroCustoContabilSub.get(i).getCodigo())) {
                    GenericaMensagem.warn("Validação", "CÓDIGO JÁ EXISTE!");
                    return;
                }
            }
            if (dao.save(centroCustoContabilSub, true)) {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                loadListCentroCustoContabilSub();
                novoLog.save("ID: " + centroCustoContabilSub.getId() + " - Descrição: " + centroCustoContabilSub.getDescricao() + " - Código: " + centroCustoContabilSub.getCodigo());
            } else {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
            }
        } else {
            CentroCustoContabilSub cc = (CentroCustoContabilSub) dao.find(centroCustoContabilSub);
            String beforeUpdate = "ID: " + cc.getId() + " - Descrição: " + cc.getDescricao() + " - Código: " + cc.getCodigo();
            if (dao.update(centroCustoContabilSub, true)) {
                GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
                loadListCentroCustoContabilSub();
                novoLog.update(beforeUpdate, "ID: " + centroCustoContabilSub.getId() + " - Descrição: " + centroCustoContabilSub.getDescricao() + " - Código: " + centroCustoContabilSub.getCodigo());
            } else {
                GenericaMensagem.warn("Erro", "AO ATUALZIAR REGISTRO!");
            }
        }
    }

    public void remove(CentroCustoContabilSub cccs) {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (dao.delete(cccs, true)) {
            GenericaMensagem.info("Sucesso", "REGISTRO REMODIVO");
            novoLog.delete("ID: " + cccs.getId() + " - Descrição: " + cccs.getDescricao() + " - Código: " + cccs.getCodigo());
            GenericaSessao.remove("centroCustoSubcontabilBean");
            loadListCentroCustoContabilSub();
        } else {
            GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO!");
        }
    }

    public void edit(CentroCustoContabilSub cccs) {
        centroCustoContabilSub = (CentroCustoContabilSub) new Dao().rebind(cccs);
        idCentroCustoContabil = cccs.getCentroCustoContabil().getId();
    }

    public CentroCustoContabilSub getCentroCustoContabilSub() {
        return centroCustoContabilSub;
    }

    public void setCentroCustoContabilSub(CentroCustoContabilSub centroCustoContabilSub) {
        this.centroCustoContabilSub = centroCustoContabilSub;
    }

    public List<CentroCustoContabilSub> getListCentroCustoContabilSub() {
        return listCentroCustoContabilSub;
    }

    public void setListCentroCustoContabilSub(List<CentroCustoContabilSub> listCentroCustoContabilSub) {
        this.listCentroCustoContabilSub = listCentroCustoContabilSub;
    }

    public List<SelectItem> getListCentroCustoContabil() {
        return listCentroCustoContabil;
    }

    public void setListCentroCustoContabil(List<SelectItem> listCentroCustoContabil) {
        this.listCentroCustoContabil = listCentroCustoContabil;
    }

    public Integer getIdCentroCustoContabil() {
        return idCentroCustoContabil;
    }

    public void setIdCentroCustoContabil(Integer idCentroCustoContabil) {
        this.idCentroCustoContabil = idCentroCustoContabil;
    }

}
