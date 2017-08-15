package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.EventoBaileDao;
import br.com.rtools.associativo.dao.EventoBandaDao;
import br.com.rtools.associativo.dao.DescricaoEventoDao;
import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.BaileDao;
import br.com.rtools.associativo.dao.BandaDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
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
public class BaileBean implements Serializable {

    private EventoBaile eventoBaile = new EventoBaile();
    private EventoServico eventoServico = new EventoServico();
    private EventoServicoValor eventoServicoValor = new EventoServicoValor();
    private AEvento evento = new AEvento();
    private AEndereco endereco = new AEndereco();
    private int idIndex = -1;
    private int idIndexBanda = -1;
    private int idIndexServico = -1;
    private Integer idBanda = null;
    private int idDescricaoEvento = 0;
    private Integer idServicos = null;
    private int nrMesa = 0;
    private Integer idNrMesa = 0;
    private Integer idNrConvite = 0;
    private List<Integer> listaQuantidade = new ArrayList();
    private List<EventoBanda> listaEventoBanda = new ArrayList();
    private List<EventoBaile> listaEventoBaile = new ArrayList();
    private List<EventoServicoValor> listaEventoServicoValor = new ArrayList();
    private List<SelectItem> listaComboBanda = new ArrayList();
    private List<SelectItem> listaComboServicos = new ArrayList();
    private List<SelectItem> listaComboDescricaoEvento = new ArrayList();
    private List<SelectItem> listaMesasDisponiveis = new ArrayList();
    private List<SelectItem> listaConvitesDisponiveis = new ArrayList();
    private String msgConfirma = "";
    private String comoPesquisa = "I";
    private String descPesquisa = "";
    private String mesaTop = "";
    private String mesaLeft = "";
    private boolean limpar = false;
    DataHoje dataHoje = new DataHoje();
    private List<EventoBaileMapa> listaMesas = new ArrayList();
    private List<EventoBaileConvite> listaConvites = new ArrayList();
    private EventoBaileMapa ebmSelecionado = new EventoBaileMapa();
    private Integer idCategoria = null;
    private List<SelectItem> listaCategoria = new ArrayList();

    private boolean visibleMapa = false;
    private Servicos servicos = new Servicos();

    @PostConstruct
    public void init() {
        loadListaServicos();
        loadListBandas();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("baileBean");
    }

    public void updateTela() {
        loadListaServicos();
        updateServicoCategoria();
    }

    public void updateServicoCategoria() {
        servicos = (Servicos) new Dao().find(new Servicos(), idServicos);
        if (servicos.getId() == 13 || servicos.getId() == 15) {
            eventoServicoValor.setValor(0);
            eventoServicoValor.setIdadeInicial(0);
            eventoServicoValor.setIdadeFinal(150);
            eventoServicoValor.setSexo("A");
            eventoServico.setSocio(false);
        }
        loadCategoria();
    }

    public void loadListaServicos() {
        listaComboServicos = new ArrayList();
        List<Servicos> select = new ArrayList();
        if (eventoServico.isMesa()) {
            select.add((Servicos) new Dao().find(new Servicos(), 12));
            select.add((Servicos) new Dao().find(new Servicos(), 13));
        } else {
            select.add((Servicos) new Dao().find(new Servicos(), 14));
            select.add((Servicos) new Dao().find(new Servicos(), 15));
        }

        if (!select.isEmpty()) {
            for (int i = 0; i < select.size(); i++) {
                if (i == 0) {
                    idServicos = select.get(i).getId();
                }
                listaComboServicos.add(
                        new SelectItem(
                                select.get(i).getId(),
                                select.get(i).getDescricao(),
                                select.get(i).getDescricao()
                        )
                );
            }
            servicos = select.get(0);
        }
        loadCategoria();
    }

    public void loadCategoria() {
        listaCategoria.clear();
        List<Categoria> list;
        if (eventoBaile.getId() == -1) {
            list = new Dao().list(new Categoria());
        } else {
            Integer servico_id = null;
            if (eventoServico.getId() == -1) {
                servico_id = idServicos;
            } else {
                servico_id = eventoServico.getServicos().getId();
            }
            list = new BaileDao().listaCategoriaPorEventoServico(
                    servico_id,
                    eventoServicoValor.getSexo(),
                    eventoServicoValor.getIdadeInicial(),
                    eventoServicoValor.getIdadeFinal(),
                    eventoBaile.getEvento().getId()
            );
        }

        for (int i = 0; i < list.size(); i++) {
            listaCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getCategoria(), "" + list.get(i).getId()));
        }
    }

    public void excluirMesa(EventoBaileMapa ebm) {
        Dao dao = new Dao();
        dao.openTransaction();
        if (!dao.delete(dao.find(new EventoBaileMapa(), ebm.getId()))) {
            dao.rollback();
            GenericaMensagem.warn("Erro!", "Não foi possível excluir esta mesa!");
        } else {
            GenericaMensagem.info("Sucesso!", "Mesa excluída com sucesso!");
            dao.commit();
        }
        listaMesas = new ArrayList();
    }

    public void excluirConvite(EventoBaileConvite ebc) {
        Dao dao = new Dao();
        dao.openTransaction();
        if (!dao.delete(dao.find(new EventoBaileConvite(), ebc.getId()))) {
            dao.rollback();
            GenericaMensagem.warn("Erro!", "Não foi possível excluir este Convite!");
        } else {
            GenericaMensagem.info("Sucesso!", "Convite excluída com sucesso!");
            dao.commit();
        }
        listaConvites = new ArrayList();
    }

    public void salvarMesa(boolean all) {
        if (eventoBaile.getId() != -1) {
            Dao dao = new Dao();
            EventoBaileDao eventoBaileDB = new EventoBaileDao();
            dao.openTransaction();
            if (all) {
                boolean err = false;
                for (SelectItem listaMesasDisponivei : listaMesasDisponiveis) {
                    if (((EventoBaileMapa) eventoBaileDB.pesquisaMesaBaile(eventoBaile.getId(), Integer.parseInt(listaMesasDisponivei.getDescription()))).getId() == -1) {
                        EventoBaileMapa ebm = new EventoBaileMapa(
                                -1,
                                eventoBaile,
                                Integer.parseInt(listaMesasDisponivei.getDescription()),
                                "",
                                null,
                                null
                        );
                        if (!dao.save(ebm)) {
                            err = true;
                            break;
                        }
                    }
                }
                if (err) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível adicionar Mesas!");
                } else {
                    dao.commit();
                    idNrMesa = 0;
                    nrMesa = 0;
                    listaMesasDisponiveis.clear();
                    listaMesas.clear();

                    GenericaMensagem.info("Sucesso", "Mesas Adicionadas!");
                }
            }
        }
    }

    public void salvarConvite(boolean all) {
        if (eventoBaile.getId() != -1) {
            Dao dao = new Dao();
            EventoBaileDao eventoBaileDB = new EventoBaileDao();
            dao.openTransaction();
            if (all) {
                boolean err = false;
                for (SelectItem conviteDisponivel : listaConvitesDisponiveis) {
                    if (((EventoBaileConvite) eventoBaileDB.pesquisaConviteBaile(eventoBaile.getId(), Integer.parseInt(conviteDisponivel.getDescription()))).getId() == -1) {
                        EventoBaileConvite ebc = new EventoBaileConvite(
                                -1,
                                eventoBaile,
                                Integer.parseInt(conviteDisponivel.getDescription()),
                                null,
                                null
                        );
                        if (!dao.save(ebc)) {
                            err = true;
                            break;
                        }
                    }
                }
                if (err) {
                    dao.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível adicionar Convite!");
                } else {
                    dao.commit();
                    idNrConvite = 0;
                    listaConvitesDisponiveis.clear();
                    listaConvites.clear();

                    GenericaMensagem.info("Sucesso", "Convites Adicionadas!");
                }
            }
        }
    }

    public List<EventoBaileMapa> getListaMesas() {
        if (listaMesas.isEmpty()) {
            if (eventoBaile.getId() != -1) {
                EventoBaileDao db = new EventoBaileDao();
                listaMesas = db.listaBaileMapa(eventoBaile.getId());
            }
        }
        return listaMesas;
    }

    public void setListaMesas(List<EventoBaileMapa> listaMesas) {
        this.listaMesas = listaMesas;
    }

    public List<EventoBaileConvite> getListaConvites() {
        if (listaConvites.isEmpty()) {
            if (eventoBaile.getId() != -1) {
                listaConvites = new EventoBaileDao().listaBaileConvite(eventoBaile.getId());
            }
        }
        return listaConvites;
    }

    public void setListaConvites(List<EventoBaileConvite> listaConvites) {
        this.listaConvites = listaConvites;
    }

    public void removerEndereco() {
        if (endereco.getId() == -1) {
            endereco = new AEndereco();
        } else {
            Dao dao = new Dao();
            dao.openTransaction();
            if (!dao.delete(dao.find(new AEndereco(), endereco.getId()))) {
                GenericaMensagem.warn("Erro", "Não foi possível excluir este endereço!");
                dao.rollback();
                return;
            }
            endereco = new AEndereco();
            dao.commit();
        }
    }

    public EventoBaile getEventoBaile() {
        if (GenericaSessao.exists("enderecoPesquisa")) {
            endereco.setEndereco((Endereco) GenericaSessao.getObject("enderecoPesquisa", true));
        }
        return eventoBaile;
    }

    public void setEventoBaile(EventoBaile eventoBaile) {
        this.eventoBaile = eventoBaile;
    }

    public String novo() {
        GenericaSessao.remove("baileBean");
        return "baile";
    }

    public void novoServico() {
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        loadListaServicos();

    }

    public void atualizaListaEventoBanda() {
        getListaEventoBanda();
    }

    public void limpar() {
        if (isLimpar() == true) {
            novo();
        }
    }

    public boolean validaSalvar() {
        if (eventoBaile.getDataString().equals("")) {
            msgConfirma = "Informar data do evento!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        }

        if (eventoBaile.getHoraInicio().equals("")) {
            msgConfirma = "Necessário preencher a hora inicial do evento!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        }

        if (eventoBaile.getHoraFim().equals("")) {
            msgConfirma = "Necessário preencher a hora final do evento!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        }
//        if (eventoBaile.getQuantidadeMesas() <= 0) {
//            msgConfirma = "Necessário informar a quantidade de mesas!";
//            GenericaMensagem.warn("Erro", msgConfirma);
//            return false;
//        }
        if (endereco.getEndereco().getId() == -1) {
            msgConfirma = "Pesquise um endereço!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        }
        if (endereco.getNumero().equals("")) {
            msgConfirma = "Informar o número do endereço!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return false;
        }
        return true;
    }

    public String salvar() {
        if (!validaSalvar()) {
            return null;
        }

        Dao dao = new Dao();
        evento.setDescricaoEvento(((DescricaoEvento) dao.find(new DescricaoEvento(), Integer.parseInt(listaComboDescricaoEvento.get(idDescricaoEvento).getDescription()))));
        dao.openTransaction();

        if (evento.getId() == null) {
            if (!DataHoje.maiorData(eventoBaile.getDataString(), DataHoje.converteData(DataHoje.dataHoje()))) {
                msgConfirma = "Data do evento deve ser superior a data de hoje!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            if (!dao.save(evento)) {
                dao.rollback();
                msgConfirma = "Falha ao inserir Evento!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            Evt evt = new Evt();
            if (!dao.save(evt)) {
                dao.rollback();
                msgConfirma = "Falha ao inserir EVT!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            eventoBaile.setEvt(evt);
            eventoBaile.setEvento(evento);
            endereco.setEvento(evento);

            if (!dao.save(eventoBaile)) {
                dao.rollback();
                msgConfirma = "Falha ao inserir Evento Baile!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            if (!dao.save(endereco)) {
                dao.rollback();
                msgConfirma = "Falha ao inserir o endereço!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            listaMesas.clear();
            msgConfirma = "Registro inserido com sucesso";
            GenericaMensagem.info("Sucesso", msgConfirma);
            dao.commit();
            loadListBandas();
        } else {
            if (!dao.update(evento)) {
                dao.rollback();
                msgConfirma = "Falha ao atualizar Evento";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            if (eventoBaile.getEvt() == null) {
                Evt evt = new Evt();
                if (!dao.save(evt)) {
                    dao.rollback();
                    msgConfirma = "Falha ao inserir EVT!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                    return null;
                }
                eventoBaile.setEvt(evt);
            }

            if (!dao.update(eventoBaile)) {
                dao.rollback();
                msgConfirma = "Falha ao atualizar Evento Baile";
                GenericaMensagem.warn("Erro", msgConfirma);
                return null;
            }

            if (endereco.getId() != -1) {
                if (!dao.update(endereco)) {
                    dao.rollback();
                    msgConfirma = "Falha ao atualizar o Endereço!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                    return null;
                }
            } else {
                endereco.setEvento(evento);
                if (!dao.save(endereco)) {
                    dao.rollback();
                    msgConfirma = "Falha ao atualizar o Endereço!";
                    GenericaMensagem.warn("Erro", msgConfirma);
                    return null;
                }
            }

            listaMesas.clear();
            msgConfirma = "Registro atualizado com sucesso!";
            GenericaMensagem.info("Sucesso", msgConfirma);
            dao.commit();
        }
        return null;
    }

    public String editar(EventoBaile eve) {
        eventoBaile = eve;
        GenericaSessao.put("eventoBandaPesquisa", eventoBaile);
        descPesquisa = "";
        EventoBaileDao eventoBaileDB = new EventoBaileDao();
        evento = eventoBaile.getEvento();
        endereco = eventoBaileDB.pesquisaEnderecoEvento(eventoBaile.getEvento().getId());
        loadCategoria();
        listaEventoServicoValor.clear();
        for (int i = 0; i < listaComboDescricaoEvento.size(); i++) {
            if (Objects.equals(Integer.valueOf(listaComboDescricaoEvento.get(i).getDescription()), eve.getEvento().getDescricaoEvento().getId())) {
                idDescricaoEvento = i;
            }
        }

        listaMesas.clear();
        listaConvites.clear();

        GenericaSessao.put("linkClicado", true);
        loadListBandas();
        loadListEventoBandas();
        return (String) GenericaSessao.getString("urlRetorno");
    }

    public String excluir() {
        if (evento.getId() == null) {
            msgConfirma = "Pesquise um Baile para ser excluído!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return null;
        }

        Dao dao = new Dao();
        dao.openTransaction();

        // EXCLUIR EVENTO BANDA
        for (EventoBanda listaEventoBanda1 : listaEventoBanda) {
            if (!excluirBanda(listaEventoBanda1.getId(), dao)) {
                msgConfirma = "Banda do Evento não podem ser excluídas!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
                return null;
            }
        }

        // EXCLUIR EVENTO SERVIÇO VALOR
        for (EventoServicoValor listaEventoServicoValor1 : listaEventoServicoValor) {
            if (!excluirEventoServico(listaEventoServicoValor1.getId(), dao)) {
                msgConfirma = "Serviços de Valores não podem ser excluídos!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
                return null;
            }
        }

        // EXCLUIR EVENTO BAILE MAPA
        for (EventoBaileMapa mesas : listaMesas) {
            if (!dao.delete(dao.find(new EventoBaileMapa(), mesas.getId()))) {
                msgConfirma = "Evento Baile Mapa não podem ser excluídos!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
                return null;
            }
        }

        // EXCLUIR EVENTO BAILE CONVITE
        for (EventoBaileConvite convites : listaConvites) {
            if (!dao.delete(dao.find(new EventoBaileConvite(), convites.getId()))) {
                msgConfirma = "Evento Baile Convite não podem ser excluídos!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
                return null;
            }
        }

        endereco = (AEndereco) dao.find(endereco);
        if (!dao.delete(endereco)) {
            msgConfirma = "Endereço Baile não pode ser excluído!";
            GenericaMensagem.warn("Erro", msgConfirma);
            dao.rollback();
            return null;
        }
        eventoBaile = (EventoBaile) dao.find(eventoBaile);
        if (!dao.delete(eventoBaile)) {
            msgConfirma = "Evento Baile não pode ser excluído! ";
            GenericaMensagem.warn("Erro", msgConfirma);
            dao.rollback();
            return null;
        }

        evento = (AEvento) dao.find(evento);
        if (!dao.delete(evento)) {
            msgConfirma = "Evento não pode ser excluído! ";
            GenericaMensagem.warn("Erro", msgConfirma);
            dao.rollback();
            return null;
        }

        msgConfirma = "Evento excluído com sucesso!";
        GenericaMensagem.info("Sucesso", msgConfirma);
        dao.commit();
        GenericaSessao.remove("baileBean");
        return null;
    }

    public void adicionarServicos() {
        if (evento.getId() == null) {
            msgConfirma = "Salve este Baile antes de Adicionar Serviços!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        if (eventoServicoValor.getId() == -1) {
            if (eventoServico.isSocio() && listaCategoria.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Lista de Categoria Vazia!");
                return;
            }
            if (idServicos == null || idServicos == -1) {
                msgConfirma = "Escolha um serviço válido!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return;
            }
        }

        if (eventoServicoValor.getValor() < 0) {
            msgConfirma = "Informar o valor do serviço!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        if (eventoServicoValor.getIdadeFinal() == 0) {
            msgConfirma = "Informar a idade final!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        if (eventoServicoValor.getIdadeFinal() < eventoServicoValor.getIdadeInicial()) {
            msgConfirma = "Idade final deve ser maior ou igual a idade inicial!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        BaileDao baileDao = new BaileDao();

        Integer categoria_id = null;
        Integer servicos_id = null;

        if (eventoServicoValor.getId() == -1) {
            categoria_id = idCategoria;
            servicos_id = idServicos;
        } else {
            if (eventoServicoValor.getEventoServico().getCategoria() != null) {
                categoria_id = eventoServicoValor.getEventoServico().getCategoria().getId();
            }
            servicos_id = eventoServicoValor.getEventoServico().getServicos().getId();

        }
        if (eventoServicoValor.getId() == -1) {
            if (baileDao.pesquisaEventoServico(servicos_id,
                    eventoServico.isSocio() ? categoria_id : null,
                    evento.getId(),
                    eventoServicoValor.getIdadeInicial(),
                    eventoServicoValor.getIdadeFinal(),
                    eventoServicoValor.getSexo()
            ) != null) {
                GenericaMensagem.warn("Atenção", "Evento Serviço já adicionado!");
                return;
            }
        }
        Dao dao = new Dao();

        if (eventoServicoValor.getId() == -1) {
            eventoServico.setServicos(((Servicos) dao.find(new Servicos(), idServicos)));
            if (eventoServico.isSocio()) {
                eventoServico.setCategoria((Categoria) dao.find(new Categoria(), idCategoria));
            } else {
                eventoServico.setCategoria(null);
            }

            eventoServicoValor.setId(-1);
            eventoServico.setId(-1);
        }

        if (eventoServicoValor.getId() == -1) {
            dao.openTransaction();
            eventoServico.setEvento(evento);

            if (!dao.save(eventoServico)) {
                msgConfirma = "Serviço não pode ser adicionado!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
            }

            if (eventoServico.isMesa()) {
                eventoServicoValor.setSexo("A");
            }

            eventoServicoValor.setEventoServico(eventoServico);
            if (!dao.save(eventoServicoValor)) {
                msgConfirma = "Serviço Valor não pode ser adicionado!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
            }

            msgConfirma = "Serviço Adicionado!";
            GenericaMensagem.info("Sucesso", msgConfirma);
            listaEventoServicoValor.clear();

            dao.commit();
        } else {
            dao.openTransaction();
            if (!dao.update(eventoServico)) {
                msgConfirma = "Serviço não pode ser atualizado!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
            }
            if (!dao.update(eventoServicoValor)) {
                msgConfirma = "Serviço Valor não pode ser atualizado!";
                GenericaMensagem.warn("Erro", msgConfirma);
                dao.rollback();
            }
            listaEventoServicoValor = new ArrayList();
            msgConfirma = "Serviço atualizado!";
            GenericaMensagem.info("Sucesso", msgConfirma);
            dao.commit();

        }
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        loadListaServicos();
    }

    public void adicionarTodosServicos() {
        if (evento.getId() == null) {
            msgConfirma = "Salve este Baile antes de Adicionar Serviços!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        if (eventoServicoValor.getId() == -1) {
            if (eventoServico.isSocio() && listaCategoria.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Lista de Categoria Vazia!");
                return;
            }
            if (idServicos == null || idServicos == -1) {
                msgConfirma = "Escolha um serviço válido!";
                GenericaMensagem.warn("Erro", msgConfirma);
                return;
            }
        }

        if (eventoServicoValor.getValor() < 0) {
            msgConfirma = "Informar o valor do serviço!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        if (eventoServicoValor.getIdadeFinal() == 0) {
            msgConfirma = "Informar a idade final!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        if (eventoServicoValor.getIdadeFinal() < eventoServicoValor.getIdadeInicial()) {
            msgConfirma = "Idade final deve ser maior ou igual a idade inicial!";
            GenericaMensagem.warn("Erro", msgConfirma);
            return;
        }

        BaileDao baileDao = new BaileDao();

        Integer categoria_id = null;
        Integer servicos_id = null;

        Dao dao = new Dao();
        if (eventoServicoValor.getId() == -1) {
            eventoServico.setEvento(evento);
            servicos_id = idServicos;
            Integer idadeInicial = eventoServicoValor.getIdadeInicial();
            Integer idadeFinal = eventoServicoValor.getIdadeFinal();
            String sexo = eventoServicoValor.getSexo();
            Boolean socio = eventoServico.isSocio();
            Boolean mesa = eventoServico.isMesa();
            Boolean individual = eventoServico.isIndividual();
            String descricao = eventoServico.getDescricao();
            double valor = eventoServicoValor.getValor();
            double descontoPrimeiraVenda = eventoServicoValor.getDescontoPrimeiraVenda();
            dao.openTransaction();
            for (int i = 0; i < listaCategoria.size(); i++) {
                categoria_id = Integer.parseInt(listaCategoria.get(i).getValue().toString());
                if (baileDao.pesquisaEventoServico(
                        servicos_id,
                        categoria_id,
                        evento.getId(),
                        idadeInicial,
                        idadeFinal,
                        sexo
                ) != null) {
                    dao.rollback();
                    GenericaMensagem.warn("Atenção", "Evento Serviço já adicionado!");
                    return;
                }
                EventoServico es = new EventoServico();
                EventoServicoValor esv = new EventoServicoValor();
                es.setCategoria((Categoria) dao.find(new Categoria(), categoria_id));
                es.setServicos(((Servicos) dao.find(new Servicos(), servicos_id)));
                es.setSocio(socio);
                es.setMesa(mesa);
                es.setIndividual(individual);
                es.setDescricao(descricao);
                es.setEvento(evento);
                if (!dao.save(es)) {
                    GenericaMensagem.warn("Erro", "Serviço não pode ser adicionado!");
                    dao.rollback();
                    return;
                }
                esv.setEventoServico(es);
                esv.setSexo(sexo);
                esv.setValor(valor);
                esv.setIdadeInicial(idadeInicial);
                esv.setIdadeFinal(idadeFinal);
                esv.setDescontoPrimeiraVenda(descontoPrimeiraVenda);
                if (!dao.save(esv)) {
                    GenericaMensagem.warn("Erro", "Serviço Valor não pode ser adicionado!");
                    dao.rollback();
                    return;
                }
            }
            dao.commit();
            GenericaMensagem.info("Sucesso", "Categorias adicionadas para o serviço!");
        }
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        loadListaServicos();
        listaEventoServicoValor = new ArrayList();
    }

    public void editEventoServicoValor(EventoServicoValor esv) {
        eventoServicoValor = (EventoServicoValor) new Dao().rebind(esv);
        eventoServico = eventoServicoValor.getEventoServico();
        loadListaServicos();
        idServicos = eventoServicoValor.getEventoServico().getServicos().getId();
        servicos = eventoServicoValor.getEventoServico().getServicos();
    }

    public String removerEventoServico(EventoServicoValor esv) {
        Dao dao = new Dao();
        dao.openTransaction();
        if (excluirEventoServico(esv.getId(), dao)) {
            listaEventoServicoValor = new ArrayList();
            eventoServicoValor = new EventoServicoValor();
            eventoServico = new EventoServico();
            GenericaMensagem.info("Sucesso", "Serviço removido com sucesso! Editar valores");
            dao.commit();

            loadListaServicos();
        } else {
            GenericaMensagem.warn("Atenção", "Serviço não pode ser removido!");
            dao.rollback();
        }
        return null;
    }

    public boolean excluirEventoServico(Integer id, Dao dao) {
        eventoServicoValor = (EventoServicoValor) dao.find(new EventoServicoValor(), id);
        if (eventoServicoValor.getId() != -1) {
            if (dao.delete(eventoServicoValor)) {
                eventoServico = (EventoServico) dao.find(new EventoServico(), eventoServicoValor.getEventoServico().getId());
                if (dao.delete(eventoServico)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public void adicionarBanda() {
        if (evento.getId() == null) {
            GenericaMensagem.warn("Atenção", "Salve este Baile antes de Adicionar uma Banda!");
            return;
        }
        if (listaComboBanda.isEmpty() || idBanda == null) {
            GenericaMensagem.warn("Atenção", "Nenhuma Banda para ser Adicionada!");
            return;
        }
        Dao dao = new Dao();
        Banda banda = (Banda) dao.find(new Banda(), idBanda);
        for (EventoBanda eb : listaEventoBanda) {
            if (eb.getBanda().getId() == banda.getId()) {
                GenericaMensagem.warn("Atenção", "Banda já cadastrada para esse Evento");
                return;
            }
        }

        EventoBanda eb = new EventoBanda(-1, banda, evento);

        dao.openTransaction();
        if (!dao.save(eb)) {
            dao.rollback();
            GenericaMensagem.error("Erro", "Banda não pode ser adicionada!");
        }

        dao.commit();
        GenericaMensagem.info("Sucesso", "Banda adicionada!");
        loadListEventoBandas();
        loadListBandas();
    }

    public void removerBanda(EventoBanda linha) {
        Dao dao = new Dao();
        dao.openTransaction();
        if (!excluirBanda(linha.getId(), dao)) {
            GenericaMensagem.error("Error", "Não foi possível remover Banda!");
            dao.rollback();
            return;
        }
        dao.commit();

        GenericaMensagem.info("Sucesso", "Banda Excluída!");
        loadListEventoBandas();
        loadListBandas();
    }

    public boolean excluirBanda(Integer id, Dao dao) {
        EventoBanda eb = (EventoBanda) dao.find(new EventoBanda(), id);
        if (eb.getId() != -1) {
            return dao.delete(eb);
        }
        return false;
    }

    public String converteMoeda(double v) {
        return Moeda.converteR$Double(v);
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

    public AEvento getEvento() {
        return evento;
    }

    public void setEvento(AEvento evento) {
        this.evento = evento;
    }

    public AEndereco getEndereco() {
        return endereco;
    }

    public void setEndereco(AEndereco endereco) {
        this.endereco = endereco;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public Integer getIdBanda() {
        return idBanda;
    }

    public void setIdBanda(Integer idBanda) {
        this.idBanda = idBanda;
    }

    public int getIdDescricaoEvento() {
        return idDescricaoEvento;
    }

    public void setIdDescricaoEvento(int idDescricaoEvento) {
        this.idDescricaoEvento = idDescricaoEvento;
    }

    public Integer getIdServicos() {
        return idServicos;
    }

    public void setIdServicos(Integer idServicos) {
        this.idServicos = idServicos;
    }

    public void loadListEventoBandas() {
        listaEventoBanda = new ArrayList();
        listaEventoBanda = new EventoBandaDao().pesquisaBandasDoEvento(eventoBaile.getEvento().getId());
    }

    public List<EventoBanda> getListaEventoBanda() {
        return listaEventoBanda;
    }

    public void setListaBanda(List<EventoBanda> listaEventoBanda) {
        this.setListaEventoBanda(listaEventoBanda);
    }

    public void loadListBandas() {
        listaComboBanda = new ArrayList();
        if (evento.getId() != null) {
            List<Banda> list = new BandaDao().findAllNotInEvento(evento.getId());
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idBanda = list.get(i).getId();
                    }
                    listaComboBanda.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
                }
            }
        }
    }

    public List<SelectItem> getListaComboBanda() {
        return listaComboBanda;
    }

    public void setListaComboBanda(List<SelectItem> listaComboBanda) {
        this.listaComboBanda = listaComboBanda;
    }

    public List<SelectItem> getListaComboServicos() {
        return listaComboServicos;
    }

    public void setListaComboServicos(List<SelectItem> listaComboServicos) {
        this.listaComboServicos = listaComboServicos;
    }

    public List<SelectItem> getListaComboDescricaoEvento() {
        if (listaComboDescricaoEvento.isEmpty()) {
            DescricaoEventoDao db = new DescricaoEventoDao();
            List<DescricaoEvento> list = db.pesquisaDescricaoPorGrupo(1);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    listaComboDescricaoEvento.add(new SelectItem(i, list.get(i).getDescricao(), Integer.toString(list.get(i).getId())));
                }
            }
        }
        return listaComboDescricaoEvento;
    }

    public void setListaComboDescricaoEvento(List<SelectItem> listaComboDescricaoEvento) {
        this.listaComboDescricaoEvento = listaComboDescricaoEvento;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public boolean isLimpar() {
        return limpar;
    }

    public void setLimpar(boolean limpar) {
        this.limpar = limpar;
    }

    public void setListaEventoBanda(List<EventoBanda> listaEventoBanda) {
        this.listaEventoBanda = listaEventoBanda;
    }

    public List<EventoBaile> getListaEventoBaile() {
        EventoBaileDao db = new EventoBaileDao();
        listaEventoBaile = db.pesquisaEventoDescricao(descPesquisa, comoPesquisa);
        return listaEventoBaile;
    }

    public void acaoPesquisaInicial() {
        comoPesquisa = "I";
    }

    public void acaoPesquisaParcial() {
        comoPesquisa = "P";
    }

    public void setListaEventoBaile(List<EventoBaile> listaEventoBaile) {
        this.listaEventoBaile = listaEventoBaile;
    }

    public List<EventoServicoValor> getListaEventoServicoValor() {
        if (listaEventoServicoValor.isEmpty()) {
            listaEventoServicoValor = new BaileDao().listaServicoValorPorEvento(evento.getId());
        }
        return listaEventoServicoValor;
    }

    public void setListaEventoServicoValor(List<EventoServicoValor> listaEventoServicoValor) {
        this.listaEventoServicoValor = listaEventoServicoValor;
    }

    public int getIdIndexBanda() {
        return idIndexBanda;
    }

    public void setIdIndexBanda(int idIndexBanda) {
        this.idIndexBanda = idIndexBanda;
    }

    public int getIdIndexServico() {
        return idIndexServico;
    }

    public void setIdIndexServico(int idIndexServico) {
        this.idIndexServico = idIndexServico;
    }

    public String getMesaTop() {
        return mesaTop;
    }

    public void setMesaTop(String mesaTop) {
        this.mesaTop = mesaTop;
    }

    public String getMesaLeft() {
        return mesaLeft;
    }

    public void setMesaLeft(String mesaLeft) {
        this.mesaLeft = mesaLeft;
    }

    public List<Integer> getListaQuantidade() {
        if (listaQuantidade.isEmpty()) {
            for (int i = 1; i < 426; i++) {
                listaQuantidade.add(i);
            }
        }
        return listaQuantidade;
    }

    public void setListaQuantidade(List<Integer> listaQuantidade) {
        this.listaQuantidade = listaQuantidade;
    }

    public int getNrMesa() {
        return nrMesa;
    }

    public void setNrMesa(int nrMesa) {
        this.nrMesa = nrMesa;
    }

    public EventoBaileMapa getEbmSelecionado() {
        return ebmSelecionado;
    }

    public void setEbmSelecionado(EventoBaileMapa ebmSelecionado) {
        this.ebmSelecionado = ebmSelecionado;
    }

    public List<SelectItem> getListaMesasDisponiveis() {
        listaMesasDisponiveis.clear();
        int qm = 0;
        if (eventoBaile.getQuantidadeMesas() > 0) {
            qm = eventoBaile.getQuantidadeMesas();
        }
        int j = 1;
        for (int i = 0; i < qm; i++) {
            listaMesasDisponiveis.add(new SelectItem(i, "Mesa " + j, "" + j));
            j++;
        }
        return listaMesasDisponiveis;
    }

    public void setListaMesasDisponiveis(List<SelectItem> listaMesasDisponiveis) {
        this.listaMesasDisponiveis = listaMesasDisponiveis;
    }

    public List<SelectItem> getListaConvitesDisponiveis() {
        listaConvitesDisponiveis.clear();
        int qm = 0;
        if (eventoBaile.getQuantidadeConvites() > 0) {
            qm = eventoBaile.getQuantidadeConvites();
        }
        int j = 1;
        for (int i = 0; i < qm; i++) {
            listaConvitesDisponiveis.add(new SelectItem(i, "Convite " + j, "" + j));
            j++;
        }
        return listaConvitesDisponiveis;
    }

    public void setListaConvitesDisponiveis(List<SelectItem> listaConvitesDisponiveis) {
        this.listaConvitesDisponiveis = listaConvitesDisponiveis;
    }

    public Integer getIdNrMesa() {
        return idNrMesa;
    }

    public void setIdNrMesa(Integer idNrMesa) {
        this.idNrMesa = idNrMesa;
    }

    public Integer getIdNrConvite() {
        return idNrConvite;
    }

    public void setIdNrConvite(Integer idNrConvite) {
        this.idNrConvite = idNrConvite;
    }

    public List<SelectItem> getListaCategoria() {
        return listaCategoria;
    }

    public void setListaCategoria(List<SelectItem> listaCategoria) {
        this.listaCategoria = listaCategoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public boolean isVisibleMapa() {
        return visibleMapa;
    }

    public void setVisibleMapa(boolean visibleMapa) {
        this.visibleMapa = visibleMapa;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

}
