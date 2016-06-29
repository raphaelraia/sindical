package br.com.rtools.utilitarios;

import java.util.List;

public class GenericaString {

    public static String converterNullToString(Object object) {
        if (object == null) {
            return "";
        } else {
            return String.valueOf(object);
        }
    }

    /**
     * Usado apenas em <p:selectCheckboxMenu value="bean.list"/>
     * Preferencialmente se houver id
     *
     * @param list
     * @return
     */
    public static String returnInList(List list) {
        String ids = null;
        try {
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null) {
                        if (ids == null) {
                            ids = "" + list.get(i);
                        } else {
                            ids += "," + list.get(i);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return ids;
    }
}
