package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.ConvencaoCidade;
import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.arrecadacao.dao.ConvencaoPeriodoDao;
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
public class ConvencaoPeriodoBean {

    private ConvencaoPeriodo convencaoPeriodo;
    private int idConvencao;
    private int idConvencaoCidade;
    private List<ConvencaoPeriodo> listConvencaoPeriodos;
    private List<SelectItem> listConvencao;
    private List<SelectItem> listConvencaoCidade;

    private ConvencaoCidade convencaoCidade;

    @PostConstruct
    public void init() {
        convencaoPeriodo = new ConvencaoPeriodo();
        idConvencao = 0;
        idConvencaoCidade = 0;
        listConvencaoPeriodos = new ArrayList();
        listConvencao = new ArrayList();
        listConvencaoCidade = new ArrayList();
        convencaoCidade = new ConvencaoCidade();
        
        loadListConvencao();
        loadListConvencaoCidade();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("convencaoPeriodoBean");
    }

    public void atualizaConvencaoCidade() {
        if (listConvencaoCidade.isEmpty()) {
            convencaoCidade = new ConvencaoCidade();
        }

        convencaoCidade = (ConvencaoCidade) new Dao().find(new ConvencaoCidade(), Integer.parseInt(listConvencaoCidade.get(idConvencaoCidade).getDescription()));
        convencaoPeriodo.setSindicato(convencaoCidade.getSindicato());
    }

    public void loadListConvencao() {
        listConvencao.clear();
        idConvencao = 0;
        
        Dao di = new Dao();
        List<Convencao> list = di.list("Convencao", true);
        for (int i = 0; i < list.size(); i++) {
            listConvencao.add(new SelectItem(i,
                    list.get(i).getDescricao().toUpperCase(),
                    Integer.toString(list.get(i).getId())));
        }
    }
    
    public void loadListConvencaoCidade() {
        listConvencaoCidade.clear();
        idConvencaoCidade = 0;
        
        if (listConvencao.isEmpty()) {
            return;
        }

        ConvencaoPeriodoDao db = new ConvencaoPeriodoDao();
        List<ConvencaoCidade> list = db.listaGrupoCidadePorConvencao(Integer.parseInt(listConvencao.get(idConvencao).getDescription()));
        for (int i = 0; i < list.size(); i++) {
            listConvencaoCidade.add(new SelectItem(
                    i,
                    list.get(i).getGrupoCidade().getDescricao(),
                    Integer.toString(list.get(i).getId()))
            );
        }
        
        atualizaConvencaoCidade();
    }

    public void clear() {
        GenericaSessao.remove("convencaoPeriodoBean");
    }

    public void edit(ConvencaoPeriodo cp) {
        convencaoPeriodo = cp;
        for (int i = 0; i < listConvencao.size(); i++) {
            if (Integer.parseInt(listConvencao.get(i).getDescription()) == convencaoPeriodo.getConvencao().getId()) {
                idConvencao = i;
                break;
            }
        }
        
        loadListConvencaoCidade();
        
        Dao dao = new Dao();
        for (int i = 0; i < listConvencaoCidade.size(); i++) {
            ConvencaoCidade cc = (ConvencaoCidade) dao.find(new ConvencaoCidade(), Integer.parseInt(listConvencaoCidade.get(i).getDescription()));
            //if (Integer.parseInt(listConvencaoCidade.get(i).getDescription()) == convencaoPeriodo.getGrupoCidade().getId()) {
            if (cc.getGrupoCidade().getId() == convencaoPeriodo.getGrupoCidade().getId()) {
                idConvencaoCidade = i;
                break;
            }
        }
        
        convencaoPeriodo.setSindicato(cp.getSindicato());
    }

    public void save() {
        ConvencaoPeriodoDao convencaoPeriodoDB = new ConvencaoPeriodoDao();
        if (convencaoPeriodo.getReferenciaInicial().equals("__/____") || convencaoPeriodo.getReferenciaInicial().equals("")) {
            GenericaMensagem.warn("Sistema", "Informar a referência inicial!");
            return;
        }
        if (convencaoPeriodo.getReferenciaFinal().equals("__/____") || convencaoPeriodo.getReferenciaFinal().equals("")) {
            GenericaMensagem.warn("Sistema", "Informar a referência final!");
            return;
        }

        convencaoPeriodo.setGrupoCidade(convencaoCidade.getGrupoCidade());

        if (listConvencao.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Nenhuma Convenção Cadastrada!");
            return;
        }

        convencaoPeriodo.setConvencao((Convencao) new Dao().find(new Convencao(), Integer.parseInt(listConvencao.get(idConvencao).getDescription())));

        if (convencaoPeriodoDB.convencaoPeriodoExiste(convencaoPeriodo)) {
            GenericaMensagem.warn("Sistema", "Convenção período já existe!");
            return;
        }

        convencaoPeriodo.setSindicato(convencaoCidade.getSindicato());

        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        if (convencaoPeriodo.getId() == -1) {
            di.openTransaction();
            if (di.save(convencaoPeriodo)) {
                di.commit();
                novoLog.save(
                        "ID: " + convencaoPeriodo.getId()
                        + " - Convencao: (" + convencaoPeriodo.getConvencao().getId() + ") " + convencaoPeriodo.getConvencao().getDescricao()
                        + " - Grupo Cidade: (" + convencaoPeriodo.getGrupoCidade().getId() + ") " + convencaoPeriodo.getGrupoCidade().getDescricao()
                        + " - Ref: " + convencaoPeriodo.getReferenciaInicial() + " - " + convencaoPeriodo.getReferenciaFinal()
                );
                convencaoPeriodo = new ConvencaoPeriodo();
                listConvencaoPeriodos.clear();
                GenericaMensagem.info("Sucesso", "Registro inserido");
            } else {
                di.rollback();
                GenericaMensagem.warn("Erro", "Erro ao inserir esse registro!");
            }
        } else {
            ConvencaoPeriodo cp = (ConvencaoPeriodo) di.find(convencaoPeriodo);
            String beforeUpdate
                    = "ID: " + cp.getId()
                    + " - Convencao: (" + cp.getConvencao().getId() + ") " + cp.getConvencao().getDescricao()
                    + " - Grupo Cidade: (" + cp.getGrupoCidade().getId() + ") " + cp.getGrupoCidade().getDescricao()
                    + " - Ref: " + cp.getReferenciaInicial() + " - " + cp.getReferenciaFinal();
            di.openTransaction();
            if (di.update(convencaoPeriodo)) {
                di.commit();
                novoLog.update(beforeUpdate,
                        "ID: " + convencaoPeriodo.getId()
                        + " - Convencao: (" + convencaoPeriodo.getConvencao().getId() + ") " + convencaoPeriodo.getConvencao().getDescricao()
                        + " - Grupo Cidade: (" + convencaoPeriodo.getGrupoCidade().getId() + ") " + convencaoPeriodo.getGrupoCidade().getDescricao()
                        + " - Ref: " + convencaoPeriodo.getReferenciaInicial() + " - " + convencaoPeriodo.getReferenciaFinal()
                );
                GenericaMensagem.info("Sucesso", "Registro atualizado");
            } else {
                di.rollback();
                GenericaMensagem.warn("Erro", "Erro ao atualizar esse registro!");
            }
        }
    }

    public void delete() {
        Dao di = new Dao();
        NovoLog novoLog = new NovoLog();
        if (convencaoPeriodo.getId() != -1) {
            di.openTransaction();
            if (di.delete(convencaoPeriodo)) {
                novoLog.delete(
                        "ID: " + convencaoPeriodo.getId()
                        + " - Convencao: (" + convencaoPeriodo.getConvencao().getId() + ") " + convencaoPeriodo.getConvencao().getDescricao()
                        + " - Grupo Cidade: (" + convencaoPeriodo.getGrupoCidade().getId() + ") " + convencaoPeriodo.getGrupoCidade().getDescricao()
                        + " - Ref: " + convencaoPeriodo.getReferenciaInicial() + " - " + convencaoPeriodo.getReferenciaFinal()
                );
                di.commit();
                convencaoPeriodo = new ConvencaoPeriodo();
                listConvencaoPeriodos.clear();
                GenericaMensagem.info("Sucesso", "Registro excluído");
            } else {
                di.rollback();
                GenericaMensagem.warn("Erro", "Erro ao excluir esse registro!");
            }
        }
    }

    public List<SelectItem> getListConvencao() {
        return listConvencao;
    }

    public void setListConvencao(List<SelectItem> listConvencao) {
        this.listConvencao = listConvencao;
    }

    public List<SelectItem> getListConvencaoCidade() {
        return listConvencaoCidade;
    }

    public void setListConvencaoCidade(List<SelectItem> listConvencaoCidade) {
        this.listConvencaoCidade = listConvencaoCidade;
    }

    public int getIdConvencao() {
        return idConvencao;
    }

    public void setIdConvencao(int idConvencao) {
        this.idConvencao = idConvencao;
    }

    public int getIdConvencaoCidade() {
        return idConvencaoCidade;
    }

    public void setIdConvencaoCidade(int idConvencaoCidade) {
        this.idConvencaoCidade = idConvencaoCidade;
    }

    public List<ConvencaoPeriodo> getListConvencaoPeriodos() {
        if (listConvencaoPeriodos.isEmpty()) {
            ConvencaoPeriodoDao db = new ConvencaoPeriodoDao();
            setListConvencaoPeriodos((List<ConvencaoPeriodo>) db.listaConvencaoPeriodo());
        }
        return listConvencaoPeriodos;
    }

    public void setListConvencaoPeriodos(List<ConvencaoPeriodo> listConvencaoPeriodos) {
        this.listConvencaoPeriodos = listConvencaoPeriodos;
    }

    public ConvencaoPeriodo getConvencaoPeriodo() {
        return convencaoPeriodo;
    }

    public void setConvencaoPeriodo(ConvencaoPeriodo convencaoPeriodo) {
        this.convencaoPeriodo = convencaoPeriodo;
    }

    public ConvencaoCidade getConvencaoCidade() {
        return convencaoCidade;
    }

    public void setConvencaoCidade(ConvencaoCidade convencaoCidade) {
        this.convencaoCidade = convencaoCidade;
    }
}
