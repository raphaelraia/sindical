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
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.webservice.classes.WSBiometriaCaptura;
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

    private final ControleAcessoWebService caws;
    private final FacesContext facesContext;
    private final ExternalContext externalContext;
    private final Gson gson;
    private final WSStatus status;

    public LeitorBiometricoBean() {
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
    }

    public void lista() {
        try {
            Integer device_number = 0;
            try {
                device_number = Integer.parseInt(GenericaRequisicao.getParametro("device_number"));
            } catch (Exception e) {

            }
            BiometriaDao biometriaDao = new BiometriaDao();
            List<Biometria> list;
            // String params = caws.get
            if (caws.getAction().equals("reload")) {
                list = biometriaDao.reloadListBiometria(device_number);
            } else {

                list = biometriaDao.listBiometria();
            }
            List<WSBiometria> wsbs = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                wsbs.add(new WSBiometria(list.get(i).getPessoa().getId(), list.get(i).getId(), list.get(i).getBiometria(), list.get(i).getAtivo(), list.get(i).getDataAtualizacaoAparelho1(), list.get(i).getDataAtualizacaoAparelho2(), list.get(i).getDataAtualizacaoAparelho3(), list.get(i).getDataAtualizacaoAparelho4()));
            }
            externalContext.getResponseOutputWriter().write(gson.toJson(wsbs));
            facesContext.responseComplete();
        } catch (NullPointerException | JSONException | IOException e) {

        }
    }

    public void limpar() {

        try {
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

        Boolean habilitar = false;
        try {
            habilitar = Boolean.parseBoolean(GenericaRequisicao.getParametro("habilitar"));
        } catch (Exception e) {

        }
        try {

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

        String codigo_biometria = "";
        String device_number = "";
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

        String device_number = "";
        String biometria_atualiza_catraca = "";
        String biometria_status = "";
        String biometria_ip = "";
        String codigo_pessoa = null;
        BiometriaAtualizaCatraca bac = null;
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
                if (!new Dao().save(bc, true)) {
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

    public void biometriaCaptura() {
        MacFilialDao macFilialDao = new MacFilialDao();
        MacFilial macFilial = macFilialDao.pesquisaMac(caws.getMac());
        if (caws.getAction().equals("pedido_captura")) {

            if (macFilial == null) {
                try {
                    externalContext.getResponseOutputWriter().write(gson.toJson(null));
                    facesContext.responseComplete();
                } catch (IOException ex) {

                }
            }
            BiometriaDao biometriaDao = new BiometriaDao();
            List<BiometriaCaptura> list = biometriaDao.pesquisaBiometriaCapturaPorMacFilial(macFilial.getId());
            if (!list.isEmpty()) {
                WSBiometriaCaptura wsbc = new WSBiometriaCaptura();
                wsbc.setCodigo_pessoa(list.get(0).getPessoa().getId());
                wsbc.setCodigo_mac_filial(macFilial.getId());
                try {
                    externalContext.getResponseOutputWriter().write(gson.toJson(wsbc));
                    facesContext.responseComplete();
                } catch (IOException ex) {

                }
            }
        } else if (caws.getAction().equals("pesquisa_biometria")) {
            Integer codigo_pessoa = 0;
            WSBiometriaCaptura wsbc = new WSBiometriaCaptura();
            try {
                Dao dao = new Dao();
                codigo_pessoa = Integer.parseInt(GenericaRequisicao.getParametro("codigo_pessoa"));
                BiometriaDao biometriaDao = new BiometriaDao();
                Biometria biometria = biometriaDao.pesquisaBiometriaPorPessoa(codigo_pessoa);
                if (biometria == null) {
                    biometria = new Biometria();
                    biometria.setLancamento(DataHoje.dataHoje());
                    biometria.setPessoa((Pessoa) dao.find(new Pessoa(), codigo_pessoa));
                    biometria.setDataAtualizacaoAparelho1(new Date());
                    biometria.setDataAtualizacaoAparelho2(new Date());
                    biometria.setDataAtualizacaoAparelho3(new Date());
                    biometria.setDataAtualizacaoAparelho4(new Date());
                    dao.save(biometria, true);
                }
                wsbc.setCodigo_pessoa(biometria.getPessoa().getId());
                wsbc.setCodigo_biometria(biometria.getId());
                wsbc.setAtiva(biometria.getAtivo());
                try {
                    externalContext.getResponseOutputWriter().write(gson.toJson(wsbc));
                    facesContext.responseComplete();
                } catch (IOException ex) {

                }
            } catch (Exception e) {

            }
        } else if (caws.getAction().equals("salvar")) {
            Integer codigo_pessoa = 0;
            try {
                codigo_pessoa = Integer.parseInt(GenericaRequisicao.getParametro("codigo_pessoa"));
                Biometria biometria = new Biometria();
                BiometriaDao biometriaDao = new BiometriaDao();
                biometria = biometriaDao.pesquisaBiometriaPorPessoa(codigo_pessoa);
                Dao dao = new Dao();
                if (biometria == null) {
                    biometria = new Biometria();
                    biometria.setLancamento(DataHoje.dataHoje());
                    biometria.setPessoa((Pessoa) dao.find(new Pessoa(), codigo_pessoa));
                    biometria.setAtivo(true);
                    biometria.setDataAtualizacaoAparelho1(new Date());
                    biometria.setDataAtualizacaoAparelho2(new Date());
                    biometria.setDataAtualizacaoAparelho3(new Date());
                    biometria.setDataAtualizacaoAparelho4(new Date());
                    if (dao.save(biometria, true)) {
                        WSBiometriaCaptura wsbc = new WSBiometriaCaptura();
                        wsbc.setCodigo_pessoa(biometria.getPessoa().getId());
                        wsbc.setCodigo_biometria(biometria.getId());
                        wsbc.setAtiva(true);
                        try {
                            externalContext.getResponseOutputWriter().write(gson.toJson(wsbc));
                            facesContext.responseComplete();
                        } catch (IOException ex) {

                        }
                    } else {
                        try {
                            WSStatus wSStatus = new WSStatus();
                            wSStatus.setCodigo(1);
                            wSStatus.setDescricao("Erro ao salvar biometria");
                            externalContext.getResponseOutputWriter().write(gson.toJson(wSStatus));
                            facesContext.responseComplete();
                        } catch (IOException ex) {

                        }
                    }
                } else {
                    WSBiometriaCaptura wsbc = new WSBiometriaCaptura();
                    wsbc.setCodigo_pessoa(biometria.getPessoa().getId());
                    wsbc.setCodigo_biometria(biometria.getId());
                    try {
                        externalContext.getResponseOutputWriter().write(gson.toJson(wsbc));
                        facesContext.responseComplete();
                    } catch (IOException ex) {

                    }
                }
            } catch (Exception e) {

            }
        } else if (caws.getAction().equals("biometria_update")) {
            String wsbiometriacaptura = "";
            try {
                wsbiometriacaptura = GenericaRequisicao.getParametro("WSBiometriaCaptura");
                WSBiometriaCaptura wsbc = gson.fromJson(wsbiometriacaptura, new WSBiometriaCaptura().getClass());
                Biometria biometria = new Biometria();
                BiometriaDao biometriaDao = new BiometriaDao();
                Dao dao = new Dao();
                biometria = (Biometria) dao.find(new Biometria(), wsbc.getCodigo_biometria());
                biometria.setBiometria(wsbc.getDigital1());
                biometria.setBiometria2(wsbc.getDigital2());
                dao.update(biometria, true);
                try {
                    externalContext.getResponseOutputWriter().write(gson.toJson(wsbc));
                    facesContext.responseComplete();
                } catch (IOException ex) {

                }
            } catch (Exception e) {

            }

        } else if (caws.getAction().equals("update_aparelhos")) {
            Integer codigo_biometria = 0;
            Boolean ativo = false;
            String digital1 = "";
            String digital2 = "";
            Dao dao = new Dao();
            String wsbiometriacaptura = GenericaRequisicao.getParametro("WSBiometriaCaptura");
            WSBiometriaCaptura wsbc = gson.fromJson(wsbiometriacaptura, new WSBiometriaCaptura().getClass());
            try {
                if (wsbc != null) {
                    codigo_biometria = wsbc.getCodigo_biometria();
                    try {
                        ativo = wsbc.getAtiva();
                        digital1 = wsbc.getDigital1();
                        digital2 = wsbc.getDigital2();
                    } catch (Exception e) {

                    }
                } else {
                    codigo_biometria = Integer.parseInt(GenericaRequisicao.getParametro("codigo_biometria"));
                    try {
                        ativo = Boolean.parseBoolean(GenericaRequisicao.getParametro("ativo"));
                        digital1 = GenericaRequisicao.getParametro("digital1");
                        digital2 = GenericaRequisicao.getParametro("digital2");
                    } catch (Exception e) {

                    }
                }
                Biometria biometria = (Biometria) dao.find(new Biometria(), codigo_biometria);
                biometria.setDataAtualizacaoAparelho1(new Date());
                biometria.setDataAtualizacaoAparelho2(new Date());
                biometria.setDataAtualizacaoAparelho3(new Date());
                biometria.setDataAtualizacaoAparelho4(new Date());
                biometria.setAtivo(ativo);
                biometria.setBiometria(digital1);
                biometria.setBiometria2(digital2);
                dao.update(biometria, true);

            } catch (Exception e) {

            }
        } else if (caws.getAction().equals("check_error")) {
            try {
                Integer type = Integer.parseInt(GenericaRequisicao.getParametro("type"));
                Integer device = Integer.parseInt(GenericaRequisicao.getParametro("device"));
                Integer code = Integer.parseInt(GenericaRequisicao.getParametro("code"));
                BiometriaErroDao biometriaErroDao = new BiometriaErroDao();
                BiometriaErro biometriaErro;
                BiometriaErro be;
                if (type == 1) {
                    be = biometriaErroDao.findByDecice(code);
                    if (be != null) {
                        new Dao().delete(be, true);
                    }
                    new Dao().delete(be, true);
                    biometriaErro = new BiometriaErro();
                    biometriaErro.setNrCodigoErro(code);
                    biometriaErro.setNrDispositivo(device);
                    biometriaErro.setMacFilial(null);
                } else if (type == 2) {
                    be = biometriaErroDao.findByMac(macFilial.getFilial().getId());
                    if (be != null) {
                        new Dao().delete(be, true);
                    }
                    biometriaErro = new BiometriaErro();
                    biometriaErro.setNrCodigoErro(null);
                    biometriaErro.setNrDispositivo(device);
                    biometriaErro.setMacFilial(MacFilial.getAcessoFilial());
                }

            } catch (Exception e) {

            }
        }
    }

}
