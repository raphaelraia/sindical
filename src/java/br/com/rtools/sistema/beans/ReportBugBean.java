package br.com.rtools.sistema.beans;

import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Modulo;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mail;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ReportBugBean {

    private String bug = "";

    public void report() {
        if (bug.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar o Bug");
            return;
        }
        if (bug.length() < 10) {
            GenericaMensagem.warn("Validação", "Deve conter no mínimo 10 caracteres");
            return;
        }
        Mail mail = new Mail();
        Usuario u = Usuario.getUsuario();
        String bugid = GenericaSessao.getString("sessaoCliente").toUpperCase() + DataHoje.data().replace("/", "") + DataHoje.hora().replace(":", "");
        bug = "<strong>BUG INFORMADO</strong><br /><br /> " + bug + "<br /><br />";
        bug += "<strong><u>Detalhes do contato</u></strong><br /><br />";
        bug += "<strong>ID: </strong> " + bugid + " <br />";
        bug += "<strong>Sistema:</strong> Sindical <br />";
        bug += "<strong>Cliente:</strong> " + GenericaSessao.getString("sessaoCliente") + " <br />";
        bug += "<strong>Usuário Login:</strong> " + u.getLogin() + " <br />";
        bug += "<strong>Nome:</strong> " + u.getPessoa().getNome() + " <br />";
        bug += "<strong>Filial:</strong> " + (MacFilial.getAcessoFilial().getFilial().getFilial().getPessoa().getNome().isEmpty() ? "Usuário sem filial especificada" : MacFilial.getAcessoFilial().getFilial().getFilial().getPessoa().getNome()) + " <br />";
        bug += "<strong>Departamento:</strong> " + (MacFilial.getAcessoFilial().getDepartamento().getDescricao().isEmpty() ? "Usuário sem departamento especificado" : MacFilial.getAcessoFilial().getDepartamento().getDescricao()) + " <br />";
        bug += "<strong>Data:</strong> " + DataHoje.data() + " <br />";
        bug += "<strong>Horário:</strong> " + DataHoje.horaMinuto() + " <br />";
        if (GenericaSessao.exists("idModulo")) {
            Modulo modulo = (Modulo) new Dao().find(new Modulo(), GenericaSessao.getInteger("idModulo"));
            if (modulo != null) {
                bug += "<strong>Módulo:</strong> " + modulo.getDescricao() + " <br />";
            }
        } else {
            bug += "<strong>Módulo: Principal</strong> <br />";
        }
        bug += "<strong>Rotina: </strong> (" + new Rotina().get().getId() + ") " + new Rotina().get().getRotina() + "  <br />";
        if (!u.getEmail().isEmpty()) {
            bug += "<strong>Email do usuário:</strong> " + u.getEmail() + " <br />";
        }
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
        emailPessoa.setDestinatario("suporte@rtools.com.br");
        emailPessoa.setPessoa(null);
        emailPessoa.setRecebimento(null);
        emailPessoas.add(emailPessoa);
        mail.setEmailPessoas(emailPessoas);
        String[] string = mail.send();
        if (string[0].isEmpty()) {
            GenericaMensagem.warn("Validação", "Erro ao enviar mensagem!" + string[0]);
        } else {
            GenericaMensagem.info("Sucesso", "Bug reportado com sucesso!");
        }
        NovoLog novoLog = new NovoLog();
        novoLog.live(bug);
        bug = "";
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

}
