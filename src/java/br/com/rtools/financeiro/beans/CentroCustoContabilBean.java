package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.CentroCustoContabil;
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

@ManagedBean
@SessionScoped
public class CentroCustoContabilBean {

    private CentroCustoContabil centroCustoContabil;
    private List<CentroCustoContabil> listCentroCustoContabil;

    @PostConstruct
    public void init() {
        centroCustoContabil = new CentroCustoContabil();
        loadListCentroCustoContabil();
    }
    

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("centroCustoContabilBean");
    }
    
    public void loadListCentroCustoContabil() {
        listCentroCustoContabil = new ArrayList();
        listCentroCustoContabil = new Dao().list(new CentroCustoContabil(), true);
    }

    public void save() {
        NovoLog novoLog = new NovoLog();
        Dao dao = new Dao();
        if(centroCustoContabil.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR DESCRIÇÃO!");
            return;
        }
        centroCustoContabil.setDescricao(centroCustoContabil.getDescricao().trim());
        if(centroCustoContabil.getCodigo() <= 0) {
            GenericaMensagem.warn("Validação", "INFORMAR CÓDIGO!");
            return;
        }
        if (centroCustoContabil.getId() == -1) {
            for(int i = 0; i < listCentroCustoContabil.size(); i++) {
                if(AnaliseString.removerAcentos(centroCustoContabil.getDescricao().toUpperCase().trim()).equals(AnaliseString.removerAcentos(listCentroCustoContabil.get(i).getDescricao().toUpperCase().trim()))) {
                    GenericaMensagem.warn("Validação", "DESCRIÇÃO JÁ EXISTE!");
                    return;
                }
                if(centroCustoContabil.getCodigo().equals(listCentroCustoContabil.get(i).getCodigo())) {
                    GenericaMensagem.warn("Validação", "CÓDIGO JÁ EXISTE!");
                    return;
                }
            }
            if (dao.save(centroCustoContabil, true)) {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                loadListCentroCustoContabil();
                novoLog.save("ID: " + centroCustoContabil.getId() + " - Descrição: " + centroCustoContabil.getDescricao() + " - Código: " + centroCustoContabil.getCodigo());
            } else {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
            }
        } else {
            CentroCustoContabil cc = (CentroCustoContabil) dao.find(centroCustoContabil);
            String beforeUpdate = "ID: " + cc.getId() + " - Descrição: " + cc.getDescricao() + " - Código: " + cc.getCodigo();
            if (dao.update(centroCustoContabil, true)) {
                GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
                loadListCentroCustoContabil();
                novoLog.update(beforeUpdate, "ID: " + centroCustoContabil.getId() + " - Descrição: " + centroCustoContabil.getDescricao() + " - Código: " + centroCustoContabil.getCodigo());
            } else {
                GenericaMensagem.warn("Erro", "AO ATUALZIAR REGISTRO!");
            }
        }
    }

    public void remove(CentroCustoContabil ccc) {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (dao.delete(ccc, true)) {
            GenericaMensagem.info("Sucesso", "REGISTRO REMODIVO");
            novoLog.delete("ID: " + ccc.getId() + " - Descrição: " + ccc.getDescricao() + " - Código: " + ccc.getCodigo());
            GenericaSessao.remove("centroCustoContabilBean");
            loadListCentroCustoContabil();
        } else {
            GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO!");
        }
    }
    
    public void edit(CentroCustoContabil ccc) {
        centroCustoContabil = (CentroCustoContabil) new Dao().rebind(ccc);        
    }

    public CentroCustoContabil getCentroCustoContabil() {
        return centroCustoContabil;
    }

    public void setCentroCustoContabil(CentroCustoContabil centroCustoContabil) {
        this.centroCustoContabil = centroCustoContabil;
    }

    public List<CentroCustoContabil> getListCentroCustoContabil() {
        return listCentroCustoContabil;
    }

    public void setListCentroCustoContabil(List<CentroCustoContabil> listCentroCustoContabil) {
        this.listCentroCustoContabil = listCentroCustoContabil;
    }

}
