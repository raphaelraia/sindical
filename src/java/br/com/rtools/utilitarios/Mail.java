package br.com.rtools.utilitarios;

import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailArquivo;
import br.com.rtools.sistema.ConfiguracaoDepartamento;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.EmailPrioridade;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    private ConfiguracaoDepartamento configuracaoDepartamento;

    public Mail() {
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
                if (configuracaoDepartamento != null) {
                    if (!configuracaoDepartamento.getServidorSmtp()) {
                        xEmail = configuracaoDepartamento.getEmail();
                    }
                    if (!registro.isSisEmailMarketing()) {
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
                }
                boolean saveArquivosEmail = false;
                for (int i = 0; i < emailPessoas.size(); i++) {
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
                                        + "         " + xAssinatura
                                        + "     </body>"
                                        + "</html>";
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
                        msg.setSubject(email.getAssunto());
                        msg.setContent(multipart);
                        msg.setSentDate(new Date());
                        String id = UUID.randomUUID().toString();
                        //msg.setHeader("X-Mailer", "Tov Are's program");
                        msg.setHeader("Content-ID", "<" + id + ">");
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
                        boolean updateEmail = false;
                        if (emailPessoas.get(i).getPessoa() == null || emailPessoas.get(i).getPessoa().getId() == -1) {
                            emailPessoas.get(i).setPessoa(null);
                        }
                        if (email.getId() == -1) {
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
                            if (emailPessoas.get(i).getId() == -1) {
                                di.save(emailPessoas.get(i), true);
                            } else {
                                di.update(emailPessoas.get(i), true);
                            }
                        }
                        strings[0] = "Enviado com Sucesso.";
                    } catch (AddressException e) {
                        strings[1] = "Email de destinatário inválido!";
                        strings[1] += " - " + returnExceptionText(e.getMessage());
                    } catch (MessagingException e) {
                        strings[1] = "" + returnExceptionText(e.getMessage());
                    } catch (UnsupportedEncodingException ex) {
                        strings[1] = "Erro ";
                        strings[1] += " - " + returnExceptionText(ex.getMessage());
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
}
