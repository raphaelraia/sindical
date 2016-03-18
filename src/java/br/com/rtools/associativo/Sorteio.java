package br.com.rtools.associativo;

import br.com.rtools.associativo.dao.SorteioCategoriaDao;
import br.com.rtools.associativo.dao.SorteioStatusDao;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "sort_sorteio",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ds_descricao", "dt_inicio"})
)
public class Sorteio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ds_descricao", nullable = false, length = 250)
    private String descricao;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_inicio", nullable = false)
    private Date dtInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "dt_fim")
    private Date dtFim;

    @Transient
    private List<SorteioCategoria> listSorteioCategoria;

    @Transient
    private SorteioStatus sorteioStatus;

    public Sorteio() {
        this.id = null;
        this.descricao = "";
        this.dtInicio = null;
        this.dtFim = null;
        this.sorteioStatus = null;
        this.listSorteioCategoria = null;        
    }

    public Sorteio(Integer id, String descricao, Date dtInicio, Date dtFim) {
        this.id = id;
        this.descricao = descricao;
        this.dtInicio = dtInicio;
        this.dtFim = dtFim;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDtInicio() {
        return dtInicio;
    }

    public void setDtInicio(Date dtInicio) {
        this.dtInicio = dtInicio;
    }

    public Date getDtFim() {
        return dtFim;
    }

    public void setDtFim(Date dtFim) {
        this.dtFim = dtFim;
    }

    public String getInicioString() {
        return DataHoje.converteData(dtInicio);
    }

    public void setInicioString(String inicioString) {
        this.dtInicio = DataHoje.converte(inicioString);
    }

    public String getFimString() {
        return DataHoje.converteData(dtFim);
    }

    public void setFimString(String fimString) {
        this.dtFim = DataHoje.converte(fimString);
    }

    public List<SorteioCategoria> getListSorteioCategoria() {
        if(this.listSorteioCategoria == null) {
            if(this.id != null) {
                this.listSorteioCategoria = new ArrayList<>();
                this.listSorteioCategoria = new SorteioCategoriaDao().findBySorteio(this.id);                
            }
        }
        return listSorteioCategoria;
    }

    public void setListSorteioCategoria(List<SorteioCategoria> listSorteioCategoria) {
        this.listSorteioCategoria = listSorteioCategoria;
    }

    public SorteioStatus getSorteioStatus() {
        if(this.sorteioStatus == null) {
            if(this.id != null) {
                sorteioStatus = new SorteioStatus();
                sorteioStatus = new SorteioStatusDao().findBySorteio(this.id);            
            }
        }
        return sorteioStatus;
    }

    public void setSorteioStatus(SorteioStatus sorteioStatus) {
        this.sorteioStatus = sorteioStatus;
    }

}
