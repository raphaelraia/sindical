package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Sorteio;
import br.com.rtools.associativo.SorteioMovimento;
import br.com.rtools.associativo.SorteioStatus;
import br.com.rtools.associativo.dao.SorteioDao;
import br.com.rtools.associativo.dao.SorteioMovimentoDao;
import br.com.rtools.associativo.dao.SorteioStatusDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class SorteioMovimentoBean {

    private Sorteio sorteio;
    private SorteioStatus sorteioStatus;
    private List<SelectItem> listSorteio;
    private Boolean historico;
    private Integer idSorteio;
    private List<SorteioMovimento> listSorteioMovimento;

    @PostConstruct
    public void init() {
        listSorteio = new ArrayList<>();
        listSorteioMovimento = new ArrayList<>();
        sorteio = new Sorteio();
        sorteioStatus = new SorteioStatus();
        historico = false;
        loadListSorteio();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("sorteioMovimentoBean");
    }

    public void loadListSorteio() {
        listSorteio = new ArrayList<>();
        sorteio = new Sorteio();
        SorteioDao sorteioDao = new SorteioDao();
        List<Sorteio> list = sorteioDao.findByHistorico(this.historico);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSorteio = list.get(i).getId();
                sorteio = list.get(i);
            }
            String descricao = "";
            descricao += "De " + list.get(i).getInicioString();
            if (list.get(i).getDtFim() != null) {
                descricao += " até " + list.get(i).getFimString();
            }
            descricao += " - " + list.get(i).getDescricao();
            listSorteio.add(new SelectItem(list.get(i).getId(), descricao));
        }
        loadListSorteioMovimento();
        sorteioStatus = new SorteioStatusDao().findBySorteio(sorteio.getId());
    }

    public void loadListSorteioMovimento() {
        listSorteioMovimento = new ArrayList();
        listSorteioMovimento = new SorteioMovimentoDao().findBySorteio(this.sorteio.getId());
    }

    public void loadSorteio() {
        sorteio = new Sorteio();
        sorteio = (Sorteio) new Dao().find(new Sorteio(), idSorteio);
        loadListSorteioMovimento();
        new SorteioStatusDao().findBySorteio(sorteio.getId());
    }

    public Integer getIdSorteio() {
        return idSorteio;
    }

    public void setIdSorteio(Integer idSorteio) {
        this.idSorteio = idSorteio;
    }

    public Sorteio getSorteio() {
        return sorteio;
    }

    public void setSorteio(Sorteio sorteio) {
        this.sorteio = sorteio;
    }

    public void process() {
        if (sorteio.getId() != null) {
            Pessoa pessoa = new SorteioMovimentoDao().sort(sorteio.getId());
            SorteioMovimento sorteioMovimento = new SorteioMovimento();
            sorteioMovimento.setOperador((Usuario) GenericaSessao.getObject("sessaoUsuario"));
            sorteioMovimento.setSorteio(sorteio);
            sorteioMovimento.setDtSorteio(new Date());
            sorteioMovimento.setPessoa(pessoa);
            if (new Dao().save(sorteioMovimento, true)) {
                listSorteioMovimento = new ArrayList();
                listSorteioMovimento = new SorteioMovimentoDao().findBySorteio(this.sorteio.getId());
                GenericaMensagem.warn("Sucesso", "Sorteio realizado");
            } else {
                GenericaMensagem.warn("Erro", "Ao realizar processo!");
            }
        }
    }

    public void delete(SorteioMovimento sm) {
        if (new Dao().delete(sm, true)) {
            listSorteioMovimento = new ArrayList();
            listSorteioMovimento = new SorteioMovimentoDao().findBySorteio(this.sorteio.getId());
            GenericaMensagem.warn("Sucesso", "Registro removido!");
            NovoLog novoLog = new NovoLog();
            novoLog.delete("ID: " + sm.getId() + " - Sorteio: (" + sm.getSorteio().getId() + ") " + sm.getSorteio().getDescricao() + " - Sorteado: (" + sm.getPessoa().getId() + ") " + sm.getPessoa().getNome() + " - CPF: " + sm.getPessoa().getDocumento() + " - Data: " + sm.getSorteioString());
        } else {
            GenericaMensagem.warn("Erro", "Ao remover sorteado!");
        }
    }

    public List<SorteioMovimento> getListSorteioMovimento() {
        return listSorteioMovimento;
    }

    public void setListSorteioMovimento(List<SorteioMovimento> listSorteioMovimento) {
        this.listSorteioMovimento = listSorteioMovimento;
    }

    public List<SelectItem> getListSorteio() {
        return listSorteio;
    }

    public void setListSorteio(List<SelectItem> listSorteio) {
        this.listSorteio = listSorteio;
    }

    public Boolean getHistorico() {
        return historico;
    }

    public void setHistorico(Boolean historico) {
        this.historico = historico;
    }

    public void loadListSorteioMovimento(Integer pessoa_id) {
        listSorteioMovimento = new SorteioMovimentoDao().findByPessoa(pessoa_id);
    }

}
