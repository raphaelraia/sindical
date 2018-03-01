package br.com.rtools.principal;

import br.com.rtools.utilitarios.Messages;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBExternal {

    private Statement statment;
    //private String url = "192.168.1.102";
    private String url = "192.168.1.100";
    private String port = "5432";
    private String database = "Rtools";
    private String user = "postgres";
    private String password = "r#@tools";

    public Connection getConnection() {
        return getConnection(false);
    }

    public Connection getConnection(Boolean sQLException) {
        try {
            //Configuracao c = DB.servidor(GenericaSessao.getString("sessaoCliente"));
            //String url = "jdbc:postgresql://200.158.101.9:5432/Rtools";
            String uri = "jdbc:postgresql://" + this.url + ":" + port + "/" + database;
            // String uri = "jdbc:postgresql://" + c.getHost() + ":" + port + "/" + c.getPersistence();
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            // props.setProperty("password", c.getSenha());
            //props.setProperty("ssl", "true");
            Connection conn = DriverManager.getConnection(uri, props);
            return conn;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            if (sQLException) {
                Messages.warn("SQLException", e.getMessage());
            }
        }
        return null;
    }

    public Statement getStatment() throws SQLException {
        statment = getConnection().createStatement();
        return statment;
    }

    public void setStatment(Statement statment) {
        this.statment = statment;
    }

    public void closeStatment() throws SQLException {
        getConnection().close();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
