package br.com.rtools.agendamentos.beans;

import br.com.rtools.agendamentos.AgendaHorarios;
import br.com.rtools.agendamentos.AgendaServico;
import br.com.rtools.agendamentos.AgendamentoCancelamento;
import br.com.rtools.agendamentos.Agendamentos;
import br.com.rtools.agendamentos.dao.AgendamentosDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.GrupoConvenio;
import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.associativo.dao.ConvenioDao;
import br.com.rtools.associativo.dao.GrupoConvenioDao;
import br.com.rtools.associativo.dao.SubGrupoConvenioDao;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.beans.FisicaUtils;
import br.com.rtools.seguranca.FilialRotina;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.seguranca.dao.FilialRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.Sessions;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class AgendamentosBean implements Serializable {

    // OBJETOS
    private AgendaHorarios acrescentarHorario;
    private Agendamentos agendamento;
    private Agendamentos agendamentosEdit;
    private Fisica pessoa;
    private Filial filial;
    private ConfiguracaoSocial configuracaoSocial;

    // LISTAS
    private List<ObjectAgendamentos> agendamentos;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listGrupoConvenio;
    private List<SelectItem> listSubGrupoConvenio;
    private List<SelectItem> listConvenio;
    private List<SelectItem> listServicos;
    private List<AgendaServico> listServicosAdicionados;

    // INTEGER
    private Integer idFilial;
    private Integer idGrupoConvenio;
    private Integer idSubGrupoConvenio;
    private Integer idConvenio;
    private Integer idServico;

    // DATAS
    private Date data;

    // BOLEANOS
    private Boolean liberaAcessaFilial;
    private Boolean desabilitaFilial;
    private Boolean showModal;

    public AgendamentosBean() {
        configuracaoSocial = ConfiguracaoSocial.get();
        pessoa = new Fisica();
        acrescentarHorario = new AgendaHorarios();
        agendamento = new Agendamentos();
        agendamentosEdit = new Agendamentos();

        agendamentos = new ArrayList();
        listFiliais = new ArrayList();
        listGrupoConvenio = new ArrayList();
        listSubGrupoConvenio = new ArrayList();
        listConvenio = new ArrayList();
        listServicos = new ArrayList();
        listServicosAdicionados = new ArrayList();

        idFilial = null;
        idGrupoConvenio = null;
        idSubGrupoConvenio = null;
        idConvenio = null;

        liberaAcessaFilial = false;
        desabilitaFilial = false;
        data = new Date();
        showModal = false;
        loadLiberaAcessaFilial();
        loadListFilial();
    }

    public void scheduler(ObjectAgendamentos oa) {
        if (oa.getAgendamento() == null) {
            agendamento = new Agendamentos();
        }
        if (oa.getAgendamento() == null) {
            agendamento = new Agendamentos();
        }

    }

    public void save() {
        if (agendamento.getId() == null) {

        } else {

        }
    }

    public void cancel() {
        if (agendamento.getId() == null) {

        } else {

        }
    }

    public void remove() {
        if (agendamento.getId() == null) {

        } else {

        }
    }

    public void transfer(Agendamentos agendamentos) {
        if (agendamento.getId() == null) {

        } else {

        }
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
    }

    public Filial getFilial() {
        if (Sessions.exists("acessoFilial") && filial == null) {
            desabilitaFilial = true;
            filial = ((MacFilial) Sessions.getObject("acessoFilial")).getFilial();
        }
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Boolean getDesabilitaFilial() {
        return desabilitaFilial;
    }

    public void setDesabilitaFilial(Boolean desabilitaFilial) {
        this.desabilitaFilial = desabilitaFilial;
    }

    public Boolean getLiberaAcessaFilial() {
        return liberaAcessaFilial;
    }

    public void setLiberaAcessaFilial(Boolean liberaAcessaFilial) {
        this.liberaAcessaFilial = liberaAcessaFilial;
    }

    public final void loadLiberaAcessaFilial() {
        if (!new ControleAcessoBean().permissaoValida("libera_acesso_filiais", 4)) {
            liberaAcessaFilial = true;
        }
    }

    public final void loadListFilial() {
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

    public void loadListServicos() {
        listServicos = new ArrayList();
        List<Servicos> list = (List<Servicos>) new ServicosDao().findBySubgrupoConvenioAgendamentos(idSubGrupoConvenio);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServico = list.get(i).getId();
            }
            listServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListServicosAdicionados() {
        listServicosAdicionados = new ArrayList();
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
        List list = new AgendamentosDao().findSchedules();
        for(int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            
        }
        
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
            case "new":
                agendamento = new Agendamentos();
                listener("init");
                break;
            case "init":
                loadListGrupoConvenio();
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                loadListServicosAdicionados();
                break;
            case "clear":

                break;
            case "close_new_sched":
                pessoa = new Fisica();
                showModal = false;
                break;
            case "grupo_convenio":
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                break;
            case "subgrupo_convenio":
                loadListSubGrupoConvenio();
                loadListConvenio();
                loadListServicos();
                break;
            case "convenio":

                break;
            case "filial":
                filial = (Filial) new Dao().find(new Filial(), idFilial);
                break;

            default:
                break;
        }
    }

    public AgendaHorarios getAcrescentarHorario() {
        return acrescentarHorario;
    }

    public void setAcrescentarHorario(AgendaHorarios acrescentarHorario) {
        this.acrescentarHorario = acrescentarHorario;
    }

    public Agendamentos getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamentos agendamento) {
        this.agendamento = agendamento;
    }

    public Agendamentos getAgendamentosEdit() {
        return agendamentosEdit;
    }

    public void setAgendamentosEdit(Agendamentos agendamentosEdit) {
        this.agendamentosEdit = agendamentosEdit;
    }

    public Fisica getPessoa() {
        if (Sessions.exists("fisicaPesquisa") || Sessions.exists("fisicaPesquisaGenerica")) {
            showModal = false;
            agendamento = new Agendamentos();
            pessoa = new Fisica();
            Fisica f;
            if (Sessions.exists("fisicaPesquisaGenerica")) {
                f = (Fisica) GenericaSessao.getObject("fisicaPesquisaGenerica", true);
            } else {
                f = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            }
            listener("new");
            showModal = true;
            pessoa = f;
        }
        return pessoa;
    }

    public void setPessoa(Fisica pessoa) {
        this.pessoa = pessoa;
    }

    public List<ObjectAgendamentos> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<ObjectAgendamentos> agendamentos) {
        this.agendamentos = agendamentos;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Boolean getShowModal() {
        return showModal;
    }

    public void setShowModal(Boolean showModal) {
        // this.showModal = showModal;
    }

    public void findPessoaFisica() {
        showModal = false;
        Fisica f = FisicaUtils.findByCPF(pessoa);
        PF.update("form_agendamentos");
        PF.update("form_person");
        if (f != null) {
            listener("new");
            showModal = true;
            pessoa = f;
        }
//        if (pessoa.getPessoa().getDocumento().equals("___.___.___-__")) {
//            pessoa.getPessoa().setDocumento("");
//            return;
//        }
//        if (!pessoa.getPessoa().getDocumento().isEmpty() && !pessoa.getPessoa().getDocumento().equals("___.___.___-__")) {
//            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(pessoa.getPessoa().getDocumento()))) {
//                GenericaMensagem.warn("Validação", "Documento (CPF) inválido! " + pessoa.getPessoa().getDocumento());
//                PF.update("form_agendamentos");
//                return;
//            }
//        }
//        if (pessoa.getPessoa().getId() == -1) {
//            FisicaDao db = new FisicaDao();
//            List<Fisica> list = db.pesquisaFisicaPorDoc(pessoa.getPessoa().getDocumento());
//            Boolean success = false;
//            if (!list.isEmpty()) {
//                success = true;
//                if (!FisicaUtils.validation(list.get(0), "agendamentos")) {
//                    return;
//                }
//                listener("new");
//                showModal = true;
//                pessoa = list.get(0);
//
//            }
//
//            if (success) {
//                PF.update("form_person");
//                PF.update("form_agendamentos");
//            }
//        }

    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }

    public List<AgendaServico> getListServicosAdicionados() {
        return listServicosAdicionados;
    }

    public void setListServicosAdicionados(List<AgendaServico> listServicosAdicionados) {
        this.listServicosAdicionados = listServicosAdicionados;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public class ObjectAgendamentos {

        private AgendaHorarios horario;
        private Agendamentos agendamento;
        private AgendamentoCancelamento cancelamento;

        public ObjectAgendamentos() {
            this.horario = null;
            this.agendamento = null;
            this.cancelamento = null;
        }

        public ObjectAgendamentos(AgendaHorarios horario, Agendamentos agendamento, AgendamentoCancelamento cancelamento) {
            this.horario = horario;
            this.agendamento = agendamento;
            this.cancelamento = cancelamento;
        }

        public AgendaHorarios getHorario() {
            return horario;
        }

        public void setHorario(AgendaHorarios horario) {
            this.horario = horario;
        }

        public Agendamentos getAgendamento() {
            return agendamento;
        }

        public void setAgendamento(Agendamentos agendamento) {
            this.agendamento = agendamento;
        }

        public AgendamentoCancelamento getCancelamento() {
            return cancelamento;
        }

        public void setCancelamento(AgendamentoCancelamento cancelamento) {
            this.cancelamento = cancelamento;
        }

    }

}
