package br.com.rtools.seguranca.beans;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.beans.ConfiguracaoFinanceiroBean;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Sessions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class MacFilialBean implements Serializable {

    private MacFilial macFilial;
    private Integer idFilial;
    private Integer idDepartamento;
    private Integer idCaixa;
    private List<MacFilial> listMacs;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listDepartamentos;
    private List<SelectItem> listCaixa;
    private Boolean mostrarTodos;

    @PostConstruct
    public void init() {
        macFilial = new MacFilial();
        idFilial = null;
        idDepartamento = null;
        idCaixa = null;
        listMacs = new ArrayList<>();
        listFiliais = new ArrayList<>();
        listDepartamentos = new ArrayList<>();
        listCaixa = new ArrayList<>();
        mostrarTodos = false;
        ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();

        cfb.init();

        macFilial.setCaixaOperador(cfb.getConfiguracaoFinanceiro().isCaixaOperador());

        loadListFiliais();
        loadListDepartamentos();
        loadListCaixa();
        loadListMacs();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("macFilialBean");
    }

    public void clear() {
        GenericaSessao.remove("macFilialBean");
    }

    public void alterarCaixa() {
        if (macFilial.getCaixaOperador()) {
            idCaixa = null;
        }
    }

    public void add() {
        MacFilialDao macFilialDao = new MacFilialDao();
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        Filial filial = (Filial) dao.find(new Filial(), idFilial);
        Departamento departamento = (Departamento) dao.find(new Departamento(), idDepartamento);
        if (macFilial.getMac().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite um mac válido!");
            return;
        }
        if (!macFilial.getMac().matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            GenericaMensagem.warn("Validação", "Digite um mac válido!");
            return;
        }
        macFilial.setDepartamento(departamento);
        macFilial.setFilial(filial);

        if (idCaixa == null) {
            macFilial.setCaixa(null);
        } else {
            for (int i = 0; i < listMacs.size(); i++) {
                if (listMacs.get(i).getCaixa() != null
                        && listMacs.get(i).getCaixa().getId() == idCaixa
                        && macFilial.getId() == -1) {
                    GenericaMensagem.warn("Validação", "Já existe uma filial cadastrada para este Caixa");
                    return;
                }
            }
            macFilial.setCaixa((Caixa) dao.find(new Caixa(), idCaixa));
        }

        dao.openTransaction();

        if (macFilial.getId() == -1) {
            if (macFilialDao.pesquisaMac(macFilial.getMac()) != null) {
                GenericaMensagem.warn("Validação", "Este computador ja está registrado!");
                return;
            }

            if (dao.save(macFilial)) {
                novoLog.save(
                        "ID: " + macFilial.getId()
                        + " - Filial: (" + macFilial.getFilial().getId() + ") " + macFilial.getFilial().getFilial().getPessoa().getNome()
                        + " - Departamento: (" + macFilial.getDepartamento().getId() + ") " + macFilial.getDepartamento().getDescricao()
                        + " - Mesa: " + macFilial.getMesa()
                        + " - Mac: " + macFilial.getMac()
                );
                dao.commit();
                GenericaMensagem.info("Salvo", "Este Computador registrado com sucesso!");
                novoLog.setTabela("seg_mac_filial");
                novoLog.setCodigo(macFilial.getId());
                macFilial = new MacFilial();
                loadListMacs();
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao inserir esse registro!");
            }
        } else {
            MacFilial mf = (MacFilial) new Dao().find(macFilial);

            String before_update
                    = "ID: " + macFilial.getId()
                    + " - Filial: (" + mf.getFilial().getId() + ") " + mf.getFilial().getFilial().getPessoa().getNome()
                    + " - Departamento: (" + mf.getDepartamento().getId() + ") " + mf.getDepartamento().getDescricao()
                    + " - Mesa: " + mf.getMesa()
                    + " - Mac: " + mf.getMac()
                    + " - Número Caixa: " + ((mf.getCaixa() == null) ? "" : mf.getCaixa().getCaixa())
                    + " - Caixa: " + ((mf.getCaixa() == null) ? "" : mf.getCaixa().getDescricao());

            if (dao.update(macFilial)) {
                novoLog.setTabela("seg_mac_filial");
                novoLog.setCodigo(macFilial.getId());
                novoLog.update(
                        before_update,
                        "ID: " + macFilial.getId()
                        + " - Filial: (" + macFilial.getFilial().getId() + ") " + macFilial.getFilial().getFilial().getPessoa().getNome()
                        + " - Departamento: (" + macFilial.getDepartamento().getId() + ") " + macFilial.getDepartamento().getDescricao()
                        + " - Mesa: " + macFilial.getMesa()
                        + " - Mac: " + macFilial.getMac()
                        + " - Número Caixa: " + ((mf.getCaixa() == null) ? "" : (macFilial.getCaixa() != null) ? macFilial.getCaixa().getCaixa() : "")
                        + " - Caixa: " + ((mf.getCaixa() == null) ? "" : (macFilial.getCaixa() != null) ? macFilial.getCaixa().getDescricao() : "")
                );
                dao.commit();
                GenericaMensagem.info("Atualizado", "Computador atualizado com sucesso!");
                macFilial = new MacFilial();
                loadListMacs();
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao inserir esse registro!");
            }

        }
    }

    public void edit(MacFilial mf) {
        macFilial = mf;
        idDepartamento = macFilial.getDepartamento().getId();
        idFilial = macFilial.getFilial().getId();

        if (macFilial.getCaixa() == null) {
            idCaixa = null;
        } else {
            idCaixa = macFilial.getCaixa().getId();
        }
    }

    public void delete(MacFilial mf) {
        macFilial = mf;
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        dao.openTransaction();
        if (dao.delete(macFilial)) {
            novoLog.delete(
                    "ID: " + macFilial.getId()
                    + " - Filial: (" + macFilial.getFilial().getId() + ") " + macFilial.getFilial().getFilial().getPessoa().getNome()
                    + " - Departamento: (" + macFilial.getDepartamento().getId() + ") " + macFilial.getDepartamento().getDescricao()
                    + " - Mesa: " + macFilial.getMesa()
                    + " - Mac: " + macFilial.getMac()
            );
            dao.commit();
            GenericaMensagem.info("Sucesso", "Este Registro excluído com sucesso!");
            loadListMacs();
        } else {
            dao.rollback();
            GenericaMensagem.info("Sucesso", "Erro ao excluir computador!");

        }
        macFilial = new MacFilial();
    }

    public void loadListFiliais() {
        listFiliais = new ArrayList();
        List<Filial> list = (List<Filial>) new Dao().list(new Filial(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            if (GenericaSessao.exists("acessoFilial")) {
                if (Objects.equals(((MacFilial) GenericaSessao.getObject("acessoFilial")).getFilial().getId(), list.get(i).getId())) {
                    idFilial = list.get(i).getId();
                }
            }
            listFiliais.add(
                    new SelectItem(
                            list.get(i).getId(),
                            list.get(i).getFilial().getPessoa().getDocumento() + " / " + list.get(i).getFilial().getPessoa().getNome()
                    )
            );
        }
    }

    public void loadListDepartamentos() {
        listDepartamentos = new ArrayList();
        List<Departamento> list = new Dao().list(new Departamento(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDepartamento = list.get(i).getId();
            }
            listDepartamentos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void refreshForm() {

    }

    public MacFilial getMacFilial() {
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public void loadListMacs() {
        listMacs = new ArrayList();
        MacFilialDao macFilialDao = new MacFilialDao();
        if (mostrarTodos) {
            listMacs = macFilialDao.listaTodosPorFilial(null);
        } else {
            listMacs = macFilialDao.listaTodosPorFilial(idFilial);
        }
    }

    public List<MacFilial> getListMacs() {
        return listMacs;
    }

    public void setListMacs(List<MacFilial> listaMacs) {
        this.listMacs = listaMacs;
    }

    public Integer getIdCaixa() {
        return idCaixa;
    }

    public void setIdCaixa(Integer idCaixa) {
        this.idCaixa = idCaixa;
    }

    public void loadListCaixa() {
        listCaixa = new ArrayList();
        Dao dao = new Dao();
        List<Caixa> list = dao.list(new Caixa());
        idCaixa = null;
        listCaixa.add(new SelectItem(null, "NENHUM CAIXA"));
        for (int i = 0; i < list.size(); i++) {
            listCaixa.add(new SelectItem(list.get(i).getId(),
                    ((String.valueOf(list.get(i).getCaixa()).length() == 1) ? ("0" + String.valueOf(list.get(i).getCaixa())) : list.get(i).getCaixa()) + " - " + list.get(i).getDescricao(),
                    Integer.toString(list.get(i).getId())));
        }
    }

    public List<SelectItem> getListCaixa() {
        return listCaixa;
    }

    public void setListCaixa(List<SelectItem> listCaixa) {
        this.listCaixa = listCaixa;
    }

    public String selecionaFilial(MacFilial mf) {
        return selecionaFilial(mf, false);
    }

    public String selecionaFilial(MacFilial mf, boolean sair) {
        GenericaSessao.remove("acessoFilial");
        ((ControleUsuarioBean) GenericaSessao.getObject("controleUsuarioBean")).setMacFilial(mf);
        ((ControleUsuarioBean) GenericaSessao.getObject("controleUsuarioBean")).setFilial(ControleUsuarioBean.retornaStringFilial(mf, Usuario.getUsuario()));

        GenericaSessao.put("acessoFilial", mf);
        GenericaSessao.put("linkClicado", true);
        if (GenericaSessao.exists("back")) {
            String back = ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).getUrlAtual();
            GenericaSessao.remove("back");
            return back;
        }
        GenericaSessao.remove("chamadaPaginaBean");
        return "menuPrincipal";

    }

    public Boolean getMostrarTodos() {
        return mostrarTodos;
    }

    public void setMostrarTodos(Boolean mostrarTodos) {
        this.mostrarTodos = mostrarTodos;
    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public List<SelectItem> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<SelectItem> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

}
