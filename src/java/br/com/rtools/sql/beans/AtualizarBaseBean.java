/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.sql.beans;

import br.com.rtools.principal.DBExternal;
import br.com.rtools.sql.AtualizarBase;
import br.com.rtools.sql.AtualizarBaseCliente;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class AtualizarBaseBean implements Serializable {

    private AtualizarBase atualizarBase;
    private List<AtualizarBase> listAtualizarBase;
    private AtualizarBaseCliente atualizarBaseCliente;
    private List<AtualizarBaseCliente> listAtualizarBaseCliente;

    @PostConstruct
    public void init() {
        atualizarBase = new AtualizarBase();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("atualizarBaseBean");
    }

    public void listener(Integer tcase) {
        if (tcase == 1) {
            GenericaSessao.remove("atualizarBaseBean");
        }

    }

    public void save() {
        if (atualizarBase.getScript().isEmpty()) {
            GenericaMensagem.info("Validação", "Cadastrar script!");
            return;
        }
        Dao dao = new Dao();
        if (atualizarBase.getId() == null) {
            if (new Dao().save(atualizarBase, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                loadListAtualizarBase();
                loadListAtualizarBaseCliente();
                return;
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
                return;
            }
        } else {
            if (dao.update(atualizarBase, true)) {
                GenericaMensagem.info("Sucesso", "Registro atualizado");
                loadListAtualizarBase();
                loadListAtualizarBaseCliente();
                return;
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
                return;
            }
        }
    }

    public void delete() {
        if (new Dao().delete(atualizarBase, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            atualizarBase = new AtualizarBase();
            for (int i = 0; i < listAtualizarBaseCliente.size(); i++) {
                new Dao().delete(listAtualizarBaseCliente.get(i), true);
            }
            loadListAtualizarBase();
            loadListAtualizarBaseCliente();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void edit(AtualizarBase ab) {
        atualizarBase = (AtualizarBase) new Dao().rebind(ab);
        loadListAtualizarBase();

    }

    public void run() {
        ResultSet rs;
        PreparedStatement ps;
        DBExternal dBExternal = new DBExternal();
        try {
            ps = dBExternal.getConnection().prepareStatement(
                    "   SELECT *                    "
                    + "   FROM sis_configuracao     "
                    + "  WHERE ds_identifica =     '" + "" + "'"
                    + "  LIMIT 1                    "
            );
            //ps.setString(1, nomeCliente);
            rs = ps.executeQuery();

            if (!rs.next()) {
                return;
            }

            Boolean ativo = rs.getBoolean("is_ativo");

            if (ativo) {
            }

//            while (rs.next()) {
//                
//                Boolean ativo = Boolean.parseBoolean(rs.getString("is_ativo"));
//                if (ativo) {
//                    return true;
//                }
//            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public void remove(AtualizarBaseCliente abc) {
        if (new Dao().delete(abc, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
        } else {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void loadListAtualizarBase() {
        listAtualizarBase = new ArrayList();
    }

    public void loadListAtualizarBaseCliente() {
        listAtualizarBaseCliente = new ArrayList();

    }

    public AtualizarBase getAtualizarBase() {
        return atualizarBase;
    }

    public void setAtualizarBase(AtualizarBase atualizarBase) {
        this.atualizarBase = atualizarBase;
    }

    public List<AtualizarBase> getListAtualizarBase() {
        return listAtualizarBase;
    }

    public void setListAtualizarBase(List<AtualizarBase> listAtualizarBase) {
        this.listAtualizarBase = listAtualizarBase;
    }

    public AtualizarBaseCliente getAtualizarBaseCliente() {
        return atualizarBaseCliente;
    }

    public void setAtualizarBaseCliente(AtualizarBaseCliente atualizarBaseCliente) {
        this.atualizarBaseCliente = atualizarBaseCliente;
    }

    public List<AtualizarBaseCliente> getListAtualizarBaseCliente() {
        return listAtualizarBaseCliente;
    }

    public void setListAtualizarBaseCliente(List<AtualizarBaseCliente> listAtualizarBaseCliente) {
        this.listAtualizarBaseCliente = listAtualizarBaseCliente;
    }

}
