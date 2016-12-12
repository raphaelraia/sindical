/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.dao.RemessaDao;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.PF;
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
public class RemessaBean implements Serializable {

    private List<SelectItem> listaConta = new ArrayList();
    private Integer indexConta = 0;

    private List<Remessa> listaRemessa = new ArrayList();
    private List<RemessaBanco> listaRemessaBanco = new ArrayList();

    private Remessa remessa = new Remessa();
    private Plano5 contaSelecionada = new Plano5();

    private String dataEnvioArquivo = "";
    
    private RemessaBanco remessaBancoSelecionada = new RemessaBanco();

    public RemessaBean() {
        loadListaConta();
        loadListaRemessa();
    }

    public void removerMovimentoRemessa(){
        Dao dao = new Dao();
        
        dao.openTransaction();
        
        if (!dao.delete(remessaBancoSelecionada)){
            GenericaMensagem.error("Erro", "Não foi possível excluir Movimento da Remessa");
            dao.rollback();
            return;
        }
        
        GenericaMensagem.info("Sucesso", "Movimento excluído da Remessa");
        dao.commit();
        
        loadListaRemessaBanco();
    }
    
    public void removerMovimentoRemessa(RemessaBanco rb){
        remessaBancoSelecionada = rb;
    }
    
    public void salvarEnvioBanco() {
        if (dataEnvioArquivo.isEmpty()) {
            GenericaMensagem.warn("Atenção", "Digite uma data para o envio válida!");
            PF.update("formRemessa:panel_enviar_arquivo");
            return;
        }

        Dao dao = new Dao();

        dao.openTransaction();

        remessa.setDtEnvioBancoString(dataEnvioArquivo);
        remessa.setUsuarioEnvioBanco(Usuario.getUsuario());
        if (!dao.update(remessa)) {
            remessa.setDtEnvioBancoString("");
            remessa.setUsuarioEnvioBanco(null);
            
            dao.rollback();
            GenericaMensagem.error("Atenção", "Erro ao atualizar data, tente novamente!");
            PF.update("formRemessa:panel_enviar_arquivo");
            return;
        }

        dao.commit();

        List<String> list_log = new ArrayList();
        list_log.add("** Remessa Atualizada **");
        list_log.add("ID: " + remessa.getId());
        list_log.add("NOME: " + remessa.getNomeArquivo());
        list_log.add("EMISSÃO: " + remessa.getDtEmissaoString());
        list_log.add("HORA EMISSÃO: " + remessa.getHoraEmissao());
        list_log.add("ENVIO BANCO: " + remessa.getDtEnvioBancoString());
        String log_string = "";
        log_string = list_log.stream().map((string_x) -> string_x + " \n").reduce(log_string, String::concat);
        NovoLog log = new NovoLog();
        log.save(
                log_string
        );
        
        loadListaRemessa();

        PF.update("formRemessa");
        PF.closeDialog("dlg_enviar_arquivo");

        GenericaMensagem.info("Sucesso", "Data de Envio Atualizada!");
    }

    public void enviarArquivo(Remessa r) {
        remessa = r;

        dataEnvioArquivo = DataHoje.data();
    }

    public void loadListaRemessaBanco() {
        listaRemessaBanco.clear();

        listaRemessaBanco = new RemessaDao().listaRemessaBanco(remessa.getId());
    }
    
    public void visualizar(Remessa r) {
        remessa = r;

        listaRemessaBanco.clear();

        listaRemessaBanco = new RemessaDao().listaRemessaBanco(remessa.getId());
    }

    public final void loadListaConta() {
        listaConta.clear();
        indexConta = 0;

        List<Plano5> result = new RemessaDao().listaConta();

        for (int i = 0; i < result.size(); i++) {
            listaConta.add(
                    new SelectItem(
                            i,
                            result.get(i).getConta(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }

        contaSelecionada = (Plano5) new Dao().find(new Plano5(), Integer.valueOf(listaConta.get(indexConta).getDescription()));
    }

    public final void loadListaRemessa() {
        listaRemessa.clear();

        contaSelecionada = (Plano5) new Dao().find(new Plano5(), Integer.valueOf(listaConta.get(indexConta).getDescription()));

        listaRemessa = new RemessaDao().listaRemessa(contaSelecionada.getContaBanco().getId());
    }

    public List<SelectItem> getListaConta() {
        return listaConta;
    }

    public void setListaConta(List<SelectItem> listaConta) {
        this.listaConta = listaConta;
    }

    public Integer getIndexConta() {
        return indexConta;
    }

    public void setIndexConta(Integer indexConta) {
        this.indexConta = indexConta;
    }

    public List<Remessa> getListaRemessa() {
        return listaRemessa;
    }

    public void setListaRemessa(List<Remessa> listaRemessa) {
        this.listaRemessa = listaRemessa;
    }

    public Remessa getRemessa() {
        return remessa;
    }

    public void setRemessa(Remessa remessa) {
        this.remessa = remessa;
    }

    public List<RemessaBanco> getListaRemessaBanco() {
        return listaRemessaBanco;
    }

    public void setListaRemessaBanco(List<RemessaBanco> listaRemessaBanco) {
        this.listaRemessaBanco = listaRemessaBanco;
    }

    public Plano5 getContaSelecionada() {
        return contaSelecionada;
    }

    public void setContaSelecionada(Plano5 contaSelecionada) {
        this.contaSelecionada = contaSelecionada;
    }

    public String getDataEnvioArquivo() {
        return dataEnvioArquivo;
    }

    public void setDataEnvioArquivo(String dataEnvioArquivo) {
        this.dataEnvioArquivo = dataEnvioArquivo;
    }

    public RemessaBanco getRemessaBancoSelecionada() {
        return remessaBancoSelecionada;
    }

    public void setRemessaBancoSelecionada(RemessaBanco remessaBancoSelecionada) {
        this.remessaBancoSelecionada = remessaBancoSelecionada;
    }

}
