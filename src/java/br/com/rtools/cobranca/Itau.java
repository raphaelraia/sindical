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

public class Itau extends Cobranca {
// CNAB 400
// O layout com 15 digitos difilmente sera utilizado. So grandes emissoes, como por exemplo magazine e luiza

    public Itau(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public Itau(List<Movimento> listaMovimento) {
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

        if ((10 - (soma % 10)) > 9) {
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

        if (soma < 11) {
            if ((soma == 1) || (soma == 0)) {
                return "1"; // a subtração abaixo pode resultar em 10 caso a soma seja "1". Apesar de ser um caso raro, estamos tratando esse posível erro.
            }
            return Integer.toString(11 - soma);
        }

        if (((11 - (soma % 11)) > 9)
                || ((11 - (soma % 11)) == 0) // Digito verificador geral nunca dara zero
                || ((11 - (soma % 11)) == 1)) {
            return "1";
        } else {
            return Integer.toString(11 - (soma % 11));
        }
    }

    @Override
    public String codigoBarras() {
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

        codigoBarras += boleto.getContaCobranca().getCarteira();
        codigoBarras += boleto.getBoletoComposto();       // nosso numero
        codigoBarras += this.moduloDez(boleto.getBoletoComposto()
                + boleto.getContaCobranca().getCodCedente()
                + boleto.getContaCobranca().getCarteira()
                + boleto.getContaCobranca().getContaBanco().getAgencia());
        codigoBarras += boleto.getContaCobranca().getContaBanco().getAgencia(); // agencia
        codigoBarras += boleto.getContaCobranca().getCodCedente();        // codigo cedente
        codigoBarras += this.moduloDez(boleto.getContaCobranca().getContaBanco().getAgencia()
                + boleto.getContaCobranca().getCodCedente());
        codigoBarras += "000";
        codigoBarras = codigoBarras.substring(0, 4) + moduloOnzeDV(codigoBarras) + codigoBarras.substring(4, codigoBarras.length());
        int o = codigoBarras.length();
        return codigoBarras;
    }

    @Override
    public String representacao() {
        String codigoBarras = this.codigoBarras();
        int i = 0;
        // campo 1
        String repNumerica = boleto.getContaCobranca().getContaBanco().getBanco().getNumero(); // banco
        repNumerica += boleto.getContaCobranca().getMoeda();
        repNumerica += boleto.getContaCobranca().getCarteira();
        repNumerica += boleto.getBoletoComposto().substring(0, 2);
        repNumerica += this.moduloDez(boleto.getContaCobranca().getContaBanco().getBanco().getNumero()
                + boleto.getContaCobranca().getMoeda()
                + boleto.getContaCobranca().getCarteira()
                + boleto.getBoletoComposto().substring(0, 2));// DAC

        // campo 2
        repNumerica += boleto.getBoletoComposto().substring(2);
        repNumerica += this.moduloDez(boleto.getBoletoComposto()
                + boleto.getContaCobranca().getCodCedente()
                + boleto.getContaCobranca().getCarteira()
                + boleto.getContaCobranca().getContaBanco().getAgencia());
        repNumerica += boleto.getContaCobranca().getContaBanco().getAgencia().substring(0, 3);
        repNumerica += this.moduloDez(boleto.getBoletoComposto().substring(3)
                + this.moduloDez(boleto.getBoletoComposto()
                        + boleto.getContaCobranca().getCodCedente()
                        + boleto.getContaCobranca().getCarteira()
                        + boleto.getContaCobranca().getContaBanco().getAgencia())
                + boleto.getContaCobranca().getContaBanco().getAgencia().substring(0, 3)); // DAC

        // campo3
        repNumerica += boleto.getContaCobranca().getContaBanco().getAgencia().substring(3, 4);
        repNumerica += boleto.getContaCobranca().getCodCedente()
                + this.moduloDez(boleto.getContaCobranca().getContaBanco().getAgencia()
                        + boleto.getContaCobranca().getCodCedente());
        repNumerica += "000";
        repNumerica += this.moduloDez(boleto.getContaCobranca().getContaBanco().getAgencia().substring(3, 4)
                + boleto.getContaCobranca().getCodCedente()
                + this.moduloDez(boleto.getContaCobranca().getContaBanco().getAgencia()
                        + boleto.getContaCobranca().getCodCedente())
                + "000");       // DAC
        /*
         // campo 4
         repNumerica += codigoBarras.substring(4, 5);

         // campo 5
         repNumerica += codigoBarras.substring(5, 10);
         repNumerica += codigoBarras.substring(10, 19);
         */

        // campo 4 e campo 5
        repNumerica += codigoBarras.substring(4, 19);

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
        return boleto.getContaCobranca().getCarteira() + "/" + boleto.getBoletoComposto() + "-"
                + this.moduloDez(boleto.getContaCobranca().getCarteira() + boleto.getBoletoComposto());
    }

    @Override
    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente().substring(0, 3) + boleto.getContaCobranca().getCodCedente().substring(3) + "-"
                + this.moduloDez(boleto.getContaCobranca().getContaBanco().getAgencia() + boleto.getContaCobranca().getCodCedente());
    }

    @Override
    public String codigoBanco() {
        return "341-7";
    }
    
    @Override
    public File gerarRemessa240() {
        return null;
    }
    
    @Override
    public File gerarRemessa400() {
        PessoaEnderecoDao ped = new PessoaEnderecoDao();
        MovimentoDao dbmov = new MovimentoDao();

        try {
            String nome_arquivo = "IT" + DataHoje.hora().replace(":", "") + ".txt";
            Dao dao = new Dao();

            dao.openTransaction();

            Remessa remessa = new Remessa(-1, nome_arquivo, DataHoje.dataHoje(), DataHoje.horaMinuto(), null, Usuario.getUsuario(), null);
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

            if (listaMovimento.isEmpty()) {
                return null;
            }
            // header ----------------------------------------------------------
            // -----------------------------------------------------------------
            CONTEUDO_REMESSA += "0"; // IDENTIFICAÇÃO DO REGISTRO HEADER  001   001 9(01) 0
            CONTEUDO_REMESSA += "1"; // TIPO DE OPERAÇÃO - REMESSA 002   002  9(01)  1
            if (1 == 1) { // REMESSA
                CONTEUDO_REMESSA += "REMESSA"; // IDENTIFICAÇÃO POR EXTENSO DO MOVIMENTO  003   009  X(07)  REMESSA
            } else {
                CONTEUDO_REMESSA += "TESTE  "; // IDENTIFICAÇÃO POR EXTENSO DO MOVIMENTO  003   009  X(07)  REMESSA
            }
            CONTEUDO_REMESSA += "01"; // IDENTIFICAÇÃO DO TIPO DE SERVIÇO 010   011 9(02)  01
            CONTEUDO_REMESSA += "COBRANCA       "; // IDENTIFICAÇÃO POR EXTENSO DO TIPO DE SERVIÇO 012   026 X(15)  COBRANCA

            Boleto boleto_rem = dbmov.pesquisaBoletos(listaMovimento.get(0).getNrCtrBoleto());
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String cedente = boleto_rem.getContaCobranca().getCedente().replace(",", "");
            String codigo_cedente = boleto_rem.getContaCobranca().getCodCedente();

            CONTEUDO_REMESSA += agencia; // AGÊNCIA MANTENEDORA DA CONTA 027   030 9(04)
            CONTEUDO_REMESSA += "00"; // COMPLEMENTO DE REGISTRO 031   032 9(02) 00
            CONTEUDO_REMESSA += "00000".substring(0, 5 - codigo_cedente.length()) + codigo_cedente; // NÚMERO DA CONTA CORRENTE DA EMPRESA 033   037 9(05)
            CONTEUDO_REMESSA += moduloOnze(agencia); // DÍGITO DE AUTO CONFERÊNCIA AG/CONTA EMPRESA 038   038 9(01)
            CONTEUDO_REMESSA += "        "; // COMPLEMENTO DO REGISTRO  039   046 X(08) 

            CONTEUDO_REMESSA += AnaliseString.normalizeUpper((cedente + "                              ").substring(0, 30)); // NOME POR EXTENSO DA "EMPRESA MÃE"  047   076 X(30)

            CONTEUDO_REMESSA += "341"; // Nº DO BANCO NA CÂMARA DE COMPENSAÇÃO 077   079 9(03)   341
            CONTEUDO_REMESSA += "BANCO ITAU SA  "; // NOME POR EXTENSO DO BANCO COBRADOR 080   094  X(15)  BANCO ITAU SA

            String[] data_gravacao = DataHoje.DataToArrayString(DataHoje.data());

            CONTEUDO_REMESSA += data_gravacao[0] + data_gravacao[1] + data_gravacao[2].substring(2, 4); // DATA DE GERAÇÃO DO ARQUIVO  095   100  9(06) DDMMAA
            CONTEUDO_REMESSA += "                                                                                                                                                                                                                                                                                                      "; // COMPLEMENTO DO REGISTRO 101   394 X(294)
            Integer sequencial_registro = 1;
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // NÚMERO SEQÜENCIAL DO REGISTRO NO ARQUIVO  395   400  9(06)  000001
            sequencial_registro++;

            buff_writer.write(CONTEUDO_REMESSA + "\r\n");

            CONTEUDO_REMESSA = "";
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------
            // CONTINUAR DAQUI ---
            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
            String documento_sindicato = sindicato.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");

            for (Integer i = 0; i < listaMovimento.size(); i++) {
                Movimento mov = listaMovimento.get(i);

                CONTEUDO_REMESSA += "1"; // TIPO DE REGISTRO  IDENTIFICAÇÃO DO REGISTRO TRANSAÇÃO 001   001  9(01) 1
                CONTEUDO_REMESSA += "02"; // CÓDIGO DE INSCRIÇÃO TIPO DE INSCRIÇÃO DA EMPRESA 002   003  9(02) NOTA 1

                CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_sindicato.length()) + documento_sindicato; // NÚMERO DE INSCRIÇÃO Nº DE INSCRIÇÃO DA EMPRESA (CPF/CNPJ) 004   017 9(14) NOTA 1
                CONTEUDO_REMESSA += agencia; // AGÊNCIA AGÊNCIA MANTENEDORA DA CONTA 018   021 9(04)
                CONTEUDO_REMESSA += "00"; // ZEROS COMPLEMENTO DE REGISTRO 022   023 9(02) “00”

                CONTEUDO_REMESSA += "00000".substring(0, 5 - codigo_cedente.length()) + codigo_cedente; // CONTA NÚMERO DA CONTA CORRENTE DA EMPRESA 024   028 9(05)
                CONTEUDO_REMESSA += moduloOnze(agencia); // DAC DÍGITO DE AUTO CONFERÊNCIA AG/CONTA EMPRESA 029   029 9(01)
                CONTEUDO_REMESSA += "    "; // BRANCOS COMPLEMENTO DE REGISTRO 030   033 X(04) 
                CONTEUDO_REMESSA += "0000"; // INSTRUÇÃO/ALEGAÇÃO CÓD.INSTRUÇÃO/ALEGAÇÃO A SER CANCELADA 034   037 9(04) NOTA 27

                CONTEUDO_REMESSA += "0000000000000000000000000".substring(0, 25 - ("" + mov.getId()).length()) + mov.getId(); // USO DA EMPRESA  IDENTIFICAÇÃO DO TÍTULO NA EMPRESA 038   062  X(25) NOTA 2
                CONTEUDO_REMESSA += "00000000".substring(0, 8 - mov.getDocumento().length()) + mov.getDocumento(); // NOSSO NÚMERO  IDENTIFICAÇÃO DO TÍTULO NO BANCO 063   070 9(08) NOTA 3
                CONTEUDO_REMESSA += "0000000000000"; // QTDE DE MOEDA  QUANTIDADE DE MOEDA VARIÁVEL 071   083 9(08)V9(5) NOTA 4   

                CONTEUDO_REMESSA += "109"; // Nº DA CARTEIRA  NÚMERO DA CARTEIRA NO BANCO 084   086 9(03) NOTA 5
                CONTEUDO_REMESSA += "                     "; // USO DO BANCO IDENTIFICAÇÃO DA OPERAÇÃO NO BANCO 087   107  X(21)
                CONTEUDO_REMESSA += "I"; // CARTEIRA  CÓDIGO DA CARTEIRA  108   108  X(01) NOTA 5
                CONTEUDO_REMESSA += "01"; // CÓD. DE OCORRÊNCIA IDENTIFICAÇÃO DA OCORRÊNCIA  109   110 9(02) NOTA 6 
                CONTEUDO_REMESSA += "0000000000".substring(0, 10 - mov.getDocumento().length()) + mov.getDocumento();; // Nº DO DOCUMENTO  Nº DO DOCUMENTO DE COBRANÇA (DUPL.,NP ETC.) 111   120  X(10) NOTA 18 

                String[] data_vencimento = DataHoje.DataToArrayString(mov.getVencimento());
                CONTEUDO_REMESSA += data_vencimento[0] + data_vencimento[1] + data_vencimento[2].substring(2, 4); // VENCIMENTO  DATA DE VENCIMENTO DO TÍTULO  121   126  9(06) NOTA 7 

                String valor_titulo = Moeda.converteR$Double(mov.getValor()).replace(".", "").replace(",", "");
                CONTEUDO_REMESSA += "0000000000000".substring(0, 13 - valor_titulo.length()) + valor_titulo; // VALOR DO TÍTULO  VALOR NOMINAL DO TÍTULO 127   139 9(11)V9(2) NOTA 8 

                CONTEUDO_REMESSA += "341"; // CÓDIGO DO BANCO   Nº DO BANCO NA CÂMARA DE COMPENSAÇÃO  140   142  9(03) 341 
                CONTEUDO_REMESSA += "00000"; // AGÊNCIA COBRADORA AGÊNCIA ONDE O TÍTULO SERÁ COBRADO 143   147 9(05) NOTA 9 

                CONTEUDO_REMESSA += "18"; // ESPÉCIE  ESPÉCIE DO TÍTULO 148   149  X(02) NOTA 10
                CONTEUDO_REMESSA += "A"; // ACEITE  IDENTIFICAÇÃO DE TÍTULO ACEITO OU NÃO ACEITO 150   150 X(01) A=ACEITE  N=NÃO ACEITE 

                String[] data_emissao = DataHoje.DataToArrayString(DataHoje.data());
                CONTEUDO_REMESSA += data_emissao[0] + data_emissao[1] + data_emissao[2].substring(2, 4); // DATA DE EMISSÃO  DATA DA EMISSÃO DO TÍTULO 151   156 9(06) NOTA 31

                CONTEUDO_REMESSA += "00"; // INSTRUÇÃO 1  1ª INSTRUÇÃO DE COBRANÇA  157   158  X(02) NOTA 11 (05 - RECEBER CONFORME INSTRUÇÕES NO PRÓPRIO TÍTULO ) 
                CONTEUDO_REMESSA += "00"; // INSTRUÇÃO 2  2ª INSTRUÇÃO DE COBRANÇA 159   160 X(02) NOTA 11 (10 - NÃO PROTESTAR) 

                CONTEUDO_REMESSA += "0000000000000"; // JUROS DE 1 DIA   VALOR DE MORA POR DIA DE ATRASO 161   173  9(11)V9(2) NOTA 12 
                CONTEUDO_REMESSA += data_emissao[0] + data_emissao[1] + data_emissao[2].substring(2, 4); // DESCONTO ATÉ   DATA LIMITE PARA CONCESSÃO DE DESCONTO 174   179  9(06) DDMMAA

                CONTEUDO_REMESSA += "0000000000000"; // VALOR DO DESCONTO  VALOR DO DESCONTO A SER CONCEDIDO 180   192  9(11)V9(2) NOTA 13 
                CONTEUDO_REMESSA += "0000000000000"; // VALOR DO I.O.F.  VALOR DO I.O.F. RECOLHIDO P/ NOTAS SEGURO 193   205  9(11)V((2) NOTA 14 
                CONTEUDO_REMESSA += "0000000000000"; // ABATIMENTO   VALOR DO ABATIMENTO A SER CONCEDIDO 206   218 9(11)V9(2) NOTA 13  
                if (mov.getPessoa().getTipoDocumento().getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // CÓDIGO DE INSCRIÇÃO IDENTIFICAÇÃO DO TIPO DE INSCRIÇÃO/PAGADOR 219   220  9(02) 01=CPF  02=CNPJ
                } else if (mov.getPessoa().getTipoDocumento().getId() == 2) {
                    CONTEUDO_REMESSA += "02"; // CÓDIGO DE INSCRIÇÃO IDENTIFICAÇÃO DO TIPO DE INSCRIÇÃO/PAGADOR 219   220  9(02) 01=CPF  02=CNPJ
                }
                String documento_pessoa = mov.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_pessoa.length()) + documento_pessoa; // NÚMERO DE INSCRIÇÃO Nº DE INSCRIÇÃO DO PAGADOR  (CPF/CNPJ) 221   234 9(14) 
                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((mov.getPessoa().getNome() + "                              ").substring(0, 30)); // NOME NOME DO PAGADOR 235   264 X(30) NOTA 15 
                CONTEUDO_REMESSA += "          "; // BRANCOS COMPLEMENTO DE REGISTRO 265   274  X(10) NOTA 15

                PessoaEndereco pessoa_endereco = ped.pesquisaEndPorPessoaTipo(mov.getPessoa().getId(), 3);
                if (pessoa_endereco != null) {
                    String end_rua = pessoa_endereco.getEndereco().getLogradouro().getDescricao(),
                            end_descricao = pessoa_endereco.getEndereco().getDescricaoEndereco().getDescricao(),
                            end_numero = pessoa_endereco.getNumero(),
                            end_bairro = pessoa_endereco.getEndereco().getBairro().getDescricao(),
                            end_cep = pessoa_endereco.getEndereco().getCep(),
                            end_cidade = pessoa_endereco.getEndereco().getCidade().getCidade(),
                            end_uf = pessoa_endereco.getEndereco().getCidade().getUf();

                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_rua + " " + end_descricao + " " + end_numero + "                                        ").substring(0, 40)); // LOGRADOURO  RUA, NÚMERO E COMPLEMENTO DO PAGADOR 275   314 X(40) 
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_bairro + "            ").substring(0, 12)); // BAIRRO BAIRRO DO PAGADOR 315   326 X(12) 
                    CONTEUDO_REMESSA += end_cep.replace("-", "").replace(".", ""); // CEP  CEP DO PAGADOR 327   334 9(08) 
                    CONTEUDO_REMESSA += AnaliseString.normalizeUpper((end_cidade + "               ").substring(0, 15)); // CIDADE   CIDADE DO PAGADOR 335   349  X(15) 
                    CONTEUDO_REMESSA += end_uf; // ESTADO   UF DO PAGADOR 350   351  X(02) 
                } else {
                    CONTEUDO_REMESSA += "                                        "; //  LOGRADOURO  RUA, NÚMERO E COMPLEMENTO DO PAGADOR 275   314 X(40)
                    CONTEUDO_REMESSA += "            "; // BAIRRO BAIRRO DO PAGADOR 315   326 X(12) 
                    CONTEUDO_REMESSA += "        "; // CEP  CEP DO PAGADOR 327   334 9(08) 
                    CONTEUDO_REMESSA += "               "; // CIDADE   CIDADE DO PAGADOR 335   349  X(15) 
                    CONTEUDO_REMESSA += "  "; // ESTADO   UF DO PAGADOR 350   351  X(02) 
                }

                CONTEUDO_REMESSA += "                              "; // SACADOR/AVALISTA NOME DO SACADOR OU AVALISTA 352   381 X(30)  NOTA 16 
                CONTEUDO_REMESSA += "    "; // BRANCOS COMPLEMENTO DO REGISTRO 382   385 X(04)   

                CONTEUDO_REMESSA += "      "; // DATA DE MORA   DATA DE MORA  386   391 9(06) DDMMAA   
                CONTEUDO_REMESSA += "00"; // PRAZO  QUANTIDADE DE DIAS 392   393  9(02) NOTA 11 (A) 
                CONTEUDO_REMESSA += " "; // BRANCOS COMPLEMENTO DO REGISTRO 394   394  X(01) 

                CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // 395 a 400 9(006) Seqüencial de Registro
                sequencial_registro++;
                buff_writer.write(CONTEUDO_REMESSA + "\r\n");

                CONTEUDO_REMESSA = "";

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, mov);

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return null;
                }

                list_log.add("ID: " + mov.getId());
                list_log.add("Valor: " + mov.getValorString());
                list_log.add("-----------------------");
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            // footer ----------------------------------------------------------
            // -----------------------------------------------------------------
            CONTEUDO_REMESSA += "9"; // 001 a 001 9(001) Identificação do Registro Trailer: “9”
            CONTEUDO_REMESSA += "                                                                                                                                                                                                                                                                                                                                                                                                         "; // 002 a 394 X(393) Complemento do Registro: “Brancos”
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // 395 a 400 9(006) Número Seqüencial do Registro no Arquivo 

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

            return new File(destino);
        } catch (IOException | NumberFormatException e) {
            e.getMessage();
            return null;
        }
    }
}
