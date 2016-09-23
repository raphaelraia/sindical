package br.com.rtools.sistema.beans;

import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisEtiquetas;
import br.com.rtools.sistema.dao.SisEtiquetasDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GenericaString;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class SisEtiquetasBean implements Serializable {

    private SisEtiquetas sisEtiquetas;
    private List<SisEtiquetas> listSisEtiquetas;

    @PostConstruct
    public void init() {
        sisEtiquetas = new SisEtiquetas();
        listSisEtiquetas = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("sisEtiquetasBean");
        GenericaSessao.remove("usuarioPesquisa");
    }

    public void clear() {
        GenericaSessao.remove("sisEtiquetasBean");
    }

    public void save() {
        if (sisEtiquetas.getTitulo().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar titulo!");
            return;
        }
        if (sisEtiquetas.getDetalhes().isEmpty()) {
            GenericaMensagem.warn("Validação", "Descrever os detalhes!");
            return;
        }
        if (sisEtiquetas.getSql().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar código SQL!");
            return;
        }
        Dao dao = new Dao();
        if (sisEtiquetas.getId() == null) {
            if (dao.save(sisEtiquetas, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                listSisEtiquetas.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
            }
        } else if (dao.update(sisEtiquetas, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
            listSisEtiquetas.clear();
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void delete(SisEtiquetas se) {
        Dao dao = new Dao();
        sisEtiquetas = se;
        if (dao.delete(sisEtiquetas, true)) {
            GenericaMensagem.info("Sucesso", "Registro excluído");
            listSisEtiquetas.clear();
            sisEtiquetas = new SisEtiquetas();
            return;
        }
        GenericaMensagem.warn("Erro", "Ao excluir registro!");
    }

    public void edit(SisEtiquetas se) {
        sisEtiquetas = se;
    }

    public void print() {
        print(sisEtiquetas);
    }

    public void print(SisEtiquetas se) {
        SisEtiquetasDao sisEtiquetasDao = new SisEtiquetasDao();
        List list = sisEtiquetasDao.execute(se.getId());
        List<Etiquetas> c = new ArrayList<>();
        Etiquetas e = new Etiquetas();
        for (Object list1 : list) {
            try {
                e = new Etiquetas(
                        GenericaString.converterNullToString(((List) list1).get(0)), // Nome
                        GenericaString.converterNullToString(((List) list1).get(1)), // Logradouro
                        GenericaString.converterNullToString(((List) list1).get(2)), // Endereço
                        GenericaString.converterNullToString(((List) list1).get(3)), // Número
                        GenericaString.converterNullToString(((List) list1).get(4)), // Bairro
                        GenericaString.converterNullToString(((List) list1).get(5)), // Cidade
                        GenericaString.converterNullToString(((List) list1).get(6)), // UF
                        GenericaString.converterNullToString(((List) list1).get(7)), // Cep
                        GenericaString.converterNullToString(((List) list1).get(8)), // Complemento
                        GenericaString.converterNullToString(((List) list1).get(9)) /// Observação
                );
            } catch (Exception ex) {
                e = new Etiquetas(
                        GenericaString.converterNullToString(((List) list1).get(0)), // Nome
                        GenericaString.converterNullToString(((List) list1).get(1)), // Logradouro
                        GenericaString.converterNullToString(((List) list1).get(2)), // Endereço
                        GenericaString.converterNullToString(((List) list1).get(3)), // Número
                        GenericaString.converterNullToString(((List) list1).get(4)), // Bairro
                        GenericaString.converterNullToString(((List) list1).get(5)), // Cidade
                        GenericaString.converterNullToString(((List) list1).get(6)), // UF
                        GenericaString.converterNullToString(((List) list1).get(7)), // Cep               
                        GenericaString.converterNullToString(((List) list1).get(8)) /// Complemento
                );
            }
            c.add(e);
        }

        if (c.isEmpty()) {
            GenericaMensagem.info("Sistema", "Nenhum registro encontrado!");
            return;
        }

        Jasper.printReports(
                "/Relatorios/ETIQUETAS.jasper",
                "etiquetas",
                (Collection) c
        );
    }

    /**
     * *
     * Imprime endereços de uma lista do Objeto Pessoa
     *
     * @param listPessoas
     */
    public static void printList(List<Pessoa> listPessoas) {
        printList(listPessoas, 2);
    }

    /**
     * Imprime endereços de uma lista do Objeto Pessoa
     *
     * @param listPessoas
     * @param tipo_endereco_id
     */
    public static void printList(List<Pessoa> listPessoas, Integer tipo_endereco_id) {
        String in_pessoas = "";
        for (int i = 0; i < listPessoas.size(); i++) {
            if (i == 0) {
                in_pessoas = "" + listPessoas.get(i).getId();
            } else {
                in_pessoas += "," + listPessoas.get(i).getId();
            }
        }
        printIn(in_pessoas, tipo_endereco_id);
    }

    /**
     * Imprime endereços de uma lista de id pessoas
     *
     * @param in_pessoa_id
     */
    public static void printIn(List<Integer> in_pessoa_id) {
        printIn(in_pessoa_id, 2);
    }

    /**
     * Imprime endereços de uma lista de id pessoas
     *
     * @param in_pessoa_id
     * @param tipo_endereco_id
     */
    public static void printIn(List<Integer> in_pessoa_id, Integer tipo_endereco_id) {
        String in_pessoas = "";
        if (in_pessoa_id.isEmpty()) {
            GenericaMensagem.info("Sistema", "Nenhum endereço informado!");
            return;
        }
        for (int i = 0; i < in_pessoa_id.size(); i++) {
            if (i == 0) {
                in_pessoas = "" + in_pessoa_id.get(i);
            } else {
                in_pessoas += "," + in_pessoa_id.get(i);
            }
        }
        printIn(in_pessoas, tipo_endereco_id);
    }

    public static void printIn(String in_pessoas) {
        printIn(in_pessoas, 2);
    }

    public static void printIn(String in_pessoas, Integer tipo_endereco_id) {
        List list = new SisEtiquetasDao().findEnderecosByInPessoa(in_pessoas, tipo_endereco_id);
        List<Etiquetas> c = new ArrayList<>();
        Etiquetas e = new Etiquetas();
        for (Object list1 : list) {
            List o = (List) list1;
            try {
                e = new Etiquetas(
                        o.get(1), // Nome
                        o.get(4), // Logradouro
                        o.get(5), // Endereço
                        o.get(6), // Número
                        o.get(8), // Bairro
                        o.get(9), // Cidade
                        o.get(10), // UF
                        o.get(11), // Cep
                        o.get(7), // Complemento
                        "" // Observação
                );
            } catch (Exception ex) {

            }
            c.add(e);
        }

        if (c.isEmpty()) {
            GenericaMensagem.info("Sistema", "Nenhum registro encontrado!");
            return;
        }

        Jasper.printReports(
                "/Relatorios/ETIQUETAS.jasper",
                "etiquetas",
                (Collection) c
        );
    }

    public static void print(List<Etiquetas> listEtiquetas) {
        if (listEtiquetas.isEmpty()) {
            GenericaMensagem.info("Sistema", "Nenhum registro encontrado!");
            return;
        }

        Jasper.printReports(
                "/Relatorios/ETIQUETAS.jasper",
                "etiquetas",
                (Collection) listEtiquetas
        );
    }

    public SisEtiquetas getSisEtiquetas() {
        if (GenericaSessao.exists("usuarioPesquisa")) {
            sisEtiquetas.setSolicitante((Usuario) GenericaSessao.getObject("usuarioPesquisa", true));
        }
        return sisEtiquetas;
    }

    public void setSisEtiquetas(SisEtiquetas sisEtiquetas) {
        this.sisEtiquetas = sisEtiquetas;
    }

    public List<SisEtiquetas> getListSisEtiquetas() {
        if (listSisEtiquetas.isEmpty()) {
            SisEtiquetasDao sisEtiquetasDao = new SisEtiquetasDao();
            try {
                listSisEtiquetas = sisEtiquetasDao.findByUser(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
            } catch (Exception e) {

            }
        }
        return listSisEtiquetas;
    }

    public void setListSisEtiquetas(List<SisEtiquetas> listSisEtiquetas) {
        this.listSisEtiquetas = listSisEtiquetas;
    }

}
