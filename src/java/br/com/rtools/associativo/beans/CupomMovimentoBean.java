package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Cupom;
import br.com.rtools.associativo.CupomCategoria;
import br.com.rtools.associativo.CupomMovimento;
import br.com.rtools.associativo.dao.CupomDao;
import br.com.rtools.associativo.dao.CupomMovimentoDao;
import br.com.rtools.associativo.lista.SociosCupomMovimento;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.db.FunctionsDao;
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
public class CupomMovimentoBean implements Serializable {

    private CupomMovimento cupomMovimento;
    private Cupom cupom;
    private List<SelectItem> listCupom;
    private Boolean historico;
    private Integer idCupom;
    private List<CupomMovimento> listCupomMovimento;
    private Pessoa pessoa;
    private List<SociosCupomMovimento> sociosCupomMovimento;

    @PostConstruct
    public void init() {
        pessoa = new Pessoa();
        cupomMovimento = new CupomMovimento();
        cupom = new Cupom();
        listCupom = new ArrayList<>();
        listCupomMovimento = new ArrayList<>();
        sociosCupomMovimento = new ArrayList<>();
        historico = false;
        loadListCupom();
        loadCupom();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("inCategoriaSocio");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("cupomMovimentoBean");
    }

    public void loadCupom() {
        if (idCupom != null) {
            cupom = new Cupom();
            cupomMovimento = new CupomMovimento();
            cupom = (Cupom) new Dao().find(new Cupom(), idCupom);
            if (cupom == null) {
                cupom = new Cupom();
            }
            loadListCupomMovimento();
        }
    }

    public void loadListCupom() {
        idCupom = null;
        listCupom = new ArrayList<>();
        cupom = new Cupom();
        CupomDao cupomDao = new CupomDao();
        List<Cupom> list = cupomDao.findByHistorico(this.historico);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCupom = list.get(i).getId();
                cupom = list.get(i);
            }
            listCupom.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao() + " - Data: " + list.get(i).getData()));
        }
        loadCupom();
        loadListCupomMovimento();
    }

    public void loadListCupomMovimento() {
        listCupomMovimento = new ArrayList();
        listCupomMovimento = new CupomMovimentoDao().findByCupom(this.cupom.getId());
    }

    public void loadSocios() {
        if (pessoa.getId() != -1 && idCupom != null) {
            sociosCupomMovimento = new ArrayList<>();
            sociosCupomMovimento = new CupomMovimentoDao().findAllSocios(idCupom, pessoa.getId());
        }
    }

    public Integer getIdCupom() {
        return idCupom;
    }

    public void setIdCupom(Integer idCupom) {
        this.idCupom = idCupom;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    public void save() {
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar uma pessoa");
            return;
        }
        if (idCupom == null) {
            GenericaMensagem.warn("Validação", "Cadastrar cupons!");
            return;
        }
        if (new FunctionsDao().inadimplente(pessoa.getId(), cupom.getCarenciaInadimplenciaDias())) {
            GenericaMensagem.warn("Validação", "Consta inadimplência acima de " + cupom.getCarenciaInadimplenciaDias() + " dia(s)");
            return;
        }
        Dao dao = new Dao();
        CupomMovimento cm = new CupomMovimento();
        NovoLog novoLog = new NovoLog();
        novoLog.saveList();
        Cupom c = (Cupom) dao.find(new Cupom(), idCupom);
        dao.openTransaction();
        Boolean success = false;
        for (int i = 0; i < sociosCupomMovimento.size(); i++) {
            if (!sociosCupomMovimento.get(i).getDisabled() && sociosCupomMovimento.get(i).getSelected()) {
                cm.setDtEmissao(new Date());
                cm.setOperador(Usuario.getUsuario());
                cm.setPessoa(sociosCupomMovimento.get(i).getPessoa());
                cm.setCupom(c);
                cm.setCodigo(sociosCupomMovimento.get(i).getCodigoCupom());
                if (dao.save(cm)) {
                    GenericaMensagem.info("Sucesso", "Registro inserido " + sociosCupomMovimento.get(i).getPessoa().getNome());
                    loadListCupomMovimento();
                    novoLog.setTabela("eve_cupom_movimento");
                    novoLog.setCodigo(cm.getId());
                    novoLog.save("ID: " + cm.getId() + " - Cupom: (" + cm.getCupom().getId() + ") " + cm.getCupom().getDescricao() + " - Pessoa: (" + cm.getPessoa().getId() + ") " + cm.getPessoa().getNome() + " - CPF: " + cm.getPessoa().getDocumento() + " - Data: " + cm.getEmissao(), true);
                }
                success = true;
                cm = new CupomMovimento();
            }
        }
        if (success) {
            dao.commit();
            novoLog.saveList();
            loadSocios();
            loadListCupomMovimento();
        } else {
            dao.rollback();
        }
    }

    public void delete(CupomMovimento cm) {
        if (new Dao().delete(cm, true)) {
            loadListCupomMovimento();
            GenericaMensagem.info("Sucesso", "Registro removido!");
            NovoLog novoLog = new NovoLog();
            novoLog.delete("ID: " + cm.getId() + " - Cupom: (" + cm.getCupom().getId() + ") " + cm.getCupom().getDescricao() + " - Pessoa: (" + cm.getPessoa().getId() + ") " + cm.getPessoa().getNome() + " - CPF: " + cm.getPessoa().getDocumento() + " - Data: " + cm.getEmissao());
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public List<CupomMovimento> getListCupomMovimento() {
        return listCupomMovimento;
    }

    public void setListCupomMovimento(List<CupomMovimento> listCupomMovimento) {
        this.listCupomMovimento = listCupomMovimento;
    }

    public List<SelectItem> getListCupom() {
        return listCupom;
    }

    public void setListCupom(List<SelectItem> listCupom) {
        this.listCupom = listCupom;
    }

    public Boolean getHistorico() {
        return historico;
    }

    public void setHistorico(Boolean historico) {
        this.historico = historico;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            loadSocios();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public CupomMovimento getCupomMovimento() {
        return cupomMovimento;
    }

    public void setCupomMovimento(CupomMovimento cupomMovimento) {
        this.cupomMovimento = cupomMovimento;
    }

    public void putCategoria() {
        if (cupom != null) {
            if (cupom.getId() != null) {
                String inCategoriaSocio = "";
                List<CupomCategoria> list = cupom.getListCupomCategoria();
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        inCategoriaSocio = "" + list.get(i).getCategoria().getId();
                    } else {
                        inCategoriaSocio += "," + list.get(i).getCategoria().getId();

                    }
                }
                if (!inCategoriaSocio.isEmpty()) {
                    GenericaSessao.put("inCategoriaSocio", inCategoriaSocio);
                }
            }
        }
    }

    public List<SociosCupomMovimento> getSociosCupomMovimento() {
        return sociosCupomMovimento;
    }

    public void setSociosCupomMovimento(List<SociosCupomMovimento> sociosCupomMovimento) {
        this.sociosCupomMovimento = sociosCupomMovimento;
    }

    public void loadListCupomMovimento(Integer pessoa_id) {
        listCupomMovimento = new CupomMovimentoDao().findByPessoa(pessoa_id);
    }

}
