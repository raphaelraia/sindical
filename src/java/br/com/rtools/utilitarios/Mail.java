package br.com.rtools.utilitarios;

import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailArquivo;
import br.com.rtools.sistema.ConfiguracaoDepartamento;
import br.com.rtools.sistema.EmailLote;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.EmailPrioridade;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@ManagedBean
@SessionScoped
public class Mail extends MailTemplate implements Serializable {
    
    private Email email;
    private Registro registro;
    private List<Email> emails;
    private List<EmailPessoa> emailPessoas;
    private List<File> files;
    private boolean saveFiles;
    private EmailArquivo emailArquivo;
    private List<EmailArquivo> emailArquivos;
    private String html;
    private String personal;
    private Boolean message_hidden;
    private Boolean unique;
    private ConfiguracaoDepartamento configuracaoDepartamento;
    private EmailLote emailLote;
    
    public Mail() {
        emailLote = null;
        unique = false;
        email = new Email();
        registro = new Registro();
        emails = new ArrayList();
        emailPessoas = new ArrayList();
        files = new ArrayList();
        saveFiles = false;
        emailArquivo = new EmailArquivo();
        emailArquivos = new ArrayList();
        html = "";
        message_hidden = false;
        configuracaoDepartamento = null;
    }
    
    public Mail(Email email, Registro registro, List<Email> emails, List<EmailPessoa> emailPessoas, List<File> files, boolean saveFiles, EmailArquivo emailArquivo, List<EmailArquivo> emailArquivos, String html, String personal, ConfiguracaoDepartamento configuracaoDepartamento) {
        this.email = email;
        this.registro = registro;
        this.emails = emails;
        this.emailPessoas = emailPessoas;
        this.files = files;
        this.saveFiles = saveFiles;
        this.emailArquivo = emailArquivo;
        this.emailArquivos = emailArquivos;
        this.html = html;
        this.personal = personal;
        this.configuracaoDepartamento = configuracaoDepartamento;
    }
    
    public String[] send() {
        return send("");
    }
    
    public String[] send(String templateHtml) {
        String[] strings = new String[]{"", "", "", ""};
        if (getRegistro() == null || getRegistro().getId() == -1) {
            strings[0] = "Informar registro!";
            return strings;
        }
        DaoInterface di = new Dao();
        ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
        cab.init();
        Juridica sindicato = cab.getConfiguracaoArrecadacao().getFilial().getFilial();
        String xEndereco = sindicato.getPessoa().getPessoaEndereco().getEnderecoCompletoString();
        String xTelefone = sindicato.getPessoa().getTelefone1();
        String xSite = sindicato.getPessoa().getSite();
        //Juridica sindicato = (Juridica) di.find(new Juridica(), 1);
        if (personal == null || personal.isEmpty()) {
            personal = sindicato.getPessoa().getNome();
        }
        if (strings[0].isEmpty()) {
            if (!emailPessoas.isEmpty()) {
                if (emailArquivos == null) {
                    emailArquivos = new ArrayList();
                }
                String xEmail = registro.getEmail();
                String xSenha = registro.getSenha();
                String xEmailResposta = registro.getSisEmailResposta();
                Integer xPorta = registro.getSisEmailPorta();
                Integer xSisEmailProtocoloId = registro.getSisEmailProtocolo().getId();
                String xSmtp = registro.getSmtp();
                Boolean xAutenticado = registro.isEmailAutenticado();
                String xEmailSindicato = sindicato.getPessoa().getEmail1();
                String xNome = sindicato.getPessoa().getNome();
                String xAssinatura = "";
                String xLogo = registro.getUrlLogo();
                if (configuracaoDepartamento != null) {
                    if (!configuracaoDepartamento.getServidorSmtp()) {
                        xEmail = configuracaoDepartamento.getEmail();
                    }
                    if (!configuracaoDepartamento.getServidorSmtp()) {
                        xSenha = configuracaoDepartamento.getSenha();
                    }
                    if (!configuracaoDepartamento.getEmailResposta().isEmpty()) {
                        xEmailResposta = configuracaoDepartamento.getEmailResposta();
                    }
                    if (!configuracaoDepartamento.getServidorSmtp()) {
                        xPorta = configuracaoDepartamento.getPorta();
                        xSisEmailProtocoloId = configuracaoDepartamento.getSisEmailProtocolo().getId();
                        xSmtp = configuracaoDepartamento.getSmtp();
                        xAutenticado = configuracaoDepartamento.getAutenticado();
                    }
                    xEmailSindicato = configuracaoDepartamento.getEmail();
                    xNome = configuracaoDepartamento.getFilial().getFilial().getPessoa().getNome() + " <br />Depto " + configuracaoDepartamento.getDepartamento().getDescricao();
                    xAssinatura = configuracaoDepartamento.getAssinatura();
                    xEndereco = configuracaoDepartamento.getFilial().getFilial().getPessoa().getPessoaEndereco().getEnderecoCompletoString();
                    xTelefone = configuracaoDepartamento.getFilial().getFilial().getPessoa().getTelefone1();
                }
                xNome = AnaliseString.converterCapitalize(xNome);
                xAssinatura = AnaliseString.converterCapitalize(xAssinatura);
                personal = AnaliseString.converterCapitalize(personal);
                boolean saveArquivosEmail = false;
                for (int i = 0; i < emailPessoas.size(); i++) {
                    Boolean success = true;
                    boolean updateEmail = false;
                    String token_email = "";
                    String message_excepcion = "";
                    try {
                        Session session;
                        if (registro.isSisEmailMarketing()) {
                            session = EnviarEmail.configureSession(registro.getSmtp(), registro.getSisEmailPorta(), registro.getEmail(), registro.getSenha(), registro.isEmailAutenticado(), registro.getSisEmailProtocolo().getId());
                            // session = EnviarEmail.configureSession(EmailMarketing.getHOSTNAME_COMERCIORP(), EmailMarketing.getPORT_COMERCIORP(), EmailMarketing.getLOGIN_COMERCIORP(), EmailMarketing.getPASSWORD_COMERCIORP(), EmailMarketing.isAUTH_COMERCIORP(), EmailMarketing.getPROTOCOL_COMERCIORP());
//                            if (ControleUsuarioBean.getCliente().equals("Sindical") || ControleUsuarioBean.getCliente().equals("ComercioRP")) {
//                                session = EnviarEmail.configureSession(EmailMarketing.getHOSTNAME_COMERCIORP(), EmailMarketing.getPORT_COMERCIORP(), EmailMarketing.getLOGIN_COMERCIORP(), EmailMarketing.getPASSWORD_COMERCIORP(), EmailMarketing.isAUTH_COMERCIORP(), EmailMarketing.getPROTOCOL_COMERCIORP());
//                            } else {
//                                session = EnviarEmail.configureSession(EmailMarketing.getHOSTNAME(), EmailMarketing.getPORT(), EmailMarketing.getLOGIN(), EmailMarketing.getPASSWORD(), EmailMarketing.isAUTH(), EmailMarketing.getPROTOCOL());
//                            }
                        } else {
                            session = EnviarEmail.configureSession(xSmtp, xPorta, xEmail, xSenha, xAutenticado, xSisEmailProtocoloId);
                        }
                        if (session == null) {
                            strings[0] = "Não foi possível realizar autenticação!";
                        }
                        MimeMessage msg = new MimeMessage(session);
                        InternetAddress internetAddress = new InternetAddress();
                        if (registro.isSisEmailMarketing()) {
                            if (configuracaoDepartamento != null) {
                                if (!configuracaoDepartamento.getEmail().isEmpty()) {
                                    msg.setFrom(new InternetAddress(configuracaoDepartamento.getEmail(), personal));
                                    if (!configuracaoDepartamento.getEmailResposta().isEmpty()) {
                                        Address address[] = {new InternetAddress(configuracaoDepartamento.getEmailResposta())};
                                        msg.setReplyTo(address);
                                    }
                                }
                            } else {
                                msg.setFrom(new InternetAddress(registro.getSisEmailResposta(), personal));
                                if (!registro.getSisEmailMarketingResposta().isEmpty()) {
                                    Address address[] = {new InternetAddress(registro.getSisEmailMarketingResposta())};
                                    msg.setReplyTo(address);
                                }
                            }
                        } else if (!xEmailResposta.isEmpty()) {
                            internetAddress.setPersonal(xEmailResposta);
                            msg.setFrom(internetAddress);
                        } else {
                            msg.setFrom(new InternetAddress(xEmail));
                        }
                        String to = "";
                        if (emailPessoas.get(i).getPessoa() != null) {
                            if (!emailPessoas.get(i).getPessoa().getEmail1().isEmpty()) {
                                to = emailPessoas.get(i).getPessoa().getEmail1();
                            } else if (!emailPessoas.get(i).getDestinatario().isEmpty()) {
                                to = emailPessoas.get(i).getDestinatario();
                            }
                        } else {
                            if (emailPessoas.get(i).getDestinatario().isEmpty()) {
                                strings[0] = "Informar e-mail do destinatário!";
                                return strings;
                            }
                            to = emailPessoas.get(i).getDestinatario();
                        }
                        String assuntox = AnaliseString.converterCapitalize(email.getAssunto());
                        String uuid = "";
                        if (unique) {
                            if (!assuntox.isEmpty() && !assuntox.contains("UUID: ")) {
                                uuid += UUID.randomUUID().toString();
                                token_email = uuid;
                                assuntox += " - [ID:" + uuid.substring(0, 8).toUpperCase() + "]";
                            }
                        } else {
                            token_email = UUID.randomUUID().toString();
                        }
                        token_email = token_email.replace("-", "");
                        token_email = token_email.replace("_", "");
                        token_email = token_email.toLowerCase();
                        token_email = stringToMD5(token_email);
                        xEndereco = AnaliseString.converterCapitalize(xEndereco);
                        String htmlString = "";
                        if (html.isEmpty()) {
                            if (templateHtml.isEmpty()) {
                                htmlString = ""
                                        + "<html>"
                                        + "     <body style='background-color: white'>"
                                        + "         <h2><b>" + xNome + "</b></h2><br /><br />"
                                        //+ "         <h2><b>" + registro.getFilial().getPessoa().getNome() + "</b></h2><br /><br />"
                                        + "         <p> " + email.getMensagem() + "</p>"
                                        + "         <br /><br />"
                                        + "         " + xAssinatura;
                                if (unique) {
                                    htmlString += "<br /><br /><br /><br /><p><i>" + " - [ID:" + uuid.substring(0, 8).toUpperCase() + "]" + "<i></p>";
                                }
                                htmlString += "     </body>"
                                        + "</html>";
                            } else if (templateHtml.equals("cerberus")) {
                                String message = email.getMensagem();
                                htmlString += getCerberus(xLogo, message, "", xSite, xNome, xEndereco, xTelefone, "", token_email);
                            } else if (templateHtml.equals("personalizado")) {
                                Juridica jur = (new JuridicaDao()).pesquisaJuridicaPorPessoa(emailPessoas.get(i).getPessoa().getId());
                                if (jur == null) {
                                    jur = sindicato;
                                    //jur = registro.getFilial();
                                }
                                htmlString += ""
                                        + "<html>"
                                        + "     <body style='background-color: white'>"
                                        + "         <h2>                                                            "
                                        + "             <b>" + xNome + "</b>   "
                                        //+ "             <b>" + registro.getFilial().getPessoa().getNome() + "</b>   "
                                        + "         <h3>                                                            "
                                        + "             A/C                                                         "
                                        + "         </h3><b> " + jur.getContato() + " </b><br /><br />              "
                                        + "         </h2><br /><br />                                               "
                                        + "         <h4> " + email.getMensagem() + "</h4><br /><br />               "
                                        + "         " + xAssinatura
                                        + "                                                                     ";
                                if (!sindicato.getPessoa().getEmail1().equals(xEmailResposta)) {
                                    //if (!registro.getFilial().getPessoa().getEmail1().equals(registro.getSisEmailResposta())) {
                                    htmlString += "<h3>Caso queira entrar em contato envie para: <strong>" + xEmailSindicato + "</strong></h3>";
                                    //htmlString += "<h3>Caso queira entrar em contato envie para: <strong>" + registro.getFilial().getPessoa().getEmail1() + "</strong></h3>";
                                }
                                if (unique) {
                                    htmlString += "<br /><br /><br /><br /><p><i>" + " - [ID:" + uuid.substring(0, 8).toUpperCase() + "]" + "<i></p>";
                                }
                                htmlString
                                        += "         <br /><br />"
                                        + "     </body>"
                                        + "</html>";
                            }
                        } else {
                            htmlString = html;
                        }
                        // SOLVED http://stackoverflow.com/questions/3902455/smtp-multipart-alternative-vs-multipart-mixed
                        // MimeMultipart multipart = new MimeMultipart("related");
                        MimeMultipart multipart = new MimeMultipart("mixed");
                        BodyPart mainPart = new MimeBodyPart();
                        if (!files.isEmpty()) {
                            EmailArquivo emailArquivoS = new EmailArquivo();
                            for (File f : files) {
                                BodyPart imagePart = new MimeBodyPart();
                                DataSource imgFds = new FileDataSource(f);
                                imagePart.setDataHandler(new DataHandler(imgFds));
                                imagePart.setFileName(f.getName());
                                multipart.addBodyPart(imagePart);
                                if (!saveArquivosEmail) {
                                    emailArquivoS.getArquivo().setExtensao("");
                                    emailArquivoS.getArquivo().setNome("");
                                    emailArquivos.add(emailArquivoS);
                                    emailArquivoS = new EmailArquivo();
                                }
                            }
                        }
                        mainPart.setContent(htmlString, "text/html; charset=utf-8");
                        multipart.addBodyPart(mainPart);
                        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        if (!emailPessoas.get(i).getCc().isEmpty()) {
                            msg.setRecipient(Message.RecipientType.CC, new InternetAddress(emailPessoas.get(i).getCc()));
                        }
                        if (!emailPessoas.get(i).getBcc().isEmpty()) {
                            msg.setRecipient(Message.RecipientType.BCC, new InternetAddress(emailPessoas.get(i).getBcc()));
                        }
                        msg.setSubject(assuntox);
                        email.setAssunto(assuntox);
                        msg.setContent(multipart);
                        msg.setSentDate(new Date());
                        //msg.setHeader("X-Mailer", "Tov Are's program");
                        // TESTES 07/06/2018
                        msg.setHeader("Content-ID", "<" + uuid + ">");
                        // NOVO HEADER PARA TESTE 07/06/2018
                        msg.setHeader("Message-ID", "<" + getUniqueMessageIDValue(session) + ">");
                        // msg.setHeader("Disposition-Notification-To", xEmailSindicato);
                        if (xEmail.contains("gmail") || xEmail.contains("googlemail")) {
                            Transport transport = session.getTransport("smtps");
                            transport.connect(xSmtp, xPorta, xEmail, xSenha);
                            transport.sendMessage(msg, msg.getAllRecipients());
                            transport.close();
                        } else {
                            if (xAutenticado) {
                                Transport transport = session.getTransport("smtps");
                                transport.connect(xSmtp, xPorta, xEmail, xSenha);
                                transport.sendMessage(msg, msg.getAllRecipients());
                                transport.close();
                            } else {
                                Transport transport = session.getTransport("smtp");
                                transport.connect(xSmtp, xPorta, xEmail, xSenha);
                                transport.sendMessage(msg, msg.getAllRecipients());
                                transport.close();
                            }
                            // Transport.send(msg);
                        }
                        success = true;
                        strings[0] = "Enviado com Sucesso.";
                    } catch (AddressException e) {
                        success = false;
                        strings[1] = "Email de destinatário inválido!";
                        strings[1] += " - " + returnExceptionText(e.getMessage());
                        message_excepcion = strings[1];
                    } catch (MessagingException e) {
                        success = false;
                        if (e.getCause() != null && e.getCause().toString().contains("PKIX")) {
                            strings[1] = "" + e.getCause().toString();
                        } else {
                            strings[1] = "" + returnExceptionText(e.getMessage());
                            if (strings[1].contains("Invalid Addresses")) {
                                strings[1] += ": " + emailPessoas.get(i).getDestinatario();
                            } else if (strings[1].contains("Dominio com caracteres errados")) {
                                strings[1] += ": " + emailPessoas.get(i).getDestinatario();
                            }
                        }
                        message_excepcion = strings[1];
                    } catch (UnsupportedEncodingException ex) {
                        success = false;
                        strings[1] = "Erro ";
                        strings[1] += " - " + returnExceptionText(ex.getMessage());
                        message_excepcion = strings[1];
                    }
                    if (emailPessoas.get(i).getPessoa() == null || emailPessoas.get(i).getPessoa().getId() == -1) {
                        emailPessoas.get(i).setPessoa(null);
                    }
                    if (email.getId() == -1) {
                        email.setId(null);
                    }
                    if (!success) {
                        email.setErro(message_excepcion);
                    }
                    if (email.getId() == null) {
                        if (emailLote != null) {
                            email.setEmailLote(emailLote);
                        }
                        email.setData(new Date());
                        email.setHora(DataHoje.livre(new Date(), "HH:mm"));
                        if (email.getUsuario() != null && email.getUsuario().getId() == -1) {
                            email.setUsuario((Usuario) GenericaSessao.getObject("sessaoUsuario"));
                        }
                        if (email.getEmailPrioridade() == null) {
                            email.setEmailPrioridade((EmailPrioridade) di.find(new EmailPrioridade(), 1));
                        } else {
                            email.setEmailPrioridade((EmailPrioridade) di.find(new EmailPrioridade(), email.getEmailPrioridade().getId()));
                        }
                        if (message_hidden) {
                            email.setMensagem("");
                        }
                        if (di.save(email, true)) {
                            emailPessoas.get(i).setUuid(token_email);
                            emailPessoas.get(i).setEmail(email);
                            emailPessoas.get(i).setHoraSaida(DataHoje.livre(new Date(), "HH:mm"));
                            di.save(emailPessoas.get(i), true);
                        }
                        if (!saveArquivosEmail) {
                            if (!emailArquivos.isEmpty()) {
                                for (EmailArquivo ea : emailArquivos) {
                                    ea.setEmail(email);
                                    if (di.save(ea.getArquivo(), true)) {
                                        if (di.save(ea, true)) {
                                        }
                                    }
                                }
                                saveArquivosEmail = true;
                                emailArquivos.size();
                            }
                        }
                    } else {
                        if (!updateEmail) {
                            di.update(email, true);
                            updateEmail = true;
                        }
                        emailPessoas.get(i).setEmail(email);
                        emailPessoas.get(i).setHoraSaida(DataHoje.livre(new Date(), "HH:mm"));
                        if (emailPessoas.get(i).getId() == null) {
                            di.save(emailPessoas.get(i), true);
                        } else {
                            di.update(emailPessoas.get(i), true);
                        }
                    }
                }
            }
        }
        return strings;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public void setEmail(Email email) {
        this.email = email;
    }
    
    public Registro getRegistro() {
        if (registro == null || registro.getId() == -1) {
            registro = (Registro) new Dao().find(new Registro(), 1);
        }
        return registro;
    }
    
    public void setRegistro(Registro registro) {
        this.registro = registro;
    }
    
    public List<Email> getEmails() {
        return emails;
    }
    
    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }
    
    public List<EmailPessoa> getEmailPessoas() {
        return emailPessoas;
    }
    
    public void setEmailPessoas(List<EmailPessoa> emailPessoas) {
        this.emailPessoas = emailPessoas;
    }
    
    public EmailArquivo getEmailArquivo() {
        return emailArquivo;
    }
    
    public void setEmailArquivo(EmailArquivo emailArquivo) {
        this.emailArquivo = emailArquivo;
    }
    
    public List<EmailArquivo> getEmailArquivos() {
        return emailArquivos;
    }
    
    public void setEmailArquivos(List<EmailArquivo> emailArquivos) {
        this.emailArquivos = emailArquivos;
    }
    
    public List<File> getFiles() {
        return files;
    }
    
    public void setFiles(List<File> files) {
        this.files = files;
    }
    
    public void addFile(File f) {
        files.add(f);
    }
    
    public String getHtml() {
        return html;
    }
    
    public void setHtml(String html) {
        this.html = html;
    }
    
    public String getPersonal() {
        return personal;
    }
    
    public void setPersonal(String personal) {
        this.personal = personal;
    }
    
    public boolean isSaveFiles() {
        return saveFiles;
    }
    
    public void setSaveFiles(boolean saveFiles) {
        this.saveFiles = saveFiles;
    }
    
    public Boolean getMessage_hidden() {
        return message_hidden;
    }
    
    public void setMessage_hidden(Boolean message_hidden) {
        this.message_hidden = message_hidden;
    }
    
    public ConfiguracaoDepartamento getConfiguracaoDepartamento() {
        return configuracaoDepartamento;
    }
    
    public void setConfiguracaoDepartamento(ConfiguracaoDepartamento configuracaoDepartamento) {
        this.configuracaoDepartamento = configuracaoDepartamento;
    }
    
    public String returnExceptionText(String e) {
        try {
            if (e.contains("Could not convert socket to TLS")) {
                
                return "Não foi possível converter socket para TLS";
            } else if (e.contains("504 Invalid Username or Password")) {
                return "Login (Email) ou senha inválida";
            } else if (e.contains("Could not connect to SMTP host")) {
                return "Não foi possível converter socket para SMTP";
            } else if (e.contains("Domain contains illegal character")) {
                return "Dominio com caracteres errados";
            }
        } catch (Exception ex) {
            
        }
        return e;
    }
    
    public String oauth() throws IOException {
//        Oauth2 oauth2 = null;
//        oauth2 = new Oauth2(null, null, null);
        return null;
    }

    // https://stackoverflow.com/questions/17818501/set-messageid-in-header-before-sending-mail
    public static String getUniqueMessageIDValue(Session ssn) {
        
        String suffix = null;
        
        InternetAddress addr = InternetAddress.getLocalAddress(ssn);
        if (addr != null) {
            suffix = addr.getAddress();
        } else {
            suffix = "javamailuser@localhost"; // worst-case default
        }
        
        StringBuffer s = new StringBuffer();

        // Unique string is <hashcode>.<id>.<currentTime>.JavaMail.<suffix>
        s.append(s.hashCode()).append('.').append(getUniqueId()).append('.').
                append(System.currentTimeMillis()).append('.').
                append("JavaMail.").
                append(suffix);
        return s.toString();
    }
    
    private static synchronized String getUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    public Boolean getUnique() {
        return unique;
    }
    
    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    /**
     *
     * @param logo
     * @param content
     * @param extra
     * @param site
     * @param company_name
     * @param address
     * @param phone
     * @param unsubscribe
     * @param token
     * @return
     */
    public String getCerberus(String logo, String content, String extra, String site, String company_name, String address, String phone, String unsubscribe, String token) {
        address = AnaliseString.converterCapitalize(address);
        String linkRecebimento1 = registro.getUrlSistemaExterno() + "ws/email_confirma_recebimento_title.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;token=" + token;
        String linkRecebimento2 = registro.getUrlSistemaExterno() + "ws/email_confirma_recebimento.jsf?cliente=" + ControleUsuarioBean.getCliente() + "&amp;token=" + token;
        String tpl = ""
                + "<!DOCTYPE html>\n"
                + "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n"
                + "    <head>\n"
                + "        <meta charset=\"utf-8\" /> <!-- utf-8 works for most cases -->\n"
                + "        <meta name=\"viewport\" content=\"width=device-width\" /> <!-- Forcing initial-scale shouldn't be necessary -->\n"
                + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" /> <!-- Use the latest (edge) version of IE rendering engine -->\n"
                + "        <meta name=\"x-apple-disable-message-reformatting\" />  <!-- Disable auto-scale in iOS 10 Mail entirely -->\n"
                + "        <title></title> <!-- The title tag shows in email notifications, like Android 4.4. -->\n"
                + "\n"
                + "        <!-- Web Font / @font-face : BEGIN -->\n"
                + "        <!-- NOTE: If web fonts are not required, lines 10 - 27 can be safely removed. -->\n"
                + "\n"
                + "        <!-- Desktop Outlook chokes on web font references and defaults to Times New Roman, so we force a safe fallback font. -->\n"
                + "        <!--[if mso]>\n"
                + "            <style>\n"
                + "                * {\n"
                + "                    font-family: sans-serif !important;\n"
                + "                }\n"
                + "            </style>\n"
                + "        <![endif]-->\n"
                + "\n"
                + "        <!-- All other clients get the webfont reference; some will render the font and others will silently fail to the fallbacks. More on that here: http://stylecampaign.com/blog/2015/02/webfont-support-in-email/ -->\n"
                + "        <!--[if !mso]><!-->\n"
                + "        <!-- insert web font reference, eg: <link href='https://fonts.googleapis.com/css?family=Roboto:400,700' rel='stylesheet' type='text/css'> -->\n"
                + "        <!--<![endif]-->\n"
                + "\n"
                + "        <!-- Web Font / @font-face : END -->\n"
                + "\n"
                + "        <!-- CSS Reset : BEGIN -->\n"
                + "        <style>\n"
                + "\n"
                + "            /* What it does: Remove spaces around the email design added by some email clients. */\n"
                + "            /* Beware: It can remove the padding / margin and add a background color to the compose a reply window. */\n"
                + "            html,\n"
                + "            body {\n"
                + "                margin: 0 auto !important;\n"
                + "                padding: 0 !important;\n"
                + "                height: 100% !important;\n"
                + "                width: 100% !important;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Stops email clients resizing small text. */\n"
                + "            * {\n"
                + "                -ms-text-size-adjust: 100%;\n"
                + "                -webkit-text-size-adjust: 100%;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Centers email on Android 4.4 */\n"
                + "            div[style*=\"margin: 16px 0\"] {\n"
                + "                margin: 0 !important;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Stops Outlook from adding extra spacing to tables. */\n"
                + "            table,\n"
                + "            td {\n"
                + "                mso-table-lspace: 0pt !important;\n"
                + "                mso-table-rspace: 0pt !important;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Fixes webkit padding issue. Fix for Yahoo mail table alignment bug. Applies table-layout to the first 2 tables then removes for anything nested deeper. */\n"
                + "            table {\n"
                + "                border-spacing: 0 !important;\n"
                + "                border-collapse: collapse !important;\n"
                + "                table-layout: fixed !important;\n"
                + "                margin: 0 auto !important;\n"
                + "            }\n"
                + "            table table table {\n"
                + "                table-layout: auto;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Prevents Windows 10 Mail from underlining links despite inline CSS. Styles for underlined links should be inline. */\n"
                + "            a {\n"
                + "                text-decoration: none;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Uses a better rendering method when resizing images in IE. */\n"
                + "            img {\n"
                + "                -ms-interpolation-mode:bicubic;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: A work-around for email clients meddling in triggered links. */\n"
                + "            *[x-apple-data-detectors],  /* iOS */\n"
                + "            .unstyle-auto-detected-links *,\n"
                + "            .aBn {\n"
                + "                border-bottom: 0 !important;\n"
                + "                cursor: default !important;\n"
                + "                color: inherit !important;\n"
                + "                text-decoration: none !important;\n"
                + "                font-size: inherit !important;\n"
                + "                font-family: inherit !important;\n"
                + "                font-weight: inherit !important;\n"
                + "                line-height: inherit !important;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Prevents Gmail from displaying a download button on large, non-linked images. */\n"
                + "            .a6S {\n"
                + "                display: none !important;\n"
                + "                opacity: 0.01 !important;\n"
                + "            }\n"
                + "            /* If the above doesn't work, add a .g-img class to any image in question. */\n"
                + "            img.g-img + div {\n"
                + "                display: none !important;\n"
                + "            }\n"
                + "\n"
                + "            /* What it does: Removes right gutter in Gmail iOS app: https://github.com/TedGoas/Cerberus/issues/89  */\n"
                + "            /* Create one of these media queries for each additional viewport size you'd like to fix */\n"
                + "\n"
                + "            /* iPhone 4, 4S, 5, 5S, 5C, and 5SE */\n"
                + "            @media only screen and (min-device-width: 320px) and (max-device-width: 374px) {\n"
                + "                .email-container {\n"
                + "                    min-width: 320px !important;\n"
                + "                }\n"
                + "            }\n"
                + "            /* iPhone 6, 6S, 7, 8, and X */\n"
                + "            @media only screen and (min-device-width: 375px) and (max-device-width: 413px) {\n"
                + "                .email-container {\n"
                + "                    min-width: 375px !important;\n"
                + "                }\n"
                + "            }\n"
                + "            /* iPhone 6+, 7+, and 8+ */\n"
                + "            @media only screen and (min-device-width: 414px) {\n"
                + "                .email-container {\n"
                + "                    min-width: 414px !important;\n"
                + "                }\n"
                + "            }\n"
                + "\n"
                + "        </style>\n"
                + "        <!-- CSS Reset : END -->\n"
                + "        <!-- Reset list spacing because Outlook ignores much of our inline CSS. -->\n"
                + "        <!--[if mso]>\n"
                + "        <style type=\"text/css\">\n"
                + "                ul,\n"
                + "                ol {\n"
                + "                        margin: 0 !important;\n"
                + "                }\n"
                + "                li {\n"
                + "                        margin-left: 30px !important;\n"
                + "                }\n"
                + "                li.list-item-first {\n"
                + "                        margin-top: 0 !important;\n"
                + "                }\n"
                + "                li.list-item-last {\n"
                + "                        margin-bottom: 10px !important;\n"
                + "                }\n"
                + "        </style>\n"
                + "        <![endif]-->\n"
                + "\n"
                + "        <!-- Progressive Enhancements : BEGIN -->\n"
                + "        <style>\n"
                + "\n"
                + "            /* What it does: Hover styles for buttons */\n"
                + "            .button-td,\n"
                + "            .button-a {\n"
                + "                transition: all 100ms ease-in;\n"
                + "            }\n"
                + "            .button-td-primary:hover,\n"
                + "            .button-a-primary:hover {\n"
                + "                background: #555555 !important;\n"
                + "                border-color: #555555 !important;\n"
                + "            }\n"
                + "\n"
                + "            /* Media Queries */\n"
                + "            @media screen and (max-width: 600px) {\n"
                + "\n"
                + "                .email-container {\n"
                + "                    width: 100% !important;\n"
                + "                    margin: auto !important;\n"
                + "                }\n"
                + "\n"
                + "                /* What it does: Forces elements to resize to the full width of their container. Useful for resizing images beyond their max-width. */\n"
                + "                .fluid {\n"
                + "                    max-width: 100% !important;\n"
                + "                    height: auto !important;\n"
                + "                    margin-left: auto !important;\n"
                + "                    margin-right: auto !important;\n"
                + "                }\n"
                + "\n"
                + "                /* What it does: Forces table cells into full-width rows. */\n"
                + "                .stack-column,\n"
                + "                .stack-column-center {\n"
                + "                    display: block !important;\n"
                + "                    width: 100% !important;\n"
                + "                    max-width: 100% !important;\n"
                + "                    direction: ltr !important;\n"
                + "                }\n"
                + "                /* And center justify these ones. */\n"
                + "                .stack-column-center {\n"
                + "                    text-align: center !important;\n"
                + "                }\n"
                + "\n"
                + "                /* What it does: Generic utility class for centering. Useful for images, buttons, and nested tables. */\n"
                + "                .center-on-narrow {\n"
                + "                    text-align: center !important;\n"
                + "                    display: block !important;\n"
                + "                    margin-left: auto !important;\n"
                + "                    margin-right: auto !important;\n"
                + "                    float: none !important;\n"
                + "                }\n"
                + "                table.center-on-narrow {\n"
                + "                    display: inline-block !important;\n"
                + "                }\n"
                + "\n"
                + "                /* What it does: Adjust typography on small screens to improve readability */\n"
                + "                .email-container p {\n"
                + "                    font-size: 17px !important;\n"
                + "                }\n"
                + "            }\n"
                + "\n"
                + "        </style>\n"
                + "        <!-- Progressive Enhancements : END -->\n"
                + "\n"
                + "        <!-- What it does: Makes background images in 72ppi Outlook render at correct size. -->\n"
                + "        <!--[if gte mso 9]>\n"
                + "        <xml>\n"
                + "            <o:OfficeDocumentSettings>\n"
                + "                <o:AllowPNG/>\n"
                + "                <o:PixelsPerInch>96</o:PixelsPerInch>\n"
                + "            </o:OfficeDocumentSettings>\n"
                + "        </xml>\n"
                + "        <![endif]-->\n"
                + "\n"
                + "    </head>\n"
                + "    <!--\n"
                + "            The email background color (#222222) is defined in three places:\n"
                + "            1. body tag: for most email clients\n"
                + "            2. center tag: for Gmail and Inbox mobile apps and web versions of Gmail, GSuite, Inbox, Yahoo, AOL, Libero, Comcast, freenet, Mail.ru, Orange.fr\n"
                + "            3. mso conditional: For Windows 10 Mail\n"
                + "    -->\n"
                + "    <body style=\"width: 100%; margin: 0; mso-line-height-rule: exactly; background-color: #f3f3f3;\">\n"
                + "        <center style=\"width: 100%; background-color: #f3f3f3\">\n"
                + "            <!--[if mso | IE]>\n"
                + "            <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color: #222222;\">\n"
                + "            <tr>\n"
                + "            <td>\n"
                + "            <![endif]-->\n"
                + "\n"
                + "            <!-- Visually Hidden Preheader Text : BEGIN -->\n"
                + "            <div style=\"display: none; font-size: 1px; line-height: 1px; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden; mso-hide: all; font-family: sans-serif;\">\n"
                + "                <!-- (Optional) This text will appear in the inbox preview, but not the email body. It can be used to supplement the email subject line or even summarize the email's contents. Extended text preheaders (~490 characters) seems like a better UX for anyone using a screenreader or voice-command apps like Siri to dictate the contents of an email. If this text is not included, email clients will automatically populate it using the text (including image alt text) at the start of the email's body. -->\n"
                + "            </div>\n"
                + "            <!-- Visually Hidden Preheader Text : END -->\n"
                + "\n"
                + "            <!-- Create white space after the desired preview text so email clients don’t pull other distracting text into the inbox preview. Extend as necessary. -->\n"
                + "            <!-- Preview Text Spacing Hack : BEGIN -->\n"
                + "            <div>\n"
                + "                <br />\n"
                + "            </div>\n"
                + "            <!-- Preview Text Spacing Hack : END -->\n"
                + "\n"
                + "            <!-- Email Body : BEGIN -->\n"
                + "            <table align=\"center\" role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"600\" style=\"margin: 0 auto;\" class=\"email-container\">\n"
                + "                <!-- Email Header : BEGIN -->\n"
                + "                <tr>\n"
                + "                    <td style=\"padding: 20px 0; text-align: center\">\n";
        if (!logo.isEmpty()) {
            tpl += "                        <img src=\"" + logo + "\" width=\"200\" height=\"50\" alt=\"alt_text\" border=\"0\" style=\"height: auto; background: #dddddd; font-family: sans-serif; font-size: 15px; line-height: 15px; color: #555555;\">\n";
        }
        tpl
                += "                    </td>\n"
                + "                </tr>\n"
                + "                <!-- Email Header : END -->\n"
                + "\n"
                + "                <!-- Hero Image, Flush : END -->\n"
                + "\n"
                + "                <!-- 1 Column Text + Button : BEGIN -->\n"
                + "                <tr>\n"
                + "                    <td style=\"background-color: #ffffff;\">\n"
                + "                        <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n"
                + "                            <tr>\n"
                + "                                <td style=\"padding: 20px; font-family: sans-serif; font-size: 15px; line-height: 20px; color: #555555;\">\n";
        tpl += content;
        tpl
                += "                                </td>\n"
                + "                            </tr>\n"
                + "                        </table>\n"
                + "                    </td>\n"
                + "                </tr>\n"
                + "\n"
                + "\n"
                + "                <!-- Clear Spacer : BEGIN -->\n"
                + "                <tr>\n"
                + "                    <td aria-hidden=\"true\" height=\"40\" style=\"font-size: 0px; line-height: 0px;\">\n"
                + "                        &nbsp;\n"
                + "                    </td>\n"
                + "                </tr>\n"
                + "                <!-- Clear Spacer : END -->\n"
                + "\n"
                + "                <!-- 1 Column Text : BEGIN -->\n"
                + "                <tr>\n"
                + "                    <td style=\"background-color: #ffffff;\">\n"
                + "                        <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n"
                + "                            <tr>\n"
                + "                                <td style=\"" + (extra.isEmpty() ? "display: none;" : "") + "padding: 20px; font-family: sans-serif; font-size: 15px; line-height: 20px; color: #555555;\">\n";
        tpl += extra;
        tpl += "                                </td>\n"
                + "                            </tr>\n"
                + "                        </table>\n"
                + "                    </td>\n"
                + "                </tr>\n"
                + "                <!-- 1 Column Text : END -->\n"
                + "\n"
                + "            </table>\n"
                + "            <!-- Email Body : END -->\n"
                + "\n"
                + "            <!-- Email Footer : BEGIN -->\n"
                + "            <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n"
                + "                <tr>\n"
                + "                    <td style=\"padding: 20px; font-family: sans-serif; font-size: 12px; line-height: 15px; text-align: center; color: #888888;\">\n"
                + "                        "
                + "                         <webversion style=\"color: #cccccc; text-decoration: underline; font-weight: bold;\">"
                + "                         " + site
                + "                         </webversion> <br />\n"
                + "                         <strong>" + company_name + "</strong>"
                + "                       <br /><span class=\"unstyle-auto-detected-links\">" + address + "<br />" + phone + "</span>\n"
                + "                            <br /><br />\n"
                + "                            <unsubscribe style=\"color: #888888; text-decoration: underline;\">" + unsubscribe + "</unsubscribe>\n"
                + "                            <br /><br />\n"
                + "                            <a href=\"" + linkRecebimento2 + "\" style=\"color: red; text-decoration: underline;\">Confirmar recebimento</a>\n"
                + "                    </td>\n"
                + "                </tr>\n"
                + "            </table>\n"
                + "            <!-- Email Footer : END -->\n";
        tpl += " <br /> <p><i>" + " - [ID:" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "]" + "<i></p>";
        tpl += "\n"
                + "            <!--[if mso | IE]>\n"
                + "            </td>\n"
                + "            </tr>\n"
                + "            </table>\n"
                + "            <![endif]-->\n"
                + "        </center>\n"
                + "    </body>\n"
                + "</html>\n"
                + "";
        return tpl;
    }
    
    public static String stringToMD5(String string) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes(Charset.forName("UTF-8")), 0, string.length());
            return new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Mail.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public EmailLote getEmailLote() {
        return emailLote;
    }
    
    public void setEmailLote(EmailLote emailLote) {
        this.emailLote = emailLote;
    }
}
