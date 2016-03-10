package br.com.rtools.sistema.beans;

import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Modulo;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Defaults;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mail;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONObject;

@ManagedBean
@ViewScoped
public class ReportBugBean implements Serializable {

    private String bug = "";

    private File fileBug = null;

    public void report() {
        if (bug.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar o Bug");
            return;
        }
        if (bug.length() < 10) {
            GenericaMensagem.warn("Validação", "Deve conter no mínimo 10 caracteres");
            return;
        }
        String bug_message = bug;
        Mail mail = new Mail();
        Usuario u = Usuario.getUsuario();
        String cliente = GenericaSessao.getString("sessaoCliente");
        MacFilial macFilial = MacFilial.getAcessoFilial();
        String bugid = GenericaSessao.getString("sessaoCliente").toUpperCase() + DataHoje.data().replace("/", "") + DataHoje.hora().replace(":", "");
        Rotina rotina = new Rotina().get();
        bug = "<strong>BUG INFORMADO</strong><br /><br /> " + bug + "<br /><br />";
        bug += "<strong><u>Detalhes do contato</u></strong><br /><br />";
        bug += "<strong>ID: </strong> " + bugid + " <br />";
        bug += "<strong>Sistema:</strong> Sindical <br />";
        bug += "<strong>Cliente:</strong> " + cliente + " <br />";
        bug += "<strong>Usuário Login:</strong> " + u.getLogin() + " <br />";
        bug += "<strong>Nome:</strong> " + u.getPessoa().getNome() + " <br />";
        bug += "<strong>Filial:</strong> " + (macFilial.getFilial().getFilial().getPessoa().getNome().isEmpty() ? "Usuário sem filial especificada" : macFilial.getFilial().getFilial().getPessoa().getNome()) + " <br />";
        bug += "<strong>Departamento:</strong> " + (macFilial.getDepartamento().getDescricao().isEmpty() ? "Usuário sem departamento especificado" : macFilial.getDepartamento().getDescricao()) + " <br />";
        bug += "<strong>Data:</strong> " + DataHoje.data() + " <br />";
        bug += "<strong>Horário:</strong> " + DataHoje.horaMinuto() + " <br />";
        Modulo modulo = new Modulo();
        if (GenericaSessao.exists("idModulo")) {
            modulo = (Modulo) new Dao().find(new Modulo(), GenericaSessao.getInteger("idModulo"));
            if (modulo != null) {
                bug += "<strong>Módulo:</strong> " + modulo.getDescricao() + " <br />";
            } else {
                modulo = new Modulo();
            }
        } else {
            bug += "<strong>Módulo: Principal</strong> <br />";
        }
        bug += "<strong>Rotina: </strong> (" + rotina.getId() + ") " + rotina.getRotina() + "  <br />";
        if (!u.getEmail().isEmpty()) {
            bug += "<strong>Email do usuário:</strong> " + u.getEmail() + " <br />";
        }
        Defaults defaults = new Defaults();
        defaults.loadJson();
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("tipo", "bug");
        jSONObject.put("bugid", bugid);
        jSONObject.put("sistema", "Sindical");
        jSONObject.put("cliente", u.getId());
        jSONObject.put("usuario_id", u.getId());
        jSONObject.put("usuario_login", u.getLogin());
        jSONObject.put("usuario_nome", u.getPessoa().getNome());
        jSONObject.put("usuario_email", u.getEmail());
        jSONObject.put("mac_id", macFilial.getId());
        jSONObject.put("mac", macFilial.getMac());
        jSONObject.put("filial_id", macFilial.getFilial().getId());
        jSONObject.put("filial_nome", macFilial.getFilial().getFilial().getPessoa().getNome());
        jSONObject.put("departamento_id", macFilial.getDepartamento().getId());
        jSONObject.put("departamento", macFilial.getDepartamento().getDescricao());
        jSONObject.put("modulo_id", modulo.getId());
        jSONObject.put("modulo", modulo.getDescricao());
        jSONObject.put("rotina_id", rotina.getId());
        jSONObject.put("rotina", rotina.getRotina());
        jSONObject.put("bug_message", bug_message);
        jSONObject.put("data_hora_envio", new Date());
        String jsonStr = jSONObject.toString().replace("\"", "'");
        bug += "<a href=\"" + defaults.getUrl_sistem_master() + "Rtools/?chamado=" + jsonStr
                + "\">Abrir chamado</a>";
        mail.setEmail(
                new Email(
                        -1,
                        DataHoje.dataHoje(),
                        DataHoje.livre(new Date(), "HH:mm"),
                        (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                        new Rotina().get(),
                        null,
                        "Bug (Sindical) (" + bugid + ") - Rotina: " + new Rotina().get().getRotina(),
                        bug,
                        false,
                        false
                )
        );
        List<EmailPessoa> emailPessoas = new ArrayList<>();
        EmailPessoa emailPessoa = new EmailPessoa();
        emailPessoa.setDestinatario(defaults.getEmail_suport());
        emailPessoa.setPessoa(null);
        emailPessoa.setRecebimento(null);
        emailPessoas.add(emailPessoa);
        mail.setEmailPessoas(emailPessoas);
        List<File> listFiles = new ArrayList<>();
        if(fileBug != null) {
            if(fileBug.exists()) {
                listFiles.add(fileBug);            
                mail.setFiles(listFiles);                
            }
        }
        String[] string = mail.send();
        if (string[0].isEmpty()) {
            GenericaMensagem.warn("Validação", "Erro ao enviar mensagem!" + string[0]);
        } else {
            NovoLog novoLog = new NovoLog();
            novoLog.live(bug);
            GenericaMensagem.info("Sucesso", "Bug reportado com sucesso!");
        }
        bug = "";
        fileBug.delete();
        fileBug = null;
    }

    public String getBug() {
        return bug;
    }

    public void setBug(String bug) {
        this.bug = bug;
    }

    public Rotina getRotina() {
        return new Rotina().get();
    }

    public void upload(FileUploadEvent event) {
        ConfiguracaoUpload cu = new ConfiguracaoUpload();
        cu.setArquivo(event.getFile().getFileName());
        fileBug = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + GenericaSessao.getString("sessaoCliente") + "/" + cu.getDiretorio() + "/" + event.getFile().getFileName()));
        if (fileBug.exists()) {
            fileBug.delete();
        }
        cu.setDiretorio("Arquivos/bugs");
        cu.setSubstituir(true);
        cu.setEvent(event);
        if (Upload.enviar(cu, true)) {
            fileBug = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + GenericaSessao.getString("sessaoCliente") + "/" + cu.getDiretorio() + "/" + event.getFile().getFileName()));
        }
    }

    public File getFileBug() {
        return fileBug;
    }

    public void setFileBug(File fileBug) {
        this.fileBug = fileBug;
    }

}
