/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Catraca;
import br.com.rtools.associativo.CatracaMonitora;
import br.com.rtools.associativo.dao.CatracaDao;
import br.com.rtools.seguranca.Departamento;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
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
public class CatracaBean implements Serializable {

    private Catraca catraca = new Catraca();
    private List<SelectItem> listaDepartamento = new ArrayList();
    private Integer indexDepartamento = 0;
    private List<ObjectCatraca> listaCatraca = new ArrayList();

    public CatracaBean() {
        loadListaDepartamento();
        loadListaCatraca();
    }

    public void novo() {
        catraca = new Catraca();
        indexDepartamento = 0;
    }

    public void salvar() {
        if (catraca.getNumero().length() < 2) {
            GenericaMensagem.warn("ATENÇÃO", "Digite um Número para a Catraca!");
            return;
        }

        if (catraca.getIp().isEmpty()) {
            GenericaMensagem.warn("ATENÇÃO", "Digite um IP!");
            return;
        }
        Dao dao = new Dao();

        catraca.setDepartamento((Departamento) dao.find(new Departamento(), Integer.valueOf(listaDepartamento.get(indexDepartamento).getDescription())));
        catraca.setNrNumero(Integer.valueOf(catraca.getNumero()));
        
        dao.openTransaction();
        if (catraca.getId() == -1) {
            if (!dao.save(catraca)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "Erro ao salvar Catraca!");
                return;
            }
            GenericaMensagem.info("SUCESSO", "Catraca Salva!");
        } else {
            if (!dao.update(catraca)) {
                dao.rollback();
                GenericaMensagem.error("ATENÇÃO", "Erro ao atualizar Catraca!");
                return;
            }

            GenericaMensagem.info("SUCESSO", "Catraca Atualizada!");
        }
        dao.commit();

        catraca = new Catraca();
        loadListaCatraca();
    }

    public void editar(ObjectCatraca c) {
        catraca = c.getCatraca();

        for (int i = 0; i < listaDepartamento.size(); i++) {
            SelectItem get = listaDepartamento.get(i);
            if (catraca.getDepartamento().getId().equals(Integer.valueOf(get.getDescription()))) {
                indexDepartamento = i;
            }
        }
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.openTransaction();
        if (!dao.delete(catraca)) {
            dao.rollback();
            GenericaMensagem.error("ATENÇÃO", "Erro ao deletar Catraca!");
            return;
        }
        dao.commit();
        GenericaMensagem.info("SUCESSO", "Catraca Excluída!");
        catraca = new Catraca();
        loadListaCatraca();
    }

    public final void loadListaDepartamento() {
        listaDepartamento.clear();
        indexDepartamento = 0;

        List<Departamento> result = new Dao().list(new Departamento());

        for (int i = 0; i < result.size(); i++) {
            listaDepartamento.add(
                    new SelectItem(
                            i,
                            result.get(i).getDescricao(),
                            Integer.toString(result.get(i).getId())
                    )
            );
        }
    }

    public final void loadListaCatraca() {
        listaCatraca.clear();
        CatracaDao dao = new CatracaDao();
        List<Catraca> result = dao.listaCatraca();
        
        for (Catraca c : result){
            listaCatraca.add(new ObjectCatraca(c, (CatracaMonitora) new Dao().rebind(dao.pesquisaCatracaMonitora(c.getId()))));
        }
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

    public Catraca getCatraca() {
        return catraca;
    }

    public void setCatraca(Catraca catraca) {
        this.catraca = catraca;
    }

    public List<ObjectCatraca> getListaCatraca() {
        return listaCatraca;
    }

    public void setListaCatraca(List<ObjectCatraca> listaCatraca) {
        this.listaCatraca = listaCatraca;
    }
    
    public class ObjectCatraca{
        private Catraca catraca;
        private CatracaMonitora catracaMonitora;

        public ObjectCatraca(Catraca catraca, CatracaMonitora catracaMonitora) {
            this.catraca = catraca;
            this.catracaMonitora = catracaMonitora;
        }

        public Catraca getCatraca() {
            return catraca;
        }

        public void setCatraca(Catraca catraca) {
            this.catraca = catraca;
        }

        public CatracaMonitora getCatracaMonitora() {
            return catracaMonitora;
        }

        public void setCatracaMonitora(CatracaMonitora catracaMonitora) {
            this.catracaMonitora = catracaMonitora;
        }
    }

}
