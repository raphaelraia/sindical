package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.AgendaCancelarHorario;
import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.agendamentos.dao.AgendaCancelarHorarioDao;
import br.com.rtools.agendamentos.dao.AgendaHorariosDao;
import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.GrupoConvenioDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
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
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@SessionScoped
public class AgendaCancelarHorarioBean implements Serializable {

    private AgendaCancelarHorario cancelarHorario;
    private AgendaHorarios horarios;
    private MacFilial macFilial;
    private Filial filial;
    private List<AgendaCancelarHorario> listaHorariosCancelados;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listSemana;
    private List<SelectItem> listHorarios;
    private List<SelectItem> listConvenio;
    private List<SelectItem> listGrupoConvenio;
    private List<SelectItem> listSubGrupoConvenio;
    private Integer idFilial;
    private Integer idSemana;
    private Integer nrQuantidadeDisponivel;
    private Integer nrQuantidadeCancelado;
    private Integer nrQuantidadeCancelar;
    private Integer nrQuantidadeDisponivelB;
    private Integer nrQuantidadeCanceladoB;
    private Integer nrQuantidadeCancelarB;
    private Date data;
    private Date dataInicial = DataHoje.dataHoje();
    private Date dataFinal;
    private Integer idHorariosDisponiveis;
    private Integer idHorario;
    private String dsHora;
    private Integer idConvenio;
    private Integer idSubGrupoConvenio;
    private Integer idGrupoConvenio;
    private boolean desabilitaBotoes;
    private boolean desabilitaFilial;
    private String tipoCancelamento;
    private Boolean habilitaSemana;
    private Boolean habilitaHorarios;
    private Boolean liberaAcessaFilial;
    private List<SelectItem> listHorariosDisponiveis;

    @PostConstruct
    public void init() {
        cancelarHorario = new AgendaCancelarHorario();
        horarios = new AgendaHorarios();
        macFilial = new MacFilial();
        listaHorariosCancelados = new ArrayList();
        listFiliais = new ArrayList<>();
        listSemana = new ArrayList<>();
        listHorarios = new ArrayList<>();
        listGrupoConvenio = new ArrayList<>();
        listSubGrupoConvenio = new ArrayList<>();
        listConvenio = new ArrayList<>();
        idFilial = 0;
        idSemana = 0;
        nrQuantidadeDisponivel = 0;
        nrQuantidadeCancelado = 0;
        nrQuantidadeCancelar = 0;
        nrQuantidadeDisponivelB = 0;
        nrQuantidadeCanceladoB = 0;
        nrQuantidadeCancelarB = 0;
        data = DataHoje.dataHoje();
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        idHorariosDisponiveis = 0;
        dsHora = "0";
        desabilitaBotoes = false;
        desabilitaFilial = false;
        tipoCancelamento = "Dia";
        habilitaSemana = false;
        habilitaHorarios = false;
        liberaAcessaFilial = false;
        loadLiberaAcessaFilial();
        new Tabbed().setTitle("1");
        loadListFilial();
        loadListSemana();
        loadListGrupoConvenio();
        loadListSubGrupoConvenio();
        loadListConvenio();

    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("cancelarHorarioBean");
        GenericaSessao.remove("tabbedBean");
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "clear":
                dsHora = "";
                data = DataHoje.dataHoje();
                idHorariosDisponiveis = 0;
                dataInicial = DataHoje.dataHoje();
                dataFinal = DataHoje.dataHoje();
                habilitaHorarios = false;
                habilitaSemana = false;
                nrQuantidadeDisponivel = 0;
                nrQuantidadeCancelado = 0;
                nrQuantidadeCancelar = 0;
                nrQuantidadeDisponivelB = 0;
                nrQuantidadeCanceladoB = 0;
                nrQuantidadeCancelarB = 0;
                break;
            case "clear_list":
                listSemana = new ArrayList();
                listHorarios = new ArrayList();
                if (getTipoCancelamento().equals("Período")) {
                    loadListSemana();
                    loadListHorarios();
                }
                break;
            case "1":
                loadListHorarios();
                calculaQuantidadeDisponivel();
                loadListHorariosCancelados();
                break;
            case "2":
                loadListHorarios();
                calculaQuantidadeDisponivel();
                loadListHorariosCancelados();
                break;
            case "filial":
                listener("clear");
                loadListGrupoConvenio();
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListHorariosDisponiveis();
                loadListHorariosCancelados();
                calculaQuantidadeDisponivel();
                listener("clear_list");
                break;
            case "subgrupo_convenio":
                listener("clear");
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListHorariosDisponiveis();
                calculaQuantidadeDisponivel();
                loadListHorariosCancelados();
                listener("clear_list");
                break;
            case "convenio":
                listener("clear");
                loadListConvenio();
                loadListHorariosDisponiveis();
                loadListHorariosCancelados();
                calculaQuantidadeDisponivel();
                listener("clear_list");
                break;
            case "habilita_semana":
                loadListSemana();
                if (habilitaSemana) {
                    loadListHorarios();
                }
                loadListHorariosCancelados();
                calculaQuantidadeDisponivel();
                break;
            case "habilita_horarios":
                loadListHorarios();
                loadListHorariosCancelados();
                calculaQuantidadeDisponivel();
                break;
            case "change_semana":
            case "change_horarios":
                loadListHorarios();
                loadListHorariosCancelados();
                calculaQuantidadeDisponivel();
                break;
            default:
                break;
        }
    }

    public void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public void process(boolean all) {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        if (!all) {
            if (nrQuantidadeCancelar == 0) {
                GenericaMensagem.warn("Sistema", "Digite uma quantidade!");
                return;
            }
        }

        Dao dao = new Dao();
        AgendaCancelarHorarioDao db = new AgendaCancelarHorarioDao();
        AgendaCancelarHorario ch;
        boolean erro = false;
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        dao.openTransaction();
        for (int i = 0; i < listHorariosDisponiveis.size(); i++) {
            cancelarHorario.setUsuario(u);
            if (all) {
                cancelarHorario.setHorario((AgendaHorarios) dao.find(new AgendaHorarios(), Integer.parseInt(listHorariosDisponiveis.get(i).getValue().toString())));
                nrQuantidadeDisponivel = cancelarHorario.getHorario().getQuantidade();
            } else {
                cancelarHorario.setHorario((AgendaHorarios) dao.find(new AgendaHorarios(), idHorariosDisponiveis));
            }
            ch = db.findBy(data, cancelarHorario.getHorario().getId(), idFilial, idSubGrupoConvenio, idConvenio);
            if (ch == null) {
                cancelarHorario.setDtData(data);
                if (all) {
                    if (nrQuantidadeDisponivel > 0) {
                        cancelarHorario.setQuantidade(nrQuantidadeDisponivel);
                    } else {
                        cancelarHorario.setQuantidade(0);
                    }

                } else {
                    cancelarHorario.setQuantidade(nrQuantidadeCancelar);
                }
                if (dao.save(cancelarHorario)) {
                    cancelarHorario = new AgendaCancelarHorario();
                    nrQuantidadeDisponivel = 0;
                    erro = false;

                } else {
                    erro = true;
                    break;
                }
            } else {
                cancelarHorario = ch;
                if (all) {
                    if (nrQuantidadeDisponivel > 0) {
                        cancelarHorario.setQuantidade(nrQuantidadeDisponivel);
                    } else {
                        cancelarHorario.setQuantidade(0);
                    }
                } else {
                    cancelarHorario.setQuantidade(ch.getQuantidade() + nrQuantidadeCancelar);
                }
                if (dao.update(cancelarHorario)) {
                    cancelarHorario = new AgendaCancelarHorario();
                    nrQuantidadeDisponivel = 0;
                    erro = false;
                } else {
                    erro = true;
                    break;
                }
            }
            if (!all) {
                break;
            }
        }

        if (erro) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Erro ao cancelar horário(s)!");
            return;
        } else {
            dao.commit();
            if(!listHorariosDisponiveis.isEmpty()) {
                GenericaMensagem.info("Sucesso", "Horário cancelado com sucesso.");                
            }
            loadListHorariosDisponiveis();
            sisProcesso.finish();
            WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        }
        nrQuantidadeDisponivel = 0;
        nrQuantidadeDisponivelB = 0;
        cancelarHorario = new AgendaCancelarHorario();
        loadListHorariosDisponiveis();
        loadListHorariosCancelados();
        calculaQuantidadeDisponivel();
    }

    public String processPeriod() {
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        sisProcesso.setProcesso("Cancelar horários");
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
        AgendaHorariosDao horariosDao = new AgendaHorariosDao();
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
        AgendaCancelarHorarioDao cancelarHorarioDao = new AgendaCancelarHorarioDao();
        AgendaCancelarHorario ch;
        Usuario u = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        dao.openTransaction();
        for (int z = 0; z < listDatas.size(); z++) {
            horarioses.clear();
            cancelarHorario = new AgendaCancelarHorario();
            strDataInicial = listDatas.get(z).toString();
            if (!habilitaSemana && !habilitaHorarios) {
                horarioses = horariosDao.findBy(f.getId(), DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)), idSubGrupoConvenio, idConvenio);
            } else if (habilitaSemana && !habilitaHorarios) {
                if (listSemana.isEmpty()) {
                    return null;
                }
                if (DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)) == idSemana) {
                    horarioses = horariosDao.findBy(f.getId(), DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)), idSubGrupoConvenio, idConvenio);
                }
            } else if (!habilitaSemana && habilitaHorarios) {
                if (listHorarios.isEmpty()) {
                    return null;
                }
                horarioses = horariosDao.findBy(f.getId(), dsHora, idSubGrupoConvenio, idConvenio);
            } else if (habilitaSemana && habilitaHorarios) {
                if (DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)) == idSemana) {
                    if (listHorarios.isEmpty() || listSemana.isEmpty()) {
                        return null;
                    }
                    horarioses = horariosDao.findByFilial(f.getId(), dsHora, DataHoje.diaDaSemana(DataHoje.converte(strDataInicial)), idSubGrupoConvenio, idConvenio);
                }
            }
            erro = false;
            for (int x = 0; x < horarioses.size(); x++) {
                if (habilitaHorarios && habilitaSemana) {
                    ch = cancelarHorarioDao.findBy(DataHoje.converte(strDataInicial), horarioses.get(x).getId(), f.getId(), horarioses.get(x).getSemana().getId(), idSubGrupoConvenio, idConvenio);
                } else {
                    ch = cancelarHorarioDao.findBy(DataHoje.converte(strDataInicial), horarioses.get(x).getId(), f.getId(), idSubGrupoConvenio, idConvenio);
                }
                Boolean save = true;
                cancelarHorario.setUsuario(u);
                if (ch == null) {
                    cancelarHorario.setHorario(horarioses.get(x));
                    cancelarHorario.setDtData(DataHoje.converte(strDataInicial));
                    if (horarioses.get(x).getQuantidade() > 0) {
                        if (habilitaHorarios && habilitaSemana) {
                            if (nrQuantidadeCancelarB == 0) {
                                cancelarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                            } else {
                                int resto = horarioses.get(x).getQuantidade() - nrQuantidadeCancelarB;
                                if (resto == 0) {
                                    break;
                                } else if (resto < 1) {
                                    break;
                                } else {
                                    cancelarHorario.setQuantidade(resto);
                                }
                            }
                        } else {
                            Integer day_of_month_a = cancelarHorario.getDtData().getDay();
                            Integer day_of_month_b = horarioses.get(x).getSemana().getId() - 1;
                            if (Objects.equals(day_of_month_a, day_of_month_b)) {
                                save = true;
                                cancelarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                            } else {
                                save = false;
                            }
                        }
                    } else {
                        cancelarHorario.setQuantidade(0);
                    }
                    if (save) {
                        if (dao.save(cancelarHorario)) {
                            cancelarHorario = new AgendaCancelarHorario();
                            erro = false;
                        } else {
                            erro = true;
                            break;
                        }
                    }
                } else {
                    boolean delete = false;
                    cancelarHorario = ch;
                    if (horarioses.get(x).getQuantidade() > 0) {
                        if (habilitaHorarios && habilitaSemana) {
                            if (nrQuantidadeCancelarB == 0) {
                                cancelarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                            } else {
                                int qtdeCancelada = horarioses.get(x).getQuantidade() - ch.getQuantidade();
                                int resto = qtdeCancelada - nrQuantidadeCancelarB;
                                if (resto == 0) {
                                    delete = true;
                                } else if (horarioses.get(x).getQuantidade() == nrQuantidadeCancelarB) {
                                    delete = true;
                                } else if (resto < 1) {
                                    delete = false;
                                    resto = horarioses.get(x).getQuantidade() - nrQuantidadeCancelarB;
                                    cancelarHorario.setQuantidade(resto);
                                } else {
                                    delete = false;
                                    cancelarHorario.setQuantidade(resto);
                                }
                            }
                        } else {
                            cancelarHorario.setQuantidade(horarioses.get(x).getQuantidade());
                        }
                    } else {
                        cancelarHorario.setQuantidade(0);
                    }
                    if (!delete) {
                        if (dao.update(cancelarHorario)) {
                            cancelarHorario = new AgendaCancelarHorario();
                            erro = false;
                        } else {
                            erro = true;
                            break;
                        }
                    } else if (dao.delete(cancelarHorario)) {
                        cancelarHorario = new AgendaCancelarHorario();
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
            GenericaMensagem.warn("Erro", "Erro ao cancelar horário(s) do período!");
            dao.rollback();
            return null;
        }
        dao.commit();
        loadListHorariosCancelados();
        cancelarHorario = new AgendaCancelarHorario();
        sisProcesso.finish();
        GenericaMensagem.info("Sucesso", "Horários cancelados com sucesso");
        WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
        return null;
    }

    public void remove(AgendaCancelarHorario ch) {
        Dao dao = new Dao();
        ch = (AgendaCancelarHorario) dao.find(new AgendaCancelarHorario(), ch.getId());
        if (ch != null) {
            if (ch != null) {
                if (dao.delete(ch, true)) {
                    GenericaMensagem.info("Sucesso", "Registro removido");
                    WSSocket.send("agendamentos_" + ControleUsuarioBean.getCliente().toLowerCase());
                } else {
                    GenericaMensagem.warn("Erro", "Ao remover registro!");
                    return;
                }
            }
        }
        calculaQuantidadeDisponivel();
        loadListHorariosCancelados();
    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public AgendaCancelarHorario getCancelarHorario() {
        return cancelarHorario;
    }

    public void setCancelarHorario(AgendaCancelarHorario cancelarHorario) {
        this.cancelarHorario = cancelarHorario;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public void loadListHorariosDisponiveis() {
        loadListHorariosCancelados();
        listHorariosDisponiveis = new ArrayList();
        List<AgendaHorarios> select = new AgendaHorariosDao().findBy(idFilial, data, false, idSubGrupoConvenio, idConvenio);
        if (select.isEmpty()) {
            desabilitaBotoes = true;
            idHorariosDisponiveis = 0;
        } else {
            desabilitaBotoes = false;
            for (int i = 0; i < select.size(); i++) {
                if (i == 0) {
                    idHorariosDisponiveis = select.get(i).getId();
                }
                listHorariosDisponiveis.add(
                        new SelectItem(
                                select.get(i).getId(),
                                select.get(i).getHora()
                        )
                );
            }
        }
    }

    public void loadListHorariosCancelados() {
        listaHorariosCancelados = new ArrayList();
        AgendaCancelarHorarioDao achd = new AgendaCancelarHorarioDao();
        switch (getTipoCancelamento()) {
            case "Dia": {
                listaHorariosCancelados = achd.findAll(idFilial, data, idSubGrupoConvenio, idConvenio);
                break;
            }
            case "Período": {
                if (habilitaSemana || habilitaHorarios) {
                    String hora = "";
                    if (habilitaHorarios) {
                        try {
                            hora = dsHora;
                        } catch (Exception e) {

                        }
                    }
                    listaHorariosCancelados = achd.findAll(idFilial, dataInicial, dataFinal, idSemana, hora, idSubGrupoConvenio, idConvenio);
                } else {
                    listaHorariosCancelados = achd.findAll2(idFilial, dataInicial, dataFinal, idSubGrupoConvenio, idConvenio);
                }
                break;
            }
        }
    }

    public List<AgendaCancelarHorario> getListaHorariosCancelados() {
        return listaHorariosCancelados;
    }

    public void setListaHorariosCancelados(List<AgendaCancelarHorario> listaHorariosCancelados) {
        this.listaHorariosCancelados = listaHorariosCancelados;
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
        loadListHorariosCancelados();
        loadListHorariosDisponiveis();
        calculaQuantidadeDisponivel();
    }

    public void dataFinalListener(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy");
        this.dataFinal = DataHoje.converte(format.format(event.getObject()));
    }

    public void calculaQuantidadeDisponivel() {
        nrQuantidadeCancelar = 0;
        nrQuantidadeCancelado = 0;
        nrQuantidadeDisponivel = 0;
        nrQuantidadeCancelarB = 0;
        nrQuantidadeCanceladoB = 0;
        nrQuantidadeDisponivelB = 0;
        AgendaCancelarHorarioDao cancelarHorarioDao = new AgendaCancelarHorarioDao();
        int idHorariox = -1;
        if (getTipoCancelamento().equals("Dia")) {
            if (!listHorariosDisponiveis.isEmpty()) {
                Dao dao = new Dao();
                horarios = (AgendaHorarios) dao.find(new AgendaHorarios(), idHorariosDisponiveis);
            } else {
                horarios = new AgendaHorarios();
                idHorariosDisponiveis = 0;
            }
            if (horarios.getId() != null) {
                AgendaCancelarHorario cancelarHorarioA = cancelarHorarioDao.findBy(data, idHorariosDisponiveis, idFilial, idSubGrupoConvenio, idConvenio);
                if (cancelarHorarioA != null) {
                    if (horarios.getQuantidade() > 0) {
                        if (cancelarHorarioA.getQuantidade() > horarios.getQuantidade()) {
                            nrQuantidadeDisponivel = 0;
                        } else {
                            nrQuantidadeDisponivel = horarios.getQuantidade() - cancelarHorarioA.getQuantidade();
                        }
                    }
                    nrQuantidadeCancelado = cancelarHorarioA.getQuantidade();
                } else {
                    nrQuantidadeDisponivel = horarios.getQuantidade();
                }
            }
        } else if (getTipoCancelamento().equals("Período")) {
            if (habilitaHorarios && habilitaSemana) {
                if (!listHorariosDisponiveis.isEmpty()) {
                    // idHorariox = dsHora;
                    Dao dao = new Dao();
                    horarios = (AgendaHorarios) dao.find(new AgendaHorarios(), idHorariox);
                } else {
                    horarios = new AgendaHorarios();
                    idHorariosDisponiveis = 0;
                }
                if (horarios.getId() != null) {
                    List<?> list = new AgendaHorariosDao().findByFilial(idFilial, horarios.getHora(), idSemana, idSubGrupoConvenio, idConvenio);
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

    public int calculaQuantidadeDisponivel(Integer quantidadeDisponivel, Integer quantidadeCancelada) {
        int quantidadeRestante = 0;
        if (quantidadeDisponivel > 0) {
            if (quantidadeCancelada > quantidadeDisponivel) {
                quantidadeRestante = 0;
            } else {
                quantidadeRestante = quantidadeDisponivel - quantidadeCancelada;
            }
        }
        return quantidadeRestante;
    }

    public void validaQuantidadeDisponivel() {
        if (nrQuantidadeDisponivel > 0) {
            if (nrQuantidadeCancelar > nrQuantidadeDisponivel) {
                nrQuantidadeCancelar = nrQuantidadeDisponivel;
            }
        }
    }

    public void validaQuantidadeDisponivelB() {
        if (nrQuantidadeDisponivelB > 0) {
            if (nrQuantidadeCancelarB > nrQuantidadeDisponivelB) {
                nrQuantidadeCancelarB = nrQuantidadeDisponivelB;
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

    public Integer getNrQuantidadeCancelado() {
        return nrQuantidadeCancelado;
    }

    public void setNrQuantidadeCancelado(Integer nrQuantidadeCancelado) {
        this.nrQuantidadeCancelado = nrQuantidadeCancelado;
    }

    public Integer getNrQuantidadeCancelar() {
        return nrQuantidadeCancelar;
    }

    public void setNrQuantidadeCancelar(Integer nrQuantidadeCancelar) {
        this.nrQuantidadeCancelar = nrQuantidadeCancelar;
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
        if (filial == null) {
            filial = MacFilial.getAcessoFilial().getFilial();
            if (filial.getId() != -1) {
                desabilitaFilial = true;
            }
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

    public void cancelamentoPor(TabChangeEvent event) {
        nrQuantidadeDisponivel = 0;
        nrQuantidadeCancelar = 0;
        nrQuantidadeCancelado = 0;
        nrQuantidadeDisponivelB = 0;
        nrQuantidadeCancelarB = 0;
        nrQuantidadeCanceladoB = 0;
        data = DataHoje.dataHoje();
        dataInicial = DataHoje.dataHoje();
        dataFinal = DataHoje.dataHoje();
        switch (event.getTab().getTitle()) {
            case "Dia":
                ((Tabbed) GenericaSessao.getObject("tabbedBean")).setActiveIndex("1");
                setTipoCancelamento("Dia");
                loadListHorariosDisponiveis();
                break;
            case "Período":
                setTipoCancelamento("Período");
                ((Tabbed) GenericaSessao.getObject("tabbedBean")).setActiveIndex("2");
                break;
            default:
                ((Tabbed) GenericaSessao.getObject("tabbedBean")).setActiveIndex("1");
                setTipoCancelamento("");
                break;
        }
        calculaQuantidadeDisponivel();
    }

    public void removeCancelamentos() {
        AgendaCancelarHorarioDao cancelarHorarioDao = new AgendaCancelarHorarioDao();
        List<AgendaCancelarHorario> list = new ArrayList();
        switch (getTipoCancelamento()) {
            case "Dia":
                list = cancelarHorarioDao.findAll(idFilial, data, idSubGrupoConvenio, idConvenio);
                break;
            case "Período":
                if (!habilitaSemana && !habilitaHorarios) {
                    list = cancelarHorarioDao.findAll2(idFilial, dataInicial, dataFinal, idSubGrupoConvenio, idConvenio);
                } else if (habilitaSemana && !habilitaHorarios) {
                    if (listSemana.isEmpty()) {
                        return;
                    }
                    list = cancelarHorarioDao.findAll(idFilial, dataInicial, dataFinal, idSemana, null, idSubGrupoConvenio, idConvenio);
                } else if (!habilitaSemana && habilitaHorarios) {
                    list = cancelarHorarioDao.findAll(idFilial, dataInicial, dataFinal, dsHora, idSubGrupoConvenio, idConvenio);
                } else if (habilitaSemana && habilitaHorarios) {
                    list = cancelarHorarioDao.findAll(idFilial, dataInicial, dataFinal, idSemana, dsHora, idSubGrupoConvenio, idConvenio);
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
                    AgendaCancelarHorario ch = (AgendaCancelarHorario) dao.find(new AgendaCancelarHorario(), list.get(i).getId());
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
        loadListHorariosCancelados();
    }

    public String getTipoCancelamento() {
        return tipoCancelamento;
    }

    public void setTipoCancelamento(String tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
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
        this.habilitaHorarios = habilitaHorarios;
    }

    public Boolean getHabilitaSemana() {
        return habilitaSemana;
    }

    public void setHabilitaSemana(Boolean habilitaSemana) {
        this.habilitaSemana = habilitaSemana;
    }

    public List<SelectItem> getListSemana() {
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

    public String getDsHorario() {
        return dsHora;
    }

    public void setDsHorario(String dsHora) {
        this.dsHora = dsHora;
    }

    public Integer getNrQuantidadeDisponivelB() {
        return nrQuantidadeDisponivelB;
    }

    public void setNrQuantidadeDisponivelB(Integer nrQuantidadeDisponivelB) {
        this.nrQuantidadeDisponivelB = nrQuantidadeDisponivelB;
    }

    public Integer getNrQuantidadeCanceladoB() {
        return nrQuantidadeCanceladoB;
    }

    public void setNrQuantidadeCanceladoB(Integer nrQuantidadeCanceladoB) {
        this.nrQuantidadeCanceladoB = nrQuantidadeCanceladoB;
    }

    public Integer getNrQuantidadeCancelarB() {
        return nrQuantidadeCancelarB;
    }

    public void setNrQuantidadeCancelarB(Integer nrQuantidadeCancelarB) {
        this.nrQuantidadeCancelarB = nrQuantidadeCancelarB;
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
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
                List<FilialRotina> list = new FilialRotinaDao().findByRotina(new Rotina().get().getId());
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

    public void loadListHorarios() {
        listHorarios = new ArrayList();
        if (habilitaHorarios) {
            AgendaHorariosDao horariosDao = new AgendaHorariosDao();
            List list;
            dsHora = "";
            if (habilitaSemana) {
                list = horariosDao.listaHorariosAgrupadosPorFilialSemana(idFilial, idSemana, idSubGrupoConvenio, idConvenio);
            } else {
                list = horariosDao.listaHorariosAgrupadosPorFilialSemana(idFilial, null, idSubGrupoConvenio, idConvenio);
            }
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    dsHora = list.get(i).toString();
                }
                listHorarios.add(new SelectItem(list.get(i).toString(), list.get(i).toString()));
            }
        }
    }

    public String getDsHora() {
        return dsHora;
    }

    public void setDsHora(String dsHora) {
        this.dsHora = dsHora;
    }

    public List<SelectItem> getListHorariosDisponiveis() {
        return listHorariosDisponiveis;
    }

    public void setListHorariosDisponiveis(List<SelectItem> listHorariosDisponiveis) {
        this.listHorariosDisponiveis = listHorariosDisponiveis;
    }

    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

}
