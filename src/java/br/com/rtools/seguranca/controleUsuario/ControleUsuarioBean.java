package br.com.rtools.seguranca.controleUsuario;

import br.com.rtools.seguranca.dao.UsuarioDao;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.beans.ConfiguracaoSocialBean;
import br.com.rtools.financeiro.Caixa;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.principal.DBExternal;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.UsuarioHistoricoAcesso;
import br.com.rtools.seguranca.dao.UsuarioHistoricoAcessoDao;
import br.com.rtools.sistema.ContadorAcessos;
import br.com.rtools.sistema.dao.AtalhoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Implantacao;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.json.JSONObject;

@ManagedBean
@SessionScoped
public class ControleUsuarioBean implements Serializable {

    private Usuario usuario = new Usuario();
    private Usuario usuarioSuporteTecnico = new Usuario();
    private String login = "";
    private String linkVoltar;
    private int fimURL;
    private int iniURL;
    private String paginaDestino;
    private String alerta;
    private String emailSuporteTecnico;
    private static String cliente;
    private static String bloqueiaMenu;
    private String filial;
    private String filialDep = "";
    private String msgErro = "";
    private MacFilial macFilial = null;
    private List<ContadorAcessos> listaContador = new ArrayList();
    private List<String> images = new ArrayList<>();
    private boolean habilitaLog = false;
    private boolean habilitaDebugQuery = false;
    private Boolean export = null;
    private String historicoAcesso = "";
    private String ip = "";
    private String dispositivo = "";

    public ControleUsuarioBean() {
        // new Correios().main();
    }

    public void atualizaDemissionaSocios() {
        FunctionsDao db = new FunctionsDao();
        ConfiguracaoSocialBean csb = new ConfiguracaoSocialBean();
        csb.init();
        ConfiguracaoSocial cs = csb.getConfiguracaoSocial();
        if (cs.getInativaDemissionado() && DataHoje.maiorData(DataHoje.dataHoje(), cs.getDataInativacaoDemissionado()) && cs.getGrupoCategoriaInativaDemissionado() != null) {
            if (db.demissionaSocios(cs.getGrupoCategoriaInativaDemissionado().getId(), cs.getDiasInativaDemissionado())) {
                Dao di = new Dao();
                cs = (ConfiguracaoSocial) di.find(cs);
                di.openTransaction();
                cs.setDataInativacaoDemissionado(DataHoje.dataHoje());
                di.update(cs);
                di.commit();
            }
        }
    }

    public boolean block() throws Exception {
        String nomeCliente = null;
        if (GenericaSessao.exists("sessaoCliente")) {
            nomeCliente = (String) GenericaSessao.getString("sessaoCliente");
        }
        if (nomeCliente == null) {
            return true;
        }
        if (nomeCliente.equals("Rtools") || nomeCliente.equals("Sindical") || nomeCliente.equals("ComercioLimeira")) {
            return true;
        }
        ResultSet rs;
        PreparedStatement ps;
        DBExternal dBExternal = new DBExternal();
        try {
            ps = dBExternal.getConnection().prepareStatement(
                    "   SELECT *                    "
                    + "   FROM sis_configuracao     "
                    + "  WHERE ds_identifica =     '" + nomeCliente + "'"
                    + "  LIMIT 1                    "
            );
            
            rs = ps.executeQuery();

            if (!rs.next()) {
                return true;
            }

            Boolean ativo = rs.getBoolean("is_ativo");

            if (ativo) {
                return true;
            }

        } catch (Exception e) {
            e.getMessage();
            return true;
        }
        return false;
    }

    public String validacao() throws Exception {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (!block()) {
            GenericaMensagem.warn("Sistema. Entre em contato com nosso suporte técnico. (16) 3964-6117", "Entre em contato com nosso suporte técnico.");
            return null;
        }

        String pagina = null;
        if (macFilial != null) {
            GenericaSessao.put("acessoFilial", macFilial);
        } else {
            GenericaSessao.put("acessoFilial");
        }
        GenericaSessao.put("indicaAcesso", "local");
        UsuarioDao db = new UsuarioDao();
        String user = usuario.getLogin(), senh = usuario.getSenha();
        if (usuario.getLogin().equals("") || usuario.getLogin().equals("Usuario")) {
            msgErro = "@ Informar nome do usuário!";
            GenericaMensagem.warn("Validação", msgErro);
            return pagina;
        }
        if (usuario.getSenha().equals("") || usuario.getSenha().equals("Senha")) {
            msgErro = "@ Informar senha!";
            GenericaMensagem.warn("Validação", msgErro);
            return pagina;
        }
        usuario = db.ValidaUsuario(usuario.getLogin(), usuario.getSenha());
        if (usuario != null) {
            filial = retornaStringFilial(macFilial, usuario);
            if (usuario.getId() != 1) {
                if (usuario.getAutenticado()) {
                    if (macFilial == null) {
                        usuario = new Usuario();
                        GenericaMensagem.warn("Obrigatório uso de MAC Filial! Contate o administrador do sistema.", "Nome do dispositivo diferente do registrado (Registro Computador/Mac Filial)!");
                        return null;
                    }
                }
//                try {
//                    if (!macFilial.getNomeDispositivo().isEmpty() && !dispositivo.equals(macFilial.getNomeDispositivo())) {
//                        usuario = new Usuario();
//                        GenericaMensagem.warn("Sistema. Nome do dispositivo diferente do registrado (Registro Computador/Mac Filial)! Contate o administrador do sistema.", "Nome do dispositivo diferente do registrado (Registro Computador/Mac Filial)!");
//                        return null;
//                    }
//                } catch (Exception e) {
//
//                }
            }
            AtalhoDao dba = new AtalhoDao();
            if (dba.listaAcessosUsuario(usuario.getId()).isEmpty()) {
                Diretorio.criar("");
                Diretorio.criar("Relatorios");
                Diretorio.criar("Imagens/Fotos");
                Diretorio.criar("Imagens/LogoPatronal");
                Diretorio.criar("Imagens/Mapas");
                Diretorio.criar("Arquivos/contrato");
                Diretorio.criar("Arquivos/convencao");
                Diretorio.criar("Arquivos/downloads/boletos");
                Diretorio.criar("Arquivos/downloads/carteirinhas");
                Diretorio.criar("Arquivos/downloads/etiquetas");
                Diretorio.criar("Arquivos/downloads/fichas");
                Diretorio.criar("Arquivos/downloads/protocolo");
                Diretorio.criar("Arquivos/downloads/relatorios");
                Diretorio.criar("Arquivos/downloads/remessa");
                Diretorio.criar("Arquivos/downloads/repis");
                Diretorio.criar("Arquivos/notificacao");
                Diretorio.criar("Arquivos/retorno/pendentes"); // EXCLUIR DEPOIS DA DATA 01/11/2014 EM FASE DE TESTES
                Diretorio.criar("Arquivos/senhas");
            }
            if (export != null && export == true) {
                Implantacao implantacao = new Implantacao();
                implantacao.run();
            }
            String c = ControleAcessoBean.getStaticClienteCoockie();
            if (c != null && !c.isEmpty()) {
                if (!c.equals(getCliente())) {

                }
            }
            // Inserir cookie
            FacesContext context = FacesContext.getCurrentInstance();
            if (context != null) {
                // Cria cookie
                Cookie coockieCliente = new Cookie("cliente", getCliente());
                // Adiciona
                ((HttpServletResponse) context.getExternalContext().getResponse()).addCookie(coockieCliente);
                if (filial != null && !filial.isEmpty()) {
                    Cookie coockieFilial = new Cookie("filial", macFilial.getMac());
                    ((HttpServletResponse) context.getExternalContext().getResponse()).addCookie(coockieFilial);
                }
            }
            pagina = "menuPrincipal";
            GenericaSessao.put("sessaoUsuario", usuario);
            GenericaSessao.put("usuarioLogin", usuario.getLogin());
            GenericaSessao.put("userName", "LOCAL - " + usuario.getLogin() + " (" + getCliente() + ")");
            GenericaSessao.put("linkClicado", true);
            GenericaSessao.put("acessoCadastro", false);
            login = ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getPessoa().getNome() + " - "
                    + ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getPessoa().getTipoDocumento().getDescricao() + ": "
                    + ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getPessoa().getDocumento();
            historicoAcesso = "";
            historicoAcesso = "ÚLTIMO ACESSO EM ";
            UsuarioHistoricoAcessoDao uhad = new UsuarioHistoricoAcessoDao();
            UsuarioHistoricoAcesso ua = uhad.lastLogin(usuario.getId());
            Dao dao = new Dao();
            if (ua != null) {
                historicoAcesso += ua.getLogin();
                historicoAcesso += " ÁS " + ua.getLoginHora() + " - ";
                historicoAcesso += " IP: " + ua.getIp();
            }
            List<UsuarioHistoricoAcesso> listUha = uhad.listOpenedSession(usuario.getId());
            for (int i = 0; i < listUha.size(); i++) {
                listUha.get(i).setDtExpired(new Date());
                dao.update(listUha.get(i), true);
            }
            UsuarioHistoricoAcesso usuarioHistoricoAcesso = new UsuarioHistoricoAcesso();
            usuarioHistoricoAcesso.setUsuario(usuario);
            usuarioHistoricoAcesso.setIp(ip);
            usuarioHistoricoAcesso.setDispositivo(dispositivo);
            usuarioHistoricoAcesso.setSessionId(request.getSession().getId());
            if (GenericaSessao.exists("acessoFilial")) {
                usuarioHistoricoAcesso.setMacFilial((MacFilial) GenericaSessao.getObject("acessoFilial"));
            }
            if (dao.save(usuarioHistoricoAcesso, true)) {
                GenericaSessao.put("usuario_historico_acesso", usuarioHistoricoAcesso);

            }
            usuario = new Usuario();
            msgErro = "";
            atualizaDemissionaSocios();
        } else {
            //log.live("Login de acesso tentativa de acesso usr:" + user + "/sen: " + senh);
            usuario = new Usuario();
            msgErro = "@ Usuário e/ou Senha inválidas! Tente novamente.";
            GenericaMensagem.warn("Validação", msgErro);
        }
        return pagina;
    }

    public static String retornaStringFilial(MacFilial mf, Usuario u) {
        if (mf == null) {
            return "";
        }

        String string_filial = "";
        string_filial = "Filial: ( " + mf.getFilial().getFilial().getPessoa().getNome() + " / " + mf.getDepartamento().getDescricao() + " )";

        if (mf.getMesa() != null && mf.getMesa() > 0) {
            string_filial += " - Guiche: " + mf.getMesa();
        }
        if (mf.getDescricao() != null && !mf.getDescricao().isEmpty()) {
            string_filial += " - Computador: " + mf.getDescricao();
        }

        if (!mf.isCaixaOperador()) {
            if (mf.getCaixa() != null) {
                if (string_filial.isEmpty()) {
                    string_filial = "Caixa: " + mf.getCaixa().getDescricao();
                } else {
                    string_filial += " - Caixa: " + mf.getCaixa().getDescricao();
                }
            } else {
                string_filial += " - Caixa: NENHUM DEFINIDO";
            }
        } else {
            FinanceiroDao dbf = new FinanceiroDao();
            Caixa caixa = dbf.pesquisaCaixaUsuario(u.getId(), mf.getFilial().getId());
            if (caixa != null) {
                if (string_filial.isEmpty()) {
                    string_filial = "Caixa: " + caixa.getDescricao();
                } else {
                    string_filial += " - Caixa: " + caixa.getDescricao();
                }
            } else {
                string_filial += " - Caixa: NENHUM DEFINIDO";
            }
        }
        return string_filial;
    }

    public String getValidacaoIndex() throws IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String chamado = request.getParameter("chamado");
        String co = "";
        JSONObject jSONObject = null;
        if (chamado != null && !chamado.isEmpty()) {
            chamado = chamado.replace("'", "\"");
            jSONObject = new JSONObject(chamado);
        }

        if (GenericaSessao.exists("sessaoCliente")) {
            GenericaSessao.remove("conexao");
        }
        String requestCliente;
        if (request.getParameter("cliente") != null && request.getParameter("cliente") != FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente")) {
            requestCliente = request.getParameter("cliente");
        } else {
            requestCliente = "Sindical";
        }
        if (!requestCliente.equals("")) {
            if (GenericaSessao.exists("sessaoCliente")) {
                GenericaSessao.remove("sessaoCliente");
            }
            if (GenericaSessao.exists("acessoFilial")) {
                GenericaSessao.remove("acessoFilial");
            }
            GenericaSessao.put("sessaoCliente", requestCliente);
        } else {
            if (GenericaSessao.exists("sessaoCliente")) {
                GenericaSessao.remove("sessaoCliente");
            }
            if (GenericaSessao.exists("acessoFilial")) {
                GenericaSessao.remove("acessoFilial");
            }
        }

        return null;
    }

    public String getValidaIndex() {
        if (GenericaSessao.exists("sessaoCliente")) {
            GenericaSessao.remove("conexao");
        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String requestCliente;
        if (request.getParameter("cliente") != null && request.getParameter("cliente") != FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente")) {
            FacesContext conext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
            session.invalidate();
            requestCliente = request.getParameter("cliente");
        } else {
            requestCliente = "Sindical";
        }
        if (!requestCliente.equals("")) {
            if (GenericaSessao.exists("sessaoCliente")) {
                GenericaSessao.remove("sessaoCliente");
            }
            if (GenericaSessao.exists("acessoFilial")) {
                GenericaSessao.remove("acessoFilial");
            }
            GenericaSessao.put("sessaoCliente", requestCliente);
        } else {
            if (GenericaSessao.exists("sessaoCliente")) {
                GenericaSessao.remove("sessaoCliente");
            }
            if (GenericaSessao.exists("acessoFilial")) {
                GenericaSessao.remove("acessoFilial");
            }
        }
        return null;
    }

    public void refreshForm(String retorno) throws IOException {
    }

    public String voltarDoAcessoNegado() {
        linkVoltar = GenericaSessao.getString("urlRetorno");
        if (linkVoltar == null) {
            return "index";
        } else {
            return converteURL();
        }
    }

    public String converteURL() {
        String url = linkVoltar;
        iniURL = url.lastIndexOf("/");
        fimURL = url.lastIndexOf(".");
        if (iniURL != -1 && fimURL != -1) {
            paginaDestino = url.substring(iniURL + 1, fimURL);
        } else {
            paginaDestino = url;
        }
        return paginaDestino;
    }

    public void carregar() {
        //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("urlIndex", request.getQueryString());
        filialDep = "oi";
    }

    public Usuario getUsuario() {
        if (usuario == null) {
            usuario = new Usuario();
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }

    public String getFilial() {
        return filial;
    }

    public void setFilial(String filial) {
        this.filial = filial;
    }

    public String getFilialDep() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            //  filialDep = request.getRequestURL().toString();
            // filialDep = requestFilial.getQueryString();
            filialDep = null;
            macFilial = null;
            filialDep = request.getParameter("filial");
            if (filialDep == null) {
                filialDep = getFilialCoockie();
            }
            if (filialDep != null) {
                MacFilialDao macFilialDao = new MacFilialDao();
                macFilial = macFilialDao.pesquisaMac(filialDep);
                if (macFilial != null) {
                    filialDep = macFilial.getFilial().getFilial().getPessoa().getNome();
                } else {
                    filialDep = "Filial sem Registro";
                }
            }
            try {
                if (!GenericaSessao.exists("ip")) {
                    // String ipAddress = request.getHeader("X-FORWARDED-FOR");
                    String ipAddress = null;
                    GenericaSessao.put("session_id", request.getSession().getId());
                    if (ipAddress == null) {
                        //ipAddress = request.getRemoteAddr();
                        ipAddress = "localhost";
                        if (ipAddress != null && !ipAddress.isEmpty()) {
                            GenericaSessao.put("ip", ipAddress);
                            if (!GenericaSessao.exists("dispositivo")) {
                                try {
                                    // InetAddress addr = InetAddress.getByName(ipAddress);  // DOMAIN NAME from IP
                                    // dispositivo = addr.getHostName();
                                    dispositivo = "nenhum";
                                    GenericaSessao.put("dispositivo", dispositivo);
                                } catch (Exception e) {

                                }
                            }
                            ip = ipAddress;
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
        return filialDep;
    }

    public void setFilialDep(String filialDep) {
        this.filialDep = filialDep;
    }

    public MacFilial getMacFilial() {
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public String getMsgErro() {
        return msgErro;
    }

    public void setMsgErro(String msgErro) {
        this.msgErro = msgErro;
    }

    public List<ContadorAcessos> getListaContador() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            Usuario usu = ((Usuario) GenericaSessao.getObject("sessaoUsuario"));
            AtalhoDao dba = new AtalhoDao();
            listaContador.clear();
            listaContador = dba.listaAcessosUsuario(usu.getId());
        }
        return listaContador;
    }

    public void setListaContador(List<ContadorAcessos> listaContador) {
        this.listaContador = listaContador;
    }

    public static String getCliente() {
        if (GenericaSessao.exists("sessaoCliente")) {
            cliente = GenericaSessao.getString("sessaoCliente");
        }
        return cliente;
    }

    public String getClienteString() {
        String novoCliente = "";
        if (GenericaSessao.exists("sessaoCliente")) {
            novoCliente = GenericaSessao.getString("sessaoCliente");
            getExport();
        }
        return novoCliente;
    }

    public String getClienteLowerCaseString() {
        String novoCliente = "";
        if (GenericaSessao.exists("sessaoCliente")) {
            novoCliente = GenericaSessao.getString("sessaoCliente").toLowerCase();
        }
        return novoCliente;
    }

    public boolean isBoqueiaMenu() {
        String nomeCliente = getClienteString();
        if (nomeCliente.equals("Rtools") || nomeCliente.equals("Sindical")) {
            return true;
        }
        return false;
    }

    public static void setBloqueiaMenu(String aBloqueiaMenu) {
        bloqueiaMenu = aBloqueiaMenu;
    }

    public void removeSessaoModuloMenuPrincipal() {
        if (GenericaSessao.exists("idModulo")) {
            GenericaSessao.remove("idModulo");
        }
    }

    public List<String> getImages() {
        if (images.isEmpty()) {
            images = new ArrayList<String>();
            images.add("1.jpg");
            images.add("2.jpg");
            images.add("3.jpg");
            images.add("4.jpg");
            images.add("5.jpg");
        }
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Usuario getUsuarioSuporteTecnico() {
        if (usuarioSuporteTecnico.getId() == -1) {
            if (GenericaSessao.exists("sessaoUsuario")) {
                usuarioSuporteTecnico = (Usuario) GenericaSessao.getObject("sessaoUsuario");
                if (usuarioSuporteTecnico.getEmail().isEmpty()) {
                    if (!usuarioSuporteTecnico.getPessoa().getEmail1().equals("")) {
                        usuarioSuporteTecnico.setEmail(usuarioSuporteTecnico.getPessoa().getEmail1());
                    } else {
                        Usuario u = (Usuario) new Dao().find(new Usuario(), 1);
                        usuarioSuporteTecnico.setEmail(u.getPessoa().getEmail1());
                    }
                }
            }
        }
        return usuarioSuporteTecnico;
    }

    public void setUsuarioSuporteTecnico(Usuario usuarioSuporteTecnico) {
        this.usuarioSuporteTecnico = usuarioSuporteTecnico;
    }

    public boolean isHabilitaLog() {
        if (GenericaSessao.exists("habilitaLog")) {
            return GenericaSessao.getBoolean("habilitaLog");
        }
        return habilitaLog;
    }

    public void setHabilitaLog(boolean habilitaLog) {
        GenericaSessao.put("habilitaLog", habilitaLog);
        this.habilitaLog = habilitaLog;
    }

    public boolean isHabilitaDebugQuery() {
        if (GenericaSessao.exists("habilitaDebugQuery")) {
            return GenericaSessao.getBoolean("habilitaDebugQuery");
        }
        return habilitaDebugQuery;
    }

    public void setHabilitaDebugQuery(boolean habilitaDebugQuery) {
        GenericaSessao.put("habilitaDebugQuery", habilitaDebugQuery);
        this.habilitaDebugQuery = habilitaDebugQuery;
    }

    public Boolean getExport() {
        try {
            export = Boolean.parseBoolean(GenericaRequisicao.getParametro("export"));
        } catch (Exception e) {
            export = null;
        }
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public String getFilialCoockie() {
        String filialCoockie = null;
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            Map<String, Object> cookies = ctx.getExternalContext().getRequestCookieMap();
            Cookie cookieCliente = (Cookie) cookies.get("filial");
            if (cookieCliente != null && !cookieCliente.getValue().isEmpty()) {
                filialDep = cookieCliente.getValue();
            }
        }
        return filialCoockie;
    }

    public String getHistoricoAcesso() {
        return historicoAcesso;
    }

    public void setHistoricoAcesso(String historicoAcesso) {
        this.historicoAcesso = historicoAcesso;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

}

//            if (!nomeCliente.equals("Rtools") && !nomeCliente.equals("Sindical")) {
//                DBExternal dbe = new DBExternal();
//                if (dbe.getConnection() != null) {
//                    try {
//                        String string = "SELECT * FROM sis_configuracao WHERE ds_identifica = '" + nomeCliente + "'";
//                        ResultSet resultSet = dbe.getStatment().executeQuery(string);
//                        String id = "";
//                        String ativo = "";
//                        while (resultSet.next()) {
//                            id = resultSet.getString("id");
//                            ativo = resultSet.getString("is_ativo");
//                            if (ativo.equals("f")) {
//                                resultSet.close();
//                                dbe.getStatment().close();
//                                msgErro = "@ Entre em contato com nossa equipe (16) 3964.6117";
//                                GenericaMensagem.warn(msgErro, "");
//                                return null;
//                            }
//                        }
//                        if (!id.equals("")) {
//                            string = "UPDATE sis_configuracao SET nr_acesso = (nr_acesso+1) WHERE id = " + id;
//                            int result = dbe.getStatment().executeUpdate(string);
//                            if (result != 1) {
//                                dbe.getStatment().close();
//                                msgErro = "@ Erro ao atualizar contador!";
//                                GenericaMensagem.warn(msgErro, "");
//                                return null;
//                            }
//                        }
//                    } catch (SQLException exception) {
//                        dbe.closeStatment();
//                        msgErro = "@ Erro!";
//                        GenericaMensagem.warn(msgErro, "");
//                        return null;
//                    }
//                    dbe.getStatment().close();
//                }
//            }
