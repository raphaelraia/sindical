package br.com.rtools.webservice;

import br.com.rtools.sistema.BackupPostgres;
import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.ControleScripts;
import br.com.rtools.sistema.TipoControleScripts;
import br.com.rtools.sistema.dao.ConfiguracaoDao;
import br.com.rtools.utilitarios.Dao;
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
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        GenericaSessao.put("sessaoCliente", "Rtools");
    }
    
    public void storeDataBaseServer() {
        try {
            String database_server = GenericaRequisicao.getParametro("database_server");
            // String device = GenericaRequisicao.getParametro("device");
            // String type = GenericaRequisicao.getParametro("type");
            // String mac = GenericaRequisicao.getParametro("mac");
            String mac = GenericaRequisicao.getParametro("mac");
            String erro = GenericaRequisicao.getParametro("erro");
            String tamanho = GenericaRequisicao.getParametro("tamanho");
            String server = GenericaRequisicao.getParametro("server");
            String backup_postgres_id = GenericaRequisicao.getParametro("backup_postgres_id");
            ControleScripts controleScripts = new ControleScripts();
            controleScripts.setControleScripts((TipoControleScripts) new Dao().find(new TipoControleScripts(), 1));
            controleScripts.setDescricao(database_server);
            Configuracao c = new ConfiguracaoDao().find(database_server);
            if (c != null) {
                controleScripts.setConfiguracao(c);
            }
            if (mac != null && !mac.isEmpty()) {
                controleScripts.setMac(mac);
            }
            if (erro != null && !erro.isEmpty()) {
                controleScripts.setErro(true);
            }
            if (tamanho != null && !tamanho.isEmpty()) {
                controleScripts.setTamanho(Integer.parseInt(tamanho) / 1024);
            }
            if (server != null && !server.isEmpty()) {
                controleScripts.setServidor(server);
            }
            if (backup_postgres_id != null && !backup_postgres_id.isEmpty()) {
                controleScripts.setBackupPostgres((BackupPostgres) new Dao().find(new BackupPostgres(), Integer.parseInt(backup_postgres_id)));
            }
            new Dao().save(controleScripts, true);
        } catch (Exception e) {
            
        }
    }
    
}
