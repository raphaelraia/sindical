package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.GrupoCidades;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.impressao.FichaSocial;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.FilialCidade;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FilialCidadeDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Download;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@ManagedBean(name = "socioCarteirinhaBean")
@SessionScoped
public class SocioCarteirinhaJSFBean {

    private SocioCarteirinha socioCarteirinha = new SocioCarteirinha();
    private boolean renderAdc = false;
    private boolean carregar = true;
    private boolean desabilitaCidade = false;
    private boolean desabilitaFilial = true;
    private String tipoPesCidades = "todos";
    private String tipoPesFilial = "todos";
    private int idListaCidades = 0;
    private int idListaFiliais = 0;
    private List listaSoc = new ArrayList();
    private List<SelectItem> listaCidades = new ArrayList<>();
    private List<SelectItem> listaFiliais = new ArrayList<>();

    public List<SelectItem> getListaCidades() {
        List<GrupoCidades> lista = (List<GrupoCidades>) new Dao().list(new GrupoCidades(), true);
        List<SelectItem> result = new ArrayList();
        if (tipoPesCidades.equals("especificas")) {
            for (int i = 0; i < lista.size(); i++) {
                result.add(new SelectItem(i,
                        lista.get(i).getCidade().getCidade() + " - " + lista.get(i).getCidade().getUf(),
                        Integer.toString(lista.get(i).getCidade().getId())));
            }
        }
        return result;
    }

    public List<SelectItem> getListaFiliais() {
        List<Filial> listaFilial = (List<Filial>) new Dao().list(new Filial(), true);
        List<SelectItem> result = new ArrayList();
        if (tipoPesFilial.equals("especificas")) {
            for (int i = 0; i < listaFilial.size(); i++) {
                result.add(new SelectItem(i,
                        listaFilial.get(i).getFilial().getPessoa().getDocumento() + " - " + listaFilial.get(i).getFilial().getPessoa().getNome(),
                        Integer.toString(listaFilial.get(i).getFilial().getPessoa().getId())));
            }
        }
        return result;
    }

    public List<Socios> getListaSocios() {
        SocioCarteirinhaDao db = new SocioCarteirinhaDao();
        FilialCidadeDao dbC = new FilialCidadeDao();
        PessoaEnderecoDao dbE = new PessoaEnderecoDao();
        FilialCidade filCidade;
        List result;
        if (listaSoc.isEmpty() && carregar) {
            if (Registro.get().isCarteirinhaDependente()) {
                result = db.pesquisaSocioSemCarteirinhaDependente();
            } else {
                result = db.pesquisaSocioSemCarteirinha();
            }
            for (int i = 0; i < result.size(); i++) {
                PessoaEndereco pesEnde = dbE.pesquisaEndPorPessoaTipo(((Socios) result.get(i)).getServicoPessoa().getPessoa().getId(), 1);
                if (pesEnde != null) {
                    filCidade = dbC.findPrincipal(pesEnde.getEndereco().getCidade().getId());
                } else {
                    filCidade = new FilialCidade();
                }

                listaSoc.add(new DataObject(true,
                        (Socios) result.get(i),
                        filCidade,
                        null,
                        null,
                        null));
            }
        }
        return listaSoc;
    }

    public String adicionarSocio() {
        for (Object listaSoc1 : listaSoc) {
            if (((Socios) ((DataObject) listaSoc1).getArgumento1()).getServicoPessoa().getId() == socioCarteirinha.getPessoa().getId()) {
                renderAdc = false;
                return "emissaoCarteirinha";
            }
        }
        FilialCidadeDao dbC = new FilialCidadeDao();
        PessoaEnderecoDao dbE = new PessoaEnderecoDao();
        PessoaEndereco pesEnde = dbE.pesquisaEndPorPessoaTipo(socioCarteirinha.getPessoa().getId(), 1);
        FilialCidade filCidade;
        if (pesEnde != null) {
            filCidade = dbC.findPrincipal(pesEnde.getEndereco().getCidade().getId());
        } else {
            filCidade = new FilialCidade();
        }
        listaSoc.add(new DataObject(true,
                socioCarteirinha.getPessoa(),
                filCidade,
                null,
                null,
                null));
        renderAdc = false;
        socioCarteirinha = new SocioCarteirinha();
        return "emissaoCarteirinha";
    }

    public String filtrarPorCidade() {
        List aux = new ArrayList();
        if (tipoPesCidades.equals("especificas")) {
            carregar = true;
            listaSoc.clear();
            getListaSocios();
            for (int i = 0; i < listaSoc.size(); i++) {
                if (((FilialCidade) ((DataObject) listaSoc.get(i)).getArgumento2()).getCidade().getId()
                        == Integer.parseInt(getListaCidades().get(idListaCidades).getDescription())) {
                    aux.add(listaSoc.get(i));
                }
            }
            listaSoc.clear();
            listaSoc.addAll(aux);
            if (listaSoc.isEmpty()) {
                carregar = false;
                return "emissaoCarteirinha";
            }
        } else {
            listaSoc.clear();
            carregar = true;
            getListaSocios();
        }
        return "emissaoCarteirinha";
    }

    public String filtrarPorFilial() {
        List aux = new ArrayList();
        if (tipoPesFilial.equals("especificas")) {
            carregar = true;
            listaSoc.clear();
            getListaSocios();
            for (int i = 0; i < listaSoc.size(); i++) {
                if (((FilialCidade) ((DataObject) listaSoc.get(i)).getArgumento2()).getFilial().getFilial().getPessoa().getId()
                        == Integer.parseInt(getListaFiliais().get(idListaFiliais).getDescription())) {
                    aux.add(listaSoc.get(i));
                }
            }
            listaSoc.clear();
            listaSoc.addAll(aux);
            if (listaSoc.isEmpty()) {
                carregar = false;
                return "emissaoCarteirinha";
            }
        } else {
            listaSoc.clear();
            carregar = true;
            getListaSocios();
        }
        return "emissaoCarteirinha";
    }

    public String habilitarCidade() {
        desabilitaFilial = true;
        desabilitaCidade = false;
        listaSoc.clear();
        carregar = true;
        tipoPesCidades = "todos";
        tipoPesFilial = "todos";
        return "emissaoCarteirinha";
    }

    public String habilitarFilial() {
        desabilitaFilial = false;
        desabilitaCidade = true;
        listaSoc.clear();
        carregar = true;
        tipoPesFilial = "todos";
        tipoPesCidades = "todos";
        return "emissaoCarteirinha";
    }

    public String visualizar() {
        if (Registro.get().isCarteirinhaDependente()) {
            imprimirCarteirinhaComDependente();
        } else {
            imprimirCarteirinhaSemDependente();
        }
        listaSoc.clear();
        carregar = true;
        getListaSocios();
        return null;
    }

    public void imprimirCarteirinhaComDependente() {
        Fisica fisica = new Fisica();
        Juridica sindicato = new Juridica();
        FisicaDao db = new FisicaDao();
        PessoaEndereco pesEndereco, pesDestinatario, pesEndEmpresa, pesEndSindicato = new PessoaEndereco();
        PessoaEnderecoDao dbEnd = new PessoaEnderecoDao();
        PessoaEmpresa pesEmpresa = new PessoaEmpresa();
        PessoaEmpresaDao dbEmp = new PessoaEmpresaDao();
        SociosDao dbSoc = new SociosDao();
        String dados[] = new String[34];
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            Collection listaSocios = new ArrayList();

            File fl = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper"));
            JasperReport jasper = (JasperReport) JRLoader.loadObject(fl);

            sindicato = (Juridica) new Dao().find(new Juridica(), 1);
            pesEndSindicato = dbEnd.pesquisaEndPorPessoaTipo(sindicato.getPessoa().getId(), 2);
            
            File logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPersonalizado.png"));

            if (!logo_cliente.exists()){
                logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
            }
            
            for (int i = 0; i < listaSoc.size(); i++) {
                if ((Boolean) ((DataObject) listaSoc.get(i)).getArgumento0()) {
                    fisica = db.pesquisaFisicaPorPessoa(((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getServicoPessoa().getPessoa().getId());
                    pesEndereco = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
                    pesEmpresa = dbEmp.pesquisaPessoaEmpresaPorFisica(fisica.getId());
                    if (pesEmpresa.getId() != -1) {
                        pesEndEmpresa = dbEnd.pesquisaEndPorPessoaTipo(pesEmpresa.getJuridica().getPessoa().getId(), 2);
                    } else {
                        pesEndEmpresa = new PessoaEndereco();
                    }

                    pesDestinatario = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);

                    try {
                        dados[0] = pesEndereco.getEndereco().getLogradouro().getDescricao();
                        dados[1] = pesEndereco.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[2] = pesEndereco.getNumero();
                        dados[3] = pesEndereco.getComplemento();
                        dados[4] = pesEndereco.getEndereco().getBairro().getDescricao();
                        dados[5] = pesEndereco.getEndereco().getCidade().getCidade();
                        dados[6] = pesEndereco.getEndereco().getCidade().getUf();
                        dados[7] = pesEndereco.getEndereco().getCep();
                    } catch (Exception e) {
                        dados[0] = "";
                        dados[1] = "";
                        dados[2] = "";
                        dados[3] = "";
                        dados[4] = "";
                        dados[5] = "";
                        dados[6] = "";
                        dados[7] = "";
                    }
                    try {
                        dados[8] = pesDestinatario.getEndereco().getLogradouro().getDescricao();
                        dados[9] = pesDestinatario.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[10] = pesDestinatario.getNumero();
                        dados[11] = pesDestinatario.getComplemento();
                        dados[12] = pesDestinatario.getEndereco().getBairro().getDescricao();
                        dados[13] = pesDestinatario.getEndereco().getCidade().getCidade();
                        dados[14] = pesDestinatario.getEndereco().getCidade().getUf();
                        dados[15] = pesDestinatario.getEndereco().getCep();
                        dados[26] = pesDestinatario.getPessoa().getDocumento();
                        dados[27] = pesDestinatario.getPessoa().getNome();
                    } catch (Exception e) {
                        dados[8] = "";
                        dados[9] = "";
                        dados[10] = "";
                        dados[11] = "";
                        dados[12] = "";
                        dados[13] = "";
                        dados[14] = "";
                        dados[15] = "";
                        dados[26] = "";
                        dados[27] = "";
                    }
                    try {
                        dados[16] = pesEmpresa.getJuridica().getPessoa().getNome();
                        dados[17] = pesEmpresa.getJuridica().getPessoa().getTelefone1();
                        dados[18] = pesEmpresa.getFuncao().getProfissao();
                        dados[19] = pesEndEmpresa.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[20] = pesEndEmpresa.getNumero();
                        dados[21] = pesEndEmpresa.getComplemento();
                        dados[22] = pesEndEmpresa.getEndereco().getBairro().getDescricao();
                        dados[23] = pesEndEmpresa.getEndereco().getCidade().getCidade();
                        dados[24] = pesEndEmpresa.getEndereco().getCidade().getUf();
                        dados[25] = pesEndEmpresa.getEndereco().getCep();
                        dados[28] = pesEmpresa.getAdmissao();
                        dados[29] = pesEmpresa.getJuridica().getPessoa().getDocumento();
                        dados[30] = pesEmpresa.getJuridica().getFantasia();
                        dados[31] = pesEndEmpresa.getEndereco().getLogradouro().getDescricao();
                        dados[32] = pesEmpresa.getCodigo();
                    } catch (Exception e) {
                        dados[16] = "";
                        dados[17] = "";
                        dados[18] = "";
                        dados[19] = "";
                        dados[20] = "";
                        dados[21] = "";
                        dados[22] = "";
                        dados[23] = "";
                        dados[24] = "";
                        dados[25] = "";
                        dados[28] = "";
                        dados[29] = "";
                        dados[30] = "";
                        dados[31] = "";
                        dados[32] = "";
                    }

                    try {
                        listaSocios.add(new FichaSocial(0,
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getId(),
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getNrMatricula(),
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getServicoPessoa().getEmissao(),
                                null,
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getCategoria().getGrupoCategoria().getGrupoCategoria(),
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getCategoria().getCategoria(),
                                fisica.getPessoa().getNome(),
                                fisica.getSexo(),
                                fisica.getNascimento(),
                                fisica.getNaturalidade(),
                                fisica.getNacionalidade(),
                                fisica.getRg(),
                                fisica.getPessoa().getDocumento(),
                                fisica.getCarteira(),
                                fisica.getSerie(),
                                fisica.getEstadoCivil(),
                                fisica.getPai(),
                                fisica.getMae(),
                                fisica.getPessoa().getTelefone1(),
                                fisica.getPessoa().getTelefone3(),
                                fisica.getPessoa().getEmail1(),
                                dados[0],
                                dados[1],
                                dados[2],
                                dados[3],
                                dados[4],
                                dados[5],
                                dados[6],
                                dados[7],
                                false,
                                dados[26],
                                dados[27],
                                dados[8],
                                dados[9],
                                dados[10],
                                dados[11],
                                dados[12],
                                dados[13],
                                dados[14],
                                dados[15],
                                dados[16],
                                dados[17],
                                null, // fax
                                dados[28],
                                dados[18],
                                dados[19],
                                dados[20],
                                dados[21],
                                dados[22],
                                dados[23],
                                dados[24],
                                dados[25],
                                logo_cliente.getAbsolutePath(),
                                "", // obs
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getParentesco().getParentesco(),
                                sindicato.getPessoa().getNome(),
                                pesEndSindicato.getEndereco().getDescricaoEndereco().getDescricao(),
                                pesEndSindicato.getNumero(),
                                pesEndSindicato.getComplemento(),
                                pesEndSindicato.getEndereco().getBairro().getDescricao(),
                                pesEndSindicato.getEndereco().getCidade().getCidade(),
                                pesEndSindicato.getEndereco().getCidade().getUf(),
                                pesEndSindicato.getEndereco().getCep(),
                                sindicato.getPessoa().getDocumento(),
                                "",
                                ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/LogoCliente.png"),
                                getFotoSocio(((Socios) ((DataObject) listaSoc.get(i)).getArgumento1())),
                                sindicato.getPessoa().getEmail1(),
                                sindicato.getPessoa().getSite(),
                                sindicato.getPessoa().getTelefone1(),
                                ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper"),
                                dados[29],
                                fisica.getPessoa().getRecadastroString(),
                                dados[30],
                                pesEndSindicato.getEndereco().getLogradouro().getDescricao(),
                                dados[31],
                                "",
                                dados[32],
                                fisica.getPis()
                        )
                        );

                        //List<Socios> deps = dbSoc.pesquisaDependentesOrdenado(fisica.getPessoa().getId());
                        List<Socios> deps = dbSoc.pesquisaDependentesOrdenado(0); // ID DA MATRICULA NAO DA PESSOA
                        for (int n = 0; n < deps.size(); n++) {
                            listaSocios.add(new FichaSocial(0,
                                    deps.get(n).getId(),
                                    ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getNrMatricula(),
                                    null,
                                    null,
                                    "",
                                    ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getCategoria().getCategoria(),
                                    deps.get(n).getServicoPessoa().getPessoa().getNome(),
                                    db.pesquisaFisicaPorPessoa(deps.get(n).getServicoPessoa().getPessoa().getId()).getSexo(),
                                    db.pesquisaFisicaPorPessoa(deps.get(n).getServicoPessoa().getPessoa().getId()).getNascimento(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    false,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    null,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    logo_cliente.getAbsolutePath(),
                                    "",
                                    deps.get(n).getParentesco().getParentesco(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/LogoCliente.png"),
                                    ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg"),
                                    "",
                                    "",
                                    "",
                                    ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper"),
                                    "",
                                    null, "", "", "", "", "", ""));
                        }
                    } catch (Exception erro) {
                        System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
                        continue;
                    }
                }
            }
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listaSocios);
            JasperPrint print = JasperFillManager.fillReport(
                    jasper,
                    null,
                    dtSource);
            byte[] arquivo = JasperExportManager.exportReportToPdf(print);
            response.setContentType("application/pdf");
            response.setContentLength(arquivo.length);
            ServletOutputStream saida = response.getOutputStream();
            saida.write(arquivo, 0, arquivo.length);
            saida.flush();
            saida.close();

            FacesContext.getCurrentInstance().responseComplete();
            Download download = new Download(
                    "Ficha Social " + fisica.getPessoa().getId() + ".pdf",
                    ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/socios.jsf"),
                    "application/pdf",
                    FacesContext.getCurrentInstance());
            download.baixar();
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public void imprimirCarteirinhaSemDependente() {
        Fisica fisica = new Fisica();
        Juridica sindicato = new Juridica();
        FisicaDao db = new FisicaDao();
        PessoaEndereco pesEndereco, pesDestinatario, pesEndEmpresa, pesEndSindicato = new PessoaEndereco();
        PessoaEnderecoDao dbEnd = new PessoaEnderecoDao();
        PessoaEmpresa pesEmpresa = new PessoaEmpresa();
        PessoaEmpresaDao dbEmp = new PessoaEmpresaDao();
        String dados[] = new String[32];
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            Collection listaSocios = new ArrayList();

            File fl = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper"));
            JasperReport jasper = (JasperReport) JRLoader.loadObject(fl);

            sindicato = (Juridica) new Dao().find(new Juridica(), 1);
            pesEndSindicato = dbEnd.pesquisaEndPorPessoaTipo(sindicato.getPessoa().getId(), 2);
            
            File logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPersonalizado.png"));
            
            if (!logo_cliente.exists()){
                logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
            }

            for (int i = 0; i < listaSoc.size(); i++) {
                if ((Boolean) ((DataObject) listaSoc.get(i)).getArgumento0()) {
                    fisica = db.pesquisaFisicaPorPessoa(((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getServicoPessoa().getPessoa().getId());
                    pesEndereco = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
                    pesEmpresa = dbEmp.pesquisaPessoaEmpresaPorFisica(fisica.getId());
                    if (pesEmpresa.getId() != -1) {
                        pesEndEmpresa = dbEnd.pesquisaEndPorPessoaTipo(pesEmpresa.getJuridica().getPessoa().getId(), 2);
                    } else {
                        pesEndEmpresa = new PessoaEndereco();
                    }

                    pesDestinatario = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);

                    try {
                        dados[0] = pesEndereco.getEndereco().getLogradouro().getDescricao();
                        dados[1] = pesEndereco.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[2] = pesEndereco.getNumero();
                        dados[3] = pesEndereco.getComplemento();
                        dados[4] = pesEndereco.getEndereco().getBairro().getDescricao();
                        dados[5] = pesEndereco.getEndereco().getCidade().getCidade();
                        dados[6] = pesEndereco.getEndereco().getCidade().getUf();
                        dados[7] = pesEndereco.getEndereco().getCep();
                    } catch (Exception e) {
                        dados[0] = "";
                        dados[1] = "";
                        dados[2] = "";
                        dados[3] = "";
                        dados[4] = "";
                        dados[5] = "";
                        dados[6] = "";
                        dados[7] = "";
                    }
                    try {
                        dados[8] = pesDestinatario.getEndereco().getLogradouro().getDescricao();
                        dados[9] = pesDestinatario.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[10] = pesDestinatario.getNumero();
                        dados[11] = pesDestinatario.getComplemento();
                        dados[12] = pesDestinatario.getEndereco().getBairro().getDescricao();
                        dados[13] = pesDestinatario.getEndereco().getCidade().getCidade();
                        dados[14] = pesDestinatario.getEndereco().getCidade().getUf();
                        dados[15] = pesDestinatario.getEndereco().getCep();
                        dados[26] = pesDestinatario.getPessoa().getDocumento();
                        dados[27] = pesDestinatario.getPessoa().getNome();
                    } catch (Exception e) {
                        dados[8] = "";
                        dados[9] = "";
                        dados[10] = "";
                        dados[11] = "";
                        dados[12] = "";
                        dados[13] = "";
                        dados[14] = "";
                        dados[15] = "";
                        dados[26] = "";
                        dados[27] = "";
                    }
                    try {
                        dados[16] = pesEmpresa.getJuridica().getPessoa().getNome();
                        dados[17] = pesEmpresa.getJuridica().getPessoa().getTelefone1();
                        dados[18] = pesEmpresa.getFuncao().getProfissao();
                        dados[19] = pesEndEmpresa.getEndereco().getDescricaoEndereco().getDescricao();
                        dados[20] = pesEndEmpresa.getNumero();
                        dados[21] = pesEndEmpresa.getComplemento();
                        dados[22] = pesEndEmpresa.getEndereco().getBairro().getDescricao();
                        dados[23] = pesEndEmpresa.getEndereco().getCidade().getCidade();
                        dados[24] = pesEndEmpresa.getEndereco().getCidade().getUf();
                        dados[25] = pesEndEmpresa.getEndereco().getCep();
                        dados[28] = pesEmpresa.getAdmissao();
                        dados[29] = pesEmpresa.getJuridica().getPessoa().getDocumento();
                        dados[30] = pesEmpresa.getJuridica().getFantasia();
                        dados[31] = pesEndEmpresa.getEndereco().getLogradouro().getDescricao();
                        dados[32] = pesEmpresa.getCodigo();
                    } catch (Exception e) {
                        dados[16] = "";
                        dados[17] = "";
                        dados[18] = "";
                        dados[19] = "";
                        dados[20] = "";
                        dados[21] = "";
                        dados[22] = "";
                        dados[23] = "";
                        dados[24] = "";
                        dados[25] = "";
                        dados[28] = "";
                        dados[29] = "";
                        dados[30] = "";
                        dados[31] = "";
                        dados[32] = "";
                    }

                    try {
                        listaSocios.add(new FichaSocial(0,
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getId(),
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getNrMatricula(),
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getServicoPessoa().getEmissao(),
                                null,
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getCategoria().getGrupoCategoria().getGrupoCategoria(),
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getMatriculaSocios().getCategoria().getCategoria(),
                                fisica.getPessoa().getNome(),
                                fisica.getSexo(),
                                fisica.getNascimento(),
                                fisica.getNaturalidade(),
                                fisica.getNacionalidade(),
                                fisica.getRg(),
                                fisica.getPessoa().getDocumento(),
                                fisica.getCarteira(),
                                fisica.getSerie(),
                                fisica.getEstadoCivil(),
                                fisica.getPai(),
                                fisica.getMae(),
                                fisica.getPessoa().getTelefone1(),
                                fisica.getPessoa().getTelefone3(),
                                fisica.getPessoa().getEmail1(),
                                dados[0],
                                dados[1],
                                dados[2],
                                dados[3],
                                dados[4],
                                dados[5],
                                dados[6],
                                dados[7],
                                false,
                                dados[26],
                                dados[27],
                                dados[8],
                                dados[9],
                                dados[10],
                                dados[11],
                                dados[12],
                                dados[13],
                                dados[14],
                                dados[15],
                                dados[16],
                                dados[17],
                                null, // fax
                                dados[28],
                                dados[18],
                                dados[19],
                                dados[20],
                                dados[21],
                                dados[22],
                                dados[23],
                                dados[24],
                                dados[25],
                                logo_cliente.getAbsolutePath(),
                                "", // obs
                                ((Socios) ((DataObject) listaSoc.get(i)).getArgumento1()).getParentesco().getParentesco(),
                                sindicato.getPessoa().getNome(),
                                pesEndSindicato.getEndereco().getDescricaoEndereco().getDescricao(),
                                pesEndSindicato.getNumero(),
                                pesEndSindicato.getComplemento(),
                                pesEndSindicato.getEndereco().getBairro().getDescricao(),
                                pesEndSindicato.getEndereco().getCidade().getCidade(),
                                pesEndSindicato.getEndereco().getCidade().getUf(),
                                pesEndSindicato.getEndereco().getCep(),
                                sindicato.getPessoa().getDocumento(),
                                "",
                                ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/LogoCliente.png"),
                                getFotoSocio(((Socios) ((DataObject) listaSoc.get(i)).getArgumento1())),
                                sindicato.getPessoa().getEmail1(),
                                sindicato.getPessoa().getSite(),
                                sindicato.getPessoa().getTelefone1(),
                                ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper"),
                                dados[29],
                                fisica.getPessoa().getRecadastroString(),
                                dados[30],
                                pesEndSindicato.getEndereco().getLogradouro().getDescricao(),
                                dados[31],
                                "",
                                dados[32],
                                fisica.getPis()
                        ));

                    } catch (Exception erro) {
                        System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
                        continue;
                    }
                }
            }
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(listaSocios);
            JasperPrint print = JasperFillManager.fillReport(
                    jasper,
                    null,
                    dtSource);
            byte[] arquivo = JasperExportManager.exportReportToPdf(print);
            response.setContentType("application/pdf");
            response.setContentLength(arquivo.length);
            ServletOutputStream saida = response.getOutputStream();
            saida.write(arquivo, 0, arquivo.length);
            saida.flush();
            saida.close();

            FacesContext.getCurrentInstance().responseComplete();
            Download download = new Download(
                    "Ficha Social " + fisica.getPessoa().getId() + ".pdf",
                    ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/socios.jsf"),
                    "application/pdf",
                    FacesContext.getCurrentInstance());
            download.baixar();
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public String getFotoSocio(Socios socios) {
        FacesContext context = FacesContext.getCurrentInstance();
        File files;
        if (socios.getId() != -1) {
            files = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/" + socios.getServicoPessoa().getPessoa().getId() + ".jpg"));
            if (files.exists()) {
                return files.getPath();
            } else {
                return ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg");
            }
        } else {
            return ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg");
        }
    }

    public SocioCarteirinha getSocioCarteirinha() {
        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("socioPesquisa") != null) {
            socioCarteirinha.setPessoa(((Socios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("socioPesquisa")).getServicoPessoa().getPessoa());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("socioPesquisa");
            renderAdc = true;
        }
        return socioCarteirinha;
    }

    public void setSocioCarteirinha(SocioCarteirinha socioCarteirinha) {
        this.socioCarteirinha = socioCarteirinha;
    }

    public boolean isRenderAdc() {
        return renderAdc;
    }

    public void setRenderAdc(boolean renderAdc) {
        this.renderAdc = renderAdc;
    }

    public String getTipoPesCidades() {
        return tipoPesCidades;
    }

    public void setTipoPesCidades(String tipoPesCidades) {
        this.tipoPesCidades = tipoPesCidades;
    }

    public String getTipoPesFilial() {
        return tipoPesFilial;
    }

    public void setTipoPesFilial(String tipoPesFilial) {
        this.tipoPesFilial = tipoPesFilial;
    }

    public int getIdListaCidades() {
        return idListaCidades;
    }

    public void setIdListaCidades(int idListaCidades) {
        this.idListaCidades = idListaCidades;
    }

    public int getIdListaFiliais() {
        return idListaFiliais;
    }

    public void setIdListaFiliais(int idListaFiliais) {
        this.idListaFiliais = idListaFiliais;
    }

    public boolean isDesabilitaCidade() {
        return desabilitaCidade;
    }

    public void setDesabilitaCidade(boolean desabilitaCidade) {
        this.desabilitaCidade = desabilitaCidade;
    }

    public boolean isDesabilitaFilial() {
        return desabilitaFilial;
    }

    public void setDesabilitaFilial(boolean desabilitaFilial) {
        this.desabilitaFilial = desabilitaFilial;
    }
}
