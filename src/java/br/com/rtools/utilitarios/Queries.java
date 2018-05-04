package br.com.rtools.utilitarios;

import java.util.ArrayList;
import java.util.List;

public class Queries {

    private List listSelect;
    private List listFrom;
    private List listJoin;
    private List listWhere;
    private List listGroupBy;
    private List listHaving;
    private List listOrderBy;

    public Queries() {
        clean();
    }

    public final void clean() {
        listFrom = new ArrayList();
        listSelect = new ArrayList();
        listJoin = new ArrayList();
        listWhere = new ArrayList();
        listGroupBy = new ArrayList();
        listHaving = new ArrayList();
        listOrderBy = new ArrayList();
    }

    public void select(String alias) {
        listSelect.add(alias);
    }

    public void select(String key, String value) {
        try {
            listSelect.add(key + " AS " + value);
        } catch (Exception e) {

        }
    }

    public void selectGroup(String key, String value) {
        try {
            listSelect.add(key + " AS " + value);
            listGroupBy.add(key);
        } catch (Exception e) {

        }
    }

    public void from(String value) {
        listFrom.add(value);
    }

    public void join(String value) {
        try {
            listJoin.add(value);
        } catch (Exception e) {

        }
    }

    public void where(String value) {
        try {
            listWhere.add(value);
        } catch (Exception e) {

        }
    }

    public void group(String value) {
        try {
            listGroupBy.add(value);
        } catch (Exception e) {

        }
    }

    public void having(String value) {
        try {
            listHaving.add(value);
        } catch (Exception e) {

        }
    }

    public void order(String value) {
        try {
            listOrderBy.add(value);
        } catch (Exception e) {

        }
    }

    public String createQuery() {
        return createQuery("");
    }

    public String createQuery(String queryName) {
        String queryString = "";
        if (Sessions.exists("session_process_uid")) {
            queryString += " -- {session_process_uid:" + Sessions.getInteger("session_process_uid", true) + "} \n\n";
        }
        if (!queryName.isEmpty()) {
            queryString += " -- " + queryName + " \n\n";
        }
        queryString += " SELECT ";
        for (int i = 0; i < listSelect.size(); i++) {
            if (i == 0) {
                queryString += listSelect.get(i).toString() + " \n";
            } else {
                queryString += ", " + listSelect.get(i).toString() + " \n";
            }
        }
        for (int i = 0; i < listFrom.size(); i++) {
            if (i == 0) {
                queryString += " FROM " + listFrom.get(i).toString() + " \n";
            } else {
                queryString += " , " + listFrom.get(i).toString() + " \n";
            }
        }
        for (int i = 0; i < listJoin.size(); i++) {
            if (i == 0) {
                queryString += " " + listJoin.get(i).toString() + " \n";
            } else {
                queryString += " " + listJoin.get(i).toString() + " \n";
            }
        }
        for (int i = 0; i < listWhere.size(); i++) {
            if (i == 0) {
                queryString += " WHERE " + listWhere.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listWhere.get(i).toString() + " \n";
            }
        }
        for (int i = 0; i < listHaving.size(); i++) {
            if (i == 0) {
                queryString += " HAVING " + listHaving.get(i).toString() + " \n";
            } else {
                queryString += " AND " + listHaving.get(i).toString() + " \n";
            }
        }
        for (int i = 0; i < listGroupBy.size(); i++) {
            if (i == 0) {
                queryString += listGroupBy.get(i).toString() + " \n";
            } else {
                queryString += ", " + listGroupBy.get(i).toString() + " \n";
            }
        }
        for (int i = 0; i < listOrderBy.size(); i++) {
            if (i == 0) {
                queryString += listOrderBy.get(i).toString() + " \n";
            } else {
                queryString += ", " + listOrderBy.get(i).toString() + " \n";
            }
        }
        return queryString;
    }

    public static String get(String query_string) {
        return new Queries().createQuery("", query_string, false);
    }

    public static String get(String query_name, String query_string) {
        return new Queries().createQuery(query_name, query_string, false);
    }

    public String createQuery(String query_name, String query_string) {
        return createQuery(query_name, query_string, false);
    }

    public String createQuery(String query_name, String query_string, Boolean where) {
        if (!query_name.isEmpty()) {
            query_string = " -- " + query_name + " \n\n" + query_string;
        }
        if (Sessions.exists("session_process_uid")) {
            query_string = " -- {session_process_uid:" + Sessions.getInteger("session_process_uid", true) + "} \n\n" + query_string;
        }
        query_string += get(where);
        return query_string;
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
        clean();
        return queryString;
    }

    public List getListWhere() {
        return listWhere;
    }

    public void setListWhere(List listWhere) {
        this.listWhere = listWhere;
    }

    public List getListFrom() {
        return listFrom;
    }

    public void setListFrom(List listFrom) {
        this.listFrom = listFrom;
    }

    public List getListSelect() {
        return listSelect;
    }

    public void setListSelect(List listSelect) {
        this.listSelect = listSelect;
    }

    public List getListJoin() {
        return listJoin;
    }

    public void setListJoin(List listJoin) {
        this.listJoin = listJoin;
    }

    public List getListGroupBy() {
        return listGroupBy;
    }

    public void setListGroupBy(List listGroupBy) {
        this.listGroupBy = listGroupBy;
    }

    public List getListHaving() {
        return listHaving;
    }

    public void setListHaving(List listHaving) {
        this.listHaving = listHaving;
    }

    public List getListOrderBy() {
        return listOrderBy;
    }

    public void setListOrderBy(List listOrderBy) {
        this.listOrderBy = listOrderBy;
    }

}
