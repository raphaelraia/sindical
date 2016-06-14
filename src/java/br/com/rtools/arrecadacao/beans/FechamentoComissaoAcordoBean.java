package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.dao.AcordoComissaoDao;
import br.com.rtools.impressao.ParametroAcordoAnalitico;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean
@SessionScoped
public class FechamentoComissaoAcordoBean {

    private List<SelectItem> listaData;
    private int idDataFechamento;
    private Acordo acordo;

    @PostConstruct
    public void init() {
        listaData = new ArrayList();
        idDataFechamento = 0;
        acordo = new Acordo();
        // mensagem = "";
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("fechamentoComissaoAcordoBean");
    }

    public List<SelectItem> getListaData() {
        if (listaData.isEmpty()) {
            int i = 0;
            AcordoComissaoDao db = new AcordoComissaoDao();
            List<Date> select = db.pesquisaTodosFechamento();
            if (select != null) {
                while (i < select.size()) {
                    listaData.add(new SelectItem(i, DataHoje.converteData(select.get(i))));
                    i++;
                }
            }
        }
        return listaData;
    }

    public synchronized void processar() {
        AcordoComissaoDao acordoComissaoDB = new AcordoComissaoDao();
        if (acordoComissaoDB.inserirAcordoComissao()) {
            listaData.clear();
            GenericaMensagem.info("Sucesso", "Concluído com sucesso");
        } else {
            GenericaMensagem.warn("Erro", "Ao gerar comissão!");

        }
    }

    public void visualizar() {
        if (!listaData.isEmpty()) {
            AcordoComissaoDao db = new AcordoComissaoDao();
            List result = db.listaAcordoComissao(listaData.get(idDataFechamento).getLabel());

            JasperReport jasper = null;
            Collection lista = new ArrayList();
            BigDecimal repasse;
            BigDecimal liquido;
            BigDecimal comissao;
            BigDecimal valor;
            BigDecimal taxa;

            for (Object result1 : result) {
                valor = new BigDecimal(Double.valueOf(((List) result1).get(9).toString()));
                taxa = new BigDecimal(Double.valueOf(((List) result1).get(10).toString()));
                repasse = new BigDecimal(Double.valueOf(((List) result1).get(11).toString()));
                repasse = (valor.subtract(taxa).multiply(repasse)).divide(new BigDecimal(100));
                liquido = valor.subtract(taxa).subtract(repasse);
                comissao = valor.subtract(taxa).subtract(repasse).multiply(new BigDecimal(0.015));

                lista.add(
                        new ParametroAcordoAnalitico(
                                ((List) result1).get(0).toString(),
                                ((List) result1).get(1).toString(),
                                (Integer) ((List) result1).get(2),
                                ((List) result1).get(3).toString(),
                                ((List) result1).get(4).toString(),
                                (Date) ((List) result1).get(5),
                                (Date) ((List) result1).get(6),
                                (Date) ((List) result1).get(7),
                                valor,
                                taxa,
                                repasse,
                                liquido,
                                DataHoje.converte(listaData.get(idDataFechamento).getLabel()),
                                (Date) ((List) result1).get(8),
                                comissao,
                                (Date) ((List) result1).get(12)
                        )
                );
            }

            Jasper.IS_HEADER = true;
            Jasper.printReports("/Relatorios/ACORDO_ANALITICO.jasper", "Acordo Analítico", lista);
        }
    }

    public void estornar() {
        if (!listaData.isEmpty()) {
            AcordoComissaoDao acordoComissaoDB = new AcordoComissaoDao();
            if (acordoComissaoDB.estornarAcordoComissao(listaData.get(idDataFechamento).getLabel())) {
                GenericaMensagem.info("Sucesso", "Fechamento de acordo estornado");
            } else {
                GenericaMensagem.warn("Erro", "Ao estornar fechamento!");
            }
            listaData.clear();
        } else {
            GenericaMensagem.warn("Validação", "Data de Fechamento vazia!");
        }
    }

    public void setListaData(List<SelectItem> listaData) {
        this.listaData = listaData;
    }

    public int getIdDataFechamento() {
        return idDataFechamento;
    }

    public void setIdDataFechamento(int idDataFechamento) {
        this.idDataFechamento = idDataFechamento;
    }

    public Acordo getAcordo() {
        return acordo;
    }

    public void setAcordo(Acordo acordo) {
        this.acordo = acordo;
    }
}
