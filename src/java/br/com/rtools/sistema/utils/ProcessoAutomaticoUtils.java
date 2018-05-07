package br.com.rtools.sistema.utils;

import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.thread.AtualizarJuridicaThread;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;

public class ProcessoAutomaticoUtils {

    public static void execute(ProcessoAutomatico pa) {

        if (pa == null || pa.getDataFinal() != null) {
            return;
        }

        Dao dao = new Dao();

        if (pa.getProcesso().equals("atualizar_juridica")) {
            AtualizarJuridicaThread.init(pa);
        }

        System.err.println("Terminou a thread");

        pa.setDataFinal(DataHoje.dataHoje());
        pa.setHoraFinal(DataHoje.hora());

        dao.update(pa, true);
    }

}
