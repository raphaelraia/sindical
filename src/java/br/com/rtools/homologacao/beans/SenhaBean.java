package br.com.rtools.homologacao.beans;

import br.com.rtools.homologacao.Senha;
import br.com.rtools.homologacao.dao.SenhaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaRequisicao;
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
public class SenhaBean implements Serializable {

    private Senha senha;
    private List<Senha> listSenha;
    private MacFilial macFilial;
    private Boolean activePoll;
    private Boolean sound;
    private String tipo;

    @PostConstruct
    public void init() {
        sound = false;
        macFilial = new MacFilial();
        listSenha = new ArrayList();
        activePoll = false;
        String client = GenericaRequisicao.getParametro("client");
        String mac = GenericaRequisicao.getParametro("mac");
        tipo = GenericaRequisicao.getParametro("tipo");
        if (tipo == null || tipo.isEmpty()) {
            tipo = "MESA";
        }
        if (client != null && mac != null && !client.isEmpty() && !mac.isEmpty()) {
            if (!client.equals(GenericaSessao.getString("sessaoCliente"))) {
                GenericaSessao.put("sessaoCliente", client);
            }
            macFilial = new MacFilialDao().pesquisaMac(mac);
            if (macFilial != null) {
                activePoll = true;
                loadSenha();
            }
        }
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        // GenericaSessao.remove("sessaoCliente");
        // GenericaSessao.remove("senhaBean");
    }

    public void loadSenha() {
        if (activePoll) {
            SenhaDao sd = new SenhaDao();
            if (!listSenha.isEmpty()) {
                List<Senha> list = sd.findRequest(macFilial.getFilial().getId());
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getMesa() > 0 && !list.get(i).getHoraChamada().isEmpty() && !DataHoje.converteData(list.get(i).getDtVerificada()).equals("01/01/1900")) {
                            sound = true;
                            break;
                        }
                    }
                    listSenha.clear();
                }
            }
            if (listSenha.isEmpty()) {
                listSenha = sd.sequence(macFilial.getFilial().getId(), 4);
            }
        }
    }

    public Senha getSenha() {
        return senha;
    }

    public void setSenha(Senha senha) {
        this.senha = senha;
    }

    public List<Senha> getListSenha() {
        return listSenha;
    }

    public void setListSenha(List<Senha> listSenha) {
        this.listSenha = listSenha;
    }

    public Senha getUltimaSenha() {
        if (!listSenha.isEmpty()) {
            return listSenha.get(0);
        }
        return new Senha();
    }

    public List<Senha> getListUltimasChamadas() {
        if (!listSenha.isEmpty() && listSenha.size() > 1) {
            List<Senha> list = new ArrayList();
            for (int i = 0; i < listSenha.size(); i++) {
                if (i > 0) {
                    list.add(listSenha.get(i));
                }
            }
            return list;
        }
        return new ArrayList<>();
    }

    public Boolean getActivePoll() {
        return activePoll;
    }

    public void setActivePoll(Boolean activePoll) {
        this.activePoll = activePoll;
    }

    public String getDisabledSound() {
        this.sound = false;
        return "";
    }

    public Boolean getSound() {
        return sound;
    }

    public void setSound(Boolean sound) {
        this.sound = sound;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
