package br.com.rtools.utilitarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryString {

    private List listAlias = new ArrayList();
    private List listFrom = new ArrayList();
    private List listWhere = new ArrayList();

    /**
     *
     * @param field
     * @param type - 0 = Total; 1 = Inicial; 2 = Parcial; 3 Filnal;
     * @return
     */
    public static String typeSearch(String field, int type) {
        switch (type) {
            case 0:
                field = " = TRIM(UPPER(FUNC_TRANSLATE('" + field + "')))";
                break;
            case 1:
                field = " LIKE TRIM(UPPER(FUNC_TRANSLATE('" + field + "%')))";
                break;
            case 2:
                field = " LIKE TRIM(UPPER(FUNC_TRANSLATE('%" + field + "%')))";
                break;
            case 3:
                field = " LIKE TRIM(UPPER(FUNC_TRANSLATE('%" + field + "')))";
                break;
        }
        return field;
    }

    public void addAlias(Map alias) {
        // listWhere.add(where);
    }

    public void addAlias(String alias) {
        // listWhere.add(where);
    }

    public void addFrom(Map from) {
        // listWhere.add(where);
    }

    public void addFrom(String from) {
        // listWhere.add(where);
    }

    public void addWhere(String where) {
        listWhere.add(where);
    }

    public String get() {
        return get(true);
    }

    public String get(Boolean where) {
        String queryString = "";
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0 && where) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        listWhere = new ArrayList();
        return queryString;
    }

    public List getListWhere() {
        return listWhere;
    }

    public void setListWhere(List listWhere) {
        this.listWhere = listWhere;
    }

    public List getListAlias() {
        return listAlias;
    }

    public void setListAlias(List listAlias) {
        this.listAlias = listAlias;
    }

    public List getListFrom() {
        return listFrom;
    }

    public void setListFrom(List listFrom) {
        this.listFrom = listFrom;
    }

}
