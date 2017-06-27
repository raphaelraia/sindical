package br.com.rtools.utilitarios;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Classe que padroniza a internacionalizacao de valores monetarios
 *
 * @version 0.1
 * @see java.util.Locale
 * @see java.text.DecimalFormat
 * @see java.text.DecimalFormatSymbols
 */
public final class Moeda {

    /**
     * Simbolos especificos do Dolar Americano
     */
    private static final DecimalFormatSymbols DOLAR = new DecimalFormatSymbols(Locale.US);
    /**
     * Mascara de dinheiro para Dolar Americano
     */
    public static final DecimalFormat DINHEIRO_DOLAR = new DecimalFormat("###,###,##0.00", DOLAR);
    /**
     * Simbolos especificos do Euro
     */
    private static final DecimalFormatSymbols EURO = new DecimalFormatSymbols(Locale.GERMANY);
    /**
     * Mascara de dinheiro para Euro
     */
    public static final DecimalFormat DINHEIRO_EURO = new DecimalFormat("� ###,###,##0.00", EURO);
    /**
     * Locale Brasileiro
     */
    private static final Locale BRAZIL = new Locale("pt", "BR");
    /**
     * S�mbolos especificos do Real Brasileiro
     */
    private static final DecimalFormatSymbols REAL = new DecimalFormatSymbols(BRAZIL);
    /**
     * Mascara de dinheiro para Real Brasileiro
     */
    public static final DecimalFormat DINHEIRO_REAL = new DecimalFormat("###,###,##0.00", REAL);

    /**
     * Mascara texto com formatacao monetaria
     *
     * @param valor Valor a ser mascarado
     * @param moeda Padrao monetario a ser usado
     * @return Valor mascarado de acordo com o padrao especificado Ex: String m
     * = Moeda.mascaraDinheiro(100,Moeda.DINHEIRO_REAL); m = R$ 100.00
     */
    public static String mascaraDinheiro(double valor, DecimalFormat moeda) {
        return moeda.format(valor);
    }

    //Converte Campo Real para campo Dolar
    public static double converteUS$(String $dolar) {
        return converteUS$($dolar, 2);
    }

    public static double converteUS$(String $dolar, Integer decimal) {
        if ($dolar == null || $dolar.isEmpty()) {
            $dolar = "0,00";
        }
        
        BigDecimal num = new BigDecimal(converteStringToDouble($dolar));
        try {
            if (decimal == null){
                decimal = 2;
            }
            
        } catch (Exception e) {
            return new Double(0);
        }
            
        return num.setScale(decimal, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }

    public static String converteR$(String $dolar) {
        //$dolar = $dolar.replaceAll("[^0-9]", "");
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return converteR$($dolar, 0);
    }

    public static String converteR$(String $dolar, Integer decimal) {
        //$dolar = $dolar.replaceAll("[^0-9]", "");
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if (decimal != null && decimal > 2) {
            DecimalFormat df = new DecimalFormat("###,###,##0.0000", REAL);
            if ($dolar.length() >= 3) {
                String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
                if (!wponto.equals(",")) {
                    $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), df);
                }
            } else {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), df);
            }
        } else if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return $dolar;
    }

    public static String converteR$Double(double valor) {
        return converteR$Double(valor, null);
    }

    public static String converteR$Double(double valor, Integer decimal) {
        String $dolar = Double.toString(valor);
        if ($dolar == null || $dolar.isEmpty()) {
            return "0,00";
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if (decimal != null && decimal > 2) {
            DecimalFormat df = new DecimalFormat("###,###,##0.0000", REAL);
            if ($dolar.length() >= 3) {
                String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
                if (!wponto.equals(",")) {
                    $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), df);
                }
            } else {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), df);
            }
        } else if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                DecimalFormat moeda = new DecimalFormat("###,###,##0.00", REAL);
                // String d = moeda.format(Double.parseDouble($dolar), moeda);
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return $dolar;
    }

    public static Double converteDoubleR$Double(double valor) {
        String $dolar = Double.toString(valor);
        if ($dolar == null || $dolar.isEmpty()) {
            return (double) 0;
        }
        $dolar = Moeda.substituiVirgula($dolar);
        if ($dolar.length() >= 3) {
            String wponto = $dolar.substring($dolar.trim().length() - 3, $dolar.trim().length() - 2);
            if (!wponto.equals(",")) {
                $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
            }
        } else {
            $dolar = Moeda.mascaraDinheiro(Double.parseDouble($dolar), Moeda.DINHEIRO_REAL);
        }
        return Moeda.substituiVirgulaDouble($dolar);
    }

    public static String substituiVirgula(String v) {
        if (v.indexOf(",") == -1) {
            return v;
        }
        v = v.replace(".", "");
        v = v.replace(",", ".");
        return v;
    }

    public static double substituiVirgulaDouble(String v) {
        if (v.indexOf(",") == -1) {
            return Double.parseDouble(v);
        }
        v = v.replace(".", "");
        v = v.replace(",", ".");
        return Double.parseDouble(v);
    }

    public static Double soma(Double[] d) {
        try {
            double t = 0;
            for (int i = 0; i < d.length; i++) {
                t += (double) d[i];
            }
            return t;
        } catch (Exception e) {
            return (double) 0;
        }
    }

    public static String incremento(String a, String b) { // a = boleto somado   ,  b = 1
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
        BigDecimal result = aBig.add(bBig);
        /*  BigDecimal potencia = null; // NÃO APAGAR
         if ((aBig.toString().length() - result.toString().length()) > 1){
         potencia = bBig.scaleByPowerOfTen(result.toString().length() -1);
         }else{
         potencia = bBig.scaleByPowerOfTen((result.toString().length() -1) * -1);
         result = result.multiply(bBig.scaleByPowerOfTen((result.toString().length() -1) * -1));
         }*/

        //return result.subtract(potencia).toString();
        return result.toString().substring(1);
    }

    // NOVOS METODOS CLAUDEMIR --------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    public static double soma(double a, double b) {
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
        //return aBig.add(bBig).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return aBig.add(bBig).doubleValue();
    }

    public static double subtracao(double a, double b) {
        BigDecimal aBig = new BigDecimal(a);
        BigDecimal bBig = new BigDecimal(b);
//        BigDecimal aBig = new BigDecimal(753.71);
//        BigDecimal bBig = new BigDecimal(0.02);
        //return aBig.subtract(bBig).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return aBig.subtract(bBig).doubleValue();
    }

    public static double multiplicar(double a, double b) {
        BigDecimal aBig = new BigDecimal(a, new MathContext(2));
        BigDecimal bBig = new BigDecimal(b, new MathContext(2));
        //return aBig.multiply(bBig).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        return aBig.multiply(bBig).doubleValue();
    }

    public static double divisao(double a, double divisor) {
        try {
            BigDecimal aBig = new BigDecimal(a, new MathContext(2));
            BigDecimal bBig = new BigDecimal(divisor, new MathContext(2));
            //return aBig.divide(bBig, new MathContext(100)).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
            return aBig.divide(bBig, new MathContext(100)).doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String converteDoubleToString(Double d) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("pt", "BR"));
//        // Formato com sinal de menos -5.000,00
//        DecimalFormat df1 = new DecimalFormat ("#,##0.00", dfs);
        // Formato com parêntese (5.000,00)
        DecimalFormat df2 = new DecimalFormat("#,##0.00;#,##0.00", dfs);
        //d = -5000.00;
//        s = df1.format (d); 
//        System.out.println (s); // imprime -5.000,00
        String s = df2.format(d);
        return s;
    }

    public static Double converteStringToDouble(String s) {
        try {
            DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols sfs = new DecimalFormatSymbols();
            sfs.setDecimalSeparator(',');
            df.setDecimalFormatSymbols(sfs);
            return df.parse(s).doubleValue();
        } catch (Exception e) {
            return new Double(0);
        }
    }

    // FIM NOVOS METODOS CLAUDEMIR --------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------
    
    public static String limparPonto(String valor) {
        valor = converteR$(valor);
        valor = substituiVirgula(valor);
        int i = 0;
        String result = "";
        while (i < valor.length()) {
            if (valor.charAt(i) != '.') {
                result += valor.charAt(i);
            } else if (((i + 2) > valor.length()) && (valor.charAt(i + 1) == '0')) {
                i++;
            }
            i++;
        }
        return result;
    }

    public static String limparVirgula(String valor) {
        int i = 0;
        String result = "";
        while (i < valor.length()) {
            if (valor.charAt(i) != ',') {
                result += valor.charAt(i);
            } else if ((i + 2) > valor.length()) {
                result += valor.charAt(i + 1);
                result += valor.charAt(i + 2);
                break;
            }
            i++;
        }
        return result;
    }

    public static String percentualDoValor(String valorFixo, String valorCalculo) {
        double v1 = Moeda.converteUS$(valorFixo);
        double v2 = Moeda.converteUS$(valorCalculo);
        return Double.toString((v2 / v1) * 100);
    }

    public static String valorDoPercentual(String valorFixo, String percentual) {
        
        double v = Moeda.converteUS$(valorFixo) - (Moeda.converteUS$(percentual) / 100) * Moeda.converteUS$(valorFixo);
        
        return converteDoubleToString(v);
    }

    public static Double percentualDoValor(Double valorFixo, Double valorCalculo) {
        double v1 = valorFixo;
        double v2 = valorCalculo;
        return Moeda.converteUS$(Double.toString((v2 / v1) * 100));
    }

    public static Double valorDoPercentual(Double valorFixo, Double percentual) {
        
        double v = valorFixo - (percentual / 100) * valorFixo;
        
        return v;
    }

}
