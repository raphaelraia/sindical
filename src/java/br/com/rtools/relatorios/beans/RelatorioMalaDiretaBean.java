/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.relatorios.beans;

import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.impressao.ParametroMalaDireta;
import br.com.rtools.pessoa.MalaDiretaGrupo;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioMalaDiretaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaString;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
public class RelatorioMalaDiretaBean implements Serializable {

    private List<SelectItem> listaRelatorio = new ArrayList();
    private Integer idRelatorio = 0;

    private List<SelectItem> listaMalaDiretaGrupo = new ArrayList();
    private Integer idMalaDiretaGrupo = 0;
    private List<ParametroMalaDireta> parametroMalaDiretas;
    private List<Etiquetas> parametroEtiquetas;

    public RelatorioMalaDiretaBean() {
        loadRelatorio();
        loadMalaDiretaGrupo();
        parametroMalaDiretas = new ArrayList();
    }

    public void filter() {
        parametroMalaDiretas = new ArrayList();
        parametroEtiquetas = new ArrayList();
        Relatorios relatorio = (Relatorios) new Dao().find(new Relatorios(), Integer.valueOf(listaRelatorio.get(idRelatorio).getDescription()));
        List<ArrayList> list = new RelatorioMalaDiretaDao().listaMalaDireta(Integer.valueOf(listaMalaDiretaGrupo.get(idMalaDiretaGrupo).getDescription()));

        if (list.isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum registro encontrado!");
            return;
        }

        for (List l : list) {
            if (relatorio.getId() != 4) {
                parametroMalaDiretas.add(
                        new ParametroMalaDireta(
                                false,
                                l.get(0), // GRUPO
                                l.get(1), // DOCUMENTO
                                l.get(2), // NOME
                                l.get(3), // LOGRADOURO
                                l.get(4), // ENDERECO
                                l.get(5), // NUMERO
                                l.get(6), // COMPLEMENTO
                                l.get(7), // BAIRRO
                                l.get(8), // CIDADE
                                l.get(9), // UF
                                l.get(10), // CEP
                                l.get(11), // TELEFONE 1
                                l.get(12), // TELEFONE 2
                                l.get(13), // TELEFONE 3
                                l.get(14), // EMAIL 1
                                l.get(15), // EMAIL 2
                                l.get(16) // EMAIL 3
                        )
                );
            } else {
                // ETIQUETA
                parametroEtiquetas.add(
                        new Etiquetas(
                                false,
                                GenericaString.converterNullToString(l.get(2)), // Nome
                                GenericaString.converterNullToString(l.get(3)), // Logradouro
                                GenericaString.converterNullToString(l.get(4)), // Endereço
                                GenericaString.converterNullToString(l.get(5)), // Número
                                GenericaString.converterNullToString(l.get(7)), // Bairro
                                GenericaString.converterNullToString(l.get(8)), // Cidade
                                GenericaString.converterNullToString(l.get(9)), // UF
                                GenericaString.converterNullToString(l.get(10)), // Cep
                                GenericaString.converterNullToString(l.get(6)) // Complemento
                        )
                );
            }
        }
    }

    public void print() {
        Relatorios relatorio = (Relatorios) new Dao().find(new Relatorios(), Integer.valueOf(listaRelatorio.get(idRelatorio).getDescription()));

        Collection lp = new ArrayList();

        if (relatorio.getId() != 4) {
            if (!parametroMalaDiretas.isEmpty()) {
                for (int i = 0; i < parametroMalaDiretas.size(); i++) {
                    if (parametroMalaDiretas.get(i).getSelected()) {
                        lp.add(parametroMalaDiretas.get(i));
                    }
                }
            }

        } else {
            if (!parametroEtiquetas.isEmpty()) {
                for (int i = 0; i < parametroEtiquetas.size(); i++) {
                    if (parametroEtiquetas.get(i).getSelected()) {
                        lp.add(parametroEtiquetas.get(i));
                    }
                }
            }
        }

        if (lp.isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum registro selecionado!");
            return;
        }

        if (!lp.isEmpty()) {
            if (relatorio.getId() != 4) {
                Jasper.IS_HEADER = true;
                Jasper.printReports(relatorio.getJasper(), "Mala Direta", lp);
            } else {
                Jasper.printReports("/Relatorios/ETIQUETAS.jasper", "Etiquetas", lp);
            }
        }
    }

    public void printAll() {

        Relatorios relatorio = (Relatorios) new Dao().find(new Relatorios(), Integer.valueOf(listaRelatorio.get(idRelatorio).getDescription()));

        List<ArrayList> lista = new RelatorioMalaDiretaDao().listaMalaDireta(Integer.valueOf(listaMalaDiretaGrupo.get(idMalaDiretaGrupo).getDescription()));
        Collection lp = new ArrayList();

        if (!lista.isEmpty()) {
            for (List l : lista) {
                if (relatorio.getId() != 4) {
                    lp.add(
                            new ParametroMalaDireta(
                                    false,
                                    l.get(0), // GRUPO
                                    l.get(1), // DOCUMENTO
                                    l.get(2), // NOME
                                    l.get(3), // LOGRADOURO
                                    l.get(4), // ENDERECO
                                    l.get(5), // NUMERO
                                    l.get(6), // COMPLEMENTO
                                    l.get(7), // BAIRRO
                                    l.get(8), // CIDADE
                                    l.get(9), // UF
                                    l.get(10), // CEP
                                    l.get(11), // TELEFONE 1
                                    l.get(12), // TELEFONE 2
                                    l.get(13), // TELEFONE 3
                                    l.get(14), // EMAIL 1
                                    l.get(15), // EMAIL 2
                                    l.get(16) // EMAIL 3
                            )
                    );
                } else {
                    // ETIQUETA
                    lp.add(
                            new Etiquetas(
                                    GenericaString.converterNullToString(l.get(2)), // Nome
                                    GenericaString.converterNullToString(l.get(3)), // Logradouro
                                    GenericaString.converterNullToString(l.get(4)), // Endereço
                                    GenericaString.converterNullToString(l.get(5)), // Número
                                    GenericaString.converterNullToString(l.get(7)), // Bairro
                                    GenericaString.converterNullToString(l.get(8)), // Cidade
                                    GenericaString.converterNullToString(l.get(9)), // UF
                                    GenericaString.converterNullToString(l.get(10)), // Cep
                                    GenericaString.converterNullToString(l.get(6)) // Complemento
                            )
                    );
                }
            }
        }

        if (lp.isEmpty()) {
            GenericaMensagem.warn("Validação", "Nenhum registro encontrado!");
            return;
        }

        if (!lp.isEmpty()) {
            if (relatorio.getId() != 4) {
                Jasper.IS_HEADER = true;
                Jasper.printReports(relatorio.getJasper(), "Mala Direta", lp);
            } else {
                Jasper.printReports("/Relatorios/ETIQUETAS.jasper", "Etiquetas", lp);
            }
        }
    }

    public final void loadRelatorio() {
        if (listaRelatorio.isEmpty()) {
            List<Relatorios> list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(356);
            Integer index = 0;
            while (index < list.size()) {
                listaRelatorio.add(
                        new SelectItem(
                                index,
                                list.get(index).getNome(),
                                Integer.toString(list.get(index).getId())
                        )
                );
                index++;
            }

            listaRelatorio.add(
                    new SelectItem(
                            index,
                            "Etiquetas",
                            "4"
                    )
            );
        }
    }

    public final void loadMalaDiretaGrupo() {
        listaMalaDiretaGrupo.clear();

        List<MalaDiretaGrupo> list = (List<MalaDiretaGrupo>) new Dao().list(new MalaDiretaGrupo(), true);

        listaMalaDiretaGrupo.add(new SelectItem(0, "TODOS", "-1"));
        for (int i = 0; i < list.size(); i++) {
            listaMalaDiretaGrupo.add(
                    new SelectItem(
                            i + 1,
                            list.get(i).getDescricao(),
                            Integer.toString(list.get(i).getId())
                    )
            );
        }

    }

    public List<SelectItem> getListaRelatorio() {
        return listaRelatorio;
    }

    public void setListaRelatorio(List<SelectItem> listaRelatorio) {
        this.listaRelatorio = listaRelatorio;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public List<SelectItem> getListaMalaDiretaGrupo() {
        return listaMalaDiretaGrupo;
    }

    public void setListaMalaDiretaGrupo(List<SelectItem> listaMalaDiretaGrupo) {
        this.listaMalaDiretaGrupo = listaMalaDiretaGrupo;
    }

    public Integer getIdMalaDiretaGrupo() {
        return idMalaDiretaGrupo;
    }

    public void setIdMalaDiretaGrupo(Integer idMalaDiretaGrupo) {
        this.idMalaDiretaGrupo = idMalaDiretaGrupo;
    }

    public List<ParametroMalaDireta> getParametroMalaDiretas() {
        return parametroMalaDiretas;
    }

    public void setParametroMalaDiretas(List<ParametroMalaDireta> parametroMalaDiretas) {
        this.parametroMalaDiretas = parametroMalaDiretas;
    }

    public List<Etiquetas> getParametroEtiquetas() {
        return parametroEtiquetas;
    }

    public void setParametroEtiquetas(List<Etiquetas> parametroEtiquetas) {
        this.parametroEtiquetas = parametroEtiquetas;
    }

    public Relatorios getRelatorios() {
        return (Relatorios) new Dao().find(new Relatorios(), Integer.valueOf(listaRelatorio.get(idRelatorio).getDescription()));
    }
}
