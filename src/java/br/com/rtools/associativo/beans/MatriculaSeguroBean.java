/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.MatriculaSeguro;
import br.com.rtools.associativo.dao.MatriculaSeguroDao;
import br.com.rtools.financeiro.FTipoDocumento;
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
public class MatriculaSeguroBean implements Serializable {

    private MatriculaSeguro matriculaSeguro = new MatriculaSeguro();
    private Integer indexServicos = 0;
    private List<SelectItem> listaServicos = new ArrayList();
    private Float valor = (float) 0;
    private Float desconto = (float) 0;
    private Float valorTotal = (float) 0;
    private List<MatriculaSeguro> listaMatriculaSeguro = new ArrayList();

    private String descricaoPesquisa = "";
    private String tipoPesquisa = "nome";

    public MatriculaSeguroBean() {
        loadListaServicos();
        loadListaMatriculaSeguro();
        
        GenericaSessao.remove("fisicaPesquisa");
    }

    public String getMaskPesquisa() {
        switch (tipoPesquisa) {
            case "nome":
                return "";
            case "cpf":
                return "999.999.999-99";
        }
        return "";
    }

    public final void loadListaMatriculaSeguro(String por) {
        listaMatriculaSeguro.clear();

        listaMatriculaSeguro = new MatriculaSeguroDao().listaMatriculaSeguro(descricaoPesquisa, null, tipoPesquisa, por);
    }

    public final void loadListaMatriculaSeguro() {
        listaMatriculaSeguro.clear();

        if (!listaServicos.isEmpty()){
            listaMatriculaSeguro = new MatriculaSeguroDao().listaMatriculaSeguro("", Integer.valueOf(listaServicos.get(indexServicos).getDescription()), "servico", "");
        }
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
        if (matriculaSeguro.getServicoPessoa().getPessoa().getId() == -1) {
            GenericaMensagem.error("ATENÇÃO", "PESQUISE UMA PESSOA PARA SALVAR!");
            return;
        }

        Pessoa pessoaCobranca;
        if (matriculaSeguro.getServicoPessoa().getPessoa().getSocios().getId() != -1) {
            pessoaCobranca = new FunctionsDao().titularDaPessoa(matriculaSeguro.getServicoPessoa().getPessoa().getId());
        } else {
            if (matriculaSeguro.getServicoPessoa().getPessoa().getFisica().getIdade() < 16){
                GenericaMensagem.error("ATENÇÃO", "Menor de 16 anos não pode ser responsável!");
                return;
            }
            
            pessoaCobranca = matriculaSeguro.getServicoPessoa().getPessoa();
        }

        Dao dao = new Dao();
        NovoLog logs = new NovoLog();

        matriculaSeguro.getServicoPessoa().setServicos((Servicos) dao.find(new Servicos(), Integer.valueOf(listaServicos.get(indexServicos).getDescription())));
        matriculaSeguro.getServicoPessoa().setNrDiaVencimento(Registro.get().getFinDiaVencimentoCobranca());
        matriculaSeguro.getServicoPessoa().setNrValorFixo(valor);
        matriculaSeguro.getServicoPessoa().setCobranca(pessoaCobranca);

        dao.openTransaction();

        if (matriculaSeguro.getId() == -1) {
            if (new MatriculaSeguroDao().pesquisaMatriculaSeguroPessoaAtiva(matriculaSeguro.getServicoPessoa().getPessoa().getId(), matriculaSeguro.getServicoPessoa().getServicos().getId()) != null) {
                GenericaMensagem.error("ATENÇÃO", "Esta pessoa já tem esse serviço ativo!");
                return;
            }

            matriculaSeguro.getServicoPessoa().setTipoDocumento((FTipoDocumento) dao.find(new FTipoDocumento(), 13));
            matriculaSeguro.getServicoPessoa().setCobranca(null);
            matriculaSeguro.getServicoPessoa().setAtivo(true);
            matriculaSeguro.getServicoPessoa().setBanco(true);
            matriculaSeguro.getServicoPessoa().setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
            matriculaSeguro.getServicoPessoa().setEvt(null);
            matriculaSeguro.getServicoPessoa().setParceiro(null);

            if (!dao.save(matriculaSeguro.getServicoPessoa())) {
                GenericaMensagem.error("ATENÇÃO", "ERRO AO SALVAR SERVIÇO PESSOA!");
                dao.rollback();
                return;
            }

            if (!dao.save(matriculaSeguro)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO SALVAR MATRÍCULA SEGURO!");
                return;
            }

            String save_log
                    = "ID Seguro: " + matriculaSeguro.getId() + " \n "
                    + "Pessoa: " + matriculaSeguro.getServicoPessoa().getPessoa().getDocumento() + " : " + matriculaSeguro.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + matriculaSeguro.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Float(matriculaSeguro.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + matriculaSeguro.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + matriculaSeguro.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + matriculaSeguro.getServicoPessoa().isDescontoFolha();

            logs.save(save_log);

            GenericaMensagem.info("SUCESSO", "MATRÍCULA SEGURO SALVA!");
        } else {
            if (!dao.update(matriculaSeguro.getServicoPessoa())) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR SERVIÇO PESSOA!");
                return;
            }

            if (!dao.update(matriculaSeguro)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "ERRO AO ATUALIZAR MATRÍCULA SEGURO!");
                return;
            }

            MatriculaSeguro ms = (MatriculaSeguro) new Dao().find(matriculaSeguro);

            String save_log
                    = "ID Seguro: " + matriculaSeguro.getId() + " \n "
                    + "Pessoa: " + matriculaSeguro.getServicoPessoa().getPessoa().getDocumento() + " : " + matriculaSeguro.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + matriculaSeguro.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Float(matriculaSeguro.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + matriculaSeguro.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + matriculaSeguro.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + matriculaSeguro.getServicoPessoa().isDescontoFolha();

            String update_log
                    = "ID Seguro: " + ms.getId() + " \n "
                    + "Pessoa: " + ms.getServicoPessoa().getPessoa().getDocumento() + " : " + ms.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Serviço: " + ms.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Valor: " + Moeda.converteR$Float(ms.getServicoPessoa().getNrValorFixo()) + " \n "
                    + "Desconto R$: " + getDescontoString() + " \n "
                    + "Desconto %: " + ms.getServicoPessoa().getNrDescontoString() + " \n "
                    + "Dia Vencimento: " + ms.getServicoPessoa().getNrDiaVencimento() + " \n "
                    + "Desconto Folha: " + ms.getServicoPessoa().isDescontoFolha();

            logs.update(save_log, update_log);

            GenericaMensagem.info("SUCESSO", "MATRÍCULA SEGURO ATUALIZADA!");
        }
        dao.commit();
        loadListaMatriculaSeguro();
    }

    public void excluir() {
        Dao dao = new Dao();

        if (matriculaSeguro.getId() != -1) {
            dao.openTransaction();
            //ServicoPessoa sp = matriculaSeguro.getServicoPessoa();

            if (!dao.delete(matriculaSeguro)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao excluir Seguro!");
                return;
            }

            if (!dao.delete(matriculaSeguro.getServicoPessoa())) {
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
        GenericaSessao.put("matriculaSeguroBean", new MatriculaSeguroBean());
    }

    public String editar(MatriculaSeguro ms) {
        matriculaSeguro = ms;
        for (int i = 0; i < listaServicos.size(); i++) {
            if (matriculaSeguro.getServicoPessoa().getServicos().getId() == Integer.valueOf(listaServicos.get(i).getDescription())) {
                indexServicos = i;
            }
        }

        valor = matriculaSeguro.getServicoPessoa().getNrValorFixo();

        calculoPercentualDesconto();
        loadListaMatriculaSeguro();

        GenericaSessao.put("linkClicado", true);
        return "matriculaSeguro";
    }

    public void calculoPercentualDesconto() {
        if (valor > 0) {
            if (matriculaSeguro.getServicoPessoa().getNrDesconto() > 100) {
                matriculaSeguro.getServicoPessoa().setNrDesconto(100);
            }
            desconto = valor - Moeda.valorDoPercentual(valor, matriculaSeguro.getServicoPessoa().getNrDesconto());
            valorTotal = Moeda.subtracaoValores(valor, desconto);
        } else {
            valor = (float) 0;
            desconto = (float) 0;
            valorTotal = (float) 0;
            matriculaSeguro.getServicoPessoa().setNrDesconto(0);
        }
    }

    public void calculoValorDesconto() {
        if (valor > 0) {
            if (desconto <= valor) {
                matriculaSeguro.getServicoPessoa().setNrDesconto(Moeda.percentualDoValor(valor, desconto));
            } else {
                calculoPercentualDesconto();
            }
            valorTotal = Moeda.subtracaoValores(valor, desconto);
        } else {
            valor = (float) 0;
            desconto = (float) 0;
            valorTotal = (float) 0;
            matriculaSeguro.getServicoPessoa().setNrDesconto(0);
        }
    }

    public void removerPessoa() {
        matriculaSeguro.getServicoPessoa().setPessoa(new Pessoa());
    }

    public MatriculaSeguro getMatriculaSeguro() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            matriculaSeguro.getServicoPessoa().setPessoa(((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa());
        }
        return matriculaSeguro;
    }

    public void setMatriculaSeguro(MatriculaSeguro matriculaSeguro) {
        this.matriculaSeguro = matriculaSeguro;
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

    public List<MatriculaSeguro> getListaMatriculaSeguro() {
        return listaMatriculaSeguro;
    }

    public void setListaMatriculaSeguro(List<MatriculaSeguro> listaMatriculaSeguro) {
        this.listaMatriculaSeguro = listaMatriculaSeguro;
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

}
