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
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
    private Double valorBoleto;
    private Double valorFolha;

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
        this.valorBoleto = new Double(0);
        this.valorFolha = new Double(0);
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
        valorFolha = new Double(0);
        valorBoleto = new Double(0);
        folhaEmpresa = new FolhaEmpresa();
        folhaEmpresa.setJuridica(new Juridica());
        folhaEmpresa.setReferencia(referencia);
        empresa = new Juridica();
        idTipoServico = idTipoServicoFilter;
        visible = true;
    }

    public void close() {
        folhaEmpresa = new FolhaEmpresa();
        if (empresa.getId() != -1) {
            // loadListFolhaEmpresas();
        }
        empresa = new Juridica();
        idTipoServico = 1;
        loadListServicos();
        visible = false;
    }

    public void removeEmpresa() {
        valorFolha = new Double(0);
        valorBoleto = new Double(0);
        folhaEmpresa = new FolhaEmpresa();
        folhaEmpresa.setReferencia(referencia);
        empresa = new Juridica();
        folhaEmpresa.setJuridica(new Juridica());
        idTipoServico = idTipoServicoFilter;
        visible = true;
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
        folhaEmpresa.setValorMes(new Float(valorFolha.toString()));
        if (folhaEmpresa.getValorMes() <= 0) {
            GenericaMensagem.warn("Validação", "INFORMAR VALORES DO BOLETO OU FOLHA!");
            return;
        }
        if (folhaEmpresa.getNumFuncionarios() < 0) {
            GenericaMensagem.warn("Validação", "INFORMAR NÚMERO DE FUNCIONÁRIOS IGUAL OU MAIOR QUE ZERO!");
            return;
        }
        dao.openTransaction();
        if (folhaEmpresa.getId() == -1) {
            List<ContribuintesInativos> ci = new ContribuintesInativosDao().findByJuridica(folhaEmpresa.getJuridica().getId());
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
            GenericaMensagem.info("Sucesso", "REGISTRO INSERIDO");
        } else {
            if (!dao.update(folhaEmpresa)) {
                GenericaMensagem.warn("Erro", "AO INSERIR REGISTRO!");
                dao.rollback();
                return;
            }
            GenericaMensagem.info("Sucesso", "REGISTRO ATUALIZADO");
        }
        dao.commit();
        for (int i = 0; i < listFolhaEmpresas.size(); i++) {
            if (folhaEmpresa.getJuridica().getPessoa().getDocumento().equals(listFolhaEmpresas.get(i).getDocumento())) {
                if (listFolhaEmpresas.get(i).getId() == null) {
                    listFolhaEmpresas.get(i).setId(folhaEmpresa.getId());
                }
                listFolhaEmpresas.get(i).setNumero_funcionarios(folhaEmpresa.getNumFuncionariosString());
                listFolhaEmpresas.get(i).setValor_boleto(valorBoleto);
                listFolhaEmpresas.get(i).setValor_folha(valorFolha);
                break;
            }
        }

    }

    public void edit(FolhaEmpresaObject fe) {
        valorFolha = new Double(0);
        valorBoleto = new Double(0);
        if (fe.getId() != null) {
            folhaEmpresa = (FolhaEmpresa) new Dao().find(new FolhaEmpresa(), new Integer(fe.getId().toString()));
            idTipoServico = folhaEmpresa.getTipoServico().getId();
            idServico = Integer.parseInt(fe.getServicos_id() + "");
            valorFolha = (double) folhaEmpresa.getValorMes();
            changeValorBoleto();
        } else {
            folhaEmpresa = new FolhaEmpresa();
            folhaEmpresa.setReferencia(referencia);
            empresa = (Juridica) new Dao().find(new Juridica(), new Integer(fe.getJuridica_id().toString()));
            idTipoServico = idTipoServicoFilter;
        }
        visible = true;
    }

    public void delete(FolhaEmpresaObject fe) {
        if (fe.getId() != null) {
            new Dao().delete(new Dao().find(new FolhaEmpresa(), fe.getId()), true);
        }
        for (int i = 0; i < listFolhaEmpresas.size(); i++) {
            if (fe.getId() == listFolhaEmpresas.get(i).getId()) {
                listFolhaEmpresas.get(i).setId(null);
                listFolhaEmpresas.get(i).setValor_boleto("0,00");
                listFolhaEmpresas.get(i).setValor_folha("0,00");
                listFolhaEmpresas.get(i).setNumero_funcionarios(0);
                break;
            }
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
            listFolhaEmpresas.add(new FolhaEmpresaObject(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7)));
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
                for (int i = 0; i < listFolhaEmpresas.size(); i++) {
                    if (folhaEmpresa.getJuridica().getPessoa().getDocumento().equals(listFolhaEmpresas.get(i).getDocumento())) {
                        folhaEmpresa.setReferencia(referencia);
                        folhaEmpresa.setDtLancamento(new Date());
                        folhaEmpresa.setJuridica((Juridica) new Dao().find(new Juridica(), (int) listFolhaEmpresas.get(i).getJuridica_id()));
                        folhaEmpresa.setNumFuncionarios(0);
                        folhaEmpresa.setValorMes(0);
                        return;
                    }
                }
                // Juridica j = (Juridica) new JuridicaDao().findByDocumento(folhaEmpresa.getJuridica().getPessoa().getDocumento());
                // if (j != null) {
                // folhaEmpresa.setJuridica(j);
                // }
            }
        }
    }

    public void findByDocument(String document) {
        if (folhaEmpresa.getJuridica().getId() == -1) {
            if (!document.isEmpty()) {
                Juridica j = (Juridica) new JuridicaDao().findByDocumento(document);
                if (j != null) {
                    valorFolha = new Double(0);
                    valorBoleto = new Double(0);
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
        return valorBoleto;
    }

    public String getValorBoletoString() {
        return Moeda.converteR$Double(valorBoleto);
    }

    public void setValorBoletoString(String valorBoletoString) {
        this.valorBoleto = (double) Moeda.converteUS$(valorBoletoString);
    }

    public Double getValorFolha() {
        return valorFolha;
    }

    public void setValorFolha(Double valorFolha) {
        this.valorFolha = valorFolha;
    }

    public String getValorFolhaString() {
        return Moeda.converteR$Double(valorFolha);
    }

    public void setValorFolhaString(String valorFolhaString) {
        this.valorFolha = (double) Moeda.converteUS$(valorFolhaString);
    }

    public void changeValorBoletoEFolha() {
        valorFolha = new Double(0);
        valorBoleto = new Double(0);
        // changeValorBoleto();
        // changeValorFolha();
    }

    public void changeValorBoleto() {
        this.valorBoleto = new FunctionsDao().arrCalculaValorBoleto(folhaEmpresa.getJuridica().getPessoa().getId(), idServico, folhaEmpresa.getReferencia(), valorFolha.toString(), folhaEmpresa.getNumFuncionarios());
    }

    public void changeValorFolha() {
        this.valorFolha = new FunctionsDao().arrCalculaValorFolha(folhaEmpresa.getJuridica().getPessoa().getId(), idServico, folhaEmpresa.getReferencia(), valorBoleto.toString());
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

    public Servicos getServicoNome() {
        try {
            Servicos s = (Servicos) new Dao().find(new Servicos(), idServico);
            return s;
        } catch (Exception e) {
            return new Servicos();
        }
    }

    public class FolhaEmpresaObject {

        private Object documento;
        private Object nome;
        private Object valor_folha;
        private Object valor_boleto;
        private Object servicos_id;
        private Object id;
        private Object numero_funcionarios;
        private Object juridica_id;

        public FolhaEmpresaObject() {
            this.documento = "";
            this.nome = "";
            this.valor_folha = "0,00";
            this.valor_boleto = "0,00";
            this.servicos_id = null;
            this.id = null;
            this.numero_funcionarios = 0;
            this.juridica_id = null;
        }

        public FolhaEmpresaObject(Object documento, Object nome, Object valor_folha, Object valor_boleto, Object servicos_id, Object id, Object numero_funcionarios, Object juridica_id) {
            this.documento = documento;
            this.nome = nome;
            this.valor_folha = valor_folha;
            this.valor_boleto = valor_boleto;
            this.servicos_id = servicos_id;
            this.id = id;
            this.numero_funcionarios = numero_funcionarios;
            this.juridica_id = juridica_id;
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

        public Object getJuridica_id() {
            return juridica_id;
        }

        public void setJuridica_id(Object juridica_id) {
            this.juridica_id = juridica_id;
        }

    }

}
