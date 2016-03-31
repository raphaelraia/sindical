package br.com.rtools.seguranca.utilitarios;

import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.db.FinanceiroDB;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
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
            FinanceiroDB db = new FinanceiroDBToplink();
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
            if (!macFilial.isCaixaOperador()) {
                if (macFilial.getCaixa() != null) {
                    return true;
                }
            } else {
                FinanceiroDB db = new FinanceiroDBToplink();
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
            registro = (Registro) new Dao().find(new Registro());
            if (registro == null) {
                registro = new Registro();
            }
        }
        return registro;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

}
