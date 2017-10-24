package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.AgendaAcrescentarHorario;
import br.com.rtools.agendamentos.AgendaCancelarHorario;
import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.agendamentos.dao.AgendaAcrescentarHorarioDao;
import br.com.rtools.agendamentos.dao.AgendaCancelarHorarioDao;
import br.com.rtools.agendamentos.dao.AgendaHorariosDao;
import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.GrupoConvenioDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
import br.com.rtools.homologacao.AcrescentarHorario;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.dao.AcrescentarHorarioDao;
import br.com.rtools.homologacao.dao.HorariosDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
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
public class AgendaAcrescentarHorarioBean implements Serializable {

    private AgendaAcrescentarHorario acrescentarHorario;
    private AgendaHorarios horarios;
    private MacFilial macFilial;
    private Filial filial;
    private List<AgendaAcrescentarHorario> listaHorariosAdicionados;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listSemana;
    private List<SelectItem> listHorarios;
    private List<SelectItem> listConvenio;
    private List<SelectItem> listGrupoConvenio;
    private List<SelectItem> listSubGrupoConvenio;
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
    private Boolean liberaAcessaFilial;
    private List<SelectItem> listHorariosDisponiveis;
    private Integer idConvenio;
    private Integer idSubGrupoConvenio;
    private Integer idGrupoConvenio;

    @PostConstruct
    public void init() {
        acrescentarHorario = new AgendaAcrescentarHorario();
        horarios = new AgendaHorarios();
        macFilial = new MacFilial();
        filial = new Filial();
        listaHorariosAdicionados = new ArrayList();
        listFiliais = new ArrayList<>();
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
        liberaAcessaFilial = false;
        new Tabbed().setTitle("1");
        loadLiberaAcessaFilial();
        loadListFilial();
        loadListGrupoConvenio();
        loadListSubGrupoConvenio();
        loadListConvenio();
        loadListHorariosAdicionados();
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
        if (listHorariosDisponiveis.isEmpty()) {
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
        AgendaAcrescentarHorarioDao aahd = new AgendaAcrescentarHorarioDao();
        AgendaAcrescentarHorario ach;
        boolean erro = false;
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        Integer horaInicioManha = 700;
        Integer horaMeioDia = 1200;
        Integer horaFinalTarde = 1900;
        dao.openTransaction();
        for (int i = 0; i < listHorariosDisponiveis.size(); i++) {
            acrescentarHorario.setUsuario(u);
            if (todos) {
                AgendaHorarios h = (AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(listHorariosDisponiveis.get(i).getValue().toString()));
                if (periodo.equals("todos")) {
                    acrescentarHorario.setHorario(h);
                } else {
                    Integer horario = Integer.parseInt(h.getHora().replace(":", ""));
                    if (periodo.equals("manha")) {
                        if (horario >= horaInicioManha && horario < horaMeioDia) {
                            acrescentarHorario.setHorario(h);
                        }
                    } else if (periodo.equals("tarde")) {
                        if (horario >= horaMeioDia && horario < horaFinalTarde) {
                            acrescentarHorario.setHorario(h);
                        }
                    }
                }
            } else {
                acrescentarHorario.setHorario((AgendaHorarios) dao.find(new AgendaHorarios(), idHorariosDisponiveis));
            }
            if (acrescentarHorario.getHorario().getId() != null) {
                ach = aahd.findBy(data, acrescentarHorario.getHorario().getId(), acrescentarHorario.getHorario().getFilial().getId(), idSubGrupoConvenio, idConvenio);
                if (ach == null) {
                    acrescentarHorario.setDtData(data);
                    if (todos) {
                        acrescentarHorario.setQuantidade(nrQuantidadeAdicionarTodos);
                    } else {
                        acrescentarHorario.setQuantidade(nrQuantidadeAdicionar);
                    }
                    if (dao.save(acrescentarHorario)) {
                        acrescentarHorario = new AgendaAcrescentarHorario();
                        nrQuantidadeDisponivel = 0;
                        erro = false;

                    } else {
                        erro = true;
                        break;
                    }
                } else {
                    acrescentarHorario = ach;
                    if (todos) {
                        acrescentarHorario.setQuantidade(ach.getQuantidade() + nrQuantidadeAdicionarTodos);
                    } else {
                        acrescentarHorario.setQuantidade(ach.getQuantidade() + nrQuantidadeAdicionar);
                    }
                    if (dao.update(acrescentarHorario)) {
                        acrescentarHorario = new AgendaAcrescentarHorario();
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
            sisProcesso.finish();
            WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        }
        nrQuantidadeDisponivel = 0;
        nrQuantidadeAdicionarTodos = 0;
        listaHorariosAdicionados.clear();
        acrescentarHorario = new AgendaAcrescentarHorario();
        loadListHorariosAdicionados();
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
        Filial f = (Filial) dao.find(new Filial(), idFilial);
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
        List<AgendaHorarios> horarioses = new ArrayList<>();
        AcrescentarHorarioDao ahd = new AcrescentarHorarioDao();
        AgendaAcrescentarHorario ah = null;
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        dao.openTransaction();
        for (int z = 0; z < listDatas.size(); z++) {
            horarioses.clear();
            acrescentarHorario = new AgendaAcrescentarHorario();
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
                    // ah = ahd.pesquisaAcrescimoHorarioSemana(DataHoje.converte(strDataInicial), horarioses.get(x).getId(), f.getId(), horarioses.get(x).getSemana().getId());
                } else {
                    // ah = ahd.pesquisaAcrescimoHorario(DataHoje.converte(strDataInicial), horarioses.get(x).getId(), f.getId());
                }
                Boolean save = true;
                acrescentarHorario.setUsuario(u);
                if (ah.getId() == null) {
                    acrescentarHorario.setHorario(horarioses.get(x));
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
                            acrescentarHorario = new AgendaAcrescentarHorario();
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
                            acrescentarHorario = new AgendaAcrescentarHorario();
                            erro = false;
                        } else {
                            erro = true;
                            break;
                        }
                    } else if (dao.delete(acrescentarHorario)) {
                        acrescentarHorario = new AgendaAcrescentarHorario();
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
        acrescentarHorario = new AgendaAcrescentarHorario();
        sisProcesso.finish();
        GenericaMensagem.info("Sucesso", "Horários adicionados com sucesso");
        WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        return null;
    }

    public void remove(AgendaAcrescentarHorario ach) {
        Dao dao = new Dao();
        ach = (AgendaAcrescentarHorario) dao.find(new AgendaAcrescentarHorario(), ach.getId());
        if (ach != null) {
            if (dao.delete(ach, true)) {
                GenericaMensagem.info("Sucesso", "Registro removido");
                WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
            } else {
                GenericaMensagem.warn("Erro", "Ao remover registro!");
                return;
            }
        }
        calculaQuantidadeDisponivel();
        loadListHorariosAdicionados();
    }

    public void loadListHorariosDisponiveis() {
        listHorariosDisponiveis = new ArrayList<>();
        List<SelectItem> result = new ArrayList<>();
        AgendaHorariosDao ahd = new AgendaHorariosDao();
        Integer semana_id = DataHoje.diaDaSemana(data);
        List<AgendaHorarios> list = ahd.findBy(idFilial, semana_id, idSubGrupoConvenio, idConvenio);
        if (list.isEmpty()) {
            desabilitaBotoes = true;
            idHorariosDisponiveis = 0;
        } else {
            desabilitaBotoes = false;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idHorariosDisponiveis = list.get(i).getId();
                }
                listHorariosDisponiveis.add(
                        new SelectItem(list.get(i).getId(), list.get(i).getHora()));
            }
        }
    }

    public List<SelectItem> getListHorariosDisponiveis() {
        return listHorariosDisponiveis;
    }

    public void setListHorariosDisponiveis(List<SelectItem> listHorariosDisponiveis) {
        this.listHorariosDisponiveis = listHorariosDisponiveis;
    }

    public AgendaAcrescentarHorario getAcrescentarHorario() {
        return acrescentarHorario;
    }

    public void setAcrescentarHorario(AgendaAcrescentarHorario acrescentarHorario) {
        this.acrescentarHorario = acrescentarHorario;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public void loadListHorariosAdicionados() {
        listaHorariosAdicionados = new ArrayList();
        AgendaAcrescentarHorarioDao achd = new AgendaAcrescentarHorarioDao();
        switch (getTipoAcrescimo()) {
            case "Dia": {
                listaHorariosAdicionados = achd.findAll(idFilial, data, idSubGrupoConvenio, idConvenio);
                break;
            }
//            case "Período": {
//                if (habilitaSemana || habilitaHorarios) {
//                    String hora = "";
//                    if (habilitaHorarios) {
//                        try {
//                            hora = "";
//                        } catch (Exception e) {
//
//                        }
//                    }
//                    listaHorariosAdicionados = achd.findAll(idFilial, dataInicial, dataFinal, idSemana, hora, idSubGrupoConvenio, idConvenio);
//                } else {
//                    listaHorariosAdicionados = achd.findAll2(idFilial, dataInicial, dataFinal, idSubGrupoConvenio, idConvenio);
//                }
//                break;
//            }
        }
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
        loadListHorariosDisponiveis();
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
        AgendaAcrescentarHorarioDao acrescentarHorarioDao = new AgendaAcrescentarHorarioDao();
        int idHorariox = -1;
        if (getTipoAcrescimo().equals("Dia")) {
            if (!listHorariosDisponiveis.isEmpty()) {
                Dao dao = new Dao();
                horarios = (AgendaHorarios) dao.find(new AgendaHorarios(), idHorariosDisponiveis);
            } else {
                horarios = new AgendaHorarios();
                idHorariosDisponiveis = 0;
            }
            if (horarios.getId() != null) {
                AgendaAcrescentarHorario acrescentarHorarioA = null;
                // = acrescentarHorarioDao.pesquisaAcrescimoHorario(data, idHorariox, idFilial,);
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
                if (!listHorariosDisponiveis.isEmpty()) {
                    horarios = (AgendaHorarios) new Dao().find(new AgendaHorarios(), idHorariosDisponiveis);
                } else {
                    horarios = new AgendaHorarios();
                    idHorariosDisponiveis = 0;
                }
                if (horarios.getId() != null) {
                    List<?> list = new AgendaHorariosDao().findBy(idFilial, horarios.getHora(), idSemana, idSubGrupoConvenio, idConvenio);
                    AgendaHorarios hx = ((List<AgendaHorarios>) list).get(0);
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

    public AgendaHorarios getHorarios() {
        return horarios;
    }

    public void setHorarios(AgendaHorarios horarios) {
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

    public void removeAcrescimos() {
        AgendaAcrescentarHorarioDao aahd = new AgendaAcrescentarHorarioDao();
        List<AgendaAcrescentarHorario> list = new ArrayList();
        switch (getTipoAcrescimo()) {
            case "Dia":
                list = aahd.findAll(idFilial, data, idSubGrupoConvenio, idConvenio);
                break;
            case "Período":
                list = aahd.findAll2(idFilial, dataInicial, dataFinal, idSubGrupoConvenio, idConvenio);
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
                    AgendaAcrescentarHorario ch = (AgendaAcrescentarHorario) dao.find(new AgendaAcrescentarHorario(), list.get(i).getId());
                    if (ch != null) {
                        if (!dao.delete(ch)) {
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
                    GenericaMensagem.info("Sucesso", "Horarios excluídos com sucesso.");
                    WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
                }
            } catch (Exception e) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao excluir horários!");
            }
        } else {
            GenericaMensagem.warn("Sistema", "Não existem horários a serem excluídos para data / período!");
        }
        calculaQuantidadeDisponivel();
        // loadListHorariosDisponiveis();
        loadListHorariosAdicionados();
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

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public void loadListSemana() {
        listSemana = new ArrayList();
        if (habilitaSemana) {
            Dao dao = new Dao();
            List<Semana> list = (List<Semana>) dao.list(new Semana());
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idSemana = list.get(i).getId();
                }
                listSemana.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
    }

    public void loadListFilial() {
        listFiliais = new ArrayList();
        Filial f = MacFilial.getAcessoFilial().getFilial();
        if (f.getId() != -1) {
            if (liberaAcessaFilial || Usuario.getUsuario().getId() == 1) {
                liberaAcessaFilial = true;
                // ROTINA MATRÍCULA ESCOLA
                Rotina r = new Rotina().get();
                List<FilialRotina> list = new ArrayList();
                if (r != null) {
                    list = new FilialRotinaDao().findByRotina(new Rotina().get().getId());
                }
                // ID DA FILIAL
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (i == 0) {
                            idFilial = list.get(i).getFilial().getId();
                        }
                        if (Objects.equals(f.getId(), list.get(i).getFilial().getId())) {
                            idFilial = f.getId();
                        }
                        listFiliais.add(new SelectItem(list.get(i).getFilial().getId(), list.get(i).getFilial().getFilial().getPessoa().getDocumento() + " / " + list.get(i).getFilial().getFilial().getPessoa().getNome()));
                    }
                } else {
                    listFiliais.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getNome() + " / " + f.getFilial().getPessoa().getDocumento()));
                }
            } else {
                idFilial = f.getId();
                listFiliais.add(new SelectItem(f.getId(), f.getFilial().getPessoa().getNome() + " / " + f.getFilial().getPessoa().getDocumento()));
            }
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
        List<SubGrupoConvenio> list = new SubGrupoConvenioDao().findAllByGrupoAndAgendamento(idGrupoConvenio);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSubGrupoConvenio = list.get(i).getId();
            }
            listSubGrupoConvenio.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public List<AgendaAcrescentarHorario> getListaHorariosAdicionados() {
        return listaHorariosAdicionados;
    }

    public void setListaHorariosAdicionados(List<AgendaAcrescentarHorario> listaHorariosAdicionados) {
        this.listaHorariosAdicionados = listaHorariosAdicionados;
    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public List<SelectItem> getListConvenio() {
        return listConvenio;
    }

    public void setListConvenio(List<SelectItem> listConvenio) {
        this.listConvenio = listConvenio;
    }

    public List<SelectItem> getListGrupoConvenio() {
        return listGrupoConvenio;
    }

    public void setListGrupoConvenio(List<SelectItem> listGrupoConvenio) {
        this.listGrupoConvenio = listGrupoConvenio;
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

    public Integer getIdGrupoConvenio() {
        return idGrupoConvenio;
    }

    public void setIdGrupoConvenio(Integer idGrupoConvenio) {
        this.idGrupoConvenio = idGrupoConvenio;
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "clear":
                // dsHora = "";
                data = DataHoje.dataHoje();
                idHorariosDisponiveis = 0;
                dataInicial = DataHoje.dataHoje();
                dataFinal = DataHoje.dataHoje();
                habilitaHorarios = false;
                habilitaSemana = false;
                nrQuantidadeDisponivel = 0;
                nrQuantidadeAdicionado = 0;
                nrQuantidadeAdicionar = 0;
                nrQuantidadeAdicionarTodos = 0;
                nrQuantidadeAdicionarTodos2 = 0;
                nrQuantidadeDisponivelB = 0;
                nrQuantidadeAdicionadoB = 0;
                nrQuantidadeAdicionalB = 0;
                break;
            case "clear_list":
                listSemana = new ArrayList();
                listHorarios = new ArrayList();
                if (getTipoAcrescimo().equals("Período")) {
                    loadListSemana();
                    // loadListHorarios();
                }
                break;
            case "filial":
                listener("clear");
                loadListGrupoConvenio();
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                calculaQuantidadeDisponivel();
                listener("clear_list");
                break;
            case "grupo_convenio":
                listener("clear");
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                calculaQuantidadeDisponivel();
                listener("clear_list");
                break;
            case "subgrupo_convenio":
                listener("clear");
                loadListConvenio();
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                calculaQuantidadeDisponivel();
                listener("clear_list");
                break;
            case "convenio":
                listener("clear");
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                calculaQuantidadeDisponivel();
                listener("clear_list");
                break;
            case "habilita_semana":
                loadListSemana();
                if (habilitaSemana) {
                    // loadListHorarios();
                }
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                break;
            case "habilita_horarios":
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                calculaQuantidadeDisponivel();
                break;
            case "change_semana":
            case "change_horarios":
                loadListHorariosDisponiveis();
                loadListHorariosAdicionados();
                calculaQuantidadeDisponivel();
                break;
            default:
                break;
        }
    }

}
