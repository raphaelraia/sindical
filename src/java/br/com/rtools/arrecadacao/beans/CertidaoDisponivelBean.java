/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.CertidaoDisponivel;
import br.com.rtools.arrecadacao.CertidaoTipo;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.dao.CertidaoDisponivelDao;
import br.com.rtools.arrecadacao.dao.WebREPISDao;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.dao.CidadeDao;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class CertidaoDisponivelBean implements Serializable {

    private CertidaoDisponivel certidaoDisponivel = new CertidaoDisponivel();

    private Integer indexListaConvencao = 0;
    private List<SelectItem> listaConvencao = new ArrayList();

    private Integer indexListaCidade = 0;
    private List<SelectItem> listaCidade = new ArrayList();

    private Integer indexListaCertidaoTipo = 0;
    private List<SelectItem> listaCertidaoTipo = new ArrayList();

    private String uf = "SP";

    private List<CertidaoDisponivel> listaCertidaoDisponivel = new ArrayList();

    private String caminhoLogo = "";
    private String caminhoFundo = "";

    public CertidaoDisponivelBean() {
        loadListaConvencao();
        loadListaCidade();
        loadListaCertidaoTipo();
        loadListaCertidaoDisponivel();
    }

    public void loadLogoCertidao() {
        if (certidaoDisponivel.getId() != -1) {
            String caminho = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/logocertidao/" + certidaoDisponivel.getId() + "/" + certidaoDisponivel.getLogo());
            if (new File(caminho).exists()) {
                caminhoLogo = "/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/logocertidao/" + certidaoDisponivel.getId() + "/" + certidaoDisponivel.getLogo();
                return;
            }
        }

        caminhoLogo = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/" + certidaoDisponivel.getLogo();
    }

    public void loadFundoCertidao() {
        if (certidaoDisponivel.getId() != -1) {
            String caminho = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/logocertidao/" + certidaoDisponivel.getId() + "/" + certidaoDisponivel.getFundo());
            if (new File(caminho).exists()) {
                caminhoFundo = "/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/logocertidao/" + certidaoDisponivel.getId() + "/" + certidaoDisponivel.getFundo();
                return;
            }
        }

        caminhoFundo = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/" + certidaoDisponivel.getFundo();
    }

    public void salvar() {
        Dao dao = new Dao();

        certidaoDisponivel.setCertidaoTipo((CertidaoTipo) dao.find(new CertidaoTipo(), Integer.valueOf(listaCertidaoTipo.get(indexListaCertidaoTipo).getDescription())));
        certidaoDisponivel.setConvencao((Convencao) dao.find(new Convencao(), Integer.valueOf(listaConvencao.get(indexListaConvencao).getDescription())));
        certidaoDisponivel.setCidade((Cidade) dao.find(new Cidade(), Integer.valueOf(listaCidade.get(indexListaCidade).getDescription())));

        dao.openTransaction();

        if (certidaoDisponivel.getId() == -1) {

            certidaoDisponivel.setLogo("LogoCliente.png");
            certidaoDisponivel.setFundo("LogoFundo.png");

            if (!dao.save(certidaoDisponivel)) {
                GenericaMensagem.error("ATENÇÃO", "Erro ao salvar Certidão Disponível!");
                dao.rollback();
                return;
            }

            dao.commit();

            loadLogoCertidao();
            loadFundoCertidao();

            GenericaMensagem.info("SUCESSO", "Certidão Disponível salva!");
        } else {
            if (!dao.update(certidaoDisponivel)) {
                GenericaMensagem.error("ATENÇÃO", "Erro ao atualizar Certidão Disponível!");
                dao.rollback();
                return;
            }

            dao.commit();

            GenericaMensagem.info("SUCESSO", "Certidão Disponível atualizada!");
        }

        loadListaCertidaoDisponivel();
    }

    public void novo() {
        certidaoDisponivel = new CertidaoDisponivel();
    }

    public void editar(CertidaoDisponivel cd) {
        certidaoDisponivel = cd;

        uf = certidaoDisponivel.getCidade().getUf();

        loadListaCidade();

        for (int i = 0; i < listaCidade.size(); i++) {
            if (Integer.valueOf(listaCidade.get(i).getDescription()) == certidaoDisponivel.getCidade().getId()) {
                indexListaCidade = i;
            }
        }

        for (int i = 0; i < listaConvencao.size(); i++) {
            if (Integer.valueOf(listaConvencao.get(i).getDescription()) == certidaoDisponivel.getConvencao().getId()) {
                indexListaConvencao = i;
            }
        }

        for (int i = 0; i < listaCertidaoTipo.size(); i++) {
            if (Integer.valueOf(listaCertidaoTipo.get(i).getDescription()) == certidaoDisponivel.getCertidaoTipo().getId()) {
                indexListaCertidaoTipo = i;
            }
        }

        loadLogoCertidao();
        loadFundoCertidao();
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(certidaoDisponivel)) {
            GenericaMensagem.error("ATENÇÃO", "Erro ao EXCLUIR Certidão Disponível!");
            dao.rollback();
            return;
        }

        dao.commit();

        novo();

        GenericaMensagem.info("SUCESSO", "Certidão Disponível EXCLUÍDA!");

        loadListaCertidaoDisponivel();
    }

    public void uploadLogo(FileUploadEvent event) {
        try {

            UUID uuidX = UUID.randomUUID();
            String uuid = uuidX.toString().replace("-", "_");
            String nome_arquivo = uuid + "." + event.getFile().getFileName().substring(event.getFile().getFileName().length() - 3);

            String caminho = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/logocertidao/" + certidaoDisponivel.getId() + "/" + nome_arquivo);

            File file = new File(caminho);

            FileUtils.writeByteArrayToFile(file, event.getFile().getContents());

            certidaoDisponivel.setLogo(nome_arquivo);

            new Dao().update(certidaoDisponivel, true);

            loadLogoCertidao();

            Thread.sleep(5000);

        } catch (InterruptedException | IOException ex) {
            ex.getMessage();
        }

    }

    public void uploadFundo(FileUploadEvent event) {
        try {
            UUID uuidX = UUID.randomUUID();
            String uuid = uuidX.toString().replace("-", "_");
            String nome_arquivo = uuid + "." + event.getFile().getFileName().substring(event.getFile().getFileName().length() - 3);

            String caminho = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/logocertidao/" + certidaoDisponivel.getId() + "/" + nome_arquivo);

            File file = new File(caminho);

            FileUtils.writeByteArrayToFile(file, event.getFile().getContents());

            certidaoDisponivel.setFundo(nome_arquivo);

            new Dao().update(certidaoDisponivel, true);

            loadFundoCertidao();

            Thread.sleep(5000);

        } catch (InterruptedException | IOException ex) {
            ex.getMessage();
        }

    }

    public final void loadListaCertidaoDisponivel() {
        listaCertidaoDisponivel.clear();

        listaCertidaoDisponivel = new CertidaoDisponivelDao().listaCertidaoDisponivel();
    }

    public final void loadListaCertidaoTipo() {

        listaCertidaoTipo.clear();
        indexListaCertidaoTipo = 0;

        List<CertidaoTipo> result = new WebREPISDao().listaCertidaoTipo();

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                listaCertidaoTipo.add(new SelectItem(i, result.get(i).getDescricao(), Integer.toString(result.get(i).getId())));
            }
        }
    }

    public final void loadListaCidade() {
        listaCidade.clear();
        indexListaCidade = 0;

        List<Cidade> result = new CidadeDao().pesquisaCidadeObj(uf);

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                listaCidade.add(new SelectItem(i, result.get(i).getCidade(), Integer.toString(result.get(i).getId())));
            }
        }
    }

    public final void loadListaConvencao() {
        listaConvencao.clear();
        indexListaConvencao = 0;

        List<Convencao> result = new Dao().list(new Convencao(), true);

        for (int i = 0; i < result.size(); i++) {
            listaConvencao.add(new SelectItem(i, result.get(i).getDescricao(), Integer.toString(result.get(i).getId())));
        }
    }

    public CertidaoDisponivel getCertidaoDisponivel() {
        return certidaoDisponivel;
    }

    public void setCertidaoDisponivel(CertidaoDisponivel certidaoDisponivel) {
        this.certidaoDisponivel = certidaoDisponivel;
    }

    public Integer getIndexListaConvencao() {
        return indexListaConvencao;
    }

    public void setIndexListaConvencao(Integer indexListaConvencao) {
        this.indexListaConvencao = indexListaConvencao;
    }

    public List<SelectItem> getListaConvencao() {
        return listaConvencao;
    }

    public void setListaConvencao(List<SelectItem> listaConvencao) {
        this.listaConvencao = listaConvencao;
    }

    public Integer getIndexListaCidade() {
        return indexListaCidade;
    }

    public void setIndexListaCidade(Integer indexListaCidade) {
        this.indexListaCidade = indexListaCidade;
    }

    public List<SelectItem> getListaCidade() {
        return listaCidade;
    }

    public void setListaCidade(List<SelectItem> listaCidade) {
        this.listaCidade = listaCidade;
    }

    public Integer getIndexListaCertidaoTipo() {
        return indexListaCertidaoTipo;
    }

    public void setIndexListaCertidaoTipo(Integer indexListaCertidaoTipo) {
        this.indexListaCertidaoTipo = indexListaCertidaoTipo;
    }

    public List<SelectItem> getListaCertidaoTipo() {
        return listaCertidaoTipo;
    }

    public void setListaCertidaoTipo(List<SelectItem> listaCertidaoTipo) {
        this.listaCertidaoTipo = listaCertidaoTipo;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public List<CertidaoDisponivel> getListaCertidaoDisponivel() {
        return listaCertidaoDisponivel;
    }

    public void setListaCertidaoDisponivel(List<CertidaoDisponivel> listaCertidaoDisponivel) {
        this.listaCertidaoDisponivel = listaCertidaoDisponivel;
    }

    public String getCaminhoLogo() {
        return caminhoLogo;
    }

    public void setCaminhoLogo(String caminhoLogo) {
        this.caminhoLogo = caminhoLogo;
    }

    public String getCaminhoFundo() {
        return caminhoFundo;
    }

    public void setCaminhoFundo(String caminhoFundo) {
        this.caminhoFundo = caminhoFundo;
    }

}
