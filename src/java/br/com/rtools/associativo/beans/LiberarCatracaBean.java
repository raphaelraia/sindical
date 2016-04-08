/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Catraca;
import br.com.rtools.associativo.CatracaLiberaAcesso;
import br.com.rtools.associativo.dao.CatracaDao;
import br.com.rtools.pessoa.BiometriaCatraca;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
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
public class LiberarCatracaBean implements Serializable {

    private Usuario usuario = new Usuario();
    private MacFilial macFilial = new MacFilial();
    private List<Catraca> listaCatraca = new ArrayList();
    private Catraca catraca = new Catraca();

    public LiberarCatracaBean() {
        usuario = Usuario.getUsuario();
        macFilial = MacFilial.getAcessoFilial();

        loadListaCatraca();
    }

    public void liberar() {
        // CASO FOR COLOCAR O DEPARTAMENTO DO MAC LOGADO
//        if (new CatracaDao().listaCatracaDepartamento(macFilial.getDepartamento().getId()).isEmpty()){
//            GenericaMensagem.error("ATENÇÃO", "Não Existe Catraca Registrada para esta Filial!");
//            return;
//        }
        
        Dao dao = new Dao();
        
        CatracaLiberaAcesso cla = new CatracaLiberaAcesso();
        
        cla.setDepartamento(catraca.getDepartamento());
        cla.setDtLiberacao(DataHoje.dataHoje());
        cla.setHoraLiberacao(DataHoje.horaMinuto());
        cla.setPessoa(usuario.getPessoa());
        cla.setCatraca(catraca);
        
        dao.openTransaction();
        
        if (!dao.save(cla)) {
            dao.rollback();
            GenericaMensagem.error("ATENÇÃO", "Erro ao Salvar Registro!");
            return;
        }
        
        BiometriaCatraca bc = new BiometriaCatraca();
        bc.setIp(catraca.getIp());
        bc.setPessoa(usuario.getPessoa());
        
        if (!dao.save(bc)) {
            dao.rollback();
            GenericaMensagem.error("ATENÇÃO", "Erro ao Salvar Registro!");
            return;
        }
        
        dao.commit();
        catraca = new Catraca();
        GenericaMensagem.info("SUCESSO", "Catraca Liberada!");
    }
    
    public void selecionarCatraca(Catraca c){
        catraca = c;
    }
    
    public final void loadListaCatraca() {
        listaCatraca.clear();

        listaCatraca = new CatracaDao().listaCatraca();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MacFilial getMacFilial() {
        if (macFilial.getId() == -1) {
            macFilial = MacFilial.getAcessoFilial();
        }
        return macFilial;
    }

    public void setMacFilial(MacFilial macFilial) {
        this.macFilial = macFilial;
    }

    public List<Catraca> getListaCatraca() {
        return listaCatraca;
    }

    public void setListaCatraca(List<Catraca> listaCatraca) {
        this.listaCatraca = listaCatraca;
    }

    public Catraca getCatraca() {
        return catraca;
    }

    public void setCatraca(Catraca catraca) {
        this.catraca = catraca;
    }

}
