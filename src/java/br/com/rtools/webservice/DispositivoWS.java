package br.com.rtools.webservice;

import br.com.rtools.pessoa.BiometriaServidor;
import br.com.rtools.pessoa.dao.BiometriaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.sistema.Dispositivo;
import br.com.rtools.sistema.dao.DispositivoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.webservice.classes.WSHeaders;
import br.com.rtools.webservice.classes.WSStatus;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.json.JSONException;

@ManagedBean
@RequestScoped
@ViewScoped
public class DispositivoWS {

    private ControleAcessoWebService caws;
    private FacesContext facesContext;
    private ExternalContext externalContext;
    private Gson gson;
    private WSStatus status;
    private WSHeaders wSHeaders;

    public DispositivoWS() {
        caws = new ControleAcessoWebService();
        facesContext = FacesContext.getCurrentInstance();
        externalContext = facesContext.getExternalContext();
        gson = new Gson();
        status = new WSStatus();
        status.setCodigo(0);
        status.setDescricao("OK");
        if (caws.getAction() == null) {
            caws.setAction("");
        }
        wSHeaders = new WSHeaders();
    }

    public void habilitar() {
        Boolean habilitar = false;
        Integer tipo_dispositivo_id = null;
        try {
            habilitar = Boolean.parseBoolean(GenericaRequisicao.getParametro("habilitar"));
            tipo_dispositivo_id = Integer.parseInt(GenericaRequisicao.getParametro("tipo_dispositivo_id"));
        } catch (Exception e) {

        }
        try {
            DispositivoDao dispositivoDao = new DispositivoDao();
            Dispositivo dispositivo = dispositivoDao.findByMacTipo(wSHeaders.getMac(), tipo_dispositivo_id);
            if (dispositivo == null) {
                status.setDescricao("Sucesso, dispositvo não encontrado");
                externalContext.getResponseOutputWriter().write(gson.toJson(status));
                facesContext.responseComplete();
            }
            if (!dispositivo.getAtivo()) {
                status.setDescricao("Sucesso, dispositvo está desabilitado! Solicite a ativação deste no sistema.");
                externalContext.getResponseOutputWriter().write(gson.toJson(status));
                facesContext.responseComplete();
            }
            if (habilitar) {
                dispositivo.setConectado(new Date());
                new Dao().update(dispositivo, true);
                status.setDescricao("Sucesso, dispositvo conectado");
                externalContext.getResponseOutputWriter().write(gson.toJson(status));
                facesContext.responseComplete();
            } else {
                dispositivo.setConectado(null);
                new Dao().update(dispositivo, true);
                status.setDescricao("Sucesso, dispositvo desconectado");
                externalContext.getResponseOutputWriter().write(gson.toJson(status));
                facesContext.responseComplete();
            }
        } catch (Exception e) {
            status.setCodigo(1);
            status.setDescricao(e.getMessage());
        }
        try {
            externalContext.getResponseOutputWriter().write(gson.toJson(status));
            facesContext.responseComplete();
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

}
