package br.com.rtools.thread;

import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.ProcessoAutomaticoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Claudemir Rtools
 */
public class RegistrarBoletoThread extends ThreadLocal<Object> {

    List<Boleto> listaBoleto = new ArrayList();
    String view = "";

    public RegistrarBoletoThread(List<Boleto> lista, String view) {
        this.listaBoleto = lista;
        this.view = view;
    }

    /**
     * NÃO FUNCIONA COM SESSÃO
     */
    public void run() {
        FutureTask theTask;
        theTask = new FutureTask(new Runnable() {
            @Override
            public void run() {
                try {
                    metodo();
                } catch (Exception e) {
                    e.getMessage();
                }
                if (Thread.interrupted()) {
                }
            }
        }, null);
        new Thread(theTask).start();
    }

    public void runDebug() {
        metodo();
    }

    public Void metodo() {

        //List<Juridica> lista_juridica = new AtualizacaoAutomaticaJuridicaDao().listaJuridicaParaAtualizacao(this.query);
        Dao dao = new Dao();
        System.err.println("Começou a thread");

        ProcessoAutomatico pa = new ProcessoAutomatico();
        pa.setDataInicio(DataHoje.dataHoje());
        pa.setHoraInicio(DataHoje.hora());
        pa.setUsuario(Usuario.getUsuario());
        pa.setTodosUsuarios(false);
        pa.setNrProgresso(0);
        pa.setNrProgressoFinal(listaBoleto.size());
        pa.setProcesso("registrar_boleto");

        dao.save(pa, true);

        for (int i = 0; i < listaBoleto.size(); i++) {
            try {
                String retorno = registrar(listaBoleto.get(i));

                ProcessoAutomaticoLog pal = new ProcessoAutomaticoLog(
                        -1,
                        pa,
                        "[" + DataHoje.hora() + "] N° " + (i + 1) + "\n"
                        + (!retorno.isEmpty() ? "[" + retorno + "]\n" : " ")
                        + " Boleto ID: " + listaBoleto.get(i).getId() + "\n"
                        + " Boleto Número: " + listaBoleto.get(i).getBoletoComposto()
                );

                dao.save(pal, true);
                pa.setNrProgresso(i + 1);

                dao.update(pa, true);

                System.err.println("Boleto Registrado N° " + i + ": " + listaBoleto.get(i).getBoletoComposto() + " [" + retorno + "]");
                // TEM QUE SER 2 SEG. SE NÃO TRAVA
                Thread.sleep(2000);
                dao.refresh(pa);

                if (pa.getCancelarProcesso()) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("[Erro] Boleto Registrado N° " + i + ": " + listaBoleto.get(i).getBoletoComposto());
            }
        }
        System.err.println("Terminou a thread");

        pa.setDataFinal(DataHoje.dataHoje());
        pa.setHoraFinal(DataHoje.hora());

        dao.update(pa, true);

        return null;
    }

    public String registrar(Boleto boleto) {
        List<Boleto> l = new ArrayList();
        l.add(boleto);

        MovimentosReceberSocialDao dbs = new MovimentosReceberSocialDao();
        FisicaDao dbf = new FisicaDao();
        FinanceiroDao db = new FinanceiroDao();

        List<Vector> lista_socio = new ArrayList();

        Pessoa pessoa = dbs.responsavelBoleto(boleto.getNrCtrBoleto());

        if (lista_socio.isEmpty()) {
            if (dbf.pesquisaFisicaPorPessoa(pessoa.getId()) != null) {
                lista_socio = db.listaBoletoSocioFisica(boleto.getNrCtrBoleto(), view); // NR_CTR_BOLETO
            } else {
                lista_socio = db.listaBoletoSocioJuridica(boleto.getNrCtrBoleto(), view); // NR_CTR_BOLETO
            }
        }
        // SOMA VALOR DAS ATRASADAS
        double valor_total_atrasadas = 0, valor_total = 0, valor_boleto = 0;

        for (Vector listax : lista_socio) {
            // SE vencimento_movimento FOR MENOR QUE vencimento_boleto_original
            if (DataHoje.menorData(DataHoje.converteData((Date) listax.get(38)), "01/" + DataHoje.converteData((Date) listax.get(40)).substring(3))) {
                valor_total_atrasadas = Moeda.soma(valor_total_atrasadas, Moeda.converteUS$(listax.get(14).toString()));
            } else {
                valor_total = Moeda.soma(valor_total, Moeda.converteUS$(listax.get(14).toString()));
            }
            valor_boleto = Moeda.soma(valor_total, valor_total_atrasadas);
        }

        ImprimirBoleto imp = new ImprimirBoleto();
        HashMap hash = imp.registrarMovimentosAss(boleto, valor_boleto, boleto.getVencimento());

        if (hash.get("boleto") != null) {
            return "";
        } else {
            return "Erro ao Registrar [" + hash.get("mensagem") + "]";
        }
    }

}
