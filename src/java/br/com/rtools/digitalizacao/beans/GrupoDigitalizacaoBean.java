package br.com.rtools.digitalizacao.beans;

import br.com.rtools.digitalizacao.Documento;
import br.com.rtools.digitalizacao.GrupoDigitalizacao;
import br.com.rtools.digitalizacao.dao.GrupoDigitalizacaoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.Modulo;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public final class GrupoDigitalizacaoBean implements Serializable {

    private GrupoDigitalizacao grupo = new GrupoDigitalizacao();
    private Integer indexModulo = 0;
    private List<SelectItem> listaModulo = new ArrayList();
    private List<GrupoDigitalizacao> listaGrupo = new ArrayList();

    public GrupoDigitalizacaoBean() {
        loadListaModulo();
        loadListaGrupo();
    }

    public void salvar() {
        if (grupo.getDescricao().isEmpty() || grupo.getDescricao().length() <= 4) {
            GenericaMensagem.warn("Atenção", "Digite uma Descrição válida!");
            return;
        }

        Dao dao = new Dao();


        grupo.setModulo((Modulo) dao.find(new Modulo(), Integer.valueOf(listaModulo.get(indexModulo).getDescription())));

        String save_log
                = "Grupo: " + grupo.getDescricao() + " - "
                + "Modulo: " + grupo.getModulo().getDescricao();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("dig_grupo");

        dao.openTransaction();
        if (grupo.getId() == -1) {
            if (!dao.save(grupo)) {
                GenericaMensagem.error("Erro", "Não foi possível salvar Grupo Digitalização!");
                dao.rollback();
                return;
            }

            novoLog.setCodigo(grupo.getId());
            novoLog.save(
                    save_log
            );
        } else {
            if (!dao.update(grupo)) {
                GenericaMensagem.error("Erro", "Não foi possível alterar Grupo Digitalização!");
                dao.rollback();
                return;
            }
            
            GrupoDigitalizacao gr = (GrupoDigitalizacao) new Dao().find(grupo);
            String updade_log
                    = "Grupo: " + gr.getDescricao() + " - "
                    + "Modulo: " + gr.getModulo().getDescricao();

            novoLog.setCodigo(grupo.getId());
            novoLog.update(save_log, updade_log);
        }

        dao.commit();

        GenericaMensagem.info("Sucesso", "Grupo Digitalização salva!");

        grupo = new GrupoDigitalizacao();
        loadListaGrupo();
    }

    public void novo() {
        grupo = new GrupoDigitalizacao();
    }

    public void editar(GrupoDigitalizacao g) {
        grupo = g;

        for (int i = 0; i < listaModulo.size(); i++) {
            if (grupo.getModulo().getId() == Integer.valueOf(listaModulo.get(i).getDescription())) {
                indexModulo = i;
            }
        }
    }

    public void excluir() {
        if (grupo.getId() != -1) {
            Dao dao = new Dao();

            dao.openTransaction();

            if (!dao.delete(dao.find(grupo))) {
                dao.rollback();
                return;
            }

            String delete_log
                    = "Grupo: " + grupo.getDescricao() + " - "
                    + "Modulo: " + grupo.getModulo().getDescricao();

            NovoLog novoLog = new NovoLog();
            novoLog.setTabela("dig_grupo");
            novoLog.setCodigo(grupo.getId());

            novoLog.delete(delete_log);

            GenericaMensagem.info("Sucesso", "Grupo Digitalização Excluido!");
            dao.commit();

            grupo = new GrupoDigitalizacao();
            loadListaGrupo();
        }
    }

    public void loadListaModulo() {
        listaModulo.clear();

        List<Modulo> result = new Dao().list(new Modulo());

        for (int i = 0; i < result.size(); i++) {
            listaModulo.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            String.valueOf(result.get(i).getId())
                    )
            );
        }
    }

    public void loadListaGrupo() {
        listaGrupo.clear();

        listaGrupo = new GrupoDigitalizacaoDao().listaGrupo();
    }

    public GrupoDigitalizacao getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoDigitalizacao grupo) {
        this.grupo = grupo;
    }

    public List<SelectItem> getListaModulo() {
        return listaModulo;
    }

    public void setListaModulo(List<SelectItem> listaModulo) {
        this.listaModulo = listaModulo;
    }

    public Integer getIndexModulo() {
        return indexModulo;
    }

    public void setIndexModulo(Integer indexModulo) {
        this.indexModulo = indexModulo;
    }

    public List<GrupoDigitalizacao> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<GrupoDigitalizacao> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

}
