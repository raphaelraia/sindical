package br.com.rtools.homologacao.beans;

import br.com.rtools.homologacao.AcrescentarHorario;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.dao.AcrescentarHorarioDao;
import br.com.rtools.homologacao.dao.HorariosDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Semana;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Tabbed;
import br.com.rtools.utilitarios.WSSocket;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class AcrescentarHorarioBean implements Serializable {

    private AcrescentarHorario acrescentarHorario;
    private Horarios horarios;
    private MacFilial macFilial;
    private Filial filial;
    private List<AcrescentarHorario> listaHorariosAdicionados;
    private List<SelectItem> listaFiliais;
    private List<SelectItem> listSemana;
    private List<SelectItem> listHorarios;
    private Integer idFilial;
    private Integer idSemana;
    private Integer nrQuantidadeDisponivel;
    private Integer nrQuantidadeAdicionado;
    private Integer nrQuantidadeAdicionar;
    private Integer nrQuantidadeAdicionarTodos;
    private Integer nrQuantidadeDisponivelB;
    private Integer nrQuantidadeAdicionadoB;
    private Integer nrQuantidadeAdicionalB;
    private Date data;
    private Date dataInicial = DataHoje.dataHoje();
    private Date dataFinal;
    private Integer idHorariosDisponiveis;
    private Integer idHorario;
    private boolean desabilitaBotoes;
    private boolean desabilitaFilial;
    private String tipoAcrescimo;
    private Boolean habilitaSemana;
    private Boolean habilitaHorarios;
    private String periodo;
    private Integer nrQuantidadeAdicionarTodos2;
    private String periodo2;

    @PostConstruct
    public void init() {
        acrescentarHorario = new AcrescentarHorario();
        horarios = new Horarios();
        macFilial = new MacFilial();
        filial = new Filial();
        listaHorariosAdicionados = new ArrayList();
        listaFiliais = new ArrayList<>();
        listSemana = new ArrayList<>();
        listHorarios = new ArrayList<>();
        idFilial = 0;
        idSemana = 0;
        nrQuantidadeDisponivel = 0;
        nrQuantidadeAdicionado = 0;
        nrQuantidadeAdicionar = 0;
        nrQuantidadeAdicionarTodos = 0;
        nrQuantidadeAdicionarTodos2 = 0;
        nrQuantidadeDisponivelB = 0;
        nrQuantidadeAdicionadoB = 0;
        nrQuantidadeAdicionalB = 0;
        data = DataHoje.dataHoje();
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        idHorariosDisponiveis = 0;
        idHorario = 0;
        desabilitaBotoes = false;
        desabilitaFilial = false;
        tipoAcrescimo = "Dia";
        habilitaSemana = false;
        habilitaHorarios = false;
        periodo = "";
        periodo2 = "";
        new Tabbed().setTitle("1");
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("acrescentarHorarioBean");
        GenericaSessao.remove("tabbedBean");
    }

    public void clear(Integer tCase) {
        if (tCase == 1) {
            listHorarios.clear();
            getListHorarios();
            calculaQuantidadeDisponivel();
        } else if (tCase == 2) {
            calculaQuantidadeDisponivel();
        } else {

        }
    }

    public void acrescentarHorarios(boolean todos) {
        if (getListaHorariosDisponiveis().isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum horário a ser acrescido!");
            return;
        }
        if (todos) {
            if (nrQuantidadeAdicionarTodos == 0) {
                GenericaMensagem.warn("Validação", "Digite uma quantidade!");
                return;
            }
        } else if (nrQuantidadeAdicionar == 0) {
            GenericaMensagem.warn("Validação", "Digite uma quantidade!");
            return;
        }
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Dao dao = new Dao();
        AcrescentarHorarioDao db = new AcrescentarHorarioDao();
        AcrescentarHorario ah;
        boolean erro = false;
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        Integer horaInicioManha = 700;
        Integer horaMeioDia = 1200;
        Integer horaFinalTarde = 1900;
        dao.openTransaction();
        for (int i = 0; i < getListaHorariosDisponiveis().size(); i++) {
            acrescentarHorario.setFilial((Filial) dao.find(new Filial(), Integer.parseInt(getListaFiliais().get(idFilial).getDescription())));
            acrescentarHorario.setUsuario(u);
            if (todos) {
                Horarios h = (Horarios) dao.find(new Horarios(), Integer.parseInt(getListaHorariosDisponiveis().get(i).getDescription()));
                if (periodo.equals("todos")) {
                    acrescentarHorario.setHorarios(h);
                } else {
                    Integer horario = Integer.parseInt(h.getHora().replace(":", ""));
                    if (periodo.equals("manha")) {
                        if (horario >= horaInicioManha && horario < horaMeioDia) {
                            acrescentarHorario.setHorarios(h);
                        }
                    } else if (periodo.equals("tarde")) {
                        if (horario >= horaMeioDia && horario < horaFinalTarde) {
                            acrescentarHorario.setHorarios(h);
                        }
                    }
                }
            } else {
                acrescentarHorario.setHorarios((Horarios) dao.find(new Horarios(), Integer.parseInt(getListaHorariosDisponiveis().get(idHorariosDisponiveis).getDescription())));
            }
            if (acrescentarHorario.getHorarios().getId() != -1) {
                ah = db.pesquisaAcrescimoHorario(data, acrescentarHorario.getHorarios().getId(), acrescentarHorario.getFilial().getId());
                if (ah.getId() == null) {
                    acrescentarHorario.setDtData(data);
                    if (todos) {
                        acrescentarHorario.setQuantidade(nrQuantidadeAdicionarTodos);
                    } else {
                        acrescentarHorario.setQuantidade(nrQuantidadeAdicionar);
                    }
                    if (dao.save(acrescentarHorario)) {
                        acrescentarHorario = new AcrescentarHorario();
                        nrQuantidadeDisponivel = 0;
                        erro = false;

                    } else {
                        erro = true;
                        break;
                    }
                } else {
                    acrescentarHorario = ah;
                    if (todos) {
                        acrescentarHorario.setQuantidade(ah.getQuantidade() + nrQuantidadeAdicionarTodos);
                    } else {
                        acrescentarHorario.setQuantidade(ah.getQuantidade() + nrQuantidadeAdicionar);
                    }
                    if (dao.update(acrescentarHorario)) {
                        acrescentarHorario = new AcrescentarHorario();
                        nrQuantidadeDisponivel = 0;
                        erro = false;
                    } else {
                        erro = true;
                        break;
                    }
                }
            }
            if (!todos) {
                break;
            }
        }

        if (erro) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Erro ao adicionado horário(s)!");
            return;
        } else {
            dao.commit();
            GenericaMensagem.info("Sucesso", "Horário adicionado com sucesso.");
            getListaHorariosDisponiveis().clear();
            sisProcesso.finish();
            WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
        }
        nrQuantidadeDisponivel = 0;
        nrQuantidadeAdicionarTodos = 0;
        listaHorariosAdicionados.clear();
        acrescentarHorario = new AcrescentarHorario();
        calculaQuantidadeDisponivel();
    }

    public String acrescentarHorarioPeriodo() {
        Integer horaInicioManha = 700;
        Integer horaMeioDia = 1200;
        Integer horaFinalTarde = 1900;
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        sisProcesso.setProcesso("Acrescentar horários");
        Date date = DataHoje.dataHoje();
        int intDataHoje = DataHoje.converteDataParaInteger(DataHoje.converteData(date));
        int intDataInicial = DataHoje.converteDataParaInteger(DataHoje.converteData(getDataInicial()));
        int intDataFinal = DataHoje.converteDataParaInteger(DataHoje.converteData(dataFinal));
        String strDataInicial = DataHoje.converteData(getDataInicial());

        if (intDataInicial < intDataHoje) {
            GenericaMensagem.warn("Sistema", "A data inicial tem que ser maior ou igual a data de hoje!");
            return null;
        }

        if (intDataFinal < intDataHoje) {
            GenericaMensagem.warn("Sistema", "A data final tem que ser maior ou igual a data de hoje!");
            return null;
        }

        if (intDataFinal < intDataInicial) {
            GenericaMensagem.warn("Sistema", "A data final tem que ser maior ou igual que a data inicial!");
            return null;
        }

        Dao dao = new Dao();
        Filial f = (Filial) dao.find(new Filial(), Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()));
        HorariosDao horariosDao = new HorariosDao();
        DataHoje dataHoje = new DataHoje();
        List listDatas = new ArrayList();
        int i = 0;
        int y = 0;
        while (i == y) {
            if (i > 0) {
                strDataInicial = dataHoje.incrementarDias(1, strDataInicial);
            }
            intDataInicial = DataHoje.converteDataParaInteger(strDataInicial);
            if (intDataInicial > intDataFinal) {
                y = i + 1;
            } else {
                listDatas.add(i, strDataInicial);
            }
            y++;
            i++;
            if (i == 100) {
                break;
            }
        }
        boolean erro = false;
        List<Horarios> horarioses = new ArrayList<>();
        AcrescentarHorarioDao acrescentarHorarioDao = new AcrescentarHorarioDao();
        AcrescentarHorario ah;
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        dao.openTransaction();
        for (int z = 0; z < listDatas.size(); z++) {
            horarioses.clear();
            acrescentarHorario = new AcrescentarHorario();
            strDataInicial = listDatas.get(z).toString();
            if (!habilitaSemana && !habilitaHorarios) {
                horarioses = horariosDao.pesquisaTodosPorFilial(f.getId(), DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)));
            } else if (habilitaSemana && !habilitaHorarios) {
                if (listSemana.isEmpty()) {
                    return null;
                }
                if (DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)) == Integer.parseInt(listSemana.get(idSemana).getDescription())) {
                    horarioses = horariosDao.pesquisaTodosPorFilial(f.getId(), DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)));
                }
            } else if (!habilitaSemana && habilitaHorarios) {
                if (listHorarios.isEmpty()) {
                    return null;
                }
                horarioses = horariosDao.pesquisaPorHorarioFilial(f.getId(), listHorarios.get(idHorario).getDescription());
            } else if (habilitaSemana && habilitaHorarios) {
                if (DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)) == Integer.parseInt(listSemana.get(idSemana).getDescription())) {
                    if (listHorarios.isEmpty() || listSemana.isEmpty()) {
                        return null;
                    }
                    horarioses = horariosDao.pesquisaPorHorarioFilial(f.getId(), listHorarios.get(idHorario).getDescription(), DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)));
                }
            }
            erro = false;
            for (int x = 0; x < horarioses.size(); x++) {
                if (habilitaHorarios && habilitaSemana) {
                    ah = acrescentarHorarioDao.pesquisaAcrescimoHorarioSemana(DataHoje.converte(strDataInicial), horarioses.get(x).getId(), f.getId(), horarioses.get(x).getSemana().getId());
                } else {
                    ah = acrescentarHorarioDao.pesquisaAcrescimoHorario(DataHoje.converte(strDataInicial), horarioses.get(x).getId(), f.getId());
                }
                Boolean save = true;
                acrescentarHorario.setUsuario(u);
                if (ah.getId() == null) {
                    acrescentarHorario.setFilial(f);
                    acrescentarHorario.setHorarios(horarioses.get(x));
                    acrescentarHorario.setDtData(DataHoje.converte(strDataInicial));
                    if (horarioses.get(x).getQuantidade() > 0) {
                        if (habilitaHorarios && habilitaSemana) {
                            if (nrQuantidadeAdicionalB == 0) {
                                acrescentarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                            } else {
                                int resto = horarioses.get(x).getQuantidade() - nrQuantidadeAdicionalB;
                                if (resto == 0) {
                                    break;
                                } else if (resto < 1) {
                                    break;
                                } else {
                                    acrescentarHorario.setQuantidade(resto);
                                }
                            }
                        } else {
                            Integer day_of_month_a = acrescentarHorario.getDtData().getDay();
                            Integer day_of_month_b = horarioses.get(x).getSemana().getId() - 1;
                            if (Objects.equals(day_of_month_a, day_of_month_b)) {
                                save = true;
                                acrescentarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                            } else {
                                save = false;
                            }
                        }
                    } else {
                        acrescentarHorario.setQuantidade(0);
                    }
                    if (save) {
                        if (dao.save(acrescentarHorario)) {
                            acrescentarHorario = new AcrescentarHorario();
                            erro = false;
                        } else {
                            erro = true;
                            break;
                        }
                    }
                } else {
                    boolean delete = false;
                    acrescentarHorario = ah;
                    if (horarioses.get(x).getQuantidade() > 0) {
                        if (habilitaHorarios && habilitaSemana) {
                            if (nrQuantidadeAdicionalB == 0) {
                                acrescentarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                            } else {
                                int qtdeAdicionada = horarioses.get(x).getQuantidade() - ah.getQuantidade();
                                int resto = qtdeAdicionada - nrQuantidadeAdicionalB;
                                if (resto == 0) {
                                    delete = true;
                                } else if (horarioses.get(x).getQuantidade() == nrQuantidadeAdicionalB) {
                                    delete = true;
                                } else if (resto < 1) {
                                    delete = false;
                                    resto = horarioses.get(x).getQuantidade() - nrQuantidadeAdicionalB;
                                    acrescentarHorario.setQuantidade(resto);
                                } else {
                                    delete = false;
                                    acrescentarHorario.setQuantidade(resto);
                                }
                            }
                        } else {
                            acrescentarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                        }
                    } else {
                        acrescentarHorario.setQuantidade(0);
                    }
                    if (!delete) {
                        if (dao.update(acrescentarHorario)) {
                            acrescentarHorario = new AcrescentarHorario();
                            erro = false;
                        } else {
                            erro = true;
                            break;
                        }
                    } else if (dao.delete(acrescentarHorario)) {
                        acrescentarHorario = new AcrescentarHorario();
                        erro = false;
                    } else {
                        erro = true;
                        break;
                    }
                    delete = false;
                }
            }
        }

        if (erro) {
            GenericaMensagem.warn("Erro", "Erro ao acrescentar horário(s) do período!");
            dao.rollback();
            return null;
        }
        dao.commit();
        listaHorariosAdicionados.clear();
        acrescentarHorario = new AcrescentarHorario();
        sisProcesso.finish();
        GenericaMensagem.info("Sucesso", "Horários adicionados com sucesso");
        WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
        return null;
    }

    public void excluir(AcrescentarHorario ah) {
        Dao dao = new Dao();
        ah = (AcrescentarHorario) dao.find(new AcrescentarHorario(), ah.getId());
        if (ah != null) {
            if (ah.getId() != null) {
                dao.openTransaction();
                if (dao.delete(ah)) {
                    dao.commit();
                    GenericaMensagem.info("Sucesso", "Registro excluído com sucesso.");
                    WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                } else {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Erro ao excluir horário adicionado!");
                    return;
                }
            }
        }
        calculaQuantidadeDisponivel();
        getListaHorariosAdicionados().clear();
    }

    public List<SelectItem> getListaFiliais() {
        if (listaFiliais.isEmpty()) {
            getFilial();
            Dao dao = new Dao();
            List<Filial> select = new ArrayList<>();
            if (filial.getId() != -1) {
                select.add((Filial) dao.find(new Filial(), filial.getId()));
            } else {
                select = (List<Filial>) dao.list(new Filial(), true);
            }
            for (int i = 0; i < select.size(); i++) {
                listaFiliais.add(
                        new SelectItem(
                                i,
                                select.get(i).getFilial().getPessoa().getDocumento() + " / " + select.get(i).getFilial().getPessoa().getNome(),
                                Integer.toString(select.get(i).getId())));
            }
        }
        return listaFiliais;
    }

    public void setListaFiliais(List<SelectItem> listaFiliais) {
        this.listaFiliais = listaFiliais;
    }

    public List<SelectItem> getListaHorariosDisponiveis() {
        getListaHorariosAdicionados();
        List<SelectItem> result = new ArrayList<>();
        HorariosDao horariosDao = new HorariosDao();
        List<Horarios> select = horariosDao.listaTodosHorariosDisponiveisPorFilial(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), data, false);
        if (select.isEmpty()) {
            desabilitaBotoes = true;
            idHorariosDisponiveis = 0;
        } else {
            desabilitaBotoes = false;
            for (int i = 0; i < select.size(); i++) {
                result.add(
                        new SelectItem(
                                i,
                                select.get(i).getHora(),
                                Integer.toString(select.get(i).getId())));
            }
        }
        return result;
    }

    public AcrescentarHorario getAcrescentarHorario() {
        return acrescentarHorario;
    }

    public void setAcrescentarHorario(AcrescentarHorario acrescentarHorario) {
        this.acrescentarHorario = acrescentarHorario;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public List<AcrescentarHorario> getListaHorariosAdicionados() {
        listaHorariosAdicionados.clear();
        switch (getTipoAcrescimo()) {
            case "Dia": {
                AcrescentarHorarioDao acrescentarHorarioDao = new AcrescentarHorarioDao();
                listaHorariosAdicionados = acrescentarHorarioDao.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), data, null);
                break;
            }
            case "Período": {
                AcrescentarHorarioDao acrescentarHorarioDao = new AcrescentarHorarioDao();
                listaHorariosAdicionados = acrescentarHorarioDao.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), dataInicial, dataFinal);
                break;
            }
        }
        return listaHorariosAdicionados;
    }

    public void setListaHorariosAdicionados(List<AcrescentarHorario> listaHorariosAdicionados) {
        this.listaHorariosAdicionados = listaHorariosAdicionados;
    }

    public Integer getIdHorariosDisponiveis() {
        return idHorariosDisponiveis;
    }

    public void setIdHorariosDisponiveis(Integer idHorariosDisponiveisI) {
        this.idHorariosDisponiveis = idHorariosDisponiveisI;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void dataListener(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.data = DataHoje.converte(format.format(event.getObject()));
        calculaQuantidadeDisponivel();
    }

    public void dataFinalListener(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataFinal = DataHoje.converte(format.format(event.getObject()));
    }

    public void calculaQuantidadeDisponivel() {
        nrQuantidadeAdicionar = 0;
        nrQuantidadeAdicionado = 0;
        nrQuantidadeDisponivel = 0;
        nrQuantidadeAdicionalB = 0;
        nrQuantidadeAdicionadoB = 0;
        nrQuantidadeDisponivelB = 0;
        AcrescentarHorarioDao acrescentarHorarioDao = new AcrescentarHorarioDao();
        int idHorariox = -1;
        if (getTipoAcrescimo().equals("Dia")) {
            if (!getListaHorariosDisponiveis().isEmpty()) {
                idHorariox = Integer.parseInt(getListaHorariosDisponiveis().get(idHorariosDisponiveis).getDescription());
                Dao dao = new Dao();
                horarios = (Horarios) dao.find(new Horarios(), idHorariox);
            } else {
                horarios = new Horarios();
                idHorariosDisponiveis = 0;
            }
            if (horarios.getId() != -1) {
                AcrescentarHorario acrescentarHorarioA = acrescentarHorarioDao.pesquisaAcrescimoHorario(data, idHorariox, Integer.parseInt(getListaFiliais().get(idFilial).getDescription()));
                if (acrescentarHorarioA != null) {
//                    if (horarios.getQuantidade() > 0) {
//                        if (acrescentarHorarioA.getQuantidade() > horarios.getQuantidade()) {
//                            nrQuantidadeDisponivel = 0;
//                        } else {
//                            nrQuantidadeDisponivel = horarios.getQuantidade() - acrescentarHorarioA.getQuantidade();
//                        }
//                    }
                    nrQuantidadeAdicionado = acrescentarHorarioA.getQuantidade();
//                } else {
                }
                nrQuantidadeDisponivel = horarios.getQuantidade();
            }
        } else if (getTipoAcrescimo().equals("Período")) {
            if (habilitaHorarios && habilitaSemana) {
                if (!getListaHorariosDisponiveis().isEmpty()) {
                    idHorariox = Integer.parseInt(getListaHorariosDisponiveis().get(idHorario).getDescription());
                    Dao dao = new Dao();
                    horarios = (Horarios) dao.find(new Horarios(), idHorariox);
                } else {
                    horarios = new Horarios();
                    idHorariosDisponiveis = 0;
                }
                if (horarios.getId() != -1) {
                    List<?> list = new HorariosDao().pesquisaPorHorarioFilial(Integer.parseInt(getListaFiliais().get(idFilial).getDescription()), horarios.getHora(), Integer.parseInt(getListSemana().get(idSemana).getDescription()));
                    Horarios hx = ((List<Horarios>) list).get(0);
                    if (hx != null) {
                        nrQuantidadeDisponivelB = hx.getQuantidade();
                    } else {
                        nrQuantidadeDisponivelB = 0;
                    }
                }
            }
        }
    }

    public int calculaQuantidadeDisponivel(Integer quantidadeDisponivel, Integer quantidadeAdicionada) {
        int quantidadeRestante = quantidadeDisponivel + quantidadeAdicionada;
        return quantidadeRestante;
    }

    public void validaQuantidadeDisponivel() {
        if (nrQuantidadeDisponivel > 0) {
            if (nrQuantidadeAdicionar > nrQuantidadeDisponivel) {
                nrQuantidadeAdicionar = nrQuantidadeDisponivel;
            }
        }
    }

    public void validaQuantidadeDisponivelB() {
        if (nrQuantidadeDisponivelB > 0) {
            if (nrQuantidadeAdicionalB > nrQuantidadeDisponivelB) {
                nrQuantidadeAdicionalB = nrQuantidadeDisponivelB;
            }
        }
    }

    public Integer getNrQuantidadeDisponivel() {
        return nrQuantidadeDisponivel;
    }

    public void setNrQuantidadeDisponivel(Integer nrQuantidadeDisponivel) {
        this.nrQuantidadeDisponivel = nrQuantidadeDisponivel;
    }

    public Horarios getHorarios() {
        return horarios;
    }

    public void setHorarios(Horarios horarios) {
        this.horarios = horarios;
    }

    public Integer getNrQuantidadeAdicionado() {
        return nrQuantidadeAdicionado;
    }

    public void setNrQuantidadeAdicionado(Integer nrQuantidadeAdicionado) {
        this.nrQuantidadeAdicionado = nrQuantidadeAdicionado;
    }

    public Integer getNrQuantidadeAdicionar() {
        return nrQuantidadeAdicionar;
    }

    public void setNrQuantidadeAdicionar(Integer nrQuantidadeAdicionar) {
        this.nrQuantidadeAdicionar = nrQuantidadeAdicionar;
    }

    public boolean isDesabilitaBotoes() {
        return desabilitaBotoes;
    }

    public void setDesabilitaBotoes(boolean desabilitaBotoes) {
        this.desabilitaBotoes = desabilitaBotoes;
    }

    public MacFilial getMacFilial() {
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public Filial getFilial() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial") != null) {
            desabilitaFilial = true;
            filial = ((MacFilial) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("acessoFilial")).getFilial();
        }
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public boolean isDesabilitaFilial() {
        return desabilitaFilial;
    }

    public void setDesabilitaFilial(boolean desabilitaFilial) {
        this.desabilitaFilial = desabilitaFilial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void acrescentarPor(TabChangeEvent event) {
        periodo = "todos";
        periodo2 = "todos";
        nrQuantidadeAdicionarTodos = 0;
        nrQuantidadeAdicionarTodos2 = 0;
        switch (event.getTab().getTitle()) {
            case "Dia":
                ((Tabbed) GenericaSessao.getObject("tabbedBean")).setActiveIndex("1");
                setTipoAcrescimo("Dia");
                break;
            case "Período":
                setTipoAcrescimo("Período");
                ((Tabbed) GenericaSessao.getObject("tabbedBean")).setActiveIndex("2");
                break;
            default:
                ((Tabbed) GenericaSessao.getObject("tabbedBean")).setActiveIndex("1");
                setTipoAcrescimo("");
                break;
        }
        nrQuantidadeDisponivel = 0;
        nrQuantidadeAdicionar = 0;
        nrQuantidadeAdicionado = 0;
        nrQuantidadeDisponivelB = 0;
        nrQuantidadeAdicionalB = 0;
        nrQuantidadeAdicionadoB = 0;
        data = DataHoje.dataHoje();
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        calculaQuantidadeDisponivel();
    }

    public void excluirAcrescimos() {
        AcrescentarHorarioDao acrescentarHorario = new AcrescentarHorarioDao();
        List<AcrescentarHorario> list = new ArrayList();
        switch (getTipoAcrescimo()) {
            case "Dia":
                list = acrescentarHorario.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), data, null);
                break;
            case "Período":
                if (!habilitaSemana && !habilitaHorarios) {
                    list = acrescentarHorario.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), dataInicial, dataFinal);
                } else if (habilitaSemana && !habilitaHorarios) {
                    if (listSemana.isEmpty()) {
                        return;
                    }
                    list = acrescentarHorario.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), dataInicial, dataFinal, Integer.parseInt(listSemana.get(idSemana).getDescription()));
                } else if (!habilitaSemana && habilitaHorarios) {
                    list = acrescentarHorario.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), dataInicial, dataFinal, listHorarios.get(idHorario).getDescription());
                } else if (habilitaSemana && habilitaHorarios) {
                    list = acrescentarHorario.listaTodosHorariosAcrescentados(Integer.parseInt(listaFiliais.get(this.idFilial).getDescription()), dataInicial, dataFinal, Integer.parseInt(listSemana.get(idSemana).getDescription()), listHorarios.get(idHorario).getDescription());
                }
                break;
            default:
                return;
        }
        if (!list.isEmpty()) {
            boolean erro = false;
            Dao dao = new Dao();
            try {
                dao.openTransaction();
                for (int i = 0; i < list.size(); i++) {
                    AcrescentarHorario ah = (AcrescentarHorario) dao.find(new AcrescentarHorario(), list.get(i).getId());
                    if (ah != null) {
                        if (!dao.delete(ah)) {
                            erro = true;
                            break;
                        }
                    }
                }
                if (erro) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Erro ao excluir horários!");
                } else {
                    dao.commit();
                    calculaQuantidadeDisponivel();
                    getListaHorariosAdicionados().clear();
                    GenericaMensagem.info("Sucesso", "Horarios excluídos com sucesso.");
                    WSSocket.send("agendamento_" + ControleUsuarioBean.getCliente().toLowerCase());
                }
            } catch (Exception e) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao excluir horários!");
            }
        } else {
            GenericaMensagem.warn("Sistema", "Não existem horários a serem excluídos para data / período!");
        }
    }

    public String getTipoAcrescimo() {
        return tipoAcrescimo;
    }

    public void setTipoAcrescimo(String tipoAcrescimo) {
        this.tipoAcrescimo = tipoAcrescimo;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Boolean getHabilitaHorarios() {
        return habilitaHorarios;
    }

    public void setHabilitaHorarios(Boolean habilitaHorarios) {
        if (!habilitaHorarios) {
            listHorarios.clear();
            idHorario = 0;
        }
        this.habilitaHorarios = habilitaHorarios;
    }

    public Boolean getHabilitaSemana() {
        return habilitaSemana;
    }

    public void setHabilitaSemana(Boolean habilitaSemana) {
        if (!habilitaSemana) {
            idSemana = 0;
            listSemana.clear();
        }
        this.habilitaSemana = habilitaSemana;
    }

    public List<SelectItem> getListSemana() {
        if (listSemana.isEmpty()) {
            Dao dao = new Dao();
            List<Semana> list = dao.list(new Semana());
            for (int i = 0; i < list.size(); i++) {
                listSemana.add(new SelectItem(i, list.get(i).getDescricao(), "" + list.get(i).getId()));
            }
        }
        return listSemana;
    }

    public void setListSemana(List<SelectItem> listSemana) {
        this.listSemana = listSemana;
    }

    public List<SelectItem> getListHorarios() {
        if (listHorarios.isEmpty()) {
            HorariosDao horariosDao = new HorariosDao();
            List list;
            if (habilitaSemana) {
                list = horariosDao.listaHorariosAgrupadosPorFilialSemana(Integer.parseInt(getListaFiliais().get(idFilial).getDescription()), Integer.parseInt(getListSemana().get(idSemana).getDescription()));
            } else {
                list = horariosDao.listaHorariosAgrupadosPorFilialSemana(Integer.parseInt(getListaFiliais().get(idFilial).getDescription()), null);
            }
            idHorario = 0;
            for (int i = 0; i < list.size(); i++) {
                listHorarios.add(new SelectItem(i, list.get(i).toString(), "" + list.get(i).toString()));
            }
        }
        return listHorarios;
    }

    public void setListHorarios(List<SelectItem> listHorarios) {
        this.listHorarios = listHorarios;
    }

    public Integer getIdSemana() {
        return idSemana;
    }

    public void setIdSemana(Integer idSemana) {
        this.idSemana = idSemana;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public Integer getNrQuantidadeDisponivelB() {
        return nrQuantidadeDisponivelB;
    }

    public void setNrQuantidadeDisponivelB(Integer nrQuantidadeDisponivelB) {
        this.nrQuantidadeDisponivelB = nrQuantidadeDisponivelB;
    }

    public Integer getNrQuantidadeAdicionadoB() {
        return nrQuantidadeAdicionadoB;
    }

    public void setNrQuantidadeAdicionadoB(Integer nrQuantidadeAdicionadoB) {
        this.nrQuantidadeAdicionadoB = nrQuantidadeAdicionadoB;
    }

    public Integer getNrQuantidadeAdicionalB() {
        return nrQuantidadeAdicionalB;
    }

    public void setNrQuantidadeAdicionalB(Integer nrQuantidadeAdicionalB) {
        this.nrQuantidadeAdicionalB = nrQuantidadeAdicionalB;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public Integer getNrQuantidadeAdicionarTodos() {
        return nrQuantidadeAdicionarTodos;
    }

    public void setNrQuantidadeAdicionarTodos(Integer nrQuantidadeAdicionarTodos) {
        this.nrQuantidadeAdicionarTodos = nrQuantidadeAdicionarTodos;
    }

    public Integer getNrQuantidadeAdicionarTodos2() {
        return nrQuantidadeAdicionarTodos2;
    }

    public void setNrQuantidadeAdicionarTodos2(Integer nrQuantidadeAdicionarTodos2) {
        this.nrQuantidadeAdicionarTodos2 = nrQuantidadeAdicionarTodos2;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }
}
