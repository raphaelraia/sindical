package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.MovimentoReceberDao;
import br.com.rtools.financeiro.lista.ListMovimentoReceber;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PlanilhaDebitoBean {

    public static void printNoNStatic(List<ListMovimentoReceber> listMovimentoReceber) {
        new PlanilhaDebitoBean().print(listMovimentoReceber);
    }

    public void print(List<ListMovimentoReceber> listMovimentoReceber) {
        List<Movimento> listaC = new ArrayList<>();
        List<Double> listaValores = new ArrayList<>();
        Dao dao = new Dao();
        for (int i = 0; i < listMovimentoReceber.size(); i++) {
            if (listMovimentoReceber.get(i).getSelected()) {
                Movimento mov = (Movimento) dao.find(new Movimento(), Integer.parseInt(String.valueOf(listMovimentoReceber.get(i).getIdMovimento())));
                mov.setMulta(Moeda.converteUS$((String) listMovimentoReceber.get(i).getMulta()));
                mov.setJuros(Moeda.converteUS$((String) listMovimentoReceber.get(i).getJuros()));
                mov.setCorrecao(Moeda.converteUS$((String) listMovimentoReceber.get(i).getCorrecao()));
                mov.setDesconto(Moeda.converteUS$((String) listMovimentoReceber.get(i).getDesconto()));
                mov.setValorBaixa(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorCalculado()));
                listaValores.add(Moeda.converteUS$((String) listMovimentoReceber.get(i).getValorMovimento()));
                listaC.add(mov);
                mov = new Movimento();
            }
        }

        if (!listaC.isEmpty()) {
            ImprimirBoleto imp = new ImprimirBoleto();
            imp.imprimirPlanilha(listaC, listaValores, false, false);
            imp.visualizar(null);
        } else {
            GenericaMensagem.warn("Validação", "Nenhum boleto selecionado!");
        }
    }

    public static List<ListMovimentoReceber> find(Integer pessoa_id) {
        return load(new ArrayList(), pessoa_id);
    }

    public static List<ListMovimentoReceber> load(List list) {
        return load(list, null);
    }

    public static List<ListMovimentoReceber> load(List list, Integer pessoa_id) {
        if (pessoa_id != null && pessoa_id != -1) {
            list = new ArrayList();
            list = new MovimentoReceberDao().pesquisaListaMovimentos(pessoa_id);

        }
        List<ListMovimentoReceber> listMovimentoReceber = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            if (o.get(5) == null) {
                o.set(5, 0.0);
            }
            if (o.get(6) == null) {
                o.set(6, 0.0);
            }
            if (o.get(7) == null) {
                o.set(7, 0.0);
            }
            if (o.get(8) == null) {
                o.set(8, 0.0);
            }
            if (o.get(9) == null) {
                o.set(9, 0.0);
            }
            if (o.get(10) == null) {
                o.set(10, 0.0);
            }
            if (o.get(11) == null) {
                o.set(11, 0.0);
            }
            if (((Integer) o.get(13)) < 0) {
                o.set(13, 0);
            }
            if (o.get(14) == null) {
                o.set(14, "");
            }
            listMovimentoReceber.add(new ListMovimentoReceber(
                    false,
                    o.get(0).toString(), // boleto
                    o.get(1).toString(), // servico
                    o.get(2).toString(), // tipo
                    o.get(3).toString(), // referencia
                    DataHoje.converteData((Date) o.get(4)), // vencimento
                    Moeda.converteR$(Double.toString((Double) o.get(5))), // valor_mov
                    o.get(6).toString(), // valor_folha
                    Moeda.converteR$(Double.toString((Double) o.get(7))), // multa
                    Moeda.converteR$(Double.toString((Double) o.get(8))), // juros
                    Moeda.converteR$(Double.toString((Double) o.get(9))), // correcao
                    Moeda.converteR$(Double.toString((Double) o.get(10))), // desconto
                    Moeda.converteR$(Double.toString((Double) o.get(11))), // valor_calculado
                    o.get(12).toString(), // meses em atraso
                    o.get(13).toString(), // dias em atraso
                    o.get(14).toString(), // indice
                    o.get(15).toString(), // id movimento
                    Moeda.converteR$(Double.toString((Double) o.get(11))) // valor_calculado original
            ));
        }
        return listMovimentoReceber;
    }
}
