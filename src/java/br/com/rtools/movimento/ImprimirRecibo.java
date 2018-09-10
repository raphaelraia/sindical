package br.com.rtools.movimento;

import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.financeiro.Baixa;
import br.com.rtools.financeiro.FormaPagamento;
import br.com.rtools.financeiro.Guia;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.impressao.ParametroEncaminhamento;
import br.com.rtools.impressao.ParametroRecibo;
import br.com.rtools.impressao.ParametroReciboFinanceiro;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.ValorExtenso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.collections.map.HashedMap;

public class ImprimirRecibo {

    private List<JasperPrint> lista_jaspers = new ArrayList();

    public void imprimir() {

        String path = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads");

        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }

        UUID uuidX = UUID.randomUUID();
        String name_file = uuidX.toString().replace("-", "_") + ".pdf";

        try {
            JRPdfExporter exporter = new JRPdfExporter();

            exporter.setExporterInput(SimpleExporterInput.getInstance(lista_jaspers));

            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path + "/" + name_file));

            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

            configuration.setCreatingBatchModeBookmarks(true);

            exporter.setConfiguration(configuration);

            exporter.exportReport();

            File flx = new File(path);

            if (flx.exists()) {
                Download download = new Download(
                        name_file,
                        path,
                        "application/pdf",
                        FacesContext.getCurrentInstance()
                );

                download.baixar();

                download.remover();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public Boolean gerar_encaminhamento(Integer id_lote) {
        Juridica sindicato = (Juridica) (new Dao()).find(new Juridica(), 1);
        PessoaEnderecoDao dbp = new PessoaEnderecoDao();
        PessoaEndereco pe = dbp.pesquisaEndPorPessoaTipo(1, 2);
        MovimentoDao db = new MovimentoDao();

        Collection vetor = new ArrayList();

        Guia guia = db.pesquisaGuias(id_lote);

        if (guia.getId() == -1) {
            return false;
        }

        SociosDao dbs = new SociosDao();

        List<Object> list_ob = db.pesquisaGuiaParaEncaminhamento(guia.getLote().getId());

        Socios socios_enc = dbs.pesquisaSocioPorPessoaAtivo((Integer) ((List) list_ob.get(0)).get(3));

        String str_usuario, str_nome, str_validade;

        PessoaEndereco pe_empresa = dbp.pesquisaEndPorPessoaTipo(guia.getPessoa().getId(), 5);

        String complemento = (pe_empresa.getComplemento().isEmpty()) ? "" : " ( " + pe_empresa.getComplemento() + " ) ";

        String endereco
                = pe_empresa.getEndereco().getLogradouro().getDescricao() + " "
                + pe_empresa.getEndereco().getDescricaoEndereco().getDescricao() + ", " + pe_empresa.getNumero() + " - " + complemento
                + pe_empresa.getEndereco().getBairro().getDescricao() + ", "
                + pe_empresa.getEndereco().getCidade().getCidade() + "  -  "
                + pe_empresa.getEndereco().getCidade().getUf();

        str_usuario = (String) ((List) list_ob.get(0)).get(7);

        str_nome = (String) ((List) list_ob.get(0)).get(4);

        Map<String, String> hash = new HashMap();

        try {

            File fl_original = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/ENCAMINHAMENTO.jasper"));
            File fl_menor = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/ENCAMINHAMENTO_MENOR.jasper"));
            File fl_jasper;

            if (fl_menor.exists()) {
                fl_jasper = fl_menor;
            } else {
                fl_jasper = fl_original;
            }

            JasperReport jasper = (JasperReport) JRLoader.loadObject(fl_jasper);

            for (Object list : list_ob) {
                List linha = (List) list;

                DataHoje dh = new DataHoje();

//                  if (lista.get(i).getServicos().isValidadeGuias() && !lista.get(i).getServicos().isValidadeGuiasVigente()) {
//                        lblVencimento = "Validade";
//                        vencimento = dataHoje.incrementarDias(lista.get(i).getServicos().getValidade(), lista.get(i).getLote().getEmissao());
//                    } else if (lista.get(i).getServicos().isValidadeGuias() && lista.get(i).getServicos().isValidadeGuiasVigente()) {
//                        lblVencimento = "Validade";
//                        vencimento = DataHoje.converteData(DataHoje.lastDayOfMonth(DataHoje.dataHoje()));
//                    } else {
//                        lblVencimento = "Validade";
//                        vencimento = "";
//                    }
//            }
                if (linha.get(12) != null) { // SE FOR PRODUTO
                    if ((Boolean) linha.get(14)) { // SE is_validade_guias_mes_vigente
                        str_validade = dh.ultimoDiaDoMes(guia.getLote().getEmissao());
                    } else {
                        str_validade = dh.incrementarDias((Integer) linha.get(15), guia.getLote().getEmissao());
                    }
                } else {
                    // SE FOR SERVIÇO
                    if ((Boolean) linha.get(16) && !(Boolean) linha.get(10)) {
                        str_validade = dh.incrementarDias((Integer) linha.get(11), guia.getLote().getEmissao());
                    } else if ((Boolean) linha.get(16) && (Boolean) linha.get(10)) {
                        // SE is_validade_guias_mes_vigente
                        str_validade = dh.ultimoDiaDoMes(guia.getLote().getEmissao());
                    } else {
                        str_validade = "";
                    }
                }

//                if (hash.containsKey(str_validade)) {
//                    hash.put(str_validade, hash.get(str_validade) + ", " + movs.getServicos().getDescricao());
//                } else {
//                    hash.put(str_validade, movs.getServicos().getDescricao());
//                }
                vetor.add(new ParametroEncaminhamento(
                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                        sindicato.getPessoa().getNome(),
                        pe.getEndereco().getDescricaoEndereco().getDescricao(),
                        pe.getEndereco().getLogradouro().getDescricao(),
                        pe.getNumero(),
                        pe.getComplemento(),
                        pe.getEndereco().getBairro().getDescricao(),
                        pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5),
                        pe.getEndereco().getCidade().getCidade(),
                        pe.getEndereco().getCidade().getUf(),
                        sindicato.getPessoa().getTelefone1(),
                        sindicato.getPessoa().getEmail1(),
                        sindicato.getPessoa().getSite(),
                        sindicato.getPessoa().getDocumento(),
                        String.valueOf(guia.getId()), // GUIA
                        String.valueOf(guia.getPessoa().getId()), // CODIGO
                        guia.getSubGrupoConvenio().getGrupoConvenio().getDescricao(), // GRUPO
                        guia.getSubGrupoConvenio().getDescricao(), // SUB GRUPO
                        pe_empresa.getPessoa().getNome(), // EMPRESA CONVENIADA
                        endereco, // EMPRESA ENDERECO
                        pe_empresa.getPessoa().getTelefone1(), // EMPRESA TELEFONE
                        (String) (((Integer) linha.get(12)) != null ? linha.get(13) : linha.get(9)),//str_servicos, // SERVICOS
                        guia.getLote().getEmissao(), // EMISSAO
                        str_validade,//str_validade, // VALIDADE
                        str_usuario, // USUARIO
                        str_nome,
                        socios_enc.getParentesco().getParentesco(),
                        (socios_enc.getMatriculaSocios().getId() == -1) ? "" : String.valueOf(socios_enc.getMatriculaSocios().getId()),
                        socios_enc.getMatriculaSocios().getCategoria().getCategoria(),
                        guia.getObservacao()
                ));

            }

            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);

            JasperPrint print = JasperFillManager.fillReport(jasper, null, dtSource);

            lista_jaspers.add(print);

            vetor.clear();

//            for (Map.Entry<String, String> entry : hash.entrySet()) {
//
//                vetor.add(new ParametroEncaminhamento(
//                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
//                        sindicato.getPessoa().getNome(),
//                        pe.getEndereco().getDescricaoEndereco().getDescricao(),
//                        pe.getEndereco().getLogradouro().getDescricao(),
//                        pe.getNumero(),
//                        pe.getComplemento(),
//                        pe.getEndereco().getBairro().getDescricao(),
//                        pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5),
//                        pe.getEndereco().getCidade().getCidade(),
//                        pe.getEndereco().getCidade().getUf(),
//                        sindicato.getPessoa().getTelefone1(),
//                        sindicato.getPessoa().getEmail1(),
//                        sindicato.getPessoa().getSite(),
//                        sindicato.getPessoa().getDocumento(),
//                        String.valueOf(guia.getId()), // GUIA
//                        String.valueOf(guia.getPessoa().getId()), // CODIGO
//                        guia.getSubGrupoConvenio().getGrupoConvenio().getDescricao(), // GRUPO
//                        guia.getSubGrupoConvenio().getDescricao(), // SUB GRUPO
//                        pe_empresa.getPessoa().getNome(), // EMPRESA CONVENIADA
//                        endereco, // EMPRESA ENDERECO
//                        pe_empresa.getPessoa().getTelefone1(), // EMPRESA TELEFONE
//                        entry.getValue(),//str_servicos, // SERVICOS
//                        guia.getLote().getEmissao(), // EMISSAO
//                        entry.getKey(),//str_validade, // VALIDADE
//                        str_usuario, // USUARIO
//                        str_nome,
//                        socios_enc.getParentesco().getParentesco(),
//                        (socios_enc.getMatriculaSocios().getId() == -1) ? "" : String.valueOf(socios_enc.getMatriculaSocios().getId()),
//                        socios_enc.getMatriculaSocios().getCategoria().getCategoria(),
//                        guia.getObservacao()
//                ));
//
//                JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
//
//                JasperPrint print = JasperFillManager.fillReport(jasper, null, dtSource);
//
//                lista_jaspers.add(print);
//
//                vetor.clear();
//            }
            if (db.imprimeEncaminhamentoJunto((Integer) ((List) list_ob.get(0)).get(5))) {

                Boolean test = gerar_recibo((Integer) ((List) list_ob.get(0)).get(0));

            }

        } catch (JRException e) {
            e.getMessage();
        }

        return true;
    }

    public Boolean gerar_recibo_generico(List<Movimento> lista_movimento, Map parameter) {
        Collection vetor = new ArrayList();
        Juridica sindicato = (Juridica) (new Dao()).find(new Juridica(), 1);
        PessoaEnderecoDao dbp = new PessoaEnderecoDao();

        PessoaEndereco pe = dbp.pesquisaEndPorPessoaTipo(1, 2);

        MovimentoDao db = new MovimentoDao();

        String ids = "";
        for (Movimento m : lista_movimento) {
            if (ids.isEmpty()) {
                ids = "" + m.getId();
            } else {
                ids += ", " + m.getId();
            }
        }

        List<Object> m_ordenado = db.listaMovimentoAgrupadoOrdemBaixa(ids);
        String sindicato_nome = sindicato.getPessoa().getNome();

        if (ControleUsuarioBean.getCliente().equals("ComercioSorocaba")) {
            sindicato_nome = AnaliseString.converterCapitalize(sindicato_nome);
        }

        try {
            if (!m_ordenado.isEmpty()) {

                for (Object obj : m_ordenado) {
                    List linha = (List) obj;

                    Pessoa pessoa = (Pessoa) new Dao().find(new Pessoa(), (Integer) linha.get(1));

                    vetor.add(
                            new ParametroReciboFinanceiro(
                                    ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                                    sindicato_nome,
                                    pe.getEndereco().getDescricaoEndereco().getDescricao(),
                                    pe.getEndereco().getLogradouro().getDescricao(),
                                    pe.getNumero(),
                                    pe.getComplemento(),
                                    pe.getEndereco().getBairro().getDescricao(),
                                    pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5),
                                    pe.getEndereco().getCidade().getCidade(),
                                    pe.getEndereco().getCidade().getUf(),
                                    sindicato.getPessoa().getTelefone1(),
                                    sindicato.getPessoa().getEmail1(),
                                    sindicato.getPessoa().getSite(),
                                    sindicato.getPessoa().getDocumento(),
                                    linha.get(0), // NOME PESSOA
                                    (Integer) linha.get(1), // ID PESSOA
                                    (Integer) linha.get(2), // ID BAIXA
                                    (Date) linha.get(3), // DATA BAIXA
                                    (pessoa.getTipoDocumento().getId() == 1) ? linha.get(11).toString() : linha.get(4).toString(), // HISTORICO CONTABIL + HISTORICO PADRAO // por causa de sorocaba foi colocado apenas o historico contabil
                                    linha.get(5).toString(), // ES
                                    (Double) linha.get(6), // VALOR BAIXADO
                                    Moeda.converteR$Double((Double) linha.get(6)) + " ( " + new ValorExtenso((Double) linha.get(6)).toString() + " )",
                                    pe.getEndereco().getCidade().getCidade() + " - " + pe.getEndereco().getCidade().getUf() + ", " + DataHoje.dataExtenso(DataHoje.converteData((Date) linha.get(3)), 3),
                                    (Integer) linha.get(7),
                                    (Integer) linha.get(8),
                                    linha.get(9).toString(),
                                    linha.get(10).toString()
                            )
                    );

                    File fl = tipoRecibo(pessoa.getTipoDocumento().getId(), linha.get(5).toString());

                    JasperReport jasper = (JasperReport) JRLoader.loadObject(fl);

                    JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
                    if (parameter == null) {
                        parameter = new HashedMap();
                    }
                    JasperPrint print = JasperFillManager.fillReport(jasper, parameter, dtSource);

                    lista_jaspers.add(print);
                }

            }
        } catch (JRException e) {
            e.getMessage();
            return false;
        }
        return true;
    }

    public File tipoRecibo(Integer id_tipo_documento, String es) {
        // TIPO DE DOCUMENTO PESSOA FISICA (CPF)
        if (id_tipo_documento == 1) {
            // RECIBO PERSONALIZADO
            File fl;

            if (es.equals("E")) {
                fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/RECIBO_FINANCEIRO_FISICA_(E).jasper"));
            } else {
                fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/RECIBO_FINANCEIRO_FISICA_(S).jasper"));
            }

            if (!fl.exists()) {
                fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/RECIBO_FINANCEIRO.jasper"));
            }

            return fl;
        } else {
            // TIPO DE DOCUMENTO PESSOA JURIDICA E OUTRO
            // RECIBO PERSONALIZADO
            File fl;

            if (es.equals("E")) {
                fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/RECIBO_FINANCEIRO_JURIDICA_(E).jasper"));
            } else {
                fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/RECIBO_FINANCEIRO_JURIDICA_(S).jasper"));
            }

            if (!fl.exists()) {
                fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/RECIBO_FINANCEIRO.jasper"));
            }

            return fl;
        }
    }

    public Boolean gerar_recibo(Integer id_movimento) {
        return gerar_recibo(id_movimento, null);
    }

    public Boolean gerar_recibo(Integer id_movimento, Map parameter) {
        MovimentoDao db = new MovimentoDao();
        Movimento movimento = db.pesquisaCodigo(id_movimento);
        try {
            Collection vetor = new ArrayList();
            Juridica sindicato = (Juridica) (new Dao()).find(new Juridica(), 1);
            PessoaEnderecoDao dbp = new PessoaEnderecoDao();

            PessoaEndereco pe = dbp.pesquisaEndPorPessoaTipo(1, 2);

            // PESQUISA FORMA DE PAGAMENTO
            List<FormaPagamento> fp = db.pesquisaFormaPagamento(movimento.getBaixa().getId());
            String formas[] = new String[15];
            double soma_dinheiro = 0, soma_outros = 0;
            for (int i = 0; i < fp.size(); i++) {
                switch (fp.get(i).getTipoPagamento().getId()) {
                    // 4 - CHEQUE
                    case 4:
                        formas[i] = fp.get(i).getTipoPagamento().getDescricao() + ": R$ " + Moeda.converteR$Double(fp.get(i).getValor()) + " (B: " + fp.get(i).getChequeRec().getBanco().getNumero() + " Ag: " + fp.get(i).getChequeRec().getAgencia() + " C: " + fp.get(i).getChequeRec().getConta() + " CH: " + fp.get(i).getChequeRec().getCheque() + ")";
                        break;
                    // 5 - CHEQUE PRÉ
                    case 5:
                        formas[i] = fp.get(i).getTipoPagamento().getDescricao() + ": R$ " + Moeda.converteR$Double(fp.get(i).getValor()) + " (B: " + fp.get(i).getChequeRec().getBanco().getNumero() + " Ag: " + fp.get(i).getChequeRec().getAgencia() + " C: " + fp.get(i).getChequeRec().getConta() + " CH: " + fp.get(i).getChequeRec().getCheque() + " P: " + fp.get(i).getChequeRec().getVencimento() + ")";
                        break;
                    // QUALQUER OUTRO    
                    case 6:
                    case 7:
                        formas[i] = fp.get(i).getTipoPagamento().getDescricao() + " - " + fp.get(i).getCartaoRec().getCartao().getDescricao() + ((!fp.get(i).getDocumento().isEmpty()) ? " (N° " + fp.get(i).getDocumento() + ")" : "") + ": R$ " + Moeda.converteR$Double(fp.get(i).getValor());
                        break;
                    default:
                        formas[i] = fp.get(i).getTipoPagamento().getDescricao() + ((!fp.get(i).getDocumento().isEmpty()) ? " (N° " + fp.get(i).getDocumento() + ")" : "") + ": R$ " + Moeda.converteR$Double(fp.get(i).getValor());
                        if (fp.get(i).getTipoPagamento().getId() == 3) {
                            soma_dinheiro = soma_dinheiro + fp.get(i).getValor();
                        }
                        break;
                }

                if (fp.get(i).getTipoPagamento().getId() != 3) {
                    soma_outros = soma_outros + fp.get(i).getValor();
                }
            }

            String lblVencimento = "", vencimento = "";

            DataHoje dataHoje = new DataHoje();
            List<Movimento> lista = db.listaMovimentoBaixaOrder(movimento.getBaixa().getId());

            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getValor() == 0) {
                    continue;
                }
                // tem casos de ter responsaveis diferentes, resultando em empresas conveniadas diferentes
                Guia gu = db.pesquisaGuias(lista.get(i).getLote().getId());
                String conveniada = "", mensagemConvenio = "";
                if (gu.getId() != -1) {
                    if (gu.getPessoa() != null) {
                        conveniada = gu.getPessoa().getNome();
                    }
                }

                if (lista.get(i).getLote().getRotina().getId() == 132) {
                    if (lista.get(i).getServicos().isValidadeGuias() && !lista.get(i).getServicos().isValidadeGuiasVigente()) {
                        lblVencimento = "Validade";
                        vencimento = dataHoje.incrementarDias(lista.get(i).getServicos().getValidade(), lista.get(i).getLote().getEmissao());
                    } else if (lista.get(i).getServicos().isValidadeGuias() && lista.get(i).getServicos().isValidadeGuiasVigente()) {
                        lblVencimento = "Validade";
                        vencimento = DataHoje.converteData(DataHoje.lastDayOfMonth(DataHoje.dataHoje()));
                    } else {
                        lblVencimento = "Validade";
                        vencimento = "";
                    }

                    // MOSTRANDO MENSAGEM APENAS SE VIER DA PAGINA EMISSÃO DE GUIAS --- by rogerinho 17/03/2015 -- chamado 579
                    mensagemConvenio = lista.get(i).getLote().getHistorico();
                } else {
                    lblVencimento = "Vencimento";
                    vencimento = lista.get(i).getVencimento();
                }

                String servico_descricao = "";
                if (lista.get(i).getServicos() != null) {
                    servico_descricao = lista.get(i).getServicos().getDescricao();
                }

                vetor.add(
                        new ParametroRecibo(
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),
                                sindicato.getPessoa().getNome(),
                                pe.getEndereco().getDescricaoEndereco().getDescricao(),
                                pe.getEndereco().getLogradouro().getDescricao(),
                                pe.getNumero(),
                                pe.getComplemento(),
                                pe.getEndereco().getBairro().getDescricao(),
                                pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5),
                                pe.getEndereco().getCidade().getCidade(),
                                pe.getEndereco().getCidade().getUf(),
                                sindicato.getPessoa().getTelefone1(),
                                sindicato.getPessoa().getEmail1(),
                                sindicato.getPessoa().getSite(),
                                sindicato.getPessoa().getDocumento(),
                                lista.get(i).getPessoa().getNome(), // RESPONSÁVEL
                                String.valueOf(lista.get(i).getPessoa().getId()), // ID_RESPONSAVEL
                                String.valueOf(lista.get(i).getBaixa().getId()), // ID_BAIXA
                                lista.get(i).getBeneficiario().getNome(), // BENEFICIÁRIO
                                servico_descricao, // SERVICO
                                vencimento, // VENCIMENTO
                                new BigDecimal(lista.get(i).getValorBaixa()), // VALOR BAIXA
                                lista.get(i).getBaixa().getUsuario().getLogin(),
                                lista.get(i).getBaixa().getBaixa(),
                                DataHoje.horaMinuto(),
                                formas[0],
                                formas[1],
                                formas[2],
                                formas[3],
                                formas[4],
                                formas[5],
                                formas[6],
                                formas[7],
                                formas[8],
                                formas[9],
                                formas[10],
                                formas[11],
                                formas[12],
                                formas[13],
                                formas[14],
                                (conveniada.isEmpty()) ? "" : "Empresa Conveniada: " + conveniada,
                                lblVencimento,
                                mensagemConvenio,
                                lista.get(i).getPessoa().getDocumento(),
                                Moeda.converteR$Double(soma_dinheiro + lista.get(i).getBaixa().getTroco()),
                                Moeda.converteR$Double(lista.get(i).getBaixa().getTroco()),
                                Moeda.converteR$Double(soma_outros)
                        )
                );
            }

            File fl = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/RECIBO.jasper"));
            JasperReport jasper = (JasperReport) JRLoader.loadObject(fl);

            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
            if (parameter == null) {
                parameter = new HashedMap();
            }

            JasperPrint print = JasperFillManager.fillReport(jasper, parameter, dtSource);

            lista_jaspers.add(print);

            boolean printPdf = true;

            if (printPdf) {

                byte[] arquivo = JasperExportManager.exportReportToPdf(print);

                salvarRecibo(arquivo, lista.get(0).getBaixa());

//                HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
//                res.setContentType("application/pdf");
//                res.setHeader("Content-disposition", "inline; filename=\"" + "recibo" + ".pdf\"");
//                res.getOutputStream().write(arquivo);
//                res.getCharacterEncoding();
//
//                FacesContext.getCurrentInstance().responseComplete();
            } else {
                // CASO QUEIRA IMPRIMIR EM HTML HTMLPRINT PRINTHTML IMPRIMIRRECIBOHTML TOHTML
                if (lista.get(0).getBaixa().getCaixa() == null) {
                    return false;
                }

                String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/" + "Arquivos/recibo/" + lista.get(0).getBaixa().getCaixa().getCaixa() + "/" + DataHoje.converteData(lista.get(0).getBaixa().getDtBaixa()).replace("/", "-"));
                Diretorio.criar("Arquivos/recibo/" + lista.get(0).getBaixa().getCaixa().getCaixa() + "/" + DataHoje.converteData(lista.get(0).getBaixa().getDtBaixa()).replace("/", "-"));

                String path_arquivo = caminho + String.valueOf(lista.get(0).getBaixa().getUsuario().getId()) + "_" + String.valueOf(lista.get(0).getBaixa().getId()) + ".html";
                File file_arquivo = new File(path_arquivo);

                String n = String.valueOf(lista.get(0).getBaixa().getUsuario().getId()) + "_" + String.valueOf(lista.get(0).getBaixa().getId()) + ".html";
                if (file_arquivo.exists()) {
                    path_arquivo = caminho + String.valueOf(lista.get(0).getBaixa().getUsuario().getId()) + "_" + String.valueOf(lista.get(0).getBaixa().getId()) + "_(2).html";
                    n = String.valueOf(lista.get(0).getBaixa().getUsuario().getId()) + "_" + String.valueOf(lista.get(0).getBaixa().getId()) + "_(2).html";
                }
                JasperExportManager.exportReportToHtmlFile(print, path_arquivo);

                Download download = new Download(n, caminho, "text/html", FacesContext.getCurrentInstance());
                download.baixar();

            }

        } catch (JRException ex) {
            ex.getMessage();
            return false;
        }

        return true;
    }

    public void salvarRecibo(byte[] arquivo, Baixa baixa) {
        if (baixa.getCaixa() == null) {
            return;
        }

        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/" + "Arquivos/recibo/" + baixa.getCaixa().getCaixa() + "/" + DataHoje.converteData(baixa.getDtBaixa()).replace("/", "-"));
        Diretorio.criar("Arquivos/recibo/" + baixa.getCaixa().getCaixa() + "/" + DataHoje.converteData(baixa.getDtBaixa()).replace("/", "-"));

        String path_arquivo = caminho + "/" + String.valueOf(baixa.getUsuario().getId()) + "_" + String.valueOf(baixa.getId()) + ".pdf";
        File file_arquivo = new File(path_arquivo);

        if (file_arquivo.exists()) {
            path_arquivo = caminho + "/" + String.valueOf(baixa.getUsuario().getId()) + "_" + String.valueOf(baixa.getId()) + "_(2).pdf";
        }

        try {
            File fl = new File(path_arquivo);
            FileOutputStream out = new FileOutputStream(fl);
            out.write(arquivo);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public List<JasperPrint> getLista_jaspers() {
        return lista_jaspers;
    }

    public void setLista_jaspers(List<JasperPrint> lista_jaspers) {
        this.lista_jaspers = lista_jaspers;
    }
}
