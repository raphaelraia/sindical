package br.com.rtools.relatorios.beans;

import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.impressao.FichaSocial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Messages;
import br.com.rtools.utilitarios.Sessions;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@ManagedBean
@SessionScoped
public class FichaSocialEmpresaBean implements Serializable {

    private Juridica empresa;
    private List<Juridica> listEmpresa;
    private List<Filters> filters;

    public FichaSocialEmpresaBean() {
        empresa = new Juridica();
        listEmpresa = new ArrayList();
        loadFilters();
    }

    public void clear() {
        GenericaSessao.remove("fichaSocialEmpresaBean");
    }

    public void listener(String tcase) {

    }

    public void addEmpresa() {
        if (empresa != null && empresa.getId() != -1) {
            listEmpresa.add(empresa);
        }
        empresa = new Juridica();
    }

    public void removeEmpresa(Juridica j) {
        if (listEmpresa.remove(j));
    }

    public final void loadFilters() {
        filters = new ArrayList<>();
        filters.add(new Filters("empresa", "Empresa", true, true));
    }

    public void close(Filters filter) {
        filter.setActive(!filter.getActive());
        load(filter);
    }

    public void close(String filter) {
        Filters f = new Filters();
        f.setKey(filter);
        f.setActive(false);
        for (Filters f2 : filters) {
            if (f2.getKey().equals(filter)) {
                f2.setActive(true);
            }
        }
        load(f);
    }

    public void load(Filters filter) {
        switch (filter.getKey()) {

            case "juridica":
                listEmpresa = new ArrayList();
                break;
        }
    }

    public void print() {

        Jasper.load();
        List ljasper = new ArrayList();
        List ljasper2 = new ArrayList();
        String[] imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};

        List<FichaSocial> listFichaSocial = new ArrayList();

        FichaSocial fs = new FichaSocial();

        List<Socios> listSocios;

        if (listEmpresa.isEmpty()) {
            if (empresa.getId() == -1) {
                return;
            }
            listSocios = new SociosDao().findBySociosByInEmpresa(empresa.getId() + "");
        } else {
            listSocios = new SociosDao().findBySociosByInEmpresa(inIdEmpresas());
        }

        if (listSocios.isEmpty()) {
            Messages.warn("Sistema", "Nenhum registro encontrado!");
            return;
        }

        Dao dao = new Dao();
        Registro registro = (Registro) dao.find(new Registro(), 1);

        String path = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper";
        String pathVerso = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTROVERSO.jasper";

        for (int i = 0; i < listSocios.size(); i++) {

            Fisica fisica;
            Juridica sindicato;
            FisicaDao db = new FisicaDao();
            PessoaEndereco pesEndereco, pesDestinatario, pesEndEmpresa, pesEndSindicato;
            PessoaEnderecoDao dbEnd = new PessoaEnderecoDao();
            SociosDao dbSoc = new SociosDao();
            FacesContext faces = FacesContext.getCurrentInstance();

            Collection listaSocios = new ArrayList();
            fisica = db.pesquisaFisicaPorPessoa(listSocios.get(i).getServicoPessoa().getPessoa().getId());
            pesEndereco = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
            sindicato = (Juridica) dao.find(new Juridica(), 1);

            File logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPersonalizado.png"));

            if (!logo_cliente.exists()) {
                logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
            }

            String fotoSocio = "";

            for (String imagensTipo1 : imagensTipo) {
                File test = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + listSocios.get(i).getServicoPessoa().getPessoa().getId() + "/" + listSocios.get(i).getServicoPessoa().getPessoa().getFisica().getFoto() + "." + imagensTipo1);
                if (test.exists()) {
                    fotoSocio = test.getAbsolutePath();
                    break;
                }
            }

            PessoaEmpresa pessoaEmpresa = new PessoaEmpresaDao().pesquisaPessoaEmpresaPorFisica(fisica.getId());;

            if (pessoaEmpresa != null) {
                if (pessoaEmpresa.getId() != -1) {
                    pesEndEmpresa = dbEnd.pesquisaEndPorPessoaTipo(pessoaEmpresa.getJuridica().getPessoa().getId(), 2);
                } else {
                    pesEndEmpresa = dbEnd.pesquisaEndPorPessoaTipo(pessoaEmpresa.getJuridica().getPessoa().getId(), 2);
                }
            } else {
                pesEndEmpresa = new PessoaEndereco();
            }

            pesEndSindicato = dbEnd.pesquisaEndPorPessoaTipo(sindicato.getPessoa().getId(), 2);
            pesDestinatario = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
            String dados[] = new String[34];
            try {
                dados[0] = pesEndereco.getEndereco().getLogradouro().getDescricao();
                dados[1] = pesEndereco.getEndereco().getDescricaoEndereco().getDescricao();
                dados[2] = pesEndereco.getNumero();
                dados[3] = pesEndereco.getComplemento();
                dados[4] = pesEndereco.getEndereco().getBairro().getDescricao();
                dados[5] = pesEndereco.getEndereco().getCidade().getCidade();
                dados[6] = pesEndereco.getEndereco().getCidade().getUf();
                dados[7] = AnaliseString.mascaraCep(pesEndereco.getEndereco().getCep());
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
                dados[15] = AnaliseString.mascaraCep(pesDestinatario.getEndereco().getCep());
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
                dados[16] = pessoaEmpresa.getJuridica().getPessoa().getNome();
                dados[17] = pessoaEmpresa.getJuridica().getPessoa().getTelefone1();
                if (pessoaEmpresa.getFuncao() == null) {
                    dados[18] = "";
                } else {
                    dados[18] = pessoaEmpresa.getFuncao().getProfissao();
                }
                dados[19] = pesEndEmpresa.getEndereco().getDescricaoEndereco().getDescricao();
                dados[20] = pesEndEmpresa.getNumero();
                dados[21] = pesEndEmpresa.getComplemento();
                dados[22] = pesEndEmpresa.getEndereco().getBairro().getDescricao();
                dados[23] = pesEndEmpresa.getEndereco().getCidade().getCidade();
                dados[24] = pesEndEmpresa.getEndereco().getCidade().getUf();
                dados[25] = AnaliseString.mascaraCep(pesEndEmpresa.getEndereco().getCep());
                dados[28] = pessoaEmpresa.getAdmissao();
                dados[29] = pessoaEmpresa.getJuridica().getPessoa().getDocumento();
                dados[30] = pessoaEmpresa.getJuridica().getFantasia();
                dados[31] = pesEndEmpresa.getEndereco().getLogradouro().getDescricao();
                dados[32] = pessoaEmpresa.getCodigo();
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
            String assinatura = "";
            File f = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/assinatura.jpg"));
            if (f.exists()) {
                assinatura = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/assinatura.jpg");
            }
            try {
                String recadastro = fisica.getPessoa().getRecadastroString();
                listaSocios.add(new FichaSocial(0,
                        listSocios.get(i).getMatriculaSocios().getTitular().getId(),
                        listSocios.get(i).getMatriculaSocios().getNrMatricula(),
                        listSocios.get(i).getMatriculaSocios().getEmissao(),
                        recadastro,
                        listSocios.get(i).getMatriculaSocios().getCategoria().getGrupoCategoria().getGrupoCategoria(),
                        listSocios.get(i).getMatriculaSocios().getCategoria().getCategoria(),
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
                        "", // fax
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
                        registro.getFichaSocial(), // obs
                        listSocios.get(i).getParentesco().getParentesco(),
                        sindicato.getPessoa().getNome(),
                        pesEndSindicato.getEndereco().getDescricaoEndereco().getDescricao(),
                        pesEndSindicato.getNumero(),
                        pesEndSindicato.getComplemento(),
                        pesEndSindicato.getEndereco().getBairro().getDescricao(),
                        pesEndSindicato.getEndereco().getCidade().getCidade(),
                        pesEndSindicato.getEndereco().getCidade().getUf(),
                        AnaliseString.mascaraCep(pesEndSindicato.getEndereco().getCep()),
                        sindicato.getPessoa().getDocumento(),
                        "",
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                        fotoSocio,
                        sindicato.getPessoa().getEmail1(),
                        sindicato.getPessoa().getSite(),
                        sindicato.getPessoa().getTelefone1(),
                        "", // ((ServletContext) faces.getExternalContext().getContext()).getRealPath(pathVerso),
                        dados[29],
                        fisica.getPessoa().getRecadastroString(),
                        dados[30],
                        pesEndSindicato.getEndereco().getLogradouro().getDescricao(),
                        dados[31],
                        assinatura,
                        dados[32],
                        fisica.getPis()
                ));

                List<Socios> deps = dbSoc.pesquisaDependentesOrdenado(listSocios.get(i).getMatriculaSocios().getId());
                for (int n = 0; n < deps.size(); n++) {
                    listaSocios.add(
                            new FichaSocial(0,
                                    deps.get(n).getServicoPessoa().getPessoa().getId(),
                                    listSocios.get(i).getMatriculaSocios().getNrMatricula(),
                                    "",
                                    "",
                                    "",
                                    listSocios.get(i).getMatriculaSocios().getCategoria().getCategoria(),
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
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    logo_cliente.getAbsolutePath(),
                                    registro.getFichaSocial(), // obs
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
                                    ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                                    ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg"),
                                    "",
                                    "",
                                    "",
                                    "", //((ServletContext) faces.getExternalContext().getContext()).getRealPath(pathVerso),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    assinatura,
                                    "",
                                    "")
                    );
                }
                if (listaSocios.isEmpty()) {
                    return;
                }

                Jasper.PATH = "downloads";
                Jasper.PART_NAME = "";

                if (listSocios.get(i).getMatriculaSocios().getCategoria().getFichaSocial()) {
                    File ficha_s = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(path));

                    if (ficha_s.exists()) {
                        JasperReport jasper_1 = (JasperReport) JRLoader.loadObject(ficha_s);
                        JRBeanCollectionDataSource dtSource_1 = new JRBeanCollectionDataSource(listaSocios);
                        ljasper.add(Jasper.fillObject(jasper_1, null, dtSource_1));
                    }
                }
//                if (listSocios.get(i).getMatriculaSocios().getCategoria().getFichaFiliacao()) {
//                    File ficha_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHA_DE_SINDICALIZACAO.jasper"));
//
//                    if (ficha_f.exists()) {
//                        JasperReport jasper_2 = (JasperReport) JRLoader.loadObject(ficha_f);
//                        JRBeanCollectionDataSource dtSource_2 = new JRBeanCollectionDataSource(listaSocios);
//                        ljasper.add(Jasper.fillObject(jasper_2, null, dtSource_2));
//                    }
//
//                }

            } catch (JRException erro) {
                System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
            }
        }

//        
        Jasper.TYPE = "default";
        // Jasper.TITLE = relatorios.getNome();
        Map map = new HashMap();
        Jasper.printReports("ficha_social", ljasper);
    }

    public Boolean getShow(String filtro) {
        try {
            for (Filters f : filters) {
                if (f.getKey().equals(filtro)) {
                    if (f.getActive()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public String inIdEmpresas() {
        String ids = null;
        for (int i = 0; i < listEmpresa.size(); i++) {
            if (i == 0) {
                ids = "" + listEmpresa.get(i).getId();
            } else {
                ids += "," + listEmpresa.get(i).getId();
            }
        }
        return ids;
    }

    public List<Filters> getFilters() {
        return filters;
    }

    public void setFilters(List<Filters> filters) {
        this.filters = filters;
    }

    public Juridica getEmpresa() {
        if (Sessions.exists("juridicaPesquisa")) {
            empresa = (Juridica) Sessions.getObject("juridicaPesquisa", true);
        }
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public List<Juridica> getListEmpresa() {
        return listEmpresa;
    }

    public void setListEmpresa(List<Juridica> listEmpresa) {
        this.listEmpresa = listEmpresa;
    }

}
