/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.AtualizaSocioDemissionadoDao;
import br.com.rtools.cobranca.TmktContato;
import br.com.rtools.cobranca.TmktHistorico;
import br.com.rtools.cobranca.TmktNatureza;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class AtualizaSocioDemissionadoBean implements Serializable {

    private String filtro = "contactar";
    private List<ObjectListaSocioDemissionado> listaSocioDemissionado = new ArrayList();
    private List<ObjectListaSocioDemissionado> listaSocioDemissionadoSelecionado = new ArrayList();
    private String aposentadoria = "nao_aposentado";

    public AtualizaSocioDemissionadoBean() {
        loadListaSocioDemissionado();
    }

    public final void loadListaSocioDemissionado() {
        listaSocioDemissionado.clear();
        listaSocioDemissionadoSelecionado.clear();

        AtualizaSocioDemissionadoDao dao_a = new AtualizaSocioDemissionadoDao();

        List<Object> result = dao_a.listaSocioDemissionado(filtro, aposentadoria);

        Dao dao = new Dao();
        for (Object ob : result) {
            List linha = (List) ob;

            listaSocioDemissionado.add(
                    new ObjectListaSocioDemissionado(
                            false,
                            (PessoaEmpresa) dao.find(new PessoaEmpresa(), (Integer) linha.get(0)),
                            linha.get(7) != null ? (Date) linha.get(7) : null
                    )
            );
        }
    }

    public void salvar() {
        if (listaSocioDemissionadoSelecionado.isEmpty()) {
            GenericaMensagem.error("Atenção", "Selecione ao menos uma linha para Salvar");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        for (ObjectListaSocioDemissionado ob : listaSocioDemissionadoSelecionado) {
            TmktHistorico historico = new TmktHistorico(
                    -1,
                    DataHoje.dataHoje(),
                    ob.getPessoaEmpresa().getFisica().getPessoa(),
                    Usuario.getUsuario(),
                    (Departamento) dao.find(new Departamento(), 6),
                    (TmktContato) dao.find(new TmktContato(), 5),
                    (TmktNatureza) dao.find(new TmktNatureza(), 2),
                    "",
                    "AVISO DE INATIVAÇÃO POR DEMISSÃO DA EMPRESA",
                    ob.getPessoaEmpresa()
            );

            if (!dao.save(historico)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Não foi possível salvar Histórico");
                return;
            }
        }

        dao.commit();
        loadListaSocioDemissionado();
        GenericaMensagem.info("Sucesso", "Registros Atualizados");
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<ObjectListaSocioDemissionado> getListaSocioDemissionado() {
        return listaSocioDemissionado;
    }

    public void setListaSocioDemissionado(List<ObjectListaSocioDemissionado> listaSocioDemissionado) {
        this.listaSocioDemissionado = listaSocioDemissionado;
    }

    public List<ObjectListaSocioDemissionado> getListaSocioDemissionadoSelecionado() {
        return listaSocioDemissionadoSelecionado;
    }

    public void setListaSocioDemissionadoSelecionado(List<ObjectListaSocioDemissionado> listaSocioDemissionadoSelecionado) {
        this.listaSocioDemissionadoSelecionado = listaSocioDemissionadoSelecionado;
    }

    public String getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(String aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public class ObjectListaSocioDemissionado {

        private Boolean checado;
        private PessoaEmpresa pessoaEmpresa;
        private Date dataLancamento;

        public ObjectListaSocioDemissionado(Boolean checado, PessoaEmpresa pessoaEmpresa, Date dataLancamento) {
            this.checado = checado;
            this.pessoaEmpresa = pessoaEmpresa;
            this.dataLancamento = dataLancamento;
        }

        public Boolean getChecado() {
            return checado;
        }

        public void setChecado(Boolean checado) {
            this.checado = checado;
        }

        public PessoaEmpresa getPessoaEmpresa() {
            return pessoaEmpresa;
        }

        public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
            this.pessoaEmpresa = pessoaEmpresa;
        }

        public Date getDataLancamento() {
            return dataLancamento;
        }

        public void setDataLancamento(Date dataLancamento) {
            this.dataLancamento = dataLancamento;
        }

        public String getDataLancamentoString() {
            return DataHoje.converteData(dataLancamento);
        }

        public void setDataLancamentoString(String dataLancamentoString) {
            this.dataLancamento = DataHoje.converte(dataLancamentoString);
        }
    }
}
