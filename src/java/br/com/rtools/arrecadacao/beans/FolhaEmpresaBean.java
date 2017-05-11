package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.ContribuintesInativos;
import br.com.rtools.arrecadacao.FolhaEmpresa;
import br.com.rtools.arrecadacao.dao.ContribuintesInativosDao;
import br.com.rtools.arrecadacao.dao.FolhaEmpresaDao;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.TipoServico;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class FolhaEmpresaBean implements Serializable {

    private FolhaEmpresa folhaEmpresa;
    private List<FolhaEmpresaObject> listFolhaEmpresas;
    private List<SelectItem> listTipoServicos;
    private Integer idTipoServico;
    private List<SelectItem> listServicos;
    private Integer idServico;
    private Integer idTipoServicoFilter;
    private Juridica empresa;
    private String referencia;
    private String by;
    private String description;
    private Boolean visible;

    public FolhaEmpresaBean() {
        GenericaSessao.remove("juridicaPesquisa");
        this.folhaEmpresa = new FolhaEmpresa();
        this.listFolhaEmpresas = new ArrayList<>();
        this.listTipoServicos = new ArrayList<>();
        this.listServicos = new ArrayList<>();
        this.idServico = 1;
        this.idTipoServico = 1;
        this.idTipoServicoFilter = 1;
        this.empresa = new Juridica();
        this.referencia = DataHoje.converteDataParaReferencia(DataHoje.dataHoje());
        by = "nome";
        description = "";
        visible = false;
        loadListServicos();
        loadListTipoServicos();
        loadListFolhaEmpresas();
    }

    public void clear() {
        GenericaSessao.remove("folhaEmpresaBean");
        GenericaSessao.remove("juridicaPesquisa");
    }

    public void newRegister() {
        folhaEmpresa = new FolhaEmpresa();
        folhaEmpresa.setReferencia(referencia);
        empresa = new Juridica();
        idTipoServico = idTipoServicoFilter;
        visible = true;
    }

    public void close() {
        folhaEmpresa = new FolhaEmpresa();
        empresa = new Juridica();
        idTipoServico = 1;
        loadListServicos();
        visible = false;        
    }

    public void removeEmpresa() {
        empresa = new Juridica();
        folhaEmpresa.setJuridica(new Juridica());
        loadListFolhaEmpresas();
    }

    public void save() {
        FolhaEmpresaDao fed = new FolhaEmpresaDao();
        Dao dao = new Dao();
        folhaEmpresa.setTipoServico((TipoServico) dao.find(new TipoServico(), idTipoServico));
        if (folhaEmpresa.getJuridica().getId() == -1) {
            GenericaMensagem.warn("Validação", "INFORMAR EMPRESA!");
            return;
        }
        if (folhaEmpresa.getReferencia().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR REFERÊNCIA!");
            return;
        }
        if (folhaEmpresa.getValorMes() <= 0) {
            GenericaMensagem.warn("Validação", "INFORMAR VALOR DO MÊS!");
            return;
        }
        if (folhaEmpresa.getNumFuncionarios() <= 0) {
            GenericaMensagem.warn("Validação", "INFORMAR NÚMERO DE FUNCIONÁRIOS!");
            return;
        }
        dao.openTransaction();
        if (folhaEmpresa.getId() == -1) {
            List<ContribuintesInativos> ci = new ContribuintesInativosDao().findByJuridica(empresa.getId());
            if (!ci.isEmpty()) {
                dao.rollback();
                GenericaMensagem.warn("Validação", "CONTRIBUINTE INÁTIVO");
                return;
            }
            if (fed.findBy(folhaEmpresa.getJuridica().getId(), idTipoServico, folhaEmpresa.getReferencia()) != null) {
                dao.rollback();
                GenericaMensagem.warn("Validação", "FOLHA JÁ CADASTRADA PARA ESSA REFERÊNCIA!");
                return;
            }
            if (!dao.save(folhaEmpresa)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
        } else {
            if (!dao.update(folhaEmpresa)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
        }
        dao.commit();
        GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        loadListFolhaEmpresas();

    }

    public void edit(FolhaEmpresaObject fe) {
        if (fe.getId() != null) {
            folhaEmpresa = (FolhaEmpresa) new Dao().find(new FolhaEmpresa(), new Integer(fe.getId().toString()));
            idTipoServico = folhaEmpresa.getTipoServico().getId();
            idServico = Integer.parseInt(fe.getServicos_id() + "");
        }
    }

    public void delete(FolhaEmpresaObject fe) {
        if (fe.getId() != null) {
            new Dao().delete(new Dao().find(new FolhaEmpresa(), fe.getId()));

        }
    }

    public void loadListServicos() {
        listServicos = new ArrayList<>();
        idServico = 1;
        List<Servicos> list = new ServicosDao().pesquisaTodos(4);
        for (int i = 0; i < list.size(); i++) {
            listServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListTipoServicos() {
        listTipoServicos = new ArrayList<>();
        idTipoServico = 1;
        List<TipoServico> list = new Dao().list(new TipoServico(), true);
        for (int i = 0; i < list.size(); i++) {
            listTipoServicos.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListFolhaEmpresas() {
        listFolhaEmpresas = new ArrayList();
        List list = new FolhaEmpresaDao().findByNative(by, description, idServico, idTipoServicoFilter, referencia);
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            listFolhaEmpresas.add(new FolhaEmpresaObject(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6)));
        }
    }

    public FolhaEmpresa getFolhaEmpresa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            folhaEmpresa.setJuridica((Juridica) GenericaSessao.getObject("juridicaPesquisa", true));
        }
        return folhaEmpresa;
    }

    public void setFolhaEmpresa(FolhaEmpresa folhaEmpresa) {
        this.folhaEmpresa = folhaEmpresa;
    }

    public List<FolhaEmpresaObject> getListFolhaEmpresas() {
        return listFolhaEmpresas;
    }

    public void setListFolhaEmpresas(List<FolhaEmpresaObject> listFolhaEmpresas) {
        this.listFolhaEmpresas = listFolhaEmpresas;
    }

    public List<SelectItem> getListTipoServicos() {
        return listTipoServicos;
    }

    public void setListTipoServicos(List<SelectItem> listTipoServicos) {
        this.listTipoServicos = listTipoServicos;
    }

    public Integer getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(Integer idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public Juridica getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public void findByDocument() {
        if (folhaEmpresa.getJuridica().getId() == -1) {
            if (!folhaEmpresa.getJuridica().getPessoa().getDocumento().isEmpty()) {
                Juridica j = (Juridica) new JuridicaDao().findByDocumento(folhaEmpresa.getJuridica().getPessoa().getDocumento());
                if (j != null) {
                    folhaEmpresa.setJuridica(j);
                }
            }
        }
    }

    public void findByDocument(String document) {
        if (folhaEmpresa.getJuridica().getId() == -1) {
            if (!document.isEmpty()) {
                Juridica j = (Juridica) new JuridicaDao().findByDocumento(document);
                if (j != null) {
                    folhaEmpresa.setJuridica(j);
                    folhaEmpresa.setReferencia(referencia);
                    idTipoServico = idTipoServicoFilter;
                    visible = true;
                }
            }
        }
    }

    public Integer getIdTipoServicoFilter() {
        return idTipoServicoFilter;
    }

    public void setIdTipoServicoFilter(Integer idTipoServicoFilter) {
        this.idTipoServicoFilter = idTipoServicoFilter;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Double getValorBoleto() {
        return new FunctionsDao().arrCalculaValorBoleto(folhaEmpresa.getJuridica().getPessoa().getId(), idServico, folhaEmpresa.getReferencia(), folhaEmpresa.getValorMes() + "", folhaEmpresa.getNumFuncionarios());
    }

    public Double getValorFolha() {
        return new FunctionsDao().arrCalculaValorFolha(folhaEmpresa.getJuridica().getPessoa().getId(), idServico, folhaEmpresa.getReferencia(), folhaEmpresa.getValorMes() + "");
    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public class FolhaEmpresaObject {

        private Object documento;
        private Object nome;
        private Object valor_folha;
        private Object valor_boleto;
        private Object servicos_id;
        private Object id;
        private Object numero_funcionarios;

        public FolhaEmpresaObject() {
            this.documento = "";
            this.nome = "";
            this.valor_folha = "0,00";
            this.valor_boleto = "0,00";
            this.servicos_id = null;
            this.id = null;
            this.numero_funcionarios = 0;
        }

        public FolhaEmpresaObject(Object documento, Object nome, Object valor_folha, Object valor_boleto, Object servicos_id, Object id, Object numero_funcionarios) {
            this.documento = documento;
            this.nome = nome;
            this.valor_folha = valor_folha;
            this.valor_boleto = valor_boleto;
            this.servicos_id = servicos_id;
            this.id = id;
            this.numero_funcionarios = numero_funcionarios;
        }

        public Object getDocumento() {
            return documento;
        }

        public void setDocumento(Object documento) {
            this.documento = documento;
        }

        public Object getNome() {
            return nome;
        }

        public void setNome(Object nome) {
            this.nome = nome;
        }

        public Object getValor_folha() {
            return valor_folha;
        }

        public void setValor_folha(Object valor_folha) {
            this.valor_folha = valor_folha;
        }

        public Object getValor_boleto() {
            return valor_boleto;
        }

        public void setValor_boleto(Object valor_boleto) {
            this.valor_boleto = valor_boleto;
        }

        public Object getServicos_id() {
            return servicos_id;
        }

        public void setServicos_id(Object servicos_id) {
            this.servicos_id = servicos_id;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public Object getNumero_funcionarios() {
            return numero_funcionarios;
        }

        public void setNumero_funcionarios(Object numero_funcionarios) {
            this.numero_funcionarios = numero_funcionarios;
        }

    }

}
