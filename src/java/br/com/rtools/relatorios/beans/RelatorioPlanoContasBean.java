/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioPlanoContasDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class RelatorioPlanoContasBean implements Serializable {

    private List<SelectItem> listRelatorio = new ArrayList();
    private Integer indexListRelatorio = 0;

    public RelatorioPlanoContasBean() {
        loadListRelatorio();
    }

    public void print() {
        Relatorios rel = new RelatorioDao().pesquisaRelatorios(Integer.valueOf(listRelatorio.get(indexListRelatorio).getDescription()));

        List<Object> result = new RelatorioPlanoContasDao().list(rel);

        if (result.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }

        List<ObjectLista> lista = new ArrayList();
        for (Object obj : result) {
            List linha = (List) obj;
            lista.add(
                    new ObjectLista(linha.get(0), linha.get(1), linha.get(2), linha.get(3), linha.get(4), linha.get(5), linha.get(6), linha.get(7), linha.get(8), linha.get(9), linha.get(10), linha.get(11), linha.get(12), linha.get(13), linha.get(14), linha.get(15))
            );
        }

        Jasper.EXPORT_TO = true;
        Jasper.TYPE = "contabil";

        Jasper.printReports(rel.getJasper(), rel.getNome(), (Collection) lista);
    }

    public final void loadListRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
            }

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    indexListRelatorio = i;
                }

                listRelatorio.add(
                        new SelectItem(
                                i,
                                list.get(i).getNome(),
                                Integer.toString(list.get(i).getId())
                        )
                );
            }
        }
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public Integer getIndexListRelatorio() {
        return indexListRelatorio;
    }

    public void setIndexListRelatorio(Integer indexListRelatorio) {
        this.indexListRelatorio = indexListRelatorio;
    }

    public class ObjectLista {

        private Object acesso;
        private Object classificador;
        private Object codigo;
        private Object conta1;
        private Object conta2;
        private Object conta3;
        private Object conta4;
        private Object conta5;
        private Object acesso1;
        private Object acesso2;
        private Object acesso3;
        private Object acesso4;
        private Object classificador1;
        private Object classificador2;
        private Object classificador3;
        private Object classificador4;

        public ObjectLista(Object acesso, Object classificador, Object codigo, Object conta1, Object conta2, Object conta3, Object conta4, Object conta5, Object acesso1, Object acesso2, Object acesso3, Object acesso4, Object classificador1, Object classificador2, Object classificador3, Object classificador4) {
            this.acesso = acesso;
            this.classificador = classificador;
            this.codigo = codigo;
            this.conta1 = conta1;
            this.conta2 = conta2;
            this.conta3 = conta3;
            this.conta4 = conta4;
            this.conta5 = conta5;
            this.acesso1 = acesso1;
            this.acesso2 = acesso2;
            this.acesso3 = acesso3;
            this.acesso4 = acesso4;
            this.classificador1 = classificador1;
            this.classificador2 = classificador2;
            this.classificador3 = classificador3;
            this.classificador4 = classificador4;
        }
        
        public Object getAcesso() {
            return acesso;
        }

        public void setAcesso(Object acesso) {
            this.acesso = acesso;
        }

        public Object getClassificador() {
            return classificador;
        }

        public void setClassificador(Object classificador) {
            this.classificador = classificador;
        }

        public Object getCodigo() {
            return codigo;
        }

        public void setCodigo(Object codigo) {
            this.codigo = codigo;
        }

        public Object getConta1() {
            return conta1;
        }

        public void setConta1(Object conta1) {
            this.conta1 = conta1;
        }

        public Object getConta2() {
            return conta2;
        }

        public void setConta2(Object conta2) {
            this.conta2 = conta2;
        }

        public Object getConta3() {
            return conta3;
        }

        public void setConta3(Object conta3) {
            this.conta3 = conta3;
        }

        public Object getConta4() {
            return conta4;
        }

        public void setConta4(Object conta4) {
            this.conta4 = conta4;
        }

        public Object getConta5() {
            return conta5;
        }

        public void setConta5(Object conta5) {
            this.conta5 = conta5;
        }

        public Object getAcesso1() {
            return acesso1;
        }

        public void setAcesso1(Object acesso1) {
            this.acesso1 = acesso1;
        }

        public Object getAcesso2() {
            return acesso2;
        }

        public void setAcesso2(Object acesso2) {
            this.acesso2 = acesso2;
        }

        public Object getAcesso3() {
            return acesso3;
        }

        public void setAcesso3(Object acesso3) {
            this.acesso3 = acesso3;
        }

        public Object getAcesso4() {
            return acesso4;
        }

        public void setAcesso4(Object acesso4) {
            this.acesso4 = acesso4;
        }

        public Object getClassificador1() {
            return classificador1;
        }

        public void setClassificador1(Object classificador1) {
            this.classificador1 = classificador1;
        }

        public Object getClassificador2() {
            return classificador2;
        }

        public void setClassificador2(Object classificador2) {
            this.classificador2 = classificador2;
        }

        public Object getClassificador3() {
            return classificador3;
        }

        public void setClassificador3(Object classificador3) {
            this.classificador3 = classificador3;
        }

        public Object getClassificador4() {
            return classificador4;
        }

        public void setClassificador4(Object classificador4) {
            this.classificador4 = classificador4;
        }
    }
}
