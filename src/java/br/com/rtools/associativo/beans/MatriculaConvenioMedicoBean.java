package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.ConfiguracaoArrecadacao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.MatriculaConvenioMedico;
import br.com.rtools.associativo.dao.MatriculaConvenioMedicoDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class MatriculaConvenioMedicoBean implements Serializable {

    private MatriculaConvenioMedico matriculaConvenioMedico;
    private ServicoPessoaBean servicoPessoaBean;
    private String message;
    private String descPesquisa;
    private String porPesquisa;
    private String comoPesquisa;
    private Boolean ativo;
    private List<MatriculaConvenioMedico> listaConvenio;
    private Boolean pessoaOposicao;

    @PostConstruct
    public void init() {
        servicoPessoaBean = null;
        GenericaSessao.put("servicoPessoaBean", new ServicoPessoaBean());
        servicoPessoaBean = ((ServicoPessoaBean) GenericaSessao.getObject("servicoPessoaBean"));
        servicoPessoaBean.setRenderServicos(true);
        matriculaConvenioMedico = new MatriculaConvenioMedico();
        message = "";
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        listaConvenio = new ArrayList();
        ativo = true;
        pessoaOposicao = false;
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("matriculaConvenioMedicoBean");
        GenericaSessao.remove("servicoPessoaBean");
    }

    public void save() {
        servicoPessoaBean = ((ServicoPessoaBean) GenericaSessao.getObject("servicoPessoaBean"));
        Dao dao = new Dao();
        NovoLog novoLog = new NovoLog();
        if (servicoPessoaBean.getTitular().getId() == -1) {
            message = "Pesquise uma Pessoa!";
            return;
        }

        MatriculaConvenioMedicoDao db = new MatriculaConvenioMedicoDao();
        List<MatriculaConvenioMedico> result = db.listaConvenioPessoa(servicoPessoaBean.getTitular().getPessoa().getId(), Integer.valueOf(servicoPessoaBean.getListaServicos().get(servicoPessoaBean.getIdServico()).getDescription()));

        if (servicoPessoaBean.getServicoPessoa().getId() == -1) {
            if (result.size() >= 1) {
                message = "Pessoa já contém Convênio Ativo!";
                return;
            }

            dao.openTransaction();
            message = servicoPessoaBean.salvarServicoPessoa(null, dao);
            if (!message.isEmpty()) {
                dao.rollback();
                return;
            }
            matriculaConvenioMedico.setServicoPessoa(servicoPessoaBean.getServicoPessoa());
            if (dao.save(matriculaConvenioMedico)) {
                novoLog.save(""
                        + "ID: " + matriculaConvenioMedico.getId() + " \n "
                        + "Código: " + matriculaConvenioMedico.getCodigo() + " \n "
                        + "Pessoa: (" + matriculaConvenioMedico.getServicoPessoa().getPessoa().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getPessoa().getNome() + " \n "
                        + "Cobrança (Pessoa): (" + matriculaConvenioMedico.getServicoPessoa().getCobranca().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getCobranca().getNome() + " \n "
                        + "Serviço Pessoa: (" + matriculaConvenioMedico.getServicoPessoa().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getServicos().getDescricao()
                );
                message = "Matricula salva com Sucesso!";
                dao.commit();
            } else {
                message = "Erro ao Salvar Matricula!";
                dao.rollback();
            }
        } else {
            if (result.size() >= 1 && (servicoPessoaBean.getServicoPessoa().getServicos().getId() != result.get(0).getServicoPessoa().getServicos().getId())) {
                message = "Pessoa já contém Convênio Ativo!";
                return;
            }
            dao.openTransaction();

            message = servicoPessoaBean.atualizarServicoPessoa(null, dao);

            if (!message.isEmpty()) {
                dao.rollback();
                return;
            }
            MatriculaConvenioMedico mcm = (MatriculaConvenioMedico) dao.find(matriculaConvenioMedico);
            matriculaConvenioMedico.setServicoPessoa(servicoPessoaBean.getServicoPessoa());
            String beforeUpdate = ""
                    + "ID: " + mcm.getId() + " \n "
                    + "Código: " + mcm.getCodigo() + " \n "
                    + "Pessoa: (" + mcm.getServicoPessoa().getPessoa().getId() + ") " + mcm.getServicoPessoa().getPessoa().getNome() + " \n "
                    + "Cobrança (Pessoa): (" + mcm.getServicoPessoa().getCobranca().getId() + ") " + mcm.getServicoPessoa().getCobranca().getNome() + " \n "
                    + "Serviço Pessoa: (" + mcm.getServicoPessoa().getId() + ") " + mcm.getServicoPessoa().getServicos().getDescricao();
            if (dao.update(matriculaConvenioMedico)) {
                message = "Matricula atualizada com Sucesso!";
                novoLog.update(beforeUpdate, ""
                        + "ID: " + matriculaConvenioMedico.getId() + " \n "
                        + "Código: " + matriculaConvenioMedico.getCodigo() + " \n "
                        + "Pessoa: (" + matriculaConvenioMedico.getServicoPessoa().getPessoa().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getPessoa().getNome() + " \n "
                        + "Cobrança (Pessoa): (" + matriculaConvenioMedico.getServicoPessoa().getCobranca().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getCobranca().getNome() + " \n "
                        + "Serviço Pessoa: (" + matriculaConvenioMedico.getServicoPessoa().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getServicos().getDescricao()
                );
                dao.commit();
            } else {
                message = "Erro ao atualizar Matricula!";
                dao.rollback();
            }
        }
    }

    public void clear() {
        GenericaSessao.remove("matriculaConvenioMedicoBean");
        GenericaSessao.remove("servicoPessoaBean");
    }

    public void delete() {
        if (servicoPessoaBean.getServicoPessoa().getId() != -1) {
            Dao dao = new Dao();
            NovoLog novoLog = new NovoLog();
            dao.openTransaction();
            matriculaConvenioMedico.setDtInativo(DataHoje.dataHoje());
            matriculaConvenioMedico.getServicoPessoa().setAtivo(false);
            if (dao.update(matriculaConvenioMedico)) {
                if (dao.update(matriculaConvenioMedico.getServicoPessoa())) {
                    novoLog.delete(
                            "ID: " + matriculaConvenioMedico.getId() + " \n "
                            + "Código: " + matriculaConvenioMedico.getCodigo() + " \n "
                            + "Pessoa: (" + matriculaConvenioMedico.getServicoPessoa().getPessoa().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getPessoa().getNome() + " \n "
                            + "Cobrança (Pessoa): (" + matriculaConvenioMedico.getServicoPessoa().getCobranca().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getCobranca().getNome() + " \n "
                            + "Serviço Pessoa: (" + matriculaConvenioMedico.getServicoPessoa().getId() + ") " + matriculaConvenioMedico.getServicoPessoa().getServicos().getDescricao()
                    );
                    servicoPessoaBean.setServicoPessoa(matriculaConvenioMedico.getServicoPessoa());
                    dao.commit();
                    // GenericaSessao.put("matriculaConvenioMedicoBean", new MatriculaConvenioMedicoBean());
                    // ((MatriculaConvenioMedicoBean) GenericaSessao.getObject("matriculaConvenioMedicoBean")).setMessage("Matricula Excluida com sucesso!");
                    message = "Matrícula Inativada!";
                } else {
                    message = "Erro ao excluir serviço pessoa!";
                    dao.rollback();
                }
            } else {
                message = "Erro ao excluir Convênio médico!";
                dao.rollback();
            }
        } else {
            message = "Pesquisar um registro!";
        }
    }

    public String edit(MatriculaConvenioMedico mcm) {
        Dao dao = new Dao();

        mcm = (MatriculaConvenioMedico) dao.rebind(mcm);
        matriculaConvenioMedico = mcm;
        servicoPessoaBean.setServicoPessoa(matriculaConvenioMedico.getServicoPessoa());
        descPesquisa = "";
        porPesquisa = "nome";
        comoPesquisa = "";
        servicoPessoaBean.editar(matriculaConvenioMedico.getServicoPessoa());
        listaConvenio.clear();
        GenericaSessao.put("linkClicado", true);
        if (!GenericaSessao.exists("urlRetorno")) {
            return "convenioMedico";
        } else {
            return (String) GenericaSessao.getString("urlRetorno");
        }
    }

    public void acaoPesquisaInicial() {
        listaConvenio.clear();
        comoPesquisa = "I";
        loadList();
    }

    public void acaoPesquisaParcial() {
        listaConvenio.clear();
        comoPesquisa = "P";
        loadList();
    }

    public void loadList() {
        if (!(descPesquisa.trim()).isEmpty()) {
            MatriculaConvenioMedicoDao db = new MatriculaConvenioMedicoDao();
            listaConvenio = db.pesquisaConvenioMedico(descPesquisa.trim(), porPesquisa, comoPesquisa, ativo);
        }
    }

    public String getDescPesquisa() {
        return descPesquisa;
    }

    public void setDescPesquisa(String descPesquisa) {
        this.descPesquisa = descPesquisa;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public List<MatriculaConvenioMedico> getListaConvenio() {
        return listaConvenio;
    }

    public void setListaConvenio(List<MatriculaConvenioMedico> listaConvenio) {
        this.listaConvenio = listaConvenio;
    }

    public MatriculaConvenioMedico getMatriculaConvenioMedico() {
        return matriculaConvenioMedico;
    }

    public void setMatriculaConvenioMedico(MatriculaConvenioMedico matriculaConvenioMedico) {
        this.matriculaConvenioMedico = matriculaConvenioMedico;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getPessoaOposicao() {
        OposicaoDao odbt = new OposicaoDao();
        if (odbt.existPessoaDocumentoPeriodo(servicoPessoaBean.getServicoPessoa().getPessoa().getDocumento(), ConfiguracaoArrecadacao.get().getIgnoraPeriodoConvencaoOposicao())) {
            pessoaOposicao = true;
        }
        return pessoaOposicao;
    }

    public void setPessoaOposicao(Boolean pessoaOposicao) {
        this.pessoaOposicao = pessoaOposicao;
    }
}
