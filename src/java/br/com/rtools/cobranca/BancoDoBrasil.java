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

public class BancoDoBrasil extends Cobranca {

    private final Boolean TESTE = GenericaSessao.getBoolean("debug");

    public BancoDoBrasil(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public BancoDoBrasil(List<BoletoRemessa> listaBoleto) {
        super(listaBoleto);
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
        if ((11 - (soma % 11)) > 9) {
            return "0";
        } else {
            return Integer.toString(11 - (soma % 11));
        }
    }

    public String moduloOnzeBB(String composicao) {
        int i = composicao.length();
        int j = 9;
        int soma = 0;
        String m;
        while (i > 0) {
            if (j < 2) {
                j = 9;
            }
            m = composicao.substring(i - 1, i);
            soma += Integer.parseInt(m) * j;
            j--;
            i--;
        }
        /*
        I. Se o resto for menor que 10 (dez) o DV será igual ao resto;  
        
        II. Se o resto for igual a 10 /dez/ o DV será igual a X; 
        
        III. Se o resto for igual a 0 /zero/ o DV será igual a 0; 
        
        IV. No exemplo acima o digito verificador será igual a 3;
         */
        if ((soma % 11) < 10) {
            return Integer.toString(soma % 11);
        } else {
            return "X";
        }
    }

    @Override
    public String codigoBarras() {
        String codigoBarras = "";
        codigoBarras = boleto.getContaCobranca().getContaBanco().getBanco().getNumero() + boleto.getContaCobranca().getMoeda(); // banco + moeda
        codigoBarras += fatorVencimento(vencimento);   // fator de vencimento
        int i = 0;

        String v_string = Moeda.limparPonto(Moeda.converteR$Double(valor));
        codigoBarras += "0000000000".substring(0, 10 - v_string.length()) + v_string;

//        int tam = Moeda.limparPonto(Moeda.converteR$Double(valor)).length();        
//        while (i != (10 - tam)) { // zeros
//            codigoBarras += "0";
//            i++;
//        }
// COBRANÇA CONVÊNIO DE 7 POSIÇÕES
        if (boleto.getBoletoComposto().length() == 17) {
            //codigoBarras += Moeda.limparPonto(Double.toString(valor)); // valor
            codigoBarras += "000000";
            codigoBarras += boleto.getBoletoComposto();       // nosso numero
            codigoBarras += boleto.getContaCobranca().getCarteira();        // carteira
            codigoBarras = codigoBarras.substring(0, 4) + this.moduloOnzeDV(codigoBarras) + codigoBarras.substring(4, codigoBarras.length());
        } else {
// COBRANÇA CONVÊNIO DE 6 POSIÇÕES
            codigoBarras += boleto.getBoletoComposto();       // nosso numero
            codigoBarras += "0000".substring(0, 4 - boleto.getContaCobranca().getContaBanco().getAgencia().length()) + boleto.getContaCobranca().getContaBanco().getAgencia(); // agencia
            codigoBarras += "00000000".substring(0, 8 - boleto.getContaCobranca().getCodCedente().length()) + boleto.getContaCobranca().getCodCedente(); // agencia
            codigoBarras += boleto.getContaCobranca().getCarteira();        // carteira
            codigoBarras = codigoBarras.substring(0, 4) + this.moduloOnzeDV(codigoBarras) + codigoBarras.substring(4, codigoBarras.length());
        }
        return codigoBarras;
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

    @Override
    public String getNossoNumeroFormatado() {
        if (boleto.getBoletoComposto().length() == 17) {
            return boleto.getBoletoComposto();
        } else {
            return boleto.getBoletoComposto() + "-" + moduloOnzeBB(boleto.getBoletoComposto());
        }
    }

    @Override
    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente() + " " + moduloOnze(boleto.getContaCobranca().getCodCedente());
    }

    @Override
    public String getAgenciaFormatada() {
        return boleto.getContaCobranca().getContaBanco().getAgencia() + " " + moduloOnze(boleto.getContaCobranca().getContaBanco().getAgencia());
    }

    @Override
    public String codigoBanco() {
        return "001-9";
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

        String nome_arquivo = "ARQ_240_" + DataHoje.hora().replace(":", "") + ".txt";

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

            CONTEUDO_REMESSA += "001"; // 01.0 Código do Banco na Compensação 133- Numérico 001 G001 001 para Banco do Brasil S.A. 
            CONTEUDO_REMESSA += "0000"; // 02.0 Lote de Serviço 474- Numérico 0000 G002
            CONTEUDO_REMESSA += "0"; // 03.0 Tipo de Registro 881- Numérico 0 G003
            CONTEUDO_REMESSA += "         "; // 04.0 Uso Exclusivo FEBRABAN / CNAB 9179- Alfanumérico Brancos G004
            CONTEUDO_REMESSA += "2"; // 05.0 Tipo de Inscrição da Empresa 18181- Numérico  G005 1 – para CPF e 2 – para CNPJ.
            CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // 06.0 Número de Inscrição da Empresa 193214- Numérico G006 Informar número da inscrição (CPF ou CNPJ) da Empresa,  alinhado à direita com zeros à esquerda.

            Boleto boleto_rem = listaBoletoRemessa.get(0).getBoleto(); // PEGO O PRIMEIRO BOLETO POIS É OBRIGATÓRIO TODOS MOVIMENTOS SEREM DA MESMA CONTA COBRANÇA
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String cedente = boleto_rem.getContaCobranca().getCedente();
            String codigo_cedente = boleto_rem.getContaCobranca().getCodCedente();

            // CONVENIO DIFERENCIADO PARA BB ( INICIAIS DO NOSSO NÚMERO )
            String convenio = "000000000";

            if (boleto_rem.getBoletoComposto().length() == 17) {
                convenio = "000000000".substring(0, 9 - boleto_rem.getBoletoComposto().substring(0, 7).length()) + boleto_rem.getBoletoComposto().substring(0, 7);
            } else if (boleto_rem.getBoletoComposto().length() == 11) {
                convenio = "000000000".substring(0, 9 - boleto_rem.getBoletoComposto().substring(0, 6).length()) + boleto_rem.getBoletoComposto().substring(0, 6);
            }

            CONTEUDO_REMESSA += convenio; // 07.0 BB1 Nùmero do convênio de cobrança BB
            CONTEUDO_REMESSA += "0014"; // 07.0 BB2 Cobrança Cedente BB '0014'
            CONTEUDO_REMESSA += "17"; // 07.0 BB3 Número da carteira de cobrança BB

            if (boleto_rem.getContaCobranca().getVariacao().length() != 3) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Campo variação em Conta Cobrança é obrigatório!");
            }

            CONTEUDO_REMESSA += boleto_rem.getContaCobranca().getVariacao(); //  ** PIRACICABA É OBRIGATÓRIO SER 035 ** 07.0 BB4 Número da variação da carteira de cobrança BB 
            CONTEUDO_REMESSA += "  "; // 07.0 BB5 Campo reservado BB

            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.0 Agência Mantenedora da Conta 53575- Numérico  G008

            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.0 Dígito Verificador da Agência 58581- Alfanumérico  G009 Obs. Em caso de dígito X informar maiúsculo. 
            CONTEUDO_REMESSA += "000000000000".substring(0, 12 - codigo_cedente.length()) + codigo_cedente; // 10.0 Número da Conta Corrente 597012- Numérico  G010
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(codigo_cedente));// 11.0 Dígito Verificador da Conta 71711- Alfanumérico  G011 Obs. Em caso de dígito X informar maiúsculo. 
            CONTEUDO_REMESSA += "0"; // 12.0 Dígito Verificador da Ag/Conta 72721- Alfanumérico G012 Campo não tratado pelo Banco do Brasil. Informar 'branco'  (espaço) OU zero
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 13.0 Nome da Empresa 7310230- Alfanumérico  G013
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper(("BANCO DO BRASIL S.A.          ").substring(0, 30)); // 14.0 Nome do Banco 10313230- Alfanumérico  G014 BANCO DO BRASIL S.A. 
            CONTEUDO_REMESSA += "          "; // 15.0 Uso Exclusivo FEBRABAN / CNAB 13314210- Alfanumérico Brancos G004 Informar 'brancos' (espaços). 
            CONTEUDO_REMESSA += "1"; // 16.0 Código Remessa / Retorno 1431431- Numérico  G015
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 17.0 Data de Geração do Arquivo 1441518- Numérico  G016 Informar no formato DDMMAAAA
            CONTEUDO_REMESSA += DataHoje.hora().replace(":", ""); // 18.0 Hora de Geração do Arquivo 1521576- Numérico G017
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 19.0 Número Seqüencial do Arquivo 1581636- Numérico  G018 Informação a cargo da empresa. O campo não é criticado pelo sistema do Banco do Brasil. Informar zeros OU um número sequencial, incrementando a cada novo arquivo
            CONTEUDO_REMESSA += "083"; // 20.0 Nº da Versão do Layout do Arquivo 1641663- Numérico 083 G019 Campo não criticado pelo sistema. Informar zeros ou número da versão do leiaute do arquivo que foi usado para formatação dos campos. Versões disponíveis: 084, 083, 082, 080, 050, 040, ou 030
            CONTEUDO_REMESSA += "00000"; // 21.0 Densidade de Gravação do Arquivo 1671715- Numérico G020 Campo não criticado pelo sistema do Banco do Brasil. Informar  zeros, 'brancos', 01600 ou 06250.
            CONTEUDO_REMESSA += "                    "; // 22.0 Para Uso Reservado do Banco 17219120- Alfanumérico  G021
            CONTEUDO_REMESSA += "                    "; // 23.0 Para Uso Reservado da Empresa 19221120- Alfanumérico G022 
            CONTEUDO_REMESSA += "                             "; // 24.0 Uso Exclusivo FEBRABAN / CNAB 21224029- Alfanumérico Brancos G004

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
            CONTEUDO_REMESSA += "001"; // 01.1 Código do Banco na Compensação 133- Numérico 001 G001 001 para Banco do Brasil S.A.
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.1 Lote de Serviço 474- Numérico G002 Começar com '0001'. Essa informação deve ser igual em todos os registros desse lote, até o seu trailer. Se o arquivo possui mais de um lote, incrementar em 1 cada lote, exemplo o 2º lote do arquivo é o '0002', e assim sucessivamente
            CONTEUDO_REMESSA += "1"; // 03.1 Tipo de Registro 881- Numérico '1' G003
            CONTEUDO_REMESSA += "R"; // 04.1 Tipo de Operação 991- Alfanumérico  G028 R – para arquivo remessa, T – quando arquivo retorno. 
            CONTEUDO_REMESSA += "01"; // 05.1 Tipo de Serviço 10112- Numérico '01' G025

            CONTEUDO_REMESSA += "  "; // 06.1 Uso Exclusivo FEBRABAN/CNAB 12132- Alfanumérico Brancos G004 Informar 'brancos' (espaços).
            CONTEUDO_REMESSA += "042"; // 07.1 Nº da Versão do Layout do Lote 14163- Numérico '042' G030
            CONTEUDO_REMESSA += " "; // 08.1 Uso Exclusivo FEBRABAN/CNAB 17171- Alfanumérico Brancos G004
            CONTEUDO_REMESSA += "2"; // 09.1 Tipo de Inscrição da Empresa 18181- Numérico  G005 1 – para CPF e 2 – para CNPJ.
            CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_sindicato.length()) + documento_sindicato; // 10.1 Nº de Inscrição da Empresa 193315- Numérico G006 Informar número da inscrição (CPF ou CNPJ) da Empresa, alinhado à  direita com zeros à esquerda.

            CONTEUDO_REMESSA += convenio; // 11.1 BB1 Nùmero do convênio de cobrança BB
            CONTEUDO_REMESSA += "0014"; // 11.1 BB2 Cobrança Cedente BB
            CONTEUDO_REMESSA += "17"; // 11.1 BB3 Número da carteira de cobrança BB
            CONTEUDO_REMESSA += boleto_rem.getContaCobranca().getVariacao(); // ** PIRACICABA É OBRIGATÓRIO SER 035 ** 11.1 BB4 Número da variação da carteira de cobrança BB
            CONTEUDO_REMESSA += "  "; // 11.1 BB5 Campo que identifica remessa de testes
//            CONTEUDO_REMESSA += "00000000000000000000".substring(0, 20 - codigo_cedente.length()) + codigo_cedente; // 11.1 Código do Convênio no Banco
            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 12.1 Agência Mantenedora da Conta 54585- Numérico  G008
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 13.1 Dígito Verificador da Conta 59591- Alfanumérico  G009
            CONTEUDO_REMESSA += "000000000000".substring(0, 12 - codigo_cedente.length()) + codigo_cedente; // 14.1 Número da Conta Corrente 607112- Numérico  G010
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(codigo_cedente)); // 15.1 Dígito Verificador da Conta 72721- Alfanumérico  G011
            CONTEUDO_REMESSA += "0"; // 16.1 Dígito Verificador da Ag/Conta 73731- Alfanumérico G012 Campo não tratado pelo Banco do Brasil. Informar 'branco' (espaço) OU  zero.

            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 17.1 Nome da Empresa 7410330- Alfanumérico  G013
            CONTEUDO_REMESSA += "                                        "; // 18.1 Mensagem 1 10414340- Alfanumérico  C073
            CONTEUDO_REMESSA += "                                        "; // 19.1 Mensagem 2 14418340- Alfanumérico  C073
            CONTEUDO_REMESSA += "00000000".substring(0, 8 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 20.1 Número Remessa/Retorno 1841918- Numérico G079 Informação a cargo da empresa. Sugerimos informar número  sequencial para controle. Campo não é criticado pelo Banco do Brasil.
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 21.1 Data de Gravação Remessa/Retorno 1921998- Numérico  G068
            CONTEUDO_REMESSA += "00000000"; // 22.1 Data do Crédito 2002078- Numérico C003
            CONTEUDO_REMESSA += "                                 "; // 23.1 Uso Exclusivo FEBRABAN/CNAB 20824033- Alfanumérico Brancos G004 Informar 'brancos' (espaços).

            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Header do Lote menor que 240: " + CONTEUDO_REMESSA);
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            Double valor_total_lote = (double) 0;
            // comecar daqui
            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Integer sequencial_registro_lote = 1;
            for (Integer i = 0; i < listaBoletoRemessa.size(); i++) {
                Boleto bol = listaBoletoRemessa.get(i).getBoleto();
                StatusRemessa sr = listaBoletoRemessa.get(i).getStatusRemessa();

                // tipo 3 - segmento P -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "001"; // 01.3P Código do Banco na Compensação 133- Numérico  G001 001 para Banco do Brasil S.A.
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3P Lote Lote de Serviço 474- Numérico  G002 Informar o número do lote ao qual pertence o registro. Deve ser igual ao número informado no Header do lote
                CONTEUDO_REMESSA += "3"; // 03.3P Tipo de Registro 881- Numérico '3' G003

                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3P Nº Sequencial do Registro no Lote 9135- Numérico  G038 Começar com 00001 e ir incrementando em 1 a cada nova linha de registro detalhe
                CONTEUDO_REMESSA += "P"; // 05.3P Cód. Segmento do Registro Detalhe 14141- Alfanumérico 'P' G03
                CONTEUDO_REMESSA += " "; // 06.3P Uso Exclusivo FEBRABAN/CNAB 15151- Alfanumérico Brancos G004
                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 07.3P Código de Movimento Remessa 16172- Numérico  C004 // REGISTRAR
                } else {
                    CONTEUDO_REMESSA += "02"; // 07.3P Código de Movimento Remessa 16172- Numérico  C004 // BAIXAR
                }
                CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.3P Agência Mantenedora da Conta 18225- Numérico  G008
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.3P Dígito Verificador da Agência 23231- Alfanumérico  G009 Obs. Em caso de dígito X informar maiúsculo. 
                CONTEUDO_REMESSA += "000000000000".substring(0, 12 - codigo_cedente.length()) + codigo_cedente; // 10.3P Número da Conta Corrente 243512- Numérico  G010
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(codigo_cedente)); // 11.3P Dígito Verificador da Conta 36361- Alfanumérico  G011 Obs. Em caso de dígito X informar maiúsculo.
                CONTEUDO_REMESSA += "0"; // 12.3P Dígito Verificador da Ag/Conta 37371- Alfanumérico  G012 Campo não tratado pelo Banco do Brasil. Informar 'branco' (espaço) OU zero.
                // 14 cobraça registrada // JÁ ESTA NO NÚMERO DO DOCUMENTO EM MOVIMENTO
                //CONTEUDO_REMESSA += "14"; // 13.3P Carteira/Nosso Número Modalidade da Carteira 41 42 9(002) Ver Nota Explicativa G069 *G069
                //CONTEUDO_REMESSA += "00000000000000000000".substring(0, 20 - mov.getDocumento().length()) + mov.getDocumento(); // 13.3P Identificação do Título no Banco 385720- Alfanumérico G069 
                CONTEUDO_REMESSA += (bol.getBoletoComposto() + "                    ").substring(0, 20); // 13.3P Identificação do Título no Banco 385720- Alfanumérico G069 
                CONTEUDO_REMESSA += "1"; // 14.3P Código da Carteira 58581-  Numérico  C006
                CONTEUDO_REMESSA += "1"; // 15.3P Forma de Cadastr. do Título no Banco 59591- Numérico C007 Campo não tratado pelo sistema do Banco do Brasil. Pode ser informado 'branco', Zero, 1 – com   cadastramento (Cobrança registrada) ou 2 – sem cadastramento (Cobrança sem registro).
                CONTEUDO_REMESSA += "0"; // 16.3P Tipo de Documento 60601- Alfanumérico C008 Campo não tratado pelo sistema do Banco do Brasil. Pode ser informado 'branco', Zero, 1 – Tradicional, ou 2 –   Escritural.
                CONTEUDO_REMESSA += "0"; // 17.3P Identificação da Emissão do Bloqueto 61611- Numérico C009 
                CONTEUDO_REMESSA += "0"; // 18.3P Identificação da Distribuição 62621-  Alfanumérico  C010
                CONTEUDO_REMESSA += "               ".substring(0, 15 - ("" + bol.getId()).length()) + bol.getId(); // 19.3P Número do Documento de Cobrança 637715-  Alfanumérico  C011
                CONTEUDO_REMESSA += bol.getVencimento().replace("/", ""); // 20.3P Data de Vencimento do Título 78858-  Numérico  C012

                if (bol.getVencimento().replace("/", "").isEmpty()) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "BOLETO: " + bol.getBoletoComposto() + " NÃO TEM VENCIMENTO!");
                }

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

                String valor_titulo;
                // FIXAR VALOR 0,01 CASO FOR MENOR QUE 0,01
                if (valor_titulo_double < 1) {
                    valor_titulo = "1";
                } else {
                    valor_titulo = Moeda.converteDoubleToString(valor_titulo_double).replace(".", "").replace(",", "");
                }

                // NO MANUAL FALA 13 PORÉM TEM QUE SER 15, ACHO QUE POR CAUSA DAS DECIMAIS ,00 (O MANUAL NÃO EXPLICA ISSO)
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - valor_titulo.length()) + valor_titulo; // 21.3P Valor Nominal do Título 8610013 2Numérico  G070
                CONTEUDO_REMESSA += "00000"; // 22.3P Agência Encarregada da Cobrança 1011055-  Numérico  C014 Informar Zeros. A agência encarregada da Cobrança é definida de acordo com o CEP do sacado. 
                CONTEUDO_REMESSA += " "; // 23.3P Dígito Verificador da Agência 1061061-  Alfanumérico  G009
                CONTEUDO_REMESSA += "02";// 24.3P Espécie do Título 1071082-  Numérico  C015

                String aceite = boleto_rem.getContaCobranca().getAceite().equals("N") ? boleto_rem.getContaCobranca().getAceite() : "A";
                CONTEUDO_REMESSA += aceite; // 25.3P Identific. de Título Aceito/Não Aceito 1091091- Alfanumérico C016
                CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 26.3P Data da Emissão do Título 1101178- Numérico G071 
                CONTEUDO_REMESSA += "3"; // 27.3P Código do Juros de Mora 1181181- Numérico C018 
                CONTEUDO_REMESSA += "00000000"; // 28.3P Data do Juros de Mora 1191268-  Numérico  C019 Não há carência de juros. 
                CONTEUDO_REMESSA += "000000000000000"; // 29.3P Juros de Mora por Dia/Taxa 12714113 2Numérico  C020
                CONTEUDO_REMESSA += "0"; // 30.3P Código do Desconto 1 1421421-  Numérico  C021
                CONTEUDO_REMESSA += "00000000"; // 31.3P Data do Desconto 1 1431508-  Numérico  C022 Zeros, quando não houver desconto a ser concedido
                CONTEUDO_REMESSA += "000000000000000"; // 32.3P Desconto 1 Valor/Percentual
                CONTEUDO_REMESSA += "000000000000000"; // 33.3P Valor do IOF a ser Recolhido 16618013 2Numérico  C024
                CONTEUDO_REMESSA += "000000000000000"; // 34.3P Valor do Abatimento 18119513 G045
                CONTEUDO_REMESSA += "                         ".substring(0, 25 - ("" + bol.getId()).length()) + bol.getId(); // 35.3P Identificação do Título na Empresa 19622025-  Alfanumérico  G072
                CONTEUDO_REMESSA += "3"; // 36.3P Código para Protesto 2212211-  Numérico  C026
                CONTEUDO_REMESSA += "00"; // 37.3P Número de Dias para Protesto 2222232- 
                CONTEUDO_REMESSA += "0"; // 38.3P Código para Baixa/Devolução 2242241- 
                CONTEUDO_REMESSA += "000"; // 39.3P Número de Dias para Baixa/Devolução 2252273- 
                CONTEUDO_REMESSA += "09"; // 40.3P Código da Moeda 2282292-  Numérico  G065
                CONTEUDO_REMESSA += "0000000000"; // 41.3P Nº do Contrato da Operação de Créd. 23023910-  Numérico  C030
                CONTEUDO_REMESSA += " "; // 42.3P Uso Exclusivo FEBRABAN/CNAB 2402401-  Alfanumérico Brancos G004 Informar 'brancos' (espaços). 

                if (CONTEUDO_REMESSA.length() != 240) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Segmento P menor que 240: " + CONTEUDO_REMESSA);
                }
                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                // tipo 3 - segmento Q -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "001"; // 01.3Q Código do Banco na Compensação 1 33- Numérico '001' G001 001 para Banco do Brasil S.A. 
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3Q Lote de Serviço 4 74- Numérico G002
                CONTEUDO_REMESSA += "3"; // 03.3Q Tipo de Registro 8 81- Numérico ‘3’ G003

                sequencial_registro_lote++;
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3Q Nº Sequencial do Registro no Lote 9 135- Numérico  G038
                CONTEUDO_REMESSA += "Q"; // 05.3Q Cód. Segmento do Registro Detalhe 14 141- Alfanumérico ‘Q’ G039
                CONTEUDO_REMESSA += " "; // 06.3Q Uso Exclusivo FEBRABAN/CNAB 15 151- Alfanumérico Brancos G004

                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 07.3Q Código de Movimento Remessa 16 172- Numérico  C004 // REGISTRAR
                } else {
                    CONTEUDO_REMESSA += "02"; // 07.3Q Código de Movimento Remessa 16 172- Numérico  C004 // BAIXAR
                }

                Pessoa pessoa = bol.getPessoa();

                if (pessoa.getTipoDocumento().getId() == 1) { // CPF
                    CONTEUDO_REMESSA += "1"; // 08.3Q Tipo de Inscrição 18 181- Numérico 
                } else if (pessoa.getTipoDocumento().getId() == 2) { // CNPJ
                    CONTEUDO_REMESSA += "2"; // 08.3Q Tipo de Inscrição 18 181- Numérico 
                }

                String documento_pessoa = pessoa.getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_pessoa.length()) + documento_pessoa; // 09.3Q Número de Inscrição 19 3315- Numérico 

                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((pessoa.getNome() + "                                        ").substring(0, 40)); // 10.3Q Nome 34 7340- Alfanumérico  G013

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

                CONTEUDO_REMESSA += "0"; // 17.3Q Tipo de Inscrição 154 1541- Numérico  G005
                CONTEUDO_REMESSA += "000000000000000"; // 18.3Q Número de Inscrição 155 16915- Numérico  G006
                CONTEUDO_REMESSA += "                                        "; // 19.3Q Nome do Sacador/Avalista 170 20940- Alfanumérico 
                CONTEUDO_REMESSA += "000"; // 20.3Q Cód. Bco. Corresp. na Compensação 210 2123- Numérico  C031 Campo não tratado. Preencher com 'zeros'
                CONTEUDO_REMESSA += "                    "; // 21.3Q Nosso Nº no Banco Correspondente 213 23220- Alfanumérico  C032 Campo não tratado. Preencher com 'brancos'. 
                CONTEUDO_REMESSA += "        "; // 22.3Q Uso Exclusivo FEBRABAN/CNAB 233 2408- Alfanumérico Brancos G004 Informar 'brancos' (espaços).

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
            CONTEUDO_REMESSA += "001"; // 01.5 Código do Banco na Compensação 133 - Numérico 001 G001 001 para Banco do Brasil S.A
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.5 Lote de Serviço 474 - Numérico  G002 Informar mesmo número do header de lote. 
            CONTEUDO_REMESSA += "5"; // 03.5 Tipo de Registro 881 - Numérico '5' G003
            CONTEUDO_REMESSA += "         "; // 04.5 Uso Exclusivo FEBRABAN/CNAB 9179 - Alfanumérico  G004 Informar 'brancos'. 
            Integer quantidade_lote = (2 * listaBoletoRemessa.size()) + 2;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_lote).length()) + ("" + quantidade_lote); // 05.5 Quantidade de Registros do Lote 18236 - Numérico G057 Total de linhas do lote (inclui Header de lote, Registros e  Trailer de lote).
            CONTEUDO_REMESSA += "                                                                                                                                                                                                                         "; // 06.5 Uso Exclusivo FEBRABAN/CNAB 24240217 - Alfanumérico Brancos G004 Informar Zeros e 'brancos'.

            if (CONTEUDO_REMESSA.length() != 240) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Rodapé do Lote menor que 240: " + CONTEUDO_REMESSA);
            }

            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            // rodapé(footer) do arquivo ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "001"; // 01.9 Código do Banco na Compensação 133- Numérico 001 G001 001 para Banco do Brasil S.A. 
            CONTEUDO_REMESSA += "9999"; // 02.9 Lote de Serviço 474- Numérico 9999 G002
            CONTEUDO_REMESSA += "9"; // 03.9 Tipo de Registro 881- Numérico 9 G003
            CONTEUDO_REMESSA += "         "; // 04.9 Uso Exclusivo FEBRABAN/CNAB 9179- Alfanumérico Brancos G004

            CONTEUDO_REMESSA += "000001"; // 05.9 Quantidade de Lotes do Arquivo 18236- Numérico  G049 Informar quantos lotes o arquivo possui. 

            Integer quantidade_registros = (2 * listaBoletoRemessa.size()) + 4;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_registros).length()) + ("" + quantidade_registros); // 06.9 Quantidade de Registros do Arquivo 24296- Numérico 

            CONTEUDO_REMESSA += "      "; // 07.9 Qtde de Contas p/ Conc. (Lotes) 30356- Numérico 

            CONTEUDO_REMESSA += "                                                                                                                                                                                                             "; // 08.9 Uso Exclusivo FEBRABAN/CNAB 36240205- Alfanumérico Brancos G004
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
        PessoaEnderecoDao ped = new PessoaEnderecoDao();

        Dao dao = new Dao();
        try {
            String nome_arquivo = "ARQX" + DataHoje.hora().replace(":", "") + ".txt";

            dao.openTransaction();

            Remessa remessa = new Remessa(-1, nome_arquivo, DataHoje.dataHoje(), DataHoje.horaMinuto(), null, Usuario.getUsuario(), null);
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

            String patch = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos");
            File fileA = new File(patch + "/downloads");
            if (!fileA.exists()) {
                fileA.mkdir();
            }

            File fileB = new File(patch + "/downloads/remessa");
            if (!fileB.exists()) {
                fileB.mkdir();
            }

            FacesContext context = FacesContext.getCurrentInstance();
            String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/remessa/");
            String destino = caminho + "/" + remessa.getId();

            File flDes = new File(destino); // 0 DIA, 1 MES, 2 ANO
            flDes.mkdir();

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
            // header ----------------------------------------------------------
            // -----------------------------------------------------------------
            CONTEUDO_REMESSA += "0"; // 001 a 001 9(001) Identificação do Registro Header: “0” (zero) 
            CONTEUDO_REMESSA += "1"; // 002 a 002 9(001) Tipo de Operação: “1” (um) 
            if (1 == 1) { // REMESSA
                CONTEUDO_REMESSA += "REMESSA"; // 003 a 009 X(007) Identificação por Extenso do Tipo de Operação 
            } else {
                CONTEUDO_REMESSA += "TESTE"; // 003 a 009 X(007) Identificação por Extenso do Tipo de Operação 
            }
            CONTEUDO_REMESSA += "01"; // 010 a 011 9(002) Identificação do Tipo de Serviço: “01” 
            CONTEUDO_REMESSA += "COBRANCA"; // 012 a 019 X(008) Identificação por Extenso do Tipo de Serviço: “COBRANCA”
            CONTEUDO_REMESSA += "       "; // 020 a 026 X(007) Complemento do Registro: “Brancos”

            Boleto boleto_rem = listaBoletoRemessa.get(0).getBoleto();
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String cedente = boleto_rem.getContaCobranca().getCedente().replace(",", "");
            String codigo_cedente = boleto_rem.getContaCobranca().getCodCedente();

            CONTEUDO_REMESSA += agencia; // 027 a 030 9(004) Prefixo da Agência: Número da Agência onde está cadastrado o convênio líder do cedente 
            CONTEUDO_REMESSA += moduloOnze(agencia); // 031 a 031 X(001) Dígito Verificador - D.V. - do Prefixo da Agência. 
            //CONTEUDO_REMESSA += "00000000".substring(0, 8 - conta.length()) + conta; // 032 a 039 9(008) Número da Conta Corrente: Número da conta onde está cadastrado o Convênio Líder do Cedente 
            //CONTEUDO_REMESSA += moduloOnze(conta); // 040 a 040 X(001) Dígito Verificador - D.V. – do Número da Conta Corrente do Cedente 
            CONTEUDO_REMESSA += "00000000".substring(0, 8 - codigo_cedente.length()) + codigo_cedente; // 032 a 039 9(008) Número da Conta Corrente: Número da conta onde está cadastrado o Convênio Líder do Cedente 
            CONTEUDO_REMESSA += moduloOnze(codigo_cedente); // 040 a 040 X(001) Dígito Verificador - D.V. – do Número da Conta Corrente do Cedente 
            CONTEUDO_REMESSA += "000000"; // 041 a 046 9(006) Complemento do Registro: “000000”
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 047 a 076 X(030) Nome do Cedente
            CONTEUDO_REMESSA += ("001BANCODOBRASIL" + "                  ").substring(0, 18); // 077 a 094 X(018) 001BANCODOBRASIL

            String[] data_gravacao = DataHoje.DataToArrayString(DataHoje.data());

            CONTEUDO_REMESSA += data_gravacao[0] + data_gravacao[1] + data_gravacao[2].substring(2, 4); // 095 a 100 9(006) Data da Gravação: Informe no formato “DDMMAA” 
            CONTEUDO_REMESSA += "0000000".substring(0, 7 - "0000010".length()) + "0000010"; // 101 a 107 9(007) Seqüencial da Remessa 
            CONTEUDO_REMESSA += "                      "; // 108 a 129 X(22) Complemento do Registro: “Brancos”
            CONTEUDO_REMESSA += boleto_rem.getContaCobranca().getBoletoInicial().substring(0, 7); // 130 a 136 9(007) Número do Convênio Líder (numeração acima de 1.000.000 um milhão)" 
            CONTEUDO_REMESSA += "                                                                                                                                                                                                                                                                  "; // 137 a 394 X(258) Complemento do Registro: “Brancos”
            Integer sequencial_registro = 1;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // 395 a 400 9(006) Seqüencial do Registro:”000001”
            sequencial_registro++;

            if (CONTEUDO_REMESSA.length() != 400) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Header de Arquivo menor que 400");
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");
            //buff_writer.newLine();

            CONTEUDO_REMESSA = "";
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
            String documento_sindicato = sindicato.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");

            for (Integer i = 0; i < listaBoletoRemessa.size(); i++) {
                Boleto bol = listaBoletoRemessa.get(i).getBoleto();
                StatusRemessa sr = listaBoletoRemessa.get(i).getStatusRemessa();
                List<Movimento> lista_m = bol.getListaMovimento();

                CONTEUDO_REMESSA += "7"; // 001 a 001 9(001) Identificação do Registro Detalhe: 7 (sete)
                CONTEUDO_REMESSA += "02"; // 002 a 003 9(002) Tipo de Inscrição do Cedente (01 - CPF / 02 - CNPJ)

                CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // 004 a 017 9(014) Número do CPF/CNPJ do Cedente 
                CONTEUDO_REMESSA += agencia; //018 a 021 9(004) Prefixo da Agência 
                CONTEUDO_REMESSA += moduloOnze(agencia); // 022 a 022 X(001) Dígito Verificador - D.V. - do Prefixo da Agência
                //CONTEUDO_REMESSA += "00000000".substring(0, 8 - conta.length()) + conta; // 023 a 030 9(008) Número da Conta Corrente do Cedente
                //CONTEUDO_REMESSA += moduloOnze(conta); // 031 a 031 X(001) Dígito Verificador - D.V. - do Número da Conta Corrente do Cedente 
                CONTEUDO_REMESSA += "00000000".substring(0, 8 - codigo_cedente.length()) + codigo_cedente; // 023 a 030 9(008) Número da Conta Corrente do Cedente
                CONTEUDO_REMESSA += moduloOnze(codigo_cedente); // 031 a 031 X(001) Dígito Verificador - D.V. - do Número da Conta Corrente do Cedente 
                CONTEUDO_REMESSA += boleto_rem.getContaCobranca().getBoletoInicial().substring(0, 7); // NÚMERO DO CONVÊNIO (criar campo no banco pra isso) // 032 a 038 9(007) Número do Convênio de Cobrança do Cedente
                CONTEUDO_REMESSA += "0000000000000000000000000".substring(0, 25 - ("" + bol.getId()).length()) + bol.getId(); // 039 a 063 X(025) Código de Controle da Empresa 
                //CONTEUDO_REMESSA += (boleto_rem.getContaCobranca().getBoletoInicial().substring(0, 7) + "0000000000").substring(0, 17 - ("" + (Integer.valueOf(mov.getDocumento()))).length()) + ("" + (Integer.valueOf(mov.getDocumento()))); // 064 a 080 9(017) Nosso-Número 
                CONTEUDO_REMESSA += "00000000000000000".substring(0, 17 - bol.getBoletoComposto().length()) + bol.getBoletoComposto(); // 064 a 080 9(017) Nosso-Número 
                CONTEUDO_REMESSA += "00"; // 081 a 082 9(002) Número da Prestação: “00” (Zeros)
                CONTEUDO_REMESSA += "00"; // 083 a 084 9(002) Grupo de Valor: “00” (Zeros)
                CONTEUDO_REMESSA += "   "; // 085 a 087 X(003) Complemento do Registro: “Brancos”
                CONTEUDO_REMESSA += " "; // 088 a 088 X(001) Indicativo de Mensagem ou Sacador/Avalista 
                CONTEUDO_REMESSA += "   "; // 089 a 091 X(003) Prefixo do Título: “Brancos”
                CONTEUDO_REMESSA += "019"; // 092 a 094 9(003) Variação da Carteira 
                CONTEUDO_REMESSA += "0"; // 095 a 095 9(001) Conta Caução: “0” (Zero)
                CONTEUDO_REMESSA += "000000"; // 096 a 101 9(006) Número do Borderô: “000000” (Zeros) 
                CONTEUDO_REMESSA += "04DSC"; // 102 a 106 X(005) Tipo de Cobrança
                CONTEUDO_REMESSA += "17"; // 107 a 108 9(002) Carteira de Cobrança 
                if (sr.getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 109 a 110 9(002) Comando // REGISTRAR 
                } else {
                    CONTEUDO_REMESSA += "02"; // 109 a 110 9(002) Comando // BAIXAR
                }
                CONTEUDO_REMESSA += "0000000000".substring(0, 10 - ("" + bol.getId()).length()) + bol.getId(); // 111 a 120 X(010) Seu Número/Número do Título Atribuído pelo Cedente 

                String[] data_vencimento = DataHoje.DataToArrayString(bol.getVencimento());

                CONTEUDO_REMESSA += data_vencimento[0] + data_vencimento[1] + data_vencimento[2].substring(2, 4); // 121 a 126 9(006) Data de Vencimento 

                Double valor_titulo_double = new Double(0);

                for (Movimento m : lista_m) {
                    valor_titulo_double = Moeda.soma(valor_titulo_double, m.getValor());
                }

                String valor_titulo;
                // FIXAR VALOR 0,01 CASO FOR MENOR QUE 0,01
                if (valor_titulo_double < 1) {
                    valor_titulo = "1";
                } else {
                    valor_titulo = Moeda.converteDoubleToString(valor_titulo_double).replace(".", "").replace(",", "");
                }

                CONTEUDO_REMESSA += "0000000000000".substring(0, 13 - valor_titulo.length()) + valor_titulo; // 127 a 139 9(011)v99 Valor do Título 
                CONTEUDO_REMESSA += "001"; // 140 a 142 9(003) Número do Banco: “001”
                CONTEUDO_REMESSA += "0000"; // 143 a 146 9(004) Prefixo da Agência Cobradora: “0000” 
                CONTEUDO_REMESSA += " "; // 147 a 147 X(001) Dígito Verificador do Prefixo da Agência Cobradora: “Brancos”
                CONTEUDO_REMESSA += "01"; // 148 a 149 9(002) Espécie de Titulo 
                CONTEUDO_REMESSA += "N"; // 150 a 150 X(001) Aceite do Título: 

                String[] data_emissao = DataHoje.DataToArrayString(DataHoje.data());

                CONTEUDO_REMESSA += data_emissao[0] + data_emissao[1] + data_emissao[2].substring(2, 4); // 151 a 156 9(006) Data de Emissão: Informe no formato “DDMMAA”
                CONTEUDO_REMESSA += "00"; // 157 a 158 9(002) Instrução Codificada 
                CONTEUDO_REMESSA += "00"; // 159 a 160 9(002) Instrução Codificada 

                CONTEUDO_REMESSA += "0000000000000"; // 161 a 173 9(011)v99 Juros de Mora por Dia de Atraso
                CONTEUDO_REMESSA += "000000"; // 174 a 179 9(006) Data Limite para Concessão de Desconto/Data de Operação do BBVendor/Juros de Mora. 
                CONTEUDO_REMESSA += "0000000000000"; // 180 a 192 9(011)v99 Valor do Desconto 
                CONTEUDO_REMESSA += "0000000000000"; // 193 a 205 9(011)v99 Valor do IOF/Qtde Unidade Variável. 
                CONTEUDO_REMESSA += "0000000000000"; // 206 a 218 9(011)v99 Valor do Abatimento 

                Pessoa pessoa = bol.getPessoa();

                if (pessoa.getTipoDocumento().getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 219 a 220 9(002) Tipo de Inscrição do Sacado CPF
                } else if (pessoa.getTipoDocumento().getId() == 2) {
                    CONTEUDO_REMESSA += "02"; // 219 a 220 9(002) Tipo de Inscrição do Sacado CNPJ
                }
                String documento_pessoa = pessoa.getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_pessoa.length()) + documento_pessoa; // 221 a 234 9(014) Número do CNPJ ou CPF do Sacado
                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((pessoa.getNome() + "                                     ").substring(0, 37)); // 235 a 271 X(037) Nome do Sacado
                CONTEUDO_REMESSA += "   "; // 272 a 274 X(003) Complemento do Registro: “Brancos”

                PessoaEndereco pessoa_endereco = ped.pesquisaEndPorPessoaTipo(pessoa.getId(), 3);
                if (pessoa_endereco != null) {
                    String end_rua = pessoa_endereco.getEndereco().getLogradouro().getDescricao(),
                            end_descricao = pessoa_endereco.getEndereco().getDescricaoEndereco().getDescricao(),
                            end_numero = pessoa_endereco.getNumero(),
                            end_bairro = pessoa_endereco.getEndereco().getBairro().getDescricao(),
                            end_cep = pessoa_endereco.getEndereco().getCep(),
                            end_cidade = pessoa_endereco.getEndereco().getCidade().getCidade(),
                            end_uf = pessoa_endereco.getEndereco().getCidade().getUf();

                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_rua + " " + end_descricao + " " + end_numero + "                                        ").substring(0, 40)); // 275 a 314 X(040) Endereço do Sacado 
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_bairro + "            ").substring(0, 12)); // 315 a 326 X(012) Bairro do Sacado 
                    CONTEUDO_REMESSA += end_cep.replace("-", "").replace(".", ""); // 327 a 334 9(008) CEP do Endereço do Sacado
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_cidade + "               ").substring(0, 15)); // 335 a 349 X(015) Cidade do Sacado 
                    CONTEUDO_REMESSA += end_uf; // 350 a 351 X(002) UF da Cidade do Sacado 
                } else {
                    CONTEUDO_REMESSA += "                                        "; // 275 a 314 X(040) Endereço do Sacado 
                    CONTEUDO_REMESSA += "            "; // 315 a 326 X(012) Bairro do Sacado 
                    CONTEUDO_REMESSA += "        "; // 327 a 334 9(008) CEP do Endereço do Sacado
                    CONTEUDO_REMESSA += "               "; // 335 a 349 X(015) Cidade do Sacado 
                    CONTEUDO_REMESSA += "  "; // 350 a 351 X(002) UF da Cidade do Sacado 
                }

                CONTEUDO_REMESSA += "                                        "; // 352 a 391 X(040) Observações/Mensagem ou Sacador/Avalista 
                CONTEUDO_REMESSA += "  "; // 392 a 393 X(002) Número de Dias Para Protesto 
                CONTEUDO_REMESSA += " "; // 394 a 394 X(001) Complemento do Registro: “Brancos”
                CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // 395 a 400 9(006) Seqüencial de Registro
                sequencial_registro++;

                if (CONTEUDO_REMESSA.length() != 400) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Detalhe de Arquivo menor que 400");
                }
                buff_writer.write(CONTEUDO_REMESSA + "\r\n");
                //buff_writer.newLine();

                CONTEUDO_REMESSA = "";

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, bol, listaBoletoRemessa.get(i).getStatusRemessa());

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return new RespostaArquivoRemessa(null, "Erro ao salvar Remessa Banco");
                }

                list_log.add("ID: " + bol.getId());
                list_log.add("Valor: " + valor_titulo);
                list_log.add("-----------------------");
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            // footer ----------------------------------------------------------
            // -----------------------------------------------------------------
            CONTEUDO_REMESSA += "9"; // 001 a 001 9(001) Identificação do Registro Trailer: “9”
            CONTEUDO_REMESSA += "                                                                                                                                                                                                                                                                                                                                                                                                         "; // 002 a 394 X(393) Complemento do Registro: “Brancos”
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // 395 a 400 9(006) Número Seqüencial do Registro no Arquivo 

            if (CONTEUDO_REMESSA.length() != 400) {
                dao.rollback();
                return new RespostaArquivoRemessa(null, "Footer de Arquivo menor que 400");
            }
            buff_writer.write(CONTEUDO_REMESSA + "\r\n");
            buff_writer.write("");
            buff_writer.flush();
            buff_writer.close();

            dao.commit();

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
    public RespostaWebService registrarBoleto() {
        // CASO QUEIRA TESTAR A ROTINA DE REGISTRO SEM REGISTRAR COLOCAR http://localhost:8080/Sindical?debug=true
        if (TESTE) {
            Dao dao = new Dao();

            boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
            boleto.setDtStatusRetorno(DataHoje.dataHoje());
            boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

            dao.update(boleto, true);
            return new RespostaWebService(boleto, "");
        }

        try {
            //String convenio = boleto.getBoletoComposto().substring(0, 6);
            // idConv É DIFERENTE DO CONVÊNIO QUE ESTA NO INICIO DO NOSSO NUMERO
            String convenio = "318045";
            Pessoa pessoa = boleto.getPessoa();

            // ACESSA O LINK
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("https://mpag.bb.com.br/site/mpag/");

            // PASSA OS PARAMETROS
            List<NameValuePair> params = new ArrayList(2);
            params.add(new BasicNameValuePair("idConv", convenio));
            params.add(new BasicNameValuePair("refTran", boleto.getBoletoComposto()));
            params.add(new BasicNameValuePair("valor", boleto.getValorString().replace(",", "").replace(".", "")));
            //params.add(new BasicNameValuePair("qtdPontos", ""));
            params.add(new BasicNameValuePair("dtVenc", boleto.getVencimento().replace("/", "")));
            params.add(new BasicNameValuePair("tpPagamento", "2"));
            params.add(new BasicNameValuePair("cpfCnpj", pessoa.getDocumento().replace("/", "").replace(".", "").replace("-", "")));
            switch (pessoa.getTipoDocumento().getId()) {
                case 1: // CPF
                    params.add(new BasicNameValuePair("indicadorPessoa", "1"));
                    break;
                case 2: // CNPJ
                    params.add(new BasicNameValuePair("indicadorPessoa", "2"));
                    break;
                default:
                    return new RespostaWebService(null, "Tipo de Documento da pessoa inválido! : " + pessoa.getTipoDocumento().getId() + " : " + pessoa.getTipoDocumento().getDescricao());
            }
            //params.add(new BasicNameValuePair("valorDesconto", ""));
            //params.add(new BasicNameValuePair("dataLimiteDesconto", ""));
            params.add(new BasicNameValuePair("tpDuplicata", "DM"));
            params.add(new BasicNameValuePair("urlRetorno", "http://localhost:8084/Sindical"));
            params.add(new BasicNameValuePair("urlInforma", "http://localhost:8084/Sindical"));
            params.add(new BasicNameValuePair("nome", AnaliseString.normalizeUpper((pessoa.getNome() + "                                                            ").substring(0, 60)).trim()));

            PessoaEndereco pessoa_endereco = new PessoaEnderecoDao().pesquisaEndPorPessoaTipo(pessoa.getId(), 3);
            if (pessoa_endereco != null) {
                String end_rua = pessoa_endereco.getEndereco().getLogradouro().getDescricao(),
                        end_descricao = pessoa_endereco.getEndereco().getDescricaoEndereco().getDescricao(),
                        end_numero = pessoa_endereco.getNumero(),
                        end_bairro = pessoa_endereco.getEndereco().getBairro().getDescricao(),
                        end_cep = pessoa_endereco.getEndereco().getCep(),
                        end_cidade = pessoa_endereco.getEndereco().getCidade().getCidade(),
                        end_uf = pessoa_endereco.getEndereco().getCidade().getUf();

                params.add(new BasicNameValuePair("endereco", AnaliseString.normalizeUpper((end_rua + " " + end_descricao + " " + end_numero + " " + end_bairro + "                                        ").substring(0, 60)).trim()));
                params.add(new BasicNameValuePair("cidade", AnaliseString.normalizeUpper((end_cidade + "                  ").substring(0, 18)).trim()));
                params.add(new BasicNameValuePair("uf", end_uf));
                String cep = end_cep.replace("-", "").replace(".", "");
                if (cep.length() < 8) {
                    return new RespostaWebService(null, pessoa.getNome() + " CEP INVÁLIDO: " + cep);
                }
                params.add(new BasicNameValuePair("cep", cep));
            } else {
                return new RespostaWebService(null, "Pessoa não possui endereço! : " + pessoa.getNome());
            }
            params.add(new BasicNameValuePair("msgLoja", ""));

            // ENVIA O FORMULARIO
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            // RETORNO DA PAGINA
            if (entity != null) {
                String msg = EntityUtils.toString(entity);

                if (msg.contains("Titulo ja incluido anteriormente. (C008-000)")) {
                    // System.out.println("Opa, já registrado??");
                    if (boleto.getDtCobrancaRegistrada() == null) {
                        Dao dao = new Dao();

                        boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
                        boleto.setDtStatusRetorno(DataHoje.dataHoje());
                        boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

                        dao.update(boleto, true);
                    }
                    return new RespostaWebService(boleto, "");
                }

                if (msg.contains("Atenção!")) {
                    System.out.println("Atenção!");
                    System.out.println(msg);
                    return new RespostaWebService(null, "Erro no retorno do Banco do Brasil");
                }

                if (boleto.getDtCobrancaRegistrada() == null) {
                    Dao dao = new Dao();

                    boleto.setDtCobrancaRegistrada(DataHoje.dataHoje());
                    boleto.setDtStatusRetorno(DataHoje.dataHoje());
                    boleto.setStatusRetorno((StatusRetorno) dao.find(new StatusRetorno(), 2));

                    dao.update(boleto, true);
                    return new RespostaWebService(boleto, "");
                }
            }

        } catch (IOException e) {
            e.getMessage();
        }
        return new RespostaWebService(null, "Não existe configuração de WEB SERVICE para esta conta");
    }
}
