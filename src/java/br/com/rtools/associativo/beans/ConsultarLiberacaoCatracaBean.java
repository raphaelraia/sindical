/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.CatracaLiberaAcesso;
import br.com.rtools.associativo.dao.CatracaDao;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.GenericaSessao;
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
public class ConsultarLiberacaoCatracaBean implements Serializable {
    private String dataInicio = "";
    private String dataFinal = "";
    private Usuario usuario = new Usuario();

    private List<SelectItem> listaDepartamento = new ArrayList();
    private Integer indexDepartamento = 0;
    
    private List<CatracaLiberaAcesso> listaCatracaLiberaAcesso = new ArrayList();
    
    public ConsultarLiberacaoCatracaBean(){
        loadListaDepartamento();
    }
    
    public void loadListaCatracaLiberaAcesso(){
        listaCatracaLiberaAcesso.clear();
        
        String dt_inicio = dataInicio.isEmpty() ? null : dataInicio, 
                dt_final = dataFinal.isEmpty() ? null : dataFinal;
        Integer id_usuario = usuario.getId() == -1 ? null : usuario.getId(), id_departamento = Integer.valueOf(listaDepartamento.get(indexDepartamento).getDescription());
        
        listaCatracaLiberaAcesso = new CatracaDao().listaPesquisaLiberacaoCatraca(dt_inicio, dt_final, id_usuario, id_departamento);
    }
    
    private void loadListaDepartamento() {
        listaDepartamento.clear();
        
        List<Departamento> result = new CatracaDao().listaDepartamentoPorCatraca();
        
        for (int i = 0; i < result.size(); i++) {
            Departamento get = result.get(i);
            listaDepartamento.add(
                    new SelectItem(
                            i,
                            get.getDescricao(),
                            Integer.toString(get.getId())
                    )
            );
        }
    }
    
    public void removerUsuario(){
        usuario = new Usuario();
    }
    
    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Usuario getUsuario() {
        if (GenericaSessao.exists("usuarioPesquisa")){
            usuario = (Usuario) GenericaSessao.getObject("usuarioPesquisa", true);
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<SelectItem> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<SelectItem> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public Integer getIndexDepartamento() {
        return indexDepartamento;
    }

    public void setIndexDepartamento(Integer indexDepartamento) {
        this.indexDepartamento = indexDepartamento;
    }

    public List<CatracaLiberaAcesso> getListaCatracaLiberaAcesso() {
        return listaCatracaLiberaAcesso;
    }

    public void setListaCatracaLiberaAcesso(List<CatracaLiberaAcesso> listaCatracaLiberaAcesso) {
        this.listaCatracaLiberaAcesso = listaCatracaLiberaAcesso;
    }
}
