package br.com.rtools.seguranca.beans;

import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.PermissaoCatraca;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.dao.DepartamentoDao;
import br.com.rtools.seguranca.dao.PermissaoCatracaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class PermissaoCatracaBean {

    private PermissaoCatraca permissaoCatraca;
    private List<PermissaoCatraca> listPermissaoCatraca;
    private List<SelectItem> listDepartamentos;
    private String departamento_id;
    private Pessoa pessoa;

    @PostConstruct
    public void init() {
        permissaoCatraca = new PermissaoCatraca();
        listDepartamentos = new ArrayList();
        listPermissaoCatraca = new ArrayList();
        departamento_id = null;
        pessoa = new Pessoa();
        loadDepartamento();
        loadPermissaoCatraca();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("permissaoCatracaBean");
    }

    public void clear() {
        GenericaSessao.remove("permissaoCatracaBean");
    }

    public void clear(Integer tcase) {
        switch (tcase) {
            case 1:
                pessoa = new Pessoa();
                break;
        }
    }

    public void loadDepartamento() {
        listDepartamentos.clear();
        List<Departamento> list = new DepartamentoDao().findNotInByTabela("seg_permissao_catraca", "id_pessoa", "" + pessoa.getId());
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                departamento_id = "" + list.get(i).getId();
            }
            listDepartamentos.add(new SelectItem("" + list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadPermissaoCatraca() {
        listPermissaoCatraca.clear();
        listPermissaoCatraca = new PermissaoCatracaDao().findByPessoa(pessoa.getId());
    }

    public void save() {
        if (listDepartamentos.isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum departamento disponível!");
            return;
        }
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar uma pessoa!");
            return;
        }
        Dao dao = new Dao();
        permissaoCatraca.setPessoa(pessoa);
        permissaoCatraca.setDepartamento((Departamento) dao.find(new Departamento(), Integer.parseInt(departamento_id)));
        if (permissaoCatraca.getId() == null) {
            if (dao.save(permissaoCatraca, true)) {
                NovoLog novoLog = new NovoLog();
                String saveString
                        = "ID: " + permissaoCatraca.getId()
                        + " - Pessoa: (" + permissaoCatraca.getPessoa().getId() + ") " + permissaoCatraca.getPessoa().getNome()
                        + " - Departamento: (" + permissaoCatraca.getDepartamento().getId() + ") " + permissaoCatraca.getDepartamento();
                novoLog.setTabela("seg_permissao_catraca");
                novoLog.setCodigo(permissaoCatraca.getId());
                novoLog.save(saveString);
                GenericaMensagem.info("Sucesso", "Registro inserido");
                permissaoCatraca = new PermissaoCatraca();
                loadDepartamento();
                loadPermissaoCatraca();
            } else {
                GenericaMensagem.warn("Erro", "Erro ao inserir registro!");
            }
        }
    }

    public void remove(PermissaoCatraca pc) {
        if (new Dao().delete(pc, true)) {
            NovoLog novoLog = new NovoLog();
            String saveString
                    = "ID: " + pc.getId()
                    + " - Pessoa: (" + pc.getPessoa().getId() + ") " + pc.getPessoa().getNome()
                    + " - Departamento: (" + pc.getDepartamento().getId() + ") " + pc.getDepartamento();
            novoLog.setTabela("seg_permissao_catraca");
            novoLog.setCodigo(pc.getId());
            novoLog.delete(saveString);
            GenericaMensagem.info("Sucesso", "Registro removido");
            loadDepartamento();
            loadPermissaoCatraca();
        } else {
            GenericaMensagem.warn("Erro", "Erro ao remover registro!");
        }
    }

    public List<SelectItem> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<SelectItem> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public PermissaoCatraca getPermissaoCatraca() {
        return permissaoCatraca;
    }

    public void setPermissaoCatraca(PermissaoCatraca permissaoCatraca) {
        this.permissaoCatraca = permissaoCatraca;
    }

    public List<PermissaoCatraca> getListPermissaoCatraca() {
        return listPermissaoCatraca;
    }

    public void setListPermissaoCatraca(List<PermissaoCatraca> listPermissaoCatraca) {
        this.listPermissaoCatraca = listPermissaoCatraca;
    }

    public String getDepartamento_id() {
        return departamento_id;
    }

    public void setDepartamento_id(String departamento_id) {
        this.departamento_id = departamento_id;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            pessoa = (Pessoa) GenericaSessao.getObject("pessoaPesquisa", true);
            loadDepartamento();
            loadPermissaoCatraca();
        }
        if (GenericaSessao.exists("usuarioPesquisa")) {
            pessoa = ((Usuario) GenericaSessao.getObject("usuarioPesquisa", true)).getPessoa();
            loadDepartamento();
            loadPermissaoCatraca();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

}
