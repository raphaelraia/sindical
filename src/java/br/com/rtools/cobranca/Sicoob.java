package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
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

public class Sicoob extends Cobranca {

    public Sicoob(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public Sicoob(List<Movimento> listaMovimento) {
        super(listaMovimento);
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
        if (boleto.getContaCobranca().isCobrancaRegistrada()) {
            fimCodigoBarras += "01";        // modalidade -- 01 com registro no banco // -- 01 sem registro no banco
        } else {
            fimCodigoBarras += "02";        // modalidade -- 02 com registro no banco // -- 02 sem registro no banco
        }

        String cedente = "0000000".substring(0, 7 - boleto.getContaCobranca().getCodCedente().length()) + boleto.getContaCobranca().getCodCedente();        // codigo cedente
        fimCodigoBarras += cedente;

        String nossoNumero = "";
        if (boleto.getContaCobranca().isCobrancaRegistrada() && boleto.getDtCobrancaRegistrada() != null) {
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
        if (boleto.getContaCobranca().isCobrancaRegistrada() && boleto.getDtCobrancaRegistrada() != null) {
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
    public File gerarRemessa240() {
        PessoaEnderecoDao ped = new PessoaEnderecoDao();
        MovimentoDao dbmov = new MovimentoDao();

        Dao dao = new Dao();
        dao.openTransaction();

        Remessa remessa = new Remessa(-1, "", DataHoje.dataHoje(), DataHoje.horaMinuto(), null, Usuario.getUsuario(), null);
        if (!dao.save(remessa)) {
            dao.rollback();
            return null;
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
            return null;
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

            if (listaMovimento.isEmpty()) {
                return null;
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

            Boleto boleto_rem = dbmov.pesquisaBoletos(listaMovimento.get(0).getNrCtrBoleto());
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

            // PAREI AQUI
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

            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            Double valor_total_lote = (double) 0;

            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Integer sequencial_registro_lote = 1;
            for (Integer i = 0; i < listaMovimento.size(); i++) {
                Movimento mov = listaMovimento.get(i);
                // tipo 3 - segmento P -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "756"; // 01.3P Código do Banco na Compensação: "756"
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3P "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
                CONTEUDO_REMESSA += "3"; // 03.3P Tipo de Registro: "3"
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3P Nº Sequencial do Registro no Lote: Número adotado para identificar a sequência de registros encaminhados no lote. Preencher com '00001' para o primeiro segmento P do lote do arquivo. Para os demais: número do segmento anterior acrescido de 1.
                CONTEUDO_REMESSA += "P"; // 05.3P Cód. Segmento do Registro Detalhe: "P"
                CONTEUDO_REMESSA += " "; // 06.3P Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                CONTEUDO_REMESSA += "01"; // 07.3P "Código de Movimento Remessa: '01' = Entrada de Títulos '09' = Protestar '10' = Desistência do Protesto e Baixar Título '11' = Desistência do Protesto e manter em carteira '31' = Alterações de outros dados"
                CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.3P Prefixo da Cooperativa: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.3P Dígito Verificador do Prefixo: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += "000000000000".substring(0, 12 - conta.length()) + conta; // 10.3P Conta Corrente: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(conta)); // 11.3P Dígito Verificador da Conta: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += " ";// moduloOnze("" + Integer.valueOf(codigo_cedente)); // 12.3P Dígito Verificador da Ag/Conta: Preencher com espaços em branco
                // 14 cobraça registrada // JÁ ESTA NO NÚMERO DO DOCUMENTO EM MOVIMENTO
                //CONTEUDO_REMESSA += "14"; // 13.3P Carteira/Nosso Número Modalidade da Carteira 41 42 9(002) Ver Nota Explicativa G069 *G069
                CONTEUDO_REMESSA += (mov.getDocumento() + "                    ").substring(0, 20); // 13.3P Nosso Número
                CONTEUDO_REMESSA += "1"; // 14.3P Código da Carteira: vide planilha "Contracapa" deste arquivo
                CONTEUDO_REMESSA += "0"; // 15.3P Forma de Cadastr. do Título no Banco: "0"
                CONTEUDO_REMESSA += " "; // 16.3P Tipo de Documento: Preencher com espaços em branco
                CONTEUDO_REMESSA += "2"; // 17.3P "Identificação da Emissão do Boleto: (vide planilha ""Contracapa"" deste arquivo) '1'  =  Sicoob Emite '2'  =  Beneficiário Emite"
                CONTEUDO_REMESSA += "2"; // 18.3P "Identificação da Distribuição do Boleto: (vide planilha ""Contracapa"" deste arquivo) '1'  =  Sicoob Distribui '2'  =  Beneficiário Distribui"

                CONTEUDO_REMESSA += "               ".substring(0, 15 - ("" + mov.getId()).length()) + mov.getId(); // 19.3P Número do Documento de Cobrança: Número adotado e controlado pelo Cliente, para identificar o título de cobrança. Informação utilizada pelo Sicoob para referenciar a identificação do documento objeto de cobrança. Poderá conter número de duplicata, no caso de cobrança de duplicatas; número da apólice, no caso de cobrança de seguros, etc.

                CONTEUDO_REMESSA += mov.getVencimento().replace("/", ""); // 20.3P Data de Vencimento do Título

                String valor_titulo = mov.getValorString().replace(".", "").replace(",", "");
                // NO MANUAL FALA 13 PORÉM TEM QUE SER 15, ACHO QUE POR CAUSA DAS DECIMAIS ,00 (O MANUAL NÃO EXPLICA ISSO)
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - valor_titulo.length()) + valor_titulo; // 21.3P Valor Nominal do Título
                CONTEUDO_REMESSA += "00000"; // 22.3P Agência Encarregada da Cobrança: "00000"
                CONTEUDO_REMESSA += " "; // 23.3P Dígito Verificador da Agência: Preencher com espaços em branco
                CONTEUDO_REMESSA += "02";// 24.3P Espécie de Título

                String aceite = boleto_rem.getContaCobranca().getAceite().equals("N") ? boleto_rem.getContaCobranca().getAceite() : "A";
                CONTEUDO_REMESSA += aceite; // 25.3P "Identific. de Título Aceito/Não Aceito: Código adotado pela FEBRABAN para identificar se o título de cobrança foi aceito (reconhecimento da dívida pelo Pagador). 'A'  =  Aceite 'N'  =  Não Aceite"
                CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 26.3P Data da Emissão do Título
                CONTEUDO_REMESSA += "0"; // 27.3P "Código do Juros de Mora: '0'  =  Isento '1'  =  Valor por Dia '2'  =  Taxa Mensal"
                CONTEUDO_REMESSA += "00000000"; // 28.3P Data do Juros de Mora: preencher com a Data de Vencimento do Título formato DDMMAAAA
                CONTEUDO_REMESSA += "000000000000000"; // 29.3P Juros Mora
                CONTEUDO_REMESSA += "0"; // 30.3P "Código do Desconto 1 '0'  =  Não Conceder desconto '1'  =  Valor Fixo Até a Data Informada '2'  =  Percentual Até a Data Informada"

                CONTEUDO_REMESSA += "00000000"; // 31.3P Data do Desconto 1
                CONTEUDO_REMESSA += "000000000000000"; // 32.3P Valor/Percentual a ser Concedido
                CONTEUDO_REMESSA += "000000000000000"; // 33.3P Valor do IOF a ser Recolhido
                CONTEUDO_REMESSA += "000000000000000"; // 34.3P Valor do Abatimento
                CONTEUDO_REMESSA += "                         ".substring(0, 25 - ("" + mov.getId()).length()) + mov.getId(); // 35.3P Identificação do Título na Empresa: Campo destinado para uso do Beneficiário para identificação do Título.
                CONTEUDO_REMESSA += "3"; // 36.3P "'1' = Protestar dias corridos '2' = Protestar dias úteis '3' = Não Protestar '9' = Cancelar Instrução de Protesto 
                // O código '9' deverá ser utilizado para cancelar um agendamento futuro de protesto e deverá estar atrelado obrigatóriamente ao código de entrada '31'."

                CONTEUDO_REMESSA += "00"; // 37.3P "Informar prazo de inicio do protesto a partir do vencimento '0' - Não protestar"
                CONTEUDO_REMESSA += "0"; // 38.3P Código para Baixa/Devolução: "0"
                CONTEUDO_REMESSA += "   "; // 39.3P Número de Dias para Baixa/Devolução: Preencher com espaços em branco
                CONTEUDO_REMESSA += "09"; // 40.3P "Código da Moeda: '02'  =  Dólar Americano Comercial (Venda) '09'  = Real"
                CONTEUDO_REMESSA += "0000000000"; // 41.3P Nº do Contrato da Operação de Créd.: "0000000000"
                CONTEUDO_REMESSA += " "; // 42.3P Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                // tipo 3 - segmento Q -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "756"; // 01.3Q Código do Banco na Compensação: "756"
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3Q "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
                CONTEUDO_REMESSA += "3"; // 03.3Q Tipo de Registro: "3"

                sequencial_registro_lote++;
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3Q "Nº Sequencial do Registro no Lote: Número adotado para identificar a sequência de registros encaminhados no lote. Preencher com '00001' para o primeiro segmento P do lote do arquivo. Para os demais: número do segmento anterior acrescido de 1. Ex: Se segmento anterior P = ""00001"". Então, segmento Q = ""00002"" e assim consecutivamente."
                CONTEUDO_REMESSA += "Q"; // 05.3Q Cód. Segmento do Registro Detalhe: "Q"
                CONTEUDO_REMESSA += " "; // 06.3Q Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
                CONTEUDO_REMESSA += "01"; // 07.3Q "Código de Movimento Remessa: '01'  =  Entrada de Títulos"

                // 08.3Q "Tipo de Inscrição Pagador: '1'  =  CPF '2'  =  CGC / CNPJ"
                if (mov.getPessoa().getTipoDocumento().getId() == 1) { // CPF
                    CONTEUDO_REMESSA += "1"; // 08.3Q 
                } else if (mov.getPessoa().getTipoDocumento().getId() == 2) { // CNPJ
                    CONTEUDO_REMESSA += "2"; // 08.3Q 
                }

                String documento_pessoa = mov.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_pessoa.length()) + documento_pessoa; // 09.3Q Número de Inscrição

                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((mov.getPessoa().getNome() + "                                        ").substring(0, 40)); // 10.3Q Nome

                PessoaEndereco pessoa_endereco = ped.pesquisaEndPorPessoaTipo(mov.getPessoa().getId(), 3);
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

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");
                CONTEUDO_REMESSA = "";

                sequencial_registro_lote++;

                valor_total_lote = Moeda.soma(valor_total_lote, mov.getValor());

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, mov);

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return null;
                }

                list_log.add("ID: " + mov.getId());
                list_log.add("Valor: " + mov.getValorString());
                list_log.add("-----------------------");
            }

            // rodapé(footer) do lote ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "756"; // 01.5 Código do Banco na Compensação: "756"
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.5 "Lote de Serviço: Número seqüencial para identificar univocamente um lote de serviço. Criado e controlado pelo responsável pela geração magnética dos dados contidos no arquivo. Preencher com '0001' para o primeiro lote do arquivo. Para os demais: número do lote anterior acrescido de 1. O número não poderá ser repetido dentro do arquivo."
            CONTEUDO_REMESSA += "5"; // 03.5 Tipo de Registro: "5"
            CONTEUDO_REMESSA += "         "; // 04.5 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            Integer quantidade_lote = (3 * listaMovimento.size()) + 2;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_lote).length()) + ("" + quantidade_lote); // 05.5 Quantidade de Registros no Lote
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + listaMovimento.size()).length()) + ("" + listaMovimento.size()); // 06.5 Quantidade de Títulos em Cobrança
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

            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            // rodapé(footer) do arquivo ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "756"; // 01.9 Código do Banco na Compensação: "756"
            CONTEUDO_REMESSA += "9999"; // 02.9 Preencher com '9999'
            CONTEUDO_REMESSA += "9"; // 03.9 Tipo de Registro: "9"
            CONTEUDO_REMESSA += "         "; // 04.9 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco
            CONTEUDO_REMESSA += "000001"; // 05.9 Quantidade de Lotes do Arquivo

            Integer quantidade_registros = (2 * listaMovimento.size()) + 4;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_registros).length()) + ("" + quantidade_registros); // 06.9 Quantidade de Registros do Arquivo
            CONTEUDO_REMESSA += "000000"; // 07.9 Qtde de Contas p/ Conc. (Lotes): "000000"
            CONTEUDO_REMESSA += "                                                                                                                                                                                                             "; // 08.9 Uso Exclusivo FEBRABAN/CNAB: Preencher com espaços em branco

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

            return new File(destino);
        } catch (IOException | NumberFormatException e) {
            e.getMessage();
            return null;
        }
    }
    
    @Override
    public File gerarRemessa400() {
        return null;
    }
}
