package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.locadoraFilme.ConfiguracaoLocadora;
import br.com.rtools.utilitarios.Dao;
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

@ManagedBean
@ViewScoped
public class ConfiguracaoLocadoraBean implements Serializable {

    private ConfiguracaoLocadora configuracaoLocadora;
    private List<SelectItem> listServico;
    private Integer idServico;

    @PostConstruct
    public void init() {
        Dao dao = new Dao();
        configuracaoLocadora = (ConfiguracaoLocadora) dao.find(new ConfiguracaoLocadora(), 1);
        if (configuracaoLocadora == null) {
            configuracaoLocadora = new ConfiguracaoLocadora();
            configuracaoLocadora.setId(1);
            configuracaoLocadora.setServicos(null);
            dao.save(configuracaoLocadora, true);
        }
        listServico = new ArrayList();
        getListServico();
        if (configuracaoLocadora.getServicos() != null) {
            idServico = configuracaoLocadora.getServicos().getId();
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoLocadoraBean");
    }

    public void load() {

    }

    public void update() {
        Dao dao = new Dao();
        if (configuracaoLocadora.getId() != -1) {
            if (idServico == null) {
                configuracaoLocadora.setServicos(null);
            } else {
                configuracaoLocadora.setServicos((Servicos) dao.find(new Servicos(), idServico));
            }
            if (dao.update(configuracaoLocadora, true)) {
                GenericaMensagem.info("Sucesso", "Configurações Aplicadas");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        }
    }

    public ConfiguracaoLocadora getConfiguracaoLocadora() {
        return configuracaoLocadora;
    }

    public void setConfiguracaoLocadora(ConfiguracaoLocadora configuracaoLocadora) {
        this.configuracaoLocadora = configuracaoLocadora;
    }

    public List<SelectItem> getListServico() {
        if (listServico.isEmpty()) {
            listServico = new ArrayList<>();
            ServicosDao servicosDao = new ServicosDao();
            servicosDao.setSituacao("A");
            List<Servicos> list = servicosDao.findAll();
            listServico.add(new SelectItem("", "Selecionar serviço"));
            for (int i = 0; i < list.size(); i++) {
                if (configuracaoLocadora.getServicos() != null) {
                    if (list.get(i).getId() == configuracaoLocadora.getServicos().getId()) {
                        setIdServico((Integer) i);
                    }
                }
                listServico.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
        return listServico;
    }

    public void setListServico(List<SelectItem> listServico) {
        this.listServico = listServico;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public static ConfiguracaoLocadora get() {
        return (ConfiguracaoLocadora) new Dao().find(new ConfiguracaoLocadora(), 1);
    }
}
