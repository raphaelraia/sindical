package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.AcordoComissaoOperador;
import br.com.rtools.arrecadacao.dao.AcordoComissaoOperadorDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Sessions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class OperadorAcordoBean implements Serializable {

    private AcordoComissaoOperador acordoComissaoOperador;
    private List<AcordoComissaoOperador> listAcordoComissaoOperador;
    private List<SelectItem> listRotinas;
    private Integer idRotina;

    @PostConstruct
    public void init() {
        acordoComissaoOperador = new AcordoComissaoOperador();
        idRotina = null;
        listAcordoComissaoOperador = new ArrayList<>();
        listRotinas = new ArrayList<>();
        loadListACO();
        loadListRotinas();
    }

    @PreDestroy
    public void destroy() {
        clear();
        GenericaSessao.remove("usuarioPesquisa");
    }

    public void clear() {
        GenericaSessao.remove("professorBean");
    }

    public void save() {
        AcordoComissaoOperadorDao acod = new AcordoComissaoOperadorDao();
        if (idRotina == null) {
            Messages.warn("Validação", "Selecionar rotina!");
            return;
        }
        if (acordoComissaoOperador.getUsuario() == null) {
            Messages.warn("Validação", "Pesquisar usuário!");
            return;
        }
        if (acordoComissaoOperador.getComissao()) {
            if (acordoComissaoOperador.getNrComissao() == 0) {
                Messages.warn("Validação", "Informar valor da comissão!");
                return;
            }
        }
        AcordoComissaoOperador aco = acod.find(acordoComissaoOperador.getUsuario().getId(), idRotina);
        if (aco != null) {
            if (aco.equals(acordoComissaoOperador)) {
                Messages.warn("Validação", "Usuário já cadastrado para esta rotina!");
                return;
            }
        }
        Dao dao = new Dao();
        acordoComissaoOperador.setRotina((Rotina) dao.find(new Rotina(), idRotina));
        NovoLog novoLog = new NovoLog();
        if (acordoComissaoOperador.getId() == null) {
            if (!dao.save(acordoComissaoOperador, true)) {
                Messages.warn("Erro", "Ao inserir registro!");
                return;
            } else {
                Messages.info("Sucesso", "Registro inserido!");
            }
            novoLog.save(
                    "ID " + acordoComissaoOperador.getId()
                    + " - Pessoa: (" + acordoComissaoOperador.getUsuario().getPessoa().getId() + ") " + acordoComissaoOperador.getUsuario().getPessoa().getNome()
                    + " - Comissão: " + acordoComissaoOperador.getNrComissao()
            );
        } else {
            aco = (AcordoComissaoOperador) dao.find(acordoComissaoOperador);
            String beforeUpdate
                    = "ID " + aco.getId()
                    + " - Pessoa: (" + aco.getUsuario().getPessoa().getId() + ") " + aco.getUsuario().getPessoa().getNome()
                    + " - Comissão: " + aco.getNrComissao();
            if (!dao.update(acordoComissaoOperador, true)) {
                dao.rollback();
                Messages.warn("Erro", "Ao atualizar registro!");
                return;
            } else {
                Messages.info("Sucesso", "Registro atualizado!");
            }
            novoLog.update(beforeUpdate,
                    "ID " + acordoComissaoOperador.getId()
                    + " - Pessoa: (" + acordoComissaoOperador.getUsuario().getPessoa().getId() + ") " + acordoComissaoOperador.getUsuario().getPessoa().getNome()
                    + " - Comissão: " + acordoComissaoOperador.getNrComissao()
            );
        }
        loadListACO();
    }

    public void edit(AcordoComissaoOperador c) {
        Dao dao = new Dao();
        acordoComissaoOperador = (AcordoComissaoOperador) dao.rebind(c);
        loadListRotinas();
        loadListACO();
        idRotina = acordoComissaoOperador.getRotina().getId();
    }

    public void delete() {
        if (acordoComissaoOperador.getId() != null) {
            Dao dao = new Dao();
            NovoLog novoLog = new NovoLog();
            if (dao.delete(acordoComissaoOperador, true)) {
                novoLog.delete(
                        "ID " + acordoComissaoOperador.getId()
                        + " - Pessoa: (" + acordoComissaoOperador.getUsuario().getPessoa().getId() + ") " + acordoComissaoOperador.getUsuario().getPessoa().getNome()
                        + " - Comissão: " + acordoComissaoOperador.getNrComissao()
                );
                loadListACO();
                Messages.info("Sucesso", "Registro removido!");
            } else {
                Messages.warn("Erro", "Ao remover registro!");
            }
        }
    }

    public void delete(AcordoComissaoOperador aco) {
        if (aco.getId() != null) {
            Dao dao = new Dao();
            NovoLog novoLog = new NovoLog();
            if (dao.delete(aco, true)) {
                novoLog.delete(
                        "ID " + aco.getId()
                        + " - Pessoa: (" + aco.getUsuario().getPessoa().getId() + ") " + aco.getUsuario().getPessoa().getNome()
                        + " - Comissão: " + aco.getNrComissao()
                );
                loadListACO();;
                acordoComissaoOperador = new AcordoComissaoOperador();
                Messages.info("Sucesso", "Registro removido!");
            } else {
                Messages.warn("Erro", "Ao remover registro!");
            }
        }
    }

    public AcordoComissaoOperador getAcordoComissaoOperador() {
        if (Sessions.exists("usuarioPesquisa")) {
            acordoComissaoOperador.setUsuario((Usuario) Sessions.getObject("usuarioPesquisa", true));
            loadListRotinas();
            loadListACO();
        }
        return acordoComissaoOperador;
    }

    public void setAcordoComissaoOperador(AcordoComissaoOperador acordoComissaoOperador) {
        this.acordoComissaoOperador = acordoComissaoOperador;
    }

    public List<AcordoComissaoOperador> getListAcordoComissaoOperador() {
        return listAcordoComissaoOperador;
    }

    public void listener(String tcase) {
        switch (tcase) {
            case "remove_user":
                acordoComissaoOperador.setUsuario(null);
                break;
        }
    }

    public void setListAcordoComissaoOperador(List<AcordoComissaoOperador> listAcordoComissaoOperador) {
        this.listAcordoComissaoOperador = listAcordoComissaoOperador;
    }

    public List<SelectItem> getListRotinas() {
        return listRotinas;
    }

    public void setListRotinas(List<SelectItem> listRotinas) {
        this.listRotinas = listRotinas;
    }

    public Integer getIdRotina() {
        return idRotina;
    }

    public void setIdRotina(Integer idRotina) {
        this.idRotina = idRotina;
    }

    public void loadListACO() {
        if (acordoComissaoOperador.getUsuario() == null) {
            listAcordoComissaoOperador = new AcordoComissaoOperadorDao().findAll();
        } else {
            listAcordoComissaoOperador = new AcordoComissaoOperadorDao().findByUsuario(acordoComissaoOperador.getUsuario().getId());
        }
    }

    public void loadListRotinas() {
        listRotinas = new ArrayList();
        List<Rotina> list = new Dao().list(new Rotina(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRotina = list.get(i).getId();
            }
            listRotinas.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
        }
    }
}
