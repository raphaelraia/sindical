package br.com.rtools.cobranca;

import br.com.rtools.associativo.dao.MovimentosReceberSocialDao;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.financeiro.StatusRemessa;
import br.com.rtools.financeiro.StatusRetorno;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Sicoob extends Cobranca {

    private final Boolean TESTE = GenericaSessao.getBoolean("debug");

    public Sicoob(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public Sicoob(List<BoletoRemessa> listaBoletoRemessa) {
        super(listaBoletoRemessa);
    }

    @Override
    public String moduloDez(String composicao) {
        int i = composicao.length();
        int j = 2;
        int soma = 0;
        int swap = 0;
        String m;
        while (i > 0) {
            m = composicao.substring(i - 1, i);
            swap = Integer.parseInt(m) * j;
            if (swap > 9) {
                String num = Integer.toString(swap);
                swap = Integer.parseInt(num.substring(0, 1)) + Integer.parseInt(num.substring(1, 2));
            }
            soma += swap;
            if (j == 2) {
                j = 1;
            } else if (j == 1) {
                j = 2;
            }
            i--;
        }

        if ((soma % 10) == 0) {
            return "0";
        } else if (soma < 10) {
            return Integer.toString(10 - soma);
        } else {
            return Integer.toString(10 - (soma % 10));
        }
    }

    @Override
    public String moduloOnze(String composicao) {
        int i = composicao.length();
        int j = 2;
        int soma = 0;
        String m;
        while (i > 0) {
            if (j > 9) {
                j = 2;
            }
            m = composicao.substring(i - 1, i);
            soma += Integer.parseInt(m) * j;
            j++;
            i--;
        }
        if ((11 - (soma % 11)) == 0 || (11 - (soma % 11)) == 1 || (11 - (soma % 11)) > 9) {
            return "1";
        } else {
            return Integer.toString(11 - (soma % 11));
        }
    }

    @Override
    public String codigoBarras() {
        String iniCodigoBarras = "", fimCodigoBarras = "";
        iniCodigoBarras = boleto.getContaCobranca().getContaBanco().getBanco().getNumero() + boleto.getContaCobranca().getMoeda(); // banco + moeda

        //fimCodigoBarras += fatorVencimento(movimento.getDtVencimento());   // fator de vencimento
        fimCodigoBarras += fatorVencimentoSicoob(vencimento);   // fator de vencimento

        int tam = Moeda.limparPonto(Moeda.converteR$Double(valor)).length();

        fimCodigoBarras += "0000000000".substring(0, 10 - tam) + Moeda.limparPonto(Moeda.converteR$Double(valor)); // valor

        fimCodigoBarras += boleto.getContaCobranca().getCarteira();       // carteira

        fimCodigoBarras += boleto.getContaCobranca().getContaBanco().getAgencia();
        if (boleto.getContaCobranca().getCobrancaRegistrada().getId() != 3) {
            fimCodigoBarras += "01";        // modalidade -- 01 com registro no banco // -- 01 sem registro no banco
        } else {
            fimCodigoBarras += "02";        // modalidade -- 02 com registro no banco // -- 02 sem registro no banco
        }

        String cedente = "0000000".substring(0, 7 - boleto.getContaCobranca().getCodCedente().length()) + boleto.getContaCobranca().getCodCedente();        // codigo cedente
        fimCodigoBarras += cedente;

        String nossoNumero = "";
        if (boleto.getContaCobranca().getCobrancaRegistrada().getId() != 3 && boleto.getDtCobrancaRegistrada() != null) {
            nossoNumero = boleto.getBoletoComposto();
        } else {
            nossoNumero = boleto.getBoletoComposto() + calculoConstante();
        }

        fimCodigoBarras += "00000000".substring(0, 8 - nossoNumero.length()) + nossoNumero;       // nosso numero

        fimCodigoBarras += "001";       // numero da parcela

        return iniCodigoBarras + this.moduloOnze(iniCodigoBarras + fimCodigoBarras) + fimCodigoBarras;
    }

    @Override
    public String representacao() {
        String codigoBarras = this.codigoBarras();
        String swap = "";
        int i = 0;
        String repNumerica = codigoBarras.substring(0, 4);
        repNumerica += codigoBarras.substring(19, 24);
        repNumerica += moduloDez(repNumerica);
        repNumerica += codigoBarras.substring(24, 34);
        repNumerica += moduloDez(codigoBarras.substring(24, 34));
        repNumerica += codigoBarras.substring(34, 44);
        repNumerica += moduloDez(codigoBarras.substring(34, 44));
        repNumerica += codigoBarras.substring(4, 5);

        swap += codigoBarras.substring(5, 19);
        i = 0;
        while (i < (15 - swap.length())) {
            swap = ("0" + swap);
            i++;
        }
        repNumerica += swap;
        repNumerica = repNumerica.substring(0, 5) + "."
                + repNumerica.substring(5, 10) + " "
                + repNumerica.substring(10, 15) + "."
                + repNumerica.substring(15, 21) + " "
                + repNumerica.substring(21, 26) + "."
                + repNumerica.substring(26, 32) + " "
                + repNumerica.substring(32, 33) + " "
                + repNumerica.substring(34, repNumerica.length());
        return repNumerica;
    }

    public String calculoConstante() {
        String agencia = boleto.getContaCobranca().getContaBanco().getAgencia();
        String cedente = "0000000".substring(0, 7 - boleto.getContaCobranca().getCodCedente().length()) + boleto.getContaCobranca().getCodCedente();
        String composicao = agencia + ("0000000000".substring(0, 10 - cedente.length()) + cedente) + "0000000".substring(0, 7 - boleto.getBoletoComposto().length()) + boleto.getBoletoComposto();
        //String composicao = "000100000000190000021";

        if (!composicao.isEmpty()) {
            int soma = 0;

            int peso = 0;
            String constante[] = new String[4];
            constante[0] = "3";
            constante[1] = "1";
            constante[2] = "9";
            constante[3] = "7";

            String quebra[] = composicao.split("");

            for (String quebra1 : quebra) {
                if (!quebra1.isEmpty()) {
                    if (Integer.valueOf(quebra1) != 0) {
                        int um = Integer.valueOf(quebra1);
                        int vezes = Integer.valueOf(constante[peso]);
                        soma = (um * vezes) + soma;
                    }
                    if (peso < 3) {
                        peso = peso + 1;
                    } else {
                        peso = 0;
                    }
                }
            }
            if ((soma % 11) == 0 || (soma % 11) == 1) {
                composicao = "0";
            } else {
                composicao = Integer.toString(11 - (soma % 11));
            }

//            if ((11 - (soma % 11)) == 0 || (11 - (soma % 11)) == 1 || (11 - (soma % 11)) > 9) {
//                // NO MANUAL NÃO FALA DO CASO DE SER MAIOR QUE 9, PORÉM EM UM TESTE CAIU ESSE CASO
//            //if ((11 - (soma % 11)) == 0 || (11 - (soma % 11)) == 1) {
//                composicao = "0";
//            } else {
//                composicao = Integer.toString(11 - (soma % 11));
//            }
        }
        return composicao;
    }

    @Override
    public String getNossoNumeroFormatado() {
        if (boleto.getContaCobranca().getCobrancaRegistrada().getId() != 3 && boleto.getDtCobrancaRegistrada() != null) {
            return boleto.getBoletoComposto();
        } else {
            return boleto.getBoletoComposto() + "-" + calculoConstante();
        }
    }

    @Override
    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente();
    }

    @Override
    public String codigoBanco() {
        return "756";
    }

    public String fatorVencimentoSicoob(Date vencimento) {
        if (vencimento != null) {
            Date dataModel = DataHoje.converte("03/07/2000");
            long dias = vencimento.getTime() - dataModel.getTime();
            // CORRIGE DATAS COM HORÁRIO DE VERÃO -- 3600000
            long total = (dias + 3600000) / 86400000;
            //long total = (dias) / 86400000;
            total = total + 1000;

            //long totalx = (dias) / 86400000;
            //totalx = totalx + 1000;
            return Long.toString(total);
        } else {
            return "";
        }
    }

    @Override
    public RespostaArquivoRemessa gerarRemessa240() {
        PessoaEnderecoDao ped = new PessoaEnderecoDao();

        Dao dao = new Dao();
        dao.openTransaction();

        Remessa remessa = new Remessa(-1, "", DataHoje.dataHoje(), DataHoje.horaMinuto(), null, Usuario.getUsuario(), null);
        if (!dao.save(remessa)) {
            dao.rollback();
            return new RespostaArquivoRemessa(null, "Erro ao salvar Remessa");
        }

        List<String> list_log = new ArrayList();
        list_log.add("** Nova Remessa **");
        list_log.add("ID: " + remessa.getId());
        list_log.add("NOME: " + remessa.getNomeArquivo());
        list_log.add("EMISSÃO: " + remessa.getDtEmissaoString());
        list_log.add("HORA EMISSÃO: " + remessa.getHoraEmissao() + "\n");
        list_log.add("** Movimentos **");

        String nome_arquivo = "E" + DataHoje.data().substring(0, 2) + "00000".substring(0, 5 - ("" + remessa.getId()).length()) + ("" + remessa.getId()) + ".REM";

        remessa.setNomeArquivo(nome_arquivo);

        if (!dao.update(remessa)) {
            dao.rollback();
            return new RespostaArquivoRemessa(null, "Erro ao atualizar Remessa");
        }

        try {
            String patch = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos");

            FacesContext context = FacesContext.getCurrentInstance();
            String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/remessa/");
            String destino = caminho + "/" + remessa.getId();

            File flDes = new File(destino);
            flDes.mkdirs();

            destino += "/" + nome_arquivo;

            FileOutputStream file_out = new FileOutputStream(destino);
            file_out.close();

            FileWriter file_writer = new FileWriter(destino);
            BufferedWriter buff_writer = new BufferedWriter(file_writer);

            String CONTEUDO_REMESSA = "";

            if (listaBoletoRemessa.isEmpty()) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Lista de Boleto vazia");
            }
            // header do arquivo -----------------------------------------------
            // -----------------------------------------------------------------
            Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
            String documento_sindicato = sindicato.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");

            CONTEUDO_REMESSA += "756"; // 01.0 Código do Sicoob na Compensação: "756"
            CONTEUDO_REMESSA += "0000"; // 02.0 Lote de Serviço: "0000"
            CONTEUDO_REMESSA += "0"; // 03.0 Tipo de Registro: "0"
            CONTEUDO_REMESSA += "         "; // Uso Exclusivo FEBRABAN / CNAB: Preencher com espaços em branco
            CONTEUDO_REMESSA += "2"; // 05.0 "Tipo de Inscrição da Empresa: '1'  =  CPF '2'  =  CGC / CNPJ"
            CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // 06.0 Número de Inscrição da Empresa
            CONTEUDO_REMESSA += "                    "; // 07.0 Código do Convênio no Sicoob: Preencher com espaços em branco

            Boleto boleto_rem = listaBoletoRemessa.get(0).getBoleto();
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            //String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").split("-")[0];
            String conta_digito = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").split("-")[1];
            String cedente = boleto_rem.getContaCobranca().getCedente();
            String codigo_cedente = boleto_rem.getContaCobranca().getCodCedente();

            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.0 Prefixo da Cooperativa: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.0 Dígito Verificador do Prefixo: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += "000000000000".substring(0, 12 - conta.length()) + conta; // 10.0 Conta Corrente: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(conta)); // 11.0 Dígito Verificador da Conta: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += "0";//moduloOnze("" + Integer.valueOf(codigo_cedente)); // 12.0 Dígito Verificador da Ag/Conta: Preencher com zeros
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 13.0 Nome da Empresa
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper(("SICOOB                        ").substring(0, 30)); // 14.0 Nome do Banco: SICOOB
            CONTEUDO_REMESSA += "          "; // 15.0 Uso Exclusivo FEBRABAN / CNAB: Preencher com espaços em branco
            CONTEUDO_REMESSA += "1"; // 16.0 Código Remessa / Retorno: "1"
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 17.0 Data de Geração do Arquivo
            CONTEUDO_REMESSA += DataHoje.hora().replace(":", ""); // 18.0 Hora de Geração do Arquivo
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 19.0 Número Seqüencial do Arquivo: Número seqüencial adotado e controlado pelo responsável pela geração do arquivo para ordenar a disposição dos arquivos encaminhados. Evoluir um número seqüencial a cada header de arquivo.
            CONTEUDO_REMESSA += "081"; // 20.0 No da Versão do Layout do Arquivo: "081"
            CONTEUDO_REMESSA += "00000"; // 21.0 Densidade de Gravação do Arquivo: "00000"
            CONTEUDO_REMESSA += "                    "; // 22.0 Para Uso Reservado do Banco: Preencher com espaços em branco
            CONTEUDO_REMESSA += "                    "; // 23.0 Para Uso Reservado da Empresa: Preencher com espaços em branco
            CONTEUDO_REMESSA += "                             "; // 24.0 Uso Exclusivo FEBRABAN / CNAB: Preencher com espaços em branco

            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Header de Arquivo menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");
            CONTEUDO_REMESSA = "";

            Integer sequencial_lote = 1;

            // header do lote ------------------------------------------------------------
            // ---------------------------------------------------------------------------
            CONTEUDO_REMESSA += "756"; // 01.1 Código do Banco na Compensação: "756"
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.1 "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
            CONTEUDO_REMESSA += "1"; // 03.1 Tipo de Registro: "1"
            CONTEUDO_REMESSA += "R"; // 04.1 Tipo de Operação: "R"

            // SE COBRANÇA REGISTRADA
            if (true == true) {
                CONTEUDO_REMESSA += "01"; // 05.1 Tipo de Serviço: "01"
            } else {
                CONTEUDO_REMESSA += "02"; // 05.1 Tipo de Serviço: "02"
            }

            CONTEUDO_REMESSA += "  "; // 06.1 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            CONTEUDO_REMESSA += "040"; // 07.1 Nº da Versão do Layout do Lote: "040"
            CONTEUDO_REMESSA += " "; // 08.1 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco

            CONTEUDO_REMESSA += "2"; // 09.1 "Tipo de Inscrição da Empresa: '1'  =  CPF '2'  =  CGC / CNPJ"
            CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_sindicato.length()) + documento_sindicato; // 10.1 Nº de Inscrição da Empresa
            CONTEUDO_REMESSA += "                    ";//.substring(0, 20 - codigo_cedente.length()) + codigo_cedente; // 11.1 Código do Convênio no Banco: Preencher com espaços em branco
            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 12.1 Prefixo da Cooperativa: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 13.1 Dígito Verificador do Prefixo: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += "000000000000".substring(0, 12 - conta.length()) + conta; // 14.1 Conta Corrente: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(conta)); // 15.1 Dígito Verificador da Conta: vide planilha "Contracapa" deste arquivo
            CONTEUDO_REMESSA += " ";//moduloOnze("" + Integer.valueOf(codigo_cedente)); // 16.1 Dígito Verificador da Ag/Conta: Preencher com espaços em branco
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 17.1 Nome da Empresa
            CONTEUDO_REMESSA += "                                        "; // 18.1 "Mensagem 1: Texto referente a mensagens que serão impressas em todos os boletos referentes ao mesmo lote. Estes campos não serão utilizados no arquivo retorno."
            CONTEUDO_REMESSA += "                                        "; // 19.1 "Mensagem 2: Texto referente a mensagens que serão impressas em todos os boletos referentes ao mesmo lote. Estes campos não serão utilizados no arquivo retorno."
            CONTEUDO_REMESSA += "00000000".substring(0, 8 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 20.1 "Número Remessa/Retorno: Número adotado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo para identificar a seqüência de envio ou devolução do arquivo entre o Beneficiário e o Sicoob. Caso número não seja informado, retornará zeros."
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 21.1 Data de Gravação Remessa/Retorno
            CONTEUDO_REMESSA += "00000000"; // 22.1 Data do Crédito: "00000000"
            CONTEUDO_REMESSA += "                                 "; // 23.1 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Header do Lote menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            Double valor_total_lote = (double) 0;

            // QUANTIDADE DE SEGMENTOS [ P / Q / Y-53 / R ]
            Integer quantidade_lote = 0;

            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Integer sequencial_registro_lote = 1;
            for (Integer i = 0; i < listaBoletoRemessa.size(); i++) {
                Boleto bol = listaBoletoRemessa.get(i).getBoleto();
                StatusRemessa sr = listaBoletoRemessa.get(i).getStatusRemessa();

                // tipo 3 - segmento P -------------------------------------------------------
                // ---------------------------------------------------------------------------
                quantidade_lote++;
                CONTEUDO_REMESSA += "756"; // 01.3P Código do Banco na Compensação: "756"
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3P "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
                CONTEUDO_REMESSA += "3"; // 03.3P Tipo de Registro: "3"
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3P Nº Sequencial do Registro no Lote: Número adotado para identificar a sequência de registros encaminhados no lote. Preencher com '00001' para o primeiro segmento P do lote do arquivo. Para os demais: número do segmento anterior acrescido de 1.
                CONTEUDO_REMESSA += "P"; // 05.3P Cód. Segmento do Registro Detalhe: "P"
                CONTEUDO_REMESSA += " "; // 06.3P Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 07.3P "Código de Movimento Remessa: '01' = Entrada de Títulos '09' = Protestar '10' = Desistência do Protesto e Baixar Título '11' = Desistência do Protesto e manter em carteira '31' = Alterações de outros dados"
                } else {
                    CONTEUDO_REMESSA += "02"; // 07.3P "Código de Movimento Remessa: '01' = Entrada de Títulos '09' = Protestar '10' = Desistência do Protesto e Baixar Título '11' = Desistência do Protesto e manter em carteira '31' = Alterações de outros dados"
                }
                CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.3P Prefixo da Cooperativa: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.3P Dígito Verificador do Prefixo: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += "000000000000".substring(0, 12 - conta.length()) + conta; // 10.3P Conta Corrente: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(conta)); // 11.3P Dígito Verificador da Conta: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += " ";// moduloOnze("" + Integer.valueOf(codigo_cedente)); // 12.3P Dígito Verificador da Ag/Conta: Preencher com espaços em branco
                // 14 cobraça registrada // JÁ ESTA NO NÚMERO DO DOCUMENTO EM MOVIMENTO
                //CONTEUDO_REMESSA += "14"; // 13.3P Carteira/Nosso Número Modalidade da Carteira 41 42 9(002) Ver Nota Explicativa G069 *G069
                CONTEUDO_REMESSA += (bol.getBoletoComposto() + "                    ").substring(0, 20); // 13.3P Nosso Número
                CONTEUDO_REMESSA += "1"; // 14.3P Código da Carteira: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += "0"; // 15.3P Forma de Cadastr. do Título no Banco: "0"
                CONTEUDO_REMESSA += " "; // 16.3P Tipo de Documento: Preencher com espaços em branco
                CONTEUDO_REMESSA += "2"; // 17.3P "Identificação da Emissão do Boleto: (vide planilha ""Contracapa"" deste arquivo) '1'  =  Sicoob Emite '2'  =  Beneficiário Emite"
                CONTEUDO_REMESSA += "2"; // 18.3P "Identificação da Distribuição do Boleto: (vide planilha ""Contracapa"" deste arquivo) '1'  =  Sicoob Distribui '2'  =  Beneficiário Distribui"

                CONTEUDO_REMESSA += "               ".substring(0, 15 - ("" + bol.getId()).length()) + bol.getId(); // 19.3P Número do Documento de Cobrança: Número adotado e controlado pelo Cliente, para identificar o título de cobrança. Informação utilizada pelo Sicoob para referenciar a identificação do documento objeto de cobrança. Poderá conter número de duplicata, no caso de cobrança de duplicatas; número da apólice, no caso de cobrança de seguros, etc.

                CONTEUDO_REMESSA += bol.getVencimento().replace("/", ""); // 20.3P Data de Vencimento do Título

                Double valor_titulo_double = new Double(0);

                // bol.getNrCtrBoleto().length() != 22 ARRECADAÇÃO
                if (bol.getNrCtrBoleto().length() != 22) {
                    List<Movimento> lista_m = bol.getListaMovimento();
                    for (Movimento m : lista_m) {
                        valor_titulo_double = Moeda.soma(valor_titulo_double, m.getValor());
                    }
                } else if (bol.getNrCtrBoleto().length() == 22) {
                    // bol.getNrCtrBoleto().length() == 22 ASSOCIATIVO
                    valor_titulo_double = new FunctionsDao().func_correcao_valor_ass(bol.getNrCtrBoleto());
                }

                String valor_titulo = Moeda.converteDoubleToString(valor_titulo_double).replace(".", "").replace(",", "");
                // NO MANUAL FALA 13 PORÉM TEM QUE SER 15, ACHO QUE POR CAUSA DAS DECIMAIS ,00 (O MANUAL NÃO EXPLICA ISSO)
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - valor_titulo.length()) + valor_titulo; // 21.3P Valor Nominal do Título
                CONTEUDO_REMESSA += "00000"; // 22.3P Agência Encarregada da Cobrança: "00000"
                CONTEUDO_REMESSA += " "; // 23.3P Dígito Verificador da Agência: Preencher com espaços em branco
                CONTEUDO_REMESSA += "02";// 24.3P Espécie de Título

                String aceite = boleto_rem.getContaCobranca().getAceite().equals("N") ? boleto_rem.getContaCobranca().getAceite() : "A";
                CONTEUDO_REMESSA += aceite; // 25.3P "Identific. de Título Aceito/Não Aceito: Código adotado pela FEBRABAN para identificar se o título de cobrança foi aceito (reconhecimento da dívida pelo Pagador). 'A'  =  Aceite 'N'  =  Não Aceite"
                CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 26.3P Data da Emissão do Título

                CONTEUDO_REMESSA += "0"; // 
                CONTEUDO_REMESSA += "00000000"; // 
                CONTEUDO_REMESSA += "000000000000000"; // 

                if (boleto_rem.getContaCobranca().getJurosMensal() <= 0) {
                    CONTEUDO_REMESSA += "0"; // 27.3P "Código do Juros de Mora: '0'  =  Isento '1'  =  Valor por Dia '2'  =  Taxa Mensal"
                    CONTEUDO_REMESSA += "00000000"; // 28.3P Data do Juros de Mora: preencher com a Data de Vencimento do Título formato DDMMAAAA
                    CONTEUDO_REMESSA += "000000000000000"; // 29.3P Juros Mora
                } else {
                    CONTEUDO_REMESSA += "2"; //  27.3P "Código do Juros de Mora: '0'  =  Isento '1'  =  Valor por Dia '2'  =  Taxa Mensal"
                    CONTEUDO_REMESSA += bol.getVencimento().replace("/", ""); // 28.3P Data do Juros de Mora: preencher com a Data de Vencimento do Título formato DDMMAAAA
                    String jr = Moeda.converteDoubleToString(boleto_rem.getContaCobranca().getJurosMensal()).replace(".", "").replace(",", "");
                    CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - jr.length()) + jr;  // 29.3P Juros Mora
                }

                CONTEUDO_REMESSA += "0"; // 30.3P "Código do Desconto 1 '0'  =  Não Conceder desconto '1'  =  Valor Fixo Até a Data Informada '2'  =  Percentual Até a Data Informada"
                CONTEUDO_REMESSA += "00000000"; // 31.3P Data do Desconto 1
                CONTEUDO_REMESSA += "000000000000000"; // 32.3P Valor/Percentual a ser Concedido
                CONTEUDO_REMESSA += "000000000000000"; // 33.3P Valor do IOF a ser Recolhido
                CONTEUDO_REMESSA += "000000000000000"; // 34.3P Valor do Abatimento
                CONTEUDO_REMESSA += "                         ".substring(0, 25 - ("" + bol.getId()).length()) + bol.getId(); // 35.3P Identificação do Título na Empresa: Campo destinado para uso do Beneficiário para identificação do Título.
                CONTEUDO_REMESSA += "3"; // 36.3P "'1' = Protestar dias corridos '2' = Protestar dias úteis '3' = Não Protestar '9' = Cancelar Instrução de Protesto 
                // O código '9' deverá ser utilizado para cancelar um agendamento futuro de protesto e deverá estar atrelado obrigatóriamente ao código de entrada '31'."

                CONTEUDO_REMESSA += "00"; // 37.3P "Informar prazo de inicio do protesto a partir do vencimento '0' - Não protestar"
                CONTEUDO_REMESSA += "0"; // 38.3P Código para Baixa/Devolução: "0"
                CONTEUDO_REMESSA += "   "; // 39.3P Número de Dias para Baixa/Devolução: Preencher com espaços em branco
                CONTEUDO_REMESSA += "09"; // 40.3P "Código da Moeda: '02'  =  Dólar Americano Comercial (Venda) '09'  = Real"
                CONTEUDO_REMESSA += "0000000000"; // 41.3P Nº do Contrato da Operação de Créd.: "0000000000"
                CONTEUDO_REMESSA += " "; // 42.3P Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                if (CONTEUDO_REMESSA.length() != 240) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Segmento P menor que 240: " + CONTEUDO_REMESSA);
                }
                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                // tipo 3 - segmento Q -------------------------------------------------------
                // ---------------------------------------------------------------------------
                quantidade_lote++;
                CONTEUDO_REMESSA += "756"; // 01.3Q Código do Banco na Compensação: "756"
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3Q "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
                CONTEUDO_REMESSA += "3"; // 03.3Q Tipo de Registro: "3"

                sequencial_registro_lote++;
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3Q "Nº Sequencial do Registro no Lote: Número adotado para identificar a sequência de registros encaminhados no lote. Preencher com '00001' para o primeiro segmento P do lote do arquivo. Para os demais: número do segmento anterior acrescido de 1. Ex: Se segmento anterior P = ""00001"". Então, segmento Q = ""00002"" e assim consecutivamente."
                CONTEUDO_REMESSA += "Q"; // 05.3Q Cód. Segmento do Registro Detalhe: "Q"
                CONTEUDO_REMESSA += " "; // 06.3Q Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 07.3Q "Código de Movimento Remessa: '01'  =  Entrada de Títulos" // REGISTRAR
                } else {
                    CONTEUDO_REMESSA += "02"; // 07.3Q "Código de Movimento Remessa: '01'  =  Entrada de Títulos" // BAIXAR
                }
                // 08.3Q "Tipo de Inscrição Pagador: '1'  =  CPF '2'  =  CGC / CNPJ"
                Pessoa pessoa = bol.getPessoa();

                if (pessoa.getTipoDocumento().getId() == 1) { // CPF
                    CONTEUDO_REMESSA += "1"; // 08.3Q 
                } else if (pessoa.getTipoDocumento().getId() == 2) { // CNPJ
                    CONTEUDO_REMESSA += "2"; // 08.3Q 
                }

                String documento_pessoa = pessoa.getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_pessoa.length()) + documento_pessoa; // 09.3Q Número de Inscrição

                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((pessoa.getNome() + "                                        ").substring(0, 40)); // 10.3Q Nome

                PessoaEndereco pessoa_endereco = ped.pesquisaEndPorPessoaTipo(pessoa.getId(), 3);
                if (pessoa_endereco != null) {
                    String end_rua = pessoa_endereco.getEndereco().getLogradouro().getDescricao(),
                            end_descricao = pessoa_endereco.getEndereco().getDescricaoEndereco().getDescricao(),
                            end_numero = pessoa_endereco.getNumero(),
                            end_bairro = pessoa_endereco.getEndereco().getBairro().getDescricao(),
                            end_cep = pessoa_endereco.getEndereco().getCep(),
                            end_cidade = pessoa_endereco.getEndereco().getCidade().getCidade(),
                            end_uf = pessoa_endereco.getEndereco().getCidade().getUf();

                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_rua + " " + end_descricao + " " + end_numero + "                                        ").substring(0, 40)); // 11.3Q Endereço
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_bairro + "               ").substring(0, 15)); // 12.3Q Bairro
                    String cep = end_cep.replace("-", "").replace(".", "");
                    if (cep.length() < 8) {
                        dao.rollback();
                        return new RespostaArquivoRemessa(null, pessoa.getNome() + " CEP INVÁLIDO: " + cep);
                    }
                    CONTEUDO_REMESSA += cep.substring(0, 5); // 13.3Q CEP
                    CONTEUDO_REMESSA += cep.substring(5, 8); // 14.3Q Sufixo do CEP
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_cidade + "               ").substring(0, 15)); // 15.3Q Cidade
                    CONTEUDO_REMESSA += end_uf; // 16.3Q UF  - Unidade da Federação
                } else {
                    CONTEUDO_REMESSA += "                                        "; // 11.3Q Endereço
                    CONTEUDO_REMESSA += "               "; // 12.3Q Bairro
                    CONTEUDO_REMESSA += "     "; // 13.3Q CEP
                    CONTEUDO_REMESSA += "   "; // 14.3Q Sufixo CEP
                    CONTEUDO_REMESSA += "               "; // 15.3Q Cidade
                    CONTEUDO_REMESSA += "  "; // 16.3Q Unidade da Federação
                }

                CONTEUDO_REMESSA += "0"; // 17.3Q "Tipo de Inscrição Sacador Avalista: '1'  =  CPF '2'  =  CGC / CNPJ"
                CONTEUDO_REMESSA += "000000000000000"; // 18.3Q Número de Inscrição
                CONTEUDO_REMESSA += "                                        "; // 19.3Q Nome do Sacador/Avalista
                CONTEUDO_REMESSA += "   "; // 20.3Q "Cód. Bco. Corresp. na Compensação: Caso o Beneficiário não tenha contratado a opção de Banco Correspondente com o Sicoob, preencher com ""000""; Caso o Beneficiário tenha contratado a opção de Banco Correspondente com o Sicoob e a emissão seja a cargo do Sicoob (SEQ 17.3.P do Segmento P do Detalhe), preencher com ""001"" (Banco do Brasil) ou ""237"" (Banco Bradesco)."
                CONTEUDO_REMESSA += "                    "; // 21.3Q "Nosso Nº no Banco Correspondente: ""1323739"" (Banco do Brasil) ou ""4498893"" (Banco Bradesco). O campo NN deve ser preenchido, somente nos casos em que o campo anterior tenha indicado o uso do Banco Correspondente. Obs.: O preenchimento deste campo será alinha à esquerda a partir da posição 213 indo até 219."
                CONTEUDO_REMESSA += "        "; // 22.3Q Uso Exclusivo FEBRABAN/CNAB

                if (CONTEUDO_REMESSA.length() != 240) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Segmento Q menor que 240: " + CONTEUDO_REMESSA);
                }

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");
                CONTEUDO_REMESSA = "";

                // CRIA O SEGMENTO R CASO TENHA MULTA EM CONTA COBRANÇA
                if (boleto_rem.getContaCobranca().getMulta() > 0) {
                    // tipo 3 - segmento R -------------------------------------------------------
                    // ---------------------------------------------------------------------------
                    quantidade_lote++;
                    CONTEUDO_REMESSA += "756"; // 01.3R Código do Banco na Compensação: "756"
                    CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3R 
                    CONTEUDO_REMESSA += "3"; // 03.3R Tipo de Registro: "3"

                    sequencial_registro_lote++;
                    CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3R
                    CONTEUDO_REMESSA += "R"; // 05.3R Cód. Segmento do Registro Detalhe: "R"
                    CONTEUDO_REMESSA += " "; // 06.3R Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                    CONTEUDO_REMESSA += "01"; // 07.3R '01'  =  Entrada de Títulos

                    CONTEUDO_REMESSA += "0"; // 08.3R Código do Desconto 2 '0'  =  Não Conceder desconto '1'  =  Valor Fixo Até a Data Informada '2'  =  Percentual Até a Data Informada
                    CONTEUDO_REMESSA += "00000000"; // 09.3R Data do Desconto 2
                    CONTEUDO_REMESSA += "000000000000000"; // 10.3R Valor/Percentual a ser Concedido

                    CONTEUDO_REMESSA += "0"; // 11.3R Código do Desconto 3 '0'  =  Não Conceder desconto '1'  =  Valor Fixo Até a Data Informada '2'  =  Percentual Até a Data Informada
                    CONTEUDO_REMESSA += "00000000"; // 12.3R Data do Desconto 3: "00000000"
                    CONTEUDO_REMESSA += "000000000000000"; // 13.3R Valor/Percentual a ser Concedido: "000000000000000"

                    CONTEUDO_REMESSA += "2"; // 14.3R Código da Multa: '0'  =  Isento '1'  =  Valor Fixo '2'  =  Percentual
                    CONTEUDO_REMESSA += bol.getVencimento().replace("/", ""); // 15.3R Data da Multa: preencher com a Data de Vencimento do Título formato DDMMAAAA
                    String ml = Moeda.converteDoubleToString(boleto_rem.getContaCobranca().getMulta()).replace(".", "").replace(",", "");
                    CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - ml.length()) + ml;  // 16.3R Valor/Percentual a Ser Aplicado Ex: 0000000000220 = 2,20%; Ex: 0000000001040 = 10,40%

                    CONTEUDO_REMESSA += "          ";  // 17.3R Informação ao Pagador: Preencher com espaços em branco
                    CONTEUDO_REMESSA += "                                        ";  // 18.3R Informação 3 Mensagem 3
                    CONTEUDO_REMESSA += "                                        ";  // 19.3R Informação 4 Mensagem 4
                    CONTEUDO_REMESSA += "                    ";  // 20.3R Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                    CONTEUDO_REMESSA += "00000000";  // 21.3R Cód. Ocor. do Pagador: "00000000"
                    CONTEUDO_REMESSA += "000";  // 22.3R Cód. do Banco na Conta do Débito: "000"
                    CONTEUDO_REMESSA += "00000";  // 23.3R Código da Agência do Débito: "00000"
                    CONTEUDO_REMESSA += " ";  // 24.3R Dígito Verificador da Agência: Preencher com espaços em branco
                    CONTEUDO_REMESSA += "000000000000";  // 25.3R Conta Corrente para Débito: "000000000000"
                    CONTEUDO_REMESSA += " ";  // 26.3R Dígito Verificador da Conta: Preencher com espaços em branco
                    CONTEUDO_REMESSA += " ";  // 27.3R Dígito Verificador Ag/Conta: Preencher com espaços em branco
                    CONTEUDO_REMESSA += "0";  // 28.3R Aviso para Débito Automático: "0"
                    CONTEUDO_REMESSA += "         ";  // 29.3R Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco

                    if (CONTEUDO_REMESSA.length() != 240) {
                        dao.rollback();
                        return new RespostaArquivoRemessa(null, "Segmento R menor que 240: " + CONTEUDO_REMESSA);
                    }

                    buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                    CONTEUDO_REMESSA = "";

                }

                sequencial_registro_lote++;

                valor_total_lote = Moeda.soma(valor_total_lote, valor_titulo_double);

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, bol, listaBoletoRemessa.get(i).getStatusRemessa());

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Erro ao salvar Remessa Banco");
                }

                list_log.add("ID: " + bol.getId());
                list_log.add("Valor: " + valor_titulo);
                list_log.add("-----------------------");
            }

            // rodapé(footer) do lote ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "756"; // 01.5 Código do Banco na Compensação: "756"
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.5 "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
            CONTEUDO_REMESSA += "5"; // 03.5 Tipo de Registro: "5"
            CONTEUDO_REMESSA += "         "; // 04.5 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            Integer quantidade_lote_fim = quantidade_lote + 2;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_lote_fim).length()) + ("" + quantidade_lote_fim); // 05.5 Quantidade de Registros no Lote
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + listaBoletoRemessa.size()).length()) + ("" + listaBoletoRemessa.size()); // 06.5 Quantidade de Títulos em Cobrança
            String valor_total = valor_total_lote.toString().replace(".", "").replace(",", "");
            CONTEUDO_REMESSA += "00000000000000000".substring(0, 17 - valor_total.length()) + valor_total; // 07.5 Valor Total dosTítulos em Carteiras
            CONTEUDO_REMESSA += "000000"; // 08.5 Quantidade de Títulos em Cobrança
            CONTEUDO_REMESSA += "00000000000000000"; // 09.5 Valor Total dosTítulos em Carteiras
            CONTEUDO_REMESSA += "000000"; // 10.5 Quantidade de Títulos em Cobrança
            CONTEUDO_REMESSA += "00000000000000000"; // 11.5 Quantidade de Títulos em Carteiras
            CONTEUDO_REMESSA += "000000"; // 12.5 Quantidade de Títulos em Cobrança
            CONTEUDO_REMESSA += "00000000000000000"; // 13.5 Valor Total dosTítulos em Carteiras
            CONTEUDO_REMESSA += "        "; // 14.5 Número do Aviso de Lançamento: Preencher com espaços em branco
            CONTEUDO_REMESSA += "                                                                                                                     "; // 15.5 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Rodapé do Lote menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            // rodapé(footer) do arquivo ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "756"; // 01.9 Código do Banco na Compensação: "756"
            CONTEUDO_REMESSA += "9999"; // 02.9 Preencher com '9999'
            CONTEUDO_REMESSA += "9"; // 03.9 Tipo de Registro: "9"
            CONTEUDO_REMESSA += "         "; // 04.9 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            CONTEUDO_REMESSA += "000001"; // 05.9 Quantidade de Lotes do Arquivo

            Integer quantidade_registros = quantidade_lote + 4;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_registros).length()) + ("" + quantidade_registros); // 06.9 Quantidade de Registros do Arquivo
            CONTEUDO_REMESSA += "000000"; // 07.9 Qtde de Contas p/ Conc. (Lotes): "000000"
            CONTEUDO_REMESSA += "                                                                                                                                                                                                             "; // 08.9 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Rodapé do Arquivo menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            buff_writer.flush();
            buff_writer.close();

            dao.commit();

            String log_string = "";
            log_string = list_log.stream().map((string_x) -> string_x + " \n").reduce(log_string, String::concat);
            NovoLog log = new NovoLog();
            log.save(
                    log_string
            );
            //dao.rollback();
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            return new RespostaArquivoRemessa(new File(destino), "");
        } catch (IOException | NumberFormatException e) {
            dao.rollback();
            return new RespostaArquivoRemessa(null, e.getMessage());
        }
    }

    @Override
    public RespostaArquivoRemessa gerarRemessa400() {
        return new RespostaArquivoRemessa(null, "Configuração do Arquivo não existe");
    }
//
//    // BACKUP FUNCIONA
//    @Override
//    public RespostaWebService registrarBoleto() {
//
//        MovimentosReceberSocialDao db_social = new MovimentosReceberSocialDao();
//        Dao dao = new Dao();
//
//        Registro reg = Registro.get();
//        Pessoa pessoa = db_social.responsavelBoleto(boleto.getNrCtrBoleto());
//
//        if (pessoa.getPessoaEndereco() == null) {
//            return new RespostaWebService(null, "Pessoa: " + pessoa.getNome() + " NÃO TEM ENDEREÇO TIPO 2");
//        }
//
//        List<Movimento> lista_movimento = db_social.listaMovimentosPorNrCtrBoleto(boleto.getNrCtrBoleto());
//
//        if (valor < 1) {
//            return new RespostaWebService(null, "Valor dos Boleto Registrados não podem ser menores que R$ 1,00, Boleto: (" + boleto.getNrBoleto() + ")");
//        }
////
////        if (boleto.getDtCobrancaRegistrada() != null) {
////
////            Boleto b = super.gerarNovoBoleto(boleto, vencimentoRegistro);
////
////            if (b == null) {
////                return new RespostaWebService(null, "Erro ao gerar novo Boleto");
////            } else {
////                boleto = b;
////            }
////
////        }
//
//        try {
//            // CASO QUEIRA TESTAR A ROTINA DE REGISTRO SEM REGISTRAR COLOCAR http://localhost:8080/Sindical?debug=true
//            if (TESTE) {
//                boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
//                boleto.setDtStatusRetorno(DataHoje.dataHoje());
//                boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));
//
//                new Dao().update(boleto, true);
//
//                new Dao().executeQuery(
//                        " INSERT INTO fin_movimento_boleto (id_movimento, id_boleto) \n "
//                        + "( \n "
//                        + "SELECT m.id, b.id \n "
//                        + "  FROM fin_boleto AS b \n "
//                        + " INNER JOIN fin_movimento AS m ON m.nr_ctr_boleto = b.nr_ctr_boleto \n "
//                        + "  LEFT JOIN fin_movimento_boleto AS mb ON mb.id_boleto = b.id AND mb.id_movimento = m.id \n "
//                        + " WHERE b.id = " + boleto.getId() + " \n "
//                        + "   AND mb.id IS NULL \n "
//                        + "   AND m.id_baixa IS NULL \n "
//                        + " GROUP BY m.id, b.id \n "
//                        + ");"
//                );
//
//                return new RespostaWebService(boleto, "");
//            }
//            
//            Boolean teste = testarWebService();
//
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//            HttpPost httppost;
//            if (!teste) {
//                httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/pesquisar_contribuinte");
//            } else {
//                httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/pesquisar_contribuinte");
//            }
//
//            List<NameValuePair> params = new ArrayList(2);
//            params.add(new BasicNameValuePair("codigo", "" + pessoa.getId()));
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//            HttpResponse response = httpclient.execute(httppost);
//            HttpEntity entity = response.getEntity();
//
//            // SE FOR IGUAL A NULL CADASTRAR CONTRIBUINTE
//            if (entity == null) {
//                if (!teste) {
//                    httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/salvar_contribuinte");
//                } else {
//                    httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/salvar_contribuinte");
//                }
//            } else if (!teste) {
//                httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/alterar_contribuinte");
//            } else {
//                httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/alterar_contribuinte");
//            }
//
//            params = new ArrayList(2);
//            params.add(new BasicNameValuePair("codigo", "" + pessoa.getId()));
//            params.add(new BasicNameValuePair("documento", pessoa.getDocumento()));
//            params.add(new BasicNameValuePair("nome", pessoa.getNome()));
//            params.add(new BasicNameValuePair("endereco", pessoa.getPessoaEndereco().getEndereco().getLogradouro().getDescricao() + " " + pessoa.getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao()));
//            params.add(new BasicNameValuePair("bairro", pessoa.getPessoaEndereco().getEndereco().getBairro().getDescricao()));
//            params.add(new BasicNameValuePair("cidade", pessoa.getPessoaEndereco().getEndereco().getCidade().getCidade()));
//            params.add(new BasicNameValuePair("uf", pessoa.getPessoaEndereco().getEndereco().getCidade().getUf()));
//            params.add(new BasicNameValuePair("cep", pessoa.getPessoaEndereco().getEndereco().getCep()));
//
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//            response = httpclient.execute(httppost);
//            entity = response.getEntity();
//
//            if (entity != null) {
//                String msg = EntityUtils.toString(entity);
//                JSONObject jSONObject = new JSONObject(msg);
//                boolean result = jSONObject.getBoolean("status");
//
//                if (!result) {
//                    String mens = jSONObject.getString("mensagem");
//                    return new RespostaWebService(null, mens);
//                }
//            }
//
//            httpclient.close();
//            httppost.abort();
//            httpclient = HttpClients.createDefault();
//
//            // PESQUISAR BOLETO
//            if (!teste) {
//                httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/pesquisar_boleto");
//            } else {
//                httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/pesquisar_boleto");
//            }
//
//            params = new ArrayList(2);
//            params.add(new BasicNameValuePair("nosso_numero", "" + boleto.getNrBoleto()));
//
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//
//            response = httpclient.execute(httppost);
//            entity = response.getEntity();
//
//            // SE NÃO EXISTIR BOLETO, CRIAR
//            httpclient.close();
//            httppost.abort();
//            httpclient = HttpClients.createDefault();
//
//            if (entity == null) {
//                if (!teste) {
//                    httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/criar_boleto");
//                } else {
//                    httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/criar_boleto");
//                }
//            } else if (!teste) {
//                httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/alterar_boleto");
//            } else {
//                httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/alterar_boleto");
//            }
//
//            params = new ArrayList(2);
//            params.add(new BasicNameValuePair("codigo_contribuinte", "" + pessoa.getId()));
//            params.add(new BasicNameValuePair("nosso_numero", "" + boleto.getNrBoleto()));
//            params.add(new BasicNameValuePair("numero_banco", "" + boleto.getContaCobranca().getContaBanco().getBanco().getNumero()));
//            params.add(new BasicNameValuePair("conta", "" + boleto.getContaCobranca().getContaBanco().getConta()));
//            params.add(new BasicNameValuePair("agencia", "" + boleto.getContaCobranca().getContaBanco().getAgencia()));
//            params.add(new BasicNameValuePair("codigo_cedente", "" + boleto.getContaCobranca().getCodCedente()));
//            params.add(new BasicNameValuePair("especie_documento", "DM"));
//            params.add(new BasicNameValuePair("layout", "1"));
//            params.add(new BasicNameValuePair("data_vencimento", DataHoje.converteData(vencimento).substring(0, 2) + DataHoje.converteData(vencimento).substring(3, 5) + DataHoje.converteData(vencimento).substring(6, 10)));
//            params.add(new BasicNameValuePair("referencia", lista_movimento.get(0).getReferencia().replace("/", "")));
//            params.add(new BasicNameValuePair("valor", Moeda.converteR$Double(valor).replace(".", "").replace(",", ".")));
//            params.add(new BasicNameValuePair("id_boleto", "" + boleto.getId()));
//
//            if (boleto.getContaCobranca().getJurosMensal() > 0) {
//                params.add(new BasicNameValuePair("juros_mensal", "" + boleto.getContaCobranca().getJurosMensal()));
//            } else {
//                params.add(new BasicNameValuePair("juros_mensal", "0"));
//            }
//
//            if (boleto.getContaCobranca().getMulta() > 0) {
//                params.add(new BasicNameValuePair("multa", "" + boleto.getContaCobranca().getMulta()));
//            } else {
//                params.add(new BasicNameValuePair("multa", "0"));
//            }
//
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//
//            response = httpclient.execute(httppost);
//            entity = response.getEntity();
//
//            if (entity != null) {
//                String msg = EntityUtils.toString(entity);
//                JSONObject jSONObject = new JSONObject(msg);
//                if (!jSONObject.getBoolean("status")) {
//                    // nao entendi
//                    //hash.put("mensagem", "Erro criação ou alteração do Boleto" + bol.getNrBoleto() + ", contate o Administrador.");
//                    //hash.put("mensagem", jSONObject.getBoolean("mensagem"));
//                    return new RespostaWebService(null, "Erro criação ou alteração do Boleto");
//                }
//            }
//
//            httpclient.close();
//            httppost.abort();
//
//            httpclient = HttpClients.createDefault();
//
//            if (!teste) {
//                httppost = new HttpPost("http://sindical.rtools.com.br:7076/webservice/cliente/" + reg.getChaveCliente() + "/imprimir_boleto");
//            } else {
//                httppost = new HttpPost("http://192.168.1.108:8084/webservice/cliente/" + reg.getChaveCliente() + "/imprimir_boleto_test");
//            }
//
//            params = new ArrayList(2);
//            params.add(new BasicNameValuePair("nosso_numero", "" + boleto.getNrBoleto()));
//
//            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//
//            response = httpclient.execute(httppost);
//            entity = response.getEntity();
//
//            if (entity != null) {
//                String msg = EntityUtils.toString(entity);
//                JSONObject jSONObject = new JSONObject(msg);
//                if (jSONObject.getBoolean("status")) {
//                    Integer index = 0;
//                    //int pos1 = msg.indexOf("75691.44111", index);
//                    int pos1 = msg.indexOf(boleto.getContaCobranca().getContaBanco().getBanco().getNumero() + "91." + boleto.getContaCobranca().getContaBanco().getAgencia() + "1", index);
//                    msg = msg.substring(pos1, pos1 + 58);
//                    msg = msg.replace(" ", "").replace(".", "");
//
//                    String nb = Integer.valueOf(msg.subSequence(21, 28).toString()).toString();
//
//                    boleto.setBoletoComposto(nb);
//                    boleto.setNrBoleto(Integer.valueOf(nb));
//
//                    boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
//                    boleto.setDtStatusRetorno(DataHoje.dataHoje());
//                    boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));
//
//                    new Dao().update(boleto, true);
//
//                    for (Movimento m : lista_movimento) {
//                        m.setDocumento(nb);
//                        new Dao().update(m, true);
//                    }
//
//                    new Dao().executeQuery(
//                            " INSERT INTO fin_movimento_boleto (id_movimento, id_boleto) \n "
//                            + "( \n "
//                            + "SELECT m.id, b.id \n "
//                            + "  FROM fin_boleto AS b \n "
//                            + " INNER JOIN fin_movimento AS m ON m.nr_ctr_boleto = b.nr_ctr_boleto \n "
//                            + "  LEFT JOIN fin_movimento_boleto AS mb ON mb.id_boleto = b.id AND mb.id_movimento = m.id \n "
//                            + " WHERE b.id = " + boleto.getId() + " \n "
//                            + "   AND mb.id IS NULL \n "
//                            + "   AND m.id_baixa IS NULL \n "
//                            + " GROUP BY m.id, b.id \n "
//                            + ");"
//                    );
//
//                    return new RespostaWebService(boleto, "");
//                } else {
//                    return new RespostaWebService(null, "Erro ao Registrar Boleto " + boleto.getNrBoleto() + "[" + jSONObject.getString("mensagem") + "], contate o Administrador.");
//                }
//            } else {
//                return new RespostaWebService(null, "Erro ao Registrar Boleto " + boleto.getNrBoleto() + ", contate o Administrador.");
//            }
//        } catch (IOException | UnsupportedOperationException e) {
//            return new RespostaWebService(null, e.getMessage());
//        }
//    }

    @Override
    public RespostaWebService registrarBoleto() {

        MovimentosReceberSocialDao db_social = new MovimentosReceberSocialDao();
        Dao dao = new Dao();

        String msgErro;

        Pessoa pessoa = db_social.responsavelBoleto(boleto.getNrCtrBoleto());

        if (pessoa.getPessoaEndereco() == null) {
            return new RespostaWebService(null, "Pessoa: " + pessoa.getNome() + " NÃO TEM ENDEREÇO TIPO 2");
        }

        List<Movimento> lista_movimento = db_social.listaMovimentosPorNrCtrBoleto(boleto.getNrCtrBoleto());

        if (valor < 1) {
            return new RespostaWebService(null, "Valor dos Boleto Registrados não podem ser menores que R$ 1,00, Boleto: (" + boleto.getNrBoleto() + ")");
        }

        try {
            // CASO QUEIRA TESTAR A ROTINA DE REGISTRO SEM REGISTRAR COLOCAR http://localhost:8080/Sindical?debug=true
            if (TESTE) {
                boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
                boleto.setDtStatusRetorno(DataHoje.dataHoje());
                boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

                new Dao().update(boleto, true);

                new Dao().executeQuery(
                        " INSERT INTO fin_movimento_boleto (id_movimento, id_boleto) \n "
                        + "( \n "
                        + "SELECT m.id, b.id \n "
                        + "  FROM fin_boleto AS b \n "
                        + " INNER JOIN fin_movimento AS m ON m.nr_ctr_boleto = b.nr_ctr_boleto \n "
                        + "  LEFT JOIN fin_movimento_boleto AS mb ON mb.id_boleto = b.id AND mb.id_movimento = m.id \n "
                        + " WHERE b.id = " + boleto.getId() + " \n "
                        + "   AND mb.id IS NULL \n "
                        + "   AND m.id_baixa IS NULL \n "
                        + " GROUP BY m.id, b.id \n "
                        + ");"
                );

                return new RespostaWebService(boleto, "");
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httppost = new HttpPost("https://geraboleto.sicoobnet.com.br/geradorBoleto/GerarBoleto.do");

            List<NameValuePair> params = new ArrayList(2);

            params.add(new BasicNameValuePair("coopCartao", boleto.getContaCobranca().getContaBanco().getAgencia().replace(".", "").replace("/", "").replace("-", ""))); // Cooperativa do Cliente coopCartao N X 4 - Cooperativas do sistema SICOOB 3260
            params.add(new BasicNameValuePair("numCliente", boleto.getContaCobranca().getCodCedente().replace(".", "").replace("/", "").replace("-", ""))); // Número do Cliente na Coop. numCliente N X 10 - Clientes cadastrados na cooperativa *** 70000 ou 68659 *** condominios rp
            params.add(new BasicNameValuePair("dataEmissao", DataHoje.data().substring(6, 10) + DataHoje.data().substring(3, 5) + DataHoje.data().substring(0, 2))); // Data da Emissão do Boleto dataEmissao N X 8 aaaammdd Informação do Cliente 20080521
            params.add(new BasicNameValuePair("codTipoVencimento", "1")); // Código do Tipo de Vencimento codTipoVencimento N X 1 - Legenda no Item 7.1 1 - // 1 - NORMAL, 2 - A VISTA, 3 - CONTRA APRENSENTAÇÃO 
            params.add(new BasicNameValuePair("dataVencimentoTit", boleto.getVencimento().substring(6, 10) + boleto.getVencimento().substring(3, 5) + boleto.getVencimento().substring(0, 2))); // Data de Vencimento do Título dataVencimentoTit N X 8 aaaammdd Informação do Cliente 20080621
            params.add(new BasicNameValuePair("valorTitulo", boleto.getValorString().replace(".", "").replace(",", "."))); // Valor do Título valorTitulo N X 9 - Informação do Cliente 325.63
            params.add(new BasicNameValuePair("numContaCorrente", boleto.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("/", "").replace("-", ""))); // Número da Conta Corrente numContaCorrente N X 10 - C/C do Cliente na Cooperativa 700003029
            params.add(new BasicNameValuePair("codEspDocumento", "DM")); // Espécie Documento codEspDocumento A X 3 - Legenda no Item 7.2 DM
            params.add(new BasicNameValuePair("nomeSacado", pessoa.getNome())); // Nome do Sacado nomeSacado A X 50 - Informação do Cliente Diego Neri
            params.add(new BasicNameValuePair("cpfCGC", pessoa.getDocumento().replace(".", "").replace("/", "").replace("-", ""))); // CPF/CNPJ do Sacado cpfCGC A X 14 - Informação do Cliente 11111111111
            params.add(new BasicNameValuePair("endereco", pessoa.getPessoaEndereco().getEndereco().getLogradouro().getDescricao() + " " + pessoa.getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao())); // Endereço do Sacado endereco A X 40 - Informação do Cliente Rua 15 de maio
            params.add(new BasicNameValuePair("bairro", pessoa.getPessoaEndereco().getEndereco().getBairro().getDescricao().isEmpty() ? "N/D" : pessoa.getPessoaEndereco().getEndereco().getBairro().getDescricao())); // Bairro do Sacado bairro A X 15 - Informação do Cliente Ponta Verde
            params.add(new BasicNameValuePair("cidade", pessoa.getPessoaEndereco().getEndereco().getCidade().getCidade())); // Cidade do Sacado cidade A X 15 - Informação do Cliente Brasília
            params.add(new BasicNameValuePair("cep", pessoa.getPessoaEndereco().getEndereco().getCep())); // CEP do Sacado cep A X 8 - Informação do Cliente 58108130
            params.add(new BasicNameValuePair("uf", pessoa.getPessoaEndereco().getEndereco().getCidade().getUf())); // UF do Sacado uf A X 2 - Informação do Cliente DF
            params.add(new BasicNameValuePair("codMunicipio", "29751")); // Código do Município do Sacado codMunicipio N X - Informação do Cliente 1009 // FIXO RIBEIRAO PRETO
            params.add(new BasicNameValuePair("chaveAcessoWeb", boleto.getContaCobranca().getChaveAcesso())); // Chave de Acesso chaveAcessoWeb A X 36 - Informação gerada pela Cooperativa DFFF3ADD-7880-4A28-8413-91EDD1DBE2E1

            //params.add(new BasicNameValuePair("codMunicipio", "29751")); // Código do Município do Sacado codMunicipio N X - Informação do Cliente 1009
            //params.add(new BasicNameValuePair("chaveAcessoWeb", "B049844D-C11D-4F5E-9D2F-87E0596304E6")); // Chave de Acesso chaveAcessoWeb A X 36 - Informação gerada pela Cooperativa DFFF3ADD-7880-4A28-8413-91EDD1DBE2E1
            if (boleto.getContaCobranca().getJurosMensal() > 0) {
                params.add(new BasicNameValuePair("percTaxaMora", "" + boleto.getContaCobranca().getJurosMensal()));
            }

            if (boleto.getContaCobranca().getMulta() > 0) {
                params.add(new BasicNameValuePair("percTaxaMulta", "" + boleto.getContaCobranca().getMulta())); // Percentual da Taxa de Multa
            }

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String REPRESENTACAO = "75691.44111";
                String result = EntityUtils.toString(entity);
                if (result.contains(REPRESENTACAO)) {
                    Integer index = 0;
                    String BOLETO_B = result;
                    //int pos1 = msg.indexOf("75691.44111", index);
                    int pos1 = BOLETO_B.indexOf(boleto.getContaCobranca().getContaBanco().getBanco().getNumero() + "91." + boleto.getContaCobranca().getContaBanco().getAgencia() + "1", index);
                    BOLETO_B = BOLETO_B.substring(pos1, pos1 + 58);
                    BOLETO_B = BOLETO_B.replace(" ", "").replace(".", "");

                    String nb = Integer.valueOf(BOLETO_B.subSequence(21, 28).toString()).toString();

                    boleto.setBoletoComposto(nb);
                    boleto.setNrBoleto(Integer.valueOf(nb));

                    boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
                    boleto.setDtStatusRetorno(DataHoje.dataHoje());
                    boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

                    new Dao().update(boleto, true);

                    for (Movimento m : lista_movimento) {
                        m.setDocumento(nb);
                        new Dao().update(m, true);
                    }

                    new Dao().executeQuery(
                            " INSERT INTO fin_movimento_boleto (id_movimento, id_boleto) \n "
                            + "( \n "
                            + "SELECT m.id, b.id \n "
                            + "  FROM fin_boleto AS b \n "
                            + " INNER JOIN fin_movimento AS m ON m.nr_ctr_boleto = b.nr_ctr_boleto \n "
                            + "  LEFT JOIN fin_movimento_boleto AS mb ON mb.id_boleto = b.id AND mb.id_movimento = m.id \n "
                            + " WHERE b.id = " + boleto.getId() + " \n "
                            + "   AND mb.id IS NULL \n "
                            + "   AND m.id_baixa IS NULL \n "
                            + " GROUP BY m.id, b.id \n "
                            + ");"
                    );

                    return new RespostaWebService(boleto, "");
                } else {

                    String noHTMLString = result.replaceAll("\\<.*?\\>", "");
                    noHTMLString = noHTMLString.replaceAll("\\/\\*([\\S\\s]+?)\\*\\/", "");
                    noHTMLString = noHTMLString.replaceAll("(?s)/\\*.*?\\*/", "");
                    noHTMLString = noHTMLString.replaceAll("<.*?>", "");
                    noHTMLString = noHTMLString.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "");
                    noHTMLString = noHTMLString.replaceAll("\\\\r", "");
                    noHTMLString = noHTMLString.replaceAll("\\\\n", "");
                    noHTMLString = noHTMLString.replaceAll("\\\\t", "");
                    String[] tokens = noHTMLString.split(" ");
                    noHTMLString = "";
                    for (String token : tokens) {
                        if (!token.trim().isEmpty()) {
                            if (!token.trim().equals("Erro")) {
                                if (!token.trim().contains("Entre em contato com o administrador do sistema.") && !token.trim().contains("Ocorreu um erro no sistema!")) {
                                    noHTMLString += token.trim() + " ";
                                }
                            }
                        }
                    }
                    noHTMLString = noHTMLString.replace("Entre em contato com o administrador do sistema.", "");
                    noHTMLString = noHTMLString.replace("Ocorreu um erro no sistema!", "");
                    noHTMLString = noHTMLString.trim();
                    if (result.contains("Ocorreu um erro no sistema")) {
                        if (result.contains("O campo [CPF/CNPJ] deve ter um valor válido")) {
                            msgErro = "Digite um CPF/CNPJ válido";
                        } else if (result.contains("O campo [Data de Vencimento] deve ter um valor válido e maior ou igual a [Data de Emissão]")) {
                            msgErro = "Data de Vencimento deve ser maior que ou igual a Data de Hoje";
                        } else if (result.contains("O campo [CEP] deve ter um valor válido")) {
                            msgErro = "Digite um CEP";
                        } else {
                            msgErro = noHTMLString;
                        }
                    } else if (noHTMLString.contains("404")) {
                        msgErro = noHTMLString;
                    } else {
                        msgErro = "Ocorreu um erro no sistema";
                    }
                }
                return new RespostaWebService(null, "Erro[2] ao Registrar Boleto " + boleto.getNrBoleto() + "[" + msgErro + "], contate o Administrador.");
            }

        } catch (IOException | UnsupportedOperationException e) {
            return new RespostaWebService(null, e.getMessage());
        }

        return new RespostaWebService(null, "Não existe configuração de WEB SERVICE para esta conta");
    }
}
