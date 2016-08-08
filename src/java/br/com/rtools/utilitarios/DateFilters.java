package br.com.rtools.utilitarios;

import java.util.Date;
import java.util.List;

public class DateFilters {

    private Boolean selected;
    private String title;
    private String type;
    private String start;
    private String finish;

    public DateFilters() {
        this.title = "";
        this.selected = false;
        this.type = "";
        this.start = "";
        this.finish = "";
    }

    public DateFilters(Boolean selected, String title, String type, String start, String finish) {
        this.selected = selected;
        this.title = title;
        this.type = type;
        this.start = start;
        this.finish = finish;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public Date getDtStart() {
        return DataHoje.converte(start);
    }

    public Date getDtFinish() {
        return DataHoje.converte(finish);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static DateFilters getDateFilters(List<DateFilters> listDateFilters, String title) {
        for (int i = 0; i < listDateFilters.size(); i++) {
            if (listDateFilters.get(i).getTitle().equals(title)) {
                return listDateFilters.get(i);
            }
        }
        return null;

    }

}
