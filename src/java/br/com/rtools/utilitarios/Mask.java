package br.com.rtools.utilitarios;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Mask {

    public String getModelo(String label) {
        return getModelo(label, false);
    }

    public String getModelo(String label, boolean pesquisaInicial) {
        label = label.toLowerCase();
        // Pesquisa Inicial
        String pi = "";
        if (pesquisaInicial) {
            pi = "?";
        }
        switch (label) {
            case "cpf":
                return pi + "999.999.999-99";
            case "cnpj":
                return pi + "99.999.999/9999-99";
            case "telefone":
                return pi + "(99) 9999-9999";
            case "celular":
                return "(99) " + pi + "9999-9999?9";
            case "telefone1":
            case "telefone2":
            case "telefone3":
                return "(**) " + pi + "****-*****";
            case "cep":
                return pi + "(99) 99.999-999";
            case "cei":
                return pi + "99.999.99999/99";
            case "nascimento":
            case "data":
                return "99/99/9999";
            default:
                return "";
        }
    }

    public String getTelefone(String value) {
        if (!value.isEmpty()) {
            if (value.length() == 15) {
                return "(99) 9999?9-9999";
            }
        }
        return "(99) 9999-9999?9";
    }

    public static String getMascara(String label) {
        Mask mask = new Mask();
        return mask.getModelo(label, false);
    }

    public static String getMascaraPesquisa(String label, boolean pesquisaInicial) {
        Mask mask = new Mask();
        return mask.getModelo(label, pesquisaInicial);
    }

    public static String cep(final String cep) {
        String cepMask = "";
        if (!cep.equals("")) {
            cepMask = cep.substring(0, 2) + "." + cep.substring(2, 5) + "-" + cep.substring(5, 8);
        }
        return cepMask;
    }

    public static String cpf(final String cpf) {
        String cpfMask = "";
        try {
            if (!cpf.equals("")) {
                cpfMask = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
            }
            return cpfMask;
        } catch (Exception e) {
            return cpf;
        }
    }

    public static String pis(final String v) {
        String vMask = "";
        try {
            if (!v.equals("")) {
                vMask = v.substring(0, 3) + "." + v.substring(3, 6) + "." + v.substring(6, 9) + "-" + v.substring(9, 11);
            }
            return vMask;
        } catch (Exception e) {
            return v;
        }
    }

    public static String cnpj(final String cnpj) {
        String cnpjMask = "";
        try {
            if (!cnpj.equals("")) {
                cnpjMask = cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
            }
            return cnpjMask;
        } catch (Exception e) {
            return cnpj;
        }
    }

    public static String applyPhoneMask(final String v) {
        if (v.isEmpty()) {
            return "";
        }
        String vMask = "";
        try {
            if (v.length() == 10) {
                vMask = "(" + v.substring(0, 2) + ") " + v.substring(2, 6) + "-" + v.substring(6, 10);
            } else if (v.length() == 11) {
                vMask = "(" + v.substring(0, 2) + ") " + v.substring(2, 7) + "-" + v.substring(7, 11);
            }
            return vMask;
        } catch (Exception e) {
            return v;
        }
    }

}
