package br.com.rtools.pessoa.beans;

import br.com.rtools.associativo.ConfiguracaoSocial;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class PessoaComplementoBean extends PesquisarProfissaoBean implements Serializable {

    private PessoaComplemento pessoaComplemento = new PessoaComplemento();
    private Pessoa pessoa = new Pessoa();
    private Pessoa responsavel = new Pessoa();
    private Registro registro = new Registro();
    private int diaVencimento = 0;
    private List<SelectItem> listaDataVencimento = new ArrayList();

    private Integer indexStatusCobranca = 0;
    private List<SelectItem> listaStatusCobranca = new ArrayList();

    public PessoaComplementoBean() {

        this.loadListaStatusCobranca();
    }

    public String update(Integer pessoa_id) {
        return update(pessoa_id, true);
    }

    public String update(Integer pessoa_id, Boolean showMessage) {
        Rotina r = new Rotina().get();
        if (pessoa_id != -1) {
            Dao dao = new Dao();
            pessoaComplemento.setPessoa((Pessoa) dao.find(new Pessoa(), (int) pessoa_id));
            pessoaComplemento.setNrDiaVencimento(diaVencimento);
            
            if (pessoaComplemento.getPessoa().getEmail1().isEmpty()) {
                if(Integer.parseInt(listaStatusCobranca.get(indexStatusCobranca).getDescription()) == 2) {
                    GenericaMensagem.warn("Importante", "Status de cobrança tipo Email deve ter email cadastro!");
                }
            }
            
            if (pessoaComplemento.getPessoa().getEmail1().isEmpty()) {
                pessoaComplemento.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), 1));
            } else {
                pessoaComplemento.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), Integer.valueOf(listaStatusCobranca.get(indexStatusCobranca).getDescription())));
            }

            if (responsavel != null && responsavel.getId() != -1) {
                pessoaComplemento.setResponsavel(responsavel);
            } else {
                pessoaComplemento.setResponsavel(null);
            }

            dao.openTransaction();
            if (pessoaComplemento.getId() == -1) {
                if (dao.save(pessoaComplemento)) {
                    dao.commit();
                    if (showMessage) {
                        GenericaMensagem.info("Sucesso", "Pessoa Complemento salva!");
                    }
                } else {
                    dao.rollback();
                    GenericaMensagem.error("Atenção", "Erro ao salvar Pessoa Complemento!");
                }
            } else if (dao.update(pessoaComplemento)) {
                dao.commit();
                if (showMessage) {
                    GenericaMensagem.info("Sucesso", "Pessoa Complemento atualizada!");
                }
            } else {
                dao.rollback();
                GenericaMensagem.error("Atenção", "Erro ao Atualizar Pessoa Complemento!");
            }
        }
        if (r.getId() == 71) {
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setPessoaComplemento(pessoaComplemento);
            PF.update("form_pessoa_fisica:id_msg_aviso_block");
        }
        if (r.getId() == 82) {
            ((JuridicaBean) GenericaSessao.getObject("juridicaBean")).setPessoaComplemento(pessoaComplemento);
            PF.update("formPessoaJuridica:id_msg_aviso_block");
        }
        return null;
    }

    public final void loadListaStatusCobranca() {
        listaStatusCobranca.clear();

        List<StatusCobranca> result = new PessoaDao().listaStatusCobranca();

        for (int i = 0; i < result.size(); i++) {
            listaStatusCobranca.add(new SelectItem(i, result.get(i).getDescricao(), Integer.toString(result.get(i).getId())));
        }
    }

    public List<SelectItem> getListaDataVencimento() {
        if (listaDataVencimento.isEmpty()) {
            for (int i = 1; i <= 31; i++) {
                listaDataVencimento.add(new SelectItem(Integer.toString(i)));
            }
        }
        return listaDataVencimento;
    }

    public void setListaDataVencimento(List<SelectItem> listaDataVencimento) {
        this.listaDataVencimento = listaDataVencimento;
    }

    public PessoaComplemento getPessoaComplemento() {
        return pessoaComplemento;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
    }

    public String pessoaComplementoPesquisaPessoa(Integer idPessoa) {
        try {
            PessoaDao pessoaDB = new PessoaDao();
            ConfiguracaoSocial cs = ConfiguracaoSocial.get();
            pessoaComplemento = pessoaDB.pesquisaPessoaComplementoPorPessoa(idPessoa);

            if (pessoaComplemento.getId() == -1) {
                diaVencimento = getRegistro().getFinDiaVencimentoCobranca();

                if (pessoaComplemento.getPessoa().getEmail1().isEmpty()) {
                    pessoaComplemento.setStatusCobranca((StatusCobranca) new Dao().find(new StatusCobranca(), 1));
                } else {
                    pessoaComplemento.setStatusCobranca(cs.getStatusCobranca());
                }
            } else {
                diaVencimento = pessoaComplemento.getNrDiaVencimento();
            }

            for (int i = 0; i < listaStatusCobranca.size(); i++) {
                if (pessoaComplemento.getStatusCobranca().getId().equals(Integer.valueOf(listaStatusCobranca.get(i).getDescription()))) {
                    indexStatusCobranca = i;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(int diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public Registro getRegistro() {
        if (registro == null || registro.getId() == -1) {
            registro = (Registro) Registro.get();
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Pessoa getResponsavel() {
        if (GenericaSessao.exists("pessoaPesquisa")) {
            responsavel = (Pessoa) GenericaSessao.getObject("pessoaPesquisa");
            GenericaSessao.remove("pessoaPesquisa");
        }
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public List<SelectItem> getListaStatusCobranca() {
        return listaStatusCobranca;
    }

    public void setListaStatusCobranca(List<SelectItem> listaStatusCobranca) {
        this.listaStatusCobranca = listaStatusCobranca;
    }

    public Integer getIndexStatusCobranca() {
        return indexStatusCobranca;
    }

    public void setIndexStatusCobranca(Integer indexStatusCobranca) {
        this.indexStatusCobranca = indexStatusCobranca;
    }
}
