package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.DescricaoEventoDao;
import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.CampeonatoDao;
import br.com.rtools.associativo.dao.EventoServicoDao;
import br.com.rtools.associativo.dao.EventoServicoValorDao;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicoRotinaDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
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
public class CampeonatoBean implements Serializable {

    private Campeonato campeonato;
    private List<SelectItem> listModalidades;
    private Integer idModalidade;
    private List<SelectItem> listDescricaoEvento;
    private Integer idDescricaoEvento;
    private List<Campeonato> listCampeonatos;
    private List<SelectItem> listGrupoEvento;
    private Integer idGrupoEvento;
    private EventoServico eventoServico;
    private EventoServicoValor eventoServicoValor;
    private List<SelectItem> listCategoria;
    private Integer idCategoria;
    private List<SelectItem> listServicos;
    private Integer idServico;
    private List<EventoServicoValor> listEventoServicoValor;
    private Servicos servicos;

    @PostConstruct
    public void init() {
        campeonato = new Campeonato();
        listDescricaoEvento = new ArrayList();
        listModalidades = new ArrayList();
        listCampeonatos = new ArrayList();
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        listGrupoEvento = new ArrayList();
        listDescricaoEvento = new ArrayList();
        listModalidades = new ArrayList();
        listCategoria = new ArrayList();
        listServicos = new ArrayList();
        listEventoServicoValor = new ArrayList();
        loadListModalidades();
        loadListGrupoEvento();
        loadListDescricaoEventos();
        loadListCampeonatos();
        loadListServicos();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("campeonatoBean");
    }

    public void loadListGrupoEvento() {
        listGrupoEvento = new ArrayList();
        List<GrupoEvento> list = new Dao().find("GrupoEvento", new int[]{4, 5});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idGrupoEvento = list.get(i).getId();
            }
            listGrupoEvento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListDescricaoEventos() {
        listDescricaoEvento = new ArrayList();
        List<DescricaoEvento> list = new DescricaoEventoDao().pesquisaDescricaoPorGrupo(idGrupoEvento);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDescricaoEvento = list.get(i).getId();
            }
            listDescricaoEvento.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListModalidades() {
        listModalidades = new ArrayList();
        List<CampeonatoModalidade> list = new Dao().list(new CampeonatoModalidade(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idModalidade = list.get(i).getId();
            }
            listModalidades.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListCampeonatos() {
        listCampeonatos = new ArrayList();
        listCampeonatos = (List<Campeonato>) new Dao().list(new Campeonato());
    }

    public void save() {
        if (campeonato.getTituloComplemento().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR COMPLEMENTO");
            return;
        }
        if (listDescricaoEvento.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR EVENTOS");
            return;
        }
        if (listModalidades.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR MODALIDADES");
            return;
        }

        Dao dao = new Dao();
        AEvento aEvento = new AEvento();
        dao.openTransaction();

        DescricaoEvento de = (DescricaoEvento) dao.find(new DescricaoEvento(), idDescricaoEvento);

        campeonato.setModalidade((CampeonatoModalidade) dao.find(new CampeonatoModalidade(), idModalidade));

        if (campeonato.getId() == null) {
            if (new CampeonatoDao().exists(de.getId(), idModalidade, campeonato.getTituloComplemento()) != null) {
                GenericaMensagem.warn("Validação", "CAMPEONATO JÁ CADASTRADO!");
                return;
            }
            Evt evt = new Evt();
            if (!dao.save(evt)) {
                GenericaMensagem.warn("Erro", "AO SALVAR EVT");
                dao.rollback();
                return;
            }
            campeonato.setEvt(evt);

            aEvento.setDescricaoEvento(de);

            if (!dao.save(aEvento)) {
                GenericaMensagem.warn("Erro", "AO SALVAR EVENTO!");
                dao.rollback();
                return;
            }

            campeonato.setEvento(aEvento);
            if (!dao.save(campeonato)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
            } else {
                GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
                dao.commit();
                loadListCampeonatos();
            }
        } else {
            if (campeonato.getEvt() == null) {
                Evt evt = new Evt();
                if (!dao.save(evt)) {
                    GenericaMensagem.warn("Erro", "AO SALVAR EVT!");
                    dao.rollback();
                    return;
                }
                campeonato.setEvt(evt);
            }
            aEvento = (AEvento) dao.find(new AEvento(), campeonato.getEvento().getId());
            aEvento.setDescricaoEvento(de);
            if (!dao.update(aEvento)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR EVENTO!");
                dao.rollback();
                return;
            }
            campeonato.setEvento(aEvento);
            List<ServicoPessoa> listServicoPessoa = new CampeonatoDao().findToUpdate(campeonato.getId());
            for (int i = 0; i < listServicoPessoa.size(); i++) {
                listServicoPessoa.get(i).setReferenciaValidade(DataHoje.converteDataParaReferencia(campeonato.getFim()));
                if (!dao.update(listServicoPessoa.get(i))) {
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO - SERVIÇO PESSOA!");
                    dao.rollback();
                    return;
                }
            }
            if (!dao.update(campeonato)) {
                GenericaMensagem.warn("Erro", "AO ATUALIZAR REGISTRO!");
                dao.rollback();
            } else {
                GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
                dao.commit();
                loadListCampeonatos();
            }
        }
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        if (campeonato.getId() != null) {
            campeonato = (Campeonato) dao.find(campeonato);
            AEvento aEvento = (AEvento) dao.find(campeonato.getEvento());

            if (!dao.delete(campeonato)) {
                GenericaMensagem.warn("Erro", "AO REMOVER REGISTRO");
                dao.rollback();
                return;
            }

            if (!dao.delete(aEvento)) {
                GenericaMensagem.warn("Erro", "AO REMOVER EVENTO!");
                dao.rollback();
            } else {
                dao.commit();
                loadListCampeonatos();
                campeonato = new Campeonato();
                GenericaMensagem.info("Sucesso", "REGISTRO REMOVIDO");
            }
        } else {
            GenericaMensagem.warn("Erro", "PESQUISE UM REGISTRO!");
            dao.rollback();
        }
    }

    public void clear() {
        GenericaSessao.remove("campeonatoBean");
    }

    public String edit(Campeonato c) {
        campeonato = (Campeonato) new Dao().rebind(c);
        idDescricaoEvento = campeonato.getEvento().getDescricaoEvento().getId();
        idModalidade = campeonato.getModalidade().getId();
        loadCategoria();
        listEventoServicoValor = new ArrayList();
        String url = (String) GenericaSessao.getString("urlRetorno");
        GenericaSessao.put("linkClicado", true);
        if (url != null) {
            GenericaSessao.put("campeonatoPesquisa", campeonato);
            return url;
        }
        return "campeonato";
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public List<SelectItem> getListModalidades() {
        return listModalidades;
    }

    public void setListModalidades(List<SelectItem> listModalidades) {
        this.listModalidades = listModalidades;
    }

    public Integer getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
    }

    public List<SelectItem> getListDescricaoEvento() {
        return listDescricaoEvento;
    }

    public void setListDescricaoEvento(List<SelectItem> listDescricaoEvento) {
        this.listDescricaoEvento = listDescricaoEvento;
    }

    public Integer getIdDescricaoEvento() {
        return idDescricaoEvento;
    }

    public void setIdDescricaoEvento(Integer idDescricaoEvento) {
        this.idDescricaoEvento = idDescricaoEvento;
    }

    public List<Campeonato> getListCampeonatos() {
        return listCampeonatos;
    }

    public void setListCampeonatos(List<Campeonato> listCampeonatos) {
        this.listCampeonatos = listCampeonatos;
    }

    public List<SelectItem> getListGrupoEvento() {
        return listGrupoEvento;
    }

    public void setListGrupoEvento(List<SelectItem> listGrupoEvento) {
        this.listGrupoEvento = listGrupoEvento;
    }

    public Integer getIdGrupoEvento() {
        return idGrupoEvento;
    }

    public void setIdGrupoEvento(Integer idGrupoEvento) {
        this.idGrupoEvento = idGrupoEvento;
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

    public void adicionarServicos() {
        if (campeonato.getEvento().getId() == null) {
            GenericaMensagem.warn("Erro", "Salve este Baile antes de Adicionar Serviços!");
            return;
        }

        if (eventoServicoValor.getId() == -1) {
            if (eventoServico.isSocio() && listCategoria.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Lista de Categoria Vazia!");
                return;
            }
            if (idServico == null || idServico == -1) {

                GenericaMensagem.warn("Erro", "Escolha um serviço válido!");
                return;
            }
        }

        if (eventoServicoValor.getValor() < 0) {
            GenericaMensagem.warn("Erro", "Informar o valor do serviço!");
            return;
        }

        Integer categoria_id = null;
        Integer servicos_id = null;

        if (eventoServicoValor.getId() == -1) {
            categoria_id = idCategoria;
            servicos_id = idServico;
        } else {
            if (eventoServicoValor.getEventoServico().getCategoria() != null) {
                categoria_id = eventoServicoValor.getEventoServico().getCategoria().getId();
            }
            servicos_id = eventoServicoValor.getEventoServico().getServicos().getId();
        }
        if (eventoServicoValor.getId() == -1) {
            if (new EventoServicoDao().pesquisaEventoServico(servicos_id,
                    eventoServico.isSocio() ? categoria_id : null,
                    campeonato.getEvento().getId(),
                    eventoServico.getResponsavel()
            ) != null) {
                GenericaMensagem.warn("Atenção", "Evento Serviço já adicionado!");
                return;
            }
        }
        Dao dao = new Dao();

        if (eventoServicoValor.getId() == -1) {
            eventoServico.setServicos(((Servicos) dao.find(new Servicos(), idServico)));
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
            eventoServico.setEvento(campeonato.getEvento());

            if (!dao.save(eventoServico)) {
                GenericaMensagem.warn("Erro", "Serviço não pode ser adicionado!");
                dao.rollback();
            }

            eventoServicoValor.setEventoServico(eventoServico);
            if (!dao.save(eventoServicoValor)) {
                GenericaMensagem.warn("Erro", "Serviço Valor não pode ser adicionado!");
                dao.rollback();
            }

            GenericaMensagem.info("Sucesso", "Serviço Adicionado");
            listEventoServicoValor = new ArrayList<>();

            dao.commit();
        } else {
            dao.openTransaction();
            if (!dao.update(eventoServico)) {
                GenericaMensagem.warn("Erro", "Serviço não pode ser atualizado!");
                dao.rollback();
            }
            if (!dao.update(eventoServicoValor)) {
                GenericaMensagem.warn("Erro", "Serviço Valor não pode ser atualizado!");
                dao.rollback();
            }
            listEventoServicoValor = new ArrayList();
            GenericaMensagem.info("Sucesso", "Serviço atualizado!");
            dao.commit();

        }
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        loadListServicos();
    }

    public void adicionarTodosServicos() {
        if (campeonato.getEvento().getId() == null) {
            GenericaMensagem.warn("Erro", "Salve este Baile antes de Adicionar Serviços!");
            return;
        }

        if (eventoServicoValor.getId() == -1) {
            if (eventoServico.isSocio() && listCategoria.isEmpty()) {
                GenericaMensagem.warn("Atenção", "Lista de Categoria Vazia!");
                return;
            }
            if (idServico == null || idServico == -1) {
                GenericaMensagem.warn("Erro", "Escolha um serviço válido!");
                return;
            }
        }

        if (eventoServicoValor.getValor() < 0) {
            GenericaMensagem.warn("Erro", "Informar o valor do serviço!");
            return;
        }

        EventoServicoDao esd = new EventoServicoDao();

        Integer categoria_id = null;
        Integer servicos_id = null;

        Dao dao = new Dao();
        if (eventoServicoValor.getId() == -1) {
            eventoServico.setEvento(campeonato.getEvento());
            servicos_id = idServico;
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
            for (int i = 0; i < listCategoria.size(); i++) {
                categoria_id = Integer.parseInt(listCategoria.get(i).getValue().toString());
                if (esd.pesquisaEventoServico(
                        servicos_id,
                        categoria_id,
                        campeonato.getEvento().getId()
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
                es.setEvento(campeonato.getEvento());
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
        loadListServicos();
        listEventoServicoValor = new ArrayList();
    }

    public void editEventoServicoValor(EventoServicoValor esv) {
        eventoServicoValor = (EventoServicoValor) new Dao().rebind(esv);
        eventoServico = eventoServicoValor.getEventoServico();
        loadListServicos();
        idServico = eventoServicoValor.getEventoServico().getServicos().getId();
        servicos = eventoServicoValor.getEventoServico().getServicos();
    }

    public String removerEventoServico(EventoServicoValor esv) {
        Dao dao = new Dao();
        dao.openTransaction();
        if (excluirEventoServico(esv.getId(), dao)) {
            listEventoServicoValor = new ArrayList();
            eventoServicoValor = new EventoServicoValor();
            eventoServico = new EventoServico();
            GenericaMensagem.info("Sucesso", "Serviço removido com sucesso! Editar valores");
            dao.commit();

            loadListServicos();
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

    public void updateTela() {
        loadListServicos();
        updateServicoCategoria();
    }

    public void updateServicoCategoria() {
        servicos = (Servicos) new Dao().find(new Servicos(), idServico);
        if (servicos.getId() == 13 || servicos.getId() == 15) {
            eventoServicoValor.setValor(0);
            eventoServicoValor.setIdadeInicial(0);
            eventoServicoValor.setIdadeFinal(150);
            eventoServicoValor.setSexo("A");
            eventoServico.setSocio(false);
        }
        loadCategoria();
    }

    public void loadListServicos() {
        listServicos = new ArrayList();
        List<Servicos> list = new ServicoRotinaDao().pesquisaTodosServicosComRotinas(new Rotina().get().getId());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idServico = list.get(i).getId();
                servicos = list.get(i);
            }
            listServicos.add(
                    new SelectItem(
                            list.get(i).getId(),
                            list.get(i).getDescricao(),
                            list.get(i).getDescricao()
                    )
            );
        }
        loadCategoria();
    }

    public void loadCategoria() {
        listCategoria = new ArrayList();
        List<Categoria> list;
        if (campeonato.getId() == null) {
            list = new Dao().list(new Categoria());
        } else {
            Integer servico_id = null;
            if (eventoServico.getId() == -1) {
                servico_id = idServico;
            } else {
                servico_id = eventoServico.getServicos().getId();
            }
            list = new EventoServicoDao().listaCategoriaPorEventoServico(
                    servico_id,
                    eventoServicoValor.getSexo(),
                    eventoServicoValor.getIdadeInicial(),
                    eventoServicoValor.getIdadeFinal(),
                    campeonato.getEvento().getId()
            );
        }

        for (int i = 0; i < list.size(); i++) {
            listCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getCategoria(), "" + list.get(i).getId()));
        }
    }

    public List<SelectItem> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<SelectItem> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public List<EventoServicoValor> getListEventoServicoValor() {
        if (listEventoServicoValor.isEmpty()) {
            if (campeonato.getId() != null) {
                listEventoServicoValor = new EventoServicoValorDao().listaServicoValorPorEvento(campeonato.getEvento().getId());
            }
        }
        return listEventoServicoValor;
    }

    public void setListEventoServicoValor(List<EventoServicoValor> listEventoServicoValor) {
        this.listEventoServicoValor = listEventoServicoValor;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public void newServico() {
        eventoServico = new EventoServico();
        eventoServicoValor = new EventoServicoValor();
        loadListServicos();

    }

}
