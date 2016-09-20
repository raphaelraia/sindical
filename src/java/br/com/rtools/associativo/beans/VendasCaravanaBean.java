package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.CVenda;
import br.com.rtools.associativo.Caravana;
import br.com.rtools.associativo.EventoServico;
import br.com.rtools.associativo.EventoServicoValor;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Reservas;
import br.com.rtools.associativo.dao.CVendaDao;
import br.com.rtools.associativo.dao.CaravanaDao;
import br.com.rtools.associativo.dao.EventoServicoDao;
import br.com.rtools.associativo.dao.EventoServicoValorDao;
import br.com.rtools.associativo.dao.ReservasDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.dao.VendasCaravanaDao;
import br.com.rtools.financeiro.CondicaoPagamento;
import br.com.rtools.financeiro.Evt;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.TipoServico;
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
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class VendasCaravanaBean implements Serializable {

    private Caravana caravana;
    private CVenda vendas;
    private EventoServicoValor eventoServicoValor;
    private EventoServico eventoServico;
    private Fisica pessoaFisica;
    private Fisica fisica;
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
    private int idCaravana;
    private int idTipo;
    private int idAdicionar;
    private int idDataEntrada;
    private int idMesVencimento;
    private int idDiaVencimento;
    private int parcelas;
    private String dataEntrada;
    private Pessoa pessoa;
    private String valorTotal;
    private String valorPago;
    private String valorOutras;
    private String valorEntrada;
    private Registro registro;
    private List<CVenda> listCVenda;
    private String type;
    private String by;
    private String as;
    private String description;
    private List<Reservas> listReservas;
    private List<CVenda> listVenda;
    private Boolean disabledSave;
    private Boolean locked;

    @PostConstruct
    public void init() {
        caravana = new Caravana();
        vendas = new CVenda();
        eventoServicoValor = new EventoServicoValor();
        eventoServico = new EventoServico();
        pessoaFisica = new Fisica();
        fisica = new Fisica();
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
        idCaravana = 0;
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
        listCVenda = new ArrayList();
        disabledSave = false;
        type = "caravana";
        by = "I";
        as = "nome";
        description = "";
        listReservas = new ArrayList();
        listVenda = new ArrayList();
        locked = false;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("rendasCaravanaBean");
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
            for (ListaReservas lr : listaReservas) {
                Reservas r = (Reservas) lr.getReservas();
                if (linha.get(0).equals(r.getEventoServico().getDescricao())) {
                    if ((Float) valor[i] == null) {
                        valor[i] = Moeda.converteUS$(lr.getValor()) * Integer.parseInt(linha.get(1).toString());
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
                            DataHoje.dataExtenso(vendas.getDataEmissaoString(), 3), // NÃO TEM EM vendas.getData
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

    public void excluir() {
        if (vendas.getId() == -1) {
            GenericaMensagem.warn("Erro", "Pesquise uma venda para ser cancelada!");
            return;
        }

        List<Reservas> lr;
        Dao dao = new Dao();
        VendasCaravanaDao db = new VendasCaravanaDao();
        Reservas res;
        dao.openTransaction();

        lr = db.listaReservasVenda(vendas.getId());
        for (Reservas lr1 : lr) {
            res = (Reservas) dao.find(lr1);
            if (!dao.delete(res)) {
                GenericaMensagem.warn("Erro", "Erro ao cancelar reservas!");
                dao.rollback();
                return;
            }
        }
        List<Movimento> listaMovimento = db.listaMovCaravana(vendas.getResponsavel().getId(), caravana.getEvt().getId());
        if (!listaMovimento.isEmpty()) {
            for (Movimento listaMovimento1 : listaMovimento) {
                if (listaMovimento1.getBaixa() != null) {
                    GenericaMensagem.warn("Erro", "Reserva com parcela paga não pode ser excluída!");
                    dao.rollback();
                    return;
                }
            }
            Movimento mov;
            Lote lot = new Lote();
            Evt evt = new Evt();
            List<Lote> listLote = new ArrayList();
            List<Evt> listEvt = new ArrayList();
            for (Movimento listaMovimento1 : listaMovimento) {
                mov = (Movimento) dao.find(listaMovimento1);
                if (!dao.delete(mov)) {
                    GenericaMensagem.warn("Erro", "Erro ao excluir movimentos!");
                    dao.rollback();
                    return;
                }
                if (mov.getLote().getId() != lot.getId()) {
                    lot = mov.getLote();
                    listLote.add(lot);
                }
            }
            for (int i = 0; i < listLote.size(); i++) {
                if (!dao.delete(listLote.get(i))) {
                    GenericaMensagem.warn("Erro", "Erro ao excluir Lote!");
                    dao.rollback();
                    return;
                }
                if (listLote.get(i).getEvt().getId() != evt.getId()) {
                    evt = listLote.get(i).getEvt();
                    listEvt.add(evt);
                }
            }
            if (!dao.delete(vendas)) {
                GenericaMensagem.warn("Erro", "Erro ao cancelar Venda!");
                dao.rollback();
                return;
            }
//            for (int i = 0; i < listEvt.size(); i++) {
//                if (!dao.delete(listEvt.get(i))) {
//                    GenericaMensagem.warn("Erro", "Erro ao excluir EVT!");
//                    dao.rollback();
//                    return;
//                }
//            }

            GenericaMensagem.info("Sucesso", "Reserva cancelada com sucesso!");
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

    public void load() {
        if (GenericaSessao.exists("baixa_sucesso", true)) {
            VendasCaravanaDao db = new VendasCaravanaDao();
            List<Movimento> listaMovimento = db.listaMovCaravana(vendas.getResponsavel().getId(), caravana.getEvt().getId());
            listaParcelas = new ArrayList();
            for (Movimento listaMovimento1 : listaMovimento) {
                if (listaMovimento1.getBaixa() == null) {
                    listaParcelas.add(new Parcelas(listaMovimento1.getVencimento(), Moeda.converteR$Float(listaMovimento1.getValor()), false, listaMovimento1));
                } else {
                    listaParcelas.add(new Parcelas(listaMovimento1.getVencimento(), Moeda.converteR$Float(listaMovimento1.getValor()), true, listaMovimento1));
                }
            }

        }
    }

    public String editar(CVenda v) {
        locked = true;
        vendas = v;
        CaravanaDao dbc = new CaravanaDao();
        caravana = dbc.pesquisaCaravanaPorEvento(vendas.getEvento().getId());

        for (int i = 0; i < listaCaravanaSelect.size(); i++) {
            if (caravana.getId() == Integer.valueOf(listaCaravanaSelect.get(i).getDescription())) {
                idCaravana = i;
                break;
            }
        }

        List<Reservas> lr;
        VendasCaravanaDao db = new VendasCaravanaDao();
        FisicaDao dbf = new FisicaDao();
        EventoServicoValorDao dbe = new EventoServicoValorDao();
        SociosDao dbs = new SociosDao();
        float valor;
        lr = db.listaReservasVenda(vendas.getId());
        listaReservas.clear();
        for (Reservas lr1 : lr) {
            valor = dbs.descontoSocioEve(lr1.getPessoa().getId(), lr1.getEventoServico().getServicos().getId());
            if (valor == 0) {
                valor = dbe.pesquisaEventoServicoValor(lr1.getEventoServico().getId()).getValor();
                listaReservas.add(new ListaReservas(dbf.pesquisaFisicaPorPessoa(lr1.getPessoa().getId()), 0, Moeda.converteR$Float(valor), Moeda.converteR$Float(lr1.getDesconto()), lr1, null));
            } else {
                listaReservas.add(new ListaReservas(dbf.pesquisaFisicaPorPessoa(lr1.getPessoa().getId()), 0, Moeda.converteR$Float(valor), Moeda.converteR$Float(lr1.getDesconto()), lr1, null));
            }
        }

        //List<Movimento> listaMovimento = db.listaMovCaravana(vendas.getResponsavel().getId(), vendas.getEvt().getId());
        List<Movimento> listaMovimento = db.listaMovCaravana(vendas.getResponsavel().getId(), caravana.getEvt().getId());

        listaParcelas.clear();
        for (Movimento listaMovimento1 : listaMovimento) {
            if (listaMovimento1.getBaixa() == null) {
                listaParcelas.add(new Parcelas(listaMovimento1.getVencimento(), Moeda.converteR$Float(listaMovimento1.getValor()), false, listaMovimento1));
            } else {
                listaParcelas.add(new Parcelas(listaMovimento1.getVencimento(), Moeda.converteR$Float(listaMovimento1.getValor()), true, listaMovimento1));
            }
        }

        pessoa = vendas.getResponsavel();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
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

    public void salvar() {
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquise um responsável!");
            return;
        }

        if (listaReservas.isEmpty()) {
            GenericaMensagem.warn("Validação", "Não é possivel concluir nenhuma Reserva!");
            return;
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
        if (vendas.getId() == -1) {
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

        for (ListaReservas lr : listaReservas) {
            if (lr.getReservas().getId() == -1) {
                Reservas res = new Reservas(-1,
                        vendas,
                        lr.getFisica().getPessoa(),
                        lr.getPoltrona(),
                        Moeda.converteUS$(lr.getDesconto()),
                        ((Reservas) lr.getReservas()).getEventoServico());
                if (!dao.save(res)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Não é possivel salvar venda!");
                    return;
                }
                lr.setReservas(res);
            }
        }

        CondicaoPagamento condicaoPagamento;
        if (listaParcelas.size() == 1 && dataEntrada.equals(DataHoje.data())) {
            condicaoPagamento = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 1);
        } else {
            condicaoPagamento = (CondicaoPagamento) dao.find(new CondicaoPagamento(), 2);
        }
        Lote lote = null;
        if (vendas.getId() == -1) {
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
        } else {
            for (int i = 0; i < listaReservas.size(); i++) {
                if (listaParcelas.get(i).getMovimento().getId() != -1) {
                    lote = listaParcelas.get(i).getMovimento().getLote();
                    break;
                }
            }
        }

        Movimento movimento;
        EventoServicoValor esv;
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
            } else if (!dao.update(listaParcelas.get(i).getMovimento())) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Não é possivel atualizar movimento!");
                return;
            }
        }

        NovoLog novoLog = new NovoLog();
        novoLog.save("ID: " + vendas.getId() + " - Responsável: " + vendas.getResponsavel().getNome() + " - Evento: (" + vendas.getEvento().getId() + ") - " + vendas.getEvento().getDescricaoEvento().getDescricao() + " - Quarto: " + vendas.getQuarto() + " - Serviço : (" + eventoServico.getServicos().getId() + ") " + eventoServico.getServicos().getDescricao());
        GenericaMensagem.info("Sucesso", "Reserva concluída com Sucesso!");
        dao.commit();
    }

    public void alter() {
        locked = false;
    }

    public void gerarParcelas() {
        disabledSave = false;
        if (parcelas < 0) {
            return;
        }

        String vencs = dataEntrada;
        String vlEnt = valorEntrada;
        float vE = Moeda.substituiVirgulaFloat(valorEntrada);
        DataHoje dh = new DataHoje();
        String vencimento = dataVencimento();
        if (vendas.getId() == -1) {
            vencimento = dataVencimento();
            listaParcelas = new ArrayList();
        }
        if (parcelas == 1) {
            listaParcelas.add(new Parcelas(vencs, Moeda.converteR$(valorTotal), false, new Movimento()));
        } else if (vE > 0) {
            listaParcelas.add(new Parcelas(dataEntrada, Moeda.converteR$(valorEntrada), false, new Movimento()));
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
                listaParcelas.add(new Parcelas(vencimento, Moeda.converteR$(vlEnt), false, new Movimento()));
            }
        } else {
            float vParcela;
            for (int i = 0; i < parcelas; i++) {
                vParcela = Moeda.substituiVirgulaFloat(valorTotal) / parcelas;
                if (i > 0) {
                    vencimento = dh.incrementarMeses(1, vencimento);
                }
                listaParcelas.add(new Parcelas(vencimento, Moeda.converteR$("" + vParcela), false, new Movimento()));
            }
        }

        listaDataEntrada.clear();

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

        if (listaCaravana.get(idCaravana).getId() == -1) {
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
        Reservas re = new Reservas();
        re.setEventoServico(eventoServico);
        //listaReserva.add(new DataObject(new Fisica(), 0, "0,00", "0,00", eventoServico, eventoServicoValor));
        listaReservas.add(new ListaReservas(new Fisica(), 0, "0,00", "0,00", re, eventoServicoValor));
    }

    public String pesquisaPassageiro(int index) {
        idAdicionar = index;
        GenericaSessao.put("pesquisaFisicaTipo", "passageiro");
        return ((ChamadaPaginaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("chamadaPaginaBean")).pesquisaPessoaFisica();
    }

    public void removerPessoa() {
        pessoa = new Pessoa();
    }

    public void removerReserva(int index, ListaReservas datao) {
        listaReservas.remove(index);
    }

    public void atualizaCaravana() {
        caravana = listaCaravana.get(idCaravana);
        vendas.setEvento(caravana.getEvento());
        listaTipo.clear();
    }

    public void atualizaTipo() {
        listaTipo.clear();
    }

    public List<SelectItem> getListaCaravanaSelect() {
        if (listaCaravanaSelect.isEmpty()) {
            List<Caravana> result = new Dao().list(new Caravana(), true);
            if (!result.isEmpty()) {
                for (int i = 0; i < result.size(); i++) {
                    listaCaravanaSelect.add(new SelectItem(i, result.get(i).getDataSaida() + " - " + result.get(i).getHoraSaida() + " - " + result.get(i).getEvento().getDescricaoEvento().getDescricao(), String.valueOf(result.get(i).getId())));
                    listaCaravana.add(result.get(i));
                }
                caravana = listaCaravana.get(idCaravana);
                vendas.setEvento(caravana.getEvento());
            }
        }
        return listaCaravanaSelect;
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa") && idAdicionar == -1 && GenericaSessao.getString("pesquisaFisicaTipo").equals("passageiro")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
        }

        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public List<SelectItem> getListaPoltrona() {
        List<Integer> select;
        VendasCaravanaDao db = new VendasCaravanaDao();
        if (!listaCaravana.isEmpty() && listaPoltrona.isEmpty()) {
            select = db.listaPoltronasUsadas(listaCaravana.get(idCaravana).getEvento().getId());

            boolean adc = true;
            String pol;
            for (int i = 1; i <= listaCaravana.get(idCaravana).getQuantidadePoltronas(); i++) {
                for (Integer select1 : select) {
                    if (i == select1) {
                        adc = false;
                        break;
                    }
                }
                if (adc) {
                    pol = "000" + i;
                    listaPoltrona.add(new SelectItem(i, pol.substring(pol.length() - 2, pol.length()), "" + i));
                }
                adc = true;
            }
        }
        return listaPoltrona;
    }

    public List<SelectItem> getListaTipo() {
        if (!listaCaravana.isEmpty()) {

            if (listaTipo.isEmpty() && listaCaravana.get(idCaravana).getId() != -1) {
                List<EventoServico> select;
                EventoServicoDao db = new EventoServicoDao();
                EventoServicoValorDao dbE = new EventoServicoValorDao();
                if (listaCaravana.get(idCaravana).getId() != -1) {
                    select = db.listaEventoServico(listaCaravana.get(idCaravana).getEvento().getId());
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

    public CVenda getVendas() {
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
            Fisica fis = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);

            if (fis.getIdade() >= listaReservas.get(idAdicionar).getEventoServicoValor().getIdadeInicial() && fis.getIdade() <= listaReservas.get(idAdicionar).getEventoServicoValor().getIdadeFinal()) {

            } else {
                GenericaMensagem.warn("Validação", "A IDADE DO PASSAGEIRO SELECIONADO NÃO SE ENQUADRA NO SERVIÇO ESCOLHIDO!");
                return listaReservas;
            }

            listaReservas.get(idAdicionar).setFisica(fis);

            SociosDao db = new SociosDao();
            float valor;
            //valor = db.descontoSocioEve(fis.getPessoa().getId() , eventoServico.getServicos().getId() );
            valor = db.descontoSocioEve(fis.getPessoa().getId(), ((Reservas) listaReservas.get(idAdicionar).getReservas()).getEventoServico().getId());
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
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public int getIdCaravana() {
        return idCaravana;
    }

    public void setIdCaravana(int idCaravana) {
        this.idCaravana = idCaravana;
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
                if (lr.getFisica().getId() != -1) {
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

    public List<CVenda> getListCVenda() {
        return listCVenda;
    }

    public void setListCVenda(List<CVenda> listCVenda) {
        this.listCVenda = listCVenda;
    }

    public void loadListCVenda(Integer pessoa_id) {
        listCVenda = new VendasCaravanaDao().findByPessoa(pessoa_id);
    }

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

    public List<Reservas> getListReservas() {
        return listReservas;
    }

    public void setListReservas(List<Reservas> listReservas) {
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
            listVenda = new CVendaDao().find(description, by, as);
        } else if (type.equals("reservas")) {
            listReservas = new ReservasDao().find(description, by, as);
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

    public List<CVenda> getListVenda() {
        return listVenda;
    }

    public void setListVenda(List<CVenda> listVenda) {
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

    public class ListaReservas {

        private Fisica fisica;
        private Integer poltrona;
        private String valor;
        private String desconto;
        private Reservas reservas;
        private EventoServicoValor eventoServicoValor;

        public ListaReservas() {
            this.fisica = new Fisica();
            this.poltrona = 0;
            this.valor = "0,00";
            this.desconto = "0,00";
            this.reservas = new Reservas();
            this.eventoServicoValor = null;
        }

        public ListaReservas(Fisica fisica, Integer poltrona, String valor, String desconto, Reservas reservas, EventoServicoValor eventoServicoValor) {
            this.fisica = fisica;
            this.poltrona = poltrona;
            this.valor = valor;
            this.desconto = desconto;
            this.reservas = reservas;
            this.eventoServicoValor = eventoServicoValor;
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

        public Reservas getReservas() {
            return reservas;
        }

        public void setReservas(Reservas reservas) {
            this.reservas = reservas;
        }

        public EventoServicoValor getEventoServicoValor() {
            return eventoServicoValor;
        }

        public void setEventoServicoValor(EventoServicoValor eventoServicoValor) {
            this.eventoServicoValor = eventoServicoValor;
        }

    }

    public class Parcelas {

        private String vencimento;
        private String valor;
        private Boolean baixado;
        private Movimento movimento;

        public Parcelas() {
            this.vencimento = "";
            this.valor = "";
            this.baixado = null;
            this.movimento = null;
        }

        public Parcelas(String vencimento, String valor, Boolean baixado, Movimento movimento) {
            this.vencimento = vencimento;
            this.valor = valor;
            this.baixado = baixado;
            this.movimento = movimento;
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
    }

}
