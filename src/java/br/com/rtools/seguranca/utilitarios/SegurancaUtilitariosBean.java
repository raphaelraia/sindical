package br.com.rtools.seguranca.utilitarios;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Sessions;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@ViewScoped
public class SegurancaUtilitariosBean implements Serializable {

    private Registro registro;
    private String mensagem;
    private String paginaBloqueada;

    @PostConstruct
    public void init() {
        registro = new Registro();
        mensagem = "";
        paginaBloqueada = "";
    }

    public String verPaginaBloqueada() {
        GenericaSessao.put("linkClicado", true);
        try {
            return ((ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean")).pagina(paginaBloqueada);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public boolean getExisteBloqueio() {
        HttpServletRequest paginaRequerida = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String paginaAtual = paginaRequerida.getRequestURI().substring(paginaRequerida.getRequestURI().lastIndexOf("/") + 1, paginaRequerida.getRequestURI().lastIndexOf("."));

        if (paginaAtual.equals("gerarBoleto") || paginaAtual.equals("servicos")) {
            FinanceiroDao db = new FinanceiroDao();
            List<Vector> listaServicoSemCobranca = db.listaServicosSemCobranca();
            if (!listaServicoSemCobranca.isEmpty()) {
                mensagem = "Definir Conta Cobrança para os seguintes Serviços <br /> <br />";
                for (Vector linha : listaServicoSemCobranca) {
                    mensagem += "Serviço / Tipo: " + linha.get(1).toString() + " - " + linha.get(3).toString() + " <br /> ";
                }

                paginaBloqueada = "servicoContaCobranca";
                return true;
            }
        }
        return false;
    }

    public boolean getExisteMacFilial() {
        return MacFilial.getAcessoFilial().getId() != -1;
    }

    public boolean getExisteMacFilialComCaixa() {
        MacFilial macFilial = MacFilial.getAcessoFilial();
        if (macFilial.getId() != -1) {
            if (!macFilial.getCaixaOperador()) {
                if (macFilial.getCaixa() != null) {
                    return true;
                }
            } else {
                FinanceiroDao db = new FinanceiroDao();
                Caixa caixa = db.pesquisaCaixaUsuario(Usuario.getUsuario().getId(), macFilial.getFilial().getId());

                if (caixa != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public Usuario getSessaoUsuario() {
        return (Usuario) GenericaSessao.getObject("sessaoUsuario");
    }

    public Registro getRegistro() {
        if (registro.getId() == -1) {
            registro = (Registro) new Dao().find(new Registro(), 1);
            if (registro == null) {
                registro = new Registro();
            }
        }
        return registro;
    }

    public Rotina getRotina() {
        return new Rotina().get();
    }

    public MacFilial getMacFilial() {
        return MacFilial.getAcessoFilial();
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void enableWebCam() {
        if (!Sessions.exists("acessoFilial")) {
            Messages.warn("Sistema", "Nenhum Mac Configurado!");
            return;
        }
        MacFilial mc = (MacFilial) Sessions.getObject("acessoFilial");
        if (mc.getWebcam()) {
            mc.setWebcam(false);
            Messages.info("Sucesso", "Webcam Desabilitada");
            new Dao().update(mc, true);
        } else {
            mc.setWebcam(true);
            Messages.info("Sucesso", "Webcam Habilitada");
            new Dao().update(mc, true);
        }
        Sessions.remove("acessoFilial");
        Sessions.put("acessoFilial", mc);
    }

}
