package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.SCobranca;
import br.com.rtools.pessoa.StatusCobranca;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class ConfiguracaoSocialBean implements Serializable {

    private ConfiguracaoSocial configuracaoSocial;
    private Registro registro;

    private int idGrupoCategoria;
    private List<SelectItem> listaGrupoCategoria;

    private int indexStatusCobranca;
    private List<SelectItem> listaStatusCobranca;

    private SCobranca cobranca = new SCobranca();

    @PostConstruct
    public void init() {
        idGrupoCategoria = 0;
        listaGrupoCategoria = new ArrayList();
        
        indexStatusCobranca = 0;
        listaStatusCobranca = new ArrayList();

        loadGrupoCategoria();

        Dao dao = new Dao();
        configuracaoSocial = (ConfiguracaoSocial) dao.find(new ConfiguracaoSocial(), 1);
        if (configuracaoSocial == null) {
            configuracaoSocial = new ConfiguracaoSocial();
            dao.save(configuracaoSocial, true);
        }

        registro = (Registro) dao.find(new Registro(), 1);

        if (configuracaoSocial.getGrupoCategoriaInativaDemissionado() == null || (listaGrupoCategoria.size() == 1)) {
            idGrupoCategoria = 0;
        } else {
            for (int i = 0; i < listaGrupoCategoria.size(); i++) {
                if (Objects.equals(configuracaoSocial.getGrupoCategoriaInativaDemissionado().getId(), Integer.valueOf(listaGrupoCategoria.get(i).getDescription()))) {
                    idGrupoCategoria = i;
                }
            }
        }

        cobranca = (SCobranca) dao.find(new SCobranca(), 1);

        loadListaStatusCobranca();

        for (int i = 0; i < listaStatusCobranca.size(); i++) {
            if (Objects.equals(configuracaoSocial.getStatusCobranca().getId(), Integer.valueOf(listaStatusCobranca.get(i).getDescription()))) {
                indexStatusCobranca = i;
            }
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoSocialBean");
    }

    public void loadListaStatusCobranca() {
        
        indexStatusCobranca = 0;
        listaStatusCobranca.clear();

        List<StatusCobranca> result = new Dao().find("StatusCobranca", new int[]{1, 2});

        for (int i = 0; i < result.size(); i++) {
            listaStatusCobranca.add(new SelectItem(i, result.get(i).getDescricao(), Integer.toString(result.get(i).getId())));
        }

    }

    public void update() {
        Dao dao = new Dao();
        if (configuracaoSocial.getId() != -1) {
            if (configuracaoSocial.getCartaoPosicaoCodigo() > 12) {
                GenericaMensagem.error("Atenção", "Posição máxima para a via é 12 !");
                return;
            }

            if (configuracaoSocial.getCartaoPosicaoCodigo() > 6) {
                GenericaMensagem.error("Atenção", "Posição máxima para o código é 6 !");
                return;
            }

            if (Integer.valueOf(listaGrupoCategoria.get(idGrupoCategoria).getDescription()) == 0) {
                configuracaoSocial.setGrupoCategoriaInativaDemissionado(null);
            } else {
                configuracaoSocial.setGrupoCategoriaInativaDemissionado((GrupoCategoria) new Dao().find(new GrupoCategoria(), Integer.valueOf(listaGrupoCategoria.get(idGrupoCategoria).getDescription())));
            }

            configuracaoSocial.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), Integer.valueOf(listaStatusCobranca.get(indexStatusCobranca).getDescription())));

            if (dao.update(configuracaoSocial, true)) {
                GenericaMensagem.info("Sucesso", "Configurações Aplicadas");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        }

        if (cobranca.getId() != -1) {

            if (dao.update(cobranca, true)) {

            }

        }
    }

    public ConfiguracaoSocial getConfiguracaoSocial() {
        return configuracaoSocial;
    }

    public void setConfiguracaoSocial(ConfiguracaoSocial configuracaoSocial) {
        this.configuracaoSocial = configuracaoSocial;
    }

    public void load() {

    }

    public void loadGrupoCategoria() {
        listaGrupoCategoria.clear();
        idGrupoCategoria = 0;

        listaGrupoCategoria.add(new SelectItem(0, "Selecione um Grupo Categoria", "0"));

        List<GrupoCategoria> grupoCategorias = (List<GrupoCategoria>) new Dao().list("GrupoCategoria");

        if (!grupoCategorias.isEmpty()) {
            for (int i = 0; i < grupoCategorias.size(); i++) {
                listaGrupoCategoria.add(new SelectItem(i + 1, grupoCategorias.get(i).getGrupoCategoria(), "" + grupoCategorias.get(i).getId()));
            }
        } else {
            listaGrupoCategoria.add(new SelectItem(0, "Nenhum Grupo Categoria Encontrado", "0"));
        }
    }

    public int getIdGrupoCategoria() {
        return idGrupoCategoria;
    }

    public void setIdGrupoCategoria(int idGrupoCategoria) {
        this.idGrupoCategoria = idGrupoCategoria;
    }

    public List<SelectItem> getListaGrupoCategoria() {
        return listaGrupoCategoria;
    }

    public void setListaGrupoCategoria(List<SelectItem> listaGrupoCategoria) {
        this.listaGrupoCategoria = listaGrupoCategoria;
    }

    /**
     * @return the cobranca
     */
    public SCobranca getCobranca() {
        return cobranca;
    }

    /**
     * @param cobranca the cobranca to set
     */
    public void setCobranca(SCobranca cobranca) {
        this.cobranca = cobranca;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public int getIndexStatusCobranca() {
        return indexStatusCobranca;
    }

    public void setIndexStatusCobranca(int indexStatusCobranca) {
        this.indexStatusCobranca = indexStatusCobranca;
    }

    public List<SelectItem> getListaStatusCobranca() {
        return listaStatusCobranca;
    }

    public void setListaStatusCobranca(List<SelectItem> listaStatusCobranca) {
        this.listaStatusCobranca = listaStatusCobranca;
    }

}
