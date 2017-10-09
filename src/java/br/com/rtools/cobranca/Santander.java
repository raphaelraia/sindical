package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.financeiro.StatusRemessa;
import br.com.rtools.financeiro.dao.MovimentoDao;
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

public class Santander extends Cobranca {

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
        fimCodigoBarras += "102";

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
        terceiro_grupo += "102";
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
                List<Movimento> lista_m = bol.getListaMovimento();
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

                for (Movimento m : lista_m) {
                    valor_titulo_double = Moeda.soma(valor_titulo_double, m.getValor());
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
    public RespostaWebService registrarBoleto() {
        
        return new RespostaWebService(null, "Não existe configuração de WEB SERVICE para esta conta");
        
    }
}
