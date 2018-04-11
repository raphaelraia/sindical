package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.Email;
import br.com.rtools.sistema.EmailPessoa;
import br.com.rtools.sistema.SisConfiguracaoEmail;
import br.com.rtools.sistema.SisNotificacao;
import br.com.rtools.sistema.SisNotificacaoCategoria;
import br.com.rtools.sistema.SisNotificacaoCliente;
import br.com.rtools.sistema.dao.ConfiguracaoDao;
import br.com.rtools.sistema.dao.SisConfiguracaoEmailDao;
import br.com.rtools.sistema.dao.SisNotificacaoClienteDao;
import br.com.rtools.sistema.dao.SisNotificacaoDao;
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
public class SisNotificacaoBean implements Serializable {

    private SisNotificacao sisNotificacao;
    private List<SelectItem> listSisNotificacaoCategoria;
    private List<SelectItem> listClientes;
    private Integer idSisNotificacaoCategoria;
    private Integer idClientes;
    private List<SisNotificacao> listSisNotificacao;
    private List<SisNotificacaoCliente> listSisNotificacaoCliente;

    @PostConstruct
    public void init() {
        sisNotificacao = new SisNotificacao();
        listSisNotificacaoCategoria = new ArrayList<>();
        listClientes = new ArrayList<>();
        listSisNotificacao = new ArrayList<>();
        listSisNotificacaoCliente = new ArrayList<>();
        loadClientes();
        loadSisNotificacaoCategoria();
        loadSisNotificacaoCliente();
    }

    public void loadClientes() {
        if (sisNotificacao.getId() != null) {
            ConfiguracaoDao configuracaoDao = new ConfiguracaoDao();
            List<Configuracao> list = configuracaoDao.findNotInByTabela("sis_notificacao_cliente", "id_notificacao", "" + sisNotificacao.getId());
            idClientes = 0;
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idClientes = list.get(i).getId();
                }
                listClientes.add(new SelectItem(list.get(i).getId(), list.get(i).getIdentifica()));
            }
        }
    }

    public void loadSisNotificacaoCategoria() {
        List<SisNotificacaoCategoria> list = new Dao().list(new SisNotificacaoCategoria());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idSisNotificacaoCategoria = list.get(i).getId();
            }
            listSisNotificacaoCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadSisNotificacaoCliente() {
        if (sisNotificacao.getId() != null) {
            listSisNotificacaoCliente = new SisNotificacaoClienteDao().findBy(sisNotificacao.getId());
        }
    }

    public String edit(SisNotificacao sn) {
        idSisNotificacaoCategoria = sn.getSisNotificacaoCategoria().getId();
        listSisNotificacaoCliente.clear();
        loadSisNotificacaoCliente();
        listClientes.clear();
        loadClientes();
        sisNotificacao = sn;
        ChamadaPaginaBean.link();
        loadSisNotificacaoCliente();
        return "sisNotificacao";
    }

    public void loadSisConfiguracao() {
    }

    public void clear() {
        GenericaSessao.remove("sisNotificacaoBean");
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void save() {
        Dao dao = new Dao();
        if(listSisNotificacaoCategoria.isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar categorias!");
            return;
        }
        if (sisNotificacao.getDtInicial() == null) {
            GenericaMensagem.warn("Validação", "Informar data de expiração!");
            return;
        }
        if (sisNotificacao.getId() == null) {
            sisNotificacao.setAgendador((Usuario) dao.find((Usuario) GenericaSessao.getObject("sessaoUsuario")));
        }
        if (sisNotificacao.getObservacao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar uma observação!");
            return;
        }
        sisNotificacao.setSisNotificacaoCategoria((SisNotificacaoCategoria) dao.find(new SisNotificacaoCategoria(), idSisNotificacaoCategoria));
        dao.openTransaction();
        if (sisNotificacao.getId() == null) {
            sisNotificacao.setDtCadastro(new Date());
            if (dao.save(sisNotificacao)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Registro inserido!");
                listSisNotificacao.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
                dao.rollback();
            }
        } else {
            if (dao.update(sisNotificacao)) {
                dao.commit();
                GenericaMensagem.info("Sucesso", "Registro atualizado!");
                listSisNotificacao.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
                dao.rollback();
            }
        }
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        for (int i = 0; i < listSisNotificacaoCliente.size(); i++) {
            if (!dao.delete(listSisNotificacaoCliente.get(i))) {
                GenericaMensagem.warn("Erro", "Ao remover registro!");
                dao.rollback();
                return;
            }
        }
        if (dao.delete(sisNotificacao)) {
            GenericaMensagem.info("Sucesso", "Registro removido!");
            dao.commit();
            listSisNotificacaoCliente.clear();
            listSisNotificacao.clear();
            sisNotificacao = new SisNotificacao();
            listClientes.clear();
        } else {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void add() {
        SisNotificacaoCliente snc = new SisNotificacaoCliente();
        snc.setSisNotificacao(sisNotificacao);
        snc.setConfiguracao((Configuracao) new Dao().find(new Configuracao(), idClientes));
        if (new Dao().save(snc, true)) {
            listClientes.clear();
            listSisNotificacaoCliente.clear();
            loadSisNotificacaoCliente();
            GenericaMensagem.info("Sucesso", "Registro inserido!");
        } else {
            GenericaMensagem.warn("Erro", "Registro já existe!");
        }
    }

    public void addAll() {
        for (int i = 0; i < listClientes.size(); i++) {
            SisNotificacaoCliente snc = new SisNotificacaoCliente();
            snc.setSisNotificacao(sisNotificacao);
            snc.setConfiguracao((Configuracao) new Dao().find(new Configuracao(), (Integer) listClientes.get(i).getValue()));
            if (new Dao().save(snc, true)) {
            } else {
                listClientes.clear();
                listSisNotificacaoCliente.clear();
                loadSisNotificacaoCliente();
                GenericaMensagem.warn("Erro", "Registro já existe!");
                return;
            }
        }
        GenericaMensagem.info("Sucesso", "Registro inserido!");
        listSisNotificacaoCliente.clear();
        loadSisNotificacaoCliente();
        listClientes.clear();
    }

    public void delete(SisNotificacaoCliente snc) {
        if (new Dao().delete(snc, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido!");
            listClientes.clear();
            listSisNotificacaoCliente.clear();
            loadSisNotificacaoCliente();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public SisNotificacao getSisNotificacao() {
        return sisNotificacao;
    }

    public void setSisNotificacao(SisNotificacao sisNotificacao) {
        this.sisNotificacao = sisNotificacao;
    }

    public List<SelectItem> getListSisNotificacaoCategoria() {
        return listSisNotificacaoCategoria;
    }

    public void setListSisNotificacaoCategoria(List<SelectItem> listSisNotificacaoCategoria) {
        this.listSisNotificacaoCategoria = listSisNotificacaoCategoria;
    }

    public List<SelectItem> getListClientes() {
        if (listClientes.isEmpty()) {
            loadClientes();
        }
        return listClientes;
    }

    public void setListClientes(List<SelectItem> listClientes) {
        this.listClientes = listClientes;
    }

    public Integer getIdSisNotificacaoCategoria() {
        return idSisNotificacaoCategoria;
    }

    public void setIdSisNotificacaoCategoria(Integer idSisNotificacaoCategoria) {
        this.idSisNotificacaoCategoria = idSisNotificacaoCategoria;
    }

    public Integer getIdClientes() {
        return idClientes;
    }

    public void setIdClientes(Integer idClientes) {
        this.idClientes = idClientes;
    }

    public List<SisNotificacao> getListSisNotificacao() {
        if (listSisNotificacao.isEmpty()) {
            listSisNotificacao = new SisNotificacaoDao().findAll();
        }
        return listSisNotificacao;
    }

    public void setListSisNotificacao(List<SisNotificacao> listSisNotificacao) {
        this.listSisNotificacao = listSisNotificacao;
    }

    public List<SisNotificacaoCliente> getListSisNotificacaoCliente() {
        return listSisNotificacaoCliente;
    }

    public void setListSisNotificacaoCliente(List<SisNotificacaoCliente> listSisNotificacaoCliente) {
        this.listSisNotificacaoCliente = listSisNotificacaoCliente;
    }

    public void send() {
        String mensagem
                = sisNotificacao.getTitulo() + " <br />"
                + sisNotificacao.getObservacao() + " <br /><br />"
                + " Data Inicial: " + sisNotificacao.getInicialString() + " <br />"
                + " Hora Inicial: " + sisNotificacao.getHoraInicial() + " <br />"
                + " Data Final: " + sisNotificacao.getFinalString() + " <br />"
                + " Hora Final: " + sisNotificacao.getHoraFinal() + " <br />";
        Mail mail = new Mail();
        mail.setEmail(
                new Email(
                        -1,
                        DataHoje.dataHoje(),
                        DataHoje.livre(new Date(), "HH:mm"),
                        (Usuario) GenericaSessao.getObject("sessaoUsuario"),
                        new Rotina().get(),
                        null,
                        sisNotificacao.getSisNotificacaoCategoria().getDescricao(),
                        mensagem,
                        false,
                        false
                )
        );
        List<EmailPessoa> emailPessoas = new ArrayList<>();
        for (int x = 0; x < listSisNotificacaoCliente.size(); x++) {
            List<SisConfiguracaoEmail> sces = new SisConfiguracaoEmailDao().findByConfiguracao(listSisNotificacaoCliente.get(x).getId());
            for (int y = 0; y < sces.size(); y++) {
                EmailPessoa emailPessoa = new EmailPessoa();
                emailPessoa.setDestinatario(sces.get(y).getEmail());
                emailPessoa.setPessoa(null);
                emailPessoa.setRecebimento(null);
                emailPessoas.add(emailPessoa);
            }
        }
        mail.setEmailPessoas(emailPessoas);
        String[] string = mail.send();
        if (string[0].isEmpty()) {
            GenericaMensagem.warn("Validação", "Erro ao enviar mensagem!" + string[0]);
        } else {
            GenericaMensagem.info("Sucesso", "Email enviado com sucesso!");
        }
    }

}
