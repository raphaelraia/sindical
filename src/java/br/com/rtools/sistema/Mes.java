package br.com.rtools.sistema;

import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.*;

@Entity
@Table(name = "sis_mes")
@NamedQueries({
    @NamedQuery(name = "Mes.findAll", query = "SELECT M FROM Mes AS M ORDER BY M.id ASC ")
})
public class Mes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 15, unique = true)
    private String descricao;

    public Mes() {
        this.id = -1;
        this.descricao = "";
    }

    public Mes(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.id;
        hash = 29 * hash + (this.descricao != null ? this.descricao.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mes other = (Mes) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.descricao == null) ? (other.descricao != null) : !this.descricao.equals(other.descricao)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Mes{" + "id=" + id + ", descricao=" + descricao + '}';
    }

    /**
     *
     * @return new SelectItem(id, descricao)
     */
    public static List<SelectItem> loadSelectItem() {
        return loadSelectItem(false);
    }

    /**
     *
     * @param index (Se a lista usa index : true = index ; false = id;
     * @return new SelectItem(id, descricao)
     */
    public static List<SelectItem> loadSelectItem(Boolean index) {
        List<SelectItem> listMeses = new ArrayList<>();
        try {
            List<Mes> list = new Dao().list(new Mes(), true);
            for (int i = 0; i < list.size(); i++) {
                String mes = "" + list.get(i).getId();
                if (list.get(i).getId() < 10) {
                    mes = "0" + list.get(i).getId();
                }
                if (index == null || !index) {
                    listMeses.add(new SelectItem(mes, list.get(i).getDescricao()));
                } else {
                    listMeses.add(new SelectItem(i, list.get(i).getDescricao(), mes));
                }
            }
        } catch (Exception e) {

        }
        return listMeses;
    }

}
