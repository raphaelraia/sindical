package br.com.rtools.sistema.beans;

import br.com.rtools.impressao.Carta;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.SisCarta;
import br.com.rtools.sistema.dao.SisCartaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean
@SessionScoped
public class SisCartaBean implements Serializable {

    private SisCarta sisCarta;
    private List<SisCarta> listSisCartas;

    @PostConstruct
    public void init() {
        sisCarta = new SisCarta();
        listSisCartas = new ArrayList<>();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("sisCartaBean");
        GenericaSessao.remove("usuarioPesquisa");
    }

    public void clear() {
        GenericaSessao.remove("sisCartaBean");
    }

    public void save() {
        if (sisCarta.getTitulo().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar titulo!");
            return;
        }
        if (sisCarta.getDetalhes().isEmpty()) {
            GenericaMensagem.warn("Validação", "Descrever os detalhes!");
            return;
        }
        if (sisCarta.getSql().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar código SQL!");
            return;
        }
        Dao dao = new Dao();
        if (sisCarta.getId() == null) {
            if (dao.save(sisCarta, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                listSisCartas.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
            }
        } else if (dao.update(sisCarta, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
            listSisCartas.clear();
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar registro!");
        }
    }

    public void delete(SisCarta c) {
        Dao dao = new Dao();
        sisCarta = c;
        if (dao.delete(sisCarta, true)) {
            GenericaMensagem.info("Sucesso", "Registro excluído");
            listSisCartas.clear();
            sisCarta = new SisCarta();
            return;
        }
        GenericaMensagem.warn("Erro", "Ao excluir registro!");
    }

    public void edit(SisCarta sc) {
        sisCarta = sc;
    }

    public void print() {
        print(sisCarta);
    }

    public void print(SisCarta sisCarta) {
        try {
            SisCartaDao sisCartaDao = new SisCartaDao();
            List<PessoaEndereco> listPessoaEndereco = sisCartaDao.findEnderecosByInPessoa(sisCarta.getSql(), 1);
            PessoaEndereco pe = Registro.get().getFilial().getPessoa().getPessoaEndereco();
            JasperReport jasperCarta = (JasperReport) Jasper.load("CARTA.jasper");
            JasperReport jasperVerso = (JasperReport) Jasper.load("CARTA_VERSO.jasper");
            Jasper jasper = new Jasper();
            jasper.start();
            // Jasper jasper = new Jasper();
            for (int i = 0; i < listPessoaEndereco.size(); i++) {
                try {
                    List<Carta> c = new ArrayList<>();
                    Carta carta = new Carta();
                    carta = new Carta(
                            sisCarta.getTitulo(), // titulo
                            "Texto", // Texto
                            "Assinatura", // Assinatura
                            "Rodapé", // Rodapé
                            ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // Logo
                            pe.getPessoa().getNome(), // Remetente Nome
                            pe.getEnderecoCompletoString(), // Remetente Endereço
                            pe.getComplemento(), // Remetente Complemento
                            listPessoaEndereco.get(i).getPessoa().getNome(), // Destinatário Nome
                            listPessoaEndereco.get(i).getEnderecoCompletoString(), // Destinatário Endereço
                            listPessoaEndereco.get(i).getComplemento() // Destinatário Complemento
                    );
                    c.add(carta);
                    jasper.add(jasperCarta, c);
                    jasper.add(jasperVerso, c);
                } catch (Exception e2) {
                    e2.getMessage();
                }

            }
            Jasper.PART_NAME = "";
            jasper.finish();
//            if (!jasperPrintList.isEmpty()) {
//                JRPdfExporter exporter = new JRPdfExporter();
//                ByteArrayOutputStream retorno = new ByteArrayOutputStream();
//
//                exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
//                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
//                exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
//                exporter.exportReport();
//
//                byte[] arquivo = retorno.toByteArray();
//                if (arquivo.length > 0) {
//                    try {
//                        HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//                        res.setContentType("application/pdf");
//                        res.setHeader("Content-disposition", "inline; filename=\"" + "carta_simplesinha_do_patrao" + ".pdf\"");
//                        res.getOutputStream().write(arquivo);
//                        res.getCharacterEncoding();
//                        FacesContext.getCurrentInstance().responseComplete();
//                    } catch (Exception e) {
//                        e.getMessage();
//                    }
//                }
//                //dao.commit();
//            }
//            Jasper.EXPORT_TYPE = "pdf";
//            Jasper.PART_NAME = "";
//            Jasper.printReports("carta", jasperPrintList);
        } catch (Exception e) {
            e.getMessage();
        }
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
        List list = new SisCartaDao().findEnderecosByInPessoa(in_pessoas, tipo_endereco_id);
        List<Carta> c = new ArrayList<>();
        Carta carta = new Carta();
        for (Object list1 : list) {
            List o = (List) list1;
            carta = new Carta(
                    "Carta/ Secret.", // titulo
                    "Texto", // Texto
                    "Assinatura", // Assinatura
                    "Rodapé", // Rodapé
                    "Logo", // Logo
                    o.get(5), // Remetente Nome
                    o.get(6), // Remetente Endereço
                    o.get(7), // Remetente Complemento
                    o.get(7), // Destinatário Nome
                    o.get(7), // Destinatário Endereço
                    o.get(7) // Destinatário Complemento
            );
            c.add(carta);
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

    public static void print(List<Carta> listEtiquetas) {
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

    public SisCarta getSisCarta() {
        if (GenericaSessao.exists("usuarioPesquisa")) {
            sisCarta.setSolicitante((Usuario) GenericaSessao.getObject("usuarioPesquisa", true));
        }
        return sisCarta;
    }

    public void setSisCarta(SisCarta sisCarta) {
        this.sisCarta = sisCarta;
    }

    public List<SisCarta> getListSisCartas() {
        if (listSisCartas.isEmpty()) {
            SisCartaDao sisCartaDao = new SisCartaDao();
            try {
                listSisCartas = sisCartaDao.findByUser(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId());
            } catch (Exception e) {

            }
        }
        return listSisCartas;
    }

    public void setListSisCartas(List<SisCarta> listSisCartas) {
        this.listSisCartas = listSisCartas;
    }

}