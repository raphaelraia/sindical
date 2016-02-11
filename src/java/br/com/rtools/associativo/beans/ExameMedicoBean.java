/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.ExameMedico;
import br.com.rtools.associativo.ValidadeExameMedico;
import br.com.rtools.associativo.dao.ExameMedicoDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class ExameMedicoBean implements Serializable {

    private List<LinhaValidadeExame> listaValidade = new ArrayList();
    private Fisica fisica = new Fisica();
    private List<ExameMedico> listaExameMedico = new ArrayList();
    private ExameMedico exameMedico = new ExameMedico();
    private String ids_departamento = "";

    public ExameMedicoBean() {
        String retorno = (String) GenericaSessao.getObject("urlRetorno");
        ids_departamento = (retorno.equals("menuAcademia") ? "11, 16" : "12");

        List<ValidadeExameMedico> result = new ExameMedicoDao().listaValidadeExameMedico(ids_departamento);

        for (ValidadeExameMedico linha_result : result) {
            listaValidade.add(
                    new LinhaValidadeExame(
                            retorno,
                            true,
                            linha_result
                    )
            );
        }
        
        loadListaExameMedico();
    }

    public final void loadListaExameMedico() {
        listaExameMedico.clear();

        ExameMedicoDao dao = new ExameMedicoDao();

        listaExameMedico = dao.listaExameMedico((fisica.getId() == -1 ? null : fisica.getPessoa().getId()), ids_departamento);
    }

    public void editar(ExameMedico em) {
        exameMedico = em;
    }

    public void salvar() {
        if (listaValidade.isEmpty()) {
            GenericaMensagem.warn("ATENÇÃO", "NENHUM DEPARTAMENTO COM VALIDADE FOI SELECIONADO!");
            return;
        }

        if (fisica.getId() == -1) {
            GenericaMensagem.warn("ATENÇÃO", "SELECIONE UMA PESSOA FÍSICA!");
            return;
        }
        Boolean salvo = false;
        ExameMedicoDao emDao = new ExameMedicoDao();
        Dao dao = new Dao();

        dao.openTransaction();
        for (LinhaValidadeExame lve : listaValidade) {
            Date data = emDao.pesquisaDataUltimoExame(fisica.getPessoa().getId(), lve.getVem().getDepartamento().getId());
            if (data == null || DataHoje.maiorData(DataHoje.dataHoje(), data)) {
                if (lve.getChkValidade()) {
                    ExameMedico em = new ExameMedico(
                            -1,
                            fisica.getPessoa(),
                            lve.getVem().getDepartamento(),
                            DataHoje.dataHoje(),
                            DataHoje.converte(new DataHoje().incrementarMeses(lve.vem.getMeses(), DataHoje.data()))
                    );

                    if (!dao.save(em)) {
                        dao.rollback();
                        GenericaMensagem.error("ATENÇÃO", "NÃO FOI POSSÍVEL SALVAR EXAME, TENTE NOVAMENTE!");
                        return;
                    }

                    salvo = true;
                }
            } else {
                GenericaMensagem.warn("ATENÇÃO", "EXAME MÉDICO (" + lve.getVem().getDepartamento().getDescricao() + ") JÁ CADASTRADO PARA ESTE PERÍODO, DATA " + DataHoje.converteData(data));
            }
        }

        if (salvo) {
            dao.commit();
            GenericaMensagem.info("SUCESSO", "EXAME MÉDICO SALVO!");
        } else {
            dao.rollback();
        }

        loadListaExameMedico();
    }
    
    public void excluir(){
        if (exameMedico.getId() != -1){
            Dao dao = new Dao();
            dao.openTransaction();
            if(!dao.delete(dao.find(exameMedico))){
                dao.rollback();
                GenericaMensagem.error("ERRO", "NÃO FOI POSSÍVEL EXCLUIR EXAME, TENTE NOVAMENTE!");
                return;
            }
            
            dao.commit();
            GenericaMensagem.info("SUCESSO", "EXAME DELETADO!");
            
            loadListaExameMedico();
        }
    }

    public void removerPessoa() {
        fisica = new Fisica();
        loadListaExameMedico();
    }

    public List<LinhaValidadeExame> getListaValidade() {
        return listaValidade;
    }

    public void setListaValidade(List<LinhaValidadeExame> listaValidade) {
        this.listaValidade = listaValidade;
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            loadListaExameMedico();
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public List<ExameMedico> getListaExameMedico() {
        return listaExameMedico;
    }

    public void setListaExameMedico(List<ExameMedico> listaExameMedico) {
        this.listaExameMedico = listaExameMedico;

    }

    public class LinhaValidadeExame {

        private String rotina;
        private Boolean chkValidade;
        private ValidadeExameMedico vem;

        public LinhaValidadeExame(String rotina, Boolean chkValidade, ValidadeExameMedico validadeExameMedico) {
            this.rotina = rotina;
            this.chkValidade = chkValidade;
            this.vem = validadeExameMedico;
        }

        public String getRotina() {
            return rotina;
        }

        public void setRotina(String rotina) {
            this.rotina = rotina;
        }

        public Boolean getChkValidade() {
            return chkValidade;
        }

        public void setChkValidade(Boolean chkValidade) {
            this.chkValidade = chkValidade;
        }

        public ValidadeExameMedico getVem() {
            return vem;
        }

        public void setVem(ValidadeExameMedico vem) {
            this.vem = vem;
        }
    }
}
