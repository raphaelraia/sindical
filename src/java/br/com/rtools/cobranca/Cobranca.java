package br.com.rtools.cobranca;

import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

public abstract class Cobranca {

    protected Integer id_pessoa;
    protected Double valor;
    protected Date vencimento;
    protected Boleto boleto;
    public final static String bancoDoBrasil = "001";
    public final static String caixaFederal = "104";
    public final static String itau = "341";
    public final static String bradesco = "237";
    public final static String real = "356";
    public final static String santander = "033";
    public final static String hsbc = "0";
    public final static String sicoob = "756";
    public final static int SICOB = 1;
    public final static int SINDICAL = 2;
    public final static int SIGCB = 3;
    protected List<BoletoRemessa> listaBoletoRemessa;

    public Cobranca(Integer id_pessoa, double valor, Date vencimento, Boleto boleto) {
        this.id_pessoa = id_pessoa;
        this.valor = valor;
        this.vencimento = vencimento;
        this.boleto = boleto;
    }

    public Cobranca(List<BoletoRemessa> listaBoletoRemessa) {
        this.listaBoletoRemessa = listaBoletoRemessa;
    }

    public abstract String moduloDez(String composicao);

    public abstract String moduloOnze(String composicao);

    public abstract String codigoBarras();

    public abstract String representacao();

    public abstract String codigoBanco();

    public String fatorVencimento(Date vencimento) {
        if (vencimento != null) {
            Date dataModel = DataHoje.converte("07/10/1997");
            long dias = vencimento.getTime() - dataModel.getTime();
            long total = (dias + 3600000) / 86400000;
            return Long.toString(total);
        } else {
            return "";
        }
    }

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

    public static Cobranca retornaCobranca(Integer id_pessoa, Double valor, Date vencimento, Boleto boleto) {
        Cobranca cobranca = null;
        if (boleto.getContaCobranca().getLayout().getId() == Cobranca.SINDICAL) {
            // ÚNICO CASO QUE UTILIZA O id_pessoa
            cobranca = new CaixaFederalSindical(id_pessoa, valor, vencimento, boleto);
        } else if ((boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.caixaFederal)) && (boleto.getContaCobranca().getLayout().getId() == Cobranca.SICOB)) {
            cobranca = new CaixaFederalSicob(null, valor, vencimento, boleto);
        } else if ((boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.caixaFederal)) && (boleto.getContaCobranca().getLayout().getId() == Cobranca.SIGCB)) {
            cobranca = new CaixaFederalSigCB(null, valor, vencimento, boleto);
        } else if (boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.itau)) {
            cobranca = new Itau(null, valor, vencimento, boleto);
        } else if (boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.bancoDoBrasil)) {
            cobranca = new BancoDoBrasil(null, valor, vencimento, boleto);
        } else if (boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.real)) {
            cobranca = new Real(null, valor, vencimento, boleto);
        } else if (boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.bradesco)) {
            cobranca = new Bradesco(null, valor, vencimento, boleto);
        } else if (boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.santander)) {
            cobranca = new Santander(null, valor, vencimento, boleto);
        } else if (boleto.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.sicoob)) {
            cobranca = new Sicoob(null, valor, vencimento, boleto);
        }
        return cobranca;
    }

    public static Cobranca retornaCobrancaRemessa(List<BoletoRemessa> lista_boleto_remessa) {
        Cobranca cobranca = null;
        Boleto boletox = lista_boleto_remessa.get(0).getBoleto();

        if (boletox.getContaCobranca().getLayout().getId() == Cobranca.SINDICAL) {
            // ÚNICO CASO QUE UTILIZA O id_pessoa
            cobranca = new CaixaFederalSindical(lista_boleto_remessa);
        } else if ((boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.caixaFederal)) && (boletox.getContaCobranca().getLayout().getId() == Cobranca.SICOB)) {
            //cobranca = new CaixaFederalSicob(lista_movimento);
        } else if ((boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.caixaFederal)) && (boletox.getContaCobranca().getLayout().getId() == Cobranca.SIGCB)) {
            cobranca = new CaixaFederalSigCB(lista_boleto_remessa);
        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.itau)) {
            cobranca = new Itau(lista_boleto_remessa);
        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.bancoDoBrasil)) {
            cobranca = new BancoDoBrasil(lista_boleto_remessa);
        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.real)) {
            //cobranca = new Real(lista_movimento);
        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.bradesco)) {
            //cobranca = new Bradesco(lista_movimento);
        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.santander)) {
            cobranca = new Santander(lista_boleto_remessa);
        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.sicoob)) {
            cobranca = new Sicoob(lista_boleto_remessa);
        }
        return cobranca;
    }

    public String getNossoNumeroFormatado() {
        return boleto.getBoletoComposto() + "-" + moduloOnze(boleto.getBoletoComposto());
    }

    public String getCedenteFormatado() {
        return boleto.getContaCobranca().getCodCedente().substring(0, 3) + "." + boleto.getContaCobranca().getCodCedente().substring(3) + "-"
                + moduloOnze(boleto.getContaCobranca().getContaBanco().getAgencia() + boleto.getContaCobranca().getCodCedente());
    }

    public String getAgenciaFormatada() {
        return boleto.getContaCobranca().getContaBanco().getAgencia();
    }

    public Boleto getBoleto() {
        return boleto;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }

    public abstract RespostaArquivoRemessa gerarRemessa240();

    public abstract RespostaArquivoRemessa gerarRemessa400();

    public abstract RespostaWebService registrarBoleto(String vencimentoRegistro);

    public static void voltarBoleto(Boleto boleto, String nr_ctr) {
        boleto.setDtRegistroBaixa(null);
        boleto.setAtivo(true);
        boleto.setNrCtrBoleto(nr_ctr);
        new Dao().update(boleto, true);
    }

    public Boolean testarWebService() {
        Boolean teste = false;
        if (!GenericaSessao.exists("webServiceBoletoTest")) {
            try {
                FacesContext fc = FacesContext.getCurrentInstance();
                if (fc != null) {
                    Map<String, Object> cookies = fc.getExternalContext().getRequestCookieMap();
                    Cookie cookieWebServiceBoletoTest = (Cookie) cookies.get("webServiceBoletoTest");
                    if (cookieWebServiceBoletoTest != null) {
                        teste = Boolean.parseBoolean(cookieWebServiceBoletoTest.getValue());
                    }
                }
            } catch (Exception e) {

            }
        } else {
            teste = GenericaSessao.getBoolean("webServiceBoletoTest");
        }
        return teste;
    }

    public static Boleto gerarNovoBoleto(Boleto b_antigo, String novo_vencimento) {
        Boleto bol_novo;

        MovimentoDao dbm = new MovimentoDao();
        Dao dao = new Dao();

        String nr_ctr = b_antigo.getNrCtrBoleto();

        b_antigo.setDtRegistroBaixa(DataHoje.dataHoje());
        b_antigo.setAtivo(false);
        b_antigo.setNrCtrBoleto("");

        if (!dao.update(b_antigo, true)) {
            return null;
        }

        int id_boleto = dbm.inserirBoletoNativo(b_antigo.getContaCobranca().getId(), novo_vencimento, b_antigo.getValor());

        dbm.insertMovimentoBoleto(b_antigo.getContaCobranca().getId(), b_antigo.getBoletoComposto());

        dao.openTransaction();

        if (id_boleto != -1) {

            bol_novo = (Boleto) dao.find(new Boleto(), id_boleto);

            bol_novo.setNrCtrBoleto(nr_ctr);

            List<Movimento> lm = dbm.listaMovimentoPorNrCtrBoleto(nr_ctr);

            for (int i = 0; i < lm.size(); i++) {
                lm.get(i).setDocumento(bol_novo.getBoletoComposto());
                lm.get(i).setNrCtrBoleto(bol_novo.getNrCtrBoleto());

                if (!dao.update(lm.get(i))) {
                    dao.rollback();
                    voltarBoleto(b_antigo, nr_ctr);

                    return null;
                }
            }

            if (!dao.update(bol_novo)) {
                dao.rollback();
                voltarBoleto(b_antigo, nr_ctr);

                return null;
            }

            dao.commit();
        } else {
            dao.rollback();
            voltarBoleto(b_antigo, nr_ctr);

            return null;
        }

        return bol_novo;
    }
}
