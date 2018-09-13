package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.FilialCidade;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.dao.FilialCidadeDao;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Types;
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
    private List<FilialCidadeAux> listFilialCidadeAux;
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
        listFilialCidadeAux = new ArrayList();
        idFilial = 0;
        adicionarLista = false;
        listFilialSelectItem = new ArrayList();
        listFilialSelectItemSub = new ArrayList();
        showModal = false;
        listFilialCidade = new ArrayList();
        idFilialSub = null;
        loadListFilialCidade();
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
        if (Types.isInteger(newValue)) {
            fx.setQuantidadeAgendamentosPorEmpresa((Integer) newValue);
        } else {
            fx.setApelido((String) newValue);
        }
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

        Dao di = new Dao();
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

    public void saveFilialCidade(FilialCidade fc, Cidade cid, Integer filial_id) {
        FilialCidadeDao db = new FilialCidadeDao();
        NovoLog novoLog = new NovoLog();
        Dao di = new Dao();
        int iCidade = cid.getId();
        if (fc == null || (Objects.equals(fc.getFilial().getFilial().getId(), filial_id))) {
            List<FilialCidade> list = db.findListBy(cid.getId(), false);
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(filial_id, list.get(i).getFilial().getId())) {
                    GenericaMensagem.warn("Sucesso", "Filial já está cadastrada! Consulte as subsedes.");
                    return;
                }
            }
        }
        FilialCidade filialCidade;
        if (fc == null && filial_id != null) {
            filialCidade = new FilialCidade();
            filialCidade.setCidade((Cidade) di.find(new Cidade(), iCidade));
            filialCidade.setFilial((Filial) di.find(new Filial(), filial_id));
            filialCidade.setPrincipal(true);
            if (di.save(filialCidade, true)) {
                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
                novoLog.save("Cidade Filial - "
                        + "ID: " + filialCidade.getId()
                        + " - Filial: (" + filialCidade.getFilial().getId() + ") " + filialCidade.getFilial().getFilial().getPessoa().getNome()
                        + " - Cidade: (" + filialCidade.getCidade().getId() + ") " + filialCidade.getCidade().getCidade()
                );
            } else {
                GenericaMensagem.warn("Erro", "Erro ao atualizar cidade!");
            }
        }
        if (fc != null && filial_id != null) {
            fc.setFilial((Filial) di.find(new Filial(), filial_id));
            fc.setPrincipal(true);
            if (di.update(fc, true)) {
                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
                novoLog.update("", "Cidade Filial - "
                        + "ID: " + fc.getId()
                        + " - Filial: (" + fc.getFilial().getId() + ") " + fc.getFilial().getFilial().getPessoa().getNome()
                        + " - Cidade: (" + fc.getCidade().getId() + ") " + fc.getCidade().getCidade()
                );
            } else {
                GenericaMensagem.warn("Erro", "Erro ao atualizar cidade!");
            }
        }
        if (fc != null && filial_id == null) {
            if (di.delete(fc, true)) {
                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
                novoLog.delete("Cidade Filial - "
                        + "ID: " + fc.getId()
                        + " - Filial: (" + fc.getFilial().getId() + ") " + fc.getFilial().getFilial().getPessoa().getNome()
                        + " - Cidade: (" + fc.getCidade().getId() + ") " + fc.getCidade().getCidade()
                );
            }

        }
//        if (filial_id != -1) {
//            filialCidade = db.pesquisaFilialPorCidade(iCidade);
//            if (filialCidade.getId() != -1) {
//            } else {
//            }
//        } else {
//            filialCidade = db.pesquisaFilialPorCidade(iCidade);
//            filialCidade.setPrincipal(true);
//            if (filialCidade.getId() != -1) {
//                if (di.delete(filialCidade, true)) {
//                    novoLog.save("Cidade Filial - "
//                            + "ID: " + filialCidade.getId()
//                            + " - Filial: (" + filialCidade.getFilial().getId() + ") " + filialCidade.getFilial().getFilial().getPessoa().getNome()
//                            + " - Cidade: (" + filialCidade.getCidade().getId() + ") " + filialCidade.getCidade().getCidade()
//                    );
//                }
//                GenericaMensagem.info("Sucesso", "Cidade atualizada com Sucesso!");
//            }
//        }
        listFilialSelectItem = new ArrayList();
        loadListFilialCidade();

    }

    public String novo() {
        filial = new Filial();
        return "filial";
    }

    public void delete(Filial fi) {
        if (fi.getId() != -1) {
            NovoLog novoLog = new NovoLog();
            Dao di = new Dao();
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

    public List<SelectItem> getListFilialSelectItem() {
        if ((listFilialSelectItem.isEmpty()) || (this.adicionarLista)) {
            listFilialSelectItem.clear();
            List<Filial> fi = new Dao().list(new Filial(), true);
            listFilialSelectItem.add(new SelectItem(null, " -- NENHUM -- "));
            for (int i = 0; i < fi.size(); i++) {
                listFilialSelectItem.add(new SelectItem(fi.get(i).getId(), fi.get(i).getFilial().getFantasia()));
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
        FilialCidadeDao filialCidadeDao = new FilialCidadeDao();
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
        FilialCidadeDao filialCidadeDao = new FilialCidadeDao();
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
        FilialCidadeDao filialCidadeDao = new FilialCidadeDao();
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

    public void loadListFilialCidade() {
        listFilialCidadeAux = new ArrayList();
        List<Cidade> listCidades = new GrupoCidadesDao().pesquisaCidadesBase();
        List<Filial> listFilial = new Dao().list(new Filial());
        FilialCidadeDao fcd = new FilialCidadeDao();
        for (int i = 0; i < listCidades.size(); i++) {
            boolean tem = false;
            for (int x = 0; x < listFilial.size(); x++) {
                FilialCidade fc = fcd.findByCidade(listCidades.get(i).getId(), listFilial.get(x).getId(), true);
                if (fc != null) {
                    tem = true;
                    listFilialCidadeAux.add(new FilialCidadeAux(fc, fc.getFilial().getId(), listCidades.get(i)));
                    break;
                }
            }
            if (!tem) {
                listFilialCidadeAux.add(new FilialCidadeAux(null, null, listCidades.get(i)));
            }
        }
    }

    public List<FilialCidadeAux> getListFilialCidadeAux() {
        return listFilialCidadeAux;
    }

    public void setListFilialCidadeAux(List<FilialCidadeAux> listFilialCidadeAux) {
        this.listFilialCidadeAux = listFilialCidadeAux;
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

    public class FilialCidadeAux {

        private FilialCidade fc;
        private Cidade cidade;
//        private List<SelectItem> listFilialSelectItem;
        private Integer idFilial;
        private List<FilialCidade> listFilialCidade;

        public FilialCidadeAux() {
            this.cidade = null;
//            this.listFilialSelectItem = new ArrayList();
            this.idFilial = null;
            this.listFilialCidade = new ArrayList();
            this.fc = null;
        }

        public FilialCidadeAux(FilialCidade fc, Integer idFilial, Cidade cidade) {
            this.fc = fc;
            this.idFilial = idFilial;
            this.cidade = cidade;
        }

//        public FilialCidadeAux(Cidade cidade, List<SelectItem> listFilialSelectItem, Integer idFilial, List<FilialCidade> listFilialCidade) {
//            this.cidade = cidade;
//            this.listFilialSelectItem = listFilialSelectItem;
//            this.idFilial = idFilial;
//            this.listFilialCidade = listFilialCidade;
//        }
        public Cidade getCidade() {
            return cidade;
        }

        public void setCidade(Cidade cidade) {
            this.cidade = cidade;
        }

//        public List<SelectItem> getListFilialSelectItem() {
//            if (listFilialSelectItem.isEmpty() || adicionarLista) {
//                if (cidade.getId() != -1) {
//                    listFilialSelectItem.clear();
//                    List<Filial> list = new Dao().list(new Filial(), true);
//                    FilialCidadeDao filialCidadeDao = new FilialCidadeDao();
//                    List<FilialCidade> listFilialCidade = filialCidadeDao.findListBy(cidade.getId());
//                    for (int i = 0; i < list.size(); i++) {
//                        for (int y = 0; y < listFilialCidade.size(); y++) {
//                            if (Objects.equals(listFilialCidade.get(y).getFilial().getId(), list.get(i).getId())) {
//                                list.remove(i);
//                                break;
//                            }
//                        }
//                    }
//                    listFilialSelectItem.add(new SelectItem(-1, " -- NENHUM -- "));
//                    for (int i = 0; i < list.size(); i++) {
//                        if (i == 0) {
//                            idFilial = list.get(i).getId();
//                        }
//                        listFilialSelectItem.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
//                    }
//                }
//            }
//            return listFilialSelectItem;
//        }
//
//        public void setListFilialSelectItem(List<SelectItem> listFilialSelectItem) {
//            this.listFilialSelectItem = listFilialSelectItem;
//        }
        public Integer getIdFilial() {
            return idFilial;
        }

        public void setIdFilial(Integer idFilial) {
            this.idFilial = idFilial;
        }

        public List<FilialCidade> getListFilialCidade() {
            if (cidade.getId() != -1) {
                FilialCidadeDao dao = new FilialCidadeDao();
                listFilialCidade = dao.findListBy(cidade.getId(), false);

            }
            return listFilialCidade;
        }

        public void setListFilialCidade(List<FilialCidade> listFilialCidade) {
            this.listFilialCidade = listFilialCidade;
        }

        public FilialCidade getFc() {
            return fc;
        }

        public void setFc(FilialCidade fc) {
            this.fc = fc;
        }

    }

}
