//package br.com.rtools.associativo.beans;
//
//import br.com.rtools.associativo.Cupom;
//import br.com.rtools.associativo.CupomMovimento;
//import br.com.rtools.associativo.Sorteio;
//import br.com.rtools.associativo.SorteioMovimento;
//import br.com.rtools.associativo.SorteioStatus;
//import br.com.rtools.associativo.dao.CupomDao;
//import br.com.rtools.associativo.dao.SorteioMovimentoDao;
//import br.com.rtools.associativo.dao.SorteioStatusDao;
//import br.com.rtools.logSistema.NovoLog;
//import br.com.rtools.pessoa.Pessoa;
//import br.com.rtools.seguranca.Usuario;
//import br.com.rtools.utilitarios.Dao;
//import br.com.rtools.utilitarios.GenericaMensagem;
//import br.com.rtools.utilitarios.GenericaSessao;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
//import javax.faces.model.SelectItem;
//
//@ManagedBean
//@SessionScoped
//public class CupomMovimentoBean {
//
//    private Cupom cupom;
//    private SorteioStatus sorteioStatus;
//    private List<SelectItem> listSorteio;
//    private Boolean historico;
//    private Integer idCupom;
//    private List<CupomMovimento> listCupomMovimento;
//    private Pessoa pessoa;
//
//    @PostConstruct
//    public void init() {
//        listSorteio = new ArrayList<>();
//        listCupomMovimento = new ArrayList<>();
//        cupom = new Cupom();
//        sorteioStatus = new SorteioStatus();
//        historico = false;
//        loadListCupons();
//    }
//
//    @PreDestroy
//    public void destroy() {
//        GenericaSessao.remove("fisicaPesquisa");
//        GenericaSessao.remove("cupomMovimentoBean");
//    }
//
//    public void loadListCupons() {
//        listSorteio = new ArrayList<>();
//        cupom = new Cupom();
//        CupomDao cupomDao = new CupomDao();
//        List<Sorteio> list = cupomDao.findByHistorico(this.historico);
//        for (int i = 0; i < list.size(); i++) {
//            if (i == 0) {
//                idSorteio = list.get(i).getId();
//                sorteio = list.get(i);
//            }
//            String descricao = "";
//            descricao += "De " + list.get(i).getInicioString();
//            if (list.get(i).getDtFim() != null) {
//                descricao += " até " + list.get(i).getFimString();
//            }
//            descricao += " - " + list.get(i).getDescricao();
//            listSorteio.add(new SelectItem(list.get(i).getId(), descricao));
//        }
//        loadListCupomMovimento();
//        sorteioStatus = new SorteioStatusDao().findBySorteio(cupom.getId());
//    }
//
//    public void loadListCupomMovimento() {
//        listCupomMovimento = new ArrayList();
//        listCupomMovimento = new SorteioMovimentoDao().findBySorteio(this.cupom.getId());
//    }
//
//    public void loadCupom() {
//        cupom = new Cupom();
//        cupom = (Cupom) new Dao().find(new Sorteio(), idCupom);
//        loadListCupomMovimento();
//        new SorteioStatusDao().findBySorteio(cupom.getId());
//    }
//
//    public Integer getIdCupom() {
//        return idCupom;
//    }
//
//    public void setIdCupom(Integer idCupom) {
//        this.idCupom = idCupom;
//    }
//
//    public Cupom getCupom() {
//        return cupom;
//    }
//
//    public void setCupom(Cupom cupom) {
//        this.cupom = cupom;
//    }
//
//    public void save() {
//        if (pessoa == null) {
//            GenericaMensagem.warn("Validação", "Pesquisar uma pessoa");
//            return;
//        }
//        if (cupom.getId() != null) {
//            Pessoa pessoa = new SorteioMovimentoDao().sort(cupom.getId());
//            SorteioMovimento sorteioMovimento = new SorteioMovimento();
//            sorteioMovimento.setOperador((Usuario) GenericaSessao.getObject("sessaoUsuario"));
//            sorteioMovimento.setSorteio(sorteio);
//            sorteioMovimento.setDtSorteio(new Date());
//            sorteioMovimento.setPessoa(pessoa);
//        }
//        if (new Dao().save(cupom, true)) {
//            pessoa = null;
//            listCupomMovimento = new ArrayList();
//            listCupomMovimento = new CupomMovimentoDao().findByCupom(this.cupom.getId());
//            GenericaMensagem.warn("Sucesso", "Sorteio realizado");
//        } else {
//            GenericaMensagem.warn("Erro", "Ao realizar processo!");
//        }
//    }
//
//    public void delete(CupomMovimento cm) {
//        if (new Dao().delete(cm, true)) {
//            listCupomMovimento = new ArrayList();
//            listCupomMovimento = new CupomMovimentoDao().findByCupom(this.cupom.getId());
//            GenericaMensagem.warn("Sucesso", "Registro removido!");
//            NovoLog novoLog = new NovoLog();
//            novoLog.delete("ID: " + cm.getId() + " - Cupom: (" + cm.getCupom().getId() + ") " + cm.getCupom().getDescricao() + " - Pessoa: (" + cm.getPessoa().getId() + ") " + cm.getPessoa().getNome() + " - CPF: " + cm.getPessoa().getDocumento() + " - Data: " + cm.getEmissao());
//        } else {
//            GenericaMensagem.warn("Erro", "Ao remover sorteado!");
//        }
//    }
//
//    public List<SorteioMovimento> getListSorteioMovimento() {
//        return listCupomMovimento;
//    }
//
//    public void setListSorteioMovimento(List<SorteioMovimento> listCupomMovimento) {
//        this.listCupomMovimento = listCupomMovimento;
//    }
//
//    public List<SelectItem> getListSorteio() {
//        return listSorteio;
//    }
//
//    public void setListSorteio(List<SelectItem> listSorteio) {
//        this.listSorteio = listSorteio;
//    }
//
//    public Boolean getHistorico() {
//        return historico;
//    }
//
//    public void setHistorico(Boolean historico) {
//        this.historico = historico;
//    }
//
//    public Pessoa getPessoa() {
//        return pessoa;
//    }
//
//    public void setPessoa(Pessoa pessoa) {
//        this.pessoa = pessoa;
//    }
//
//}
