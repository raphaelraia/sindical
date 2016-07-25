package br.com.rtools.pessoa.beans;

import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaEmpresaDao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PessoaSugestaoBean implements Serializable {

    private List<Fisica> listFisicaSugestao;
    private List<Juridica> listJuridicaSugestao;
    private List<Socios> listaSocios;

    @PostConstruct
    public void load() {
        listFisicaSugestao = new ArrayList();
        listJuridicaSugestao = new ArrayList();
        listaSocios = new ArrayList();
    }

    @PreDestroy
    public void destroy() {

    }

    public void sugerirPessoaFisica(Pessoa p, String dialog, String update) {
        if (p.getId() == -1) {
            if (!p.getNome().isEmpty()) {
                listFisicaSugestao = new ArrayList();
                listFisicaSugestao = new FisicaDao().findByNome(p.getNome());
                if (!listFisicaSugestao.isEmpty()) {
                    if (dialog != null && !dialog.isEmpty()) {
                        PF.openDialog(dialog);
                    }
                    if (update != null && !update.isEmpty()) {
                        PF.update(update);
                    }
                }
            }
        }
    }

    public void sugerirPessoaFisica(Pessoa p) {
        if (p.getId() == -1) {
            if (!p.getNome().isEmpty()) {
                listFisicaSugestao = new ArrayList();
                listFisicaSugestao = new FisicaDao().findByNome(p.getNome());
                if (!listFisicaSugestao.isEmpty()) {
                    PF.openDialog("dlg_sugestoes_fisica");
                    PF.update("form_pessoa_sugestao:i_sugestoes_fisica");
                }
            }
        }
    }

    public void sugerirPessoaJuridica(Pessoa p, String dialog, String update) {
        if (p.getId() == -1) {
            if (!p.getNome().isEmpty()) {
                listJuridicaSugestao = new ArrayList();
                listJuridicaSugestao = new JuridicaDao().findByNome(p.getNome());
                if (!listJuridicaSugestao.isEmpty()) {
                    if (dialog != null && !dialog.isEmpty()) {
                        PF.openDialog(dialog);
                    }
                    if (update != null && !update.isEmpty()) {
                        PF.update(update);
                    }
                }
            }
        }
    }

    public void sugerirPessoaJuridica(Pessoa p) {
        if (p.getId() == -1) {
            if (!p.getNome().isEmpty()) {
                listJuridicaSugestao = new ArrayList();
                listJuridicaSugestao = new JuridicaDao().findByNome(p.getNome());
                if (!listJuridicaSugestao.isEmpty()) {
                    PF.openDialog("dlg_sugestoes_juridica");
                    PF.update("form_pessoa_sugestao:i_sugestoes_juridica");
                }
            }
        }
    }

    public void selectedPessoa(Pessoa p) {
        GenericaSessao.put("pessoaPesquisa", p);
    }

    public void selectedPessoaJuridica(Juridica j) {
        GenericaSessao.put("juridicaPesquisa", j);
    }

    public void selectedPessoaFisica(Fisica f) {
        GenericaSessao.put("fisicaPesquisa", f);
    }

    public void selectedPessoa(Pessoa p, String session_name) {
        if (session_name == null || session_name.isEmpty()) {
            GenericaSessao.put("pessoaPesquisa", p);
        } else {
            GenericaSessao.put(session_name, p);
        }
    }

    public void selectedPessoaJuridica(Juridica j, String session_name) {
        if (session_name == null || session_name.isEmpty()) {
            GenericaSessao.put("juridicaPesquisa", j);
        } else {
            GenericaSessao.put(session_name, j);
        }
    }

    public void selectedPessoaFisica(Fisica f, String session_name) {
        if (session_name == null || session_name.isEmpty()) {
            GenericaSessao.put("fisicaPesquisa", f);
        } else {
            GenericaSessao.put(session_name, f);
        }
    }

    public void listenerSocios(Integer idPessoa) {
        listaSocios.clear();
        SociosDao sociosDao = new SociosDao();
        Socios s = sociosDao.pesquisaSocioPorPessoaAtivo(idPessoa);
        if (s != null && s.getId() != -1) {
            listaSocios = sociosDao.pesquisaDependentePorMatricula(s.getMatriculaSocios().getId(), false);
        }
    }

    public String pessoaEmpresaString(Fisica f) {
        String pessoaEmpresaString = "";
        PessoaEmpresaDao pessoaEmpresaDB = new PessoaEmpresaDao();
        PessoaEmpresa pe = (PessoaEmpresa) pessoaEmpresaDB.pesquisaPessoaEmpresaPorFisica(f.getId());
        if (pe != null) {
            if (pe.getId() != -1) {
                pessoaEmpresaString = pe.getJuridica().getPessoa().getNome();
            }
        }
        return (pessoaEmpresaString.isEmpty()) ? "SEM EMPRESA" : pessoaEmpresaString;
    }

    public boolean existePessoaOposicaoPorDocumento(String documento) {
        if (!documento.isEmpty()) {
            OposicaoDao odbt = new OposicaoDao();
            return odbt.existPessoaDocumentoPeriodo(documento);
        }
        return false;
    }

    public List<Fisica> getListFisicaSugestao() {
        return listFisicaSugestao;
    }

    public void setListFisicaSugestao(List<Fisica> listFisicaSugestao) {
        this.listFisicaSugestao = listFisicaSugestao;
    }

    public List<Juridica> getListJuridicaSugestao() {
        return listJuridicaSugestao;
    }

    public void setListJuridicaSugestao(List<Juridica> listJuridicaSugestao) {
        this.listJuridicaSugestao = listJuridicaSugestao;
    }

    public List<Socios> getListaSocios() {
        return listaSocios;
    }

    public void setListaSocios(List<Socios> listaSocios) {
        this.listaSocios = listaSocios;
    }

}
