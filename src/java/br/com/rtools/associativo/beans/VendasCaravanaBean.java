package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.CaravanaVenda;
import br.com.rtools.associativo.Caravana;
import br.com.rtools.associativo.EventoServico;
import br.com.rtools.associativo.EventoServicoValor;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.CaravanaReservas;
import br.com.rtools.associativo.dao.CaravanaVendaDao;
import br.com.rtools.associativo.dao.CaravanaDao;
import br.com.rtools.associativo.dao.EventoServicoDao;
import br.com.rtools.associativo.dao.EventoServicoValorDao;
import br.com.rtools.associativo.dao.CaravanaReservasDao;
import br.com.rtools.associativo.dao.PoltronasDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.VendasCaravanaDao;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.MovimentoInativo;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.impressao.ParametroFichaReserva;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.beans.FisicaBean;
import br.com.rtools.pessoa.beans.JuridicaBean;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.sistema.BloqueioRotina;
import br.com.rtools.sistema.dao.BloqueioRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.SelectItemSort;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class VendasCaravanaBean implements Serializable {

    private Caravana caravana;
    private CaravanaVenda vendas;
    private CaravanaReservas reservas;
    private EventoServicoValor eventoServicoValor;
    private EventoServico eventoServico;
    private Fisica pessoaFisica;
    private Juridica pessoaJuridica;
    private PessoaEndereco pessoaEndereco;
    private List<Caravana> listaCaravana;
    private List<SelectItem> listaCaravanaSelect;
    private List<SelectItem> listaPoltrona;
    private List<SelectItem> listaDataEntrada;
    private List<SelectItem> listaMesVencimento;
    private List<Parcelas> listaParcelas;
    private List<ListaReservas> listaReservas;
    private List<SelectItem> listaTipo;
    private List<SelectItem> listaDataVencimento;
    private Integer idCaravanaSelect;
    private Integer idTipo;
    private Integer idAdicionar;
    private Integer idDataEntrada;
    private Integer idMesVencimento;
    private Integer idDiaVencimento;
    private Integer parcelas;
    private String dataEntrada;
    private Pessoa pessoa;
    private String valorTotal;
    private String valorPago;
    private String valorOutras;
    private String valorEntrada;
    private Registro registro;
    // private List<CaravanaVenda> listCVenda;
    private String type;
    private String by;
    private String as;
    private String description;
    private List<CaravanaReservas> listReservas;
    private List<CaravanaReservas> listReservasCanceladas;
    private List<CaravanaVenda> listVenda;
    private Boolean disabledSave;
    private Boolean locked;
    private Boolean old;
    private String motivoCancelamento;
    private Boolean novoParcelamento;
    private List<Movimento> listMovimento;
    private Boolean disabledGerarParcelas;
    private List<Integer> listPoltronasReservadas;
    private Boolean canceled;
    private BloqueioRotina bloqueioRotina;

    @PostConstruct
    public void init() {
        caravana = new Caravana();
        vendas = new CaravanaVenda();
        eventoServicoValor = new EventoServicoValor();
        eventoServico = new EventoServico();
        pessoaFisica = new Fisica();
        pessoaJuridica = new Juridica();
        pessoaEndereco = new PessoaEndereco();
        listaCaravana = new ArrayList();
        listaCaravanaSelect = new ArrayList();
        listaPoltrona = new ArrayList();
        listaDataEntrada = new ArrayList();
        listaMesVencimento = new ArrayList();
        listaParcelas = new ArrayList();
        listaReservas = new ArrayList();
        listaTipo = new ArrayList();
        listaDataVencimento = new ArrayList();
        idCaravanaSelect = 0;
        idTipo = 0;
        idAdicionar = -1;
        idDataEntrada = 0;
        idMesVencimento = 0;
        idDiaVencimento = 0;
        parcelas = 1;
        dataEntrada = DataHoje.data();
        pessoa = new Pessoa();
        valorTotal = "0,00";
        valorPago = "0,00";
        valorOutras = "0,00";
        valorEntrada = "0,00";
        registro = new Registro();
        // listCVenda = new ArrayList();
        disabledSave = false;
        type = "caravana";
        by = "I";
        as = "nome";
        description = "";
        listMovimento = new ArrayList();
        listReservas = new ArrayList();
        listVenda = new ArrayList();
        listPoltronasReservadas = new ArrayList();
        locked = false;
        old = false;
        loadListaCaravanaSelect();
        motivoCancelamento = "";
        novoParcelamento = false;
        disabledGerarParcelas = false;
        canceled = false;
        new BloqueioRotinaDao().liberaRotinaBloqueada(new Rotina().get().getId());
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("vendasCaravanaBean");
        GenericaSessao.remove("fisicaPesquisa");
        GenericaSessao.remove("pessoaPesquisa");
        GenericaSessao.remove("pesquisaFisicaTipo");
    }

    public void imprimirFichaReserva() {
        List<ParametroFichaReserva> l = new ArrayList();
        DataHoje dh = new DataHoje();
        PessoaEndereco pe = vendas.getResponsavel().getPessoaEndereco();

        String empresa_nome = "", empresa_cnpj = "";
        if (vendas.getResponsavel().getFisica().getId() != -1) {
            PessoaEmpresa pem = vendas.getResponsavel().getFisica().getPessoaEmpresa();

            if (pem != null) {
                empresa_nome = pem.getJuridica().getPessoa().getNome();
                empresa_cnpj = pem.getJuridica().getPessoa().getDocumento();
            }
        }
        Object tipo[] = new Object[6];
        Object quantidade[] = new Object[6];
        Object valor[] = new Object[6];
        Float total = (float) 0;
        VendasCaravanaDao db = new VendasCaravanaDao();

        List<Object> result = db.listaTipoAgrupado(vendas.getId());

        for (int i = 0; i < result.size(); i++) {
            List linha = ((List) result.get(i));
            tipo[i] = linha.get(0);
            quantidade[i] = linha.get(1);
            //valor[i] = linha.get(2);
            float desconto = 0;
            for (ListaReservas lr : listaReservas) {
                CaravanaReservas r = (CaravanaReservas) lr.getReservas();
                if (linha.get(0).equals(r.getEventoServico().getDescricao())) {
                    desconto += lr.getReservas().getDesconto();
                }
            }
            for (ListaReservas lr : listaReservas) {
                CaravanaReservas r = (CaravanaReservas) lr.getReservas();
                if (linha.get(0).equals(r.getEventoServico().getDescricao())) {
                    if ((Float) valor[i] == null) {
                        valor[i] = (Moeda.converteUS$(lr.getValor()) * Integer.parseInt(linha.get(1).toString())) - desconto;
                        total = total + (Float) valor[i];
                    }
                }
            }
        }
        String financeiroString = "P | Vencimento | Valor (R$) \n";
        for (int i = 0; i < listaParcelas.size(); i++) {
            financeiroString += (i + 1) + " | " + listaParcelas.get(i).getVencimento() + " | " + listaParcelas.get(i).getValor() + "\n";
        }

        for (ListaReservas lr : listaReservas) {
            Fisica f = (Fisica) lr.getFisica();
            l.add(
                    new ParametroFichaReserva(
                            vendas.getResponsavel().getNome(),
                            pe.getEnderecoCompletoString(),
                            empresa_nome,
                            empresa_cnpj,
                            f.getPessoa().getNome(),
                            f.getPessoa().getDocumento().equals("0") ? "" : f.getPessoa().getDocumento(),
                            f.getPessoa().getSocios().getMatriculaSocios().getCategoria().getCategoria(),
                            f.getSexo(),
                            dh.calcularIdade(f.getNascimento()),
                            f.getNascimento(),
                            vendas.getObservacao(),
                            caravana.getHoraSaida(),
                            caravana.getHoraRetorno(),
                            "De " + caravana.getDataSaida() + " à " + caravana.getDataRetorno(),
                            DataHoje.calculoDosDias(caravana.getDtSaida(), caravana.getDtRetorno()),
                            vendas.getEvento().getDescricaoEvento().getDescricao(),
                            DataHoje.dataExtenso(vendas.getEmissao(), 3), // NÃO TEM EM vendas.getData
                            DataHoje.dataExtenso(DataHoje.data(), 3),
                            tipo[0],
                            tipo[1],
                            tipo[2],
                            tipo[3],
                            tipo[4],
                            tipo[5],
                            quantidade[0],
                            quantidade[1],
                            quantidade[2],
                            quantidade[3],
                            quantidade[4],
                            quantidade[5],
                            valor[0],
                            valor[1],
                            valor[2],
                            valor[3],
                            valor[4],
                            valor[5],
                            total,
                            financeiroString,
                            listaParcelas
                    )
            );
        }

        Jasper.IS_HEADER = true;
        Jasper.IS_HEADER_PARAMS = true;
        Jasper.FILIAL = (Filial) new Dao().find(new Filial(), 1);
        Jasper.printReports("FICHA_RESERVA.jasper", "Ficha de Reserva", l);
    }

    public void cancel() {
        if (isBlock()) {
            GenericaMensagem.warn("Sistema", "CARAVANA BLOQUEADA PARA MANUTENÇÃO DAS POLTRONAS POR! SOLICITE A LIBERAÇÃO!");
            return;
        }
        if (vendas.getId() == -1) {
            GenericaMensagem.warn("Erro", "Pesquise uma venda para ser cancelada!");
            return;
        }
        if (vendas.getMotivoCancelamento().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR UM MOTIVO PARA O CANCELAMENTO DESTA VENDA!");
            return;
        }
        if (vendas.getMotivoCancelamento().length() < 5) {
            GenericaMensagem.warn("Validação", "INFORMAR UM MOTIVO VÁLIDO!");
            return;
        }
        vendas.setOperadorCancelamento(Usuario.getUsuario());
        vendas.setDtCancelamento(new Date());
        List<CaravanaReservas> lr;
        Dao dao = new Dao();
        CaravanaReservas res;
        dao.openTransaction();

        lr = new CaravanaReservasDao().listaReservasVenda(vendas.getId());
        Usuario usuario = Usuario.getUsuario();
        for (CaravanaReservas lr1 : lr) {
            res = (CaravanaReservas) dao.find(lr1);
            if (res.getCancelamento() != null) {
                res.setOperadorCancelamento(usuario);
                res.setDtCancelamento(new Date());
                res.setMotivoCancelamento(vendas.getMotivoCancelamento());
                if (!dao.update(res)) {
                    GenericaMensagem.warn("Erro", "AO CANCELAR RESERVAS!");
                    dao.rollback();
                    return;
                }
            }
        }
        MovimentoDao md = new MovimentoDao();
        List<Movimento> listaMovimento = md.findByLote(vendas.getLote().getId());
        if (!listaMovimento.isEmpty()) {
            for (Movimento listaMovimento1 : listaMovimento) {
                if (listaMovimento1.getBaixa() == null) {
                    listaMovimento1.setAtivo(false);
                    if (!dao.update(listaMovimento1)) {
                        GenericaMensagem.warn("Erro", "AO INATIVAR MOVIMENTO!");
                        dao.rollback();
                        return;
                    }
                    MovimentoInativo movimentoInativo = new MovimentoInativo();
                    movimentoInativo.setMovimento(listaMovimento1);
                    movimentoInativo.setDtData(new Date());
                    movimentoInativo.setUsuario(usuario);
                    movimentoInativo.setHistorico("CANCELAMENTO DE COMPRA DE RESERVAS PARA CARAVANA! ID: (" + (caravana.getEvento().getId()) + ") " + caravana.getEvento().getDescricaoEvento().getDescricao() + " - DATA SAÍDA: " + caravana.getDataSaida());
                    if (!dao.save(movimentoInativo)) {
                        GenericaMensagem.warn("Erro", "AO INATIVAR MOVIMENTO!");
                        dao.rollback();
                        return;
                    }
                }
            }
            Movimento mov;
            Lote lot = new Lote();
            Evt evt = new Evt();
            List<Lote> listLote = new ArrayList();
            List<Evt> listEvt = new ArrayList();
//            for (Movimento listaMovimento1 : listaMovimento) {
//                mov = (Movimento) dao.find(listaMovimento1);
//                if (!dao.delete(mov)) {
//                    GenericaMensagem.warn("Erro", "Erro ao excluir movimentos!");
//                    dao.rollback();
//                    return;
//                }
//                if (mov.getLote().getId() != lot.getId()) {
//                    lot = mov.getLote();
//                    listLote.add(lot);
//                }
//            }
//            for (int i = 0; i < listLote.size(); i++) {
//                if (!dao.delete(listLote.get(i))) {
//                    GenericaMensagem.warn("Erro", "Erro ao excluir Lote!");
//                    dao.rollback();
//                    return;
//                }
//                if (listLote.get(i).getEvt().getId() != evt.getId()) {
//                    evt = listLote.get(i).getEvt();
//                    listEvt.add(evt);
//                }
//            }
//            if (!dao.delete(vendas)) {
//                GenericaMensagem.warn("Erro", "Erro ao cancelar Venda!");
//                dao.rollback();
//                return;
//            }
//            for (int i = 0; i < listEvt.size(); i++) {
//                if (!dao.delete(listEvt.get(i))) {
//                    GenericaMensagem.warn("Erro", "Erro ao excluir EVT!");
//                    dao.rollback();
//                    return;
//                }
//            }
            vendas.setOperadorCancelamento(usuario);
            vendas.setDtCancelamento(new Date());
            if (!dao.update(vendas)) {
                GenericaMensagem.warn("Erro", "Erro ao cancelar Venda!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "RESERVA CANCELADA COM SUCESSO!");
            dao.commit();
            GenericaSessao.remove("vendasCaravanaBean");
        } else {
            if (!dao.delete(vendas)) {
                GenericaMensagem.warn("Erro", "Erro ao cancelar Venda!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "Reserva cancelada com sucesso!");
            dao.commit();
            GenericaSessao.remove("vendasCaravanaBean");
        }
    }

    public void loadListReservas() {
        listaReservas = new ArrayList();
        float valor = 0;
        List<CaravanaReservas> lr = new CaravanaReservasDao().findByCaravanaVenda(vendas.getId(), true);
        FisicaDao dbf = new FisicaDao();
        EventoServicoValorDao dbe = new EventoServicoValorDao();
        SociosDao dbs = new SociosDao();
        for (CaravanaReservas lr1 : lr) {
            valor = dbs.descontoSocioEve(lr1.getPessoa().getId(), lr1.getEventoServico().getServicos().getId());
            if (valor == 0) {
                valor = dbe.pesquisaEventoServicoValor(lr1.getEventoServico().getId()).getValor();
                listaReservas.add(new ListaReservas(dbf.pesquisaFisicaPorPessoa(lr1.getPessoa().getId()), lr1.getPoltrona(), Moeda.converteR$Float(valor), Moeda.converteR$Float(lr1.getDesconto()), lr1, null, new ArrayList()));
            } else {
                listaReservas.add(new ListaReservas(dbf.pesquisaFisicaPorPessoa(lr1.getPessoa().getId()), lr1.getPoltrona(), Moeda.converteR$Float(valor), Moeda.converteR$Float(lr1.getDesconto()), lr1, null, new ArrayList()));
            }
        }
    }

    public void loadListReservasCanceladas() {
        listReservasCanceladas = new ArrayList();
        listReservasCanceladas = new CaravanaReservasDao().findByCaravanaVenda(vendas.getId(), false);
    }

    public void loadPoltronasReservas() {
        listPoltronasReservadas = new ArrayList();
        int pos = -1;
        for (int i = 0; i < listaReservas.size(); i++) {
            listPoltronasReservadas.add(listaReservas.get(i).getPoltrona());
            if (listaReservas.get(i).getFisica().getId() == -1) {
                listaReservas.get(i).getListPoltrona().clear();
                pos = i;
            }
        }
        if (pos != -1) {
            listaReservas.get(pos).getListPoltrona();
            for (int i = 0; i < listaReservas.get(pos).getListPoltrona().size(); i++) {
                for (int j = 0; j < listPoltronasReservadas.size(); j++) {
                    if (listaReservas.get(pos).getListPoltrona().get(i).getValue().equals(listPoltronasReservadas.get(j))) {
                        listaReservas.get(pos).getListPoltrona().remove(i);
                    }
                }
            }
        }
    }

    public void editReserva(CaravanaReservas cr) {
        reservas = new CaravanaReservas();
        reservas = cr;
    }

    public void updateReserva() {
        new Dao().update(reservas, true);

    }

    public void cancelReserva() {
        int countReservas = 0;
        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getReservas().getId() != null && listaReservas.get(i).getReservas().getDtCancelamento() == null) {
                countReservas++;
            }
        }
        if (countReservas < 2) {
            GenericaMensagem.warn("Validação", "É NECESSÁRIO NO MÍNIMO DUAS RESERVAS CONCLUÍDAS PARA REALIZAR O CANCELAMENTO INDIVIDUAL!");
            GenericaMensagem.warn("Sistema", "FAÇA O CANCELAMENTO DA VENDA PARA CANCELAR UMA ÚNICA RESERVA!");
            return;

        }
        if (vendas.getId() == -1) {
            GenericaMensagem.warn("Erro", "Pesquise uma venda para ser cancelada!");
            return;
        }
        if (motivoCancelamento.isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR UM MOTIVO PARA O CANCELAMENTO DESTA VENDA!");
            return;
        }
        if (motivoCancelamento.length() < 5) {
            GenericaMensagem.warn("Validação", "INFORMAR UM MOTIVO VÁLIDO!");
            return;
        }
        reservas.setOperadorCancelamento(Usuario.getUsuario());
        reservas.setDtCancelamento(new Date());
        reservas.setMotivoCancelamento(motivoCancelamento);
        GenericaMensagem.warn("IMPORTANTE", "PARA FINALIZAR O CANCELAMENTO DESTA RESERVA CONCLUA O PROCESSO");
        //loadListReservasCanceladas();
        reservas = null;
        disabledGerarParcelas = true;
    }

    public void load() {
        if (GenericaSessao.exists("baixa_sucesso", true)) {
            loadListMovimento();
            loadListParcelas();

        }
    }

    public void loadListMovimento() {
        listMovimento = new ArrayList();
        if (vendas.getLote() != null) {
            listMovimento = new MovimentoDao().findByLote(vendas.getLote().getId());
        }
    }

    public void loadListParcelas() {
        listaParcelas = new ArrayList();
        for (Movimento listaMovimento1 : listMovimento) {
            if (listaMovimento1.getBaixa() == null) {
                listaParcelas.add(new Parcelas(listaMovimento1.getVencimento(), Moeda.converteR$Float(listaMovimento1.getValor()), false, listaMovimento1, listaMovimento1.isAtivo()));
            } else {
                listaParcelas.add(new Parcelas(listaMovimento1.getVencimento(), Moeda.converteR$Float(listaMovimento1.getValor()), true, listaMovimento1, listaMovimento1.isAtivo()));
            }
        }
    }

    public String edit(CaravanaVenda v) {
        listaCaravanaSelect = new ArrayList();
        loadListaCaravanaSelect();
        locked = true;
        vendas = (CaravanaVenda) new Dao().rebind(v);
        loadListReservas();
        loadListReservasCanceladas();
        if (vendas.getCaravana() == null) {
            CaravanaDao dbc = new CaravanaDao();
            caravana = dbc.pesquisaCaravanaPorEvento(vendas.getEvento().getId());
        } else {
            caravana = vendas.getCaravana();
        }
        if (listaCaravanaSelect.isEmpty()) {
            idCaravanaSelect = caravana.getId();
            listaCaravanaSelect.add(new SelectItem(caravana.getId(), caravana.getDataSaida() + " - " + caravana.getHoraSaida() + " - " + caravana.getEvento().getDescricaoEvento().getDescricao(), "0"));
            listaCaravana.add(caravana);
        }
        idCaravanaSelect = vendas.getCaravana().getId();
        bloqueioRotina = new BloqueioRotinaDao().existRotinaCodigo(142, vendas.getCaravana().getId());
        loadListMovimento();
        loadListParcelas();
        pessoa = vendas.getResponsavel();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        int countBaixa = 0;
        if (!listMovimento.isEmpty()) {
            for (int i = 0; i < listMovimento.size(); i++) {
                if (listMovimento.get(i).getBaixa() != null) {
                    countBaixa++;
                }
            }
            countBaixa++;
            parcelas = countBaixa;
        }
        return "vendasCaravana";
    }

    public void atualizaValoresParcela(int index) {
        String s = getCalculaValorMovimentoAlterado();
        if (!s.isEmpty()) {
            GenericaMensagem.fatal("Validação", s);
        }

    }

    public void atualizaValoresGrid(int index) {
        listaReservas.get(index).setValor(Moeda.converteR$(listaReservas.get(index).getValor()));
        if (Moeda.converteUS$(listaReservas.get(index).getDesconto()) > Moeda.converteUS$(listaReservas.get(index).getValor())) {
            listaReservas.get(index).setDesconto(Moeda.converteR$(listaReservas.get(index).getValor()));
        } else {
            listaReservas.get(index).setDesconto(Moeda.converteR$(listaReservas.get(index).getDesconto()));
        }
    }

    public String cadastroFisica() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fisicaBean", new FisicaBean());
        ((FisicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fisicaBean")).editarFisicaParametro(pessoaFisica);

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).pessoaFisicaComParametros();
    }

    public String cadastroJuridica() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("juridicaBean", new JuridicaBean());
        ((JuridicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("juridicaBean")).editar(pessoaJuridica);

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).pessoaJuridicaComParametros();
    }

    public void save() {
        if (isBlock()) {
            GenericaMensagem.warn("Sistema", "CARAVANA BLOQUEADA PARA MANUTENÇÃO DAS POLTRONAS POR! SOLICITE A LIBERAÇÃO!");
            return;
        }
        Boolean save = true;
        if (vendas.getId() != null) {
            save = false;
        }
        if (canceled) {
            canceled = false;
        }
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquise um responsável!");
            return;
        }

        if (listaReservas.isEmpty()) {
            GenericaMensagem.warn("Validação", "Não é possivel concluir nenhuma Reserva!");
            return;
        }

        if (save) {
            CaravanaVendaDao cVendaDao = new CaravanaVendaDao();
            if (cVendaDao.findByResponsavel(caravana.getId(), vendas.getResponsavel().getId()) != null) {
                GenericaMensagem.warn("Validação", "RESPONSÁVEL JÁ CADASTRADO!");
                return;
            }
        }

        if (listaParcelas.isEmpty()) {
            GenericaMensagem.warn("Validação", "Não é possivel concluir sem parcelas!");
            return;
        }
        float soma = 0;

        for (Parcelas listaParcela : listaParcelas) {
            soma = Moeda.somaValores(soma, Moeda.converteUS$(String.valueOf(listaParcela.getValor())));
        }

        if (soma < Moeda.converteUS$(valorTotal)) {
            GenericaMensagem.warn("Validação", "Valor das parcelas é MENOR que o valor total!");
            return;
        }

        if (soma > Moeda.converteUS$(valorTotal)) {
            GenericaMensagem.warn("Validação", "Valor das parcelas é MAIOR que o valor total!");
            return;
        }

        Dao dao = new Dao();
        dao.openTransaction();
        Usuario usuario = Usuario.getUsuario();
        if (vendas.getId() == null) {
            vendas.setOperador(usuario);
            vendas.setCaravana(caravana);
            if (!dao.save(vendas)) {
                GenericaMensagem.warn("Erro", "Não é possivel salvar venda!");
                dao.rollback();
                return;
            }
        } else if (!dao.update(vendas)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Erro ao atualizar esta venda!");
            return;
        }

        CaravanaReservasDao crd = new CaravanaReservasDao();
        for (ListaReservas lr : listaReservas) {
            if (lr.getReservas().getId() == null) {
                if (!crd.findPoltronaDisponivel(vendas.getCaravana().getId(), lr.getPoltrona()).isEmpty()) {
                    GenericaMensagem.warn("Validação", "Esta poltrona já foi reservada! Poltrona nº" + lr.getPoltrona());
                    dao.rollback();
                    return;
                }
                CaravanaReservas res = new CaravanaReservas();
                res.setVenda(vendas);
                res.setPessoa(lr.getFisica().getPessoa());
                res.setPoltrona(lr.getPoltrona());
                res.setDesconto(Moeda.converteUS$(lr.getDesconto()));
                res.setEventoServico(((CaravanaReservas) lr.getReservas()).getEventoServico());
                res.setOperador(Usuario.getUsuario());
                res.setDtReserva(new Date());
                if (!dao.save(res)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Não é possivel salvar venda!");
                    return;
                }
                lr.setReservas(res);
            } else {
                lr.getReservas().setPoltrona(lr.getPoltrona());
                if (!dao.update(lr.getReservas())) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Não é possivel salvar venda!");
                    return;
                }
            }
        }

        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getReservas().getDtCancelamento() != null) {
                listaReservas.remove(i);
            }
        }

        CondicaoPagamento condicaoPagamento;
        if (listaParcelas.size() == 1 && dataEntrada.equals(DataHoje.data())) {
            condicaoPagamento = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1);
        } else {
            condicaoPagamento = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 2);
        }
        Lote lote;
        if (vendas.getLote() == null) {
            lote = new Lote(
                    -1,
                    (Rotina) dao.find(new Rotina(), 142),
                    "R",
                    DataHoje.data(),
                    pessoa,
                    eventoServico.getServicos().getPlano5(),
                    false,
                    "",
                    Moeda.converteUS$(valorTotal),
                    (Filial) dao.find(new Filial(), 1),
                    (Departamento) dao.find(new Departamento(), 6),
                    caravana.getEvt(),
                    "",
                    (FTipoDocumento) dao.find(new FTipoDocumento(), 13),
                    condicaoPagamento,
                    (FStatus) dao.find(new FStatus(), 1),
                    null,
                    false,
                    0,
                    null,
                    null,
                    null,
                    false,
                    ""
            );
            if (!dao.save(lote)) {
                GenericaMensagem.warn("Erro", "Não foi possível salvar Lote!");
                dao.rollback();
                return;
            }
            vendas.setLote(lote);
        } else {
            lote = vendas.getLote();
        }
        if (novoParcelamento) {
            MovimentoDao md = new MovimentoDao();
            List<Movimento> listaMovimento = md.findByLote(vendas.getLote().getId());
            if (!listaMovimento.isEmpty()) {
                for (Movimento listaMovimento1 : listaMovimento) {
                    if (listaMovimento1.getBaixa() == null) {
                        listaMovimento1.setAtivo(false);
                        if (!dao.update(listaMovimento1)) {
                            GenericaMensagem.warn("Erro", "AO INATIVAR MOVIMENTO!");
                            dao.rollback();
                            return;
                        }
                        MovimentoInativo movimentoInativo = new MovimentoInativo();
                        movimentoInativo.setMovimento(listaMovimento1);
                        movimentoInativo.setDtData(new Date());
                        movimentoInativo.setUsuario(usuario);
                        movimentoInativo.setHistorico("CANCELAMENTO DE COMPRA DE RESERVAS PARA CARAVANA! ID: (" + (caravana.getEvento().getId()) + ") " + caravana.getEvento().getDescricaoEvento().getDescricao() + " - DATA SAÍDA: " + caravana.getDataSaida());
                        if (!dao.save(movimentoInativo)) {
                            GenericaMensagem.warn("Erro", "AO INATIVAR MOVIMENTO!");
                            dao.rollback();
                            return;
                        }
                    }
                }
                novoParcelamento = false;
            }
        }
        Movimento movimento;
        for (int i = 0; i < listaParcelas.size(); i++) {
            if (listaParcelas.get(i).getMovimento().getId() == -1) {
                movimento = new Movimento(
                        -1,
                        lote,
                        eventoServico.getServicos().getPlano5(), //esv.getEventoServico().getServicos().getPlano5(),
                        pessoa,
                        eventoServico.getServicos(), //esv.getEventoServico().getServicos(),
                        null,
                        (TipoServico) dao.find(new TipoServico(), 1),
                        null,
                        Moeda.converteUS$(listaParcelas.get(i).getValor()),
                        DataHoje.dataReferencia(listaParcelas.get(i).getVencimento()),
                        listaParcelas.get(i).getVencimento(),
                        parcelas,
                        true,
                        "E",
                        false,
                        pessoa,
                        pessoa,
                        "",
                        "",
                        "",
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        null,
                        0,
                        new MatriculaSocios()
                );

                if (!dao.save(movimento)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Não é possivel salvar movimento!");
                    return;
                }
            } else {
                listaParcelas.get(i).getMovimento().setVencimento(listaParcelas.get(i).getVencimento());
                listaParcelas.get(i).getMovimento().setValorString(listaParcelas.get(i).getValor());
                if (!dao.update(listaParcelas.get(i).getMovimento())) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Não é possivel atualizar movimento!");
                    return;
                }
            }
        }
        NovoLog novoLog = new NovoLog();
        // novoLog.save("ID: " + vendas.getId() + " - Responsável: " + vendas.getResponsavel().getNome() + " - Evento: (" + vendas.getEvento().getId() + ") - " + vendas.getEvento().getDescricaoEvento().getDescricao() + " - Quarto: " + vendas.getQuarto() + " - Serviço : (" + eventoServico.getServicos().getId() + ") " + eventoServico.getServicos().getDescricao());
        dao.commit();
        loadListMovimento();
        loadListParcelas();
        loadListReservasCanceladas();
        if (lote != null) {
            float vlTotal = 0;
            for (int i = 0; i < listMovimento.size(); i++) {
                vlTotal += listMovimento.get(i).getValor();
            }
            lote.setValor(vlTotal);
            dao.update(lote, true);
            vendas.setLote(lote);
            dao.update(vendas, true);
        }
        listaPoltrona = new ArrayList();
        locked = true;
        GenericaMensagem.info("Sucesso", "Reserva concluída com Sucesso!");
    }

    public void alter() {
        if (isBlock()) {
            GenericaMensagem.warn("Sistema", "CARAVANA BLOQUEADA PARA MANUTENÇÃO DAS POLTRONAS POR! SOLICITE A LIBERAÇÃO!");
            return;
        }
        if (vendas.getDtCancelamento() == null) {
            locked = false;
        } else {
            GenericaMensagem.warn("Sistema", "ESSA VENDA ENCONTRA-SE CANCELADA!");
        }
    }

    public void gerarParcelas() {
        disabledSave = false;
        if (parcelas < 0) {
            return;
        }
        if (vendas.getId() != null) {
            novoParcelamento = true;
        }

        String vencs = dataEntrada;
        String vlEnt = valorEntrada;
        float vE = Moeda.substituiVirgulaFloat(valorEntrada);
        DataHoje dh = new DataHoje();
        String vencimento = dataVencimento();
        vencimento = dataVencimento();
        List<Parcelas> listaParcelasMem = listaParcelas;
        listaParcelas = new ArrayList();
        if (parcelas == 1) {
            listaParcelas.add(new Parcelas(vencs, Moeda.converteR$(valorTotal), false, new Movimento(), true));
        } else if (vE > 0) {
            listaParcelas.add(new Parcelas(dataEntrada, Moeda.converteR$(valorEntrada), false, new Movimento(), true));
            vlEnt = Moeda.converteR$Float(
                    Moeda.divisaoValores(
                            Moeda.subtracaoValores(Moeda.substituiVirgulaFloat(valorTotal), Moeda.substituiVirgulaFloat(vlEnt)
                            //Moeda.substituiVirgulaFloat(valorAPagar), Moeda.substituiVirgulaFloat(vlEnt)
                            ),
                            parcelas - 1
                    ));
            for (int i = 1; i < parcelas; i++) {
                if (i > 1) {
                    vencimento = dh.incrementarMeses(1, vencimento);
                }
                listaParcelas.add(new Parcelas(vencimento, Moeda.converteR$(vlEnt), false, new Movimento(), false));
            }
        } else {
            float vParcela = 0;
            float qtdeParcelas = parcelas;
            float vTotal = Moeda.substituiVirgulaFloat(valorTotal);
            if (vendas.getId() != null) {
                int qtdeBaixada = 0;
                vTotal = Moeda.substituiVirgulaFloat(valorTotal) - Moeda.substituiVirgulaFloat(valorPago);
                for (int i = 0; i < listMovimento.size(); i++) {
                    if (listMovimento.get(i).getBaixa() != null) {
                        qtdeBaixada++;
                        listaParcelas.add(new Parcelas(listMovimento.get(i).getVencimento(), Moeda.converteR$("" + listMovimento.get(i).getValor()), true, listMovimento.get(i), true));
                        vencimento = dh.incrementarMeses(1, listMovimento.get(i).getVencimento());
                    }
                }
                qtdeParcelas = parcelas - qtdeBaixada;
            }
            if (qtdeParcelas == 0) {
                GenericaMensagem.warn("Validação", "O NÚMERO DE PARCELAS DEVE SER SUPERIOR AO NÚMERO JÁ BAIXADO!");
                listaParcelas = listaParcelasMem;
                return;
            }
            for (int i = 0; i < qtdeParcelas; i++) {
                vParcela = vTotal / qtdeParcelas;
                if (i > 0) {
                    vencimento = dh.incrementarMeses(1, vencimento);
                }
                listaParcelas.add(new Parcelas(vencimento, Moeda.converteR$("" + vParcela), false, new Movimento(), true));
            }
        }

        listaDataEntrada = new ArrayList();
        disabledGerarParcelas = false;
        atualizaValoresParcela(0);
    }

    public String dataVencimento() {
        String dataVencimento;
        DataHoje dh = new DataHoje();
        String mesPrimeiraParcela = DataHoje.converteDataParaReferencia(dh.incrementarMeses(1, DataHoje.data()));
        String mes;
        String ano;
//        if (!listaMesVencimento.isEmpty()) {
//            mesPrimeiraParcela = listaMesVencimento.get(idMesVencimento).getDescription();
//        } else {
//            mesPrimeiraParcela = DataHoje.dataReferencia(DataHoje.data());
//        }
        mes = mesPrimeiraParcela.substring(0, 2);
        ano = mesPrimeiraParcela.substring(3, 7);
        if (DataHoje.qtdeDiasDoMes(Integer.parseInt(mes), Integer.parseInt(ano)) >= idDiaVencimento) {
            if (idDiaVencimento < 10) {
                dataVencimento = "0" + idDiaVencimento + "/" + mes + "/" + ano;
            } else {
                dataVencimento = idDiaVencimento + "/" + mes + "/" + ano;
            }
        } else {
            String diaSwap = Integer.toString(DataHoje.qtdeDiasDoMes(Integer.parseInt(mes), Integer.parseInt(ano)));
            if (diaSwap.length() < 2) {
                diaSwap = "0" + diaSwap;
            }
            dataVencimento = diaSwap + "/" + mes + "/" + ano;
        }
        return dataVencimento;
    }

    public void adicionarReserva() {
        if (canceled) {
            GenericaMensagem.warn("Validação", "ANTES DE ADICIONAR UMA RESERVA É NECESSÁRIO CONCLUIR O CANCELAMENTO E REFAZER AS PARCELAS!");
            return;
        }
        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getFisica().getId() == -1) {
                GenericaMensagem.warn("Validação", "INCLUIR PASSAGEIRO A RESERVA ADICIONADA!");
                return;
            }
        }
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Erro", "Pesquise um responsável!");
            return;
        }

        if (pessoaEndereco.getId() == -1) {
            GenericaMensagem.warn("Erro", "Cadastre um endereço para este responsável!");
            return;
        }
        if (pessoaFisica.getId() != -1) {
            // VERIFICA SE PESSOA É MAIOR DE IDADE
            DataHoje dh = new DataHoje();
            int idade = dh.calcularIdade(pessoaFisica.getNascimento());
            if (idade < 18) {
                GenericaMensagem.warn("Erro", "Esta pessoa não é maior de idade, não poderá ser responsável!");
                return;
            }
        }
        if (caravana.getId() == -1) {
            GenericaMensagem.warn("Erro", "Erro confirmar caravana!");
            return;
        }
        if (getListaPoltrona().isEmpty()) {
            GenericaMensagem.warn("Erro", "Não existe mais poltronas disponíveis!");
            return;
        }

        //EventoServicoDB dbEs = new EventoServicoDao();
        // PASSAGEIRO --- VALOR --- DESCONTO --- TIPO / VALOR
        //listaReserva.add(new DataObject(new Fisica(), 0, "0,00", "0,00", dbEs.pesquisaCodigo(Integer.valueOf(listaTipo.get(idTipo).getDescription())), eventoServico));
        CaravanaReservas re = new CaravanaReservas();
        re.setEventoServico(eventoServico);
        //listaReserva.add(new DataObject(new Fisica(), 0, "0,00", "0,00", eventoServico, eventoServicoValor));        
        listaReservas.add(new ListaReservas(new Fisica(), 0, "0,00", "0,00", re, eventoServicoValor, new ArrayList()));
        loadPoltronasReservas();
    }

    public void adicionarReservaResponsavel() {
        idAdicionar = listReservas.size();
        Fisica f = (Fisica) new Dao().find(new Fisica(), pessoa.getFisica().getId());
        GenericaSessao.put("fisicaPesquisa", f);
        GenericaSessao.put("pesquisaFisicaTipo", "passageiro");
        adicionarReserva();

    }

    public String pesquisaPassageiro(int index) {
        idAdicionar = index;
        GenericaSessao.put("pesquisaFisicaTipo", "passageiro");
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pesquisaPessoaFisica();
    }

    public String cadastrarPassageiro(int index) {
        idAdicionar = index;
        GenericaSessao.put("cadastrar", true);
        GenericaSessao.put("pesquisaFisicaTipo", "passageiro");
        GenericaSessao.remove("fisicaBean");
        return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pessoaFisica();
    }

    public void removerPessoa() {
        pessoa = new Pessoa();
    }

    public void removerReserva(int index, ListaReservas datao) {
        listaReservas.remove(index);
    }

    public void atualizaCaravana() {
        for (int i = 0; i < listaCaravana.size(); i++) {
            if (listaCaravana.get(i).getId().equals(getIdCaravanaSelect())) {
                caravana = listaCaravana.get(i);
                break;
            }
        }
        vendas.setEvento(caravana.getEvento());
        listaTipo = new ArrayList();
        bloqueioRotina = new BloqueioRotinaDao().existRotinaCodigo(142, caravana.getId());
    }

    public void atualizaTipo() {
        EventoServicoValorDao dbE = new EventoServicoValorDao();
        eventoServico = (EventoServico) new Dao().find(new EventoServico(), Integer.parseInt(listaTipo.get(idTipo).getDescription()));
        eventoServicoValor = dbE.pesquisaEventoServicoValor(eventoServico.getId());
    }

    public void loadListaCaravanaSelect() {
        listaCaravanaSelect = new ArrayList();
        listaCaravana = new ArrayList();
        List<Caravana> list = new CaravanaDao().findAll((old ? "old" : "current"));
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idCaravanaSelect = list.get(i).getId();
                    caravana = list.get(i);
                    vendas.setEvento(list.get(i).getEvento());
                }
                listaCaravanaSelect.add(new SelectItem(list.get(i).getId(), list.get(i).getDataSaida() + " - " + list.get(i).getHoraSaida() + " - " + list.get(i).getEvento().getDescricaoEvento().getDescricao(), Integer.toString(i)));
                listaCaravana.add(list.get(i));

            }
            bloqueioRotina = new BloqueioRotinaDao().existRotinaCodigo(new Rotina().get().getId(), caravana.getId());
        }

    }

    public List<SelectItem> getListaCaravanaSelect() {
        return listaCaravanaSelect;
    }

    public void setListaCaravanaSelect(List<SelectItem> listaCaravanaSelect) {
        this.listaCaravanaSelect = listaCaravanaSelect;
    }

    public List<SelectItem> getListaPoltrona() {
        List<Integer> select;
        if (!listaCaravana.isEmpty() && listaPoltrona.isEmpty()) {
            select = new PoltronasDao().listaPoltronasUsadas(caravana.getEvento().getId());

            boolean adc = true;
            String pol;
            for (int i = 1; i <= caravana.getQuantidadePoltronas(); i++) {
                for (Integer select1 : select) {
                    if (i == select1) {
                        adc = false;
                        break;
                    }
                }
                if (adc) {
                    pol = "000" + i;
                    listaPoltrona.add(new SelectItem(i, pol.substring(pol.length() - 2, pol.length())));
                }
                adc = true;
            }
        }
        return listaPoltrona;
    }

    public Boolean getShowResponsavel() {
        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getFisica().getPessoa().getId() == pessoa.getId()) {
                return false;
            }
        }
        return true;
    }

    public List<SelectItem> getListaTipo() {
        if (!listaCaravana.isEmpty()) {

            if (listaTipo.isEmpty() && caravana.getId() != -1) {
                idTipo = 0;
                List<EventoServico> select;
                EventoServicoDao db = new EventoServicoDao();
                EventoServicoValorDao dbE = new EventoServicoValorDao();
                if (caravana.getId() != -1) {
                    select = db.listaEventoServico(caravana.getEvento().getId());
                    for (int i = 0; i < select.size(); i++) {
                        listaTipo.add(new SelectItem(i, select.get(i).getDescricao(), "" + select.get(i).getId()));
                    }
                    if (idTipo >= select.size()) {
                        idTipo = 0;
                    }
                    eventoServico = (EventoServico) new Dao().find(new EventoServico(), select.get(idTipo).getId());
                    eventoServicoValor = dbE.pesquisaEventoServicoValor(eventoServico.getId());
                }
            }
        }
        return listaTipo;
    }

    public String novo() {
        GenericaSessao.remove("vendasCaravanaBean");
        return "vendasCaravana";
    }

    public void setListaTipo(List<SelectItem> listaTipo) {
        this.listaTipo = listaTipo;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public CaravanaVenda getVendas() {
        return vendas;
    }

    public Pessoa getPessoa() {
        if ((GenericaSessao.exists("fisicaPesquisa") && GenericaSessao.getString("pesquisaFisicaTipo").equals("responsavel")) || GenericaSessao.exists("juridicaPesquisa")) {
            idDiaVencimento = 0;
            if (GenericaSessao.exists("fisicaPesquisa")) {
                pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            }
            if (GenericaSessao.exists("juridicaPesquisa")) {
                pessoa = ((Juridica) GenericaSessao.getObject("juridicaPesquisa", true)).getPessoa();
            }
            /**
             * Tipo : 0 => Aluno / 1 => Responsável
             *
             * @param tipo
             */
            PessoaDao pdb = new PessoaDao();
            PessoaComplemento pc;
            Pessoa p;
            p = pessoa;
            pc = pdb.pesquisaPessoaComplementoPorPessoa(p.getId());
            if (pc.getId() == -1) {
                Registro r;
                Dao dao = new Dao();
                r = (Registro) dao.find(new Registro(), 1);
                if (r.getId() != -1) {
                    pc.setNrDiaVencimento(r.getFinDiaVencimentoCobranca());
                    pc.setCobrancaBancaria(true);
                    pc.setPessoa(p);
                    if (dao.save(pc)) {
                        dao.commit();
                    } else {
                        dao.rollback();
                    }
                }
            }
            FisicaDao dbf = new FisicaDao();
            JuridicaDao dbj = new JuridicaDao();
            pessoaFisica = dbf.pesquisaFisicaPorPessoa(pessoa.getId());
            if (pessoaFisica == null) {
                pessoaJuridica = dbj.pesquisaJuridicaPorPessoa(pessoa.getId());
                pessoaFisica = new Fisica();
                if (pessoaJuridica == null) {
                    pessoaJuridica = new Juridica();
                }
            } else {
                pessoaJuridica = new Juridica();
            }

            vendas.setResponsavel(pessoa);
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<ListaReservas> getListaReservas() {
        if (GenericaSessao.exists("fisicaPesquisa") && idAdicionar != -1) {
            disabledGerarParcelas = false;
            Fisica fis = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            for (int i = 0; i < listaReservas.size(); i++) {
                if (fis.getPessoa().getId() == listaReservas.get(i).getFisica().getPessoa().getId()) {
                    GenericaMensagem.warn("Validação", "PASSAGEIRO JÁ CADASTRADO PARA ESSA VENDA!");
                    return listaReservas;
                }
            }
            CaravanaReservasDao caravanaReservasDao = new CaravanaReservasDao();
            if (!caravanaReservasDao.findPassageiroCaravana(caravana.getId(), fis.getPessoa().getId()).isEmpty()) {
                GenericaMensagem.warn("Validação", "PASSAGEIRO JÁ CADASTRADO PARA ESSA CARAVANA!");
                return listaReservas;
            }
            if (fis.getPessoa().getPessoaComplemento() != null && fis.getPessoa().getPessoaComplemento().getId() != -1) {
                if (fis.getPessoa().getPessoaComplemento().getObsAviso() != null && !fis.getPessoa().getPessoaComplemento().getObsAviso().isEmpty()) {
                    GenericaMensagem.fatal("Mensagem", "AVISO: " + fis.getPessoa().getPessoaComplemento().getObsAviso());
                }
            }
            if (vendas.getId() != null) {
                disabledGerarParcelas = true;
            }

            if (fis.getIdade() >= listaReservas.get(idAdicionar).getEventoServicoValor().getIdadeInicial() && fis.getIdade() <= listaReservas.get(idAdicionar).getEventoServicoValor().getIdadeFinal()) {

            } else {
                GenericaMensagem.warn("Validação", "A IDADE DO PASSAGEIRO SELECIONADO NÃO SE ENQUADRA NO SERVIÇO ESCOLHIDO!");
                return listaReservas;
            }
            listaReservas.get(idAdicionar).setFisica(fis);

            SociosDao db = new SociosDao();
            float valor;
            //valor = db.descontoSocioEve(fis.getPessoa().getId() , eventoServico.getServicos().getId() );
            valor = db.descontoSocioEve(fis.getPessoa().getId(), ((CaravanaReservas) listaReservas.get(idAdicionar).getReservas()).getEventoServico().getId());
            if (valor == 0) {
                listaReservas.get(idAdicionar).setValor(Moeda.converteR$Float(((EventoServicoValor) listaReservas.get(idAdicionar).getEventoServicoValor()).getValor()));
                //listaReserva.get(idAdicionar).setArgumento3(Moeda.converteR$Float( Moeda.subtracaoValores(eventoServicoValor.getValor(), 0)));// NA VERDADE SUBTRAI PELO DESCONTO
            } else {
                listaReservas.get(idAdicionar).setValor(Moeda.converteR$Float(valor));
                //listaReserva.get(idAdicionar).setArgumento3(Moeda.converteR$Float( Moeda.subtracaoValores(valor, 0)));// NA VERDADE SUBTRAI PELO DESCONTO
            }
            idAdicionar = -1;
        }
        return listaReservas;
    }

    public void setListaReservas(List<ListaReservas> listaReservas) {
        this.listaReservas = listaReservas;
    }

    public List<Parcelas> getListaParcelas() {
        return listaParcelas;
    }

    public void setListaParcelas(List<Parcelas> listaParcelas) {
        this.listaParcelas = listaParcelas;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    public String getParcelasString() {
        return Integer.toString(parcelas);
    }

    public void setParcelasString(String parcelasString) {
        this.parcelas = Integer.parseInt(parcelasString);
        if (this.parcelas == 0) {
            this.parcelas = 1;
        }
        int countBaixa = 0;
        if (!listMovimento.isEmpty()) {
            for (int i = 0; i < listMovimento.size(); i++) {
                if (listMovimento.get(i).getBaixa() != null) {
                    countBaixa++;
                }
            }
            if (this.parcelas <= countBaixa) {
                GenericaMensagem.warn("Validação", "JÁ EXISTE(M) " + countBaixa + " PARCELA(S) PAGA(S), SENDO PERMITIDO QUE MÍNIMO SEJAM GERADAS " + (countBaixa + 1) + " PARCELAS");
                countBaixa++;
                parcelas = countBaixa;

            }
        }
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Integer getIdCaravanaSelect() {
        return idCaravanaSelect;
    }

    public void setIdCaravanaSelect(Integer idCaravanaSelect) {
        this.idCaravanaSelect = idCaravanaSelect;
    }

    public Caravana getCaravana() {
        return caravana;
    }

    public void setCaravana(Caravana caravana) {
        this.caravana = caravana;
    }

    public String getValorTotal() {
        if (!listaReservas.isEmpty()) {
            float valor = 0;
            float desconto = 0;
            for (ListaReservas lr : listaReservas) {
                if (lr.getFisica().getId() != -1 && lr.getReservas().getDtCancelamento() == null) {
                    valor = Moeda.somaValores(valor, Moeda.substituiVirgulaFloat(lr.getValor()));
                    desconto = Moeda.somaValores(desconto, Moeda.substituiVirgulaFloat(lr.getDesconto()));
                }
            }
            valorTotal = Moeda.converteR$Float(Moeda.subtracaoValores(valor, desconto));
        } else {
            valorTotal = "0,00";
        }
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorPago() {
        if (!listaParcelas.isEmpty()) {
            float valor = 0;
            for (Parcelas listaParcela : listaParcelas) {
                if (listaParcela.getBaixado()) {
                    valor = Moeda.somaValores(valor, Moeda.substituiVirgulaFloat(String.valueOf(listaParcela.getValor())));
                }
            }
            valorPago = Moeda.converteR$Float(valor);
        } else {
            valorPago = "0,00";
        }
        return valorPago;
    }

    public void setValorPago(String valorPago) {
        this.valorPago = valorPago;
    }

    public String getValorOutras() {
        if (!listaParcelas.isEmpty()) {
            float valor = 0;
            for (Parcelas listaParcela : listaParcelas) {
                if (!listaParcela.getBaixado()) {
                    valor = Moeda.somaValores(valor, Moeda.substituiVirgulaFloat(String.valueOf(listaParcela.getValor())));
                }
            }
            valorOutras = Moeda.converteR$Float(valor);
        } else {
            valorOutras = "0,00";
        }
        return valorOutras;
    }

    public void setValorOutras(String valorOutras) {
        this.valorOutras = valorOutras;
    }

    public String getValorEntrada() {
        if (valorEntrada.isEmpty()) {
            valorEntrada = "0";
        }

        if (Moeda.converteUS$(valorEntrada) > Moeda.converteUS$(valorTotal)) {
            valorEntrada = valorTotal;
        }
        if (Moeda.converteUS$(valorEntrada) < 0) {
            valorEntrada = "0";
        }

        return Moeda.converteR$(valorEntrada);
    }

    public void setValorEntrada(String valorEntrada) {
        if (valorEntrada.isEmpty()) {
            valorEntrada = "0";
        }
        this.valorEntrada = Moeda.substituiVirgula(valorEntrada);
    }

    public PessoaEndereco getPessoaEndereco() {
        if (pessoaEndereco.getId() == -1) {
            PessoaEnderecoDao dbp = new PessoaEnderecoDao();
            pessoaEndereco = dbp.pesquisaEndPorPessoaTipo(pessoa.getId(), 4);
            if (pessoaEndereco == null) {
                pessoaEndereco = new PessoaEndereco();
            }
        }
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public Fisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(Fisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Juridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(Juridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public int getIdDataEntrada() {
        return idDataEntrada;
    }

    public void setIdDataEntrada(int idDataEntrada) {
        this.idDataEntrada = idDataEntrada;
    }

    public int getIdMesVencimento() {
        return idMesVencimento;
    }

    public void setIdMesVencimento(int idMesVencimento) {
        this.idMesVencimento = idMesVencimento;
    }

    public List<SelectItem> getListaDataEntrada() {
        float vE = Moeda.substituiVirgulaFloat(valorEntrada);
        if (vE > 0) {
            if (listaDataEntrada.isEmpty()) {
                idDataEntrada = 0;
                DataHoje dh = new DataHoje();
                String dataEntradaX;
                for (int i = 0; i < 20; i++) {
                    dataEntradaX = dh.incrementarDias(i, DataHoje.data());
                    listaDataEntrada.add(new SelectItem(i, dataEntradaX, dataEntradaX));
                    if (dataEntradaX.equals(DataHoje.data())) {
                        idDataEntrada = i;
                    }
                }
            }
        }
        return listaDataEntrada;
    }

    public void setListaDataEntrada(List<SelectItem> listaDataEntrada) {
        this.listaDataEntrada = listaDataEntrada;
    }

    public List<SelectItem> getListaMesVencimento() {
        if (listaMesVencimento.isEmpty()) {
            int dE = DataHoje.converteDataParaInteger(dataEntrada);
            boolean isTaxa = false;
            String data;
            DataHoje dh = new DataHoje();
            data = DataHoje.data();
            int dH = DataHoje.converteDataParaInteger(data);
            int dtVecto = 0;
            float vE = Moeda.substituiVirgulaFloat(valorEntrada);
            if (vE > 0) {
                //dtVecto = Integer.parseInt(dataEntrada.substring(0, 2));
                data = dh.incrementarMeses(1, data);
            } else {
                data = DataHoje.data();
            }
//            if (dE >= dH && dtVecto > idDiaVencimento && vE > 0) {
//                data = dh.incrementarMeses(1, data);
//            } else if (dE >= dH && dtVecto > idDiaVencimento) {
//                data = dh.incrementarMeses(1, data);
//            } else {
//            }
            String mesAno;
            int iDtMr;
            int iDtVct;
            int qtdeParcelas = parcelas;
            if (parcelas < 6) {
                qtdeParcelas = 6;
            }
            for (int i = 0; i < qtdeParcelas; i++) {
                if (i > 0) {
                    data = dh.incrementarMeses(1, data);
                }
                if (!isTaxa) {
                    iDtMr = DataHoje.converteDataParaInteger(DataHoje.data());
                    iDtVct = DataHoje.converteDataParaInteger(data);
                    if (vE > 0) {
                        if (iDtVct > iDtMr) {
                            idMesVencimento = i;
                            isTaxa = true;
                        } else {
                            idMesVencimento = 0;
                        }
                    } else {
                        isTaxa = true;
                        idMesVencimento = 0;
                    }
                }
                mesAno = data.substring(3, 5) + "/" + data.substring(6, 10);
                listaMesVencimento.add(new SelectItem(i, mesAno, mesAno));
            }
        }
        return listaMesVencimento;
    }

    public void setListaMesVencimento(List<SelectItem> listaMesVencimento) {
        this.listaMesVencimento = listaMesVencimento;
    }

    public List<SelectItem> getListaDataVencimento() {
        if (listaDataVencimento.isEmpty()) {
            for (int i = 1; i <= 31; i++) {
                listaDataVencimento.add(new SelectItem(Integer.toString(i)));
            }
        }
        return listaDataVencimento;
    }

    public void setListaDataVencimento(List<SelectItem> listaDataVencimento) {
        this.listaDataVencimento = listaDataVencimento;
    }

    public int getIdDiaVencimento() {
        PessoaDao pessoaDB = new PessoaDao();
        if (pessoa.getId() != -1) {
            PessoaComplemento pc = pessoaDB.pesquisaPessoaComplementoPorPessoa(pessoa.getId());
            if (pc.getId() == -1) {
                if (getRegistro() != null) {
                    this.idDiaVencimento = registro.getFinDiaVencimentoCobranca();
                } else {
                    this.idDiaVencimento = Integer.parseInt(DataHoje.data().substring(0, 2));
                }
            } else {
                this.idDiaVencimento = pc.getNrDiaVencimento();
            }
        } else {
            this.idDiaVencimento = Integer.parseInt(DataHoje.data().substring(0, 2));
        }
        return idDiaVencimento;
    }

    public void setIdDiaVencimento(int idDiaVencimento) {
        this.idDiaVencimento = idDiaVencimento;
    }

    public void updatePessoaComplemento() {
        if (pessoa.getId() != -1) {
            PessoaDao pessoaDB = new PessoaDao();
            PessoaComplemento pc = pessoaDB.pesquisaPessoaComplementoPorPessoa(pessoa.getId());
            pc.setNrDiaVencimento(idDiaVencimento);
            Dao dao = new Dao();
            if (dao.update(pc, true)) {

            } else {

            }
        }
    }

    public Registro getRegistro() {
        if (registro.getId() == -1) {
            registro = (Registro) new Dao().find(new Registro(), 1);
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

//    public List<CaravanaVenda> getListCVenda() {
//        return listCVenda;
//    }
//
//    public void setListCVenda(List<CaravanaVenda> listCVenda) {
//        this.listCVenda = listCVenda;
//    }
//
//    public void loadListCVenda(Integer pessoa_id) {
//        listCVenda = new VendasCaravanaDao().findByPessoa(pessoa_id);
//    }
    public String getCalculaValorMovimentoAlterado() {
        if (!listaParcelas.isEmpty()) {
            try {
                disabledSave = false;
                BigDecimal vc = new BigDecimal(0);
                for (int i = 0; i < listaParcelas.size(); i++) {
                    vc = vc.add(new BigDecimal(Moeda.converteUS$(listaParcelas.get(i).getValor())));
                }
                Float v = Float.parseFloat(vc.toString());
                Float vn = v;
                if (!v.equals(Moeda.converteUS$(valorTotal))) {
                    disabledSave = true;
                    if (vn > 0) {
                        if (vn > Moeda.converteUS$(valorTotal)) {
                            Float diff = vn - Moeda.converteUS$(valorTotal);
                            return "Existe uma difereça na soma das parcelas: Remover R$ " + Moeda.converteR$Float(diff) + ". Corrigir para concluir.";
                        } else {
                            Float diff = Moeda.converteUS$(valorTotal) - vn;
                            return "Existe uma difereça na soma das parcelas: Acrescentar R$ " + Moeda.converteR$Float(diff) + ". Corrigir para concluir.";
                        }
                    } else if (vn < 0) {
                        if (!listaParcelas.isEmpty()) {
                            return "Existe uma difereça na soma das parcelas: Remover R$ " + Moeda.converteR$Float(vn) + ". Corrigir para concluir.";
                        }
                    } else {
                        return "";
                    }
                }
            } catch (Exception e) {
                return "Erro no valor";
            }
        }
        return "";
    }

    public Boolean getDisabledSave() {
        return disabledSave;
    }

    public void setDisabledSave(Boolean disabledSave) {
        this.disabledSave = disabledSave;
    }

    public List<CaravanaReservas> getListReservas() {
        return listReservas;
    }

    public void setListReservas(List<CaravanaReservas> listReservas) {
        this.listReservas = listReservas;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void findFilter(String tcase) {
        switch (tcase) {
            case "I":
                as = tcase;
                break;
            case "P":
                as = tcase;
                break;
        }
        loadList();
    }

    public void loadList() {
        listReservas = new ArrayList();
        listVenda = new ArrayList();
        if (description.isEmpty()) {
            return;
        }
        if (type.equals("caravana")) {
            listVenda = new CaravanaVendaDao().find(description, by, as);
        } else if (type.equals("reservas")) {
            listReservas = new CaravanaReservasDao().find(description, by, as);
        }
    }

    public String getMask() {
        String b = by;
        if (by.equals("documento") || by.equals("responsavel_documento")) {
            b = "cpf";
        }
        return Mask.getMascaraPesquisa(b, true);
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CaravanaVenda> getListVenda() {
        return listVenda;
    }

    public void setListVenda(List<CaravanaVenda> listVenda) {
        this.listVenda = listVenda;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getDisabledEntrada() {
        for (int i = 0; i < listaParcelas.size(); i++) {
            if (listaParcelas.get(i).getBaixado()) {
                return true;
            }
        }
        return false;
    }

    public Boolean getOld() {
        return old;
    }

    public void setOld(Boolean old) {
        this.old = old;
    }

    public Integer getIndexCaravanaSelect() {
        try {
            return Integer.parseInt(listaCaravanaSelect.get(idCaravanaSelect).getDescription());
        } catch (Exception e) {
            return 0;
        }
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public CaravanaReservas getReservas() {
        return reservas;
    }

    public void setReservas(CaravanaReservas reservas) {
        this.reservas = reservas;
    }

    public List<CaravanaReservas> getListReservasCanceladas() {
        return listReservasCanceladas;
    }

    public void setListReservasCanceladas(List<CaravanaReservas> listReservasCanceladas) {
        this.listReservasCanceladas = listReservasCanceladas;
    }

    public Boolean getDisabledGerarParcelas() {
        return disabledGerarParcelas;
    }

    public void setDisabledGerarParcelas(Boolean disabledGerarParcelas) {
        this.disabledGerarParcelas = disabledGerarParcelas;
    }

    public List<Integer> getListPoltronasReservadas() {
        return listPoltronasReservadas;
    }

    public void setListPoltronasReservadas(List<Integer> listPoltronasReservadas) {
        this.listPoltronasReservadas = listPoltronasReservadas;
    }

    public boolean isBlock() {
        if (bloqueioRotina != null) {
            if (bloqueioRotina.getId() != -1) {
                return true;
            }
        }
        return false;
    }

    public BloqueioRotina getBloqueioRotina() {
        return bloqueioRotina;
    }

    public void setBloqueioRotina(BloqueioRotina bloqueioRotina) {
        this.bloqueioRotina = bloqueioRotina;
    }

    public class ListaReservas {

        private Fisica fisica;
        private Integer poltrona;
        private String valor;
        private String desconto;
        private CaravanaReservas reservas;
        private EventoServicoValor eventoServicoValor;
        private List<SelectItem> listPoltrona;

        public ListaReservas() {
            this.fisica = new Fisica();
            this.poltrona = 0;
            this.valor = "0,00";
            this.desconto = "0,00";
            this.reservas = new CaravanaReservas();
            this.eventoServicoValor = null;
            this.listPoltrona = new ArrayList();
        }

        public ListaReservas(Fisica fisica, Integer poltrona, String valor, String desconto, CaravanaReservas reservas, EventoServicoValor eventoServicoValor, List<SelectItem> listPoltrona) {
            this.fisica = fisica;
            this.poltrona = poltrona;
            this.valor = valor;
            this.desconto = desconto;
            this.reservas = reservas;
            this.eventoServicoValor = eventoServicoValor;
            this.listPoltrona = listPoltrona;
        }

        public Fisica getFisica() {
            return fisica;
        }

        public void setFisica(Fisica fisica) {
            this.fisica = fisica;
        }

        public Integer getPoltrona() {
            return poltrona;
        }

        public void setPoltrona(Integer poltrona) {
            this.poltrona = poltrona;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getDesconto() {
            return desconto;
        }

        public void setDesconto(String desconto) {
            this.desconto = desconto;
        }

        public CaravanaReservas getReservas() {
            return reservas;
        }

        public void setReservas(CaravanaReservas reservas) {
            this.reservas = reservas;
        }

        public EventoServicoValor getEventoServicoValor() {
            return eventoServicoValor;
        }

        public void setEventoServicoValor(EventoServicoValor eventoServicoValor) {
            this.eventoServicoValor = eventoServicoValor;
        }

        public List<SelectItem> getListPoltrona() {
            if (!listaCaravana.isEmpty() && listPoltrona.isEmpty()) {
                if (caravana.getId() != -1) {
                    List<Integer> select = new PoltronasDao().listaPoltronasUsadas(caravana.getEvento().getId());
                    boolean adc = true;
                    String pol;
                    if (poltrona != 0) {
                        listPoltrona.add(new SelectItem(poltrona, (poltrona < 10 ? ("0" + poltrona) : (poltrona + ""))));
                    }
                    for (int i = 1; i <= caravana.getQuantidadePoltronas(); i++) {
                        for (Integer select1 : select) {
                            if (i == select1) {
                                adc = false;
                                break;
                            }
                        }
                        if (adc) {
                            pol = "000" + i;
                            listPoltrona.add(new SelectItem(i, pol.substring(pol.length() - 2, pol.length())));
                        }
                        adc = true;
                    }
                    SelectItemSort.sort(listPoltrona);
                }
            }
            return listPoltrona;
        }

        public void setListPoltrona(List<SelectItem> listPoltrona) {
            this.listPoltrona = listPoltrona;
        }

    }

    public class Parcelas {

        private String vencimento;
        private String valor;
        private Boolean baixado;
        private Movimento movimento;
        private Boolean cancelado;

        public Parcelas() {
            this.vencimento = "";
            this.valor = "";
            this.baixado = null;
            this.movimento = null;
            this.cancelado = null;
        }

        public Parcelas(String vencimento, String valor, Boolean baixado, Movimento movimento, Boolean cancelado) {
            this.vencimento = vencimento;
            this.valor = valor;
            this.baixado = baixado;
            this.movimento = movimento;
            this.cancelado = cancelado;
        }

        public String getVencimento() {
            return vencimento;
        }

        public void setVencimento(String vencimento) {
            this.vencimento = vencimento;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public Boolean getBaixado() {
            return baixado;
        }

        public void setBaixado(Boolean baixado) {
            this.baixado = baixado;
        }

        public Movimento getMovimento() {
            return movimento;
        }

        public void setMovimento(Movimento movimento) {
            this.movimento = movimento;
        }

        public Boolean getCancelado() {
            return cancelado;
        }

        public void setCancelado(Boolean cancelado) {
            this.cancelado = cancelado;
        }
    }

}
