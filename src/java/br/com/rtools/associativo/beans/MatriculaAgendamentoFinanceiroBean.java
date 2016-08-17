/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.MatriculaAgendamentoFinanceiro;
import br.com.rtools.associativo.MatriculaSeguro;
import br.com.rtools.associativo.dao.MatriculaAgendamentoFinanceiroDao;
import br.com.rtools.associativo.dao.MatriculaSeguroDao;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class MatriculaAgendamentoFinanceiroBean implements Serializable {

    private MatriculaAgendamentoFinanceiro matriculaAgendamento = new MatriculaAgendamentoFinanceiro();
    private Integer indexServicos = 0;
    private List<SelectItem> listaServicos = new ArrayList();
    private Float valor = (float) 0;
    private Float desconto = (float) 0;
    private Float valorTotal = (float) 0;
    private List<ServicoPessoa> listaServicoPessoa = new ArrayList();

    private String descricaoPesquisa = "";
    private String tipoPesquisa = "nome";

    public MatriculaAgendamentoFinanceiroBean() {
        loadListaServicos();
        loadListaServicoPessoa();
        
        GenericaSessao.remove("fisicaPesquisa");
    }

    public final void loadListaServicoPessoa() {
        listaServicoPessoa.clear();

        listaServicoPessoa = new MatriculaAgendamentoFinanceiroDao().listaServicoPessoa(matriculaAgendamento.getServicoPessoa().getPessoa().getId());
    }

    public final void loadListaServicos() {
        listaServicos.clear();
        indexServicos = 0;

        ServicosDao db = new ServicosDao();
        List<Servicos> select = db.pesquisaTodos(423);
        for (int i = 0; i < select.size(); i++) {
            listaServicos.add(
                    new SelectItem(
                            i,
                            select.get(i).getDescricao(),
                            Integer.toString(select.get(i).getId())
                    )
            );
        }
    }

    public void salvar() {
        if (matriculaAgendamento.getServicoPessoa().getPessoa().getId() == -1) {
            GenericaMensagem.error("ATENÇÃO", "PESQUISE UMA PESSOA PARA SALVAR!");
            return;
        }

        Pessoa pessoaCobranca;
        if (matriculaAgendamento.getServicoPessoa().getPessoa().getSocios().getId() != -1) {
            pessoaCobranca = new FunctionsDao().titularDaPessoa(matriculaAgendamento.getServicoPessoa().getPessoa().getId());
        } else {
            if (matriculaAgendamento.getServicoPessoa().getPessoa().getFisica().getIdade() < 16){
                GenericaMensagem.error("ATENÇÃO", "Menor de 16 anos não pode ser responsável!");
                return;
            }
            
            pessoaCobranca = matriculaAgendamento.getServicoPessoa().getPessoa();
        }

        Dao dao = new Dao();
        NovoLog logs = new NovoLog();

        matriculaAgendamento.getServicoPessoa().setServicos((Servicos) dao.find(new Servicos(), Integer.valueOf(listaServicos.get(indexServicos).getDescription())));
        matriculaAgendamento.getServicoPessoa().setNrDiaVencimento(Registro.get().getFinDiaVencimentoCobranca());
        matriculaAgendamento.getServicoPessoa().setNrValorFixo(valor);
        matriculaAgendamento.getServicoPessoa().setCobranca(pessoaCobranca);

        dao.openTransaction();

        if (matriculaAgendamento.getId() == -1) {
            if (new MatriculaSeguroDao().pesquisaMatriculaSeguroPessoaAtiva(matriculaAgendamento.getServicoPessoa().getPessoa().getId(), matriculaAgendamento.getServicoPessoa().getServicos().getId()) != null) {
                GenericaMensagem.error("ATENÇÃO", "Esta pessoa já tem esse serviço ativo!");
                return;
            }

            matriculaAgendamento.getServicoPessoa().setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 13));
            matriculaAgendamento.getServicoPessoa().setCobranca(null);
            matriculaAgendamento.getServicoPessoa().setAtivo(true);
            matriculaAgendamento.getServicoPessoa().setBanco(true);
            matriculaAgendamento.getServicoPessoa().setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
            matriculaAgendamento.getServicoPessoa().setEvt(null);
            matriculaAgendamento.getServicoPessoa().setParceiro(null);

            if (!dao.save(matriculaAgendamento.getServicoPessoa())) {
                GenericaMensagem.error("ATENÇÃO", "ERRO AO SALVAR SERVIÇO PESSOA!");
                dao.rollback();
                return;
            }

            if (!dao.save(matriculaAgendamento)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO SALVAR MATRÍCULA SEGURO!");
                return;
            }

            String save_log
                    = "ID Seguro: " + matriculaAgendamento.getId() + " \n "
                    + "Pessoa: " + matriculaAgendamento.getServicoPessoa().getPessoa().getDocumento() + " : " + matriculaAgendamento.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Float(matriculaAgendamento.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + matriculaAgendamento.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + matriculaAgendamento.getServicoPessoa().isDescontoFolha();

            logs.save(save_log);

            GenericaMensagem.info("SUCESSO", "MATRÍCULA SEGURO SALVA!");
        } else {
            if (!dao.update(matriculaAgendamento.getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR SERVIÇO PESSOA!");
                return;
            }

            if (!dao.update(matriculaAgendamento)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR MATRÍCULA SEGURO!");
                return;
            }

            MatriculaAgendamentoFinanceiro ma = (MatriculaAgendamentoFinanceiro) new Dao().find(matriculaAgendamento);

            String save_log
                    = "ID Seguro: " + matriculaAgendamento.getId() + " \n "
                    + "Pessoa: " + matriculaAgendamento.getServicoPessoa().getPessoa().getDocumento() + " : " + matriculaAgendamento.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Float(matriculaAgendamento.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + matriculaAgendamento.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + matriculaAgendamento.getServicoPessoa().isDescontoFolha();

            String update_log
                    = "ID Seguro: " + ma.getId() + " \n "
                    + "Pessoa: " + ma.getServicoPessoa().getPessoa().getDocumento() + " : " + ma.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + ma.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Float(ma.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + ma.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + ma.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + ma.getServicoPessoa().isDescontoFolha();

            logs.update(save_log, update_log);

            GenericaMensagem.info("SUCESSO", "MATRÍCULA SEGURO ATUALIZADA!");
        }
        
        dao.commit();
        loadListaServicoPessoa();
    }

    public void excluir() {
        Dao dao = new Dao();

        if (matriculaAgendamento.getId() != -1) {
            dao.openTransaction();
            //ServicoPessoa sp = matriculaSeguro.getServicoPessoa();

            if (!dao.delete(matriculaAgendamento)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao excluir Seguro!");
                return;
            }

            if (!dao.delete(matriculaAgendamento.getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao excluir Serviço Pessoa!");
                return;
            }

            dao.commit();
            novo();
            GenericaMensagem.info("Sucesso", "Seguro Excluído!");
        }
    }

    public void novo() {
        GenericaSessao.put("matriculaSeguroBean", new MatriculaAgendamentoFinanceiroBean());
    }

    public String editar(MatriculaAgendamentoFinanceiro ma) {
        matriculaAgendamento = ma;
        for (int i = 0; i < listaServicos.size(); i++) {
            if (matriculaAgendamento.getServicoPessoa().getServicos().getId() == Integer.valueOf(listaServicos.get(i).getDescription())) {
                indexServicos = i;
            }
        }

        valor = matriculaAgendamento.getServicoPessoa().getNrValorFixo();

        calculoPercentualDesconto();
        loadListaServicoPessoa();

        GenericaSessao.put("linkClicado", true);
        return "matriculaSeguro";
    }

    public void calculoPercentualDesconto() {
        if (valor > 0) {
            if (matriculaAgendamento.getServicoPessoa().getNrDesconto() > 100) {
                matriculaAgendamento.getServicoPessoa().setNrDesconto(100);
            }
            desconto = valor - Moeda.valorDoPercentual(valor, matriculaAgendamento.getServicoPessoa().getNrDesconto());
            valorTotal = Moeda.subtracaoValores(valor, desconto);
        } else {
            valor = (float) 0;
            desconto = (float) 0;
            valorTotal = (float) 0;
            matriculaAgendamento.getServicoPessoa().setNrDesconto(0);
        }
    }

    public void calculoValorDesconto() {
        if (valor > 0) {
            if (desconto <= valor) {
                matriculaAgendamento.getServicoPessoa().setNrDesconto(Moeda.percentualDoValor(valor, desconto));
            } else {
                calculoPercentualDesconto();
            }
            valorTotal = Moeda.subtracaoValores(valor, desconto);
        } else {
            valor = (float) 0;
            desconto = (float) 0;
            valorTotal = (float) 0;
            matriculaAgendamento.getServicoPessoa().setNrDesconto(0);
        }
    }

    public void removerPessoa() {
        matriculaAgendamento.getServicoPessoa().setPessoa(new Pessoa());
    }

    public Integer getIndexServicos() {
        return indexServicos;
    }

    public void setIndexServicos(Integer indexServicos) {
        this.indexServicos = indexServicos;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public void setListaServicos(List<SelectItem> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public Float getValor() {
        return valor;
    }

    public void setValor(Float valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Float(valor);
    }

    public void setValorString(String valorString) {
        this.valor = Moeda.converteUS$(valorString);
    }

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }

    public String getDescontoString() {
        return Moeda.converteR$Float(desconto);
    }

    public void setDescontoString(String descontoString) {
        this.desconto = Moeda.converteUS$(descontoString);
    }

    public Float getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorTotalString() {
        return Moeda.converteR$Float(valorTotal);
    }

    public void setValorTotalString(String valorTotalString) {
        this.valorTotal = Moeda.converteUS$(valorTotalString);
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public MatriculaAgendamentoFinanceiro getMatriculaAgendamento() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            matriculaAgendamento.getServicoPessoa().setPessoa(((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa());
        }
        return matriculaAgendamento;
    }

    public void setMatriculaAgendamento(MatriculaAgendamentoFinanceiro matriculaAgendamento) {
        this.matriculaAgendamento = matriculaAgendamento;
    }

    public List<ServicoPessoa> getListaServicoPessoa() {
        return listaServicoPessoa;
    }

    public void setListaServicoPessoa(List<ServicoPessoa> listaServicoPessoa) {
        this.listaServicoPessoa = listaServicoPessoa;
    }

}
