package br.com.rtools.utilitarios;

import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.MatriculaSociosDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.impressao.CartaoSocial;
import br.com.rtools.impressao.FichaSocial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class ImpressaoParaSocios {

    private List<ObjectCard> listObjectCard = new ArrayList();

    public static boolean imprimirCarteirinha(List listaCartao) {
        return imprimirCarteirinha(listaCartao, null);
    }

    public static boolean imprimirCarteirinha(List listaCartao, Reports r) {
        Reports reports = new Reports();
        if (r != null) {
            reports = r;
        }
        List<ModeloCarteirinha> listaModelo = new Dao().list(new ModeloCarteirinha());
        Map<Integer, List> hash = new HashMap();
        SociosDao sociosDao = new SociosDao();
        String titular;
        String dependente;
        String orgaoOrigem = "";
        String codigoFuncional = "";
        Socios socioDependente;

        Map<String, String> modelsCard = new LinkedHashMap<>();
        for (int i = 0; i < listaCartao.size(); i++) {
            modelsCard.put((String) ((List) listaCartao.get(i)).get(20), (String) ((List) listaCartao.get(i)).get(20));
        }
        List[] listCartao2 = new ArrayList[modelsCard.size()];
        List<CartaoSocial>[] listax = new ArrayList[modelsCard.size()];

        for (int i = 0; i < listCartao2.length; i++) {
            listCartao2[i] = new ArrayList();
            listax[i] = new ArrayList();
        }

        int a = 0;
        for (Map.Entry<String, String> entry : modelsCard.entrySet()) {
            for (int i = 0; i < listaCartao.size(); i++) {
                if (Objects.equals(entry.getKey(), (String) ((List) listaCartao.get(i)).get(20))) {
                    listCartao2[a].add((List) listaCartao.get(i));
                }
            }
            a++;
        }

        for (int h = 0; h < listCartao2.length; h++) {

            for (int i = 0; i < listCartao2[h].size(); i++) {

                String id_pessoa = ((List) (listCartao2[h].get(i))).get(0).toString();

                String logoCartao = "";

                String matr = "";
                if (((List) (listCartao2[h].get(i))).get(10) != null) {
                    matr = "000000".substring(0, 6 - ((List) (listCartao2[h].get(i))).get(10).toString().length()) + ((List) (listCartao2[h].get(i))).get(10).toString();
                }

                String via = "00";
                if (((List) (listCartao2[h].get(i))).get(11) != null) {
                    via = ((List) (listCartao2[h].get(i))).get(11).toString();
                }

                if (via.length() == 1) {
                    via = "0" + via;
                }

                Registro reg = (Registro) new Dao().find(new Registro(), 1);
                String bc = getConverteNullString(((List) (listCartao2[h].get(i))).get(18)) + via; //             String bc = ((List) (listCartao2[h].get(i))).get(0).toString() + via; // id_pessoa
                String barras;

                if (reg.isValidadeBarras() && ((List) (listCartao2[h].get(i))).get(6) != null) {
                    Date vencto = DataHoje.converte(((List) (listCartao2[h].get(i))).get(6).toString());
                    if (vencto != null) {
                        Date dataModel = DataHoje.converte("07/10/1997");
                        long dias = vencto.getTime() - dataModel.getTime();
                        long total = dias / 86400000;

                        bc += "0000".substring(0, 4 - Long.toString(total).length()) + Long.toString(total);
                    } else {
                        bc += "0000";
                    }
                    barras = "00000000000000".substring(0, 14 - bc.length()) + bc;
                } else {
                    barras = "0000000000".substring(0, 10 - bc.length()) + bc;
                }
                //String barras = "0000000000".substring(0, 10 - bc.length()) + bc;

                SocioCarteirinha carteirinha = (SocioCarteirinha) new Dao().find(new SocioCarteirinha(), (Integer) ((List) listCartao2[h].get(i)).get(19));

                File file_img = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/cartao.jpg"));

                String caminho_img = "";

                if (file_img.exists()) {
                    caminho_img = file_img.getPath();
                }

                String endereco = getConverteNullString(((List) (listCartao2[h].get(i))).get(21)) + " "
                        + getConverteNullString(((List) (listCartao2[h].get(i))).get(22)) + ", "
                        + getConverteNullString(((List) (listCartao2[h].get(i))).get(23)) + " "
                        + getConverteNullString(((List) (listCartao2[h].get(i))).get(24)) + " - "
                        + getConverteNullString(((List) (listCartao2[h].get(i))).get(25));

                String cidade_uf = getConverteNullString(((List) (listCartao2[h].get(i))).get(26)) + " - " + getConverteNullString(((List) (listCartao2[h].get(i))).get(27));
                if (getConverteNullString(((List) (listCartao2[h].get(i))).get(36)).equals("TITULAR") || getConverteNullString(((List) (listCartao2[h].get(i))).get(36)).isEmpty()) {
                    titular = getConverteNullString(((List) (listCartao2[h].get(i))).get(1));
                    dependente = "";
                    orgaoOrigem = getConverteNullString(((List) (listCartao2[h].get(i))).get(9));
                    codigoFuncional = getConverteNullString(((List) (listCartao2[h].get(i))).get(35));
                } else {
                    socioDependente = sociosDao.pesquisaTitularPorDependente(Integer.valueOf(((List) (listCartao2[h].get(i))).get(0).toString()));
                    titular = socioDependente.getMatriculaSocios().getTitular().getNome();
                    dependente = getConverteNullString(((List) (listCartao2[h].get(i))).get(1));
                }
                try {
                    if (new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png")).exists()) {
                        logoCartao = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png");
                    }
                } catch (Exception e) {

                }
                try {
                    if (new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/logo_preto_branco.png")).exists()) {
                        logoCartao = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/logo_preto_branco.png");
                    }
                } catch (Exception e) {

                }

                FisicaDao fisicaDB = new FisicaDao();
                Fisica fisica = fisicaDB.pesquisaFisicaPorPessoa(Integer.valueOf(id_pessoa));
                String[] imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
                File foto_cartao = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/images/.png");

                for (String imagensTipo1 : imagensTipo) {
                    File test = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/cliente/" + ControleUsuarioBean.getCliente().toLowerCase() + "/imagens/pessoa/" + fisica.getPessoa().getId() + "/" + fisica.getFoto() + "." + imagensTipo1);
                    if (test.exists()) {
                        foto_cartao = test;
                        break;
                    }
                }
                String assinatura = "";
                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/assinatura.jpg"));
                if (f.exists()) {
                    assinatura = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/assinatura.jpg");
                }
                String cartaoVerso = "";
                File fileCartaoVerso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/cartao_verso.jpg"));
                if (fileCartaoVerso.exists()) {
                    cartaoVerso = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/cartao_verso.jpg");
                }

                if (carteirinha.getModeloCarteirinha().getJasper().equals("CARTAO_CONTRIBUINTE.jasper")) {
                    cartaoVerso = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/FUNDO_CARTAO_CONTRIBUINTE.png");
                }

                listax[h].add(
                        new CartaoSocial(
                                matr, //                                                         CODIGO
                                barras, //                                                       BARRAS 
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(1)), //  NOME
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(3)), //  EMPRESA
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(2)), //  CNPJ
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(8)), //  DATA ADMISSAO
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(6)), //  DATA VALIDADE
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(5)), //  CIDADE
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(7)), //  UF
                                logoCartao, // LOGO
                                foto_cartao.getAbsolutePath(), // CAMINHO FOTO
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(13)), // FILIAÇÃO
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(14)), // PROFISSÃO
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(15)), // CPF
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(16)), // RG
                                Integer.valueOf(((List) (listCartao2[h].get(i))).get(0).toString()), //    ID_PESSOA
                                endereco, //                                                     ENDERECO
                                cidade_uf, //                                                    CIDADE
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(29)), // NACIONALIDADE
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(30)), // NASCIMENTO
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(31)), // ESTADO CIVIL
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(32)), // CARTEIRA
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(33)), // SERIE
                                caminho_img, //                                                  IMAGEM FUNDO
                                codigoFuncional, //                                              CÓDIGO FUNCIONAL
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(36)), // ÓRGÃO EXPEDITOR
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(36)), // PARENTESCO
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(37)), // CATEGORIA
                                orgaoOrigem, //  FANTASIA
                                titular, //                                                      TITULAR
                                dependente, //                                                   DEPENDENTE
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(38)), // FANTASIA EMPRESA - TITULAR
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(39)), //  CÓDIGO FUNCIONAL - TITULAR
                                (getConverteNullString(((List) (listCartao2[h].get(i))).get(40)).isEmpty()) ? 0 : Integer.parseInt(getConverteNullString(((List) (listCartao2[h].get(i))).get(40))), // TITULAR ID
                                getConverteNullString(((List) (listCartao2[h].get(i))).get(41)), // GRUPO CATEGORIA
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/imagemExtra.png"), // IMAGEM EXTRA
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/imagemExtra2.png"), // IMAGEM EXTRA 2
                                (!getConverteNullString(((List) (listCartao2[h].get(i))).get(42)).isEmpty()) ? "( APOSENTADO )" : "", // DATA APOSENTADORIA
                                via,
                                new ArrayList(),
                                assinatura,
                                cartaoVerso,
                                ((List) (listCartao2[h].get(i))).get(43),
                                ((List) (listCartao2[h].get(i))).get(44)
                        )
                );
                // ControleUsuarioBean.getCliente().equals("ExtrativaRP") || ControleUsuarioBean.getCliente().equals("CondominiosRP") || ControleUsuarioBean.getCliente().equals("MetalRP") || ControleUsuarioBean.getCliente().equals("MetalBatatais")
                if (carteirinha.getModeloCarteirinha().getDependenteLista()) {
                    if (getConverteNullString(((List) (listCartao2[h].get(i))).get(36)).equals("TITULAR")) {
                        for (int x = 0; x < listax[h].size(); x++) {
                            MatriculaSocios ms = new MatriculaSociosDao().findByMatricula(Integer.parseInt(listax[h].get(x).getMatricula()), listax[h].get(x).getTitular_id());
                            List<CartaoSocial> listCartaoSocialDependente = new ArrayList();
                            if (ms != null) {
                                List<Socios> listSocios = new SociosDao().pesquisaDependentesOrdenado(ms.getId());
                                for (int z = 0; z < listSocios.size(); z++) {
                                    CartaoSocial cartaoSocial = new CartaoSocial();
                                    cartaoSocial.setDependente(listSocios.get(z).getServicoPessoa().getPessoa().getNome());
                                    cartaoSocial.setParentesco(listSocios.get(z).getParentesco().getParentesco());
                                    cartaoSocial.setNascimento(listSocios.get(z).getServicoPessoa().getPessoa().getFisica().getNascimento());
                                    listCartaoSocialDependente.add(cartaoSocial);
                                }
                                listax[h].get(x).setListDependentes(listCartaoSocialDependente);
                            }
                        }
                    }
                }

                for (ModeloCarteirinha modelo : listaModelo) {
                    if (Objects.equals(carteirinha.getModeloCarteirinha().getId(), modelo.getId())) {
                        hash.put(modelo.getId(), listax[h]);
                    }
                }
            }
        }
        for (int i = 0; i < listax.length; i++) {
            if (listax[i].isEmpty()) {
                return false;
            }
        }
        try {
//             String patch = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos");
            Diretorio.criar("downloads/carteirinhas");
            // ModeloCarteirinha modelo;
            List ljasper = new ArrayList();
            JasperReport jasper;
            JasperReport subJasper;
            JasperReport jasperVerso;
            String subreport = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/DEPENDENTES.jasper");
//            DBExternal con = new DBExternal();
//            con.setDatabase(GenericaSessao.getString("sessaoCliente"));
            Map map = new HashMap();
//            if (!new File(subreport).exists()) {
//                subreport = null;
//            } else {
//                map.put("REPORT_CONNECTION", con.getConnection());
//            }
            String mimeType = "application/pdf";
            for (Entry<Integer, List> entry : hash.entrySet()) {
                ModeloCarteirinha modelo = (ModeloCarteirinha) new Dao().find(new ModeloCarteirinha(), entry.getKey());
                String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + modelo.getJasper());
                if (caminho == null) {
                    GenericaMensagem.error("Erro jasper: " + modelo.getJasper(), "Modelo não encontrado na pasta Relatório!");
                    continue;
                }

                File file = new File(
                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + modelo.getJasper())
                );
                //* ADD LISTA DE JASPERS *//
                JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(entry.getValue());
                jasper = (JasperReport) JRLoader.loadObject(file);
                File subreportFile = new File(subreport);
                if (subreportFile.exists()) {
                    if (subreport != null) {
                        map.put("template_dir", subreport);
                    }
                }
                ljasper.add(reports.fillObject(jasper, map, dtSource));
                try {
                    File fileVerso = new File(
                            ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CARTAO_VERSO.jasper")
                    );
                    if (fileVerso.exists()) {
                        //* ADD LISTA DE JASPERS *//
                        dtSource = new JRBeanCollectionDataSource(entry.getValue());
                        jasperVerso = (JasperReport) JRLoader.loadObject(fileVerso);
                        ljasper.add(reports.fillObject(jasperVerso, map, dtSource));
                    }
                } catch (Exception ev) {
                    GenericaMensagem.warn("Erro", "Ao imprimir verso do cartão! " + ev.getMessage());
                    break;
                }
            }
            if (r == null) {
                reports.setPART_NAME("");
                reports.setPATH("downloads");
                reports.print("cartao_social", ljasper);
            } else {
                reports.print("cartao_social", ljasper);
            }
        } catch (JRException e) {
            return false;
        }

        return true;
    }

    public static void comDependente(String nomeDownload, String path, String pathVerso, Socios socios, PessoaEmpresa pessoaEmpresa, MatriculaSocios matriculaSocios, boolean imprimirVerso, String fotoSocio) {
        Dao dao = new Dao();
        Registro registro = (Registro) dao.find(new Registro(), 1);
        Fisica fisica;
        Juridica sindicato;
        FisicaDao db = new FisicaDao();
        PessoaEndereco pesEndereco, pesDestinatario, pesEndEmpresa, pesEndSindicato;
        PessoaEnderecoDao dbEnd = new PessoaEnderecoDao();
        SociosDao dbSoc = new SociosDao();
        FacesContext faces = FacesContext.getCurrentInstance();

        Collection listaSocios = new ArrayList();
        fisica = db.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId());
        pesEndereco = dbEnd.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
        sindicato = (Juridica) dao.find(new Juridica(), 1);

        File logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPersonalizado.png"));

        if (!logo_cliente.exists()) {
            logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
        }

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
                    matriculaSocios.getTitular().getId(),
                    matriculaSocios.getNrMatricula(),
                    matriculaSocios.getEmissao(),
                    recadastro,
                    matriculaSocios.getCategoria().getGrupoCategoria().getGrupoCategoria(),
                    matriculaSocios.getCategoria().getCategoria(),
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
                    imprimirVerso,
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
                    socios.getParentesco().getParentesco(),
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
                    ((ServletContext) faces.getExternalContext().getContext()).getRealPath(pathVerso),
                    dados[29],
                    fisica.getPessoa().getRecadastroString(),
                    dados[30],
                    pesEndSindicato.getEndereco().getLogradouro().getDescricao(),
                    dados[31],
                    assinatura,
                    dados[32],
                    fisica.getPis()
            ));

            List<Socios> deps = dbSoc.pesquisaDependentesOrdenado(matriculaSocios.getId());
            for (int n = 0; n < deps.size(); n++) {
                listaSocios.add(new FichaSocial(0,
                        deps.get(n).getServicoPessoa().getPessoa().getId(),
                        matriculaSocios.getNrMatricula(),
                        "",
                        "",
                        "",
                        matriculaSocios.getCategoria().getCategoria(),
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
                        imprimirVerso,
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
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath(pathVerso),
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
            Jasper.load();
            Jasper.PATH = "downloads";
            Jasper.PART_NAME = "";

            List ljasper = new ArrayList();
            if (matriculaSocios.getCategoria().getFichaSocial()) {
                File ficha_s = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(path));

                if (ficha_s.exists()) {
                    JasperReport jasper_1 = (JasperReport) JRLoader.loadObject(ficha_s);
                    JRBeanCollectionDataSource dtSource_1 = new JRBeanCollectionDataSource(listaSocios);
                    ljasper.add(Jasper.fillObject(jasper_1, null, dtSource_1));
                }
            }

            if (matriculaSocios.getCategoria().getFichaFiliacao()) {
                File ficha_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHA_DE_SINDICALIZACAO.jasper"));

                if (ficha_f.exists()) {
                    JasperReport jasper_2 = (JasperReport) JRLoader.loadObject(ficha_f);
                    JRBeanCollectionDataSource dtSource_2 = new JRBeanCollectionDataSource(listaSocios);
                    ljasper.add(Jasper.fillObject(jasper_2, null, dtSource_2));
                }

            }

            Jasper.printReports("FICHA_SOCIAL", ljasper);

        } catch (JRException erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public static void semDependente(String pathPasta, String nomeDownload, String path, String pathVerso, Socios socios, PessoaEmpresa pessoaEmpresa, MatriculaSocios matriculaSocios, boolean imprimirVerso, List<Socios> listaDependentes) {
        Fisica fisica = new Fisica();
        Juridica sindicato = new Juridica();
        FisicaDao db = new FisicaDao();
        PessoaEndereco pesEndereco, pesDestinatario, pesEndEmpresa, pesEndSindicato = new PessoaEndereco();
        PessoaEnderecoDao dbEnd = new PessoaEnderecoDao();
        PessoaEmpresa pesEmpresa = new PessoaEmpresa();
        PessoaEmpresaDao dbEmp = new PessoaEmpresaDao();
        String dados[] = new String[34];
        List<Socios> listaSocs = new ArrayList();
        Dao dao = new Dao();
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            File logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPersonalizado.png"));

            if (!logo_cliente.exists()) {
                logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
            }

            //HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
            Collection listaSocios = new ArrayList<FichaSocial>();
            JasperReport jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath(path))
            );
            sindicato = (Juridica) dao.find(new Juridica(), 1);
            pesEndSindicato = dbEnd.pesquisaEndPorPessoaTipo(sindicato.getPessoa().getId(), 2);

            listaSocs.add(socios);
            listaSocs.addAll(listaDependentes);

            for (int i = 0; i < listaSocs.size(); i++) {
                fisica = db.pesquisaFisicaPorPessoa(listaSocs.get(i).getServicoPessoa().getPessoa().getId());
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
                    dados[16] = pesEmpresa.getJuridica().getPessoa().getNome();
                    dados[17] = pesEmpresa.getJuridica().getPessoa().getTelefone1();
                    dados[18] = pesEmpresa.getFuncao().getProfissao();
                    dados[19] = pesEndEmpresa.getEndereco().getDescricaoEndereco().getDescricao();
                    dados[20] = pesEndEmpresa.getNumero();
                    dados[21] = pesEndEmpresa.getComplemento();
                    dados[22] = pesEndEmpresa.getEndereco().getBairro().getDescricao();
                    dados[23] = pesEndEmpresa.getEndereco().getCidade().getCidade();
                    dados[24] = pesEndEmpresa.getEndereco().getCidade().getUf();
                    dados[25] = AnaliseString.mascaraCep(pesEndEmpresa.getEndereco().getCep());
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
                    dados[32] = "";
                }

                try {
                    listaSocios.add(new FichaSocial(0,
                            listaSocs.get(i).getMatriculaSocios().getTitular().getId(),
                            listaSocs.get(i).getMatriculaSocios().getNrMatricula(),
                            listaSocs.get(i).getServicoPessoa().getEmissao(),
                            null,
                            listaSocs.get(i).getMatriculaSocios().getCategoria().getGrupoCategoria().getGrupoCategoria(),
                            listaSocs.get(i).getMatriculaSocios().getCategoria().getCategoria(),
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
                            listaSocs.get(i).getParentesco().getParentesco(),
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
                            getFotoSocio(listaSocs.get(i)),
                            sindicato.getPessoa().getEmail1(),
                            sindicato.getPessoa().getSite(),
                            sindicato.getPessoa().getTelefone1(),
                            ((ServletContext) faces.getExternalContext().getContext()).getRealPath(pathVerso),
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
            if (listaSocios.isEmpty()) {
                return;
            }
            Jasper.load();
            Jasper.PATH = "downloads";

            List ljasper = new ArrayList();
            if (matriculaSocios.getCategoria().getFichaSocial()) {
                File ficha_s = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(path));

                if (ficha_s.exists()) {
                    JasperReport jasper_1 = (JasperReport) JRLoader.loadObject(ficha_s);
                    JRBeanCollectionDataSource dtSource_1 = new JRBeanCollectionDataSource(listaSocios);
                    ljasper.add(Jasper.fillObject(jasper_1, null, dtSource_1));
                }
            }

            if (matriculaSocios.getCategoria().getFichaFiliacao()) {
                File ficha_f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHA_DE_SINDICALIZACAO.jasper"));

                if (ficha_f.exists()) {
                    JasperReport jasper_2 = (JasperReport) JRLoader.loadObject(ficha_f);
                    JRBeanCollectionDataSource dtSource_2 = new JRBeanCollectionDataSource(listaSocios);
                    ljasper.add(Jasper.fillObject(jasper_2, null, dtSource_2));
                }

            }

            Jasper.printReports("FICHA_SOCIAL", ljasper);
        } catch (JRException erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
        //return null;
    }

    public static void branco() {
        String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/fichas");
        PessoaEnderecoDao enderecoDB = new PessoaEnderecoDao();
        Dao dao = new Dao();

        FacesContext faces = FacesContext.getCurrentInstance();
        File logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoPersonalizado.png"));

        if (!logo_cliente.exists()) {
            logo_cliente = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));
        }

        try {
            Collection listaSocios = new ArrayList<>();
            Juridica sindicato = (Juridica) dao.find(new Juridica(), 1);
            PessoaEndereco pessoaEndereco = enderecoDB.pesquisaEndPorPessoaTipo(sindicato.getPessoa().getId(), 2);
            Registro registro = (Registro) dao.find(new Registro(), 1);
            try {
                listaSocios.add(new FichaSocial(0,
                        0, // ID TITULAR
                        0, // NR MATRICULA
                        DataHoje.data(), // DATA EMISSÃO
                        DataHoje.data(),
                        "", // MATR GRUPO
                        "", // MATR SOC_CATEGORIA
                        "", // NOME
                        "", // SEXO
                        DataHoje.data(), // NASCIMENTO
                        "", // NATURALIDADE
                        "", // NACIONALIDADE
                        "", // RG
                        "", // DOCUMENTO
                        "", // CARTEIRINHA
                        "", // SERIE
                        "", // ESTADO CÍVIL
                        "", // PAI
                        "", // MÃE
                        "", // TEL1
                        "", // TEL2
                        "", // EMAIL1
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
                        "", // fax
                        DataHoje.data(),
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
                        "",
                        sindicato.getPessoa().getNome(),
                        pessoaEndereco.getEndereco().getDescricaoEndereco().getDescricao(),
                        pessoaEndereco.getNumero(),
                        pessoaEndereco.getComplemento(),
                        pessoaEndereco.getEndereco().getBairro().getDescricao(),
                        pessoaEndereco.getEndereco().getCidade().getCidade(),
                        pessoaEndereco.getEndereco().getCidade().getUf(),
                        AnaliseString.mascaraCep(pessoaEndereco.getEndereco().getCep()),
                        sindicato.getPessoa().getDocumento(),
                        "",
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                        "",
                        sindicato.getPessoa().getEmail1(),
                        sindicato.getPessoa().getSite(),
                        sindicato.getPessoa().getTelefone1(),
                        "",
                        "",
                        DataHoje.data(),
                        "",
                        pessoaEndereco.getEndereco().getLogradouro().getDescricao(),
                        "",
                        "",
                        "",
                        ""
                ));
            } catch (Exception erro) {
                System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
            }
            Jasper.load();
            Jasper.PART_NAME = "";
            Jasper.PATH = "ficha";
            //Jasper.printReports("/Relatorios/FICHACADASTROBRANCO.jasper", "ficha_branco", listaSocios);
            //String jasper_path = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath();

            String jasper_path = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTROBRANCO.jasper";

            Jasper.printReports(jasper_path, "ficha_branco", listaSocios);
        } catch (Exception e) {
        }
    }

    public static String getFotoSocio(Socios socios) {
        FacesContext context = FacesContext.getCurrentInstance();
        File files;
        //if (socios.getId() != -1){
        files = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + socios.getServicoPessoa().getPessoa().getId() + ".jpg"));
        if (files.exists()) {
            return files.getPath();
        } else {
            return ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg");
        }
        //}else
        //    return ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg");
    }

    public static String getConverteNullString(Object object) {
        if (object == null) {
            return "";
        } else {
            return String.valueOf(object);

        }
    }

    private class ObjectCard {

        private Integer key;
        private ModeloCarteirinha modeloCarteirinha;
        private List listCartao;

        public ObjectCard() {
            this.key = null;
            this.modeloCarteirinha = null;
            this.listCartao = new ArrayList();
        }

        public ObjectCard(Integer key, ModeloCarteirinha modeloCarteirinha, List listCartao) {
            this.key = key;
            this.modeloCarteirinha = modeloCarteirinha;
            this.listCartao = listCartao;
        }

        public Integer getKey() {
            return key;
        }

        public void setKey(Integer key) {
            this.key = key;
        }

        public ModeloCarteirinha getModeloCarteirinha() {
            return modeloCarteirinha;
        }

        public void setModeloCarteirinha(ModeloCarteirinha modeloCarteirinha) {
            this.modeloCarteirinha = modeloCarteirinha;
        }

        public List getListCartao() {
            return listCartao;
        }

        public void setListCartao(List listCartao) {
            this.listCartao = listCartao;
        }

    }
}
