/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.MatriculaAgendamentoFinanceiro;
import br.com.rtools.associativo.dao.MatriculaAgendamentoFinanceiroDao;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
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
    private Double valor = (double) 0;
    private Double desconto = (double) 0;
    private Double valorTotal = (double) 0;
    private List<LinhaServicoPessoa> listaServicoPessoa = new ArrayList();

    private Boolean desabilitaValor = false;

    public MatriculaAgendamentoFinanceiroBean() {
        loadListaServicos();

        GenericaSessao.remove("fisicaPesquisa");
    }

    public final void loadValorServico() {
        desabilitaValor = false;

        valor = (double) 0;
        valorTotal = (double) 0;
        desconto = (double) 0;

        if (listaServicos.get(indexServicos).getDescription() == null) {
            GenericaMensagem.error("ATENÇÃO", "CADASTRE SERVIÇOS PARA ESTA ROTINA!");
            return;
        }

        Servicos se = (Servicos) new Dao().find(new Servicos(), Integer.valueOf(listaServicos.get(indexServicos).getDescription()));

        if (se != null && matriculaAgendamento.getServicoPessoa().getPessoa().getId() != -1) {
            if (matriculaAgendamento.getServicoPessoa().getPessoa().getSocios().getId() != -1) {
                valor = new FunctionsDao().valorServico(matriculaAgendamento.getServicoPessoa().getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, matriculaAgendamento.getServicoPessoa().getPessoa().getSocios().getMatriculaSocios().getCategoria().getId());
                valorTotal = valor;
            } else {
                valor = new FunctionsDao().valorServico(matriculaAgendamento.getServicoPessoa().getPessoa().getId(), se.getId(), DataHoje.dataHoje(), 0, null);
                valorTotal = valor;
            }
        }

        if (valor > 0) {
            desabilitaValor = true;
        }
    }

    public final void loadListaServicoPessoa() {
        listaServicoPessoa.clear();

        List<Object> result = new MatriculaAgendamentoFinanceiroDao().listaServicoPessoaMatricula(matriculaAgendamento.getServicoPessoa().getPessoa().getId());

        Dao dao = new Dao();
        for (Object ob : result) {
            List linha = (List) ob;
            ServicoPessoa sp = (ServicoPessoa) dao.find(new ServicoPessoa(), Integer.valueOf(linha.get(0).toString()));
            MatriculaAgendamentoFinanceiro ma = (linha.get(1) != null) ? (MatriculaAgendamentoFinanceiro) dao.find(new MatriculaAgendamentoFinanceiro(), Integer.valueOf(linha.get(1).toString())) : null;

            listaServicoPessoa.add(
                    new LinhaServicoPessoa(
                            sp,
                            ma
                    )
            );
        }
    }

    public final void loadListaServicos() {
        listaServicos.clear();
        indexServicos = 0;

        ServicosDao db = new ServicosDao();
        List<Servicos> select = db.pesquisaTodos(427);
        if (!select.isEmpty()) {
            for (int i = 0; i < select.size(); i++) {
                listaServicos.add(
                        new SelectItem(
                                i,
                                select.get(i).getDescricao(),
                                Integer.toString(select.get(i).getId())
                        )
                );
            }
        } else {
            listaServicos.add(new SelectItem(0, "NENHUM SERVIÇO ADICIONADO PARA ESTA ROTINA", null));
        }
    }

    public void salvar() {
        if (matriculaAgendamento.getServicoPessoa().getPessoa().getId() == -1) {
            GenericaMensagem.error("ATENÇÃO", "PESQUISE UMA PESSOA PARA SALVAR!");
            return;
        }

        if (listaServicos.get(indexServicos).getDescription() == null) {
            GenericaMensagem.error("ATENÇÃO", "ADICIONE SERVIÇOS PARA ESTA ROTINA!");
            return;
        }

        Pessoa pessoaCobranca;
        if (matriculaAgendamento.getServicoPessoa().getPessoa().getSocios().getId() != -1) {
            pessoaCobranca = new FunctionsDao().titularDaPessoa(matriculaAgendamento.getServicoPessoa().getPessoa().getId());
        } else {
            if (matriculaAgendamento.getServicoPessoa().getPessoa().getFisica().getIdade() < 16) {
                GenericaMensagem.error("ATENÇÃO", "Menor de 16 anos não pode ser responsável!");
                return;
            }

            pessoaCobranca = matriculaAgendamento.getServicoPessoa().getPessoa();
        }

        Dao dao = new Dao();
        NovoLog logs = new NovoLog();

        matriculaAgendamento.getServicoPessoa().setServicos((Servicos) dao.find(new Servicos(), Integer.valueOf(listaServicos.get(indexServicos).getDescription())));
        matriculaAgendamento.getServicoPessoa().setNrDiaVencimento(Registro.get().getFinDiaVencimentoCobranca());
        if (!desabilitaValor) {
            matriculaAgendamento.getServicoPessoa().setNrValorFixo(valor);
        } else {
            matriculaAgendamento.getServicoPessoa().setNrValorFixo(0);
        }
        matriculaAgendamento.getServicoPessoa().setCobranca(pessoaCobranca);

        dao.openTransaction();

        if (matriculaAgendamento.getId() == -1) {
            if (new MatriculaAgendamentoFinanceiroDao().pesquisaMatriculaAgendamentoPessoaAtiva(matriculaAgendamento.getServicoPessoa().getPessoa().getId(), matriculaAgendamento.getServicoPessoa().getServicos().getId()) != null) {
                GenericaMensagem.error("ATENÇÃO", "Esta pessoa já tem esse serviço ativo!");
                return;
            }

            matriculaAgendamento.getServicoPessoa().setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 13));
            //matriculaAgendamento.getServicoPessoa().setCobranca(null);
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
                GenericaMensagem.error("ATENÇÃO", "ERRO AO SALVAR MATRÍCULA AGENDAMENTO!");
                return;
            }

            String save_log
                    = "ID Agendamento Financeiro: " + matriculaAgendamento.getId() + " \n "
                    + "Pessoa: " + matriculaAgendamento.getServicoPessoa().getPessoa().getDocumento() + " : " + matriculaAgendamento.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Double(matriculaAgendamento.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + matriculaAgendamento.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + matriculaAgendamento.getServicoPessoa().isDescontoFolha();

            logs.save(save_log);

            GenericaMensagem.info("SUCESSO", "MATRÍCULA AGENDAMENTO SALVA!");
        } else {
            if (!dao.update(matriculaAgendamento.getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR SERVIÇO PESSOA!");
                return;
            }

            if (!dao.update(matriculaAgendamento)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR MATRÍCULA AGENDAMENTO!");
                return;
            }

            MatriculaAgendamentoFinanceiro ma = (MatriculaAgendamentoFinanceiro) new Dao().find(matriculaAgendamento);

            String save_log
                    = "ID Agendamento: " + matriculaAgendamento.getId() + " \n "
                    + "Pessoa: " + matriculaAgendamento.getServicoPessoa().getPessoa().getDocumento() + " : " + matriculaAgendamento.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Double(matriculaAgendamento.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + matriculaAgendamento.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + matriculaAgendamento.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + matriculaAgendamento.getServicoPessoa().isDescontoFolha();

            String update_log
                    = "ID Agendamento: " + ma.getId() + " \n "
                    + "Pessoa: " + ma.getServicoPessoa().getPessoa().getDocumento() + " : " + ma.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + ma.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Double(ma.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + ma.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + ma.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + ma.getServicoPessoa().isDescontoFolha();

            logs.update(save_log, update_log);

            GenericaMensagem.info("SUCESSO", "MATRÍCULA AGENDAMENTO ATUALIZADA!");
        }

        dao.commit();

        novo();
        loadListaServicoPessoa();
    }

    public void excluir() {
        Dao dao = new Dao();

        if (matriculaAgendamento.getId() != -1) {
            dao.openTransaction();

            if (!dao.delete(matriculaAgendamento)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao excluir Agendamento Financeiro!");
                return;
            }

            if (!dao.delete(matriculaAgendamento.getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao excluir Serviço Pessoa!");
                return;
            }

            dao.commit();

            novo();
            loadListaServicoPessoa();

            GenericaMensagem.info("Sucesso", "Agendamento Financeiro Excluído!");
        }
    }

    public void novo() {
        //GenericaSessao.put("matriculaAgendamentoFinanceiroBean", new MatriculaAgendamentoFinanceiroBean());
        Pessoa p = matriculaAgendamento.getServicoPessoa().getPessoa();
        matriculaAgendamento = new MatriculaAgendamentoFinanceiro();
        indexServicos = 0;
        valor = (double) 0;
        desconto = (double) 0;
        valorTotal = (double) 0;

        matriculaAgendamento.getServicoPessoa().setPessoa(p);
    }

    public void editar(LinhaServicoPessoa lsp) {
        matriculaAgendamento = lsp.getMatriculaAgendamentoFinanceiro();
        for (int i = 0; i < listaServicos.size(); i++) {
            if (matriculaAgendamento.getServicoPessoa().getServicos().getId() == Integer.valueOf(listaServicos.get(i).getDescription())) {
                indexServicos = i;
            }
        }

        valor = matriculaAgendamento.getServicoPessoa().getNrValorFixo();

        calculoPercentualDesconto();
    }

    public void calculoPercentualDesconto() {
        if (valor > 0) {
            if (matriculaAgendamento.getServicoPessoa().getNrDesconto() > 100) {
                matriculaAgendamento.getServicoPessoa().setNrDesconto(100);
            }
            desconto = valor - Moeda.valorDoPercentual(valor, matriculaAgendamento.getServicoPessoa().getNrDesconto());
            valorTotal = Moeda.subtracao(valor, desconto);
        } else {
            valor = (double) 0;
            desconto = (double) 0;
            valorTotal = (double) 0;
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
            valorTotal = Moeda.subtracao(valor, desconto);
        } else {
            valor = (double) 0;
            desconto = (double) 0;
            valorTotal = (double) 0;
            matriculaAgendamento.getServicoPessoa().setNrDesconto(0);
        }
    }

    public void removerPessoa() {
//        matriculaAgendamento.getServicoPessoa().setPessoa(new Pessoa());
//        
//        loadListaServicoPessoa();
        GenericaSessao.put("matriculaAgendamentoFinanceiroBean", new MatriculaAgendamentoFinanceiroBean());
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

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getValorString() {
        return Moeda.converteR$Double(valor);
    }

    public void setValorString(String valorString) {
        this.valor = Moeda.converteUS$(valorString);
    }

    public Double getDesconto() {
        return desconto;
    }

    public void setDesconto(Double desconto) {
        this.desconto = desconto;
    }

    public String getDescontoString() {
        return Moeda.converteR$Double(desconto);
    }

    public void setDescontoString(String descontoString) {
        this.desconto = Moeda.converteUS$(descontoString);
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getValorTotalString() {
        return Moeda.converteR$Double(valorTotal);
    }

    public void setValorTotalString(String valorTotalString) {
        this.valorTotal = Moeda.converteUS$(valorTotalString);
    }

    public MatriculaAgendamentoFinanceiro getMatriculaAgendamento() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            matriculaAgendamento.getServicoPessoa().setPessoa(((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa());
            loadListaServicoPessoa();

            loadValorServico();
        }
        return matriculaAgendamento;
    }

    public void setMatriculaAgendamento(MatriculaAgendamentoFinanceiro matriculaAgendamento) {
        this.matriculaAgendamento = matriculaAgendamento;
    }

    public List<LinhaServicoPessoa> getListaServicoPessoa() {
        return listaServicoPessoa;
    }

    public void setListaServicoPessoa(List<LinhaServicoPessoa> listaServicoPessoa) {
        this.listaServicoPessoa = listaServicoPessoa;
    }

    public Boolean getDesabilitaValor() {
        return desabilitaValor;
    }

    public void setDesabilitaValor(Boolean desabilitaValor) {
        this.desabilitaValor = desabilitaValor;
    }

    public class LinhaServicoPessoa {

        private ServicoPessoa servicoPessoa;
        private MatriculaAgendamentoFinanceiro matriculaAgendamentoFinanceiro;

        public LinhaServicoPessoa(ServicoPessoa servicoPessoa, MatriculaAgendamentoFinanceiro matriculaAgendamentoFinanceiro) {
            this.servicoPessoa = servicoPessoa;
            this.matriculaAgendamentoFinanceiro = matriculaAgendamentoFinanceiro;
        }

        public ServicoPessoa getServicoPessoa() {
            return servicoPessoa;
        }

        public void setServicoPessoa(ServicoPessoa servicoPessoa) {
            this.servicoPessoa = servicoPessoa;
        }

        public MatriculaAgendamentoFinanceiro getMatriculaAgendamentoFinanceiro() {
            return matriculaAgendamentoFinanceiro;
        }

        public void setMatriculaAgendamentoFinanceiro(MatriculaAgendamentoFinanceiro matriculaAgendamentoFinanceiro) {
            this.matriculaAgendamentoFinanceiro = matriculaAgendamentoFinanceiro;
        }

    }

}
