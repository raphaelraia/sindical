package br.com.rtools.cobranca;

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
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.dao.FunctionsDao;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.ServletContext;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Santander extends Cobranca {

    private final Boolean TESTE = GenericaSessao.getBoolean("debug");

    public Santander(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public Santander(List<BoletoRemessa> listaBoletoRemessa) {
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

        if (((10 - (soma % 10)) == 10) || ((soma % 10) == 0)) {
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
        int resto = (soma % 11);

        if (resto == 10) {
            return "1";
        } else if (resto == 1 || resto == 0) {
            return "0";
        } else {
            return Integer.toString(11 - resto);
        }
    }

    @Override
    public String moduloOnzeDV(String composicao) {
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
        int resto = (soma % 11);

        if (resto == 10 || resto == 1 || resto == 0) {
            return "1";
        } else {
            return Integer.toString(11 - resto);
        }
    }

    @Override
    public String codigoBarras() {
        String iniCodigoBarras = "", fimCodigoBarras = "";
        iniCodigoBarras = boleto.getContaCobranca().getContaBanco().getBanco().getNumero() + boleto.getContaCobranca().getMoeda(); // banco + moeda

        fimCodigoBarras += fatorVencimento(vencimento);   // fator de vencimento

        int tam = Moeda.limparPonto(Moeda.converteR$Double(valor)).length();

        fimCodigoBarras += "0000000000".substring(0, 10 - tam) + Moeda.limparPonto(Moeda.converteR$Double(valor)); // valor

        fimCodigoBarras += "9";

        String cedente = "0000000".substring(0, 7 - boleto.getContaCobranca().getCodCedente().length()) + boleto.getContaCobranca().getCodCedente();        // codigo cedente
        fimCodigoBarras += cedente;

        String nossoNumero = boleto.getBoletoComposto() + this.moduloOnze(boleto.getBoletoComposto());//boleto.getBoletoComposto() + calculoConstante();
        fimCodigoBarras += "0000000000000".substring(0, 13 - nossoNumero.length()) + nossoNumero;       // nosso numero

        fimCodigoBarras += "0";       // IOS -- [ 0 demais clientes ] -- [ 7 - 7% ] -- limitado a [ 9% - 9 ]
        fimCodigoBarras += "101";

        return iniCodigoBarras + this.moduloOnzeDV(iniCodigoBarras + fimCodigoBarras) + fimCodigoBarras;
    }

    @Override
    public String representacao() {
        String codigoBarras = this.codigoBarras();
        // PRIMEIRO GRUPO --
        String primeiro_grupo = codigoBarras.substring(0, 4);
        primeiro_grupo += codigoBarras.substring(19, 24);
        primeiro_grupo += moduloDez(primeiro_grupo);

        // SEGUNDO GRUPO --
        String segundo_grupo = codigoBarras.substring(24, 27);
        String nossoNumero = boleto.getBoletoComposto() + this.moduloOnze(boleto.getBoletoComposto());
        nossoNumero = "0000000000000".substring(0, 13 - nossoNumero.length()) + nossoNumero;
        segundo_grupo += nossoNumero.substring(0, 7);
        segundo_grupo += moduloDez(segundo_grupo);

        // TERCEIRO GRUPO --
        String terceiro_grupo = nossoNumero.substring(7, 13);
        terceiro_grupo += "0"; // IOS -- [ 0 demais clientes ] -- [ 7 - 7% ] -- limitado a [ 9% - 9 ]
        terceiro_grupo += "101";
        terceiro_grupo += moduloDez(terceiro_grupo);

        // QUARTO GRUPO
        String quarto_grupo = codigoBarras.substring(4, 5);

        // QUINTO GRUPO --
        String quinto_grupo = codigoBarras.substring(5, 19);

        String repNumerica = primeiro_grupo + segundo_grupo + terceiro_grupo + quarto_grupo + quinto_grupo;
        repNumerica = repNumerica.substring(0, 5) + "."
                + repNumerica.substring(5, 10) + " "
                + repNumerica.substring(10, 15) + "."
                + repNumerica.substring(15, 21) + " "
                + repNumerica.substring(21, 26) + "."
                + repNumerica.substring(26, 32) + " "
                + repNumerica.substring(32, 33) + " "
                + repNumerica.substring(33, repNumerica.length());
        return repNumerica;
    }

    @Override
    public String getNossoNumeroFormatado() {
        return boleto.getBoletoComposto() + "-" + this.moduloOnze(boleto.getBoletoComposto());
    }

    @Override
    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente();
    }

    @Override
    public String getAgenciaFormatada() {
        return boleto.getContaCobranca().getContaBanco().getAgencia() + "-" + this.moduloDez(boleto.getContaCobranca().getContaBanco().getAgencia());
    }

    @Override
    public String codigoBanco() {
        return "033-7";
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

        String nome_arquivo = "SAN_240_" + DataHoje.hora().replace(":", "") + ".txt";

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

            CONTEUDO_REMESSA += "033"; // 01.0 Código do Banco na Compensação '033'
            CONTEUDO_REMESSA += "0000"; // 02.0 Lote de Serviço
            CONTEUDO_REMESSA += "0"; // 03.0 Tipo de Registro
            CONTEUDO_REMESSA += "         "; // 04.0 Uso Exclusivo FEBRABAN / CNAB
            CONTEUDO_REMESSA += "2"; // 05.0 Tipo de Inscrição da Empresa
            CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // 06.0 Número de Inscrição da Empresa

            Boleto boleto_rem = listaBoletoRemessa.get(0).getBoleto();
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String cedente = boleto_rem.getContaCobranca().getCedente();
            String codigo_cedente = boleto_rem.getContaCobranca().getCodCedente();

            CONTEUDO_REMESSA += "000000000000000"; // 07.0 Convênio Código de transmissão ( NÃO ENTENDI )

            CONTEUDO_REMESSA += "     "; // 08.0 Uso Exclusivo FEBRABAN / CNAB

            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 09.0 Agência Mantenedora da Conta

            CONTEUDO_REMESSA += " "; // 10.0 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += "000000000000".substring(0, 12 - codigo_cedente.length()) + codigo_cedente; // 11.0 Código do Convênio Santander
            CONTEUDO_REMESSA += " "; // 12.0 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += " "; // 13.0 Uso Exclusivo do Banco

            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 14.0 Nome da Empresa
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper(("BANCO REAL                    ").substring(0, 30)); // 15.0 Nome do Banco
            CONTEUDO_REMESSA += "          "; // 16.0 Uso Exclusivo FEBRABAN / CNAB
            CONTEUDO_REMESSA += "1"; // 17.0 Código Remessa / Retorno
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 18.0 Data de Geração do Arquivo
            CONTEUDO_REMESSA += DataHoje.hora().replace(":", ""); // 19.0 Hora de Geração do Arquivo

            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 20.0 Número Seqüencial do Arquivo
            CONTEUDO_REMESSA += "030"; // 21.0 Nº da Versão do Layout do Arquivo
            CONTEUDO_REMESSA += "00000"; // 22.0 Densidade de Gravação do Arquivo
            CONTEUDO_REMESSA += "                    "; // 23.0 Para Uso Reservado do Banco
            CONTEUDO_REMESSA += "                    "; // 24.0 Para Uso Reservado da Empresa
            CONTEUDO_REMESSA += "            "; // 25.0 Uso Exclusivo FEBRABAN / CNAB
            CONTEUDO_REMESSA += " "; // 26.0 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += "    "; // 27.0 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += "  "; // 28.0 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += "          "; // 29.0 Cód. Ocor. Cobrança sem Papel

            // -----------------------------------------------------------------
            // -----------------------------------------------------------------
            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Header de Arquivo menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");
            CONTEUDO_REMESSA = "";

            Integer sequencial_lote = 1;

            // header do lote ------------------------------------------------------------
            // ---------------------------------------------------------------------------
            CONTEUDO_REMESSA += "033"; // 01.1 Código do Banco na Compensação
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.1 Lote de Serviço
            CONTEUDO_REMESSA += "1"; // 03.1 Tipo de Registro
            CONTEUDO_REMESSA += "R"; // 04.1 Tipo de Operação
            CONTEUDO_REMESSA += "01"; // 05.1 Tipo de Serviço

            CONTEUDO_REMESSA += "  "; // 06.1 Uso Exclusivo FEBRABAN / CNAB
            CONTEUDO_REMESSA += "020"; // 07.1 Nº da Versão do Layout do Lote
            CONTEUDO_REMESSA += " "; // 08.1 Uso Exclusivo FEBRABAN / CNAB
            CONTEUDO_REMESSA += "2"; // 09.1 Tipo de Inscrição da Empresa
            CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_sindicato.length()) + documento_sindicato; // 10.1 Nº de Inscrição da Empresa

            CONTEUDO_REMESSA += "                    "; // 11.1 Uso Exclusivo do Banco

            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 12.1 Agência Mantenedora da Conta 54585- Numérico  G008
            CONTEUDO_REMESSA += " "; // 13.1 Uso Exclusivo FEBRABAN / CNAB

            CONTEUDO_REMESSA += "000000000000".substring(0, 12 - codigo_cedente.length()) + codigo_cedente; // 14.0 Código do Convênio Santander

            CONTEUDO_REMESSA += " "; // 15.1 Uso Exclusivo FEBRABAN / CNAB
            CONTEUDO_REMESSA += " "; // 16.1 Uso Exclusivo FEBRABAN / CNAB

            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 17.1 Nome da Empresa 7410330- Alfanumérico  G013
            CONTEUDO_REMESSA += "                                        "; // 18.1 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += "                                        "; // 19.1 Uso Exclusivo do Banco
            CONTEUDO_REMESSA += "00000000".substring(0, 8 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 20.1 Número Remessa/Retorno
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 21.1 Data de Gravação Remessa/Retorno
            CONTEUDO_REMESSA += "00000000"; // 22.1 Data do Crédito
            CONTEUDO_REMESSA += "                                 "; // 23.1 Uso Exclusivo FEBRABAN / CNAB

            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Header do Lote menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            Double valor_total_lote = (double) 0;
            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Integer sequencial_registro_lote = 1;
            for (Integer i = 0; i < listaBoletoRemessa.size(); i++) {
                Boleto bol = listaBoletoRemessa.get(i).getBoleto();
                StatusRemessa sr = listaBoletoRemessa.get(i).getStatusRemessa();

                // tipo 3 - segmento P -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "033"; // 01.3P Código do Banco na Compensação
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3P Lote Lote de Serviço
                CONTEUDO_REMESSA += "3"; // 03.3P Tipo de Registro
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3P Nº Sequencial do Registro no Lote

                CONTEUDO_REMESSA += "P"; // 05.3P Cód. Segmento do Registro Detalhe
                CONTEUDO_REMESSA += " "; // 06.3P Uso Exclusivo FEBRABAN / CNAB
                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 07.3P Código de Movimento Remessa // REGISTRAR
                } else {
                    CONTEUDO_REMESSA += "02"; // 07.3P Código de Movimento Remessa // BAIXAR
                }
                CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.3P Agência Mantenedora da Conta
                CONTEUDO_REMESSA += " "; // 09.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "000000000000".substring(0, 12 - codigo_cedente.length()) + codigo_cedente; // 10.3P Número da Conta Corrente
                CONTEUDO_REMESSA += "  "; // 11.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "0000000"; // 12.3P Nosso número

                CONTEUDO_REMESSA += "000000000"; // 13.3P Uso Exclusivo do Banco
                // Seguradoras Registrada
                CONTEUDO_REMESSA += "0000000"; // 14.3P 
                CONTEUDO_REMESSA += "000000000"; // 14.3P 
                CONTEUDO_REMESSA += "000000000"; // 14.3P 
                CONTEUDO_REMESSA += "00000"; // 14.3P 
                // fim Seguradoras Registrada

                CONTEUDO_REMESSA += "00000"; // 15.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "2"; // 16.3P  Emissão Boleto Identificação da Emissão do Boleto ‘1’ Banco emite ‘2’ Cliente emite 

                CONTEUDO_REMESSA += "2"; // 17.3P Distribuição do Boleto Identificação da distribuição
                CONTEUDO_REMESSA += "               ".substring(0, 15 - ("" + bol.getBoletoComposto()).length()) + bol.getBoletoComposto(); // 18.3P Seu número
                CONTEUDO_REMESSA += bol.getVencimento().replace("/", ""); // 19.3P Data de Vencimento do Título

                String valor_titulo;
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

                // FIXAR VALOR 1,00 CASO FOR MENOR QUE 1,00
                //if (mov.getValor() < 1) {
                //    valor_titulo = "100";
                //} else {
                valor_titulo = Moeda.converteDoubleToString(valor_titulo_double).replace(".", "").replace(",", "");
                //}
                // NO MANUAL FALA 13 PORÉM TEM QUE SER 15, ACHO QUE POR CAUSA DAS DECIMAIS ,00 (O MANUAL NÃO EXPLICA ISSO)
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - valor_titulo.length()) + valor_titulo; // 20.3P Valor Nominal do Título
                CONTEUDO_REMESSA += "00000"; // 21.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += " "; // 22.3P Uso Exclusivo do Banco

                CONTEUDO_REMESSA += "02";// 23.3P Espécie do Título

                String aceite = boleto_rem.getContaCobranca().getAceite().equals("N") ? boleto_rem.getContaCobranca().getAceite() : "A";
                CONTEUDO_REMESSA += aceite; // 24.3P Identific. de Título Aceito/Não Aceito
                CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 25.3P Data da Emissão do Título
                CONTEUDO_REMESSA += "3"; // 26.3P Código do Juros de Mora
                CONTEUDO_REMESSA += "00000000"; // 27.3P Data do Juros de Mora
                CONTEUDO_REMESSA += "000000000000000"; // 28.3P Juros de Mora por Dia/Taxa
                CONTEUDO_REMESSA += "0"; // 29.3P Código do Desconto 1
                CONTEUDO_REMESSA += "00000000"; // 30.3P Data do Desconto 1
                CONTEUDO_REMESSA += "000000000000000"; // 31.3P Valor/Percentual a ser Concedido
                CONTEUDO_REMESSA += "000000000000000"; // 32.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "000000000000000"; // 33.3P Valor do Abatimento
                CONTEUDO_REMESSA += "                         ".substring(0, 25 - ("" + bol.getId()).length()) + bol.getId(); // 34.3P Identificação do Título na Empresa 19622025-  Alfanumérico  G072
                CONTEUDO_REMESSA += "3"; // 35.3P Código para Protesto
                CONTEUDO_REMESSA += "00"; // 36.3P Número de Dias para Protesto
                CONTEUDO_REMESSA += "0"; // 37.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "000"; // 38.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "09"; // 39.3P Código da Moeda
                CONTEUDO_REMESSA += "0000000000"; // 40.3P Uso Exclusivo do Banco
                CONTEUDO_REMESSA += " "; // 41.3P Uso Exclusivo FEBRABAN/CNAB

                if (CONTEUDO_REMESSA.length() != 240) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Segmento P menor que 240: " + CONTEUDO_REMESSA);
                }
                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                // tipo 3 - segmento Q -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "033"; // 01.3Q Código do Banco na Compensação
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3Q Lote de Serviço
                CONTEUDO_REMESSA += "3"; // 03.3Q Tipo de Registro

                sequencial_registro_lote++;
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3Q Nº Sequencial do Registro no Lote
                CONTEUDO_REMESSA += "Q"; // 05.3Q Cód. Segmento do Registro Detalhe
                CONTEUDO_REMESSA += " "; // 06.3Q Uso Exclusivo FEBRABAN/CNAB
                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 07.3Q Código de Movimento Remessa // REGISTRAR
                } else {
                    CONTEUDO_REMESSA += "02"; // 07.3Q Código de Movimento Remessa // BAIXAR
                }
                Pessoa pessoa = bol.getPessoa();

                if (pessoa.getTipoDocumento().getId() == 1) { // CPF
                    CONTEUDO_REMESSA += "1"; // 08.3Q Tipo de Inscrição
                } else if (pessoa.getTipoDocumento().getId() == 2) { // CNPJ
                    CONTEUDO_REMESSA += "2"; // 08.3Q Tipo de Inscrição
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
                    CONTEUDO_REMESSA += cep.substring(0, 5); // 13.3Q CEP do Pagador
                    CONTEUDO_REMESSA += cep.substring(5, 8); // 14.3Q Sufixo do CEP do Pagador
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_cidade + "               ").substring(0, 15)); // 15.3Q Cidade do Pagador
                    CONTEUDO_REMESSA += end_uf; // 16.3Q Unidade da Federação do Pagador
                } else {
                    CONTEUDO_REMESSA += "                                        "; // 11.3Q Endereço do Pagador
                    CONTEUDO_REMESSA += "               "; // 12.3Q Bairro do Pagador
                    CONTEUDO_REMESSA += "     "; // 13.3Q CEP do Pagador
                    CONTEUDO_REMESSA += "   "; // 14.3Q Sufixo do CEP do Pagador
                    CONTEUDO_REMESSA += "               "; // 15.3Q Cidade do Pagador
                    CONTEUDO_REMESSA += "  "; // 16.3Q Unidade da Federação do Pagador
                }

                CONTEUDO_REMESSA += "0"; // 17.3Q Tipo de Inscrição
                CONTEUDO_REMESSA += "000000000000000"; // 18.3Q Número de Inscrição
                CONTEUDO_REMESSA += "                                        "; // 19.3Q Nome do Sacador/Avalista 170 20940- Alfanumérico 
                CONTEUDO_REMESSA += "000"; // 20.3Q Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "                    "; // 21.3Q Uso Exclusivo do Banco
                CONTEUDO_REMESSA += "        "; // 22.3Q Uso Exclusivo FEBRABAN/CNAB

                if (CONTEUDO_REMESSA.length() != 240) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Segmento Q menor que 240: " + CONTEUDO_REMESSA);
                }

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                sequencial_registro_lote++;

                Double valor_l;

                if (valor_titulo_double < 1) {
                    valor_l = new Double(1);
                } else {
                    valor_l = valor_titulo_double;
                }

                valor_total_lote = Moeda.soma(valor_total_lote, valor_l);

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, bol, listaBoletoRemessa.get(i).getStatusRemessa());

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Erro ao salvar Remessa Banco");
                }

                list_log.add("ID: " + bol.getId());
                list_log.add("Valor: " + Moeda.converteDoubleToString(valor_l));
                list_log.add("-----------------------");
            }

            // rodapé(footer) do lote ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "033"; // 01.5 Código do Banco na Compensação
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.5 Lote de Serviço
            CONTEUDO_REMESSA += "5"; // 03.5 Tipo de Registro
            CONTEUDO_REMESSA += "         "; // 04.5 Uso Exclusivo FEBRABAN/CNAB
            Integer quantidade_lote = (2 * listaBoletoRemessa.size()) + 2;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_lote).length()) + ("" + quantidade_lote); // 05.5 Quantidade de Registros do Lote
            CONTEUDO_REMESSA += "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"; // 06.5 Uso exclusivo do Banco
            CONTEUDO_REMESSA += "        "; // 14.5 N. do Aviso Número do Aviso de Lançamento
            CONTEUDO_REMESSA += "                                                                                                                     "; // 15.5 Uso Exclusivo FEBRABAN/CNAB
            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Rodapé do Lote menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            // rodapé(footer) do arquivo ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "033"; // 01.9 Código do Banco na Compensação
            CONTEUDO_REMESSA += "9999"; // 02.9 Lote de Serviço
            CONTEUDO_REMESSA += "9"; // 03.9 Tipo de Registro
            CONTEUDO_REMESSA += "         "; // 04.9 Uso Exclusivo FEBRABAN/CNAB

            CONTEUDO_REMESSA += "000001"; // 05.9 Quantidade de Lotes do Arquivo

            Integer quantidade_registros = (2 * listaBoletoRemessa.size()) + 4;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_registros).length()) + ("" + quantidade_registros); // 06.9 Quantidade de Registros do Arquivo

            CONTEUDO_REMESSA += "000000"; // 07.9 Qtde de Contas p/ Conc. (Lotes)

            CONTEUDO_REMESSA += "                                                                                                                                                                                                             "; // 08.9 Uso Exclusivo FEBRABAN/CNAB
            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Rodapé do Arquivo menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            buff_writer.flush();
            buff_writer.close();

            dao.commit();
            //dao.rollback();

            String log_string = "";
            log_string = list_log.stream().map((string_x) -> string_x + " \n").reduce(log_string, String::concat);
            NovoLog log = new NovoLog();
            log.save(
                    log_string
            );

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

    @Override
    public RespostaWebService registrarBoleto(String vencimentoRegistro) {
        // CASO QUEIRA TESTAR A ROTINA DE REGISTRO SEM REGISTRAR COLOCAR TESTE = TRUE
        if (TESTE) {
            Dao dao = new Dao();

            boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
            boleto.setDtStatusRetorno(DataHoje.dataHoje());
            boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

            dao.update(boleto, true);
            return new RespostaWebService(boleto, "");
        }
        /*
        
        ATENÇÃO ----------------------------------------------------------------
        ATENÇÃO ----------------------------------------------------------------
        ATENÇÃO ----------------------------------------------------------------
        
        A ÚLTIMA VERSÃO DO JAVA QUE FUNCIONA É A JDK 1.8 (v51)
        
        JÁ TESTEI VERSÕES MAIS RECENTES ATÉ A DATA 28/03/2018 E NENHUMA DEU CERTO
        
        VERSÕES ANTERIORES TAMBÉM FORAM TESTADAS E NÃO FUNCIONARAM
        
        ATENÇÃO ----------------------------------------------------------------
        ATENÇÃO ----------------------------------------------------------------
        ATENÇÃO ----------------------------------------------------------------
        
         */

        try {
            //File flCert = new File("C:/PC201707105759.pfx");
            File flCert = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/conf/PC201707105759.pfx"));
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(flCert.getAbsolutePath()), "sisrtools989899".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, "sisrtools989899".toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(clientStore);
            TrustManager[] tms = tmf.getTrustManagers();

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kms, null, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Definir a URL Do Serviço sem a ?WSDL no fim
            URL url = new URL("https://ymbdlb.santander.com.br/dl-ticket-services/TicketEndpointService?wsdl");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

//            // Define que a Conexão terá uma saída/retorno                        
            conn.setDoInput(true);
            conn.setDoOutput(true);
//
//            // Método a ser Consumido pela requisição
//            //conn.setRequestProperty("SOAPAction","http://localhost:8080/WsServidor/retornarString");
//            // Propriedades da Mensagem SOAP
            conn.setRequestProperty("Type", "Request-Response");
            conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            conn.setRequestProperty("User-Agent", "Jakarta Commons-HttpClient/3.1");

            // Canal de Saída da Requisição
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            // Mensagem no Formato SOAP
            //String xml = TICKET_SEG_CONSULTA();
            Pessoa pessoa = boleto.getPessoa();
            String td = "";
            if (pessoa.getTipoDocumento().getId() == 1) { // CPF
                td = "01"; // CPF
            } else if (pessoa.getTipoDocumento().getId() == 2) { // CNPJ
                td = "02"; // CNPJ
            }

            String endereco = pessoa.getPessoaEndereco().getEndereco().getLogradouro().getDescricao() + " " + pessoa.getPessoaEndereco().getEndereco().getDescricaoEndereco().getDescricao();
            String bairro = pessoa.getPessoaEndereco().getEndereco().getBairro().getDescricao();
            String cidade = pessoa.getPessoaEndereco().getEndereco().getCidade().getCidade();
            String uf = pessoa.getPessoaEndereco().getEndereco().getCidade().getUf();
            String cep = pessoa.getPessoaEndereco().getEndereco().getCep().replace(".", "").replace("-", "");

            String valor_titulo = Moeda.converteDoubleToString(valor).replace(".", "").replace(",", "");
            valor_titulo = "000000000000000".substring(0, 15 - valor_titulo.length()) + valor_titulo;

            String pagador_nome = AnaliseString.normalizeSpecial(AnaliseString.normalizeUpper(pessoa.getNome()));
            String pagador_endereco = AnaliseString.normalizeSpecial(AnaliseString.normalizeUpper(endereco));
            String pagador_bairro = AnaliseString.normalizeSpecial(AnaliseString.normalizeUpper(bairro));

            pagador_nome = (pagador_nome + "                                        ").substring(0, 40).trim();
            pagador_endereco = (pagador_endereco + "                                        ").substring(0, 40).trim();

            String xml = TICKET_SEG_ENTRADA_TITULO(
                    boleto.getContaCobranca().getCodCedente(),
                    td,
                    pessoa.getDocumento().replace(".", "").replace("-", "").replace("/", ""),
                    pagador_nome,
                    pagador_endereco,
                    pagador_bairro,
                    AnaliseString.normalizeUpper(cidade),
                    uf,
                    cep,
                    getNossoNumeroFormatado().replace("-", ""),
                    DataHoje.converteData(vencimento).replace("/", ""),
                    DataHoje.data().replace("/", ""),
                    "02", // CÓDIGO PARA 'DM'(duplicata mercantil)) NO MANUAL SANTANDER //boleto.getContaCobranca().getEspecieDoc().toUpperCase(),
                    valor_titulo
            );

            wr.write(xml);
            wr.flush();

            // Leitura da Resposta do Serviço
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            // Leituras das Linhas da Resposta
            String linhas = "";
            while (rd.ready()) {
                linhas += rd.readLine();
            }

            wr.close();
            rd.close();
            conn.getInputStream().close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(linhas)));

            Element rootElement = document.getDocumentElement();

            String requestQueueID = getString("retCode", rootElement);

            if (requestQueueID.equals("0")) {

                String requestTicket = getString("ticket", rootElement);

                url = new URL("https://ymbcash.santander.com.br/ymbsrv/CobrancaEndpointService?wsdl");
                conn = (HttpsURLConnection) url.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Type", "Request-Response");
                conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
                conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
                conn.setRequestProperty("User-Agent", "Jakarta Commons-HttpClient/3.1");

                wr = new OutputStreamWriter(conn.getOutputStream());

                String nsu = "" + boleto.getId(); // AMBIENTE PRODUÇÃO

                String xmlTicket = TICKET_ENTRADA(requestTicket, nsu, boleto.getContaCobranca().getEstacao());

                wr.write(xmlTicket);
                wr.flush();

                // Leitura da Resposta do Serviço
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Leituras das Linhas da Resposta
                linhas = "";
                while (rd.ready()) {
                    linhas += rd.readLine();
                }

                wr.close();
                rd.close();
                conn.getInputStream().close();

                factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder_2;

                builder_2 = factory.newDocumentBuilder();
                document = builder_2.parse(new InputSource(new StringReader(linhas)));

                rootElement = document.getDocumentElement();

                String resultSituacao = getString("situacao", rootElement);

                if (resultSituacao.equals("00")) { // BOLETO REGISTRADO
                    if (boleto.getDtCobrancaRegistrada() == null) {
                        Dao dao = new Dao();

                        boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
                        boleto.setDtStatusRetorno(DataHoje.dataHoje());
                        boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

                        dao.update(boleto, true);
                    }
                    return new RespostaWebService(boleto, "");
                } else {

                    String resultMessage = getString("descricaoErro", rootElement);

                    if (resultMessage.contains("@ERYKE0001")) {
                        if (boleto.getDtCobrancaRegistrada() == null) {
                            Dao dao = new Dao();

                            boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
                            boleto.setDtStatusRetorno(DataHoje.dataHoje());
                            boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

                            dao.update(boleto, true);
                        }
                        return new RespostaWebService(boleto, "");
                    }

                    return new RespostaWebService(null, resultMessage);
                }
            }

            return new RespostaWebService(null, "Não existe configuração de WEB SERVICE para esta conta");
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException | ParserConfigurationException | SAXException e) {
            e.getMessage();
        } finally {
            System.out.println("Fim");
        }

        return new RespostaWebService(null, "Não existe configuração de WEB SERVICE para esta conta");

    }

    protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

    public String TICKET_SEG_ENTRADA_TITULO(String CONVENIO, String TIPO_DOCUMENTO, String NUM_DOCUMENTO, String NOME, String ENDERECO, String BAIRRO, String CIDADE, String UF, String CEP, String NOSSO_NUMERO, String VENCIMENTO, String EMISSAO, String ESPECIE, String VALOR) {
        return "<soapenv:Envelope\n"
                + "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    xmlns:impl=\"http://impl.webservice.dl.app.bsbr.altec.com/\">\n"
                + "    <soapenv:Header/>\n"
                + "    <soapenv:Body>\n"
                + "        <impl:create>\n"
                + "            <TicketRequest>\n"
                + "                <dados>\n"
                + "                    <entry>\n"
                + "                        <key>CONVENIO.COD-BANCO</key>\n"
                + "                        <value>0033</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>CONVENIO.COD-CONVENIO</key>\n"
                + "                        <value>" + CONVENIO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.TP-DOC</key>\n"
                + "                        <value>" + TIPO_DOCUMENTO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.NUM-DOC</key>\n"
                + "                        <value>" + NUM_DOCUMENTO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.NOME</key>\n"
                + "                        <value>" + NOME + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.ENDER</key>\n"
                + "                        <value>" + ENDERECO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.BAIRRO</key>\n"
                + "                        <value>" + BAIRRO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.CIDADE</key>\n"
                + "                        <value>" + CIDADE + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.UF</key>\n"
                + "                        <value>" + UF + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>PAGADOR.CEP</key>\n"
                + "                        <value>" + CEP + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>TITULO.NOSSO-NUMERO</key>\n"
                + "                        <value>" + NOSSO_NUMERO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>TITULO.SEU-NUMERO </key>\n"
                + "                        <value>" + NOSSO_NUMERO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>TITULO.DT-VENCTO</key>\n"
                + "                        <value>" + VENCIMENTO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>TITULO.DT-EMISSAO</key>\n"
                + "                        <value>" + EMISSAO + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>TITULO.ESPECIE</key>\n"
                + "                        <value>" + ESPECIE + "</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>TITULO.VL-NOMINAL</key>\n"
                + "                        <value>" + VALOR + "</value>\n"
                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.PC-MULTA</key>\n"
                //                + "                        <value>00000</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.QT-DIAS-MULTA</key>\n"
                //                + "                        <value>00</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.PC-JURO</key>\n"
                //                + "                        <value>00000</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.TP-DESC</key>\n"
                //                + "                        <value>0</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.VL-DESC</key>\n"
                //                + "                        <value>000000000000000</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.DT-LIMI-DESC</key>\n"
                //                + "                        <value>00000000</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.VL-ABATIMENTO</key>\n"
                //                + "                        <value>000000000000000</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.TP-PROTESTO</key>\n"
                //                + "                        <value>0</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.QT-DIAS-PROTESTO</key>\n"
                //                + "                        <value>0</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>TITULO.QT-DIAS-BAIXA</key>\n"
                //                + "                        <value>0</value>\n"
                //                + "                    </entry>\n"
                //                + "                    <entry>\n"
                //                + "                        <key>MENSAGEM</key>\n"
                //                + "                        <value></value>\n"
                //                + "                    </entry>\n"
                + "                </dados>\n"
                + "                <expiracao>100</expiracao>\n"
                + "                <sistema>YMB</sistema>\n"
                + "            </TicketRequest>\n"
                + "        </impl:create>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
    }

    public String TICKET_SEG_CONSULTA(String CONVENIO) {
        return "<soapenv:Envelope\n"
                + "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    xmlns:impl=\"http://impl.webservice.dl.app.bsbr.altec.com/\">\n"
                + "    <soapenv:Header/>\n"
                + "    <soapenv:Body>\n"
                + "        <impl:create>\n"
                + "            <TicketRequest>\n"
                + "                <dados>\n"
                + "                    <entry>\n"
                + "                        <key>CONVENIO.COD-BANCO</key>\n"
                + "                        <value>0033</value>\n"
                + "                    </entry>\n"
                + "                    <entry>\n"
                + "                        <key>CONVENIO.COD-CONVENIO</key>\n"
                + "                        <value>" + CONVENIO + "</value>\n"
                + "                    </entry>\n"
                + "                </dados>\n"
                + "                <expiracao>100</expiracao>\n"
                + "                <sistema>YMB</sistema>\n"
                + "            </TicketRequest>\n"
                + "        </impl:create>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
    }

    public String TICKET_CONSULTA(String ticket_seg, String NSU, String ESTACAO) {
        return "<soapenv:Envelope\n"
                + "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    xmlns:impl=\"http://impl.webservice.ymb.app.bsbr.altec.com/\">\n"
                + "    <soapenv:Header/>\n"
                + "    <soapenv:Body>\n"
                + "        <impl:consultaTitulo>\n"
                + "            <dto>\n"
                + "                <dtNsu>" + DataHoje.data().replace("/", "") + "</dtNsu>\n"
                + "                <estacao>" + ESTACAO + "</estacao>\n"
                + "                <nsu>" + NSU + "</nsu>\n" // 'TST' PARA TESTE, EM PRODUÇÃO NÃO SEI O QUE COLOCAR, APARENTEMENTE DEIXA VAZIO
                + "                <ticket>" + ticket_seg + "</ticket>\n"
                + "                <tpAmbiente>T</tpAmbiente>\n"
                + "            </dto>\n"
                + "        </impl:consultaTitulo>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
    }

    public String TICKET_ENTRADA(String ticket_seg, String NSU, String ESTACAO) {
        return "<soapenv:Envelope\n"
                + "    xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    xmlns:impl=\"http://impl.webservice.ymb.app.bsbr.altec.com/\">\n"
                + "    <soapenv:Header/>\n"
                + "    <soapenv:Body>\n"
                + "        <impl:registraTitulo>\n"
                + "            <dto>\n"
                + "                <dtNsu>" + DataHoje.data().replace("/", "") + "</dtNsu>\n"
                + "                <estacao>" + ESTACAO + "</estacao>\n"
                + "                <nsu>" + NSU + "</nsu>\n" // 'TST' PARA TESTE, EM PRODUÇÃO NÃO SEI O QUE COLOCAR, APARENTEMENTE DEIXA VAZIO
                + "                <ticket>" + ticket_seg + "</ticket>\n"
                + "                <tpAmbiente>P</tpAmbiente>\n"
                + "            </dto>\n"
                + "        </impl:registraTitulo>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
    }

}
