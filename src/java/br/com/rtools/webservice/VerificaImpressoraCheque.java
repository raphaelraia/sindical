/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.webservice;

import br.com.rtools.financeiro.ImpressoraCheque;
import br.com.rtools.financeiro.dao.ImpressaoChequeDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.webservice.classes.WSImpressoraCheque;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@RequestScoped
public class VerificaImpressoraCheque implements Serializable {

    @Resource
    WebServiceContext wsContext;

    public void verifica() {
        /*
        ATENÇÃO, OS CAMINHOS DEFINIDOS AQUI NESTE WEB SERVICE ESTÃO CONFIGURADOS NO urlrewrite.xml
         */
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        // VALIDA CLIENTE ---
        if (GenericaSessao.exists("sessaoCliente")) {
            GenericaSessao.remove("conexao");
        }
        String requestCliente;
        if (request.getParameter("cliente") != null && request.getParameter("cliente") != FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente")) {
            FacesContext conext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();
            }
            requestCliente = request.getParameter("cliente");
        } else {
            requestCliente = "Sindical";
        }
        GenericaSessao.put("sessaoCliente", requestCliente);
        // ---

        try {
            WSImpressoraCheque ws = new WSImpressoraCheque();
            if (request.getParameter("numero_impressora") != null) {
                Integer nr_impressora = Integer.parseInt(request.getParameter("numero_impressora"));
                Dao dao = new Dao();

                ImpressoraCheque ic = new ImpressaoChequeDao().pesquisaImpressoraNumeroAtiva(nr_impressora);
                if (ic != null) {
                    ic = (ImpressoraCheque) dao.rebind(ic);
                    ws = new WSImpressoraCheque(
                            ic.getId(),
                            ic.getImpressora(), ic.getApelido(),
                            ic.getAtivo(),
                            ic.getBanco(),
                            ic.getValor(),
                            ic.getFavorecido(),
                            ic.getCidade(),
                            ic.getData(),
                            ic.getMensagem()
                    );
                }

                externalContext.getResponseOutputWriter().write(new JSONObject(ws).toString());
                facesContext.responseComplete();
                return;
            }

            externalContext.getResponseOutputWriter().write(new JSONObject(ws).toString());
            facesContext.responseComplete();
        } catch (NumberFormatException | IOException e) {
            e.getMessage();
        }
    }

    @Resource
    WebServiceContext wsContext2;

    public void ativa() {
        /*
        ATENÇÃO, OS CAMINHOS DEFINIDOS AQUI NESTE WEB SERVICE ESTÃO CONFIGURADOS NO urlrewrite.xml
         */
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        // VALIDA CLIENTE ---
        if (GenericaSessao.exists("sessaoCliente")) {
            GenericaSessao.remove("conexao");
        }

        String requestCliente;
        if (request.getParameter("cliente") != null && request.getParameter("cliente") != FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente")) {
            FacesContext conext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();
            }
            requestCliente = request.getParameter("cliente");
        } else {
            requestCliente = "Sindical";
        }
        GenericaSessao.put("sessaoCliente", requestCliente);
        // ---

        try {
            if (request.getParameter("numero_impressora") != null) {
                Integer nr_impressora = Integer.parseInt(request.getParameter("numero_impressora"));
                Dao dao = new Dao();

                ImpressoraCheque ic = new ImpressaoChequeDao().pesquisaImpressoraNumero(nr_impressora);

                if (ic == null) {
                    externalContext.getResponseOutputWriter().write(new JSONObject("{\"ativa\":false}").toString());
                    facesContext.responseComplete();
                    return;
                }

                if (request.getParameter("ativar") != null && request.getParameter("ativar").equals("true")) {
                    ic.setAtivo(true);
                    dao.update(ic, true);
                } else {
                    ic.setAtivo(false);
                    dao.update(ic, true);
                }

                externalContext.getResponseOutputWriter().write(new JSONObject("{\"ativa\":true}").toString());
                facesContext.responseComplete();
                return;
            }

            externalContext.getResponseOutputWriter().write(new JSONObject("{\"ativa\":false}").toString());
            facesContext.responseComplete();
        } catch (NumberFormatException | IOException e) {
            e.getMessage();
        }
    }

    @Resource
    WebServiceContext wsContext3;

    public void limpar() {
        /*
        ATENÇÃO, OS CAMINHOS DEFINIDOS AQUI NESTE WEB SERVICE ESTÃO CONFIGURADOS NO urlrewrite.xml
         */
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        // VALIDA CLIENTE ---
        if (GenericaSessao.exists("sessaoCliente")) {
            GenericaSessao.remove("conexao");
        }

        String requestCliente;
        if (request.getParameter("cliente") != null && request.getParameter("cliente") != FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente")) {
            FacesContext conext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();
            }
            requestCliente = request.getParameter("cliente");
        } else {
            requestCliente = "Sindical";
        }
        GenericaSessao.put("sessaoCliente", requestCliente);
        // ---

        try {
            if (request.getParameter("numero_impressora") != null) {
                Integer nr_impressora = Integer.parseInt(request.getParameter("numero_impressora"));
                String mensagem = request.getParameter("mensagem");

                Dao dao = new Dao();

                ImpressoraCheque ic = new ImpressaoChequeDao().pesquisaImpressoraNumero(nr_impressora);

                if (ic == null) {
                    externalContext.getResponseOutputWriter().write(new JSONObject("{\"status\":false}").toString());
                    facesContext.responseComplete();
                    return;
                }

                ic.setBanco(null);
                ic.setCidade(null);
                ic.setData(null);
                ic.setFavorecido(null);
                ic.setMensagem(null);
                ic.setValor(null);
                if (mensagem.equals("imprimindo") || mensagem.equals("ok")) {
                    ic.setMensagemErro("");
                } else {
                    ic.setMensagemErro(mensagem);
                }

                dao.update(ic, true);

                externalContext.getResponseOutputWriter().write(new JSONObject("{\"status\":true}").toString());
                facesContext.responseComplete();
                return;
            }

            externalContext.getResponseOutputWriter().write(new JSONObject("{\"status\":false}").toString());
            facesContext.responseComplete();
        } catch (NumberFormatException | IOException e) {
            e.getMessage();
        }
    }

    @Resource
    WebServiceContext wsContext4;

    public void lista() {
        /*
        ATENÇÃO, OS CAMINHOS DEFINIDOS AQUI NESTE WEB SERVICE ESTÃO CONFIGURADOS NO urlrewrite.xml
         */
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        externalContext.setResponseContentType("application/json");
        externalContext.setResponseCharacterEncoding("UTF-8");
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        // VALIDA CLIENTE ---
        if (GenericaSessao.exists("sessaoCliente")) {
            GenericaSessao.remove("conexao");
        }

        String requestCliente;
        if (request.getParameter("cliente") != null && request.getParameter("cliente") != FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoCliente")) {
            FacesContext conext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();
            }
            requestCliente = request.getParameter("cliente");
        } else {
            requestCliente = "Sindical";
        }
        GenericaSessao.put("sessaoCliente", requestCliente);
        // ---

        try {
            List<ImpressoraCheque> list_ic = new ImpressaoChequeDao().listaImpressora();

            JSONObject obj = new JSONObject();
            //obj.put("name", "mkyong.com");
            //obj.put("age", new Integer(100));

            JSONArray list = new JSONArray();
            list_ic.stream().forEach((ic) -> {
                list.put(ic.getImpressora() + " - " + ic.getApelido());
            });

            obj.put("impressoras", list);

            if (list_ic.isEmpty()) {
                externalContext.getResponseOutputWriter().write(obj.toString());
                facesContext.responseComplete();
                return;
            }

            externalContext.getResponseOutputWriter().write(new JSONObject("{\"lista\":vazia}").toString());
            facesContext.responseComplete();
        } catch (NumberFormatException | IOException e) {
            e.getMessage();
        }
    }
}
