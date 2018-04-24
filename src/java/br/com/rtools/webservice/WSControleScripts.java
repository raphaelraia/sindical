package br.com.rtools.webservice;

import br.com.rtools.sistema.dao.ConfiguracaoDao;
import br.com.rtools.sistema.dao.ControleScriptsDao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@RequestScoped
@ViewScoped
public class WSControleScripts implements Serializable {

    public WSControleScripts() {
        // HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        // GenericaSessao.put("sessaoCliente", "Rtools");
    }

    public void storeDataBaseServer() {
        try {
            String database_server = GenericaRequisicao.getParametro("database_server");
            if (database_server == null || database_server.isEmpty()) {
                return;
            }
            String mac = GenericaRequisicao.getParametro("mac");
            if (mac == null) {
                mac = "";
            }
            String erro = GenericaRequisicao.getParametro("erro");
            String tamanho = GenericaRequisicao.getParametro("tamanho");
            String server = GenericaRequisicao.getParametro("server");
            String bpid = GenericaRequisicao.getParametro("backup_postgres_id");
            Integer c = null;
            Integer tipo_controle_scripts_id = null;
            Integer backup_postgres_id = null;
            Integer configuracao_id = new ConfiguracaoDao().findConfiguracaoId(database_server);
            if (backup_postgres_id != null && !bpid.isEmpty()) {
                backup_postgres_id = Integer.parseInt(bpid);
                tipo_controle_scripts_id = 2;
            } else {
                tipo_controle_scripts_id = 1;
            }

            if (tamanho == null || tamanho.isEmpty()) {
                tamanho = "0";
            }
            if (backup_postgres_id == null || bpid.isEmpty()) {
                backup_postgres_id = null;
            }
            new ControleScriptsDao().store(mac, Integer.parseInt(tamanho), (erro != null && !erro.isEmpty()), database_server, server, tipo_controle_scripts_id, backup_postgres_id, configuracao_id);
        } catch (Exception e) {

        }
    }

}
