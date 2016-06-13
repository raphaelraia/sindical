package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Registro;
import br.com.rtools.sistema.ConfiguracaoCnpj;
import br.com.rtools.sistema.TipoPesquisaCnpj;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class ConfiguracaoCnpjBean implements Serializable {

    private ConfiguracaoCnpj configuracaoCnpj;
    private Registro registro;
    private List<SelectItem> listaTipoPesquisa = new ArrayList();
    private Integer indexTipoPesquisa = 0;

    @PostConstruct
    public void init() {
        Dao dao = new Dao();
        loadListaTipoPesquisa();

        configuracaoCnpj = (ConfiguracaoCnpj) dao.find(new ConfiguracaoCnpj(), 1);

        if (configuracaoCnpj == null) {
            configuracaoCnpj = new ConfiguracaoCnpj();
            configuracaoCnpj.setTipoPesquisaCnpj((TipoPesquisaCnpj) new Dao().find(new TipoPesquisaCnpj(), Integer.valueOf(listaTipoPesquisa.get(indexTipoPesquisa).getDescription())));
            dao.save(configuracaoCnpj, true);
        }

        for (int i = 0; i < listaTipoPesquisa.size(); i++) {
            if (configuracaoCnpj.getTipoPesquisaCnpj().getId().equals(Integer.valueOf(listaTipoPesquisa.get(i).getDescription()))) {
                indexTipoPesquisa = i;
            }
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoCnpjBean");
    }

    public void loadListaTipoPesquisa() {
        listaTipoPesquisa.clear();

        List<TipoPesquisaCnpj> result = new Dao().list(new TipoPesquisaCnpj());

        for (int i = 0; i < result.size(); i++) {
            listaTipoPesquisa.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public void update() {
        Dao dao = new Dao();
        if (configuracaoCnpj.getId() != -1) {
            configuracaoCnpj.setTipoPesquisaCnpj((TipoPesquisaCnpj) new Dao().find(new TipoPesquisaCnpj(), Integer.valueOf(listaTipoPesquisa.get(indexTipoPesquisa).getDescription())));
            if (dao.update(configuracaoCnpj, true)) {
                configuracaoCnpj.setDataAtualizacao(DataHoje.dataHoje());
                GenericaMensagem.info("Sucesso", "Configurações aplicadas");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        }
    }

    public ConfiguracaoCnpj getConfiguracaoCnpj() {
        return configuracaoCnpj;
    }

    public void setConfiguracaoCnpj(ConfiguracaoCnpj configuracaoCnpj) {
        this.configuracaoCnpj = configuracaoCnpj;
    }

    public void load() {

    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public void onChange(TabChangeEvent event) {
        Tab activeTab = event.getTab();
    }

//    @Column(name = "is_cadastro_cnpj", columnDefinition = "boolean default false")
//    private boolean cadastroCnpj;
    public List<SelectItem> getListaTipoPesquisa() {
        return listaTipoPesquisa;
    }

    public void setListaTipoPesquisa(List<SelectItem> listaTipoPesquisa) {
        this.listaTipoPesquisa = listaTipoPesquisa;
    }

    public Integer getIndexTipoPesquisa() {
        return indexTipoPesquisa;
    }

    public void setIndexTipoPesquisa(Integer indexTipoPesquisa) {
        this.indexTipoPesquisa = indexTipoPesquisa;
    }

}
