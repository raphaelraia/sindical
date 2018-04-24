package br.com.rtools.seguranca.beans;

import br.com.rtools.seguranca.LogDefinicoes;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class LogDefinicoesBean implements Serializable {

    private LogDefinicoes logDefinicoes;
    private List<LogDefinicoes> listLogDefinicoes;
    private List<SelectItem> listRotinas;
    private Integer idRotina;
    private Integer diasManter;

    @PostConstruct
    public void init() {
        logDefinicoes = new LogDefinicoes();
        idRotina = null;
        diasManter = 0;
        listLogDefinicoes = new ArrayList();
        listRotinas = new ArrayList();
        loadListRotinas();
        loadListLogDefinicoes();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("logDefinicoesBean");
    }

    public void loadListLogDefinicoes() {
        listLogDefinicoes = new ArrayList();
        Rotina r = new Rotina();
        r.setRotina("TODAS AS ROTINAS (Exceto as configuradas)");
        listLogDefinicoes.add(new LogDefinicoes(null, r, LogDefinicoes.KEEP_DAYS_LOGS));
        List<LogDefinicoes> list = new Dao().list(new LogDefinicoes());
        if (!list.isEmpty()) {
            listLogDefinicoes.addAll(list);
        }
    }

    public void loadListRotinas() {
        RotinaDao rotinaDao = new RotinaDao();
        List<Rotina> list = rotinaDao.findByTabela("seg_log", "id_rotina");
        listRotinas.add(new SelectItem(null, "SEM ROTINAS ESPECIFICAS"));
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRotina = list.get(i).getId();
            }
            listRotinas.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
        }
    }

    public void store() {
        // 180 dias ou 6 meses        
        if (diasManter < 180) {
            if (diasManter != 0) {
                Messages.warn("Validação", "Deve ser superior ou igual a 180 dias!!!");
                return;
            }
        }
        if (idRotina == null) {
            for (int i = 0; i < listLogDefinicoes.size(); i++) {
                if (listLogDefinicoes.get(i).getRotina() == null) {
                    Messages.warn("Validação", "Configuração genérica já aplicada!!!");
                    return;
                }
            }
        } else {
            for (int i = 0; i < listLogDefinicoes.size(); i++) {
                if (listLogDefinicoes.get(i).getRotina() != null && listLogDefinicoes.get(i).getRotina().getId() == idRotina) {
                    Messages.warn("Validação", "Configuração gnérica já aplicada!!!");
                    return;
                }
            }
        }
        Dao dao = new Dao();
        LogDefinicoes logDefinicoes = new LogDefinicoes();
        if (idRotina == null) {
            logDefinicoes.setRotina(null);
        } else {
            logDefinicoes.setRotina((Rotina) dao.find(new Rotina(), idRotina));
        }
        logDefinicoes.setDiasManter(diasManter);
        if (logDefinicoes.getId() == null) {
            if (dao.save(logDefinicoes, true)) {
                Messages.info("Sucesso", "Registro inserido com sucesso");
                Messages.info("Sistema", "Esse log não será excluído do sistema, pois seu valor ladrão é 0!!!");
                loadListLogDefinicoes();
            } else {
                Messages.warn("Erro", "Ao inserir registro!");
            }
        }

    }

    public void delete(LogDefinicoes ld) {
        Dao dao = new Dao();
        if (dao.delete(ld, true)) {
            loadListRotinas();
            loadListLogDefinicoes();
            GenericaMensagem.info("Sucesso", "Registro removido!");
        } else {
            GenericaMensagem.warn("Erro", "Erro ao Excluir!");
        }
    }

    public void update(LogDefinicoes ld) {
        Dao dao = new Dao();
        if (ld.getDiasManter() < 180) {
            if (ld.getDiasManter() != 0) {
                Messages.warn("Validação", "Deve ser superior ou igual a 180 dias!!!");
                return;
            }
        }
        if (dao.update(ld, true)) {
            loadListRotinas();
            loadListLogDefinicoes();
            GenericaMensagem.info("Sucesso", "Registro atualizado!");
        } else {
            GenericaMensagem.warn("Erro", "Erro ao atualizar!");
        }
    }

    public List<LogDefinicoes> getListLogDefinicoes() {
        return listLogDefinicoes;
    }

    public void setListLogDefinicoes(List<LogDefinicoes> listLogDefinicoes) {
        this.listLogDefinicoes = listLogDefinicoes;
    }

    public List<SelectItem> getListRotinas() {
        return listRotinas;
    }

    public void setListRotinas(List<SelectItem> listRotinas) {
        this.listRotinas = listRotinas;
    }

    public Integer getIdRotina() {
        return idRotina;
    }

    public void setIdRotina(Integer idRotina) {
        this.idRotina = idRotina;
    }

    public Integer getDiasManter() {
        return diasManter;
    }

    public void setDiasManter(Integer diasManter) {
        this.diasManter = diasManter;
    }

    public LogDefinicoes getLogDefinicoes() {
        return logDefinicoes;
    }

    public void setLogDefinicoes(LogDefinicoes logDefinicoes) {
        this.logDefinicoes = logDefinicoes;
    }

}
