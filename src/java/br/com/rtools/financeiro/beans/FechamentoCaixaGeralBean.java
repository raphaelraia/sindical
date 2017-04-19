package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.FechamentoCaixa;
import br.com.rtools.financeiro.TransferenciaCaixa;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class FechamentoCaixaGeralBean implements Serializable {

    private List<ListaCaixaGeral> listaFechamentoCaixa;
    private String valorFechamento;
    private List<Vector> listaDetalhesFechamento;
    private FechamentoCaixa fechamento;
    private String filtro = "30dias";

    @PostConstruct
    public void init() {
        listaFechamentoCaixa = new ArrayList();
        listaDetalhesFechamento = new ArrayList();
        loadListaFechamentoCaixa();

        valorFechamento = "0,00";

        fechamento = new FechamentoCaixa();
    }

    @PreDestroy
    public void destroy() {

    }

    public void estornarFechamento() {
        if (fechamento.getId() != -1) {

            FinanceiroDao db = new FinanceiroDao();

            List<TransferenciaCaixa> listat = db.listaTransferencia(fechamento.getId());

            Dao dao = new Dao();

            if (!listat.isEmpty()) {

                dao.openTransaction();

                for (TransferenciaCaixa tc : listat) {
                    if (!dao.delete(dao.find(tc))) {
                        GenericaMensagem.error("Erro", "Não foi possível concluir estorno!");
                        dao.rollback();
                        return;
                    }
                }

                dao.commit();

                GenericaMensagem.info("Sucesso", "Estorno Concluído!");
                loadListaFechamentoCaixa();
            }

        }

    }

    public void loadEstornarFechamento(Integer id_fechamento) {
        fechamento = (FechamentoCaixa) new Dao().find(new FechamentoCaixa(), id_fechamento);
    }

    public void loadListaDetalhesFechamento(Integer id_caixa, Integer id_fechamento) {
        listaDetalhesFechamento.clear();

        FinanceiroDao db = new FinanceiroDao();

        listaDetalhesFechamento = db.listaDetalhesFechamentoCaixaGeral(id_caixa, id_fechamento);
    }

    public void loadListaFechamentoCaixa() {
        listaFechamentoCaixa.clear();

        FinanceiroDao db = new FinanceiroDao();

        List<Object> list = db.listaFechamentoCaixaGeral(filtro);

        for (Object result : list) {
            List linha = (List) result;

            Caixa cx = (Caixa) new Dao().find(new Caixa(), (Integer) linha.get(6));

            listaFechamentoCaixa.add(
                    new ListaCaixaGeral(
                            linha.get(0).toString(),
                            (linha.get(1) != null) ? (Date) linha.get(1) : null,
                            (linha.get(1) != null) ? linha.get(2).toString() : "",
                            (linha.get(3) != null) ? (Date) linha.get(3) : null,
                            ((Double) linha.get(4)).floatValue(),
                            (linha.get(1) != null) ? (Integer) linha.get(5) : null,
                            (Integer) linha.get(6),
                            (Date) linha.get(7),
                            cx
                    )
            );
        }

    }

    public void salvar() {
        if (!listaFechamentoCaixa.isEmpty()) {

            Dao di = new Dao();

            float valor = Moeda.substituiVirgulaFloat(valorFechamento), soma = 0;

            for (ListaCaixaGeral linha : listaFechamentoCaixa) {
                soma = Moeda.somaValores(soma, linha.getValor());
                if (linha.getDataFechamento() == null) {
                    GenericaMensagem.warn("Atenção", "Caixa " + linha.getNomeCaixa() + " não tem Data de FECHAMENTO!");
                    return;
                }
                if (linha.getDataTransferencia() == null) {
                    GenericaMensagem.warn("Atenção", "Caixa " + linha.getNomeCaixa() + " não tem Data de TRANSFERÊNCIA!");
                    return;
                }
            }

            if (valor != Moeda.converteFloatR$Float(soma)) {
                GenericaMensagem.warn("Atenção", "Total Incorreto!");
                return;
            }

            di.openTransaction();
            
            for (ListaCaixaGeral linha : listaFechamentoCaixa) {
                FechamentoCaixa fc = (FechamentoCaixa) di.find(new FechamentoCaixa(), linha.getIdFechamentoCaixa());

                fc.setDtFechamentoGeral(DataHoje.dataHoje());

                if (!di.update(fc)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Não foi possível alterar Fechamento de Caixa!");
                    return;
                }
            }
            
            di.commit();

            GenericaMensagem.info("Sucesso", "Fechamento Geral Concluído!");
            
            loadListaFechamentoCaixa();
        }
    }

    public String converteData(Date data) {
        return DataHoje.converteData(data);
    }

    public String converteValor(String valor) {
        return Moeda.converteR$(valor);
    }

    public List<ListaCaixaGeral> getListaFechamentoCaixa() {
        return listaFechamentoCaixa;
    }

    public void setListaFechamentoCaixa(List<ListaCaixaGeral> listaFechamentoCaixa) {
        this.listaFechamentoCaixa = listaFechamentoCaixa;
    }

    public String getValorFechamento() {
        return Moeda.converteR$(valorFechamento);
    }

    public void setValorFechamento(String valorFechamento) {
        this.valorFechamento = Moeda.converteR$(valorFechamento);
    }

    public List<Vector> getListaDetalhesFechamento() {
        return listaDetalhesFechamento;
    }

    public void setListaDetalhesFechamento(List<Vector> listaDetalhesFechamento) {
        this.listaDetalhesFechamento = listaDetalhesFechamento;
    }

    public FechamentoCaixa getFechamento() {
        return fechamento;
    }

    public void setFechamento(FechamentoCaixa fechamento) {
        this.fechamento = fechamento;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public class ListaCaixaGeral {

        private String nomeCaixa;
        private Date dataFechamento;
        private String horaFechamento;
        private Date dataTransferencia;
        private Float valor;
        private Integer idFechamentoCaixa;
        private Integer idCaixa;
        private Date dataBaixa;
        private Caixa caixa;

        public ListaCaixaGeral() {
            this.nomeCaixa = "";
            this.dataFechamento = null;
            this.horaFechamento = "";
            this.dataTransferencia = null;
            this.valor = new Float(0);
            this.idFechamentoCaixa = null;
            this.idCaixa = null;
            this.dataBaixa = null;
            this.caixa = null;
        }

        public ListaCaixaGeral(String nomeCaixa, Date dataFechamento, String horaFechamento, Date dataTransferencia, Float valor, Integer idFechamentoCaixa, Integer idCaixa, Date dataBaixa, Caixa caixa) {
            this.nomeCaixa = nomeCaixa;
            this.dataFechamento = dataFechamento;
            this.horaFechamento = horaFechamento;
            this.dataTransferencia = dataTransferencia;
            this.valor = valor;
            this.idFechamentoCaixa = idFechamentoCaixa;
            this.idCaixa = idCaixa;
            this.dataBaixa = dataBaixa;
            this.caixa = caixa;
        }

        public String getNomeCaixa() {
            return nomeCaixa;
        }

        public void setNomeCaixa(String nomeCaixa) {
            this.nomeCaixa = nomeCaixa;
        }

        public Date getDataFechamento() {
            return dataFechamento;
        }

        public void setDataFechamento(Date dataFechamento) {
            this.dataFechamento = dataFechamento;
        }

        public String getDataFechamentoString() {
            return DataHoje.converteData(dataFechamento);
        }

        public void setDataFechamentoString(String dataFechamentoString) {
            this.dataFechamento = DataHoje.converte(dataFechamentoString);
        }

        public String getHoraFechamento() {
            return horaFechamento;
        }

        public void setHoraFechamento(String horaFechamento) {
            this.horaFechamento = horaFechamento;
        }

        public Date getDataTransferencia() {
            return dataTransferencia;
        }

        public void setDataTransferencia(Date dataTransferencia) {
            this.dataTransferencia = dataTransferencia;
        }

        public String getDataTransferenciaString() {
            return DataHoje.converteData(dataTransferencia);
        }

        public void setDataTransferenciaString(String dataTransferenciaString) {
            this.dataTransferencia = DataHoje.converte(dataTransferenciaString);
        }

        public Float getValor() {
            return valor;
        }

        public void setValor(Float valor) {
            this.valor = valor;
        }
        
        public String getValorString() {
            return Moeda.converteR$Float(valor);
        }

        public void setValorString(String valorString) {
            this.valor = Moeda.converteUS$(valorString);
        }

        public Integer getIdFechamentoCaixa() {
            return idFechamentoCaixa;
        }

        public void setIdFechamentoCaixa(Integer idFechamentoCaixa) {
            this.idFechamentoCaixa = idFechamentoCaixa;
        }

        public Integer getIdCaixa() {
            return idCaixa;
        }

        public void setIdCaixa(Integer idCaixa) {
            this.idCaixa = idCaixa;
        }

        public Date getDataBaixa() {
            return dataBaixa;
        }

        public void setDataBaixa(Date dataBaixa) {
            this.dataBaixa = dataBaixa;
        }
        
        public String getDataBaixaString() {
            return DataHoje.converteData(dataBaixa);
        }

        public void setDataBaixaString(String dataBaixaString) {
            this.dataBaixa = DataHoje.converte(dataBaixaString);
        }

        public Caixa getCaixa() {
            return caixa;
        }

        public void setCaixa(Caixa caixa) {
            this.caixa = caixa;
        }

    }
}
