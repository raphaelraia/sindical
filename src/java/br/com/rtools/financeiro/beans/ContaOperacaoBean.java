package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.CentroCusto;
import br.com.rtools.financeiro.ContaOperacao;
import br.com.rtools.financeiro.ContaTipo;
import br.com.rtools.financeiro.Operacao;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.dao.CentroCustoDao;
import br.com.rtools.financeiro.dao.ContaOperacaoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
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

public class ContaOperacaoBean implements Serializable {

    private ContaOperacao contaOperacao;
    private ContaOperacao[] selectedContaOperacao;
    private List<ContaOperacao> listContaOperacao;
    private List<Plano5> listPlano5;
    private Plano5[] selectedPlano5;
    private String es;
    private Boolean contaFixa;
    private Boolean selectedAll;
    /**
     * [0] Operação | [1] Centro de Custo | [2] Filial | [3] Plano de Contas 5 -
     * Agrupado | [4] Centro de Custo (Edit)
     */
    private List<SelectItem>[] listSelectItem;
    private Integer[] index;
    private Plano5 plano5;
    private Boolean visiblePlano5;
    private List<SelectItem> listContaTipo;
    private Integer idContaTipo;

    @PostConstruct
    public void init() {
        contaOperacao = new ContaOperacao();
        selectedContaOperacao = null;
        listContaOperacao = new ArrayList<>();
        listPlano5 = new ArrayList<>();
        selectedPlano5 = null;
        listSelectItem = new ArrayList[]{
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
        };
        index = new Integer[]{0, 0, 0, 0, 0};
        es = "E";
        contaFixa = false;
        plano5 = null;
        selectedAll = false;
        visiblePlano5 = false;
        listContaTipo = new ArrayList();
        idContaTipo = 0;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("contaOperacaoBean");
    }

    public void clear() {
        GenericaSessao.remove("contaOperacaoBean");
    }

    public void clear(Integer type) {
        if (null != type) {
            switch (type) {
                case 1:
                    selectedAll = false;
                    index[1] = 0;
                    index[3] = 0;
                    listSelectItem[1] = new ArrayList();
                    listSelectItem[3] = new ArrayList();
                    listPlano5.clear();
                    listContaOperacao.clear();
                    break;
                case 4:
                    if (type == 4) {
                        listPlano5.clear();
                        listContaOperacao.clear();

                        contaOperacao = new ContaOperacao();
                        PF.closeDialog("dlg_co");
                        PF.update("form_co");
                    }
                    break;
                case 2:
                    selectedAll = false;
                    getListPlano5().clear();
                    getListContaOperacao().clear();
                    break;
                case 3:
                    getListContaOperacao().clear();
                    break;
                case 5:
                    index[1] = 0;
                    index[2] = 0;
                    Dao dao = new Dao();
                    contaOperacao.setOperacao((Operacao) dao.find(new Operacao(), Integer.parseInt(getListOperacoes().get(index[0]).getDescription())));
                    contaFixa = false;
                    break;
                case 6:
                    break;
                case 7:
                    Boolean sa = false;
                    selectedAll = !selectedAll;
                    for (int i = 0; i < listPlano5.size(); i++) {
                        listPlano5.get(i).setSelected(selectedAll);
                    }
                    break;
                case 8:
                    index[0] = 0;
                    index[1] = 0;
                    index[3] = 0;
                    listSelectItem[0] = new ArrayList();
                    listSelectItem[1] = new ArrayList();
                    listSelectItem[3] = new ArrayList();
                    listPlano5.clear();
                    listContaOperacao.clear();
                    break;
                default:
                    break;
            }
        }
    }

    public void setItem(Plano5 p) {
        contaOperacao = new ContaOperacao();
        contaOperacao.setPlano5(p);
        Dao dao = new Dao();
        contaOperacao.setOperacao((Operacao) dao.find(new Operacao(), Integer.parseInt(getListOperacoes().get(index[0]).getDescription())));
        PF.openDialog("dlg_co");
        PF.update("form_co:i_panel_co");
    }

    public void editPlano5(Plano5 p) {
        plano5 = p;
        visiblePlano5 = true;
        loadContaTipo();
        if (p.getContaTipo() == null) {
            idContaTipo = -1;
        } else {
            idContaTipo = p.getContaTipo().getId();
        }
    }

    public void closePlano5() {
        plano5 = null;
        idContaTipo = 0;
        listContaTipo = new ArrayList();
        visiblePlano5 = false;
    }

    public void updatePlano5() {
        if (idContaTipo == -1) {
            plano5.setContaTipo(null);
        } else {
            plano5.setContaTipo((ContaTipo) new Dao().find(new ContaTipo(), idContaTipo));
        }
        if (new Dao().update(plano5, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void save() {
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (getListFilial().isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar Filial");
            PF.update("form_co:i_message_co");
            return;
        }
        contaOperacao.setFilial((Filial) dao.find(new Filial(), Integer.parseInt(getListFilial().get(index[2]).getDescription())));
        if (getListCentroCusto().isEmpty()) {
            contaOperacao.setCentroCusto(null);
        } else if (contaOperacao.getId() == -1) {
            contaOperacao.setCentroCusto((CentroCusto) dao.find(new CentroCusto(), Integer.parseInt(getListCentroCusto().get(index[1]).getDescription())));
        } else {
            contaOperacao.setCentroCusto((CentroCusto) dao.find(new CentroCusto(), Integer.parseInt(getListCentroCusto().get(index[4]).getDescription())));
        }
        dao.openTransaction();
        if (contaOperacao.getId() == -1) {
            if (dao.save(contaOperacao)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Registro adicionado com sucesso");
                clear(2);
                PF.update("form_co:i_panel_co");
                PF.update("form_co:i_message_co");
                PF.update("form_co:i_tbl_co");
                novoLog.save("" + contaOperacao.toString());
            } else {
                GenericaMensagem.warn("Erro", "Ao adicionar registro!");
                PF.update("form_co:i_panel_co");
                PF.update("form_co:i_message_co");
                dao.rollback();
            }
        } else {
            ContaOperacao co = (ContaOperacao) dao.find(contaOperacao);
            if (dao.update(contaOperacao)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Registro atualizado com sucesso");
                clear(2);
                PF.update("form_co:i_panel_co");
                PF.update("form_co:i_message_co");
                PF.update("form_co:i_tbl_co");
                novoLog.update(contaOperacao.toString(), co.toString());
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizado registro!");
                PF.update("form_co:i_panel_co");
                PF.update("form_co:i_message_co");
                dao.rollback();
            }
        }
    }

    public void saveAll(Boolean selected) {
        Dao dao = new Dao();
        ContaOperacao cc = new ContaOperacao();
        Operacao o = (Operacao) dao.find(new Operacao(), Integer.parseInt(getListOperacoes().get(index[0]).getDescription()));
        if (getListFilial().isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar Filial");
            PF.update("form_co:i_message_co_todos");
            return;
        }
        CentroCusto centroCusto = null;
        Filial f = (Filial) dao.find(new Filial(), Integer.parseInt(getListFilial().get(index[2]).getDescription()));
        if (!getListCentroCusto().isEmpty()) {
            centroCusto = (CentroCusto) dao.find(new CentroCusto(), Integer.parseInt(getListCentroCusto().get(index[1]).getDescription()));
        }
        for (int i = 0; i < listPlano5.size(); i++) {
            if (listPlano5.get(i).getSelected() || selected) {
                cc.setPlano5(listPlano5.get(i));
                cc.setOperacao(o);
                cc.setFilial(f);
                cc.setCentroCusto(centroCusto);
                cc.setContaFixa(contaFixa);
                dao.save(cc, true);
                cc = new ContaOperacao();
            }
        }
        PF.update("form_co:i_panel_co_todos");
        PF.update("form_co:i_message_co_todos");
        PF.update("form_co:i_tbl_co");
        GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
    }

    public void editItem(ContaOperacao co) {
        getListCentroCusto().clear();
        index[4] = 0;
        contaOperacao = co;
        if (co.getCentroCusto() != null) {
            for (int i = 0; i < getListCentroCusto().size(); i++) {
                if (Integer.parseInt(getListCentroCusto().get(i).getDescription()) == co.getCentroCusto().getId()) {
                    index[4] = i;
                    break;
                }
            }
        }
        PF.openDialog("dlg_co");
        PF.update("form_co:i_panel_co");
    }

    public void removeItens() {
        boolean err = false;
        if (selectedContaOperacao == null) {
            GenericaMensagem.warn("Validação", "Nenhum item selecionado!");
            return;
        }
        if (selectedContaOperacao.length == 0) {
            GenericaMensagem.warn("Validação", "Nenhum item selecionado!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        for (int i = 0; i < selectedContaOperacao.length; i++) {
            for (int j = 0; j < listContaOperacao.size(); j++) {
                if (listContaOperacao.get(j).getId() == selectedContaOperacao[i].getId()) {
                    if (!dao.delete(listContaOperacao.get(j))) {
                        err = true;
                        break;
                    }
                }
            }
            if (err) {
                break;
            }
        }
        if (err) {
            GenericaMensagem.warn("Erro", "Ao excluir registros selecionados!");
            dao.rollback();
        } else {
            GenericaMensagem.info("Sucesso", "Registro(s) excluídos com sucesso");
            clear(2);
            PF.update("form_co");
            dao.commit();
        }
    }

    public void removeAllItens() {
        Dao dao = new Dao();
        dao.openTransaction();
        boolean err = false;
        for (int i = 0; i < listContaOperacao.size(); i++) {
            if (!dao.delete(listContaOperacao.get(i))) {
                err = true;
                break;
            }
        }
        if (err) {
            GenericaMensagem.warn("Erro", "Ao excluir registros selecionados!");
            dao.rollback();
        } else {
            GenericaMensagem.info("Sucesso", "Registro(s) excluídos com sucesso");
            selectedContaOperacao = null;
            clear(2);
            PF.update("form_co");
            dao.commit();
        }
    }

    public void updateContaOperacao(ContaOperacao co) {
        if (co.isContaFixa()) {
            co.setContaFixa(false);
        } else {
            co.setContaFixa(true);
        }
        Dao dao = new Dao();
        if (dao.update(co, true)) {
            clear(2);
            PF.update("form_co");
            GenericaMensagem.info("Sucesso", "Registro atualizado");
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar");
        }
    }

    public void removeItem(ContaOperacao co) {
        Dao dao = new Dao();
        if (dao.delete(co, true)) {
            clear(2);
            PF.update("form_co");
            GenericaMensagem.info("Sucesso", "Registro excluído");
        } else {
            GenericaMensagem.warn("Erro", "Ao excluir");
        }
    }

    /**
     * <strong>[0] Operações</strong>
     *
     * @return
     */
    public List<SelectItem> getListOperacoes() {
        if (listSelectItem[0].isEmpty()) {
            List<Operacao> list = (List<Operacao>) new Dao().list(new Operacao(), true);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    index[0] = i;
                }
                listSelectItem[0].add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
            }
            if (listSelectItem[0].isEmpty()) {
                listSelectItem[0] = new ArrayList<>();
            }
        }
        return listSelectItem[0];
    }

    /**
     * <strong>[1] Centro de Custo Contábil</strong>
     *
     * @return
     */
    public List<SelectItem> getListCentroCusto() {
        if (listSelectItem[1].isEmpty()) {
            try {
                Integer operacao_id = Integer.parseInt(listSelectItem[0].get(index[0]).getDescription());
                Operacao o = (Operacao) new Dao().find(new Operacao(), operacao_id);
                if (o.getCentroCusto()) {
                    Integer filial_id = Integer.parseInt(listSelectItem[2].get(index[2]).getDescription());
                    List<CentroCusto> list = new CentroCustoDao().findByFilial(filial_id);
                    index[1] = 0;
                    for (int i = 0; i < list.size(); i++) {
                        listSelectItem[1].add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
                    }
                }
            } catch (Exception e) {

            }
        }
        return listSelectItem[1];
    }

    /**
     * <strong>[2] Centro de Custo Contábil Sub</strong>
     *
     * @return
     */
    public List<SelectItem> getListFilial() {
        if (listSelectItem[2].isEmpty()) {
            List<Filial> list = (List<Filial>) new Dao().list(new Filial(), true);
            for (int i = 0; i < list.size(); i++) {
                listSelectItem[2].add(new SelectItem(i, list.get(i).getFilial().getPessoa().getNome(), "" + list.get(i).getId()));
            }
            if (listSelectItem[2].isEmpty()) {
                listSelectItem[2] = new ArrayList<>();
            }
            index[2] = 0;
        }
        return listSelectItem[2];
    }

    /**
     * Usando plano_vw
     *
     * @return
     */
    public List<SelectItem> getListPlano4Group() {
        if (listSelectItem[3].isEmpty()) {
            index[3] = 0;
            if (!getListOperacoes().isEmpty() && index[0] != null) {
                ContaOperacaoDao cod = new ContaOperacaoDao();
                List list = cod.listPlano4AgrupadoPlanoVwNotInContaOperacao(Integer.parseInt(getListOperacoes().get(index[0]).getDescription()));
                for (int i = 0; i < list.size(); i++) {
                    listSelectItem[3].add(new SelectItem(i, ((List) list.get(i)).get(1).toString(), ((List) list.get(i)).get(0).toString()));
                }
            }
            if (listSelectItem[3].isEmpty()) {
                listSelectItem[3] = new ArrayList<>();
            }
        }
        return listSelectItem[3];
    }

    public ContaOperacao getContaOperacao() {
        return contaOperacao;
    }

    public void setContaOperacao(ContaOperacao contaOperacao) {
        this.contaOperacao = contaOperacao;
    }

    public ContaOperacao[] getSelectedContaOperacao() {
        return selectedContaOperacao;
    }

    public void setSelectedContaOperacao(ContaOperacao[] selectedContaOperacao) {
        this.selectedContaOperacao = selectedContaOperacao;
    }

    public List<ContaOperacao> getListContaOperacao() {
        if (!getListOperacoes().isEmpty()) {
            try {
                if (!getListOperacoes().isEmpty() && !getListPlano4Group().isEmpty()) {
                    ContaOperacaoDao cod = new ContaOperacaoDao();
                    listContaOperacao = (List<ContaOperacao>) cod.findByContaOperacao(Integer.parseInt(getListFilial().get(index[2]).getDescription()), Integer.parseInt(getListOperacoes().get(index[0]).getDescription()), Integer.parseInt(getListPlano4Group().get(index[3]).getDescription()));
                }
            } catch (NumberFormatException e) {
                listContaOperacao.clear();
                return new ArrayList();
            }
        }
        return listContaOperacao;
    }

    public void setListContaOperacao(List<ContaOperacao> listContaOperacao) {
        this.listContaOperacao = listContaOperacao;
    }

    public List<Plano5> getListPlano5() {
        if (listPlano5.isEmpty()) {
            if (!getListOperacoes().isEmpty() && index[0] != null && index[3] != null) {
                ContaOperacaoDao cod = new ContaOperacaoDao();
                listPlano5 = (List<Plano5>) cod.findPlano5ByPlano4NotInContaOperacao(Integer.parseInt(getListFilial().get(index[2]).getDescription()), Integer.parseInt(getListOperacoes().get(index[0]).getDescription()), Integer.parseInt(getListPlano4Group().get(index[3]).getDescription()));
            }
        }
        if (listPlano5.isEmpty()) {
            selectedAll = false;
        }
        return listPlano5;
    }

    public void setListPlano5(List<Plano5> listPlano5) {
        this.listPlano5 = listPlano5;
    }

    public Plano5[] getSelectedPlano5() {
        return selectedPlano5;
    }

    public void setSelectedPlano5(Plano5[] selectedPlano5) {
        this.selectedPlano5 = selectedPlano5;
    }

    public Integer[] getIndex() {
        return index;
    }

    public void setIndex(Integer[] index) {
        this.index = index;
    }

    public Boolean getHabilitaTodos() {
        for (int i = 0; i < listPlano5.size(); i++) {
            if (listPlano5.get(i).getSelected()) {
                return true;
            }
        }
        return false;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public Boolean getContaFixa() {
        return contaFixa;
    }

    public void setContaFixa(Boolean contaFixa) {
        this.contaFixa = contaFixa;
    }

    public Boolean getSelectedAll() {
        return selectedAll;
    }

    public void setSelectedAll(Boolean selectedAll) {
        this.selectedAll = selectedAll;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public Boolean getVisiblePlano5() {
        return visiblePlano5;
    }

    public void setVisiblePlano5(Boolean visiblePlano5) {
        this.visiblePlano5 = visiblePlano5;
    }

    public void loadContaTipo() {
        List<ContaTipo> list = new Dao().list(new ContaTipo(), true);
        listContaTipo = new ArrayList();
        idContaTipo = -1;
        listContaTipo.add(new SelectItem(-1, "NENHUM TIPO"));
        for (int i = 0; i < list.size(); i++) {
            listContaTipo.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public List<SelectItem> getListContaTipo() {
        return listContaTipo;
    }

    public void setListContaTipo(List<SelectItem> listContaTipo) {
        this.listContaTipo = listContaTipo;
    }

    public Integer getIdContaTipo() {
        return idContaTipo;
    }

    public void setIdContaTipo(Integer idContaTipo) {
        this.idContaTipo = idContaTipo;
    }

}
