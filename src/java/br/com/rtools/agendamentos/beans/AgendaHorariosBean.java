package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.agendamentos.dao.AgendaHorariosDao;
import br.com.rtools.associativo.Convenio;
import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.GrupoConvenioDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.WSSocket;
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
public class AgendaHorariosBean implements Serializable {

    private AgendaHorarios horarios;
    private AgendaHorarios horariosReativar;
    private List<AgendaHorarios> listHorarios;
    private List<SelectItem> listSemana;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listConvenio;
    private List<SelectItem> listGrupoConvenio;
    private List<SelectItem> listSubGrupoConvenio;
    private String horaInicial;
    private String horaFinal;
    private Boolean comIntervalo;
    private int intervalo;
    private int intInicial;
    private int intFinal;
    private int quantidade;
    private Integer idConvenio;
    private Integer idSubGrupoConvenio;
    private Integer idGrupoConvenio;
    private Integer idFilial;
    private Integer idSemana;
    private Boolean web;
    private Boolean socio;

    @PostConstruct
    public void init() {
        web = false;
        socio = false;
        horarios = new AgendaHorarios();
        horariosReativar = new AgendaHorarios();
        horaInicial = "";
        horaFinal = "";
        comIntervalo = false;
        intervalo = 0;
        intInicial = 0;
        intFinal = 0;
        quantidade = 0;
        idFilial = 0;
        idSemana = 1;
        listSemana = new ArrayList<>();
        listFiliais = new ArrayList<>();
        listHorarios = new ArrayList<>();
        listConvenio = new ArrayList<>();
        listGrupoConvenio = new ArrayList<>();
        listSubGrupoConvenio = new ArrayList<>();
        loadListFiliais();
        loadListSemana();
        loadListGrupoConvenio();
        loadListSubGrupoConvenio();
        loadListConvenio();
        loadListHorarios();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("agendaHorariosBean");
    }

    public void save() {
        AgendaHorariosDao horariosDao = new AgendaHorariosDao();
        Dao dao = new Dao();
        if (comIntervalo) {
            if ((intervalo < 10) || (intervalo > 30)) {
                GenericaMensagem.warn("Validação", "Intervalo de Horário inválido!");
                return;
            }
            if (horarios.getQuantidade() <= 0) {
                GenericaMensagem.warn("Validação", "Digite a quantidade para este Horário!");
                return;
            }
            intInicial = Integer.parseInt(horaInicial.substring(0, 2) + horaInicial.substring(3, 5));
            intFinal = Integer.parseInt(horaFinal.substring(0, 2) + horaFinal.substring(3, 5));
            String strHoras = horaInicial.substring(0, 2);
            String strMinutos = horaInicial.substring(3, 5);
            String horarioIns = "";
            int soma = 0;
            horarios.setHora(strHoras + ":" + strMinutos);
            horarios.setFilial((Filial) dao.find(new Filial(), idFilial));
            horarios.setSemana((Semana) dao.find(new Semana(), idSemana));
            horarios.setSubGrupoConvenio((SubGrupoConvenio) dao.find(new SubGrupoConvenio(), idSubGrupoConvenio));
            horarios.setConvenio((Pessoa) dao.find(new Pessoa(), idConvenio));
            horarios.setWeb(web);
            horarios.setSocio(socio);
            quantidade = horarios.getQuantidade();
            if (!horariosDao.findByFilial(horarios.getFilial().getId(), horarios.getHora(), horarios.getSemana().getId(), idSubGrupoConvenio, idConvenio).isEmpty()) {
                GenericaMensagem.warn("Validação", "Horário já cadastrado!");
                return;
            }
            dao.save(horarios, true);
            horarios = new AgendaHorarios();
            while (intInicial < intFinal) {
                if ((intInicial + intervalo) < intFinal) {
                    soma = Integer.parseInt(strMinutos) + intervalo;
                    if (soma >= 60) {
                        strHoras = Integer.toString(Integer.parseInt(strHoras) + 1);
                        if (soma == 60) {
                            strMinutos = "00";
                        } else {
                            strMinutos = Integer.toString(soma - 60);
                        }
                        horarioIns = ("00" + strHoras).substring(
                                ("00" + strHoras).length() - 2, ("00" + strHoras).length()) + ":"
                                + ("00" + strMinutos).substring(
                                        ("00" + strMinutos).length() - 2, ("00" + strMinutos).length());
                        horarios.setHora(horarioIns);
                    } else {
                        strMinutos = Integer.toString(soma);
                        horarioIns = ("00" + strHoras).substring(
                                ("00" + strHoras).length() - 2, ("00" + strHoras).length()) + ":"
                                + ("00" + strMinutos).substring(
                                        ("00" + strMinutos).length() - 2, ("00" + strMinutos).length());
                        horarios.setHora(horarioIns);
                    }
                    intInicial = Integer.parseInt(horarios.getHora().substring(0, 2) + horarios.getHora().substring(3, 5));
                    horarios.setFilial((Filial) dao.find(new Filial(), idFilial));
                    horarios.setSemana((Semana) dao.find(new Semana(), idSemana));
                    horarios.setSubGrupoConvenio((SubGrupoConvenio) dao.find(new SubGrupoConvenio(), idSubGrupoConvenio));
                    horarios.setConvenio((Pessoa) dao.find(new Pessoa(), idConvenio));
                    horarios.setWeb(web);
                    horarios.setSocio(socio);
                    if (!horariosDao.findByFilial(idFilial, horarios.getHora(), horarios.getSemana().getId(), idSubGrupoConvenio, idConvenio).isEmpty()) {
                        horarios = new AgendaHorarios();
                        continue;
                    }

                    horarios.setQuantidade(quantidade);
                    if (dao.save(horarios, true)) {
                        GenericaMensagem.info("Sucesso", "Registro adicionado");
                        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                    } else {
                        GenericaMensagem.warn("Erro", "Ao adicionar registro");
                    }
                    horarios = new AgendaHorarios();
                } else {
                    break;
                }
            }
            loadListHorarios();
        } else {
            if (horarios.getHora().equals("")) {
                GenericaMensagem.warn("Validação", "Digite o horário!");
                return;
            }
            if (horarios.getQuantidade() <= 0) {
                GenericaMensagem.warn("Validação", "Digite a quantidade para este Horário!");
                return;
            }

            List hors = horariosDao.findByFilial(idFilial, horarios.getHora(), idSemana, idSubGrupoConvenio, idConvenio);
            if (horarios.getId() == null) {
                if (!hors.isEmpty()) {
                    GenericaMensagem.warn("Validação", "Horário já cadastrado!");
                    return;
                }
                horarios.setFilial((Filial) dao.find(new Filial(), idFilial));
                horarios.setSemana((Semana) dao.find(new Semana(), idSemana));
                horarios.setSubGrupoConvenio((SubGrupoConvenio) dao.find(new SubGrupoConvenio(), idSubGrupoConvenio));
                horarios.setConvenio((Pessoa) dao.find(new Pessoa(), idConvenio));
                horarios.setWeb(web);
                horarios.setSocio(socio);
                if (dao.save(horarios, true)) {
                    GenericaMensagem.info("Sucesso", "Registro adicionado");
                    horarios = new AgendaHorarios();
                } else {
                    GenericaMensagem.warn("Erro", "Ao adicionar registro");
                }
            } else {
                if (!hors.isEmpty()
                        && ((AgendaHorarios) hors.get(0)).getQuantidade().equals(horarios.getQuantidade())) {
                    GenericaMensagem.warn("Validação", "Horário já cadastrado!");
                    return;
                }
                if (dao.update(horarios, true)) {
                    GenericaMensagem.info("Sucesso", "Registro atualizado");
                    horarios = new AgendaHorarios();
                    WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                } else {
                    GenericaMensagem.warn("Erro", "Ao atualizar registro");
                }
            }
            loadListHorarios();
            horariosReativar = new AgendaHorarios();
        }
    }

    public void reativar() {
        if (horariosReativar.getId() != null) {
            Dao dao = new Dao();
            horariosReativar.setAtivo(true);
            if (dao.update(horariosReativar, true)) {
                GenericaMensagem.info("Sucesso", "Horário reativado");
                WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
            } else {
                GenericaMensagem.warn("Erro", "Ao reativar horário!");
            }
            loadListHorarios();
        }
        horariosReativar = new AgendaHorarios();
    }

    public void edit(AgendaHorarios h1) {
        horariosReativar = h1;
    }

    public String editQuantidade(AgendaHorarios h) {
        Dao dao = new Dao();
        if (h.getQuantidade() <= 0) {
            h.setQuantidade(0);
            h.setAtivo(false);
        }
        GenericaMensagem.info("Sucesso", "Registro atualizado");
        dao.update(h, true);
        loadListHorarios();
        return null;
    }

    public void update(AgendaHorarios h) {
        Dao dao = new Dao();
        GenericaMensagem.info("Sucesso", "Registro atualizado");
        dao.update(h, true);
    }

    public void delete(AgendaHorarios h) {
        Dao dao = new Dao();
        if (h.getId() != null) {
            if (dao.delete(h, true)) {
                GenericaMensagem.info("Sucesso", "Registro excluído");
                WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
            } else {
                h.setAtivo(false);
                if (dao.update(h, true)) {
                    GenericaMensagem.info("Sucesso", "Registro inátivado");
                    WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                }
            }
            loadListHorarios();
        }
    }

    public List<SelectItem> getListSemana() {
        return listSemana;
    }

    public void setListSemana(List<SelectItem> listSemana) {
        this.listSemana = listSemana;
    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public void loadListSemana() {
        listSemana = new ArrayList();
        Dao dao = new Dao();
        List<Semana> list = (List<Semana>) dao.list(new Semana());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSemana = list.get(i).getId();
            }
            listSemana.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListFiliais() {
        listFiliais = new ArrayList();
        Dao dao = new Dao();
        List<Filial> list = (List<Filial>) dao.list(new Filial(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            listFiliais.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getDocumento() + " / " + list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public void loadListGrupoConvenio() {
        listGrupoConvenio = new ArrayList();
        List<GrupoConvenio> list = (List<GrupoConvenio>) new GrupoConvenioDao().findAllToAgendaHorarios();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupoConvenio = list.get(i).getId();
            }
            listGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListConvenio() {
        listConvenio = new ArrayList();
        List<Pessoa> list = (List<Pessoa>) new ConvenioDao().findAllBySubGrupoConvenio(idSubGrupoConvenio);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idConvenio = list.get(i).getId();
            }
            listConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
        }
    }

    public void loadListSubGrupoConvenio() {
        listSubGrupoConvenio = new ArrayList();
        Dao dao = new Dao();
        List<SubGrupoConvenio> list = new SubGrupoConvenioDao().findAllByGrupoAndAgendamento(idGrupoConvenio);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSubGrupoConvenio = list.get(i).getId();
            }
            listSubGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public AgendaHorarios getFeriados() {
        return horarios;
    }

    public void setFeriados(AgendaHorarios horarios) {
        this.setHorarios(horarios);
    }

    public AgendaHorarios getHorarios() {
        return horarios;
    }

    public void setHorarios(AgendaHorarios horarios) {
        this.horarios = horarios;
    }

    public int getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }

    public int getIntInicial() {
        return intInicial;
    }

    public void setIntInicial(int intInicial) {
        this.intInicial = intInicial;
    }

    public int getIntFinal() {
        return intFinal;
    }

    public void setIntFinal(int intFinal) {
        this.intFinal = intFinal;
    }

    public String getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Integer getIdSemana() {
        return idSemana;
    }

    public void setIdSemana(Integer idSemana) {
        this.idSemana = idSemana;
    }

    public void listener(String tcase) {
        if (tcase.endsWith("subgrupo_convenio")) {
            loadListSubGrupoConvenio();
            loadListConvenio();
            loadListHorarios();
        } else if (tcase.endsWith("convenio")) {
            loadListConvenio();
            loadListHorarios();
        }

    }

    public void loadListHorarios() {
        listHorarios = new ArrayList();
        listHorarios = new AgendaHorariosDao().findBy(idFilial, idSemana, idSubGrupoConvenio, idConvenio);
    }

    public List<AgendaHorarios> getListHorarios() {
        return listHorarios;
    }

    public void setListHorarios(List<AgendaHorarios> listHorarios) {
        this.listHorarios = listHorarios;
    }

    public Boolean getComIntervalo() {
        return comIntervalo;
    }

    public void setComIntervalo(Boolean comIntervalo) {
        this.comIntervalo = comIntervalo;
    }

    public AgendaHorarios getHorariosReativar() {
        return horariosReativar;
    }

    public void setHorariosReativar(AgendaHorarios horariosReativar) {
        this.horariosReativar = horariosReativar;
    }

    public List<SelectItem> getListConvenio() {
        return listConvenio;
    }

    public void setListConvenio(List<SelectItem> listConvenio) {
        this.listConvenio = listConvenio;
    }

    public List<SelectItem> getListSubGrupoConvenio() {
        return listSubGrupoConvenio;
    }

    public void setListSubGrupoConvenio(List<SelectItem> listSubGrupoConvenio) {
        this.listSubGrupoConvenio = listSubGrupoConvenio;
    }

    public Integer getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Integer idConvenio) {
        this.idConvenio = idConvenio;
    }

    public Integer getIdSubGrupoConvenio() {
        return idSubGrupoConvenio;
    }

    public void setIdSubGrupoConvenio(Integer idSubGrupoConvenio) {
        this.idSubGrupoConvenio = idSubGrupoConvenio;
    }

    public List<SelectItem> getListGrupoConvenio() {
        return listGrupoConvenio;
    }

    public void setListGrupoConvenio(List<SelectItem> listGrupoConvenio) {
        this.listGrupoConvenio = listGrupoConvenio;
    }

    public Integer getIdGrupoConvenio() {
        return idGrupoConvenio;
    }

    public void setIdGrupoConvenio(Integer idGrupoConvenio) {
        this.idGrupoConvenio = idGrupoConvenio;
    }

    public Boolean getWeb() {
        return web;
    }

    public void setWeb(Boolean web) {
        this.web = web;
    }

    public Boolean getSocio() {
        return socio;
    }

    public void setSocio(Boolean socio) {
        this.socio = socio;
    }

}
