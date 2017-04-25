package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.GrupoCidade;
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
import java.io.Serializable;
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
public class SorteioMovimentoBean implements Serializable {

    private Sorteio sorteio;
    private SorteioStatus sorteioStatus;
    private List<SelectItem> listSorteio;
    private List<SelectItem> listStatus;
    private Boolean historico;
    private Integer idSorteio;
    private Integer idStatus;
    private List<SorteioMovimento> listSorteioMovimento;

    @PostConstruct
    public void init() {
        listSorteio = new ArrayList<>();
        listStatus = new ArrayList<>();
        listSorteioMovimento = new ArrayList<>();
        sorteio = new Sorteio();
        sorteioStatus = new SorteioStatus();
        historico = false;
        loadListSorteio();
        loadListSorteioStatus();
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

    }

    public void loadListSorteioStatus() {
        listStatus = new ArrayList<>();
        idStatus = null;
        sorteioStatus = new SorteioStatus();
        SorteioStatusDao sorteioStatusDao = new SorteioStatusDao();
        if (idSorteio != null) {
            Integer idGrupoCidade = null;
            List<SorteioStatus> list = sorteioStatusDao.findBySorteio(idSorteio);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    if (list.get(i).getGrupoCidade() != null) {
                        idGrupoCidade = list.get(i).getGrupoCidade().getId();
                    }
                    idStatus = list.get(i).getId();
                }
                if (list.get(i).getGrupoCidade() == null) {
                    listStatus.add(new SelectItem(null, "NENHUM GRUPO CIDADE"));
                } else {
                    listStatus.add(new SelectItem(list.get(i).getId(), list.get(i).getGrupoCidade().getDescricao().toUpperCase()));
                }
            }
            loadListSorteioMovimento();
            if (sorteio.getId() != null) {
                sorteioStatus = new SorteioStatusDao().findBySorteio(sorteio.getId(), idGrupoCidade);
            }
        }
    }

    public void loadListSorteioMovimento() {
        listSorteioMovimento = new ArrayList();
        if (this.sorteio.getId() != null) {
            listSorteioMovimento = new SorteioMovimentoDao().findBySorteio(this.sorteio.getId());
        }
    }

    public void loadSorteio() {
        loadSorteio(true);
    }

    public void loadSorteio(Boolean reloadSorteioStatus) {
        sorteio = new Sorteio();
        if (idSorteio != null) {
            sorteio = (Sorteio) new Dao().find(new Sorteio(), idSorteio);
        }
        loadListSorteioMovimento();
        if (reloadSorteioStatus) {
            loadListSorteioStatus();
        }
        if (sorteio.getId() != null) {
            new SorteioStatusDao().findBySorteio(sorteio.getId());
        }
        SorteioStatus ss = (SorteioStatus) new Dao().find(new SorteioStatus(), idStatus);
        sorteioStatus = new SorteioStatus();
        if (ss != null) {
            if (ss.getGrupoCidade() == null) {
                sorteioStatus = new SorteioStatusDao().findBySorteio(sorteio.getId(), null);
            } else {
                sorteioStatus = new SorteioStatusDao().findBySorteio(sorteio.getId(), ss.getGrupoCidade().getId());
            }
        }
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
            Integer grupo_cidade_id = null;
            SorteioStatus ss = (SorteioStatus) new Dao().find(new SorteioStatus(), idStatus);
            if (ss != null) {
                if (ss.getGrupoCidade().getId() != -1) {
                    grupo_cidade_id = ss.getGrupoCidade().getId();
                }
            }
            Pessoa pessoa = new SorteioMovimentoDao().sort(sorteio.getId(), grupo_cidade_id);
            SorteioMovimento sorteioMovimento = new SorteioMovimento();
            sorteioMovimento.setOperador((Usuario) GenericaSessao.getObject("sessaoUsuario"));
            sorteioMovimento.setSorteio(sorteio);
            sorteioMovimento.setDtSorteio(new Date());
            sorteioMovimento.setPessoa(pessoa);
            sorteioMovimento.setGrupoCidade(sorteioStatus.getGrupoCidade());
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

    public SorteioStatus getSorteioStatus() {
        return sorteioStatus;
    }

    public void setSorteioStatus(SorteioStatus sorteioStatus) {
        this.sorteioStatus = sorteioStatus;
    }

    public List<SelectItem> getListStatus() {
        return listStatus;
    }

    public void setListStatus(List<SelectItem> listStatus) {
        this.listStatus = listStatus;
    }

    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }

}
