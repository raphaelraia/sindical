package br.com.rtools.webservice;

import br.com.rtools.webservice.classes.WSBiometria;
import br.com.rtools.pessoa.Biometria;
import br.com.rtools.pessoa.BiometriaAtualizaCatraca;
import br.com.rtools.pessoa.BiometriaCaptura;
import br.com.rtools.pessoa.BiometriaCatraca;
import br.com.rtools.pessoa.BiometriaErro;
import br.com.rtools.pessoa.BiometriaServidor;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.BiometriaAtualizaCatracaDao;
import br.com.rtools.pessoa.dao.BiometriaCatracaDao;
import br.com.rtools.pessoa.dao.BiometriaDao;
import br.com.rtools.pessoa.dao.BiometriaErroDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.webservice.classes.WSStatus;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.sistema.ImpressoraMatricial;
import br.com.rtools.sistema.ImpressoraMatricialLinhas;
import br.com.rtools.sistema.dao.ImpressoraMatricialDao;
import br.com.rtools.sistema.dao.ImpressoraMatricialLinhasDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.webservice.classes.WSBiometriaCaptura;
import br.com.rtools.webservice.classes.WSHeaders;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
public class ImpressoraMatricialWS implements Serializable {

    private final ControleAcessoWebService caws;
    private final FacesContext facesContext;
    private final ExternalContext externalContext;
    private final Gson gson;
    private final WSStatus status;
    private final WSHeaders wSHeaders;

    public ImpressoraMatricialWS() {
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

    public void load() {
        try {
            Integer tipo_dispositivo_id = null;
            try {
                tipo_dispositivo_id = Integer.parseInt(GenericaRequisicao.getParametro("tipo_dispositivo_id"));
            } catch (NumberFormatException e) {
                return;
            }
            ImpressoraMatricial im = new ImpressoraMatricialDao().findByMacTipo(wSHeaders.getMac(), tipo_dispositivo_id);
            if (im != null) {
                List<ImpressoraMatricialLinhas> list = new ImpressoraMatricialLinhasDao().findByImpressoraMatricial(im.getId());
                externalContext.getResponseOutputWriter().write(gson.toJson(list));
                facesContext.responseComplete();
            }
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

    public void alerta() {
        try {
            Integer tipo_dispositivo_id = null;
            String mensagem_alerta = "";
            try {
                tipo_dispositivo_id = Integer.parseInt(GenericaRequisicao.getParametro("tipo_dispositivo_id"));
                mensagem_alerta = GenericaRequisicao.getParametro("mensagem_alerta");
            } catch (NumberFormatException e) {
                return;
            }
            ImpressoraMatricial im = new ImpressoraMatricialDao().findByMacTipo(wSHeaders.getMac(), tipo_dispositivo_id);
            if (im != null) {
                im.getDispositivo().setMensagemAlerta(mensagem_alerta);
                new Dao().update(im, true);
            }
        } catch (Exception e) {

        }
    }

    public void clear() {
        try {
            Integer tipo_dispositivo_id = null;
            try {
                tipo_dispositivo_id = Integer.parseInt(GenericaRequisicao.getParametro("tipo_dispositivo_id"));
            } catch (NumberFormatException e) {
                return;
            }
            ImpressoraMatricial im = new ImpressoraMatricialDao().findByMacTipo(wSHeaders.getMac(), tipo_dispositivo_id);
            if (im != null) {
                List<ImpressoraMatricialLinhas> list = new ImpressoraMatricialLinhasDao().findByImpressoraMatricial(im.getId());
                for (int i = 0; i < list.size(); i++) {
                    new Dao().delete(list.get(i), true);
                }
                new Dao().delete(im, true);
            }
        } catch (Exception e) {

        }
    }

}
