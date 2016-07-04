package br.com.rtools.pessoa.beans;

import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Profissao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class ProfissaoBean extends PesquisarProfissaoBean implements Serializable {

    private Profissao prof;
    private String s_cbo;
    private String s_profissao;

    @PostConstruct
    public void init() {
        prof = new Profissao();
        s_cbo = "";
        s_profissao = "";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("profissaoBean");
    }

    public void clearProfissao() {
        prof = new Profissao();
        super.profissao = new Profissao();
    }

    public void editProfissao(Profissao pr) {
        prof = pr;
        super.getListaProfissao().clear();
    }

    public void saveProfissao() {

        if (prof.getProfissao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite a profissão!");
            return;
        }
        NovoLog novoLog = new NovoLog();
        Dao dao = new Dao();
        if (prof.getId() == -1) {
            if (dao.existsDescription(prof.getProfissao(), "ds_profissao", "pes_profissao")) {
                GenericaMensagem.warn("Validação", "Profissão já cadastrada!");
                return;
            }
            if (!prof.getCbo().isEmpty()) {
                if (dao.existsDescription(prof.getCbo(), "ds_cbo", "pes_profissao")) {
                    GenericaMensagem.warn("Validação", "CBO já existe!");
                    return;
                }
            }
            dao.openTransaction();
            if (dao.save(prof)) {
                GenericaMensagem.info("Sucesso!", "Profissão salva com sucesso");
                novoLog.save(
                        "ID: " + prof.getId()
                        + " - Profissão: " + prof.getProfissao()
                        + " - CBO: " + prof.getCbo()
                );
            } else {
                GenericaMensagem.warn("Erro", "Erro ao salvar profissão!");
                dao.rollback();
                return;
            }
        } else {
            Profissao p = (Profissao) dao.find(prof);
            String beforeUpdate
                    = "ID: " + p.getId()
                    + " - Profissão: " + p.getProfissao()
                    + " - CBO: " + p.getCbo();
            dao.openTransaction();
            if (dao.update(prof)) {
                novoLog.update(beforeUpdate,
                        "ID: " + prof.getId()
                        + " - Profissão: " + prof.getProfissao()
                        + " - CBO: " + prof.getCbo()
                );
                GenericaMensagem.info("Sucesso!", "Profissão atualizada com sucesso");
            } else {
                GenericaMensagem.warn("Erro", "Erro ao atualizar profissão!");
                dao.rollback();
                return;
            }
        }
        dao.commit();
        super.getListaProfissao().clear();
        prof = new Profissao();
    }

    public void deleteProfissao() {
        if (prof.getId() == -1) {
            GenericaMensagem.warn("Erro", "Selecione uma profissão para ser excluída!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        if (dao.delete(prof)) {
            NovoLog novoLog = new NovoLog();
            novoLog.delete(
                    "ID: " + prof.getId()
                    + " - Profissão: " + prof.getProfissao()
                    + " - CBO: " + prof.getCbo()
            );
            GenericaMensagem.info("Sucesso!", "Profissão deletada com sucesso!");
            dao.commit();
        } else {
            GenericaMensagem.warn("Erro", "Erro ao deletar profissão!");
            dao.rollback();
        }
        super.getListaProfissao().clear();
        prof = new Profissao();
    }

    public String getS_cbo() {
        s_cbo = super.profissao.getCbo();
        return s_cbo;
    }

    public void setS_cbo(String s_cbo) {
        this.s_cbo = s_cbo;
    }

    public String getS_profissao() {
        s_profissao = super.profissao.getProfissao();
        return s_profissao;
    }

    public void setS_profissao(String s_profissao) {
        this.s_profissao = s_profissao;
    }

    public Profissao getProf() {
        return prof;
    }

    public void setProf(Profissao prof) {
        this.prof = prof;
    }
}
