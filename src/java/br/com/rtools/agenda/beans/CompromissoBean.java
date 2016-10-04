package br.com.rtools.agenda.beans;

import br.com.rtools.agenda.Compromisso;
import br.com.rtools.agenda.CompromissoCategoria;
import br.com.rtools.agenda.CompromissoUsuario;
import br.com.rtools.agenda.Secretaria;
import br.com.rtools.agenda.dao.CompromissoDao;
import br.com.rtools.agenda.dao.CompromissoUsuarioDao;
import br.com.rtools.agenda.dao.SecretariaDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
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
public class CompromissoBean implements Serializable {

    private Compromisso compromisso;
    private CompromissoUsuario compromissoUsuario;
    private List<SelectItem> listUsuario;
    private Integer idUsuario;
    private Integer idUsuarioFiltro;
    private List<SelectItem> listCompromissoCategoria;
    private Integer idCompromissoCategoria;
    private List<Compromisso> listCompromissos;
    private Boolean visibled;
    private List<CompromissoUsuario> listCompromissoUsuario;
    private List<SelectItem> listSemana;
    private Integer idSemana;
    private List<SelectItem> listRepeticao;
    private Integer idRepeticao;
    private Boolean semanal;
    private Date data;
    private String tipoHistorico;
    private String tipoData;
    private String dataInicial;
    private String dataFinal;
    private String cancelados;

    @PostConstruct
    public void init() {
        semanal = false;
        visibled = false;
        compromisso = new Compromisso();
        loadListCompromissos();
        listCompromissoUsuario = new ArrayList();
        loadListPeriodoRepeticao();
        loadListUsuario();
        loadListCompromissoCategoria();
        data = new Date();
        tipoHistorico = "hoje_amanha";
        tipoData = "";
        dataInicial = "";
        dataFinal = "";
        cancelados = "ativos";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("compromissoBean");
        GenericaSessao.remove("enderecoPesquisa");
        GenericaSessao.remove("pessoaBean");
    }

    public void clear() {
        GenericaSessao.remove("compromissoBean");
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "reload_tipos":
                tipoHistorico = "";
                break;
            case "usuario":
                loadListUsuario();
                break;
            case "periodo":
                if (idRepeticao != null) {
                    if (idRepeticao == 2) {
                        loadListSemana();
                        compromisso.setData("");
                    } else {
                        compromisso.setData(DataHoje.data());
                    }
                }
                break;
            case "compromisso":
                if (data != null) {
                    compromisso.setDtData(data);
                    compromisso.setHoraFinal(DataHoje.livre(data, "HH:mm"));
                    compromisso.setHoraFinal("");
                }
                data = null;
                break;
            case "compromisso_particular":
                compromissoUsuario = new CompromissoUsuario();
                compromissoUsuario.setUsuario(Usuario.getUsuario());
                compromisso.setParticular(true);
                if (data != null) {
                    compromisso.setDtData(data);
                    compromisso.setHoraFinal(DataHoje.livre(data, "HH:mm"));
                    compromisso.setHoraFinal("");
                }
                data = null;
                break;
            case "pesquisar":
                listCompromissos = new ArrayList();
                listCompromissos = new CompromissoDao().findCompromissos(Usuario.getUsuario().getId(), tipoHistorico, tipoData, dataInicial, dataFinal, cancelados, idUsuarioFiltro);
                break;
            default:
                break;
        }

    }

    public void loadListUsuario() {
        listUsuario = new ArrayList();
        List<Secretaria> list = new SecretariaDao().findBySecretaria(Usuario.getUsuario().getId());
        for (int x = 0; x < list.size(); x++) {
            for (int i = 0; i < listUsuario.size(); i++) {
                if (listCompromissoUsuario.get(i).getUsuario().getId() == list.get(x).getUsuario().getId()) {
                    list.remove(x);
                    break;
                }
            }
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsuario().getAtivo()) {
                if (idUsuario == null) {
                    idUsuario = list.get(i).getId();
                }
                listUsuario.add(new SelectItem(list.get(i).getUsuario().getId(), list.get(i).getUsuario().getPessoa().getNome()));
            }
        }
    }

    public void loadListCompromissoCategoria() {
        listCompromissoCategoria = new ArrayList();
        List<CompromissoCategoria> list = new Dao().list(new CompromissoCategoria(), true);
        for (int i = 0; i < list.size(); i++) {
            if (idCompromissoCategoria == null) {
                idCompromissoCategoria = list.get(i).getId();
            }
            listCompromissoCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListSemana() {
        listSemana = new ArrayList();
        List<Semana> list = new Dao().list(new Semana());
        for (int i = 0; i < list.size(); i++) {
            if (idSemana == null) {
                idSemana = list.get(i).getId();
            }
            listSemana.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao().toUpperCase()));
        }
    }

    public void loadListPeriodoRepeticao() {
        listRepeticao = new ArrayList();
        List<Periodo> list = new Dao().list(new Periodo(), true);
        idRepeticao = null;
        listRepeticao.add(new SelectItem(null, "NENHUM"));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDescricao().contains("SEMANAL")) {
                listRepeticao.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
    }

    public void loadListCompromissos() {
        listCompromissos = new ArrayList();
        listCompromissos = new CompromissoDao().findCompromissos(Usuario.getUsuario().getId());
    }

    public void loadListCompromissoUsuario() {
        listCompromissoUsuario = new ArrayList();
        listCompromissoUsuario = new CompromissoUsuarioDao().findByCompromisso(compromisso.getId());

    }

    public void save() {
        if (listCompromissoCategoria.isEmpty()) {
            GenericaMensagem.warn("VALIDAÇÃO", "CADASTRAR CATEGORIAS!");
            return;
        }
        if (!compromisso.getParticular()) {
            if (listUsuario.isEmpty()) {
                GenericaMensagem.warn("VALIDAÇÃO", "CADASTRAR USUÁRIOS PARA ESTA SECRETÁRIA(O)!");
                return;
            }
        }
        Dao dao = new Dao();
        if (compromisso.getCadastro().isEmpty()) {
            GenericaMensagem.warn("VALIDAÇÃO", "INFORMAR DATA!");
            return;
        }
        if (compromisso.getHoraInicial().isEmpty()) {
            GenericaMensagem.warn("VALIDAÇÃO", "INFORMAR HORA INICIAL!");
            return;
        }
        if (compromisso.getDescricao().isEmpty()) {
            GenericaMensagem.warn("VALIDAÇÃO", "INFORMAR UMA DESCRIÇÃO VÁLIDA!");
            return;
        }
        if (idRepeticao != null) {
            compromisso.setPeriodoRepeticao((Periodo) dao.find(new Periodo(), idRepeticao));
            if (idRepeticao == 2) {
                compromisso.setSemana((Semana) dao.find(new Semana(), idSemana));
            }
        }
        NovoLog novoLog = new NovoLog();
        compromisso.setCompromissoCategoria((CompromissoCategoria) dao.find(new CompromissoCategoria(), idCompromissoCategoria));
        compromisso.setSecretaria(Usuario.getUsuario());
        if (compromisso.getId() == null) {
            dao.openTransaction();
            if (!dao.save(compromisso)) {
                dao.rollback();
                GenericaMensagem.warn("ERRO", "AO ADICIONAR REGISTRO!");
                return;
            }
            if (compromissoUsuario != null && compromissoUsuario.getUsuario() != null && compromissoUsuario.getUsuario().getId() != -1) {
                compromissoUsuario.setCompromisso(compromisso);
                if (!dao.save(compromissoUsuario)) {
                    dao.rollback();
                    GenericaMensagem.warn("ERRO", "AO ADICIONAR REGISTRO!");
                    return;
                }
            }
            dao.commit();
            loadListUsuario();
            loadListCompromissos();
            String saveString = "ID: " + compromisso.getId() + " - Categoria: " + compromisso.getCompromissoCategoria().getDescricao() + " - Descrição: " + compromisso.getDescricao() + " - Data: " + compromisso.getData() + " - Horário: " + compromisso.getHoraInicial();
            novoLog.setTabela("age_compromisso");
            novoLog.setCodigo(compromisso.getId());
            novoLog.save(saveString);
            GenericaMensagem.info("SUCESSO", "REGISTRO INSERIDO");
        } else {
            Compromisso c = (Compromisso) dao.find(new Compromisso(), compromisso.getId());
            String beforeUpdate = "ID: " + c.getId() + " - Categoria: " + c.getCompromissoCategoria().getDescricao() + " - Descrição: " + c.getDescricao() + " - Data: " + c.getData() + " - Horário: " + c.getHoraInicial();
            String afterUpdate = "ID: " + compromisso.getId() + " - Categoria: " + compromisso.getCompromissoCategoria().getDescricao() + " - Descrição: " + compromisso.getDescricao() + " - Data: " + compromisso.getData() + " - Horário: " + compromisso.getHoraInicial();
            if (dao.update(compromisso, true)) {
                novoLog.setTabela("age_compromisso");
                novoLog.setCodigo(compromisso.getId());
                novoLog.update(beforeUpdate, afterUpdate);
                loadListUsuario();
                loadListCompromissos();
                GenericaMensagem.info("SUCESSO", "REGISTRO ATUALIZADO");
            } else {
                GenericaMensagem.warn("ERRO", "AO ATUALIZADO REGISTRO!");
            }
        }
        compromissoUsuario = null;
    }

    public void edit(Compromisso c) {
        compromisso = (Compromisso) new Dao().rebind(c);
        idCompromissoCategoria = compromisso.getCompromissoCategoria().getId();
        visibled = true;
        semanal = false;
        loadListCompromissoUsuario();
        if (compromisso.getPeriodoRepeticao() != null) {
            idRepeticao = compromisso.getPeriodoRepeticao().getId();
            if (compromisso.getSemana() != null) {
                if (compromisso.getPeriodoRepeticao().getId() == 2) {
                    loadListSemana();
                    idSemana = compromisso.getSemana().getId();
                    semanal = true;
                }
            }
        }
    }

    public void cancel() {
        compromisso.setCancelamento(DataHoje.data());
        compromisso.setUsuarioCancelador(Usuario.getUsuario());
        if (new Dao().update(compromisso, true)) {
            GenericaMensagem.info("SUCESSO", "REGISTRO ATUALIZADO");
        } else {
            GenericaMensagem.warn("ERRO", "AO ATUALIZADO REGISTRO!");
        }
    }

    public void delete() {
        if (compromisso.getId() != null) {
            for (int i = 0; i < listCompromissoUsuario.size(); i++) {
                new Dao().delete(listCompromissoUsuario.get(i), true);
            }
            if (new Dao().delete(compromisso, true)) {
                GenericaMensagem.info("SUCESSO", "REGISTRO REMOVIDO");
                GenericaSessao.remove("compromissoBean");
                NovoLog novoLog = new NovoLog();
                String deleteString = "ID: " + compromisso.getId() + " - Categoria: " + compromisso.getCompromissoCategoria().getDescricao() + " - Descrição: " + compromisso.getDescricao() + " - Data: " + compromisso.getData() + " - Horário: " + compromisso.getHoraInicial();
                novoLog.setTabela("age_compromisso");
                novoLog.setCodigo(compromisso.getId());
                novoLog.delete(deleteString);
            } else {
                GenericaMensagem.warn("ERRO", "AO REMOVER REGISTRO!");
            }
        }
    }

    public void addCompromissoUsuario() {
        if (compromisso.getId() != null) {
            CompromissoUsuario cu = new CompromissoUsuario();
            cu.setCompromisso(compromisso);
            Dao dao = new Dao();
            cu.setUsuario((Usuario) dao.find(new Usuario(), idUsuario));
            if (dao.save(cu, true)) {
                GenericaMensagem.info("SUCESSO", "REGISTRO REMOVIDO");
                loadListCompromissoUsuario();
                loadListUsuario();
            } else {
                GenericaMensagem.warn("ERRO", "AO REMOVER REGISTRO!");
            }
        }
    }

    public void removeCompromissoUsuario(CompromissoUsuario cu) {
        Dao dao = new Dao();
        if (dao.delete(cu, true)) {
            GenericaMensagem.info("SUCESSO", "REGISTRO REMOVIDO");
            loadListUsuario();
            loadListCompromissoUsuario();
        } else {
            GenericaMensagem.warn("ERRO", "AO REMOVER REGISTRO!");
        }
    }

    public Compromisso getCompromisso() {
        if (GenericaSessao.exists("enderecoPesquisa")) {
            compromisso.setLocal((Endereco) GenericaSessao.getObject("enderecoPesquisa", true));
        }
        if (GenericaSessao.exists("pessoaPesquisa")) {
            compromisso.setPessoa((Pessoa) GenericaSessao.getObject("pessoaPesquisa", true));
        }
        if (compromisso.getLocal() == null) {
            if (compromisso.getPessoa() != null) {
                PessoaEndereco pessoaEndereco = compromisso.getPessoa().getPessoaEndereco();
                if (pessoaEndereco != null) {
                    compromisso.setLocal(pessoaEndereco.getEndereco());
                    compromisso.setNumero(pessoaEndereco.getNumero());
                    compromisso.setComplemento(pessoaEndereco.getComplemento());
                }
            }
        }
        return compromisso;
    }

    public void setCompromisso(Compromisso compromisso) {
        this.compromisso = compromisso;
    }

    public List<SelectItem> getListUsuario() {
        return listUsuario;
    }

    public void setListUsuario(List<SelectItem> listUsuario) {
        this.listUsuario = listUsuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<SelectItem> getListCompromissoCategoria() {
        return listCompromissoCategoria;
    }

    public void setListCompromissoCategoria(List<SelectItem> listCompromissoCategoria) {
        this.listCompromissoCategoria = listCompromissoCategoria;
    }

    public Integer getIdCompromissoCategoria() {
        return idCompromissoCategoria;
    }

    public void setIdCompromissoCategoria(Integer idCompromissoCategoria) {
        this.idCompromissoCategoria = idCompromissoCategoria;
    }

    public List<Compromisso> getListCompromissos() {
        return listCompromissos;
    }

    public void setListCompromissos(List<Compromisso> listCompromissos) {
        this.listCompromissos = listCompromissos;
    }

    public Boolean getVisibled() {
        return visibled;
    }

    public void setVisibled(Boolean visibled) {
        if (data != null) {
            compromisso.setData(DataHoje.converteData(data));
        }
        this.visibled = visibled;
    }

    public List<CompromissoUsuario> getListCompromissoUsuario() {
        return listCompromissoUsuario;
    }

    public void setListCompromissoUsuario(List<CompromissoUsuario> listCompromissoUsuario) {
        this.listCompromissoUsuario = listCompromissoUsuario;
    }

    public List<SelectItem> getListSemana() {
        return listSemana;
    }

    public void setListSemana(List<SelectItem> listSemana) {
        this.listSemana = listSemana;
    }

    public Integer getIdSemana() {
        return idSemana;
    }

    public void setIdSemana(Integer idSemana) {
        this.idSemana = idSemana;
    }

    public List<SelectItem> getListRepeticao() {
        return listRepeticao;
    }

    public void setListRepeticao(List<SelectItem> listRepeticao) {
        this.listRepeticao = listRepeticao;
    }

    public Integer getIdRepeticao() {
        return idRepeticao;
    }

    public void setIdRepeticao(Integer idRepeticao) {
        this.idRepeticao = idRepeticao;
    }

    public Boolean getSemanal() {
        return semanal;
    }

    public void setSemanal(Boolean semanal) {
        this.semanal = semanal;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public CompromissoUsuario getCompromissoUsuario() {
        return compromissoUsuario;
    }

    public void setCompromissoUsuario(CompromissoUsuario compromissoUsuario) {
        this.compromissoUsuario = compromissoUsuario;
    }

    public String getTipoHistorico() {
        return tipoHistorico;
    }

    public void setTipoHistorico(String tipoHistorico) {
        this.tipoHistorico = tipoHistorico;
    }

    public String getTipoData() {
        return tipoData;
    }

    public void setTipoData(String tipoData) {
        this.tipoData = tipoData;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getCancelados() {
        return cancelados;
    }

    public void setCancelados(String cancelados) {
        this.cancelados = cancelados;
    }

    public Integer getIdUsuarioFiltro() {
        return idUsuarioFiltro;
    }

    public void setIdUsuarioFiltro(Integer idUsuarioFiltro) {
        this.idUsuarioFiltro = idUsuarioFiltro;
    }

}
