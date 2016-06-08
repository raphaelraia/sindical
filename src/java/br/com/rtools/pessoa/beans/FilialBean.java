package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.FilialCidade;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.db.FilialCidadeDBToplink;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DaoInterface;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.CellEditEvent;

@ManagedBean
@SessionScoped
public class FilialBean {

    private Filial filial;
    private Filial filialSubsede;
    private List<Filial> listaFilial;
    private int idFilial;
    // private List<DataObject> listaCidade;
    private List<CidadesAux> listCidadesAux;
    private boolean adicionarLista;
    private List<SelectItem> listFilialSelectItem;
    private List<SelectItem> listFilialSelectItemSub;
    private Boolean showModal;
    private Juridica juridica;
    private Cidade selectedCidade;
    private List<FilialCidade> listFilialCidade;
    private Integer idFilialSub;

    @PostConstruct
    public void init() {
        filial = new Filial();
        filialSubsede = new Filial();
        juridica = new Juridica();
        listaFilial = new ArrayList();
        listCidadesAux = new ArrayList();
        idFilial = 0;
        // listaCidade = new ArrayList();
        adicionarLista = false;
        listFilialSelectItem = new ArrayList();
        listFilialSelectItemSub = new ArrayList();
        showModal = false;
        listFilialCidade = new ArrayList();
        idFilialSub = null;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("filialBean");
        GenericaSessao.remove("juridicaPesquisa");
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        Dao dao = new Dao();
        Filial fx = (Filial) dao.find(new Filial(), listaFilial.get(event.getRowIndex()).getId());
        fx.setQuantidadeAgendamentosPorEmpresa((Integer) newValue);

        dao.openTransaction();
        if (!dao.update(fx)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível atualizar filial");
            return;
        }
        dao.commit();
        GenericaMensagem.info("Sucesso", "Filial Atualizada!");
    }

    public void updateFilial(Filial filialx) {

        Dao dao = new Dao();

        dao.openTransaction();
        if (!dao.update(filialx)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Não foi possível atualizar filial");
            return;
        }

        dao.commit();

    }

    public void removerFilial() {
        filial = new Filial();
    }

    public void clear() {
        GenericaSessao.remove("filialBean");
    }

    public Filial getFilial() {
        if (!showModal) {
            if (GenericaSessao.exists("juridicaPesquisa")) {
                filial.setFilial((Juridica) GenericaSessao.getObject("juridicaPesquisa", true));
            }
        }
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public void btnAddFilial() {

        FilialDao db = new FilialDao();

        if (!db.pesquisaFilialExiste(filial.getFilial().getId()).isEmpty()) {
            GenericaMensagem.warn("Erro", "Filial já existe!");
            return;
        }

        DaoInterface di = new Dao();
        NovoLog novoLog = new NovoLog();
        di.openTransaction();
        if (filial.getId() == -1) {
            filial.setMatriz((Juridica) di.find(new Juridica(), 1));
            if (di.save(filial)) {
                novoLog.save(
                        "ID: " + filial.getId()
                        + " - Filial: " + filial.getFilial().getPessoa().getNome()
                );
                GenericaMensagem.info("Sucesso", "Registro adicionado com sucesso");
                di.commit();
            } else {
                GenericaMensagem.warn("Erro", "Falha ao adicionar a filial!");
                di.rollback();
            }
            filial = new Filial();
            adicionarLista = true;
        } else {
            Filial f = (Filial) di.find(filial);
            String beforeUpdate
                    = "ID: " + f.getId()
                    + " - Filial: " + f.getFilial().getPessoa().getNome();
            if (di.update(filial)) {
                novoLog.update(beforeUpdate,
                        "ID: " + filial.getId()
                        + " - Filial: " + filial.getFilial().getPessoa().getNome()
                );
                GenericaMensagem.info("Sucesso", "Registro atualizado com sucesso");
                di.commit();
            } else {
                GenericaMensagem.warn("Erro", "Falha ao atualizar a filial!");
                di.rollback();
            }
        }
        listaFilial.clear();
        filial = new Filial();
    }

    public void saveCidadeFilial(Cidade cid, Integer filial_id) {
        FilialCidadeDBToplink db = new FilialCidadeDBToplink();
        FilialCidade filialCidade;
        NovoLog novoLog = new NovoLog();
        DaoInterface di = new Dao();
        int iCidade = cid.getId();
        List<FilialCidade> list = db.findListBy(cid.getId(), false);
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(filial_id, list.get(i).getFilial().getId())) {
                GenericaMensagem.warn("Sucesso", "Filial já está cadastrada! Consulte as subsedes.");
                return;
            }
        }
        if (filial_id != -1) {
            filialCidade = db.pesquisaFilialPorCidade(iCidade);
            if (filialCidade.getId() != -1) {
                filialCidade.setFilial((Filial) di.find(new Filial(), filial_id));
                filialCidade.setPrincipal(true);
                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
                if (di.update(filialCidade, true)) {
                    novoLog.update("", "Cidade Filial - "
                            + "ID: " + filialCidade.getId()
                            + " - Filial: (" + filialCidade.getFilial().getId() + ") " + filialCidade.getFilial().getFilial().getPessoa().getNome()
                            + " - Cidade: (" + filialCidade.getCidade().getId() + ") " + filialCidade.getCidade().getCidade()
                    );
                }
            } else {
                filialCidade = new FilialCidade();
                filialCidade.setCidade((Cidade) di.find(new Cidade(), iCidade));
                filialCidade.setFilial((Filial) di.find(new Filial(), filial_id));
                filialCidade.setPrincipal(true);
                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
                if (di.save(filialCidade, true)) {
                    novoLog.save("Cidade Filial - "
                            + "ID: " + filialCidade.getId()
                            + " - Filial: (" + filialCidade.getFilial().getId() + ") " + filialCidade.getFilial().getFilial().getPessoa().getNome()
                            + " - Cidade: (" + filialCidade.getCidade().getId() + ") " + filialCidade.getCidade().getCidade()
                    );
                }
            }
        } else {
            filialCidade = db.pesquisaFilialPorCidade(iCidade);
            filialCidade.setPrincipal(true);
            if (filialCidade.getId() != -1) {
                if (di.delete(filialCidade, true)) {
                    novoLog.save("Cidade Filial - "
                            + "ID: " + filialCidade.getId()
                            + " - Filial: (" + filialCidade.getFilial().getId() + ") " + filialCidade.getFilial().getFilial().getPessoa().getNome()
                            + " - Cidade: (" + filialCidade.getCidade().getId() + ") " + filialCidade.getCidade().getCidade()
                    );
                }
                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
            }
        }
        listFilialSelectItem = new ArrayList();

    }

    public String novo() {
        filial = new Filial();
        return "filial";
    }

    public void delete(Filial fi) {
        if (fi.getId() != -1) {
            NovoLog novoLog = new NovoLog();
            DaoInterface di = new Dao();
            di.openTransaction();
            if (di.delete(fi)) {
                novoLog.delete(
                        "ID: " + fi.getId()
                        + " - Filial: " + fi.getFilial().getPessoa().getNome()
                );
                GenericaMensagem.info("Sucesso", "Filial excluída com sucesso");
                listaFilial.clear();
                listFilialSelectItem.clear();
                filial = new Filial();
                getListaFilialSemMatriz().clear();
                di.commit();
            } else {
                GenericaMensagem.warn("Erro", "Não foi possível excluir essa filial. Verifique se há vínculos!");
                listaFilial.clear();
                filial = new Filial();
                di.rollback();
            }
        }
    }

    public List<Filial> getListaFilial() {
        listaFilial = new Dao().list(new Filial(), true);
        return listaFilial;
    }

    public List<Filial> getListaFilialSemMatriz() {
        if (listaFilial.isEmpty()) {
            Dao dao = new Dao();
            listaFilial = dao.list(new Filial(), true);
        }
        return listaFilial;
    }

    public int getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(int idFilial) {
        this.idFilial = idFilial;
    }
//
//    public List<DataObject> getListaCidade() {
//        if (listaCidade.isEmpty()) {
//            GrupoCidadesDB dbCids = new GrupoCidadesDao();
//            //List<GrupoCidades> lis = dbCids.pesquisaTodos();
//            List<Cidade> lis = dbCids.pesquisaCidadesBase();
//
//            DaoInterface di = new Dao();
//            List<FilialCidade> fc = (List<FilialCidade>) di.list(new FilialCidade());
//
//            if (!lis.isEmpty()) {
//                boolean tem;
//                for (int i = 0; i < lis.size(); i++) {
//                    tem = false;
//                    for (int w = 0; w < fc.size(); w++) {
//                        if (lis.get(i).getId() == fc.get(w).getCidade().getId()) {
//                            for (int u = 0; u < getResult().size(); u++) {
//                                if (Objects.equals(fc.get(w).getFilial().getId(), Integer.valueOf(listFilialSelectItem.get(u).getDescription()))) {
//                                    listaCidade.add(new DataObject((Cidade) di.find(new Cidade(), lis.get(i).getId()), u));
//                                    tem = true;
//                                }
//                                if (tem) {
//                                    break;
//                                }
//                            }
//                            if (tem) {
//                                break;
//                            }
//                        }
//                        if (tem) {
//                            break;
//                        }
//                    }
//                    if (!tem) {
//                        listaCidade.add(new DataObject((Cidade) di.find(new Cidade(), lis.get(i).getId()), 0));
//                    }
//                }
//            }
//        }
//        return listaCidade;
//    }
//
//    public void setListaCidade(List<DataObject> listaCidade) {
//        this.listaCidade = listaCidade;
//    }

    public List<SelectItem> getListFilialSelectItem() {
        if ((listFilialSelectItem.isEmpty()) || (this.adicionarLista)) {
            listFilialSelectItem.clear();
            List<Filial> fi = new Dao().list(new Filial(), true);
            listFilialSelectItem.add(new SelectItem(-1, " -- NENHUM -- "));
            for (int i = 0; i < fi.size(); i++) {
                listFilialSelectItem.add(new SelectItem(fi.get(i).getId(), fi.get(i).getFilial().getPessoa().getNome()));
            }
            this.adicionarLista = false;
        }
        return listFilialSelectItem;
    }

    public void setListFilialSelectItem(List<SelectItem> listFilialSelectItem) {
        this.listFilialSelectItem = listFilialSelectItem;
    }

    public void loadListFilialSelectItemSub() {
        listFilialSelectItemSub = new ArrayList();
        FilialCidadeDBToplink filialCidadeDao = new FilialCidadeDBToplink();
        FilialCidade fc = filialCidadeDao.findPrincipal(selectedCidade.getId());
        List<Filial> list = new Dao().list(new Filial(), true);
        if (fc != null) {
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getId(), fc.getFilial().getId())) {
                    list.remove(i);
                    break;
                }
            }
        }
        if (!listFilialCidade.isEmpty()) {
            for (int y = 0; y < list.size(); y++) {
                for (int i = 0; i < listFilialCidade.size(); i++) {
                    if (Objects.equals(list.get(y).getId(), listFilialCidade.get(i).getFilial().getId())) {
                        list.remove(y);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilialSub = list.get(i).getId();
            }
            listFilialSelectItemSub.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public List<SelectItem> getListFilialSelectItemSub() {
        return listFilialSelectItemSub;
    }

    public void setListFilialSelectItemSub(List<SelectItem> listFilialSelectItemSub) {
        this.listFilialSelectItemSub = listFilialSelectItemSub;
    }

    public Boolean getShowModal() {
        return showModal;
    }

    public void setShowModal(Boolean showModal) {
        this.showModal = showModal;
    }

    public void loadListFilialCidade(Cidade c, Boolean principal) {
        FilialCidadeDBToplink filialCidadeDao = new FilialCidadeDBToplink();
        listFilialCidade = new ArrayList();
        listFilialCidade = filialCidadeDao.findListBy(c.getId(), principal);
    }

    public void openModal(Cidade c) {
        filialSubsede = new Filial();
        showModal = true;
        selectedCidade = c;
        juridica = new Juridica();
        loadListFilialCidade(c, false);
        loadListFilialSelectItemSub();
    }

    public void closeModal() {
        filialSubsede = new Filial();
        listFilialCidade = new ArrayList();
        showModal = false;
        selectedCidade = null;
        idFilialSub = null;
    }

    public Juridica getJuridica() {
        if (showModal) {
            if (GenericaSessao.exists("juridicaPesquisa")) {
                juridica = (Juridica) GenericaSessao.getObject("juridicaPesquisa", true);
            }
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public void saveSubsede() {
        if (listFilialSelectItemSub.isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar filiais!");
            return;
        }
        FilialCidadeDBToplink filialCidadeDao = new FilialCidadeDBToplink();
        FilialCidade fc = filialCidadeDao.find(juridica.getId(), selectedCidade.getId());
        if (fc != null) {
            GenericaMensagem.warn("Validação", "Filial já cadastrada para esta cidade!");
            return;
        }
        filialSubsede.setCentroCusto(0);
        filialSubsede.setMatriz(new Registro().getRegistroEmpresarial().getFilial());
        Dao dao = new Dao();
        dao.openTransaction();
        fc = new FilialCidade();
        fc.setFilial((Filial) dao.find(new Filial(), idFilialSub));
        fc.setCidade(selectedCidade);
        fc.setPrincipal(false);
        if (!dao.save(fc)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao inserir Filial!");
            return;
        }
        dao.commit();
        GenericaMensagem.info("Sucesso", "Registro inserido");
        loadListFilialCidade(selectedCidade, false);
        loadListFilialSelectItemSub();
        juridica = new Juridica();
    }

    public void deleteSubsede(FilialCidade fc) {
        Filial f = fc.getFilial();
        Dao dao = new Dao();
        if (!dao.delete(fc, true)) {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
            return;
        }
        GenericaMensagem.info("Sucesso", "Registro removido");
        loadListFilialCidade(selectedCidade, false);
        loadListFilialSelectItemSub();
    }

    public List<CidadesAux> getListCidadesAux() {
        if (listCidadesAux.isEmpty()) {
            GrupoCidadesDao dbCids = new GrupoCidadesDao();
            //List<GrupoCidades> lis = dbCids.pesquisaTodos();
            List<Cidade> lis = dbCids.pesquisaCidadesBase();

            DaoInterface di = new Dao();
            List<FilialCidade> fc = (List<FilialCidade>) di.list(new FilialCidade());

            if (!lis.isEmpty()) {
                boolean tem;
                for (int i = 0; i < lis.size(); i++) {
                    tem = false;
                    for (int w = 0; w < fc.size(); w++) {
                        if (lis.get(i).getId() == fc.get(w).getCidade().getId()) {
                            for (int u = 0; u < getListFilialSelectItem().size(); u++) {
                                try {
                                    Integer filial_id = Integer.parseInt(listFilialSelectItem.get(u).getValue().toString());
                                    if (Objects.equals(fc.get(w).getFilial().getId(), filial_id)) {
                                        listCidadesAux.add(new CidadesAux(filial_id, (Cidade) di.find(new Cidade(), lis.get(i).getId())));
                                        tem = true;
                                    }
                                } catch (Exception e) {
                                    e.getCause();
                                }
                                if (tem) {
                                    break;
                                }
                            }
                            if (tem) {
                                break;
                            }
                        }
                        if (tem) {
                            break;
                        }
                    }
                    if (!tem) {
                        listCidadesAux.add(new CidadesAux(-1, (Cidade) di.find(new Cidade(), lis.get(i).getId())));
                    }
                }
            }
        }
        return listCidadesAux;
    }

    public void setListCidadesAux(List<CidadesAux> listCidadesAux) {
        this.listCidadesAux = listCidadesAux;
    }

    public Cidade getSelectedCidade() {
        return selectedCidade;
    }

    public void setSelectedCidade(Cidade selectedCidade) {
        this.selectedCidade = selectedCidade;
    }

    public List<FilialCidade> getListFilialCidade() {
        return listFilialCidade;
    }

    public void setListFilialCidade(List<FilialCidade> listFilialCidade) {
        this.listFilialCidade = listFilialCidade;
    }

    public Filial getFilialSubsede() {
        return filialSubsede;
    }

    public void setFilialSubsede(Filial filialSubsede) {
        this.filialSubsede = filialSubsede;
    }

    public Integer getIdFilialSub() {
        return idFilialSub;
    }

    public void setIdFilialSub(Integer idFilialSub) {
        this.idFilialSub = idFilialSub;
    }

    public class CidadesAux {

        private Integer index;
        private Cidade cidade;
        private List<SelectItem> listFilialSelectItem;
        private Integer idFilial;
        private List<FilialCidade> listFilialCidade;

        public CidadesAux() {
            this.index = null;
            this.cidade = null;
            this.listFilialSelectItem = new ArrayList();
            this.idFilial = null;
            this.listFilialCidade = new ArrayList();
        }

        public CidadesAux(Integer index, Cidade cidade) {
            this.index = index;
            this.cidade = cidade;
        }

        public CidadesAux(Integer index, Cidade cidade, List<SelectItem> listFilialSelectItem, Integer idFilial, List<FilialCidade> listFilialCidade) {
            this.index = index;
            this.cidade = cidade;
            this.listFilialSelectItem = listFilialSelectItem;
            this.idFilial = idFilial;
            this.listFilialCidade = listFilialCidade;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Cidade getCidade() {
            return cidade;
        }

        public void setCidade(Cidade cidade) {
            this.cidade = cidade;
        }

        public List<SelectItem> getListFilialSelectItem() {
            if (listFilialSelectItem.isEmpty() || adicionarLista) {
                if (cidade.getId() != -1) {
                    listFilialSelectItem.clear();
                    List<Filial> list = new Dao().list(new Filial(), true);
                    FilialCidadeDBToplink filialCidadeDao = new FilialCidadeDBToplink();
                    List<FilialCidade> listFilialCidade = filialCidadeDao.findListBy(cidade.getId());
                    for (int i = 0; i < list.size(); i++) {
                        for (int y = 0; y < listFilialCidade.size(); y++) {
                            if (Objects.equals(listFilialCidade.get(y).getFilial().getId(), list.get(i).getId())) {
                                list.remove(i);
                                break;
                            }
                        }
                    }
                    listFilialSelectItem.add(new SelectItem(-1, " -- NENHUM -- "));
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            idFilial = list.get(i).getId();
                        }
                        listFilialSelectItem.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
                    }
                }
            }
            return listFilialSelectItem;
        }

        public void setListFilialSelectItem(List<SelectItem> listFilialSelectItem) {
            this.listFilialSelectItem = listFilialSelectItem;
        }

        public Integer getIdFilial() {
            return idFilial;
        }

        public void setIdFilial(Integer idFilial) {
            this.idFilial = idFilial;
        }

        public List<FilialCidade> getListFilialCidade() {
            if (cidade.getId() != -1) {
                FilialCidadeDBToplink filialCidadeDBToplink = new FilialCidadeDBToplink();
                listFilialCidade = filialCidadeDBToplink.findListBy(cidade.getId(), false);

            }
            return listFilialCidade;
        }

        public void setListFilialCidade(List<FilialCidade> listFilialCidade) {
            this.listFilialCidade = listFilialCidade;
        }

    }

}
