package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.EventoServicoValorDao;
import br.com.rtools.associativo.dao.DescricaoEventoDao;
import br.com.rtools.associativo.dao.EventoServicoDao;
import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.CaravanaDao;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class CaravanaBean implements Serializable {

    private Caravana caravana;
    private Servicos servicos;
    private EventoServico eventoServico;
    private EventoServicoValor eventoServicoValor;
    private Integer idDescricaoEvento;
    private Integer idGrupoEvento;
    private Integer idServicos;
    private Integer idIndex;
    private Integer idIndexServicos;
    private String valor;
    private List<DataObject> listaServicosAdd;
    private List<Caravana> listaCaravana;
    private boolean habilitado;

    private List<SelectItem> listaDescricaoEvento = new ArrayList();
    private List<SelectItem> listaGrupoEvento = new ArrayList();
    private List<SelectItem> listaServicos = new ArrayList();
    private List listFiles;

    @PostConstruct
    public void init() {
        caravana = new Caravana();
        servicos = new Servicos();
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        idDescricaoEvento = 0;
        idGrupoEvento = 0;
        idServicos = 0;
        idIndex = -1;
        idIndexServicos = -1;
        valor = "0.0";
        listaServicosAdd = new ArrayList();
        listaCaravana = new ArrayList();
        habilitado = true;
        listaDescricaoEvento = new ArrayList();
        listaGrupoEvento = new ArrayList();
        listaServicos = new ArrayList();
        listFiles = new ArrayList();
        loadListaServicos();
        loadListaGrupoEvento();
        loadListaDescricaoEvento();
        loadListCaravanas();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("caravanaBean");
    }

    public void loadListaServicos() {
        listaServicos = new ArrayList();
        List<Servicos> list = new ServicosDao().pesquisaTodos(138);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServicos = list.get(i).getId();
            }
            listaServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListaGrupoEvento() {
        listaGrupoEvento = new ArrayList();
        List<GrupoEvento> list = (List<GrupoEvento>) new Dao().list(new GrupoEvento(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupoEvento = list.get(i).getId();
            }
            listaGrupoEvento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListaDescricaoEvento() {
        listaDescricaoEvento = new ArrayList();
        if (listaGrupoEvento.isEmpty()) {
            return;
        }
        List<DescricaoEvento> list = new DescricaoEventoDao().pesquisaDescricaoPorGrupo(2);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDescricaoEvento = list.get(i).getId();
            }
            listaDescricaoEvento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListCaravanas() {
        listaCaravana = new ArrayList();
        listaCaravana = (List<Caravana>) new CaravanaDao().findAll("all_desc");
    }

    public Boolean validaSalvar() {
        if (listaDescricaoEvento.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR DESCRIÇÃO DE EVENTOS!");
            return false;
        }

        if (caravana.getDataSaida().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE UMA DATA DE SAÍDA!");
            return false;
        }

        if (caravana.getHoraSaida().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE UM HORÁRIO DE SAÍDA!");
            return false;
        }

        if (caravana.getDataChegada().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE UMA DATA DE CHEGADA!");
            return false;
        }

        if (caravana.getHoraChegada().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE UM HORÁRIO DE CHEGADA!");
            return false;
        }

        if (caravana.getDataRetorno().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE UMA DATA DE RETORNO!");
            return false;
        }

        if (caravana.getHoraRetorno().isEmpty()) {
            GenericaMensagem.warn("Validação", "DIGITE UM HORÁRIO DE RETORNO!");
            return false;
        }

        return true;
    }

    public void save() {
        if (!validaSalvar()) {
            return;
        }

        Dao dao = new Dao();
        AEvento aEvento = new AEvento();
        dao.openTransaction();

        DescricaoEvento de = (DescricaoEvento) dao.find(new DescricaoEvento(), idDescricaoEvento);
        if (caravana.getId() == null) {
            Evt evt = new Evt();
            if (!dao.save(evt)) {
                GenericaMensagem.warn("Erro", "AO SALVAR EVT");
                dao.rollback();
                return;
            }
            caravana.setEvt(evt);

            aEvento.setDescricaoEvento(de);
            if (!dao.save(aEvento)) {
                GenericaMensagem.warn("Erro", "AO SALVAR EVENTO!");
                dao.rollback();
                return;
            }

            caravana.setEvento(aEvento);
            if (!dao.save(caravana)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
            } else {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                dao.commit();
                loadListCaravanas();
            }
        } else {
            if (caravana.getEvt() == null) {
                Evt evt = new Evt();
                if (!dao.save(evt)) {
                    GenericaMensagem.warn("Erro", "AO SALVAR EVT!");
                    dao.rollback();
                    return;
                }
                caravana.setEvt(evt);
            }
            aEvento = (AEvento) dao.find(new AEvento(), caravana.getEvento().getId());
            aEvento.setDescricaoEvento(de);
            if (!dao.update(aEvento)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EVENTO!");
                dao.rollback();
                return;
            }
            caravana.setEvento(aEvento);
            if (!dao.update(caravana)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR CARAVANA!");
                dao.rollback();
            } else {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                dao.commit();
                loadListCaravanas();
            }
        }
    }

    public String adicionarServico() {
        if (getListaServicos().isEmpty()) {
            GenericaMensagem.warn("Erro", "CADASTRAR SERVIÇOS!");
            return null;
        }

        if (caravana.getId() == null) {
            GenericaMensagem.warn("Atenção", "SALVAR A CARAVANA ANTES DE ADICIONAR SERVIÇOS!");
            return null;
        }

        Dao dao = new Dao();
        dao.openTransaction();
        servicos = new Servicos();
        servicos = (Servicos) dao.find(new Servicos(), idServicos);
        float vl = 0;
        eventoServico.setServicos(servicos);
        if (eventoServico.getId() == -1) {
            eventoServico.setEvento(caravana.getEvento());
            if (!dao.save(eventoServico)) {
                GenericaMensagem.warn("Erro", "AO INSERIR EVENTO SERVIÇO!");
                dao.rollback();
                return null;
            }
        } else if (!dao.update(eventoServico)) {
            GenericaMensagem.warn("Erro", "AO ATAULIZAR EVENTO SERVIÇO!");
            dao.rollback();
            return null;
        }
        eventoServicoValor.setEventoServico(eventoServico);
        vl = Float.valueOf(valor);
        eventoServicoValor.setValor(vl);
        if (eventoServicoValor.getId() == -1) {
            if (!dao.save(eventoServicoValor)) {
                GenericaMensagem.warn("Erro", "AO INSERIR SERVIÇO VALOR!");
                dao.rollback();
                return null;
            }
        } else if (!dao.update(eventoServicoValor)) {
            GenericaMensagem.warn("Erro", "AO ATUALIZAR SERVIÇO VALOR!");
            dao.rollback();
            return null;
        }

        dao.commit();
        GenericaMensagem.info("Sucesso", "SERVIÇO INSERIDO");
        if (eventoServico.isIndividual()) {
            listaServicosAdd.add(new DataObject(servicos, eventoServico, eventoServicoValor, eventoServico.isIndividual(), valor, "<< Sim >>"));
        } else {
            listaServicosAdd.add(new DataObject(servicos, eventoServico, eventoServicoValor, eventoServico.isIndividual(), valor, "<< Não >>"));
        }
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        valor = "0";
        return null;
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        if (caravana.getId() != null) {
            caravana = (Caravana) dao.find(caravana);
            AEvento aEvento = (AEvento) dao.find(caravana.getEvento());
            if (!listaServicosAdd.isEmpty()) {
                DataObject dtObj = null;
                for (DataObject listaServicosAdd1 : listaServicosAdd) {
                    dtObj = listaServicosAdd1;
                    if (!excluirServicos(dao, dtObj)) {
                        dao.rollback();
                        GenericaMensagem.warn("Erro", "AO REMOVER LISTA DE SERVIÇOS!");
                        return;
                    }
                }
            }
            if (!dao.delete(caravana)) {
                GenericaMensagem.warn("Erro", "AO REMOVER CARAVANA");
                dao.rollback();
                return;
            }

            if (!dao.delete(aEvento)) {
                GenericaMensagem.warn("Erro", "AO REMOVER EVENTO!");
                dao.rollback();
            } else {
                dao.commit();
                loadListCaravanas();
                caravana = new Caravana();
                GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
            }
        } else {
            GenericaMensagem.warn("Erro", "PESQUISE UMA CARAVANA!");
            dao.rollback();
        }
    }

    public String excluirServicos(DataObject dob) {
        Dao dao = new Dao();
        dao.openTransaction();
        DataObject dtObj = dob;//(DataObject) listaServicosAdd.get(idIndexServicos);
        if (excluirServicos(dao, dtObj)) {
            dao.commit();
        } else {
            dao.rollback();
        }
        return null;
    }

    public boolean excluirServicos(Dao dao, DataObject dtObj) {
        eventoServico = (EventoServico) dao.find(new EventoServico(), ((EventoServico) dtObj.getArgumento1()).getId());
        eventoServicoValor = (EventoServicoValor) dao.find(new EventoServicoValor(), ((EventoServicoValor) dtObj.getArgumento2()).getId());
        if (!dao.delete(eventoServicoValor)) {
            GenericaMensagem.warn("Erro", "AO REMOVER SERVIÇO VALOR!");
            return false;
        }

        if (!dao.delete(eventoServico)) {
            GenericaMensagem.warn("Erro", "AO REMOVER EVENTO SERVIÇO!");
            return false;
        } else {
            GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
            eventoServico = new EventoServico();
            eventoServicoValor = new EventoServicoValor();
            return true;
        }
    }

    public String novo() {
        GenericaSessao.remove("caravanaBean");
        return "caravana";
    }

    public String editar(Caravana car) {
        caravana = (Caravana) new Dao().rebind(car);
        idDescricaoEvento = caravana.getEvento().getDescricaoEvento().getId();
        idGrupoEvento = caravana.getEvento().getDescricaoEvento().getGrupoEvento().getId();
        String url = (String) GenericaSessao.getString("urlRetorno");
        GenericaSessao.put("linkClicado", true);
        if (url != null) {
            GenericaSessao.put("caravanaPesquisa", caravana);
            return url;
        }
        return "caravana";
    }

    public void editEventoServicoValor(DataObject esv) {
        eventoServico = (EventoServico) esv.getArgumento1();
        eventoServicoValor = (EventoServicoValor) esv.getArgumento2();
        idServicos = eventoServicoValor.getEventoServico().getServicos().getId();
        valor = eventoServicoValor.getValorString();
    }

    public List<SelectItem> getListaDescricaoEvento() {
        return listaDescricaoEvento;
    }

    public List<SelectItem> getListaGrupoEvento() {
        return listaGrupoEvento;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public List<DataObject> getListaServicosAdd() {
        EventoServicoDao dbE = new EventoServicoDao();
        EventoServicoValorDao dbEv = new EventoServicoValorDao();
        if (caravana.getId() != null) {
            listaServicosAdd.clear();
            List<EventoServico> evs;
            EventoServicoValor ev;
            evs = dbE.listaEventoServico(caravana.getEvento().getId());
            for (int i = 0; i < evs.size(); i++) {
                ev = dbEv.pesquisaEventoServicoValor(evs.get(i).getId());
                if (evs.get(i).isIndividual()) {
                    listaServicosAdd.add(new DataObject(evs.get(i).getServicos(), evs.get(i), ev, evs.get(i).isIndividual(), Moeda.converteR$Float(ev.getValor()), "<< Sim >>"));
                } else {
                    listaServicosAdd.add(new DataObject(evs.get(i).getServicos(), evs.get(i), ev, evs.get(i).isIndividual(), Moeda.converteR$Float(ev.getValor()), "<< Não >>"));
                }
            }
        }
        return listaServicosAdd;
    }

    public void setListaServicosAdd(List listaServicosAdd) {
        this.listaServicosAdd = listaServicosAdd;
    }

    public void refreshForm() {
    }

    public Caravana getCaravana() {
        return caravana;
    }

    public void setCaravana(Caravana caravana) {
        this.caravana = caravana;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public EventoServico getEventoServico() {
        return eventoServico;
    }

    public void setEventoServico(EventoServico eventoServico) {
        this.eventoServico = eventoServico;
    }

    public EventoServicoValor getEventoServicoValor() {
        return eventoServicoValor;
    }

    public void setEventoServicoValor(EventoServicoValor eventoServicoValor) {
        this.eventoServicoValor = eventoServicoValor;
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public List<Caravana> getListaCaravana() {
        return listaCaravana;
    }

    public void setListaCaravana(List<Caravana> listaCaravana) {
        this.listaCaravana = listaCaravana;
    }

    public Integer getIdDescricaoEvento() {
        return idDescricaoEvento;
    }

    public void setIdDescricaoEvento(Integer idDescricaoEvento) {
        this.idDescricaoEvento = idDescricaoEvento;
    }

    public Integer getIdGrupoEvento() {
        return idGrupoEvento;
    }

    public void setIdGrupoEvento(Integer idGrupoEvento) {
        this.idGrupoEvento = idGrupoEvento;
    }

    public Integer getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(Integer idServicos) {
        this.idServicos = idServicos;
    }

    public Integer getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(Integer idIndex) {
        this.idIndex = idIndex;
    }

    public Integer getIdIndexServicos() {
        return idIndexServicos;
    }

    public void setIdIndexServicos(Integer idIndexServicos) {
        this.idIndexServicos = idIndexServicos;
    }

    // ARQUIVOS
    public List getListFiles() {
        if (caravana.getId() != null) {
            listFiles.clear();
            Diretorio.getCliente();
            listFiles = Diretorio.listaArquivos("arquivos/caravana/" + caravana.getId());
        }
        return listFiles;
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        configuracaoUpload.setDiretorio("arquivos/caravana/" + caravana.getId());
        configuracaoUpload.setEvent(event);
        configuracaoUpload.setResourceFolder(true);
        if (Upload.enviar(configuracaoUpload, true)) {
            listFiles.clear();
        }
        getListFiles();
    }

    public void deleteFiles(int index) {
        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/" + getPath() + "/" + (String) ((DataObject) listFiles.get(index)).getArgumento1());
        File fl = new File(caminho);
        fl.delete();
        listFiles.remove(index);
        listFiles.clear();
        getListFiles();
        PF.update("form_caravana:id_grid_uploads");
        PF.update("form_caravana:id_btn_anexo");
    }

    public String getPath() {
        return "resources/cliente/" + GenericaSessao.getString("sessaoCliente").toLowerCase() + "/arquivos/caravana/" + caravana.getId();
    }

}
