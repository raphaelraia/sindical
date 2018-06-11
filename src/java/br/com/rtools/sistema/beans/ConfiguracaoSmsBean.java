package br.com.rtools.sistema.beans;

import br.com.rtools.configuracao.ConfiguracaoSms;
import br.com.rtools.configuracao.ConfiguracaoSmsGrupo;
import br.com.rtools.configuracao.dao.ConfiguracaoSmsDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.SMSWS;
import java.io.IOException;
import java.io.Serializable;
import java.net.ProtocolException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.primefaces.event.SelectEvent;

@ManagedBean
@ViewScoped
public class ConfiguracaoSmsBean implements Serializable {

    private ConfiguracaoSms configuracaoSms;
    private List<ConfiguracaoSms> listConfiguracaoSms;
    private List<SelectItem> listaGrupo = new ArrayList();
    private Integer idGrupo = null;
    private Registro registro;
    private String mobile_phone;
    private String message;
    private Date schedule;

    @PostConstruct
    public void init() {
        schedule = null;
        mobile_phone = "";
        message = "";
        Dao dao = new Dao();
        registro = (Registro) dao.find(new Registro(), 1);
        loadListGrupo();
        loadListConfiguracaoSms();

        configuracaoSms = (ConfiguracaoSms) new ConfiguracaoSmsDao().findPrincipal();

        if (configuracaoSms == null) {
            configuracaoSms = new ConfiguracaoSms();
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoSmsBean");
    }

    public void loadListConfiguracaoSms() {
        listConfiguracaoSms = new ArrayList<>();
        listConfiguracaoSms = new Dao().list(new ConfiguracaoSms());
    }

    public void loadListGrupo() {
        listaGrupo = new ArrayList();
        List<ConfiguracaoSmsGrupo> list = new Dao().list(new ConfiguracaoSmsGrupo());

        for (int i = 0; i < list.size(); i++) {
            listaGrupo.add(
                    new SelectItem(
                            list.get(i).getId(),
                            list.get(i).getDescricao(),
                            "" + list.get(i).getId()
                    )
            );
        }
    }

    public void enviaSms() {
        registro = (Registro) new Dao().rebind(registro);
        if (registro.getEnviaSms()) {
            registro.setEnviaSms(false);
        } else {
            registro.setEnviaSms(true);
        }
        new Dao().update(registro, true);
    }

    public void send() {
        if (mobile_phone.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar Número do Celular");
            return;
        }
        if (message.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar Mensagem");
            return;
        }
        SMSWS smsws = new SMSWS();
        if (schedule != null) {
            smsws.setScheduling_date(DataHoje.livre(schedule, "yyyy-MM-dd HH:mm:ss"));
        }
        smsws.setReference("TESTE");
        smsws.setMobile_phone(mobile_phone);
        smsws.setMessage(message);
        smsws.setShow_messages(true);
        smsws.send();
    }

    public void dataListener(SelectEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        schedule = DataHoje.converteDataHora(format.format(event.getObject()));
        System.out.println(schedule);
    }

    public void save() {
        Dao dao = new Dao();
        configuracaoSms.setConfiguracaoSmsGrupo((ConfiguracaoSmsGrupo) new Dao().find(new ConfiguracaoSmsGrupo(), idGrupo));
        if (configuracaoSms.getId() == null) {
            if (dao.save(configuracaoSms, true)) {
                GenericaMensagem.info("Sucesso", "Configurações aplicadas");
                configuracaoSms = new ConfiguracaoSms();
                loadListConfiguracaoSms();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        } else {
            if (dao.update(configuracaoSms, true)) {
                GenericaMensagem.info("Sucesso", "Configurações aplicadas");
                configuracaoSms = new ConfiguracaoSms();
                loadListConfiguracaoSms();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        }
    }

    public void edit(ConfiguracaoSms cs) {
        configuracaoSms = (ConfiguracaoSms) new Dao().rebind(cs);
    }

    public void defaults(ConfiguracaoSms cs) {
        for (int i = 0; i < listConfiguracaoSms.size(); i++) {
            listConfiguracaoSms.get(i).setPrincipal(false);
            if (Objects.equals(cs.getId(), listConfiguracaoSms.get(i).getId())) {
                listConfiguracaoSms.get(i).setPrincipal(true);
            }
            new Dao().update(listConfiguracaoSms.get(i), true);
            if (Objects.equals(cs.getId(), configuracaoSms.getId())) {
                configuracaoSms = listConfiguracaoSms.get(i);
            }
        }

    }

    public ConfiguracaoSms getConfiguracaoCnpj() {
        return configuracaoSms;
    }

    public void setConfiguracaoCnpj(ConfiguracaoSms configuracaoSms) {
        this.configuracaoSms = configuracaoSms;
    }

    public void load() {

    }

    public String getSaldo() throws ProtocolException, IOException {
//        Charset charset = Charset.forName("UTF-8");
//        Integer status;
//        String statusBoolean = "OK";
//        String error = "";
//        String message = "";
//        String query = "";
//        String method = "GET";
//        // 2235824594887334ABV16325666555
//        ConfiguracaoSms cc = (ConfiguracaoSms) new Dao().find(new ConfiguracaoSms(), 1);
//        if (cc == null) {
//            GenericaMensagem.warn("Sistema", "Configuração do CNPJ não encontrada!");
//            return null;
//        }
//        if (cc.getTipoPesquisaCnpj().getId() == 5) {
//            query = "http://ws.hubdodesenvolvedor.com.br/v2/saldo/?";
//            query += "info";
//            query += "&";
//            query += "token=" + URLEncoder.encode(cc.getToken(), "UTF-8");
//
//        }
//        URL url = new URL(query);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod(method);
//// con.setRequestProperty("Content-length", String.valueOf(query.length()));
//        con.setRequestProperty("User-Agent", "Mozilla/5.0");
//        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//        con.setDoInput(true);
//        con.setDoOutput(true);
//        int responseCode = con.getResponseCode();
//        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
//        String inputLine;
//        StringBuilder response = new StringBuilder();
//        try {
//            while ((inputLine = br.readLine()) != null) {
//                response.append(inputLine);
//            }
//            br.close();
//        } catch (Exception e) {
//
//        }
//        String rensponseString = response.toString();
//        JSONObject result = new JSONObject(rensponseString);
//        if (cc.getTipoPesquisaCnpj().getId() == 5) {
//            try {
//                statusBoolean = "" + result.getBoolean("status");
//                error = result.getString("return");
//            } catch (Exception e) {
//                try {
//                    statusBoolean = result.getString("status");
//                    // error = result.getString("return");
//                } catch (Exception e2) {
//                }
//            }
//            try {
//                message = result.getString("message");
//            } catch (Exception e) {
//            }
//            //          ERRO PARA FALTA DE CRÉDITOS
//            if (statusBoolean.equals("NOK") || statusBoolean.equals("false")) {
//                return "CONTATE O ADMINISTRADOR DO SISTEMA (STATUS 7)!";
//            }
//
//            //          ERRO PARA DEMAIS STATUS -- NÃO CONSEGUIU PESQUISAR
//            if (statusBoolean.equals("NOK") || statusBoolean.equals("false")) {
//                return "NOK";
//            }
//
//            //          ERRO PARA DEMAIS STATUS -- NÃO CONSEGUIU PESQUISAR
//            if (statusBoolean.equals("NOK")) {
//                return ". TENTE NOVAMENTE MAIS TARDE!!! SISTEMA DA RECEITA ESTA APRESENTANDO INSTABILIDADE NO MOMENTO!!!. ";
//            }
//
//            JSONArray obj = result.getJSONArray("result");
//            try {
//                String r = result.getJSONArray("result").toString().replace("[", "");
//                r = r.replace("]", "");
//                JSONObject object = new JSONObject(r);
//                return object.getString("saldo");
//            } catch (JSONException e) {
//
//            }
//
//        }
        return "0";
    }

    public ConfiguracaoSms getConfiguracaoSms() {
        return configuracaoSms;
    }

    public void setConfiguracaoSms(ConfiguracaoSms configuracaoSms) {
        this.configuracaoSms = configuracaoSms;
    }

    public List<ConfiguracaoSms> getListConfiguracaoSms() {
        return listConfiguracaoSms;
    }

    public void setListConfiguracaoSms(List<ConfiguracaoSms> listConfiguracaoSms) {
        this.listConfiguracaoSms = listConfiguracaoSms;
    }

    public List<SelectItem> getListaGrupo() {
        return listaGrupo;
    }

    public void setListaGrupo(List<SelectItem> listaGrupo) {
        this.listaGrupo = listaGrupo;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

}
