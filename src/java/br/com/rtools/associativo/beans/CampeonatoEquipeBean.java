package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.CampeonatoDao;
import br.com.rtools.associativo.dao.CampeonatoDependenteDao;
import br.com.rtools.associativo.dao.CampeonatoEquipeDao;
import br.com.rtools.associativo.dao.MatriculaCampeonatoDao;
import br.com.rtools.associativo.dao.EquipeDao;
import br.com.rtools.associativo.dao.EventoServicoDao;
import br.com.rtools.associativo.dao.EventoServicoValorDao;
import br.com.rtools.associativo.dao.ParentescoDao;
import br.com.rtools.associativo.utils.SocioCarteirinhaUtils;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Periodo;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
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
import org.primefaces.event.UnselectEvent;

@ManagedBean
@SessionScoped
public class CampeonatoEquipeBean implements Serializable {

    private CampeonatoEquipe campeonatoEquipe;
    private CampeonatoEquipe campeonatoEquipeDelete;
    private MatriculaCampeonato cadastrarDependente;
    private CampeonatoDependente campeonatoDependente;
    private List<CampeonatoDependente> listCampeonatoDependente;
    private List<SelectItem> listCampeonatos;
    private List<SelectItem> listEquipes;
    private Integer idCampeonato;
    private Integer idEquipe;
    private List<CampeonatoEquipe> listCampeonatoEquipes;
    private List<MatriculaCampeonato> listMatriculaCampeonato;
    private List<MatriculaCampeonato> listMatriculaCampeonatoSuspensos;
    private List<MatriculaCampeonato> selectedMC;
    private Pessoa membroEquipe;
    private Boolean editMembrosEquipe;
    private Boolean editDependentes;
    private Fisica fisicaDependente;
    private List<SelectItem> listPatentesco;
    private Integer idPatentesco;
    private Boolean ativas;
    private String motivoInativacao;
    private String motivoInativacaoDependente;
    private Date dataInicialSuspencao;
    private Date dataFimSuspencao;
    private String motivoSuspensao;
    private Boolean vigentes;

    @PostConstruct
    public void init() {
        dataInicialSuspencao = new Date();
        dataFimSuspencao = null;
        motivoSuspensao = "";
        selectedMC = new ArrayList();
        ativas = true;
        editMembrosEquipe = false;
        campeonatoEquipeDelete = null;
        editDependentes = false;
        campeonatoEquipe = new CampeonatoEquipe();
        campeonatoDependente = new CampeonatoDependente();
        listCampeonatoEquipes = new ArrayList();
        listCampeonatos = new ArrayList();
        listPatentesco = new ArrayList();
        listCampeonatoDependente = new ArrayList();
        listMatriculaCampeonatoSuspensos = new ArrayList();
        listEquipes = new ArrayList();
        fisicaDependente = new Fisica();
        loadListCampeonatos();
        loadListCampeonatoEquipes();
        loadListEquipes();
        motivoInativacao = "";
        motivoInativacaoDependente = "";
        vigentes = true;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("fisicaGenericaBean");
        GenericaSessao.remove("campeonatoEquipeBean");
    }

    public void reload() {
        loadListCampeonatos();
        loadListCampeonatoEquipes();
        loadListEquipes();
    }

    public void loadListCampeonatos() {
        idCampeonato = null;
        idEquipe = null;
        listCampeonatos = new ArrayList();
        List<Campeonato> list = new CampeonatoDao().findAll(vigentes);
        idEquipe = null;
        listEquipes = new ArrayList();
        listCampeonatoEquipes = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idCampeonato = list.get(i).getId();
            }
            listCampeonatos.add(new SelectItem(list.get(i).getId(), list.get(i).getEvento().getDescricaoEvento().getDescricao() + " " + list.get(i).getTituloComplemento()));
        }
    }

    public void loadListEquipes() {
        idEquipe = null;
        listEquipes = new ArrayList();
        if(idCampeonato != null) {
            Campeonato c = (Campeonato) new Dao().find(new Campeonato(), idCampeonato);
            if (c != null && idCampeonato != null) {
                List<Equipe> list = new EquipeDao().findByModalidade(c.getModalidade().getId());
                for (int i = 0; i < list.size(); i++) {
                    Boolean disabled = false;
                    for (int x = 0; x < listCampeonatoEquipes.size(); x++) {
                        if (Objects.equals(listCampeonatoEquipes.get(x).getEquipe().getId(), list.get(i).getId())) {
                            disabled = true;
                        }
                    }
                    if (!disabled) {
                        if (idEquipe == null) {
                            idEquipe = list.get(i).getId();
                        }
                    }

                    listEquipes.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao(), "", disabled));
                }
            }            
        }
    }

    public void loadListCampeonatoEquipes() {
        listCampeonatoEquipes = new ArrayList();
        if (idCampeonato != null) {
            listCampeonatoEquipes = new CampeonatoEquipeDao().findByCampeonato(idCampeonato);
        }
        loadListEquipes();
    }

    public void loadListMatriculaCampeonato(Integer campeonato_equipe_id) {
        if (campeonato_equipe_id == null) {
            campeonato_equipe_id = campeonatoEquipe.getId();
        }
        selectedMC = new ArrayList();
        listMatriculaCampeonato = new ArrayList();
        listMatriculaCampeonato = new MatriculaCampeonatoDao().findByCampeonatoEquipe(campeonato_equipe_id, ativas);
    }

    public void loadListCampeonatoDependentens(Integer matricula_campeonato_id) {
        listCampeonatoDependente = new ArrayList();
        listCampeonatoDependente = new CampeonatoDependenteDao().findByMatriculaCampeonato(matricula_campeonato_id);
    }

    public void save() {
        if (idCampeonato == null) {
            GenericaMensagem.warn("Validação", "INFORMAR/CADASTRAR CAMPEONATO!");
            return;
        }
        if (idEquipe == null) {
            GenericaMensagem.warn("Validação", "INFORMAR/CADASTRAR EQUIPE!");
            return;
        }
        if (new CampeonatoEquipeDao().exists(idEquipe, idCampeonato) != null) {
            GenericaMensagem.warn("Validação", "EQUIPE JÁ CADASTRADA PARA ESTE CAMPEONATO!");
            return;

        }
        Dao dao = new Dao();
        dao.openTransaction();

        if (campeonatoEquipe.getId() == null) {
            campeonatoEquipe.setCampeonato((Campeonato) dao.find(new Campeonato(), idCampeonato));
            campeonatoEquipe.setEquipe((Equipe) dao.find(new Equipe(), idEquipe));
            if (!dao.save(campeonatoEquipe)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
            dao.commit();
            edit(campeonatoEquipe);
        } else {
//            if (!dao.update(campeonatoEquipe)) {
//                GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO!");
//                dao.rollback();
//                return;
//            }
//            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
//            dao.commit();
        }
        loadListCampeonatoEquipes();
    }

    public void delete() {
        if (campeonatoEquipeDelete == null) {
            return;
        }
        List<CampeonatoEquipe> list = new CampeonatoEquipeDao().findByCampeonato(campeonatoEquipeDelete.getId());
        if (!list.isEmpty()) {
            GenericaMensagem.warn("Validação", "REMOVER MEMBROS VÍNCULADOS!!!");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        if (campeonatoEquipeDelete.getId() != null) {
            campeonatoEquipeDelete = (CampeonatoEquipe) dao.find(campeonatoEquipeDelete);
            if (!dao.delete(campeonatoEquipeDelete)) {
                GenericaMensagem.warn("Validação", "EQUIPE COM MATRICULAS INATIVAS NÃO PODEM SER EXCLUÍDAS!!!");
                dao.rollback();
                return;
            }
            dao.commit();
            campeonatoEquipeDelete = null;
            GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        } else {
            GenericaMensagem.warn("Erro", "PESQUISE UMA EQUIPE!");
            dao.rollback();
        }
        loadListCampeonatoEquipes();
        loadListEquipes();
    }

    public void clear() {
        GenericaSessao.remove("campeonatoEquipeBean");
    }

    public String edit(CampeonatoEquipe ce) {
        selectedMC = new ArrayList();
        dataInicialSuspencao = new Date();
        dataFimSuspencao = null;
        motivoSuspensao = "";
        motivoInativacao = "";
        motivoInativacaoDependente = "";
        ativas = true;
        campeonatoEquipe = (CampeonatoEquipe) new Dao().rebind(ce);
        idCampeonato = ce.getCampeonato().getId();
        idEquipe = ce.getEquipe().getId();
        editMembrosEquipe = true;
        membroEquipe = null;
        loadListMatriculaCampeonato(ce.getId());
        return null;
    }

    public void delete(CampeonatoEquipe ce) {
        campeonatoEquipeDelete = ce;
    }

    public void addMembroEquipe() {
        motivoInativacao = "";
        if (membroEquipe == null) {
            GenericaMensagem.warn("Validação", "PESQUISAR PESSOA!");
            return;
        }
        Socios socio = membroEquipe.getSocios();
        Categoria c = null;
        if (socio.getId() != -1) {
            c = socio.getMatriculaSocios().getCategoria();
        }
        EventoServico eventoServico;
        ServicoPessoa sp = new ServicoPessoa();
        EventoServicoValor esv;
        if (c == null) {
            eventoServico = new EventoServicoDao().findByEvento(campeonatoEquipe.getCampeonato().getEvento().getId(), null, true);
            if (eventoServico == null) {
                GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA RESPONSÁVEL!");
                return;
            }
            esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), null, true);
            if (esv == null) {
                GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA RESPONSÁVEL!");
                return;
            } else {
                sp.setNrValorFixo(esv.getValor());
            }
        } else {

            /* c.getId() */ eventoServico = new EventoServicoDao().findByEvento(campeonatoEquipe.getCampeonato().getEvento().getId(), null, true);
            if (eventoServico == null) {
                eventoServico = new EventoServicoDao().findByEvento(campeonatoEquipe.getCampeonato().getEvento().getId(), null, true);
                if (eventoServico == null) {
                    GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA RESPONSÁVEL!");
                    return;
                }
                /* c.getId() */ esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), null, true);
                if (esv == null) {
                    esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), null, true);
                    if (esv == null) {
                        GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA RESPONSÁVEL!");
                        return;
                    }
                }
                sp.setNrValorFixo(esv.getValor());
            } else {
                /* c.getId() */ esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), null, true);
                if (esv == null) {
                    esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), null, true);
                    if (esv == null) {
                        GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA RESPONSÁVEL!");
                        return;
                    }
                } else {
                    sp.setNrValorFixo(esv.getValor());
                }
            }
        }
        MatriculaCampeonato mc = new MatriculaCampeonatoDao().findByPessoaSuspensa(membroEquipe.getId());
        if (mc != null) {
            GenericaMensagem.warn("Validação", "MEMBRO SUSPENSO! MOTIVO:" + mc.getMotivoSuspensao() + " - VIGÊNCIA DA SUSPENSÃO DE " + DataHoje.converteData(mc.getDtSuspensaoInicio()) + " ATÉ " + DataHoje.converteData(mc.getDtSuspensaoFim()));
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();

        if (socio.getId() == -1) {
            SocioCarteirinhaUtils scu = new SocioCarteirinhaUtils();
            scu.setPessoa(membroEquipe);
            // 5 ANOS
            scu.setValidadeMeses(5 * 12);
            if (!scu.storeDefault(dao)) {
                return;
            }
        }
        sp.setServicos(eventoServico.getServicos());
        sp.setNrDiaVencimento(membroEquipe.getPessoaComplemento().getNrDiaVencimento());
        sp.setCobranca(membroEquipe);
        sp.setCobrancaMovimento(null);
        sp.setPessoa(membroEquipe);
        sp.setAtivo(true);
        sp.setDescontoFolha(false);
        sp.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(campeonatoEquipe.getCampeonato().getInicio()));
        sp.setReferenciaValidade(DataHoje.converteDataParaReferencia(campeonatoEquipe.getCampeonato().getFim()));
        sp.setTipoDocumento((FTipoDocumento) dao.find(new TipoDocumento(), 13));
        sp.setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
        sp.setPeriodoCobranca((Periodo) dao.find(new Periodo(), 3));
        sp.setParceiro(null);
        if (!dao.save(sp)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO INSERIR SERVIÇO PESSOA!");
            return;
        }
        MatriculaCampeonato cep = new MatriculaCampeonato();
        cep.setCampeonato(campeonatoEquipe.getCampeonato());
        cep.setCampeonatoEquipe(campeonatoEquipe);
        cep.setServicoPessoa(sp);
        mc = new MatriculaCampeonatoDao().existsInCampeonato(campeonatoEquipe.getCampeonato().getId(), membroEquipe.getId(), true);
        if (mc != null) {
            GenericaMensagem.warn("Validação", "PESSOA ESTÁ CADASTRADA PARA ESTE CAMPEONADO");
            return;
        }
        CampeonatoDependente cd = new CampeonatoDependenteDao().exists(campeonatoEquipe.getCampeonato().getId(), fisicaDependente.getPessoa().getId());
        if (cd != null) {
            GenericaMensagem.warn("Validação", "PESSOA ESTÁ CADASTRADA PARA ESSA MATRÍCULA");
            return;
        }
        cd = new CampeonatoDependenteDao().existsInCampeonato(campeonatoEquipe.getCampeonato().getId(), membroEquipe.getId());
        if (cd != null) {
            GenericaMensagem.warn("Validação", "PESSOA ESTÁ CADASTRADA COMO DEPENDENTE NESTE CAMPEONADO DE " + cd.getMatriculaCampeonato().getServicoPessoa().getPessoa().getNome());
            return;
        }
        if (new MatriculaCampeonatoDao().exists(campeonatoEquipe.getId(), campeonatoEquipe.getCampeonato().getId(), membroEquipe.getId()) != null) {
            dao.rollback();
            GenericaMensagem.warn("Validação", "PESSOA JÁ ESTA NESSA EQUIPE!");
            return;
        }
        if (new MatriculaCampeonatoDao().existsInCampeonato(campeonatoEquipe.getCampeonato().getId(), membroEquipe.getId()) != null) {
            dao.rollback();
            GenericaMensagem.warn("Validação", "PESSOA JÁ ESTA CADASTRADA NESTE CAMPEONATO!");
            return;
        }
        if (!new CampeonatoEquipeDao().saveNativeCarterinha(dao, membroEquipe.getId())) {
            dao.rollback();
            GenericaMensagem.warn("Validação", "ERRO AO ADICIONAR CARTEIRINHA!");
            return;
        }
        if (!dao.save(cep)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
            return;
        }
        dao.commit();
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("matr_campeonato");
        novoLog.setCodigo(cep.getId());
        novoLog.save(
                "Campeonato Novo Membro - ID: " + cep.getId()
                + " - Titular: " + cep.getServicoPessoa().getPessoa().getNome()
                + " - Campeonato ID: " + cep.getCampeonato().getId()
                + " - " + cep.getCampeonato().getEvento().getDescricaoEvento().getGrupoEvento().getDescricao()
                + " - " + cep.getCampeonato().getEvento().getDescricaoEvento().getDescricao()
                + " - " + cep.getCampeonato().getTituloComplemento()
                + " - " + cep.getCampeonato().getModalidade().getDescricao()
                + " - Campeonato Equipe ID: " + cep.getCampeonatoEquipe().getId() + " - " + cep.getCampeonatoEquipe().getEquipe().getDescricao()
        );
        membroEquipe = null;
        fisicaDependente = new Fisica();
        GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        loadListMatriculaCampeonato(campeonatoEquipe.getId());
    }

    public void addDependente() {
        motivoInativacaoDependente = "";
        if (fisicaDependente == null || fisicaDependente.getId() == -1) {
            GenericaMensagem.warn("Validação", "PESQUISAR PESSOA!");
            return;
        }
        if (new CampeonatoDependenteDao().exists(cadastrarDependente.getId(), fisicaDependente.getPessoa().getId()) != null) {
            GenericaMensagem.warn("Validação", "PESSOA JÁ ESTA NESSA EQUIPE!");
            return;
        }
        if (fisicaDependente.getPessoa().getSocios() != null && fisicaDependente.getPessoa().getSocios().getId() != -1) {
            GenericaMensagem.warn("Validação", "DEPENDENTE NÃO PODE SER SÓCIO!");
            return;
        }
        MatriculaCampeonato mc = new MatriculaCampeonatoDao().exists(campeonatoEquipe.getId(), campeonatoEquipe.getCampeonato().getId(), fisicaDependente.getPessoa().getId());
        if (mc != null) {
            GenericaMensagem.warn("Validação", "MEMBRO MÁTRICULADO, NÃO PODE SER DEPENDENTE!");
            return;
        }
        if (cadastrarDependente.getServicoPessoa().getPessoa().getId() == fisicaDependente.getPessoa().getId()) {
            GenericaMensagem.warn("Validação", "DEPENDENTE NÃO PODE SER MEMBRO TITULAR!");
            return;
        }
        PessoaComplemento pc = cadastrarDependente.getServicoPessoa().getPessoa().getPessoaComplemento();
        CampeonatoDependente cd = new CampeonatoDependente();
        EventoServico eventoServico;
        ServicoPessoa sp = new ServicoPessoa();
        Categoria c = null;
        EventoServicoValor esv;
        if (c == null) {
            eventoServico = new EventoServicoDao().findByEvento(cadastrarDependente.getCampeonato().getEvento().getId(), null, false);
            if (eventoServico == null) {
                GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA DEPENDENTE!");
                return;
            }
            esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), null, false);
            if (esv == null) {
                sp.setNrValorFixo(0);
            } else {
                sp.setNrValorFixo(esv.getValor());
            }
        } else {
            eventoServico = new EventoServicoDao().findByEvento(cadastrarDependente.getCampeonato().getEvento().getId(), /* c.getId() */ null, false);
            if (eventoServico == null) {
                eventoServico = new EventoServicoDao().findByEvento(cadastrarDependente.getCampeonato().getEvento().getId(), null, false);
                if (eventoServico == null) {
                    GenericaMensagem.warn("Validação", "CADASTRAR TABELA DE PREÇOS PARA DEPENDENTE!");
                    return;
                }
                esv = new EventoServicoValorDao().findByEventoCategoria(eventoServico.getId(), /* c.getId() */ null, false);
                if (esv == null) {
                    sp.setNrValorFixo(0);
                } else {
                    sp.setNrValorFixo(esv.getValor());
                }
            } else {
                sp.setNrValorFixo(0);
            }
        }

        Dao dao = new Dao();
        dao.openTransaction();
        SocioCarteirinhaUtils scu = new SocioCarteirinhaUtils();
        scu.setPessoa(fisicaDependente.getPessoa());
        // 5 ANOS
        scu.setValidadeMeses(5 * 12);
        if (!scu.storeDefault(dao)) {
            return;
        }
        sp.setServicos(eventoServico.getServicos());
        sp.setNrDiaVencimento(pc.getNrDiaVencimento());
        sp.setCobranca(cadastrarDependente.getServicoPessoa().getPessoa());
        sp.setCobrancaMovimento(null);
        sp.setPessoa(fisicaDependente.getPessoa());
        sp.setAtivo(true);
        sp.setDescontoFolha(false);
        sp.setReferenciaVigoracao(DataHoje.converteDataParaReferencia(cadastrarDependente.getCampeonato().getInicio()));
        sp.setReferenciaValidade(DataHoje.converteDataParaReferencia(cadastrarDependente.getCampeonato().getFim()));
        sp.setTipoDocumento((FTipoDocumento) dao.find(new TipoDocumento(), 13));
        sp.setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
        sp.setPeriodoCobranca((Periodo) dao.find(new Periodo(), 3));
        sp.setParceiro(null);
        if (!dao.save(sp)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO INSERIR SERVIÇO PESSOA!");
            return;
        }
        if (!new CampeonatoEquipeDao().saveNativeCarterinha(dao, fisicaDependente.getPessoa().getId())) {
            dao.rollback();
            GenericaMensagem.warn("Validação", "ERRO AO ADICIONAR CARTEIRINHA!");
            return;
        }

        cd.setMatriculaCampeonato(cadastrarDependente);
        cd.setParentesco((Parentesco) dao.find(new Parentesco(), idPatentesco));
        cd.setServicoPessoa(sp);

        if (!dao.save(cd)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO INSERIR DEPENDENTE!");
            return;
        }
        dao.commit();
        cadastrarDependente.setListCampeonatoDependente(null);
        fisicaDependente = new Fisica();
        loadListCampeonatoDependentens(cadastrarDependente.getId());
        GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("eve_campeonato_dependente");
        novoLog.setCodigo(cd.getId());
        novoLog.delete(
                "Campeonato Novo Dependente - ID: " + cd.getId()
                + " - Dependente: " + cd.getServicoPessoa().getPessoa().getNome()
                + " - Parentesco: " + cd.getParentesco().getParentesco()
                + " - Titular: " + cd.getMatriculaCampeonato().getServicoPessoa().getPessoa().getNome()
                + " - Campeonato ID: " + cd.getMatriculaCampeonato().getCampeonato().getId()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getEvento().getDescricaoEvento().getGrupoEvento().getDescricao()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getEvento().getDescricaoEvento().getDescricao()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getTituloComplemento()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getModalidade().getDescricao()
                + " - Campeonato Equipe ID: " + cd.getMatriculaCampeonato().getCampeonatoEquipe().getId() + " - " + cd.getMatriculaCampeonato().getCampeonatoEquipe().getEquipe().getDescricao()
        );
    }

    public void deleteMembroEquipe(MatriculaCampeonato cep) {
        Dao dao = new Dao();
        dao.openTransaction();
        cep.getServicoPessoa().setAtivo(false);
        cep.getServicoPessoa().setMotivoInativacao("INATIVADO PELO OPERADOR: " + Usuario.getUsuario().getPessoa().getNome());
        cep.getServicoPessoa().setDtInativacao(new Date());
        if (!dao.update(cep.getServicoPessoa())) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
            return;
        }
        cep.setDtInativacao(new Date());
        if (!dao.update(cep)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER MEMBRO!");
            return;
        }
        loadListCampeonatoDependentens(cep.getId());
        for (int i = 0; i < listCampeonatoDependente.size(); i++) {
            listCampeonatoDependente.get(i).getServicoPessoa().setAtivo(false);
            listCampeonatoDependente.get(i).getServicoPessoa().setInativacao("SISTEMA");
            listCampeonatoDependente.get(i).getServicoPessoa().setDtInativacao(new Date());
            if (!dao.update(listCampeonatoDependente.get(i).getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
                return;
            }
        }
        dao.commit();
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("matr_campeonato");
        novoLog.setCodigo(cep.getId());
        novoLog.delete(
                "Campeonato Membro Inativação - ID: " + cep.getId()
                + " - Titular: " + cep.getServicoPessoa().getPessoa().getNome()
                + " - Campeonato ID: " + cep.getCampeonato().getId()
                + " - " + cep.getCampeonato().getEvento().getDescricaoEvento().getGrupoEvento().getDescricao()
                + " - " + cep.getCampeonato().getEvento().getDescricaoEvento().getDescricao()
                + " - " + cep.getCampeonato().getTituloComplemento()
                + " - " + cep.getCampeonato().getModalidade().getDescricao()
                + " - Campeonato Equipe ID: " + cep.getCampeonatoEquipe().getId() + " - " + cep.getCampeonatoEquipe().getEquipe().getDescricao()
        );
        motivoInativacao = "";
        GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        loadListMatriculaCampeonato(cep.getCampeonatoEquipe().getId());

    }

    public void deleteCampeonatoDependente(CampeonatoDependente cd) {
        Dao dao = new Dao();
        dao.openTransaction();
        cd.getServicoPessoa().setAtivo(false);
        if (motivoInativacaoDependente.isEmpty()) {
            motivoInativacaoDependente = "INATIVADO PELO OPERADOR";
        }
        cd.getServicoPessoa().setMotivoInativacao("INATIVADO PELO OPERADOR: " + Usuario.getUsuario().getPessoa().getNome());
        cd.getServicoPessoa().setDtInativacao(new Date());
        if (!dao.update(cd.getServicoPessoa())) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
            return;
        }
        cd.setServicoPessoa(null);
        if (!dao.delete(cd)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER DEPENDENTE!");
            return;
        }
        dao.commit();
        GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("eve_campeonato_dependente");
        novoLog.setCodigo(cd.getId());
        novoLog.delete(
                "Campeonato Dependente Inativação - ID: " + cd.getId()
                + " - Dependente: " + cd.getServicoPessoa().getPessoa().getNome()
                + " - Parentesco: " + cd.getParentesco().getParentesco()
                + " - Titular: " + cd.getMatriculaCampeonato().getServicoPessoa().getPessoa().getNome()
                + " - Campeonato ID: " + cd.getMatriculaCampeonato().getCampeonato().getId()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getEvento().getDescricaoEvento().getGrupoEvento().getDescricao()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getEvento().getDescricaoEvento().getDescricao()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getTituloComplemento()
                + " - " + cd.getMatriculaCampeonato().getCampeonato().getModalidade().getDescricao()
                + " - Campeonato Equipe ID: " + cd.getMatriculaCampeonato().getCampeonatoEquipe().getId() + " - " + cd.getMatriculaCampeonato().getCampeonatoEquipe().getEquipe().getDescricao()
        );
        cadastrarDependente.setListCampeonatoDependente(null);
        loadListCampeonatoDependentens(cadastrarDependente.getId());
    }

    public CampeonatoEquipe getCampeonatoEquipe() {
        if (GenericaSessao.exists("campeonatoEquipePesquisa")) {
            campeonatoEquipe = (CampeonatoEquipe) GenericaSessao.getObject("campeonatoEquipePesquisa", true);
        }
        return campeonatoEquipe;
    }

    public void setCampeonatoEquipe(CampeonatoEquipe campeonatoEquipe) {
        this.campeonatoEquipe = campeonatoEquipe;
    }

    public List<SelectItem> getListCampeonatos() {
        return listCampeonatos;
    }

    public void setListCampeonatos(List<SelectItem> listCampeonatos) {
        this.listCampeonatos = listCampeonatos;
    }

    public List<SelectItem> getListEquipes() {
        return listEquipes;
    }

    public void setListEquipes(List<SelectItem> listEquipes) {
        this.listEquipes = listEquipes;
    }

    public Integer getIdCampeonato() {
        return idCampeonato;
    }

    public void setIdCampeonato(Integer idCampeonato) {
        this.idCampeonato = idCampeonato;
    }

    public Integer getIdEquipe() {
        return idEquipe;
    }

    public void setIdEquipe(Integer idEquipe) {
        this.idEquipe = idEquipe;
    }

    public List<CampeonatoEquipe> getListCampeonatoEquipes() {
        return listCampeonatoEquipes;
    }

    public void setListCampeonatoEquipes(List<CampeonatoEquipe> listCampeonatoEquipes) {
        this.listCampeonatoEquipes = listCampeonatoEquipes;
    }

    public Pessoa getMembroEquipe() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            membroEquipe = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return membroEquipe;
    }

    public void setMembroEquipe(Pessoa membroEquipe) {
        this.membroEquipe = membroEquipe;
    }

    public Boolean getMembroEquipeDebito() {
        if (membroEquipe != null && membroEquipe.getId() != -1) {
            if (new FunctionsDao().inadimplente(membroEquipe.getId())) {
                return true;
            }
        }
        return false;
    }

    public void listener(String tcase) {
        if (tcase.equals("membros_equipe")) {
            this.editMembrosEquipe = false;
            this.campeonatoEquipe = new CampeonatoEquipe();
            loadListEquipes();
        }
        if (tcase.equals("dependentes")) {
            this.editDependentes = false;
        }

    }

    public Boolean getEditMembrosEquipe() {
        return editMembrosEquipe;
    }

    public void setEditMembrosEquipe(Boolean editMembrosEquipe) {
        this.editMembrosEquipe = editMembrosEquipe;
    }

    public List<MatriculaCampeonato> getListMatriculaCampeonato() {
        return listMatriculaCampeonato;
    }

    public void setListMatriculaCampeonato(List<MatriculaCampeonato> listMatriculaCampeonato) {
        this.listMatriculaCampeonato = listMatriculaCampeonato;
    }

    public void defineDependente(MatriculaCampeonato mc) {
        editDependentes = true;
        editMembrosEquipe = false;
        cadastrarDependente = mc;
        fisicaDependente = new Fisica();
        loadListParentesco();
        loadListCampeonatoDependentens(cadastrarDependente.getId());
    }

    public MatriculaCampeonato getCadastrarDependente() {
        return cadastrarDependente;
    }

    public void setCadastrarDependente(MatriculaCampeonato cadastrarDependente) {
        this.cadastrarDependente = cadastrarDependente;
    }

    public Boolean getEditDependentes() {
        return editDependentes;
    }

    public void setEditDependentes(Boolean editDependentes) {
        this.editDependentes = editDependentes;
        if (!this.editDependentes) {
            editMembrosEquipe = true;
        }
    }

    public Fisica getFisicaDependente() {
        if (GenericaSessao.exists("fisicaPesquisaGenerica")) {
            fisicaDependente = (Fisica) GenericaSessao.getObject("fisicaPesquisaGenerica", true);
            loadListParentesco();
        }
        return fisicaDependente;
    }

    public void setFisicaDependente(Fisica fisicaDependente) {
        this.fisicaDependente = fisicaDependente;
    }

    public Boolean getFisicaDependenteDebito() {
        if (fisicaDependente != null && fisicaDependente.getId() != -1) {
            if (new FunctionsDao().inadimplente(fisicaDependente.getPessoa().getId())) {
                return true;
            }
        }
        return false;
    }

    public CampeonatoDependente getCampeonatoDependente() {
        return campeonatoDependente;
    }

    public void setCampeonatoDependente(CampeonatoDependente campeonatoDependente) {
        this.campeonatoDependente = campeonatoDependente;
    }

    public List<CampeonatoDependente> getListCampeonatoDependente() {
        return listCampeonatoDependente;
    }

    public void setListCampeonatoDependente(List<CampeonatoDependente> listCampeonatoDependente) {
        this.listCampeonatoDependente = listCampeonatoDependente;
    }

    public void loadListParentesco() {
        listPatentesco = new ArrayList();
        idPatentesco = null;
        if (fisicaDependente.getId() != -1) {
            List<Parentesco> list = new ParentescoDao().findBySexo(fisicaDependente.getSexo());
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idPatentesco = list.get(i).getId();
                }
                listPatentesco.add(new SelectItem(list.get(i).getId(), list.get(i).getParentesco(), list.get(i).getParentesco()));
            }
        }
    }

    public Integer getIdPatentesco() {
        return idPatentesco;
    }

    public void setIdPatentesco(Integer idPatentesco) {
        this.idPatentesco = idPatentesco;
    }

    public List<SelectItem> getListPatentesco() {
        return listPatentesco;
    }

    public void setListPatentesco(List<SelectItem> listPatentesco) {
        this.listPatentesco = listPatentesco;
    }

    public List<SelectItem> getListPatentescoEdit(String sexo) {
        List<SelectItem> selectItems = new ArrayList();
        List<Parentesco> list = new ParentescoDao().findBySexo(sexo);
        for (int i = 0; i < list.size(); i++) {
            selectItems.add(new SelectItem(list.get(i).getId(), list.get(i).getParentesco(), list.get(i).getParentesco()));
        }
        return selectItems;
    }

    public void updateCampeonatoDependente(CampeonatoDependente cd) {
        Dao dao = new Dao();
        Parentesco p = (Parentesco) dao.find(new Parentesco(), cd.getParentesco().getId());
        cd.setParentesco(p);
        if (!dao.update(cd, true)) {
            GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO!");
        }
    }

    public CampeonatoEquipe getCampeonatoEquipeDelete() {
        return campeonatoEquipeDelete;
    }

    public void setCampeonatoEquipeDelete(CampeonatoEquipe campeonatoEquipeDelete) {
        this.campeonatoEquipeDelete = campeonatoEquipeDelete;
    }

    public Boolean getAtivas() {
        return ativas;
    }

    public void setAtivas(Boolean ativas) {
        this.ativas = ativas;
    }

    public String getMotivoInativacao() {
        return motivoInativacao;
    }

    public void setMotivoInativacao(String motivoInativacao) {
        this.motivoInativacao = motivoInativacao;
    }

    public String getMotivoInativacaoDependente() {
        return motivoInativacaoDependente;
    }

    public void setMotivoInativacaoDependente(String motivoInativacaoDependente) {
        this.motivoInativacaoDependente = motivoInativacaoDependente;
    }

    public void onRowSelect(SelectEvent event) {
        selectedMC.add(((MatriculaCampeonato) event.getObject()));
    }

    public void onRowUnselect(UnselectEvent event) {
        boolean remove = selectedMC.remove((MatriculaCampeonato) event.getObject());
    }

    public void toggleSelectedListener() {
        System.out.println(selectedMC.size());
    }

    public List<MatriculaCampeonato> getSelectedMC() {
        return selectedMC;
    }

    public void setSelectedMC(List<MatriculaCampeonato> selectedMC) {
        this.selectedMC = selectedMC;
    }

    public Date getDataInicialSuspencao() {
        return dataInicialSuspencao;
    }

    public void setDataInicialSuspencao(Date dataInicialSuspencao) {
        this.dataInicialSuspencao = dataInicialSuspencao;
    }

    public Date getDataFimSuspencao() {
        return dataFimSuspencao;
    }

    public void setDataFimSuspencao(Date dataFimSuspencao) {
        this.dataFimSuspencao = dataFimSuspencao;
    }

    public String getMotivoSuspensao() {
        return motivoSuspensao;
    }

    public void setMotivoSuspensao(String motivoSuspensao) {
        this.motivoSuspensao = motivoSuspensao;
    }

    public void confirmaSuspensao() {
        if (dataInicialSuspencao == null) {
            GenericaMensagem.warn("Validação", "Informar data inicial!");
            return;
        }
        if (dataFimSuspencao == null) {
            GenericaMensagem.warn("Validação", "Informar data final!");
            return;
        }
        if (motivoSuspensao.isEmpty() || motivoSuspensao.length() < 5) {
            GenericaMensagem.warn("Validação", "Informar motivo válido!");
            return;
        }
        Dao dao = new Dao();
        for (int i = 0; i < selectedMC.size(); i++) {
            selectedMC.get(i).setDtSuspensaoInicio(dataInicialSuspencao);
            selectedMC.get(i).setDtSuspensaoFim(dataFimSuspencao);
            selectedMC.get(i).setMotivoSuspensao(motivoSuspensao);
            selectedMC.get(i).setDtInativacao(new Date());
            selectedMC.get(i).getServicoPessoa().setMotivoInativacao("JOGADOR SUSPENSO: " + motivoSuspensao + " - OPERADOR: " + Usuario.getUsuario().getPessoa().getNome());
            dao.update(selectedMC.get(i), true);
            selectedMC.get(i).getServicoPessoa().setAtivo(false);
            dao.update(selectedMC.get(i).getServicoPessoa(), true);
        }
        dataInicialSuspencao = new Date();
        dataFimSuspencao = null;
        motivoSuspensao = "";
        selectedMC = new ArrayList();
        loadListMatriculaCampeonato(null);
        GenericaMensagem.info("Validação", "Membros suspensos com sucesso!");
        PF.closeDialog("dlg_suspender_membros");
        PF.closeDialog("dlg_confirma_suspensao");
        PF.update("form_campeonato_equipe");
    }

    public void desfazerSuspensao(MatriculaCampeonato cep) {
        Dao dao = new Dao();
        dao.openTransaction();
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        novoLog.setTabela("matr_campeonato");
        novoLog.setCodigo(cep.getId());
        novoLog.update("",
                "Campeonato Membro Cancelamento de Suspensão - ID: " + cep.getId()
                + " - Titular: " + cep.getServicoPessoa().getPessoa().getNome()
                + " - Campeonato ID: " + cep.getCampeonato().getId()
                + " - " + cep.getCampeonato().getEvento().getDescricaoEvento().getDescricao()
                + " - " + cep.getCampeonato().getTituloComplemento()
                + " - " + cep.getCampeonato().getModalidade().getDescricao()
                + " - Campeonato Equipe ID: " + cep.getCampeonatoEquipe().getId() + " - " + cep.getCampeonatoEquipe().getEquipe().getDescricao()
                + " - Período de " + DataHoje.converteData(cep.getDtSuspensaoInicio()) + " até " + DataHoje.converteData(cep.getDtSuspensaoInicio())
                + " - Motivo " + cep.getMotivoSuspensao()
        );
        cep.setDtSuspensaoFim(null);
        cep.setDtSuspensaoInicio(null);
        cep.setMotivoSuspensao("");
        cep.setDtInativacao(new Date());
        if (!dao.update(cep)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO REMOVER MEMBRO!");
            return;
        }
        dao.commit();
        novoLog.saveList();
        GenericaMensagem.info("Sucesso", "SUSPENSÃO CANCELADA");
        if (editMembrosEquipe) {
            loadListMatriculaCampeonato(null);
        } else {
            loadListMembrosSuspensos();
        }
    }

    public void inativarMembrosEquipe() {
        if (motivoInativacao.isEmpty() || motivoInativacao.length() < 5) {
            GenericaMensagem.warn("Validação", "Informar motivo válido!");
            PF.update("form_campeonato_equipe:i_i_d");
            return;
        }
        Dao dao = new Dao();
        dao.openTransaction();
        NovoLog novoLog = new NovoLog();
        novoLog.startList();
        for (int i = 0; i < selectedMC.size(); i++) {
            selectedMC.get(i).getServicoPessoa().setDtInativacao(new Date());
            selectedMC.get(i).getServicoPessoa().setMotivoInativacao(motivoInativacao + " - INATIVADO PELO OPERADOR: " + Usuario.getUsuario().getPessoa().getNome());
            selectedMC.get(i).getServicoPessoa().setAtivo(false);
            dao.update(selectedMC.get(i), true);
            if (!dao.update(selectedMC.get(i).getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
                PF.update("form_campeonato_equipe:i_i_d");
                return;
            }
            selectedMC.get(i).setDtInativacao(new Date());
            if (!dao.update(selectedMC.get(i))) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO REMOVER MEMBRO!");
                PF.update("form_campeonato_equipe:i_i_d");
                return;
            }

            List<CampeonatoDependente> listCD = new CampeonatoDependenteDao().findByMatriculaCampeonato(selectedMC.get(i).getId());
            for (int x = 0; x < listCD.size(); x++) {
                listCD.get(x).getServicoPessoa().setAtivo(false);
                listCD.get(x).getServicoPessoa().setInativacao("SISTEMA");
                listCD.get(x).getServicoPessoa().setDtInativacao(new Date());
                if (!dao.update(listCD.get(x).getServicoPessoa())) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO PESSOA!");
                    PF.update("form_campeonato_equipe:i_i_d");
                    return;
                }
                novoLog.setTabela("matr_campeonato");
                novoLog.setCodigo(selectedMC.get(i).getId());
                novoLog.delete(
                        "Campeonato Membro Inativação - ID: " + selectedMC.get(i).getId()
                        + " - Titular: " + selectedMC.get(i).getServicoPessoa().getPessoa().getNome()
                        + " - Campeonato ID: " + selectedMC.get(i).getCampeonato().getId()
                        + " - " + selectedMC.get(i).getCampeonato().getEvento().getDescricaoEvento().getGrupoEvento().getDescricao()
                        + " - " + selectedMC.get(i).getCampeonato().getEvento().getDescricaoEvento().getDescricao()
                        + " - " + selectedMC.get(i).getCampeonato().getTituloComplemento()
                        + " - " + selectedMC.get(i).getCampeonato().getModalidade().getDescricao()
                        + " - Campeonato Equipe ID: " + selectedMC.get(i).getCampeonatoEquipe().getId() + " - " + selectedMC.get(i).getCampeonatoEquipe().getEquipe().getDescricao()
                );
            }
        }
        dao.commit();
        novoLog.saveList();
        motivoInativacao = "";
        selectedMC = new ArrayList();
        loadListMatriculaCampeonato(null);
        GenericaMensagem.info("Sucesso", "REGISTRO INATIVADO");
        PF.closeDialog("dlg_inativar_membros");
        PF.update("form_campeonato_equipe");

    }

    public List<MatriculaCampeonato> getListMatriculaCampeonatoSuspensos() {
        return listMatriculaCampeonatoSuspensos;
    }

    public void setListMatriculaCampeonatoSuspensos(List<MatriculaCampeonato> listMatriculaCampeonatoSuspensos) {
        this.listMatriculaCampeonatoSuspensos = listMatriculaCampeonatoSuspensos;
    }

    public void loadListMembrosSuspensos() {
        listMatriculaCampeonatoSuspensos = new ArrayList();
        listMatriculaCampeonatoSuspensos = new MatriculaCampeonatoDao().findAllSuspensos();
    }

    public Boolean getVigentes() {
        return vigentes;
    }

    public void setVigentes(Boolean vigentes) {
        this.vigentes = vigentes;
    }

}
