/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.DeclaracaoPeriodo;
import br.com.rtools.associativo.DeclaracaoPessoa;
import br.com.rtools.associativo.DeclaracaoTipo;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.dao.DeclaracaoPessoaDao;
import br.com.rtools.associativo.dao.DeclaracaoTipoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class DeclaracaoPessoaBean implements Serializable {

    private Integer indexDeclaracaoTipo = 0;
    private List<SelectItem> listaDeclaracaoTipo = new ArrayList();
    private Integer indexConvenio = 0;
    private List<SelectItem> listaConvenio = new ArrayList();
    private DeclaracaoPessoa declaracaoPessoa = new DeclaracaoPessoa();
    private List<DeclaracaoPessoa> listaDeclaracaoPessoa = new ArrayList();

    private String descricaoPesquisa = "";
    private List<ObjectPesquisaPessoa> listaPessoa = new ArrayList();
    private ObjectPesquisaPessoa objPesquisaPessoaSelecionada = null;

    private Boolean chkTodosConvenios = false;

    private Integer indexDeclaracaoPeriodo = 0;
    private List<SelectItem> listaDeclaracaoPeriodo = new ArrayList();

    public DeclaracaoPessoaBean() {
        loadListaDeclaracaoTipo();
        loadListaDeclaracaoPeriodo();

        loadListaConvenio();
        loadListaDeclaracaoPessoa();
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.openTransaction();

        String delete_log
                = "Pessoa: " + declaracaoPessoa.getPessoa().getDocumento() + ": " + declaracaoPessoa.getPessoa().getNome() + " \n "
                + "Convênio: " + declaracaoPessoa.getConvenio().getDocumento() + ": " + declaracaoPessoa.getConvenio().getNome() + " \n "
                + "Tipo Declaração: " + declaracaoPessoa.getDeclaracaoPeriodo().getDeclaracaoTipo().getDescricao() + " \n "
                + "Período Declaração: " + declaracaoPessoa.getDeclaracaoPeriodo().getDescricao() + " - " + declaracaoPessoa.getDeclaracaoPeriodo().getAno();

        Integer declaracao_id = declaracaoPessoa.getId();
        if (!dao.delete(declaracaoPessoa)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Erro ao excluir declaração!");
            return;
        }

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("soc_declaracao_pessoa");

        novoLog.setCodigo(declaracao_id);

        novoLog.delete(
                delete_log
        );

        dao.commit();

        declaracaoPessoa = new DeclaracaoPessoa();

        loadListaDeclaracaoPessoa();

        GenericaMensagem.info("Sucesso", "Declaração excluída!");
    }

    public void novo() {
        GenericaSessao.put("declaracaoPessoaBean", new DeclaracaoPessoaBean());
    }

    public void selecionarPessoa(ObjectPesquisaPessoa opp) {
        objPesquisaPessoaSelecionada = opp;
    }

    public void limparPesquisa() {
        listaPessoa.clear();
        descricaoPesquisa = "";
    }

    public Boolean validaImprimir() {
        if (objPesquisaPessoaSelecionada == null) {
            return false;
        }

        DeclaracaoTipo declaracao_tipo = (DeclaracaoTipo) new Dao().find(new DeclaracaoTipo(), Integer.valueOf(listaDeclaracaoTipo.get(indexDeclaracaoTipo).getDescription()));
        DeclaracaoPeriodo declaracao_periodo = (DeclaracaoPeriodo) new Dao().find(new DeclaracaoPeriodo(), Integer.valueOf(listaDeclaracaoPeriodo.get(indexDeclaracaoPeriodo).getDescription()));

        if (objPesquisaPessoaSelecionada.getParentesco().getId() != 1) {
            if (objPesquisaPessoaSelecionada.getIdade() < declaracao_tipo.getIdadeInicio()) {
                GenericaMensagem.error("Atenção", "Beneficiário fora abaixo da faixa de idade ! Idade mínima" + declaracao_tipo.getIdadeInicio());
                return false;
            }
            if (objPesquisaPessoaSelecionada.getIdade() > declaracao_tipo.getIdadeFinal()) {
                GenericaMensagem.error("Atenção", "Beneficiário fora abaixo da faixa de idade ! Idade máxima" + declaracao_tipo.getIdadeFinal());
                return false;
            }
        }

        if (!new DeclaracaoPessoaDao().listaDeclaracaoPessoaAnoVigente(objPesquisaPessoaSelecionada.getBeneficiario().getId(), declaracao_periodo.getId()).isEmpty()) {
            GenericaMensagem.error("Atenção", "Declaração já impressa para o ano vigente!");
            return false;
        }

        if (new FunctionsDao().inadimplente(objPesquisaPessoaSelecionada.getBeneficiario().getId(), declaracao_tipo.getDiasCarencia())) {
            GenericaMensagem.error("Atenção", "Beneficiário Inadimplente não pode imprimir declaração!");
            return false;
        }

        return true;
    }

    public void imprimir() {
        if (!validaImprimir()) {
            return;
        }

        DeclaracaoPeriodo declaracao_periodo = (DeclaracaoPeriodo) new Dao().find(new DeclaracaoPeriodo(), Integer.valueOf(listaDeclaracaoPeriodo.get(indexDeclaracaoPeriodo).getDescription()));
        Pessoa pessoa_convenio = (Pessoa) new Dao().find(new Pessoa(), Integer.valueOf(listaConvenio.get(indexConvenio).getDescription()));

        DeclaracaoPessoa declaracao_pessoa = new DeclaracaoPessoa(-1, DataHoje.dataHoje(), objPesquisaPessoaSelecionada.getBeneficiario(), pessoa_convenio, declaracao_periodo, objPesquisaPessoaSelecionada.getMatricula());

        Dao dao = new Dao();
        dao.openTransaction();

        if (!dao.save(declaracao_pessoa)) {
            dao.rollback();
            GenericaMensagem.error("Atenção", "Não foi possível salvar Declaração Pessoa");
            return;
        }

        dao.commit();

        String save_log
                = "Pessoa: " + declaracao_pessoa.getPessoa().getDocumento() + ": " + declaracao_pessoa.getPessoa().getNome() + " \n "
                + "Convênio: " + declaracao_pessoa.getConvenio().getDocumento() + ": " + declaracao_pessoa.getConvenio().getNome() + " \n "
                + "Tipo Declaração: " + declaracao_pessoa.getDeclaracaoPeriodo().getDeclaracaoTipo().getDescricao() + " \n "
                + "Período Declaração: " + declaracao_pessoa.getDeclaracaoPeriodo().getDescricao() + " - " + declaracao_pessoa.getDeclaracaoPeriodo().getAno();

        NovoLog novoLog = new NovoLog();
        novoLog.setTabela("soc_declaracao_pessoa");

        novoLog.setCodigo(declaracao_pessoa.getId());
        novoLog.save(
                save_log
        );

        imprimir_jasper(declaracao_pessoa);

        objPesquisaPessoaSelecionada = null;
        loadListaDeclaracaoPessoa();
    }

    public void print(Integer declaraca_pessoa_id) {
        imprimir_jasper((DeclaracaoPessoa) new Dao().find(new DeclaracaoPessoa(), declaraca_pessoa_id));
    }

    public void imprimir_jasper(DeclaracaoPessoa declaracao_pessoa) {
        Map map = new HashMap();
        map.put("titular", declaracao_pessoa.getMatricula().getTitular().getNome());
        map.put("convenio", declaracao_pessoa.getConvenio().getNome());
        map.put("data_emissao", declaracao_pessoa.getDtEmissao());

        Map map_ob = new HashMap();
        map_ob.put("nome_beneficiario", declaracao_pessoa.getPessoa().getNome());
        map_ob.put("parentesco_beneficiario", declaracao_pessoa.getPessoa().getSocios().getParentesco().getParentesco());
        map_ob.put("codigo_beneficiario", declaracao_pessoa.getMatricula().getNrMatricula());

        List list = new ArrayList();
        list.add(map_ob);

        Jasper.TYPE = "default";
        Jasper.FILIAL = (Filial) new Dao().find(new Filial(), 1);
        Jasper.printReports(declaracao_pessoa.getDeclaracaoPeriodo().getDeclaracaoTipo().getJasper(), declaracao_pessoa.getDeclaracaoPeriodo().getDeclaracaoTipo().getDescricao(), list, map);
    }

    public final void loadListaDeclaracaoTipo() {
        listaDeclaracaoTipo.clear();
        List<DeclaracaoTipo> result = new DeclaracaoTipoDao().listaDeclaracaoTipo();

        for (int i = 0; i < result.size(); i++) {
            listaDeclaracaoTipo.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaDeclaracaoPeriodo() {
        listaDeclaracaoPeriodo.clear();
        List<DeclaracaoPeriodo> result = new DeclaracaoTipoDao().listaDeclaracaoPeriodoEmissao(Integer.valueOf(listaDeclaracaoTipo.get(indexDeclaracaoTipo).getDescription()));

        for (int i = 0; i < result.size(); i++) {
            listaDeclaracaoPeriodo.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao() + " - " + result.get(i).getAno(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public final void loadListaDeclaracao() {
        loadListaConvenio();

        loadListaDeclaracaoPeriodo();
    }

    public final void loadListaConvenio() {
        listaConvenio.clear();
        List<Object> result = new DeclaracaoPessoaDao().listaConvenio(Integer.valueOf(listaDeclaracaoTipo.get(indexDeclaracaoTipo).getDescription()));

        for (int i = 0; i < result.size(); i++) {
            List linha = (List) result.get(i);

            listaConvenio.add(
                    new SelectItem(
                            i,
                            linha.get(1).toString() + "; " + linha.get(2).toString() + "; " + linha.get(3).toString(),
                            "" + (Integer) linha.get(0)
                    )
            );
        }
    }

    public final void loadListaDeclaracaoPessoa() {
        listaDeclaracaoPessoa.clear();

        Integer id_pessoa_convenio = Integer.valueOf(listaConvenio.get(indexConvenio).getDescription());
        listaDeclaracaoPessoa = new DeclaracaoPessoaDao().listaDeclaracaoPessoa(chkTodosConvenios == false ? id_pessoa_convenio : null);

    }

    public final void loadListaPessoa(String InicialParcial) {
        listaPessoa.clear();

        List<Object> result = new DeclaracaoPessoaDao().listaPessoa(descricaoPesquisa, InicialParcial);
        Dao dao = new Dao();
        FunctionsDao func = new FunctionsDao();
        for (Object ob : result) {
            List linha = (List) ob;
            listaPessoa.add(
                    new ObjectPesquisaPessoa(
                            func.titularDaPessoa((Integer) linha.get(0)),
                            (Pessoa) dao.find(new Pessoa(), (Integer) linha.get(0)),
                            (MatriculaSocios) dao.find(new MatriculaSocios(), (Integer) linha.get(1)),
                            (Categoria) dao.find(new Categoria(), (Integer) linha.get(2)),
                            (Parentesco) dao.find(new Parentesco(), (Integer) linha.get(3)),
                            (Integer) linha.get(4)
                    )
            );
        }
    }

    public Integer getIndexDeclaracaoTipo() {
        return indexDeclaracaoTipo;
    }

    public void setIndexDeclaracaoTipo(Integer indexDeclaracaoTipo) {
        this.indexDeclaracaoTipo = indexDeclaracaoTipo;
    }

    public List<SelectItem> getListaDeclaracaoTipo() {
        return listaDeclaracaoTipo;
    }

    public void setListaDeclaracaoTipo(List<SelectItem> listaDeclaracaoTipo) {
        this.listaDeclaracaoTipo = listaDeclaracaoTipo;
    }

    public Integer getIndexConvenio() {
        return indexConvenio;
    }

    public void setIndexConvenio(Integer indexConvenio) {
        this.indexConvenio = indexConvenio;
    }

    public List<SelectItem> getListaConvenio() {
        return listaConvenio;
    }

    public void setListaConvenio(List<SelectItem> listaConvenio) {
        this.listaConvenio = listaConvenio;
    }

    public DeclaracaoPessoa getDeclaracaoPessoa() {
        return declaracaoPessoa;
    }

    public void setDeclaracaoPessoa(DeclaracaoPessoa declaracaoPessoa) {
        this.declaracaoPessoa = declaracaoPessoa;
    }

    public List<DeclaracaoPessoa> getListaDeclaracaoPessoa() {
        return listaDeclaracaoPessoa;
    }

    public void setListaDeclaracaoPessoa(List<DeclaracaoPessoa> listaDeclaracaoPessoa) {
        this.listaDeclaracaoPessoa = listaDeclaracaoPessoa;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public List<ObjectPesquisaPessoa> getListaPessoa() {
        return listaPessoa;
    }

    public void setListaPessoa(List<ObjectPesquisaPessoa> listaPessoa) {
        this.listaPessoa = listaPessoa;
    }

    public ObjectPesquisaPessoa getObjPesquisaPessoaSelecionada() {
        return objPesquisaPessoaSelecionada;
    }

    public void setObjPesquisaPessoaSelecionada(ObjectPesquisaPessoa objPesquisaPessoaSelecionada) {
        this.objPesquisaPessoaSelecionada = objPesquisaPessoaSelecionada;
    }

    public Boolean getChkTodosConvenios() {
        return chkTodosConvenios;
    }

    public void setChkTodosConvenios(Boolean chkTodosConvenios) {
        this.chkTodosConvenios = chkTodosConvenios;
    }

    public Integer getIndexDeclaracaoPeriodo() {
        return indexDeclaracaoPeriodo;
    }

    public void setIndexDeclaracaoPeriodo(Integer indexDeclaracaoPeriodo) {
        this.indexDeclaracaoPeriodo = indexDeclaracaoPeriodo;
    }

    public List<SelectItem> getListaDeclaracaoPeriodo() {
        return listaDeclaracaoPeriodo;
    }

    public void setListaDeclaracaoPeriodo(List<SelectItem> listaDeclaracaoPeriodo) {
        this.listaDeclaracaoPeriodo = listaDeclaracaoPeriodo;
    }

    public class ObjectPesquisaPessoa {

        private Pessoa titular;
        private Pessoa beneficiario;
        private MatriculaSocios matricula;
        private Categoria categoria;
        private Parentesco parentesco;
        private Integer idade;

        public ObjectPesquisaPessoa(Pessoa titular, Pessoa beneficiario, MatriculaSocios matricula, Categoria categoria, Parentesco parentesco, Integer idade) {
            this.titular = titular;
            this.beneficiario = beneficiario;
            this.matricula = matricula;
            this.categoria = categoria;
            this.parentesco = parentesco;
            this.idade = idade;
        }

        public Pessoa getTitular() {
            return titular;
        }

        public void setTitular(Pessoa titular) {
            this.titular = titular;
        }

        public Pessoa getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Pessoa beneficiario) {
            this.beneficiario = beneficiario;
        }

        public MatriculaSocios getMatricula() {
            return matricula;
        }

        public void setMatricula(MatriculaSocios matricula) {
            this.matricula = matricula;
        }

        public Categoria getCategoria() {
            return categoria;
        }

        public void setCategoria(Categoria categoria) {
            this.categoria = categoria;
        }

        public Parentesco getParentesco() {
            return parentesco;
        }

        public void setParentesco(Parentesco parentesco) {
            this.parentesco = parentesco;
        }

        public Integer getIdade() {
            return idade;
        }

        public void setIdade(Integer idade) {
            this.idade = idade;
        }

    }
}
