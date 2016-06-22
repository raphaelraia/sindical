package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.financeiro.FStatus;
import br.com.rtools.financeiro.FechamentoCaixa;
import br.com.rtools.financeiro.TransferenciaCaixa;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.impressao.ParametroCaixaAnalitico;
import br.com.rtools.impressao.ResumoFechamentoCaixa;
import br.com.rtools.impressao.beans.ImprimirFechamentoCaixa;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public final class FechamentoCaixaBean implements Serializable {

    private int idCaixa = 0;
    private final List<SelectItem> listaCaixa = new ArrayList();
    private int idCaixaDestino = 0;
    private List<SelectItem> listaCaixaDestino = new ArrayList();
    private FechamentoCaixa fechamento = new FechamentoCaixa();
    private String valor = "";
    private String valorTransferencia = "";
    //private String saldoAnterior = "";
    private String saldoAtual = "0,00";
    private String dataSaldo = "";
    private ContaSaldo contaSaldo = new ContaSaldo();
    private List<DataObject> listaFechamento = new ArrayList();
    private String dataResumoFechamento = DataHoje.data();

    private final ConfiguracaoFinanceiroBean cfb = new ConfiguracaoFinanceiroBean();

    public FechamentoCaixaBean() {
        cfb.init();
        getListaCaixa();
        loadListaFechamento();
    }

    public void loadListaFechamento() {
        listaFechamento.clear();
        if (!listaCaixa.isEmpty() && Integer.valueOf(listaCaixa.get(idCaixa).getDescription()) != 0) {
            FinanceiroDao db = new FinanceiroDao();
            Caixa caixa = (Caixa) (new Dao().find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription())));
            if (caixa == null) {
                return;
            }

            List<Vector> lista = db.listaFechamentoCaixa(caixa.getId());

            for (int i = 0; i < lista.size(); i++) {
                int status = 0;
                float soma = 0;
                if (Moeda.converteUS$(lista.get(i).get(2).toString()) > Moeda.converteUS$(lista.get(i).get(3).toString())) {
                    status = 1;
                    soma = Moeda.subtracaoValores(Moeda.converteUS$(lista.get(i).get(2).toString()), Moeda.converteUS$(lista.get(i).get(3).toString()));
                } else if (Moeda.converteUS$(lista.get(i).get(2).toString()) < Moeda.converteUS$(lista.get(i).get(3).toString())) {
                    status = 2;
                    soma = Moeda.subtracaoValores(Moeda.converteUS$(lista.get(i).get(3).toString()), Moeda.converteUS$(lista.get(i).get(2).toString()));
                }
                listaFechamento.add(new DataObject(lista.get(i),
                        DataHoje.converteData((Date) lista.get(i).get(4)), // DATA
                        lista.get(i).get(5).toString(), // HORA
                        Moeda.converteR$(lista.get(i).get(2).toString()), // VALOR FECHAMENTO
                        Moeda.converteR$(lista.get(i).get(3).toString()), // VALOR INFORMADO
                        status,// STATUS
                        Moeda.converteR$Float(soma),
                        null,
                        null,
                        null
                ));
            }
            loadDataFechamento();
        }
    }

    public void loadDataFechamento() {
        FinanceiroDao db = new FinanceiroDao();
        String d = db.dataFechamentoCaixa(Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));
        if (d.isEmpty()) {
            fechamento.setData(DataHoje.data());
        } else {
            fechamento.setData(d);
        }
    }

    public void transferirParaCentral() {
        if (fechamento.getId() == -1) {
            return;
        }

        CaixaFechadoBean cf = new CaixaFechadoBean();

        cf.transferirCaixaGenerico(fechamento.getId(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()), valorTransferencia);

        fechamento = new FechamentoCaixa();
        loadListaFechamento();
    }

    public boolean permissaoFechamentoCaixa() {
        ControleAcessoBean cab = new ControleAcessoBean();
        MacFilial mac = MacFilial.getAcessoFilial();

        if (mac != null && mac.getId() != -1 && mac.getCaixa() != null) {
            if (Integer.valueOf(listaCaixa.get(idCaixa).getDescription()) != mac.getCaixa().getId() && cab.getBotaoFecharCaixaOutroUsuario()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void analitico(DataObject linha) {
        FinanceiroDao db = new FinanceiroDao();

        // id_fechamento_caixa
        List<Vector> result = db.listaRelatorioAnalitico((Integer) ((Vector) linha.getArgumento0()).get(1));
        Collection lista = new ArrayList();

        for (int i = 0; i < result.size(); i++) {
            lista.add(new ParametroCaixaAnalitico(
                    result.get(i).get(0).toString(),
                    DataHoje.converteData((Date) result.get(i).get(1)),
                    (result.get(i).get(2) == null) ? "" : result.get(i).get(2).toString(),
                    (result.get(i).get(3) == null) ? "" : result.get(i).get(3).toString(),
                    (result.get(i).get(4) == null) ? "" : result.get(i).get(4).toString(),
                    (result.get(i).get(5) == null) ? "" : result.get(i).get(5).toString(),
                    (result.get(i).get(6) == null) ? "" : result.get(i).get(6).toString(),
                    (result.get(i).get(7) == null) ? "" : result.get(i).get(7).toString(),
                    (result.get(i).get(8) == null) ? "" : result.get(i).get(8).toString(),
                    BigDecimal.valueOf(Double.valueOf(String.valueOf(Moeda.converteUS$(result.get(i).get(9).toString())))),
                    BigDecimal.valueOf(Double.valueOf(String.valueOf(Moeda.converteUS$(result.get(i).get(10).toString())))),
                    DataHoje.converteData((Date) result.get(i).get(12))
            )
            );
        }

        try {
            Jasper.PATH = "downloads";
            Jasper.PART_NAME = "";
            Jasper.printReports("/Relatorios/CAIXA_ANALITICO.jasper", "analitico", lista);
//            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/CAIXA_ANALITICO.jasper"));
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);
//
//            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dtSource);
//            byte[] arquivo = JasperExportManager.exportReportToPdf(jasperPrint);
//            
//            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//            res.setContentType("application/pdf");
//            res.setHeader("Content-disposition", "inline; filename=\"Relatório Caixa Analítico.pdf\"");
//            res.getOutputStream().write(arquivo);
//            res.getCharacterEncoding();
//            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void resumoFechamentoCaixa() {
        if (dataResumoFechamento.isEmpty()) {
            return;
        }

        FinanceiroDao db = new FinanceiroDao();

        // data do fechamento
        List<Vector> result = db.listaResumoFechamentoCaixa(dataResumoFechamento);
        //result.addAll(db.listaResumoFechamentoCaixa("09/02/2015"));
        Collection lista = new ArrayList();

        for (int i = 0; i < result.size(); i++) {
            lista.add(new ResumoFechamentoCaixa(
                    DataHoje.converteData((Date) result.get(i).get(0)),
                    (result.get(i).get(1) == null) ? "" : (result.get(i).get(1).equals("E")) ? "ENTRADA" : "SAÍDA",
                    (result.get(i).get(2) == null) ? "" : result.get(i).get(2).toString(),
                    (result.get(i).get(3) == null) ? "" : result.get(i).get(3).toString(),
                    (result.get(i).get(4) == null) ? "" : result.get(i).get(4).toString(),
                    BigDecimal.valueOf(Double.valueOf(String.valueOf(Moeda.converteUS$(result.get(i).get(5).toString())))),
                    result.get(i).get(6),
                    result.get(i).get(7),
                    result.get(i).get(8)
            )
            );
        }

        try {
            Jasper.PATH = "downloads";
            Jasper.PART_NAME = "";
            Jasper.printReports("/Relatorios/RESUMO_FECHAMENTO_CAIXA.jasper", "fechamento_caixa", lista);

//            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/RESUMO_FECHAMENTO_CAIXA.jasper"));
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);
//
//            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dtSource);
//            byte[] arquivo = JasperExportManager.exportReportToPdf(jasperPrint);
//            
//            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//            res.setContentType("application/pdf");
//            res.setHeader("Content-disposition", "inline; filename=\"Resumo Fechamento Caixa.pdf\"");
//            res.getOutputStream().write(arquivo);
//            res.getCharacterEncoding();
//            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void imprimir(DataObject linha) {
        ImprimirFechamentoCaixa ifc = new ImprimirFechamentoCaixa();
        Integer id_fechamento;

        if (linha != null) {
            id_fechamento = (Integer) ((Vector) linha.getArgumento0()).get(1);
        } else {
            id_fechamento = fechamento.getId();
        }

        ifc.imprimir(id_fechamento, Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));

    }

    public void transferir() {
        if (!listaCaixa.isEmpty() && Integer.valueOf(listaCaixa.get(idCaixa).getDescription()) == 0) {
            GenericaMensagem.error("Erro", "Lista de Caixa incompleta!");
            return;
        }

        if (!listaCaixaDestino.isEmpty() && Integer.valueOf(listaCaixaDestino.get(idCaixa).getDescription()) == 0) {
            GenericaMensagem.error("Erro", "Lista de Caixa Destino incompleta!");
            return;
        }

        Dao dao = new Dao();
        Caixa caixa = (Caixa) dao.find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));
        Caixa caixa_destino = (Caixa) dao.find(new Caixa(), Integer.valueOf(listaCaixaDestino.get(idCaixaDestino).getDescription()));

        FinanceiroDao db = new FinanceiroDao();
        List<Vector> result_entrada = db.listaMovimentoCaixa(caixa.getId(), "E", null, fechamento.getData());
        List<TransferenciaCaixa> lEntrada = db.listaTransferenciaEntrada(caixa.getId(), null, fechamento.getData());

        if (result_entrada.isEmpty() && lEntrada.isEmpty()) {
            GenericaMensagem.warn("Erro", "Não existe movimentos para este Caixa!");
            return;

        }

        dao.openTransaction();
        TransferenciaCaixa tc = new TransferenciaCaixa(
                -1,
                caixa,
                Moeda.converteUS$(valorTransferencia),
                caixa_destino,
                DataHoje.dataHoje(),
                (FStatus) dao.find(new FStatus(), 13),
                null,
                null,
                (Usuario) GenericaSessao.getObject("sessaoUsuario")
        );

        if (!dao.save(tc)) {
            GenericaMensagem.warn("Erro", "Não foi possivel completar transferência!");
            dao.rollback();
        }

        dao.commit();
        GenericaMensagem.info("Sucesso", "Dinheiro transferido com Sucesso!");
        valorTransferencia = "0,00";
    }

    public void excluir(Caixa c) {
        Dao dao = new Dao();

        dao.openTransaction();
    }

    public void salvar() {
        Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");

        if (usuario == null) {
            GenericaMensagem.warn("Erro", "Faça o login novamente!");
            return;
        }

        FinanceiroDao db = new FinanceiroDao();

        Dao dao = new Dao();
        Caixa caixa = (Caixa) dao.find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));

        // SE CAIXA FOR IGUAL A 01 FECHAR APENAS SE NÃO HOUVER OUTRO CAIXA ABERTO
        if (caixa.getCaixa() == 1) {
            List result = (List) db.listaQuantidadeCaixasAberto(fechamento.getData());
            if (result.get(0) == null || ((Integer) result.get(0)) > 0) {
                GenericaMensagem.warn("Erro", "Ainda existe caixa a ser fechado!");
                return;
            }
        }

        if (!db.listaFechamentoCaixaTransferencia(caixa.getId()).isEmpty()) {
            GenericaMensagem.warn("Erro", "Seu caixa ainda NÃO FOI TRANSFERIDO, caixa não pode ser fechado!");
            return;
        }

        ControleAcessoBean cab = new ControleAcessoBean();
        List<Vector> result_entrada;
        List<Vector> result_saida;
        List<TransferenciaCaixa> lEntrada;
        List<TransferenciaCaixa> lSaida;

        // true NÃO TEM PERMISSÃO
        boolean permissao = cab.getBotaoFecharCaixaOutroUsuario();

        result_entrada = db.listaMovimentoCaixa(caixa.getId(), "E", null, fechamento.getData());
        result_saida = db.listaMovimentoCaixa(caixa.getId(), "S", null, fechamento.getData());
        lEntrada = db.listaTransferenciaEntrada(caixa.getId(), null, fechamento.getData());
        lSaida = db.listaTransferenciaSaida(caixa.getId(), null, fechamento.getData());

        if (result_entrada.isEmpty() && result_saida.isEmpty() && lEntrada.isEmpty() && lSaida.isEmpty()) {
            GenericaMensagem.warn("Erro", "Não existe movimentos para este Caixa!");
            return;
        }

        // true NÃO TEM PERMISSÃO
        if (permissao) {
            List<Vector> result_entrada_user = db.listaMovimentoCaixa(caixa.getId(), "E", usuario.getId(), fechamento.getData());
            List<Vector> result_saida_user = db.listaMovimentoCaixa(caixa.getId(), "S", usuario.getId(), fechamento.getData());
            List<TransferenciaCaixa> lEntrada_user = db.listaTransferenciaEntrada(caixa.getId(), usuario.getId(), fechamento.getData());
            List<TransferenciaCaixa> lSaida_user = db.listaTransferenciaSaida(caixa.getId(), usuario.getId(), fechamento.getData());

            if (result_entrada_user.isEmpty() && result_saida_user.isEmpty() && lEntrada_user.isEmpty() && lSaida_user.isEmpty()) {
                GenericaMensagem.warn("Erro", "Usuário não efetuou recebimento neste Caixa!");
                return;
            }
        }

        dao.openTransaction();

        fechamento.setUsuario(usuario);
        //fechamento.setSaldoAtual(Moeda.converteUS$(saldoAtual));
        if (!dao.save(fechamento)) {
            GenericaMensagem.warn("Erro", "Não foi possivel concluir este fechamento!");
            dao.rollback();
            fechamento = new FechamentoCaixa();
            return;
        }

        if (!result_entrada.isEmpty()) {
            float valorx = 0;
            for (int i = 0; i < result_entrada.size(); i++) {
                Baixa ba = (Baixa) dao.find(new Baixa(), (Integer) result_entrada.get(i).get(8));
                ba.setFechamentoCaixa(fechamento);

                valorx = Moeda.somaValores(valorx, Float.parseFloat(Double.toString((Double) result_entrada.get(i).get(6))));
                if (!dao.update(ba)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel alterar a Baixa!");
                    dao.rollback();
                    fechamento = new FechamentoCaixa();
                    return;
                }
            }
            fechamento.setValorFechamento(valorx);
            dao.update(fechamento);
        }

        if (!result_saida.isEmpty()) {
            float valorx = 0;
            for (int i = 0; i < result_saida.size(); i++) {
                Baixa ba = (Baixa) dao.find(new Baixa(), (Integer) result_saida.get(i).get(8));
                ba.setFechamentoCaixa(fechamento);

                valorx = Moeda.somaValores(valorx, Float.parseFloat(Double.toString((Double) result_saida.get(i).get(6))));
                if (!dao.update(ba)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel alterar a Baixa!");
                    dao.rollback();
                    fechamento = new FechamentoCaixa();
                    return;
                }
            }
            //fechamento.setValorFechamento(valorx);
            fechamento.setValorFechamento(Moeda.subtracaoValores(fechamento.getValorFechamento(), valorx));
            dao.update(fechamento);
        }

        if (!lEntrada.isEmpty()) {
            float valorx = 0;
            for (int i = 0; i < lEntrada.size(); i++) {
                TransferenciaCaixa tc = (TransferenciaCaixa) dao.find(new TransferenciaCaixa(), lEntrada.get(i).getId());
                tc.setFechamentoEntrada(fechamento);

                valorx = Moeda.somaValores(valorx, tc.getValor());
                if (!dao.update(tc)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel alterar a entrada de Transferência entre Caixas!");
                    dao.rollback();
                    fechamento = new FechamentoCaixa();
                    return;
                }
            }
            fechamento.setValorFechamento(Moeda.somaValores(fechamento.getValorFechamento(), valorx));
            dao.update(fechamento);
        }

        if (!lSaida.isEmpty()) {
            float valorx = 0;//fechamento.getValorFechamento();
            for (int i = 0; i < lSaida.size(); i++) {
                TransferenciaCaixa tc = (TransferenciaCaixa) dao.find(new TransferenciaCaixa(), lSaida.get(i).getId());
                tc.setFechamentoSaida(fechamento);

                valorx = Moeda.somaValores(valorx, tc.getValor());
                if (!dao.update(tc)) {
                    GenericaMensagem.warn("Erro", "Não foi possivel alterar a saída de Transferência entre Caixas!");
                    dao.rollback();
                    fechamento = new FechamentoCaixa();
                    return;
                }
            }
            fechamento.setValorFechamento(Moeda.subtracaoValores(fechamento.getValorFechamento(), valorx));
            dao.update(fechamento);
        }

        fechamento.setValorFechamento(Moeda.somaValores(fechamento.getValorFechamento(), Moeda.converteUS$(saldoAtual)));

        // CALCULO PARA SOMAR OS VALORES DA QUERY
        if (cfb.getConfiguracaoFinanceiro().isAlterarValorFechamento()) {
            fechamento.setValorInformado(Moeda.converteUS$(valor));
        } else {
            fechamento.setValorInformado(fechamento.getValorFechamento());
        }

        dao.update(fechamento);

        dao.commit();
        GenericaMensagem.info("Sucesso", "Fechamento de Caixa concluído!");

        // CAIXA 01 NÃO PODE SER TRANSFERIDO AUTOMÁTICAMENTE
        if (caixa.getCaixa() != 1) {
            // TRANSFERE CAIXA AUTOMATICO
            CaixaFechadoBean cf = new CaixaFechadoBean();
            if (cfb.getConfiguracaoFinanceiro().isTransferenciaAutomaticaCaixa()) {
                if (cfb.getConfiguracaoFinanceiro().isModalTransferencia()) {
                    //valorTransferencia = Moeda.converteR$Float(Moeda.subtracaoValores(fechamento.getValorFechamento(), caixa.getFundoFixo()));
                    
                    // ROGÉRIO QUER QUE TRANSFERE ZERO CASO O VALOR SEJA NEGATIVO
                    if (fechamento.getValorInformado() < 0)
                        valorTransferencia = Moeda.converteR$Float(0);
                    else
                        valorTransferencia = cf.somaValorTransferencia(fechamento, caixa);
                    
                    PF.openDialog("i_dlg_transferir");
                    PF.update(":i_panel_transferencia");
                } else {
                    //valorTransferencia = Moeda.converteR$Float(Moeda.subtracaoValores(fechamento.getValorFechamento(), caixa.getFundoFixo()));
                    // ROGÉRIO QUER QUE TRANSFERE ZERO CASO O VALOR SEJA NEGATIVO
                    if (fechamento.getValorInformado() < 0)
                        valorTransferencia = Moeda.converteR$Float(0);
                    else
                        valorTransferencia = cf.somaValorTransferencia(fechamento, caixa);
                    
                    transferirParaCentral();
                }
            }
        }

        //fechamento = new FechamentoCaixa();
        //listaFechamento.clear();
        loadListaFechamento();
        valor = "0,00";
    }

    public List<SelectItem> getListaCaixa() {
        if (listaCaixa.isEmpty()) {
            ControleAcessoBean cab = new ControleAcessoBean();
            boolean permissao = cab.getBotaoFecharCaixaOutroUsuario();
            Caixa cx;
            Usuario usuario = ((Usuario) GenericaSessao.getObject("sessaoUsuario"));

            // TRUE é igual NÃO ter permissão
            if (usuario.getId() != 1 && permissao) {
                MacFilial mac = MacFilial.getAcessoFilial();
                if (!cfb.getConfiguracaoFinanceiro().isCaixaOperador()) {
                    if (mac.getId() == -1 || mac.getCaixa() == null || mac.getCaixa().getId() == -1) {
                        listaCaixa.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
                        return listaCaixa;
                    }

                    cx = mac.getCaixa();
                } else {
                    if (mac == null) {
                        listaCaixa.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
                        return listaCaixa;
                    }

                    FinanceiroDao dbf = new FinanceiroDao();
                    cx = dbf.pesquisaCaixaUsuario(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId(), mac.getFilial().getId());

                    if (cx == null) {
                        listaCaixa.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
                        return listaCaixa;
                    }
                }

                // TRUE é igual NÃO ter permissão
                if (permissao) {
                    listaCaixa.add(
                            new SelectItem(
                                    0,
                                    cx.getCaixa() + " - " + cx.getDescricao(),
                                    Integer.toString(cx.getId())
                            )
                    );
                } else {
                    List<Caixa> list = new FinanceiroDao().listaCaixa();
                    if (!list.isEmpty()) {

                        // TRUE é igual não ter permissão
                        for (int i = 0; i < list.size(); i++) {

                            listaCaixa.add(
                                    new SelectItem(i,
                                            list.get(i).getCaixa() + " - " + list.get(i).getDescricao(),
                                            Integer.toString(list.get(i).getId())));
                        }
                    } else {
                        listaCaixa.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getId() == cx.getId()) {
                            idCaixa = i;
                        }
                    }
                }
            } else {
                List<Caixa> list = new FinanceiroDao().listaCaixa();
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        listaCaixa.add(
                                new SelectItem(i,
                                        list.get(i).getCaixa() + " - " + list.get(i).getDescricao(),
                                        Integer.toString(list.get(i).getId())));
                    }
                } else {
                    listaCaixa.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
                }
            }
        }
        return listaCaixa;
    }

    public int getIdCaixa() {
        return idCaixa;
    }

    public void setIdCaixa(int idCaixa) {
        this.idCaixa = idCaixa;
    }

    public FechamentoCaixa getFechamento() {
        return fechamento;
    }

    public void setFechamento(FechamentoCaixa fechamento) {
        this.fechamento = fechamento;
    }

    public String getValorTransferencia() {
        return Moeda.converteR$(valorTransferencia);
    }

    public void setValorTransferencia(String valorTransferencia) {
        this.valorTransferencia = Moeda.substituiVirgula(valorTransferencia);
    }

    public String getValor() {
        return Moeda.converteR$(valor);
    }

    public void setValor(String valor) {
        this.valor = Moeda.substituiVirgula(valor);
    }

    public int getIdCaixaDestino() {
        return idCaixaDestino;
    }

    public void setIdCaixaDestino(int idCaixaDestino) {
        this.idCaixaDestino = idCaixaDestino;
    }

    public List<SelectItem> getListaCaixaDestino() {
        if ((listaCaixaDestino == null || listaCaixaDestino.isEmpty()) && (!listaCaixa.isEmpty() && Integer.valueOf(listaCaixa.get(idCaixa).getDescription()) != 0)) {
            Dao dao = new Dao();
            List<Caixa> list = dao.list(new Caixa());
            Caixa caixa = (Caixa) dao.find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));

            if (!list.isEmpty() && caixa != null) {
                if (listaCaixaDestino == null) {
                    listaCaixaDestino = new ArrayList();
                }
                for (int i = 0; i < list.size(); i++) {
                    listaCaixaDestino.add(new SelectItem(i,
                            list.get(i).getCaixa() + " - " + list.get(i).getDescricao(),
                            Integer.toString(list.get(i).getId())));
                    if (list.get(i).getId() == caixa.getId()) {
                        idCaixaDestino = i;
                    }
                }
            } else {
                listaCaixaDestino.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
            }
        } else if (listaCaixaDestino.isEmpty()) {
            listaCaixaDestino.add(new SelectItem(0, "Nenhum Caixa Encontrado", "0"));
        }
        return listaCaixaDestino;
    }

    public void setListaCaixaDestino(List<SelectItem> listaCaixaDestino) {
        this.listaCaixaDestino = listaCaixaDestino;
    }

    public List<DataObject> getListaFechamento() {
        return listaFechamento;
    }

    public void setListaFechamento(List<DataObject> listaFechamento) {
        this.listaFechamento = listaFechamento;
    }

    public String getSaldoAtual() {
        if (!listaCaixa.isEmpty() && Integer.valueOf(listaCaixa.get(idCaixa).getDescription()) != 0) {
            Caixa caixa = (Caixa) new Dao().find(new Caixa(), Integer.valueOf(listaCaixa.get(idCaixa).getDescription()));
            if (caixa == null) {
                return saldoAtual = "0,00";
            }

            FinanceiroDao db = new FinanceiroDao();
            List<Vector> lista = db.pesquisaSaldoAtual(caixa.getId());

            if (!lista.isEmpty()) {
                saldoAtual = Moeda.converteR$(lista.get(0).get(1).toString());
                dataSaldo = DataHoje.converteData((Date) lista.get(0).get(2));
            } else {
                saldoAtual = "0,00";
                dataSaldo = "";
            }
        }
        return Moeda.converteR$(saldoAtual);
    }

    public void setSaldoAtual(String saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public String getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(String dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public String getDataResumoFechamento() {
        return dataResumoFechamento;
    }

    public void setDataResumoFechamento(String dataResumoFechamento) {
        this.dataResumoFechamento = dataResumoFechamento;
    }

    public ConfiguracaoFinanceiroBean getCfb() {
        return cfb;
    }
}
