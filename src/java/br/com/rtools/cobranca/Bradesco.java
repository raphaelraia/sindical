package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.StatusRemessa;
import br.com.rtools.utilitarios.Moeda;
import java.io.File;
import java.util.Date;

public class Bradesco extends Cobranca {

    public Bradesco(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        super(id_pessoa, valor, vencimento, boleto);
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

        if ((10 - (soma % 10)) == 10) {
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
            if (j > 7) {
                j = 2;
            }
            m = composicao.substring(i - 1, i);
            soma += Integer.parseInt(m) * j;
            j++;
            i--;
        }
        if ((11 - (soma % 11)) > 9) {
            return "P";
        } else if ((soma % 11) == 0) {
            return "0";
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
        int j = 0;
        int tam = Moeda.limparPonto(Moeda.converteR$Double(valor)).length();
        while (i != (10 - tam)) { // zeros
            codigoBarras += "0";
            i++;
        }
        j = codigoBarras.length();
        codigoBarras += Moeda.limparPonto(Double.toString(valor)); // valor
        codigoBarras += boleto.getContaCobranca().getContaBanco().getAgencia();
        codigoBarras += boleto.getContaCobranca().getCarteira();
        codigoBarras += boleto.getBoletoComposto();       // nosso numero
        codigoBarras += boleto.getContaCobranca().getCodCedente();        // codigo cedente
        codigoBarras += "0";
        j = codigoBarras.length();
        codigoBarras = codigoBarras.substring(0, 4) + this.moduloOnzeDV(codigoBarras) + codigoBarras.substring(4, codigoBarras.length());
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
        return boleto.getContaCobranca().getCarteira() + "/" + boleto.getBoletoComposto() + "-" + moduloOnze(boleto.getContaCobranca().getCarteira() + boleto.getBoletoComposto());
    }

    @Override
    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente() + "-" + moduloOnze(boleto.getContaCobranca().getCodCedente());

    }

    @Override
    public String getAgenciaFormatada() {
        return boleto.getContaCobranca().getContaBanco().getAgencia() + "-" + moduloOnze(boleto.getContaCobranca().getContaBanco().getAgencia());
    }

    @Override
    public String codigoBanco() {
        return "237-2";
    }

    @Override
    public RespostaArquivoRemessa gerarRemessa240() {
        return new RespostaArquivoRemessa(null, "Configuração do Arquivo não existe");
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
