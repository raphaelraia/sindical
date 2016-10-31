package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.Remessa;
import br.com.rtools.financeiro.RemessaBanco;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.financeiro.dao.RemessaBancoDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
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
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class BancoDoBrasil extends Cobranca {

    public BancoDoBrasil(Integer id_pessoa, Float valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
    }

    public BancoDoBrasil(List<Movimento> listaMovimento) {
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

        String v_string = Moeda.limparPonto(Moeda.converteR$Float(valor));
        codigoBarras += "0000000000".substring(0, 10 - v_string.length()) + v_string;

//        int tam = Moeda.limparPonto(Moeda.converteR$Float(valor)).length();        
//        while (i != (10 - tam)) { // zeros
//            codigoBarras += "0";
//            i++;
//        }
// COBRANÇA CONVÊNIO DE 7 POSIÇÕES
        if (boleto.getBoletoComposto().length() == 17) {
            //codigoBarras += Moeda.limparPonto(Float.toString(valor)); // valor
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
    public File gerarRemessa() {
        PessoaEnderecoDao ped = new PessoaEnderecoDao();
        MovimentoDao dbmov = new MovimentoDao();

        try {
            String nome_arquivo = "ARQX" + DataHoje.hora().replace(":", "") + ".txt";
            Dao dao = new Dao();

            dao.openTransaction();

            Remessa remessa = new Remessa(-1, nome_arquivo, DataHoje.dataHoje(), DataHoje.horaMinuto());
            if (!dao.save(remessa)) {
                dao.rollback();
                return null;
            }

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

            Boleto boleto_rem = dbmov.pesquisaBoletos(listaMovimento.get(0).getNrCtrBoleto());
            String agencia = boleto_rem.getContaCobranca().getContaBanco().getAgencia();
            String conta = boleto_rem.getContaCobranca().getContaBanco().getConta().replace(".", "").replace("-", "");
            String cedente = boleto_rem.getContaCobranca().getCedente();
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

            buff_writer.write(CONTEUDO_REMESSA);
            buff_writer.newLine();

            CONTEUDO_REMESSA = "";
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            // body ------------------------------------------------------------
            // -----------------------------------------------------------------
            Juridica sindicato = (Juridica) new Dao().find(new Juridica(), 1);
            String documento_sindicato = sindicato.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");

            for (Integer i = 0; i < listaMovimento.size(); i++) {
                Movimento mov = listaMovimento.get(i);

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
                CONTEUDO_REMESSA += "0000000000000000000000000".substring(0, 25 - ("" + mov.getId()).length()) + mov.getId(); // 039 a 063 X(025) Código de Controle da Empresa 
                //CONTEUDO_REMESSA += (boleto_rem.getContaCobranca().getBoletoInicial().substring(0, 7) + "0000000000").substring(0, 17 - ("" + (Integer.valueOf(mov.getDocumento()))).length()) + ("" + (Integer.valueOf(mov.getDocumento()))); // 064 a 080 9(017) Nosso-Número 
                CONTEUDO_REMESSA += "00000000000000000".substring(0, 17 -  mov.getDocumento().length()) + mov.getDocumento(); // 064 a 080 9(017) Nosso-Número 
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
                CONTEUDO_REMESSA += "01"; // 109 a 110 9(002) Comando
                CONTEUDO_REMESSA += "0000000000".substring(0, 10 - ("" + mov.getId()).length()) + mov.getId(); // 111 a 120 X(010) Seu Número/Número do Título Atribuído pelo Cedente 

                String[] data_vencimento = DataHoje.DataToArrayString(mov.getVencimento());

                CONTEUDO_REMESSA += data_vencimento[0] + data_vencimento[1] + data_vencimento[2].substring(2, 4); // 121 a 126 9(006) Data de Vencimento 

                String valor_titulo = Moeda.converteR$Float(mov.getValor()).replace(".", "").replace(",", "");

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
                if (mov.getPessoa().getTipoDocumento().getId() == 1) {
                    CONTEUDO_REMESSA += "01"; // 219 a 220 9(002) Tipo de Inscrição do Sacado CPF
                } else if (mov.getPessoa().getTipoDocumento().getId() == 2) {
                    CONTEUDO_REMESSA += "02"; // 219 a 220 9(002) Tipo de Inscrição do Sacado CNPJ
                }
                String documento_pessoa = mov.getPessoa().getDocumento().replace("/", "").replace(".", "").replace("-", "");
                CONTEUDO_REMESSA += "00000000000000".substring(0, 14 - documento_pessoa.length()) + documento_pessoa; // 221 a 234 9(014) Número do CNPJ ou CPF do Sacado
                CONTEUDO_REMESSA += AnaliseString.normalizeUpper((mov.getPessoa().getNome() + "                                     ").substring(0, 37)); // 235 a 271 X(037) Nome do Sacado
                CONTEUDO_REMESSA += "   "; // 272 a 274 X(003) Complemento do Registro: “Brancos”

                PessoaEndereco pessoa_endereco = ped.pesquisaEndPorPessoaTipo(mov.getPessoa().getId(), 3);
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
                buff_writer.write(CONTEUDO_REMESSA);
                buff_writer.newLine();

                CONTEUDO_REMESSA = "";

                RemessaBanco remessaBanco = new RemessaBanco(-1, remessa, mov);

                if (!dao.save(remessaBanco)) {
                    dao.rollback();
                    return null;
                }
            }
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            // footer ----------------------------------------------------------
            // -----------------------------------------------------------------
            CONTEUDO_REMESSA += "9"; // 001 a 001 9(001) Identificação do Registro Trailer: “9”
            CONTEUDO_REMESSA += "                                                                                                                                                                                                                                                                                                                                                                                                         "; // 002 a 394 X(393) Complemento do Registro: “Brancos”
            CONTEUDO_REMESSA += "000000".substring(0, 6 - ("" + sequencial_registro).length()) + ("" + sequencial_registro); // 395 a 400 9(006) Número Seqüencial do Registro no Arquivo 

            buff_writer.write(CONTEUDO_REMESSA);
            buff_writer.flush();
            buff_writer.close();

            dao.commit();
            // -----------------------------------------------------------------
            // -----------------------------------------------------------------

            return new File(destino);
        } catch (IOException | NumberFormatException e) {
            e.getMessage();
            return null;
        }
    }
}
