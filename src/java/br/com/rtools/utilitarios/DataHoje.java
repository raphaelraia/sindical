package br.com.rtools.utilitarios;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class DataHoje {

    /*
                String dia = date.substring(8, 10);
            String mes = date.substring(5, 7);
            String ano = date.substring(0, 4);
     */
    public static Integer dia() {
        Date dateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return Integer.parseInt(dateFormat.format(dateTime).substring(8, 10));
    }

    public static Integer mes() {
        Date dateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return Integer.parseInt(dateFormat.format(dateTime).substring(5, 7));
    }

    public static Integer ano() {
        Date dateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return Integer.parseInt(dateFormat.format(dateTime).substring(0, 4));
    }

    public static Integer dia(String data) {
        return Integer.parseInt(data.substring(8, 10));
    }

    public static Integer mes(String data) {
        return Integer.parseInt(data.substring(5, 7));
    }

    public static Integer ano(String data) {
        return Integer.parseInt(data.substring(0, 4));
    }

    public static Date dataHoje() {
        Date dateTime = new Date();
        return dateTime;
    }

    public static java.sql.Date dataHojeSQL() {
        Date dateTime = new Date();
        java.sql.Date sqlDate = new java.sql.Date(dateTime.getTime());
        return sqlDate;
    }

    public static String data() {
        Date dateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String a = dateFormat.format(dateTime);
        return a;
    }

    public static String referencia() {
        Date dateTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
        String a = dateFormat.format(dateTime);
        return a;
    }

    public static String livre(Date date, String format) {
        if (date == null) {
            return data();
        }
        if (format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        String a = dateFormat.format(date);
        return a;
    }

    public static String converteData(Date data) {
        if (data != null) {
            String a = data.toString();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String b = dateFormat.format(data);
            return b;
        } else {
            return "";
        }
    }

    public String converteDataMB(Date data) {
        return converteData(data);
    }

    public static String converteHora(Date data) {
        if (data != null) {
            String a = data.toString();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            String b = dateFormat.format(data);
            return b;
        } else {
            return "";
        }
    }

    public static String converteData(String data) {
        if (!data.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(data);
        } else {
            return "";
        }
    }

    public String converteDataMB(String data) {
        return converteData(data);
    }

    public Date converteMB(String data) {
        return converte(data);
    }

    public static Date converte(String data) {
        if (data != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                return dateFormat.parse(data);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Date converteDataHora(String dataHora) {
        if (dataHora != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                return dateFormat.parse(dataHora);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Date converteDataHora(String data, String hora) {
        if (data != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    return dateFormat.parse(data + " " + hora);
                } catch (Exception e) {
                    return dateFormat.parse(data + " " + hora + ":00");
                }
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Date converteDateSql(java.sql.Date date) {
        String data = date.toString();
        if (data != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return dateFormat.parse(data);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Date converteDateSqlToDate(String date) {
        if (date != null) {
            date = date.replace("[", "");
            date = date.replace("]", "");
            String dia = date.substring(8, 10);
            String mes = date.substring(5, 7);
            String ano = date.substring(0, 4);
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                return dateFormat.parse(dia + "/" + mes + "/" + ano);
            } catch (ParseException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Date converteStringToSqlDate(String date) {
        if (date != null) {
            date = date.replace("/", "");
            try {
                String dia = date.substring(0, 2);
                String mes = date.substring(2, 4);
                String ano = date.substring(4, 8);

                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Date parsed = format.parse(ano + mes + dia);
                java.sql.Date sql = new java.sql.Date(parsed.getTime());
                return sql;
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            return null;
        }
        return null;
    }

    public static Date converteStringToSqlDate(String date, String time) {
        if (date != null) {
            date = date.replace("/", "");
            try {
                String dia = date.substring(0, 2);
                String mes = date.substring(2, 4);
                String ano = date.substring(4, 8);

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsed = format.parse(ano + mes + dia);
                java.sql.Date sql = new java.sql.Date(parsed.getTime());
                return sql;
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            return null;
        }
        return null;
    }

    public static String dataReferencia(String data) {
        //String referencia = "";
        DataHoje dt = new DataHoje();
        String referencia = dt.decrementarMeses(1, data);
        return referencia.substring(3);
    }

    public static String converteDataParaReferencia(String data) {
        try {
            return data.substring(3);
        } catch (Exception e) {
            return "";
        }
    }

    public static String converteDataParaReferencia(Date date) {
        String data = converteData(date);
        try {
            return data.substring(3);
        } catch (Exception e) {
            return "";
        }
    }

    public static String[] ArrayDataHoje() {
        String[] result = new String[3];
        result[0] = DataHoje.data().substring(0, 2);
        result[1] = DataHoje.data().substring(3, 5);
        result[2] = DataHoje.data().substring(6, 10);
        return result;
    }

    public static String[] DataToArray(String data) {
        String[] result = new String[3];
        result[0] = data.substring(0, 2);
        result[1] = data.substring(3, 5);
        result[2] = data.substring(6, 10);
        return result;
    }

    public static String[] DataToArray(Date data) {
        String[] result = new String[3];
        result[0] = converteData(data).substring(0, 2);
        result[1] = converteData(data).substring(3, 5);
        result[2] = converteData(data).substring(6, 10);
        return result;
    }

    public static int[] ArrayDataHojeInt() {
        int[] result = new int[3];
        result[0] = Integer.parseInt(DataHoje.data().substring(0, 2));
        result[1] = Integer.parseInt(DataHoje.data().substring(3, 5));
        result[2] = Integer.parseInt(DataHoje.data().substring(6, 10));
        return result;
    }

    public static int[] DataToArrayInt(String data) {
        int[] result = new int[3];
        result[0] = Integer.parseInt(data.substring(0, 2));
        result[1] = Integer.parseInt(data.substring(3, 5));
        result[2] = Integer.parseInt(data.substring(6, 10));
        return result;
    }

    public static int[] DataToArrayInt(Date data) {
        int[] result = new int[3];
        result[0] = Integer.parseInt(converteData(data).substring(0, 2));
        result[1] = Integer.parseInt(converteData(data).substring(3, 5));
        result[2] = Integer.parseInt(converteData(data).substring(6, 10));
        return result;
    }

    public static String[] DataToArrayString(String data) {
        String[] result = new String[3];
        result[0] = data.substring(0, 2);
        result[1] = data.substring(3, 5);
        result[2] = data.substring(6, 10);
        return result;
    }

    public static String[] DataToArrayString(Date data) {
        String[] result = new String[3];
        String dataS = converteData(data);
        result[0] = dataS.substring(0, 2);
        result[1] = dataS.substring(3, 5);
        result[2] = dataS.substring(6, 10);
        return result;
    }

    public static int qtdeDiasDoMes(int mes, int ano) {
        int dias = 0;
        if ((mes == 1)
                || (mes == 3)
                || (mes == 5)
                || (mes == 7)
                || (mes == 8)
                || (mes == 10)
                || (mes == 12)) {
            return 31;
        } else if ((mes == 4)
                || (mes == 6)
                || (mes == 9)
                || (mes == 11)) {
            return 30;
        } else if (mes == 2) {
            if (isBisexto(ano)) {
                return 29;
            } else {
                return 28;
            }
        }

        return dias;
    }

    public static boolean isBisexto(int ano) {
        if ((ano % 4) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int getMaxDiasFevereiro(Integer ano) {
        Calendar cal = (Calendar) Calendar.getInstance().clone();
        cal.set(Calendar.YEAR, ano);
        cal.set(Calendar.MONTH, 1);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int calculoDosDiasInt(Date dInicial, Date dFinal) {
        try {
            return Integer.parseInt(Long.toString(calculoDosDias(dInicial, dFinal)));
        } catch (Exception e) {
            return 0;
        }
    }

    public static long calculoDosDias(Date dInicial, Date dFinal) {
        DataHoje.converteDataParaInteger(DataHoje.converteData(dInicial));
        DataHoje.converteDataParaInteger(DataHoje.converteData(dFinal));
        if (((dInicial != null) && (dFinal != null))
                && (DataHoje.converteDataParaInteger(DataHoje.converteData(dInicial)) < DataHoje.converteDataParaInteger(DataHoje.converteData(dFinal)))) {
            long dias = dFinal.getTime() - dInicial.getTime();
            // CORRIGE DATAS COM HORÁRIO DE VERÃO -- 3600000
            long total = (dias + 3600000) / 86400000;
            //long total = (dias) / 86400000;
            return total;
        } else {
            return 0;
        }
    }

    public static boolean validaReferencias(String refInicial, String refFinal) {
        if (!refInicial.equals("") && !refFinal.equals("")) {
            String d1 = refInicial.substring(3, 7) + refInicial.substring(0, 2);
            int INTrefInicial = Integer.valueOf(d1);
            String d2 = refFinal.substring(3, 7) + refFinal.substring(0, 2);
            int INTrefFinal = Integer.valueOf(d2);
            if (INTrefInicial <= INTrefFinal) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean integridadeReferencia(String referencia) {
        if (referencia.length() != 7) {
            return false;
        }
        if ((AnaliseString.conteudoData(referencia.substring(0, 2) + referencia.substring(3, 7)))
                && (referencia.substring(2, 3).equals("/"))
                && (Integer.parseInt(referencia.substring(0, 2)) < 13)
                && (Integer.parseInt(referencia.substring(0, 2)) > 0)
                && (Integer.parseInt(referencia.substring(3, 7)) > 1822)
                && (Integer.parseInt(referencia.substring(3, 7)) < 9999)) {
            return true;
        }
        return false;
    }

    public static Boolean validaReferencia(String referencia) {
        return new DataHoje().integridadeReferencia(referencia);
    }

    public static String converteReferenciaVencimento(String referencia, String diaVencimento, String tipo) {
        String vencimento = DataHoje.data();
        DataHoje data = new DataHoje();
        if ((data.integridadeReferencia(referencia))
                && (tipo.length() == 1)) {
            if (diaVencimento.length() == 1) {
                diaVencimento = "0" + diaVencimento;
            }
            int[] dataI = DataHoje.DataToArrayInt(data.incrementarMeses(1, diaVencimento + "/" + referencia));
            if (Integer.parseInt(diaVencimento) < DataHoje.DataToArrayInt(vencimento)[0]) {
                vencimento = vencimento.substring(0, 2) + "/" + referencia;
            } else if (qtdeDiasDoMes(dataI[1], dataI[2]) >= Integer.parseInt(diaVencimento)) {
                vencimento = diaVencimento + "/" + referencia;
            } else {
                vencimento = qtdeDiasDoMes(dataI[1], dataI[2]) + "/" + referencia;
            }
            if (tipo.equals("E")) {
                vencimento = data.incrementarMeses(1, vencimento);
            }

        }
        return vencimento;
    }

    public String incrementarMeses(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                int c = 0;
                int[] d = DataHoje.DataToArrayInt(data);
                if ((d[1] + qtd) > 12) {
                    int tmp = (d[1] + qtd);
                    while (tmp > 12) {
                        tmp -= 12;
                        c++;
                    }
                    d[2] += c;
                    d[1] = tmp;
                } else {
                    d[1] += qtd;
                }
                if (d[0] > qtdeDiasDoMes(d[1], d[2])) {
                    d[0] = qtdeDiasDoMes(d[1], d[2]);
                }
                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String decrementarMeses(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                int c = 0;
                int[] d = DataHoje.DataToArrayInt(data);
                if ((d[1] - qtd) < 1) {
                    int tmp = (d[1] - qtd);
                    while (tmp < 1) {
                        tmp += 12;
                        c++;
                    }
                    d[2] -= c;
                    d[1] = tmp;
                } else {
                    d[1] -= qtd;
                }
                if (d[0] > qtdeDiasDoMes(d[1], d[2])) {
                    d[0] = qtdeDiasDoMes(d[1], d[2]);
                }
                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String decrementarSemanas(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                int[] d = DataHoje.DataToArrayInt(data);
                int[] mesA = DataToArrayInt(decrementarMeses(1, data));
                int diasMesA = qtdeDiasDoMes(mesA[1], mesA[2]);
                if ((d[0] - (qtd * 7)) < 1) {
                    int tmp = (qtd * 7);
                    diasMesA += d[0];
                    tmp = diasMesA - tmp;
                    d[1]--;
                    if (d[1] < 1) {
                        d[1] = 12;
                        d[2]--;
                    }
                    diasMesA = qtdeDiasDoMes(mesA[1], mesA[2]);
                    while (tmp > diasMesA) {
                        tmp = diasMesA - tmp;
                        d[1]--;
                        if (d[1] < 1) {
                            d[1] = 12;
                            d[2]--;
                        }
                        diasMesA = qtdeDiasDoMes(mesA[1], mesA[2]);
                    }
                    d[0] = tmp;
                } else {
                    d[0] -= (qtd * 7);
                }

                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String incrementarSemanas(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                int[] d = DataHoje.DataToArrayInt(data);
                int dias = qtdeDiasDoMes(d[1], d[2]);
                if ((d[0] + (qtd * 7)) > dias) {
                    int tmp = (qtd * 7) + d[0];
                    while (tmp >= dias) {
                        tmp -= dias;
                        d[1]++;
                        if (d[1] > 12) {
                            d[1] = 1;
                            d[2]++;
                        }
                        dias = qtdeDiasDoMes(d[1], d[2]);
                    }
                    d[0] = tmp;
                } else {
                    d[0] += (qtd * 7);
                }
                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String incrementarDias(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                int[] d = DataHoje.DataToArrayInt(data);
                int dias = qtdeDiasDoMes(d[1], d[2]);
                if (qtd > dias) {
                    while (qtd >= dias) {
                        qtd -= dias;
                        d[1]++;
                        if (d[1] > 12) {
                            d[1] = 1;
                            d[2]++;
                        }
                        dias = qtdeDiasDoMes(d[1], d[2]);
                    }
                    d[0] += qtd;
                } else {
                    d[0] += qtd;
                }
                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String decrementarDias(int qtd, String data) {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        calendar.setTime(DataHoje.converte(data));
        calendar.add(GregorianCalendar.DATE, -qtd);
        return dt.format(calendar.getTime());
    }

    public String incrementarAnos(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                if (qtd <= 0) {
                    qtd = 1;
                }
                int[] d = DataHoje.DataToArrayInt(data);
                d[2] = d[2] + qtd;
                if ((d[1] == 2) && ((d[0] == 28) || (d[0] == 29))) {
                    if (isBisexto(d[2])) {
                        d[0] = 29;
                    } else {
                        d[0] = 28;
                    }
                }
                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String incrementarMesesUltimoDia(int qtd, String data) {
        if (isDataValida(data)) {
            try {
                int c = 0;
                int[] d = DataHoje.DataToArrayInt("32/" + data.substring(3, 10));
                if ((d[1] + qtd) > 12) {
                    int tmp = (d[1] + qtd);
                    while (tmp > 12) {
                        tmp -= 12;
                        c++;
                    }
                    d[2] += c;
                    d[1] = tmp;
                } else {
                    d[1] += qtd;
                }

                if (d[0] > qtdeDiasDoMes(d[1], d[2])) {
                    d[0] = qtdeDiasDoMes(d[1], d[2]);
                }

                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static boolean menorData(String menor, String data) {
        if (!isDataValida(menor) || !isDataValida(data)) {
            return false;
        }
        String[] d1 = DataToArrayString(menor);
        String[] d2 = DataToArrayString(data);
        boolean is = Integer.parseInt(d1[2] + d1[1] + d1[0]) < Integer.parseInt(d2[2] + d2[1] + d2[0]);
        return is;

    }

    public static boolean menorData(Date menor, Date data) {
        return menorData(converteData(menor), converteData(data));
    }

    public static boolean maiorData(String maior, String data) {
        if (!isDataValida(maior) || !isDataValida(data)) {
            return false;
        }
        String[] d1 = DataToArrayString(maior);
        String[] d2 = DataToArrayString(data);
        boolean is = Integer.parseInt(d1[2] + d1[1] + d1[0]) > Integer.parseInt(d2[2] + d2[1] + d2[0]);
        return is;
    }

    public static boolean maiorData(Date maior, Date data) {
        return maiorData(converteData(maior), converteData(data));
    }

    public static boolean igualdadeData(String data1, String data2) {
        if (!isDataValida(data1) || !isDataValida(data2)) {
            return false;
        }
        String[] d1 = DataToArrayString(data1);
        String[] d2 = DataToArrayString(data2);
        boolean is = Integer.parseInt(d1[2] + d1[1] + d1[0]) == Integer.parseInt(d2[2] + d2[1] + d2[0]);
        return is;
    }

    public static boolean igualdadeData(Date data1, Date data2) {
        return igualdadeData(converteData(data1), converteData(data2));
    }

    public String mascararData(String data) {
        return DataHoje.converteData(DataHoje.converte(data));
    }

    public static String hora() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        return sdf.format(gc.getTime());
    }

    public static String incrementarHoraAtual(int minutos) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String result;
        if (minutos > 0) {
            gc.add(Calendar.MINUTE, minutos);
            result = sdf.format(gc.getTime());
        } else {
            result = sdf.format(gc.getTime());
        }
        return result;
    }

    public static String incrementarHora(String hora, int minutos) {
        String result = "";
        if (!hora.isEmpty() && hora.length() == 5) {
            GregorianCalendar gc = new GregorianCalendar();

            gc.set(0, 0, 0, Integer.valueOf(hora.substring(0, 2)), Integer.valueOf(hora.substring(3, 5)));

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            if (minutos > 0) {
                gc.add(Calendar.MINUTE, minutos);
                result = sdf.format(gc.getTime());
            } else {
                result = sdf.format(gc.getTime());
            }
        }
        return result;
    }

    public static Date incrementarMinuto(Date date, Integer minutos) {
        try {
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);
            instance.add(Calendar.MINUTE, minutos);
            return instance.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static String horaSemPonto() {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        return sdf.format(gc.getTime());
    }

    public static String horaMinuto() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        return sdf.format(gc.getTime());
    }

    public static String colocarBarras(String data) {
        if (data.isEmpty()) {
            return "";
        }
        String d;
        String m;
        String a;
        try {
            d = data.substring(0, 2);
        } catch (Exception e) {
            d = "01";
        }
        try {
            m = data.substring(2, 4);
        } catch (Exception e) {
            m = "01";
        }
        try {
            a = data.substring(4, 8);
        } catch (Exception e) {
            a = "1900";
        }
        String newDate = d + "/" + m + "/" + a;
        if (!isDataValida(newDate)) {
            return "01/01/1900";
        }
        return newDate;
    }

    public static int converteDataParaInteger(String data) {
        if (isDataValida(data)) {
            return Integer.parseInt(DataHoje.DataToArrayString(data)[2] + DataHoje.DataToArrayString(data)[1] + DataHoje.DataToArrayString(data)[0]);
        }
        return 0;
    }

    public static int converteDataParaRefInteger(String data) {
        try {
            String part1 = data.substring(0, 2);
            String part2 = data.substring(3, 7);
            int novaData = Integer.parseInt(part2 + part1);
            return novaData;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     *
     * @param dataMaior
     * @param dataMenor
     * @return String[] {[0]dias, [1]meses, [2]anos}
     */
    public Integer[] diferencaEntreDatas(String dataMaior, String dataMenor) {
        try {
            Integer[] integers = new Integer[3];
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            GregorianCalendar maior = new GregorianCalendar();
            maior.setTime(dateFormat.parse(dataMenor));

            GregorianCalendar menor = new GregorianCalendar();
            menor.setTime(dateFormat.parse(dataMaior));

            GregorianCalendar dif = new GregorianCalendar();
            dif.setTimeInMillis(maior.getTimeInMillis() - menor.getTimeInMillis());
            integers[0] = dif.get(GregorianCalendar.DAY_OF_MONTH);
            integers[1] = dif.get(GregorianCalendar.MONTH) + 1;
            integers[2] = maior.get(GregorianCalendar.YEAR) - menor.get(GregorianCalendar.YEAR);
//            System.out.println(maior.get(GregorianCalendar.YEAR));
//            System.out.println(menor.get(GregorianCalendar.YEAR));
//            System.out.println("Diferença: " + (maior.get(GregorianCalendar.YEAR) - menor.get(GregorianCalendar.YEAR)) + " ano(s), "
//                    + (dif.get(GregorianCalendar.MONTH) + 1) + " mes(es), "
//                    + dif.get(GregorianCalendar.DAY_OF_MONTH) + " dia(s)");
            return integers;
        } catch (ParseException ex) { //Lança exceção caso a data informada não esteja no formato "dd/MM/yyyy"    
            ex.printStackTrace(System.err);
            return null;
        }
    }

    public boolean faixaCincoAnosApos(String data) {
        int dataHoje = DataHoje.converteDataParaRefInteger(DataHoje.data());
        int dataAntes = DataHoje.converteDataParaRefInteger(decrementarMeses(60, DataHoje.data()));
        int dataM = DataHoje.converteDataParaRefInteger(data);
        if ((dataM > dataAntes) && (dataM <= dataHoje)) {
            return true;
        } else {
            return false;
        }
    }

    public int calcularIdade(String dataNascimento) {
        int idade = 0;
        if (isDataValida(dataNascimento)) {
            int dN = converteDataParaInteger(dataNascimento);
            int dH = converteDataParaInteger(data());
            if (dN >= dH) {
                return 0;
            }
            String dataHoje = DataHoje.data();
            int[] dataH = DataHoje.DataToArrayInt(dataHoje);
            int[] dataN = DataHoje.DataToArrayInt(dataNascimento);
            idade = dataH[2] - dataN[2];
            int[] novaData = DataHoje.DataToArrayInt(incrementarAnos(idade, dataNascimento));
            if (dataH[1] < novaData[1]) {
                idade--;
            } else if (dataH[1] == novaData[1]) {
                if (dataH[0] < dataN[0]) {
                    idade--;
                }
            }
        }
        return idade;
    }

    public int calcularIdade(Date data) {
        if (data == null) {
            return 0;
        }
        return calcularIdade(DataHoje.converteData(data));
    }

    public static int quantidadeMeses(Date dataInicial, Date dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            return 0;
        }
        final double MES_EM_MILISEGUNDOS = 30.0 * 24.0 * 60.0 * 60.0 * 1000.0;
        int numeroDeMeses = (int) (double) ((dataFinal.getTime() - dataInicial.getTime()) / MES_EM_MILISEGUNDOS);
        if (numeroDeMeses <= 0) {
            return 0;
        }
        return numeroDeMeses;
    }

    public static int quantidadeMeses(String dataInicial, String dataFinal) {
        return quantidadeMeses(converte(dataInicial), converte(dataFinal));
    }

    public static String validaHora(String hora) {
        if (hora.isEmpty() || hora.equals("__:__")) {
            return "";
        }
        int n1;
        int n2;
        if (hora.length() == 1) {
            hora = "0" + hora + ":00";
        }

        if (hora.length() == 2) {
            if ((Integer.parseInt(hora) >= 0) && (Integer.parseInt(hora) <= 23)) {
                hora = hora + ":00";
            } else {
                hora = "";
            }
        } else if (hora.length() == 3) {
            n1 = Integer.parseInt(hora.substring(0, 2));
            String pontos = hora.substring(2, 3);

            if (((n1 >= 0) && (n1 <= 23)) && pontos.equals(":")) {
                hora = hora + "00";
            } else {
                hora = "";
            }
        } else if (hora.length() == 4) {
            n1 = Integer.parseInt(hora.substring(0, 2));
            n2 = Integer.parseInt(hora.substring(3, 4));
            String pontos = hora.substring(2, 3);

            if ((pontos.equals(":")) && ((n1 >= 0) && (n1 <= 23)) && ((n2 >= 0) && (n2 <= 5))) {
                hora = hora + "0";
            } else {
                hora = "";
            }
        } else if (hora.length() == 5) {
            n1 = Integer.parseInt(hora.substring(0, 2));
            n2 = Integer.parseInt(hora.substring(3, 5));
            String pontos = hora.substring(2, 3);

            if (!(((n1 >= 0) && (n1 <= 23)) && ((n2 >= 0) && (n2 <= 59)) && (pontos.equals(":")))) {
                hora = "";
            }
        }
        return hora;
    }

    public static String dataExtenso(String data) {
        return dataExtenso(data, 0);
    }

    /**
     * Tipo
     *
     * @param data
     * @param tipo 0 = dia extenso/mes extenso/ano extenso/ ; 1 = mes/ano ; 2 -
     * ano; 3 - dia/mes extenso/ano extenso/
     * @return
     */
    public static String dataExtenso(String data, int tipo) {
        if (!isDataValida(data)) {
            return "";
        }
        String extenso;
        try {
            String dia = data.substring(0, 2);
            String mes = data.substring(3, 5);
            String ano = data.substring(6, 10);
            if (tipo != 3) {
                switch (Integer.parseInt(dia)) {
                    case 1:
                        dia = "Primeiro";
                        break;
                    case 2:
                        dia = "Dois";
                        break;
                    case 3:
                        dia = "Três";
                        break;
                    case 4:
                        dia = "Quatro";
                        break;
                    case 5:
                        dia = "Cinco";
                        break;
                    case 6:
                        dia = "Seis";
                        break;
                    case 7:
                        dia = "Sete";
                        break;
                    case 8:
                        dia = "Oito";
                        break;
                    case 9:
                        dia = "Nove";
                        break;
                    case 10:
                        dia = "Dez";
                        break;
                    case 11:
                        dia = "Onze";
                        break;
                    case 12:
                        dia = "Doze";
                        break;
                    case 13:
                        dia = "Treze";
                        break;
                    case 14:
                        dia = "Quatorze";
                        break;
                    case 15:
                        dia = "Quinze";
                        break;
                    case 16:
                        dia = "Dezesseis";
                        break;
                    case 17:
                        dia = "Dezessete";
                        break;
                    case 18:
                        dia = "Dezoito";
                        break;
                    case 19:
                        dia = "Dezenove";
                        break;
                    case 20:
                        dia = "Vinte";
                        break;
                    case 21:
                        dia = "Vinte e Um";
                        break;
                    case 22:
                        dia = "Vinte e Dois";
                        break;
                    case 23:
                        dia = "Vinte e Três";
                        break;
                    case 24:
                        dia = "Vinte e Quatro";
                        break;
                    case 25:
                        dia = "Vinte e Cinco";
                        break;
                    case 26:
                        dia = "Vinte e Seis";
                        break;
                    case 27:
                        dia = "Vinte e Sete";
                        break;
                    case 28:
                        dia = "Vinte e Oito";
                        break;
                    case 29:
                        dia = "Vinte e Nove";
                        break;
                    case 30:
                        dia = "Trinta";
                        break;
                    case 31:
                        dia = "Trinta e Um";
                        break;
                }
            }
            switch (Integer.parseInt(mes)) {
                case 1:
                    mes = "Janeiro";
                    break;
                case 2:
                    mes = "Fevereiro";
                    break;
                case 3:
                    mes = "Março";
                    break;
                case 4:
                    mes = "Abril";
                    break;
                case 5:
                    mes = "Maio";
                    break;
                case 6:
                    mes = "Junho";
                    break;
                case 7:
                    mes = "Julho";
                    break;
                case 8:
                    mes = "Agosto";
                    break;
                case 9:
                    mes = "Setembro";
                    break;
                case 10:
                    mes = "Outubro";
                    break;
                case 11:
                    mes = "Novembro";
                    break;
                case 12:
                    mes = "Dezembro";
                    break;
            }
            if (tipo == 2) {
                extenso = ano;
            } else if (tipo == 1) {
                extenso = mes + " de " + ano;
            } else {
                extenso = dia + " de " + mes + " de " + ano;
            }
        } catch (NumberFormatException e) {
            extenso = data;

        }
        return extenso;

    }

    public static int diaDaSemana(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int dia = calendar.get(GregorianCalendar.DAY_OF_WEEK);
        return dia;
    }

    /**
     * Convert String with various formats into java.util.Date
     *
     * @param input Date as a string
     * @return java.util.Date object if input string is parsed successfully else
     * returns null System.out.println("10/14/2012" + " = " +
     * dataValidaConverte("10/14/2012")); System.out.println("10-Jan-2012" + " =
     * " + dataValidaConverte("10-Jan-2012")); System.out.println("01.03.2002" +
     * " = " + dataValidaConverte("01.03.2002"));
     * System.out.println("12/03/2010" + " = " +
     * dataValidaConverte("12/03/2010")); System.out.println("19.Feb.2011" + " =
     * " + dataValidaConverte("19.Feb.2011")); System.out.println("4/20/2012" +
     * " = " + dataValidaConverte("4/20/2012")); System.out.println("some
     * string" + " = " + dataValidaConverte("some string"));
     * System.out.println("123456" + " = " + dataValidaConverte("123456"));
     * System.out.println("null" + " = " + dataValidaConverte(null));
     */
    public static Date dataValidaConverte(String input) {
        List<SimpleDateFormat> dateFormats = new ArrayList<>();
        dateFormats.add(new SimpleDateFormat("M/dd/yyyy"));
        dateFormats.add(new SimpleDateFormat("dd.M.yyyy"));
        dateFormats.add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
        dateFormats.add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
        dateFormats.add(new SimpleDateFormat("dd.MMM.yyyy"));
        dateFormats.add(new SimpleDateFormat("dd-MMM-yyyy"));
        dateFormats.add(new SimpleDateFormat("dd/MM/yyyy"));
        Date date = null;
        if (null == input) {
            return null;
        }
        for (SimpleDateFormat format : dateFormats) {
            try {
                format.setLenient(false);
                date = format.parse(input);
            } catch (ParseException e) {
                //Shhh.. try other formats
            }
            if (date != null) {
                break;
            }
        }

        return date;
    }

    public static boolean isDataValida(String input) {
        return dataValidaConverte(input) != null;
    }

    public String primeiroDiaDoMes(String data) {
        if (isDataValida(data)) {
            try {
//                int c = 0;
//                int[] d = DataHoje.DataToArrayInt("32/" + data.substring(3, 10));
////                if ((d[1] + qtd) > 12) {
////                    int tmp = (d[1] + qtd);
////                    while (tmp > 12) {
////                        tmp -= 12;
////                        c++;
////                    }
////                    d[2] += c;
////                    d[1] = tmp;
////                } else {
////                    d[1] += qtd;
////                }
//
//                if (d[0] > qtdeDiasDoMes(d[1], d[2])) {
//                    d[0] = qtdeDiasDoMes(d[1], d[2]);
//                }

                return mascararData("01/" + data.substring(3, 10));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public String ultimoDiaDoMes(String data) {
        if (isDataValida(data)) {
            try {
                int c = 0;
                int[] d = DataHoje.DataToArrayInt("32/" + data.substring(3, 10));
//                if ((d[1] + qtd) > 12) {
//                    int tmp = (d[1] + qtd);
//                    while (tmp > 12) {
//                        tmp -= 12;
//                        c++;
//                    }
//                    d[2] += c;
//                    d[1] = tmp;
//                } else {
//                    d[1] += qtd;
//                }

                if (d[0] > qtdeDiasDoMes(d[1], d[2])) {
                    d[0] = qtdeDiasDoMes(d[1], d[2]);
                }

                return mascararData(d[0] + "/" + d[1] + "/" + d[2]);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static String alterDay(int dia, String dataString) {
        String diaString;
        if (dia > 9) {
            diaString = "" + dia;
        } else {
            diaString = "0" + dia;
        }
        String mesString = dataString.substring(3, 5);
        String anoString = dataString.substring(6, 10);
        dataString = diaString + "/" + mesString + "/" + anoString;
        if (!isDataValida(dataString)) {
            return "";
        }
        return dataString;
    }

    /**
     * @param hourIn H:m timestamp, i.e. [Hour in day (0-23)]:[Minute in hour
     * (0-59)]
     * @return total minutes after 00:00
     */
    public static Integer convertTimeToInteger(String hourIn) {
        try {
            String[] hourMin = hourIn.split(":");
            int hour = Integer.parseInt(hourMin[0]);
            int mins = Integer.parseInt(hourMin[1]);
            int hoursInMins = hour * 60;
            return hoursInMins + mins;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param minutesIn H:m timestamp, i.e. [Hour in day (0-23)]:[Minute in hour
     * (0-59)]
     * @return total minutes after 00:00
     */
    public static String convertMinutesToTime(Integer minutesIn) {
        try {
            String hourString = "";
            int hours = minutesIn / 60;
            int minutes = minutesIn % 60;
            if (hours < 10) {
                hourString += "0";
            }
            hourString += "" + hours + ":";
            if (minutes < 10) {
                hourString += "0" + minutes;
            }
            hourString += "";
            return hourString;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Retorna o ultimo dia do mês
     *
     * @param month
     * @return
     */
    public static Integer lastDayOfMonthInteger(Date month) {
        try {
            month = converte("01/02/2015");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(month);
            //Ultimo dia do mês
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_MONTH));
            Integer day = calendar.getTime().getDay();
            return day;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Retorna o ultimo dia do mês
     *
     * @param month
     * @return
     */
    public static Date lastDayOfMonth(Date month) {
        try {
            month = converte("01/02/2015");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(month);
            //Ultimo dia do mês
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_MONTH));
            return calendar.getTime();
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Compara uma data num determinado período
     *
     * @param date (Data a ser comparada)
     * @param start (Inicio)
     * @param finish (Fim)
     * @return
     */
    public static Boolean between(String date, String start, String finish) {
        try {
            int d = DataHoje.converteDataParaInteger(date);
            int s = DataHoje.converteDataParaInteger(start);
            int f = DataHoje.converteDataParaInteger(finish);
            if (d >= s && d <= f) {
                return true;
            }
        } catch (Exception e) {
            return null;
        }
        return false;
    }

    public static Boolean between(Date date, Date start, Date finish) {
        return between(converteData(date), converteData(start), converteData(finish));
    }

    public static Boolean betweenAge(Integer age, Integer start, Integer end) {
        try {
            if (age >= start && age <= end) {
                return true;
            }
        } catch (Exception e) {
            return null;
        }
        return false;
    }

    public String getDiaSemana(String data) {
        if (data.isEmpty()) {
            return "";
        }
        try {
            Date d = DataHoje.converte(data);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            int DAY_OF_WEEK = calendar.get(Calendar.DAY_OF_WEEK);
            switch (DAY_OF_WEEK) {
                case 1:
                    return "Domingo";
                case 2:
                    return "Segunda-Feira";
                case 3:
                    return "Terça-Feira";
                case 4:
                    return "Quarta-Feira";
                case 5:
                    return "Quinta-Feira";
                case 6:
                    return "Sexta-Feira";
                case 7:
                    return "Sábado";
            }
        } catch (Exception e) {
            return "";
        }
        return "";

    }

    public static Integer diffHour(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Calendar calStart = Calendar.getInstance();
            Calendar calEnd = Calendar.getInstance();

            calStart.setTime(sdf.parse(startTime));
            calEnd.setTime(sdf.parse(endTime));

            int minutes = (int) ((calEnd.getTimeInMillis() - calStart.getTimeInMillis())) / 60000;
//            long horas = (calEnd.getTimeInMillis() - calStart.getTimeInMillis()) / 3600000;
//            String diferenca = horas + ":" + resto;
//            int rest = minutes % 60;
//            int hours = minutes / 60;
            return minutes;
        } catch (ParseException e) {
            return 0;
        }
    }

    public static Integer diffMonths(Date startDate, Date endDate) {
        return diffMonths(DataHoje.converteData(startDate), DataHoje.converteData(endDate));
    }

    public static Integer diffMonths(String startDate, String endDate) {
        try {
            Calendar startCalendar = new GregorianCalendar();
            startCalendar.setTime(DataHoje.converte(startDate));
            Calendar endCalendar = new GregorianCalendar();
            endCalendar.setTime(DataHoje.converte(endDate));
            int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
            return diffMonth;
        } catch (Exception e) {
            return 0;
        }
    }

    public static Integer diffDays(String startDate, String endDate) {
        try {
            Calendar startCalendar = new GregorianCalendar();
            Calendar endCalendar = new GregorianCalendar();

            startCalendar.setTime(DataHoje.converte(startDate));
            endCalendar.setTime(DataHoje.converte(endDate));

            long millis1 = startCalendar.getTimeInMillis();
            long millis2 = endCalendar.getTimeInMillis();

            long diff = millis2 - millis1;

            long diffDays = diff / (24 * 60 * 60 * 1000);

            return Integer.parseInt(diffDays + "");
        } catch (Exception e) {
            return 0;
        }
    }

    public long diff(String tcase) {
        // Creates two calendars instances
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        // Set the date for both of the calendar instance
        cal1.set(2006, Calendar.DECEMBER, 30);
        cal2.set(2007, Calendar.MAY, 3);

        // Get the represented date in milliseconds
        long millis1 = cal1.getTimeInMillis();
        long millis2 = cal2.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = millis2 - millis1;

        // Calculate difference in seconds
        long diffSeconds = diff / 1000;

        // Calculate difference in minutes
        long diffMinutes = diff / (60 * 1000);

        // Calculate difference in hours
        long diffHours = diff / (60 * 60 * 1000);

        // Calculate difference in days
        long diffDays = diff / (24 * 60 * 60 * 1000);

        System.out.println("In milliseconds: " + diff + " milliseconds.");
        System.out.println("In seconds: " + diffSeconds + " seconds.");
        System.out.println("In minutes: " + diffMinutes + " minutes.");
        System.out.println("In hours: " + diffHours + " hours.");
        System.out.println("In days: " + diffDays + " days.");
        return 0;
    }
}
