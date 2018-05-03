package br.com.rtools.principal;

import br.com.rtools.utilitarios.Sessions;

public class DBClient extends DBExternal {

    public DBClient() {
        super();
        configure(Sessions.getString("sessaoCliente"));
    }

}
