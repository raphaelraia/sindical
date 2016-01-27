package br.com.rtools.webservice;
    
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ManagedBean
@ViewScoped
@Path("/executar_sistema")
public class ExecutarSistemaWS {

    public ExecutarSistemaWS() {
        String teste = "";
        teste = "x";
        teste = "u";
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{cliente}/{login}/{password}")
    public String getLogin(
            @PathParam("cliente") String cliente,
            @PathParam("login") String login,
            @PathParam("password") String password
    ) {
            String c = cliente;
        String l = login;
        String p = password;
        return null;
    }
}
