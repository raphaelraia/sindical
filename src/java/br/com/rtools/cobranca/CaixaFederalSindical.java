package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Registro;
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

public class CaixaFederalSindical extends Cobranca {

    public CaixaFederalSindical(Integer id_pessoa, double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public CaixaFederalSindical(List<Movimento> listaMovimento) {
        super(listaMovimento);
    }

    @Override
    public String codigoBarras() {
        JuridicaDao jurDB = new JuridicaDao();

        String ent = ((Registro) Registro.get()).getTipoEntidade();
        // (1-Sindicato, 2-Federação, 3-Confederação)
        if (ent.equals("S")) {
            ent = "1";
        } else if (ent.equals("F")) {
            ent = "2";
        } else if (ent.equals("C")) {
            ent = "3";
        }
        String codigoBarras = "";
        codigoBarras = boleto.getContaCobranca().getContaBanco().getBanco().getNumero() + boleto.getContaCobranca().getMoeda(); // banco + moeda
        codigoBarras += fatorVencimento(vencimento);   // fator de vencimento
        int i = 0;

        int tam = Moeda.limparPonto(Moeda.converteR$Double(valor)).length();
        while (i != (10 - tam)) { // zeros
            codigoBarras += "0";
            i++;
        }
        codigoBarras += Moeda.limparPonto(Double.toString(valor)); // valor
        codigoBarras += "97";
        codigoBarras += boleto.getContaCobranca().getSicasSindical();
        codigoBarras += jurDB.pesquisaJuridicaPorPessoa(id_pessoa).getCnae().getNumero().substring(0, 1);
        //codigoBarras += "1";
        codigoBarras += ent;
        codigoBarras += "77";
        codigoBarras += boleto.getBoletoComposto();       // nosso numero
        codigoBarras += jurDB.pesquisaJuridicaPorPessoa(id_pessoa).getCnae().getNumero().substring(1, 3);
        codigoBarras = codigoBarras.substring(0, 4) + moduloOnzeDV(codigoBarras) + codigoBarras.substring(4, codigoBarras.length());
        int dd = codigoBarras.length();
        return codigoBarras;
    }

    @Override
    public String representacao() {
        String codigoBarras = codigoBarras();

        String repNumerica = codigoBarras.substring(0, 4);
        repNumerica += "97";
        repNumerica += codigoBarras.substring(21, 44);
        repNumerica += codigoBarras.substring(4, 19);
        repNumerica = repNumerica.substring(0, 9) + this.moduloDez(repNumerica.substring(0, 9)) + repNumerica.substring(9, repNumerica.length());
        repNumerica = repNumerica.substring(0, 20) + this.moduloDez(repNumerica.substring(10, 20)) + repNumerica.substring(20, repNumerica.length());
        repNumerica = repNumerica.substring(0, 31) + this.moduloDez(repNumerica.substring(21, 31)) + repNumerica.substring(31, repNumerica.length());
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

    @Override
    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente().substring(0, 3) + "."
                + boleto.getContaCobranca().getCodCedente().substring(3, 6) + "."
                + boleto.getContaCobranca().getCodCedente().substring(6) + "-"
                + this.moduloOnze(boleto.getContaCobranca().getCodCedente());
    }

    @Override
    public String getNossoNumeroFormatado() {
        return boleto.getBoletoComposto();
    }

    @Override
    public String codigoBanco() {
        return "104-0";
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

        String nome_arquivo = "E" + DataHoje.data().substring(0, 2) + "00000".substring(0, 5 - ("" + remessa.getId()).length()) + ("" + remessa.getId()) + ".REM";

        remessa.setNomeArquivo(nome_arquivo);

        if (!dao.update(remessa)) {
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

        try {
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

            File flDes = new File(destino);
            flDes.mkdir();

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

            CONTEUDO_REMESSA += "104"; // 01.0 Código do Banco 1 3 9(003) Preencher '104’ G001
            CONTEUDO_REMESSA += "0000"; // 02.0 Código do Lote 4 7 9(004) Preencher '0000' *G002
            CONTEUDO_REMESSA += "0"; // 03.0 Tipo de Registro 8 8 9(001) Preencher '0' (equivale a Header de Arquivo) *G003
            CONTEUDO_REMESSA += "         "; // 04.0 CNAB Filler 9 17 X(009) Preencher com espaços G004
            CONTEUDO_REMESSA += "2"; // 05.0 Tipo de Inscrição do Beneficiário 18 18 9(001) Preencher com o tipo de inscrição do Beneficiário: '1', se CPF (pessoa física); ou '2' se CNPJ (pessoa jurídica) *G005
            CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // 06.0 Número de Inscrição do Beneficiário 19 32 9(014) Ver Nota Explicativa G006 *G006
            CONTEUDO_REMESSA += "00000000000000000000"; // 07.0 Uso Exclusivo CAIXA 33 52 9(020) Preencher com zeros -

            Boleto boleto_rem = dbmov.pesquisaBoletos(listaMovimento.get(0).getNrCtrBoleto());
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String cedente = boleto_rem.getContaCobranca().getCedente();
            String codigo_cedente = "0" + boleto_rem.getContaCobranca().getSicasSindical();

            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.0 Agência Mantenedora da Conta 53 57 9(005) Preencher com o código da agência detentora da conta, com um zero à esquerda *G008
            //CONTEUDO_REMESSA += moduloOnze("00000".substring(0, 5 - agencia.length()) + agencia); // 09.0 Dígito Verificador da Agência 58 58 X(001) Preencher com o dígito verificador da agência, informado pela CAIXA *G009
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.0 Dígito Verificador da Agência 58 58 X(001) Preencher com o dígito verificador da agência, informado pela CAIXA *G009
            CONTEUDO_REMESSA += "000000".substring(0, 6 - codigo_cedente.length()) + codigo_cedente; // 10.0 Código do Beneficiário 59 64 9(006) Código fornecido pela CAIXA, através da agência de relacionamento do cliente; trata-se do código do Beneficiário (6 posições) *G007
            CONTEUDO_REMESSA += "0000000"; // 11.0 Uso Exclusivo CAIXA 65 71 9(007) Preencher com zeros -
            CONTEUDO_REMESSA += "0"; // 12.0 Uso Exclusivo CAIXA 72 72 9(001) Preencher com zeros -
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 13.0 Nome da Empresa 73 102 X(030) Preencher com o Nome da empresa G013
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper(("CAIXA ECONOMICA FEDERAL       ").substring(0, 30)); // 14.0 Banco Beneficiário Nome do Banco 103 132 X(030) Preencher 'CAIXA ECONOMICA FEDERAL' G014
            CONTEUDO_REMESSA += "          "; // 15.0 CNAB Filler 133 142 X(010) Preencher com espaços G004
            CONTEUDO_REMESSA += "1"; // 16.0 Arquivo Código Remessa / Retorno 143 143 9(001) Preencher '1' G015
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 17.0 Data de Geração do Arquivo 144 151 9(008) Preencher com a data da criação do arquivo, no formato DDMMAAAA (Dia, Mês e Ano) G016
            CONTEUDO_REMESSA += DataHoje.hora().replace(":", ""); // 18.0 Hora de Geração do Arquivo 152 157 9(006) Preencher com a hora de criação do arquivo, no formato HHMMSS (Hora, Minuto e Segundos) G017
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 19.0 NSA 158 163 9(006) Número seqüencial adotado e controlado pelo responsável pela geração do arquivo para ordenar a disposição dos arquivos encaminhados; evoluir de 1 em 1 para cada Header de Arquivo *G018
            CONTEUDO_REMESSA += "101"; // 20.0 Layout do Arquivo No da Versão do Layout do Arquivo 164 166 3 - Num ' “101”' *G019 
            CONTEUDO_REMESSA += "00000"; // 21.0 Densidade de Gravação do Arquivo 167 171 9(005) Preencher com zeros G020
            CONTEUDO_REMESSA += "                    "; // 22.0 Uso Exclusivo CAIXA Filler 172 191 X(020) Preencher com espaços G021

            if (1 == 2) { // REMESSA-TESTE -- Periodo de homologação
                CONTEUDO_REMESSA += "REMESSA-TESTE       "; // 23.0 Reservado Empresa Situação do Arquivo 192 211 X(020) Preencher com ‘REMESSA-TESTE' na fase de testes (simulado); Preencher com ‘REMESSA-PRODUCAO’, se estiver em produção G022
            } else { // REMESSA-PRODUÇÃO -- Apos homologação
                CONTEUDO_REMESSA += "REMESSA-PRODUCAO    "; // 23.0 Reservado Empresa Situação do Arquivo 192 211 X(020) Preencher com ‘REMESSA-TESTE' na fase de testes (simulado); Preencher com ‘REMESSA-PRODUCAO’, se estiver em produção G022
            }

            CONTEUDO_REMESSA += "    "; // 24.0 Versão do Aplicativo Versão Aplicativo CAIXA 212 215 X(004) Preencher com espaços C077
            CONTEUDO_REMESSA += "                         "; // 25.0 CNAB Filler 216 240 X(025) Preencher com espaços G004
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            buff_writer.write(CONTEUDO_REMESSA + "\r\n");
            CONTEUDO_REMESSA = "";

            Integer sequencial_lote = 1;

            // header do lote ------------------------------------------------------------
            // ---------------------------------------------------------------------------
            CONTEUDO_REMESSA += "104"; // 01.0 Código do Banco 1 3 9(003) Preencher '104’ G001
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.1 Lote de Serviço 4 7 9(004) Ver Nota Explicativa G002; ATENÇÃO: Dentro de um Lote pode haver vários serviços (segmentos); o campo Lote de Serviço de cada um dos segmentos de um mesmo Lote deve ser igual ao informado neste campo 02.1. Após o fechamento de um Lote, o próximo Lote deve possuir o número do lote anterior acrescido de 1, e a mesma lógica do segmentos do novo lote seguirem o número dele se aplica *G002
            CONTEUDO_REMESSA += "1"; // 03.1 Tipo de Registro 8 8 9(001) Preencher '1’ (equivale a Header de Lote) *G003
            CONTEUDO_REMESSA += "R"; // 04.1 Serviço Tipo de Operação 9 9 X(001) Preencher ‘R’  (equivale a Arquivo Remessa) *G028

            // SE COBRANÇA REGISTRADA
            if (true == true) {
                CONTEUDO_REMESSA += "01"; // 05.1 Tipo de Serviço 10 11 9(002) Preencher com ‘01', se Cobrança Registrada; ou ‘02’, se Cobrança Sem Registro/Serviços *G025
            } else {
                CONTEUDO_REMESSA += "02"; // 05.1 Tipo de Serviço 10 11 9(002) Preencher com ‘01', se Cobrança Registrada; ou ‘02’, se Cobrança Sem Registro/Serviços *G025
            }

            CONTEUDO_REMESSA += "00"; // 06.1 Filler 12 13 9(002) Preencher com zeros G004
            CONTEUDO_REMESSA += "060"; // 07.1- Layout do Lote Nº da Versão do Layout do Lote 14 16 3 - Num “060” *G030
            CONTEUDO_REMESSA += " "; // 08.1 CNAB Filler 17 17 X(001) Preencher com espaços G004

            CONTEUDO_REMESSA += "2"; // 09.1 Empresa Tipo de Inscrição do Beneficiário 18 18 9(001) Preencher com o tipo de inscrição do beneficiário: '1', se CPF (pessoa física); ou '2' se CNPJ (pessoa jurídica) *G005
            CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // 10.1 Número de Inscrição do Beneficiário 19 33 9(015) Ver Nota Explicativa G006 *G006
            CONTEUDO_REMESSA += "000000".substring(0, 6 - codigo_cedente.length()) + codigo_cedente; // 11.1 Código do Beneficiário 34 39 9(006) Código fornecido pela CAIXA, através da agência de relacionamento do cliente; trata-se do código do Beneficiário (6 posições) *G007
            CONTEUDO_REMESSA += "000000000000000"; // 11.1 Uso Exclusivo CAIXA 40 53 9(014) Preencher com zeros -\
            CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 12.1 Agência Mantenedora da Conta 54 58 9(005) Preencher com o código da agência detentora da conta, com um zero à esquerda *G008
            CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 13.1 Dígito Verificador da Agência 59 59 X(001) Preencher com o dígito verificador da agência, informado pela CAIXA *G011
            CONTEUDO_REMESSA += "000000".substring(0, 6 - codigo_cedente.length()) + codigo_cedente; // 14.1 Código do Convênio no Banco 60 65 9(006) Mesmo código do campo 11.1 *G007
            CONTEUDO_REMESSA += "0000000"; // 15.1 Código do Modelo de Boleto Personalizado 66 72 9(007) Código fornecido pela CAIXA/Gráfica, utilizado somente quando o modelo do boleto for personalizado; do contrário, preencher com zeros C078
            CONTEUDO_REMESSA += "0"; // 16.1 Uso Exclusivo CAIXA 73 73 9(001) Preencher '0’                 
            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // 17.1 Nome da Empresa 74 103 X(030) Preencher com o Nome da empresa G013
            CONTEUDO_REMESSA += "                                        "; // 18.1 Informações Mensagem 1 104 143 X(040) Ver Nota Explicativa C073 C073
            CONTEUDO_REMESSA += "                                        "; // 19.1 Mensagem 2 144 183 X(040) Ver Nota Explicativa C073 C073
            CONTEUDO_REMESSA += "00000000".substring(0, 8 - ("" + remessa.getId()).length()) + ("" + remessa.getId()); // 21.1 Controle da Cobrança Número da Remessa 184 191 9(008) Número Seqüencial do Arquivo; preencher com a mesma numeração sequencial de arquivo (campo 19.0) G079
            CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 21.1 Data de Gravação Remessa 192 199 9(008) Preencher com a data da gravação do arquivo, no formato DDMMAAAA (Dia, Mês e Ano) G068
            CONTEUDO_REMESSA += "00000000"; // 22.1 Data do Crédito Filler 200 207 9(008) Preencher com zeros C003 
            CONTEUDO_REMESSA += "                                 "; // 23.1 CNAB Filler 208 240 X(033) Preencher com espaços G004

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
                CONTEUDO_REMESSA += "104"; // 01.3P Controle Código do Banco 1 3 9(003) Preencher '104’ G001
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3P Lote de Serviço 4 7 9(004) Ver Nota Explicativa G002; ATENÇÃO: Dentro de um mesmo Lote de Serviço, todos os Segmentos devem trazer nesse campo o mesmo número do campo equivalente a esse no Header de Lote (campo 02.1) *G002
                CONTEUDO_REMESSA += "3"; // 03.3P Tipo de Registro 8 8 9(001) Preencher '3’ (equivale a Detalhe de Lote) *G003

                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3P Serviço Nº Sequencial do Registro no Lote 9 13 9(005) Ver Nota Explicativa G038; evoluir de 1 em 1 para cada Segmento do Lote *G038
                CONTEUDO_REMESSA += "P"; // 05.3P Cód. Segmento do Registro Detalhe 14 14 X(001) Preencher 'P’ *G039
                CONTEUDO_REMESSA += " "; // 06.3P Filler 15 15 X(001) Preencher com espaços G004
                CONTEUDO_REMESSA += "01"; // 07.3P Código de Movimento Remessa 16 17 9(002) Ver Nota Explicativa C004 *C004
                CONTEUDO_REMESSA += "00000".substring(0, 5 - agencia.length()) + agencia; // 08.3P Código de identificação do Beneficiário Agência Mantenedora da Conta 18 22 9(005) Prencher com o código da agência detentora da conta, com um zero à esquerda *G008
                CONTEUDO_REMESSA += moduloOnze("" + Integer.valueOf(agencia)); // 09.3P Dígito Verificador da Agência 23 23 X(001) Preencher com o dígito verificador da agência, informado pela CAIXA *G009
                CONTEUDO_REMESSA += "000000".substring(0, 6 - codigo_cedente.length()) + codigo_cedente; // 10.3P Código do Convênio no Banco 24 29 9(006) Código fornecido pela CAIXA, através da agência de relacionamento do cliente; trata-se do código do Beneficiário (6 posições) *G007
                CONTEUDO_REMESSA += "00000000"; // 11.3P Uso Exclusivo CAIXA - Filler 30 37 9(008) Preencher com zeros 
                CONTEUDO_REMESSA += "000"; // 12.3P Uso Exclusivo CAIXA Filler 38 40 9(003) Preencher com zeros 
                // 14 cobraça registrada // JÁ ESTA NO NÚMERO DO DOCUMENTO EM MOVIMENTO
                //CONTEUDO_REMESSA += "14"; // 13.3P Carteira/Nosso Número Modalidade da Carteira 41 42 9(002) Ver Nota Explicativa G069 *G069
                CONTEUDO_REMESSA += "00000000000000000".substring(0, 17 - mov.getDocumento().length()) + mov.getDocumento(); // 13.3P Carteira/Nosso Número Título no Banco 43 57 9(015) *G069
                CONTEUDO_REMESSA += "1"; // 14.3P Características da Cobrança Código da Carteira 58 58 9(001) Preencher '1’, equivalente a Cobrança Simples *C006
                CONTEUDO_REMESSA += "1"; // 15.3P Forma de Cadastramento do Título no Banco 59 59 9(001) Preencher somente quando desejar que o boleto seja emitido pelo banco, com: ‘1’ Cobrança Registrada ou ‘2’ Cobrança sem Registro *C007
                CONTEUDO_REMESSA += "2"; // 16.3P Tipo de Documento 60 60 X(001) Preencher '2’ (equivale a Escritural) C008
                CONTEUDO_REMESSA += "2"; // 17.3P Identificação da Emissão do Boleto 61 61 9(001) Ver Nota Explicativa C009 *C009
                CONTEUDO_REMESSA += "0"; // 18.3P Identificação da Entrega do Boleto 62 62 X(001) Ver Nota Explicativa C010 C010
                CONTEUDO_REMESSA += "           ".substring(0, 11 - ("" + mov.getId()).length()) + mov.getId(); // 19.3P Nº do Documento (Seu Nº) Número do Documento de Cobrança 63 73 X(011) Campo de preenchimento obrigatório; preencher com Seu Número de controle do título *C011
                CONTEUDO_REMESSA += "    "; // 19.3P Uso Exclusivo CAIXA Filler 74 77 X(004) Preencher com espaços -
                CONTEUDO_REMESSA += mov.getVencimento().replace("/", ""); // 20.3P Vencimento Data de Vencimento do Título 78 85 9(008) Preencher com a data de vencimento do título, no formato DDMMAAAA (Dia, Mês e Ano); para regras de vencimento à vista ou contra-apresentação, vide Nota Explicativa C012 *C012

                String valor_titulo;
                // FIXAR VALOR 1,00 CASO FOR MENOR QUE 1,00
                if (mov.getValor() < 1) {
                    valor_titulo = "100";
                } else {
                    valor_titulo = mov.getValorString().replace(".", "").replace(",", "");
                }

                // NO MANUAL FALA 13 PORÉM TEM QUE SER 15, ACHO QUE POR CAUSA DAS DECIMAIS ,00 (O MANUAL NÃO EXPLICA ISSO)
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - valor_titulo.length()) + valor_titulo; // 21.3P Valor do Título Valor Nominal do Título 86 100 9(013) Preencher com o valor original do título, utilizando 2 casas decimais (exemplo: título de valor 530,44 - preencher 0000000053044) *G070
                CONTEUDO_REMESSA += "00000"; // 22.3P Ag. Cobradora Agência Encarregada da Cobrança 101 105 9(005) Preencher com zeros *C014
                CONTEUDO_REMESSA += "0"; // 23.3P DV Dígito Verificador da Agência 106 106 X(001) Preencher '0’ *C014
                CONTEUDO_REMESSA += "99";//CONTEUDO_REMESSA += boleto_rem.getContaCobranca().getEspecieDoc(); // 24.3P Espécie de Título Espécie do Título 107 108 9(002) Ver Nota Explicativa C015 *C015

                String aceite = "A";
                CONTEUDO_REMESSA += aceite; // 25.3P Aceite Identific. de Título Aceito/Não Aceito 109 109 X(001) Indica se o título de cobrança possui aceite do pagador; preencher com ‘A’ (Aceite) ou ‘N’ (Não Aceite) C016
                CONTEUDO_REMESSA += DataHoje.data().replace("/", ""); // 26.3P Data Emissão do Título Data da Emissão do Título 110 117 9(008) Preencher com a data de emissão do título, no formato DDMMAAAA (Dia, Mês e Ano) G071            
                CONTEUDO_REMESSA += "3"; // 27.3P Juros Código do Juros de Mora 118 118 9(001) Indica o tipo de pagamento de juros de mora; preencher com o tipo de preferência: ‘1’ (Valor por Dia); ou ‘2’ (Taxa Mensal); ou ‘3’ (Isento) *C018
                CONTEUDO_REMESSA += "00000000"; // 28.3P Data do Juros de Mora 119 126 9(008) Preencher com a data indicativa do início da cobrança de Juros de Mora, no formato DDMMAAAA (Dia, Mês e Ano), devendo ser maior que a Data de Vencimento; ATENÇÃO, caso a informação seja inválida ou não informada, o sistema assumirá data igual à Data de Vencimento + 1 *C019
                CONTEUDO_REMESSA += "000000000000000"; // 29.3P Juros de Mora por Dia/Taxa 127 141 9(013) Preencher de acordo com a informação do campo 27.3P, utilizando duas casas decimais: Se 27.3P = ‘1’, informar Valor Se = ‘2’, informar percentual; Se = '3', preencher com zeros C020
                CONTEUDO_REMESSA += "0"; // 30.3P Desconto 1 Código do Desconto 1 142 142 9(001) Indica o tipo de desconto que deseja conceder ao Pagador do título: ‘0’ (Sem Desconto); ou ‘1’  (Valor Fixo até a Data do Desconto informada); ou ‘2’ (Percentual até a Data do Desconto informada) *C021
                CONTEUDO_REMESSA += "00000000"; // 31.3P Data do Desconto 1 143 150 9(008) Preencher de acordo com a informação do campo 30.3P (Código do Desconto), no formato DDMMAAAA (Dia, Mês e Ano); Se 30.3P = ‘0’, preencher com zeros; Se = '1' ou '2', informar a data limite do desconto C022
                CONTEUDO_REMESSA += "000000000000000"; // 32.3P Valor/Percentual a ser Concedido 151 165 9(013) Preencher de acordo com a informação do campo 30.3P (Código do Desconto), utilizando duas casas decimais: Se 30.3P = '0', preencher com zeros; Se  = ‘1’, informar Valor; Se = ‘2’, informar percentual C023
                CONTEUDO_REMESSA += "000000000000000"; // 33.3P Vlr IOF Valor do IOF a ser Recolhido 166 180 9(013) Preencher com o Valor original do IOF (Imposto sobre Operações Financeiras) do título prêmio de seguro na data de sua emissão, utilizando duas casas decimais; caso não seja título prêmio de seguro, preencher com zeros C024
                CONTEUDO_REMESSA += "000000000000000"; // 34.3P Vlr Abatimento Valor do Abatimento 181 195 9(013) Preencher com o valor do abatimento (redução do valor do documento) dado ao Pagador do título, expresso em moeda corrente com duas casas decimais G045
                CONTEUDO_REMESSA += "                         ".substring(0, 25 - ("" + mov.getId()).length()) + mov.getId(); // 35.3P Uso Empresa Cedente Identificação do Título na Empresa 196 220 X(025) Preencher igual ao campo 19.3P (Número do Documento de Cobrança) G072
                CONTEUDO_REMESSA += "3"; // 36.3P Código p/ Protesto Código para Protesto 221 221 9(001) Preencher conforme desejado com  ‘1’ (Protestar); ou ‘3’ (Não Protestar); ou ‘9’ (Cancelamento Protesto Automático); ATENÇÃO: O código 9 somente pode ser utilizado se a informação do campo 07.3P (Código de Movimento Remessa) for igual a '31': C026
                CONTEUDO_REMESSA += "00"; // 37.3P Prazo p/ Protesto Número de Dias para Protesto 222 223 9(002) Preencher com o número desejado de dias após a data de vencimento para inicialização do processo de cobrança via protesto; pode ser de 02 a 90 dias, sendo: De 02 a 05   = dias úteis; Acima de 05 = dias corridos C027
                CONTEUDO_REMESSA += "1"; // 38.3P Código p/ Baixa/ Devolução Código para Baixa/Devolução 224 224 9(001) Preencher com o procedimento que deseja que CAIXA realize para o título vencido: '1’ (Baixar / Devolver) ou ‘2’ (Não Baixar / Não  Devolver) C028
                CONTEUDO_REMESSA += "030"; // 39.3P Prazo p/ Baixa/ Devolução Número de Dias para Baixa/ Devolução 225 227 X(003) Preencher com o número desejado de dias corridos após a data de vencimento do  título não pago em que a CAIXA deverá baixá-lo da carteira e devolvê-lo; pode ser de 05 a 120 dias corridos; ATENÇÃO: Esse prazo não pode ser menor que o prazo para protesto (campo 37.3P), quando este existir C029
                CONTEUDO_REMESSA += "09"; // 40.3P Código da Moeda Código da Moeda 228 229 9(002) Preencher ‘09’ (REAL) *G065
                CONTEUDO_REMESSA += "0000000000"; // 41.3P Uso Exclusivo CAIXA Filler 230 239 9(010) Preencher com zeros -
                CONTEUDO_REMESSA += "2"; // 42.3P CNAB Filler 240 240 X(001) Preencher com espaços G004

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");
                CONTEUDO_REMESSA = "";

                // tipo 3 - segmento Q -------------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "104"; // 01.3Q Controle Código do Banco 1 3 9(003) Preencher '104’ G001
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3Q Lote de Serviço 4 7 9(004) Ver Nota Explicativa G002; ATENÇÃO: Dentro de um mesmo Lote de Serviço, todos os Segmentos devem trazer nesse campo o mesmo número do campo equivalente a esse no Header de Lote (campo 02.1) *G002
                CONTEUDO_REMESSA += "3"; // 03.3Q Tipo de Registro 8 8 9(001) Preencher '3’ (equivale a Detalhe de Lote) *G003

                sequencial_registro_lote++;
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3Q Serviço Nº Sequencial do Registro no Lote 9 13 9(005) Ver Nota Explicativa G038 *G038
                CONTEUDO_REMESSA += "Q"; // 05.3Q Cód. Segmento do Registro Detalhe 14 14 X(001) Preencher 'Q’ *G039
                CONTEUDO_REMESSA += " "; // 06.3Q Filler 15 15 X(001) Preencher com espaços G004
                CONTEUDO_REMESSA += "01"; // 07.3Q Código de Movimento Remessa 16 17 9(002) Ver Nota Explicativa C004 *C004

                if (mov.getPessoa().getTipoDocumento().getId() == 1) { // CPF
                    CONTEUDO_REMESSA += "1"; // 08.3Q Dados do Pagador Tipo de Inscrição do Pagador 18 18 9(001) Preencher com o tipo de inscrição do Pagador: '1', se CPF (pessoa física); ou '2' se CNPJ (pessoa jurídica) *G005
                } else if (mov.getPessoa().getTipoDocumento().getId() == 2) { // CNPJ
                    CONTEUDO_REMESSA += "2"; // 08.3Q Dados do Pagador Tipo de Inscrição do Pagador 18 18 9(001) Preencher com o tipo de inscrição do Pagador: '1', se CPF (pessoa física); ou '2' se CNPJ (pessoa jurídica) *G005
                }

                String documento_pessoa = mov.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "000000000000000".substring(0, 15 - documento_pessoa.length()) + documento_pessoa; // 09.3Q Número de Inscrição do Pagador 19 33 9(015) Preencher com o número do CNPJ ou CPF do Pagador, conforme o caso *G006

                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((mov.getPessoa().getNome() + "                                        ").substring(0, 40)); // 10.3Q Nome do Pagador 34 73 X(040) Preencher com Nome do Pagador 

                PessoaEndereco pessoa_endereco = ped.pesquisaEndPorPessoaTipo(mov.getPessoa().getId(), 3);
                if (pessoa_endereco != null) {
                    String end_rua = pessoa_endereco.getEndereco().getLogradouro().getDescricao(),
                            end_descricao = pessoa_endereco.getEndereco().getDescricaoEndereco().getDescricao(),
                            end_numero = pessoa_endereco.getNumero(),
                            end_bairro = pessoa_endereco.getEndereco().getBairro().getDescricao(),
                            end_cep = pessoa_endereco.getEndereco().getCep(),
                            end_cidade = pessoa_endereco.getEndereco().getCidade().getCidade(),
                            end_uf = pessoa_endereco.getEndereco().getCidade().getUf();

                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_rua + " " + end_descricao + " " + end_numero + "                                        ").substring(0, 40)); // 11.3Q Endereço do Pagador 74 113 X(040) Ver Nota Explicativa G032 G032
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_bairro + "               ").substring(0, 15)); // 12.3Q Bairro do Pagador 114 128 X(015) G032
                    String cep = end_cep.replace("-", "").replace(".", "");
                    CONTEUDO_REMESSA += cep.substring(0, 5); // 13.3Q CEP do Pagador 129 133 9(005) Preencher com o código adotado pelos CORREIOS para identificação do endereço G034
                    CONTEUDO_REMESSA += cep.substring(5, 8); // 14.3Q Sufixo do CEP do Pagador 134 136 9(003) Preencher com o código adotado pelos CORREIOS para complementação do código de CEP G035
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_cidade + "               ").substring(0, 15)); // 15.3Q Cidade do Pagador 137 151 X(015) Preencher com o nome do município correspondente ao endereço do Pagador G033
                    CONTEUDO_REMESSA += end_uf; // 16.3Q Unidade da Federação do Pagador 152 153 X(002) Preencher com o código do Estado ou Unidade da Federação correspondente ao município G036
                } else {
                    CONTEUDO_REMESSA += "                                        "; // 11.3Q Endereço do Pagador 74 113 X(040) Ver Nota Explicativa G032 G032
                    CONTEUDO_REMESSA += "               "; // 12.3Q Bairro do Pagador 114 128 X(015) G032
                    CONTEUDO_REMESSA += "     "; // 13.3Q CEP do Pagador 129 133 9(005) Preencher com o código adotado pelos CORREIOS para identificação do endereço G034
                    CONTEUDO_REMESSA += "   "; // 14.3Q Sufixo do CEP do Pagador 134 136 9(003) Preencher com o código adotado pelos CORREIOS para complementação do código de CEP G035
                    CONTEUDO_REMESSA += "               "; // 15.3Q Cidade do Pagador 137 151 X(015) Preencher com o nome do município correspondente ao endereço do Pagador G033
                    CONTEUDO_REMESSA += "  "; // 16.3Q Unidade da Federação do Pagador 152 153 X(002) Preencher com o código do Estado ou Unidade da Federação correspondente ao município G036
                }

                CONTEUDO_REMESSA += "0000000000000"; // 17.3Q Capital Social da Empresa Capital Social da Empresa 154 166 11 2 Num  C098 
                CONTEUDO_REMESSA += "0000000000000"; // 18.3Q Capital Social do Estabelecime nto Capital Social do Estabelecimento 167 179 11 2 Num  C099 
                CONTEUDO_REMESSA += "000000000"; // 19.3Q Número de Empregados Contribuinte s Número de Empregados Contribuintes 180 188 9 - Num  C100 
                CONTEUDO_REMESSA += "0000000000000"; // 20.3Q Total da Remuneraçã o – Contribuinte s Total da Remuneração – Contribuintes 189 201 11 2 Num  C101 
                CONTEUDO_REMESSA += "000000000"; // 21.3Q Total de Empregados do Estabelecime nto Total de Empregados do Estabelecimento 202 210 9 - Num  C102 
                String cnae = mov.getPessoa().getJuridica().getCnae().getNumero().replace("-", "").replace("/", "");
                cnae = cnae.substring(0, 5);
                CONTEUDO_REMESSA += cnae; // 22.3Q Informação de CNAE Código CNAE Contribuinte/Pagador 211 215 5 - Num  C103 
                /*
                1-SINDICATO
                2-FEDERAÇÃO
                3-CONFEDERAÇÃO
                4-CENTRAL SINDICAL
                5-MTE
                 */
                CONTEUDO_REMESSA += "1"; // 23.3Q Dados do Beneficiár io Tipo de entidade Tipo de Entidade Sindical 216 216 1 - Num  C105

                String cod_sindical = boleto_rem.getContaCobranca().getSicasSindical();
                CONTEUDO_REMESSA += "00000".substring(0, 5 - cod_sindical.length()) + cod_sindical; // 24.3Q Código Sindical Código sindical da Entidade Sindical 217 221 5 - Num  C106
                CONTEUDO_REMESSA += "                   "; // 25.3Q  CNAB Uso Exclusivo FEBRABAN/ CNAB Uso Exclusivo FEBRABAN/CNAB  222 240 19 Alfa Brancos G004 *G006 

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                // tipo 3 - segmento Y-53 ----------------------------------------------------
                // ---------------------------------------------------------------------------
                CONTEUDO_REMESSA += "104"; // 01.3Y Controle Banco Código do Banco na Compensação 1 3 3 - Num  G001 
                CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.3Y Lote Lote de Serviço 4 7 4 - Num  *G002 
                CONTEUDO_REMESSA += "3"; // 03.3Y Registro Tipo de Registro 8 8 1 - Num ‘3’ *G003 

                sequencial_registro_lote++;
                CONTEUDO_REMESSA += "00000".substring(0, 5 - ("" + sequencial_registro_lote).length()) + ("" + sequencial_registro_lote); // 04.3Y Serviço Nº do Registro Nº Sequencial do Registro no Lote 9 13 5 - Num  *G038 
                CONTEUDO_REMESSA += "Y"; // 05.3Y Segmento Cód. Segmento do Registro Detalhe 14 14 1 - Alfa ‘Y’ *G039 
                CONTEUDO_REMESSA += " "; // 06.3Y CNAB Uso Exclusivo FEBRABAN/CNAB 15 15 1 - Alfa Brancos G004 
                CONTEUDO_REMESSA += "01"; // 07.3Y Cód. Mov. Código de Movimento Remessa 16 17 2 - Num  *C004 
                CONTEUDO_REMESSA += "53"; // 08.3Y Cod. Reg. Opcional Identificação Registro Opcional 18 19 2 - Num '53' *G067 
                /*
                '01' = Aceita qualquer valor ‘02’= Entre o mínimo e o máximo ‘03’= Não aceita pagamento com o valor divergente
                 */
                CONTEUDO_REMESSA += "02"; // 09.3Y Tipo de Pagamento Identificação de Tipo de Pagamento Identificação de Tipo de Pagamento 20 21 2 - Num  C078
                CONTEUDO_REMESSA += "01"; // 10.3Y Quantidade de Pagamentos Possíveis Quantidade de Pagamentos Possíveis 22 23 2  Num  C079 
                /*
                  ‘1’ = % (percentual) ‘2’ = valor 
                 */
                CONTEUDO_REMESSA += "2"; // 11.3Y  Alteração Nominal do Título Tipo de Valor  Tipo de Valor Informado 24 24 1  Num  C080
                CONTEUDO_REMESSA += "000000090000000"; // 12.3Y Valor Máximo/Percentual Valor Máximo 25 39 13 2 Num  C081 
                CONTEUDO_REMESSA += "2"; // 14.3Y Tipo de Valor Tipo de Valor Informado 40 40 1  Num  C08
                CONTEUDO_REMESSA += "000000000000100"; // 15.3Y Valor Mínimo/Percentual Valor Mínimo 41 55 13 2 Num  C082 
                CONTEUDO_REMESSA += "                                                                                                                                                                                         "; // 17.3Y CNAB Uso Exclusivo FEBRABAN/CNAB 56 240 185  Num Brancos G004

                buff_writer.write(CONTEUDO_REMESSA + "\r\n");
                CONTEUDO_REMESSA = "";

                sequencial_registro_lote++;

                Double valor_l;

                if (mov.getValor() < 1) {
                    valor_l = new Double(1);
                } else {
                    valor_l = mov.getValor();
                }

                valor_total_lote = Moeda.soma(valor_total_lote, valor_l);

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, mov);

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return null;
                }

                list_log.add("ID: " + mov.getId());
                list_log.add("Valor: " + Moeda.converteDoubleToString(valor_l));
                list_log.add("-----------------------");
            }

            // rodapé(footer) do lote ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "104"; // 01.5 Controle Código do Banco 1 3 9(003) Preencher ‘104’ G001
            CONTEUDO_REMESSA += "0000".substring(0, 4 - ("" + sequencial_lote).length()) + ("" + sequencial_lote); // 02.5 Lote de Serviço 4 7 9(004) Ver Nota Explicativa G002; ATENÇÃO: Dentro de um mesmo Lote de Serviço, todos os Segmentos devem trazer nesse campo o mesmo número do campo equivalente a esse no Header de Lote (campo 02.1) *G002
            CONTEUDO_REMESSA += "5"; // 03.5 Tipo de Registro 8 8 9(001) Preencher ‘5’ (equivale a Trailer de Lote) *G003
            CONTEUDO_REMESSA += "         "; // 04.5 CNAB Filler 9 17 X(009) Preencher com espaços G004
            Integer quantidade_lote = (3 * listaMovimento.size()) + 2;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_lote).length()) + ("" + quantidade_lote); // 05.5 Qtde de Registros Quantidade de Registros no Lote 18 23 9(006) Preencher com a Quantidade de registros no lote; trata-se da somatória dos registros de tipo 1, 3, e 5 *G057
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + listaMovimento.size()).length()) + ("" + listaMovimento.size()); // 06.5 Totalização da Cobrança Simples Quantidade de Títulos em Cobrança Simples 24 29 9(006) Preencher com a Quantidade total de títulos informados no lote *C070
            String valor_total = valor_total_lote.toString().replace(".", "").replace(",", "");
            CONTEUDO_REMESSA += "00000000000000000".substring(0, 17 - valor_total.length()) + valor_total; // 07.5 Valor Total dos Títulos em Carteiras de Cobrança Simples 30 46 9(017) Preencher com o Valor total de títulos informados no lote *C071
            CONTEUDO_REMESSA += "000000"; // 08.5 Totalização da Cobrança Caucionada Quantidade de Títulos em Cobranças Caucionadas 47 52 9(006) Preencher com zeros *C070
            CONTEUDO_REMESSA += "00000000000000000"; // 09.5 Valor Total dos Títulos em Carteiras Caucionadas 53 69 9(017) Preencher com zeros *C071
            CONTEUDO_REMESSA += "000000"; // 10.5 Totalização da Cobrança Descontada Quantidade de Títulos em Cobrança Descontada 70 75 9(006) Preencher com zeros *C070
            CONTEUDO_REMESSA += "00000000000000000"; // 11.5 Quantidade de Títulos em Carteiras Descontadas 76 92 9(017) Preencher com zeros *C071
            CONTEUDO_REMESSA += "                               "; // 12.5 CNAB Filler 93 123 X(031) Preencher com espaços G004
            CONTEUDO_REMESSA += "                                                                                                                     "; // 13.5 CNAB Filler 124 240 X(117) G004

            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";

            // rodapé(footer) do arquivo ----------------------------------------------------
            // ---------------------------------------------------------------------------                
            CONTEUDO_REMESSA += "104"; // 01.9 Controle Código do Banco 1 3 9(003) Preencher ‘104’ G001
            CONTEUDO_REMESSA += "9999"; // 02.9 Lote de Serviço 4 7 9(004) Preencher ‘9999’ *G002
            CONTEUDO_REMESSA += "9"; // 03.9 Tipo de Registro 8 8 9(001) Preencher ‘9’ (equivale a Trailer de Arquivo) *G003
            CONTEUDO_REMESSA += "         "; // 04.9 CNAB Filler 9 17 X(009) Preencher com espaços G004

            CONTEUDO_REMESSA += "000001"; // 05.9 Totais Quantidade de Lotes do Arquivo 18 23 9(006) Informar o Número total de lotes enviados no arquivo; trata-se da somatória dos registros de tipo 1, incluindo header e trailer G049

            Integer quantidade_registros = (3 * listaMovimento.size()) + 4;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + quantidade_registros).length()) + ("" + quantidade_registros); // 06.9 Quantidade de Registros do Arquivo 24 29 9(006) Informar o Número do total de registros enviados no arquivo; trata-se da somatória dos registros de tipo 0, 1, 3, 5 e 9 G056
            CONTEUDO_REMESSA += "      "; // 07.9 CNAB Filler 30 35 X(006) Preencher com espaços G004
            CONTEUDO_REMESSA += "                                                                                                                                                                                                             "; // 08.9 CNAB Filler 36 240 X(105) G004

            buff_writer.write(CONTEUDO_REMESSA);
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
