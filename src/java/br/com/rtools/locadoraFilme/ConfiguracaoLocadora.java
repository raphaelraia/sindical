package br.com.rtools.locadoraFilme;

import br.com.rtools.financeiro.Servicos;
import br.com.rtools.utilitarios.Dao;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "conf_locadora")
public class ConfiguracaoLocadora implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "id_servico", referencedColumnName = "id", nullable = true)
    @ManyToOne
    private Servicos servicos;
    @Column(name = "ds_obs", nullable = false)
    private String obs;

    public ConfiguracaoLocadora() {
        this.id = -1;
        this.servicos = null;
        this.obs = "";
    }

    public ConfiguracaoLocadora(Integer id, Servicos servicos, String obs) {
        this.id = id;
        this.servicos = servicos;
        this.obs = obs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public static ConfiguracaoLocadora get() {
        return (ConfiguracaoLocadora) new Dao().find(new ConfiguracaoLocadora(), 1);
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public String toString() {
        return "ConfiguracaoLocadora{" + "id=" + id + ", servicos=" + servicos + ", obs=" + obs + '}';
    }

}
