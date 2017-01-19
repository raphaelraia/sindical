package br.com.rtools.arrecadacao.beans;

import br.com.rtools.financeiro.ImpressoraCheque;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ConfiguracaoImpressoraChequeBean implements Serializable {

    private ImpressoraCheque impressoraCheque;
    private List<ImpressoraCheque> listImpressoraCheque;

    @PostConstruct
    public void init() {
        impressoraCheque = new ImpressoraCheque();
        impressoraCheque.setMac(MacFilial.getAcessoFilial().getMac());
        loadListImpressoraCheque();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoImpressoraChequeBean");
    }
    
    public void clear() {
        GenericaSessao.remove("configuracaoImpressoraChequeBean");
    }

    public void loadListImpressoraCheque() {
        listImpressoraCheque = new ArrayList();
        listImpressoraCheque = new Dao().list(new ImpressoraCheque());
    }

    public void edit(ImpressoraCheque ic) {
        impressoraCheque = (ImpressoraCheque) new Dao().rebind(ic);
    }

    public void remove(ImpressoraCheque ic) {
        if (new Dao().delete(ic, true)) {
            GenericaMensagem.info("Sucesso", "Impressora removida");
            loadListImpressoraCheque();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover este registro!");
        }
        impressoraCheque = new ImpressoraCheque();
        loadListImpressoraCheque();
    }

    public void save() {
        if (impressoraCheque.getApelido().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR APELIDO DA IMPRESSORA!");
            return;
        }
        if (impressoraCheque.getImpressora() == 0) {
            GenericaMensagem.warn("Validação", "INFORMAR O NÚMERO IMPRESSORA!");
            return;
        }
        if (impressoraCheque.getMac().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR MAC DO DISPOSITIVO DA IMPRESSORA!");
            return;
        }
        if (!impressoraCheque.getMac().matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            GenericaMensagem.warn("Validação", "INFORMAR UM MAC VÁLIDO!");
            return;
        }
        if (impressoraCheque.getId() == -1) {
            if (new Dao().save(impressoraCheque, true)) {
                GenericaMensagem.info("Sucesso", "Configurações Aplicadas");
                loadListImpressoraCheque();
                impressoraCheque = new ImpressoraCheque();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        } else if (new Dao().update(impressoraCheque, true)) {
            GenericaMensagem.info("Sucesso", "Configurações Aplicadas");
            loadListImpressoraCheque();
            impressoraCheque = new ImpressoraCheque();
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
        }
    }

    public ImpressoraCheque getImpressoraCheque() {
        return impressoraCheque;
    }

    public void setImpressoraCheque(ImpressoraCheque impressoraCheque) {
        this.impressoraCheque = impressoraCheque;
    }

    public List<ImpressoraCheque> getListImpressoraCheque() {
        return listImpressoraCheque;
    }

    public void setListImpressoraCheque(List<ImpressoraCheque> listImpressoraCheque) {
        this.listImpressoraCheque = listImpressoraCheque;
    }

}
