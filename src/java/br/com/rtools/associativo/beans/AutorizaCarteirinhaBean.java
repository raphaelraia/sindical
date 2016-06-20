package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.AutorizaImpressaoCartao;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class AutorizaCarteirinhaBean {

    private Usuario usuario = new Usuario();
    private Fisica fisica = new Fisica();
    private AutorizaImpressaoCartao impressaoCartao = new AutorizaImpressaoCartao();
    private int idModelo = 0;
    private List<SelectItem> listaModelo = new ArrayList<SelectItem>();
    private List<AutorizaImpressaoCartao> listaAutorizacao = new ArrayList<AutorizaImpressaoCartao>();

    public AutorizaCarteirinhaBean() {
        if (GenericaSessao.getObject("sessaoUsuario") != null) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }

        impressaoCartao.setFoto(((Registro) new Dao().find(new Registro(), 1)).isFotoCartao());
    }

    public void autorizar() {

        if (Integer.valueOf(listaModelo.get(0).getDescription()) == 0) {
            GenericaMensagem.warn("Erro", "Nenhum Modelo de carteirinha encontrado!");
            return;
        }

        Dao dao = new Dao();
        SocioCarteirinhaDao db = new SocioCarteirinhaDao();

        if (db.listaSocioCarteirinhaAutoriza(fisica.getPessoa().getId(), Integer.valueOf(listaModelo.get(idModelo).getDescription())).isEmpty()) {
            GenericaMensagem.warn("Erro", "Esta Pessoa NÃO POSSUI carteirinha para ser autorizada!");
            return;
        }

        if (!db.listaAutoriza(fisica.getPessoa().getId(), Integer.valueOf(listaModelo.get(idModelo).getDescription())).isEmpty()) {
            GenericaMensagem.warn("Erro", "Esta Pessoa já esta autorizada a imprimir carteirinha!");
            return;
        }

        impressaoCartao.setModeloCarteirinha((ModeloCarteirinha) dao.find(new ModeloCarteirinha(), Integer.valueOf(listaModelo.get(idModelo).getDescription())));
        impressaoCartao.setUsuario(usuario);
        impressaoCartao.setPessoa(fisica.getPessoa());

        dao.openTransaction();
        if (!dao.save(impressaoCartao)) {
            GenericaMensagem.warn("Erro", "Não foi possível salvar autorização!");
            dao.rollback();
            return;
        }

        NovoLog novoLog = new NovoLog();
        String saveLog
                = "ID" + impressaoCartao.getId()
                + " - Pessoa {ID: " + impressaoCartao.getPessoa().getId() + " - Nome: " + impressaoCartao.getPessoa().getNome() + " }"
                + " - Autorizada por {ID: " + impressaoCartao.getUsuario().getPessoa().getId() + " - Nome: " + impressaoCartao.getUsuario().getPessoa().getNome() + " }"
                + " - Modelo {ID: " + impressaoCartao.getModeloCarteirinha().getId() + " - Modelo: " + impressaoCartao.getModeloCarteirinha().getDescricao() + " }";
        novoLog.setTabela("soc_autoriza_impressao_cartao");
        novoLog.setCodigo(impressaoCartao.getId());
        novoLog.save(saveLog);

        dao.commit();
        GenericaMensagem.info("Sucesso", "Autorização Concluída!");
    }

    public void excluir(AutorizaImpressaoCartao linha) {
        if (linha.getHistoricoCarteirinha() != null) {
            GenericaMensagem.warn("Erro", "Essa autorização não pode ser excluída!");
        }

        Dao dao = new Dao();

        dao.openTransaction();

        if (!dao.delete(linha)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Autorização não pode ser excluído!");
        } else {
            NovoLog novoLog = new NovoLog();
            String deleteLog
                    = "ID" + impressaoCartao.getId()
                    + " - Pessoa {ID: " + impressaoCartao.getPessoa().getId() + " - Nome: " + impressaoCartao.getPessoa().getNome() + " }"
                    + " - Autorizada por {ID: " + impressaoCartao.getUsuario().getPessoa().getId() + " - Nome: " + impressaoCartao.getUsuario().getPessoa().getNome() + " }"
                    + " - Modelo {ID: " + impressaoCartao.getModeloCarteirinha().getId() + " - Modelo: " + impressaoCartao.getModeloCarteirinha().getDescricao() + " }";
            novoLog.setTabela("soc_autoriza_impressao_cartao");
            novoLog.setCodigo(impressaoCartao.getId());
            novoLog.delete(deleteLog);
            GenericaMensagem.info("Sucesso", "Autorização Excluída!");
            dao.commit();
            listaAutorizacao.clear();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Fisica getFisica() {
        if (GenericaSessao.getObject("fisicaPesquisa") != null) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa");
            GenericaSessao.remove("fisicaPesquisa");
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public AutorizaImpressaoCartao getImpressaoCartao() {
        return impressaoCartao;
    }

    public void setImpressaoCartao(AutorizaImpressaoCartao impressaoCartao) {
        this.impressaoCartao = impressaoCartao;
    }

    public int getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }

    public List<SelectItem> getListaModelo() {
        if (listaModelo.isEmpty()) {
            List<ModeloCarteirinha> result = (new Dao()).list(new ModeloCarteirinha());

            if (!result.isEmpty()) {
                for (int i = 0; i < result.size(); i++) {
                    listaModelo.add(new SelectItem(i, result.get(i).getDescricao(), String.valueOf(result.get(i).getId())));
                }
            } else {
                listaModelo.add(new SelectItem(0, "Nenhum Modelo Encontrado", "0"));
            }
        }
        return listaModelo;
    }

    public void setListaModelo(List<SelectItem> listaModelo) {
        this.listaModelo = listaModelo;
    }

    public List<AutorizaImpressaoCartao> getListaAutorizacao() {
        if (listaAutorizacao.isEmpty()) {
            listaAutorizacao = new Dao().list(new AutorizaImpressaoCartao());
        }
        return listaAutorizacao;
    }

    public void setListaAutorizacao(List<AutorizaImpressaoCartao> listaAutorizacao) {
        this.listaAutorizacao = listaAutorizacao;
    }
}
