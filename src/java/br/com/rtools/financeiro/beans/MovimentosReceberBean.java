package br.com.rtools.financeiro.beans;

import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.financeiro.Impressao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.MovimentoReceberDao;
import br.com.rtools.financeiro.lista.ListMovimentoReceber;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.sistema.BloqueioRotina;
import br.com.rtools.sistema.dao.BloqueioRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MovimentosReceberBean extends MovimentoValorBean implements Serializable {

    private List<DataObject> listaMovimentos;
    private List<ListMovimentoReceber> listMovimentoReceber;
    private Pessoa pessoa;
    private String multa;
    private String juros;
    private String correcao;
    private String desconto;
    private String descontoTela;
    private String total;
    private String acrescimo;
    private String acrescimoSemSindical;
    private String totalSemSindical;
    private boolean marcarTodos;
    private int index;
    private BloqueioRotina bloqueioRotina;
    private ConfiguracaoArrecadacaoBean cab;

    @PostConstruct
    public void init() {
        listaMovimentos = new ArrayList<>();
        listMovimentoReceber = new ArrayList<>();
        bloqueioRotina = null;
        pessoa = new Pessoa();
        multa = "0";
        juros = "0";
        correcao = "0";
        desconto = "0";
        descontoTela = "0";
        total = "0";
        acrescimo = "0";
        acrescimoSemSindical = "0";
        totalSemSindical = "0";
        marcarTodos = false;
        index = 0;

        cab = new ConfiguracaoArrecadacaoBean();
        cab.init();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("movimentosReceberBean");
        GenericaSessao.remove("movimentoValorBean");
        GenericaSessao.remove("pessoaPesquisa");
        GenericaSessao.remove("juridicaBean");
    }

    public String imprimirPlanilha() {
        PlanilhaDebitoBean.printNoNStatic(listMovimentoReceber);
        return null;
    }

    public String imprimir() {
        MovimentoReceberDao db = new MovimentoReceberDao();
        List<Movimento> lista = new ArrayList();
        List<Float> listaValores = new ArrayList();
        List<String> listaVencimentos = new ArrayList();

        if (!listMovimentoReceber.isEmpty()) {
            Movimento mov = new Movimento();
            SegurancaUtilitariosBean su = new SegurancaUtilitariosBean();
            Dao dao = new Dao();
            dao.openTransaction();
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    mov = (Movimento) dao.find(new Movimento(), Integer.parseInt(String.valueOf(listMovimentoReceber.get(i).getIdMovimento())));
                    if (mov.getTipoServico().getId() == 4 && Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculado()) <= 0) {
                        GenericaMensagem.warn("Validação", "Acordo sem salvar!");
                        return null;
                    }

                    mov.setMulta(Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()));
                    mov.setJuros(Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros()));
                    mov.setCorrecao(Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));
                    mov.setDesconto(Moeda.converteUS$((String) listMovimentoReceber.get(i).getDesconto()));
                    listaValores.add(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculado()));
                    if (DataHoje.converteDataParaInteger(mov.getVencimento()) < DataHoje.converteDataParaInteger(DataHoje.data())) {
                        DataHoje d = new DataHoje();
                        String novaData = d.incrementarMesesUltimoDia(1, d.decrementarMeses(1, DataHoje.data()));
                        listaVencimentos.add(novaData);
                    } else {
                        listaVencimentos.add(mov.getVencimento());
                    }
                    lista.add(mov);
                    Impressao impressao = new Impressao();
                    impressao.setUsuario(su.getSessaoUsuario());
                    impressao.setDtVencimento(DataHoje.converte(listaVencimentos.get(listaVencimentos.size() - 1)));
                    impressao.setMovimento(mov);
                    if (!dao.save(impressao)) {
                        dao.rollback();
                        return null;
                    }
                }
            }
            dao.commit();
        }

        if (lista.isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum boleto selecionado!");
            return null;
        }
        ImprimirBoleto imp = new ImprimirBoleto();
        lista = imp.atualizaContaCobrancaMovimento(lista);
        imp.imprimirBoleto(lista, listaValores, listaVencimentos, false);
        imp.visualizar(null);
        listaMovimentos.clear();
        listMovimentoReceber.clear();
        return null;
    }

    public String telaAcordo() {
        BloqueioRotinaDao bloqueioRotinaDao = new BloqueioRotinaDao();
        bloqueioRotina = bloqueioRotinaDao.existUsuarioRotinaPessoa(95, pessoa.getId());
        if (bloqueioRotina != null) {
            if (bloqueioRotina.getUsuario().getId() != ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId()) {
                pessoa = new Pessoa();
                listMovimentoReceber.clear();
                GenericaMensagem.warn("Empresa em processo de acordo", "Responsável pelo acordo: " + bloqueioRotina.getUsuario().getPessoa().getNome());
                return null;
            }
        }
        Dao dao = new Dao();
        List lista = new ArrayList();
        ///MovimentoReceberDao db = new MovimentoReceberDao();
        Movimento movimento = new Movimento();
        boolean err = false, err_2 = false;
        if (!listMovimentoReceber.isEmpty()) {
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    Movimento m = (Movimento) dao.find(new Movimento(), Integer.parseInt(listMovimentoReceber.get(i).getIdMovimento()));
                    if (m.getAcordo() != null) {
                        GenericaMensagem.warn("Boleto " + listMovimentoReceber.get(i).getBoleto() + " já acordado", "Data do acordo: " + m.getAcordo().getData() + " - Usuário: " + m.getAcordo().getUsuario().getPessoa().getNome());
                        err = true;
                    }

                    if (cab.getConfiguracaoArrecadacao().getNrDiasAcordo() != 0) {
                        String data_para_acordo = new DataHoje().incrementarDias(cab.getConfiguracaoArrecadacao().getNrDiasAcordo(), m.getVencimento());
                        // SE A DATA DE VENCIMENTO + OS DIAS DE ACORDO ex. 01/01/2000 + [30] dias FOR MENOR QUE A DATA ATUAL (existe um movimento vencido a mais que [30] dias) 

                        // CASO NÃO TENHA NENHUM MOVIMENTO VENCIDO MAIS QUE [30] dias ENTÃO RETORNAR true, E NÃO PERMITIR O ACORDO
                        // SENDO ASSIM É OBRIGATÓRIO PELO MENOS UM DOS MOVIMENTOS SELECIONADOS ESTAREM VENCIDOS MAIS QUE [30] dias
                        if (DataHoje.menorData(data_para_acordo, DataHoje.data())) {
                            err_2 = true;
                        }
                    }
                    //m = new Movimento();
                }
            }

            if (err) {
                return null;
            }

            if (cab.getConfiguracaoArrecadacao().getNrDiasAcordo() != 0 && !err_2) {
                GenericaMensagem.warn("ATENÇÃO", "NENHUM BOLETO VENCIDO A MAIS QUE " + cab.getConfiguracaoArrecadacao().getNrDiasAcordo() + " DIAS");
                return null;
            }

            dao.openTransaction();
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    movimento = (Movimento) dao.find(new Movimento(), Integer.parseInt(String.valueOf(listMovimentoReceber.get(i).getIdMovimento())));
                    if (movimento.getTipoServico().getId() == 4) {
                        GenericaMensagem.warn("Notificação", "Não é possível criar este acordo novamente!");
                        return null;
                    }
                    if (Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculado()) <= 0) {
                        GenericaMensagem.warn("Validação", "Não é possível criar acordo com valores zerados!");
                        return null;
                    }
                    movimento.setValor(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorMovimento()));
                    if (!dao.update(movimento)) {
                        GenericaMensagem.warn("Erro", "Ao alterar valor do Movimento id: " + movimento.getId());
                        dao.rollback();
                    }
                    movimento.setMulta(Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()));
                    movimento.setJuros(Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros()));
                    movimento.setCorrecao(Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));
                    movimento.setDesconto(Moeda.converteUS$((String) listMovimentoReceber.get(i).getDesconto()));
                    movimento.setValorBaixa(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculado()));
                    lista.add(movimento);
                }
            }
            dao.commit();
            if (!lista.isEmpty()) {
                GenericaSessao.put("listaMovimento", lista);
                return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).acordo();
            } else {
                GenericaMensagem.warn("Validação", "Nenhum boleto selecionado!");
            }
        } else {
            GenericaMensagem.warn("Validação", "Lista vazia");
        }
        return null;
    }

    public String telaBaixa() {
        List<Movimento> list = new ArrayList<>();
        Dao dao = new Dao();
        Movimento movimento = new Movimento();
        if (!listMovimentoReceber.isEmpty()) {
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    movimento = (Movimento) dao.find(new Movimento(), Integer.parseInt(String.valueOf(listMovimentoReceber.get(i).getIdMovimento())));
                    movimento.setMulta(Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()));
                    movimento.setJuros(Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros()));
                    movimento.setCorrecao(Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));
                    movimento.setDesconto(Moeda.converteUS$((String) listMovimentoReceber.get(i).getDesconto()));
                    movimento.setValor(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculado()));
                    movimento.setValorBaixa(movimento.getValor());
                    list.add(movimento);
                }
            }
            if (!list.isEmpty()) {
                GenericaSessao.put("listaMovimento", list);
                return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).baixaGeral();
            } else {
                GenericaMensagem.warn("Validação", "Nenhum boleto selecionado!");
            }
        } else {
            GenericaMensagem.warn("Validação", "Lista vazia!");
        }
        return null;
    }

    public void addIndex(int ix) {
        index = ix;
    }

    @Override
    public synchronized void carregarFolha() {
        if (!listMovimentoReceber.isEmpty()) {
            Dao dao = new Dao();
            Movimento movimento = (Movimento) dao.find(new Movimento(), Integer.parseInt(listMovimentoReceber.get(index).getIdMovimento()));
            super.carregarFolha(movimento);
        }
    }

    @Override
    public void atualizaValorGrid(String tipo) {
        Dao dao = new Dao();
        Movimento m = (Movimento) dao.find(new Movimento(), Integer.parseInt(listMovimentoReceber.get(index).getIdMovimento()));
        BloqueioRotinaDao bloqueioRotinaDao = new BloqueioRotinaDao();
        bloqueioRotina = bloqueioRotinaDao.existUsuarioRotinaPessoa(95, pessoa.getId());
        if (bloqueioRotina == null) {
            bloqueioRotina = new BloqueioRotina();
            bloqueioRotina.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));
            bloqueioRotina.setRotina((Rotina) dao.find(new Rotina(), 95));
            bloqueioRotina.setPessoa(m.getPessoa());
            bloqueioRotina.setBloqueio(DataHoje.dataHoje());
            dao.save(bloqueioRotina, true);
        } else if (bloqueioRotina.getUsuario().getId() != ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId()) {
            pessoa = new Pessoa();
            listMovimentoReceber.clear();
            GenericaMensagem.warn("Empresa em processo de acordo", "Responsável pelo acordo: " + bloqueioRotina.getUsuario().getPessoa().getNome());
            return;
        }
        listMovimentoReceber.get(index).setValorMovimento(super.atualizaValor(true, tipo));
        listMovimentoReceber.clear(); // LIMPANDO AQUI PARA ATUALIZAR O VALOR CALCULADO
        desconto = "0";
    }

    public void selected() {
        for (int j = 0; j < listMovimentoReceber.size(); j++) {
            listMovimentoReceber.get(j).setSelected(marcarTodos);
        }
    }

    public void selectedAll() {
        for (int i = 0; i < listMovimentoReceber.size(); i++) {
            listMovimentoReceber.get(i).setSelected(marcarTodos);
        }
    }

    public void blockAcordo() {
        Dao dao = new Dao();
        Movimento m = (Movimento) dao.find(new Movimento(), Integer.parseInt(listMovimentoReceber.get(index).getIdMovimento()));
        BloqueioRotinaDao bloqueioRotinaDao = new BloqueioRotinaDao();
        bloqueioRotina = bloqueioRotinaDao.existUsuarioRotinaPessoa(95, pessoa.getId());
        if (bloqueioRotina == null) {
            bloqueioRotina = new BloqueioRotina();
            bloqueioRotina.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));
            bloqueioRotina.setRotina((Rotina) dao.find(new Rotina(), 95));
            bloqueioRotina.setPessoa(m.getPessoa());
            bloqueioRotina.setBloqueio(DataHoje.dataHoje());
            dao.save(bloqueioRotina, true);
        } else if (bloqueioRotina.getUsuario().getId() != ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId()) {
            pessoa = new Pessoa();
            listMovimentoReceber.clear();
            GenericaMensagem.warn("Empresa em processo de acordo", "Responsável pelo acordo: " + bloqueioRotina.getUsuario().getPessoa().getNome());
        }

    }

    public void calcula() {
        if (!listMovimentoReceber.isEmpty()) {
        }
    }

    public String getTotal() {
        if (!listMovimentoReceber.isEmpty()) {
            float soma = 0;
            float somaS = 0;
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    if (listMovimentoReceber.get(i).getServico().toUpperCase().equals("SINDICAL")) {
                        somaS = Moeda.somaValores(soma, Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculadoOriginal()));
                    }

                    soma = Moeda.somaValores(soma, Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculadoOriginal()));
                }
            }
            totalSemSindical = Moeda.converteR$Float(Moeda.subtracaoValores(soma, somaS));
            return total = Moeda.converteR$Float(soma);
        } else {
            return "0";
        }
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getMulta() {
        if (!listMovimentoReceber.isEmpty()) {
            float soma = 0;
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    soma = Moeda.somaValores(soma, Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()));
                }
            }
            return multa = Moeda.converteR$Float(soma);
        } else {
            return "0";
        }
    }

    public void setMulta(String multa) {
        this.multa = multa;
    }

    public String getJuros() {
        if (!listMovimentoReceber.isEmpty()) {
            float soma = 0;
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    soma = Moeda.somaValores(soma, Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros()));
                }
            }
            return juros = Moeda.converteR$Float(soma);
        } else {
            return "0";
        }
    }

    public void setJuros(String juros) {
        this.juros = juros;
    }

    public String getCorrecao() {
        if (!listMovimentoReceber.isEmpty()) {
            float soma = 0;
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (listMovimentoReceber.get(i).getSelected()) {
                    soma = Moeda.somaValores(soma, Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));
                }
            }
            return correcao = Moeda.converteR$Float(soma);
        } else {
            return "0";
        }
    }

    public void setCorrecao(String correcao) {
        this.correcao = correcao;
    }

    public String getAcrescimo() {
        if (!listMovimentoReceber.isEmpty()) {
            return acrescimo = Moeda.converteR$Float(Moeda.somaValores(Moeda.somaValores(Moeda.converteUS$(juros), Moeda.converteUS$(correcao)), Moeda.converteUS$(multa)));
        } else {
            return "0";
        }
    }

    public void setAcrescimo(String acrescimo) {
        this.acrescimo = acrescimo;
    }

    public String getAcrescimoSemSindical() {
        float m = 0;
        float j = 0;
        float c = 0;
        if (!listMovimentoReceber.isEmpty()) {
            for (int i = 0; i < listMovimentoReceber.size(); i++) {
                if (!listMovimentoReceber.get(i).getServico().toUpperCase().equals("SINDICAL") && listMovimentoReceber.get(i).getSelected()) {
                    m = Moeda.somaValores(m, Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()));
                    j = Moeda.somaValores(j, Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros()));
                    c = Moeda.somaValores(c, Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));
                }
            }
            return acrescimoSemSindical = Moeda.converteR$(String.valueOf(Moeda.somaValores(Moeda.somaValores(m, j), c)));
        } else {
            return "0";
        }
    }

    public void setAcrescimoSemSindical(String acrescimoSemSindical) {
        this.acrescimoSemSindical = acrescimoSemSindical;
    }

    public String getTotalPagar() {
        if (!listMovimentoReceber.isEmpty()) {
            if (Moeda.subtracaoValores(Moeda.substituiVirgulaFloat(total), Float.valueOf(desconto)) >= 0) {
                return Moeda.converteR$Float(Moeda.subtracaoValores(Moeda.substituiVirgulaFloat(total), Float.valueOf(desconto)));
            } else {
                return "0,00";
            }
        } else {
            return "0,00";
        }
    }

    public String removerPesquisa() {
        GenericaSessao.remove("pessoaPesquisa");
        pessoa = new Pessoa();
        listaMovimentos.clear();
        listMovimentoReceber.clear();
        desconto = "0";
        bloqueioRotina = null;
        return "movimentosReceber";
    }

    public void calculoDesconto() {
        float desc = 0;
        float acre = 0;

        if (Float.valueOf(desconto) > Moeda.substituiVirgulaFloat(acrescimoSemSindical)) {
            desconto = String.valueOf(Moeda.substituiVirgulaFloat(acrescimoSemSindical));
        }

        for (int i = 0; i < listMovimentoReceber.size(); i++) {
            if (!listMovimentoReceber.get(i).getServico().toUpperCase().equals("SINDICAL") && listMovimentoReceber.get(i).getSelected()) {
                acre = Moeda.somaValores(Moeda.somaValores(Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()), Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros())), Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));

                desc = Moeda.multiplicarValores(Moeda.divisaoValores(Float.valueOf(desconto), Moeda.substituiVirgulaFloat(acrescimoSemSindical)), 100);
                desc = Moeda.divisaoValores(Moeda.multiplicarValores(acre, desc), 100);

                listMovimentoReceber.get(i).setDesconto(Moeda.converteR$Float(desc, 4));
                listMovimentoReceber.get(i).setValorCalculado(Moeda.converteR$Float(Moeda.subtracaoValores(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculadoOriginal()), desc), 4));
            } else {
                listMovimentoReceber.get(i).setDesconto(Moeda.converteR$Float(0));
                listMovimentoReceber.get(i).setValorCalculado(Moeda.converteR$Float(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculadoOriginal())));
            }
        }

    }

    public void limpaLista() {
        listaMovimentos.clear();
        listMovimentoReceber.clear();
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa") || GenericaSessao.exists("juridicaPesquisa")) {
            pessoa = ((Juridica) GenericaSessao.getObject("juridicaPesquisa", true)).getPessoa();
            listaMovimentos.clear();
            listMovimentoReceber.clear();
            desconto = "0";
            BloqueioRotinaDao bloqueioRotinaDao = new BloqueioRotinaDao();
            bloqueioRotina = bloqueioRotinaDao.existUsuarioRotinaPessoa(95, pessoa.getId());
            if (bloqueioRotina != null) {
                if (bloqueioRotina.getUsuario().getId() != ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId()) {
                    GenericaMensagem.warn("Empresa em processo de acordo", "Responsável pelo acordo: " + bloqueioRotina.getUsuario().getPessoa().getNome());
                    pessoa = new Pessoa();
                }
            }
            getListMovimentoReceber();
            // calcula();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getDesconto() {
        if (desconto.isEmpty()) {
            desconto = "0";
        }
        return Moeda.converteR$(desconto);
    }

    public void setDesconto(String desconto) {
        if (desconto.isEmpty()) {
            desconto = "0";
        }
        try {
            float d = Moeda.substituiVirgulaFloat(desconto);
            if (d < 0) {
                this.desconto = "0";
            } else {
                this.desconto = Moeda.substituiVirgula(desconto);
            }

        } catch (Exception e) {
            this.desconto = "0";
        }
    }

    public boolean isMarcarTodos() {
        return marcarTodos;
    }

    public void setMarcarTodos(boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public List<ListMovimentoReceber> getListMovimentoReceber() {
        if (listMovimentoReceber.isEmpty() && pessoa.getId() != -1) {
            MovimentoReceberDao db = new MovimentoReceberDao();
            List lista = db.pesquisaListaMovimentos(pessoa.getId());
            listMovimentoReceber = PlanilhaDebitoBean.load(lista);
        }
        return listMovimentoReceber;
    }

    public void setListMovimentoReceber(List<ListMovimentoReceber> listMovimentoReceber) {
        this.listMovimentoReceber = listMovimentoReceber;
    }

    @Override
    public void carregarFolha(DataObject valor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BloqueioRotina getBloqueioRotina() {
        return bloqueioRotina;
    }

    public void setBloqueioRotina(BloqueioRotina bloqueioRotina) {
        this.bloqueioRotina = bloqueioRotina;
    }

    public void removeBloqueioRotina() {
        if (bloqueioRotina != null) {
            if (bloqueioRotina.getId() != -1) {
                Dao dao = new Dao();
                boolean s = dao.delete(bloqueioRotina, true);
                if (s) {
                    bloqueioRotina = null;
                }
                GenericaSessao.remove("bovimentosReceberBean");
            }
        }
    }

    public boolean isUnlock() {
        if (bloqueioRotina != null) {
            if (bloqueioRotina.getId() != -1) {
                if (bloqueioRotina.getUsuario().getId() == ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}
