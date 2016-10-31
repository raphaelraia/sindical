package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.SisAutorizacoes;
import br.com.rtools.sistema.dao.SisAutorizacoesDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Messages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class SisAutorizacoesBean implements Serializable {

    private SisAutorizacoes sisAutorizacoes;
    private List<SisAutorizacoes> listSisAutorizacoes;
    private String refusedMotive;
    private String typeFilter;

    public SisAutorizacoesBean() {
        refusedMotive = "";
        typeFilter = "aberto";
        loadListSisAutorizacoes();
    }

    public void accept(SisAutorizacoes sa) {
        SisAutorizacoesDao sad = new SisAutorizacoesDao();
        sa.setGestor(Usuario.getUsuario());
        sa.setDtAutorizacao(new Date());
        sa.setHoraAutorizacao(DataHoje.horaMinuto());
        sa.setAutorizado(true);
        Dao dao = new Dao();
        dao.openTransaction();
        if (!sad.execute(dao, sa)) {
            dao.rollback();
            Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
            return;
        }
        if (!new Dao().update(sa, true)) {
            dao.rollback();
            Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
            return;
        }
        dao.commit();
        Messages.info("Sucesso", "SOLICITAÇÃO AUTORIZADA");
        loadListSisAutorizacoes();
        if (sa.getTabela().equals("pes_pessoa")) {
            new Dao().refresh(sa.getPessoa());
        }
    }

    public void refused(SisAutorizacoes sa) {
        sisAutorizacoes = sa;
        loadListSisAutorizacoes();
    }

    public void refused() {
        if (refusedMotive.isEmpty()) {
            Messages.warn("Validação", "INFORMAR O MOTIVO DA RECUSA!");
            return;
        }
        if (refusedMotive.length() < 5) {
            Messages.warn("Validação", "INFORMAR O MOTIVO VÁLIDO!");
            return;
        }
        sisAutorizacoes.setGestor(Usuario.getUsuario());
        sisAutorizacoes.setDtAutorizacao(new Date());
        sisAutorizacoes.setHoraAutorizacao(DataHoje.horaMinuto());
        sisAutorizacoes.setAutorizado(false);
        sisAutorizacoes.setMotivoRecusa(refusedMotive);
        if (!new Dao().update(sisAutorizacoes, true)) {
            Messages.warn("Erro", "AO REALIZAR AUTORIZAÇÃO!");
            return;
        }
        sisAutorizacoes = new SisAutorizacoes();
        Messages.info("Sucesso", "SOLICITAÇÃO RECUSADA");
        refusedMotive = "";
        loadListSisAutorizacoes();
    }

    public SisAutorizacoes getSisAutorizacoes() {
        return sisAutorizacoes;
    }

    public void setSisAutorizacoes(SisAutorizacoes sisAutorizacoes) {
        this.sisAutorizacoes = sisAutorizacoes;
    }

    public List<SisAutorizacoes> getListSisAutorizacoes() {
        return listSisAutorizacoes;
    }

    public void setListSisAutorizacoes(List<SisAutorizacoes> listSisAutorizacoes) {
        this.listSisAutorizacoes = listSisAutorizacoes;
    }

    public String getRefusedMotive() {
        return refusedMotive;
    }

    public void setRefusedMotive(String refusedMotive) {
        this.refusedMotive = refusedMotive;
    }

    public final void loadListSisAutorizacoes() {
        listSisAutorizacoes = new ArrayList();
        listSisAutorizacoes = new SisAutorizacoesDao().findAll(typeFilter);
    }

    public String getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }

}
