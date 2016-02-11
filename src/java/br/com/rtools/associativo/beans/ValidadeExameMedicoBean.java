/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.ValidadeExameMedico;
import br.com.rtools.associativo.dao.ExameMedicoDao;
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
public class ValidadeExameMedicoBean implements Serializable {

    private Integer indexDepartamento = 0;
    private List<SelectItem> listaDepartamento = new ArrayList();

    private ValidadeExameMedico validadeExameMedico = new ValidadeExameMedico();

    private List<ValidadeExameMedico> listaValidadeExameMedico = new ArrayList();

    public ValidadeExameMedicoBean() {
        loadListaDepartamento();
        loadListaValidadeExameMedico();
    }

    public final void loadListaDepartamento() {
        listaDepartamento.clear();

        List<Departamento> result = new ArrayList();
        result.add((Departamento) new Dao().find(new Departamento(), 11)); // RECEPÇÃO ACADEMIA
        result.add((Departamento) new Dao().find(new Departamento(), 12)); // RECEPÇÃO CLUBE
        result.add((Departamento) new Dao().find(new Departamento(), 16)); // HIDROGINÁSTICA

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

    public final void loadListaValidadeExameMedico() {
        listaValidadeExameMedico.clear();

        listaValidadeExameMedico = new ExameMedicoDao().listaValidadeExameMedico();
    }

    public void salvar() {
        Dao dao = new Dao();

        dao.openTransaction();

        validadeExameMedico.setDepartamento((Departamento) dao.find(new Departamento(), Integer.valueOf(listaDepartamento.get(indexDepartamento).getDescription())));
        if (validadeExameMedico.getId() == -1) {
            if (new ExameMedicoDao().pesquisaValidadeExameMedico(validadeExameMedico.getDepartamento().getId()) != null) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Validade para este departamento já existe!");
                return;
            }

            if (!dao.save(validadeExameMedico)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao salvar validade, tente novamente!");
                return;
            }
        } else {
            if (!dao.update(validadeExameMedico)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao atualizar validade, tente novamente!");
                return;
            }
        }

        dao.commit();
        loadListaValidadeExameMedico();
        validadeExameMedico = new ValidadeExameMedico();
        GenericaMensagem.info("Sucesso", "Validade Exame Médico salvo!");
    }

    public void excluir() {
        Dao dao = new Dao();

        dao.openTransaction();

        if (validadeExameMedico.getId() != -1) {
            if (!dao.delete(validadeExameMedico)) {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao excluir validade, tente novamente!");
                return;
            }
        }

        dao.commit();
        loadListaValidadeExameMedico();
        validadeExameMedico = new ValidadeExameMedico();
        GenericaMensagem.info("Sucesso", "Validade Exame Médico excluído!");
    }

    public void editar(ValidadeExameMedico vem) {
        validadeExameMedico = vem;

        for (int i = 0; i < listaDepartamento.size(); i++) {
            if (validadeExameMedico.getDepartamento().getId().equals(Integer.valueOf(listaDepartamento.get(i).getDescription()))) {
                indexDepartamento = i;
                break;
            }
        }
    }

    public Integer getIndexDepartamento() {
        return indexDepartamento;
    }

    public void setIndexDepartamento(Integer indexDepartamento) {
        this.indexDepartamento = indexDepartamento;
    }

    public List<SelectItem> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<SelectItem> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public ValidadeExameMedico getValidadeExameMedico() {
        return validadeExameMedico;
    }

    public void setValidadeExameMedico(ValidadeExameMedico validadeExameMedico) {
        this.validadeExameMedico = validadeExameMedico;
    }

    public List<ValidadeExameMedico> getListaValidadeExameMedico() {
        return listaValidadeExameMedico;
    }

    public void setListaValidadeExameMedico(List<ValidadeExameMedico> listaValidadeExameMedico) {
        this.listaValidadeExameMedico = listaValidadeExameMedico;
    }

}
