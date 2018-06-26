package br.com.rtools.movimento;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.ConvencaoServico;
import br.com.rtools.arrecadacao.MensagemConvencao;
import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.arrecadacao.dao.ConvencaoServicoDao;
import br.com.rtools.arrecadacao.dao.AcordoDao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.arrecadacao.dao.GrupoCidadesDao;
import br.com.rtools.arrecadacao.dao.MensagemConvencaoDao;
import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.cobranca.*;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.financeiro.Historico;
import br.com.rtools.financeiro.Lote;
import br.com.rtools.financeiro.MensagemCobranca;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.ServicoContaCobranca;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.financeiro.dao.BoletoDao;
import br.com.rtools.financeiro.dao.FinanceiroDao;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.ContaCobrancaDao;
import br.com.rtools.financeiro.dao.ServicoContaCobrancaDao;
import br.com.rtools.impressao.DemonstrativoAcordo;
import br.com.rtools.impressao.DemonstrativoEPlanilhaAcordoSocial;
import br.com.rtools.impressao.ParametroBoleto;
import br.com.rtools.impressao.ParametroBoletoSocial;
import br.com.rtools.impressao.Promissoria;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.Links;
import br.com.rtools.utilitarios.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import javax.activation.MimetypesFileTypeMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

@ManagedBean
@ViewScoped
public class ImprimirBoleto implements Serializable {

    private String pathPasta = "";
    private byte[] arquivo = new byte[0];

    public HashMap registrarMovimentosAss(Boleto boleto, Double valor, String vencimento) {
        Dao dao = new Dao();

        HashMap hash = new LinkedHashMap();

        switch (boleto.getContaCobranca().getCobrancaRegistrada().getId()) {
            // NECESSÁRIO ARQUIVO REMESSA PARA IMPRIMIR
            case 1:
                if (boleto.getStatusRetorno() == null) {
                    boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 4));
                    boleto.setDtStatusRetorno(DataHoje.dataHoje());
                    dao.update(boleto, true);
                }

                switch (boleto.getStatusRetorno().getId()) {
                    case 1:
                        // BOLETO FOI REJEITADO
                        hash.put("boleto", null);
                        hash.put("mensagem", "Boletos Reijeitados pelo Cobrança Registrada não pode ser impresso! Boleto: (" + boleto.getBoletoComposto() + ")");
                        return hash;
                    case 2:
                        // BOLETO FOI REGISTRADO
                        hash.put("boleto", boleto);
                        hash.put("mensagem", "");
                        return hash;

                    // case 3: NÃO EXISTE POIS BOLETO LIQUIDADO NÃO PODE SER IMPRESSO
                    case 4:
                        // BOLETO JÁ FOI SOLICITADO PARA REGISTRO

                        // MESMO COM COBRANÇA REGISTRADA O BOLETO PODERÁ SER IMPRESSO CASO O VENCIMENTO DO MESMO FOR MENOR QUE 01/09/2017
                        if (DataHoje.maiorData(boleto.getVencimento(), "01/09/2017")) {
                            hash.put("boleto", null);
                            hash.put("mensagem", "Solicitação de Registro enviada! Boleto: (" + boleto.getBoletoComposto() + ")");
                        } else {
                            hash.put("boleto", boleto);
                            hash.put("mensagem", "");
                        }
                        return hash;
                    case 5:
                        // BOLETO JÁ FOI ENVIADO PARA REGISTRO, APENAS AGUARDANDO RETORNO
                        hash.put("boleto", null);
                        hash.put("mensagem", "Aguardando Boleto ser Registrado! Boleto: (" + boleto.getBoletoComposto() + ")");
                        return hash;
                    case 6:
                        Cobranca cobranca = Cobranca.retornaCobranca(null, valor, boleto.getDtVencimento(), boleto);

                        if (cobranca == null) {
                            hash.put("boleto", null);
                            hash.put("mensagem", "Erro ao encontrar Cobrança");
                            return hash;
                        }

                        cobranca.setBoleto(Cobranca.gerarNovoBoleto(boleto, boleto.getVencimento()));
                        if (cobranca.getBoleto() == null) {
                            hash.put("boleto", null);
                            hash.put("mensagem", "Erro ao gerar novo boleto!");
                            return hash;
                        } else {
                            hash.put("boleto", cobranca.getBoleto());
                            hash.put("mensagem", "");
                            return hash;
                        }

                    default:
                        break;
                }

            case 2:
                // CONTINUA PARA O REGISTRO VIA WEB SERVICE
                if (boleto.getStatusRetorno() != null && boleto.getStatusRetorno().getId() == 2) {
                    hash.put("boleto", boleto);
                    hash.put("mensagem", "");
                    return hash;
                }

                break;
            case 3:
                // COBRANÇA SEM REGISTRO APENAS RETORNA O BOLETO
                hash.put("boleto", boleto);
                hash.put("mensagem", "");
                return hash;
        }

        //Cobranca cobranca = Cobranca.retornaCobranca(null, boleto.getValor(), boleto.getDtVencimento(), boleto);
        Cobranca cobranca = Cobranca.retornaCobranca(null, valor, boleto.getDtVencimento(), boleto);

        if (cobranca == null) {
            hash.put("boleto", null);
            hash.put("mensagem", "Erro ao encontrar Cobrança");
            return hash;
        }

        if (boleto.getStatusRetorno() != null && boleto.getStatusRetorno().getId() == 6) {
            cobranca.setBoleto(Cobranca.gerarNovoBoleto(boleto, boleto.getVencimento()));
            if (cobranca.getBoleto() == null) {
                hash.put("boleto", null);
                hash.put("mensagem", "Erro ao gerar novo boleto!");
                return hash;
            }
        }

        RespostaWebService resp = cobranca.registrarBoleto(vencimento);

        if (resp.getBoleto() == null) {
            hash.put("boleto", null);
            hash.put("mensagem", resp.getMensagem());
            return hash;
        }

        hash.put("boleto", resp.getBoleto());
        hash.put("mensagem", "");
        return hash;
    }

    public HashMap registrarMovimentos(List<Movimento> lista, Boolean imprimeVencido) {

        Dao dao = new Dao();

        List<Movimento> listaAdd = new ArrayList();

        HashMap hash = new LinkedHashMap();

        String logs = "";
        for (int i = 0; i < lista.size(); i++) {
            Boleto bol = lista.get(i).getBoleto();
            if (bol == null) {
                if (logs.isEmpty()) {
                    logs = " ** " + "Boleto para o Movimento ID " + lista.get(i).getId() + " não encontrado, contate o Administrador.";
                } else {
                    logs += " ** " + "Boleto para o Movimento ID " + lista.get(i).getId() + " não encontrado, contate o Administrador." + " \n ";
                }
                continue;
            }

            switch (bol.getContaCobranca().getCobrancaRegistrada().getId()) {
                // NECESSÁRIO ARQUIVO REMESSA PARA IMPRIMIR
                case 1:
                    if (bol.getStatusRetorno() == null) {
                        bol.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 4));
                        bol.setDtStatusRetorno(DataHoje.dataHoje());
                        dao.update(bol, true);
                    }

                    if (DataHoje.menorData(bol.getVencimento(), DataHoje.data()) && bol.getStatusRetorno().getId() != 2) {
                        if (!imprimeVencido) {
                            if (logs.isEmpty()) {
                                logs = " ** " + "Boleto vencido não pode ser impresso: (" + bol.getBoletoComposto() + ")";
                            } else {
                                logs += " ** " + "Boleto vencido não pode ser impresso: (" + bol.getBoletoComposto() + ")" + " \n ";
                            }

                            continue;
                        }
                    }

                    switch (bol.getStatusRetorno().getId()) {
                        case 1:
                            // BOLETO FOI REJEITADO
                            if (logs.isEmpty()) {
                                logs = " ** " + "Boletos Reijeitados pelo Cobrança Registrada não pode ser impresso! Boleto: (" + bol.getBoletoComposto() + ")";
                            } else {
                                logs += " ** " + "Boletos Reijeitados pelo Cobrança Registrada não pode ser impresso! Boleto: (" + bol.getBoletoComposto() + ")" + " \n ";
                            }
                            continue;

                        case 2:
                            // BOLETO FOI REGISTRADO
                            listaAdd.add(lista.get(i));
                            continue;
                        // case 3: NÃO EXISTE POIS BOLETO LIQUIDADO NÃO PODE SER IMPRESSO
                        case 4:
                            // BOLETO JÁ FOI SOLICITADO PARA REGISTRO

                            // MESMO COM COBRANÇA REGISTRADA O BOLETO PODERÁ SER IMPRESSO CASO O VENCIMENTO DO MESMO FOR MENOR QUE 01/09/2017
                            if (DataHoje.maiorData(bol.getVencimento(), "01/09/2017")) {

                                if (logs.isEmpty()) {
                                    logs = " ** " + "Solicitação de Registro enviada! Boleto: (" + bol.getBoletoComposto() + ")";
                                } else {
                                    logs += " ** " + "Solicitação de Registro enviada! Boleto: (" + bol.getBoletoComposto() + ")" + " \n ";
                                }
                            } else {
                                listaAdd.add(lista.get(i));
                            }
                            continue;
                        case 5:
                            // BOLETO JÁ FOI ENVIADO PARA REGISTRO, APENAS AGUARDANDO RETORNO
                            if (logs.isEmpty()) {
                                logs = " ** " + "Aguardando Boleto ser Registrado! Boleto: (" + bol.getBoletoComposto() + ")";
                            } else {
                                logs += " ** " + "Aguardando Boleto ser Registrado! Boleto: (" + bol.getBoletoComposto() + ")" + " \n ";
                            }

                            continue;
                        case 6:
                            Cobranca cobranca = Cobranca.retornaCobranca(null, bol.getValor(), bol.getDtVencimento(), bol);

                            if (cobranca == null) {
                                if (logs.isEmpty()) {
                                    logs = " ** " + "Erro ao encontrar Cobrança! Boleto: (" + bol.getBoletoComposto() + ")";
                                } else {
                                    logs += " ** " + "Erro ao encontrar Cobrança Boleto: (" + bol.getBoletoComposto() + ")" + " \n ";
                                }
                                continue;
                            }

                            cobranca.setBoleto(Cobranca.gerarNovoBoleto(bol, bol.getVencimento()));
                            if (cobranca.getBoleto() == null) {
                                if (logs.isEmpty()) {
                                    logs = " ** " + "Erro ao gerar novo Boleto: (" + bol.getBoletoComposto() + ")";
                                } else {
                                    logs += " ** " + "Erro ao gerar novo Boleto: (" + bol.getBoletoComposto() + ")" + " \n ";
                                }
                            } else {
                                listaAdd.add(lista.get(i));
                            }

                            continue;
                        default:
                            break;
                    }

                case 2:
                    // CONTINUA PARA O REGISTRO VIA WEB SERVICE
                    if (DataHoje.menorData(bol.getVencimento(), DataHoje.data()) && bol.getStatusRetorno().getId() != 2) {
                        if (!imprimeVencido) {

                            if (logs.isEmpty()) {
                                logs = " ** " + "Boleto vencido não pode ser impresso: (" + bol.getBoletoComposto() + ")";
                            } else {
                                logs += " ** " + "Boleto vencido não pode ser impresso: (" + bol.getBoletoComposto() + ")" + " \n ";
                            }

                            continue;
                        }
                    }

                    if (bol.getStatusRetorno() != null && bol.getStatusRetorno().getId() == 2) {
                        listaAdd.add(lista.get(i));

                        continue;
                    }

                    break;
                case 3:
                    // COBRANÇA SEM REGISTRO APENAS RETORNA O BOLETO
                    listaAdd.add(lista.get(i));
                    continue;
            }

            if (bol.getValor() < 1) {
                if (logs.isEmpty()) {
                    logs = " ** " + "Valor dos Boleto Registrados não podem ser menores que R$ 1,00, Boleto: (" + bol.getNrBoleto() + ")";
                } else {
                    logs += " ** " + "Valor dos Boleto Registrados não podem ser menores que R$ 1,00, Boleto: (" + bol.getNrBoleto() + ")" + " \n ";
                }
                continue;
            }

            //13/02/2017 
            // na lista de movimento vem o vencimento à ser alterado
            // PEGO O MOVIMENTO ANTIGO PARA QUE O VENCIMENTO fin_movimento.dt_vencimento NÃO SEJA ALTERADO NA IMPRESSÃO QUANDO EXECUTAR update
            // (inicialmente vindo do processamento individual) OBJETO EM QUESTÃO ( lista.get(i) )
            // dt_vencimento NÃO PODE SER ALTERADO QUANDO IMPRIMIR DO processamento individual
//            Movimento mov_antigo = (Movimento) dao.find(lista.get(i));
//            lista.get(i).setVencimento(mov_antigo.getVencimento());
            Cobranca cobranca = Cobranca.retornaCobranca(null, bol.getValor(), bol.getDtVencimento(), bol);

            if (cobranca == null) {
                hash.put("lista", new ArrayList());
                hash.put("mensagem", "Erro ao encontrar Cobrança");
                return hash;
            }

            if (bol.getStatusRetorno() != null && bol.getStatusRetorno().getId() == 6) {
                cobranca.setBoleto(Cobranca.gerarNovoBoleto(bol, bol.getVencimento()));
                if (cobranca.getBoleto() == null) {
                    hash.put("lista", new ArrayList());
                    hash.put("mensagem", "Erro ao gerar novo boleto");
                    return hash;
                }
            }

            RespostaWebService resp = cobranca.registrarBoleto(bol.getVencimento());

            if (resp.getBoleto() == null) {
                hash.put("lista", new ArrayList());
                hash.put("mensagem", resp.getMensagem());
                return hash;
            }

            listaAdd.add(resp.getBoleto().getListaMovimento().get(0));
        }

        if (!logs.isEmpty()) {

            hash.put("lista", new ArrayList());
            hash.put("mensagem", logs);
            return hash;
        }

        hash.put("lista", listaAdd);
        hash.put("mensagem", "");
        return hash;
    }

    public List<Movimento> atualizaContaCobrancaMovimento(List<Movimento> lista) {
        ServicoContaCobrancaDao db = new ServicoContaCobrancaDao();
        ContaCobrancaDao dbc = new ContaCobrancaDao();
        MovimentoDao dbm = new MovimentoDao();
        Dao dao = new Dao();

        List<Movimento> listaAdd = new ArrayList();

        for (int i = 0; i < lista.size(); i++) {
            Boleto bol = dbm.pesquisaBoletos(lista.get(i).getNrCtrBoleto());
            if (bol == null) {
                ContaCobranca cc = dbc.pesquisaServicoCobranca(lista.get(i).getServicos().getId(), lista.get(i).getTipoServico().getId());
                int id_boleto = dbm.inserirBoletoNativo(cc.getId(), lista.get(i).getVencimento(), lista.get(i).getValor());
                bol = (Boleto) dao.find(new Boleto(), id_boleto);
                lista.get(i).setDocumento(bol.getBoletoComposto());
                bol.setNrCtrBoleto(lista.get(i).getNrCtrBoleto());
                dao.openTransaction();
                if (dao.update(lista.get(i)) && dao.update(bol)) {
                    dao.commit();
                } else {
                    dao.rollback();
                }
                continue;
            }

            ServicoContaCobranca scc = db.pesquisaServPorIdServIdTipoServIdContCobranca(lista.get(i).getServicos().getId(), lista.get(i).getTipoServico().getId(), bol.getContaCobranca().getId());
            if (scc == null) {
                continue;
            }

            if (scc.getTipoServico().getId() == 4) {
                Movimento mov = new Movimento(
                        -1,
                        null,
                        scc.getServicos().getPlano5(),
                        lista.get(i).getPessoa(),
                        lista.get(i).getServicos(),
                        null,
                        scc.getTipoServico(),
                        lista.get(i).getAcordo(),
                        lista.get(i).getValor(),
                        lista.get(i).getReferencia(),
                        null, // lista.get(i).getVencimento()
                        1,
                        true,
                        "E",
                        false,
                        lista.get(i).getPessoa(),
                        lista.get(i).getPessoa(),
                        "",
                        "",
                        null, // lista.get(i).getVencimento()
                        0, 0, 0, 0, 0, 0, 0, lista.get(i).getTipoDocumento(), 0, null
                );

                GerarMovimento.inativarUmMovimento(lista.get(i), "REIMPRESSÃO COM NOVO CEDENTE.");
                GerarMovimento.salvarUmMovimento(new Lote(), mov, null, mov.getValor());
                listaAdd.add(mov);

                Historico his = new Historico();
                AcordoDao dba = new AcordoDao();
                Historico his_pesquisa = dba.pesquisaHistoricoMov(bol.getContaCobranca().getId(), lista.get(i).getId());

                if (his_pesquisa != null) {
                    his.setMovimento(mov);
                    his.setHistorico(his_pesquisa.getHistorico());
                    his.setComplemento(his_pesquisa.getComplemento());
                }

                dao.openTransaction();
                if (dao.save(his)) {
                    dao.commit();
                } else {
                    dao.rollback();
                }
            } else {
                Movimento mov = new Movimento(
                        -1,
                        null,
                        scc.getServicos().getPlano5(),
                        lista.get(i).getPessoa(),
                        lista.get(i).getServicos(),
                        null,
                        scc.getTipoServico(),
                        lista.get(i).getAcordo(),
                        lista.get(i).getValor(),
                        lista.get(i).getReferencia(),
                        null, // lista.get(i).getVencimento()
                        1,
                        true,
                        "E",
                        false,
                        lista.get(i).getPessoa(),
                        lista.get(i).getPessoa(),
                        "",
                        "",
                        null, // lista.get(i).getVencimento()
                        0,
                        0, 0, 0, 0, 0, 0, lista.get(i).getTipoDocumento(), 0, null);

                GerarMovimento.inativarUmMovimento(lista.get(i), "REIMPRESSÃO COM NOVO CEDENTE.");

                GerarMovimento.salvarUmMovimento(new Lote(), mov, null, mov.getValor());
                listaAdd.add(mov);
            }
        }
        if (listaAdd.isEmpty()) {
            return lista;
        } else {
            return listaAdd;
        }
    }

    public byte[] imprimirBoleto(List<Movimento> lista, boolean imprimeVerso, boolean imprimeVencido) {
        HashMap hash = registrarMovimentos(lista, imprimeVencido);

        if (((ArrayList) hash.get("lista")).isEmpty()) {
            GenericaMensagem.error("Atenção", hash.get("mensagem").toString());
            return new byte[0];
        }

        // ATUALIZA A LISTA COM OS BOLETOS REGISTRADOS OU MODIFICADOS
        lista.clear();
        lista.addAll((ArrayList) hash.get("lista"));

        MensagemConvencaoDao dbm = new MensagemConvencaoDao();
        GrupoCidadesDao dbgc = new GrupoCidadesDao();
        CnaeConvencaoDao dbco = new CnaeConvencaoDao();
        int i = 0;
        String mensagemErroMovimento = "Movimento(s) sem mensagem: ";
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            Collection vetor = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            JuridicaDao jurDB = new JuridicaDao();
            String swap[] = new String[50];
            PessoaEndereco pe = null;
            MovimentoDao movDB = new MovimentoDao();

            CnaeConvencaoDao cnaeConv = new CnaeConvencaoDao();
            Cobranca cobranca = null;
            BigDecimal valor;
            String mensagem = "";
            MensagemCobranca mensagemCobranca = null;
            Historico historico = null;

            File file_jasper = new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO.jasper"));

            JasperReport jasper = (JasperReport) JRLoader.loadObject(file_jasper);

            while (i < lista.size()) {
                if (lista.get(i).getBaixa() != null && lista.get(i).getBaixa().getId() != -1) {
                    break;
                }

                Boleto boletox = movDB.pesquisaBoletos(lista.get(i).getNrCtrBoleto());
                if (boletox.getContaCobranca().getLayout().getId() == 2) {
                    if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/SINDICAL.jasper")).exists()) {
                        swap[40] = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/SINDICAL.jasper");
                    } else {
                        swap[40] = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/SINDICAL.jasper");
                    }
                } else if (new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/SICOB.jasper")).exists()) {
                    swap[40] = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/SICOB.jasper");
                } else {
                    swap[40] = ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/SICOB.jasper");
                }

                if (boletox.getContaCobranca().getLayoutBarrasNovo()) {
                    if (boletox.getBoletoComposto().length() < 17) {
                        GenericaMensagem.error("Atenção", "Número do boleto deve ter 17 dígitos e começar com 14 para layout novo!");
                        return new byte[0];
                    }
                }

                swap[43] = "";
                swap[42] = "";

                // CHAMADO ROGÉRIO #2337 PEDIU PARA TIRAR O VALOR APENAS NO CODIGO DE BARRAS E REPRESENTAÇÃO
                // CASO BOLETO FOR SEM REGISTRO
                if (boletox.getContaCobranca().getCobrancaRegistrada().getId() == 3) {
                    cobranca = Cobranca.retornaCobranca(lista.get(i).getPessoa().getId(), new Double(0), boletox.getDtVencimento(), boletox);
                } else {
                    cobranca = Cobranca.retornaCobranca(lista.get(i).getPessoa().getId(), boletox.getValor(), boletox.getDtVencimento(), boletox);
                }

                if (boletox.getContaCobranca().getLayout().getId() == Cobranca.SINDICAL) {
                    swap[43] = "Competência (" + lista.get(i).getReferencia() + ")";
                    swap[42] = "BLOQUETO DE CONTRIBUIÇÃO SINDICAL URBANA.";
                }

                try {
                    swap[0] = jurDB.pesquisaJuridicaPorPessoa(lista.get(i).getPessoa().getId()).getContabilidade().getPessoa().getNome();
                } catch (Exception e) {
                    swap[0] = "";
                }

                Convencao conv = new Convencao();
                try {
                    conv = cnaeConv.pesquisarCnaeConvencaoPorPessoa(lista.get(i).getPessoa().getId());
                    swap[1] = conv.getDescricao();
                } catch (Exception e) {
                    swap[1] = "";
                }

                // ENDEREÇO DE COBRANCA DA PESSOA -------------------------------------------------------------------------
                // NO CASO PODE SER OU NÃO O ENDEREÇO DA CONTABILIDADE ----------------------------------------------------
                try {
                    pe = pesEndDB.pesquisaEndPorPessoaTipo(lista.get(i).getPessoa().getId(), 3);
                    swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                    swap[3] = pe.getNumero();
                    swap[4] = pe.getComplemento();
                    swap[5] = pe.getEndereco().getBairro().getDescricao();
                    swap[6] = pe.getEndereco().getCidade().getCidade();
                    swap[7] = pe.getEndereco().getCidade().getUf();
                    swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                } catch (Exception e) {
                    swap[2] = "";
                    swap[3] = "";
                    swap[4] = "";
                    swap[5] = "";
                    swap[6] = "";
                    swap[7] = "";
                    swap[8] = "";
                }

                // ENDEREÇO SOMENTE DA PESSOA -------------------------------------------------------------------------
                int id_cidade_endereco = 0;
                try {
                    pe = pesEndDB.pesquisaEndPorPessoaTipo(lista.get(i).getPessoa().getId(), 2);
                    if (pe == null) {
                        pe = pesEndDB.pesquisaEndPorPessoaTipo(lista.get(i).getPessoa().getId(), 1);
                    } else {
                        //pe = pesEndDB.pesquisaEndPorPessoaTipo(lista.get(i).getPessoa().getId(), 2);
                    }
                    id_cidade_endereco = pe.getEndereco().getCidade().getId();
                    swap[9] = pe.getEndereco().getEnderecoSimplesToString();
                    swap[10] = pe.getNumero();
                    swap[11] = pe.getComplemento();
                    swap[12] = pe.getEndereco().getBairro().getDescricao();
                    swap[13] = pe.getEndereco().getCidade().getCidade();
                    swap[14] = pe.getEndereco().getCidade().getUf();
                    swap[15] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                } catch (Exception e) {
                    swap[9] = "";
                    swap[10] = "";
                    swap[11] = "";
                    swap[12] = "";
                    swap[13] = "";
                    swap[14] = "";
                    swap[15] = "";
                }

                try {
                    swap[16] = boletox.getContaCobranca().getCedente();
                    swap[17] = "";
                } catch (Exception e) {
                    swap[16] = "-1";
                    swap[17] = "";
                }

                // ESSE PEGA O ENDEREÇO COMERCIAL DO SINDICATO.
                try {
                    pe = pesEndDB.pesquisaEndPorPessoaTipo(1, 2);
                    swap[18] = pe.getEndereco().getEnderecoSimplesToString();
                    swap[19] = pe.getNumero();
                    swap[20] = pe.getComplemento();
                    swap[21] = pe.getEndereco().getBairro().getDescricao();
                    swap[22] = pe.getEndereco().getCidade().getCidade();
                    swap[23] = pe.getEndereco().getCidade().getUf();
                    swap[24] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                    swap[30] = pe.getPessoa().getDocumento();
                } catch (Exception e) {
                    swap[18] = "";
                    swap[19] = "";
                    swap[20] = "";
                    swap[21] = "";
                    swap[22] = "";
                    swap[23] = "";
                    swap[24] = "";
                }

                try {
                    swap[26] = cobranca.representacao();
                    swap[27] = cobranca.codigoBarras();
                } catch (Exception e) {
                    swap[26] = "";
                    swap[27] = "";
                }

                // VERIFICA SE O ENDEREÇO DE COBRANCA É IGUAL AO ENDERECO COMERCIAL --------------------------------------------------------
//                if (swap[2].equals(swap[9]) &&
//                    swap[3].equals(swap[10]) &&
//                    swap[4].equals(swap[11])){
//                        swap[0] = "";
//                        swap[2] = "";
//                        swap[3] = "";
//                        swap[4] = "";
//                        swap[5] = "";
//                        swap[6] = "";
//                        swap[7] = "";
//                        swap[8] = "";
//                }
                try {
//                    swap[44] = lista.get(i).getContaCobranca().getCodigoSindical().substring(0, 3) + "." + //codigosindical
//                            lista.get(i).getContaCobranca().getCodigoSindical().substring(3, 6) + "."
//                            + lista.get(i).getContaCobranca().getCodigoSindical().substring(6, lista.get(i).getContaCobranca().getCodigoSindical().length()) + "-"
//                            + cobranca.moduloOnze(lista.get(i).getContaCobranca().getCodigoSindical());
                    FilialDao filialDB = new FilialDao();
                    String entidade = filialDB.pesquisaRegistroPorFilial(1).getTipoEntidade();
                    String sicas = boletox.getContaCobranca().getSicasSindical();
                    if (entidade.equals("S")) {
                        swap[44] = "S-" + sicas;
                    } else if (entidade.equals("C")) {
                        swap[44] = "C-" + sicas.substring(sicas.length() - 3, sicas.length());
                    } else if (entidade.equals("F")) {
                        swap[44] = "F-" + sicas.substring(sicas.length() - 3, sicas.length());
                    }
                } catch (Exception e) {
                    swap[44] = "";
                }

                valor = new BigDecimal(boletox.getValor());
                if (valor.toString().equals("0")) {
                    valor = null;
                }

                ConvencaoCidadeDao dbCon = new ConvencaoCidadeDao();
                historico = movDB.pesquisaHistorico(lista.get(i).getId());
                if (historico != null) {
                    if (historico.getHistorico().isEmpty() && historico.getComplemento().isEmpty()) {
                        new Dao().delete(historico, true);
                        historico = null;
                    }
                }

                // <!-- 
                /**
                 * 1° Boletos convencionais pegarão sempre as mensagens da
                 * tabela: arr_mensagem_convencao; 2º Boletos tipo acordo
                 * pegarão sempre a mensagem do contribuínte na tabela
                 * fin_historico e a mensagem de cobrança (Doc. do banco:
                 * arr_mensagem_convencao.ds_referencia = ''); 3° Boletos de
                 * mensagens avulsas, que não se enquadram na convenção sempre
                 * existirá, um registro em fin_historico pegando mensagem do
                 * contribuinte no campo ds_historico e doc. do banco em
                 * ds_complemento;
                 */
                if (historico == null) {
                    mensagemCobranca = movDB.pesquisaMensagemCobranca(lista.get(i).getId());
                    mensagem = mensagemCobranca.getMensagemConvencao().getMensagemContribuinte();//mensagem
                    swap[25] = mensagemCobranca.getMensagemConvencao().getMensagemCompensacao();
                } else {
                    mensagem = historico.getHistorico();
                    swap[25] = historico.getComplemento();
                    if (lista.get(i).getTipoServico().getId() == 4) {
                        Convencao convencao = dbco.pesquisarCnaeConvencaoPorPessoa(lista.get(i).getPessoa().getId());
                        if (convencao == null) {
                            return arquivo;
                        }
                        MensagemConvencao mc = dbm.verificaMensagem(convencao.getId(),
                                lista.get(i).getServicos().getId(),
                                lista.get(i).getTipoServico().getId(),
                                dbgc.grupoCidadesPorPessoa(lista.get(i).getPessoa().getId(), convencao.getId()).getId(), "");
                        if (mc == null) {
                            return arquivo;
                        }
                        // mensagemCobranca = movDB.pesquisaMensagemCobranca(lista.get(i).getId());
                        // swap[25] = mensagemCobranca.getMensagemConvencao().getMensagemCompensacao();
                        swap[25] = mc.getMensagemCompensacao();
                    }
                }
                // -->

                mensagemErroMovimento += " " + swap[0] + "\n "
                        + lista.get(i).getPessoa().getNome() + "\n"
                        + lista.get(i).getDocumento() + "\n";

                if ((historico == null) && (mensagemCobranca == null)) {
                    break;
                }

                String codc = cobranca.getCedenteFormatado();
                // CAIXA EXIGE QUE SE COLOQUE O AGENCIA/COD SINDICAL NA FICHA DE COMPENSACAO NO LUGAR DO AG/COD CEDENDE,
                // POREM CONCATENANDO COM O DIGITO VERIFICADOR DO COD CEDENTE EX.
                // 0242/004.136.02507-5 >>>>> FICARA : 0242/S02507-5

                String referencia = "Ref:. " + lista.get(i).getReferencia(), descricaoServico = "Contribuição:. " + lista.get(i).getServicos().getDescricao();
                if (boletox.getContaCobranca().getLayout().getId() == 2) {
                    //codc = swap[44] + "-" + codc.substring(codc.length() - 1, codc.length()); 17/03/2014 -- HOMOLOGAÇÃO DE ARCERBURGO EXIRGIU A RETIRADA DESDE DV
                    //codc = swap[44]; // NOVA VERSÃO VOLTA A SER COMO ANTES // 0242/S02507-5 >>>>> FICARA : ???.004.136.02507-5
                } else {
                    // SE NÃO FOR SINDICAL E FOR ACORDO NÃO MOSTRAR REFERÊNCIA 
                    if (lista.get(i).getServicos().getId() != 1 && lista.get(i).getTipoServico().getId() == 4) {
                        referencia = "";
                    }

                    if (!lista.get(i).getServicos().isBoleto()) {
                        descricaoServico = "";
                    }

                    ConvencaoServico cservico = new ConvencaoServicoDao().pesquisaConvencaoServico(conv.getId(), dbCon.pesquisaGrupoCidadeJuridica(conv.getId(), id_cidade_endereco).getId());
                    if (cservico != null) {
                        descricaoServico = cservico.getClausula() + " - " + descricaoServico;
                    }
                }

                vetor.add(new ParametroBoleto(
                        referencia, // ref (referencia)
                        imprimeVerso, // imprimeVerso
                        swap[0], //escritorio
                        descricaoServico, //  contribuicao (servico)
                        lista.get(i).getTipoServico().getDescricao(), // tipo
                        swap[1], //  grupo (convencao)
                        lista.get(i).getPessoa().getDocumento(), // cgc (cnpj)
                        lista.get(i).getPessoa().getNome(), //  sacado
                        valor, //  valor
                        swap[2],//endereco
                        swap[3],//numero
                        swap[4],//complemento
                        swap[5],//bairro
                        swap[6],//cidade
                        swap[7],//estado
                        swap[8],//cep
                        lista.get(i).getDocumento(),// boleto
                        swap[9],// sacado_endereco
                        swap[10],//sacado_numero
                        swap[11],//sacado_complemento
                        swap[12],//sacado_bairro
                        swap[13],//sacado_cidade
                        swap[14],//sacado_estado
                        swap[15],//sacado_cep
                        cobranca.getNossoNumeroFormatado(),//nossonum (nosso numero)
                        boletox.getDtProcessamentoString(),// datadoc
                        boletox.getVencimento(),// VENCIMENTO
                        cobranca.codigoBanco(),// codbanco
                        boletox.getContaCobranca().getMoeda(),//moeda
                        boletox.getContaCobranca().getEspecieMoeda(),// especie_doc
                        boletox.getContaCobranca().getEspecieDoc(),//especie
                        cobranca.getAgenciaFormatada(),//cod_agencia
                        codc,//codcedente
                        boletox.getContaCobranca().getAceite(),//aceite
                        boletox.getContaCobranca().getCarteira(),//carteira
                        boletox.getContaCobranca().getLayout().getId() == 2 ? lista.get(i).getReferencia() : lista.get(i).getReferencia().substring(3),// competência / exercicio
                        swap[16],//nomeentidade
                        swap[40],
                        mensagem,//movDB.pesquisaMensagemCobranca(lista.get(i).getId()).getMensagemConvencao().getMensagemContribuinte(),//mensagem
                        boletox.getContaCobranca().getLocalPagamento(),//local_pag
                        swap[18],//endent
                        swap[19],//nument (numero entidade)
                        swap[20],//compent
                        swap[21],//baient
                        swap[22],//cident
                        swap[23],//estent
                        swap[24],//cepent
                        swap[30],//cgcent
                        swap[26],//REPNUM
                        swap[27],//CODBAR
                        swap[25],//mensagem_boleto
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath(boletox.getContaCobranca().getContaBanco().getBanco().getLogo().trim()),
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"),//logoEmpresa
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/serrilha.GIF"),//serrilha
                        jurDB.pesquisaJuridicaPorPessoa(lista.get(i).getPessoa().getId()).getCnae().getNumero().substring(0, 3),//cnae
                        boletox.getContaCobranca().getCategoriaSindical(),//categoria
                        swap[44], //codigosindical
                        swap[43], //usoBanco
                        swap[42], //textoTitulo
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_VERSO.jasper"),//caminhoVerso
                        boletox.getContaCobranca().getContaBanco().getFilial().getFilial().getPessoa().getNome(),
                        descricaoServico
                ));
                i++;
            }

            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
            JasperPrint print = JasperFillManager.fillReport(
                    jasper,
                    null,
                    dtSource);
            arquivo = JasperExportManager.exportReportToPdf(print);
            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
            File f = new File(pathPasta);
            if (!f.exists()) {
                f.mkdirs();
            }

        } catch (Exception e) {
            int x = i;
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + e.getMessage() + " " + mensagemErroMovimento);
        }
        return arquivo;
    }

    public RespostaArquivoRemessa imprimirRemessa(List<BoletoRemessa> lista_boleto_remessa) {
        Cobranca cobranca = Cobranca.retornaCobrancaRemessa(lista_boleto_remessa);
        Boleto boletox = lista_boleto_remessa.get(0).getBoleto();

        if (cobranca != null) {
            RespostaArquivoRemessa resp = boletox.getContaCobranca().getNrLayout() == 240 ? cobranca.gerarRemessa240() : cobranca.gerarRemessa400();

            if (resp.getArquivo() != null) {
                Dao dao = new Dao();
                for (BoletoRemessa br : lista_boleto_remessa) {
                    Boleto b = br.getBoleto();
                    b.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 5));
                    b.setDtStatusRetorno(DataHoje.dataHoje());
                    dao.update(b, true);
                }
            }

            Boolean zipar = false;
            if (zipar) {
                Zip zip = new Zip();
                List<File> lf = new ArrayList();

                lf.add(resp.getArquivo());
                File file_destino = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/remessa/remessa.rar"));
                try {
                    zip.zip(lf, file_destino);
                } catch (Exception e) {
                    return new RespostaArquivoRemessa(null, e.getMessage());
                }

                resp.setArquivo(file_destino);
            }
            return resp;
        }
        return new RespostaArquivoRemessa(null, "CONTA COBRANÇA NÃO ENCONTRADA");
    }

    public byte[] imprimirAcordo(List<Movimento> lista, Acordo acordo, Historico historico, boolean imprimir_pro) {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            JasperReport jasper;
            Collection vetor = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe = null;
            JuridicaDao jurDB = new JuridicaDao();
            Juridica juridica = new Juridica();
            int i = 0;
            String swap[] = new String[35];
            Pessoa pessoa = null;
            Filial filial = null;

            if (!lista.isEmpty()) {
                ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
                cab.init();
                filial = cab.getConfiguracaoArrecadacao().getFilial();
                pessoa = lista.get(0).getPessoa();
            }

            try {
                juridica = jurDB.pesquisaJuridicaPorPessoa(pessoa.getId());
                swap[0] = juridica.getPessoa().getNome();
                swap[1] = juridica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 2);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[23] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[23] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(filial.getFilial().getPessoa().getId(), 3);
                swap[9] = filial.getFilial().getPessoa().getNome();
                swap[10] = filial.getFilial().getPessoa().getTipoDocumento().getDescricao();
                swap[11] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[12] = pe.getEndereco().getLogradouro().getDescricao();
                swap[13] = pe.getNumero();
                swap[14] = pe.getComplemento();
                swap[15] = pe.getEndereco().getBairro().getDescricao();
                swap[16] = pe.getEndereco().getCidade().getCidade();
                swap[17] = pe.getEndereco().getCidade().getUf();
                swap[18] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[19] = filial.getFilial().getPessoa().getDocumento();
                swap[20] = filial.getFilial().getPessoa().getTelefone1();
                swap[21] = filial.getFilial().getPessoa().getSite();
                swap[22] = filial.getFilial().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[9] = "";
                swap[10] = "";
                swap[11] = "";
                swap[12] = "";
                swap[13] = "";
                swap[14] = "";
                swap[15] = "";
                swap[16] = "";
                swap[17] = "";
                swap[18] = "";
                swap[19] = "";
                swap[20] = "";
                swap[21] = "";
                swap[22] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3);
                swap[34] = juridica.getContabilidade().getPessoa().getNome();
                swap[24] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[25] = pe.getEndereco().getLogradouro().getDescricao();
                swap[26] = pe.getNumero();
                swap[27] = pe.getComplemento();
                swap[28] = pe.getEndereco().getBairro().getDescricao();
                swap[29] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[30] = pe.getEndereco().getCidade().getCidade();
                swap[31] = pe.getEndereco().getCidade().getUf();
                swap[32] = juridica.getContabilidade().getPessoa().getTelefone1();
                swap[33] = juridica.getContabilidade().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[34] = "";
                swap[24] = "";
                swap[25] = "";
                swap[26] = "";
                swap[27] = "";
                swap[28] = "";
                swap[29] = "";
                swap[30] = "";
                swap[31] = "";
                swap[32] = "";
                swap[33] = "";
            }

            MovimentoDao db = new MovimentoDao();

            while (i < lista.size()) {
                Boleto boleto = db.pesquisaBoletos(lista.get(i).getNrCtrBoleto());

                BigDecimal valor = new BigDecimal(0), multa = new BigDecimal(0), juros = new BigDecimal(0), correcao = new BigDecimal(0), desconto = new BigDecimal(0);

                vetor.add(new DemonstrativoAcordo(
                        acordo.getId(), // codacordo
                        acordo.getData(), // data
                        acordo.getContato(), // contato
                        swap[0], // razao
                        swap[1], // cnpj
                        swap[2], //endereco
                        swap[3], // numero
                        swap[4], // complemento
                        swap[5], // bairro
                        swap[6], // cidade
                        swap[8], // cep
                        swap[7], // uf
                        swap[23], // telefone
                        historico.getHistorico(), // obs
                        lista.get(i).getServicos().getDescricao(), // desc_contribuicao
                        lista.get(i).getDocumento(), // boleto
                        lista.get(i).getVencimento(), // vencto
                        new BigDecimal(lista.get(i).getValor()), // vlrpagar
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                        swap[9], // sinNome
                        swap[11], // sinEndereco
                        swap[12], // sinLogradouro
                        swap[13], // sinNumero
                        swap[14], // sinComplemento
                        swap[15], // sinBairro
                        swap[18], // sinCep
                        swap[16], // sinCidade
                        swap[17], // sinUF
                        swap[20], // sinTelefone
                        swap[22], // sinEmail
                        swap[21], // sinSite
                        swap[10], // sinTipoDocumento
                        swap[19], // sinDocumento
                        swap[34], // escNome
                        swap[24], // escEndereco
                        swap[25], // escLogradouro
                        swap[26], // escNumero
                        swap[27], // escComplemento
                        swap[28], // escBairro
                        swap[29], // escCep
                        swap[30], // escCidade
                        swap[31], // escUF
                        swap[32], // escTelefone
                        swap[33], // escEmail
                        valor,
                        multa,
                        juros,
                        correcao,
                        desconto,
                        lista.get(i).getTipoServico().getDescricao(),
                        lista.get(i).getReferencia(),
                        "Planilha de Débito Referente ao Acordo Número " + acordo.getId(),
                        acordo.getUsuario().getPessoa().getNome(),
                        acordo.getEmail()
                ));
                i++;
            }

            List ljasper = new ArrayList();
            //* JASPER 1 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/DEMOSTRATIVO_ACORDO.jasper"))
            );
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
            ljasper.add(JasperFillManager.fillReport(jasper, null, dtSource));
            //* ------------- *//

            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream retorno = new ByteArrayOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, ljasper);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
            exporter.exportReport();

            arquivo = retorno.toByteArray();

            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
        return arquivo;
    }

    public byte[] imprimirPromissoria(List<Movimento> lista, boolean imprimirVerso) {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            JasperReport jasper;
            Collection vetor = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe = null;
            JuridicaDao jurDB = new JuridicaDao();
            Juridica juridica = new Juridica();
            int i = 0;
            String swap[] = new String[35];
            Pessoa pessoa = null;
            Filial filial = null;

            if (!lista.isEmpty()) {
                ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
                cab.init();
                filial = cab.getConfiguracaoArrecadacao().getFilial();
                pessoa = lista.get(0).getPessoa();
            }

            try {
                juridica = jurDB.pesquisaJuridicaPorPessoa(pessoa.getId());
                swap[0] = juridica.getPessoa().getNome();
                swap[1] = juridica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 2);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[23] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[23] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(filial.getFilial().getPessoa().getId(), 3);
                swap[9] = filial.getFilial().getPessoa().getNome();
                swap[10] = filial.getFilial().getPessoa().getTipoDocumento().getDescricao();
                swap[11] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[12] = pe.getEndereco().getLogradouro().getDescricao();
                swap[13] = pe.getNumero();
                swap[14] = pe.getComplemento();
                swap[15] = pe.getEndereco().getBairro().getDescricao();
                swap[16] = pe.getEndereco().getCidade().getCidade();
                swap[17] = pe.getEndereco().getCidade().getUf();
                swap[18] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[19] = filial.getFilial().getPessoa().getDocumento();
                swap[20] = filial.getFilial().getPessoa().getTelefone1();
                swap[21] = filial.getFilial().getPessoa().getSite();
                swap[22] = filial.getFilial().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[9] = "";
                swap[10] = "";
                swap[11] = "";
                swap[12] = "";
                swap[13] = "";
                swap[14] = "";
                swap[15] = "";
                swap[16] = "";
                swap[17] = "";
                swap[18] = "";
                swap[19] = "";
                swap[20] = "";
                swap[21] = "";
                swap[22] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3);
                swap[34] = juridica.getContabilidade().getPessoa().getNome();
                swap[24] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[25] = pe.getEndereco().getLogradouro().getDescricao();
                swap[26] = pe.getNumero();
                swap[27] = pe.getComplemento();
                swap[28] = pe.getEndereco().getBairro().getDescricao();
                swap[29] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[30] = pe.getEndereco().getCidade().getCidade();
                swap[31] = pe.getEndereco().getCidade().getUf();
                swap[32] = juridica.getContabilidade().getPessoa().getTelefone1();
                swap[33] = juridica.getContabilidade().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[34] = "";
                swap[24] = "";
                swap[25] = "";
                swap[26] = "";
                swap[27] = "";
                swap[28] = "";
                swap[29] = "";
                swap[30] = "";
                swap[31] = "";
                swap[32] = "";
                swap[33] = "";
            }

            while (i < lista.size()) {
                ValorExtenso ve = new ValorExtenso(new BigDecimal(lista.get(i).getValor()));
                vetor.add(
                        new Promissoria(
                                "(" + lista.get(i).getAcordo().getId() + ") " + (i + 1) + "/" + lista.size(), // numero
                                ve.toString(), // extenso
                                new BigDecimal(lista.get(i).getValor()), // valor
                                swap[0], // razao
                                juridica.getPessoa().getTipoDocumento().getId() == 4 ? "" : juridica.getPessoa().getTipoDocumento().getDescricao(), // tipodocumento 
                                juridica.getPessoa().getTipoDocumento().getId() == 4 ? "" : juridica.getPessoa().getDocumento(), // documento
                                swap[2], // endereco
                                swap[4], // complemento
                                swap[3], // numero
                                swap[5], // bairro
                                swap[6], // cidade
                                swap[8], // cep
                                swap[7], // uf
                                swap[9], // sinnome
                                swap[19], // sinDocumento
                                swap[16], // sinCidade
                                swap[17], // sinUF
                                lista.get(i).getVencimento(),//DataHoje.DataToArray(lista.get(i).getVencimento())[2]+"-"+DataHoje.DataToArray(lista.get(i).getVencimento())[1]+"-"+DataHoje.DataToArray(lista.get(i).getVencimento())[0], // vencto
                                ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/promissoria.jpg"),
                                DataHoje.dataExtenso(DataHoje.data()) // fundo_promissoria
                        )
                );
                i++;
            }

            List ljasper = new ArrayList();
            //* JASPER 1 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/PROMISSORIA.jasper"))
            );
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
            ljasper.add(JasperFillManager.fillReport(jasper, null, dtSource));
            //* ------------- *//

            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream retorno = new ByteArrayOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, ljasper);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
            exporter.exportReport();

            arquivo = retorno.toByteArray();

            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
        return arquivo;
    }

    public byte[] imprimirPlanilha(List<Movimento> lista, List<Double> listaValores, boolean calculo, boolean imprimirVerso) {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            JasperReport jasper;
            Collection vetor = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe = null;
            JuridicaDao jurDB = new JuridicaDao();
            Juridica juridica = new Juridica();
            int i = 0;
            String swap[] = new String[35];
            Pessoa pessoa = null;
            Filial filial = null;

            if (!lista.isEmpty()) {
                ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
                cab.init();
                filial = cab.getConfiguracaoArrecadacao().getFilial();
                pessoa = lista.get(0).getPessoa();
            }

            try {
                juridica = jurDB.pesquisaJuridicaPorPessoa(pessoa.getId());
                swap[0] = juridica.getPessoa().getNome();
                swap[1] = juridica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 2);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[23] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[23] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(filial.getFilial().getPessoa().getId(), 3);
                swap[9] = filial.getFilial().getPessoa().getNome();
                swap[10] = filial.getFilial().getPessoa().getTipoDocumento().getDescricao();
                swap[11] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[12] = pe.getEndereco().getLogradouro().getDescricao();
                swap[13] = pe.getNumero();
                swap[14] = pe.getComplemento();
                swap[15] = pe.getEndereco().getBairro().getDescricao();
                swap[16] = pe.getEndereco().getCidade().getCidade();
                swap[17] = pe.getEndereco().getCidade().getUf();
                swap[18] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[19] = filial.getFilial().getPessoa().getDocumento();
                swap[20] = filial.getFilial().getPessoa().getTelefone1();
                swap[21] = filial.getFilial().getPessoa().getSite();
                swap[22] = filial.getFilial().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[9] = "";
                swap[10] = "";
                swap[11] = "";
                swap[12] = "";
                swap[13] = "";
                swap[14] = "";
                swap[15] = "";
                swap[16] = "";
                swap[17] = "";
                swap[18] = "";
                swap[19] = "";
                swap[20] = "";
                swap[21] = "";
                swap[22] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3);
                swap[34] = juridica.getContabilidade().getPessoa().getNome();
                swap[24] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[25] = pe.getEndereco().getLogradouro().getDescricao();
                swap[26] = pe.getNumero();
                swap[27] = pe.getComplemento();
                swap[28] = pe.getEndereco().getBairro().getDescricao();
                swap[29] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[30] = pe.getEndereco().getCidade().getCidade();
                swap[31] = pe.getEndereco().getCidade().getUf();
                swap[32] = juridica.getContabilidade().getPessoa().getTelefone1();
                swap[33] = juridica.getContabilidade().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[34] = "";
                swap[24] = "";
                swap[25] = "";
                swap[26] = "";
                swap[27] = "";
                swap[28] = "";
                swap[29] = "";
                swap[30] = "";
                swap[31] = "";
                swap[32] = "";
                swap[33] = "";
            }

            MovimentoDao db = new MovimentoDao();

            while (i < lista.size()) {
                BigDecimal valor, multa, juros, correcao, desconto;
                List<Vector> lAcres = new Vector();
                MovimentoDao dbm = new MovimentoDao();

                if (calculo) {
                    lAcres = dbm.pesquisaAcrescimo(lista.get(i).getId());
                    if (lAcres.isEmpty()) {
                        valor = new BigDecimal(0);
                        multa = new BigDecimal(0);
                        juros = new BigDecimal(0);
                        correcao = new BigDecimal(0);
                        desconto = new BigDecimal(0);
                    } else {
                        valor = new BigDecimal(((Double) lAcres.get(0).get(0)).doubleValue());
                        multa = new BigDecimal(((Double) lAcres.get(0).get(1)).doubleValue());
                        juros = new BigDecimal(((Double) lAcres.get(0).get(2)).doubleValue());
                        correcao = new BigDecimal(((Double) lAcres.get(0).get(3)).doubleValue());
                        desconto = new BigDecimal(((Double) lAcres.get(0).get(4)).doubleValue());
                    }
                } else {
                    valor = new BigDecimal(lista.get(i).getValorBaixa());
                    multa = new BigDecimal(lista.get(i).getMulta());
                    juros = new BigDecimal(lista.get(i).getJuros());
                    correcao = new BigDecimal(lista.get(i).getCorrecao());
                    desconto = new BigDecimal(lista.get(i).getDesconto());
                }

                vetor.add(new DemonstrativoAcordo(
                        //acordo.getId(), // codacordo
                        //acordo.getData(), // data
                        //acordo.getContato(), // contato
                        0, // codacordo
                        lista.get(i).getVencimento(), // data
                        "", // contato
                        swap[0], // razao
                        swap[1], // cnpj
                        swap[2], //endereco
                        swap[3], // numero
                        swap[4], // complemento
                        swap[5], // bairro
                        swap[6], // cidade
                        swap[8], // cep
                        swap[7], // uf
                        swap[23], // telefone
                        //                        historico.getHistorico(), // obs
                        "", // obs
                        lista.get(i).getServicos().getDescricao(), // desc_contribuicao
                        lista.get(i).getDocumento(), // boleto
                        lista.get(i).getVencimento(), // vencto
                        valor, // vlrpagar
                        ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                        swap[9], // sinNome
                        swap[11], // sinEndereco
                        swap[12], // sinLogradouro
                        swap[13], // sinNumero
                        swap[14], // sinComplemento
                        swap[15], // sinBairro
                        swap[18], // sinCep
                        swap[16], // sinCidade
                        swap[17], // sinUF
                        swap[20], // sinTelefone
                        swap[22], // sinEmail
                        swap[21], // sinSite
                        swap[10], // sinTipoDocumento
                        swap[19], // sinDocumento
                        swap[34], // escNome
                        swap[24], // escEndereco
                        swap[25], // escLogradouro
                        swap[26], // escNumero
                        swap[27], // escComplemento
                        swap[28], // escBairro
                        swap[29], // escCep
                        swap[30], // escCidade
                        swap[31], // escUF
                        swap[32], // escTelefone
                        swap[33], // escEmail
                        //new BigDecimal(lista.get(i).getValor()),
                        new BigDecimal(listaValores.get(i)),
                        multa,
                        juros,
                        correcao,
                        desconto,
                        lista.get(i).getTipoServico().getDescricao(),
                        lista.get(i).getReferencia(),
                        "Planilha de Débito",
                        "",
                        ""));
                i++;
            }

            List ljasper = new ArrayList();
            //* JASPER 1 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/PLANILHA_DE_DEBITO.jasper"))
            );
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor);
            ljasper.add(JasperFillManager.fillReport(jasper, null, dtSource));
            //* ------------- *//

            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream retorno = new ByteArrayOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, ljasper);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
            exporter.exportReport();

            arquivo = retorno.toByteArray();

            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
        return arquivo;
    }

    public byte[] imprimirAcordoAcordado(List<Movimento> lista, Acordo acordo, String historico, boolean imprimirVerso) {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            JasperReport jasper;
            Collection vetor1 = new ArrayList(), vetor2 = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe = null;
            JuridicaDao jurDB = new JuridicaDao();
            Juridica juridica = new Juridica();
            int i = 0;
            String swap[] = new String[35];
            Pessoa pessoa = null;
            Filial filial = null;

            if (!lista.isEmpty()) {
                ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
                cab.init();
                filial = cab.getConfiguracaoArrecadacao().getFilial();
                pessoa = lista.get(0).getPessoa();
            }
            try {
                juridica = jurDB.pesquisaJuridicaPorPessoa(pessoa.getId());
                swap[0] = juridica.getPessoa().getNome();
                swap[1] = juridica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 2);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[23] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[23] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(filial.getFilial().getPessoa().getId(), 3);
                swap[9] = filial.getFilial().getPessoa().getNome();
                swap[10] = filial.getFilial().getPessoa().getTipoDocumento().getDescricao();
                swap[11] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[12] = pe.getEndereco().getLogradouro().getDescricao();
                swap[13] = pe.getNumero();
                swap[14] = pe.getComplemento();
                swap[15] = pe.getEndereco().getBairro().getDescricao();
                swap[16] = pe.getEndereco().getCidade().getCidade();
                swap[17] = pe.getEndereco().getCidade().getUf();
                swap[18] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[19] = filial.getFilial().getPessoa().getDocumento();
                swap[20] = filial.getFilial().getPessoa().getTelefone1();
                swap[21] = filial.getFilial().getPessoa().getSite();
                swap[22] = filial.getFilial().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[9] = "";
                swap[10] = "";
                swap[11] = "";
                swap[12] = "";
                swap[13] = "";
                swap[14] = "";
                swap[15] = "";
                swap[16] = "";
                swap[17] = "";
                swap[18] = "";
                swap[19] = "";
                swap[20] = "";
                swap[21] = "";
                swap[22] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3);
                swap[34] = juridica.getContabilidade().getPessoa().getNome();
                swap[24] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[25] = pe.getEndereco().getLogradouro().getDescricao();
                swap[26] = pe.getNumero();
                swap[27] = pe.getComplemento();
                swap[28] = pe.getEndereco().getBairro().getDescricao();
                swap[29] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[30] = pe.getEndereco().getCidade().getCidade();
                swap[31] = pe.getEndereco().getCidade().getUf();
                swap[32] = juridica.getContabilidade().getPessoa().getTelefone1();
                swap[33] = juridica.getContabilidade().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[34] = "";
                swap[24] = "";
                swap[25] = "";
                swap[26] = "";
                swap[27] = "";
                swap[28] = "";
                swap[29] = "";
                swap[30] = "";
                swap[31] = "";
                swap[32] = "";
                swap[33] = "";
            }

            MovimentoDao db = new MovimentoDao();

            List<Vector> lAcres = new Vector();

            while (i < lista.size()) {
                Boleto boleto = db.pesquisaBoletos(lista.get(i).getNrCtrBoleto());

                lAcres = db.pesquisaAcrescimo(lista.get(i).getId());
                BigDecimal valor, multa, juros, correcao, desconto;

                if (lAcres.isEmpty()) {
                    valor = new BigDecimal(0);
                    multa = new BigDecimal(0);
                    juros = new BigDecimal(0);
                    correcao = new BigDecimal(0);
                    desconto = new BigDecimal(0);
                } else {
                    valor = new BigDecimal(((Double) lAcres.get(0).get(0)).doubleValue());
                    multa = new BigDecimal(((Double) lAcres.get(0).get(1)).doubleValue());
                    juros = new BigDecimal(((Double) lAcres.get(0).get(2)).doubleValue());
                    correcao = new BigDecimal(((Double) lAcres.get(0).get(3)).doubleValue());
                    desconto = new BigDecimal(((Double) lAcres.get(0).get(4)).doubleValue());

//                    valor = new BigDecimal(Moeda.subtracaoValores(
//                                    Moeda.somaValores(Moeda.somaValores(multa.doubleValue(), juros.doubleValue()), correcao.doubleValue()), desconto.doubleValue()
//                                )
//                    );
                }

                BigDecimal valor_calculado = new BigDecimal(Moeda.soma(lista.get(i).getValor(), Moeda.subtracao(
                        Moeda.soma(Moeda.soma(multa.doubleValue(), juros.doubleValue()), correcao.doubleValue()), desconto.doubleValue())));

                if (lista.get(i).getTipoServico().getId() == 4 && lista.get(i).isAtivo()) {
                    vetor1.add(new DemonstrativoAcordo(
                            acordo.getId(), // codacordo
                            acordo.getData(), // data
                            acordo.getContato(), // contato
                            swap[0], // razao
                            swap[1], // cnpj
                            swap[2], //endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[8], // cep
                            swap[7], // uf
                            swap[23], // telefone
                            historico, // obs
                            lista.get(i).getServicos().getDescricao(), // desc_contribuicao
                            lista.get(i).getDocumento(), // boleto
                            lista.get(i).getVencimento(), // vencto
                            //new BigDecimal(lista.get(i).getValor()), // vlrpagar
                            valor_calculado, // vlrpagar
                            ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                            swap[9], // sinNome
                            swap[11], // sinEndereco
                            swap[12], // sinLogradouro
                            swap[13], // sinNumero
                            swap[14], // sinComplemento
                            swap[15], // sinBairro
                            swap[18], // sinCep
                            swap[16], // sinCidade
                            swap[17], // sinUF
                            swap[20], // sinTelefone
                            swap[22], // sinEmail
                            swap[21], // sinSite
                            swap[10], // sinTipoDocumento
                            swap[19], // sinDocumento
                            swap[34], // escNome
                            swap[24], // escEndereco
                            swap[25], // escLogradouro
                            swap[26], // escNumero
                            swap[27], // escComplemento
                            swap[28], // escBairro
                            swap[29], // escCep
                            swap[30], // escCidade
                            swap[31], // escUF
                            swap[32], // escTelefone
                            swap[33], // escEmail
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            lista.get(i).getTipoServico().getDescricao(),
                            lista.get(i).getReferencia(),
                            "Planilha de Débito Referente ao Acordo Número " + acordo.getId(),
                            acordo.getUsuario().getPessoa().getNome(),
                            acordo.getEmail()
                    ));
                } else if (!lista.get(i).isAtivo()) {
                    vetor2.add(new DemonstrativoAcordo(
                            acordo.getId(), // codacordo
                            acordo.getData(), // data
                            acordo.getContato(), // contato
                            swap[0], // razao
                            swap[1], // cnpj
                            swap[2], //endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[8], // cep
                            swap[7], // uf
                            swap[23], // telefone
                            historico, // obs
                            lista.get(i).getServicos().getDescricao(), // desc_contribuicao
                            lista.get(i).getDocumento(), // boleto
                            lista.get(i).getVencimento(), // vencto
                            //new BigDecimal(lista.get(i).getValor()), // vlrpagar
                            valor_calculado, // vlrpagar
                            ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                            swap[9], // sinNome
                            swap[11], // sinEndereco
                            swap[12], // sinLogradouro
                            swap[13], // sinNumero
                            swap[14], // sinComplemento
                            swap[15], // sinBairro
                            swap[18], // sinCep
                            swap[16], // sinCidade
                            swap[17], // sinUF
                            swap[20], // sinTelefone
                            swap[22], // sinEmail
                            swap[21], // sinSite
                            swap[10], // sinTipoDocumento
                            swap[19], // sinDocumento
                            swap[34], // escNome
                            swap[24], // escEndereco
                            swap[25], // escLogradouro
                            swap[26], // escNumero
                            swap[27], // escComplemento
                            swap[28], // escBairro
                            swap[29], // escCep
                            swap[30], // escCidade
                            swap[31], // escUF
                            swap[32], // escTelefone
                            swap[33], // escEmail
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            lista.get(i).getTipoServico().getDescricao(),
                            lista.get(i).getReferencia(),
                            "Planilha de Débito Referente ao Acordo Número " + acordo.getId(),
                            acordo.getUsuario().getPessoa().getNome(),
                            acordo.getEmail()
                    ));
                }
                i++;
            }

            List ljasper = new ArrayList();
            //* JASPER 1 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/DEMOSTRATIVO_ACORDO.jasper"))
            );
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor1);
            ljasper.add(Jasper.fillObject(jasper, null, dtSource));

            //* JASPER 2 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/PLANILHA_DE_DEBITO.jasper"))
            );
            dtSource = new JRBeanCollectionDataSource(vetor2);
            ljasper.add(Jasper.fillObject(jasper, null, dtSource));

            Jasper.EXPORT_TYPE = "pdf";
            Jasper.PART_NAME = "";
            Jasper.printReports("planilha_acordo", ljasper);
            //* ------------- *//
//
//
//
//            JRPdfExporter exporter = new JRPdfExporter();
//            ByteArrayOutputStream retorno = new ByteArrayOutputStream();
//
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, ljasper);
//            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
//            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
//            exporter.exportReport();
//
//            arquivo = retorno.toByteArray();
//
//            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
        return arquivo;
    }

    public void imprimirAcordoSocial(List<Movimento> lista, Acordo acordo, Historico historico) {
        try {

            if (historico == null) {
                GenericaMensagem.error("Atenção", "Histórico não encontrado!");
                return;
            }

            FacesContext faces = FacesContext.getCurrentInstance();
            JasperReport jasper;
            Collection vetor1 = new ArrayList(), vetor2 = new ArrayList(), vetor3 = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe;

            int i = 0;
            String swap[] = new String[35];
            Pessoa pessoa;

            if (lista.isEmpty()) {
                return;
            }

            pessoa = lista.get(0).getPessoa();

            Fisica fisica;
            try {
                fisica = new FisicaDao().pesquisaFisicaPorPessoa(pessoa.getId());
                swap[0] = fisica.getPessoa().getNome();
                swap[1] = fisica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 3);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[9] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[9] = "";
            }

            MovimentoDao dbm = new MovimentoDao();
            int qnt = dbm.pesquisaAcordoAberto(acordo.getId()).size();
            while (i < lista.size()) {
                List<Vector> lAcres = dbm.pesquisaAcrescimo(lista.get(i).getId());

                BigDecimal valor, multa, juros, correcao, desconto;

                if (lAcres.isEmpty()) {
                    valor = new BigDecimal(0);
                    multa = new BigDecimal(0);
                    juros = new BigDecimal(0);
                    correcao = new BigDecimal(0);
                    desconto = new BigDecimal(0);
                } else {
                    valor = new BigDecimal((Double) lAcres.get(0).get(0));
                    multa = new BigDecimal((Double) lAcres.get(0).get(1));
                    juros = new BigDecimal((Double) lAcres.get(0).get(2));
                    correcao = new BigDecimal((Double) lAcres.get(0).get(3));
                    desconto = new BigDecimal((Double) lAcres.get(0).get(4));
                }

                BigDecimal valor_calculado
                        = new BigDecimal(
                                Moeda.soma(
                                        lista.get(i).getValor(), Moeda.subtracao(
                                        Moeda.soma(
                                                Moeda.soma(multa.doubleValue(), juros.doubleValue()), correcao.doubleValue()), desconto.doubleValue()
                                )
                                )
                        );
                if (lista.get(i).getTipoServico().getId() == 4) {
                    vetor1.add(new DemonstrativoEPlanilhaAcordoSocial(
                            acordo.getId(), // acordo_id
                            acordo.getData(), // acordo_data
                            acordo.getContato(), // acordo_contato
                            acordo.getEmail(), // acordo_email
                            swap[0], // nome
                            swap[1], // documento
                            swap[2], // endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[7], // cep
                            swap[8], // uf
                            swap[9], // telefone
                            historico.getHistorico(), // obs
                            lista.get(i).getDtVencimento(), // vencto
                            valor_calculado, // vlr pagar
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            lista.get(i).getServicos().getDescricao(), // servico
                            lista.get(i).getTipoServico().getDescricao(),
                            lista.get(i).getReferencia(),
                            acordo.getUsuario().getPessoa().getNome()
                    ));
                } else {
                    vetor2.add(new DemonstrativoEPlanilhaAcordoSocial(
                            acordo.getId(), // acordo_id
                            acordo.getData(), // acordo_data
                            acordo.getContato(), // acordo_contato
                            acordo.getEmail(), // acordo_email
                            swap[0], // nome
                            swap[1], // documento
                            swap[2], // endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[7], // cep
                            swap[8], // uf
                            swap[9], // telefone
                            historico.getHistorico(), // obs
                            lista.get(i).getDtVencimento(), // vencto
                            valor_calculado, // vlr pagar
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            lista.get(i).getServicos().getDescricao(), // servico
                            lista.get(i).getTipoServico().getDescricao(),
                            lista.get(i).getReferencia(),
                            acordo.getUsuario().getPessoa().getNome()
                    ));
                }
                i++;

            }
//            
            ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
            cab.init();

            Juridica juridica = (Juridica) new Dao().find(new Juridica(), 1);
            String documentox = juridica.getPessoa().getDocumento();// ? sindicato.getPessoa().getDocumento() : ;

            Map parameters = new HashMap();
//
//            // MOEDA PARA BRASIL VALORES IREPORT PT-BR CONVERTE VALOR JASPER VALOR IREPORT VALOR
            parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));
            parameters.put("sindicato_nome", juridica.getPessoa().getNome());
            parameters.put("sindicato_documento", documentox);
            parameters.put("sindicato_site", juridica.getPessoa().getSite());
            parameters.put("sindicato_logradouro", juridica.getPessoa().getPessoaEndereco().getEndereco().getLogradouro().getDescricao());
            parameters.put("sindicato_endereco", juridica.getPessoa().getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao());
            parameters.put("sindicato_numero", juridica.getPessoa().getPessoaEndereco().getNumero());
            parameters.put("sindicato_complemento", juridica.getPessoa().getPessoaEndereco().getComplemento());
            parameters.put("sindicato_bairro", juridica.getPessoa().getPessoaEndereco().getEndereco().getBairro().getDescricao());
            parameters.put("sindicato_cidade", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getCidade());
            parameters.put("sindicato_uf", juridica.getPessoa().getPessoaEndereco().getEndereco().getCidade().getUf());
            parameters.put("sindicato_cep", juridica.getPessoa().getPessoaEndereco().getEndereco().getCep());
            parameters.put("sindicato_telefone", juridica.getPessoa().getTelefone1());
            parameters.put("sindicato_logo", ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"));

            List ljasper = new ArrayList();
            //* JASPER 1 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/DEMOSTRATIVO_ACORDO_SOCIAL.jasper"))
            );
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor1);
            ljasper.add(JasperFillManager.fillReport(jasper, parameters, dtSource));
            //* ------------- *//

            //* JASPER 2 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/PLANILHA_DE_ACORDO_SOCIAL.jasper"))
            );
            dtSource = new JRBeanCollectionDataSource(vetor2);
            ljasper.add(JasperFillManager.fillReport(jasper, parameters, dtSource));
            //* ------------- *//

            /**
             * NÃO DESCOBRI COMO SETAR Map parameters = new HashMap(); DEPOIS DE
             * CRIAR fillReport() NESTE CASO PARA SETAR A FILIAL NO parameters
             * Jasper.printReports("planilha_de_acordo_e_demostrativo",
             * ljasper);
             */
            UUID uuidX = UUID.randomUUID();
            String uuid = "_" + uuidX.toString().replace("-", "_");
            String downloadName = "planilha_de_acordo_e_demostrativo" + uuid + ".pdf";

            if (!Diretorio.criar("/Arquivos/downloads/relatorios/planilha_de_acordo_e_demostrativo")) {
                GenericaMensagem.info("Sistema", "Erro ao criar diretório!");
                return;
            }

            String realPath = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/relatorios/planilha_de_acordo_e_demostrativo/";
            String dirPath = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(realPath);
            File file = new File(dirPath + "/" + downloadName);

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(SimpleExporterInput.getInstance(ljasper));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file.getPath()));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setCreatingBatchModeBookmarks(true);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            Download download = new Download(downloadName, dirPath, "application/pdf", FacesContext.getCurrentInstance());
            download.baixar();
            download.remover();
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public byte[] imprimirAcordoPromissoria(List<Movimento> lista, Acordo acordo, Historico historico, boolean imprimir_pro) {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();
            JasperReport jasper;
            Collection vetor1 = new ArrayList(), vetor2 = new ArrayList(), vetor3 = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe = null;
            JuridicaDao jurDB = new JuridicaDao();
            Juridica juridica = new Juridica();
            int i = 0;
            String swap[] = new String[35];
            Pessoa pessoa = null;
            Filial filial = null;

            if (!lista.isEmpty()) {
                ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
                cab.init();
                filial = cab.getConfiguracaoArrecadacao().getFilial();
                pessoa = lista.get(0).getPessoa();
            }

            try {
                juridica = jurDB.pesquisaJuridicaPorPessoa(pessoa.getId());
                swap[0] = juridica.getPessoa().getNome();
                swap[1] = juridica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 2);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[23] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[23] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(filial.getFilial().getPessoa().getId(), 3);
                swap[9] = filial.getFilial().getPessoa().getNome();
                swap[10] = filial.getFilial().getPessoa().getTipoDocumento().getDescricao();
                swap[11] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[12] = pe.getEndereco().getLogradouro().getDescricao();
                swap[13] = pe.getNumero();
                swap[14] = pe.getComplemento();
                swap[15] = pe.getEndereco().getBairro().getDescricao();
                swap[16] = pe.getEndereco().getCidade().getCidade();
                swap[17] = pe.getEndereco().getCidade().getUf();
                swap[18] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[19] = filial.getFilial().getPessoa().getDocumento();
                swap[20] = filial.getFilial().getPessoa().getTelefone1();
                swap[21] = filial.getFilial().getPessoa().getSite();
                swap[22] = filial.getFilial().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[9] = "";
                swap[10] = "";
                swap[11] = "";
                swap[12] = "";
                swap[13] = "";
                swap[14] = "";
                swap[15] = "";
                swap[16] = "";
                swap[17] = "";
                swap[18] = "";
                swap[19] = "";
                swap[20] = "";
                swap[21] = "";
                swap[22] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(juridica.getContabilidade().getPessoa().getId(), 3);
                swap[34] = juridica.getContabilidade().getPessoa().getNome();
                swap[24] = pe.getEndereco().getDescricaoEndereco().getDescricao();
                swap[25] = pe.getEndereco().getLogradouro().getDescricao();
                swap[26] = pe.getNumero();
                swap[27] = pe.getComplemento();
                swap[28] = pe.getEndereco().getBairro().getDescricao();
                swap[29] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[30] = pe.getEndereco().getCidade().getCidade();
                swap[31] = pe.getEndereco().getCidade().getUf();
                swap[32] = juridica.getContabilidade().getPessoa().getTelefone1();
                swap[33] = juridica.getContabilidade().getPessoa().getEmail1();
            } catch (Exception e) {
                swap[34] = "";
                swap[24] = "";
                swap[25] = "";
                swap[26] = "";
                swap[27] = "";
                swap[28] = "";
                swap[29] = "";
                swap[30] = "";
                swap[31] = "";
                swap[32] = "";
                swap[33] = "";
            }

            MovimentoDao dbm = new MovimentoDao();
            int qnt = dbm.pesquisaAcordoAberto(acordo.getId()).size();
            while (i < lista.size()) {
                List<Vector> lAcres = new Vector();

                lAcres = dbm.pesquisaAcrescimo(lista.get(i).getId());
                BigDecimal valor, multa, juros, correcao, desconto;

                if (lAcres.isEmpty()) {
                    valor = new BigDecimal(0);
                    multa = new BigDecimal(0);
                    juros = new BigDecimal(0);
                    correcao = new BigDecimal(0);
                    desconto = new BigDecimal(0);
                } else {
                    valor = new BigDecimal(((Double) lAcres.get(0).get(0)).doubleValue());
                    multa = new BigDecimal(((Double) lAcres.get(0).get(1)).doubleValue());
                    juros = new BigDecimal(((Double) lAcres.get(0).get(2)).doubleValue());
                    correcao = new BigDecimal(((Double) lAcres.get(0).get(3)).doubleValue());
                    desconto = new BigDecimal(((Double) lAcres.get(0).get(4)).doubleValue());
                }

                BigDecimal valor_calculado = new BigDecimal(Moeda.soma(lista.get(i).getValor(), Moeda.subtracao(
                        Moeda.soma(Moeda.soma(multa.doubleValue(), juros.doubleValue()), correcao.doubleValue()), desconto.doubleValue())));

                if (lista.get(i).getTipoServico().getId() == 4) {
                    vetor1.add(new DemonstrativoAcordo(
                            acordo.getId(), // codacordo
                            acordo.getData(), // data
                            acordo.getContato(), // contato
                            swap[0], // razao
                            swap[1], // cnpj
                            swap[2], //endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[8], // cep
                            swap[7], // uf
                            swap[23], // telefone
                            historico.getHistorico(), // obs
                            lista.get(i).getServicos().getDescricao(), // desc_contribuicao
                            lista.get(i).getDocumento(), // boleto
                            lista.get(i).getVencimento(), // vencto
                            valor_calculado, // vlrpagar
                            ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                            swap[9], // sinNome
                            swap[11], // sinEndereco
                            swap[12], // sinLogradouro
                            swap[13], // sinNumero
                            swap[14], // sinComplemento
                            swap[15], // sinBairro
                            swap[18], // sinCep
                            swap[16], // sinCidade
                            swap[17], // sinUF
                            swap[20], // sinTelefone
                            swap[22], // sinEmail
                            swap[21], // sinSite
                            swap[10], // sinTipoDocumento
                            swap[19], // sinDocumento
                            swap[34], // escNome
                            swap[24], // escEndereco
                            swap[25], // escLogradouro
                            swap[26], // escNumero
                            swap[27], // escComplemento
                            swap[28], // escBairro
                            swap[29], // escCep
                            swap[30], // escCidade
                            swap[31], // escUF
                            swap[32], // escTelefone
                            swap[33], // escEmail
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            lista.get(i).getTipoServico().getDescricao(),
                            lista.get(i).getReferencia(),
                            "Planilha de Débito Referente ao Acordo Número " + acordo.getId(),
                            acordo.getUsuario().getPessoa().getNome(),
                            acordo.getEmail()
                    ));

                    ValorExtenso ve = new ValorExtenso(new BigDecimal(lista.get(i).getValor()));
                    vetor3.add(new Promissoria(
                            "(" + lista.get(i).getAcordo().getId() + ") " + (vetor3.size() + 1) + "/" + qnt, // numero
                            ve.toString(), // extenso
                            new BigDecimal(lista.get(i).getValor()), // valor
                            swap[0], // razao
                            juridica.getPessoa().getTipoDocumento().getId() == 4 ? "" : juridica.getPessoa().getTipoDocumento().getDescricao(), // tipodocumento 
                            juridica.getPessoa().getTipoDocumento().getId() == 4 ? "" : juridica.getPessoa().getDocumento(), // documento
                            swap[2], // endereco
                            swap[4], // complemento
                            swap[3], // numero
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[8], // cep
                            swap[7], // uf
                            swap[9], // sinnome
                            swap[19], // sinDocumento
                            swap[16], // sinCidade
                            swap[17], // sinUF
                            lista.get(i).getVencimento(),// DataHoje.DataToArray(lista.get(i).getVencimento())[2]+"-"+DataHoje.DataToArray(lista.get(i).getVencimento())[1]+"-"+DataHoje.DataToArray(lista.get(i).getVencimento())[0], // vencto
                            ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Imagens/promissoria.jpg"),
                            DataHoje.dataExtenso(DataHoje.data())) // fundo_promissoria
                    );
                } else {
                    vetor2.add(new DemonstrativoAcordo(
                            acordo.getId(), // codacordo
                            acordo.getData(), // data
                            acordo.getContato(), // contato
                            swap[0], // razao
                            swap[1], // cnpj
                            swap[2], //endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[8], // cep
                            swap[7], // uf
                            swap[23], // telefone
                            historico.getHistorico(), // obs
                            lista.get(i).getServicos().getDescricao(), // desc_contribuicao
                            lista.get(i).getDocumento(), // boleto
                            lista.get(i).getVencimento(), // vencto
                            //new BigDecimal(lista.get(i).getValor()), // vlrpagar
                            valor_calculado, // vlrpagar
                            ((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // sinLogo
                            swap[9], // sinNome
                            swap[11], // sinEndereco
                            swap[12], // sinLogradouro
                            swap[13], // sinNumero
                            swap[14], // sinComplemento
                            swap[15], // sinBairro
                            swap[18], // sinCep
                            swap[16], // sinCidade
                            swap[17], // sinUF
                            swap[20], // sinTelefone
                            swap[22], // sinEmail
                            swap[21], // sinSite
                            swap[10], // sinTipoDocumento
                            swap[19], // sinDocumento
                            swap[34], // escNome
                            swap[24], // escEndereco
                            swap[25], // escLogradouro
                            swap[26], // escNumero
                            swap[27], // escComplemento
                            swap[28], // escBairro
                            swap[29], // escCep
                            swap[30], // escCidade
                            swap[31], // escUF
                            swap[32], // escTelefone
                            swap[33], // escEmail
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            lista.get(i).getTipoServico().getDescricao(),
                            lista.get(i).getReferencia(),
                            "Planilha de Débito Referente ao Acordo Número " + acordo.getId(),
                            acordo.getUsuario().getPessoa().getNome(),
                            acordo.getEmail()
                    ));
                }
                i++;

            }

            List ljasper = new ArrayList();
            //* JASPER 1 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/DEMOSTRATIVO_ACORDO.jasper"))
            );
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(vetor1);
            ljasper.add(JasperFillManager.fillReport(jasper, null, dtSource));
            //* ------------- *//

            //* JASPER 2 *//
            jasper = (JasperReport) JRLoader.loadObject(
                    new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/PLANILHA_DE_DEBITO.jasper"))
            );
            dtSource = new JRBeanCollectionDataSource(vetor2);
            ljasper.add(JasperFillManager.fillReport(jasper, null, dtSource));
            //* ------------- *//

            if (imprimir_pro) {
                //* JASPER 3 *//
                jasper = (JasperReport) JRLoader.loadObject(
                        new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Relatorios/PROMISSORIA.jasper"))
                );
                dtSource = new JRBeanCollectionDataSource(vetor3);
                ljasper.add(JasperFillManager.fillReport(jasper, null, dtSource));
                //* ------------- *//
            }

            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream retorno = new ByteArrayOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, ljasper);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
            exporter.exportReport();

            arquivo = retorno.toByteArray();

            pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/boletos");
        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
        return arquivo;
    }

    /*
    METODO ANTIGO
     */
    public byte[] imprimirBoletoSocial(Boleto boleto, String view, boolean imprimeVerso) {
        List<Boleto> l = new ArrayList();
        l.add(boleto);
        return imprimirBoletoSocial(l, view, imprimeVerso);
    }

    /*
    METODO ANTIGO
     */
    public byte[] imprimirBoletoSocial(List<Boleto> listaBoleto, String view, boolean imprimeVerso) {

        List lista = new ArrayList();
        Filial filial = (Filial) new Dao().find(new Filial(), 1);
        ConfiguracaoSocial cs = (ConfiguracaoSocial.get());
        FinanceiroDao db = new FinanceiroDao();

        try {
            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_SOCIAL.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);

            File file_jasper_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_SOCIAL_VERSO.jasper"));
            JasperReport jasperReportVerso = (JasperReport) JRLoader.loadObject(file_jasper_verso);

            List<JasperPrint> jasperPrintList = new ArrayList();
            File file_promo = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/BannerPromoBoleto.png"));
            if (!file_promo.exists()) {
                file_promo = null;
            }

            File file_promo_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoBoletoVersoSocial.png"));
            if (!file_promo_verso.exists()) {
                file_promo_verso = null;
            }

            MovimentosReceberSocialDao dbs = new MovimentosReceberSocialDao();
            JuridicaDao dbj = new JuridicaDao();
            FisicaDao dbf = new FisicaDao();

            List<Vector> lista_socio = new ArrayList();
            for (Boleto boleto : listaBoleto) {
                // PESSOA RESPONSÁVEL PELO BOLETO
                Pessoa pessoa = dbs.responsavelBoleto(boleto.getNrCtrBoleto());

                // VERIFICA SE O BOLETO ESTA VENCIDO
//                if (DataHoje.menorData(boleto.getVencimento(), DataHoje.data())) {
//                    GenericaMensagem.fatal("Atenção", "Boleto " + boleto.getBoletoComposto() + " vencido!");
//                    return new byte[0];
//                }
                String contabilidade = "";
                if (lista_socio.isEmpty()) {
                    if (dbf.pesquisaFisicaPorPessoa(pessoa.getId()) != null) {
                        lista_socio = db.listaBoletoSocioFisica(boleto.getNrCtrBoleto(), view); // NR_CTR_BOLETO
                    } else {
                        lista_socio = db.listaBoletoSocioJuridica(boleto.getNrCtrBoleto(), view); // NR_CTR_BOLETO
                        Juridica j = dbj.pesquisaJuridicaPorPessoa(pessoa.getId());
                        String doc = (j.getContabilidade() != null
                                && !j.getContabilidade().getPessoa().getDocumento().isEmpty()
                                && !j.getContabilidade().getPessoa().getDocumento().equals("0")) ? j.getContabilidade().getPessoa().getDocumento() + " - " : " ";

                        contabilidade = (j.getContabilidade() != null) ? "CONTABILIDADE : " + doc + j.getContabilidade().getPessoa().getNome() : "";
                    }
                }

                if (lista_socio.isEmpty()) {
                    GenericaMensagem.warn("Atenção", "Lista não encontrada. CTR_BOLETO:. " + boleto.getNrCtrBoleto() + " VIEW:. " + view);
                    return new byte[0];
                }

                Cobranca cobranca = null;
                // SOMA VALOR DAS ATRASADAS
                double valor_total_atrasadas = 0, valor_total = 0, valor_boleto = 0;
                List<String> list_at = new ArrayList();
                for (Vector listax : lista_socio) {
                    // SE vencimento_movimento FOR MENOR QUE vencimento_boleto_original
                    if (DataHoje.menorData(DataHoje.converteData((Date) listax.get(38)), "01/" + DataHoje.converteData((Date) listax.get(39)).substring(3))) {
                        valor_total_atrasadas = Moeda.soma(valor_total_atrasadas, Moeda.converteUS$(listax.get(14).toString()));
                        list_at.add(DataHoje.converteData((Date) listax.get(38)));
                    } else {
                        valor_total = Moeda.soma(valor_total, Moeda.converteUS$(listax.get(14).toString()));
                    }
                    valor_boleto = Moeda.soma(valor_total, valor_total_atrasadas);
                }

                String mensagemAtrasadas = "Mensalidade(s) Atrasada(s) Corrigida(s)";
                if (!list_at.isEmpty()) {
                    mensagemAtrasadas = "Mensalidade(s) Atrasada(s) Corrigida(s) de " + list_at.get(0).substring(3) + " até " + list_at.get(list_at.size() - 1).substring(3);
                }

                HashMap hash = new HashMap();

                hash = registrarMovimentosAss(boleto, valor_boleto, boleto.getVencimento());
                if (hash.get("boleto") != null) {
                    boleto = (Boleto) hash.get("boleto");
                } else {
                    if (hash.get("boleto") == null) {
                        if (hash.get("mensagem") != null) {
                            if (!hash.get("mensagem").toString().isEmpty()) {
                                if (hash.get("mensagem").toString().contains("Erro")) {
                                    GenericaMensagem.warn(hash.get("mensagem").toString(), "");
                                } else {
                                    GenericaMensagem.warn("Atenção", hash.get("mensagem").toString());
                                }
                            }
                        }
                    }
                    return new byte[0];
                }

                for (int i = 0; i < lista_socio.size(); i++) {
                    lista_socio.get(i).set(20, boleto.getBoletoComposto());
                }

                // CHAMADO ROGÉRIO #2390 PEDIU PARA TIRAR O VALOR APENAS NO CODIGO DE BARRAS E REPRESENTAÇÃO
                // CASO BOLETO FOR SEM REGISTRO
                if (boleto.getContaCobranca().getCobrancaRegistrada().getId() == 3) {
                    cobranca = Cobranca.retornaCobranca(null, new Double(0), boleto.getDtVencimento(), boleto);
                } else {
                    cobranca = Cobranca.retornaCobranca(null, valor_boleto, boleto.getDtVencimento(), boleto);
                }

                int qntItens = 0;
                for (int w = 0; w < lista_socio.size(); w++) {
//                    if (DataHoje.maiorData(DataHoje.converteData((Date) lista_socio.get(w).get(38)), "01/" + DataHoje.converteData((Date) lista_socio.get(w).get(40)).substring(3))
//                            || DataHoje.igualdadeData(DataHoje.converteData((Date) lista_socio.get(w).get(38)), "01/" + DataHoje.converteData((Date) lista_socio.get(w).get(40)).substring(3))) {
                    qntItens++;
                    
                    // NÃO ESTA PEGANDO O VALOR CALCULADO DE FIN_BOLETO
                    //----                    
                    double valor = Moeda.converteUS$(lista_socio.get(w).get(14).toString());
                    lista.add(new ParametroBoletoSocial(
                            ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // LOGO SINDICATO
                            filial.getFilial().getPessoa().getNome(),
                            (Integer) lista_socio.get(w).get(5), // CODIGO
                            lista_socio.get(w).get(6).toString(), // RESPONSAVEL
                            boleto.getVencimento(), // VENCIMENTO
                            (lista_socio.get(w).get(8) == null) ? "" : lista_socio.get(w).get(8).toString(), // MATRICULA
                            (lista_socio.get(w).get(10) == null) ? "" : lista_socio.get(w).get(10).toString(), // CATEGORIA
                            (lista_socio.get(w).get(9) == null) ? "" : lista_socio.get(w).get(9).toString(), // GRUPO
                            (Integer) lista_socio.get(w).get(12), // CODIGO BENEFICIARIO
                            lista_socio.get(w).get(13).toString(), // BENEFICIARIO
                            lista_socio.get(w).get(11).toString(), // SERVICO
                            valor, // VALOR
                            Moeda.converteR$Double(valor_total), // VALOR TOTAL
                            //Moeda.converteR$(lista_socio.get(w).get(15).toString()), // VALOR ATRASADAS
                            Moeda.converteR$Double(valor_total_atrasadas), // VALOR ATRASADAS
                            Moeda.converteR$Double(Moeda.soma(valor_total, valor_total_atrasadas)), // VALOR ATÉ VENCIMENTO
                            file_promo == null ? null : file_promo.getAbsolutePath(), // LOGO PROMOÇÃO
                            ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(boleto.getContaCobranca().getContaBanco().getBanco().getLogo().trim()), // LOGO BANCO
                            lista_socio.get(w).get(16).toString(), // MENSAGEM
                            lista_socio.get(w).get(18).toString(), // AGENCIA
                            cobranca.representacao(), // REPRESENTACAO
                            lista_socio.get(w).get(19).toString(), // CODIGO CEDENTE
                            lista_socio.get(w).get(20).toString(), // NOSSO NUMENTO
                            DataHoje.converteData((Date) lista_socio.get(w).get(4)), // PROCESSAMENTO
                            cobranca.codigoBarras(), // CODIGO DE BARRAS
                            ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/serrilha.GIF"), // SERRILHA
                            lista_socio.get(w).get(31).toString() + " " + lista_socio.get(w).get(32).toString(), // ENDERECO RESPONSAVEL
                            lista_socio.get(w).get(26).toString() + " " + lista_socio.get(w).get(27).toString(), // ENDERECO FILIAL
                            lista_socio.get(w).get(35).toString() + " " + lista_socio.get(w).get(34).toString() + " " + lista_socio.get(w).get(33).toString(), // COMPLEMENTO RESPONSAVEL
                            lista_socio.get(w).get(28).toString() + " - " + lista_socio.get(w).get(29).toString() + " " + lista_socio.get(w).get(30).toString(), // COMPLEMENTO FILIAL
                            lista_socio.get(w).get(24).toString(), // CNPJ FILIAL
                            lista_socio.get(w).get(25).toString(), // TELEFONE FILIAL
                            lista_socio.get(w).get(21).toString(), // EMAIL FILIAL
                            lista_socio.get(w).get(23).toString(), // SITE FILIAL
                            file_promo_verso == null ? null : file_promo_verso.getAbsolutePath(), // LOGO BOLETO VERSO SOCIAL
                            lista_socio.get(w).get(37).toString(), // LOCAL DE PAGAMENTO
                            lista_socio.get(w).get(36).toString(), // INFORMATIVO
                            pessoa.getTipoDocumento().getDescricao() + ": " + pessoa.getDocumento(),
                            //String.valueOf(lista_socio.size()), // QUANTIDADE DE ITENS PARA MOSTRAR OS ATRASADOS TAMBEḾ
                            String.valueOf(qntItens), // QUANTIDADE DE ITENS
                            boleto.getContaCobranca().getContaBanco().getBanco().getNumero(),
                            mensagemAtrasadas,
                            DataHoje.converteData((Date) lista_socio.get(w).get(38)).substring(3), // VENCIMENTO SERVIÇO
                            contabilidade, // CONTABILIDADE DA PESSOA JURÍDICA
                            boleto.getMensagem() // MENSAGEM QUE FICA ACIMA DE "Mensalidades Atrasadas"
                    ));
                    //}
                }

                JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
                Map map = new HashMap();
                map.put("titulo_extrato", cs.getTituloExtrato());
                jasperPrintList.add(JasperFillManager.fillReport(jasperReport, map, dtSource));
                if (imprimeVerso) {
                    dtSource = new JRBeanCollectionDataSource(lista);
                    jasperPrintList.add(JasperFillManager.fillReport(jasperReportVerso, null, dtSource));
                }

                lista.clear();
                lista_socio.clear();
            }

            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream retorno = new ByteArrayOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
            exporter.exportReport();

            arquivo = retorno.toByteArray();

        } catch (JRException e) {
            e.getMessage();
        }
        return arquivo;
    }

    /*
    METODO ATUAL EM USO
     */
    public byte[] imprimirBoletoSocial_2(String listaBoleto, String view, String tipo, boolean imprimeVerso) {
        List lista = new ArrayList();
        Filial filial = (Filial) new Dao().find(new Filial(), 1);
        FinanceiroDao db = new FinanceiroDao();
        MovimentoDao movimentoDao = new MovimentoDao();
        BoletoDao boletoDao = new BoletoDao();
        ConfiguracaoSocial cs = (ConfiguracaoSocial.get());
        Dao dao = new Dao();
        //dao.openTransaction();
        try {
            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_SOCIAL_2.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);

            File file_jasper_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_SOCIAL_VERSO.jasper"));
            JasperReport jasperReportVerso = (JasperReport) JRLoader.loadObject(file_jasper_verso);

            List<JasperPrint> jasperPrintList = new ArrayList();
            File file_promo = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/BannerPromoBoleto.png"));
            if (!file_promo.exists()) {
                file_promo = null;
            }

            File file_promo_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoBoletoVersoSocial.png"));
            if (!file_promo_verso.exists()) {
                file_promo_verso = null;
            }

            List<Object> result = db.listaBoletoSocio(listaBoleto, view, tipo);

            // SOMA VALOR DAS ATRASADAS
            double valor_total_mes = 0, valor_total_vencimento = 0, valor_boleto = 0;

            int qntItens = 0;
            Boleto boleto = null;
            Boolean novo_boleto = true;
            String representacao = "", codigo_barras = "";

            Usuario usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
            List listImpressao = new ArrayList();
            Boolean e_fisica = tipo.equals("fisica");

            for (Integer i = 0; i < result.size(); i++) {
                List linha = (List) result.get(i);
                String valor = "0,00";

                valor = Moeda.converteR$Double(Moeda.converteUS$(linha.get(14).toString()));
                // ANTES O VALOR ERA SOMADO DIFERENTE PARA FÍSICA E JURIDICA PORQUE NA JURIDICA PODIA TER MAIS DE UM MOVIMENTO PARA CADA LINHA, 
                // COM AS MUDANÇAS A QUERY RETORNA TODOS OS MOVIMENTOS
//                if (e_fisica) {
//                    valor = Moeda.converteR$Double(Moeda.converteUS$(linha.get(14).toString()));
//                } else {
//                    Boleto b = movimentoDao.pesquisaBoletos(linha.get(2).toString());
//                    List<Movimento> lm = b.getListaMovimento();
//                    Double valor_s = new Double(0);
//                    for (Movimento mx : lm){
//                        valor_s = Moeda.soma(valor_s, mx.getValor());
//                    }
//                    //m = (Movimento) movimentoDao.findByNrCtrBoletoTitular(linha.get(2).toString(), Integer.parseInt(linha.get(41).toString()));
//                    //if (m == null){
//                    //    i++;
//                    //    continue;
//                    //}
//                    //valor = m.getValorString();
//                    
//                    valor = Moeda.converteR$Double(valor_s);
//                    //valor = Moeda.converteR$Double(Moeda.converteUS$(linha.get(14).toString()));
//                }

                try {
                    if (novo_boleto) {
                        valor_boleto = 0;
                        valor_total_mes = 0;
                        valor_total_vencimento = 0;
                        representacao = "";
                        codigo_barras = "";
                        boleto = boletoDao.findByNrCtrBoleto(linha.get(2).toString());
                        novo_boleto = false;
                    }

                    if (linha.get(2).toString().equals(((List) result.get(i + 1)).get(2))) {
                        valor_boleto = Moeda.soma(valor_boleto, Moeda.converteUS$(valor));
                        if ((Integer) linha.get(12) != 0) {
                            // SE vencimento_movimento FOR MAIOR OU IGUAL QUE vencimento_boleto_original
                            if (DataHoje.maiorData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(39)).substring(3))
                                    || DataHoje.igualdadeData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(39)).substring(3))) {
                                valor_total_mes = Moeda.soma(valor_total_mes, Moeda.converteUS$(valor));
                            }
                        }
                    } else {
                        valor_boleto = Moeda.soma(valor_boleto, Moeda.converteUS$(valor));
                        if ((Integer) linha.get(12) != 0) {
                            // SE vencimento_movimento FOR MAIOR OU IGUAL QUE vencimento_boleto_original
                            if (DataHoje.maiorData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(39)).substring(3))
                                    || DataHoje.igualdadeData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(39)).substring(3))) {
                                valor_total_mes = Moeda.soma(valor_total_mes, Moeda.converteUS$(valor));
                            }
                        }
                        valor_total_vencimento = valor_boleto;

                        boleto = new BoletoDao().findByNrCtrBoleto(linha.get(2).toString());

                        // PROVISÓRIO FALAR COM O ROGÉRIO, NÃO REGISTRAR CASO ESTEJA REGISTRADO.
                        // HashMap hash = registrarMovimentosAss(boleto, valor_boleto, boleto.getVencimento());
                        // 03/01/2017 - INICIO DA ALTERAÇÃO
                        HashMap hash = new HashMap();

                        hash = registrarMovimentosAss(boleto, valor_boleto, boleto.getVencimento());
                        if (hash.get("boleto") != null) {
                            boleto = (Boleto) hash.get("boleto");
                        } else {
                            return new byte[0];
                        }

                        linha.set(20, boleto.getBoletoComposto());
                        // 03/01/2017 - FIM DA ALTERAÇÃO
                        Cobranca cobranca;
                        // CHAMADO ROGÉRIO #2390 PEDIU PARA TIRAR O VALOR APENAS NO CODIGO DE BARRAS E REPRESENTAÇÃO
                        // CASO BOLETO FOR SEM REGISTRO
                        if (boleto.getContaCobranca().getCobrancaRegistrada().getId() == 3) {
                            cobranca = Cobranca.retornaCobranca(null, new Double(0), boleto.getDtVencimento(), boleto);
                        } else {
                            cobranca = Cobranca.retornaCobranca(null, valor_boleto, boleto.getDtVencimento(), boleto);
                        }

                        representacao = cobranca.representacao();
                        codigo_barras = cobranca.codigoBarras();

                        novo_boleto = true;
                    }
                } catch (Exception e) {
                    e.getMessage();
                    valor_boleto = Moeda.soma(valor_boleto, Moeda.converteUS$(valor));
                    if ((Integer) linha.get(12) != 0) {
                        // SE vencimento_movimento FOR MAIOR OU IGUAL QUE vencimento_boleto_original
                        if (DataHoje.maiorData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(39)).substring(3))
                                || DataHoje.igualdadeData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(39)).substring(3))) {
                            valor_total_mes = Moeda.soma(valor_total_mes, Moeda.converteUS$(valor));
                        }
                    }
                    valor_total_vencimento = valor_boleto;
                    // PROVISÓRIO FALAR COM O ROGÉRIO, NÃO REGISTRAR CASO ESTEJA REGISTRADO.
                    // HashMap hash = registrarMovimentosAss(boleto, valor_boleto, boleto.getVencimento());
                    // 03/01/2017 - INICIO DA ALTERAÇÃO
                    boleto = new BoletoDao().findByNrCtrBoleto(linha.get(2).toString());
                    HashMap hash = new HashMap();

                    hash = registrarMovimentosAss(boleto, valor_boleto, boleto.getVencimento());
                    if (hash.get("boleto") != null) {
                        boleto = (Boleto) hash.get("boleto");
                    } else {
                        return new byte[0];
                    }

                    linha.set(20, boleto.getBoletoComposto());
                    // 03/01/2017 - FIM DA ALTERAÇÃO

                    Cobranca cobranca;
                    // CHAMADO ROGÉRIO #2390 PEDIU PARA TIRAR O VALOR APENAS NO CODIGO DE BARRAS E REPRESENTAÇÃO
                    // CASO BOLETO FOR SEM REGISTRO
                    if (boleto.getContaCobranca().getCobrancaRegistrada().getId() == 3) {
                        cobranca = Cobranca.retornaCobranca(null, new Double(0), boleto.getDtVencimento(), boleto);
                    } else {
                        cobranca = Cobranca.retornaCobranca(null, valor_boleto, boleto.getDtVencimento(), boleto);
                    }

                    representacao = cobranca.representacao();
                    codigo_barras = cobranca.codigoBarras();

                    novo_boleto = true;
                }
//
//                if (DataHoje.maiorData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(40)).substring(3))
//                        || DataHoje.igualdadeData(DataHoje.converteData((Date) linha.get(38)), "01/" + DataHoje.converteData((Date) linha.get(40)).substring(3))) {
                qntItens++;
                lista.add(
                        new ParametroBoletoSocial(
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // LOGO SINDICATO
                                filial.getFilial().getPessoa().getNome(),
                                (Integer) linha.get(5), // CODIGO
                                linha.get(6).toString(), // RESPONSAVEL
                                DataHoje.converteData((Date) linha.get(7)), // VENCIMENTO
                                (linha.get(8) == null) ? "" : linha.get(8).toString(), // MATRICULA
                                (linha.get(10) == null) ? "" : linha.get(10).toString(), // CATEGORIA
                                (linha.get(9) == null) ? "" : linha.get(9).toString(), // GRUPO
                                e_fisica ? (Integer) linha.get(12) : (Integer) linha.get(41), // CODIGO BENEFICIARIO
                                e_fisica ? linha.get(13).toString() : linha.get(42).toString(), // BENEFICIARIO
                                e_fisica ? linha.get(11).toString() : "", // SERVICO
                                //                                    Moeda.converteUS$(linha.get(14).toString()), // VALOR
                                Moeda.converteUS$(valor), // VALOR
                                Moeda.converteR$Double(valor_total_mes),//Moeda.converteR$Double(Moeda.converteUS$(linha.get(15).toString())), // VALOR TOTAL MÊS
                                "0,00", // VALOR ATRASADAS
                                Moeda.converteR$Double(valor_total_vencimento), // VALOR ATÉ VENCIMENTO
                                file_promo == null ? null : file_promo.getAbsolutePath(), // LOGO PROMOÇÃO
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(boleto.getContaCobranca().getContaBanco().getBanco().getLogo().trim()), // LOGO BANCO
                                linha.get(16).toString(), // MENSAGEM
                                linha.get(18).toString(), // AGENCIA
                                representacao, // REPRESENTACAO
                                linha.get(19).toString(), // CODIGO CEDENTE
                                linha.get(20).toString(), // NOSSO NUMENTO
                                DataHoje.converteData((Date) linha.get(4)), // PROCESSAMENTO
                                codigo_barras, // CODIGO DE BARRAS
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/serrilha.GIF"), // SERRILHA
                                linha.get(31).toString() + " " + linha.get(32).toString(), // ENDERECO RESPONSAVEL
                                linha.get(26).toString() + " " + linha.get(27).toString(), // ENDERECO FILIAL
                                linha.get(35).toString() + " " + linha.get(34).toString() + " " + linha.get(33).toString(), // COMPLEMENTO RESPONSAVEL
                                linha.get(28).toString() + " - " + linha.get(29).toString() + " " + linha.get(30).toString(), // COMPLEMENTO FILIAL
                                linha.get(24).toString(), // CNPJ FILIAL
                                linha.get(25).toString(), // TELEFONE FILIAL
                                linha.get(21).toString(), // EMAIL FILIAL
                                linha.get(23).toString(), // SITE FILIAL
                                file_promo_verso == null ? null : file_promo_verso.getAbsolutePath(), // LOGO BOLETO VERSO SOCIAL
                                linha.get(37).toString(), // LOCAL DE PAGAMENTO
                                linha.get(36).toString(), // INFORMATIVO
                                linha.get(43).toString(), //pessoa.getTipoDocumento().getDescricao() + ": " + pessoa.getDocumento(),
                                String.valueOf(qntItens), // QUANTIDADE DE ITENS
                                boleto.getContaCobranca().getContaBanco().getBanco().getNumero(),
                                linha.get(45).toString(), // REFERENCIA MENSALIDADES ATRASADAS
                                DataHoje.converteData((Date) linha.get(38)).substring(3), // VENCIMENTO SERVIÇO
                                (linha.get(44) == null) ? "" : linha.get(44).toString(), // CONTABILIDADE DA PESSOA JURÍDICA
                                boleto.getMensagem() // MENSAGEM QUE FICA ACIMA DE "Mensalidades Atrasadas"
                        )
                );

                if (Integer.valueOf(linha.get(1).toString()) > 0) {
                    String insert_impressao
                            = "INSERT INTO fin_impressao (dt_impressao, dt_vencimento, ds_hora, id_movimento, id_usuario) \n "
                            + "VALUES (CURRENT_DATE, '" + DataHoje.converteData((Date) linha.get(7)) + "', '" + DataHoje.hora() + "', " + Integer.valueOf(linha.get(1).toString()) + ", " + usuario.getId() + ")";
                    dao.openTransaction();
                    if (!dao.executeQuery(insert_impressao)) {
                        dao.rollback();
                        GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
                        return null;
                    }
                    dao.commit();
                    // SE FICAR LENTO EU BRUNO IREI SUGERIR USAR A LISTA ABAIXO
                    // listImpressao.add(insert_impressao);
//                        Movimento m = (Movimento) dao.find(new Movimento(), Integer.valueOf(linha.get(1).toString()));
//
//                        Impressao impressao = new Impressao();
//
//                        impressao.setUsuario(usuario);
//                        impressao.setDtVencimento(m.getDtVencimento());
//                        impressao.setMovimento(m);
//
//                        if (!dao.save(impressao, true)) {
//                            dao.rollback();
//                            GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
//                            return null;
//                        }
                }
//                }

                if (novo_boleto) {
                    Map map = new HashMap();
                    map.put("titulo_extrato", cs.getTituloExtrato());
                    // DEIXA 30 SEGUNDOS MAIS LENTO, E O VERSO APARACE NO FINAL DE CADA BOLETO
                    JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
                    jasperPrintList.add(JasperFillManager.fillReport(jasperReport, map, dtSource));
                    if (imprimeVerso) {
                        dtSource = new JRBeanCollectionDataSource(lista);
                        jasperPrintList.add(JasperFillManager.fillReport(jasperReportVerso, null, dtSource));
                    }

                    lista.clear();
                }
            }

            /*
            30 SEGUNDOS MAIS RÁPIDO, PORÉM O VERSO SÓ APARACE NO FINAL DO JASPER
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
            jasperPrintList.add(JasperFillManager.fillReport(jasperReport, null, dtSource));
            if (imprimeVerso) {
                dtSource = new JRBeanCollectionDataSource(lista);
                jasperPrintList.add(JasperFillManager.fillReport(jasperReportVerso, null, dtSource));
            }

            lista.clear();
             */
            if (!jasperPrintList.isEmpty()) {
                // SE FICAR LENTO EU BRUNO IREI SUGERIR USAR A LISTA ABAIXO
                if (!listImpressao.isEmpty()) {
//                    try {
//                        dao.openTransaction();
//                        for (int i = 0; i < listImpressao.size(); i++) {
//                            if (!dao.executeQuery(listImpressao.get(i).toString())) {
//                                dao.rollback();
//                                GenericaMensagem.error("Erro", "Não foi possível SALVAR impressão!");
//                                return null;
//                            }
//                        }
//                        dao.commit();
//                    } catch (Exception e) {
//
//                    }
                }
                JRPdfExporter exporter = new JRPdfExporter();
                ByteArrayOutputStream retorno = new ByteArrayOutputStream();

                exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
                exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
                exporter.exportReport();

                arquivo = retorno.toByteArray();
                //dao.commit();
            }
        } catch (JRException e) {
            e.getMessage();
            //dao.rollback();
        }
        return arquivo;
    }

    byte[] concat(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            totalLength += arrays[i].length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++) {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }
        return result;
    }

    public void visualizar(File file) {
        if (file != null) {
            byte[] arq = new byte[(int) file.length()];
            try {
                HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                res.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file.getName()));
                //res.setContentType("application/pdf");
                //res.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\"");
                res.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + "\"");
                res.getOutputStream().write(arq);
                res.getCharacterEncoding();
                FacesContext.getCurrentInstance().responseComplete();
            } catch (Exception e) {
                e.getMessage();
            }
            return;
        }
        if (arquivo.length > 0) {
            try {
                HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                res.setContentType("application/pdf");
                res.setHeader("Content-disposition", "inline; filename=\"" + "boleto_x" + ".pdf\"");
                res.getOutputStream().write(arquivo);
                res.getCharacterEncoding();
                FacesContext.getCurrentInstance().responseComplete();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public void visualizar_remessa(File file) {
        if (file != null) {
            //byte[] arq = new byte[(int) file.length()];
            try {
                byte[] arq = IOUtils.toByteArray(FileUtils.openInputStream(file));
                HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                //res.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file.getName()));
                res.setContentType("application/x-rar-compressed");
                res.setHeader("Content-disposition", "attachment; filename=\"" + file.getName() + "\"");
                res.getOutputStream().write(arq);
                res.getCharacterEncoding();
                FacesContext.getCurrentInstance().responseComplete();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public void baixarArquivo() {
        UUID uuidX = UUID.randomUUID();
        String uuid = "_" + uuidX.toString().replace("-", "_");
        SalvaArquivos sa = new SalvaArquivos(arquivo, "boleto" + uuid + ".pdf", false);
        sa.salvaNaPasta(pathPasta);
        Download download = new Download("boleto" + uuid + ".pdf", pathPasta, "application/pdf", FacesContext.getCurrentInstance());
        download.baixar();
    }

    public String criarLink(Pessoa pessoa, String caminho) {
        String hash = String.valueOf(pessoa.getId()) + "_" + String.valueOf(DataHoje.converteDataParaInteger(DataHoje.data())) + "_" + DataHoje.horaSemPonto() + ".pdf";
        SalvaArquivos sa = new SalvaArquivos(arquivo, hash, false);
        sa.salvaNaPasta(pathPasta);
        Links links = new Links();
        links.setCaminho(caminho);
        links.setNomeArquivo(hash);
        links.setPessoa(pessoa);
        Dao dao = new Dao();
        dao.openTransaction();
        if (dao.save(links)) {
            dao.commit();
            return hash;
        } else {
            dao.rollback();
            return "";
        }
    }

    public String getPathPasta() {
        return pathPasta;
    }

    public void setPathPasta(String pathPasta) {
        this.pathPasta = pathPasta;
    }

    public Boolean getTeste() {
        Boolean teste = false;
        if (!GenericaSessao.exists("webServiceBoletoTest")) {
            try {
                FacesContext fc = FacesContext.getCurrentInstance();
                if (fc != null) {
                    Map<String, Object> cookies = fc.getExternalContext().getRequestCookieMap();
                    Cookie cookieWebServiceBoletoTest = (Cookie) cookies.get("webServiceBoletoTest");
                    if (cookieWebServiceBoletoTest != null) {
                        teste = Boolean.parseBoolean(cookieWebServiceBoletoTest.getValue());
                    }
                }
            } catch (Exception e) {

            }
        } else {
            teste = GenericaSessao.getBoolean("webServiceBoletoTest");
        }
        return teste;
    }
}
