package br.com.rtools.seguranca.beans;

import br.com.rtools.seguranca.dao.PermissaoDepartamentoDao;
import br.com.rtools.seguranca.dao.PermissaoDao;
import br.com.rtools.seguranca.dao.RotinaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.*;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.dao.RotinaGrupoDao;
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
public class PermissaoBean implements Serializable {

    private Permissao permissao;
    private Modulo modulo;
    private Rotina rotina;
    private Rotina rotinaSelected;
    private Evento evento;
    private List<Permissao> listaPermissoes;
    private PermissaoDepartamento permissaoDepartamento;
    //private List<ListaPermissaoDepartamento> listPermissaoDepartamento;
    private String msgConfirma;
    private String indicaTab;
    private String descricaoPesquisa;
    private String tabDisabled;
//    private List listaPermissoesDisponiveis;
//    private List listaPermissoesAdicionadas;
    private List<ListaPermissaoDepartamento> listaPermissoesDisponiveis;
    private List<ListaPermissaoDepartamento> listaPermissoesAdicionadas;
    private List<SelectItem> listaRotinas;
    private List<SelectItem> listaModulos;
    private List<SelectItem> listaEventos;
    private List<SelectItem> listaEventosFiltro;
    private List<SelectItem> listaDepartamentos;
    private List<SelectItem> listaNiveis;
    private List<SelectItem> listaRotinaGrupo;
    private Integer idModulo;
    private Integer idRotina;
    private int idEvento;
    private Integer idEventoFiltro;
    private int idDepartamento;
    private int idNivel;
    private int idIndex;
    private Integer idRotinaGrupo;
    private Boolean rotinaGrupo;
    private String rotinaDescricao;

    // EVENTOS
    // 1
    private Boolean inclusao;
    // 2
    private Boolean exclusao;
    // 3
    private Boolean alteracao;
    // 4
    private Boolean consulta;

    @PostConstruct
    public void init() {
        rotinaDescricao = "";
        inclusao = false;
        exclusao = false;
        alteracao = false;
        consulta = false;
        rotinaGrupo = false;
        permissao = new Permissao();
        modulo = new Modulo();
        rotina = new Rotina();
        evento = new Evento();
        listaPermissoes = new ArrayList();
        permissaoDepartamento = new PermissaoDepartamento();
        msgConfirma = "";
        indicaTab = "permissao";
        descricaoPesquisa = "";
        tabDisabled = "true";
        listaPermissoesDisponiveis = new ArrayList();
        listaPermissoesAdicionadas = new ArrayList();
        listaRotinas = new ArrayList();
        listaModulos = new ArrayList();
        listaEventos = new ArrayList();
        listaEventosFiltro = new ArrayList();
        listaDepartamentos = new ArrayList();
        listaNiveis = new ArrayList();
        // listPermissaoDepartamento = new ArrayList<ListaPermissaoDepartamento>();
        idModulo = 0;
        idRotina = 0;
        idEvento = 0;
        idDepartamento = 0;
        idNivel = 0;
        idIndex = -1;
        idRotinaGrupo = null;
        ChamadaPaginaBean.setModulo("SEGURANCA");
        loadListModulo();
        loadListRotina();
        loadListPermissao();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("permissaoBean");
    }

    public void reload() {
        loadListRotina();
        loadListPermissao();
        rotinaDescricao = "";
    }

    public void loadListRotinaGrupo() {
        listaRotinaGrupo = new ArrayList();
        idRotinaGrupo = null;
        if (rotinaGrupo) {
            List<Rotina> list = new RotinaGrupoDao().findAll();
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRotinaGrupo = list.get(i).getId();
                }
                listaRotinaGrupo.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
            }
        }
        loadListRotina();
    }

    public void loadListModulo() {
        listaModulos = new ArrayList();
        Dao dao = new Dao();
        List<Modulo> list = dao.list(new Modulo(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idModulo = list.get(i).getId();
            }
            listaModulos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListRotina() {
        rotinaSelected = new Rotina();
        listaRotinas = new ArrayList();
        List<Rotina> list = new RotinaDao().findNotInByTabela("seg_permissao", "id_modulo", "" + idModulo);
        if (rotinaGrupo && idRotinaGrupo != null) {
            List<RotinaGrupo> listRotinaGrupo = new RotinaGrupoDao().findByGrupo(idRotinaGrupo);
            int b = 0;
            for (int y = 0; y < listRotinaGrupo.size(); y++) {
                for (int i = 0; i < list.size(); i++) {
                    if (listRotinaGrupo.get(y).getRotina().getId() == list.get(i).getId()) {
                        if (b == 0) {
                            idRotina = list.get(i).getId();
                            rotinaSelected = list.get(i);
                        }
                        listaRotinas.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRotina = list.get(i).getId();
                    rotinaSelected = list.get(i);
                }
                listaRotinas.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
            }
        }
    }

    public void loadListPermissao() {
        listaPermissoes = new ArrayList();
        PermissaoDao permissaoDao = new PermissaoDao();
        if (rotinaDescricao.isEmpty()) {
            listaPermissoes = permissaoDao.findModuloGroup(idModulo);
        } else {
            listaPermissoes = permissaoDao.findModuloGroup(idModulo, rotinaDescricao);
        }
        rotinaDescricao = "";
    }

    // MÓDULO / ROTINA
    public void addPermissao() {
        rotinaDescricao = "";
        if (listaRotinas.isEmpty()) {
            GenericaMensagem.warn("Sistema", "Não há rotinas disponíveis para serem adicionadas a esse módulo");
            return;
        }
        PermissaoDao permissaoDao = new PermissaoDao();
        Dao dao = new Dao();
        modulo = (Modulo) dao.find(new Modulo(), idModulo);
        rotina = (Rotina) dao.find(new Rotina(), idRotina);
        boolean sucesso = false;
        boolean next = false;
        if (permissaoDao.pesquisaPermissaoModRot(modulo.getId(), rotina.getId()).isEmpty()) {
            dao.openTransaction();
            for (int i = 0; i < getListaEventos().size(); i++) {
                if (inclusao || exclusao || alteracao || consulta) {
                    if (inclusao && Integer.valueOf(listaEventos.get(i).getDescription()) == 1) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 1);
                    }
                    if (exclusao && Integer.valueOf(listaEventos.get(i).getDescription()) == 2) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 2);
                    }
                    if (alteracao && Integer.valueOf(listaEventos.get(i).getDescription()) == 3) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 3);
                    }
                    if (consulta && Integer.valueOf(listaEventos.get(i).getDescription()) == 4) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 4);
                    }
                } else {
                    if (rotinaSelected.getInclusao() && Integer.valueOf(listaEventos.get(i).getDescription()) == 1) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 1);
                    }
                    if (rotinaSelected.getExclusao() && Integer.valueOf(listaEventos.get(i).getDescription()) == 2) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 2);
                    }
                    if (rotinaSelected.getAlteracao() && Integer.valueOf(listaEventos.get(i).getDescription()) == 3) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 3);
                    }
                    if (rotinaSelected.getConsulta() && Integer.valueOf(listaEventos.get(i).getDescription()) == 4) {
                        next = true;
                        evento = (Evento) dao.find(new Evento(), 4);
                    }

                }
                if (next) {
                    permissao.setModulo(modulo);
                    permissao.setRotina(rotina);
                    permissao.setEvento(evento);
                    next = false;
                    if (!dao.save(permissao)) {
                        sucesso = false;
                        break;
                    }
                    permissao = new Permissao();
                    sucesso = true;
                }
            }
            if (sucesso) {
                NovoLog novoLog = new NovoLog();
                novoLog.save("Permissão [" + modulo.getDescricao() + " - " + rotina.getRotina() + "]");
                dao.commit();
                GenericaMensagem.info("Sucesso", "Registro adicionado com sucesso");
                loadListRotina();
                loadListPermissao();
            } else {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro adicionar permissão(s)!");
            }
        } else {
            GenericaMensagem.warn("Sistema", "Permissão já existente!");
        }
        inclusao = false;
        exclusao = false;
        alteracao = false;
        consulta = false;
        permissao = new Permissao();
        rotinaDescricao = "";
    }

    public void removePermissao(Permissao p) {
        rotinaDescricao = "";
        PermissaoDao permissaoDao = new PermissaoDao();
        List<Permissao> listaPermissao = (List<Permissao>) permissaoDao.pesquisaPermissaoModRot(p.getModulo().getId(), p.getRotina().getId());
        Dao dao = new Dao();
        dao.openTransaction();
        boolean sucesso = false;
        for (int i = 0; i < listaPermissao.size(); i++) {
            permissao = (Permissao) dao.find(new Permissao(), listaPermissao.get(i).getId());
            if (!dao.delete(permissao)) {
                sucesso = false;
                break;
            }
            sucesso = true;
            permissao = new Permissao();
        }
        if (sucesso) {
            NovoLog novoLog = new NovoLog();
            novoLog.save("Permissão [" + p.getModulo().getDescricao() + " - " + p.getRotina().getRotina() + "]");
            dao.commit();
            GenericaMensagem.info("Sucesso", "Permissão(s) removida(s) com sucesso");
            loadListRotina();
            loadListPermissao();
        } else {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Erro ao remover permissão(s)!");
        }
    }

    // PERMISSÃO DEPARTAMENTO   
    public String adicionarPermissaoDpto() {
        if (!listaPermissoesDisponiveis.isEmpty()) {
            boolean erro = false;
            boolean temRegistros = false;
            Dao dao = new Dao();
            dao.openTransaction();
            for (int i = 0; i < listaPermissoesDisponiveis.size(); i++) {
                if (listaPermissoesDisponiveis.get(i).isSelected()) {
                    Permissao perm = listaPermissoesDisponiveis.get(i).getPermissao();
                    Departamento depto = (Departamento) dao.find(new Departamento(), Integer.parseInt(getListaDepartamentos().get(idDepartamento).getDescription()));
                    Nivel niv = (Nivel) dao.find(new Nivel(), Integer.parseInt(getListaNiveis().get(idNivel).getDescription()));
                    permissaoDepartamento.setPermissao(perm);
                    permissaoDepartamento.setDepartamento(depto);
                    permissaoDepartamento.setNivel(niv);

                    if (!dao.save(permissaoDepartamento)) {
                        temRegistros = false;
                        erro = true;
                        break;
                    }
                    temRegistros = true;
                    permissaoDepartamento = new PermissaoDepartamento();
                }
            }
            if (erro) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao adicionar permissão(s)!");
            } else {
                dao.commit();
                if (temRegistros) {
                    listaPermissoesAdicionadas.clear();
                    listaPermissoesDisponiveis.clear();
                    GenericaMensagem.info("Sucesso", "Permissão(s) adicionada(s) com sucesso");
                } else {
                    GenericaMensagem.info("Sistema", "Não foi selecionada nenhuma permissão!");
                }
            }
        }
        return null;
    }

    public String adicionarPermissaoDptoDBClick(Permissao p) {
        if (!listaPermissoesDisponiveis.isEmpty()) {
            boolean erro = false;
            Dao dao = new Dao();
            dao.openTransaction();
            Permissao perm = p;
            Departamento depto = (Departamento) dao.find(new Departamento(), Integer.parseInt(getListaDepartamentos().get(idDepartamento).getDescription()));
            Nivel niv = (Nivel) dao.find(new Nivel(), Integer.parseInt(getListaNiveis().get(idNivel).getDescription()));
            permissaoDepartamento.setPermissao(perm);
            permissaoDepartamento.setDepartamento(depto);
            permissaoDepartamento.setNivel(niv);
            if (!dao.save(permissaoDepartamento)) {
                erro = true;
            }
            permissaoDepartamento = new PermissaoDepartamento();
            if (erro) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao adicionar permissão(s)!");
            } else {
                dao.commit();
                listaPermissoesAdicionadas.clear();
                listaPermissoesDisponiveis.clear();
                GenericaMensagem.info("Sucesso", "Permissão(s) adicionada(s) com sucesso");
            }
        }
        return null;
    }

    public String adicionarTodasPermissaoDpto() {
        if (!listaPermissoesDisponiveis.isEmpty()) {
            boolean erro = false;
            Dao dao = new Dao();
            dao.openTransaction();
            for (int i = 0; i < listaPermissoesDisponiveis.size(); i++) {
                Permissao perm = listaPermissoesDisponiveis.get(i).getPermissao();
                Departamento depto = (Departamento) dao.find(new Departamento(), Integer.parseInt(listaDepartamentos.get(idDepartamento).getDescription()));
                Nivel niv = (Nivel) dao.find(new Nivel(), Integer.parseInt(listaNiveis.get(idNivel).getDescription()));
                permissaoDepartamento.setPermissao(perm);
                permissaoDepartamento.setDepartamento(depto);
                permissaoDepartamento.setNivel(niv);
                if (!dao.save(permissaoDepartamento)) {
                    erro = true;
                    break;
                }
                permissaoDepartamento = new PermissaoDepartamento();
            }
            if (erro) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao adicionar permissão(s)!");
            } else {
                dao.commit();
                listaPermissoesAdicionadas.clear();
                listaPermissoesDisponiveis.clear();
                GenericaMensagem.info("Sucesso", "Permissão(s) adicionada(s) com sucesso");
            }
        }
        return null;
    }

    public String excluirPermissaoDepto() {
        if (!listaPermissoesAdicionadas.isEmpty()) {
            boolean erro = false;
            boolean temRegistros = false;
            Dao dao = new Dao();
            dao.openTransaction();
            for (int i = 0; i < listaPermissoesAdicionadas.size(); i++) {
                if (listaPermissoesAdicionadas.get(i).isSelected()) {
                    permissaoDepartamento = (PermissaoDepartamento) listaPermissoesAdicionadas.get(i).getPermissaoDepartamento();
                    if (!dao.delete(permissaoDepartamento)) {
                        erro = true;
                        temRegistros = false;
                        break;
                    }
                    temRegistros = true;
                }
                permissaoDepartamento = new PermissaoDepartamento();
            }
            if (erro) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao remover permissão(s)!");
            } else {
                dao.commit();
                if (temRegistros) {
                    listaPermissoesAdicionadas.clear();
                    listaPermissoesDisponiveis.clear();
                    GenericaMensagem.info("Sucesso", "Permissão(s) removida(s) com sucesso");
                } else {
                    GenericaMensagem.info("Sistema", "Não foi selecionada nenhuma permissão!");
                }
            }
        }
        return null;
    }

    public String excluirPermissaoDeptoDBClick(PermissaoDepartamento pd) {
        if (!listaPermissoesAdicionadas.isEmpty()) {
            boolean erro = false;
            Dao dao = new Dao();
            dao.openTransaction();
            permissaoDepartamento = pd;
            if (!dao.delete(permissaoDepartamento)) {
                erro = true;
            }
            permissaoDepartamento = new PermissaoDepartamento();
            if (erro) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao remover permissão(s)!");
            } else {
                dao.commit();
                listaPermissoesAdicionadas.clear();
                listaPermissoesDisponiveis.clear();
                GenericaMensagem.info("Sucesso", "Permissão(s) removida(s) com sucesso");
            }
        }
        return null;
    }

    public String excluirTodasPermissaoDepto() {
        if (!listaPermissoesAdicionadas.isEmpty()) {
            boolean erro = false;
            Dao dao = new Dao();
            dao.openTransaction();
            for (int i = 0; i < listaPermissoesAdicionadas.size(); i++) {
                permissaoDepartamento = listaPermissoesAdicionadas.get(i).getPermissaoDepartamento();
                if (!dao.delete(permissaoDepartamento)) {
                    erro = true;
                    break;
                }
            }
            if (erro) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao remover permissão(s)!");
            } else {
                dao.commit();
                listaPermissoesAdicionadas.clear();
                listaPermissoesDisponiveis.clear();
                GenericaMensagem.info("Sucesso", "Permissão(s) removidas com sucesso");
            }
        }
        permissaoDepartamento = new PermissaoDepartamento();
        return null;
    }

    public void pesquisaPermissoesDepartamento() {
        listaPermissoesDisponiveis.clear();
        listaPermissoesAdicionadas.clear();
    }

    public void limparPesquisaPermissoesDepartamento() {
        descricaoPesquisa = "";
        listaPermissoesDisponiveis.clear();
        listaPermissoesAdicionadas.clear();
    }

    public List getListaPermissaoDpto() {
        List result = new Dao().list(new PermissaoDepartamento());
        return result;
    }

    public Permissao getPermissao() {
        return permissao;
    }

    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public Integer getIdModulo() {
        return idModulo;
    }

    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
    }

    public Integer getIdRotina() {
        return idRotina;
    }

    public void setIdRotina(Integer idRotina) {
        this.idRotina = idRotina;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public String getIndicaTab() {
        return indicaTab;
    }

    public void setIndicaTab(String indicaTab) {
        this.indicaTab = indicaTab;
    }

    public String getTabDisabled() {
        return tabDisabled;
    }

    public void setTabDisabled(String tabDisabled) {
        this.tabDisabled = tabDisabled;
    }

    public int getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(int idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(int idNivel) {
        this.idNivel = idNivel;
    }

    public PermissaoDepartamento getPermissaoDepartamento() {
        return permissaoDepartamento;
    }

    public void setPermissaoDepartamento(PermissaoDepartamento permissaoDepartamento) {
        this.permissaoDepartamento = permissaoDepartamento;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public List<SelectItem> getListaModulos() {
        return listaModulos;
    }

    public void setListaModulos(List<SelectItem> listaModulos) {
        this.listaModulos = listaModulos;
    }

    public List<SelectItem> getListaRotinas() {
        return listaRotinas;
    }

    public void setListaRotinas(List<SelectItem> listaRotinas) {
        this.listaRotinas = listaRotinas;
    }

    public List<SelectItem> getListaEventos() {
        if (listaEventos.isEmpty()) {
            Dao dao = new Dao();
            List eventos = dao.list(new Evento(), true);
            for (int i = 0; i < eventos.size(); i++) {
                listaEventos.add(new SelectItem(i, ((Evento) eventos.get(i)).getDescricao(), Integer.toString(((Evento) eventos.get(i)).getId())));
            }
        }
        return listaEventos;
    }

    public void setListaEventos(List<SelectItem> listaEventos) {
        this.listaEventos = listaEventos;
    }

    public List<SelectItem> getListaNiveis() {
        if (listaNiveis.isEmpty()) {
            Dao dao = new Dao();
            List niveis = dao.list(new Nivel(), true);
            for (int i = 0; i < niveis.size(); i++) {
                listaNiveis.add(new SelectItem(i,
                        ((Nivel) niveis.get(i)).getDescricao(),
                        Integer.toString(((Nivel) niveis.get(i)).getId())));
            }

        }
        return listaNiveis;
    }

    public void setListaNiveis(List<SelectItem> listaNiveis) {
        this.listaNiveis = listaNiveis;
    }

    public List<SelectItem> getListaDepartamentos() {
        if (listaDepartamentos.isEmpty()) {
            Dao dao = new Dao();
            List departamentos = dao.list(new Departamento(), true);
            for (int i = 0; i < departamentos.size(); i++) {
                listaDepartamentos.add(new SelectItem(i,
                        ((Departamento) departamentos.get(i)).getDescricao(),
                        Integer.toString(((Departamento) departamentos.get(i)).getId())));
            }
        }
        return listaDepartamentos;
    }

    public void setListaDepartamentos(List<SelectItem> listaDepartamentos) {
        this.listaDepartamentos = listaDepartamentos;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public List<Permissao> getListaPermissoes() {
        return listaPermissoes;
    }

    public void setListaPermissoes(List<Permissao> listaPermissoes) {
        this.listaPermissoes = listaPermissoes;
    }

    public List<ListaPermissaoDepartamento> getListaPermissoesDisponiveis() {
        if (listaPermissoesDisponiveis.isEmpty()) {
            PermissaoDepartamentoDao permissaoDepartamentoDB = new PermissaoDepartamentoDao();
            int idDepto = Integer.parseInt(listaDepartamentos.get(idDepartamento).getDescription());
            int idNiv = Integer.parseInt(listaNiveis.get(idNivel).getDescription());
            List<Permissao> list = permissaoDepartamentoDB.listaPermissaoDepartamentoDisponivel(idDepto, idNiv, idEventoFiltro, descricaoPesquisa);
            for (Permissao list1 : list) {
                listaPermissoesDisponiveis.add(new ListaPermissaoDepartamento(null, list1, false));
            }
        }
        return listaPermissoesDisponiveis;
    }

    public void setListaPermissoesDisponiveis(List<ListaPermissaoDepartamento> listaPermissoesDisponiveis) {
        this.listaPermissoesDisponiveis = listaPermissoesDisponiveis;
    }

    public List<ListaPermissaoDepartamento> getListaPermissoesAdicionadas() {
        if (listaPermissoesAdicionadas.isEmpty()) {
            PermissaoDepartamentoDao permissaoDepartamentoDB = new PermissaoDepartamentoDao();
            int idDepto = Integer.parseInt(listaDepartamentos.get(idDepartamento).getDescription());
            int idNiv = Integer.parseInt(listaNiveis.get(idNivel).getDescription());
            List<PermissaoDepartamento> list = permissaoDepartamentoDB.listaPermissaoDepartamentoAdicionada(idDepto, idNiv, idEventoFiltro, descricaoPesquisa);
            for (PermissaoDepartamento list1 : list) {
                listaPermissoesAdicionadas.add(new ListaPermissaoDepartamento(list1, list1.getPermissao(), false));
            }
        }
        return listaPermissoesAdicionadas;
    }

    public void setListaPermissoesAdicionadas(List<ListaPermissaoDepartamento> listaPermissoesAdicionadas) {
        this.listaPermissoesAdicionadas = listaPermissoesAdicionadas;
    }

    public List<SelectItem> getListaRotinaGrupo() {
        return listaRotinaGrupo;
    }

    public void setListaRotinaGrupo(List<SelectItem> listaRotinaGrupo) {
        this.listaRotinaGrupo = listaRotinaGrupo;
    }

    public Integer getIdRotinaGrupo() {
        return idRotinaGrupo;
    }

    public void setIdRotinaGrupo(Integer idRotinaGrupo) {
        this.idRotinaGrupo = idRotinaGrupo;
    }

    public Boolean getRotinaGrupo() {
        return rotinaGrupo;
    }

    public void setRotinaGrupo(Boolean rotinaGrupo) {
        this.rotinaGrupo = rotinaGrupo;
    }

    /**
     * 1
     *
     * @return
     */
    public Boolean getInclusao() {
        return inclusao;
    }

    public void setInclusao(Boolean inclusao) {
        this.inclusao = inclusao;
    }

    /**
     * 2
     *
     * @return
     */
    public Boolean getExclusao() {
        return exclusao;
    }

    public void setExclusao(Boolean exclusao) {
        this.exclusao = exclusao;
    }

    /**
     * 3
     *
     * @return
     */
    public Boolean getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(Boolean alteracao) {
        this.alteracao = alteracao;
    }

    /**
     * 4
     *
     * @return
     */
    public Boolean getConsulta() {
        return consulta;
    }

    public void setConsulta(Boolean consulta) {
        this.consulta = consulta;
    }

    public Permissao getPermissao(Permissao p, Integer evento_id) {
        return new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(p.getModulo().getId(), p.getRotina().getId(), evento_id);
    }

    public void updateInclusao(int index) {
        Permissao p1 = listaPermissoes.get(index);
        updatePermissao(p1, 1);
    }

    public void updateExclusao(int index) {
        Permissao p1 = listaPermissoes.get(index);
        updatePermissao(p1, 2);

    }

    public void updateAlteracao(int index) {
        Permissao p1 = listaPermissoes.get(index);
        updatePermissao(p1, 3);
    }

    public void updateConsulta(int index) {
        Permissao p1 = listaPermissoes.get(index);
        updatePermissao(p1, 4);
    }

    public void updatePermissao(Permissao p, Integer evento_id) {
        Permissao p2 = new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(p.getModulo().getId(), p.getRotina().getId(), evento_id);
        Dao dao = new Dao();
        if (p2.getId() == -1) {
            p2.setModulo(p.getModulo());
            p2.setRotina(p.getRotina());
            p2.setEvento((Evento) dao.find(new Evento(), evento_id));
            if (dao.save(p2, true)) {
                GenericaMensagem.info("Sucesso", "Registro atualizado");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
            }
        } else if (dao.delete(p2, true)) {
            List list = new PermissaoDao().pesquisaPermissaoModRot(p2.getModulo().getId(), p2.getRotina().getId());
            if (list.isEmpty()) {
                for (int i = 0; i < listaPermissoes.size(); i++) {
                    if (p2.getModulo().getId() == listaPermissoes.get(i).getModulo().getId() && p2.getRotina().getId() == listaPermissoes.get(i).getRotina().getId()) {
                        listaPermissoes.remove(i);
                        PF.update("form_permissao:tbl");
                        loadListRotina();
                        PF.update("form_permissao:i_rotinas");
                        break;
                    }
                }
            }
            GenericaMensagem.info("Sucesso", "Registro atualizado");
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
        }
    }

    public String getRotinaDescricao() {
        return rotinaDescricao;
    }

    public void setRotinaDescricao(String rotinaDescricao) {
        this.rotinaDescricao = rotinaDescricao;
    }

    public Rotina getRotinaSelected() {
        return rotinaSelected;
    }

    public void setRotinaSelected(Rotina rotinaSelected) {
        this.rotinaSelected = rotinaSelected;
    }

    public void loadRotina() {
        if (idRotina != null) {
            rotinaSelected = (Rotina) new Dao().find(new Rotina(), idRotina);
        }
    }

    public List<SelectItem> getListaEventosFiltro() {
        if (listaEventosFiltro.isEmpty()) {
            Dao dao = new Dao();
            List<Evento> list = dao.list(new Evento(), true);
            listaEventosFiltro.add(new SelectItem(null, "TODOS"));
            idEventoFiltro = null;
            for (int i = 0; i < list.size(); i++) {
                listaEventosFiltro.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
        return listaEventosFiltro;
    }

    public void setListaEventosFiltro(List<SelectItem> listaEventosFiltro) {
        this.listaEventosFiltro = listaEventosFiltro;
    }

    public Integer getIdEventoFiltro() {
        return idEventoFiltro;
    }

    public void setIdEventoFiltro(Integer idEventoFiltro) {
        this.idEventoFiltro = idEventoFiltro;
    }
}
