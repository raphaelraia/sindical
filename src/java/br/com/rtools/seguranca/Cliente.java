package br.com.rtools.seguranca;

import br.com.rtools.utilitarios.Sessions;

public class Cliente {

    public static String get() {
        if(Sessions.exists("sessaoCliente")) {
            return Sessions.getString("sessaoCliente");
        }
        return null;
    }
}
