package br.com.rtools.utilitarios;

import br.com.rtools.configuracao.ConfiguracaoSms;
import br.com.rtools.configuracao.dao.ConfiguracaoSmsDao;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.seguranca.Cliente;
import br.com.rtools.seguranca.Registro;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SMSWS {

    private String mobile_phone;
    private String reference;
    private String message;
    private String scheduling_date;
    private ConfiguracaoSms configuracaoSms;
    private Boolean show_messages;
    private Filial filial;

    public SMSWS() {
        filial = null;
        show_messages = false;
        configuracaoSms = new ConfiguracaoSmsDao().findPrincipal();
        mobile_phone = "";
        message = "";
        reference = "";
        scheduling_date = "";
    }

    public void send() {

        if (configuracaoSms != null) {
            if (configuracaoSms.getConfiguracaoSmsGrupo().getDescricao().equals("TWW")) {
                tww("send");
            }

        }
    }

    public void delete() {

        if (configuracaoSms != null) {
            if (configuracaoSms.getConfiguracaoSmsGrupo().getDescricao().equals("TWW")) {
                tww("delete");
            }

        }
    }

    public void tww(String tcase) {
        String urlString = configuracaoSms.getUrlServico();
        switch (tcase) {
            case "send":
                if (scheduling_date != null && !scheduling_date.isEmpty()) {
                    urlString += "/EnviaSMSAge";
                } else {
                    urlString += "/EnviaSMS";
                }
                if (reference.isEmpty()) {
                    if (show_messages) {
                        Messages.warn("Validação", "Informar referência!");
                    }
                    return;
                }
                break;
            case "delete":
                urlString += "/DelSMSAgenda?";
                if (reference.isEmpty()) {
                    if (show_messages) {
                        Messages.warn("Validação", "Informar referência!");
                    }
                    return;
                }
                break;
            case "find":
                urlString += "/BuscaSMSAgenda";
                if (reference.isEmpty()) {
                    if (show_messages) {
                        Messages.warn("Validação", "Informar referência!");
                    }
                    return;
                }
                break;
            default:
                break;
        }
        Properties parameters = new Properties();
        parameters.setProperty("NumUsu", configuracaoSms.getLogin());
        parameters.setProperty("Senha", configuracaoSms.getSenha());
        parameters.setProperty("SeuNum", Cliente.get() + "_" + reference);
        if (mobile_phone != null && !mobile_phone.isEmpty()) {
            mobile_phone = mobile_phone.replace(" ", "");
            mobile_phone = mobile_phone.replace("(", "");
            mobile_phone = mobile_phone.replace(")", "");
            mobile_phone = mobile_phone.replace("-", "");
            mobile_phone = mobile_phone.replace(".", "");
            mobile_phone = mobile_phone.replace("-", "");
            parameters.setProperty("Celular", mobile_phone);
        }
        if (scheduling_date != null && !scheduling_date.isEmpty()) {
            if (scheduling_date.contains("/")) {
                parameters.setProperty("Agendamento", DataHoje.livre(DataHoje.converteDataHora(scheduling_date), "yyyy-MM-dd HH:mm:ss"));
            } else {
                parameters.setProperty("Agendamento", scheduling_date);
            }
        }
        Filial f;
        if (filial == null) {
            Registro r = new Registro().get();
            Juridica j = r.getFilial();
            f = new FilialDao().findByJuridica(j.getId());
        } else {
            f = filial;
        }
        switch (tcase) {
            case "send":
                if (f.getApelido() == null || f.getApelido().isEmpty()) {
                    String jur = AnaliseString.removerAcentos(f.getFilial().getPessoa().getNome());
                    String respSplit[] = jur.split(" ");
                    for (int i = 0; i < respSplit.length; i++) {
                        message = respSplit[i] + " " + message;
                        break;
                    }
                } else {
                    message = AnaliseString.removerAcentos(f.getApelido()) + " - " + message;
                }
                message = AnaliseString.removerAcentos(message);
                message = reduce(message);
                if (message.isEmpty()) {
                    return;
                }
                break;
        }
        parameters.setProperty("Mensagem", message);
        Iterator i = parameters.keySet().iterator();
        int counter = 0;
        String parans = "";
        while (i.hasNext()) {
            String name = (String) i.next();
            String value = "";
            try {
                value = URLEncoder.encode(parameters.getProperty(name), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SMSWS.class.getName()).log(Level.SEVERE, null, ex);
            }
            urlString += (++counter == 1 ? "?" : "&") + name + "=" + value;
        }
        System.out.println(urlString);
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Request-Method", "GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            StringBuilder newData = new StringBuilder();
            String s = "";
            while (null != ((s = br.readLine()))) {
                newData.append(s);
            }
            String response = newData.toString();
            XStream xstream = new XStream(new DomDriver());
            // String result = xstream.fromXML(newData);
            if (response.equals("OK") || (response.contains("OK") && !response.contains("NOK"))) {
                if (show_messages) {
                    Messages.info("Sucesso", "SMS Enviado!");
                }
            }
            if (response.equals("NOK")) {
                if (show_messages) {
                    Messages.warn("Validação", "Erro ao enviar mensagem!");
                }
            }
            connection.disconnect();
            br.close();
        } catch (Exception e) {
            e.getCause();
        }
    }

    public String reduce(String message) {
        try {
            if (message.length() > 160) {
                message = message.substring(0, 160);
            }
        } catch (Exception e) {

        }
        return message;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setReferenceInteger(Integer reference) {
        try {
            this.reference = reference + "";
        } catch (Exception e) {

        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScheduling_date() {
        return scheduling_date;
    }

    public void setScheduling_date(String scheduling_date) {
        this.scheduling_date = scheduling_date;
    }

    public Date getDate() {
        return DataHoje.converteDateSqlToDate(scheduling_date);
    }

    /**
     * Define o dia do envio, 24 horas antes
     *
     * @param days_before (Número de dias para lembrar)
     * @param date (Data referência)
     * @param time (Horário)
     */
    public void schedule_to(Integer days_before, String date, String time) {
        DataHoje dh = new DataHoje();
        Date d = DataHoje.converteDataHora(dh.decrementarDias(1, date), time);
        String dataString = DataHoje.livre(d, "");
        scheduling_date = dataString;
    }

    /**
     * Define o dia do envio, 24 horas antes
     *
     * @param days_before (Número de dias para lembrar)
     * @param date (Data referência)
     * @param time (Horário)
     */
    public void schedule_to(Integer days_before, Date date, String time) {
        DataHoje dh = new DataHoje();
        Date d = DataHoje.converteDataHora(dh.decrementarDias(1, DataHoje.converteData(date)), time);
        String dataString = DataHoje.livre(d, "");
        scheduling_date = dataString;
    }

    public ConfiguracaoSms getConfiguracaoSms() {
        return configuracaoSms;
    }

    public void setConfiguracaoSms(ConfiguracaoSms configuracaoSms) {
        this.configuracaoSms = configuracaoSms;
    }

    public Boolean getShow_messages() {
        return show_messages;
    }

    public void setShow_messages(Boolean show_messages) {
        this.show_messages = show_messages;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

}
