package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.Acordo;
import static br.com.rtools.arrecadacao.beans.AcordoBean.BubbleSort;
import static br.com.rtools.arrecadacao.beans.AcordoBean.BubbleSortServico;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.beans.MovimentosReceberBean;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.financeiro.dao.TipoServicoDao;
import br.com.rtools.movimento.GerarMovimento;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class AcordoSocialBean implements Serializable {

    private Acordo acordo = new Acordo();
    private Pessoa pessoa = new Pessoa();
    private List<DataObject> listaVizualizado = new ArrayList();
    private String total = "0,00";
    private int parcela = 1;
    private int idVencimento = 0;
    private List<SelectItem> listaVencimento = new ArrayList();
    private int frequencia = 30;
    private String valorEntrada = "0,00";
    private String vencimento = DataHoje.data();
    private Historico historico = new Historico();
    private List<DataObject> listaOperado = new ArrayList();
    private List<Movimento> listaMovs = new ArrayList();
    private Boolean imprimeVerso = false;
    private String ultimaData = "";
    private Registro registro = new Registro();

    @PostConstruct
    public void init() {
        loadPessoa();
        loadListaVisualizado();

        registro = (Registro) new Dao().find(new Registro(), 1);
    }

    public Integer retornaDiaVencimentoPessoa(Integer id_pessoa) {
        PessoaDao dbp = new PessoaDao();
        PessoaComplemento pc = dbp.pesquisaPessoaComplementoPorPessoa(id_pessoa);

        if (pc.getId() == -1) {
            return registro.getFinDiaVencimentoCobranca();
        } else {
            return pc.getNrDiaVencimento();
        }
    }

    public void loadPessoa() {
        if (GenericaSessao.getObject("listaMovimento") != null) {
            listaMovs = GenericaSessao.getList("listaMovimento");
            pessoa = listaMovs.get(0).getPessoa();
            GenericaSessao.remove("listaMovimento");
        }
    }

    public void loadListaVisualizado() {
        if (listaVizualizado.isEmpty() && !listaMovs.isEmpty() && pessoa.getId() != -1) {
            historico.setHistorico("ACORDO CORRESPONDENTE A: ");
            double soma = 0;

            Map<Integer, DataObject> hash = new LinkedHashMap();

            for (Movimento listaMov : listaMovs) {
                soma = Moeda.soma(soma, listaMov.getValorBaixa());
                Double valor_linha;
                String s_historico;

                if (hash.get(listaMov.getServicos().getId()) == null) {
                    s_historico = listaMov.getServicos().getDescricao();
                    valor_linha = listaMov.getValorBaixa();
                } else {
                    s_historico = hash.get(listaMov.getServicos().getId()).getArgumento2().toString();
                    valor_linha = Moeda.soma(Moeda.converteUS$(hash.get(listaMov.getServicos().getId()).getArgumento1().toString()), listaMov.getValorBaixa());
                }

                hash.put(listaMov.getServicos().getId(), new DataObject(listaMov.getServicos(), Moeda.converteR$Double(valor_linha), s_historico));
            }

            for (Map.Entry<Integer, DataObject> entry : hash.entrySet()) {
                listaVizualizado.add(new DataObject(entry.getValue().getArgumento0(), entry.getValue().getArgumento1(), entry.getValue().getArgumento3()));
                historico.setHistorico(historico.getHistorico() + "" + entry.getValue().getArgumento2() + ", ");
            }

            total = Moeda.converteR$Double(soma);
        }
    }

    public synchronized void adicionarParcela() {
        try {
            TipoServicoDao dbTipoServico = new TipoServicoDao();
            ContaCobrancaDao ctaCobraDB = new ContaCobrancaDao();
            TipoServico tipoServico = dbTipoServico.pesquisaCodigo(4);
            DataHoje data = new DataHoje();
            int j = 0, k = 0;
            Servicos servico;
            ContaCobranca contaCobranca;
            listaOperado.clear();
            //String ultimoVencimento = getListaVencimento().get(idVencimento).getLabel();
            double valorTotalOutras = 0;
            double valorSwap = Moeda.substituiVirgulaDouble(valorEntrada);
            double valorTotal = Moeda.converteDoubleR$Double(Moeda.substituiVirgulaDouble(total));
            double[] vetorEntrada = new double[listaVizualizado.size()];
            double pdE = Moeda.divisao(valorSwap, valorTotal);
            double valorParcela = 0;

            Integer dia = retornaDiaVencimentoPessoa(pessoa.getId());
            String dia_vencimento = (dia < 10) ? "0" + dia : "" + dia;

            for (int i = 0; i < listaVizualizado.size(); i++) {
                vetorEntrada[i] = Moeda.substituiVirgulaDouble((String) listaVizualizado.get(i).getArgumento1());
                if (listaVizualizado.size() > 1) {
                    vetorEntrada[i] = Moeda.converteDoubleR$Double(Moeda.multiplicar(vetorEntrada[i], pdE));
                } else {
                    vetorEntrada[i] = valorSwap;
                }
            }

            for (int i = 0; i < listaVizualizado.size(); i++) {
                servico = (Servicos) listaVizualizado.get(i).getArgumento0();
                contaCobranca = ctaCobraDB.pesquisaServicoCobranca(servico.getId(), tipoServico.getId());
                if (contaCobranca != null) {

                    String ultimoVencimento = getListaVencimento().get(idVencimento).getLabel();
                    j = 0;
                    if (parcela > 1) {
                        valorTotalOutras = Moeda.substituiVirgulaDouble((String) listaVizualizado.get(i).getArgumento1());
                        valorTotalOutras = Moeda.subtracao(valorTotalOutras, vetorEntrada[i]);
                        valorSwap = vetorEntrada[i];
                        valorParcela = Moeda.converteDoubleR$Double(Moeda.divisao(valorTotalOutras, parcela - 1));
                    } else {
                        valorSwap = Moeda.substituiVirgulaDouble((String) listaVizualizado.get(i).getArgumento1());
                    }
                    while (j < parcela) {
                        if (j != 0) {
                            if ((Moeda.subtracao(valorTotalOutras, valorParcela) != 0) && ((j + 1) == parcela)) {
                                valorParcela = valorTotalOutras;
                            } else {
                                valorTotalOutras = Moeda.subtracao(valorTotalOutras, valorParcela);
                            }
                            valorSwap = valorParcela;
                        }

                        Movimento mov = new Movimento(
                                -1,
                                null,
                                servico.getPlano5(),
                                pessoa,
                                servico,
                                null,
                                tipoServico,
                                null,
                                valorSwap,
                                referencia(ultimoVencimento),
                                ultimoVencimento,
                                1,
                                true,
                                "E",
                                false,
                                pessoa,
                                pessoa,
                                "",
                                "",
                                ultimoVencimento,
                                0,
                                0, 0, 0, 0, 0, 0, (FTipoDocumento) new Dao().find(new FTipoDocumento(), 2), 0, null
                        );

                        listaOperado.add(new DataObject(false, ++k, mov, (String) listaVizualizado.get(i).getArgumento3(), null, null));

                        if (j == 0) {
                            //ultimoVencimento = acordo.getData();
                            Integer dia_atual = Integer.valueOf(ultimoVencimento.substring(0, 2));

                            if (dia_atual <= Integer.valueOf(dia_vencimento)) {
                                ultimoVencimento = dia_vencimento + ultimoVencimento.substring(2);
                            } else if (dia_atual > Integer.valueOf(dia_vencimento)) {
                                ultimoVencimento = dia_vencimento + ultimoVencimento.substring(2);
                                ultimoVencimento = data.incrementarMeses(1, ultimoVencimento);
                            }
                        }

                        if (frequencia == 30) {
                            ultimoVencimento = data.incrementarMeses(1, ultimoVencimento);
                            if (ultimoVencimento.substring(3, 5).equals("02")) {
                                ultimoVencimento = acordo.getData().substring(0, 2) + ultimoVencimento.substring(2);
                            }
                        } else if (frequencia == 7) {
                            ultimoVencimento = data.incrementarSemanas(1, ultimoVencimento);
                        }
                        j++;
                    }

                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        BubbleSort(listaOperado);
        ultimaData = ((Movimento) listaOperado.get(listaOperado.size() - 1).getArgumento2()).getVencimento();
    }

    public synchronized String subirData() {
        String vencimentoOut = getListaVencimento().get(idVencimento).getLabel();

        if (listaOperado.isEmpty()) {
            return null;
        }

        int i = 0;
        List listas = new ArrayList();
        List<Integer> subLista = new ArrayList();
        DataHoje data = new DataHoje();
        String dataPrincipal = "";
        String referencia = "";
        while (i < listaOperado.size()) {
            if ((Boolean) listaOperado.get(i).getArgumento0()) {
                subLista.add(i);
            } else {
                if (!(subLista.isEmpty())) {
                    listas.add(subLista);
                    subLista = new ArrayList();
                }
                while (i < listaOperado.size()) {
                    if (listaOperado.size() > (i + 1)) {
                        if ((Boolean) listaOperado.get(i + 1).getArgumento0()) {
                            break;
                        }
                    }
                    i++;
                }
            }
            i++;
        }

        if (!(subLista.isEmpty())) {
            listas.add(subLista);
        }

        i = 0;

        while (i < listas.size()) {
            int j = 0;

            Movimento movimento = (Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2();
            String date = movimento.getVencimento();

            if (frequencia == 30) {
                if ((DataHoje.menorData(data.decrementarMeses(1, date), vencimentoOut))
                        && (!DataHoje.igualdadeData(data.decrementarMeses(1, date), vencimentoOut))) {
                    i++;
                    continue;
                }
                dataPrincipal = movimento.getVencimento();
                dataPrincipal = data.decrementarMeses(1, dataPrincipal);
                referencia = data.decrementarMeses(1, dataPrincipal);
            } else if (frequencia == 7) {
                if ((DataHoje.menorData(data.decrementarSemanas(1, date), vencimentoOut))
                        && (!DataHoje.igualdadeData(data.decrementarSemanas(1, date), vencimentoOut))) {
                    i++;
                    continue;
                }
                dataPrincipal = movimento.getVencimento();
                dataPrincipal = data.decrementarSemanas(1, dataPrincipal);
                referencia = data.decrementarSemanas(1, dataPrincipal);
            }

            while (j < ((List<Integer>) listas.get(i)).size()) {
                ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setVencimento(dataPrincipal);
                if (movimento.getServicos().getId() != 1) {
                    ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setReferencia(referencia.substring(3));
                }
                j++;
            }

            i++;
        }

        BubbleSort(listaOperado);
        ordernarPorServico();

        while (i < listaOperado.size()) {
            listaOperado.get(i).setArgumento1(i + 1);
            i++;
        }
        return null;
    }

    public synchronized String descerData() {
        if (listaOperado.isEmpty()) {
            return null;
        }

        int i = 0;

        List listas = new ArrayList();
        List<Integer> subLista = new ArrayList();
        DataHoje data = new DataHoje();
        String dataPrincipal = "";
        String referencia = "";
        while (i < listaOperado.size()) {
            if ((Boolean) listaOperado.get(i).getArgumento0()) {
                subLista.add(i);
            } else {
                if (!(subLista.isEmpty())) {
                    listas.add(subLista);
                    subLista = new ArrayList();
                }
                while (i < listaOperado.size()) {
                    if (listaOperado.size() > (i + 1)) {
                        if ((Boolean) listaOperado.get(i + 1).getArgumento0()) {
                            break;
                        }
                    }
                    i++;
                }
            }
            i++;
        }

        if (!(subLista.isEmpty())) {
            listas.add(subLista);
        }

        i = 0;

        while (i < listas.size()) {
            int j = 0;
            Movimento movimento = ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2());
            String date = movimento.getVencimento();

            if (frequencia == 30) {
                if (DataHoje.maiorData(data.incrementarMeses(1, date), ultimaData)) {
                    i++;
                    continue;
                }
                referencia = movimento.getVencimento();
                dataPrincipal = data.incrementarMeses(1, referencia);
            } else if (frequencia == 7) {
                if (DataHoje.maiorData(data.incrementarSemanas(1, date), ultimaData)) {
                    i++;
                    continue;
                }
                referencia = movimento.getVencimento();
                dataPrincipal = data.incrementarSemanas(1, referencia);
            }

            while (j < ((List<Integer>) listas.get(i)).size()) {
                ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setVencimento(dataPrincipal);
                if (((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).getServicos().getId() != 1) {
                    ((Movimento) listaOperado.get(((List<Integer>) listas.get(i)).get(j)).getArgumento2()).setReferencia(referencia.substring(3));
                }
                j++;
            }
            i++;
        }

        BubbleSort(listaOperado);
        ordernarPorServico();

        i = 0;

        while (i < listaOperado.size()) {
            listaOperado.get(i).setArgumento1(i + 1);
            i++;
        }
        return null;
    }

    public synchronized void ordernarPorServico() {
        int i = 0;
        int indI = 0, indF = 0;
        String data = ((Movimento) listaOperado.get(i).getArgumento2()).getVencimento();
        while (i < listaOperado.size()) {
            if (!data.equals(((Movimento) listaOperado.get(i).getArgumento2()).getVencimento())) {
                BubbleSortServico(listaOperado.subList(indI, indF));
                indI = indF;
                indF++;
                data = ((Movimento) listaOperado.get(i).getArgumento2()).getVencimento();
            } else {
                indF++;
            }
            i++;
        }
    }

    public synchronized void efetuarAcordo() {
        if (listaOperado.isEmpty()) {
            GenericaMensagem.error("Atenção", "Acordo não foi gerado!");
            return;
        }

        List<Movimento> listaAcordo = new ArrayList();
        List<String> listaHistorico = new ArrayList();

        for (DataObject listaOperado1 : listaOperado) {
            listaAcordo.add((Movimento) listaOperado1.getArgumento2());
            //listaHistorico.add((String) listaOperado1.getArgumento3());
            listaHistorico.add(historico.getHistorico());
        }

        try {
            String mensagem = GerarMovimento.salvarListaAcordoSocial(acordo, listaAcordo, listaMovs, listaHistorico);
            if (mensagem.isEmpty()) {
                GenericaMensagem.info("Sucesso", "Acordo Concluído!");
            }

            String url = (String) GenericaSessao.getString("urlRetorno");
            switch (url) {
                case "movimentosReceber":
                    ((MovimentosReceberBean) GenericaSessao.getObject("movimentosReceberBean")).getListMovimentoReceber().clear();
                    ((MovimentosReceberBean) GenericaSessao.getObject("movimentosReceberBean")).setDesconto("0");
                    break;
                case "movimentosReceberSocial":
                    ((MovimentosReceberSocialBean) GenericaSessao.getObject("movimentosReceberSocialBean")).getListaMovimento().clear();
                    break;
            }
            if (!mensagem.isEmpty()) {
                GenericaMensagem.error("Atenção", mensagem);
            }
        } catch (Exception e) {
            GenericaMensagem.error("Atenção", "Acordo não foi gerado");

        }
    }

    public void imprimirBoletos() {
 
    }

    public void imprimirPlanilha() {
        ImprimirBoleto imp = new ImprimirBoleto();
        List listaImp = new ArrayList();

        MovimentoDao db = new MovimentoDao();
        listaImp.addAll(db.pesquisaAcordoTodos(acordo.getId()));

        if (!listaImp.isEmpty()) {
            imp.imprimirAcordoSocial(listaImp, acordo, historico);
        }

    }

    public String referencia(String data) {
        if (data.length() == 10) {
            String ref = data.substring(3);
            String mes = ref.substring(0, 2);
            if (!(mes.equals("01"))) {
                if ((Integer.parseInt(mes) - 1) < 10) {
                    ref = "0" + Integer.toString(Integer.parseInt(mes) - 1) + data.substring(5);
                } else {
                    ref = Integer.toString(Integer.parseInt(mes) - 1) + data.substring(5);
                }
            } else {
                ref = "12/" + Integer.toString(Integer.parseInt(data.substring(6)) - 1);
            }
            return ref;
        } else {
            return null;
        }
    }

    public void limparEntrada() {
        valorEntrada = "0,00";
    }

    public Acordo getAcordo() {
        return acordo;
    }

    public void setAcordo(Acordo acordo) {
        this.acordo = acordo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<DataObject> getListaVizualizado() {
        return listaVizualizado;
    }

    public void setListaVizualizado(List<DataObject> listaVizualizado) {
        this.listaVizualizado = listaVizualizado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getParcela() {
        return parcela;
    }

    public void setParcela(int parcela) {
        this.parcela = parcela;
    }

    public int getIdVencimento() {
        return idVencimento;
    }

    public void setIdVencimento(int idVencimento) {
        this.idVencimento = idVencimento;
    }

    public List<SelectItem> getListaVencimento() {
        if (listaVencimento.isEmpty()) {
            int i = 0;
            DataHoje data = new DataHoje();
            listaVencimento.add(new SelectItem(
                    i,
                    vencimento)
            );
            i++;
            while (i < 31) {
                listaVencimento.add(new SelectItem(
                        i,
                        data.incrementarDias(i, vencimento))
                );
                i++;
            }
        }
        return listaVencimento;
    }

    public void setListaVencimento(List<SelectItem> listaVencimento) {
        this.listaVencimento = listaVencimento;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(int frequencia) {
        this.frequencia = frequencia;
    }

    public String getValorEntrada() {
        double valorTmp = Moeda.substituiVirgulaDouble(valorEntrada);
        double totalOutra = Moeda.substituiVirgulaDouble(total);

        if (valorEntrada.equals("0") || valorEntrada.equals("0,00")) {
            double valorTmp2 = Moeda.divisao(totalOutra, parcela);
            if (parcela > 1) {
                valorEntrada = Moeda.converteR$Double(valorTmp2);
                return valorEntrada;
            }
        } else if (valorTmp > (Moeda.multiplicar(totalOutra, (double) 0.05))
                && valorTmp < (Moeda.multiplicar(totalOutra, (double) 0.8))) {
            return Moeda.converteR$(valorEntrada);
        } else {
            double valorTmp2 = Moeda.divisao(totalOutra, parcela);
            if (parcela > 1) {
                valorEntrada = Moeda.converteR$Double(valorTmp2);
            }
        }
        return Moeda.converteR$(valorEntrada);
    }

    public void setValorEntrada(String valorEntrada) {
        this.valorEntrada = valorEntrada;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public Historico getHistorico() {
        return historico;
    }

    public void setHistorico(Historico historico) {
        this.historico = historico;
    }

    public List<DataObject> getListaOperado() {
        return listaOperado;
    }

    public void setListaOperado(List<DataObject> listaOperado) {
        this.listaOperado = listaOperado;
    }

    public Boolean getImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(Boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

}
