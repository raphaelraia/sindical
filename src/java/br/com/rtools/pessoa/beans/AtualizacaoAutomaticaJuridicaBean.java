/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.pessoa.beans;

import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.dao.AtualizacaoAutomaticaJuridicaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ProcessoAutomatico;
import br.com.rtools.sistema.dao.ProcessoAutomaticoDao;
import br.com.rtools.thread.AtualizarJuridicaThread;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class AtualizacaoAutomaticaJuridicaBean implements Serializable {

    //private ProcessoAutomatico processoAutomatico;
    private Boolean processoIniciado = false;
    private List<Juridica> listaJuridica = new ArrayList();

    private Boolean inadimplentes = true;
    private Boolean cadastradosMais = true;
    private Boolean servicosArrecadacao = true;
    private Boolean empresasAtivas = true;
    private Boolean naoPagaram = true;
    
    private String ultimaDataSindical = "";

    public AtualizacaoAutomaticaJuridicaBean() {
        loadListaJuridica();
        
        ultimaDataSindical = new AtualizacaoAutomaticaJuridicaDao().pesquisaUltimaDataSindical();
    }

    public final void loadListaJuridica() {
        listaJuridica.clear();

        listaJuridica = new AtualizacaoAutomaticaJuridicaDao().listaJuridicaParaAtualizacao(inadimplentes, cadastradosMais, servicosArrecadacao, empresasAtivas, naoPagaram);
        
//        for (int i = 0; i < 10; i++) {
//            listaJuridica.addAll(new AtualizacaoAutomaticaJuridicaDao().listaJuridicaParaAtualizacao(inadimplentes, cadastradosMais, servicosArrecadacao, empresasAtivas, naoPagaram));
//        }
    }

    public void iniciar() {
        try {
            if (!validaInicio()) {
                return;
            }

            //List<Juridica> list = new ArrayList();
//            listaJuridica.clear();
//            for (int i = 0; i < 20; i++) {
//                listaJuridica.add((Juridica) new Dao().find(new Juridica(), 1));
//            }
            //String query = "SELECT j.* FROM pes_juridica j WHERE j.id = 21667"; // Object Juridica
            new AtualizarJuridicaThread(listaJuridica).runDebug();

            // THREAD NÃO FUNCIONA COM SESSÃO
            // new AtualizarJuridicaThread(list).run();
            // OU
//             ThreadExecute thread = new ThreadExecute(new AtualizarJuridicaThread(list));
//             Thread t = new Thread(thread);
//             t.start();
            processoIniciado = false;
            loadListaJuridica();
            GenericaMensagem.info("SUCESSO", "ATUALIZAÇÃO CONCLUÍDA!");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void iniciarConfirmado() {
        if (!validaInicio()) {
            return;
        }

        processoIniciado = true;
    }

    public Boolean validaInicio() {
        ProcessoAutomatico pa = new ProcessoAutomaticoDao().pesquisarProcesso("atualizar_juridica", Usuario.getUsuario().getId());

        if (pa.getId() != -1) {
            GenericaMensagem.info("SUCESSO", "PROCESSO JÁ INICIADO, AGUARDE O TÉRMINO!");
            return false;
        }

        if (listaJuridica.isEmpty()) {
            GenericaMensagem.warn("ATENÇÃO", "LISTA DE EMPRESAS VAZIA!");
            return false;
        }

        return true;
    }

    public Boolean getProcessoIniciado() {
        return processoIniciado;
    }

    public void setProcessoIniciado(Boolean processoIniciado) {
        this.processoIniciado = processoIniciado;
    }

    public List<Juridica> getListaJuridica() {
        return listaJuridica;
    }

    public void setListaJuridica(List<Juridica> listaJuridica) {
        this.listaJuridica = listaJuridica;
    }

    public Boolean getInadimplentes() {
        return inadimplentes;
    }

    public void setInadimplentes(Boolean inadimplentes) {
        this.inadimplentes = inadimplentes;
    }

    public Boolean getCadastradosMais() {
        return cadastradosMais;
    }

    public void setCadastradosMais(Boolean cadastradosMais) {
        this.cadastradosMais = cadastradosMais;
    }

    public Boolean getServicosArrecadacao() {
        return servicosArrecadacao;
    }

    public void setServicosArrecadacao(Boolean servicosArrecadacao) {
        this.servicosArrecadacao = servicosArrecadacao;
    }

    public Boolean getEmpresasAtivas() {
        return empresasAtivas;
    }

    public void setEmpresasAtivas(Boolean empresasAtivas) {
        this.empresasAtivas = empresasAtivas;
    }

    public Boolean getNaoPagaram() {
        return naoPagaram;
    }

    public void setNaoPagaram(Boolean naoPagaram) {
        this.naoPagaram = naoPagaram;
    }

    public String getUltimaDataSindical() {
        return ultimaDataSindical;
    }

    public void setUltimaDataSindical(String ultimaDataSindical) {
        this.ultimaDataSindical = ultimaDataSindical;
    }
}
