package br.com.rtools.associativo.lista;

import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class ListModeloCarterinhaCategoria {

    private Integer modelo_categoria_id;
    private List<SelectItem> listCategoria;
    private List<SelectItem> listRotina;
    private Integer categoria_id;
    private Integer rotina_id;
    private List<ModeloCarteirinhaCategoria> listMCC;

    public ListModeloCarterinhaCategoria() {
        this.modelo_categoria_id = null;
        this.listCategoria = new ArrayList<>();
        this.listRotina = new ArrayList<>();
        this.categoria_id = null;
        this.rotina_id = null;
        this.listMCC = new ArrayList<>();
    }

    public ListModeloCarterinhaCategoria(Integer modelo_categoria_id, List<SelectItem> listCategoria, List<SelectItem> listRotina, Integer categoria_id, Integer rotina_id, List<ModeloCarteirinhaCategoria> listMCC) {
        this.listCategoria = listCategoria;
        this.listRotina = listRotina;
        this.categoria_id = categoria_id;
        this.rotina_id = rotina_id;
        this.listMCC = listMCC;
    }

    public List<SelectItem> getListCategoria() {
        return listCategoria;
    }

    public void setListCategoria(List<SelectItem> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public List<SelectItem> getListRotina() {
        return listRotina;
    }

    public void setListRotina(List<SelectItem> listRotina) {
        this.listRotina = listRotina;
    }

    public Integer getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(Integer categoria_id) {
        this.categoria_id = categoria_id;
    }

    public Integer getRotina_id() {
        return rotina_id;
    }

    public void setRotina_id(Integer rotina_id) {
        this.rotina_id = rotina_id;
    }

    public List<ModeloCarteirinhaCategoria> getListMCC() {
        return listMCC;
    }

    public void setListMCC(List<ModeloCarteirinhaCategoria> listMCC) {
        this.listMCC = listMCC;
    }

    public Integer getModelo_categoria_id() {
        return modelo_categoria_id;
    }

    public void setModelo_categoria_id(Integer modelo_categoria_id) {
        this.modelo_categoria_id = modelo_categoria_id;
    }
}
