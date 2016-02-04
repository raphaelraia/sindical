package br.com.rtools.webservice;

import br.com.rtools.webservice.classes.WSBiometria;
import br.com.rtools.pessoa.Biometria;
import br.com.rtools.pessoa.BiometriaAtualizaCatraca;
import br.com.rtools.pessoa.BiometriaCatraca;
import br.com.rtools.pessoa.BiometriaServidor;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.dao.BiometriaAtualizaCatracaDao;
import br.com.rtools.pessoa.dao.BiometriaCatracaDao;
import br.com.rtools.pessoa.dao.BiometriaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.webservice.classes.WSStatus;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaRequisicao;
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
public class LeitorBiometricoBean implements Serializable {

    public LeitorBiometricoBean() {
//        Class c = this.getClass();
//        Method method;
//        ControleAcessoWebService caws = new ControleAcessoWebService();
//        if (caws.getAction() != null && !caws.getAction().isEmpty()) {
//            try {
//                method = c.getMethod(caws.getAction(), new Class[]{});
//                method.invoke(null, (Object[]) null);
//            } catch (NoSuchMethodException e) {
//                //System.out.println(erro);
//            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
//                Logger.getLogger(LeitorBiometricoBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

    public void lista() {
        try {
            Integer device_number = 0;
            try {
                device_number = Integer.parseInt(GenericaRequisicao.getParametro("device_number"));
            } catch (Exception e) {

            }
            ControleAcessoWebService caws = new ControleAcessoWebService();
            BiometriaDao biometriaDao = new BiometriaDao();
            List<Biometria> list;
            // String params = caws.get
            if (caws.getAction() != null && caws.getAction().equals("reload")) {
                list = biometriaDao.reloadListBiometria(device_number);
            } else {

                list = biometriaDao.listBiometria();
            }
            List<WSBiometria> wsbs = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                wsbs.add(new WSBiometria(list.get(i).getPessoa().getId(), list.get(i).getId(), list.get(i).getBiometria(), list.get(i).getAtivo(), list.get(i).getDataAtualizacaoAparelho1(), list.get(i).getDataAtualizacaoAparelho2(), list.get(i).getDataAtualizacaoAparelho3(), list.get(i).getDataAtualizacaoAparelho4()));
            }
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.setResponseContentType("application/json");
            externalContext.setResponseCharacterEncoding("UTF-8");
            Gson gson = new Gson();
            externalContext.getResponseOutputWriter().write(gson.toJson(wsbs));
            facesContext.responseComplete();
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

    public void limpar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        WSStatus status = new WSStatus();
        status.setCodigo(0);
        status.setDescricao("OK");
        try {
            ControleAcessoWebService caws = new ControleAcessoWebService();
            BiometriaDao biometriaDao = new BiometriaDao();
            MacFilialDao macFilialDao = new MacFilialDao();
            MacFilial macFilial = macFilialDao.pesquisaMac(caws.getMac());
            Dao dao = new Dao();
            List list = biometriaDao.pesquisaBiometriaCapturaPorMacFilial(macFilial.getId());
            if (!list.isEmpty()) {
                for (Object list1 : list) {
                    dao.delete(list1, true);
                }
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

    public void habilitar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Boolean habilitar = false;
        WSStatus status = new WSStatus();
        status.setCodigo(0);
        status.setDescricao("OK");
        try {
            habilitar = Boolean.parseBoolean(GenericaRequisicao.getParametro("habilitar"));
        } catch (Exception e) {

        }
        try {
            ControleAcessoWebService caws = new ControleAcessoWebService();
            BiometriaDao biometriaDao = new BiometriaDao();
            MacFilialDao macFilialDao = new MacFilialDao();
            MacFilial macFilial = macFilialDao.pesquisaMac(caws.getMac());
            List<BiometriaServidor> list = biometriaDao.pesquisaStatusPorComputador(macFilial.getId());
            Dao dao = new Dao();
            if (habilitar) {
                if (!list.isEmpty()) {
                    BiometriaServidor biometriaServidor = list.get(0);
                    if (!biometriaServidor.getAtivo()) {
                        biometriaServidor.setDataAtivo(new Date());
                        biometriaServidor.setAtivo(true);
                        dao.update(biometriaServidor, true);
                        status.setDescricao("Sucesso, servidor atualizado");
                    }
                } else {
                    BiometriaServidor biometriaServidor = new BiometriaServidor();
                    biometriaServidor.setDataAtivo(new Date());
                    biometriaServidor.setAtivo(true);
                    biometriaServidor.setMacFilial(macFilial);
                    dao.save(biometriaServidor, true);
                    status.setDescricao("Sucesso, servidor adicionado");
                }
            } else if (!list.isEmpty()) {
                BiometriaServidor biometriaServidor = list.get(0);
                if (dao.delete(biometriaServidor, true)) {
                    status.setDescricao("Sucesso, servidor removido");
                } else {
                    status.setCodigo(1);
                    status.setDescricao("Erro ao realizar operação!");
                }
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

    public void atualizar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String codigo_biometria = "";
        String device_number = "";
        WSStatus status = new WSStatus();
        status.setCodigo(0);
        status.setDescricao("OK");
        List listCodigoBiometria = new ArrayList<>();
        try {
            codigo_biometria = GenericaRequisicao.getParametro("codigo_biometria");
        } catch (Exception e) {

        }
        try {
            device_number = GenericaRequisicao.getParametro("device_number");
        } catch (Exception e) {

        }
        try {
            ControleAcessoWebService caws = new ControleAcessoWebService();
            String[] listIdBiometria = codigo_biometria.split(",");
            Dao dao = new Dao();
            for (int i = 0; i < listIdBiometria.length; i++) {
                Biometria biometria = (Biometria) new Dao().find(new Biometria(), Integer.parseInt(listIdBiometria[0]));
                if (device_number.equals("1")) {
                    biometria.setDataAtualizacaoAparelho1(null);
                } else if (device_number.equals("2")) {
                    biometria.setDataAtualizacaoAparelho2(null);
                } else if (device_number.equals("3")) {
                    biometria.setDataAtualizacaoAparelho3(null);
                } else if (device_number.equals("4")) {
                    biometria.setDataAtualizacaoAparelho4(null);
                }
                dao.update(biometria, true);
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

    public void atualizaCatraca() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String device_number = "";
        String biometria_atualiza_catraca = "";
        String biometria_status = "";
        String biometria_ip = "";
        String codigo_pessoa = null;
        WSStatus status = new WSStatus();
        status.setCodigo(0);
        status.setDescricao("OK");
        BiometriaAtualizaCatraca bac = null;
        ControleAcessoWebService caws = new ControleAcessoWebService();
        if (caws.getAction() != null) {
            if (caws.getAction().equals("update")) {
                try {
                    biometria_atualiza_catraca = GenericaRequisicao.getParametro("biometria_atualiza_catraca");
                    bac = gson.fromJson(biometria_atualiza_catraca, new BiometriaAtualizaCatraca().getClass());
                    new Dao().update(bac, true);
                } catch (Exception e) {
                    status.setCodigo(1);
                    status.setDescricao(e.getMessage());
                }
                try {
                    externalContext.getResponseOutputWriter().write(gson.toJson(bac));
                    facesContext.responseComplete();
                } catch (NullPointerException | JSONException | IOException e) {

                }
            } else if (caws.getAction() != null && caws.getAction().equals("update_catraca")) {
                BiometriaCatraca bc = new BiometriaCatraca();
                BiometriaCatracaDao bcd = new BiometriaCatracaDao();
                try {
                    biometria_status = GenericaRequisicao.getParametro("biometria_status");
                } catch (Exception e) {

                }
                try {
                    biometria_ip = GenericaRequisicao.getParametro("biometria_ip");
                } catch (Exception e) {

                }
                if (biometria_status.equals("1")) {
                    bcd.destroy(biometria_ip);
                    System.err.println("Biometria não encontrada!");
                    bc.setIp(biometria_ip);
                    bc.setPessoa(null);
                    new Dao().save(bc, true);
                    return;
                }
                bc.setIp(biometria_ip);
                try {
                    codigo_pessoa = GenericaRequisicao.getParametro("codigo_pessoa");
                } catch (Exception e) {

                }
                if (codigo_pessoa == null) {
                    bc.setPessoa(null);
                } else {
                    List list = bcd.findByPessoa(Integer.parseInt(codigo_pessoa));
                    for (int i = 0; i < list.size(); i++) {
                        new Dao().delete(list.get(i), true);
                    }
                    bc.setPessoa((Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(codigo_pessoa)));
                }
                WSStatus wSStatus = new WSStatus();                
                if(!new Dao().save(bc, true)) {
                    wSStatus.setCodigo(1);
                    wSStatus.setDescricao("Erro ao atualizar biometria catraca!");
                }
                try {
                    externalContext.getResponseOutputWriter().write(gson.toJson(wSStatus));
                    facesContext.responseComplete();
                } catch (NullPointerException | JSONException | IOException e) {

                }
            }
        } else {
            try {
                try {
                    device_number = GenericaRequisicao.getParametro("device_number");
                    bac = new BiometriaAtualizaCatracaDao().refresh(Integer.parseInt(device_number));
                    bac = (BiometriaAtualizaCatraca) new Dao().rebind(bac);
                } catch (Exception e) {

                }
            } catch (Exception e) {
                status.setCodigo(1);
                status.setDescricao(e.getMessage());
            }
            try {
                externalContext.getResponseOutputWriter().write(gson.toJson(bac));
                facesContext.responseComplete();
            } catch (NullPointerException | JSONException | IOException e) {

            }
        }
    }

    public void reload() {
        try {
            Integer device_number = 0;
            try {
                device_number = Integer.parseInt(GenericaRequisicao.getParametro("device_number"));
            } catch (Exception e) {

            }
            new BiometriaDao().reload(device_number);
        } catch (Exception e) {

        }
    }

}
