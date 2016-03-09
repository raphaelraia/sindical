/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.thread;

import br.com.rtools.pessoa.Juridica;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Claudemir Rtools
 */
public class ThreadExecute implements Runnable  {

    String processo = "";
    List<Juridica> listaJuridica = new ArrayList();
    Class classe;

    AtualizarJuridicaThread ajt;

    public ThreadExecute(Class c) {
        classe = c;
        //this.processo = p;
        //this.listaJuridica = lj;
    }

    public ThreadExecute(AtualizarJuridicaThread ajt) {
        this.ajt = ajt;
        //this.processo = p;
        //this.listaJuridica = lj;
    }

    @Override
    public void run() {
        if (ajt != null) {
            ajt.runDebug();
        }

//        if (classe.getName().equals("AtualizarJuridicaThread")) {
//            try {
//                ((AtualizarJuridicaThread) classe.newInstance()).run();
//            } catch (InstantiationException | IllegalAccessException e) {
//                e.getMessage();
//            }
//        }
    }
}
