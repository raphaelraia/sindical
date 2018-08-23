package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Suspencao;
import br.com.rtools.associativo.dao.SuspencaoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class SuspencaoBean {

    private Suspencao suspencao;
    private List<Suspencao> listSuspencao;

    @PostConstruct
    public void init() {
        suspencao = new Suspencao();
        listSuspencao = new ArrayList();        
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("suspencaoBean");
        GenericaSessao.remove("pessoaPesquisa");
    }

    public void clear() {
        GenericaSessao.remove("suspencaoBean");
    }

    public void save() {
        if (suspencao.getPessoa().getId() == -1) {
            Messages.warn("Pesquise um sócio para Suspender!");
            return;
        }
        if (suspencao.getDataInicial().length() < 7 || suspencao.getDataFinal().length() < 7) {
            Messages.warn("Data inválida!");
            return;
        }
        if (DataHoje.converteDataParaInteger(suspencao.getDataInicial())
                > DataHoje.converteDataParaInteger(suspencao.getDataFinal())) {
            Messages.warn("Data inicial não pode ser maior que data final!");
            return;
        }
        if (suspencao.getMotivo().equals("") || suspencao.getMotivo() == null) {
            Messages.warn("Digite um motivo de Suspensão!");
            return;
        }
        SuspencaoDao suspencaoDB = new SuspencaoDao();
        if (suspencaoDB.exists(suspencao.getPessoa().getId()) != null) {
            Messages.warn("Sócio já encontra-se suspenso!");
            return;
        }
        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        if (suspencao.getId() == -1) {
            if (di.save(suspencao, true)) {
                novoLog.save(
                        "ID: " + suspencao.getId()
                        + " - Pessoa: (" + suspencao.getPessoa().getId() + ") " + suspencao.getPessoa().getNome()
                        + " - Período: " + suspencao.getDataInicial() + " até " + suspencao.getDataFinal()
                        + " - Motivo: " + suspencao.getMotivo()
                );
                Messages.info("Registro inserido com sucesso");
                loadListSuspensao();
            } else {
                Messages.warn("Erro ao inserir registro!");
            }
        } else {
            Suspencao s = (Suspencao) di.find(suspencao);
            String beforeUpdate
                    = "ID: " + s.getId()
                    + " - Pessoa: (" + s.getPessoa().getId() + ") " + s.getPessoa().getNome()
                    + " - Período: " + s.getDataInicial() + " até " + s.getDataFinal()
                    + " - Motivo: " + s.getMotivo();
            if (di.update(suspencao, true)) {
                novoLog.update(beforeUpdate,
                        "ID: " + suspencao.getId()
                        + " - Pessoa: (" + suspencao.getPessoa().getId() + ") " + suspencao.getPessoa().getNome()
                        + " - Período: " + suspencao.getDataInicial() + " até " + suspencao.getDataFinal()
                        + " - Motivo: " + suspencao.getMotivo()
                );
                Messages.info("Registro atualizado com sucesso");
                loadListSuspensao();
            } else {
                Messages.warn("Erro ao atualizar registro!");
            }
        }
    }

    public void delete() {
        if (suspencao.getId() == -1) {
            Messages.warn("Selecione uma suspensão para ser excluída!");
            return;
        }
        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        if (di.delete(suspencao, true)) {
            novoLog.delete(
                    "ID: " + suspencao.getId()
                    + " - Pessoa: (" + suspencao.getPessoa().getId() + ") " + suspencao.getPessoa().getNome()
                    + " - Período: " + suspencao.getDataInicial() + " até " + suspencao.getDataFinal()
                    + " - Motivo: " + suspencao.getMotivo()
            );
            suspencao = new Suspencao();
            loadListSuspensao();
            Messages.info("Registro removido com sucesso");
        } else {
            Messages.warn("Erro ao remover registro!");
        }
    }

    public void novo() {
        suspencao = new Suspencao();
        loadListSuspensao();
    }

    public String edit(Suspencao s) {
        Dao di = new Dao();
        suspencao = (Suspencao) di.rebind(s);
        GenericaSessao.put("pessoaPesquisa", suspencao.getPessoa());
        GenericaSessao.put("linkClicado", true);
        return "suspensao";
    }

    public List<Suspencao> getListSuspencao() {
        return listSuspencao;
    }

    public void setListSuspencao(List<Suspencao> listSuspencao) {
        this.listSuspencao = listSuspencao;
    }

    public Suspencao getSuspencao() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            suspencao.setPessoa((Pessoa) GenericaSessao.getObject("pessoaPesquisa", true));
        }
        return suspencao;
    }

    public void loadListSuspensao() {
        listSuspencao = new ArrayList();
        listSuspencao = (List<Suspencao>) new Dao().list(new Suspencao(), true);
    }

    public void setSuspencao(Suspencao suspencao) {
        this.suspencao = suspencao;
    }

}
