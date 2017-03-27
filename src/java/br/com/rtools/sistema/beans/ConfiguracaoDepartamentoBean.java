package br.com.rtools.sistema.beans;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.SisEmailProtocolo;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.dao.DepartamentoDao;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.ConfiguracaoDepartamento;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mail;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class ConfiguracaoDepartamentoBean implements Serializable {

    private ConfiguracaoDepartamento configuracaoDepartamento;
    private List<ConfiguracaoDepartamento> listConfiguracaoDepartamento;
    private List<SelectItem> listFiliais;
    private List<SelectItem> listSisEmailProtocolo;
    private List<SelectItem> listDepartamentos;
    private Integer idFilial;
    private Integer idDepartamento;
    private Integer idSisEmailProtocolo;

    @PostConstruct
    public void init() {
        configuracaoDepartamento = new ConfiguracaoDepartamento();
        listFiliais = new ArrayList<>();
        listConfiguracaoDepartamento = new ArrayList<>();
        listDepartamentos = new ArrayList<>();
        listSisEmailProtocolo = new ArrayList<>();
        idFilial = null;
        idDepartamento = null;
        idSisEmailProtocolo = null;
        Registro r = Registro.get();
        if(r.isSisEmailMarketing()) {
           configuracaoDepartamento.setServidorSmtp(true);
        }
        loadListFiliais();
        loadListSisEmailProtocolo();
        loadListDepartamentos();
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("configuracaoDepartamentoBean");
        GenericaSessao.remove("registroEmpresarialBean");
    }

    public void loadListFiliais() {
        listFiliais = new ArrayList<>();
        List<Filial> list = (List<Filial>) new Dao().list(new Filial(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            listFiliais.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public List<SelectItem> getListFiliais() {
        return listFiliais;
    }

    public void setListFiliais(List<SelectItem> listFiliais) {
        this.listFiliais = listFiliais;
    }

    public void loadListDepartamentos() {
        listDepartamentos = new ArrayList<>();
        List<Departamento> list = new DepartamentoDao().findNotInByTabela("conf_departamento", "id_filial", "" + idFilial);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idDepartamento = list.get(i).getId();
            }
            listDepartamentos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public List<SelectItem> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<SelectItem> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public ConfiguracaoDepartamento getConfiguracaoDepartamento() {
        return configuracaoDepartamento;
    }

    public void setConfiguracaoDepartamento(ConfiguracaoDepartamento configuracaoDepartamento) {
        this.configuracaoDepartamento = configuracaoDepartamento;
    }

    public List<ConfiguracaoDepartamento> getListConfiguracaoDepartamento() {
        if (listConfiguracaoDepartamento.isEmpty()) {
            listConfiguracaoDepartamento = new Dao().list(new ConfiguracaoDepartamento(), true);
        }
        return listConfiguracaoDepartamento;
    }

    public void setListConfiguracaoDepartamento(List<ConfiguracaoDepartamento> listConfiguracaoDepartamento) {
        this.listConfiguracaoDepartamento = listConfiguracaoDepartamento;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public void loadListSisEmailProtocolo() {
        listSisEmailProtocolo = new ArrayList<>();
        List<SisEmailProtocolo> list = (List<SisEmailProtocolo>) new Dao().list(new SisEmailProtocolo());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSisEmailProtocolo = list.get(i).getId();
            }
            listSisEmailProtocolo.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public List<SelectItem> getListSisEmailProtocolo() {
        return listSisEmailProtocolo;
    }

    public void setListSisEmailProtocolo(List<SelectItem> listSisEmailProtocolo) {
        this.listSisEmailProtocolo = listSisEmailProtocolo;
    }

    public Integer getIdSisEmailProtocolo() {
        return idSisEmailProtocolo;
    }

    public void setIdSisEmailProtocolo(Integer idSisEmailProtocolo) {
        this.idSisEmailProtocolo = idSisEmailProtocolo;
    }

    public void save() {
        Dao dao = new Dao();
        if (configuracaoDepartamento.getEmail().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar email!");
            return;
        }
        if (configuracaoDepartamento.getSenha().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar senha!");
            return;
        }
        if (configuracaoDepartamento.getSmtp().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar servidor SMTP!");
            return;
        }
        configuracaoDepartamento.setDepartamento((Departamento) dao.find(new Departamento(), idDepartamento));
        configuracaoDepartamento.setFilial((Filial) dao.find(new Filial(), idFilial));
        configuracaoDepartamento.setSisEmailProtocolo((SisEmailProtocolo) dao.find(new SisEmailProtocolo(), idSisEmailProtocolo));
        if (!configuracaoDepartamento.getSenha().isEmpty() && !configuracaoDepartamento.getSenhaConfirma().isEmpty()) {
            if (!configuracaoDepartamento.getSenha().equals(configuracaoDepartamento.getSenhaConfirma())) {
                GenericaMensagem.warn("Validação", "Senhas não correspondem");
                return;
            }
        }
        if (configuracaoDepartamento.getId() == null) {
            if (dao.save(configuracaoDepartamento, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
            } else {
                GenericaMensagem.warn("Erro", "Email já existe!");
            }
        } else if (dao.update(configuracaoDepartamento, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
        loadListDepartamentos();
        listConfiguracaoDepartamento.clear();

    }

    public void remove(ConfiguracaoDepartamento ed) {
        if (new Dao().delete(ed, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Email já existe!");
        }
        listConfiguracaoDepartamento.clear();
    }

    public void edit(ConfiguracaoDepartamento ed) {
        listDepartamentos = new ArrayList<>();
        List<Departamento> list = new Dao().list(new Departamento(), true);
        for (int i = 0; i < list.size(); i++) {
            listDepartamentos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }        
        configuracaoDepartamento = ed;
        idFilial = ed.getFilial().getId();
        idDepartamento = ed.getDepartamento().getId();
        idSisEmailProtocolo = ed.getSisEmailProtocolo().getId();
        if (configuracaoDepartamento.getId() != null) {
            configuracaoDepartamento.setSenhaConfirma(configuracaoDepartamento.getSenha());
        }
    }

    public void send(ConfiguracaoDepartamento ed) {
        if (ed.getEmailTest().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar e-mail!");
            return;
        }
        Dao dao = new Dao();
        Mail mail = new Mail();
        mail.setConfiguracaoDepartamento(ed);
        mail.setEmail(
                new Email(
                        -1,
                        DataHoje.dataHoje(),
                        DataHoje.livre(new Date(), "HH:mm"),
                        (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                        (Rotina) dao.find(new Rotina(), 111),
                        null,
                        "Email teste.",
                        "",
                        false,
                        false
                )
        );
        List<EmailPessoa> emailPessoas = new ArrayList<>();
        EmailPessoa emailPessoa = new EmailPessoa();
        emailPessoa.setDestinatario(ed.getEmailTest());
        emailPessoa.setPessoa(null);
        emailPessoa.setRecebimento(null);
        emailPessoas.add(emailPessoa);
        mail.setEmailPessoas(emailPessoas);
        String[] string = mail.send();
        if (string[0].isEmpty()) {
            GenericaMensagem.warn("Validação", "Erro ao enviar mensagem!" + string[1]);
        } else {
            GenericaMensagem.info("Sucesso", "Email enviado com sucesso!");
        }
    }

}
